package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MRtfGenerator;
import PT.Minimal.geral.MUtil;

import java.util.Vector;
import java.io.File;

/**
 * MRtfAction - Classe para criar arquivos RTF sem abertura ou impressão automática
 * Implementa MExternalCommand para integração com o sistema de ações
 */
public class MRtfAction implements MExternalCommand {
    private MCommand minCommand;
    private MForm minForm;
    private Vector wrows;
    private String generatedFilePath;
    
    public MRtfAction() {
    }
    
    /**
     * Define o comando associado
     * @param wcmd Comando
     */
    public void setMCommand(MCommand wcmd) {
        minCommand = wcmd;
    }
    
    /**
     * Define o formulário associado
     * @param wform Formulário
     */
    public void setMForm(MForm wform) {
        minForm = wform;
        
        // Se for um formulário de registro único, cria um vetor com esse registro
        if (minForm.getType() == "SINGLEROW") {
            Vector wv = new Vector();
            MRow wr = minForm.getRow().Copy();
            MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
            wv.addElement(wr);
            setRows(wv);
        }
    }
    
    /**
     * Define as linhas a serem processadas
     * @param wv Vector de MRows
     */
    public void setRows(Vector wv) {
        wrows = wv;
    }
    
    /**
     * Executa a ação para gerar o arquivo RTF
     * @throws MError Caso ocorra algum erro
     */
    public void execute() throws MError {
        // Verifica se existem registros
        if (wrows == null || wrows.size() == 0) {
            throw new MError("MIN-000103W");
        }
        
        // Obtém os argumentos
        String[] wargs = null;
        if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0)) {
            wargs = MUtil.getArgs(minCommand.getCallerArgs(), ' ');
        } else if ((minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0)) {
            wargs = MUtil.getArgs(minCommand.getArgs(), ' ');
        }
        
        if (wargs == null || wargs.length == 0) {
            throw new MError("Argumentos insuficientes para gerar RTF");
        }
        
        // Cria o gerador de RTF
        MRtfGenerator generator = new MRtfGenerator(minForm);
        generator.setRows(wrows);
        
        // Prepara os argumentos
        String templatePath = wargs[0];
        String additionalArgs = (wargs.length > 1) ? wargs[1] : null;
        String outputFile = (wargs.length > 2) ? wargs[2] : null;
        
        // Gera o arquivo RTF
        generatedFilePath = generator.generateRtf(templatePath, additionalArgs, outputFile);
        
        // Define o resultado no formulário para que possa ser acessado posteriormente
        minForm.set("GENERATED_RTF_PATH", generatedFilePath);
        
        // Caso o argumento NOOK não esteja presente, exibe mensagem de confirmação
        if (additionalArgs == null || !additionalArgs.toUpperCase().contains("NOOK")) {
            File file = new File(generatedFilePath);
            throw new MError("Arquivo RTF gerado com sucesso: " + file.getName());
        }
    }
    
    /**
     * Retorna o caminho do arquivo gerado
     * @return Caminho do arquivo gerado
     */
    public String getGeneratedFilePath() {
        return generatedFilePath;
    }
}
