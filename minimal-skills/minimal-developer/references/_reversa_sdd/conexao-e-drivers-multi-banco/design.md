# Conexao e Drivers Multi Banco, Design Tecnico

## Interface

### Classes Principais

| Simbolo | Assinatura | Retorno | Observacao |
|---------|------------|---------|------------|
| `MConnection.MConnection()` | `()` | instancia | Usa `MUtil.myProp` e nomeia a conexao como `DEFAULT`. 🟢 |
| `MConnection.MConnection` | `(pname, pdsn, puser, ppasswd)` | instancia | Cria conexao nomeada com credenciais explicitas. 🟢 |
| `MConnection.openConnection` | `()` | `void` | Registra driver, abre JDBC, detecta metadados, configura ambiente. 🟢 |
| `MConnection.getMConnection` | `(pname?)` | `MConnection` | Resolve conexao cacheada e cria `DEFAULT` sob demanda. 🟢 |
| `MConnection.getMCursor` | `(MConnection ou nome/credenciais)` | `MCursor` | Instancia cursor por ambiente. 🟢 |
| `MConnection.beginBatch` | `()` | `void` | Desliga autocommit e marca batch ativo. 🟢 |
| `MConnection.commitBatch` | `()` | `void` | Commit, restaura autocommit e limpa batch. 🟢 |
| `MConnection.rollbackBatch` | `()` | `void` | Rollback, restaura autocommit e limpa batch. 🟢 |
| `MCursor.execQuery` | `(name?, sql)` | `void` | Aplica `checkSQL`, executa query, prepara metadados de resultado. 🟢 |
| `MCursor.prepareStatement` | `(name?, sql)` | `PreparedStatement` | Aplica `checkSQL` e prepara statement. 🟢 |
| `MCursor.prepareCall` | `(name?, sql)` | `CallableStatement` | Aplica `checkSQL` e prepara chamada procedural. 🟢 |
| `MCursor.execStatement` | `(sql, doCheck)` | `int` | Implementado por cursor especifico. 🟢 |
| `MCursor.execPreparedUpdate` | `()` | `int` | Implementado por cursor especifico. 🟢 |
| `MCursor.getKey` | `(entidade, fase, zk)` | `String` | Implementado por dialeto para gerar chave tecnica. 🟢 |
| `MCursor.checkTriggers` | `(entidade)` | `void` | Implementado por dialeto; efetivo principalmente em Informix/SQL Server. 🟢 |

### Drivers e Ambientes

| Sinal no DSN | Driver registrado | Ambiente normalizado | Cursor | Confianca |
|--------------|-------------------|----------------------|--------|-----------|
| `oracle` | `oracle.jdbc.driver.OracleDriver` | `Oracle` | `MCursorOra` | 🟢 CONFIRMADO |
| `informix` | `com.informix.jdbc.IfxDriver` | `Informix` | `MCursorInf` | 🟢 CONFIRMADO |
| `odbc` | `sun.jdbc.odbc.JdbcOdbcDriver` | depende do produto | depende do produto | 🟢 CONFIRMADO |
| `microsoft` | `com.microsoft.jdbc.sqlserver.SQLServerDriver` | `SQL Server` | `MCursorSql` | 🟢 CONFIRMADO |
| `jdbc:sqlserver` | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | `SQL Server` | `MCursorSql` | 🟢 CONFIRMADO |
| `postgresql` | `org.postgresql.Driver` | `PostgreSQL` | `MCursorPg` | 🟢 CONFIRMADO |

## Fluxo Principal

