package PT.Minimal.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;
import sun.tools.java.Environment;
import sun.tools.java.Scanner;







public class MEditorDocument
  extends PlainDocument
{
  MEditor editor;
  
  public MEditorDocument(MEditor editor)
  {
    super(new GapContent(1024));
    this.editor = editor;
    
    putProperty("tabSize", new Integer(3));
  }
  
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException
  {
    if (editor.getCaret().getMode()) {
      super.insertString(offset, str, a);
    } else {
      char[] text = getText(0, getLength()).toCharArray();
      int i = offset;
      while ((i < text.length) && (i - offset < str.length())) {
        remove(i, 1);
        i++;
      }
      super.insertString(offset, str, a);
    }
  }
  


  public Scanner createScanner()
  {
    Scanner s;
    

    try
    {
      s = new Scanner(new LocalEnvironment(), new DocumentInputStream(0, getLength()));
    } catch (IOException e) {
      s = null;
    }
    return s;
  }
  





  public int getScannerStart(int p)
  {
    Element elem = getDefaultRootElement();
    int lineNum = elem.getElementIndex(p);
    Element line = elem.getElement(lineNum);
    AttributeSet a = line.getAttributes();
    while ((a.isDefined(CommentAttribute)) && (lineNum > 0)) {
      lineNum--;
      line = elem.getElement(lineNum);
      a = line.getAttributes();
    }
    return line.getStartOffset();
  }
  










  protected void insertUpdate(AbstractDocument.DefaultDocumentEvent chng, AttributeSet attr)
  {
    super.insertUpdate(chng, attr);
    

    Element root = getDefaultRootElement();
    DocumentEvent.ElementChange ec = chng.getChange(root);
    if (ec != null) {
      Element[] added = ec.getChildrenAdded();
      boolean inComment = false;
      for (int i = 0; i < added.length; i++) {
        Element elem = added[i];
        int p0 = elem.getStartOffset();
        int p1 = elem.getEndOffset();
        String s;
        try {
          s = getText(p0, p1 - p0);
        } catch (BadLocationException bl) {
          s = null;
        }
        if (inComment) {
          MutableAttributeSet a = (MutableAttributeSet)elem.getAttributes();
          a.addAttribute(CommentAttribute, CommentAttribute);
          int index = s.indexOf("*/");
          if (index >= 0)
          {
            inComment = false;
          }
        }
        else {
          int index = s.indexOf("/*");
          if (index >= 0)
          {
            index = s.indexOf("*/", index);
            if (index < 0)
            {
              inComment = true;
            }
          }
        }
      }
    }
  }
  









  protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng)
  {
    super.removeUpdate(chng);
  }
  







  static final Object CommentAttribute = new AttributeKey(null);
  
  static class AttributeKey { AttributeKey(MEditorDocument.1 x0) { this(); }
    


    public String toString()
    {
      return "comment";
    }
    
    private AttributeKey() {}
  }
  
  class DocumentInputStream extends InputStream
  {
    Segment segment;
    int p0;
    int p1;
    int pos;
    int index;
    
    public DocumentInputStream(int p0, int p1) {
      segment = new Segment();
      this.p0 = p0;
      this.p1 = Math.min(getLength(), p1);
      pos = p0;
      try {
        loadSegment();
      } catch (IOException ioe) {
        throw new Error("unexpected: " + ioe);
      }
    }
    














    public int read()
      throws IOException
    {
      if (index >= segment.offset + segment.count) {
        if (pos >= p1)
        {
          return -1;
        }
        loadSegment();
      }
      return segment.array[(index++)];
    }
    
    void loadSegment() throws IOException {
      try {
        int n = Math.min(1024, p1 - pos);
        getText(pos, n, segment);
        pos += n;
        index = segment.offset;
      } catch (BadLocationException e) {
        throw new IOException("Bad location");
      }
    }
  }
  

  public class Scanner
    extends Scanner
  {
    int p0;
    

    Scanner(MEditorDocument.LocalEnvironment localEnv, MEditorDocument.DocumentInputStream docStream)
      throws IOException
    {
      super(docStream);
      scanComments = true;
    }
    



    public void setRange(int p0, int p1)
      throws IOException
    {
      useInputStream(new MEditorDocument.DocumentInputStream(MEditorDocument.this, p0, p1));
      this.p0 = p0;
    }
    



    public final int getStartOffset()
    {
      int begOffs = (int)(pos & 0xFFFFFFFF);
      return p0 + begOffs;
    }
    



    public final int getEndOffset()
    {
      int endOffs = (int)(getEndPos() & 0xFFFFFFFF);
      return p0 + endOffs;
    }
  }
  

  static class LocalEnvironment
    extends Environment
  {
    LocalEnvironment() {}
    

    public void error(Object source, int where, String err, Object arg1, Object arg2, Object arg3)
    {
      System.err.println(err + ", location: " + where + ", args (" + arg1 + ", " + arg2 + ", " + arg3 + ")");
    }
  }
}
