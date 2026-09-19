# Matriz de Paridade e Roadmap: VS Code & Zed (`pnuts-tooling`)

**Data de Atualização:** 2026-09-17  
**Branch de Trabalho:** `feature/pnuts-groovy-lsp`  
**Objetivo:** Fonte de verdade operacional para acompanhar o que já foi implementado, as disparidades conhecidas entre VS Code e Zed, e o progresso das tarefas pendentes até a paridade total.

---

## 1. Decisões Arquiteturais e Particularidades dos Editores

1. **Formatação no Zed (Buffer Não Salvo vs LSP):**
   - **Decisão:** Manter **intencionalmente** a configuração do formatador externo no Zed (`.zed/settings.json` apontando para `pnuts-lsp --stdin`).
   - **Motivo:** O Zed possui limitações conhecidas no tratamento de buffers não salvos (`untitled:`) via LSP. Manter o formatador externo garante que rascunhos e buffers temporários do dia a dia continuem formatando instantaneamente sem perda de produtividade.

2. **Autocomplete no VS Code (Duplicidade):**
   - **Problema:** No VS Code, itens como `getRows`, `getMRows` e `getRow` aparecem **duas vezes** na lista de sugestões (uma como função e outra como snippet).
   - **Causa:** O `vscode-pnuts/extension.js` registra um `CompletionItemProvider` em JavaScript paralelamente ao `LanguageClient` (`pnuts-lsp`), e o `package.json` carrega `snippets/pnuts.json`.
   - **Ação:** O provider JS deve agir exclusivamente como fallback caso o LSP não inicialize.

3. **Navegação (Go to Definition):**
   - **Disparidade:** No **Zed**, `Ctrl + Click` já navega com sucesso para a definição das funções. No **VS Code**, o Go to Definition (`F12` / `Ctrl + Click`) **não está funcionando**.
   - **Ação:** Investigar o registro de capabilities do `vscode-languageclient` e o handler no VS Code para restabelecer a navegação.

---

## 2. Matriz Comparativa de Funcionalidades

| Funcionalidade | No LSP Core (`pnuts-lsp`) | No VS Code (`vscode-pnuts`) | No Zed (`zed-pnuts`) | Status Geral & Ação |
| :--- | :---: | :---: | :---: | :--- |
| **Formatação de Documento** | ✅ Nativo Rust | ✅ Nativo via LSP (+ fallback JS) | ✅ Externo via `--stdin` (+ LSP) | **Estável** (Zed mantém `--stdin` por design para `untitled`). |
| **Formatação em Range** (`rangeFormatting`) | ❌ Pendente | ❌ Indisponível | ❌ Indisponível | **Pendente** no `pnuts-lsp`. |
| **Autocomplete / Sugestões** | ✅ Nativo Rust | ✅ Limpo (LSP com fallback JS) | ✅ Limpo (LSP + Buffer Words) | **Paridade atingida**. |
| **Snippets com Placeholders** | ✅ Nativo Rust | ✅ Suportado | ✅ Suportado | **Paridade atingida**. |
| **Diagnósticos em Tempo Real** | ✅ Nativo Rust | ✅ Sublinhado e Problems tab | ✅ Sublinhado nativo | **Paridade atingida**. |
| **Go to Definition** | ✅ Implementado | ✅ Funciona (F12 / Ctrl + Click) | ✅ Funciona (Ctrl + Click) | **Paridade atingida**. |
| **Hover / Documentação** | ✅ Implementado | ✅ Suportado | ⚠️ Depende de atalho no Zed | Paridade no protocolo. |
| **Document Symbols / Outline** | ✅ Implementado | ✅ Outline tab | ✅ Duplo (`outline.scm` + LSP) | **Paridade atingida**. |
| **Rename Symbol** (`rename`) | ✅ Implementado | ✅ Suportado via LSP (F2) | ✅ Suportado via LSP | **Concluído**: AST Tree-sitter com prepareRename e escopo seguro. |
| **Find References** (`references`)| ❌ Pendente | ❌ Indisponível | ❌ Indisponível | **Pendente** no `pnuts-lsp`. |
| **Suporte a Groovy (`.groovy`)** | ✅ Parser tolerante | ✅ Oficializado no `package.json` | ⚠️ Apenas no `settings.json` | **Oficializado no VS Code**; pendente no Zed. |
| **Portabilidade da Gramática** | N/A | ✅ TextMate empacotado | ⚠️ Caminho absoluto Windows no `extension.toml` | **Ajustar Zed**: Tornar caminho relativo. |

