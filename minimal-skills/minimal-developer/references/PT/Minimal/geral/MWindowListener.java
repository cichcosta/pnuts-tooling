package PT.Minimal.geral;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;













class MWindowListener
  extends WindowAdapter
{
  MFrame minFrame;
  MDialog minDialog;
  MForm minForm;
  WindowEvent wWindowEvent;
  
  public MWindowListener(MForm wform)
  {
    minForm = wform;
  }
  








  public MWindowListener(MFrame wframe)
  {
    minFrame = wframe;
  }
  








  public MWindowListener(MDialog wdialog)
  {
    minDialog = wdialog;
  }
  
















  public void windowClosing(WindowEvent e)
  {
    wWindowEvent = e;
    if (minForm != null) {
      if (minForm.getMFrame().getBlocked()) {
        System.out.println("tentou fechar janela bloqueada:" + minForm.getMFrame());
      } else {
        try {
          MHE = new MHandleEvent("CLOSEWINDOW", this);
        } catch (MError err) { MHandleEvent MHE;
          err.show();
        } catch (RuntimeException ex) {
          new MError(ex).show();
        }
      }
    }
  }
  









  public void windowActivated(WindowEvent e)
  {
    wWindowEvent = e;
    System.out.println("windowActivated");
    if ((e.getWindow() instanceof MFrame)) {
      MFrame wframe = (MFrame)e.getWindow();
      
      if ((wframe.getBlocked()) && (MFrame.getFinalModalChild(wframe) != null))
      {

        MFrame wchild = MFrame.getFinalModalChild(wframe);
        
        System.out.println("windowActivated:" + wframe.getTitle() + ":" + wchild.getTitle());
        

        wchild.setVisible(true);
      }
    }
  }
  








  public void windowDeactivated(WindowEvent e)
  {
    wWindowEvent = e;
  }
  








  public void close()
  {
    minForm = null;
    minFrame = null;
    minDialog = null;
    wWindowEvent = null;
  }
}
