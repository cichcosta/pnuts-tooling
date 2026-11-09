# Dominio e Regras de Negocio — MinimalJ

## Escopo Interpretado

O MinimalJ nao implementa um unico dominio de negocio vertical. Ele e um framework desktop Java/Swing orientado a metadados para construir, executar e administrar formularios, comandos, regras Pnuts, metadados de banco e rotinas fiscais/administrativas. O dominio principal extraido e, portanto, o metamodelo operacional do framework.

## Glossario

| Termo | Definicao | Evidencia | Confianca |
|-------|-----------|-----------|-----------|
| Entidade (`ent`, `iob`) | Unidade funcional/tabela/tela manipulada pelo framework. Usada para localizar formularios, comandos, autorizacoes e scripts. | `MForm.getEnt()`, `MCheckAut.checkAut(wiob, ...)`, `MPnutsExp.getKey(iob, itc, icomp, ifase)` | 🟢 CONFIRMADO |
| Formulario (`MForm`) | Tela Swing que concentra estado visual, entidade ativa, acao corrente, fase de execucao e linha em edicao. | `PT/Minimal/geral/MForm.java` | 🟢 CONFIRMADO |
| Linha (`MRow`) | Registro de dados persistivel, com metadados de colunas, chaves, valores e hooks Pnuts antes/depois de CRUD. | `PT/Minimal/geral/MRow.java` | 🟢 CONFIRMADO |
| Comando (`MCommand`) | Definicao metadata-driven de uma acao de UI, incluindo classe executora, argumentos, nivel de autorizacao, fase Z e encadeamento. | `MHandleCommand`, `MCommand` | 🟢 CONFIRMADO |
| Pnuts | Motor de scripts usado para regras dinamicas de formulario, linha, campo e comando, sem recompilar Java. | `PT/Minimal/pnuts/MPnuts.java`, `Minimal.pnut` | 🟢 CONFIRMADO |
| Fase (`fase`, `ifase`, `zfase`) | Marcador de momento do fluxo: fases de formulario (`0`, `1`, `2`) e fases de hooks/regra (`BF`, `AF`, `BR`, `AR`, etc.). | `MInsert`, `MUpdate`, `MDelete`, `MRow`, `MPnuts` | 🟢 CONFIRMADO |
| `zk` | Identificador tecnico da linha, gerado/validado por cursores e usado para lock, update, delete e comandos Z. | `MRow.insertRow`, `MRow.updateRow`, `MRow.deleteRow` | 🟢 CONFIRMADO |
| `zcst` | Codigo de estado da linha usado para bloquear edicoes ou marcar ciclo CRUD (`ps`, `us`, `ds`, estados que iniciam por `r`). | `MInsert`, `MUpdate`, `MDelete`, `MinSaltoME` | 🟢 CONFIRMADO |
| `zprc` | Campo de processamento usado em triggers/procedures geradas para controlar execucao de regras de banco. | `MGerDDLOra`, `MGerDDLSql`, `MInsert` | 🟢 CONFIRMADO |
| `suact0` | Tabela de conta/sessao de usuario usada no login para obter menu, grupo, organizacao, nivel de autorizacao, tipo e idioma. | `MLogin.doDefaultConnection` | 🟢 CONFIRMADO |
| `ssact0` | Tabela de autorizacoes gerais e especiais carregada por entidade. | `MCheckAut.loadAut` | 🟢 CONFIRMADO |
| IES | Integracao fiscal portuguesa para Informacao Empresarial Simplificada via JAXB. | `PT/Minimal/util/Mies.java` | 🟢 CONFIRMADO |

## Regras Principais

### R01 — O usuario `minimal` ignora autorizacao normal

`MCheckAut.checkAut` retorna `true` imediatamente quando `MLogin.getUser()` e `"minimal"`. Quando o login normal falha para outro usuario, `MLogin` lanca erro; para `minimal`, assume nivel de autorizacao `"9"`.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/geral/MCheckAut.java`, `PT/Minimal/geral/MLogin.java`

### R02 — Todo comando metadata-driven passa por autorizacao antes de reflexao

`MHandleCommand` resolve o comando pelo nome e pela entidade do formulario, chama `MCheckAut.checkAut(ent, comando, nivel)`, e so depois instancia a classe com `Class.forName(...).newInstance()`.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/actions/MHandleCommand.java`

### R03 — Usuarios cujo tipo contem `CS` nao podem executar comandos de escrita

`MInsert`, `MUpdate`, `MDelete`, `MCallPnuts` e `MStatusChanger` bloqueiam execucao quando `MLogin.getUserTipo().indexOf("CS") != -1`.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/actions/MInsert.java`, `MUpdate.java`, `MDelete.java`, `MStatusChanger.java`, `MCallPnuts.java`

### R04 — O login popula identidade, grupo, organizacao, menu, nivel, tipo e idioma a partir de `suact0`

Depois de abrir a conexao default, o sistema consulta `suact0` para o usuario do banco (`lower(USER)`) valido entre `dini` e `dfim`. Os campos `imen`, `iorg`, `igrp`, `cns`, `cut` e `ilan` alteram o comportamento global da sessao.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/geral/MLogin.java`

