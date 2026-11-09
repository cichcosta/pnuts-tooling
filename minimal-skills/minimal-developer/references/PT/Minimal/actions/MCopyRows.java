package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MRow;

public class MCopyRows implements MExternalCommand
{
  MCommand minCommand;
  PT.Minimal.geral.MForm minForm;
  java.util.Vector wrows;
  String EOL = "\n";
  private String[] wargs;
  
  public MCopyRows() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(PT.Minimal.geral.MForm wform) {
    minForm = wform;
  }
  
  public void setRows(java.util.Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError {
    if (wrows.size() == 0) {
      throw new MError("MIN-000103W");
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0)) {
      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getCallerArgs(), ' ');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0)) {
      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getArgs(), ' ');
    }
    copy_rows();
    MError werr = new MError("MIN-000000M");
    werr.show(minForm.getMFrame());
  }
  
  public void copy_rows() throws MError {
    String[] wparms = PT.Minimal.geral.MUtil.getConnectionParameters();
    MCursor curs0 = MConnection.getMCursor();
    MCursor curs2 = MConnection.getMCursor(wparms[0], wparms[1], wparms[2]);
    try
    {
      if ((curs2.getMConnection().getURL().equals("jdbc:informix-sqli://simh2160.minimal.pt:1526/minj9801:INFORMIXSERVER=simh2160")) || (curs2.getMConnection().getURL().equals("jdbc:informix-sqli://simh2160:1526/minj9801:INFORMIXSERVER=simh2160")))
      {
        throw new MError("MIN-000021E");
      }
      if ((curs2.getMConnection().getURL().equals(curs0.getMConnection().getURL())) && (curs2.getMConnection().getUser().equals(curs0.getMConnection().getUser()))) {
        throw new MError("MIN-000022E");
      }
      
      for (int j = 0; j < wrows.size(); j++) {
        MRow wyrow = ((MRow)wrows.elementAt(j)).Copy();
        try {
          wyrow.deleteRow(curs2, false);
        } catch (MError err) {
          if ((err.getMinErrorCode() == null) || (!err.getMinErrorCode().equals("MIN-000102E")))
          {
            throw err;
          }
        }
        if (wargs[0].trim().equalsIgnoreCase("szk")) {
          wyrow.setField("zk", "0");
        }
        wyrow.insertRow(curs2, false);
        if (wyrow.hasAssociatedObject("ROWTXT", curs0)) {
          wyrow.setAssociatedObject("ROWTXT", wyrow.getAssociatedObject("ROWTXT", curs0), curs2);
        }
        
        if ((wyrow.getEnt().toLowerCase().startsWith("sdob")) && 
          (wyrow.hasAssociatedObject("OBJDDL", curs0))) {
          wyrow.setAssociatedObject("OBJDDL", wyrow.getAssociatedObject("OBJDDL", curs0), curs2);
        }
        
        if ((wyrow.getEnt().toLowerCase().startsWith("mfic")) && 
          (wyrow.hasAssociatedObject("FICH_ROW", curs0))) {
          wyrow.setAssociatedObject("FICH_ROW", wyrow.getAssociatedObject("FICH_ROW", curs0), curs2);
        }
      }
    }
    finally
    {
      curs0.close();
      curs2.close();
      curs2.getMConnection().closeConnection();
    }
  }
}
