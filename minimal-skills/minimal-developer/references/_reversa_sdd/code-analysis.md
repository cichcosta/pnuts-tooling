# Análise Técnica Consolidada (Code Analysis) — MinimalJ

Este documento apresenta a análise profunda e detalhada do sistema legado **MinimalJ**, estruturado sob a granularidade de **Casos de Uso** e construído em **Java Swing** com integração dinâmica em scripts **Pnuts**.

---

## 1. Visão Geral da Arquitetura e Fluxo de Dados

A arquitetura do **MinimalJ** funciona como um ecossistema cliente-servidor rico (Desktop) orientado a metadados e focado em formulários de cadastro de dados comerciais e fiscais (especificamente desenhados para o cenário português, como a Informação Empresarial Simplificada - IES).

### Fluxo de Controle Centralizado (Padrão Command)
1. **Entrada de Comandos:** Todo evento gerado pelo usuário (clique em botões, seleção de menus) é encapsulado em uma string de comando (ex: `INSERT_ROW`, `DELETE_ROW`, `MAKEDDL`).
2. **Roteamento Central:** O `@PT/Minimal/actions/MHandleCommand.java` recebe esta string, extrai argumentos utilizando `@PT/Minimal/geral/MUtil.java:getArgs` e busca o mapeamento correspondente de `MCommand` por meio de `MForm.getCmd`.
3. **Autenticação:** O handler executa uma validação rigorosa de permissões chamando `@PT/Minimal/geral/MCheckAut.java:checkAut` com base no nível requerido pelo comando.
4. **Instanciação Dinâmica:** Se autorizado, a classe do comando é carregada em tempo de execução via reflexão (`Class.forName(wcmd.getCmd()).newInstance()`). Todas as classes executoras implementam a interface `@PT/Minimal/actions/MExternalCommand.java`.
5. **Execução de Negócio & Scripts:** O comando injeta o formulário Swing ativo e seus dados, obtém o motor de scripts Pnuts associado e executa a lógica em Java e/ou delega a validação aos scripts `.pnut` (ex: `Minimal.pnut`).

---

## 2. O Motor de Metadados e ORM Customizado

O MinimalJ possui um gerador de DDL e gerenciador de persistência altamente estruturado baseado em sufixos de tabelas. Os arquivos geradores como `@PT/Minimal/actions/MGerDDLOra.java` e `@PT/Minimal/actions/MGerDDLPos.java` expõem esta heurística:

- **Heurística de Sufixos das Tabelas:**
  - `t0`: Tabelas de entidades principais.
  - `tx`: Tabelas de extensão/detalhes estruturais (1:1 ou 1:N com as principais).
  - `tc`: Tabelas de códigos (lookups/domínios).
  - `tw`: Tabelas de trabalho (temporárias).
- **Heurística de Índices e Sequências:**
  - `i0`: Índices padrões sobre tabelas principais.
  - `izk`: Índices de chaves primárias auto-incrementais.
  - `ix`: Índices sobre tabelas de extensão.
  - `szk`: Sequências auto-incrementais geradas no Oracle/Postgres.
- **Heurística de Códigos Procedimentais:**
  - `pz` e `p0`: Stored Procedures registradas para execução de regras no banco de dados.

O banco de dados é lido em formato de `@PT/Minimal/sql/MRecord.java` (que estende `Vector`), permitindo que cada linha de banco seja tratada como uma coleção ordenada dinâmica, facilmente integrada com propriedades do Pnuts.

---

## 3. Integração Fiscal (DGCI / AT Portugal)

O sistema conta com um módulo de integração fiscal direta em `@PT/Minimal/util/Mies.java`. 
- **Objetivo:** Preparar as declarações anuais da **IES (Informação Empresarial Simplificada)** para a Autoridade Tributária e Aduaneira portuguesa (antiga DGCI).
- **Tecnologia:** Utiliza **JAXB** (`javax.xml.bind`) para mapear dados internos no schema oficial `pt.gov.dgci._2007.ies_da`.
- **Invariante de Negócio:** Mapeamento explícito de campos fiscais regulamentados como `Q101` (Rosto da declaração) e listas de NIFs de empresas de origem (`Q10NifEmpresaOrigemLista`).

---

## 4. Dicionário de Dados Resumido (Tipos Centrais do Framework)

| Estrutura / Classe | Pacote Base | Descrição Funcional | Atributos Críticos | Confiança |
|--------------------|-------------|---------------------|---------------------|-----------|
| **`MConnection`** | `PT.Minimal.geral` | Gerencia a sessão JDBC com suporte a Postgres, Oracle, Informix e pontes ODBC. | `minConnection`, `minDMD`, `wdcode`, `wdmsg` | 🟢 CONFIRMADO |
| **`MRecord`** | `PT.Minimal.sql` | Abstração de linha do banco de dados compatível com Pnuts. | `columns` (MHashtable), `colNames` (Vector) | 🟢 CONFIRMADO |
| **`MinMain`** | `PT.Minimal.geral` | Ponto de entrada padrão da aplicação. Inicializa a tela de login. | `went` (entidade ativa), `wsplash` (login GUI) | 🟢 CONFIRMADO |
| **`MForm`** | `PT.Minimal.geral` | Representa telas de formulários no padrão Swing. | `fase` (estágio do form), `ent` (id da entidade) | 🟢 CONFIRMADO |
| **`MHandleCommand`** | `PT.Minimal.actions` | Router e despachante centralizado de comandos string-based. | `minCmd` (comando), `row_vector` (dados da tela) | 🟢 CONFIRMADO |

---

## 5. Áreas Críticas, Riscos e Invariantes

1. **Dependência de Recursos Removidos (Risco Alto):**
   - A classe `MConnection` importa `sun.jdbc.odbc.JdbcOdbcDriver`. Esta ponte JDBC-ODBC foi descontinuada e removida oficialmente no **Java 8**. O sistema requer Java 7 ou inferior para compilar e rodar esta ponte sem quebras de runtime.
2. **Segurança por Obscuridade e Reflexão (Risco Médio):**
   - O carregamento dinâmico em `MHandleCommand` via `Class.forName(wcmd.getCmd())` abre margem para falhas de carregamento de classes (`ClassNotFoundException`) se os nomes dos comandos salvos no banco ou arquivos de recursos estiverem dessincronizados do classpath do build.
3. **Ausência de Suíte de Testes Automatizados (Risco Alto):**
   - Com 0% de testes automatizados, modificações em regras de negócio básicas (como fluxo de CRUD de `MInsert` ou `MDelete`) requerem bateria manual completa de testes visuais através de Swing.
4. **Acoplamento com Motor Pnuts (Risco Médio):**
   - O comportamento das janelas Swing é dinamicamente alterado em tempo de execução carregando scripts como `Minimal.pnut`. Regras de negócio implícitas residem nesses scripts de formato texto fora do fluxo de compilação do Java.