1. O chamador solicita uma conexao via construtor ou `MConnection.getMConnection(...)`. 🟢 `PT/Minimal/geral/MConnection.java:509`
2. `openConnection()` registra o driver compatível com o DSN por buscas de substring. 🟢 `PT/Minimal/geral/MConnection.java:91`
3. `DriverManager.getConnection(wdsn, wuser, wpasswd)` abre a conexao JDBC. 🟢 `PT/Minimal/geral/MConnection.java:133`
4. `DatabaseMetaData` fornece produto, versao e URL; o produto e normalizado em `wamb` e a versao em `wmotor`. 🟢 `PT/Minimal/geral/MConnection.java:135`
5. A conexao e cacheada em `MConnectionList` pelo nome `wname`. 🟢 `PT/Minimal/geral/MConnection.java:180`
6. O isolamento e autocommit sao ajustados conforme ambiente; Informix recebe `set lock mode to wait 60`; Oracle recebe configuracao de schema/role. 🟢 `PT/Minimal/geral/MConnection.java:184`
7. O chamador solicita `MCursor`; `getMCursor(MConnection)` escolhe a subclasse pelo prefixo de `getAmbient()`. 🟢 `PT/Minimal/geral/MConnection.java:619`
8. O cursor executa SQL via `execQuery`, `prepareStatement`, `prepareCall`, `execStatement` ou `execPreparedUpdate`, sempre aplicando `checkSQL` quando configurado. 🟢 `PT/Minimal/geral/MCursor.java:516`
9. Em erros de conexao resetada reconhecidos, `MConnection` ou `MCursor` tentam `reconnect()` e repetem a operacao. 🟢 `PT/Minimal/geral/MCursor.java:527`

## Fluxos Alternativos

- **Conexao DEFAULT sob demanda:** `getMConnection("DEFAULT")` cria `new MConnection()` se o cache nao contem `DEFAULT`. 🟢
- **Conexao nomeada externa:** `getMConnection(nome, dsn, user, passwd)` cria conexao apenas se o nome ainda nao existir. 🟢
- **Fechamento global:** `closeAll()` percorre conexoes cacheadas, fecha cada uma e remove do cache. 🟢
- **Fechamento de auxiliares:** `closeAllExceptNormal()` preserva `DEFAULT` e `JMETADATA`, fechando conexoes temporarias. 🟢
- **Reconexao:** `reconnect()` fecha e reabre a conexao; depois reexecuta variaveis globais Pnuts de login. 🟢
- **Ambiente desconhecido:** se `getAmbient()` nao indica banco suportado, `getMCursor` lanca `MIN-983999E`. 🟢

## Dependencias

- JDBC standard (`java.sql.Connection`, `DriverManager`, `DatabaseMetaData`, `Statement`, `PreparedStatement`, `CallableStatement`, `ResultSet`). 🟢
- Drivers externos Oracle, Informix, PostgreSQL e SQL Server. 🟢 para uso no codigo; 🔴 para versoes e JARs.
- `sun.jdbc.odbc.JdbcOdbcDriver` para ODBC legado. 🟢
- `MLogin` para propriedades `MULTISCHEMA`, `SCHEMA`, `SCHEMAFORCE`, `JMETADATA` e verificacao de chave Minimal. 🟢
- `MPnuts`/pacote `LOGIN` na reconexao para restaurar variaveis globais. 🟢
- `MConsole.getTraceSql()` para tracing SQL textual no console. 🟢
- `MError` para empacotar `SQLException` com ambiente e SQL. 🟢

## Decisoes de Design Identificadas

| Decisao | Evidencia no codigo | Confianca |
|---------|---------------------|-----------|
| Resolver driver por substring do DSN, nao por configuracao tipada. | `PT/Minimal/geral/MConnection.java:87` | 🟢 CONFIRMADO |
| Cache global estatico de conexoes nomeadas. | `PT/Minimal/geral/MConnection.java:180`, `:514` | 🟢 CONFIRMADO |
| Usar subclasses de cursor para dialetos SQL. | `PT/Minimal/geral/MConnection.java:619` | 🟢 CONFIRMADO |
| Traduzir SQL legado em `checkSQL` por dialeto. | `PT/Minimal/geral/MCursorOra.java:24`, `MCursorPg.java:24`, `MCursorSql.java:23`, `MCursorInf.java:21` | 🟢 CONFIRMADO |
| Usar stored procedures/funcoes `<ENTIDADE>pk` para gerar `zk` antes de insert. | `PT/Minimal/geral/MCursorOra.java:77`, `MCursorInf.java:60`, `MCursorSql.java:150`, `MCursorPg.java:87` | 🟢 CONFIRMADO |
| Representar transacao batch e work por flags booleanas no wrapper de conexao. | `PT/Minimal/geral/MConnection.java:37`, `:343`, `:395` | 🟢 CONFIRMADO |
| Reconectar apenas para erros conhecidos de connection reset. | `PT/Minimal/geral/MConnection.java:357`, `PT/Minimal/geral/MCursor.java:527` | 🟢 CONFIRMADO |

