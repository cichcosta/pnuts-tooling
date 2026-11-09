package PT.Minimal.rmi;

import java.io.PrintStream;

public class HelloServer
{
  public HelloServer() {}
  
  public static void main(String[] argv) {
    try {
      java.rmi.Naming.rebind("Hello", new Hello("Hello, world!"));
      System.out.println("Hello Server is ready.");
    } catch (Exception e) {
      System.out.println("Hello Server failed: " + e);
    }
  }
}
