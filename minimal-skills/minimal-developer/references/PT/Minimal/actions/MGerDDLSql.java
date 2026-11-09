package PT.Minimal.actions;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MUtil;
import java.util.Hashtable;
import java.util.Vector;

public class MGerDDLSql extends MGerDDL implements MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  PT.Minimal.geral.MForm minForm;
  Vector wrows;
  String EOL = "\n";
  private String wobj;
  private String wobjname;
  private String[] wargs;
  
  public MGerDDLSql() {}
  
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
    if (wstype.equals("rd")) {
      return get0TrigRDDDL(wobj);
    }
    if (wstype.equals("ri")) {
      return get0TrigRIDDL(wobj);
    }
    


    if (wstype.equals("ru")) {
      return get0TrigRUDDL(wobj);
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
          wddl = wddl + " char(" + wnchar + ") ";
          wallfd = wallfd + " char(" + wnchar + ")";
        }
        if (wc_te.equals("d")) {
          wddl = wddl + " char(10) ";
          wallfd = wallfd + " char(10)";
        }
        if (wc_te.equals("n")) {
          wddl = wddl + " decimal(" + wnchar + "," + wndec + ") ";
          wallfd = wallfd + " decimal(" + wnchar + "," + wndec + ")";
        }
        
        if (wc_te.equals("s")) {
          wddl = wddl + " decimal(32,0)";
          wallfd = wallfd + " decimal(32,0)";
        }
        



        if (wc_te.equals("i")) {
          wddl = wddl + " integer ";
          wallfd = wallfd + " integer";
        }
        
        if (wc_te.equals("t")) {
          wddl = wddl + " long";
          wallfd = wallfd + " long";
        }
        if (wc_te.equals("b")) {
          wddl = wddl + " image";
          wallfd = wallfd + " image";
        }
        


        wddl = wddl + EOL;
      }
    }
    if (wddl.trim().length() != 0) {
      wddl = "create table dbo." + wtable + "t0 (" + EOL + " " + wddl.substring(1) + ")" + EOL;
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
      windice = "create unique nonclustered index " + wtable + "i0 on " + wtable + "t0 (" + windice.substring(1) + ")" + EOL;
      

      if (wtstorage.trim().length() == 0) {}
    }
    

    return windice;
  }
  
  public String get0tableZKIndex(String wobj, String wtstorage) throws MError {
    String wtable = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wtable = wobj.substring(0, 4);
    } else {
      wtable = wobj.substring(0, 7);
    }
    String windice = "";
    windice = windice + "create unique nonclustered index " + wtable + "izk on " + wtable + "t0 (zk)" + EOL;
    
    if (wtstorage.trim().length() != 0) {}
    

    return windice;
  }
  
  public String getZkSequenceDDL(String wobj) {
    String wextgeral = "create table dbo.EEEEszk (" + EOL + "  zk int " + EOL + ") " + EOL;
    


    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7)).toLowerCase();
  }
  
  public String getSynonymDDL(String wobj)
  {
    String wextgeral = "create public synonym EEEE for EEEE " + EOL;
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7)).toLowerCase();
  }
  
  public String getGrantTableDDL(String wobj)
  {
    String wextgeral = "grant select, update, delete, insert on EEEE to USER_OF_MINIMAL " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.trim().toLowerCase());
  }
  






  public String getGrantProcedureDDL(String wobj)
  {
    String wextgeral = "grant execute on EEEE to USER_OF_MINIMAL " + EOL;
    return MUtil.stringRep(wextgeral, "EEEE", wobj.trim().toLowerCase());
  }
  






  public String get0synonymDDL(String wobj)
  {
    String wextgeral = "create public synonym EEEEt0 for EEEEt0 " + EOL;
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  






  public String getXtableDDL(String wobj, String wtstorage)
  {
    String wextgeral = "create table dbo.EEEEtx (" + EOL + "  zkent decimal(32,0) " + EOL + ", zcomp char(18) " + EOL + ", zchar char(100) " + EOL + ", zdec decimal(21,6) " + EOL + ")" + EOL;
    





    if ((wtstorage.trim().length() == 0) || (
    


      (!wobj.startsWith("w")) || (wobj.trim().length() < 7))) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getXsynonymDDL(String wobj)
  {
    String wextgeral = "create public synonym EEEEtx for EEEEtx " + EOL;
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getXtableIndex(String wobj, String wtstorage) throws MError
  {
    String wextgeral = "create unique nonclustered index EEEEix on EEEEtx (zkent,zcomp,zchar,zdec)" + EOL;
    if ((wtstorage.trim().length() == 0) || (
    


      (!wobj.startsWith("w")) || (wobj.trim().length() < 7))) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getXtableZKIndex(String wobj, String wtstorage) throws MError
  {
    String wextgeral = "create unique nonclustered index EEEEixk on EEEEtx (zk)" + EOL;
    if ((wtstorage.trim().length() == 0) || (
    


      (!wobj.startsWith("w")) || (wobj.trim().length() < 7))) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String getXZkSequenceDDL(String wobj)
  {
    String wextgeral = "create sequence EEEEsxzk " + EOL + "  INCREMENT BY 1 " + EOL + " START WITH 1 " + EOL + " NOMAXVALUE " + EOL + " NOCYCLE " + EOL + " NOCACHE " + EOL;
    





    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  

  public String getXTrigRIDDL(String wobj)
  {
    String wextgeral = "create or replace trigger dbo.EEEErxi BEFORE INSERT OR DELETE OR UPDATE on EEEEtx " + EOL + "for each row " + EOL + "  BEGIN " + EOL + "    if inserting then" + EOL + "      if :new.zk = null or :new.zk = 0 then" + EOL + "        select EEEEsxzk.NEXTVAL into :new.zk from dual;" + EOL + "      end if;" + EOL + "    end if;" + EOL + "  END;" + EOL;
    








    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0TrigRDDDL(String wobj)
    throws MError
  {
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wfields = getAllFields(wobj, "NO_DDL");
    String wfieldsx = MUtil.stringRep(wfields.substring(1), "FIELD.", " ");
    wfieldsx = MUtil.stringRep(wfieldsx.substring(1), " pi,", " pi,");
    String wfieldsy = MUtil.stringRep(wfields, "FIELD.", "@x");
    wfieldsy = MUtil.stringRep(wfieldsy.substring(1), " @xpi,", " @xpi,");
    
    String wextgeral = "create trigger dbo.EEEErd on EEEEt0 WITH ENCRYPTION FOR DELETE AS" + EOL + " " + EOL + "DECLARE " + EOL + MUtil.stringRep(wfieldsddl.substring(1), "FIELD.", "@x") + EOL + " " + EOL + "DECLARE xcursor CURSOR local FAST_FORWARD FOR " + EOL + "select " + EOL + wfieldsx + EOL + "from deleted " + EOL + "open xcursor " + EOL + "fetch next from  xcursor" + EOL + "into " + EOL + wfieldsy + EOL + " " + EOL + "WHILE @@FETCH_STATUS = 0" + EOL + "BEGIN " + EOL;
    















    String wwobj = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wwobj = wobj.substring(0, 4).toLowerCase();
    } else {
      wwobj = wobj.substring(0, 7).toLowerCase();
    }
    
    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "P0' and cstm='AM'").equals("0"))
    {

      wextgeral = wextgeral + "exec EEEEp0  'RD'" + EOL + MUtil.stringRep(wfields, "FIELD.", "@x") + EOL + MUtil.stringRep(wfields, "FIELD.", "@x") + EOL;
    }
    

    wextgeral = wextgeral + " delete from egbot0 where zkent = @xzk and zent = '" + wwobj.toUpperCase() + "';" + EOL + " delete from bbint0 where zkent = @xzk and zent = '" + wwobj.toUpperCase() + "';" + EOL + " delete from EEEEtx where zkent = @xzk;" + EOL + "fetch next from  xcursor" + EOL + "into " + EOL + wfieldsy + EOL + "END " + EOL + "CLOSE xcursor" + EOL + "DEALLOCATE xcursor " + EOL + " " + EOL;
    













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
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wfields = getAllFields(wobj, "NO_DDL");
    String wfieldsx = MUtil.stringRep(wfields.substring(1), "FIELD.", " ");
    wfieldsx = MUtil.stringRep(wfieldsx.substring(1), " pi,", " pi,");
    String wfieldsy = MUtil.stringRep(wfields, "FIELD.", "@y");
    wfieldsy = MUtil.stringRep(wfieldsy.substring(1), " @ypi,", " @ypi,");
    
    String wextgeral = "create trigger dbo.EEEEri on EEEEt0 WITH ENCRYPTION FOR INSERT AS" + EOL + " " + EOL + "DECLARE " + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + " @wzk decimal(32,0), " + EOL + MUtil.stringRep(wfieldsddl.substring(1), "FIELD.", "@y") + EOL + " " + EOL;
    }
    else
    {
      wextgeral = wextgeral + " @wzk decimal(32,0), @yzk decimal(32,0)  " + EOL;
    }
    wextgeral = wextgeral + "select @wzk = inserted.zk from inserted" + EOL + "if not @wzk > 0 " + EOL + "   BEGIN " + EOL + "   DECLARE zkcursor CURSOR local FOR " + EOL + "   select  zk from EEEEt0 where not zk > 0 for update of zk " + EOL + "   open zkcursor " + EOL + "   fetch next from  zkcursor into @yzk " + EOL + "   WHILE @@FETCH_STATUS = 0 " + EOL + "      BEGIN " + EOL + "      update EEEEszk set zk = zk + 1 " + EOL + "      if @@rowcount = 0 " + EOL + "         BEGIN " + EOL + "         insert into EEEEszk values (1) " + EOL + "         END " + EOL + "      update EEEEt0 set zk = (select dbo.mindbdef() + zk from EEEEszk) " + EOL + "      where current of zkcursor " + EOL + "      fetch next from  zkcursor into @yzk " + EOL + "      END " + EOL + "   END " + EOL + " " + EOL + "if @wzk > 0 " + EOL + "   BEGIN " + EOL;
    
























    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wobj.substring(0, 4).toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "   DECLARE ycursor CURSOR local FAST_FORWARD FOR " + EOL + "   select " + EOL + "   " + wfieldsx + EOL + "   from inserted " + EOL + "   open ycursor " + EOL + "   fetch next from  ycursor" + EOL + "   into " + EOL + "   " + wfieldsy + EOL + " " + EOL + "   WHILE @@FETCH_STATUS = 0" + EOL + "      BEGIN " + EOL + "      exec EEEEp0  'RI'" + EOL + "      " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "      " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "      fetch next from  ycursor" + EOL + "      into " + EOL + "      " + wfieldsy + EOL + "      END " + EOL + "   CLOSE ycursor" + EOL + "   DEALLOCATE ycursor " + EOL;
    }
    





















    wextgeral = wextgeral + "      update EEEEt0 set zutl = USER, zgrp = '0000', zdst = convert(char(10),getdate(),126), zhst = substring(convert(char(20),getdate(),126),12,8), zprc = 1 from inserted where EEEEt0.zk = inserted.zk" + EOL + " " + EOL + "   END " + EOL;
    



    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0TrigRIDDLold(String wobj) throws MError
  {
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wfields = getAllFields(wobj, "NO_DDL");
    String wfieldsx = MUtil.stringRep(wfields.substring(1), "FIELD.", " ");
    wfieldsx = MUtil.stringRep(wfieldsx.substring(1), " pi,", " pi,");
    String wfieldsy = MUtil.stringRep(wfields, "FIELD.", "@y");
    wfieldsy = MUtil.stringRep(wfieldsy.substring(1), " @ypi,", " @ypi,");
    
    String wextgeral = "create trigger dbo.EEEEri on EEEEt0 WITH ENCRYPTION FOR INSERT AS" + EOL + " " + EOL + "DECLARE " + EOL;
    

    String wwobj = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wwobj = wobj.substring(0, 4).toLowerCase();
    } else {
      wwobj = wobj.substring(0, 7).toLowerCase();
    }
    
    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + " @wzk decimal(32,0), " + EOL + MUtil.stringRep(wfieldsddl.substring(1), "FIELD.", "@y") + EOL + " " + EOL;
    }
    else
    {
      wextgeral = wextgeral + " @wzk decimal(32,0), @yzk decimal(32,0)  " + EOL;
    }
    wextgeral = wextgeral + "select @wzk = inserted.zk from inserted" + EOL + "if not @wzk > 0 " + EOL + "   BEGIN " + EOL + "   DECLARE zkcursor CURSOR local FOR " + EOL + "   select  zk from EEEEt0 where not zk > 0 for update of zk " + EOL + "   open zkcursor " + EOL + "   fetch next from  zkcursor into @yzk " + EOL + "   WHILE @@FETCH_STATUS = 0 " + EOL + "      BEGIN " + EOL + "      update EEEEszk set zk = zk + 1 " + EOL + "      if @@rowcount = 0 " + EOL + "         BEGIN " + EOL + "         insert into EEEEszk values (1) " + EOL + "         END " + EOL + "      update EEEEt0 set zk = (select dbo.mindbdef() + zk from EEEEszk) " + EOL + "      where current of zkcursor " + EOL + "      fetch next from  zkcursor into @yzk " + EOL + "      END " + EOL + "   END " + EOL + " " + EOL + "if @wzk > 0 " + EOL + "   BEGIN " + EOL;
    























    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "   DECLARE ycursor CURSOR local FAST_FORWARD FOR " + EOL + "   select " + EOL + "   " + wfieldsx + EOL + "   from inserted " + EOL + "   open ycursor " + EOL + "   fetch next from  ycursor" + EOL + "   into " + EOL + "   " + wfieldsy + EOL + " " + EOL + "   WHILE @@FETCH_STATUS = 0" + EOL + "      BEGIN " + EOL + "      exec EEEEp0  'RI'" + EOL + "      " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "      " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "      fetch next from  ycursor" + EOL + "      into " + EOL + "      " + wfieldsy + EOL + "      END " + EOL + "   CLOSE ycursor" + EOL + "   DEALLOCATE ycursor " + EOL;
    }
    





















    wextgeral = wextgeral + "      update EEEEt0 set zutl = USER, zgrp = '0000', zdst = convert(char(10),getdate(),126), zhst = substring(convert(char(20),getdate(),126),12,8) from inserted where EEEEt0.zk = inserted.zk" + EOL + " " + EOL + "   END " + EOL;
    



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
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wfields = getAllFields(wobj, "NO_DDL");
    String wfieldsx1 = MUtil.stringRep(wfields.substring(1), "FIELD.", "x.");
    wfieldsx1 = MUtil.stringRep(wfieldsx1.substring(1), " x.pi,", " x.pi,");
    String wfieldsx2 = MUtil.stringRep(wfields.substring(1), "FIELD.", "y.");
    wfieldsx2 = MUtil.stringRep(wfieldsx2.substring(1), " y.pi,", " y.pi,");
    String wfieldsy1 = MUtil.stringRep(wfields, "FIELD.", "@x");
    wfieldsy1 = MUtil.stringRep(wfieldsy1.substring(1), " @xpi,", " @xpi,");
    String wfieldsy2 = MUtil.stringRep(wfields, "FIELD.", "@y");
    wfieldsy2 = MUtil.stringRep(wfieldsy2.substring(1), " @ypi,", " @ypi,");
    
    String wextgeral = "create trigger dbo.EEEEru on EEEEt0 WITH ENCRYPTION FOR UPDATE AS " + EOL + " " + EOL;
    

    String wwobj = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wwobj = wobj.substring(0, 4).toLowerCase();
    } else {
      wwobj = wobj.substring(0, 7).toLowerCase();
    }
    
    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "DECLARE " + EOL + " @wzk decimal(32,0), " + EOL + MUtil.stringRep(wfieldsddl.substring(1), "FIELD.", "@x") + EOL + "DECLARE " + EOL + MUtil.stringRep(wfieldsddl.substring(1), "FIELD.", "@y") + EOL + " " + EOL + "select @wzk = deleted.zk from deleted" + EOL + "if not @wzk > 0 " + EOL + "   BEGIN " + EOL + "   DECLARE ycursor CURSOR local FAST_FORWARD FOR " + EOL + "   select " + EOL + "   " + wfieldsx2 + EOL + "   from inserted y" + EOL + "   where not y.zprc<0 " + EOL + "   open ycursor " + EOL + "   fetch next from  ycursor" + EOL + "   into " + EOL + "   " + wfieldsy2 + EOL + " " + EOL + "   WHILE @@FETCH_STATUS = 0" + EOL + "      BEGIN " + EOL + "      exec EEEEp0  'RI'" + EOL + "      " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "      " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "      fetch next from  ycursor" + EOL + "      into " + EOL + "      " + wfieldsy2 + EOL + "      END " + EOL + "   CLOSE ycursor" + EOL + "   DEALLOCATE ycursor " + EOL + " " + EOL + "      update EEEEt0 set zutl = USER, zgrp = '0000', zdst = convert(char(10),getdate(),126), zhst = substring(convert(char(20),getdate(),126),12,8), zprc = 1 from inserted where EEEEt0.zk = inserted.zk" + EOL + " " + EOL + "   END " + EOL + " " + EOL + "if @wzk > 0 " + EOL + "   BEGIN " + EOL + "DECLARE ycursor CURSOR local FAST_FORWARD FOR " + EOL + "select " + EOL + wfieldsx1 + EOL + "," + wfieldsx2 + EOL + "from deleted x, inserted y" + EOL + "where x.zk=y.zk and x.zprc>=1 " + EOL + "open ycursor " + EOL + "fetch next from  ycursor" + EOL + "into " + EOL + wfieldsy1 + EOL + "," + wfieldsy2 + EOL + " " + EOL + "WHILE @@FETCH_STATUS = 0" + EOL + "BEGIN " + EOL + "if @yzprc > 0 " + EOL + "   BEGIN" + EOL + "   exec EEEEp0  'RU'" + EOL + "   " + MUtil.stringRep(wfields, "FIELD.", "@x") + EOL + "   " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "   END " + EOL + "fetch next from  ycursor" + EOL + "into " + EOL + wfieldsy1 + EOL + "," + wfieldsy2 + EOL + "END " + EOL + "CLOSE ycursor" + EOL + "DEALLOCATE ycursor " + EOL + " " + EOL + "      select @yzprc = inserted.zprc from inserted" + EOL + "      if @yzprc <> -1 BEGIN update EEEEt0 set zutl = USER, zgrp = '0000', zdst = convert(char(10),getdate(),126), zhst = substring(convert(char(20),getdate(),126),12,8), zprc = 1 from inserted where EEEEt0.zk = inserted.zk END" + EOL + "      if @yzprc =  -1 BEGIN update EEEEt0 set zprc = 1 from inserted where EEEEt0.zk = inserted.zk END" + EOL + " " + EOL + "   END " + EOL;





































    }
    else
    {





































      wextgeral = wextgeral + "DECLARE " + EOL + " @wzk decimal(32,0), @yzprc decimal(1,0) " + EOL + " " + EOL + "select @wzk = deleted.zk from deleted" + EOL + "if @wzk > 0 " + EOL + "   BEGIN " + EOL + "      select @yzprc = inserted.zprc from inserted" + EOL + "      if @yzprc <> -1 BEGIN update EEEEt0 set zutl = USER, zgrp = '0000', zdst = convert(char(10),getdate(),126), zhst = substring(convert(char(20),getdate(),126),12,8), zprc = 1 from inserted where EEEEt0.zk = inserted.zk END" + EOL + "      if @yzprc =  -1 BEGIN update EEEEt0 set zprc = 1 from inserted where EEEEt0.zk = inserted.zk END" + EOL + " " + EOL + "   END " + EOL;
    }
    












    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  public String get0TrigRUDDLold(String wobj) throws MError
  {
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wfields = getAllFields(wobj, "NO_DDL");
    String wfieldsx1 = MUtil.stringRep(wfields.substring(1), "FIELD.", "x.");
    wfieldsx1 = MUtil.stringRep(wfieldsx1.substring(1), " x.pi,", " x.pi,");
    String wfieldsx2 = MUtil.stringRep(wfields.substring(1), "FIELD.", "y.");
    wfieldsx2 = MUtil.stringRep(wfieldsx2.substring(1), " y.pi,", " y.pi,");
    String wfieldsy1 = MUtil.stringRep(wfields, "FIELD.", "@x");
    wfieldsy1 = MUtil.stringRep(wfieldsy1.substring(1), " @xpi,", " @xpi,");
    String wfieldsy2 = MUtil.stringRep(wfields, "FIELD.", "@y");
    wfieldsy2 = MUtil.stringRep(wfieldsy2.substring(1), " @ypi,", " @ypi,");
    
    String wextgeral = "create trigger dbo.EEEEru on EEEEt0 WITH ENCRYPTION FOR UPDATE AS " + EOL + " " + EOL + "DECLARE " + EOL;
    


    String wwobj = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wwobj = wobj.substring(0, 4).toLowerCase();
    } else {
      wwobj = wobj.substring(0, 7).toLowerCase();
    }
    
    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + " @wzk decimal(32,0), " + EOL + MUtil.stringRep(wfieldsddl.substring(1), "FIELD.", "@x") + EOL + "DECLARE " + EOL + MUtil.stringRep(wfieldsddl.substring(1), "FIELD.", "@y") + EOL + " " + EOL;

    }
    else
    {

      wextgeral = wextgeral + " @wzk decimal(32,0) " + EOL;
    }
    
    wextgeral = wextgeral + "select @wzk = deleted.zk from deleted" + EOL + "if not @wzk > 0 " + EOL + "   BEGIN " + EOL;
    


    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "   DECLARE ycursor CURSOR local FAST_FORWARD FOR " + EOL + "   select " + EOL + "   " + wfieldsx2 + EOL + "   from inserted y" + EOL + "   open ycursor " + EOL + "   fetch next from  ycursor" + EOL + "   into " + EOL + "   " + wfieldsy2 + EOL + " " + EOL + "   WHILE @@FETCH_STATUS = 0" + EOL + "      BEGIN " + EOL + "      exec EEEEp0  'RI'" + EOL + "      " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "      " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "      fetch next from  ycursor" + EOL + "      into " + EOL + "      " + wfieldsy2 + EOL + "      END " + EOL + "   CLOSE ycursor" + EOL + "   DEALLOCATE ycursor " + EOL + " " + EOL;
    }
    


























    wextgeral = wextgeral + "      update EEEEt0 set zutl = USER, zgrp = '0000', zdst = convert(char(10),getdate(),126), zhst = substring(convert(char(20),getdate(),126),12,8) from inserted where EEEEt0.zk = inserted.zk" + EOL + " " + EOL + "   END " + EOL + " " + EOL + "if @wzk > 0 " + EOL + "   BEGIN " + EOL;
    





    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "DECLARE ycursor CURSOR local FAST_FORWARD FOR " + EOL + "select " + EOL + wfieldsx1 + EOL + "," + wfieldsx2 + EOL + "from deleted x, inserted y" + EOL + "where x.zk=y.zk " + EOL + "open ycursor " + EOL + "fetch next from  ycursor" + EOL + "into " + EOL + wfieldsy1 + EOL + "," + wfieldsy2 + EOL + " " + EOL + "WHILE @@FETCH_STATUS = 0" + EOL + "BEGIN " + EOL + "   BEGIN" + EOL + "   exec EEEEp0  'RU'" + EOL + "   " + MUtil.stringRep(wfields, "FIELD.", "@x") + EOL + "   " + MUtil.stringRep(wfields, "FIELD.", "@y") + EOL + "   END " + EOL + "fetch next from  ycursor" + EOL + "into " + EOL + wfieldsy1 + EOL + "," + wfieldsy2 + EOL + "END " + EOL + "CLOSE ycursor" + EOL + "DEALLOCATE ycursor " + EOL + " " + EOL;
    }
    



























    wextgeral = wextgeral + "      update EEEEt0 set zutl = USER, zgrp = '0000', zdst = convert(char(10),getdate(),126), zhst = substring(convert(char(20),getdate(),126),12,8) from inserted where EEEEt0.zk = inserted.zk" + EOL + " " + EOL + "END " + EOL;
    


    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  


  public String getZtableDDL(String wobj, String wtstorage)
  {
    String wextgeral = "create table EEEEtz (" + EOL + "  zkent decimal(32,0) " + EOL + ", zcmd char(18) " + EOL + ", zfase char(2) " + EOL + ", zctl char(20)" + EOL + ", zargs varchar(100) " + EOL + ", zcode integer" + EOL + ", zmsg varchar(100)" + EOL + ")" + EOL;
    









    if (wtstorage.trim().length() != 0) {
      wextgeral = wextgeral + " tablespace  \"" + wtstorage.toUpperCase() + "\"" + EOL;
    }
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7)).toLowerCase();
  }
  

  private String getZTrigDDL(String wobj)
  {
    String wextgeral = "create trigger EEEErz update of zcmd on EEEEtz " + EOL + "referencing new as a" + EOL + "for each row(" + EOL + "        execute procedure EEEEpz" + EOL + "        (a.zkent, a.zcmd, a.zfase, a.zctl, a.zargs, a.zcode, a.zmsg)" + EOL + "        into zctl, zcode, zmsg);" + EOL;
    





    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4)).toLowerCase();
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7)).toLowerCase();
  }
  
  private String getZProcDDL(String wobj, String wtext)
    throws MError
  {
    if (!existsZPRC(wobj)) {
      return getZProcDDLold(wobj, wtext);
    }
    if ((wtext != null) && (wtext.trim().length() != 0)) {
      String woldup = ", zhst = TO_CHAR(SYSDATE, 'HH24:MI:SS') ";
      String wnewup = ", zhst = TO_CHAR(SYSDATE, 'HH24:MI:SS'), zprc = -1 ";
      return MUtil.stringRep(wtext, woldup, wnewup);
    }
    String wextgeral = "create procedure dbo.EEEEpz" + EOL + "  @zkent decimal(32), @zcmd char(18), @zfase char(2)," + EOL + "  @zctl char(20) OUTPUT, @zargs varchar(100), @zcode integer OUTPUT," + EOL + "  @zmsg varchar(100) OUTPUT WITH ENCRYPTION AS" + EOL + " " + EOL;
    





    String wwobj = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wwobj = wobj.substring(0, 4).toLowerCase();
    } else {
      wwobj = wobj.substring(0, 7).toLowerCase();
    }
    
    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "PZ' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "-- SQL_DEFINE_BEGIN " + EOL + "  declare @wecode integer;" + EOL + "  declare @wisam integer;" + EOL + "  declare @wevar varchar(254);" + EOL + "  declare @wdfunc varchar(254);" + EOL + "-- SQL_DEFINE_END  " + EOL + "  /*on exception set wecode, wisam, wevar" + EOL + "    raise exception -746, wecode, ' '||wevar||'\\nEEEEpz:'||wecode||' '||wdfunc;" + EOL + "  end exception;*/" + EOL + "-- SQL_BODY_BEGIN " + EOL + "  set @wdfunc = ' ';" + EOL + "  set @wecode = 0; " + EOL + "  set @wisam = 0; " + EOL + "  set @wevar = ' ';" + EOL + "  -- Reflexo automático dos campos de controlo " + EOL + "  -- (Utilizador, Data, Hora) da operacao " + EOL + " " + EOL + "-- SQL_BODY_END " + EOL;
    }
    



























    wextgeral = wextgeral + "  select @zctl,  @zcode,   @zmsg" + EOL;
    

    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  private String getZProcDDLold(String wobj, String wtext)
    throws MError
  {
    if ((wtext != null) && (wtext.trim().length() != 0)) {
      return wtext;
    }
    String wextgeral = "create procedure dbo.EEEEpz" + EOL + "  @zkent decimal(32), @zcmd char(18), @zfase char(2)," + EOL + "  @zctl char(20) OUTPUT, @zargs varchar(100), @zcode integer OUTPUT," + EOL + "  @zmsg varchar(100) OUTPUT WITH ENCRYPTION AS" + EOL + " " + EOL;
    




    String wwobj = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wwobj = wobj.substring(0, 4).toLowerCase();
    } else {
      wwobj = wobj.substring(0, 7).toLowerCase();
    }
    
    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "PZ' and cstm='AM'").equals("0")) {
      wextgeral = wextgeral + "-- SQL_DEFINE_BEGIN " + EOL + "  declare @wecode integer;" + EOL + "  declare @wisam integer;" + EOL + "  declare @wevar varchar(254);" + EOL + "  declare @wdfunc varchar(254);" + EOL + "-- SQL_DEFINE_END  " + EOL + "  /*on exception set wecode, wisam, wevar" + EOL + "    raise exception -746, wecode, ' '||wevar||'\\nEEEEpz:'||wecode||' '||wdfunc;" + EOL + "  end exception;*/" + EOL + "-- SQL_BODY_BEGIN " + EOL + "  set @wdfunc = ' ';" + EOL + "  set @wecode = 0; " + EOL + "  set @wisam = 0; " + EOL + "  set @wevar = ' ';" + EOL + "  -- Reflexo automático dos campos de controlo " + EOL + "  -- (Utilizador, Data, Hora) da operacao " + EOL + " " + EOL + "-- SQL_BODY_END " + EOL;
    }
    



























    wextgeral = wextgeral + "  select @zctl,  @zcode,   @zmsg" + EOL;
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  

  public String get0ProcDDL(String wobj, String wtext)
    throws MError
  {
    String wtable = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wtable = wobj.substring(0, 4);
    } else {
      wtable = wobj.substring(0, 7);
    }
    
    String wfieldsddl = getAllFields(wobj, "WITH_DDL");
    String wextgeral = "@drop procedure dbo.EEEEp0" + EOL;
    
    String wwobj = "";
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      wwobj = wobj.substring(0, 4).toLowerCase();
    } else {
      wwobj = wobj.substring(0, 7).toLowerCase();
    }
    

    if (!MUtil.getSQLValue("select count(*) from sdobt0 where idob = '" + wwobj.toUpperCase() + "P0' and cstm='AM'").equals("0")) {
      wextgeral = "create procedure dbo.EEEEp0" + EOL + "        @zfase char(2)" + EOL + MUtil.stringRep(wfieldsddl, "FIELD.", "@x") + EOL + MUtil.stringRep(wfieldsddl, "FIELD.", "@y") + EOL + "          WITH ENCRYPTION AS" + EOL + "-- end create procedure" + EOL + "-- SQL_DEFINE_BEGIN" + EOL + "  declare @wecode integer;" + EOL + "  declare @wisam integer;" + EOL + "  declare @wevar varchar(254);" + EOL + "  declare @wdfunc varchar(254);" + EOL + "-- SQL_DEFINE_END" + EOL + "  /**on exception set wecode, wisam, wevar" + EOL + "    raise exception -746, wecode, ' '||wevar||'\\nEEEEp0:'||wecode||' '||wdfunc;" + EOL + "  end exception;*/" + EOL + "-- SQL_BODY_BEGIN" + EOL + "  set @wdfunc = ' ';" + EOL + "  set @wecode = 0; " + EOL + "  set @wisam = 0; " + EOL + "  set @wevar = ' ';" + EOL + "-- SQL_BODY_END" + EOL;
      




















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
  


  public String getCRZKCommandDML(String wobj)
  {
    String wextgeral = "insert into EEEEszk select dbo.minZkInt(zk) from EEEEt0 where zk = (select max(zk) from EEEEt0 where zk between dbo.minDBDef() and dbo.minDBMax()) ;" + EOL;
    
    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  
  private String getKProcDDL(String wobj)
    throws MError
  {
    String wextgeral = "create procedure dbo.EEEEpk  " + EOL + "WITH ENCRYPTION AS " + EOL + "update EEEEszk set zk = zk + 1 " + EOL + "if @@rowcount = 0 " + EOL + "  insert into EEEEszk values (1) " + EOL + "select dbo.mindbdef() + zk as zk from EEEEszk " + EOL;
    






    if ((!wobj.startsWith("w")) || (wobj.trim().length() < 7)) {
      return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 4).toLowerCase());
    }
    return MUtil.stringRep(wextgeral, "EEEE", wobj.substring(0, 7).toLowerCase());
  }
  


  public boolean existsTable(String wtable, MCursor wcurs)
    throws MError
  {
    boolean wboo = false;
    String wcsel = " select name from sysobjects  where name = '" + wtable.toLowerCase() + "'";
    
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
    String wcsel = " select b.keyno,c.name as column_name, d.name as tab_name  from sysindexes a, sysindexkeys b, syscolumns c, sysobjects d where a.name = '" + windex.toLowerCase() + "' " + " and a.id = b.id and a.indid = b.indid " + " and b.id = c.id and b.colid = c.colid " + " and b.id = d.id " + " order by b.keyno";
    





    System.out.println(wcsel);
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
            String wsystable = (String)wsysindex.get("tab_name");
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
    String wcsel = " select a.type, a.colid, isnull(a.length,0) as length, isnull(a.xprec,0) as xprec, isnull(a.xscale,0) as xscale from syscolumns a, sysobjects b where b.name = '" + wtable.toLowerCase() + "'" + " and a.name = '" + wcolumn.toLowerCase() + "'" + " and a.id=b.id ";
    







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
      
      int wsysncol = new Integer((String)wsyscol.get("colid")).intValue();
      if ((wncol != 0) && (wsysncol != wncol)) {
        System.out.println("ExistsColumn OutOFOrder " + wcolumn + " - " + wboo);
        return false;
      }
      if (wtype.trim().length() == 0) {
        return true;
      }
      String wsystype = (String)wsyscol.get("type");
      
      int wsysnchar = new Integer((String)wsyscol.get("length")).intValue();
      int wsysprecision = 0;
      int wsysscale = 0;
      if (wsystype.equals("106")) {
        wsysprecision = new Integer((String)wsyscol.get("xprec")).intValue();
        wsysscale = new Integer((String)wsyscol.get("xscale")).intValue();
      }
      

      if ((wtype.toLowerCase().equals("c")) && (wsystype.equals("39")) && 
        (wsysnchar == wnchar)) {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("d")) && (wsystype.equals("39")) && 
        (wsysnchar >= wnchar)) {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("s")) && (wsystype.equals("106"))) {
        wboo = true;
      }
      
      if ((wtype.toLowerCase().equals("n")) && (wsystype.equals("106")))
      {
        if ((wsysprecision == wnchar) && (wsysscale == wndec)) {
          wboo = true;
        }
      }
      
      if ((wtype.toLowerCase().equals("i")) && (wsystype.equals("38"))) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("t")) && (wsystype.equals("34"))) {
        wboo = true;
      }
      if ((wtype.toLowerCase().equals("b")) && (wsystype.equals("34"))) {
        wboo = true;
      }
    }
    if (!wboo) {
      System.out.println("ExistsColumn " + wcolumn + " - " + wtype + " / " + "wsystype" + " / " + wboo);
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
      String wcsel = " select name from sysobjects  where name = '" + wobj.toUpperCase() + "'";
      


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
      String wcsel = "exec sp_rename " + wtable + "t0, " + "   " + wtable + "t0" + MUtil.getToday("yyMMdd");
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
        if (existsColumn(woldtable, wi_comp, false, wc_te, wnchar, wndec, sdob_curs)) {
          if (withTrim) {
            if ((wc_te.equals("c")) || ((wc_te.equals("d")) && (withTrim))) {
              wallfields = wallfields + ", isnull(rtrim(" + wi_comp + "),' ')";
            } else {
              wallfields = wallfields + ", isnull(" + wi_comp + ",0)";
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
  
  public String getAllFields(String wobj, String wtype)
    throws MError
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