---

## 3. Fases e Checklist de Execução

### 🎯 Passo 1: Alinhamento, Eliminação de Ruídos e Correção de Definição (IMEDIATO)
- [x] **1.1. Eliminar duplicação de autocomplete no VS Code:**
  - Ajustar `vscode-pnuts/extension.js` para registrar o `CompletionItemProvider` JS nativo **apenas como fallback** quando o LSP estiver desabilitado ou indisponível (mesma lógica usada no formatter).
  - Limpar `vscode-pnuts/snippets/pnuts.json` e `zed-pnuts/snippets/pnuts.json` para manter estritamente snippets estruturais (`if`, `for`, `switch`, etc.), eliminando prefixos de chamadas de métodos de banco (`getRows`, `getMRows`, etc.) que colidiam com as funções nativas do LSP.
  - Empacotado VSIX `vscode-pnuts-0.3.8.vsix`.
- [x] **1.2. Investigar e Corrigir Go to Definition no VS Code:**
  - Diagnóstico: `clientOptions.documentSelector` continha apenas `{ scheme: 'file' }` e `{ scheme: 'untitled' }`. No VS Code via Remote-SSH (ou WSL/Containers), o esquema dos buffers abertos é `vscode-remote://`. Isso impedia o VS Code de acoplar o `LanguageClient` aos arquivos remotos, silenciando tanto o **Go to Definition** quanto o **Autocomplete** via LSP.
  - Correção: `documentSelector` atualizado para englobar `{ language: 'pnuts' }`, `{ language: 'groovy' }`, bem como os esquemas explícitos `vscode-remote`, `vscode-vfs`, `file` e `untitled`.
  - `activate` tornado assíncrono com `await client.start()`, flags `--stdio` tratadas no `main.rs` e resiliência de borda de token implementada em `navigation.rs` (`find_definition` e `find_hover`).
  - VSIX `0.3.9` gerado e instalado no VS Code Server.
