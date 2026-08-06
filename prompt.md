CONTEXTO DO PROJETO

Trabalho em uma empresa que usa uma linguagem de scripting legada chamada
Pnuts, rodando sobre a JVM (compatível até Java 8), com sintaxe parecida
com JavaScript/Java. Não existe formatter, linter, LSP ou syntax
highlighting moderno para essa linguagem. Temos atualmente 6.944 arquivos
`.pnuts` no projeto, com esse número crescendo diariamente.

Você está rodando localmente, com acesso ao sistema de arquivos e a
comandos de shell. Você tem free tier limitado, então trabalhe de forma
incremental, evitando reprocessar arquivos grandes desnecessariamente
mais de uma vez, e me avise se perceber que uma tarefa vai consumir
muito contexto/chamadas antes de executá-la.

OBJETIVO GERAL

Construir uma gramática Tree-sitter mínima e tolerante a erros para a
linguagem Pnuts, associada à extensão de arquivo `.pnuts`, com:

1. Highlighting básico funcionando no VSCode e no Zed.
2. Um formatter baseado em Topiary (queries .scm sobre a árvore
   Tree-sitter), cobrindo, em ordem de prioridade:
   a. Indentação consistente baseada em profundidade de blocos `{}`.
   b. Quebra de linha automática/normalizada.
   c. Inserção/normalização de ponto e vírgula ao fim de statements.
3. NÃO é objetivo deste projeto: autocomplete, LSP completo, análise
   semântica profunda, ou validação de tipos. O foco é estritamente
   formatação sintática superficial.

A gramática deve ser TOLERANTE: comandos/estruturas específicas do Pnuts
que não forem totalmente mapeadas devem virar nós genéricos de
expressão/statement, sem quebrar o parsing do arquivo inteiro (Pnuts
mistura sintaxe JS-like com comandos próprios do framework interno da
empresa, ainda não totalmente documentados).

DECISÕES TÉCNICAS JÁ TOMADAS

- Linguagem de implementação do tooling: Rust.
- Motor de parsing: Tree-sitter (grammar.js -> parser em C, com bindings
  Rust via crate tree-sitter).
- Motor de formatação: Topiary, usando queries declarativas (.scm) em
  cima da árvore Tree-sitter, em vez de um pretty-printer escrito à mão.
- Extensão de arquivo definida: .pnuts (já confirmado que não conflita
  com uso relevante existente).
- Ferramenta de execução deste prompt: Gemini CLI, no free tier.

FONTES DISPONÍVEIS LOCALMENTE

1. Repositório oficial (abandonado há ~9 anos) do Pnuts, em:
   [/pnuts-repo]

   Estrutura relevante já identificada:
   - doc/lang.html          -> especificação formal da sintaxe (PRIORITÁRIO)
   - doc/quickref.html      -> referência rápida de palavras-chave
   - doc/convention.html    -> possíveis convenções oficiais de estilo
   - doc/type.html          -> sistema de tipos
   - doc/command.html       -> comandos/estruturas de controle
   - doc.ja/ e doc.pl/      -> traduções, IGNORAR
   - api/src/               -> provável parser/lexer Java original
   - launcher/              -> scripts de execução, baixa prioridade

2. Amostra de arquivos `.pnuts` reais do meu projeto de trabalho, em:
   [/pnuts-samples]

   Inclui arquivos pequenos, médios, grandes, com `foreach`, `if/else`,
   `for`, `while`, `try/catch`, chamadas encadeadas tipo
   `wrows.elements()`, strings com caracteres especiais, e comentários
   de linha e bloco.

   Exemplo real de sintaxe que precisa ser reconhecido:
     foreach wrow(wrows.elements()) {
       // corpo do loop
     }

DOCUMENTAÇÃO TÉCNICA DE REFERÊNCIA

- Tree-sitter (docs principais):
  <https://tree-sitter.github.io/tree-sitter/>
- Tree-sitter (criação de gramáticas):
  <https://tree-sitter.github.io/tree-sitter/creating-parsers>
- Tree-sitter (CLI):
  <https://tree-sitter.github.io/tree-sitter/cli>
- Tree-sitter (repositório oficial):
  <https://github.com/tree-sitter/tree-sitter>
- Lista de gramáticas existentes (referência, linguagens tipo JS/Java):
  <https://github.com/tree-sitter/tree-sitter/wiki/List-of-parsers>
- Topiary (repositório oficial, docs de queries .scm de formatação):
  <https://github.com/tweag/topiary>
- VSCode (Extension API):
  <https://code.visualstudio.com/api>
