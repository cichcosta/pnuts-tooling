# Próximos Passos e Backlog Priorizado: Pnuts & Groovy Tooling

> **Data de Atualização:** 2026-09-20  
> **Branch Ativa:** `feature/pnuts-groovy-lsp`  
> **Versão Atual da Extensão:** `0.3.18` (instalada no VS Code Server)  
> **Status dos Testes:** 58 testes automatizados passando (100% de cobertura no Cargo).

Este documento é o plano operacional para as próximas sessões. Seu objetivo é garantir que nenhuma informação se perca, evitar discussões repetidas e guiar a execução passo a passo com clareza.

---

## 1. Estado Atual Consolidado (O que já está pronto e testado)

- **LSP Core (`pnuts-lsp`):**
  - Formatação completa (`textDocument/formatting`) com Topiary integrado e regras de indentação/comentários.
  - Formatação de trechos selecionados (`textDocument/rangeFormatting`) com preservação da indentação base.
  - Autocomplete (`textDocument/completion`) de escopo (funções locais, parâmetros, variáveis locais, built-ins, classes comuns e snippets).
  - Navegação (`textDocument/definition`) para funções, parâmetros e variáveis.
  - Localização de referências (`textDocument/references`).
  - Document Outline (`textDocument/documentSymbol`).
  - Renomeação segura (`textDocument/rename` e `prepareRename`).
  - Diagnósticos sintáticos em tempo real (`textDocument/publishDiagnostics`).
  - Documentação flutuante (`textDocument/hover`).
- **Colorização Sintática (VS Code):**
  - **Pnuts:** Paridade com Groovy para classes/constantes em maiúsculas (`WN`, `MConnection`, `MLogin` $\rightarrow$ `entity.name.type.class.pnuts`), propriedades (`support.type.property-name.pnuts`), variáveis e métodos.
  - **Groovy:** Correção do lookbehind da regex de *slashy strings* para evitar colisão com divisão aritmética (`(endTime - startTime) / 1000`) e diferenciação cromática de `m.idoc.trim()`.
- **Limpeza do Zed:**
  - Diretório `zed-pnuts/` removido (~1.3 GB liberados).
  - Receita e script de recriação registrados em [`docs/ZED_SETUP_RECIPE.md`](file:///home/chsilva/personal/projects/pnuts-tooling/docs/ZED_SETUP_RECIPE.md) e [`scripts/recreate-zed.sh`](file:///home/chsilva/personal/projects/pnuts-tooling/scripts/recreate-zed.sh).

---

## 2. Backlog Priorizado para as Próximas Sessões

### 🎯 Passo 1: Implementação do Signature Help (`textDocument/signatureHelp`)
- **Objetivo:** Exibir um balão flutuante interativo ao abrir parênteses `(` ou digitar vírgulas `,` durante a chamada de qualquer função ou método.
- **Comportamento Esperado:**
  - Mostrar a assinatura completa: `getMCursor(query: String, connection: MConnection)`.
  - Destacar em **negrito** o parâmetro ativo conforme o usuário avança com as vírgulas.
  - Suportar tanto funções locais declaradas no script (`function calcular(a, b, c)`) quanto funções nativas de banco e runtime (`getDialogFormEx`, `getRows`, etc.).
- **Tarefas de Implementação:**
  1. Criar `pnuts-lsp/src/signature_help.rs` com inspeção da AST Tree-sitter para nós `call_expression` e contagem de argumentos (vírgulas).
  2. Integrar suporte na tabela de assinaturas de funções do domínio e funções declaradas no arquivo.
  3. Registrar `signature_help_provider` nas capabilities de `initialize` em `pnuts-lsp/src/backend.rs` com triggers `vec!["(".to_string(), ",".to_string()]`.
  4. Adicionar testes unitários em `signature_help.rs` e teste de integração JSON-RPC em `tests/lsp_e2e_test.rs`.

---

### 🎨 Passo 2: Investigação e Refinamento do Ícone do Pnuts
- **Problema Notado:** O usuário observou que o ícone do Pnuts no VS Code continuou com a aparência anterior após a atualização.
- **Causas Potenciais a Investigar:**
  1. **Cache de Ícones do VS Code:** O VS Code armazena em cache os SVGs na interface web/desktop. Fazer um teste forçando a limpeza do cache de temas ou recarregando o tema de ícones.
  2. **Extensão Icon vs File Icon:** No [`vscode-pnuts/package.json`](file:///home/chsilva/personal/projects/pnuts-tooling/vscode-pnuts/package.json), a propriedade raiz `"icon": "icons/pnuts.png"` define o ícone exibido na lista de extensões instaladas. O arquivo `pnuts.png` ainda é a imagem antiga de alta resolução.
  3. **Tema de Ícones Ativo:** O VS Code aplica o ícone do arquivo de acordo com o tema selecionado (`File Icon Theme`). Se o usuário estiver utilizando "Seti" ou "Material Icon Theme", o tema pode ter precedência sobre o ícone contribuído pela linguagem.
- **Tarefas de Resolução:**
  - Gerar uma versão PNG moderna a partir do novo SVG minimalista para substituir `icons/pnuts.png`.
  - Revisar a contribuição no `icon-theme.json` e orientar o usuário sobre como ativar o `Pnuts File Icons` ou verificar a precedência.

---

### 📖 Passo 3: Enriquecimento da Documentação no Hover (`textDocument/hover`)
- **Objetivo:** Tornar os balões de ajuda ao passar o mouse ainda mais ricos e completos.
- **Tarefas:**
  - Mapear objetos e classes frequentes no dia a dia: `MConnection`, `MLogin`, `MForm`, `MCursor`, métodos de transação (`commit`, `rollback`).
  - Adicionar blocos de código markdown com exemplos de uso direto para copiar e colar.

---

### ⚡ Passo 4: Code Actions / Quick Fixes (`textDocument/codeAction`)
- **Objetivo:** Atalhos inteligentes via `Ctrl + .` (ou lâmpada amarela do editor).
- **Exemplos de Ações:**
  - "Envolver trecho selecionado em bloco try/catch".
  - "Adicionar imports de classes comuns da JVM (`java.util.List`, `java.util.HashMap`, etc.)".
  - "Inserir cabeçalho de script padrão".

---

## 3. Comandos Úteis para Retomada

```bash
# Executar todos os testes da suíte Rust:
cargo test

# Compilar binário LSP otimizado de release:
cargo build --release

# Empacotar e reinstalar extensão no VS Code Server:
cd vscode-pnuts
npx @vscode/vsce package --no-git-tag-version
~/.vscode-server/bin/7debcd0e2acdea1c52de81bf9ee1620444407dda/bin/code-server --install-extension vscode-pnuts-0.3.18.vsix --force
```
