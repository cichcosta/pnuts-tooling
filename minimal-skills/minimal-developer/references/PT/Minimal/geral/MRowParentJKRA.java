package PT.Minimal.geral;

import com.objectshare.grid.data.JKRowAdapter;
import java.util.Observable;













public class MRowParentJKRA
  extends JKRowAdapter
{
  public MRowParentJKRA() {}
  
  public Object[] getChildren()
  {
    return ((MRowParent)getRowObject()).getChildren();
  }
  



  public void update(Observable o, Object arg)
  {
    if ((arg != null) && (arg.equals("children"))) {
      childrenChanged();
    } else {
      super.update(o, arg);
    }
  }
}
