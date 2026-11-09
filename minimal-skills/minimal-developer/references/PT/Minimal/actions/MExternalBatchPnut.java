package PT.Minimal.actions;

import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MLogin;
import PT.Minimal.pnuts.;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Vector;

public class MExternalBatchPnut implements MExternalCommand {
  private PT.Minimal.geral.MCommand minCommand;
  private PT.Minimal.geral.MForm minForm;
  private Vector wrows;
  private String[] args;
  mpnut;
  MConnection wconn;

  public MExternalBatchPnut() {
  }

  public void setMCommand(PT.Minimal.geral.MCommand wcmd) {
    minCommand = wcmd;
  }

  public void setMForm(PT.Minimal.geral.MForm wform) {
    minForm = wform;
  }

  public void setRows(Vector wv) {
    wrows = wv;
  }

  public void execute()
    throws MError
  {
    String wclient = args[0];
    String wuser = args[1];
    String wpassw = args[2];
    String wfunc = args[3].replace('\'', '"');
    

    String wfunction = wfunc.substring(0, wfunc.indexOf('('));
    String wobj = wfunction.substring(0, wfunction.indexOf('_')).toUpperCase();
    
    MLogin.loadLoginProperties();
    Properties p = MLogin.getLoginProperties();
    p.put("user.name", wuser);
    p.put("user.passwd", wpassw);
    p.put("user.dsn", wclient);
    p.put("user.xdsn", wclient);
    

    MLogin.doLogin();
    wconn = MConnection.getMConnection();
    PT.Minimal.geral.MCursor mcursor = MConnection.getMCursor();
    PT.Minimal.geral.console.MConsole.start();
    try {
      mpnut = new (wobj.toUpperCase());
      
      if (mpnut.existsFunction(wfunction)) {
        mpnut.set("mcursor", mcursor);
        wconn.beginWork();
        
        mpnut.eval(wfunc);
        wconn.commitWork();
      } else {
        System.out.println("!*** MError não existe o mpnut");
        throw new MError("MIN-LOG081E");
      }
    } catch (MError err) {
      err.printStackTrace();
      if (wconn.inBeginWork()) {
        wconn.rollbackWork();
      }
      mcursor.close();
      System.out.println(err.getMessage());
      System.out.println(err.getAuxMessage());
    }
    
    mpnut.unset("mcursor");
  }

  public static void main(String[] args) throws MError {
    try {
      MExternalBatchPnut wmain = new MExternalBatchPnut();
      args = args;
      wmain.execute();
    } catch (RuntimeException ex) {
      ex.printStackTrace();
    }

    System.out.println("!*** EOF BatchPnuts !");
    System.exit(0);
  }
}
