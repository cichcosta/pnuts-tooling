package PT.Minimal.editor;

import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MPanel;
import PT.Minimal.geral.MUtil;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.plaf.TextUI;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class MEditor extends MForm
{
  protected MEditorKit kit;
  protected JEditorPane editArea;
  protected JTextField mode;
  protected JTextField location;
  protected OverwriteCaret caret;
  protected int xsize;
  protected int ysize;
  protected int xinset;
  protected int yinset;
  
  public MEditor(String formName, String ent, String entName, String type)
  {
    super(formName, ent);
    setEntName(entName);
    
    editArea = new JEditorPane();
    editArea.setBackground(Color.white);
    editArea.setEditable(true);
    

    editArea.setSize(100, 50);
    editArea.setFont(new Font("Monospaced", 0, 12));
    
    kit = new MEditorKit(this);
    kit.setComponent(editArea);
    editArea.setEditorKitForContentType("text/java", kit);
    editArea.setContentType(type);
    
    editArea.addKeyListener(new MEditorKeyAdapter(this));
    
    editArea.addCaretListener(new javax.swing.event.CaretListener() {
      public void caretUpdate(CaretEvent e) {
        updateLocation();
      }
      
    });
    FontMetrics fm = editArea.getFontMetrics(editArea.getFont());
    xsize = fm.charWidth('m');
    ysize = fm.getHeight();
    
    if ((Boolean.valueOf(System.getProperties().getProperty("PT.Minimal.pnutsColor", "false")).booleanValue()) && (MUtil.classExists("sun.tools.java.Constants")))
    {


      MEditorContext styles = kit.getStylePreferences();
      
      Style s = styles.getStyleForScanValue(MEditorToken.COMMENT.getScanValue());
      StyleConstants.setForeground(s, new Color(102, 153, 153));
      s = styles.getStyleForScanValue(MEditorToken.STRINGVAL.getScanValue());
      StyleConstants.setForeground(s, new Color(102, 153, 102));
      Color keyword = new Color(102, 102, 255);
      for (int code = 70; code <= 130; code++) {
        s = styles.getStyleForScanValue(code);
        if (s != null) {
          StyleConstants.setForeground(s, keyword);
        }
      }
    }
    
    mode = new JTextField("Insert", 10);
    mode.setHorizontalAlignment(0);
    mode.setBackground(Color.lightGray);
    mode.setBorder(new SoftBevelBorder(1));
    
    location = new JTextField("Line = 0 Column = 0", 15);
    location.setHorizontalAlignment(0);
    location.setEditable(false);
    location.setBackground(Color.lightGray);
    location.setBorder(new SoftBevelBorder(1));
    
    caret = new OverwriteCaret();
    caret.setBlinkRate(500);
    editArea.setCaret(caret);
    
    lastContainer = editArea;
    
    JScrollPane scroller = new JScrollPane();
    JViewport port = scroller.getViewport();
    port.add(editArea);
    
    port.setScrollMode(2);
    
    scroller.setHorizontalScrollBarPolicy(30);
    scroller.setVerticalScrollBarPolicy(20);
    

    setCenterPanel(scroller);
    
    MPanel bottom = constrBotPanel(0);
    bottom.setLayout(new FlowLayout(2));
    bottom.add(mode);
    bottom.add(location);
    bottom.addButtons(3);
    
    setBotPanel(bottom);
    setOKButton((MButton)MUtil.getComponent(bottom, "wok"));
    setENDButton((MButton)MUtil.getComponent(bottom, "wend"));
    
    prepareForm();
  }
  

  public boolean isEditable()
  {
    return editArea.isEditable();
  }
  
  public void setEditable(boolean flag) {
    editArea.setEditable(flag);
  }
  
  public void setText(String text) {
    System.out.println("!*** MEditor setText");
    if (!"".equals(text)) {
      int maxLength = 0;
      int textLength = text.length();
      int len = 0;
      for (int pos = 0; pos < textLength; pos++) {
        if (text.charAt(pos) == '\n') {
          if (len > maxLength) maxLength = len;
          len = 0;
        }
      }
      if (len > maxLength) { maxLength = len;
      }
      editArea.setSize(maxLength * xsize, getHeight());
      editArea.setText(text);
      editArea.setCaretPosition(1);
    } else {
      editArea.setCaretPosition(0);
    }
  }
  
  public String getText() {
    return editArea.getText();
  }
  
  public void showForm(boolean flag) {
    kit.resetUndo();
    super.showForm(flag);
    if (editArea.isEditable()) {
      MUtil.requestFocus(editArea);
    }
  }
  
  public void updateLocation()
  {
    try {
      Rectangle r = editArea.modelToView(editArea.getCaretPosition());
      int y = y / ysize + 1;
      int x = x / xsize + 1;
      location.setText("Line = " + y + " Column = " + x);
      location.repaint();
    }
    catch (Exception ex) {}
  }
  
  public OverwriteCaret getCaret() {
    return caret;
  }
  
  public void read(FileReader r, Object o) throws IOException {
    editArea.read(r, o);
  }
  
  public void toggleInsertMode()
  {
    caret.toggleMode();
    mode.setText(caret.getMode() ? "Insert" : "Overwrite");
    mode.repaint();
  }
  
  static class OverwriteCaret extends DefaultCaret { OverwriteCaret() {}
    
    protected boolean insert = true;
    
    public void setMode(boolean b) { insert = b; }
    
    public boolean getMode() { return insert; }
    
    public void toggleMode() { insert = (!insert); }
    
    protected synchronized void damage(Rectangle r) {
      if (r != null) {
        x = (x - 4);
        y = y;
        width = 12;
        height = height;
        repaint();
      }
    }
    
    public void paint(Graphics g) {
      if (isVisible()) {
        try {
          TextUI mapper = getComponent().getUI();
          Rectangle r = mapper.modelToView(getComponent(), getComponent().getCaretPosition());
          
          g.setColor(getComponent().getCaretColor());
          if (!insert) {
            g.fillRect(x, y, 6, height - 1);
          } else {
            g.drawLine(x, y, x, y + height - 1);
          }
        }
        catch (Exception e) {}
      }
    }
  }
}
