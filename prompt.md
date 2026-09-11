CONTEXTO DO PROJETO

Trabalho em uma empresa que usa uma linguagem de scripting legada chamada
Pnuts, rodando sobre a JVM (compatível até Java 8), com sintaxe parecida
com JavaScript/Java, além de scripts em Groovy. Não existe formatter,
linter, LSP ou syntax highlighting moderno e integrado para essas linguagens.
Temos milhares de arquivos `.pnuts` e `.groovy` no projeto.

Você está rodando localmente, com acesso ao sistema de arquivos e a
comandos de shell. Trabalhe de forma incremental, evitando reprocessar
arquivos grandes desnecessariamente mais de uma vez, e me avise se perceber
que uma tarefa vai consumir muito contexto antes de executá-la.

OBJETIVO GERAL

1. Manter e aperfeiçoar a gramática Tree-sitter tolerante a erros para
   Pnuts e Groovy, cobrindo syntax highlighting e formatação sintática.
2. Evoluir o ecossistema de ferramentas para um **Language Server (LSP)**
   unificado (`pnuts-lsp`), suportando tanto **Pnuts** quanto **Groovy**,
   com:
   a. **Formatação Nativa de Documento e Seleção:** Via protocolo LSP
      (`textDocument/formatting`), permitindo que editores como Zed e
      VSCode formatem buffers salvos e não salvos instantaneamente sem
      nenhuma dependência de configurações manuais de shell ou `settings.json`.
   b. **Autocomplete e Sugestão de Código:** Sugestão contextual baseada na
      árvore sintática (AST), cobrindo palavras-chave, variáveis de escopo,
      funções declaradas, blocos de controle (`foreach`, `if`, etc.) e
      padrões comuns do framework interno.
   c. **Syntax Highlighting e Diagnósticos:** Detecção e exibição visual de
      erros de sintaxe em tempo real (`textDocument/publishDiagnostics`).
   d. **Navegação de Código:** Navegação para definição (`textDocument/definition`)
      e outline de símbolos (`textDocument/documentSymbol`).

A gramática e o LSP devem ser TOLERANTES: comandos e funções desconhecidas
ou dinâmicas não devem quebrar o parsing do arquivo inteiro nem travar o
servidor LSP.

DECISÕES TÉCNICAS CONSOLIDADAS

- Linguagem de implementação do LSP e tooling: Rust.
- Motor de parsing: Tree-sitter (`tree-sitter-pnuts`, grammar.js -> C parser).
- Motor de formatação: Topiary e AST Tree-sitter.
- Framework de LSP: `tower-lsp` (Rust assíncrono via tokio / stdio).
- Extensões suportadas:
  - VSCode (`vscode-pnuts`): Integração direta via Language Client.
  - Zed (`zed-pnuts`): Integração via `extension.toml` com Language Server.
- Repositório e Branch de trabalho:
  - Branch do LSP: `feature/pnuts-groovy-lsp`
  - Documentos de referência rápida: `LSP_ROADMAP.md` e `CONTEXT.md`.

DOCUMENTAÇÃO TÉCNICA E GUIAS DE FOCO

- [LSP_ROADMAP.md]: Mapa detalhado de fases, milestones e capacidades do LSP.
- [CONTEXT.md]: Memória técnica do projeto, histórico de bugs corrigidos e regras para evitar regressões.
- [docs/GUIA_DE_PNUTS.md]: Guia consolidado de sintaxe e padrões da linguagem Pnuts.
- [ZED_FORMATTER_DEBUG.md]: Histórico de diagnóstico de formatação no Zed.

FASES DE EXECUÇÃO DO PROJETO

- FASES 0 a 7 (CONCLUÍDAS):
  Bootstrap da gramática Tree-sitter, motor Topiary CLI, queries básicas de
  highlighting, criação dos pacotes iniciais de extensão para VSCode e Zed.

- FASE 8 - ESTABILIZAÇÃO DO PARSER E TESTES CONTÍNUOS (EM ANDAMENTO):
  Garantir robustez total contra bugs recentes (`char(34)`, `for (in)`, `[:]`,
  `catch (Exception e)`), mantendo testes de regressão em `test/corpus/`.

- FASE 9 - CORE DO SERVIDOR LSP (`pnuts-lsp`):
  Criação da crate Rust `pnuts-lsp`, ciclo de vida do servidor (initialize,
  shutdown) e gerenciamento de buffers em memória via JSON-RPC / stdio.

- FASE 10 - FORMATAÇÃO NATIVA VIA LSP:
  Implementação de `textDocument/formatting`, eliminando de vez a necessidade
  de comandos externos manuais em `settings.json` no Zed e no VSCode.

- FASE 11 - AUTOCOMPLETE E SUGESTÕES CONTEXTUAIS:
  Implementação de `textDocument/completion` para palavras-chave, variáveis
  locais, parâmetros de função e snippets do framework.

- FASE 12 - DIAGNÓSTICOS E NAVEGAÇÃO:
  Implementação de `textDocument/publishDiagnostics`, `textDocument/definition`
  e `textDocument/hover`.

- FASE 13 - EMPACOTAMENTO E DISTRIBUIÇÃO PLUG & PLAY:
  Geração de binários estáticos multiplataforma e publicação das extensões.

REGRAS DE SEGURANÇA E CONDUTA

1. Nunca quebrar funcionalidades existentes de formatação e highlighting.
2. Cada nova capacidade do LSP deve vir acompanhada de testes unitários ou de integração.
3. Manter sempre `LSP_ROADMAP.md` e `CONTEXT.md` atualizados a cada avanço relevante.
4. Trabalhar incrementalmente com foco exclusivo na fase ativa da roadmap.
