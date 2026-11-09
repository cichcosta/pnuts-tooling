package PT.Minimal.entities;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import java.util.Hashtable;
import java.util.Vector;

public class MRFC0 implements PT.Minimal.actions.MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  PT.Minimal.geral.MForm minForm;
  Vector wrows;
  String EOL = "\n";
  
  public MRFC0() {}
  
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
  
  public void execute() throws MError {
    if (minCommand.getName().equals("PROTOTYPE_ENT")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      MRow xmrfct = (MRow)wrows.elementAt(0);
      showPanel((String)xmrfct.getField("i_ob"), "ME");
    }
    if (minCommand.getName().equals("PROTOTYPE_CRIT")) {
      if (wrows.size() == 0) {
        throw new MError("Não existem Registos ... ");
      }
      MRow xmrfct = (MRow)wrows.elementAt(0);
      showPanel((String)xmrfct.getField("i_ob"), "MC");
    }
    if (minCommand.getName().equals("PROTOTYPE_BR")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      MRow xmrfct = (MRow)wrows.elementAt(0);
      showPanel((String)xmrfct.getField("i_ob"), "BR");
    }
    if (minCommand.getName().equals("REORDER")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      throw new MError("Comando Desactivado.");
    }
    

    if (minCommand.getName().equals("REFLECT_ROW")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      reflectRow();
    }
    if (minCommand.getName().equals("CREATEBCTXROW")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      createBCTXRow();
    }
    if (minCommand.getName().equals("CREATEEXTS")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      throw new MError("Comando Desactivado.");
    }
    

    if (minCommand.getName().equals("RUNENT")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      MRow xmrfct = (MRow)wrows.elementAt(0);
      new PT.Minimal.geral.MinMain((String)xmrfct.getField("i_ob"), "Entidade");
    }
    if (minCommand.getName().equals("MAKEDDL")) {
      do_DDL();
    }
  }
  
  private void show_DDL(String wobj) throws MError {
    String wtable = wobj.substring(0, 4);
    
    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA", "jdbc:odbc:minwxxi", "minimal", "mnmn6990");
    String wddl = "";
    String wallf = "";
    String wallfd = "";
    String windice = "";
    String windicez = "";
    


    String wcsel = " select * from mrfct0  where i_ob = '" + wobj + "' " + " order by q_ot";
    


    mrfc_con.execQuery(wcsel);
    Vector wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      String wi_comp = (String)wh.get("i_comp");
      String wc_te = (String)wh.get("c_te");
      String wx_sql = (String)wh.get("x_sql");
      int wnchar = Integer.valueOf((String)wh.get("q_cari")).intValue();
      int wndec = Integer.valueOf((String)wh.get("q_deci")).intValue();
      if ((wx_sql.startsWith("K")) || (wx_sql.startsWith("A"))) {
        wallf = wallf + ", FIELD." + wi_comp;
        wallfd = wallfd + ", FIELD." + wi_comp;
        wddl = wddl + ", " + wi_comp;
        if (wc_te.equals("c")) {
          wddl = wddl + " char(" + wnchar + ")" + EOL;
          wallfd = wallfd + " char(" + wnchar + ")";
        }
        if (wc_te.equals("d")) {
          wddl = wddl + " char(10)" + EOL;
          wallfd = wallfd + " char(10)";
        }
        if (wc_te.equals("n")) {
          wddl = wddl + " decimal(" + wnchar + "," + wndec + ")" + EOL;
          wallfd = wallfd + " decimal(" + wnchar + "," + wndec + ")";
        }
        if (wc_te.equals("s")) {
          wddl = wddl + " serial" + EOL;
          wallfd = wallfd + " integer";
        }
        if (wc_te.equals("i")) {
          wddl = wddl + " integer" + EOL;
          wallfd = wallfd + " integer";
        }
        if (wc_te.equals("t")) {
          wddl = wddl + " text" + EOL;
          wallfd = wallfd + " text";
        }
        if (wc_te.equals("b")) {
          wddl = wddl + " byte" + EOL;
          wallfd = wallfd + " byte";
        }
      }
    }
    
    wcsel = " select * from mrfct0  where i_ob = '" + wobj + "' " + " and x_sql like 'K%'" + " order by x_sql, q_ot";
    



    mrfc_con.execQuery(wcsel);
    wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      String wi_comp = (String)wh.get("i_comp");
      String wc_te = (String)wh.get("c_te");
      String wx_sql = (String)wh.get("x_sql");
      int wnchar = Integer.valueOf((String)wh.get("q_cari")).intValue();
      int wndec = Integer.valueOf((String)wh.get("q_deci")).intValue();
      if (((wx_sql.startsWith("K")) || (wx_sql.startsWith("A"))) && 
        (wx_sql.startsWith("K"))) {
        windice = windice + "," + wi_comp;
      }
    }
    

    if (wddl.trim().length() != 0) {
      wddl = "create table 'minimal'." + wtable + "T0 (" + EOL + " " + wddl.substring(1) + ");" + EOL;
    }
    
    if ((windice.trim().length() != 0) && (!windice.equals("zk")))
    {
      windice = "create unique index 'minimal'." + wtable + "i0 on " + wtable + "t0 (" + windice.substring(1) + ");" + EOL;
    }
    


    windicez = "create unique index 'minimal'." + wtable + "i0k on " + wtable + "t0 (zk);" + EOL;
    
    String wdrop = "drop table " + wtable + "T0; " + EOL;
    
    String wallddl = wdrop.toLowerCase() + wddl.toLowerCase() + windice.toLowerCase() + windicez.toLowerCase() + getXtableDDL(wobj) + getZtableDDL(wobj) + get0ProcDDL(wobj, wallfd) + get0TrigDDL(wobj, wallf) + getZProcDDL(wobj) + getZTrigDDL(wobj);
    










    javax.swing.JTextArea minDDL = new javax.swing.JTextArea(wallddl);
    javax.swing.JScrollPane wpa = new javax.swing.JScrollPane(minDDL);
    minDDL.setText(wallddl);
    minDDL.setSize(525, 350);
    wpa.setName("DDLPane");
    minDDL.setName("minDDL");
    
    PT.Minimal.geral.MDialog wd = new PT.Minimal.geral.MDialog(minForm.getMFrame(), "DDL - " + wtable + "T0", true, 3, wpa);
    
    wd.getForm().setEnt(minForm.getEnt());
    wd.getForm().setRow((MRow)wrows.elementAt(0));
    wd.getForm().setAction(minCommand.getName());
    wd.getForm().setFase(101);
    wd.getForm().setActionObject(minDDL);
    wd.getForm().prepareForm();
    wd.pack();
    wd.setSize(new java.awt.Dimension(525, 350));
    wd.setCentered(new java.awt.Dimension(525, 350));
    wd.getForm().showForm();
  }
  
  private String getXtableDDL(String wobj) {
    String wextgeral = "drop table EEEEtx;" + EOL + "create table 'minimal'.EEEEtx (" + EOL + "  zk serial " + EOL + ", zkent integer " + EOL + ", zcomp char(18) " + EOL + ", zchar char(100) " + EOL + ", zdec decimal(21,6)" + EOL + ");" + EOL + "create unique index 'minimal'.EEEEix on EEEEtx (zkent,zcomp);" + EOL + "create unique index 'minimal'.EEEEixk on EEEEtx (zk);" + EOL;
    








    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
  }
  
  private String getZtableDDL(String wobj) {
    String wextgeral = "drop table EEEEtz;" + EOL + "create table 'minimal'.EEEEtz (" + EOL + "  zkent integer " + EOL + ", zcmd char(18) " + EOL + ", zfase char(2) " + EOL + ", zctl char(20)" + EOL + ", zargs varchar(100) " + EOL + ", zcode integer" + EOL + ", zmsg varchar(100)" + EOL + ");" + EOL + "create unique index 'minimal'.EEEEiz on EEEEtz (zkent);" + EOL;
    












    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
  }
  
  private String getZTrigDDL(String wobj) {
    String wextgeral = "create trigger 'minimal'.EEEErz update of zcmd on EEEEtz " + EOL + "referencing new as a" + EOL + "for each row(" + EOL + "        execute procedure EEEEpz" + EOL + "        (a.zkent, a.zcmd, a.zfase, a.zctl, a.zargs, a.zcode, a.zmsg)" + EOL + "        into zctl, zcode, zmsg);" + EOL;
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
  }
  
  private String getZProcDDL(String wobj) throws MError {
    String wtable = wobj.substring(0, 4);
    MCursor mrfc_curs = PT.Minimal.geral.MConnection.getMCursor();
    String wtext = " ";
    String wextgeral = " ";
    MRow wspro = PT.Minimal.geral.MMData.getMRow("SPRO0");
    wspro.setField("iproct", "PZ");
    wspro.setField("iproc", wtable + "PZ");
    wspro.selectRow(mrfc_curs);
    if (mrfc_curs.getWdcode() == 0) {
      wtext = new String(wspro.getAssociatedObject("PROCDDL"));
    }
    if (wtext.trim().length() == 0) {
      wextgeral = "drop procedure EEEEpz;" + EOL + "create procedure 'minimal'.EEEEpz" + EOL + "  (zkent integer, zcmd char(18), zfase char(2)," + EOL + "  zctl char(20), zargs varchar(100), zcode integer," + EOL + "  zmsg varchar(100))" + EOL + "  returning char(20), integer, varchar(100);" + EOL + " " + EOL + "  -- Reflexo automático dos campos de controlo " + EOL + "  -- (Utilizador, Data, Hora) da operacao" + EOL + " " + EOL + "  update EEEEt0 " + EOL + "  set zutl = USER " + EOL + "  , zdst = today " + EOL + "  , zhst = current hour to second " + EOL + "  where zk = zkent; " + EOL + " " + EOL + "return zctl, zcode, zmsg; " + EOL + "end procedure;" + EOL;

















    }
    else if (!wtext.startsWith("create procedure 'minimal'.")) {
      wtext = "create procedure 'minimal'." + wtext.substring(17);
    }
    
    if (wtext.trim().length() != 0) {
      wextgeral = "drop procedure EEEEpz;" + EOL + wtext + EOL;
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private String get0ProcDDL(String wobj, String wfieldsddl) throws MError {
    String wtable = wobj.substring(0, 4);
    MCursor mrfc_curs = PT.Minimal.geral.MConnection.getMCursor();
    String wtext = " ";
    
    MRow wspro = PT.Minimal.geral.MMData.getMRow("SPRO0");
    wspro.setField("iproct", "P0");
    wspro.setField("iproc", wtable + "P0");
    wspro.selectRow(mrfc_curs);
    if (mrfc_curs.getWdcode() == 0) {
      wtext = new String(wspro.getAssociatedObject("PROCDDL"));
      int wend = wtext.indexOf("-- end create procedure");
      if (wend != -1) {
        wtext = wtext.substring(wend);
      } else {
        wtext = " ";
      }
    }
    String wextgeral = "drop procedure EEEEp0;" + EOL + "create procedure 'minimal'.EEEEp0" + EOL + "        (zfase char(2)" + EOL + MUtil.stringRep(wfieldsddl, "FIELD.", "x") + EOL + MUtil.stringRep(wfieldsddl, "FIELD.", "y") + EOL + "         )" + EOL;
    





    if (wtext.trim().length() == 0) {
      wextgeral = wextgeral + "-- end create procedure" + EOL + "if zfase = 'RI' then" + EOL + "  insert into EEEEtz (zkent, zcmd) values (yzk, 'INSERT_ROW');" + EOL + "end if;" + EOL + "end procedure;" + EOL;

    }
    else
    {

      wextgeral = wextgeral + wtext + EOL;
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private String get0TrigDDL(String wobj, String wfields)
  {
    String wextgeral = "create trigger 'minimal'.EEEErd delete on EEEEt0 " + EOL + "referencing old as y" + EOL + "for each row(" + EOL + "        execute procedure EEEEp0" + EOL + "        ('RD'" + EOL + MUtil.stringRep(wfields, "FIELD", "y") + EOL + MUtil.stringRep(wfields, "FIELD", "y") + EOL + "        )" + EOL + " );" + EOL + "create trigger 'minimal'.EEEEri insert on EEEEt0 " + EOL + "referencing new as x" + EOL + "for each row(" + EOL + "        execute procedure EEEEp0" + EOL + "        ('RI'" + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + "        )" + EOL + " );" + EOL + "create trigger 'minimal'.EEEEru update on EEEEt0 " + EOL + "referencing old as x new as y" + EOL + "for each row(" + EOL + "        execute procedure EEEEp0" + EOL + "        ('RU'" + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + MUtil.stringRep(wfields, "FIELD", "y") + EOL + "        )" + EOL + " );" + EOL;
    



























    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private void do_DDL() throws MError {
    MCursor mrfc_curs = PT.Minimal.geral.MConnection.getMCursor();
    mrfc_curs.beginBatch();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow xmrfct = (MRow)wrows.elementAt(j);
        String wtext = "";String wtable = "";
        if ((!((String)xmrfct.getField("i_ob")).startsWith("W")) || (((String)xmrfct.getField("i_ob")).trim().length() < 7)) {
          wtable = ((String)xmrfct.getField("i_ob")).substring(0, 4);
        } else {
          wtable = ((String)xmrfct.getField("i_ob")).substring(0, 7);
        }
        
        MRow wsdob = PT.Minimal.geral.MMData.getMRow("SDOB0");
        
        wsdob.setField("idob", wtable + "T0");
        wsdob.setField("idobt", "TB");
        wsdob.setField("ndob", "Tabela principal ");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "table");
        wsdob.setField("qger", "10");
        wsdob.setField("cstm", "GE");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "I0");
        wsdob.setField("idobt", "IX");
        wsdob.setField("ndob", "Indice principal ");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("idobto", "index");
        wsdob.setField("qger", "15");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "IZK");
        wsdob.setField("idobt", "IX");
        wsdob.setField("ndob", "Indice do zk ");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "index");
        wsdob.setField("qger", "15");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "TX");
        wsdob.setField("idobt", "TB");
        wsdob.setField("ndob", "Tabela de extensões");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "table");
        wsdob.setField("qger", "10");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "IX");
        wsdob.setField("idobt", "IX");
        wsdob.setField("ndob", "Indice da tabela de extensões");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "index");
        wsdob.setField("qger", "15");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "SZK");
        wsdob.setField("idobt", "TB");
        wsdob.setField("ndob", "Tabela geradora de ZKs");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "table");
        wsdob.setField("qger", "10");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
















        wsdob.setField("idob", wtable + "P0");
        wsdob.setField("idobt", "P0");
        wsdob.setField("ndob", "Procedure principal");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "procedure");
        wsdob.setField("qger", "20");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "PZ");
        wsdob.setField("idobt", "PZ");
        wsdob.setField("ndob", "Procedure auxiliar");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "procedure");
        wsdob.setField("qger", "20");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "PK");
        wsdob.setField("idobt", "PK");
        wsdob.setField("ndob", "Procedure de criacao de ZKs");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "procedure");
        wsdob.setField("qger", "20");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "CRZK");
        wsdob.setField("idobt", "CM");
        wsdob.setField("ndob", "Comando para refazer o ultimo ZK");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "Comando");
        wsdob.setField("qger", "99");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        wsdob.setField("idob", wtable + "RI");
        wsdob.setField("idobt", "TG");
        wsdob.setField("ndob", "Trigger de insert");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "trigger");
        wsdob.setField("qger", "40");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "RU");
        wsdob.setField("idobt", "TG");
        wsdob.setField("ndob", "Trigger de update");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "trigger");
        wsdob.setField("qger", "40");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
        
        wsdob.setField("idob", wtable + "RD");
        wsdob.setField("idobt", "TG");
        wsdob.setField("ndob", "Trigger de delete");
        wsdob.setField("iamb", "Informix");
        wsdob.setField("cst", "PD");
        wsdob.setField("dst", MUtil.getToday());
        wsdob.setField("idobto", "trigger");
        wsdob.setField("qger", "40");
        try {
          wsdob.insertRow(mrfc_curs);
        } catch (MError err) {
          if (err.getWdcode() != 65297) {
            throw err;
          }
        }
      }
      
      mrfc_curs.commitBatch();
    } finally {
      if (mrfc_curs.inBeginBatch()) {
        mrfc_curs.rollbackBatch();
      }
      mrfc_curs.close();
    }
  }
  
  private void showPanel(String wobj, String wtype) throws MError {
    PT.Minimal.geral.MForm.closeForms(wobj + wtype);
    PT.Minimal.actions.MViewPanel wvp = new PT.Minimal.actions.MViewPanel();
    PT.Minimal.geral.MCommand wcmd = new PT.Minimal.geral.MCommand(wobj, "VIEWPANEL");
    String wwaction = minForm.getAction();
    minForm.setAction("INSERT_ROW");
    wcmd.setCallerArgs(wobj + "," + "Painel Propotipo" + "," + wtype + "," + "DEFAULT" + "," + "PROTOTYPE");
    wvp.setMCommand(wcmd);
    wvp.setMForm(minForm);
    Vector wrows_pass = new Vector();
    wrows_pass.addElement(wrows.elementAt(0));
    wvp.setRows(wrows_pass);
    wvp.execute();
  }
  
  private void reorder(String wobj)
    throws MError
  {
    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA", "jdbc:odbc:minwxxi", "minimal", "XXXXXXXXXXX");
    



    String wcsel = "select zk, q_ot from mrfct0  where i_ob = '" + wobj + "' " + " and q_ot > 0 " + " order by q_ot, ient";
    



    mrfc_con.execQuery(wcsel);
    Vector wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    wcsel = "update mrfct0 set q_ot = ? where zk = ? ";
    mrfc_con.prepareStatement(wcsel);
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      String zk = (String)xmrfct.get("zk");
      mrfc_con.setPreparedPar(1, (i + 1) * 10);
      mrfc_con.setPreparedPar(2, Double.valueOf(zk).doubleValue());
      int nrows = mrfc_con.execPreparedUpdate();
      if (nrows == 0) {
        mrfc_con.closePreparedStatement();
        throw new MError("MIN-000100E");
      }
    }
    mrfc_con.closePreparedStatement();
    
    wcsel = "select zk, q_oe, q_ot from mrfct0  where i_ob = '" + wobj + "' " + " and q_oe > 0 " + " order by q_oe, q_ot";
    



    mrfc_con.execQuery(wcsel);
    wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    wcsel = "update mrfct0 set q_oe = ? where zk = ? ";
    mrfc_con.prepareStatement(wcsel);
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      String zk = (String)xmrfct.get("zk");
      mrfc_con.setPreparedPar(1, (i + 1) * 10);
      mrfc_con.setPreparedPar(2, Double.valueOf(zk).doubleValue());
      int nrows = mrfc_con.execPreparedUpdate();
      if (nrows == 0) {
        mrfc_con.closePreparedStatement();
        throw new MError("MIN-000100E");
      }
    }
    mrfc_con.closePreparedStatement();
    
    wcsel = "select zk, q_oc, q_ot from mrfct0  where i_ob = '" + wobj + "' " + " and q_oc > 0 " + " order by q_oc, q_ot";
    



    mrfc_con.execQuery(wcsel);
    wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    wcsel = "update mrfct0 set q_oc = ? where zk = ? ";
    mrfc_con.prepareStatement(wcsel);
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      String zk = (String)xmrfct.get("zk");
      mrfc_con.setPreparedPar(1, (i + 1) * 10);
      mrfc_con.setPreparedPar(2, Double.valueOf(zk).doubleValue());
      int nrows = mrfc_con.execPreparedUpdate();
      if (nrows == 0) {
        mrfc_con.closePreparedStatement();
        throw new MError("MIN-000100E");
      }
    }
    mrfc_con.closePreparedStatement();
    
    wcsel = "select zk, q_ob, q_ot from mrfct0  where i_ob = '" + wobj + "' " + " and q_ob > 0 " + " order by q_ob, q_ot";
    



    mrfc_con.execQuery(wcsel);
    wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    wcsel = "update mrfct0 set q_ob = ? where zk = ? ";
    mrfc_con.prepareStatement(wcsel);
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      String zk = (String)xmrfct.get("zk");
      mrfc_con.setPreparedPar(1, (i + 1) * 10);
      mrfc_con.setPreparedPar(2, Double.valueOf(zk).doubleValue());
      mrfc_con.execPreparedUpdate();
    }
    mrfc_con.closePreparedStatement();
  }
  
  private void reflectRow() throws MError {
    String wcsel = " ";
    

    MCursor mrfc_curs = PT.Minimal.geral.MConnection.getMCursor("JMETADATA", "jdbc:odbc:minwxxi", "minimal", "mnmn6990");
    
    for (int j = 0; j < wrows.size(); j++) {
      MRow xmrfct = (MRow)wrows.elementAt(j);
      wcsel = " update mrfct0  set t_comp = '" + xmrfct.getField("t_comp") + " ' " + " , t_lin = '" + xmrfct.getField("t_lin") + " ' " + " , t_lin = '" + xmrfct.getField("t_lin") + " ' " + " , t_col = '" + xmrfct.getField("t_col") + " ' " + " , t_mask = '" + xmrfct.getField("t_mask") + " ' " + " , t_cmd = '" + xmrfct.getField("t_cmd") + " ' " + " , q_car = " + xmrfct.getField("q_car") + " " + " , q_cari = " + xmrfct.getField("q_cari") + " " + " , q_dec = " + xmrfct.getField("q_dec") + " " + " , q_deci = " + xmrfct.getField("q_deci") + " " + " , x_md = '" + xmrfct.getField("x_md") + " ' " + " , x_ajp = '" + xmrfct.getField("x_ajp") + " ' " + " , x_rg = '" + xmrfct.getField("x_rg") + " ' " + " , x_tc = '" + xmrfct.getField("x_tc") + " ' " + " , x_tb = '" + xmrfct.getField("x_tb") + " ' " + " , t_txt = '" + xmrfct.getField("t_txt") + " ' " + " , t_prn1 = '" + xmrfct.getField("t_prn1") + " ' " + " , t_prn2 = '" + xmrfct.getField("t_prn2") + " ' " + " , c_total = '" + xmrfct.getField("c_total") + " ' " + " , iexs = '" + xmrfct.getField("iexs") + " ' " + " , ictx = '" + xmrfct.getField("ictx") + " ' " + " , niob = '" + xmrfct.getField("niob") + " ' " + " , tcmdargs = '" + xmrfct.getField("tcmdargs") + " ' " + " where i_comp = '" + xmrfct.getField("i_comp") + " ' " + " and x_sql[1] = '" + ((String)xmrfct.getField("x_sql")).substring(0, 1) + "' ";
      
























      System.out.println(wcsel);
      mrfc_curs.execStatement(wcsel);
      mrfc_curs.closeStatement();
    }
    mrfc_curs.close();
  }
  
  private void createBCTXRow() throws MError {
    String wcsel = " ";
    

    MCursor mrfc_curs = PT.Minimal.geral.MConnection.getMCursor("JMETADATA", "jdbc:odbc:minwxxi", "minimal", "mnmn6990");
    
    for (int j = 0; j < wrows.size(); j++) {
      MRow xmrfct = (MRow)wrows.elementAt(j);
      wcsel = " insert into bctxt0  values (0, USER, '0000', current hour to second, today, 'ps' , 'BCM'  , 'BCTX*" + ((String)xmrfct.getField("i_comp")).toUpperCase() + " ' " + " , ' ', ' ' " + " , '" + xmrfct.getField("t_comp") + " ', ' ', ' ', ' '" + ", 1, " + xmrfct.getField("q_cari") + ", 0" + " , 'EM', today " + " , '0001-01-01', '9999-12-31', 0 " + " )";
      









      System.out.println(wcsel);
      mrfc_curs.execStatement(wcsel);
      mrfc_curs.closeStatement();
      
      wcsel = " insert into bctxt0  select 0, USER, '0000', current hour to second, today, 'ps' , 'BCM'  , 'BCTX*" + ((String)xmrfct.getField("i_comp")).toUpperCase() + " ' " + " , '*', ' ' " + " , 'Código Provisório', ' ', ' ', ' ', 0, 0, 0" + " , 'ED', today " + " , '0001-01-01', '9999-12-31'" + " , minzk() " + " from systables where tabid = 1";
      









      System.out.println(wcsel);
      mrfc_curs.execStatement(wcsel);
      mrfc_curs.closeStatement();
    }
    mrfc_curs.close();
  }
  
  private void createExtensions() throws MError {
    String wcsel = " ";
    

    MCursor mrfc_curs = PT.Minimal.geral.MConnection.getMCursor("JMETADATA", "jdbc:odbc:minwxxi", "minimal", "mnmn6990");
    
    for (int j = 0; j < wrows.size(); j++) {
      MRow xmrfct = (MRow)wrows.elementAt(j);
      wcsel = " delete from mexttw ";
      System.out.println(wcsel);
      mrfc_curs.execStatement(wcsel);
      mrfc_curs.closeStatement();
      
      wcsel = " insert into mexttw select * from mextt0 where i_ob = 'EEEE0EXT' ";
      System.out.println(wcsel);
      mrfc_curs.execStatement(wcsel);
      mrfc_curs.closeStatement();
      
      wcsel = " update mexttw set zk = 0, i_ob = '" + xmrfct.getField("i_ob") + "EXT' ";
      System.out.println(wcsel);
      mrfc_curs.execStatement(wcsel);
      mrfc_curs.closeStatement();
      
      wcsel = " insert into mextt0 select * from mexttw ";
      System.out.println(wcsel);
      mrfc_curs.execStatement(wcsel);
      mrfc_curs.closeStatement();
    }
    mrfc_curs.close();
  }
}
