package PT.Minimal.pnuts;

import PT.Minimal.geral.MError;
import PT.Minimal.geral.MHashtable;
import java.io.Serializable;





















public class MPnutsExp
  implements Serializable
{
  public static final String KEY_SEP = "+";
  protected Object key;
  protected String name;
  protected String cfunc;
  protected String func;
  protected String fmin;
  protected String fcli;
  private static String wstart_func = "import(\"PT.Minimal.geral.*\");import(\"PT.Minimal.util.*\");import(\"PT.Minimal.pnuts.*\");";
  





  public MPnutsExp(String fname, String fexp)
  {
    key = fname;
    name = makeName(fname);
    cfunc = fname;
    
    func = ("function " + fname + "{" + wstart_func + fexp + "\n}\n");
  }
  






  public MPnutsExp(String prefix, MHashtable mjex)
  {
    String iob = (String)mjex.get("i_ob");
    String itc = (String)mjex.get("i_tc");
    String icomp = (String)mjex.get("i_comp");
    String ifase = (String)mjex.get("i_fase");
    
    cfunc = ((String)mjex.get("cfunc"));
    name = makeName(cfunc);
    key = getKey(iob, itc, icomp, ifase, cfunc);
    
    if ((cfunc.startsWith("MIN_")) || (cfunc.startsWith("CLI_"))) {
      cfunc = cfunc.substring(4);
    }
    
    String exp = "function " + prefix + "_" + cfunc + "{" + wstart_func + (String)mjex.get("text") + "\n}\n";
    


    if (prefix.equals("MIN")) { fmin = exp;
    } else if (prefix.equals("CLI")) fcli = exp; else {
      throw new MError("MIN-910007E", new String[] { cfunc });
    }
  }
  




  public void addExp(String prefix, MHashtable mjex)
  {
    String iob = (String)mjex.get("i_ob");
    String itc = (String)mjex.get("i_tc");
    String icomp = (String)mjex.get("i_comp");
    String ifase = (String)mjex.get("i_fase");
    
    String wcfunc = (String)mjex.get("cfunc");
    Object wkey = getKey(iob, itc, icomp, ifase, wcfunc);
    
    if (!((String)key).equals((String)wkey)) {
      throw new MError("MIN-910008E", new String[] { cfunc });
    }
    
    String exp = "function " + prefix + "_" + cfunc + "{" + wstart_func + (String)mjex.get("text") + "\n}\n";
    

    if (prefix.equals("MIN")) { fmin = exp;
    } else if (prefix.equals("CLI")) fcli = exp; else {
      throw new MError("MIN-910007E", new String[] { cfunc });
    }
  }
  
  public Object getKey() {
    return key;
  }
  
  public String getExp() {
    if (func == null) {
      String prefix = fcli == null ? "MIN_" : "CLI_";
      func = ("function " + cfunc + "{\n" + prefix + cfunc + "\n}\n");
    }
    return func;
  }
  
  public String getMinExp() { return fmin; }
  
  public String getCliExp() {
    return fcli;
  }
  
  public String getName() {
    return name;
  }
  
  public String getMinName() { return "MIN_" + name; }
  
  public String getCliName() {
    return "CLI_" + name;
  }
  







  public static String makeName(String cfunc)
  {
    int i = cfunc.indexOf("(");
    if (i <= 0) return cfunc;
    return cfunc.substring(0, i);
  }
  




  public static String keyToName(Object key)
  {
    return ((String)key).replace('*', 'x').replace('+', '_').toLowerCase();
  }
  







  public static Object nameToKey(String name)
  {
    if ((name.startsWith("MIN_")) || (name.startsWith("CLI_"))) {
      name = name.substring(4);
    }
    if (!name.equals(name.toLowerCase())) { return null;
    }
    StringBuffer key = new StringBuffer(name);
    









    if (name.startsWith("x_")) {
      key.setCharAt(0, '*');
    }
    

    int n = name.indexOf('_');
    if (n == -1) return null;
    key.setCharAt(n, '+');
    

    n = name.indexOf('_', n + 1);
    if (n == -1) return null;
    key.setCharAt(n, '+');
    

    n = name.lastIndexOf('_');
    if (n == -1) return null;
    key.setCharAt(n, '+');
    
    return key.toString().toUpperCase();
  }
  





















  public static Object getKey(MHashtable mjex)
  {
    return getKey(mjex.get("i_ob"), mjex.get("i_tc"), mjex.get("i_comp"), mjex.get("i_fase"), mjex.get("cfunc"));
  }
  







  public static Object getKey(String iob, String itc, String icomp, String ifase)
  {
    return getKey(iob, itc, icomp, ifase, "*");
  }
  







  public static Object getKey(Object iob, Object itc, Object icomp, Object ifase)
  {
    return getKey(iob, itc, icomp, ifase, "*");
  }
  








  public static Object getKey(Object iob, Object itc, Object icomp, Object ifase, Object cfunc)
  {
    return getKey(iob.toString(), itc.toString(), icomp.toString(), ifase.toString(), cfunc.toString());
  }
  








  private static Object getKey(String iob, String itc, String icomp, String ifase, String cfunc)
  {
    return iob.trim().toUpperCase() + "+" + itc.trim().toUpperCase() + "+" + icomp.trim().toUpperCase() + "+" + ifase.trim().toUpperCase();
  }
}
