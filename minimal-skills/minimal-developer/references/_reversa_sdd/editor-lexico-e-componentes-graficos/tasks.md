# Editor Lexico e Componentes Graficos, Tarefas de Implementacao

## Pre-requisitos

- [ ] Stack de UI desktop equivalente a Swing disponivel, ou decisao explicita de componente alvo no sistema novo. 🟢
- [ ] Contratos de formulario base equivalentes a `MForm`, painel central, painel inferior e botoes OK/END definidos. 🟢
- [ ] Decisao de compatibilidade para lexer: manter dependencia em JDK antigo `sun.tools.java.*` ou substituir por lexer moderno. 🟢
- [ ] Politica de teclado para acentos e AltGr definida para ambiente alvo. 🟢
- [ ] Criterio de teste visual/manual definido enquanto nao houver automacao de UI. 🔴

## Tarefas

- [ ] T-01, Criar componente de editor como tela/formulario integrado ao framework alvo.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:29`, `PT/Minimal/editor/MEditor.java:40`, `PT/Minimal/editor/MEditor.java:119`
  - Criterio de pronto: Instancia do editor recebe `formName`, `ent`, `entName` e `type`, monta area central editavel e painel inferior com controles de status e comandos padrao.
  - Confianca: 🟢

- [ ] T-02, Implementar area de edicao monoespacada com rolagem e content type configuravel.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:44`, `PT/Minimal/editor/MEditor.java:53`, `PT/Minimal/editor/MEditor.java:108`
  - Criterio de pronto: Editor exibe area branca, editavel, fonte monoespacada 12, com scroll horizontal/vertical e content type aplicado.
  - Confianca: 🟢

- [ ] T-03, Implementar API publica de texto e leitura de arquivo.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:142`, `PT/Minimal/editor/MEditor.java:164`, `PT/Minimal/editor/MEditor.java:192`
  - Criterio de pronto: `setText`, `getText` e `read` carregam/retornam conteudo; texto multiline recalcula largura com base na maior linha.
  - Confianca: 🟢

- [ ] T-04, Implementar caret customizado com modos Insert e Overwrite.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:101`, `PT/Minimal/editor/MEditor.java:196`, `PT/Minimal/editor/MEditor.java:203`
  - Criterio de pronto: Caret mostra linha vertical em Insert, bloco em Overwrite, alterna estado e atualiza campo `mode`.
  - Confianca: 🟢

- [ ] T-05, Implementar documento com comportamento de sobrescrita.
  - Origem no legado: `PT/Minimal/editor/MEditorDocument.java:37`
  - Criterio de pronto: Em Insert, digitar apenas insere; em Overwrite, digitar remove caracteres existentes antes de inserir a string nova.
  - Confianca: 🟢

- [ ] T-06, Implementar atualizacao de linha/coluna no movimento do caret.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:61`, `PT/Minimal/editor/MEditor.java:176`
  - Criterio de pronto: Mudanca de caret atualiza indicador `Line = X Column = Y` sem travar UI.
  - Confianca: 🟢

- [ ] T-07, Implementar editor kit/document factory e historico de undo/redo.
  - Origem no legado: `PT/Minimal/editor/MEditorKit.java:84`, `PT/Minimal/editor/MEditorKit.java:100`, `PT/Minimal/editor/MEditorKit.java:109`
  - Criterio de pronto: Documento criado pelo kit registra edicoes em gerenciador de undo; `showForm` ou abertura equivalente limpa historico antigo.
  - Confianca: 🟢

- [ ] T-08, Implementar atalhos de teclado para navegacao, selecao, select-all, undo e redo.
  - Origem no legado: `PT/Minimal/editor/MEditorKit.java:338`, `PT/Minimal/editor/MEditorKit.java:367`, `PT/Minimal/editor/MEditorKit.java:377`
  - Criterio de pronto: Ctrl+Home, Ctrl+End, Shift+Ctrl+Home/End, Ctrl+A, Shift+PageUp/PageDown, Ctrl+Z e Ctrl+Y executam acoes equivalentes.
  - Confianca: 🟢

- [ ] T-09, Implementar colorizacao lexical para conteudo Java ou substituto moderno equivalente.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:71`, `PT/Minimal/editor/MEditorContext.java:180`, `PT/Minimal/editor/MEditorDocument.java:54`
  - Criterio de pronto: Comentarios, strings e palavras-chave Java recebem estilos; quando lexer indisponivel, editor basico segue funcional.
  - Confianca: 🟢

- [ ] T-10, Recriar catalogo de tokens e estilos de token.
  - Origem no legado: `PT/Minimal/editor/MEditorToken.java`, `PT/Minimal/editor/MEditorContext.java:33`, `PT/Minimal/editor/MEditorContext.java:57`
  - Criterio de pronto: Tokens possuem categoria, valor de scan e estilo consultavel por codigo; estilos sao usados na renderizacao.
  - Confianca: 🟢

- [ ] T-11, Implementar tratamento de comentarios de bloco para definir ponto inicial do scanner.
  - Origem no legado: `PT/Minimal/editor/MEditorDocument.java:73`, `PT/Minimal/editor/MEditorDocument.java:97`, `PT/Minimal/editor/MEditorDocument.java:162`
  - Criterio de pronto: Linhas dentro de comentario `/* ... */` sao marcadas e o scanner reinicia antes do bloco quando necessario.
  - Confianca: 🟢

