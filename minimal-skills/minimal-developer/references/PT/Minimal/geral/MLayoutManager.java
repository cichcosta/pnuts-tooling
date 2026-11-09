package PT.Minimal.geral;

import java.awt.Rectangle;

public class MLayoutManager implements java.awt.LayoutManager, java.io.Serializable
{
  private int minWidth = 0; private int minHeight = 0;
  private boolean sizeUnknown = true;
  

  public MLayoutManager() {}
  

  public void addLayoutComponent(String name, java.awt.Component comp) {}
  
  public void removeLayoutComponent(java.awt.Component comp) {}
  
  public java.awt.Dimension preferredLayoutSize(java.awt.Container parent)
  {
    return minimumLayoutSize(parent);
  }
  
  public java.awt.Dimension minimumLayoutSize(java.awt.Container parent) {
    java.awt.Dimension dim = new java.awt.Dimension(0, 0);
    


    java.awt.Insets insets = parent.getInsets();
    width = (minWidth + left + right + 10);
    height = (minHeight + top + bottom);
    return dim;
  }
  
  public void layoutContainer(java.awt.Container parent) {
    int nComps = parent.getComponentCount();
    java.awt.Insets insets = parent.getInsets();
    int x = 10;int y = top + 10;
    
    boolean isM = false;
    String wminAnchor = null;
    int wminRelPosX = 0;
    int wminRelPosY = 0;
    int wminPosX = 0;
    int wminPosY = 0;
    boolean wminBreakLn = false;
    int wminWidth = 100;
    int wdefgap = 10;
    
    Rectangle previousBounds = new Rectangle(0, 0, x, y);
    for (int i = 0; i < nComps; i++) {
      wminBreakLn = false;
      java.awt.Component c = parent.getComponent(i);
      java.awt.Dimension d = c.getPreferredSize();
      


      x = x + width + wdefgap;
      y = y;
      if ((c instanceof javax.swing.JTabbedPane)) {
        wminBreakLn = true;
      }
      

      if ((c instanceof MField)) {
        MField minComponent = (MField)c;
        wminAnchor = minComponent.getMinAnchor();
        wminRelPosX = minComponent.getMinRelPosX();
        wminRelPosY = minComponent.getMinRelPosY();
        wminPosX = minComponent.getMinPosX();
        wminPosY = minComponent.getMinPosY();
        wminBreakLn = minComponent.getMinBreakLn();
      }
      











      if ((wminRelPosX != 0) || (wminRelPosY != 0))
      {







        x = x + width + wminRelPosX;
        y = y + wminRelPosY;
      }
      
      if (wminBreakLn == true) {
        x = wminRelPosX + wdefgap;
        y = y + height + 0 + wminRelPosY;
      }
      






      if ((c instanceof MImage)) {
        System.out.println("MLayoutManager MImage = X:" + x + " Y:" + y + d);
      }
      
      c.setBounds(new Rectangle(x, y, width, height));
      

      previousBounds = c.getBounds();
      if (minWidth < x + width) {
        minWidth = (x + width);
      }
      minHeight = (y + height);
      sizeUnknown = false;
    }
  }
  
  public String toString()
  {
    String str = "";
    return getClass().getName();
  }
}
