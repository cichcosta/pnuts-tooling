---
name: minimal-developer
description: Guide for writing and formatting scripts and SQL queries for the Minimal system. Use this skill when writing, refactoring, or updating code that involves the Minimal system, Pnuts scripting language, or SQL queries for Minimal's database structure.
---

# Minimal System Developer Guide

This skill provides guidelines and reference materials for developing scripts and SQL queries for the Minimal system. The system heavily utilizes the Pnuts scripting language integrated with Java, and specific SQL query formatting rules.

## Core Rules & Conventions

### SQL Queries (Minimal Pattern)

When writing SQL queries for the Minimal system, you **must** adhere to the following rules:

1. **`WITH (NOLOCK)` in `SELECT`**
   - Always add `WITH (NOLOCK)` immediately after each table involved in a `SELECT` statement, including in `JOIN`s.
   - *Example:*
     ```sql
     SELECT a.*
       FROM bctxt0 a WITH (NOLOCK)
       JOIN outra_tabela b WITH (NOLOCK) ON b.id = a.id
     ```

2. **`WITH (ROWLOCK)` in `UPDATE` and `DELETE`**
   - Always place `WITH (ROWLOCK)` immediately after the target table name and before clauses like `TOP`, `SET`, or `WHERE`.
   - *Example:*
     ```sql
     UPDATE TOP (1) bctxt0 WITH (ROWLOCK)
        SET zk = @wzk
      WHERE ictx = @ictx
        AND ivalk = @ivalk
        AND zk = 0;
     ```

3. **Function Comments in SQL Strings**
   - If the context file contains the pattern `Função: <nome>`, you must extract `<nome>` and append it as a comment at the end of each SQL instruction built in the script.
   - *Format:* `wsel += " /* Função: <nome> */ "` (Keep exactly one space before `/*`).

4. **Org Field Completion**
   - Always complete the `iorg` field with `'TEC'` in SQL queries when applicable.

### Pnuts Scripting

Minimal uses Pnuts, a dynamic scripting language for the Java platform.

1. **Syntax & Features:**
   - Expression-based, no type declarations or semicolons required.
   - Variables are created automatically upon assignment.
   - Control structures available: `if-else`, `foreach`, `while`, `for`, `switch-case`, `break`, `continue`, `return`.
   - Functions are defined using the `function` keyword.
   - Functions must be defined before they are used.
   - Exception handling uses `catch` (without `try`) placed at the start of a function block.

2. **Java Integration:**
   - Seamlessly invokes Java functions and methods.
   - Import Java classes using `import("pacote.classe.*")`.
   - When a function needs Java classes, place the required `import("...")` inside the function itself.

#### Pnuts — Construtores, Tipagem e Chamadas Estáticas

- Construtores sem `new`
  - Pnuts não interpreta `new`. Chame a classe como função.
  - Exemplos: `Date()`, `Integer(42)`. (Evite `new Date()`.)

- Variáveis sem tipo (tipagem dinâmica)
  - Não declare tipo. Apenas atribua.
  - Correto: `lista = []`, `nome = "John"`.
  - Evite: `List lista = []`, `String nome = "John"`.

- Estáticos com `::` (não use `.`)
  - Métodos e campos estáticos: `Classe::metodo` / `Classe::CAMPO`.
  - Exemplos: `Arrays::asList([1,2,3])`, `Math::max(10, 20)`, `Color::WHITE`.
  - Use `.` só para métodos de instância: `tam = lista.size()`.

- Dicas de import e nomes qualificados
  - Com `import("java.util.*")`, use `Arrays::asList(...)`.
  - Se a classe for usada dentro de uma `function`, faça o `import("...")` dentro da própria `function`.
  - Sem import, use nome qualificado: `"java.util.Arrays"::asList([...])`.

Exemplo:
```pnuts
function minhaFuncao(){
    import("java.util.*")
    import("java.lang.Math")
    import("java.awt.Color")

    d = Date()
    lista = Arrays::asList([1, 2, 3])
    tam = lista.size()
    maximo = Math::max(10, 20)
    branco = Color::WHITE
    l2 = "java.util.Arrays"::asList([4, 5, 6])
}

minhaFuncao()
```
3. **Common Practices in Minimal:**
   - Do **not** randomly autocomplete links, keys, numbers, or logic values to avoid breaking the logic.
   - Always place `flush()` immediately after any `println(...)` command.
   - When retrieving a single value from the database, prefer `getSQLValue(wsel)` for text/strings or `getSQLNumber(wsel)` for numbers, rather than using `getMRows` or `getRows`.
   - Example string concatenation for SQL in Pnuts:
     ```cpp
     // Função: bctx_mc_corrige_zk_fn()
     cursor  = MConnection::getMCursor()

     wsel  = "SELECT a.* "
     wsel += "  FROM bctxt0 a WITH (NOLOCK) "
     wsel += " WHERE a.ictx = 'CIELO*PARAM' "
     wsel += "   AND a.ivalk <> ' ' "
     wsel += "   AND a.zk = 0 "
     wsel += " /* Função: bctx_mc_corrige_zk_fn */ "
     vbctx = getMRows("BCTX", wsel)
     ```

