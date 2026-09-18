#!/usr/bin/env bash
# ==============================================================================
# Unified Installer and Cleaner for Pnuts & Groovy Tooling in Zed (Linux / macOS)
# ==============================================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "=== Instalador Unificado Pnuts Tooling para Zed (Linux / macOS) ==="

# 1. Instalar binários em ~/.local/bin
TARGET_BIN_DIR="$HOME/.local/bin"
mkdir -p "$TARGET_BIN_DIR"

echo "[1/5] Instalando binários em $TARGET_BIN_DIR..."
if [ -f "$REPO_ROOT/target/release/pnuts-lsp" ]; then
    cp "$REPO_ROOT/target/release/pnuts-lsp" "$TARGET_BIN_DIR/pnuts-lsp"
elif [ -f "$REPO_ROOT/zed-pnuts/bin/pnuts-lsp" ]; then
    cp "$REPO_ROOT/zed-pnuts/bin/pnuts-lsp" "$TARGET_BIN_DIR/pnuts-lsp"
fi

if [ -f "$REPO_ROOT/zed-pnuts/bin/pnuts-format" ]; then
    cp "$REPO_ROOT/zed-pnuts/bin/pnuts-format" "$TARGET_BIN_DIR/pnuts-format"
fi

chmod +x "$TARGET_BIN_DIR/pnuts-lsp" "$TARGET_BIN_DIR/pnuts-format" 2>/dev/null || true

# 2. Garantir PATH universal em ~/.zshenv, ~/.bashrc e ~/.zshrc
echo "[2/5] Configurando PATH persistente para sessões interativas e SSH..."
for RC_FILE in "$HOME/.zshenv" "$HOME/.bashrc" "$HOME/.zshrc"; do
    if [ -f "$RC_FILE" ] || [ "$RC_FILE" = "$HOME/.zshenv" ]; then
        if ! grep -q 'export PATH=.*\.local/bin' "$RC_FILE" 2>/dev/null; then
            echo 'export PATH="$HOME/.local/bin:$PATH"' >> "$RC_FILE"
            echo "   -> Adicionado PATH em $RC_FILE"
        fi
    fi
done

export PATH="$TARGET_BIN_DIR:$PATH"

# 3. Limpar caches/extensões corrompidas do Zed
echo "[3/5] Limpando caches e extensões antigas do Zed..."
rm -rf "$HOME/.local/share/zed/remote_extensions/pnuts" 2>/dev/null || true

# 4. Atualizar settings.json do Zed com configuração limpa e multiplataforma
echo "[4/5] Atualizando configurações do Zed..."
python3 -c "
import json, os

settings_paths = [
    os.path.expanduser('~/.config/zed/settings.json'),
    os.path.join('$REPO_ROOT', '.zed/settings.json')
]

for p in settings_paths:
    if not os.path.exists(p):
        continue
    try:
        with open(p, 'r', encoding='utf-8') as f:
            data = json.load(f)
    except Exception:
        data = {}

    if 'languages' not in data:
        data['languages'] = {}

    data['languages']['Pnuts'] = {
        'language_servers': ['pnuts-lsp', '...'],
        'formatter': {
            'external': {
                'command': 'pnuts-lsp',
                'arguments': ['--stdin']
            }
        },
        'format_on_save': 'on',
        'tab_size': 2
    }

    data['languages']['Groovy'] = {
        'language_servers': ['pnuts-lsp', '...'],
        'formatter': {
            'external': {
                'command': 'pnuts-lsp',
                'arguments': ['--stdin']
            }
        },
        'format_on_save': 'on',
        'tab_size': 2
    }

    if 'file_types' not in data:
        data['file_types'] = {}
    data['file_types']['Pnuts'] = ['pnuts', 'pnut']
    data['file_types']['Groovy'] = ['groovy']

    with open(p, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2)
    print(f'   -> Configuração limpa salva em {p}')
"

# 5. Teste de validação
echo "[5/5] Validando formatação via stdin..."
TEST_INPUT='function teste(a,b){return a+b;}'
FORMATTED_OUTPUT=$(echo "$TEST_INPUT" | "$TARGET_BIN_DIR/pnuts-lsp" --stdin)

if echo "$FORMATTED_OUTPUT" | grep -q 'function teste(a, b)'; then
    echo "========================================================"
    echo "✅ Instalação e higienização concluídas com SUCESSO!"
    echo "O comando 'pnuts-lsp' está pronto e operacional no Linux."
    echo "========================================================"
else
    echo "⚠️ Aviso: A validação do comando retornou saída inesperada."
    exit 1
fi
