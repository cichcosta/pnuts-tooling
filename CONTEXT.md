# Memória e Contexto do Projeto: Pnuts & Groovy Tooling

Este arquivo é a fonte da verdade sobre o estado atual do projeto, decisões técnicas consolidadas, bugs já resolvidos e lições aprendidas. Qualquer agente de IA ou desenvolvedor deve consultar este arquivo para manter o contexto vivo e evitar retrabalho ou regressões.

---

## 1. Informações Básicas do Repositório

- **Repositório:** `pnuts-tooling`
- **Branch Ativa para o LSP:** `feature/pnuts-groovy-lsp`
- **Branch Base:** `pnuts-grammar-bootstrap` (derivada da `main`)
- **Linguagens Suportadas:**
  - **Pnuts:** `.pnuts`, `.pnut`
  - **Groovy:** `.groovy`

---

## 2. Decisões de Arquitetura Consolidadas

1. **Parser Central:** Tree-sitter (`tree-sitter-pnuts`).
   - Gramática em `tree-sitter-pnuts/grammar.js`.
   - Gera parser em C compilado para shared libraries (`.so`, `.dll`, `.dylib`) e `.wasm` (usado nativamente pelo Zed e VSCode).
2. **Motor de Formatação Atual:** Topiary CLI + queries em Scheme (`pnuts.scm`).
   - Binários empacotados em `resources/bin/` e executáveis em `bin/`.
3. **Language Server Protocol (`pnuts-lsp` - Fase 2 Concluída):**
   - Implementado em Rust com `tower-lsp` e `tokio` (comunicação stdio / JSON-RPC).
   - Workspace Cargo raiz contendo `pnuts-lsp` e `tree-sitter-pnuts`.
   - Gerenciamento de documentos em memória (`DocumentStore`) tolerante a esquemas `file://` e buffers não salvos `untitled:`.
   - Re-parsing incremental via AST Tree-sitter em `didOpen`, `didChange`, `didSave` e remoção em `didClose`.
   - Logs de tracing roteados exclusivamente para `stderr`, preservando o canal `stdout` para mensagens JSON-RPC.

---

## 3. Histórico de Bugs Críticos Resolvidos na Gramática (Regressões Proibidas)

Ao modificar `grammar.js`, NUNCA reintroduzir os seguintes erros:

1. **Literais de Caractere (`char(34)`):**
   - O Pnuts suporta a sintaxe `char(34)` para representar caracteres como aspas ou caracteres especiais.
   - Mapeado como regra específica no parser.
2. **Loops `for (x in y)` e `foreach x(y)`:**
   - Suporte a ambas as variantes de iteração de coleções Java/Groovy/Pnuts.
3. **Mapas Vazios e Literais (`wmap = [:]`):**
   - Na sintaxe Groovy/Pnuts, `[:]` define um mapa vazio. Anteriormente colidia com subscripts de array.
4. **Tratamento de Exceções (`catch (Exception e)`):**
   - Suporte a tipos de exceção tipados com declaração de variável e blocos multilinhas.
5. **Comentários Inline no Fim da Linha:**
   - Regra de formatação: comentários no final de comandos são movidos para a linha anterior com indentação preservada para evitar quebras de bloco.
6. **Espaçamento de Blocos Multilinhas:**
   - Blocos com mais de uma linha de conteúdo recebem uma linha em branco após a abertura `{` e nenhuma linha em branco antes do fechamento `}`.
