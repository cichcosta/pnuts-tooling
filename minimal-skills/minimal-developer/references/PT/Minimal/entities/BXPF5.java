package PT.Minimal.entities;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;import PT.Minimal.pnuts.;
import java.util.Vector;

public class BXPF5 implements PT.Minimal.actions.MExternalCommand {
  PT.Minimal.geral.MCommand minCommand;
  MForm mform;
  Vector wrows;
  String EOL = "\n";

  public BXPF5() {
  }

  public void setMCommand(PT.Minimal.geral.MCommand wcmd) {
    minCommand = wcmd;
  }

  public void setMForm(MForm wform) {
    mform = wform;
  }

  public void setRows(Vector wv) {
    wrows = wv;
  }

  public void execute() throws MError {
    if (wrows.size() == 0) {
      throw new MError("MIN-000103W");
    }
    if (minCommand.getName().equals("RUN_MIG")) {
      run_mig();
    }
    if (minCommand.getName().equals("UNRUN_MIG")) {
      unrun_mig();
    }
    MError werr = new MError("MIN-000000M");
    werr.show(mform.getMFrame());
  }

  private void run_mig()
      throws MError {
    MCursor wupdate = PT.Minimal.geral.MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow wrold = (MRow) wrows.elementAt(j);
        if (wrold.isValidRow()) {

          MRow wr = wrold.Copy();
          run_mig_object(wr, wupdate);
          wrold.updateRow(wupdate, wr);
          wrold.refreshRow(wupdate);
        }
      }
    } finally {
      wupdate.close();
    }
  }

  private void run_mig_object(MRow wr, MCursor wupdate) throws MError {
    String wtext = "";
    int i = 0;
    String wzk_ini = "";
    String wzk_end = "";
    wr.setField("dexe", PT.Minimal.geral.MUtil.getToday());
    wr.setField("hini", PT.Minimal.geral.MUtil.getTime());
    
    String wobject = ((String)wr.getField("ixpfr")).trim();
    if (wobject.length() < 5) {
      throw new MError("MIN-BXPF52E", new String[] { wobject });
    }
    String wixpfr = wobject;
    wobject = wobject.substring(0, 5);
    
    String wiorg = ((String)wr.getField("iorg")).trim();
    String wsql = ((String)wr.getField("tobs")).trim();
    String wdini = ((String)wr.getField("dini")).trim();
    String wdfim = ((String)wr.getField("dfim")).trim();
    String wtmodres = ((String)wr.getField("tmodres")).trim();
    
    if (wtmodres.startsWith("EXECUTADO")) {
      throw new MError("MIN-021100E");
    }
    
    String wtable_in = wobject.substring(0, 4) + "TI";
    String wtable_out = wobject.substring(0, 4) + "T0";
    

    String wcsel = " ";
    

    MRow wobrow = PT.Minimal.geral.MMData.getMRow(wobject.substring(0, 4));
    
    wcsel = " update " + wtable_in + " set zk = 0 " + " where zdst between '" + wdini + "' and '" + wdfim + "' " + " and zctl = '" + wixpfr.trim() + "' ";
    


    if (wobrow.containsKey("iorg")) {
      wcsel = wcsel + " and iorg = '" + wiorg + "' ";
    }
    
    System.out.println(wcsel);
    int n = wupdate.execStatement(wcsel);
    


    wcsel = " select * from " + wtable_in + " where zdst between '" + wdini + "' and '" + wdfim + "' " + " and (zk is null or zk = 0)  and zctl in (' ','" + wixpfr.trim() + "') " + wsql;
    


    if (wobrow.containsKey("iorg")) {
      wcsel = wcsel + " and iorg = '" + wiorg + "' ";
    }
    
    wcsel = wcsel + " order by zdst, zhst ";
    
    System.out.println(wcsel);
    
    Vector wv = PT.Minimal.geral.MUtil.getRows(wobrow, wcsel, 999999, wupdate);
    
    if (wv.size() > 0)
    {
      MForm mform = getMFormForMigration(wobject);
      
      mpnut = .findMPnut(wobject);
      mpnut.setForm(mform);
      
      mpnut.evalPnut("BXPF5", "MG", wobject, "BM");
      for (i = 0; i < wv.size(); i++) {
        MRow wrow = (MRow)wv.elementAt(i);
        
        mform.setRow(wrow.Copy());
        mform.clearFormValues();
        mform.getRow().setField("zk", "0");
        mform.setFormValues(mform.getRow().getRecord());
        mpnut.setRow(mform.getRow());
        
        mform.setFormValues(PT.Minimal.geral.MUtil.expand(mform, "NoForce", mform.getFormValues()));
        mpnut.evalPnut("BXPF5", "MG", wobject, "BF");
        mpnut.evalFormPnut(mform, "BF");
        
        mform.setFase(1);
        mform.setAtribFormat(mform.getCenterPanel(), "INSERT_ROW");
        try
        {
          wupdate.beginBatch();
          mpnut.setForm(mform);
          mpnut.setRow(mform.getRow());
          
          PT.Minimal.geral.MUtil.validate(mform, "Force");
          mform.setFormValues(PT.Minimal.geral.MUtil.expand(mform, "Force", mform.getFormValues()));
          
          mpnut.evalFormPnut(mform, "AF");
          mpnut.evalPnut("BXPF5", "MG", wobject, "AF");
          
          PT.Minimal.geral.MUtil.MoveRecord(mform.getFormValues(), mform.getRow().getRecord());
          mform.getRow().setField("zcri", PT.Minimal.geral.MLogin.getUser());
          mform.getRow().setField("zgrp", PT.Minimal.geral.MLogin.getUserNodo());
          mform.getRow().setField("zhst", " ");
          mform.getRow().setField("zdst", " ");
          
          String wzcst = ((String)mform.getRow().getField("zcst")).trim();
          if ((wzcst.length() != 0) && (wzcst.toLowerCase().startsWith("r"))) {
            wzcst = "r#";
          } else {
            wzcst = "p#";
          }
          
          mform.getRow().setField("zcst", wzcst);
          

          mpnut.evalPnut("BXPF5", "MG", wobject, "BI");
          

          mform.getRow().insertRow(wupdate);
          mform.getRow().setZCommand("INSERT_ROW", "AF", " ", wupdate);
          
          mpnut.evalPnut("BXPF5", "MG", wobject, "AI");
          
          String wset = "zk = " + mform.getRow().getField("zk");
          wrow.updateRow(wupdate, wrow, wtable_in, wset);
          
          wzk_end = (String)mform.getRow().getField("zk");
          if (wzk_ini.trim().length() == 0) {
            wzk_ini = (String)mform.getRow().getField("zk");
          }
          
          wupdate.commitBatch();
        } catch (MError err) {
          System.out.println(err.getMessage());
          wtext = wtext + String.valueOf(i + 1) + " Chave do Registo: " + wrow.toString().substring(wrow.toString().indexOf("[")) + EOL + err.getMinErrorCode() + "-" + err.getMessage() + EOL;
          
          if (err.getAtrib().trim().length() != 0) {
            wtext = wtext + " Atributo: " + err.getAtrib() + "=" + wrow.get(err.getAtrib()) + EOL;
          }
          wtext = wtext + "--------------------------------------------" + EOL;
        } finally {
          if (wupdate.inBeginBatch()) {
            wupdate.rollbackBatch();
          }
        }
      }
      mpnut.evalPnut("BXPF5", "MG", wobject, "AM");
      MForm.removeMForm(wobject + "ME");
    }
    wr.setField("hfim", PT.Minimal.geral.MUtil.getTime());
    wr.setField("turegp", wzk_ini + " " + wzk_end);
    wr.setField("qregp", String.valueOf(i));
    wr.setField("zcst", "ro");
    if (wtext.trim().length() == 0) {
      wr.setField("cexe", "OK");
      wr.setField("tmodres", "EXECUTADO");
    } else {
      wr.setField("cexe", "?E");
      wr.setField("tmodres", "EXECUTADO ERROS");
      wr.setAssociatedText(wtext);
    }
  }

  private void unrun_mig()
      throws MError {
    MCursor wupdate = PT.Minimal.geral.MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow wrold = (MRow) wrows.elementAt(j);
        if (wrold.isValidRow()) {

          MRow wr = wrold.Copy();
          unrun_mig_object(wr, wupdate);
          wrold.updateRow(wupdate, wr);
          wrold.refreshRow(wupdate);
        }
      }
    } finally {
      wupdate.close();
    }
  }

  private void unrun_mig_object(MRow wr, MCursor wupdate)
      throws MError {
    String wtext = "";
    int i = 0;
    String wzk_ini = "";
    String wzk_end = "";
    wr.setField("dexe", PT.Minimal.geral.MUtil.getToday());
    wr.setField("hini", PT.Minimal.geral.MUtil.getTime());

    String wobject = ((String) wr.getField("ixpfr")).trim();
    if (wobject.length() < 5) {
      throw new MError("MIN-BXPF52E", new String[] { wobject });
    }
    String wixpfr = wobject;
    wobject = wobject.substring(0, 5);

    String wiorg = ((String) wr.getField("iorg")).trim();
    String wsql = ((String) wr.getField("tobs")).trim();
    String wdini = ((String) wr.getField("dini")).trim();
    String wdfim = ((String) wr.getField("dfim")).trim();
    String wtmodres = ((String) wr.getField("tmodres")).trim();

    if (!wtmodres.startsWith("EXECUTADO")) {
      throw new MError("MIN-021100E");
    }

    String wturegp = ((String) wr.getField("turegp")).trim();
    String[] wargs = PT.Minimal.geral.MUtil.getArgs(wturegp, " ");

    String wtable_in = wobject.substring(0, 4) + "TI";
    String wtable_out = wobject.substring(0, 4) + "T0";

    MRow wobrow = PT.Minimal.geral.MMData.getMRow(wobject.substring(0, 4));

    if ((wargs != null) && (wargs.length > 0)) {
      wzk_ini = wargs[0];
      if (wargs.length > 1) {
        wzk_end = wargs[1];
      } else {
        wzk_end = wargs[0];
      }
      try {
        wupdate.beginBatch();
        String wcsel = " delete from " + wtable_out + " where zk between " + wzk_ini + " and " + wzk_end
            + " and zcst in ('p@','p#','r#') and zctl = '" + wixpfr.trim() + "' ";

        if (wobrow.containsKey("iorg")) {
          wcsel = wcsel + " and iorg = '" + wiorg + "' ";
        }
        System.out.println(wcsel);
        i = wupdate.execStatement(wcsel);
        wupdate.closeStatement();

        wcsel = " update " + wtable_in + " set zk = 0 " + " where zdst between '" + wdini + "' and '" + wdfim + "' "
            + " and zctl = '" + wixpfr.trim() + "' ";

        if (wobrow.containsKey("iorg")) {
          wcsel = wcsel + " and iorg = '" + wiorg + "' ";
        }
        System.out.println(wcsel);
        int n = wupdate.execStatement(wcsel);

        wupdate.closeStatement();
        wupdate.commitBatch();
      } finally {
        if (wupdate.inBeginBatch()) {
          wupdate.rollbackBatch();
        }
      }
    }

    wr.setField("hfim", PT.Minimal.geral.MUtil.getTime());
    wr.setField("turegp", wzk_ini + " " + wzk_end);
    wr.setField("qregp", String.valueOf(i));
    wr.setField("zcst", "us");
    wr.setField("cexe", "AN");
    wr.setField("tmodres", "ANULADO");
  }

  private MForm getMFormForMigration(String went)
      throws MError {
    MForm mform = MForm.getMForm(went + "ME");
    if (mform == null) {
      mform = new MForm(went + "ME");
      mform.setEnt(went);

      mform.setCenterPanel(MForm.getPanel(went, "ME"));

      mform.prepareForm();
    }

    mform.setAction("INSERT_ROW");
    return mform;
  }
}
