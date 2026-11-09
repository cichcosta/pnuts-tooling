use std::collections::HashMap;
use tower_lsp::lsp_types::*;
use tree_sitter::Tree;
use url::Url;

use crate::navigation::position_to_byte_offset;

/// Common Java classes that can be auto-imported into Pnuts / Groovy scripts.
pub(crate) const AUTO_IMPORTABLE_CLASSES: &[(&str, &str)] = &[
    ("List", "java.util.List"),
    ("ArrayList", "java.util.ArrayList"),
    ("Map", "java.util.Map"),
    ("HashMap", "java.util.HashMap"),
    ("Set", "java.util.Set"),
    ("HashSet", "java.util.HashSet"),
    ("Vector", "java.util.Vector"),
    ("BigDecimal", "java.math.BigDecimal"),
    ("BigInteger", "java.math.BigInteger"),
    ("SimpleDateFormat", "java.text.SimpleDateFormat"),
    ("Date", "java.util.Date"),
    ("Calendar", "java.util.Calendar"),
    ("JSONObject", "org.json.JSONObject"),
    ("JSONArray", "org.json.JSONArray"),
];

/// Computes available code actions (Quick Fixes and Refactorings) for the given range.
pub fn provide_code_actions(
    _tree: Option<&Tree>,
    source: &str,
    range: Range,
    uri: &Url,
) -> Vec<CodeActionOrCommand> {
    let mut actions = Vec::new();

    // 1. Refactoring: Wrap selection (or line) in try / catch block
    if let Some(wrap_action) = action_wrap_try_catch(source, range, uri) {
        actions.push(CodeActionOrCommand::CodeAction(wrap_action));
    }

    // 2. Quick Fixes: Auto-import common Java classes
    let import_actions = action_suggest_imports(source, range, uri);
    for act in import_actions {
        actions.push(CodeActionOrCommand::CodeAction(act));
    }

    // 3. Quick Action: Insert standard script header
    if let Some(header_action) = action_insert_header(source, range, uri) {
        actions.push(CodeActionOrCommand::CodeAction(header_action));
    }

    actions
}

/// Creates a refactoring action to wrap the selected lines in a try/catch block.
fn action_wrap_try_catch(source: &str, range: Range, uri: &Url) -> Option<CodeAction> {
    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
    if lines.is_empty() {
        return None;
    }

    let start_line = (range.start.line as usize).min(lines.len() - 1);
    let end_line = (range.end.line as usize).min(lines.len() - 1);

    // If selection is completely empty and on an empty line, skip
    if start_line == end_line && lines[start_line].trim().is_empty() {
        return None;
    }

    let first_line = lines[start_line];
    let indent_len = first_line.chars().take_while(|c| *c == ' ' || *c == '\t').count();
    let base_indent = &first_line[..indent_len];
    let extra_indent = "    ";

    let mut body = String::new();
    for l in start_line..=end_line {
        let line = lines[l];
        if line.trim().is_empty() {
            body.push('\n');
        } else {
            body.push_str(base_indent);
            body.push_str(extra_indent);
            body.push_str(line.trim_start());
            body.push('\n');
        }
    }

    let wrapped = format!(
        "{}try {{\n{}{}    flush();\n{}}} catch (Exception e) {{\n{}{}showError(e.getMessage());\n{}}}",
        base_indent, body, base_indent, base_indent, base_indent, extra_indent, base_indent
    );

    let edit_range = Range {
        start: Position {
            line: start_line as u32,
            character: 0,
        },
        end: Position {
            line: end_line as u32,
            character: lines[end_line].encode_utf16().count() as u32,
        },
    };

    let mut changes = HashMap::new();
    changes.insert(
        uri.clone(),
        vec![TextEdit {
            range: edit_range,
            new_text: wrapped,
        }],
    );

    Some(CodeAction {
        title: "Envolver em bloco try / catch".to_string(),
        kind: Some(CodeActionKind::REFACTOR_REWRITE),
        diagnostics: None,
        edit: Some(WorkspaceEdit {
            changes: Some(changes),
            document_changes: None,
            change_annotations: None,
        }),
        command: None,
        is_preferred: Some(true),
        disabled: None,
        data: None,
    })
}

