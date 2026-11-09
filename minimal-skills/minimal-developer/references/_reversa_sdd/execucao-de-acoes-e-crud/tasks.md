# Execucao de Acoes e CRUD, Tasks

## Escopo

Reconstruir o motor de comandos metadata-driven e os fluxos de insert, update, delete e alteracao de status em lote, mantendo equivalencia operacional com o legado.

## Pre-requisitos

- [ ] Exportar metadados de comandos por entidade: nome, tipo, classe executora, nivel, argumentos, `ZFase` e proximo comando. 🔴
- [ ] Exportar scripts Pnuts por entidade e por hook (`BF`, `AF`, `BR`, `AR`, `BC`, `BA`, `AA`, `AC`). 🔴
- [ ] Obter DDL das tabelas operacionais com colunas `zk`, `zcst`, `zcri`, `zgrp`, `zprc`, `zdst` e `zhst`. 🔴
- [ ] Definir adaptador transacional equivalente a `MCursor.beginBatch`, `commitBatch`, `rollbackBatch` e `inBeginBatch`. 🟢
- [ ] Mapear catalogo de mensagens `MIN-*` usadas em falhas de comando e CRUD. 🟡

## Implementacao

- [ ] `CRUD-01` Implementar contrato de executor externo equivalente a `MExternalCommand`.
  - Aceitar metadados do comando, formulario/contexto ativo e linhas selecionadas.
  - Expor metodo unico de execucao.

- [ ] `CRUD-02` Implementar roteador de comandos equivalente a `MHandleCommand`.
  - Normalizar `OKDIALOG` para acao atual.
  - Tratar `CLOSEWINDOW` como fechamento da janela/dialogo ativo.
  - Parsear comando e argumentos por virgula.

- [ ] `CRUD-03` Implementar resolucao metadata-driven de comando.
  - Buscar comando por entidade ativa e nome solicitado.
  - Falhar com mensagem operacional quando comando nao existir.
  - Preservar argumentos brutos para o executor.

- [ ] `CRUD-04` Implementar autorizacao antes de reflexao/instanciacao.
  - Validar entidade, comando e nivel.
  - Bloquear execucao antes de carregar classe ou handler.
  - Registrar falha sem executar efeitos colaterais.

- [ ] `CRUD-05` Implementar carregamento dinamico de executor.
  - Resolver classe/handler a partir dos metadados.
  - Injetar comando, formulario e rows.
  - Validar que o handler implementa o contrato esperado.

- [ ] `CRUD-06` Implementar maquina de fases CRUD.
  - Fase `0`: abrir/preparar formulario de acao.
  - Fase `1`: executar gravacao/confirmacao.
  - Fase `2`: preparar proximo item ou encerrar ciclo.

- [ ] `CRUD-07` Implementar fluxo de insert.
  - Bloquear usuario tipo `CS`.
  - Criar/reusar formulario `ENTME`.
  - Executar `BF` antes da exibicao.
  - Validar e expandir campos em modo `Force`.
  - Executar `AF` antes da persistencia.
  - Popular `zcri`, `zgrp`, `zhst`, `zdst`, `zcst = ps` e `zprc = 1` quando existirem.
  - Inserir row, executar comando Z quando aplicavel, atualizar browse e commitar.

- [ ] `CRUD-08` Implementar fluxo de update.
  - Bloquear usuario tipo `CS`.
  - Carregar row selecionada no formulario.
  - Ignorar rows invalidas e bloquear `zcst` iniciado por `r`.
  - Validar e expandir em modo `Force`.
  - Executar `AF`, aplicar campos no clone e definir `zcri`, `zgrp`, `zcst = us`.
  - Atualizar row com controle otimista, comando Z opcional, refresh e commit.

- [ ] `CRUD-09` Implementar fluxo de delete.
  - Bloquear usuario tipo `CS`.
  - Exigir rows selecionadas.
  - Ignorar rows invalidas e bloquear `zcst` iniciado por `r`.
  - Executar `AF`.
  - Rejeitar `zk = 0` e `zk` iniciado por `*`.
  - Executar comando Z em fase `BF` quando aplicavel.
  - Excluir row, refrescar browse, zerar `zk` no formulario e commitar.

- [ ] `CRUD-10` Implementar alteracao de status em lote.
  - Validar quantidade minima de argumentos.
  - Executar hook `BC` antes do loop.
  - Para cada row, validar transicao por trios de argumentos.
  - Aplicar novo status/campo/data.
  - Executar hooks `BA` e `AA`, update, comando Z opcional e commit por row.
  - Executar hook `AC` apos o loop.
  - Disparar proximo comando quando configurado.

