#!/usr/bin/env bash
# ==============================================================================
# Script de Reconstrução Automática do zed-pnuts
# ==============================================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$REPO_ROOT"
echo "=== [1/5] Criando estrutura de pastas de zed-pnuts ==="
mkdir -p zed-pnuts/src
mkdir -p zed-pnuts/languages/pnuts
mkdir -p zed-pnuts/languages/groovy
mkdir -p zed-pnuts/icon_themes
mkdir -p zed-pnuts/icons
mkdir -p zed-pnuts/snippets
mkdir -p zed-pnuts/bin
mkdir -p .zed

echo "=== [2/5] Gerando Cargo.toml e extension.toml ==="
cat << 'EOF' > zed-pnuts/Cargo.toml
[package]
name = "pnuts-zed-extension"
version = "0.3.0"
edition = "2021"

[workspace]

[lib]
crate-type = ["cdylib"]
path = "src/lib.rs"

[dependencies]
zed_extension_api = "0.7.0"
EOF

cat << 'EOF' > zed-pnuts/extension.toml
id = "pnuts"
name = "Pnuts"
version = "0.3.0"
schema_version = 1
authors = ["Pnuts Tooling Team"]
description = "Pnuts language support for Zed, including syntax highlighting, code navigation, brackets, indents, and native Language Server Protocol (LSP) formatter."
repository = "https://github.com/pnuts-tooling/pnuts-tooling"
snippets = ["./snippets/pnuts.json"]

[lib]
kind = "Rust"
version = "0.7.0"

[grammars.pnuts]
repository = "https://github.com/pnuts-tooling/pnuts-tooling"
rev = "feature/pnuts-groovy-lsp"
path = "tree-sitter-pnuts"

[language_servers.pnuts-lsp]
name = "Pnuts Language Server"
languages = ["Pnuts", "Groovy"]
EOF

echo "=== [3/5] Gerando src/lib.rs da extensão Rust Zed ==="
cat << 'EOF' > zed-pnuts/src/lib.rs
use zed_extension_api::{self as zed, Command, Extension, LanguageServerId, Os, Result, Worktree};

struct PnutsExtension;

impl Extension for PnutsExtension {
    fn new() -> Self {
        PnutsExtension
    }

    fn language_server_command(
        &mut self,
        _language_server_id: &LanguageServerId,
        worktree: &Worktree,
    ) -> Result<Command> {
        let (os, _arch) = zed::current_platform();
        let is_windows = matches!(os, Os::Windows);

        let env_vars = worktree.shell_env();
        let get_env = |key: &str| -> Option<String> {
            env_vars
                .iter()
                .find(|(k, _)| k.eq_ignore_ascii_case(key))
                .map(|(_, v)| v.clone())
        };

        let binary = if is_windows {
            worktree
                .which("pnuts-lsp.exe")
                .or_else(|| worktree.which("pnuts-lsp"))
                .unwrap_or_else(|| {
                    let localappdata = get_env("LOCALAPPDATA")
                        .or_else(|| get_env("USERPROFILE").map(|u| format!("{}/AppData/Local", u.trim_end_matches(['/', '\\']))));

                    if let Some(lad) = localappdata {
                        let base = lad.trim_end_matches(['/', '\\']);
                        let root = worktree.root_path();
                        let trimmed_root = root.trim_end_matches(['/', '\\']);
                        if trimmed_root.ends_with("zed-pnuts") {
                            format!("{trimmed_root}/bin/pnuts-lsp.exe")
                        } else if trimmed_root.contains("pnuts-tooling") {
                            format!("{trimmed_root}/zed-pnuts/bin/pnuts-lsp.exe")
                        } else {
                            format!("{base}/Zed/extensions/installed/pnuts/bin/pnuts-lsp.exe")
                        }
                    } else {
                        "pnuts-lsp.exe".to_string()
                    }
                })
        } else {
            worktree
                .which("pnuts-lsp")
                .unwrap_or_else(|| {
                    let root = worktree.root_path();
                    let trimmed_root = root.trim_end_matches(['/', '\\']);
                    if trimmed_root.ends_with("zed-pnuts") {
                        format!("{trimmed_root}/bin/pnuts-lsp")
                    } else if trimmed_root.contains("pnuts-tooling") {
                        format!("{trimmed_root}/zed-pnuts/bin/pnuts-lsp")
                    } else {
                        let home = get_env("HOME");
                        let (os, arch) = zed::current_platform();
                        let is_mac = matches!(os, Os::Mac);

                        if let Some(h) = home {
                            let base = h.trim_end_matches(['/', '\\']);
                            if is_mac {
                                if matches!(arch, zed_extension_api::Architecture::Aarch64) {
                                    format!("{base}/Library/Application Support/Zed/extensions/pnuts-tooling/zed-pnuts/bin/pnuts-lsp-darwin-arm64")
                                } else {
                                    format!("{base}/Library/Application Support/Zed/extensions/pnuts-tooling/zed-pnuts/bin/pnuts-lsp-darwin-x64")
                                }
                            } else {
                                format!("{base}/.local/bin/pnuts-lsp")
                            }
                        } else {
                            "pnuts-lsp".to_string()
                        }
                    }
                })
        };

        let command_path = if is_windows {
            binary.replace('/', "\\")
        } else {
            binary
        };

        Ok(Command {
            command: command_path,
            args: vec![],
            env: Default::default(),
        })
    }
}