7. **Formatação de Cláusulas `switch` (`case` e `default`):**
   - A sintaxe do compilador/runtime Pnuts (`Pnuts.jjt` / JavaCC) não aceita quebras de linha (`<EOL>`) entre a expressão do case e os dois pontos (`"case" Expression(env) ":"`). Uma quebra de linha antes de `:` gerava o erro em runtime: `MError getAuxMessage(): Encountered "\n" at column ...`.
   - No Topiary (`pnuts.scm`), a query anterior continha `(switch_case (_) @append_hardline)` que inseria uma quebra de linha imediatamente após o valor do case (ex: `"2017"`), empurrando o `:` para a próxima linha (`case "2017"\n:`). Além disso, `":" @append_indent_start` sem `@append_indent_end` correspondente causava indentação cumulativa em escadinha para cada `case` subsequente.
   - **Correção consolidada**:
     - Statements do case formatados com `(switch_case ":" (_) @append_hardline @allow_blank_line_before)`.
     - Dois pontos colados ao valor/keyword com `":" @prepend_antispace`.
     - Delimitação de escopo de indentação com `(switch_case) @append_indent_end @allow_blank_line_before` e `(switch_default) @append_indent_end @allow_blank_line_before`.

8. **Ausência de Escapes Unicode (`\uXXXX`) em Strings com Aspas Duplas:**
   - O lexer JavaCC do runtime Pnuts (`STRING_CONTENT` em `Pnuts.jjt`) aceita apenas escapes básicos (`\\`, `\"`, `\n`, `\t`, `\b`, `\r`, `\f`). O uso de `"\uFEFF"` dentro de aspas duplas gera erro léxico/sintático em tempo de execução: `MError getAuxMessage(): Encountered "\"" at column ...`.
   - **Solução consolidada**: Validar caracteres especiais pelo seu código inteiro nativo com `int(str.charAt(0)) == 65279` ou criar o caractere com `char(65279)`.
9. **Escopo Local de Imports dentro de Functions:**
   - No runtime Pnuts/Minimal, funções (`function nome(...) { ... }`) executam em um contexto (`pnuts.lang.Context`) próprio e isolado. `import(...)` declarados no topo/escopo global do arquivo **não são herdados** dentro das funções.
   - A tentativa de instanciar classes como `ArrayList()`, `HashMap()`, etc. dentro de uma função sem import local gera o erro: `MError getAuxMessage(): <Classe> is not defined-1`.
   - **Solução consolidada**: Declarar sempre os `import(...)` necessários localmente no início do corpo de cada função.

---


## 4. Editores Suportados

- **VSCode (`vscode-pnuts`):**
  - Empacotado como extensão `.vsix`.
  - Atualmente autossuficiente (executa no host local ou remoto).
  - Ponto de atenção: `textDocument/definition` (Go to Definition) precisa de ajuste; autocomplete duplicado por redundância do provider JS.
- **Zed (`zed-pnuts`):**
  - Instalado como Dev Extension (`zed: install dev extension`).
  - Utiliza gramática WASI compilada pelo próprio Zed.
  - **Decisão Arquitetural Consolidada:** O formatador externo (`external` chamando `pnuts-lsp --stdin`) é mantido intencionalmente no `.zed/settings.json` para garantir formatação instantânea em buffers não salvos (`untitled:`), contornando limitações do Zed via LSP em rascunhos.
  - `Ctrl + Click` (Go to Definition) e Completion já funcionam de forma limpa.

---

## 5. Diretrizes para Agentes de IA

1. **Manter o Foco:** Trabalhar estritamente nas fases definidas no `LSP_ROADMAP.md` e na matriz em `docs/ROADMAP_PARIDADE_VSCODE_ZED.md`.
2. **Verificação de Regressão:** Antes de concluir qualquer alteração no parser ou formatador, rodar os testes de validação contra a pasta `pnuts-samples/` e contra a base de testes.
3. **Atualização Contínua:** Sempre que uma funcionalidade for concluída ou um bug crítico for corrigido, documentar neste arquivo (`CONTEXT.md`) e atualizar o checklist em `docs/ROADMAP_PARIDADE_VSCODE_ZED.md`.

---

## 6. Estado Atual e Ponto de Retomada Imediato

