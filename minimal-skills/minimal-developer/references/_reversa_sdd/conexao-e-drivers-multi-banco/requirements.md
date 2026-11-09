# Conexao e Drivers Multi Banco

## Visao Geral

A unit de conexao e drivers multi-banco fornece a infraestrutura JDBC do MinimalJ: abre conexoes nomeadas, registra drivers conforme o DSN, identifica o produto do banco, cria cursores especificos por dialeto e controla transacoes de trabalho ou batch. 🟢 **CONFIRMADO**

Ela e uma dependencia central para login, autorizacao, CRUD, geracao de DDL, metadados, scripts Pnuts com acesso a dados e rotinas administrativas. 🟢 **CONFIRMADO**

Validacao humana em 2026-05-22: o runtime alvo deve **preservar** o comportamento Java legado, incluindo compatibilidade com JDBC-ODBC, em vez de migrar imediatamente para JDK moderno com substituicoes. 🟢 **CONFIRMADO PELO USUARIO**

## Responsabilidades

- Abrir conexao JDBC a partir de DSN, usuario e senha. 🟢 **CONFIRMADO**
- Registrar drivers conforme padroes no DSN: Oracle, Informix, ODBC, SQL Server e PostgreSQL. 🟢 **CONFIRMADO**
- Detectar ambiente/produto do banco via `DatabaseMetaData`. 🟢 **CONFIRMADO**
- Manter cache estatico de conexoes nomeadas, incluindo `DEFAULT` e `JMETADATA`. 🟢 **CONFIRMADO**
- Criar cursor especializado por ambiente: `MCursorInf`, `MCursorOra`, `MCursorSql` ou `MCursorPg`. 🟢 **CONFIRMADO**
- Configurar isolamento, autocommit, schema Oracle e lock mode Informix. 🟢 **CONFIRMADO**
- Executar queries, statements, prepared statements e calls por meio de `MCursor`. 🟢 **CONFIRMADO**
- Gerenciar `beginWork`/`commitWork`/`rollbackWork` e `beginBatch`/`commitBatch`/`rollbackBatch`. 🟢 **CONFIRMADO**
- Tentar reconexao em erros de conexao resetada para PostgreSQL, Informix, Oracle e SQL Server. 🟢 **CONFIRMADO**

## Regras de Negocio

- A conexao default sem parametros deve usar `MUtil.myProp["user.dsn"]`, `MUtil.myProp["user.name"]` e `MUtil.myProp["user.passwd"]`. 🟢 **CONFIRMADO**
- Cada conexao deve ser registrada em `MConnectionList` pelo nome (`wname`) apos abertura bem-sucedida. 🟢 **CONFIRMADO**
- `getMConnection("DEFAULT")` cria uma nova conexao default se ela ainda nao existir. 🟢 **CONFIRMADO**
- `getMConnection(nome, dsn, usuario, senha)` reutiliza uma conexao nomeada existente antes de criar outra. 🟢 **CONFIRMADO**
- O ambiente deve ser normalizado para `Informix`, `Oracle`, `SQL Server` ou `PostgreSQL` quando o produto do banco indicar esses motores. 🟢 **CONFIRMADO**
- Para Oracle, o sistema deve ajustar `Locale.US` antes de registrar o driver. 🟢 **CONFIRMADO**
- Para Oracle com `MULTISCHEMA`, o sistema deve definir `CURRENT_SCHEMA` com base no usuario/schema e tentar ativar role `USER_OF_<SCHEMA>`. 🟢 **CONFIRMADO**
- Para Oracle sem multischema, o sistema deve definir `CURRENT_SCHEMA=MINIMAL` e tentar role `USER_OF_MINIMAL`. 🟢 **CONFIRMADO**
- Para Informix, o sistema deve executar `set lock mode to wait 60`. 🟢 **CONFIRMADO**
- `beginWork` nao deve iniciar se `beginBatch` estiver ativo; `beginBatch` nao deve iniciar se ja houver work ou batch em aberto. 🟢 **CONFIRMADO**
- `commitBatch` e `rollbackBatch` devem falhar se nao houver batch aberto. 🟢 **CONFIRMADO**
- Um ambiente sem cursor especializado conhecido deve falhar com erro `MIN-983999E`. 🟢 **CONFIRMADO**
- A lista completa de versoes de drivers e JARs externos nao esta versionada no repositorio. 🔴 **LACUNA**

