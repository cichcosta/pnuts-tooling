package PT.Minimal.pnuts;

import PT.Minimal.geral.MBrowse;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MHashtable;
import PT.Minimal.geral.MMData;
import PT.Minimal.geral.MRow;
import PT.Minimal.util.MDate;
import PT.Minimal.util.MNumberUtil;
import PT.Minimal.util.MString;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Enumeration;
import pnuts.lang.Context;
import pnuts.lang.Package;
import pnuts.lang.Pnuts;
import pnuts.lang.PnutsException;
import pnuts.lang.PnutsFunction;
import pnuts.lang.Property;

public class MPnuts
{
  public static final String INIT_PNUT = "init.pnut";
  public static final String MINIMAL_PNUT = "PT/Minimal/pnuts/Minimal.pnut";
  public static final String PACKAGE_PNUT = "PT/Minimal/pnuts/MPackage.pnut";
  protected static boolean started = false;
  protected static boolean trace = false;
  
  protected static String _MPackage;
  
  protected static Context initContext;
  
  protected static MHashtable mpnuts;
  
  protected static MHashtable zglobals;
  
  protected static MHashtable Z;
  
  protected MHashtable zemc;
  protected MHashtable X;
  protected String sPackage;
  protected Context pContext;
  protected Package pPackage;
  public static final String TC_FORM = "FM";
  public static final String TC_ATRIBUTE = "AT";
  public static final String TC_ROW = "MR";
  public static final String TC_MANCODE = "MC";
  public static final String OB_SYSTEM = "EEEE0";
  
  public MPnuts(String pkg)
  {
    initMPnuts();
    
    sPackage = pkg;
    mpnuts.put(sPackage, this);
    initPackage();
  }
  
  public String toString()
  {
    return getClass().toString() + "[" + sPackage + "]";
  }
  








  public static void initMPnuts()
  {
    if (!started) {
      started = true;
      zglobals = new MHashtable(true);
      Z = zglobals;
      
      setTrace(Boolean.valueOf(System.getProperties().getProperty("PT.Minimal.pnutsTrace", "false")).booleanValue());
      mpnuts = new MHashtable();
      
      initContext = new Context();
      initContext.setErrorStream(null);
      try
      {
        Pnuts.load("init.pnut", initContext);
        Pnuts.load("PT/Minimal/pnuts/Minimal.pnut", initContext);
        

        mpnuts.getClass().getClassLoader();byte[] wbytes = PT.Minimal.geral.MUtil.getBytesFromStream(ClassLoader.getSystemResourceAsStream("PT/Minimal/pnuts/MPackage.pnut"));
        _MPackage = new String(wbytes);
      }
      catch (Exception ex) {
        System.err.println("\nErro durante o load do init.pnut e/ou Minimal.pnut!\n");
        
        new MError(ex).show();
        System.exit(1);
      }
    }
  }
  
  protected Context getContext(Package pkg)
  {
    Context ctxt = new Context(pkg);
    ctxt.setErrorStream(null);
    
    return ctxt;
  }
  






  public void initPackage()
  {
    zemc = new MHashtable(true);
    zemc.setName("zemc");
    X = zemc;
    


    pPackage = new MPnutsPackage(this, sPackage);
    pContext = new Context(pPackage);
    pContext.setErrorStream(null);
    pPackage.set("zemc", zemc);
    pPackage.set("X", X);
    pPackage.set("zglobals", zglobals);
    pPackage.set("Z", Z);
    zemc.put("zglobals", zglobals);
    zemc.put("Z", Z);
    
    eval(_MPackage);
  }
  





  public static void setTrace(boolean mode)
  {
    trace = mode;
    Pnuts.setVerbose(trace);
  }
  




  public static boolean getTrace()
  {
    return trace;
  }
  




  public static boolean isMPnut(String pkg)
  {
    boolean result = started ? mpnuts.containsKey(pkg) : false;
    
    return result;
  }
  





