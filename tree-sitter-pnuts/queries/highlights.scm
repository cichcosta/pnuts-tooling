;; Keywords
[
  "if"
  "else"
  "while"
  "do"
  "for"
  "foreach"
  "switch"
  "case"
  "default"
  "return"
  "yield"
  "try"
  "catch"
  "finally"
  "throw"
  "break"
  "import"
  "package"
  "static"
  "new"
  "class"
  "extends"
  "implements"
  "instanceof"
] @keyword

(continue_statement) @keyword
"function" @keyword.function

;; Types
(primitive_type) @type.builtin
(type
  (package_path
    (identifier) @type))

;; Literals
(boolean_literal) @boolean
(null_literal) @constant.builtin
(string_literal) @string
(template_string) @string
(number_literal) @number
(comment) @comment

;; Functions & Methods
(function_definition
  (identifier) @function)

(method_definition
  (identifier) @function.method)

(call_expression
  (identifier) @function)

(call_expression
  (member_expression
    (identifier) @function.method))

(call_expression
  (static_member_expression
    (identifier) @function.method))

;; Properties & Variables
(member_expression
  (identifier) @property)

(static_member_expression
  (identifier) @property)

(global_identifier) @variable.builtin

(identifier) @variable

;; Operators & Delimiters
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
  "!"
  "?"
  ":"
  "=>"
  "->"
  ".."
  "+"
  "-"
  "*"
  "/"
  "%"
  "++"
  "--"
] @operator

[
  "("
  ")"
  "["
  "]"
  "{"
  "}"
  ";"
  ","
  "."
  "::"
] @punctuation.delimiter
