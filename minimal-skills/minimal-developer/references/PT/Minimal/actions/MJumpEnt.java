package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MExp;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MinSaltoME;
import PT.Minimal.pnuts.;
import java.util.Vector;

public class MJumpEnt implements MExternalCommand, MRegisteredAction
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private String watrib;
  private String wentname;
  private String waction;
  private String[] wargs;
  private boolean checkTriggers = true;
  
  public MJumpEnt() {}
  
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
      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getArgs(), ',');
    }
    System.out.println("!*** MJumpEnt args.length: " + wargs.length + " " + wargs[0].toUpperCase() + " " + minForm.getType());
    System.out.println("!*** MJumpEnt minCommand.getCallerArgs " + minCommand.getCallerArgs());
    if ((wargs == null) || (wargs.length < 3)) {
      throw new MError("Argumentos invalidos");
    }
    if (!PT.Minimal.geral.MCheckAut.checkAut(wargs[0], "INQUIRY_ROW", "1")) {
      throw new MError("MIN-009998E", new String[] { PT.Minimal.geral.MLogin.getUser(), wargs[0] });
    }
    mpnut = .getMPnut(minForm.getEnt());
    mpnut.setForm(minForm);
    mpnut.setRow(null);
    
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      PT.Minimal.geral.MUtil.MoveRecord(minForm.getFormValues(), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    
    if (wrows.size() == 0) {
      throw new MError("MIN-000103W");
    }
    
    watrib = wargs[0].toUpperCase();
    wentname = wargs[1];
    waction = wargs[2].trim();
    if ((wargs.length > 3) && 
      (wargs[3].trim().toLowerCase().equals("false"))) {
      checkTriggers = false;
    }
    
    do_JumpEnt();
  }
  
  public void do_JumpEnt() throws MError {
    try {
      MExp[] wvexp = MForm.getExp(minForm.getEnt(), watrib, "S1");
      if ((wvexp == null) && (!watrib.startsWith("QNOT0"))) {
        throw new MError("Expressão de salto (" + minForm.getEnt() + "," + watrib + "," + "S1) não encontrada!!!");
      }
      
      for (int j = 0; j < 1; j++) {
        MRow wr = (MRow)wrows.elementAt(j);
        if (wr.isValidRow())
        {

          if (((String)wr.getField("zk")).equals("0")) {
            throw new MError("MIN-000103E");
          }
          if (((String)wr.getField("zk")).startsWith("*")) {
            throw new MError("MIN-000103E");
          }
          mpnut.setForm(minForm);
          mpnut.setRow(wr);
          
          mpnut.set("wrows", wrows);
          boolean bfound = false;
          for (int i = 0; ((i < 1) && (watrib.startsWith("QNOT0"))) || (i < wvexp.length); i++) {
            if (!watrib.startsWith("QNOT0")) {
              MExp wexp = wvexp[i];
              if (mpnut.evalCondition(wexp.getCondition())) {
                wr.getRecord().put("zk" + minForm.getEnt().substring(0, 4).toLowerCase(), wr.getField("zk"));
                register(wexp.getAssociateObject() + "ME");
                mpnut.evalPnut(minForm.getEnt(), "MC", minCommand.getName(), "BF");
                



                MinSaltoME wms = new MinSaltoME(wexp.getAssociateObject(), wentname, minForm, wrows, wexp, waction, checkTriggers);
                bfound = true;
                break;
              }
            } else {
              MExp wexp = new MExp(wentname, watrib, "S1");
              wexp.setAssociateObject("QNOT0");
              wexp.setSelect("a.*");
              wexp.setFrom("qnott0 a");
              wexp.setWhere(" 1 = 1 ");
              wexp.setOrder("a.ientt2,a.ient2 ");
              
              MinSaltoME wms = new MinSaltoME("QNOT0", wentname, minForm, wrows, wexp, waction, checkTriggers);
              bfound = true;
              break;
            }
          }
          if (!bfound) {
            throw new MError("Não existe expressão S1 válida...");
          }
        }
      }
      if ((minCommand.getOkCode() != null) && (minCommand.getOkCode().trim().length() != 0))
      {
        MError wmsg = new MError(minCommand.getOkCode());
        wmsg.show(minForm.getMFrame());
      }
      if ((minCommand.getNextCommand() != null) && (minCommand.getNextCommand().trim().length() != 0))
      {
        new MHandleCommand(minForm.getMFrame(), null, minForm, minForm.getMFrame(), null, minCommand.getNextCommand());
      }
    }
    catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
    
    wrows = null;
  }
  
  public void register(String formName) {
    MRegister.registerAction(formName, this);
  }
  
  public void registerCall(String formName) throws MError {
    mpnut.set("MCOMMAND", this);
    mpnut.evalPnut(minForm.getEnt(), "MC", minCommand.getName(), "RC");
    MRegister.removeJump(minForm.getEnt());
  }
}
