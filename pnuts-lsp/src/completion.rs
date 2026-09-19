use std::collections::HashSet;
use tower_lsp::lsp_types::*;
use tree_sitter::{Node, Tree};

/// Keywords supported by Pnuts and Groovy scripting.
pub(crate) const KEYWORDS: &[(&str, &str)] = &[
    ("function", "Defines a function: function name(args) { ... }"),
    ("class", "Defines a class: class Name { ... }"),
    ("if", "Conditional branching: if (condition) { ... }"),
    ("else", "Alternative branch: else { ... }"),
    ("for", "Loop iteration: for (x in y) or for (i=0; i<n; i++)"),
    ("foreach", "Iteration loop: foreach item (collection) { ... }"),
    ("while", "While loop: while (condition) { ... }"),
    ("do", "Do-while loop: do { ... } while (condition);"),
    ("try", "Exception handling block: try { ... } catch (e) { ... }"),
    ("catch", "Catches exception: catch (Exception e) { ... }"),
    ("finally", "Guaranteed cleanup block: finally { ... }"),
    ("throw", "Throws an exception: throw new Exception(...)"),
    ("return", "Returns value from function: return value;"),
    ("break", "Breaks out of loop or switch"),
    ("continue", "Continues to next loop iteration"),
    ("switch", "Switch selection: switch (expr) { case ... }"),
    ("case", "Switch branch: case value:"),
    ("default", "Default switch branch: default:"),
    ("import", "Imports Java class or package: import java.util.*"),
    ("package", "Declares namespace package: package com.mycorp"),
    ("null", "Null literal reference"),
    ("true", "Boolean true literal"),
    ("false", "Boolean false literal"),
    ("new", "Instantiates an object: new ClassName(...)"),
    ("var", "Declares a local variable: var x = 10;"),
    ("def", "Declares dynamically-typed variable or method"),
];