/// Inspects the cursor position and file contents to offer auto-import quick fixes.
fn action_suggest_imports(source: &str, range: Range, uri: &Url) -> Vec<CodeAction> {
    let mut actions = Vec::new();

    // Check if cursor or selection is on an identifier
    let word_at_cursor = get_word_at_position(source, range.start);

    for &(class_name, qualified) in AUTO_IMPORTABLE_CLASSES {
        // Only suggest if:
        // 1. The cursor is on this class name, OR the file mentions it somewhere
        // 2. The file does not already have an import for it
        let matches_cursor = word_at_cursor.as_deref() == Some(class_name);
        let in_source = matches_cursor || source.contains(class_name);

        if in_source && !has_import(source, class_name, qualified) {
            let (insert_line, insert_text) = compute_import_insertion(source, qualified);
            let edit_range = Range {
                start: Position {
                    line: insert_line as u32,
                    character: 0,
                },
                end: Position {
                    line: insert_line as u32,
                    character: 0,
                },
            };

            let mut changes = HashMap::new();
            changes.insert(
                uri.clone(),
                vec![TextEdit {
                    range: edit_range,
                    new_text: insert_text,
                }],
            );

            actions.push(CodeAction {
                title: format!("Importar '{}'", qualified),
                kind: Some(CodeActionKind::QUICKFIX),
                diagnostics: None,
                edit: Some(WorkspaceEdit {
                    changes: Some(changes),
                    document_changes: None,
                    change_annotations: None,
                }),
                command: None,
                is_preferred: Some(matches_cursor),
                disabled: None,
                data: None,
            });
        }
    }

    actions
}

/// Checks if the source already imports the class either explicitly or via wildcard.
fn has_import(source: &str, class_name: &str, qualified: &str) -> bool {
    let pkg = qualified.rsplit_once('.').map(|(p, _)| p).unwrap_or("");
    for line in source.lines() {
        let t = line.trim();
        if t.starts_with("import ") {
            let without_import = t.strip_prefix("import ").unwrap_or("").trim_end_matches(';').trim();
            if without_import == qualified || without_import == class_name {
                return true;
            }
            if !pkg.is_empty() && (without_import == format!("{}.*", pkg) || without_import == format!("{}::*", pkg)) {
                return true;
            }
        }
    }
    false
}

/// Determines the line index and text to insert an import statement.
fn compute_import_insertion(source: &str, qualified: &str) -> (usize, String) {
    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
    let mut last_import_line = None;
    let mut package_line = None;

    for (idx, line) in lines.iter().enumerate() {
        let t = line.trim();
        if t.starts_with("package ") {
            package_line = Some(idx);
        } else if t.starts_with("import ") {
            last_import_line = Some(idx);
        }
    }

    if let Some(imp_idx) = last_import_line {
        (imp_idx + 1, format!("import {};\n", qualified))
    } else if let Some(pkg_idx) = package_line {
        (pkg_idx + 1, format!("\nimport {};\n", qualified))
    } else {
        (0, format!("import {};\n", qualified))
    }
}

/// Offers an action to insert standard script documentation header.
fn action_insert_header(source: &str, range: Range, uri: &Url) -> Option<CodeAction> {
    // Only offer if cursor is at the top of the file (lines 0 or 1) and no header exists
    if range.start.line > 1 {
        return None;
    }

    if source.contains("============================================================================") {
        return None;
    }

    let header_text = r#"// ============================================================================
// Script: Rotina de Processamento
// Descrição: Execução de lógica de negócio e regras fiscais/banco
// ============================================================================

"#;

    let mut changes = HashMap::new();
    changes.insert(
        uri.clone(),
        vec![TextEdit {
            range: Range {
                start: Position { line: 0, character: 0 },
                end: Position { line: 0, character: 0 },
            },
            new_text: header_text.to_string(),
        }],
    );

    Some(CodeAction {
        title: "Inserir cabeçalho padrão de script".to_string(),
        kind: Some(CodeActionKind::QUICKFIX),
        diagnostics: None,
        edit: Some(WorkspaceEdit {
            changes: Some(changes),
            document_changes: None,
            change_annotations: None,
        }),
        command: None,
        is_preferred: Some(false),
        disabled: None,
        data: None,
    })
}

