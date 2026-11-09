package PT.Minimal.geral;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JTextField;











public class MTextField
  extends JTextField
  implements MTextComponent, Serializable
{
  private String minNome;
  private String minAnchor;
  private String minMsg = " ";
  private int minRelPosX = 0;
  private int minRelPosY = 0;
  private int minPosX = 0;
  private int minPosY = 0;
  private boolean minBreakLn = false;
  private int minWidth = 10;
  private int minFieldWidth = 0;
  private int minInternalWidth = 10;
  private int minDecimals = 0;
  
  private String atribType = "4";
  private String dataType = "TEXTTYPE";
  private String displayType = "N";
  private boolean mustFill = false;
  private boolean attentionFill = false;
  private boolean isChanged = false;
  private String minMask = " ";
  private String minInternalMask = "N#########.##";
  
  private String wxrg = " ";
  private String wxajp = " ";
  private String wictx = " ";
  private String wniob = " ";
  private String wiexs = " ";
  private String wexsargs = " ";
  



  private String minCmd;
  



  private String minCmdArgs;
  



  private MTextDocument minDoc;
  



  public MTextField() {}
  



  public MTextField(String wnome)
  {
    minNome = wnome;
    setAtrrib();
  }
  









  public MTextField(String wnome, int wwidth)
  {
    minNome = wnome;
    minWidth = wwidth;
    setAtrrib();
  }
  










  public MTextField(String wnome, int wwidth, boolean wbreakln)
  {
    minNome = wnome;
    minWidth = wwidth;
    minBreakLn = wbreakln;
    setAtrrib();
  }
  














  public MTextField(String wnome, int wwidth, boolean wbreakln, int wrelposx, int wrelposy)
  {
    minNome = wnome;
    minWidth = wwidth;
    minBreakLn = wbreakln;
    minRelPosX = wrelposx;
    minRelPosY = wrelposy;
    setAtrrib();
  }
  













  public MTextField(String wnome, int wwidth, boolean wbreakln, int wrelposx, int wrelposy, String wmsg)
  {
    minNome = wnome;
    minWidth = wwidth;
    minBreakLn = wbreakln;
    minRelPosX = wrelposx;
    minRelPosY = wrelposy;
    minMsg = wmsg;
    setAtrrib();
  }
  






  public void setAtrrib()
  {
    setName(minNome);
    setFont(DEFAULTFONT);
    setBackground(DEFAULTCOLOR);
    setForeground(DEFFONTCOLOR);
    minFieldWidth = minWidth;
    setColumns(minWidth + 1);
    

    setBorder(BorderFactory.createMatteBorder(3, 2, 2, 2, new Color(215, 228, 244)));
    
    minDoc = new MTextDocument(this);
    setDocument(minDoc);
    setScrollOffset(5);
  }
  








  public Dimension getMinimumSize()
  {
    Dimension d = super.getPreferredSize();
    if (width > minFieldWidth) {
      minFieldWidth = width;
    }
    width = minFieldWidth;
    height = MUtil.FIELD_HEIGHT;
    return d;
  }
  
















  public Dimension getPreferredSize()
  {
    return getMinimumSize();
  }
  








  public void setWidth(int width)
  {
    minFieldWidth = width;
  }
  








  public int getWidth()
  {
    if (minFieldWidth != 0) {
      return minFieldWidth;
    }
    return super.getWidth();
  }
  









  public int getNumCar()
  {
    return minWidth;
  }
  








  public void setNumCar(int width)
  {
    minWidth = width;
  }
  








  public void setInternalWidth(int width)
  {
    minInternalWidth = width;
    minDoc.setLimit(width);
  }
  








  public int getInternalWidth()
  {
    return minInternalWidth;
  }
  








  public void setName(String ws)
  {
    super.setName(ws);
  }
  








  public String getName()
  {
    return super.getName();
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
  
  public boolean getAttentionFill() { return attentionFill; }
  












  public void setChanged(boolean wchanged)
  {
    if ((wchanged) && (wchanged != isChanged)) {
      String wfield = MUtil.getArg(getExpansionField(), ',');
      
      if ((wfield != null) && (wfield.trim().length() != 0)) {
        Component wc = MUtil.getComponent(getParent(), wfield);
        
        if ((wc != null) && ((wc instanceof MTextField)) && 
          (!((MTextField)wc).isEditable()))
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
  








  public void setMsg(String wmsg)
  {
    minMsg = wmsg;
  }
  








  public String getMsg()
  {
    String wmsg = minMsg;
    
    if (mustFill) {
      wmsg = "(m) " + wmsg;
    }
    if (getPromptCode().trim().length() != 0) {
      wmsg = "(?) " + wmsg;
    }
    return wmsg;
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
    minDoc.setMask(wmask);
  }
  














  public String getMask()
  {
    return minMask;
  }
  








  public String getFormatedText()
  {
    return getFormatedText(getText());
  }
  








  public void setDecimals(int wdecimals)
  {
    minDecimals = wdecimals;
  }
  








  public int getDecimals()
  {
    return minDecimals;
  }
  










  public String getFormatedText(String ws)
  {
    return getFormatedText(ws, false);
  }
  











  public String getFormatedText(String ws, boolean withError)
  {
    int wnumcar = getInternalWidth();
    
    if ((wnumcar != 0) && (wnumcar < ws.length())) {
      ws = ws.substring(0, wnumcar);
    }
    if (getMask().startsWith("U")) {
      ws = ws.toUpperCase();
    }
    if (getMask().startsWith("L")) {
      ws = ws.toLowerCase();
    }
    if ((getMask().startsWith("N")) && (ws.trim().length() != 0)) {
      String wformat = getMask().substring(1);
      

      DecimalFormat wdf = new DecimalFormat(wformat);
      
      wdf.setDecimalFormatSymbols(MUtil.DFS);
      try {
        BigDecimal wbd = new BigDecimal(MUtil.getNumber(ws));
        
        wbd = wbd.setScale(getDecimals(), 4);
        ws = wdf.format(wbd.doubleValue());
        

        while (ws.length() < getNumCar()) ws = ' ' + ws;
      }
      catch (Exception ex)
      {
        if (withError) {
          System.out.println("Atributo:" + getName() + "-" + ws + "-" + wformat);
          


          Toolkit.getDefaultToolkit().beep();
          MError werr = new MError("MIN-001030E", new String[] { ws, getName(), wformat });
          


          throw werr;
        }
      }
    }
    if ((getMask().startsWith("D")) && (ws.trim().length() != 0) && 
      (ws.indexOf("_") == -1) && (ws.indexOf("%") == -1)) {
      String wsave = ws;
      String wformat = getMask().substring(1);
      SimpleDateFormat wdatef = new SimpleDateFormat(wformat);
      try
      {
        Date myDate = wdatef.parse(ws);
        
        ws = wdatef.format(myDate);
      } catch (Exception ex) {
        if (withError) {
          System.out.println("Atributo:" + getName() + "-" + ws + "-" + wformat);
          MUtil.requestFocus(this);
          

          Toolkit.getDefaultToolkit().beep();
          MError werr = new MError("MIN-001030E", new String[] { ws, getName(), wformat });
          


          throw werr;
        }
      }
      if (!wsave.equals(ws)) {
        if ((wformat.equals("HH.mm")) && (ws.equals("00.00")) && (wsave.equals("24.00"))) {
          ws = wsave;
        } else {
          if (withError) {
            System.out.println("Atributo:" + getName() + "-" + ws + "-" + wformat);
            Toolkit.getDefaultToolkit().beep();
            MError werr = new MError("MIN-001030E", new String[] { wsave, getName(), wformat });
            


            throw werr;
          }
          ws = wsave;
        }
      }
    }
    
    return ws;
  }
  








  public String getUnFormatedText()
  {
    String ws = getText();
    
    if ((minMask.startsWith("N")) && (ws.trim().length() != 0)) {
      ws = MUtil.getNumber(ws);
    }
    return ws;
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
  

  private boolean wstatus = true;
  








  public boolean isFocusTraversable()
  {
    return wstatus;
  }
  








  public void setMode(boolean wmode)
  {
    wstatus = wmode;
  }
  








  public void setAtribFormat(String wmode)
  {
    if (getAtribType() == "1") {
      if (wmode == "P") {
        setBackground(new Color(245, 241, 223));
        setForeground(new Color(0, 0, 255));
        setEditable(false);
        setMode(false);
      }
      if (wmode == "N") {
        setBackground(MField.KEYCOLOR);
        setForeground(new Color(0, 0, 255));
        setEditable(true);
        setMode(true);
      }
    }
    if (getAtribType() == "3") {
      setFont(MField.EXPFONT);
      setBackground(MField.EXPCOLOR);
      setForeground(MField.EXPFONTCOLOR);
      setEditable(false);
      setMode(false);
    }
    if (getAtribType() == "2") {
      if (wmode == "P")
      {
        setBackground(new Color(245, 241, 223));
        setForeground(MField.DEFFONTCOLOR);
        setEditable(false);
        setMode(false);
      }
      if (wmode == "N") {
        setBackground(MField.DEFAULTCOLOR);
        setForeground(MField.DEFFONTCOLOR);
        setEditable(true);
        setMode(true);
        if (attentionFill == true) {
          setBackground(MField.ATTENTIONFILL);
        }
        if (mustFill == true) {
          setBackground(MField.MUSTFILL);
        }
      }
    }
    if ((getDisplayType() == "P") && (getAtribType() != "3")) {
      setFont(MField.EXPFONT);
      
      setBackground(new Color(245, 241, 223));
      setForeground(MField.PROTFONTCOLOR);
      setEditable(false);
      setMode(false);
    }
  }
  


















  public void setText(String t)
  {
    if (t == null) {
      t = "";
    }
    String txt = getText();
    

    if (txt.equals(t)) {
      return;
    }
    super.setText(t);
  }
}
