# Autenticacao e Login, Tarefas de Implementacao

## Pre-requisitos

- [ ] Runtime Java com suporte a Swing/AWT definido para a nova implementacao. 🟢
- [ ] Abstracao de conexao equivalente a `MConnection` disponivel para abrir `JMETADATA` e `DEFAULT`. 🟢
- [ ] Contrato minimo das tabelas `suact0`, `ssact0` e `bctxt0` levantado ou simulado para testes. 🔴
- [ ] Mecanismo equivalente para scripts/variaveis globais de login definido, caso Pnuts seja preservado. 🟡
- [ ] Catalogo de erros `MIN-*` decidido para compatibilidade ou mapeamento na nova stack. 🟡

## Tarefas

- [ ] T-01, Implementar carregamento de propriedades de login
  - Origem no legado: `PT/Minimal/geral/MLogin.java:58`
  - Criterio de pronto: propriedades de arquivo e propriedades de sistema populam `user.name`, `user.passwd`, `user.dsn`, `user.xdsn` e demais chaves sem abrir UI quando ja existem.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-02, Implementar decisao entre login automatico e login visual
  - Origem no legado: `PT/Minimal/geral/MLogin.java:154`
  - Criterio de pronto: com `user.dsn`, `user.name` e `user.passwd`, o sistema chama login automatico; sem DSN, apresenta formulario com usuario, senha e DSN.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-03, Implementar captura de credenciais do formulario
  - Origem no legado: `PT/Minimal/geral/MLogin.java:292`, `PT/Minimal/geral/MLogin.java:297`
  - Criterio de pronto: campos `user`, `passwd` e `dsn` atualizam propriedades de sessao, resolvem alias de DSN e definem `DEFAULT`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-04, Implementar inicializacao limpa de sessao
  - Origem no legado: `PT/Minimal/geral/MLogin.java:239`
  - Criterio de pronto: conexoes anteriores sao fechadas, flags de login sao reiniciadas, credenciais de metadados sao resolvidas e variaveis `JTEMP`/`JSTART` sao calculadas por sistema operacional.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-05, Implementar conexao de metadados `JMETADATA`
  - Origem no legado: `PT/Minimal/geral/MLogin.java:396`
  - Criterio de pronto: modo normal abre `JMETADATA` com `user.metaname`/`user.metapasswd`; modos `OFFLINE`, `METAFILE` e `METAFILEALL` nao exigem conexao online; modos `OFFLINE`/`INLINE` ativam serializacao local.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-06, Implementar conexao default e carga de sessao
  - Origem no legado: `PT/Minimal/geral/MLogin.java:319`
  - Criterio de pronto: `DEFAULT` abre com `user.dsn`, `user.name`, `user.passwd`; consulta `suact0`; popula menu, organizacao, grupo, nivel, tipo e idioma.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-07, Implementar fallback e bloqueio para usuario sem `suact0`
  - Origem no legado: `PT/Minimal/geral/MLogin.java:374`
  - Criterio de pronto: usuario diferente de `minimal` sem registro valido recebe erro equivalente a `MIN-009997E`; usuario `minimal` recebe nivel `"9"`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-08, Implementar carga de metadados locais/cliente
  - Origem no legado: `PT/Minimal/geral/MLogin.java:383`, `PT/Minimal/geral/MLogin.java:617`
  - Criterio de pronto: metadados de cliente sao carregados ou serializados conforme o modo configurado, sem quebrar login quando o modo offline estiver ativo.
  - Confianca: 🟢 CONFIRMADO para existencia; 🟡 INFERIDO para contrato completo

- [ ] T-09, Implementar variaveis globais de login
  - Origem no legado: `PT/Minimal/geral/MLogin.java:912`
  - Criterio de pronto: `SCHEMAFORCE = S` valida o DSN escolhido contra `SCHEMA`; rotina equivalente a `LOGIN/MC/VARIABLES/FN` e executada ou a lacuna e explicitamente configurada.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-10, Implementar autorizacao por entidade/comando/nivel
  - Origem no legado: `PT/Minimal/geral/MCheckAut.java:21`
  - Criterio de pronto: `minimal` retorna permitido; demais usuarios carregam autorizacoes por entidade e avaliam regras especiais antes das gerais.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-11, Implementar carga e cache de regras `AG` e `AE`
  - Origem no legado: `PT/Minimal/geral/MCheckAut.java:122`
  - Criterio de pronto: regras de `ssact0` sao consultadas para entidade exata, prefixo de quatro caracteres e prefixo de um caractere; resultados sao cacheados por entidade.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-12, Implementar avaliacao de linhas de autorizacao
  - Origem no legado: `PT/Minimal/geral/MCheckAut.java:101`
  - Criterio de pronto: `tsac` e comparado contra usuario e grupo/nodo em minusculas; `DN` inverte o resultado.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-13, Implementar bloqueio de entidade inicial sem permissao
  - Origem no legado: `PT/Minimal/geral/MinMain.java:51`
  - Criterio de pronto: antes de montar a entidade principal, permissao `checkAut(ent, "*", "1")` e obrigatoria; falha gera erro equivalente a `MIN-009998E`.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-14, Implementar troca de senha compativel
  - Origem no legado: `PT/Minimal/geral/MLogin.java:699`
  - Criterio de pronto: funcionalidade valida banco suportado, senha atual, nova senha e repeticao; gera SQL por dialeto ou alternativa segura equivalente para Oracle, SQL Server e PostgreSQL.
  - Confianca: 🟢 CONFIRMADO

