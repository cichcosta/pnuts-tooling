package PT.Minimal.actions;

import PT.Minimal.geral.MBrowse;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import com.objectshare.grid.data.JKTableModel;
import java.util.Vector;



public class MChangeCurrency
  implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private String wnewcurrency;
  private String[] wargs;
  
  public MChangeCurrency() {}
  
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
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    

    if ((wargs == null) || (wargs.length < 1)) {
      throw new MError("Não existe opcão indicada ...");
    }
    
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      MUtil.MoveRecord(minForm.getFormValues(), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    if (minForm.getType() == "MULTIROW") {
      JKTableModel myModel = (JKTableModel)minForm.getBrowse().getModel();
      wrows = myModel.getRowVector();
    }
    wnewcurrency = wargs[0].toLowerCase().substring(0, 1);
    
    for (int j = 0; j < wrows.size(); j++) {
      MRow wr = (MRow)wrows.elementAt(j);
      wr.changeCurrency(wnewcurrency);
    }
    minForm.setCurrency(wnewcurrency);
    minForm.getRow().changeCurrency(wnewcurrency);
    minForm.getBrowse().calcTotal();
  }
}
