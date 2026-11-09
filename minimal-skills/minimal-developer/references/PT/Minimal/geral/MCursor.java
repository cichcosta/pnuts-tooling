package PT.Minimal.geral;

import PT.Minimal.geral.console.MConsole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

public abstract class MCursor
{
  public static final int OK = 0;
  public static final int NOTFOUND = 100;
  public final java.math.BigInteger BI_ZERO = java.math.BigInteger.valueOf(0L);
  public final java.math.BigDecimal BD_ZERO = new java.math.BigDecimal(0);
  public final Integer I_ZERO = new Integer(0);
  public final Double D_ZERO = new Double(0.0D);
  public final Float F_ZERO = new Float(0.0F);
  public final String EMPTY_STRING = "";
  public final Boolean FALSE = new Boolean(false);
  



  public String minWcsel = " ";
  


  public MConnection minConnection;
  


  public PreparedStatement minPreparedStatement;
  


  public Statement minStatement;
  


  public Statement batchStatement;
  


  public ResultSet minResultSet;
  


  public ResultSetMetaData minRSMD;
  

  public SQLException minSQLException;
  

  public int wdcode = 0;
  


  public String wdmsg = " ";
  


  public int numCols;
  


  private String[] colNames;
  


  private String statName;
  


  private static Hashtable LColnames;
  


  public String MQuote = "\"";
  





























  public MCursor() {}
  




























  public Object getDefaultObject(int n)
    throws SQLException
  {
    int type = minRSMD.getColumnType(n);
    switch (type) {
    case -5:  return BI_ZERO;
    case -7:  return FALSE;
    case 1:  return "";
    case 3:  return BD_ZERO.setScale(minRSMD.getScale(n));
    case 8:  return D_ZERO;
    case 6:  return F_ZERO;
    case 4:  return I_ZERO;
    case 12:  return "";
    }
    
    return null;
  }
  
  public int getColumnPosition(String name) { name = name.toLowerCase();
    int n = colNames.length;
    for (int i = 0; i < n; i++) {
      if (colNames[i].equals(name)) {
        return i;
      }
    }
    return -1;
  }
  

  public String get(String wmetdi)
    throws MError
  {
    String wdsel = "?";
    try
    {
      wdsel = minResultSet.getString(wmetdi);
      if (wdsel == null) {
        wdsel = "";
      } else {
        wdsel = PT.Minimal.util.MString.trimRight(wdsel);
      }
      
    }
    catch (SQLException ex)
    {
      minSQLException = ex;
      minZstaf();
    }
    return wdsel;
  }
  

  public String get(int j)
    throws MError
  {
    String wdsel = "?";
    try
    {
      wdsel = minResultSet.getString(j);
      if (wdsel == null) {
        wdsel = "";
      } else {
        wdsel = PT.Minimal.util.MString.trimRight(wdsel);
      }
      
    }
    catch (SQLException ex)
    {
      minSQLException = ex;
      minZstaf();
    }
    return wdsel;
  }
  


