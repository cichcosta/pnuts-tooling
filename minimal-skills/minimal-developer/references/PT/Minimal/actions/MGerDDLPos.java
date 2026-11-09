package PT.Minimal.actions;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MUtil;
import java.util.Hashtable;
import java.util.Vector;

public class MGerDDLPos extends MGerDDL implements MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  PT.Minimal.geral.MForm minForm;
  Vector wrows;
  String EOL = "\n";
  private String wobj;
  private String wobjname;
  private String[] wargs;
  
  public MGerDDLPos() {}
  
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
  
  public void execute() throws MError
  {}
  
  public String getDDL(PT.Minimal.geral.MRow xsdobt, String wtext) throws MError {
    String wobj = ((String)xsdobt.getField("idob")).toLowerCase();
    String wtype = ((String)xsdobt.getField("idobt")).toLowerCase();
    String wtstorage = ((String)xsdobt.getField("tstorage")).toLowerCase();
    String wcdim = ((String)xsdobt.getField("cdim")).toLowerCase();
    String wstype = wobj.substring(4);
    System.out.println("wstype: " + wstype);
    if ((wstype.equals("t0")) && (wtype.equalsIgnoreCase("TB"))) {
      return get0tableDDL(wobj, wtstorage, wcdim);
    }
    if (wtype.equalsIgnoreCase("GR")) {
      if ((wstype.equals("t0")) || (wstype.equals("tx")) || (wstype.equals("tc")) || (wstype.equals("tw")) || (wstype.equals("szk"))) {
        return getGrantTableDDL(wobj);
      }
      return getGrantProcedureDDL(wobj);
    }
    
    if (wstype.equals("i0")) {
      return get0tableIndex(wobj, wtstorage);
    }
    if (wstype.equals("izk")) {
      return get0tableZKIndex(wobj, wtstorage);
    }
    if (wstype.equals("tx")) {
      return getXtableDDL(wobj, wtstorage);
    }
    if (wstype.equals("ix")) {
      return getXtableIndex(wobj, wtstorage);
    }
    


    if (wstype.equals("tz")) {
      return getZtableDDL(wobj, wtstorage);
    }
    if (wstype.equals("iz")) {
      return getZtableIndex(wobj, wtstorage);
    }
    if (wstype.equals("rz")) {
      return getZTrigDDL(wobj);
    }
    if (wstype.equals("pz")) {
      return getZProcDDL(wobj, wtext);
    }
    if (wstype.equals("p0")) {
      return get0ProcDDL(wobj, wtext);
    }
    if (wstype.equals("rd")) {
      return get0TrigRDDDL(wobj);
    }
    if (wstype.equals("ri")) {
      return get0TrigRIDDL(wobj);
    }
    if (wstype.equals("ru")) {
      return get0TrigRUDDL(wobj);
    }
    if (wstype.equals("szk")) {
      return getZkSequenceDDL(wobj);
    }
    if (wstype.equals("pk")) {
      return getKProcDDL(wobj);
    }
    if ((wstype.equals("crzk")) && (wtype.equalsIgnoreCase("CM"))) {
      return getCRZKCommandDML(wobj);
    }
    if ((wstype.equals("crzk")) && (wtype.equalsIgnoreCase("FN"))) {
      return getCRZKFunctionDML(wobj);
    }
    

    return wtext;
  }
  
  public String get0tableDDL(String wobj, String wtstorage, String wcdim) throws MError {
    String wtable = wobj.substring(0, 4);
    
    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    String wddl = "";
    String wallf = "";
    String wallfd = "";
    String windice = "";
    
    int wtotrec = 0;
    

    String wcsel = " select * from mrfct0  where i_ob = '" + wtable.toUpperCase() + "' " + " order by q_ot";
    


    mrfc_con.execQuery(wcsel);
    Vector wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      String wi_comp = (String)wh.get("i_comp");
      String wc_te = (String)wh.get("c_te");
      wc_te = wc_te.toLowerCase();
      String wx_sql = (String)wh.get("x_sql");
      int wnchar = Integer.valueOf((String)wh.get("q_cari")).intValue();
      int wndec = Integer.valueOf((String)wh.get("q_deci")).intValue();
      wtotrec += wnchar;
      if ((wx_sql.startsWith("K")) || (wx_sql.startsWith("A"))) {
        wallf = wallf + ", FIELD." + wi_comp;
        wallfd = wallfd + ", FIELD." + wi_comp;
        wddl = wddl + ", " + wi_comp;
        if (wc_te.equals("c")) {
          wddl = wddl + " char(" + wnchar + ") " + EOL;
          wallfd = wallfd + " char(" + wnchar + ")";
        }
        if (wc_te.equals("d")) {
          wddl = wddl + " char(10) " + EOL;
          wallfd = wallfd + " char(10)";
        }
        if (wc_te.equals("n")) {
          wddl = wddl + " decimal(" + wnchar + "," + wndec + ") " + EOL;
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
          wddl = wddl + " text " + EOL;
          wallfd = wallfd + " text";
        }
        if (wc_te.equals("b")) {
          wddl = wddl + " bytea " + EOL;
          wallfd = wallfd + " bytea";
        }
      }
    }
    if (wddl.trim().length() != 0) {
      wddl = "create table " + wtable + "t0 (" + EOL + " " + wddl.substring(1) + ")" + EOL;
    }
    


























    return wddl;
  }
  
  public String get0tableIndex(String wobj, String wtstorage) throws MError {
    String wtable = wobj.substring(0, 4);
    
    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    String wddl = "";
    String wallf = "";
    String wallfd = "";
    String windice = "";
    

    String wcsel = " select * from mrfct0  where i_ob = '" + wtable.toUpperCase() + "' " + " and x_sql like 'K%'" + " order by x_sql, q_ot";
    



    mrfc_con.execQuery(wcsel);
    Vector wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      String wi_comp = (String)wh.get("i_comp");
      String wc_te = (String)wh.get("c_te");
      wc_te = wc_te.toLowerCase();
      String wx_sql = (String)wh.get("x_sql");
      int wnchar = Integer.valueOf((String)wh.get("q_cari")).intValue();
      int wndec = Integer.valueOf((String)wh.get("q_deci")).intValue();
      if (((wx_sql.startsWith("K")) || (wx_sql.startsWith("A"))) && 
        (wx_sql.startsWith("K"))) {
        windice = windice + "," + wi_comp;
      }
    }
    

    if (windice.trim().length() != 0) {
      windice = "create unique index " + wtable + "i0 on " + wtable + "t0 (" + windice.substring(1) + ")" + EOL;
    }
    






    return windice;
  }
  
  public String get0tableZKIndex(String wobj, String wtstorage) throws MError {
    String wtable = wobj.substring(0, 4);
    String windice = "";
    windice = windice + "create unique index " + wtable + "izk on " + wtable + "t0 (zk)" + EOL;
    





    return windice;
  }
  
  public String getXtableDDL(String wobj, String wtstorage)
  {
    String wextgeral = "create table EEEEtx (" + EOL + "  zkent decimal(32,0) " + EOL + ", zcomp char(18) " + EOL + ", zchar char(100) " + EOL + ", zdec decimal(21,6) " + EOL + ")" + EOL;
    











    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getZkSequenceDDL(String wobj) {
    String wextgeral = "create sequence EEEEszk " + EOL + "  INCREMENT 1 " + EOL + " START 1 " + EOL;
    

    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
  }
  
  public String getGrantTableDDL(String wobj) {
    String wextgeral = "grant select, update, delete, insert on EEEE to group USER_OF_MINIMAL " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.trim().toLowerCase());
  }
  
  public String getGrantProcedureDDL(String wobj) {
    String wextgeral = "grant execute on function EEEE() to group USER_OF_MINIMAL " + EOL;
    if (wobj.substring(4, 6).toLowerCase().equals("pz")) {
      wextgeral = "grant execute on function EEEE(decimal(32), char(18), char(2),  char(20), varchar(100), int4,  varchar(100)) to group USER_OF_MINIMAL " + EOL;
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.trim().toLowerCase());
  }
  
  public String getCRZKCommandDML(String wobj) {
    String wextgeral = "SELECT setval('EEEEszk', (select int8(max(zk)-mindbdef()) from EEEEt0 where zk between minDBDef() and minDBMax())) " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getCRZKFunctionDML(String wobj) {
    String wextgeral = "create or replace function bcptszk() returns decimal (32,0) as ' " + EOL + "DECLARE " + EOL + "DECLARE " + EOL + "BEGIN " + EOL + "create or replace function bcptszk() returns decimal (32,0) as ' " + EOL;
    







    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getXtableIndex(String wobj, String wtstorage) throws MError
  {
    String wextgeral = "create unique index EEEEix on EEEEtx (zkent,zcomp,zchar,zdec)" + EOL;
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getXtableZKIndex(String wobj, String wtstorage) throws MError {
    String wextgeral = "create unique index EEEEixk on EEEEtx (zk)" + EOL;
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getZtableDDL(String wobj, String wtstorage) {
    String wextgeral = "create table 'minimal'.EEEEtz (" + EOL + "  zkent integer default 0 not null" + EOL + ", zcmd char(18) default ' ' not null" + EOL + ", zfase char(2) default ' ' not null" + EOL + ", zctl char(20) default ' ' not null" + EOL + ", zargs varchar(100)  default ' ' not null" + EOL + ", zcode integer  default 0 not null" + EOL + ", zmsg varchar(100) default ' ' not null" + EOL + ");" + EOL;
    







    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " in " + wtstorage + EOL;
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getZtableIndex(String wobj, String wtstorage) throws MError {
    String wextgeral = "create unique index EEEEiz on EEEEtz (zkent)" + EOL;
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private String getZTrigDDL(String wobj) {
    String wextgeral = "create trigger 'minimal'.EEEErz update of zcmd on EEEEtz " + EOL + "referencing new as a" + EOL + "for each row(" + EOL + "        execute procedure EEEEpz" + EOL + "        (a.zkent, a.zcmd, a.zfase, a.zctl, a.zargs, a.zcode, a.zmsg)" + EOL + "        into zctl, zcode, zmsg);" + EOL;
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private String getKProcDDL(String wobj) throws MError {
    String wextgeral = "create or replace function EEEEpk() returns decimal(32,0) as ' " + EOL + " " + EOL + "  DECLARE" + EOL + "  wzk decimal(32,0);" + EOL + "  BEGIN" + EOL + "     select nextval(\\'EEEEszk\\') into wzk;" + EOL + "     wzk :=  mindbdef() + wzk;" + EOL + "     return wzk;" + EOL + "  END' LANGUAGE 'plpgsql' STABLE;" + EOL;
    







    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private String getZProcDDL(String wobj, String wtext) throws MError {
    if (!existsZPRC(wobj)) {
      return getZProcDDLold(wobj, wtext);
    }
    if ((wtext != null) && (wtext.trim().length() != 0)) {
      String woldup = ", zhst = current hour to second ";
      String wnewup = ", zhst = current hour to second, zprc = -1 ";
      return MUtil.stringRep(wtext, woldup, wnewup);
    }
    String wtable = wobj.substring(0, 4);
    String wextgeral = "create or replace function EEEEpz" + EOL + "  (decimal(32), char(18), char(2),  char(20), varchar(100), int4,  varchar(100))  returns int4 as ' " + EOL + " " + EOL + "-- PGL_DEFINE_BEGIN" + EOL + "  DECLARE" + EOL + "  wecode integer;" + EOL + "  wisam integer;" + EOL + "  wevar varchar(254);" + EOL + "  wdfunc varchar(254);" + EOL + "-- PGL_DEFINE_END" + EOL + "  zkent alias for $1;" + EOL + "  zcmd alias for $2;" + EOL + "  zfase alias for $3;" + EOL + "  zctl alias for $4;" + EOL + "  zargs alias for $5;" + EOL + "  zcode alias for $6;" + EOL + "  zmsg alias for $7;" + EOL + "  BEGIN" + EOL + "-- PGL_BODY_BEGIN" + EOL + "  wdfunc := \\' \\';" + EOL + "  wecode := 0; " + EOL + "  wisam := 0; " + EOL + "  wevar := \\' \\';" + EOL + "-- PGL_BODY_END" + EOL + "  return 0; " + EOL + "  END ' language 'plpgsql' IMMUTABLE; " + EOL;
    

























    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private String getZProcDDLold(String wobj, String wtext) throws MError {
    if ((wtext != null) && (wtext.trim().length() != 0)) {
      return wtext;
    }
    String wtable = wobj.substring(0, 4);
    String wextgeral = "create or replace function EEEEpz" + EOL + "  (decimal(32), char(18), char(2),  char(20), varchar(100), int4,  varchar(100))  returns int4 as ' " + EOL + " " + EOL + "-- PGL_DEFINE_BEGIN" + EOL + "  DECLARE" + EOL + "  wecode integer;" + EOL + "  wisam integer;" + EOL + "  wevar varchar(254);" + EOL + "  wdfunc varchar(254);" + EOL + "-- PGL_DEFINE_END" + EOL + "  zkent alias for $1;" + EOL + "  zcmd alias for $2;" + EOL + "  zfase alias for $3;" + EOL + "  zctl alias for $4;" + EOL + "  zargs alias for $5;" + EOL + "  zcode alias for $6;" + EOL + "  zmsg alias for $7;" + EOL + "  BEGIN" + EOL + "-- PGL_BODY_BEGIN" + EOL + "  wdfunc := \\' \\';" + EOL + "  wecode := 0; " + EOL + "  wisam := 0; " + EOL + "  wevar := \\' \\';" + EOL + "-- PGL_BODY_END" + EOL + "  return 0; " + EOL + "  END ' language 'plpgsql' IMMUTABLE; " + EOL;
    

























    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String get0ProcDDL(String wobj, String wtext) throws MError
  {
    if (!existsZPRC(wobj)) {
      return get0ProcDDLold(wobj, wtext);
    }
    String wtable = wobj.substring(0, 4);
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wextgeral = "create or replace function EEEEp0() returns trigger as ' " + EOL + "-- end create procedure" + EOL + "-- PGL_DEFINE_BEGIN" + EOL + "  DECLARE" + EOL + "  wecode integer;" + EOL + "  wisam integer;" + EOL + "  wevar varchar(254);" + EOL + "  wdfunc varchar(254);" + EOL + "-- PGL_DEFINE_END" + EOL + "  BEGIN" + EOL + "  if tg_op = \\'INSERT\\' then" + EOL + "     if not new.zk > 0 then" + EOL + "        new.zk = EEEEpk();" + EOL + "     end if;" + EOL + "  end if;" + EOL + "  if tg_op <> \\'DELETE\\' then" + EOL + "     if new.zprc <> -1 then" + EOL + "        new.zutl := USER;" + EOL + "        new.zgrp := \\'0000\\';" + EOL + "        new.zdst := current_date;" + EOL + "        new.zhst := substring(current_time,1,8);" + EOL + "        new.zprc := 1;" + EOL + "     end if;" + EOL + "     if new.zprc =  -1 then" + EOL + "        new.zprc := 1;" + EOL + "        return new;" + EOL + "     end if;" + EOL + "  else " + EOL + "     delete from egbot0 where zkent = old.zk and zent = \\'EEEE\\';" + EOL + "     delete from bbint0 where zkent = old.zk and zent = \\'EEEE\\';" + EOL + "     delete from EEEEtx where zkent = old.zk;" + EOL + "  end if;" + EOL + "-- PGL_BODY_BEGIN" + EOL + "    wdfunc := \\' \\';" + EOL + "    wecode := 0; " + EOL + "    wisam := 0; " + EOL + "    wevar := \\' \\';" + EOL + "-- PGL_BODY_END" + EOL + "  if tg_op <> \\'DELETE\\' then" + EOL + "    return new;" + EOL + "  else " + EOL + "    return old;" + EOL + "  end if;" + EOL + "  END ' language 'plpgsql' VOLATILE; " + EOL;
    












































    if (wtext.trim().length() != 0) {
      int wtini = wtext.indexOf("-- end create procedure");
      if (wtini != -1) {
        int wdini = wextgeral.indexOf("-- end create procedure");
        wextgeral = wextgeral.substring(0, wdini) + wtext.substring(wtini);
      }
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toUpperCase());
  }
  
  public String get0ProcDDLold(String wobj, String wtext) throws MError {
    String wtable = wobj.substring(0, 4);
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wextgeral = "create or replace function EEEEp0() returns trigger as ' " + EOL + "-- end create procedure" + EOL + "-- PGL_DEFINE_BEGIN" + EOL + "  DECLARE" + EOL + "  wecode integer;" + EOL + "  wisam integer;" + EOL + "  wevar varchar(254);" + EOL + "  wdfunc varchar(254);" + EOL + "-- PGL_DEFINE_END" + EOL + "  BEGIN" + EOL + "  if tg_op = \\'INSERT\\' then" + EOL + "     if not new.zk > 0 then" + EOL + "        new.zk = EEEEpk();" + EOL + "     end if;" + EOL + "  end if;" + EOL + "  if tg_op <> \\'DELETE\\' then" + EOL + "        new.zutl := USER;" + EOL + "        new.zgrp := \\'0000\\';" + EOL + "        new.zdst := current_date;" + EOL + "        new.zhst := substring(current_time,1,8);" + EOL + "  else " + EOL + "     delete from egbot0 where zkent = old.zk and zent = \\'EEEE\\';" + EOL + "     delete from bbint0 where zkent = old.zk and zent = \\'EEEE\\';" + EOL + "     delete from EEEEtx where zkent = old.zk;" + EOL + "  end if;" + EOL + "-- PGL_BODY_BEGIN" + EOL + "    wdfunc := \\' \\';" + EOL + "    wecode := 0; " + EOL + "    wisam := 0; " + EOL + "    wevar := \\' \\';" + EOL + "-- PGL_BODY_END" + EOL + "  if tg_op <> \\'DELETE\\' then" + EOL + "    return new;" + EOL + "  else " + EOL + "    return old;" + EOL + "  end if;" + EOL + "  END ' language 'plpgsql' VOLATILE; " + EOL;
    









































    if (wtext.trim().length() != 0) {
      int wtini = wtext.indexOf("-- end create procedure");
      if (wtini != -1) {
        int wdini = wextgeral.indexOf("-- end create procedure");
        wextgeral = wextgeral.substring(0, wdini) + wtext.substring(wtini);
      }
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toUpperCase());
  }
  
  public String get0TrigRDDDL(String wobj) throws MError {
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger EEEErd before delete on EEEEt0 " + EOL + "for each row " + EOL + " execute procedure EEEEp0();" + EOL;
    

    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String get0TrigRIDDL(String wobj) throws MError {
    if (!existsZPRC(wobj)) {
      return get0TrigRIDDLold(wobj);
    }
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger EEEEri before delete or insert or update on EEEEt0 " + EOL + "for each row " + EOL + " execute procedure EEEEp0();" + EOL;
    

    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String get0TrigRIDDLold(String wobj) throws MError {
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger EEEEri before delete or insert or update on EEEEt0 " + EOL + "for each row " + EOL + " execute procedure EEEEp0();" + EOL;
    

    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String get0TrigRUDDL(String wobj) throws MError {
    if (!existsZPRC(wobj)) {
      return get0TrigRUDDLold(wobj);
    }
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger EEEEru before update on EEEEt0 " + EOL + "for each row " + EOL + " execute procedure EEEEp0();" + EOL;
    

    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String get0TrigRUDDLold(String wobj) throws MError {
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger EEEEru before update on EEEEt0 " + EOL + "for each row " + EOL + " execute procedure EEEEp0();" + EOL;
    

    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getAllFields(String wobj, String wtype) throws MError {
    String wtable = wobj.substring(0, 4);
    
    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    String wddl = "";
    String wallf = "";
    String wallfd = "";
    String wallfields = "";
    String windice = "";
    


    String wcsel = " select * from mrfct0  where i_ob = '" + wtable.toUpperCase() + "' " + " order by q_ot";
    


    mrfc_con.execQuery(wcsel);
    Vector wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      String wi_comp = (String)wh.get("i_comp");
      String wc_te = (String)wh.get("c_te");
      wc_te = wc_te.toLowerCase();
      String wx_sql = (String)wh.get("x_sql");
      int wnchar = Integer.valueOf((String)wh.get("q_cari")).intValue();
      int wndec = Integer.valueOf((String)wh.get("q_deci")).intValue();
      if ((wx_sql.startsWith("K")) || (wx_sql.startsWith("A"))) {
        wallfields = wallfields + ", FIELD." + wi_comp;
        if (wc_te.equals("c")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " char(" + wnchar + ") default ' ' not null" + EOL;
          wallfd = wallfd + " char(" + wnchar + ")";
        }
        if (wc_te.equals("d")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " char(10) default ' ' not null" + EOL;
          wallfd = wallfd + " char(10)";
        }
        if (wc_te.equals("n")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " decimal(" + wnchar + "," + wndec + ") default 0 not null" + EOL;
          wallfd = wallfd + " decimal(" + wnchar + "," + wndec + ")";
        }
        if (wc_te.equals("s")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " serial" + EOL;
          wallfd = wallfd + " integer";
        }
        if (wc_te.equals("i")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " integer default 0 not null" + EOL;
          wallfd = wallfd + " integer";
        }
      }
    }
    








    if (wtype.equals("WITH_BLOBS")) {
      return wallfields;
    }
    if (wtype.equals("WITH_DDL")) {
      return wallfd;
    }
    return wallf;
  }
  
  public boolean existsTable(String wtable, MCursor wcurs) throws MError
  {
    boolean wboo = false;
    String wcsel = " select relname from pg_class  where relname = ? ";
    
    wcurs.prepareStatement(wcsel);
    wcurs.setPreparedPar(1, wtable.toLowerCase());
    wcurs.execPreparedQuery();
    if (wcurs.getWdcode() == 0) {
      wboo = true;
    }
    wcurs.closePreparedStatement();
    if (wboo) {
      wcsel = " select count(*) from " + wtable.toLowerCase();
      wcurs.prepareStatement(wcsel);
      wcurs.execQuery(wcsel);
      if (wcurs.getWdcode() == 0) {
        int wnrows = wcurs.getInt(1);
        if (wnrows == 0) {
          wboo = false;
        }
      }
      wcurs.closePreparedStatement();
    }
    return wboo;
  }
  
  public boolean existsIndex(String windex, MCursor wcurs, PT.Minimal.geral.MRow wrow) throws MError {
    boolean wboo = false;
    Hashtable wsysindex = null;
    String wcsel = " select b.* from pg_indexes b  where b.indexname = '" + windex.toLowerCase() + "'";
    

    wcurs.prepareStatement(wcsel);
    
    wcurs.execPreparedQuery();
    if (wcurs.getWdcode() == 0) {
      wsysindex = wcurs.getRecord();
      wboo = true;
    }
    wcurs.closeStatement();
    wcurs.closePreparedStatement();
    if (wboo) {
      String wddl = wrow.getAssociatedText("OBJDDL").toLowerCase();
      
      if (wddl.trim().length() != 0) {
        int wini = wddl.indexOf("(");
        int wend = wddl.indexOf(")");
        if ((wend > wini) && (wini != -1)) {
          String windexcols = wddl.substring(wini, wend + 1);
          
          int won = wddl.indexOf("on");
          String wtable = "";
          
          if ((won != -1) && (won < wini - 4)) {
            wtable = wddl.substring(won + 3, wini).trim();
          } else {
            return wboo;
          }
          
          String wddl2 = (String)wsysindex.get("indexdef");
          wini = wddl2.indexOf("(");
          wend = wddl2.indexOf(")");
          if ((wend > wini) && (wini != -1)) {
            String windexcols2 = wddl2.substring(wini, wend + 1);
            windexcols2 = MUtil.stringRep(windexcols2, ", ", ",");
            if (windexcols.trim().compareTo(windexcols2.trim()) != 0) {
              return false;
            }
          } else {
            return false;
          }
        }
      }
    }
    
    return wboo;
  }
  
  public boolean existsColumn(String wtable, String wcolumn, boolean checkType, String wtype, int wnchar, int wndec, MCursor wcurs) throws MError {
    return existsColumn(wtable, wcolumn, checkType, wtype, 0, wnchar, wndec, wcurs);
  }
  
  public boolean existsColumn(String wtable, String wcolumn, boolean checkType, String wtype, int wncol, int wnchar, int wndec, MCursor wcurs) throws MError {
    boolean wboo = false;
    Hashtable wsyscol = null;
    String wcsel = " select b.* from pg_class a, pg_attribute b where a.relfilenode = b.attrelid  and a.relname = ?  and b.attname = ? ";
    



    wcurs.prepareStatement(wcsel);
    wcurs.setPreparedPar(1, wtable.toLowerCase());
    wcurs.setPreparedPar(2, wcolumn.toLowerCase());
    wcurs.execPreparedQuery();
    if (wcurs.getWdcode() == 0) {
      wsyscol = wcurs.getRecord();
      wboo = true;
    }
    wcurs.closePreparedStatement();
    if (!wboo) {
      System.out.println("Column Inexistente " + wcolumn);
      return wboo;
    }
    if (!checkType) {
      return wboo;
    }
    if (wsyscol != null) {
      wboo = false;
      
      int wsysncol = new Integer((String)wsyscol.get("attnum")).intValue();
      if ((wncol != 0) && (wsysncol != wncol)) {
        System.out.println("ExistsColumn OutOFOrder " + wcolumn + " - " + wsysncol + " - " + wboo);
        return false;
      }
      if (wtype.trim().length() == 0) {
        return true;
      }
      
      int wsystype = new Integer((String)wsyscol.get("atttypid")).intValue();
      int wsysnchar = new Integer((String)wsyscol.get("attlen")).intValue();
      int wsysprecision = 0;
      int wsysscale = 0;
      if ((wtype.toLowerCase().equals("c")) && (wsystype == 1042))
      {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("d")) && (wsystype == 1042))
      {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("s")) && (wsystype == 262)) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("n")) && (wsystype == 1700)) {
        wsysprecision = wsysnchar / 256;
        wsysscale = wsysnchar % 256;
        
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("i")) && (wsystype == 23))
      {




        wboo = true;
      }
      if ((wtype.toLowerCase().equals("t")) && (wsystype == 0)) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("b")) && (wsystype == 17)) {
        wboo = true;
      }
    }
    
    if (!wboo) {
      System.out.println("ExistsColumn " + wcolumn + " - " + wboo);
    }
    
    return wboo;
  }
  
  public boolean syncTable(String wobj, String wtstorage, MCursor sdob_curs, boolean metafile) throws MError {
    boolean wboo = false;
    boolean isColumn = true;
    String wtable = wobj.substring(0, 4);
    Vector wv = null;
    MCursor mrfc_con;
    MCursor mrfc_con; if (metafile) {
      mrfc_con = PT.Minimal.geral.MConnection.getMCursor("DEFAULT");
    } else {
      mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    }
    String wddl = "";
    String wallf = "";
    String wallfd = "";
    String windice = "";
    

    String wcsel;
    
    if (metafile) {
      wcsel = "select * from siatt0";
    } else {
      wcsel = "select * from mrfct0";
    }
    String wcsel = wcsel + " where i_ob = '" + wtable.toUpperCase() + "' " + " and substr(x_sql,1,1) in ('K', 'A') " + " order by q_ot";
    


    mrfc_con.execQuery(wcsel);
    wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    
    for (int i = 0; (isColumn) && (wv != null) && (i < wv.size()); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      String wi_comp = (String)wh.get("i_comp");
      String wc_te = (String)wh.get("c_te");
      wc_te = wc_te.toLowerCase();
      String wx_sql = (String)wh.get("x_sql");
      int wnchar = Integer.valueOf((String)wh.get("q_cari")).intValue();
      int wndec = Integer.valueOf((String)wh.get("q_deci")).intValue();
      if ((wx_sql.startsWith("K")) || (wx_sql.startsWith("A"))) {
        isColumn = existsColumn(wtable + "t0", wi_comp, true, wc_te, i + 1, wnchar, wndec, sdob_curs);
      }
    }
    if (!isColumn) {
      wcsel = "alter table " + wtable + "t0" + " rename to " + wtable + "t0" + MUtil.getToday("yyMMdd");
      System.out.println(wcsel);
      sdob_curs.execStatement(wcsel);
      sdob_curs.closeStatement();
      wboo = true;
    }
    return wboo;
  }
  
  public void copySyncTable(String wobj, MCursor sdob_curs, boolean metafile) throws MError {
    String wtable = wobj.substring(0, 4) + "t0";
    String woldtable = wtable + MUtil.getToday("yyMMdd");
    
    String wvalues = getSyncColumns(wobj, sdob_curs, woldtable, false, false, metafile);
    String wselcols = getSyncColumns(wobj, sdob_curs, woldtable, true, true, metafile);
    if (wvalues.trim().length() > 0)
    {
      String wcsel = " insert into " + wtable + " (" + wvalues + ") " + " select " + wselcols + " from " + woldtable;
      

      System.out.println(wcsel);
      sdob_curs.execStatement(wcsel);
      sdob_curs.closeStatement();
    }
  }
  
  private String getSyncColumns(String wobj, MCursor sdob_curs, String woldtable, boolean withTrim, boolean forSelect, boolean metafile) throws MError {
    String wtable = wobj.substring(0, 4);
    String wallfields = "";
    
    String wcsel;
    
    MCursor mrfc_con;
    if (metafile) {
      MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor("DEFAULT");
      wcsel = "select * from siatt0 ";
    } else {
      mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
      wcsel = "select * from mrfct0 ";
    }
    String wcsel = wcsel + " where i_ob = '" + wtable.toUpperCase() + "' ";
    wcsel = wcsel + " order by q_ot";
    
    mrfc_con.execQuery(wcsel);
    Vector wv = mrfc_con.getAllRecords();
    mrfc_con.closeStatement();
    if (!metafile) {
      mrfc_con.close();
    }
    
    for (int i = 0; i < wv.size(); i++) {
      Hashtable wh = (Hashtable)wv.elementAt(i);
      String wi_comp = (String)wh.get("i_comp");
      String wc_te = (String)wh.get("c_te");
      wc_te = wc_te.toLowerCase();
      String wx_sql = (String)wh.get("x_sql");
      int wnchar = Integer.valueOf((String)wh.get("q_cari")).intValue();
      int wndec = Integer.valueOf((String)wh.get("q_deci")).intValue();
      if ((wx_sql.startsWith("K")) || (wx_sql.startsWith("A"))) {
        if (existsColumn(woldtable, wi_comp, false, wc_te, wnchar, wndec, sdob_curs)) {
          wallfields = wallfields + ", " + wi_comp;
        }
        else if (forSelect) {
          if ((wc_te.equals("c")) || (wc_te.equals("d"))) {
            wallfields = wallfields + ", ' '";
          } else {
            wallfields = wallfields + ", 0";
          }
        } else {
          wallfields = wallfields + ", " + wi_comp;
        }
      }
    }
    
    if (wallfields.trim().length() > 0) {
      return wallfields.substring(1);
    }
    return "";
  }
  
  public boolean existsZPRC(String wobj) throws MError
  {
    boolean wboo = false;
    String wtable = wobj.substring(0, 4).toUpperCase();
    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    

    String wcsel = " select nvl(max('true'),'false') as bool from mrfct0  where i_ob = '" + wtable + "' " + " and i_comp = 'zprc'";
    

    try
    {
      mrfc_con.execQuery(wcsel);
      Hashtable wh = mrfc_con.getRecord();
      wboo = new Boolean((String)wh.get("bool")).booleanValue();
    } finally {
      mrfc_con.closeStatement();
    }
    return wboo;
  }
}
