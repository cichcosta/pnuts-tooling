package PT.Minimal.entities;

import PT.Minimal.geral.MDialog;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import java.util.Vector;

public class SPRO0 implements PT.Minimal.actions.MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  MForm minForm;
  Vector wrows;
  String EOL = "\n";
  
  public SPRO0() {}
  
  public void setMCommand(PT.Minimal.geral.MCommand wcmd)
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
    if ((minCommand.getName().equals("MAKEPROCDDL")) && (minForm.getFase() != 101)) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      MRow xsprot = (MRow)wrows.elementAt(0);
      show_DDL();
    }
    if ((minCommand.getName().equals("MAKEPROCDDL")) && (minForm.getFase() == 101)) {
      do_DDL();
    }
    if (minCommand.getName().equals("READDBDDL")) {
      read_db_DDL();
    }
  }
  
  private void show_DDL() throws MError {
    try {
      for (int j = 0; j < wrows.size(); j++) {
        String wtext = "";
        byte[] wobj = ((MRow)wrows.elementAt(j)).getAssociatedObject("PROCDDL");
        if (wobj != null) {
          wtext = new String(wobj);
        }
        javax.swing.JTextArea minText = new javax.swing.JTextArea(wtext);
        minText.setName("minText");
        javax.swing.JScrollPane wpa = new javax.swing.JScrollPane(minText);
        MDialog wd = new MDialog(minForm.getMFrame(), "DDL - Procedimentos", true, 3, wpa);
        

        wd.getForm().setEnt(minForm.getEnt());
        wd.getForm().setAction(minCommand.getName());
        wd.getForm().setFase(101);
        wd.getForm().setActionObject(minText);
        wd.getForm().setRow((MRow)wrows.elementAt(j));
        wd.getForm().prepareForm();
        wd.pack();
        wd.setSize(new java.awt.Dimension(525, 350));
        wd.setCentered(new java.awt.Dimension(525, 350));
        wd.getForm().showForm();
      }
    } catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
    wrows = null;
  }
  
  private void read_db_DDL() throws MError {
    PT.Minimal.geral.MCursor spro_curs = PT.Minimal.geral.MConnection.getMCursor();
    



    for (int j = 0; j < wrows.size(); j++) {
      MRow wspro = (MRow)wrows.elementAt(j);
      String wprocname = ((String)wspro.getField("iproc")).toLowerCase();
      String wcsel = "select b.seqno, b.data from sysprocbody b, sysprocedures a where a.procid = b.procid and b.datakey = 'T' and a.procname = '" + wprocname + "' " + "order by b.seqno ";
      



      spro_curs.execQuery(wcsel);
      if (spro_curs.getWdcode() == 100) {
        throw new MError("Procedure não existe...!");
      }
      Vector wv = spro_curs.getAllRecords();
      String wtext = "";
      for (int i = 0; i < wv.size(); i++) {
        wtext = wtext + (String)((java.util.Hashtable)wv.elementAt(i)).get("data");
      }
      spro_curs.closeStatement();
      wspro.setAssociatedObject("PROCDDL", wtext.getBytes());
    }
    spro_curs.close();
  }
  
  private void do_DDL() throws MError {
    String wddl = ((javax.swing.JTextArea)minForm.getActionObject()).getText();
    minForm.getRow().setAssociatedObject("PROCDDL", wddl.getBytes());
    String wprocname = ((String)minForm.getRow().getField("iproc")).toLowerCase();
    PT.Minimal.geral.MCursor spro_curs = PT.Minimal.geral.MConnection.getMCursor();
    System.out.println(wddl);
    String wcsel = "drop procedure " + wprocname + "; ";
    try {
      spro_curs.execStatement(wcsel);
      spro_curs.closeStatement();
    } catch (MError err) {
      if ((err.getWdcode() != 65330) && (err.getWdcode() != 64862)) {
        throw err;
      }
    }
    try
    {
      spro_curs.execStatement(wddl);
      spro_curs.closeStatement();
    } catch (MError err) {
      if ((err.getWdcode() != 65330) && (err.getWdcode() != 64862)) {
        throw err;
      }
    }
    minForm.hideForm();
  }
}
