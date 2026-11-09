use tower_lsp::lsp_types::*;
use tree_sitter::{Node, Tree};
use url::Url;

use crate::completion::{BUILTIN_FUNCTIONS, COMMON_JAVA_CLASSES, COMMON_METHODS};

/// Converts a tree-sitter node to an LSP Range using line-based UTF-16 character offsets.
pub fn node_to_range(node: Node, lines: &[&str]) -> Range {
    let start_point = node.start_position();
    let end_point = node.end_position();

    let start_line_str = lines.get(start_point.row).copied().unwrap_or("");
    let end_line_str = lines.get(end_point.row).copied().unwrap_or("");

    let start_char = byte_col_to_utf16(start_line_str, start_point.column);
    let end_char = byte_col_to_utf16(end_line_str, end_point.column);

    Range {
        start: Position {
            line: start_point.row as u32,
            character: start_char,
        },
        end: Position {
            line: end_point.row as u32,
            character: end_char,
        },
    }
}

/// Converts a byte column in a line to a UTF-16 code unit offset.
fn byte_col_to_utf16(line: &str, byte_col: usize) -> u32 {
    let bounded_col = byte_col.min(line.len());
    let valid_slice = match std::str::from_utf8(&line.as_bytes()[..bounded_col]) {
        Ok(s) => s,
        Err(e) => {
            let valid_len = e.valid_up_to();
            &line[..valid_len]
        }
    };
    valid_slice.encode_utf16().count() as u32
}

/// Converts an LSP Position (line, utf16 character) to a byte offset in the source.
pub fn position_to_byte_offset(source: &str, position: Position) -> Option<usize> {
    let mut current_line = 0u32;
    let mut current_byte = 0usize;

    for line in source.split('\n') {
        let line_without_cr = line.strip_suffix('\r').unwrap_or(line);
        if current_line == position.line {
            let mut utf16_count = 0u32;
            for (byte_idx, ch) in line_without_cr.char_indices() {
                if utf16_count >= position.character {
                    return Some(current_byte + byte_idx);
                }
                utf16_count += ch.len_utf16() as u32;
            }
            return Some(current_byte + line_without_cr.len());
        }
        current_byte += line.len() + 1; // +1 for '\n'
        current_line += 1;
    }

    None
}

/// Locates the definition of the symbol at the given position.
pub fn find_definition(
    tree: &Tree,
    source: &str,
    position: Position,
    uri: &Url,
) -> Option<GotoDefinitionResponse> {
    let offset = position_to_byte_offset(source, position)?;
    let root = tree.root_node();
    let raw_node = root.descendant_for_byte_range(offset, offset)?;

    let node = match raw_node.kind() {
        "identifier" | "property_identifier" | "type_identifier" => raw_node,
        _ if offset > 0 => {
            if let Some(prev) = root.descendant_for_byte_range(offset - 1, offset - 1) {
                match prev.kind() {
                    "identifier" | "property_identifier" | "type_identifier" => prev,
                    _ => raw_node,
                }
            } else {
                raw_node
            }
        }
        _ => raw_node,
    };

    // Get symbol name
    let symbol_name = match node.kind() {
        "identifier" | "property_identifier" | "type_identifier" => {
            node.utf8_text(source.as_bytes()).ok()?
        }
        _ => {
            if let Some(parent) = node.parent() {
                if parent.kind() == "identifier" || parent.kind() == "property_identifier" {
                    parent.utf8_text(source.as_bytes()).ok()?
                } else {
                    return None;
                }
            } else {
                return None;
            }
        }
    };

    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();

    // 1. Search for function/method definition with matching name
    if let Some(target) = find_function_or_method_def(root, source, symbol_name) {
        let range = node_to_range(target, &lines);
        return Some(GotoDefinitionResponse::Scalar(Location::new(
            uri.clone(),
            range,
        )));
    }

    // 2. Search for parameter in current function scope
    let mut curr = Some(node);
    while let Some(n) = curr {
        if n.kind() == "function_definition" || n.kind() == "method_definition" {
            if let Some(param_node) = find_parameter_in_func(n, source, symbol_name) {
                let range = node_to_range(param_node, &lines);
                return Some(GotoDefinitionResponse::Scalar(Location::new(
                    uri.clone(),
                    range,
                )));
            }
        }
        curr = n.parent();
    }

    // 3. Search for assignment / declaration of variable
    if let Some(assign_node) = find_assignment_def(root, source, symbol_name, node.start_byte()) {
        let range = node_to_range(assign_node, &lines);
        return Some(GotoDefinitionResponse::Scalar(Location::new(
            uri.clone(),
            range,
        )));
    }

    None
}

