package PT.Minimal.geral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Properties;













public class MHelp
{
  public MHelp() {}
  
  public static void showHelp(String wid)
    throws MError
  {
    String whpath = "";
    if ((wid.startsWith("HELP")) && (wid.length() > 6)) {
      wid = wid.substring(5);
      System.out.println(wid);
    }
    if (MLogin.hasLoginProperty("JHELPDIR")) {
      whpath = MLogin.getLoginProperty("JHELPDIR");
    } else {
      whpath = "mjhelp/man/";
    }
    
    whpath = whpath + wid + ".html";
    System.out.println(whpath);
    String wcmd = MUtil.myProp.getProperty("JSTART");
    try {
      Runtime.getRuntime().exec(wcmd + " " + whpath);
    } catch (FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro '" + whpath + "' não encontrado");
    } catch (IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro '" + whpath + "' não encontrado");
    }
  }
  














  public static void createHelp()
    throws MError
  {}
  













  public static void main(String[] args)
  {
    System.out.println("Inicio - " + new Date());
    MHelp wmain = new MHelp();
    showHelp("XX");
    System.out.println("Fim - " + new Date());
  }
}
