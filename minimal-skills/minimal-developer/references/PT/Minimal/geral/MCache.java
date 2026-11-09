package PT.Minimal.geral;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;









public class MCache
{
  private static Hashtable LJExp = new Hashtable();
  private static Hashtable LExp = new Hashtable();
  private static Hashtable LRow = new Hashtable();
  private static Hashtable LCodeTableName = new Hashtable();
  private static Hashtable LErrorCode = new Hashtable();
  private static Hashtable LErrorConversion = new Hashtable();
  
  public static final char JEXP_SEP = '+';
  
  public static final char EXP_SEP = '+';
  

  private MCache() {}
  

  public static String getJExpKeyObj(String key)
  {
    int i = key.indexOf('+');
    if (i <= 0) throw new MError("Chave de Pnut inválida: " + key);
    return key.substring(0, i);
  }
  
  public static String getJExpKey(String i_ob, String i_tc, String i_comp, String i_fase) { return (i_ob.trim() + '+' + i_tc.trim() + '+' + i_comp.trim() + '+' + i_fase.trim()).toUpperCase(); }
  
  public static boolean containsJExpObj(String wobj)
  {
    return LJExp.containsKey(wobj);
  }
  

  public static void clearJExps()
  {
    LJExp.clear();
  }
  
  public static Enumeration getJExpObjs() { return LJExp.keys(); }
  
  public static void addJExps(String wobj, Hashtable wjexps)
  {
    LJExp.put(wobj.trim(), wjexps);
  }
  
  public static Object getJExp(String wobj, String wkey) {
    Hashtable wjexps = (Hashtable)LJExp.get(wobj);
    





    if ((wjexps != null) && (wjexps.get(wkey) != null)) {
      return wjexps.get(wkey);
    }
    return null;
  }
  
  public static boolean containsJExp(String wobj, String wkey) throws MError {
    Hashtable wjexps = (Hashtable)LJExp.get(wobj);
    




    return wjexps.containsKey(wkey);
  }
  




  public static String getExpKey(String wicomp, String wtexp)
  {
    return wicomp.trim().toUpperCase() + '+' + wtexp.trim().toUpperCase();
  }
  
  public static boolean containsExpObj(String wobj) throws MError {
    return LExp.containsKey(wobj);
  }
  
  public static Enumeration getExpObjs() { return LExp.keys(); }
  
  public static void removeExps(String wiob) {
    LExp.remove(wiob);
  }
  
  public static void addExps(String wiob, Hashtable wexps) throws MError
  {
    LExp.put(wiob, wexps);
  }
  
  public static void addExps(String wiob, String wicomp, String wtexp, MExp[] wexps) throws MError {
    Hashtable wobj = (Hashtable)LExp.get(wiob);
    if (wobj == null) {
      wobj = new Hashtable();
      LExp.put(wiob, wobj);
    }
    wobj.put(getExpKey(wicomp, wtexp), wexps);
  }
  
  public static MExp[] getExp(String wiob, String wicomp, String wtexp) throws MError {
    Hashtable wexps = (Hashtable)LExp.get(wiob);
    if (wexps == null) {
      MError err = new MError("MIN-000000E", new String[] { " - Cache." });
      err.setMessage("Objecto " + wiob + " não encontrado na cache.");
      throw err;
    }
    MExp[] wexpl = (MExp[])wexps.get(getExpKey(wicomp, wtexp));
    
    return wexpl;
  }
  
  public static boolean containsExp(String wiob, String wicomp, String wtexp) throws MError { Hashtable wexps = (Hashtable)LExp.get(wiob);
    if (wexps == null) {
      MError err = new MError("MIN-000000E", new String[] { " - Cache." });
      err.setMessage("Objecto " + wiob + " não encontrado na cache.");
      throw err;
    }
    return wexps.containsKey(getExpKey(wicomp, wtexp));
  }
  
  public static void addMRow(String iob, MRow row)
  {
    LRow.put(iob, row);
  }
  
  public static MRow getMRow(String iob) { return (MRow)LRow.get(iob); }
  
  public static void removeMRow(String wobj) {
    LRow.remove(wobj);
  }
  
  public static boolean CodeTableNameLoaded() throws MError
  {
    return !LCodeTableName.isEmpty();
  }
  
  public static String getCodeTableName(String wiob) throws MError {
    if (LCodeTableName.containsKey(wiob)) {
      return (String)LCodeTableName.get(wiob);
    }
    return "Tabela de Códigos";
  }
  
  public static void loadCodeTableName(Vector wv)
    throws MError
  {
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      LCodeTableName.put(wh.get("ictx"), wh.get("nctx"));
    }
  }
  
  public static boolean ErrorCodeLoaded() throws MError
  {
    return !LErrorCode.isEmpty();
  }
  
  public static Hashtable getErrorCode(String wiob) throws MError {
    return (Hashtable)LErrorCode.get(wiob);
  }
  
  public static void loadErrorCode(Vector wv) throws MError
  {
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      LErrorCode.put(wh.get("ierr"), wh);
    }
  }
  
  public static boolean ErrorConversionLoaded() throws MError
  {
    return !LErrorConversion.isEmpty();
  }
  
  public static String getErrorConversion(String wobj, String wamb) throws MError {
    return (String)LErrorConversion.get(wobj + wamb);
  }
  
  public static void loadErrorConversion(Vector wv) throws MError
  {
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      LErrorConversion.put((String)wh.get("ierc") + (String)wh.get("iamb"), wh.get("ierr"));
    }
  }
  
  public static void removeAll()
  {
    LJExp.clear();
    LExp.clear();
    LRow.clear();
    LCodeTableName.clear();
    LErrorCode.clear();
    LErrorConversion.clear();
  }
}
