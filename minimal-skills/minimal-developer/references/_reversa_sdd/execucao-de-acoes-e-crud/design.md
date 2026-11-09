# Execucao de Acoes e CRUD, Design Tecnico

## Interface

### Roteador de Comandos

| Simbolo | Assinatura | Retorno | Observacao |
|---------|------------|---------|------------|
| `MHandleCommand.MHandleCommand` | `(MFrame, MDialog, MForm, Window, Component, String)` | instancia | Delega para variante com `row_vector`. 🟢 |
| `MHandleCommand.MHandleCommand` | `(MFrame, MDialog, MForm, Window, Component, String, Vector)` | instancia | Processa comando, resolve `MCommand`, autoriza e executa. 🟢 |
| `MForm.getCmd` | `(entidade, comando)` | `MCommand` | Fonte metadata-driven do executor. 🟢 |
| `MCheckAut.checkAut` | `(entidade, comando, nivel)` | `boolean` | Gate antes de reflexao. 🟢 |

### Contrato de Executor

| Simbolo | Assinatura | Retorno | Observacao |
|---------|------------|---------|------------|
| `MExternalCommand.setMCommand` | `(MCommand)` | `void` | Injeta metadata do comando. 🟢 |
| `MExternalCommand.setMForm` | `(MForm)` | `void` | Injeta formulario ativo. 🟢 |
| `MExternalCommand.setRows` | `(Vector)` | `void` | Injeta linhas selecionadas. 🟢 |
| `MExternalCommand.execute` | `()` | `void` | Executa comportamento do comando. 🟢 |

### Executores CRUD

| Classe | Comandos centrais | Entrada | Saida | Confianca |
|--------|-------------------|---------|-------|-----------|
| `MInsert` | `INSERT_ROW`, `INSERT_ROW_TEXT` | `MForm`, `MCommand`, rows | Linha criada, browse atualizado, fase `2` | 🟢 |
| `MUpdate` | `UPDATE_ROW` | `MForm`, `MCommand`, rows | Linha alterada, browse atualizado, fase `2` | 🟢 |
| `MDelete` | `DELETE_ROW` | `MForm`, `MCommand`, rows | Linha apagada, browse atualizado/proxima linha | 🟢 |
| `MStatusChanger` | comando metadata-driven | rows + args do comando | Status/data/codigos atualizados em lote | 🟢 |

### Persistencia de Linha

| Simbolo | Assinatura | Retorno | Observacao |
|---------|------------|---------|------------|
| `MRow.insertRow` | `(MCursor, createZK?)` | `void` | Gera `zk`, executa hooks `BR`/`AR`, insere SQL. 🟢 |
| `MRow.updateRow` | `(MCursor, MRow, checkChanges?, updZk?)` | `void` | Lock opcional, update com `zk`/keys e controle `zdst`/`zhst`. 🟢 |
| `MRow.deleteRow` | `(MCursor, checkChanges?)` | `void` | Lock opcional, delete com keys e controle `zdst`/`zhst`. 🟢 |
| `MRow.setZCommand` | `(comando, fase, args, MCursor)` | `Hashtable` | Delega para cursor chamar procedure Z. 🟢 |

## Fluxo Principal

1. Evento UI chega como string `minCmd` em `MHandleCommand`. 🟢 `PT/Minimal/actions/MHandleCommand.java:33`
2. `OKDIALOG` e `CLOSEWINDOW` sao normalizados para acao/formulario atual ou fechamento. 🟢 `PT/Minimal/actions/MHandleCommand.java:37`
3. `MUtil.getArgs(minCmd.toUpperCase(), ',')` extrai objeto/comando e argumentos. 🟢 `PT/Minimal/actions/MHandleCommand.java:58`
4. `MForm.getCmd(minForm.getEnt(), wobj_args[0])` resolve `MCommand`. 🟢 `PT/Minimal/actions/MHandleCommand.java:65`
5. Para comandos `MJ`, `MCheckAut.checkAut` valida permissao por entidade/comando/nivel. 🟢 `PT/Minimal/actions/MHandleCommand.java:74`
6. Classe executora em `wcmd.getCmd()` e instanciada por `Class.forName(...).newInstance()`. 🟢 `PT/Minimal/actions/MHandleCommand.java:89`
7. Se objeto implementa `MExternalCommand`, recebe `MCommand`, `MForm` e linhas selecionadas do browse. 🟢 `PT/Minimal/actions/MHandleCommand.java:91`
8. `execute()` do comando roda fluxo especifico de CRUD/status. 🟢 `PT/Minimal/actions/MHandleCommand.java:108`

