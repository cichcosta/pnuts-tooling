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
async fn test_lsp_formatting_continue_statement_separation() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

    #[allow(deprecated)]
    let init_params = InitializeParams {
        process_id: Some(124),
        root_path: None,
        root_uri: Some(Url::parse("file:///workspace").unwrap()),
        initialization_options: None,
        capabilities: ClientCapabilities::default(),
        trace: None,
        workspace_folders: None,
        client_info: None,
        locale: None,
    };
    backend.initialize(init_params).await.unwrap();
    backend.initialized(InitializedParams {}).await;

    let uri = Url::parse("file:///workspace/continue_test.pnuts").unwrap();
    let unformatted = "while (true) {\n  continue\n  println(\"wddolst INICIO 2 >>> \" + wddolst.toString()); flush();\n}\n";

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

    let format_params = DocumentFormattingParams {
        text_document: TextDocumentIdentifier { uri: uri.clone() },
        options: FormattingOptions {
            tab_size: 2,
            insert_spaces: true,
            ..Default::default()
        },
        work_done_progress_params: Default::default(),
    };

    let edits = backend.formatting(format_params).await.unwrap();
    if let Some(edits) = edits {
        if !edits.is_empty() {
            let formatted = &edits[0].new_text;
            assert!(!formatted.contains("continueprintln"));
            assert!(formatted.contains("continue\n"));
            assert!(formatted.contains("println(\"wddolst INICIO 2 >>> \" + wddolst.toString()); flush();"));
        }
    }

    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier { uri },
        })
        .await;
    backend.shutdown().await.unwrap();
}

#[tokio::test]
async fn test_lsp_formatting_switch_case_statements() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

    #[allow(deprecated)]
    let init_params = InitializeParams {
        process_id: Some(125),
        root_path: None,
        root_uri: Some(Url::parse("file:///workspace").unwrap()),
        initialization_options: None,
        capabilities: ClientCapabilities::default(),
        trace: None,
        workspace_folders: None,
        client_info: None,
        locale: None,
    };
    backend.initialize(init_params).await.unwrap();
    backend.initialized(InitializedParams {}).await;

    let uri = Url::parse("file:///workspace/switch_test.pnuts").unwrap();
    let unformatted = "switch (wdataref) {\n  case \"2017\":\nwpICMSInterPart = 60.00;\nbreak;\n  case \"2018\":\nwpICMSInterPart = 80.00;\nbreak;\n  default:\nbreak;\n}\n";

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

    let format_params = DocumentFormattingParams {
        text_document: TextDocumentIdentifier { uri: uri.clone() },
        options: FormattingOptions {
            tab_size: 2,
            insert_spaces: true,
            ..Default::default()
        },
        work_done_progress_params: Default::default(),
    };

    let edits = backend.formatting(format_params).await.unwrap();
    if let Some(edits) = edits {
        if !edits.is_empty() {
            let formatted = &edits[0].new_text;
            assert!(formatted.contains("case \"2017\":"));
            assert!(formatted.contains("case \"2018\":"));
            assert!(formatted.contains("default:"));
            assert!(!formatted.contains("case \"2017\"\n"));
            assert!(!formatted.contains("case \"2017\" :"));
            assert!(!formatted.contains("case \"2017\"\r\n:"));
        }
    }

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
    assert!(labels.contains(&"getRows"), "Expected getRows domain builtin");
    assert!(labels.contains(&"getMRows"), "Expected getMRows domain builtin");
    assert!(labels.contains(&"getSQVNumber"), "Expected getSQVNumber domain builtin");
    assert!(labels.contains(&"showMessage"), "Expected showMessage domain builtin");
    assert!(labels.contains(&"formatNumber"), "Expected formatNumber domain builtin");
    assert!(labels.contains(&"foreach (in)"), "Expected foreach snippet");
    assert!(labels.contains(&"function"), "Expected function keyword");
    assert!(labels.contains(&"List"), "Expected List java class");
    assert!(labels.contains(&"BigDecimal"), "Expected BigDecimal java class");

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

#[tokio::test]
async fn test_lsp_formatting_import_and_package_statements() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

    let init_params = InitializeParams {
        process_id: Some(1),
        #[allow(deprecated)]
        root_path: None,
        root_uri: Some(Url::parse("file:///workspace").unwrap()),
        initialization_options: None,
        capabilities: ClientCapabilities::default(),
        trace: None,
        workspace_folders: Some(vec![WorkspaceFolder {
            uri: Url::parse("file:///workspace").unwrap(),
            name: "workspace".to_string(),
        }]),
        client_info: Some(ClientInfo {
            name: "test-client".to_string(),
            version: Some("1.0.0".to_string()),
        }),
        locale: None,
    };

    backend.initialize(init_params).await.expect("initialize failed");
    backend.initialized(InitializedParams {}).await;

    let uri = Url::parse("file:///workspace/imports.pnuts").unwrap();
    let unformatted = "import (\"java.io.*\");\npackage (\"com.ibyte.erp\");\nimport java.util.List;\n";

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
        .formatting(format_params)
        .await
        .expect("formatting request failed");

    if let Some(edits) = edits {
        if !edits.is_empty() {
            let formatted = &edits[0].new_text;
            assert!(
                formatted.contains("import(\"java.io.*\");"),
                "import(...) must not have space after keyword: got {}",
                formatted
            );
            assert!(
                formatted.contains("package(\"com.ibyte.erp\");"),
                "package(...) must not have space after keyword: got {}",
                formatted
            );
            assert!(
                formatted.contains("import java.util.List;"),
                "import java.util.List must retain space: got {}",
                formatted
            );
        }
    }

    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier { uri },
        })
        .await;

    backend.shutdown().await.unwrap();
}

