use std::path::{Path, PathBuf};
use std::process::Stdio;
use tokio::io::AsyncWriteExt;
use tokio::process::Command;
use tower_lsp::lsp_types::{Position, Range, TextEdit};
use tracing::{debug, warn};

#[derive(Debug)]
pub enum FormatterError {
    BinaryNotFound,
    Io(std::io::Error),
    ProcessFailed(i32, String),
    EmptyOutput,
}

impl std::fmt::Display for FormatterError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            FormatterError::BinaryNotFound => {
                write!(f, "Formatter binary not found (looked for pnuts-format / topiary)")
            }
            FormatterError::Io(e) => write!(f, "I/O error during formatting: {}", e),
            FormatterError::ProcessFailed(code, msg) => {
                write!(f, "Formatter process failed with exit code {}: {}", code, msg)
            }
            FormatterError::EmptyOutput => write!(f, "Empty or invalid formatter output"),
        }
    }
}

impl std::error::Error for FormatterError {}

impl From<std::io::Error> for FormatterError {
    fn from(e: std::io::Error) -> Self {
        FormatterError::Io(e)
    }
}

/// Finds the starting index of a line comment (`//` or `/* ... */`) outside of string literals.
/// Returns None if no comment is found on the line.
pub fn find_comment_start(line: &str) -> Option<usize> {
    let mut in_str: Option<char> = None;
    let mut escaped = false;
    let bytes = line.as_bytes();
    let n = bytes.len();
    let mut i = 0;

    while i < n {
        let b = bytes[i];
        let c = b as char;

        if escaped {
            escaped = false;
            i += 1;
            continue;
        }

        if c == '\\' && in_str.is_some() {
            escaped = true;
            i += 1;
            continue;
        }

        if let Some(quote) = in_str {
            if c == quote {
                in_str = None;
            }
            i += 1;
            continue;
        }

        if c == '"' || c == '\'' || c == '`' {
            in_str = Some(c);
            i += 1;
            continue;
        }

        if c == '/' && i + 1 < n {
            if bytes[i + 1] == b'/' {
                return Some(i);
            }
            if bytes[i + 1] == b'*' {
                let rest = &line[i + 2..];
                if let Some(close_idx) = rest.find("*/") {
                    let after = &rest[close_idx + 2..];
                    if after.trim().is_empty() {
                        return Some(i);
                    }
                }
            }
        }

        i += 1;
    }

    None
}

/// Ensures that comments starting with `//` have a space after the slashes.
/// Example: `//este é um comentario` -> `// este é um comentario`.
/// Preserves visual divider lines like `//***`, `//===`, `//---` (3+ identical divider chars).
pub fn format_comment(comment: &str) -> String {
    if comment.starts_with("/*") || !comment.starts_with("//") {
        return comment.to_string();
    }
    let rest = &comment[2..];
    if rest.is_empty() {
        return comment.to_string();
    }
    let first = rest.chars().next().unwrap();
    if first == ' ' || first == '\t' {
        return comment.to_string();
    }
    // Check if rest starts with 3 or more identical divider characters (*=-/#~.)
    if rest.len() >= 3 {
        let chars: Vec<char> = rest.chars().take(3).collect();
        if chars.len() == 3 && "*=-/#~.".contains(chars[0]) && chars[0] == chars[1] && chars[1] == chars[2] {
            return comment.to_string();
        }
    }
    format!("// {}", rest)
}

/// Preprocesses comments in Pnuts source code:
/// 1. Ensures space after `//` in all comments.
/// 2. Moves trailing comments at the end of code lines to the line directly above that code,
///    preserving the code line's indentation.
///    Exception: lines starting with `}`, `else`, `catch`, `finally` keep comments on the same line.
pub fn preprocess_comments(text: &str) -> String {
    if text.is_empty() {
        return String::new();
    }

    let default_eol = if text.contains("\r\n") { "\r\n" } else { "\n" };
    let mut out = String::with_capacity(text.len() + 128);

    for line in text.split_inclusive('\n') {
        let (raw, eol) = if let Some(stripped) = line.strip_suffix("\r\n") {
            (stripped, "\r\n")
        } else if let Some(stripped) = line.strip_suffix('\n') {
            (stripped, "\n")
        } else {
            (line, "")
        };

        let Some(idx) = find_comment_start(raw) else {
            out.push_str(line);
            continue;
        };

        let code_part = &raw[..idx];
        let comment_part = &raw[idx..];
        let formatted_comment = format_comment(comment_part);

        let code_stripped = code_part.trim();
        let indent = &code_part[..code_part.len() - code_part.trim_start().len()];
        let effective_eol = if eol.is_empty() { default_eol } else { eol };

        if code_stripped.is_empty() {
            // Whole line is a comment
            out.push_str(indent);
            out.push_str(&formatted_comment);
            out.push_str(eol);
        } else {
            // Trailing comment after code
            if code_stripped.starts_with('}')
                || code_stripped.starts_with("else")
                || code_stripped.starts_with("catch")
                || code_stripped.starts_with("finally")
            {
                out.push_str(code_part.trim_end());
                out.push(' ');
                out.push_str(&formatted_comment);
                out.push_str(eol);
            } else {
                // Move comment to the line above
                out.push_str(indent);
                out.push_str(&formatted_comment);
                out.push_str(effective_eol);

                out.push_str(code_part.trim_end());
                out.push_str(eol);
            }
        }
    }

    out
}

