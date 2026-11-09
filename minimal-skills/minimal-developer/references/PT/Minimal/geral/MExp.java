package PT.Minimal.geral;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;

public class MExp
{
  private String wiob;
  private String watrib;
  private String wtexp;
  private String wcondition;
  private String wassobject;
  private String wselect = " ";
  private String wfrom = " ";
  private String wwhere = " ";
  private String wgroup = " ";
  private String worder = " ";
  private String wtargs = " ";
  
  private String wdestatrib = null;
  
  private String wtab;
  
  private String[] wargs;
  private String[] wcond;
  private String[] wout;
  private String[] wotherout;
  private String wtext;
  private String wtostring = "";
  
  public MExp copyExp() {
    MExp wexp = new MExp(wiob, watrib, wtexp);
    wcondition = wcondition;
    wassobject = wassobject;
    wselect = wselect;
    wfrom = wfrom;
    wwhere = wwhere;
    wgroup = wgroup;
    worder = worder;
    wtargs = wtargs;
    wdestatrib = wdestatrib;
    wtab = wtab;
    wtostring = wtostring;
    if (wargs == null) wargs = null; else
      System.arraycopy(wargs, 0, wargs, 0, wargs.length);
    if (wcond == null) wcond = null; else
      System.arraycopy(wcond, 0, wcond, 0, wcond.length);
    if (wout == null) wout = null; else
      System.arraycopy(wout, 0, wout, 0, wout.length);
    if (wotherout == null) wotherout = null; else {
      System.arraycopy(wotherout, 0, wotherout, 0, wotherout.length);
    }
    return wexp;
  }
  
  public MExp(Hashtable wmexs) {
    wiob = ((String)wmexs.get("i_ob"));
    watrib = ((String)wmexs.get("i_comp"));
    wtexp = ((String)wmexs.get("c_exp"));
    wtostring = ("OBJ:" + wiob + " COMP:" + watrib + " TE:" + wtexp);
    
    setCondition((String)wmexs.get("t_cond"));
    setAssociateObject((String)wmexs.get("t_obh"));
    setSelect((String)wmexs.get("t_select"));
    setWhere((String)wmexs.get("t_where"));
    setFrom((String)wmexs.get("t_from"));
    setGroup((String)wmexs.get("t_group"));
    setOrder((String)wmexs.get("t_order"));
    setTextArgs((String)wmexs.get("t_args"));
  }
  
  public MExp(String iob, String atrib, String texp)
  {
    wiob = iob;
    watrib = atrib;
    wtexp = texp;
    wtostring = ("OBJ:" + wiob + " COMP:" + watrib + " TE:" + wtexp);
  }
  
  public MExp(String texp, String wtxt) {
    wtexp = texp;
    wtext = wtxt;
    
    int i1 = -1;
    int i2 = -1;
    int i3 = -1;
    int i4 = -1;
    i1 = wtxt.indexOf(")");
    if (i1 != -1) {
      wargs = getArgs(wtxt.substring(1, i1));
      i2 = wtxt.indexOf(")", i1 + 1);
    }
    if (i2 != -1) {
      wcond = getArgs(wtxt.substring(i1 + 2, i2));
      i3 = wtxt.indexOf(")", i2 + 1);
    }
    if (i3 != -1) {
      wout = getArgs(wtxt.substring(i2 + 2, i3));
      i4 = wtxt.indexOf(")", i3 + 1);
    }
    if (i4 != -1) {
      wotherout = getArgs(wtxt.substring(i3 + 2, i4));
    }
  }
  
  public String getTable() {
    return wtab;
  }
  
  public String getAssociateObject() {
    return wassobject;
  }
  
  public void setAssociateObject(String ws) { wassobject = ws; }
  
  public String getCondition()
  {
    return wcondition;
  }
  
  public void setCondition(String ws) { wcondition = ws; }
  
  public String getSelect()
  {
    return wselect;
  }
  
  public void setSelect(String ws) { wselect = ws; }
  
  public String getFrom()
  {
    return wfrom;
  }
  
