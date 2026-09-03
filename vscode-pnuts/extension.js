const vscode = require('vscode');
const { spawn, execSync } = require('child_process');
const path = require('path');
const fs = require('fs');
const os = require('os');

let outputChannel = null;

function getOutputChannel() {
    if (!outputChannel) {
        outputChannel = vscode.window.createOutputChannel('Pnuts Formatter');
    }
    return outputChannel;
}

function log(msg) {
    try {
        const channel = getOutputChannel();
        channel.appendLine(`[${new Date().toISOString()}] ${msg}`);
    } catch (e) {
        console.log(`[vscode-pnuts] ${msg}`);
    }
}

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
 * Normalizes system platform and architecture info.
 */
function getPlatformInfo() {
    const platform = process.platform;
    const arch = process.arch;
    return {
        platform,
        arch,
        key: `${platform}-${arch}`,
        isWindows: platform === 'win32',
        isDarwin: platform === 'darwin',
        isLinux: platform === 'linux'
    };
}

/**
 * Resolves the Topiary binary executable path.
 * Priority:
 * 1. Explicitly configured path in 'pnuts.topiary.path' (if valid and exists on disk)
 * 2. Bundled binary inside extension resources/bin/<platform-arch>/
 * 3. Standard candidate paths (~/.cargo/bin, LocalAppData, /usr/local/bin, etc.)
 * 4. PATH lookup via 'where' / 'which'
 * 5. Fallback string 'topiary'
 */
function resolveBinary(context, configuredPath) {
    const { platform, key, isWindows } = getPlatformInfo();
    const binName = isWindows ? 'topiary.exe' : 'topiary';

    // 1. Explicitly configured path that exists
    if (configuredPath && configuredPath !== 'topiary') {
        const expanded = expandPath(configuredPath);
        if (fs.existsSync(expanded)) {
            log(`Using configured Topiary binary: ${expanded}`);
            return expanded;
        }
        log(`Configured path "${configuredPath}" not found on disk. Checking bundled binary...`);
    }

    // 2. Bundled binary inside extension resources
    if (context && context.extensionPath) {
        const candidateBundledPaths = [
            path.join(context.extensionPath, 'resources', 'bin', key, binName),
            path.join(context.extensionPath, 'resources', 'bin', `${platform}-x64`, binName),
            path.join(context.extensionPath, 'resources', 'bin', binName)
        ];

        for (const candidate of candidateBundledPaths) {
            try {
                if (fs.existsSync(candidate)) {
                    if (!isWindows) {
                        try {
                            fs.chmodSync(candidate, 0o755);
                        } catch (e) {
                            // Non-fatal if chmod fails
                        }
                    }
                    log(`Using bundled Topiary binary: ${candidate}`);
                    return candidate;
                }
            } catch (e) {
                // Ignore search errors
            }
        }
    }

    // 3. Common system candidate paths
    const home = os.homedir();
    const candidatePaths = [];

    if (process.env.CARGO_HOME) {
        candidatePaths.push(path.join(process.env.CARGO_HOME, 'bin', binName));
    }
    if (process.env.LOCALAPPDATA && isWindows) {
        candidatePaths.push(path.join(process.env.LOCALAPPDATA, 'Programs', 'topiary', binName));
    }
    if (process.env.USERPROFILE && isWindows) {
        candidatePaths.push(path.join(process.env.USERPROFILE, '.cargo', 'bin', binName));
    }

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
                log(`Found system Topiary binary: ${candidate}`);
                return candidate;
            }
        } catch (e) {
            // Ignore search errors
        }
    }

    // 4. System which / where
    try {
        const checkCmd = isWindows ? 'where topiary' : 'which topiary';
        const found = execSync(checkCmd, { encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] }).trim().split(/\r?\n/)[0];
        if (found && fs.existsSync(found)) {
            log(`Found Topiary binary in PATH: ${found}`);
            return found;
        }
    } catch (e) {
        // 'which' or 'where' failed
    }

    // Fallback if user explicitly supplied a path (even if not verified by fs.existsSync yet)
    if (configuredPath && configuredPath !== 'topiary') {
        return expandPath(configuredPath);
    }

    log(`Topiary binary fallback to 'topiary'`);
    return 'topiary';
}

/**
 * Resolves the Tree-sitter grammar library file path.
 */
