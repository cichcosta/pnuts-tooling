# Autenticacao e Login, Design Tecnico

## Interface

### Entradas de Configuracao

| Entrada | Origem | Uso | Confianca |
|---------|--------|-----|-----------|
| `PT.Minimal.propfile` | Propriedade de sistema | Define o arquivo de propriedades a carregar, com fallback para `/Minimal.pro`. | 🟢 CONFIRMADO |
| `PT.Minimal.user.name` | Propriedade de sistema | Sobrescreve `user.name`. | 🟢 CONFIRMADO |
| `PT.Minimal.user.passwd` | Propriedade de sistema | Sobrescreve `user.passwd`. | 🟢 CONFIRMADO |
| `PT.Minimal.user.dsn` | Propriedade de sistema | Sobrescreve `user.dsn` e pode alimentar `user.xdsn`. | 🟢 CONFIRMADO |
| `JMETADATA`, `JMETAUSER`, `JMETAPASSWD` | Propriedades de login | Configuram conexao e credenciais de metadados. | 🟢 CONFIRMADO |
| `JMETADATAMODE` | Propriedades de login | Controla modo online/offline/metafile de metadados. | 🟢 CONFIRMADO |
| `SCHEMA`, `SCHEMAFORCE` | Propriedades de login | Valida coerencia entre schema esperado e DSN escolhido. | 🟢 CONFIRMADO |

### Interface Visual

| Simbolo | Assinatura | Retorno | Observacao |
|---------|------------|---------|------------|
| `MLogin.showLogin()` | `()` | `void` | Cria um `MFrame` e delega para `showLogin(MFrame)`. 🟢 |
| `MLogin.showLogin(MFrame)` | `(MFrame wf)` | `void` | Executa login automatico se credenciais existem; caso contrario monta dialogo Swing. 🟢 |
| `MLogin.showLogin(MFrame, MMenu)` | `(MFrame wf, MMenu wm)` | `void` | Variante usada com menu inicial; tambem permite login automatico. 🟢 |
| `MLogin.doLogin(MForm)` | `(MForm minForm)` | `void` | Extrai campos do formulario e delega para `doLogin(Hashtable)`. 🟢 |
| `MLogin.doLogin(Hashtable)` | `(Hashtable wh)` | `void` | Recebe `user`, `passwd`, `dsn`, resolve alias e chama `doLogin()`. 🟢 |

### Saidas e Estado Publico

| Metodo | Saida | Origem | Confianca |
|--------|-------|--------|-----------|
| `MLogin.getUser()` | Usuario atual | `user.name` | 🟢 CONFIRMADO |
| `MLogin.getUserNodo()` | Grupo/nodo | `suact0.igrp` ou `"0000"` inicial | 🟢 CONFIRMADO |
| `MLogin.getUserAutLevel()` | Nivel de autorizacao | `suact0.cns` ou `"9"` para `minimal` sem registro | 🟢 CONFIRMADO |
| `MLogin.getUserTipo()` | Tipo de usuario | `suact0.cut` | 🟢 CONFIRMADO |
| `MLogin.getUserMenu()` | Menu inicial | `suact0.imen`, default `"J000A"` | 🟢 CONFIRMADO |
| `MLogin.getUserOrg()` | Organizacao | `suact0.iorg` ou parametro `@*ORGDEFAULT` | 🟢 CONFIRMADO |
| `MLogin.getLang()` | Idioma | `suact0.ilan` | 🟢 CONFIRMADO |

## Fluxo Principal

