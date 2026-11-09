---
name: pnuts
description: >-
  Comprehensive knowledge base, language reference, coding standards, corporate APIs,
  runtime gotchas, and tooling guidelines for developing in Pnuts (.pnuts, .pnut, .pnc)
  and related Groovy scripts on this server. Use this skill whenever writing, analyzing,
  refactoring, debugging, or reviewing Pnuts or Groovy scripts.
---

# Pnuts Development & Language Guide

Pnuts is a dynamic, expressive scripting language running on the JVM (compatible up to Java 8), combining JavaScript-like syntax with direct, transparent Java interoperability and JavaBeans property access.

This skill is the single source of truth for writing reliable, production-grade Pnuts scripts, avoiding known runtime engine crashes, and leveraging the tooling ecosystem available on this server.

---

## ⚡ The Golden Rules of Pnuts (Must-Read)

1. **Local Imports Inside Functions:**
   - Functions (`function name(...) { ... }`) execute in an isolated `pnuts.lang.Context`.
   - Global `import(...)` declarations at the top of a file **are NOT inherited** inside functions.
   - **Always** place required `import(...)` calls inside each function's body (e.g., `import("java.util.ArrayList")`).
2. **No Unicode Escapes (`\uXXXX`) in Double-Quoted Strings:**
   - The JavaCC parser (`Pnuts.jjt`) crashes with lexical errors on `"\uXXXX"`.
   - Use `char(65279)` or integer comparison `int(str.charAt(0)) == 65279`.
3. **Switch / Case Colon Attachment:**
   - Do NOT place a line break between the case value and the colon: `case "VAL":` must have the colon on the same line.
4. **Empty Maps vs Array Subscripts:**
   - Empty maps are declared as `wmap = [:]`. Array indexing is `arr[i]`.
5. **Database Transactions:**
   - Always wrap transactional rollback in `catch (Exception, function(e) { conn.rollbackWork(); showError(...); })`.
6. **No Ternary Operator (`cond ? a : b`):**
   - The ternary operator is unsupported/unstable in this Pnuts runtime and triggers evaluation failures.
   - **Always** use explicit `if / else` blocks.
7. **No Java-style `try / catch / finally` Syntax:**
   - Standard Java syntax `try { ... } catch (...) { ... }` or `try { ... } finally { ... }` is NOT supported and causes syntax errors: `Encountered "{" at column 7-1`.
   - **Always** use the Pnuts built-in catch function: `catch (Exception, function(e) { ... })`.

---

## 📚 Detailed Reference Documentation

For complete manuals and specifications, consult the following sub-documents in this skill:

- [Language Syntax & Fundamentals](./references/syntax.md): Variables, collections, control flows, functions, closures, and Java interop.
- [Critical Gotchas & Prohibited Regressions](./references/gotchas.md): Exact causes of runtime crashes, lexer limitations, and how to avoid them.
- [Corporate APIs & Business Objects](./references/corporate-apis.md): Specifications for `MConnection`, `MCursor`, `MRow`, `MLogin`, `MForm`, `MDate`, `MFile`, and UI helper functions.
- [Tooling & Environment](./references/tooling.md): Using `pnuts-lsp`, `pnuts-format`, VS Code extension, and CLI tools on this server.

---

## 🛠️ Standard Script Templates

### 1. Standard Function with Local Imports
```pnuts
function processarDados(listaEntrada) {
  import("java.util.ArrayList")
  import("java.util.HashMap")
  import("java.math.BigDecimal")

  resultado = ArrayList()
  mapa = [:]

  for (item : listaEntrada) {
    if (item != null) {
      resultado.add(item)
    }
  }

  return resultado
}
```

### 2. Transactional Database Script (`MConnection` / `MCursor`)
```pnuts
import("java.util.ArrayList")

function salvarRegistro(conn, idUsuario, nomeUsuario, valor) {
  import("java.math.BigDecimal")

  if (conn == null) {
    showError("Conexão com o banco inválida.")
    return false
  }

  catch (Exception, function(e) {
    conn.rollbackWork()
    showError("Falha ao salvar registro: " + e.getMessage())
    return false
  })

  conn.beginWork()

  sql = "UPDATE USUARIOS SET NOME = '" + nomeUsuario + "', VALOR = " + valor + " WHERE ID = " + idUsuario
  conn.execute(sql)

  conn.commitWork()
  return true
}
```

### 3. Iterating Records with `MCursor`
```pnuts
function buscarUsuariosAtivos(conn) {
  import("java.util.ArrayList")

  usuarios = ArrayList()
  cursor = conn.getMCursor("SELECT ID, NOME, EMAIL FROM USUARIOS WHERE ATIVO = 'S'")

  catch(Exception, function(e) {
    if (cursor != null) {
      cursor.close()
    }
    showError("Erro ao buscar usuários: " + e.getMessage())
    return usuarios
  })

  cursor.open()
  while (cursor.next()) {
    row = cursor.getCurrentRow()
    usuario = [:]
    usuario.id = row.get("ID")
    usuario.nome = row.get("NOME")
    usuario.email = row.get("EMAIL")
    usuarios.add(usuario)
  }
  cursor.close()
  catch(Exception, null)

  return usuarios
}
```

---

## 🚀 Tooling Available on this Server

- **LSP Server:** `/home/chsilva/.local/bin/pnuts-lsp` (integrated in editors via stdio).
- **Code Formatter:** `/home/chsilva/.local/bin/pnuts-format` (Topiary AST-based 2-space formatter).
- **VS Code Extension:** Packaged under `vscode-pnuts/` in the tooling repository.
