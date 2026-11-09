# Guia Completo: Skills Minimal & Pnuts no Antigravity

Este documento consolida o escaneamento, a instalação global, a documentação prática e o guia de uso detalhado das skills do diretório [`minimal-skills/`](../minimal-skills/), além de apresentar uma análise técnica comparativa sobre como essas skills aprimoram e complementam a skill [`pnuts`](../skills/pnuts/).

---

## Sumário
1. [Histórico de Instalação e Escaneamento](#1-histórico-de-instalação-e-escaneamento)
2. [Estrutura das Novas Skills](#2-estrutura-das-novas-skills)
3. [Guia Detalhado e Exemplos Práticos](#3-guia-detalhado-e-exemplos-práticos)
   - [Skill 1: `code-review-pnuts`](#skill-1-code-review-pnuts)
   - [Skill 2: `minimal-developer`](#skill-2-minimal-developer)
   - [Skill 3: `minimal-telas`](#skill-3-minimal-telas)
4. [Como Usar no Dia a Dia com Antigravity](#4-como-usar-no-dia-a-dia-com-antigravity)
5. [Análise: Como essas Skills Melhoram a sua Skill Pnuts](#5-análise-como-essas-skills-melhoram-a-sua-skill-pnuts)
6. [Próximos Passos Recomendados](#6-próximos-passos-recomendados)

---

## 1. Histórico de Instalação e Escaneamento

### Localização Global
O Antigravity CLI e a plataforma Gemini utilizam como diretório global de skills do usuário o caminho:
```bash
~/.gemini/config/skills/
```

### Mecanismo de Instalação por Symlinks
Para que qualquer alteração feita no repositório local seja refletida instantaneamente e de forma global em todas as sessões do Antigravity (sem duplicidade ou dessincronização de arquivos), foram criados links simbólicos apontando para os diretórios canônicos:

```bash
ln -s /home/chsilva/personal/projects/pnuts-tooling/minimal-skills/code-review-pnuts ~/.gemini/config/skills/code-review-pnuts
ln -s /home/chsilva/personal/projects/pnuts-tooling/minimal-skills/minimal-developer ~/.gemini/config/skills/minimal-developer
ln -s /home/chsilva/personal/projects/pnuts-tooling/minimal-skills/minimal-telas ~/.gemini/config/skills/minimal-telas
```

O diretório global agora contém:
* `code-review-pnuts -> /home/chsilva/personal/projects/pnuts-tooling/minimal-skills/code-review-pnuts`
* `minimal-developer -> /home/chsilva/personal/projects/pnuts-tooling/minimal-skills/minimal-developer`
* `minimal-telas -> /home/chsilva/personal/projects/pnuts-tooling/minimal-skills/minimal-telas`
* `pnuts -> /home/chsilva/personal/projects/pnuts-tooling/skills/pnuts` *(já existente)*

### Ajuste de Caminhos e Permissões
Durante o escaneamento, identificou-se que o arquivo `minimal-developer/SKILL.md` continha referências com caminhos absolutos legados do Windows (`c:\Users\jlucas\...`). Esses caminhos foram normalizados para referências relativas compatíveis com Linux e qualquer ambiente:
* `references/_reversa_sdd/architecture.md`
* `references/_reversa_sdd/domain.md`
* `references/_reversa_sdd/state-machines.md`
* `references/_reversa_sdd/permissions.md`
* `references/_reversa_sdd/code-analysis.md`

Além disso, as permissões de leitura e execução de diretórios foram padronizadas (`chmod -R 755 minimal-skills`).

---

## 2. Estrutura das Novas Skills

```
minimal-skills/
├── code-review-pnuts/
│   └── SKILL.md                  # Regras estritas de revisão, checklist e severidades
├── minimal-developer/
│   ├── SKILL.md                  # Convenções SQL, Pnuts e padrões de integração
│   └── references/
│       ├── _reversa_sdd/         # Engenharia reversa completa do ERP Minimal (Arquitetura, C4, Domínio, State Machines)
│       ├── documentação pnuts_.pdf
│       ├── pnuts-syntax.md       # Sintaxe e funções utilitárias Pnuts
│       ├── PT/                   # Notas em português
│       └── Tutorial Pnuts (1).md
└── minimal-telas/
    ├── SKILL.md                  # Guia de parametrização de telas (tabela mrfct0)
    ├── references/
    │   └── parametrizacao.md     # Dicionário de colunas e armadilhas da mrfct0
    ├── scripts/                  # Scripts auxiliares para telas
    └── templates/                # Modelos de metadados
```

---

## 3. Guia Detalhado e Exemplos Práticos

### Skill 1: `code-review-pnuts`

#### Objetivo
Atua como um revisor de código experiente focado no dialeto Pnuts do interpretador Minimal, evitando erros comuns cometidos por modelos de linguagem que assumem sintaxe pura de Java, Python ou JavaScript.

#### Regras de Ouro Inspecionadas
1. **Sem palavra-chave `new`**: Pnuts instancia classes invocando a classe diretamente como função (ex: `Date()`, `Integer(10)`).
2. **Métodos estáticos com `::`**: Métodos e constantes estáticas exigem `::` e não ponto `.` (ex: `Arrays::asList(...)`, `Math::max(a, b)`).
3. **Imports locais por função**: O escopo de execução em Pnuts (`pnuts.lang.Context`) isola funções. Imports no topo do arquivo não são herdados dentro de `function()`.
4. **Sem operador ternário (`? :`)**: Causa quebras de avaliação no motor; deve-se usar blocos `if / else`.
5. **Tratamento de Exceções**: O dialeto corporativo usa `catch(Exception, function(e) { ... })` e não `try { ... } catch (...)`.
6. **Saída em buffer**: Todo `println(...)` deve ser seguido imediatamente por `flush()`.
7. **Ciclo de vida do Minimal**: Verificação das fases de hook (`BF`, `AF`, `BR`, `AR`, `BC`, `BA`, `AA`, `AC`, `RI`, `RU`, `RD`).

#### Exemplo Prático de Revisão

❌ **Código Incorreto (típico erro de LLM / Java padrão):**
```pnuts
import("java.util.ArrayList")
import("java.util.Date")

function gerarRelatorio(filtro) {
    // ERRO 1: new Date() não existe em Pnuts
    dataAtual = new Date()
    
    // ERRO 2: Falta import local de ArrayList nesta função
    lista = new ArrayList()
    
    // ERRO 3: Chamada estática com ponto (.) em vez de (::)
    limite = Math.max(filtro.min, 100)
    
    // ERRO 4: Operador ternário não suportado
    status = filtro.ativo ? "A" : "I"
    
    // ERRO 5: try-catch no formato Java quebra no interpretador
    try {
        println("Iniciando processamento...")
        // ERRO 6: Sem flush() após println
    } catch (e) {
        println("Erro")
    }
}
```

✅ **Código Corrigido conforme `code-review-pnuts`:**
```pnuts
function gerarRelatorio(filtro) {
    import("java.util.ArrayList")
    import("java.util.Date")
    import("java.lang.Math")

    // Construtor sem new
    dataAtual = Date()
    lista = ArrayList()

    // Método estático com ::
    limite = Math::max(filtro.min, 100)

    // if/else explícito
    status = "I"
    if (filtro.ativo) {
        status = "A"
    }

    // Tratamento de exceção nativo Pnuts
    catch (Exception, function(e) {
        println("Erro: " + e.getMessage())
        flush()
    })

    println("Iniciando processamento...")
    flush()
}
```

---

### Skill 2: `minimal-developer`

#### Objetivo
Padroniza o desenvolvimento de scripts corporativos, rotinas de banco de dados e consultas SQL no Minimal ERP.

#### Diretrizes Essenciais
1. **`WITH (NOLOCK)` Obrigatório**: Toda consulta `SELECT` (incluindo `JOIN`) deve conter explicitamente `WITH (NOLOCK)` após o alias ou nome da tabela.
2. **`WITH (ROWLOCK)` em Updates/Deletes**: Modificações de dados devem posicionar `WITH (ROWLOCK)` imediatamente após a tabela-alvo, antes de cláusulas como `TOP`, `SET` ou `WHERE`.
3. **Identificação de Função em Queries**: Se o contexto define `Função: <nome>`, a query deve terminar com `/* Função: <nome> */`.
4. **Campo `iorg`**: Filtros de multitenancy/organização devem incluir `iorg = 'TEC'` sempre que a tabela possuir essa coluna.
5. **Acesso Otimizado ao Banco**:
   * Para valor escalar texto: usar `getSQLValue(wsel)`.
   * Para valor numérico: usar `getSQLNumber(wsel)`.
   * Para conjuntos de dados: `getMRows("TABELA", wsel)` ou `MCursor`.

#### Exemplo Prático de Consulta e Execução

```pnuts
// Função: bctx_mc_corrige_zk_fn()
function bctx_mc_corrige_zk_fn(ictxParam, ivalkParam) {
    import("br.com.tecno.kernel.MConnection")

    cursor = MConnection::getMCursor()

    // 1. SELECT otimizado com WITH (NOLOCK) e identificação de função
    wsel  = "SELECT a.* "
    wsel += "  FROM bctxt0 a WITH (NOLOCK) "
    wsel += " WHERE a.ictx = '" + ictxParam + "' "
    wsel += "   AND a.ivalk = '" + ivalkParam + "' "
    wsel += "   AND a.zk = 0 "
    wsel += " /* Função: bctx_mc_corrige_zk_fn */ "

    vbctx = getMRows("BCTX", wsel)

    if (vbctx != null && vbctx.size() > 0) {
        // 2. UPDATE seguro com WITH (ROWLOCK)
        wupd  = "UPDATE TOP (1) bctxt0 WITH (ROWLOCK) "
        wupd += "   SET zk = 1 "
        wupd += " WHERE ictx = '" + ictxParam + "' "
        wupd += "   AND ivalk = '" + ivalkParam + "' "
        wupd += "   AND zk = 0 "
        wupd += " /* Função: bctx_mc_corrige_zk_fn */ "

        MConnection::executeSQL(wupd)
    }

    return vbctx
}
```

---

### Skill 3: `minimal-telas`

#### Objetivo
Parametrização, diagnóstico e manutenção do dicionário de dados de telas nativas do Minimal (`mrfct0`). Controla formulários de critérios (`MC`), grades de resultados (`BR`) e fichas de edição (`ME`).

#### Colunas Principais de `mrfct0`
| Coluna | Descrição | Regra Prática |
| :--- | :--- | :--- |
| `i_ob` | Nome do Objeto da tela | 4 letras da tabela base (ex: `DDOC` para `ddoct0`). |
| `i_comp` | Nome do atributo/campo | Coluna física ou campo genérico (`w...`). |
| `q_ot` | Ordem no modelo | Ordem geral do metadado. |
| `q_oe` | Ordem de Edição | Posição no formulário/ficha (`ME`). `0` oculta da edição. |
| `q_oc` | Ordem de Critérios | Posição no painel de filtros (`MC`). `0` oculta dos filtros. |
| `q_ob` | Ordem na Grade | Posição na coluna do grid de busca (`BR`). `0` oculta da grade. |
| `t_lin` | Rótulo no Form | Label exibido ao lado do campo. |
| `t_col` | Rótulo na Grade | Título do cabeçalho da coluna. |
| `t_txt` | Tooltip / Ajuda | Texto explicativo ao passar o cursor. |
| `x_cl` | Quebra de linha | Definir como `'/'` para forçar o campo a iniciar uma nova linha. |

#### Exemplo Prático de Script SQL para `mrfct0`
```sql
-- Parametrização do campo 'ientt' para a tela 'DDOC'
-- Respeita a regra de WITH (NOLOCK) e atualizações seguras
SELECT a.i_ob, a.i_comp, a.t_lin, a.t_col AS coluna, a.t_txt
  FROM mrfct0 a WITH (NOLOCK)
 WHERE a.i_ob = 'DDOC'
   AND a.i_comp = 'ientt'
   AND a.c_te <> '*'
 ORDER BY a.i_ob, a.q_ot, a.q_oe;
```

---

## 4. Como Usar no Dia a Dia com Antigravity

### Ativação Automática (Progressive Disclosure)
O Antigravity monitora continuamente a intenção dos seus prompts. Quando você pergunta sobre:
* "Como adicionar uma coluna na tela do Minimal" -> Ativa automaticamente `minimal-telas`.
* "Escreva um script Pnuts para buscar dados" -> Ativa automaticamente `minimal-developer`.
* "Revise este script Pnuts" -> Ativa automaticamente `code-review-pnuts`.

### Prompts Recomendados para Invocação Direta

#### Para Revisão de Código:
```markdown
Por favor, faça um code review deste script Pnuts usando a skill code-review-pnuts:
[código ou caminho do arquivo]
```

#### Para Desenvolvimento e Regras de SQL:
```markdown
Seguindo as convenções da skill minimal-developer, gere uma função Pnuts que consulte
a tabela ddoct0 com bloqueio NOLOCK e aplique o comentário com o nome da função.
```

#### Para Construção ou Correção de Telas:
```markdown
Usando a skill minimal-telas, gere o SQL de parametrização da tabela mrfct0 para adicionar
os campos código, descrição e data de emissão na tela NFCE, com quebra de linha após a descrição.
```

#### Fluxo Combinado (Esteira Completa de Produção):
```
1. minimal-telas      -> Define metadados e hooks necessários.
2. minimal-developer  -> Implementa a lógica das queries e das funções Pnuts.
3. code-review-pnuts  -> Valida o código final antes do commit ou implantação.
```

---

## 5. Análise: Como essas Skills Melhoram a sua Skill Pnuts

Sua instalação já contava com a skill [`pnuts`](../skills/pnuts/), que é excelente para fundamentos de linguagem e suporte a tooling (LSP, Tree-Sitter, VS Code, Zed). No entanto, a incorporação do pacote `minimal-skills` eleva significativamente a qualidade do ecossistema:

### 1. Correção de Contradição Crítica na Skill Base
* **Problema Encontrado**: No arquivo [`skills/pnuts/references/corporate-apis.md`](../skills/pnuts/references/corporate-apis.md#L72), havia um exemplo contendo a sintaxe Java:
  ```pnuts
  try {
      cursor.open()
      ...
  } finally {
      cursor.close()
  }
  ```
  Isso entrava em contradição direta com a **Golden Rule #7** de [`skills/pnuts/SKILL.md`](../skills/pnuts/SKILL.md#L36), que proíbe `try / catch / finally` no estilo Java por causar falha de parser no interpretador (`Encountered "{" at column 7-1`).
* **Como as Novas Skills Ajudam**: `minimal-developer` e `code-review-pnuts` trazem os exemplos canônicos corretos e testados em produção, utilizando a função `catch (Exception, function(e) { ... })` e fechamento explícito de recursos.

### 2. Acesso a APIs Corporativas de Produção
A skill base `pnuts` documentava principalmente `MConnection`, `MCursor` e `MRow`. O conjunto `minimal-skills` expande para:
* `getMRows("TABELA", wsel)`
* `getSQLValue(wsel)` e `getSQLNumber(wsel)`
* `MResultSet` e `MRecord`
* Funções de extensão corporativa: `x_mc_select_extension_fn` e `x_mc_update_extension_fn`
* Contexto de hooks de tela (`mjext0`, `mexst0`, `mcfxt0`, `sment0`)

### 3. Convenções de Banco de Dados de Missão Crítica
A skill base tratava SQL de forma genérica. As novas skills trazem as regras de concorrência do SQL Server do Minimal:
* Isolamento de leitura: `WITH (NOLOCK)` mandatório para evitar deadlocks em ERP concorrente.
* Concorrência de escrita: `WITH (ROWLOCK)` mandatório em `UPDATE` e `DELETE`.
* Rastreabilidade: Inserção do comentário `/* Função: <nome> */` nos buffers SQL para profiler do banco.
* Multitenancy: Preenchimento automático de `iorg = 'TEC'`.

### 4. Engenharia Reversa Arquitetural (`_reversa_sdd`)
A pasta [`minimal-developer/references/_reversa_sdd/`](../minimal-skills/minimal-developer/references/_reversa_sdd) contém uma base de conhecimento detalhada:
* **`architecture.md`**: Visão global da arquitetura do Minimal.
* **`domain.md`**: Dicionário de dados, modelos de entidades e tabelas do sistema.
* **`state-machines.md`**: Máquinas de estado e regras de transição de status.
* **`permissions.md`**: Matriz de segurança e permissões de acesso.
* **`code-analysis.md`**: Análise profunda da integração entre Pnuts e o backend Java.

---

## 6. Próximos Passos Recomendados

1. **Correção no arquivo de APIs Corporativas**:
   * Substituir o trecho com `try { ... } finally { ... }` em [`skills/pnuts/references/corporate-apis.md`](../skills/pnuts/references/corporate-apis.md) pelo padrão homologado em `minimal-developer`.
2. **Uso nos Editores**:
   * O seu LSP (`pnuts-lsp`) e as regras do `tree-sitter-pnuts` podem se beneficiar diretamente dos padrões de `code-review-pnuts` para aprimorar diagnósticos estáticos e linter.
3. **Sincronização Git**:
   * Como os symlinks foram configurados apontando para este repositório, qualquer melhoria ou nova nota que você adicionar em `minimal-skills/` estará instantaneamente disponível em qualquer projeto aberto no Antigravity.
