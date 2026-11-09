# Integracao RMI Distribuidos, Tarefas de Implementacao

> Spec SDD gerada pelo Reversa Writer. Foca em uma sequencia de tarefas executaveis para reimplementar a unit a partir do legado, com rastreabilidade ao codigo original.

## Pre-requisitos

- [ ] Runtime Java com suporte a Java RMI disponivel.
  - Origem no legado: `PT/Minimal/rmi/HelloInterface.java:3`, `PT/Minimal/rmi/Hello.java:4`
  - Criterio de pronto: build compila tipos `Remote`, `RemoteException` e `UnicastRemoteObject`.
  - Confianca: 🟢

- [X] Decidir se RMI deve ser mantido como requisito produtivo, teste legado ou exemplo removivel.
  - Origem no legado: `_reversa_sdd/architecture.md`, `_reversa_sdd/c4-context.md`
  - Criterio de pronto: decisao registrada antes de expor qualquer servico remoto em rede real. Decisao 2026-05-22: nao preservar como requisito produtivo.
  - Confianca: 🟢

- [ ] Definir ambiente de registry RMI apenas se algum teste historico exigir reexecucao controlada.
  - Origem no legado: `PT/Minimal/rmi/HelloServer.java:11`, `PT/Minimal/rmi/HelloClient.java:11`
  - Criterio de pronto: host, porta e ciclo de vida do registry documentados em ambiente local/controlado, nunca contra `194.65.54.218` sem nova decisao operacional.
  - Confianca: 🟢

## Tarefas

- [ ] T-01, Implementar contrato remoto `HelloInterface`
  - Origem no legado: `PT/Minimal/rmi/HelloInterface.java:1`
  - Criterio de pronto: interface remota estende `java.rmi.Remote` e declara `String say() throws RemoteException`.
  - Confianca: 🟢

- [ ] T-02, Implementar objeto remoto `Hello`
  - Origem no legado: `PT/Minimal/rmi/Hello.java:6`
  - Criterio de pronto: classe estende `UnicastRemoteObject`, implementa `HelloInterface` e compila com construtor que propaga `RemoteException`.
  - Confianca: 🟢

- [ ] T-03, Persistir mensagem recebida no construtor
  - Origem no legado: `PT/Minimal/rmi/Hello.java:12`
  - Criterio de pronto: campo interno `message` recebe exatamente o argumento `msg` sem transformacao.
  - Confianca: 🟢

- [ ] T-04, Implementar `say()` como retorno direto da mensagem
  - Origem no legado: `PT/Minimal/rmi/Hello.java:19`
  - Criterio de pronto: chamada a `say()` retorna o valor atual de `message` e nao executa efeito colateral.
  - Confianca: 🟢

- [ ] T-05, Implementar servidor RMI
  - Origem no legado: `PT/Minimal/rmi/HelloServer.java:8`
  - Criterio de pronto: `main(String[] argv)` instancia `new Hello("Hello, world!")` e registra com `Naming.rebind("Hello", ...)`.
  - Confianca: 🟢

- [ ] T-06, Emitir mensagem de sucesso do servidor
  - Origem no legado: `PT/Minimal/rmi/HelloServer.java:12`
  - Criterio de pronto: apos `rebind` bem-sucedido, processo imprime `Hello Server is ready.`.
  - Confianca: 🟢

- [ ] T-07, Preservar tratamento de falha do servidor
  - Origem no legado: `PT/Minimal/rmi/HelloServer.java:13`
  - Criterio de pronto: excecoes durante publicacao sao capturadas e impressas com prefixo `Hello Server failed: `.
  - Confianca: 🟢

- [ ] T-08, Implementar cliente RMI legado
  - Origem no legado: `PT/Minimal/rmi/HelloClient.java:8`
  - Criterio de pronto: `main(String[] argv)` executa lookup em `//194.65.54.218/Hello`, converte para `HelloInterface`, chama `say()` e imprime retorno.
  - Confianca: 🟢

- [ ] T-09, Preservar tratamento de falha do cliente
  - Origem no legado: `PT/Minimal/rmi/HelloClient.java:14`
  - Criterio de pronto: excecoes durante lookup, cast ou chamada remota sao capturadas e impressas com prefixo `HelloClient exception: `.
  - Confianca: 🟢

