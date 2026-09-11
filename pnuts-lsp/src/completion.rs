use std::collections::HashSet;
use tower_lsp::lsp_types::*;
use tree_sitter::{Node, Tree};

/// Keywords supported by Pnuts and Groovy scripting.
const KEYWORDS: &[(&str, &str)] = &[
    ("function", "Defines a function: function name(args) { ... }"),
    ("class", "Defines a class: class Name { ... }"),
    ("if", "Conditional branching: if (condition) { ... }"),
    ("else", "Alternative branch: else { ... }"),
    ("for", "Loop iteration: for (x in y) or for (i=0; i<n; i++)"),
    ("foreach", "Iteration loop: foreach item (collection) { ... }"),
    ("while", "While loop: while (condition) { ... }"),
    ("do", "Do-while loop: do { ... } while (condition);"),
    ("try", "Exception handling block: try { ... } catch (e) { ... }"),
    ("catch", "Catches exception: catch (Exception e) { ... }"),
    ("finally", "Guaranteed cleanup block: finally { ... }"),
    ("throw", "Throws an exception: throw new Exception(...)"),
    ("return", "Returns value from function: return value;"),
    ("break", "Breaks out of loop or switch"),
    ("continue", "Continues to next loop iteration"),
    ("switch", "Switch selection: switch (expr) { case ... }"),
    ("case", "Switch branch: case value:"),
    ("default", "Default switch branch: default:"),
    ("import", "Imports Java class or package: import java.util.*"),
    ("package", "Declares namespace package: package com.mycorp"),
    ("null", "Null literal reference"),
    ("true", "Boolean true literal"),
    ("false", "Boolean false literal"),
    ("new", "Instantiates an object: new ClassName(...)"),
    ("var", "Declares a local variable: var x = 10;"),
    ("def", "Declares dynamically-typed variable or method"),
];

/// Built-in functions common to Pnuts environments.
const BUILTIN_FUNCTIONS: &[(&str, &str, &str)] = &[
    ("println", "(msg)", "Prints message followed by newline to stdout"),
    ("print", "(msg)", "Prints message without newline to stdout"),
    ("flush", "()", "Flushes the standard output buffer"),
    ("char", "(code)", "Returns the character corresponding to ASCII/Unicode code"),
    ("open", "(filename)", "Opens a file or stream resource"),
    ("load", "(module)", "Loads and evaluates an external script or module"),
    ("eval", "(script)", "Dynamically evaluates a Pnuts expression string"),
    ("error", "(msg)", "Throws a runtime error with message"),
    ("getContext", "()", "Retrieves the current execution Context"),
    ("isArray", "(obj)", "Checks if target object is an array"),
    ("isFunction", "(obj)", "Checks if target object is a callable function"),
];

/// Common Java standard library classes frequently used in Pnuts scripts.
const COMMON_JAVA_CLASSES: &[(&str, &str)] = &[
    ("List", "java.util.List interface"),
    ("ArrayList", "java.util.ArrayList resizable array implementation"),
    ("Map", "java.util.Map key-value collection interface"),
    ("HashMap", "java.util.HashMap hash table implementation"),
    ("Set", "java.util.Set unique collection interface"),
    ("HashSet", "java.util.HashSet hash table set implementation"),
    ("String", "java.lang.String character sequence"),
    ("StringBuilder", "java.lang.StringBuilder mutable character sequence"),
    ("Integer", "java.lang.Integer boxed integer utility"),
    ("Double", "java.lang.Double boxed floating-point utility"),
    ("Boolean", "java.lang.Boolean boxed boolean utility"),
    ("Date", "java.util.Date timestamp representation"),
    ("Calendar", "java.util.Calendar date/time calendar utility"),
    ("SimpleDateFormat", "java.text.SimpleDateFormat date formatting"),
    ("Exception", "java.lang.Exception standard checked exception"),
    ("RuntimeException", "java.lang.RuntimeException unchecked exception"),
];

/// Structure describing a code snippet template.
struct Snippet {
    label: &'static str,
    snippet: &'static str,
    detail: &'static str,
}

