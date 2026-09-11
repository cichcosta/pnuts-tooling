#!/usr/bin/env bash
# Pnuts Formatter Runner for Linux & macOS
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -x "$DIR/../zed-pnuts/bin/pnuts-format" ]; then
    exec "$DIR/../zed-pnuts/bin/pnuts-format" "$@"
elif command -v python3 >/dev/null 2>&1 && [ -f "$DIR/format_pnuts.py" ]; then
    exec python3 "$DIR/format_pnuts.py" "$@"
fi
