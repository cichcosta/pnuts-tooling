package PT.Minimal.geral;

import PT.Minimal.util.MXmlReader;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

public class MMenu extends MFrame
{
  private MForm minForm;
  private javax.swing.JFrame minFrame;
  private static Hashtable htTran = new Hashtable();
  private static String wopcao = ""; private static String wopcao2 = "";
  private String wck = "CLK";
  
  public MMenu() throws MError {
    this("JPPLA");
    
    setBackground(new Color(215, 228, 244));
  }
  
  public MMenu(String wini) throws MError {
    super("Menu Minimal");
    new PT.Minimal.util.AltGrPatch();
    
    setBackground(new Color(215, 228, 244));
    
    MImages wimgs = new MImages();
    wimgs.getImg(" ");
    PT.Minimal.geral.console.MConsole.start();
    minFrame = this;
    
    MLogin.setInitialMenu(this);
    
    if (wini.trim().equals("#ENT")) {
      return;
    }
    
    MLogin.showLogin(this, this);
  }
  
  public void doInitialMenu()
    throws MError
  {
    try
    {
      minFrame.setTitle("Menu Minimal");
      minForm = new MForm("Menu Minimal");
      MForm.removeMForm("Menu Minimal");
      minForm.setEnt("EEEE0");
      minForm.setEntName("Menu Minimal");
      
      String wini = MLogin.getUserMenu();
      




      String wkc = "";
      String wkcsuf = "";
      if ((MLogin.hasLoginProperty(wck)) && (MLogin.getLoginProperty(wck).trim().length() > 13)) {
        wkc = MLogin.getLoginProperty(wck);
        wkcsuf = wkc.substring(18);
        wkcsuf = wkcsuf + " : ";
      }
      
      if ((!MLogin.hasLoginProperty(wck)) || ((MLogin.hasLoginProperty(wck)) && ((MLogin.getLoginProperty(wck).trim().length() <= 18) || (!MLogin.getLoginProperty(wck).substring(5, 6).equals("-")))))
      {
        throw new MError("MIN-000000E", new String[] { " - Chave de Licenciamento." });
      }
      
      minFrame.getContentPane().setLayout(new java.awt.BorderLayout());
      


      if ((MLogin.hasLoginProperty("SCHEMA")) && ((!MLogin.hasLoginProperty("SCHEMAFORCE")) || (!MLogin.getLoginProperty("SCHEMAFORCE").equals("S")))) {
        String wschema = MLogin.getLoginProperty("SCHEMA").toUpperCase();
        wkcsuf = wkcsuf + wschema + " : ";
      }
      minFrame.setTitle("Minimal L&W 4.20 - " + wkcsuf + MLogin.getChosenDSN());
      
      minFrame.addWindowListener(minForm.getWindowListener());
      
      JMenuBar wmb;
      
      JMenuBar wmb;
      
      if (MSerializedObject.existsObject("DEFAULT", "MEN", wini)) {
        wmb = (JMenuBar)MSerializedObject.getObject("DEFAULT", "MEN", wini);
      } else {
        wmb = getMainMenuBar(wini);
      }
      wmb.setBackground(new Color(215, 228, 244));
      minForm.addMenuBar(wmb);
      
      String went = "EEEE0";
      PT.Minimal.pnuts.MPnuts.findMPnut(went);
      
      Vector wvcmd = MMData.getCmds(went);
      Vector LCmd = new Vector();
      

      for (int i = 0; i < wvcmd.size(); i++) {
        Hashtable xmcfxt = (Hashtable)wvcmd.elementAt(i);
        MCommand wcmd = new MCommand((String)xmcfxt.get("i_ob"), (String)xmcfxt.get("i_cmd"));
        

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
        
        LCmd.addElement(wcmd);
      }
      
      MForm.addCmds(went, LCmd);
      

      setForm(minForm);
      minForm.prepareForm();
      
      minFrame.setResizable(false);
      minFrame.pack();
      minFrame.setSize(Math.max(getPreferredSizewidth, 625), 65);
      


      MMData.getPanel("SERR0BR");
      
      minForm.setFormParent(this);
      

      minForm.showForm();
      














































































      System.out.println("!*** MMenu minFrame: ");
      System.out.println(minFrame instanceof java.awt.Component);
    } catch (MError err) {
      err.show(minFrame);
      throw err;
    }
  }
  
  public static JMenuBar getMainMenuBar(String wini) throws MError
  {
    String srest = null;String wversao = " ";String wlight = " ";String wpais = "PT";String wadd = "S";String wimen = " ";String wiobj = " ";String wiobjold = " ";String wexclude = "";
    

    htTran = new Hashtable();
    
    if (MLogin.getLang().trim().length() > 0) {
      if (MLogin.hasLoginProperty("LANGLOC")) {
        String sFile = MLogin.getLoginProperty("LANGLOC") + "/tran/" + MLogin.getLang() + "tran";
        MGetMData.getRunTimeAtribsHLL("LB", sFile, htTran);
      }
      else if (MLogin.hasLoginProperty("METADATALOC")) {
        String sFile = MLogin.getLoginProperty("METADATALOC") + "/tran/" + MLogin.getLang() + "tran";
        MGetMData.getRunTimeAtribsHLL("LB", sFile, htTran);
      }
      
    }
    else if (MLogin.hasLoginProperty("LANG")) {
      if (MLogin.hasLoginProperty("LANGLOC")) {
        String sFile = MLogin.getLoginProperty("LANGLOC") + "/tran/" + MLogin.getLoginProperty("LANG") + "tran";
        MGetMData.getRunTimeAtribsHLL("LB", sFile, htTran);
      }
      else if (MLogin.hasLoginProperty("METADATALOC")) {
        String sFile = MLogin.getLoginProperty("METADATALOC") + "/tran/" + MLogin.getLoginProperty("LANG") + "tran";
        MGetMData.getRunTimeAtribsHLL("LB", sFile, htTran);
      }
    }
    


    Hashtable htHidden = new Hashtable();
    String sFile = null;
    
    if (MLogin.hasLoginProperty("CLIDATALOC")) {
      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/hidden/" + "EEEE0" + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
    }
    if (MLogin.hasLoginProperty("METADATALOC")) {
      sFile = MLogin.getLoginProperty("METADATALOC") + "/hidden/" + "EEEE0" + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
    }
    
    if (!MLogin.getLoginProperty("JMETADATA").equals(MLogin.getLoginProperty("DEFAULT"))) {
      String wsel = " select * from scfgt0 where iorg = ' ' and zk = (select max(zk) from scfgt0)";
      
      MCursor scfg_curs = MConnection.getMCursor();
      try {
        Vector vscfg = MUtil.getRows(wsel, scfg_curs);
        if (vscfg.size() > 0) {
          MRow rscfg = (MRow)vscfg.firstElement();
          MLogin.setLoginProperty("REGION", (String)rscfg.getField("cep1"));
          MLogin.setLoginProperty("TEMPLATE", (String)rscfg.getField("cep2"));
        }
      } catch (MError err) {
        System.out.println("!*** MMenu getWdcode: " + err.getWdcode());

      }
      finally
      {
        scfg_curs.close();
      }
    }
    else {
      MLogin.setLoginProperty("REGION", "PT");
      MLogin.setLoginProperty("TEMPLATE", "*");
    }
    
    wexclude = "";
    
    if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WHB0%' and iobj not like 'HCEP1%' ";
    }
    
