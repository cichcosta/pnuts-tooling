#!/usr/bin/env python3
"""
Pnuts Formatter CLI Tool
Uses Topiary and the Pnuts Tree-sitter grammar to format .pnuts files.
Zero external OS dependencies required: Automatically resolves bundled
cross-platform Topiary binaries and Tree-sitter grammar libraries for
Linux, macOS (Apple Silicon & Intel), and Windows.

Usage:
  # Stdin -> Stdout formatting (for Zed, editors, and pipes)
  python3 scripts/format_pnuts.py --stdin < file.pnuts > formatted.pnuts
  cat file.pnuts | python3 scripts/format_pnuts.py

  # File / Directory formatting
  python3 scripts/format_pnuts.py [--diff | --write | --check] [paths...]
"""

import argparse
import difflib
import os
import platform
import shutil
import stat
import subprocess
import sys
import tempfile
from pathlib import Path
from typing import Optional, Tuple

SCRIPT_DIR = Path(__file__).resolve().parent
if (SCRIPT_DIR.parent / "extension.toml").is_file():
    EXTENSION_ROOT = SCRIPT_DIR.parent
    REPO_ROOT = EXTENSION_ROOT.parent if (EXTENSION_ROOT.parent / ".topiary").is_dir() else EXTENSION_ROOT
else:
    REPO_ROOT = SCRIPT_DIR.parent
    EXTENSION_ROOT = REPO_ROOT / "zed-pnuts"


def get_platform_keys() -> Tuple[str, str, str]:
    """
    Returns (sys_name, arch_name, key_name).
    sys_name: 'linux', 'darwin', 'win32'
    arch_name: 'x64', 'arm64'
    key_name: e.g. 'linux-x64', 'darwin-arm64', 'win32-x64'
    """
    system = platform.system().lower()
    machine = platform.machine().lower()

    if system.startswith("win"):
        sys_name = "win32"
    elif system == "darwin":
        sys_name = "darwin"
    else:
        sys_name = "linux"

    if machine in ("x86_64", "amd64", "x64"):
        arch_name = "x64"
    elif machine in ("arm64", "aarch64"):
        arch_name = "arm64"
    else:
        arch_name = "x64"

    return sys_name, arch_name, f"{sys_name}-{arch_name}"


def resolve_binary(custom_path: Optional[str] = None) -> Optional[str]:
    """Finds the Topiary binary executable."""
    sys_name, arch_name, key = get_platform_keys()
    bin_name = "topiary.exe" if sys_name == "win32" else "topiary"

    # 1. Custom configured path
    if custom_path:
        p = Path(custom_path).expanduser().resolve()
        if p.is_file():
            return str(p)

    # 2. Bundled binary candidates
    bundled_roots = [
        EXTENSION_ROOT / "resources" / "bin",
        REPO_ROOT / "zed-pnuts" / "resources" / "bin",
        REPO_ROOT / "vscode-pnuts" / "resources" / "bin",
        SCRIPT_DIR.parent / "resources" / "bin",
        SCRIPT_DIR / "resources" / "bin",
    ]

    for root in bundled_roots:
        candidates = [
            root / key / bin_name,
            root / f"{sys_name}-x64" / bin_name,
            root / bin_name,
        ]
        for candidate in candidates:
            if candidate.is_file():
                if sys_name != "win32":
                    try:
                        current_mode = candidate.stat().st_mode
                        candidate.chmod(current_mode | stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH)
                    except Exception:
                        pass
                return str(candidate)

    # 3. System common locations
    home = Path.home()
    system_candidates = [
        home / ".cargo" / "bin" / bin_name,
        home / ".local" / "bin" / bin_name,
        Path("/usr/local/bin") / bin_name,
        Path("/usr/bin") / bin_name,
        Path("/snap/bin") / bin_name,
        Path("/home/linuxbrew/.linuxbrew/bin") / bin_name,
    ]
    for candidate in system_candidates:
        if candidate.is_file():
            return str(candidate)

    # 4. PATH lookup
    in_path = shutil.which(bin_name) or shutil.which("topiary")
    if in_path:
        return in_path

    return None


