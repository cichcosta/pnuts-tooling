const vscode = require('vscode');
const { spawn, execSync } = require('child_process');
const path = require('path');
const fs = require('fs');
const os = require('os');

/**
 * Expands tilde (~) to the user's home directory.
 */
function expandPath(filePath) {
    if (!filePath) return filePath;
    if (filePath === '~') return os.homedir();
    if (filePath.startsWith('~/') || filePath.startsWith('~\\')) {
        return path.join(os.homedir(), filePath.slice(2));
    }
    return filePath;
}

/**
 * Resolves the Topiary binary executable path.
 */
function resolveBinary(configuredPath) {
    if (configuredPath && configuredPath !== 'topiary') {
        const expanded = expandPath(configuredPath);
        if (fs.existsSync(expanded)) {
            return expanded;
        }
        return expanded;
    }

    const home = os.homedir();
    const isWindows = process.platform === 'win32';
    const binName = isWindows ? 'topiary.exe' : 'topiary';

    const candidatePaths = [];

    // Environment variables
    if (process.env.CARGO_HOME) {
        candidatePaths.push(path.join(process.env.CARGO_HOME, 'bin', binName));
    }
    if (process.env.LOCALAPPDATA && isWindows) {
        candidatePaths.push(path.join(process.env.LOCALAPPDATA, 'Programs', 'topiary', binName));
    }
    if (process.env.USERPROFILE && isWindows) {
        candidatePaths.push(path.join(process.env.USERPROFILE, '.cargo', 'bin', binName));
    }

    // Common standard paths
    candidatePaths.push(
        path.join(home, '.cargo', 'bin', binName),
        path.join(home, '.local', 'bin', binName),
        path.join(home, '.nix-profile', 'bin', binName),
        path.join(home, '.asdf', 'shims', binName),
        '/usr/local/bin/topiary',
        '/usr/bin/topiary',
        '/snap/bin/topiary',
        '/home/linuxbrew/.linuxbrew/bin/topiary',
        '/nix/var/nix/profiles/default/bin/topiary'
    );

    for (const candidate of candidatePaths) {
        try {
            if (candidate && fs.existsSync(candidate)) {
                return candidate;
            }
        } catch (e) {
            // Ignore search errors
        }
    }

    // Fallback: try system which/where command
    try {
        const checkCmd = isWindows ? 'where topiary' : 'which topiary';
        const found = execSync(checkCmd, { encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] }).trim().split(/\r?\n/)[0];
        if (found && fs.existsSync(found)) {
            return found;
        }
    } catch (e) {
        // 'which' or 'where' failed, fallback to default 'topiary'
    }

    return 'topiary';
}

/**
 * Finds configuration, query paths, and workspace root for Topiary.
 * @param {vscode.ExtensionContext} [context]
 */
function resolveTopiaryOptions(context) {
    const config = vscode.workspace.getConfiguration('pnuts.topiary');
    const rawBinary = config.get('path') || 'topiary';
    const binaryPath = resolveBinary(rawBinary);

    let configPath = expandPath(config.get('configPath') || '');
    let queryPath = expandPath(config.get('queryPath') || '');
    let workspaceRoot = undefined;

    if (vscode.workspace.workspaceFolders && vscode.workspace.workspaceFolders.length > 0) {
        workspaceRoot = vscode.workspace.workspaceFolders[0].uri.fsPath;
    }

    // 1. Resolve query path: workspace folder -> bundled resources
    if (!queryPath && vscode.workspace.workspaceFolders) {
        for (const folder of vscode.workspace.workspaceFolders) {
            const possibleQuery = path.join(folder.uri.fsPath, '.topiary', 'queries');
            if (fs.existsSync(possibleQuery)) {
                queryPath = possibleQuery;
                break;
            }
        }
    }
    if (!queryPath && context) {
        const bundledQuery = path.join(context.extensionPath, 'resources', 'queries');
        if (fs.existsSync(bundledQuery)) {
            queryPath = bundledQuery;
        }
    }

    // 2. Resolve config path: workspace folder -> dynamic generation from bundled grammar
    if (!configPath && vscode.workspace.workspaceFolders) {
        for (const folder of vscode.workspace.workspaceFolders) {
            const possibleConfig = path.join(folder.uri.fsPath, '.topiary', 'languages.ncl');
            if (fs.existsSync(possibleConfig)) {
                configPath = possibleConfig;
                break;
            }
        }
    }

    // Fallback: generate dynamic configuration pointing to bundled Tree-sitter grammar
    if (!configPath && context) {
        const ext = process.platform === 'win32' ? '.dll' : (process.platform === 'darwin' ? '.dylib' : '.so');
        const candidateGrammars = [
            path.join(context.extensionPath, 'resources', 'grammars', `libtree-sitter-pnuts${ext}`),
            path.join(context.extensionPath, 'resources', 'grammars', 'libtree-sitter-pnuts.so')
        ];

        const grammarPath = candidateGrammars.find(g => fs.existsSync(g));
        if (grammarPath) {
            const storageDir = context.globalStorageUri ? context.globalStorageUri.fsPath : path.join(os.tmpdir(), 'vscode-pnuts');
            try {
                if (!fs.existsSync(storageDir)) {
                    fs.mkdirSync(storageDir, { recursive: true });
                }
                const generatedConfigPath = path.join(storageDir, 'languages.ncl');
                const escapedGrammarPath = grammarPath.replace(/\\/g, '/');
                const nclContent = `{\n  languages = {\n    pnuts = {\n      extensions = ["pnuts"],\n      grammar = {\n        source = {\n          path = "${escapedGrammarPath}",\n        },\n        symbol = "tree_sitter_pnuts",\n      },\n      indent = "  ",\n    },\n  },\n}\n`;
                fs.writeFileSync(generatedConfigPath, nclContent, 'utf8');
                configPath = generatedConfigPath;
            } catch (err) {
                console.error('[vscode-pnuts] Failed to generate dynamic languages.ncl:', err);
            }
        }
    }

    return { binaryPath, configPath, queryPath, workspaceRoot };
}

