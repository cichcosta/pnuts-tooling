# Execucao de Acoes e CRUD

## Visao Geral

A unit de execucao de acoes e CRUD transforma eventos de interface em comandos executaveis, valida autorizacao, instancia classes por reflexao e executa operacoes de insert, update, delete e mudanca de status com transacao, hooks Pnuts, atualizacao de linha e refresh visual. 🟢 **CONFIRMADO**

O comportamento e metadata-driven: `MHandleCommand` localiza `MCommand` a partir da entidade/formulario e do nome do comando, enquanto as classes `MInsert`, `MUpdate`, `MDelete`, `MStatusChanger` e outras implementam `MExternalCommand`. 🟢 **CONFIRMADO**

## Responsabilidades

- Resolver comandos de UI por entidade e nome usando `MForm.getCmd`. 🟢 **CONFIRMADO**
- Validar autorizacao do comando antes de instanciar a classe executora. 🟢 **CONFIRMADO**
- Instanciar executores via `Class.forName(...).newInstance()`. 🟢 **CONFIRMADO**
- Injetar `MCommand`, `MForm` e linhas selecionadas no executor. 🟢 **CONFIRMADO**
- Controlar fases de formulario (`0`, `1`, `2`) para abrir tela, gravar e preparar proxima linha. 🟢 **CONFIRMADO**
- Bloquear operacoes de escrita para usuarios cujo tipo contem `CS`. 🟢 **CONFIRMADO**
- Executar validacao/expansao de formulario e hooks Pnuts antes/depois da gravacao. 🟢 **CONFIRMADO**
- Persistir insert/update/delete por `MRow` e `MCursor` dentro de batch transacional. 🟢 **CONFIRMADO**
- Executar comandos Z (`setZCommand`) quando `ZFase` exige `BF`, `AF` ou `BT`. 🟢 **CONFIRMADO**
- Atualizar browse/formulario apos gravacao e mover para o proximo item em operacoes multi-row. 🟢 **CONFIRMADO**

## Regras de Negocio

- Todo comando metadata-driven do tipo `MJ` deve passar por `MCheckAut.checkAut(entidade, comando, nivel)` antes de reflexao. 🟢 **CONFIRMADO**
- Se a autorizacao falhar, o sistema deve lançar erro `MIN-009998E`. 🟢 **CONFIRMADO**
- Apenas objetos que implementam `MExternalCommand` recebem `setMCommand`, `setMForm`, `setRows` e `execute`. 🟢 **CONFIRMADO**
- Usuarios cujo tipo (`MLogin.getUserTipo()`) contem `CS` nao podem executar `MInsert`, `MUpdate`, `MDelete`, `MStatusChanger` ou comandos de escrita equivalentes. 🟢 **CONFIRMADO**
- `INSERT_ROW` em fase `0` abre formulario de manutencao; em fase `1` grava; em fase `2` prepara a proxima linha. 🟢 **CONFIRMADO**
- `UPDATE_ROW` em fase `0` abre formulario de manutencao; em fase `1` grava; em fase `2` prepara a proxima linha. 🟢 **CONFIRMADO**
- `DELETE_ROW` em fase `0` abre confirmacao/manutencao; em fase `1` exclui. 🟢 **CONFIRMADO**
- Insert e update devem validar formulario com `MUtil.validate(..., "Force")`, expandir valores com `MUtil.expand(..., "Force")` e executar hook Pnuts `AF` antes de persistir. 🟢 **CONFIRMADO**
- `MRow.insertRow`, `updateRow` e `deleteRow` executam hooks de linha `BR` e `AR` ao redor do SQL. 🟢 **CONFIRMADO**
- Insert deve preencher auditoria/estado com `zcri`, `zgrp`, `zhst`, `zdst`, `zcst = ps` e `zprc = 1` quando existir. 🟢 **CONFIRMADO**
- Update deve preencher `zcri`, `zgrp` e `zcst = us`. 🟢 **CONFIRMADO**
- Delete deve rejeitar linhas com `zk == "0"` ou `zk` iniciado por `"*"`. 🟢 **CONFIRMADO**
- Delete deve bloquear linha cujo `zcst` inicia com `r` durante `prepareNext`. 🟢 **CONFIRMADO**
- Update e delete devem aplicar controle otimista por `zdst` e `zhst` quando `checkChanges` estiver ativo. 🟢 **CONFIRMADO**
- Qualquer batch ainda aberto no `finally` deve sofrer rollback. 🟢 **CONFIRMADO**
- A lista completa de comandos, fases Z e argumentos reais depende dos metadados de producao; o usuario nao sabe se existe export disponivel nesta etapa. 🔴 **LACUNA**

