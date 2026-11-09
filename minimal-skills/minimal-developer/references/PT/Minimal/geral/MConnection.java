package PT.Minimal.geral;

import PT.Minimal.pnuts.;
import com.informix.jdbc.IfxDriver;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import oracle.jdbc.driver.OracleDriver;
import org.postgresql.Driver;
import org.postgresql.util.PSQLException;
import sun.jdbc.odbc.JdbcOdbcDriver;









public class MConnection
{
  private static Hashtable MConnectionList;
  public Connection minConnection;
  public DatabaseMetaData minDMD;
  public SQLException minSQLException;
  public int wdcode = 0;
  


  public String wdmsg = " ";
  
  public String MQuote;
  
  private String wname;
  
  private String wdsn;
  private String wuser;
  private String wpasswd;
  private String wamb;
  private String wmotor;
  private String wurl;
  private boolean bBegin = false; private boolean bBatch = false; private boolean locked = false;
  
  public String wschema;
  

  public MConnection()
    throws MError
  {
    Properties p = MUtil.myProp;
    wdsn = ((String)p.get("user.dsn"));
    wuser = ((String)p.get("user.name"));
    wpasswd = ((String)p.get("user.passwd"));
    wname = "DEFAULT";
    openConnection();
  }
  

  public MConnection(String pname, String pdsn, String puser, String ppasswd)
    throws MError
  {
    wdsn = pdsn;
    wuser = puser;
    wpasswd = ppasswd;
    wname = pname;
    openConnection();
  }
  




