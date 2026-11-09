package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MExp;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MLogin;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MinSalto;
import java.util.Vector;

public class MJumpBrowse implements MExternalCommand, MRegisteredAction
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  PT.Minimal.pnuts. mpnut;
  private String watrib;
  private String wentname;
  private String[] wargs;
  private boolean checkTriggers = true;
  
  public MJumpBrowse() {}
  
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
  
  public void execute() throws PT.Minimal.geral.MError {
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getArgs(), ',');
    }
    
    System.out.println(wargs.length);
    System.out.println(minCommand.getCallerArgs());
    if ((wargs == null) || (wargs.length < 1)) {
      throw new PT.Minimal.geral.MError("Não existe opcão indicada ...");
    }
    if ((minForm.getType() == "SINGLEROW") || ((wargs.length > 2) && (wargs[2].equals("NONE")))) {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      PT.Minimal.geral.MUtil.MoveRecord(minForm.getFormValues(), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    if ((wrows.size() == 0) && (wargs.length > 2) && (!wargs[2].equals("NONE"))) {
      throw new PT.Minimal.geral.MError("MIN-000103W");
    }
    watrib = wargs[0].toUpperCase();
    wentname = PT.Minimal.geral.MUtil.lang("\"" + wargs[1] + "\"");
    if ((wargs.length > 2) && 
      (wargs[2].trim().toLowerCase().equals("false"))) {
      checkTriggers = false;
    }
    
    if (!PT.Minimal.geral.MCheckAut.checkAut(wargs[0], "INQUIRY_ROW", "1")) {
      throw new PT.Minimal.geral.MError("MIN-009998E", new String[] { MLogin.getUser(), wargs[0] });
    }
    mpnut = PT.Minimal.pnuts..getMPnut(minForm.getEnt());
    mpnut.setForm(minForm);
    mpnut.setRow(null);
    
    do_JumpBrowse();
  }
  
  public void do_JumpBrowse() throws PT.Minimal.geral.MError {
    try {
      boolean bfound = false;
      MExp[] wvexp = MForm.getExp(minForm.getEnt(), watrib, "S1");
      if ((wvexp == null) && (!watrib.startsWith("SOUT9")) && (!watrib.startsWith("SQRY9")) && (!watrib.startsWith("SCGP9")) && (!watrib.startsWith("BOBA0")) && (!watrib.startsWith("MEXT9")) && (!watrib.equals("QNOT0")) && (!watrib.equals("QNOT1"))) {
        throw new PT.Minimal.geral.MError("Expressão de salto (" + minForm.getEnt() + "," + watrib + "," + "S1) não encontrada!!!");
      }
      mpnut.setForm(minForm);
      
      for (int j = 0; j < 1; j++) {
        MRow wr = (MRow)wrows.elementAt(j);
        if (!wr.isValidRow()) {
          if ((j == 0) && (wrows.size() > 1)) {
            wr = (MRow)wrows.elementAt(1);
            if ((!wr.isValidRow()) && (wrows.size() > 2)) {
              wr = (MRow)wrows.elementAt(2);
              if (!wr.isValidRow()) {
                continue;
              }
            }
          } else {
            if ((j != 0) || (wrows.size() != 1)) continue;
            minForm.getBrowse().selectMRow(1);
            wr = (MRow)wrows.elementAt(0);
          }
        }
        





        if ((wr.containsKey("zk")) && (((String)wr.getField("zk")).equals("0"))) {
          throw new PT.Minimal.geral.MError("MIN-000103E");
        }
        if ((wr.containsKey("zk")) && (((String)wr.getField("zk")).startsWith("*"))) {
          throw new PT.Minimal.geral.MError("MIN-000103E");
        }
        mpnut.setRow(wr);
        if ((!watrib.startsWith("SOUT9")) && (!watrib.startsWith("SQRY9")) && (!watrib.startsWith("SCGP9")) && (!watrib.startsWith("BOBA0")) && (!watrib.startsWith("MEXT9")) && (!watrib.equals("QNOT0")) && (!watrib.equals("QNOT1"))) {
          for (int i = 0; i < wvexp.length; i++) {
            MExp wexp = wvexp[i];
            if (mpnut.evalCondition(wexp.getCondition())) {
              wr.getRecord().put("zk" + minForm.getEnt().substring(0, 4).toLowerCase(), wr.getField("zk"));
              register(wexp.getAssociateObject() + "BR");
              mpnut.evalPnut(minForm.getEnt(), "MC", minCommand.getName(), "BF");
              
              MinSalto wms = new MinSalto(wexp.getAssociateObject(), wentname, minForm, wr, wexp, checkTriggers);
              bfound = true;
              break;
            }
          }
        } else {
          if (watrib.startsWith("SOUT9")) {
            MExp wexp = new MExp(wentname, watrib, "S1");
            wexp.setAssociateObject("SOUT9");
            wexp.setSelect("a.*, a.cmcm||' '||substr(a.casel,1,1)||substr(a.caout,1,1)||substr(a.cidir,1,1)||' '||substr(a.imapt,1,3) as wid1 ");
            wexp.setFrom("soutt0 a");
            
            if (PT.Minimal.geral.MConnection.getMConnection().getAmbient().startsWith("I")) {
              wexp.setWhere("a.cep1 in ('*','" + MLogin.getLoginProperty("REGION") + "') and a.cep2 in ('*','" + MLogin.getLoginProperty("TEMPLATE") + "') and a.iobj = '" + minForm.getEnt() + "' and a.ipri||a.igrp||a.iutl = (select min(b.ipri)||max(b.igrp||b.iutl) from soutt0 b where b.iobj = a.iobj and b.imapt = a.imapt and b.imapr = a.imapr and b.igrp in ('*','" + MLogin.getUserNodo() + "')  and b.iutl in ('*',USER)) and a.igrp in ('*','" + MLogin.getUserNodo() + "')  and a.iutl in ('*',USER) ");
            } else {
              wexp.setWhere("a.cep1 in ('*','" + MLogin.getLoginProperty("REGION") + "') and a.cep2 in ('*','" + MLogin.getLoginProperty("TEMPLATE") + "') and a.iobj = '" + minForm.getEnt() + "' and a.ipri||a.igrp||a.iutl = (select min(b.ipri)||max(b.igrp||b.iutl) from soutt0 b where b.iobj = a.iobj and b.imapt = a.imapt and b.imapr = a.imapr and b.igrp in ('*','" + MLogin.getUserNodo() + "')  and b.iutl in ('*',lower(USER))) and a.igrp in ('*','" + MLogin.getUserNodo() + "')  and a.iutl in ('*',lower(USER)) ");
            }
            
            wexp.setOrder("a.iobj,a.ipri,a.xord,a.imapr,a.imapt,a.cmcm,cep1 desc, cep2 desc, a.igrp desc,a.iutl desc ");
            MinSalto wms = new MinSalto("SOUT9", wentname, minForm, wr, wexp, checkTriggers);
            bfound = true;
            break;
          }
          
          if (watrib.startsWith("SQRY9")) {
            MExp wexp = new MExp(wentname, watrib, "S1");
            wexp.setAssociateObject("SQRY9");
            wexp.setSelect("a.* ");
            wexp.setFrom("sqryt0 a");
            wexp.setWhere("a.cep1 in ('*','" + MLogin.getLoginProperty("REGION") + "') and a.cep2 in ('*','" + MLogin.getLoginProperty("TEMPLATE") + "') and a.iobj = '" + minForm.getEnt() + "' and a.ipri||a.igrp||a.iutl = (select lower(min(b.ipri)||max(b.igrp||b.iutl)) from sqryt0 b where b.iobj = a.iobj and b.imapr = a.imapr and b.igrp in ('*','" + MLogin.getUserNodo() + "')  and b.iutl in ('*',lower(USER))) and a.igrp in ('*','" + MLogin.getUserNodo() + "')  and a.iutl in ('*',lower(USER)) ");
            wexp.setOrder("a.iobj,a.ipri,a.xord,a.imapr,cep1 desc, cep2 desc, a.igrp desc,a.iutl desc ");
            MinSalto wms = new MinSalto("SQRY9", wentname, minForm, wr, wexp, checkTriggers);
            bfound = true;
            break;
          }
          
          if (watrib.startsWith("SCGP9"))
          {
            MExp wexp = new MExp(wentname, watrib, "S1");
            wexp.setAssociateObject("SCGP9");
            wexp.setSelect("a.*");
            wexp.setFrom("scgpt0 a");
            wexp.setWhere("a.iobj = '" + minForm.getEnt().substring(0, 5) + "' ");
            wexp.setOrder("a.xord,a.nctx ");
            MinSalto wms = new MinSalto("SCGP9", wentname, minForm, wr, wexp, checkTriggers);
            bfound = true;
            break;
          }
          
          if (watrib.startsWith("BOBA0"))
          {
            MExp wexp = new MExp(wentname, watrib, "S1");
            wexp.setAssociateObject("BOBA0");
            wexp.setSelect("a.*");
            wexp.setFrom("bobat0 a");
            wexp.setWhere("a.zent = '" + minForm.getEnt().substring(0, 4) + "' and zkent = " + wr.get("zk"));
            wexp.setOrder("a.zent,a.xord,a.doba,a.ioba,a.iobat,a.zk ");
            MinSalto wms = new MinSalto("BOBA0", wentname, minForm, wr, wexp, checkTriggers);
            bfound = true;
            break;
          }
          
          if (watrib.startsWith("MEXT9"))
          {
            MExp wexp = new MExp(wentname, watrib, "S1");
            wexp.setAssociateObject("MEXT9");
            wexp.setSelect("a.*");
            wexp.setFrom("mextt0 a");
            wexp.setWhere("a.i_ob like '" + minForm.getEnt() + "%' ");
            wexp.setOrder("a.i_ob,a.q_ot, a.q_oe ");
            MinSalto wms = new MinSalto("MEXT9", wentname, minForm, wr, wexp, checkTriggers);
            bfound = true;
            break;
          }
          

          if ((watrib.startsWith("QNOT0")) || (watrib.startsWith("QNOT1")))
          {
            if (wvexp == null) {
              MExp wexp = new MExp(wentname, watrib, "S1");
              wexp.setFrom("qnott0 a");
              MinSalto wms; MinSalto wms; if (watrib.startsWith("QNOT0")) {
                wexp.setSelect("a.*,substr(tnot0,1,54) as wtnot0,substr(tnot,1,39) as wtnot,'Conhecimento: '||a.ientt||' '||trim(a.ient)||' '||a.cst||' '||a.dst as wid0,'De: '||trim(a.ientt0)||' '||trim(a.ient0)||'  '||trim(bentxn(a.ientt0,a.ient0,'*','*','9999-12-31'))||'  '||a.cst0||' '||a.dst0 as wid1, bentxn(a.ientt,a.ient,'*','*','9999-12-31') as nent, 'Seguimento: '||a.ientt2||' '||trim(a.ient2)||' '||a.cst2||' '||a.dst2 as wid2");
                wexp.setTextArgs("(ient0,ient0,ientt,ient,ientt2,ient2)()(wtnot0,wid0,wtnot,wid2)(wtnot0,wid1))");
                wexp.setOrder("a.dnot desc,a.hnot desc,a.ientt0,a.ient0, a.ientt,a.ient,ientt2,ient2");
                wexp.setAssociateObject("QNOT0");
                wexp.setWhere(" exists (select '*' from suact0 c, qnoxt0 d where c.iutl = lower(USER) and c.ientt = d.ientt and c.ient=d.ient and a.cst0 not like 'X%' and d.ccx = 'RM' and a.iorg=d.iorg and a.isis = d.isis and a.inot=d.inot)");
                wms = new MinSalto("QNOT0", wentname, minForm, wr, wexp, checkTriggers);

              }
              else
              {

                wexp.setSelect("a.*,substr(tnot0,1,54) as wtnot0,substr(tnot,1,39) as wtnot,'Conhecimento: '||a.ientt||' '||trim(a.ient)||' '||a.cst||' '||a.dst as wid0,'De: '||trim(a.ientt0)||' '||trim(a.ient0)||'  '||trim(bentxn(a.ientt0,a.ient0,'*','*','9999-12-31'))||'  '||a.cst0||' '||a.dst0 as wid1, bentxn(a.ientt,a.ient,'*','*','9999-12-31') as nent, 'Seguimento: '||a.ientt2||' '||trim(a.ient2)||' '||a.cst2||' '||a.dst2 as wid2");
                wexp.setTextArgs("(ientt,ient,ientt2,ient2,ient0,ient0)()(wtnot0,wid0,wtnot,wid2)(wtnot0,wid1))");
                wexp.setOrder("a.dnot desc,a.hnot desc,a.ientt,a.ient,ientt2,ient2,a.ientt0,a.ient0");
                wexp.setAssociateObject("QNOT1");
                wexp.setWhere(" exists (select '*' from suact0 c, qnoxt0 d where c.iutl = lower(USER) and c.ientt = d.ientt and c.ient=d.ient and a.cst0 like 'E%' and d.ccx in ('NT','SG','CC') and d.cst not like 'X%' and a.iorg=d.iorg and a.isis = d.isis and a.inot=d.inot)");
                
                wms = new MinSalto("QNOT1", wentname, minForm, wr, wexp, checkTriggers);
              }
              wexp.setOrder("  ");
              bfound = true;
              break;
            }
            for (int i = 0; i < wvexp.length; i++) {
              MExp wexp = wvexp[i];
              if (mpnut.evalCondition(wexp.getCondition())) {
                wr.getRecord().put("zk" + minForm.getEnt().substring(0, 4).toLowerCase(), wr.getField("zk"));
                register(wexp.getAssociateObject() + "BR");
                mpnut.evalPnut(minForm.getEnt(), "MC", minCommand.getName(), "BF");
                MinSalto wms = new MinSalto(wexp.getAssociateObject(), wentname, minForm, wr, wexp, checkTriggers);
                bfound = true;
                break;
              }
            }
          }
        }
        


        if (!bfound) {
          throw new PT.Minimal.geral.MError("Não existe expressão S1 válida...");
        }
      }
    }
    catch (PT.Minimal.geral.MError ex) {
      ex.show(minForm.getMFrame());
    }
    
    wrows = null;
  }
  
  public void register(String formName) {
    MRegister.registerAction(formName, this);
  }
  
  public void registerCall(String formName) {
    mpnut.set("MCOMMAND", this);
    mpnut.evalPnut(minForm.getEnt(), "MC", minCommand.getName(), "RC");
    MRegister.removeJump(minForm.getEnt());
  }
}
