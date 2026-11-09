# Scripts e Regras Dinamicas Pnuts

## Visao Geral

A unit fornece o motor de scripts Pnuts do MinimalJ, permitindo carregar funcoes globais, criar pacotes por entidade/contexto, avaliar expressoes dinamicas e executar hooks de formulario, campo, linha e comando em tempo de execucao. 🟢

Ela conecta scripts `.pnut`, metadados `MJEX`, formularios Swing, linhas `MRow`, cursores JDBC e utilitarios Java, permitindo alterar regras de negocio sem recompilar o nucleo Java. 🟢

## Responsabilidades

- Inicializar o runtime Pnuts com `init.pnut`, `Minimal.pnut` e template `MPackage.pnut`. 🟢
- Manter registry estatico de pacotes `MPnuts` por nome, com busca, criacao sob demanda, reload e limpeza global. 🟢
- Criar contexto e pacote Pnuts por entidade, expondo `zemc`, `X`, `zglobals` e `Z`. 🟢
- Avaliar strings Pnuts, funcoes Pnuts e condicoes booleanas com conversao de erros para `MError`. 🟢
- Resolver placeholders `[VAR]`, `[REC.CAMPO]`, `[pnuts:expr]` e formatos embutidos em strings. 🟢
- Injetar `form`, `m`, `row`, `mcursor`, `wrows` e `command` no pacote durante execucoes. 🟢
- Localizar e executar hooks por chave `(iob, itc, icomp, ifase)` para sistema, formulario, campo e linha. 🟢
- Cachear funcoes compiladas ou ausentes, evitando reavaliar expressoes a cada chamada. 🟢
- Expor biblioteca padrao `Minimal.pnut` com funcoes de string, numero, data, SQL, erro, formulario, moeda, email e validacao. 🟢
- Permitir comando `MCallPnuts` para executar script associado a comando de interface com transacao JDBC. 🟢

## Regras de Negocio

- `initMPnuts()` deve executar uma unica vez por processo enquanto `started=false`; depois reutiliza estado global. 🟢
- `PT.Minimal.pnutsTrace=true` deve habilitar verbose do Pnuts e logs de `MPnutsPackage`. 🟢
- Falha ao carregar `init.pnut`, `Minimal.pnut` ou `MPackage.pnut` deve mostrar `MError` e encerrar processo com `System.exit(1)`. 🟢
- Cada `MPnuts(pkg)` deve registrar a instancia em `mpnuts`, criar `MPnutsPackage`, expor variaveis globais e avaliar o template `_MPackage`. 🟢
- `findMPnut(pkg)` deve retornar pacote existente ou criar novo pacote quando ausente. 🟢
- `getMPnut(pkg)` deve falhar quando runtime ainda nao iniciou ou pacote nao existe. 🟢
- `reloadAll()` deve recarregar todos os pacotes e limpar cache de expressoes em `MMData.clearJExps()`. 🟢
- `clearAll()` deve remover pacotes Pnuts, limpar globals e retornar `started=false`. 🟢
- `evalCondition(cond)` deve retornar `true` para condicao nula/vazia e exigir resultado Boolean para condicao nao vazia. 🟢
- `evalValues` deve substituir marcadores entre colchetes, rejeitar `[` aninhado e rejeitar marcador sem `]`. 🟢
- Marcador `pnuts:` deve avaliar expressao Pnuts e substituir `null` por string vazia. 🟢
- Formatos numericos, data, maiuscula/minuscula e trim devem seguir modificadores `N`, `D`, `U`, `L`, `F`, `E` implementados em `evalVar`. 🟢
- Hooks de formulario devem executar primeiro hook de sistema `EEEE0` derivado do componente e depois hook da entidade. 🟢
- Hooks de campo devem executar primeiro hook de sistema por atributo; depois tentar chave especifica `icomp + "_" + formName`, e por fim chave generica `icomp`. 🟢
- Hooks de linha devem executar primeiro hook de sistema `MR` e depois hook da entidade da linha. 🟢
- Quando expressao nao existe, cache deve guardar marcador `_NULL` tambem para nomes `MIN_` e `CLI_`. 🟢
- `MPnutsExp` deve montar funcoes com imports padrao de `PT.Minimal.geral.*`, `PT.Minimal.util.*` e `PT.Minimal.pnuts.*`. 🟢
- Usuarios cujo tipo contem `CS` nao podem executar `MCallPnuts`. 🟢
- `MCallPnuts` deve executar em transacao; em falha, rollback deve ocorrer se a conexao ainda estiver em begin work. 🟢
- Se ultimo argumento de `MCallPnuts` nao for `NOOK`, sucesso deve mostrar mensagem `MIN-000000M`. 🟢
- Scripts Pnuts por entidade/hook existem em fontes hibridas: alguns em diretorios fisicos da instalacao e outros na tabela `mjext0`, carregados ao iniciar a aplicacao. O workspace atual nao contem esse conjunto completo. 🟢 para arquitetura hibrida confirmada pelo usuario; 🔴 para conteudo completo ausente.

