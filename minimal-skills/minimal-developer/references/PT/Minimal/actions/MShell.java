package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Vector;

public class MShell implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private String[] wargs;
  private boolean isValid = true;
  
  private String wobj;
  private String wobjname;
  
  public MShell() {}
  
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
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    System.out.println(wargs.length);
    System.out.println(minCommand.getCallerArgs());
    if ((wargs == null) || (wargs.length < 1)) {
      throw new MError("Não existe opcão indicada ...");
    }
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    if (wrows.size() == 0) {
      throw new MError("MIN-000103W");
    }
    String waction = null;
    try {
      waction = mpnut.evalFormatedValues(wargs[0]);
      String wcmd = MUtil.myProp.getProperty("JSTART");
      Runtime.getRuntime().exec(waction);
    } catch (FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("FileStreamsTest: " + e);
      e.printStackTrace();
    }
  }
}
