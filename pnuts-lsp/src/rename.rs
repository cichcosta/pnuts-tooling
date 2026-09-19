use std::collections::{HashMap, HashSet};
use tower_lsp::lsp_types::*;
use tree_sitter::{Node, Tree};
use url::Url;

use crate::completion::{BUILTIN_FUNCTIONS, COMMON_JAVA_CLASSES, KEYWORDS};
use crate::navigation::{
    find_function_or_method_def, find_parameter_in_func, get_name_identifier, node_to_range,
    position_to_byte_offset,
};

/// Validates whether a proposed name is a valid identifier.
pub fn is_valid_identifier(name: &str) -> bool {
    if name.is_empty() {
        return false;
    }
    let mut chars = name.chars();
    let first = chars.next().unwrap();
    if !first.is_alphabetic() && first != '_' {
        return false;
    }
    for ch in chars {
        if !ch.is_alphanumeric() && ch != '_' {
            return false;
        }
    }
    !is_keyword(name)
}

/// Checks whether a name is a reserved keyword in Pnuts/Groovy.
pub fn is_keyword(name: &str) -> bool {
    KEYWORDS.iter().any(|&(kw, _)| kw == name)
}

/// Checks whether a symbol is protected (built-in or keyword) and cannot be renamed.
pub fn is_protected_symbol(name: &str) -> bool {
    if is_keyword(name) {
        return true;
    }
    if BUILTIN_FUNCTIONS.iter().any(|&(f, _, _, _)| f == name) {
        return true;
    }
    if COMMON_JAVA_CLASSES.iter().any(|&(c, _)| c == name) {
        return true;
    }
    false
}

/// Locates the target identifier node and its text at the given position.
pub fn resolve_target_symbol<'a>(
    tree: &'a Tree,
    source: &'a str,
    position: Position,
) -> Option<(Node<'a>, &'a str)> {
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

    if is_protected_symbol(symbol_name) {
        return None;
    }

    Some((node, symbol_name))
}

/// Checks if a node is the right-hand property of an object access (e.g., `obj.property`).
fn is_property_access(node: Node) -> bool {
    if let Some(parent) = node.parent() {
        if parent.kind() == "member_expression" || parent.kind() == "static_member_expression" {
            if let Some(last) = parent.child(parent.child_count().saturating_sub(1)) {
                return last == node;
            }
        }
    }
    false
}

/// Finds the enclosing function or method definition for a given node.
fn find_enclosing_function<'a>(node: Node<'a>) -> Option<Node<'a>> {
    let mut curr = node.parent();
    while let Some(n) = curr {
        if n.kind() == "function_definition" || n.kind() == "method_definition" {
            return Some(n);
        }
        curr = n.parent();
    }
    None
}