- [ ] `CRUD-11` Implementar persistencia de row.
  - Recriar `insertRow`, `updateRow`, `deleteRow` e `setZCommand`.
  - Executar hooks de row `BR` e `AR`.
  - Suportar SQL direto e prepared statement conforme politica do alvo.
  - Preservar extensoes de row quando existirem.

- [ ] `CRUD-12` Implementar controle otimista.
  - Usar `zk` e chaves de negocio nas operacoes.
  - Validar `zdst` e `zhst` quando modo de checagem estiver ativo.
  - Falhar quando update/delete afetar zero ou mais de uma row.

- [ ] `CRUD-13` Implementar integracao com comando Z.
  - Chamar procedure/servico equivalente com comando, fase, args e cursor.
  - Preservar retorno em estrutura chave-valor.
  - Aplicar somente nas fases indicadas por `ZFase`.

- [ ] `CRUD-14` Implementar sincronizacao UI/browse.
  - Refrescar linha persistida apos insert/update/delete/status.
  - Atualizar ou remover row no browse.
  - Restaurar foco, botoes, formato e estado do formulario.

- [ ] `CRUD-15` Implementar seguranca transacional defensiva.
  - Todo fluxo de escrita deve usar `try/finally`.
  - Executar rollback se o batch ainda estiver aberto.
  - Fechar cursor/recurso mesmo em erro.

## Testes de Paridade

- [ ] `CRUD-T01` Roteador executa comando autorizado e injeta metadados, formulario e rows no executor.
- [ ] `CRUD-T02` Roteador bloqueia comando sem autorizacao antes de instanciar handler.
- [ ] `CRUD-T03` `OKDIALOG` reaproveita a acao corrente do formulario.
- [ ] `CRUD-T04` Insert executa hooks `BF`, `AF`, `BR`, `AR` na ordem esperada.
- [ ] `CRUD-T05` Insert popula campos de auditoria/estado e atualiza browse apos commit.
- [ ] `CRUD-T06` Update bloqueia row com `zcst` iniciado por `r`.
- [ ] `CRUD-T07` Update detecta conflito otimista por `zdst`/`zhst`.
- [ ] `CRUD-T08` Delete rejeita row sem `zk` persistido.
- [ ] `CRUD-T09` Delete executa rollback quando hook ou delete falha.
- [ ] `CRUD-T10` Status changer commita cada row de forma independente.
- [ ] `CRUD-T11` Status changer executa hooks `BC`, `BA`, `AA`, `AC` na ordem esperada.
- [ ] `CRUD-T12` Comando Z e chamado apenas nas fases configuradas.
- [ ] `CRUD-T13` Falha em update/delete com zero rows afetadas gera erro equivalente.
- [ ] `CRUD-T14` Falha em update/delete com mais de uma row afetada gera erro equivalente.
- [ ] `CRUD-T15` Usuario tipo `CS` nao consegue executar insert/update/delete/status.

## Migracoes e Dados

- [ ] `CRUD-M01` Migrar tabela/fonte de metadados de comandos.
- [ ] `CRUD-M02` Migrar catalogo de argumentos e `ZFase`.
- [ ] `CRUD-M03` Migrar scripts Pnuts necessarios para hooks de CRUD.
- [ ] `CRUD-M04` Criar camada de compatibilidade para procedures chamadas por `setZCommand`.
- [ ] `CRUD-M05` Criar fixtures de rows com estados `ps`, `us`, `ds`, `r*`, `st` e chaves invalidas.

## Criterios de Aceite

- [ ] Comandos CRUD podem ser executados apenas via metadados e autorizacao.
- [ ] Insert, update, delete e status preservam fases, hooks, transacao e refresh.
- [ ] Rollback ocorre em toda falha antes do commit.
- [ ] Controle otimista impede sobrescrita silenciosa.
- [ ] Browse/formulario ficam sincronizados apos cada operacao.
- [ ] Testes de paridade cobrem sucesso, bloqueio, erro e rollback.

## Riscos

- 🔴 Sem metadados reais, classes, argumentos e proximos comandos podem ficar incompletos.
- 🔴 Sem scripts Pnuts, hooks podem esconder regras de negocio criticas.
- 🔴 Sem DDL, tipos e constraints dos campos `z*` precisam ser inferidos.
- 🟡 Reflexao por classe pode ser substituida por registry no alvo, mas deve manter equivalencia de configuracao.
- 🟡 SQL direto do legado exige decisao consciente entre compatibilidade literal e prepared statements.

