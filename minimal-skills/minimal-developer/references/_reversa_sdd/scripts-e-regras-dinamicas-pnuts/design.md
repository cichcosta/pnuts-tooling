# Scripts e Regras Dinamicas Pnuts, Design Tecnico

## Interface

### Classes, scripts e metodos

| Simbolo | Assinatura | Retorno | Observacao |
|---------|------------|---------|------------|
| `MPnuts.initMPnuts` | `()` | `void` | Bootstrap global; carrega `init.pnut`, `Minimal.pnut` e template `MPackage.pnut`. 🟢 |
| `MPnuts.MPnuts` | `(pkg: String)` | instancia `MPnuts` | Inicializa runtime, registra pacote no registry e chama `initPackage`. 🟢 |
| `MPnuts.findMPnut` | `(pkg: String)` | `MPnuts` | Busca pacote existente ou cria novo. 🟢 |
| `MPnuts.getMPnut` | `(pkg: String)` | `MPnuts` | Busca pacote ja criado; falha se runtime/pacote ausente. 🟢 |
| `MPnuts.reload` | `()` | `void` | Remove pacote Pnuts, limpa cache e recria contexto. 🟢 |
| `MPnuts.reloadAll` | `()` | `void` | Recarrega todos pacotes e limpa expressoes em `MMData`. 🟢 |
| `MPnuts.clearAll` | `()` | `void` | Remove pacotes, globals e estado estatico. 🟢 |
| `MPnuts.eval` | `(pnut: String)` | `Object` | Avalia string Pnuts no contexto do pacote. 🟢 |
| `MPnuts.eval` | `(func: PnutsFunction, parms: Object[])` | `Object` | Chama funcao Pnuts com parametros. 🟢 |
| `MPnuts.evalFunc` | `(pnut: String, key: Object)` | `PnutsFunction` | Avalia texto que deve retornar `PnutsFunction`. 🟢 |
| `MPnuts.evalCondition` | `(cond: String)` | `boolean` | Avalia condicao Pnuts exigindo Boolean. 🟢 |
| `MPnuts.evalValues` | `(str: String)` | `String` | Expande marcadores `[ ... ]` com variaveis e expressoes. 🟢 |
| `MPnuts.setRow` | `(row: MRow)` | `void` | Injeta/remove `row` em `zemc` e contexto. 🟢 |
| `MPnuts.setForm` | `(form: MForm)` | `void` | Injeta/remove `form` e alias `m`. 🟢 |
| `MPnuts.evalPnut` | `(iob, itc, icomp, ifase, check)` | `Object` | Resolve hook por chave e executa com fallback opcional `*`. 🟢 |
| `MPnuts.evalFormPnut` | `(form: MForm, icomp: String, ifase: String)` | `Object` | Executa hook de sistema e depois hook de formulario. 🟢 |
| `MPnuts.evalFieldPnut` | `(form: MForm, icomp: String, ifase: String)` | `Object` | Executa hook de sistema e tenta hook especifico/generico de campo. 🟢 |
| `MPnuts.evalRowPnut` | `(row: MRow, icomp: String, ifase: String)` | `Object` | Executa hook de sistema e depois hook de linha. 🟢 |
| `MPnuts.loadFunction` | `(name: String)` | `PnutsFunction` | Converte nome em chave, busca/compila funcao e usa cache. 🟢 |
| `MPnutsExp` | `(prefix: String, mjex: MHashtable)` | instancia `MPnutsExp` | Converte metadado `MJEX` em funcao `MIN_` ou `CLI_`. 🟢 |
| `MPnutsPackage.defined` | `(var: String)` | `boolean` | Consulta pacote e tenta carregamento dinamico de funcao ausente. 🟢 |
| `MCallPnuts.execute` | `()` | `void` | Executa script Pnuts de comando com autorizacao, rows, cursor e transacao. 🟢 |
| `Minimal.pnut` | script global | funcoes Pnuts | Biblioteca padrao de utilitarios, SQL, erros, validacao, moeda e email. 🟢 |
| `MPackage.pnut` | template por pacote | funcoes Pnuts | Adiciona `listPackage`, `evalValues`, `evalFormatedValues`, `getIorgx`. 🟢 |

### Chaves e dados