## Requisitos Funcionais

| ID | Requisito | Prioridade | Criterio de Aceite |
|----|-----------|------------|--------------------|
| RF-01 | Inicializar runtime Pnuts global uma unica vez. 🟢 | Must | Dado primeira chamada a `initMPnuts`, quando executada, entao cria `zglobals`, `mpnuts`, `initContext`, carrega scripts base e guarda `_MPackage`; chamadas seguintes nao recarregam enquanto `started=true`. |
| RF-02 | Criar e registrar pacote Pnuts por nome. 🟢 | Must | Dado `findMPnut("ENT")`, quando pacote nao existe, entao instancia `MPnuts("ENT")`, registra em `mpnuts` e inicializa contexto/pacote. |
| RF-03 | Expor variaveis globais e locais padronizadas no pacote. 🟢 | Must | Dado pacote inicializado, entao `zemc`, `X`, `zglobals` e `Z` existem no pacote Pnuts e no mapa local quando aplicavel. |
| RF-04 | Avaliar scripts e funcoes com traducao de erros. 🟢 | Must | Dado script ou `PnutsFunction`, quando ocorre `PnutsException`, entao `MError` interno e relancado ou excecao original e encapsulada em `MError`. |
| RF-05 | Avaliar condicoes booleanas. 🟢 | Must | Dado condicao vazia, retorna `true`; dado condicao que nao retorna Boolean, lanca `MError("Condição inválida...")`. |
| RF-06 | Expandir placeholders `[ ... ]` em strings. 🟢 | Must | Dado texto com `[VAR]`, `[OBJ.CAMPO]`, `[pnuts:expr]` ou formato, quando `evalValues` executa, entao substitui marcadores ou falha em sintaxe invalida. |
| RF-07 | Injetar `row`, `form` e aliases no contexto. 🟢 | Must | Dado `setRow` ou `setForm`, quando argumento nao nulo, entao variaveis sao expostas em `zemc` e `pContext`; quando nulo, sao removidas. |
| RF-08 | Resolver hooks por chave de metadados. 🟢 | Must | Dado `(iob,itc,icomp,ifase)`, quando hook existe em `MMData.getJExp`, entao cria/carrega funcao Pnuts e executa. |
| RF-09 | Executar fallback de hook com `*` quando `check=true`. 🟢 | Should | Dado hook especifico ausente, quando `evalPnut(..., true)` executa, entao tenta chave com `iob="*"`. |
| RF-10 | Avaliar hooks de sistema antes dos hooks de formulario/campo/linha. 🟢 | Must | Dado chamada `evalFormPnut`, `evalFieldPnut` ou `evalRowPnut`, entao hook `EEEE0` apropriado e executado antes do hook especifico. |
| RF-11 | Cachear funcoes carregadas e ausentes. 🟢 | Should | Dado chamada repetida ao mesmo hook, quando cache ja contem funcao ou `_NULL`, entao evita reconstruir expressao. |
| RF-12 | Recarregar pacotes em runtime. 🟢 | Should | Dado `reload()` ou `reloadAll()`, quando executado, entao pacotes sao removidos/recriados e caches limpos. |
| RF-13 | Expor biblioteca padrao `Minimal.pnut`. 🟢 | Must | Dado scripts de negocio, quando executados, entao funcoes de numero, string, data, SQL, erros, formulario, moeda, email e validacao estao disponiveis. |
| RF-14 | Executar Pnuts via comando de interface. 🟢 | Must | Dado `MCallPnuts` autorizado com argumentos validos, quando executado, entao injeta form/cursor/rows/command, executa script por linha ou globalmente e controla transacao. |
| RF-15 | Bloquear execucao Pnuts para usuario tipo `CS`. 🟢 | Must | Dado usuario com tipo contendo `CS`, quando `MCallPnuts.execute()` inicia, entao lanca `MIN-009998E`. |
| RF-16 | Listar e consultar pacote dinamicamente. 🟢 | Could | Dado pacote Pnuts, quando `listPackage`, `defined`, `keys` ou `elements` sao usados, entao retorna estado ativo e pode logar se trace ativo. |

