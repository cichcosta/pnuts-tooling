package PT.Minimal.geral;

import javax.swing.JCheckBox;

public class MCheckBox extends JCheckBox implements MField, java.io.Serializable
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
  
  public MCheckBox(String wnome) {
    minNome = wnome;
    super.setText(minNome);
    setAtrrib();
  }
  
  public MCheckBox(String wnome, boolean wbreakln) {
    minNome = wnome;
    super.setText(minNome);
    minBreakLn = wbreakln;
    setAtrrib();
  }
  
  public MCheckBox(String wnome, String wlabel, boolean wbreakln) { minNome = wnome;
    super.setText(wlabel);
    minBreakLn = wbreakln;
    setAtrrib();
  }
  
  public MCheckBox(String wnome, String wcmd)
  {
    minNome = wnome;
    super.setText(minNome);
    setCmd(wcmd);
    setAtrrib();
  }
  
  public MCheckBox(String wnome, String wlabel, String wcmd) {
    minNome = wnome;
    super.setText(wlabel);
    setCmd(wcmd);
    setAtrrib();
  }
  
  public MCheckBox(String wnome, String wlabel, String wcmd, boolean wbreakln) { minNome = wnome;
    super.setText(wlabel);
    setCmd(wcmd);
    minBreakLn = wbreakln;
    setAtrrib();
  }
  
  public void setAtrrib() { setName(minNome); }
  
  public void setCmd(String wcmd)
  {
    minCmd = wcmd;
    setActionCommand(minCmd);
  }
  
  public java.awt.Dimension getMinimumSize() {
    java.awt.Dimension d = super.getPreferredSize();
    if (width > minWidth) {
      minWidth = width;
    }
    width = minWidth;
    height = MUtil.CHECKBOX_HEIGHT;
    return d;
  }
  
  public java.awt.Dimension getPreferredSize() {
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
    if (isSelected()) {
      return "S";
    }
    return "N";
  }
  
  public String getUnFormatedText()
  {
    return getFormatedText();
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
  

  public void setMode(boolean wmode) { wstatus = wmode; }
  
  public void setAtribFormat(String wmode) {
    if (getAtribType() == "1") {
      if (wmode == "P") {
        setBackground(MField.EXPCOLOR);
        setEnabled(false);
        setMode(false);
      }
      if (wmode == "N") {
        setBackground(MField.KEYCOLOR);
        setEnabled(true);
        setMode(true);
      }
    }
    if (getAtribType() == "3") {
      setFont(MField.EXPFONT);
      setBackground(MField.EXPCOLOR);
      setForeground(MField.EXPFONTCOLOR);
      setEnabled(false);
      setMode(false);
    }
    if (getAtribType() == "2") {
      if (wmode == "P") {
        setBackground(MField.EXPCOLOR);
        setEnabled(false);
        setMode(false);
      }
      if (wmode == "N") {
        setBackground(MField.DEFAULTCOLOR);
        setEnabled(true);
        setMode(true);
      }
    }
    if ((getDisplayType() == "P") && (getAtribType() != "3")) {
      setFont(MField.EXPFONT);
      setBackground(MField.EXPCOLOR);
      setForeground(MField.PROTFONTCOLOR);
      setEnabled(false);
      setMode(false);
    }
  }
}
