package PT.Minimal.sql;

import PT.Minimal.geral.MError;
import PT.Minimal.geral.MHashtable;
import PT.Minimal.util.MString;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Vector;
import pnuts.lang.Property;




















public class MRecord
  extends Vector
  implements Property
{
  protected MHashtable columns;
  protected Vector colNames;
  
  public MRecord(String name)
  {
    super(10, 5);
    columns = new MHashtable();
    columns.setName(name);
    colNames = new Vector(10, 5);
  }
  





  public MRecord(MHashtable columns)
  {
    this(columns.size(), columns);
  }
  






  public MRecord(int size, MHashtable columns)
  {
    super(size, 1);
    this.columns = columns;
    colNames = new Vector(size, 5);
    
    colNames.setSize(size);
    for (Enumeration e = columns.elements(); e.hasMoreElements();) {
      Object key = e.nextElement();
      colNames.setElementAt(key, ((Integer)columns.get(key)).intValue());
    }
  }
  






  public MRecord(int size, MHashtable columns, Vector colNames)
  {
    super(size, 1);
    this.columns = columns;
    this.colNames = colNames;
  }
  






  public String getName()
  {
    return columns.getName();
  }
  





  public Object getID(String name)
  {
    return columns.getID(name);
  }
  








  public int getIndex(String name)
  {
    return getIndex(name, false);
  }
  








  public int getIndex(String name, boolean create)
  {
    int i = retrieveIndex(name, create);
    if (i == -1) {
      throw new MError("MIN-910002E", new Object[] { getID(name) });
    }
    return i;
  }
  








  public int retrieveIndex(String name)
  {
    return retrieveIndex(name, false);
  }
  









  public int retrieveIndex(String name, boolean create)
  {
    int i = -1;
    if (MString.isInteger(name)) {
      try {
        i = Integer.valueOf(name).intValue();
      } catch (NumberFormatException ex) {}
    } else {
      Integer I = (Integer)columns.retrieve(name);
      if ((create) && (I == null)) {
        synchronized (columns) {
          I = (Integer)columns.retrieve(name);
          if (I == null) {
            I = new Integer(columns.size());
            columns.put(name, I);
            colNames.addElement(name);
          }
        }
      }
      if (I != null) {
        i = I.intValue();
      }
    }
    if ((i < -1) || (i > columns.size())) {
      i = -1;
    }
    return i;
  }
  








  public void set(String name, Object value)
  {
    int i = getIndex(name, true);
    if (i >= size()) {
      setSize(i + 1);
    }
    setElementAt(value, i);
  }
  







  public void put(String name, Object value)
  {
    int i = retrieveIndex(name, true);
    if (i >= size()) {
      setSize(i + 1);
    }
    setElementAt(value, i);
  }
  






  public Object retrieve(String name)
  {
    int i = retrieveIndex(name, false);
    if (i < size()) {
      return elementAt(i);
    }
    return null;
  }
  







  public Object get(String name)
  {
    int i = getIndex(name);
    if (i < size()) {
      return elementAt(i);
    }
    return null;
  }
  






  public BigDecimal getBigDecimal(String name)
  {
    Object obj = get(name);
    if (obj == null) {
      return null;
    }
    if ((obj instanceof BigDecimal))
    {
      return ((BigDecimal)obj).setScale(15, 4);
    }
    if ((obj instanceof Number)) {
      return new BigDecimal(((Number)obj).doubleValue());
    }
    try {
      return new BigDecimal(obj.toString());
    } catch (ClassCastException ex) {
      throw new MError("MIN-910005E", new Object[] { getID(name), ex.getMessage(), obj, ex });
    }
  }
  







  public double getDouble(String name)
  {
    Object obj = get(name);
    if (obj == null) {
      return 0.0D;
    }
    if ((obj instanceof Number)) {
      return ((Number)obj).intValue();
    }
    try {
      return new Double(obj.toString()).doubleValue();
    } catch (ClassCastException ex) {
      throw new MError("MIN-910005E", new Object[] { getID(name), ex.getMessage(), obj, ex });
    }
  }
  







  public int getInt(String name)
  {
    Object obj = get(name);
    if (obj == null) {
      return 0;
    }
    if ((obj instanceof Number)) {
      return ((Number)obj).intValue();
    }
    try {
      return new Integer(obj.toString()).intValue();
    } catch (ClassCastException ex) {
      throw new MError("MIN-910005E", new Object[] { getID(name), ex.getMessage(), obj, ex });
    }
  }
  







  public String getString(String name)
  {
    Object obj = get(name);
    if (obj == null) {
      return null;
    }
    if ((obj instanceof String)) {
      return (String)obj;
    }
    return obj.toString();
  }
  










  public int colSize()
  {
    return columns.size();
  }
  





  public Enumeration keys()
  {
    return colNames.elements();
  }
  





  public boolean containsKey(String name)
  {
    return columns.containsKey(name);
  }
  





  public String toString()
  {
    return getClass().toString() + "[" + getName() + "," + colSize() + "]";
  }
  





  public String toLongString()
  {
    String title = "Columns";
    int maxLen = title.length();
    int index = 0; for (int size = colNames.size(); index < size; index++) {
      int len = ((String)colNames.get(index)).length();
      if (len > maxLen) {
        maxLen = len;
      }
    }
    
    StringBuffer str = new StringBuffer(toString());
    str.append("\n\t").append(MString.fillRight(title, maxLen)).append("  T Len");
    int index = 0; for (int size = colNames.size(); index < size; index++) {
      str.append("\n\t").append(MString.fillRight(colNames.elementAt(index).toString(), maxLen)).append("  ");
      Object val = elementAt(index);
      if (val == null) {
        str.append("?     = null");
      } else if ((val instanceof Number)) {
        str.append("N     = ").append(val);
      } else if ((val instanceof String)) {
        int len = val.toString().length();
        str.append("C").append(MString.fillLeft(String.valueOf(len), 4)).append(" = '").append(val).append("'");
      } else {
        str.append("?     = ").append(val);
      }
    }
    return str.toString();
  }
}
