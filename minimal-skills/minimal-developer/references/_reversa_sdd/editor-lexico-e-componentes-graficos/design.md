# Editor Lexico e Componentes Graficos, Design Tecnico

## Interface

### Classes e metodos

| Simbolo | Assinatura | Retorno | Observacao |
|---------|------------|---------|------------|
| `MEditor` | `(formName: String, ent: String, entName: String, type: String)` | instancia `MEditor` | Monta formulario Swing com `JEditorPane`, `MEditorKit`, painel inferior, botoes OK/END e caret customizado. 🟢 |
| `MEditor.isEditable` | `()` | `boolean` | Delega para `editArea.isEditable()`. 🟢 |
| `MEditor.setEditable` | `(flag: boolean)` | `void` | Delega para `editArea.setEditable(flag)`. 🟢 |
| `MEditor.setText` | `(text: String)` | `void` | Calcula maior linha, redimensiona area editavel e posiciona caret. 🟢 |
| `MEditor.getText` | `()` | `String` | Retorna texto atual do `JEditorPane`. 🟢 |
| `MEditor.showForm` | `(flag: boolean)` | `void` | Limpa undo, delega exibicao para `MForm` e solicita foco quando editavel. 🟢 |
| `MEditor.updateLocation` | `()` | `void` | Atualiza campo `location` com linha/coluna calculadas via `modelToView`. 🟢 |
| `MEditor.read` | `(r: FileReader, o: Object)` | `void` | Delega leitura para `editArea.read(r, o)`. 🟢 |
| `MEditor.toggleInsertMode` | `()` | `void` | Alterna `OverwriteCaret.insert` e atualiza campo `mode`. 🟢 |
| `MEditorKit.createDefaultDocument` | `()` | `Document` | Cria `MEditorDocument` e registra listener de undo. 🟢 |
| `MEditorKit.setComponent` | `(c: JTextComponent)` | `void` | Carrega keymap e registra atalhos de pagina, undo e redo. 🟢 |
| `MEditorDocument.insertString` | `(offset: int, str: String, a: AttributeSet)` | `void` | Insere normalmente em modo Insert; remove caracteres existentes antes de inserir em modo Overwrite. 🟢 |
| `MEditorDocument.createScanner` | `()` | `Scanner` | Cria scanner baseado em `sun.tools.java.Scanner` sobre stream do documento. 🟢 |
| `MEditorContext.create` | `(elem: Element)` | `View` | Retorna `JavaView` para pintar texto com colorizacao lexical. 🟢 |
| `MEditorKeyAdapter.keyPressed` | `(e: KeyEvent)` | `void` | Alterna modo com tecla Insert e mantem buffer de ultimos caracteres. 🟢 |
| `MEditorKeyAdapter.keyTyped` | `(e: KeyEvent)` | `void` | Converte combinacoes de acento + vogal para caracteres acentuados mapeados. 🟢 |
| `MEditorTest.main` | `(args: String[])` | `void` | Teste visual manual: cria frame, le arquivo e exibe editor. 🟢 |

### Dados de entrada e saida

| Dado | Origem | Uso | Saida |
|------|--------|-----|-------|
| `formName`, `ent`, `entName` | Construtor `MEditor` | Inicializacao de `MForm` e titulo/nome funcional | Estado interno do formulario. 🟢 |
| `type` | Construtor `MEditor` | Define content type do `JEditorPane` | Comportamento do editor kit ativo. 🟢 |
| `FileReader` | `MEditor.read` | Carrega texto externo | Conteudo dentro de `editArea`. 🟢 |
| `KeyEvent` | `MEditorKeyAdapter` | Alterna modo e compoe acentos | Mutacao do evento ou estado do caret. 🟢 |
| Documento Swing | `MEditorDocument` | Fonte do scanner e de edicoes | Texto renderizado/editado. 🟢 |

## Fluxo Principal

