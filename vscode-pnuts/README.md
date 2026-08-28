# Pnuts Language Support for VSCode

Syntax highlighting and Topiary-based code formatting for the **Pnuts** scripting language (`.pnuts`, `.pnut`).

## Features

- **Syntax Highlighting**: Complete TextMate grammar covering Pnuts syntax (keywords, functions, types, operators, static methods, closures, template literals, comments).
- **Code Formatting**: Integrated `DocumentFormattingEditProvider` backed by Topiary.
- **Language Configuration**: Auto-closing brackets, comments toggle (`//` and `/* */`), and indentation rules.

## Requirements

- `topiary` CLI installed and available in your `PATH` (e.g. `cargo install topiary-cli`).
  - **Remote SSH / WSL Users**: When running VS Code in WSL or Remote-SSH mode, Topiary must be installed in the **remote environment** (inside the WSL distribution or on the remote SSH server), not only on the Windows host.

## Extension Settings

This extension contributes the following settings:

* `pnuts.topiary.path`: Path to the Topiary executable (default: `"topiary"`).
* `pnuts.topiary.configPath`: Path to `languages.ncl` (falls back automatically to bundled grammar configuration).
* `pnuts.topiary.queryPath`: Path to the queries directory (falls back automatically to bundled queries).

## Usage

1. Open any `.pnuts` file.
2. Format on demand via **Shift + Alt + F** (or right click -> *Format Document*).
3. Enable "Format on Save" in VSCode settings if desired.
