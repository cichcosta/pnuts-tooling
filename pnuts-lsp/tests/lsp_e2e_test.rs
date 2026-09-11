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

#[tokio::test]
async fn test_lsp_formatting_lifecycle_and_idempotence() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

    // 1. Initialize handshake - verify formatting capability
    #[allow(deprecated)]
    let init_params = InitializeParams {
        process_id: Some(123),
        root_path: None,
        root_uri: Some(Url::parse("file:///workspace").unwrap()),
        initialization_options: None,
        capabilities: ClientCapabilities::default(),
        trace: None,
        workspace_folders: None,
        client_info: Some(ClientInfo {
            name: "VSCode".to_string(),
            version: Some("1.90.0".to_string()),
        }),
        locale: None,
    };

    let init_result = backend.initialize(init_params).await.expect("initialize failed");
    assert_eq!(
        init_result.capabilities.document_formatting_provider,
        Some(OneOf::Left(true))
    );

    backend.initialized(InitializedParams {}).await;

    // 2. Open an unformatted document
    let uri = Url::parse("file:///workspace/test.pnuts").unwrap();
    let unformatted = "a = 100 // esse e o valor\nif (a > 10) {\nprintln(a);\nprintln(b);\n\n}\n";

    backend
        .did_open(DidOpenTextDocumentParams {
            text_document: TextDocumentItem {
                uri: uri.clone(),
                language_id: "pnuts".to_string(),
                version: 1,
                text: unformatted.to_string(),
            },
        })
        .await;

    // 3. Request formatting
    let format_params = DocumentFormattingParams {
        text_document: TextDocumentIdentifier { uri: uri.clone() },
        options: FormattingOptions {
            tab_size: 2,
            insert_spaces: true,
            ..Default::default()
        },
        work_done_progress_params: Default::default(),
    };

    let edits = backend
        .formatting(format_params.clone())
        .await
        .expect("formatting request failed");

    let edits = edits.expect("Expected TextEdit list from formatting");
    assert_eq!(edits.len(), 1);
    let edit = &edits[0];

    // Verify formatting rules were applied
    assert!(edit.new_text.contains("// esse e o valor\na = 100"));
    assert!(edit.new_text.contains("if (a > 10) {\n\n"));
    assert!(!edit.new_text.contains("\n\n}"));

    // 4. Update document with the formatted text and request formatting again
    backend
        .did_change(DidChangeTextDocumentParams {
            text_document: VersionedTextDocumentIdentifier {
                uri: uri.clone(),
                version: 2,
            },
            content_changes: vec![TextDocumentContentChangeEvent {
                range: None,
                range_length: None,
                text: edit.new_text.clone(),
            }],
        })
        .await;

    // Second formatting request: idempotence check (should return empty edits)
    let second_edits = backend
        .formatting(format_params)
        .await
        .expect("second formatting request failed")
        .expect("Expected Some vec");

    assert!(
        second_edits.is_empty(),
        "Idempotent formatting should produce no edits on already formatted document"
    );

    // 5. Cleanup
    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier { uri },
        })
        .await;
    backend.shutdown().await.unwrap();
}

