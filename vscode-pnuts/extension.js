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

        let validCwd = os.tmpdir();
        if (workspaceRoot) {
            try {
                if (fs.existsSync(workspaceRoot) && fs.statSync(workspaceRoot).isDirectory()) {
                    validCwd = workspaceRoot;
                }
            } catch (e) {
                validCwd = os.tmpdir();
            }
        }

        const spawnOptions = {
            env,
            cwd: validCwd,
            windowsHide: true
        };

        log(`Spawning Topiary: "${binaryPath}" ${args.join(' ')} (cwd: ${validCwd})`);

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
                if (!fs.existsSync(binaryPath)) {
                    if (remote === 'wsl') {
                        const distro = process.env.WSL_DISTRO_NAME ? ` (${process.env.WSL_DISTRO_NAME})` : '';
                        hint = `VS Code is running in WSL${distro}. The Topiary binary was not found ('${binaryPath}'). Please ensure execution permissions or set 'pnuts.topiary.path' in VS Code Settings (WSL tab).`;
                    } else if (remote === 'ssh-remote') {
                        hint = `VS Code is connected via Remote - SSH. Topiary was not found on the remote machine ('${binaryPath}').`;
                    } else {
                        hint = `Binary not found ('${binaryPath}'). If you configured a custom path in 'pnuts.topiary.path', verify it exists, or clear the setting to use the bundled binary.`;
                    }
                } else {
                    hint = `Process spawn failed (ENOENT) running '${binaryPath}' in directory '${validCwd}': ${err.message}`;
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
                const isJump = prevTrimmed === 'continue' || prevTrimmed === 'continue;' ||
                               prevTrimmed === 'break' || prevTrimmed === 'break;' ||
                               prevTrimmed.startsWith('return') || prevTrimmed.startsWith('throw');

                if (!isComment && !isBlockBoundary && !isJump && prevTrimmed !== '') {
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

let LanguageClient = null;
try {
    const lc = require('vscode-languageclient/node');
    LanguageClient = lc.LanguageClient;
} catch (e) {
    // Graceful fallback if vscode-languageclient is not present
}

let client = null;

/**
 * Resolves the Pnuts LSP binary executable path.
 */
function resolveLspBinary(context, configuredPath) {
    const { platform, key, isWindows } = getPlatformInfo();
    const binName = isWindows ? 'pnuts-lsp.exe' : 'pnuts-lsp';

    // 1. Explicitly configured
    if (configuredPath && configuredPath !== '') {
        const expanded = expandPath(configuredPath);
        if (fs.existsSync(expanded)) {
            log(`Using configured LSP binary: ${expanded}`);
            return expanded;
        }
    }

    // 2. Bundled inside extension resources/bin
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
                        try { fs.chmodSync(candidate, 0o755); } catch (e) {}
                    }
                    log(`Using bundled LSP binary: ${candidate}`);
                    return candidate;
                }
            } catch (e) {}
        }
    }

    // 3. Known repository or workspace paths
    const home = os.homedir();
    const candidatePaths = [
        path.join(home, 'personal', 'projects', 'pnuts-tooling', 'target', 'release', binName),
        path.join(home, 'personal', 'projects', 'pnuts-tooling', 'zed-pnuts', 'bin', binName),
        path.join(home, '.cargo', 'bin', binName),
        '/usr/local/bin/' + binName,
        '/usr/bin/' + binName
    ];

    for (const candidate of candidatePaths) {
        try {
            if (fs.existsSync(candidate)) {
                log(`Found LSP binary at standard path: ${candidate}`);
                return candidate;
            }
        } catch (e) {}
    }

    // 4. PATH lookup
    try {
        const cmd = isWindows ? `where ${binName}` : `which ${binName}`;
        const stdout = execSync(cmd, { encoding: 'utf-8', stdio: ['pipe', 'pipe', 'ignore'] }).trim();
        const firstLine = stdout.split(/\r?\n/)[0];
        if (firstLine && fs.existsSync(firstLine)) {
            log(`Found LSP binary in PATH: ${firstLine}`);
            return firstLine;
        }
    } catch (e) {}

    return null;
}

