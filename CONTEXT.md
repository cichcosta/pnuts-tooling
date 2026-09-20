# Memória e Contexto do Projeto: Pnuts & Groovy Tooling

Este arquivo é a fonte da verdade sobre o estado atual do projeto, decisões técnicas consolidadas, bugs já resolvidos e lições aprendidas. Qualquer agente de IA ou desenvolvedor deve consultar este arquivo para manter o contexto vivo e evitar retrabalho ou regressões.

---

## 1. Informações Básicas do Repositório

- **Repositório:** `pnuts-tooling`
- **Branch Ativa para o LSP:** `feature/pnuts-groovy-lsp`
- **Branch Base:** `pnuts-grammar-bootstrap` (derivada da `main`)
- **Linguagens Suportadas:**
  - **Pnuts:** `.pnuts`, `.pnut`
  - **Groovy:** `.groovy`

---

## 2. Decisões de Arquitetura Consolidadas

1. **Parser Central:** Tree-sitter (`tree-sitter-pnuts`).
   - Gramática em `tree-sitter-pnuts/grammar.js`.
   - Gera parser em C compilado para shared libraries (`.so`, `.dll`, `.dylib`) e `.wasm` (usado nativamente pelo Zed e VSCode).
2. **Motor de Formatação Atual:** Topiary CLI + queries em Scheme (`pnuts.scm`).
   - Binários empacotados em `resources/bin/` e executáveis em `bin/`.
3. **Language Server Protocol (`pnuts-lsp` - Fase 2 Concluída):**
   - Implementado em Rust com `tower-lsp` e `tokio` (comunicação stdio / JSON-RPC).
   - Workspace Cargo raiz contendo `pnuts-lsp` e `tree-sitter-pnuts`.
   - Gerenciamento de documentos em memória (`DocumentStore`) tolerante a esquemas `file://` e buffers não salvos `untitled:`.
   - Re-parsing incremental via AST Tree-sitter em `didOpen`, `didChange`, `didSave` e remoção em `didClose`.
   - Logs de tracing roteados exclusivamente para `stderr`, preservando o canal `stdout` para mensagens JSON-RPC.

---

## 3. Histórico de Bugs Críticos Resolvidos na Gramática (Regressões Proibidas)

Ao modificar `grammar.js`, NUNCA reintroduzir os seguintes erros:

1. **Literais de Caractere (`char(34)`):**
   - O Pnuts suporta a sintaxe `char(34)` para representar caracteres como aspas ou caracteres especiais.
   - Mapeado como regra específica no parser.
2. **Loops `for (x in y)` e `foreach x(y)`:**
   - Suporte a ambas as variantes de iteração de coleções Java/Groovy/Pnuts.
3. **Mapas Vazios e Literais (`wmap = [:]`):**
   - Na sintaxe Groovy/Pnuts, `[:]` define um mapa vazio. Anteriormente colidia com subscripts de array.
4. **Tratamento de Exceções (`catch (Exception e)`):**
   - Suporte a tipos de exceção tipados com declaração de variável e blocos multilinhas.
5. **Comentários Inline no Fim da Linha:**
   - Regra de formatação: comentários no final de comandos são movidos para a linha anterior com indentação preservada para evitar quebras de bloco.
6. **Espaçamento de Blocos Multilinhas:**
   - Blocos com mais de uma linha de conteúdo recebem uma linha em branco após a abertura `{` e nenhuma linha em branco antes do fechamento `}`.
7. **Formatação de Cláusulas `switch` (`case` e `default`):**
   - A sintaxe do compilador/runtime Pnuts (`Pnuts.jjt` / JavaCC) não aceita quebras de linha (`<EOL>`) entre a expressão do case e os dois pontos (`"case" Expression(env) ":"`). Uma quebra de linha antes de `:` gerava o erro em runtime: `MError getAuxMessage(): Encountered "\n" at column ...`.
   - No Topiary (`pnuts.scm`), a query anterior continha `(switch_case (_) @append_hardline)` que inseria uma quebra de linha imediatamente após o valor do case (ex: `"2017"`), empurrando o `:` para a próxima linha (`case "2017"\n:`). Além disso, `":" @append_indent_start` sem `@append_indent_end` correspondente causava indentação cumulativa em escadinha para cada `case` subsequente.
   - **Correção consolidada**:
     - Statements do case formatados com `(switch_case ":" (_) @append_hardline @allow_blank_line_before)`.
     - Dois pontos colados ao valor/keyword com `":" @prepend_antispace`.
     - Delimitação de escopo de indentação com `(switch_case) @append_indent_end @allow_blank_line_before` e `(switch_default) @append_indent_end @allow_blank_line_before`.

