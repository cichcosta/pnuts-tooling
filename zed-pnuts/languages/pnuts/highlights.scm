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

;; Identifiers & Variables
(identifier) @variable
(global_identifier) @variable.builtin

;; Parameters
(parameter
  (identifier) @variable.parameter)

;; Property Access (obj.prop or Class::prop)
(property_identifier) @property

;; Regular Function Calls (func())
(call_expression
  (identifier) @function)

;; Method Calls (obj.method() or Class::method())
(call_expression
  (member_expression
    (property_identifier) @function.method @function))

(call_expression
  (static_member_expression
    (property_identifier) @function.method @function))

;; Function & Method Definitions
(function_definition
  (identifier) @function)

(method_definition
  (property_identifier) @function.method @function)

;; Special Identifiers (this, super)
((identifier) @variable.special
 (#match? @variable.special "^(this|super)$"))

;; Constants (ALL_CAPS)
((identifier) @constant
 (#match? @constant "^[A-Z][A-Z0-9_]+$"))

((property_identifier) @constant
 (#match? @constant "^[A-Z][A-Z0-9_]+$"))

;; Types / Classes (PascalCase)
((identifier) @type
 (#match? @type "^[A-Z][a-zA-Z0-9_]*$"))

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