/**
 * Creates a comprehensive native CompletionItemProvider for Pnuts language.
 */
function createCompletionItemProvider() {
    const KEYWORDS = [
        ['function', 'Defines a function: function name(args) { ... }'],
        ['class', 'Defines a class: class Name { ... }'],
        ['if', 'Conditional branching: if (condition) { ... }'],
        ['else', 'Alternative branch: else { ... }'],
        ['for', 'Loop iteration: for (x in y) or for (i=0; i<n; i++)'],
        ['foreach', 'Iteration loop: foreach item (collection) { ... }'],
        ['while', 'While loop: while (condition) { ... }'],
        ['do', 'Do-while loop: do { ... } while (condition);'],
        ['try', 'Exception handling block: try { ... } catch (e) { ... }'],
        ['catch', 'Catches exception: catch (Exception e) { ... }'],
        ['finally', 'Guaranteed cleanup block: finally { ... }'],
        ['throw', 'Throws an exception: throw new Exception(...)'],
        ['return', 'Returns value from function: return value;'],
        ['break', 'Breaks out of loop or switch'],
        ['continue', 'Continues to next loop iteration'],
        ['switch', 'Switch selection: switch (expr) { case ... }'],
        ['case', 'Switch branch: case value:'],
        ['default', 'Default switch branch: default:'],
        ['import', 'Imports Java class or package: import java.util.*'],
        ['package', 'Declares namespace package: package com.mycorp'],
        ['null', 'Null literal reference'],
        ['true', 'Boolean true literal'],
        ['false', 'Boolean false literal'],
        ['new', 'Instantiates an object: new ClassName(...)'],
        ['var', 'Declares a local variable: var x = 10;'],
        ['def', 'Declares dynamically-typed variable or method'],
        ['instanceof', 'Tests object type: obj instanceof Class']
    ];

    const BUILTIN_FUNCTIONS = [
        // Pnuts Core Primitives & Output
        ['println', '(msg)', 'Prints message followed by newline to stdout'],
        ['print', '(msg)', 'Prints message without newline to stdout'],
        ['flush', '()', 'Flushes stdout buffer immediately'],
        ['char', '(code)', 'Returns character for ASCII/Unicode code (e.g. char(34) -> \'"\')'],
        ['open', '(filename)', 'Opens file or stream resource'],
        ['load', '(module)', 'Loads and evaluates script module'],
        ['loadFile', '(filepath)', 'Loads and evaluates a script from local file path'],
        ['require', '(module)', 'Loads module only if it has not been loaded yet'],
        ['use', '(packagename)', 'Imports module package utilities (e.g. use(\'pnuts.lib\'))'],
        ['eval', '(script)', 'Dynamically evaluates Pnuts script expression'],
        ['error', '(msg)', 'Throws runtime error with message'],
        ['getContext', '()', 'Retrieves current Pnuts execution Context'],
        ['isArray', '(obj)', 'Checks if target object is an array'],
        ['isFunction', '(obj)', 'Checks if target object is a callable function'],
        ['defined', '(varName)', 'Checks whether a variable or symbol is defined in scope'],

        // Database & Recordset Operations
        ['getRows', '(table, sqlQuery)', 'Executes SQL query and returns list/Vector of table records'],
        ['getMRows', '(table, sqlQuery)', 'Executes SQL query and returns list/Vector of mapped rows (MRow) with named columns'],
        ['getRow', '(table, sqlQuery)', 'Executes SQL query and returns a single table record'],
        ['getMRow', '(table, [sqlQuery])', 'Instantiates a new MRow or retrieves single mapped row for table'],
        ['getSQLNumber', '(sqlQuery)', 'Executes SQL query and returns a single numeric scalar result (e.g. count, sum)'],
        ['getSQVNumber', '(sqlQuery)', 'Executes SQL query and returns a single numeric scalar result (alias of getSQLNumber)'],
        ['getSQLValue', '(sqlQuery)', 'Executes SQL query and returns a single scalar value/string'],
        ['getSQLValues', '(sqlQuery)', 'Executes SQL query and returns list/Vector of scalar values from query'],
        ['executeSQL', '(sqlStatement)', 'Executes an INSERT, UPDATE, DELETE or DDL SQL statement'],
        ['executeProcedure', '(procName, [params])', 'Executes a database stored procedure with arguments'],
        ['openQuery', '(sqlQuery)', 'Opens a database query cursor for iteration'],
        ['getMCursor', '()', 'Retrieves current database transaction cursor (MConnection::getMCursor())'],
        ['beginWork', '()', 'Begins a database transaction on cursor'],
        ['commitWork', '()', 'Commits active database transaction'],
        ['rollbackWork', '()', 'Rolls back active database transaction'],
        ['insertRow', '(row)', 'Inserts row record into database table'],
        ['updateRow', '(row)', 'Updates row record in database table'],
        ['deleteRow', '(row)', 'Deletes row record from database table'],
        ['refreshRow', '()', 'Refreshes current row buffer from database'],
        ['refresh', '()', 'Refreshes current UI form or dataset'],

        // User Interface & Error Handling
        ['showMessage', '(msg)', 'Displays GUI message dialog alert box to user'],
        ['showError', '(code, [params])', 'Displays error dialog box with error code or parameters'],
        ['throwError', '(msg)', 'Aborts execution and throws application error message dialog'],
        ['getDialogForm', '(parent, name, mode, title, size)', 'Opens dialog form window with title and modal mode'],
        ['showForm', '(form)', 'Displays form window on screen'],
        ['expandForm', '(form, flag)', 'Expands or collapses form components'],
        ['setFormat', '(mask)', 'Sets display/edit mask format on form component'],
        ['getUser', '()', 'Returns current logged-in user name (MLogin::getUser())'],
        ['getUserNodo', '()', 'Returns current network station or workstation node ID'],
        ['getTparms', '(category, param)', 'Retrieves system parameter value from parameter configuration table'],
        ['getParam', '(name)', 'Retrieves parameter from execution context or form'],
        ['setParam', '(name, value)', 'Sets parameter into execution context or form'],

        // Number & Math Operations
        ['formatNumber', '(value, mask)', 'Formats number using format mask (e.g. formatNumber(val, "N0.00"))'],
        ['formatNumero', '(value, mask)', 'Formats number using format mask (alias of formatNumber)'],
        ['round', '(value, decimals)', 'Rounds number or BigDecimal to specified decimal places'],
        ['toNumber', '(value)', 'Converts string or object to number'],
        ['toBigDecimal', '(value)', 'Converts number or string to java.math.BigDecimal'],
        ['today', '()', 'Returns current system date object'],

        // Collections & String Utilities
        ['inList', '(item, list)', 'Checks if item exists in collection, array, or list of values'],
        ['trim', '(str)', 'Removes leading and trailing whitespace from string'],
        ['padRight', '(str, len, [padChar])', 'Pads string to length with spaces or specified character on right'],
        ['padLeft', '(str, len, [padChar])', 'Pads string to length with spaces or specified character on left'],
        ['substring', '(start, end)', 'Extracts substring between character indices'],
        ['indexOf', '(str)', 'Finds index of substring within string'],
        ['replaceAll', '(regex, replacement)', 'Replaces all occurrences of regex pattern in string'],
        ['replace', '(oldStr, newStr)', 'Replaces occurrences of substring with replacement string'],
        ['toUpperCase', '()', 'Converts string characters to uppercase'],
        ['toLowerCase', '()', 'Converts string characters to lowercase'],
        ['split', '(delimiter)', 'Splits string by delimiter into array of strings'],
        ['elements', '()', 'Enumerates elements of collection or Vector for foreach iteration'],
        ['length', '()', 'Returns length of string or collection'],
        ['toString', '()', 'Converts object to string representation'],
        ['digit', '(char)', 'Returns numeric digit value of character'],
        ['montaTag', '(tag, val)', 'Constructs XML/JSON tag element string']
    ];

    const COMMON_JAVA_CLASSES = [
        ['List', 'java.util.List interface'],
        ['ArrayList', 'java.util.ArrayList resizable array implementation'],
        ['Map', 'java.util.Map key-value collection interface'],
        ['HashMap', 'java.util.HashMap hash table implementation'],
        ['Set', 'java.util.Set unique collection interface'],
        ['HashSet', 'java.util.HashSet hash table set implementation'],
        ['Vector', 'java.util.Vector legacy thread-safe list'],
        ['String', 'java.lang.String character sequence'],
        ['StringBuilder', 'java.lang.StringBuilder mutable character sequence'],
        ['StringBuffer', 'java.lang.StringBuffer thread-safe mutable character sequence'],
        ['Integer', 'java.lang.Integer boxed integer utility'],
        ['Double', 'java.lang.Double boxed floating-point utility'],
        ['Boolean', 'java.lang.Boolean boxed boolean utility'],
        ['BigDecimal', 'java.math.BigDecimal arbitrary-precision decimal for currency & tax'],
        ['BigInteger', 'java.math.BigInteger arbitrary-precision integer'],
        ['Date', 'java.util.Date timestamp representation'],
        ['Calendar', 'java.util.Calendar date/time calendar utility'],
        ['SimpleDateFormat', 'java.text.SimpleDateFormat date formatting'],
        ['Exception', 'java.lang.Exception standard checked exception'],
        ['RuntimeException', 'java.lang.RuntimeException unchecked exception'],
        ['MConnection', 'Database connection manager (MConnection::getMCursor())'],
        ['MLogin', 'Authentication context utility (MLogin::getUser())'],
        ['MDate', 'Framework date parsing and manipulation utility'],
        ['MFile', 'File system and stream helper utility'],
        ['JSONObject', 'org.json.JSONObject JSON object representation'],
        ['JSONArray', 'org.json.JSONArray JSON array representation']
    ];

    const SNIPPETS = [
        { label: 'if', body: 'if (${1:condition}) {\n\t$0\n}', detail: 'if condition block' },
        { label: 'ifelse', body: 'if (${1:condition}) {\n\t$2\n} else {\n\t$0\n}', detail: 'if-else condition block' },
        { label: 'while', body: 'while (${1:condition}) {\n\t$0\n}', detail: 'while loop block' },
        { label: 'dowhile', body: 'do {\n\t$0\n} while (${1:condition});', detail: 'do-while loop block' },
        { label: 'for (i)', body: 'for (${1:i} = 0; ${1:i} < ${2:max}; ${1:i}++) {\n\t$0\n}', detail: 'Indexed for loop' },
        { label: 'for (in)', body: 'for (${1:item} in ${2:collection}) {\n\t$0\n}', detail: 'for (item in collection) loop' },
        { label: 'forin', body: 'for (${1:item} in ${2:collection}) {\n\t$0\n}', detail: 'for (item in collection) loop (alias)' },
        { label: 'foreach', body: 'foreach ${1:item} (${2:collection}) {\n\t$0\n}', detail: 'foreach item (collection) loop' },
        { label: 'foreachelements', body: 'foreach ${1:row} (${2:vrows}.elements()) {\n\t$0\n}', detail: 'foreach row in collection elements()' },
        { label: 'function', body: 'function ${1:name}(${2:params}) {\n\t$0\n}', detail: 'Function declaration' },
        { label: 'fn', body: '${1:name} = function(${2:params}) {\n\t$0\n};', detail: 'Function expression assignment' },
        { label: 'trycatch', body: 'try {\n\t$1\n} catch (${2:Exception} ${3:e}) {\n\t$0\n}', detail: 'try-catch exception block' },
        { label: 'trycatchfinally', body: 'try {\n\t$1\n} catch (${2:Exception} ${3:e}) {\n\t$4\n} finally {\n\t$0\n}', detail: 'try-catch-finally block' },
        { label: 'switch', body: 'switch (${1:expr}) {\n\tcase ${2:value}:\n\t\t$3\n\t\tbreak;\n\tdefault:\n\t\t$0\n}', detail: 'switch-case block' },
        { label: 'class', body: 'class ${1:ClassName} {\n\t${1:ClassName}(${2:params}) {\n\t\t$0\n\t}\n}', detail: 'Class definition' },
        { label: 'import', body: 'import("${1:java.util.*}");', detail: 'Import package or class' },
        { label: 'database-transaction', body: 'cursor = MConnection::getMCursor();\ncursor.beginWork();\ntry {\n\t$0\n\tcursor.commitWork();\n} catch (Exception e) {\n\tcursor.rollbackWork();\n\tthrowError(e.getMessage());\n}', detail: 'Database transaction block (beginWork / commitWork / rollbackWork)' },
        { label: 'getRows', body: 'getRows("${1:TABLE}", "${2:select * from ...}")', detail: 'getRows(table, sqlQuery) query records' },
        { label: 'getMRows', body: 'getMRows("${1:TABLE}", "${2:select * from ...}")', detail: 'getMRows(table, sqlQuery) mapped records' },
        { label: 'getRow', body: 'getRow("${1:TABLE}", "${2:select * from ...}")', detail: 'getRow(table, sqlQuery) single record' },
        { label: 'getMRow', body: 'getMRow("${1:TABLE}")', detail: 'getMRow(table) instantiate/retrieve MRow' },
        { label: 'getSQLNumber', body: 'getSQLNumber("${1:select count(1) from ...}")', detail: 'getSQLNumber(sql) numeric scalar result' },
        { label: 'getSQVNumber', body: 'getSQVNumber("${1:select count(1) from ...}")', detail: 'getSQVNumber(sql) numeric scalar result' },
        { label: 'getSQLValue', body: 'getSQLValue("${1:select col from ...}")', detail: 'getSQLValue(sql) string scalar result' },
        { label: 'executeSQL', body: 'executeSQL("${1:update table set ...}");', detail: 'executeSQL(sql) execute DML/DDL' },
        { label: 'throwError', body: 'throwError("${1:Erro na execução}");', detail: 'Throw application error dialog' }
    ];

    return {
        provideCompletionItems(document, position) {
            const items = [];
            const seen = new Set();

            // 1. Keywords
            for (const [kw, desc] of KEYWORDS) {
                const item = new vscode.CompletionItem(kw, vscode.CompletionItemKind.Keyword);
                item.detail = desc;
                items.push(item);
                seen.add(kw);
            }

            // 2. Builtins
            for (const [fn, sig, desc] of BUILTIN_FUNCTIONS) {
                const item = new vscode.CompletionItem(fn, vscode.CompletionItemKind.Function);
                item.detail = `${fn}${sig}`;
                item.documentation = new vscode.MarkdownString(desc);
                if (sig && sig.startsWith('(') && sig.endsWith(')')) {
                    const inner = sig.slice(1, -1).trim();
                    if (inner) {
                        const params = inner.split(',').map((p, idx) => `\${${idx + 1}:${p.trim()}}`).join(', ');
                        item.insertText = new vscode.SnippetString(`${fn}(${params})`);
                    } else {
                        item.insertText = new vscode.SnippetString(`${fn}()$0`);
                    }
                }
                items.push(item);
                seen.add(fn);
            }

            // 3. Common Java classes
            for (const [cls, desc] of COMMON_JAVA_CLASSES) {
                const item = new vscode.CompletionItem(cls, vscode.CompletionItemKind.Class);
                item.detail = desc;
                items.push(item);
                seen.add(cls);
            }

            // 4. Snippets
            for (const snip of SNIPPETS) {
                const item = new vscode.CompletionItem(snip.label, vscode.CompletionItemKind.Snippet);
                item.insertText = new vscode.SnippetString(snip.body);
                item.detail = snip.detail;
                items.push(item);
            }

            // 5. In-document dynamic symbols
            const text = document.getText();
            const fnRegex = /(?:function\s+([a-zA-Z0-9_]+)\s*\(|([a-zA-Z0-9_]+)\s*=\s*function\s*\()/g;
            let m;
            while ((m = fnRegex.exec(text)) !== null) {
                const fnName = m[1] || m[2];
                if (fnName && !seen.has(fnName)) {
                    seen.add(fnName);
                    const item = new vscode.CompletionItem(fnName, vscode.CompletionItemKind.Function);
                    item.detail = 'Function defined in document';
                    items.push(item);
                }
            }

            // Variable identifiers
            const varRegex = /\b([a-zA-Z_][a-zA-Z0-9_]*)\s*=/g;
            while ((m = varRegex.exec(text)) !== null) {
                const varName = m[1];
                if (varName && !seen.has(varName) && varName.length > 1) {
                    seen.add(varName);
                    const item = new vscode.CompletionItem(varName, vscode.CompletionItemKind.Variable);
                    item.detail = 'Variable in document';
                    items.push(item);
                }
            }

            return items;
        }
    };
}

/**
 * Automatically ensures executable permissions (+x / 0755) for all bundled binaries
 * on Unix platforms (Linux / macOS). This transparently resolves permission loss
 * from VSIX extraction in remote SSH environments without any manual user intervention.
 */
function ensureBundledBinariesExecutable(context) {
    if (process.platform === 'win32' || !context || !context.extensionPath) {
        return;
    }
    const binBase = path.join(context.extensionPath, 'resources', 'bin');
    if (!fs.existsSync(binBase)) {
        return;
    }
    try {
        const entries = fs.readdirSync(binBase, { withFileTypes: true });
        for (const entry of entries) {
            const entryPath = path.join(binBase, entry.name);
            if (entry.isDirectory()) {
                try {
                    const subFiles = fs.readdirSync(entryPath);
                    for (const subFile of subFiles) {
                        const subFilePath = path.join(entryPath, subFile);
                        try {
                            const stat = fs.statSync(subFilePath);
                            if (stat.isFile()) {
                                fs.chmodSync(subFilePath, 0o755);
                            }
                        } catch (e) {}
                    }
                } catch (e) {}
            } else if (entry.isFile()) {
                try {
                    fs.chmodSync(entryPath, 0o755);
                } catch (e) {}
            }
        }
        log('Proactively ensured executable permissions (0755) on all bundled binaries.');
    } catch (e) {
        log(`Notice: Could not scan bin directory for permissions: ${e.message}`);
    }
}

/**
 * Activates the VSCode extension.
 * @param {vscode.ExtensionContext} context
 */
async function activate(context) {
    log('Pnuts Language Support extension activating...');

    // 0. Proactively ensure executable permissions on bundled binaries for remote-SSH / Unix
    ensureBundledBinariesExecutable(context);

    // Restore default icon theme if it was previously set to pnuts-icons
    try {
        const currentIconTheme = vscode.workspace.getConfiguration('workbench').get('iconTheme');
        if (currentIconTheme === 'pnuts-icons') {
            await vscode.workspace.getConfiguration('workbench').update('iconTheme', undefined, vscode.ConfigurationTarget.Global);
            log('Restored workbench.iconTheme to default');
        }
    } catch (e) {
        // Non-critical
    }

    // 1. Start LanguageClient if LSP binary is found and enabled
    const config = vscode.workspace.getConfiguration('pnuts');
    const lspEnabled = config.get('lsp.enabled', true);
    const configuredLspPath = config.get('lsp.path', '');

    if (lspEnabled && LanguageClient) {
        const lspBinary = resolveLspBinary(context, configuredLspPath);
        if (lspBinary) {
            try {
                let lspChannel = null;
                try {
                    lspChannel = vscode.window.createOutputChannel('Pnuts Language Server', { log: true });
                } catch (e) {
                    lspChannel = vscode.window.createOutputChannel('Pnuts Language Server');
                }
                if (lspChannel && typeof lspChannel.onDidChangeLogLevel !== 'function') {
                    lspChannel.onDidChangeLogLevel = () => ({ dispose: () => {} });
                }
                if (lspChannel && typeof lspChannel.logLevel === 'undefined') {
                    lspChannel.logLevel = 3;
                }

                const serverOptions = {
                    command: lspBinary,
                    args: [],
                    options: { env: process.env }
                };
                const clientOptions = {
                    documentSelector: [
                        { language: 'pnuts' },
                        { language: 'groovy' },
                        { scheme: 'file', language: 'pnuts' },
                        { scheme: 'untitled', language: 'pnuts' },
                        { scheme: 'vscode-remote', language: 'pnuts' },
                        { scheme: 'vscode-vfs', language: 'pnuts' },
                        { scheme: 'sftp', language: 'pnuts' },
                        { scheme: 'sshfs', language: 'pnuts' },
                        { scheme: 'file', language: 'groovy' },
                        { scheme: 'untitled', language: 'groovy' },
                        { scheme: 'vscode-remote', language: 'groovy' },
                        { scheme: 'vscode-vfs', language: 'groovy' },
                        { scheme: 'sftp', language: 'groovy' },
                        { scheme: 'sshfs', language: 'groovy' },
                        'pnuts',
                        'groovy'
                    ],
                    outputChannel: lspChannel
                };
                client = new LanguageClient(
                    'pnuts-lsp',
                    'Pnuts Language Server',
                    serverOptions,
                    clientOptions
                );
                await client.start();
                context.subscriptions.push({
                    dispose: () => {
                        if (client) {
                            return client.stop();
                        }
                    }
                });
                log(`Started Pnuts Language Server (LSP) from: ${lspBinary}`);
            } catch (err) {
                log(`Failed to start Pnuts Language Server: ${err.message}`);
                vscode.window.showErrorMessage(`Pnuts LSP Error: ${err.message}`);
                client = null;
            }
        } else {
            log('Pnuts LSP binary not found. Using native completion and formatting providers.');
            vscode.window.showWarningMessage('Pnuts LSP executable binary not found. Using basic fallback mode.');
        }
    }

    // 2. Register Native Completion Item Provider (Fallback only when LSP is inactive)
    if (!client) {
        try {
            const completionProvider = vscode.languages.registerCompletionItemProvider(
                ['pnuts', 'groovy'],
                createCompletionItemProvider(),
                '.', ':', '$'
            );
            context.subscriptions.push(completionProvider);
            log('Registered fallback Pnuts CompletionItemProvider (LSP inactive).');
        } catch (e) {
            log(`Failed to register fallback completion provider: ${e.message}`);
        }
    } else {
        log('Using Pnuts Language Server native completion provider.');
    }

    // 3. Register Document Formatting Edit Provider (Topiary fallback only when LSP is inactive)
    if (!client) {
        const formatter = vscode.languages.registerDocumentFormattingEditProvider(['pnuts', 'groovy'], {
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

        // Fallback range formatter: prevents VS Code from formatting whole file when selection format is requested!
        const rangeFormatter = vscode.languages.registerDocumentRangeFormattingEditProvider(['pnuts', 'groovy'], {
            async provideDocumentRangeFormattingEdits(document, range) {
                log('Selection formatting requested while LSP is inactive; avoiding whole-file fallback.');
                return [];
            }
        });
        context.subscriptions.push(rangeFormatter);

        log('Registered fallback Topiary DocumentFormattingEditProvider (LSP inactive).');
    } else {
        log('Using Pnuts Language Server native high-performance formatter.');
    }
    log('Pnuts Language Support extension activated successfully.');
}

async function deactivate() {
    if (client) {
        await client.stop();
        client = null;
    }
    if (outputChannel) {
        outputChannel.dispose();
    }
}

module.exports = {
    activate,
    deactivate,
    resolveBinary,
    resolveLspBinary,
    resolveGrammarPath,
    resolveQueryPath,
    resolveConfigPath,
    resolveTopiaryOptions,
    createCompletionItemProvider,
    formatWithTopiary
};
