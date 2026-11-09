;; ====================================================================
;; Topiary Formatting Queries for Pnuts (.pnuts)
;; ====================================================================

;; --------------------------------------------------------------------
;; 1. Indentation & Block Scopes
;; --------------------------------------------------------------------

(block
  "{" @append_indent_start @append_hardline
  "}" @prepend_indent_end @prepend_hardline
)

(closure_expression
  "{" @append_indent_start @append_hardline
  "}" @prepend_indent_end @prepend_hardline
)

(class_body
  "{" @append_indent_start @append_hardline
  "}" @prepend_indent_end @prepend_hardline
)

(switch_statement
  "{" @append_indent_start @append_hardline
  "}" @prepend_indent_end @prepend_hardline
)

(switch_case
  ":" @prepend_antispace @append_indent_start @append_hardline
)
(switch_case) @append_indent_end @allow_blank_line_before

(switch_default
  ":" @prepend_antispace @append_indent_start @append_hardline
)
(switch_default) @append_indent_end @allow_blank_line_before

;; --------------------------------------------------------------------
;; 2. Statement Terminations & Line Breaks
;; --------------------------------------------------------------------

(source_file
  (_) @append_hardline @allow_blank_line_before
)

(block
  (_) @append_hardline @allow_blank_line_before
)

(closure_expression
  (_) @append_hardline @allow_blank_line_before
)

(switch_case
  ":"
  (_) @append_hardline @allow_blank_line_before
)

(switch_default
  ":"
  (_) @append_hardline @allow_blank_line_before
)

(class_body
  (_) @append_hardline @allow_blank_line_before
)

;; --------------------------------------------------------------------
;; 3. Keywords & Control Flow Spacing
;; --------------------------------------------------------------------

[
  "if"
  "while"
  "for"
  "foreach"
  "switch"
  "catch"
] @append_space

[
  "function"
  "class"
  "extends"
  "implements"
  "return"
  "throw"
  "yield"
  "break"
  "continue"
  "new"
  "import"
  "package"
  "static"
  "case"
  "default"
  "do"
] @append_space

"catch" @prepend_space @append_space
"finally" @prepend_space @append_space
"else" @prepend_space @append_space
"while" @prepend_space @append_space
"extends" @prepend_space @append_space
"implements" @prepend_space @append_space
"instanceof" @prepend_space @append_space

;; Space after closing parenthesis in control flow structures
(if_statement
  ")" @append_space
)

(while_statement
  ")" @append_space
)

(do_while_statement
  "do" @append_space
)

(do_while_statement
  ")" @append_space
)

(for_statement
  ";" @append_space
)

(for_statement
  ")" @append_space
)

(foreach_statement
  ")" @append_space
)

(foreach_statement
  "]" @append_space
)

(catch_clause
  ")" @append_space
)

;; --------------------------------------------------------------------
;; 4. Operators Spacing
;; --------------------------------------------------------------------

[
  "="
  "+="
  "-="
  "*="
  "/="
  "%="
  "&="
  "|="
  "^="
  "<<="
  ">>="
  ">>>="
  "=="
  "!="
  "==="
  "!=="
  "<"
  "<="
  ">"
  ">="
  "&&"
  "||"
  "&"
  "|"
  "^"
  "<<"
  ">>"
  ">>>"
  "+"
  "-"
  "*"
  "/"
  "%"
  "=>"
  "->"
  "?"
  ":"
  "in"
  "instanceof"
] @prepend_space @append_space

;; Unary operators (no space after)
(unary_expression
  [
    "!"
    "~"
    "-"
    "+"
    "++"
    "--"
  ] @append_antispace
)

;; Update expressions (no space before ++ / --)
(update_expression
  [
    "++"
    "--"
  ] @prepend_antispace
)

;; --------------------------------------------------------------------
;; 5. Delimiters, Punctuation & Braces
;; --------------------------------------------------------------------

"," @prepend_antispace @append_space
";" @prepend_antispace

;; Member access and static scope (obj.field, Class::method)
(member_expression
  [
    "."
    "?."
    "*."
  ] @prepend_antispace @append_antispace
)

(static_member_expression
  "::" @prepend_antispace @append_antispace
)

;; Calls and Subscripts (no space before opening delimiter)
(call_expression
  "(" @prepend_antispace
)

(subscript_expression
  "[" @prepend_antispace
)

;; Parentheses and bracket padding (clean inner content)
"(" @append_antispace
")" @prepend_antispace
"[" @append_antispace
"]" @prepend_antispace

;; Type spacing in declarations and parameters
(variable_declaration
  (type) @append_space
)

(parameter
  (type) @append_space
)

(function_definition
  (type) @append_space
)

(function_definition
  "(" @prepend_antispace
)

(method_definition
  "(" @prepend_antispace
)

(import_statement
  "(" @prepend_antispace
)

(package_statement
  "(" @prepend_antispace
)

(method_definition
  (type) @append_space
)

(cast_expression
  ")" @append_space
)

;; Space before opening brace of blocks / closures
(function_definition
  (block
    "{" @prepend_space
  )
)

(closure_expression
  "{" @prepend_space
)

(if_statement
  (block
    "{" @prepend_space
  )
)

(while_statement
  (block
    "{" @prepend_space
  )
)

(do_while_statement
  (block
    "{" @prepend_space
  )
)

(for_statement
  (block
    "{" @prepend_space
  )
)

(foreach_statement
  (block
    "{" @prepend_space
  )
)

(switch_statement
  "{" @prepend_space
)

(try_statement
  (block
    "{" @prepend_space
  )
)

(catch_clause
  (block
    "{" @prepend_space
  )
)

(finally_clause
  (block
    "{" @prepend_space
  )
)

(class_definition
  (class_body
    "{" @prepend_space
  )
)

;; --------------------------------------------------------------------
;; 6. Comments
;; --------------------------------------------------------------------

(comment) @append_hardline @allow_blank_line_before
