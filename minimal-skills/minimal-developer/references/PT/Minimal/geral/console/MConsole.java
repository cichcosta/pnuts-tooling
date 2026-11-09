package PT.Minimal.geral.console;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;


public class MConsole
  extends JFrame
  implements MStreamObserver
{
  public static MConsole console;
  private static boolean log = false;
  



  private static boolean trace = false;
  



  private static boolean traceSql = false;
  
  private static JTextArea jtext;
  
  private MObservableStream errorDevice;
  
  private MObservableStream outputDevice;
  
  private PrintStream errorStream;
  
  private PrintStream outputStream;
  
  private PrintStream _errorStream;
  private PrintStream _outputStream;
  
  public static void start()
  {
    boolean cons = false;
    boolean showcons = false;
    
    trace = Boolean.valueOf(System.getProperties().getProperty("PT.Minimal.trace", "true")).booleanValue();
    System.out.println("!*** MConsole trace:" + trace);
    
    cons = Boolean.valueOf(System.getProperties().getProperty("PT.Minimal.console", "true")).booleanValue();
    System.out.println("!*** MConsole console:" + cons);
    showcons = Boolean.valueOf(System.getProperties().getProperty("PT.Minimal.showConsole", "false")).booleanValue();
    System.out.println("!*** MConsole showConsole:" + showcons);
    if (cons) {
      console = new MConsole();
      if (showcons) {
        showConsole();
      }
    }
  }
  
  public MConsole() {
    super("Java Console");
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        MConsole.jtext.setText("");
        MConsole.access$102(false);
        setVisible(false);
      }
      

    });
    jtext = new JTextArea();
    jtext.setEditable(true);
    jtext.setLineWrap(true);
    jtext.setWrapStyleWord(true);
    
    JScrollPane jscroll = new JScrollPane(jtext);
    JViewport jvport = jscroll.getViewport();
    jvport.add(jtext);
    
    jvport.setScrollMode(2);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add("Center", jscroll);
    

    _errorStream = System.err;
    _outputStream = System.out;
    




    pack();
    setError();
    setOutput();
    setEnabled(true);
    setLocation(100, 50);
  }
  
  public static void showConsole(boolean mostra) {
    if (console != null) {
      if (mostra) {
        console.setVisible(true);
      }
      
      log = true;
    }
  }
  
  public static void showConsole() {
    showConsole(true);
  }
  
  public static void hideConsole() {
    if (console != null) {
      console.setVisible(false);
      
      log = true;
    }
  }
  



  public void clear()
  {
    outputDevice.reset();
    




    errorDevice.reset();
    
    jtext.setText("");
  }
  
  public final void setError()
  {
    errorDevice = new MObservableStream();
    errorDevice.addStreamObserver(this);
    
    errorStream = new PrintStream(errorDevice, true);
    System.setErr(errorStream);
  }
  
  public final void resetError() {
    System.setErr(_errorStream);
    errorDevice = null;
    errorStream = null;
  }
  
  public final void setOutput() {
    outputDevice = new MObservableStream();
    outputDevice.addStreamObserver(this);
    
    outputStream = new PrintStream(outputDevice, true);
    System.setOut(outputStream);
  }
  
  public final void resetOutput() {
    System.setOut(_outputStream);
    outputDevice = null;
    outputStream = null;
  }
  
  public Dimension getMinimumSize() {
    return new Dimension(500, 400);
  }
  
  public Dimension getPreferredSize() {
    return getMinimumSize();
  }
  
  public static String getConsoleText() {
    return jtext.getText();
  }
  
  public void streamChanged() {
    if (log) jtext.append(outputDevice.toString());
    try {
      if (trace) outputDevice.writeTo(_outputStream);
    }
    catch (IOException e) {}
    outputDevice.reset();
    
    errorStream.checkError();
    if (log) jtext.append(errorDevice.toString());
    try {
      if (trace) errorDevice.writeTo(_errorStream);
    }
    catch (IOException e) {}
    errorDevice.reset();
  }
  
  public static void setTraceSql(boolean state) {
    traceSql = state;
  }
  
  public static boolean getTraceSql() { return traceSql; }
  
  public static void enableTraceSql() {
    traceSql = true;
  }
  
  public static void disableTraceSql() { traceSql = false; }
}
