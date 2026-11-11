use std::fs;
use zed_extension_api::{
    self as zed, Architecture, Command, DownloadedFileType, Extension,
    GithubReleaseOptions, LanguageServerId, LanguageServerInstallationStatus, Os, Result, Worktree,
};

const GITHUB_REPO: &str = "cichcosta/pnuts-tooling";

struct PnutsExtension {
    cached_binary_path: Option<String>,
}

impl Extension for PnutsExtension {
    fn new() -> Self {
        PnutsExtension {
            cached_binary_path: None,
        }
    }

    fn language_server_command(
        &mut self,
        language_server_id: &LanguageServerId,
        worktree: &Worktree,
    ) -> Result<Command> {
        let env_vars = worktree.shell_env();
        let get_env = |key: &str| -> Option<String> {
            env_vars
                .iter()
                .find(|(k, _)| k.eq_ignore_ascii_case(key))
                .map(|(_, v)| v.clone())
        };

        // Detecta se o worktree alvo onde o comando será executado é Windows ou Unix/Linux.
        // Em conexões Remote-SSH, o worktree roda no servidor remoto (ex: Linux).
        let is_target_windows = {
            let root = worktree.root_path();
            if root.starts_with('/') {
                false
            } else if get_env("WINDIR").is_some() || get_env("SYSTEMROOT").is_some() {
                true
            } else {
                let (os, _arch) = zed::current_platform();
                matches!(os, Os::Windows)
            }
        };

        // 1. FAST PATH: Se o binário já estiver no PATH do worktree ou do sistema
        let bin_name = if is_target_windows { "pnuts-lsp.exe" } else { "pnuts-lsp" };
        if let Some(path) = worktree.which(bin_name) {
            return Ok(Command {
                command: path,
                args: vec![],
                env: Default::default(),
            });
        }

        // 2. CACHED PATH: Se já localizamos nesta sessão
        if let Some(ref cached) = self.cached_binary_path {
            return Ok(Command {
                command: cached.clone(),
                args: vec![],
                env: Default::default(),
            });
        }

        // 3. AUTO-PROVISIONING / DOWNLOAD OFICIAL DO ZED
        // Tenta baixar a release oficial mais recente via GitHub Releases do Zed API
        let download_result = self.download_server_binary(language_server_id, is_target_windows);

        if let Ok(path) = download_result {
            self.cached_binary_path = Some(path.clone());
            return Ok(Command {
                command: path,
                args: vec![],
                env: Default::default(),
            });
        }

        let download_err = download_result.err().unwrap_or_else(|| "erro desconhecido".to_string());

        // 4. FALLBACK TRANSPARENTE: Varredura de caminhos padrão do host
        let root = worktree.root_path();
        let trimmed_root = root.trim_end_matches(['/', '\\']);

        let candidate = if is_target_windows {
            let localappdata = get_env("LOCALAPPDATA")
                .or_else(|| get_env("USERPROFILE").map(|u| format!("{}/AppData/Local", u.trim_end_matches(['/', '\\']))));

            if let Some(lad) = localappdata {
                let base = lad.trim_end_matches(['/', '\\']);
                if trimmed_root.ends_with("zed-pnuts") {
                    format!("{trimmed_root}/bin/pnuts-lsp.exe")
                } else if trimmed_root.contains("pnuts-tooling") {
                    format!("{trimmed_root}/zed-pnuts/bin/pnuts-lsp.exe")
                } else {
                    format!("{base}/Zed/extensions/installed/pnuts/bin/pnuts-lsp.exe")
                }
            } else {
                "pnuts-lsp.exe".to_string()
            }
        } else {
            let home = get_env("HOME");
            if let Some(h) = home {
                let base = h.trim_end_matches(['/', '\\']);
                if trimmed_root.ends_with("zed-pnuts") {
                    format!("{trimmed_root}/bin/pnuts-lsp")
                } else if trimmed_root.contains("pnuts-tooling") {
                    format!("{trimmed_root}/zed-pnuts/bin/pnuts-lsp")
                } else {
                    format!("{base}/.local/bin/pnuts-lsp")
                }
            } else {
                "pnuts-lsp".to_string()
            }
        };

        let command_path = if is_target_windows {
            candidate.replace('/', "\\")
        } else {
            candidate
        };

        if fs::metadata(&command_path).map_or(false, |stat| stat.is_file()) {
            return Ok(Command {
                command: command_path,
                args: vec![],
                env: Default::default(),
            });
        }

        Err(format!(
            "Não foi possível iniciar o pnuts-lsp. Falha no auto-download da release GitHub: {download_err}. Caminho local verificado: {command_path}"
        ))
    }
}

impl PnutsExtension {
    fn download_server_binary(
        &self,
        language_server_id: &LanguageServerId,
        is_target_windows: bool,
    ) -> Result<String> {
        let (platform, arch) = zed::current_platform();

        zed::set_language_server_installation_status(
            language_server_id,
            &LanguageServerInstallationStatus::CheckingForUpdate,
        );

        let release = zed::latest_github_release(
            GITHUB_REPO,
            GithubReleaseOptions {
                require_assets: true,
                pre_release: false,
            },
        )?;

        let (target_os, target_arch) = if is_target_windows {
            (Os::Windows, Architecture::X8664)
        } else {
            match platform {
                Os::Mac => (Os::Mac, arch),
                _ => (Os::Linux, Architecture::X8664),
            }
        };

        let (asset_name, file_type, binary_name) = match (target_os, target_arch) {
            (Os::Windows, _) => (
                format!("pnuts-lsp-{}-x86_64-pc-windows-msvc.zip", release.version),
                DownloadedFileType::Zip,
                "pnuts-lsp.exe",
            ),
            (Os::Mac, Architecture::Aarch64) => (
                format!("pnuts-lsp-{}-aarch64-apple-darwin.tar.gz", release.version),
                DownloadedFileType::GzipTar,
                "pnuts-lsp",
            ),
            (Os::Mac, Architecture::X8664) => (
                format!("pnuts-lsp-{}-x86_64-apple-darwin.tar.gz", release.version),
                DownloadedFileType::GzipTar,
                "pnuts-lsp",
            ),
            _ => (
                format!("pnuts-lsp-{}-x86_64-unknown-linux-gnu.tar.gz", release.version),
                DownloadedFileType::GzipTar,
                "pnuts-lsp",
            ),
        };

        let asset = release
            .assets
            .iter()
            .find(|a| a.name == asset_name)
            .ok_or_else(|| format!("Nenhum asset encontrado para {asset_name}"))?;

        let version_dir = format!("pnuts-lsp-{}", release.version);
        let binary_path = format!("{version_dir}/{binary_name}");

        if !fs::metadata(&binary_path).map_or(false, |stat| stat.is_file()) {
            zed::set_language_server_installation_status(
                language_server_id,
                &LanguageServerInstallationStatus::Downloading,
            );

            zed::download_file(
                &asset.download_url,
                &version_dir,
                file_type,
            )?;

            let _ = zed::make_file_executable(&binary_path);
            let format_bin = if is_target_windows { "pnuts-format.exe" } else { "pnuts-format" };
            let topiary_bin = if is_target_windows { "topiary.exe" } else { "topiary" };
            let _ = zed::make_file_executable(&format!("{version_dir}/{format_bin}"));
            let _ = zed::make_file_executable(&format!("{version_dir}/{topiary_bin}"));
        }

        zed::set_language_server_installation_status(
            language_server_id,
            &LanguageServerInstallationStatus::None,
        );

        Ok(binary_path)
    }
}

zed::register_extension!(PnutsExtension);

