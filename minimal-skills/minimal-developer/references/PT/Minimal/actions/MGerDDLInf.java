package PT.Minimal.actions;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MUtil;
import java.util.Hashtable;
import java.util.Vector;

public class MGerDDLInf extends MGerDDL implements MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  PT.Minimal.geral.MForm minForm;
  Vector wrows;
  String EOL = "\n";
  private String wobj;
  private String wobjname;
  private String[] wargs;
  
  public MGerDDLInf() {}
  
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
    String wstype = "";
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wstype = wobj.substring(4);
    } else {
      wstype = wobj.substring(7);
    }
    
    System.out.println("wstype: " + wstype);
    if (wstype.equals("t0")) {
      return get0tableDDL(wobj, wtstorage, wcdim);
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
      return getSZKtableDDL(wobj, wtstorage);
    }
    if (wstype.equals("pk")) {
      return getKProcDDL(wobj);
    }
    if (wstype.equals("crzk")) {
      return getCRZKCommandDML(wobj);
    }
    
    return wtext;
  }
  
  public String get0tableDDL(String wobj, String wtstorage, String wcdim) throws MError {
    String wtable = "";
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wtable = wobj.substring(0, 4);
    } else {
      wtable = wobj.substring(0, 7);
    }
    

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
          wddl = wddl + " byte " + EOL;
          wallfd = wallfd + " byte";
        }
      }
    }
    if (wddl.trim().length() != 0) {
      wddl = "create table " + wtable + "t0 (" + EOL + " " + wddl.substring(1) + ")" + EOL;
      

      if (wtstorage.trim().length() != 0) {
        wddl = wddl + " in " + wtstorage + EOL;
      }
      if (wcdim.trim().length() != 0) {
        String wextent = " ";
        int wnpages = 16;
        if (wcdim.equals("s")) {
          wnpages = Math.max(1000 * wtotrec / 3000, wnpages);
        }
        if (wcdim.equals("m")) {
          wnpages = Math.max(10000 * wtotrec / 3000, wnpages);
        }
        if (wcdim.equals("l")) {
          wnpages = Math.max(25000 * wtotrec / 3000, wnpages);
        }
        if (wcdim.equals("xl")) {
          wnpages = Math.max(100000 * wtotrec / 3000, wnpages);
        }
        if (wcdim.equals("xxl")) {
          wnpages = Math.max(200000 * wtotrec / 3000, wnpages);
        }
        wextent = wextent + "extent size " + wnpages + " next size " + wnpages / 2;
        wddl = wddl + wextent + EOL;
      }
    }
    
    return wddl;
  }
  
  public String get0tableIndex(String wobj, String wtstorage) throws MError
  {
    String wtable = "";
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wtable = wobj.substring(0, 4);
    } else {
      wtable = wobj.substring(0, 7);
    }
    

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
      

      if (wtstorage.trim().length() != 0) {
        windice = windice + " in " + wtstorage + EOL;
      }
    }
    return windice;
  }
  
  public String get0tableZKIndex(String wobj, String wtstorage) throws MError
  {
    String wtable = "";
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wtable = wobj.substring(0, 4);
    } else {
      wtable = wobj.substring(0, 7);
    }
    String windice = "";
    windice = windice + "create unique index " + wtable + "izk on " + wtable + "t0 (zk)" + EOL;
    
    if (wtstorage.trim().length() != 0) {
      windice = windice + " in " + wtstorage + EOL;
    }
    return windice;
  }
  
  public String getXtableDDL(String wobj, String wtstorage)
  {
    String wextgeral = "create table 'minimal'.EEEEtx (" + EOL + "  zkent decimal(32,0) " + EOL + ", zcomp char(18) " + EOL + ", zchar char(100) " + EOL + ", zdec decimal(21,6) " + EOL + ")" + EOL;
    











    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " in " + wtstorage + EOL;
    }
    

    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getSZKtableDDL(String wobj, String wtstorage)
  {
    String wextgeral = "create table 'minimal'.EEEEszk (" + EOL + "  zk serial " + EOL + ")" + EOL;
    

    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " in " + wtstorage + EOL;
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  

  public String getCRZKCommandDML(String wobj)
  {
    String wextgeral = "insert into EEEEszk select minZkInt(zk) from EEEEt0 where zk = (select max(zk) from EEEEt0 where zk between minDBDef() and minDBMax()) ;" + EOL;
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getXtableIndex(String wobj, String wtstorage) throws MError
  {
    String wextgeral = "create unique index 'minimal'.EEEEix on EEEEtx (zkent,zcomp,zchar,zdec)" + EOL;
    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " in " + wtstorage + EOL;
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getXtableZKIndex(String wobj, String wtstorage) throws MError
  {
    String wextgeral = "create unique index 'minimal'.EEEEixk on EEEEtx (zk)" + EOL;
    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " in " + wtstorage + EOL;
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getZtableDDL(String wobj, String wtstorage)
  {
    String wextgeral = "create table 'minimal'.EEEEtz (" + EOL + "  zkent integer default 0 not null" + EOL + ", zcmd char(18) default ' ' not null" + EOL + ", zfase char(2) default ' ' not null" + EOL + ", zctl char(20) default ' ' not null" + EOL + ", zargs varchar(100)  default ' ' not null" + EOL + ", zcode integer  default 0 not null" + EOL + ", zmsg varchar(100) default ' ' not null" + EOL + ");" + EOL;
    







    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " in " + wtstorage + EOL;
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getZtableIndex(String wobj, String wtstorage) throws MError
  {
    String wextgeral = "create unique index 'minimal'.EEEEiz on EEEEtz (zkent)" + EOL;
    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " in " + wtstorage + EOL;
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  private String getZTrigDDL(String wobj)
  {
    String wextgeral = "create trigger 'minimal'.EEEErz update of zcmd on EEEEtz " + EOL + "referencing new as a" + EOL + "for each row(" + EOL + "        execute procedure EEEEpz" + EOL + "        (a.zkent, a.zcmd, a.zfase, a.zctl, a.zargs, a.zcode, a.zmsg)" + EOL + "        into zctl, zcode, zmsg);" + EOL;
    





    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  private String getKProcDDL(String wobj) throws MError
  {
    String wextgeral = "create procedure 'minimal'.EEEEpk()" + EOL + "  returning decimal(32,0);" + EOL + " " + EOL + "  define wecode integer;" + EOL + "  define wisam integer;" + EOL + "  define wevar varchar(254);" + EOL + "  define wdfunc varchar(254);" + EOL + "  define wzk decimal(32,0);" + EOL + "  define wintzk integer;" + EOL + "  on exception set wecode, wisam, wevar" + EOL + "    raise exception -746, wecode, ' '||wevar||'\\nEEEEpk:'||wecode||' '||wdfunc;" + EOL + "  end exception;" + EOL + "  let wdfunc = 'EEEEszk'; let wecode = 0; let wisam = 0; let wevar = ' ';" + EOL + "  delete from EEEEszk;" + EOL + "  insert into EEEEszk values(0);" + EOL + " " + EOL + "  let wdfunc = 'dbinfo';" + EOL + "  let wintzk =  dbinfo('sqlca.sqlerrd1');" + EOL + " " + EOL + "  let wdfunc = 'MINDBDEF';" + EOL + "  let wzk =  mindbdef() + wintzk;" + EOL + " " + EOL + "return wzk; " + EOL + "end procedure;" + EOL;
    























    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  private String getZProcDDL(String wobj, String wtext) throws MError
  {
    if (!existsZPRC(wobj)) {
      return getZProcDDLold(wobj, wtext);
    }
    if ((wtext != null) && (wtext.trim().length() != 0)) {
      String woldup = ", zhst = current hour to second ";
      String wnewup = ", zhst = current hour to second, zprc = -1 ";
      return MUtil.stringRep(wtext, woldup, wnewup);
    }
    String wtable = wobj.substring(0, 4);
    String wextgeral = "create procedure 'minimal'.EEEEpz" + EOL + "  (zkent decimal(32), zcmd char(18), zfase char(2)," + EOL + "  zctl char(20), zargs varchar(100), zcode integer," + EOL + "  zmsg varchar(100))" + EOL + "  returning char(20), integer, varchar(100);" + EOL + " " + EOL;
    





    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "PZ' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "-- INF_DEFINE_BEGIN" + EOL + "  define wecode integer;" + EOL + "  define wisam integer;" + EOL + "  define wevar varchar(254);" + EOL + "  define wdfunc varchar(254);" + EOL + "-- INF_DEFINE_END" + EOL + "  on exception set wecode, wisam, wevar" + EOL + "    raise exception -746, wecode, ' '||wevar||'\\nEEEEpz:'||wecode||' '||wdfunc;" + EOL + "  end exception;" + EOL + "-- INF_BODY_BEGIN" + EOL + "  let wdfunc = ' ';" + EOL + "  let wecode = 0; " + EOL + "  let wisam = 0; " + EOL + "  let wevar = ' ';" + EOL + "  -- Reflexo automático dos campos de controlo " + EOL + "  -- (Utilizador, Data, Hora) da operacao" + EOL + " " + EOL + "-- INF_BODY_END" + EOL;
    }
    



























    wextgeral = wextgeral + "return zctl, zcode, zmsg; " + EOL + "end procedure;" + EOL;
    

    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  private String getZProcDDLold(String wobj, String wtext) throws MError
  {
    if ((wtext != null) && (wtext.trim().length() != 0)) {
      return wtext;
    }
    String wtable = wobj.substring(0, 4);
    String wextgeral = "create procedure 'minimal'.EEEEpz" + EOL + "  (zkent decimal(32), zcmd char(18), zfase char(2)," + EOL + "  zctl char(20), zargs varchar(100), zcode integer," + EOL + "  zmsg varchar(100))" + EOL + "  returning char(20), integer, varchar(100);" + EOL + " " + EOL;
    





    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "PZ' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "-- INF_DEFINE_BEGIN" + EOL + "  define wecode integer;" + EOL + "  define wisam integer;" + EOL + "  define wevar varchar(254);" + EOL + "  define wdfunc varchar(254);" + EOL + "-- INF_DEFINE_END" + EOL + "  on exception set wecode, wisam, wevar" + EOL + "    raise exception -746, wecode, ' '||wevar||'\\nEEEEpz:'||wecode||' '||wdfunc;" + EOL + "  end exception;" + EOL + "-- INF_BODY_BEGIN" + EOL + "  let wdfunc = ' ';" + EOL + "  let wecode = 0; " + EOL + "  let wisam = 0; " + EOL + "  let wevar = ' ';" + EOL + "  -- Reflexo automático dos campos de controlo " + EOL + "  -- (Utilizador, Data, Hora) da operacao" + EOL + " " + EOL + "-- INF_BODY_END" + EOL;
    }
    



























    wextgeral = wextgeral + "return zctl, zcode, zmsg; " + EOL + "end procedure;" + EOL;
    

    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0ProcDDL(String wobj, String wtext)
    throws MError
  {
    String wtable = wobj.substring(0, 4);
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wextgeral = "@drop procedure 'minimal'.EEEEp0";
    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = "create procedure 'minimal'.EEEEp0" + EOL + "        (zfase char(2)" + EOL + MUtil.stringRep(wfieldsddl, "FIELD.", "x") + EOL + MUtil.stringRep(wfieldsddl, "FIELD.", "y") + EOL + "         )" + EOL + "-- end create procedure" + EOL + "-- INF_DEFINE_BEGIN" + EOL + "  define wecode integer;" + EOL + "  define wisam integer;" + EOL + "  define wevar varchar(254);" + EOL + "  define wdfunc varchar(254);" + EOL + "-- INF_DEFINE_END" + EOL + "  on exception set wecode, wisam, wevar" + EOL + "    raise exception -746, wecode, ' '||wevar||'\\nEEEEp0:'||wecode||' '||wdfunc;" + EOL + "  end exception;" + EOL + "-- INF_BODY_BEGIN" + EOL + "  let wdfunc = ' ';" + EOL + "  let wecode = 0; " + EOL + "  let wisam = 0; " + EOL + "  let wevar = ' ';" + EOL + "-- INF_BODY_END" + EOL + "end procedure;" + EOL;
      




















      if (wtext.trim().length() != 0) {
        int wtini = wtext.indexOf("-- end create procedure");
        if (wtini != -1) {
          int wdini = wextgeral.indexOf("-- end create procedure");
          wextgeral = wextgeral.substring(0, wdini) + wtext.substring(wtini);
        }
      }
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0TrigRDDDL(String wobj)
    throws MError
  {
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger 'minimal'.EEEErd delete on EEEEt0 " + EOL + "referencing old as y" + EOL + "for each row(" + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "        execute procedure EEEEp0" + EOL + "        ('RD'" + EOL + MUtil.stringRep(wfields, "FIELD", "y") + EOL + MUtil.stringRep(wfields, "FIELD", "y") + EOL + "        ), " + EOL;
    }
    




    wextgeral = wextgeral + "          delete from egbot0 where zkent = y.zk and zent = '" + wobj.substring(0, 4).toUpperCase() + "'" + EOL + "        , delete from bbint0 where zkent = y.zk and zent = '" + wobj.substring(0, 4).toUpperCase() + "'" + EOL + "        , delete from EEEEtx where zkent = y.zk" + EOL + " );" + EOL;
    



    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0TrigRIDDL(String wobj) throws MError
  {
    if (!existsZPRC(wobj)) {
      return get0TrigRIDDLold(wobj);
    }
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger 'minimal'.EEEEri insert on EEEEt0 " + EOL + "referencing new as x" + EOL + "for each row" + EOL + "        when (x.zk is null or x.zk = 0) (execute procedure EEEEpk() into zk)" + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "        ,when (not x.zprc < 0) (execute procedure EEEEp0" + EOL + "        ('RI'" + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + "        )), " + EOL + " (execute procedure minz() into zutl, zgrp, zdst, zhst, zprc);" + EOL;


    }
    else
    {

      wextgeral = wextgeral + " ,(execute procedure minz() into zutl, zgrp, zdst, zhst, zprc);" + EOL;
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0TrigRIDDLold(String wobj) throws MError
  {
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger 'minimal'.EEEEri insert on EEEEt0 " + EOL + "referencing new as x" + EOL + "for each row" + EOL + "        when (x.zk is null or x.zk = 0) (execute procedure EEEEpk() into zk)" + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "        ,(execute procedure EEEEp0" + EOL + "        ('RI'" + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + "        )), " + EOL + " (execute procedure minzo() into zutl, zgrp, zdst, zhst);" + EOL;


    }
    else
    {

      wextgeral = wextgeral + " ,(execute procedure minzo() into zutl, zgrp, zdst, zhst);" + EOL;
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0TrigRUDDL(String wobj) throws MError
  {
    if (!existsZPRC(wobj)) {
      return get0TrigRUDDLold(wobj);
    }
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger 'minimal'.EEEEru update on EEEEt0 " + EOL + "referencing old as x new as y" + EOL + "for each row " + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "        when (y.zprc >= 0) (" + EOL + "        execute procedure EEEEp0" + EOL + "        ('RU'" + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + MUtil.stringRep(wfields, "FIELD", "y") + EOL + "        )), " + EOL;
    }
    




    wextgeral = wextgeral + " when (y.zprc <> -1) (execute procedure minz() into zutl, zgrp, zdst, zhst, zprc)," + EOL;
    wextgeral = wextgeral + " when (y.zprc =  -1) (execute procedure tonumber('1') into zprc); " + EOL;
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0TrigRUDDLold(String wobj) throws MError
  {
    String wfields = getAllFields(wobj, "NO_DDL");
    String wextgeral = "create trigger 'minimal'.EEEEru update on EEEEt0 " + EOL + "referencing old as x new as y" + EOL + "for each row " + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "        when (y.zhst = x.zhst and y.zdst = x.zdst) (" + EOL + "        execute procedure EEEEp0" + EOL + "        ('RU'" + EOL + MUtil.stringRep(wfields, "FIELD", "x") + EOL + MUtil.stringRep(wfields, "FIELD", "y") + EOL + "        )), " + EOL;
    }
    





    wextgeral = wextgeral + " (execute procedure minzo() into zutl, zgrp, zdst, zhst);" + EOL;
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
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
    String wcsel = " select tabname from systables  where tabname = ? ";
    
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
    String wcsel = " select * from sysindexes  where idxname = '" + windex.toLowerCase() + "'";
    

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
          int won = wddl.indexOf("on");
          String wtable = "";
          
          if ((won != -1) && (won < wini - 4)) {
            wtable = wddl.substring(won + 3, wini).trim();
          } else {
            return wboo;
          }
          
          String[] wargs = MUtil.getArgs(wddl.substring(wini + 1, wend), ',');
          
          int widxcol = new Integer((String)wsysindex.get("part" + wargs.length)).intValue();
          if (widxcol == 0) {
            System.out.println("Nr de colunas no indice diferente: " + wargs.length);
            return false;
          }
          
          if (wargs.length < 16) {
            int j = wargs.length + 1;
            widxcol = new Integer((String)wsysindex.get("part" + j)).intValue();
            if (widxcol != 0) {
              System.out.println("Nr de colunas no indice diferente: " + wargs.length);
              return false;
            }
          }
          for (int i = 0; i < wargs.length; i++) {
            if (wargs[i].trim().indexOf(" ") != -1) {
              wargs[i] = wargs[i].trim().substring(0, wargs[i].trim().indexOf(" "));
            }
            int j = i + 1;
            widxcol = new Integer((String)wsysindex.get("part" + j)).intValue();
            wboo = existsColumn(wtable, wargs[i], true, "", Math.abs(widxcol), 0, 0, wcurs);
            if (!wboo) {
              return wboo;
            }
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
    String wcsel = " select b.* from systables a, syscolumns b where a.tabid = b.tabid  and a.tabname = ?  and b.colname = ? ";
    



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
      
      int wsysncol = new Integer((String)wsyscol.get("colno")).intValue();
      if ((wncol != 0) && (wsysncol != wncol)) {
        System.out.println("ExistsColumn OutOFOrder " + wcolumn + " - " + wsysncol + " - " + wboo);
        return false;
      }
      if (wtype.trim().length() == 0) {
        return true;
      }
      
      int wsystype = new Integer((String)wsyscol.get("coltype")).intValue();
      int wsysnchar = new Integer((String)wsyscol.get("collength")).intValue();
      int wsysprecision = 0;
      int wsysscale = 0;
      if ((wtype.toLowerCase().equals("c")) && ((wsystype == 256) || (wsystype == 0)) && 
        (wsysnchar == wnchar)) {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("d")) && ((wsystype == 256) || (wsystype == 0)) && 
        (wsysnchar >= wnchar)) {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("s")) && (wsystype == 262)) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("n")) && ((wsystype == 261) || (wsystype == 5))) {
        wsysprecision = wsysnchar / 256;
        wsysscale = wsysnchar % 256;
        if ((wsysprecision == wnchar) && (wsysscale == wndec)) {
          wboo = true;
        }
      }
      if ((wtype.toLowerCase().equals("i")) && ((wsystype == 258) || (wsystype == 2)))
      {




        wboo = true;
      }
      if ((wtype.toLowerCase().equals("t")) && ((wsystype == 268) || (wsystype == 12))) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("b")) && ((wsystype == 267) || (wsystype == 11))) {
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
      wcsel = "rename table " + wtable + "t0" + " to " + wtable + "t0" + MUtil.getToday("yyMMdd");
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
