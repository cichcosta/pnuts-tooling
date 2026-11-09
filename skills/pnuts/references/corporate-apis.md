# Pnuts: APIs Corporativas, Banco de Dados & Objetos de Negócio

No ecossistema corporativo legado em que o Pnuts opera, scripts interagem intensamente com o framework de persistência, controle de transação, dados de sessão e interface visual.

---

## 1. `MConnection`: Gerenciador de Conexão com o Banco de Dados

Representa a sessão JDBC ativa conectada ao banco de dados relacional. É o objeto central para execução de DDL/DML, controle de transações e criação de cursores.

### Principais Métodos:
- `conn.beginWork()`: Inicia uma transação explícita (desabilita auto-commit).
- `conn.commitWork()`: Confirma e persiste as alterações realizadas na transação.
- `conn.rollbackWork()`: Reverte todas as operações realizadas desde o último `beginWork()`.
- `conn.execute(sql)`: Executa um comando SQL (UPDATE, INSERT, DELETE, etc.).
- `conn.getMCursor(sql)`: Cria e retorna um cursor para consulta de dados (`SELECT`).
- `conn.close()`: Fecha a conexão (usado apenas em conexões dedicadas).

### Padrão Transacional Canônico:
> [!IMPORTANT]
> Toda escrita no banco deve obrigatoriamente seguir o tratamento de exceção com `catch (Exception, function(e) { ... })`:

```pnuts
function atualizarPreco(conn, idProduto, novoPreco) {
  if (conn == null) {
    showError("Conexão inválida.")
    return false
  }

  catch (Exception, function(e) {
    conn.rollbackWork()
    showError("Falha na transação: " + e.getMessage())
    return false
  })

  conn.beginWork()

  sql = "UPDATE PRODUTOS SET PRECO = " + novoPreco + " WHERE ID = " + idProduto
  conn.execute(sql)

  conn.commitWork()
  return true
}
```

---

## 2. `MCursor` e `MRow`: Consultas e Manipulação de Registros

### `MCursor`
Objeto de iteração sobre resultados de queries SQL (análogo a um `ResultSet` inteligente):
- `cursor.open()`: Abre o cursor e posiciona antes do primeiro registro.
- `cursor.next()`: Avança para o próximo registro. Retorna `true` se houver linha ou `false` se chegar ao fim.
- `cursor.getCurrentRow()`: Retorna o objeto `MRow` referente à linha atual.
- `cursor.close()`: Fecha o cursor e libera recursos do banco (obrigatório em bloco `finally`).

### `MRow`
Representa um registro individual do cursor:
- `row.get("NOME_COLUNA")`: Retorna o valor da coluna informada.
- `row.NOME_COLUNA`: Acesso direto como propriedade.
- `row.set("NOME_COLUNA", novoValor)`: Atualiza o valor de uma coluna no registro em memória.

### Exemplo de Consulta Completa:
```pnuts
function listarProdutos(conn, statusFiltro) {
  import("java.util.ArrayList")

  produtos = ArrayList()
  sql = "SELECT ID, DESCRICAO, PRECO FROM PRODUTOS WHERE STATUS = '" + statusFiltro + "' ORDER BY DESCRICAO"
  cursor = conn.getMCursor(sql)

  try {
    cursor.open()
    while (cursor.next()) {
      row = cursor.getCurrentRow()
      item = [:]
      item.id = row.get("ID")
      item.descricao = row.get("DESCRICAO")
      item.preco = row.get("PRECO")
      produtos.add(item)
    }
  } finally {
    cursor.close()
  }

  return produtos
}
```

---

## 3. `MLogin`: Dados da Sessão e Contexto de Autenticação

Representa o usuário autenticado na aplicação corporativa:
- `login.getUser()` / `login.user`: Nome ou login do usuário conectado.
- `login.getCompany()` / `login.company`: Código ou ID da empresa/filial ativa.
- `login.hasPermission(codigo)`: Verifica permissão de acesso a funcionalidade.

---

## 4. `MForm` e Telas de Diálogo

Objetos de interface gráfica e formulários dinâmicos da plataforma:
- `form.show()`: Exibe o formulário para o usuário.
- `form.close()`: Fecha a janela.
- `form.setField(nomeCampo, valor)`: Preenche um componente visual.
- `form.getField(nomeCampo)`: Obtém o valor preenchido na tela.

---

## 5. Funções de Apoio Globais (Built-ins do Framework)

- `getRows(sql)`: Executa uma query e retorna diretamente uma lista de mapas com os resultados (para consultas simples e rápidas).
- `getDialogForm(nomeTela)`: Carrega e instancia uma tela/formulário por identificador.
- `getDialogFormEx(nomeTela, parametros)`: Instancia tela passando mapa de parâmetros iniciais.
- `showError(mensagem)`: Exibe modal de erro na interface do operador.
- `showMessage(mensagem)`: Exibe modal informativo de sucesso ou alerta.

---

## 6. Classes Java de Alta Frequência

Ao utilizar estas classes em qualquer função, lembre-se de declarar o import local:

```pnuts
function exemploImports() {
  import("java.util.ArrayList")
  import("java.util.HashMap")
  import("java.util.List")
  import("java.util.Map")
  import("java.math.BigDecimal")
  import("java.math.RoundingMode")
  import("java.text.SimpleDateFormat")
  import("java.util.Date")
  import("java.util.Calendar")

  df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
  agoraFormatado = df.format(Date())
}
```

---

## 7. Contexto Global de Sessão (`zglobals`)

O `zglobals` é o mapa global compartilhado (`java.util.Map`) da sessão do usuário no Millennium ERP. É amplamente utilizado para transmitir registros e parâmetros entre comandos e telas (especialmente quando funções são registradas no ERP com aridade 0).

### Regra Crítica de Destruição (`zglobals.remove`):
Nunca deixe variáveis em `zglobals` sem limpeza. Se não forem destruídas com `remove("CHAVE")`, elas persistem na memória da sessão, gerando bugs graves de retenção de cache (ex: menus manuais assumindo valores antigos de execuções automatizadas anteriores).

### Padrão Canônico:
```pnuts
// 1. Passagem de parâmetros
zglobals.ROW_UPD_CLI = wnnf
zglobals.DOC = "NVD"

// 2. Execução da rotina
wnvd = fulfillment_mc_gerar_doc_fn(wientt, wient)

// 3. Destruição IMEDIATA das variáveis
zglobals.remove("ROW_UPD_CLI")
zglobals.remove("DOC")
```

