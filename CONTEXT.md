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

---

## 4. Editores Suportados

- **VSCode (`vscode-pnuts`):**
  - Empacotado como extensão `.vsix`.
  - Atualmente autossuficiente (executa no host local ou remoto).
- **Zed (`zed-pnuts`):**
  - Instalado como Dev Extension (`zed: install dev extension`).
  - Utiliza gramática WASI compilada pelo próprio Zed.
  - Com o LSP (Fase 3), passará a formatar nativamente sem necessitar de bloco `external` no `settings.json`.

---

## 5. Diretrizes para Agentes de IA

1. **Manter o Foco:** Trabalhar estritamente nas fases definidas no `LSP_ROADMAP.md`.
2. **Verificação de Regressão:** Antes de concluir qualquer alteração no parser ou formatador, rodar os testes de validação contra a pasta `pnuts-samples/` e contra a base de testes.
3. **Atualização Contínua:** Sempre que uma funcionalidade for concluída ou um bug crítico for corrigido, documentar neste arquivo (`CONTEXT.md`).

---

## 6. Estado Atual e Ponto de Retomada Imediato

- **Fase Atual Concluída:** **Fase 4: Autocomplete e Sugestões de Código Inteligentes (`textDocument/completion`)**.
  - Módulo nativo [`completion.rs`](file:///home/chsilva/personal/projects/pnuts-tooling/pnuts-lsp/src/completion.rs) implementado em Rust com:
    - **Inspeção de Escopo via AST Tree-sitter:**
      - Extração automática de variáveis locais (`variable_declaration`, `assignment_expression`, `for_statement`, `foreach_statement`, `catch_clause`) declaradas antes da posição do cursor.
      - Extração de parâmetros da função atual quando o cursor estiver dentro do seu corpo.
      - Extração de funções declaradas no arquivo com detalhe de assinatura `function nome(param1, param2)`.
    - **Snippets LSP com Tabstops:** `foreach (in)`, `foreach`, `for (i)`, `if`, `if-else`, `while`, `function`, `try-catch`, `try-catch-finally`, `switch`, `println`, `println-flush`, `char(34)`.
    - **Built-in Functions:** `println`, `print`, `flush`, `char`, `open`, `load`, `eval`, `error`, `getContext`, `isArray`, `isFunction`.
    - **Classes Comuns Java:** `List`, `ArrayList`, `Map`, `HashMap`, `Set`, `HashSet`, `String`, `StringBuilder`, `Integer`, `Double`, `Boolean`, `Date`, `Calendar`, `SimpleDateFormat`, `Exception`, `RuntimeException`.
    - **Palavras-chave:** `function`, `class`, `if`, `else`, `for`, `foreach`, `while`, `do`, `try`, `catch`, `finally`, `throw`, `return`, `break`, `continue`, `switch`, `case`, `default`, `import`, `package`, `null`, `true`, `false`, `new`, `var`, `def`.
    - Ordenação refinada com `sort_text` priorizando símbolos de escopo > snippets > built-ins > classes > palavras-chave.
  - Handler `completion` implementado em [`backend.rs`](file:///home/chsilva/personal/projects/pnuts-tooling/pnuts-lsp/src/backend.rs) e declarado nas capacidades (`completion_provider`).
  - 22/22 testes automatizados passando (unitários e integração e2e via JSON-RPC).
  - Binário release gerado e instalado em `~/.local/bin/pnuts-lsp` e empacotado em `zed-pnuts/` e `vscode-pnuts/`.

- **Próxima Ação Imediata ao Retomar:** **Fase 5: Diagnósticos em Tempo Real (Error Highlighting via `textDocument/publishDiagnostics`)**.
  1. Implementar varredura da árvore Tree-sitter buscando nós de erro (`ERROR` e `MISSING`) no documento.
  2. Gerar lista de `Diagnostic` do LSP com range preciso (linha, coluna de início e fim) e severidade `DiagnosticSeverity::ERROR`.
  3. Publicar diagnósticos automaticamente para o editor (`client.publish_diagnostics(uri, diagnostics, version)`) nos eventos `did_open`, `did_change` e `did_save`.
  4. Adicionar testes unitários de detecção de erro e testes de integração e2e.
