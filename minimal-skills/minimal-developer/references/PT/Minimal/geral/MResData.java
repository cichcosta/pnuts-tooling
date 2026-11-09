package PT.Minimal.geral;

import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JToggleButton;

public class MResData
{
  private String Ent = null;
  private String EntName = null;
  private Vector LMenu = new Vector();
  private Vector LMenuItem = new Vector();
  private static Hashtable LPanelComponents = new Hashtable();
  private Hashtable LPanel = new Hashtable();
  private Hashtable LToolBar = new Hashtable();
  private Hashtable LToolBarItem = new Hashtable();
  private MImages wimgs;
  private Vector LExp = new Vector();
  private Vector LCmd = new Vector();
  private static Hashtable hCritExt = new Hashtable();
  private static Hashtable hQueries = new Hashtable();
  private static String sPromptForm = "";
  private static String sQueryTerc = "";
  
  public static final int TEXTFIELD = 0;
  public static final int LABEL = 1;
  public static final int BUTTON = 2;
  public static final int COLUMN = 3;
  public static final String LEFT = "Left";
  public static final String RIGHT = "Right";
  public static final String CENTER = "Center";
  public static final String MLAYOUT = "MLayout";
  public static final String LIST = "List";
  public static final String NORMAL = "Normal";
  public static final String TEXT = "Text";
  public static final String TEXTBOT = "TextBot";
  public static final String BOTH = "Both";
  private javax.swing.ButtonGroup wcurrencygroup;
  
  public MResData()
  {
    wimgs = new MImages();
  }
  
  public void setEnt(String ws) { Ent = ws; }
  
  public String getEnt() {
    return Ent;
  }
  
  public void setEntName(String ws) { EntName = ws; }
  
  public String getEntName() {
    return EntName;
  }
  
  public static void setQueryTerc(String ws) { sQueryTerc = ws; }
  
  public static String getQueryTerc() {
    return sQueryTerc;
  }
  
  public void addMenu(String ws) { LMenu.addElement(ws); }
  
  public MExp addExp(String wiob, String wtexp, String watrib)
  {
    MExp wexp = new MExp(wiob, wtexp, watrib);
    LExp.addElement(wexp);
    return wexp;
  }
  
  public MCommand addCmd(String iob, String cmd) {
    MCommand wcmd = new MCommand(iob, cmd);
    LCmd.addElement(wcmd);
    return wcmd;
  }
  
  public Vector getExps() {
    return LExp;
  }
  
  public Vector getCmds() {
    return LCmd;
  }
  
  public void addMenuItem(String wmenu, String wlabel, String wcmd) {
    String[] ws = new String[3];
    ws[0] = wmenu;
    ws[1] = wlabel;
    ws[2] = wcmd;
    LMenuItem.addElement(ws);
  }
  
  public void addMenuSeparator(String wmenu) {
    String[] ws = new String[3];
    ws[0] = wmenu;
    ws[1] = "-";
    ws[2] = null;
    LMenuItem.addElement(ws);
  }
  
  public static Hashtable getPanelComponents() {
    return LPanelComponents;
  }
  
  public Vector getPanelComponents(String ws) {
    return (Vector)LPanelComponents.get(ws);
  }
  
  public void setPanelComponents(String ws, Vector wv) {
    if (LPanelComponents.contains(ws)) {
      LPanelComponents.remove(ws);
    }
    LPanelComponents.put(ws, wv);
  }
  
  public javax.swing.JMenuBar getMenuBar()
  {
    javax.swing.JMenuBar wmb = new javax.swing.JMenuBar();
    
    for (int j = 0; j < LMenu.size(); j++) {
      String ws = (String)LMenu.elementAt(j);
      javax.swing.JMenu wm = new javax.swing.JMenu(ws);
      
      for (int i = 0; i < LMenuItem.size(); i++) {
        String[] witem = (String[])LMenuItem.elementAt(i);
        if (witem[0].equals(ws)) {
          if (witem[1].equals("-")) {
            wm.addSeparator();
          } else {
            javax.swing.JMenuItem wmi = new javax.swing.JMenuItem(witem[1]);
            
            wmi.setActionCommand(witem[2]);
            wm.add(wmi);
          }
        }
      }
      wmb.add(wm);
    }
    return wmb;
  }
  
  public javax.swing.JMenuBar getJMenuBar() {
    javax.swing.JMenuBar wmb = new javax.swing.JMenuBar();
    
    for (int j = 0; j < LMenu.size(); j++) {
      String ws = (String)LMenu.elementAt(j);
      javax.swing.JMenu wm = new javax.swing.JMenu(ws);
      
      for (int i = 0; i < LMenuItem.size(); i++) {
        String[] witem = (String[])LMenuItem.elementAt(i);
        if (witem[0].equals(ws)) {
          if (witem[1].equals("-")) {
            wm.addSeparator();
          } else {
            javax.swing.JMenuItem wmi;
            if (wimgs.getImg(witem[1] + "_mi") != null) {
              javax.swing.JMenuItem wmi = new javax.swing.JMenuItem(witem[1], new javax.swing.ImageIcon(wimgs.getImg(witem[1] + "_mi")));
              
              wmi.setHorizontalTextPosition(4);
            } else {
              wmi = new javax.swing.JMenuItem(witem[1], new javax.swing.ImageIcon(wimgs.getImg("BLANK_mi")));
              
              wmi.setHorizontalTextPosition(4);
            }
            
            wmi.setActionCommand(witem[2]);
            wm.add(wmi);
          }
        }
      }
      wmb.add(wm);
    }
    return wmb;
  }
  
