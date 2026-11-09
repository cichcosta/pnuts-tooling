package PT.Minimal.actions;

import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MMail;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MTextArea;
import PT.Minimal.geral.MTextField;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

public class MSendMail implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean isValid = true;
  
  public MSendMail() {}
  
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
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0) && 
      (minCommand.getCallerArgs().startsWith("CANCEL"))) {
      System.out.println(minCommand.getCallerArgs());
      isValid = false;
      cancelOperation(minForm);
    }
    
    MForm mform = MForm.getMForm(minForm.getEnt() + "MSML");
    if (isValid) {
      if ((minCommand.getName().equals("MAIL_ROW")) && (minForm.getFase() == 0)) {
        if (wrows.size() == 0) {
          throw new MError("MIN-000103W");
        }
        show_mail_row();
      }
      if ((minCommand.getName().equals("MAIL_ROW")) && (minForm.getFase() == 1)) {
        try {
          do_mail_row();
        } catch (MError err) {
          err.show(minForm.getMFrame());
        }
      }
      if ((minCommand.getName().equals("SEND_MAIL")) && (minForm.getFase() == 1)) {
        try {
          sendMail();
        } catch (MError err) {
          err.show(minForm.getMFrame());
        }
      }
      if ((minCommand.getName().equals("BROWSE_ATTACH")) && (minForm.getFase() == 1)) {
        try {
          browseFile(mform);
        } catch (MError err) {
          err.show(minForm.getMFrame());
        }
      }
    }
  }
  
  public void show_mail_row() throws MError {
    if (wrows.size() == 0) {
      wrows.addElement(minForm.getRow().Copy());
    }
    MForm mform = MForm.getMForm(minForm.getEnt() + "MSML");
    try {
      if (mform == null) {
        mform = new MForm(minForm.getEnt() + "MSML");
        mform.setSize(new Dimension(500, 300));
        mform.setEnt(minForm.getEnt());
        mform.setEntName(minForm.getEntName());
        
        mform.setCenterPanel(MForm.getPanelCopy("ZSML0ME", minForm.getEnt() + "PSML"));
        mform.setBotPanel(MForm.getPanel(minForm.getEnt() + "MEBT"));
        mform.showStatusBar(true);
        MButton wok = (MButton)MUtil.getComponent(mform.getBotPanel(), "wok");
        mform.setOKButton(wok);
        MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
        mform.setENDButton(wend);
        
        javax.swing.JMenuBar wmenubar = PT.Minimal.geral.MMenu.getMinMenuBar("ZSML0ME");
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
      wend.setEnabled(true);
      MButton wok = (MButton)MUtil.getComponent(mform.getBotPanel(), "wok");
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
  
  public void do_mail_row() throws MError
  {
    MUtil.validate(minForm, "Force");
    sendMail();
    prepareNext(minForm);
  }
  
  public void prepareNext(MForm mform) throws MError
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
    
    mform.setRow(wrow);
    if (((String)mform.getRow().getField("zcst")).toLowerCase().startsWith("r"))
    {
      mform.setAtribFormat(mform.getCenterPanel(), "INQUIRY_ROW");
    } else {
      mform.setAtribFormat(mform.getCenterPanel(), "INSERT_ROW");
    }
    
    mform.clearFormValues();
    mform.setFormValues(mform.getRow().getRecord());
    mform.setFormValues(MUtil.expand(mform, "NoForce", mform.getFormValues()));
    
    minForm.setAtribFormat(minForm.getCenterPanel(), "INSERT_ROW");
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
  
  public void sendMail()
  {
    MUtil.validate(minForm, "Force");
    
    MForm mform = MForm.getMForm(minForm.getEnt() + "MSML");
    MMail msg = new MMail();
    
    msg.setMailServer(((MTextField)mform.getFormComponent("tfserver")).getFormatedText());
    msg.setFrom(((MTextField)mform.getFormComponent("tfde")).getFormatedText());
    msg.setTo(((MTextField)mform.getFormComponent("tfpara")).getFormatedText());
    msg.setSubject(((MTextField)mform.getFormComponent("tfsubject")).getFormatedText());
    msg.setText(((MTextArea)mform.getFormComponent("tatexto")).getTextArea().getText());
    msg.setAttachments(((MTextArea)mform.getFormComponent("tatexto")).getAttachments());
    msg.sendMail();
    ((MTextArea)mform.getFormComponent("tatexto")).getAttachments().removeAllElements();
    ((MTextArea)mform.getFormComponent("tatexto")).getTextArea().setText(null);
  }
  
  void browseFile(MForm mform)
  {
    String filename = new String();
    String path = new String();
    JFileChooser chooser = new JFileChooser();
    String sAttach = ((MTextField)mform.getFormComponent("tfattach")).getFormatedText();
    int returnVal = chooser.showOpenDialog(new java.awt.Panel());
    if (returnVal == 0) {
      path = chooser.getCurrentDirectory() + System.getProperty("file.separator");
      filename = chooser.getSelectedFile().getName();
      if (sAttach == null) {
        ((MTextField)mform.getFormComponent("tfattach")).setText(filename + ";");
      } else {
        ((MTextField)mform.getFormComponent("tfattach")).setText(sAttach + filename + ";");
      }
      ((MTextArea)mform.getFormComponent("tatexto")).addAttach(path + filename);
      System.out.println("VECTOR = " + ((MTextArea)mform.getFormComponent("tatexto")).getAttachments());
    }
  }
}
