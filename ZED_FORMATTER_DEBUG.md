# Diagnóstico e Plano de Correção do Formatter Pnuts no Zed

Este documento detalha o estado atual, a análise técnica das causas raízes pelas quais o formatter funcionou no VS Code mas ainda não no Zed, e o plano de ação exato para resolver os problemas no Zed amanhã.

---

## 1. Contexto & Status Atual

### O que foi validado com sucesso no VS Code (`vscode-pnuts`)
- A extensão do VS Code foi atualizada para a versão **`0.2.4`** (`vscode-pnuts/vscode-pnuts-0.2.4.vsix`).
- O pipeline no Node.js/VS Code funciona 100%:
  1. Comentários ao final de sentenças sobem para a linha anterior com indentação correspondente.
  2. Blocos com mais de 1 linha recebem uma linha em branco no topo (após `{`).
  3. Linhas em branco imediatamente anteriores ao fecha-chaves `}` são removidas.

### O que falhou no Zed
No Zed, ao salvar ou formatar um arquivo `.pnuts`:
1. **Comentários**: O comentário foi para a **linha de baixo** após a sentença (ex.: `a = 100\n// comentario`), em vez de subir para a linha de cima.
2. **Blocos multilinhas**: Não foi adicionada a linha em branco após `{`.
3. **Novo requisito**: Garantir que nunca haja linhas em branco antes de `}`.

---

## 2. Especificação Exata dos 3 Comportamentos Esperados

### Regra 1: Comentário ao final de sentença sobe para a linha de cima
Qualquer comentário `//` ou `/* ... */` colocado ao final de uma linha após código executável/sentença deve ser movido para a linha **imediatamente superior**, herdando a indentação da sentença.

```pnuts
// Entrada:
a = 100 // aqui é um comentarios

// Saída esperada:
// aqui é um comentarios
a = 100
```

Dentro de blocos:
```pnuts
// Entrada:
if (a > b) {
  showMessage("exemplo"); // comentario da chamada
}

// Saída esperada:
if (a > b) {
  // comentario da chamada
  showMessage("exemplo");
}
```

*Nota*: Comentários após fechamento de bloco ou encadeamentos (`}`, `} else {`, `catch`, `finally`) permanecem na mesma linha para não corromper a estrutura do bloco.

---

### Regra 2: Linha em branco no topo de blocos multilinhas
Se um bloco `{ ... }` contiver **mais de uma linha de código/comentário aninhada**, deve haver uma linha em branco logo após a linha do abre-chaves `{`.
- Se o bloco tiver **apenas 1 linha** de conteúdo, **não** adiciona linha em branco (mantém compacto).
- Se o bloco for vazio (`{}`), permanece sem linha em branco.
- Se já existir uma linha em branco, mantém apenas uma (idempotência).

```pnuts
// Entrada:
if (a > b) {
  showMessage("linha 1");
  showMessage("linha 2");
}

// Saída esperada:
if (a > b) {

  showMessage("linha 1");
  showMessage("linha 2");
}
```

---

### Regra 3: Nunca ter linhas em branco antes do fecha-chaves `}`
Entre a última instrução de um bloco e a chave de fechamento `}`, **nunca deve haver linha em branco**. Qualquer linha em branco antes do `}` deve ser eliminada.

```pnuts
// Entrada (com linhas em branco no final do bloco):
if (a > b) {

  showMessage("linha 1");
  showMessage("linha 2");

}

// Saída esperada:
if (a > b) {

  showMessage("linha 1");
  showMessage("linha 2");
}
```

---

## 3. Diagnóstico Técnico: Por Que Falhou no Zed?

### Causa Raiz 1: O executável Windows `pnuts-format.exe` não foi recompilado
- No Zed sob Windows, a configuração padrão documentada em `zed-pnuts/README.md` aponta para:
  `"command": "C:\\path\\to\\pnuts-tooling\\zed-pnuts\\bin\\pnuts-format.exe"`
- O arquivo `pnuts-format.exe` no repositório ainda é a versão compilada antes dessas correções.
- Quando o executável antigo roda, ele chama o `topiary.exe` diretamente sem o pré-processador de comentários e sem o pós-processador de blocos.
- **Comportamento do Topiary padrão**:
  O Topiary aplica a query `(_) @append_hardline` na sentença `a = 100`, inserindo uma quebra de linha **entre** `a = 100` e `// aqui é um comentarios`. Como o Topiary não reordena nós na árvore sintática, o comentário é jogado para a **linha de baixo**!
  Além disso, o Topiary padrão não insere linha em branco após `{` nem remove linhas em branco antes de `}`.