## Fluxo de Insert

1. `MInsert.execute()` bloqueia usuario tipo `CS` e carrega pacote Pnuts da entidade. 🟢 `PT/Minimal/actions/MInsert.java:41`
2. Fase `0`: `show_insert_row()` cria/reusa formulario `ENTME`, configura toolbar, botoes, menu, acao e fase `1`. 🟢 `PT/Minimal/actions/MInsert.java:98`
3. `prepareNext()` carrega proxima linha/base e executa hook de formulario `BF`. 🟢 `PT/Minimal/actions/MInsert.java`
4. Fase `1`: `do_insert_row()` abre `MCursor`, inicia batch, valida e expande form com modo `Force`. 🟢 `PT/Minimal/actions/MInsert.java:188`
5. Hook de formulario `AF` roda antes da persistencia. 🟢 `PT/Minimal/actions/MInsert.java:205`
6. Nova `MRow` recebe valores de formulario e campos `zcri`, `zgrp`, `zhst`, `zdst`, `zcst = ps`, `zprc = 1` se existir. 🟢 `PT/Minimal/actions/MInsert.java:207`
7. `MRow.insertRow(wupdate)` executa `BR`, gera `zk`, insere via SQL direto/prepared, extensoes e `AR`. 🟢 `PT/Minimal/geral/MRow.java:395`
8. Se `ZFase` inicia `AF` ou `BT`, executa `setZCommand(..., "AF", ...)`. 🟢 `PT/Minimal/actions/MInsert.java:217`
9. Linha e refrescada, browse recebe/atualiza row, form volta para formato de consulta e batch commita. 🟢 `PT/Minimal/actions/MInsert.java:223`
10. `finally` garante rollback se batch ainda aberto e fecha cursor. 🟢 `PT/Minimal/actions/MInsert.java:237`

## Fluxo de Update

1. `MUpdate.execute()` bloqueia `CS`, carrega Pnuts e garante linhas selecionadas. 🟢 `PT/Minimal/actions/MUpdate.java:40`
2. Fase `0`: `show_update_row()` cria/reusa formulario `ENTME`, configura acao e fase `1`. 🟢 `PT/Minimal/actions/MUpdate.java:101`
3. `prepareNext()` ignora linhas invalidas, bloqueia `zcst` iniciando por `r`, expande form `NoForce`, executa `BF`. 🟢 `PT/Minimal/actions/MUpdate.java:248`
4. Fase `1`: `do_update_row()` inicia batch, valida/expande `Force`, executa `AF`. 🟢 `PT/Minimal/actions/MUpdate.java:196`
5. Copia linha atual, aplica valores do formulario, define `zcri`, `zgrp`, `zcst = us`. 🟢 `PT/Minimal/actions/MUpdate.java:210`
6. `MRow.updateRow(wupdate, wr)` executa `BR`, lock se transacao ativa, update por keys/`zk`, controle `zdst`/`zhst`, extensoes e `AR`. 🟢 `PT/Minimal/geral/MRow.java:544`
7. Se `ZFase` inicia `AF` ou `BT`, executa comando Z. 🟢 `PT/Minimal/actions/MUpdate.java:216`
8. Linha e browse refrescam, form recebe valores expandidos `NoForce`, batch commita. 🟢 `PT/Minimal/actions/MUpdate.java:219`
9. `finally` aplica rollback se necessario e fecha cursor. 🟢 `PT/Minimal/actions/MUpdate.java:228`