#[tokio::test]
async fn test_lsp_navigation_lifecycle() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

    #[allow(deprecated)]
    let init_params = InitializeParams {
        process_id: Some(999),
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

    let init_res = backend.initialize(init_params).await.unwrap();
    assert_eq!(init_res.capabilities.definition_provider, Some(OneOf::Left(true)));
    assert_eq!(init_res.capabilities.hover_provider, Some(HoverProviderCapability::Simple(true)));
    assert_eq!(init_res.capabilities.document_symbol_provider, Some(OneOf::Left(true)));

    backend.initialized(InitializedParams {}).await;

    let uri = Url::parse("file:///workspace/app.pnuts").unwrap();
    let text = "function calcular(a, b) {\n  return a * b;\n}\n\nres = calcular(5, 10);\nlinhas = getRows(\"TAB\", \"select 1\");";

    backend
        .did_open(DidOpenTextDocumentParams {
            text_document: TextDocumentItem {
                uri: uri.clone(),
                language_id: "pnuts".to_string(),
                version: 1,
                text: text.to_string(),
            },
        })
        .await;

    // 1. Test Goto Definition: on 'calcular' at line 4, col 8
    #[allow(deprecated)]
    let def_params = GotoDefinitionParams {
        text_document_position_params: TextDocumentPositionParams {
            text_document: TextDocumentIdentifier { uri: uri.clone() },
            position: Position { line: 4, character: 8 },
        },
        work_done_progress_params: Default::default(),
        partial_result_params: Default::default(),
    };

    let def_res = backend.goto_definition(def_params).await.unwrap();
    assert!(def_res.is_some(), "Goto definition must return location");
    if let Some(GotoDefinitionResponse::Scalar(loc)) = def_res {
        assert_eq!(loc.range.start.line, 0);
    } else {
        panic!("Expected scalar goto definition response");
    }

    // 2. Test Hover: on 'getRows' at line 5, col 12
    #[allow(deprecated)]
    let hover_params = HoverParams {
        text_document_position_params: TextDocumentPositionParams {
            text_document: TextDocumentIdentifier { uri: uri.clone() },
            position: Position { line: 5, character: 12 },
        },
        work_done_progress_params: Default::default(),
    };

    let hover_res = backend.hover(hover_params).await.unwrap();
    assert!(hover_res.is_some(), "Hover must return documentation for getRows");
    if let Some(h) = hover_res {
        if let HoverContents::Markup(m) = h.contents {
            assert!(m.value.contains("getRows"));
        } else {
            panic!("Expected markup hover content");
        }
    }

    // 3. Test Document Symbols
    #[allow(deprecated)]
    let sym_params = DocumentSymbolParams {
        text_document: TextDocumentIdentifier { uri: uri.clone() },
        work_done_progress_params: Default::default(),
        partial_result_params: Default::default(),
    };

    let sym_res = backend.document_symbol(sym_params).await.unwrap();
    assert!(sym_res.is_some(), "Document symbols must be returned");
    if let Some(DocumentSymbolResponse::Nested(syms)) = sym_res {
        assert!(syms.iter().any(|s| s.name == "calcular" && s.kind == SymbolKind::FUNCTION));
    } else {
        panic!("Expected nested document symbol response");
    }

    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier { uri },
        })
        .await;

    backend.shutdown().await.unwrap();
}

