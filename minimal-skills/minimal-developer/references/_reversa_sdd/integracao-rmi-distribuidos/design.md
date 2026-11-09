# Integracao RMI Distribuidos, Design Tecnico

> Spec SDD gerada pelo Reversa Writer. Foca no COMO a unit e construida, com base no codigo legado lido.

## Interface

A interface externa da unit e Java RMI classico, sem endpoint HTTP, sem fila e sem contrato serializado proprio. 🟢 **CONFIRMADO**

| Simbolo | Assinatura | Retorno | Observacao |
|---------|------------|---------|------------|
| `HelloInterface.say` | `() throws RemoteException` | `String` | Contrato remoto unico exposto pelo modulo. 🟢 |
| `Hello.Hello` | `(msg: String) throws RemoteException` | `Hello` | Construtor armazena a mensagem em campo interno e registra objeto exportavel por heranca de `UnicastRemoteObject`. 🟢 |
| `Hello.say` | `() throws RemoteException` | `String` | Retorna o valor atual de `message` sem transformacao. 🟢 |
| `HelloServer.main` | `(argv: String[])` | `void` | Registra `new Hello("Hello, world!")` com nome RMI `Hello`. 🟢 |
| `HelloClient.main` | `(argv: String[])` | `void` | Faz lookup de `//194.65.54.218/Hello`, chama `say()` e imprime o resultado. 🟢 |

## Contrato RMI

```java
public interface HelloInterface extends java.rmi.Remote {
    String say() throws java.rmi.RemoteException;
}
```

🟢 **CONFIRMADO** em `PT/Minimal/rmi/HelloInterface.java`.

### Nome Remoto

| Propriedade | Valor | Evidencia | Confianca |
|-------------|-------|-----------|-----------|
| Nome publicado pelo servidor | `Hello` | `PT/Minimal/rmi/HelloServer.java:11` | 🟢 |
| URL consultada pelo cliente | `//194.65.54.218/Hello` | `PT/Minimal/rmi/HelloClient.java:11` | 🟢 |
| Mensagem publicada | `Hello, world!` | `PT/Minimal/rmi/HelloServer.java:11` | 🟢 |

## Fluxo Principal, Servidor

1. `HelloServer.main(String[] argv)` inicia bloco `try`. 🟢 `PT/Minimal/rmi/HelloServer.java:9`
2. O servidor instancia `new Hello("Hello, world!")`. 🟢 `PT/Minimal/rmi/HelloServer.java:11`
3. O construtor `Hello(String msg)` chama construtor de `UnicastRemoteObject` implicitamente e armazena `msg` em `message`. 🟢 `PT/Minimal/rmi/Hello.java:12`
4. O servidor executa `java.rmi.Naming.rebind("Hello", objetoRemoto)`. 🟢 `PT/Minimal/rmi/HelloServer.java:11`
5. Em sucesso, imprime `Hello Server is ready.`. 🟢 `PT/Minimal/rmi/HelloServer.java:12`

## Fluxo Principal, Cliente

1. `HelloClient.main(String[] argv)` inicia bloco `try`. 🟢 `PT/Minimal/rmi/HelloClient.java:9`
2. O cliente executa `java.rmi.Naming.lookup("//194.65.54.218/Hello")`. 🟢 `PT/Minimal/rmi/HelloClient.java:11`
3. O retorno do lookup e convertido para `HelloInterface`. 🟢 `PT/Minimal/rmi/HelloClient.java:11`
4. O cliente chama `hello.say()`. 🟢 `PT/Minimal/rmi/HelloClient.java:13`
5. O retorno e enviado para `System.out.println`. 🟢 `PT/Minimal/rmi/HelloClient.java:13`

## Fluxos Alternativos

- **Falha de publicacao no servidor:** qualquer excecao no bloco de `HelloServer.main` e capturada por `catch (Exception e)` e impressa como `Hello Server failed: ` concatenado com a excecao. 🟢 `PT/Minimal/rmi/HelloServer.java:13`
- **Falha de lookup ou chamada no cliente:** qualquer excecao no bloco de `HelloClient.main` e capturada por `catch (Exception e)` e impressa como `HelloClient exception: ` concatenado com a excecao. 🟢 `PT/Minimal/rmi/HelloClient.java:14`
- **Mensagem configurada diferente:** `Hello.say()` sempre retorna o campo `message`; se outro construtor receber valor diferente, esse valor passa a ser o retorno remoto. 🟢 `PT/Minimal/rmi/Hello.java:12`, `PT/Minimal/rmi/Hello.java:19`
- **Uso integrado ao MinimalJ principal:** nao ha fluxo alternativo confirmado que conecte RMI ao login, comandos, formularios, Pnuts ou JDBC. 🟢 busca por referencias RMI no workspace.

## Dependencias

