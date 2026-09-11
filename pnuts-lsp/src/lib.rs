pub mod backend;
pub mod document;
pub mod formatter;
pub mod parser;

pub use backend::Backend;
pub use document::{Document, DocumentSnapshot, DocumentStore, SharedDocumentStore};
pub use formatter::{format_code, FormatterError};
pub use parser::{create_parser, has_errors, parse_pnuts};

#[cfg(test)]
mod tests {
    use super::*;
    use std::sync::Arc;
    use tower_lsp::lsp_types::*;
    use tower_lsp::LanguageServer;
    use tower_lsp::LspService;
    use url::Url;

    #[tokio::test]
    async fn test_lsp_lifecycle_initialize_and_shutdown() {
        let (service, _socket) = LspService::new(|client| Backend::new(client));
        let backend = service.inner();

        #[allow(deprecated)]
        let init_params = InitializeParams {
            process_id: Some(1),
            root_path: None,
            root_uri: Some(Url::parse("file:///workspace").unwrap()),
            initialization_options: None,
            capabilities: ClientCapabilities::default(),
            trace: None,
            workspace_folders: None,
            client_info: Some(ClientInfo {
                name: "test-editor".to_string(),
                version: Some("1.0.0".to_string()),
            }),
            locale: None,
        };

        let init_result = backend.initialize(init_params).await.unwrap();
        assert_eq!(
            init_result.capabilities.text_document_sync,
            Some(TextDocumentSyncCapability::Kind(TextDocumentSyncKind::FULL))
        );
        let server_info = init_result.server_info.expect("ServerInfo expected");
        assert_eq!(server_info.name, "pnuts-lsp");

        // 2. Initialized notification
        backend.initialized(InitializedParams {}).await;

        // 3. Shutdown
        let shutdown_result = backend.shutdown().await;
        assert!(shutdown_result.is_ok());
    }

    #[tokio::test]
    async fn test_lsp_document_synchronization_in_memory() {
        let store = Arc::new(DocumentStore::new());
        let (service, _socket) =
            LspService::new(|client| Backend::with_store(client, store.clone()));
        let backend = service.inner();

        // 1. Open untitled buffer
        let untitled_uri = Url::parse("untitled:Untitled-1").unwrap();
        let open_params = DidOpenTextDocumentParams {
            text_document: TextDocumentItem {
                uri: untitled_uri.clone(),
                language_id: "pnuts".to_string(),
                version: 1,
                text: "a = 1;\nb = 2;\nprintln(a + b);".to_string(),
            },
        };

        backend.did_open(open_params).await;

        assert_eq!(store.len().await, 1);
        let snapshot = store.get_snapshot(&untitled_uri).await.unwrap();
        assert_eq!(snapshot.version, 1);
        assert!(snapshot.has_tree);
        assert!(!snapshot.has_errors);

        // 2. Change untitled buffer
        let change_params = DidChangeTextDocumentParams {
            text_document: VersionedTextDocumentIdentifier {
                uri: untitled_uri.clone(),
                version: 2,
            },
            content_changes: vec![TextDocumentContentChangeEvent {
                range: None,
                range_length: None,
                text: "a = 100;\nb = 200;\nprintln(a + b);".to_string(),
            }],
        };

        backend.did_change(change_params).await;

        let snapshot2 = store.get_snapshot(&untitled_uri).await.unwrap();
        assert_eq!(snapshot2.version, 2);
        assert_eq!(snapshot2.text, "a = 100;\nb = 200;\nprintln(a + b);");

        // 3. Close buffer
        let close_params = DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier {
                uri: untitled_uri.clone(),
            },
        };

        backend.did_close(close_params).await;
        assert_eq!(store.len().await, 0);
        assert!(!store.contains(&untitled_uri).await);
    }
}
