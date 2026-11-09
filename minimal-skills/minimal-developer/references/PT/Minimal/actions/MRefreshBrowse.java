package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MUtil;
import java.util.Vector;

public class MRefreshBrowse implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private String wobj;
  private String wobjname;
  private String[] wargs;
  
  public MRefreshBrowse() {}
  
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
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    
    String wnfname = minForm.getEnt() + "MC";
    MForm wnform = MForm.getMForm(wnfname);
    if (wnform == null) {
      throw new PT.Minimal.geral.MError("OPERAÇÃO NÃO DISPONÍVEL EM SALTO!");
    }
    wnform.setFase(1);
    
    MCommand wdocrit = new MCommand(minForm.getEnt(), "CRITERIUM_ROW");
    MCriterium wcri = new MCriterium("BR");
    wcri.setMForm(wnform);
    wcri.setMCommand(wdocrit);
    wcri.execute();
  }
}