| Dado | Formato | Uso | Evidencia |
|------|---------|-----|-----------|
| Hook key | `IOB+ITC+ICOMP+IFASE` | Indexa expressoes dinamicas em cache e `MMData.getJExp`. | `PT/Minimal/pnuts/MPnutsExp.java:280` 🟢 |
| Prefixos | `MIN_`, `CLI_` | Guardam variantes de funcao por origem/cliente. | `PT/Minimal/pnuts/MPnutsExp.java:70` 🟢 |
| Tipos de contexto | `FM`, `AT`, `MR`, `MC` | Formulario, atributo, linha e codigo manual/comando. | `PT/Minimal/pnuts/MPnuts.java:41` 🟢 |
| Objeto sistema | `EEEE0` | Hook global antes de hooks especificos. | `PT/Minimal/pnuts/MPnuts.java:45` 🟢 |
| Globais | `zglobals`, `Z` | Estado global compartilhado entre pacotes. | `PT/Minimal/pnuts/MPnuts.java:78`, `PT/Minimal/pnuts/MPnuts.java:130` 🟢 |
| Locais | `zemc`, `X` | Estado local do pacote/entidade. | `PT/Minimal/pnuts/MPnuts.java:119` 🟢 |

## Fluxo Principal

### Bootstrap global

1. `MPnuts` inicia com `started=false`, `trace=false` e constantes de scripts base. 🟢 `PT/Minimal/pnuts/MPnuts.java:23`
2. `initMPnuts()` verifica `started`; se falso, seta `started=true`, cria `zglobals`, alias `Z`, registry `mpnuts` e `initContext`. 🟢 `PT/Minimal/pnuts/MPnuts.java:75`
3. Propriedade `PT.Minimal.pnutsTrace` define `trace` e chama `Pnuts.setVerbose(trace)`. 🟢 `PT/Minimal/pnuts/MPnuts.java:82`
4. `Pnuts.load("init.pnut", initContext)` e `Pnuts.load("PT/Minimal/pnuts/Minimal.pnut", initContext)` carregam biblioteca base. 🟢 `PT/Minimal/pnuts/MPnuts.java:89`
5. `MPackage.pnut` e lido como bytes do classpath e guardado em `_MPackage`. 🟢 `PT/Minimal/pnuts/MPnuts.java:93`
6. Qualquer erro no bootstrap mostra `MError` e finaliza processo. 🟢 `PT/Minimal/pnuts/MPnuts.java:96`

### Criacao de pacote

1. Construtor `MPnuts(pkg)` chama `initMPnuts()`. 🟢 `PT/Minimal/pnuts/MPnuts.java:55`
2. A instancia e registrada em `mpnuts` sob `sPackage`. 🟢 `PT/Minimal/pnuts/MPnuts.java:58`
3. `initPackage()` cria `zemc`, alias `X`, `MPnutsPackage`, `Context`, e injeta `zemc`, `X`, `zglobals`, `Z`. 🟢 `PT/Minimal/pnuts/MPnuts.java:117`
4. `eval(_MPackage)` executa template por pacote, adicionando funcoes auxiliares. 🟢 `PT/Minimal/pnuts/MPnuts.java:137`

### Execucao de hook

1. Chamador invoca `evalFormPnut`, `evalFieldPnut`, `evalRowPnut` ou `evalPnut`. 🟢
2. Chave e montada via `MPnutsExp.getKey(iob,itc,icomp,ifase)`. 🟢 `PT/Minimal/pnuts/MPnuts.java:912`
3. `getExp(key)` converte chave para nome de cache (`keyToName`) e procura em `cache`. 🟢 `PT/Minimal/pnuts/MPnuts.java:846`
4. Se ausente, busca metadado por `MMData.getJExp((String)key)`. 🟢 `PT/Minimal/pnuts/MPnuts.java:856`
5. Se metadado ausente, grava `_NULL` para nome base, `MIN_` e `CLI_`. 🟢 `PT/Minimal/pnuts/MPnuts.java:858`
6. Se metadado existe, avalia expressoes `MIN`, `CLI` e wrapper principal por `evalFunc`, guarda no cache e retorna funcao. 🟢 `PT/Minimal/pnuts/MPnuts.java:864`
7. `evalPnut` exige `PnutsFunction`; se valido, executa `eval(f)`. 🟢 `PT/Minimal/pnuts/MPnuts.java:892`

### Execucao via comando `MCallPnuts`

1. `execute()` bloqueia usuarios cujo tipo contem `CS`. 🟢 `PT/Minimal/actions/MCallPnuts.java:43`
2. Obtem pacote Pnuts da entidade do formulario. 🟢 `PT/Minimal/actions/MCallPnuts.java:47`
3. Resolve argumentos de `callerArgs` ou `args` do comando. 🟢 `PT/Minimal/actions/MCallPnuts.java:49`
4. Em formulario `SINGLEROW`, copia row do formulario, move valores da UI para record e usa essa row como selecao. 🟢 `PT/Minimal/actions/MCallPnuts.java:60`
5. Injeta `form`, `command`, `mcursor` e `wrows` no pacote. 🟢 `PT/Minimal/actions/MCallPnuts.java:71`
6. Em modo `SEL`, itera linhas, injeta `row`, abre transacao, avalia script e comita por linha. 🟢 `PT/Minimal/actions/MCallPnuts.java:81`
7. Fora de `SEL`, abre uma transacao, avalia script uma vez e comita. 🟢 `PT/Minimal/actions/MCallPnuts.java:90`
8. Se ultimo argumento nao for `NOOK`, mostra sucesso `MIN-000000M`. 🟢 `PT/Minimal/actions/MCallPnuts.java:95`
9. Em `finally`, faz rollback pendente, fecha cursor e remove `mcursor`/`wrows`. 🟢 `PT/Minimal/actions/MCallPnuts.java:101`

