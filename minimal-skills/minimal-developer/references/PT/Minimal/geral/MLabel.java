package PT.Minimal.geral;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;

public class MLabel extends JLabel implements MField, java.io.Serializable
{
  private String minNome;
  private String minAnchor;
  private String minMsg = " ";
  private int minRelPosX = 0;
  private int minRelPosY = 0;
  private int minPosX = 0;
  private int minPosY = 0;
  private boolean minBreakLn = false;
  public int minWidth = 120;
  
  private String atribType = "4";
  private String dataType = "TEXTTYPE";
  private String displayType = "N";
  private boolean mustFill = false;
  private String minMask = " ";
  private String minInternalMask = "";
  private String minCmd;
  private String minCmdArgs;
  
  public MLabel() {}
  
  public MLabel(String wnome)
  {
    minNome = wnome;
    setText(minNome);
    setHorizontalAlignment(4);
    setVerticalAlignment(0);
    
    setAtrrib();
  }
  
  public MLabel(String wnome, boolean wbreakln) {
    minNome = wnome;
    minBreakLn = wbreakln;
    setText(minNome);
    setHorizontalAlignment(4);
    setVerticalAlignment(0);
    
    setAtrrib();
  }
  
  public MLabel(String wnome, int wwidth) {
    minNome = wnome;
    minWidth = wwidth;
    setText(minNome);
    setHorizontalAlignment(4);
    setVerticalAlignment(0);
    
    setAtrrib();
  }
  
  public MLabel(String wnome, boolean wbreakln, int wwidth) {
    minNome = wnome;
    minBreakLn = wbreakln;
    minWidth = wwidth;
    setText(minNome);
    setHorizontalAlignment(4);
    setVerticalAlignment(0);
    
    setAtrrib();
  }
  
  public MLabel(String wnome, boolean wbreakln, int wwidth, int walign, int wrelposx, int wrelposy) {
    minNome = wnome;
    minRelPosX = wrelposx;
    minRelPosY = wrelposy;
    minBreakLn = wbreakln;
    minWidth = wwidth;
    setText(minNome);
    setHorizontalAlignment(walign);
    
    setAtrrib();
  }
  
  public MLabel(String wnome, int wwidth, int walign) {
    minNome = wnome;
    minWidth = wwidth;
    setText(minNome);
    setHorizontalAlignment(walign);
    
    setAtrrib();
  }
  
  public void setAtrrib() { setName(minNome);
    setFont(new java.awt.Font("Dialog", 0, 11));
    setForeground(new Color(0, 80, 0));
  }
  

  public void setName(String ws)
  {
    super.setName(ws);
  }
  
  public String getName() {
    return super.getName();
  }
  
  public Dimension getMinimumSize() {
    Dimension d = super.getPreferredSize();
    


    if (width > minWidth) {
      minWidth = width;
    }
    width = minWidth;
    height = MUtil.LABEL_HEIGHT;
    return d;
  }
  
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }
  
  public void setWidth(int width) {
    minWidth = width;
  }
  
  public int getWidth() {
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
  
  public String getDataType() {
    return dataType;
  }
  
  public void setCommand(String wcmd) { minCmd = wcmd; }
  
  public String getCommand() {
    return minCmd;
  }
  
  public void setCommandArgs(String wcmdargs) {
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
  
  private boolean wstatus = false;
  
  public boolean isFocusTraversable() {
    return wstatus;
  }
  
  public void setMode(boolean wmode) {
    wstatus = wmode;
  }
  
  public void setAtribFormat(String wmode) {}
}
