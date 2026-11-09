package PT.Minimal.actions;

import PT.Minimal.geral.MError;
import java.util.Enumeration;
import java.util.Hashtable;







public class MRegister
{
  private static Hashtable LAction = new Hashtable();
  

  private static Hashtable JAction = new Hashtable();
  
  public MRegister() {}
  
  public static void registerAction(String wobj, MRegisteredAction wraction) {
    LAction.put(wobj, wraction);
  }
  
  public static void checkInterest(String wobj)
    throws MError
  {
    MRegisteredAction wraction = (MRegisteredAction)LAction.get(wobj);
    if (wraction != null) {
      wraction.registerCall(wobj);
      LAction.remove(wobj);
      wraction = null;
    }
  }
  
  public static void checkJump(String wfrom, String wto)
  {
    if ((wto.length() == 5) && (!wto.startsWith("SOUT9"))) {
      if (wfrom.length() > 5) wfrom = wfrom.substring(0, 5);
      if (wto.length() > 5) wto = wto.substring(0, 5);
      if ((JAction.containsKey(wto)) || (JAction.containsValue(wto))) {
        throw new MError("Entidade " + wto + " já aberta.");
      }
      JAction.put(wfrom, wto);
    }
  }
  
  public static void removeJump(String wto)
  {
    if (wto.length() > 5) wto = wto.substring(0, 5);
    for (Enumeration e = JAction.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (wkey.equals(wto)) {
        JAction.remove(wkey);
      }
      if ((JAction.containsKey(wkey)) && (JAction.get(wkey).equals(wto)))
        JAction.remove(wkey);
    }
  }
  
  public static void clearJump() {
    JAction.clear();
  }
}