#[tokio::test]
async fn test_lsp_completion_lifecycle_and_scoped_suggestions() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

    // 1. Initialize and verify completion provider capability
    #[allow(deprecated)]
    let init_params = InitializeParams {
        process_id: Some(456),
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

    let init_result = backend.initialize(init_params).await.expect("initialize failed");
    assert!(init_result.capabilities.completion_provider.is_some());

    backend.initialized(InitializedParams {}).await;

    // 2. Open an untitled buffer with variables and a function
    let uri = Url::parse("untitled:script.pnuts").unwrap();
    let code = r#"
        globalCompany = "Ibyte";

        function calculateCommission(salesAmount, taxRate) {
            netAmount = salesAmount * (1.0 - taxRate);
            return netAmount;
        }

        total = 5000;
    "#;

    backend
        .did_open(DidOpenTextDocumentParams {
            text_document: TextDocumentItem {
                uri: uri.clone(),
                language_id: "pnuts".to_string(),
                version: 1,
                text: code.to_string(),
            },
        })
        .await;

    // 3. Request completions at cursor inside function (line 4)
    let completion_params_inside = CompletionParams {
        text_document_position: TextDocumentPositionParams {
            text_document: TextDocumentIdentifier { uri: uri.clone() },
            position: Position { line: 4, character: 12 },
        },
        work_done_progress_params: Default::default(),
        partial_result_params: Default::default(),
        context: None,
    };

    let resp = backend
        .completion(completion_params_inside)
        .await
        .expect("completion request failed")
        .expect("expected completion response");

    let items = match resp {
        CompletionResponse::Array(list) => list,
        CompletionResponse::List(cl) => cl.items,
    };

    let labels: Vec<&str> = items.iter().map(|i| i.label.as_str()).collect();

    // Local function parameters
    assert!(labels.contains(&"salesAmount"), "Expected salesAmount parameter");
    assert!(labels.contains(&"taxRate"), "Expected taxRate parameter");

    // Declared functions and outer variables
    assert!(labels.contains(&"calculateCommission"), "Expected calculateCommission function");
    assert!(labels.contains(&"globalCompany"), "Expected globalCompany variable");

    // Built-in functions, keywords and snippets
    assert!(labels.contains(&"println"), "Expected println builtin");
    assert!(labels.contains(&"foreach (in)"), "Expected foreach snippet");
    assert!(labels.contains(&"function"), "Expected function keyword");
    assert!(labels.contains(&"List"), "Expected List java class");

    // 4. Cleanup
    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier { uri },
        })
        .await;
    backend.shutdown().await.unwrap();
}

#[tokio::test]
async fn test_lsp_realtime_diagnostics_lifecycle() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

    #[allow(deprecated)]
    let init_params = InitializeParams {
        process_id: Some(789),
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

    backend.initialize(init_params).await.unwrap();
    backend.initialized(InitializedParams {}).await;

    let uri = Url::parse("file:///workspace/syntax_check.pnuts").unwrap();

    // 1. Open valid file - should have no error diagnostics
    let valid_code = "x = 10;\nprintln(x);\n";
    backend
        .did_open(DidOpenTextDocumentParams {
            text_document: TextDocumentItem {
                uri: uri.clone(),
                language_id: "pnuts".to_string(),
                version: 1,
                text: valid_code.to_string(),
            },
        })
        .await;

    let initial_has_errors = backend
        .documents()
        .get_snapshot(&uri)
        .await
        .unwrap()
        .has_errors;
    assert!(!initial_has_errors, "Initial valid code should not have errors");

    // 2. Change file to contain broken syntax
    let broken_code = "function ( broken syntax %%% ;";
    backend
        .did_change(DidChangeTextDocumentParams {
            text_document: VersionedTextDocumentIdentifier {
                uri: uri.clone(),
                version: 2,
            },
            content_changes: vec![TextDocumentContentChangeEvent {
                range: None,
                range_length: None,
                text: broken_code.to_string(),
            }],
        })
        .await;

    let broken_snap = backend.documents().get_snapshot(&uri).await.unwrap();
    assert!(broken_snap.has_errors, "Broken code must be flagged with has_errors");

    let diags = backend
        .documents()
        .with_tree(&uri, |tree| pnuts_lsp::collect_diagnostics(tree, broken_code))
        .await
        .unwrap();

    assert!(!diags.is_empty(), "Broken code must produce syntax diagnostics");
    assert_eq!(diags[0].severity, Some(DiagnosticSeverity::ERROR));
    assert_eq!(diags[0].source, Some("pnuts-lsp".to_string()));

    // 3. Fix the syntax error (didChange to valid code again)
    let fixed_code = "function fixed() { return 42; }";
    backend
        .did_change(DidChangeTextDocumentParams {
            text_document: VersionedTextDocumentIdentifier {
                uri: uri.clone(),
                version: 3,
            },
            content_changes: vec![TextDocumentContentChangeEvent {
                range: None,
                range_length: None,
                text: fixed_code.to_string(),
            }],
        })
        .await;

    let fixed_snap = backend.documents().get_snapshot(&uri).await.unwrap();
    assert!(!fixed_snap.has_errors, "Fixed code must not have errors");

    let fixed_diags = backend
        .documents()
        .with_tree(&uri, |tree| pnuts_lsp::collect_diagnostics(tree, fixed_code))
        .await
        .unwrap();

    assert!(fixed_diags.is_empty(), "Fixed code must have 0 diagnostics");

    // 4. Close document
    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier { uri },
        })
        .await;

    backend.shutdown().await.unwrap();
}
