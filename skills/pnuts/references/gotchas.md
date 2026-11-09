# Pnuts: Armadilhas Críticas, Gotchas e Regressões Proibidas

Este documento cataloga todos os comportamentos inesperados, peculiaridades da arquitetura interna do compilador Pnuts (`Pnuts.jjt` / JavaCC) e lições aprendidas em produção.

---

## 🛑 1. Escopo de Imports e Isolamento de `Context` em Funções

### O Problema:
No runtime Pnuts, blocos `function nome(...) { ... }` são executados em instâncias independentes de contexto (`pnuts.lang.Context`). 
Declarações `import(...)` feitas no escopo global (no topo do arquivo) **NÃO são propagadas** para o contexto interno das funções.

### Sintoma em Execução:
```text
MError getAuxMessage(): ArrayList is not defined-1
MError getAuxMessage(): HashMap is not defined-1
```

### Regra Obrigatória:
Sempre declare os `import(...)` necessários **localmente, no início de cada função**:

```pnuts
// ❌ INCORRETO:
import("java.util.ArrayList")

function processar() {
  lista = ArrayList() // FALHA: ArrayList is not defined
}

// ✅ CORRETO:
function processar() {
  import("java.util.ArrayList")
  lista = ArrayList() // SUCESSO
}
```

---

## 🛑 2. Ausência de Suporte a Escapes Unicode (`\uXXXX`) em Strings

### O Problema:
O analisador léxico JavaCC do Pnuts (`STRING_CONTENT` em `Pnuts.jjt`) implementa apenas escapes básicos de caracteres (`\\`, `\"`, `\n`, `\t`, `\b`, `\r`, `\f`). O uso de notações como `"\uFEFF"` ou qualquer sequência `\uXXXX` dentro de aspas duplas dispara erro sintático imediato em tempo de compilação/execução.

### Sintoma em Execução:
```text
MError getAuxMessage(): Encountered "\"" at column ...
```

### Solução Consolidada:
1. Para validar se uma string inicia com um caractere Unicode especial (ex: UTF-8 BOM):
   ```pnuts
   // ✅ CORRETO: Validação pelo código numérico inteiro
   if (str.length() > 0 && int(str.charAt(0)) == 65279) {
     str = str.substring(1)
   }
   ```
2. Para construir o caractere dinamicamente:
   ```pnuts
   // ✅ CORRETO: Construção via função char()
   bom = char(65279)
   ```

---

## 🛑 3. Formatação Rígida de Cláusulas `switch / case`

### O Problema:
A gramática JavaCC do compilador Pnuts define case como:
```text
"case" Expression(env) ":"
```
Não é permitido token `<EOL>` (quebra de linha) entre a expressão avaliada e os dois pontos (`:`).

### Sintoma em Execução:
Se o formatador ou desenvolvedor quebrar a linha antes do `:`, o runtime falha com:
```text
MError getAuxMessage(): Encountered "\n" at column ...
```

### Regra Obrigatória:
Os dois pontos do `case` e do `default` devem permanecer estritamente colados ao valor na mesma linha:

```pnuts
// ❌ INCORRETO:
switch (ano) {
  case "2026"
  :
    processar()
}

// ✅ CORRETO:
switch (ano) {
  case "2026":
    processar()
    break
  default:
    padrao()
    break
}
```

---

## 🛑 4. Colisão de Barra de Divisão `/` com *Slashy Strings* (Groovy/Pnuts)

### O Problema:
Tanto em Groovy quanto em parsers TextMate, `/padrao/` define expressões regulares multilinhas (*slashy strings*). Se uma divisão aritmética como `elapsedTime = (endTime - startTime) / 1000` for interpretada erroneamente como abertura de regex, todo o código subsequente até a próxima barra perde realce e pode induzir erros de análise sintática.

### Regra de Escrita:
Mantenha sempre espaços claros ao redor de operadores de divisão:
```pnuts
// ✅ Recomendado:
total = (fim - inicio) / 1000
taxa = valor / total
```

---

## 🛑 5. Declaração de Mapas Vazios (`[:]`)

### O Problema:
Escrever `wmap = []` cria uma **lista/array vazio**, e não um mapa (tabela hash). Tentar indexar com chaves nomeadas em um array gerará erro de tipo de índice.

### Regra Obrigatória:
- Para arrays vazios: `arr = []`
- Para mapas vazios: `wmap = [:]`

```pnuts
// ✅ Mapa associativo chave-valor:
registro = [:]
registro.codigo = 101
registro.nome = "Produção"
```

