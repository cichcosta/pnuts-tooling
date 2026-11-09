package PT.Minimal.geral;

import PT.Minimal.pnuts.MPnuts;
import PT.Minimal.util.MNumberUtil;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import java.util.Vector;





public class MPrepareEnt
{
  private MRow wr;
  private MResData wrd;
  
  public static void initJExps(String went)
    throws MError
  {
    MPnuts.findMPnut(went);
  }
  

  public MPrepareEnt(String went, String wentname)
    throws MError
  {
    if ((went.startsWith("W")) && (MLogin.getUserTipo().indexOf("X") == -1) && (!MLogin.getUser().equals("minimal")) && (!MLogin.getUser().equalsIgnoreCase(getMConnection"DEFAULT"wschema)))
    {
      throw new MError("MIN-009998E", new String[] { MLogin.getUser(), " aceder ao Datamart Minimal." });
    }
    

    MUtil.DFS.setDecimalSeparator('.');
    MUtil.DFS.setGroupingSeparator(' ');
    
    wrd = new MResData();
    

    boolean wbrline = false;
    
    wrd.setEnt(went);
    wrd.setEntName(wentname);
    
    wr = MMData.getMRow(went);
    

    initJExps(went);
    


    MMData.getExps(went);
    
    String wklc = "klc";String wklc2 = "CLK";
    

















    String sExt = "N";
    String sSel = " select count(*) from  mextt0  where i_ob like '" + went + "%' ";
    
    if (MNumberUtil.toInt(MUtil.getSQLValue(sSel)) > 0) {
      sExt = "S";
    }
    

    wrd.addMenu("Geral");
    wrd.addMenu("Comandos");
    wrd.addMenu("Outros");
    

    Vector wvcmd = MMData.getCmds(went);
    
    for (int i = 0; i < wvcmd.size(); i++) {
      Hashtable xmcfxt = (Hashtable)wvcmd.elementAt(i);
      MCommand wcmd = wrd.addCmd((String)xmcfxt.get("i_ob"), (String)xmcfxt.get("i_cmd"));
      

      wcmd.setType((String)xmcfxt.get("i_tcmd"));
      wcmd.setCmd((String)xmcfxt.get("t_cmd"));
      wcmd.setOrder((String)xmcfxt.get("t_ord"));
      wcmd.setArgs((String)xmcfxt.get("t_args"));
      wcmd.setMenuText((String)xmcfxt.get("t_menu"));
      wcmd.setPrefText((String)xmcfxt.get("t_pref"));
      wcmd.setHelpText((String)xmcfxt.get("t_ajuda"));
      wcmd.setZFase((String)xmcfxt.get("cfase"));
      wcmd.setOkCode((String)xmcfxt.get("ierr1"));
      wcmd.setFailCode((String)xmcfxt.get("ierr2"));
      wcmd.setNextCommand((String)xmcfxt.get("i_cmds"));
      wcmd.setCommandAutLevel((String)xmcfxt.get("cns"));
      
      if (wcmd.getMenuText().trim().length() != 0) {
        wrd.addMenuItem("Comandos", wcmd.getMenuText(), wcmd.getName());
      }
    }
    
    wrd.addMenuItem("Geral", "Criterios", "CRITERIUM_ROW");
    wrd.addMenuSeparator("Geral");
    wrd.addMenuItem("Geral", "Consultar", "INQUIRY_ROW");
    wrd.addMenuItem("Geral", "Alterar", "UPDATE_ROW");
    wrd.addMenuItem("Geral", "Eliminar", "DELETE_ROW");
    wrd.addMenuItem("Geral", "Inserir", "INSERT_ROW");
    wrd.addMenuItem("Geral", "Texto Associado", "TEXT_ROW");
    
    wrd.addMenuSeparator("Geral");
    wrd.addMenuItem("Geral", "Imprimir", "PRINT_BROWSE");
    wrd.addMenuItem("Geral", "Imprimir c/Txt", "PRINT_BRTEXT");
    wrd.addMenuSeparator("Geral");
    wrd.addMenuItem("Geral", "Sair", "CLOSEWINDOW");
    
    wrd.addMenuItem("Outros", "Reinicializar Fichas", "RERUN_FX");
    wrd.addMenuItem("Outros", "Reinicializar SQL", "RERUN_SQL");
    wrd.addMenuSeparator("Outros");
    wrd.addMenuItem("Outros", "Executar", "Execute");
    

    wrd.addToolBar("ToolBar Minimal", "Normal");
    
    Vector wv = MMData.getMenuItems(went + "BR");
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xsment = (Hashtable)wv.elementAt(i);
      if ((!((String)xsment.get("tgif")).equals("BLANK")) && (!((String)xsment.get("tgif")).startsWith("#")))
      {
        wrd.addToolBarItem("ToolBar Minimal", (String)xsment.get("nopcao"), (String)xsment.get("iobj"), (String)xsment.get("tgif"));
      }
      



      if (((String)xsment.get("tgif")).startsWith("#"))
      {
        wrd.addToolBarItem("ToolBar Minimal", (String)xsment.get("tgif") + " " + (String)xsment.get("nopcao"), (String)xsment.get("iobj"), (String)xsment.get("tgif"));
      }
    }
    