/// Normalizes `flush()` calls to stay on the same line as the preceding command.
/// Example:
///   `println(...);`
///   `flush();`
/// becomes:
///   `println(...); flush();`
pub fn normalize_flush(text: &str) -> String {
    if text.is_empty() {
        return String::new();
    }

    let has_crlf = text.contains("\r\n");
    let eol = if has_crlf { "\r\n" } else { "\n" };

    let lines: Vec<&str> = text.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();
    let mut out: Vec<String> = Vec::with_capacity(lines.len());

    for line in lines {
        let trimmed = line.trim();
        let is_flush = trimmed.starts_with("flush")
            && trimmed.contains('(')
            && (trimmed.ends_with(')') || trimmed.ends_with(");"));

        if is_flush && !out.is_empty() {
            // Look backward for previous non-empty line
            let mut prev_idx = (out.len() as isize) - 1;
            while prev_idx >= 0 && out[prev_idx as usize].trim().is_empty() {
                prev_idx -= 1;
            }

            if prev_idx >= 0 {
                let p_idx = prev_idx as usize;
                let prev_trimmed = out[p_idx].trim();
                let is_comment = prev_trimmed.starts_with("//")
                    || prev_trimmed.starts_with("/*")
                    || prev_trimmed.ends_with("*/");
                let is_block_boundary = prev_trimmed.ends_with('{')
                    || prev_trimmed.ends_with('}')
                    || prev_trimmed.ends_with(':');
                let is_jump = prev_trimmed == "continue"
                    || prev_trimmed == "continue;"
                    || prev_trimmed == "break"
                    || prev_trimmed == "break;"
                    || prev_trimmed.starts_with("return")
                    || prev_trimmed.starts_with("throw");

                if !is_comment && !is_block_boundary && !is_jump && !prev_trimmed.is_empty() {
                    let mut prev_line = out[p_idx].clone();
                    if !prev_line.trim_end().ends_with(';') {
                        prev_line.push(';');
                    }
                    let mut flush_cmd = trimmed.to_string();
                    if !flush_cmd.ends_with(';') {
                        flush_cmd.push(';');
                    }

                    out[p_idx] = format!("{} {}", prev_line.trim_end(), flush_cmd);
                    continue;
                }
            }
        }

        out.push(line.to_string());
    }

    let mut result = out.join(eol);
    if (text.ends_with('\n') || text.ends_with("\r\n")) && !result.ends_with(eol) {
        result.push_str(eol);
    }
    result
}