---

## 🛑 6. Caracteres Especiais e Aspas (`char(34)`)

Para concatenar ou comparar aspas duplas literais dentro de expressões onde o escape com `\"` cause ambiguidade em comandos SQL ou strings dinâmicas:

```pnuts
// Representação limpa de aspas duplas
aspas = char(34)
sql = "WHERE nome = " + aspas + nomeParam + aspas
```

---

## 🛑 7. Operador Ternário (`cond ? valTrue : valFalse`) Não Suportado

### O Problema:
O operador ternário (`? :`) apresenta comportamento imprevisível ou falhas de análise/execução no runtime Pnuts legado em produção.

### Sintoma em Execução:
Erros sintáticos, nós inesperados no compilador ou falha na avaliação de expressões condicionais inline.

### Regra Obrigatória:
**NUNCA utilize o operador ternário em scripts Pnuts.** Utilize sempre estruturas condicionais `if / else` explícitas:

```pnuts
// ❌ INCORRETO:
valor = (ativo) ? 100 : 0
nome = (param != null) ? param : "PADRAO"

// ✅ CORRETO:
valor = 0
if (ativo) {
  valor = 100
}

nome = "PADRAO"
if (param != null) {
  nome = param
}
```

---

## 🛑 8. Proibição da Sintaxe Java `try { ... } catch (...)` (Erro `Encountered "{" at column 7-1`)

### O Problema:
O parser do Pnuts não possui a palavra-chave/estrutura de bloco `try { ... } catch (...)` ou `try { ... } finally { ... }` do Java. Tentar utilizá-la causa erro fatal de compilação/análise léxica imediato no runtime.

### Sintoma em Execução:
```text
pnuts.lang.ParseException: Encountered "{" at line X, column Y.
```

### Regra Obrigatória:
**NUNCA utilize `try { ... } catch (...)`, `finally` ou relançamento com `throw e`.**
1. No Pnuts, o tratamento de exceções é feito através da função nativa **`catch(ClasseExcecao, function(e) { ... })`**, declarada antes do bloco a ser protegido.
2. Para relançar ou propagar erros para o chamador ou interface do ERP, **NUNCA use `throw e`** (isso causa `MError: Encountered "e" at column 9`). Utilize sempre a função nativa do Minimal **`throwError(...)`**:
3. Para desregistrar o manipulador de exceção após o bloco concluído, passe `null`: `catch(Exception, null)`.

```pnuts
// ❌ INCORRETO:
try {
  conn.beginWork()
  conn.execute(sql)
  conn.commitWork()
} catch (Exception e) {
  conn.rollbackWork()
  throw e // FALHA: Encountered "e" at column 9
}

// ✅ CORRETO:
catch (Exception, function(e) {
  conn.rollbackWork()
  wmsg = ""
  if (e.getMessage() != null && e.getMessage().trim() != "") {
    wmsg = e.getMessage().trim()
  } else {
    wmsg = e.toString()
  }
  throwError("Erro na transação: " + wmsg)
})

conn.beginWork()
conn.execute(sql)
conn.commitWork()

catch (Exception, null) // Desregistra handler
```

---

## 🛑 9. Sintaxe de Laços `for`: Utilizar `:` ou `foreach`, NUNCA `for (x in y)`

### O Problema:
A gramática JavaCC do compilador Pnuts (`Pnuts.jjt`) define a iteração de coleções exclusivamente com o operador dois pontos (`:`) ou a palavra-chave `foreach`. A palavra-chave `in` **NÃO é um token válido** para laços em Pnuts (ao contrário de Groovy, JavaScript ou Python). Tentar utilizar `for (item in lista)` causa um `pnuts.lang.ParseException` imediato.

### Sintoma em Execução:
```text
pnuts.lang.ParseException: Encountered "in" at line X, column Y.
```

### Regra Obrigatória:
- Para iterar coleções/arrays/iterators: utilize `for (item : colecao)` ou `foreach item (colecao)`.
- Para intervalos numéricos: utilize `for (i : inicio..fim)`.
- Para laços tradicionais: utilize `for (i = 0; i < tamanho; i++)`.

```pnuts
// ❌ INCORRETO:
for (item in lista) {
  processar(item)
}

// ✅ CORRETO (Pnuts com dois pontos):
for (item : lista) {
  processar(item)
}

// ✅ CORRETO (foreach nativo):
foreach item (lista) {
  processar(item)
}
```


