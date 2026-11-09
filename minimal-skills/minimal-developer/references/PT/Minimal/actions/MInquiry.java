package PT.Minimal.actions;

import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.util.Vector;

public class MInquiry implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean isValid = true;
  
  public MInquiry() {}
  
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
        
        System.out.println(minCommand.getCallerArgs());
        cancelOperation(minForm);
        isValid = false;
      }
    }
    if (isValid) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      if ((minCommand.getName().equals("INQUIRY_ROW")) && (minForm.getFase() == 0)) {
        show_inquiry_row();
      }
      if ((minCommand.getName().equals("INQUIRY_ROW")) && (minForm.getFase() == 1)) {
        do_inquiry_row();
      }
    }
  }
  
  public void show_inquiry_row() throws MError {
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
      MButton wok = (MButton)MUtil.getComponent(mform.getBotPanel(), "wok");
      wok.setEnabled(false);
      MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
      wend.setCommand(minCommand.getName());
      wend.setCommandArgs("CANCEL");
      wend.setText(MUtil.lang("\"Seguinte\""));
      wend.setEnabled(true);
      
      if (minForm.getBrowse() != null) {
        mform.setBrowse(minForm.getBrowse());
      }
      prepareNext(mform);
    } catch (MError ex) {
      mform.hideForm();
      ex.show(minForm.getMFrame());
    }
  }
  








  public void do_inquiry_row()
  {
    try
    {
      minForm.setActionObjectIndex(minForm.getActionObjectIndex() + 1);
      prepareNext(minForm);
    } catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
  }
  
  public void prepareNext(MForm mform) throws MError {
    Vector wrows = (Vector)mform.getActionObject();
    int j = mform.getActionObjectIndex();
    
    PT.Minimal.geral.MRow wrow = null;
    if (wrows != null) {
      while (j < wrows.size()) {
        wrow = (PT.Minimal.geral.MRow)wrows.elementAt(j);
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
    
    mform.setRow(wrow);
    mpnut.setForm(mform);
    mpnut.setRow(mform.getRow());
    



    mform.clearFormValues();
    mform.setFormValues(mform.getRow().getRecord());
    mform.setFormValues(MUtil.expand(mform, "NoForce", mform.getFormValues()));
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
