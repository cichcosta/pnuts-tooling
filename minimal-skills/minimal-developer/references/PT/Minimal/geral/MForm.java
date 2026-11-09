package PT.Minimal.geral;

import com.objectshare.grid.data.JKTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.TextComponent;
import java.awt.Window;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

public class MForm extends MPanel implements pnuts.lang.Property
{
  public static final String MULTIROW = "MULTIROW";
  public static final String SINGLEROW = "SINGLEROW";
  public static final int B_CANCEL = 1;
  public static final int B_OK = 2;
  private Container formParent;
  private Component theComponent = null;
  public static Hashtable LForm;
  public static Hashtable LCmd;
  private static Hashtable PanelList = null;
  
  public MActionListener minAction;
  
  public MItemListener minItemListener;
  
  public MMouseListener minMouse;
  
  public MFocusListener minFocus;
  public MKeyListener minKListener;
  public MWindowListener minWListener;
  public MBrowse minBrowse;
  public Container lastContainer;
  private String lastCmd;
  private String action = " ";
  private String actionArgs = " ";
  private int actionIndex = 0;
  private int fase = 0;
  
  private Object wactionObject;
  private String Ent;
  private String wfname;
  private String wftype = "SINGLEROW";
  
  private MRow minRow = null;
  
  private Hashtable critRecord;
  private Hashtable formRecord = new Hashtable();
  
  private JTabbedPane minTabbedPane;
  
  private MButton OKButton;
  private MButton ENDButton;
  private ButtonGroup wcurrencygroup;
  private String wcurrency = "m";
  

  private String EntName = null;
  private Dimension minDim = new Dimension(0, 30);
  
  public MPanel GreaterPanel = new MPanel();
  public MPanel GreatPanel = new MPanel();
  public Container MenuPanel = null;
  public Container ToolPanel = null;
  public MPanel FormPanel = new MPanel();
  public Container TopPanel = null;
  public Container CenterPanel = null;
  public Container BotPanel = null;
  
  public Container StatusPanel = null;
  public boolean statusShow = false;
  
  public Vector wrows;
  
  protected JKTableModel myModel;
  