## Requisitos Funcionais

| ID | Requisito | Prioridade | Criterio de Aceite |
|----|-----------|------------|--------------------|
| RF-01 | Resolver comando a partir da entidade e string de comando. | Must | Dado `minCmd`, quando `MHandleCommand` executa, entao extrai argumentos, busca `MForm.getCmd(entidade, comando)` e obtem `MCommand`. |
| RF-02 | Validar autorizacao antes de instanciar comando `MJ`. | Must | Dado comando `MJ`, quando `checkAut` retorna falso, entao `MIN-009998E` e lancado e `Class.forName` nao e executado. |
| RF-03 | Instanciar e executar `MExternalCommand`. | Must | Dado classe configurada em `MCommand.getCmd`, quando ela implementa `MExternalCommand`, entao recebe command/form/rows e `execute()` e chamado. |
| RF-04 | Bloquear usuario tipo `CS` em escrita. | Must | Dado `MLogin.getUserTipo()` contendo `CS`, quando insert/update/delete/status executa, entao erro `MIN-009998E` e emitido. |
| RF-05 | Implementar ciclo de insert por fases. | Must | Dado `INSERT_ROW`, fase `0` abre manutencao, fase `1` grava em batch, fase `2` prepara proximo registro. |
| RF-06 | Implementar ciclo de update por fases. | Must | Dado `UPDATE_ROW`, fase `0` abre manutencao, fase `1` grava em batch, fase `2` prepara proximo registro. |
| RF-07 | Implementar ciclo de delete por fases. | Must | Dado `DELETE_ROW`, fase `0` abre manutencao/confirmacao e fase `1` executa exclusao em batch. |
| RF-08 | Executar hooks Pnuts de formulario e linha. | Must | Quando uma escrita ocorre, entao hooks de formulario (`AF`, `CN` quando cancelado) e hooks de linha (`BR`, `AR`) sao executados nos momentos equivalentes. |
| RF-09 | Executar comandos Z conforme `ZFase`. | Should | Dado `ZFase` iniciando por `BF`, `AF` ou `BT`, quando o comando grava, entao `setZCommand` e chamado na fase correta. |
| RF-10 | Atualizar UI/browse apos persistencia. | Should | Depois de insert/update/delete, o browse e formulario refletem a linha gravada, atualizada ou excluida. |
| RF-11 | Implementar mudanca de status em lote. | Should | Dado `MStatusChanger` com argumentos validos, quando linhas validas sao selecionadas, entao status/data/codigos sao atualizados em batch por linha. |
| RF-12 | Suportar operacoes multi-row. | Should | Dado vetor de linhas selecionadas, quando insert/update/delete/status executa, entao percorre linhas validas e avanca `actionObjectIndex`. |

## Requisitos Nao Funcionais

| Tipo | Requisito inferido | Evidencia no codigo | Confianca |
|------|--------------------|---------------------|-----------|
| Seguranca | Autorizacao deve ocorrer antes da reflexao do comando. | `PT/Minimal/actions/MHandleCommand.java:74`, `:89` | 🟢 CONFIRMADO |
| Seguranca | Usuarios `CS` devem ser read-only para escrita. | `PT/Minimal/actions/MInsert.java:43`, `MUpdate.java:42`, `MDelete.java:37` | 🟢 CONFIRMADO |
| Consistencia | Escritas devem usar batch com rollback em `finally`. | `PT/Minimal/actions/MInsert.java:191`, `:239`; `MUpdate.java:199`, `:231`; `MDelete.java:156`, `:192` | 🟢 CONFIRMADO |
| Consistencia | Update/delete devem usar `zk` e controle otimista `zdst`/`zhst` quando ativo. | `PT/Minimal/geral/MRow.java:629`, `:652`, `:930` | 🟢 CONFIRMADO |
| Extensibilidade | Novos comandos podem ser adicionados por metadados desde que implementem `MExternalCommand`. | `PT/Minimal/actions/MExternalCommand.java` | 🟢 CONFIRMADO |
| Observabilidade | Fluxos emitem logs textuais no console, mas sem tracing estruturado. | `PT/Minimal/actions/MHandleCommand.java:65`, `MStatusChanger.java:123` | 🟢 CONFIRMADO |

## Criterios de Aceitacao