/// Provides documentation hover information for the symbol at the given position.
pub fn find_hover(tree: &Tree, source: &str, position: Position) -> Option<Hover> {
    let offset = position_to_byte_offset(source, position)?;
    let root = tree.root_node();
    let raw_node = root.descendant_for_byte_range(offset, offset)?;

    let node = match raw_node.kind() {
        "identifier" | "property_identifier" => raw_node,
        _ if offset > 0 => {
            if let Some(prev) = root.descendant_for_byte_range(offset - 1, offset - 1) {
                match prev.kind() {
                    "identifier" | "property_identifier" => prev,
                    _ => raw_node,
                }
            } else {
                raw_node
            }
        }
        _ => raw_node,
    };

    let symbol_name = match node.kind() {
        "identifier" | "property_identifier" => node.utf8_text(source.as_bytes()).ok()?,
        _ => {
            if let Some(p) = node.parent() {
                if p.kind() == "identifier" || p.kind() == "property_identifier" {
                    p.utf8_text(source.as_bytes()).ok()?
                } else {
                    return None;
                }
            } else {
                return None;
            }
        }
    };

    // 1. Check Builtin / Domain Functions
    for &(name, sig, _snippet, doc) in BUILTIN_FUNCTIONS {
        if name == symbol_name {
            let markdown = format!("```pnuts\n{}{}\n```\n\n{}", name, sig, doc);
            return Some(Hover {
                contents: HoverContents::Markup(MarkupContent {
                    kind: MarkupKind::Markdown,
                    value: markdown,
                }),
                range: None,
            });
        }
    }

    // 2. Check locally defined function in the AST
    if let Some(func_node) = find_function_or_method_def(root, source, symbol_name) {
        let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
        let start_row = func_node.start_position().row;
        let sig_line = lines.get(start_row).copied().unwrap_or("").trim();
        let markdown = format!("```pnuts\n{}\n```\n\n*(local definition)*", sig_line);
        return Some(Hover {
            contents: HoverContents::Markup(MarkupContent {
                kind: MarkupKind::Markdown,
                value: markdown,
            }),
            range: None,
        });
    }

    // 3. Check parameter in current scope
    let mut curr = Some(node);
    while let Some(n) = curr {
        if n.kind() == "function_definition" || n.kind() == "method_definition" {
            if let Some(_param_node) = find_parameter_in_func(n, source, symbol_name) {
                let markdown = format!("```pnuts\n(parameter) {}\n```", symbol_name);
                return Some(Hover {
                    contents: HoverContents::Markup(MarkupContent {
                        kind: MarkupKind::Markdown,
                        value: markdown,
                    }),
                    range: None,
                });
            }
        }
        curr = n.parent();
    }

    // 4. Check Enterprise Classes with rich documentation and code examples
    if let Some(hover_text) = get_enterprise_class_hover(symbol_name) {
        return Some(Hover {
            contents: HoverContents::Markup(MarkupContent {
                kind: MarkupKind::Markdown,
                value: hover_text.to_string(),
            }),
            range: None,
        });
    }

    // 5. Check Common Java Framework Classes
    for &(name, desc) in COMMON_JAVA_CLASSES {
        if name == symbol_name {
            let markdown = format!("```pnuts\nclass {}\n```\n\n{}", name, desc);
            return Some(Hover {
                contents: HoverContents::Markup(MarkupContent {
                    kind: MarkupKind::Markdown,
                    value: markdown,
                }),
                range: None,
            });
        }
    }

    // 6. Check Common Methods
    for &(name, sig, _snippet, doc) in COMMON_METHODS {
        if name == symbol_name {
            let markdown = format!("```pnuts\n{}{}\n```\n\n{}", name, sig, doc);
            return Some(Hover {
                contents: HoverContents::Markup(MarkupContent {
                    kind: MarkupKind::Markdown,
                    value: markdown,
                }),
                range: None,
            });
        }
    }

    // 7. Check local variable assignment (either at declaration or at usage)
    let var_assign = if let Some(parent) = node.parent() {
        if parent.kind() == "assignment_expression" && parent.child(0) == Some(node) {
            Some(node)
        } else {
            find_assignment_def(root, source, symbol_name, node.start_byte())
        }
    } else {
        find_assignment_def(root, source, symbol_name, node.start_byte())
    };

    if let Some(assign_node) = var_assign {
        let parent = assign_node.parent();
        let decl_text = if let Some(p) = parent {
            if p.kind() == "assignment_expression" {
                p.utf8_text(source.as_bytes()).unwrap_or(symbol_name).trim().to_string()
            } else {
                format!("var {}", symbol_name)
            }
        } else {
            format!("var {}", symbol_name)
        };
        let markdown = format!("```pnuts\n(variable) {}\n```\n\n*(local variable)*", decl_text);
        return Some(Hover {
            contents: HoverContents::Markup(MarkupContent {
                kind: MarkupKind::Markdown,
                value: markdown,
            }),
            range: None,
        });
    }

    None
}