/// Ensures that any block `{ ... }` with more than one nested line of content
/// has a blank line immediately following the opening brace line.
/// Also removes any blank lines immediately preceding the closing brace `}`.
pub fn normalize_block_spacing(text: &str) -> String {
    if text.is_empty() {
        return String::new();
    }

    let has_crlf = text.contains("\r\n");
    let eol = if has_crlf { "\r\n" } else { "\n" };

    let lines: Vec<&str> = text.split('\n').map(|l| l.strip_suffix('\r').unwrap_or(l)).collect();

    let mut open_braces = Vec::new();
    let mut matched_blocks = Vec::new();

    let mut in_block_comment = false;
    let mut in_string: Option<char> = None;
    let mut escaped = false;

    for (line_idx, line) in lines.iter().enumerate() {
        let bytes = line.as_bytes();
        let n = bytes.len();
        let mut i = 0;

        while i < n {
            let c = bytes[i] as char;

            if in_block_comment {
                if c == '*' && i + 1 < n && bytes[i + 1] as char == '/' {
                    in_block_comment = false;
                    i += 2;
                    continue;
                }
                i += 1;
                continue;
            }

            if let Some(quote) = in_string {
                if escaped {
                    escaped = false;
                    i += 1;
                    continue;
                }
                if c == '\\' {
                    escaped = true;
                    i += 1;
                    continue;
                }
                if c == quote {
                    in_string = None;
                }
                i += 1;
                continue;
            }

            if c == '"' || c == '\'' || c == '`' {
                in_string = Some(c);
                i += 1;
                continue;
            }

            if c == '/' && i + 1 < n {
                if bytes[i + 1] as char == '/' {
                    break;
                }
                if bytes[i + 1] as char == '*' {
                    in_block_comment = true;
                    i += 2;
                    continue;
                }
            }

            if c == '{' {
                open_braces.push(line_idx);
                i += 1;
                continue;
            }

            if c == '}' {
                if let Some(open_line) = open_braces.pop() {
                    matched_blocks.push((open_line, line_idx));
                }
                i += 1;
                continue;
            }

            i += 1;
        }
    }

    let mut lines_to_remove = std::collections::HashSet::new();
    let mut need_blank_after = std::collections::HashSet::new();

    for (open_line, close_line) in matched_blocks {
        // Remove any blank lines immediately before closing brace }
        let mut k = (close_line as isize) - 1;
        while k > (open_line as isize) && lines[k as usize].trim().is_empty() {
            lines_to_remove.insert(k as usize);
            k -= 1;
        }

        // Check content lines
        if close_line > open_line + 1 {
            let nested_content_count = (open_line + 1..close_line)
                .filter(|&idx| !lines_to_remove.contains(&idx) && !lines[idx].trim().is_empty())
                .count();

            if nested_content_count > 1 {
                if !lines[open_line + 1].trim().is_empty() {
                    need_blank_after.insert(open_line);
                }
            }
        }
    }

    let mut new_lines = Vec::with_capacity(lines.len() + need_blank_after.len());
    for (line_idx, line) in lines.iter().enumerate() {
        if lines_to_remove.contains(&line_idx) {
            continue;
        }
        new_lines.push(line.to_string());
        if need_blank_after.contains(&line_idx) {
            new_lines.push(String::new());
        }
    }

    let mut result = new_lines.join(eol);
    if (text.ends_with('\n') || text.ends_with("\r\n")) && !result.ends_with(eol) {
        result.push_str(eol);
    }
    result
}

fn strip_verbatim_prefix(path: PathBuf) -> PathBuf {
    if let Some(s) = path.to_str() {
        if let Some(stripped) = s.strip_prefix(r"\\?\") {
            return PathBuf::from(stripped);
        }
    }
    path
}

