package PT.Minimal.geral;

import PT.Minimal.geral.console.MConsole;
import PT.Minimal.util.MString;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class MCursorInf extends MCursor
{
  public MCursorInf(MConnection wconn) throws MError
  {
    minConnection = wconn;
  }
  
  public String checkSQL(String wcsel) {
    wcsel = MUtil.stringRep(wcsel, "(+)", " ");
    wcsel = MUtil.stringRep(wcsel, "MINVARIABLES.", " ");
    wcsel = MUtil.stringRep(wcsel, "''", "' '");
    return wcsel;
  }
  
  public void checkTriggers(String went) throws MError {
    System.out.println("checkTriggers MCursor " + went);
    wdcode = 0;
    String wcsel = " select count(*) as ntriggers from sysobjstate  where objtype in ('T','I') and state = 'E' and name[5,7] in ('ri','ru','rd','izk') and name like '" + went.substring(0, 4).toLowerCase() + "%'";
    




    try
    {
      execQuery(wcsel);
      if (getWdcode() != 0) {
        System.out.println(wcsel);
        System.out.println("wdcode:" + wdcode);
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


      String wcsel = "{call " + went.substring(0, 4) + "pk()}";
      try {
        prepareCall(wcsel);
        execCall();
        wzk = get(1);
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
      System.out.println("call " + wname + "(" + wzk + ", '" + wcommand + "', '" + wfase + "', ' ', '" + wargs + "', 0, ' '");
    }
    String wcsel = "execute procedure " + wname + "(?,?,?,' ',?,0,' ')";
    
    MHashtable wzr = new MHashtable();
    wzr.setName(wname);
    try {
      prepareCall(wcsel);
      
      setPreparedPar(1, new BigDecimal(wzk));
      setPreparedPar(2, wcommand);
      setPreparedPar(3, wfase);
      setPreparedPar(4, wargs);
      





      execCall();
      if (getWdcode() == 100) {
        throw new MError("MIN-999999E");
      }
      
      wzr.put("zctl", get(1));
      wzr.put("zcode", get(2));
      wzr.put("zmsg", get(3));
    }
    finally {
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
        wcsel = "set triggers for " + wtable + " enabled";
      } else {
        wcsel = "set triggers for " + wtable + " disabled";
      }
      execStatement(wcsel);
    } finally {
      closeStatement();
    }
  }
  
  public String[] getArrayOfReplicationTableNames(String owner) throws MError {
    return null;
  }
  
  public String[] getArrayOfTableNames(String owner) throws MError
  {
    String wcsel = " select upper(tabname) from systables  where owner = '" + owner + "'";
    

    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfIndexesNames(String owner)
    throws MError
  {
    String wcsel = " select upper(idxname) from sysindexes  where owner = '" + owner + "'" + " and idxname[1] between 'a' and 'z'";
    


    return getColumnAsArray(wcsel, 1);
  }
  
  public String[] getArrayOfTriggersNames(String owner) throws MError
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
  
  public int execStatement(String wcsel, boolean doCheck)
    throws MError
  {
    Statement wstat = null;
    i = -1;
    if (doCheck) {
      if (MConsole.getTraceSql()) System.out.println("!*** MCursorinf.execStatement doCheck 1: " + minWcsel);
      minWcsel = checkSQL(wcsel);
      if ((MConsole.getTraceSql()) && (!wcsel.equals(minWcsel))) System.out.println("MCursorinf.execStatement doCheck 2: " + minWcsel);
    } else {
      minWcsel = wcsel;
    }
    try
    {
      wstat = minConnection.getConnection().createStatement();
      i = wstat.executeUpdate(minWcsel);
      if (MConsole.getTraceSql()) { System.out.println("!*** MCursorinf.execStatement executeUpdate " + i + " " + minWcsel);
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
      if (MConsole.getTraceSql()) { System.out.println("!*** MCursorInf.execPreparedUpdate: " + minWcsel);
      }
      qcode = minPreparedStatement.executeUpdate();
      

      if (MConsole.getTraceSql()) System.out.println("!*** MCursorInf.execPreparedUpdate qcode: " + qcode);
      if (MConsole.getTraceSql()) System.out.println("!*** MCursorInf.execPreparedUpdate: " + qcode + " linhas.");
    } catch (SQLException ex) {
      minSQLException = ex;
      System.out.println("!*** MCursorInf.execPreparedUpdate ex: " + ex);
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
