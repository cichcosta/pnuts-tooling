package PT.Minimal.geral;

import PT.Minimal.actions.MHandleCommand;












public class MThread
  extends Thread
{
  public String sTName;
  public String minCmd;
  public MForm minForm;
  
  MThread(String sTName, String minCmd, MForm minForm)
  {
    this.sTName = sTName;
    this.minCmd = minCmd;
    this.minForm = minForm;
  }
  
  public void run() {
    MHandleCommand wcomm;
    if (sTName == "RUN") {
      wcomm = new MHandleCommand(minForm.getMFrame(), null, minForm, minForm.getMFrame(), null, minCmd);
    }
    if (sTName == "SLEEP") {
      try {
        sleep(30000L);
      }
      catch (InterruptedException e) {}
    }
  }
}