/// Returns rich markdown documentation and usage examples for enterprise classes.
fn get_enterprise_class_hover(symbol: &str) -> Option<&'static str> {
    match symbol {
        "MConnection" => Some(
            "```pnuts\nclass MConnection\n```\n\n**MConnection**: Gerenciador de conexão e controle transacional com o banco de dados.\n\n### Métodos Frequentes:\n- `MConnection::getMCursor()`: Retorna o cursor de banco ativo para execução de comandos e transações.\n\n### Exemplo de Uso:\n```pnuts\ncursor = MConnection::getMCursor();\ncursor.beginWork();\ntry {\n    cursor.executeSQL(\"UPDATE CLIENTES SET ATIVO = 'S' WHERE ID = 1\");\n    cursor.commitWork();\n} catch (Exception e) {\n    cursor.rollbackWork();\n    showError(e.getMessage());\n}\n```"
        ),
        "MLogin" => Some(
            "```pnuts\nclass MLogin\n```\n\n**MLogin**: Provedor de contexto de autenticação e sessão do usuário.\n\n### Métodos Frequentes:\n- `MLogin::getUser()`: Retorna o nome do usuário autenticado na sessão.\n- `MLogin::getUserNodo()`: Retorna a identificação da estação de trabalho ou nodo de rede.\n\n### Exemplo de Uso:\n```pnuts\nusuario = MLogin::getUser();\nprintln(\"Executado por: \" + usuario);\n```"
        ),
        "MForm" => Some(
            "```pnuts\nclass MForm\n```\n\n**MForm**: Utilitário de interface para abertura e controle de telas e caixas de diálogo.\n\n### Métodos Frequentes:\n- `MForm::getDialogForm(parent, name, mode, title, size)`: Abre diálogo modal ou não-modal.\n- `MForm::getDialogFormEx(parent, name, mode, title, size, [options])`: Abre diálogo com opções estendidas.\n\n### Exemplo de Uso:\n```pnuts\nwdfform = MForm::getDialogForm(m, \"DDOC0CFPSR\", \"ME\", \"Informações Fiscais\", 3);\n```"
        ),
        "MCursor" => Some(
            "```pnuts\nclass MCursor\n```\n\n**MCursor**: Cursor de execução SQL e controle de transação.\n\n### Métodos Frequentes:\n- `cursor.beginWork()`: Inicia uma transação no banco de dados.\n- `cursor.commitWork()`: Confirma a transação ativa.\n- `cursor.rollbackWork()`: Desfaz a transação ativa em caso de erro.\n- `cursor.close()`: Libera o cursor e recursos associados.\n\n### Exemplo de Uso:\n```pnuts\ncursor.beginWork();\ntry {\n    executeSQL(\"INSERT INTO HISTORICO (MSG) VALUES ('OK')\");\n    cursor.commitWork();\n} catch (Exception e) {\n    cursor.rollbackWork();\n}\n```"
        ),
        "MRow" | "MRows" => Some(
            "```pnuts\nclass MRow\n```\n\n**MRow**: Representação em memória de um registro de banco de dados com colunas nomeadas.\n\n### Métodos Frequentes:\n- `row.getString(col)`: Retorna o valor da coluna como texto.\n- `row.getInt(col)`: Retorna o valor como inteiro.\n- `row.getBigDecimal(col)`: Retorna o valor monetário ou decimal exato.\n- `row.set(col, valor)`: Atribui novo valor para a coluna no buffer.\n\n### Exemplo de Uso:\n```pnuts\nwrow = getMRow(\"CLIENTES\");\nwrow.set(\"NOME\", \"EMPRESA EXEMPLO\");\ninsertRow(wrow);\n```"
        ),
        "MDate" => Some(
            "```pnuts\nclass MDate\n```\n\n**MDate**: Utilitário para formatação, cálculo e conversão de datas e timestamps."
        ),
        "MFile" => Some(
            "```pnuts\nclass MFile\n```\n\n**MFile**: Utilitário para manipulação de arquivos, diretórios e fluxos no sistema de arquivos."
        ),
        "JSONObject" => Some(
            "```pnuts\nclass JSONObject\n```\n\n**JSONObject**: Objeto estruturado JSON (`org.json.JSONObject`).\n\n### Exemplo de Uso:\n```pnuts\njson = new JSONObject();\njson.put(\"status\", \"sucesso\");\njson.put(\"codigo\", 200);\n```"
        ),
        "JSONArray" => Some(
            "```pnuts\nclass JSONArray\n```\n\n**JSONArray**: Coleção de elementos em formato de array JSON (`org.json.JSONArray`).\n\n### Exemplo de Uso:\n```pnuts\narray = new JSONArray();\narray.put(jsonItem);\n```"
        ),
        _ => None,
    }
}

