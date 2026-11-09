package PT.Minimal.actions;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MUtil;
import java.util.Hashtable;
import java.util.Vector;

public class MGerDDLOra extends MGerDDL implements MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  PT.Minimal.geral.MForm minForm;
  Vector wrows;
  String EOL = "\n";
  private String wobj;
  private String wobjname;
  private String[] wargs;
  
  public MGerDDLOra() {}
  
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
    System.out.println("wobj: " + wobj);
    System.out.println("wtype: " + wtype);
    System.out.println("wstype: " + wstype);
    if ((wstype.equals("t0")) && (wtype.equalsIgnoreCase("TB"))) {
      return get0tableDDL(wobj, wtstorage, wcdim);
    }
    if (wtype.equalsIgnoreCase("SY")) {
      return getSynonymDDL(wobj);
    }
    if (wtype.equalsIgnoreCase("GR")) {
      if ((wstype.equals("t0")) || (wstype.equals("tx")) || (wstype.equals("tc")) || (wstype.equals("tw"))) {
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
    if ((wstype.equals("tx")) && (wtype.equalsIgnoreCase("TB"))) {
      return getXtableDDL(wobj, wtstorage);
    }
    


    if (wstype.equals("ix")) {
      return getXtableIndex(wobj, wtstorage);
    }
    


    if (wstype.equals("szk")) {
      return getZkSequenceDDL(wobj);
    }
    








    if (wstype.equals("pz")) {
      return getZProcDDL(wobj, wtext);
    }
    if (wstype.equals("p0")) {
      return get0ProcDDL(wobj, wtext);
    }
    


    if (wstype.equals("ri")) {
      return get0TrigRIDDL(wobj);
    }
    if (wstype.equals("rxi")) {
      return getXTrigRIDDL(wobj);
    }
    


    if (wstype.equals("pk")) {
      return getKProcDDL(wobj);
    }
    if (wstype.equals("crzk")) {
      return getCRZKCommandDML(wobj);
    }
    
    return wtext;
  }
  
  public String get0tableDDL(String wobj, String wtstorage, String wcdim) throws MError
  {
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
    mrfc_con.close();
    
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
          wddl = wddl + " decimal(32,0)" + EOL;
          wallfd = wallfd + " decimal(32,0)";
        }
        



        if (wc_te.equals("i")) {
          wddl = wddl + " integer " + EOL;
          wallfd = wallfd + " integer";
        }
        
        if (wc_te.equals("t")) {
          wddl = wddl + " long" + EOL;
          wallfd = wallfd + " long";
        }
        if (wc_te.equals("b")) {
          wddl = wddl + " long raw" + EOL;
          wallfd = wallfd + " long raw";
        }
      }
    }
    if (wddl.trim().length() != 0) {
      wddl = "create table " + wtable + "t0 (" + EOL + " " + wddl.substring(1) + ")" + EOL;
      
      if (wcdim.trim().length() != 0) {
        String wextent = " ";
        int wnpages = 100;
        if (wcdim.equals("s")) {
          wnpages = Math.max(1000 * wtotrec / 1024, wnpages);
        } else if (wcdim.equals("m")) {
          wnpages = Math.max(10000 * wtotrec / 1024, wnpages);
        } else if (wcdim.equals("l")) {
          wnpages = Math.max(25000 * wtotrec / 1024, wnpages);
        } else if (wcdim.equals("xl")) {
          wnpages = Math.max(100000 * wtotrec / 1024, wnpages);
        } else if (wcdim.equals("xxl")) {
          wnpages = Math.max(200000 * wtotrec / 1024, wnpages);
        }
        wextent = wextent + "storage ( initial " + wnpages + "K next " + wnpages / 2 + "K maxextents unlimited) ";
        wddl = wddl + wextent + EOL;
      }
      if (wtstorage.trim().length() != 0) {
        wddl = wddl + " tablespace #TBS " + EOL;
      }
    }
    return wddl;
  }
  
  public String get0tableIndex(String wobj, String wtstorage) throws MError
  {
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
    mrfc_con.close();
    
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
      

      if (wtstorage.trim().length() != 0) {
        windice = windice + " tablespace #TBS " + EOL;
      }
    }
    return windice;
  }
  
  public String get0tableZKIndex(String wobj, String wtstorage) throws MError {
    String wtable = wobj.substring(0, 4);
    String windice = "";
    windice = windice + "create unique index " + wtable + "izk on " + wtable + "t0 (zk)" + EOL;
    
    if (wtstorage.trim().length() != 0) {
      windice = windice + " tablespace #TBS " + EOL;
    }
    return windice;
  }
  
  public String getZkSequenceDDL(String wobj) {
    String wextgeral = "create sequence EEEEszk " + EOL + "  INCREMENT BY 1 " + EOL + " START WITH 1 " + EOL + " NOMAXVALUE " + EOL + " NOCYCLE " + EOL + " NOCACHE " + EOL;
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
  }
  
  public String getSynonymDDL(String wobj) {
    String wextgeral = "create public synonym EEEE for EEEE " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.trim().toLowerCase());
  }
  
  public String getGrantTableDDL(String wobj) {
    String wextgeral = "grant select, update, delete, insert on EEEE to USER_OF_MINIMAL " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.trim().toLowerCase());
  }
  
  public String getGrantProcedureDDL(String wobj) {
    String wextgeral = "grant execute on EEEE to USER_OF_MINIMAL " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.trim().toLowerCase());
  }
  
  public String get0synonymDDL(String wobj) {
    String wextgeral = "create public synonym EEEEt0 for EEEEt0 " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  





  public String getXtableDDL(String wobj, String wtstorage)
  {
    String wextgeral = "create table EEEEtx (" + EOL + "  zkent decimal(32,0) " + EOL + ", zcomp char(18) " + EOL + ", zchar char(100) " + EOL + ", zdec decimal(21,6) " + EOL + ")" + EOL;
    





    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " tablespace #TBS " + EOL;
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getXsynonymDDL(String wobj) {
    String wextgeral = "create public synonym EEEEtx for EEEEtx " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getXtableIndex(String wobj, String wtstorage) throws MError {
    String wextgeral = "create unique index EEEEix on EEEEtx (zkent,zcomp,zchar,zdec)" + EOL;
    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " tablespace #TBS " + EOL;
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getXtableZKIndex(String wobj, String wtstorage) throws MError {
    String wextgeral = "create unique index EEEEixk on EEEEtx (zk)" + EOL;
    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " tablespace #TBS " + EOL;
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getXZkSequenceDDL(String wobj) {
    String wextgeral = "create sequence EEEEsxzk " + EOL + "  INCREMENT BY 1 " + EOL + " START WITH 1 " + EOL + " NOMAXVALUE " + EOL + " NOCYCLE " + EOL + " NOCACHE " + EOL;
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getXTrigRIDDL(String wobj) {
    String wextgeral = "create or replace trigger EEEErxi BEFORE INSERT OR DELETE OR UPDATE on EEEEtx " + EOL + "for each row " + EOL + "  BEGIN " + EOL + "    if inserting then" + EOL + "      if :new.zk = null or :new.zk = 0 then" + EOL + "        select EEEEsxzk.NEXTVAL into :new.zk from dual;" + EOL + "      end if;" + EOL + "    end if;" + EOL + "  END;" + EOL;
    







    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getZtableDDL(String wobj, String wtstorage) {
    String wextgeral = "create table EEEEtz (" + EOL + "  zkent decimal(32,0) " + EOL + ", zcmd char(18) " + EOL + ", zfase char(2) " + EOL + ", zctl char(20)" + EOL + ", zargs varchar(100) " + EOL + ", zcode integer" + EOL + ", zmsg varchar(100)" + EOL + ")" + EOL;
    









    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " tablespace #TBS " + EOL;
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
  }
  
  private String getZTrigDDL(String wobj) {
    String wextgeral = "create trigger EEEErz update of zcmd on EEEEtz " + EOL + "referencing new as a" + EOL + "for each row(" + EOL + "        execute procedure EEEEpz" + EOL + "        (a.zkent, a.zcmd, a.zfase, a.zctl, a.zargs, a.zcode, a.zmsg)" + EOL + "        into zctl, zcode, zmsg);" + EOL;
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
  }
  
  private String getZProcDDL(String wobj, String wtext) throws MError {
    if (!existsZPRC(wobj)) {
      return getZProcDDLold(wobj, wtext);
    }
    if ((wtext != null) && (wtext.trim().length() != 0)) {
      String woldup = ", zhst = TO_CHAR(SYSDATE, 'HH24:MI:SS') ";
      String wnewup = ", zhst = TO_CHAR(SYSDATE, 'HH24:MI:SS'), zprc = -1 ";
      return MUtil.stringRep(wtext, woldup, wnewup);
    }
    
    String wextgeral = "create or replace procedure EEEEpz" + EOL + "  (zkent in number, zcmd in char, zfase in char," + EOL + "  zctl in out char, zargs in varchar2, zcode in out integer," + EOL + "  zmsg in out varchar2) as " + EOL + "-- ORA_DEFINE_BEGIN" + EOL;
    




    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "PZ' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "  wecode integer;" + EOL + "  wisam integer;" + EOL + "  wevar varchar(254);" + EOL;
    }
    

    wextgeral = wextgeral + "  wdfunc varchar(254);" + EOL + "-- ORA_DEFINE_END" + EOL + "  BEGIN" + EOL + "-- ORA_BODY_BEGIN" + EOL + " " + EOL + "  wdfunc := ' ';" + EOL;
    





    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "PZ' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "  wecode := 0; " + EOL + "  wisam := 0; " + EOL + "  wevar := ' ';" + EOL + "  -- Reflexo automático dos campos de controlo " + EOL + "  -- (Utilizador, Data, Hora) da operacao" + EOL + " " + EOL + "-- ORA_BODY_END" + EOL + "  EXCEPTION " + EOL + "  WHEN OTHERS THEN " + EOL + "      wecode := SQLCODE ;" + EOL + "      wevar := SQLERRM;" + EOL + "      RAISE_APPLICATION_ERROR(-20746, ' '||wevar||'\\nEEEEpz:'||wecode||' '||wdfunc);" + EOL;
    }
    


















    wextgeral = wextgeral + "END; " + EOL + "-- end procedure;" + EOL;
    

    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private String getZProcDDLold(String wobj, String wtext) throws MError {
    if ((wtext != null) && (wtext.trim().length() != 0)) {
      return wtext;
    }
    String wextgeral = "create or replace procedure EEEEpz" + EOL + "  (zkent in number, zcmd in char, zfase in char," + EOL + "  zctl in out char, zargs in varchar2, zcode in out integer," + EOL + "  zmsg in out varchar2) as " + EOL + "-- ORA_DEFINE_BEGIN" + EOL;
    




    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "PZ' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "  wecode integer;" + EOL + "  wisam integer;" + EOL + "  wevar varchar(254);" + EOL;
    }
    

    wextgeral = wextgeral + "  wdfunc varchar(254);" + EOL + "-- ORA_DEFINE_END" + EOL + "  BEGIN" + EOL + "-- ORA_BODY_BEGIN" + EOL + " " + EOL + "  wdfunc := ' ';" + EOL;
    





    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "PZ' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "  wecode := 0; " + EOL + "  wisam := 0; " + EOL + "  wevar := ' ';" + EOL + "  -- Reflexo automático dos campos de controlo " + EOL + "  -- (Utilizador, Data, Hora) da operacao" + EOL + " " + EOL + "-- ORA_BODY_END" + EOL + "  EXCEPTION " + EOL + "  WHEN OTHERS THEN " + EOL + "      wecode := SQLCODE ;" + EOL + "      wevar := SQLERRM;" + EOL + "      RAISE_APPLICATION_ERROR(-20746, ' '||wevar||'\\nEEEEpz:'||wecode||' '||wdfunc);" + EOL;
    }
    


















    wextgeral = wextgeral + "END; " + EOL + "-- end procedure;" + EOL;
    
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String get0ProcDDL(String wobj, String wtext) throws MError
  {
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wxfields = MUtil.stringRep(wfieldsddl, "FIELD.", "x");
    wxfields = MUtil.stringRep(wxfields, "INOUT", "IN");
    
    String wyfields = MUtil.stringRep(wfieldsddl, "FIELD.", "y");
    wyfields = MUtil.stringRep(wyfields, "INOUT", "IN");
    
    String wtable = wobj.substring(0, 4);
    String wextgeral = "@drop procedure EEEEp0" + EOL;
    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = "create or replace procedure EEEEp0" + EOL + "        ( zfase in char" + EOL + wxfields + EOL + wyfields + EOL + "        ) as " + EOL + "-- end create procedure" + EOL + "-- ORA_DEFINE_BEGIN" + EOL + "  wecode integer;" + EOL + "  wisam integer;" + EOL + "  wevar varchar(254);" + EOL + "  wdfunc varchar(254);" + EOL + "-- ORA_DEFINE_END" + EOL + "  BEGIN " + EOL + "-- ORA_BODY_BEGIN" + EOL + " " + EOL + "    wdfunc := ' ';" + EOL + "    wecode := 0; " + EOL + "    wisam := 0; " + EOL + "    wevar := ' ';" + EOL + " " + EOL + "-- ORA_BODY_END" + EOL + "  EXCEPTION " + EOL + "  WHEN OTHERS THEN " + EOL + "      wecode := SQLCODE ;" + EOL + "      wevar := SQLERRM;" + EOL + "      RAISE_APPLICATION_ERROR(-20746, ' '||wevar||'\\nEEEEp0:'||wecode||' '||wdfunc);" + EOL + "  END;" + EOL + "  -- end procedure;" + EOL;
      


























      if (wtext.trim().length() != 0) {
        int wtini = wtext.indexOf("-- end create procedure");
        if (wtini != -1) {
          int wdini = wextgeral.indexOf("-- end create procedure");
          wextgeral = wextgeral.substring(0, wdini) + wtext.substring(wtini);
        }
      }
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String getCRZKCommandDML(String wobj) {
    String wextgeral = "declare " + EOL + "wminzk integer; " + EOL + "cursor_name INTEGER; " + EOL + "ret INTEGER; " + EOL + "string varchar2(255); " + EOL + "begin " + EOL + "select max(minZkInt(zk)) into wminzk from EEEEt0 where zk between minDBDef() and minDBMax(); " + EOL + "if wminzk is not null then " + EOL + "cursor_name := DBMS_SQL.OPEN_CURSOR; " + EOL + "string := 'DROP SEQUENCE EEEESZK'; " + EOL + "DBMS_SQL.PARSE(cursor_name, string,1); " + EOL + "ret := DBMS_SQL.EXECUTE(cursor_name);  " + EOL + "select 'CREATE SEQUENCE EEEESZK INCREMENT BY 1 START WITH '||to_char(wminzk+1)||' NOMAXVALUE NOCYCLE NOCACHE' into string from dual; " + EOL + "DBMS_SQL.PARSE(cursor_name, string,1);  " + EOL + "ret := DBMS_SQL.EXECUTE(cursor_name);  " + EOL + "DBMS_SQL.CLOSE_CURSOR(cursor_name); " + EOL + "end if; " + EOL + "end; " + EOL;
    
















    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  private String getKProcDDL(String wobj) throws MError {
    String wextgeral = "create or replace function EEEEpk RETURN NUMBER IS " + EOL + "  wzk NUMBER(32,0);" + EOL + "BEGIN" + EOL + "  select EEEEszk.NEXTVAL into wzk from dual;" + EOL + "  wzk :=  mindbdef() + wzk;" + EOL + "  RETURN (wzk);" + EOL + "END EEEEpk;" + EOL;
    





    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String get0TrigRIDDL(String wobj) throws MError {
    if (!existsZPRC(wobj)) {
      return get0TrigRIDDLold(wobj);
    }
    
    String wfields = getAllFields(wobj, "NO_DDL");
    

    String wextgeral = "create or replace trigger EEEEri BEFORE INSERT OR DELETE OR UPDATE on EEEEt0 " + EOL + "for each row " + EOL + "  declare wfase char(2);" + EOL + "BEGIN " + EOL + "--ORA_BEGIN_DBMS_REPUTIL" + EOL + "    if inserting then" + EOL + "      if :new.zk = null or :new.zk = 0 then" + EOL + "        :new.zk := EEEEpk();" + EOL + "      end if;" + EOL;
    








    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "      wfase := 'RI';" + EOL + "      if not :new.zprc < 0 then EEEEp0(wfase" + EOL + MUtil.stringRep(wfields, "FIELD", ":new") + EOL + MUtil.stringRep(wfields, "FIELD", ":new") + EOL + "             );" + EOL + "      end if;" + EOL;
    }
    





    wextgeral = wextgeral + "      :new.zutl := USER; :new.zgrp := '0000'; :new.zdst := TO_CHAR(SYSDATE, 'YYYY-MM-DD'); :new.zhst := TO_CHAR(SYSDATE, 'HH24:MI:SS'); :new.zprc := 1;" + EOL + "    end if;" + EOL + "    if updating then" + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "      wfase := 'RU';" + EOL + "      if :new.zprc >= 0 then" + EOL + "        EEEEp0(wfase" + EOL + MUtil.stringRep(wfields, "FIELD", ":old") + EOL + MUtil.stringRep(wfields, "FIELD", ":new") + EOL + "               );" + EOL + "      end if;" + EOL;
    }
    





    wextgeral = wextgeral + "      if not :new.zprc = -1 then :new.zutl := USER; :new.zgrp := '0000'; :new.zdst := TO_CHAR(SYSDATE, 'YYYY-MM-DD'); :new.zhst := TO_CHAR(SYSDATE, 'HH24:MI:SS'); :new.zprc := 1; end if;" + EOL;
    wextgeral = wextgeral + "      if     :new.zprc = -1 then :new.zprc := 1; end if;" + EOL + "    end if;" + EOL + "    if deleting then" + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "      wfase := 'RD';" + EOL + "      EEEEp0(wfase" + EOL + MUtil.stringRep(wfields, "FIELD", ":old") + EOL + MUtil.stringRep(wfields, "FIELD", ":old") + EOL + "               );" + EOL;
    }
    




    wextgeral = wextgeral + "      delete from egbot0 where zkent = :old.zk and zent = '" + wobj.substring(0, 4).toUpperCase() + "';" + EOL + "      delete from bbint0 where zkent = :old.zk and zent = '" + wobj.substring(0, 4).toUpperCase() + "';" + EOL + "      delete from EEEEtx where zkent = :old.zk;" + EOL + "    end if;" + EOL + "--ORA_END_DBMS_REPUTIL" + EOL + "END;";
    




    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public String get0TrigRIDDLold(String wobj) throws MError {
    String wfields = getAllFields(wobj, "NO_DDL");
    

    String wextgeral = "create or replace trigger EEEEri BEFORE INSERT OR DELETE OR UPDATE on EEEEt0 " + EOL + "for each row " + EOL + "  declare wfase char(2);" + EOL + "BEGIN " + EOL + "--ORA_BEGIN_DBMS_REPUTIL" + EOL + "    if inserting then" + EOL + "      if :new.zk = null or :new.zk = 0 then" + EOL + "        :new.zk := EEEEpk();" + EOL + "      end if;" + EOL;
    








    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "      wfase := 'RI';" + EOL + "      EEEEp0(wfase" + EOL + MUtil.stringRep(wfields, "FIELD", ":new") + EOL + MUtil.stringRep(wfields, "FIELD", ":new") + EOL + "             );" + EOL;
    }
    




    wextgeral = wextgeral + "      :new.zutl := USER; :new.zgrp := '0000'; :new.zdst := TO_CHAR(SYSDATE, 'YYYY-MM-DD'); :new.zhst := TO_CHAR(SYSDATE, 'HH24:MI:SS');" + EOL + "    end if;" + EOL + "    if updating then" + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "      wfase := 'RU';" + EOL + "      if :new.zhst = :old.zhst and :new.zdst = :old.zdst then" + EOL + "        EEEEp0(wfase" + EOL + MUtil.stringRep(wfields, "FIELD", ":old") + EOL + MUtil.stringRep(wfields, "FIELD", ":new") + EOL + "               );" + EOL + "      end if;" + EOL;
    }
    






    wextgeral = wextgeral + "      :new.zutl := USER; :new.zgrp := '0000'; :new.zdst := TO_CHAR(SYSDATE, 'YYYY-MM-DD'); :new.zhst := TO_CHAR(SYSDATE, 'HH24:MI:SS');" + EOL + "    end if;" + EOL + "    if deleting then" + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "      wfase := 'RD';" + EOL + "      EEEEp0(wfase" + EOL + MUtil.stringRep(wfields, "FIELD", ":old") + EOL + MUtil.stringRep(wfields, "FIELD", ":old") + EOL + "               );" + EOL;
    }
    




    wextgeral = wextgeral + "      delete from egbot0 where zkent = :old.zk and zent = '" + wobj.substring(0, 4).toUpperCase() + "';" + EOL + "      delete from EEEEtx where zkent = :old.zk;" + EOL + "    end if;" + EOL + "--ORA_END_DBMS_REPUTIL" + EOL + "END;";
    



    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
  }
  
  public boolean existsTable(String wtable, MCursor wcurs) throws MError
  {
    boolean wboo = false;
    String wcsel = " select table_name from user_tables  where table_name = '" + wtable.toUpperCase() + "'";
    
    System.out.println(wcsel);
    wcurs.execQuery(wcsel);
    if (wcurs.getWdcode() == 0) {
      wboo = true;
    }
    wcurs.closeStatement();
    












    return wboo;
  }
  
  public boolean existsIndex(String windex, MCursor wcurs, PT.Minimal.geral.MRow wrow) throws MError {
    boolean wboo = false;
    Hashtable wsysindex = null;
    Vector wv = null;
    String wcsel = " select * from user_ind_columns  where index_name = '" + windex.toUpperCase() + "' order by COLUMN_POSITION";
    

    wcurs.prepareStatement(wcsel);
    
    wcurs.execPreparedQuery();
    if (wcurs.getWdcode() == 0) {
      wv = wcurs.getAllRecords();
      wboo = true;
    }
    wcurs.closeStatement();
    wcurs.closePreparedStatement();
    if ((wboo) && (wv != null)) {
      String wddl = wrow.getAssociatedText("OBJDDL").toLowerCase();
      
      if (wddl.trim().length() != 0) {
        int wini = wddl.indexOf("(");
        int wend = wddl.indexOf(")");
        
        if ((wend > wini) && (wini != -1)) {
          int won = wddl.indexOf("on");
          String wtable = "";
          
          if ((won != -1) && (won < wini - 4)) {
            wtable = wddl.substring(won + 3, wini).trim();
          } else {
            return wboo;
          }
          
          String[] wargs = MUtil.getArgs(wddl.substring(wini + 1, wend), ',');
          if (wargs.length != wv.size()) {
            System.out.println("Nr de colunas no indice diferente: " + wargs.length);
            return false;
          }
          for (int i = 0; i < wargs.length; i++) {
            if (wargs[i].trim().indexOf(" ") != -1) {
              wargs[i] = wargs[i].trim().substring(0, wargs[i].trim().indexOf(" "));
            }
            wsysindex = (Hashtable)wv.elementAt(i);
            String wsyscol = (String)wsysindex.get("column_name");
            String wsystable = (String)wsysindex.get("table_name");
            if ((!wsyscol.equalsIgnoreCase(wargs[i])) || (!wsystable.equalsIgnoreCase(wtable))) {
              System.out.println("ExistsColumn OutOFOrder " + wsyscol + " - " + i);
              return false;
            }
          }
        }
      }
    }
    return wboo;
  }
  

  public boolean existsColumn(String wtable, String wcolumn, boolean checkType, String wtype, int wnchar, int wndec, MCursor wcurs) throws MError { return existsColumn(wtable, wcolumn, checkType, wtype, 0, wnchar, wndec, wcurs); }
  
  public boolean existsColumn(String wtable, String wcolumn, boolean checkType, String wtype, int wncol, int wnchar, int wndec, MCursor wcurs) throws MError {
    boolean wboo = false;
    Hashtable wsyscol = null;
    String wcsel = " select data_type, column_id, nvl(data_length,0) as data_length, nvl(data_precision,0) as data_precision, nvl(data_scale,0) as data_scale from user_tab_columns where table_name = '" + wtable.toUpperCase() + "'" + " and column_name = '" + wcolumn.toUpperCase() + "'";
    





    wcurs.execQuery(wcsel);
    if (wcurs.getWdcode() == 0) {
      wsyscol = wcurs.getRecord();
      wboo = true;
    }
    
    wcurs.closeStatement();
    
    if (!wboo) {
      System.out.println("Column Inexistente " + wcolumn);
      return wboo;
    }
    if (!checkType) {
      return wboo;
    }
    
    if (wsyscol != null) {
      wboo = false;
      
      int wsysncol = new Integer((String)wsyscol.get("column_id")).intValue();
      if ((wncol != 0) && (wsysncol != wncol)) {
        System.out.println("ExistsColumn OutOFOrder " + wcolumn + " - " + wboo);
        return false;
      }
      if (wtype.trim().length() == 0) {
        return true;
      }
      String wsystype = (String)wsyscol.get("data_type");
      int wsysnchar = new Integer((String)wsyscol.get("data_length")).intValue();
      int wsysprecision = 0;
      int wsysscale = 0;
      if (wsystype.equals("NUMBER")) {
        wsysprecision = new Integer((String)wsyscol.get("data_precision")).intValue();
        wsysscale = new Integer((String)wsyscol.get("data_scale")).intValue();
      }
      

      if ((wtype.toLowerCase().equals("c")) && (wsystype.equals("CHAR")) && 
        (wsysnchar == wnchar)) {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("d")) && (wsystype.equals("CHAR")) && 
        (wsysnchar >= wnchar)) {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("s")) && (wsystype.equals("NUMBER"))) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("n")) && (wsystype.equals("NUMBER")) && 
        (wsysprecision == wnchar) && (wsysscale == wndec)) {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("i")) && (wsystype.equals("NUMBER"))) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("t")) && (wsystype.equals("LONG RAW"))) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("b")) && (wsystype.equals("LONG RAW"))) {
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
    

    if (wtstorage.trim().length() != 0) {
      String wcsel = " select table_name from user_tables  where table_name = '" + wobj.toUpperCase() + "'";
      

      System.out.println(wcsel);
      sdob_curs.execQuery(wcsel);
      if (sdob_curs.getWdcode() == 100) {
        isColumn = false;
        System.out.println("Tabela " + wtable + " não está no tablespace " + wtstorage);
      }
      sdob_curs.closeStatement();
    }
    
    if (isColumn) { String wcsel;
      if (metafile) {
        wcsel = "select * from siatt0";
      } else {
        wcsel = "select * from mrfct0";
      }
      String wcsel = wcsel + " where i_ob = '" + wtable.toUpperCase() + "' " + " and substr(x_sql,1,1) in ('K', 'A') " + " order by q_ot";
      


      mrfc_con.execQuery(wcsel);
      wv = mrfc_con.getAllRecords();
      mrfc_con.closeStatement();
    }
    
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
      String wcsel = "alter table " + wtable + "t0" + " rename to " + wtable + "t0" + MUtil.getToday("yyMMdd");
      System.out.println(wcsel);
      sdob_curs.execStatement(wcsel);
      sdob_curs.closeStatement();
      wboo = true;
    }
    
    if (!metafile) {
      mrfc_con.close();
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
          if (withTrim) {
            if ((wc_te.equals("c")) || ((wc_te.equals("d")) && (withTrim))) {
              wallfields = wallfields + ", nvl(rtrim(" + wi_comp + "),' ')";
            } else {
              wallfields = wallfields + ", nvl(" + wi_comp + ",0)";
            }
          } else {
            wallfields = wallfields + ", " + wi_comp;
          }
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
  
  public String getAllFields(String wobj, String wtype) throws MError
  {
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
    mrfc_con.close();
    
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
          wddl = wddl + " char(" + wnchar + ")" + EOL;
          wallfd = wallfd + " INOUT char";
        }
        if (wc_te.equals("d")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " char(10)" + EOL;
          wallfd = wallfd + " INOUT char";
        }
        if (wc_te.equals("n")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " decimal(" + wnchar + "," + wndec + ")" + EOL;
          wallfd = wallfd + " INOUT numeric";
        }
        if (wc_te.equals("s")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " serial" + EOL;
          wallfd = wallfd + " INOUT numeric";
        }
        if (wc_te.equals("i")) {
          wallf = wallf + ", FIELD." + wi_comp;
          wallfd = wallfd + ", FIELD." + wi_comp;
          wddl = wddl + ", " + wi_comp;
          wddl = wddl + " integer" + EOL;
          wallfd = wallfd + " INOUT integer";
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
