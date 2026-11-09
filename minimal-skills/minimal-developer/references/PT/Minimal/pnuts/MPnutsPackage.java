package PT.Minimal.pnuts;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import pnuts.lang.Package;











public class MPnutsPackage
  extends Package
{
  MPnuts mpnut;
  
  MPnutsPackage(MPnuts p, String name)
  {
    super(name);
    
    packages.put(name, this);
    
    parent = this;
    
    mpnut = p;
  }
  
  public boolean defined(String var) {
    boolean found = super.defined(var);
    if (!found) {
      found = mpnut.loadFunction(var) != null;
      

      if ((!found) && (MPnuts.getTrace())) System.out.println("MPnuts " + name + "." + var + " not defined.");
      if (found) System.out.println(name + ".defined(" + var + "): " + super.defined(var));
    }
    return found;
  }
  
  public void set(String var, Object value) { if (MPnuts.getTrace()) System.out.println("MPnuts " + name + "." + var + "= " + value);
    super.set(var, value);
  }
  
  public Object get(String var) { Object value = super.get(var);
    if (MPnuts.getTrace()) System.out.println("MPnuts " + name + "." + var + ": " + value);
    return value;
  }
  
  public void clear(String var) { if (MPnuts.getTrace()) System.out.println("MPnuts " + name + ".clear(" + var + ")");
    super.clear(var);
  }
  
  public Enumeration elements() {
    Enumeration e = super.elements();
    if (MPnuts.getTrace()) System.out.println("MPnuts " + name + ".elements() ? " + e);
    return e;
  }
  
  public Enumeration keys() { Enumeration e = super.keys();
    if (MPnuts.getTrace()) System.out.println("MPnuts " + name + ".keys() ? " + e);
    return e;
  }
}
