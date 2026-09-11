use tree_sitter::Language;

unsafe extern "C" {
    fn tree_sitter_pnuts() -> *const ();
}

/// Returns the tree-sitter [`Language`] for Pnuts.
pub fn language() -> Language {
    unsafe { Language::from_raw(tree_sitter_pnuts() as *const tree_sitter::ffi::TSLanguage) }
}

/// The syntax highlighting query for Pnuts.
pub const HIGHLIGHTS_QUERY: &str = include_str!("../../queries/highlights.scm");

/// The content of the node-types.json file.
pub const NODE_TYPES: &str = include_str!("../../src/node-types.json");

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_can_load_grammar() {
        let mut parser = tree_sitter::Parser::new();
        parser
            .set_language(&language())
            .expect("Error loading Pnuts grammar");
        let parsed = parser.parse("a = 1 + 2;", None).expect("Failed to parse");
        let root = parsed.root_node();
        assert_eq!(root.kind(), "source_file");
        assert!(!root.has_error());
    }
}
