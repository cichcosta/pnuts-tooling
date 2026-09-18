use tower_lsp::lsp_types::*;
use tree_sitter::{Node, Tree};
use url::Url;

use crate::completion::BUILTIN_FUNCTIONS;

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

    None
}

/// Collects document symbols (outline of functions, methods, classes).
pub fn find_document_symbols(tree: &Tree, source: &str) -> Vec<DocumentSymbol> {
    let mut symbols = Vec::new();
    let root = tree.root_node();
    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();

    collect_symbols_recursive(root, source, &lines, &mut symbols);
    symbols
}

fn get_name_identifier<'a>(node: Node<'a>) -> Option<Node<'a>> {
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

fn find_function_or_method_def<'a>(
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

fn find_parameter_in_func<'a>(
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

fn find_assignment_def<'a>(
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
}