1. `MLogin.loadLoginProperties()` verifica se `user.dsn` ja existe; se nao existir, carrega o arquivo definido por `PT.Minimal.propfile` e aplica propriedades de sistema para usuario, senha e DSN. 🟢 `PT/Minimal/geral/MLogin.java:58`
2. `MLogin.showLogin(...)` decide entre login automatico e dialogo visual. Se `user.dsn`, `user.name` e `user.passwd` existirem, chama `doLogin()` diretamente. 🟢 `PT/Minimal/geral/MLogin.java:154`
3. No caminho visual, o sistema cria campos Swing para `user`, `passwd` e `dsn`, coloca a entidade do formulario como `Menu Login` e aguarda a acao do usuario. 🟢 `PT/Minimal/geral/MLogin.java:169`
4. `doLogin(Hashtable)` copia credenciais para `myProp`, resolve aliases de DSN, define `DEFAULT` e chama `doLogin()`. 🟢 `PT/Minimal/geral/MLogin.java:297`
5. `doLogin()` fecha conexoes antigas, limpa flags `logged` e `METAlogged`, resolve credenciais de metadados e variaveis de ambiente local (`JTEMP`, `JSTART`). 🟢 `PT/Minimal/geral/MLogin.java:239`
6. `doLogin()` chama `doMetaDataConnection()` para abrir `JMETADATA`, criar `MDBProperties` de metadados e ativar modo serializado quando configurado. 🟢 `PT/Minimal/geral/MLogin.java:396`
7. `doLogin()` chama `doDefaultConnection()` para abrir `DEFAULT`, consultar `suact0`, carregar atributos de sessao e criar propriedades do banco/atributos. 🟢 `PT/Minimal/geral/MLogin.java:319`
8. `setGlobalVariables()` valida schema quando `SCHEMAFORCE = S`, inicializa pacote Pnuts `LOGIN` e executa a rotina `LOGIN/MC/VARIABLES/FN`. 🟢 `PT/Minimal/geral/MLogin.java:912`
9. `MinMain.run()` valida permissao para a entidade inicial com `MCheckAut.checkAut(went, "*", "1")` antes de preparar `MPrepareEnt` e montar o `MForm`. 🟢 `PT/Minimal/geral/MinMain.java:28`

## Fluxos Alternativos

- **Login automatico por propriedades:** quando `user.dsn`, `user.name` e `user.passwd` ja existem, o dialogo nao e exibido e `doLogin()` roda diretamente. 🟢
- **Login por alias de DSN:** `doLogin(Hashtable)` aceita um DSN escolhido no formulario e, se ele corresponder a uma chave das propriedades, troca para o valor `x...` associado. 🟢
- **Metadados offline/metafile:** `doMetaDataConnection()` evita conexao `JMETADATA` para modos `OFFLINE`, `METAFILE` e `METAFILEALL`; para `OFFLINE` ou `INLINE`, ativa `MSerializedObject.setMode(true)`. 🟢
- **Usuario `minimal`:** se `suact0` nao retorna registro, o usuario `minimal` recebe nivel `"9"` em vez de erro; em `MCheckAut`, esse usuario retorna autorizado imediatamente. 🟢
- **Falha de usuario comum:** se a consulta `suact0` falha para usuario diferente de `minimal`, o fluxo lanca `MIN-009997E` e nao completa login. 🟢
- **Senha invalida na troca de senha:** senhas vazias, repeticao divergente ou senha atual incorreta geram erros `MIN-CPASS2E`, `MIN-CPASS3E` ou `MIN-CPASS4E`. 🟢

## Dependencias

- `MConnection` e `MCursor`: abertura de conexoes `JMETADATA`/`DEFAULT`, queries em `suact0`, leitura de propriedades e execucao de SQL de troca de senha. 🟢
- `MDBProperties` e `MAtribProperties`: carregamento de propriedades e atributos a partir dos bancos conectados. 🟢
- `MForm`, `MDialog`, `MTextField`, `MChoice`, `MLabel`: construcao da UI Swing de login e troca de senha. 🟢
- `MCheckAut`: validacao de permissao por entidade/comando/nivel depois do login. 🟢
- `MPnuts`: inicializacao do pacote `LOGIN` e execucao de variaveis globais. 🟢
- Tabelas `suact0`, `ssact0` e `bctxt0`: sessao, autorizacao e parametro de organizacao default. 🟢 para uso; 🔴 para schema completo.

## Decisoes de Design Identificadas

