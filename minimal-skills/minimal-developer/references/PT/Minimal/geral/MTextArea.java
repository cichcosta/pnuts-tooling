package PT.Minimal.geral;

import PT.Minimal.util.MString;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JViewport;




public class MTextArea
  extends JTextArea
  implements MTextComponent, Serializable
{
  public String minNome;
  public String minAnchor;
  public int minRelPosX = 0;
  public int minRelPosY = 0;
  public int minPosX = 0;
  public int minPosY = 0;
  public boolean minBreakLn = false;
  public int minWidth = 1000;
  public int minHeight = 1000;
  
  public String minCmd;
  public String minCmdArgs;
  public String minTipoCmd;
  private String atribType = "4";
  private String dataType = "BINARY";
  private String displayType = "N";
  private boolean mustFill = false;
  private boolean attentionFill = false;
  private String minMask = " ";
  private int minInternalWidth = 0;
  private String minInternalMask = "";
  private String minMsg = " ";
  private MForm minForm;
  private JTextArea ta;
  private JViewport port;
  private Vector vAttach = new Vector();
  private int linhas;
  private int colunas;
  private boolean isChanged = false;
  private String wxrg = " ";
  private String wxajp = " ";
  private String wictx = " ";
  private String wniob = " ";
  private String wiexs = " ";
  private String wexsargs = " ";
  




  private MTextDocument minDoc;
  




  public MTextArea(String wnome)
  {
    ta = new JTextArea(wnome);
    minNome = wnome;
    
    setAtrrib();
    setAtrrib();
  }
  













  public MTextArea(String wnome, String wcmd, int wlines, int wcolumns)
  {
    this(wnome, "", wcmd, wlines, wcolumns);
    
    setAtrrib();
  }
  














  public MTextArea(String wnome, String wlabel, String wcmd, int wlines, int wcolumns)
  {
    this(wnome, wlabel, wcmd, wlines, wcolumns, false);
  }
  




















  public MTextArea(String wnome, String wlabel, String wcmd, int wlines, int wcolumns, boolean wbreakln)
  {
    setName(wnome);
    minNome = wnome;
    
    setCmd(wcmd);
    minBreakLn = wbreakln;
    FontMetrics fmDimensoes = getFontMetrics(getFont());
    

    linhas = (wlines * fmDimensoes.getHeight());
    
    colunas = (wlines * fmDimensoes.getHeight() + 4);
    linhas = (wcolumns * fmDimensoes.charWidth('a') + fmDimensoes.charWidth('a'));
    
    setPreferredSize(new Dimension(linhas, colunas));
    setAtrrib();
    
    setColumns(wcolumns);
    setRows(wlines);
    setLineWrap(true);
    setWrapStyleWord(true);
    
    addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        if (e.getKeyCode() == 9)
        {
          e.setKeyCode(17);
          
          transferFocus();
        }
      }
    });
  }
  





















  public MTextArea(String wnome, String wlabel, String wcmd, int wlines, int wcolumns, boolean wbreakln, int hchar, int vchar)
  {
    this(wnome, wlabel, wcmd, wlines, wcolumns, wbreakln);
    
    setColumns(hchar);
    setRows(vchar);
  }
  




































  public void paint(Graphics g)
  {
    super.paint(g);
  }
  










  public Dimension getPreferredSize()
  {
    return new Dimension(linhas, colunas);
  }
  









  public JTextArea getTextArea()
  {
    return ta;
  }
  






  public void setAtrrib()
  {
    setName(minNome);
    setFont(DEFAULTFONT);
    setBackground(DEFAULTCOLOR);
    setForeground(DEFFONTCOLOR);
    


    setBorder(BorderFactory.createMatteBorder(3, 2, 2, 2, new Color(215, 228, 244)));
    
    minDoc = new MTextDocument(this);
    setDocument(minDoc);
  }
  








  public void setCmd(String wcmd)
  {
    minCmd = wcmd;
  }
  








  public Dimension getMinimumSize()
  {
    return super.getPreferredSize();
  }
  








  public void setWidth(int width)
  {
    minWidth = width;
  }
  








  public void setMustFill(boolean wboo)
  {
    mustFill = wboo;
  }
  
  public void setAttentionFill(boolean wboo) { attentionFill = wboo; }
  









  public boolean getMustFill()
  {
    return mustFill;
  }
  








  public void setMsg(String wmsg)
  {
    minMsg = wmsg;
  }
  








  public String getMsg()
  {
    return minMsg;
  }
  








  public void setDisplayType(String ws)
  {
    displayType = ws;
  }
  








  public String getDisplayType()
  {
    return displayType;
  }
  








  public void setMask(String wmask)
  {
    minMask = wmask;
  }
  








  public String getMask()
  {
    return minMask;
  }
  








  public String getFormatedText()
  {
    if (MString.trimRight(getText()).length() > getRows() * getColumns()) {
      return getText().substring(0, getRows() * getColumns());
    }
    return MString.trimRight(getText());
  }
  









  public String getUnFormatedText()
  {
    if (MString.trimRight(getText()).length() > getRows() * getColumns()) {
      return getText().substring(0, getRows() * getColumns());
    }
    return MString.trimRight(getText());
  }
  































































  public void setChanged(boolean wchanged)
  {
    if ((wchanged) && (wchanged != isChanged)) {
      String wfield = MUtil.getArg(getExpansionField(), ',');
      
      if ((wfield != null) && (wfield.trim().length() != 0)) {
        Component wc = MUtil.getComponent(getParent(), wfield);
        
        if ((wc != null) && ((wc instanceof MTextField)))
        {

          ((MTextField)wc).setText("******************");
        }
      }
    }
    isChanged = wchanged;
  }
  








  public boolean getChanged()
  {
    return isChanged;
  }
  








  public void setPromptCode(String ws)
  {
    wiexs = ws;
  }
  








  public String getPromptCode()
  {
    return wiexs;
  }
  








  public void setPromptTable(String ws)
  {
    wictx = ws;
  }
  








  public String getPromptTable()
  {
    return wictx;
  }
  








  public void setExpansionField(String ws)
  {
    wniob = ws;
  }
  








  public String getExpansionField()
  {
    return wniob;
  }
  








  public void setPromptControl(String wpc)
  {
    wxajp = wpc;
  }
  








  public String getPromptControl()
  {
    return wxajp;
  }
  








  public void setRule(String wrg)
  {
    wxrg = wrg;
  }
  








  public String getRule()
  {
    return wxrg;
  }
  








  public void setPromptArgs(String wargs)
  {
    wexsargs = wargs;
  }
  








  public String getPromptArgs()
  {
    return wexsargs;
  }
  








  public void setAtribFormat(String wmode)
  {
    if (wmode == "P") {
      setBackground(new Color(255, 251, 238));
      setForeground(new Color(0, 0, 255));
      setEditable(false);
    }
    
    if (wmode == "N") {
      setBackground(MField.DEFAULTCOLOR);
      setForeground(MField.DEFFONTCOLOR);
      if (attentionFill == true) {
        setBackground(MField.ATTENTIONFILL);
      }
      if (mustFill == true) {
        setBackground(MField.MUSTFILL);
      }
      setEditable(true);
      setMode(true);
    }
  }
  








  public void setInternalWidth(int w)
  {
    minInternalWidth = w;
  }
  








  public int getInternalWidth()
  {
    return minInternalWidth;
  }
  








  public void setInternalMask(String wmask)
  {
    minInternalMask = wmask;
  }
  








  public String getInternalMask()
  {
    return minInternalMask;
  }
  








  public void setAtribType(String wat)
  {
    atribType = wat;
  }
  








  public String getAtribType()
  {
    return atribType;
  }
  








  public void setDataType(String ws)
  {
    dataType = ws;
  }
  








  public String getDataType()
  {
    return dataType;
  }
  








  public void setCommand(String wcmd)
  {
    minCmd = wcmd;
  }
  








  public String getCommand()
  {
    return minCmd;
  }
  








  public void setCommandArgs(String wcmdargs)
  {
    minCmdArgs = wcmdargs;
  }
  








  public String getCommandArgs()
  {
    return minCmdArgs;
  }
  








  public String getMinAnchor()
  {
    return minAnchor;
  }
  








  public int getMinRelPosX()
  {
    return minRelPosX;
  }
  








  public int getMinRelPosY()
  {
    return minRelPosY;
  }
  








  public int getMinPosX()
  {
    return minPosX;
  }
  








  public int getMinPosY()
  {
    return minPosY;
  }
  








  public boolean getMinBreakLn()
  {
    return minBreakLn;
  }
  

  private boolean wstatus = true;
  








  public boolean isFocusTraversable()
  {
    return wstatus;
  }
  








  public void setMode(boolean wmode)
  {
    wstatus = wmode;
  }
  








  public void addAttach(String path)
  {
    vAttach.add(path);
  }
  








  public Vector getAttachments()
  {
    return vAttach;
  }
}
