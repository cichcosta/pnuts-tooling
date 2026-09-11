use tree_sitter::{Parser, Tree};

/// Creates a new configured Tree-sitter parser for Pnuts.
pub fn create_parser() -> Option<Parser> {
    let mut parser = Parser::new();
    parser
        .set_language(&tree_sitter_pnuts::language())
        .ok()?;
    Some(parser)
}

/// Parses the given source text using the Pnuts Tree-sitter grammar.
/// An optional `old_tree` can be supplied for incremental parsing.
pub fn parse_pnuts(text: &str, old_tree: Option<&Tree>) -> Option<Tree> {
    let mut parser = create_parser()?;
    parser.parse(text, old_tree)
}

/// Checks whether the parsed tree contains any syntax errors.
pub fn has_errors(tree: &Tree) -> bool {
    tree.root_node().has_error()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_parse_valid_pnuts() {
        let code = r#"
            package test;
            import java.util.List;

            function calculate(a, b) {
                if (a > b) {
                    return a * 2;
                } else {
                    return b - 1;
                }
            }
        "#;

        let tree = parse_pnuts(code, None).expect("Should produce a Tree");
        assert_eq!(tree.root_node().kind(), "source_file");
        assert!(!has_errors(&tree));
    }

    #[test]
    fn test_parse_error_tolerance() {
        // Syntax with an error: tree-sitter should produce an ERROR node without failing
        let code = "function (broken syntax %%% ;";
        let tree = parse_pnuts(code, None).expect("Should parse even with errors");
        assert!(has_errors(&tree));
    }

    #[test]
    fn test_parse_critical_grammar_cases() {
        // Critical bugs previously resolved in grammar.js:
        // 1. char(34)
        let code_char = "c = char(34);";
        let tree_char = parse_pnuts(code_char, None).unwrap();
        assert!(!has_errors(&tree_char));

        // 2. foreach x(y) and for (x in y)
        let code_foreach = "foreach item(list) { println(item); }";
        let tree_foreach = parse_pnuts(code_foreach, None).unwrap();
        assert!(!has_errors(&tree_foreach));

        let code_for_in = "for (item in list) { println(item); }";
        let tree_for_in = parse_pnuts(code_for_in, None).unwrap();
        assert!(!has_errors(&tree_for_in));

        // 3. Map literal [:]
        let code_map = "wmap = [:];";
        let tree_map = parse_pnuts(code_map, None).unwrap();
        assert!(!has_errors(&tree_map));

        // 4. catch (Exception e)
        let code_catch = "try { work(); } catch (Exception e) { handleError(e); }";
        let tree_catch = parse_pnuts(code_catch, None).unwrap();
        assert!(!has_errors(&tree_catch));
    }
}
