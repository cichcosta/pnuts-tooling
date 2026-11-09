# Conexao e Drivers Multi Banco, Tarefas de Implementacao

## Pre-requisitos

- [ ] Definir runtime Java alvo e estrategia para drivers JDBC externos. 🔴
- [ ] Obter JARs e versoes dos drivers Oracle, Informix, PostgreSQL e SQL Server. 🔴
- [ ] Decidir substituto para `sun.jdbc.odbc.JdbcOdbcDriver` em runtime moderno. 🔴
- [ ] Preparar bancos ou containers de teste para cada dialeto suportado, ou mocks de JDBC com metadados realistas. 🟡
- [ ] Definir estrategia de compatibilidade para SQL legado traduzido por `checkSQL`. 🟢

## Tarefas

- [ ] T-01, Implementar wrapper de conexao nomeada
  - Origem no legado: `PT/Minimal/geral/MConnection.java:28`
  - Criterio de pronto: objeto de conexao preserva nome, DSN, usuario, senha, ambiente, versao, URL, metadata, quote string e flags transacionais.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-02, Implementar abertura de conexao default por propriedades
  - Origem no legado: `PT/Minimal/geral/MConnection.java:44`
  - Criterio de pronto: construtor/fixture default le `user.dsn`, `user.name` e `user.passwd` de configuracao equivalente a `MUtil.myProp`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-03, Implementar registro de drivers por DSN
  - Origem no legado: `PT/Minimal/geral/MConnection.java:87`
  - Criterio de pronto: DSNs contendo `oracle`, `informix`, `odbc`, `microsoft`, `jdbc:sqlserver` e `postgresql` selecionam os drivers correspondentes ou adapters modernos equivalentes.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-04, Implementar deteccao e normalizacao de ambiente
  - Origem no legado: `PT/Minimal/geral/MConnection.java:135`
  - Criterio de pronto: `DatabaseMetaData.getDatabaseProductName()`, versao e URL sao lidos e normalizados para `Informix`, `Oracle`, `SQL Server` ou `PostgreSQL`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-05, Implementar configuracao inicial por dialeto
  - Origem no legado: `PT/Minimal/geral/MConnection.java:184`
  - Criterio de pronto: isolamento/autocommit sao configurados; Informix aplica `set lock mode to wait 60`; Oracle aplica `CURRENT_SCHEMA`/role conforme `MULTISCHEMA`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-06, Implementar cache de conexoes nomeadas
  - Origem no legado: `PT/Minimal/geral/MConnection.java:180`, `PT/Minimal/geral/MConnection.java:514`
  - Criterio de pronto: chamadas repetidas por nome retornam a mesma conexao; `DEFAULT` e criado sob demanda; conexoes nomeadas com credenciais explicitas sao reutilizadas.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-07, Implementar fechamento global e fechamento seletivo
  - Origem no legado: `PT/Minimal/geral/MConnection.java:565`, `PT/Minimal/geral/MConnection.java:574`
  - Criterio de pronto: `closeAll` fecha e remove todas; `closeAllExceptNormal` preserva `DEFAULT` e `JMETADATA`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-08, Implementar controle `beginWork`/`commitWork`/`rollbackWork`
  - Origem no legado: `PT/Minimal/geral/MConnection.java:343`
  - Criterio de pronto: work impede inicio duplicado, respeita batch ativo, controla autocommit e restaura estado apos commit/rollback.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-09, Implementar controle `beginBatch`/`commitBatch`/`rollbackBatch`
  - Origem no legado: `PT/Minimal/geral/MConnection.java:395`
  - Criterio de pronto: batch impede sobreposicao com work/batch, controla autocommit, lanca erros equivalentes para estados invalidos e restaura flags.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-10, Implementar reconexao para erros reconhecidos
  - Origem no legado: `PT/Minimal/geral/MConnection.java:357`, `PT/Minimal/geral/MCursor.java:527`
  - Criterio de pronto: erros PostgreSQL "Connection reset", Informix `-79716`, Oracle `28` e SQL Server "Connection reset by peer" acionam reconexao e retry quando aplicavel.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-11, Implementar fabrica de cursores por ambiente
  - Origem no legado: `PT/Minimal/geral/MConnection.java:619`
  - Criterio de pronto: ambientes conhecidos retornam cursor especializado; ambiente desconhecido falha com erro equivalente a `MIN-983999E`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-12, Implementar base abstrata de cursor
  - Origem no legado: `PT/Minimal/geral/MCursor.java:16`
  - Criterio de pronto: cursor base encapsula SQL corrente, statements, result set, metadata, `wdcode`, `wdmsg`, metodos de query/prepared/call e conversao de resultados.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-13, Implementar traducao SQL por dialeto
  - Origem no legado: `PT/Minimal/geral/MCursorOra.java:24`, `PT/Minimal/geral/MCursorPg.java:24`, `PT/Minimal/geral/MCursorSql.java:23`, `PT/Minimal/geral/MCursorInf.java:21`
  - Criterio de pronto: cada cursor transforma sintaxes legadas documentadas (`[]`, `nvl`, `today`, `outer`, `(+)`, `||`, `substr`, funcoes `dbo.*`) com testes de regressao.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-14, Implementar geracao de chave tecnica `zk`
  - Origem no legado: `PT/Minimal/geral/MCursorOra.java:77`, `PT/Minimal/geral/MCursorInf.java:60`, `PT/Minimal/geral/MCursorSql.java:150`, `PT/Minimal/geral/MCursorPg.java:87`
  - Criterio de pronto: fase `BF` chama funcao/procedure `<ENTIDADE>pk` equivalente por dialeto e retorna chave; outras fases preservam `wzk`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-15, Implementar chamadas procedurais Z
  - Origem no legado: `PT/Minimal/geral/MCursorOra.java:92`, `PT/Minimal/geral/MCursorPg.java:102`, `PT/Minimal/geral/MCursorSql.java:175`, `PT/Minimal/geral/MCursorInf.java:84`
  - Criterio de pronto: `setZCommand` chama `<ENTIDADE>PZ` por dialeto e retorna hash com `zctl`, `zcode`, `zmsg` quando disponivel.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-16, Implementar lock, triggers e introspeccao de objetos
  - Origem no legado: `PT/Minimal/geral/MCursor.java:1101`, `PT/Minimal/geral/MCursorOra.java:152`, `PT/Minimal/geral/MCursorInf.java:136`
  - Criterio de pronto: cada dialeto cobre `lockTable`, `setTriggersEnabled`, `checkTriggers` e listas de tabelas, indices, triggers, procedures, sequences, synonyms, grants e invalids conforme suporte legado.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-17, Implementar execucao de statements e updates preparados por dialeto
  - Origem no legado: `PT/Minimal/geral/MCursorOra.java:268`, `PT/Minimal/geral/MCursorPg.java:329`, `PT/Minimal/geral/MCursorSql.java:334`, `PT/Minimal/geral/MCursorInf.java:233`
  - Criterio de pronto: `execStatement` e `execPreparedUpdate` retornam contagem de linhas e preservam tratamento de erro/trace SQL.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-18, Preservar efeito colateral de reconexao sobre variaveis Pnuts
  - Origem no legado: `PT/Minimal/geral/MConnection.java:536`
  - Criterio de pronto: apos reconexao, variaveis globais equivalentes a `LOGIN/MC/VARIABLES/FN` sao reaplicadas ou a alternativa moderna e explicitamente definida.
  - Confianca: 🟢 CONFIRMADO

