package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MDialog;
import PT.Minimal.geral.MForm;
import java.util.Vector;

public class MShowZAtribs implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private String wobj;
  private String wobjname;
  private String[] wargs;
  
  public MShowZAtribs() {}
  
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
  
  public void execute() throws PT.Minimal.geral.MError {
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getArgs(), ',');
    }
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      PT.Minimal.geral.MRow wr = minForm.getRow().Copy();
      PT.Minimal.geral.MUtil.MoveRecord(PT.Minimal.geral.MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    showZAtribs();
  }
  
  private void showZAtribs() throws PT.Minimal.geral.MError {
    String wpanel = "ZEMC0";
    String wpaneltype = "ME";
    String wconnect = "JMETADATA";
    
    MForm wfmc = MForm.getMForm(wpanel + wpaneltype);
    if (wfmc == null) {
      System.out.println(wpanel);
      System.out.println(minForm.getEntName());
      wfmc = new MForm(wpanel + wpaneltype);
      wfmc.setEnt(minForm.getEnt());
      wfmc.setCenterPanel(MForm.getPanel(wpanel, wpaneltype, wconnect));
      wfmc.showStatusBar(true);
      wfmc.prepareForm();
      MDialog wd = new MDialog(minForm.getMFrame(), "Atributos de Controle", true, wfmc, 1);
      getBotPanelgetInsetsright = 10;
      getCenterPanelgetInsetsright = 10;
      getCenterPanelgetInsetsleft = 10;
      wfmc.prepareForm();
      wd.pack();
      wd.setSize(wfmc.getPreferredSize());
      wd.setCentered();
    }
    

    wfmc.setAtribFormat(wfmc.getCenterPanel(), "DELETE_ROW");
    lastContainer = minForm.getCenterPanel();
    wfmc.setAction("NONE");
    wfmc.setFase(401);
    for (int j = 0; j < wrows.size(); j++) {
      wfmc.setFormValues(((PT.Minimal.geral.MRow)wrows.elementAt(j)).getRecord());
      
      wfmc.showForm();
    }
    wrows = null;
  }
}
