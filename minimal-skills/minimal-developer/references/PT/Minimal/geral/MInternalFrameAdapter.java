package PT.Minimal.geral;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;



















class MInternalFrameAdapter
  extends InternalFrameAdapter
{
  MFrame minFrame;
  MDialog minDialog;
  MForm minForm;
  InternalFrameEvent wWindowEvent;
  
  public MInternalFrameAdapter(MForm wform)
  {
    minForm = wform;
  }
  








  public MInternalFrameAdapter(MFrame wframe)
  {
    minFrame = wframe;
  }
  








  public MInternalFrameAdapter(MDialog wdialog)
  {
    minDialog = wdialog;
  }
  















  public void internalFrameClosing(InternalFrameEvent e)
  {
    try
    {
      wWindowEvent = e;
      MHE = new MHandleEvent("CLOSEWINDOW", this);
    } catch (MError err) { MHandleEvent MHE;
      err.show();
    }
  }
}