/**
 * Formats Pnuts source code using Topiary CLI.
 * @param {string} text - Original document content
 * @param {vscode.ExtensionContext} [context] - Extension context
 * @returns {Promise<string>} - Formatted document content
 */
function formatWithTopiary(text, context) {
    return new Promise((resolve, reject) => {
        const { binaryPath, configPath, queryPath, workspaceRoot } = resolveTopiaryOptions(context);

        const args = ['format', '-l', 'pnuts'];
        if (configPath) {
            args.push('-C', configPath);
        }

        // Augment PATH with common binary locations
        const cargoBin = path.join(os.homedir(), '.cargo', 'bin');
        const localBin = path.join(os.homedir(), '.local', 'bin');
        const envPath = [cargoBin, localBin, process.env.PATH].filter(Boolean).join(path.delimiter);

        const env = Object.assign({}, process.env, { PATH: envPath });
        if (queryPath) {
            env.TOPIARY_LANGUAGE_DIR = queryPath;
        }

        const spawnOptions = {
            env,
            cwd: workspaceRoot || process.cwd()
        };

        const child = spawn(binaryPath, args, spawnOptions);

        let stdout = '';
        let stderr = '';

        child.stdout.on('data', (data) => {
            stdout += data.toString();
        });

        child.stderr.on('data', (data) => {
            stderr += data.toString();
        });

        child.on('error', (err) => {
            if (err.code === 'ENOENT') {
                const remote = vscode.env && vscode.env.remoteName;
                let hint = '';
                if (remote === 'wsl') {
                    const distro = process.env.WSL_DISTRO_NAME ? ` (${process.env.WSL_DISTRO_NAME})` : '';
                    hint = `VS Code is running in WSL${distro}. Topiary must be installed inside your WSL Linux environment (e.g. run 'cargo install topiary-cli' in the WSL terminal) and accessible in PATH, or set 'pnuts.topiary.path' in VS Code Settings (WSL tab).`;
                } else if (remote === 'ssh-remote') {
                    hint = `VS Code is connected via Remote - SSH. Topiary must be installed on the remote SSH machine (e.g. run 'cargo install topiary-cli' in the remote SSH terminal) and accessible in PATH, or set 'pnuts.topiary.path' in VS Code Settings (Remote SSH tab).`;
                } else {
                    hint = `Ensure Topiary is installed (e.g. 'cargo install topiary-cli') and accessible in PATH, or configure 'pnuts.topiary.path' in VS Code settings.`;
                }
                reject(new Error(
                    `Topiary binary not found ('${binaryPath}'). ${hint}`
                ));
            } else {
                reject(new Error(`Failed to launch Topiary binary ('${binaryPath}'): ${err.message}`));
            }
        });

        child.on('close', (code) => {
            if (code === 0) {
                resolve(stdout);
            } else {
                reject(new Error(`Topiary exited with code ${code}: ${stderr || stdout}`));
            }
        });

        child.stdin.write(text);
        child.stdin.end();
    });
}

/**
 * Activates the VSCode extension.
 * @param {vscode.ExtensionContext} context
 */
function activate(context) {
    const formatter = vscode.languages.registerDocumentFormattingEditProvider('pnuts', {
        async provideDocumentFormattingEdits(document) {
            const text = document.getText();
            try {
                const formatted = await formatWithTopiary(text, context);
                const fullRange = new vscode.Range(
                    document.positionAt(0),
                    document.positionAt(text.length)
                );
                return [vscode.TextEdit.replace(fullRange, formatted)];
            } catch (err) {
                vscode.window.showErrorMessage(`Pnuts Formatter Error: ${err.message}`);
                return [];
            }
        }
    });

    context.subscriptions.push(formatter);
}

function deactivate() {}

module.exports = {
    activate,
    deactivate,
    resolveBinary,
    resolveTopiaryOptions,
    formatWithTopiary
};
