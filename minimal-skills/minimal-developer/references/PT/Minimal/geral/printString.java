package PT.Minimal.geral;

import java.awt.Graphics;

public class printString extends printerObject
{
  String st;
  
  public printString(String s) {
    st = s;
  }
  
  public void draw(Graphics g, java.awt.Point p)
  {
    g.drawString(st, x, y);
    x += g.getFontMetrics(g.getFont()).stringWidth(st);
  }
}
