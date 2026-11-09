# Referência de parametrização de telas

## Fontes e precedência

Síntese do `Projeto Roberto/GUIA-Cadastro-de-Telas-Minimal.md` e da nota `Minimal/09 - Tela Form Metadata (mrfct0).md`, consultados em 2026-10-19. Usar o Obsidian como fonte central atualizada; conferir os tipos no ambiente alvo. As tabelas abaixo descrevem o esquema histórico de 52 colunas, não garantem igualdade entre versões.

No guia RACN há inferências que as seções posteriores da nota esclarecem:

| Ponto | Interpretação a usar |
|---|---|
| `q_oc` chamado de ordem da grade em trecho antigo | `q_oc` = critérios MC; `q_ob` = grade BR. |
| `x_ajp='p'` interpretado como read-only | `x_ajp` controla o prompt F4; `c_ta='P'` protege o campo. |
| `x_sql='A'` interpretado como critério alfabético | `A` classifica coluna física comum; `XN` é expansão; `*` é somente formulário. |
| Toda tela tratada como objeto raiz único | RACN adotou raiz única. O kernel também usa raiz de quatro caracteres para RW e visões como DDOC0 para MC/BR/ME; preservar o padrão efetivo. |
| Prefixo `w` como prova de campo virtual | É indício; conferir esquema e classificação. |
| `MAX(zk)+1` como receita geral | É exemplo histórico, vulnerável a concorrência e a tabela vazia. Não copiar sem resolver a alocação de chave no ambiente. |

## Projeções do objeto

| Modo | Conteúdo | Seleção/ordem | Arquivo típico em `METADATALOC/mrfc/` |
|---|---|---|---|
| RW | Modelo MTable/MRow da raiz | `q_ot` | `<raiz>row` |
| MC | Critérios | `q_oc > 0`, ordem `q_oc,q_ot` | `<visao>crit` |
| BR | Grade | `q_ob > 0`, ordem `q_ob,q_ot` | `<visao>browse` |
| ME | Ficha de edição/consulta | `q_oe > 0`, ordem `q_oe,q_ot` | `<visao>ent` |
| JP | Botões de salto | `i_tc='BJ'`, `q_oe > 0` | `<visao>jump` |

Filtros de tipo documentados: MC aceita `CB,TF,BT,TP,PN,MI,SY,LB,TA`; BR aceita `TF,CB,SY,TA`; ME aceita `CB,IM,TF,BT,TP,PN,MI,SY,LB,TA`. Não deduzir a configuração completa de um componente apenas por sua sigla.

O índice único `mrfci0` cobre `(i_ng,i_ut,i_ob,i_tc,i_comp)`. Reutilizar as ordens da mesma linha para edição, critérios e grade. Diferentes subobjetos podem ter o mesmo `i_comp`; isso não equivale a duplicar a chave lógica no mesmo objeto.

## Colunas de `mrfct0`

### Controle e identificação

| Coluna | Tipo observado | Uso |
|---|---|---|
| `zutl` | char(8) | Usuário da alteração |
| `zgrp` | char(4) | Grupo |
| `zhst` | char(8) | Hora `hh:mm:ss` |
| `zdst` | char(10) | Data `yyyy-MM-dd` |
| `zcst` | char(2) | Situação do registro |
| `zk` | decimal(17,0) | Chave da linha; não identity no esquema observado |
| `i_ng` | char(4) | Núcleo/grupo; `0000` observado |
| `i_ut` | char(8) | Utilizador; `minimal` observado |
| `i_ob` | char(18) | Raiz, visão ou subobjeto conforme carregamento |
| `i_tc` | char(2) | Tipo de componente; `TF` nos campos do exemplo RACN |
| `i_comp` | char(18) | Identificador do campo/componente |
| `t_tab` | char(15) | Tabela editada |
| `t_tabcol` | char(15) | Coluna editada |

Não adotar `jlucas`, `910`, `us`, `0000` ou `minimal` automaticamente para outra carga; confirmar os valores pertinentes.

### Apresentação e comportamento

