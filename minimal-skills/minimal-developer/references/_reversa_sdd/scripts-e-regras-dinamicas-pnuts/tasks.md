# Scripts e Regras Dinamicas Pnuts, Tarefas de Implementacao

## Pre-requisitos

- [ ] Biblioteca/runtime Pnuts original identificado ou decisao de motor de scripts substituto aprovada. 🔴
- [ ] Scripts base `init.pnut`, `Minimal.pnut` e `MPackage.pnut` disponiveis no classpath ou empacotamento equivalente. 🟢
- [ ] Fonte de metadados `MJEX` exportada ou contrato equivalente para `(iob,itc,icomp,ifase,cfunc,text)` definido. 🔴
- [ ] Objetos equivalentes a `MForm`, `MRow`, `MConnection`, `MCursor`, `MHashtable`, `MError` e `MMData` disponiveis. 🟢
- [ ] Politica de seguranca para scripts dinamicos definida, incluindo SQL, email e acesso a propriedades. 🔴

## Tarefas

- [ ] T-01, Implementar bootstrap global do motor Pnuts.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:75`, `PT/Minimal/pnuts/MPnuts.java:89`, `PT/Minimal/pnuts/MPnuts.java:93`
  - Criterio de pronto: Primeira inicializacao cria globals, registry, contexto, carrega scripts base e guarda template de pacote; chamadas seguintes nao recarregam.
  - Confianca: 🟢

- [ ] T-02, Implementar configuracao de trace.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:82`, `PT/Minimal/pnuts/MPnutsPackage.java`
  - Criterio de pronto: Propriedade `PT.Minimal.pnutsTrace` habilita verbose do runtime e logs de operacoes de pacote.
  - Confianca: 🟢

- [ ] T-03, Implementar tratamento fatal de erro no bootstrap.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:96`
  - Criterio de pronto: Falha em `init.pnut`, `Minimal.pnut` ou `MPackage.pnut` e exibida como erro operacional e impede iniciar app em estado parcial.
  - Confianca: 🟢

- [ ] T-04, Implementar registry de pacotes `MPnuts`.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:55`, `PT/Minimal/pnuts/MPnuts.java:176`, `PT/Minimal/pnuts/MPnuts.java:191`
  - Criterio de pronto: `isMPnut`, `getMPnut` e `findMPnut` preservam semantica de existencia, falha e criacao sob demanda.
  - Confianca: 🟢

- [ ] T-05, Implementar inicializacao de pacote local.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:117`, `PT/Minimal/pnuts/MPnuts.java:137`
  - Criterio de pronto: Cada pacote cria `zemc`/`X`, contexto proprio, `MPnutsPackage`, injeta `zglobals`/`Z` e avalia `MPackage.pnut`.
  - Confianca: 🟢

- [ ] T-06, Implementar wrapper de pacote com lookup dinamico.
  - Origem no legado: `PT/Minimal/pnuts/MPnutsPackage.java:18`
  - Criterio de pronto: `defined(var)` consulta pacote e, se ausente, tenta `mpnut.loadFunction(var)` antes de responder falso.
  - Confianca: 🟢

- [ ] T-07, Implementar avaliacao de string Pnuts e funcao Pnuts.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:378`, `PT/Minimal/pnuts/MPnuts.java:425`
  - Criterio de pronto: Scripts e `PnutsFunction` executam no contexto do pacote, traduzindo `PnutsException` e erros internos para `MError`.
  - Confianca: 🟢

- [ ] T-08, Implementar avaliacao de condicoes.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:512`
  - Criterio de pronto: Condicao vazia retorna `true`; condicao nao booleana falha com mensagem de condicao invalida.
  - Confianca: 🟢

- [ ] T-09, Implementar expansao de placeholders em strings.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:575`, `PT/Minimal/pnuts/MPnuts.java:636`
  - Criterio de pronto: `[VAR]`, `[OBJ.CAMPO]`, `[pnuts:expr]`, formatos de tamanho, `N`, `D`, `L`, `U`, `F`, `E` funcionam e erros de colchete sao detectados.
  - Confianca: 🟢

