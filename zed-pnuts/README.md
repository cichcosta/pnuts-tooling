# Pnuts Language Extension for Zed

Language extension for [Zed](https://zed.dev) providing syntax highlighting, symbol outlines, brackets, and formatter integration for **Pnuts** (`.pnuts`).

## Features

- **Grammar & Highlighting**: Powered by Tree-sitter (`tree-sitter-pnuts`).
- **Code Navigation**: Outline and symbol navigation for functions, classes, and methods.
- **Topiary Formatter Integration**: Configurable via Zed external formatter settings.

## Installation / Development in Zed

1. Open Zed.
2. Run command **Extensions: Install Dev Extension** (`cmd-shift-p` / `ctrl-shift-p`).
3. Select the `zed-pnuts` folder.

## Configuring the Formatter in Zed

In your Zed `settings.json`, configure the Topiary external formatter for Pnuts:

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
            "/absolute/path/to/pnuts-tooling/.topiary/languages.ncl",
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