| Decisao | Evidencia no codigo | Confianca |
|---------|---------------------|-----------|
| Estado de sessao mantido em campos estaticos de `MLogin`. | `PT/Minimal/geral/MLogin.java:24` | 🟢 CONFIRMADO |
| Login pode ser automatico por propriedades ou visual por Swing. | `PT/Minimal/geral/MLogin.java:154` | 🟢 CONFIRMADO |
| Metadados e dados podem usar credenciais/conexoes separadas (`JMETADATA` e `DEFAULT`). | `PT/Minimal/geral/MLogin.java:239`, `PT/Minimal/geral/MLogin.java:396` | 🟢 CONFIRMADO |
| Autorizacao e carregada sob demanda e cacheada por entidade em `LAut`/`LSpecialAut`. | `PT/Minimal/geral/MCheckAut.java:15`, `PT/Minimal/geral/MCheckAut.java:30` | 🟢 CONFIRMADO |
| Autorizacao especial `AE` decide antes da autorizacao geral `AG`. | `PT/Minimal/geral/MCheckAut.java:34` | 🟢 CONFIRMADO |
| O usuario `minimal` funciona como bypass administrativo. | `PT/Minimal/geral/MCheckAut.java:26` | 🟢 CONFIRMADO |
| A rotina de variaveis globais de login fica em Pnuts. | `PT/Minimal/geral/MLogin.java:912` | 🟢 CONFIRMADO |

## Estado Interno

| Campo | Classe | Evolucao | Confianca |
|-------|--------|----------|-----------|
| `myProp` | `MLogin` | Recebe propriedades de arquivo/sistema/formulario e vira fonte global via `MUtil.myProp`. | 🟢 |
| `wuser` | `MLogin` | Definido a partir de `user.name` durante `doLogin()`. | 🟢 |
| `wuserNodo` | `MLogin` | Comeca como `"0000"` e pode ser substituido por `suact0.igrp`. | 🟢 |
| `wuserAutLevel` | `MLogin` | Vem de `suact0.cns` ou recebe `"9"` para `minimal`. | 🟢 |
| `wuserTipo` | `MLogin` | Vem de `suact0.cut`; usado por comandos de escrita fora desta unit. | 🟢 |
| `wuserMenu` | `MLogin` | Default `"J000A"`, substituido por `suact0.imen`. | 🟢 |
| `wuserOrg` | `MLogin` | Vem de `bctxt0.@*ORGDEFAULT` e pode ser substituido por `suact0.iorg`. | 🟢 |
| `wlang` | `MLogin` | Vem de `suact0.ilan`. | 🟢 |
| `logged` / `METAlogged` | `MLogin` | Resetados no inicio de `doLogin()` e marcados apos conexoes bem-sucedidas. | 🟢 |
| `LAut` / `LSpecialAut` | `MCheckAut` | Hashtables estaticas de cache por entidade para regras `AG` e `AE`. | 🟢 |

## Observabilidade

- O login emite logs no console com DSN, usuario, queries e progresso de conexao. 🟢 `PT/Minimal/geral/MLogin.java:121`, `PT/Minimal/geral/MLogin.java:332`
- `MConnection` tambem imprime produto, motor, URL, isolamento e autocommit ao abrir conexao, o que apoia diagnostico de login. 🟢 `PT/Minimal/geral/MConnection.java`
- Nao ha evidencia de logging estruturado, metricas, auditoria persistente de login ou tracing distribuido. 🔴 **LACUNA**

## Riscos e Lacunas

- 🔴 Sem DDL/dump de `suact0` e `ssact0`, nao e possivel validar tipos, chaves, cardinalidades e politicas reais de autorizacao.
- 🔴 O arquivo de propriedades de producao nao esta no workspace; aliases de DSN, modos de metadados e credenciais esperadas podem estar incompletos.
- 🟡 O cache estatico de autorizacao (`LAut`, `LSpecialAut`) pode ficar obsoleto se regras mudarem no banco durante a sessao; existe `clearAut()`, mas nao foi fechado o gatilho operacional.
- 🟡 Comparacoes de nivel usam `String.compareTo`, assumindo codificacao lexicografica compativel com a semantica de `cns`.
- 🟢 A troca de senha monta SQL por concatenacao com valores digitados, o que deve ser tratado com cuidado em uma reimplementacao.