## Requisitos Funcionais

| ID | Requisito | Prioridade | Criterio de Aceite |
|----|-----------|------------|--------------------|
| RF-01 | Abrir conexao JDBC com DSN, usuario e senha. | Must | Dado DSN e credenciais validos, quando `openConnection()` executa, entao `DriverManager.getConnection` retorna conexao e `DatabaseMetaData` e armazenado. |
| RF-02 | Registrar driver conforme DSN. | Must | Dado DSN contendo `oracle`, `informix`, `odbc`, `microsoft`, `jdbc:sqlserver` ou `postgresql`, quando `openConnection()` executa, entao o driver correspondente e registrado antes da conexao. |
| RF-03 | Identificar ambiente e versao do banco. | Must | Quando a conexao abre, entao `getAmbient()`, `getEngine()` e `getURL()` retornam dados derivados de `DatabaseMetaData`. |
| RF-04 | Cachear conexoes nomeadas. | Must | Dado conexao ja criada para um nome, quando `getMConnection(nome, ...)` executa novamente, entao retorna a instancia existente. |
| RF-05 | Criar cursor especifico por ambiente. | Must | Dado conexao Oracle/Informix/SQL Server/PostgreSQL, quando `getMCursor(MConnection)` executa, entao retorna `MCursorOra`/`MCursorInf`/`MCursorSql`/`MCursorPg`. |
| RF-06 | Executar queries e expor metadados de resultado. | Must | Quando `MCursor.execQuery()` executa com sucesso, entao `numCols`, `colNames`, `ResultSetMetaData` e primeira linha sao preparados. |
| RF-07 | Executar SQL preparado e callable statement. | Should | Dado SQL com parametros, quando `prepareStatement`/`prepareCall` executa, entao o statement e criado apos passar por `checkSQL`. |
| RF-08 | Controlar transacoes por work e batch. | Must | Dado `beginBatch`, quando `commitBatch` ou `rollbackBatch` executa, entao commit/rollback ocorre, autocommit volta a `true` e flag de batch e limpa. |
| RF-09 | Reabrir conexao em queda detectada. | Should | Dado erro de conexao resetada reconhecido, quando query ou inicio de transacao falha, entao o sistema chama `reconnect()` e tenta reabrir a conexao. |
| RF-10 | Fechar conexoes globais. | Must | Quando `closeAll()` executa, entao cada conexao cacheada e fechada e removida de `MConnectionList`. |

## Requisitos Nao Funcionais

| Tipo | Requisito inferido | Evidencia no codigo | Confianca |
|------|--------------------|---------------------|-----------|
| Compatibilidade | Deve suportar Oracle, Informix, PostgreSQL, SQL Server e ODBC conforme DSN. | `PT/Minimal/geral/MConnection.java:91`, `:98`, `:105`, `:112`, `:119`, `:126` | 🟢 CONFIRMADO |
| Compatibilidade | ODBC depende de `sun.jdbc.odbc.JdbcOdbcDriver`, removido em Java 8+. | `PT/Minimal/geral/MConnection.java:17`, `:105` | 🟢 CONFIRMADO |
| Confiabilidade | Deve tentar reconexao para erros de connection reset reconhecidos por dialeto. | `PT/Minimal/geral/MConnection.java:357`, `PT/Minimal/geral/MCursor.java:527` | 🟢 CONFIRMADO |
| Consistencia | Operacoes batch devem controlar autocommit e impedir sobreposicao com work. | `PT/Minimal/geral/MConnection.java:395`, `:459`, `:493` | 🟢 CONFIRMADO |
| Observabilidade | Deve emitir logs de DSN, produto, motor, URL, isolamento e autocommit no console. | `PT/Minimal/geral/MConnection.java:85`, `:164` | 🟢 CONFIRMADO |
| Portabilidade | A ausencia de gerenciador de dependencias torna o runtime dependente do classpath externo. | `_reversa_sdd/dependencies.md` | 🟢 CONFIRMADO |

## Criterios de Aceitacao

