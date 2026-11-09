# Pnuts: Sintaxe Fundamental & Referência da Linguagem

Este documento cobre a sintaxe completa e as convenções da linguagem Pnuts sobre a JVM.

---

## 1. Variáveis e Tipagem Dinâmica

Em Pnuts não existem palavras-chave como `var`, `let` ou declaração explícita de tipos para variáveis locais. A variável é alocada na primeira atribuição no escopo ativo:

```pnuts
empresa = "Minha Empresa"
ano = 2026
ativo = true
saldo = 1500.50
```

---

## 2. Tipos Primitivos e Numéricos

Pnuts mapeia tipos nativos diretamente para primitivos e classes wrappers da JVM:

- **Inteiros**: `int` / `long` (ou `BigInteger` quando excede os limites).
- **Decimais / Ponto Flutuante**: `double` por padrão.
- **Valores Monetários e Precisão Arbitrária**: Recomenda-se utilizar `java.math.BigDecimal` para cálculos financeiros para evitar imprecisões de ponto flutuante:
  ```pnuts
  import("java.math.BigDecimal")
  valorTotal = BigDecimal("1500.75")
  ```

---

## 3. Strings & Caracteres

- **Aspas Duplas e Simples**: Strings podem ser delimitadas por `"` ou `'`:
  ```pnuts
  nome = "João da Silva"
  codigo = 'USR-001'
  mensagem = "Olá, " + nome + "!"
  ```
- **Caracteres Especiais (`char(N)`):**
  Pnuts fornece a função built-in `char(...)` para representar caracteres por seu código numérico:
  ```pnuts
  aspas = char(34)       // Aspas duplas "
  bomUtf8 = char(65279)  // Byte Order Mark (BOM)
  ```
- **Métodos Nativos de String (Java Interop):**
  Todas as instâncias de string possuem acesso direto aos métodos de `java.lang.String`:
  ```pnuts
  tamanho = nome.length()
  maiusculo = nome.toUpperCase()
  partes = nome.split(" ")
  ehIgual = nome.equals("João da Silva")
  ```

---

## 4. Coleções: Arrays, Listas e Mapas

### 4.1. Arrays
Pnuts suporta literais de arrays com colchetes:
```pnuts
numeros = [10, 20, 30, 40]
primeiro = numeros[0]
numeros[1] = 25
```

### 4.2. Mapas (Dicionários / Key-Value)
- **Mapa Vazio**: Deve ser declarado explicitamente como `[:]` para evitar ambiguidade com indexação:
  ```pnuts
  wmap = [:]
  ```
- **Mapa com Valores Iniciais**:
  ```pnuts
  config = ["host": "192.168.1.10", "porta": 8080, "ssl": true]
  ```
- **Acesso e Atribuição**: Suporta notação por chave ou notação de propriedade:
  ```pnuts
  host = config["host"]
  porta = config.porta
  config.timeout = 5000
  ```

### 4.3. Listas Java (`ArrayList`)
```pnuts
import("java.util.ArrayList")

lista = ArrayList()
lista.add("Item 1")
lista.add("Item 2")
qtd = lista.size()
```

---

## 5. Estruturas de Controle de Fluxo

### 5.1. Condicionais (`if / else if / else`)
```pnuts
if (status == "A") {
  descricao = "Ativo"
} else if (status == "I") {
  descricao = "Inativo"
} else {
  descricao = "Desconhecido"
}
```

### 5.2. `switch / case / default`
> [!IMPORTANT]
> **Regra Obrigatória do Parser JavaCC:** Os dois pontos (`:`) do `case` e `default` devem estar colados na mesma linha da expressão. NUNCA insira quebra de linha antes dos dois pontos.

```pnuts
switch (tipoOperacao) {
  case 1:
    executarInclusao()
    break
  case 2:
    executarAlteracao()
    break
  case 3:
    executarExclusao()
    break
  default:
    showError("Operação inválida.")
    break
}
```

### 5.3. Laços de Repetição
Pnuts suporta múltiplas variantes de iteração:

- **`for (item : colecao)`**:
  ```pnuts
  for (item : lista) {
    println(item)
  }
  ```
- **`foreach item (colecao)`**:
  ```pnuts
  foreach usuario (usuarios) {
    println(usuario.nome)
  }
  ```
- **`for` Tradicional com Contador**:
  ```pnuts
  for (i = 0; i < 10; i++) {
    println("Iteração: " + i)
  }
  ```
- **`while`**:
  ```pnuts
  while (cursor.next()) {
    processarLinha(cursor.getCurrentRow())
  }
  ```

---

## 6. Funções, Closures e Geradores

### 6.1. Declaração de Funções
```pnuts
function calcularDesconto(valorBruto, percentual) {
  if (percentual <= 0) {
    return valorBruto
  }
  desconto = valorBruto * (percentual / 100.0)
  return valorBruto - desconto
}
```

### 6.2. Funções Anônimas / Closures
```pnuts
somador = function (a, b) { a + b }
resultado = somador(10, 20)
```

### 6.3. Geradores (`yield`)
Pnuts suporta geradores que retêm o estado de iteração via `yield`:
```pnuts
function sequencia(inicio, fim) {
  for (i = inicio; i <= fim; i++) {
    yield i
  }
}
```

---

## 7. Interoperabilidade Transparente com Java

### 7.1. Instanciação Direta
Classes no classpath da JVM podem ser instanciadas diretamente com ou sem import prévio:
```pnuts
// Com import
import("java.util.Date")
dataAtual = Date()

// Pelo nome totalmente qualificado (FQCN)
calendario = java.util.Calendar::getInstance()
```

### 7.2. Chamada de Métodos Estáticos
Usa-se a sintaxe `Classe::metodoEstatico(...)` ou `Classe.metodoEstatico(...)`:
```pnuts
maximo = Math::max(10, 25)
agoraMs = java.lang.System::currentTimeMillis()
```

### 7.3. Acesso a JavaBeans
Campos com getters/setters (`getDescricao()`, `setDescricao(val)`) são expostos como propriedades diretas:
```pnuts
// Equivalente a obj.setDescricao("Teste")
obj.descricao = "Teste"

// Equivalente a obj.getDescricao()
texto = obj.descricao
```

---

## 8. Tratamento de Exceções

O Pnuts utiliza a função nativa `catch(ClasseExcecao, function(e) { ... })` para capturar exceções da JVM (o bloco `try / catch` tradicional do Java não é suportado pelo parser do Pnuts):

```pnuts
catch (java.sql.SQLException, function(e) {
  showError("Erro SQL: " + e.getMessage())
  return false
})

catch (Exception, function(e) {
  showError("Erro genérico: " + e.getMessage())
  return false
})

executarOperacaoArriscada()
```
