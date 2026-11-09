---
name: minimal-telas
description: Desenvolver, cadastrar, alterar e diagnosticar telas nativas do ERP Minimal por metadados mrfct0. Usar para criar formulários, critérios MC, grades BR, fichas ME, campos, máscaras, obrigatoriedade, proteção, lookups F4, descrições, abas, painéis e saltos; gerar ou revisar SQL de parametrização; investigar campos ausentes, duplicados ou alterações que não aparecem. Integra menus, expressões e hooks Pnuts quando necessários.
---

# Desenvolvimento de telas no Minimal

Construir telas usando os metadados e componentes existentes do Minimal. Entregar a menor alteração que atenda ao pedido, preservando a parametrização atual e os valores fornecidos pelo usuário.

## 1. Consultar as fontes

1. Consultar primeiro a nota `Minimal/09 - Tela Form Metadata (mrfct0).md` no Obsidian. Descobrir o servidor e suas ferramentas antes de chamá-las; não assumir nomes de ferramentas ou conexão SQL disponível.
2. Ler [Referência de parametrização](references/parametrizacao.md) para interpretar campos, limites e armadilhas. Consultar `Projeto Roberto/GUIA-Cadastro-de-Telas-Minimal.md`, relativo à raiz do workspace, quando precisar do histórico RACN ou do exemplo original.
3. Para scripts ou SQL, ativar também `minimal-developer`. Para Pnuts produzido, realizar a revisão `code-review-pnuts`.
4. Resolver divergências pela evidência: esquema e parametrização do ambiente alvo, comportamento confirmado no kernel e notas atuais. Tratar exemplos históricos e itens inferidos como referências, sem promover hipóteses a regras.
5. Se o Obsidian estiver indisponível, informar a limitação e avançar no que as fontes locais sustentam; identificar o que depende de confirmação.
6. Registrar novas descobertas ou correções na nota relevante de `Minimal/`. Não substituir o Obsidian por memórias operacionais.

## 2. Inspecionar a tela e delimitar a entrega

Extrair do pedido e dos arquivos disponíveis:

- Objetivo, tabela base, raiz do objeto, visão aberta pelo menu e ambiente alvo.
- Campos visíveis em edição, critérios e resultados; títulos, ordem, quebras de linha, máscaras, obrigatoriedade e proteção.
- Lookups, descrições, filtros, comandos e regras de negócio necessários.
- Escopo: criar tela, alterar campos ou diagnosticar comportamento.

Inspecionar o DDL real, os registros atuais de `mrfct0`, as customizações `mfclt0`/`mextt0` e uma tela semelhante quando disponíveis. Verificar se a instalação usa `METAFILE`/`METAFILEALL` e como carrega os metadados do cliente.

Usar consultas somente de leitura nessa inspeção. Sem acesso ao banco, usar dumps e arquivos fornecidos; pedir apenas os dados indispensáveis ainda ausentes. Não inventar tabela, coluna, objeto, usuário, grupo, contexto de lookup, argumento ou chave.

Distinguir a raiz usada por `RW` da visão usada por `MC`/`BR`/`ME`. Preservar visões e subobjetos existentes. Aplicar a decisão de usar somente `RACN` apenas ao caso em que esse padrão esteja confirmado.

## 3. Montar o mapa de campos

Antes de gerar a carga, organizar os campos no próprio raciocínio ou na resposta, sem criar documento extra desnecessário:

| Campo | Origem física/expansão/extensão/formulário | Tipo e tamanho | Edição | Critérios | Grade | Layout | Lookup/regra |
|---|---|---|---|---|---|---|---|
| Nome real | Tabela.coluna ou origem confirmada | Tipo, máscara e limites | Ordem/proteção | Ordem/comparação | Ordem/título | Linha/painel | Configuração existente |

Aplicar as regras:

- Parametrizar todas as colunas físicas da tabela no modelo da raiz, inclusive as técnicas; não criar colunas fictícias para completar um padrão.
- Separar `q_ot` (modelo), `q_oe` (edição), `q_oc` (critérios) e `q_ob` (grade). Usar zero para excluir a respectiva projeção visual.
- Reutilizar a linha do campo para as projeções do mesmo objeto. Respeitar a unicidade `(i_ng, i_ut, i_ob, i_tc, i_comp)`.
- Ocultar e proteger os campos técnicos conforme a referência. Manter `cst`/`dst` no fim quando esse for o padrão da tela.
- Classificar os campos pelo esquema e por `x_sql`; não decidir que uma coluna existe apenas pelo nome ou pelo prefixo `w`.
- Configurar a descrição de lookup como expansão quando aplicável; verificar a existência dos campos de destino e das expressões de ajuda/expansão.
- Usar `x_cl='/'` para iniciar linha. Configurar abas e painéis a partir de exemplo real compatível, sem inventar vínculos entre subobjetos.

## 4. Implementar somente o necessário

### Criar ou alterar metadados