def resolve_grammar(custom_path: Optional[str] = None) -> Optional[str]:
    """Finds the Tree-sitter dynamic library."""
    sys_name, arch_name, key = get_platform_keys()
    if sys_name == "win32":
        lib_names = ["tree-sitter-pnuts.dll", "libtree-sitter-pnuts.dll"]
    elif sys_name == "darwin":
        lib_names = ["libtree-sitter-pnuts.dylib", "tree-sitter-pnuts.dylib"]
    else:
        lib_names = ["libtree-sitter-pnuts.so", "tree-sitter-pnuts.so"]

    # 1. Custom path
    if custom_path:
        p = Path(custom_path).expanduser().resolve()
        if p.is_file():
            return str(p)

    # 2. Workspace locations
    ws_candidates = [
        REPO_ROOT / ".topiary" / "grammars",
        REPO_ROOT / "tree-sitter-pnuts",
    ]
    for root in ws_candidates:
        for lib in lib_names:
            candidate = root / lib
            if candidate.is_file():
                return str(candidate)

    # 3. Bundled grammars
    grammar_roots = [
        EXTENSION_ROOT / "resources" / "grammars",
        REPO_ROOT / "zed-pnuts" / "resources" / "grammars",
        REPO_ROOT / "vscode-pnuts" / "resources" / "grammars",
        SCRIPT_DIR.parent / "resources" / "grammars",
        SCRIPT_DIR / "resources" / "grammars",
    ]

    for root in grammar_roots:
        for lib in lib_names:
            candidates = [
                root / key / lib,
                root / f"{sys_name}-x64" / lib,
                root / lib,
            ]
            for candidate in candidates:
                if candidate.is_file():
                    return str(candidate)

    return None


def resolve_query(custom_path: Optional[str] = None) -> Optional[str]:
    """Finds the Topiary pnuts.scm query file."""
    if custom_path:
        p = Path(custom_path).expanduser().resolve()
        if p.is_file():
            return str(p)
        if p.is_dir() and (p / "pnuts.scm").is_file():
            return str(p / "pnuts.scm")

    candidates = [
        REPO_ROOT / ".topiary" / "queries" / "pnuts.scm",
        EXTENSION_ROOT / "resources" / "queries" / "pnuts.scm",
        REPO_ROOT / "zed-pnuts" / "resources" / "queries" / "pnuts.scm",
        REPO_ROOT / "vscode-pnuts" / "resources" / "queries" / "pnuts.scm",
        SCRIPT_DIR.parent / "resources" / "queries" / "pnuts.scm",
        SCRIPT_DIR / "resources" / "queries" / "pnuts.scm",
    ]

    for candidate in candidates:
        if candidate.is_file():
            return str(candidate)

    return None


def get_or_create_config(grammar_path: Optional[str]) -> Optional[str]:
    """Resolves or dynamically generates languages.ncl configuration with absolute grammar path."""
    if not grammar_path:
        return None

    temp_dir = Path(tempfile.gettempdir()) / "pnuts-topiary"
    temp_dir.mkdir(parents=True, exist_ok=True)
    generated_config = temp_dir / "languages.ncl"

    escaped_grammar = grammar_path.replace("\\", "/")
    content = f"""{{
  languages = {{
    pnuts = {{
      extensions = ["pnuts"],
      grammar = {{
        source = {{
          path = "{escaped_grammar}",
        }},
        symbol = "tree_sitter_pnuts",
      }},
      indent = "  ",
    }},
  }},
}}
"""
    generated_config.write_text(content, encoding="utf-8")
    return str(generated_config)


