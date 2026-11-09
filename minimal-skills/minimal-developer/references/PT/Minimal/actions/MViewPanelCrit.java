package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MDialog;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.pnuts.;
import java.io.PrintStream;

public class MViewPanelCrit implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  java.util.Vector wrows;
  private String wpanel;
  private String wpanelname = " ";
  private String wpaneltype = "MC";
  private String wconnect = "JMETADATA";
  
  private String[] wargs;
  
  public MViewPanelCrit() {}
  
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
    wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getCallerArgs(), ',');
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
    if ((minCommand.getName().equals("VIEWPANELCRIT")) && (minForm.getFase() != 61)) {
      show_viewPanel();
    }
    if ((minCommand.getName().equals("VIEWPANELCRIT")) && (minForm.getFase() == 61)) {
      do_viewPanel();
    }
  }
  
  public void show_viewPanel() throws MError {
    MForm wfmc = MForm.getMForm(wpanel + wpaneltype);
    try {
      if (wfmc == null) {
        System.out.println(wpanel);
        System.out.println(minForm.getEntName());
        wfmc = new MForm(wpanel + wpaneltype);
        wfmc.setEnt(minForm.getEnt());
        wfmc.setCenterPanel(MForm.getPanel(wpanel, wpaneltype, wconnect));
        wfmc.prepareForm();
        MDialog wd = new MDialog(minForm.getMFrame(), minForm.getEntName() + " - " + wpanelname, true, wfmc, 3);
        getBotPanelgetInsetsright = 10;
        getCenterPanelgetInsetsright = 10;
        getCenterPanelgetInsetsleft = 10;
        wfmc.prepareForm();
        wd.pack();
        wd.setSize(wfmc.getPreferredSize());
        wd.setCentered();
      }
      

      ((MDialog)wfmc.getFormParent()).setTitle(minForm.getEntName() + " - " + wpanelname);
      wfmc.setAtribFormat(wfmc.getCenterPanel(), minForm.getAction());
      lastContainer = minForm.getCenterPanel();
      wfmc.setAction(minCommand.getName());
      wfmc.setFase(61);
      



      wfmc.setRow(minForm.getRow());
      PT.Minimal.geral.MUtil.MoveRecord(minForm.getCritRecord(), wfmc.getCenterPanel());
      wfmc.setActionObject(minForm.getFormComponentsRecord());
       mpnut = .getMPnut(wfmc.getEnt());
      mpnut.put(wfmc.getName(), wfmc);
      mpnut.evalFormPnut(wfmc, "BF");
      mpnut.remove(wfmc.getName());
      






      wfmc.showForm();
    }
    catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
    wrows = null;
  }
  
  public void do_viewPanel() throws MError {
    PT.Minimal.geral.MUtil.MoveRecordElements(minForm.getFormComponentsRecord(), (java.util.Hashtable)minForm.getActionObject());
    minForm.hideForm();
  }
}