  public static MPnuts getMPnut(String pkg)
  {
    if (!started) {
      throw new MError("MPnut " + pkg + " não existe.");
    }
    try {
      return (MPnuts)mpnuts.get(pkg);
    } catch (RuntimeException err) {
      throw new MError(err);
    }
  }
  



  public static MPnuts findMPnut(String pkg)
    throws MError
  {
    MPnuts mpnut = null;
    if (started) {
      mpnut = (MPnuts)mpnuts.retrieve(pkg);
    }
    if (mpnut == null) {
      mpnut = new MPnuts(pkg);
    }
    return mpnut;
  }
  



  public Context getContext()
  {
    return pContext;
  }
  



  public Package getPackage()
  {
    return pPackage;
  }
  






  public void reload()
    throws MError
  {
    Package.remove(sPackage);
    

    zemc.clear();
    zemc = null;
    cache.clear();
    pPackage = null;
    pContext = null;
    
    initPackage();
  }
  



  public static void reloadAll()
  {
    if (!started) {
      return;
    }
    

    for (Enumeration e = mpnuts.elements(); e.hasMoreElements();)
    {


      MPnuts pnut = (MPnuts)e.nextElement();
      
      pnut.reload();
    }
    
    MMData.clearJExps();
  }
  




  public static void clearAll()
  {
    if (!started) {
      return;
    }
    

    for (Enumeration e = mpnuts.keys(); e.hasMoreElements();) {
      String pkg = (String)e.nextElement();
      Package.remove(pkg);
      
      MPnuts mp = (MPnuts)mpnuts.get(pkg);
      zemc.clear();
      zemc = null;
      pPackage = null;
      pContext = null;
    }
    
    mpnuts.clear();
    _MPackage = null;
    initContext = null;
    
    zglobals.clear();
    zglobals = null;
    
    started = false;
  }
  





  public void set(String var, Object obj)
    throws MError
  {
    if (obj != null) {
      pPackage.set(var, obj);
    } else {
      pPackage.clear(var);
    }
  }
  




  public Object get(String var)
  {
    if (containsKey(var)) return pPackage.get(var);
    return null;
  }
  



  public void unset(String var)
    throws MError
  {
    pPackage.clear(var);
  }
  





  public boolean containsKey(String var)
  {
    return pPackage.defined(var);
  }
  





  public void put(Object var, Object value)
  {
    if (value == null) {
      zemc.remove(var);
    } else {
      zemc.put(var, value);
    }
  }
  



  public Object retrieve(String var)
    throws MError
  {
    return zemc.retrieve(var);
  }
  



  public void remove(String var)
    throws MError
  {
    zemc.remove(var);
  }
  







  public Object eval(String pnut)
    throws MError
  {
    Object obj = null;
    if (pnut != null) {
      try {
        obj = Pnuts.eval(pnut, pContext);
      }
      catch (PnutsException ex) {
        Throwable e = ex.getThrowable();
        if (e == null) {
          throw new MError(ex);
        }
        if ((e instanceof MError)) {
          throw ((MError)e);
        }
        if ((e instanceof Error)) {
          throw ((Error)e);
        }
        throw new MError((Exception)e);
      }
      catch (Exception ex) {
        throw new MError(ex);
      }
    }
    return obj;
  }
  






  public Object eval(PnutsFunction func)
    throws MError
  {
    return eval(func, null);
  }
  







  public Object eval(PnutsFunction func, Object[] parms)
    throws MError
  {
    Object obj = null;
    if (func != null) {
      try {
        if (parms == null) {
          parms = new Object[0];
        }
        obj = func.call(parms, pContext);
      }
      catch (PnutsException ex) {
        Throwable e = ex.getThrowable();
        if (e == null) {
          throw new MError(ex);
        }
        if ((e instanceof Error)) {
          throw ((Error)e);
        }
        MError err;
        if ((e instanceof MError)) {
          MError err = (MError)e;
          if (err.isFromPnut()) {
            throw err;
          }
        } else {
          err = new MError((Exception)e);
        }
        err.setAuxMessage(err.getAuxMessage() + "\n-------------------------------------------\n" + func.getName() + "()");
        throw err;
      } catch (Exception ex) {
        if ((ex instanceof MError)) {
          throw ((MError)ex);
        }
        
        throw new MError(ex);
      }
    }
    return obj;
  }
  









