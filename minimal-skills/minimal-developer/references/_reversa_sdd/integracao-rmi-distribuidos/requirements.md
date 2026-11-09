# Integracao RMI Distribuidos

> Spec SDD gerada pelo Reversa Writer. Foca no QUE a unit faz, nao no como.

## Visao Geral

A unit documenta a integracao Java RMI presente no MinimalJ por meio de um servidor `HelloServer`, um cliente `HelloClient`, uma implementacao remota `Hello` e o contrato `HelloInterface`. 🟢 **CONFIRMADO**

O comportamento confirmado e uma chamada remota simples que publica o nome `Hello` no registry RMI e permite que um cliente remoto chame `say()` para receber a mensagem configurada no servidor. 🟢 **CONFIRMADO**

O papel produtivo dessa integracao dentro dos fluxos principais do MinimalJ nao aparece ligado a menus, formularios, comandos CRUD, Pnuts ou JDBC; em validacao humana de 2026-05-22, o usuario confirmou que RMI nao era usado como requisito produtivo a preservar. 🟢 **CONFIRMADO**

## Responsabilidades

- Publicar um objeto remoto RMI sob o nome logico `Hello`. 🟢 **CONFIRMADO**
- Expor contrato remoto `HelloInterface.say()` com retorno `String` e excecao `RemoteException`. 🟢 **CONFIRMADO**
- Permitir cliente RMI consultar `//194.65.54.218/Hello` e imprimir o retorno de `say()`. 🟢 **CONFIRMADO**
- Registrar falhas de servidor e cliente no console com mensagens simples. 🟢 **CONFIRMADO**
- Servir como integracao distribuida experimental/de exemplo, sem obrigatoriedade no sistema novo conforme validacao humana. 🟢 **CONFIRMADO**

## Regras de Negocio

- O servidor deve registrar o objeto remoto com o nome `Hello` usando `java.rmi.Naming.rebind`. 🟢 **CONFIRMADO**
- O objeto remoto deve retornar exatamente a mensagem recebida no construtor de `Hello`. 🟢 **CONFIRMADO**
- O servidor legado instancia `Hello` com a mensagem `"Hello, world!"`. 🟢 **CONFIRMADO**
- O cliente legado deve procurar o servico remoto no host fixo `194.65.54.218` e nome `Hello`. 🟢 **CONFIRMADO**
- Qualquer excecao no servidor deve ser capturada e impressa como `Hello Server failed: <erro>`. 🟢 **CONFIRMADO**
- Qualquer excecao no cliente deve ser capturada e impressa como `HelloClient exception: <erro>`. 🟢 **CONFIRMADO**
- Nao ha autenticacao, autorizacao, criptografia, retry ou timeout explicitos no codigo RMI. 🟢 **CONFIRMADO**
- Nao ha evidencia de que RMI seja chamado pelo fluxo principal `MinMain`, pelo motor de comandos ou por scripts Pnuts. 🟢 **CONFIRMADO**
- Nao reconstruir RMI como feature obrigatoria do sistema novo, salvo decisao posterior explicita. 🟢 **CONFIRMADO PELO USUARIO**

## Requisitos Funcionais

| ID | Requisito | Prioridade | Criterio de Aceite |
|----|-----------|------------|--------------------|
| RF-01 | Publicar servico RMI `Hello`. 🟢 | Must | Dado registry RMI acessivel, quando `HelloServer.main()` executa, entao `Naming.rebind("Hello", new Hello("Hello, world!"))` registra o objeto remoto sem erro. |
| RF-02 | Expor contrato remoto `say()`. 🟢 | Must | Dado uma instancia remota `Hello`, quando cliente chama `say()`, entao recebe a mensagem armazenada no campo `message`. |
| RF-03 | Implementar interface remota Java RMI. 🟢 | Must | Dado classe `Hello`, quando compilada, entao ela estende `UnicastRemoteObject` e implementa `HelloInterface`, que por sua vez estende `Remote`. |
| RF-04 | Consultar servico remoto pelo cliente legado. 🟢 | Should | Dado servico `Hello` disponivel em `//194.65.54.218/Hello`, quando `HelloClient.main()` executa, entao `Naming.lookup()` retorna `HelloInterface` e imprime `hello.say()`. |
| RF-05 | Reportar falha de inicializacao do servidor no console. 🟢 | Should | Dado erro em `Naming.rebind` ou construcao de `Hello`, quando `HelloServer.main()` executa, entao imprime `Hello Server failed: <erro>`. |
| RF-06 | Reportar falha de chamada do cliente no console. 🟢 | Should | Dado erro em lookup, cast ou chamada remota, quando `HelloClient.main()` executa, entao imprime `HelloClient exception: <erro>`. |
| RF-07 | Manter integracao isolada do fluxo principal e fora do escopo obrigatorio do sistema novo. 🟢 | Could | Dado execucao normal de `MinMain`, quando menus e formularios sao usados, entao nenhum comportamento RMI e requerido para login, CRUD, Pnuts ou JDBC. |

