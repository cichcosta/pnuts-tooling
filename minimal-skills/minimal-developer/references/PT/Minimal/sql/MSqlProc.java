package PT.Minimal.sql;

import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.console.MConsole;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;














public class MSqlProc
{
  public static int BIGINT = -5;
  public static int BINARY = -2;
  public static int BIT = -7;
  public static int CHAR = 1;
  public static int DATE = 91;
  public static int DECIMAL = 3;
  public static int DOUBLE = 8;
  public static int FLOAT = 6;
  public static int INTEGER = 4;
  public static int LONGVARBINARY = -4;
  public static int LONGVARCHAR = -1;
  public static int NULL = 0;
  public static int NUMERIC = 2;
  public static int OTHER = 1111;
  public static int REAL = 7;
  public static int SMALLINT = 5;
  public static int TIME = 92;
  public static int TIMESTAMP = 93;
  public static int TINYINT = -6;
  public static int VARBINARY = -3;
  public static int VARCHAR = 12;
  
  MConnection mconn;
  
  CallableStatement stat;
  ResultSet rs = null;
  boolean hasMoreRows = false;
  
  public MSqlProc()
  {
    this(null, null, null);
  }
  
  public MSqlProc(MConnection mc) {
    this(mc, null, null);
  }
  
  public MSqlProc(MConnection mc, String proc) {
    this(mc, proc, null);
  }
  
  public MSqlProc(MConnection mc, String proc, Object[] parms) {
    if (mc == null) {
      mconn = MConnection.getMConnection();
    } else {
      mconn = mc;
    }
    

    if (proc != null) {
      try {
        prepare(proc, parms == null ? 0 : parms.length);
        setParameters(parms);
        if (mconn.getAmbient().toUpperCase().startsWith("POSTGRESQL")) {
          setOutParameter(1, 4);
          executeP();
        }
        else {
          execute();
        }
      } finally {
        close();
      }
    }
  }
  

  public static void doStaticProc(String proc)
  {
    new MSqlProc(null, proc, null);
  }
  
  public static void doStaticProc(String proc, Object[] parms) { new MSqlProc(null, proc, parms); }
  

  public static void doStaticProc(MConnection mc, String proc, Object[] parms) { new MSqlProc(mc, proc, parms); }
  
  public static void doStaticProc(MConnection mc, String proc, Vector vparms) {
    Object[] parms = new Object[vparms.size()];
    vparms.copyInto(parms);
    new MSqlProc(mc, proc, parms);
  }
  
  public void prepare(String proc)
  {
    prepare(proc, 0);
  }
  
  public void prepare(String proc, int nparms) {
    if (stat != null) {
      throw new MError("MIN-999999E");
    }
    try {
      String wsql = "{call " + proc + "(";
      if (mconn.getAmbient().toUpperCase().startsWith("POSTGRESQL")) {
        wsql = "{? = call " + proc + "(";
      }
      if (nparms > 0) {
        for (int i = nparms; i > 1; i--) {
          wsql = wsql + "?, ";
        }
        wsql = wsql + "?";
      }
      wsql = wsql + ")}";
      if (MConsole.getTraceSql()) System.out.println(wsql);
      stat = mconn.getConnection().prepareCall(wsql);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameters(Object[] parms)
  {
    if (parms != null) {
      int i = 0; for (int n = parms.length; i < n; i++) {
        setParameter(i + 1, parms[i]);
      }
    }
  }
  
  public void setOutParameter(int index, int type)
  {
    try {
      stat.registerOutParameter(index, type);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setOutParameter(int index, int type, int scale) {
    try {
      stat.registerOutParameter(index, type, scale);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameter(int index, String value)
  {
    try
    {
      stat.setString(index, value);
    }
    catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameter(int index, int value) {
    try {
      stat.setInt(index, value);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameter(int index, long value) {
    try {
      stat.setLong(index, value);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameter(int index, float value) {
    try {
      stat.setFloat(index, value);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameter(int index, double value) {
    try {
      stat.setDouble(index, value);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameter(int index, Date value) {
    try {
      stat.setDate(index, value);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameter(int index, BigDecimal value) {
    try {
      stat.setBigDecimal(index, value);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void setParameter(int index, Object value)
  {
    try {
      String wstring = value.toString();
      if (mconn.getAmbient().toUpperCase().startsWith("POSTGRESQL")) {
        index++;
        if (value.getClass().getCanonicalName().indexOf(".Integer") >= 0) {
          stat.setInt(index, Integer.parseInt(wstring));
        } else {
          stat.setString(index, wstring);
        }
      } else {
        stat.setString(index, wstring);
      }
      
    }
    catch (SQLException ex)
    {
      throw new MError(ex);
    }
  }
  
  public int execute()
  {
    try {
      return stat.executeUpdate();
    }
    catch (SQLException ex)
    {
      throw new MError(ex);
    }
  }
  
  public boolean executeP() {
    try {
      return stat.execute();
    }
    catch (SQLException ex)
    {
      throw new MError(ex);
    }
  }
  
  public void executeRS() {
    try {
      rs = stat.executeQuery();
      hasMoreRows = rs.next();
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  


  public ResultSet getRS() { return rs; }
  
  public boolean next() {
    if (!hasMoreRows) {
      return false;
    }
    try {
      return this.hasMoreRows = rs.next();
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public String getRowString(int index) { if (!hasMoreRows) {
      throw new MError("MIN-000100E");
    }
    try {
      return rs.getString(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public int getRowInt(int index) { if (!hasMoreRows) {
      throw new MError("MIN-000100E");
    }
    try {
      return rs.getInt(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public long getRowLong(int index) { if (!hasMoreRows) {
      throw new MError("MIN-000100E");
    }
    try {
      return rs.getLong(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public float getRowFloat(int index) { if (!hasMoreRows) {
      throw new MError("MIN-000100E");
    }
    try {
      return rs.getFloat(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public double getRowDouble(int index) { if (!hasMoreRows) {
      throw new MError("MIN-000100E");
    }
    try {
      return rs.getDouble(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public Date getRowDate(int index) { if (!hasMoreRows) {
      throw new MError("MIN-000100E");
    }
    try {
      return rs.getDate(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public String getString(int index)
  {
    try {
      return stat.getString(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public int getInt(int index) {
    try {
      return stat.getInt(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public long getLong(int index) {
    try {
      return stat.getLong(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public float getFloat(int index) {
    try {
      return stat.getFloat(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public double getDouble(int index) {
    try {
      return stat.getDouble(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public Date getDate(int index) {
    try {
      return stat.getDate(index);
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  public void clear() {
    if (stat != null) {
      try {
        stat.clearParameters();
      } catch (SQLException ex) {
        throw new MError(ex);
      }
    }
  }
  
  public void close() {
    try {
      if (mconn != null) {
        mconn = null;
      }
      if (stat != null) {
        stat.close();
        stat = null;
      }
      if (rs != null) {
        rs.close();
        rs = null;
      }
    } catch (SQLException ex) {
      throw new MError(ex);
    }
  }
  
  protected void finalize() {
    if ((stat != null) || (rs != null) || (mconn != null)) {
      System.out.println("unclosed MSqlProc");
      close();
    }
  }
}
