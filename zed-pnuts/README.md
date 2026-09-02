# Pnuts Language Support for Zed

Language extension for [Zed](https://zed.dev) providing syntax highlighting, symbol outlines, brackets, automatic indentation, and zero-dependency standalone Topiary-based code formatting for **Pnuts** (`.pnuts`, `.pnut`).

## Features

- **Tree-sitter Syntax Highlighting**: Accurate highlighting for Pnuts language constructs, expressions, keywords, types, closures, and Java interop syntax.
- **Symbol Outline & Navigation**: Outlines and breadcrumbs for functions, methods, classes, and packages.
- **Smart Indentation & Brackets**: Tree-sitter indent queries (`indents.scm`) and bracket matching (`brackets.scm`).
- **Pnuts File Icons**: Peanut vector icon (`icons/pnuts.svg`) and icon theme (`icon_themes/pnuts.json`) identifying `.pnuts` and `.pnut` scripts in tabs and file trees.
- **Zero-Dependency Code Formatting**: Cross-platform formatting powered by Topiary with pre-compiled binaries and Tree-sitter grammars bundled for **Linux (x64)**, **macOS (Apple Silicon arm64 & Intel x64)**, and **Windows (x64)**. No need to install Cargo, Rust, or system compilers.

---

## Installation in Zed

### Method 1: Install Dev Extension (Local Development)

1. Open **Zed**.
2. Open the Command Palette (`Ctrl+Shift+P` on Linux/Windows, `Cmd+Shift+P` on macOS).
3. Run **`zed: install dev extension`** (or **Extensions: Install Dev Extension**).
4. Select the `zed-pnuts` folder from your cloned repository.
5. If you update queries, grammars, or icons, open the Command Palette, run **`zed: extensions`**, find **Pnuts** under "Dev Extensions", and click **Rebuild**.

---

## Configuring File Icons in Zed

Unlike editors with per-language file icon associations, Zed manages file icons through **Icon Themes**. The extension includes the **Pnuts Icon Theme** with the peanut icon for `.pnuts` and `.pnut` files, along with fallback directory and generic file icons.

To activate the Pnuts Icon Theme:
1. Open the Command Palette (`Ctrl+Shift+P`).
2. Run **`icon theme selector: toggle`** and select **Pnuts Icon Theme** (or **Pnuts Icon Theme Light**).
3. Ensure file icons are enabled in your tabs and file tree by adding to your `settings.json`:
   ```json
   {
     "icon_theme": "Pnuts Icon Theme",
     "tabs": {
       "file_icons": true
     }
   }
   ```

---

## Configuring Formatting in Zed

Zed supports external formatters per language in `settings.json`.

### Option A: Using Workspace Settings (Zero Manual Config)

If you open the `pnuts-tooling` repository in Zed, the workspace `.zed/settings.json` is already configured and format-on-save works immediately out of the box!

### Option B: User Settings (`~/.config/zed/settings.json` or `%APPDATA%\Zed\settings.json`)

Add the following to your Zed `settings.json`:

#### On Linux / macOS:

```json
{
  "languages": {
    "Pnuts": {
      "formatter": {
        "external": {
          "command": "/absolute/path/to/pnuts-tooling/zed-pnuts/bin/pnuts-format",
          "arguments": []
        }
      },
      "format_on_save": "on",
      "tab_size": 2
    }
  }
}
```

#### On Windows:

```json
{
  "languages": {
    "Pnuts": {
      "formatter": {
        "external": {
          "command": "C:\\path\\to\\pnuts-tooling\\zed-pnuts\\bin\\pnuts-format.exe",
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

## Standalone Multiplatform Support

The extension bundles pre-compiled Topiary executables and dynamic grammar libraries:

| Platform | Architecture | Binary Path | Grammar Library |
| :--- | :--- | :--- | :--- |
| **Linux** | `x86_64` | `resources/bin/linux-x64/topiary` | `resources/grammars/linux-x64/libtree-sitter-pnuts.so` |
| **macOS** | `Apple Silicon (arm64)` | `resources/bin/darwin-arm64/topiary` | `resources/grammars/darwin-arm64/libtree-sitter-pnuts.dylib` |
| **macOS** | `Intel (x86_64)` | `resources/bin/darwin-x64/topiary` | `resources/grammars/darwin-x64/libtree-sitter-pnuts.dylib` |
| **Windows** | `x86_64` | `resources/bin/win32-x64/topiary.exe` | `resources/grammars/win32-x64/tree-sitter-pnuts.dll` |

---

## File Structure

```text
zed-pnuts/
├── extension.toml                     # Zed extension manifest
├── LICENSE                            # MIT License
├── README.md                          # Extension documentation
├── bin/
│   ├── format_pnuts                   # POSIX formatter runner
│   └── format_pnuts.cmd               # Windows formatter runner
├── languages/
│   └── pnuts/
│       ├── config.toml                # Language configuration
│       ├── highlights.scm             # Syntax highlighting query
│       ├── brackets.scm               # Bracket pairing query
│       ├── outline.scm                # Symbol outline query
│       └── indents.scm                # Indentation query
└── resources/
    ├── bin/                           # Bundled standalone Topiary binaries
    │   ├── darwin-arm64/topiary
    │   ├── darwin-x64/topiary
    │   ├── linux-x64/topiary
    │   └── win32-x64/topiary.exe
    ├── grammars/                      # Bundled dynamic grammar libraries
    │   ├── darwin-arm64/libtree-sitter-pnuts.dylib
    │   ├── darwin-x64/libtree-sitter-pnuts.dylib
    │   ├── linux-x64/libtree-sitter-pnuts.so
    │   └── win32-x64/tree-sitter-pnuts.dll
    └── queries/
        └── pnuts.scm                  # Topiary formatting query
```

## License

MIT
