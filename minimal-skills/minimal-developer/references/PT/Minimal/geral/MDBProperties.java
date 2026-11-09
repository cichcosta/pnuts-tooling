package PT.Minimal.geral;

import java.util.Hashtable;















public class MDBProperties
{
  private MConnection wconnection;
  private MHashtable wprop = new MHashtable(true, true);
  private String wiutl = "*";
  private String wiob = "*";
  private String wiobs = "*";
  
  public MDBProperties(MConnection wwconnection)
  {
    wconnection = wwconnection;
    
    MCursor wcurs = MConnection.getMCursor(wconnection);
    try
    {
      String wcsel = "select * from sprot0  where iutl = '" + wiutl + "' " + " and iob = '" + wiob + "' " + " and iobs = '" + wiobs + "' " + " and cst like 'E%'";
      






      wcurs.execQuery(wcsel);
      for (; 
          wcurs.getWdcode() == 0; wcurs.next()) {
        Hashtable xsprot = wcurs.getRecord();
        wprop.put(xsprot.get("ipro"), xsprot.get("tval"));
      }
    }
    catch (MError err)
    {
      if ((err.getWdcode() != 65330) && (err.getWdcode() != 942)) {
        throw err;
      }
    } finally {
      wcurs.closeStatement();
      wcurs.close();
    }
  }
  
  public void saveProperty(String wkey) {
    MCursor wcurs = MConnection.getMCursor(wconnection);
    try {
      saveProperty(wkey, wcurs);
    }
    catch (MError err) {
      if ((err.getWdcode() != 65330) && (err.getWdcode() != 942)) {
        throw err;
      }
    } finally {
      wcurs.close();
    }
  }
  

  public void saveProperty(String wkey, MCursor wcurs)
  {
    String wcsel = "update sprot0  set tval = '" + wprop.getString(wkey) + "' " + " where iutl = '" + wiutl + "' " + " and iob = '" + wiob + "' " + " and iobs = '" + wiobs + "' " + " and ipro = '" + wkey + "' ";
    





    try
    {
      int wn = wcurs.execStatement(wcsel);
      if (wn == 0) {
        wcurs.closeStatement();
        wcsel = "insert into sprot0 values ( 0 , user, '0000', '" + MUtil.getToday("HH:mm:ss") + "'" + ", '" + MUtil.getToday() + "', 'ro'" + ", '" + wiutl + "', '" + wiob + "', '" + wiobs + "'" + ", '" + wkey + "', '" + wprop.getString(wkey) + "'" + ", 'E0', '" + MUtil.getToday() + "', 'MN')";
        






        wcurs.execStatement(wcsel);
      }
    } finally {
      wcurs.closeStatement();
    }
  }
  
  public Object get(String wkey) {
    return wprop.get(wkey);
  }
  
  public Object put(String wkey, Object wval) {
    return wprop.put(wkey, wval);
  }
  
  public boolean containsKey(String wkey) {
    return wprop.containsKey(wkey);
  }
}
