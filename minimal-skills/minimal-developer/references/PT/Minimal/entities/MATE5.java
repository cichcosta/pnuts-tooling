package PT.Minimal.entities;

import PT.Minimal.geral.MDialog;
import PT.Minimal.geral.MRow;
import java.util.Vector;

public class MATE5 implements PT.Minimal.actions.MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  PT.Minimal.geral.MForm minForm;
  Vector wrows;
  
  public MATE5() {}
  
  public void setMCommand(PT.Minimal.geral.MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(PT.Minimal.geral.MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws PT.Minimal.geral.MError {
    if (minCommand.getName().equals("MOVE_FORW")) {
      if (wrows.size() == 0) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      moveForward();
    }
    if (minCommand.getName().equals("MOVE_BACK")) {
      if (wrows.size() == 0) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      
      moveBack();
    }
    if (minCommand.getName().equals("GER_ENT")) {
      if (wrows.size() == 0) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      ger_ent();
    }
    if (minCommand.getName().equals("GER_DDL")) {
      if (wrows.size() == 0) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      ger_DDL();
    }
    if (minCommand.getName().equals("EXEC_DDL")) {
      exec_DDL();
    }
    if (minCommand.getName().equals("RUN_ENT")) {
      if (wrows.size() == 0) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      run_ent();
    }
  }
  
  public void moveForward() throws PT.Minimal.geral.MError
  {
    MRow xmatet = (MRow)wrows.elementAt(0);
    PT.Minimal.geral.MCursor mate_curs = PT.Minimal.geral.MConnection.getMCursor();
    String wcsel = " update matet0 ";
    wcsel = wcsel + " set matexoc = matexoc + 1 ";
    wcsel = wcsel + " where mprji = '" + xmatet.getField("mprji") + "' ";
    wcsel = wcsel + " and metdi = '" + xmatet.getField("metdi") + "' ";
    wcsel = wcsel + " and morgi = '" + xmatet.getField("morgi") + "' ";
    wcsel = wcsel + " and mvngi = '" + xmatet.getField("mvngi") + "' ";
    wcsel = wcsel + " and metdis = '" + xmatet.getField("metdis") + "' ";
    wcsel = wcsel + " and matexoc >= " + xmatet.getField("matexoc") + " ";
    mate_curs.execStatement(wcsel);
    mate_curs.closeStatement();
  }
  
  public void moveBack() throws PT.Minimal.geral.MError
  {
    MRow xmatet = (MRow)wrows.elementAt(0);
    PT.Minimal.geral.MCursor mate_curs = PT.Minimal.geral.MConnection.getMCursor();
    String wcsel = " update matet0 ";
    wcsel = wcsel + " set matexoc = matexoc - 1 ";
    wcsel = wcsel + " where mprji = '" + xmatet.getField("mprji") + "' ";
    wcsel = wcsel + " and metdi = '" + xmatet.getField("metdi") + "' ";
    wcsel = wcsel + " and morgi = '" + xmatet.getField("morgi") + "' ";
    wcsel = wcsel + " and mvngi = '" + xmatet.getField("mvngi") + "' ";
    wcsel = wcsel + " and metdis = '" + xmatet.getField("metdis") + "' ";
    wcsel = wcsel + " and matexoc >= " + xmatet.getField("matexoc") + " ";
    mate_curs.execStatement(wcsel);
    mate_curs.closeStatement();
  }
  
  public void ger_ent() throws PT.Minimal.geral.MError
  {
    MRow xmatet = (MRow)wrows.elementAt(0);
    Vector wv = new Vector();
    
    wv.addElement(new PT.Minimal.geral.MLabel("Projecto", true));
    PT.Minimal.geral.MTextField wtf = new PT.Minimal.geral.MTextField("wproj", 3, false);
    wtf.setMask("U");
    wtf.setText((String)xmatet.getField("mprji"));
    wv.addElement(wtf);
    wv.addElement(new PT.Minimal.geral.MLabel("Entidade", true));
    wtf = new PT.Minimal.geral.MTextField("wmetd", 4, false);
    wtf.setMask("U");
    wtf.setText((String)xmatet.getField("metdi"));
    wv.addElement(wtf);
    wtf = new PT.Minimal.geral.MTextField("wmetdsuf", 1, false);
    wtf.setMask("U");
    wtf.setText((String)xmatet.getField("metdis"));
    wv.addElement(wtf);
    MDialog wd = new MDialog(minForm.getMFrame(), "Gerar", true, wv, 3, "Gerar");
    wd.pack();
    wd.setSize(wd.getPreferredSize());
    wd.setLocation(20, 20);
    
    wd.setVisible(true);
  }
  
  public void run_ent() throws PT.Minimal.geral.MError { MRow xmatet = (MRow)wrows.elementAt(0);
    PT.Minimal.geral.MUtil.showExec(minForm.getMFrame(), (String)xmatet.getField("metdi") + (String)xmatet.getField("metdis"));
  }
  
  public void ger_DDL() throws PT.Minimal.geral.MError {
    String EOL = "\n";
    MRow xmatet = (MRow)wrows.elementAt(0);
    PT.Minimal.geral.MCursor mate_curs = PT.Minimal.geral.MConnection.getMCursor();
    String wcsel = " select * from matet0 ";
    wcsel = wcsel + " where mprji = '" + xmatet.getField("mprji") + "' ";
    wcsel = wcsel + " and metdi = '" + xmatet.getField("metdi") + "' ";
    wcsel = wcsel + " and morgi = '" + xmatet.getField("morgi") + "' ";
    wcsel = wcsel + " and mvngi = '" + xmatet.getField("mvngi") + "' ";
    wcsel = wcsel + " and metdis = '" + xmatet.getField("metdis") + "' ";
    wcsel = wcsel + " and mateckx <> '*' order by matexoc ";
    mate_curs.execQuery(wcsel);
    Vector wv = mate_curs.getAllRecords();
    mate_curs.closeStatement();
    String wddl = "create table " + xmatet.getField("metdi") + "T0 (" + EOL + "  ";
    String windice = "";
    for (int i = 0; i < wv.size(); i++) {
      java.util.Hashtable wh = (java.util.Hashtable)wv.elementAt(i);
      String wmatei = (String)wh.get("matei");
      String wmatect = (String)wh.get("matect");
      String wmateckx = (String)wh.get("mateckx");
      int wnchar = Integer.valueOf((String)wh.get("mateqc")).intValue();
      int wndec = Integer.valueOf((String)wh.get("mateqd")).intValue();
      if (i > 0) {
        wddl = wddl + ", ";
      }
      wddl = wddl + wmatei;
      if (wmateckx.trim().length() != 0) {
        windice = windice + "," + wmatei;
      }
      if (wmatect.equals("c")) {
        wddl = wddl + " char(" + wnchar + ")" + EOL;
      }
      if (wmatect.equals("d")) {
        wddl = wddl + " char(10)" + EOL;
      }
      if (wmatect.equals("n")) {
        wddl = wddl + " decimal(" + wnchar + "," + wndec + ")" + EOL;
      }
      if (wmatect.equals("s")) {
        wddl = wddl + " serial" + EOL;
      }
      if (wmatect.equals("i")) {
        wddl = wddl + " integer" + EOL;
      }
      if (wmatect.equals("t")) {
        wddl = wddl + " text" + EOL;
      }
      if (wmatect.equals("b")) {
        wddl = wddl + " byte" + EOL;
      }
    }
    wddl = wddl + ");" + EOL;
    if (windice.trim().length() != 0) {
      windice = "create unique index " + xmatet.getField("metdi") + "i0 on " + xmatet.getField("metdi") + "t0 (" + windice.substring(1) + ");" + EOL;
    }
    


    String wdrop = "drop table " + xmatet.getField("metdi") + "T0; " + EOL;
    
    java.awt.TextArea minDDL = new java.awt.TextArea(wdrop.toLowerCase() + wddl.toLowerCase() + windice.toLowerCase());
    minDDL.setName("minDDL");
    
    MDialog wd = new MDialog(minForm.getMFrame(), "DDL - " + xmatet.getField("metdi") + "T0", true, 3, minDDL);
    
    wd.getForm().setEnt(minForm.getEnt());
    wd.getForm().setAction("EXEC_DDL");
    wd.getForm().setRow((MRow)wrows.elementAt(0));
    wd.pack();
    wd.setSize(wd.getPreferredSize());
    wd.setCentered();
    wd.setVisible(true);
  }
  
  private void exec_DDL() throws PT.Minimal.geral.MError {
    java.awt.Component wc = PT.Minimal.geral.MUtil.getComponent(minForm.getCenterPanel(), "minDDL");
    if (wc != null) {
      String wddl = ((java.awt.TextArea)wc).getText();
      PT.Minimal.geral.MCursor mate_curs = PT.Minimal.geral.MConnection.getMCursor();
      int wini = 0;
      for (int wfim = wddl.indexOf(";"); wfim != -1; wfim = wddl.indexOf(";", wini)) {
        String wcsel = wddl.substring(wini, wfim);
        wini = wfim + 1;
        try {
          mate_curs.execStatement(wcsel);
          mate_curs.closeStatement();
        } catch (PT.Minimal.geral.MError err) {
          if (err.getWdcode() != 65330) {
            throw err;
          }
        }
      }
    }
  }
}
