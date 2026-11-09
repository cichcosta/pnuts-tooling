package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MLogin;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;import PT.Minimal.pnuts.;
import java.io.PrintStream;
import java.util.Vector;

public class MCallPnuts implements MExternalCommand {
  MCommand minCommand;
  MForm minForm;
  Vector wrows;mpnut;
  private String[] wargs;
  private boolean isValid = true;

  private String wobj;

  private String wobjname;

  public MCallPnuts() {
  }

  public void setMCommand(MCommand wcmd) {
    minCommand = wcmd;
  }

  public void setMForm(MForm wform) {
    minForm = wform;
  }

  public void setRows(Vector wv) {
    wrows = wv;
  }

  public void execute()
    throws MError
  {
    if (MLogin.getUserTipo().indexOf("CS") != -1) {
      throw new MError("MIN-009998E", new String[] { MLogin.getUser(), "executar este comando." });
    }
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0)) {
      wargs = MUtil.getArgs(minCommand.getCallerArgs().trim(), ' ');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0)) {
      wargs = MUtil.getArgs(minCommand.getArgs(), ' ');
    }
    
    System.out.println("!*** MCalPnuts CommandArgs: " + minCommand.getCallerArgs());
    if ((wargs == null) || (wargs.length < 1)) {
      throw new MError("Numero incorrecto de parametros.");
    }
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      
      MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    if ((wrows.size() == 0) && (!wargs[1].equals("NONE"))) {
      throw new MError("MIN-000103W");
    }
    mpnut.setForm(minForm);
    mpnut.set("command", this);
    
    MConnection wconn = MConnection.getMConnection();
    MCursor mcursor = MConnection.getMCursor();
    
    mpnut.set("mcursor", mcursor);
    mpnut.set("wrows", wrows);
    try {
      if ((wargs.length < 2) || (wargs[1].equals("SEL"))) {
        for (int j = 0; j < wrows.size(); j++) {
          MRow mrow = (MRow)wrows.elementAt(j);
          
          mpnut.setRow(mrow);
          wconn.beginWork();
          mpnut.eval(wargs[0]);
          wconn.commitWork();
        }
      } else {
        wconn.beginWork();
        mpnut.eval(wargs[0]);
        wconn.commitWork();
      }
      
      if (!wargs[(wargs.length - 1)].equals("NOOK")) {
        MError werr = new MError("MIN-000000M");
        werr.show(minForm.getMFrame());
      }
    } finally {
      if (wconn.inBeginWork()) {
        wconn.rollbackWork();
      }
      mcursor.close();
      mpnut.unset("mcursor");
      mpnut.unset("wrows");
    }
  }
}
