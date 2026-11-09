package PT.Minimal.geral;

import java.io.PrintStream;
import java.math.BigDecimal;











public class MSerializedObject
{
  public static String METASERPATH = "JMETADATA";
  public static String LOCALSERPATH = "DEFAULT";
  
  public static final String GLOBAL = "GLOBAL";
  
  public static final String OBJECT = "OBJECT";
  public static final String RTF = "RTF";
  public static final String MEN = "MEN";
  public static final String ROW = "ROW";
  public static final String ENT = "ENT";
  public static final String ERR = "ERR";
  private static boolean isOn = false;
  
  private static MHashtable LCache = new MHashtable();
  

  public MSerializedObject() {}
  

  public static boolean existsObject(String wconnect, String wtype, String wtable, String wname)
  {
    if (!isOn) {
      return false;
    }
    
    String wkey = getKey(wconnect, wtype, wname);
    
    MZipIn wzipin = (MZipIn)LCache.get(wkey);
    
    String wobjname = wtable + wname;
    
    if (wobjname.trim() == "MEXSBGEO*0") {
      wobjname = "MEXSBGEO*";
    }
    
    return wzipin.contains(wobjname);
  }
  



  public static boolean existsObject(String wconnect, String wtype, String wname)
  {
    if (!isOn) {
      return false;
    }
    
    String wkey = getKey(wconnect, wtype, wname);
    
    MZipIn wzipin = (MZipIn)LCache.get(wkey);
    

    return wzipin.contains(wname);
  }
  
  public static Object getObject(String wconnect, String wtype, String wtable, String wname) {
    Object wobj = null;
    



    if (!isOn) {
      return null;
    }
    
    String wkey = getKey(wconnect, wtype, wname);
    
    MZipIn wzipin = (MZipIn)LCache.get(wkey);
    String wobjname = wtable + wname;
    System.out.println("!*** MSerializedObject Vai devolver o " + wobjname);
    return wzipin.getObject(wobjname);
  }
  
  public static Object getObject(String wconnect, String wtype, String wname) {
    Object wobj = null;
    



    if (!isOn) {
      return null;
    }
    
    String wkey = getKey(wconnect, wtype, wname);
    
    MZipIn wzipin = (MZipIn)LCache.get(wkey);
    System.out.println("!*** MSerializedObject Vai devolver o " + wname);
    return wzipin.getObject(wname);
  }
  










  public static void saveObject(String wserpath, String wtype, String wtable, String wname, Object wobj) {}
  









  public static MZipIn loadZipFile(String wific, String wifict, String wconnect)
    throws MError
  {
    byte[] wtext = null;
    


    String wcsel = "select zk from mfict0  where ific = '" + wific + "'" + "   and ifict = '" + wifict + "'" + "   and cmeta = '" + wconnect + "'";
    





    MCursor mrfc_con = MConnection.getMCursor();
    try
    {
      System.out.println("!*** MSerializedObject query: " + wcsel);
      mrfc_con.execQuery(wcsel);
      if (mrfc_con.getWdcode() == 0) {
        MHashtable mfic = mrfc_con.getRecord();
        mrfc_con.closeStatement();
        BigDecimal wzk = mfic.getBigDecimal("zk");
        wtext = MUtil.getAssociatedObject("MFIC", wzk, "FICH_ROW", mrfc_con);
      }
    } finally {
      mrfc_con.closeStatement();
      mrfc_con.close();
    }
    return new MZipIn(wtext);
  }
  


  public static String getKey(String wconnect, String wtype, String wname)
  {
    String wfile = wname;
    if (((wtype.equals("ENT")) && (wname.length() == 6) && (wname.endsWith("RW"))) || (wname.length() <= 4))
    {
      wtype = "ROW";
      if (wname.length() > 4) {
        wfile = wname.substring(0, 4);
      }
    }
    if ((wtype.equals("ENT")) && (wname.length() > 5)) {
      wfile = wname.substring(0, 5);
    }
    
    String wkey = wconnect + wtype + wfile;
    if (!LCache.containsKey(wkey)) {
      LCache.put(wkey, loadZipFile(wfile, wtype, wconnect));
    }
    
    return wkey;
  }
  
  public static void setMode(boolean wboo)
  {
    isOn = wboo;
  }
  
  public static boolean getMode() {
    return isOn;
  }
}