- [ ] T-15, Isolar estado global de sessao para testabilidade
  - Origem no legado: `PT/Minimal/geral/MLogin.java:24`
  - Criterio de pronto: nova implementacao oferece um objeto/contexto de sessao testavel que preserve os getters comportamentais do legado.
  - Confianca: 🟡 INFERIDO

## Tarefas de Teste

- [ ] TT-01, Teste do login automatico com propriedades completas
  - Origem no legado: `PT/Minimal/geral/MLogin.java:154`
  - Criterio de pronto: confirma que o dialogo nao abre e que `doLogin` inicializa `JMETADATA` e `DEFAULT`.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-02, Teste do login visual sem DSN pre-configurado
  - Origem no legado: `PT/Minimal/geral/MLogin.java:169`
  - Criterio de pronto: confirma campos `user`, `passwd`, `dsn` e traducao para propriedades de sessao.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-03, Teste de usuario comum sem registro em `suact0`
  - Origem no legado: `PT/Minimal/geral/MLogin.java:374`
  - Criterio de pronto: retorna erro equivalente a `MIN-009997E` e nao marca sessao como logada.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-04, Teste de bypass do usuario `minimal`
  - Origem no legado: `PT/Minimal/geral/MCheckAut.java:26`
  - Criterio de pronto: `checkAut` retorna permitido e usuario sem `suact0` recebe nivel `"9"`.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-05, Teste de autorizacao especial `AE` com negativa `DN`
  - Origem no legado: `PT/Minimal/geral/MCheckAut.java:34`, `PT/Minimal/geral/MCheckAut.java:72`
  - Criterio de pronto: regra especial decide antes de regra geral e `DN` inverte permissao.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-06, Teste de bloqueio da entidade inicial
  - Origem no legado: `PT/Minimal/geral/MinMain.java:51`
  - Criterio de pronto: quando `checkAut` falha, `MPrepareEnt` nao e chamado.
  - Confianca: 🟢 CONFIRMADO

- [ ] TT-07, Teste de troca de senha por dialeto
  - Origem no legado: `PT/Minimal/geral/MLogin.java:790`
  - Criterio de pronto: Oracle, SQL Server e PostgreSQL geram comandos equivalentes; bancos nao suportados geram erro equivalente a `MIN-CPASS1E`.
  - Confianca: 🟢 CONFIRMADO

## Tarefas de Migracao de Dados

- [ ] TM-01, Extrair DDL e amostras anonimizadas de `suact0`
  - Origem no legado: `PT/Minimal/geral/MLogin.java:332`
  - Criterio de pronto: campos usados pelo login possuem tipos, obrigatoriedade, indices e valores exemplo documentados.
  - Confianca: 🔴 LACUNA

- [ ] TM-02, Extrair DDL e amostras anonimizadas de `ssact0`
  - Origem no legado: `PT/Minimal/geral/MCheckAut.java:128`
  - Criterio de pronto: regras `AG`, `AE`, `AL`, `DN`, niveis `cns` e formato de `tsac` estao documentados.
  - Confianca: 🔴 LACUNA

- [ ] TM-03, Extrair parametros de `bctxt0`
  - Origem no legado: `PT/Minimal/geral/MLogin.java:339`
  - Criterio de pronto: parametro `@*ORGDEFAULT` e demais parametros de login relevantes foram mapeados.
  - Confianca: 🔴 LACUNA

## Ordem Sugerida

1. Implementar propriedades e decisao de login (`T-01` a `T-04`), porque todo fluxo depende da sessao inicial.
2. Implementar conexoes e carga de sessao (`T-05` a `T-08`), com mocks de banco enquanto DDL real nao estiver disponivel.
3. Implementar variaveis globais e autorizacao (`T-09` a `T-13`), garantindo equivalencia dos casos `minimal`, `AG`, `AE` e `DN`.
4. Implementar troca de senha (`T-14`) depois do contexto de banco estar estavel.
5. Refatorar estado global para um contexto testavel (`T-15`) preservando compatibilidade comportamental.
6. Executar testes (`TT-01` a `TT-07`) e bloquear homologacao enquanto `TM-01` a `TM-03` nao forem resolvidas.

## Lacunas Pendentes (🔴)

- DDL real e dados de referencia de `suact0`, `ssact0` e `bctxt0`.
- Catalogo completo de codigos `cns`, `cut`, `isac`, `isact` e formato de `tsac`.
- Arquivo de propriedades original com aliases de DSN, modos `JMETADATAMODE` e credenciais de metadados.
- Script/metadado real da rotina `LOGIN/MC/VARIABLES/FN` quando nao estiver embutido nos `.pnut` versionados.

