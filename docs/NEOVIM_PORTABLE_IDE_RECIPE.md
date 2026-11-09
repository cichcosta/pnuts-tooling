# Guia Mestre e Receita Operacional: Neovim como IDE Portável, Veloz e Estética

> **Data de Criação:** 2026-09-20  
> **Versão Alvo:** Neovim `v0.12.5+` (LuaJIT)  
> **Gerenciador de Runtimes:** `mise` (Java 11/21/25, Kotlin, Go, Rust, Zig, Node, Python)  
> **Foco:** Velocidade de Inicialização (< 30ms), Beleza Estética (Catppuccin Macchiato/Mocha), Capacidades Completas de IDE (LSP, Debug Visual/DAP, Formatação, Testes) e Portabilidade Multiplataforma (Linux, macOS, Windows, WSL, BSD, SSH).

---

## 1. Princípios Arquiteturais e Diretrizes de Design

1. **Velocidade Extrema (Startup < 30ms):**
   - Plugins carregados sob demanda (*lazy-loading*) por evento (`BufReadPost`, `BufNewFile`), comando ou tipo de arquivo (*filetype*).
   - Motor de autocomplete moderno em **Rust** (`blink.cmp`), eliminando a sobrecarga de memória e a latência de motores legados.
   - Fuzzy Finder em C/Rust (`fzf-lua` ou `snacks.picker`) para indexação e busca em microssegundos.
2. **Beleza & Ergonomia Visual (Estilo Catppuccin):**
   - Tema oficial `catppuccin/nvim` (sabores Macchiato e Mocha) com transparência opcional.
   - Interface flutuante suave (`noice.nvim` + `nui.nvim`): linha de comandos flutuante centralizada, caixas de diálogo elegantes e popups de hover/assinatura com bordas arredondadas.
   - Barra de status contextual (`lualine.nvim`) com branch Git, diagnósticos do LSP e status do Debugger.
   - Gerenciamento de abas visuais (`bufferline.nvim`) com ícones devicons coloridos e indicadores de modificação.
   - Notificações de progresso do LSP não-bloqueantes (`fidget.nvim`).