def format_code(source: str) -> Tuple[int, str, str]:
    """Formats source code string using Topiary."""
    binary_path = resolve_binary()
    if not binary_path:
        return 1, "", "Topiary binary not found. Please verify bundled resources or PATH."

    grammar_path = resolve_grammar()
    config_path = get_or_create_config(grammar_path)
    query_path = resolve_query()

    cmd = [binary_path, "format", "-l", "pnuts"]
    if config_path:
        cmd.extend(["-C", config_path])
    if query_path:
        cmd.extend(["-q", query_path])

    env = dict(os.environ)
    if query_path:
        q_dir = Path(query_path).parent if Path(query_path).is_file() else Path(query_path)
        env["TOPIARY_LANGUAGE_DIR"] = str(q_dir)

    preprocessed = preprocess_comments(source)

    try:
        proc = subprocess.run(
            cmd,
            input=preprocessed,
            capture_output=True,
            text=True,
            env=env,
        )
        if proc.returncode == 0:
            formatted = normalize_flush(proc.stdout)
            formatted = normalize_block_spacing(formatted)
            return 0, formatted, proc.stderr
        return proc.returncode, proc.stdout, proc.stderr
    except Exception as e:
        return 1, "", f"Failed to execute Topiary ({binary_path}): {e}"


def find_comment_start(line: str) -> int:
    """
    Finds the starting index of a trailing comment (// or /* ... */) outside of string literals.
    Returns -1 if no comment is found.
    """
    in_str = None
    escaped = False
    i = 0
    n = len(line)
    while i < n:
        c = line[i]
        if escaped:
            escaped = False
            i += 1
            continue
        if c == "\\" and in_str:
            escaped = True
            i += 1
            continue
        if in_str:
            if c == in_str:
                in_str = None
            i += 1
            continue
        if c in ('"', "'", "`"):
            in_str = c
            i += 1
            continue
        if c == "/" and i + 1 < n:
            if line[i + 1] == "/":
                return i
            if line[i + 1] == "*":
                close_idx = line.find("*/", i + 2)
                if close_idx != -1 and line[close_idx + 2:].strip() == "":
                    return i
        i += 1
    return -1


def format_comment(comment: str) -> str:
    """
    Ensures that comments starting with // have a space after the slashes.
    Example:
      //este é um comentario -> // este é um comentario
    Preserves visual divider lines like //***, //---, //=== (3+ identical non-alphanumeric chars).
    """
    if comment.startswith("/*"):
        return comment
    if not comment.startswith("//"):
        return comment
    rest = comment[2:]
    if rest == "":
        return comment
    if rest[0] in (" ", "\t"):
        return comment
    if len(rest) >= 3 and rest[0] in "*=-/#~." and all(c == rest[0] for c in rest[:3]):
        return comment
    return "// " + rest


def preprocess_comments(text: str) -> str:
    """
    Preprocesses comments in Pnuts source code:
    1. Ensures space after // in all comments (e.g. //comentario -> // comentario).
    2. Moves trailing comments at the end of code lines to the line directly above that code,
       preserving the code line's indentation.
       Example:
         var variavel = 1; // esse é o valor da variavel
       becomes:
         // esse é o valor da variavel
         var variavel = 1;
    """
    lines = text.splitlines(keepends=True)
    out = []

    for line in lines:
        raw = line.rstrip("\r\n")
        eol = line[len(raw):]

        idx = find_comment_start(raw)
        if idx == -1:
            out.append(line)
            continue

        code_part = raw[:idx]
        comment_part = raw[idx:]
        formatted_comment = format_comment(comment_part)

        code_stripped = code_part.strip()
        indent = code_part[:len(code_part) - len(code_part.lstrip())]

        if not code_stripped:
            # Full line comment
            out.append(indent + formatted_comment + eol)
        else:
            # Trailing inline comment after code
            if code_stripped.startswith(("}", "else", "catch", "finally")):
                # Keep block-closing or chaining comments on the same line
                out.append(code_part.rstrip() + " " + formatted_comment + eol)
            else:
                # Move comment to the line above the code with matching indentation
                out.append(indent + formatted_comment + eol)
                out.append(code_part.rstrip() + eol)

    return "".join(out)