- [ ] T-10, Isolar RMI do fluxo principal MinimalJ
  - Origem no legado: `PT/Minimal/rmi/*`, `_reversa_sdd/code-analysis.md`, `_reversa_sdd/architecture.md`
  - Criterio de pronto: login, Swing, comandos, CRUD, JDBC e Pnuts nao dependem do modulo RMI, salvo decisao humana posterior.
  - Confianca: 🟡

- [ ] T-11, Externalizar host remoto antes de uso moderno
  - Origem no legado: `PT/Minimal/rmi/HelloClient.java:11`
  - Criterio de pronto: host `194.65.54.218` deixa de ser literal no codigo reimplementado e passa por configuracao validada.
  - Confianca: 🟡

- [X] T-12, Definir politica minima de seguranca se RMI for mantido
  - Origem no legado: ausencia de autenticacao/autorizacao em `PT/Minimal/rmi/*`
  - Criterio de pronto: decisao explicita sobre rede permitida, autenticacao, criptografia ou desativacao do endpoint. Decisao 2026-05-22: nao manter endpoint produtivo.
  - Confianca: 🟢

## Tarefas de Teste

- [ ] TT-01, Testar retorno direto de `Hello.say()`
  - Origem no legado: `PT/Minimal/rmi/Hello.java:19`
  - Criterio de pronto: instancia criada com `msg = "X"` retorna `"X"` em `say()`.
  - Confianca: 🟢

- [ ] TT-02, Testar publicacao do servidor em registry local
  - Origem no legado: `PT/Minimal/rmi/HelloServer.java:11`
  - Criterio de pronto: com registry local ativo, `HelloServer.main()` registra nome `Hello` e imprime sucesso.
  - Confianca: 🟢

- [ ] TT-03, Testar cliente consumindo servico em ambiente controlado
  - Origem no legado: `PT/Minimal/rmi/HelloClient.java:11`
  - Criterio de pronto: lookup aponta para endpoint de teste configurado e imprime mensagem remota esperada.
  - Confianca: 🟡

- [ ] TT-04, Testar falha de servidor sem registry
  - Origem no legado: `PT/Minimal/rmi/HelloServer.java:14`
  - Criterio de pronto: sem registry disponivel, processo nao derruba stack trace cru e imprime prefixo `Hello Server failed: `.
  - Confianca: 🟢

- [ ] TT-05, Testar falha de cliente com host indisponivel
  - Origem no legado: `PT/Minimal/rmi/HelloClient.java:15`
  - Criterio de pronto: com endpoint indisponivel, processo captura excecao e imprime prefixo `HelloClient exception: `.
  - Confianca: 🟢

- [ ] TT-06, Testar que fluxo principal nao exige RMI
  - Origem no legado: busca de referencias em `PT/Minimal` e `_reversa_sdd/code-analysis.md`
  - Criterio de pronto: inicializacao principal, login e operacoes de formulario nao inicializam `HelloServer` nem chamam `HelloClient`.
  - Confianca: 🟡

## Tarefas de Migracao de Dados

Nao aplicavel. A unit RMI nao define schema, tabela, migracao de dados ou persistencia propria no codigo legado. 🟢

## Ordem Sugerida

1. Implementar `HelloInterface`, porque `Hello`, servidor e cliente dependem do contrato remoto.
2. Implementar `Hello` e `say()`, porque servidor publica essa implementacao.
3. Implementar servidor com registry local em ambiente de teste.
4. Implementar cliente apontando para host configuravel em ambiente controlado.
5. Adicionar testes de sucesso e falha.
6. Como decisao humana atual e nao preservar RMI como requisito produtivo, manter implementacao apenas como compatibilidade historica/teste isolado se necessario.
7. Se decisao futura reativar RMI, definir seguranca e configuracao antes de qualquer exposicao de rede.

## Lacunas Pendentes (🔴)

- 🟢 Java RMI nao e requisito produtivo a preservar, conforme validacao humana de 2026-05-22.
- 🟢 Host `194.65.54.218` fica apenas como evidencia historica no codigo legado; nao deve ser usado sem nova decisao operacional.