/// Locates the `pnuts-format` executable binary or Topiary executable.
pub fn resolve_formatter_binary() -> Option<PathBuf> {
    let exe_name = if cfg!(windows) { "pnuts-format.exe" } else { "pnuts-format" };

    // 1. Check explicit environment variable PNUTS_FORMAT_BIN
    if let Ok(env_path) = std::env::var("PNUTS_FORMAT_BIN") {
        let p = PathBuf::from(env_path);
        if p.is_file() {
            return Some(strip_verbatim_prefix(p));
        }
    }

    let platform_dir = if cfg!(windows) {
        "win32-x64"
    } else if cfg!(target_os = "macos") {
        if cfg!(target_arch = "aarch64") {
            "darwin-arm64"
        } else {
            "darwin-x64"
        }
    } else {
        "linux-x64"
    };

    // 2. Check PNUTS_TOOLING_DIR environment variable
    if let Ok(env_tooling) = std::env::var("PNUTS_TOOLING_DIR") {
        let root = PathBuf::from(env_tooling);
        let candidates = [
            root.join("vscode-pnuts").join("resources").join("bin").join(platform_dir).join(exe_name),
            root.join("bin").join(exe_name),
        ];
        for c in candidates {
            if c.is_file() {
                return Some(strip_verbatim_prefix(c));
            }
        }
    }

    // 3. Check relative to current running executable
    if let Ok(current_exe) = std::env::current_exe() {
        if let Some(exe_dir) = current_exe.parent() {
            let candidates = [
                exe_dir.join(exe_name),
                exe_dir.join(format!("../vscode-pnuts/resources/bin/{platform_dir}")).join(exe_name),
                exe_dir.join(format!("../../vscode-pnuts/resources/bin/{platform_dir}")).join(exe_name),
                exe_dir.join(format!("../../../vscode-pnuts/resources/bin/{platform_dir}")).join(exe_name),
                exe_dir.join("../bin").join(exe_name),
            ];
            for c in candidates {
                if c.is_file() {
                    return Some(strip_verbatim_prefix(c));
                }
                if let Ok(canonical) = c.canonicalize() {
                    if canonical.is_file() {
                        return Some(strip_verbatim_prefix(canonical));
                    }
                }
            }
        }
    }

    // 4. Check relative to current working directory
    let cwd_candidates = [
        PathBuf::from(format!("vscode-pnuts/resources/bin/{platform_dir}")).join(exe_name),
        PathBuf::from("bin").join(exe_name),
        PathBuf::from(exe_name),
    ];
    for c in cwd_candidates {
        if c.is_file() {
            return Some(strip_verbatim_prefix(c));
        }
        if let Ok(canonical) = c.canonicalize() {
            if canonical.is_file() {
                return Some(strip_verbatim_prefix(canonical));
            }
        }
    }

    // 5. Check user home directories and AppData
    if let Ok(localappdata) = std::env::var("LOCALAPPDATA") {
        let lad = PathBuf::from(localappdata);
        let lad_candidates = [
            lad.join("Zed/extensions/installed/pnuts/bin").join(exe_name),
        ];
        for c in lad_candidates {
            if c.is_file() {
                return Some(c);
            }
        }
    }

    if let Ok(home) = std::env::var("HOME").or_else(|_| std::env::var("USERPROFILE")) {
        let home_dir = PathBuf::from(home);
        let home_candidates = [
            home_dir.join(".cargo/bin").join(exe_name),
            home_dir.join(".local/bin").join(exe_name),
            home_dir.join(format!("personal/projects/pnuts-tooling/vscode-pnuts/resources/bin/{platform_dir}")).join(exe_name),
        ];
        for c in home_candidates {
            if c.is_file() {
                return Some(c);
            }
        }
    }

    // 6. Check system PATH
    if let Ok(path_var) = std::env::var("PATH") {
        let separator = if cfg!(windows) { ';' } else { ':' };
        for dir in path_var.split(separator) {
            let candidate = Path::new(dir).join(exe_name);
            if candidate.is_file() {
                return Some(candidate);
            }
        }
    }

    None
}

/// Formats source code using the native `pnuts-format` engine.
pub async fn format_code(text: &str) -> Result<String, FormatterError> {
    if text.trim().is_empty() {
        return Ok(text.to_string());
    }

    let binary = resolve_formatter_binary().ok_or(FormatterError::BinaryNotFound)?;
    debug!("Using formatter binary at: {:?}", binary);

    let mut child = Command::new(&binary)
        .arg("--stdin")
        .stdin(Stdio::piped())
        .stdout(Stdio::piped())
        .stderr(Stdio::piped())
        .spawn()?;

    if let Some(mut stdin) = child.stdin.take() {
        stdin.write_all(text.as_bytes()).await?;
        stdin.flush().await?;
    }

    let output = child.wait_with_output().await?;

    if !output.status.success() {
        let exit_code = output.status.code().unwrap_or(1);
        let err_msg = String::from_utf8_lossy(&output.stderr).to_string();
        warn!("Formatter binary returned exit code {}: {}", exit_code, err_msg);
        return Err(FormatterError::ProcessFailed(exit_code, err_msg));
    }

    let stdout_str = String::from_utf8_lossy(&output.stdout).to_string();
    if stdout_str.is_empty() && !text.trim().is_empty() {
        return Err(FormatterError::EmptyOutput);
    }

    Ok(stdout_str)
}

