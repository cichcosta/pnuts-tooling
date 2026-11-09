package PT.Minimal.actions;

import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MLogin;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.util.Vector;

public class MUpdate implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean isValid = true;
  
  public MUpdate() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
    wrows = new Vector();
    wrows.addElement(wform.getRow());
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError
  {
    if (MLogin.getUserTipo().indexOf("CS") != -1) {
      throw new MError("MIN-009998E", new String[] { MLogin.getUser(), "executar este comando." });
    }
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    




























    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      if (minCommand.getCallerArgs().startsWith("CANCEL"))
      {
        if ((minForm.getBrowse() == null) || (!minForm.getBrowse().getName().startsWith(minForm.getEnt()))) {
          MRegister.removeJump(minForm.getEnt());
        }
        
        if (minForm.getEnt() != null) {
          mpnut.evalFormPnut(minForm, "CN");
        }
        System.out.println(minCommand.getCallerArgs());
        if (minForm.getFase() == 2) {
          prepareNext(minForm);
          return;
        }
        cancelOperation(minForm);
        return;
      }
    }
    if (isValid) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      if ((minCommand.getName().equals("UPDATE_ROW")) && (minForm.getFase() == 0)) {
        show_update_row();
        return;
      }
      if ((minCommand.getName().equals("UPDATE_ROW")) && (minForm.getFase() == 1)) {
        try {
          do_update_row();
        } catch (MError err) {
          err.show(minForm.getMFrame());
        }
        return;
      }
      if ((minCommand.getName().equals("UPDATE_ROW")) && (minForm.getFase() == 2)) {
        prepareNext(minForm);
        return;
      }
    }
  }
  
  public void show_update_row() throws MError {
    if (wrows.size() == 0) {
      wrows.addElement(minForm.getRow().Copy());
    }
    MForm mform = MForm.getMForm(minForm.getEnt() + "ME");
    try {
      if (mform == null) {
        mform = new MForm(minForm.getEnt() + "ME");
        mform.setEnt(minForm.getEnt());
        mform.setEntName(minForm.getEntName());
        mform.addToolBar((PT.Minimal.geral.MToolBar)MForm.getPanel("ToolBar Entidade"));
        
        mform.setCenterPanel(MForm.getPanel(minForm.getEnt(), "ME"));
        mform.setBotPanel(MForm.getPanel(minForm.getEnt() + "MEBT"));
        mform.showStatusBar(true);
        MButton wok = (MButton)MUtil.getComponent(mform.getBotPanel(), "wok");
        mform.setOKButton(wok);
        MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
        mform.setENDButton(wend);
        javax.swing.JMenuBar wmenubar = PT.Minimal.geral.MMenu.getMinMenuBar(mform.getEnt() + "ME");
        if (wmenubar != null) {
          mform.addMenuBar(wmenubar);
        }
        mform.prepareForm();
        
        MFrame wframe = new MFrame();
        wframe.setTitle(mform.getEntName());
        
        wframe.setForm(mform);
        wframe.pack();
        wframe.setSize(mform.getPreferredSize());
        wframe.center(getPreferredSizewidth, getPreferredSizeheight, false);
        
        mpnut.put(mform.getName(), mform);
      }
      
      mpnut.setForm(mform);
      mpnut.setRow(null);
      MFrame.setModalParent(minForm.getMFrame(), mform.getMFrame());
      
      ((MFrame)mform.getFormParent()).setTitle(minForm.getEntName() + " - " + minCommand.getMenuText());
      mform.setAtribFormat(mform.getCenterPanel(), minCommand.getName().trim());
      mform.setAction(minCommand.getName());
      mform.setFase(1);
      mform.setActionObject(wrows);
      mform.setActionObjectIndex(0);
      MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
      wend.setCommand(minCommand.getName());
      wend.setCommandArgs("CANCEL");
      wend.setText(MUtil.lang("\"Seguinte\""));
      mform.getOKButton().setEnabled(true);
      mform.getENDButton().setEnabled(true);
      
      if (minForm.getBrowse() != null) {
        mform.setBrowse(minForm.getBrowse());
      }
      
      prepareNext(mform);
    } catch (MError ex) {
      mform.hideForm();
      ex.show(minForm.getMFrame());
    }
  }
  









  public void do_update_row()
    throws MError
  {
    MCursor wupdate = PT.Minimal.geral.MConnection.getMCursor();
    try
    {
      wupdate.beginBatch();
      
      mpnut.setForm(minForm);
      mpnut.setRow(minForm.getRow());
      
      MUtil.validate(minForm, "Force");
      minForm.setFormValues(MUtil.expand(minForm, "Force", minForm.getFormValues()));
      
      mpnut.evalFormPnut(minForm, "AF");
      
      MRow wr = minForm.getRow().Copy();
      MUtil.MoveRecord(minForm.getFormValues(), wr.getRecord());
      wr.setField("zcri", MLogin.getUser());
      wr.setField("zgrp", MLogin.getUserNodo());
      wr.setField("zcst", "us");
      
      minForm.getRow().updateRow(wupdate, wr);
      if ((minCommand.getZFase().startsWith("AF")) || (minCommand.getZFase().startsWith("BT"))) {
        minForm.getRow().setZCommand(minCommand.getName(), "AF", " ", wupdate);
      }
      minForm.getRow().refreshRow(wupdate);
      if (minForm.getBrowse() != null) {
        minForm.getBrowse().refresh(minForm.getRow());
      }
      minForm.setFormValues(MUtil.expand(minForm, "NoForce", minForm.getRow().getRecord()));
      

      wupdate.commitBatch();
    }
    finally
    {
      if (wupdate.inBeginBatch()) {
        wupdate.rollbackBatch();
      }
      wupdate.close();
    }
    minForm.setActionObjectIndex(minForm.getActionObjectIndex() + 1);
    


    minForm.setFase(2);
    System.out.println("modelo em 2;" + minForm.getFase());
    minForm.setAtribFormat(minForm.getCenterPanel(), "INQUIRY_ROW");
    minForm.getOKButton().setEnabled(false);
    minForm.getENDButton().setEnabled(true);
    MUtil.requestFocus(minForm.getENDButton());
  }
  



  public void prepareNext(MForm mform)
    throws MError
  {
    Vector wrows = (Vector)mform.getActionObject();
    int j = mform.getActionObjectIndex();
    
    MRow wrow = null;
    if (wrows != null) {
      while (j < wrows.size()) {
        wrow = (MRow)wrows.elementAt(j);
        if (wrow.isValidRow()) {
          break;
        }
        mform.setActionObjectIndex(++j);
        j = mform.getActionObjectIndex();
      }
    }
    
    if ((wrows == null) || (j >= wrows.size())) {
      mform.hideForm();
      return;
    }
    
    if (j >= wrows.size() - 1) {
      mform.getENDButton().setText(MUtil.lang("\"Terminar\""));
    }
    
    if (((String)wrow.getField("zcst")).toLowerCase().startsWith("r")) {
      mform.hideForm();
      throw new MError("MIN-002030E");
    }
    
    mform.setRow(wrow);
    mform.clearFormValues();
    mform.setFormValues(mform.getRow().getRecord());
    
    mpnut.setForm(mform);
    mpnut.setRow(mform.getRow());
    
    mform.setFormValues(MUtil.expand(mform, "NoForce", mform.getFormValues()));
    
    mpnut.evalFormPnut(mform, "BF");
    
    mform.setFase(1);
    mform.setAtribFormat(mform.getCenterPanel(), minCommand.getName().trim());
    mform.getOKButton().setEnabled(true);
    mform.getENDButton().setEnabled(true);
    MUtil.requestFocus(mform.getOKButton());
    

    mform.showForm();
  }
  
  public void cancelOperation(MForm mform) throws MError {
    if (wrows == null) {
      mform.hideForm();
      return;
    }
    mform.setActionObjectIndex(mform.getActionObjectIndex() + 1);
    prepareNext(mform);
  }
}