8. **Ausência de Escapes Unicode (`\uXXXX`) em Strings com Aspas Duplas:**
   - O lexer JavaCC do runtime Pnuts (`STRING_CONTENT` em `Pnuts.jjt`) aceita apenas escapes básicos (`\\`, `\"`, `\n`, `\t`, `\b`, `\r`, `\f`). O uso de `"\uFEFF"` dentro de aspas duplas gera erro léxico/sintático em tempo de execução: `MError getAuxMessage(): Encountered "\"" at column ...`.
   - **Solução consolidada**: Validar caracteres especiais pelo seu código inteiro nativo com `int(str.charAt(0)) == 65279` ou criar o caractere com `char(65279)`.
9. **Escopo Local de Imports dentro de Functions:**
   - No runtime Pnuts/Minimal, funções (`function nome(...) { ... }`) executam em um contexto (`pnuts.lang.Context`) próprio e isolado. `import(...)` declarados no topo/escopo global do arquivo **não são herdados** dentro das funções.
   - A tentativa de instanciar classes como `ArrayList()`, `HashMap()`, etc. dentro de uma função sem import local gera o erro: `MError getAuxMessage(): <Classe> is not defined-1`.
   - **Solução consolidada**: Declarar sempre os `import(...)` necessários localmente no início do corpo de cada função.

---


## 4. Objetivos Globais e Premissas Inegociáveis do Projeto

### 4.1. Objetivo Geral
Desenvolver uma suíte completa, moderna e estável de ferramentas de desenvolvimento para **Pnuts** (`.pnuts`, `.pnut`) e **Groovy** (`.groovy`), abrangendo:
- Language Server Protocol (`pnuts-lsp` em Rust): navegação (Go to Definition), busca de referências (Find References), renomeação semântica (Rename Symbol), auto-completar inteligente com escopo e métodos Java/banco, diagnósticos de sintaxe em tempo real e formatação.
- Syntax Highlighting completo e vibrante (TextMate grammar).
- Formatação de código determinística (Topiary CLI integrado ao LSP e via stdio).

### 4.2. Premissas Fundamentais (Requisitos Obrigatórios)
1. **Multiplataforma Real (Linux, Windows, macOS, Remote-SSH):**
   - O ecossistema deve funcionar com o mesmo nível de fidelidade em Linux, Windows e macOS, bem como conectado a servidores remotos via Remote-SSH.
2. **Suporte Pleno a Buffers Não Salvos (`untitled:` / rascunhos):**
   - Caso de uso crítico: colar ou escrever código temporário em uma aba nova sem salvar no disco e usufruir de formatação automática, colorização e recursos do LSP instantaneamente.
3. **Zero Fricção para Usuários Não Técnicos:**
   - A experiência de instalação e uso deve ser "out of the box". Pessoas não técnicas não devem ser forçadas a configurar variáveis de ambiente (PATH), compilar binários ou editar caminhos absolutos manualmente no sistema operacional.
4. **100% Free / Open Source e Alta Velocidade:**
   - Priorizar ferramentas leves, abertas e ultrarrápidas, evitando IDEs pesadas e proprietárias.

---

## 5. Estratégia e Roteiro de Editores / IDEs

A ordem de trabalho acordada para as IDEs é rigorosamente sequencial:

1. **Fase 1 (Foco Atual e Imediato): VS Code (`vscode-pnuts`):**
   - Fechar, validar e estabilizar 100% das funcionalidades no VS Code antes de qualquer outra frente.
   - Empacotado via extensão `.vsix` autossuficiente (incluindo binários para Linux e Windows).
