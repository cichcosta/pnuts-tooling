package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.pnuts.;

public class MDisplayForm implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  java.util.Vector wrows;
  private boolean isValid = true;
  private String wpanel;
  private String wpanelname = " ";
  private String wpaneltype = "ME";
  private String wconnect = "JMETADATA";
  private String waction = "INSERT_ROW";
  private String[] wargs;
   mpnut;
  
  public MDisplayForm() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(java.util.Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError {
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
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
    if (wargs.length < 1) {
      throw new MError("Não existe painel indicado ...");
    }
    wpanel = wargs[0].toUpperCase();
    if (wargs.length > 1) {
      wpanelname = wargs[1];
    }
    if (wargs.length > 2) {
      wpaneltype = wargs[2];
    }
    if (wargs.length > 3) {
      wconnect = wargs[3];
    }
    if (wargs.length > 4) {
      waction = wargs[4];
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      if (minCommand.getCallerArgs().startsWith("CANCEL")) {
        System.out.println(minCommand.getCallerArgs());
        cancelOperation(minForm);
        isValid = false;
      }
    }
    if (isValid) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      mpnut.set("wrows", wrows);
      if (minForm.getFase() == 0) {
        display_form();
      }
      if (minForm.getFase() == 1) {
        do_display_row();
      }
      mpnut.unset("wrows");
    }
  }
  
  public void display_form() throws MError {
    MForm wfmc = MForm.getMForm(wpanel + wpaneltype);
    try {
      if (wfmc == null) {
        System.out.println(wpanel);
        System.out.println(minForm.getEntName());
        wfmc = new MForm(wpanel + wpaneltype);
        wfmc.setEnt(minForm.getEnt());
        wfmc.setCenterPanel(MForm.getPanel(wpanel, wpaneltype, wconnect));
        wfmc.setEntName(minForm.getEntName());
        wfmc.setBotPanel(minForm.constrBotPanel(3));
        wfmc.showStatusBar(true);
        wfmc.prepareForm();
        MFrame wframe = new MFrame();
        wframe.setTitle(minForm.getEntName() + " - " + wpanelname);
        wframe.setForm(wfmc);
        wframe.pack();
        wframe.setSize(wfmc.getPreferredSize());
        wframe.center(getPreferredSizewidth, getPreferredSizeheight, false);
      }
      

      MFrame.setModalParent(minForm.getMFrame(), wfmc.getMFrame());
      
      ((MFrame)wfmc.getFormParent()).setTitle(minForm.getEntName() + " - " + wpanelname);
      wfmc.setAtribFormat(wfmc.getCenterPanel(), waction);
      wfmc.setAction(minCommand.getName());
      wfmc.setFase(1);
      wfmc.setActionObject(wrows);
      wfmc.setActionObjectIndex(0);
      PT.Minimal.geral.MButton wend = (PT.Minimal.geral.MButton)PT.Minimal.geral.MUtil.getComponent(wfmc.getBotPanel(), "wend");
      
      wend.setCommand(minCommand.getName());
      wend.setCommandArgs("CANCEL");
      if (minForm.getBrowse() != null) {
        wfmc.setBrowse(minForm.getBrowse());
      }
      prepareNext(wfmc);
    } catch (MError ex) {
      wfmc.hideForm();
      ex.show(minForm.getMFrame());
    }
  }
  
  public void do_display_row() {
    try {
      MCursor wstat = PT.Minimal.geral.MConnection.getMCursor();
      try {
        wstat.beginWork();
         mpnut = .getMPnut(minForm.getEnt());
        mpnut.setRow(minForm.getRow());
        mpnut.setForm(minForm);
        mpnut.set("cursor", wstat);
        mpnut.evalFormPnut(minForm, "AF");
        mpnut.setForm(null);
        mpnut.setRow(null);
        mpnut.set("cursor", null);
        wstat.commitWork();
      } finally {
        if (wstat.inBeginWork()) {
          wstat.rollbackWork();
        }
        wstat.close();
      }
      minForm.setActionObjectIndex(minForm.getActionObjectIndex() + 1);
      prepareNext(minForm);
    } catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
  }
  
  public void prepareNext(MForm wfmc) throws MError {
    java.util.Vector wrows = (java.util.Vector)wfmc.getActionObject();
    int j = wfmc.getActionObjectIndex();
    if (j >= wrows.size()) {
      wfmc.hideForm();
    }
    if (j < wrows.size()) {
      System.out.println("!*** MDisplayForm Entrou ciclo: " + j);
      wfmc.setRow((PT.Minimal.geral.MRow)wrows.elementAt(j));
      wfmc.setFormValues(wfmc.getRow().getRecord());
      MCursor wstat = PT.Minimal.geral.MConnection.getMCursor();
      wstat.beginWork();
      try {
         mpnut = .getMPnut(wfmc.getEnt());
        mpnut.setRow(wfmc.getRow());
        mpnut.setForm(wfmc);
        mpnut.set("cursor", wstat);
        
        mpnut.evalFormPnut(wfmc, "BF");
        mpnut.setForm(null);
        mpnut.setRow(null);
        mpnut.set("cursor", null);
        wstat.commitWork();
      } finally {
        if (wstat.inBeginWork()) {
          wstat.rollbackWork();
        }
        wstat.close();
      }
      wfmc.showForm(false);
    }
  }
  
  public void cancelOperation(MForm wfmc) throws MError { wfmc.setActionObjectIndex(wfmc.getActionObjectIndex() + 1);
    prepareNext(wfmc);
  }
}
