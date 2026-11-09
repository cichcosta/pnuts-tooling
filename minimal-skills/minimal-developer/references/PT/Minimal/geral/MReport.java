package PT.Minimal.geral;

import PT.Minimal.pnuts.MPnuts;

public abstract interface MReport
{
  public abstract void prepareReport()
    throws MError;
  
  public abstract void closeReport()
    throws MError;
  
  public abstract void prepareCursor(String paramString1, String paramString2)
    throws MError;
  
  public abstract boolean hasMoreThen(String paramString, int paramInt)
    throws MError;
  
  public abstract String getCursorRow(String paramString, int paramInt)
    throws MError;
  
  public abstract String getReportFileName()
    throws MError;
  
  public abstract MPnuts getMPnut();
  
  public abstract String[] getArgs();
}
