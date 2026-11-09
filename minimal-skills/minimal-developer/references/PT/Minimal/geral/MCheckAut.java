package PT.Minimal.geral;

import java.util.Hashtable;
import java.util.Vector;








public class MCheckAut
{
  private static Hashtable LAut = new Hashtable();
  private static Hashtable LSpecialAut = new Hashtable();
  

  public MCheckAut() {}
  
  public static boolean checkAut(String wiob, String wiobs, String wcns)
  {
    boolean waut = false;
    Boolean wspecialaut = null;
    
    if (MLogin.getUser().equals("minimal")) {
      return true;
    }
    
    if (!LAut.containsKey(wiob)) {
      loadAut(wiob);
    }
    
    wspecialaut = checkSpecialAut(wiob, wiobs, wcns, (Vector)LSpecialAut.get(wiob));
    if (wspecialaut != null) {
      return wspecialaut.booleanValue();
    }
    wspecialaut = checkSpecialAut(wiob, "*", wcns, (Vector)LSpecialAut.get(wiob));
    if (wspecialaut != null) {
      return wspecialaut.booleanValue();
    }
    

    int wz = wcns.compareTo(MLogin.getUserAutLevel());
    if (wz > 0) {
      return false;
    }
    
    waut = checkAut(wiob, wiobs, (Vector)LAut.get(wiob));
    if (!wiobs.equals("*")) {
      waut = checkAut(wiob, "*", (Vector)LAut.get(wiob));
    }
    return waut;
  }
  
  private static Boolean checkSpecialAut(String wiob, String wiobs, String wopcns, Vector wv) {
    Boolean wspecialaut = null;
    boolean waut = true;
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wsac = (Hashtable)wv.elementAt(i);
      
      String wsac_wiobs = (String)wsac.get("iobs");
      String wisac = (String)wsac.get("isac");
      if (wsac_wiobs.equals(wiobs)) {
        waut = checkAutLine("AL", (String)wsac.get("tsac"));
        if (waut) {
          String wcns = (String)wsac.get("cns");
          int wz = wopcns.compareTo(wcns);
          if (wz > 0) {
            return new Boolean(false);
          }
          if (wisac.equals("DN")) {
            waut = !waut;
          }
          return new Boolean(waut);
        }
      }
    }
    return wspecialaut;
  }
  
  private static boolean checkAut(String wiob, String wiobs, Vector wv) {
    boolean waut = true;
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wsac = (Hashtable)wv.elementAt(i);
      
      String wsac_wiobs = (String)wsac.get("iobs");
      if (wsac_wiobs.equals(wiobs)) {
        waut = checkAutLine((String)wsac.get("isac"), (String)wsac.get("tsac"));
        String wcns = (String)wsac.get("cns");
        int wz = wcns.compareTo(MLogin.getUserAutLevel());
        if (wz <= 0) break;
        return false;
      }
    }
    

    return waut;
  }
  
  public static boolean checkAutLine(String wisac, String wtsac) {
    boolean waut = false;
    
    wtsac = wtsac.toLowerCase();
    String wuser = MLogin.getUser().toLowerCase();
    String wuserNodo = MLogin.getUserNodo().toLowerCase();
    
    int i = wtsac.indexOf(wuser + ";");
    int z = wtsac.indexOf(wuserNodo + ";");
    
    if ((i != -1) || (z != -1)) {
      waut = true;
    }
    
    if (wisac.equals("DN")) {
      waut = !waut;
    }
    
    return waut;
  }
  
  private static void loadAut(String wiob) {
    MCursor ssac_curs = MConnection.getMCursor();
    

    try
    {
      String wcsel = " select iob, iobs, isact, isac, isacnl, tsac, cns from ssact0 ";
      wcsel = wcsel + " where iob in ('" + wiob + "',  '" + wiob.substring(0, 4) + "',  '" + wiob.substring(0, 1) + "')";
      wcsel = wcsel + " and isact = 'AG' ";
      wcsel = wcsel + " order by iob desc, iobs, isacnl";
      ssac_curs.execQuery(wcsel);
      
      Vector wv = ssac_curs.getAllRecords();
      LAut.put(wiob, wv);
      

      wcsel = " select iob, iobs, isact, isac, isacnl, tsac, cns from ssact0 ";
      wcsel = wcsel + " where iob in ('" + wiob + "',  '" + wiob.substring(0, 4) + "',  '" + wiob.substring(0, 1) + "')";
      wcsel = wcsel + " and isact = 'AE' ";
      wcsel = wcsel + " order by iob desc, iobs, isacnl";
      
      ssac_curs.execQuery(wcsel);
      Vector wv1 = ssac_curs.getAllRecords();
      LSpecialAut.put(wiob, wv1);
    } finally {
      ssac_curs.closeStatement();
      ssac_curs.close();
    }
  }
  
  public static void clearAut() {
    LAut.clear();
    LSpecialAut.clear();
  }
}
