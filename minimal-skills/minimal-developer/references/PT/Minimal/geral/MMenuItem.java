package PT.Minimal.geral;

import java.awt.Color;

public class MMenuItem extends javax.swing.JMenuItem implements MField, java.io.Serializable
{
  public String minNome;
  public String minAnchor;
  public int minRelPosX = 0;
  public int minRelPosY = 0;
  public int minPosX = 0;
  public int minPosY = 0;
  public boolean minBreakLn = false;
  public int minWidth = 90;
  
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
  
  public MMenuItem(String wnome) {
    minNome = wnome;
    setText(minNome);
    setAtrrib();
    
    setBackground(new Color(158, 190, 231));
  }
  
  public MMenuItem(String wnome, javax.swing.ImageIcon wicon) { super(wnome, wicon);
    minNome = wnome;
    setText(minNome);
    setAtrrib();
    setBackground(new Color(158, 190, 231));
  }
  
  public MMenuItem(String wnome, String wcmd) {
    minNome = wnome;
    setText(minNome);
    setCmd(wcmd);
    setAtrrib();
    setBackground(new Color(158, 190, 231));
  }
  
  public MMenuItem(String wnome, String wlabel, String wcmd) {
    minNome = wnome;
    setText(wlabel);
    setCmd(wcmd);
    setAtrrib();
    setBackground(new Color(158, 190, 231));
  }
  
  public MMenuItem(String wnome, String wlabel, String wcmd, boolean wbreakln) { minNome = wnome;
    setText(wlabel);
    setCmd(wcmd);
    minBreakLn = wbreakln;
    setAtrrib();
    setBackground(new Color(158, 190, 231));
  }
  
  public void setAtrrib() { setName(minNome); }
  
  public void setCmd(String wcmd)
  {
    minCmd = wcmd;
    setActionCommand(minCmd);
  }
  

















  public void setMustFill(boolean wboo)
  {
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