function resolveGrammarPath(context) {
    const { platform, key, isWindows, isDarwin } = getPlatformInfo();
    const dllName = isWindows ? 'tree-sitter-pnuts.dll' : (isDarwin ? 'libtree-sitter-pnuts.dylib' : 'libtree-sitter-pnuts.so');
    const altDllName = isWindows ? 'libtree-sitter-pnuts.dll' : dllName;

    // Check workspace first if available
    if (vscode.workspace.workspaceFolders) {
        for (const folder of vscode.workspace.workspaceFolders) {
            const wsCandidates = [
                path.join(folder.uri.fsPath, '.topiary', 'grammars', dllName),
                path.join(folder.uri.fsPath, 'tree-sitter-pnuts', dllName),
                path.join(folder.uri.fsPath, 'tree-sitter-pnuts', altDllName)
            ];
            for (const wsCandidate of wsCandidates) {
                if (fs.existsSync(wsCandidate)) {
                    log(`Using workspace Tree-sitter grammar: ${wsCandidate}`);
                    return wsCandidate;
                }
            }
        }
    }

    // Bundled grammars inside extension resources
    if (context && context.extensionPath) {
        const bundledCandidates = [
            path.join(context.extensionPath, 'resources', 'grammars', key, dllName),
            path.join(context.extensionPath, 'resources', 'grammars', `${platform}-x64`, dllName),
            path.join(context.extensionPath, 'resources', 'grammars', dllName),
            path.join(context.extensionPath, 'resources', 'grammars', altDllName),
            path.join(context.extensionPath, 'resources', 'grammars', 'win32-x64', 'tree-sitter-pnuts.dll'),
            path.join(context.extensionPath, 'resources', 'grammars', 'linux-x64', 'libtree-sitter-pnuts.so'),
            path.join(context.extensionPath, 'resources', 'grammars', 'darwin-arm64', 'libtree-sitter-pnuts.dylib'),
            path.join(context.extensionPath, 'resources', 'grammars', 'darwin-x64', 'libtree-sitter-pnuts.dylib')
        ];

        for (const candidate of bundledCandidates) {
            if (fs.existsSync(candidate)) {
                log(`Using bundled Tree-sitter grammar: ${candidate}`);
                return candidate;
            }
        }
    }

    log(`No Tree-sitter grammar dynamic library found.`);
    return null;
}

/**
 * Resolves the Topiary query file (pnuts.scm).
 */
function resolveQueryPath(context) {
    const config = vscode.workspace.getConfiguration('pnuts.topiary');
    const configuredQuery = expandPath(config.get('queryPath') || '');
    if (configuredQuery && fs.existsSync(configuredQuery)) {
        try {
            const stat = fs.statSync(configuredQuery);
            if (stat.isDirectory()) {
                const inDir = path.join(configuredQuery, 'pnuts.scm');
                if (fs.existsSync(inDir)) return inDir;
            }
            return configuredQuery;
        } catch (e) {
            return configuredQuery;
        }
    }

    // Check workspace .topiary/queries/pnuts.scm
    if (vscode.workspace.workspaceFolders) {
        for (const folder of vscode.workspace.workspaceFolders) {
            const wsQuery = path.join(folder.uri.fsPath, '.topiary', 'queries', 'pnuts.scm');
            if (fs.existsSync(wsQuery)) {
                log(`Using workspace query: ${wsQuery}`);
                return wsQuery;
            }
        }
    }

    // Bundled query
    if (context && context.extensionPath) {
        const bundledQuery = path.join(context.extensionPath, 'resources', 'queries', 'pnuts.scm');
        if (fs.existsSync(bundledQuery)) {
            log(`Using bundled query: ${bundledQuery}`);
            return bundledQuery;
        }
    }

    log(`No Topiary query file found.`);
    return null;
}

/**
 * Resolves or generates the languages.ncl configuration for Topiary.
 */
