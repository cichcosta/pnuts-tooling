use pnuts_lsp::Backend;
use std::env;
use std::io;
use tower_lsp::{LspService, Server};
use tracing_subscriber::EnvFilter;

const VERSION: &str = env!("CARGO_PKG_VERSION");
const HELP_TEXT: &str = "\
pnuts-lsp - Language Server Protocol server and formatter for Pnuts and Groovy

USAGE:
    pnuts-lsp [OPTIONS]

OPTIONS:
    -s, --stdin, --format    Format Pnuts/Groovy source code from standard input to stdout
    -h, --help               Print this help message
    -V, --version            Print version information

ENVIRONMENT:
    RUST_LOG                 Filter for logging output (default: info). Logs are emitted to stderr.
";

#[tokio::main]
async fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() > 1 {
        match args[1].as_str() {
            "-V" | "--version" => {
                println!("pnuts-lsp {}", VERSION);
                return;
            }
            "-h" | "--help" => {
                print!("{}", HELP_TEXT);
                return;
            }
            "-s" | "--stdin" | "--format" => {
                use std::io::Read;
                let mut input = String::new();
                if let Err(e) = io::stdin().read_to_string(&mut input) {
                    eprintln!("Error reading standard input: {}", e);
                    std::process::exit(1);
                }
                match pnuts_lsp::format_code(&input).await {
                    Ok(formatted) => {
                        print!("{}", formatted);
                        return;
                    }
                    Err(e) => {
                        eprintln!("Formatting failed: {}", e);
                        // Output original input so editors do not wipe buffer content
                        print!("{}", input);
                        std::process::exit(0);
                    }
                }
            }
            "--stdio" => {
                // Default mode: run LSP JSON-RPC over stdio
            }
            unknown => {
                eprintln!("Unknown argument: {}\n", unknown);
                eprint!("{}", HELP_TEXT);
                std::process::exit(1);
            }
        }
    }

    // Initialize tracing to stderr so stdout remains clean for JSON-RPC messages
    tracing_subscriber::fmt()
        .with_writer(io::stderr)
        .with_env_filter(
            EnvFilter::try_from_default_env().unwrap_or_else(|_| EnvFilter::new("info")),
        )
        .init();

    let stdin = tokio::io::stdin();
    let stdout = tokio::io::stdout();

    let (service, socket) = LspService::new(Backend::new);
    Server::new(stdin, stdout, socket).serve(service).await;
}