```gherkin
Cenario: Abrir conexao Oracle
Dado um DSN contendo oracle
Quando MConnection.openConnection executa
Entao OracleDriver e registrado
E a conexao JDBC e aberta
E o ambiente e normalizado como Oracle

Cenario: Abrir conexao PostgreSQL
Dado um DSN contendo postgresql
Quando MConnection.openConnection executa
Entao org.postgresql.Driver e registrado
E getMCursor retorna MCursorPg para essa conexao

Cenario: Ambiente sem cursor suportado
Dado uma conexao cujo ambiente nao inicia com Informix, Oracle, SQL Server ou Postgres
Quando getMCursor executa
Entao o sistema falha com erro MIN-983999E

Cenario: Batch com commit
Dado uma conexao aberta sem work ou batch ativo
Quando beginBatch executa
E commitBatch executa
Entao a transacao e commitada
E autoCommit volta a true
E inBeginBatch retorna falso

Cenario: Query com queda de conexao reconhecida
Dado uma query em execucao
E o driver retorna erro de connection reset reconhecido
Quando execQuery captura o erro
Entao o sistema chama reconnect
E tenta executar a query novamente
```

## Prioridade (MoSCoW)

| Requisito | MoSCoW | Justificativa |
|-----------|--------|---------------|
| Abrir conexao JDBC e identificar ambiente | Must | Base de login, metadados, CRUD, DDL e autorizacao. 🟢 |
| Criar cursor especifico por banco | Must | Cada dialeto tem comportamento proprio para SQL, triggers e chaves. 🟢 |
| Controlar transacoes por batch/work | Must | Escritas CRUD dependem de commit/rollback correto. 🟢 |
| Suporte a reconexao | Should | Importante para resiliencia, mas implementado apenas para erros especificos. 🟢 |
| ODBC | Could | Suporte legado, mas depende de recurso removido em Java moderno. 🟢 |
| Logs em console | Could | Ajuda diagnostico, mas nao substitui observabilidade estruturada. 🟢 |

## Rastreabilidade de Codigo

| Arquivo | Funcao / Classe | Cobertura |
|---------|-----------------|-----------|
| `PT/Minimal/geral/MConnection.java` | `openConnection`, `getMConnection`, `getMCursor`, `beginBatch`, `commitBatch`, `rollbackBatch`, `closeAll` | 🟢 |
| `PT/Minimal/geral/MCursor.java` | `execQuery`, `prepareStatement`, `prepareCall`, tratamento de `SQLException`/`PSQLException` | 🟢 |
| `PT/Minimal/geral/MCursorInf.java` | Cursor especializado Informix | 🟢 |
| `PT/Minimal/geral/MCursorOra.java` | Cursor especializado Oracle | 🟢 |
| `PT/Minimal/geral/MCursorPg.java` | Cursor especializado PostgreSQL | 🟢 |
| `PT/Minimal/geral/MCursorSql.java` | Cursor especializado SQL Server | 🟢 |
| `_reversa_sdd/dependencies.md` | Mapeamento de drivers e dependencias externas | 🟢 |
| `_reversa_sdd/architecture.md` | Container "Motor de persistencia" e integracoes JDBC | 🟢 |

## Lacunas Pendentes

| Lacuna | Impacto | Confianca |
|--------|---------|-----------|
| Versoes dos drivers Oracle, Informix, PostgreSQL, SQL Server, ODBC e Pnuts nao estao no repositorio. | Dificulta reproduzir runtime e diagnosticar compatibilidade. | 🔴 LACUNA |
| Nao ha suite automatizada exercitando os quatro cursores especializados. | Risco alto em migracao de SQL/dialetos. | 🔴 LACUNA |
| O contrato completo de `checkSQL`, `getKey` e `checkTriggers` depende das subclasses de cursor e metadados reais. | Exige detalhamento adicional nos arquivos de design/tasks desta unit. | 🟡 INFERIDO |
| O suporte ODBC depende de Java legado. | Bloqueia execucao direta em JDK moderno sem substituicao de driver ou ponte externa. | 🟢 CONFIRMADO |

Nota de validacao humana: em 2026-05-22, o usuario escolheu preservar o runtime legado. A lacuna de versoes/JARs permanece, mas a direcao arquitetural para esta extracao e manter compatibilidade com o ambiente antigo.