## Fluxo de Delete

1. `MDelete.execute()` bloqueia `CS`, carrega Pnuts e trata cancelamento. 🟢 `PT/Minimal/actions/MDelete.java:35`
2. Fase `0`: se rows vazio, erro `MIN-000103W`; senao abre formulario de confirmacao/manutencao. 🟢 `PT/Minimal/actions/MDelete.java:61`
3. `prepareNext()` ignora rows invalidas, bloqueia `zcst` iniciando por `r`, executa `BF` e mostra form. 🟢 `PT/Minimal/actions/MDelete.java:198`
4. Fase `1`: `do_delete_row()` inicia batch, executa hook form `AF`, valida `zk != 0` e `zk` nao iniciado por `*`. 🟢 `PT/Minimal/actions/MDelete.java:151`
5. Se `ZFase` inicia `BF` ou `BT`, executa comando Z em fase `BF` e refresca linha. 🟢 `PT/Minimal/actions/MDelete.java:169`
6. `MRow.deleteRow(wupdate)` executa `BR`, lock se transacao ativa, delete com keys e controle `zdst`/`zhst`, depois `AR`. 🟢 `PT/Minimal/geral/MRow.java:873`
7. Browse refresca, indice avanca, batch commita, `zk` do form vira `"0"`. 🟢 `PT/Minimal/actions/MDelete.java:184`
8. `finally` aplica rollback se necessario e fecha cursor. 🟢 `PT/Minimal/actions/MDelete.java:190`

## Fluxo de Status em Lote

1. `MStatusChanger` carrega nome, codigos OK/falha, proximo comando, args e `ZFase` de `MCommand`. 🟢 `PT/Minimal/actions/MStatusChanger.java:27`
2. `execute()` bloqueia `CS`, exige pelo menos 6 argumentos e rows nao vazio. 🟢 `PT/Minimal/actions/MStatusChanger.java:59`
3. Pnuts recebe cursor, args, form e rows; hook `BC` roda antes do loop. 🟢 `PT/Minimal/actions/MStatusChanger.java:77`
4. Para cada row valida, cria copia, valida transicao por grupos de 3 argumentos, aplica novo estado/campo/data. 🟢 `PT/Minimal/actions/MStatusChanger.java:88`
5. Batch por linha executa hooks `BA` e `AA`, `updateRow`, comando Z opcional, commit ou rollback. 🟢 `PT/Minimal/actions/MStatusChanger.java:123`
6. Linha antiga refresca no `finally`; hook `AC` roda apos loop. 🟢 `PT/Minimal/actions/MStatusChanger.java:161`
7. `wNextCmd` dispara novo `MHandleCommand` se configurado. 🟢 `PT/Minimal/actions/MStatusChanger.java:170`

## Dependencias

- `MForm`: entidade ativa, fase, acao, browse, valores de formulario, botoes e formato visual. 🟢
- `MCommand`: tipo, classe executora, nivel, argumentos, `ZFase`, proximo comando, textos/codigos. 🟢
- `MCheckAut`: autorizacao antes de reflexao. 🟢
- `MLogin`: usuario, grupo/nodo, tipo de usuario. 🟢
- `MConnection`/`MCursor`: batch, SQL, lock, comando Z. 🟢
- `MRow`: persistencia de linha, estado `wcst`, hooks de linha, extensoes. 🟢
- `MPnuts`: hooks de formulario, linha e comando. 🟢
- `MUtil`: parse de args, validacao, expansao, move record, linguagem e foco. 🟢
- Metadados de comandos e scripts por entidade. 🔴 para conteudo real completo.

## Decisoes de Design Identificadas