1. Entregar SQL `.sql` com lista explícita de colunas, instruções completas e escopo identificável.
2. Em tela existente, preferir alterações pontuais por chave lógica. Não apagar a tela inteira para alterar um campo.
3. Em carga repetível, tratar registros existentes explicitamente: definir se serão preservados ou atualizados; não gerar duplicatas nem chamar um INSERT simples de idempotente.
4. Conferir comprimentos, precisão, escala, nulabilidade e defaults no esquema alvo. Não truncar silenciosamente textos de negócio.
5. Inspecionar a geração de `zk`: não é identity no esquema histórico. Não tratar `MAX(zk)+1` com `NOLOCK` como alocador seguro em concorrência; reutilizar o mecanismo confirmado do ambiente ou explicitar a necessidade de carga exclusiva.
6. Usar `WITH (NOLOCK)` nas tabelas consultadas e `WITH (ROWLOCK)` no destino de `UPDATE`/`DELETE`, conforme o padrão SQL do projeto. Restringir alterações ao objeto e aos demais componentes da chave relevantes.
7. Aplicar `iorg='TEC'` nas consultas de negócio quando a tabela tiver essa coluna; não inventar `iorg` em `mrfct0`.
8. Escapar literais e validar identificadores originados de entrada externa. Se houver `Função: <nome>` no contexto, acrescentar o comentário ao final de cada SQL montado.
9. Preparar a entrega antes de solicitar qualquer autorização ainda necessária para executar uma carga. Distinguir geração de arquivo, execução no banco e validação visual.

### Integrar comportamento

Reutilizar capacidades nativas de máscara, obrigatoriedade, proteção, F4 e expansão antes de escrever Pnuts. Consultar a seção correspondente no Obsidian para comandos (`mcfxt0`), expressões (`mexst0`), menus (`sment0`) e scripts (`mjext0`).

Se Pnuts for necessário:

- Produzir `.txt` compatível com Java 8 e com as regras de `minimal-developer`.
- Manter o corpo principal direto no arquivo de `mjext0`, sem função wrapper autoexecutada; declarar auxiliares antes do uso.
- Preservar nomes e alterações do usuário. Usar imports dentro das funções, construtores sem `new`, estáticos com `::`, e `flush()` após `println()`.
- Respeitar a transação iniciada pelo Eval: se houver `commitWork()` e continuidade, chamar `beginWork()` imediatamente depois.
- Ler/escrever extensões pelas funções `x_mc_select_extension_fn` e `x_mc_update_extension_fn`, sem SQL direto nessas tabelas.
- Não compilar Pnuts nem criar verificadores auxiliares sem solicitação explícita. Fazer revisão por leitura e informar os limites dessa verificação.

## 5. Conferir e entregar

Conferir por leitura:

- Cobertura de todas as colunas físicas, sem confundir expansão com persistência.
- Chaves lógicas sem duplicatas; atualizações com escopo correto.
- Ordens ME/MC/BR independentes e técnicos ocultos/protegidos.
- Tamanhos compatíveis, especialmente `t_txt` de até 59 no esquema documentado.
- Quebras de linha, máscaras, precisão, obrigatoriedade e lookups coerentes.
- Nenhum valor histórico de RACN copiado como default de outra tela.
- Nenhuma exclusão ampla, dependência ausente ou placeholder apresentado como SQL pronto.

Quando houver acesso e autorização para homologação, verificar no Minimal: abertura pelo menu/objeto, critérios simples e intervalo, colunas e ordem da grade, ficha, F4, expansão da descrição, obrigatoriedade e proteção. Testar gravação apenas quando fizer parte do escopo autorizado.

Se a mudança não aparecer, investigar fonte de metadados, overrides e índice de customizações carregado no login antes de repetir INSERTs. Orientar nova sessão/reinício conforme o ambiente; não prometer que recarregar a tela elimina todo cache.

Entregar os arquivos solicitados, explicar o efeito da alteração e informar o que foi conferido ou executado. Listar pendências reais e os passos de homologação não realizados, sem afirmar que a tela funcionou apenas porque o SQL foi revisado.

## Exemplos de aplicação

- **“Criar tela para a tabela informada”**: consultar fontes → obter DDL e modelo semelhante → mapear raiz/visão e todos os campos → gerar carga → revisar → orientar homologação.
- **“Adicionar campo à grade”**: localizar a linha/visão efetiva → ajustar `q_ob` e `t_col` → preservar `q_oe`/`q_oc` → verificar a origem dos metadados.
- **“Separar campos em linhas e proteger um deles”**: ajustar `x_cl` e `c_ta`, preservando o controle de F4 em `x_ajp`.
- **“A descrição não aparece no F4”**: verificar `iexs`, `ictx`, `niob`, `texsargs`, expressões `P1`/`E1` e metadados dos subpainéis.
- **“Cadastrei no banco e não apareceu”**: investigar `METAFILE`, `DEFAULT`, `mfclt0` e `loadClientMeta`, sem cadastrar duplicatas.
