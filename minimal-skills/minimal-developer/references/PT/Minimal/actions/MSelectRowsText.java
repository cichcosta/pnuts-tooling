package PT.Minimal.actions;

import PT.Minimal.geral.MBrowse;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import com.objectshare.base.util.OSIIntVector;
import com.objectshare.grid.data.JKTableModel;
import java.io.PrintStream;
import java.util.Vector;

public class MSelectRowsText
  implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private JKTableModel myModel;
  private String[] wargs;
  
  public MSelectRowsText() {}
  
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
  
  public void execute() throws MError
  {
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      MUtil.MoveRecord(minForm.getFormValues(), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    if (minForm.getType() == "MULTIROW") {
      myModel = ((JKTableModel)minForm.getBrowse().getModel());
      wrows = myModel.getRowVector();
    }
    OSIIntVector wvint = new OSIIntVector();
    MCursor text_curs = MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow wr = (MRow)wrows.elementAt(j);
        if ((!wr.getCst().equals("st")) && (wr.hasText(text_curs))) {
          System.out.println("Row com texto:" + j);
          wvint.add(j + 1);
        }
      }
    } finally {
      text_curs.close();
    }
    minForm.getBrowse().setSelectedRowIndices(wvint);
  }
}
