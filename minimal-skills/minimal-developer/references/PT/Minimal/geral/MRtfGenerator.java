package PT.Minimal.geral;

import PT.Minimal.pnuts.MPnuts;
import PT.Minimal.actions.MExternalCommand;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

/**
 * MRtfGenerator - Classe para gerar arquivos RTF sem impressão ou visualização automática
 * Baseada em MGesPrint e MPrintBrowse
 */
public class MRtfGenerator {
    private static int fileNumber = 1;
    public static String EOL = "\n";
    public static String PGF = "\000X0C";
    private String fileExtension = ".rtf";
    private MPnuts mpnut;
    private String wfilename;
    private String generatedFilePath;
    private MForm minForm;
    private Vector wrows;
    private String[] wargs;
    
    /**
     * Construtor que recebe um MForm
     * @param form Formulário contendo dados
     * @throws MError Caso ocorra algum erro
     */
    public MRtfGenerator(MForm form) throws MError {
        this.minForm = form;
        if (minForm.getType() == "SINGLEROW") {
            Vector wv = new Vector();
            MRow wr = minForm.getRow().Copy();
            MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
            wv.addElement(wr);
            setRows(wv);
        }
        
        // Inicializa o MPnuts para processamento de templates
        mpnut = PT.Minimal.pnuts.MPnuts.getMPnut(minForm.getEnt());
        mpnut.setForm(minForm);
        if (wrows != null && wrows.size() > 0) {
            mpnut.setRow((MRow)wrows.elementAt(0));
        }
        mpnut.set("vrows", wrows);
    }
    
    /**
     * Define os dados que serão usados no relatório
     * @param wv Vector contendo os MRows
     */
    public void setRows(Vector wv) {
        wrows = wv;
    }
    
    /**
     * Método principal para gerar o arquivo RTF
     * @param templatePath Caminho completo do template ou relativo
     * @return Caminho do arquivo RTF gerado
     * @throws MError Caso ocorra algum erro
     */
    public String generateRtf(String templatePath) throws MError {
        return generateRtf(templatePath, null, null);
    }
    
    /**
     * Método principal para gerar o arquivo RTF com argumentos personalizados
     * @param templatePath Caminho do template
     * @param args Argumentos adicionais
     * @param outputFileName Nome do arquivo de saída (opcional)
     * @return Caminho do arquivo RTF gerado
     * @throws MError Caso ocorra algum erro
     */
    public String generateRtf(String templatePath, String args, String outputFileName) throws MError {
        try {
            // Prepara os argumentos
            parseArgs(templatePath, args);
            
            // Localiza e carrega o arquivo de template
            wfilename = checkFile(wargs[0], (wrows != null && wrows.size() > 0) ? (MRow)wrows.elementAt(0) : null);
            
            // Prepara o relatório
            prepareReport();
            
            // Processa o arquivo RTF
            processRtfFile(outputFileName);
            
            // Retorna o caminho do arquivo gerado
            return generatedFilePath;
            
        } catch (FileNotFoundException e) {
            throw new MError("Arquivo template não encontrado: " + e.getMessage());
        } catch (IOException e) {
            throw new MError("Erro ao processar arquivo: " + e.getMessage());
        }
    }
    
    /**
     * Parse dos argumentos passados para o gerador
     * @param templatePath Caminho do template
     * @param additionalArgs Argumentos adicionais
     */
    private void parseArgs(String templatePath, String additionalArgs) {
        String[] wwargs = null;
        if (additionalArgs != null && additionalArgs.trim().length() != 0) {
            wwargs = MUtil.getArgs(additionalArgs, ' ');
        } else {
            wwargs = MUtil.getArgs(templatePath, ' ');
        }
        
        wargs = new String[4];
        for (int i = 0; (i < wwargs.length) && (i < 4); i++) {
            wargs[i] = wwargs[i];
        }
        
        if (wargs[0] == null) {
            wargs[0] = templatePath;
        }
        
        if (wargs[2] == null) {
            wargs[2] = "MINTEMPLATES";
        }
    }
    