def normalize_block_spacing(text: str) -> str:
    """
    Ensures that any block { ... } with more than one nested line of content
    has a blank line immediately following the opening brace line.
    Applies to control structures (if/else, loops: for, foreach, while, do..while),
    functions, methods, classes, try/catch/finally, and switch blocks.
    Example:
      if (a > b) {
        showMessage(exemplo muitline sem, o espcaco);
        showMessage(mai uma linha para exemplo;)
      }
    becomes:
      if (a > b) {

        showMessage(exemplo muitline sem, o espcaco);
        showMessage(mai uma linha para exemplo;)
      }
    """
    if not text:
        return text

    lines = text.splitlines()
    has_crlf = "\r\n" in text
    eol = "\r\n" if has_crlf else "\n"

    open_braces = []
    matched_blocks = []

    in_block_comment = False
    in_string = None
    escaped = False

    for line_idx, line in enumerate(lines):
        i = 0
        n = len(line)
        while i < n:
            c = line[i]

            if in_block_comment:
                if c == "*" and i + 1 < n and line[i + 1] == "/":
                    in_block_comment = False
                    i += 2
                    continue
                i += 1
                continue

            if in_string:
                if escaped:
                    escaped = False
                    i += 1
                    continue
                if c == "\\" and in_string:
                    escaped = True
                    i += 1
                    continue
                if c == in_string:
                    in_string = None
                    i += 1
                    continue
                i += 1
                continue

            if c in ('"', "'", "`"):
                in_string = c
                i += 1
                continue

            if c == "/" and i + 1 < n:
                if line[i + 1] == "/":
                    break
                if line[i + 1] == "*":
                    in_block_comment = True
                    i += 2
                    continue

            if c == "{":
                open_braces.append(line_idx)
                i += 1
                continue

            if c == "}":
                if open_braces:
                    open_line = open_braces.pop()
                    matched_blocks.append((open_line, line_idx))
                i += 1
                continue

            i += 1

    lines_to_remove = set()
    need_blank_after = set()

    for open_line, close_line in matched_blocks:
        # Remove any blank lines immediately before closing brace }
        k = close_line - 1
        while k > open_line and lines[k].strip() == "":
            lines_to_remove.add(k)
            k -= 1

        # Check content lines
        if close_line > open_line + 1:
            nested_content = [
                lines[idx] for idx in range(open_line + 1, close_line)
                if idx not in lines_to_remove and lines[idx].strip() != ""
            ]
            if len(nested_content) > 1:
                # Need blank line after opening brace if not already present
                if lines[open_line + 1].strip() != "":
                    need_blank_after.add(open_line)

    new_lines = []
    for line_idx, line in enumerate(lines):
        if line_idx in lines_to_remove:
            continue
        new_lines.append(line)
        if line_idx in need_blank_after:
            new_lines.append("")

    result = eol.join(new_lines)
    if text.endswith("\n") or text.endswith("\r\n"):
        result += eol
    return result


def normalize_flush(text: str) -> str:
    """
    Ensures flush() calls stay on the same line as the preceding command.
    Example:
      println(...);
      flush();
    becomes:
      println(...); flush();
    """
    import re
    lines = text.splitlines(keepends=True)
    out = []
    flush_pattern = re.compile(r"^\s*flush\s*\([^)]*\)\s*;?\s*$")

    for line in lines:
        if flush_pattern.match(line) and out:
            prev_idx = len(out) - 1
            while prev_idx >= 0 and out[prev_idx].strip() == "":
                prev_idx -= 1

            if prev_idx >= 0:
                prev_line = out[prev_idx].rstrip("\r\n")
                prev_stripped = prev_line.strip()

                is_comment = (
                    prev_stripped.startswith("//")
                    or prev_stripped.startswith("/*")
                    or prev_stripped.endswith("*/")
                )
                is_block_boundary = (
                    prev_stripped.endswith("{")
                    or prev_stripped.endswith("}")
                    or prev_stripped.endswith(":")
                )

                if not is_comment and not is_block_boundary and prev_stripped != "":
                    if not prev_stripped.endswith(";"):
                        prev_line += ";"

                    flush_cmd = line.strip()
                    if not flush_cmd.endswith(";"):
                        flush_cmd += ";"

                    eol = "\r\n" if out[prev_idx].endswith("\r\n") else "\n"
                    out[prev_idx] = prev_line + " " + flush_cmd + eol
                    continue
        out.append(line)
    return "".join(out)


