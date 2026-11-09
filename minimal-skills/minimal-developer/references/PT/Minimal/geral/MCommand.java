package PT.Minimal.geral;

import java.util.Hashtable;

public class MCommand
{
  private String wiob;
  private String wname;
  private String wtype;
  private String wcmd;
  private String word;
  private String wargs;
  private String wcaller_args;
  private String[] wobj_args;
  private String wtmenu;
  private String wtpref;
  private String wthelp;
  private String wzfase;
  private String wokcode;
  private String wfailcode;
  private String wnextcmd;
  private String wcmdAutLevel;
  private Hashtable htTran;
  
  public MCommand(String iob, String name) {
    wiob = iob;
    wname = name;
  }
  
  public String getObj() {
    return wiob;
  }
  
  public String getName() {
    return wname;
  }
  
  public void setName(String ws) { wname = ws; }
  
  public String getType() {
    return wtype;
  }
  
  public void setType(String ws) {
    wtype = ws;
  }
  
  public String getCmd() {
    return wcmd;
  }
  
  public void setCmd(String ws) {
    wcmd = ws;
  }
  
  public String getOrder() {
    return word;
  }
  
  public void setOrder(String ws) {
    word = ws;
  }
  
  public String getArgs() {
    return wargs;
  }
  
  public void setArgs(String ws) {
    wargs = ws;
  }
  
  public String getCallerArgs() {
    return wcaller_args;
  }
  
  public void setCallerArgs(String ws) {
    wcaller_args = ws;
  }
  
  public String[] getObjArgs() {
    return wobj_args;
  }
  
  public void setObjArgs(String[] ws) {
    wobj_args = ws;
  }
  
  public String getMenuText()
  {
    htTran = MGetMData.getTran();
    if ((htTran != null) && (htTran.containsKey(wtmenu)) && 
      (!((String)htTran.get(wtmenu)).trim().equals("*"))) {
      wtmenu = ((String)htTran.get(wtmenu));
    }
    

    return wtmenu;
  }
  
  public void setMenuText(String ws) {
    wtmenu = ws;
  }
  
  public String getPrefText() {
    return wtpref;
  }
  
  public void setPrefText(String ws) {
    wtpref = ws;
  }
  
  public String getHelpText() {
    return wthelp;
  }
  
  public void setHelpText(String ws) {
    wthelp = ws;
  }
  
  public String getZFase() { return wzfase; }
  
  public void setZFase(String ws)
  {
    wzfase = ws;
  }
  
  public String getOkCode() { return wokcode; }
  
  public void setOkCode(String ws) {
    wokcode = ws;
  }
  
  public String getFailCode() { return wfailcode; }
  
  public void setFailCode(String ws) {
    wfailcode = ws;
  }
  
  public String getNextCommand() { return wnextcmd; }
  
  public void setNextCommand(String ws) {
    wnextcmd = ws;
  }
  
  public String getCommandAutLevel() { return wcmdAutLevel; }
  
  public void setCommandAutLevel(String ws) {
    wcmdAutLevel = ws;
  }
}
