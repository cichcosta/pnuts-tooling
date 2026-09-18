/**
 * Tree-sitter grammar for Pnuts (.pnuts)
 */

module.exports = grammar({
  name: 'pnuts',

  extras: $ => [
    $.comment,
    /[\s\uFEFF\u00A0\u1680\u2000-\u200A\u202F\u205F\u3000\ufeff]/
  ],

  supertypes: $ => [
    $._statement,
    $._expression,
  ],

  conflicts: $ => [
    [$.closure_expression, $.block],
    [$._statement, $._expression],
    [$._expression, $.tuple_expression],
    [$.tuple_expression, $.closure_expression],
    [$.for_statement, $.tuple_expression],
    [$.for_statement, $._expression],
    [$.new_expression],
    [$.type, $.new_expression],
    [$.wildcard_import, $.package_path],
    [$.package_path, $._expression],
    [$.identifier, $.property_identifier],
  ],

  rules: {
    source_file: $ => repeat($._statement),

    _statement: $ => choice(
      $.block,
      $.if_statement,
      $.while_statement,
      $.do_while_statement,
      $.for_statement,
      $.foreach_statement,
      $.switch_statement,
      $.function_definition,
      $.class_definition,
      $.try_statement,
      $.import_statement,
      $.package_statement,
      $.variable_declaration,
      $.return_statement,
      $.yield_statement,
      $.throw_statement,
      $.break_statement,
      $.continue_statement,
      $.empty_statement,
      $.expression_statement
    ),

    empty_statement: $ => ';',

    block: $ => seq(
      '{',
      repeat($._statement),
      '}'
    ),

    expression_statement: $ => prec.right(1, seq(
      $._expression,
      optional(';')
    )),

    if_statement: $ => prec.right(seq(
      'if',
      '(',
      $._expression,
      ')',
      $._statement,
      optional(seq('else', $._statement))
    )),

    while_statement: $ => seq(
      'while',
      '(',
      $._expression,
      ')',
      $._statement
    ),

    do_while_statement: $ => prec.right(seq(
      'do',
      $._statement,
      'while',
      '(',
      $._expression,
      ')',
      optional(';')
    )),

    for_statement: $ => seq(
      'for',
      '(',
      choice(
        // C-style for loop: for (i = 0; i < 10; i++) or for (int i = 0; i < 10; i++)
        seq(
          optional(choice(
            seq($.type, $.identifier, optional(seq('=', $._expression))),
            $._expression
          )),
          ';',
          optional($._expression),
          ';',
          optional($._expression)
        ),
        // Iterator for loop: for (i : list) or for (i in list) or for (i : 0..10) or for (k, v : map)
        seq(
          optional($.type),
          commaSep1(choice($.identifier, $.global_identifier)),
          choice(':', 'in'),
          $._expression,
          optional(seq('..', $._expression))
        )
      ),
      ')',
      $._statement
    ),

    foreach_statement: $ => choice(
      prec(2, seq(
        'foreach',
        optional($.identifier),
        choice(
          seq('(', $._expression, ')'),
          seq('[', optional($.argument_list), ']')
        ),
        $._statement
      )),
      prec(1, seq('foreach', ';'))
    ),

    switch_statement: $ => seq(
      'switch',
      '(',
      $._expression,
      ')',
      '{',
      repeat(choice($.switch_case, $.switch_default)),
      '}'
    ),

    switch_case: $ => seq(
      'case',
      $._expression,
      ':',
      repeat($._statement)
    ),

    switch_default: $ => seq(
      'default',
      ':',
      repeat($._statement)
    ),

    function_definition: $ => prec(2, seq(
      choice(
        seq('function', optional($.identifier)),
        seq($.type, $.identifier)
      ),
      '(',
      optional($.parameter_list),
      ')',
      $.block
    )),

    class_definition: $ => prec(17, seq(
      'class',
      $.package_path,
      optional(seq('extends', $.package_path)),
      optional(seq('implements', commaSep1($.package_path))),
      $.class_body
    )),

    class_body: $ => seq(
      '{',
      repeat(choice(
        $.method_definition,
        $._statement
      )),
      '}'
    ),

    method_definition: $ => prec(2, seq(
      optional($.type),
      $.property_identifier,
      '(',
      optional($.parameter_list),
      ')',
      $.block
    )),

    variable_declaration: $ => prec.right(1, seq(
      $.type,
      $.identifier,
      optional(seq('=', $._expression)),
      optional(';')
    )),

    try_statement: $ => seq(
      'try',
      $.block,
      repeat($.catch_clause),
      optional($.finally_clause)
    ),

    catch_clause: $ => prec.right(seq(
      'catch',
      '(',
      choice(
        seq($.type, choice($.identifier, $.global_identifier)),
        commaSep1($._expression)
      ),
      ')',
      optional($.block)
    )),

    finally_clause: $ => seq(
      'finally',
      $.block
    ),

    import_statement: $ => prec.right(seq(
      'import',
      optional(choice(
        seq('(', optional($.argument_list), ')'),
        seq(optional('static'), choice('*', $.wildcard_import, $.package_path))
      )),
      optional(';')
    )),

    wildcard_import: $ => seq(
      repeat1(seq($.identifier, '.')),
      '*'
    ),

    package_statement: $ => prec.right(seq(
      'package',
      optional(choice(
        seq('(', optional($.argument_list), ')'),
        $.package_path
      )),
      optional(';')
    )),

    package_path: $ => prec.left(sep1($.identifier, '.')),

    type: $ => prec(2, seq(
      choice(
        $.primitive_type,
        $.package_path
      ),
      repeat(seq('[', ']'))
    )),

    primitive_type: $ => choice(
      'def',
      'var',
      'boolean',
      'byte',
      'char',
      'short',
      'int',
      'long',
      'float',
      'double',
      'void'
    ),

    return_statement: $ => prec.right(seq(
      'return',
      optional($._expression),
      optional(';')
    )),

    throw_statement: $ => prec.right(seq(
      'throw',
      $._expression,
      optional(';')
    )),

    break_statement: $ => prec.right(seq(
      'break',
      optional(';')
    )),

    yield_statement: $ => prec.right(seq(
      'yield',
      optional($._expression),
      optional(';')
    )),

    continue_statement: $ => prec.right(seq(
      'continue',
      optional(';')
    )),

    parameter_list: $ => commaSep1($.parameter),

    parameter: $ => seq(
      optional($.type),
      $.identifier,
      optional(seq('[', ']'))
    ),

    argument_list: $ => commaSep1($._expression),

    _expression: $ => choice(
      $.if_statement,
      $.assignment_expression,
      $.binary_expression,
      $.unary_expression,
      $.update_expression,
      $.cast_expression,
      $.ternary_expression,
      $.call_expression,
      $.member_expression,
      $.static_member_expression,
      $.subscript_expression,
      $.closure_expression,
      $.function_definition,
      $.class_expression,
      $.new_expression,
      $.array_literal,
      $.map_literal,
      $.parenthesized_expression,
      $.template_string,
      $.string_literal,
      $.number_literal,
      $.boolean_literal,
      $.null_literal,
      $.identifier,
      $.global_identifier,
      $.primitive_type
    ),

    assignment_expression: $ => prec.right(1, seq(
      choice(
        $.identifier,
        $.global_identifier,
        $.member_expression,
        $.static_member_expression,
        $.subscript_expression,
        $.array_literal,
        $.tuple_expression
      ),
      choice('=', '+=', '-=', '*=', '/=', '%=', '&=', '|=', '^=', '<<=', '>>=', '>>>='),
      choice($._expression, $.tuple_expression)
    )),

    tuple_expression: $ => prec.left(seq(
      choice($.identifier, $.global_identifier, $.number_literal, $.string_literal, $.boolean_literal, $.null_literal),
      repeat1(seq(',', choice($.identifier, $.global_identifier, $.number_literal, $.string_literal, $.boolean_literal, $.null_literal)))
    )),

    class_expression: $ => prec(16, seq(
      'class',
      choice(
        seq('(', optional($.argument_list), ')'),
        $.package_path
      )
    )),

    cast_expression: $ => prec.right(13, seq(
      '(',
      $.type,
      ')',
      $._expression
    )),

    binary_expression: $ => choice(
      ...[
        ['..', 2],
        ['||', 3],
        ['&&', 4],
        ['|', 5],
        ['^', 6],
        ['&', 7],
        ['==', 8],
        ['!=', 8],
        ['===', 8],
        ['!==', 8],
        ['<', 9],
        ['<=', 9],
        ['>', 9],
        ['>=', 9],
        ['instanceof', 9],
        ['in', 9],
        ['<<', 10],
        ['>>', 10],
        ['>>>', 10],
        ['+', 11],
        ['-', 11],
        ['*', 12],
        ['/', 12],
        ['%', 12],
      ].map(([operator, precedence]) =>
        prec.left(precedence, seq(
          $._expression,
          operator,
          $._expression
        ))
      )
    ),

    unary_expression: $ => prec(13, seq(
      choice('!', '~', '-', '+', '++', '--'),
      $._expression
    )),

    update_expression: $ => prec(14, seq(
      $._expression,
      choice('++', '--')
    )),

    ternary_expression: $ => prec.right(1, seq(
      $._expression,
      '?',
      $._expression,
      ':',
      $._expression
    )),

    call_expression: $ => prec(14, seq(
      $._expression,
      '(',
      optional($.argument_list),
      ')'
    )),

    member_expression: $ => prec(15, seq(
      $._expression,
      choice('.', '?.', '*.'),
      choice($.property_identifier, 'class')
    )),

    static_member_expression: $ => prec(15, seq(
      $._expression,
      '::',
      choice($.property_identifier, 'class')
    )),

    subscript_expression: $ => prec(15, seq(
      $._expression,
      '[',
      choice(
        optional($._expression),
        seq($._expression, '..', optional($._expression))
      ),
      ']'
    )),

    closure_expression: $ => seq(
      '{',
      optional(seq(commaSep1($.identifier), '->')),
      repeat($._statement),
      '}'
    ),

    new_expression: $ => prec(16, seq(
      'new',
      choice(
        seq(
          $.package_path,
          '(',
          optional($.argument_list),
          ')',
          optional($.class_body)
        ),
        seq(
          choice($.primitive_type, $.package_path),
          repeat1(seq('[', optional($._expression), ']')),
          optional(seq('{', optional($.argument_list), '}'))
        ),
        $._expression
      )
    )),

    array_literal: $ => seq(
      optional(seq('(', choice($.primitive_type, $.package_path), '[])')),
      '[',
      optional($.argument_list),
      ']'
    ),

    map_literal: $ => choice(
      seq('[', ':', ']'),
      seq('[', commaSep1($.map_entry), ']'),
      seq('{', commaSep1($.map_entry), '}')
    ),

    map_entry: $ => seq(
      $._expression,
      choice('=>', ':'),
      $._expression
    ),

    parenthesized_expression: $ => seq(
      '(',
      $._expression,
      ')'
    ),

    template_string: $ => seq(
      '`',
      repeat(choice(
        $.template_substitution,
        $.template_content
      )),
      '`'
    ),

    template_substitution: $ => choice(
      seq('${', $._expression, '}'),
      seq('\\(', $._expression, '\\)')
    ),

    template_content: $ => token.immediate(/[^`\\$]+|\$|\\/),

    string_literal: $ => token(choice(
      seq('"""', repeat(choice(/[^"\\]+/, /"[^"\\]+/, /""[^"\\]+/, seq('\\', /[\s\S]/))), '"""'),
      seq("'''", repeat(choice(/[^'\\]+/, /'[^'\\]+/, /''[^'\\]+/, seq('\\', /[\s\S]/))), "'''"),
      seq('"', repeat(choice(/[^"\\\r\n]+/, seq('\\', /[\s\S]/))), '"'),
      seq("'", repeat(choice(/[^'\\\r\n]+/, seq('\\', /[\s\S]/))), "'")
    )),

    number_literal: $ => token(seq(
      choice(
        /0[xX][0-9a-fA-F]+/,
        /#[0-9a-fA-F]+/,
        /\d+(\.\d+)?([eE][+-]?\d+)?/,
        /\.\d+([eE][+-]?\d+)?/
      ),
      optional(/[a-zA-Z]+/) // unit quantity e.g. 1.2cm
    )),

    boolean_literal: $ => choice('true', 'false'),

    null_literal: $ => 'null',

    global_identifier: $ => seq('::', $.identifier),

    identifier: $ => /[a-zA-Z_$][a-zA-Z0-9_$]*/,
    property_identifier: $ => /[a-zA-Z_$][a-zA-Z0-9_$]*/,

    comment: $ => token(choice(
      seq('//', /.*/),
      seq('/*', /[^*]*\*+([^/*][^*]*\*+)*/, '/')
    ))
  }
});

function commaSep1(rule) {
  return seq(rule, repeat(seq(',', rule)));
}

function sep1(rule, separator) {
  return seq(rule, repeat(seq(separator, rule)));
}
