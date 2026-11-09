# Editor Lexico e Componentes Graficos

## Visao Geral

A unit fornece um editor Swing embutido em `MForm`, com area `JEditorPane`, barras de rolagem, botoes padrao de formulario e status de modo/posicao do cursor. 🟢

O editor suporta conteudo `text/java`, colorizacao por tokens Java quando `PT.Minimal.pnutsColor=true` e `sun.tools.java.Constants` existe no runtime, alem de leitura de arquivos por `FileReader`. 🟢

O componente tambem trata edicao em modo insert/overwrite, undo/redo, selecao por pagina e composicao manual de caracteres acentuados usados em portugues. 🟢

## Responsabilidades

- Exibir e editar texto monoespacado dentro de uma tela Swing integrada ao framework `MForm`. 🟢
- Registrar `MEditorKit` como kit de edicao para `text/java` e aplicar `MEditorDocument` como documento padrao. 🟢
- Colorizar texto Java a partir de `sun.tools.java.Scanner`, `MEditorToken` e `MEditorContext`. 🟢
- Alternar entre modo Insert e Overwrite via tecla Insert e atualizar o indicador visual do modo. 🟢
- Manter indicador textual de linha e coluna com base na posicao do caret. 🟢
- Disponibilizar undo/redo via `UndoManager` e atalhos Ctrl+Z/Ctrl+Y. 🟢
- Adaptar combinacoes de acentos (`~`, `^`, `´`, `` ` ``, `¨`) para vogais acentuadas. 🟢

## Regras de Negocio

- O editor deve herdar o ciclo de vida de `MForm`, incluindo paineis, botoes OK/END e chamada de `prepareForm()`. 🟢
- A area editavel deve usar fonte `Monospaced`, fundo branco e tipo de conteudo informado no construtor. 🟢
- A colorizacao especial so deve ser configurada quando a propriedade `PT.Minimal.pnutsColor` estiver ativa e a classe `sun.tools.java.Constants` existir. 🟢
- Comentarios de bloco iniciados por `/*` e ainda nao encerrados por `*/` devem marcar as linhas seguintes com `CommentAttribute` para orientar o scanner. 🟢
- Em modo Overwrite, a insercao deve remover caracteres existentes na faixa coberta pelo novo texto antes de inserir a string. 🟢
- A tecla Insert deve alternar o modo do caret e o texto do campo `mode` entre `Insert` e `Overwrite`. 🟢
- Atalhos de teclado devem mapear Home/End com Ctrl e Shift, Ctrl+A, Shift+PageUp/PageDown, Ctrl+Z e Ctrl+Y. 🟢
- A composicao manual de acentos cobre combinacoes explicitas de vogais maiusculas/minusculas para til, circunflexo, agudo, grave e trema; outras combinacoes nao recebem transformacao especial. 🟢
- O editor de teste carrega um arquivo por argumento de linha de comando ou usa caminho local fixo `v:\devjava\src\PT\Minimal\geral\MBrowse.java` como fallback. 🟢
- A confiabilidade em Java moderno e limitada porque o lexer depende de APIs internas `sun.tools.java.*`. 🟢

## Requisitos Funcionais

| ID | Requisito | Prioridade | Criterio de Aceite |
|----|-----------|------------|--------------------|
| RF-01 | Criar tela de editor como subclasse de `MForm`, com `JEditorPane`, `JScrollPane`, painel inferior e botoes OK/END. 🟢 | Must | Dado um construtor `MEditor(formName, ent, entName, type)`, quando instanciado, entao a tela contem area central rolavel, campo de modo, campo de localizacao e botoes padrao obtidos do painel inferior. |
| RF-02 | Registrar `MEditorKit` no `JEditorPane` para `text/java` e aplicar o content type recebido pelo construtor. 🟢 | Must | Dado `type="text/java"`, quando o editor inicia, entao `editArea` usa `MEditorKit` e documento criado por `MEditorDocument`. |
| RF-03 | Permitir setar, obter e ler texto por `setText`, `getText` e `read(FileReader,Object)`. 🟢 | Must | Dado texto ou arquivo valido, quando carregado no editor, entao o conteudo fica disponivel em `editArea.getText()`. |
| RF-04 | Ajustar largura interna do editor com base no maior comprimento de linha ao receber texto nao vazio. 🟢 | Should | Dado texto multiline, quando `setText` executa, entao `editArea.setSize(maxLength * xsize, getHeight())` e chamado antes de aplicar o texto. |
| RF-05 | Alternar Insert/Overwrite por tecla Insert e refletir o estado no campo `mode`. 🟢 | Must | Dado editor focado, quando usuario pressiona Insert, entao `toggleInsertMode()` inverte o modo do caret e atualiza `mode`. |
| RF-06 | Em modo Overwrite, substituir caracteres existentes durante `insertString`. 🟢 | Must | Dado caret em modo Overwrite, quando usuario digita texto, entao o documento remove ate o tamanho do texto novo antes de inserir. |
| RF-07 | Atualizar campo de localizacao com linha e coluna calculadas a partir do caret. 🟢 | Should | Dado movimento do caret, quando evento `CaretEvent` ocorre, entao `location` recebe texto no formato `Line = X Column = Y`. |
| RF-08 | Suportar undo/redo e selecao por pagina via acoes registradas em `MEditorKit`. 🟢 | Should | Dado edicoes no documento, quando usuario usa Ctrl+Z/Ctrl+Y ou Shift+PageUp/PageDown, entao as acoes registradas sao executadas. |
| RF-09 | Colorizar tokens Java quando a configuracao e o runtime permitirem. 🟢 | Should | Dado `PT.Minimal.pnutsColor=true` e `sun.tools.java.Constants` presente, quando texto Java e renderizado, entao estilos de comentario, string e palavras-chave recebem cores configuradas. |
| RF-10 | Compor caracteres acentuados portugueses a partir de tecla morta seguida de vogal suportada. 🟢 | Should | Dado sequencia `~` + `a`, quando `keyTyped` processa o evento sem modificadores, entao o caractere digitado vira `ã`; sequencias nao mapeadas permanecem sem substituicao especial. |
| RF-11 | Disponibilizar classe manual de teste visual para abrir arquivo em janela `MFrame`. 🟢 | Could | Dado `MEditorTest` com caminho valido, quando executado, entao cria `MFrame`, injeta `MEditor`, le arquivo e exibe formulario. |

## Requisitos Nao Funcionais

| Tipo | Requisito inferido | Evidencia no codigo | Confianca |
|------|--------------------|---------------------|-----------|
| Compatibilidade | Runtime precisa expor `sun.tools.java.Constants`, `sun.tools.java.Scanner` e `sun.tools.java.Environment` para colorizacao/lexer completo. | `PT/Minimal/editor/MEditor.java:71`, `PT/Minimal/editor/MEditorDocument.java:16`, `PT/Minimal/editor/MEditorDocument.java:61` | 🟢 |
| Usabilidade | Editor deve manter fonte monoespacada, indicador de modo e indicador de linha/coluna para edicao de codigo/texto tecnico. | `PT/Minimal/editor/MEditor.java:49`, `PT/Minimal/editor/MEditor.java:90`, `PT/Minimal/editor/MEditor.java:95`, `PT/Minimal/editor/MEditor.java:182` | 🟢 |
| Usabilidade | Atalhos basicos de edicao devem existir para selecionar tudo, navegar ate inicio/fim, desfazer, refazer e selecionar por pagina. | `PT/Minimal/editor/MEditorKit.java:338`, `PT/Minimal/editor/MEditorKit.java:367`, `PT/Minimal/editor/MEditorKit.java:372`, `PT/Minimal/editor/MEditorKit.java:377`, `PT/Minimal/editor/MEditorKit.java:382` | 🟢 |
| Manutenibilidade | Colorizacao depende de APIs internas removidas em runtimes modernos, logo migracao para Java atual exige trocar o scanner. | `PT/Minimal/editor/MEditorToken.java`, `PT/Minimal/editor/MEditorDocument.java:16` | 🟢 |
| Confiabilidade | Falhas de scanner durante pintura sao capturadas de forma generica e apenas impressas, sem recuperacao estruturada para o usuario. | `PT/Minimal/editor/MEditorContext.java:213` | 🟢 |

## Criterios de Aceitacao

```gherkin
Cenario: abrir editor Java com estrutura visual basica
Dado uma chamada a MEditor("EDITOR", "EDIT", "Editor Teste", "text/java")
Quando o construtor termina
Entao o formulario possui JEditorPane rolavel, campo "Insert", campo "Line = 0 Column = 0" e botoes OK/END

Cenario: carregar texto no editor
Dado um MEditor instanciado
Quando setText recebe texto nao vazio com multiplas linhas
Entao o JEditorPane recebe o texto
E sua largura interna considera o maior comprimento de linha

Cenario: alternar modo de digitacao
Dado o editor em modo Insert
Quando o usuario pressiona a tecla Insert
Entao o caret alterna para Overwrite
E o campo de modo exibe "Overwrite"

Cenario: sobrescrever texto existente
Dado o documento com texto existente e caret em modo Overwrite
Quando o usuario digita uma string
Entao o documento remove caracteres existentes na faixa da string
E insere a nova string na mesma posicao

Cenario: colorir Java quando configurado
Dado a propriedade PT.Minimal.pnutsColor=true
E a classe sun.tools.java.Constants disponivel
Quando o editor renderiza conteudo text/java
Entao comentarios, strings e palavras-chave recebem estilos de cor configurados

Cenario: runtime sem APIs internas de scanner
Dado ambiente sem sun.tools.java.Constants
Quando o editor e construido
Entao a configuracao explicita de cores Pnuts/Java nao e aplicada
E a aplicacao continua criando o editor Swing basico

Cenario: compor acentos
Dado sequencia de teclas sem modificadores
Quando o usuario digita "~" seguido de "a"
Entao o evento tipado e alterado para "ã"
```

## Prioridade (MoSCoW)

| Requisito | MoSCoW | Justificativa |
|-----------|--------|---------------|
| Estrutura Swing integrada a `MForm` | Must | Sem a subclasse de `MForm`, o editor nao participa do padrao de telas do MinimalJ. 🟢 |
| Carregamento e edicao de texto | Must | `setText`, `getText`, `read` e documento editavel formam o fluxo principal do componente. 🟢 |
| Insert/Overwrite | Must | O documento altera comportamento de insercao conforme modo do caret. 🟢 |
| Colorizacao Java | Should | Depende de propriedade e APIs internas; editor basico funciona sem ativar cores. 🟢 |
| Undo/redo e selecao por pagina | Should | Melhora edicao, mas nao bloqueia exibicao/carregamento do texto. 🟢 |
| Composicao manual de acentos | Should | Importante para entrada pt-PT/pt-BR, mas restrita a combinacoes mapeadas. 🟢 |
| `MEditorTest` | Could | E ferramenta manual de teste visual, nao fluxo produtivo principal. 🟢 |

## Rastreabilidade de Codigo

| Arquivo | Funcao / Classe | Cobertura |
|---------|-----------------|-----------|
| `PT/Minimal/editor/MEditor.java` | `MEditor`, `setText`, `getText`, `read`, `toggleInsertMode`, `OverwriteCaret` | 🟢 |
| `PT/Minimal/editor/MEditorKit.java` | `MEditorKit`, `createDefaultDocument`, `UndoAction`, `RedoAction`, `SelectionPageUpAction`, `SelectionPageDownAction`, `setComponent` | 🟢 |
| `PT/Minimal/editor/MEditorDocument.java` | `MEditorDocument`, `insertString`, `createScanner`, `getScannerStart`, `insertUpdate`, `DocumentInputStream`, `LocalEnvironment` | 🟢 |
| `PT/Minimal/editor/MEditorContext.java` | `MEditorContext`, `JavaView`, `drawUnselectedText`, `updateScanner` | 🟢 |
| `PT/Minimal/editor/MEditorKeyAdapter.java` | `keyPressed`, `keyTyped` | 🟢 |
| `PT/Minimal/editor/MEditorToken.java` | Categorias e valores de tokens Java usados por estilos | 🟢 |
| `PT/Minimal/editor/MEditorTest.java` | `main` de teste visual manual | 🟢 |