- [x] **1.3. Portabilidade da Gramática, Formatação em Buffers Não Salvos e Instaladores Unificados no Zed (`zed-pnuts`):**
  - Identificada a causa raiz da falha em buffers não salvos: no Zed, buffers `untitled` rodam estritamente no cliente local (Windows), enquanto buffers com caminho no disco rodam no servidor remoto (Linux via SSH). O comando `pnuts-lsp` falhava no Windows por ausência no `PATH` do usuário.
  - Implementado parser flexível e agnóstico de ordem de argumentos CLI no `pnuts-lsp/src/main.rs`.
  - Recompilados binários otimizados de release para Linux e Windows.
  - Criado script de instalação e higienização automática para Linux/macOS: [`scripts/install-zed.sh`](file:///home/chsilva/personal/projects/pnuts-tooling/scripts/install-zed.sh).
  - Criado script de instalação e higienização automática para Windows: [`scripts/install-zed.ps1`](file:///home/chsilva/personal/projects/pnuts-tooling/scripts/install-zed.ps1) (adiciona ao PATH automaticamente e higieniza o `settings.json` do Zed sem exigir configuração manual).
  - Removidos caminhos fixos de usuário de `zed-pnuts/src/lib.rs` para suportar qualquer usuário.
- [x] **1.4. Validar e Empacotar VSIX atualizado:**
  - Gerado pacote `vscode-pnuts-0.3.11.vsix` contendo os binários atualizados com tratamento de flags flexíveis (`--stdio`), dynamic binary resolution e esquemas de buffer remotos. Instalado com sucesso no VS Code Server.

### 🎯 Passo 2: Oficializar o Suporte a Groovy (`.groovy`)
- [x] **2.1. VS Code:** Adicionar suporte a `.groovy` no `package.json` (`activationEvents`, `languages`, `documentSelector`, `snippets`).
- [ ] **2.2. Zed:** Declarar suporte a Groovy no `extension.toml`.
- [x] **2.3. Testes:** Validar formatação e completion em scripts `.groovy` de exemplo (implementado teste E2E `test_lsp_groovy_document_lifecycle_and_completions`).

### 🎯 Passo 3: Implementação de Novas Capacidades Core no `pnuts-lsp`
- [x] **3.1. Rename Symbol (`textDocument/rename`):**
  - Implementado módulo `rename.rs` com resolução de escopos via AST Tree-sitter (parâmetros, variáveis locais e funções).
  - Implementado `textDocument/prepareRename` para validação prévia de identificadores e proteção de built-ins/keywords.
  - 5 testes unitários e 1 teste E2E criados e validados. VSIX `0.3.12` gerado e instalado.
- [ ] **3.2. Find References (`textDocument/references`):**
  - Localizar todas as referências de um símbolo no escopo do documento.
  - Testes unitários e E2E.
- [ ] **3.3. Range Formatting (`textDocument/rangeFormatting`):**
  - Formatar trechos selecionados de código sem desestruturar o restante do buffer.

---

## 4. Registro de Progresso das Tarefas

| Data | Tarefa | Status | Observações |
| :--- | :--- | :---: | :--- |
| 2026-09-17 | Levantamento inicial de disparidades e documentação | ✅ Concluído | Identificada duplicação de autocomplete e falha de Go to Definition no VS Code. Mantido formatador externo no Zed para buffers `untitled`. |
| 2026-09-18 | 1.1 Eliminar duplicação de autocomplete no VS Code | ✅ Concluído | Provedor JS tornado fallback condicional (`!client`), snippets limpos para conter apenas blocos de controle, VSIX 0.3.8 gerado. |
| 2026-09-18 | 1.2 Corrigir Go to Definition e Autocomplete no VS Code | ✅ Concluído | Identificada restrição de scheme (`vscode-remote`), documentSelector corrigido para todas as origens, `client.start()` tornado assíncrono, VSIX 0.3.9 instalado e testado 100%. |
| 2026-09-18 | 1.3 Zed Extension & Buffers Não Salvos | ✅ Concluído | Identificado que buffers `untitled` rodam no cliente local Windows e arquivos salvos no SSH Linux. Criados scripts de instalação e higienização automática (`install-zed.sh` e `install-zed.ps1`), tornando PATH e `settings.json` automáticos e transparentes. |
| 2026-09-18 | 1.4 Validar e Empacotar VSIX atualizado | ✅ Concluído | Pacote `vscode-pnuts-0.3.11.vsix` gerado com os binários de release e instalado com sucesso no VS Code Server (`code-server`). |
| 2026-09-18 | 2.1 & 2.3 Suporte Oficial a Groovy no VS Code e Testes E2E | ✅ Concluído | Declarada linguagem `groovy`, snippets e seletor `vscode-vfs` no VS Code. Implementado teste E2E automatizado de ciclo de vida e completions para documentos Groovy. |
| 2026-09-18 | 3.1 Rename Symbol (LSP Core & VS Code) | ✅ Concluído | Módulo `rename.rs` com suporte a `rename` e `prepareRename`. Proteção de built-ins e isolamento estrito de parâmetros e escopos locais. VSIX `0.3.12` instalado e 49 testes aprovados. |
