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
3. **Evolução Planejada (LSP):**
   - Transição para um servidor LSP nativo (`pnuts-lsp`), unificando formatação, autocomplete e diagnósticos.
   - Elimina dependências externas e configurações manuais nos editores.

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
