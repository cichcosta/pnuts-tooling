# Receita de Reconstrução: Extensão Pnuts & Groovy para Zed (`zed-pnuts`)

> **Nota:** Este documento é a receita completa, determinística e autossuficiente para recriar, compilar, configurar e instalar todo o ecossistema de suporte ao Pnuts e Groovy no editor **Zed**, caso no futuro seja necessário reativar ou publicar a extensão.

---

## 1. Contexto e Motivação da Higienização

A pasta `zed-pnuts/` continha cerca de **1.3 GB** em disco, composto principalmente por:
1. Diretório de build do compilador Rust para WebAssembly (`zed-pnuts/target/`).
2. Cópias duplicadas de binários nativos (`topiary`, `pnuts-lsp`, `pnuts-format`) para Windows, Linux e macOS.

Como o foco de desenvolvimento do projeto foi consolidado com sucesso na extensão do **VS Code** (que empacota um único arquivo `.vsix` completo e autocontido), a pasta `zed-pnuts/` foi removida do repositório para manter a árvore de trabalho limpa, ágil e livre de redundâncias.

---

## 2. Métodos de Reconstrução

### Método A: Restauração Instantânea via Git (Recomendado)
Todo o histórico do `zed-pnuts` está permanentemente preservado no Git no commit `9c32c45` (ou anterior). Para restaurar a pasta e arquivos relacionados exatamente como estavam:

```bash
# Na raiz do repositório pnuts-tooling:
git checkout 9c32c45 -- zed-pnuts/ scripts/install-zed.sh scripts/install-zed.ps1 .zed/
```

Caso queira descartar novamente depois:
```bash
rm -rf zed-pnuts
```

---

### Método B: Script Autônomo de Recriação (`recreate_zed.sh`)
Se preferir recriar a estrutura do zero sem recorrer ao histórico do Git, você pode rodar o seguinte script Bash:

```bash
#!/usr/bin/env bash
set -e

echo "=== Recriando zed-pnuts do zero ==="
mkdir -p zed-pnuts/src
mkdir -p zed-pnuts/languages/pnuts
mkdir -p zed-pnuts/languages/groovy
mkdir -p zed-pnuts/icon_themes
mkdir -p zed-pnuts/icons
mkdir -p zed-pnuts/snippets
mkdir -p zed-pnuts/bin
mkdir -p .zed

# 1. Cargo.toml
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

# 2. extension.toml
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

# 3. src/lib.rs
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

# 4. Configuração de Linguagem: Pnuts
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

# 5. Configuração de Linguagem: Groovy
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

# 6. Tree-sitter Queries (Brackets, Indents, Outline, Highlights)
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

# Highlights vêm direto da gramática Tree-sitter oficial
cp tree-sitter-pnuts/queries/highlights.scm zed-pnuts/languages/pnuts/highlights.scm
cp tree-sitter-pnuts/queries/highlights.scm zed-pnuts/languages/groovy/highlights.scm

# Snippets e Ícones
cp vscode-pnuts/snippets/pnuts.json zed-pnuts/snippets/pnuts.json
cp vscode-pnuts/icons/pnuts.svg zed-pnuts/icons/pnuts.svg

cat << 'EOF' > zed-pnuts/icon_themes/pnuts.json
{
  "$schema": "https://zed.dev/schema/icon_themes/v0.3.0.json",
  "name": "Pnuts Icon Theme",
  "author": "Pnuts Tooling Team",
  "themes": [
    {
      "name": "Pnuts Icon Theme",
      "appearance": "dark",
      "file_icons": {
        "pnuts": { "path": "./icons/pnuts.svg" }
      },
      "file_suffixes": {
        "pnuts": "pnuts",
        "pnut": "pnuts",
        "groovy": "pnuts"
      }
    },
    {
      "name": "Pnuts Icon Theme Light",
      "appearance": "light",
      "file_icons": {
        "pnuts": { "path": "./icons/pnuts.svg" }
      },
      "file_suffixes": {
        "pnuts": "pnuts",
        "pnut": "pnuts",
        "groovy": "pnuts"
      }
    }
  ]
}
EOF

# 7. Copiar executável nativo do LSP para a pasta de execução local
if [ -f "target/release/pnuts-lsp" ]; then
  cp target/release/pnuts-lsp zed-pnuts/bin/pnuts-lsp
  chmod +x zed-pnuts/bin/pnuts-lsp
fi

echo "✅ zed-pnuts recriado com sucesso!"
```

---

## 3. Configuração do Zed (`settings.json`)

Para que o Zed utilize o formatador e o Language Server em arquivos Pnuts e Groovy, adicione o seguinte trecho em `~/.config/zed/settings.json` (Linux/macOS) ou `%APPDATA%\Zed\settings.json` (Windows):

```json
{
  "file_types": {
    "Pnuts": ["pnuts", "pnut"],
    "Groovy": ["groovy"]
  },
  "languages": {
    "Pnuts": {
      "language_servers": [
        "pnuts-lsp",
        "..."
      ],
      "formatter": {
        "external": {
          "command": "pnuts-lsp",
          "arguments": ["--stdin"]
        }
      },
      "format_on_save": "on",
      "tab_size": 2
    },
    "Groovy": {
      "language_servers": [
        "pnuts-lsp",
        "..."
      ],
      "formatter": {
        "external": {
          "command": "pnuts-lsp",
          "arguments": ["--stdin"]
        }
      },
      "format_on_save": "on",
      "tab_size": 2
    }
  }
}
```

> **Por que manter o `"formatter.external"` com `--stdin`?**  
> O Zed possui uma assimetria arquitetural em sessões Remote-SSH: buffers sem nome (`untitled`) rodam exclusivamente no host local (Windows/macOS), enquanto arquivos no disco rodam no servidor remoto (Linux). Ter o formatador configurado via CLI `--stdin` garante que mesmo arquivos temporários não salvos possam ser formatados com o atalho `Alt + Shift + F`.

---

## 4. Como Compilar e Instalar no Zed

1. Abra o **Zed**.
2. Abra a paleta de comandos (`Ctrl+Shift+P` ou `Cmd+Shift+P`).
3. Digite e selecione: **`zed: install dev extension`**.
4. Selecione a pasta `zed-pnuts/` na janela de navegação.
5. O Zed compilará o crate Rust para WebAssembly (`wasm32-wasip1`) e clonará/compilará a gramática Tree-sitter declarada no `extension.toml`.
6. Para recompilar alterações nas queries ou código Rust:
   - Abra a paleta de comandos (`Ctrl+Shift+P`).
   - Digite **`zed: extensions`**.
   - Na aba **Dev Extensions**, encontre **Pnuts** e clique em **Rebuild**.
7. Para verificar erros de inicialização ou logs do Language Server no Zed:
   - Paleta de comandos $\rightarrow$ **`zed: open log`**.