    if ((wv == null) || (wv.size() == 0))
    {
      wrd.addToolBar("ToolBar Minimal", "Normal");
      wrd.addToolBarItem("ToolBar Minimal", "Criterios", "CRITERIUM_ROW");
      wrd.addToolBarItem("ToolBar Minimal", "Refrescar", "REFRESH_ROWS");
      wrd.addToolBarItem("ToolBar Minimal", "---------", "-----------");
      wrd.addToolBarItem("ToolBar Minimal", "Consultar", "INQUIRY_ROW");
      wrd.addToolBarItem("ToolBar Minimal", "Alterar", "UPDATE_ROW");
      wrd.addToolBarItem("ToolBar Minimal", "Eliminar", "DELETE_ROW");
      wrd.addToolBarItem("ToolBar Minimal", "Inserir", "INSERT_ROW");
      wrd.addToolBarItem("ToolBar Minimal", "Texto Associado", "TEXT_ROW");
      wrd.addToolBarItem("ToolBar Minimal", "---------", "-----------");
      wrd.addToolBarItem("ToolBar Minimal", "Imprimir", "PRINT_BROWSE");
    }
    




    wrd.addToolBar("ToolBar Criterios", "Normal");
    



    wrd.addToolBarItem("ToolBar Criterios", "Cut", "Cut");
    wrd.addToolBarItem("ToolBar Criterios", "Copy", "Copy");
    wrd.addToolBarItem("ToolBar Criterios", "Paste", "Paste");
    wrd.addToolBarItem("ToolBar Criterios", "-------", "----------");
    wrd.addToolBarItem("ToolBar Criterios", "Limpar", "CLEAR_FORM");
    wrd.addToolBarItem("ToolBar Criterios", "-------", "----------");
    wrd.addToolBarItem("ToolBar Criterios", "Prompt", "PROMPT_ROW");
    wrd.addToolBarItem("ToolBar Criterios", "-------", "----------");
    wrd.addToolBarItem("ToolBar Criterios", "Criterios Adicionais", "EXTRA_CRIT");
    wrd.addToolBarItem("ToolBar Criterios", "-------", "----------");
    wrd.addToolBarItem("ToolBar Criterios", "Imprimir", "PRINT_CRIT");
    if (sExt.equals("S")) {
      wrd.addToolBarItem("ToolBar Criterios", "-------", "----------");
      wrd.addToolBarItem("ToolBar Criterios", "#EXT Extensões", "MC_EXTENSIONS");
    }
    

    wrd.addToolBar("ToolBar Entidade", "Normal");
    
    wrd.addToolBarItem("ToolBar Entidade", "Cut", "Cut");
    wrd.addToolBarItem("ToolBar Entidade", "Copy", "Copy");
    wrd.addToolBarItem("ToolBar Entidade", "Paste", "Paste");
    wrd.addToolBarItem("ToolBar Entidade", "-------", "----------");
    wrd.addToolBarItem("ToolBar Entidade", "Prompt", "PROMPT_ROW");
    wrd.addToolBarItem("ToolBar Entidade", "-------", "----------");
    wrd.addToolBarItem("ToolBar Entidade", "Texto Associado", "TEXT_ROW");
    if (sExt.equals("S")) {
      wrd.addToolBarItem("ToolBar Entidade", "-------", "----------");
      wrd.addToolBarItem("ToolBar Entidade", "#EXT Extensões", "ME_EXTENSIONS");
    }
    
