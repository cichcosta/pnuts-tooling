package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import java.io.PrintStream;
import java.util.Vector;











public class MCallProcZ
  implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private String[] wargs;
  
  public MCallProcZ() {}
  
  public void setMCommand(MCommand wcmd)
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
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    if ((wargs != null) && (wargs.length == 2)) {
      String[] wtmp = new String[3];
      wtmp[0] = wargs[0];
      wtmp[1] = "-";
      wtmp[2] = wargs[1];
      wargs = wtmp;
    }
    if ((wargs == null) || (wargs.length < 3)) {
      throw new MError(minCommand.getName() + ": Número de argumentos invalidos");
    }
    
    String wcommand = wargs[0];
    String wfase = wargs[1];
    String wparms = wargs[2];
    
    if ("-".equals(wcommand)) {
      wcommand = minCommand.getName();
    }
    if ("-".equals(wfase)) {
      wfase = "BF";
    }
    
    System.out.println("MCallProcZ " + minCommand.getName() + ": " + wcommand + ", " + wfase + ", " + wparms);
    MCursor mcursor = MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        mcursor.beginWork();
        MRow wr = (MRow)wrows.elementAt(j);
        wr.setZCommand(wcommand, wfase, wparms, mcursor);
        wr.refreshRow(mcursor);
        mcursor.commitWork();
      }
    } finally {
      if (mcursor.inBeginWork()) {
        mcursor.rollbackWork();
      }
      mcursor.close();
    }
  }
}