/// Built-in and domain functions common to Pnuts environments and enterprise business scripts.
pub(crate) const BUILTIN_FUNCTIONS: &[(&str, &str, &str, &str)] = &[
    // Pnuts Core Primitives & Output
    ("println", "(msg)", "println(${1:message});", "Prints message followed by newline to stdout"),
    ("print", "(msg)", "print(${1:message});", "Prints message without newline to stdout"),
    ("flush", "()", "flush()", "Flushes the standard output buffer immediately"),
    ("char", "(code)", "char(${1:34})", "Returns character for ASCII/Unicode code (e.g. char(34) -> '\"')"),
    ("open", "(filename)", "open(\"${1:filename}\")", "Opens a file or stream resource"),
    ("load", "(module)", "load(\"${1:module}\")", "Loads and evaluates an external script or module"),
    ("loadFile", "(filepath)", "loadFile(\"${1:filepath}\")", "Loads and evaluates a script from local file path"),
    ("require", "(module)", "require(\"${1:module}\")", "Loads module only if it has not been loaded yet"),
    ("use", "(packagename)", "use(\"${1:packagename}\")", "Imports module package utilities (e.g. use('pnuts.lib'))"),
    ("eval", "(script)", "eval(\"${1:script}\")", "Dynamically evaluates a Pnuts expression string in current context"),
    ("error", "(msg)", "error(\"${1:msg}\")", "Throws a runtime error with message"),
    ("getContext", "()", "getContext()", "Retrieves the current Pnuts execution Context"),
    ("isArray", "(obj)", "isArray(${1:obj})", "Checks if target object is an array"),
    ("isFunction", "(obj)", "isFunction(${1:obj})", "Checks if target object is a callable function"),
    ("defined", "(varName)", "defined(\"${1:varName}\")", "Checks whether a variable or symbol is defined in scope"),

    // Database & Recordset Operations
    ("getRows", "(table, sqlQuery)", "getRows(\"${1:TABLE}\", \"${2:select * from ...}\")", "Executes SQL query and returns list/Vector of table records"),
    ("getMRows", "(table, sqlQuery)", "getMRows(\"${1:TABLE}\", \"${2:select * from ...}\")", "Executes SQL query and returns list/Vector of mapped rows (MRow) with named columns"),
    ("getRow", "(table, sqlQuery)", "getRow(\"${1:TABLE}\", \"${2:select * from ...}\")", "Executes SQL query and returns a single table record"),
    ("getMRow", "(table, [sqlQuery])", "getMRow(\"${1:TABLE}\")", "Instantiates a new MRow or retrieves single mapped row for table"),
    ("getSQLNumber", "(sqlQuery)", "getSQLNumber(\"${1:select count(1) from ...}\")", "Executes SQL query and returns a single numeric scalar result (e.g. count, sum)"),
    ("getSQVNumber", "(sqlQuery)", "getSQVNumber(\"${1:select count(1) from ...}\")", "Executes SQL query and returns a single numeric scalar result (alias of getSQLNumber)"),
    ("getSQLValue", "(sqlQuery)", "getSQLValue(\"${1:select col from ...}\")", "Executes SQL query and returns a single scalar value/string"),
    ("getSQLValues", "(sqlQuery)", "getSQLValues(\"${1:select col from ...}\")", "Executes SQL query and returns list/Vector of scalar values from the query"),
    ("executeSQL", "(sqlStatement)", "executeSQL(\"${1:update table set ...}\");", "Executes an INSERT, UPDATE, DELETE or DDL SQL statement"),
    ("executeProcedure", "(procName, [params])", "executeProcedure(\"${1:procName}\", [${2:params}])", "Executes a database stored procedure with arguments"),
    ("openQuery", "(sqlQuery)", "openQuery(\"${1:sqlQuery}\")", "Opens a database query cursor for iteration"),
    ("getMCursor", "()", "getMCursor()", "Retrieves current database transaction cursor (MConnection::getMCursor())"),
    ("beginWork", "()", "beginWork()", "Begins a database transaction on cursor"),
    ("commitWork", "()", "commitWork()", "Commits the active database transaction"),
    ("rollbackWork", "()", "rollbackWork()", "Rolls back the active database transaction"),
    ("insertRow", "(row)", "insertRow(${1:row})", "Inserts row record into database table"),
    ("updateRow", "(row)", "updateRow(${1:row})", "Updates row record in database table"),
    ("deleteRow", "(row)", "deleteRow(${1:row})", "Deletes row record from database table"),
    ("refreshRow", "()", "refreshRow()", "Refreshes current row buffer from database"),
    ("refresh", "()", "refresh()", "Refreshes current UI form or dataset"),

    // User Interface & Error Handling
    ("showMessage", "(msg)", "showMessage(\"${1:DEBUG}: \" + ${2:variable}.toString());", "Displays GUI message dialog alert box to user"),
    ("showError", "(code, [params])", "showError(${1:code}, [${2:params}])", "Displays error dialog box with error code or parameters"),
    ("throwError", "(msg)", "throwError(\"${1:Erro na execução}\");", "Aborts execution and throws application error message dialog"),
    ("getDialogForm", "(parent, name, mode, title, size)", "getDialogForm(${1:parent}, \"${2:name}\", ${3:mode}, \"${4:title}\", ${5:size})", "Opens dialog form window with title and modal mode"),
    ("showForm", "(form)", "showForm(${1:form})", "Displays form window on screen"),
    ("expandForm", "(form, flag)", "expandForm(${1:form}, ${2:flag})", "Expands or collapses form components"),
    ("setFormat", "(mask)", "setFormat(\"${1:mask}\")", "Sets display/edit mask format on form component"),
    ("getUser", "()", "getUser()", "Returns current logged-in user name (MLogin::getUser())"),
    ("getUserNodo", "()", "getUserNodo()", "Returns current network station or workstation node ID"),
    ("getTparms", "(category, param)", "getTparms(\"${1:category}\", \"${2:param}\")", "Retrieves system parameter value from parameter configuration table"),
    ("getParam", "(name)", "getParam(\"${1:name}\")", "Retrieves parameter from execution context or form"),
    ("setParam", "(name, value)", "setParam(\"${1:name}\", ${2:value})", "Sets parameter into execution context or form"),

    // Number & Math Operations
    ("formatNumber", "(value, mask)", "formatNumber(${1:value}, \"${2:N0.00}\")", "Formats number using format mask (e.g. formatNumber(val, 'N0.00'))"),
    ("formatNumero", "(value, mask)", "formatNumero(${1:value}, \"${2:N0.00}\")", "Formats number using format mask (alias of formatNumber)"),
    ("round", "(value, decimals)", "round(${1:value}, ${2:2})", "Rounds number or BigDecimal to specified decimal places"),
    ("toNumber", "(value)", "toNumber(${1:value})", "Converts string or object to number"),
    ("toBigDecimal", "(value)", "toBigDecimal(${1:value})", "Converts number or string to java.math.BigDecimal"),
    ("today", "()", "today()", "Returns current system date object"),

    // Collections & String Utilities
    ("inList", "(item, list)", "inList(${1:item}, [${2:values}])", "Checks if item exists in collection, array, or list of values"),
    ("trim", "(str)", "trim(${1:str})", "Removes leading and trailing whitespace from string"),
    ("padRight", "(str, len, [padChar])", "padRight(${1:str}, ${2:len})", "Pads string to length with spaces or specified character on right"),
    ("padLeft", "(str, len, [padChar])", "padLeft(${1:str}, ${2:len})", "Pads string to length with spaces or specified character on left"),
    ("substring", "(str, start, [end])", "substring(${1:str}, ${2:start}, ${3:end})", "Returns substring between character indices"),
    ("indexOf", "(str, search)", "indexOf(${1:str}, \"${2:search}\")", "Returns index of search term within string or collection"),
    ("replaceAll", "(str, regex, replacement)", "replaceAll(${1:str}, \"${2:regex}\", \"${3:replacement}\")", "Replaces all occurrences of regex pattern in string"),
    ("replace", "(str, oldStr, newStr)", "replace(${1:str}, \"${2:oldStr}\", \"${3:newStr}\")", "Replaces occurrences of substring with replacement string"),
    ("toUpperCase", "(str)", "toUpperCase(${1:str})", "Converts string characters to uppercase"),
    ("toLowerCase", "(str)", "toLowerCase(${1:str})", "Converts string characters to lowercase"),
    ("split", "(str, delimiter)", "split(${1:str}, \"${2:delimiter}\")", "Splits string by delimiter into array of strings"),
    ("elements", "()", "elements()", "Enumerates elements of collection or Vector for foreach iteration"),
    ("length", "()", "length()", "Returns length or count of elements"),
    ("toString", "()", "toString()", "Converts object to string representation"),
    ("digit", "(char)", "digit(${1:char})", "Returns numeric digit value of character"),
    ("montaTag", "(tag, val)", "montaTag(\"${1:tag}\", ${2:val})", "Constructs XML/JSON tag element string"),
];