- `java.rmi.Remote`, para marcar `HelloInterface` como contrato remoto. 🟢 `PT/Minimal/rmi/HelloInterface.java:3`
- `java.rmi.RemoteException`, para declarar falhas remotas no contrato e implementacao. 🟢 `PT/Minimal/rmi/HelloInterface.java:4`, `PT/Minimal/rmi/Hello.java:3`
- `java.rmi.server.UnicastRemoteObject`, para exportar a implementacao remota `Hello`. 🟢 `PT/Minimal/rmi/Hello.java:4`
- `java.rmi.Naming`, usado por nome totalmente qualificado para `rebind` e `lookup`. 🟢 `PT/Minimal/rmi/HelloServer.java:11`, `PT/Minimal/rmi/HelloClient.java:11`
- Registry RMI externo ao codigo, requerido pelo uso de `Naming.rebind`/`lookup`, mas nao configurado no repositorio. 🟡 **INFERIDO**

## Decisoes de Design Identificadas

| Decisao | Evidencia no codigo | Confianca |
|---------|---------------------|-----------|
| Usar Java RMI nativo em vez de HTTP, socket manual ou mensageria. | imports e chamadas `java.rmi.*` em `PT/Minimal/rmi/*` | 🟢 |
| Expor contrato remoto minimo com uma unica operacao `say()`. | `PT/Minimal/rmi/HelloInterface.java:6` | 🟢 |
| Implementar objeto remoto como `UnicastRemoteObject`. | `PT/Minimal/rmi/Hello.java:7` | 🟢 |
| Publicar nome RMI simples `Hello`, sem namespace de aplicacao. | `PT/Minimal/rmi/HelloServer.java:11` | 🟢 |
| Fixar host do cliente em `194.65.54.218`. | `PT/Minimal/rmi/HelloClient.java:11` | 🟢 |
| Tratar erros apenas com captura ampla e impressao em console. | `PT/Minimal/rmi/HelloServer.java:13`, `PT/Minimal/rmi/HelloClient.java:14` | 🟢 |
| Manter RMI separado dos modulos centrais do framework e fora do escopo obrigatorio do sistema novo. | ausencia de referencias alem de `PT/Minimal/rmi`; validacao humana 2026-05-22 | 🟢 |

## Estado Interno

| Campo | Classe | Tipo | Evolucao | Confianca |
|-------|--------|------|----------|-----------|
| `message` | `Hello` | `String` | Definido no construtor e retornado por `say()`; nao ha setter ou mutacao posterior. | 🟢 |

A unit nao mantem estado global proprio alem do registro externo no registry RMI, que nao esta representado em classe do repositorio. 🟢 **CONFIRMADO** para ausencia no codigo; 🟡 **INFERIDO** para estado externo.

## Observabilidade

| Evento | Saida | Evidencia | Confianca |
|--------|-------|-----------|-----------|
| Servidor pronto | `Hello Server is ready.` | `PT/Minimal/rmi/HelloServer.java:12` | 🟢 |
| Falha de servidor | `Hello Server failed: <erro>` | `PT/Minimal/rmi/HelloServer.java:14` | 🟢 |
| Retorno do cliente | valor de `hello.say()` | `PT/Minimal/rmi/HelloClient.java:13` | 🟢 |
| Falha de cliente | `HelloClient exception: <erro>` | `PT/Minimal/rmi/HelloClient.java:15` | 🟢 |

Nao ha logger, metricas, traces, correlation id, codigos de erro estruturados ou auditoria de chamadas RMI. 🟢 **CONFIRMADO**

## Seguranca

| Aspecto | Situacao | Evidencia | Confianca |
|---------|----------|-----------|-----------|
| Autenticacao | Ausente no modulo RMI. | Nenhum controle em `PT/Minimal/rmi/*` | 🟢 |
| Autorizacao | Ausente no modulo RMI. | Nenhuma chamada a `MCheckAut` em `PT/Minimal/rmi/*` | 🟢 |
| Criptografia | Ausente no codigo. | Uso direto de `Naming.lookup`/`rebind` sem socket factory customizada | 🟢 |
| Configuracao de host | Hardcoded no cliente. | `PT/Minimal/rmi/HelloClient.java:11` | 🟢 |
| Exposicao de superficie | Baixa no codigo atual, porque so ha `say()`, mas risco operacional depende do registry real. | contrato `HelloInterface` minimo; registry nao documentado | 🟡 |

## Riscos e Lacunas

- 🟢 Usuario confirmou em 2026-05-22 que RMI nao era requisito produtivo a preservar; tratar como exemplo/teste legado.
- 🟢 O endereco `194.65.54.218` deve permanecer apenas como evidencia historica do cliente legado; nao reexecutar chamada contra esse host sem decisao operacional nova.
- 🟢 Falhas remotas nao possuem retry, timeout explicito ou fallback no codigo legado.
- 🟢 A chamada RMI nao participa do controle de permissao do MinimalJ.
