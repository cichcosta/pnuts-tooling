package PT.Minimal.geral;

import java.util.Hashtable;
import java.util.Vector;








public class MMData
{
  public static final String P_ROW = "RW";
  public static final String P_ENT = "ME";
  public static final String P_CRIT = "MC";
  public static final String P_BROWSE = "BR";
  public static final String P_JUMP = "JP";
  
  private MMData() {}
  
  public static String getExpKey(String wicomp, String wtexp)
  {
    return MCache.getExpKey(wicomp, wtexp);
  }
  
  public static void reloadExps(String wobj) throws MError {
    MCache.removeExps(wobj);
    getExps(wobj);
  }
  
  public static void getExps(String wobj) throws MError {
    MCache.addExps(wobj, MGetMData.getExps(wobj));
  }
  
  public static MExp[] getExp(String wobj, String wcomp, String wtexp) throws MError {
    if (!MCache.containsExpObj(wobj)) {
      MCache.addExps(wobj, MGetMData.getExps(wobj));
    }
    MExp[] wexps = MCache.getExp(wobj, wcomp, wtexp);
    if ((wexps == null) && (!wcomp.equals("*"))) {
      wexps = MCache.getExp(wobj, "*", wtexp);
    }
    return wexps;
  }
  
  public static MExp[] getExpCopy(String wobj, String wcomp, String wtexp) throws MError { MExp[] wexps = getExp(wobj, wcomp, wtexp);
    if (wexps == null) {
      return null;
    }
    MExp[] wexpsCopy = new MExp[wexps.length];
    for (int i = 0; i < wexps.length; i++) {
      wexpsCopy[i] = wexps[i].copyExp();
    }
    return wexpsCopy;
  }
  
  public static void addExps(String wiob, String wcomp, String wtexp, MExp[] wexps) throws MError {
    MCache.addExps(wiob, wcomp, wtexp, wexps);
  }
  
  public static void addExps(String wiob, Hashtable wexps) throws MError { MCache.addExps(wiob, wexps); }
  



  public static String getJExpKeyObj(String wkey)
  {
    return MCache.getJExpKeyObj(wkey);
  }
  
  public static String getJExpKey(String i_ob, String i_tc, String i_comp, String i_fase) { return MCache.getJExpKey(i_ob, i_tc, i_comp, i_fase); }
  


  public static void clearJExps()
    throws MError
  {}
  

  public static void addJExps(String wobj)
    throws MError
  {
    if (!MCache.containsJExpObj(wobj.trim())) {
      MCache.addJExps(wobj.trim(), MGetMData.getJExps(wobj.trim()));
    }
  }
  
  public static Object getJExp(String wkey) throws MError {
    addJExps(MCache.getJExpKeyObj(wkey));
    return MCache.getJExp(getJExpKeyObj(wkey), wkey);
  }
  
  public static MPanel getPanel(String wpanel)
    throws MError
  {
    return getPanel(wpanel, getPanelType(wpanel), "JMETADATA");
  }
  
  public static MPanel getPanel(String wpanel, String wtype) throws MError { return getPanel(wpanel, wtype, "JMETADATA"); }
  
  public static MPanel getPanel(String wpanel, String wtype, String wconnect) throws MError {
    return MGetMData.getPanel(wpanel, wtype, wconnect);
  }
  
  public static Vector getPanelFields(String wpanel) throws MError {
    return MGetMData.getPanelFields(wpanel);
  }
  
  public static String getPanelObj(String wpanel) {
    return MGetMData.getPanelObj(wpanel);
  }
  
  public static String getPanelType(String wpanel) {
    return MGetMData.getPanelType(wpanel);
  }
  


  public static String getCodeTableName(String wictx)
    throws MError
  {
    if (!MCache.CodeTableNameLoaded()) {
      MCache.loadCodeTableName(MGetMData.getCodeTableNames());
    }
    return MCache.getCodeTableName(wictx);
  }
  
  public static Hashtable getErrorCode(String wobj) throws MError {
    if (!MCache.ErrorCodeLoaded()) {
      MCache.loadErrorCode(MGetMData.getErrorCodes());
    }
    return MCache.getErrorCode(wobj);
  }
  
  public static String getErrorConversion(String wobj, String wamb) throws MError
  {
    if (!MCache.ErrorConversionLoaded()) {
      MCache.loadErrorConversion(MGetMData.getErrorConversions());
    }
    return MCache.getErrorConversion(wobj, wamb);
  }
  









  public static Vector getCmds(String wobj)
    throws MError
  {
    return MGetMData.getCmds(wobj);
  }
  
  public static Vector getJumpFields(String wobj) throws MError {
    return MGetMData.getJumpFields(wobj);
  }
  
  public static MRow getMRow(String wobj) throws MError {
    MRow wrow = MCache.getMRow(wobj);
    if (wrow == null) {
      wrow = MGetMData.getMRow(wobj);
      MCache.addMRow(wobj, wrow);
    }
    return wrow.Copy();
  }
  
  public static Vector getRunTimeAtribs(String wobj, String wtype) throws MError
  {
    return MGetMData.getRunTimeAtribs(wobj, wtype);
  }
  



  public static Vector getRunTimeAtribs(String wobj, String wtype, String wconnect, String wtable)
    throws MError
  {
    return MGetMData.getRunTimeAtribs(wobj, wtype, wconnect, wtable);
  }
  
  public static Vector getMenuItems(String wori) throws MError {
    return MGetMData.getMenuItems(wori);
  }
}
