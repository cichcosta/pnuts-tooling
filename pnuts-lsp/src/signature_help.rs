use tower_lsp::lsp_types::*;
use tree_sitter::{Node, Tree};

use crate::completion::{BUILTIN_FUNCTIONS, COMMON_METHODS};
use crate::navigation::position_to_byte_offset;

/// Represents the call context determined at cursor position.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct CallContext {
    /// The raw callee expression before '(' (e.g. "getRows", "MConnection::getMCursor", "m.idoc.trim").
    pub callee: String,
    /// The base function/method name (e.g. "getRows", "getMCursor", "trim").
    pub function_name: String,
    /// 0-indexed parameter position the cursor is currently at.
    pub active_parameter: usize,
}

/// Computes SignatureHelp for the call expression at the given position.
pub fn provide_signature_help(
    tree: Option<&Tree>,
    source: &str,
    position: Position,
) -> Option<SignatureHelp> {
    let offset = position_to_byte_offset(source, position)?;
    let context = find_call_context(source, offset)?;

    let mut signatures = Vec::new();

    // 1. First priority: Check local function or method in AST if tree is available
    if let Some(t) = tree {
        if let Some(local_sig) = find_local_function_signature(t, source, &context.function_name, context.active_parameter) {
            signatures.push(local_sig);
        }
    }

    // 2. Second priority: Built-in & Domain Functions matching function_name
    let is_method_call = context.callee.contains('.') || context.callee.contains("::");

    let mut builtin_sigs = Vec::new();
    for &(name, sig, _snippet, doc) in BUILTIN_FUNCTIONS {
        if name == context.function_name {
            builtin_sigs.push(build_signature_info(name, sig, doc, context.active_parameter));
        }
    }

    // 3. Third priority: Common Java / Object methods
    let mut method_sigs = Vec::new();
    for &(name, sig, _snippet, doc) in COMMON_METHODS {
        if name == context.function_name {
            method_sigs.push(build_signature_info(name, sig, doc, context.active_parameter));
        }
    }

    if is_method_call {
        // If called as method (e.g. obj.trim() or MConnection::getMCursor()), prioritize methods / qualified builtins
        signatures.extend(method_sigs);
        signatures.extend(builtin_sigs);
    } else {
        // If called as regular function, prioritize built-ins
        signatures.extend(builtin_sigs);
        signatures.extend(method_sigs);
    }

    if signatures.is_empty() {
        return None;
    }

    Some(SignatureHelp {
        signatures,
        active_signature: Some(0),
        active_parameter: Some(context.active_parameter as u32),
    })
}

/// Identifies the enclosing call expression and active parameter index at `offset`.
pub fn find_call_context(source: &str, offset: usize) -> Option<CallContext> {
    let masked = mask_comments_and_strings(source, offset)?;
    let bytes = masked.as_bytes();

    let mut paren_depth: isize = 0;
    let mut bracket_depth: usize = 0;
    let mut brace_depth: usize = 0;
    let mut comma_count: usize = 0;

    let mut i = bytes.len();
    while i > 0 {
        i -= 1;
        let b = bytes[i];
        match b {
            b'}' => brace_depth += 1,
            b'{' => {
                if brace_depth > 0 {
                    brace_depth -= 1;
                }
            }
            b']' => bracket_depth += 1,
            b'[' => {
                if bracket_depth > 0 {
                    bracket_depth -= 1;
                }
            }
            b')' => paren_depth += 1,
            b',' => {
                if paren_depth == 0 && bracket_depth == 0 && brace_depth == 0 {
                    comma_count += 1;
                }
            }
            b'(' => {
                if paren_depth > 0 {
                    paren_depth -= 1;
                } else if bracket_depth == 0 && brace_depth == 0 {
                    // Possible opening paren of the active call
                    if let Some((callee, func_name)) = extract_callee_before(&masked, i) {
                        return Some(CallContext {
                            callee,
                            function_name: func_name,
                            active_parameter: comma_count,
                        });
                    }
                    // If no identifier preceded '(', it was a grouping parenthesis (e.g. (a + b));
                    // continue searching backwards.
                }
            }
            _ => {}
        }
    }

    None
}