    if (went.equals("DDOL9")) {
      wrd.addToolBarItem("ToolBar Entidade", "-------", "----------");
      wrd.addToolBarItem("ToolBar Entidade", "#TAD Texto Associado Documento", "TAD");
    }
    
    String sLang = "PT";
    String sCommand = "Comando";String sNReg = "Num Registos";String sReg = "Registos";String sSelected = "Seleccionados";
    if ((MLogin.hasLoginProperty("LANG")) || (MLogin.getLang().trim().length() > 0)) {
      if (MLogin.getLang().trim().length() > 0) {
        sLang = MLogin.getLang().trim();
      } else {
        sLang = MLogin.getLoginProperty("LANG");
      }
      if (sLang.equals("US")) {
        sCommand = "Command";sNReg = "Num Registers";sReg = "Registers";sSelected = "Selected";
      }
    }
    
    wrd.addPanel(went + "MCTP", "Right");
    wrd.addPanelComponent(went + "MCTP", 1, "wnmaxsel", sNReg, null, 70, false);
    MTextField wtf = (MTextField)wrd.addPanelComponent(went + "MCTP", 0, "wmaxsel", "MaxSel", null, 5, false);
    wtf.setMask("N####0");
    wtf.setInternalWidth(5);
    wtf.setText("200");
    wtf.setText(wtf.getFormatedText());
    

    wrd.addPanel(went + "MCBT", "Right");
    MButton wokbt = (MButton)wrd.addPanelComponent(went + "MCBT", 2, "wok", "OK", "OKDIALOG", 0, false);
    wrd.addPanelComponent(went + "MCBT", 2, "wend", MUtil.lang("\"Cancelar\""), "CLOSEWINDOW", 0, false);
    

    wrd.addPanel(went + "BRTP", "MLayout");
    





    wrd.addPanelComponent(went + "BRTP", 1, "wcmd", sCommand, null, 55, false);
    wtf = (MTextField)wrd.addPanelComponent(went + "BRTP", 0, "wcmd", "Comando", null, 4, false);
    if ((MLogin.hasLoginProperty("DIRECTCOMMAND")) && (MLogin.getLoginProperty("DIRECTCOMMAND").startsWith("N")))
    {
      wtf.setEditable(false);
    }
    wtf.setCommand("BRCMD");
    
    wrd.addPanelComponent(went + "BRTP", 1, "wnreg", sReg, null, 70, false);
    wtf = (MTextField)wrd.addPanelComponent(went + "BRTP", 0, "wnreg", "Registos", null, 5, false);
    wtf.setMask("N####0");
    wtf.setText(wtf.getFormatedText("0"));
    wtf.setEnabled(false);
    wrd.addPanelComponent(went + "BRTP", 1, "wnsel", sSelected, null, 90, false);
    wtf = (MTextField)wrd.addPanelComponent(went + "BRTP", 0, "wnsel", "Seleccionados", null, 5, false);
    wtf.setMask("N####0");
    wtf.setText(wtf.getFormatedText("0"));
    wtf.setEnabled(false);
    

    wrd.addPanel(went + "MEBT", "Right");
    wv = MMData.getJumpFields(went);
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      MButton wbt = (MButton)wrd.addPanelComponent(went + "MEBT", 2, (String)xmrfct.get("i_comp"), (String)xmrfct.get("t_lin"), (String)xmrfct.get("t_cmd"), 0, false);
      



      wbt.setCommandArgs((String)xmrfct.get("tcmdargs"));
    }
    wrd.addPanelComponent(went + "MEBT", 2, "wok", "OK", "OKDIALOG", 0, false);
    wrd.addPanelComponent(went + "MEBT", 2, "wend", MUtil.lang("\"Seguinte\""), "CLOSEWINDOW", 0, false);
    

    wrd.addPanel(went + "BRBT", "Right");
    wrd.addPanelComponent(went + "BRBT", 2, "wend", MUtil.lang("\"Terminar\""), "CLOSEWINDOW", 0, false);
  }
  

































































































































































































































































































































































































  public MRow getMRow()
  {
    return wr;
  }
  
  public MResData getResData() { return wrd; }
}
