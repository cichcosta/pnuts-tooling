package PT.Minimal.geral;

import PT.Minimal.util.MString;





public class MQuery
{
  private String wselect = " ";
  private String wfrom = " ";
  private String wwhere = " ";
  private String wgroup = " ";
  private String worder = " ";
  private String wtype = "SELECT";
  private MQuery wqunion = null;
  
  public MQuery() {}
  
  public String getQuery()
  {
    String wcsel = " ";
    if (wtype.equals("SELECT")) {
      if (wselect.trim().length() != 0) {
        wcsel = wcsel + " select " + wselect;
      }
      if (wfrom.trim().length() != 0) {
        wcsel = wcsel + " from " + wfrom;
      }
      if (wwhere.trim().length() != 0)
      {
        wcsel = wcsel + " where " + wwhere;
      }
      if (wgroup.trim().length() != 0) {
        wcsel = wcsel + " group by " + wgroup;
      }
      if (wqunion != null) {
        wcsel = wcsel + " union " + wqunion.getQuery();
      }
      
      if (worder.trim().length() != 0) {
        wcsel = wcsel + " order by " + worder;
      }
    }
    if (wtype.equals("DELETE")) {
      if (wfrom.trim().length() != 0) {
        wcsel = wcsel + " delete from " + wfrom;
      }
      if (wwhere.trim().length() != 0)
      {
        wcsel = wcsel + " where " + wwhere;
      }
    }
    return wcsel;
  }
  
  public void setType(String ws) {
    wtype = ws;
  }
  
  public String addSelect(String ws) {
    if ((wselect.trim().length() != 0) && (ws.trim().length() != 0)) {
      wselect += " , ";
    }
    wselect += ws;
    return wselect;
  }
  
  public void setSelect(String ws)
  {
    wselect = ws;
  }
  
  public String addFrom(String ws) {
    if ((wfrom.trim().length() != 0) && (ws.trim().length() != 0)) {
      wfrom += " , ";
    }
    wfrom += ws;
    return wfrom;
  }
  
  public void setFrom(String ws) {
    wfrom = ws;
  }
  
  public String addWhere(String ws) {
    if ((wwhere.trim().length() != 0) && (ws.trim().length() != 0)) {
      wwhere += " and ";
    }
    wwhere += ws;
    return wwhere;
  }
  
  public String addWhere(String watrib, String watribtype, String wval) { return addWhere(watrib, watribtype, wval, false); }
  


  public String addWhere(String watrib, String watribtype, String wval, boolean hasNot)
  {
    wval = MString.trimRight(wval);
    String wnot = "";
    if (wval.length() != 0) {
      if (hasNot) {
        wnot = "not ";
      }
      if (watribtype == "NUMTYPE") {
        addWhere(wnot + watrib + " = " + wval);
      }
      
      if (watribtype == "TEXTTYPE") {
        if ((wval.indexOf("%") == -1) && (wval.indexOf("_") == -1)) {
          if ((watrib.startsWith("a.t")) || (watrib.startsWith("a.n"))) {
            addWhere(wnot + "lower(" + watrib + ")" + " = lower('" + wval + "')");
          } else {
            addWhere(wnot + watrib + " = '" + wval + "'");
          }
          

        }
        else if ((watrib.startsWith("a.t")) || (watrib.startsWith("a.n"))) {
          if ((MConnection.getMConnection("DEFAULT").getAmbient().toUpperCase().startsWith("ORACLE")) || (MConnection.getMConnection("DEFAULT").getAmbient().toUpperCase().startsWith("POSTGRE"))) {
            addWhere(wnot + "trim(lower(" + watrib + "))" + " like lower('" + wval + "')");
          } else {
            addWhere(wnot + "lower(" + watrib + ")" + " like lower('" + wval + "')");
          }
        }
        else if ((MConnection.getMConnection("DEFAULT").getAmbient().toUpperCase().startsWith("ORACLE")) || (MConnection.getMConnection("DEFAULT").getAmbient().toUpperCase().startsWith("POSTGRE"))) {
          addWhere(wnot + "trim(" + watrib + ")" + " like '" + wval + "'");
        } else {
          addWhere(wnot + " " + watrib + " " + " like '" + wval + "'");
        }
      }
    }
    

    return wwhere;
  }
  
  public String addWhere(String watrib, String watribtype, String wval1, String wval2) {
    return addWhere(watrib, watribtype, wval1, wval2, false);
  }
  
  public String addWhere(String watrib, String watribtype, String wval1, String wval2, boolean hasNot) {
    String wnot = "";
    if (hasNot) {
      wnot = "not ";
    }
    if (wval2.trim().length() == 0) {
      addWhere(wnot + watrib, watribtype, wval1);
    }
    if ((wval2.trim().length() != 0) && (wval1.trim().length() != 0)) {
      if (watribtype == "NUMTYPE") {
        addWhere(wnot + watrib + " between " + wval1 + " and " + wval2);
      }
      if (watribtype == "TEXTTYPE") {
        addWhere(wnot + watrib + " between '" + wval1 + "' and '" + wval2 + "' ");
      }
    }
    if ((wval2.trim().length() != 0) && (wval1.trim().length() == 0)) {
      if (watribtype == "NUMTYPE") {
        addWhere(wnot + watrib + " <= " + wval2);
      }
      if (watribtype == "TEXTTYPE") {
        addWhere(wnot + watrib + " <= '" + wval2 + "' ");
      }
    }
    return wwhere;
  }
  

  public String addWhereZkExtension(String wtable, String wkey, String watribtype, String wval)
  {
    wval = MString.trimRight(wval);
    if (wval.length() != 0) {
      String wext = " a.zk in (select distinct x.zkent from " + wtable.substring(0, 4) + "tx x where x.zcomp = '" + wkey + "' and ";
      if (watribtype == "NUMTYPE") {
        String watrib = "x.zdec";
        wext = wext + watrib + " = " + wval;
      }
      if (watribtype == "TEXTTYPE") {
        String watrib = "x.zchar";
        if ((wval.indexOf("_") == -1) && (wval.indexOf("%") == -1)) {
          wext = wext + watrib + " = '" + wval + "'";
        } else {
          wext = wext + watrib + " like '" + wval + "'";
        }
      }
      addWhere(wext + ")");
    }
    return wwhere;
  }
  
  public String addWhere(String watrib, String watribtype, String[] wval) {
    return wwhere;
  }
  
  public void setWhere(String ws) {
    wwhere = ws;
  }
  
  public String addGroup(String ws) {
    if ((wgroup.trim().length() != 0) && (ws.trim().length() != 0)) {
      wgroup += " , ";
    }
    wgroup += ws;
    return wgroup;
  }
  
  public void setGroup(String ws) {
    wgroup = ws;
  }
  
  public String addOrder(String ws) {
    if ((worder.trim().length() != 0) && (ws.trim().length() != 0)) {
      worder += " , ";
    }
    worder += ws;
    return worder;
  }
  
  public String getSelect() { return wselect; }
  
  public String getFrom() {
    return wfrom;
  }
  
  public String getWhere() { return wwhere; }
  
  public void setOrder(String ws) {
    worder = ws;
  }
  
  public String getOrder() { return worder; }
  
  public String getGroup() {
    return wgroup;
  }
  
  public void setUnionQuery(MQuery wq) {
    wqunion = wq;
  }
  
  public MQuery getUnionQuery() { return wqunion; }
}
