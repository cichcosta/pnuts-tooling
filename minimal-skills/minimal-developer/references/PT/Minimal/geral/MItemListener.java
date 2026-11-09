package PT.Minimal.geral;

import java.awt.Button;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JMenuItem;









class MItemListener
  implements ItemListener
{
  MForm minForm;
  MFrame minFrame;
  MDialog minDialog;
  String minCommand;
  ItemEvent wActionEvent;
  
  public MItemListener(MFrame wframe)
  {
    minFrame = wframe;
  }
  








  public MItemListener(MForm wform)
  {
    minForm = wform;
  }
  








  public MItemListener(MDialog wdialog)
  {
    minDialog = wdialog;
  }
  







  public void itemStateChanged(ItemEvent e)
  {
    try
    {
      wActionEvent = e;
      MHandleEvent MHE; if ((e.getSource() instanceof Button)) {
        minCommand = ((Button)e.getSource()).getActionCommand();
        MHE = new MHandleEvent(minCommand, this); }
      MHandleEvent MHE;
      if ((e.getSource() instanceof JButton)) {
        minCommand = ((JButton)e.getSource()).getActionCommand();
        if ((e.getSource() instanceof MButton)) {
          MButton bt = (MButton)e.getSource();
          
          if (!bt.hasFocus()) {
            bt.requestFocus();
          }
        }
        MHE = new MHandleEvent(minCommand, this);
      }
      


      MHandleEvent MHE;
      

      if ((e.getSource() instanceof MenuItem)) {
        minCommand = ((MenuItem)e.getSource()).getActionCommand();
        System.out.println("Vai entrar no MHandleEvent" + minCommand);
        MHE = new MHandleEvent(minCommand, this); }
      MHandleEvent MHE;
      if ((e.getSource() instanceof JMenuItem)) {
        minCommand = ((JMenuItem)e.getSource()).getActionCommand();
        System.out.println("Vai entrar no MHandleEvent" + minCommand);
        MHE = new MHandleEvent(minCommand, this);
      }
      if ((e.getSource() instanceof Menu)) {
        minCommand = ((Menu)e.getSource()).getActionCommand();
        System.out.println("Vai entrar no MHandleEvent" + minCommand);
        MHE = new MHandleEvent(minCommand, this);
      }
    }
    catch (MError err)
    {
      MHandleEvent MHE;
      












      err.show();
    } catch (RuntimeException ex) {
      new MError(ex).show();
    }
    System.out.println("Item Command " + wActionEvent);
  }
  







  public void close()
  {
    minForm = null;
    minFrame = null;
    minDialog = null;
    minCommand = null;
    wActionEvent = null;
  }
}