    /**
     * Processa o arquivo RTF
     * @param outputFileName Nome do arquivo de saída (opcional)
     * @throws MError Caso ocorra algum erro
     * @throws IOException Caso ocorra erro de IO
     */
    private void processRtfFile(String outputFileName) throws MError, IOException {
        // Lê o template
        File wfile = new File(wfilename);
        BufferedInputStream wstream = new BufferedInputStream(new java.io.FileInputStream(wfile));
        
        byte[] b = new byte[(int)wfile.length()];
        wstream.read(b, 0, b.length);
        wstream.close();
        
        String wallfile = clearRTF(new String(b));
        
        // Localiza seções do template
        int wpos1 = wallfile.indexOf("STARTCURSOR");
        int wpos2 = wallfile.lastIndexOf("ENDCURSOR");
        int wpospar1 = -1;
        int wpospar2 = -1;
        int wpospar3 = -1;
        
        if ((wpos1 != -1) && (wpos2 != -1) && (fileExtension.equals(".rtf"))) {
            wpospar1 = wallfile.indexOf("\\par", wpos1);
            wpospar2 = wallfile.lastIndexOf("\\par", wpos2);
            wpospar3 = wallfile.indexOf("\\par", wpos2);
        }
        
        if ((wpospar1 == -1) && (wpospar2 == -1) && (wpos1 != -1) && (wpos2 != -1) && (!fileExtension.equals(".rtf"))) {
            wpospar1 = wallfile.indexOf(EOL, wpos1);
            wpospar2 = wallfile.lastIndexOf(EOL, wpos2);
            wpospar3 = wallfile.indexOf(EOL, wpos2);
            if ((wpospar1 != -1) && (wpospar2 != -1)) {
                wpospar1 += 1;
                wpospar2 += 1;
                wpospar3 += 1;
            }
        }
        
        if (wpospar3 < 1) {
            wpospar3 = wallfile.length();
        }
        
        // Processa seções do template
        String wfile_head = wallfile;
        String wfile_curs = "";
        String wfile_foot = "";
        
        StringBuffer wout_curs = new StringBuffer(1000000);
        String wstartline = "";
        String wendline = "";
        
        if ((wpos1 != -1) && (wpos2 != -1) && (wpospar1 != -1) && (wpospar2 != -1)) {
            wfile_head = wallfile.substring(0, wpos1);
            wfile_curs = wallfile.substring(wpospar1, wpospar2);
            wstartline = wallfile.substring(wpos1, wpospar1);
            wendline = wallfile.substring(wpos2, wpospar3);
            if ((wpospar3 > 0) && (wpospar3 != wallfile.length())) {
                wfile_foot = wallfile.substring(wpospar3);
            }
            
            prepareCursor("DEFAULT", wfile_curs);
            
            mpnut.set("wout_curs", wout_curs);
            if (wrows != null) {
                for (int i = 0; i < wrows.size(); i++) {
                    mpnut.set("wrowcount", new Integer(i));
                    mpnut.evalFormatedValues(wstartline);
                    wout_curs.append(getCursorRow("DEFAULT", i));
                    mpnut.evalFormatedValues(wendline);
                }
            }
            mpnut.unset("wrowcount");
            mpnut.unset("wout_curs");
        }
        
        wfile_head = mpnut.evalFormatedValues(wfile_head);
        wfile_foot = mpnut.evalFormatedValues(wfile_foot);
        
        // Gera o arquivo RTF
        String fileOutName = (outputFileName != null) ? 
                             outputFileName : 
                             MUtil.myProp.getProperty("user.name") + fileNumber + fileExtension;
        fileNumber += 1;
        
        String filePath = MUtil.myProp.getProperty("JTEMP") + File.separator + fileOutName;
        File outputFile = new File(filePath);
        
        DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
        
        if (wfile_head.trim().length() != 0) {
            fos.write(wfile_head.getBytes());
        }
        
        if (wout_curs.length() != 0) {
            fos.writeBytes(wout_curs.toString());
        }
        
        if (wfile_foot.trim().length() != 0) {
            fos.write(wfile_foot.getBytes());
        }
        
        fos.close();
        
        // Armazena o caminho do arquivo gerado para retorno
        generatedFilePath = outputFile.getAbsolutePath();
    }
    
    /**
     * Prepara o relatório para geração
     * @throws MError Caso ocorra algum erro
     */
    public void prepareReport() throws MError {
        String wheader1 = " ";
        String wheader2 = " ";
        String wdef_cursor = "";
        
        MHashtable wh = new MHashtable(true);
        wh.put("HEADER1", wheader1);
        wh.put("HEADER2", wheader2);
        wh.put("DEFAULT", wdef_cursor);
        wh.put("TOTAL", " ");
        wh.put("ENTNAME", minForm.getEntName());
        wh.put("ENT", minForm.getEnt());
        wh.put("USER", MLogin.getUser());
        
        mpnut.put("REPORT", wh);
        mpnut.set("REPORT", wh);
        if (wrows != null) {
            mpnut.set("wrows", wrows);
        }
    }
    
    /**
     * Prepara o cursor para processamento
     * @param cursor_name Nome do cursor
     * @param cursor_mask Máscara do cursor
     * @throws MError Caso ocorra algum erro
     */
    public void prepareCursor(String cursor_name, String cursor_mask) throws MError {
        MHashtable wmh = (MHashtable) mpnut.get("REPORT");
        if (cursor_mask.indexOf("DEFAULT") != -1) {
            wmh.put(cursor_name, mpnut.evalFormatedValues(cursor_mask));
        } else {
            wmh.put(cursor_name, cursor_mask);
        }
    }
    
    /**
     * Obtém uma linha do cursor
     * @param cursor_name Nome do cursor
     * @param i Índice da linha
     * @return String contendo a linha formatada
     * @throws MError Caso ocorra algum erro
     */
    public String getCursorRow(String cursor_name, int i) throws MError {
        MHashtable wmh = (MHashtable) mpnut.get("REPORT");
        
        if (wrows == null || i >= wrows.size()) {
            return "";
        }
        
        MRow wrow = (MRow) wrows.elementAt(i);
        mpnut.setRow(wrow);
        String ws = mpnut.evalFormatedValues((String) wmh.get(cursor_name));
        return ws;
    }
    