3. **Capacidade Completa de IDE (Debug Visual + Refatoração):**
   - Depuração visual estilo VS Code via DAP (`nvim-dap`, `nvim-dap-ui`, `nvim-dap-virtual-text`).
   - Formatação assíncrona automática ao salvar (`conform.nvim`).
   - Suporte de primeira classe a **Pnuts e Groovy** conectando nosso próprio [`pnuts-lsp`](file:///home/chsilva/personal/projects/pnuts-tooling/target/release/pnuts-lsp) e gramática [`tree-sitter-pnuts`](file:///home/chsilva/personal/projects/pnuts-tooling/tree-sitter-pnuts).
4. **Portabilidade Universal (Dotfiles Git):**
   - Configuração 100% contida em `~/.config/nvim/` (ou `%LOCALAPPDATA%\nvim` no Windows).
   - Um simples `git clone` em qualquer máquina nova instala e configura a IDE completa em segundos.

---

## 2. Estrutura Modular da Configuração (`~/.config/nvim/`)

```text
~/.config/nvim/
├── init.lua                        # Entrada principal (carrega lazy.nvim e configurações base)
├── lua/
│   ├── config/
│   │   ├── options.lua             # Configurações globais (linhas relativas, tabs, clipboard, undo)
│   │   ├── keymaps.lua             # Atalhos universais ergonômicos (<leader> = espaço)
│   │   └── autocmds.lua            # Ações automáticas (highlight ao copiar, format on save)
│   ├── plugins/
│   │   ├── ui.lua                  # Catppuccin, lualine, bufferline, noice, mini.icons
│   │   ├── navigation.lua          # snacks.picker / fzf-lua, oil.nvim (gerenciador de arquivos)
│   │   ├── treesitter.lua          # nvim-treesitter (syntax highlight preciso + gramática do pnuts)
│   │   ├── completion.lua          # blink.cmp (autocompletion veloz em Rust)
│   │   ├── formatting.lua          # conform.nvim (formatador assíncrono multi-linguagem)
│   │   ├── lsp/
│   │   │   ├── init.lua            # Configurações gerais do nvim-lspconfig e handlers de UI
│   │   │   ├── mason.lua           # Gerenciador de download automático de LSPs e DAP adapters
│   │   │   └── pnuts.lua           # Conexão nativa com o pnuts-lsp e formatação Topiary
│   │   └── dap/
│   │       ├── init.lua            # nvim-dap, nvim-dap-ui, nvim-dap-virtual-text
│   │       └── adapters.lua        # Configurações dos debuggers (Delve, CodeLLDB, JS-Debug, Java)
```

---

## 3. Matriz de Linguagens, LSPs, Formatadores e Debuggers (DAP)

| Linguagem / Ecossistema | Servidor LSP | Formatador (`conform.nvim`) | Adaptador de Debug (DAP) |
| :--- | :--- | :--- | :--- |
| **Pnuts** | `pnuts-lsp` *(nativo nosso)* | `topiary` / `pnuts-format` | Logging / Stdio dedicado |
| **Java** (Quarkus / Spring / Maven / Gradle) | `jdtls` (via `nvim-jdtls`) | `google-java-format` | `java-debug-adapter` + `vscode-java-test` |
| **Kotlin** | `kotlin-language-server` | `ktlint` | `kotlin-debug-adapter` |
| **Go** | `gopls` | `gofumpt` + `goimports` | `delve` (`dlv` via `nvim-dap-go`) |
| **Rust** | `rust-analyzer` (via `rustaceanvim`) | `rustfmt` | `codelldb` (via `nvim-dap`) |
| **Zig** | `zls` | `zig fmt` | `codelldb` (via `nvim-dap`) |
| **React / TypeScript / JavaScript** | `vtsls` ou `ts_ls` + `tailwindcss` | `prettier` ou `biome` | `vscode-js-debug` (Node & Chrome/Edge) |
| **Node.js** | `vtsls` + `eslint` | `prettier` ou `biome` | `vscode-js-debug` (anexo a processos) |

---

## 4. Estação de Debug Visual (DAP)

A experiência de depuração no Neovim replica o conforto visual do VS Code sem o peso do Electron:

### Componentes:
- **`nvim-dap`:** O cliente central do Debug Adapter Protocol.
- **`nvim-dap-ui`:** Abre automaticamente painéis visuais com:
  - **Scopes / Variables:** Inspeção de variáveis locais, argumentos e objetos em árvore expansível.
  - **Watch:** Expressões monitoradas customizadas.
  - **Call Stack:** Pilha de chamadas de funções entre threads.
  - **Breakpoints:** Lista de todos os pontos de interrupção ativos.
  - **REPL / Console:** Console interativo para executar código no contexto pausado.
- **`nvim-dap-virtual-text`:** Exibe o valor de variáveis diretamente inline ao lado do código no momento da pausa.

### Tabela Universal de Atalhos de Debug:

| Atalho | Ação de Depuração |
| :--- | :--- |
| `<F5>` | Iniciar Depuração / Continuar Execução |
| `<F9>` | Alternar Breakpoint (bolinha vermelha na coluna) |
| `<S-F9>` | Breakpoint Condicional (pausa apenas quando expressão for verdadeira) |
| `<F10>` | Step Over (avançar para a próxima linha sem entrar na função) |
| `<F11>` | Step Into (entrar na função) |
| `<F12>` | Step Out (sair da função atual) |
| `<leader>dr` | Reiniciar Sessão de Debug |
| `<leader>dq` | Encerrar / Desconectar Debugger |
| `<leader>du` | Alternar Visibilidade do Painel Visual (`DapUI Toggle`) |
| `<leader>de` | Avaliar Expressão sob o cursor em popup flutuante |

---

## 5. Portabilidade e Compatibilidade Multiplataforma

### 1. Detecção Automática do Sistema Operacional (Lua):
```lua
local is_windows = vim.fn.has("win32") == 1
local is_wsl = vim.fn.has("wsl") == 1
local is_mac = vim.fn.has("mac") == 1
local is_linux = vim.fn.has("unix") == 1 and not is_mac and not is_wsl
```

### 2. Integração Universal com Clipboard do Sistema:
- **Linux (Wayland):** `wl-copy` / `wl-paste`
- **Linux (X11) & BSD:** `xclip` / `xsel`
- **macOS:** `pbcopy` / `pbpaste`
- **Windows Nativo:** `clip.exe` / PowerShell
- **WSL 2:** `win32yank.exe` (sincroniza a área de transferência do Linux instantaneamente com o Windows)

### 3. Integração com `mise`:
O Neovim herda automaticamente os shims do `mise` presentes no `PATH` (`~/.local/share/mise/shims`), garantindo que a versão correta do Java, Node, Go ou Rust de cada projeto seja selecionada sem necessidade de configuração manual.

### 4. SSH Remoto Sem Servidor Pesado:
Para trabalhar em servidores remotos:
```bash
# Conexão direta com repasse de agente SSH e terminal completo
ssh -A usuario@servidor.com
cd /meu-projeto && nvim .
```
O Neovim roda nativamente no terminal remoto, consumindo ~40MB de RAM e operando com resposta instantânea em qualquer conexão de rede.

---

## 6. Roteiro de Execução Passo a Passo (Fases de Implementação)

### 🟢 Fase 1: Fundação Estética e Navegação Ultra-Rápida
1. Instalar `lazy.nvim` no `init.lua`.
2. Configurar tema `catppuccin/nvim` (Macchiato/Mocha) com bordas flutuantes elegantes.
3. Configurar `lualine.nvim` (statusline com git, lsp e debug).
4. Configurar `bufferline.nvim` (abas de arquivos com ícones).
5. Configurar `noice.nvim` e `nui.nvim` para interface flutuante moderna.
6. Configurar `oil.nvim` para edição do sistema de arquivos como texto puro e `snacks.picker` / `fzf-lua` para busca instantânea de arquivos.

### 🟢 Fase 2: Motor de Código e Inteligência (LSP + Treesitter + Pnuts)
1. Instalar `nvim-treesitter` e compilar parsers essenciais (Rust, Go, Java, TS/React, Lua).
2. Adicionar parser local do `tree-sitter-pnuts` com queries de realce sintático.
3. Instalar `mason.nvim` e `nvim-lspconfig` para provisionamento automático de LSPs.
4. Conectar [`pnuts-lsp`](file:///home/chsilva/personal/projects/pnuts-tooling/target/release/pnuts-lsp) diretamente ao Neovim para arquivos `.pnuts`.
5. Instalar `blink.cmp` (motor de autocomplete ultraleve em Rust com fuzzy search e suporte a snippets).
6. Configurar `conform.nvim` com format-on-save para Topiary, Prettier, Gofumpt, Rustfmt, etc.

### 🟢 Fase 3: Estação de Depuração Completa (DAP)
1. Instalar `nvim-dap`, `nvim-dap-ui` e `nvim-dap-virtual-text`.
2. Configurar sinais visuais (ícones de breakpoint na lateral e linha ativa destacada).
3. Integrar `delve` para Go via `nvim-dap-go`.
4. Integrar `codelldb` para Rust e Zig.
5. Integrar `vscode-js-debug` para aplicações Node.js e React/Vite.
6. Mapear as teclas de atalho de debug padronizadas (`F5`, `F9`, `F10`, `F11`, `F12`).

### 🟢 Fase 4: Especializações Enterprise (Java / Kotlin / Testes)
1. Configurar `nvim-jdtls` para projetos Maven/Gradle complexos (Quarkus, Spring Boot).
2. Configurar `java-debug-adapter` e suporte a execução de testes JUnit.
3. Instalar `neotest` para execução de testes unitários com diagnósticos inline e visualização de árvore de testes.

### 🟢 Fase 5: Versionamento em Dotfiles e Distribuição
1. Criar repositório `dotfiles` (ex: `~/.dotfiles/nvim` ou repositório no GitHub/GitLab).
2. Criar script de instalação de 1 comando (`install.sh` / `install.ps1`) para clonar e rodar o Neovim com tudo pronto em qualquer máquina.
3. Testar a portabilidade em Linux, WSL e macOS.

---

Este guia é a especificação oficial de engenharia para a construção da nossa IDE portável no Neovim.
