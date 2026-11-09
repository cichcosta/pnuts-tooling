package PT.Minimal.actions;

import PT.Minimal.geral.MBrowse;
import PT.Minimal.geral.MBrowseColumn;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MHashtable;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import PT.Minimal.util.MDate;
import com.objectshare.grid.data.JKTableModel;
import java.io.PrintStream;
import java.util.Vector;




















public abstract class MSendExcel
  implements MExternalCommand
{
  private MCommand minCommand;
  private MForm minForm;
  private Vector wrows;
  private String wName;
  private String wOkCode;
  private String wFailCode;
  private String wNextCmd;
  private String wArgLine;
  private String[] wargs;
  private String wzfase;
   mpnut;
  MHashtable csv;
  JKTableModel myModel;
  
  public MSendExcel() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
    
    wName = wcmd.getName().trim();
    wOkCode = wcmd.getOkCode().trim();
    wFailCode = wcmd.getFailCode().trim();
    wNextCmd = wcmd.getNextCommand().trim();
    wArgLine = wcmd.getArgs().trim();
    wargs = MUtil.getArgs(wArgLine, ',');
    wzfase = wcmd.getZFase();
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wvector) {
    wrows = wvector;
  }
  
  public void execute() throws MError {
    myModel = ((JKTableModel)minForm.getBrowse().getModel());
    
    if (wargs.length < 1) {
      throw new MError("Não existe ficheiro template para o excel");
    }
    if ((wargs.length > 1) && 
      (wargs[1].toUpperCase().equals("ALL"))) {
      wrows = myModel.getRowVector();
    }
    
    if (wrows.size() == 0) {
      throw new MError("Selecção não existe... ");
    }
    

    csv = new MHashtable();
    mpnut = new ("excel");
    mpnut.setForm(minForm);
    mpnut.set("CSV", csv);
    mpnut.put("CSV", csv);
  }
  




  public void prepareCsv()
    throws MError
  {
    String wheader1 = "";String wheader2 = "";String wdef_cursor = "";
    
    String csv_separator = (String)csv.retrieve("SEPARATOR");
    String csv_decimal = (String)csv.retrieve("DECIMAL");
    
    if (wargs[0].toUpperCase().indexOf("DEFAULT") != -1) {
      Object[] myCols = myModel.getColumnArray();
      
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          wheader1 = wheader1 + csv_separator + "\"" + wcol.getPrintTitle1() + "\"";
          wheader2 = wheader2 + csv_separator + "\"" + wcol.getPrintTitle2() + "\"";
          
          if (wcol.getDataType() == "NUMTYPE") {
            wdef_cursor = wdef_cursor + csv_separator + "[toNumber(row." + wcol.getName() + ")]";
          } else {
            wdef_cursor = wdef_cursor + csv_separator + "\"[row." + wcol.getName() + "]\"";
          }
        }
      }
      System.out.println(wheader1);
      System.out.println(wheader2);
      System.out.println(wdef_cursor);
    }
    
    csv.put("ENTNAME", minForm.getEntName());
    csv.put("HEADER1", wheader1);
    csv.put("HEADER2", wheader2);
    csv.put("DEFAULT", wdef_cursor);
    csv.put("TOTAL", " ");
    csv.put("FOOTER", MDate.getToday() + "  " + MUtil.getTime() + MUtil.getMinProperty("user.name"));
  }
  
  public String getCsvFileName() throws MError
  {
    return wargs[0];
  }
  
  public void closeReport() throws MError
  {
    mpnut.reload();
    mpnut = null;
  }
  
  public void prepareCursor(String cursor_name, String cursor_mask) throws MError {
    if ((cursor_mask.indexOf("DEFAULT") == -1) || 
    




      (minForm.getBrowse().isTotalized()))
    {
      System.out.println(getCursorRow("DEFAULT", wrows.size() - 1));
    }
  }
  
  public boolean hasMoreThan(String cursor_name, int n) throws MError {
    if (wrows.size() > n) {
      if ((minForm.getBrowse().isTotalized()) && (wrows.size() == n + 1)) {
        return false;
      }
      return true;
    }
    return false;
  }
  
  public String getCursorRow(String cursor_name, int i) throws MError
  {
    MRow mrow = (MRow)wrows.elementAt(i);
    mpnut.setRow(mrow);
    mpnut.eval("row = zemc.row");
    MUtil.expand(mrow);
    
    return mpnut.evalValues((String)csv.get(cursor_name));
  }
}