zed::register_extension!(PnutsExtension);
EOF

echo "=== [4/5] Gerando configurações de linguagem e Tree-sitter queries ==="
cat << 'EOF' > zed-pnuts/languages/pnuts/config.toml
name = "Pnuts"
grammar = "pnuts"
path_suffixes = ["pnuts", "pnut"]
line_comments = ["// "]
block_comment = ["/*", "*/"]
autoclose_before = ";:.,=}])>"
brackets = [
    { start = "{", end = "}", close = true, newline = true },
    { start = "[", end = "]", close = true, newline = true },
    { start = "(", end = ")", close = true, newline = true },
    { start = "\"", end = "\"", close = true, newline = false, not_in = ["string", "comment"] },
    { start = "'", end = "'", close = true, newline = false, not_in = ["string", "comment"] }
]
EOF

cat << 'EOF' > zed-pnuts/languages/groovy/config.toml
name = "Groovy"
grammar = "pnuts"
path_suffixes = ["groovy"]
line_comments = ["// "]
block_comment = ["/*", "*/"]
autoclose_before = ";:.,=}])>"
brackets = [
    { start = "{", end = "}", close = true, newline = true },
    { start = "[", end = "]", close = true, newline = true },
    { start = "(", end = ")", close = true, newline = true },
    { start = "\"", end = "\"", close = true, newline = false, not_in = ["string", "comment"] },
    { start = "'", end = "'", close = true, newline = false, not_in = ["string", "comment"] }
]
EOF

cat << 'EOF' > zed-pnuts/languages/pnuts/brackets.scm
("{" @open "}" @close)
("[" @open "]" @close)
("(" @open ")" @close)
EOF
cp zed-pnuts/languages/pnuts/brackets.scm zed-pnuts/languages/groovy/brackets.scm

cat << 'EOF' > zed-pnuts/languages/pnuts/indents.scm
[
  (block "}" @end)
  (class_body "}" @end)
  (switch_statement "}" @end)
  (array_literal "]" @end)
  (map_literal "]" @end)
  (map_literal "}" @end)
  (closure_expression "}" @end)
  (parenthesized_expression ")" @end)
] @indent

(switch_case) @indent
(switch_default) @indent
EOF
cp zed-pnuts/languages/pnuts/indents.scm zed-pnuts/languages/groovy/indents.scm

cat << 'EOF' > zed-pnuts/languages/pnuts/outline.scm
(function_definition
  (identifier) @name) @item

(method_definition
  (property_identifier) @name) @item

(class_definition
  (package_path) @name) @item

(package_statement
  (package_path) @name) @item
EOF
cp zed-pnuts/languages/pnuts/outline.scm zed-pnuts/languages/groovy/outline.scm

if [ -f "tree-sitter-pnuts/queries/highlights.scm" ]; then
    cp tree-sitter-pnuts/queries/highlights.scm zed-pnuts/languages/pnuts/highlights.scm
    cp tree-sitter-pnuts/queries/highlights.scm zed-pnuts/languages/groovy/highlights.scm
fi

if [ -f "vscode-pnuts/snippets/pnuts.json" ]; then
    cp vscode-pnuts/snippets/pnuts.json zed-pnuts/snippets/pnuts.json
fi

if [ -f "vscode-pnuts/icons/pnuts.svg" ]; then
    cp vscode-pnuts/icons/pnuts.svg zed-pnuts/icons/pnuts.svg
fi

echo "=== [5/5] Copiando binário nativo pnuts-lsp ==="
if [ -f "target/release/pnuts-lsp" ]; then
    cp target/release/pnuts-lsp zed-pnuts/bin/pnuts-lsp
    chmod +x zed-pnuts/bin/pnuts-lsp
elif [ -f "$HOME/.local/bin/pnuts-lsp" ]; then
    cp "$HOME/.local/bin/pnuts-lsp" zed-pnuts/bin/pnuts-lsp
    chmod +x zed-pnuts/bin/pnuts-lsp
fi

echo "========================================================"
echo "✅ zed-pnuts recriado com SUCESSO!"
echo "Para instalar no Zed: Command Palette -> 'zed: install dev extension' -> selecione a pasta 'zed-pnuts/'"
echo "========================================================"
