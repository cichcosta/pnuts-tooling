package PT.Minimal.editor;

import PT.Minimal.geral.MFrame;
import PT.Minimal.util.AltGrPatch;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import javax.swing.text.JTextComponent;













public class MEditorTest
{
  static String filename;
  
  public MEditorTest() {}
  
  public static void main(String[] args)
  {
    if (args.length >= 1) {
      filename = args[0];
    }
    else
    {
      filename = "v:\\devjava\\src\\PT\\Minimal\\geral\\MBrowse.java";
    }
    try
    {
      new AltGrPatch();
      Dimension wdim = new Dimension(525, 350);
      
      MEditor meditor = new MEditor("EDITOR", "EDIT", "Editor Teste", "text/java");
      
      MFrame wframe = new MFrame();
      wframe.setTitle("Editor Teste");
      
      wframe.setForm(meditor);
      wframe.pack();
      wframe.center(width, height, false);
      

      File file = new File(filename);
      meditor.read(new FileReader(file), file);
      
      meditor.setLocation(100, 50);
      meditor.setSize(600, 600);
      




      meditor.showForm(true);
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  static class focuser implements Runnable { Component c;
    
    public focuser(Component c) { this.c = c; }
    






    public void run()
    {
      System.out.println("requestFocus");
      c.requestFocus();
      if ((c instanceof JTextComponent))
        ((JTextComponent)c).setCaretPosition(1);
    }
  }
  
  protected static final class winAdapter extends WindowAdapter {
    protected winAdapter() {}
    
    public void windowClosing(WindowEvent e) { System.exit(0); }
  }
}
