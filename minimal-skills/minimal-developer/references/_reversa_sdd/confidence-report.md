# Relatorio de Confianca — MinimalJ

> Gerado pelo Revisor em 2026-05-22.
> Status: final apos processamento das respostas do usuario.

---

## Resumo Geral

| Nivel | Quantidade | Percentual |
|-------|------------|------------|
| 🟢 CONFIRMADO | 961 | 90.0% |
| 🟡 INFERIDO | 29 | 2.7% |
| 🔴 LACUNA | 78 | 7.3% |
| **Total** | 1068 | 100% |

**Confianca geral:** 91.3% (formula: 🟢 + metade dos 🟡).

## Por Spec

| Spec | 🟢 | 🟡 | 🔴 | Confianca |
|------|----|----|----|-----------|
| `_reversa_sdd/autenticacao-e-login/requirements.md` | 35 | 1 | 4 | 88.8% |
| `_reversa_sdd/autenticacao-e-login/design.md` | 60 | 2 | 4 | 92.4% |
| `_reversa_sdd/autenticacao-e-login/tasks.md` | 23 | 4 | 5 | 78.1% |
| `_reversa_sdd/conexao-e-drivers-multi-banco/requirements.md` | 45 | 1 | 3 | 92.9% |
| `_reversa_sdd/conexao-e-drivers-multi-banco/design.md` | 72 | 3 | 4 | 93.0% |
| `_reversa_sdd/conexao-e-drivers-multi-banco/tasks.md` | 27 | 1 | 7 | 78.6% |
| `_reversa_sdd/editor-lexico-e-componentes-graficos/requirements.md` | 50 | 0 | 0 | 100.0% |
| `_reversa_sdd/editor-lexico-e-componentes-graficos/design.md` | 76 | 0 | 1 | 98.7% |
| `_reversa_sdd/editor-lexico-e-componentes-graficos/tasks.md` | 26 | 0 | 5 | 83.9% |
| `_reversa_sdd/execucao-de-acoes-e-crud/requirements.md` | 49 | 0 | 5 | 90.7% |
| `_reversa_sdd/execucao-de-acoes-e-crud/design.md` | 88 | 2 | 5 | 93.7% |
| `_reversa_sdd/execucao-de-acoes-e-crud/tasks.md` | 1 | 3 | 6 | 25.0% |
| `_reversa_sdd/scripts-e-regras-dinamicas-pnuts/requirements.md` | 73 | 0 | 5 | 93.6% |
| `_reversa_sdd/scripts-e-regras-dinamicas-pnuts/design.md` | 102 | 0 | 3 | 97.1% |
| `_reversa_sdd/scripts-e-regras-dinamicas-pnuts/tasks.md` | 33 | 0 | 10 | 76.7% |
| `_reversa_sdd/integracao-rmi-distribuidos/requirements.md` | 42 | 1 | 0 | 98.8% |
| `_reversa_sdd/integracao-rmi-distribuidos/design.md` | 50 | 3 | 0 | 97.2% |
| `_reversa_sdd/integracao-rmi-distribuidos/tasks.md` | 20 | 4 | 1 | 88.0% |
| `_reversa_sdd/architecture.md` | 28 | 2 | 0 | 96.7% |
| `_reversa_sdd/domain.md` | 24 | 0 | 4 | 85.7% |
| `_reversa_sdd/permissions.md` | 14 | 0 | 3 | 82.4% |
| `_reversa_sdd/state-machines.md` | 14 | 0 | 3 | 82.4% |

## Resultado da Revisao

- 6 units canônicas revisadas; todas contem `requirements.md`, `design.md` e `tasks.md`.
- Revisao cruzada externa: nao realizada, porque `doc_level = essencial`.
- Matrizes globais `code-spec-matrix.md` e `spec-impact-matrix.md`: nao exigidas no nivel essencial.
- Perguntas geradas: 6.
- Perguntas respondidas: 6.
- Reclassificacoes/decisoes registradas: 5 grupos de ajuste.

## Respostas Incorporadas

| Pergunta | Resultado | Efeito |
|----------|-----------|--------|
| 1 | Usuario nao possui DDL/export de auth/permissoes. | Lacuna mantida; escopo limitado aos valores confirmados no codigo. |
| 2 | Usuario nao sabe se existe export de comandos. | Lacuna mantida; motor especificado, catalogo operacional pendente. |
| 3 | Scripts Pnuts existem no diretorio de instalacao e em `mjext0`. | Arquitetura hibrida de scripts confirmada; inventario completo segue pendente. |
| 4 | Runtime alvo deve preservar Java legado. | Direcao arquitetural confirmada para compatibilidade JDBC-ODBC/Pnuts legado. |
| 5 | IES/DGCI nao e feature obrigatoria do sistema novo. | Integracao fiscal mantida como capacidade legada documentada, fora do contrato SDD obrigatorio. |
| 6 | RMI nao e requisito produtivo a preservar. | Unit RMI reclassificada como exemplo/teste legado, fora do escopo obrigatorio. |

## Lacunas Pendentes 🔴

### Autenticacao e Permissoes
- DDL/dados reais de `suact0`, `ssact0` e `bctxt0` ausentes; politica real por usuario, grupo, entidade e nivel segue incompleta.
  - Pergunta correspondente: `questions.md#pergunta-1`

### Comandos e CRUD
- Catalogo real de comandos, argumentos, `ZFase`, proximos comandos e procedures por entidade nao foi exportado.
  - Pergunta correspondente: `questions.md#pergunta-2`

### Scripts Pnuts
- Fonte hibrida foi confirmada, mas faltam export completo do diretorio de scripts de instalacao e da tabela `mjext0`.
  - Pergunta correspondente: `questions.md#pergunta-3`

### Runtime e Drivers
- Direcao de preservar Java legado foi confirmada, mas faltam JARs e versoes originais de Pnuts/JDBC/JavaMail/JAF.
  - Pergunta correspondente: `questions.md#pergunta-4`

## Recomendacoes

- [ ] Para paridade comportamental, priorizar obtencao de dumps/metadados de `suact0`, `ssact0`, `bctxt0`, `MCommand`/`ZFase` e `mjext0`.
- [ ] Localizar diretorios fisicos de scripts Pnuts da instalacao original e anexar ao workspace para nova extracao.
- [ ] Catalogar JARs originais do runtime legado antes de qualquer tentativa de build.
- [ ] Manter IES e RMI como capacidades legadas documentadas, nao como features obrigatorias do sistema novo nesta rodada.

## Historico de Reclassificacoes

| De | Para | Afirmacao | Evidencia |
|----|------|-----------|-----------|
| 🟡/🔴 | 🟢 | RMI nao e requisito produtivo a preservar. | Resposta do usuario em `_reversa_sdd/questions.md#pergunta-6` |
| 🟡 | 🟢 | IES existe no codigo, mas nao e feature obrigatoria do sistema novo. | Resposta do usuario em `_reversa_sdd/questions.md#pergunta-5` |
| 🔴 parcial | 🟢 parcial | Scripts Pnuts usam fonte hibrida: diretorio fisico + banco `mjext0`. | Resposta do usuario em `_reversa_sdd/questions.md#pergunta-3` |
| 🔴 parcial | 🟢 parcial | Runtime alvo deve preservar Java legado. | Resposta do usuario em `_reversa_sdd/questions.md#pergunta-4` |
