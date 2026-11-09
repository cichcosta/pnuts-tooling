# Perguntas para Validacao — MinimalJ

> Gerado pelo Revisor em 2026-05-22.
> Responda no chat ou preencha o campo **Resposta** de cada pergunta e avise quando terminar.

---

## Pergunta 1

✅ Respondida

**Contexto:** Autenticacao, autorizacao e permissoes dependem das tabelas `suact0`, `ssact0` e `bctxt0`, mas o workspace nao contem DDL ou dados de referencia.
**Spec afetada:** `_reversa_sdd/autenticacao-e-login/requirements.md`, `_reversa_sdd/permissions.md`
**Pergunta:** Voce tem DDL/export dessas tabelas e o catalogo dos valores de `cut`, `cns`, `isac`, `isact` e `tsac`? Se nao tiver, quais valores devem ser considerados obrigatorios alem de `CS`, `AG`, `AE`, `AL` e `DN`?
**Impacto:** Fecha a politica real de login/permissao. Sem isso, a reimplementacao so consegue reproduzir o mecanismo, nao as regras reais por usuario/grupo/entidade.

**Resposta:** não tenho

---

## Pergunta 2

✅ Respondida

**Contexto:** Execucao de comandos e CRUD dependem de metadados `MCommand`, `ZFase`, argumentos, proximos comandos e procedures `<ENTIDADE>PZ`.
**Spec afetada:** `_reversa_sdd/execucao-de-acoes-e-crud/requirements.md`, `_reversa_sdd/execucao-de-acoes-e-crud/tasks.md`
**Pergunta:** Existe export de comandos por entidade com classe executora, tipo, nivel, argumentos, `ZFase`, proximo comando e mensagens? Se existir, qual arquivo/fonte devemos usar?
**Impacto:** Sem esse catalogo, a spec cobre o motor de comandos, mas nao a lista operacional de comandos que precisa existir no sistema novo.

**Resposta:** não sei

---

## Pergunta 3

✅ Respondida

**Contexto:** Regras dinamicas podem viver em scripts Pnuts por entidade/hook e em metadados `MJEX`.
**Spec afetada:** `_reversa_sdd/scripts-e-regras-dinamicas-pnuts/requirements.md`, `_reversa_sdd/execucao-de-acoes-e-crud/design.md`
**Pergunta:** Ha uma pasta/export completo de scripts Pnuts de producao e metadados `MJEX` para `(iob,itc,icomp,ifase,cfunc,text)`? Se nao houver, o sistema novo deve manter motor de scripts generico ou congelar apenas os hooks visiveis no codigo?
**Impacto:** Decide se a reimplementacao precisa preservar extensibilidade dinamica ou pode converter regras conhecidas para codigo estatico.

**Resposta:** Alguns scripts existem fisicamentes em diretorio onde a aplicação foi instalado, outros estão no banco de dados da tabela mjext0  e são carregados ao iniciar a aplicação.

---

## Pergunta 4

✅ Respondida

**Contexto:** Runtime depende de Java legado, Pnuts e drivers JDBC externos que nao estao versionados; ODBC usa `sun.jdbc.odbc.JdbcOdbcDriver`.
**Spec afetada:** `_reversa_sdd/conexao-e-drivers-multi-banco/requirements.md`, `_reversa_sdd/dependencies.md`
**Pergunta:** Qual runtime alvo deve ser considerado: preservar Java legado compativel com JDBC-ODBC, ou migrar para JDK moderno substituindo ODBC/Pnuts/drivers? Existem JARs e versoes originais?
**Impacto:** Define compatibilidade, build reproduzivel e estrategia de substituicao de drivers.

**Resposta:** preservar

---

## Pergunta 5

✅ Respondida

**Contexto:** `PT/Minimal/util/Mies.java` implementa geracao XML IES/DGCI via JAXB, mas o plano de units nao gerou uma spec canônica para integracao fiscal.
**Spec afetada:** `_reversa_sdd/domain.md`, `_reversa_sdd/architecture.md`, possivel nova unit `_reversa_sdd/integracao-fiscal-ies/`
**Pergunta:** A integracao fiscal IES/DGCI deve ser tratada como feature obrigatoria do sistema novo? Se sim, existe fluxo/tela/dados reais alem do exemplo em `Mies.java`?
**Impacto:** Pode exigir nova unit SDD; se ignorada, uma capacidade fiscal confirmada no codigo fica fora do contrato executavel.

**Resposta:** Não.

---

## Pergunta 6

✅ Respondida

**Contexto:** RMI contem `HelloServer`, `HelloClient` e host fixo `194.65.54.218`, mas sem evidência de uso no fluxo principal.
**Spec afetada:** `_reversa_sdd/integracao-rmi-distribuidos/requirements.md`, `_reversa_sdd/integracao-rmi-distribuidos/design.md`
**Pergunta:** RMI era usado em producao ou e apenas exemplo/teste? O host `194.65.54.218` ainda tem significado operacional? Se RMI for mantido, qual politica de seguranca/rede deve existir?
**Impacto:** Decide se a unit RMI deve ser reconstruida, modernizada, isolada ou descartada.

**Resposta:** Não.