- [ ] T-10, Implementar injecao e remocao de `row`, `form` e aliases.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:769`, `PT/Minimal/pnuts/MPnuts.java:786`
  - Criterio de pronto: `setRow(null)` e `setForm(null)` removem variaveis; valores nao nulos ficam acessiveis via pacote/contexto.
  - Confianca: 🟢

- [ ] T-11, Implementar modelo de chave e nome de funcao `MPnutsExp`.
  - Origem no legado: `PT/Minimal/pnuts/MPnutsExp.java:37`, `PT/Minimal/pnuts/MPnutsExp.java:157`, `PT/Minimal/pnuts/MPnutsExp.java:280`
  - Criterio de pronto: Chaves `IOB+ITC+ICOMP+IFASE`, nomes normalizados e wrappers com imports padrao sao gerados de forma compativel.
  - Confianca: 🟢

- [ ] T-12, Implementar montagem de funcoes `MIN_` e `CLI_`.
  - Origem no legado: `PT/Minimal/pnuts/MPnutsExp.java:67`, `PT/Minimal/pnuts/MPnutsExp.java:96`, `PT/Minimal/pnuts/MPnutsExp.java:117`
  - Criterio de pronto: Metadado de prefixo `MIN`/`CLI` gera funcao prefixada e wrapper principal delega para `CLI_` quando existe, senao `MIN_`.
  - Confianca: 🟢

- [ ] T-13, Implementar cache de funcoes e ausencias.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:831`, `PT/Minimal/pnuts/MPnuts.java:846`, `PT/Minimal/pnuts/MPnuts.java:858`
  - Criterio de pronto: Funcoes compiladas e `_NULL` sao cacheados por nome base, `MIN_` e `CLI_`; chamadas repetidas nao consultam metadados inutilmente.
  - Confianca: 🟢

- [ ] T-14, Implementar resolucao e execucao de hook generico.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:892`, `PT/Minimal/pnuts/MPnuts.java:912`, `PT/Minimal/pnuts/MPnuts.java:940`
  - Criterio de pronto: `existsPnut`, `evalPnut` e fallback `iob="*"` preservam comportamento legado.
  - Confianca: 🟢

- [ ] T-15, Implementar hooks de sistema, formulario, campo e linha.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:921`, `PT/Minimal/pnuts/MPnuts.java:976`, `PT/Minimal/pnuts/MPnuts.java:1000`, `PT/Minimal/pnuts/MPnuts.java:1022`
  - Criterio de pronto: Hooks de sistema `EEEE0` executam antes; campo tenta variante por formulario antes da generica; linha usa entidade da row.
  - Confianca: 🟢

- [ ] T-16, Implementar reload e limpeza de runtime.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:226`, `PT/Minimal/pnuts/MPnuts.java:244`, `PT/Minimal/pnuts/MPnuts.java:267`
  - Criterio de pronto: `reload`, `reloadAll` e `clearAll` removem pacotes/caches/globals conforme escopo e deixam sistema em estado consistente.
  - Confianca: 🟢

- [ ] T-17, Migrar biblioteca padrao `Minimal.pnut`.
  - Origem no legado: `PT/Minimal/pnuts/Minimal.pnut`
  - Criterio de pronto: Funcoes de string, numero, data, SQL, erro, formulario, moeda, email, validacao e constantes globais existem com assinaturas equivalentes.
  - Confianca: 🟢

- [ ] T-18, Migrar template por pacote `MPackage.pnut`.
  - Origem no legado: `PT/Minimal/pnuts/MPackage.pnut`
  - Criterio de pronto: Cada pacote recebe `listPackage`, `evalValues`, `evalFormatedValues` e `getIorgx` equivalentes.
  - Confianca: 🟢

- [ ] T-19, Implementar comando transacional `MCallPnuts`.
  - Origem no legado: `PT/Minimal/actions/MCallPnuts.java:43`, `PT/Minimal/actions/MCallPnuts.java:71`, `PT/Minimal/actions/MCallPnuts.java:81`
  - Criterio de pronto: Usuario `CS` bloqueado; argumentos resolvidos; form/command/cursor/rows injetados; modo `SEL` executa por linha; modo global executa uma vez.
  - Confianca: 🟢

- [ ] T-20, Implementar rollback/finalizacao de comando Pnuts.
  - Origem no legado: `PT/Minimal/actions/MCallPnuts.java:95`, `PT/Minimal/actions/MCallPnuts.java:101`
  - Criterio de pronto: Sucesso mostra `MIN-000000M` salvo `NOOK`; falha faz rollback se necessario; cursor fecha; `mcursor` e `wrows` sao removidos.
  - Confianca: 🟢

- [ ] T-21, Documentar e decidir anomalias de compatibilidade.
  - Origem no legado: `PT/Minimal/pnuts/MPnutsExp.java:280`, `PT/Minimal/actions/MCallPnuts.java`, `PT/Minimal/pnuts/MPnuts.java:267`
  - Criterio de pronto: Decisoes registradas sobre colisao de chave sem `cfunc`, fonte decompilado quebrado de `MCallPnuts` e comportamento de `clearAll`.
  - Confianca: 🟢

## Tarefas de Teste

- [ ] TT-01, Testar bootstrap feliz.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:75`
  - Criterio de pronto: Runtime carrega scripts base, registry fica inicializado e `_MPackage` nao vazio.
  - Confianca: 🟢

