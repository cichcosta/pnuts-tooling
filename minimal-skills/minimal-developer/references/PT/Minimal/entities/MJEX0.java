package PT.Minimal.entities;

import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import java.util.Vector;

public class MJEX0 implements PT.Minimal.actions.MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  MForm minForm;
  Vector wrows;
  String EOL = "\n";
  
  public MJEX0() {}
  
  public void setMCommand(PT.Minimal.geral.MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError {
    if (wrows.size() == 0) {
      throw new MError("MIN-000103W");
    }
    
    if (minCommand.getName().equals("SYNCMJEX")) {
      sync_MJEX();
    }
    MError werr = new MError("MIN-000000M");
    werr.show(minForm.getMFrame());
  }
  
  public void sync_MJEX() throws MError
  {
    MForm wlogin = MForm.getMForm("Menu Login");
    wlogin.setEnt("SDOB0");
    wlogin.setAction("NONE");
    wlogin.showForm();
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    java.util.Hashtable wh = PT.Minimal.geral.MUtil.getRecord(wlogin.getCenterPanel());
    
    String wdsn = (String)wh.get("dsn");
    java.util.Properties p = PT.Minimal.geral.MUtil.myProp;
    java.util.Enumeration e; if (p.contains(wdsn)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    


    MCursor curs0 = MConnection.getMCursor();
    MCursor curs2 = MConnection.getMCursor(wdsn, (String)wh.get("user"), (String)wh.get("passwd"));
    try {
      if ((curs2.getMConnection().getURL().equals("jdbc:informix-sqli://simh2160.minimal.pt:1526/minj9801:INFORMIXSERVER=simh2160")) || (curs2.getMConnection().getURL().equals("jdbc:informix-sqli://simh2160:1526/minj9801:INFORMIXSERVER=simh2160")))
      {
        throw new MError("MIN-MJEX21E");
      }
      if (curs2.getMConnection().getURL().equals(curs0.getMConnection().getURL())) {
        throw new MError("MIN-MJEX22E");
      }
      
      for (int j = 0; j < wrows.size(); j++) {
        MRow wyrow = (MRow)wrows.elementAt(j);
        try {
          wyrow.deleteRow(curs2, false);
        } catch (MError err) {
          if ((err.getMinErrorCode() == null) || (!err.getMinErrorCode().equals("MIN-000102E")))
          {
            throw err;
          }
        }
        wyrow.insertRow(curs2, false);
        if (wyrow.hasAssociatedObject("ROWTXT", curs0)) {
          wyrow.setAssociatedObject("ROWTXT", wyrow.getAssociatedObject("ROWTXT", curs0), curs2);
        }
      }
    } finally {
      curs0.close();
      curs2.close();
    }
  }
}