## Tela/Form Metadata (`mrfct0`)

A tabela `mrfct0` parametriza os campos que compõem as telas do Minimal.

| Coluna | Significado |
|--------|-------------|
| `i_ob` | Objeto que nomeia a tela. A raiz é sempre os 4 primeiros dígitos da tabela (ex: `DDOC` para `ddoct0`). |
| `i_comp` | Nome do campo/atributo da tabela que aparece na tela. |
| `t_lin` | Nome que aparece na linha do campo nos forms. |
| `t_col` | Nome que aparece na coluna da tela de resultados. |
| `t_txt` | Texto auxiliar (tooltip) que aparece ao passar o mouse sobre o campo do form. |

**Observações:**
- Campos `i_comp` que começam com `"w"` geralmente são campos genéricos usados com tabelas de extensão e não existem na tabela base da tela.
- A coluna `c_te` pode indicar exclusão (`c_te <> '*'`).
- Ordenação típica: `i_ob`, `q_ot`, `q_oe`.

**Exemplo de query:**
```sql
SELECT a.i_ob, i_comp, t_lin, t_col as coluna, t_txt
FROM mrfct0 a WITH (NOLOCK)
WHERE a.i_ob = 'DDOC'
  AND a.i_comp = 'ientt'
  AND c_te <> '*'
ORDER BY a.i_ob, a.q_ot, a.q_oe
```

## System Integration & Workflows

### 1. Accessing Context and System Architecture
You have powerful tools and resources available to understand and interact with the Minimal system. Use them proactively to gather context before writing or modifying scripts.

#### Architecture Documentation
The complete documentation of the Minimal structure (generated via reverse engineering) is bundled with this skill.
When you need to understand how the system works, refer to these key files:
- [architecture.md](references/_reversa_sdd/architecture.md): Overall system architecture.
- [domain.md](references/_reversa_sdd/domain.md): Domain models, tables, and data dictionary.
- [state-machines.md](references/_reversa_sdd/state-machines.md): State machines and workflow logic.
- [permissions.md](references/_reversa_sdd/permissions.md): Permissions and access control.
- [code-analysis.md](references/_reversa_sdd/code-analysis.md): Code analysis and specific Java/Pnuts patterns.

#### Live Database Queries (MCP SQL)
You have access to the Minimal database via the `mssql_mcp_server` MCP.
- **When to use:** To verify a table schema, check column existence, or inspect sample data.
- **How to use:** Call the `mcp6_execute_sql` tool to run `SELECT` queries. Always keep queries fast and read-only. Example: `SELECT TOP 10 * FROM bctxt0 WITH (NOLOCK)`.

#### Fetching Scripts from the Database
Minimal functions are stored in the database. You can fetch them directly.
- **When to use:** When you need to read, understand, or modify an existing Minimal function.
- **How to use:** Call the `mcp6_get_function` tool. Provide the `function_name` and an `output` path (e.g., `funcao_xyz.txt`). Then, read the saved file.

### 2. Minimal to External Scripts (VBScript / Batch)
- **Silent Execution:** When configuring Minimal (e.g., in `.pro` files) to trigger external scripts (like `.vbs`), use `wscript` instead of `cmd.exe /c` or `start`. This ensures the script runs entirely in the background without flashing a black console window to the user.
- **Cross-Session Data Transfer:** To pass data safely from Minimal to an external script in a Terminal Server (RDS) environment, use the `%TEMP%` folder (`java.io.tmpdir`). Write a temporary `.txt` file using Pnuts, and have the VBScript read it. This guarantees perfect session isolation.

### 3. Active Directory (LDAP) Integration
When integrating with Microsoft Graph API or requiring the official `userPrincipalName` (UPN):
- The `temail` stored in `XUACT0` is often an alias and may cause `404 ResourceNotFound` errors.
- **Solution:** Fetch the employee ID (`cmat` or `ient`) from the database, prefix it with "EM" (e.g., "EM12345"), and use JNDI (`javax.naming.directory.InitialDirContext`) to query the Active Directory (`ldap://ibyte.corp`) by filtering `facsimileTelephoneNumber`. Extract the `userPrincipalName` or `mail` directly from the LDAP response.

## References

For deeper understanding of Pnuts syntax and advanced Minimal system capabilities, refer to the bundled documentation:

- **Pnuts Syntax & Patterns:** See [pnuts-syntax.md](references/pnuts-syntax.md) for data manipulation, arrays, and built-in functions.
- **Official Pnuts Tutorials:** Refer to the local files (not loaded into context automatically):
  - `Minimal/documentação pnuts_.pdf`
  - `Minimal/Tutorial Pnuts (1).md`