- [ ] T-12, Implementar composicao manual de caracteres acentuados.
  - Origem no legado: `PT/Minimal/editor/MEditorKeyAdapter.java:17`, `PT/Minimal/editor/MEditorKeyAdapter.java:35`, `PT/Minimal/editor/MEditorKeyAdapter.java:110`
  - Criterio de pronto: Sequencias mapeadas para til, circunflexo, agudo, grave e trema produzem vogais acentuadas correspondentes; sequencias nao mapeadas nao alteram evento.
  - Confianca: 🟢

- [ ] T-13, Recriar harness de teste visual/manual.
  - Origem no legado: `PT/Minimal/editor/MEditorTest.java:19`, `PT/Minimal/editor/MEditorTest.java:33`, `PT/Minimal/editor/MEditorTest.java:45`
  - Criterio de pronto: Executavel de teste abre janela com editor, aceita caminho de arquivo por argumento e carrega conteudo para inspecao.
  - Confianca: 🟢

- [ ] T-14, Corrigir ou decidir compatibilidade de anomalias preservadas do legado.
  - Origem no legado: `PT/Minimal/editor/MEditorToken.java`, `PT/Minimal/editor/MEditor.java:176`, `PT/Minimal/editor/MEditorKit.java:70`
  - Criterio de pronto: Decisao documentada para igualdade de tokens, calculo de linha/coluna e clone de editor kit; nova implementacao passa testes mesmo que nao replique bugs acidentais.
  - Confianca: 🟢

## Tarefas de Teste

- [ ] TT-01, Testar abertura do editor com `type="text/java"` e estrutura visual basica.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:40`
  - Criterio de pronto: Area editavel, scroll, campos `Insert` e `Line = 0 Column = 0`, OK/END visiveis.
  - Confianca: 🟢

- [ ] TT-02, Testar `setText`, `getText` e `read` com arquivo Java multiline.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:142`, `PT/Minimal/editor/MEditor.java:192`
  - Criterio de pronto: Conteudo carregado bate com origem e largura considera maior linha.
  - Confianca: 🟢

- [ ] TT-03, Testar Insert/Overwrite.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:196`, `PT/Minimal/editor/MEditorDocument.java:37`
  - Criterio de pronto: Insert preserva texto existente; Overwrite substitui caracteres na posicao do caret.
  - Confianca: 🟢

- [ ] TT-04, Testar undo/redo e atalhos registrados.
  - Origem no legado: `PT/Minimal/editor/MEditorKit.java:196`, `PT/Minimal/editor/MEditorKit.java:224`, `PT/Minimal/editor/MEditorKit.java:338`
  - Criterio de pronto: Ctrl+Z desfaz, Ctrl+Y refaz, Ctrl+A seleciona tudo e atalhos de pagina movem selecao.
  - Confianca: 🟢

- [ ] TT-05, Testar colorizacao com lexer disponivel e fallback sem lexer.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:71`, `PT/Minimal/editor/MEditorContext.java:180`
  - Criterio de pronto: Com lexer, comentarios/string/keywords recebem cor; sem lexer, editor continua abrindo e editando texto.
  - Confianca: 🟢

- [ ] TT-06, Testar composicao de acentos.
  - Origem no legado: `PT/Minimal/editor/MEditorKeyAdapter.java:44`
  - Criterio de pronto: `~a`, `^e`, `´i`, `` `o `` e `¨u` produzem caracteres esperados; combinacao nao mapeada nao altera caractere.
  - Confianca: 🟢

- [ ] TT-07, Testar atualizacao de linha/coluna.
  - Origem no legado: `PT/Minimal/editor/MEditor.java:176`
  - Criterio de pronto: Mover caret por teclado/mouse atualiza indicador com valores coerentes.
  - Confianca: 🟢

## Tarefas de Migracao de Dados

- [ ] TM-01, Nao aplicavel: unit nao possui schema, tabelas ou migracoes de dados persistentes.
  - Origem no legado: `PT/Minimal/editor`
  - Criterio de pronto: Nenhum script de banco exigido para reimplementar editor.
  - Confianca: 🟢

## Ordem Sugerida

1. T-01 e T-02 primeiro, porque definem superficie visual e integracao com formulario.
2. T-03, T-04 e T-05 em seguida, porque formam comportamento basico de edicao.
3. T-06, T-07 e T-08 depois, para completar ergonomia e atalhos.
4. T-09, T-10 e T-11 depois da edicao basica, porque colorizacao depende do documento e da view.
5. T-12 apos fluxo de teclado basico.
6. T-13 e testes TT-01 a TT-07 para validar paridade.
7. T-14 por ultimo, documentando decisoes sobre bugs/anomalias antes de congelar compatibilidade.

## Lacunas Pendentes (🔴)

- 🔴 Nao ha teste automatizado legado; validar visualmente ou criar harness novo antes de afirmar paridade total.
- 🔴 Nao ha decisao de produto sobre preservar bugs aparentes (`equals`, `clone`, calculo de linha/coluna) ou corrigir na reimplementacao.
- 🔴 Runtime alvo ainda precisa decidir se aceita dependencia em JDK antigo ou substitui `sun.tools.java.*` por lexer moderno.