| Coluna | Tipo observado | Uso |
|---|---|---|
| `q_ot` | decimal(5) | Ordem no modelo |
| `t_comp` | char(50) | Título |
| `t_lin` | char(50) | Rótulo no formulário |
| `t_col` | char(50) | Cabeçalho da grade; `\n` observado para quebra |
| `c_te` | char(2) | Tipo de dado; `C`, `N`, `D` nos exemplos; outros exigem modelo compatível |
| `t_mask` | char(40) | Máscara: `U` maiúsculas, `L` minúsculas, `N` numérica, `D` data |
| `c_ta` | char(2) | `N` editável, `P` protegido; semântica específica em painéis |
| `c_label` | char(2) | `S`/`N` observados; conferir efeito no componente alvo |
| `t_cmd` | char(15) | Comando associado |
| `t_gif` | char(15) | Imagem/ícone; configuração a validar |
| `q_car` | decimal(5) | Largura de exibição |
| `q_cari` | decimal(5) | Largura interna; pode truncar texto no componente |
| `q_dec` | decimal(5) | Decimais de exibição |
| `q_deci` | decimal(5) | Decimais internos |
| `x_sql` | char(2) | Classificação SQL do campo |
| `x_md` | char(1) | `M` obrigatório, `A` atenção; preservar caixa usada no ambiente |
| `x_up` | char(1) | Não aplicado em `getMTextField` segundo a nota; usar máscara `U` para maiúsculas |
| `x_ajd` | char(1) | Semântica a validar |
| `x_ajp` | char(1) | `promptControl`, controle de F4; não define read-only |
| `x_rg` | char(8) | Regra de validação |
| `x_cl` | char(1) | `/` abre linha; em PN, `t/c/b` = North/Center/South |
| `q_oe` | decimal(5) | Ordem na ficha ME |
| `q_oc` | decimal(5) | Ordem nos critérios MC |
| `x_tc` | char(1) | Comparação; `i` cria intervalo e campo `<i_comp>_z`, `=` igualdade |
| `q_ob` | decimal(5) | Ordem na grade BR |
| `x_tb` | char(1) | Tipo de browse; valores observados, significado a validar |
| `t_dlb` | char(18) | Semântica a validar |
| `q_dly` | decimal(5) | Linhas de altura de MTextArea |
| `q_dlx` | decimal(5) | Não tratar como coordenada X confirmada; inferência do guia |
| `c_st` | char(1) | `*` ativo nos registros observados |
| `t_txt` | char(59) | Tooltip, limite histórico de 59 |
| `c_total` | char(1) | `T` totaliza coluna no rodapé |
| `t_prn1` | char(25) | Título de impressão 1 |
| `t_prn2` | char(25) | Título de impressão 2 |

Máscaras observadas: `Dyyyy-MM-dd`, `N###0.00###`, `U`, `L`. Adequar precisão e tamanho à coluna real, sem copiar a máscara numérica indiscriminadamente.

### Lookup e comandos

| Coluna | Tipo observado | Uso |
|---|---|---|
| `ictx` | char(18) | `promptTable`, contexto/objeto da ajuda |
| `niob` | char(50) | `expansionField`, campo(s) de destino |
| `iexs` | char(18) | `promptCode`, grupo de expressões P1/E1 |
| `texsargs` | char(50) | Argumentos; no MPrompt, primeiro é título e seguintes são cabeçalhos, com `HIDE` para ocultar |
| `tcmdargs` | char(50) | Argumentos do comando |

Exemplos históricos de configuração: `IUORG → iuorgx` com `iexs=BENT*1`; `IENT → nent` com `iexs=BENT*1`; `BCTX*SN` para domínio sim/não. Confirmar a configuração real antes de reutilizar.

O prompt F4 resolve `P1` do campo ou do par `iexs/ictx`. A expansão ao sair do campo resolve `E1` e preenche destinos de `niob`. Na abertura da ficha ocorre expansão `NoForce`; na validação, `Force`. Código inválido pode gerar `MIN-001020E`; obrigatório ausente, `MIN-001010E`.

Ao inspecionar árvores de abas/painéis, preservar a origem de cada ocorrência. Se construir um mapa achatado para lookup, não deixar atributos vazios de uma ocorrência apagarem atributos preenchidos de outra.

## Campos físicos, virtuais e técnicos

| `x_sql` | Significado documentado |
|---|---|
| `K` | Chave física |
| `A` | Coluna física comum |
| `EX` | Extensão |
| `XN` | Expansão/descrição; não é coluna da tabela principal |
| `*` | Somente formulário |

`K1` aparece no `zk` de telas de referência. Preservar o padrão confirmado do objeto; não assumir equivalência completa com `K` sem verificar o kernel.