  public void setFrom(String ws) { wfrom = ws; }
  
  public String getWhere()
  {
    return wwhere;
  }
  
  public void setWhere(String ws) { wwhere = ws; }
  
  public String getGroup() {
    return wgroup;
  }
  
  public void setGroup(String ws) { wgroup = ws; }
  
  public String getOrder() {
    return worder;
  }
  
  public void setOrder(String ws) { worder = ws; }
  
  public void setTextArgs(String ws)
  {
    wtargs = ws;
  }
  
  public String getTextArgs() { return wtargs; }
  
  public String[] getArgs(String wt)
  {
    Vector wv = new Vector();
    int j = 0;
    for (int i = 0; i < wt.length(); i++) {
      if (wt.substring(i, i + 1).equals(","))
      {
        wv.addElement(MUtil.lang("\"" + wt.substring(j, i) + "\""));
        j = i + 1;
      }
    }
    if (wt.substring(j, wt.length()).trim().length() != 0) {
      wv.addElement(wt.substring(j, wt.length()));
    }
    String[] args = new String[wv.size()];
    wv.copyInto(args);
    return args;
  }
  







  public void expand(Hashtable wh) {}
  






  public String getPromptSql(Hashtable wh)
  {
    String wcsel = getSqlPrompt(wh);
    System.out.println("!*** MExp: " + wcsel);
    return wcsel;
  }
  
  public String getTab() {
    return wtab;
  }
  
  public String getTexp() { return wtexp; }
  
  public void setTexp(String ws)
  {
    wtexp = ws;
  }
  
  public String getAtrib() {
    return watrib;
  }
  
  public void setAtrib(String ws) {
    watrib = ws;
  }
  
  public String getObj() {
    return wiob;
  }
  
  public void setObj(String ws) {
    wiob = ws;
  }
  
  public String[] getCondAtrib() {
    return wcond;
  }
  
  public String[] getOutAtrib() { return wout; }
  
  public String[] getOtherOutAtrib() {
    return wotherout;
  }
  
  public String[] getExpArgs() { return wargs; }
  
  public String getDestAtrib()
  {
    return wdestatrib;
  }
  