1. `MEditor` chama `super(formName, ent)` e registra `entName` no formulario. 🟢 `PT/Minimal/editor/MEditor.java:40`
2. Cria `JEditorPane`, define fundo branco, modo editavel, tamanho inicial e fonte `Monospaced` 12. 🟢 `PT/Minimal/editor/MEditor.java:44`
3. Instancia `MEditorKit`, associa o componente ao kit, registra o kit para `text/java` e aplica o `contentType` recebido. 🟢 `PT/Minimal/editor/MEditor.java:53`
4. Registra `MEditorKeyAdapter` para tratar Insert e composicao de acentos. 🟢 `PT/Minimal/editor/MEditor.java:59`
5. Registra listener de caret para recalcular linha/coluna a cada mudanca de posicao. 🟢 `PT/Minimal/editor/MEditor.java:61`
6. Calcula metricas de fonte (`xsize`, `ysize`) para dimensionamento e posicao do cursor. 🟢 `PT/Minimal/editor/MEditor.java:68`
7. Se `PT.Minimal.pnutsColor=true` e `sun.tools.java.Constants` existe, configura estilos de comentario, string e palavras-chave. 🟢 `PT/Minimal/editor/MEditor.java:71`
8. Cria campos de status `mode` e `location`, ambos em cinza claro com `SoftBevelBorder`. 🟢 `PT/Minimal/editor/MEditor.java:90`
9. Cria `OverwriteCaret`, define blink rate e o instala no `JEditorPane`. 🟢 `PT/Minimal/editor/MEditor.java:101`
10. Envolve `editArea` em `JScrollPane`, define politicas de rolagem e instala no painel central. 🟢 `PT/Minimal/editor/MEditor.java:108`
11. Cria painel inferior via `constrBotPanel`, adiciona campos de status, botoes e associa OK/END pelos nomes `wok` e `wend`. 🟢 `PT/Minimal/editor/MEditor.java:119`
12. Finaliza com `prepareForm()`, herdando ciclo de vida do framework MinimalJ. 🟢 `PT/Minimal/editor/MEditor.java:129`

## Fluxos Alternativos

- **Texto vazio em `setText`:** nao altera conteudo; posiciona caret em `0`. 🟢 `PT/Minimal/editor/MEditor.java:160`
- **Texto nao vazio em `setText`:** percorre caracteres para achar maior linha, ajusta largura da area e posiciona caret em `1`. 🟢 `PT/Minimal/editor/MEditor.java:145`
- **Modo Overwrite:** `MEditorDocument.insertString` remove caracteres a partir do offset ate o tamanho da string ou fim do texto, depois insere a string. 🟢 `PT/Minimal/editor/MEditorDocument.java:37`
- **Scanner indisponivel:** `createScanner` captura `IOException` e retorna `null`; uso posterior pode perder colorizacao ou falhar na view. 🟢 `PT/Minimal/editor/MEditorDocument.java:54`
- **Falha de scanner durante pintura:** `JavaView.updateScanner` captura `Throwable` e imprime stack trace. 🟢 `PT/Minimal/editor/MEditorContext.java:213`
- **Undo/redo impossivel:** `UndoAction` e `RedoAction` capturam `CannotUndoException`/`CannotRedoException`, imprimem erro e atualizam estado das acoes. 🟢 `PT/Minimal/editor/MEditorKit.java:196`
- **Combinacao de acento nao mapeada:** `wsetkey` fica `0`, evento nao recebe `setKeyChar` e buffer e limpo. 🟢 `PT/Minimal/editor/MEditorKeyAdapter.java:110`

## Dependencias

- `PT.Minimal.geral.MForm`: classe base do editor, fornece paineis, botoes, ciclo de exibicao e `prepareForm`. 🟢
- `PT.Minimal.geral.MButton`, `MPanel`, `MUtil`: constroem painel inferior, buscam componentes `wok`/`wend`, verificam existencia de classe e solicitam foco. 🟢
- Swing/AWT (`JEditorPane`, `JScrollPane`, `JTextField`, `DefaultCaret`, `TextAction`, `ViewFactory`): infraestrutura visual e de edicao. 🟢
- `javax.swing.undo.UndoManager`: historico de undo/redo. 🟢
- `sun.tools.java.Constants`, `Scanner`, `Environment`: lexer Java legado usado para tokens, nomes de operadores e colorizacao. 🟢
- `PT.Minimal.util.AltGrPatch`: usado pelo teste visual manual antes de abrir o editor. 🟢

## Decisoes de Design Identificadas

