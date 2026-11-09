package PT.Minimal.actions;

import PT.Minimal.geral.MBrowseColumn;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MLogin;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import com.objectshare.grid.data.JKTableModel;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Vector;

public class MExportCSV implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private String wobj;
  private String wobjname;
  private String[] wargs;
  private JKTableModel myModel;
  private static int fileNumber = 1;
  private String fileExtension = ".csv";
  
  private  mpnut;
  

  public MExportCSV() {}
  
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
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    mpnut = .getMPnut(minForm.getEnt());
    mpnut.setForm(minForm);
    
    exportCSV();
  }
  
  private void exportCSV() throws MError {
    myModel = ((JKTableModel)minForm.getBrowse().getModel());
    
    StringBuffer wcsvfile = new StringBuffer(1000000);
    
    Object[] myCols = myModel.getColumnArray();
    
















    String whead1 = "";
    String whead2 = "";
    String wnumber = "";
    for (int i = 1; i < myCols.length; i++) {
      MBrowseColumn wcol = (MBrowseColumn)myCols[i];
      



      if (i > 1) {
        if ((MLogin.hasLoginProperty("CSVSEPARATORS")) && (MLogin.getLoginProperty("CSVSEPARATORS").trim().length() == 5)) {
          whead1 = whead1 + MLogin.getLoginProperty("CSVSEPARATORS").substring(3, 4);
          whead2 = whead2 + MLogin.getLoginProperty("CSVSEPARATORS").substring(3, 4);
        } else {
          whead1 = whead1 + ";";
          whead2 = whead2 + ";";
        }
      }
      
      whead1 = whead1 + "\"" + wcol.getPrintTitle1().trim() + "\"";
      whead2 = whead2 + "\"" + wcol.getPrintTitle2().trim() + "\"";
    }
    
    wcsvfile.append(whead1 + "\n" + whead2 + "\n");
    
    for (int row = 1; row <= myModel.getNumberOfRows(); row++) {
      MRow wmrow = (MRow)myModel.getRow(row);
      
      for (int col = 1; col < myCols.length; col++) {
        if (col > 1) {
          if ((MLogin.hasLoginProperty("CSVSEPARATORS")) && (MLogin.getLoginProperty("CSVSEPARATORS").trim().length() == 5)) {
            wcsvfile.append(MLogin.getLoginProperty("CSVSEPARATORS").substring(3, 4));
          } else {
            wcsvfile.append(";");
          }
        }
        MBrowseColumn wcol = (MBrowseColumn)myCols[col];
        

        if (wmrow.getType(wcol.getName()) == "NUMTYPE") {
          wnumber = PT.Minimal.util.MNumberUtil.toString((String)wmrow.getField(wcol.getName()), wcol.getMask());
          if (!MLogin.hasLoginProperty("CSVSEPARATORS")) {
            wnumber = wnumber.replace('.', '!');
            wnumber = wnumber.replace(' ', '.');
            wnumber = wnumber.replace('!', ',');
            if ((wnumber.length() > 0) && (wnumber.charAt(0) == '.')) {
              wnumber = wnumber.substring(1);
            }
          }
          if ((MLogin.hasLoginProperty("CSVSEPARATORS")) && (MLogin.getLoginProperty("CSVSEPARATORS").equals("(,.)"))) {
            wnumber = wnumber.replace(' ', ',');
          }
          if ((MLogin.hasLoginProperty("CSVSEPARATORS")) && (MLogin.getLoginProperty("CSVSEPARATORS").trim().length() == 5)) {
            wnumber = wnumber.replace(' ', 'X');
            wnumber = wnumber.replace('.', 'Y');
            wnumber = wnumber.replace('X', MLogin.getLoginProperty("CSVSEPARATORS").charAt(1));
            wnumber = wnumber.replace('Y', MLogin.getLoginProperty("CSVSEPARATORS").charAt(2));
          }
          wcsvfile.append(wnumber);
        } else {
          wcsvfile.append("\"" + wmrow.getFormatedValue(wcol.getName()) + "\"");
        }
      }
      wcsvfile.append("\n");
    }
    try
    {
      String fileOutName = MUtil.myProp.getProperty("user.name") + fileNumber + ".csv";
      fileNumber += 1;
      String wfilename = MUtil.myProp.getProperty("JTEMP") + File.separator + fileOutName;
      File woutfile = new File(wfilename);
      System.out.println("Output File: " + woutfile.getAbsolutePath());
      
      DataOutputStream fos = new DataOutputStream(new java.io.FileOutputStream(woutfile));
      
      fos.write(wcsvfile.toString().getBytes());
      
      fos.close();
      String wcmd = "";
      if (MLogin.getLoginProperties().getProperty("JSTART" + fileExtension.substring(1, 4).toUpperCase()) != null) {
        wcmd = MUtil.myProp.getProperty("JSTART" + fileExtension.substring(1, 4).toUpperCase());
      } else {
        wcmd = MUtil.myProp.getProperty("JSTART") + " excel " + woutfile.getAbsolutePath();
      }
      mpnut.put("JSTART", MUtil.myProp.getProperty("JSTART"));
      mpnut.put("FILENAME", woutfile.getAbsolutePath());
      System.out.println(wcmd);
      wcmd = mpnut.evalFormatedValues(wcmd);
      System.out.println(wcmd);
      Runtime.getRuntime().exec(wcmd);
    }
    catch (java.io.FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    } catch (IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError("Ficheiro não encontrado");
    }
  }
}
