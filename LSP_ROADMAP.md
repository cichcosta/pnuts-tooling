# Roadmap & Arquitetura: Pnuts & Groovy LSP (Language Server Protocol)

Este documento define o objetivo técnico, a arquitetura e as fases de execução para transformar as ferramentas de Pnuts e Groovy em um **Language Server (LSP)** de alta performance, unificando formatação, syntax highlighting, autocompletion e sugestões de código para **Zed** e **VSCode**.

---

## 1. Visão Geral e Objetivos

### O Problema Atual:
- Formatação e syntax highlighting dependem de executáveis avulsos e queries Topiary.
- No Zed, a ausência de um LSP exige configurações manuais (`settings.json`) e gera problemas em buffers não salvos (`untitled`) em sessões remotas.
- Desenvolvedores precisam de produtividade moderna: autocomplete de métodos, variáveis de escopo, snippets de controle de fluxo e detecção de erros em tempo real.

### A Solução (LSP):
- Um único binário de Language Server (`pnuts-lsp`) leve, multiplataforma (Linux, Windows, macOS), escrito em Rust com base no `tower-lsp` e no parser Tree-sitter existente.
- Suporte nativo tanto para arquivos **Pnuts** (`.pnuts`, `.pnut`) quanto para scripts **Groovy** (`.groovy`).
- Integração "Plug & Play": instalar a extensão no VSCode ou no Zed ativa automaticamente todas as funcionalidades sem exigir nenhuma configuração manual no `settings.json`.

---

## 2. Capacidades do LSP (Por Fase de Implementação)

| Capacidade LSP | Método do Protocolo | Descrição |
| :--- | :--- | :--- |
| **Formatação** | `textDocument/formatting` | Formatação de documento completo (via motor Topiary/AST embutido). |
| **Formatação em Range** | `textDocument/rangeFormatting` | Formatação de trechos selecionados de código. |
| **Autocomplete / Sugestões**| `textDocument/completion` | Palavras-chave, estruturas de controle, variáveis locais e funções. |
| **Diagnósticos / Linter** | `textDocument/publishDiagnostics` | Identificação visual de erros de sintaxe (nós `ERROR` da Tree-sitter). |
| **Hover / Documentação** | `textDocument/hover` | Exibição de assinaturas de funções e tipos básicos ao passar o mouse. |
| **Ir para Definição** | `textDocument/definition` | Navegação direta para declaração de variáveis e funções. |
| **Símbolos do Documento** | `textDocument/documentSymbol` | Outline estruturado de classes, métodos e funções. |

---

## 3. Fases de Execução

### Fase 1: Estabilização e Blindagem do Parser / Formatter Base
> **Meta:** Garantir que o parser Tree-sitter e o formatador continuem 100% tolerantes a erros em bases de código legadas.
- [ ] Ampliar corpus de testes com scripts reais de produção (Pnuts e Groovy).
- [ ] Testes de regressão automatizados para casos críticos já resolvidos:
  - Literais de caractere `char(34)`.
  - Loops `for (x in y)` e `foreach x(y)`.
  - Mapas literais `wmap = [:]`.
  - Blocos multilinhas `catch (Exception e)`.
- [ ] Manter pipeline de compilação da gramática C / WASM / Rust atualizado.

### Fase 2: Core do LSP Server em Rust (`pnuts-lsp`)
> **Meta:** Criar o esqueleto do servidor LSP comunicando via JSON-RPC / stdio.
- [x] Criar crate `pnuts-lsp` no repositório com `tower-lsp` e `tree-sitter`.
- [x] Implementar ciclo de vida básico do LSP:
  - `initialize` e `initialized`.
  - `shutdown` e `exit`.
- [x] Gerenciamento de estado de documentos em memória (`textDocument/didOpen`, `textDocument/didChange`, `textDocument/didClose`), permitindo funcionamento perfeito em buffers **não salvos** (`untitled`).

### Fase 3: Provedor de Formatação Nativo no LSP
> **Meta:** Resolver de vez a formatação no Zed e VSCode sem tocar em `settings.json`.
- [x] Implementar `textDocument/formatting` dentro do `pnuts-lsp`.
- [x] O LSP recebe o buffer via memória/protocolo, aplica as transformações e retorna `TextEdit[]`.
- [x] Pré-processamento de comentários (mover comentário no fim da linha para cima) e pós-processamento de chaves/flush integrados nativamente em Rust.
- [x] Atualizar extensões:
  - **Zed (`zed-pnuts`):** Registrar o `pnuts-lsp` no `extension.toml`. O Zed passa a formatar nativamente via LSP ao salvar ou no atalho.
  - **VSCode (`vscode-pnuts`):** Conectar e suportar `pnuts-lsp` e binários empacotados.

### Fase 4: Autocomplete e Sugestões de Código Inteligentes
> **Meta:** Acelerar a escrita de código com sugestões contextuais.
- [x] **Snippets & Palavras-chave:**
  - `foreach`, `for`, `while`, `if`, `else`, `try/catch/finally`, `function`, `switch/case`.
  - Comandos rápidos (`println`, `println-flush`, `char(34)`).
- [x] **Inspeção de Escopo via AST Tree-sitter:**
  - Sugerir variáveis locais declaradas antes da posição do cursor.
  - Sugerir parâmetros da função atual quando o cursor estiver dentro do seu escopo.
  - Sugerir funções e métodos declarados no documento com assinatura de parâmetros.
- [x] **Sugestões de Bibliotecas Comuns:**
  - Pacotes Java comuns na JVM (`List`, `ArrayList`, `Map`, `HashMap`, `Set`, `HashSet`, `StringBuilder`, `SimpleDateFormat`, etc.).
  - Built-ins da linguagem Pnuts (`println`, `print`, `flush`, `char`, `load`, `eval`, `open`, etc.).

### Fase 5: Diagnósticos em Tempo Real (Error Highlighting)
> **Meta:** Avisar o desenvolvedor de erros sintáticos antes de rodar o script.
- [x] Varrer a árvore Tree-sitter buscando nós `ERROR` ou nós `MISSING`.
- [x] Publicar diagnósticos (`textDocument/publishDiagnostics`) com sublinhado vermelho e mensagem clara.
- [x] Integração automática e em tempo real nos eventos `did_open`, `did_change`, `did_save` e limpeza em `did_close`.

---

## 4. Princípios e Regras de Segurança

1. **Incremental e Focado:** Não tentar fazer tudo de uma vez. Cada fase deve ter testes e validação antes de passar à próxima.
2. **Respeito ao Legado:** Pnuts e Groovy aceitam sintaxes dinâmicas e heterogêneas; o parser e o LSP NUNCA devem travar ou recusar o arquivo, mas sim tratar exceções de forma tolerante e graciosa.
3. **Multiplataforma:** Binários gerados para Linux x64, macOS (arm64/x64) e Windows x64.
