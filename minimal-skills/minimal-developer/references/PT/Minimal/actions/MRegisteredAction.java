package PT.Minimal.actions;

import PT.Minimal.geral.MError;

public abstract interface MRegisteredAction
{
  public abstract void register(String paramString);
  
  public abstract void registerCall(String paramString)
    throws MError;
}
