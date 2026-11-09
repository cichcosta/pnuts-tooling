# Mapeamento de Dependências — MinimalJ

Este documento descreve as dependências, bibliotecas e pacotes identificados no sistema legado **MinimalJ**.

---

## 1. Stack Tecnológica Base

| Componente | Categoria | Descrição / Detalhes | Versão Identificada |
|------------|-----------|----------------------|---------------------|
| **Java SE** | Runtime / Linguagem | Plataforma de execução principal. Requer retrocompatibilidade com versões legadas devido ao uso de drivers nativos removidos em versões recentes do JDK (ex: JDBC-ODBC Bridge). | Compatível com JDK legados (ex: Java 5 / 6 / 7) |
| **Java Swing & AWT** | Framework UI | Utilizado para renderização e gerenciamento de janelas, formulários e eventos da aplicação desktop. | Nativo do Java Standard Edition |

---

## 2. Bibliotecas de Terceiros e Integrações

### Pnuts Scripting Engine
- **Namespace importado:** `pnuts.lang.*`
- **Uso no código:** Permite a interpretação dinâmica de scripts e regras de negócio sem a necessidade de recompilação do código Java principal.
- **Classes de Acoplamento:** `PT.Minimal.pnuts.MPnuts`, `PT.Minimal.pnuts.MPnutsExp`, `PT.Minimal.pnuts.MPnutsPackage`.
- **Arquivos Associados:** `MPackage.pnut`, `Minimal.pnut`, `Teste.pnut`.
- **Versão:** Não especificada nos arquivos fonte (carregada via classpath).

---

## 3. Drivers de Conectividade e Banco de Dados (JDBC)

O sistema possui acoplamento estrito e de baixo nível com múltiplos SGBDs. Os drivers de conexão são declarados e importados explicitamente na classe `@c:\MinimalJ\PT\Minimal\geral\MConnection.java:4-18`:

| Driver / Classe de Conexão | Banco de Dados Alvo | Origem / Fornecedor |
|----------------------------|---------------------|----------------------|
| `com.informix.jdbc.IfxDriver` | **IBM Informix** | Driver JDBC oficial Informix (`com.informix`) |
| `oracle.jdbc.driver.OracleDriver` | **Oracle Database** | Driver Oracle JDBC Thin (`oracle.jdbc`) |
| `org.postgresql.Driver` | **PostgreSQL** | Driver oficial PostgreSQL (`org.postgresql`) |
| `sun.jdbc.odbc.JdbcOdbcDriver` | **Pontes ODBC / Outros** | Ponte nativa JDBC-ODBC da Sun Microsystems (Removida no Java 8) |

---

## 4. APIs e Utilitários Nativos do Java

O sistema utiliza extensivamente os seguintes pacotes nativos do ecossistema Java SE:

- **`java.sql.*`**: Manipulação direta de statements SQL, transações e metadados de bancos de dados (`java.sql.Connection`, `java.sql.DatabaseMetaData`, `java.sql.Statement`, `java.sql.ResultSet`).
- **`java.awt.*` & `javax.swing.*`**: Criação de interfaces gráficas customizadas, tratamento de eventos e correção de teclado (`java.awt.event.ActionEvent`, `javax.swing.AbstractAction`, `javax.swing.text.JTextComponent`).
- **`java.rmi.*`**: Comunicação distribuída e chamadas de métodos remotos (`java.rmi.Naming`, `java.rmi.Remote`, `java.rmi.RemoteException`, `java.rmi.server.UnicastRemoteObject`).
- **`java.math.BigDecimal`**: Utilizado em operações de cálculos matemáticos de alta precisão (ex: manipulação de moedas, câmbios e valores decimais no módulo `util/` e `actions/`).
