package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.sql.MSqlProc;
import java.io.PrintStream;
import java.util.Vector;




























public class MCallProc
  implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private String[] wargs;
  
  public MCallProc() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  








  public void setMForm(MForm wform)
  {
    minForm = wform;
  }
  








  public void setRows(Vector wv)
  {
    wrows = wv;
  }
  







  public void execute()
    throws MError
  {
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0)) {
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0)) {
      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    System.out.println(wargs.length);
    System.out.println(minCommand.getCallerArgs());
    if ((wargs == null) || (wargs.length < 1)) {
      throw new MError("Argumentos invalidos");
    }
    
    boolean parms = (wargs.length > 0) && ("KEY".equals(wargs[1]));
    String name = wargs[0];
    
    MConnection mconn = MConnection.getMConnection();
    MSqlProc proc = new MSqlProc(mconn);
    try
    {
      proc.prepare(name, parms ? 2 : 0);
      
      int i = 0; for (int n = wrows.size(); i < n; i++) {
        mconn.beginWork();
        if (parms) {
          MRow mrow = (MRow)wrows.elementAt(i);
          
          proc.setParameters(new Object[] { mrow.getEnt(), mrow.get("zk") });
        }
        

        proc.execute();
        mconn.commitWork();
      }
    } finally {
      if (mconn.inBeginWork()) {
        mconn.rollbackWork();
      }
      proc.close();
    }
  }
}
