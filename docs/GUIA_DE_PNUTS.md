# 🥜 Guia Completo da Linguagem Pnuts & Manual de Treinamento

Bem-vindo ao manual de treinamento e referência da linguagem **Pnuts**! Este documento foi estruturado para capacitar novos desenvolvedores a compreender a sintaxe, os recursos nativos, a interoperabilidade com a JVM, as convenções corporativas e o uso das ferramentas de desenvolvimento instaladas no nosso repositório.

---

## 📑 Sumário

1. [Visão Geral & Arquitetura](#1-visão-geral--arquitetura)
2. [Configuração do Ambiente e Ferramentas (Tooling)](#2-configuração-do-ambiente-e-ferramentas-tooling)
3. [Sintaxe Fundamental & Tipos de Dados](#3-sintaxe-fundamental--tipos-de-dados)
4. [Operadores & Expressões](#4-operadores--expressões)
5. [Coleções: Arrays, Listas e Mapas](#5-coleções-arrays-listas-e-mapas)
6. [Estruturas de Controle de Fluxo](#6-estruturas-de-controle-de-fluxo)
7. [Funções, Closures & Geradores](#7-funções-closures--geradores)
8. [Interoperabilidade com Java (Java Interop)](#8-interoperabilidade-com-java-java-interop)
9. [Tratamento de Exceções & Erros](#9-tratamento-de-exceções--erros)
10. [Pacotes, Módulos & Importações](#10-pacotes-módulos--importações)
11. [Padrões e Casos Reais na Empresa](#11-padrões-e-casos-reais-na-empresa)
12. [Cheatsheet: Pnuts vs Java vs JavaScript](#12-cheatsheet-pnuts-vs-java-vs-javascript)

---

## 1. Visão Geral & Arquitetura

**Pnuts** é uma linguagem de script dinâmica, expressiva e leve projetada especificamente para rodar sobre a **JVM (Java Virtual Machine)**. 

### Principais Características:
- **Tipagem Dinâmica:** Variáveis não exigem declaração de tipo prévia.
- **Sintaxe Híbrida:** Combina a legibilidade do JavaScript com o poder e a tipagem do ecossistema Java.
- **Java Interop Transparente:** Acesso direto a classes, métodos, campos e construtores Java da JVM sem necessidade de bridges complexas.
- **Suporte a JavaBeans:** Propriedades `getX()` / `setX()` são acessíveis diretamente por propriedade `.x`.
- **Compatibilidade com Java 8:** Roda diretamente sobre os runtimes corporativos legados da JVM.

---

## 2. Configuração do Ambiente e Ferramentas (Tooling)

O nosso repositório contém todo o ecossistema moderno para desenvolvimento com arquivos `.pnuts`, `.pnut` e `.pnc`.

### 2.1. Extensões de Arquivo
- `.pnuts` / `.pnut`: Scripts e regras de negócio Pnuts.
- `.pnc`: Classes scriptadas (*scripted classes*).

### 2.2. Visual Studio Code
A pasta `vscode-pnuts/` contém a extensão com suporte a:
- Syntax Highlighting completo.
- Formatação de código via atalho (`Shift + Alt + F` no Linux/Windows ou `Shift + Option + F` no macOS) ou ao salvar (*Format on Save*).

**Instalação rápida:**
```bash
ln -s "$(pwd)/vscode-pnuts" ~/.vscode/extensions/pnuts-language-support
```

### 2.3. Editor Zed
A pasta `zed-pnuts/` integra a gramática Tree-sitter e o formatador Topiary ao Zed:
1. Abra o Zed e use `Ctrl+Shift+P` -> `Extensions: Install Dev Extension`.
2. Aponte para a pasta `zed-pnuts/`.

### 2.4. Formatador CLI (`scripts/format_pnuts.py`)
Utilizamos o **Topiary** com queries Tree-sitter para padronizar indentação (2 espaços), quebras de linha e normalização de statements.

- **Modo Inspeção Seguro (Diff / Dry-run):**
  ```bash
  python3 scripts/format_pnuts.py --diff pnuts-samples/
  ```
- **Formatação em Lote (Escreve nos arquivos):**
  ```bash
  python3 scripts/format_pnuts.py --write caminho/para/scripts
  ```
- **Verificação em CI (Check):**
  ```bash
  python3 scripts/format_pnuts.py --check caminho/para/scripts
  ```

---

## 3. Sintaxe Fundamental & Tipos de Dados

### 3.1. Comentários
```pnuts
// Comentário de uma única linha

/* Comentário
   de múltiplas linhas */
```

### 3.2. Variáveis e Declaração
As variáveis são criadas na primeira atribuição no escopo atual. Não há palavras-chave como `var`, `let` ou tipos explícitos para variáveis locais:
```pnuts
empresa = "Minha Empresa"
ano = 2026
ativo = true
```

### 3.3. Literais Numéricos e Decimais (Atenção a Valores Financeiros!)
Pnuts possui excelente integração com tipos numéricos da JVM:
```pnuts
// Inteiros (Integer, Long ou BigInteger se exceder limite)
qtd = 10
codigoHex = 0xff          // Formato Java (Integer)
corHex = #ffff0000        // Formato Pnuts com '#'

// Ponto Flutuante (Double / Float)
taxa = 0.05
fator = 1.25f

// BigDecimal (Sufixo 'B' - INDISPENSÁVEL PARA VALORES FISCAIS/MONETÁRIOS)
valorMonetario = 1500.50B
aliquota = 0.18B
resultado = 1.2B * 3      // 3.6 (sem erros de imprecisão de double)
```

> [!IMPORTANT]
> Em cálculos de impostos (ICMS, IPI, PIS, COFINS, ST), sempre utilize literais `BigDecimal` com sufixo `B` (ex: `11B`, `0.05B`) para evitar erros de ponto flutuante.

### 3.4. Strings e Interpolação
Pnuts oferece três formas de lidar com strings:
```pnuts
// 1. Strings padrão com aspas duplas
nome = "Pnuts"

// 2. Interpolação de Strings: \(expressão)
dataAtual = "Hoje é: \(date())"
mensagem = "Total: \(preco * qtd)"

// 3. Verbatim String com crase ` (preserva aspas e barras sem precisar escapar)
// Excelente para queries SQL e expressões regulares!
query = `SELECT * FROM bentt0 WHERE ictx = "TESTE" AND campo LIKE 'A%'`
```

### 3.5. Caracteres, Booleanos e Null
```pnuts
letra = 'A'
escapeUnicode = '\u0041'
status = true
invalido = false
semValor = null
```

### 3.6. Avaliação de Valores Lógicos (Truthy / Falsy)
Nas condições (`if`, `while`, `? :`), os seguintes valores são convertidos para `false`:
- `false`
- `null`
- `0` (qualquer tipo numérico zero)
- `""` (string vazia)

Todos os outros valores (incluindo objetos não nulos, strings preenchidas e números diferentes de zero) avaliam para `true`.

---

## 4. Operadores & Expressões

### 4.1. Operadores Aritméticos
```pnuts
soma = a + b
subtracao = a - b
multiplicacao = a * b
divisao = a / b
resto = a % b

// Incremento e Decremento
a++
++a
b--
--b
```

### 4.2. Operadores de Comparação
Pnuts realiza comparações de alto nível automaticamente:
- Compara números por valor (`1 == 1.0` é `true`).
- Compara Strings por conteúdo via `String.compareTo()`.
- Compara objetos `Comparable` por ordem.
- Compara Arrays elemento a elemento recursivamente!

```pnuts
a == b    // Igualdade
a != b    // Diferença
a < b     // Menor que
a <= b    // Menor ou igual
a > b     // Maior que
a >= b    // Maior ou igual

// Exemplo com Arrays:
[1, 2, [3, 4]] == [1, 2, [3, 4]]  // true!
```

### 4.3. Operadores Lógicos e Bitwise
```pnuts
// Lógicos
condicao = (x > 0) && (y < 10)
valido = ativo || (nivel == 1)
naoValido = !ativo

// Bitwise (operam sobre inteiros e BigInteger)
mascara = a & b
combinado = a | b
deslocado = 1 << 8
```

### 4.4. Operador Ternário
```pnuts
status = (saldo > 0) ? "POSITIVO" : "NEGATIVO"
```

### 4.5. Atribuição Composta e Paralela (Desestruturação)
```pnuts
// Atribuições compostas
total += 10
fator *= 2

// Atribuição paralela de listas/arrays:
x, y = [100, 200]
// x passa a valer 100, y passa a valer 200

dia, mes, ano = ["27", "08", "2026"]
```

---

## 5. Coleções: Arrays, Listas e Mapas

### 5.1. Arrays e Listas
```pnuts
// Array heterogêneo (Object[])
arr = [1, "dois", 3.0, true]

// Array tipado nativo Java
numeros = (int[])[1, 2, 3]
matriz = int[3][3] // Cria int[3][3] inicializado com 0

// Tamanho do array
tamanho = arr.length

// Lista dinâmica (java.util.List)
minhaLista = {1, 2, 3}
```

### 5.2. Mapas (Dicionários / `java.util.HashMap`)
Mapas são declarados usando chaves `{ ... }` com pares `chave => valor`:
```pnuts
usuario = {
  "id" => 101,
  "nome" => "Carlos Silva",
  "cargo" => "Desenvolvedor"
}

// Acesso a elementos do mapa:
println(usuario["nome"])   // Via chave entre colchetes
println(usuario.nome)      // Via notação de ponto!

// Inserção / Atualização:
usuario["departamento"] = "TI"
usuario.ativo = true
```

### 5.3. Indexação Avançada & Slicing
Pnuts possui recursos avançados para fatiamento de coleções e strings:
```pnuts
dados = ["A", "B", "C", "D", "E"]

// 1. Índices Negativos (contam do fim para o início):
ultimo = dados[-1]        // "E"
penultimo = dados[-2]     // "D"

// 2. Fatiamento (Slicing):
parte = dados[1..3]       // ["B", "C", "D"]
doInicioAoFim = dados[2..] // ["C", "D", "E"]

// 3. Fatiamento em Strings:
texto = "0123456789"
sub = texto[2..5]         // "2345"
```

### 5.4. Seleção & Filtragem com Predicados `[ ... ]`
Você pode filtrar coleções passando uma expressão booleana ou função dentro dos colchetes:
```pnuts
produtos = list() // lista de objetos com .preco e .ativo

// Filtragem direta por propriedade do item:
produtosAtivos = produtos[ativo == true]
produtosCaros = produtos[preco > 1000]

// Filtragem com closure:
pares = [1, 2, 3, 4, 5, 6][function(x) x % 2 == 0] // [2, 4, 6]
```

### 5.5. Operações com Coleções (`+`, `-`, `*`)
```pnuts
// Concatenação
juntos = [1, 2] + [3, 4]   // [1, 2, 3, 4]
mapaUnido = {"a" => 1} + {"b" => 2}

// Diferença (subtração de elementos)
restante = [1, 2, 3, 4] - [2, 4]  // [1, 3]

// Interseção de Sets
comuns = set([1, 2, 3]) * set([2, 3, 4])  // [2, 3]
```

---

## 6. Estruturas de Controle de Fluxo

### 6.1. `if / else if / else`
O `if` pode ser usado como declaração ou como expressão retornando valor:
```pnuts
if (score >= 90) {
  conceito = "A"
} else if (score >= 70) {
  conceito = "B"
} else {
  conceito = "C"
}

// Como expressão (atribuição direta):
resultado = if (status) "Ativo" else "Inativo"
```

### 6.2. `switch / case`
O `switch` compara valores usando `.equals()`:
```pnuts
switch (tipoOperacao) {
  case "VENDA":
    processarVenda()
    break
  case "COMPRA":
    processarCompra()
    break
  default:
    throwError("Operação desconhecida")
}
```

### 6.3. Laços de Repetição (`for`, `foreach`, `while`, `do..while`)

#### A. Intervalo Numérico (`for (i : inicio..fim)`)
```pnuts
// Crescente: 0 a 4
for (i : 0..4) {
  println("Índice: " + i)
}

// Decrescente: 5 a 1
for (i : 5..1) {
  println("Contagem: " + i)
}
```

#### B. Iteração de Coleções e Iterators
```pnuts
itens = ["Mouse", "Teclado", "Monitor"]

for (item : itens) {
  println("Produto: " + item)
}
```

#### C. A Sintaxe Corporativa `foreach`
Na base de código da empresa, o comando `foreach` é amplamente utilizado para percorrer coleções e cursores:
```pnuts
// Iterando sobre um Vector/Enumeration/Collection:
foreach wrow(wrows.elements()) {
  wipsr = wrow.ipsr
  wval = wrow.mvalun
  println("Processando linha item: " + wipsr)
}
```

#### D. Laço Tradicional estilo C/Java
```pnuts
for (i = 0; i < itens.length; i++) {
  println(itens[i])
}
```

#### E. `while` e `do..while`
```pnuts
while (cursor.hasNext()) {
  linha = cursor.next()
}

do {
  tentativas++
} while (tentativas < 3)
```

---

## 7. Funções, Closures & Geradores

### 7.1. Declaração de Funções
```pnuts
function calcularTotal(precoUnitario, quantidade) {
  subtotal = precoUnitario * quantidade
  return subtotal
}

// A última expressão de uma função é retornada automaticamente se 'return' for omitido:
function dobro(n) {
  n * 2
}
```

### 7.2. Sobrecarga por Quantidade de Argumentos (Aridade)
Em Pnuts, funções com o mesmo nome e números diferentes de parâmetros coexistem de forma independente:
```pnuts
function formatarValor(valor) {
  return formatarValor(valor, "R$")
}

function formatarValor(valor, moeda) {
  return moeda + " " + valor
}

println(formatarValor(50.0))       // "R$ 50.0"
println(formatarValor(50.0, "USD")) // "USD 50.0"
```

### 7.3. Parâmetros Variáveis (Varargs)
Coloque `[]` no último parâmetro:
```pnuts
function logMensagem(categoria, mensagens[]) {
  for (msg : mensagens) {
    println("[" + categoria + "] " + msg)
  }
}

logMensagem("INFO", "Iniciando processo", "Arquivo carregado", "Concluído com sucesso")
```

### 7.4. Funções Anônimas & Sintaxe de Arrow (`{a, b -> ...}`)
```pnuts
// Função anônima padrão
soma = function(a, b) { a + b }

// Sintaxe compacta de Arrow / Closure:
multiplica = {a, b -> a * b}

println(multiplica(5, 4)) // 20
```

### 7.5. Geradores e `yield`
Geradores criam sequências avaliadas sob demanda (*lazy evaluation*):
```pnuts
function gerarFiscais(lista) {
  for (item : lista) {
    if (item.valorImposto > 0) {
      yield item
    }
  }
}

// Consumindo o gerador:
for (fiscal : gerarFiscais(documentos)) {
  println(fiscal.id)
}
```

---

## 8. Interoperabilidade com Java (Java Interop)

Pnuts roda dentro da JVM e permite manipular qualquer classe Java nativamente.

### 8.1. Importação de Classes Java
```pnuts
// Importar uma classe
import("java.math.BigDecimal")
import("java.text.SimpleDateFormat")

// Importar pacote com wildcard
import("java.util.*")
import("java.io.*")

// Import estático
import static java.lang.Math.*
raiz = sqrt(64) // 8.0
```

### 8.2. Instanciação de Classes
Você pode instanciar usando `new` ou invocando a classe diretamente como função:
```pnuts
// Estilo 1: new
dataFormat = new SimpleDateFormat("dd/MM/yyyy")

// Estilo 2: Chamada direta da classe
lista = ArrayList()
ponto = java.awt.Point(10, 20)
```

### 8.3. Métodos e Campos Estáticos
Para membros estáticos, utiliza-se a notação `::` (ou `.` quando a classe for referenciada):
```pnuts
// Invocando métodos estáticos
Character::isDigit('9')                   // true
System::currentTimeMillis()
MLogin::getUser()

// Acessando constantes estáticas
modoArredondamento = BigDecimal::ROUND_DOWN
corAzul = java.awt.Color::blue
```

### 8.4. Acesso a Propriedades JavaBeans (Dot Notation)
Qualquer objeto com métodos `getPropriedade()` / `setPropriedade(valor)` pode ser acessado de forma direta:
```pnuts
data = new java.util.Date()
// data.time equivale a data.getTime()
println(data.time)

// Exemplo com formulários internos:
wdfform = MForm::getDialogForm(m, "MEUFORM", "ME", "Título", 3)
wdfform.ipsr = "PROD001"   // Invoca wdfform.setIpsr("PROD001")
wdfform.qpsr = 10          // Invoca wdfform.setQpsr(10)
```

### 8.5. Conversões Explícitas para Tipos Primitivos
Pnuts oferece funções embutidas correspondentes aos tipos primitivos Java:
```pnuts
num = int("123")         // Converte String -> Integer (123)
flt = double("12.34")    // Converte String -> Double (12.34)
letra = char(65)         // Converte código ASCII -> Character ('A')
codigo = int('A')        // Retorna 65
bin = int("1010", 2)     // Parsing com base (retorna 10)
```

### 8.6. Criação de Subclasses e Listeners Dinâmicos
```pnuts
import java.awt.event.ActionListener

botao.addActionListener(new ActionListener() {
  actionPerformed(evento) {
    println("Botão clicado!")
  }
})
```

---

## 9. Tratamento de Exceções & Erros

### 9.1. Bloco `try / catch / finally`
```pnuts
try {
  validarChaveNFe(chaveAcesso)
  processarLote()
} catch (java.io.IOException e) {
  println("Erro de E/S: " + e.getMessage())
} catch (Exception e) {
  println("Erro genérico: " + e.getMessage())
} finally {
  cursor.commitWork()
  flush()
}
```

### 9.2. Lançando Exceções (`throw` e `throwError`)
```pnuts
// Lançando string (cria RuntimeException automaticamente)
if (wsize == 0) {
  throw "Documento não possui linhas para processamento."
}

// Na nossa base de código corporativa, frequentemente usamos o helper:
if (valorInvalido) {
  throwError("Operação cancelada pelo usuário.")
}
```

---

## 10. Pacotes, Módulos & Importações

### 10.1. Pacotes Pnuts
```pnuts
// Definir pacote do script atual:
package("fiscal.regras")

function calcularAliquota(ufOrigem, ufDestino) {
  if (ufOrigem == ufDestino) return 0.18B
  return 0.12B
}

// Acessando de outro script:
aliq = fiscal.regras::calcularAliquota("SP", "RJ")
```

### 10.2. Carregamento de Outros Scripts
- `loadFile("caminho/arquivo.pnuts")`: Executa um script local.
- `require("meu_script")`: Carrega o script apenas se ainda não tiver sido carregado.
- `eval("2 + 2")`: Avalia código dinamicamente em runtime.

### 10.3. Módulos da Biblioteca Pnuts
O Pnuts possui módulos padronizados que podem ser habilitados via `use()`:
- `use("pnuts.lib")`: Utilitários gerais (`println`, `format`, `date`, `sort`, `filter`).
- `use("pnuts.regex")`: Expressões regulares.
- `use("pnuts.text")`: Manipulação avançada de textos e templates.
- `use("pnuts.io")`: Streams e manipulação de arquivos.

---

## 11. Padrões e Casos Reais na Empresa

Nossa base de código manipula principalmente faturamento, notas fiscais (NF-e, NFS-e), estoque e integrações ERP. Abaixo estão os padrões fundamentais adotados no nosso dia a dia.

### 11.1. Estrutura Padrão de Cabeçalho de Script
```pnuts
//********************************************************************************
// Nome.....: ddoc0_mc_exemplo_fn(wparam1, wparam2)
// Objetivo.: Descrição sucinta da regra de negócio
// Histórico:
// 2026-08-27 - NOME DO DESENVOLVEDOR - NPP.001234 - Implementação inicial
//********************************************************************************
```

### 11.2. Logs e Flush Obrigatório
Sempre que emitir mensagens no console corporativo, utilize `flush()` para garantir o envio imediato da saída ao buffer:
```pnuts
println("FUNCTION: DDOC0_MC_EXEMPLO_FN - INICIO"); flush()
```

### 11.3. Acesso ao Banco de Dados e Cursores
```pnuts
// Obter cursor e transação
cursor = MConnection::getMCursor()
cursor.beginWork()

try {
  // Query retornando valor único:
  testado = getSQLValue("select testado from bentt0 where ient = 'TEC'")
  
  // Query retornando número:
  qtdItens = getSQLNumber("select count(zk) from ddolt0 where idoc = '" + widoc + "'")

  // Carregar linhas como coleção de registros (Vector de MRows):
  wsel = "select a.* from ddolt0 a where a.idoc = '" + widoc + "'"
  wvddol = getMRows("DDOL0", wsel)

  // Iterar nas linhas:
  foreach wddol(wvddol.elements()) {
    wipsr = wddol.ipsr.trim()
    wval = wddol.mvalun
    // processamento...
  }

  // Executar update/insert/delete:
  executeSQL("update ddolt0 set zcst = 'ok' where idoc = '" + widoc + "'")

  cursor.commitWork()
} catch (Exception e) {
  cursor.rollbackWork()
  throwError("Falha na execução: " + e.getMessage())
}
```

### 11.4. Manipulação Segura de Strings e Campos Nulos
Sempre use `.trim()` para evitar problemas com espaços em branco vindos do banco de dados (campos `CHAR` fixos):
```pnuts
wiorg = row.iorg.trim()
wcfop = wddol.ctcf.trim()
```

### 11.5. Cálculos Financeiros com `BigDecimal`
```pnuts
import("java.math.BigDecimal")

valorTotal = 1250.75B
divisor = 11B

// Divisão com controle de arredondamento
resultado = toBigDecimal(valorTotal).divide(divisor, BigDecimal::ROUND_DOWN).intValue()
```

---

## 12. Cheatsheet: Pnuts vs Java vs JavaScript

| Recurso | Pnuts | Java | JavaScript (ES6+) |
| :--- | :--- | :--- | :--- |
| **Declaração de Variável** | `x = 10` | `int x = 10;` | `let x = 10;` |
| **Número BigDecimal** | `100.50B` | `new BigDecimal("100.50")` | `BigInt(100)` (aprox) |
| **Array** | `[1, 2, 3]` | `new int[]{1, 2, 3}` | `[1, 2, 3]` |
| **Acesso a Índices Negativos** | `arr[-1]` | `arr[arr.length - 1]` | `arr.at(-1)` |
| **Map / Dicionário** | `{"chave" => "valor"}` | `Map.of("chave", "valor")` | `{"chave": "valor"}` |
| **Interpolação de String** | `"Total: \(valor)"` | `String.format("Total: %s", v)` | `` `Total: ${valor}` `` |
| **Verbatim String** | `` `SELECT * FROM tab` `` | `"""SELECT * FROM tab"""` | `` `SELECT * FROM tab` `` |
| **Chamada de Método Estático** | `Classe::metodo()` | `Classe.metodo()` | `Classe.metodo()` |
| **Propriedade JavaBean** | `obj.nome` | `obj.getNome()` | `obj.nome` |
| **Iteração em Intervalo** | `for (i : 0..9)` | `for (int i=0; i<10; i++)` | `for (let i=0; i<10; i++)` |
| **Laço em Coleção** | `foreach x(lista.elements())` | `for (T x : lista)` | `for (const x of lista)` |
| **Função / Closure** | `function soma(a, b) { a + b }` ou `{a, b -> a + b}` | `(a, b) -> a + b` | `(a, b) => a + b` |
| **Conversão de Tipo** | `int("123")` | `Integer.parseInt("123")` | `parseInt("123")` |
| **Igualdade de Arrays** | `[1, 2] == [1, 2]` *(true)* | `Arrays.equals(a, b)` | `JSON.stringify(a)==...` |

---

## 🎯 Dicas de Boas Práticas para o Time

1. **Formate antes de Commitar:** Use o atalho `Shift + Alt + F` no VSCode ou execute `python3 scripts/format_pnuts.py --diff <seu-arquivo>` antes de enviar seu código para revisão.
2. **Cuidado com Strings SQL:** Utilize crases `` `...` `` para construir queries SQL limpas e sem caracteres de escape desnecessários.
3. **Não ignore o `trim()`:** Campos alfanuméricos de bancos legados costumam ter *padding* de espaços.
4. **Utilize `BigDecimal`:** Cálculos de impostos e valores contábeis NUNCA devem ser feitos com ponto flutuante padrão (`float`/`double`).
5. **Transações Atômicas:** Sempre garanta que `cursor.beginWork()` tenha o respectivo `commitWork()` ou `rollbackWork()` encapsulados em blocos `try/catch`.

---
*Documentação gerada para onboarding e padronização do time de engenharia.*
