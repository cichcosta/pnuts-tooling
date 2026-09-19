use std::collections::HashSet;
use tower_lsp::lsp_types::*;
use tree_sitter::{Node, Tree};
use url::Url;

use crate::navigation::{
    find_function_or_method_def, find_parameter_in_func, get_name_identifier, node_to_range,
    position_to_byte_offset,
};
use crate::rename::{
    collect_occurrences_in_scope, find_enclosing_function, is_keyword, is_property_access,
};

/// Resolves the target identifier node and its text for a Find References request.
/// Returns None if cursor is on a keyword, whitespace, or invalid token.
pub fn resolve_reference_symbol<'a>(
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

    if is_keyword(symbol_name) {
        return None;
    }

    Some((node, symbol_name))
}

/// Finds all references for the symbol at the given position within the document.
pub fn find_references(
    tree: &Tree,
    source: &str,
    position: Position,
    context: &ReferenceContext,
    uri: &Url,
) -> Option<Vec<Location>> {
    let (target_node, symbol_name) = resolve_reference_symbol(tree, source, position)?;
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

    // Determine declaration node to support context.include_declaration == false
    let declaration_node: Option<Node> = if is_param {
        enclosing_fn.and_then(|f| find_parameter_in_func(f, source, symbol_name))
    } else if is_func_name {
        find_function_or_method_def(root, source, symbol_name)
    } else {
        // For local / global variables, the declaration is the earliest assignment or declaration
        occurrences.iter().min_by_key(|n| n.start_byte()).copied()
    };

    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
    let mut byte_spans = HashSet::new();
    let mut locations = Vec::new();

    for node in occurrences {
        if !context.include_declaration {
            if let Some(decl) = declaration_node {
                if node.start_byte() == decl.start_byte() && node.end_byte() == decl.end_byte() {
                    continue;
                }
            }
        }

        let span = (node.start_byte(), node.end_byte());
        if byte_spans.insert(span) {
            let range = node_to_range(node, &lines);
            locations.push(Location::new(uri.clone(), range));
        }
    }

    locations.sort_by(|a, b| {
        a.range
            .start
            .line
            .cmp(&b.range.start.line)
            .then_with(|| a.range.start.character.cmp(&b.range.start.character))
    });

    Some(locations)
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser::parse_pnuts;

    #[test]
    fn test_references_function_with_and_without_declaration() {
        let source = r#"function calcTotal(x, y) {
    return x + y;
}

res1 = calcTotal(1, 2);
res2 = calcTotal(3, 4);
"#;
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        // 1. Cursor on `calcTotal` definition (line 0, character 10)
        let pos = Position {
            line: 0,
            character: 10,
        };

        // include_declaration = true -> 3 locations (definition + 2 calls)
        let ctx_with = ReferenceContext {
            include_declaration: true,
        };
        let refs_with = find_references(&tree, source, pos, &ctx_with, &uri).unwrap();
        assert_eq!(refs_with.len(), 3);
        assert_eq!(refs_with[0].range.start.line, 0);
        assert_eq!(refs_with[1].range.start.line, 4);
        assert_eq!(refs_with[2].range.start.line, 5);

        // include_declaration = false -> 2 locations (only the 2 calls)
        let ctx_without = ReferenceContext {
            include_declaration: false,
        };
        let refs_without = find_references(&tree, source, pos, &ctx_without, &uri).unwrap();
        assert_eq!(refs_without.len(), 2);
        assert_eq!(refs_without[0].range.start.line, 4);
        assert_eq!(refs_without[1].range.start.line, 5);

        // 2. Cursor on call site (line 4, character 8)
        let call_pos = Position {
            line: 4,
            character: 8,
        };
        let refs_from_call = find_references(&tree, source, call_pos, &ctx_with, &uri).unwrap();
        assert_eq!(refs_from_call.len(), 3);
    }

    #[test]
    fn test_references_parameter_with_and_without_declaration() {
        let source = r#"function greet(name, title) {
    msg = title + " " + name;
    println(name);
    return msg;
}
"#;
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        // Cursor on `name` in parameter list (line 0, character 16)
        let pos = Position {
            line: 0,
            character: 16,
        };

        let ctx_with = ReferenceContext {
            include_declaration: true,
        };
        let refs_with = find_references(&tree, source, pos, &ctx_with, &uri).unwrap();
        assert_eq!(refs_with.len(), 3);
        assert_eq!(refs_with[0].range.start.line, 0);
        assert_eq!(refs_with[1].range.start.line, 1);
        assert_eq!(refs_with[2].range.start.line, 2);

        let ctx_without = ReferenceContext {
            include_declaration: false,
        };
        let refs_without = find_references(&tree, source, pos, &ctx_without, &uri).unwrap();
        assert_eq!(refs_without.len(), 2);
        assert_eq!(refs_without[0].range.start.line, 1);
        assert_eq!(refs_without[1].range.start.line, 2);
    }

    #[test]
    fn test_references_local_variable_scoped() {
        let source = r#"function taskA() {
    counter = 1;
    counter = counter + 1;
    return counter;
}

function taskB() {
    counter = 100;
    return counter;
}
"#;
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        // Cursor on `counter` inside taskA (line 1, character 6)
        let pos = Position {
            line: 1,
            character: 6,
        };

        let ctx_with = ReferenceContext {
            include_declaration: true,
        };
        let refs_with = find_references(&tree, source, pos, &ctx_with, &uri).unwrap();
        // taskA has 4 occurrences of counter, taskB's counter must not be included
        assert_eq!(refs_with.len(), 4);
        for loc in &refs_with {
            assert!(loc.range.start.line <= 3);
        }

        let ctx_without = ReferenceContext {
            include_declaration: false,
        };
        let refs_without = find_references(&tree, source, pos, &ctx_without, &uri).unwrap();
        // 4 occurrences minus declaration (line 1) = 3 references
        assert_eq!(refs_without.len(), 3);
        for loc in &refs_without {
            assert!(loc.range.start.line > 1 && loc.range.start.line <= 3);
        }
    }

    #[test]
    fn test_references_builtin_calls() {
        let source = r#"println("one");
println("two");
"#;
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        let pos = Position {
            line: 0,
            character: 2,
        };
        let ctx = ReferenceContext {
            include_declaration: true,
        };
        let refs = find_references(&tree, source, pos, &ctx, &uri).unwrap();
        assert_eq!(refs.len(), 2);
    }

    #[test]
    fn test_references_keyword_returns_none() {
        let source = "if (true) { return null; }";
        let tree = parse_pnuts(source, None).unwrap();
        let uri = Url::parse("file:///test.pnuts").unwrap();

        // Cursor on `if`
        let pos = Position {
            line: 0,
            character: 1,
        };
        let ctx = ReferenceContext {
            include_declaration: true,
        };
        assert!(find_references(&tree, source, pos, &ctx, &uri).is_none());
    }
}