2. **Fase 2 (Próxima Frente): Neovim (`nvim`) e Helix:**
   - Após a validação completa no VS Code, criar as configurações e integrações oficiais para **Neovim** (via `nvim-lspconfig` e parser Tree-sitter) e **Helix** (`languages.toml` nativo conectando ao `pnuts-lsp` via stdio).
   - O Emacs foi avaliado como alternativa viável sob demanda, mas fica secundário a Neovim e Helix.
3. **Decisão Consolidada sobre o Zed:**
   - **ABANDONADO DEFINITIVAMENTE:** O Zed foi descartado devido a restrições arquiteturais incontornáveis (assimetria de buffers `untitled` locais vs remotos no Remote-SSH, restrições do runtime WASI e exigência de executáveis no PATH local do Windows). NUNCA mais gastar tempo, tokens ou commits com `zed-pnuts`.

---

## 6. Diretrizes para Agentes de IA

1. **Prioridade Absoluta:** Trabalhar unicamente no VS Code e no core de `pnuts-lsp` até que todos os itens estejam 100% aprovados pelo usuário.
2. **NUNCA Olhar para o Zed:** Não gastar tokens nem alterar arquivos em `zed-pnuts`.
3. **Respeitar as Premissas:** Todo novo recurso deve levar em conta buffers não salvos (`untitled:`), facilidade para o usuário final e compatibilidade de ambiente.
4. **Passo a Passo Interativo:** Fazer uma alteração por vez e obter validação antes de prosseguir.
5. **Verificação Contínua:** Rodar `cargo test` antes de propor qualquer conclusão.

---

## 7. Diagnóstico e Correções Consolidadas (v0.3.15)

