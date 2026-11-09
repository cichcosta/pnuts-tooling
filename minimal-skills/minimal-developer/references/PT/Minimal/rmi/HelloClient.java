package PT.Minimal.rmi;

import java.io.PrintStream;

public class HelloClient
{
  public HelloClient() {}
  
  public static void main(String[] argv) {
    try {
      HelloInterface hello = (HelloInterface)java.rmi.Naming.lookup("//194.65.54.218/Hello");
      
      System.out.println(hello.say());
    } catch (Exception e) {
      System.out.println("HelloClient exception: " + e);
    }
  }
}
