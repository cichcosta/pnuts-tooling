package PT.Minimal.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;







public class MEditorContext
  extends StyleContext
  implements ViewFactory
{
  Style[] tokenStyles;
  transient Color[] tokenColors;
  transient Font[] tokenFonts;
  
  public MEditorContext()
  {
    Style root = getStyle("default");
    tokenStyles = new Style[''];
    MEditorToken[] tokens = MEditorToken.all;
    int n = tokens.length;
    for (int i = 0; i < n; i++) {
      MEditorToken t = tokens[i];
      Style parent = getStyle(t.getCategory());
      if (parent == null) {
        parent = addStyle(t.getCategory(), root);
      }
      Style s = addStyle(null, parent);
      s.addAttribute(MEditorToken.TokenAttribute, t);
      tokenStyles[t.getScanValue()] = s;
    }
  }
  






  public Color getForeground(int code)
  {
    if (tokenColors == null) {
      tokenColors = new Color[''];
    }
    if ((code >= 0) && (code < tokenColors.length)) {
      Color c = tokenColors[code];
      if (c == null) {
        Style s = tokenStyles[code];
        c = StyleConstants.getForeground(s);
      }
      return c;
    }
    return Color.black;
  }
  



  public Font getFont(int code)
  {
    if (tokenFonts == null) {
      tokenFonts = new Font[''];
    }
    if (code < tokenFonts.length) {
      Font f = tokenFonts[code];
      if (f == null) {
        Style s = tokenStyles[code];
        f = getFont(s);
      }
      return f;
    }
    return null;
  }
  





  public Style getStyleForScanValue(int code)
  {
    if (code < tokenStyles.length) {
      return tokenStyles[code];
    }
    return null;
  }
  

  public View create(Element elem)
  {
    return new JavaView(elem);
  }
  








  class JavaView
    extends WrappedPlainView
  {
    MEditorDocument.Scanner lexer;
    







    boolean lexerValid;
    







    JavaView(Element elem)
    {
      super();
      MEditorDocument doc = (MEditorDocument)getDocument();
      lexer = doc.createScanner();
      lexerValid = false;
    }
    











    public void paint(Graphics g, Shape a)
    {
      super.paint(g, a);
      lexerValid = false;
    }
    















    protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1)
      throws BadLocationException
    {
      Document doc = getDocument();
      Color last = null;
      int mark = p0;
      
      while (p0 < p1) {
        updateScanner(p0);
        int p = Math.min(lexer.getEndOffset(), p1);
        p = p <= p0 ? p1 : p;
        Color fg = getForeground(lexer.token);
        if ((fg != last) && (last != null))
        {
          g.setColor(last);
          Segment text = getLineBuffer();
          doc.getText(mark, p0 - mark, text);
          x = Utilities.drawTabbedText(text, x, y, g, this, mark);
          mark = p0;
        }
        last = fg;
        p0 = p;
      }
      
      g.setColor(last);
      Segment text = getLineBuffer();
      doc.getText(mark, p1 - mark, text);
      x = Utilities.drawTabbedText(text, x, y, g, this, mark);
      return x;
    }
    


    void updateScanner(int p)
    {
      try
      {
        if (!lexerValid) {
          MEditorDocument doc = (MEditorDocument)getDocument();
          lexer.setRange(doc.getScannerStart(p), doc.getLength());
          lexerValid = true;
        }
        while (lexer.getEndOffset() <= p) {
          lexer.scan();
        }
      }
      catch (Throwable e)
      {
        e.printStackTrace();
      }
    }
  }
}