## Requisitos Nao Funcionais

| Tipo | Requisito inferido | Evidencia no codigo | Confianca |
|------|--------------------|---------------------|-----------|
| Compatibilidade | Runtime precisa incluir biblioteca Pnuts (`pnuts.lang.*`) e scripts base no classpath. | `PT/Minimal/pnuts/MPnuts.java:16`, `PT/Minimal/pnuts/MPnuts.java:89`, `PT/Minimal/pnuts/MPnuts.java:93` | 🟢 |
| Confiabilidade | Falha de bootstrap Pnuts e fatal para a aplicacao. | `PT/Minimal/pnuts/MPnuts.java:96` | 🟢 |
| Observabilidade | Trace depende de propriedade `PT.Minimal.pnutsTrace` e `Pnuts.setVerbose`. | `PT/Minimal/pnuts/MPnuts.java:82`, `PT/Minimal/pnuts/MPnutsPackage.java` | 🟢 |
| Performance | Cache de funcoes e `_NULL` evita recompilar/rebuscar hooks inexistentes. | `PT/Minimal/pnuts/MPnuts.java:831`, `PT/Minimal/pnuts/MPnuts.java:852` | 🟢 |
| Transacionalidade | `MCallPnuts` executa scripts dentro de `beginWork`/`commitWork` e faz rollback em `finally` quando necessario. | `PT/Minimal/actions/MCallPnuts.java:85`, `PT/Minimal/actions/MCallPnuts.java:101` | 🟢 |
| Seguranca | Execucao manual de comando Pnuts e bloqueada para usuario tipo `CS`, mas scripts ainda podem executar SQL e email quando autorizados. | `PT/Minimal/actions/MCallPnuts.java:43`, `PT/Minimal/pnuts/Minimal.pnut:315`, `PT/Minimal/pnuts/Minimal.pnut:872` | 🟢 |
| Manutenibilidade | Regras reais dependem de metadados `MJEX`/`mjext0` e scripts fisicos por entidade; o usuario confirmou que ambos podem existir fora do workspace. | `_reversa_sdd/domain.md`, `PT/Minimal/pnuts/MPnuts.java:852`, validacao humana 2026-05-22 | 🟢 para a fonte hibrida; 🔴 para inventario incompleto |

## Criterios de Aceitacao

```gherkin
Cenario: inicializar motor Pnuts
Dado runtime com biblioteca Pnuts e scripts base no classpath
Quando initMPnuts executa pela primeira vez
Entao init.pnut e Minimal.pnut sao carregados
E MPackage.pnut e lido para _MPackage
E started fica true

Cenario: criar pacote de entidade
Dado motor Pnuts iniciado
Quando findMPnut("LOGIN") e chamado sem pacote existente
Entao um MPnuts e criado para LOGIN
E o pacote expoe zemc, X, zglobals e Z
E MPackage.pnut e avaliado no contexto do pacote

Cenario: avaliar condicao valida
Dado pacote Pnuts inicializado
Quando evalCondition recebe expressao que retorna Boolean
Entao o valor booleano e retornado

Cenario: avaliar condicao invalida
Dado pacote Pnuts inicializado
Quando evalCondition recebe expressao que nao retorna Boolean
Entao MError de condicao invalida e lancado

Cenario: expandir placeholders
Dado zemc contem variavel CLIENTE com valor "ABC"
Quando evalValues recebe "Nome=[CLIENTE]"
Entao retorna "Nome=ABC"

Cenario: executar hook de formulario
Dado metadado MJEX possui hook para entidade e fase
Quando evalFormPnut e chamado
Entao hook de sistema EEEE0 e avaliado antes
E hook especifico da entidade e executado

Cenario: executar comando Pnuts por linhas selecionadas
Dado usuario autorizado e comando com argumento de script
E existem linhas selecionadas
Quando MCallPnuts.execute executa em modo SEL
Entao cada linha e exposta como row
E cada execucao ocorre dentro de transacao
E cursor e rows sao removidos do pacote no finally

Cenario: bloquear usuario CS
Dado usuario cujo tipo contem CS
Quando MCallPnuts.execute e chamado
Entao erro MIN-009998E impede execucao
```

