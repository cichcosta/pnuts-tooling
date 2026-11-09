package PT.Minimal.geral;

import java.util.Hashtable;









public class MAtribProperties
{
  private MConnection wconnection;
  private MHashtable wprop = new MHashtable(true, true);
  private String wiutl = "*";
  private String wiob = "*";
  
  public MAtribProperties(MConnection wwconnection)
  {
    String woldiobs = "";
    wconnection = wwconnection;
    
    String wcsel = "select * from sprot0  where iutl = '" + wiutl + "' " + " and iob = '" + wiob + "' " + " and iobs <> '*' " + " and cst like 'E%'" + " order by iobs, ipro";
    






    MCursor wcurs = MConnection.getMCursor(wconnection);
    try {
      wcurs.execQuery(wcsel);
      Hashtable xsprot = new Hashtable();
      Hashtable watribprop = new Hashtable();
      
      if (wcurs.getWdcode() == 0) {
        xsprot = wcurs.getRecord();
        woldiobs = (String)xsprot.get("iobs");
      }
      while (wcurs.getWdcode() == 0) {
        String wiobs = (String)xsprot.get("iobs");
        if (!woldiobs.equals(wiobs)) {
          wprop.put(woldiobs, watribprop);
          watribprop = new Hashtable();
          woldiobs = (String)xsprot.get("iobs");
        }
        watribprop.put(xsprot.get("ipro"), xsprot.get("tval"));
        wcurs.next();
        if (wcurs.getWdcode() == 0) {
          xsprot = wcurs.getRecord();
        }
      }
      if (wcurs.getWdcode() == 0) {
        wprop.put(woldiobs, watribprop);
      }
    }
    catch (MError err) {
      if ((err.getWdcode() != 65330) && (err.getWdcode() != 942)) {
        throw err;
      }
    } finally {
      wcurs.closeStatement();
      wcurs.close();
    }
  }
  
  public Object get(String wkey) {
    return wprop.get(wkey);
  }
  
  public boolean containsKey(String wkey) {
    return wprop.containsKey(wkey);
  }
}