| Decisao | Evidencia no codigo | Confianca |
|---------|---------------------|-----------|
| Comando UI vira string e e resolvido em metadados por entidade. | `PT/Minimal/actions/MHandleCommand.java:58` | 🟢 CONFIRMADO |
| Autorizacao ocorre antes da instanciacao refletida. | `PT/Minimal/actions/MHandleCommand.java:74` | 🟢 CONFIRMADO |
| Executores compartilham interface `MExternalCommand`. | `PT/Minimal/actions/MExternalCommand.java` | 🟢 CONFIRMADO |
| Fases `0/1/2` controlam abertura/gravação/proximo item. | `PT/Minimal/actions/MInsert.java:98`, `MUpdate.java:101`, `MDelete.java:61` | 🟢 CONFIRMADO |
| Hooks Pnuts existem em camada de formulario, linha e comando. | `PT/Minimal/actions/MInsert.java:205`, `PT/Minimal/geral/MRow.java:369`, `MStatusChanger.java:81` | 🟢 CONFIRMADO |
| Escritas usam batch e rollback defensivo em `finally`. | `PT/Minimal/actions/MInsert.java:191`, `MUpdate.java:199`, `MDelete.java:156` | 🟢 CONFIRMADO |
| `MRow` escolhe SQL direto ou prepared conforme ambiente/tipo de campo. | `PT/Minimal/geral/MRow.java:430`, `:572`, `:895` | 🟢 CONFIRMADO |
| Controle otimista usa `zdst` e `zhst`. | `PT/Minimal/geral/MRow.java:652`, `:930` | 🟢 CONFIRMADO |

## Estado Interno

| Estado | Dono | Evolucao | Confianca |
|--------|------|----------|-----------|
| `minForm.fase` | `MForm` | `0` consulta, `1` edicao/confirmacao, `2` pos-gravacao/proximo item. | 🟢 |
| `minForm.action` | `MForm` | Definido por comando atual e reutilizado em `OKDIALOG`. | 🟢 |
| `actionObject` | `MForm` | Vetor de rows selecionadas para operacoes multi-row. | 🟢 |
| `actionObjectIndex` | `MForm` | Indice da proxima row a processar. | 🟢 |
| `wcst` | `MRow` | Estado local da row: `ps`, `us`, `ds`, `st` etc. | 🟢 |
| `zcst` | Record | Estado persistido/operacional usado por insert/update/delete/status. | 🟢 |
| `zk` | Record | Chave tecnica para update/delete/refresh/comando Z. | 🟢 |
| `zdst`/`zhst` | Record | Controle otimista de data/hora. | 🟢 |
| `wrows` | Executores | Rows selecionadas ou copiadas do browse/form. | 🟢 |

## Observabilidade

- `MHandleCommand` imprime comando, entidade, tipo de comando, classe e tamanho de rows. 🟢
- CRUD imprime fases, SQL em `MRow`, `zk`, commits e erros por stack/MError. 🟢
- `MStatusChanger` imprime timestamps por etapa (`beginBatch`, `evalPnut`, update, `setZCommand`, commit). 🟢
- `MConsole.getTraceSql()` aumenta detalhe em SQL/prepared params. 🟢
- Nao ha log estruturado, correlation id, metricas ou auditoria centralizada da execucao de comandos. 🔴 **LACUNA**

## Riscos e Lacunas

- 🔴 Sem dump de metadados, comandos reais, classes, niveis, args e `ZFase` ficam incompletos.
- 🔴 Sem scripts Pnuts por entidade, hooks podem alterar comportamento nao visivel no codigo Java.
- 🔴 Sem schema, `zcri`, `zgrp`, `zcst`, `zprc`, `zdst`, `zhst`, `zk` nao tem tipo/constraint confirmados.
- 🟡 Reflexao por nome de classe falha em runtime se metadados e classpath divergirem.
- 🟡 SQL direto em modo `N` aumenta risco de escaping e divergencia por dialeto.
- 🟢 Rollback defensivo reduz risco de transacao aberta em erro, mas depende de `inBeginBatch()` correto.

