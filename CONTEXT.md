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

- **Fase Atual Concluída:** **Fase 5: Diagnósticos em Tempo Real (Error Highlighting via `textDocument/publishDiagnostics`)**.
  - Módulo nativo [`diagnostics.rs`](file:///home/chsilva/personal/projects/pnuts-tooling/pnuts-lsp/src/diagnostics.rs) implementado em Rust:
    - Varredura recursiva de nós `ERROR` e `MISSING` na AST Tree-sitter do documento.
    - Conversão precisa de colunas byte-offset para offsets UTF-16 conforme especificação LSP.
    - Ranges ajustados para garantir visibilidade do sublinhado vermelho mesmo em nós sem largura (ex: tokens esperados ausentes).
    - Mensagens amigáveis contextualizadas com o token inesperado ou token ausente.
    - Publicação em tempo real via `self.client.publish_diagnostics` nos eventos `did_open`, `did_change` e `did_save`.
    - Limpeza imediata de diagnósticos residuais no evento `did_close`.
    - Fix em `Document::update`: reparsing determinístico em modo full sync.
  - 23/23 testes automatizados passando (unitários de diagnóstico, parser, formatador, completions e integração e2e).
  - Binário release gerado e atualizado em `~/.local/bin/pnuts-lsp`, `zed-pnuts/` e `vscode-pnuts/`.

- **Próximas Melhorias do LSP (Capacidades de Navegação):**
  1. **Hover / Documentação (`textDocument/hover`):** Exibir documentação de funções e tipos ao passar o mouse.
  2. **Ir para Definição (`textDocument/definition`):** Navegar diretamente para a declaração de variáveis locais ou funções no documento.
  3. **Símbolos do Documento (`textDocument/documentSymbol`):** Outline estruturado de funções, métodos e variáveis do arquivo para a paleta de símbolos do Zed e VSCode.
