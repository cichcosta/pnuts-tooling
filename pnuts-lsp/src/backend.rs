use std::sync::Arc;
use tower_lsp::jsonrpc::Result;
use tower_lsp::lsp_types::*;
use tower_lsp::{Client, LanguageServer};
use tracing::{debug, info, warn};

use crate::document::{DocumentStore, SharedDocumentStore};

/// The LSP backend handling JSON-RPC protocol requests and notifications.
pub struct Backend {
    client: Client,
    documents: SharedDocumentStore,
}

impl Backend {
    /// Creates a new backend with a fresh in-memory document store.
    pub fn new(client: Client) -> Self {
        Self {
            client,
            documents: Arc::new(DocumentStore::new()),
        }
    }

    /// Creates a backend sharing an existing document store (useful for tests).
    pub fn with_store(client: Client, documents: SharedDocumentStore) -> Self {
        Self { client, documents }
    }

    /// Accessor for the shared in-memory document store.
    pub fn documents(&self) -> &SharedDocumentStore {
        &self.documents
    }

    /// Publishes syntax diagnostics for the given document URI.
    pub async fn update_diagnostics(&self, uri: &Url, version: Option<i32>) {
        if let Some(text) = self.documents.get_text(uri).await {
            let diags = self
                .documents
                .with_tree(uri, |tree| crate::diagnostics::collect_diagnostics(tree, &text))
                .await
                .unwrap_or_default();

            self.client
                .publish_diagnostics(uri.clone(), diags, version)
                .await;
        }
    }
}

#[tower_lsp::async_trait]
impl LanguageServer for Backend {
    async fn initialize(&self, params: InitializeParams) -> Result<InitializeResult> {
        info!("Initializing pnuts-lsp server...");
        if let Some(client_info) = params.client_info {
            info!(
                "Connected client: {} {}",
                client_info.name,
                client_info.version.unwrap_or_default()
            );
        }

        Ok(InitializeResult {
            capabilities: ServerCapabilities {
                text_document_sync: Some(TextDocumentSyncCapability::Kind(
                    TextDocumentSyncKind::FULL,
                )),
                document_formatting_provider: Some(OneOf::Left(true)),
                completion_provider: Some(CompletionOptions {
                    resolve_provider: Some(false),
                    trigger_characters: Some(vec![".".to_string(), ":".to_string()]),
                    all_commit_characters: None,
                    work_done_progress_options: Default::default(),
                    completion_item: None,
                }),
                ..ServerCapabilities::default()
            },
            server_info: Some(ServerInfo {
                name: "pnuts-lsp".to_string(),
                version: Some(env!("CARGO_PKG_VERSION").to_string()),
            }),
        })
    }

    async fn initialized(&self, _: InitializedParams) {
        info!("pnuts-lsp server initialized successfully.");
        self.client
            .log_message(MessageType::INFO, "pnuts-lsp server initialized!")
            .await;
    }

    async fn shutdown(&self) -> Result<()> {
        info!("Shutting down pnuts-lsp server...");
        Ok(())
    }

    async fn did_open(&self, params: DidOpenTextDocumentParams) {
        let doc = params.text_document;
        info!("didOpen: {} (v{})", doc.uri, doc.version);

        self.documents
            .open(doc.uri.clone(), doc.language_id, doc.version, doc.text)
            .await;
        self.update_diagnostics(&doc.uri, Some(doc.version)).await;
    }

    async fn did_change(&self, mut params: DidChangeTextDocumentParams) {
        let doc = params.text_document;
        debug!("didChange: {} (v{})", doc.uri, doc.version);

        if let Some(change) = params.content_changes.pop() {
            self.documents
                .update(&doc.uri, doc.version, change.text)
                .await;
            self.update_diagnostics(&doc.uri, Some(doc.version)).await;
        }
    }

    async fn did_save(&self, params: DidSaveTextDocumentParams) {
        let uri = params.text_document.uri;
        info!("didSave: {}", uri);

        if let Some(text) = params.text {
            let version = self.documents.get_version(&uri).await.unwrap_or(0);
            self.documents.update(&uri, version, text).await;
            self.update_diagnostics(&uri, Some(version)).await;
        } else {
            let version = self.documents.get_version(&uri).await;
            self.update_diagnostics(&uri, version).await;
        }
    }

    async fn did_close(&self, params: DidCloseTextDocumentParams) {
        let uri = params.text_document.uri;
        info!("didClose: {}", uri);

        self.documents.close(&uri).await;
        self.client.publish_diagnostics(uri, vec![], None).await;
    }

    async fn formatting(
        &self,
        params: DocumentFormattingParams,
    ) -> Result<Option<Vec<TextEdit>>> {
        let uri = params.text_document.uri;
        info!("textDocument/formatting: {}", uri);

        let Some(text) = self.documents.get_text(&uri).await else {
            warn!("Document not found for formatting: {}", uri);
            return Ok(None);
        };

        if text.trim().is_empty() {
            return Ok(Some(vec![]));
        }

        match crate::formatter::format_code(&text).await {
            Ok(formatted) => {
                if formatted == text {
                    debug!("Document is already formatted: {}", uri);
                    return Ok(Some(vec![]));
                }

                let full_range = compute_full_document_range(&text);
                Ok(Some(vec![TextEdit {
                    range: full_range,
                    new_text: formatted,
                }]))
            }
            Err(e) => {
                warn!("Formatting failed for {}: {}", uri, e);
                // Return Ok(None) to prevent editor from overwriting or losing code
                Ok(None)
            }
        }
    }

    async fn completion(
        &self,
        params: CompletionParams,
    ) -> Result<Option<CompletionResponse>> {
        let uri = params.text_document_position.text_document.uri;
        let position = params.text_document_position.position;
        debug!("textDocument/completion at {}: {:?}", uri, position);

        let Some(text) = self.documents.get_text(&uri).await else {
            return Ok(None);
        };

        let items = self
            .documents
            .with_tree(&uri, |tree| {
                crate::completion::provide_completions(Some(tree), &text, position)
            })
            .await
            .unwrap_or_else(|| {
                crate::completion::provide_completions(None, &text, position)
            });

        Ok(Some(CompletionResponse::Array(items)))
    }
}

/// Computes the 0-indexed LSP Range covering the entire document.
pub fn compute_full_document_range(text: &str) -> Range {
    let mut line_count = 0;
    let mut last_line_len = 0;

    for line in text.split('\n') {
        line_count += 1;
        let clean = line.strip_suffix('\r').unwrap_or(line);
        last_line_len = clean.encode_utf16().count();
    }

    let end_line = if line_count > 0 { line_count - 1 } else { 0 };

    Range {
        start: Position {
            line: 0,
            character: 0,
        },
        end: Position {
            line: end_line as u32,
            character: last_line_len as u32,
        },
    }
}