- **Documento Operacional Central:** `docs/ROADMAP_PARIDADE_VSCODE_ZED.md`.
- **Fase Atual (Passo 1):** **Alinhamento, Eliminação de Ruídos e Correção de Definição (IMEDIATO)**.
  1. **Remoção de duplicidade no VS Code (Concluído):** Provedor JS agora atua apenas como fallback condicional (`!client`) e catálogo de snippets mantido estritamente para blocos estruturais de controle de fluxo, eliminando duplicidade com as funções nativas do LSP.
  2. **Correção de Go to Definition e Autocomplete no VS Code (Concluído):** Corrigido o `documentSelector` que filtrava documentos por `scheme: 'file'`, o que excluía buffers remotos via Remote-SSH (`vscode-remote://`). `activate` tornado assíncrono com `await client.start()`, adicionado suporte a `--stdio` no `main.rs`, trigger `$`, e resiliência de borda em `navigation.rs`. VSIX `0.3.9` gerado e instalado.
  3. **Portabilidade, Buffers Não Salvos e Instaladores Unificados no Zed (Concluído):** Identificada assimetria entre execução local (Windows) para buffers `untitled` e remota (Linux via SSH) para buffers de worktree. Criados instaladores automáticos (`scripts/install-zed.sh` e `scripts/install-zed.ps1`) que adicionam o executável ao PATH e higienizam o `settings.json` eliminando caminhos manuais. Resolução em `zed-pnuts/src/lib.rs` tornada genérica sem hardcoded username.
  4. **Empacotamento e Suporte Groovy no VS Code (Concluído):** VSIX `0.3.11` empacotado e instalado no VS Code Server (`code-server`). Adicionada contribuição formal para linguagem `groovy`, snippets estruturais, seletor de scheme `vscode-vfs` e teste automatizado E2E de ciclo de vida e completions em `.groovy` adicionado com sucesso.
  5. **Rename Symbol e prepareRename (Concluído - Passo 3.1):** Implementado módulo `rename.rs` com suporte completo a `textDocument/rename` e `textDocument/prepareRename` no `pnuts-lsp`. Validação de identificadores válidos, proteção estrita contra renomeação acidental de palavras-chave e built-ins (`println`, `getRows`, etc.), e isolamento seguro de escopo (parâmetros, variáveis locais e funções). VSIX `0.3.12` gerado e instalado no VS Code Server. Todos os 49 testes automatizados passando 100%.
  6. **Realce de Sintaxe do Groovy no VS Code (Concluído - Passo 2.4):** Criada gramática TextMate dedicada em `syntaxes/groovy.tmLanguage.json` para `.groovy` no `vscode-pnuts`. Suporte a anotações (`@CompileStatic`), strings interpoladas (`${...}`, `$var`), multiline (`"""`, `'''`), regex slashy (`/.../`, `$/.../$`), operadores Groovy (Elvis `?:`, safe navigation `?.`, spread `*.`, spaceship `<=>`, method pointer `.&`), tipos primitivos e classes comuns. VSIX `0.3.13` gerado e instalado no VS Code Server.
  7. **Find References (Concluído - Passo 3.2):** Implementado módulo `references.rs` com suporte nativo a `textDocument/references`. Resolução de escopo para funções, parâmetros e variáveis locais. Suporte estrito a `ReferenceContext { include_declaration }` para incluir ou excluir a declaração de acordo com o pedido do cliente LSP.
  8. **Range Formatting (Concluído - Passo 3.3):** Implementado `format_range` em `formatter.rs` com preservação inteligente de indentação base, formatação isolada do snippet e re-injeção sem desestruturar o código externo. Capability `document_range_formatting_provider` exposta e testada via E2E.
  9. **Suporte a Groovy no Zed (Concluído - Passo 2.2):** Criada configuração em `languages/groovy/config.toml` e queries (`brackets.scm`, `highlights.scm`, `indents.scm`, `outline.scm`). `extension.toml` atualizado para associar o `pnuts-lsp` às linguagens `["Pnuts", "Groovy"]` e URL do repositório da gramática tornada portátil sem caminhos locais fixos.
- **Métricas de Qualidade:** 57 testes automatizados (45 testes unitários e 12 testes de integração E2E com Tower-LSP) passando com 100% de sucesso.