```gherkin
Cenario: Comando autorizado e executado por reflexao
Dado um formulario com entidade ativa
E um MCommand do tipo MJ cadastrado para o comando recebido
E checkAut retorna permitido
Quando MHandleCommand processa o comando
Entao a classe configurada em MCommand.getCmd e instanciada
E recebe MCommand, MForm e linhas selecionadas
E execute e chamado

Cenario: Comando sem autorizacao
Dado um MCommand do tipo MJ
E checkAut retorna falso
Quando MHandleCommand processa o comando
Entao o sistema lanca MIN-009998E
E nao instancia a classe executora

Cenario: Insert de linha
Dado formulario em fase 1 para INSERT_ROW
Quando o usuario confirma a gravacao
Entao o sistema valida e expande o formulario
E executa Pnuts AF
E preenche zcri, zgrp e zcst ps
E executa MRow.insertRow em batch
E commita a transacao

Cenario: Update com controle otimista
Dado uma linha existente com zk, zdst e zhst
Quando o usuario confirma UPDATE_ROW
Entao MRow.updateRow inclui zk no where
E inclui zdst e zhst quando checkChanges esta ativo
E falha se nenhuma linha for alterada

Cenario: Delete de linha reservada
Dado uma linha com zk igual a 0 ou iniciado por *
Quando DELETE_ROW e confirmado
Entao o sistema lanca MIN-000103E
E nao executa delete fisico/logico

Cenario: Rollback automatico em erro de escrita
Dado uma operacao de escrita com batch aberto
Quando ocorre erro antes de commitBatch
Entao o finally chama rollbackBatch
E a conexao e fechada
```

## Prioridade (MoSCoW)

| Requisito | MoSCoW | Justificativa |
|-----------|--------|---------------|
| Resolver e autorizar comandos | Must | Todo comportamento de UI passa pelo roteador central. 🟢 |
| Executar CRUD com transacao | Must | E o fluxo operacional central do framework. 🟢 |
| Hooks Pnuts | Must | Regras de negocio dinamicas dependem deles. 🟢 |
| Controle otimista por `zdst`/`zhst` | Must | Evita sobrescrita/exclusao concorrente silenciosa. 🟢 |
| Mudanca de status em lote | Should | Relevante para fluxos operacionais, mas separado do CRUD basico. 🟢 |
| Refresh de browse/UI | Should | Necessario para experiencia desktop fiel, mas nao altera persistencia. 🟢 |

## Rastreabilidade de Codigo

| Arquivo | Funcao / Classe | Cobertura |
|---------|-----------------|-----------|
| `PT/Minimal/actions/MHandleCommand.java` | roteamento, autorizacao e reflexao de comandos | 🟢 |
| `PT/Minimal/actions/MExternalCommand.java` | contrato dos executores | 🟢 |
| `PT/Minimal/actions/MInsert.java` | fluxo `INSERT_ROW` | 🟢 |
| `PT/Minimal/actions/MUpdate.java` | fluxo `UPDATE_ROW` | 🟢 |
| `PT/Minimal/actions/MDelete.java` | fluxo `DELETE_ROW` | 🟢 |
| `PT/Minimal/actions/MStatusChanger.java` | mudanca de status em lote | 🟢 |
| `PT/Minimal/geral/MRow.java` | persistencia de linha, hooks BR/AR, `zcst`, `zk`, controle otimista | 🟢 |
| `_reversa_sdd/domain.md` | regras R02, R03, R06, R07, R08, R09 | 🟢 |
| `_reversa_sdd/state-machines.md` | fases de formulario, ciclo `zcst`, hooks Pnuts | 🟢 |
| `_reversa_sdd/permissions.md` | bloqueios de autorizacao e tipo `CS` | 🟢 |

## Lacunas Pendentes

| Lacuna | Impacto | Confianca |
|--------|---------|-----------|
| Metadados reais de comandos (`MCommand`) nao foram exportados. | Nao e possivel listar todos os comandos, niveis, classes, argumentos, `ZFase`, proximo comando e mensagens por entidade. | 🔴 LACUNA |
| Scripts Pnuts por entidade em producao nao estao completos no workspace. | Regras `AF`, `BF`, `BR`, `AR`, `BC`, `BA`, `AA`, `AC` podem estar incompletas. | 🔴 LACUNA |
| Catalogo completo de estados `zcst` nao esta centralizado. | Apenas `ps`, `us`, `ds`, prefixo `r` e `st` foram confirmados no codigo analisado. | 🔴 LACUNA |
| Sem banco/metadados, nao e possivel validar todas as procedures de comandos Z. | `setZCommand` depende de contratos `<ENTIDADE>PZ` por dialeto. | 🔴 LACUNA |

Nota de validacao humana: em 2026-05-22, o usuario informou nao saber se existe export de comandos por entidade. A reimplementacao pode preservar o motor de comandos, mas a lista operacional completa permanece bloqueada ate obtencao de metadados `MCommand`/`ZFase`/procedures.
