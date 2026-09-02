#!/usr/bin/env bash
# Pnuts Formatter Runner for Linux & macOS
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec python3 "$DIR/format_pnuts.py" "$@"