/// Checks whether a function defines or assigns a symbol locally in its own scope.
fn has_local_definition(func: Node, source: &str, target_name: &str) -> bool {
    let mut stack = vec![func];
    while let Some(n) = stack.pop() {
        if n != func && (n.kind() == "function_definition" || n.kind() == "method_definition") {
            continue;
        }
        match n.kind() {
            "assignment_expression" => {
                if let Some(left) = n.child(0) {
                    if left.kind() == "identifier" {
                        if let Ok(text) = left.utf8_text(source.as_bytes()) {
                            if text == target_name {
                                return true;
                            }
                        }
                    }
                }
            }
            "variable_declaration" | "for_statement" | "foreach_statement" | "catch_clause" => {
                for i in 0..n.child_count() {
                    if let Some(child) = n.child(i) {
                        if child.kind() == "identifier" {
                            if let Ok(text) = child.utf8_text(source.as_bytes()) {
                                if text == target_name {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            _ => {}
        }
        for i in 0..n.child_count() {
            if let Some(child) = n.child(i) {
                stack.push(child);
            }
        }
    }
    false
}

/// Recursively collects identifier occurrences of target_name within a given scope.
fn collect_occurrences_in_scope<'a>(
    scope_node: Node<'a>,
    source: &str,
    target_name: &str,
    is_top_level: bool,
    out: &mut Vec<Node<'a>>,
) {
    let mut stack = vec![scope_node];

    while let Some(node) = stack.pop() {
        // If this is a nested function (and not the scope root itself):
        if node != scope_node && (node.kind() == "function_definition" || node.kind() == "method_definition") {
            // Check if shadowed by parameter
            if find_parameter_in_func(node, source, target_name).is_some() {
                continue;
            }
            // Check if shadowed by local definition when renaming a top-level symbol
            if is_top_level && has_local_definition(node, source, target_name) {
                continue;
            }
        }

        if node.kind() == "identifier" || node.kind() == "property_identifier" {
            if let Ok(text) = node.utf8_text(source.as_bytes()) {
                if text == target_name {
                    if !is_property_access(node) {
                        out.push(node);
                    }
                }
            }
        }

        for i in (0..node.child_count()).rev() {
            if let Some(child) = node.child(i) {
                stack.push(child);
            }
        }
    }
}

/// Prepares the rename action, returning the symbol range and placeholder.
pub fn prepare_rename(
    tree: &Tree,
    source: &str,
    position: Position,
) -> Option<PrepareRenameResponse> {
    let (node, symbol_name) = resolve_target_symbol(tree, source, position)?;
    if is_property_access(node) {
        return None;
    }
    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
    let range = node_to_range(node, &lines);

    Some(PrepareRenameResponse::RangeWithPlaceholder {
        range,
        placeholder: symbol_name.to_string(),
    })
}

/// Renames the symbol at the given position and produces a WorkspaceEdit.
pub fn rename_symbol(
    tree: &Tree,
    source: &str,
    position: Position,
    new_name: &str,
    uri: &Url,
) -> Option<WorkspaceEdit> {
    if !is_valid_identifier(new_name) {
        return None;
    }

    let (target_node, symbol_name) = resolve_target_symbol(tree, source, position)?;
    if is_property_access(target_node) {
        return None;
    }

    let root = tree.root_node();
    let enclosing_fn = find_enclosing_function(target_node);

    let is_param = if let Some(func) = enclosing_fn {
        find_parameter_in_func(func, source, symbol_name).is_some()
    } else {
        false
    };

    let is_func_name = find_function_or_method_def(root, source, symbol_name).is_some()
        || (target_node.parent().map_or(false, |p| {
            (p.kind() == "function_definition" || p.kind() == "method_definition")
                && get_name_identifier(p) == Some(target_node)
        }));

    let mut occurrences = Vec::new();

    if is_param {
        let func = enclosing_fn.unwrap();
        collect_occurrences_in_scope(func, source, symbol_name, false, &mut occurrences);
    } else if is_func_name {
        collect_occurrences_in_scope(root, source, symbol_name, true, &mut occurrences);
    } else if let Some(func) = enclosing_fn {
        collect_occurrences_in_scope(func, source, symbol_name, false, &mut occurrences);
    } else {
        collect_occurrences_in_scope(root, source, symbol_name, true, &mut occurrences);
    }

    if occurrences.is_empty() {
        return None;
    }

    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
    let mut byte_spans = HashSet::new();
    let mut edits = Vec::new();

    for node in occurrences {
        let span = (node.start_byte(), node.end_byte());
        if byte_spans.insert(span) {
            let range = node_to_range(node, &lines);
            edits.push(TextEdit {
                range,
                new_text: new_name.to_string(),
            });
        }
    }

    edits.sort_by(|a, b| {
        a.range
            .start
            .line
            .cmp(&b.range.start.line)
            .then_with(|| a.range.start.character.cmp(&b.range.start.character))
    });

    let mut changes = HashMap::new();
    changes.insert(uri.clone(), edits);

    Some(WorkspaceEdit {
        changes: Some(changes),
        document_changes: None,
        change_annotations: None,
    })
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser::parse_pnuts;

    #[test]
    fn test_rename_function_parameter() {
        let source = r#"
            function process(items) {
                total = 0;
                foreach item(items) {
                    total += item.val;
                }
                return items;
            }
            function other(items) {
                return items;
            }
        "#;
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        // Position on 'items' parameter declaration at line 1, char 29
        let pos = Position { line: 1, character: 31 };
        let edit = rename_symbol(&tree, source, pos, "orderList", &uri);
        assert!(edit.is_some());

        let ws_edit = edit.unwrap();
        let changes = ws_edit.changes.unwrap();
        let edits = changes.get(&uri).unwrap();

        // Must rename the 3 occurrences inside `process`, but NOT the occurrence inside `other`
        assert_eq!(edits.len(), 3);
        for e in edits {
            assert_eq!(e.new_text, "orderList");
            // None of the edits should be on line 8 (where `other` is defined)
            assert_ne!(e.range.start.line, 8);
        }
    }

    #[test]
    fn test_rename_local_variable_scoped() {
        let source = r#"
            function first() {
                var total = 10;
                total = total + 5;
                return total;
            }
            function second() {
                total = 999;
                return total;
            }
        "#;
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        // Position on 'total' in `first` at line 2, char 22
        let pos = Position { line: 2, character: 22 };
        let edit = rename_symbol(&tree, source, pos, "subtotal", &uri);
        assert!(edit.is_some());

        let edits = edit.unwrap().changes.unwrap().remove(&uri).unwrap();
        // 4 occurrences in `first`:
        // 1. var total = 10;
        // 2. total (LHS) = ...
        // 3. ... + total (RHS)
        // 4. return total;
        assert_eq!(edits.len(), 4);
        for e in &edits {
            assert_eq!(e.new_text, "subtotal");
            // Must stay within lines 2..=4
            assert!(e.range.start.line >= 2 && e.range.start.line <= 4);
        }
    }

    #[test]
    fn test_rename_function_definition_and_calls() {
        let source = r#"
            function calculate(a, b) {
                return a + b;
            }
            res = calculate(10, 20);
            println(calculate(30, 40));
        "#;
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        // Position on 'calculate' call at line 4, char 18
        let pos = Position { line: 4, character: 18 };
        let edit = rename_symbol(&tree, source, pos, "computeSum", &uri);
        assert!(edit.is_some());

        let edits = edit.unwrap().changes.unwrap().remove(&uri).unwrap();
        // Declaration on line 1, call on line 4, call on line 5 = 3 occurrences
        assert_eq!(edits.len(), 3);
        for e in &edits {
            assert_eq!(e.new_text, "computeSum");
        }
    }

    #[test]
    fn test_rename_protected_builtin_returns_none() {
        let source = r#"
            println("Hello");
            getRows("TABLE", "query");
        "#;
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        // Position on println
        let pos = Position { line: 1, character: 14 };
        let prep = prepare_rename(&tree, source, pos);
        assert!(prep.is_none(), "prepare_rename on println must be None");

        let edit = rename_symbol(&tree, source, pos, "myPrint", &uri);
        assert!(edit.is_none(), "rename_symbol on println must be None");
    }

    #[test]
    fn test_rename_invalid_identifier_returns_none() {
        let source = "var validName = 10;";
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        let pos = Position { line: 0, character: 5 };
        // Empty name
        assert!(rename_symbol(&tree, source, pos, "", &uri).is_none());
        // Name starting with digit
        assert!(rename_symbol(&tree, source, pos, "123bad", &uri).is_none());
        // Reserved keyword
        assert!(rename_symbol(&tree, source, pos, "function", &uri).is_none());
    }
}