/// Replaces string contents and comments with spaces up to `offset`.
/// Returns `None` if `offset` is inside a comment (where signature help is disabled).
fn mask_comments_and_strings(source: &str, offset: usize) -> Option<String> {
    let bytes = source.as_bytes();
    let limit = offset.min(bytes.len());
    let mut masked = String::with_capacity(limit);
    let mut i = 0;

    enum State {
        Code,
        LineComment,
        BlockComment,
        SingleQuoteString,
        DoubleQuoteString,
        TripleSingleString,
        TripleDoubleString,
    }

    let mut state = State::Code;

    while i < limit {
        match state {
            State::Code => {
                if i + 2 < limit && &bytes[i..i + 3] == b"\"\"\"" {
                    state = State::TripleDoubleString;
                    masked.push_str("\"\"\"");
                    i += 3;
                } else if i + 2 < limit && &bytes[i..i + 3] == b"'''" {
                    state = State::TripleSingleString;
                    masked.push_str("'''");
                    i += 3;
                } else if i + 1 < limit && bytes[i] == b'/' && bytes[i + 1] == b'/' {
                    state = State::LineComment;
                    masked.push_str("  ");
                    i += 2;
                } else if i + 1 < limit && bytes[i] == b'/' && bytes[i + 1] == b'*' {
                    state = State::BlockComment;
                    masked.push_str("  ");
                    i += 2;
                } else if bytes[i] == b'"' {
                    state = State::DoubleQuoteString;
                    masked.push('"');
                    i += 1;
                } else if bytes[i] == b'\'' {
                    state = State::SingleQuoteString;
                    masked.push('\'');
                    i += 1;
                } else {
                    masked.push(bytes[i] as char);
                    i += 1;
                }
            }
            State::LineComment => {
                if bytes[i] == b'\n' {
                    state = State::Code;
                    masked.push('\n');
                } else {
                    masked.push(' ');
                }
                i += 1;
            }
            State::BlockComment => {
                if i + 1 < limit && bytes[i] == b'*' && bytes[i + 1] == b'/' {
                    state = State::Code;
                    masked.push_str("  ");
                    i += 2;
                } else if bytes[i] == b'\n' {
                    masked.push('\n');
                    i += 1;
                } else {
                    masked.push(' ');
                    i += 1;
                }
            }
            State::SingleQuoteString => {
                if bytes[i] == b'\\' && i + 1 < limit {
                    masked.push_str("  ");
                    i += 2;
                } else if bytes[i] == b'\'' {
                    state = State::Code;
                    masked.push('\'');
                    i += 1;
                } else if bytes[i] == b'\n' {
                    state = State::Code;
                    masked.push('\n');
                    i += 1;
                } else {
                    masked.push(' ');
                    i += 1;
                }
            }
            State::DoubleQuoteString => {
                if bytes[i] == b'\\' && i + 1 < limit {
                    masked.push_str("  ");
                    i += 2;
                } else if bytes[i] == b'"' {
                    state = State::Code;
                    masked.push('"');
                    i += 1;
                } else if bytes[i] == b'\n' {
                    state = State::Code;
                    masked.push('\n');
                    i += 1;
                } else {
                    masked.push(' ');
                    i += 1;
                }
            }
            State::TripleSingleString => {
                if i + 2 < limit && &bytes[i..i + 3] == b"'''" {
                    state = State::Code;
                    masked.push_str("'''");
                    i += 3;
                } else if bytes[i] == b'\n' {
                    masked.push('\n');
                    i += 1;
                } else {
                    masked.push(' ');
                    i += 1;
                }
            }
            State::TripleDoubleString => {
                if i + 2 < limit && &bytes[i..i + 3] == b"\"\"\"" {
                    state = State::Code;
                    masked.push_str("\"\"\"");
                    i += 3;
                } else if bytes[i] == b'\n' {
                    masked.push('\n');
                    i += 1;
                } else {
                    masked.push(' ');
                    i += 1;
                }
            }
        }
    }

    // Do not trigger signature help if cursor is inside a comment
    match state {
        State::LineComment | State::BlockComment => None,
        _ => Some(masked),
    }
}

