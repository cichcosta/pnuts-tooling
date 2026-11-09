package PT.Minimal.geral;

import PT.Minimal.pnuts.MPnuts;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;

public class MTable
  extends Observable implements Cloneable
{
  private String zKey;
  private String wTable;
  private String went;
  private Hashtable rowKeys = new Hashtable();
  private Hashtable rowTypes = new Hashtable();
  private Hashtable rowAtrib = new Hashtable();
  private Hashtable rowColDesc = new Hashtable();
  private Hashtable rowExpTab = new Hashtable();
  private Hashtable rowExpExp = new Hashtable();
  private Hashtable rowExpAtt = new Hashtable();
  private Hashtable rowRules = new Hashtable();
  private Hashtable rowCmds = new Hashtable();
  private Hashtable rowCmdsArgs = new Hashtable();
  private Hashtable rowLength = new Hashtable();
  private Hashtable rowExtLength = new Hashtable();
  private Hashtable rowMasks = new Hashtable();
  
  private String[] colNames;
  
  private String[] colDescs;
  
  private MBrowse wbrowse;
  public static final String KEY = "K";
  public static final String ATRIB = "A";
  public static final String EXTENSION = "EX";
  public static final String EXPANSION = "XN";
  public static final String FORMONLY = "*";
  public static final String TEXTTYPE = "TEXTTYPE";
  public static final String NUMTYPE = "NUMTYPE";
  public static final String DATETYPE = "DATETYPE";
  private boolean existsExtensions = false;
  
  private MPnuts mpnut;
  
  private String wcst = " ";
  

  public MTable() {}
  
  public MTable(Hashtable wh)
  {
    rowAtrib = new Hashtable();
    for (Enumeration e = wh.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      rowAtrib.put(wkey, wh.get(wkey));
    }
  }
  
  public MTable(String[] wrownames) {
    rowAtrib = new Hashtable();
    for (int i = 0; i < wrownames.length; i++) {
      rowAtrib.put(wrownames[i], " ");
    }
  }
  
  public String[] getColNames() {
    return colNames;
  }
  
  public String getColName(int i) {
    return colNames[i];
  }
  
  public void setColNames(String[] cols)
  {
    colNames = cols;
  }
  
  public String[] getColDescs() {
    return colDescs;
  }
  
  public String getColDesc(int i) {
    return colDescs[i];
  }
  
  public void setColDescs(String[] cols)
  {
    colDescs = cols;
  }
  
  public String[] getAtribs() {
    String[] watribs = new String[rowAtrib.size()];
    int i = 0;
    for (Enumeration e = rowAtrib.keys(); e.hasMoreElements();) {
      watribs[i] = ((String)e.nextElement());
      i += 1;
    }
    return watribs;
  }
  


  private void setMPnutx(String went) throws MError { mpnut = MPnuts.findMPnut("ROW_" + went.substring(0, 4)); }
  
  public void setMPnut() throws MError
  {}
  
  public MPnuts findMPnut() throws MError {
    if (mpnut == null) {
      setMPnutx(went);
    }
    return mpnut;
  }
  





  public void setTable(String ws)
  {
    wTable = ws;
  }
  
  public String getTable() {
    return wTable;
  }
  
  public void setEnt(String ws) {
    went = ws;
  }
  
  public String getEnt() {
    return went;
  }
  
  public Hashtable getKeys()
  {
    return rowKeys;
  }
  
  public String getKey(String ws) {
    if (rowKeys.containsKey(ws)) {
      return (String)rowKeys.get(ws);
    }
    return "A";
  }
  

  public void setKeys(Hashtable wh)
  {
    rowKeys = wh;
    for (Enumeration e = rowKeys.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (((String)rowKeys.get(wkey)).equals("EX")) {
        existsExtensions = true;
        break;
      }
    }
  }
  
  public boolean hasExtensions() {
    return existsExtensions;
  }
  
  public void setTypes(Hashtable wh) {
    rowTypes = wh;
  }
  
  public void setType(String ws, String wt) {
    rowTypes.put(ws, wt);
  }
  
  public String getType(String ws) {
    if (rowTypes.containsKey(ws)) {
      return (String)rowTypes.get(ws);
    }
    return "TEXTTYPE";
  }
  
  public void setLength(Hashtable wh)
  {
    rowLength = wh;
  }
  
  public void setExtLength(Hashtable wh) { rowExtLength = wh; }
  
  public Point getLength(String ws)
  {
    return (Point)rowLength.get(ws);
  }
  
  public int getNumCar(String ws) {
    return rowLength.get(ws)).x;
  }
  
  public int getNumDec(String ws) {
    return rowLength.get(ws)).y;
  }
  
  public Point getExtLength(String ws) {
    return (Point)rowExtLength.get(ws);
  }
  
  public int getExtNumCar(String ws) {
    if (rowExtLength.get(ws) == null) {
      return 0;
    }
    return rowExtLength.get(ws)).x;
  }
  
  public int getExtNumDec(String ws) {
    return rowExtLength.get(ws)).y;
  }
  

  public void setColDesc(Hashtable wh) { rowColDesc = wh; }
  
  public String getColDesc(String ws) {
    if (rowColDesc.containsKey(ws)) {
      return (String)rowColDesc.get(ws);
    }
    return " ";
  }
  

  public void setExpTab(Hashtable wh)
  {
    rowExpTab = wh;
  }
  
  public String getExpTab(String ws) { return (String)rowExpTab.get(ws); }
  
  public void setExpExp(Hashtable wh)
  {
    rowExpExp = wh;
  }
  
  public String getExpExp(String ws) { return (String)rowExpExp.get(ws); }
  
  public void setExpAtt(Hashtable wh)
  {
    rowExpAtt = wh;
  }
  
  public String getExpAtt(String ws) { return (String)rowExpAtt.get(ws); }
  

  public void setRules(Hashtable wh)
  {
    rowRules = wh;
  }
  
  public String getRule(String ws) {
    return (String)rowRules.get(ws);
  }
  
  public void setCmds(Hashtable wh) {
    rowCmds = wh;
  }
  
  public String getCmd(String ws) {
    return (String)rowCmds.get(ws);
  }
  
  public void setCmdsArgs(Hashtable wh) {
    rowCmdsArgs = wh;
  }
  
  public String getCmdArg(String ws) {
    return (String)rowCmdsArgs.get(ws);
  }
  
  public void setMasks(Hashtable wh) {
    rowMasks = wh;
  }
  
  public Hashtable getMasks() {
    return rowMasks;
  }
  
  public String getMask(String ws)
  {
    return (String)rowMasks.get(ws);
  }
  

  public void setKey(String wfield) { rowKeys.put(wfield, "K"); }
  
  public void setCurrency() {
    Enumeration e;
    if (rowMasks != null) {
      for (e = rowMasks.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if (wkey.startsWith("m")) {
          String wkey1 = "k" + wkey.substring(1);
          if (!rowMasks.containsKey(wkey1)) {
            rowMasks.put(wkey1, rowMasks.get(wkey));
          }
        }
      }
    }
    Enumeration e;
    if (rowTypes != null) {
      for (e = rowTypes.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if (wkey.startsWith("m")) {
          String wkey1 = "k" + wkey.substring(1);
          if (!rowTypes.containsKey(wkey1)) {
            rowTypes.put(wkey1, rowTypes.get(wkey));
          }
        }
      }
    }
  }
}
