use pnuts_lsp::Backend;
use tower_lsp::lsp_types::*;
use tower_lsp::{LanguageServer, LspService};
use url::Url;

#[tokio::test]
async fn test_full_jsonrpc_lifecycle_with_untitled_document() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

    // 1. Initialize handshake
    let init_params = InitializeParams {
        process_id: Some(42),
        #[allow(deprecated)]
        root_path: None,
        root_uri: Some(Url::parse("file:///workspace").unwrap()),
        initialization_options: None,
        capabilities: ClientCapabilities::default(),
        trace: None,
        workspace_folders: None,
        client_info: Some(ClientInfo {
            name: "Zed".to_string(),
            version: Some("0.180.0".to_string()),
        }),
        locale: None,
    };

    let result = backend.initialize(init_params).await.expect("initialize failed");
    assert_eq!(
        result.capabilities.text_document_sync,
        Some(TextDocumentSyncCapability::Kind(TextDocumentSyncKind::FULL))
    );
    assert_eq!(
        result.server_info.map(|s| s.name),
        Some("pnuts-lsp".to_string())
    );

    // 2. Initialized notification
    backend.initialized(InitializedParams {}).await;

    // 3. Open an untitled buffer (common in Zed/VSCode for new unsaved files)
    let untitled_uri = Url::parse("untitled:Untitled-1").unwrap();
    let initial_code = r#"
        function calculateTotal(items) {
            total = 0;
            foreach item(items) {
                total += item.price;
            }
            return total;
        }
    "#;

    backend
        .did_open(DidOpenTextDocumentParams {
            text_document: TextDocumentItem {
                uri: untitled_uri.clone(),
                language_id: "pnuts".to_string(),
                version: 1,
                text: initial_code.to_string(),
            },
        })
        .await;

    // Check that document exists in memory and parsed successfully
    let store = backend.documents();
    assert_eq!(store.len().await, 1);
    assert!(store.contains(&untitled_uri).await);

    let snap = store.get_snapshot(&untitled_uri).await.unwrap();
    assert_eq!(snap.version, 1);
    assert!(snap.has_tree);
    assert!(!snap.has_errors);

    // 4. Update the buffer (didChange)
    let updated_code = r#"
        function calculateTotal(items) {
            total = 0;
            for (item in items) {
                total += item.price;
            }
            return total;
        }
    "#;

    backend
        .did_change(DidChangeTextDocumentParams {
            text_document: VersionedTextDocumentIdentifier {
                uri: untitled_uri.clone(),
                version: 2,
            },
            content_changes: vec![TextDocumentContentChangeEvent {
                range: None,
                range_length: None,
                text: updated_code.to_string(),
            }],
        })
        .await;

    let snap_updated = store.get_snapshot(&untitled_uri).await.unwrap();
    assert_eq!(snap_updated.version, 2);
    assert_eq!(snap_updated.text, updated_code);
    assert!(snap_updated.has_tree);
    assert!(!snap_updated.has_errors);

    // 5. Close the buffer (didClose)
    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier {
                uri: untitled_uri.clone(),
            },
        })
        .await;

    assert_eq!(store.len().await, 0);
    assert!(!store.contains(&untitled_uri).await);

    // 6. Shutdown
    let shutdown_result = backend.shutdown().await;
    assert!(shutdown_result.is_ok());
}