/// Collects document symbols (outline of functions, methods, classes).
pub fn find_document_symbols(tree: &Tree, source: &str) -> Vec<DocumentSymbol> {
    let mut symbols = Vec::new();
    let root = tree.root_node();
    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();

    collect_symbols_recursive(root, source, &lines, &mut symbols);
    symbols
}

pub fn get_name_identifier<'a>(node: Node<'a>) -> Option<Node<'a>> {
    for i in 0..node.child_count() {
        if let Some(child) = node.child(i) {
            if child.kind() == "identifier" || child.kind() == "property_identifier" {
                return Some(child);
            } else if child.kind() == "package_path" {
                for j in (0..child.child_count()).rev() {
                    if let Some(c) = child.child(j) {
                        if c.kind() == "identifier" {
                            return Some(c);
                        }
                    }
                }
            }
        }
    }
    None
}

fn collect_symbols_recursive(
    node: Node,
    source: &str,
    lines: &[&str],
    out: &mut Vec<DocumentSymbol>,
) {
    match node.kind() {
        "function_definition" => {
            if let Some(name_node) = get_name_identifier(node) {
                if let Ok(name) = name_node.utf8_text(source.as_bytes()) {
                    let range = node_to_range(node, lines);
                    let selection_range = node_to_range(name_node, lines);

                    #[allow(deprecated)]
                    out.push(DocumentSymbol {
                        name: name.to_string(),
                        detail: Some("function".to_string()),
                        kind: SymbolKind::FUNCTION,
                        tags: None,
                        deprecated: None,
                        range,
                        selection_range,
                        children: None,
                    });
                }
            }
        }
        "class_definition" => {
            if let Some(name_node) = get_name_identifier(node) {
                if let Ok(name) = name_node.utf8_text(source.as_bytes()) {
                    let range = node_to_range(node, lines);
                    let selection_range = node_to_range(name_node, lines);

                    let mut children = Vec::new();
                    for i in 0..node.child_count() {
                        if let Some(child) = node.child(i) {
                            collect_symbols_recursive(child, source, lines, &mut children);
                        }
                    }

                    #[allow(deprecated)]
                    out.push(DocumentSymbol {
                        name: name.to_string(),
                        detail: Some("class".to_string()),
                        kind: SymbolKind::CLASS,
                        tags: None,
                        deprecated: None,
                        range,
                        selection_range,
                        children: if children.is_empty() { None } else { Some(children) },
                    });
                    return;
                }
            }
        }
        "method_definition" => {
            if let Some(name_node) = get_name_identifier(node) {
                if let Ok(name) = name_node.utf8_text(source.as_bytes()) {
                    let range = node_to_range(node, lines);
                    let selection_range = node_to_range(name_node, lines);

                    #[allow(deprecated)]
                    out.push(DocumentSymbol {
                        name: name.to_string(),
                        detail: Some("method".to_string()),
                        kind: SymbolKind::METHOD,
                        tags: None,
                        deprecated: None,
                        range,
                        selection_range,
                        children: None,
                    });
                }
            }
        }
        _ => {
            for i in 0..node.child_count() {
                if let Some(child) = node.child(i) {
                    collect_symbols_recursive(child, source, lines, out);
                }
            }
        }
    }
}

