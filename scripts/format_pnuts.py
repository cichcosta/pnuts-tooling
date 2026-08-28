#!/usr/bin/env python3
"""
Pnuts Formatter CLI Tool
Uses Topiary and the Pnuts Tree-sitter grammar to format .pnuts files.

Usage:
  python3 scripts/format_pnuts.py [--diff | --write | --check] [paths...]

Options:
  --diff     (Default) Show unified diff without modifying files.
  --write    Overwrite files in-place with formatted content.
  --check    Exit with code 1 if any file needs formatting (useful in CI).
  --quiet    Suppress per-file progress output.
"""

import argparse
import difflib
import os
import subprocess
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
CONFIG_FILE = REPO_ROOT / ".topiary" / "languages.ncl"
QUERIES_DIR = REPO_ROOT / ".topiary" / "queries"


def format_code(source: str) -> tuple[int, str, str]:
    env = dict(os.environ, TOPIARY_LANGUAGE_DIR=str(QUERIES_DIR))
    cmd = [
        "topiary",
        "format",
        "-C",
        str(CONFIG_FILE),
        "-l",
        "pnuts",
    ]
    proc = subprocess.run(
        cmd,
        input=source,
        capture_output=True,
        text=True,
        env=env,
    )
    return proc.returncode, proc.stdout, proc.stderr


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
    parser = argparse.ArgumentParser(description="Pnuts Batch Formatter")
    mode_group = parser.add_mutually_exclusive_group()
    mode_group.add_argument(
        "--diff",
        action="store_true",
        default=True,
        help="Show diffs without modifying files (default mode)",
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
        default=["pnuts-samples"],
        help="Files or directories to format (default: pnuts-samples)",
    )

    args = parser.parse_args()
    mode = "write" if args.write else ("check" if args.check else "diff")

    files = find_pnuts_files(args.paths)
    if not files:
        print("No .pnuts files found in specified paths.")
        return 0

    print(f"Found {len(files)} .pnuts files to process (mode: {mode.upper()}).\n")

    unformatted_count = 0
    error_count = 0

    for file_path in files:
        try:
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                original = f.read()
        except Exception as e:
            print(f"[ERROR] Failed to read {file_path}: {e}")
            error_count += 1
            continue

        code, formatted, stderr = format_code(original)
        if code != 0:
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