  public PnutsFunction evalFunc(String pnut, Object key)
  {
    if (pnut == null) {
      return null;
    }
    try
    {
      Object f = Pnuts.eval(pnut, getContext(pContext.getCurrentPackage()));
      if (!(f instanceof PnutsFunction)) {
        throw new MError("Mpnuts.evalFunc: \"" + key + "\" não é uma função.");
      }
      return (PnutsFunction)f;
    }
    catch (PnutsException ex) {
      Throwable e = ex.getThrowable();
      if (e == null) {
        throw new MError(ex);
      }
      if ((e instanceof MError)) {
        throw ((MError)e);
      }
      if ((e instanceof Error)) {
        throw ((Error)e);
      }
      throw new MError((Exception)e);
    }
    catch (Exception ex) {
      throw new MError(ex);
    }
  }
  






  public boolean evalCondition(String cond)
    throws MError
  {
    if ((cond == null) || (cond.trim().length() == 0)) {
      return true;
    }
    String s = cond.trim();
    Object obj = eval(s);
    if ((obj == null) || (!(obj instanceof Boolean))) {
      throw new MError("Condição inválida: \"" + cond.trim() + "\" !!!");
    }
    
    return ((Boolean)obj).booleanValue();
  }
  







  public String evalValues(String str)
    throws MError
  {
    return evalValues(str, false, false);
  }
  






  public String evalFzemcValues(String str)
    throws MError
  {
    return evalValues(str, true, false);
  }
  






  public String evalFormatedValues(String str)
    throws MError
  {
    return evalValues(str, false, true);
  }
  











  private String evalValues(String str, boolean fzemc, boolean format)
    throws MError
  {
    StringReader sr = new StringReader(str);
    StringWriter sw = new StringWriter();
    try
    {
      char[] cc = new char[str.length()];
      int n = -1;
      
      for (int c = sr.read(); c != -1; c = sr.read())
      {
        if (n == -1)
        {
          if (c == 91) {
            n = 0;
          } else {
            sw.write(c);
          }
          
        }
        else if (c == 93) {
          String s = new String(cc, 0, n).trim();
          
          sw.write(evalVar(s, fzemc, format));
          n = -1;
        } else if (c == 91) {
          if (n == 0) {
            sw.write(c);
            n = -1;
          } else {
            throw new MError("'[' encontrado dentro de '[...]'");
          }
        } else {
          cc[(n++)] = ((char)c);
        }
      }
      
      if (n != -1) {
        throw new MError("encontrado '[' sem ']'");
      }
      
      sr.close();
      String sout = sw.toString();
      sw.close();
      return sout;
    } catch (IOException ex) {
      throw new MError(ex);
    }
  }
  