function resolveConfigPath(context, grammarPath) {
    const config = vscode.workspace.getConfiguration('pnuts.topiary');
    const configuredConfig = expandPath(config.get('configPath') || '');
    if (configuredConfig && fs.existsSync(configuredConfig)) {
        log(`Using configured languages.ncl: ${configuredConfig}`);
        return configuredConfig;
    }

    // Check workspace .topiary/languages.ncl
    if (vscode.workspace.workspaceFolders) {
        for (const folder of vscode.workspace.workspaceFolders) {
            const wsConfig = path.join(folder.uri.fsPath, '.topiary', 'languages.ncl');
            if (fs.existsSync(wsConfig)) {
                log(`Using workspace languages.ncl: ${wsConfig}`);
                return wsConfig;
            }
        }
    }

    // Dynamically generate languages.ncl pointing to resolved grammar
    if (grammarPath) {
        const storageDir = (context && context.globalStorageUri)
            ? context.globalStorageUri.fsPath
            : path.join(os.tmpdir(), 'vscode-pnuts');

        try {
            if (!fs.existsSync(storageDir)) {
                fs.mkdirSync(storageDir, { recursive: true });
            }
            const generatedConfigPath = path.join(storageDir, 'languages.ncl');
            // Normalize path to forward slashes for Nickel cross-platform parsing
            const escapedGrammarPath = grammarPath.replace(/\\/g, '/');
            const nclContent = `{\n  languages = {\n    pnuts = {\n      extensions = ["pnuts"],\n      grammar = {\n        source = {\n          path = "${escapedGrammarPath}",\n        },\n        symbol = "tree_sitter_pnuts",\n      },\n      indent = "  ",\n    },\n  },\n}\n`;

            fs.writeFileSync(generatedConfigPath, nclContent, 'utf8');
            log(`Generated dynamic languages.ncl at: ${generatedConfigPath}`);
            return generatedConfigPath;
        } catch (err) {
            log(`Failed to generate dynamic languages.ncl: ${err.message}`);
        }
    }

    return null;
}

/**
 * Finds all configuration options for Topiary execution.
 * @param {vscode.ExtensionContext} [context]
 */
function resolveTopiaryOptions(context) {
    const config = vscode.workspace.getConfiguration('pnuts.topiary');
    const rawBinary = config.get('path') || 'topiary';
    const binaryPath = resolveBinary(context, rawBinary);
    const grammarPath = resolveGrammarPath(context);
    const configPath = resolveConfigPath(context, grammarPath);
    const queryPath = resolveQueryPath(context);

    let workspaceRoot = undefined;
    if (vscode.workspace.workspaceFolders && vscode.workspace.workspaceFolders.length > 0) {
        workspaceRoot = vscode.workspace.workspaceFolders[0].uri.fsPath;
    }

    return { binaryPath, configPath, queryPath, grammarPath, workspaceRoot };
}

/**
 * Formats Pnuts source code using Topiary CLI.
 * @param {string} text - Original document content
 * @param {vscode.ExtensionContext} [context] - Extension context
 * @returns {Promise<string>} - Formatted document content
 */
function formatWithTopiary(text, context) {
    return new Promise((resolve, reject) => {
        const { binaryPath, configPath, queryPath, grammarPath, workspaceRoot } = resolveTopiaryOptions(context);

        const args = ['format', '-l', 'pnuts'];
        if (configPath) {
            args.push('-C', configPath);
        }
        if (queryPath) {
            args.push('-q', queryPath);
        }

        // Augment PATH with common binary locations
        const cargoBin = path.join(os.homedir(), '.cargo', 'bin');
        const localBin = path.join(os.homedir(), '.local', 'bin');
        const envPath = [cargoBin, localBin, process.env.PATH].filter(Boolean).join(path.delimiter);

        const env = Object.assign({}, process.env, { PATH: envPath });
        if (queryPath) {
            try {
                const queryDir = fs.statSync(queryPath).isDirectory() ? queryPath : path.dirname(queryPath);
                env.TOPIARY_LANGUAGE_DIR = queryDir;
            } catch (e) {
                // Ignore stat errors
            }
        }

        const spawnOptions = {
            env,
            cwd: workspaceRoot || os.tmpdir(),
            windowsHide: true
        };

        log(`Spawning Topiary: "${binaryPath}" ${args.join(' ')}`);

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
            log(`Process spawn error: ${err.message} (code: ${err.code})`);
            if (err.code === 'ENOENT') {
                const remote = vscode.env && vscode.env.remoteName;
                let hint = '';
                if (remote === 'wsl') {
                    const distro = process.env.WSL_DISTRO_NAME ? ` (${process.env.WSL_DISTRO_NAME})` : '';
                    hint = `VS Code is running in WSL${distro}. The Topiary binary was not found. Please ensure execution permissions or set 'pnuts.topiary.path' in VS Code Settings (WSL tab).`;
                } else if (remote === 'ssh-remote') {
                    hint = `VS Code is connected via Remote - SSH. Topiary was not found on the remote machine.`;
                } else {
                    hint = `Binary not found ('${binaryPath}'). If you configured a custom path in 'pnuts.topiary.path', verify it exists, or clear the setting to use the bundled binary.`;
                }
                reject(new Error(`Topiary binary error: ${hint}`));
            } else {
                reject(new Error(`Failed to launch Topiary ('${binaryPath}'): ${err.message}`));
            }
        });

        child.on('close', (code) => {
            if (code === 0) {
                log('Formatting completed successfully.');
                const flushed = normalizeFlush(stdout);
                resolve(normalizeBlockSpacing(flushed));
            } else {
                log(`Topiary failed with exit code ${code}. Stderr: ${stderr}`);
                reject(new Error(`Topiary exit code ${code}: ${stderr || stdout || 'Unknown error'}`));
            }
        });

        const preprocessedText = preprocessComments(text);
        child.stdin.write(preprocessedText);
        child.stdin.end();
    });
}

