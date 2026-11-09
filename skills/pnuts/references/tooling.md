# Pnuts: Ecossistema de Ferramentas (Tooling & IDE) no Servidor

Este servidor possui o ecossistema completo de desenvolvimento para Pnuts e Groovy, compilado com alto desempenho em Rust e C.

---

## 1. Binários Globais Instalados

Os seguintes binários residem em `/home/chsilva/.local/bin/` (já exportados no `PATH` global do servidor):

| Binário | Caminho | Finalidade |
| :--- | :--- | :--- |
| `pnuts-lsp` | `~/.local/bin/pnuts-lsp` | Servidor LSP de alto desempenho (JSON-RPC via stdio). Suporta Pnuts e Groovy. |
| `pnuts-format` | `~/.local/bin/pnuts-format` | Formatador sintático de código baseado em Topiary + Tree-sitter (2 espaços). |

---

## 2. Capacidades do `pnuts-lsp`

O `pnuts-lsp` implementa os recursos essenciais do Language Server Protocol:

1. **Formatação de Documento e de Seleção (`textDocument/formatting`, `textDocument/rangeFormatting`):**
   - Formata buffers salvos e não salvos (`untitled:`) instantaneamente.
   - Padrão de 2 espaços por nível de indentação, normalização de operadores e quebras de linha estritas.
2. **Signature Help (`textDocument/signatureHelp`):**
   - Acionado ao digitar `(` ou `,` e via atalho `Ctrl + Shift + Space`.
   - Auxilia parâmetros de funções locais, built-ins corporativos (`getRows`, `getMCursor`, `getDialogForm`, etc.) e métodos de objetos.
3. **Documentação Contextual (`textDocument/hover`):**
   - Cards ricos em Markdown com snippets e exemplos práticos para classes de negócio (`MConnection`, `MLogin`, `MForm`, `MCursor`, etc.).
4. **Code Actions & Quick Fixes (`textDocument/codeAction`):**
   - Acionado via `Ctrl + .`:
     - *Envolver em bloco try / catch* com tratamento padrão.
     - *Importar '<classe>'* para classes Java frequentes (`java.util.ArrayList`, `java.util.HashMap`, etc.).
     - *Inserir cabeçalho padrão de script*.
5. **Navegação & Renomeação Semântica:**
   - Go to Definition (`textDocument/definition`).
   - Find References (`textDocument/references`).
   - Rename Symbol (`textDocument/rename`, `textDocument/prepareRename`).
6. **Diagnósticos em Tempo Real (`textDocument/publishDiagnostics`):**
   - Detecção de erros de sintaxe via AST Tree-sitter tolerante a falhas.

---

## 3. Formatação Manual via CLI

Para formatar um arquivo ou testar formatação diretamente na linha de comando:

```bash
# Formatar lendo de stdin e escrevendo em stdout:
pnuts-format < script.pnuts > script_formatado.pnuts

# Ou usando o script python do repositório pnuts-tooling:
python3 /home/chsilva/personal/projects/pnuts-tooling/scripts/format_pnuts.py --diff caminho/para/scripts
python3 /home/chsilva/personal/projects/pnuts-tooling/scripts/format_pnuts.py --write caminho/para/scripts
```

---

## 4. Extensão VS Code (`vscode-pnuts`)

O repositório `pnuts-tooling/vscode-pnuts/` empacota a extensão oficial:
- Arquivo de distribuição: `vscode-pnuts/vscode-pnuts-0.3.22.vsix`.
- Inclui os binários nativos empacotados para Linux x64 e Windows x64.
- Ícones dedicados no tema Catppuccin Macchiato para arquivos `.pnuts` (amendoim âmbar) e `.groovy` (glifo ciano).

---

## 5. Gramática Tree-sitter (`tree-sitter-pnuts`)

- Código-fonte da gramática: `tree-sitter-pnuts/grammar.js`.
- Gera o parser em C (`src/parser.c`), compatível com Neovim, Helix, Zed e extensões WASM.
- Conjunto de testes de sintaxe e regressão em `tree-sitter-pnuts/test/corpus/`.