    if ((MLogin.hasLoginProperty("EXTENSIONS")) && (!MLogin.getLoginProperty("EXTENSIONS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WZ%' ";
    }
    
    if ((MLogin.hasLoginProperty("EXTENSIONSRH")) && (!MLogin.getLoginProperty("EXTENSIONSRH").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WHZ%' ";
    }
    
    if ((MLogin.hasLoginProperty("PORTOS")) && (!MLogin.getLoginProperty("PORTOS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WOP%' ";
    }
    
    if ((MLogin.hasLoginProperty("ETPS")) && (!MLogin.getLoginProperty("ETPS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WTP%' ";
    }
    

    if ((MLogin.hasLoginProperty("DATAMART")) && (!MLogin.getLoginProperty("DATAMART").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WW00%' ";
    }
    
    JMenuBar wmb = new JMenuBar();
    wmb.setBackground(new Color(215, 228, 244));
    
    String wcsel = " select imen, iobjt, iobj, xopcao, nopcao, tgif from sment0 ";
    wcsel = wcsel + " where imen = '" + wini + "' " + wexclude;
    
    wcsel = wcsel + " order by imen, xopcao, nopcao";
    


    Vector wv = new Vector();
    


    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")))
    {


      if (MLogin.hasLoginProperty("VERSION")) {
        wversao = MLogin.getLoginProperty("VERSION");
        if ((wversao.trim().length() > 9) && (
          (wversao.substring(9, 10).equals("E")) || (wversao.substring(9, 10).equals("L")))) {
          wlight = "S";
        }
      }
      


      System.out.println("!*** MMenu getMainMenuBar wv em ficheiro: " + MLogin.getLoginProperty("METADATALOC") + "/smen/" + wini + "menu");
      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/smen/" + wini + "menu");
      










      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        String stot = xml.getTagValue("wkobj");
        
        srest = stot;
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          
          ht.put("imen", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iobjt", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iobj", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("xopcao", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("nopcao", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("tgif", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("targs", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          
          wadd = "S";
          
          if (((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 4).equals("WHB0"))) || ((((String)ht.get("iobj")).length() > 4) && (((String)ht.get("iobj")).substring(0, 5).equals("HCEP1")))) {
            if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          
          if ((((String)ht.get("iobj")).length() > 4) && (((String)ht.get("iobj")).substring(0, 4).equals("WW00"))) {
            if ((MLogin.hasLoginProperty("DATAMART")) && (!MLogin.getLoginProperty("DATAMART").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 2) && (((String)ht.get("iobj")).substring(0, 2).equals("WZ")))
          {
            if ((MLogin.hasLoginProperty("EXTENSIONS")) && (!MLogin.getLoginProperty("EXTENSIONS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WHZ"))) {
            if ((MLogin.hasLoginProperty("EXTENSIONSRH")) && (!MLogin.getLoginProperty("EXTENSIONSRH").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WOP"))) {
            if ((MLogin.hasLoginProperty("PORTOS")) && (!MLogin.getLoginProperty("PORTOS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WTP"))) {
            if ((MLogin.hasLoginProperty("ETPS")) && (!MLogin.getLoginProperty("ETPS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if (wlight.equals("S")) {
            wiobj = (String)ht.get("iobj");
            if (wiobj.trim().equals(wiobjold.trim())) {
              wadd = "N";
            }
            if ((wiobj.trim().length() > 4) && ((wiobj.substring(0, 4).equals("CRCT")) || (wiobj.substring(0, 4).equals("CCHV")) || (wiobj.substring(0, 4).equals("BPSS")) || (wiobj.substring(0, 4).equals("BPSC")) || (wiobj.substring(0, 4).equals("WDCC")) || (wiobj.substring(0, 4).equals("WDCA")) || (wiobj.substring(0, 3).equals("BPR")) || (wiobj.substring(0, 3).equals("CAO")) || (wiobj.substring(0, 3).equals("CMO")) || (wiobj.substring(0, 4).equals("DDOO")) || (wiobj.substring(0, 3).equals("DPR")) || (wiobj.substring(0, 2).equals("DJ")) || (wiobj.substring(0, 1).equals("K")) || (wiobj.substring(0, 4).equals("WGRP")) || (wiobj.substring(0, 4).equals("WOC0")) || (wiobj.substring(0, 4).equals("WOCP")) || (wiobj.substring(0, 4).equals("COBO")) || (wiobj.substring(0, 4).equals("WPTX")) || (wiobj.substring(0, 4).equals("WPTP")) || (wiobj.substring(0, 4).equals("WFXL")) || (wiobj.substring(0, 4).equals("WPE0")) || (wiobj.substring(0, 3).equals("PEM")) || (wiobj.substring(0, 4).equals("WPO0")) || (wiobj.substring(0, 1).equals("O")) || (wiobj.substring(0, 4).equals("WEC0")) || (wiobj.substring(0, 4).equals("WVD0")) || (wiobj.substring(0, 4).equals("WCP0")) || (wiobj.substring(0, 4).equals("WBL0")) || (wiobj.substring(0, 4).equals("WSQ0")) || (wiobj.substring(0, 4).equals("HPAT")) || (wiobj.substring(0, 4).equals("WHE3")) || (wiobj.substring(0, 4).equals("WHE6")) || (wiobj.substring(0, 4).equals("WHS0")) || (wiobj.substring(0, 4).equals("WHF0")) || (wiobj.substring(0, 4).equals("WHM0")) || (wiobj.substring(0, 4).equals("WHA0")) || (wiobj.substring(0, 4).equals("WHJ0")) || (wiobj.substring(0, 4).equals("WHR0")) || (wiobj.substring(0, 5).equals("IIPT9")) || (wiobj.substring(0, 5).equals("BXPF0"))))
            {









































              wadd = "N";
            } else {
              wiobjold = wiobj;
            }
          }
          

          if (!wadd.equals("N")) {
            wv.add(ht);
          } else {
            wadd = "S";
          }
          
        }
        
      }
      

    }
    else
    {
      MCursor esnm_curs = MConnection.getMCursor();
      try {
        esnm_curs.execQuery(wcsel);
        if (esnm_curs.getWdcode() == 0) {
          wv = esnm_curs.getAllRecords();
          for (int i = 0; i <= wv.size() - 1; i++) {}
        }
      }
      catch (MError err) {
        System.out.println("!*** MMenu getWdcode: " + err.getWdcode());
        if ((err.getWdcode() != 65330) && (err.getWdcode() != 942)) {
          throw err;
        }
      } finally {
        esnm_curs.close();
      }
    }
    
    if ((wv != null) && (wv.size() > 0)) {
      for (int i = 0; i < wv.size(); i++) {
        Hashtable xsment = (Hashtable)wv.elementAt(i);
        if (((String)xsment.get("iobjt")).startsWith("M"))
        {
          wadd = "S";
          wadd = (String)htHidden.get((String)xsment.get("imen") + ":" + (String)xsment.get("nopcao"));
          if (wadd == null) {
            wadd = "S";
          }
          
          if (htTran.containsKey((String)xsment.get("nopcao"))) {
            wadd = "N";
            wmb.add(getMenu((String)xsment.get("iobj"), (String)htTran.get((String)xsment.get("nopcao"))));
          }
          
          if (wadd.equals("S")) {
            wmb.add(getMenu((String)xsment.get("iobj"), (String)xsment.get("nopcao")));
          }
        }
      }
    }
    

    String sLang = "PT";
    String sGeral = "Geral";String sNotFrom = "Notificações de Terceiros";String sNotTo = "Notificações a Terceiros";String sNotHist = "Histórico Notificações";
    String sCloseW = "Fechar Janelas";String sRecon = "Reconectar Base de Dados";String sChangePass = "Mudar Password";
    String sShowConsole = "Mostrar Consola";String sShowMemory = "Mostrar Memória";String sExecute = "Executar";String sHelp = "Ajuda";String sDirectImp = "Impressão Directa";String sExit = "Sair";
    if ((MLogin.hasLoginProperty("LANG")) || (MLogin.getLang().trim().length() > 0)) {
      if (MLogin.getLang().trim().length() > 0) {
        sLang = MLogin.getLang().trim();
      } else {
        sLang = MLogin.getLoginProperty("LANG");
      }
      if (sLang.equals("US")) {
        sGeral = "General";
        sNotFrom = "Notifications From";
        sNotTo = "Notifications To";
        sNotHist = "Notifications History";
        sCloseW = "Close Windows";
        sRecon = "Data Base Reconnect";
        sChangePass = "Change Password";
        sShowConsole = "Show Console";
        sShowMemory = "Show Memory";
        sExecute = "Execute";
        sHelp = "Help";
        sDirectImp = "Direct Print";
        sExit = "Exit";
      }
    }
    

    JMenu wapl = new MJMenuPlus(sGeral);
    
    wapl.setBackground(new Color(215, 228, 244));
    





    JMenuItem wqnot1 = new JMenuItem(sNotFrom);
    wqnot1.setBackground(new Color(215, 228, 244));
    wqnot1.setActionCommand("QNOT1_JUMP");
    wapl.add(wqnot1);
    
    JMenuItem wqnot0 = new JMenuItem(sNotTo);
    wqnot0.setBackground(new Color(215, 228, 244));
    wqnot0.setActionCommand("QNOT0_JUMP");
    wapl.add(wqnot0);
    
    JMenuItem wqnot0h = new JMenuItem(sNotHist);
    wqnot0h.setBackground(new Color(215, 228, 244));
    wqnot0h.setActionCommand("RUN QNOT0 Histórico Notificações");
    wapl.add(wqnot0h);
    
    wapl.addSeparator();
    
    JMenuItem wclose = new JMenuItem(sCloseW);
    wclose.setBackground(new Color(215, 228, 244));
    wclose.setActionCommand("CLOSEALL");
    wapl.add(wclose);
    
    JMenuItem wrecon = new JMenuItem(sRecon);
    wrecon.setBackground(new Color(215, 228, 244));
    wrecon.setActionCommand("RECONNECT");
    wapl.add(wrecon);
    
    if ((!MLogin.hasLoginProperty("CHANGEPASSWORD")) || (!MLogin.getLoginProperty("CHANGEPASSWORD").startsWith("N")))
    {
      if (!MLogin.getDefaultAmbient().equals("Informix")) {
        wapl.addSeparator();
        
        JMenuItem wcpass = new JMenuItem(sChangePass);
        wcpass.setBackground(new Color(215, 228, 244));
        
        wcpass.setActionCommand("CHANGE_PASSWD");
        wapl.add(wcpass);
      }
    }
    

    wapl.addSeparator();
    
    JCheckBoxMenuItem wexon = new JCheckBoxMenuItem("Explain");
    wexon.setBackground(new Color(215, 228, 244));
    wexon.setActionCommand("Explain");
    wapl.add(wexon);
    JCheckBoxMenuItem wtron = new JCheckBoxMenuItem("Trace");
    wtron.setBackground(new Color(215, 228, 244));
    wtron.setActionCommand("pnutsTrace");
    wapl.add(wtron);
    
    JCheckBoxMenuItem wsqlon = new JCheckBoxMenuItem("Trace Sql");
    wsqlon.setBackground(new Color(215, 228, 244));
    wsqlon.setActionCommand("sqlTrace");
    wapl.add(wsqlon);
    
    wapl.addSeparator();
    
    JMenuItem wconon = new JMenuItem(sShowConsole);
    wconon.setBackground(new Color(215, 228, 244));
    
    wconon.setActionCommand("SHOW_CONSOLE");
    wapl.add(wconon);
    
    wapl.addSeparator();
    
    JMenuItem wshowmem = new JMenuItem(sShowMemory);
    wshowmem.setBackground(new Color(215, 228, 244));
    
    wshowmem.setActionCommand("SHOW_MEM");
    wapl.add(wshowmem);
    
    wapl.addSeparator();
    
    if ((!MLogin.hasLoginProperty("DIRECTEXECUTE")) || (!MLogin.getLoginProperty("DIRECTEXECUTE").startsWith("N")))
    {
      JMenuItem wexe = new JMenuItem(sExecute);
      wexe.setBackground(new Color(215, 228, 244));
      
      wexe.setActionCommand("Execute");
      wapl.add(wexe);
      wapl.addSeparator();
    }
    
    JMenuItem whelp = new JMenuItem(sHelp);
    whelp.setBackground(new Color(215, 228, 244));
    
    whelp.setActionCommand("HELP");
    wapl.add(whelp);
    
    wapl.addSeparator();
    
    JCheckBoxMenuItem wprintop = new JCheckBoxMenuItem(sDirectImp);
    wprintop.setBackground(new Color(215, 228, 244));
    wprintop.setActionCommand("printPreview");
    wapl.add(wprintop);
    
    wapl.addSeparator();
    
    JMenuItem wfim = new JMenuItem(sExit);
    wfim.setBackground(new Color(215, 228, 244));
    
    wfim.setActionCommand("ENDALL");
    wapl.add(wfim);
    


    wmb.add(wapl);
    
    return wmb;
  }
  
  public static JMenuBar getMinMenuBar(String wori)
    throws MError
  {
    String srest = null;String wversao = " ";String wlight = " ";String wpais = "PT";String wadd = "S";String wimen = " ";String wiobj = " ";String wiobjold = " ";String wexclude = "";
    

    JMenuBar wmb = null;
    


    Hashtable htHidden = new Hashtable();
    Hashtable htLabels = new Hashtable();
    String sFile = null;String sLabel = null;String sLabelNew = null;
    
    String wwobj = "!";
    if (wori.trim().length() > 4) {
      wwobj = wori.substring(0, 5);
    }
    

    if (MLogin.hasLoginProperty("CLIDATALOC"))
    {

      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/hidden/" + "EEEE0" + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
      

      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/hidden/" + wwobj.substring(0, 5) + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
    }
    


    if (MLogin.hasLoginProperty("METADATALOC"))
    {

      sFile = MLogin.getLoginProperty("METADATALOC") + "/hidden/" + "EEEE0" + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
      

      sFile = MLogin.getLoginProperty("METADATALOC") + "/hidden/" + wwobj.substring(0, 5) + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
    }
    



    String wconnect = "JMETADATA";
    String wcsel = " select imen, iobjt, iobj, xopcao, nopcao, tgif from sment0 ";
    wcsel = wcsel + " where imen = '" + wori + "' ";
    
    wcsel = wcsel + " order by imen, xopcao, nopcao";
    

    Vector wv = new Vector();
    

    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE"))))
    {



      if (MLogin.hasLoginProperty("VERSION")) {
        wversao = MLogin.getLoginProperty("VERSION");
        if ((wversao.trim().length() > 9) && (
          (wversao.substring(9, 10).equals("E")) || (wversao.substring(9, 10).equals("L")))) {
          wlight = "S";
        }
      }
      

      System.out.println("!*** MMenu getMinMenuBar wv em ficheiro: " + MLogin.getLoginProperty("METADATALOC") + "/smen/" + wori + "menu");
      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/smen/" + wori + "menu");
      










      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        String stot = xml.getTagValue("wkobj");
        
        srest = stot;
        
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          
          ht.put("imen", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iobjt", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iobj", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("xopcao", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("nopcao", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("tgif", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("targs", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          

          wadd = (String)htHidden.get((String)ht.get("iobj") + ":*");
          if (wadd == null) { wadd = "S";
          }
          
          if (wlight.equals("S")) {
            wiobj = (String)ht.get("iobj");
            if (wiobj.trim().equals(wiobjold.trim())) {
              wadd = "N";
            }
            if ((wiobj.trim().length() > 4) && ((wiobj.substring(0, 4).equals("CRCT")) || (wiobj.substring(0, 4).equals("CCHV")) || (wiobj.substring(0, 4).equals("BPSS")) || (wiobj.substring(0, 4).equals("BPSC")) || (wiobj.substring(0, 4).equals("WDCC")) || (wiobj.substring(0, 4).equals("WDCA")) || (wiobj.substring(0, 3).equals("BPR")) || (wiobj.substring(0, 3).equals("CAO")) || (wiobj.substring(0, 3).equals("CMO")) || (wiobj.substring(0, 4).equals("DDOO")) || (wiobj.substring(0, 3).equals("DPR")) || (wiobj.substring(0, 2).equals("DJ")) || (wiobj.substring(0, 1).equals("K")) || (wiobj.substring(0, 4).equals("WGRP")) || (wiobj.substring(0, 4).equals("WOC0")) || (wiobj.substring(0, 4).equals("WOCP")) || (wiobj.substring(0, 4).equals("COBO")) || (wiobj.substring(0, 4).equals("WPTX")) || (wiobj.substring(0, 4).equals("WPTP")) || (wiobj.substring(0, 4).equals("WFXL")) || (wiobj.substring(0, 4).equals("WPE0")) || (wiobj.substring(0, 3).equals("PEM")) || (wiobj.substring(0, 4).equals("WPO0")) || (wiobj.substring(0, 1).equals("O")) || (wiobj.substring(0, 4).equals("WEC0")) || (wiobj.substring(0, 4).equals("WVD0")) || (wiobj.substring(0, 4).equals("WCP0")) || (wiobj.substring(0, 4).equals("WBL0")) || (wiobj.substring(0, 4).equals("WSQ0")) || (wiobj.substring(0, 4).equals("HPAT")) || (wiobj.substring(0, 4).equals("WHE3")) || (wiobj.substring(0, 4).equals("WHE6")) || (wiobj.substring(0, 4).equals("WHS0")) || (wiobj.substring(0, 4).equals("WHF0")) || (wiobj.substring(0, 4).equals("WHM0")) || (wiobj.substring(0, 4).equals("WHA0")) || (wiobj.substring(0, 4).equals("WHJ0")) || (wiobj.substring(0, 4).equals("WHR0")) || (wiobj.substring(0, 5).equals("IIPT9")) || (wiobj.substring(0, 5).equals("BXPF0"))))
            {









































              wadd = "N";
            } else {
              wiobjold = wiobj;
            }
          }
          


          if (((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 4).equals("WHB0"))) || ((((String)ht.get("iobj")).length() > 4) && (((String)ht.get("iobj")).substring(0, 5).equals("HCEP1")))) {
            if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 4) && (((String)ht.get("iobj")).substring(0, 4).equals("WW00"))) {
            if ((MLogin.hasLoginProperty("DATAMART")) && (!MLogin.getLoginProperty("DATAMART").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 2) && (((String)ht.get("iobj")).substring(0, 2).equals("WZ")))
          {
            if ((MLogin.hasLoginProperty("EXTENSIONS")) && (!MLogin.getLoginProperty("EXTENSIONS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WHZ"))) {
            if ((MLogin.hasLoginProperty("EXTENSIONSRH")) && (!MLogin.getLoginProperty("EXTENSIONSRH").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WOP"))) {
            if ((MLogin.hasLoginProperty("PORTOS")) && (!MLogin.getLoginProperty("PORTOS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WTP"))) {
            if ((MLogin.hasLoginProperty("ETPS")) && (!MLogin.getLoginProperty("ETPS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          


          if (!wadd.equals("N")) {
            wv.add(ht);
          } else {
            wadd = "S";

          }
          

        }
        
      }
      

    }
    else if (MSerializedObject.existsObject(wconnect, "ENT", "SMEN", wori)) {
      wmb = (JMenuBar)MSerializedObject.getObject(wconnect, "ENT", "SMEN", wori);
      wmb.setBackground(new Color(215, 228, 244));
    }
    else {
      MCursor esnm_curs = MConnection.getMCursor(wconnect);
      try {
        esnm_curs.execQuery(wcsel);
        if (esnm_curs.getWdcode() == 0) {
          wv = esnm_curs.getAllRecords();
        }
      } finally {
        esnm_curs.close();
      }
    }
    

    if (wv != null) {
      wmb = new JMenuBar();
      wmb.setBackground(new Color(215, 228, 244));
      














      for (int i = 0; (wv != null) && (i < wv.size()); i++) {
        Hashtable xsment = (Hashtable)wv.elementAt(i);
        wadd = "S";
        if (((String)xsment.get("iobjt")).startsWith("M")) {
          wadd = (String)htHidden.get((String)xsment.get("imen") + ":" + (String)xsment.get("nopcao"));
          if (wadd == null) {
            wadd = "S";
          }
          
          if ((htTran != null) && (htTran.containsKey((String)xsment.get("nopcao")))) {
            wadd = "N";
            wmb.add(getMinMenu((String)xsment.get("iobj"), (String)htTran.get((String)xsment.get("nopcao")), false, wconnect));
          }
          
          if (wadd.equals("S"))
          {
            wmb.add(getMinMenu((String)xsment.get("iobj"), (String)xsment.get("nopcao"), false, wconnect));
          }
        }
      }
    }
    

    if ((MLogin.existsClientMetaFor("SMEN" + wori)) && (!MLogin.isSameAsMetaData()))
    {

      wv = new Vector();
      
      if (!MSerializedObject.existsObject(MSerializedObject.LOCALSERPATH, "OBJECT", "SMEN", wori)) {
        wconnect = "DEFAULT";
        MCursor esnm_curs2 = MConnection.getMCursor(wconnect);
        try
        {
          esnm_curs2.execQuery(wcsel);
          if (esnm_curs2.getWdcode() == 0) {
            if (wmb == null) {
              wmb = new JMenuBar();
              wmb.setBackground(new Color(215, 228, 244));
            }
            
            wv = esnm_curs2.getAllRecords();
          }
        } finally {
          esnm_curs2.close();
        }
      }
      














      for (int i = 0; (wv != null) && (i < wv.size()); i++) {
        Hashtable xsment = (Hashtable)wv.elementAt(i);
        if (((String)xsment.get("iobjt")).startsWith("M"))
        {
          wadd = "S";
          wadd = (String)htHidden.get((String)xsment.get("imen") + ":" + (String)xsment.get("nopcao"));
          if (wadd == null) {
            wadd = "S";
          }
          
          if (htTran.containsKey((String)xsment.get("nopcao"))) {
            wadd = "N";
            wmb.add(getMinMenu((String)xsment.get("iobj"), (String)htTran.get((String)xsment.get("nopcao")), false, wconnect));
          }
          
          if (wadd.equals("S")) {
            wmb.add(getMinMenu((String)xsment.get("iobj"), (String)xsment.get("nopcao"), false, wconnect));
          }
        }
      }
    }
    return wmb;
  }
  
  private static JMenu getMinMenu(String wori, String wname, boolean hasIcon, String wconnect) throws MError
  {
    String srest = null;String wversao = " ";String wlight = " ";String wpais = "PT";String wadd = "S";String wimen = " ";String wiobj = " ";String wiobjold = " ";String wexclude = " ";
    

    File fFile = null;
    java.io.BufferedReader brReader = null;
    StringBuffer sbBuffer = null;
    String sData = null;
    
    Hashtable htHidden = new Hashtable();
    Hashtable htLabels = new Hashtable();
    String sFile = null;String sLabel = null;String sLabelNew = null;
    
    String wwobj = "!";
    if (wori.trim().length() > 4) {
      wwobj = wori.substring(0, 5);
    }
    

    if (MLogin.hasLoginProperty("CLIDATALOC"))
    {

      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/hidden/" + "EEEE0" + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
      

      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/hidden/" + wwobj.substring(0, 5) + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
    }
    


    if (MLogin.hasLoginProperty("METADATALOC"))
    {

      sFile = MLogin.getLoginProperty("METADATALOC") + "/hidden/" + "EEEE0" + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
      

      sFile = MLogin.getLoginProperty("METADATALOC") + "/hidden/" + wwobj.substring(0, 5) + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
    }
    


    JMenu wMenu = new MJMenuPlus(wname);
    wMenu.setBackground(new Color(215, 228, 244));
    


    if (hasIcon) {
      wMenu.setIcon(new javax.swing.ImageIcon(MImages.getPreparedImage("ALLBLANK_mi")));
      wMenu.setHorizontalTextPosition(4);
    }
    











    wexclude = "";
    
    if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WHB0%' and iobj not like 'HCEP1%' ";
    }
    
    if ((MLogin.hasLoginProperty("DATAMART")) && (!MLogin.getLoginProperty("DATAMART").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WW00%' ";
    }
    
    if ((MLogin.hasLoginProperty("EXTENSIONS")) && (!MLogin.getLoginProperty("EXTENSIONS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WZ%' ";
    }
    
    if ((MLogin.hasLoginProperty("EXTENSIONSRH")) && (!MLogin.getLoginProperty("EXTENSIONSRH").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WHZ%' ";
    }
    
    if ((MLogin.hasLoginProperty("PORTOS")) && (!MLogin.getLoginProperty("PORTOS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WOP%' ";
    }
    
    if ((MLogin.hasLoginProperty("ETPS")) && (!MLogin.getLoginProperty("ETPS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WTP%' ";
    }
    

    String wcsel = " select imen, iobjt, iobj, xopcao, nopcao, tgif, targs from sment0 ";
    wcsel = wcsel + " where imen = '" + wori + "' " + wexclude;
    
    wcsel = wcsel + " order by imen, xopcao, nopcao";
    

    Vector wv = new Vector();
    
    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && (wconnect.equals("JMETADATA")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE"))))
    {



      if (MLogin.hasLoginProperty("VERSION")) {
        wversao = MLogin.getLoginProperty("VERSION");
        if ((wversao.trim().length() > 9) && (
          (wversao.substring(9, 10).equals("E")) || (wversao.substring(9, 10).equals("L")))) {
          wlight = "S";
        }
      }
      

      System.out.println("!*** MMenu getMinMenu wv em ficheiro: " + MLogin.getLoginProperty("METADATALOC") + "/smen/" + wori + "menu");
      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/smen/" + wori + "menu");
      










      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        String stot = xml.getTagValue("wkobj");
        srest = stot;
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          
          ht.put("imen", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iobjt", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iobj", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("xopcao", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("nopcao", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("tgif", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("targs", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          
          if (wlight.equals("S")) {
            wiobj = (String)ht.get("iobj");
            if (wiobj.trim().equals(wiobjold.trim())) {
              wadd = "N";
            }
            if ((wiobj.trim().length() > 4) && ((wiobj.substring(0, 4).equals("CRCT")) || (wiobj.substring(0, 4).equals("CCHV")) || (wiobj.substring(0, 4).equals("BPSS")) || (wiobj.substring(0, 4).equals("BPSC")) || (wiobj.substring(0, 4).equals("WDCC")) || (wiobj.substring(0, 4).equals("WDCA")) || (wiobj.substring(0, 3).equals("BPR")) || (wiobj.substring(0, 3).equals("CAO")) || (wiobj.substring(0, 3).equals("CMO")) || (wiobj.substring(0, 4).equals("DDOO")) || (wiobj.substring(0, 3).equals("DPR")) || (wiobj.substring(0, 2).equals("DJ")) || (wiobj.substring(0, 1).equals("K")) || (wiobj.substring(0, 4).equals("WGRP")) || (wiobj.substring(0, 4).equals("WOC0")) || (wiobj.substring(0, 4).equals("WOCP")) || (wiobj.substring(0, 4).equals("COBO")) || (wiobj.substring(0, 4).equals("WPTX")) || (wiobj.substring(0, 4).equals("WPTP")) || (wiobj.substring(0, 4).equals("WFXL")) || (wiobj.substring(0, 4).equals("WPE0")) || (wiobj.substring(0, 3).equals("PEM")) || (wiobj.substring(0, 4).equals("WPO0")) || (wiobj.substring(0, 1).equals("O")) || (wiobj.substring(0, 4).equals("WEC0")) || (wiobj.substring(0, 4).equals("WVD0")) || (wiobj.substring(0, 4).equals("WCP0")) || (wiobj.substring(0, 4).equals("WBL0")) || (wiobj.substring(0, 4).equals("WSQ0")) || (wiobj.substring(0, 4).equals("HPAT")) || (wiobj.substring(0, 4).equals("WHE3")) || (wiobj.substring(0, 4).equals("WHE6")) || (wiobj.substring(0, 4).equals("WHS0")) || (wiobj.substring(0, 4).equals("WHF0")) || (wiobj.substring(0, 4).equals("WHM0")) || (wiobj.substring(0, 4).equals("WHA0")) || (wiobj.substring(0, 4).equals("WHJ0")) || (wiobj.substring(0, 4).equals("WHR0")) || (wiobj.substring(0, 5).equals("IIPT9")) || (wiobj.substring(0, 5).equals("BXPF0"))))
            {









































              wadd = "N";
            } else {
              wiobjold = wiobj;
            }
          }
          


          if ((ht.get("iobj").toString().equals("DO_PROC_RM_CL")) || (ht.get("iobj").toString().equals("UNDO_PROC_RM_CL")) || (ht.get("iobj").toString().equals("JUMPBXPF1XTRN0")))
          {

            wadd = "N";
            if ((MLogin.hasLoginProperty("REMOTEPROC")) && ((MLogin.getLoginProperty("REMOTEPROC").startsWith("S")) || (MLogin.getLoginProperty("REMOTEPROC").startsWith("X"))))
            {
              wadd = "S";
            }
          }
          if ((wiobj.trim().length() > 4) && (wiobj.substring(0, 5).equals("BXPF1")) && ((ht.get("iobj").toString().equals("DO_PROC")) || (ht.get("iobj").toString().equals("UNDO_PROC"))))
          {
            if ((MLogin.hasLoginProperty("REMOTEPROC")) && (MLogin.getLoginProperty("REMOTEPROC").startsWith("S")))
            {
              wadd = "N";
            }
          }
          
          if (((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 4).equals("WHB0"))) || ((((String)ht.get("iobj")).length() > 4) && (((String)ht.get("iobj")).substring(0, 5).equals("HCEP1")))) {
            if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          
          if ((((String)ht.get("iobj")).length() > 4) && (((String)ht.get("iobj")).substring(0, 4).equals("WW00"))) {
            if ((MLogin.hasLoginProperty("DATAMART")) && (!MLogin.getLoginProperty("DATAMART").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 2) && (((String)ht.get("iobj")).substring(0, 2).equals("WZ")))
          {
            if ((MLogin.hasLoginProperty("EXTENSIONS")) && (!MLogin.getLoginProperty("EXTENSIONS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WHZ"))) {
            if ((MLogin.hasLoginProperty("EXTENSIONSRH")) && (!MLogin.getLoginProperty("EXTENSIONSRH").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WOP"))) {
            if ((MLogin.hasLoginProperty("PORTOS")) && (!MLogin.getLoginProperty("PORTOS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WTP"))) {
            if ((MLogin.hasLoginProperty("ETPS")) && (!MLogin.getLoginProperty("ETPS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if (!wadd.equals("N")) {
            wv.add(ht);
          } else {
            wadd = "S";
          }
          
        }
        
      }
      

    }
    else
    {
      MCursor esnm_curs = MConnection.getMCursor(wconnect);
      try {
        esnm_curs.execQuery(wcsel);
        wv = esnm_curs.getAllRecords();
      } finally {
        esnm_curs.closeStatement();
        esnm_curs.close();
      }
    }
    

    for (int i = 0; (wv != null) && (i < wv.size()); i++) {
      Hashtable xsment = (Hashtable)wv.elementAt(i);
      
      wadd = "S";
      

      wadd = (String)htHidden.get((String)xsment.get("imen") + ":" + (String)xsment.get("nopcao"));
      
      if (wadd == null) {
        wadd = "S";
      }
      

      wopcao = (String)xsment.get("nopcao");
      if ((htTran != null) && (htTran.containsKey(wopcao))) {
        wopcao = (String)htTran.get(wopcao);
      }
      
      if (wadd.equals("S"))
      {
        if (!((String)xsment.get("iobjt")).startsWith("-")) {
          if (((String)xsment.get("iobjt")).startsWith("M")) {
            wMenu.add(getMinMenu((String)xsment.get("iobj"), wopcao, true, wconnect));
          }
          


          if (((String)xsment.get("iobjt")).startsWith("X")) {
            wMenu.add(getMinMenuItem((String)xsment.get("iobj"), wopcao, (String)xsment.get("xopcao"), (String)xsment.get("tgif"), (String)xsment.get("targs")));
          }
        } else {
          wMenu.addSeparator();
        }
      }
    }
    return wMenu;
  }
  
  private static JMenuItem getMinMenuItem(String wori, String wname, String wtitle, String wtgif, String wargs) {
    MMenuItem witem = null;
    if (wtgif.equals("NONE")) {
      witem = new MMenuItem(wname);
    }
    if (!wtgif.equals("NONE")) {
      if (MImages.getPreparedImage(wtgif + "_mi") != null) {
        witem = new MMenuItem(wname, new javax.swing.ImageIcon(MImages.getPreparedImage(wtgif + "_mi")));
        witem.setHorizontalTextPosition(4);
      } else {
        witem = new MMenuItem(wname, new javax.swing.ImageIcon(MImages.getPreparedImage("BLANK_mi")));
        witem.setHorizontalTextPosition(4);
      }
    }
    if (!wargs.trim().equals("***")) {
      witem.setActionCommand(wori);
      witem.setCommandArgs(wargs);
    }
    witem.setBackground(new Color(215, 228, 244));
    return witem;
  }
  
  private static JMenu getMenu(String wori, String wname) throws MError
  {
    String srest = null;String wversao = " ";String wlight = " ";String wpais = "PT";String wadd = "S";String wimen = " ";String wiobj = " ";String wiobjold = " ";
    


    Hashtable htHidden = new Hashtable();
    Hashtable htLabels = new Hashtable();
    String sFile = null;String sLabel = null;String sLabelNew = null;
    
    if (MLogin.hasLoginProperty("CLIDATALOC")) {
      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/hidden/" + "EEEE0" + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
    }
    if (MLogin.hasLoginProperty("METADATALOC")) {
      sFile = MLogin.getLoginProperty("METADATALOC") + "/hidden/" + "EEEE0" + "hidden";
      MGetMData.getRunTimeAtribsHLL("HD", sFile, htHidden);
    }
    

    JMenu wMenu = new MJMenuPlus(wname);
    wMenu.setBackground(new Color(215, 228, 244));
    






    String wexclude = "";
    if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WHB0%' and iobj not like 'HCEP1%' ";
    }
    
    if ((MLogin.hasLoginProperty("DATAMART")) && (!MLogin.getLoginProperty("DATAMART").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WW00%' ";
    }
    
    if ((MLogin.hasLoginProperty("EXTENSIONS")) && (!MLogin.getLoginProperty("EXTENSIONS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WZ%' ";
    }
    
    if ((MLogin.hasLoginProperty("EXTENSIONSRH")) && (!MLogin.getLoginProperty("EXTENSIONSRH").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WHZ%' ";
    }
    
    if ((MLogin.hasLoginProperty("PORTOS")) && (!MLogin.getLoginProperty("PORTOS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WOP%' ";
    }
    if ((MLogin.hasLoginProperty("ETPS")) && (!MLogin.getLoginProperty("ETPS").startsWith("N")))
    {
      wexclude = wexclude + "";
    } else {
      wexclude = wexclude + " and iobj not like 'WTP%' ";
    }
    
    String wcsel = " select imen, iobjt, iobj, xopcao, nopcao, tgif, targs from sment0 ";
    wcsel = wcsel + " where imen = '" + wori + "' and iobjt <> '#T' " + wexclude;
    
    wcsel = wcsel + " order by imen, xopcao, nopcao";
    

    Vector wv = new Vector();
    

    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")))
    {


      if (MLogin.hasLoginProperty("VERSION")) {
        wversao = MLogin.getLoginProperty("VERSION");
        if ((wversao.trim().length() > 9) && (
          (wversao.substring(9, 10).equals("E")) || (wversao.substring(9, 10).equals("L")))) {
          wlight = "S";
        }
      }
      


      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/smen/" + wori + "menu");
      










      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        String stot = xml.getTagValue("wkobj");
        srest = stot;
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          String t_imen = srest.substring(0, srest.indexOf("<|>"));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          String t_iobjt = srest.substring(0, srest.indexOf("<|>"));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          String t_iobj = srest.substring(0, srest.indexOf("<|>"));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          String t_xopcao = srest.substring(0, srest.indexOf("<|>"));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          String t_nopcao = srest.substring(0, srest.indexOf("<|>"));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          String t_tgif = srest.substring(0, srest.indexOf("<|>"));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          String t_targs = srest.substring(0, srest.indexOf("<|>"));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          
          ht.put("iobjt", t_iobjt);
          ht.put("imen", t_imen);
          ht.put("iobj", t_iobj);
          ht.put("tgif", t_tgif);
          ht.put("nopcao", t_nopcao);
          ht.put("xopcao", t_xopcao);
          ht.put("targs", t_targs);
          
          if (wlight.equals("S")) {
            wiobj = (String)ht.get("iobj");
            if (wiobj.trim().equals(wiobjold.trim())) {
              wadd = "N";
            }
            if ((wiobj.trim().length() > 4) && ((wiobj.substring(0, 4).equals("CRCT")) || (wiobj.substring(0, 4).equals("CCHV")) || (wiobj.substring(0, 4).equals("BPSS")) || (wiobj.substring(0, 4).equals("BPSC")) || (wiobj.substring(0, 4).equals("WDCC")) || (wiobj.substring(0, 4).equals("WDCA")) || (wiobj.substring(0, 3).equals("BPR")) || (wiobj.substring(0, 3).equals("CAO")) || (wiobj.substring(0, 3).equals("CMO")) || (wiobj.substring(0, 4).equals("DDOO")) || (wiobj.substring(0, 3).equals("DPR")) || (wiobj.substring(0, 2).equals("DJ")) || (wiobj.substring(0, 1).equals("K")) || (wiobj.substring(0, 4).equals("WGRP")) || (wiobj.substring(0, 4).equals("WOC0")) || (wiobj.substring(0, 4).equals("WOCP")) || (wiobj.substring(0, 4).equals("COBO")) || (wiobj.substring(0, 4).equals("WPTX")) || (wiobj.substring(0, 4).equals("WPTP")) || (wiobj.substring(0, 4).equals("WFXL")) || (wiobj.substring(0, 4).equals("WPE0")) || (wiobj.substring(0, 3).equals("PEM")) || (wiobj.substring(0, 4).equals("WPO0")) || (wiobj.substring(0, 1).equals("O")) || (wiobj.substring(0, 4).equals("WEC0")) || (wiobj.substring(0, 4).equals("WVD0")) || (wiobj.substring(0, 4).equals("WCP0")) || (wiobj.substring(0, 4).equals("WBL0")) || (wiobj.substring(0, 4).equals("WSQ0")) || (wiobj.substring(0, 4).equals("HPAT")) || (wiobj.substring(0, 4).equals("WHE3")) || (wiobj.substring(0, 4).equals("WHE6")) || (wiobj.substring(0, 4).equals("WHS0")) || (wiobj.substring(0, 4).equals("WHF0")) || (wiobj.substring(0, 4).equals("WHM0")) || (wiobj.substring(0, 4).equals("WHA0")) || (wiobj.substring(0, 4).equals("WHJ0")) || (wiobj.substring(0, 4).equals("WHR0")) || (wiobj.substring(0, 5).equals("IIPT9")) || (wiobj.substring(0, 5).equals("BXPF0"))))
            {









































              wadd = "N";
            } else {
              wiobjold = wiobj;
            }
          }
          

          if (((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 4).equals("WHB0"))) || ((((String)ht.get("iobj")).length() > 4) && (((String)ht.get("iobj")).substring(0, 5).equals("HCEP1")))) {
            if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          
          if ((((String)ht.get("iobj")).length() > 4) && (((String)ht.get("iobj")).substring(0, 4).equals("WW00"))) {
            if ((MLogin.hasLoginProperty("DATAMART")) && (!MLogin.getLoginProperty("DATAMART").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 2) && (((String)ht.get("iobj")).substring(0, 2).equals("WZ")))
          {
            if ((MLogin.hasLoginProperty("EXTENSIONS")) && (!MLogin.getLoginProperty("EXTENSIONS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WHZ"))) {
            if ((MLogin.hasLoginProperty("EXTENSIONSRH")) && (!MLogin.getLoginProperty("EXTENSIONSRH").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WOP"))) {
            if ((MLogin.hasLoginProperty("PORTOS")) && (!MLogin.getLoginProperty("PORTOS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          

          if ((((String)ht.get("iobj")).length() > 3) && (((String)ht.get("iobj")).substring(0, 3).equals("WTP"))) {
            if ((MLogin.hasLoginProperty("ETPS")) && (!MLogin.getLoginProperty("ETPS").startsWith("N")))
            {
              wadd = "S";
            } else {
              wadd = "N";
            }
          }
          


          if (!wadd.equals("N")) {
            wv.add(ht);
          } else {
            wadd = "S";
          }
          
        }
        
      }
      

    }
    else
    {
      MCursor esnm_curs = MConnection.getMCursor();
      try {
        esnm_curs.execQuery(wcsel);
        wv = esnm_curs.getAllRecords();
      } finally {
        esnm_curs.closeStatement();
        esnm_curs.close();
      }
    }
    

    String wsep = "N";
    for (int i = 0; (wv != null) && (i < wv.size()); i++) {
      Hashtable xsment = (Hashtable)wv.elementAt(i);
      
      wadd = (String)htHidden.get((String)xsment.get("imen") + ":" + (String)xsment.get("nopcao"));
      
      if (wadd == null) {
        wadd = "S";
      }
      
      wopcao = (String)xsment.get("nopcao");
      wopcao2 = (String)xsment.get("targs");
      if (htTran.containsKey(wopcao)) {
        wopcao = (String)htTran.get(wopcao);
      }
      if (htTran.containsKey(wopcao2)) {
        wopcao2 = (String)htTran.get(wopcao2);
      }
      

      if (wadd.equals("S"))
      {
        if (!((String)xsment.get("iobjt")).startsWith("-")) {
          if (((String)xsment.get("iobjt")).startsWith("M")) {
            wMenu.add(getMenu((String)xsment.get("iobj"), (String)xsment.get("xopcao") + ". " + wopcao));
          }
          if (((String)xsment.get("iobjt")).equals("T")) {
            if ((!((String)xsment.get("xopcao")).equals("0")) && 
              (wsep.equals("N"))) {
              wMenu.addSeparator();
            }
            
            wMenu.add(getMenuItem((String)xsment.get("iobj"), "" + wopcao2.trim().toUpperCase(), " *** ", true, " *** "));
          }
          wsep = "N";
          if (((String)xsment.get("iobjt")).startsWith("J")) {
            wMenu.add(getMenuItem((String)xsment.get("iobj"), (String)xsment.get("xopcao") + ". " + wopcao, wopcao, true, (String)xsment.get("targs")));
          }
          if (((String)xsment.get("iobjt")).startsWith("K")) {
            wMenu.add(getMenuItem((String)xsment.get("iobj"), (String)xsment.get("xopcao") + ". " + wopcao, wopcao, false, (String)xsment.get("targs")));
          }
          if (((String)xsment.get("iobjt")).startsWith("X")) {
            wMenu.add(getMinMenuItem((String)xsment.get("iobj"), (String)xsment.get("xopcao") + ". " + wopcao, wopcao, (String)xsment.get("tgif"), (String)xsment.get("targs")));
          }
        } else {
          if (wsep.equals("N")) {
            wMenu.addSeparator();
          }
          wsep = "S";
        }
      }
    }
    
    return wMenu;
  }
  
  private static MMenuItem getMenuItem(String wori, String wname, String wtitle, boolean checkTriggers, String wargs) {
    MMenuItem witem = new MMenuItem(wname);
    witem.setBackground(new Color(215, 228, 244));
    if (checkTriggers) {
      if (wori.trim().length() > 4) {
        witem.setActionCommand("RUN " + wori.substring(0, 5) + " " + wtitle);
        witem.setCommandArgs(wargs);
      }
    } else {
      witem.setActionCommand("MAPRUN " + wori.trim() + " " + wtitle);
      witem.setCommandArgs(wargs);
    }
    return witem;
  }
  
  public MForm getMForm()
  {
    return minForm;
  }
  
  public Hashtable getTran() {
    return htTran;
  }
  
  public static void main(String[] args) {
    System.out.println("!*** MMenu Main Inicio: " + new java.util.Date());
    


    try
    {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      
      UIManager.put("Menu.font", new Font("Dialog", 0, 12));
      UIManager.put("MenuItem.font", new Font("Dialog", 0, 12));
      UIManager.put("Button.font", new Font("Dialog", 0, 12));
      UIManager.put("Button.select", new Color(255, 255, 192));
      UIManager.put("ToggleButton.select", new Color(255, 255, 192));
      

      UIManager.put("Label.font", new Font("Dialog", 0, 12));
      UIManager.put("CheckBox.font", new Font("Dialog", 0, 12));
      UIManager.put("RadioButton.font", new Font("Dialog", 0, 12));
      UIManager.put("Panel.font", new Font("Dialog", 0, 12));
      UIManager.put("TabbedPane.font", new Font("Dialog", 0, 12));
      
      UIManager.put("TabbedPane.selected", new Color(204, 204, 255));
      UIManager.put("CheckBoxMenuItem.font", new Font("Dialog", 0, 12));
      UIManager.put("ComboBox.background", new Color(255, 255, 255));
      

      UIManager.put("Label.select", new Color(0, 255, 0));
      
      UIManager.put("PaneText.font", new Font("Dialog", 0, 12));
      UIManager.put("Dialog.font", new Font("Dialog", 0, 12));
      


      UIManager.put("OptionPane.font", new Font("Dialog", 0, 12));
      UIManager.put("OptionPane.background", new Color(255, 255, 192));
      UIManager.put("Panel.background", new Color(255, 255, 192));
      
      MImages wimg = new MImages();
      wimg.getImg(" ");
      MinMain wmmain; if ((args.length != 0) && (args[0].trim() != "")) {
        if (!args[0].startsWith("-")) {
          System.out.println("!*** MMenu Main ASSYNC: " + args[0]);
          MMenu wmain = new MMenu(args[0]);
          wmain.setBackground(new Color(215, 228, 244));
        } else {
          PT.Minimal.geral.console.MConsole.start();
          System.out.println("!*** MMenu Main: " + args[0] + "/" + args[1]);
          String went = args[0].substring(0, 6);
          String wparms = args[1].trim();
          if (args.length > 2) {
            wparms = wparms + " " + args[2].trim();
          }
          if (args.length > 3) {
            wparms = wparms + " " + args[3].trim();
          }
          
          if (args.length > 4) {
            wparms = wparms + " " + args[4].trim();
          }
          
          if (args.length > 5) {
            wparms = wparms + " " + args[5].trim();
          }
          
          if (args.length > 6) {
            wparms = wparms + " " + args[6].trim();
          }
          
          wmmain = new MinMain(went, wparms);
        }
      } else {
        MMenu wmain = new MMenu("J000A");
        wmain.setBackground(new Color(215, 228, 244));
      }
    }
    catch (MError err)
    {
      System.exit(0);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(0);
    }
    System.out.println("!*** MMenu Main Fim - " + new java.util.Date());
  }
}