  public void openConnection()
    throws MError
  {
    try
    {
      System.out.println("!*** MConnection DSN: " + wdsn);
      
      if (wdsn.indexOf("oracle") != -1) {
        Locale.setDefault(Locale.US);
        try {
          DriverManager.registerDriver(new OracleDriver());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (wdsn.indexOf("informix") != -1) {
        try {
          DriverManager.registerDriver(new IfxDriver());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (wdsn.indexOf("odbc") != -1) {
        try {
          DriverManager.registerDriver(new JdbcOdbcDriver());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (wdsn.indexOf("microsoft") != -1) {
        try {
          DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (wdsn.indexOf("jdbc:sqlserver") != -1) {
        try {
          DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (wdsn.indexOf("postgresql") != -1) {
        try {
          DriverManager.registerDriver(new Driver());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      

      minConnection = DriverManager.getConnection(wdsn, wuser, wpasswd);
      
      minDMD = minConnection.getMetaData();
      wamb = minDMD.getDatabaseProductName();
      System.out.println("!*** MConnection Produto: " + wamb);
      wmotor = minDMD.getDatabaseProductVersion();
      System.out.println("!*** MConnection Motor:" + wmotor);
      wurl = minDMD.getURL();
      if (wamb.toUpperCase().startsWith("INFORMIX")) {
        wamb = "Informix";
        wmotor = ("I" + wmotor);
      }
      else if (wamb.toUpperCase().startsWith("ORACLE")) {
        wamb = "Oracle";
        int wini = wmotor.indexOf("Release ");
        if (wini != -1) {
          wini += 8;
          int wfim = wmotor.indexOf(" ", wini);
          wmotor = ("O" + wmotor.substring(wini, wfim));
        }
      }
      else if (wamb.toUpperCase().startsWith("MICROSOFT")) {
        wamb = "SQL Server";
        wmotor = ("S" + wmotor);
      }
      else if (wamb.toUpperCase().startsWith("POSTGRESQL")) {
        wamb = "PostgreSQL";
        wmotor = ("P" + wmotor);
      }
      




      System.out.println("!*** MConnection Connection " + wname + " OK!");
      System.out.println("!*** MConnection URL: " + wurl);
      System.out.println("!*** MConnection Ambiente: " + wamb);
      System.out.println("!*** MConnection Engine: " + wmotor);
      System.out.println("!*** MConnection Isolation:" + minConnection.getTransactionIsolation());
      System.out.println("!*** MConnection AutoCommit:" + minConnection.getAutoCommit());
      
      MQuote = minDMD.getIdentifierQuoteString();
      if (MQuote.trim().length() == 0) {
        MQuote = "\"";
      }
      if (MConnectionList == null) {
        MConnectionList = new Hashtable();
      }
      MConnectionList.put(wname, this);
      

      if (minConnection.getTransactionIsolation() != 0) {
        if (wamb.equals("Informix")) {
          minConnection.setTransactionIsolation(1);
          System.out.println("!*** MConnection Isolation: TRANSACTION_READ_UNCOMMITTED");
        }
        if (wamb.equals("Oracle")) {
          if (minDMD.supportsTransactionIsolationLevel(1)) {
            System.out.println("!*** MConnection Isolation: TRANSACTION_READ_UNCOMMITTED");
            minConnection.setTransactionIsolation(1);
          } else {
            minConnection.setTransactionIsolation(2);
            System.out.println("!*** MConnection Isolation: TRANSACTION_READ_COMMITTED");
          }
          if (wamb.equals("SQL Server")) {
            minConnection.setTransactionIsolation(1);
            System.out.println("!*** MConnection Isolation: TRANSACTION_READ_UNCOMMITTED");
          }
          if (wamb.equals("PostgreSQL"))
          {
            minConnection.setTransactionIsolation(1);
            System.out.println("!*** MConnection Isolation: TRANSACTION_READ_COMMITTED");
          }
        }
        


        minConnection.setAutoCommit(true);
        if (wamb.equals("Informix"))
        {
          Statement wsql = minConnection.createStatement();
          try {
            wsql.executeUpdate("set lock mode to wait 60");
          } finally {
            wsql.close();
            wsql = null;
          }
        }
        if (wamb.equals("Oracle"))
        {
          Statement wsql = minConnection.createStatement();
          try {
            if ((MLogin.hasLoginProperty("MULTISCHEMA")) && (!MLogin.getLoginProperty("MULTISCHEMA").startsWith("N")) && (!wname.equals("JMETADATA"))) {
              wschema = wuser.substring(0).toUpperCase();
              int i = wuser.indexOf("#");
              if (i > -1) {
                wschema = wuser.substring(0, i);
              } else {
                MLogin.checkMinimalKey();
              }
              MLogin.setLoginProperty("SCHEMA", wschema);
              System.out.println("!*** MConnection CURRENT_SCHEMA = " + wschema);
              wsql.executeUpdate("ALTER SESSION SET CURRENT_SCHEMA=" + wschema);
              try {
                if (!wuser.equalsIgnoreCase(wschema)) {
                  wsql.executeUpdate("SET ROLE USER_OF_" + wschema);
                }
              } catch (Exception e) {
                System.out.println(e.getMessage());
              }
            }
            else
            {
              System.out.println("!*** MConnection CURRENT_SCHEMA = MINIMAL");
              wsql.executeUpdate("ALTER SESSION SET CURRENT_SCHEMA=MINIMAL");
              try {
                if (!wuser.equalsIgnoreCase("minimal")) {
                  wsql.executeUpdate("SET ROLE USER_OF_MINIMAL");
                } else {
                  MLogin.checkMinimalKey();
                }
              } catch (Exception e) {
                System.out.println(e.getMessage());
              }
            }
          } finally {
            wsql.close();
            wsql = null;
          }
        }
        else if ((MLogin.isSameAsMetaData()) || (wuser.equals("minimal"))) {
          MLogin.checkMinimalKey();
        }
      }
      

      System.out.println("!*** MConnection Catalog:" + minConnection.getCatalog());
      System.out.println("!*** MConnection Isolation:" + minConnection.getTransactionIsolation());
      System.out.println("!*** MConnection AutoCommit:" + minConnection.getAutoCommit());
    } catch (SQLException ex) {
      MError werr = new MError(ex);
      werr.setAmbient(getAmbient());
      throw werr;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  


  public void closeConnection()
    throws MError
  {
    try
    {
      try
      {
        minConnection.close();
      } catch (SQLException e) {
        minConnection.close();
      }
      
      bBatch = false;
      bBegin = false;
      if (MConnectionList != null) {
        MConnectionList.remove(wname);
      }
    }
    catch (SQLException ex) {
      MError werr = new MError(ex);
      throw werr;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public Connection getConnection() {
    return minConnection;
  }
  
  public boolean supportsTransaction() throws MError
  {
    try {
      return minConnection.getTransactionIsolation() != 0;
    } catch (SQLException ex) {
      MError werr = new MError(ex);
      throw werr;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return false;
  }
  
  public String getName()
  {
    return wname;
  }
  
  public String getUser() {
    return wuser;
  }
  
  public DatabaseMetaData getDatabaseMetaData() {
    return minDMD;
  }
  
  public boolean inBeginBatch() {
    return bBatch;
  }
  
  public boolean inBeginWork() {
    return bBegin;
  }
  
  public void beginWork() throws MError {
    if (!bBatch) {
      System.out.println("!*** MConnection beginWork()");
      if (bBegin) {
        throw new MError("MIN-999995E");
      }
      try {
        minConnection.setAutoCommit(false);
      } catch (PSQLException ex) {
        if (ex.getMessage().indexOf("Connection reset") > 0) {
          System.out.println("Reconecta");
          reconnect();
          openConnection();
          try {
            minConnection.setAutoCommit(false);
          } catch (SQLException ex1) {
            MError werr = new MError(ex1);
            werr.setAmbient(getAmbient());
          }
        } else {
          MError werr = new MError(ex);
          werr.setAmbient(getAmbient());
        }
      } catch (SQLException ex) {
        String amb = getAmbient().substring(0, 1);
        int ec = ex.getErrorCode();
        if (((amb.equalsIgnoreCase("I")) && (ec == -79716)) || ((amb.equalsIgnoreCase("O")) && (ec == 28)) || ((amb.equalsIgnoreCase("S")) && (ex.getMessage().indexOf("Connection reset by peer") > 0))) {
          reconnect();
          openConnection();
          try {
            minConnection.setAutoCommit(false);
          } catch (SQLException ex1) {
            MError werr = new MError(ex1);
            werr.setAmbient(getAmbient());
          }
        } else {
          MError werr = new MError(ex);
          werr.setAmbient(getAmbient());
          throw werr;
        }
      }
      bBegin = true;
    }
  }
  
  public void beginBatch() throws MError {
    System.out.println("!*** MConnection beginBatch()");
    if (bBegin) {
      throw new MError("MIN-999995E");
    }
    if (bBatch) {
      throw new MError("MIN-999996E");
    }
    try {
      minConnection.setAutoCommit(false);
    } catch (PSQLException ex) {
      if (ex.getMessage().indexOf("Connection reset") > 0) {
        System.out.println("Reconecta");
        reconnect();
        openConnection();
        try {
          minConnection.setAutoCommit(false);
        } catch (SQLException ex1) {
          MError werr = new MError(ex1);
          werr.setAmbient(getAmbient());
        }
      } else {
        MError werr = new MError(ex);
        werr.setAmbient(getAmbient());
      }
    } catch (SQLException ex) {
      String amb = getAmbient().substring(0, 1);
      int ec = ex.getErrorCode();
      if (((amb.equalsIgnoreCase("I")) && (ec == -79716)) || ((amb.equalsIgnoreCase("O")) && (ec == 28)) || ((amb.equalsIgnoreCase("S")) && (ex.getMessage().indexOf("Connection reset by peer") > 0))) {
        reconnect();
        openConnection();
        try {
          minConnection.setAutoCommit(false);
        } catch (SQLException ex1) {
          MError werr = new MError(ex1);
          werr.setAmbient(getAmbient());
        }
      } else {
        MError werr = new MError(ex);
        werr.setAmbient(getAmbient());
        throw werr;
      }
    }
    bBatch = true;
  }
  
  public void commitWork() throws MError {
    if (!bBatch) {
      System.out.println("!*** MConnection commitWork()");
      if (!bBegin) {
        throw new MError("MIN-999997E");
      }
      try {
        minConnection.commit();
        minConnection.setAutoCommit(true);
      } catch (SQLException ex) {
        MError werr = new MError(ex);
        werr.setAmbient(getAmbient());
        throw werr;
      }
      bBegin = false;
    }
  }
  
  public void commitBatch() throws MError {
    System.out.println("!*** MConnection commitBatch()");
    if (!bBatch) {
      throw new MError("MIN-999998E");
    }
    try {
      minConnection.commit();
      minConnection.setAutoCommit(true);
    } catch (SQLException ex) {
      MError werr = new MError(ex);
      werr.setAmbient(getAmbient());
      throw werr;
    }
    bBatch = false;
  }
  
  public void rollbackWork() throws MError {
    if (!bBatch) {
      System.out.println("!*** MConnection rollbackWork()");
      if (!bBegin) {
        throw new MError("MIN-999997E");
      }
      try {
        minConnection.rollback();
        minConnection.setAutoCommit(true);
      } catch (SQLException ex) {
        MError werr = new MError(ex);
        werr.setAmbient(getAmbient());
        throw werr;
      }
      bBegin = false;
    }
  }
  
  public void rollbackBatch() throws MError {
    System.out.println("!*** MConnection rollbackBatch()");
    if (!bBatch) {
      throw new MError("MIN-999998E");
    }
    try {
      minConnection.rollback();
      minConnection.setAutoCommit(true);
    } catch (SQLException ex) {
      MError werr = new MError(ex);
      werr.setAmbient(getAmbient());
      throw werr;
    }
    bBatch = false;
  }
  
  public static MConnection getMConnection() throws MError
  {
    return getMConnection("DEFAULT");
  }
  
  public static MConnection getMConnection(String pname) throws MError {
    MConnection wmc = null;
    if (MConnectionList != null) {
      wmc = (MConnection)MConnectionList.get(pname);
    }
    if ((wmc == null) && (pname.equals("DEFAULT"))) {
      wmc = new MConnection();
    }
    return wmc;
  }
  
  public static void reconnect(String pname) throws MError {
    MConnection wmc = null;
    if (MConnectionList != null) {
      wmc = (MConnection)MConnectionList.get(pname);
    }
    if (wmc != null) {
      wmc.reconnect();
    }
  }
  
  public void reconnect() throws MError {
    if (locked) {
      throw new MError("MIN-999994E");
    }
    closeConnection();
    try {
      openConnection();
    } catch (Exception e) {
      openConnection();
    }
    
     mpnut = new ("LOGIN");
    if (mpnut.existsPnut("LOGIN", "MC", "VARIABLES", "FN")) {
      mpnut.evalPnut("LOGIN", "MC", "VARIABLES", "FN");
    } else {
      throw new MError("MIN-LOG081E");
    }
  }
  
  public static MConnection getMConnection(String pname, String pdsn, String puser, String ppasswd) throws MError {
    MConnection wmc = null;
    if (MConnectionList != null) {
      wmc = (MConnection)MConnectionList.get(pname);
    }
    if (wmc == null) {
      wmc = new MConnection(pname, pdsn, puser, ppasswd);
    }
    return wmc;
  }
  
  public static void closeAll() throws MError { Enumeration e;
    if (MConnectionList != null)
      for (e = MConnectionList.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        ((MConnection)MConnectionList.get(wkey)).closeConnection();
        MConnectionList.remove(wkey);
      }
  }
  
  public static void closeAllExceptNormal() throws MError {
    Enumeration e;
    if (MConnectionList != null) {
      for (e = MConnectionList.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((!wkey.equals("DEFAULT")) && (!wkey.equals("JMETADATA"))) {
          ((MConnection)MConnectionList.get(wkey)).closeConnection();
          MConnectionList.remove(wkey);
        }
      }
    }
  }
  
  public String getAmbient() {
    return wamb;
  }
  
  public String getEngine() {
    return wmotor;
  }
  
  public String getURL() {
    return wurl;
  }
  
  public static MCursor getMCursor() throws MError {
    MConnection wmc = getMConnection();
    return getMCursor(wmc);
  }
  
  public static MCursor getMCursor(String wpname) throws MError {
    MConnection wmc = getMConnection(wpname);
    return getMCursor(wmc);
  }
  
  public static MCursor getMCursor(String pdsn, String puser, String ppasswd) throws MError {
    MConnection wmc = getMConnection(pdsn, pdsn, puser, ppasswd);
    return getMCursor(wmc);
  }
  
  public static MCursor getMCursor(String pname, String pdsn, String puser, String ppasswd) throws MError {
    MConnection wmc = getMConnection(pname, pdsn, puser, ppasswd);
    return getMCursor(wmc);
  }
  
  public static MCursor getMCursor(MConnection wconn) throws MError {
    if (wconn.getAmbient().toUpperCase().startsWith("INFORMIX")) {
      return new MCursorInf(wconn);
    }
    if (wconn.getAmbient().toUpperCase().startsWith("ORACLE")) {
      return new MCursorOra(wconn);
    }
    if (wconn.getAmbient().toUpperCase().startsWith("SQL SERVER")) {
      return new MCursorSql(wconn);
    }
    if (wconn.getAmbient().toUpperCase().startsWith("POSTGRES")) {
      return new MCursorPg(wconn);
    }
    throw new MError("MIN-983999E");
  }
}