/**
 * Finds the starting index of a line comment (// or /* ... * /) outside of string literals.
 * Returns -1 if no comment is found.
 */
function findCommentStart(line) {
    let inStr = null;
    let escaped = false;
    const n = line.length;
    let i = 0;
    while (i < n) {
        const c = line[i];
        if (escaped) {
            escaped = false;
            i++;
            continue;
        }
        if (c === '\\' && inStr) {
            escaped = true;
            i++;
            continue;
        }
        if (inStr) {
            if (c === inStr) {
                inStr = null;
            }
            i++;
            continue;
        }
        if (c === '"' || c === "'" || c === '`') {
            inStr = c;
            i++;
            continue;
        }
        if (c === '/' && i + 1 < n) {
            if (line[i + 1] === '/') {
                return i;
            }
            if (line[i + 1] === '*') {
                const closeIdx = line.indexOf('*/', i + 2);
                if (closeIdx !== -1 && line.slice(closeIdx + 2).trim() === '') {
                    return i;
                }
            }
        }
        i++;
    }
    return -1;
}

/**
 * Ensures that comments starting with // have a space after the slashes.
 * Example:
 *   //este é um comentario -> // este é um comentario
 * Preserves visual divider lines like //***, //---, //=== (3+ identical non-alphanumeric chars).
 */
function formatComment(comment) {
    if (comment.startsWith('/*')) {
        return comment;
    }
    if (!comment.startsWith('//')) {
        return comment;
    }
    const rest = comment.slice(2);
    if (rest === '') {
        return comment;
    }
    if (rest[0] === ' ' || rest[0] === '\t') {
        return comment;
    }
    if (rest.length >= 3 && '*=-/#~.'.includes(rest[0]) && rest.slice(0, 3) === rest[0].repeat(3)) {
        return comment;
    }
    return '// ' + rest;
}

/**
 * Preprocesses comments in Pnuts source code:
 * 1. Ensures space after // in all comments.
 * 2. Moves trailing comments at the end of code lines to the line directly above that code,
 *    preserving the code line's indentation.
 *    Example:
 *      var variavel = 1; // esse é o valor da variavel
 *    becomes:
 *      // esse é o valor da variavel
 *      var variavel = 1;
 */
function preprocessComments(text) {
    if (!text) return text;
    const lines = text.split(/\r?\n/);
    const hasCrlf = text.includes('\r\n');
    const eol = hasCrlf ? '\r\n' : '\n';
    const out = [];

    for (const line of lines) {
        const idx = findCommentStart(line);
        if (idx === -1) {
            out.push(line);
            continue;
        }

        const codePart = line.slice(0, idx);
        const commentPart = line.slice(idx);
        const formattedComment = formatComment(commentPart);

        const codeStripped = codePart.trim();
        const indentMatch = codePart.match(/^\s*/);
        const indent = indentMatch ? indentMatch[0] : '';

        if (!codeStripped) {
            out.push(indent + formattedComment);
        } else {
            if (codeStripped.startsWith('}') || codeStripped.startsWith('else') || codeStripped.startsWith('catch') || codeStripped.startsWith('finally')) {
                out.push(codePart.trimEnd() + ' ' + formattedComment);
            } else {
                out.push(indent + formattedComment);
                out.push(codePart.trimEnd());
            }
        }
    }
    return out.join(eol);
}

/**
 * Normalizes flush() calls to stay on the same line as the preceding command.
 * Example:
 *   println(...);
 *   flush();
 * becomes:
 *   println(...); flush();
 */
