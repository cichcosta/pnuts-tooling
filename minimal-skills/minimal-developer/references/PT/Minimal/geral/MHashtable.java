package PT.Minimal.geral;

import PT.Minimal.util.MNumberUtil;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import pnuts.lang.Property;
















public class MHashtable
  extends Hashtable
  implements Property, Serializable
{
  protected static final Object NULL = new Object() {
    public String toString() { return "NULL"; } };
  protected boolean changes = false;
  protected boolean exists = false;
  protected boolean nulls = false;
  protected String name = null;
  








  public MHashtable() {}
  







  public MHashtable(boolean allowChange)
  {
    changes = allowChange;
  }
  







  public MHashtable(boolean allowChange, boolean allowExists)
  {
    changes = allowChange;
    exists = allowExists;
  }
  







  public MHashtable(boolean allowChange, boolean allowExists, boolean allowNulls)
  {
    changes = allowChange;
    exists = allowExists;
    nulls = allowNulls;
  }
  







  public MHashtable(Hashtable h)
  {
    for (Enumeration e = h.keys(); e.hasMoreElements();) {
      Object key = e.nextElement();
      put(key, h.get(key));
    }
  }
  








  public MHashtable(Object[] vec)
  {
    if (vec != null) {
      for (int i = 0; i < vec.length; i++) {
        put("par" + new Integer(i + 1).toString(), vec[i]);
      }
    }
  }
  








  public MHashtable(Vector vec)
  {
    if (vec != null) {
      for (int i = 0; i < vec.size(); i++) {
        put("par" + new Integer(i + 1).toString(), vec.elementAt(i));
      }
    }
  }
  






  public void setName(String name)
  {
    this.name = name;
  }
  




  public String getName()
  {
    return name;
  }
  




  public Object getID(Object key)
  {
    if (name == null) return key;
    return name + "." + key;
  }
  




  public void setAllowChange(boolean allowChange)
  {
    changes = allowChange;
  }
  


  public boolean getAllowChange()
  {
    return changes;
  }
  




  public void setAllowExists(boolean allowExist)
  {
    exists = allowExist;
  }
  


  public boolean getAllowExists()
  {
    return exists;
  }
  




  public void setAllowNulls(boolean allowNulls)
  {
    nulls = allowNulls;
  }
  


  public boolean getAllowNulls()
  {
    return nulls;
  }
  






  public Object get(String key)
  {
    return get(key);
  }
  






  public Object get(Object key)
  {
    Object value = super.get(key);
    if ((value == null) && (!exists)) {
      throw new MError("MIN-910002E", new Object[] { getID(key) });
    }
    if (value == NULL) {
      value = null;
    }
    
    return value;
  }
  






  public String getString(Object key)
  {
    Object obj = get(key);
    if (obj == null) {
      return null;
    }
    if ((obj instanceof String)) {
      return (String)obj;
    }
    return obj.toString();
  }
  






  public int getInt(Object key)
  {
    Object obj = get(key);
    if (obj == null) {
      return 0;
    }
    if ((obj instanceof Number)) {
      return ((Number)obj).intValue();
    }
    try {
      return MNumberUtil.toInt(obj);
    } catch (NumberFormatException ex) {
      throw new MError("MIN-910005E", new Object[] { getID(key), ex.getMessage(), obj, ex });
    }
  }
  






  public double getDouble(Object key)
  {
    Object obj = get(key);
    if (obj == null) {
      return 0.0D;
    }
    if ((obj instanceof Number)) {
      return ((Number)obj).intValue();
    }
    try {
      return new Double(obj.toString()).doubleValue();
    } catch (NumberFormatException ex) {
      throw new MError("MIN-910005E", new Object[] { getID(key), ex.getMessage(), obj, ex });
    }
  }
  






  public BigDecimal getBigDecimal(Object key)
  {
    Object obj = get(key);
    if (obj == null) {
      return null;
    }
    if ((obj instanceof BigDecimal)) {
      return (BigDecimal)obj;
    }
    if ((obj instanceof Number)) {
      return new BigDecimal(((Number)obj).doubleValue());
    }
    try {
      return new BigDecimal(obj.toString());
    } catch (NumberFormatException ex) {
      throw new MError("MIN-910005E", new Object[] { getID(key), ex.getMessage(), obj, ex });
    }
  }
  







  public void set(String key, Object value)
  {
    set(key, value);
  }
  







  public void set(Object key, Object value)
  {
    if ((!changes) && (!containsKey(key))) {
      throw new MError("MIN-910002E", new Object[] { getID(key) });
    }
    if (value == null) {
      if (!nulls) {
        throw new MError("MIN-910004E", new Object[] { getID(key) });
      }
      System.out.println("ATENÇÃO: variável '" + key + "' com valor nulo.");
      value = NULL;
    }
    
    super.put(key, value);
  }
  






  public void unset(Object key)
  {
    if (!changes) {
      throw new MError("MIN-910003E", new Object[] { getID(key) });
    }
    if ((!exists) && (!containsKey(key))) {
      throw new MError("MIN-910002E", new Object[] { getID(key) });
    }
    super.remove(key);
  }
  







  public Object put(Object key, Object value)
  {
    if (value == null) {
      if (!nulls) {
        throw new MError("MIN-910004E", new Object[] { getID(key) });
      }
      System.out.println("ATENÇÃO: variável '" + key + "' com valor nulo.");
      value = NULL;
    }
    return super.put(key, value);
  }
  




  public Object retrieve(Object key)
  {
    Object obj = super.get(key);
    if (obj == NULL) {
      return null;
    }
    return obj;
  }
}