- [ ] TT-02, Testar falha de bootstrap.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:96`
  - Criterio de pronto: Ausencia de script base gera erro fatal controlado, sem continuar em estado parcial.
  - Confianca: 🟢

- [ ] TT-03, Testar criacao, busca, reload e clear de pacotes.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:176`, `PT/Minimal/pnuts/MPnuts.java:226`, `PT/Minimal/pnuts/MPnuts.java:267`
  - Criterio de pronto: Pacote criado e reutilizado; reload limpa cache; clear remove registry e globals.
  - Confianca: 🟢

- [ ] TT-04, Testar `eval`, `evalFunc` e traducao de erros.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:378`, `PT/Minimal/pnuts/MPnuts.java:475`
  - Criterio de pronto: Resultado de script valido retorna; excecoes Pnuts viram `MError` equivalente.
  - Confianca: 🟢

- [ ] TT-05, Testar `evalCondition`.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:512`
  - Criterio de pronto: Vazio retorna true, boolean retorna valor, nao boolean falha.
  - Confianca: 🟢

- [ ] TT-06, Testar `evalValues`.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:575`
  - Criterio de pronto: Substitui variaveis, propriedades, `pnuts:`, formatos; detecta `[` aninhado e `[` sem fechamento.
  - Confianca: 🟢

- [ ] TT-07, Testar resolucao de hooks e cache.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:846`, `PT/Minimal/pnuts/MPnuts.java:912`
  - Criterio de pronto: Hook existente executa; ausente cacheia `_NULL`; fallback `*` funciona quando habilitado.
  - Confianca: 🟢

- [ ] TT-08, Testar ordem de hooks de sistema.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:976`, `PT/Minimal/pnuts/MPnuts.java:1000`, `PT/Minimal/pnuts/MPnuts.java:1022`
  - Criterio de pronto: Sistema roda antes de formulario/campo/linha e efeitos sao observaveis.
  - Confianca: 🟢

- [ ] TT-09, Testar biblioteca `Minimal.pnut`.
  - Origem no legado: `PT/Minimal/pnuts/Minimal.pnut`
  - Criterio de pronto: Validacoes `validateOne`, `validateAny`, `validateAll`, `validateEmpty`, SQL wrappers, erro e funcoes de string/numero/data passam cenarios basicos.
  - Confianca: 🟢

- [ ] TT-10, Testar `MCallPnuts`.
  - Origem no legado: `PT/Minimal/actions/MCallPnuts.java:43`
  - Criterio de pronto: Usuario `CS` bloqueado; usuario autorizado executa modo `SEL` e global; commit/rollback/finally funcionam.
  - Confianca: 🟢

## Tarefas de Migracao de Dados

- [ ] TM-01, Exportar metadados de expressoes Pnuts.
  - Origem no legado: `PT/Minimal/pnuts/MPnuts.java:856`, `_reversa_sdd/domain.md`
  - Criterio de pronto: Dados equivalentes a `MJEX`/`mjext0` com `i_ob`, `i_tc`, `i_comp`, `i_fase`, `cfunc`, `text`, prefixo `MIN/CLI` exportados e versionados.
  - Confianca: 🔴

- [ ] TM-02, Exportar scripts Pnuts por entidade e hook.
  - Origem no legado: `_reversa_sdd/state-machines.md`
  - Criterio de pronto: Hooks `BF`, `AF`, `BR`, `AR`, `BC`, `BA`, `AA`, `AC` e outros `ifase` reais disponiveis para testes de paridade.
  - Confianca: 🔴

- [ ] TM-03, Inventariar dependencias Pnuts/JavaMail/JAF.
  - Origem no legado: `PT/Minimal/pnuts/Minimal.pnut:872`, `PT/Minimal/pnuts/MPnuts.java:16`
  - Criterio de pronto: Versoes e jars originais identificados ou substitutos aprovados.
  - Confianca: 🔴

## Ordem Sugerida

1. T-01 a T-05 primeiro, porque definem runtime e pacote.
2. T-06 a T-10 depois, para avaliacao, variaveis e placeholders.
3. T-11 a T-15 em seguida, pois resolvem metadados e hooks.
4. T-16 apos cache/hook, para garantir reload sem estado obsoleto.
5. T-17 e T-18 para biblioteca Pnuts real.
6. T-19 e T-20 para comando transacional.
7. TM-01 a TM-03 devem rodar antes de afirmar paridade de negocio.
8. T-21 e testes TT-01 a TT-10 fecham compatibilidade.

## Lacunas Pendentes (🔴)

- 🔴 Sem export de metadados `MJEX`, nao ha lista completa de hooks, fases, chaves e textos Pnuts reais.
- 🔴 Sem versao original dos jars Pnuts/JavaMail/JAF, compatibilidade de runtime fica incerta.
- 🔴 Politica moderna de sandbox/autorizacao para scripts ainda precisa ser definida; legado permite SQL/email dentro dos scripts quando usuario passa autorizacao do comando.
