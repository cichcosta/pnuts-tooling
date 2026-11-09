package PT.Minimal.geral;

import com.objectshare.grid.data.JKModel;
import com.objectshare.grid.data.JKTableModel;
import com.objectshare.grid.edit.JKEditEvent;
import com.objectshare.grid.list.JKSelectionListener;
import java.io.PrintStream;
import java.util.Vector;







public class MBrowseEdit
  extends MBrowse
  implements JKSelectionListener
{
  private Vector wsave;
  private boolean allowAdd = true;
  
  public MBrowseEdit() {
    System.out.println("!*** MBrowseEdit");
  }
  





  public void moveForward()
  {
    JKEditEvent wed = getEditEvent();
    if (wed != null) {
      int wcol = wed.getColumnNumber();
      int wrow = wed.getRowNumber();
      int wncols = getNumberOfColumns();
      int wnrows = getNumberOfRows();
      if (wcol < wncols) {
        wcol += 1;
      } else {
        wrow += 1;
        wcol = 1;
      }
      if (!isCellVisible(wrow, wcol)) {
        keepCellVisible(wrow, wcol);
      }
      MTextFieldEditingPolicy wep = (MTextFieldEditingPolicy)getColumnEditingPolicy(wcol);
      wep.setHandleClose(false);
      openEditor(wcol, wrow);
    }
  }
  
  public void moveBackward() {
    JKEditEvent wed = getEditEvent();
    if (wed != null) {
      int wcol = wed.getColumnNumber();
      int wrow = wed.getRowNumber();
      int wncols = getNumberOfColumns();
      int wnrows = getNumberOfRows();
      if (wcol > 1) {
        wcol -= 1;
      } else {
        wrow -= 1;
        wcol = wncols;
      }
      if (!isCellVisible(wrow, wcol)) {
        keepCellVisible(wrow, wcol);
      }
      MTextFieldEditingPolicy wep = (MTextFieldEditingPolicy)getColumnEditingPolicy(wcol);
      wep.setHandleClose(false);
      openEditor(wcol, wrow);
    }
  }
  
  public void moveUp()
  {
    JKEditEvent wed = getEditEvent();
    if (wed != null) {
      int wcol = wed.getColumnNumber();
      int wrow = wed.getRowNumber();
      int wncols = getNumberOfColumns();
      int wnrows = getNumberOfRows();
      if (wrow > 1) {
        wrow -= 1;
        if (!isCellVisible(wrow, wcol)) {
          keepCellVisible(wrow, wcol);
        }
        MTextFieldEditingPolicy wep = (MTextFieldEditingPolicy)getColumnEditingPolicy(wcol);
        wep.setHandleClose(false);
        openEditor(wcol, wrow);
      }
    }
  }
  
  public void moveDown() {
    JKEditEvent wed = getEditEvent();
    System.out.println("!*** MBrowseEdit EditEVent: " + wed);
    if (wed != null) {
      int wcol = wed.getColumnNumber();
      int wrow = wed.getRowNumber();
      int wncols = getNumberOfColumns();
      int wnrows = getNumberOfRows();
      if (wrow == wnrows) {
        addNewRowForEdit();
        wcol = 1;
        MTextFieldEditingPolicy wep = (MTextFieldEditingPolicy)getColumnEditingPolicy(wcol);
        wep.setHandleClose(false);
      }
      wrow += 1;
      if (!isCellVisible(wrow, wcol)) {
        keepCellVisible(wrow, wcol);
      }
      MTextFieldEditingPolicy wep = (MTextFieldEditingPolicy)getColumnEditingPolicy(wcol);
      wep.setHandleClose(false);
      openEditor(wcol, wrow);
    }
  }
  
  public void allowsAdd(boolean wboo) {
    allowAdd = wboo;
  }
  
  public void addNewRowForEdit() {
    if (allowAdd) {
      MRow wrow = MUtil.getMFormRowOrNull(this);
      MForm mform = MUtil.getMForm(this);
      System.out.println("Form : " + mform.getName().substring(0, 5));
      if (wrow == null) {
        System.out.println("!*** MBrowseEdit wrow null ");
        getModel().addRows(1);
      } else {
        closeEditor();
        MRow yrow = wrow.Copy();
        System.out.println("!*** MBrowseEdit wrow " + wrow);
        yrow.set("zk", "");
        System.out.println("!*** MBrowseEdit yrow " + yrow);
        
        if (mform.getName().substring(0, 5).equals("DDOLF")) {
          String[] campos = yrow.getAtribs();
          System.out.println("MFL Limpa row");
          for (int i = 0; i < campos.length; i++) {
            String campo = campos[i];
            System.out.println("Campo " + i + " --> " + campo);
            if ((yrow.get(campo) instanceof String)) {
              System.out.println(campo + " - " + yrow.get(campo));
              yrow.set(campo, " ".trim());
            }
          }
        }
        addRow(yrow);
        calcTotal();
      }
    }
  }
  
  public void cut() {
    if (isEditable()) {
      Vector wv = getSelectedRowObjectVector();
      if (wv.size() != 0) {
        wsave = wv;
        int[] windexes = getSelectedRowIndexArray();
        for (int i = windexes.length; i > 0; i--) {
          wrows.removeElementAt(windexes[(i - 1)] - 1);
        }
        myModel.removeRows(wsave);
        calcTotal();
      }
    }
  }
  
  public void paste() {
    System.out.println("!*** MBrowseEdit isEditable " + isEditable() + wsave);
    if (isEditable()) {
      if ((wsave != null) && (wsave.size() != 0)) {
        for (int i = 0; i < wsave.size(); i++)
        {

          MRow wrow = (MRow)wsave.elementAt(i);
          wrow = wrow.Copy();
          wrow.set("zk", "");
          addRow(wrow);
        }
        calcTotal();
      } else {
        addNewRowForEdit();
      }
    }
  }
  
  public void copy() {
    if (isEditable()) {
      Vector wv = getSelectedRowObjectVector();
      if (wv.size() != 0) {
        wsave = wv;
      }
    }
  }
}
