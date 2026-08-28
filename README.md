# Pnuts Tooling

Tooling moderno para a linguagem de script **Pnuts** (`.pnuts`, `.pnut`):
- **Gramática Tree-sitter** tolerante a falhas (`tree-sitter-pnuts`)
- **Formatador de Código** baseado no Topiary (`.topiary/`)
- **Extensão para Visual Studio Code** (`vscode-pnuts/`)
- **Extensão para Zed** (`zed-pnuts/`)
- **Ferramenta CLI para Formatação em Lote** (`scripts/format_pnuts.py`)
- **📖 [Guia Completo da Linguagem Pnuts & Treinamento](docs/GUIA_DE_PNUTS.md)**

---

## 📦 Estrutura do Repositório

```text
.
├── .topiary/               # Configuração e queries do Topiary (languages.ncl, pnuts.scm)
├── pnuts-samples/          # Amostra de scripts .pnuts reais de teste
├── scripts/                # Scripts utilitários (format_pnuts.py)
├── tree-sitter-pnuts/      # Gramática Tree-sitter (grammar.js, parser C, tests)
├── vscode-pnuts/           # Extensão para VSCode (TextMate grammar + Formatter)
└── zed-pnuts/              # Extensão para Zed (Tree-sitter queries + Formatter)
```

---

## 🛠️ Pré-requisitos

1. **Rust & Cargo**:
   ```bash
   curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
   ```
2. **Tree-sitter CLI**:
   ```bash
   cargo install tree-sitter-cli
   ```
3. **Topiary CLI**:
   ```bash
   cargo install topiary-cli
   ```
4. **Node.js** (para a extensão VSCode):
   - Versão >= 18.0.0

---

## 🚀 Como Rodar o Formatter

### 1. Modo Seguro: "Apenas Diff" (Dry Run)

Para inspecionar as alterações de formatação sem modificar nenhum arquivo em disco:

```bash
# Inspecionar diffs na amostra padrão (pnuts-samples)
python3 scripts/format_pnuts.py --diff

# Inspecionar diffs em uma pasta específica do seu projeto
python3 scripts/format_pnuts.py --diff /caminho/para/seus/arquivos_pnuts
```

### 2. Formatação em Lote (Sobrescrita em Disco)

Após inspecionar o diff e aprovar as mudanças:

```bash
# Aplicar formatação em todos os arquivos de uma pasta
python3 scripts/format_pnuts.py --write /caminho/para/seus/arquivos_pnuts
```

### 3. Modo de Verificação (CI/Check)

Para validar se todos os arquivos estão devidamente formatados:

```bash
python3 scripts/format_pnuts.py --check /caminho/para/seus/arquivos_pnuts
```

### 4. Formatar um Arquivo Específico via CLI Topiary

```bash
TOPIARY_LANGUAGE_DIR=.topiary/queries topiary format -C .topiary/languages.ncl -l pnuts < arquivo.pnuts
```

---

## 💻 Instalação das Extensões

### Visual Studio Code

1. Abra a pasta `vscode-pnuts` ou crie um link simbólico na pasta de extensões do VSCode:
   ```bash
   ln -s "$(pwd)/vscode-pnuts" ~/.vscode/extensions/pnuts-language-support
   ```
2. Ou empacote e instale via `vsce`:
   ```bash
   cd vscode-pnuts
   npx @vscode/vsce package
   code --install-extension vscode-pnuts-0.1.0.vsix
   ```
3. Reinicie o VSCode.
4. Abra qualquer arquivo `.pnuts` e pressione **Shift + Alt + F** para formatar.

### Zed

1. Abra o **Zed**.
2. Abra a paleta de comandos (`Ctrl+Shift+P` / `Cmd+Shift+P`).
3. Digite e selecione: `Extensions: Install Dev Extension`.
4. Selecione a pasta `zed-pnuts/`.
5. No seu arquivo de configurações do Zed (`settings.json`), adicione a configuração do formatter externo:

```json
{
  "languages": {
    "Pnuts": {
      "formatter": {
        "external": {
          "command": "topiary",
          "arguments": [
            "format",
            "-C",
            "/caminho/absoluto/para/pnuts-tooling/.topiary/languages.ncl",
            "-l",
            "pnuts"
          ]
        }
      },
      "format_on_save": "on"
    }
  }
}
```

---

## 🧪 Rodando os Testes da Gramática

```bash
cd tree-sitter-pnuts
tree-sitter test
tree-sitter parse ../pnuts-samples/*.pnuts --quiet --stat
```
