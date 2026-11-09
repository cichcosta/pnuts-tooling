package PT.Minimal.geral;

import PT.Minimal.geral.console.MConsole;
import PT.Minimal.util.MString;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class MCursorSql extends MCursor
{
  public MCursorSql(MConnection wconn) throws MError
  {
    minConnection = wconn;
  }
  



  public String checkSQL(String wcsel)
  {
    int wend;
    

    for (int i = wcsel.indexOf("["); i != -1; i = wcsel.indexOf("[", wend)) {
      wend = wcsel.indexOf("]", i);
      if (wend == -1) break;
      for (int waux = i - 1; waux > 0; waux--) {
        char ch = wcsel.charAt(waux);
        if ((!Character.isLetterOrDigit(ch)) && (ch != '_') && (ch != '.')) {
          break;
        }
      }
      
      String[] wparameters = MUtil.getArgs(wcsel.substring(i + 1, wend), ',');
      
      if ((wparameters.length == 0) || (wparameters.length > 2)) break;
      int wsend;
      int wsini;
      if (wparameters.length == 1) {
        int wsini = Integer.valueOf(wparameters[0].trim()).intValue();
        wsend = wsini;
      } else {
        wsini = Integer.valueOf(wparameters[0].trim()).intValue();
        wsend = Integer.valueOf(wparameters[1].trim()).intValue();
      }
      String wvar = wcsel.substring(waux + 1, i);
      int wsend = wsend - wsini + 1;
      wcsel = wcsel.substring(0, waux + 1) + "substr(" + wvar + "," + wsini + "," + wsend + ")" + wcsel.substring(wend + 1);
    }
    


    wcsel = MUtil.stringRep(wcsel, " outer ", " ");
    wcsel = MUtil.stringRep(wcsel, " OUTER ", " ");
    wcsel = MUtil.stringRep(wcsel, "''", "' '");
    

    wcsel = MUtil.stringRep(wcsel, "(+)", " ");
    wcsel = MUtil.stringRep(wcsel, "MINVARIABLES.", " ");
    wcsel = MUtil.stringRep(wcsel, "''", "' '");
    wcsel = MUtil.stringRep(wcsel, "||", "+");
    wcsel = MUtil.stringRep(wcsel, "substr(", "substring(");
    
    wcsel = MUtil.stringRep(wcsel, "nvl", "isnull");
    wcsel = MUtil.stringRep(wcsel, "today()", "convert(char(10),getdate(),126)");
    wcsel = MUtil.stringRep(wcsel, "today", "convert(char(10),getdate(),126)");
    wcsel = MUtil.stringRep(wcsel, "time()", "substring(convert(char(20),getdate(),126),12,8)");
    wcsel = MUtil.stringRep(wcsel, "current hour to second", "substring(convert(char(20),getdate(),126),12,8)");
    
    wcsel = MUtil.stringRep(wcsel, "bconxn(", "dbo.bconxn(");
    wcsel = MUtil.stringRep(wcsel, "bctxxn(", "dbo.bctxxn(");
    wcsel = MUtil.stringRep(wcsel, "bentxn(", "dbo.bentxn(");
    wcsel = MUtil.stringRep(wcsel, "bpsrxn(", "dbo.bpsrxn(");
    wcsel = MUtil.stringRep(wcsel, "bproxn(", "dbo.bproxn(");
    wcsel = MUtil.stringRep(wcsel, "hcrmxn(", "dbo.hcrmxn(");
    wcsel = MUtil.stringRep(wcsel, "pnavxn(", "dbo.pnavxn(");
    wcsel = MUtil.stringRep(wcsel, "cidaxv(", "dbo.cidaxv(");
    
    wcsel = MUtil.stringRep(wcsel, "wconxn(", "dbo.wconxn(");
    wcsel = MUtil.stringRep(wcsel, "wctxxn(", "dbo.wctxxn(");
    wcsel = MUtil.stringRep(wcsel, "wentxn(", "dbo.wentxn(");
    wcsel = MUtil.stringRep(wcsel, "wpsrxn(", "dbo.wpsrxn(");
    wcsel = MUtil.stringRep(wcsel, "wproxn(", "dbo.wproxn(");
    wcsel = MUtil.stringRep(wcsel, "wcrmxn(", "dbo.wcrmxn(");
    

    wcsel = MUtil.stringRep(wcsel, "imptxk(", "dbo.imptxk(");
    wcsel = MUtil.stringRep(wcsel, "instr(", "dbo.instr(");
    wcsel = MUtil.stringRep(wcsel, "jfptxn(", "dbo.jfptxn(");
    wcsel = MUtil.stringRep(wcsel, "minadddays(", "dbo.minadddays(");
    wcsel = MUtil.stringRep(wcsel, "minaddmonths(", "dbo.minaddmonths(");
    wcsel = MUtil.stringRep(wcsel, "minaddyears(", "dbo.minaddyears(");
    wcsel = MUtil.stringRep(wcsel, "mindiffdays(", "dbo.mindiffdays(");
    wcsel = MUtil.stringRep(wcsel, "minamort(", "dbo.minamort(");
    wcsel = MUtil.stringRep(wcsel, "minianoctb(", "dbo.minianoctb(");
    wcsel = MUtil.stringRep(wcsel, "minicon(", "dbo.minicon(");
    wcsel = MUtil.stringRep(wcsel, "mincvm(", "dbo.mincvm(");
    wcsel = MUtil.stringRep(wcsel, "mingetnct(", "dbo.mingetnct(");
    wcsel = MUtil.stringRep(wcsel, "minixn(", "dbo.minixn(");
    wcsel = MUtil.stringRep(wcsel, "minncon(", "dbo.minncon(");
    wcsel = MUtil.stringRep(wcsel, "minround(", "dbo.minround(");
    wcsel = MUtil.stringRep(wcsel, "length(", "len(");
    wcsel = MUtil.stringRep(wcsel, "to_date(", "dbo.to_date(");
    wcsel = MUtil.stringRep(wcsel, "to_char(", "dbo.to_char(");
    wcsel = MUtil.stringRep(wcsel, "to_number(", "dbo.to_number(");
    wcsel = MUtil.stringRep(wcsel, "trim(", "rtrim(");
    wcsel = MUtil.stringRep(wcsel, "rrtrim(", "rtrim(");
    wcsel = MUtil.stringRep(wcsel, "lpad(", "dbo.lpad(");
    wcsel = MUtil.stringRep(wcsel, "dbo.dbo.", "dbo.");
    
    return wcsel;
  }
  
  public void checkTriggers(String went) throws MError {
    System.out.println("!*** MCursorSql checkTriggers MCursor " + went);
    wdcode = 0;
    String wcsel = " select count(*) as ntriggers from sysobjstate  where objtype in ('T','I') and state = 'E' and name[5,7] in ('ri','ru','rd','izk') and name like '" + went.substring(0, 4).toLowerCase() + "%'";
    




    try
    {
      execQuery(wcsel);
      if (getWdcode() != 0) {
        System.out.println("!*** MCursorSql: " + wcsel);
        System.out.println("!*** MCursorSql wdcode:" + wdcode);
        throw new MError("MIN-900001E");
      }
      int wntriggers = getInt(1);
      if (wntriggers < 4) {
        throw new MError("MIN-900001E");
      }
    } finally {
      closeStatement();
    }
  }
  






  public String getKey(String went, String wfase, String wzk)
    throws MError
  {
    if (wfase.equalsIgnoreCase("BF"))
    {


      String wcsel = "{call dbo." + went.substring(0, 4).toLowerCase() + "pk}";
      try {
        prepareCall(wcsel);
        execCall();
        wzk = get(1);
        if (MConsole.getTraceSql()) System.out.println("!*** MCursorSql getzk: " + wzk + " " + wcsel);
      } finally {
        closePreparedStatement();
        closeStatement();
      }
    }
    



    return wzk;
  }
  
  public MHashtable setZCommand(String wcommand, String wfase, String went, String wargs, String wzk) throws MError
  {
    String wname = (went.substring(0, 4) + "PZ").toLowerCase();
    if (MConsole.getTraceSql()) {
      System.out.println("!*** MCursorSql call " + wname + "(" + wzk + ", '" + wcommand + "', '" + wfase + "', ' ', '" + wargs + "', 0, ' '");
    }
    

    String wcsel = "execute procedure " + wname + "(?,?,?,?,?,?, ?)";
    

    MHashtable wzr = new MHashtable();
    wzr.setName(wname);
    try {
      wcsel = MUtil.stringRep(wcsel, "execute", "exec");
      wcsel = MUtil.stringRep(wcsel, "procedure", " ");
      wcsel = MUtil.stringRep(wcsel, "(", " ");
      wcsel = MUtil.stringRep(wcsel, ")", " ");
      

      prepareCall(wcsel);
      
      setPreparedPar(1, new BigDecimal(wzk));
      setPreparedPar(2, wcommand);
      setPreparedPar(3, wfase);
      setPreparedPar(4, " ");
      setPreparedPar(5, wargs);
      setPreparedPar(6, 0);
      setPreparedPar(7, " ");
      





      execCall();
      if (getWdcode() == 100) {
        throw new MError("MIN-999999E");
      }
      
      wzr.put("zctl", get(1));
      wzr.put("zcode", get(2));
      wzr.put("zmsg", get(3));
    }
    finally
    {
      closePreparedStatement();
      closeStatement();
    }
    
    return wzr;
  }
  




  public void lockRows(String wcsel)
    throws MError
  {}
  




  public void lockTable(String wtable)
    throws MError
  {
    try
    {
      String wcsel = "lock table " + wtable + " in exclusive mode";
      execStatement(wcsel);
    } finally {
      closeStatement();
    }
  }
  
  public void setTriggersEnabled(String wtable, boolean wmode) throws MError {
    try {
      String wcsel;
      String wcsel;
      if (wmode) {
        wcsel = "alter table " + wtable + " enable trigger all ";
      } else {
        wcsel = "alter table " + wtable + " disable trigger all ";
      }
      execStatement(wcsel);
    } finally {
      closeStatement();
    }
  }
  
  public String[] getArrayOfReplicationTableNames(String owner) throws MError {
    return null;
  }
  
  public String[] getArrayOfTableNames(String owner) throws MError {
    String wcsel = " select upper(name) as tabname from sysobjects where xtype in ('U','V') order by tabname ";
    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfIndexesNames(String owner) throws MError
  {
    String wcsel = " select upper(idxname) from sysindexes  where owner = '" + owner + "'" + " and idxname[1] between 'a' and 'z'";
    


    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfTriggersNames(String owner)
    throws MError
  {
    String wcsel = " select upper(trigname) from systriggers  where owner = '" + owner + "'";
    

    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfProceduresNames(String owner)
    throws MError
  {
    String wcsel = " select upper(procname) from sysprocedures  where owner = '" + owner + "'";
    

    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfSequencesNames(String owner)
    throws MError
  {
    return null;
  }
  
  public String[] getArrayOfSynonymsNames(String owner)
    throws MError
  {
    String wcsel = " select upper(synname) from syssynonyms  where owner = '" + owner + "'";
    

    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfGrantsNames(String owner)
    throws MError
  {
    return null;
  }
  
  public String[] getArrayOfInvalidNames(String owner)
    throws MError
  {
    String wcsel = " select upper(name) from sysobjstate  where owner = '" + owner + "'" + " and state <> 'E'";
    


    return getColumnAsArray(wcsel, 1);
  }
  
  public int execStatement(String wcsel, boolean doCheck) throws MError
  {
    Statement wstat = null;
    i = 0;
    int iUpdCount = -1;
    boolean bMoreResults = true;
    if (doCheck) {
      if (MConsole.getTraceSql()) System.out.println("!*** MCursorSql.execStatement doCheck 1: " + minWcsel);
      minWcsel = checkSQL(wcsel);
      if ((MConsole.getTraceSql()) && (!wcsel.equals(minWcsel))) System.out.println("MCursorSql.execStatement doCheck 2: " + minWcsel);
    } else {
      minWcsel = wcsel;
    }
    try
    {
      wstat = minConnection.getConnection().createStatement();
      wstat.execute(minWcsel);
      iUpdCount = wstat.getUpdateCount();
      
      if (MConsole.getTraceSql()) { System.out.println("!*** MCursorSql.execStatement getUpdate " + iUpdCount);
      }
      while ((bMoreResults) || (iUpdCount != -1)) {
        i = iUpdCount;
        bMoreResults = wstat.getMoreResults();
        iUpdCount = wstat.getUpdateCount();
        if (MConsole.getTraceSql()) { System.out.println("!*** MCursorSql.execStatement getUpdateCount " + iUpdCount + " " + bMoreResults);
        }
      }
      if (MConsole.getTraceSql()) { System.out.println("!*** MCursorSql.execStatement executeUpdate " + i + " " + minWcsel);
      }
      













      return i;
    }
    catch (SQLException ex)
    {
      minSQLException = ex;
      minZstaf();
    } finally {
      try {
        if (wstat != null) {
          wstat.close();
          wstat = null;
        }
      } catch (SQLException ex) {
        minSQLException = ex;
        minZstaf();
      }
    }
  }
  
  public int execPreparedUpdate()
    throws MError
  {
    int qcode = -1;
    int iUpdCount = -1;
    boolean bMoreResults = true;
    try {
      if (MConsole.getTraceSql()) System.out.println("!*** MCursorSql.execPreparedUpdate: " + minWcsel);
      minPreparedStatement.execute();
      iUpdCount = minPreparedStatement.getUpdateCount();
      
      if (MConsole.getTraceSql()) { System.out.println("!*** MCursorSql.execPreparedUpdate getUpdate " + iUpdCount);
      }
      while ((bMoreResults) || (iUpdCount != -1)) {
        qcode = iUpdCount;
        bMoreResults = minPreparedStatement.getMoreResults();
        iUpdCount = minPreparedStatement.getUpdateCount();
        if (MConsole.getTraceSql()) { System.out.println("!*** MCursorSql.execPreparedUpdate getUpdateCount " + iUpdCount + " " + bMoreResults);
        }
      }
      if (MConsole.getTraceSql()) System.out.println("!*** MCursorSql.execPreparedUpdate qcode: " + qcode);
      if (MConsole.getTraceSql()) System.out.println("!*** MCursorSql.execPreparedUpdate: " + qcode + " linhas.");
    } catch (SQLException ex) {
      minSQLException = ex;
      System.out.println("!*** MCursorSql.execPreparedUpdate ex: " + ex);
      minZstaf();
    }
    



    return qcode;
  }
  

  public Object getObject(int n)
    throws MError
  {
    Object obj = null;
    try {
      obj = minResultSet.getObject(n);
      if (obj == null) {
        obj = getDefaultObject(n);
      } else if ((obj instanceof String)) {
        obj = MString.trimRight((String)obj);
      } else if ((obj instanceof BigDecimal)) {
        int s = ((BigDecimal)obj).scale();
        int sx = minRSMD.getScale(n);
        if (s < sx) {
          obj = ((BigDecimal)obj).setScale(sx);
        }
      }
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return obj;
  }
  

  public Object getObject(String name)
    throws MError
  {
    Object obj = null;
    try {
      obj = minResultSet.getObject(name);
      if (obj == null) {
        obj = getDefaultObject(getColumnPosition(name));
      } else if ((obj instanceof String)) {
        obj = MString.trimRight((String)obj);
      } else if ((obj instanceof BigDecimal)) {
        int s = ((BigDecimal)obj).scale();
        int sx = minRSMD.getScale(getColumnPosition(name));
        if (s < sx) {
          obj = ((BigDecimal)obj).setScale(sx);
        }
      }
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return obj;
  }
}