## Requisitos Nao Funcionais

| Tipo | Requisito inferido | Evidencia no codigo | Confianca |
|------|--------------------|---------------------|-----------|
| Compatibilidade | Deve rodar em Java SE com suporte a Java RMI classico. | `PT/Minimal/rmi/HelloInterface.java:3`, `PT/Minimal/rmi/Hello.java:4`, `PT/Minimal/rmi/HelloServer.java:11` | 🟢 CONFIRMADO |
| Operabilidade | Deve depender de registry RMI ativo e resolucao de nome RMI acessivel. | `PT/Minimal/rmi/HelloServer.java:11`, `PT/Minimal/rmi/HelloClient.java:11` | 🟢 CONFIRMADO |
| Observabilidade | Falhas sao observadas apenas por `System.out.println`, sem log estruturado. | `PT/Minimal/rmi/HelloServer.java:12`, `PT/Minimal/rmi/HelloServer.java:14`, `PT/Minimal/rmi/HelloClient.java:13`, `PT/Minimal/rmi/HelloClient.java:15` | 🟢 CONFIRMADO |
| Seguranca | Nao ha autenticacao, autorizacao, TLS, controle de origem ou validacao de host no modulo RMI. | Ausencia desses mecanismos nos quatro arquivos de `PT/Minimal/rmi/` | 🟢 CONFIRMADO |
| Configurabilidade | Host remoto do cliente esta hardcoded, sem arquivo de configuracao identificado. | `PT/Minimal/rmi/HelloClient.java:11` | 🟢 CONFIRMADO |

> Validado em 2026-05-22: RMI nao deve ser considerado requisito produtivo obrigatorio.

## Criterios de Aceitacao

```gherkin
Cenario: Servidor publica objeto remoto
Dado registry RMI disponivel para o processo Java
Quando HelloServer.main() e executado
Entao o nome "Hello" e registrado via Naming.rebind
E o console imprime "Hello Server is ready."

Cenario: Cliente consome mensagem remota
Dado servico RMI "Hello" disponivel em //194.65.54.218/Hello
Quando HelloClient.main() e executado
Entao o cliente chama HelloInterface.say()
E imprime a mensagem retornada pelo servidor

Cenario: Servidor falha ao publicar
Dado registry RMI indisponivel ou objeto remoto nao instanciavel
Quando HelloServer.main() tenta executar Naming.rebind
Entao a excecao e capturada
E o console imprime mensagem iniciada por "Hello Server failed:"

Cenario: Cliente falha ao consultar servico
Dado host 194.65.54.218 indisponivel ou nome Hello ausente no registry
Quando HelloClient.main() tenta executar Naming.lookup
Entao a excecao e capturada
E o console imprime mensagem iniciada por "HelloClient exception:"
```

## Prioridade (MoSCoW)

| Requisito | MoSCoW | Justificativa |
|-----------|--------|---------------|
| Publicacao `Hello` via RMI | Must | Comportamento central e unico do servidor RMI legado. 🟢 |
| Contrato `HelloInterface.say()` | Must | Contrato minimo necessario para chamada remota. 🟢 |
| Cliente consultando `//194.65.54.218/Hello` | Should | Implementado no legado, mas nao conectado ao fluxo principal conhecido. 🟢 |
| Mensagens de erro em console | Should | Unico mecanismo confirmado de diagnostico do modulo. 🟢 |
| Uso produtivo em fluxos MinimalJ | Won't | Sem evidencia de chamada por GUI, comandos, Pnuts ou JDBC no workspace; usuario confirmou que nao deve ser preservado como requisito produtivo. 🟢 |

> Prioridade inferida por frequencia de chamada e posicao na cadeia de dependencias.

## Rastreabilidade de Codigo

| Arquivo | Funcao / Classe | Cobertura |
|---------|-----------------|-----------|
| `PT/Minimal/rmi/HelloInterface.java` | `HelloInterface`, `say()` | 🟢 |
| `PT/Minimal/rmi/Hello.java` | `Hello`, `Hello(String)`, `say()` | 🟢 |
| `PT/Minimal/rmi/HelloServer.java` | `HelloServer.main(String[])` | 🟢 |
| `PT/Minimal/rmi/HelloClient.java` | `HelloClient.main(String[])` | 🟢 |
| `_reversa_sdd/architecture.md` | Integracao Java RMI em contexto C4 | 🟢 |
| `_reversa_sdd/dependencies.md` | Dependencia `java.rmi.*` | 🟢 |
| `_reversa_sdd/c4-context.md` | Relacao RMI como teste/experimental | 🟡 |

## Lacunas

| Lacuna | Impacto | Confianca |
|--------|---------|-----------|
| Host `194.65.54.218` nao tem explicacao funcional no repositorio. | Como RMI nao sera preservado como requisito produtivo, manter apenas como evidencia historica e evitar reexecucao contra esse endereco. | 🟢 |
| Nao ha consumidores alem de `HelloClient`. | Reforca decisao de tratar RMI como exemplo/teste legado. | 🟢 |