def find_pnuts_files(paths: list[str]) -> list[Path]:
    result = []
    for p_str in paths:
        p = Path(p_str)
        if p.is_file() and p.suffix in (".pnuts", ".pnut"):
            result.append(p)
        elif p.is_dir():
            for child in p.rglob("*"):
                if child.is_file() and child.suffix in (".pnuts", ".pnut"):
                    result.append(child)
    return sorted(result)


def main():
    parser = argparse.ArgumentParser(description="Pnuts Code Formatter")
    mode_group = parser.add_mutually_exclusive_group()
    mode_group.add_argument(
        "--stdin",
        action="store_true",
        help="Read from standard input and output formatted code to standard output",
    )
    mode_group.add_argument(
        "--diff",
        action="store_true",
        default=False,
        help="Show diffs without modifying files",
    )
    mode_group.add_argument(
        "--write",
        action="store_true",
        help="Overwrite files in-place with formatted output",
    )
    mode_group.add_argument(
        "--check",
        action="store_true",
        help="Check formatting without writing, exit with 1 if changes needed",
    )
    parser.add_argument(
        "--quiet",
        action="store_true",
        help="Quiet output",
    )
    parser.add_argument(
        "paths",
        nargs="*",
        default=[],
        help="Files or directories to format",
    )

    args = parser.parse_args()

    # If --stdin or if no paths are provided and input is piped via stdin
    if args.stdin or (not args.paths and not sys.stdin.isatty()):
        source = sys.stdin.read()
        code, formatted, stderr = format_code(source)
        if code == 0:
            sys.stdout.write(formatted)
            return 0
        else:
            sys.stderr.write(f"Pnuts Formatter Error:\n{stderr}\n")
            return code or 1

    # File / directory mode
    paths = args.paths if args.paths else ["pnuts-samples"]
    mode = "write" if args.write else ("check" if args.check else "diff")

    files = find_pnuts_files(paths)
    if not files:
        if not args.quiet:
            print("No .pnuts files found in specified paths.")
        return 0

    if not args.quiet:
        print(f"Found {len(files)} .pnuts files to process (mode: {mode.upper()}).\n")

    unformatted_count = 0
    error_count = 0

    for file_path in files:
        try:
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                original = f.read()
        except Exception as e:
            if not args.quiet:
                print(f"[ERROR] Failed to read {file_path}: {e}")
            error_count += 1
            continue

        code, formatted, stderr = format_code(original)
        if code != 0:
            if not args.quiet:
                print(f"[FAIL] {file_path}: {stderr.strip()}")
            error_count += 1
            continue

        if original != formatted:
            unformatted_count += 1
            if mode == "diff":
                diff = difflib.unified_diff(
                    original.splitlines(keepends=True),
                    formatted.splitlines(keepends=True),
                    fromfile=str(file_path),
                    tofile=f"{file_path} (formatted)",
                    n=3,
                )
                print("".join(diff))
            elif mode == "write":
                with open(file_path, "w", encoding="utf-8") as f:
                    f.write(formatted)
                if not args.quiet:
                    print(f"[FORMATTED] {file_path}")
            elif mode == "check":
                if not args.quiet:
                    print(f"[NEEDS FORMATTING] {file_path}")
        else:
            if not args.quiet and mode == "write":
                print(f"[UNCHANGED] {file_path}")

    if not args.quiet:
        print(f"\n--- Summary ---")
        print(f"Total files processed: {len(files)}")
        print(f"Files needing formatting / formatted: {unformatted_count}")
        print(f"Errors: {error_count}")

    if mode == "check" and unformatted_count > 0:
        return 1
    if error_count > 0:
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
