# Diretrizes Obrigatórias para Programação em Pnuts

Ao criar, editar, analisar ou refatorar scripts na linguagem **Pnuts** (`.pnuts`, `.pnut`, `.pnc`) ou scripts relacionados em **Groovy** neste servidor, você **DEVE** seguir rigorosamente as regras abaixo:

## 1. Escopo Local de Imports dentro de Funções
- No runtime Pnuts, blocos `function nome(...) { ... }` rodam em um contexto (`pnuts.lang.Context`) próprio e isolado.
- Declarações `import(...)` no escopo global (topo do arquivo) **NÃO são propagadas** para o interior de funções.
- **Sempre declare os `import(...)` necessários no início do corpo de cada função** (ex: `import("java.util.ArrayList")`, `import("java.math.BigDecimal")`). Não fazer isso causa erro em tempo de execução (`MError: <Classe> is not defined-1`).

## 2. Proibição Estrita de Escapes Unicode (`\uXXXX`)
- O analisador léxico JavaCC do Pnuts (`Pnuts.jjt`) **não aceita** sequências de escape Unicode como `"\uFEFF"` dentro de strings com aspas duplas, gerando erro sintático fatal (`MError: Encountered "\""`).
- Para testar ou criar caracteres especiais, utilize `char(N)` ou compare pelo código inteiro:
  ```pnuts
  // Correto:
  if (str.length() > 0 && int(str.charAt(0)) == 65279) { ... }
  bom = char(65279)
  aspas = char(34)
  ```

## 3. Cláusulas `switch / case`: Dois Pontos Colados
- A sintaxe do compilador Pnuts exige que os dois pontos (`:`) fiquem na **mesma linha** da expressão do case.
- **Nunca insira quebra de linha** antes do `:` em `case` ou `default`:
  ```pnuts
  // Correto:
  case "VALOR":
    processar()
    break
  ```

## 4. Declaração de Coleções e Mapas
- Mapas vazios devem ser declarados com `[:]` (ex: `wmap = [:]`). O uso de `[]` define uma lista/array vazio, e não uma tabela associativa.

## 5. Tratamento de Exceções e Padrão Transacional (`MConnection`)
- Blocos Java-style `try { ... } catch (...)` e `throw e` **NÃO são suportados** pelo analisador do Pnuts e causam erro fatal de compilação (`Encountered "{"` ou `Encountered "e"`).
- Sempre utilize a função nativa `catch(ClasseExcecao, function(e) { ... })` declarada antes do bloco a proteger, e `throwError(...)` para lançar/relançar exceções:
  ```pnuts
  catch (Exception, function(e) {
    conn.rollbackWork()
    showError("Erro na transação: " + e.getMessage())
    return false
  })

  conn.beginWork()
  // operações DDL/DML...
  conn.commitWork()
  catch (Exception, null) // desregistra o handler
  ```

## 6. Padrão de Formatação e Estilo
- Indentação estrita de **2 espaços**.
- Espaçamento claro ao redor de barras de divisão aritmética (ex: `(fim - inicio) / 1000`) para prevenir ambiguidades sintáticas.