const SNIPPETS: &[Snippet] = &[
    Snippet {
        label: "foreach (in)",
        snippet: "for (${1:item} in ${2:collection}) {\n\t$0\n}",
        detail: "for (item in collection) loop",
    },
    Snippet {
        label: "foreach",
        snippet: "foreach ${1:item} (${2:collection}) {\n\t$0\n}",
        detail: "foreach item (collection) loop",
    },
    Snippet {
        label: "for (i)",
        snippet: "for (${1:i} = 0; ${1:i} < ${2:max}; ${1:i}++) {\n\t$0\n}",
        detail: "Indexed for loop",
    },
    Snippet {
        label: "if",
        snippet: "if (${1:condition}) {\n\t$0\n}",
        detail: "if condition block",
    },
    Snippet {
        label: "if-else",
        snippet: "if (${1:condition}) {\n\t$2\n} else {\n\t$0\n}",
        detail: "if / else condition block",
    },
    Snippet {
        label: "while",
        snippet: "while (${1:condition}) {\n\t$0\n}",
        detail: "while loop block",
    },
    Snippet {
        label: "function",
        snippet: "function ${1:name}(${2:params}) {\n\t$0\n}",
        detail: "Function declaration",
    },
    Snippet {
        label: "try-catch",
        snippet: "try {\n\t$1\n} catch (${2:Exception} ${3:e}) {\n\t$0\n}",
        detail: "try / catch exception block",
    },
    Snippet {
        label: "try-catch-finally",
        snippet: "try {\n\t$1\n} catch (${2:Exception} ${3:e}) {\n\t$4\n} finally {\n\t$0\n}",
        detail: "try / catch / finally block",
    },
    Snippet {
        label: "switch",
        snippet: "switch (${1:expr}) {\n\tcase ${2:value}:\n\t\t$3\n\t\tbreak;\n\tdefault:\n\t\t$0\n}",
        detail: "switch / case block",
    },
    Snippet {
        label: "println",
        snippet: "println(${1:message});",
        detail: "Print line to stdout",
    },
    Snippet {
        label: "println-flush",
        snippet: "println(${1:message}); flush();",
        detail: "Print line and flush immediately",
    },
    Snippet {
        label: "char",
        snippet: "char(${1:34})",
        detail: "Character literal from ASCII code",
    },
];

/// Collects all completion items relevant at the given cursor position.
pub fn provide_completions(
    tree: Option<&Tree>,
    source: &str,
    position: Position,
) -> Vec<CompletionItem> {
    let mut items = Vec::new();
    let mut seen = HashSet::new();

    let cursor_line = position.line as usize;

    // 1. AST Scope Inspection (Local variables, parameters, and declared functions)
    if let Some(t) = tree {
        let root = t.root_node();
        collect_ast_symbols(root, source, cursor_line, &mut items, &mut seen);
    }

    // 2. Snippets
    for s in SNIPPETS {
        items.push(CompletionItem {
            label: s.label.to_string(),
            kind: Some(CompletionItemKind::SNIPPET),
            detail: Some(s.detail.to_string()),
            insert_text: Some(s.snippet.to_string()),
            insert_text_format: Some(InsertTextFormat::SNIPPET),
            sort_text: Some(format!("2_{}", s.label)),
            ..Default::default()
        });
    }

    // 3. Built-in Functions
    for &(name, sig, doc) in BUILTIN_FUNCTIONS {
        if seen.insert(name.to_string()) {
            items.push(CompletionItem {
                label: name.to_string(),
                kind: Some(CompletionItemKind::FUNCTION),
                detail: Some(format!("{}{}", name, sig)),
                documentation: Some(Documentation::String(doc.to_string())),
                insert_text: Some(format!("{}($0)", name)),
                insert_text_format: Some(InsertTextFormat::SNIPPET),
                sort_text: Some(format!("3_{}", name)),
                ..Default::default()
            });
        }
    }

    // 4. Common Java Classes
    for &(cls, doc) in COMMON_JAVA_CLASSES {
        if seen.insert(cls.to_string()) {
            items.push(CompletionItem {
                label: cls.to_string(),
                kind: Some(CompletionItemKind::CLASS),
                detail: Some(doc.to_string()),
                insert_text: Some(cls.to_string()),
                insert_text_format: Some(InsertTextFormat::PLAIN_TEXT),
                sort_text: Some(format!("4_{}", cls)),
                ..Default::default()
            });
        }
    }

    // 5. Language Keywords
    for &(kw, doc) in KEYWORDS {
        if seen.insert(kw.to_string()) {
            items.push(CompletionItem {
                label: kw.to_string(),
                kind: Some(CompletionItemKind::KEYWORD),
                detail: Some("Keyword".to_string()),
                documentation: Some(Documentation::String(doc.to_string())),
                insert_text: Some(kw.to_string()),
                insert_text_format: Some(InsertTextFormat::PLAIN_TEXT),
                sort_text: Some(format!("5_{}", kw)),
                ..Default::default()
            });
        }
    }

    items
}

