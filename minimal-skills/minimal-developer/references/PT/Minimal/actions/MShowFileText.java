package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JEditorPane;

public class MShowFileText implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  public static javax.swing.text.JTextComponent.KeyBinding[] defaultBindings = { new javax.swing.text.JTextComponent.KeyBinding(javax.swing.KeyStroke.getKeyStroke(35, 2), "caret-end"), new javax.swing.text.JTextComponent.KeyBinding(javax.swing.KeyStroke.getKeyStroke(36, 2), "caret-begin") };
  







  private boolean isValid = true;
  
  private String wobj;
  private String wobjname;
  private String[] wargs;
  private java.awt.Dimension wdim = new java.awt.Dimension(525, 350);
  
  public MShowFileText() {}
  
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
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    System.out.println(wargs.length);
    System.out.println(minCommand.getCallerArgs());
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
    
    if (((wargs == null) || (wargs.length > 2)) && 
      (wargs[2].startsWith("CANCEL"))) {
      System.out.println(wargs[2]);
      cancelOperation(minForm);
      isValid = false;
    }
    
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
  























































  private void showAssText()
    throws MError
  {}
  























































  public void prepareNext(MForm wfmc)
    throws MError
  {
    Vector wrows = (Vector)wfmc.getActionObject();
    int j = wfmc.getActionObjectIndex();
    if (j >= wrows.size()) {
      wfmc.hideForm();
    }
    if (j < wrows.size()) {
      JEditorPane minText = (JEditorPane)lastContainer;
      


      wfmc.setRow((MRow)wrows.elementAt(j));
      
      mpnut.setRow(wfmc.getRow());
      
      String wtext = "";
      
      String wfilename = mpnut.evalFormatedValues(wargs[0]);
      File wfile = new File(wfilename);
      if (wfile.exists()) {
        try {
          BufferedInputStream wstream = new BufferedInputStream(new java.io.FileInputStream(wfile));
          

          byte[] b = new byte[(int)wfile.length()];
          wstream.read(b, 0, b.length);
          wstream.close();
          wtext = new String(b);
        } catch (java.io.FileNotFoundException e) {
          System.err.println("FileStreamsTest: " + e);
          throw new MError("Ficheiro não encontrado");
        } catch (IOException e) {
          System.err.println("FileStreamsTest: " + e);
          throw new MError("Ficheiro não encontrado");
        }
      }
      try
      {
        minText.read(new java.io.StringReader(wtext), null);
      } catch (IOException ex) {
        throw new MError(ex);
      }
      
      if (((String)((MRow)wrows.elementAt(j)).getField("zcst")).toLowerCase().startsWith("r")) {
        minText.setEditable(false);
      } else {
        minText.setEditable(true);
      }
      
      wfmc.showForm();
    }
  }
  
  private void saveAssText()
    throws MError
  {
    JEditorPane minText = (JEditorPane)minForm.lastContainer;
    String wtext = minText.getText();
    if (minText.isEditable()) {
      mpnut.setRow(minForm.getRow());
      String wfilename = mpnut.evalFormatedValues(wargs[0]);
      try {
        File wfile = new File(wfilename);
        java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(wfile));
        fos.write(wtext.getBytes());
        fos.close();
      } catch (IOException e) {
        System.err.println("FileStreamsTest: " + e);
        throw new MError("Ficheiro não encontrado");
      }
    }
    minForm.setActionObjectIndex(minForm.getActionObjectIndex() + 1);
    prepareNext(minForm);
  }
  
  public void cancelOperation(MForm wfmc) throws MError {
    wfmc.setActionObjectIndex(wfmc.getActionObjectIndex() + 1);
    prepareNext(wfmc);
  }
}
