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


## 4. Editores Suportados (REGRA ESTRITA: APENAS VS CODE)

- **VS Code (`vscode-pnuts`) - EDITOR ÚNICO E EXCLUSIVO DO PROJETO:**
  - Empacotado como extensão `.vsix` (`vscode-pnuts-0.3.13.vsix`).
  - Executa no host local ou no VS Code Server (`code-server` / Remote-SSH).
  - Suporta `.pnuts`, `.pnut` e `.groovy`.
  - **REGRA PERMANENTE:** O Zed foi COMPLETAMENTE ABANDONADO e descontinuado. NUNCA mencione, olhe, sugira ou altere qualquer arquivo relacionado ao Zed (`zed-pnuts`). Todo o foco do projeto é 100% exclusivo no VS Code.

---

## 5. Diretrizes para Agentes de IA

1. **Foco Exclusivo no VS Code:** Trabalhar apenas nas ferramentas do VS Code e no core do `pnuts-lsp`.
2. **NUNCA Olhar para o Zed:** Não gastar tokens nem tempo com Zed.
3. **Passo a Passo Interativo:** Fazer uma única coisa por vez e validar com o usuário antes de avançar.
4. **Verificação de Regressão:** Antes de concluir qualquer alteração no parser ou formatador, rodar os testes (`cargo test`).

---

## 6. Estado Atual e Roteiro Imediato para Retomada ("vamos continuar")

Quando o usuário retornar e disser "vamos continuar", apresentar IMEDIATAMENTE os passos de teste abaixo:

### ⚠️ AÇÃO OBRIGATÓRIA ANTES DE TESTAR:
O VS Code Server precisa recarregar a janela para descarregar a versão antiga em memória (`0.3.12`) e inicializar a nova versão (`0.3.13`):
- Pressione `Ctrl + Shift + P` -> execute **`Developer: Reload Window`** (Recarregar Janela).
*(Sem o reload, a janela aberta continua rodando a versão anterior que não possuía `rangeFormattingProvider` registrado no LSP, fazendo o VS Code acionar o fallback de formatar o arquivo inteiro).*

### O que o usuário deve testar na v0.3.13:
1. **Format Selection (`Ctrl + K, Ctrl + F`):**
   - Selecionar apenas 2 ou 3 linhas desformatadas dentro de uma função.
   - Pressionar `Ctrl + K, Ctrl + F`.
   - Confirmar que **apenas a seleção** é formatada, mantendo a indentação e o restante do arquivo intocados.
2. **Colorização de Sintaxe do Groovy (`.groovy`):**
   - Abrir qualquer script `.groovy`.
   - Confirmar o realce vivo de cores: anotações (`@Override`, `@CompileStatic`), palavras-chave (`def`), strings interpoladas (`"${var}"`), strings multilinhas (`"""`) e operadores.
3. **Find References (`Shift + F12`):**
   - Posicionar o cursor em uma função ou variável e apertar `Shift + F12`.
   - Confirmar se o painel do VS Code lista todas as referências no arquivo.
4. **Rename Symbol (`F2`):**
   - Posicionar o cursor em uma variável local ou parâmetro, apertar `F2`, digitar o novo nome e dar Enter.