/// Recursively walks the Tree-sitter AST to extract variables and functions.
fn collect_ast_symbols(
    node: Node,
    source: &str,
    cursor_line: usize,
    items: &mut Vec<CompletionItem>,
    seen: &mut HashSet<String>,
) {
    let kind = node.kind();

    // Collect function definitions across the document
    if kind == "function_definition" {
        if let Some(name_node) = node.child_by_field_name("name").or_else(|| {
            // Find child node of kind "identifier" before parameter_list
            for i in 0..node.child_count() {
                if let Some(child) = node.child(i) {
                    if child.kind() == "identifier" {
                        return Some(child);
                    }
                }
            }
            None
        }) {
            if let Ok(name) = name_node.utf8_text(source.as_bytes()) {
                if !name.is_empty() && seen.insert(name.to_string()) {
                    let sig = extract_param_signature(node, source);
                    items.push(CompletionItem {
                        label: name.to_string(),
                        kind: Some(CompletionItemKind::FUNCTION),
                        detail: Some(format!("function {}{}", name, sig)),
                        insert_text: Some(format!("{}($0)", name)),
                        insert_text_format: Some(InsertTextFormat::SNIPPET),
                        sort_text: Some(format!("0_{}", name)),
                        ..Default::default()
                    });
                }
            }
        }

        // If the cursor is inside this function body, extract its parameters as variables!
        let start_row = node.start_position().row;
        let end_row = node.end_position().row;
        if cursor_line >= start_row && cursor_line <= end_row {
            extract_parameters(node, source, items, seen);
        }
    }

    // Collect variable declarations before cursor
    if node.start_position().row <= cursor_line {
        match kind {
            "variable_declaration" => {
                for i in 0..node.child_count() {
                    if let Some(child) = node.child(i) {
                        if child.kind() == "identifier" {
                            add_variable(child, source, "Local Variable", items, seen);
                        }
                    }
                }
            }
            "assignment_expression" => {
                // The target being assigned to is the first child
                if let Some(target) = node.child(0) {
                    if target.kind() == "identifier" {
                        add_variable(target, source, "Variable", items, seen);
                    }
                }
            }
            "for_statement" => {
                // Iterator loop: for (item in list)
                for i in 0..node.child_count() {
                    if let Some(child) = node.child(i) {
                        if child.kind() == "identifier" {
                            add_variable(child, source, "Loop Variable", items, seen);
                        }
                    }
                }
            }
            "foreach_statement" => {
                // foreach item (list)
                for i in 0..node.child_count() {
                    if let Some(child) = node.child(i) {
                        if child.kind() == "identifier" {
                            add_variable(child, source, "Foreach Variable", items, seen);
                        }
                    }
                }
            }
            "catch_clause" => {
                // catch (Exception e)
                for i in 0..node.child_count() {
                    if let Some(child) = node.child(i) {
                        if child.kind() == "identifier" {
                            // If previous sibling was type or Exception, this is the variable
                            add_variable(child, source, "Exception Variable", items, seen);
                        }
                    }
                }
            }
            _ => {}
        }
    }

    // Traverse children
    let count = node.child_count();
    for i in 0..count {
        if let Some(child) = node.child(i) {
            collect_ast_symbols(child, source, cursor_line, items, seen);
        }
    }
}