  private String evalVar(String s, boolean fzemc, boolean format)
    throws MError
  {
    String sorig = s;
    try {
      if ((fzemc) && (!s.startsWith("FZEMC."))) {
        s = "[" + s + "]";
      }
      else {
        String sform = null;
        Object val; if (s.toLowerCase().startsWith("pnuts:")) {
          Object val = eval(s.substring(6));
          if (val == null) val = "";
        } else {
          int i = s.indexOf(':');
          if (i != -1) {
            sform = s.substring(i + 1).trim();
            s = s.substring(0, i);
          }
          int j = s.indexOf('.');
          Object val; if (j > 0) {
            String rec = s.substring(0, j);
            String var = s.substring(j + 1);
            Object obj = zemc.get(rec);
            Object val; if (format) { Object val;
              if ((obj instanceof MRow)) {
                val = ((MRow)obj).getFormatedValue(var); } else { Object val;
                if ((obj instanceof MForm)) {
                  val = ((MForm)obj).getFormatedValue(var);
                } else
                  val = ((Property)obj).get(var);
              }
            } else {
              val = ((Property)obj).get(var);
            }
          } else {
            val = zemc.get(s);
          }
        }
        boolean is_number = val instanceof Number;
        if ((is_number) && (!(val instanceof BigDecimal))) {
          s = new BigDecimal(((Number)val).doubleValue()).toString();
        } else {
          s = val.toString();
        }
        if (sform != null) {
          if (Character.isDigit(sform.charAt(0))) {
            int k = Integer.valueOf(sform).intValue();
            if (k == 0) {
              s = MString.trimRight(s);
            }
            else if (s.length() >= k) {
              s = s.substring(0, k);
            }
            else if (is_number) {
              s = MString.fillLeft(s, k);
            } else {
              s = MString.fillRight(s, k);
            }
            

          }
          else
          {
            char c = sform.charAt(0);
            sform = sform.substring(1);
            switch (c) {
            case 'E':  s = MNumberUtil.toText(new BigDecimal(s));
              break;
            case 'N':  int i = sform.indexOf(':');
              if (i == -1) {
                s = MNumberUtil.toString(s, sform);
              } else {
                int n = Integer.valueOf(sform.substring(i + 1)).intValue();
                sform = sform.substring(0, i);
                s = MNumberUtil.toString(s, sform, n);
              }
              break;
            case 'D':  s = MDate.valueOf(s, sform).toString();
              break;
            case 'L':  s = s.toLowerCase();
              break;
            case 'U':  s = s.toUpperCase();
              break;
            
            case 'F': 
              sform = sform.toLowerCase();
              if ("trim".equals(sform)) {
                s = s.trim();
              } else if ("ltrim".equals(sform)) {
                s = MString.trimLeft(s);
              } else if ("rtrim".equals(sform)) {
                s = MString.trimRight(s);
              } else
                throw new MError("Função '" + sform + "' não encontrada.");
              break;
            case 'G': case 'H': case 'I': case 'J': case 'K': case 'M': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': default: 
              throw new RuntimeException("Modificador '" + sform + "' não encontrado.");
            


            }
            
          }
        }
      }
    }
    catch (Exception ex)
    {
      if ((ex instanceof MError)) {
        throw ((MError)ex);
      }
      MError err = new MError(ex);
      err.setMessage("evalVar(\"" + sorig + "\") - " + ex.getMessage());
      throw err;
    }
    return s;
  }
  














  public void setRow(MRow row)
  {
    if (row != null) {
      zemc.put("row", row);
      pContext.set("row", row);
    } else {
      zemc.remove("row");
      pContext.getCurrentPackage().clear("row");
    }
  }
  






  public void setForm(MForm form)
    throws MError
  {
    if (form != null) {
      zemc.put("form", form);
      zemc.put("m", form);
      pContext.set("form", form);
      pContext.set("m", form);
    } else {
      zemc.remove("form");
      zemc.remove("m");
      pContext.getCurrentPackage().clear("form");
      pContext.getCurrentPackage().clear("m");
    }
  }
  




























  protected static final Object _NULL = new Object() { public String toString() { return "NULL"; } };
  protected MHashtable cache = new MHashtable(true);
  





  private Object getFunc(String name)
  {
    return cache.retrieve(name);
  }
  




  private PnutsFunction getExp(Object key)
  {
    String fname = MPnutsExp.keyToName(key);
    Object f = getFunc(fname);
    if (f != null) {
      if (f == _NULL) return null;
      return (PnutsFunction)f;
    }
    
    MPnutsExp pexp = (MPnutsExp)MMData.getJExp((String)key);
    if (pexp == null) {
      cache.put(fname, _NULL);
      cache.put("MIN_" + fname, _NULL);
      cache.put("CLI_" + fname, _NULL);
      return null;
    }
    
    f = _NULL;
    String exp = pexp.getMinExp();
    if (exp != null) {
      f = evalFunc(exp, key);
    }
    
    cache.put(pexp.getMinName(), f);
    
    f = _NULL;
    exp = pexp.getCliExp();
    if (exp != null) {
      f = evalFunc(exp, key);
    }
    
    cache.put(pexp.getCliName(), f);
    
    exp = pexp.getExp();
    f = evalFunc(exp, key);
    

    cache.put(pexp.getName(), f);
    return (PnutsFunction)f;
  }
  