  public void addToolBar(String wtb, String wtype) {
    LToolBar.put(wtb, wtype);
    LToolBarItem.put(wtb, new Vector());
  }
  
  public void addToolBarItem(String wtb, String wlabel, String wcmd) { String[] ws = new String[4];
    ws[0] = wtb;
    ws[1] = wlabel;
    ws[2] = wcmd;
    ws[3] = wlabel;
    
    ((Vector)LToolBarItem.get(wtb)).addElement(ws);
  }
  
  public void addToolBarItem(String wtb, String wlabel, String wcmd, String wtgif) { String[] ws = new String[4];
    ws[0] = wtb;
    ws[1] = wlabel;
    ws[2] = wcmd;
    ws[3] = wtgif;
    
    ((Vector)LToolBarItem.get(wtb)).addElement(ws);
  }
  














  public MToolBar getToolBar(String ws)
  {
    MToolBar wtb = new MToolBar();
    
    Vector wv = (Vector)LToolBarItem.get(ws);
    for (int i = 0; i < wv.size(); i++) {
      String[] witem = (String[])wv.elementAt(i);
      if (!witem[1].startsWith("-")) {
        wtb.addPushButton(witem[1], witem[2], true, wimgs.getImg(witem[3] + "_on"), wimgs.getImg(witem[1] + "_on"), wimgs.getImg(witem[1] + "_off"));
      } else {
        wtb.addSeparator();
      }
    }
    return wtb;
  }
  
