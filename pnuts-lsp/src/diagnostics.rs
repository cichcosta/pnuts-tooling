use tower_lsp::lsp_types::*;
use tree_sitter::{Node, Tree};

/// Maximum number of diagnostics reported per file to prevent editor lag.
const MAX_DIAGNOSTICS: usize = 50;

/// Collects syntax error diagnostics from a Tree-sitter syntax tree.
pub fn collect_diagnostics(tree: &Tree, source: &str) -> Vec<Diagnostic> {
    let mut diagnostics = Vec::new();
    let root = tree.root_node();

    if !root.has_error() {
        return diagnostics;
    }

    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
    traverse_errors(root, source, &lines, &mut diagnostics);

    diagnostics
}

fn traverse_errors(
    node: Node,
    source: &str,
    lines: &[&str],
    diagnostics: &mut Vec<Diagnostic>,
) {
    if diagnostics.len() >= MAX_DIAGNOSTICS {
        return;
    }

    if node.is_error() {
        let diag = build_diagnostic(node, source, lines, false);
        diagnostics.push(diag);
        // Do not traverse into child errors of an ERROR node to avoid duplicate overlapping squigglies
        return;
    }

    if node.is_missing() {
        let diag = build_diagnostic(node, source, lines, true);
        diagnostics.push(diag);
        return;
    }

    // Traverse only children that contain errors
    let count = node.child_count();
    for i in 0..count {
        if let Some(child) = node.child(i) {
            if child.has_error() {
                traverse_errors(child, source, lines, diagnostics);
            }
        }
    }
}

fn build_diagnostic(
    node: Node,
    source: &str,
    lines: &[&str],
    is_missing: bool,
) -> Diagnostic {
    let start_point = node.start_position();
    let end_point = node.end_position();

    let start_line = start_point.row;
    let end_line = end_point.row;

    let start_line_str = lines.get(start_line).copied().unwrap_or("");
    let end_line_str = lines.get(end_line).copied().unwrap_or("");

    let start_char = byte_col_to_utf16(start_line_str, start_point.column);
    let mut end_char = byte_col_to_utf16(end_line_str, end_point.column);

    // If start and end are identical (e.g. missing token or 0-width error),
    // extend end_char by at least 1 character or to the end of the line so the squiggly is visible.
    if start_line == end_line && start_char == end_char {
        let line_len = start_line_str.encode_utf16().count() as u32;
        if end_char < line_len {
            end_char += 1;
        } else if end_char > 0 {
            // Underline previous character if at end of line
            end_char = start_char;
        }
    }

    let range = Range {
        start: Position {
            line: start_line as u32,
            character: start_char,
        },
        end: Position {
            line: end_line as u32,
            character: end_char.max(start_char + 1),
        },
    };

    let message = if is_missing {
        format!("Syntax error: missing expected '{}'", node.kind())
    } else {
        if let Ok(text) = node.utf8_text(source.as_bytes()) {
            let trimmed = text.trim();
            if !trimmed.is_empty() && trimmed.len() <= 20 {
                format!("Syntax error: unexpected token '{}'", trimmed)
            } else {
                "Syntax error: unexpected token or invalid syntax".to_string()
            }
        } else {
            "Syntax error: unexpected token or invalid syntax".to_string()
        }
    };

    Diagnostic {
        range,
        severity: Some(DiagnosticSeverity::ERROR),
        code: Some(NumberOrString::String("syntax-error".to_string())),
        code_description: None,
        source: Some("pnuts-lsp".to_string()),
        message,
        related_information: None,
        tags: None,
        data: None,
    }
}

/// Converts a byte column offset in a line into a UTF-16 character offset.
fn byte_col_to_utf16(line: &str, byte_col: usize) -> u32 {
    let mut safe_col = byte_col.min(line.len());
    while safe_col > 0 && !line.is_char_boundary(safe_col) {
        safe_col -= 1;
    }
    line[..safe_col].encode_utf16().count() as u32
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser::parse_pnuts;

    #[test]
    fn test_diagnostics_on_valid_code() {
        let code = r#"
            package com.test;
            import java.util.List;

            function calculate(a, b) {
                if (a > b) {
                    return a * 2;
                }
                return b;
            }
        "#;
        let tree = parse_pnuts(code, None).unwrap();
        let diags = collect_diagnostics(&tree, code);
        assert!(diags.is_empty(), "Valid code should produce 0 diagnostics");
    }

    #[test]
    fn test_diagnostics_on_syntax_error() {
        let code = "function ( broken syntax %%% ;";
        let tree = parse_pnuts(code, None).unwrap();
        let diags = collect_diagnostics(&tree, code);

        assert!(!diags.is_empty(), "Broken code must produce at least 1 diagnostic");
        let first = &diags[0];
        assert_eq!(first.severity, Some(DiagnosticSeverity::ERROR));
        assert_eq!(first.source, Some("pnuts-lsp".to_string()));
        assert!(first.message.contains("Syntax error"));
    }

    #[test]
    fn test_byte_col_to_utf16_conversion() {
        let line = "let preço = 100;";
        // 'ç' is 2 bytes in UTF-8, 1 code unit in UTF-16
        let byte_pos = line.find('=').unwrap();
        let utf16_pos = byte_col_to_utf16(line, byte_pos);
        assert_eq!(utf16_pos, 10);
    }
}
