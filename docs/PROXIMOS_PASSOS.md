# Próximos Passos e Backlog Priorizado: Pnuts & Groovy Tooling

> **Data de Atualização:** 2026-09-20  
> **Branch Ativa:** `feature/pnuts-groovy-lsp`  
> **Versão Atual da Extensão:** `0.3.22` (instalada no VS Code Server)  
> **Status dos Testes:** 74 testes automatizados passando (100% de cobertura no Cargo: 59 unitários, 14 E2E e 1 grammar).

Este documento é o plano operacional para as próximas sessões. Seu objetivo é garantir que nenhuma informação se perca, evitar discussões repetidas e guiar a execução passo a passo com clareza.

---

## 1. Estado Atual Consolidado (O que já está pronto e testado)

- **LSP Core (`pnuts-lsp`):**
  - **Signature Help (`textDocument/signatureHelp`):**
    - Balão flutuante interativo com assinatura completa ativado por digitação de `(` e `,` ou atalho `Ctrl+Shift+Space`.
    - Destaque em negrito do parâmetro ativo em tempo real (`active_parameter`), com suporte a múltiplos argumentos e chamadas aninhadas (`formatNumber(round(x, 2), ...)`).
    - Suporte a funções locais declaradas no script (`function nome(...)`), métodos em classes, funções nativas/banco (`getRows`, `getMCursor`, `getDialogFormEx`, etc.) e métodos de objetos (`COMMON_METHODS`).
    - Scanner léxico resiliente com mascaramento de strings, comentários e blocos aninhados (`[]`, `{}`).
  - **Hover Enriquecido (`textDocument/hover`):**
    - Cards completos de documentação e blocos de código com exemplos práticos prontos para copiar e colar para as classes essenciais do dia a dia: `MConnection`, `MLogin`, `MForm`, `MCursor`, `MRow`, `MDate`, `MFile`, `JSONObject`, `JSONArray`.
    - Documentação de métodos comuns de objetos (`trim`, `substring`, `get`, `put`, `size`, `beginWork`, `commitWork`, etc.).
    - Exibição de tipo e declaração de variáveis locais atribuídas no escopo.
  - **Code Actions & Quick Fixes (`textDocument/codeAction`):**
    - Atalho `Ctrl + .` / lâmpada amarela do editor.
    - **Refactoring:** "Envolver em bloco try / catch" envolvendo a seleção ou linha com indentação perfeita e tratamento padrão com `showError`.
    - **Quick Fix:** "Importar '<classe>'" para classes Java frequentes (`ArrayList`, `HashMap`, `List`, `Map`, `BigDecimal`, `SimpleDateFormat`, `JSONObject`, etc.) com inserção limpa no topo do arquivo sem duplicatas.
    - **Quick Action:** "Inserir cabeçalho padrão de script" para novos arquivos ou scripts sem header.
  - Formatação completa (`textDocument/formatting`) com Topiary integrado e regras de indentação/comentários.
  - Formatação de trechos selecionados (`textDocument/rangeFormatting`) com preservação da indentação base.
  - Autocomplete (`textDocument/completion`) de escopo (funções locais, parâmetros, variáveis locais, built-ins, classes comuns e snippets).
  - Navegação (`textDocument/definition`) para funções, parâmetros e variáveis.
  - Localização de referências (`textDocument/references`).
  - Document Outline (`textDocument/documentSymbol`).
  - Renomeação segura (`textDocument/rename` e `prepareRename`).
  - Diagnósticos sintáticos em tempo real (`textDocument/publishDiagnostics`).
- **Ícones Específicos das Linguagens e Colorização Sintática (VS Code):**
  - **Ícones Nativos Catppuccin (inspirados em `daveedmee/catppuccin-icons`):**
    - `icons/pnuts.svg`: Amendoim vetorial autêntico em Catppuccin Peach (`#f5a97f`), nervuras em Yellow (`#eed49f`), contorno Macchiato Base (`#24273a`) e brilhos em Rosewater (`#f4dbd6`).
    - `icons/pnuts.png`: Renderização vetorial anti-aliased de alta resolução (256x256, Lanczos supersampling) do ícone Catppuccin Pnuts para o marketplace e painel de extensões.
    - `icons/groovy.svg`: Glifo dinâmico em Catppuccin Sapphire (`#7dc4e4`) com laço em Catppuccin Sky (`#91d7e3`).
    - **Sem interferência no tema de ícones:** Nenhuma alteração global em `workbench.iconTheme`, preservando 100% o tema de pastas e arquivos padrão do usuário.
  - **Pnuts:** Paridade com Groovy para classes/constantes em maiúsculas (`WN`, `MConnection`, `MLogin` $\rightarrow$ `entity.name.type.class.pnuts`), propriedades (`support.type.property-name.pnuts`), variáveis e métodos.
  - **Groovy:** Correção do lookbehind da regex de *slashy strings* para evitar colisão com divisão aritmética (`(endTime - startTime) / 1000`) e diferenciação cromática de `m.idoc.trim()`.
- **Limpeza do Zed:**
  - Diretório `zed-pnuts/` removido (~1.3 GB liberados).
  - Receita e script de recriação registrados em [`docs/ZED_SETUP_RECIPE.md`](file:///home/chsilva/personal/projects/pnuts-tooling/docs/ZED_SETUP_RECIPE.md) e [`scripts/recreate-zed.sh`](file:///home/chsilva/personal/projects/pnuts-tooling/scripts/recreate-zed.sh).

---

## 2. Backlog do Sprint Concluído com Sucesso (v0.3.22)

- [x] **Passo 1: Signature Help (`textDocument/signatureHelp`)**
- [x] **Passo 2: Ícones Nativos Catppuccin para Pnuts e Groovy (`icons/pnuts.svg`, `icons/groovy.svg`, `icons/pnuts.png`)**
- [x] **Passo 3: Preservação estrita do Tema de Ícones Padrão do Usuário (sem alterar `workbench.iconTheme`)**
- [x] **Passo 4: Enriquecimento da Documentação no Hover (`textDocument/hover`)**
- [x] **Passo 5: Code Actions / Quick Fixes (`textDocument/codeAction`)**

---

## 3. Próximos Passos Sugeridos para os Próximos Sprints

1. **Construção da IDE Portável no Neovim (v0.12+):**
   - Especificação técnica e roteiro completo registrados em [`docs/NEOVIM_PORTABLE_IDE_RECIPE.md`](file:///home/chsilva/personal/projects/pnuts-tooling/docs/NEOVIM_PORTABLE_IDE_RECIPE.md).
   - Execução em 5 fases:
     - **Fase 1:** Base visual estética (Catppuccin Macchiato, Lazy.nvim, Lualine, Bufferline, Snacks/Fzf-lua, Blink.cmp).
     - **Fase 2:** LSP, Treesitter e integração nativa com o `pnuts-lsp`.
     - **Fase 3:** Estação de Debug Visual (DAP UI) para Go, Rust, Zig, JS/TS e Java.
     - **Fase 4:** Especializações Enterprise (Java/Quarkus com JDTLS, Kotlin, Neotest).
     - **Fase 5:** Empacotamento em repositório Dotfiles universal multiplataforma.
2. **Gramática Groovy Tree-sitter Nativa:**
   - Suporte a construções específicas do Groovy avançado (closures multilinhas tipadas, traits).