- VSCode (syntax highlighting via TextMate grammar):
  <https://code.visualstudio.com/api/language-extensions/syntax-highlight-guide>
- VSCode (language extensions, visão geral):
  <https://code.visualstudio.com/api/language-extensions/overview>
- VSCode (DocumentFormattingEditProvider):
  <https://code.visualstudio.com/api/references/vscode-api>
- Zed (docs gerais):
  <https://zed.dev/docs>
- Zed (desenvolvimento de extensões):
  <https://zed.dev/docs/extensions/developing-extensions>
- Zed (suporte a linguagens):
  <https://zed.dev/docs/extensions/languages>
- Zed (repositório oficial de extensões, exemplos reais):
  <https://github.com/zed-industries/extensions>
- Repositório oficial (inativo) do Pnuts:
  <https://github.com/tomatsu/pnuts>

FASES DE EXECUÇÃO (respeite esta ordem, não pule etapas)

FASE 0 - VALIDAÇÃO DE AMBIENTE

- Confirme que rustc, cargo, tree-sitter-cli e topiary estão instalados
  e acessíveis no PATH. Reporte as versões encontradas.
- Confirme que os caminhos locais do repositório Pnuts e da amostra de
  arquivos .pnuts existem e não estão vazios.

FASE 1 - LEVANTAMENTO DA GRAMÁTICA

- Leia doc/lang.html, doc/quickref.html, doc/convention.html,
  doc/type.html e doc/command.html.
- Liste, de forma estruturada: tokens, delimitadores, regras de bloco,
  regra de terminação de statement (ponto e vírgula obrigatório,
  opcional, ou ASI), e convenções de estilo já documentadas oficialmente.
- Identifique o arquivo principal de parser/lexer dentro de api/src/ e
  reporte se há divergências entre o código-fonte e o que está
  documentado em doc/lang.html.

FASE 2 - CRUZAMENTO COM CÓDIGO REAL

- Percorra a amostra em pnuts-samples/ e identifique construções
  sintáticas usadas no dia a dia que NÃO estão cobertas pela
  documentação/parser original (prováveis comandos customizados do
  framework interno, como o exemplo `foreach wrow(...)` acima).
- Gere uma lista consolidada dessas divergências antes de prosseguir.

FASE 3 - PROPOSTA DE ARQUITETURA (aguarde minha aprovação aqui)

- Apresente a estrutura de pastas proposta para o projeto (gramática,
  queries de highlighting, queries de formatação do Topiary, extensão
  VSCode, extensão Zed, corpus de testes).
- Não crie os arquivos ainda nesta fase, apenas descreva o plano.

FASE 4 - GRAMÁTICA TREE-SITTER

- Implemente grammar.js de forma tolerante a erros, cobrindo o que foi
  levantado nas Fases 1 e 2.
- Trate comandos desconhecidos do framework interno como nós genéricos
  de expressão/statement.
- Rode tree-sitter generate e tree-sitter test, criando um corpus de
  testes baseado nos arquivos reais da amostra.

FASE 5 - FORMATAÇÃO COM TOPIARY

- Escreva as queries .scm de formatação para: indentação por bloco,
  quebra de linha, e inserção/normalização de ponto e vírgula.
- Rode o Topiary contra a amostra em modo de comparação (diff), sem
  sobrescrever arquivos ainda.
- Reporte casos em que a formatação ficou incorreta ou arriscada.

FASE 6 - INTEGRAÇÃO COM EDITORES

- Monte a estrutura mínima de extensão para VSCode (highlighting +
  registro do formatter) e para Zed (extension.toml + gramática +
  formatter externo apontando para o binário/queries do Topiary).

FASE 7 - DOCUMENTAÇÃO FINAL

- Gere um README curto explicando como instalar as extensões, como
  rodar o formatter em lote sobre os arquivos .pnuts existentes, e como
  rodar em modo "apenas diff" antes de aplicar em produção.

REGRAS DE SEGURANÇA

- Nunca sobrescreva arquivos .pnuts reais do projeto sem antes gerar um
  diff e eu aprovar.
- Trabalhe sempre dentro de uma branch Git dedicada.
- Se encontrar uma construção sintática ambígua que não consiga resolver
  nem pela documentação nem pelo parser original, PARE e me pergunte,
  trazendo o trecho de código exato em questão, em vez de assumir um
  comportamento.
- Se perceber que está prestes a consumir uma quantidade grande de
  chamadas/contexto do free tier em uma única tarefa (ex: ler todos os
  6.944 arquivos de uma vez), me avise antes e sugira uma abordagem em
  lotes menores.

Comece pela FASE 0 e me reporte o resultado antes de avançar.
