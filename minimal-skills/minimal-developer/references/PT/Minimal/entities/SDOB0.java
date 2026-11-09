package PT.Minimal.entities;

import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class SDOB0 implements PT.Minimal.actions.MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  MForm minForm;
  Vector wrows;
  String EOL = "\n";
  

  public SDOB0() {}
  
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
    if (minCommand.getName().equals("MAKEDDL")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      do_DDL((PT.Minimal.geral.MLogin.hasLoginProperty("JMETADATAMODE")) && (PT.Minimal.geral.MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")));
    }
    if (minCommand.getName().equals("GERDDL")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      ger_DDL();
    }
    if (minCommand.getName().equals("CREATEORAOBJ")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      create_ORAOBJ();
    }
    if (minCommand.getName().equals("CREATESQLOBJ")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      create_SQLOBJ();
    }
    if (minCommand.getName().equals("CREATEPOSOBJ")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      create_POSOBJ();
    }
    if (minCommand.getName().equals("SYNCDDL")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      sync_DDL((PT.Minimal.geral.MLogin.hasLoginProperty("JMETADATAMODE")) && (PT.Minimal.geral.MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")));
    }
    if (minCommand.getName().equals("SYNCALL")) {
      sync_ALL();
    }
    if (minCommand.getName().equals("SYNCMETA")) {
      sync_Meta();
    }
    if (minCommand.getName().equals("SYNCCLIENT")) {
      sync_Client(false, (PT.Minimal.geral.MLogin.hasLoginProperty("JMETADATAMODE")) && (PT.Minimal.geral.MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")));
    }
    if (minCommand.getName().equals("SYNCCLIENT_DATA")) {
      sync_Client(true, (PT.Minimal.geral.MLogin.hasLoginProperty("JMETADATAMODE")) && (PT.Minimal.geral.MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")));
    }
    if (minCommand.getName().equals("COPYDATA")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      copy_Data();
    }
    if (minCommand.getName().equals("COPYTABLE")) {
      copy_Table();
    }
    if (minCommand.getName().equals("DELDATA")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      del_Data();
    }
    if (minCommand.getName().equals("SYNCDATA")) {
      sync_Data((PT.Minimal.geral.MLogin.hasLoginProperty("JMETADATAMODE")) && (PT.Minimal.geral.MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")));
    }
    if (minCommand.getName().equals("TRANPROC")) {
      translateProcedure(wrows);
    }
    if (minCommand.getName().equals("TRANINDEX")) {
      translateIndex(wrows);
    }
    if (minCommand.getName().equals("TRAN_ALL")) {
      translateAll();
    }
    MError werr = new MError("MIN-000000M");
    werr.show(minForm.getMFrame());
  }
  
  public void copy_Data() throws MError
  {
    MForm wlogin = MForm.getMForm("Duplo Login");
    if (wlogin != null) {
      wlogin.showForm();
    } else {
      MUtil.showDoubleLogin(minForm.getMFrame());
      wlogin = MForm.getMForm("Duplo Login");
    }
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
    Properties p = MUtil.myProp;
    String wdsn1 = (String)wh.get("dsn1");
    java.util.Enumeration e; if (p.contains(wdsn1)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn1)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn1 = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    
    String wdsn2 = (String)wh.get("dsn2");
    java.util.Enumeration e; if (p.contains(wdsn2)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn2)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn2 = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    
    MCursor curs0 = MConnection.getMCursor(wdsn1, (String)wh.get("user"), (String)wh.get("passwd"));
    MCursor curs1 = MConnection.getMCursor(wdsn1, (String)wh.get("user"), (String)wh.get("passwd"));
    MCursor curs2 = MConnection.getMCursor(wdsn2, (String)wh.get("user"), (String)wh.get("passwd"));
    
    try
    {
      for (int i = 0; i < wrows.size(); i++) {
        MRow xsdobt = (MRow)wrows.elementAt(i);
        if (!((String)xsdobt.getField("zcst")).toLowerCase().startsWith("r")) {
          throw new MError("MIN-002035E");
        }
        String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
        if (!wobjname.substring(4, 6).equals("t0")) {
          throw new MError("MIN-002040E");
        }
        
        MRow wrow = PT.Minimal.geral.MMData.getMRow(wobjname.substring(0, 4) + "0");
        String wcsel = "select * from " + wobjname.substring(0, 4) + "T0";
        curs0.execQuery(wcsel);
        MRow wyrow = wrow.Copy();
        while (curs0.getWdcode() == 0) {
          MUtil.MoveRecord(curs0.getRecord(), wyrow.getRecord());
          
          wyrow.insertRow(curs2, false);
          if (wyrow.hasAssociatedObject("ROWTXT", curs1)) {
            wyrow.setAssociatedObject("ROWTXT", wyrow.getAssociatedObject("ROWTXT", curs1), curs2);
          }
          if ((wobjname.startsWith("sdob")) && 
            (wyrow.hasAssociatedObject("OBJDDL", curs1))) {
            wyrow.setAssociatedObject("OBJDDL", wyrow.getAssociatedObject("OBJDDL", curs1), curs2);
          }
          
          curs0.next();
        }
        curs0.closeStatement();
      }
    } finally {
      curs0.close();
      curs1.close();
      curs2.close();
    }
  }
  
  public void sync_ALL() throws MError {
    System.out.println("!*** SDOB0 sync_ALL(): ");
    
    MForm wlogin = MForm.getMForm("Sincronização");
    if (wlogin != null) {
      wlogin.showForm();
    } else {
      MUtil.showTripleLogin(minForm.getMFrame());
      wlogin = MForm.getMForm("Sincronização");
    }
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
    Properties p = MUtil.myProp;
    String wdsn1 = (String)wh.get("dsn1");
    java.util.Enumeration e; if (p.contains(wdsn1)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn1)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn1 = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    
    String wdsn2 = (String)wh.get("dsn2");
    java.util.Enumeration e; if (p.contains(wdsn2)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn2)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn2 = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    
    String wdsn3 = (String)wh.get("dsn3");
    java.util.Enumeration e; if (p.contains(wdsn3)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn3)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn3 = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    

    MCursor curs1 = MConnection.getMCursor(wdsn1, (String)wh.get("user1"), (String)wh.get("passwd1"));
    MCursor curs2 = MConnection.getMCursor(wdsn2, (String)wh.get("user2"), (String)wh.get("passwd2"));
    MCursor curs3 = MConnection.getMCursor(wdsn3, (String)wh.get("user3"), (String)wh.get("passwd3"));
    try {
      boolean wsyncdata = ((PT.Minimal.geral.MCheckBox)wlogin.getFormComponent("wsyncdata")).isSelected();
      sync_Meta(curs1, MConnection.getMCursor(curs1.getMConnection()), curs2);
      sync_Client(wsyncdata, curs2, curs3, "", "", false);
    } finally {
      curs1.close();
      curs2.close();
      curs3.close();
    }
  }
  
  public void copy_Table() throws MError {
    MForm wlogin = MForm.getMForm("Duplo Login");
    if (wlogin != null) {
      wlogin.showForm();
    } else {
      MUtil.showDoubleLogin(minForm.getMFrame());
      wlogin = MForm.getMForm("Duplo Login");
    }
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
    Properties p = MUtil.myProp;
    String wdsn1 = (String)wh.get("dsn1");
    java.util.Enumeration e; if (p.contains(wdsn1)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn1)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn1 = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    
    String wdsn2 = (String)wh.get("dsn2");
    java.util.Enumeration e; if (p.contains(wdsn2)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn2)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn2 = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    
    MCursor curs0 = MConnection.getMCursor(wdsn1, (String)wh.get("user"), (String)wh.get("passwd"));
    MCursor curs1 = MConnection.getMCursor(wdsn1, (String)wh.get("user"), (String)wh.get("passwd"));
    MCursor curs2 = MConnection.getMCursor(wdsn2, (String)wh.get("user"), (String)wh.get("passwd"));
    

    MForm wdata = MForm.getDialogForm(minForm, "ZEMC0CT", "ME", "Copia de Tabelas", 3);
    

    wdata.showForm();
    
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    
    String wtori = wdata.getFormValue("tori");
    String wtdest = wdata.getFormValue("tdest");
    String wtrow = wdata.getFormValue("trow");
    

    try
    {
      MRow wrow = PT.Minimal.geral.MMData.getMRow(wtrow.toUpperCase());
      wrow.getMTable().setTable(wtdest);
      String wcsel = "select * from " + wtori;
      curs0.execQuery(wcsel);
      MRow wyrow = wrow.Copy();
      while (curs0.getWdcode() == 0) {
        MUtil.MoveRecord(curs0.getRecord(), wyrow.getRecord());
        wyrow.insertRow(curs2, false);
        curs0.next();
      }
      curs0.closeStatement();
    } finally {
      curs0.close();
      curs1.close();
      curs2.close();
    }
  }
  
  public void sync_Client(boolean withData, boolean metafile) {
    System.out.println("!*** SDOB0 sync_Client(" + withData + "," + metafile + ")");
    MCursor curs0;
    MCursor curs0;
    MCursor curs2; if (!metafile) {
      MForm wlogin = MForm.getMForm("Menu Login");
      wlogin.setEnt("SDOB0");
      wlogin.setAction("NONE");
      wlogin.showForm();
      if (getFormParentbuttonChoosed == 1) {
        throw new MError("MIN-000001W");
      }
      Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
      
      String wdsn = (String)wh.get("dsn");
      Properties p = MUtil.myProp;
      java.util.Enumeration e; if (p.contains(wdsn)) {
        for (e = p.keys(); e.hasMoreElements();) {
          String wkey = (String)e.nextElement();
          if ((p.getProperty(wkey).equals(wdsn)) && 
            (p.get("x" + wkey.substring(1)) != null)) {
            wdsn = (String)p.get("x" + wkey.substring(1));
          }
        }
      }
      
      MCursor curs2 = MConnection.getMCursor(wdsn, (String)wh.get("user"), (String)wh.get("passwd"));
      curs0 = MConnection.getMCursor();
    } else {
      curs0 = MConnection.getMCursor();
      curs2 = MConnection.getMCursor();
    }
    try
    {
      PT.Minimal.pnuts. mpnut = PT.Minimal.pnuts..findMPnut("SDOB0");
      mpnut.set("sync_curs", curs2);
      mpnut.set("meta_curs", curs0);
      mpnut.evalPnut("SDOB0             ", "MC", "SINC_CLIENTE", "BF");
      mpnut.unset("sync_curs");
      mpnut.unset("meta_curs");
      
      apllyPatches("BF", curs0, curs2, metafile);
      sync_Client(withData, curs0, curs2, "", "", metafile);
      apllyPatches("AF", curs0, curs2, metafile);
      apllySCFG(curs2);
      
      mpnut.set("sync_curs", curs2);
      mpnut.set("meta_curs", curs0);
      mpnut.evalPnut("SDOB0", "MC", "SINC_CLIENTE", "AF");
      mpnut.unset("sync_curs");
      mpnut.unset("meta_curs");
    }
    finally
    {
      curs0.close();
      curs2.close();
    }
  }
  
  public void sync_Client(boolean withData, MCursor curs0, MCursor curs2, String wzdst, String wzhst, boolean metafile) throws MError
  {
    System.out.println("!*** SDOB0 sync_Client(" + withData + "," + curs0 + "," + curs2 + "," + wzdst + "," + wzhst + ")");
    String wamb = curs2.getMConnection().getAmbient();
    

    PT.Minimal.geral.MDBProperties wdbprop = new PT.Minimal.geral.MDBProperties(curs2.getMConnection());
    String wlsync = (String)wdbprop.get("LAST_CLIENT_SYNC");
    String wdbtype = (String)wdbprop.get("DATABASE_TYPE");
    if ((wdbtype == null) || (!wdbtype.equals("CLIENT"))) {
      throw new MError("MIN-SDOB12E");
    }
    if (wzdst.trim().length() == 0) {
      if (wlsync != null) {
        wzdst = wlsync.substring(0, 10);
        wzhst = wlsync.substring(11);
      }
      MForm wdata = MForm.getDialogForm(minForm, "ZEMC0D", "ME", "Sincronização Cliente", 3);
      
      wdata.setFormValue("zdst", wzdst);
      wdata.setFormValue("zhst", wzhst);
      wdata.setFormValue("iamb", curs2.getMConnection().getAmbient());
      ((java.awt.Dialog)wdata.getFormParent()).setTitle("Sincronização Cliente");
      
      wdata.showForm();
      
      if (getFormParentbuttonChoosed == 1) {
        throw new MError("MIN-000001W");
      }
      wzdst = wdata.getFormValue("zdst");
      wzhst = wdata.getFormValue("zhst");
    }
    
    if (wzdst.trim().length() == 0) {
      wzdst = " ";
    }
    

    if (!metafile) {
      PT.Minimal.geral.MDBProperties wdbprop0 = new PT.Minimal.geral.MDBProperties(curs0.getMConnection());
      wlsync = (String)wdbprop0.get("LAST_META_SYNC");
      wdbtype = (String)wdbprop0.get("DATABASE_TYPE");
      if ((wdbtype == null) || (!wdbtype.equals("METADATA")) || (wlsync == null)) {
        System.out.println("MFL : " + curs0.getMConnection().getURL());
        if ((curs0.getMConnection().getURL().equals("jdbc:informix-sqli://simh2160.minimal.pt:1526/minj9801:INFORMIXSERVER=simh2160")) || (curs0.getMConnection().getURL().equals("jdbc:informix-sqli://simh2160:1526/minj9801:INFORMIXSERVER=simh2160")))
        {
          wlsync = MUtil.getToday() + " " + MUtil.getTime();
        } else {
          throw new MError("MIN-SDOB13E");
        }
      }
    } else {
      PT.Minimal.geral.MDBProperties wdbprop0 = new PT.Minimal.geral.MDBProperties(curs0.getMConnection());
      wlsync = (String)wdbprop0.get("LAST_META_LOAD");
      if (wlsync == null) {
        throw new MError("MIN-SDOB13E");
      }
    }
    wdbprop.put("LAST_CLIENT_SYNC", wlsync);
    try
    {
      MRow xsdobt;
      MRow xsdobt;
      if (metafile) {
        String wcsel = " select * from siobt0 ";
        xsdobt = PT.Minimal.geral.MMData.getMRow("SIOB");
      } else {
        wcsel = " select * from sdobt0 ";
        xsdobt = PT.Minimal.geral.MMData.getMRow("SDOB");
      }
      String wcsel = wcsel + " where iamb = '" + wamb + "' ";
      wcsel = wcsel + " and cst like 'E%' ";
      wcsel = wcsel + " and cstm = 'MS' ";
      wcsel = wcsel + " and zdst||zhst >= '" + wzdst + wzhst + "' ";
      wcsel = wcsel + " order by idob[1,4], qger ";
      
      Vector wv = MUtil.getRows(xsdobt, wcsel, 999999, curs0);
      do_DDL(wv, curs2, true, metafile);
      
      if (metafile) {
        wcsel = " select * from siobt0 ";
      } else {
        wcsel = " select * from sdobt0 ";
      }
      wcsel = wcsel + " where iamb = '" + wamb + "' ";
      wcsel = wcsel + " and idobt in ('PH', 'PB','P0', 'PZ', 'FN', 'PM', 'SY', 'GR', 'TW') ";
      wcsel = wcsel + " and cstm <> 'MS' ";
      wcsel = wcsel + " and cst like 'E%' ";
      wcsel = wcsel + " and zdst||zhst >= '" + wzdst + wzhst + "' ";
      wcsel = wcsel + " order by idob[1,4], qger ";
      
      wv = MUtil.getRows(xsdobt, wcsel, 999999, curs0);
      do_DDL(wv, curs2, true, metafile);
      
      if (metafile) {
        wcsel = " select * from siobt0 ";
      } else {
        wcsel = " select * from sdobt0 ";
      }
      wcsel = wcsel + " where iamb = '" + wamb + "' ";
      wcsel = wcsel + " and idob like '%T0%' ";
      wcsel = wcsel + " and idobt = 'TB' ";
      wcsel = wcsel + " and cst like 'E%' ";
      wcsel = wcsel + " and cstm <> 'MS' ";
      wcsel = wcsel + " and idob[1,4] in ";
      wcsel = wcsel + " ( select distinct idob[1,4]";
      if (metafile) {
        wcsel = wcsel + " from siobt0 ";
      } else {
        wcsel = wcsel + " from sdobt0 ";
      }
      wcsel = wcsel + " where iamb = '" + wamb + "' ";
      wcsel = wcsel + " and cst like 'E%' ";
      wcsel = wcsel + " and idobt not in ('PH', 'PB','P0', 'PZ', 'FN', 'PM', 'SY', 'GR', 'TW') ";
      wcsel = wcsel + " and zdst||zhst >= '" + wzdst + wzhst + "') ";
      wcsel = wcsel + " order by idob[1,4], qger ";
      
      wv = MUtil.getRows(xsdobt, wcsel, 999999, curs0);
      sync_DDL(wv, curs2, metafile);
      

      if (wamb.startsWith("Informix")) {
        if (metafile) {
          xsdobt = PT.Minimal.geral.MMData.getMRow("SIOB");
          wcsel = " select * from siobt0 ";
        } else {
          xsdobt = PT.Minimal.geral.MMData.getMRow("SDOB");
          wcsel = " select * from sdobt0 ";
        }
        wcsel = wcsel + " where iamb = '" + wamb + "' " + " and cst like 'E%' " + " and idobt = 'PM' " + " and zdst||zhst >= '" + wzdst + wzhst + "' " + " order by idob[1,4], qger ";
        




        wv = MUtil.getRows(xsdobt, wcsel, 999999, curs0);
        do_DDL(wv, curs2, true, metafile);
      }
      
      wcsel = " select * from sdobt0 ";
      wcsel = wcsel + " where iamb = '" + wamb + "' ";
      wcsel = wcsel + " and cst like 'E%' ";
      wcsel = wcsel + " order by idob[1,4], qger ";
      

      wv = MUtil.getRows(xsdobt, wcsel, 999999, curs2);
      do_DDL(wv, curs2, true, metafile);
      
      if (withData) {
        sync_Data(curs0, curs2, metafile);
      }
      wdbprop.saveProperty("LAST_CLIENT_SYNC");
    }
    finally {}
  }
  
  public void sync_Meta()
    throws MError
  {
    System.out.println("!*** SDOB0 sync_Meta()");
    MForm wlogin = MForm.getMForm("Menu Login");
    wlogin.setEnt("SDOB0");
    wlogin.setAction("NONE");
    wlogin.showForm();
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
    
    String wdsn = (String)wh.get("dsn");
    Properties p = MUtil.myProp;
    java.util.Enumeration e; if (p.contains(wdsn)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    


    MCursor curs0 = MConnection.getMCursor();
    MCursor curs1 = MConnection.getMCursor();
    MCursor curs2 = MConnection.getMCursor(wdsn, (String)wh.get("user"), (String)wh.get("passwd"));
    try {
      sync_Meta(curs0, curs1, curs2);
    } finally {
      curs0.close();
      curs1.close();
      curs2.close();
    }
  }
  
  public void sync_Meta(MCursor curs0, MCursor curs1, MCursor curs2) throws MError {
    System.out.println("!*** SDOB0 sync_Meta(" + curs0 + "," + curs1 + "," + curs2 + ")");
    
    String wzdst = "";
    String wzhst = "";
    String wmetaamb = "";
    


    PT.Minimal.geral.MDBProperties wdbprop = new PT.Minimal.geral.MDBProperties(curs2.getMConnection());
    String wlsync = (String)wdbprop.get("LAST_META_SYNC");
    String wdbtype = (String)wdbprop.get("DATABASE_TYPE");
    if ((wdbtype == null) || (!wdbtype.equals("METADATA"))) {
      throw new MError("MIN-SDOB11E");
    }
    
    if (wlsync != null) {
      wzdst = wlsync.substring(0, 10);
      wzhst = wlsync.substring(11);
    }
    MForm wdata = MForm.getDialogForm(minForm, "ZEMC0D", "ME", "Sincronização Metadata", 3);
    
    wdata.setFormValue("zdst", wzdst);
    wdata.setFormValue("zhst", wzhst);
    wdata.setFormValue("iamb", curs2.getMConnection().getAmbient());
    ((java.awt.Dialog)wdata.getFormParent()).setTitle("Sincronização Metadata");
    
    wdata.showForm();
    
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    wzdst = wdata.getFormValue("zdst");
    wzhst = wdata.getFormValue("zhst");
    wmetaamb = wdata.getFormValue("iamb");
    
    if (wzdst.trim().length() == 0) {
      wzdst = " ";
    }
    if (wmetaamb.trim().length() == 0) {
      wmetaamb = "%";
    }
    
    PT.Minimal.geral.MDBProperties wdbprop0 = new PT.Minimal.geral.MDBProperties(curs0.getMConnection());
    wlsync = (String)wdbprop0.get("LAST_META_SYNC");
    wdbtype = (String)wdbprop0.get("DATABASE_TYPE");
    if ((wdbtype == null) || (!wdbtype.equals("METADATA")) || (wlsync == null)) {
      System.out.println(curs0.getMConnection().getURL());
      if ((curs0.getMConnection().getURL().equals("jdbc:informix-sqli://simh2160.minimal.pt:1526/minj9801:INFORMIXSERVER=simh2160")) || (curs0.getMConnection().getURL().equals("jdbc:informix-sqli://simh2160:1526/minj9801:INFORMIXSERVER=simh2160")))
      {
        wlsync = MUtil.getToday() + " " + MUtil.getTime();
        wmetaamb = "%";
        System.out.println("Traduzir Objectos para Oracle...");
        translateAll();
      } else {
        throw new MError("MIN-SDOB13E");
      }
    }
    
    wdbprop.put("LAST_META_SYNC", wlsync);
    
    MRow xsdobt = minForm.getRow().Copy();
    try
    {
      Vector wv = null;
      
      String wcsel = " select * from sdobt0 a where a.iamb = '" + curs2.getMConnection().getAmbient() + "' " + " and cst like 'E%' " + " and a.idob in ('MINVARIABLES', 'SMENW1', 'MRFCW1', 'MRFCXCAR', 'SCFGPSETGM', 'SCFGPSETGC','TRIM','RTRIM','TONUMBER','TOCHAR')" + " order by a.idob, a.qger";
      





      wv = MUtil.getRows(xsdobt, wcsel, 9999, curs0);
      if ((wv != null) && (wv.size() > 0)) {
        System.out.println("Criar procedures na Metadata...");
        do_DDL(wv, curs2, true, false);
      }
      
      sync_MetaTable("mrfct0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("mjext0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("mexst0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("mcfxt0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("sment0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("soutt0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("bctxt0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("serrt0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("serct0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("sdobt0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("scgpt0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      sync_MetaTable("sacht0", wzdst, wzhst, curs0, curs1, curs2, wmetaamb);
      

      wdbprop.saveProperty("LAST_META_SYNC");
    }
    finally {}
  }
  
  private void sync_MetaTable(String wobjname, String wzdst, String wzhst, MCursor curs0, MCursor curs1, MCursor curs2) throws MError
  {
    sync_MetaTable(wobjname, wzdst, wzhst, curs0, curs1, curs2, "%");
    System.out.println("!*** SDOB0 sync_MetaTable(" + wobjname + "," + wzhst + "," + curs0 + "," + curs1 + "," + curs2 + ")");
  }
  
  private void sync_MetaTable(String wobjname, String wzdst, String wzhst, MCursor curs0, MCursor curs1, MCursor curs2, String wmetaamb) throws MError
  {
    System.out.println("!*** SDOB0 sync_MetaTable(" + wobjname + "," + wzhst + "," + curs0 + "," + curs1 + "," + curs2 + "," + wmetaamb + ")");
    

    boolean totalSync = false;
    
    if (wzdst.trim().length() == 0) {
      totalSync = true;
    }
    
    if (totalSync) {
      String wcsel = " delete from " + wobjname.substring(0, 4) + "T0 ";
      curs2.execStatement(wcsel);
      
      curs2.closeStatement();
    }
    if (!totalSync) {
      String wcsel = "select zk from " + wobjname.substring(0, 4) + "T0" + " order by zk";
      curs0.execQuery(wcsel);
      Vector wvrec = null;
      String wfirst = "";
      System.out.println("A eliminar da " + wobjname + " por zk ...");
      
      while (curs0.getWdcode() == 0) {
        wvrec = curs0.getXRecords(500);
        String win = "";
        for (int i = 0; i < wvrec.size(); i++) {
          win = win + ", " + ((Hashtable)wvrec.elementAt(i)).get("zk");
        }
        if (wfirst.trim().length() == 0) {
          wfirst = (String)((Hashtable)wvrec.elementAt(0)).get("zk");
        }
        if (wvrec.size() > 0) {
          wcsel = " delete from " + wobjname.substring(0, 4) + "T0 ";
          wcsel = wcsel + " where zk between " + ((Hashtable)wvrec.elementAt(0)).get("zk") + " and " + ((Hashtable)wvrec.elementAt(wvrec.size() - 1)).get("zk");
          wcsel = wcsel + " and zk not in (" + win.substring(1) + ")";
          curs2.execStatement(wcsel);
          
          curs2.closeStatement();
        }
      }
      curs0.closeStatement();
      if ((wvrec != null) && (wvrec.size() > 0)) {
        wcsel = " delete from " + wobjname.substring(0, 4) + "T0 ";
        wcsel = wcsel + " where zk < " + wfirst;
        System.out.println(wcsel);
        curs2.execStatement(wcsel);
        curs2.closeStatement();
        
        wcsel = " delete from " + wobjname.substring(0, 4) + "T0 ";
        wcsel = wcsel + " where zk > " + ((Hashtable)wvrec.elementAt(wvrec.size() - 1)).get("zk");
        curs2.execStatement(wcsel);
        curs2.closeStatement();
        System.out.println(wcsel);
      }
    }
    
    MRow wrow = PT.Minimal.geral.MMData.getMRow(wobjname.substring(0, 4) + "0");
    String wcsel = "select * from " + wobjname.substring(0, 4) + "T0" + " where zdst||zhst >= '" + wzdst + wzhst + "' ";
    
    if ((!totalSync) && (wobjname.startsWith("sdob")) && (
      (wmetaamb.equals("Informix")) || (wmetaamb.equals("Oracle")))) {
      wcsel = wcsel + " and iamb like '" + wmetaamb + "' ";
    }
    

    System.out.println(wcsel);
    
    curs0.execQuery(wcsel);
    MRow wyrow = wrow.Copy();
    while (curs0.getWdcode() == 0) {
      MUtil.MoveRecord(curs0.getRecord(), wyrow.getRecord());
      
      if (!totalSync) {
        try {
          wyrow.deleteRow(curs2, false);
        } catch (MError err) {
          if ((err.getMinErrorCode() == null) || (!err.getMinErrorCode().equals("MIN-000102E")))
          {
            throw err;
          }
        }
      }
      wyrow.insertRow(curs2, false);
      if (wyrow.hasAssociatedObject("ROWTXT", curs1)) {
        wyrow.setAssociatedObject("ROWTXT", wyrow.getAssociatedObject("ROWTXT", curs1), curs2);
      }
      if ((wobjname.startsWith("sdob")) && 
        (wyrow.hasAssociatedObject("OBJDDL", curs1))) {
        wyrow.setAssociatedObject("OBJDDL", wyrow.getAssociatedObject("OBJDDL", curs1), curs2);
      }
      
      curs0.next();
    }
    curs0.closeStatement();
  }
  
  private void sync_MetaTableMRFC(String wobjname, String wzdst, String wzhst, MCursor curs0, MCursor curs1, MCursor curs2)
    throws MError
  {
    Vector wviob = new Vector();
    String wcsel = "select distinct(i_ob) as wiob from mrfct0 where zdst||zhst >= '" + wzdst + wzhst + "' ";
    curs0.execQuery(wcsel);
    if (curs0.getWdcode() == 0) {
      wviob = curs0.getAllRecords();
    }
    curs0.closeStatement();
    MRow wrow = PT.Minimal.geral.MMData.getMRow(wobjname.substring(0, 4) + "0");
    
    for (int i = 0; i < wviob.size(); i++) {
      Hashtable wh = (Hashtable)wviob.elementAt(i);
      String wiob = (String)wh.get("wiob");
      System.out.println(wiob);
      wcsel = "select * from " + wobjname.substring(0, 4) + "T0" + " where i_ob = '" + wiob + "' ";
      
      curs0.execQuery(wcsel);
      System.out.println(wcsel);
      MRow wyrow = wrow.Copy();
      if (curs0.getWdcode() == 0) {
        wcsel = "delete from " + wobjname.substring(0, 4) + "T0" + " where i_ob = '" + wiob + "' ";
        curs2.execStatement(wcsel);
        System.out.println(wcsel);
        curs2.closeStatement();
      }
      while (curs0.getWdcode() == 0) {
        MUtil.MoveRecord(curs0.getRecord(), wyrow.getRecord());
        wyrow.insertRow(curs2, false);
        
        if (wyrow.hasAssociatedObject("ROWTXT", curs1)) {
          wyrow.setAssociatedObject("ROWTXT", wyrow.getAssociatedObject("ROWTXT", curs1), curs2);
        }
        if ((wobjname.startsWith("sdob")) && 
          (wyrow.hasAssociatedObject("OBJDDL", curs1))) {
          wyrow.setAssociatedObject("OBJDDL", wyrow.getAssociatedObject("OBJDDL", curs1), curs2);
        }
        
        curs0.next();
      }
      curs0.closeStatement();
    }
  }
  
  public void del_Data() throws MError
  {
    MForm wlogin = MForm.getMForm("Menu Login");
    wlogin.setEnt("SDOB0");
    wlogin.setAction("NONE");
    wlogin.showForm();
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
    
    String wdsn = (String)wh.get("dsn");
    Properties p = MUtil.myProp;
    java.util.Enumeration e; if (p.contains(wdsn)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    

    MCursor sdob_curs = MConnection.getMCursor(wdsn, (String)wh.get("user"), (String)wh.get("passwd"));
    try {
      for (int i = 0; i < wrows.size(); i++) {
        MRow xsdobt = (MRow)wrows.elementAt(i);
        if (!((String)xsdobt.getField("zcst")).toLowerCase().startsWith("r")) {
          throw new MError("MIN-002035E");
        }
        String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
        String wobjtype = ((String)xsdobt.getField("idobto")).toLowerCase();
        if (!wobjtype.equals("table")) {
          throw new MError("MIN-002040E");
        }
        
        String wcsel = "delete from  " + wobjname + " ";
        System.out.println(wcsel);
        sdob_curs.execStatement(wcsel);
      }
    } finally {
      sdob_curs.closeStatement();
      sdob_curs.close();
    }
  }
  
  public void sync_Data(boolean metafile) throws MError {
    System.out.println("!*** SDOB0 sync_Data() ");
    MCursor sdob_curs;
    MCursor sdob_curs; if (!metafile) {
      MForm wlogin = MForm.getMForm("Menu Login");
      wlogin.setEnt("SDOB0");
      wlogin.setAction("NONE");
      wlogin.showForm();
      if (getFormParentbuttonChoosed == 1) {
        throw new MError("MIN-000001W");
      }
      Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
      
      String wdsn = (String)wh.get("dsn");
      Properties p = MUtil.myProp;
      java.util.Enumeration e; if (p.contains(wdsn)) {
        for (e = p.keys(); e.hasMoreElements();) {
          String wkey = (String)e.nextElement();
          if ((p.getProperty(wkey).equals(wdsn)) && 
            (p.get("x" + wkey.substring(1)) != null)) {
            wdsn = (String)p.get("x" + wkey.substring(1));
          }
        }
      }
      
      sdob_curs = MConnection.getMCursor(wdsn, (String)wh.get("user"), (String)wh.get("passwd"));
    } else {
      sdob_curs = MConnection.getMCursor();
    }
    MCursor curs = MConnection.getMCursor();
    try
    {
      sync_Data(curs, sdob_curs, metafile);
    } finally {
      sdob_curs.close();
      curs.close();
    }
  }
  
  public void sync_Data(MCursor curs, MCursor sdob_curs, boolean metafile) throws MError {
    System.out.println("!*** SDOB0 sync_Data(" + curs + "," + sdob_curs + ")");
    





    PT.Minimal.geral.MDBProperties wdbprop = new PT.Minimal.geral.MDBProperties(sdob_curs.getMConnection());
    String wdbtype = (String)wdbprop.get("DATABASE_TYPE");
    if ((wdbtype == null) || (!wdbtype.equals("CLIENT"))) {
      throw new MError("MIN-SDOB12E");
    }
    String tabela;
    String tabela;
    if (metafile) {
      tabela = "sitxt0";
    } else {
      tabela = "bctxt0";
    }
    MRow wbctx = PT.Minimal.geral.MMData.getMRow("BCTX0");
    String wcsel = "select zk, ictx from " + tabela + " where ivalk = ' ' order by ictx ";
    curs.execQuery(wcsel);
    Vector wv = curs.getAllRecords();
    curs.closeStatement();
    for (int j = 0; j < wv.size(); j++) {
      String wictx = (String)((Hashtable)wv.elementAt(j)).get("ictx");
      String wzk = (String)((Hashtable)wv.elementAt(j)).get("zk");
      


      wcsel = "select count(*) as wcount, nvl(max(zdst||zhst),' ') as wmax ";
      wcsel = wcsel + "from bctxt0";
      wcsel = wcsel + " where ictx = '" + wictx + "' and ivalk <> ' '";
      sdob_curs.execQuery(wcsel);
      Hashtable wcheck1 = sdob_curs.getRecord();
      sdob_curs.closeStatement();
      
      wcsel = "select count(*) as wcount, nvl(max(zdst||zhst),' ') as wmax ";
      wcsel = wcsel + "from " + tabela;
      wcsel = wcsel + " where ictx = '" + wictx + "' and ivalk <> ' '";
      curs.execQuery(wcsel);
      Hashtable wcheck2 = curs.getRecord();
      curs.closeStatement();
      
      if ((wcheck1.get("wcount").equals(wcheck2.get("wcount"))) && (wcheck1.get("wmax").equals(wcheck2.get("wmax"))))
      {
        System.out.println("Tabela " + wictx + " considerada sincronizada ...");
      }
      else {
        System.out.println("Sincroniza " + wictx);
        

        wcsel = "select '+' from bctxt0 where ivalk = ' ' and ictx = '" + wictx + "'";
        sdob_curs.execQuery(wcsel);
        sdob_curs.closeStatement();
        if (sdob_curs.getWdcode() == 100) {
          wbctx.setField("zk", wzk);
          if (metafile) {
            wMTable.setTable("SITXT0");
          }
          wbctx.refreshRow(curs);
          System.out.println(wbctx.get("ictx") + " " + wbctx.get("ivalk"));
          wbctx.setField("zkbctx", "0");
          wMTable.setTable("BCTXT0");
          wbctx.insertRow(sdob_curs, true);
        }
        

        String wcst = "EM";
        wcsel = "select * from " + tabela + " where ivalk <> ' ' and ictx = '" + wictx + "' and cst ='" + wcst + "'";
        Vector wv2 = MUtil.getRows(wbctx, wcsel, 999999, curs);
        for (int z = 0; z < wv2.size(); z++) {
          MRow wbctx2 = (MRow)wv2.elementAt(z);
          String wivalk = (String)wbctx2.get("ivalk");
          String wnctx = (String)wbctx2.get("nctx");
          wcsel = "select '+' from bctxt0 where ivalk = '" + wivalk + "' and ictx = '" + wictx + "'";
          sdob_curs.execQuery(wcsel);
          sdob_curs.closeStatement();
          if (sdob_curs.getWdcode() == 100) {
            System.out.println(wbctx2.get("ictx") + " " + wbctx2.get("ivalk"));
            wbctx2.setField("zkbctx", "0");
            wbctx2.insertRow(sdob_curs, true);
          }
          wcsel = "update bctxt0 set nctx = '" + wnctx + "' where ivalk = '" + wivalk + "' and ictx = '" + wictx + "'";
          sdob_curs.execStatement(wcsel);
          sdob_curs.closeStatement();
        }
        

        wcst = "ED";
        wcsel = "select '+' from bctxt0 where ivalk <> ' ' and ictx = '" + wictx + "' and cst <> 'EM' and cst <>'" + wcst + "'";
        sdob_curs.execQuery(wcsel);
        sdob_curs.closeStatement();
        if (sdob_curs.getWdcode() == 100) {
          wcsel = "select * from " + tabela + " where ivalk <> ' ' and ictx = '" + wictx + "' and cst ='" + wcst + "'";
          wv2 = MUtil.getRows(wbctx, wcsel, 999999, curs);
          for (int z = 0; z < wv2.size(); z++) {
            MRow wbctx2 = (MRow)wv2.elementAt(z);
            String wivalk = (String)wbctx2.get("ivalk");
            String wnctx = (String)wbctx2.get("nctx");
            wcsel = "select '+' from bctxt0 where ivalk = '" + wivalk + "' and ictx = '" + wictx + "'";
            sdob_curs.execQuery(wcsel);
            sdob_curs.closeStatement();
            if (sdob_curs.getWdcode() == 100) {
              System.out.println(wbctx2.get("ictx") + " " + wbctx2.get("ivalk"));
              wbctx2.setField("zkbctx", "0");
              wbctx2.insertRow(sdob_curs, true);
            }
            wcsel = "update bctxt0 set nctx = '" + wnctx + "' where ivalk = '" + wivalk + "' and ictx = '" + wictx + "'";
            sdob_curs.execStatement(wcsel);
            sdob_curs.closeStatement();
          }
        }
      }
    }
    

    if (metafile) {
      tabela = "sient0";
    } else {
      tabela = "sment0";
    }
    wv = null;
    
    wcsel = "select distinct imen from " + tabela + " where substr(imen,6,2) not in ('ME', 'MC', 'BR')";
    curs.execQuery(wcsel);
    if (curs.getWdcode() == 0) {
      System.out.println("SMEN DELETE");
      
      Vector wvmen = curs.getAllRecords();
      for (int j = 0; j < wvmen.size(); j++) {
        String wmenu = (String)((Hashtable)wvmen.elementAt(j)).get("imen");
        wcsel = " delete from sment0 where imen = '" + wmenu + "'";
        sdob_curs.execStatement(wcsel);
        sdob_curs.closeStatement();
      }
      wvmen = null;
    }
    curs.closeStatement();
    

    MRow wsmen = PT.Minimal.geral.MMData.getMRow("SMEN0");
    wcsel = "select * from " + tabela + " where substr(imen,6,2) not in ('ME', 'MC', 'BR')";
    wv = MUtil.getRows(wsmen, wcsel, 999999, curs);
    int cont = wv.size();
    System.out.println("SMEN INSERT");
    for (int j = 0; j < wv.size(); j++) {
      cont--;
      if (cont % 500 == 0) {
        System.out.println("SMEN " + cont);
      }
      wsmen = (MRow)wv.elementAt(j);
      try {
        wsmen.setField("zk", "-1");
        wsmen.insertRow(sdob_curs);
      } catch (MError err) {
        if ((err.getMinErrorCode() != null) && (err.getMinErrorCode().equals("MIN-000803E"))) {
          wsmen.updateRow(sdob_curs, wsmen);
        } else {
          throw err;
        }
      }
    }
    

    if (metafile) {
      tabela = "siutt0";
    } else {
      tabela = "soutt0";
    }
    wv = null;
    
    wcsel = "select * from " + tabela + " where cst <> 'PD'";
    curs.execQuery(wcsel);
    if (curs.getWdcode() == 0)
    {
      Vector wvout = curs.getAllRecords();
      System.out.println("SOUT DELETE");
      for (int j = 0; j < wvout.size(); j++) {
        String wiobj = (String)((Hashtable)wvout.elementAt(j)).get("iobj");
        String wcep1 = (String)((Hashtable)wvout.elementAt(j)).get("cep1");
        String wcep2 = (String)((Hashtable)wvout.elementAt(j)).get("cep2");
        String wigrp = (String)((Hashtable)wvout.elementAt(j)).get("igrp");
        String wiutl = (String)((Hashtable)wvout.elementAt(j)).get("iutl");
        String wipri = (String)((Hashtable)wvout.elementAt(j)).get("ipri");
        String wimapt = (String)((Hashtable)wvout.elementAt(j)).get("imapt");
        String wimapr = (String)((Hashtable)wvout.elementAt(j)).get("imapr");
        String wcmcm = (String)((Hashtable)wvout.elementAt(j)).get("cmcm");
        
        wcsel = " delete from soutt0 ";
        wcsel = wcsel + " where (iobj = '" + wiobj + "'";
        wcsel = wcsel + "    and cep1 = '" + wcep1 + "'";
        wcsel = wcsel + "    and cep2 = '" + wcep2 + "'";
        wcsel = wcsel + "    and igrp = '" + wigrp + "'";
        wcsel = wcsel + "    and iutl = '" + wiutl + "'";
        wcsel = wcsel + "    and ipri = '" + wipri + "'";
        wcsel = wcsel + "    and imapt = '" + wimapt + "'";
        wcsel = wcsel + "    and imapr = '" + wimapr + "'";
        wcsel = wcsel + "    and cmcm = '" + wcmcm + "')";
        wcsel = wcsel + "    or cst <> 'EC' ";
        
        sdob_curs.execStatement(wcsel);
        sdob_curs.closeStatement();
      }
      wvout = null;
    }
    curs.closeStatement();
    
    MRow wsout = PT.Minimal.geral.MMData.getMRow("SOUT0");
    
    wcsel = "select * from " + tabela + " order by iobj";
    wv = MUtil.getRows(wsout, wcsel, 999999, curs);
    System.out.println("SOUT INSERT");
    cont = wv.size();
    for (int j = 0; j < wv.size(); j++) {
      cont--;
      if (cont % 500 == 0) {
        System.out.println("SOUT " + cont);
      }
      wsout = (MRow)wv.elementAt(j);
      MRow wsoutnew = wsout.Copy();
      try {
        wsoutnew.setField("zk", "-1");
        
        wsoutnew.insertRow(sdob_curs);
        if (wsout.hasAssociatedObject("ROWTXT", curs, tabela)) {
          wsoutnew.setAssociatedObject("ROWTXT", wsout.getAssociatedObject("ROWTXT", curs), sdob_curs);
        }
      } catch (MError err) {
        if ((err.getMinErrorCode() != null) && (err.getMinErrorCode().equals("MIN-000803E"))) {
          System.out.println("Ent : " + wsout.get("iobj") + " " + wsout.get("imapr"));
          wsout.updateRow(sdob_curs, wsout, false);
        } else {
          throw err;
        }
      }
    }
    wv = null;
    

    if (metafile) {
      tabela = "sigpt0";
    } else {
      tabela = "scgpt0";
    }
    wv = null;
    
    wcsel = "select * from " + tabela;
    curs.execQuery(wcsel);
    System.out.println("SCGP DELETE");
    if (curs.getWdcode() == 0)
    {
      Vector wvout = curs.getAllRecords();
      for (int j = 0; j < wvout.size(); j++) {
        String wiobj = (String)((Hashtable)wvout.elementAt(j)).get("iobj");
        String wictx = (String)((Hashtable)wvout.elementAt(j)).get("ictx");
        String wiorg = (String)((Hashtable)wvout.elementAt(j)).get("iorg");
        wcsel = " delete from scgpt0 ";
        wcsel = wcsel + " where (iobj = '" + wiobj + "'";
        wcsel = wcsel + "    and ictx = '" + wictx + "'";
        wcsel = wcsel + "    and iorg = '" + wiorg + "')";
        wcsel = wcsel + "    or cst <> 'EC' ";
        sdob_curs.execStatement(wcsel);
        sdob_curs.closeStatement();
      }
      wvout = null;
    }
    curs.closeStatement();
    
    MRow wscgp = PT.Minimal.geral.MMData.getMRow("SCGP0");
    wcsel = "select * from " + tabela + " order by ictx";
    wv = MUtil.getRows(wscgp, wcsel, 999999, curs);
    cont = wv.size();
    System.out.println("SCGP INSERT");
    for (int j = 0; j < wv.size(); j++) {
      wscgp = (MRow)wv.elementAt(j);
      cont--;
      if (cont % 500 == 0) {
        System.out.println("SCGP " + cont);
      }
      try {
        wscgp.setField("zk", "-1");
        
        wscgp.insertRow(sdob_curs);
      } catch (MError err) {
        if ((err.getMinErrorCode() != null) && (err.getMinErrorCode().equals("MIN-000803E"))) {
          wscgp.updateRow(sdob_curs, wscgp);
        } else {
          throw err;
        }
      }
    }
    wv = null;
    
    if (metafile) {
      tabela = "sicht0";
    } else {
      tabela = "sacht0";
    }
    wv = null;
    
    System.out.println("SACH DELETE");
    wcsel = " delete from sacht0 ";
    wcsel = wcsel + " where cst in ('XE','XM') ";
    sdob_curs.execStatement(wcsel);
    sdob_curs.closeStatement();
    curs.closeStatement();
    
    MRow wsach = PT.Minimal.geral.MMData.getMRow("SACH0");
    wcsel = "select * from " + tabela + " where cst in ('XE','XM') order by iob";
    wv = MUtil.getRows(wsach, wcsel, 999999, curs);
    System.out.println("SACH INSERT");
    System.out.println("SACH : " + wv.size() + " linhas.");
    String sachold = " ";
    cont = wv.size();
    for (int j = 0; j < wv.size(); j++) {
      wsach = (MRow)wv.elementAt(j);
      cont--;
      if (cont % 500 == 0) {
        System.out.println("SACH " + cont);
      }
      try {
        wsach.setField("zk", "-1");
        if (sachold != (String)wsach.getField("iob")) {
          sachold = (String)wsach.getField("iob");
        }
        
        wsach.insertRow(sdob_curs);
      } catch (MError err) {
        if ((err.getMinErrorCode() != null) && (err.getMinErrorCode().equals("MIN-000803E"))) {
          wsach.updateRow(sdob_curs, wsach);
        } else {
          throw err;
        }
      }
    }
    wv = null;
  }
  
  public void sync_DDL(boolean metafile) throws MError {
    System.out.println("!*** SDOB0 sync_DDL()");
    MCursor sdob_curs;
    MCursor sdob_curs; if (!metafile) {
      MForm wlogin = MForm.getMForm("Menu Login");
      wlogin.setEnt("SDOB0");
      wlogin.setAction("NONE");
      wlogin.showForm();
      if (getFormParentbuttonChoosed == 1) {
        throw new MError("MIN-000001W");
      }
      Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
      String wdsn = (String)wh.get("dsn");
      Properties p = MUtil.myProp;
      java.util.Enumeration e; if (p.contains(wdsn)) {
        for (e = p.keys(); e.hasMoreElements();) {
          String wkey = (String)e.nextElement();
          if ((p.getProperty(wkey).equals(wdsn)) && 
            (p.get("x" + wkey.substring(1)) != null)) {
            wdsn = (String)p.get("x" + wkey.substring(1));
          }
        }
      }
      
      sdob_curs = MConnection.getMCursor(wdsn, (String)wh.get("user"), (String)wh.get("passwd"));
    } else {
      sdob_curs = MConnection.getMCursor("DEFAULT");
    }
    try
    {
      sync_DDL(wrows, sdob_curs, metafile);
    } finally {
      sdob_curs.close();
    }
  }
  
  protected String convertAmbient(String wamb)
  {
    int i = wamb.indexOf(' ');
    if (i > 0) wamb = wamb.substring(0, i);
    return wamb;
  }
  
  public void sync_DDL(Vector wvrows, MCursor sdob_curs, boolean metafile) throws MError
  {
    System.out.println("!*** SDOB0 sync_DDL(" + wvrows + "," + sdob_curs + ")");
    
    String wamb = sdob_curs.getMConnection().getAmbient();
    PT.Minimal.actions.MGerDDL wgerddl;
    PT.Minimal.actions.MGerDDL wgerddl; if (wamb.equalsIgnoreCase("Oracle")) {
      wgerddl = new PT.Minimal.actions.MGerDDLOra();
    } else { PT.Minimal.actions.MGerDDL wgerddl;
      if (wamb.equalsIgnoreCase("SQL Server")) {
        wgerddl = new PT.Minimal.actions.MGerDDLSql();
      } else { PT.Minimal.actions.MGerDDL wgerddl;
        if (wamb.equalsIgnoreCase("PostgreSQL")) {
          wgerddl = new PT.Minimal.actions.MGerDDLPos();
        } else {
          wgerddl = new PT.Minimal.actions.MGerDDLInf();
        }
      }
    }
    
    for (int j = 0; j < wvrows.size(); j++) {
      MRow xsdobt = (MRow)wvrows.elementAt(j);
      if (!((String)xsdobt.getField("zcst")).toLowerCase().startsWith("r")) {
        throw new MError("MIN-002035E");
      }
      String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
      String wobjtype = ((String)xsdobt.getField("idobt")).toLowerCase();
      String wobjdes = ((String)xsdobt.getField("idobto")).toLowerCase();
      String wobjamb = ((String)xsdobt.getField("iamb")).toLowerCase();
      String wtstorage = ((String)xsdobt.getField("tstorage")).toLowerCase();
      wobjamb = convertAmbient(wobjamb);
      System.out.println("!*** SDOB0 sync_DDL wamb / wobjamb: " + wamb + "/" + wobjamb + "/" + wobjname);
      if (!convertAmbient(wamb).equalsIgnoreCase(wobjamb)) {
        throw new MError("MIN-002045E");
      }
      if ((!wobjname.substring(4, 6).equals("t0")) || (!wobjtype.equals("tb")))
      {
        throw new MError("MIN-002040E");
      }
      String wcsel = "";
      

      if (metafile) {
        wcsel = " select * from siobt0 ";
      } else {
        wcsel = " select * from sdobt0 ";
      }
      wcsel = wcsel + " where iamb like ?  and idob like ?  and cst like 'E%'";
      

      boolean tabExists = wgerddl.existsTable(wobjname.substring(0, 4) + "t0", sdob_curs);
      boolean tableSynced = false;
      if (tabExists) {
        try {
          if (wgerddl.syncTable(wobjname.substring(0, 4) + "t0", wtstorage, sdob_curs, metafile)) {
            Vector wvtable = new Vector();
            wvtable.addElement(xsdobt);
            do_DDL(wvtable, sdob_curs, true, metafile);
            wgerddl.copySyncTable(wobjname.substring(0, 4) + "t0", sdob_curs, metafile);
            tableSynced = true;
          }
        } catch (MError err) {
          System.out.println("Erro: " + wobjname + " " + err.getWdcode());
          xsdobt.setAssociatedText(err.getMessage() + EOL + err.getAuxMessage());
          MError wnerr = new MError("MIN-M00202E", new String[] { wobjdes, wobjname });
          throw wnerr;
        }
        wcsel = wcsel + " and not (idob like  '____T0%' and idobt = 'TB') ";
      }
      if (wgerddl.existsTable(wobjname.substring(0, 4) + "tx", sdob_curs)) {
        wcsel = wcsel + " and not (idob like  '____TX%' and idobt = 'TB') ";
      }
      if (wgerddl.existsTable(wobjname.substring(0, 4) + "tm", sdob_curs)) {
        wcsel = wcsel + " and not (idob like  '____TM%' and idobt = 'TB') ";
      }
      if (wgerddl.existsTable(wobjname.substring(0, 4) + "szk", sdob_curs)) {
        wcsel = wcsel + " and idob not like  '____SZK%' ";
      }
      wcsel = wcsel + " order by qger, idob";
      MCursor wcurs = MConnection.getMCursor();
      wcurs.prepareStatement(wcsel);
      wcurs.setPreparedPar(1, (String)xsdobt.getField("iamb") + "%");
      wcurs.setPreparedPar(2, wobjname.substring(0, 4).toUpperCase() + "%");
      wcurs.execPreparedQuery();
      
      if (wcurs.getWdcode() == 0) {
        Vector wbrec = wcurs.getAllRecords();
        Vector wgerrows = new Vector();
        

        for (int i = 0; i < wbrec.size(); i++) {
          MRow wyrow = xsdobt.Copy();
          MUtil.MoveRecord((Hashtable)wbrec.elementAt(i), wyrow.getRecord());
          String widxtype = ((String)wyrow.getField("idobto")).toLowerCase();
          String widx = ((String)wyrow.getField("idob")).toLowerCase();
          if ((widxtype.equals("index")) && (!tableSynced) && (tabExists)) {
            if (!wgerddl.existsIndex(widx, sdob_curs, wyrow)) {
              wgerrows.addElement(wyrow);
            }
          } else {
            wgerrows.addElement(wyrow);
          }
        }
        System.out.println("wgerrows : " + wgerrows.size());
        do_DDL(wgerrows, sdob_curs, true, metafile);
      }
      wcurs.closePreparedStatement();
    }
  }
  
  private void ger_DDL() throws MError
  {
    for (int j = 0; j < wrows.size(); j++) {
      MRow xsdobt = (MRow)wrows.elementAt(j);
      if (((String)xsdobt.getField("zcst")).toLowerCase().startsWith("r")) {
        throw new MError("MIN-002030E");
      }
      String wtext = "";
      String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
      String wobjtype = ((String)xsdobt.getField("idobt")).toLowerCase();
      String wobjamb = ((String)xsdobt.getField("iamb")).toLowerCase();
      byte[] wobj = xsdobt.getAssociatedObject("OBJDDL");
      
      if (wobj != null) {
        wtext = new String(wobj);
      }
      
      System.out.println("!*** SDOB0 MGerDDL: " + wobjamb);
      PT.Minimal.actions.MGerDDL wgerddl; PT.Minimal.actions.MGerDDL wgerddl; if (wobjamb.equalsIgnoreCase("Oracle")) {
        wgerddl = new PT.Minimal.actions.MGerDDLOra();
      } else { PT.Minimal.actions.MGerDDL wgerddl;
        if (wobjamb.equalsIgnoreCase("SQL Server")) {
          wgerddl = new PT.Minimal.actions.MGerDDLSql();
        } else { PT.Minimal.actions.MGerDDL wgerddl;
          if (wobjamb.equalsIgnoreCase("PostgreSQL")) {
            wgerddl = new PT.Minimal.actions.MGerDDLPos();
          } else {
            wgerddl = new PT.Minimal.actions.MGerDDLInf();
          }
        }
      }
      wtext = wgerddl.getDDL(xsdobt, wtext);
      
      if (wtext.trim().length() == 0) {
        MError wnerr = new MError("MIN-900003E", new String[] { wobjtype, wobjname });
        throw wnerr;
      }
      xsdobt.setAssociatedObject("OBJDDL", wtext.getBytes());
    }
    wrows = null;
  }
  
  private void do_DDL(boolean metafile) throws MError { MCursor sdob_curs;
    MCursor sdob_curs;
    if (!metafile) {
      MForm wlogin = MForm.getMForm("Menu Login");
      wlogin.setEnt("SDOB0");
      wlogin.setAction("NONE");
      wlogin.showForm();
      if (getFormParentbuttonChoosed == 1) {
        throw new MError("MIN-000001W");
      }
      Hashtable wh = MUtil.getRecord(wlogin.getCenterPanel());
      String wdsn = (String)wh.get("dsn");
      Properties p = MUtil.myProp;
      java.util.Enumeration e; if (p.contains(wdsn)) {
        for (e = p.keys(); e.hasMoreElements();) {
          String wkey = (String)e.nextElement();
          if ((p.getProperty(wkey).equals(wdsn)) && 
            (p.get("x" + wkey.substring(1)) != null)) {
            wdsn = (String)p.get("x" + wkey.substring(1));
          }
        }
      }
      
      sdob_curs = MConnection.getMCursor(wdsn, (String)wh.get("user"), (String)wh.get("passwd"));
    } else {
      sdob_curs = MConnection.getMCursor("DEFAULT");
    }
    try
    {
      do_DDL(wrows, sdob_curs, false, metafile);
    } finally {
      sdob_curs.close();
    }
  }
  
  public void do_DDL(Vector wvrows, MCursor sdob_curs, boolean withErrors, boolean metafile) throws MError {
    String werr = "";
    
    String wamb = sdob_curs.getMConnection().getAmbient();
    for (int j = 0; j < wvrows.size(); j++) {
      String wddl = " ";
      MRow xsdobt = (MRow)wvrows.elementAt(j);
      String wobjamb = ((String)xsdobt.getField("iamb")).toLowerCase();
      wobjamb = convertAmbient(wobjamb);
      System.out.println("!*** SDOB0 do_DDL wamb / wobjamb: " + wamb + "/" + wobjamb + "/" + (String)xsdobt.getField("idob"));
      if (!convertAmbient(wamb).equalsIgnoreCase(wobjamb)) {
        throw new MError("MIN-002045E");
      }
      if (!((String)xsdobt.getField("zcst")).toLowerCase().startsWith("r")) {
        throw new MError("MIN-002035E");
      }
      
      byte[] wobj = xsdobt.getAssociatedObject("OBJDDL");
      if (wobj != null) {
        wddl = new String(wobj);
        

        StringBuffer wwddl = new StringBuffer(wddl);
        Character ch10 = new Character('\n');
        
        while (wwddl.indexOf("--") >= 0)
        {
          int traco = wwddl.indexOf("--");
          int flinha = wwddl.indexOf(ch10.toString(), traco + 2);
          if (flinha >= 0) {
            wwddl.delete(traco, flinha);
          } else {
            wwddl.delete(traco, wwddl.length());
          }
        }
        wddl = wwddl.toString();
      }
      

      if ((wddl.trim().length() != 0) && (wddl.trim().startsWith("@"))) {
        try {
          sdob_curs.execStatement(wddl.trim().substring(1));
        } catch (MError err) {
          System.out.println("!*** SDOB0 getWdcode: " + err.getWdcode() + " " + wddl);
        }
      }
      if ((wddl.trim().length() != 0) && (!wddl.trim().startsWith("@"))) {
        String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
        String wobjtype = ((String)xsdobt.getField("idobto")).toLowerCase();
        String wcsel;
        if (wobjtype.equals("table")) {
          try {
            wcsel = " select count(*) from " + wobjname;
            sdob_curs.prepareStatement(wcsel);
            sdob_curs.execQuery(wcsel);
            if (sdob_curs.getWdcode() == 0) {
              int wnrows = sdob_curs.getInt(1);
              if (wnrows != 0) {
                throw new MError("Tabela " + wobjname + " contem registos. ");
              }
            }
          } catch (MError err) {
            System.out.println("!*** SDOB0 getWdcode: " + err.getWdcode());
            

            if ((err.getWdcode() != 65330) && (err.getWdcode() != 208) && (err.getWdcode() != 942) && (err.getWdcode() != 980)) {
              throw err;
            }
          } finally {
            sdob_curs.closeStatement();
            sdob_curs.closePreparedStatement();
          }
        }
        
        String wcsel;
        if (((wobjtype.equals("index")) || ((wobjname.length() > 7) && (wobjname.substring(4, 8).equals("crzk")))) && (wobjamb.toLowerCase().startsWith("sql"))) {
          String wobjnamex = " ";
          if (wobjname.substring(4, 6).equals("ix")) {
            wobjnamex = wobjname.substring(0, 4) + "tx." + wobjname;
          } else {
            wobjnamex = wobjname.substring(0, 4) + "t0." + wobjname;
          }
          if ((wobjname.length() > 7) && (wobjname.substring(4, 8).equals("crzk"))) {
            sdob_curs.execStatement("delete from " + wobjname.substring(0, 4) + "szk");
          }
          wcsel = "drop " + wobjtype + " " + wobjnamex + " ";
        } else {
          wcsel = "";
          if ((!wobjtype.equalsIgnoreCase("grant")) && ((wobjname.length() <= 7) || (!wobjname.substring(4, 8).equals("crzk")))) {
            wcsel = "drop " + wobjtype + " " + wobjname + " ";
          }
        }
        if ((wobjtype.equals("trigger")) && (wobjamb.toLowerCase().startsWith("pos"))) {
          wcsel = wcsel + " on " + wobjname.substring(0, 4) + "t0";
        }
        try {
          sdob_curs.execStatement(wcsel);
        } catch (MError err) {
          System.out.println("Drop Error: " + err.getWdcode() + " " + wcsel);
        } finally {
          sdob_curs.closeStatement();
        }
        
        try
        {
          System.out.println("Do " + wobjtype + " " + wobjname);
          if ((sdob_curs.getMConnection().getAmbient().equalsIgnoreCase("Oracle")) && (wobjtype.equalsIgnoreCase("grant"))) {
            wddl = MUtil.stringRep(wddl, "USER_OF_MINIMAL", "USER_OF_" + sdob_curs.getMConnection().getUser());
          }
          if (wddl.toLowerCase().indexOf("tablespace") >= 0) {
            PT.Minimal.geral.MDBProperties wdbprop = new PT.Minimal.geral.MDBProperties(sdob_curs.getMConnection());
            String tbs = (String)wdbprop.get("TBSPACE" + wobjname.toUpperCase().substring(0, 5));
            if (tbs != null) {
              wddl = MUtil.stringRep(wddl, "#TBS", tbs.trim());
            } else {
              tbs = (String)wdbprop.get("TBSPACE" + wobjname.toUpperCase().substring(0, 4));
              if (tbs != null) {
                wddl = MUtil.stringRep(wddl, "#TBS", tbs.trim());
              } else {
                tbs = (String)wdbprop.get("TBSPACE" + wobjname.toUpperCase());
                if (tbs != null) {
                  wddl = MUtil.stringRep(wddl, "#TBS", tbs.trim());
                }
                else if (wobjtype.equals("index")) {
                  wddl = MUtil.stringRep(wddl, "#TBS", "INDX");
                } else {
                  wddl = MUtil.stringRep(wddl, "#TBS", "USR");
                }
              }
            }
          }
          


          String wddl2 = "";
          if ((wobjname.length() > 5) && (wobjname.substring(4, 6).equals("t0")) && (wobjtype.equalsIgnoreCase("grant")) && (wobjamb.toLowerCase().startsWith("p"))) { String x;
            String x;
            if (metafile) {
              x = MUtil.getSQLValue("select nvl(max(zutl),' ') from siobt0 where idob = upper('" + wobjname.substring(0, 4) + "szk')");
            } else {
              x = MUtil.getSQLValue("select nvl(max(zutl),' ') from sdobt0 where idob = upper('" + wobjname.substring(0, 4) + "szk')");
            }
            if (x.trim() != "") {
              wddl2 = wddl;
              wddl2 = MUtil.stringRep(wddl2, wobjname.substring(0, 4) + "t0", wobjname.substring(0, 4) + "szk");
              sdob_curs.execStatement(wddl2);
            }
          }
          String x;
          if ((wobjname.length() > 7) && (wobjname.substring(4, 8).equals("crzk")) && (wobjamb.toLowerCase().startsWith("p"))) {
            x = MUtil.getSQLValue(wddl, sdob_curs);
          } else {
            sdob_curs.execStatement(wddl);
          }
        } catch (MError err) {
          if (!wobjname.substring(4).equals("crzk")) {
            System.out.println("Erro: " + wobjname + " " + err.getWdcode());
            xsdobt.setAssociatedText(err.getMessage() + EOL + err.getAuxMessage());
            if (withErrors) {
              MError wnerr = new MError("MIN-M00202Q", new String[] { wobjtype, wobjname });
              System.out.println(wnerr.getMessage());
              if ((wobjtype.equals("table")) || (wobjtype.equals("trigger")) || (wobjtype.equals("index")))
              {

                int w = wnerr.show(minForm.getMFrame());
                if (w == 1) {
                  throw new MError("MIN-000001W");
                }
              }
            } else {
              MError wnerr = new MError("MIN-M00202E", new String[] { wobjtype, wobjname });
              throw wnerr;
            }
            
          }
        }
        finally
        {
          sdob_curs.closeStatement();
        }
        if ((wobjamb.equalsIgnoreCase("Oracle")) && ((wobjtype.equals("procedure")) || (wobjtype.equals("function")))) {
          try {
            wcsel = " select to_char(line)||' ' ||text from all_errors where name = '" + wobjname.toUpperCase() + "' order by line";
            sdob_curs.prepareStatement(wcsel);
            sdob_curs.execQuery(wcsel);
            if (sdob_curs.getWdcode() == 0) {
              for (; sdob_curs.getWdcode() == 0; sdob_curs.next()) {
                werr = werr + EOL + sdob_curs.get(1);
              }
              xsdobt.setAssociatedText(werr);
              if (withErrors) {
                MError wnerr = new MError("MIN-M00201Q", new String[] { wobjtype, wobjname });
                System.out.println(wnerr.getMessage());

              }
              else
              {

                MError wnerr = new MError("MIN-M00201E", new String[] { wobjtype, wobjname });
                throw wnerr;
              }
            }
          } catch (MError err) {
            System.out.println(err.getWdcode());
            if ((err.getWdcode() != 65330) && (err.getWdcode() != 942)) {
              throw err;
            }
          } finally {
            sdob_curs.closeStatement();
            sdob_curs.closePreparedStatement();
          }
        }
        xsdobt.setAssociatedText(werr);
      }
    }
  }
  
  public void create_ORAOBJ() throws MError {
    MCursor sdob_curs = MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow xsdobt = (MRow)wrows.elementAt(j);
        String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
        if (!wobjname.substring(4, 6).equals("t0")) {
          throw new MError("MIN-002040E");
        }
        String wcsel = "";
        
        wcsel = " select * from sdobt0  where iamb = 'Oracle'  and idob like 'EEEE%' ";
        


        MRow wyrow = xsdobt.Copy();
        Vector wv = MUtil.getRows(xsdobt, wcsel, 999999, sdob_curs);
        for (int i = 0; i < wv.size(); i++) {
          wyrow = (MRow)wv.elementAt(i);
          String woldobjname = ((String)wyrow.getField("idob")).toLowerCase();
          String wnewname = wobjname.substring(0, 4) + woldobjname.substring(4);
          wnewname = wnewname.toUpperCase();
          wyrow.setField("zk", "-1");
          wyrow.setField("idob", wnewname);
          wyrow.insertRow(sdob_curs);
        }
      }
    } finally {
      sdob_curs.close();
    }
  }
  
  public void create_SQLOBJ() throws MError {
    MCursor sdob_curs = MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow xsdobt = (MRow)wrows.elementAt(j);
        String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
        if ((!wobjname.substring(4, 6).equals("t0")) && (!wobjname.substring(7, 9).equals("t0"))) {
          throw new MError("MIN-002040E");
        }
        String wcsel = "";
        
        wcsel = " select * from sdobt0  where iamb = 'SQL Server'  and idob like 'EEEE%' ";
        


        MRow wyrow = xsdobt.Copy();
        Vector wv = MUtil.getRows(xsdobt, wcsel, 999999, sdob_curs);
        for (int i = 0; i < wv.size(); i++) {
          wyrow = (MRow)wv.elementAt(i);
          String woldobjname = ((String)wyrow.getField("idob")).toLowerCase();
          String wnewname = "";
          if ((!wobjname.startsWith("w")) || (wobjname.trim().length() < 7)) {
            wnewname = wobjname.substring(0, 4) + woldobjname.substring(4);
          } else {
            wnewname = wobjname.substring(0, 7) + woldobjname.substring(4);
          }
          
          wnewname = wnewname.toUpperCase();
          wyrow.setField("zk", "-1");
          wyrow.setField("idob", wnewname);
          wyrow.insertRow(sdob_curs);
        }
      }
    } finally {
      sdob_curs.close();
    }
  }
  
  public void create_POSOBJ() throws MError {
    MCursor sdob_curs = MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow xsdobt = (MRow)wrows.elementAt(j);
        String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
        if (!wobjname.substring(4, 6).equals("t0")) {
          throw new MError("MIN-002040E");
        }
        String wcsel = "";
        
        wcsel = " select * from sdobt0  where iamb = 'PostgreSQL'  and idob like 'EEEE%' ";
        


        MRow wyrow = xsdobt.Copy();
        Vector wv = MUtil.getRows(xsdobt, wcsel, 999999, sdob_curs);
        for (int i = 0; i < wv.size(); i++) {
          wyrow = (MRow)wv.elementAt(i);
          String woldobjname = ((String)wyrow.getField("idob")).toLowerCase();
          String wnewname = wobjname.substring(0, 4) + woldobjname.substring(4);
          wnewname = wnewname.toUpperCase();
          wyrow.setField("zk", "-1");
          wyrow.setField("idob", wnewname);
          wyrow.insertRow(sdob_curs);
        }
      }
    } finally {
      sdob_curs.close();
    }
  }
  
  private void translateAll() throws MError {
    MCursor sdob_curs = MConnection.getMCursor();
    try {
      String wcsel = "";
      
      MRow xsdobt = minForm.getRow().Copy();
      wcsel = " select * from sdobt0 a where a.iamb = 'Informix'  and a.idobt like 'P%'  and a.cst like 'E%'  and a.cstm = 'AM'  and exists (select '+' from sdobt0 b where b.iamb = 'Oracle'  and b.idob = a.idob  and b.idobt in ('P0', 'PZ', 'FN', 'PM')  and b.zdst||b.zhst < a.zdst||a.zhst)  order by  a.iamb,a.qger, a.idobt, a.idob";
      









      Vector wv = MUtil.getRows(xsdobt, wcsel, 9999, sdob_curs);
      if ((wv != null) && (wv.size() > 0)) {
        translateProcedure(wv);
      }
      wcsel = " select * from sdobt0 a where a.iamb = 'Informix'  and a.idobt like 'I%'  and a.cst like 'E%'  and a.cstm = 'AM'  and exists (select '+' from sdobt0 b where b.iamb = 'Oracle'  and b.idob = a.idob  and b.zdst||b.zhst < a.zdst||a.zhst)  order by  a.iamb,a.qger, a.idobt, a.idob";
      









      wv = MUtil.getRows(xsdobt, wcsel, 9999, sdob_curs);
      if ((wv != null) && (wv.size() > 0)) {
        translateIndex(wv);
      }
    } finally {
      sdob_curs.close();
    }
  }
  
  private void translateIndex(Vector wvrows) throws MError {
    MCursor sdob_curs = MConnection.getMCursor();
    try {
      for (int j = 0; j < wvrows.size(); j++) {
        String wddl = " ";
        MRow xsdobt = (MRow)wvrows.elementAt(j);
        String wobjamb = ((String)xsdobt.getField("iamb")).toLowerCase();
        if (!wobjamb.equalsIgnoreCase("INFORMIX")) {
          throw new MError("MIN-002046E");
        }
        if (!((String)xsdobt.getField("idobto")).toLowerCase().startsWith("index")) {
          throw new MError("MIN-002047E");
        }
        if (!((String)xsdobt.getField("zcst")).toLowerCase().startsWith("r")) {
          throw new MError("MIN-002035E");
        }
        byte[] wobj = xsdobt.getAssociatedObject("OBJDDL");
        if (wobj != null) {
          wddl = new String(wobj);
        }
        if (wddl.trim().length() != 0)
        {

          String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
          String wobjtype = ((String)xsdobt.getField("idobto")).toLowerCase();
          String wobjsuf = ((String)xsdobt.getField("idobt")).toLowerCase();
          String wcsel = "";
          wcsel = " select * from sdobt0  where iamb = 'Oracle'  and idob = '" + wobjname.toUpperCase() + "' " + " and idobt = '" + wobjsuf.toUpperCase() + "' ";
          



          MRow wyrow = xsdobt.Copy();
          Vector wv = MUtil.getRows(xsdobt, wcsel, 1, sdob_curs);
          if (wv.size() != 1)
          {
            wyrow = xsdobt.Copy();
            wyrow.setField("iamb", "Oracle");
            wyrow.insertRow(sdob_curs);
          } else {
            wyrow = (MRow)wv.elementAt(0);
          }
          String wnewddl = " ";
          wobj = wyrow.getAssociatedObject("OBJDDL");
          if (wobj != null) {
            wnewddl = new String(wobj);
          }
          System.out.println(wobjname);
          wyrow.setAssociatedObject("OBJDDL", changeIndex(wobjtype, wobjname, wddl, wnewddl, wyrow.getField("iamb").toString()).getBytes());
          wyrow.setField("dst", MUtil.getToday());
          wyrow.setField("zdst", MUtil.getToday());
          wyrow.setField("zhst", MUtil.getTime());
          wyrow.setField("cstm", (String)xsdobt.getField("cstm"));
          wyrow.updateRow(sdob_curs, wyrow, false);
          

          wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
          wobjtype = ((String)xsdobt.getField("idobto")).toLowerCase();
          wobjsuf = ((String)xsdobt.getField("idobt")).toLowerCase();
          wcsel = "";
          wcsel = " select * from sdobt0  where iamb = 'PostgreSQL'  and idob = '" + wobjname.toUpperCase() + "' " + " and idobt = '" + wobjsuf.toUpperCase() + "' ";
          



          wyrow = xsdobt.Copy();
          wv = MUtil.getRows(xsdobt, wcsel, 1, sdob_curs);
          if (wv.size() != 1)
          {
            wyrow = xsdobt.Copy();
            wyrow.setField("iamb", "PostgreSQL");
            wyrow.insertRow(sdob_curs);
          } else {
            wyrow = (MRow)wv.elementAt(0);
          }
          wnewddl = " ";
          wobj = wyrow.getAssociatedObject("OBJDDL");
          if (wobj != null) {
            wnewddl = new String(wobj);
          }
          System.out.println(wobjname);
          
          wyrow.setAssociatedObject("OBJDDL", wddl.getBytes());
          wyrow.setField("dst", MUtil.getToday());
          wyrow.setField("zdst", MUtil.getToday());
          wyrow.setField("zhst", MUtil.getTime());
          wyrow.setField("cstm", (String)xsdobt.getField("cstm"));
          wyrow.updateRow(sdob_curs, wyrow, false);
          

          wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
          wobjtype = ((String)xsdobt.getField("idobto")).toLowerCase();
          wobjsuf = ((String)xsdobt.getField("idobt")).toLowerCase();
          wcsel = "";
          wcsel = " select * from sdobt0  where iamb = 'SQL Server'  and idob = '" + wobjname.toUpperCase() + "' " + " and idobt = '" + wobjsuf.toUpperCase() + "' ";
          



          wyrow = xsdobt.Copy();
          wv = MUtil.getRows(xsdobt, wcsel, 1, sdob_curs);
          if (wv.size() != 1)
          {
            wyrow = xsdobt.Copy();
            wyrow.setField("iamb", "SQL Server");
            wyrow.insertRow(sdob_curs);
          } else {
            wyrow = (MRow)wv.elementAt(0);
          }
          wnewddl = " ";
          wobj = wyrow.getAssociatedObject("OBJDDL");
          if (wobj != null) {
            wnewddl = new String(wobj);
          }
          System.out.println(wobjname);
          wyrow.setAssociatedObject("OBJDDL", changeIndexSql(wobjtype, wobjname, wddl, wnewddl).getBytes());
          wyrow.setField("dst", MUtil.getToday());
          wyrow.setField("zdst", MUtil.getToday());
          wyrow.setField("zhst", MUtil.getTime());
          wyrow.setField("cstm", (String)xsdobt.getField("cstm"));
          wyrow.updateRow(sdob_curs, wyrow, false);
        }
      }
    }
    finally {
      sdob_curs.close();
    }
  }
  
  private String changeIndex(String wobjtype, String wobjname, String winfddl, String woraddl, String storage) throws MError {
    String wnewddl = "";
    wnewddl = MUtil.stringRep(winfddl.toLowerCase(), "cluster", "");
    wnewddl = MUtil.stringRep(wnewddl, ";", "");
    wnewddl = MUtil.stringRep(wnewddl, "'minimal'.", " ");
    if (storage.trim().length() > 0) wnewddl = wnewddl + " tablespace indx ";
    return wnewddl;
  }
  
  private String changeIndexSql(String wobjtype, String wobjname, String winfddl, String woraddl) throws MError {
    String wnewddl = "";
    wnewddl = MUtil.stringRep(winfddl.toLowerCase(), "cluster", "");
    wnewddl = MUtil.stringRep(wnewddl.toLowerCase(), "index", "nonclustered index");
    wnewddl = MUtil.stringRep(wnewddl, ";", "");
    wnewddl = MUtil.stringRep(wnewddl, "'minimal'.", " ");
    return wnewddl;
  }
  
  private void translateProcedure(Vector wvrows) throws MError {
    MCursor sdob_curs = MConnection.getMCursor();
    try {
      for (int j = 0; j < wvrows.size(); j++) {
        String wddl = " ";
        MRow xsdobt = (MRow)wvrows.elementAt(j);
        String wobjamb = ((String)xsdobt.getField("iamb")).toLowerCase();
        if (!wobjamb.equalsIgnoreCase("INFORMIX")) {
          throw new MError("MIN-002046E");
        }
        if (!((String)xsdobt.getField("idobto")).toLowerCase().startsWith("procedure")) {
          throw new MError("MIN-002047E");
        }
        if (!((String)xsdobt.getField("zcst")).toLowerCase().startsWith("r")) {
          throw new MError("MIN-002035E");
        }
        
        byte[] wobj = xsdobt.getAssociatedObject("OBJDDL");
        if (wobj != null) {
          wddl = new String(wobj);
        }
        if (wddl.trim().length() != 0)
        {

          String wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
          String wobjtype = ((String)xsdobt.getField("idobto")).toLowerCase();
          String wcsel = "";
          wcsel = " select * from sdobt0  where iamb = 'Oracle'  and idob = '" + wobjname.toUpperCase() + "' " + " and idobt in ('P0', 'PZ', 'FN', 'PM') ";
          



          MRow wyrow = xsdobt.Copy();
          Vector wv = MUtil.getRows(xsdobt, wcsel, 1, sdob_curs);
          if (wv.size() != 1) {
            throw new MError("MIN-M00203E", new String[] { wobjtype, wobjname });
          }
          wyrow = (MRow)wv.elementAt(0);
          String wnewddl = " ";
          wobj = wyrow.getAssociatedObject("OBJDDL");
          if (wobj != null) {
            wnewddl = new String(wobj);
          }
          System.out.println(wobjname);
          wyrow.setAssociatedObject("OBJDDL", changeProc(wobjtype, wobjname, wddl, wnewddl).getBytes());
          wyrow.setField("dst", MUtil.getToday());
          wyrow.setField("zdst", MUtil.getToday());
          wyrow.setField("zhst", MUtil.getTime());
          wyrow.setField("cstm", (String)xsdobt.getField("cstm"));
          wyrow.updateRow(sdob_curs, wyrow, false);
          

          wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
          wobjtype = ((String)xsdobt.getField("idobto")).toLowerCase();
          wcsel = "";
          wcsel = " select * from sdobt0  where iamb = 'SQL Server'  and idob = '" + wobjname.toUpperCase() + "' " + " and idobt in ('P0', 'PZ', 'FN', 'PM') ";
          



          wyrow = xsdobt.Copy();
          wv = MUtil.getRows(xsdobt, wcsel, 1, sdob_curs);
          if (wv.size() != 1) {
            throw new MError("MIN-M00203E", new String[] { wobjtype, wobjname });
          }
          wyrow = (MRow)wv.elementAt(0);
          wnewddl = " ";
          wobj = wyrow.getAssociatedObject("OBJDDL");
          if (wobj != null) {
            wnewddl = new String(wobj);
          }
          System.out.println(wobjname);
          wyrow.setAssociatedObject("OBJDDL", changeProcSql(wobjtype, wobjname, wddl, wnewddl).getBytes());
          wyrow.setField("dst", MUtil.getToday());
          wyrow.setField("zdst", MUtil.getToday());
          wyrow.setField("zhst", MUtil.getTime());
          wyrow.setField("cstm", (String)xsdobt.getField("cstm"));
          wyrow.updateRow(sdob_curs, wyrow, false);
          

          wobjname = ((String)xsdobt.getField("idob")).toLowerCase();
          wobjtype = ((String)xsdobt.getField("idobto")).toLowerCase();
          wcsel = "";
          wcsel = " select * from sdobt0  where iamb = 'PostgreSQL'  and idob = '" + wobjname.toUpperCase() + "' " + " and idobt in ('P0', 'PZ', 'FN', 'PM') ";
          



          wyrow = xsdobt.Copy();
          wv = MUtil.getRows(xsdobt, wcsel, 1, sdob_curs);
          if (wv.size() != 1) {
            throw new MError("MIN-M00203E", new String[] { wobjtype, wobjname });
          }
          wyrow = (MRow)wv.elementAt(0);
          wnewddl = " ";
          wobj = wyrow.getAssociatedObject("OBJDDL");
          if (wobj != null) {
            wnewddl = new String(wobj);
          }
          System.out.println("!*** SDOB: " + wobjname);
          wyrow.setAssociatedObject("OBJDDL", changeProcPgl(wobjtype, wobjname, wddl, wnewddl).getBytes());
          wyrow.setField("dst", MUtil.getToday());
          wyrow.setField("zdst", MUtil.getToday());
          wyrow.setField("zhst", MUtil.getTime());
          wyrow.setField("cstm", (String)xsdobt.getField("cstm"));
          wyrow.updateRow(sdob_curs, wyrow, false);
        }
      }
    }
    finally {
      sdob_curs.close();
    }
  }
  
  private String changeProc(String wobjtype, String wobjname, String winfddl, String woraddl) throws MError {
    String wddl = woraddl;
    String winfdefine = "";
    String winfbody = "";
    







    boolean isFunction = false;
    
    for (int i = winfddl.indexOf("--#PGL"); i != -1; i = winfddl.indexOf("--#PGL", 0)) {
      int wend = winfddl.indexOf(EOL, i);
      if (wend == -1) break;
      winfddl = winfddl.substring(0, i) + winfddl.substring(wend);
    }
    



    int wend = 0;
    for (int i = winfddl.indexOf("--ORA_EXCLUDE_BEGIN"); i != -1; i = winfddl.indexOf("--ORA_EXCLUDE_BEGIN", wend)) {
      wend = winfddl.indexOf("--ORA_EXCLUDE_END", i);
      if (wend == -1) break;
      winfddl = winfddl.substring(0, i) + winfddl.substring(wend);
    }
    



    int winfini = winfddl.indexOf("INF_DEFINE_BEGIN");
    int winfend = winfddl.indexOf("INF_DEFINE_END");
    if ((winfini != -1) && (winfend != -1)) {
      winfdefine = winfddl.substring(winfini, winfend);
    } else {
      throw new MError("MIN-M00204E", new String[] { wobjtype, wobjname });
    }
    winfini = winfddl.indexOf("INF_BODY_BEGIN");
    winfend = winfddl.indexOf("INF_BODY_END");
    if ((winfini != -1) && (winfend != -1)) {
      winfbody = winfddl.substring(winfini, winfend);
    } else {
      throw new MError("MIN-M00204E", new String[] { wobjtype, wobjname });
    }
    
    int waux = woraddl.indexOf(" function ");
    if (waux != -1) {
      isFunction = true;
    }
    waux = woraddl.indexOf(" FUNCTION ");
    if (waux != -1) {
      isFunction = true;
    }
    

    wend = 0;
    for (int i = winfdefine.indexOf("define global "); i != -1; i = winfdefine.indexOf("define global ", wend)) {
      wend = winfdefine.indexOf(" ", i + 14);
      if (wend == -1) break;
      String wglobalvar = winfdefine.substring(i + 14, wend).trim();
      
      winfdefine = winfdefine.substring(0, i) + "-- " + winfdefine.substring(i);
      
      waux = 0;
      for (int j = winfbody.indexOf(wglobalvar); j != -1; j = winfbody.indexOf(wglobalvar, waux)) {
        int waux2 = j + wglobalvar.length();
        char ch = winfbody.charAt(waux2);
        char ch0 = winfbody.charAt(j - 1);
        if (((!Character.isLetterOrDigit(ch)) && (ch != '_') && (ch != '.')) || ((!Character.isLetterOrDigit(ch0)) && (ch0 != '_') && (ch0 != '.')))
        {
          winfbody = winfbody.substring(0, j) + "MINVARIABLES." + winfbody.substring(j);
        }
        waux = winfbody.indexOf(wglobalvar, j) + 1;
      }
    }
    



    wend = 0;
    for (int i = winfdefine.indexOf("like "); i != -1; i = winfdefine.indexOf("like ", wend)) {
      wend = winfdefine.indexOf(";", i);
      if (wend == -1) break;
      winfdefine = winfdefine.substring(0, i) + winfdefine.substring(i + 4, wend) + "%TYPE" + winfdefine.substring(wend);
    }
    


    winfdefine = MUtil.stringRep(winfdefine, "define ", " ");
    


    winfbody = MUtil.stringRep(winfbody, "end foreach", "end loop");
    int wcurscount = 0;
    String winto = "";
    String wcurs = "";
    wend = 0;
    for (int i = winfbody.indexOf("foreach"); i != -1; i = winfbody.indexOf("foreach", wend)) {
      wend = winfbody.indexOf("-- end declare cursor", i);
      if (wend == -1) break;
      wcurscount += 1;
      waux = winfbody.indexOf("into ", i);
      int waux2 = winfbody.indexOf("from ", i);
      winto = winfbody.substring(waux, waux2 - 1);
      wcurs = winfbody.substring(i + 8, waux) + winfbody.substring(waux2, wend - 1);
      int waux3 = winfdefine.lastIndexOf(";");
      winfdefine = winfdefine.substring(0, waux3 + 1) + EOL + " cursor curs" + wcurscount + " is " + EOL + wcurs + ";" + winfdefine.substring(waux3 + 1);
      winfbody = winfbody.substring(0, i) + "OPEN curs" + wcurscount + ";" + EOL + "  LOOP" + EOL + "  FETCH curs" + wcurscount + " " + winto + ";" + EOL + "  EXIT WHEN curs" + wcurscount + "%NOTFOUND;" + EOL + winfbody.substring(wend);
      








      wend = winfbody.indexOf("-- end declare cursor", i);
      waux2 = winfbody.indexOf("end loop", wend);
      System.out.println("wend:" + wend + " waux2:" + waux2);
      if (waux2 != -1) {
        winfbody = winfbody.substring(0, waux2 + 8) + EOL + "  CLOSE curs" + wcurscount + ";" + EOL + winfbody.substring(waux2 + 8);
      }
    }
    








    wend = 0;
    for (int i = winfbody.indexOf("select "); i != -1; i = winfbody.indexOf("select ", wend))
    {
      for (wend = winfbody.indexOf(";", i); (winfbody.substring(wend + 1, wend + 2).equals("%")) || (winfbody.substring(wend + 1, wend + 2).equals("'")) || ((winfbody.substring(wend + 1, wend + 2).equals("\"")) && (wend != -1)); wend = winfbody.indexOf(";", wend + 1)) {}
      
      if (wend == -1) break;
      boolean inSql = false;
      waux = winfbody.lastIndexOf(";", i);
      int waux2 = winfbody.lastIndexOf("insert", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("update", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("delete", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("select", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      if (!inSql) {
        winfbody = winfbody.substring(0, i) + "  wecode := 0;" + EOL + "  BEGIN" + EOL + winfbody.substring(i, wend + 1) + EOL + "  EXCEPTION " + EOL + "  WHEN NO_DATA_FOUND THEN  " + EOL + "      wecode := SQLCODE ;" + EOL + "  END;" + winfbody.substring(wend + 1);
        










        wend = winfbody.indexOf("END;", i);
      }
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("date("); i != -1; i = winfbody.indexOf("date(", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_date(" + winfbody.substring(i + 5, wend) + ", 'YYYY-MM-DD'" + winfbody.substring(wend);
    }
    



    wend = 0;
    for (int i = winfbody.indexOf("let "); i != -1; i = winfbody.indexOf("let ", wend)) {
      wend = winfbody.indexOf("=", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, wend) + ":" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("tochar("); i != -1; i = winfbody.indexOf("tochar(", wend)) {
      wend = winfbody.indexOf("(", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_char" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("tonumber("); i != -1; i = winfbody.indexOf("tonumber(", wend)) {
      wend = winfbody.indexOf("(", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_number" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("return "); i != -1; i = winfbody.indexOf("return ", wend)) {
      wend = winfbody.indexOf(";", i);
      if (wend == -1) break;
      if (isFunction) {
        winfbody = winfbody.substring(0, i) + "return (" + winfbody.substring(i + 7, wend) + ")" + winfbody.substring(wend);
      } else {
        winfbody = winfbody.substring(0, i + 6) + winfbody.substring(wend);
      }
    }
    



    wend = 0;
    for (int i = winfbody.indexOf("call MINVARIABLES."); i != -1; i = winfbody.indexOf("call MINVARIABLES.", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + winfbody.substring(i + 18);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("call "); i != -1; i = winfbody.indexOf("call ", wend)) {
      wend = winfbody.indexOf(")", i);
      waux = winfbody.indexOf(";", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + winfbody.substring(i + 4, wend + 1) + winfbody.substring(waux);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("dbinfo(\"sqlca.sqlerrd2\")"); i != -1; i = winfbody.indexOf("dbinfo(\"sqlca.sqlerrd2\")", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "SQL%ROWCOUNT" + winfbody.substring(i + 24);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("dbinfo('sqlca.sqlerrd2')"); i != -1; i = winfbody.indexOf("dbinfo('sqlca.sqlerrd2')", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "SQL%ROWCOUNT" + winfbody.substring(i + 24);
    }
    


    winfbody = MUtil.stringRep(winfbody, "end for", "end loop");
    winfbody = MUtil.stringRep(winfbody, "end while", "end loop");
    winfbody = MUtil.stringRep(winfbody, "exit for", "exit");
    
    wend = 0;
    for (int i = winfbody.indexOf("while "); i != -1; i = winfbody.indexOf("while ", wend)) {
      wend = winfbody.indexOf(EOL, i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, wend) + " loop " + winfbody.substring(wend);
    }
    



    wend = 0;
    for (int i = winfbody.indexOf("for "); i != -1; i = winfbody.indexOf("for ", wend)) {
      wend = winfbody.indexOf("=", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, wend) + " in " + winfbody.substring(wend + 1);
      wend = winfbody.indexOf(" to ", i);
      winfbody = winfbody.substring(0, wend) + ".." + winfbody.substring(wend + 4);
      wend = winfbody.indexOf(EOL, i);
      winfbody = winfbody.substring(0, wend) + " loop" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("end if"); i != -1; i = winfbody.indexOf("end if", wend)) {
      wend = winfbody.indexOf(EOL, i);
      if (wend == -1) break;
      waux = winfbody.indexOf(";", i);
      if ((waux == -1) || (waux > wend)) {
        winfbody = winfbody.substring(0, wend) + ";" + winfbody.substring(wend);
      }
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("end loop"); i != -1; i = winfbody.indexOf("end loop", wend)) {
      wend = winfbody.indexOf(EOL, i);
      if (wend == -1) break;
      waux = winfbody.indexOf(";", i);
      if ((waux == -1) || (waux > wend)) {
        winfbody = winfbody.substring(0, wend) + ";" + winfbody.substring(wend);
      }
    }
    



    wend = 0;
    



    for (int i = winfbody.indexOf("["); i != -1; i = winfbody.indexOf("[", wend)) {
      boolean inStr = false;
      for (int j = i - 1; j > 0; j--) {
        char ch = winfbody.charAt(j);
        if ((ch == '\r') || (ch == '\n')) {
          break;
        }
        if ((ch == '\'') || (ch == '"')) {
          inStr = !inStr;
        }
      }
      if (inStr) {
        i++;wend = i;
      }
      else {
        wend = winfbody.indexOf("]", i);
        if (wend == -1) break;
        waux = winfbody.lastIndexOf(" ", i);
        waux = Math.max(waux, winfbody.lastIndexOf(";", i));
        waux = Math.max(waux, winfbody.lastIndexOf(")", i));
        waux = Math.max(waux, winfbody.lastIndexOf(",", i));
        waux = Math.max(waux, winfbody.lastIndexOf("=", i));
        waux = Math.max(waux, winfbody.lastIndexOf("<", i));
        waux = Math.max(waux, winfbody.lastIndexOf(">", i));
        waux = Math.max(waux, winfbody.lastIndexOf("EOL", i));
        
        String[] wparameters = MUtil.getArgs(winfbody.substring(i + 1, wend), ',');
        
        if ((wparameters.length == 0) || (wparameters.length > 2))
          throw new MError("Parametros da substring errados");
        int wsend;
        int wsini; if (wparameters.length == 1) {
          int wsini = Integer.valueOf(wparameters[0].trim()).intValue();
          wsend = wsini;
        } else {
          wsini = Integer.valueOf(wparameters[0].trim()).intValue();
          wsend = Integer.valueOf(wparameters[1].trim()).intValue();
        }
        String wvar = winfbody.substring(waux + 1, i);
        int wsend = wsend - wsini + 1;
        winfbody = winfbody.substring(0, waux + 1) + "substr(" + wvar + "," + wsini + "," + wsend + ")" + winfbody.substring(wend + 1);
      }
    }
    

    wend = 0;
    for (int i = winfbody.indexOf("raise exception "); i != -1; i = winfbody.indexOf("raise exception ", wend)) {
      wend = winfbody.indexOf(";", i);
      if (wend == -1) break;
      waux = winfbody.indexOf(",", i);
      int waux2 = winfbody.indexOf(",", waux + 1);
      int waux3 = winfbody.indexOf("\"", waux2);
      if ((waux != -1) && (waux2 != -1) && (waux3 != -1) && (waux3 < wend)) {
        String ws = "RAISE_APPLICATION_ERROR(-20746," + winfbody.substring(waux3, wend) + ")";
        winfbody = winfbody.substring(0, i) + ws + winfbody.substring(wend);
      }
    }
    


    winfbody = MUtil.stringRep(winfbody, "let ", " ");
    winfbody = MUtil.stringRep(winfbody, "today", "to_char(sysdate, 'YYYY-MM-DD')");
    winfbody = MUtil.stringRep(winfbody, "current hour to second", "to_char(sysdate, 'HH24:MI:SS')");
    winfbody = MUtil.stringRep(winfbody, "\"'\"", "\"''\"");
    winfbody = winfbody.replace('"', '\'');
    winfdefine = MUtil.stringRep(winfdefine, "today", "to_char(sysdate, 'YYYY-MM-DD')");
    winfdefine = MUtil.stringRep(winfdefine, "current hour to second", "to_char(sysdate, 'HH24:MI:SS')");
    
    winfdefine = MUtil.stringRep(winfdefine, "\"'\"", "\"''\"");
    winfdefine = winfdefine.replace('"', '\'');
    winfdefine = MUtil.stringRep(winfdefine, "wevar varchar(254)", "wevar varchar(1024)");
    

    int woraini = wddl.indexOf("ORA_DEFINE_BEGIN");
    int woraend = wddl.indexOf("ORA_DEFINE_END");
    if ((woraini != -1) && (woraend != -1)) {
      wddl = wddl.substring(0, woraini) + MUtil.stringRep(winfdefine, "INF_DEFINE", "ORA_DEFINE") + wddl.substring(woraend);
    } else {
      throw new MError("MIN-M00205E", new String[] { wobjtype, wobjname });
    }
    
    woraini = wddl.indexOf("ORA_BODY_BEGIN");
    woraend = wddl.indexOf("ORA_BODY_END");
    if ((woraini != -1) && (woraend != -1)) {
      wddl = wddl.substring(0, woraini) + MUtil.stringRep(winfbody, "INF_BODY", "ORA_BODY") + wddl.substring(woraend);
    } else {
      throw new MError("MIN-M00205E", new String[] { wobjtype, wobjname });
    }
    return wddl;
  }
  
  private String changeProcSql(String wobjtype, String wobjname, String winfddl, String woraddl) throws MError {
    String wddl = woraddl;
    String winfdefvars = "";
    String winfdefine = "";
    String winfbody = "";
    int winfini = 0;
    int winfend = 0;
    int woraini = 0;
    int woraend = 0;
    int wini = 0;
    int wend = 0;
    int waux = 0;
    int waux2 = 0;
    int waux3 = 0;
    boolean isFunction = false;
    

    wend = 0;
    for (int i = winfddl.indexOf("--SQL_EXCLUDE_BEGIN"); i != -1; i = winfddl.indexOf("--SQL_EXCLUDE_BEGIN", wend)) {
      wend = winfddl.indexOf("--SQL_EXCLUDE_END", i);
      if (wend == -1) break;
      winfddl = winfddl.substring(0, i) + winfddl.substring(wend);
    }
    


    for (int i = winfddl.indexOf("--#PGL "); i != -1; i = winfddl.indexOf("--#PGL ", 0)) {
      wend = winfddl.indexOf(EOL, i);
      if (wend == -1) break;
      winfddl = winfddl.substring(0, i) + winfddl.substring(wend);
    }
    




    winfini = winfddl.indexOf("INF_DEFINE_BEGIN");
    winfend = winfddl.indexOf("INF_DEFINE_END");
    if ((winfini != -1) && (winfend != -1)) {
      winfdefine = winfddl.substring(winfini, winfend);
      winfdefine = winfdefine.substring(0, 16) + EOL + "   declare @CONTROLFECTH int;" + EOL + "   declare @WROWCOUNT int;" + EOL + winfdefine.substring(16);
    } else {
      throw new MError("MIN-M00204E", new String[] { wobjtype, wobjname });
    }
    winfini = winfddl.indexOf("INF_BODY_BEGIN");
    winfend = winfddl.indexOf("INF_BODY_END");
    if ((winfini != -1) && (winfend != -1)) {
      winfbody = winfddl.substring(winfini, winfend);
    } else {
      throw new MError("MIN-M00204E", new String[] { wobjtype, wobjname });
    }
    
    waux = woraddl.indexOf(" function ");
    if (waux != -1) {
      isFunction = true;
    }
    waux = woraddl.indexOf(" FUNCTION ");
    if (waux != -1) {
      isFunction = true;
    }
    

    wend = 0;
    for (int i = winfdefine.indexOf("define global "); i != -1; i = winfdefine.indexOf("define global ", wend)) {
      wend = winfdefine.indexOf(" ", i + 14);
      if (wend == -1) break;
      String wglobalvar = winfdefine.substring(i + 14, wend).trim();
      
      winfdefine = winfdefine.substring(0, i) + "-- " + winfdefine.substring(i);
      
      waux = 0;
      for (int j = winfbody.indexOf(wglobalvar); j != -1; j = winfbody.indexOf(wglobalvar, waux)) {
        waux2 = j + wglobalvar.length();
        char ch = winfbody.charAt(waux2);
        char ch0 = winfbody.charAt(j - 1);
        if (((!Character.isLetterOrDigit(ch)) && (ch != '_') && (ch != '.')) || ((!Character.isLetterOrDigit(ch0)) && (ch0 != '_') && (ch0 != '.')))
        {
          if (waux2 != -1) {
            winfbody = winfbody.substring(0, j) + "dbo." + winfbody.substring(j, waux2) + "()" + winfbody.substring(waux2);
          } else {
            winfbody = winfbody.substring(0, j) + "dbo." + winfbody.substring(j);
          }
        }
        waux = winfbody.indexOf(wglobalvar, j) + 1;
      }
    }
    



    wend = 0;
    String wiob = " ";String wcol = " ";String wtype = " ";String wcari = " ";String wdeci = " ";String wdef = " ";
    for (int i = winfdefine.indexOf("like "); i != -1; i = winfdefine.indexOf("like ", wend)) {
      wend = winfdefine.indexOf(";", i);
      if (wend == -1) break;
      wiob = winfdefine.substring(i + 5, i + 9).toUpperCase();
      wcol = winfdefine.substring(i + 12, wend).toLowerCase();
      wtype = MUtil.getSQLValue("select c_te from mrfct0 where i_ob = '" + wiob + "' and i_comp = '" + wcol + "' ");
      wcari = MUtil.getSQLValue("select q_cari from mrfct0 where i_ob = '" + wiob + "' and i_comp = '" + wcol + "' ");
      if (!wtype.toUpperCase().equals("N")) {
        wdef = " char(" + wcari + ")";
      } else {
        wdeci = MUtil.getSQLValue("select q_deci from mrfct0 where i_ob = '" + wiob + "' and i_comp = '" + wcol + "' ");
        wdef = " decimal(" + wcari + "," + wdeci + ")";
      }
      
      winfdefine = winfdefine.substring(0, i) + wdef + winfdefine.substring(wend);
    }
    



    winfbody = MUtil.stringRep(winfbody, "nvl(", "isnull(");
    winfbody = MUtil.stringRep(winfbody, "||", "+");
    winfbody = MUtil.stringRep(winfbody, "end foreach", "end loop");
    winfbody = MUtil.stringRep(winfbody, "--#SQL ", " ");
    int wcurscount = 0;
    String winto = "";
    String wcurs = "";
    wend = 0;
    for (int i = winfbody.indexOf("foreach"); i != -1; i = winfbody.indexOf("foreach", wend)) {
      wend = winfbody.indexOf("-- end declare cursor", i);
      if (wend == -1) break;
      wcurscount += 1;
      waux = winfbody.indexOf("into ", i);
      waux2 = winfbody.indexOf("from ", i);
      winto = winfbody.substring(waux, waux2 - 1);
      wcurs = winfbody.substring(i + 8, waux) + winfbody.substring(waux2, wend - 1);
      wcurs = MUtil.stringRep(wcurs, "select", "!zelect");
      waux3 = winfdefine.lastIndexOf(";");
      
      winfbody = winfbody.substring(0, i) + EOL + " declare curs" + wcurscount + " cursor local FAST_FORWARD !zor " + EOL + wcurs + EOL + "  OPEN curs" + wcurscount + ";" + EOL + "  SET @CONTROLFECTH=0 " + EOL + "  WHILE @CONTROLFECTH=0 " + EOL + "  BEGIN " + EOL + "  FETCH curs" + wcurscount + " " + winto + ";" + EOL + "  SET @CONTROLFECTH=@@FETCH_STATUS IF @CONTROLFECTH <> 0 BREAK" + EOL + winfbody.substring(wend);
      
















      wend = winfbody.indexOf("-- end declare cursor", i);
      waux2 = winfbody.indexOf("end loop", wend);
      System.out.println("wend:" + wend + " waux2:" + waux2);
      if (waux2 != -1) {
        winfbody = winfbody.substring(0, waux2 + 8) + EOL + "  CLOSE curs" + wcurscount + "; DEALLOCATE curs" + wcurscount + " ;" + EOL + winfbody.substring(waux2 + 8);
      }
    }
    










    wend = 0;
    for (int i = winfbody.indexOf("select"); i != -1; i = winfbody.indexOf("select", wend)) {
      waux = winfbody.indexOf("into", i);
      wend = winfbody.indexOf("from", i);
      if ((waux != -1) && (waux < wend)) {
        waux2 = winfbody.substring(i, wend).indexOf("count(*) into wcount");
        if (waux2 == -1) {
          waux2 = winfbody.substring(i, wend).indexOf("max(zk) into wzk");
        }
        if (wend == -1) break;
        if (waux2 == -1) {
          wini = winfbody.indexOf("--SEL", wend);
          waux = winfbody.indexOf("SELEND", wini);
          if ((waux != -1) && (waux > wend)) {
            winfbody = winfbody.substring(0, i + 6) + winfbody.substring(wini + 5, waux) + winfbody.substring(wend, wini) + winfbody.substring(waux + 6);
          }
        }
      }
    }
    





    wend = 0;
    for (int i = winfbody.indexOf("update"); i != -1; i = winfbody.indexOf("update", wend)) {
      waux = winfbody.indexOf("set", i);
      wend = waux + 3;
      if (waux == -1) break;
      winfbody = winfbody.substring(0, waux) + " WITH (ROWLOCK) " + winfbody.substring(waux);
    }
    





    wend = 0;
    for (int i = winfbody.indexOf("delete from"); i != -1; i = winfbody.indexOf("delete from", wend)) {
      waux = winfbody.indexOf("where", i);
      wend = waux + 3;
      if (waux == -1) break;
      winfbody = winfbody.substring(0, waux) + " WITH (ROWLOCK) " + winfbody.substring(waux);
    }
    





    winfdefine = MUtil.stringRep(winfdefine, "define ", "declare @");
    winfini = woraddl.indexOf("SQL_DEFINE_BEGIN");
    winfdefvars = woraddl.substring(0, winfini) + winfdefine;
    




    winfbody = MUtil.stringRep(winfbody, "let ", "set ");
    winfbody = MUtil.stringRep(winfbody, "then", "BEGIN");
    winfbody = MUtil.stringRep(winfbody, "end if", "END");
    winfbody = MUtil.stringRep(winfbody, "call ", "execute ");
    winfbody = MUtil.stringRep(winfbody, "count(*) into wcount", "wcount = count(*)");
    winfbody = MUtil.stringRep(winfbody, "max(zk) into wzk", "wzk = max(zk)");
    

    wend = 0;
    for (int i = winfbody.indexOf("select "); i != -1; i = winfbody.indexOf("select ", wend))
    {
      for (wend = winfbody.indexOf(";", i); (winfbody.substring(wend + 1, wend + 2).equals("%")) || (winfbody.substring(wend + 1, wend + 2).equals("'")) || ((winfbody.substring(wend + 1, wend + 2).equals("\"")) && (wend != -1)); wend = winfbody.indexOf(";", wend + 1)) {}
      
      if (wend == -1) break;
      boolean inSql = false;
      waux = winfbody.lastIndexOf(";", i);
      waux2 = winfbody.lastIndexOf("insert", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("update", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("delete", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("select", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      if (!inSql) {
        winfbody = winfbody.substring(0, i) + "  set wecode = 0;" + EOL + "  BEGIN" + EOL + winfbody.substring(i, wend + 1) + EOL + "  IF @@ERROR <> 0  " + EOL + "      set wecode = @@ERROR ;" + EOL + "  END;" + winfbody.substring(wend + 1);
        









        wend = winfbody.indexOf("END;", i);
      }
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("date("); i != -1; i = winfbody.indexOf("date(", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_date(" + winfbody.substring(i + 5, wend) + ", 'YYYY-MM-DD'" + winfbody.substring(wend);
    }
    



    wend = 0;
    for (int i = winfbody.indexOf("let "); i != -1; i = winfbody.indexOf("let ", wend)) {
      wend = winfbody.indexOf("=", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, wend) + " " + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("tochar("); i != -1; i = winfbody.indexOf("tochar(", wend)) {
      wend = winfbody.indexOf("(", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_char" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("tonumber("); i != -1; i = winfbody.indexOf("tonumber(", wend)) {
      wend = winfbody.indexOf("(", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_number" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("return "); i != -1; i = winfbody.indexOf("return ", wend)) {
      wend = winfbody.indexOf(";", i);
      if (wend == -1) break;
      if (isFunction) {
        winfbody = winfbody.substring(0, i) + "return (" + winfbody.substring(i + 7, wend) + ")" + winfbody.substring(wend);
      }
      else if (winfbody.substring(i, wend).indexOf("zctl", 0) > 0) {
        winfbody = winfbody.substring(0, i) + " select @zctl,@zcode,@zmsg; return " + winfbody.substring(wend);
        wend += 10;
      }
      else if (winfbody.substring(i, wend).indexOf("z", 0) > 0) {
        winfbody = winfbody.substring(0, i) + "select " + winfbody.substring(i + 7);
      } else {
        winfbody = winfbody.substring(0, i) + " " + winfbody.substring(wend);
      }
    }
    





    wend = 0;
    for (int i = winfbody.indexOf("call MINVARIABLES."); i != -1; i = winfbody.indexOf("call MINVARIABLES.", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + winfbody.substring(i + 18);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("execute "); i != -1; i = winfbody.indexOf("execute ", wend)) {
      wini = winfbody.indexOf("(", i);
      wend = winfbody.indexOf(")", i);
      waux = winfbody.indexOf(";", i);
      if (wend == -1) break;
      if ((wini != -1) && (waux != -1) && (wend > wini + 1)) {
        winfbody = winfbody.substring(0, wini) + " " + winfbody.substring(wini + 1, wend) + " " + winfbody.substring(waux);
      }
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("dbinfo(\"sqlca.sqlerrd2\")"); i != -1; i = winfbody.indexOf("dbinfo(\"sqlca.sqlerrd2\")", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "@WROWCOUNT" + winfbody.substring(i + 24);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("dbinfo('sqlca.sqlerrd2')"); i != -1; i = winfbody.indexOf("dbinfo('sqlca.sqlerrd2')", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "@WROWCOUNT" + winfbody.substring(i + 24);
    }
    


    winfbody = MUtil.stringRep(winfbody, "end for", "END");
    winfbody = MUtil.stringRep(winfbody, "end while", "END");
    winfbody = MUtil.stringRep(winfbody, "exit for", "break");
    
    wend = 0;
    for (int i = winfbody.indexOf("while "); i != -1; i = winfbody.indexOf("while ", wend)) {
      wend = winfbody.indexOf(EOL, i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, wend) + " loop " + winfbody.substring(wend);
    }
    



    wend = 0;
    for (int i = winfbody.indexOf("for "); i != -1; i = winfbody.indexOf("for ", wend)) {
      wend = winfbody.indexOf(" to ", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "set i = 0; WHILE i + 1 <= " + winfbody.substring(wend + 4);
      

      wend = winfbody.indexOf(EOL, i);
      winfbody = winfbody.substring(0, wend) + " BEGIN set i= i + 1" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("end loop"); i != -1; i = winfbody.indexOf("end loop", wend)) {
      wend = winfbody.indexOf(EOL, i);
      if (wend == -1) break;
      waux = winfbody.indexOf(";", i);
      if ((waux == -1) || (waux > wend)) {
        winfbody = winfbody.substring(0, wend) + ";" + winfbody.substring(wend);
      }
    }
    



    wend = 0;
    



    for (int i = winfbody.indexOf("["); i != -1; i = winfbody.indexOf("[", wend)) {
      boolean inStr = false;
      for (int j = i - 1; j > 0; j--) {
        char ch = winfbody.charAt(j);
        if ((ch == '\r') || (ch == '\n')) {
          break;
        }
        if ((ch == '\'') || (ch == '"')) {
          inStr = !inStr;
        }
      }
      if (inStr) {
        i++;wend = i;
      }
      else {
        wend = winfbody.indexOf("]", i);
        if (wend == -1) break;
        waux = winfbody.lastIndexOf(" ", i);
        waux = Math.max(waux, winfbody.lastIndexOf(";", i));
        waux = Math.max(waux, winfbody.lastIndexOf(")", i));
        waux = Math.max(waux, winfbody.lastIndexOf(",", i));
        waux = Math.max(waux, winfbody.lastIndexOf("=", i));
        waux = Math.max(waux, winfbody.lastIndexOf("<", i));
        waux = Math.max(waux, winfbody.lastIndexOf(">", i));
        waux = Math.max(waux, winfbody.lastIndexOf("EOL", i));
        
        String[] wparameters = MUtil.getArgs(winfbody.substring(i + 1, wend), ',');
        
        if ((wparameters.length == 0) || (wparameters.length > 2))
          throw new MError("Parametros da substring errados");
        int wsend;
        int wsini; if (wparameters.length == 1) {
          int wsini = Integer.valueOf(wparameters[0].trim()).intValue();
          wsend = wsini;
        } else {
          wsini = Integer.valueOf(wparameters[0].trim()).intValue();
          wsend = Integer.valueOf(wparameters[1].trim()).intValue();
        }
        String wvar = winfbody.substring(waux + 1, i);
        int wsend = wsend - wsini + 1;
        winfbody = winfbody.substring(0, waux + 1) + "substr(" + wvar + "," + wsini + "," + wsend + ")" + winfbody.substring(wend + 1);
      }
    }
    

    wend = 0;
    for (int i = winfbody.indexOf("raise exception "); i != -1; i = winfbody.indexOf("raise exception ", wend)) {
      wend = winfbody.indexOf(";", i);
      if (wend == -1) break;
      waux = winfbody.indexOf(",", i);
      waux2 = winfbody.indexOf(",", waux + 1);
      waux3 = winfbody.indexOf("\"", waux2);
      if ((waux != -1) && (waux2 != -1) && (waux3 != -1) && (waux3 < wend)) {
        String ws = "RAISE_APPLICATION_ERROR(-20746," + winfbody.substring(waux3, wend) + ")";
        winfbody = winfbody.substring(0, i) + ws + winfbody.substring(wend);
      }
    }
    



    winfbody = MUtil.stringRep(winfbody, "today", "convert(char(10),getdate(),126)");
    winfbody = MUtil.stringRep(winfbody, "current hour to second", "substr(convert(char(20),getdate(),126),12,8)");
    winfbody = MUtil.stringRep(winfbody, "\"'\"", "\"''\"");
    winfbody = winfbody.replace('"', '\'');
    winfdefine = MUtil.stringRep(winfdefine, "today", "convert(char(10),getdate(),126)");
    winfdefine = MUtil.stringRep(winfdefine, "current hour to second", "substring(convert(char(20),getdate(),126),12,8)");
    
    winfdefine = MUtil.stringRep(winfdefine, "\"'\"", "\"''\"");
    winfdefine = winfdefine.replace('"', '\'');
    

    String wvardef = " ";String wold = " ";String wnew = " ";
    char chbf = ' ';char chaf = ' ';
    for (int i = winfdefvars.indexOf("@"); i != -1; i = winfdefvars.indexOf("@", wend)) {
      wend = winfdefvars.indexOf(" ", i);
      if (wend == -1) break;
      wvardef = winfdefvars.substring(i, wend).trim();
      wold = wvardef.substring(1);
      wnew = wvardef;
      for (int j = winfbody.indexOf(wold); j != -1; j = winfbody.indexOf(wold, j + wnew.length())) {
        chbf = winfbody.charAt(j - 1);
        chaf = winfbody.charAt(j + wold.length());
        if ((!Character.isLetterOrDigit(chaf)) && (!Character.isLetterOrDigit(chbf)))
        {
          winfbody = winfbody.substring(0, j) + wnew + winfbody.substring(j + wold.length(), winfbody.length());
        }
      }
    }
    







    winfbody = MUtil.stringRep(winfbody, "@@", "@");
    winfbody = MUtil.stringRep(winfbody, "@ERROR", "@@ERROR");
    winfbody = MUtil.stringRep(winfbody, "@ROWCOUNT", "@@ROWCOUNT");
    winfbody = MUtil.stringRep(winfbody, "@FETCH_STATUS", "@@FETCH_STATUS");
    winfbody = MUtil.stringRep(winfbody, "else", "END else");
    winfbody = MUtil.stringRep(winfbody, "end loop", "END");
    winfbody = MUtil.stringRep(winfbody, "cmctpkx(", "dbo.cmctpkx(");
    winfbody = MUtil.stringRep(winfbody, "minexer(", "dbo.minexer(");
    winfbody = MUtil.stringRep(winfbody, "miniipt(", "dbo.miniipt(");
    winfbody = MUtil.stringRep(winfbody, "minmimp(", "dbo.minmimp(");
    

    winfbody = MUtil.stringRep(winfbody, "trim(", "rtrim(");
    winfbody = MUtil.stringRep(winfbody, "minicon2(", "dbo.minicon2(");
    winfbody = MUtil.stringRep(winfbody, "miniconi(", "dbo.miniconi(");
    winfbody = MUtil.stringRep(winfbody, "miniconf(", "dbo.miniconf(");
    winfbody = MUtil.stringRep(winfbody, "minipst(", "dbo.minipst(");
    winfbody = MUtil.stringRep(winfbody, "minmcred(", "dbo.minmcred(");
    winfbody = MUtil.stringRep(winfbody, "minmdeb(", "dbo.minmdeb(");
    winfbody = MUtil.stringRep(winfbody, "!zelect", "select");
    winfbody = MUtil.stringRep(winfbody, "!zor", "for");
    winfbody = MUtil.stringRep(winfbody, "dbo.dbo.", "dbo.");
    
    woraini = wddl.indexOf("SQL_DEFINE_BEGIN");
    woraend = wddl.indexOf("SQL_DEFINE_END");
    if ((woraini != -1) && (woraend != -1)) {
      wddl = wddl.substring(0, woraini) + MUtil.stringRep(winfdefine, "INF_DEFINE", "SQL_DEFINE") + wddl.substring(woraend);
    } else {
      throw new MError("MIN-M00206E", new String[] { wobjtype, wobjname });
    }
    
    woraini = wddl.indexOf("SQL_BODY_BEGIN");
    woraend = wddl.indexOf("SQL_BODY_END");
    if ((woraini != -1) && (woraend != -1)) {
      wddl = wddl.substring(0, woraini) + MUtil.stringRep(winfbody, "INF_BODY", "SQL_BODY") + wddl.substring(woraend);
    } else {
      throw new MError("MIN-M00206E", new String[] { wobjtype, wobjname });
    }
    
    wend = 0;
    for (int i = wddl.indexOf("--SQL_EXCLUDE_BEGIN"); i != -1; i = wddl.indexOf("--SQL_EXCLUDE_BEGIN", wend)) {
      wend = wddl.indexOf("--SQL_EXCLUDE_END", i);
      if (wend == -1) break;
      wddl = wddl.substring(0, i) + wddl.substring(wend);
    }
    



    return wddl;
  }
  
  private String changeProcPgl(String wobjtype, String wobjname, String winfddl, String woraddl) throws MError {
    String wddl = woraddl;
    String winfdefvars = "";
    String winfdefine = "";
    String winfbody = "";
    int winfini = 0;
    int winfend = 0;
    int woraini = 0;
    int woraend = 0;
    int wini = 0;
    int wend = 0;
    int waux = 0;
    int waux2 = 0;
    int waux3 = 0;
    boolean isFunction = false;
    

    wend = 0;
    for (int i = winfddl.indexOf("--SQL_EXCLUDE_BEGIN"); i != -1; i = winfddl.indexOf("--SQL_EXCLUDE_BEGIN", wend)) {
      wend = winfddl.indexOf("--SQL_EXCLUDE_END", i);
      if (wend == -1) break;
      winfddl = winfddl.substring(0, i) + winfddl.substring(wend);
    }
    


    wend = 0;
    for (int i = winfddl.indexOf("--PGL_EXCLUDE_BEGIN"); i != -1; i = winfddl.indexOf("--PGL_EXCLUDE_BEGIN", wend)) {
      wend = winfddl.indexOf("--PGL_EXCLUDE_END", i);
      if (wend == -1) break;
      winfddl = winfddl.substring(0, i) + winfddl.substring(wend);
    }
    



    winfini = winfddl.indexOf("INF_DEFINE_BEGIN");
    winfend = winfddl.indexOf("INF_DEFINE_END");
    if ((winfini != -1) && (winfend != -1)) {
      winfdefine = winfddl.substring(winfini, winfend);
      
      winfdefine = winfdefine.substring(0, 16) + EOL + "  DECLARE " + EOL + "  executeX int4;" + EOL + "  wrowcount integer;" + EOL + "  wrow record;" + winfdefine.substring(16);
    } else {
      throw new MError("MIN-M00204E", new String[] { wobjtype, wobjname });
    }
    winfini = winfddl.indexOf("INF_BODY_BEGIN");
    winfend = winfddl.indexOf("INF_BODY_END");
    if ((winfini != -1) && (winfend != -1)) {
      winfbody = winfddl.substring(winfini, winfend);
    } else {
      throw new MError("MIN-M00204E", new String[] { wobjtype, wobjname });
    }
    
    waux = woraddl.indexOf(" function ");
    if (waux != -1) {
      isFunction = true;
    }
    waux = woraddl.indexOf(" FUNCTION ");
    if (waux != -1) {
      isFunction = true;
    }
    

    wend = 0;
    for (int i = winfdefine.indexOf("define global "); i != -1; i = winfdefine.indexOf("define global ", wend)) {
      wend = winfdefine.indexOf(" ", i + 14);
      if (wend == -1) break;
      String wglobalvar = winfdefine.substring(i + 14, wend).trim();
      
      winfdefine = winfdefine.substring(0, i) + "-- " + winfdefine.substring(i);
      
      waux = 0;
      for (int j = winfbody.indexOf(wglobalvar); j != -1; j = winfbody.indexOf(wglobalvar, waux)) {
        waux2 = j + wglobalvar.length();
        char ch = winfbody.charAt(waux2);
        char ch0 = winfbody.charAt(j - 1);
        if (((!Character.isLetterOrDigit(ch)) && (ch != '_') && (ch != '.')) || ((!Character.isLetterOrDigit(ch0)) && (ch0 != '_') && (ch0 != '.')))
        {
          if (waux2 != -1) {
            winfbody = winfbody.substring(0, j) + " " + winfbody.substring(j, waux2) + "()" + winfbody.substring(waux2);
          } else {
            winfbody = winfbody.substring(0, j) + " " + winfbody.substring(j);
          }
        }
        waux = winfbody.indexOf(wglobalvar, j) + 1;
      }
    }
    



    wend = 0;
    String wiob = " ";String wcol = " ";String wtype = " ";String wcari = " ";String wdeci = " ";String wdef = " ";
    for (int i = winfdefine.indexOf("like "); i != -1; i = winfdefine.indexOf("like ", wend)) {
      wend = winfdefine.indexOf(";", i);
      if (wend == -1) break;
      wiob = winfdefine.substring(i + 5, i + 9).toUpperCase();
      wcol = winfdefine.substring(i + 12, wend).toLowerCase();
      wtype = MUtil.getSQLValue("select c_te from mrfct0 where i_ob = '" + wiob + "' and i_comp = '" + wcol + "' ");
      wcari = MUtil.getSQLValue("select q_cari from mrfct0 where i_ob = '" + wiob + "' and i_comp = '" + wcol + "' ");
      if (!wtype.toUpperCase().equals("N")) {
        wdef = " char(" + wcari + ")";
      } else {
        wdeci = MUtil.getSQLValue("select q_deci from mrfct0 where i_ob = '" + wiob + "' and i_comp = '" + wcol + "' ");
        wdef = " decimal(" + wcari + "," + wdeci + ")";
      }
      
      winfdefine = winfdefine.substring(0, i) + wdef + winfdefine.substring(wend);
    }
    



    if (wobjname.substring(4, 6).equals("p0")) {
      winfbody = MUtil.stringRep(winfbody, "zfase", "tg_op");
    }
    winfbody = MUtil.stringRep(winfbody, "\"RD\"", "'DELETE\"");
    winfbody = MUtil.stringRep(winfbody, "\"RI\"", "'INSERT\"");
    winfbody = MUtil.stringRep(winfbody, "\"RU\"", "'UPDATE\"");
    winfbody = MUtil.stringRep(winfbody, "'RD'", "'DELETE'");
    winfbody = MUtil.stringRep(winfbody, "'RI'", "'INSERT'");
    winfbody = MUtil.stringRep(winfbody, "'RU'", "'UPDATE'");
    


    winfbody = MUtil.stringRep(winfbody, "end foreach", "end loop");
    winfbody = MUtil.stringRep(winfbody, "--#PGL ", " ");
    int wcurscount = 0;
    String winto = "";
    String wcurs = "";
    wend = 0;
    
    winfdefine = MUtil.stringRep(winfdefine, "define ", "");
    winfini = woraddl.indexOf("PGL_DEFINE_BEGIN");
    winfdefvars = woraddl.substring(0, winfini) + winfdefine;
    




    winfbody = MUtil.stringRep(winfbody, "let ", "");
    
    winfbody = MUtil.stringRep(winfbody, "end if", "end if;");
    winfbody = MUtil.stringRep(winfbody, ";;", ";");
    winfbody = MUtil.stringRep(winfbody, "call ", "execute ");
    



    wend = 0;
    for (int i = winfbody.indexOf("select "); i != -1; i = winfbody.indexOf("select ", wend))
    {
      for (wend = winfbody.indexOf(";", i); (winfbody.substring(wend + 1, wend + 2).equals("%")) || (winfbody.substring(wend + 1, wend + 2).equals("'")) || ((winfbody.substring(wend + 1, wend + 2).equals("\"")) && (wend != -1)); wend = winfbody.indexOf(";", wend + 1)) {}
      
      if (wend == -1) break;
      boolean inSql = false;
      waux = winfbody.lastIndexOf(";", i);
      waux2 = winfbody.lastIndexOf("insert", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("update", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("delete", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      waux2 = winfbody.lastIndexOf("select", i - 1);
      if (waux2 > waux) {
        inSql = true;
      }
      if (inSql) {}
    }
    

















    wend = 0;
    for (int i = winfbody.indexOf("date("); i != -1; i = winfbody.indexOf("date(", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_date(" + winfbody.substring(i + 5, wend) + ", 'YYYY-MM-DD'" + winfbody.substring(wend);
    }
    



    wend = 0;
    for (int i = winfbody.indexOf("let "); i != -1; i = winfbody.indexOf("let ", wend)) {
      wend = winfbody.indexOf("=", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, wend) + " " + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("tochar("); i != -1; i = winfbody.indexOf("tochar(", wend)) {
      wend = winfbody.indexOf("(", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_char" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("tonumber("); i != -1; i = winfbody.indexOf("tonumber(", wend)) {
      wend = winfbody.indexOf("(", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "to_number" + winfbody.substring(wend);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("return "); i != -1; i = winfbody.indexOf("return ", wend)) {
      wend = winfbody.indexOf(";", i);
      if (wend == -1) break;
      if (isFunction) {
        winfbody = winfbody.substring(0, i) + "return (" + winfbody.substring(i + 7, wend) + ")" + winfbody.substring(wend);
      }
      else if (winfbody.substring(i, wend).indexOf("zctl", 0) > 0) {
        winfbody = winfbody.substring(0, i) + " select @zctl,@zcode,@zmsg; return " + winfbody.substring(wend);
        wend += 10;
      }
      else if (winfbody.substring(i, wend).indexOf("z", 0) > 0) {
        winfbody = winfbody.substring(0, i) + "select " + winfbody.substring(i + 7);
      } else {
        winfbody = winfbody.substring(0, i) + " " + winfbody.substring(wend);
      }
    }
    





    wend = 0;
    for (int i = winfbody.indexOf("call MINVARIABLES."); i != -1; i = winfbody.indexOf("call MINVARIABLES.", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + winfbody.substring(i + 18);
    }
    


    wend = 0;
    wend = 0;
    for (int i = winfbody.indexOf("dbinfo(\"sqlca.sqlerrd2\")"); i != -1; i = winfbody.indexOf("dbinfo(\"sqlca.sqlerrd2\")", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "wrowcount" + winfbody.substring(i + 24);
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("dbinfo('sqlca.sqlerrd2')"); i != -1; i = winfbody.indexOf("dbinfo('sqlca.sqlerrd2')", wend)) {
      wend = winfbody.indexOf(")", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "wrowcount" + winfbody.substring(i + 24);
    }
    


    winfbody = MUtil.stringRep(winfbody, "end for", "END");
    winfbody = MUtil.stringRep(winfbody, "end while", "END");
    winfbody = MUtil.stringRep(winfbody, "exit for", "break");
    
    wend = 0;
    for (int i = winfbody.indexOf("while "); i != -1; i = winfbody.indexOf("while ", wend)) {
      wend = winfbody.indexOf(EOL, i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, wend) + " loop " + winfbody.substring(wend);
    }
    



    wend = 0;
    

    for (int i = winfbody.indexOf("for "); i != -1; i = winfbody.indexOf("for ", wend)) {
      wend = winfbody.indexOf(" to ", i);
      if (wend == -1) break;
      winfbody = winfbody.substring(0, i) + "i = 0; WHILE i + 1 <= " + winfbody.substring(wend + 4);
      

      wend = winfbody.indexOf(EOL, i);
      String ww = winfbody.substring(wend);
      int k = ww.indexOf("END");
      if (k > 0) {
        ww = ww.substring(0, k) + "END LOOP;" + ww.substring(k + 3);
      }
      winfbody = winfbody.substring(0, wend) + " LOOP i= i + 1;" + ww;
    }
    


    wend = 0;
    for (int i = winfbody.indexOf("end loop"); i != -1; i = winfbody.indexOf("end loop", wend)) {
      wend = winfbody.indexOf(EOL, i);
      if (wend == -1) break;
      waux = winfbody.indexOf(";", i);
      if ((waux == -1) || (waux > wend)) {
        winfbody = winfbody.substring(0, wend) + ";" + winfbody.substring(wend);
      }
    }
    



    wend = 0;
    



    for (int i = winfbody.indexOf("["); i != -1; i = winfbody.indexOf("[", wend)) {
      boolean inStr = false;
      for (int j = i - 1; j > 0; j--) {
        char ch = winfbody.charAt(j);
        if ((ch == '\r') || (ch == '\n')) {
          break;
        }
        if ((ch == '\'') || (ch == '"')) {
          inStr = !inStr;
        }
      }
      if (inStr) {
        i++;wend = i;
      }
      else {
        wend = winfbody.indexOf("]", i);
        if (wend == -1) break;
        waux = winfbody.lastIndexOf(" ", i);
        waux = Math.max(waux, winfbody.lastIndexOf(";", i));
        waux = Math.max(waux, winfbody.lastIndexOf(")", i));
        waux = Math.max(waux, winfbody.lastIndexOf(",", i));
        waux = Math.max(waux, winfbody.lastIndexOf("=", i));
        waux = Math.max(waux, winfbody.lastIndexOf("<", i));
        waux = Math.max(waux, winfbody.lastIndexOf(">", i));
        waux = Math.max(waux, winfbody.lastIndexOf("EOL", i));
        
        String[] wparameters = MUtil.getArgs(winfbody.substring(i + 1, wend), ',');
        
        if ((wparameters.length == 0) || (wparameters.length > 2))
          throw new MError("Parametros da substring errados");
        int wsend;
        int wsini; if (wparameters.length == 1) {
          int wsini = Integer.valueOf(wparameters[0].trim()).intValue();
          wsend = wsini;
        } else {
          wsini = Integer.valueOf(wparameters[0].trim()).intValue();
          wsend = Integer.valueOf(wparameters[1].trim()).intValue();
        }
        String wvar = winfbody.substring(waux + 1, i);
        int wsend = wsend - wsini + 1;
        winfbody = winfbody.substring(0, waux + 1) + "substr(" + wvar + "," + wsini + "," + wsend + ")" + winfbody.substring(wend + 1);
      }
    }
    

    wend = 0;
    for (int i = winfbody.indexOf("raise exception "); i != -1; i = winfbody.indexOf("raise exception ", wend)) {
      wend = winfbody.indexOf(";", i);
      if (wend == -1) break;
      waux = winfbody.indexOf(",", i);
      waux2 = winfbody.indexOf(",", waux + 1);
      waux3 = winfbody.indexOf("\"", waux2);
      if ((waux != -1) && (waux2 != -1) && (waux3 != -1) && (waux3 < wend)) {
        String ws = "RAISE_APPLICATION_ERROR(-20746," + winfbody.substring(waux3, wend) + ")";
        winfbody = winfbody.substring(0, i) + ws + winfbody.substring(wend);
      }
    }
    




    winfbody = MUtil.stringRep(winfbody, "today", "current_date");
    winfbody = MUtil.stringRep(winfbody, "current hour to second", "substring(current_time,1,8)");
    winfbody = MUtil.stringRep(winfbody, "\"'\"", "\"''\"");
    winfbody = winfbody.replace('"', '\'');
    winfdefine = MUtil.stringRep(winfdefine, "today", "current_date");
    winfdefine = MUtil.stringRep(winfdefine, "current hour to second", "substring(current_time,1,8)");
    




    System.out.println("Substituicoes de vars 1: |" + winfdefvars + "|");
    String wvardef = " ";String wold = " ";String wnew = " ";
    char chbf = ' ';char chaf = ' ';
    for (int i = winfdefvars.indexOf("@"); i != -1; i = winfdefvars.indexOf("@", wend)) {
      wend = winfdefvars.indexOf(" ", i);
      if (wend == -1) break;
      wvardef = winfdefvars.substring(i, wend).trim();
      wold = wvardef.substring(1);
      wnew = wvardef;
      for (int j = winfbody.indexOf(wold); j != -1; j = winfbody.indexOf(wold, j + wnew.length())) {
        chbf = winfbody.charAt(j - 1);
        chaf = winfbody.charAt(j + wold.length());
        if ((!Character.isLetterOrDigit(chaf)) && (!Character.isLetterOrDigit(chbf)))
        {
          winfbody = winfbody.substring(0, j) + wnew + winfbody.substring(j + wold.length(), winfbody.length());
        }
      }
    }
    







    winfbody = MUtil.stringRep(winfbody, "--#SQL set @WROWCOUNT = @@ROWCOUNT", "GET DIAGNOSTICS wrowcount = ROW_COUNT;");
    
    winfbody = MUtil.stringRep(winfbody, "'", "\\'");
    winfbody = MUtil.stringRep(winfbody, "\"", "\\'");
    System.out.println("!*** SDOB Ojname: " + wobjname);
    if (wobjname.substring(4, 6).equals("p0")) {
      winfbody = MUtil.stringRep(winfbody, "=x", "=old.");
      winfbody = MUtil.stringRep(winfbody, "|x", "|old.");
      winfbody = MUtil.stringRep(winfbody, ">x", ">old.");
      winfbody = MUtil.stringRep(winfbody, "<x", "<old.");
      winfbody = MUtil.stringRep(winfbody, "-x", "-old.");
      winfbody = MUtil.stringRep(winfbody, "+x", "+old.");
      winfbody = MUtil.stringRep(winfbody, "(x", "(old.");
      winfbody = MUtil.stringRep(winfbody, " x", " old.");
      winfbody = MUtil.stringRep(winfbody, "=y", "=new.");
      winfbody = MUtil.stringRep(winfbody, "|y", "|new.");
      winfbody = MUtil.stringRep(winfbody, ">y", ">new.");
      winfbody = MUtil.stringRep(winfbody, "<y", "<new.");
      winfbody = MUtil.stringRep(winfbody, "-y", "-new.");
      winfbody = MUtil.stringRep(winfbody, "+y", "+new.");
      winfbody = MUtil.stringRep(winfbody, "(y", "(new.");
      winfbody = MUtil.stringRep(winfbody, ",y", ",new.");
      winfbody = MUtil.stringRep(winfbody, " y", " new.");
      winfbody = MUtil.stringRep(winfbody, "!x", "x");
      winfbody = MUtil.stringRep(winfbody, "!y", "y");
      winfbody = MUtil.stringRep(winfbody, "return;", "return new;");
      winfbody = MUtil.stringRep(winfbody, "return (old)", "return old");
    }
    winfbody = MUtil.stringRep(winfbody, "!y", "y");
    winfbody = MUtil.stringRep(winfbody, " returning zctl, zcode, zmsg", "");
    winfbody = MUtil.stringRep(winfbody, "return zctl, zcode, zmsg;", "return 0;");
    winfbody = MUtil.stringRep(winfbody, "return (zctl, zcode, zmsg);", "return 0;");
    winfbody = MUtil.stringRep(winfbody, " return;", " return 0;");
    winfbody = MUtil.stringRep(winfbody, "execute ", "executeX = ");
    

    woraini = wddl.indexOf("PGL_DEFINE_BEGIN");
    woraend = wddl.indexOf("PGL_DEFINE_END");
    if ((woraini != -1) && (woraend != -1)) {
      wddl = wddl.substring(0, woraini) + MUtil.stringRep(winfdefine, "INF_DEFINE", "PGL_DEFINE") + wddl.substring(woraend);
    } else {
      throw new MError("MIN-M00207E", new String[] { wobjtype, wobjname });
    }
    
    woraini = wddl.indexOf("PGL_BODY_BEGIN");
    woraend = wddl.indexOf("PGL_BODY_END");
    if ((woraini != -1) && (woraend != -1)) {
      wddl = wddl.substring(0, woraini) + MUtil.stringRep(winfbody, "INF_BODY", "PGL_BODY") + wddl.substring(woraend);
    } else {
      throw new MError("MIN-M00207E", new String[] { wobjtype, wobjname });
    }
    
    wend = 0;
    for (int i = wddl.indexOf("--SQL_EXCLUDE_BEGIN"); i != -1; i = wddl.indexOf("--SQL_EXCLUDE_BEGIN", wend)) {
      wend = wddl.indexOf("--SQL_EXCLUDE_END", i);
      if (wend == -1) break;
      wddl = wddl.substring(0, i) + wddl.substring(wend);
    }
    


    wend = 0;
    for (int i = wddl.indexOf("--PGL_EXCLUDE_BEGIN"); i != -1; i = wddl.indexOf("--PGL_EXCLUDE_BEGIN", wend)) {
      wend = wddl.indexOf("--PGL_EXCLUDE_END", i);
      if (wend == -1) break;
      wddl = wddl.substring(0, i) + wddl.substring(wend);
    }
    



    if ((wddl.indexOf("update") > -1) || (wddl.indexOf("delete") > -1) || (wddl.indexOf("insert") > -1)) {
      wddl = MUtil.stringRep(wddl, "IMMUTABLE", "VOLATILE");
    }
    return wddl;
  }
  
  public void apllyPatches(String wfase, boolean metafile) throws MError {
    String[] wparms = MUtil.getConnectionParameters();
    MCursor curs0 = MConnection.getMCursor("JMETADATA");
    MCursor curs2 = MConnection.getMCursor(wparms[0], wparms[1], wparms[2]);
    try {
      PT.Minimal.geral.MDBProperties wdbprop = new PT.Minimal.geral.MDBProperties(curs2.getMConnection());
      String wdbtype = (String)wdbprop.get("DATABASE_TYPE");
      if ((wdbtype == null) || (!wdbtype.equals("CLIENT"))) {
        throw new MError("MIN-SDOB12E");
      }
      
      apllyPatches(wfase, curs0, curs2, metafile);
    } finally {
      curs0.close();
      curs2.close();
    }
  }
  

  public void apllyPatches(String wfase, MCursor curs0, MCursor curs2, boolean metafile)
    throws MError
  {
    MRow wrow = PT.Minimal.geral.MMData.getMRow("MJEX");
    String wcsel; if (metafile) {
      wcsel = " select * from siext0";
    } else {
      wcsel = " select * from mjext0";
    }
    String wcsel = wcsel + " where i_ob = 'PATCH' ";
    wcsel = wcsel + " and i_tc = 'PB' ";
    wcsel = wcsel + " and i_fase = '" + wfase + "' ";
    wcsel = wcsel + " and cst = 'E0' ";
    wcsel = wcsel + " order by i_comp ";
    System.out.println(wcsel);
    Vector wv = MUtil.getRows(wrow, wcsel, 999999, curs0);
    apllyPatches(wv, curs0, curs2);
  }
  

  public static void apllyPatches(Vector wv, MCursor curs0, MCursor curs2)
    throws MError
  {
    for (int i = 0; (wv != null) && (i < wv.size()); i++) {
      MRow wyrow = ((MRow)wv.elementAt(i)).Copy();
      
      String witc = (String)wyrow.get("i_tc");
      if (!witc.equals("PB")) {
        throw new MError("Tipo de ficha invalida! TC <> 'PB'");
      }
      try {
        wyrow.insertRow(curs2, false);
        if (wyrow.hasAssociatedObject("ROWTXT", curs0)) {
          wyrow.setAssociatedObject("ROWTXT", wyrow.getAssociatedObject("ROWTXT", curs0), curs2);
        }
        PT.Minimal.pnuts. mpnut = PT.Minimal.pnuts..findMPnut("PATCH");
        mpnut.set("sync_curs", curs2);
        mpnut.set("meta_curs", curs0);
        mpnut.evalPnut((String)wyrow.get("i_ob"), (String)wyrow.get("i_tc"), (String)wyrow.get("i_comp"), (String)wyrow.get("i_fase"));
        mpnut.unset("sync_curs");
        mpnut.unset("meta_curs");
      }
      catch (MError err)
      {
        if ((err.getMinErrorCode() == null) || (!err.getMinErrorCode().equals("MIN-000803E")))
        {
          if ((curs2.supportsTransaction()) && (curs2.inBeginBatch())) {
            curs2.rollbackBatch();
          }
          if ((curs2.supportsTransaction()) && (curs2.inBeginWork())) {
            curs2.rollbackWork();
          }
          wyrow.refreshRow(curs2);
          wyrow.deleteRow(curs2);
          throw err;
        }
      }
    }
  }
  
  public static void apllyPatches(Vector wv) throws MError
  {
    String[] wparms = MUtil.getConnectionParameters();
    MCursor curs0 = MConnection.getMCursor("JMETADATA");
    MCursor curs2 = MConnection.getMCursor(wparms[0], wparms[1], wparms[2]);
    try {
      PT.Minimal.geral.MDBProperties wdbprop = new PT.Minimal.geral.MDBProperties(curs2.getMConnection());
      String wdbtype = (String)wdbprop.get("DATABASE_TYPE");
      if ((wdbtype == null) || (!wdbtype.equals("CLIENT"))) {
        throw new MError("MIN-SDOB12E");
      }
      
      apllyPatches(wv, curs0, curs2);
    } finally {
      curs0.close();
      curs2.close();
    }
  }
  
  public static void apllySCFG(boolean metafile) throws MError
  {
    String[] wparms = MUtil.getConnectionParameters();
    MCursor curs2 = MConnection.getMCursor(wparms[0], wparms[1], wparms[2]);
    try {
      apllySCFG(curs2);
    } finally {
      curs2.close();
    }
  }
  

  public static void apllySCFG(MCursor curs2)
    throws MError
  {
    String wamb = curs2.getMConnection().getAmbient();
    String wmotor = curs2.getMConnection().getEngine();
    


    PT.Minimal.geral.MDBProperties wdbprop = new PT.Minimal.geral.MDBProperties(curs2.getMConnection());
    String wlsync = (String)wdbprop.get("LAST_CLIENT_SYNC");
    String wdbtype = (String)wdbprop.get("DATABASE_TYPE");
    if ((wdbtype == null) || (!wdbtype.equals("CLIENT"))) {
      throw new MError("MIN-SDOB12E");
    }
    
    if (wlsync == null)
      throw new MError("MIN-SDOB15E");
    if (wlsync.trim().length() != 19) {
      throw new MError("MIN-SDOB15E");
    }
    
    String wivrs = wlsync;
    wivrs = MUtil.stringRep(wivrs, "-", "");
    wivrs = MUtil.stringRep(wivrs, ":", "");
    wivrs = MUtil.stringRep(wivrs, " ", "");
    
    if (wmotor.length() > 10) {
      wmotor = wmotor.substring(0, 10);
    }
    if (wivrs.length() > 14) {
      wivrs = wivrs.substring(0, 14);
    }
    
    String wcsel = " update scfgt0 ";
    wcsel = wcsel + " set ";
    wcsel = wcsel + "   cep0 = 'MJV' ";
    wcsel = wcsel + " , cep3 = '03' ";
    wcsel = wcsel + " , cep4 = '11' ";
    wcsel = wcsel + " , xmotor = '" + wmotor.trim() + "' ";
    wcsel = wcsel + " , ivrs = '" + wivrs.trim() + "' ";
    wcsel = wcsel + " , ipsr = 'MJV-'||substr(ipsr,5,7)||'03.11' ";
    try
    {
      int res = curs2.execStatement(wcsel);
      curs2.closeStatement();
      if (res == 0)
      {
        wcsel = " insert into scfgt0 values  (0, 'minimal', '0000', '" + MUtil.getTime() + "', '" + MUtil.getToday() + "', 'ps', 1.0 " + " , ' ', ' ', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S' " + " , 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S' " + " , 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S' " + " , 'S', 'S', 'S', 'S' " + " , 'PTE', 'EUR', 'PTE', 'EUR' " + " , '01-01', '12-31', '08', '06', '06', '08', '06', '06' " + " , ' ', ' ', 'T', 'N', '*', ' ', ' ', ' ', ' ' " + " , '0001-01-01', '9999-12-31' " + " , 'MJV-PT.EMP.02.01', 'MJV', 'MINIMAL/J (JAVA)' " + " , 'PT', 'PORTUGAL', 'EMP', 'EMPRESAS EM GERAL' " + " , '02', '01', '" + wivrs.trim() + "', ' ', ' ', 0, 0, 0 " + " , ' ', ' ', ' ', 0, ' ', ' ', ' ', '" + wmotor.trim() + "') ";
        












        res = curs2.execStatement(wcsel);
        curs2.closeStatement();
        if (res == 0) {
          throw new MError("MIN-SDOB16E");
        }
      }
    } finally {
      curs2.closeStatement();
    }
  }
}
