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
        let (os, _arch) = zed::current_platform();
        let is_windows = matches!(os, Os::Windows);

        let env_vars = worktree.shell_env();
        let get_env = |key: &str| -> Option<String> {
            env_vars
                .iter()
                .find(|(k, _)| k.eq_ignore_ascii_case(key))
                .map(|(_, v)| v.clone())
        };

        let binary = if is_windows {
            worktree
                .which("pnuts-lsp.exe")
                .or_else(|| worktree.which("pnuts-lsp"))
                .unwrap_or_else(|| {
                    let localappdata = get_env("LOCALAPPDATA")
                        .or_else(|| get_env("USERPROFILE").map(|u| format!("{}/AppData/Local", u.trim_end_matches(['/', '\\']))));

                    if let Some(lad) = localappdata {
                        let base = lad.trim_end_matches(['/', '\\']);
                        format!("{base}/Zed/extensions/pnuts-tooling/zed-pnuts/bin/pnuts-lsp.exe")
                    } else {
                        let root = worktree.root_path();
                        let trimmed_root = root.trim_end_matches(['/', '\\']);
                        if trimmed_root.ends_with("zed-pnuts") {
                            format!("{trimmed_root}/bin/pnuts-lsp.exe")
                        } else if trimmed_root.contains("pnuts-tooling") {
                            format!("{trimmed_root}/zed-pnuts/bin/pnuts-lsp.exe")
                        } else {
                            "C:/Users/chsilva/AppData/Local/Zed/extensions/pnuts-tooling/zed-pnuts/bin/pnuts-lsp.exe".to_string()
                        }
                    }
                })
        } else {
            worktree
                .which("pnuts-lsp")
                .unwrap_or_else(|| {
                    let root = worktree.root_path();
                    let trimmed_root = root.trim_end_matches(['/', '\\']);
                    if trimmed_root.ends_with("zed-pnuts") {
                        format!("{trimmed_root}/bin/pnuts-lsp")
                    } else if trimmed_root.contains("pnuts-tooling") {
                        format!("{trimmed_root}/zed-pnuts/bin/pnuts-lsp")
                    } else {
                        let home = get_env("HOME");
                        let (os, arch) = zed::current_platform();
                        let is_mac = matches!(os, Os::Mac);

                        if let Some(h) = home {
                            let base = h.trim_end_matches(['/', '\\']);
                            if is_mac {
                                if matches!(arch, zed_extension_api::Architecture::Aarch64) {
                                    format!("{base}/Library/Application Support/Zed/extensions/pnuts-tooling/zed-pnuts/bin/pnuts-lsp-darwin-arm64")
                                } else {
                                    format!("{base}/Library/Application Support/Zed/extensions/pnuts-tooling/zed-pnuts/bin/pnuts-lsp-darwin-x64")
                                }
                            } else {
                                format!("{base}/.local/bin/pnuts-lsp")
                            }
                        } else {
                            "pnuts-lsp".to_string()
                        }
                    }
                })
        };

        let command_path = if is_windows {
            binary.replace('/', "\\")
        } else {
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