/// Inspects the text immediately preceding the opening `(` at `paren_idx`.
/// Returns `Some((full_callee, function_name))` if it corresponds to a function/method call.
fn extract_callee_before(source: &str, paren_idx: usize) -> Option<(String, String)> {
    let bytes = source.as_bytes();
    let mut idx = paren_idx;

    // 1. Skip whitespace backwards
    while idx > 0 && (bytes[idx - 1] == b' ' || bytes[idx - 1] == b'\t' || bytes[idx - 1] == b'\r' || bytes[idx - 1] == b'\n') {
        idx -= 1;
    }
    if idx == 0 {
        return None;
    }

    // 2. Read identifier backwards
    let end_ident = idx;
    while idx > 0 {
        let c = bytes[idx - 1];
        if c.is_ascii_alphanumeric() || c == b'_' || c == b'$' {
            idx -= 1;
        } else {
            break;
        }
    }
    let start_ident = idx;
    if start_ident == end_ident {
        return None; // No identifier before '('
    }

    let func_name = source[start_ident..end_ident].to_string();

    // 3. Exclude language control flow statements
    const CONTROL_KEYWORDS: &[&str] = &[
        "if", "while", "for", "foreach", "switch", "catch", "return", "throw", "assert",
    ];
    if CONTROL_KEYWORDS.contains(&func_name.as_str()) {
        return None;
    }

    // 4. Check if preceded by "function" or "def" keyword (i.e. this is a declaration, not a call)
    let mut before_ident = start_ident;
    while before_ident > 0 && (bytes[before_ident - 1] == b' ' || bytes[before_ident - 1] == b'\t' || bytes[before_ident - 1] == b'\r' || bytes[before_ident - 1] == b'\n') {
        before_ident -= 1;
    }
    let mut kw_start = before_ident;
    while kw_start > 0 && (bytes[kw_start - 1].is_ascii_alphanumeric() || bytes[kw_start - 1] == b'_') {
        kw_start -= 1;
    }
    if kw_start < before_ident {
        let prev_word = &source[kw_start..before_ident];
        if prev_word == "function" {
            return None;
        }
    }

    // 5. Check for qualifiers before ident: `::`, `.`, `?.`, `*.`
    let mut qual_end = start_ident;
    while qual_end > 0 && (bytes[qual_end - 1] == b' ' || bytes[qual_end - 1] == b'\t') {
        qual_end -= 1;
    }

    let mut callee_start = start_ident;
    if qual_end >= 2 && &bytes[qual_end - 2..qual_end] == b"::" {
        let mut q = qual_end - 2;
        while q > 0 && (bytes[q - 1] == b' ' || bytes[q - 1] == b'\t') {
            q -= 1;
        }
        while q > 0 && (bytes[q - 1].is_ascii_alphanumeric() || bytes[q - 1] == b'_' || bytes[q - 1] == b'$') {
            q -= 1;
        }
        callee_start = q;
    } else if qual_end >= 1 && bytes[qual_end - 1] == b'.' {
        let sep_len = if qual_end >= 2 && (bytes[qual_end - 2] == b'?' || bytes[qual_end - 2] == b'*') { 2 } else { 1 };
        let mut q = qual_end - sep_len;
        while q > 0 && (bytes[q - 1] == b' ' || bytes[q - 1] == b'\t') {
            q -= 1;
        }
        while q > 0 && (bytes[q - 1].is_ascii_alphanumeric() || bytes[q - 1] == b'_' || bytes[q - 1] == b'$') {
            q -= 1;
        }
        callee_start = q;
    }

    let full_callee = source[callee_start..end_ident]
        .replace(' ', "")
        .replace('\t', "");

    Some((full_callee, func_name))
}

/// Builds a `SignatureInformation` object from a name, signature string and documentation.
fn build_signature_info(
    name: &str,
    sig: &str,
    doc: &str,
    active_param: usize,
) -> SignatureInformation {
    let param_strings = parse_signature_params(sig);
    let params: Vec<ParameterInformation> = param_strings
        .into_iter()
        .map(|p| ParameterInformation {
            label: ParameterLabel::Simple(p),
            documentation: None,
        })
        .collect();

    SignatureInformation {
        label: format!("{}{}", name, sig),
        documentation: Some(Documentation::MarkupContent(MarkupContent {
            kind: MarkupKind::Markdown,
            value: doc.to_string(),
        })),
        parameters: Some(params),
        active_parameter: Some(active_param as u32),
    }
}

