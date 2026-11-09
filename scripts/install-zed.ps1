# ==============================================================================
# Instalador Unificado Pnuts & Groovy Tooling para Zed (Windows)
# Executa a configuracao completa, adiciona ao PATH e limpa erros anteriores.
# ==============================================================================

Write-Host "=== Instalador Unificado Pnuts Tooling para Zed (Windows) ===" -ForegroundColor Cyan

# 1. Localizar pasta dos binarios
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$CandidateBinDirs = @(
    "$ScriptDir\..\zed-pnuts\bin",
    "$env:LOCALAPPDATA\Zed\extensions\pnuts-tooling\zed-pnuts\bin",
    "$env:USERPROFILE\AppData\Local\Zed\extensions\pnuts-tooling\zed-pnuts\bin"
)

$BinDir = $null
foreach ($dir in $CandidateBinDirs) {
    if (Test-Path "$dir\pnuts-lsp.exe") {
        $BinDir = (Resolve-Path $dir).Path
        break
    }
}

if (-not $BinDir) {
    Write-Host "❌ Erro: Nao foi possivel encontrar 'pnuts-lsp.exe'. Verifique se o repositorio pnuts-tooling esta baixado." -ForegroundColor Red
    exit 1
}

Write-Host "[1/4] Binarios localizados em: $BinDir" -ForegroundColor Green

# 2. Adicionar automaticamente ao PATH do Usuario no Windows
Write-Host "[2/4] Verificando variavel de ambiente PATH..." -ForegroundColor Yellow
$UserPath = [Environment]::GetEnvironmentVariable("Path", "User")
$PathParts = $UserPath -split ';' | Where-Object { $_ -ne "" }

if ($PathParts -notcontains $BinDir) {
    $NewPath = "$UserPath;$BinDir"
    [Environment]::SetEnvironmentVariable("Path", $NewPath, "User")
    $env:Path = "$env:Path;$BinDir"
    Write-Host "   -> Adicionado ao PATH do Windows com sucesso!" -ForegroundColor Green
} else {
    Write-Host "   -> O diretorio ja se encontra no PATH do Windows." -ForegroundColor Gray
}

# 3. Limpar e atualizar o settings.json do Zed
Write-Host "[3/4] Atualizando settings.json do Zed..." -ForegroundColor Yellow
$SettingsCandidates = @(
    "$env:APPDATA\Zed\settings.json",
    "$env:LOCALAPPDATA\Zed\settings.json",
    "$env:USERPROFILE\.config\zed\settings.json"
)

$SettingsFound = $false
foreach ($settingsFile in $SettingsCandidates) {
    if (Test-Path $settingsFile) {
        $SettingsFound = $true
        # Backup
        Copy-Item -Path $settingsFile -Destination "$settingsFile.bak" -Force

        try {
            $content = Get-Content -Path $settingsFile -Raw -Encoding UTF8
            # Limpar comentarios JS (//) antes do parsing JSON
            $cleanJson = $content -replace '(?m)^\s*//.*$', ''
            $jsonObj = $cleanJson | ConvertFrom-Json

            if (-not $jsonObj.languages) {
                $jsonObj | Add-Member -NotePropertyName "languages" -NotePropertyValue (New-Object PSObject) -Force
            }

            # Configurar Pnuts limpo
            $pnutsConf = [PSCustomObject]@{
                language_servers = @("pnuts-lsp", "...")
                formatter = [PSCustomObject]@{
                    external = [PSCustomObject]@{
                        command = "pnuts-lsp"
                        arguments = @("--stdin")
                    }
                }
                format_on_save = "on"
                tab_size = 2
            }
            $jsonObj.languages | Add-Member -NotePropertyName "Pnuts" -NotePropertyValue $pnutsConf -Force

            # Configurar Groovy limpo
            $groovyConf = [PSCustomObject]@{
                language_servers = @("pnuts-lsp", "...")
                formatter = [PSCustomObject]@{
                    external = [PSCustomObject]@{
                        command = "pnuts-lsp"
                        arguments = @("--stdin")
                    }
                }
                format_on_save = "on"
                tab_size = 2
            }
            $jsonObj.languages | Add-Member -NotePropertyName "Groovy" -NotePropertyValue $groovyConf -Force

            # Salvar JSON atualizado
            $jsonOutput = $jsonObj | ConvertTo-Json -Depth 10
            Set-Content -Path $settingsFile -Value $jsonOutput -Encoding UTF8
            Write-Host "   -> Atualizado com sucesso: $settingsFile" -ForegroundColor Green
        } catch {
            Write-Host "   -> Aviso ao processar ${settingsFile}: $($_.Exception.Message)" -ForegroundColor Yellow
        }
    }
}

if (-not $SettingsFound) {
    # Se nao existia settings.json, criar na pasta padrao
    $DefaultSettings = "$env:APPDATA\Zed\settings.json"
    $SettingsDir = Split-Path -Parent $DefaultSettings
    if (-not (Test-Path $SettingsDir)) {
        New-Item -ItemType Directory -Path $SettingsDir -Force | Out-Null
    }
    $initialConfig = @{
        file_types = @{
            Pnuts = @("pnuts", "pnut")
            Groovy = @("groovy")
        }
        languages = @{
            Pnuts = @{
                language_servers = @("pnuts-lsp", "...")
                formatter = @{
                    external = @{
                        command = "pnuts-lsp"
                        arguments = @("--stdin")
                    }
                }
                format_on_save = "on"
                tab_size = 2
            }
            Groovy = @{
                language_servers = @("pnuts-lsp", "...")
                formatter = @{
                    external = @{
                        command = "pnuts-lsp"
                        arguments = @("--stdin")
                    }
                }
                format_on_save = "on"
                tab_size = 2
            }
        }
    } | ConvertTo-Json -Depth 10
    Set-Content -Path $DefaultSettings -Value $initialConfig -Encoding UTF8
    Write-Host "   -> Criado novo settings.json padrao em $DefaultSettings" -ForegroundColor Green
}

# 4. Validar execucao do formatador
Write-Host "[4/4] Validando teste de formatacao via pnuts-lsp..." -ForegroundColor Yellow
$sampleCode = "function teste(a,b){return a+b;}"
try {
    $formatted = $sampleCode | & "$BinDir\pnuts-lsp.exe" --stdin
    if ($formatted -match "function teste\(a, b\)") {
        Write-Host "   -> Teste executado com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "   -> Saida retornada: $formatted" -ForegroundColor Yellow
    }
} catch {
    Write-Host "   -> Erro no teste: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "✅ Instalacao e higienizacao concluidas no Windows!" -ForegroundColor Green
Write-Host "Reinicie o Zed para carregar o novo PATH." -ForegroundColor White
Write-Host "Em buffers nao salvos (untitled), use Alt + Shift + F para formatar." -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
