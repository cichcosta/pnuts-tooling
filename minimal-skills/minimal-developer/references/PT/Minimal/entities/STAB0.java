package PT.Minimal.entities;

import PT.Minimal.actions.MExternalCommand;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import java.util.Vector;

public class STAB0 implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  String EOL = "\n";
  
  public STAB0() {}
  
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
    if ((minCommand.getName().equals("MAKEDDL")) && (minForm.getFase() != 101) && 
      (wrows.size() == 0)) {
      throw new MError("MIN-000103W");
    }
  }
}
