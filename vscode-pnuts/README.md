# Pnuts Language Support for VSCode

Syntax highlighting and Topiary-based code formatting for the **Pnuts** scripting language (`.pnuts`, `.pnut`).

## Features

- **Syntax Highlighting**: Complete TextMate grammar covering Pnuts syntax (keywords, functions, types, operators, static methods, closures, template literals, comments).
- **Code Formatting**: Integrated `DocumentFormattingEditProvider` backed by Topiary.
- **Language Configuration**: Auto-closing brackets, comments toggle (`//` and `/* */`), and indentation rules.

## Requirements

- **Zero configuration / Zero external installation required!**
  The extension now comes pre-packaged with standalone Topiary binaries and Tree-sitter grammars for Windows (x64), Linux (x64), and macOS (Apple Silicon & Intel).
- Optional: If you prefer using your own system-installed Topiary build, you can set `pnuts.topiary.path` in VS Code settings.

## Extension Settings

This extension contributes the following optional settings:

* `pnuts.topiary.path`: Path to custom Topiary executable (default: auto-detects bundled binary).
* `pnuts.topiary.configPath`: Path to custom `languages.ncl` (default: auto-generated from bundled grammar).
* `pnuts.topiary.queryPath`: Path to custom queries directory (default: auto-detected from bundled `pnuts.scm`).

## Usage

1. Open any `.pnuts` file.
2. Format on demand via **Shift + Alt + F** (or right click -> *Format Document*).
3. Enable "Format on Save" in VSCode settings if desired.
