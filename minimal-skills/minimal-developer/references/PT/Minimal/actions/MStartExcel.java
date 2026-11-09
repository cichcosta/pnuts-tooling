package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MFile;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MLogin;
import PT.Minimal.pnuts.;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

public class MStartExcel implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private static int fileNumber = 1;
  
  private String[] wargs;
  private boolean isValid = true;
  
  private String wobj;
  private String wobjname;
  
  public MStartExcel() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError {
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getCallerArgs(), ' ');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getArgs(), ' ');
    }
    System.out.println(wargs.length);
    System.out.println(minCommand.getCallerArgs());
    if ((wargs == null) || (wargs.length < 2)) {
      throw new MError("Não existe opcão indicada ...");
    }
    String wfilename = checkFile(wargs[0]);
    wfilename = mpnut.evalFormatedValues(wfilename);
    String wexcelargs = mpnut.evalFormatedValues(wargs[1]);
    try
    {
      MFile wfiletemplate = new MFile(wfilename);
      if (!wfiletemplate.isFile()) {
        throw new MError("MIN-FILE01E", new String[] { wfiletemplate.getName() });
      }
      wfiletemplate.readFile();
      
      String fileOutName = MLogin.getLoginProperty("JTEMP") + File.separator + MLogin.getLoginProperty("user.name") + fileNumber + ".xls";
      


      fileNumber += 1;
      wfiletemplate.saveFile(fileOutName);
      
      MFile wminodbcj = new MFile("Minodbcj.xls");
      if (!wfiletemplate.isFile()) {
        throw new MError("MIN-FILE01E", new String[] { wminodbcj.getName() });
      }
      
      String wminodbcj_out = MLogin.getLoginProperty("JTEMP") + File.separator + "Minodbcj.xls";
      


      MFile wminodbcj_temp = new MFile(wminodbcj_out);
      if ((!wminodbcj_temp.isFile()) || (wminodbcj_temp.lastModified() < wminodbcj.lastModified()))
      {
        wminodbcj.readFile();
        wminodbcj.saveFile(wminodbcj_out);
      }
      
      String wcmd = MLogin.getLoginProperty("JSTART") + " excel " + " \"" + wminodbcj_out + "\" " + " \"" + fileOutName + "\" " + wexcelargs;
      




      System.out.println(wcmd);
      Runtime.getRuntime().exec(wcmd);
    } catch (FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("FileStreamsTest: " + e);
      e.printStackTrace();
    }
  }
  





  private String checkFile(String wfilename)
  {
    mpnut.put("TEMPLATES", MLogin.getCliTemplateDir());
    String ws = mpnut.evalFormatedValues(wfilename);
    File wfile = new File(ws);
    System.out.println(wfile.getAbsolutePath() + " - " + wfile.exists());
    if (wfile.exists()) {
      return wfile.getAbsolutePath();
    }
    
    mpnut.put("TEMPLATES", MLogin.getLogoTemplateDir());
    ws = mpnut.evalFormatedValues(wfilename);
    wfile = new File(ws);
    System.out.println(wfile.getAbsolutePath() + " - " + wfile.exists());
    if (wfile.exists()) {
      return wfile.getAbsolutePath();
    }
    
    mpnut.put("TEMPLATES", MLogin.getMinTemplateDir());
    ws = mpnut.evalFormatedValues(wfilename);
    wfile = new File(ws);
    System.out.println(wfile.getAbsolutePath() + " - " + wfile.exists());
    if (wfile.exists()) {
      return wfile.getAbsolutePath();
    }
    throw new MError("MIN-910006E", new String[] { wfilename });
  }
}