fn add_variable(
    node: Node,
    source: &str,
    detail: &str,
    items: &mut Vec<CompletionItem>,
    seen: &mut HashSet<String>,
) {
    if let Ok(name) = node.utf8_text(source.as_bytes()) {
        let name_str = name.trim();
        if !name_str.is_empty() && seen.insert(name_str.to_string()) {
            items.push(CompletionItem {
                label: name_str.to_string(),
                kind: Some(CompletionItemKind::VARIABLE),
                detail: Some(detail.to_string()),
                insert_text: Some(name_str.to_string()),
                insert_text_format: Some(InsertTextFormat::PLAIN_TEXT),
                sort_text: Some(format!("1_{}", name_str)),
                ..Default::default()
            });
        }
    }
}

fn extract_parameters(
    func_node: Node,
    source: &str,
    items: &mut Vec<CompletionItem>,
    seen: &mut HashSet<String>,
) {
    for i in 0..func_node.child_count() {
        if let Some(child) = func_node.child(i) {
            if child.kind() == "parameter_list" {
                for p_idx in 0..child.child_count() {
                    if let Some(param) = child.child(p_idx) {
                        if param.kind() == "parameter" {
                            for id_idx in 0..param.child_count() {
                                if let Some(id_node) = param.child(id_idx) {
                                    if id_node.kind() == "identifier" {
                                        add_variable(id_node, source, "Parameter", items, seen);
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

fn extract_param_signature(func_node: Node, source: &str) -> String {
    for i in 0..func_node.child_count() {
        if let Some(child) = func_node.child(i) {
            if child.kind() == "parameter_list" {
                if let Ok(text) = child.utf8_text(source.as_bytes()) {
                    return format!("({})", text);
                }
            }
        }
    }
    "()".to_string()
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser::parse_pnuts;

    #[test]
    fn test_keywords_and_snippets_available() {
        let code = "a = 1;\n";
        let tree = parse_pnuts(code, None).unwrap();
        let completions = provide_completions(Some(&tree), code, Position { line: 1, character: 0 });

        let labels: Vec<&str> = completions.iter().map(|c| c.label.as_str()).collect();

        // Check keywords
        assert!(labels.contains(&"function"));
        assert!(labels.contains(&"if"));
        assert!(labels.contains(&"foreach"));
        assert!(labels.contains(&"while"));
        assert!(labels.contains(&"return"));

        // Check snippets
        assert!(labels.contains(&"foreach (in)"));
        assert!(labels.contains(&"if-else"));
        assert!(labels.contains(&"println-flush"));

        // Check standard Java classes
        assert!(labels.contains(&"List"));
        assert!(labels.contains(&"ArrayList"));
        assert!(labels.contains(&"Map"));
        assert!(labels.contains(&"HashMap"));
    }

    #[test]
    fn test_ast_local_variable_extraction() {
        let code = r#"
            totalPrice = 1500;
            userRole = "admin";
            println(totalPrice);
        "#;
        let tree = parse_pnuts(code, None).unwrap();
        let completions = provide_completions(Some(&tree), code, Position { line: 3, character: 0 });

        let var_items: Vec<&CompletionItem> = completions
            .iter()
            .filter(|c| c.kind == Some(CompletionItemKind::VARIABLE))
            .collect();

        let var_labels: Vec<&str> = var_items.iter().map(|c| c.label.as_str()).collect();
        assert!(var_labels.contains(&"totalPrice"));
        assert!(var_labels.contains(&"userRole"));
    }

    #[test]
    fn test_ast_function_and_parameter_extraction() {
        let code = r#"
            function processOrder(orderId, customerEmail) {
                isValid = true;
                return isValid;
            }
        "#;
        let tree = parse_pnuts(code, None).unwrap();

        // Cursor inside function body (line 2)
        let completions_inside = provide_completions(Some(&tree), code, Position { line: 2, character: 10 });
        let labels_inside: Vec<&str> = completions_inside.iter().map(|c| c.label.as_str()).collect();

        assert!(labels_inside.contains(&"processOrder"));
        assert!(labels_inside.contains(&"orderId"));
        assert!(labels_inside.contains(&"customerEmail"));
    }
}
