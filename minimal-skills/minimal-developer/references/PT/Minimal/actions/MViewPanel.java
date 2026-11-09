package PT.Minimal.actions;

import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.Vector;

public class MViewPanel implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean isValid = true;
  
  private String wpanel;
  private String wpanelname = " ";
  private String wpaneltype = "ME";
  private String went;
  private String wconnect = "JMETADATA";
  
  private String waction;
  
  private String[] wargs;
  
  public MViewPanel() {}
  
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
  
  public void execute() throws MError {
    System.out.println("!*** MViewPanel Args:" + minCommand.getArgs());
    System.out.println("!*** MViewPanel callerArgs:" + minCommand.getCallerArgs());
    wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    if (wargs == null) {
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    System.out.println("!*** MViewPanel wargs: " + wargs.length);
    if (wargs.length < 1) {
      throw new MError("Não existe painel indicado ...");
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      if (minCommand.getCallerArgs().startsWith("CANCEL")) {
        System.out.println("!*** MViewPanel minCommand: " + minCommand.getCallerArgs());
        if (minForm.getFase() == 62) {
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
      System.out.println("!*** MViewPanel Com/Fase: " + minCommand.getName() + " " + minForm.getFase());
      if ((minCommand.getName().startsWith("VIEWPANEL")) && (minForm.getFase() == 61)) {
        try {
          do_row();
        } catch (MError err) {
          err.show(minForm.getMFrame());
        }
        cancelOperation(minForm);
        return;
      }
      if ((minCommand.getName().startsWith("VIEWPANEL")) && (minForm.getFase() == 62)) {
        prepareNext(minForm);
        return;
      }
      if (minCommand.getName().startsWith("VIEWPANEL"))
      {
        if (wargs.length > 1) {
          wpanel = wargs[0].toUpperCase();
          went = wpanel.substring(0, 5);
        }
        if ((wpanel.equals("NULL")) && (wrows != null)) {
          wpanel = ((String)((MRow)wrows.firstElement()).get("i_ob") + "ME");
        }
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
        } else {
          waction = " ";
        }
        if ((mpnut == null) && (!waction.equals("PROTOTYPE"))) {
          mpnut = .findMPnut(went);
        }
        show_row();
        return;
      }
    }
  }
  
  public void show_row() throws MError {
    MForm mform = MForm.getMForm(wpanel + wpaneltype);
    try {
      if (mform == null) {
        mform = new MForm(wpanel + wpaneltype);
        mform.setEnt(went);
        mform.setEntName(wpanelname);
        System.out.println("!*** MViewPanel TESTEFA pan/ent: " + wpanel + wpaneltype + " " + went);
        

        mform.setCenterPanel(MForm.getPanel(wpanel, wpaneltype, wconnect));
        
        mform.setBotPanel(mform.constrBotPanel(3));
        mform.showStatusBar(true);
        MButton wok = (MButton)MUtil.getComponent(mform.getBotPanel(), "wok");
        mform.setOKButton(wok);
        MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
        mform.setENDButton(wend);
        javax.swing.JMenuBar wmenubar = PT.Minimal.geral.MMenu.getMinMenuBar(wpanel + "ME");
        if (wmenubar != null) {
          mform.addMenuBar(wmenubar);
        }
        mform.prepareForm();
        
        MFrame wframe = new MFrame();
        wframe.setTitle(wpanel + wpaneltype);
        
        wframe.setForm(mform);
        wframe.pack();
        wframe.setSize(mform.getPreferredSize());
        
        wframe.center(getPreferredSizewidth, getPreferredSizeheight + 20, false);
        
        if (mpnut != null) {
          mpnut.put(mform.getName(), mform);
        }
      }
      if (mpnut != null) {
        mpnut.setForm(mform);
        mpnut.setRow(null);
      }
      MFrame.setModalParent(minForm.getMFrame(), mform.getMFrame());
      ((MFrame)mform.getFormParent()).setTitle(wpanelname + " - " + minCommand.getMenuText());
      
      if (minForm.getFase() != 1) {
        waction = "INQUIRY_ROW";
        mform.setAction("INQUIRY_ROW");
        mform.setAtribFormat(mform.getCenterPanel(), "INQUIRY_ROW");
      } else {
        mform.setAction(minCommand.getName() + "," + minForm.getAction());
        mform.setAtribFormat(mform.getCenterPanel(), mform.getAction());
      }
      mform.setFase(61);
      mform.setActionObject(wrows);
      mform.setActionObjectIndex(0);
      MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
      wend.setCommand(mform.getAction());
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
  

  public void do_row()
    throws MError
  {
    if (mpnut == null) {
      mpnut = .findMPnut(minForm.getEnt());
    }
    mpnut.setForm(minForm);
    mpnut.setRow(minForm.getRow());
    

    MUtil.validate(minForm, "Force");
    
    minForm.setFormValues(MUtil.expand(minForm, "Force", minForm.getFormValues()));
    
    mpnut.evalFormPnut(minForm, "AF");
    MUtil.MoveRecord(minForm.getFormValues(), minForm.getRow().getRecord());
    
    minForm.setActionObjectIndex(minForm.getActionObjectIndex() + 1);
    


    minForm.setFase(62);
    System.out.println("!*** MViewPanel modelo em 2;" + minForm.getFase());
    minForm.setAtribFormat(minForm.getCenterPanel(), "INQUIRY_ROW");
    minForm.getOKButton().setEnabled(false);
    minForm.getENDButton().setEnabled(true);
    MUtil.requestFocus(minForm.getENDButton());
  }
  
  public void prepareNext(MForm mform) throws MError {
    if (mpnut == null) {
      mpnut = .findMPnut(minForm.getEnt());
    }
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
    if (mpnut != null) {
      mform.setRow(wrow);
      mform.clearFormValues();
      mform.setFormValues(mform.getRow().getRecord());
      mpnut.setForm(mform);
      mpnut.setRow(mform.getRow());
      mform.setFormValues(MUtil.expand(mform, "NoForce", mform.getFormValues()));
      mpnut.evalFormPnut(mform, "BF");
    }
    mform.setFase(61);
    mform.setAtribFormat(mform.getCenterPanel(), mform.getAction());
    mform.getOKButton().setEnabled(true);
    mform.getENDButton().setEnabled(true);
    MUtil.requestFocus(mform.getOKButton());
    

    mform.showForm(false);
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
