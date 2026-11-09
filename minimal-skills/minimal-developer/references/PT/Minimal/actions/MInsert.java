package PT.Minimal.actions;

import PT.Minimal.geral.MBrowse;
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
import java.io.PrintStream;
import java.util.Vector;

public class MInsert implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean multiRow = false;
  
  public MInsert() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
    multiRow = (wrows.size() > 1);
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
        if ((minForm.getBrowse() == null) || (minForm.getBrowse() == null) || (!minForm.getBrowse().getName().startsWith(minForm.getEnt()))) {
          MRegister.removeJump(minForm.getEnt());
        }
        
        if (minForm.getEnt() != null) {
          mpnut.evalFormPnut(minForm, "CN");
        }
        System.out.println("!*** Minsert getCallerArgs: " + minCommand.getCallerArgs());
        cancelOperation(minForm);
        return;
      }
    }
    
    if (minCommand.getName().startsWith("INSERT_ROW")) {
      switch (minForm.getFase()) {
      case 2:  prepareNext(minForm);
        return;
      case 1:  try {
          do_insert_row();
        } catch (MError err) {
          err.show(minForm.getMFrame());
        }
        return;
      case 0: 
        show_insert_row();
        return;
      }
    }
  }
  
  public void show_insert_row() throws MError
  {
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
      if (minCommand.getName().startsWith("INSERT_ROW_TEXT")) {
        mform.setActionArgs("ROW_TEXT");
        mform.setAction("INSERT_ROW");
      } else {
        mform.setActionArgs(" ");
        mform.setAction(minCommand.getName());
      }
      mform.setAtribFormat(mform.getCenterPanel(), mform.getAction().trim());
      mform.setFase(1);
      mform.setActionObject(wrows);
      mform.setActionObjectIndex(0);
      MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
      wend.setCommand(mform.getAction());
      wend.setCommandArgs("CANCEL");
      wend.setText(MUtil.lang("\"Seguinte\""));
      MButton wok = (MButton)MUtil.getComponent(mform.getBotPanel(), "wok");
      mform.getOKButton().setEnabled(true);
      mform.getENDButton().setEnabled(true);
      
      if (minForm.getBrowse() != null) {
        mform.setBrowse(minForm.getBrowse());
      }
      
      if (wrows.size() == 0) {
        wrows.addElement(minForm.getRow().Copy());
      }
      prepareNext(mform);
    } catch (MError ex) {
      mform.hideForm();
      ex.show(minForm.getMFrame());
    }
  }
  
  public void do_insert_row() throws MError
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
      wr.setField("zhst", " ");
      wr.setField("zdst", " ");
      wr.setField("zcst", "ps");
      if (wr.containsKey("zprc")) {
        wr.setField("zprc", "1");
      }
      
      wr.insertRow(wupdate);
      
      if (minForm.getActionArgs().equals("ROW_TEXT")) {
        wr.setAssociatedObject("ROWTXT", MUtil.getAssociatedObject(minForm.getEnt(), new java.math.BigDecimal((String)minForm.getClientProperty("woldzk")), "ROWTXT", wupdate), wupdate);
      }
      
      if ((minCommand.getZFase().startsWith("AF")) || (minCommand.getZFase().startsWith("BT")))
      {
        wr.setZCommand(minCommand.getName(), "AF", " ", wupdate);
      }
      
      wr.refreshRow(wupdate);
      if (minForm.getBrowse() != null) {
        minForm.getBrowse().addRow(wr);
        minForm.getBrowse().refresh(wr);
      }
      minForm.setRow(wr);
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
    



    minForm.setFase(2);
    System.out.println("!*** Minsert modelo em 2;" + minForm.getFase());
    minForm.setAtribFormat(minForm.getCenterPanel(), "INQUIRY_ROW");
    
    if (minForm.getActionObject() != null) {
      Vector warows = (Vector)minForm.getActionObject();
      multiRow = (warows.size() > 1);
    }
    
    if (((wrows == null) || (wrows.size() != 0)) || 
    




      (multiRow)) {
      minForm.getOKButton().setEnabled(false);
      
      minForm.getENDButton().setEnabled(true);
      
      MUtil.requestFocus(minForm.getENDButton());
    }
    else {
      minForm.getOKButton().setEnabled(true);
      minForm.getENDButton().setEnabled(true);
      MUtil.requestFocus(minForm.getOKButton());
    }
  }
  



  public void prepareNext(MForm mform)
    throws MError
  {
    Vector wrows = (Vector)mform.getActionObject();
    int j = mform.getActionObjectIndex();
    
    MRow wrow = null;
    boolean lastRow = false;
    if (wrows != null) {
      while (j < wrows.size()) {
        wrow = (MRow)wrows.elementAt(j);
        mform.setActionObjectIndex(++j);
        j = mform.getActionObjectIndex();
        if ((wrow.isValidRow()) || (wrow.isDeletedRow())) {
          break;
        }
        mform.setRow(null);
        wrow = null;
      }
      
      System.out.println("!*** Minsert while: " + wrow);
      lastRow = j >= wrows.size();
      if ((wrow == null) && (!multiRow)) {
        wrow = mform.getRow();
        System.out.println("!*** Minsert getRow: " + wrow);
      }
    }
    







    if (wrow == null) {
      mform.hideForm();
      return;
    }
    

    mform.setRow(wrow.Copy());
    mform.getRow().selectRow();
    mform.clearFormValues();
    
    mform.putClientProperty("woldzk", mform.getRow().getField("zk"));
    mform.getRow().setField("zk", "0");
    
    mform.setFormValues(mform.getRow().getRecord());
    
    mpnut.setForm(mform);
    mpnut.setRow(mform.getRow());
    
    mform.setFormValues(MUtil.expand(mform, "NoForce", mform.getFormValues()));
    
    mpnut.evalFormPnut(mform, "BF");
    
    mform.setFase(1);
    mform.setAtribFormat(mform.getCenterPanel(), minCommand.getName().trim());
    
    MButton wok = mform.getOKButton();
    wok.setEnabled(true);
    MButton wend = mform.getENDButton();
    wend.setEnabled(true);
    if (lastRow) { wend.setText(MUtil.lang("\"Terminar\""));
    }
    mform.showForm();
  }
  
  public void cancelOperation(MForm mform) throws MError
  {
    System.out.println("!*** Minsert Entrou no Cancel Insert: " + mform.getActionObject());
    if (mform.getActionObject() == null) {
      mform.hideForm();
      return;
    }
    
    System.out.println("!*** Minsert getActionObjectIndex: " + mform.getActionObjectIndex());
    
    Vector wrows = (Vector)mform.getActionObject();
    int j = mform.getActionObjectIndex();
    if (j >= wrows.size()) {
      mform.setActionObject(null);
      mform.hideForm();
    } else {
      prepareNext(mform);
    }
  }
}