function normalizeFlush(text) {
    if (!text) return text;
    const lines = text.split(/\r?\n/);
    const hasCrlf = text.includes('\r\n');
    const eol = hasCrlf ? '\r\n' : '\n';
    const out = [];
    const flushRegex = /^\s*flush\s*\([^)]*\)\s*;?\s*$/;

    for (const line of lines) {
        if (flushRegex.test(line) && out.length > 0) {
            let prevIdx = out.length - 1;
            while (prevIdx >= 0 && out[prevIdx].trim() === '') {
                prevIdx--;
            }

            if (prevIdx >= 0) {
                let prevLine = out[prevIdx];
                const prevTrimmed = prevLine.trim();

                const isComment = prevTrimmed.startsWith('//') || prevTrimmed.startsWith('/*') || prevTrimmed.endsWith('*/');
                const isBlockBoundary = prevTrimmed.endsWith('{') || prevTrimmed.endsWith('}') || prevTrimmed.endsWith(':');

                if (!isComment && !isBlockBoundary && prevTrimmed !== '') {
                    if (!prevTrimmed.endsWith(';')) {
                        prevLine += ';';
                    }

                    let flushCmd = line.trim();
                    if (!flushCmd.endsWith(';')) {
                        flushCmd += ';';
                    }

                    out[prevIdx] = prevLine + ' ' + flushCmd;
                    continue;
                }
            }
        }
        out.push(line);
    }
    return out.join(eol);
}

/**
 * Ensures that any block { ... } with more than one nested line of content
 * has a blank line immediately following the opening brace line.
 * Applies to control structures (if/else, loops: for, foreach, while, do..while),
 * functions, methods, classes, try/catch/finally, and switch blocks.
 */
function normalizeBlockSpacing(text) {
    if (!text) return text;
    const lines = text.split(/\r?\n/);
    const hasCrlf = text.includes('\r\n');
    const eol = hasCrlf ? '\r\n' : '\n';

    const openBraces = [];
    const matchedBlocks = [];

    let inBlockComment = false;
    let inString = null;
    let escaped = false;

    for (let lineIdx = 0; lineIdx < lines.length; lineIdx++) {
        const line = lines[lineIdx];
        const n = line.length;
        let i = 0;

        while (i < n) {
            const c = line[i];

            if (inBlockComment) {
                if (c === '*' && i + 1 < n && line[i + 1] === '/') {
                    inBlockComment = false;
                    i += 2;
                    continue;
                }
                i++;
                continue;
            }

            if (inString) {
                if (escaped) {
                    escaped = false;
                    i++;
                    continue;
                }
                if (c === '\\' && inString) {
                    escaped = true;
                    i++;
                    continue;
                }
                if (c === inString) {
                    inString = null;
                    i++;
                    continue;
                }
                i++;
                continue;
            }

            if (c === '"' || c === "'" || c === '`') {
                inString = c;
                i++;
                continue;
            }

            if (c === '/' && i + 1 < n) {
                if (line[i + 1] === '/') {
                    break;
                }
                if (line[i + 1] === '*') {
                    inBlockComment = true;
                    i += 2;
                    continue;
                }
            }

            if (c === '{') {
                openBraces.push(lineIdx);
                i++;
                continue;
            }

            if (c === '}') {
                if (openBraces.length > 0) {
                    const openLine = openBraces.pop();
                    matchedBlocks.push([openLine, lineIdx]);
                }
                i++;
                continue;
            }

            i++;
        }
    }

    const linesToRemove = new Set();
    const needBlankAfter = new Set();

    for (const [openLine, closeLine] of matchedBlocks) {
        // Remove any blank lines immediately before closing brace }
        let k = closeLine - 1;
        while (k > openLine && lines[k].trim() === '') {
            linesToRemove.add(k);
            k--;
        }

        // Check content lines
        if (closeLine > openLine + 1) {
            const nestedContentLines = [];
            for (let idx = openLine + 1; idx < closeLine; idx++) {
                if (!linesToRemove.has(idx) && lines[idx].trim() !== '') {
                    nestedContentLines.push(lines[idx]);
                }
            }
            if (nestedContentLines.length > 1) {
                if (lines[openLine + 1].trim() !== '') {
                    needBlankAfter.add(openLine);
                }
            }
        }
    }

    const newLines = [];
    for (let lineIdx = 0; lineIdx < lines.length; lineIdx++) {
        if (linesToRemove.has(lineIdx)) {
            continue;
        }
        newLines.push(lines[lineIdx]);
        if (needBlankAfter.has(lineIdx)) {
            newLines.push('');
        }
    }

    let result = newLines.join(eol);
    if (text.endsWith('\n') || text.endsWith('\r\n')) {
        result += eol;
    }
    return result;
}

/**
 * Activates the VSCode extension.
 * @param {vscode.ExtensionContext} context
 */
function activate(context) {
    log('Pnuts Language Support extension activated.');

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

function deactivate() {
    if (outputChannel) {
        outputChannel.dispose();
    }
}

module.exports = {
    activate,
    deactivate,
    resolveBinary,
    resolveGrammarPath,
    resolveQueryPath,
    resolveConfigPath,
    resolveTopiaryOptions,
    formatWithTopiary
};
