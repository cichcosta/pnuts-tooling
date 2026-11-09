package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.JEditorPane;


public class MText
  implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  static Dimension wdim = new Dimension(525, 350);
  
  public MText() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError {
    if ((minCommand.getName().equals("TEXT_ROW")) && (minForm.getFase() == 0)) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      show_text_row();
    }
    if ((minCommand.getName().equals("TEXT_ROW")) && (minForm.getFase() == 1)) {
      do_text_row();
    }
  }
  



























  public void show_text_row()
    throws MError
  {}
  



























  public void do_text_row()
    throws MError
  {
    minForm.getRow().setAssociatedText(((JEditorPane)minForm.getActionObject()).getText());
    minForm.hideForm();
  }
}