  public MToolBar getCurrencyToolBar(int wno_currencies) {
    wcurrencygroup = new javax.swing.ButtonGroup();
    MToolBar wtb = new MToolBar();
    


    String wlabel1 = "Escudos (PTE)";String wlabel2 = "Euros (EUR)";
    
    if (wno_currencies == 3) { String witem;
      if ((MLogin.getDBProperty("MINMOEDA1") != null) && (MLogin.getDBProperty("MINMOEDA1") != "PTE") && (MLogin.getDBProperty("MINMOEDA1") != "EUR")) {
        witem = "TRN";
      } else {
        witem = "CMD";
      }
      JToggleButton wjtb = wtb.addToggleButton(witem, "CHANGE_TO_" + witem, true, wimgs.getImg(witem + "_on"), wimgs.getImg(witem + "_on"), wimgs.getImg(witem + "_off"));
      wjtb.setToolTipText("Moeda de Transação");
      wjtb.setSelected(true);
      if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
      {
        wjtb.setBackground(new java.awt.Color(158, 190, 231));
      } else {
        wjtb.setBackground(new java.awt.Color(215, 228, 244));
      }
      wcurrencygroup.add(wjtb);
      String witem = "PTE";
      String witemi = witem;
      if (MLogin.getDBProperty("MINMOEDA1") != null) {
        witemi = MLogin.getDBProperty("MINMOEDA1");
        if ((!witemi.equals("PTE")) && (!witemi.equals("EUR"))) {
          witem = witemi;wlabel1 = witemi;
        }
        if (witemi.equals("EUR")) {
          wlabel1 = "EUR";
        }
      }
      wjtb = wtb.addToggleButton(witem, "CHANGE_TO_" + witem, true, wimgs.getImg(witemi + "_on"), wimgs.getImg(witem + "_on"), wimgs.getImg(witem + "_off"));
      if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
      {
        wjtb.setBackground(new java.awt.Color(158, 190, 231));
      } else {
        wjtb.setBackground(new java.awt.Color(215, 228, 244));
      }
      wcurrencygroup.add(wjtb);
      wjtb.setToolTipText(wlabel1);
    }
    if (wno_currencies == 2) {
      String witem = "PTE";
      String witemi = witem;
      if (MLogin.getDBProperty("MINMOEDA1") != null) {
        witemi = MLogin.getDBProperty("MINMOEDA1");
        if ((!witemi.equals("PTE")) && (!witemi.equals("EUR"))) {
          witem = witemi;wlabel1 = witemi;
        }
        if (witemi.equals("EUR")) {
          wlabel1 = "EUR";
        }
      }
      JToggleButton wjtb = wtb.addToggleButton(witem, "CHANGE_TO_" + witem, true, wimgs.getImg(witem + "_on"), wimgs.getImg(witem + "_on"), wimgs.getImg(witem + "_off"));
      if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
      {
        wjtb.setBackground(new java.awt.Color(158, 190, 231));
      } else {
        wjtb.setBackground(new java.awt.Color(215, 228, 244));
      }
      wcurrencygroup.add(wjtb);
      wjtb.setToolTipText(wlabel1);
      wjtb.setSelected(true);
    }
    String witem = "EUR";
    String witemi = witem;
    if (MLogin.getDBProperty("MINMOEDA2") != null) {
      witemi = MLogin.getDBProperty("MINMOEDA2");
      if ((!witemi.equals("PTE")) && (!witemi.equals("EUR"))) {
        witem = witemi;wlabel2 = witemi;
      }
      if (witemi.equals("PTE")) {
        wlabel2 = "PTE";
      }
    }
    JToggleButton wjtb = wtb.addToggleButton(witem, "CHANGE_TO_" + witem, true, wimgs.getImg(witemi + "_on"), wimgs.getImg(witem + "_on"), wimgs.getImg(witem + "_off"));
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      wjtb.setBackground(new java.awt.Color(158, 190, 231));
    } else {
      wjtb.setBackground(new java.awt.Color(215, 228, 244));
    }
    wcurrencygroup.add(wjtb);
    wjtb.setToolTipText(wlabel2);
    return wtb;
  }
  

  public javax.swing.ButtonGroup getCurrencyButtonGroup()
  {
    return wcurrencygroup;
  }
  
  public static String getCritExt(String sIOb) {
    if (hCritExt.containsKey(sIOb)) {
      return (String)hCritExt.get(sIOb);
    }
    return "";
  }
  
  public static void setCritExt(String sIOb, String sWhere) {
    hCritExt.put(sIOb, sWhere);
  }
  
  public static String getQueries(String sIOb) {
    if (hQueries.containsKey(sIOb)) {
      return (String)hQueries.get(sIOb);
    }
    return "";
  }
  
  public static void setQueries(String sIOb, String sQuery) {
    hQueries.put(sIOb, sQuery);
  }
  
  public static String getPromptForm() {
    return sPromptForm;
  }
  
  public static void setPromptForm(String s) { sPromptForm = s; }
  

  public void addPanel(String wpanel, String wtype)
  {
    LPanel.put(wpanel, wtype);
    LPanelComponents.put(wpanel, new Vector());
  }
  
  public Object addPanelComponent(String wpanel, int wtype, String wmatei, String wlabel, String wcmd, int wqc, boolean wbreakln) {
    if (wtype == 0) {
      ((Vector)LPanelComponents.get(wpanel)).addElement(new MTextField(wmatei, wqc, wbreakln));
    }
    if (wtype == 1) {
      ((Vector)LPanelComponents.get(wpanel)).addElement(new MLabel(wlabel, wbreakln, wqc));
    }
    if (wtype == 2) {
      ((Vector)LPanelComponents.get(wpanel)).addElement(new MButton(wmatei, wlabel, wcmd));
    }
    if (wtype == 3) {
      wlabel = MUtil.stringRep(wlabel, "\\n", "\n");
      ((Vector)LPanelComponents.get(wpanel)).addElement(new MBrowseColumn(wmatei, wlabel, ((Vector)LPanelComponents.get(wpanel)).size()));
    }
    return ((Vector)LPanelComponents.get(wpanel)).lastElement();
  }
  
  public Hashtable getAllPanels() {
    Hashtable wh = new Hashtable();
    for (java.util.Enumeration e = LPanel.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      wh.put(wkey, getPanel(wkey));
    }
    for (java.util.Enumeration e = LToolBar.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      wh.put(wkey, getToolBar(wkey));
    }
    return wh;
  }
  
  public MPanel getPanel(String ws)
  {
    MPanel wp = new MPanel();
    if ((String)LPanel.get(ws) == "MLayout") {
      wp.setLayout(new MLayoutManager());
      Vector wv = (Vector)LPanelComponents.get(ws);
      for (int i = 0; i < wv.size(); i++)
      {
        wp.add((java.awt.Component)wv.elementAt(i));
      }
    }
    if ((String)LPanel.get(ws) == "Right") {
      wp.setLayout(new java.awt.FlowLayout(2));
      Vector wv = (Vector)LPanelComponents.get(ws);
      for (int i = 0; i < wv.size(); i++) {
        wp.add((java.awt.Component)wv.elementAt(i));
      }
    }
    if ((String)LPanel.get(ws) == "Left") {
      wp.setLayout(new java.awt.FlowLayout(0));
      Vector wv = (Vector)LPanelComponents.get(ws);
      for (int i = 0; i < wv.size(); i++) {
        wp.add((java.awt.Component)wv.elementAt(i));
      }
    }
    if ((String)LPanel.get(ws) == "List") {
      wp.setLayout(new java.awt.BorderLayout());
      MBrowse wbr = new MBrowse();
      wbr.setEnt(getEnt());
      wbr.setType("BROWSE");
      wbr.constrBrowse((Vector)LPanelComponents.get(ws));
      wp.add("Center", wbr);
    }
    
    return wp;
  }
}
