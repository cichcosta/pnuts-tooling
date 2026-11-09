package PT.Minimal.geral;

import java.awt.Font;
import java.awt.Graphics;

public class printTab extends printerObject
{
  static int tab_width = 0;
  int tab_dist;
  Font tabFnt;
  
  public printTab(Font tbFont, int tabdist) {
    tabFnt = tbFont;
    
    tab_dist = tabdist;
  }
  
  public void draw(Graphics g, java.awt.Point p) {
    if (tab_width == 0) {
      tab_width = g.getFontMetrics(tabFnt).stringWidth("W");
    }
    
    if (x < tab_dist * tab_width) {
      x = (tab_dist * tab_width);
    }
  }
}