## Fluxos Alternativos

- **Condicao vazia:** `evalCondition(null)` ou string vazia retorna `true`. 🟢 `PT/Minimal/pnuts/MPnuts.java:512`
- **Resultado nao booleano:** `evalCondition` lanca `MError("Condição inválida...")`. 🟢 `PT/Minimal/pnuts/MPnuts.java:519`
- **Placeholder `pnuts:`:** `evalVar` avalia expressao apos prefixo e substitui `null` por `""`. 🟢 `PT/Minimal/pnuts/MPnuts.java:647`
- **Placeholder com formato numerico/data/string:** `evalVar` aplica comprimento, extenso, numero, data, lower/upper ou trim. 🟢 `PT/Minimal/pnuts/MPnuts.java:676`
- **Hook especifico ausente:** `evalPnut(..., check=true)` tenta chave com `iob="*"`. 🟢 `PT/Minimal/pnuts/MPnuts.java:948`
- **Campo com hook especifico por form ausente:** `evalFieldPnut` tenta `icomp + "_" + formName`; se ausente, tenta `icomp`. 🟢 `PT/Minimal/pnuts/MPnuts.java:1006`
- **Funcao nao carregavel por nome:** `loadFunction` retorna `null` para nome que nao vira chave; pode lancar `MIN-999999E` se estado inconsistente. 🟢 `PT/Minimal/pnuts/MPnuts.java:1035`
- **Execucao sem rows em `MCallPnuts`:** se nao houver rows e segundo argumento nao for `NONE`, lanca `MIN-000103W`. 🟢 `PT/Minimal/actions/MCallPnuts.java:68`

## Dependencias

- `pnuts.lang.Context`, `Package`, `Pnuts`, `PnutsFunction`, `PnutsException`, `Property`: runtime de scripting. 🟢
- `PT.Minimal.geral.MHashtable`: mapas com comportamento customizado para globals, locals e cache. 🟢
- `PT.Minimal.geral.MMData`: fonte de expressoes `MJEX` via `getJExp` e limpeza por `clearJExps`. 🟢
- `PT.Minimal.geral.MForm`, `MBrowse`, `MRow`: objetos expostos para hooks de formulario, browse e linha. 🟢
- `PT.Minimal.geral.MConnection`, `MCursor`: conexao, cursor e transacao para comando Pnuts e funcoes SQL em `Minimal.pnut`. 🟢
- `PT.Minimal.util.MDate`, `MNumberUtil`, `MString`: formatacao e conversoes usadas em `evalVar` e `Minimal.pnut`. 🟢
- JavaMail/JAF (`javax.mail`, `javax.activation`): envio de email em `Minimal.pnut`. 🟢
- Metadados externos `mjext0`/equivalentes: fonte real de parte dos hooks, conforme validacao humana; outra parte dos scripts existe fisicamente no diretorio de instalacao. Dump/DDL/conteudo completo nao esta no workspace. 🟢 para fonte hibrida; 🔴 para inventario ausente.

## Decisoes de Design Identificadas

| Decisao | Evidencia no codigo | Confianca |
|---------|---------------------|-----------|
| Runtime Pnuts e singleton global por processo. | `PT/Minimal/pnuts/MPnuts.java:75`, `PT/Minimal/pnuts/MPnuts.java:267` | 🟢 |
| Cada entidade/contexto recebe pacote Pnuts separado com estado local `zemc`. | `PT/Minimal/pnuts/MPnuts.java:55`, `PT/Minimal/pnuts/MPnuts.java:117` | 🟢 |
| Estado global e compartilhado via `zglobals`/`Z`. | `PT/Minimal/pnuts/MPnuts.java:78`, `PT/Minimal/pnuts/MPnuts.java:130` | 🟢 |
| Expressoes de metadados sao transformadas em funcoes Pnuts com imports padrao embutidos. | `PT/Minimal/pnuts/MPnutsExp.java:37`, `PT/Minimal/pnuts/MPnutsExp.java:74` | 🟢 |
| Variantes `MIN_` e `CLI_` permitem sobreposicao por camada/cliente. | `PT/Minimal/pnuts/MPnutsExp.java:70`, `PT/Minimal/pnuts/MPnuts.java:864` | 🟢 |
| Hooks globais de sistema rodam antes de hooks especificos. | `PT/Minimal/pnuts/MPnuts.java:976`, `PT/Minimal/pnuts/MPnuts.java:1000`, `PT/Minimal/pnuts/MPnuts.java:1022` | 🟢 |
| Ausencia de hook tambem e cacheada. | `PT/Minimal/pnuts/MPnuts.java:858` | 🟢 |
| `MCallPnuts` executa scripts com transacao JDBC propria. | `PT/Minimal/actions/MCallPnuts.java:85`, `PT/Minimal/actions/MCallPnuts.java:90` | 🟢 |

