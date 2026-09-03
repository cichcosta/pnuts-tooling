@echo off
rem Pnuts Formatter Wrapper for Windows (Zed & CLI)
setlocal enabledelayedexpansion

set "BIN_DIR=%~dp0"
set "RUNNER_EXE=%BIN_DIR%pnuts-format.exe"

where python >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    if exist "%BIN_DIR%format_pnuts.py" (
        python "%BIN_DIR%format_pnuts.py" %*
        exit /b %ERRORLEVEL%
    )
)

where python3 >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    if exist "%BIN_DIR%format_pnuts.py" (
        python3 "%BIN_DIR%format_pnuts.py" %*
        exit /b %ERRORLEVEL%
    )
)

if exist "%RUNNER_EXE%" (
    "%RUNNER_EXE%" %*
    exit /b %ERRORLEVEL%
)

rem Direct fallback using bundled topiary.exe
set "TOPIARY_BIN=%BIN_DIR%..\resources\bin\win32-x64\topiary.exe"
set "GRAMMAR=%BIN_DIR%..\resources\grammars\win32-x64\tree-sitter-pnuts.dll"
set "QUERY=%BIN_DIR%..\resources\queries\pnuts.scm"
set "CFG_FILE=%TEMP%\pnuts-topiary-languages.ncl"

set "ESC_GRAMMAR=%GRAMMAR:\=/%"

(
echo {
echo   languages = {
echo     pnuts = {
echo       extensions = ["pnuts"],
echo       grammar = {
echo         source = {
echo           path = "!ESC_GRAMMAR!",
echo         },
echo         symbol = "tree_sitter_pnuts",
echo       },
echo       indent = "  ",
echo     },
echo   },
echo }
) > "%CFG_FILE%"

set "TOPIARY_LANGUAGE_DIR=%BIN_DIR%..\resources\queries"
"%TOPIARY_BIN%" format -C "%CFG_FILE%" -q "%QUERY%" -l pnuts %*
exit /b %ERRORLEVEL%