## Tarefas de Teste

- [ ] TT-01, Testar selecao de driver por DSN
  - Origem no legado: `PT/Minimal/geral/MConnection.java:87`
  - Criterio de pronto: cada substring de DSN seleciona driver/adaptador correto; DSN desconhecido falha de forma controlada.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-02, Testar fabrica de cursores por ambiente
  - Origem no legado: `PT/Minimal/geral/MConnection.java:619`
  - Criterio de pronto: Oracle, Informix, SQL Server e PostgreSQL retornam suas subclasses; ambiente desconhecido gera `MIN-983999E`.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-03, Testar ciclo batch feliz
  - Origem no legado: `PT/Minimal/geral/MConnection.java:395`, `PT/Minimal/actions/MInsert.java:191`
  - Criterio de pronto: `beginBatch` desliga autocommit, `commitBatch` commita, religa autocommit e limpa flag.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-04, Testar rollback batch em erro
  - Origem no legado: `PT/Minimal/actions/MDelete.java:191`, `PT/Minimal/actions/MUpdate.java:230`
  - Criterio de pronto: erro durante fluxo de escrita deixa `inBeginBatch` verdadeiro e `rollbackBatch` desfaz a transacao.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-05, Testar traducoes `checkSQL`
  - Origem no legado: `PT/Minimal/geral/MCursorSql.java:23`, `PT/Minimal/geral/MCursorPg.java:24`
  - Criterio de pronto: casos de `nvl`, `today`, `substr`, `[]`, `||`, `(+)`, `from dual` e `MINVARIABLES.` produzem SQL esperado por dialeto.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-06, Testar geracao de `zk`
  - Origem no legado: `PT/Minimal/geral/MCursorOra.java:77`, `PT/Minimal/geral/MCursorInf.java:60`
  - Criterio de pronto: fase `BF` chama o contrato `<ENTIDADE>pk`; fase diferente de `BF` preserva a chave recebida.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-07, Testar `setZCommand`
  - Origem no legado: `PT/Minimal/geral/MCursorOra.java:92`, `PT/Minimal/geral/MCursorPg.java:102`
  - Criterio de pronto: chamada `<ENTIDADE>PZ` recebe comando, fase, args e `zk`; retorno normaliza `zctl`, `zcode`, `zmsg`.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-08, Testar reconexao em erro reconhecido
  - Origem no legado: `PT/Minimal/geral/MCursor.java:527`
  - Criterio de pronto: erro simulado de connection reset executa `reconnect`, reabre conexao e repete query uma vez.
  - Confianca: 🟢 CONFIRMADO

