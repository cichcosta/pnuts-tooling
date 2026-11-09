# Autenticacao e Login

## Visao Geral

A unit de autenticacao e login inicializa a sessao do MinimalJ, coleta ou recebe credenciais, abre as conexoes de metadados e dados, carrega identidade/autorizacao do usuario e prepara variaveis globais usadas pelo restante do framework. 🟢 **CONFIRMADO**

O login tambem define o menu inicial, grupo/nodo, organizacao, nivel de autorizacao, tipo de usuario e idioma a partir da tabela `suact0`. 🟢 **CONFIRMADO**

## Responsabilidades

- Exibir dialogo Swing de login quando `user.dsn`, `user.name` e `user.passwd` nao estiverem previamente configurados. 🟢 **CONFIRMADO**
- Aceitar credenciais via propriedades de sistema ou arquivo de propriedades antes de abrir a UI. 🟢 **CONFIRMADO**
- Abrir conexao `JMETADATA` para carregar metadados e conexao `DEFAULT` para sessao/dados. 🟢 **CONFIRMADO**
- Consultar `suact0` para resolver atributos operacionais da sessao. 🟢 **CONFIRMADO**
- Validar autorizacao inicial da entidade antes de abrir a tela principal. 🟢 **CONFIRMADO**
- Executar script Pnuts de variaveis globais de login quando disponivel. 🟢 **CONFIRMADO**
- Permitir troca de senha em Oracle, SQL Server e PostgreSQL. 🟢 **CONFIRMADO**

## Regras de Negocio

- Se `user.dsn`, `user.name` e `user.passwd` ja estiverem definidos, o sistema executa `doLogin()` diretamente sem exigir dialogo visual. 🟢 **CONFIRMADO**
- Quando nao ha DSN configurado, o sistema le propriedades, monta um dialogo `Menu Login` e solicita usuario, senha e DSN. 🟢 **CONFIRMADO**
- O login normal consulta `suact0` com `iutl = lower(USER)` e validade entre `dini` e `dfim`. 🟢 **CONFIRMADO**
- Se a consulta de `suact0` falhar e o usuario nao for `minimal`, o sistema deve bloquear o login com erro `MIN-009997E`. 🟢 **CONFIRMADO**
- Se o usuario for `minimal` e nao houver registro valido em `suact0`, o nivel de autorizacao deve ser assumido como `"9"`. 🟢 **CONFIRMADO**
- O usuario `minimal` ignora a autorizacao normal em `MCheckAut.checkAut`. 🟢 **CONFIRMADO**
- A entidade inicial so pode abrir se `MCheckAut.checkAut(entidade, "*", "1")` permitir. 🟢 **CONFIRMADO**
- A autorizacao combina nivel de acesso (`cns`), usuario, grupo/nodo e regras `AG`/`AE` da tabela `ssact0`. 🟢 **CONFIRMADO**
- Regras `DN` invertem o resultado de permissao, funcionando como negativa explicita. 🟢 **CONFIRMADO**
- A mudanca de senha so e suportada quando o banco DEFAULT e Oracle, SQL Server ou PostgreSQL. 🟢 **CONFIRMADO**
- A semantica completa de todos os valores de `cut`, `cns`, `isac`, `isact` e `tsac` depende dos metadados reais do banco; o usuario confirmou nao possuir DDL/export dessas tabelas nesta etapa. 🔴 **LACUNA**

## Requisitos Funcionais

| ID | Requisito | Prioridade | Criterio de Aceite |
|----|-----------|------------|--------------------|
| RF-01 | Carregar propriedades de login a partir de arquivo/propriedades de sistema antes de exibir UI. | Must | Dado um `PT.Minimal.propfile` valido, quando `loadLoginProperties()` executa, entao `MUtil.myProp` contem `user.dsn`, `user.name` e/ou demais chaves encontradas. |
| RF-02 | Autenticar automaticamente quando DSN, usuario e senha ja existem nas propriedades. | Must | Dado `user.dsn`, `user.name` e `user.passwd`, quando `showLogin()` executa, entao `doLogin()` e chamado sem abrir o dialogo de login. |
| RF-03 | Exibir dialogo Swing de login quando nao houver DSN configurado. | Should | Dado ausencia de `user.dsn`, quando `showLogin(MFrame)` executa, entao o dialogo `Menu Login` mostra campos `user`, `passwd` e `dsn`. |
| RF-04 | Abrir conexoes `JMETADATA` e `DEFAULT` durante o login. | Must | Quando `doLogin()` executa, entao `doMetaDataConnection()` e `doDefaultConnection()` sao chamados antes de marcar `logged = true`. |
| RF-05 | Popular atributos de sessao a partir de `suact0`. | Must | Dado registro valido em `suact0`, quando `doDefaultConnection()` executa, entao menu, organizacao, grupo, nivel, tipo e idioma sao copiados para o estado estatico de `MLogin`. |
| RF-06 | Bloquear usuario comum sem registro valido em `suact0`. | Must | Dado usuario diferente de `minimal` sem registro valido, quando `doDefaultConnection()` executa, entao erro `MIN-009997E` e lancado. |
| RF-07 | Aplicar bypass administrativo para usuario `minimal`. | Must | Dado usuario `minimal`, quando `MCheckAut.checkAut()` executa, entao retorna `true` sem carregar regras de `ssact0`. |
| RF-08 | Validar permissao inicial antes de preparar a entidade principal. | Must | Dado entidade inicial sem permissao, quando `MinMain.run()` executa, entao erro `MIN-009998E` e lancado antes de `MPrepareEnt`. |
| RF-09 | Suportar troca de senha nos bancos permitidos. | Should | Dado banco DEFAULT Oracle, SQL Server ou PostgreSQL, quando a senha atual confere e a nova senha foi repetida corretamente, entao o comando SQL de alteracao de senha e executado. |

