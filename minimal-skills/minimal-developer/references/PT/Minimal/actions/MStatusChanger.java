package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.pnuts.;
import java.io.PrintStream;
import java.util.Date;
import java.util.Vector;

public class MStatusChanger implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private String wName;
  private String wOkCode;
  private String wFailCode;
  private String wNextCmd;
  private String wArgLine;
  private String[] wargs;
  private String wzfase;
  
  public MStatusChanger() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
    
    wName = wcmd.getName().trim();
    wOkCode = wcmd.getOkCode().trim();
    wFailCode = wcmd.getFailCode().trim();
    wNextCmd = wcmd.getNextCommand().trim();
    wArgLine = wcmd.getArgs().trim();
    
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getArgs(), ',');
    }
    

    wzfase = wcmd.getZFase();
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError
  {
    if (PT.Minimal.geral.MLogin.getUserTipo().indexOf("CS") != -1) {
      throw new MError("MIN-009998E", new String[] { PT.Minimal.geral.MLogin.getUser(), "executar este comando." });
    }
    

    System.out.println(wargs.length);
    System.out.println(wArgLine);
    
    if (wargs.length < 6) {
      throw new MError("MIN-900000E");
    }
    if (wrows.size() == 0) {
      throw new MError("MIN-000103W");
    }
     mpnut = .getMPnut(minForm.getEnt());
    
    PT.Minimal.geral.MCursor wupdate = PT.Minimal.geral.MConnection.getMCursor();
    try {
      mpnut.set("cursor", wupdate);
      mpnut.set("wobjargs", minCommand.getObjArgs());
      mpnut.setForm(minForm);
      mpnut.set("wrows", wrows);
      mpnut.evalPnut(minForm.getEnt(), "MC", wName, "BC");
      for (int j = 0; j < wrows.size(); j++) {
        MRow wrold = (MRow)wrows.elementAt(j);
        if (wrold.isValidRow())
        {

          MRow wr = wrold.Copy();
          String wcst = (String)wr.getField(wargs[0]);
          String wcste = wcst;
          
          boolean isValid = false;
          
          for (int i = 3; i + 2 < wargs.length; i += 3) {
            System.out.println("wcst: <" + wcst + "> i: " + i + ", wargs[i] <" + wargs[i] + ">");
            String weq = "=";
            if ((wcst.equals(wargs[i])) || (weq.equals(wargs[i]))) {
              isValid = true;
              break;
            }
          }
          if (!isValid) {
            if ((wFailCode != null) && (wFailCode.trim().length() != 0)) {
              throw new MError(wFailCode);
            }
            throw new MError("MIN-021000E");
          }
          
          if (!wargs[(i + 1)].equals("=")) {
            wcst = wargs[(i + 1)];
          }
          if (!wargs[(i + 2)].equals("=")) {
            wcste = wargs[(i + 2)];
          }
          
          wr.setField(wargs[0], wcst);
          wr.setField(wargs[1], PT.Minimal.geral.MUtil.getToday("yyyy-MM-dd"));
          if (!wargs[2].equals("=")) {
            wr.setField("zcst", wargs[2]);
          }
          MError wzerr = null;
          wupdate.beginBatch();
          System.out.println("1 - beginBatch(): " + new Date());
          try {
            mpnut.set("rowold", wrold);
            mpnut.setRow(wrold);
            mpnut.setRow(wr);
            mpnut.set("cursor", wupdate);
            mpnut.setForm(minForm);
            mpnut.set("wrows", wrows);
            System.out.println("2 - evalPnut BA: " + new Date());
            mpnut.evalPnut(minForm.getEnt(), "MC", wName, "BA");
            System.out.println("3 - Inicio update: " + new Date());
            wrold.updateRow(wupdate, wr);
            System.out.println("4 - Fim update: " + new Date());
            
            if ((minCommand.getZFase().startsWith("AF")) || (minCommand.getZFase().startsWith("BT"))) {
              System.out.println("5 - setZCommand: " + new Date());
              java.util.Hashtable wzr = wr.setZCommand(minCommand.getName(), "AF", wArgLine, wupdate);
              
              String wzmsg = (String)wzr.get("zmsg");
              if ((wzmsg != null) && (wzmsg.trim() != "") && (wzmsg.startsWith("MIN-"))) {
                wzerr = new MError(wzmsg);
                if (wzerr.getMinErrorType() != 1) {
                  wr.setField(wargs[0], wcste);
                  wr.updateRow(wupdate, wr);
                }
              }
            }
            System.out.println("6 - evalPnut AA: " + new Date());
            mpnut.evalPnut(minForm.getEnt(), "MC", wName, "AA");
            System.out.println("7 - commitBatch(): " + new Date());
            wupdate.commitBatch();
            System.out.println("8 - Fim commitBatch(): " + new Date());

          }
          finally
          {

            if (wupdate.inBeginBatch()) {
              wupdate.rollbackBatch();
            }
            wrold.refreshRow(wupdate);
          }
          if (wzerr != null) {
            if (wzerr.getMinErrorType() != 0) {
              wzerr.show(minForm.getMFrame());
            } else {
              throw wzerr;
            }
          }
          if ((wNextCmd != null) && (wNextCmd.length() != 0))
            new MHandleCommand(minForm.getMFrame(), null, minForm, minForm.getMFrame(), null, wNextCmd);
        }
      }
      mpnut.setForm(minForm);
      mpnut.set("wrows", wrows);
      mpnut.evalPnut(minForm.getEnt(), "MC", wName, "AC");
    }
    finally {
      wupdate.close();
      mpnut.unset("rowold");
      mpnut.unset("cursor");
    }
    if ((wOkCode != null) && (wOkCode.length() != 0)) {
      new MError(wOkCode).show(minForm.getMFrame());
    }
  }
}
