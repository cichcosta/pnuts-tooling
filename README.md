# Pnuts Tooling

Tooling moderno e multiplataforma para a linguagem de script **Pnuts** (`.pnuts`, `.pnut`):
- **Gramática Tree-sitter** tolerante a falhas (`tree-sitter-pnuts`)
- **Formatador de Código Standalone** baseado no Topiary com suporte a **Linux (x64)**, **macOS (Apple Silicon arm64 & Intel x64)** e **Windows (x64)**
- **Extensão para Visual Studio Code** (`vscode-pnuts/`) - *Zero dependências externas do SO*
- **Extensão para Zed** (`zed-pnuts/`) - *Zero dependências externas do SO*
- **Ferramenta CLI para Formatação Individual e em Lote** (`scripts/format_pnuts.py`)
- **📖 [Guia Completo da Linguagem Pnuts & Treinamento](docs/GUIA_DE_PNUTS.md)**

---

## 📦 Estrutura do Repositório

```text
.
├── .topiary/               # Configuração e queries do Topiary (languages.ncl, pnuts.scm)
├── .zed/                   # Configuração de workspace para Zed (settings.json)
├── docs/                   # Documentação detalhada e guia de treinamento Pnuts
├── pnuts-samples/          # Amostra de scripts .pnuts reais de teste
├── scripts/                # Scripts utilitários (format_pnuts.py, format_pnuts.sh, format_pnuts.cmd)
├── tree-sitter-pnuts/      # Gramática Tree-sitter (grammar.js, parser C, tests)
├── vscode-pnuts/           # Extensão para VSCode (TextMate grammar + Formatter standalone)
└── zed-pnuts/              # Extensão para Zed (Tree-sitter queries + Formatter standalone)
```

---

## 🚀 Formatação de Código (CLI)

O formatador resolve automaticamente os binários pré-compilados do Topiary e a biblioteca dinâmica do Tree-sitter Pnuts para o seu sistema operacional (Linux, macOS, Windows). Não é necessário instalar Cargo, Rust ou compiladores C.

### 1. Formatação via Stdin / Pipe (Zed, Editores, Pipes)

```bash
# Stdin para Stdout
python3 scripts/format_pnuts.py --stdin < arquivo.pnuts > formatado.pnuts
cat arquivo.pnuts | python3 scripts/format_pnuts.py
```

### 2. Modo Seguro: "Apenas Diff" (Dry Run)

Para inspecionar as alterações de formatação sem modificar nenhum arquivo em disco:

```bash
# Inspecionar diffs na amostra padrão (pnuts-samples)
python3 scripts/format_pnuts.py --diff

# Inspecionar diffs em uma pasta específica do seu projeto
python3 scripts/format_pnuts.py --diff /caminho/para/seus/arquivos_pnuts
```

### 3. Formatação em Lote (Sobrescrita em Disco)

Após inspecionar o diff e aprovar as mudanças:

```bash
# Aplicar formatação em todos os arquivos de uma pasta
python3 scripts/format_pnuts.py --write /caminho/para/seus/arquivos_pnuts
```

### 4. Modo de Verificação (CI/Check)

Para validar se todos os arquivos estão devidamente formatados:

```bash
python3 scripts/format_pnuts.py --check /caminho/para/seus/arquivos_pnuts
```

---

## 💻 Extensões para Editores

### 1. Visual Studio Code (`vscode-pnuts`)

Extensão completa com realce de sintaxe TextMate, configuração de linguagem e provedor de formatação integrado (`DocumentFormattingEditProvider`). Utiliza o binário Topiary standalone embutido para Windows, Linux e macOS sem necessidade de instalações extras do sistema.

#### Instalação:

```bash
# Opção A: Link simbólico para desenvolvimento
ln -s "$(pwd)/vscode-pnuts" ~/.vscode/extensions/pnuts-language-support

# Opção B: Instalar o pacote VSIX pronto
code --install-extension vscode-pnuts/vscode-pnuts-0.2.3.vsix
```

Pressione **Shift + Alt + F** (ou ative `editor.formatOnSave`) em qualquer arquivo `.pnuts`.

---

### 2. Zed (`zed-pnuts`)

Extensão para o editor Zed fornecendo realce de sintaxe via Tree-sitter (`highlights.scm`), outline de símbolos (`outline.scm`), emparelhamento de colchetes (`brackets.scm`), indentação inteligente (`indents.scm`) e integração com o formatador Topiary standalone.

#### Instalação:

1. Abra o **Zed**.
2. Abra a paleta de comandos (`Ctrl+Shift+P` no Linux/Windows, `Cmd+Shift+P` no macOS).
3. Selecione **`zed: install dev extension`** (ou `Extensions: Install Dev Extension`).
4. Selecione a pasta `zed-pnuts/`.

#### Configuração do Formatador no Zed:

##### No Linux / macOS (`~/.config/zed/settings.json`):

```json
{
  "languages": {
    "Pnuts": {
      "formatter": {
        "external": {
          "command": "/caminho/absoluto/para/pnuts-tooling/zed-pnuts/bin/format_pnuts",
          "arguments": []
        }
      },
      "format_on_save": "on",
      "tab_size": 2
    }
  }
}
```

##### No Windows (`%APPDATA%\Zed\settings.json`):

```json
{
  "languages": {
    "Pnuts": {
      "formatter": {
        "external": {
          "command": "C:\\caminho\\para\\pnuts-tooling\\zed-pnuts\\bin\\format_pnuts.cmd",
          "arguments": []
        }
      },
      "format_on_save": "on",
      "tab_size": 2
    }
  }
}
```

---

## 🛠️ Desenvolvimento da Gramática Tree-sitter (Opcional)

Apenas necessário se você desejar alterar e recompilar a gramática Tree-sitter:

1. **Rust & Cargo**:
   ```bash
   curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
   ```
2. **Tree-sitter CLI**:
   ```bash
   cargo install tree-sitter-cli
   ```
3. **Executar Testes**:
   ```bash
   cd tree-sitter-pnuts
   tree-sitter test
   tree-sitter parse ../pnuts-samples/*.pnuts --quiet --stat
   ```

---

## 📄 Licença

Distribuído sob a licença [MIT](LICENSE).
