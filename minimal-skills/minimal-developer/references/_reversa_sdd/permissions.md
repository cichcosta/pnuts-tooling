# Permissoes e Controle de Acesso — MinimalJ

## Modelo de Permissao

O MinimalJ usa um modelo hibrido de autorizacao por usuario/grupo/nivel, com regras carregadas do banco. Nao ha evidencia de RBAC moderno por papeis nomeados; a autorizacao e baseada em:

- usuario (`MLogin.getUser()`);
- grupo/nodo (`MLogin.getUserNodo()`);
- nivel de autorizacao (`MLogin.getUserAutLevel()`);
- tipo de usuario (`MLogin.getUserTipo()`);
- autorizacoes gerais e especiais da tabela `ssact0`;
- entidade/comando solicitados.

## Fontes de Identidade

| Atributo | Origem | Uso | Confianca |
|----------|--------|-----|-----------|
| Usuario | Login manual, propriedades ou batch (`user.name`). | Identidade principal, auditoria e conexao JDBC. | 🟢 CONFIRMADO |
| Senha | Login manual, propriedades ou batch (`user.passwd`). | Conexao JDBC e alteracao de senha. | 🟢 CONFIRMADO |
| DSN | Login/propriedade (`user.dsn`). | Conexao default e conexoes auxiliares. | 🟢 CONFIRMADO |
| Grupo/nodo (`igrp`) | Registro `suact0`. | Autorizacao por linha de acesso. | 🟢 CONFIRMADO |
| Organizacao (`iorg`) | `suact0` ou parametro `@*ORGDEFAULT`. | Escopo organizacional de sessao. | 🟢 CONFIRMADO |
| Menu (`imen`) | `suact0`. | Menu inicial do usuario. | 🟢 CONFIRMADO |
| Nivel (`cns`) | `suact0`. | Comparacao contra nivel requerido pelo comando. | 🟢 CONFIRMADO |
| Tipo (`cut`) | `suact0`. | Bloqueios especiais como `CS`. | 🟢 CONFIRMADO |

## Matriz de Permissoes

| Operacao | Usuario `minimal` | Usuario comum autorizado | Usuario comum sem autorizacao | Tipo contendo `CS` | Evidencia |
|----------|-------------------|--------------------------|-------------------------------|--------------------|-----------|
| Abrir entidade inicial | Permitido | Permitido se `checkAut(ent, "*", "1")` passar | Bloqueado | Depende de regra geral | `MinMain`, `MCheckAut` |
| Executar comando `MJ` | Permitido | Permitido se entidade/comando/nivel passarem | Bloqueado com `MIN-009998E` | Bloqueado se comando de escrita tambem testar `CS` | `MHandleCommand` |
| Inserir linha | Permitido | Permitido apos autorizacao do comando | Bloqueado antes da classe ou por regra | Bloqueado explicitamente | `MInsert` |
| Atualizar linha | Permitido | Permitido apos autorizacao do comando | Bloqueado antes da classe ou por regra | Bloqueado explicitamente | `MUpdate` |
| Excluir linha | Permitido | Permitido apos autorizacao do comando e validacao de `zk`/`zcst` | Bloqueado antes da classe ou por regra | Bloqueado explicitamente | `MDelete` |
| Executar Pnuts via comando | Permitido | Permitido apos autorizacao do comando | Bloqueado antes da classe ou por regra | Bloqueado explicitamente | `MCallPnuts` |
| Mudar status em lote | Permitido | Permitido apos autorizacao do comando e transicao valida | Bloqueado antes da classe ou por regra | Bloqueado explicitamente | `MStatusChanger` |
| Alterar senha | Nao ha bypass especifico identificado | Permitido conforme usuario/conexao atual | Dependente do banco | Dependente do banco | `MLogin` |

## Regras de Autorizacao

### P01 — Bypass administrativo

Se o usuario logado e `minimal`, `MCheckAut` retorna permitido sem consultar regras carregadas. Esse usuario tambem recebe nivel `"9"` quando nao ha registro valido em `suact0`.

Confianca: 🟢 CONFIRMADO

### P02 — Autorizacao por nivel

O comando informa um nivel requerido (`wcmd.getCommandAutLevel()`). `MCheckAut` compara esse nivel com `MLogin.getUserAutLevel()`. Se o requerido for superior ao nivel do usuario, o acesso e negado.

Confianca: 🟢 CONFIRMADO

### P03 — Autorizacao por entidade/comando

As regras sao carregadas de `ssact0` para a entidade exata, o prefixo de quatro caracteres e o prefixo de um caractere. O mesmo mecanismo permite regra especifica (`iobs` igual ao comando) ou regra geral (`iobs = "*"`) da entidade.

Confianca: 🟢 CONFIRMADO

### P04 — Allow/deny por linha de acesso

`checkAutLine` procura usuario ou grupo/nodo no texto `tsac`. Quando `isac == "DN"`, inverte o resultado, representando negativa explicita.

Confianca: 🟢 CONFIRMADO

### P05 — Autorizacao especial tem precedencia

Antes da autorizacao geral, `checkAut` testa regras especiais (`isact = 'AE'`) para o comando exato e para `"*"`. Se uma regra especial retornar resultado, ela decide a permissao.

Confianca: 🟢 CONFIRMADO

### P06 — Tipo `CS` e read-only operacional para escrita

Mesmo que o comando tenha passado pela autorizacao metadata-driven, classes de escrita impedem execucao quando o tipo do usuario contem `CS`.

Confianca: 🟢 CONFIRMADO

## Lacunas

| Lacuna | Impacto | Confianca |
|--------|---------|-----------|
| Nao ha dump de `suact0`/`ssact0`. | Nao e possivel listar usuarios, grupos, niveis reais, regras por entidade ou excecoes especiais. | 🔴 LACUNA |
| Sem metadados de comandos, nao e possivel fechar todos os niveis `cns` por operacao. | A matriz acima descreve o mecanismo, nao a politica completa de uma instalacao. | 🔴 LACUNA |
| Sem documentacao de negocio para `cut`, apenas `CS` foi identificado por codigo. | Outros tipos podem existir e ter semantica em scripts/banco. | 🔴 LACUNA |
