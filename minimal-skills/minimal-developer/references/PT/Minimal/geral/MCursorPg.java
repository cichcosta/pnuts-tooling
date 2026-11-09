package PT.Minimal.geral;

import PT.Minimal.geral.console.MConsole;
import PT.Minimal.util.MString;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class MCursorPg extends MCursor
{
  public MCursorPg(MConnection wconn) throws MError
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
    




    wcsel = MUtil.stringRep(wcsel, "nvl(", "coalesce(");
    wcsel = MUtil.stringRep(wcsel, "= USER", "=lower(USER)");
    wcsel = MUtil.stringRep(wcsel, "=USER", "=lower(USER)");
    


    wcsel = MUtil.stringRep(wcsel, "from dual", " ");
    wcsel = MUtil.stringRep(wcsel, "outer", " ");
    wcsel = MUtil.stringRep(wcsel, "OUTER", " ");
    wcsel = MUtil.stringRep(wcsel, "''", "' '");
    wcsel = MUtil.stringRep(wcsel, "(+)", " ");
    
    return wcsel;
  }
  
  public void checkTriggers(String went) throws MError {
    System.out.println("checkTriggers MCursor em Oracle" + went);
  }
  








  public String getKey(String went, String wfase, String wzk)
    throws MError
  {
    if (wfase.equalsIgnoreCase("BF")) {
      String wcsel = "select " + went.substring(0, 4) + "pk()  from dual";
      try {
        execQuery(wcsel);
        wzk = get(1);
      } finally {
        closeStatement();
      }
    }
    return wzk;
  }
  
  public MHashtable setZCommand(String wcommand, String wfase, String went, String wargs, String wzk) throws MError { String wname = went.substring(0, 4) + "PZ";
    if (MConsole.getTraceSql()) {
      System.out.println("call " + wname + "(" + wzk + ", '" + wcommand + "', '" + wfase + "', ' ', '" + wargs + "', 0, ' '");
    }
    String wcsel = "{call " + wname + "(?,?,?,?,?,?, ?) }";
    if (minConnection.getAmbient().toUpperCase().startsWith("POSTGRESQL")) {
      wcsel = "{? = call " + wname + "(?,?,?,?,?,?, ?) }";
    }
    


    MHashtable wzr = new MHashtable();
    wzr.setName(wname);
    try
    {
      CallableStatement wcs = prepareCall(wcsel);
      
      setPreparedPar(2, new BigDecimal(wzk));
      setPreparedPar(3, wcommand);
      setPreparedPar(4, wfase);
      setPreparedPar(5, " ");
      setPreparedPar(6, wargs);
      setPreparedPar(7, 0);
      setPreparedPar(8, " ");
      
      registerOutParameter(1, 4);
      

































      executeP(wcs);
      
      if (getWdcode() == 100) {
        throw new MError("MIN-999999E");
      }
      

      String wzcode = String.valueOf(wcs.getInt(1));
      System.out.println(wzcode);
      

      wzr.put("zctl", " ");
      wzr.put("zcode", wzcode);
      wzr.put("zmsg", " ");
    }
    catch (SQLException ex)
    {
      minSQLException = ex;
      minZstaf();
    } finally {
      closePreparedStatement();
      closeStatement();
    }
    return wzr;
  }
  
  public boolean executeP(CallableStatement wcs) {
    try {
      wdcode = 0;
      return wcs.execute();
    }
    catch (SQLException ex)
    {
      throw new MError(ex, "PostgreSQL");
    }
  }
  





  public void lockRows(String wcsel)
    throws MError
  {}
  




  public void lockTable(String wtable)
    throws MError
  {
    try
    {
      String wcsel = "lock table " + wtable + " in exclusive mode ";
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
        wcsel = "alter table " + wtable + " enable all triggers";
      } else {
        wcsel = "alter table " + wtable + " disable all triggers";
      }
      execStatement(wcsel);
    } finally {
      closeStatement();
    }
  }
  
  public String[] getArrayOfReplicationTableNames(String owner) throws MError
  {
    String wcsel = " select view_name from all_views  where owner= '" + owner.toUpperCase() + "'" + " and substr(view_name,5,1) = 'T'";
    


    return getColumnAsArray(wcsel, 1);
  }
  

  public String[] getArrayOfTableNames(String owner)
    throws MError
  {
    String wcsel = " select object_name from all_objects  where owner= '" + owner.toUpperCase() + "'" + " and (object_type = 'TABLE' or object_type = 'VIEW' and substr(object_name,5,1) = 'T')";
    


    return getColumnAsArray(wcsel, 1);
  }
  

  public String[] getArrayOfIndexesNames(String owner)
    throws MError
  {
    String wcsel = " select index_name from all_indexes  where owner = '" + owner.toUpperCase() + "'";
    

    return getColumnAsArray(wcsel, 1);
  }
  

  public String[] getArrayOfTriggersNames(String owner)
    throws MError
  {
    String wcsel = " select trigger_name from all_triggers  where owner = '" + owner.toUpperCase() + "'";
    

    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfProceduresNames(String owner)
    throws MError
  {
    String wcsel = " select object_name from all_objects  where owner = '" + owner.toUpperCase() + "'" + " and object_type in ('PACKAGE','PROCEDURE', 'FUNCTION')";
    


    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfSequencesNames(String owner)
    throws MError
  {
    String wcsel = " select object_name from all_objects  where owner = '" + owner.toUpperCase() + "'" + " and object_type = 'SEQUENCE'";
    


    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfSynonymsNames(String owner)
    throws MError
  {
    String wcsel = " select object_name from all_objects  where owner = 'PUBLIC' and object_type = 'SYNONYM'";
    


    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfGrantsNames(String owner)
    throws MError
  {
    String wcsel = " select distinct(table_name) from ALL_TAB_PRIVS  where GRANTOR = '" + owner.toUpperCase() + "'" + " and GRANTEE = 'USER_OF_MINIMAL'";
    


    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfInvalidNames(String owner)
    throws MError
  {
    String wcsel = " select object_name from all_objects  where owner = '" + owner.toUpperCase() + "'" + " and status = 'INVALID'";
    


    return getColumnAsArray(wcsel, 1);
  }
  
  public int execStatement(String wcsel, boolean doCheck) throws MError
  {
    Statement wstat = null;
    i = -1;
    if (doCheck) {
      if (MConsole.getTraceSql()) System.out.println("!*** MCursorOra.execStatement doCheck 1: " + minWcsel);
      minWcsel = checkSQL(wcsel);
      if ((MConsole.getTraceSql()) && (!wcsel.equals(minWcsel))) System.out.println("MCursorPg.execStatement doCheck 2: " + minWcsel);
    } else {
      minWcsel = wcsel;
    }
    try
    {
      wstat = minConnection.getConnection().createStatement();
      i = wstat.executeUpdate(minWcsel);
      if (MConsole.getTraceSql()) { System.out.println("!*** MCursorPg.execStatement executeUpdate " + i + " " + minWcsel);
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
  
  public int execPreparedUpdate() throws MError
  {
    int qcode = -1;
    try
    {
      if (MConsole.getTraceSql()) { System.out.println("!*** MCursorPg.execPreparedUpdate: " + minWcsel);
      }
      qcode = minPreparedStatement.executeUpdate();
      

      if (MConsole.getTraceSql()) System.out.println("!*** MCursorPg.execPreparedUpdate qcode: " + qcode);
      if (MConsole.getTraceSql()) System.out.println("!*** MCursorpg.execPreparedUpdate: " + qcode + " linhas.");
    } catch (SQLException ex) {
      minSQLException = ex;
      System.out.println("!*** MCursorPg.execPreparedUpdate ex: " + ex);
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
          obj = ((BigDecimal)obj).setScale(17);
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
          obj = ((BigDecimal)obj).setScale(17);
        }
      }
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return obj;
  }
}