  public MForm(String fname)
  {
    this(fname, null);
    
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(190, 213, 239));
    } else {
      setBackground(new Color(236, 233, 216));
    }
  }
  

  public MForm(String fname, String entname)
  {
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(190, 213, 239));
    } else {
      setBackground(new Color(236, 233, 216));
    }
    wfname = fname;
    setEnt(entname);
    










    minFocus = new MFocusListener(this);
    minAction = new MActionListener(this);
    minMouse = new MMouseListener(this);
    minWListener = new MWindowListener(this);
    minKListener = new MKeyListener(this);
    minItemListener = new MItemListener(this);
    GreaterPanel.setLayout(new BorderLayout());
    GreatPanel.setLayout(new BorderLayout());
    FormPanel.setLayout(new BorderLayout());
    StatusPanel = new MStatusBar(fname);
    


    if (LForm == null) {
      LForm = new Hashtable();
    }
    LForm.put(fname, this);
  }
  
  public static MForm getMForm(String fname)
  {
    if (LForm != null) {
      return (MForm)LForm.get(fname);
    }
    return null;
  }
  
  public static void setMForm(String fname, MForm wmf)
  {
    LForm.remove(fname);
    LForm.put(fname, wmf);
  }
  

  public static void removeMForm(String fname)
  {
    if ((LForm != null) && 
      (LForm.containsKey(fname))) {
      LForm.remove(fname);
    }
  }
  

  public static void closeAllForms()
  {
    Runtime.getRuntime().gc();
    
    Enumeration e;
    if (LForm != null) {
      for (e = LForm.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        MForm wform = (MForm)LForm.get(wkey);
        wform.closeForm();
      }
    }
    Runtime.getRuntime().gc();
  }
  

  public static void closeForms(String fname)
  {
    Enumeration e;
    
    if (LForm != null)
      for (e = LForm.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if (wkey.startsWith(fname)) {
          MForm wform = (MForm)LForm.get(wkey);
          wform.closeForm();
          LForm.remove(fname);
        }
      }
  }
  
  public static void removeForm(String fname) {
    Enumeration e;
    if (LForm != null) {
      for (e = LForm.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        MForm wform = (MForm)LForm.get(wkey);
        if (wform.getName().equals(fname)) {
          wform.closeForm();
        }
      }
    }
    LForm.remove(fname);
  }
  
  public void setBusy(boolean busy)
  {
    getMFrame().setBusy(busy);
  }
  
  public void closeForm()
  {
    if ((getEnt() != null) && (getEnt().startsWith("Menu"))) {
      return;
    }
    removeFocusListener(minFocus);
    
    hideForm();
    if ((minBrowse != null) && (getAction() != null) && (
      (getAction().equals("BROWSE_ROW")) || (getAction().equals("PROMPT_ROW"))))
    {
      minBrowse.removeAll();
      minBrowse.disconnect();
    }
    
    removeAll();
    Hashtable wh = getPanelList();
    Enumeration e; if (wh != null) {
      for (e = wh.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        
        if ((getName() != null) && (wkey != null) && (wkey.trim().length() != 0) && (wkey.startsWith(getName()))) {
          ((Container)wh.get(wkey)).removeAll();
          wh.remove(wkey);
        }
      }
    }
    
    removeCmds(getName());
    
    minFocus.close();
    minAction.close();
    minMouse.close();
    minWListener.close();
    minKListener.close();
    minItemListener.close();
    
    minFocus = null;
    minAction = null;
    minMouse = null;
    minWListener = null;
    minKListener = null;
    minItemListener = null;
    
    wh = null;
    LForm.remove(getName());
    Component wc = getFormParent();
    if (wc != null) {
      if ((wc instanceof MFrame)) {
        ((MFrame)wc).getContentPane().removeAll();
        ((MFrame)wc).removeAll();
        ((MFrame)wc).dispose();
      }
      if ((wc instanceof MDialog)) {
        ((MDialog)wc).getContentPane().removeAll();
        ((MDialog)wc).removeAll();
        ((MDialog)wc).dispose();
      }
    }
  }
  
  public static void addCmds(String wiob, Vector wvcmds)
  {
    if (LCmd == null) {
      LCmd = new Hashtable();
    }
    LCmd.put(wiob, wvcmds);
  }
  









  public static void removeCmds(String wiob)
  {
    if ((LCmd != null) && (wiob != null) && 
      (LCmd.containsKey(wiob))) {
      LCmd.remove(wiob);
    }
  }
  

  public static MCommand getCmd(String wiob, String cmd)
  {
    MCommand wcmd = null;
    Vector wv = null;
    if ((LCmd != null) && (wiob != null) && 
      (LCmd.containsKey(wiob))) {
      wv = (Vector)LCmd.get(wiob);
      for (int j = 0; j < wv.size(); j++) {
        if (((MCommand)wv.elementAt(j)).getName().equals(cmd)) {
          wcmd = (MCommand)wv.elementAt(j);
          break;
        }
        if (((MCommand)wv.elementAt(j)).getPrefText().toUpperCase().equals(cmd)) {
          wcmd = (MCommand)wv.elementAt(j);
          break;
        }
      }
    }
    
    return wcmd;
  }
  



  public static void addExps(String wiob, Hashtable wvexps)
    throws MError
  {
    MMData.addExps(wiob, wvexps);
  }
  
  public static void reloadExps(String wiob) throws MError
  {
    MMData.reloadExps(wiob);
  }
  
  public static MExp[] getExp(String wiob, String watrib, String wtexp) throws MError
  {
    return MMData.getExp(wiob, watrib, wtexp);
  }
  
  public static void addPreparedExp(String wiob, String watrib, String wtexp, MExp[] wexps) throws MError
  {
    MMData.addExps(wiob, watrib, wtexp, wexps);
  }
  



















  public void prepareForm()
  {
    if (TopPanel != null) {
      FormPanel.add(TopPanel, "North");
    }
    if (CenterPanel != null) {
      FormPanel.add(CenterPanel, "Center");
    }
    if (BotPanel != null) {
      FormPanel.add(BotPanel, "South");
    }
    if (ToolPanel != null) {
      ((MPanel)ToolPanel).setBorder(BorderFactory.createEtchedBorder());
      GreatPanel.add(ToolPanel, "North");
    }
    GreatPanel.add(FormPanel, "Center");
    if ((StatusPanel != null) && (statusShow)) {
      ((MPanel)StatusPanel).setBorder(BorderFactory.createEtchedBorder());
      
      GreatPanel.add(StatusPanel, "South");
    }
    if (ToolPanel != null) {
      GreatPanel.add(ToolPanel, "North");
    }
    if (MenuPanel != null) {
      GreaterPanel.add(MenuPanel, "North");
    }
    GreaterPanel.add(GreatPanel, "Center");
    
    setLayout(new BorderLayout());
    add(GreaterPanel, "Center");
    addFocusListener(minFocus);
    
    setCursor(java.awt.Cursor.getPredefinedCursor(0));
  }
  


  public void setTopPanel(Container wp)
  {
    if (TopPanel != null) {
      FormPanel.remove(TopPanel);
    }
    TopPanel = wp;
    
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      TopPanel.setBackground(new Color(215, 228, 244));
    } else {
      TopPanel.setBackground(new Color(236, 233, 216));
    }
    if (TopPanel != null) {
      Component[] wcomponents = TopPanel.getComponents();
      for (int j = 0; j < wcomponents.length; j++) {
        wcomponents[j].addMouseListener(minMouse);
        wcomponents[j].addFocusListener(minFocus);
        wcomponents[j].addKeyListener(minKListener);
        if ((wcomponents[j] instanceof MButton)) {
          ((MButton)wcomponents[j]).addActionListener(minAction);
        }
        if ((wcomponents[j] instanceof MTextField)) {
          ((MTextField)wcomponents[j]).addActionListener(minAction);
        }
        if (!(wcomponents[j] instanceof MTextArea)) {}
      }
    }
  }
  






  public void setCenterPanel(Container wp)
  {
    if (CenterPanel != null) {
      FormPanel.remove(CenterPanel);
    }
    CenterPanel = wp;
    if ((CenterPanel instanceof MPanel))
    {
      ((MPanel)CenterPanel).setBorder(BorderFactory.createRaisedBevelBorder());
    }
    createFormRecord(CenterPanel);
    for (Enumeration e = formRecord.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      Component[] wcomponents = (Component[])formRecord.get(wkey);
      for (int j = 0; j < wcomponents.length; j++) {
        if (!(wcomponents[j] instanceof MBrowse)) {
          wcomponents[j].addMouseListener(minMouse);
          wcomponents[j].addFocusListener(minFocus);
          wcomponents[j].addKeyListener(minKListener);
          if ((wcomponents[j] instanceof MButton)) {
            ((MButton)wcomponents[j]).addActionListener(minAction);
          }
          if ((wcomponents[j] instanceof MTextField)) {
            ((MTextField)wcomponents[j]).addActionListener(minAction);
          }
          if ((!(wcomponents[j] instanceof MTextArea)) || 
          


            ((wcomponents[j] instanceof JPasswordField))) {
            ((JPasswordField)wcomponents[j]).addActionListener(minAction);
          }
        }
        if ((wcomponents[j] instanceof MBrowse))
        {
          minBrowse = ((MBrowse)wcomponents[j]);
          wcomponents[j].addMouseListener(minMouse);
          wcomponents[j].addKeyListener(minKListener);
          
          if (getTopPanel() != null) {
            MTextField wtf = (MTextField)MUtil.getComponent(getTopPanel(), "wnsel");
            if (wtf != null) {
              minBrowse.setRegSelField(wtf);
            }
            wtf = (MTextField)MUtil.getComponent(getTopPanel(), "wnreg");
            if (wtf != null) {
              minBrowse.setNumRegField(wtf);
            }
          }
        }
      }
    }
  }
  
  public void setBotPanel(Container wp)
  {
    if (BotPanel != null) {
      FormPanel.remove(BotPanel);
    }
    BotPanel = wp;
    
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      BotPanel.setBackground(new Color(195, 212, 253));
    } else {
      BotPanel.setBackground(new Color(236, 233, 216));
    }
    Component[] wcomponents = BotPanel.getComponents();
    for (int j = 0; j < wcomponents.length; j++) {
      wcomponents[j].addMouseListener(minMouse);
      wcomponents[j].addFocusListener(minFocus);
      wcomponents[j].addKeyListener(minKListener);
      if ((wcomponents[j] instanceof MButton)) {
        ((MButton)wcomponents[j]).addActionListener(minAction);
      }
      if ((wcomponents[j] instanceof MTextField)) {
        ((MTextField)wcomponents[j]).addActionListener(minAction);
      }
      if (!(wcomponents[j] instanceof MTextArea)) {}
    }
  }
  

  public Container getTopPanel()
  {
    return TopPanel;
  }
  
  public Container getCenterPanel()
  {
    return CenterPanel;
  }
  
  public Container getBotPanel()
  {
    return BotPanel;
  }
  
  public void setAction(String ws)
  {
    action = ws;
  }
  
  public String getAction() {
    return action;
  }
  
  public void setActionArgs(String ws) {
    actionArgs = ws;
  }
  
  public String getActionArgs() {
    return actionArgs;
  }
  
  public void setFase(int wfase) {
    fase = wfase;
  }
  
  public int getFase() {
    return fase;
  }
  
  public void setEnt(String ws)
  {
    Ent = ws;
  }
  
  public String getEnt() {
    return Ent;
  }
  
  public void setName(String ws) {
    wfname = ws;
  }
  
  public String getName() {
    return wfname;
  }
  
  public void setType(String ws) {
    wftype = ws;
  }
  
  public String getType() {
    return wftype;
  }
  
  public void setEntName(String ws) {
    EntName = ws;
  }
  
  public String getEntName() {
    return EntName;
  }
  
  public void setRow(MRow wr)
  {
    minRow = wr;
  }
  



  public MRow getRow()
  {
    return minRow;
  }
  
  public void setCritRecord(Hashtable wr)
  {
    critRecord = wr;
  }
  
  public Hashtable getCritRecord()
  {
    return critRecord;
  }
  
  public void addPanelList(Hashtable wh) {
    if (PanelList == null) {
      PanelList = new Hashtable();
    }
    for (Enumeration e = wh.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      PanelList.put(wkey, wh.get(wkey));
    }
  }
  
  public void removePanelList(String ws) {
    if (PanelList.contains(ws)) {
      PanelList.remove(ws);
    }
  }
  
  public Hashtable getPanelList()
  {
    return PanelList;
  }
  
  public static void setPanel(String wname, Container wp)
  {
    if (PanelList == null) {
      PanelList = new Hashtable();
    }
    PanelList.put(wname, wp);
  }
  
  public static Container getPanel(String wname) throws MError
  {
    if (PanelList.get(wname) != null) {
      return (Container)PanelList.get(wname);
    }
    Container wp = MMData.getPanel(wname);
    if (wp != null) {
      setPanel(wname, wp);
    }
    return wp;
  }
  
  public static Container getPanelCopy(String wModelName, String wname)
    throws MError
  {
    if (PanelList.get(wname) != null) {
      return (Container)PanelList.get(wname);
    }
    Container wp = MMData.getPanel(wModelName);
    if (wp != null) {
      setPanel(wname, wp);
    }
    return wp;
  }
  
  public static Container getPanel(String wname, String wtype)
    throws MError
  {
    if (PanelList.get(wname + wtype) != null) {
      return (Container)PanelList.get(wname + wtype);
    }
    Container wp = MMData.getPanel(wname, wtype);
    if (wp != null) {
      setPanel(wname + wtype, wp);
    }
    return wp;
  }
  
  public static Container getPanel(String wname, String wtype, String wconnect)
    throws MError
  {
    if ((PanelList != null) && (PanelList.get(wname + wtype) != null)) {
      return (Container)PanelList.get(wname + wtype);
    }
    Container wp = MMData.getPanel(wname, MMData.getPanelType(wtype), wconnect);
    if (wp != null) {
      setPanel(wname + wtype, wp);
    }
    return wp;
  }
  

  public MWindowListener getWindowListener()
  {
    return minWListener;
  }
  
  public MActionListener getActionListener() {
    return minAction;
  }
  
  public MItemListener getItemListener() {
    return minItemListener;
  }
  
  public MFocusListener getFocusListener() {
    return minFocus;
  }
  
  public void setLastCmd(String wcmd) {
    lastCmd = wcmd;
  }
  
  public String getLastCmd()
  {
    return lastCmd;
  }
  
  public void setBrowse(MBrowse wbr)
  {
    minBrowse = wbr;
  }
  
  public MBrowse getBrowse()
  {
    if ((minBrowse == null) && 
      (CenterPanel != null)) {
      Component[] wcomponents = CenterPanel.getComponents();
      for (int j = 0; j < wcomponents.length; j++) {
        if ((wcomponents[j] instanceof MBrowse)) {
          minBrowse = ((MBrowse)wcomponents[j]);
        }
        if ((wcomponents[j] instanceof Container)) {
          for (int i = 0; i < ((Container)wcomponents[j]).getComponentCount(); i++) {
            if ((((Container)wcomponents[j]).getComponent(i) instanceof MBrowse)) {
              minBrowse = ((MBrowse)((Container)wcomponents[j]).getComponent(i));
            }
          }
        }
      }
    }
    
    return minBrowse;
  }
  
  public Vector getBrowseRows() {
    System.out.println("!*** MForm getBrowseRows");
    myModel = ((JKTableModel)getBrowse().getModel());
    wrows = myModel.getRowVector();
    return wrows;
  }
  
  public MFrame getMFrame()
  {
    Component wc = this;
    while (!(wc instanceof Window)) {
      wc = wc.getParent();
    }
    Window minWindow = (Window)wc;
    if ((minWindow instanceof MDialog)) {
      MDialog minDialog = (MDialog)minWindow;
      return (MFrame)minDialog.getParent();
    }
    return (MFrame)minWindow;
  }
  

  public Window getFormWindow()
  {
    Component wc = this;
    System.out.println("!*** MForm getFormWindow: " + wc);
    while (!(wc instanceof Window)) {
      wc = wc.getParent();
    }
    System.out.println("!*** MForm getFormWindow2: " + wc);
    return (Window)wc;
  }
  
  public void showStatusBar(boolean wboo)
  {
    statusShow = wboo;
  }
  
  public MStatusBar getStatusBar()
  {
    return (MStatusBar)StatusPanel;
  }
  
  public void addToolBar(MToolBar minToolBarGeral)
  {
    if (ToolPanel == null) {
      ToolPanel = new MPanel();
    }
    minToolBarGeral.addActionListener(minAction);
    minToolBarGeral.prepareToolBar();
    ToolPanel.add(minToolBarGeral);
    ToolPanel.setLayout(new java.awt.GridLayout(ToolPanel.getComponentCount(), 1));
    
    addDimension(minToolBarGeral.getPreferredSize());
  }
  
  public void addToolBarPanel(MPanel minToolBarPanel)
  {
    if (ToolPanel == null) {
      ToolPanel = new MPanel();
    }
    ToolPanel.add(minToolBarPanel);
    ToolPanel.setLayout(new java.awt.GridLayout(ToolPanel.getComponentCount(), 1));
    
    addDimension(minToolBarPanel.getPreferredSize());
  }
  
  public void addMenuBar(JMenuBar wmb)
  {
    if (MenuPanel == null) {
      MenuPanel = new MPanel();
      MenuPanel.setLayout(new java.awt.FlowLayout(0));
    }
    for (int j = 0; j < wmb.getMenuCount(); j++) {
      setMenuActions(wmb.getMenu(j));
    }
    wmb.setOpaque(true);
    wmb.setAlignmentX(0.0F);
    wmb.setAlignmentY(0.0F);
    MenuPanel.add(wmb);
    addDimension(wmb.getPreferredSize());
  }
  
  private void setMenuActions(JMenu wJMenu)
  {
    for (int i = 0; i < wJMenu.getMenuComponentCount(); i++) {
      if ((wJMenu.getMenuComponent(i) instanceof JMenu)) {
        setMenuActions((JMenu)wJMenu.getMenuComponent(i));
      }
      if ((wJMenu.getMenuComponent(i) instanceof JMenuItem)) {
        ((JMenuItem)wJMenu.getMenuComponent(i)).addActionListener(getActionListener());
      }
    }
  }
  

  public void setAtribFormat(Container wp, String wop)
  {
    Component[] wcomponents = wp.getComponents();
    for (int j = 0; j < wcomponents.length; j++)
    {
      if ((wcomponents[j] instanceof MField)) {
        MField wf = (MField)wcomponents[j];
        wf.setAtribFormat("P");
        if ((wop.indexOf("UPDATE_ROW") != -1) && (wf.getAtribType() != "1")) {
          wf.setAtribFormat("N");
        }
        if (wop.indexOf("INSERT_ROW") != -1) {
          wf.setAtribFormat("N");
        }
        if (wf.getAtribType() == "3") {
          wf.setAtribFormat("P");
        }
        if (wf.getDisplayType() == "P") {
          wf.setAtribFormat("P");
        }
        if (wop.indexOf("CRITERIUM_ROW") != -1) {
          wf.setAtribFormat("N");
        }
      }
      
      if ((wcomponents[j] instanceof MTextArea)) {
        MTextArea wta = (MTextArea)wcomponents[j];
        wta.setAtribFormat("P");
        if ((wop.indexOf("UPDATE_ROW") != -1) && (wta.getAtribType() != "1")) {
          wta.setAtribFormat("N");
        }
        if (wop.indexOf("INSERT_ROW") != -1) {
          wta.setAtribFormat("N");
        }
        if (wta.getAtribType() == "3") {
          wta.setAtribFormat("P");
        }
        if (wta.getDisplayType() == "P") {
          wta.setAtribFormat("P");
        }
        if (wop.indexOf("CRITERIUM_ROW") != -1) {
          wta.setAtribFormat("N");
        }
      }
      

      if ((wcomponents[j] instanceof JTabbedPane)) {
        JTabbedPane wtp = (JTabbedPane)wcomponents[j];
        for (int i = 0; i < wtp.getTabCount(); i++) {
          setAtribFormat((Container)wtp.getComponentAt(i), wop);
        }
      }
      if ((wcomponents[j] instanceof MPanel)) {
        setAtribFormat((Container)wcomponents[j], wop);
      }
    }
  }
  
  private void addDimension(Dimension wdim)
  {
    minDim.height += height;
    if (width > minDim.width) {
      minDim.width = width;
    }
  }
  
  public Dimension getMinimumSize()
  {
    Dimension wdim = new Dimension(0, 0);
    width = minDim.width;
    height = minDim.height;
    if (ToolPanel != null) {
      height += ToolPanel.getPreferredSize().height;
      if (ToolPanel.getPreferredSize().width > width) {
        width = ToolPanel.getPreferredSize().width;
      }
    }
    if (TopPanel != null) {
      height += TopPanel.getPreferredSize().height;
      if (TopPanel.getPreferredSize().width > width) {
        width = TopPanel.getPreferredSize().width;
      }
    }
    if ((CenterPanel != null) && (CenterPanel != null))
    {
      if (CenterPanel.getSize().height > 0) {
        height += CenterPanel.getPreferredSize().height;
      }
      


      if ((CenterPanel.getSize().width > 0) && 
        (CenterPanel.getPreferredSize().width > width)) {
        width = CenterPanel.getPreferredSize().width;
      }
    }
    

    if (BotPanel != null) {
      BotPanel.doLayout();
      height += BotPanel.getPreferredSize().height;
      if (BotPanel.getPreferredSize().width + 20 > width) {
        width = (BotPanel.getPreferredSize().width + 20);
      }
    }
    


    if ((StatusPanel != null) && (statusShow)) {
      height += StatusPanel.getPreferredSize().height;
    }
    


    return wdim;
  }
  
  public Dimension getPreferredSize() {
    Dimension s = getSize();
    Dimension m = getMinimumSize();
    return new Dimension(Math.max(width, width), Math.max(height, height));
  }
  
  public void hideForm()
  {
    if (formParent != null) {
      if ((formParent instanceof MFrame)) {
        MFrame.removeModalChild(getMFrame());
      }
      try {
        PT.Minimal.actions.MRegister.checkInterest(getName());
      } catch (MError err) {
        err.show(getMFrame());
      }
      if ((getOKButton() != null) && (getOKButton().isEnabled())) {
        getOKButton().requestFocus();
      } else if ((getENDButton() != null) && (getENDButton().isEnabled())) {
        getENDButton().requestFocus();
      }
      formParent.setVisible(false);
    }
  }
  
  public void showForm() {
    showForm(true);
  }
  
  public void showForm(boolean hideParent) { showForm(hideParent, true); }
  
  public void showForm(boolean hideParent, boolean setDefFocus)
  {
    Container wc = null;
    MTextField focusOwner = null;
    MTextArea focusOwnerTA = null;
    

    if ((setDefFocus) && 
      (getCenterPanelOrSelectedPane() != null) && ((getCenterPanelOrSelectedPane() instanceof Container)))
    {
      wc = (Container)getCenterPanelOrSelectedPane();
      Component[] wcomponents = wc.getComponents();
      for (int j = wcomponents.length - 1; j >= 0; j--) {
        if ((wcomponents[j] instanceof MTextField)) {
          MTextField wtf = (MTextField)wcomponents[j];
          wtf.setChanged(false);
          if ((wtf.isEditable()) && (wtf.isEnabled())) {
            focusOwner = wtf;
          }
        }
        if ((wcomponents[j] instanceof MTextArea)) {
          MTextArea wta = (MTextArea)wcomponents[j];
          wta.setChanged(false);
          if ((wta.isEditable()) && (wta.isEnabled())) {
            focusOwnerTA = wta;
          }
        }
      }
    }
    


    if ((formParent != null) && (!formParent.isVisible())) {
      if (hideParent) {
        MFrame wparent = MFrame.getModalParent(getMFrame());
        if ((wparent != null) && (wparent.getEnt().equals(getEnt())))
        {
          wparent.setVisible(false);
        }
      }
      
      if ((getOKButton() != null) && (getOKButton().isEnabled())) {
        getRootPane().setDefaultButton(getOKButton());
      } else if ((getENDButton() != null) && (getENDButton().isEnabled())) {
        getRootPane().setDefaultButton(getENDButton());
      }
      
      formParent.setVisible(true);
    }
    

    if (focusOwner != null) {
      focusOwner.requestFocus();
    }
    else if (focusOwnerTA != null) {
      focusOwnerTA.requestFocus();
    } else if ((getOKButton() != null) && (getOKButton().isEnabled())) {
      getOKButton().requestFocus();
    } else if ((getENDButton() != null) && (getENDButton().isEnabled())) {
      getENDButton().requestFocus();
    }
  }
  


  public void setFormParent(Container wcon)
  {
    formParent = wcon;
  }
  
  public Container getFormParent()
  {
    return formParent;
  }
  
  public void setActionObject(Object wob)
  {
    wactionObject = wob;
  }
  
  public Object getActionObject() {
    return wactionObject;
  }
  
  public void setActionObjectIndex(int windex) {
    actionIndex = windex;
  }
  
  public int getActionObjectIndex() {
    return actionIndex;
  }
  
  private void createFormRecord(Container wc)
  {
    Component[] wcomponents = wc.getComponents();
    for (int j = 0; j < wcomponents.length; j++)
    {


      if (wcomponents[j].getName() != null) {
        if (formRecord.containsKey(wcomponents[j].getName().toLowerCase())) {
          Component[] wocomp = (Component[])formRecord.get(wcomponents[j].getName().toLowerCase());
          Component[] wncomp = new Component[wocomp.length + 1];
          
          if ((!(wcomponents[j] instanceof MTextArea)) || (
          


            ((wcomponents[j] instanceof MTextField)) && ((wocomp[0] instanceof MTextField)) && (((MTextField)wcomponents[j]).getDisplayType() != "P") && (((MTextField)wocomp[0]).getDisplayType() == "P")))
          {


            wncomp[0] = wcomponents[j];
            for (int i = 0; i < wocomp.length; i++) {
              wncomp[(i + 1)] = wocomp[i];


            }
            


          }
          else if ((!(wcomponents[j] instanceof MTextArea)) || (
          

            ((wcomponents[j] instanceof MTextArea)) && ((wocomp[0] instanceof MTextArea)) && (((MTextArea)wcomponents[j]).getDisplayType() != "P") && (((MTextArea)wocomp[0]).getDisplayType() == "P")))
          {



            wncomp[0] = wcomponents[j];
            for (int i = 0; i < wocomp.length; i++) {
              wncomp[(i + 1)] = wocomp[i];
            }
          } else {
            for (int i = 0; i < wocomp.length; i++) {
              wncomp[i] = wocomp[i];
            }
            wncomp[wocomp.length] = wcomponents[j];
          }
          

          formRecord.put(wcomponents[j].getName().toLowerCase(), wncomp);
        } else {
          Component[] wncomp = new Component[1];
          wncomp[0] = wcomponents[j];
          formRecord.put(wcomponents[j].getName().toLowerCase(), wncomp);
        }
      }
      if ((wcomponents[j] instanceof JTabbedPane)) {
        minTabbedPane = ((JTabbedPane)wcomponents[j]);
        for (int i = 0; i < minTabbedPane.getTabCount(); i++) {
          createFormRecord((Container)minTabbedPane.getComponentAt(i));
        }
      }
      if ((wcomponents[j] instanceof MPanel)) {
        createFormRecord((Container)wcomponents[j]);
      }
    }
  }
  
  public Hashtable getFormValues()
  {
    Hashtable whashtable = new Hashtable();
    for (Enumeration e = formRecord.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      Component[] wcomponents = (Component[])formRecord.get(wkey);
      int j = 0;
      
      if ((wcomponents[j] instanceof JTextComponent))
      {
        if ((wcomponents[j] instanceof MTextField))
        {
          whashtable.put(wcomponents[j].getName(), ((MTextField)wcomponents[j]).getUnFormatedText());
        }
        else {
          whashtable.put(wcomponents[j].getName(), ((MTextArea)wcomponents[j]).getUnFormatedText());
        }
      }
      if ((wcomponents[j] instanceof MChoice)) {
        whashtable.put(wcomponents[j].getName(), ((MChoice)wcomponents[j]).getSelectedItem());
      }
      if ((wcomponents[j] instanceof TextComponent))
      {
        whashtable.put(wcomponents[j].getName(), ((TextComponent)wcomponents[j]).getText());
      }
    }
    
    return whashtable;
  }
  

  public Hashtable getFormExpansions()
  {
    Hashtable whashtable = new Hashtable();
    for (Enumeration e = formRecord.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      Component[] wcomponents = (Component[])formRecord.get(wkey);
      for (int j = 0; j < wcomponents.length; j++) {
        if ((wcomponents[j] instanceof MTextField)) {
          MTextField wtf = (MTextField)wcomponents[j];
          if (wtf.getAtribType().equals("3")) {
            whashtable.put(wcomponents[j].getName(), ((MTextField)wcomponents[j]).getUnFormatedText());
            break;
          }
        }
      }
    }
    return whashtable;
  }
  

  public Component getFormExpansion(String wkey)
  {
    Component wc = null;
    if (formRecord.containsKey(wkey.toLowerCase())) {
      Component[] wcomponents = (Component[])formRecord.get(wkey.toLowerCase());
      for (int j = 0; j < wcomponents.length; j++) {
        if ((wcomponents[j] instanceof MTextField)) {
          MTextField wtf = (MTextField)wcomponents[j];
          if (wtf.getAtribType().equals("3")) {
            wc = wcomponents[j];
            break;
          }
        }
      }
    }
    return wc;
  }
  
  public Component getFormComponent(String wkey)
  {
    Component wc = null;
    if (formRecord.containsKey(wkey.toLowerCase())) {
      Component[] wcomponents = (Component[])formRecord.get(wkey.toLowerCase());
      wc = wcomponents[0];
    }
    return wc;
  }
  

  public boolean containsKey(String wkey)
  {
    boolean b = formRecord.containsKey(wkey.toLowerCase());
    
    return formRecord.containsKey(wkey.toLowerCase());
  }
  
  public void clearFormValues()
  {
    for (Enumeration e = formRecord.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      Component[] wcomponents = (Component[])formRecord.get(wkey);
      for (int j = 0; j < wcomponents.length; j++) {
        if ((wcomponents[j] instanceof JTextComponent)) {
          ((JTextComponent)wcomponents[j]).setText("");
        }
        if ((wcomponents[j] instanceof TextComponent)) {
          ((TextComponent)wcomponents[j]).setText("");
        }
      }
    }
  }
  
  public void setFormValues(Hashtable whashtable)
  {
    for (Enumeration e = formRecord.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      String wval = String.valueOf(whashtable.get(wkey));
      if (whashtable.get(wkey) == null) {
        wval = "";
      }
      setFormValue(wkey, wval);
    }
  }
  
  public void setFormValue(String wkey, String wval)
  {
    if (formRecord.containsKey(wkey.toLowerCase())) {
      Component[] wcomponents = (Component[])formRecord.get(wkey.toLowerCase());
      for (int j = 0; j < wcomponents.length; j++) {
        if ((wcomponents[j] instanceof JTextComponent)) {
          if ((wcomponents[j] instanceof MTextField))
          {
            ((MTextField)wcomponents[j]).setText(((MTextField)wcomponents[j]).getFormatedText(wval));
          }
          else if ((wcomponents[j] instanceof MTextArea))
          {
            ((MTextArea)wcomponents[j]).setText(wval);

          }
          else if ((wcomponents[j] instanceof MCheckBox)) {
            ((MCheckBox)wcomponents[j]).setText(wval);
          } else {
            ((JTextComponent)wcomponents[j]).setText(wval);
          }
        }
        if ((wcomponents[j] instanceof TextComponent)) {
          ((TextComponent)wcomponents[j]).setText(wval);
        }
      }
    }
  }
  
  public void setFormValue(String wkey, String wval, String watribtype)
  {
    if (formRecord.containsKey(wkey.toLowerCase())) {
      Component[] wcomponents = (Component[])formRecord.get(wkey.toLowerCase());
      for (int j = 0; j < wcomponents.length; j++) {
        if ((wcomponents[j] instanceof JTextComponent)) {
          if ((wcomponents[j] instanceof MTextField)) {
            MTextField wtf = (MTextField)wcomponents[j];
            if (wtf.getAtribType().equals(watribtype)) {
              wtf.setText(wtf.getFormatedText(wval));
            }
          } else if ((wcomponents[j] instanceof MCheckBox)) {
            ((MCheckBox)wcomponents[j]).setText(wval);
          } else {
            ((JTextComponent)wcomponents[j]).setText(wval);
          }
        }
        if ((wcomponents[j] instanceof TextComponent)) {
          ((TextComponent)wcomponents[j]).setText(wval);
        }
      }
    }
  }
  
  public void setUnFormattedFormValue(String wkey, String wval)
  {
    if (formRecord.containsKey(wkey.toLowerCase())) {
      Component[] wcomponents = (Component[])formRecord.get(wkey.toLowerCase());
      if ((wcomponents[0] instanceof MTextField)) {
        setFormValue(wkey, ((MTextField)wcomponents[0]).getFormatedText(wval));
      }
    }
  }
  
  public String getFormValue(String wkey)
  {
    String ws = null;
    wkey = wkey.toLowerCase();
    if (formRecord.containsKey(wkey)) {
      Component[] wcomponents = (Component[])formRecord.get(wkey);
      int j = 0;
      if ((wcomponents[j] instanceof JTextComponent)) {
        if ((wcomponents[j] instanceof MTextField)) {
          ws = ((MTextField)wcomponents[j]).getUnFormatedText();
        } else {
          ws = ((MTextArea)wcomponents[j]).getUnFormatedText();
        }
      } else if ((wcomponents[j] instanceof MChoice)) {
        Object wobject = ((MChoice)wcomponents[j]).getSelectedItem();
        if ((wobject instanceof String)) {
          ws = (String)wobject;
        }
      } else if ((wcomponents[j] instanceof TextComponent)) {
        ws = ((TextComponent)wcomponents[j]).getText();
      }
    }
    return ws;
  }
  
  public Object get(String wkey)
  {
    wkey = wkey.toLowerCase();
    Object wobj;
    Object wobj; if ("formaction".equals(wkey)) {
      wobj = getAction();
    } else {
      wobj = getFormValue(wkey);
      if (wobj == null) {
        if (getRow() != null) {
          wobj = getRow().get(wkey);
        }
      } else {
        Component wc = getFormComponent(wkey);
        if ((wc != null) && ((wc instanceof MTextField))) {
          MTextField wtf = (MTextField)wc;
          if (wtf.getMask().startsWith("N")) {
            wobj = new Double(MUtil.getNumber((String)wobj));
          }
        }
      }
      if (wobj == null) {
        throw new MError("MIN-910002E", new String[] { getName() + "." + wkey });
      }
    }
    
    return wobj;
  }
  
  public String getFormatedValue(String wkey)
  {
    wkey = wkey.toLowerCase();
    if ("formaction".equals(wkey)) {
      return getAction();
    }
    String wobj = getFormValue(wkey);
    if ((wobj == null) && 
      (getRow() != null)) {
      wobj = getRow().getFormatedValue(wkey);
    }
    
    if (wobj == null) {
      throw new MError("MIN-910001E", new String[] { getName() + "." + wkey });
    }
    return wobj;
  }
  


  public void set(String wkey, Object wval)
  {
    if ((wval instanceof Number)) { BigDecimal wbdc;
      BigDecimal wbdc;
      if ((wval instanceof BigDecimal)) {
        wbdc = (BigDecimal)wval;
      } else {
        wbdc = new BigDecimal(((Number)wval).doubleValue());
      }
      if (wbdc.scale() > 20) wbdc = wbdc.setScale(20, 4);
      wval = wbdc.toString();
    }
    
    if ((wval instanceof String)) {
      if (formRecord.containsKey(wkey.toLowerCase())) {
        setUnFormattedFormValue(wkey.toLowerCase(), (String)wval);
      } else if (getRow() != null) {
        getRow().set(wkey.toLowerCase(), (String)wval);
      }
    } else {
      String wemsg = "MForm " + getName() + "." + wkey + ": valor de tipo " + wval.getClass() + " não permitido.";
      throw new MError("MIN-910001E", new String[] { wemsg });
    }
  }
  
  public void showComponentTab(String wkey)
  {
    Component wc = null;
    if (formRecord.containsKey(wkey.toLowerCase())) {
      Component[] wcomponents = (Component[])formRecord.get(wkey.toLowerCase());
      wc = wcomponents[0];
    }
    
    while ((wc != null) && (!(wc instanceof Window))) {
      if ((wc.getParent() instanceof JTabbedPane)) {
        JTabbedPane wtp = (JTabbedPane)wc.getParent();
        wtp.setSelectedComponent(wc);
        break;
      }
      if ((minTabbedPane != null) && 
        (minTabbedPane.indexOfComponent(wc) != -1)) {
        minTabbedPane.setSelectedComponent(wc);
      }
      
      wc = wc.getParent();
    }
  }
  
  public Hashtable getFormComponentsRecord()
  {
    return formRecord;
  }
  
  public MPanel constrBotPanel(int wsumbot)
  {
    MPanel wbotpanel = new MPanel(getName() + "_BP");
    wbotpanel.setLayout(new java.awt.FlowLayout(2));
    wbotpanel.addButtons(wsumbot);
    MButton wok = (MButton)MUtil.getComponent(wbotpanel, "wok");
    if (wok != null) {
      setOKButton(wok);
    }
    MButton wend = (MButton)MUtil.getComponent(wbotpanel, "wend");
    if (wend != null) {
      setOKButton(wend);
    }
    return wbotpanel;
  }
  























  public void setCurrencyButtonGroup(ButtonGroup wbg)
  {
    wcurrencygroup = wbg;
  }
  
  public void setCurrencySelectedButton(String wnewcurrency) {
    System.out.println("!*** MForm setCurrencySelectedButton: " + wnewcurrency);
    if (wcurrencygroup != null)
    {
      String wcmd = "";int ib = 1;
      if (wnewcurrency == null) {
        wnewcurrency = "m";
      }
      if (wnewcurrency.equals("m"))
      {
        ib = 1;
      }
      if (wnewcurrency.equals("v"))
      {
        ib = 2;
      }
      if (wnewcurrency.equals("y"))
      {
        ib = 3;
      }
      int i = 1;
      for (Enumeration e = wcurrencygroup.getElements(); e.hasMoreElements();) {
        JToggleButton wtb = (JToggleButton)e.nextElement();
        if (i == ib) {
          wtb.setSelected(true);
          break;
        }
        i++;
      }
      





      setCurrency(wnewcurrency);
    }
  }
  
  public void setCurrency(String wnewcurrency)
  {
    wcurrency = wnewcurrency;
  }
  
  public String getCurrency() {
    return wcurrency;
  }
  
  public String getCurrencyName() {
    String wcmd = "";
    if (wcurrency.equals("m")) {
      wcmd = "CMD";
    }
    if (wcurrency.equals("v")) {
      wcmd = "PTE";
    }
    if (wcurrency.equals("y")) {
      wcmd = "EUR";
    }
    return wcmd;
  }
  
  public void setOKButton(MButton wok)
  {
    OKButton = wok;
  }
  
  public MButton getOKButton() {
    return OKButton;
  }
  
  public void setENDButton(MButton wend) {
    ENDButton = wend;
  }
  
  public MButton getENDButton() {
    return ENDButton;
  }
  
  public Component getCenterPanelOrSelectedPane() {
    if (minTabbedPane != null) {
      return minTabbedPane.getSelectedComponent();
    }
    return getCenterPanel();
  }
  

  public static MForm getDialogForm(MForm minForm, String wpanel, String wpaneltype, String wpanelname, int wbutt)
  {
    return getDialogForm(minForm, wpanel, wpaneltype, wpanelname, wbutt, "JMETADATA");
  }
  
  public static MForm getDialogForm(MForm minForm, String wpanel, String wpaneltype, String wpanelname, int wbutt, String wconnection)
  {
    MForm wfmc = getMForm(wpanel + wpaneltype);
    if ((wfmc == null) || (MGetMData.getHidden().size() > 0))
    {
      System.out.println("!*** MForm getNames: " + minForm.getEnt() + " " + wpanel + " " + wpaneltype + " " + wpanelname + " " + wbutt + " " + wconnection);
      wfmc = new MForm(wpanel + wpaneltype);
      wfmc.setEnt(minForm.getEnt());
      wfmc.setCenterPanel(getPanel(wpanel, wpaneltype, wconnection));
      wfmc.showStatusBar(false);
      wfmc.prepareForm();
      MDialog wd = new MDialog(minForm.getMFrame(), wpanelname, true, wfmc, wbutt);
      getBotPanelgetInsetsright = 10;
      getCenterPanelgetInsetsright = 10;
      getCenterPanelgetInsetsleft = 10;
      wfmc.prepareForm();
      wd.pack();
      wd.setSize(getPreferredSizewidth, getPreferredSizeheight + 10);
      wd.setCentered();
    }
    

    wfmc.setAtribFormat(wfmc.getCenterPanel(), "INSERT_ROW");
    lastContainer = minForm.getCenterPanel();
    wfmc.setAction("NONE");
    wfmc.setFase(401);
    return wfmc;
  }
  
  public String toString()
  {
    String out = getClass() + "[" + getName() + ",ent=" + getEnt() + ",type=" + getType() + ",action=" + getAction() + ",fase=" + getFase() + ",actionindex=" + getActionObjectIndex() + "]";
    






    return out;
  }
  

  public String toLongString()
  {
    String wout = toString() + "\n";
    wout = wout + "FORM RECORD" + "\n";
    for (Enumeration e = formRecord.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      Component[] wcomponents = (Component[])formRecord.get(wkey);
      for (int j = 0; j < wcomponents.length; j++) {
        wout = wout + wkey + ":" + wcomponents[j] + "\n";
      }
    }
    wout = wout + "FORM VALUES" + "\n";
    Hashtable whashtable = getFormValues();
    for (Enumeration e = whashtable.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      wout = wout + wkey + "=" + whashtable.get(wkey) + "\n";
    }
    
    return wout;
  }
  

  public void refreshForm()
  {
    MUtil.refreshForm(this);
  }
}
