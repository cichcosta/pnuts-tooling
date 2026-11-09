package PT.Minimal.actions;

import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MRow;
import PT.Minimal.pnuts.;
import java.util.Vector;

public class MDelete implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean isValid = true;
  
  public MDelete() {}
  
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
  
  public void execute() throws MError
  {
    if (PT.Minimal.geral.MLogin.getUserTipo().indexOf("CS") != -1) {
      throw new MError("MIN-009998E", new String[] { PT.Minimal.geral.MLogin.getUser(), PT.Minimal.geral.MUtil.lang("\"executar este comando.\"") });
    }
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      if (minCommand.getCallerArgs().startsWith("CANCEL"))
      {
        if ((minForm.getBrowse() == null) || (!minForm.getBrowse().getName().startsWith(minForm.getEnt())))
        {
          MRegister.removeJump(minForm.getEnt());
        }
        
        if (minForm.getEnt() != null) {
          mpnut.evalFormPnut(minForm, "CN");
        }
        System.out.println("!*** MDelete: " + minCommand.getCallerArgs());
        cancelOperation(minForm);
        isValid = false;
      }
    }
    if (isValid) {
      if ((minCommand.getName().equals("DELETE_ROW")) && (minForm.getFase() == 0)) {
        if (wrows.size() == 0) {
          throw new MError("MIN-000103W");
        }
        show_delete_row();
      }
      if ((minCommand.getName().equals("DELETE_ROW")) && (minForm.getFase() == 1)) {
        try {
          do_delete_row();
        } catch (MError err) {
          err.show(minForm.getMFrame());
        }
      }
    }
  }
  
  public void show_delete_row() throws MError {
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
        
        MButton wok = (MButton)PT.Minimal.geral.MUtil.getComponent(mform.getBotPanel(), "wok");
        mform.setOKButton(wok);
        MButton wend = (MButton)PT.Minimal.geral.MUtil.getComponent(mform.getBotPanel(), "wend");
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
      MButton wend = (MButton)PT.Minimal.geral.MUtil.getComponent(mform.getBotPanel(), "wend");
      wend.setCommand(minCommand.getName());
      wend.setCommandArgs("CANCEL");
      wend.setText(PT.Minimal.geral.MUtil.lang("\"Seguinte\""));
      wend.setEnabled(true);
      MButton wok = (MButton)PT.Minimal.geral.MUtil.getComponent(mform.getBotPanel(), "wok");
      wok.setEnabled(true);
      if (minForm.getBrowse() != null) {
        mform.setBrowse(minForm.getBrowse());
      }
      
      prepareNext(mform);
    } catch (MError ex) {
      mform.hideForm();
      ex.show(minForm.getMFrame());
    }
  }
  


  public void do_delete_row()
    throws MError
  {
    PT.Minimal.geral.MCursor wupdate = PT.Minimal.geral.MConnection.getMCursor();
    try {
      wupdate.beginBatch();
      
      mpnut.setForm(minForm);
      mpnut.setRow(minForm.getRow());
      mpnut.evalFormPnut(minForm, "AF");
      
      System.out.println("ZK: " + minForm.getRow().getField("zk"));
      if (((String)minForm.getRow().getField("zk")).equals("0")) {
        throw new MError("MIN-000103E");
      }
      if (((String)minForm.getRow().getField("zk")).startsWith("*")) {
        throw new MError("MIN-000103E");
      }
      if ((minCommand.getZFase().startsWith("BF")) || (minCommand.getZFase().startsWith("BT"))) {
        minForm.getRow().setZCommand(minCommand.getName(), "BF", " ", wupdate);
        

        minForm.getRow().refreshRow(wupdate);
      }
      
      MRow wr = minForm.getRow().Copy();
      
      minForm.getRow().deleteRow(wupdate);
      
      if (minForm.getBrowse() != null) {
        minForm.getBrowse().refresh(minForm.getRow());
      }
      

      minForm.setActionObjectIndex(minForm.getActionObjectIndex() + 1);
      
      wupdate.commitBatch();
      minForm.getRow().setField("zk", "0");
    }
    finally {
      if (wupdate.inBeginBatch()) {
        wupdate.rollbackBatch();
      }
      wupdate.close();
    }
    
    prepareNext(minForm);
  }
  
  public void prepareNext(MForm mform) throws MError {
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
      mform.getENDButton().setText(PT.Minimal.geral.MUtil.lang("\"Terminar\""));
    }
    
    mform.setRow(wrow);
    if (((String)mform.getRow().getField("zcst")).toLowerCase().startsWith("r")) {
      mform.hideForm();
      throw new MError("MIN-002030E");
    }
    
    mpnut.setForm(mform);
    mpnut.setRow(mform.getRow());
    mform.clearFormValues();
    mform.setFormValues(mform.getRow().getRecord());
    mform.setFormValues(PT.Minimal.geral.MUtil.expand(mform, "NoForce", mform.getFormValues()));
    mpnut.evalFormPnut(mform, "BF");
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
