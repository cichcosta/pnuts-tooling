package PT.Minimal.geral;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.TextField;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MBTextField
  extends TextField implements MTextComponent
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
  private MTextFieldEditingPolicy wep;
  
  public MBTextField() {}
  
  public MBTextField(MTextFieldEditingPolicy wwep)
  {
    wep = wwep;
    setAtrrib();
  }
  
  public MTextFieldEditingPolicy getMTextFieldEditingPolicy() {
    return wep;
  }
  
  public void setAtrrib()
  {
    setName(wep.getMBrowseColumn().getColName());
    setFont(DEFAULTFONT);
    setBackground(DEFAULTCOLOR);
    setForeground(DEFFONTCOLOR);
    setAtribFormat("N");
  }
  
  public void transferFocus() {
    wep.moveForward();
  }
  
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }
  
  public int getNumCar()
  {
    return minWidth;
  }
  
  public void setNumCar(int width) {
    minWidth = width;
  }
  
  public void setInternalWidth(int width) {
    minInternalWidth = width;
  }
  
  public int getInternalWidth() {
    return wep.getMBrowseColumn().getInternalWidth();
  }
  
  public void setName(String ws) {
    super.setName(ws);
  }
  
  public String getName() {
    return super.getName();
  }
  
  public void setMustFill(boolean wboo) {
    mustFill = wboo;
  }
  
  public boolean getMustFill() {
    return mustFill;
  }
  
  public void setChanged(boolean wchanged) {
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
  
  public boolean getChanged() {
    return isChanged;
  }
  
  public void setMsg(String wmsg) {
    minMsg = wmsg;
  }
  
  public String getMsg() {
    String wmsg = minMsg;
    if (mustFill) {
      wmsg = "(m) " + wmsg;
    }
    if (getPromptCode().trim().length() != 0) {
      wmsg = "(?) " + wmsg;
    }
    return wmsg;
  }
  
  public void setDisplayType(String ws) {
    displayType = ws;
  }
  
  public String getDisplayType() {
    return wep.getMBrowseColumn().getDisplayType();
  }
  
  public void setMask(String wmask) { minMask = wmask; }
  
  public String getMask()
  {
    return wep.getMBrowseColumn().getMask();
  }
  
  public String getFormatedText() {
    return getFormatedText(getText());
  }
  
  public void setDecimals(int wdecimals) {
    minDecimals = wdecimals;
  }
  
  public int getDecimals() {
    return minDecimals;
  }
  
  public String getFormatedText(String ws) {
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
      catch (Exception ex) {
        MUtil.requestFocus(this);
        selectAll();
        MError werr = new MError("MIN-001030E");
        throw werr;
      }
    }
    if ((getMask().startsWith("D")) && (ws.trim().length() != 0) && 
      (ws.indexOf("_") == -1) && (ws.indexOf("%") == -1))
    {
      String wformat = getMask().substring(1);
      SimpleDateFormat wdatef = new SimpleDateFormat(wformat);
      try {
        Date myDate = wdatef.parse(ws);
        ws = wdatef.format(myDate);
      } catch (Exception ex) {
        MUtil.requestFocus(this);
        selectAll();
        MError werr = new MError("MIN-001030E");
        throw werr;
      }
    }
    
    return ws;
  }
  
  public String getUnFormatedText() {
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
  
  public String getInternalMask() {
    return minInternalMask;
  }
  
  public void setAtribType(String wat) { atribType = wat; }
  
  public String getAtribType()
  {
    return wep.getMBrowseColumn().getAtribType();
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
  
  public void setPromptCode(String ws) {
    wiexs = ws;
  }
  
  public String getPromptCode() {
    return wep.getMBrowseColumn().getPromptCode();
  }
  
  public void setPromptTable(String ws) {
    wictx = ws;
  }
  
  public String getPromptTable() {
    return wep.getMBrowseColumn().getPromptTable();
  }
  
  public void setExpansionField(String ws) {
    wniob = ws;
  }
  
  public String getExpansionField() {
    return wep.getMBrowseColumn().getExpansionField();
  }
  
  public void setPromptControl(String wpc) {
    wxajp = wpc;
  }
  
  public String getPromptControl() {
    return wep.getMBrowseColumn().getPromptControl();
  }
  
  public void setRule(String wrg) {
    wxrg = wrg;
  }
  
  public String getRule() {
    return wep.getMBrowseColumn().getRule();
  }
  
  public void setPromptArgs(String wargs) {
    wexsargs = wargs;
  }
  
  public String getPromptArgs() {
    return wep.getMBrowseColumn().getPromptArgs();
  }
  
  private boolean wstatus = true;
  
  public boolean isFocusTraversable() {
    return wstatus;
  }
  
  public void setMode(boolean wmode) {
    wstatus = wmode;
  }
  
  public void setAtribFormat(String wmode) {
    if (getAtribType() == "1") {
      if (wmode == "P") {
        setBackground(MField.EXPCOLOR);
        setEditable(false);
        setMode(false);
      }
      if (wmode == "N") {
        setBackground(MField.KEYCOLOR);
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
        setBackground(MField.DEFAULTCOLOR);
        setEditable(false);
        setMode(false);
      }
      if (wmode == "N") {
        setBackground(MField.DEFAULTCOLOR);
        setEditable(true);
        setMode(true);
      }
    }
    if ((getDisplayType() == "P") && (getAtribType() != "3")) {
      setFont(MField.EXPFONT);
      
      setBackground(MField.DEFAULTCOLOR);
      setForeground(MField.PROTFONTCOLOR);
      setEditable(false);
      setMode(false);
    }
  }
}