/// Common Java standard library and framework classes frequently used in Pnuts scripts.
pub(crate) const COMMON_JAVA_CLASSES: &[(&str, &str)] = &[
    ("List", "java.util.List interface"),
    ("ArrayList", "java.util.ArrayList resizable array implementation"),
    ("Map", "java.util.Map key-value collection interface"),
    ("HashMap", "java.util.HashMap hash table implementation"),
    ("Set", "java.util.Set unique collection interface"),
    ("HashSet", "java.util.HashSet hash table set implementation"),
    ("Vector", "java.util.Vector legacy thread-safe list"),
    ("String", "java.lang.String character sequence"),
    ("StringBuilder", "java.lang.StringBuilder mutable character sequence"),
    ("StringBuffer", "java.lang.StringBuffer thread-safe mutable character sequence"),
    ("Integer", "java.lang.Integer boxed integer utility"),
    ("Double", "java.lang.Double boxed floating-point utility"),
    ("Boolean", "java.lang.Boolean boxed boolean utility"),
    ("BigDecimal", "java.math.BigDecimal arbitrary-precision decimal for currency & tax"),
    ("BigInteger", "java.math.BigInteger arbitrary-precision integer"),
    ("Date", "java.util.Date timestamp representation"),
    ("Calendar", "java.util.Calendar date/time calendar utility"),
    ("SimpleDateFormat", "java.text.SimpleDateFormat date formatting"),
    ("Exception", "java.lang.Exception standard checked exception"),
    ("RuntimeException", "java.lang.RuntimeException unchecked exception"),
    ("MConnection", "Database connection manager (MConnection::getMCursor())"),
    ("MLogin", "Authentication context utility (MLogin::getUser())"),
    ("MDate", "Framework date parsing and manipulation utility"),
    ("MFile", "File system and stream helper utility"),
    ("JSONObject", "org.json.JSONObject JSON object representation"),
    ("JSONArray", "org.json.JSONArray JSON array representation"),
];

