# C4 Contexto — MinimalJ

Este diagrama descreve o MinimalJ no nivel de contexto: quem usa o sistema, quais sistemas externos participam e quais protocolos foram identificados.

```mermaid
flowchart TB
    subgraph Pessoas
        user["Usuario operacional\nOpera menus, formularios e CRUD"]
        admin["Administrador / usuario minimal\nConfigura metadados, comandos, DDL e permissoes"]
    end

    minimalj["MinimalJ\nFramework desktop Java/Swing\nNamespace PT.Minimal"]

    subgraph RuntimeLocal["Runtime local"]
        pnuts["Pnuts Engine\nScripts dinamicos e hooks"]
        rmi["Java RMI\nHelloServer/HelloClient"]
    end

    subgraph Bancos["Bancos relacionais corporativos"]
        defaultdb["DEFAULT\nDados, sessao e autorizacao"]
        metadb["JMETADATA\nMetadados de UI, comandos, atributos e DDL"]
        dbdrivers["Drivers JDBC\nOracle / Informix / PostgreSQL / SQL Server / ODBC"]
    end

    ies["IES / Autoridade Tributaria Portuguesa\nXML JAXB pt.gov.dgci._2007.ies_da"]

    user -->|"interacao GUI Swing/AWT"| minimalj
    admin -->|"uso administrativo e bypass especial"| minimalj

    minimalj -->|"interpreta e avalia hooks"| pnuts
    minimalj -.->|"RMI de teste/experimental"| rmi
    minimalj -->|"JDBC"| dbdrivers
    dbdrivers --> defaultdb
    dbdrivers --> metadb
    minimalj -->|"gera/exporta XML fiscal"| ies

    defaultdb -->|"suact0: sessao do usuario"| minimalj
    defaultdb -->|"ssact0: autorizacoes AG/AE"| minimalj
    metadb -->|"mrfct0 e objetos derivados"| minimalj
```

## Relacionamentos

| Origem | Destino | Relacao | Confianca |
|--------|---------|---------|-----------|
| Usuario operacional | MinimalJ | Usa telas Swing e dispara comandos por menus/botoes. | 🟢 CONFIRMADO |
| Administrador / `minimal` | MinimalJ | Opera configuracoes e bypass de autorizacao normal. | 🟢 CONFIRMADO |
| MinimalJ | Bancos relacionais | Usa JDBC direto para conexao, transacao, SQL, metadados e DDL. | 🟢 CONFIRMADO |
| MinimalJ | Pnuts | Carrega `init.pnut`, `Minimal.pnut`, `MPackage.pnut` e avalia hooks. | 🟢 CONFIRMADO |
| MinimalJ | IES / AT Portugal | Usa JAXB para estruturas de declaracao IES. | 🟡 INFERIDO |
| MinimalJ | Java RMI | Contem cliente/servidor de exemplo ou infraestrutura rudimentar. | 🟡 INFERIDO |