## Prioridade (MoSCoW)

| Requisito | MoSCoW | Justificativa |
|-----------|--------|---------------|
| Bootstrap Pnuts e pacote por entidade | Must | Sem runtime e pacote, nenhum hook dinamico funciona. 🟢 |
| Avaliacao de scripts/funcoes/condicoes | Must | Base operacional para regras dinamicas. 🟢 |
| Injecao de form/row/contexto | Must | Hooks dependem de estado do formulario e linha. 🟢 |
| Resolucao de hooks por metadado | Must | Regras de formulario/campo/linha sao indexadas por `iob/itc/icomp/ifase`. 🟢 |
| Biblioteca `Minimal.pnut` | Must | Scripts esperam funcoes utilitarias globais. 🟢 |
| `MCallPnuts` transacional | Must | Comandos de UI podem executar Pnuts com efeitos de banco. 🟢 |
| Cache de funcoes | Should | Importante para performance e para evitar repeticao de lookup/compilacao. 🟢 |
| Reload/clear runtime | Should | Necessario para administracao e atualizacao dinamica, mas nao para caminho feliz de execucao. 🟢 |
| Trace/listagem de pacote | Could | Ajuda diagnostico, nao bloqueia execucao funcional. 🟢 |

## Rastreabilidade de Codigo

| Arquivo | Funcao / Classe | Cobertura |
|---------|-----------------|-----------|
| `PT/Minimal/pnuts/MPnuts.java` | Bootstrap, registry, contexto, avaliacao, hooks, cache, expansao de valores | 🟢 |
| `PT/Minimal/pnuts/MPnutsExp.java` | Chaves, nomes, wrappers `MIN_`/`CLI_`, montagem de funcoes | 🟢 |
| `PT/Minimal/pnuts/MPnutsPackage.java` | Pacote customizado, lookup dinamico e trace | 🟢 |
| `PT/Minimal/pnuts/Minimal.pnut` | Biblioteca padrao de utilitarios, SQL, erros, validacoes, moeda, email | 🟢 |
| `PT/Minimal/pnuts/MPackage.pnut` | Inicializacao por pacote, evalValues, listPackage, getIorgx | 🟢 |
| `PT/Minimal/actions/MCallPnuts.java` | Execucao de comando Pnuts via UI, autorizacao e transacao | 🟢 |
| `_reversa_sdd/domain.md` | Regras de dominio e lacunas sobre scripts por entidade | 🟢 |
| `_reversa_sdd/state-machines.md` | Fases e hooks Pnuts (`BF`, `AF`, `BR`, `AR`) | 🟢 |

## Lacunas

| Lacuna | Impacto | Confianca |
|--------|---------|-----------|
| Scripts Pnuts por entidade/hook cadastrados em producao nao estao no workspace. | Nao e possivel enumerar todas as regras reais de negocio executadas via `MJEX`. | 🔴 |
| DDL/conteudo de tabelas de metadados `mjext0`/equivalentes nao esta disponivel. | Resolucao exata de chaves, funcoes e fases depende de dados externos. | 🔴 |
| Versao exata da biblioteca Pnuts nao esta versionada no repositorio. | Pode haver diferencas de sintaxe/runtime ao reimplementar ou executar legado. | 🔴 |

Nota de validacao humana: em 2026-05-22, o usuario confirmou que parte dos scripts existe fisicamente no diretorio de instalacao e parte reside no banco, na tabela `mjext0`, sendo carregada na inicializacao da aplicacao. Portanto, a arquitetura de carga hibrida e confirmada; o conteudo concreto ainda precisa ser exportado para paridade completa.
