package PT.Minimal.actions;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MRow;

public abstract class MGerDDL
{
  public MGerDDL() {}
  
  public abstract String getDDL(MRow paramMRow, String paramString)
    throws MError;
  
  public abstract boolean existsTable(String paramString, MCursor paramMCursor)
    throws MError;
  
  public abstract boolean existsIndex(String paramString, MCursor paramMCursor, MRow paramMRow)
    throws MError;
  
  public abstract boolean existsColumn(String paramString1, String paramString2, boolean paramBoolean, String paramString3, int paramInt1, int paramInt2, MCursor paramMCursor)
    throws MError;
  
  public abstract boolean syncTable(String paramString1, String paramString2, MCursor paramMCursor, boolean paramBoolean)
    throws MError;
  
  public abstract void copySyncTable(String paramString, MCursor paramMCursor, boolean paramBoolean)
    throws MError;
}
