package PT.Minimal.geral;

import java.awt.Font;
import java.awt.Graphics;

public class printFont extends printerObject
{
  Font fnt;
  
  public printFont(Font f)
  {
    fnt = f;
  }
  

  public void draw(Graphics g, java.awt.Point p)
  {
    g.setFont(fnt);
    if (y <= 0)
    {
      y = g.getFontMetrics(fnt).getHeight();
    }
  }
}