Bloco técnico observado em SINC:

| `q_ot` | Campo | Título | Tipo | `x_sql` observado |
|---:|---|---|---|---|
| 10 | zk | Clave Interna | N | K1 |
| 20 | zutl | Utilizador | C | A |
| 30 | zgrp | Modo | C | A |
| 40 | zhst | Hora Status | C | A |
| 50 | zdst | Data Status | C | A |
| 60 | zcst | Status | C | A |
| 70 | zprc | Controlo Processador | N | A |
| 80 | zctl | Controlo Geral | C | A |
| 90 | iorg | Organizacao | C | A |

Usar os campos efetivamente existentes. Negócio em 100+ e `cst`/`dst` no fim conforme o modelo. Para técnicos ocultos: `t_lin=''`, `t_col=''`, `q_oe=0`, `q_oc=0`, `q_ob=0`, `c_ta='P'`. O padrão histórico também usa `x_ajp='p'`; não atribuir a ele a proteção.

Todas as colunas físicas precisam de parametrização; isso não significa mostrar todas em todas as visões.

## Layout e eventos

O `MLayoutManager` distribui campos horizontalmente. Usar `x_cl='/'` nos campos que iniciam nova linha. Rótulos devem acompanhar a disposição; copiar o padrão de `c_label` de uma tela compatível.

Tipos estruturais: `TP` para abas, `PN` para painéis, `BJ` para saltos. Em PN, `c_ta='BR'` indica browse embutido, `BE` grade editável, `ME` subformulário. Inspecionar a ligação dos objetos antes de gerar registros estruturais.

Fluxo resumido: entrada prepara BR e abre MC → critérios montam a consulta → BR exibe resultados → ME consulta/insere/altera a ficha. Hooks documentados: `BF` antes do formulário, `AF` na confirmação, `TC` no tratamento de critérios, `BQ` antes da query, `AQ` depois, `BR`/`AR` antes/depois da persistência da linha. Expressões `BR`, `E5`, `SS` compõem consulta/filtros/segurança; `S1` participa dos saltos.

Não confundir modo BR, expressão SQL BR e hook Pnuts BR. Verificar objeto, componente, fase e convenção de cadastro de cada um no Obsidian.

## Carregamento e diagnóstico

`JMETADATA` contém metadados base; `DEFAULT`, customizações do cliente. Em `METAFILE`/`METAFILEALL`, a base pode vir do disco. `MLogin.loadClientMeta` indexa no login os objetos customizados do banco.

As camadas têm comportamentos diferentes:

- MRFC: campos do banco podem ser concatenados aos do arquivo; `mfclt0` participa do override na leitura do banco. Não supor sobrescrita automática de qualquer campo base.
- MEXS e MCFX: customizações podem substituir entradas pela respectiva chave.
- MJEX: implementações `MIN_` e `CLI_` coexistem; o despachante público prioriza a customização disponível.
- SMEN: em `METAFILEALL`, o menu principal pode vir exclusivamente do arquivo existente.
- Visibilidade/rótulos/tamanhos também podem vir de arquivos HLL por usuário, grupo ou escopo global.

| Sintoma | Investigar |
|---|---|
| Campo ausente | Objeto/visão efetiva, tipo aceito, ordem da projeção, overrides e fonte dos metadados |
| Campo só aparece nos critérios | `q_oc > 0` não define grade; conferir `q_ob` |
| Tudo na mesma linha | Ausência de `x_cl='/'` |
| Campo protegido incorretamente | `c_ta`, sem confundir com `x_ajp` |
| F4/descrição falha | P1/E1, iexs, ictx, niob, argumentos e subpainéis |
| Msg 2601 / mrfci0 | Chave lógica duplicada |
| Msg 8152 | Comprimento excedido, especialmente `t_txt` |
| Msg 2812 citando RACN | Execução parcial no meio do INSERT histórico; executar instruções completas |
| Alteração não aparece | Fonte Metafile/banco, índice de customizações no login, cache e sessão |

Há triggers `mrfcri`, `mrfcru`, `mrfcrd` no esquema histórico; seus corpos não foram obtidos no guia. Não inferir efeitos nem desabilitá-las.

Em Pnuts, `getMRows` retorna vetor e deve ser iterado; `getMRow("OBJ")` fornece template de linha, não implica consulta por chave. Usar objetos/APIs adequados quando a consulta trouxer colunas calculadas fora do modelo.
