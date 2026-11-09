package PT.Minimal.geral;

import java.awt.Dimension;
import java.awt.Frame;

public class MDialog extends javax.swing.JDialog
{
  public MPanel centerPanel;
  public MPanel botPanel = new MPanel();
  
  public static final int B_CANCEL = 1;
  
  public static final int B_OK = 2;
  
  public int buttonChoosed = 0;
  public java.util.Vector wfields;
  public String wi_ob;
  private MForm thisForm;
  
  public MDialog(Frame wparent, String wtitle, boolean wmodal) {
    super(wparent, wtitle, wmodal);
    



    setBackground(new java.awt.Color(224, 233, 254));
  }
  
  public MDialog(Frame wparent, String wtitle, boolean wmodal, MForm wform) { super(wparent, wmodal);
    setTitle(wtitle);
    add("Center", wform);
    setBackground(new java.awt.Color(224, 233, 254));
    
    addWindowListener(wform.getWindowListener());
    thisForm = wform;
    wform.setFormParent(this);
  }
  
  public MDialog(Frame wparent, String wtitle, boolean wmodal, MForm wform, int wsumbot) {
    super(wparent, wmodal);
    setTitle(wtitle);
    add("Center", wform);
    setBackground(new java.awt.Color(224, 233, 254));
    
    addWindowListener(wform.getWindowListener());
    thisForm = wform;
    if (wsumbot > 0) {
      constrBotPanel(wsumbot);
      wform.setBotPanel(botPanel);
    }
    wform.prepareForm();
    wform.setFormParent(this);
  }
  
  public MDialog(Frame wparent, String wtitle, boolean wmodal, int wsumbot, String wmsg) {
    this(wparent, wtitle, wmodal);
    centerPanel = new MPanel("MSG", 2);
    centerPanel.setLayout(new java.awt.BorderLayout());
    centerPanel.add("Center", new java.awt.Label(wmsg, 1));
    MForm wform = new MForm("Error Form");
    thisForm = wform;
    wform.setCenterPanel(centerPanel);
    if (wsumbot > 0) {
      constrBotPanel(wsumbot);
      wform.setBotPanel(botPanel);
    }
    wform.prepareForm();
    addWindowListener(wform.getWindowListener());
    add("Center", wform);
    setBackground(new java.awt.Color(224, 233, 254));
    
    wform.setFormParent(this);
  }
  
  public MDialog(Frame wparent, String wtitle, boolean wmodal, int wsumbot, java.awt.Component wcomp) {
    this(wparent, wtitle, wmodal);
    centerPanel = new MPanel("MSG", 2);
    centerPanel.setLayout(new java.awt.BorderLayout());
    centerPanel.add("Center", wcomp);
    MForm wform = new MForm(wtitle);
    thisForm = wform;
    wform.setCenterPanel(centerPanel);
    if (wsumbot > 0) {
      constrBotPanel(wsumbot);
      wform.setBotPanel(botPanel);
    }
    wform.prepareForm();
    addWindowListener(wform.getWindowListener());
    add("Center", wform);
    setBackground(new java.awt.Color(224, 233, 254));
    
    wform.setFormParent(this);
  }
  
  public MDialog(Frame wparent, String wtitle, boolean wmodal, java.util.Vector wpfields, int wsumbot, String wcmd)
  {
    this(wparent, wtitle, wmodal);
    centerPanel = new MPanel(wi_ob, 2);
    centerPanel.setLayout(new MLayoutManager());
    wfields = wpfields;
    for (int i = 0; i < wfields.size(); i++) {
      java.awt.Component minComponent = (java.awt.Component)wfields.elementAt(i);
      centerPanel.add(minComponent);
    }
    MForm wform = new MForm(wtitle);
    thisForm = wform;
    wform.setAction(wcmd);
    wform.setCenterPanel(centerPanel);
    if (wsumbot > 0) {
      constrBotPanel(wsumbot);
      wform.setBotPanel(botPanel);
    }
    wform.prepareForm();
    addWindowListener(wform.getWindowListener());
    add("Center", wform);
    setBackground(new java.awt.Color(224, 233, 254));
    
    wform.setFormParent(this);
  }
  
  public MDialog(Frame wparent, String wtitle, boolean wmodal, MPanel wpanel, int wsumbot, String wcmd) {
    this(wparent, wtitle, wmodal);
    centerPanel = wpanel;
    MForm wform = new MForm(wtitle);
    thisForm = wform;
    wform.setAction(wcmd);
    wform.setCenterPanel(centerPanel);
    if (wsumbot > 0) {
      constrBotPanel(wsumbot);
      wform.setBotPanel(botPanel);
    }
    wform.prepareForm();
    addWindowListener(wform.getWindowListener());
    add("Center", wform);
    setBackground(new java.awt.Color(224, 233, 254));
    
    wform.setFormParent(this);
  }
  
  public void setCentered() {
    setCentered(getPreferredSize());
  }
  
  public void setCentered(Dimension d) {
    java.awt.Rectangle parentOrig = getParent().getBounds();
    System.out.println(parentOrig);
    if (parentOrig.isEmpty()) {
      width = getDefaultToolkitgetScreenSizewidth;
      height = getDefaultToolkitgetScreenSizeheight;
      x = 0;
      y = 0;
    }
    if ((x == 0) || (y == 0)) {
      setLocation((getDefaultToolkitgetScreenSizewidth - width) / 2, (getDefaultToolkitgetScreenSizeheight - height) / 2);
    }
    else {
      setLocation(x + (width - width) / 2, y + (height - height) / 2);
    }
  }
  
  public void constrCenterPanel()
  {
    for (int i = 0; i < wfields.size(); i++) {
      java.awt.Component minComponent = (java.awt.Component)wfields.elementAt(i);
      centerPanel.add(minComponent);
    }
  }
  
  public void constrBotPanel(int wsumbot) {
    int wbot = wsumbot;
    botPanel.setLayout(new java.awt.FlowLayout(2));
    
    if (wbot - 2 >= 0) {
      wbot -= 2;
      MButton minOK = new MButton("OK");
      minOK.setName("wok");
      minOK.setCmd("OKDIALOG");
      botPanel.add(minOK);
      thisForm.setOKButton(minOK);
    }
    if (wbot - 1 >= 0) {
      wbot -= 1;
      MButton wend = new MButton("wend", MUtil.lang("\"Cancelar\""), "CLOSEWINDOW");
      wend.setCmd("CLOSEWINDOW");
      botPanel.add(wend);
      thisForm.setENDButton(wend);
    }
  }
  

  public MForm getForm() { return thisForm; }
  
  public Dimension getPreferredSize() {
    doLayout();
    
    return getLayout().preferredLayoutSize(this);
  }
  
  public java.awt.Component add(String pos, java.awt.Component wc) {
    return getContentPane().add(pos, wc);
  }
}