/// Standard Java and Pnuts object methods commonly invoked on variables and expressions.
pub(crate) const COMMON_METHODS: &[(&str, &str, &str, &str)] = &[
    // String & Text Methods
    ("trim", "()", "trim()", "Removes leading and trailing whitespace"),
    ("substring", "(start, [end])", "substring(${1:start}, ${2:end})", "Extracts substring between character indices"),
    ("indexOf", "(str)", "indexOf(\"${1:str}\")", "Returns index of first occurrence of substring or character"),
    ("lastIndexOf", "(str)", "lastIndexOf(\"${1:str}\")", "Returns index of last occurrence of substring or character"),
    ("startsWith", "(prefix)", "startsWith(\"${1:prefix}\")", "Tests if this string starts with the specified prefix"),
    ("endsWith", "(suffix)", "endsWith(\"${1:suffix}\")", "Tests if this string ends with the specified suffix"),
    ("replace", "(oldStr, newStr)", "replace(\"${1:oldStr}\", \"${2:newStr}\")", "Replaces all occurrences of target sequence with replacement"),
    ("replaceAll", "(regex, replacement)", "replaceAll(\"${1:regex}\", \"${2:replacement}\")", "Replaces each substring matching regex pattern"),
    ("toUpperCase", "()", "toUpperCase()", "Converts all characters in string to uppercase"),
    ("toLowerCase", "()", "toLowerCase()", "Converts all characters in string to lowercase"),
    ("split", "(delimiter)", "split(\"${1:delimiter}\")", "Splits this string around matches of given delimiter / regex"),
    ("charAt", "(index)", "charAt(${1:index})", "Returns char value at specified index"),
    ("equalsIgnoreCase", "(anotherString)", "equalsIgnoreCase(\"${1:another}\")", "Compares string ignoring case considerations"),

    // Collections & Arrays & Iteration
    ("size", "()", "size()", "Returns number of elements in collection or map"),
    ("length", "()", "length()", "Returns length of string, array or collection"),
    ("isEmpty", "()", "isEmpty()", "Returns true if collection or string contains no elements"),
    ("get", "(indexOrKey)", "get(${1:indexOrKey})", "Retrieves element at index or value mapped to key"),
    ("put", "(key, value)", "put(${1:key}, ${2:value})", "Associates specified value with specified key in map"),
    ("add", "(element)", "add(${1:element})", "Appends specified element to collection or list"),
    ("remove", "(indexOrKey)", "remove(${1:indexOrKey})", "Removes element at specified index or mapped key"),
    ("clear", "()", "clear()", "Removes all elements from collection or map"),
    ("contains", "(element)", "contains(${1:element})", "Returns true if collection contains specified element"),
    ("containsKey", "(key)", "containsKey(${1:key})", "Returns true if map contains mapping for specified key"),
    ("containsValue", "(value)", "containsValue(${1:value})", "Returns true if map maps one or more keys to value"),
    ("keySet", "()", "keySet()", "Returns Set view of keys contained in map"),
    ("values", "()", "values()", "Returns Collection view of values contained in map"),
    ("elements", "()", "elements()", "Returns enumeration of elements in collection or Vector for iteration"),
    ("hasMoreElements", "()", "hasMoreElements()", "Tests if this enumeration contains more elements"),
    ("nextElement", "()", "nextElement()", "Returns next element of this enumeration"),
    ("iterator", "()", "iterator()", "Returns iterator over elements in collection"),
    ("hasNext", "()", "hasNext()", "Returns true if iteration has more elements"),
    ("next", "()", "next()", "Returns next element in iteration"),

    // Conversions & Common Object Methods
    ("toString", "()", "toString()", "Returns string representation of object"),
    ("equals", "(obj)", "equals(${1:obj})", "Indicates whether some other object is equal to this one"),
    ("compareTo", "(another)", "compareTo(${1:another})", "Compares this object with another for order"),
    ("intValue", "()", "intValue()", "Returns numeric value as primitive int"),
    ("longValue", "()", "longValue()", "Returns numeric value as primitive long"),
    ("doubleValue", "()", "doubleValue()", "Returns numeric value as primitive double"),

    // ERP & Database / Recordset Column Getters
    ("getString", "(columnName)", "getString(\"${1:columnName}\")", "Retrieves value of designated column as string"),
    ("getInt", "(columnName)", "getInt(\"${1:columnName}\")", "Retrieves value of designated column as integer"),
    ("getDouble", "(columnName)", "getDouble(\"${1:columnName}\")", "Retrieves value of designated column as double"),
    ("getBigDecimal", "(columnName)", "getBigDecimal(\"${1:columnName}\")", "Retrieves value of designated column as BigDecimal"),
    ("getDate", "(columnName)", "getDate(\"${1:columnName}\")", "Retrieves value of designated column as Date/timestamp"),
    ("set", "(columnName, value)", "set(\"${1:columnName}\", ${2:value})", "Sets value of designated column in row buffer"),

    // Cursor / Resource Lifecycle
    ("beginWork", "()", "beginWork()", "Begins database transaction on cursor (MConnection::getMCursor())"),
    ("commitWork", "()", "commitWork()", "Commits active database transaction"),
    ("rollbackWork", "()", "rollbackWork()", "Rolls back active database transaction"),
    ("close", "()", "close()", "Closes stream, database cursor, or resource"),
    ("flush", "()", "flush()", "Flushes buffered output stream immediately"),
];

/// Structure describing a code snippet template.
struct Snippet {
    label: &'static str,
    snippet: &'static str,
    detail: &'static str,
}