## Requisitos Nao Funcionais

| Tipo | Requisito inferido | Evidencia no codigo | Confianca |
|------|--------------------|---------------------|-----------|
| Seguranca | Autorizacao inicial obrigatoria por entidade antes de abrir a tela principal. | `PT/Minimal/geral/MinMain.java:51` | 🟢 CONFIRMADO |
| Seguranca | Usuario comum sem sessao valida em `suact0` nao deve autenticar. | `PT/Minimal/geral/MLogin.java:332`, `PT/Minimal/geral/MLogin.java:374` | 🟢 CONFIRMADO |
| Seguranca | Autorizacao especial `AE` deve ter precedencia sobre autorizacao geral `AG`. | `PT/Minimal/geral/MCheckAut.java:34`, `PT/Minimal/geral/MCheckAut.java:38` | 🟢 CONFIRMADO |
| Compatibilidade | Troca de senha depende do dialeto do banco e so cobre Oracle, SQL Server e PostgreSQL. | `PT/Minimal/geral/MLogin.java:700` | 🟢 CONFIRMADO |
| Operabilidade | Login em modo metadados pode trabalhar com modos `OFFLINE`, `INLINE`, `METAFILE` e `METAFILEALL`. | `PT/Minimal/geral/MLogin.java:396` | 🟢 CONFIRMADO |

## Criterios de Aceitacao

```gherkin
Cenario: Login automatico com propriedades completas
Dado que user.dsn, user.name e user.passwd estao definidos
Quando o MinimalJ chama showLogin
Entao o sistema executa doLogin sem abrir o dialogo Menu Login
E as conexoes JMETADATA e DEFAULT sao inicializadas

Cenario: Login visual sem DSN pre-configurado
Dado que user.dsn nao esta definido
Quando o MinimalJ chama showLogin com um MFrame
Entao o sistema exibe o dialogo Menu Login
E solicita usuario, senha e DSN

Cenario: Usuario comum sem sessao valida
Dado que o usuario nao e minimal
E nao existe registro valido em suact0 para lower(USER)
Quando o sistema executa doDefaultConnection
Entao o login falha com erro MIN-009997E

Cenario: Usuario minimal sem registro de sessao
Dado que o usuario e minimal
E nao existe registro valido em suact0
Quando o sistema executa doDefaultConnection
Entao o usuario recebe nivel de autorizacao 9
E MCheckAut permite as operacoes verificadas por checkAut

Cenario: Entidade inicial sem permissao
Dado que checkAut retorna falso para a entidade inicial
Quando MinMain tenta preparar a tela principal
Entao o sistema lanca MIN-009998E
E nao executa MPrepareEnt para essa entidade
```

## Prioridade (MoSCoW)

| Requisito | MoSCoW | Justificativa |
|-----------|--------|---------------|
| Abrir conexoes `JMETADATA` e `DEFAULT` | Must | Sem conexoes nao ha metadados, sessao, autorizacao ou dados operacionais. 🟢 |
| Validar `suact0` e carregar identidade | Must | Define usuario, grupo, nivel, tipo, menu, idioma e organizacao usados globalmente. 🟢 |
| Aplicar `MCheckAut` na entidade inicial | Must | Bloqueia acesso antes da montagem de tela e dados. 🟢 |
| Dialogo visual de login | Should | Necessario quando credenciais nao estao preconfiguradas, mas ha caminho automatico por propriedades. 🟢 |
| Troca de senha | Should | Funcionalidade operacional importante, mas nao e pre-condicao para iniciar sessao. 🟢 |
| Modos offline/metafile de metadados | Could | Suporte condicionado por propriedades, sem evidencias completas de uso no workspace. 🟡 |

## Rastreabilidade de Codigo

| Arquivo | Funcao / Classe | Cobertura |
|---------|-----------------|-----------|
| `PT/Minimal/geral/MLogin.java` | `loadLoginProperties`, `showLogin`, `doLogin`, `doDefaultConnection`, `doMetaDataConnection`, `setGlobalVariables`, troca de senha | 🟢 |
| `PT/Minimal/geral/MCheckAut.java` | `checkAut`, `checkSpecialAut`, `checkAutLine`, `loadAut` | 🟢 |
| `PT/Minimal/geral/MinMain.java` | `run` | 🟢 |
| `PT/Minimal/actions/MHandleCommand.java` | validacao de comandos via `MCheckAut.checkAut` | 🟢 |
| `_reversa_sdd/domain.md` | regras R01, R04 e R05 | 🟢 |
| `_reversa_sdd/permissions.md` | modelo de permissao e matriz de acesso | 🟢 |

## Lacunas Pendentes

| Lacuna | Impacto | Confianca |
|--------|---------|-----------|
| DDL e dados reais de `suact0` e `ssact0` nao estao no workspace. | Nao permite listar usuarios, grupos, niveis e excecoes reais de producao. | 🔴 LACUNA |
| Catalogo completo dos valores de `cut`, `cns`, `isac`, `isact` e `tsac` nao foi encontrado. | Regras de acesso alem de `CS`, `AG`, `AE`, `AL` e `DN` podem existir nos metadados. | 🔴 LACUNA |
| Arquivo de propriedades original de producao nao esta disponivel. | Nao e possivel fechar todos os aliases de DSN e modos de metadados usados em ambiente real. | 🔴 LACUNA |

Nota de validacao humana: em 2026-05-22, o usuario confirmou que nao possui DDL/export de `suact0`, `ssact0`, `bctxt0` nem catalogo adicional dos valores de autorizacao. A spec deve permanecer limitada aos mecanismos e valores confirmados no codigo (`CS`, `AG`, `AE`, `AL`, `DN`) ate surgirem metadados reais.
