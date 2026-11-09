package PT.Minimal.actions;

import PT.Minimal.editor.MEditor;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.Vector;

public class MShowAssText implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean isValid = true;
  private String editMode = "NORMAL";
  
  private String wobj;
  private String wobjname;
  private String[] wargs;
  private Dimension wdim = new Dimension(525, 350);
  
  public MShowAssText() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm mform) {
    minForm = mform;
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
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    System.out.println("!*** MShowAssText wargs.length: " + wargs.length);
    System.out.println("!*** MShowAssText minCommand: " + minCommand.getArgs());
    if ((wargs == null) || (wargs.length < 2)) {
      throw new MError("Não existe opcão indicada ...");
    }
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    wobj = wargs[0].toUpperCase();
    wobjname = wargs[1];
    if ((wargs.length > 2) && (!wargs[2].startsWith("CANCEL")) && (!wargs[2].startsWith("EDIT"))) {
      wobj = (wobj + "-" + wargs[2]);
    }
    

    if (wargs.length > 2) {
      if (wargs[2].startsWith("CANCEL")) {
        System.out.println("!*** MShowAssText wargs[2]: " + wargs[2]);
        cancelOperation(minForm);
        isValid = false;
      } else {
        editMode = wargs[2].trim();
      }
    }
    System.out.println("!*** MShowAssText editMode:" + editMode + " Valid: " + isValid);
    if (isValid) {
      if (minForm.getFase() != 401) {
        if (wrows.size() == 0) {
          throw new MError("MIN-000103W");
        }
        showAssText();
      }
      if (minForm.getFase() == 401) {
        saveAssText();
      }
    }
  }
  
  private void showAssText() throws MError {
    String wfname = "TEXT" + wobj + minForm.getEnt();
    System.out.println("!*** MShowAssText wfname: " + wfname);
    MEditor meditor = (MEditor)MForm.getMForm(wfname);
    
    if (meditor == null)
    {
      String type = "text/plain";
      if ((minForm.getEnt().startsWith("MJEX")) && (Boolean.valueOf(System.getProperties().getProperty("PT.Minimal.pnutsColor", "false")).booleanValue()) && (MUtil.classExists("sun.tools.java.Constants")))
      {


        type = "text/java";
      }
      meditor = new MEditor(wfname, minForm.getEnt(), minForm.getEntName(), type);
      
      PT.Minimal.geral.MButton wend = meditor.getENDButton();
      wend.setCommand(minCommand.getName());
      wend.setCommandArgs(wobj + "," + wobjname + "," + "CANCEL");
      
      MFrame wframe = new MFrame();
      wframe.setTitle(minForm.getEntName() + " - " + wobjname);
      
      wframe.setForm(meditor);
      wframe.pack();
      wframe.center(wdim.width, wdim.height, false);
      mpnut.put(meditor.getName(), meditor);
    }
    

    mpnut.setForm(meditor);
    mpnut.setRow(null);
    MFrame.setModalParent(minForm.getMFrame(), meditor.getMFrame());
    
    ((MFrame)meditor.getFormParent()).setTitle(minForm.getEntName() + " - " + wobjname);
    meditor.setAtribFormat(meditor.getCenterPanel(), minCommand.getName().trim());
    meditor.setAction(minCommand.getName());
    meditor.setActionArgs(wobj);
    meditor.setFase(401);
    meditor.setActionObject(wrows);
    meditor.setActionObjectIndex(0);
    if (minForm.getBrowse() != null) {
      meditor.setBrowse(minForm.getBrowse());
    }
    prepareNext(meditor);
  }
  
  public void prepareNext(MEditor meditor) throws MError
  {
    Vector wrows = (Vector)meditor.getActionObject();
    int j = meditor.getActionObjectIndex();
    System.out.println("!*** MShowAssText prepareNext 1: " + j + " " + wrows.size());
    
    if (j >= wrows.size()) {
      meditor.hideForm();
    }
    if (j < wrows.size()) {
      MRow wr = (MRow)wrows.elementAt(j);
      if (((String)wr.getField("zk")).equals("0")) {
        meditor.hideForm();
        throw new MError("MIN-000103E");
      }
      if (((String)wr.getField("zk")).startsWith("*")) {
        meditor.hideForm();
        throw new MError("MIN-000103E");
      }
      
      meditor.setRow(wr);
      
      String wtext = " ";
      byte[] wobjbytes = meditor.getRow().getAssociatedObject(wobj);
      System.out.println("!*** MShowAssText prepareNext 2: " + wobj + " " + wobjbytes);
      if (wobjbytes != null) {
        wtext = new String(wobjbytes);
      }
      meditor.setText(wtext);
      
      boolean editable = !((String)((MRow)wrows.elementAt(j)).getField("zcst")).toLowerCase().startsWith("r");
      if (editMode.equals("EDIT")) {
        meditor.setEditable(true);
      }
      if (editMode.equals("NORMAL")) {
        meditor.setEditable(editable);
      }
      if (editMode.equals("NOEDIT")) {
        meditor.setEditable(false);
      }
      
      meditor.revalidate();
      meditor.showForm(false);
    }
  }
  
  private void saveAssText() throws MError
  {
    MEditor meditor = (MEditor)minForm;
    String wtext = meditor.getText();
    wobj = meditor.getActionArgs();
    System.out.println("!*** MShowAssText saveAssText: " + wobj);
    
    MCursor wupdate = PT.Minimal.geral.MConnection.getMCursor();
    try {
      wupdate.beginWork();
      if (meditor.isEditable()) {
        meditor.getRow().setAssociatedObject(wobj, wtext.getBytes(), wupdate);
      }
      wupdate.commitWork();
    } finally {
      if (wupdate.inBeginWork()) {
        wupdate.rollbackWork();
      }
      wupdate.close();
    }
    meditor.setActionObjectIndex(meditor.getActionObjectIndex() + 1);
    prepareNext(meditor);
  }
  
  public void cancelOperation(MForm mform) throws MError {
    if (wrows == null) {
      mform.hideForm();
      return;
    }
    mform.setActionObjectIndex(mform.getActionObjectIndex() + 1);
    prepareNext((MEditor)mform);
  }
}