const SNIPPETS: &[Snippet] = &[
    Snippet {
        label: "if",
        snippet: "if (${1:condition}) {\n\t$0\n}",
        detail: "if condition block",
    },
    Snippet {
        label: "ifelse",
        snippet: "if (${1:condition}) {\n\t$2\n} else {\n\t$0\n}",
        detail: "if / else condition block",
    },
    Snippet {
        label: "if-else",
        snippet: "if (${1:condition}) {\n\t$2\n} else {\n\t$0\n}",
        detail: "if / else condition block (alias)",
    },
    Snippet {
        label: "while",
        snippet: "while (${1:condition}) {\n\t$0\n}",
        detail: "while loop block",
    },
    Snippet {
        label: "dowhile",
        snippet: "do {\n\t$0\n} while (${1:condition});",
        detail: "do-while loop block",
    },
    Snippet {
        label: "for (i)",
        snippet: "for (${1:i} = 0; ${1:i} < ${2:max}; ${1:i}++) {\n\t$0\n}",
        detail: "Indexed for loop",
    },
    Snippet {
        label: "for (in)",
        snippet: "for (${1:item} in ${2:collection}) {\n\t$0\n}",
        detail: "for (item in collection) loop",
    },
    Snippet {
        label: "foreach (in)",
        snippet: "for (${1:item} in ${2:collection}) {\n\t$0\n}",
        detail: "for (item in collection) loop",
    },
    Snippet {
        label: "forin",
        snippet: "for (${1:item} in ${2:collection}) {\n\t$0\n}",
        detail: "for (item in collection) loop (alias)",
    },
    Snippet {
        label: "foreach",
        snippet: "foreach ${1:item} (${2:collection}) {\n\t$0\n}",
        detail: "foreach item (collection) loop",
    },
    Snippet {
        label: "foreach (elements)",
        snippet: "foreach ${1:row} (${2:vrows}.elements()) {\n\t$0\n}",
        detail: "foreach row in collection elements()",
    },
    Snippet {
        label: "foreachelements",
        snippet: "foreach ${1:row} (${2:vrows}.elements()) {\n\t$0\n}",
        detail: "foreach row in collection elements() (alias)",
    },
    Snippet {
        label: "function",
        snippet: "function ${1:name}(${2:params}) {\n\t$0\n}",
        detail: "Function declaration",
    },
    Snippet {
        label: "fn",
        snippet: "${1:name} = function(${2:params}) {\n\t$0\n};",
        detail: "Function expression assignment",
    },
    Snippet {
        label: "trycatch",
        snippet: "try {\n\t$1\n} catch (${2:Exception} ${3:e}) {\n\t$0\n}",
        detail: "try / catch exception block",
    },
    Snippet {
        label: "try-catch",
        snippet: "try {\n\t$1\n} catch (${2:Exception} ${3:e}) {\n\t$0\n}",
        detail: "try / catch exception block (alias)",
    },
    Snippet {
        label: "trycatchfinally",
        snippet: "try {\n\t$1\n} catch (${2:Exception} ${3:e}) {\n\t$4\n} finally {\n\t$0\n}",
        detail: "try / catch / finally block",
    },
    Snippet {
        label: "try-catch-finally",
        snippet: "try {\n\t$1\n} catch (${2:Exception} ${3:e}) {\n\t$4\n} finally {\n\t$0\n}",
        detail: "try / catch / finally block (alias)",
    },
    Snippet {
        label: "switch",
        snippet: "switch (${1:expr}) {\n\tcase ${2:value}:\n\t\t$3\n\t\tbreak;\n\tdefault:\n\t\t$0\n}",
        detail: "switch / case block",
    },
    Snippet {
        label: "database-transaction",
        snippet: "cursor = MConnection::getMCursor();\ncursor.beginWork();\ntry {\n\t$0\n\tcursor.commitWork();\n} catch (Exception e) {\n\tcursor.rollbackWork();\n\tthrowError(e.getMessage());\n}",
        detail: "Database transaction block (beginWork / commitWork / rollbackWork)",
    },
    Snippet {
        label: "println-flush",
        snippet: "println(\"${1:DEBUG}: \" + ${2:variable}.toString()); flush();",
        detail: "Print line and flush immediately",
    },
    Snippet {
        label: "printlnflush",
        snippet: "println(\"${1:DEBUG}: \" + ${2:variable}.toString()); flush();",
        detail: "Print line and flush immediately (alias)",
    },
];