### Causa Raiz 2: Execução via Python no `.zed/settings.json` com caminhos relativos
No arquivo `.zed/settings.json`:
```json
{
  "languages": {
    "Pnuts": {
      "formatter": {
        "external": {
          "command": "python3",
          "arguments": ["scripts/format_pnuts.py", "--stdin"]
        }
      }
    }
  }
}
```
- Se o Zed abrir um subdiretório, arquivo isolado, ou se o diretório de trabalho do processo (`cwd`) do Zed não for exatamente a raiz `/home/.../pnuts-tooling`, o comando `python3 scripts/format_pnuts.py` falha silenciosamente (`can't open file 'scripts/format_pnuts.py'`).
- No Windows, o comando padrão do Python costuma ser `python` e não `python3`.

### Causa Raiz 3: Comunicação de Stdin vs Arquivo no Zed
O Zed envia o conteúdo do buffer via **stdin** e espera o código formatado via **stdout**.
Se o formatter receber argumentos inesperados ou retornar código de saída diferente de 0, o Zed descarta o stdout e mantém o texto original ou exibe erro no log (`zed: open log`).

---

## 4. Estrutura de Arquivos Envolvidos no Zed

| Arquivo | Função |
|---|---|
| `.zed/settings.json` | Configurações de workspace do Zed (formatter externo, format on save) |
| `zed-pnuts/bin/format_pnuts.py` | Script Python com pré-processador de comentários e pós-processador de blocos |
| `zed-pnuts/bin/format_pnuts` | Wrapper shell para Linux/macOS que autolocaliza o script Python |
| `zed-pnuts/bin/format_pnuts.cmd` | Wrapper batch para Windows que detecta `python` / `python3` ou fallback para `.exe` |
| `zed-pnuts/bin/pnuts-format` | Binário Linux compilado a partir de `scripts/pnuts_format_runner.c` |
| `zed-pnuts/bin/pnuts-format.exe` | Binário Windows 64-bit para execução standalone no Zed Windows |
| `scripts/pnuts_format_runner.c` | Código-fonte C do runner standalone |
| `zed-pnuts/extension.toml` | Manifesto da extensão Zed |
| `zed-pnuts/languages/pnuts/config.toml` | Configurações de linguagem do Zed (comentários, brackets) |

---

## 5. Plano de Ação para Execução Amanhã

### Passo 1: Recompilar o binário Windows `pnuts-format.exe`
Utilizar o compilador cross-platform do **Zig** (`zig cc -target x86_64-windows`) já instalado na máquina para compilar `scripts/pnuts_format_runner.c` diretamente para Windows:
```bash
zig cc -target x86_64-windows -O2 scripts/pnuts_format_runner.c -o zed-pnuts/bin/pnuts-format.exe
```
Isso garante que mesmo se o Zed no Windows executar o `pnuts-format.exe` nativamente sem Python, o binário terá as regras completas de pré-processamento de comentários e normalização de chaves.

### Passo 2: Tornar os wrappers do Zed robustos e independentes do CWD
Garantir que `zed-pnuts/bin/format_pnuts` e `format_pnuts.cmd` resolvam seu próprio diretório (`DIR=$(dirname ...)` ou `%~dp0`) e apontem para caminhos absolutos/resolvidos, garantindo que funcionem independentemente de onde o Zed foi aberto:
- No Linux/macOS: usar `zed-pnuts/bin/format_pnuts`
- No Windows: usar `zed-pnuts/bin/format_pnuts.cmd`

### Passo 3: Configurar o `.zed/settings.json` para usar o wrapper autocontido
Ajustar o `.zed/settings.json` do repositório para usar o wrapper do diretório `bin/`, que detecta automaticamente o ambiente:
```json
{
  "languages": {
    "Pnuts": {
      "formatter": {
        "external": {
          "command": "python3",
          "arguments": ["zed-pnuts/bin/format_pnuts.py", "--stdin"]
        }
      },
      "format_on_save": "on",
      "tab_size": 2
    }
  }
}
```

### Passo 4: Diagnóstico ao vivo no Zed
Caso ainda ocorra divergência:
1. Abrir a paleta de comandos do Zed (`Ctrl+Shift+P` / `Cmd+Shift+P`).
2. Executar `zed: open log` para inspecionar a linha de comando exata que o Zed disparou e o código de saída / erro retornado pelo formatter externo.
3. Se estiver usando a extensão instalada no Zed via Dev Extension, executar `zed: extensions` -> **Pnuts** -> **Rebuild**.

---

## 6. Checklist de Validação Final
- [ ] Teste 1: `a = 100 // comentario` -> comentário vai para cima da sentença.
- [ ] Teste 2: `if (a > b) { msg1; msg2; }` -> linha em branco adicionada após `{`.
- [ ] Teste 3: `if (a > b) { msg1; msg2; \n\n }` -> linhas em branco antes de `}` removidas.
- [ ] Teste 4: `if (a > b) { msg1; }` -> bloco de linha única permanece compacto (sem linha extra).
- [ ] Teste 5: Idempotência -> salvar o arquivo múltiplas vezes não altera nada.