/// Helper to get the word/identifier under the cursor.
fn get_word_at_position(source: &str, position: Position) -> Option<String> {
    let offset = position_to_byte_offset(source, position)?;
    let bytes = source.as_bytes();

    let mut start = offset;
    while start > 0 && (bytes[start - 1].is_ascii_alphanumeric() || bytes[start - 1] == b'_' || bytes[start - 1] == b'$') {
        start -= 1;
    }

    let mut end = offset;
    while end < bytes.len() && (bytes[end].is_ascii_alphanumeric() || bytes[end] == b'_' || bytes[end] == b'$') {
        end += 1;
    }

    if start == end {
        None
    } else {
        Some(source[start..end].to_string())
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_wrap_try_catch_action() {
        let source = "a = 1;\nb = 2;";
        let uri = Url::parse("file:///workspace/test.pnuts").unwrap();
        let range = Range {
            start: Position { line: 0, character: 0 },
            end: Position { line: 1, character: 6 },
        };

        let actions = provide_code_actions(None, source, range, &uri);
        let try_action = actions
            .iter()
            .find_map(|a| match a {
                CodeActionOrCommand::CodeAction(ca) if ca.title.contains("try / catch") => Some(ca),
                _ => None,
            })
            .expect("Try/catch action should be available");

        let edit = try_action.edit.as_ref().unwrap();
        let changes = edit.changes.as_ref().unwrap();
        let text_edits = changes.get(&uri).unwrap();
        assert!(text_edits[0].new_text.contains("try {"));
        assert!(text_edits[0].new_text.contains("catch (Exception e)"));
        assert!(text_edits[0].new_text.contains("showError(e.getMessage());"));
    }

    #[test]
    fn test_auto_import_action() {
        let source = "lista = new ArrayList();";
        let uri = Url::parse("file:///workspace/test.pnuts").unwrap();
        let range = Range {
            start: Position { line: 0, character: 14 }, // On ArrayList
            end: Position { line: 0, character: 14 },
        };

        let actions = provide_code_actions(None, source, range, &uri);
        let import_action = actions
            .iter()
            .find_map(|a| match a {
                CodeActionOrCommand::CodeAction(ca) if ca.title.contains("java.util.ArrayList") => Some(ca),
                _ => None,
            })
            .expect("Import action for ArrayList should be available");

        let edit = import_action.edit.as_ref().unwrap();
        let changes = edit.changes.as_ref().unwrap();
        let text_edits = changes.get(&uri).unwrap();
        assert_eq!(text_edits[0].new_text, "import java.util.ArrayList;\n");
    }

    #[test]
    fn test_insert_script_header_action() {
        let source = "a = 1;\n";
        let uri = Url::parse("file:///workspace/test.pnuts").unwrap();
        let range = Range {
            start: Position { line: 0, character: 0 },
            end: Position { line: 0, character: 0 },
        };

        let actions = provide_code_actions(None, source, range, &uri);
        let header_action = actions
            .iter()
            .find_map(|a| match a {
                CodeActionOrCommand::CodeAction(ca) if ca.title.contains("cabeçalho") => Some(ca),
                _ => None,
            })
            .expect("Header action should be available at top of file");

        let edit = header_action.edit.as_ref().unwrap();
        let changes = edit.changes.as_ref().unwrap();
        let text_edits = changes.get(&uri).unwrap();
        assert!(text_edits[0].new_text.contains("============================================================================"));
    }
}