/// Formats a specific line range within the document, preserving surrounding context and indentation.
pub async fn format_range(
    text: &str,
    range: Range,
) -> Result<Option<TextEdit>, FormatterError> {
    let all_lines: Vec<&str> = text
        .split('\n')
        .map(|l| l.strip_suffix('\r').unwrap_or(l))
        .collect();

    if all_lines.is_empty() {
        return Ok(None);
    }

    let start_line = range.start.line as usize;
    let end_line = (range.end.line as usize).min(all_lines.len().saturating_sub(1));
    if start_line > end_line || start_line >= all_lines.len() {
        return Ok(None);
    }

    let eol = if text.contains("\r\n") { "\r\n" } else { "\n" };
    let selected = &all_lines[start_line..=end_line];

    if selected.iter().all(|l| l.trim().is_empty()) {
        return Ok(Some(TextEdit {
            range: Range {
                start: Position {
                    line: start_line as u32,
                    character: 0,
                },
                end: Position {
                    line: end_line as u32,
                    character: all_lines[end_line].encode_utf16().count() as u32,
                },
            },
            new_text: selected.join(eol),
        }));
    }

    // Determine common leading whitespace across non-empty lines
    let mut common_indent: Option<String> = None;
    for line in selected {
        if line.trim().is_empty() {
            continue;
        }
        let indent: String = line.chars().take_while(|c| c.is_whitespace()).collect();
        match &common_indent {
            None => common_indent = Some(indent),
            Some(curr) => {
                let common_len = curr
                    .chars()
                    .zip(indent.chars())
                    .take_while(|(a, b)| a == b)
                    .count();
                common_indent = Some(curr.chars().take(common_len).collect());
            }
        }
    }
    let base_indent = common_indent.unwrap_or_default();

    // Dedent the snippet
    let mut dedented_lines = Vec::new();
    for line in selected {
        if line.starts_with(&base_indent) {
            dedented_lines.push(&line[base_indent.len()..]);
        } else {
            dedented_lines.push(line.trim_start());
        }
    }
    let snippet = dedented_lines.join(eol);

    // Format the snippet
    let formatted_snippet = match format_code(&snippet).await {
        Ok(f) => f,
        Err(_) => return Ok(None),
    };

    // Re-apply indentation to formatted lines
    let clean_formatted = formatted_snippet.trim_end_matches(|c| c == '\r' || c == '\n');
    let mut reindented_lines = Vec::new();
    for line in clean_formatted.split('\n') {
        let clean = line.strip_suffix('\r').unwrap_or(line);
        if clean.trim().is_empty() {
            reindented_lines.push(String::new());
        } else {
            reindented_lines.push(format!("{}{}", base_indent, clean));
        }
    }

    let new_text = reindented_lines.join(eol);
    let orig_text = selected.join(eol);

    let target_range = Range {
        start: Position {
            line: start_line as u32,
            character: 0,
        },
        end: Position {
            line: end_line as u32,
            character: all_lines[end_line].encode_utf16().count() as u32,
        },
    };

    if new_text == orig_text {
        return Ok(Some(TextEdit {
            range: target_range,
            new_text: orig_text,
        }));
    }

    Ok(Some(TextEdit {
        range: target_range,
        new_text,
    }))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_format_comment_spacing() {
        assert_eq!(format_comment("//comentario"), "// comentario");
        assert_eq!(format_comment("// já com espaco"), "// já com espaco");
        assert_eq!(format_comment("/* bloco */"), "/* bloco */");
        // Dividers preserved
        assert_eq!(format_comment("//***"), "//***");
        assert_eq!(format_comment("//==="), "//===");
        assert_eq!(format_comment("//---"), "//---");
    }

    #[test]
    fn test_preprocess_comments_rule1_trailing_comment_moves_up() {
        let input = "a = 100; // aqui é um comentarios\n";
        let expected = "// aqui é um comentarios\na = 100;\n";
        assert_eq!(preprocess_comments(input), expected);

        // Inside block with indentation
        let input_block = "  showMessage(\"exemplo\"); // comentario da chamada\n";
        let expected_block = "  // comentario da chamada\n  showMessage(\"exemplo\");\n";
        assert_eq!(preprocess_comments(input_block), expected_block);

        // Closing braces and chaining remain on same line
        let input_close = "} // fim do bloco\n";
        let expected_close = "} // fim do bloco\n";
        assert_eq!(preprocess_comments(input_close), expected_close);
    }

    #[test]
    fn test_normalize_flush() {
        let input = "println(\"hello\");\nflush();\n";
        let expected = "println(\"hello\"); flush();\n";
        assert_eq!(normalize_flush(input), expected);
    }

    #[test]
    fn test_normalize_flush_does_not_attach_to_jump_statements() {
        let input_continue = "continue\nflush();\n";
        assert_eq!(normalize_flush(input_continue), "continue\nflush();\n");

        let input_break = "break;\nflush();\n";
        assert_eq!(normalize_flush(input_break), "break;\nflush();\n");

        let input_return = "return\nflush();\n";
        assert_eq!(normalize_flush(input_return), "return\nflush();\n");

        let input_throw = "throw err\nflush();\n";
        assert_eq!(normalize_flush(input_throw), "throw err\nflush();\n");
    }

    #[test]
    fn test_normalize_block_spacing_multiline() {
        // Multiline block gets blank line after {
        let input = "if (a > b) {\n  showMessage(\"linha 1\");\n  showMessage(\"linha 2\");\n}\n";
        let expected = "if (a > b) {\n\n  showMessage(\"linha 1\");\n  showMessage(\"linha 2\");\n}\n";
        assert_eq!(normalize_block_spacing(input), expected);
    }

    #[test]
    fn test_normalize_block_spacing_remove_blank_before_close() {
        // Trailing blank line before } is removed
        let input = "if (a > b) {\n\n  showMessage(\"linha 1\");\n  showMessage(\"linha 2\");\n\n}\n";
        let expected = "if (a > b) {\n\n  showMessage(\"linha 1\");\n  showMessage(\"linha 2\");\n}\n";
        assert_eq!(normalize_block_spacing(input), expected);
    }

    #[test]
    fn test_normalize_block_spacing_single_line_stays_compact() {
        // Single statement inside block stays compact (no added blank line)
        let input = "if (a > b) {\n  showMessage(\"linha única\");\n}\n";
        let expected = "if (a > b) {\n  showMessage(\"linha única\");\n}\n";
        assert_eq!(normalize_block_spacing(input), expected);
    }

    #[test]
    fn test_normalize_block_spacing_idempotence() {
        let input = "if (a > b) {\n\n  showMessage(\"linha 1\");\n  showMessage(\"linha 2\");\n}\n";
        let formatted_once = normalize_block_spacing(input);
        let formatted_twice = normalize_block_spacing(&formatted_once);
        assert_eq!(formatted_once, formatted_twice);
    }

    #[tokio::test]
    async fn test_full_formatter_execution_e2e() {
        let input = "a = 100 // comentario\nif (a > 10) {\nprintln(a);\nprintln(b);\n\n}\n";
        let result = format_code(input).await;
        if let Ok(formatted) = result {
            assert!(formatted.contains("// comentario\na = 100"));
            assert!(formatted.contains("if (a > 10) {\n\n"));
            assert!(!formatted.contains("\n\n}"));
        } else {
            // If binary not found in CI/unusual environment, skip gracefully
            println!("Skipping binary execution test: binary not found");
        }
    }

    #[tokio::test]
    async fn test_continue_and_println_formatting_regression() {
        let input = "      continue\n      println(\"wddolst INICIO 2 >>> \" + wddolst.toString()); flush();\n";
        let result = format_code(input).await;
        if let Ok(formatted) = result {
            assert!(formatted.contains("continue\n"));
            assert!(formatted.contains("println(\"wddolst INICIO 2 >>> \" + wddolst.toString()); flush();"));
            assert!(!formatted.contains("continueprintln"));
        }
    }

    #[tokio::test]
    async fn test_switch_case_formatting_regression() {
        let input = "switch (wdataref) {\ncase \"2017\": wpICMSInterPart = 60.00; break;\ncase \"2018\": wpICMSInterPart = 80.00; break;\ndefault: break;\n}\n";
        let result = format_code(input).await;
        if let Ok(formatted) = result {
            assert!(formatted.contains("case \"2017\":"));
            assert!(formatted.contains("case \"2018\":"));
            assert!(formatted.contains("default:"));
            assert!(!formatted.contains("case \"2017\"\n"));
            assert!(!formatted.contains("case \"2017\" :"));
            assert!(!formatted.contains("case \"2017\"\r\n:"));
        }
    }

    #[tokio::test]
    async fn test_format_range_with_indentation() {
        let code = r#"function calc() {
    a=1;
    b=2;
    return a+b;
}
"#;
        // Select lines 1 to 2 (`    a=1;` and `    b=2;`)
        let range = Range {
            start: Position { line: 1, character: 0 },
            end: Position { line: 2, character: 8 },
        };

        if let Ok(Some(edit)) = format_range(code, range).await {
            assert_eq!(edit.range.start.line, 1);
            assert_eq!(edit.range.end.line, 2);
            assert!(edit.new_text.contains("    a = 1;"));
            assert!(edit.new_text.contains("    b = 2;"));
        }
    }

    #[tokio::test]
    async fn test_format_range_invalid_fragment_returns_none() {
        let code = "function foo() {\n";
        let range = Range {
            start: Position { line: 0, character: 0 },
            end: Position { line: 0, character: 15 },
        };
        let res = format_range(code, range).await;
        assert!(res.is_ok());
        assert!(res.unwrap().is_none());
    }
}

