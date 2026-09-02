@echo off
rem Pnuts Formatter Runner for Windows
set "DIR=%~dp0"
python "%DIR%format_pnuts.py" %*
