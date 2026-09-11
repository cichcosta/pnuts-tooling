use std::sync::Arc;
use tower_lsp::jsonrpc::Result;
use tower_lsp::lsp_types::*;
use tower_lsp::{Client, LanguageServer};
use tracing::{debug, info};

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
    }

    async fn did_change(&self, mut params: DidChangeTextDocumentParams) {
        let doc = params.text_document;
        debug!("didChange: {} (v{})", doc.uri, doc.version);

        if let Some(change) = params.content_changes.pop() {
            self.documents
                .update(&doc.uri, doc.version, change.text)
                .await;
        }
    }

    async fn did_save(&self, params: DidSaveTextDocumentParams) {
        let uri = params.text_document.uri;
        info!("didSave: {}", uri);

        if let Some(text) = params.text {
            let version = self.documents.get_version(&uri).await.unwrap_or(0);
            self.documents.update(&uri, version, text).await;
        }
    }

    async fn did_close(&self, params: DidCloseTextDocumentParams) {
        let uri = params.text_document.uri;
        info!("didClose: {}", uri);

        self.documents.close(&uri).await;
    }
}