pub fn find_function_or_method_def<'a>(
    node: Node<'a>,
    source: &str,
    target_name: &str,
) -> Option<Node<'a>> {
    if node.kind() == "function_definition" || node.kind() == "method_definition" {
        if let Some(name_node) = get_name_identifier(node) {
            if let Ok(text) = name_node.utf8_text(source.as_bytes()) {
                if text == target_name {
                    return Some(name_node);
                }
            }
        }
    }

    for i in 0..node.child_count() {
        if let Some(child) = node.child(i) {
            if let Some(found) = find_function_or_method_def(child, source, target_name) {
                return Some(found);
            }
        }
    }
    None
}

pub fn find_parameter_in_func<'a>(
    func_node: Node<'a>,
    source: &str,
    target_name: &str,
) -> Option<Node<'a>> {
    for i in 0..func_node.child_count() {
        if let Some(child) = func_node.child(i) {
            if child.kind() == "parameter_list" {
                for p_idx in 0..child.child_count() {
                    if let Some(param) = child.child(p_idx) {
                        if param.kind() == "parameter" {
                            for id_idx in 0..param.child_count() {
                                if let Some(id_node) = param.child(id_idx) {
                                    if id_node.kind() == "identifier" {
                                        if let Ok(text) = id_node.utf8_text(source.as_bytes()) {
                                            if text == target_name {
                                                return Some(id_node);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    None
}

pub fn find_assignment_def<'a>(
    root: Node<'a>,
    source: &str,
    target_name: &str,
    before_byte: usize,
) -> Option<Node<'a>> {
    let mut best_match = None;
    search_assignments(root, source, target_name, before_byte, &mut best_match);
    best_match
}

fn search_assignments<'a>(
    node: Node<'a>,
    source: &str,
    target_name: &str,
    before_byte: usize,
    best_match: &mut Option<Node<'a>>,
) {
    if node.start_byte() >= before_byte {
        return;
    }

    if node.kind() == "assignment_expression" {
        if let Some(left) = node.child(0) {
            if left.kind() == "identifier" {
                if let Ok(text) = left.utf8_text(source.as_bytes()) {
                    if text == target_name {
                        *best_match = Some(left);
                    }
                }
            }
        }
    }

    for i in 0..node.child_count() {
        if let Some(child) = node.child(i) {
            search_assignments(child, source, target_name, before_byte, best_match);
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser::parse_pnuts;

    #[test]
    fn test_goto_definition_function() {
        let source = "function soma(a, b) {\n  return a + b;\n}\n\nresultado = soma(10, 20);";
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///workspace/test.pnuts").unwrap();

        // Position on 'soma' in 'soma(10, 20)' at line 4, char 13
        let position = Position {
            line: 4,
            character: 13,
        };
        let def = find_definition(&tree, source, position, &uri).expect("Definition should be found");

        if let GotoDefinitionResponse::Scalar(location) = def {
            assert_eq!(location.uri, uri);
            // Definition is at line 0
            assert_eq!(location.range.start.line, 0);
        } else {
            panic!("Expected Scalar GotoDefinitionResponse");
        }
    }

    #[test]
    fn test_goto_definition_parameter() {
        let source = "function test(paramA, paramB) {\n  return paramA * 2;\n}";
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///workspace/test.pnuts").unwrap();

        // Position on 'paramA' inside body (line 1, char 10)
        let position = Position {
            line: 1,
            character: 10,
        };
        let def = find_definition(&tree, source, position, &uri).expect("Parameter definition should be found");

        if let GotoDefinitionResponse::Scalar(location) = def {
            assert_eq!(location.range.start.line, 0);
        } else {
            panic!("Expected Scalar GotoDefinitionResponse");
        }
    }

    #[test]
    fn test_hover_builtin_domain_functions() {
        let source = "rows = getRows(\"CLIENTES\", \"select * from clientes\");";
        let tree = parse_pnuts(source, None).unwrap();

        let position = Position {
            line: 0,
            character: 9, // On 'getRows'
        };

        let hover = find_hover(&tree, source, position).expect("Hover should exist for getRows");
        if let HoverContents::Markup(content) = hover.contents {
            assert!(content.value.contains("getRows"));
            assert!(content.value.contains("Executes SQL query"));
        } else {
            panic!("Expected Markup HoverContents");
        }
    }

    #[test]
    fn test_hover_local_function() {
        let source = "function minhaFuncao(x) {\n  return x;\n}\nminhaFuncao(1);";
        let tree = parse_pnuts(source, None).unwrap();

        let position = Position {
            line: 3,
            character: 4, // On 'minhaFuncao'
        };

        let hover = find_hover(&tree, source, position).expect("Hover should exist for local function");
        if let HoverContents::Markup(content) = hover.contents {
            assert!(content.value.contains("minhaFuncao"));
            assert!(content.value.contains("local definition"));
        } else {
            panic!("Expected Markup HoverContents");
        }
    }

    #[test]
    fn test_document_symbols_outline() {
        let source = "function funcA() {}\nfunction funcB(p) {}\nclass MinhaClasse {\n  methodA() {}\n}";
        let tree = parse_pnuts(source, None).unwrap();

        let symbols = find_document_symbols(&tree, source);
        assert!(symbols.iter().any(|s| s.name == "funcA" && s.kind == SymbolKind::FUNCTION));
        assert!(symbols.iter().any(|s| s.name == "funcB" && s.kind == SymbolKind::FUNCTION));
        assert!(symbols.iter().any(|s| s.name == "MinhaClasse" && s.kind == SymbolKind::CLASS));
    }

    #[test]
    fn test_hover_enterprise_classes() {
        let source = "cursor = MConnection::getMCursor();\nuser = MLogin::getUser();\nform = MForm::getDialogForm(m, 'TESTE', 'ME', 'Titulo', 3);";
        let tree = parse_pnuts(source, None).unwrap();

        // 1. MConnection hover
        let hover_conn = find_hover(&tree, source, Position { line: 0, character: 12 }).unwrap();
        if let HoverContents::Markup(c) = hover_conn.contents {
            assert!(c.value.contains("MConnection"));
            assert!(c.value.contains("Gerenciador de conexão"));
            assert!(c.value.contains("cursor.beginWork()"));
        } else {
            panic!("Expected markup");
        }

        // 2. MLogin hover
        let hover_login = find_hover(&tree, source, Position { line: 1, character: 9 }).unwrap();
        if let HoverContents::Markup(c) = hover_login.contents {
            assert!(c.value.contains("MLogin"));
            assert!(c.value.contains("MLogin::getUser()"));
        } else {
            panic!("Expected markup");
        }

        // 3. MForm hover
        let hover_form = find_hover(&tree, source, Position { line: 2, character: 9 }).unwrap();
        if let HoverContents::Markup(c) = hover_form.contents {
            assert!(c.value.contains("MForm"));
            assert!(c.value.contains("getDialogForm"));
        } else {
            panic!("Expected markup");
        }
    }

    #[test]
    fn test_hover_common_methods_and_variables() {
        let source = "texto = '  teste  ';\nlimpo = texto.trim();";
        let tree = parse_pnuts(source, None).unwrap();

        // Hover on 'trim'
        let hover_trim = find_hover(&tree, source, Position { line: 1, character: 16 }).unwrap();
        if let HoverContents::Markup(c) = hover_trim.contents {
            assert!(c.value.contains("trim"));
            assert!(c.value.contains("whitespace"));
        } else {
            panic!("Expected markup");
        }

        // Hover on local variable 'limpo'
        let hover_var = find_hover(&tree, source, Position { line: 1, character: 2 }).unwrap();
        if let HoverContents::Markup(c) = hover_var.contents {
            assert!(c.value.contains("limpo"));
            assert!(c.value.contains("variable"));
        } else {
            panic!("Expected markup");
        }
    }
}
