package PT.Minimal.geral;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class MAction extends AbstractAction
{
  public MAction(String ws)
  {
    super(ws);
  }
  
  public MAction(String ws, javax.swing.Icon wic) {
    super(ws, wic);
  }
  
  public void actionPerformed(ActionEvent wev) {}
}