## Tarefas de Migracao de Dados

- [ ] TM-01, Inventariar DSNs e aliases reais
  - Origem no legado: `PT/Minimal/geral/MLogin.java:297`, `PT/Minimal/entities/SDOB0.java:153`
  - Criterio de pronto: todos os DSNs usados em producao estao classificados por banco, credencial, ambiente e finalidade.
  - Confianca: 🔴 LACUNA

- [ ] TM-02, Inventariar procedures `<ENTIDADE>pk` e `<ENTIDADE>PZ`
  - Origem no legado: `PT/Minimal/geral/MCursorOra.java:77`, `PT/Minimal/geral/MCursorOra.java:92`
  - Criterio de pronto: cada entidade com CRUD tem contratos de chave e comandos Z documentados por banco.
  - Confianca: 🔴 LACUNA

- [ ] TM-03, Inventariar objetos de banco usados por introspeccao
  - Origem no legado: `PT/Minimal/geral/MCursorOra.java:188`, `PT/Minimal/geral/MCursorInf.java:167`
  - Criterio de pronto: tabelas, indices, triggers, procedures, sequences, synonyms e grants de cada owner/schema foram exportados.
  - Confianca: 🔴 LACUNA

## Ordem Sugerida

1. Implementar wrapper e cache de conexoes (`T-01` a `T-07`).
2. Implementar transacoes e reconexao (`T-08` a `T-10`), porque CRUD depende disso.
3. Implementar fabrica/base de cursores (`T-11` e `T-12`).
4. Implementar dialect adapters (`T-13` a `T-17`) com testes unitarios por transformacao SQL.
5. Integrar reconexao com variaveis globais (`T-18`) depois que a unit de Pnuts estiver desenhada.
6. Executar `TT-01` a `TT-08` contra mocks e, depois, contra bancos reais quando `TM-01` a `TM-03` forem resolvidas.

## Lacunas Pendentes (🔴)

- JARs e versoes exatas de drivers JDBC externos.
- Substituto moderno para ODBC.
- DSNs reais de producao e credenciais/aliases anonimizados.
- Contratos reais das procedures `<ENTIDADE>pk` e `<ENTIDADE>PZ`.
- Massa de teste multi-banco para validar `checkSQL`, transacoes, locks e introspeccao.

