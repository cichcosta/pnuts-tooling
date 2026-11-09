package PT.Minimal.geral;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
























public class MClass
{
  private MClass() {}
  
  public static Class getClass(String name)
    throws MError
  {
    try
    {
      return Class.forName(name);
    } catch (ClassNotFoundException ex) {
      MError err = new MError("MIN-900020E", new Object[] { name, ex.getMessage(), ex });
      err.setNextException(ex);
      throw err;
    }
  }
  






  public static Object getObject(String name)
    throws MError
  {
    return getObject(name, null);
  }
  






  public static Object getObject(Object obj)
    throws MError
  {
    return getObject(obj.getClass(), null);
  }
  







  public static Object getObject(String name, Object[] parms)
    throws MError
  {
    return getObject(getClass(name), parms);
  }
  







  public static Object getObject(Object obj, Object[] parms)
    throws MError
  {
    return getObject(obj.getClass(), parms);
  }
  








  public static Object getObject(Class xclass, Object[] parms)
    throws MError
  {
    try
    {
      if ((parms == null) || (parms.length == 0)) {
        return xclass.newInstance();
      }
      
      int n = parms.length;
      Class[] cparms = new Class[n];
      for (int i = 0; i < n; i++) {
        cparms[i] = (parms[i] == null ? null : parms[i].getClass());
      }
      
      Constructor constr = xclass.getConstructor(cparms);
      return constr.newInstance(parms);
    } catch (IllegalAccessException ex) {
      MError err = new MError("MIN-900021E", new Object[] { xclass.toString(), ex.getMessage(), ex });
      err.setNextException(ex);
      throw err;
    } catch (InstantiationException ex) {
      MError err = new MError("MIN-900022E", new Object[] { xclass.toString(), ex.getMessage(), ex });
      err.setNextException(ex);
      throw err;
    }
    catch (NoSuchMethodException ex) {
      MError err = new MError("MIN-900023E", new Object[] { xclass.toString(), "", ex });
      err.setNextException(ex);
      throw err;
    } catch (InvocationTargetException ex) {
      if ((ex.getTargetException() instanceof MError)) {
        throw ((MError)ex.getTargetException());
      }
      String msg = ex.getTargetException().getMessage();
      if (msg == null) {
        msg = "null";
      }
      MError err = new MError("MIN-900024E", new Object[] { xclass.toString(), msg, ex.getTargetException() });
      if ((ex.getTargetException() instanceof Exception)) {
        err.setNextException((Exception)ex.getTargetException());
      } else {
        ex.getTargetException().printStackTrace();
      }
      throw err;
    }
  }
  







  public static Object[] getArray(String name, int length)
    throws MError
  {
    return getArray(getClass(name), length);
  }
  






  public static Object[] getArray(Object obj, int length)
    throws MError
  {
    return getArray(obj.getClass(), length);
  }
  






  public static Object[] getArray(String name, int[] dim)
    throws MError
  {
    return getArray(getClass(name), dim);
  }
  






  public static Object[] getArray(Object obj, int[] dim)
    throws MError
  {
    return getArray(obj.getClass(), dim);
  }
  








  public static Object[] getArray(Class xclass, int length)
    throws MError
  {
    return (Object[])Array.newInstance(xclass, length);
  }
  






  public static Object[] getArray(Class xclass, int[] dim)
    throws MError
  {
    return (Object[])Array.newInstance(xclass, dim);
  }
}