### R05 — Autorizacao combina nivel numerico/textual e listas allow/deny

A autorizacao compara o nivel requerido (`wcns`) com `MLogin.getUserAutLevel()`. Tambem avalia linhas de autorizacao geral (`isact = 'AG'`) e especial (`isact = 'AE'`) da tabela `ssact0`. O codigo `DN` inverte o resultado, funcionando como negativa explicita.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/geral/MCheckAut.java`

### R06 — CRUD tem dois niveis de hooks: formulario e linha

Operacoes de insert/update/delete executam validacao/expansao de formulario e hooks Pnuts de formulario (`AF`, `CN`) no comando. Em seguida, `MRow` executa hooks de linha antes/depois (`BR`, `AR`) da operacao SQL.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/actions/MInsert.java`, `MUpdate.java`, `MDelete.java`, `PT/Minimal/geral/MRow.java`

### R07 — Atualizacao e exclusao usam controle otimista por data/hora

Quando `checkChanges` esta ativo, `MRow.updateRow` e `MRow.deleteRow` adicionam `zdst` e `zhst` ao `where`. Se nenhuma linha for alterada, lanca `MIN-000102E`; se mais de uma linha for afetada, lanca `MIN-000284E`.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/geral/MRow.java`

### R08 — Exclusao bloqueia linhas sem chave real ou marcadas como reservadas

`MDelete.do_delete_row` rejeita linhas com `zk == "0"` ou `zk` iniciado por `"*"`. O fluxo de preparacao tambem bloqueia linhas cujo `zcst` comeca por `r`.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/actions/MDelete.java`

### R09 — Transacoes de escrita devem abrir batch e fazer rollback em finally se nao houve commit

Comandos de escrita usam `beginBatch`, `commitBatch` e, em `finally`, verificam `inBeginBatch()` para chamar `rollbackBatch`. Esse padrao aparece em insert, update, delete e mudanca de status.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/actions/MInsert.java`, `MUpdate.java`, `MDelete.java`, `MStatusChanger.java`

### R10 — O sistema gera DDL e stored procedures por heuristicas de sufixo

Geradores DDL criam tabelas, indices, triggers e procedures com nomes derivados de entidades e sufixos (`t0`, `tz`, `p0`, `pz`, `ri`, `rz`, etc.). As procedures recebem comandos/fases (`zcmd`, `zfase`) e campos de controle.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/actions/MGerDDLOra.java`, `MGerDDLSql.java`, `MGerDDLPos.java`, `MGerDDLInf.java`

### R11 — Regras de validacao de formulario sao declarativas e reutilizaveis via Pnuts

`Minimal.pnut` define validacoes como "exatamente um preenchido", "pelo menos um", "todos ou nenhum" e "todos vazios", emitindo erros MIN padronizados.

Confianca: 🟢 CONFIRMADO  
Evidencias: `PT/Minimal/pnuts/Minimal.pnut`

### R12 — A integracao fiscal IES existe no codigo, mas nao e feature obrigatoria do sistema novo

`Mies.java` usa JAXB e classes `pt.gov.dgci._2007.ies_da`, indicando suporte a declaracoes IES portuguesas. Nao ha evidencia suficiente de telas/fluxos completos nesta etapa para afirmar o processo fiscal ponta a ponta.

Em validacao humana de 2026-05-22, o usuario respondeu que a integracao fiscal IES/DGCI nao deve ser tratada como feature obrigatoria do sistema novo. A capacidade fica documentada como artefato legado existente, nao como contrato SDD a reconstruir nesta rodada.

Confianca: 🟢 CONFIRMADO para existencia no codigo e para decisao de nao obrigatoriedade no sistema novo  
Evidencias: `PT/Minimal/util/Mies.java`; validacao humana 2026-05-22

## Lacunas

| Lacuna | Impacto | Confianca |
|--------|---------|-----------|
| Dicionario completo das tabelas de metadados (`suact0`, `ssact0`, `mjext0`, `mcfxt0`, etc.) nao esta disponivel como DDL formal. | Limita a precisao de permissoes, menus, comandos e scripts cadastrados em producao. | 🔴 LACUNA |
| Valores completos de `zcst`, `cut`, `cns`, `i_tc`, `i_fase` e codigos MIN nao estao centralizados no codigo. | Exige extracao de banco/metadados para fechar regras e mensagens. | 🔴 LACUNA |
| Historico Git ausente no workspace. | ADRs retroativos por commit nao puderam ser gerados no nivel essencial e nao ha fonte historica local. | 🔴 LACUNA |
| Drivers e dependencias externas nao estao versionados no repositorio. | Dificulta reproducao de runtime, especialmente Pnuts, JDBC legado e bibliotecas Swing de terceiros. | 🔴 LACUNA |