/// Parses comma-separated parameter names from a signature like `"(table, sqlQuery)"`.
pub fn parse_signature_params(sig: &str) -> Vec<String> {
    let s = sig.trim();
    let s = s.strip_prefix('(').unwrap_or(s);
    let s = s.strip_suffix(')').unwrap_or(s).trim();
    if s.is_empty() {
        return Vec::new();
    }
    s.split(',')
        .map(|p| p.trim().to_string())
        .filter(|p| !p.is_empty())
        .collect()
}

/// Recursively searches the AST for a local `function_definition` or `method_definition` matching `target_name`.
fn find_local_function_signature(
    tree: &Tree,
    source: &str,
    target_name: &str,
    active_param: usize,
) -> Option<SignatureInformation> {
    let root = tree.root_node();
    let func_node = find_func_node_recursive(root, source, target_name)?;

    // Extract parameters from parameter_list child
    let mut param_labels = Vec::new();
    for i in 0..func_node.child_count() {
        if let Some(child) = func_node.child(i) {
            if child.kind() == "parameter_list" {
                for p_idx in 0..child.child_count() {
                    if let Some(param) = child.child(p_idx) {
                        if param.kind() == "parameter" {
                            if let Ok(param_text) = param.utf8_text(source.as_bytes()) {
                                let clean = param_text.trim();
                                if !clean.is_empty() {
                                    param_labels.push(clean.to_string());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    let is_method = func_node.kind() == "method_definition";
    let sig_label = if is_method {
        format!("{}({})", target_name, param_labels.join(", "))
    } else {
        format!("function {}({})", target_name, param_labels.join(", "))
    };

    let params: Vec<ParameterInformation> = param_labels
        .into_iter()
        .map(|p| ParameterInformation {
            label: ParameterLabel::Simple(p),
            documentation: None,
        })
        .collect();

    // Extract documentation from preceding comment if available
    let start_row = func_node.start_position().row;
    let doc_comment = extract_preceding_doc_comment(source, start_row);
    let doc_text = match doc_comment {
        Some(comment) => format!("*(local definition)*\n\n{}", comment),
        None => "*(local definition)*".to_string(),
    };

    Some(SignatureInformation {
        label: sig_label,
        documentation: Some(Documentation::MarkupContent(MarkupContent {
            kind: MarkupKind::Markdown,
            value: doc_text,
        })),
        parameters: Some(params),
        active_parameter: Some(active_param as u32),
    })
}

/// Recursively searches for the `function_definition` or `method_definition` node with the given name.
fn find_func_node_recursive<'a>(
    node: Node<'a>,
    source: &str,
    target_name: &str,
) -> Option<Node<'a>> {
    if node.kind() == "function_definition" || node.kind() == "method_definition" {
        if let Some(name_id) = crate::navigation::get_name_identifier(node) {
            if let Ok(name) = name_id.utf8_text(source.as_bytes()) {
                if name == target_name {
                    return Some(node);
                }
            }
        }
    }

    for i in 0..node.child_count() {
        if let Some(child) = node.child(i) {
            if let Some(found) = find_func_node_recursive(child, source, target_name) {
                return Some(found);
            }
        }
    }

    None
}

/// Extracts any comment lines immediately preceding the line at `line_idx`.
fn extract_preceding_doc_comment(source: &str, line_idx: usize) -> Option<String> {
    if line_idx == 0 {
        return None;
    }

    let lines: Vec<&str> = source.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
    let mut comments = Vec::new();
    let mut cur = line_idx;

    while cur > 0 {
        cur -= 1;
        let line = lines.get(cur)?.trim();
        if line.starts_with("//") {
            let clean = line.strip_prefix("//").unwrap_or(line).trim();
            comments.push(clean.to_string());
        } else if line.ends_with("*/") || line.starts_with("/*") || line.starts_with('*') {
            let clean = line
                .trim_start_matches('/')
                .trim_start_matches('*')
                .trim_end_matches('/')
                .trim_end_matches('*')
                .trim();
            if !clean.is_empty() {
                comments.push(clean.to_string());
            }
        } else {
            break;
        }
    }

    if comments.is_empty() {
        None
    } else {
        comments.reverse();
        Some(comments.join("\n"))
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser::parse_pnuts;

    #[test]
    fn test_find_call_context_simple() {
        let code = "getRows(";
        let ctx = find_call_context(code, 8).expect("Context should be found");
        assert_eq!(ctx.function_name, "getRows");
        assert_eq!(ctx.active_parameter, 0);
    }

    #[test]
    fn test_find_call_context_first_arg_comma() {
        let code = "getRows(\"TABLE\", ";
        let ctx = find_call_context(code, code.len()).expect("Context should be found");
        assert_eq!(ctx.function_name, "getRows");
        assert_eq!(ctx.active_parameter, 1);
    }

    #[test]
    fn test_find_call_context_nested_call() {
        let code = "getRows(\"TABLE\", round(val, ";
        let ctx = find_call_context(code, code.len()).expect("Context should be found");
        assert_eq!(ctx.function_name, "round");
        assert_eq!(ctx.active_parameter, 1);
    }

    #[test]
    fn test_find_call_context_after_nested_call_closed() {
        let code = "getRows(\"TABLE\", round(val, 2), ";
        let ctx = find_call_context(code, code.len()).expect("Context should be found");
        assert_eq!(ctx.function_name, "getRows");
        assert_eq!(ctx.active_parameter, 2);
    }

    #[test]
    fn test_find_call_context_static_method() {
        let code = "MConnection::getMCursor(";
        let ctx = find_call_context(code, code.len()).expect("Context should be found");
        assert_eq!(ctx.function_name, "getMCursor");
        assert_eq!(ctx.callee, "MConnection::getMCursor");
        assert_eq!(ctx.active_parameter, 0);
    }

    #[test]
    fn test_find_call_context_ignore_strings_with_commas() {
        let code = "getRows(\"hello, world (foo)\", ";
        let ctx = find_call_context(code, code.len()).expect("Context should be found");
        assert_eq!(ctx.function_name, "getRows");
        assert_eq!(ctx.active_parameter, 1);
    }

    #[test]
    fn test_find_call_context_ignore_in_comments() {
        let code = "// getRows(";
        let ctx = find_call_context(code, code.len());
        assert!(ctx.is_none());
    }

    #[test]
    fn test_provide_signature_help_builtin() {
        let code = "getRows(\"TABLE\", ";
        let tree = parse_pnuts(code, None);
        let help = provide_signature_help(
            tree.as_ref(),
            code,
            Position {
                line: 0,
                character: code.len() as u32,
            },
        ).expect("SignatureHelp should be provided");

        assert_eq!(help.active_parameter, Some(1));
        assert!(!help.signatures.is_empty());
        let sig = &help.signatures[0];
        assert_eq!(sig.label, "getRows(table, sqlQuery)");
        let params = sig.parameters.as_ref().unwrap();
        assert_eq!(params.len(), 2);
        assert_eq!(params[0].label, ParameterLabel::Simple("table".to_string()));
        assert_eq!(params[1].label, ParameterLabel::Simple("sqlQuery".to_string()));
    }

    #[test]
    fn test_provide_signature_help_local_function() {
        let code = r#"
            // Realiza o cálculo de taxas
            function calcular(valor, taxa, aliquota) {
                return valor * (taxa + aliquota);
            }

            resultado = calcular(100.0, 
        "#;
        let tree = parse_pnuts(code, None);
        let pos = Position { line: 6, character: 40 };
        let help = provide_signature_help(tree.as_ref(), code, pos).expect("SignatureHelp should be provided");

        assert_eq!(help.active_parameter, Some(1));
        let sig = &help.signatures[0];
        assert_eq!(sig.label, "function calcular(valor, taxa, aliquota)");
        let params = sig.parameters.as_ref().unwrap();
        assert_eq!(params.len(), 3);
        assert_eq!(params[0].label, ParameterLabel::Simple("valor".to_string()));
        assert_eq!(params[1].label, ParameterLabel::Simple("taxa".to_string()));
        assert_eq!(params[2].label, ParameterLabel::Simple("aliquota".to_string()));
        assert!(sig.documentation.is_some());
    }
}
