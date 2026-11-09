package PT.Minimal.geral;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.Serializable;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;















public class MJMenuPlus
  extends JMenu
  implements Serializable
{
  public MJMenuPlus(String label)
  {
    super(label);
  }
  









  public void setPopupMenuVisible(boolean b)
  {
    boolean isVisible = isPopupMenuVisible();
    
    if (b != isVisible)
    {


      if ((b == true) && (isShowing())) {
        Point p = getPopupMenuOrigin();
        
        getPopupMenu().show(this, x, y);
      }
      else {
        getPopupMenu().setVisible(false);
      }
    }
  }
  







  protected Point getPopupMenuOrigin()
  {
    int x = 0;
    int y = 0;
    JPopupMenu pm = getPopupMenu();
    


    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension s = getSize();
    Dimension pmSize = pm.getSize();
    


    if (width == 0) {
      pmSize = pm.getPreferredSize();
    }
    Point position = getLocationOnScreen();
    
    Container parent = getParent();
    
    if ((parent instanceof JPopupMenu))
    {



      if (x + width + width < width) {
        x = width;
      } else {
        x = 0 - width;
      }
      

      if (y + height < height) {
        y = 0;
      } else {
        y = height - height;
      }
      

    }
    else
    {
      if (x + width < width) {
        x = 0;
      } else {
        x = width - width;
      }
      

      if (y + height + height < height) {
        y = height;
      } else {
        y = 0 - height;
      }
    }
    return new Point(x, y);
  }
}
