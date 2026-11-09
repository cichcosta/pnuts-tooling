package PT.Minimal.entities;

import PT.Minimal.actions.MExternalCommand;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import java.util.Vector;

public class SMAN0 implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  String EOL = "\n";
  
  public SMAN0() {}
  
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
  
  public void execute()
    throws MError
  {}
}
