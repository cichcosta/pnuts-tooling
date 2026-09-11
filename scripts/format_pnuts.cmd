@echo off
rem Pnuts Formatter Runner for Windows
set "DIR=%~dp0"
if exist "%DIR%..\zed-pnuts\bin\pnuts-format.exe" (
    "%DIR%..\zed-pnuts\bin\pnuts-format.exe" %*
    exit /b %ERRORLEVEL%
)
python "%DIR%format_pnuts.py" %*