  public String getSqlExpand(Hashtable wh)
  {
    String wcsel = " select ";
    for (int i = 0; i < wout.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + ", ";
      }
      wcsel = wcsel + wout[i];
    }
    wcsel = wcsel + " from " + wtab + "t0 ";
    for (int i = 0; i < wcond.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + " and ";
      } else {
        wcsel = wcsel + " where ";
      }
      if (wcond[(i + 1)].trim().startsWith("'")) {
        wcsel = wcsel + wcond[i] + " = " + wcond[(i + 1)];
      } else if (wcond[(i + 1)].trim().startsWith("=")) {
        wcsel = wcsel + wcond[i] + " = '" + (String)wh.get(wcond[i]) + "' ";
      } else {
        wcsel = wcsel + wcond[i] + " = '" + (String)wh.get(wcond[(i + 1)]) + "' ";
      }
    }
    return wcsel;
  }
  
  public String getSqlExpand() {
    System.out.println("!*** MExp: " + wtext);
    String wcsel = " select ";
    for (int i = 0; i < wout.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + ", ";
      }
      wcsel = wcsel + wout[i] + " as " + wout[(i + 1)];
    }
    wcsel = wcsel + " from " + wtab + "t0 ";
    for (int i = 0; i < wcond.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + " and ";
      } else {
        wcsel = wcsel + " where ";
      }
      if (wcond[(i + 1)].trim().startsWith("'")) {
        wcsel = wcsel + wcond[i] + " = " + wcond[(i + 1)];
      } else if (wcond[(i + 1)].trim().startsWith("=")) {
        wcsel = wcsel + wcond[i] + " = '[m." + wcond[i] + "]' ";
      } else {
        wcsel = wcsel + wcond[i] + " = '[m." + wcond[(i + 1)] + "]' ";
      }
    }
    return wcsel;
  }
  
  public String getSqlSalto() {
    System.out.println("!*** MExp: " + wtext);
    String wcsel = "  ";
    wcsel = wcsel + " from " + wtab + "t0 a ";
    for (int i = 0; i < wcond.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + " and ";
      } else {
        wcsel = wcsel + " where ";
      }
      if (wcond[(i + 1)].trim().startsWith("'")) {
        wcsel = wcsel + "a." + wcond[i] + " = " + wcond[(i + 1)];
      } else if (wcond[(i + 1)].trim().startsWith("=")) {
        wcsel = wcsel + "a." + wcond[i] + " = '[m." + wcond[i] + "]' ";
      } else {
        wcsel = wcsel + "a." + wcond[i] + " = '[m." + wcond[(i + 1)] + "]' ";
      }
    }
    return wcsel;
  }
  
  public String getSqlExpandBrowse() {
    String wcsel = " select ";
    for (int i = 0; i < wout.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + ", ";
      }
      wcsel = wcsel + " b." + wout[i] + " as " + wout[(i + 1)];
    }
    wcsel = wcsel + " from " + wtab + "t0 b";
    for (int i = 0; i < wcond.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + " and ";
      } else {
        wcsel = wcsel + " where ";
      }
      if (wcond[(i + 1)].trim().startsWith("'")) {
        wcsel = wcsel + " b." + wcond[i] + " = " + wcond[(i + 1)];
      } else if (wcond[(i + 1)].trim().startsWith("=")) {
        wcsel = wcsel + " b." + wcond[i] + " = a." + wcond[i] + " ";
      } else {
        wcsel = wcsel + " b." + wcond[i] + " = a." + wcond[(i + 1)] + " ";
      }
    }
    return wcsel;
  }
  
  private String getSqlPrompt(Hashtable wh) {
    String wcsel = " select ";
    for (int i = 0; i < wout.length; i += 1) {
      if (i > 0) {
        wcsel = wcsel + ", ";
      }
      wcsel = wcsel + wout[i];
    }
    wcsel = wcsel + " from " + wtab + "t0 ";
    for (int i = 0; i < wcond.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + " and ";
      } else {
        wcsel = wcsel + " where ";
      }
      if (wcond[(i + 1)].trim().startsWith("'")) {
        wcsel = wcsel + wcond[i] + " like " + wcond[(i + 1)];
      } else if (wcond[(i + 1)].trim().startsWith("=")) {
        wcsel = wcsel + wcond[i] + " like '" + (String)wh.get(wcond[i]) + "%' ";
      } else {
        wcsel = wcsel + wcond[i] + " like '" + (String)wh.get(wcond[(i + 1)]) + "%' ";
      }
    }
    return wcsel;
  }
  
  public String getSqlPrompt() {
    System.out.println("!*** MExp: " + wtext);
    String wcsel = " select ";
    for (int i = 0; i < wout.length; i += 1) {
      if (i > 0) {
        wcsel = wcsel + ", ";
      }
      if (i == 0) {
        wcsel = wcsel + "a." + wout[i] + " as " + watrib;
      } else {
        wcsel = wcsel + "a." + wout[i];
      }
    }
    wcsel = wcsel + " from " + wtab + "t0 a ";
    




    if (wcond.length == 0) {
      wcsel = wcsel + " where ";
    }
    for (int i = 0; i < wcond.length; i += 2) {
      if (i > 0) {
        wcsel = wcsel + " and ";
      } else {
        wcsel = wcsel + " where ";
      }
      if (wcond[(i + 1)].trim().startsWith("'")) {
        wcsel = wcsel + "a." + wcond[i] + " like " + wcond[(i + 1)];
      } else if (wcond[(i + 1)].trim().startsWith("=")) {
        wcsel = wcsel + "a." + wcond[i] + " like '[m." + wcond[i] + "]%' ";
      } else {
        wcsel = wcsel + "a." + wcond[i] + " like '[m." + wcond[(i + 1)] + "]%' ";
      }
    }
    return wcsel;
  }
  
  public String toString() {
    return wtostring;
  }
}