## Estado Interno

| Campo | Classe | Evolucao | Confianca |
|-------|--------|----------|-----------|
| `MConnectionList` | `MConnection` | Inicializada sob demanda e usada como cache global por nome. | 🟢 |
| `minConnection` | `MConnection` | Conexao JDBC real aberta/fechada/reaberta. | 🟢 |
| `minDMD` | `MConnection` | Metadata do banco usada para produto, versao, URL e quote string. | 🟢 |
| `wamb` | `MConnection` | Ambiente normalizado (`Informix`, `Oracle`, `SQL Server`, `PostgreSQL`). | 🟢 |
| `wmotor` | `MConnection` | Versao do motor prefixada por banco em alguns casos. | 🟢 |
| `wurl` | `MConnection` | URL retornada pelo metadata. | 🟢 |
| `MQuote` | `MConnection`/`MCursor` | Quote identifier do banco, com fallback para `"`. | 🟢 |
| `bBegin` | `MConnection` | Marca work transacional aberto. | 🟢 |
| `bBatch` | `MConnection` | Marca batch transacional aberto. | 🟢 |
| `locked` | `MConnection` | Impede reconexao quando travada. | 🟢 |
| `minWcsel` | `MCursor` | SQL corrente apos traducao por `checkSQL`. | 🟢 |
| `wdcode` / `wdmsg` | `MCursor`/`MConnection` | Codigo/mensagem operacional de SQL. | 🟢 |
| `numCols` / `colNames` | `MCursor` | Metadados da query executada. | 🟢 |

## Variacoes por Dialeto

| Dialeto | `checkSQL` | Chave `zk` | Trigger/lock/introspeccao | Confianca |
|---------|------------|------------|---------------------------|-----------|
| Oracle | Converte sintaxe `campo[ini,fim]` para `substr`, remove `outer`, troca `''` por `' '`. | `select <ent>pk() from dual`. | Locks com `for update nowait`; habilita/desabilita triggers; consulta `all_objects`, `all_indexes`, `all_triggers`. | 🟢 |
| PostgreSQL | Converte `nvl` para `coalesce`, `USER` para `lower(USER)`, remove `from dual`, `(+)` e `outer`. | Usa chamada similar a Oracle, com ajustes em procedures. | Locks de tabela; calls com retorno posicional. | 🟢 |
| SQL Server | Converte concat `||` para `+`, `substr` para `substring`, `today()`/`time()`, `nvl` para `isnull`, varias funcoes para `dbo.*`. | `{call dbo.<ent>pk}`. | `exec` para procedures e introspeccao por catalogos SQL Server. | 🟢 |
| Informix | Remove `(+)`, `MINVARIABLES.` e normaliza `''`. | `{call <ent>pk()}`. | `sysobjstate`, `systriggers`, `sysprocedures`, `set triggers`. | 🟢 |

## Observabilidade

- `MConnection.openConnection()` imprime DSN, produto, motor, URL, isolamento, autocommit e catalogo no console. 🟢
- `MConnection.beginBatch`, `commitBatch`, `rollbackBatch`, `beginWork`, `commitWork` e `rollbackWork` imprimem eventos transacionais. 🟢
- `MCursor` e subclasses imprimem SQL quando `MConsole.getTraceSql()` esta ativo. 🟢
- Erros SQL sao encapsulados em `MError` com ambiente e, em `MCursor.minZstaf`, com SQL corrente. 🟢
- Nao ha evidencia de metrica, tracing estruturado, pool de conexoes ou telemetria persistente. 🔴 **LACUNA**

## Riscos e Lacunas

- 🔴 As versoes e JARs exatos dos drivers externos nao estao versionados.
- 🔴 O suporte ODBC usa `sun.jdbc.odbc.JdbcOdbcDriver`, indisponivel em Java 8+.
- 🟡 A traducao SQL por substituicao textual e sensivel a maiusculas/minusculas, literais e casos de borda.
- 🟡 O cache global de conexoes dificulta isolamento de testes e multi-tenant moderno.
- 🟡 `closeAll()` remove entradas do hashtable enquanto enumera chaves, comportamento a validar em runtime alvo.
- 🟢 A reconexao reexecuta variaveis Pnuts de login, entao uma reimplementacao precisa preservar esse efeito colateral.