    /**
     * Verifica e localiza o arquivo de template
     * @param wfilename Nome do arquivo de template
     * @param wrow MRow contendo dados
     * @return Caminho completo do arquivo de template
     * @throws MError Caso o arquivo não seja encontrado
     */
    private String checkFile(String wfilename, MRow wrow) throws MError {
        if (wrow != null) {
            mpnut.setRow(wrow);
            
            String wiorg = "";
            if (wrow.containsKey("iorg")) {
                wiorg = ((String) wrow.get("iorg")).trim();
                if (wiorg.equals("*")) {
                    wiorg = "X";
                }
                mpnut.put("IORG", wiorg);
            } else {
                mpnut.put("IORG", "");
            }
        }
        
        // Verifica arquivos DEFAULT.rtf
        if (wfilename.indexOf("DEFAULT.rtf") != -1) {
            File wnfile = new File(MLogin.getCliTemplateDir() + "/" + minForm.getEnt().substring(0, 4) + "/" 
                    + minForm.getEnt() + "/" + "DEFAULT.rtf");
            
            if (wnfile.exists()) {
                return wnfile.getAbsolutePath();
            }
            
            wnfile = new File(MLogin.getLogoTemplateDir() + "/" + minForm.getEnt().substring(0, 4) + "/" 
                    + minForm.getEnt() + "/" + "DEFAULT.rtf");
            
            if (wnfile.exists()) {
                return wnfile.getAbsolutePath();
            }
            
            wnfile = new File(MLogin.getMinTemplateDir() + "/" + minForm.getEnt().substring(0, 4) + "/" 
                    + minForm.getEnt() + "/" + "DEFAULT.rtf");
            
            if (wnfile.exists()) {
                return wnfile.getAbsolutePath();
            }
        }
        
        // Busca nos diretórios de templates
        mpnut.put("TEMPLATES", MLogin.getCliTemplateDir());
        
        String ws = mpnut.evalFormatedValues(wfilename);
        File wfile = new File(ws);
        
        if (wfile.exists()) {
            return wfile.getAbsolutePath();
        }
        
        mpnut.put("IORG", "");
        ws = mpnut.evalFormatedValues(wfilename);
        wfile = new File(ws);
        
        if (wfile.exists()) {
            return wfile.getAbsolutePath();
        }
        
        mpnut.put("TEMPLATES", MLogin.getLogoTemplateDir());
        
        if (wrow != null && wrow.containsKey("iorg")) {
            mpnut.put("IORG", ((String) wrow.get("iorg")).trim());
        }
        
        ws = mpnut.evalFormatedValues(wfilename);
        wfile = new File(ws);
        
        if (wfile.exists()) {
            return wfile.getAbsolutePath();
        }
        
        mpnut.put("IORG", "");
        ws = mpnut.evalFormatedValues(wfilename);
        wfile = new File(ws);
        
        if (wfile.exists()) {
            return wfile.getAbsolutePath();
        }
        
        mpnut.put("TEMPLATES", MLogin.getMinTemplateDir());
        ws = mpnut.evalFormatedValues(wfilename);
        wfile = new File(ws);
        
        if (wfile.exists()) {
            return wfile.getAbsolutePath();
        }
        
        throw new MError("MIN-910006E", new String[] { wfilename });
    }

    /**
     * Limpa as tags RTF do texto
     * @param wtext Texto do RTF
     * @return Texto processado
     */
    public String clearRTF(String wtext) {
        int j = 0;
        for (int i = wtext.indexOf("["); i != -1; i = wtext.indexOf("[", j)) {
            int wend = wtext.indexOf("]", i);
            if (wend != -1) {
                wtext = wtext.substring(0, i) + clearRTFPnut(wtext.substring(i, wend + 1)) + wtext.substring(wend + 1);
            }
            j = i + 1;
        }
        return wtext;
    }
    
    /**
     * Limpa as tags de pnuts no RTF
     * @param wpnut Texto pnut
     * @return Texto limpo
     */
    public String clearRTFPnut(String wpnut) {
        String wclearpnut = "";
        boolean inLixo = false;
        String wscar = "";
        for (int i = 0; i < wpnut.length(); i++) {
            wscar = wpnut.substring(i, i + 1);
            if ((wscar.equals("}")) || (wscar.equals("{"))) {
                inLixo = true;
            }
            if (inLixo) {
                if (wscar.equals(" ")) {
                    inLixo = false;
                }
            } else {
                wclearpnut = wclearpnut + wscar;
            }
        }
        wclearpnut = MUtil.stringRep(wclearpnut, "\n", "");
        wclearpnut = MUtil.stringRep(wclearpnut, "\r", "");
        return wclearpnut;
    }
    
    /**
     * Retorna o caminho do arquivo gerado
     * @return Caminho do arquivo gerado
     */
    public String getGeneratedFilePath() {
        return generatedFilePath;
    }
}