/// Collects all completion items relevant at the given cursor position.
pub fn provide_completions(
    tree: Option<&Tree>,
    source: &str,
    position: Position,
) -> Vec<CompletionItem> {
    let mut items = Vec::new();
    let mut seen = HashSet::new();

    let cursor_line = position.line as usize;

    // 1. AST Scope Inspection (Local variables, parameters, and declared functions)
    if let Some(t) = tree {
        let root = t.root_node();
        collect_ast_symbols(root, source, cursor_line, &mut items, &mut seen);
    }

    // 2. Common Object & Recordset Methods
    for &(name, sig, snippet, doc) in COMMON_METHODS {
        if seen.insert(name.to_string()) {
            items.push(CompletionItem {
                label: name.to_string(),
                kind: Some(CompletionItemKind::METHOD),
                detail: Some(format!("method {}{}", name, sig)),
                documentation: Some(Documentation::String(doc.to_string())),
                insert_text: Some(snippet.to_string()),
                insert_text_format: Some(InsertTextFormat::SNIPPET),
                sort_text: Some(format!("1_{}", name)),
                ..Default::default()
            });
        }
    }

    // 3. Built-in and Domain Functions
    for &(name, sig, snippet, doc) in BUILTIN_FUNCTIONS {
        if seen.insert(name.to_string()) {
            items.push(CompletionItem {
                label: name.to_string(),
                kind: Some(CompletionItemKind::FUNCTION),
                detail: Some(format!("{}{}", name, sig)),
                documentation: Some(Documentation::String(doc.to_string())),
                insert_text: Some(snippet.to_string()),
                insert_text_format: Some(InsertTextFormat::SNIPPET),
                sort_text: Some(format!("2_{}", name)),
                ..Default::default()
            });
        }
    }

    // 4. Code Block Snippets (control flow, loops, try-catch, transactions)
    for s in SNIPPETS {
        if seen.insert(s.label.to_string()) {
            items.push(CompletionItem {
                label: s.label.to_string(),
                kind: Some(CompletionItemKind::SNIPPET),
                detail: Some(s.detail.to_string()),
                insert_text: Some(s.snippet.to_string()),
                insert_text_format: Some(InsertTextFormat::SNIPPET),
                sort_text: Some(format!("3_{}", s.label)),
                ..Default::default()
            });
        }
    }

    // 5. Common Java Classes
    for &(cls, doc) in COMMON_JAVA_CLASSES {
        if seen.insert(cls.to_string()) {
            items.push(CompletionItem {
                label: cls.to_string(),
                kind: Some(CompletionItemKind::CLASS),
                detail: Some(doc.to_string()),
                insert_text: Some(cls.to_string()),
                insert_text_format: Some(InsertTextFormat::PLAIN_TEXT),
                sort_text: Some(format!("4_{}", cls)),
                ..Default::default()
            });
        }
    }

    // 5. Language Keywords
    for &(kw, doc) in KEYWORDS {
        if seen.insert(kw.to_string()) {
            items.push(CompletionItem {
                label: kw.to_string(),
                kind: Some(CompletionItemKind::KEYWORD),
                detail: Some("Keyword".to_string()),
                documentation: Some(Documentation::String(doc.to_string())),
                insert_text: Some(kw.to_string()),
                insert_text_format: Some(InsertTextFormat::PLAIN_TEXT),
                sort_text: Some(format!("5_{}", kw)),
                ..Default::default()
            });
        }
    }

    items
}

/// Recursively walks the Tree-sitter AST to extract variables and functions.
fn collect_ast_symbols(
    node: Node,
    source: &str,
    cursor_line: usize,
    items: &mut Vec<CompletionItem>,
    seen: &mut HashSet<String>,
) {
    let kind = node.kind();

    // Collect function definitions across the document
    if kind == "function_definition" {
        if let Some(name_node) = node.child_by_field_name("name").or_else(|| {
            // Find child node of kind "identifier" before parameter_list
            for i in 0..node.child_count() {
                if let Some(child) = node.child(i) {
                    if child.kind() == "identifier" {
                        return Some(child);
                    }
                }
            }
            None
        }) {
            if let Ok(name) = name_node.utf8_text(source.as_bytes()) {
                if !name.is_empty() && seen.insert(name.to_string()) {
                    let sig = extract_param_signature(node, source);
                    items.push(CompletionItem {
                        label: name.to_string(),
                        kind: Some(CompletionItemKind::FUNCTION),
                        detail: Some(format!("function {}{}", name, sig)),
                        insert_text: Some(format!("{}($0)", name)),
                        insert_text_format: Some(InsertTextFormat::SNIPPET),
                        sort_text: Some(format!("0_{}", name)),
                        ..Default::default()
                    });
                }
            }
        }

        // If the cursor is inside this function body, extract its parameters as variables!
        let start_row = node.start_position().row;
        let end_row = node.end_position().row;
        if cursor_line >= start_row && cursor_line <= end_row {
            extract_parameters(node, source, items, seen);
        }
    }

    // Collect variable declarations before cursor
    if node.start_position().row <= cursor_line {
        match kind {
            "variable_declaration" => {
                for i in 0..node.child_count() {
                    if let Some(child) = node.child(i) {
                        if child.kind() == "identifier" {
                            add_variable(child, source, "Local Variable", items, seen);
                        }
                    }
                }
            }
            "assignment_expression" => {
                // The target being assigned to is the first child
                if let Some(target) = node.child(0) {
                    if target.kind() == "identifier" {
                        add_variable(target, source, "Variable", items, seen);
                    }
                }
            }
            "for_statement" => {
                // Iterator loop: for (item in list)
                for i in 0..node.child_count() {
                    if let Some(child) = node.child(i) {
                        if child.kind() == "identifier" {
                            add_variable(child, source, "Loop Variable", items, seen);
                        }
                    }
                }
            }
            "foreach_statement" => {
                // foreach item (list)
                for i in 0..node.child_count() {
                    if let Some(child) = node.child(i) {
                        if child.kind() == "identifier" {
                            add_variable(child, source, "Foreach Variable", items, seen);
                        }
                    }
                }
            }
            "catch_clause" => {
                // catch (Exception e)
                for i in 0..node.child_count() {
                    if let Some(child) = node.child(i) {
                        if child.kind() == "identifier" {
                            // If previous sibling was type or Exception, this is the variable
                            add_variable(child, source, "Exception Variable", items, seen);
                        }
                    }
                }
            }
            _ => {}
        }
    }

    // Traverse children
    let count = node.child_count();
    for i in 0..count {
        if let Some(child) = node.child(i) {
            collect_ast_symbols(child, source, cursor_line, items, seen);
        }
    }
}

