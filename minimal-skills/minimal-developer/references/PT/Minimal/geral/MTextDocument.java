package PT.Minimal.geral;

import java.awt.Toolkit;
import java.io.PrintStream;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

















public class MTextDocument
  extends PlainDocument
{
  public static final int UNDEFINED = 0;
  public static final int LOWER = 1;
  public static final int UPPER = 2;
  public static final int DATE = 4;
  public static final int NUMBER = 8;
  protected int type = 0;
  protected String mask = null;
  protected int limit = 0;
  
  protected MTextComponent txt;
  
  public MTextDocument(MTextComponent txtField)
  {
    this(txtField, 0);
  }
  












  public MTextDocument(MTextComponent txtField, int limit)
  {
    txt = txtField;
    setLimit(limit);
  }
  
  public void remove(int i, int f) throws BadLocationException
  {
    txt.setChanged(true);
    super.remove(i, f);
  }
  









  public void insertString(int offset, String str, AttributeSet attr)
    throws BadLocationException
  {
    if (str == null) { return;
    }
    switch (type) {
    case 2:  str = str.toUpperCase(); break;
    case 1:  str = str.toLowerCase(); break;
    case 4:  if (!checkDate(offset, str)) {
        Toolkit.getDefaultToolkit().beep(); return;
      }
      break;
    case 8: 
      if (!checkNumber(offset, str)) {
        Toolkit.getDefaultToolkit().beep(); return;
      }
      
      break;
    }
    
    boolean isOneChar = str.length() <= 1;
    boolean nextField = false;
    int len = limit - getLength();
    if (getType() == 8) { len += 2;
    }
    


    if ((limit > 0) && 
      (str.length() >= len)) {
      nextField = true;
      if (len > 0) {
        str = str.substring(0, len);
      }
    }
    
















    if ((limit <= 0) || (len > 0)) {
      super.insertString(offset, str, attr);
      txt.setChanged(true);
    }
    
    if ((nextField) && (isOneChar) && 
      (((JComponent)txt).hasFocus())) {
      System.out.println(txt.getName() + ".transferFocus()");
      txt.transferFocus();
    }
    


    if ((txt instanceof MTextArea))
    {


      MTextArea areaTexto = (MTextArea)txt;
      
      limit = (areaTexto.getColumns() * areaTexto.getRows());
    }
  }
  


  private boolean checkDate(int offset, String str)
  {
    int n = str.length();
    int i = 0; for (int j = offset; i < n; j++) {
      char c = str.charAt(i);
      char m = mask.charAt(j);
      if (c != '%') {
        if (Character.isLetterOrDigit(m)) {
          if (!Character.isDigit(c)) return false;
        } else if (c != m) {
          return false;
        }
      }
      i++;
    }
    








    return true;
  }
  
  private boolean checkNumber(int offset, String str) throws BadLocationException {
    int n = getLength();
    String t = n == 0 ? "" : getText(0, n);
    boolean empty = t.trim().length() == 0;
    boolean point = !empty;
    
    n = str.length();
    for (int i = 0; i < n; i++) {
      char c = str.charAt(i);
      if (c == '.') {
        if (point) return false;
        point = true;
      } else if ((c == '-') || (c == '+')) {
        if (!empty) return false;
      }
      else if ((c != ' ') && (!Character.isDigit(c))) { return false;
      }
    }
    return true;
  }
  




  public int getType()
  {
    return type;
  }
  




  public void setType(int type)
  {
    this.type = type;
  }
  





  public String getMask()
  {
    return mask;
  }
  


















  public void setMask(String m)
  {
    mask = m;
    setType(0);
    if (mask != null) {
      mask = mask.trim();
      if (mask.length() != 0) {
        switch (mask.charAt(0)) {
        case 'L':  setType(1);
          break;
        case 'U':  setType(2);
          break;
        case 'N':  mask = mask.substring(1);
          setType(8);
          break;
        case 'D':  mask = mask.substring(1);
          setType(4);
          break;
        default:  setLimit(mask.length());
        }
        
      }
    }
  }
  





  public int getLimit()
  {
    return limit;
  }
  







  public void setLimit(int limit)
  {
    this.limit = limit;
    if (limit > 0) {
      int len = getLength();
      if (len > limit) {
        try {
          super.remove(limit, len - limit);
        } catch (BadLocationException ex) {
          ex.printStackTrace();
          Toolkit.getDefaultToolkit().beep();
        }
      }
    }
  }
}
