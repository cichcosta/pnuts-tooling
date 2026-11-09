package PT.Minimal.geral;

import PT.Minimal.actions.MRegister;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import javax.swing.AbstractButton;
import javax.swing.JPasswordField;





class MActionListener
  implements ActionListener, Runnable
{
  MForm minForm;
  MFrame minFrame;
  MDialog minDialog;
  String minCommand;
  ActionEvent wActionEvent;
  private Thread clockThread = null;
  



  private Thread verificaThread;
  




  public MActionListener(MFrame wframe)
  {
    minFrame = wframe;
  }
  









  public MActionListener(MForm wform)
  {
    minForm = wform;
  }
  









  public MActionListener(MDialog wdialog)
  {
    minDialog = wdialog;
  }
  








  public void actionPerformed(ActionEvent e)
  {
    System.out.println("!*** MActionListener actionPerformed: " + minForm + " " + minForm.getEnt() + " " + e + " " + e.getActionCommand());
    




    if ((minForm != null) && (e.getActionCommand() == "CLOSEWINDOW")) {
      MRegister.removeJump(minForm.getEnt());
    }
    

    try
    {
      wActionEvent = e;
      MHandleEvent MHE;
      if ((e.getSource() instanceof Button)) {
        minCommand = ((Button)e.getSource()).getActionCommand();
        
        if ((clockThread == null) && (minForm.getName().trim() != "Menu Minimal")) {}
        



        MHE = new MHandleEvent(minCommand, this);
      } else { MHandleEvent MHE;
        if ((e.getSource() instanceof AbstractButton))
        {

          minCommand = ((AbstractButton)e.getSource()).getActionCommand();
          if ((e.getSource() instanceof MButton)) {
            MButton bt = (MButton)e.getSource();
            
            if (!bt.hasFocus()) {
              bt.requestFocus();
            }
          }
          
          if ((clockThread == null) && (minForm.getName().trim() != "Menu Minimal")) {}
          


          MHE = new MHandleEvent(minCommand, this);
        } else if ((e.getSource() instanceof MTextField)) {
          MTextField wtf = (MTextField)e.getSource();
          MHandleEvent MHE;
          if ((wtf.getCommand() != null) && (wtf.getCommand().trim().length() != 0)) {
            minCommand = (wtf.getCommand() + wActionEvent.getActionCommand());
            wtf.setText("");
            MHE = new MHandleEvent(minCommand, this);











          }
          else if (minForm.getOKButton() != null) {
            minForm.setFormValue(wtf.getName(), wtf.getText());
            


            minForm.getOKButton().doClick();
          }
        }
        else if ((e.getSource() instanceof JPasswordField)) {
          minForm.getOKButton().doClick();
        }
      }
    }
    catch (MError err) {
      err.show();
    } catch (RuntimeException ex) {
      new MError(ex).show();
    }
    


    if (clockThread != null) {}
  }
  












  public void close()
  {
    System.out.println("!*** MActionListener clone");
    minForm = null;
    minFrame = null;
    minDialog = null;
    minCommand = null;
    wActionEvent = null;
  }
  




















  public void run()
  {
    clockThread = null;
  }
}