#[tokio::test]
async fn test_lsp_groovy_document_lifecycle_and_completions() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

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
            name: "VSCode".to_string(),
            version: Some("1.90.0".to_string()),
        }),
        locale: None,
    };

    backend.initialize(init_params).await.expect("initialize failed");
    backend.initialized(InitializedParams {}).await;

    let uri = Url::parse("file:///workspace/sample.groovy").unwrap();
    let code = r#"
        import java.util.ArrayList

        function processOrders(orders) {
            def totalAmount = 0
            for (order in orders) {
                totalAmount += order.value
            }
            return totalAmount
        }
    "#;

    backend
        .did_open(DidOpenTextDocumentParams {
            text_document: TextDocumentItem {
                uri: uri.clone(),
                language_id: "groovy".to_string(),
                version: 1,
                text: code.to_string(),
            },
        })
        .await;

    // 1. Completion inside processOrders
    let comp_params = CompletionParams {
        text_document_position: TextDocumentPositionParams {
            text_document: TextDocumentIdentifier { uri: uri.clone() },
            position: Position { line: 6, character: 16 },
        },
        work_done_progress_params: Default::default(),
        partial_result_params: Default::default(),
        context: None,
    };

    let comp_res = backend.completion(comp_params).await.unwrap();
    assert!(comp_res.is_some(), "Completion for groovy document must return items");
    if let Some(CompletionResponse::Array(items)) = comp_res {
        let labels: Vec<String> = items.into_iter().map(|i| i.label).collect();
        assert!(labels.contains(&"totalAmount".to_string()), "Scope completion must suggest totalAmount");
        assert!(labels.contains(&"orders".to_string()), "Parameter completion must suggest orders");
        assert!(labels.contains(&"processOrders".to_string()), "Function completion must suggest processOrders");
    } else {
        panic!("Expected CompletionResponse::Array");
    }

    // 2. Document symbols
    #[allow(deprecated)]
    let sym_params = DocumentSymbolParams {
        text_document: TextDocumentIdentifier { uri: uri.clone() },
        work_done_progress_params: Default::default(),
        partial_result_params: Default::default(),
    };

    let sym_res = backend.document_symbol(sym_params).await.unwrap();
    assert!(sym_res.is_some());
    if let Some(DocumentSymbolResponse::Nested(syms)) = sym_res {
        assert!(syms.iter().any(|s| s.name == "processOrders"));
    }

    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier { uri },
        })
        .await;

    backend.shutdown().await.unwrap();
}

#[tokio::test]
async fn test_lsp_rename_and_prepare_rename_lifecycle() {
    let (service, _socket) = LspService::new(Backend::new);
    let backend = service.inner();

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
            name: "VSCode".to_string(),
            version: Some("1.90.0".to_string()),
        }),
        locale: None,
    };

    let init_res = backend.initialize(init_params).await.unwrap();
    assert!(
        init_res.capabilities.rename_provider.is_some(),
        "ServerCapabilities must expose rename_provider"
    );
    backend.initialized(InitializedParams {}).await;

    let uri = Url::parse("file:///workspace/rename_test.pnuts").unwrap();
    let code = r#"
        function formatName(firstName, lastName) {
            fullName = firstName + " " + lastName;
            return fullName;
        }
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

    // 1. prepare_rename on parameter `firstName` (line 1, col 29)
    let prep_params = TextDocumentPositionParams {
        text_document: TextDocumentIdentifier { uri: uri.clone() },
        position: Position { line: 1, character: 30 },
    };

    let prep_res = backend.prepare_rename(prep_params).await.unwrap();
    assert!(prep_res.is_some(), "prepare_rename must succeed on firstName");
    if let Some(PrepareRenameResponse::RangeWithPlaceholder { placeholder, .. }) = prep_res {
        assert_eq!(placeholder, "firstName");
    } else {
        panic!("Expected RangeWithPlaceholder");
    }

    // 2. rename `firstName` to `givenName`
    #[allow(deprecated)]
    let rename_params = RenameParams {
        text_document_position: TextDocumentPositionParams {
            text_document: TextDocumentIdentifier { uri: uri.clone() },
            position: Position { line: 1, character: 30 },
        },
        new_name: "givenName".to_string(),
        work_done_progress_params: Default::default(),
    };

    let rename_res = backend.rename(rename_params).await.unwrap();
    assert!(rename_res.is_some(), "rename must return a WorkspaceEdit");
    let ws_edit = rename_res.unwrap();
    let changes = ws_edit.changes.expect("WorkspaceEdit must have changes");
    let file_edits = changes.get(&uri).expect("Changes must contain document URI");
    assert_eq!(file_edits.len(), 2, "Must rename parameter and its usage");
    for edit in file_edits {
        assert_eq!(edit.new_text, "givenName");
    }

    // 3. prepare_rename on keyword `return` (line 3, col 14) -> must return None
    let keyword_prep = backend
        .prepare_rename(TextDocumentPositionParams {
            text_document: TextDocumentIdentifier { uri: uri.clone() },
            position: Position { line: 3, character: 14 },
        })
        .await
        .unwrap();
    assert!(keyword_prep.is_none(), "prepare_rename on keyword must return None");

    // 4. rename with invalid identifier -> must return None
    #[allow(deprecated)]
    let invalid_rename = backend
        .rename(RenameParams {
            text_document_position: TextDocumentPositionParams {
                text_document: TextDocumentIdentifier { uri: uri.clone() },
                position: Position { line: 1, character: 30 },
            },
            new_name: "123invalid".to_string(),
            work_done_progress_params: Default::default(),
        })
        .await
        .unwrap();
    assert!(invalid_rename.is_none(), "rename with invalid name must return None");

    backend
        .did_close(DidCloseTextDocumentParams {
            text_document: TextDocumentIdentifier { uri },
        })
        .await;

    backend.shutdown().await.unwrap();
}