## Estado Interno

| Campo | Classe | Tipo | Evolucao |
|-------|--------|------|----------|
| `started` | `MPnuts` | `boolean static` | `false` inicial; vira `true` no bootstrap; volta `false` em `clearAll`. 🟢 |
| `trace` | `MPnuts` | `boolean static` | Derivado de `PT.Minimal.pnutsTrace`; controla verbose Pnuts e logs do pacote. 🟢 |
| `_MPackage` | `MPnuts` | `String static` | Template carregado de `MPackage.pnut`; reavaliado em cada pacote. 🟢 |
| `initContext` | `MPnuts` | `Context static` | Contexto usado no load dos scripts base. 🟢 |
| `mpnuts` | `MPnuts` | `MHashtable static` | Registry de pacotes por nome. 🟢 |
| `zglobals`/`Z` | `MPnuts` | `MHashtable static` | Estado global compartilhado. 🟢 |
| `zemc`/`X` | `MPnuts` | `MHashtable` | Estado local do pacote; recebe row, form e variaveis de execucao. 🟢 |
| `pContext` | `MPnuts` | `Context` | Contexto Pnuts da instancia. 🟢 |
| `pPackage` | `MPnuts` | `Package` | Pacote Pnuts customizado. 🟢 |
| `cache` | `MPnuts` | `MHashtable` | Cache por nome de funcao; guarda `PnutsFunction` ou `_NULL`. 🟢 |
| `key/name/fmin/fcli/func` | `MPnutsExp` | strings/objeto | Representam metadado compilavel em funcoes Pnuts. 🟢 |
| `wargs/wrows/mpnut` | `MCallPnuts` | args, rows, pacote | Estado temporario de execucao de comando. 🟢 |

## Observabilidade

- Bootstrap falho imprime `Erro durante o load do init.pnut e/ou Minimal.pnut!` em stderr, mostra `MError` e encerra. 🟢 `PT/Minimal/pnuts/MPnuts.java:96`
- Trace Pnuts e habilitado por `PT.Minimal.pnutsTrace`; `MPnutsPackage` imprime `defined`, `set`, `get`, `clear`, `keys`, `elements`. 🟢 `PT/Minimal/pnuts/MPnutsPackage.java`
- `MCallPnuts` imprime argumentos do comando em stdout com prefixo `!*** MCalPnuts CommandArgs`. 🟢 `PT/Minimal/actions/MCallPnuts.java:56`
- Funcoes desatualizadas em `Minimal.pnut` imprimem aviso antes de delegar ou falhar. 🟢 `PT/Minimal/pnuts/Minimal.pnut:395`
- Nao ha logger estruturado, metricas, auditoria de script executado ou tracing transacional identificados. 🟢

## Riscos e Lacunas

- 🟢/🔴 Scripts reais por entidade/hook sao carregados de duas fontes confirmadas pelo usuario: arquivos fisicos no diretorio de instalacao e registros no banco `mjext0`; paridade comportamental completa exige exportar ambas as fontes.
- 🔴 Versao da biblioteca Pnuts e dependencias JavaMail/JAF nao estao versionadas no repositorio.
- 🟢 `clearAll()` percorre `mpnuts.keys()` mas limpa campos de instancia sem qualificar corretamente no trecho decompilado, sugerindo risco de bug/decompilacao.
- 🟢 `MPnutsExp.getKey(..., cfunc)` ignora `cfunc` na chave final; funcoes diferentes no mesmo `(iob,itc,icomp,ifase)` podem colidir se metadados permitirem multiplas funcoes.
- 🟢 `MCallPnuts` no fonte analisado contem sinais de decompilacao quebrada (`import PT.Minimal.pnuts.;`, `Vector wrows;mpnut;`, `.getMPnut`), exigindo validacao contra fonte original antes de recompilar.
- 🟢 Scripts podem executar SQL, email e acesso a propriedades globais; controle de seguranca observado e concentrado no bloqueio de usuario `CS` para comando manual, nao em sandbox Pnuts.
