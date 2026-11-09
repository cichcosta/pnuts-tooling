package PT.Minimal.geral;

import java.awt.Graphics;

public class newLine extends printerObject
{
  public newLine() {}
  
  public void draw(Graphics g, java.awt.Point p) {
    x = 0;
    
    y += g.getFontMetrics(g.getFont()).getHeight();
  }
}
