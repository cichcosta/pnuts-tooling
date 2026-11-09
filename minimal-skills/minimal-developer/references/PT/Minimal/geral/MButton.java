package PT.Minimal.geral;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

public class MButton extends JButton implements MField, Serializable
{
  public String minNome;
  public String minAnchor;
  public int minRelPosX = 0;
  public int minRelPosY = 0;
  public int minPosX = 0;
  public int minPosY = 0;
  public boolean minBreakLn = false;
  public int minWidth = MUtil.BUTTON_WIDTH;
  
  public String minCmd;
  public String minCmdArgs;
  public String minTipoCmd;
  private String atribType = "4";
  private String dataType = "TEXTTYPE";
  private String displayType = "N";
  private boolean mustFill = false;
  private String minMask = " ";
  private String minInternalMask = "";
  private String minMsg = " ";
  

  private Border empty;
  

  private int i = -1;
  









  public MButton(String wnome)
  {
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(174, 204, 236));
    } else {
      setBackground(new Color(215, 228, 244));
    }
    

    empty = BorderFactory.createMatteBorder(3, 2, 2, 2, new Color(215, 228, 244));
    setBorder(empty);
    minNome = wnome;
    setText(minNome);
    setAtrrib();
    changeCursorW(this);
  }
  
  public MButton(String wnome, String wcmd)
  {
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(174, 204, 236));
    } else {
      setBackground(new Color(215, 228, 244));
    }
    
    empty = BorderFactory.createMatteBorder(3, 2, 2, 2, new Color(215, 228, 244));
    setBorder(empty);
    minNome = wnome;
    setText(minNome);
    setCmd(wcmd);
    setAtrrib();
    changeCursorW(this);
  }
  
  public MButton(String wnome, String wlabel, String wcmd)
  {
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(174, 204, 236));
    } else {
      setBackground(new Color(215, 228, 244));
    }
    setFont(new Font("Dialog", 0, 11));
    setForeground(new Color(0, 80, 0));
    
    empty = BorderFactory.createMatteBorder(3, 2, 2, 2, new Color(195, 212, 253));
    setBorder(empty);
    setText(wlabel);
    minWidth = getWidth();
    getMinimumSize();
    setWidth(minWidth);
    minNome = wnome;
    setCmd(wcmd);
    setAtrrib();
    setWidth(minWidth);
    changeCursorW(this);
  }
  
  public MButton(String wnome, String wlabel, String wcmd, boolean wbreakln)
  {
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(174, 204, 236));
    } else {
      setBackground(new Color(215, 228, 244));
    }
    
    empty = BorderFactory.createMatteBorder(3, 2, 2, 2, new Color(215, 228, 244));
    setBorder(empty);
    setText(wlabel);
    minWidth = getWidth();
    getMinimumSize();
    setWidth(minWidth);
    minNome = wnome;
    setCmd(wcmd);
    minBreakLn = wbreakln;
    setAtrrib();
    
    changeCursorW(this);
  }
  

  public MButton(String wnome, String wlabel, String wcmd, int width, boolean wbreakln)
  {
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(174, 204, 236));
    } else {
      setBackground(new Color(215, 228, 244));
    }
    setFont(new Font("Dialog", 0, 11));
    setForeground(new Color(0, 80, 0));
    
    empty = BorderFactory.createMatteBorder(3, 2, 2, 2, new Color(215, 228, 244));
    setBorder(empty);
    setText(wlabel);
    
    minWidth = getWidth();
    getMinimumSize();
    setWidth(minWidth);
    
    minNome = wnome;
    setCmd(wcmd);
    minBreakLn = wbreakln;
    setAtrrib();
    













    changeCursorW(this);
  }
  

























































  public void changeCursorW(JButton b)
  {
    b.addMouseListener(new java.awt.event.MouseListener()
    {
      public void mouseClicked(MouseEvent evt) {
        Cursor normalCursor = new Cursor(12);
        setCursor(normalCursor);
      }
      
      public void mousePressed(MouseEvent evt)
      {
        Cursor normalCursor = new Cursor(0);
        setCursor(normalCursor);
      }
      
      public void mouseReleased(MouseEvent evt)
      {
        Cursor normalCursor = new Cursor(12);
        setCursor(normalCursor);
      }
      
      public void mouseEntered(MouseEvent evt)
      {
        Cursor normalCursor = new Cursor(12);
        setCursor(normalCursor);
      }
      
      public void mouseExited(MouseEvent evt)
      {
        Cursor normalCursor = new Cursor(0);
        setCursor(normalCursor);
      }
    });
  }
  





  public void setAtrrib()
  {
    setName(minNome);
  }
  
  public void setCmd(String wcmd) {
    minCmd = wcmd;
    setActionCommand(minCmd);
  }
  
  public Dimension getMinimumSize() {
    Dimension d = super.getPreferredSize();
    if (minWidth > 90) {
      minWidth = 90;
    }
    if (width > minWidth) {
      minWidth = (width + 4);
    }
    if ((minWidth < 152) && (minWidth > 129)) {
      minWidth = 152;
    }
    if ((minWidth < 130) && (minWidth > 104)) {
      minWidth = 130;
    }
    if ((minWidth < 105) && (minWidth > 90)) {
      minWidth = 105;
    }
    if ((minWidth < 91) && (minWidth > 70)) {
      minWidth = 90;
    }
    if ((minWidth < 71) && (minWidth > 50)) {
      minWidth = 70;
    }
    if (minWidth < 51) {
      minWidth = 43;
    }
    
    if (getText().trim().length() == 1) {
      minWidth = 30;
    }
    

    width = minWidth;
    height = MUtil.BUTTON_HEIGHT;
    return d;
  }
  
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }
  
  public void setWidth(int width) { minWidth = width; }
  
  public int getWidth()
  {
    return minWidth;
  }
  
  public void setMustFill(boolean wboo) {
    mustFill = wboo;
  }
  
  public boolean getMustFill() {
    return mustFill;
  }
  
  public void setMsg(String wmsg) {
    minMsg = wmsg;
  }
  
  public String getMsg() {
    return minMsg;
  }
  
  public void setDisplayType(String ws) { displayType = ws; }
  
  public String getDisplayType()
  {
    return displayType;
  }
  
  public void setMask(String wmask) { minMask = wmask; }
  
  public String getMask()
  {
    return minMask;
  }
  
  public String getFormatedText() {
    return getText();
  }
  
  public String getUnFormatedText() {
    return getText();
  }
  
  public void setInternalMask(String wmask)
  {
    minInternalMask = wmask;
  }
  
  public String getInternalMask() {
    return minInternalMask;
  }
  
  public void setAtribType(String wat) { atribType = wat; }
  
  public String getAtribType()
  {
    return atribType;
  }
  
  public void setDataType(String ws) {
    dataType = ws;
  }
  

  public String getDataType() { return dataType; }
  
  public void setCommand(String wcmd) {
    minCmd = wcmd;
    setActionCommand(minCmd);
  }
  
  public String getCommand() { return minCmd; }
  
  public void setCommandArgs(String wcmdargs)
  {
    minCmdArgs = wcmdargs;
  }
  
  public String getCommandArgs() { return minCmdArgs; }
  
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
  
  public boolean isFocusTraversable() {
    return wstatus;
  }
  
  public void setMode(boolean wmode) {
    wstatus = wmode;
  }
  
  public void setAtribFormat(String wmode) {}
}
