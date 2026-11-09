package PT.Minimal.geral;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
















class MMouseListener
  extends MouseAdapter
{
  MForm minForm;
  MFrame minFrame;
  MDialog minDialog;
  MouseEvent wMouseEvent;
  String minCommand;
  
  public MMouseListener(MFrame wframe)
  {
    minFrame = wframe;
  }
  








  public MMouseListener(MForm wform)
  {
    minForm = wform;
  }
  








  public MMouseListener(MDialog wdialog)
  {
    minDialog = wdialog;
  }
  


























  public void mousePressed(MouseEvent e)
  {
    try
    {
      wMouseEvent = e;
      if ((e.getComponent() instanceof MTextField)) {
        MTextField wtf = (MTextField)e.getComponent();
        if (!wtf.isEditable()) {
          wtf.setMode(true);
          wtf.requestFocus();
          wtf.selectAll();
          wtf.copy();
        }
      }
      











      if ((e.getComponent() instanceof MTextArea)) {
        MTextArea wta = (MTextArea)e.getComponent();
        if (!wta.isEditable()) {
          wta.setMode(true);
          wta.requestFocus();
          wta.selectAll();
          wta.copy();
        }
      }
    }
    catch (MError err)
    {
      err.show();
    } catch (RuntimeException ex) {
      new MError(ex).show();
    }
  }
  

  public void mouseReleased(MouseEvent e)
  {
    try
    {
      wMouseEvent = e;
      MHandleEvent MHE; if (e.isPopupTrigger())
      {



        e.getComponent().requestFocus();
        MHE = new MHandleEvent("PROMPT_ROW", this);
      }
      
      if ((e.getComponent() instanceof MTextField)) {
        MTextField wtf = (MTextField)e.getComponent();
        if (!wtf.isEditable()) {
          wtf.setMode(false);
        }
      }
      








      if ((e.getComponent() instanceof MTextArea)) {
        MTextArea wta = (MTextArea)e.getComponent();
        if (!wta.isEditable()) {
          wta.setMode(false);

        }
        

      }
      


    }
    catch (MError err)
    {

      err.show();
    } catch (RuntimeException ex) {
      new MError(ex).show();
    }
  }
  







  public void mouseClicked(MouseEvent e)
  {
    try
    {
      wMouseEvent = e;
      if (e.getClickCount() >= 2) { MHandleEvent MHE;
        if ((e.getComponent() instanceof MBrowse)) {
          MBrowse wbr = (MBrowse)e.getComponent();
          
          if (!(wbr instanceof MBrowseEdit)) { MHandleEvent MHE;
            if (wbr.getType().equals("BROWSE")) {
              MHE = new MHandleEvent("UPDATE_ROW", this);
            }
            if (wbr.getType().equals("PROMPT")) {
              MHE = new MHandleEvent("OKDIALOG", this);
            }
          }
        }
        if ((e.getComponent() instanceof MTextField)) {
          MTextField wtf = (MTextField)e.getComponent();
          if (wtf.getSelectionEnd() == 0) {
            wtf.paste();
          } else {
            wtf.selectAll();wtf.copy();
          }
        }
        






        if ((e.getComponent() instanceof MTextArea)) {
          MTextArea wta = (MTextArea)e.getComponent();
          if (wta.getSelectionEnd() == 0) {
            wta.paste();
          } else {
            wta.selectAll();wta.copy();
          }
        }
      }
    }
    catch (MError err) {
      err.show();
    } catch (RuntimeException ex) {
      new MError(ex).show();
    }
  }
  






  public void close()
  {
    minForm = null;
    minFrame = null;
    minDialog = null;
    minCommand = null;
    wMouseEvent = null;
  }
}