### Causas Raízes Identificadas e Sanadas:
1. **Groovy Colors:** O arquivo [`syntaxes/groovy.tmLanguage.json`](file:///home/chsilva/personal/projects/pnuts-tooling/vscode-pnuts/syntaxes/groovy.tmLanguage.json) possuía um erro de escape de JSON na linha 367 (`\d+` em vez de `\\d+`), o que fazia o parser interno do VS Code disparar um `SyntaxError` e abortar completamente o carregamento da gramática, deixando arquivos `.groovy` sem realce.
2. **Format Selection:** O servidor `pnuts-lsp` busca o binário `pnuts-format` para executar a formatação e pós-processamento, porém esse executável só existia na pasta legada do Zed e nunca havia sido incluído no pacote da extensão do VS Code (`resources/bin/`). Compilamos e incluímos `pnuts-format` (Linux) e `pnuts-format.exe` (Windows) diretamente em `vscode-pnuts/resources/bin/`.
3. **Paridade Windows/Linux:** Compilamos via `cargo-zigbuild` o `pnuts-lsp.exe` e `pnuts-format.exe` com 100% de paridade das novas funcionalidades de range formatting, references e rename.

### Arquivo Empacotado:
- [`vscode-pnuts-0.3.15.vsix`](file:///home/chsilva/personal/projects/pnuts-tooling/vscode-pnuts/vscode-pnuts-0.3.15.vsix) (instalado no VS Code Server e disponível para instalação no Windows).

---

## 8. Diagnóstico e Correções Consolidadas (v0.3.16)

### Causas Raízes Identificadas e Sanadas:
1. **Perda de Cores a partir da Linha 177 (Slashy Strings vs Divisão Aritmética):**
   - Na linha 177 (`elapsedTime = (endTime - startTime) / 1000`), a regra TextMate `string.regexp.slashy.groovy` usava `(?<![a-zA-Z0-9_)\\]])\\/(?![/*])`.
   - Por ter espaços ao redor da barra (` ) / 1000 `), o lookbehind de 1 caractere no Oniguruma enxergava apenas o caractere de espaço `' '`, que não estava na lista de exclusão. Isso fazia o parser TextMate interpretar a barra de divisão `/` como o início de uma regex multilinhas (*slashy string*), engolindo todo o código subsequente até a próxima barra e corrompendo toda a colorização dali em diante.
   - **Solução consolidada:** O início de slashy strings foi restringido para contextos sintaticamente válidos onde uma regex pode iniciar em Groovy: após `=`, `=~`, `==~`, `~`, `(`, `[`, `{`, `,`, `:`, `!`, `?`, `return`, `assert`, `case`, `throw`, `yield`, `in` ou início de linha com `(?<=(?:^|[=~,(:{\\[?!]|return|assert|case|throw|yield|in)\\s*)\\/(?![/*])`. Linhas com divisão com ou sem espaços agora são 100% preservadas como operadores aritméticos.
2. **Diferenciação Cromática de Variável, Propriedade e Método (`m.idoc.trim()`):**
   - Em [`vscode-pnuts/syntaxes/groovy.tmLanguage.json`](file:///home/chsilva/personal/projects/pnuts-tooling/vscode-pnuts/syntaxes/groovy.tmLanguage.json), propriedades de objeto estavam com o escopo `variable.other.property.groovy`. Temas do VS Code (como Dark+) atribuem a mesma cor (`#9CDCFE`) para qualquer `variable.*`, fazendo com que `m` e `idoc` ficassem com a mesma cor.
   - **Solução consolidada:** Adicionamos o grupo `#properties` posicionado estrategicamente após `#functions` e mapeamos acessos a propriedades (`.`, `?.`, `*.`, `::`) para `support.type.property-name.groovy` (exatamente como já feito com sucesso na gramática Pnuts). Dessa forma:
     - `m` fica como `variable.other.groovy` (azul claro).
     - `idoc` fica como `support.type.property-name.groovy` (cor de propriedade / verde/ciano).
     - `trim()` fica como `entity.name.function.method.groovy` (amarelo).

### Arquivo Empacotado:
- [`vscode-pnuts-0.3.16.vsix`](file:///home/chsilva/personal/projects/pnuts-tooling/vscode-pnuts/vscode-pnuts-0.3.16.vsix) (gerado e instalado no VS Code Server).

---

## 9. Diagnóstico e Correções Consolidadas (v0.3.17)

### Causas Raízes Identificadas e Sanadas:
1. **Identificadores em Caixa Alta e Classes/Constantes Brancas no Pnuts (`WN`, `MConnection`, etc.):**
   - Na gramática Pnuts (`syntaxes/pnuts.tmLanguage.json`), não existia regra para `#classes` nem para `support.class.pnuts`. Identificadores iniciados com letra maiúscula como `WN`, `MConnection`, `MLogin`, `MForm` caíam diretamente em `variable.other.pnuts`, ficando com a cor padrão branca/azul indistinguível das variáveis locais.
   - No Groovy, por outro lado, existia a regra `#classes` mapeando `\b[A-Z][a-zA-Z0-9_$]*\b` para `entity.name.type.class.groovy`, o que dava a `WN` e às classes Java a cor verde/ciano de classes e constantes.
   - **Solução consolidada:**
     - Adicionamos a regra `#classes` (`entity.name.type.class.pnuts`) em [`syntaxes/pnuts.tmLanguage.json`](file:///home/chsilva/personal/projects/pnuts-tooling/vscode-pnuts/syntaxes/pnuts.tmLanguage.json#L280) e incluímos no array de `patterns` antes de `#identifiers`.
     - Adicionamos classes utilitárias de runtime Java/Pnuts em `support.class.pnuts` dentro de `types`.
     - Adicionamos a seção `#properties` e suporte a safe-navigation `?.` e `*.` também na gramática Pnuts para 100% de paridade com o Groovy.
     - Agora tanto no Pnuts quanto no Groovy:
       - `WN`, `MConnection`, `MLogin`, `MForm` $\rightarrow$ `entity.name.type.class.pnuts` (verde/ciano).
       - `werror`, `wsel`, `m`, `wrow` $\rightarrow$ `variable.other.pnuts` (azul claro).
       - `idoct`, `idoc`, `cstc`, `csto` $\rightarrow$ `support.type.property-name.pnuts` (cor de propriedade).
       - `trim()`, `getMCursor()`, `equals()` $\rightarrow$ `entity.name.function.method.pnuts` (amarelo).

### Arquivo Empacotado:
- [`vscode-pnuts-0.3.17.vsix`](file:///home/chsilva/personal/projects/pnuts-tooling/vscode-pnuts/vscode-pnuts-0.3.17.vsix) (gerado e instalado no VS Code Server).