fn add_variable(
    node: Node,
    source: &str,
    detail: &str,
    items: &mut Vec<CompletionItem>,
    seen: &mut HashSet<String>,
) {
    if let Ok(name) = node.utf8_text(source.as_bytes()) {
        let name_str = name.trim();
        if !name_str.is_empty() && seen.insert(name_str.to_string()) {
            items.push(CompletionItem {
                label: name_str.to_string(),
                kind: Some(CompletionItemKind::VARIABLE),
                detail: Some(detail.to_string()),
                insert_text: Some(name_str.to_string()),
                insert_text_format: Some(InsertTextFormat::PLAIN_TEXT),
                sort_text: Some(format!("1_{}", name_str)),
                ..Default::default()
            });
        }
    }
}

fn extract_parameters(
    func_node: Node,
    source: &str,
    items: &mut Vec<CompletionItem>,
    seen: &mut HashSet<String>,
) {
    for i in 0..func_node.child_count() {
        if let Some(child) = func_node.child(i) {
            if child.kind() == "parameter_list" {
                for p_idx in 0..child.child_count() {
                    if let Some(param) = child.child(p_idx) {
                        if param.kind() == "parameter" {
                            for id_idx in 0..param.child_count() {
                                if let Some(id_node) = param.child(id_idx) {
                                    if id_node.kind() == "identifier" {
                                        add_variable(id_node, source, "Parameter", items, seen);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fn extract_param_signature(func_node: Node, source: &str) -> String {
    for i in 0..func_node.child_count() {
        if let Some(child) = func_node.child(i) {
            if child.kind() == "parameter_list" {
                if let Ok(text) = child.utf8_text(source.as_bytes()) {
                    return format!("({})", text);
                }
            }
        }
    }
    "()".to_string()
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::parser::parse_pnuts;

    #[test]
    fn test_keywords_and_snippets_available() {
        let code = "a = 1;\n";
        let tree = parse_pnuts(code, None).unwrap();
        let completions = provide_completions(Some(&tree), code, Position { line: 1, character: 0 });

        let labels: Vec<&str> = completions.iter().map(|c| c.label.as_str()).collect();

        // Check keywords
        assert!(labels.contains(&"function"));
        assert!(labels.contains(&"if"));
        assert!(labels.contains(&"foreach"));
        assert!(labels.contains(&"while"));
        assert!(labels.contains(&"return"));

        // Check snippets
        assert!(labels.contains(&"foreach (in)"));
        assert!(labels.contains(&"if-else"));
        assert!(labels.contains(&"println-flush"));

        // Check standard Java classes
        assert!(labels.contains(&"List"));
        assert!(labels.contains(&"ArrayList"));
        assert!(labels.contains(&"Map"));
        assert!(labels.contains(&"HashMap"));
    }

    #[test]
    fn test_ast_local_variable_extraction() {
        let code = r#"
            totalPrice = 1500;
            userRole = "admin";
            println(totalPrice);
        "#;
        let tree = parse_pnuts(code, None).unwrap();
        let completions = provide_completions(Some(&tree), code, Position { line: 3, character: 0 });

        let var_items: Vec<&CompletionItem> = completions
            .iter()
            .filter(|c| c.kind == Some(CompletionItemKind::VARIABLE))
            .collect();

        let var_labels: Vec<&str> = var_items.iter().map(|c| c.label.as_str()).collect();
        assert!(var_labels.contains(&"totalPrice"));
        assert!(var_labels.contains(&"userRole"));
    }

    #[test]
    fn test_ast_function_and_parameter_extraction() {
        let code = r#"
            function processOrder(orderId, customerEmail) {
                isValid = true;
                return isValid;
            }
        "#;
        let tree = parse_pnuts(code, None).unwrap();

        // Cursor inside function body (line 2)
        let completions_inside = provide_completions(Some(&tree), code, Position { line: 2, character: 10 });
        let labels_inside: Vec<&str> = completions_inside.iter().map(|c| c.label.as_str()).collect();

        assert!(labels_inside.contains(&"processOrder"));
        assert!(labels_inside.contains(&"orderId"));
        assert!(labels_inside.contains(&"customerEmail"));
    }

    #[test]
    fn test_domain_builtins_and_database_completions() {
        let code = "wsel = \"select * from ddolt0\";\n";
        let tree = parse_pnuts(code, None).unwrap();
        let completions = provide_completions(Some(&tree), code, Position { line: 1, character: 0 });

        let labels: Vec<&str> = completions.iter().map(|c| c.label.as_str()).collect();

        // Database methods
        assert!(labels.contains(&"getRows"));
        assert!(labels.contains(&"getMRows"));
        assert!(labels.contains(&"getRow"));
        assert!(labels.contains(&"getMRow"));
        assert!(labels.contains(&"getSQLNumber"));
        assert!(labels.contains(&"getSQVNumber"));
        assert!(labels.contains(&"getSQLValue"));
        assert!(labels.contains(&"getSQLValues"));
        assert!(labels.contains(&"executeSQL"));
        assert!(labels.contains(&"executeProcedure"));
        assert!(labels.contains(&"getMCursor"));
        assert!(labels.contains(&"beginWork"));
        assert!(labels.contains(&"commitWork"));
        assert!(labels.contains(&"rollbackWork"));

        // UI & Dialog methods
        assert!(labels.contains(&"showMessage"));
        assert!(labels.contains(&"showError"));
        assert!(labels.contains(&"throwError"));
        assert!(labels.contains(&"getDialogForm"));
        assert!(labels.contains(&"getUser"));

        // Formatting & Collections
        assert!(labels.contains(&"formatNumber"));
        assert!(labels.contains(&"inList"));
        assert!(labels.contains(&"round"));
        assert!(labels.contains(&"trim"));
        assert!(labels.contains(&"padRight"));
        assert!(labels.contains(&"elements"));

        // Domain classes
        assert!(labels.contains(&"BigDecimal"));
        assert!(labels.contains(&"MConnection"));
        assert!(labels.contains(&"JSONObject"));

        // Verify snippet parameter pre-filling
        let get_rows = completions.iter().find(|c| c.label == "getRows").unwrap();
        assert_eq!(get_rows.insert_text_format, Some(InsertTextFormat::SNIPPET));
        assert!(get_rows.insert_text.as_ref().unwrap().contains("${1:TABLE}"));

        let get_row = completions.iter().find(|c| c.label == "getRow").unwrap();
        assert_eq!(get_row.insert_text_format, Some(InsertTextFormat::SNIPPET));
        assert!(get_row.insert_text.as_ref().unwrap().contains("${1:TABLE}"));

        let get_mrows = completions.iter().find(|c| c.label == "getMRows").unwrap();
        assert_eq!(get_mrows.insert_text_format, Some(InsertTextFormat::SNIPPET));
        assert!(get_mrows.insert_text.as_ref().unwrap().contains("${1:TABLE}"));

        let get_mrow = completions.iter().find(|c| c.label == "getMRow").unwrap();
        assert_eq!(get_mrow.insert_text_format, Some(InsertTextFormat::SNIPPET));
        assert!(get_mrow.insert_text.as_ref().unwrap().contains("${1:TABLE}"));
    }

    #[test]
    fn test_common_object_methods_completions() {
        let code = "a = 1;\n";
        let tree = parse_pnuts(code, None).unwrap();
        let completions = provide_completions(Some(&tree), code, Position { line: 1, character: 0 });

        let method_items: Vec<&CompletionItem> = completions
            .iter()
            .filter(|c| c.kind == Some(CompletionItemKind::METHOD))
            .collect();

        let method_labels: Vec<&str> = method_items.iter().map(|c| c.label.as_str()).collect();

        assert!(method_labels.contains(&"size"));
        assert!(method_labels.contains(&"length"));
        assert!(method_labels.contains(&"trim"));
        assert!(method_labels.contains(&"substring"));
        assert!(method_labels.contains(&"startsWith"));
        assert!(method_labels.contains(&"endsWith"));
        assert!(method_labels.contains(&"isEmpty"));
        assert!(method_labels.contains(&"containsKey"));
        assert!(method_labels.contains(&"getString"));
        assert!(method_labels.contains(&"getInt"));
        assert!(method_labels.contains(&"getDouble"));
        assert!(method_labels.contains(&"getDate"));

        let get_string = completions.iter().find(|c| c.label == "getString").unwrap();
        assert_eq!(get_string.insert_text_format, Some(InsertTextFormat::SNIPPET));
        assert!(get_string.insert_text.as_ref().unwrap().contains("${1:columnName}"));
    }

    #[test]
    fn test_inspect_completions() {
        let code = "a = 1;\n";
        let tree = parse_pnuts(code, None).unwrap();
        let completions = provide_completions(Some(&tree), code, Position { line: 1, character: 0 });

        let get_items: Vec<&CompletionItem> = completions
            .iter()
            .filter(|c| c.label.to_lowercase().contains("get"))
            .collect();

        for item in &get_items {
            println!(
                "Label: {:20} | Kind: {:?} | Insert: {:35} | Sort: {:?}",
                item.label,
                item.kind,
                item.insert_text.as_deref().unwrap_or(""),
                item.sort_text
            );
        }
    }
}
