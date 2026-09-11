@echo off
rem Pnuts Formatter Wrapper for Windows (Zed & CLI)
setlocal enabledelayedexpansion

set "BIN_DIR=%~dp0"
set "RUNNER_EXE=%BIN_DIR%pnuts-format.exe"

rem 1. Prefer native standalone runner (fast, zero dependencies)
if exist "%RUNNER_EXE%" (
    "%RUNNER_EXE%" %*
    exit /b !ERRORLEVEL!
)

rem 2. Fallback to format_pnuts.py with python
where python >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    if exist "%BIN_DIR%format_pnuts.py" (
        python "%BIN_DIR%format_pnuts.py" %*
        exit /b !ERRORLEVEL!
    )
    if exist "%BIN_DIR%..\..\scripts\format_pnuts.py" (
        python "%BIN_DIR%..\..\scripts\format_pnuts.py" %*
        exit /b !ERRORLEVEL!
    )
)

rem 3. Fallback to format_pnuts.py with python3
where python3 >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    if exist "%BIN_DIR%format_pnuts.py" (
        python3 "%BIN_DIR%format_pnuts.py" %*
        exit /b !ERRORLEVEL!
    )
    if exist "%BIN_DIR%..\..\scripts\format_pnuts.py" (
        python3 "%BIN_DIR%..\..\scripts\format_pnuts.py" %*
        exit /b !ERRORLEVEL!
    )
)

echo Pnuts formatter: neither pnuts-format.exe nor python was found in %BIN_DIR% >&2
exit /b 1
