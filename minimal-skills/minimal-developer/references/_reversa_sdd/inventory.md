# Inventário do Sistema — MinimalJ

## Visão Geral do Projeto
O **MinimalJ** (empacotado sob o namespace `PT.Minimal`) é uma plataforma ou framework desktop legado escrito em **Java** com interface gráfica baseada em **Swing/AWT**. O sistema se integra com a linguagem de script dinâmica **Pnuts** e possui conectividade nativa com múltiplos bancos de dados relacionais corporativos de grande porte (Oracle, PostgreSQL, Informix e pontes ODBC).

---

## Estatísticas de Arquivos e Linhas de Código

- **Total de Arquivos:** 191 arquivos (excluindo infraestrutura interna de agentes e versionamento)
- **Linguagens Identificadas:**
  - **Java (`.java`):** 187 arquivos (Linguagem Principal - 97.9%)
  - **Pnuts Script (`.pnut`):** 3 arquivos (Linguagem de Scripting - 1.6%)
  - **JSON (`.json`):** 1 arquivo (Configurações de ambiente - 0.5%)

---

## Estrutura de Diretórios e Módulos

A estrutura física do repositório está centralizada na pasta raiz `PT/Minimal/`, subdividida nos seguintes subdiretórios funcionais:

### 1. `geral/` (94 arquivos)
* **Descrição:** É o motor principal do framework MinimalJ.
* **Componentes Chave:**
  - Contém as classes base para renderização de telas Swing (`MFrame`, `MForm`, `MButton`, `MLabel`, `MDialog`).
  - Lógica de autenticação e tela de entrada (`MLogin`, `MLoginWindow`).
  - Gerenciamento de conexões e execução de instruções no banco de dados (`MConnection`, `MCursor`, `MCursorOra`, `MCursorPg`, etc.).
  - Subpasta `console/` contendo componentes de fluxo de saída em console customizado (`MConsole`, `MObservableStream`).

### 2. `actions/` (52 arquivos)
* **Descrição:** Implementação de operações transacionais comuns e comandos de interface.
* **Componentes Chave:**
  - Operações básicas de CRUD (`MInsert`, `MUpdate`, `MDelete`).
  - Utilitários de interface e automação (`MExportCSV`, `MSendExcel`, `MSendMail`, `MPrintBrowse`).
  - Motores de geração de DDL específicos por banco (`MGerDDLOra`, `MGerDDLPos`, `MGerDDLInf`, `MGerDDLSql`).

### 3. `entities/` (12 arquivos)
* **Descrição:** Implementações concretas de comandos de negócio e migrações que estendem `MExternalCommand`.
* **Componentes Chave:**
  - Gerenciamento e migração de tabelas e procedures (`BXPF5`, `SMAN0`, `SPRO0`, `STAB0`, `STOC0`, etc.).
  - Execução de lógicas procedimentais de migração do banco de dados (`run_mig`, `unrun_mig`).

### 4. `util/` (11 arquivos)
* **Descrição:** Classes utilitárias compartilhadas em todo o sistema.
* **Componentes Chave:**
  - Tratamento customizado de dados (`MDate`, `MNumber`, `MNumberUtil`, `MString`).
  - Correção de bugs de teclado Swing (`AltGrPatch`).
  - Parser simplificado de XML (`MXmlReader`).

### 5. `editor/` (7 arquivos)
* **Descrição:** Um editor de texto customizado integrado à aplicação desktop.
* **Componentes Chave:**
  - Motor de edição rico e analisador léxico (`MEditor`, `MEditorToken`, `MEditorKit`).

### 6. `pnuts/` (6 arquivos)
* **Descrição:** Módulo de integração e execução de scripts dinâmicos Pnuts.
* **Componentes Chave:**
  - Mecanismo de execução Java-Pnuts (`MPnuts`, `MPnutsExp`, `MPnutsPackage`).
  - Arquivos de script executáveis (`Minimal.pnut`, `MPackage.pnut`, `Teste.pnut`).

### 7. `rmi/` (4 arquivos)
* **Descrição:** Infraestrutura rudimentar para Remote Method Invocation (RMI).
* **Componentes Chave:**
  - Servidor e cliente distribuídos (`HelloServer`, `HelloClient`, `HelloInterface`).

### 8. `sql/` (3 arquivos)
* **Descrição:** Abstrações leves para tratamento de dados retornados do banco.
* **Componentes Chave:**
  - Estrutura de dados tabulares (`MRecord`, `MResultSet`, `MSqlProc`).

---

## Pontos de Entrada (Entry Points) Detectados

O Scout identificou as seguintes classes que possuem o método executor principal (`public static void main`):

1. **`PT/Minimal/geral/MinMain.java` (Principal):**
   - Classe principal de inicialização de todo o ecossistema MinimalJ.
   - Carrega as propriedades do sistema, valida triggers e invoca a tela de login (`MLoginWindow` / `MLogin.showLogin`).
2. **`PT/Minimal/editor/MEditorTest.java` (Manual/Ferramenta):**
   - Classe de teste unitário manual para renderizar o editor gráfico Swing com patch de teclado (`AltGrPatch`).
3. **`PT/Minimal/rmi/HelloServer.java` e `HelloClient.java` (Serviços):**
   - Pontos de partida para instanciar servidores RMI de testes distribuídos.

---

## Arquivos de Configuração

- **`.vscode/settings.json`:** Gerencia configurações específicas do editor VS Code (desabilita avisos de limites do Git).

---

## Cobertura e Estrutura de Testes

- **Testes Automatizados:** Nenhuma suíte de testes unitários ou de integração automatizados (como JUnit ou TestNG) foi encontrada nas dependências do projeto.
- **Testes Manuais:**
  - `PT/Minimal/editor/MEditorTest.java` (Testa visualmente o componente editor).
  - `PT/Minimal/pnuts/Teste.pnut` (Script manual de validações).
- **Estimativa de Cobertura de Testes:** **0% de cobertura automatizada**.

---

## Organização de Specs Recomendada

- **Granularidade Sugerida:** `feature`
- **Motivação:** Como se trata de um sistema legado com pacotes estruturados puramente em camadas tecnológicas (GUI, ações, utilitários, scripts pnuts) sem uma separação clara de pastas de domínio funcionais na raiz, a organização por **features funcionais** é o formato ideal para mapear os fluxos ponta a ponta que cruzam as lógicas de conexão, ações e interfaces.