  public int getInt(String wmetdi)
    throws MError
  {
    int wnsel = 0;
    try {
      wnsel = minResultSet.getInt(wmetdi);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return wnsel;
  }
  
  public int getInt(int j) throws MError { int wnsel = 0;
    try {
      wnsel = minResultSet.getInt(j);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return wnsel;
  }
  
  public long getLong(String wmetdi)
    throws MError
  {
    long wnsel = 0L;
    try {
      wnsel = minResultSet.getLong(wmetdi);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return wnsel;
  }
  
  public double getDouble(String wmetdi)
    throws MError
  {
    double wnsel = 0.0D;
    try {
      wnsel = minResultSet.getDouble(wmetdi);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return wnsel;
  }
  
  public byte[] getBytes(String wmetdi)
    throws MError
  {
    wbytes = null;
    java.io.InputStream wbin = null;
    ByteArrayOutputStream wbout = new ByteArrayOutputStream();
    
    try
    {
      wbin = minResultSet.getBinaryStream(wmetdi);
      int wbyte;
      while ((wbyte = wbin.read()) != -1)
        wbout.write(wbyte);
      MError werr;
      return wbout.toByteArray();
    }
    catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    } catch (IOException e) {
      werr = new MError(e);
      throw werr;
    } finally {
      try {
        if (wbin != null) wbin.close();
        if (wbout != null) wbout.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  

  public byte[] getBytes(int j)
    throws MError
  {
    wbytes = null;
    java.io.InputStream wbin = null;
    ByteArrayOutputStream wbout = new ByteArrayOutputStream();
    
    try
    {
      wbin = minResultSet.getBinaryStream(j);
      int wbyte;
      while ((wbyte = wbin.read()) != -1)
        wbout.write(wbyte);
      MError werr;
      return wbout.toByteArray();
    }
    catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    } catch (IOException e) {
      werr = new MError(e);
      throw werr;
    } finally {
      try {
        if (wbin != null) wbin.close();
        if (wbout != null) wbout.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  














































  public MHashtable getRecord()
    throws MError
  {
    MHashtable wrecord = new MHashtable(true, true);
    wrecord.setName(statName);
    for (int j = 0; j < numCols; j++) {
      wrecord.put(colNames[j], get(j + 1));
    }
    return wrecord;
  }
  

  public Vector getRecordAsVector()
    throws MError
  {
    Vector wrecord = new Vector();
    for (int j = 0; j < numCols; j++) {
      wrecord.addElement(getObject(j + 1));
    }
    return wrecord;
  }
  

  public Vector getAllRecords()
    throws MError
  {
    Vector wAllRecords = new Vector(1000);
    
    while (wdcode == 0) {
      wAllRecords.addElement(getRecord());
      next();
    }
    
    wAllRecords.trimToSize();
    return wAllRecords;
  }
  
  public Vector getXRecords(int wnum) throws MError {
    if (wnum == -1) {
      return getAllRecords();
    }
    Vector wAllRecords = new Vector(wnum);
    
    for (int j = 0; (j < wnum) && (wdcode == 0); j++) {
      wAllRecords.addElement(getRecord());
      next();
    }
    
    wAllRecords.trimToSize();
    return wAllRecords;
  }
  



  public void next()
    throws MError
  {
    try
    {
      boolean wfound = minResultSet.next();
      if (!wfound) {
        wdcode = 100;
        closeStatement();
        closePreparedStatement();
      }
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  

  public void closeStatement()
    throws MError
  {
    try
    {
      if (minResultSet != null)
      {
        minResultSet.close();
        minResultSet = null;
      }
      if (minStatement != null) {
        minStatement.close();
        minStatement = null;
      }
    }
    catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void closePreparedStatement() throws MError {
    try {
      if (minResultSet != null)
      {
        minResultSet.close();
        minResultSet = null;
      }
      if (minPreparedStatement != null)
      {
        minPreparedStatement.close();
        minPreparedStatement = null;
      }
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void close() throws MError {
    closeStatement();
    closePreparedStatement();
  }
  


  public void minZstaf()
    throws MError
  {
    wdcode = minSQLException.getErrorCode();
    MError werr = new MError(minSQLException, minConnection.getAmbient());
    werr.setSQLStatement(minWcsel);
    

    close();
    throw werr;
  }
  

  public void execQuery(String wcsel) throws MError { execQuery(null, wcsel); }
  
  public void execQuery(String name, String wcsel) throws MError {
    statName = name;
    if (MConsole.getTraceSql()) System.out.println("!*** MCursor.execQuery " + name + ": " + wcsel);
    wdcode = 0;
    minWcsel = checkSQL(wcsel);
    if ((MConsole.getTraceSql()) && (!wcsel.equals(minWcsel))) System.out.println("!*** MCursor.execQuery " + name + ": " + minWcsel);
    try {
      minResultSet = null;
      try {
        minStatement = minConnection.getConnection().createStatement();
        minResultSet = minStatement.executeQuery(minWcsel);
      } catch (org.postgresql.util.PSQLException ex) {
        if (ex.getMessage().indexOf("Connection reset") > 0) {
          System.out.println("Reconecta");
          minConnection.reconnect();
          minConnection.openConnection();
          minStatement = minConnection.getConnection().createStatement();
          minResultSet = minStatement.executeQuery(minWcsel);
        } else {
          minSQLException = ex;
          minZstaf();
        }
        
      }
      catch (SQLException ex)
      {
        String amb = minConnection.getAmbient().substring(0, 1);
        int ec = ex.getErrorCode();
        if (((amb.equalsIgnoreCase("I")) && (ec == -79716)) || ((amb.equalsIgnoreCase("O")) && (ec == 28)) || ((amb.equalsIgnoreCase("S")) && (ex.getMessage().indexOf("Connection reset by peer") > 0))) {
          minConnection.reconnect();
          minConnection.openConnection();
          minStatement = minConnection.getConnection().createStatement();
          minResultSet = minStatement.executeQuery(minWcsel);
        } else {
          minSQLException = ex;
          minZstaf();
        }
      }
      
      next();
      
      if (wdcode == 0) {
        minRSMD = minResultSet.getMetaData();
        numCols = minRSMD.getColumnCount();
        colNames = new String[numCols];
        for (int j = 0; j < numCols; j++) {
          colNames[j] = minRSMD.getColumnName(j + 1).toLowerCase();
        }
        if (statName == null) {
          statName = minRSMD.getTableName(1);
        }
      }
    }
    catch (SQLException ex) {
      minSQLException = ex;
      

      minZstaf();
    }
  }
  
  public int getNumCols() {
    return numCols;
  }
  
  public String[] getColNames() {
    return colNames;
  }
  

  public PreparedStatement prepareStatement(String wcsel) throws MError { return prepareStatement(null, wcsel); }
  
  public PreparedStatement prepareStatement(String name, String wcsel) throws MError {
    statName = name;
    try {
      if (MConsole.getTraceSql()) System.out.println("!*** MCursor.prepareStatement " + name + ": " + wcsel);
      minWcsel = checkSQL(wcsel);
      if ((MConsole.getTraceSql()) && (!wcsel.equals(minWcsel))) System.out.println("!*** MCursor.prepareStatement " + name + ": " + minWcsel);
      minPreparedStatement = minConnection.getConnection().prepareStatement(minWcsel);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return minPreparedStatement;
  }
  

  public CallableStatement prepareCall(String wcsel) throws MError { return prepareCall(null, wcsel); }
  
  public CallableStatement prepareCall(String name, String wcsel) throws MError {
    statName = name;
    try {
      if (MConsole.getTraceSql()) System.out.println("!*** MCursor.prepareCall " + name + ": " + minWcsel);
      minWcsel = checkSQL(wcsel);
      if ((MConsole.getTraceSql()) && (!wcsel.equals(minWcsel))) System.out.println("!*** MCursor.prepareCall " + name + ": " + minWcsel);
      minPreparedStatement = minConnection.getConnection().prepareCall(minWcsel);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return (CallableStatement)minPreparedStatement;
  }
  
  public void registerOutParameter(int parameterIndex, int sqlType) throws MError {
    try {
      ((CallableStatement)minPreparedStatement).registerOutParameter(parameterIndex, sqlType);
    }
    catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws MError {
    try {
      ((CallableStatement)minPreparedStatement).registerOutParameter(parameterIndex, sqlType, scale);
    }
    catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  





















  public boolean execPreparedQuery()
    throws MError
  {
    try
    {
      if (MConsole.getTraceSql()) System.out.println("!*** MCursor.execPreparedQuery: " + minWcsel);
      wdcode = 0;
      minResultSet = minPreparedStatement.executeQuery();
      minRSMD = minResultSet.getMetaData();
      numCols = minRSMD.getColumnCount();
      colNames = new String[numCols];
      for (int j = 0; j < numCols; j++) {
        colNames[j] = minRSMD.getColumnName(j + 1).toLowerCase();
      }
      next();
    } catch (org.postgresql.util.PSQLException ex) {
      if (ex.getMessage().indexOf("Connection reset") > 0) {
        System.out.println("Reconecta");
        minConnection.reconnect();
        minConnection.openConnection();
        return false;
      }
      minSQLException = ex;
      minZstaf();
    }
    catch (SQLException ex) {
      String amb = minConnection.getAmbient().substring(0, 1);
      int ec = ex.getErrorCode();
      if (((amb.equalsIgnoreCase("I")) && (ec == -79716)) || ((amb.equalsIgnoreCase("O")) && (ec == 28)) || ((amb.equalsIgnoreCase("S")) && (ex.getMessage().indexOf("Connection reset by peer") > 0))) {
        minConnection.reconnect();
        minConnection.openConnection();
        return false;
      }
      minSQLException = ex;
      minZstaf();
    }
    
    return true;
  }
  
  public void execCall() throws MError {
    try {
      wdcode = 0;
      minResultSet = ((CallableStatement)minPreparedStatement).executeQuery();
      






      next();
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void setPreparedPar(int wpar, String wvalue) throws MError
  {
    try {
      if ("".equals(wvalue)) {
        wvalue = " ";
      }
      minPreparedStatement.setString(wpar, wvalue);

    }
    catch (SQLException ex)
    {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void setPreparedPar(int wpar, int wvalue) throws MError {
    try {
      minPreparedStatement.setInt(wpar, wvalue);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void setPreparedPar(int wpar, double wvalue) throws MError {
    try {
      minPreparedStatement.setDouble(wpar, wvalue);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  

  public void setPreparedPar(int wpar, java.math.BigDecimal wvalue)
    throws MError
  {
    try
    {
      minPreparedStatement.setBigDecimal(wpar, wvalue);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void setPreparedPar(int wpar, byte[] wvalue)
    throws MError
  {
    try
    {
      minPreparedStatement.setBinaryStream(wpar, new java.io.ByteArrayInputStream(wvalue), wvalue.length);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void setPreparedPar(int wpar, byte[] wvalue, String wtype) throws MError {
    try { if (wtype.equals("BINARY"))
      {
        minPreparedStatement.setBinaryStream(wpar, new java.io.ByteArrayInputStream(wvalue), wvalue.length);
      }
      if (wtype.equals("TEXT"))
      {
        minPreparedStatement.setBinaryStream(wpar, new java.io.ByteArrayInputStream(wvalue), wvalue.length);
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      minSQLException = ex;
      minZstaf();
    }
  }
  









  public int execStatement(String wcsel)
    throws MError
  {
    return execStatement(wcsel, true);
  }
  
































  public void createStatement()
    throws MError
  {
    batchStatement = null;
    try
    {
      batchStatement = minConnection.getConnection().createStatement();
    }
    catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void addBatch(String wcsel) throws MError
  {
    try
    {
      batchStatement.addBatch(wcsel);
    }
    catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
  }
  
  public void executeBatchStatement()
    throws MError
  {
    int[] i = null;
    try
    {
      i = batchStatement.executeBatch();
      




      try
      {
        if (batchStatement != null) {
          batchStatement.close();
          batchStatement = null;
        }
      } catch (SQLException ex) {
        minSQLException = ex;
        minZstaf();
      }
      

      k = 0;
    }
    catch (SQLException ex)
    {
      minSQLException = ex;
      minZstaf();
    } finally {
      try {
        if (batchStatement != null) {
          batchStatement.close();
          batchStatement = null;
        }
      } catch (SQLException ex) {
        minSQLException = ex;
        minZstaf();
      }
    }
    
    int k;
    for (int j = 0; j < i.length; j++) {
      k += i[j];
    }
  }
  



  public MRow getMRow(String wtable)
    throws MError
  {
    MRow wrow = null;
    Hashtable watrib = new Hashtable();
    Hashtable wtypes = new Hashtable();
    try {
      minResultSet = minConnection.getDatabaseMetaData().getColumns(null, null, wtable, "%");
      
      next();
      Vector wvcols = new Vector();
      while (wdcode == 0) {
        String wkey = minResultSet.getString(4).toLowerCase();
        wvcols.addElement(wkey);
        int wtype = minResultSet.getInt(5);
        watrib.put(wkey, " ");
        
        if ((wtype == 1) || (wtype == 12)) {
          wtypes.put(wkey, "TEXTTYPE");
        } else if (wtype == -1) {
          wtypes.put(wkey, "TEXT");
        } else {
          wtypes.put(wkey, "NUMTYPE");
        }
        next();
      }
      close();
      wdcode = 0;
      String[] wcols = new String[wvcols.size()];
      for (int j = 0; j < wvcols.size(); j++) {
        wcols[j] = ((String)wvcols.elementAt(j));
      }
      MTable wmt = new MTable(watrib);
      wmt.setColNames(wcols);
      wmt.setTable(wtable);
      wmt.setTypes(wtypes);
      wmt.setCurrency();
      wrow = new MRow(wmt, watrib);
    } catch (SQLException ex) {
      minSQLException = ex;
      minZstaf();
    }
    return wrow;
  }
  
  public int getWdcode() { return wdcode; }
  








  public boolean supportsTransaction()
    throws MError
  {
    return minConnection.supportsTransaction();
  }
  
  public boolean inBeginBatch() {
    return minConnection.inBeginBatch();
  }
  
  public boolean inBeginWork() { return minConnection.inBeginWork(); }
  
  public void beginWork() throws MError
  {
    minConnection.beginWork();
  }
  
  public void beginBatch() throws MError { minConnection.beginBatch(); }
  
  public void commitWork() throws MError
  {
    minConnection.commitWork();
  }
  
  public void commitBatch() throws MError { minConnection.commitBatch(); }
  
  public void rollbackWork() throws MError
  {
    minConnection.rollbackWork();
  }
  
  public void rollbackBatch() throws MError { minConnection.rollbackBatch(); }
  
  public MConnection getMConnection()
  {
    return minConnection;
  }
  
  public String getUser() {
    return minConnection.getUser();
  }
  
  public String getAmbient() {
    return minConnection.getAmbient();
  }
  
  public String[] getColumnAsArray(String wcsel, int colnumber)
    throws MError
  {
    wdcode = 0;
    String[] wnames = null;
    try
    {
      execQuery(wcsel);
      
      if (getWdcode() == 0) {
        Vector wv = new Vector();
        while (wdcode == 0) {
          wv.addElement(get(colnumber));
          next();
        }
        wnames = new String[wv.size()];
        wv.copyInto(wnames);
      }
    } finally {
      closeStatement();
    }
    return wnames;
  }
  





  protected void finalize()
  {
    close();
  }
  




  public String toString()
  {
    return getClass().toString() + "[" + statName + ":wdcode=" + wdcode + "\n" + minWcsel + "\n]";
  }
  
  public String getLockRowsSQL(String wcsel) throws MError
  {
    wdcode = 0;
    String wcsel_local = wcsel.toLowerCase();
    String wlock = null;
    int wtableini = -1;
    int wtableend = -1;
    int wwhereini = -1;
    int wwhereend = -1;
    wtableini = wcsel_local.indexOf("from ");
    if (wtableini != -1) {
      wtableini += 4;
      wtableend = wcsel_local.indexOf("where ");
    } else {
      wtableini = wcsel_local.indexOf("update ");
      if (wtableini != -1) {
        wtableini += 6;
        wtableend = wcsel_local.indexOf("set ");
      }
    }
    
    wwhereini = wcsel_local.indexOf("where ");
    
    wwhereend = wcsel_local.indexOf("group ");
    if (wwhereend == -1) {
      wwhereend = wcsel_local.indexOf("order ");
    }
    if (wwhereend == -1) {
      wwhereend = wcsel.length();
    }
    wlock = "select '+' from " + wcsel.substring(wtableini, wtableend);
    if (wwhereini != -1) {
      wlock = wlock + wcsel.substring(wwhereini, wwhereend);
    }
    return wlock;
  }
  
  public abstract void lockRows(String paramString)
    throws MError;
  
  public abstract void lockTable(String paramString)
    throws MError;
  
  public abstract void setTriggersEnabled(String paramString, boolean paramBoolean)
    throws MError;
  
  public abstract void checkTriggers(String paramString)
    throws MError;
  
  public abstract String getKey(String paramString1, String paramString2, String paramString3)
    throws MError;
  
  public abstract MHashtable setZCommand(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws MError;
  
  public abstract String checkSQL(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfReplicationTableNames(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfTableNames(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfIndexesNames(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfTriggersNames(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfProceduresNames(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfSequencesNames(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfSynonymsNames(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfGrantsNames(String paramString)
    throws MError;
  
  public abstract String[] getArrayOfInvalidNames(String paramString)
    throws MError;
  
  public abstract int execStatement(String paramString, boolean paramBoolean)
    throws MError;
  
  public abstract int execPreparedUpdate()
    throws MError;
  
  public abstract Object getObject(int paramInt)
    throws MError;
  
  public abstract Object getObject(String paramString)
    throws MError;
}