| Decisao | Evidencia no codigo | Confianca |
|---------|---------------------|-----------|
| Editor implementado como formulario completo, nao apenas componente isolado. | `PT/Minimal/editor/MEditor.java:29`, `PT/Minimal/editor/MEditor.java:119`, `PT/Minimal/editor/MEditor.java:129` | 🟢 |
| `JEditorPane` usa `MEditorKit` customizado para content type `text/java`. | `PT/Minimal/editor/MEditor.java:53`, `PT/Minimal/editor/MEditorKit.java:60` | 🟢 |
| Colorizacao lexical acoplada a `sun.tools.java.*`, API interna do JDK antigo. | `PT/Minimal/editor/MEditorToken.java`, `PT/Minimal/editor/MEditorDocument.java:16`, `PT/Minimal/editor/MEditor.java:71` | 🟢 |
| Undo/redo vive no `MEditorKit`, resetado a cada `showForm`. | `PT/Minimal/editor/MEditorKit.java:100`, `PT/Minimal/editor/MEditor.java:168` | 🟢 |
| Modo Overwrite e uma regra de documento, acionada pelo estado do caret. | `PT/Minimal/editor/MEditor.java:196`, `PT/Minimal/editor/MEditorDocument.java:37` | 🟢 |
| Acentuacao portuguesa e tratada manualmente no listener de teclado. | `PT/Minimal/editor/MEditorKeyAdapter.java:35` | 🟢 |
| Teste visual depende de caminho local absoluto quando argumento nao e informado. | `PT/Minimal/editor/MEditorTest.java:24` | 🟢 |

## Estado Interno

| Campo | Classe | Tipo | Evolucao |
|-------|--------|------|----------|
| `kit` | `MEditor` | `MEditorKit` | Criado no construtor, ligado a `editArea`, usado para resetar undo e obter estilos. 🟢 |
| `editArea` | `MEditor` | `JEditorPane` | Recebe texto, content type, editor kit, listeners e caret. 🟢 |
| `mode` | `MEditor` | `JTextField` | Comeca como `Insert`; muda para `Overwrite` quando caret alterna. 🟢 |
| `location` | `MEditor` | `JTextField` | Comeca como `Line = 0 Column = 0`; atualizado em eventos de caret. 🟢 |
| `caret.insert` | `OverwriteCaret` | `boolean` | Comeca `true`; alterna em `toggleInsertMode`. 🟢 |
| `undo` | `MEditorKit` | `UndoManager` | Recebe edits do documento; descartado em `resetUndo`. 🟢 |
| `preferences` | `MEditorKit` | `MEditorContext` | Criado sob demanda e usado como `ViewFactory`. 🟢 |
| `tokenStyles` | `MEditorContext` | `Style[]` | Criado a partir de `MEditorToken.all` no construtor. 🟢 |
| `lexer`/`lexerValid` | `JavaView` | scanner + flag | Scanner e reiniciado/avancado durante pintura de texto. 🟢 |
| `wacc` | `MEditorKeyAdapter` | `char[3]` | Guarda historico curto de teclas para composicao de acentos e e limpo apos `keyTyped`. 🟢 |

## Observabilidade

- `MEditor.setText` imprime `!*** MEditor setText` em stdout. 🟢 `PT/Minimal/editor/MEditor.java:143`
- `MEditorKit.getActions` imprime `!*** MEditorKit getActions` em stdout. 🟢 `PT/Minimal/editor/MEditorKit.java:122`
- `MEditorKit.setComponent` imprime `!*** MEditorKit setComponent` em stdout. 🟢 `PT/Minimal/editor/MEditorKit.java:364`
- Falhas de undo/redo imprimem mensagem e stack trace. 🟢 `PT/Minimal/editor/MEditorKit.java:200`
- `LocalEnvironment.error` imprime erros do scanner em stderr com localizacao e argumentos. 🟢 `PT/Minimal/editor/MEditorDocument.java:285`
- `JavaView.updateScanner` imprime stack trace para qualquer `Throwable`. 🟢 `PT/Minimal/editor/MEditorContext.java:222`
- Nao ha metricas, traces estruturados ou logger dedicado identificados nesta unit. 🟢

## Riscos e Lacunas

- 🟢 Dependencia em `sun.tools.java.*` limita compilacao/execucao em JDKs modernos; migracao requer lexer alternativo.
- 🟢 `MEditorToken.equals` compara `scanValue == scanValue`, retornando verdadeiro para qualquer `MEditorToken`; isso pode distorcer igualdade de tokens.
- 🟢 `MEditorKit.clone` atribui `preferences = preferences`, sem copiar o estado para a instancia clonada de forma clara.
- 🟢 `MEditor.updateLocation` usa nomes `x` e `y` sem declarar variaveis locais a partir do `Rectangle r`; no fonte analisado isso sugere codigo decompilado ou erro de compilacao.
- 🟢 `MEditorDocument` declara classe interna `Scanner extends Scanner`, nome que conflita com import `sun.tools.java.Scanner` e pode depender de forma especifica de fonte/decompilacao.
- 🔴 Nao ha evidencia de testes automatizados para caret, acentuacao, undo/redo ou colorizacao; validacao atual parece manual via `MEditorTest`.
