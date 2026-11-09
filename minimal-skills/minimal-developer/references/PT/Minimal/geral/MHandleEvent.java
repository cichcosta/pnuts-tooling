package PT.Minimal.geral;

import PT.Minimal.actions.MHandleCommand;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.EventListener;















public class MHandleEvent
{
  public MFrame minFrame;
  public MDialog minDialog;
  private MForm minForm;
  private Window minWindow;
  public Component minComponent;
  public String minCmd;
  public EventListener minListener;
  
  public MHandleEvent(String wcommand, EventListener wlistener)
    throws MError
  {
    minCmd = wcommand;
    minListener = wlistener;
    






    if ((wlistener instanceof MActionListener)) {
      minForm = minForm;
      if ((wActionEvent.getSource() instanceof Component)) {
        minComponent = ((Component)wActionEvent.getSource());
      }
    }
    

    if ((wlistener instanceof MMouseListener)) {
      minForm = minForm;
      minComponent = wMouseEvent.getComponent();
    }
    
    if ((wlistener instanceof MWindowListener)) {
      minForm = minForm;
      minComponent = wWindowEvent.getComponent();
    }
    
    if ((wlistener instanceof MKeyListener)) {
      minForm = minForm;
      minComponent = wKeyEvent.getComponent();
    }
    










    MHandleCommand wcomm;
    










    if ((minForm != null) && 
      (!minForm.getMFrame().getBlocked())) {
      minForm.setLastCmd(minCmd);
      wcomm = new MHandleCommand(minFrame, minDialog, minForm, minWindow, minComponent, minCmd);
    }
  }
}
