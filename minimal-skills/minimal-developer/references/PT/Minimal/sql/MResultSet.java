package PT.Minimal.sql;

import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MHashtable;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
























public class MResultSet
{
  public static final int ALL_RECORDS = Integer.MAX_VALUE;
  protected int numCols;
  protected Vector colNames;
  protected MHashtable columns;
  protected Vector records;
  protected boolean autoStat = false;
  

  protected MCursor stat;
  

  protected String name;
  

  protected String sql;
  

  protected int wdcode;
  


  public MResultSet(String name, String sql)
  {
    this(name, sql, Integer.MAX_VALUE);
  }
  













  public MResultSet(String name, String sql, int rows)
  {
    this((MCursor)null, name, sql, rows);
  }
  










  public MResultSet(MConnection conn, String name, String sql)
  {
    this(conn, name, sql, Integer.MAX_VALUE);
  }
  














  public MResultSet(MConnection conn, String name, String sql, int rows)
  {
    this(MConnection.getMCursor(), name, sql, rows, true);
  }
  







  public MResultSet(MCursor statement, String name, String sql)
  {
    this(statement, name, sql, Integer.MAX_VALUE);
  }
  













  public MResultSet(MCursor statement, String name, String sql, int rows)
  {
    this(statement, name, sql, rows, false);
  }
  
  protected MResultSet(MCursor statement, String name, String sql, int rows, boolean auto)
  {
    stat = statement;
    this.name = name;
    this.sql = sql;
    
    autoStat = auto;
    if (stat == null) {
      stat = MConnection.getMCursor();
      autoStat = true;
    }
    try
    {
      stat.execQuery(name, sql);
      
      wdcode = stat.getWdcode();
      if (wdcode != 0) {
        makeVector(0);
        closeStat();
        return;
      }
      
      getColumns();
      

      getRecords(rows);
    } catch (MError ex) {
      try {
        close();
      } catch (MError exx) {
        exx.printStackTrace();
      }
      throw ex;
    }
  }
  





  public void setAllowExists(boolean allow)
  {
    columns.setAllowExists(allow);
  }
  




  public void setAllowChange(boolean allow)
  {
    columns.setAllowChange(allow);
  }
  



  private void getColumns()
  {
    numCols = stat.getNumCols();
    String[] cols = stat.getColNames();
    

    columns = new MHashtable();
    columns.setName(name);
    

    colNames = new Vector(numCols, 5);
    
    for (int i = 0; i < numCols; i++) {
      String col = cols[i];
      if (columns.containsKey(col)) {
        throw new MError("MIN-910009E", new String[] { name, col });
      }
      columns.put(cols[i], new Integer(i));
      colNames.addElement(col);
    }
  }
  






  private void makeVector(int size)
  {
    if (records != null) {
      records.removeAllElements();
    }
    

    if (size == Integer.MAX_VALUE) {
      records = new Vector(500, 500);
    } else {
      records = new Vector(size, 1);
    }
  }
  





  public void getAllRecords()
  {
    getRecords(Integer.MAX_VALUE);
  }
  






  public synchronized void getRecords(int rows)
  {
    try
    {
      if (stat == null) {
        close();
        makeVector(0);
        return;
      }
      


      if (rows <= 0) {
        rows = Integer.MAX_VALUE;
      }
      makeVector(rows);
      

      while (wdcode == 0) { rows--; if (rows < 0) break;
        MRecord rec = new MRecord(numCols, columns, colNames);
        for (int i = 0; i < numCols; i++) {
          rec.addElement(stat.getObject(i + 1));
        }
        records.addElement(rec);
        stat.next();
        wdcode = stat.getWdcode();
      }
      records.trimToSize();
      if (wdcode != 0) {
        closeStat();
      }
    } catch (MError ex) {
      try {
        close();
      } catch (MError exx) {
        exx.printStackTrace();
      }
      throw ex;
    }
  }
  



  public void close()
  {
    columns = null;
    if (records != null) {
      records.removeAllElements();
      records = null;
    }
    closeStat();
  }
  




  public void closeStat()
  {
    if (stat != null) {
      stat.closeStatement();
      if (autoStat) {
        stat.close();
      }
      stat = null;
    }
  }
  





  public int size()
  {
    return records.size();
  }
  





  public boolean isClosed()
  {
    return columns == null;
  }
  





  public boolean isEmpty()
  {
    return records.isEmpty();
  }
  





  public Enumeration elements()
  {
    return records.elements();
  }
  




  public MRecord firstElement()
  {
    return (MRecord)records.firstElement();
  }
  




  public MRecord get(int i)
  {
    return (MRecord)records.elementAt(i);
  }
  




  public MRecord elementAt(int i)
  {
    return (MRecord)records.elementAt(i);
  }
  




  public MRecord lastElementAt()
  {
    return (MRecord)records.lastElement();
  }
  





  public Enumeration keys()
  {
    if (columns == null) {
      throw new MError("Statement already closed");
    }
    return columns.keys();
  }
  





  public String toString()
  {
    return getClass().toString() + "[" + name + ":columns=" + numCols + ",records=" + size() + ",wdcode=" + wdcode + "\n" + sql + "\n]";
  }
  






  protected void finalize()
    throws Throwable
  {
    System.out.println("Finalizing: " + this);
    if (columns != null) {
      System.out.println("ResultSet not closed:\n" + this);
    }
    close();
    super.finalize();
  }
}