  public Object evalPnut(Object key, Object expr)
  {
    if (expr == null) {
      return null;
    }
    if (!(expr instanceof PnutsFunction)) {
      throw new MError("MIN-999994E", new Object[] { "eval(" + key + ")" });
    }
    
    PnutsFunction f = (PnutsFunction)expr;
    
    return eval(f);
  }
  





  public boolean existsPnut(String iob, String itc, String icomp, String ifase)
  {
    Object key = MPnutsExp.getKey(iob, itc, icomp, ifase);
    Object exp = getExp(key);
    return exp != null;
  }
  


  public Object evalSystemPnut(String itc, String icomp, String ifase)
    throws MError
  {
    Object key = MPnutsExp.getKey("EEEE0", itc, icomp, ifase);
    Object expr = getExp(key);
    return evalPnut(key, expr);
  }
  






  public Object evalPnut(String iob, String itc, String icomp, String ifase, boolean check)
    throws MError
  {
    Object key = MPnutsExp.getKey(iob, itc, icomp, ifase);
    Object expr = getExp(key);
    if ((expr == null) && (check)) {
      key = MPnutsExp.getKey("*", itc, icomp, ifase);
      expr = getExp(key);
    }
    return evalPnut(key, expr);
  }
  





  public Object evalPnut(String iob, String itc, String icomp, String ifase)
    throws MError
  {
    return evalPnut(iob, itc, icomp, ifase, true);
  }
  







  public Object evalBrowsePnut(MBrowse form, String ifase)
    throws MError
  {
    return evalPnut(form.getEnt(), "FM", form.getName(), ifase);
  }
  





  public Object evalFormPnut(MForm form, String icomp, String ifase)
    throws MError
  {
    evalSystemPnut("FM", "EEEE0" + icomp.substring(5), ifase);
    
    return evalPnut(form.getEnt().substring(0, 5), "FM", icomp, ifase);
  }
  





  public Object evalFormPnut(MForm form, String ifase)
    throws MError
  {
    return evalFormPnut(form, form.getName(), ifase);
  }
  





  public Object evalFieldPnut(MForm form, String icomp, String ifase)
    throws MError
  {
    evalSystemPnut("AT", icomp, ifase);
    
    String ent = form.getEnt().substring(0, 5);
    String fname = form.getName();
    Object key = MPnutsExp.getKey(ent, "AT", icomp + "_" + fname, ifase);
    Object expr = getExp(key);
    
    if (expr == null) {
      key = MPnutsExp.getKey(ent, "AT", icomp, ifase);
      expr = getExp(key);
    }
    return evalPnut(key, expr);
  }
  





  public Object evalRowPnut(MRow row, String icomp, String ifase)
    throws MError
  {
    evalSystemPnut("MR", icomp, ifase);
    
    return evalPnut(row.getEnt().substring(0, 4), "MR", icomp, ifase);
  }
  





  public PnutsFunction loadFunction(String name)
  {
    Object f = getFunc(name);
    if (f != null) {
      if (f == _NULL) return null;
      throw new MError("MIN-999999E");
    }
    
    Object key = MPnutsExp.nameToKey(name);
    




    if (key == null) { return null;
    }
    
    getExp(key);
    f = getFunc(name);
    if (f == null) {
      throw new MError("MIN-999999E");
    }
    if (f == _NULL) { return null;
    }
    return (PnutsFunction)f;
  }
  






  public Object evalFunction(String name)
  {
    return evalFunction(name, null);
  }
  





  public Object evalFunction(String name, Object[] parms)
  {
    PnutsFunction f = loadFunction(name);
    if (f == null) return null;
    return eval(f, parms);
  }
  




  public boolean existsFunction(String name)
  {
    PnutsFunction f = loadFunction(name);
    return f != null;
  }
}
