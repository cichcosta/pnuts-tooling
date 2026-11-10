use zed_extension_api::{self as zed, Command, Extension, LanguageServerId, Os, Result, Worktree};

struct PnutsExtension;

impl Extension for PnutsExtension {
    fn new() -> Self {
        PnutsExtension
    }

    fn language_server_command(
        &mut self,
        _language_server_id: &LanguageServerId,
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
        // ATENÇÃO: Em sessões Remote-SSH, `zed::current_platform()` retorna o SO do cliente Zed local
        // (ex: Windows), mas o worktree executa no servidor remoto (ex: Linux)!
        let is_target_windows = {
            let root = worktree.root_path();
            if root.starts_with('/') {
                // Caminho absoluto POSIX: certamente é Linux / macOS no host remoto
                false
            } else if get_env("WINDIR").is_some() || get_env("SYSTEMROOT").is_some() {
                true
            } else {
                let (os, _arch) = zed::current_platform();
                matches!(os, Os::Windows)
            }
        };

        let binary = if is_target_windows {
            worktree
                .which("pnuts-lsp.exe")
                .or_else(|| worktree.which("pnuts-lsp"))
                .unwrap_or_else(|| {
                    let localappdata = get_env("LOCALAPPDATA")
                        .or_else(|| get_env("USERPROFILE").map(|u| format!("{}/AppData/Local", u.trim_end_matches(['/', '\\']))));

                    if let Some(lad) = localappdata {
                        let base = lad.trim_end_matches(['/', '\\']);
                        let root = worktree.root_path();
                        let trimmed_root = root.trim_end_matches(['/', '\\']);
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
                })
        } else {
            // Ambiente Alvo Unix / Linux (Local ou Remote via SSH)
            worktree
                .which("pnuts-lsp")
                .unwrap_or_else(|| {
                    let home = get_env("HOME");
                    let root = worktree.root_path();
                    let trimmed_root = root.trim_end_matches(['/', '\\']);

                    if let Some(h) = home {
                        let base = h.trim_end_matches(['/', '\\']);
                        if trimmed_root.ends_with("zed-pnuts") {
                            format!("{trimmed_root}/bin/pnuts-lsp")
                        } else if trimmed_root.contains("pnuts-tooling") {
                            format!("{trimmed_root}/zed-pnuts/bin/pnuts-lsp")
                        } else {
                            format!("{base}/.local/bin/pnuts-lsp")
                        }
                    } else if trimmed_root.ends_with("zed-pnuts") {
                        format!("{trimmed_root}/bin/pnuts-lsp")
                    } else if trimmed_root.contains("pnuts-tooling") {
                        format!("{trimmed_root}/zed-pnuts/bin/pnuts-lsp")
                    } else {
                        "pnuts-lsp".to_string()
                    }
                })
        };

        let command_path = if is_target_windows {
            binary.replace('/', "\\")
        } else {
            let _ = zed::make_file_executable(&binary);
            binary
        };

        Ok(Command {
            command: command_path,
            args: vec![],
            env: Default::default(),
        })
    }
}

zed::register_extension!(PnutsExtension);
