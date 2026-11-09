package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import java.util.Vector;

public abstract interface MExternalCommand
{
  public static final String EOL = "\n";
  
  public abstract void setMCommand(MCommand paramMCommand);
  
  public abstract void setMForm(MForm paramMForm);
  
  public abstract void setRows(Vector paramVector);
  
  public abstract void execute()
    throws MError;
}
