package PT.Minimal.geral;

import PT.Minimal.geral.console.MConsole;
import PT.Minimal.pnuts.MPnuts;
import PT.Minimal.util.MNumberUtil;
import PT.Minimal.util.MString;
import java.awt.Point;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import pnuts.lang.Property;

public class MRow extends Observable implements Cloneable, Property
{
  private Hashtable rowAtrib;
  private MBrowse wbrowse;
  public MTable wMTable;
  public static final String KEY = "K";
  public static final String ATRIB = "A";
  public static final String EXTENSION = "EX";
  public static final String EXPANSION = "XN";
  public static final String FORMONLY = "*";
  public static final String TEXTTYPE = "TEXTTYPE";
  public static final String NUMTYPE = "NUMTYPE";
  public static final String SERIAL = "SERIAL";
  public static final String TEXT = "TEXT";
  public static final String BINARY = "BINARY";
  public static final String DATETYPE = "DATETYPE";
  private String wcurrency;
  private int wno_currency = 0;
  private boolean hasCurrency = false;
  
  private boolean checkedTextBlob = false;
  private boolean hasTextBlob = false;
  
  private String wcst = " ";
  

  public MRow() {}
  

  public MRow(MTable wmt, String[] wrownames)
  {
    System.out.println("!*** MRow from array ");
    

    wMTable = wmt;
    rowAtrib = new Hashtable();
    for (int i = 0; i < wrownames.length; i++) {
      rowAtrib.put(wrownames[i], "");
    }
    checkCurrency();
  }
  


  public MRow(MTable wmt, Hashtable wh)
  {
    wMTable = wmt;
    rowAtrib = new Hashtable();
    for (Enumeration e = wh.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      rowAtrib.put(wkey, wh.get(wkey));
    }
    checkCurrency();
  }
  
  public MRow(MRow wr)
  {
    System.out.println("!*** MRow from MRow ");
    
    wMTable = wMTable;
    rowAtrib = new Hashtable();
    for (Enumeration e = rowAtrib.keys(); e.hasMoreElements();) {
      rowAtrib.put((String)e.nextElement(), "");
    }
    checkCurrency();
  }
  
  private void checkCurrency()
  {
    Enumeration e;
    if (hasCurrency) {
      System.out.println("!*** MRow changeCurrency: " + wcurrency);
      changeCurrency(wcurrency);
    } else {
      for (e = rowAtrib.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        






        if (wkey.startsWith("k")) {
          if (wcurrency == null) {
            String wkey1 = "m" + wkey.substring(1);
            if (rowAtrib.containsKey(wkey1)) {
              wcurrency = "m";
              wno_currency = 3;
            } else {
              wcurrency = "v";
              wno_currency = 2;
            }
            if ((getEnt().startsWith("CM")) || (getEnt().startsWith("CB")) || (getEnt().startsWith("CAO")) || (getEnt().startsWith("COB")) || (getEnt().startsWith("DTB")) || (getEnt().startsWith("IIPT")) || (getEnt().startsWith("IM"))) {
              wcurrency = "v";
            }
          }
          hasCurrency = true;
          changeCurrency(wcurrency);
        }
      }
    }
  }
  
  public void changeCurrency(String wnewcurrency)
  {
    if ((wnewcurrency != null) && (hasCurrency))
    {


      for (Enumeration e = rowAtrib.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if (wkey.startsWith("k")) {
          String wnewkey = wnewcurrency + wkey.substring(1);
          if (rowAtrib.containsKey(wnewkey)) {
            rowAtrib.put(wkey, rowAtrib.get(wnewkey));
          }
        }
      }
      
      wcurrency = wnewcurrency;
      refresh();
    }
  }
  
  public String[] getAtribs() {
    return wMTable.getAtribs();
  }
  
  public String getTable() {
    return wMTable.getTable();
  }
  
  public String getEnt() {
    return wMTable.getEnt();
  }
  
  public Hashtable getRecord() {
    return rowAtrib;
  }
  
  public void setRecord(Hashtable wh) {
    rowAtrib = wh;
  }
  
  public String[] getColNames() {
    return wMTable.getColNames();
  }
  
  public String getColName(int i) {
    return wMTable.getColName(i);
  }
  
  public String[] getColDescs() {
    return wMTable.getColDescs();
  }
  
  public String getColDesc(int i) {
    return wMTable.getColDesc(i);
  }
  
  public Hashtable getKeys() {
    return wMTable.getKeys();
  }
  
  public String getKey(String ws) {
    return wMTable.getKey(ws);
  }
  
  public String getType(String ws) {
    return wMTable.getType(ws);
  }
  
  public Point getLength(String ws)
  {
    return wMTable.getLength(ws);
  }
  
  public int getNumCar(String ws) {
    return wMTable.getNumCar(ws);
  }
  
  public int getNumDec(String ws) {
    return wMTable.getNumDec(ws);
  }
  
  public Point getExtLength(String ws) { return wMTable.getExtLength(ws); }
  
  public int getExtNumCar(String ws)
  {
    return wMTable.getExtNumCar(ws);
  }
  
  public int getExtNumDec(String ws) {
    return wMTable.getExtNumDec(ws);
  }
  
  public String getColDesc(String ws) {
    return wMTable.getColDesc(ws);
  }
  
  public String getExpTab(String ws)
  {
    return wMTable.getExpTab(ws);
  }
  
  public String getExpExp(String ws) { return wMTable.getExpExp(ws); }
  
  public String getExpAtt(String ws) {
    return wMTable.getExpAtt(ws);
  }
  
  public String getRule(String ws) {
    return wMTable.getRule(ws);
  }
  
  public String getCmd(String ws) {
    return wMTable.getCmd(ws);
  }
  
  public String getCmdArg(String ws) {
    return wMTable.getCmdArg(ws);
  }
  

  public String getMask(String ws)
  {
    return wMTable.getMask(ws);
  }
  
  public String gb(String ws)
  {
    String s = (String)rowAtrib.get(ws);
    int w = MForm.getMForm(getEnt() + "BR").getBrowse().getBColCar(ws);
    System.out.println("!*** MRow: " + s + " " + w);
    
    if (getType(ws) == "NUMTYPE") {
      s = MUtil.getFormattedNumber(s, getMask(ws), w);
    } else {
      s = s + "                                                                         ";
      s = s.substring(0, w);
    }
    return s;
  }
  
  public void setBrowse(MBrowse mbrowse)
  {
    wbrowse = mbrowse;
  }
  
  public MBrowse getBrowse() { return wbrowse; }
  
  public void setMTable(MTable wmt)
  {
    wMTable = wmt;
  }
  
  public MTable getMTable() { return wMTable; }
  
  public Object getField(String wkey)
  {
    if (wcst.equals("ds")) {
      return "********";
    }
    return rowAtrib.get(wkey);
  }
  
  public String getCst()
  {
    return wcst;
  }
  
  public void setCst(String s) {
    wcst = s;
  }
  
  public boolean isValidRow() {
    return (!wcst.equals("ds")) && (!wcst.equals("st"));
  }
  
  public boolean isDeletedRow() { return wcst.equals("ds"); }
  
  public boolean isSubTotalRow() {
    return wcst.equals("st");
  }
  
  public String getFormatedText(String wkey)
  {
    String ws = prepVal(wkey);
    
    if ((wcst.equals("st")) && 
      (ws == "0")) {
      for (ws = " "; ws.length() < getExtNumCar(wkey); ws = ws + ' ') {}
      return ws;
    }
    
    if ((getMask(wkey) != null) && (getMask(wkey).startsWith("N")) && (ws.trim().length() != 0)) {
      String wformat = getMask(wkey).substring(1);
      DecimalFormat wdf = new DecimalFormat(wformat);
      wdf.setDecimalFormatSymbols(MUtil.DFS);
      try {
        BigDecimal wbd = new BigDecimal(ws);
        ws = wdf.format(wbd.doubleValue());
        while (ws.length() < getExtNumCar(wkey)) ws = ' ' + ws;
      } catch (Exception ex) {
        ws = "*********";
      }
    } else {
      while (ws.length() < getExtNumCar(wkey)) ws = ws + ' ';
    }
    return ws;
  }
  
  public Object setField(String wkey, Object wobj) {
    if (rowAtrib.containsKey(wkey)) {
      try {
        return rowAtrib.put(wkey, wobj);
      } catch (NullPointerException ex) {
        throw new MError("Atributo '" + wkey + "' com valor nulo.");
      }
    }
    return null;
  }
  
  public MRow Copy()
  {
    MRow wc = null;
    try {
      wc = (MRow)clone();
      rowAtrib = new Hashtable();
      for (Enumeration e = rowAtrib.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        rowAtrib.put(wkey, rowAtrib.get(wkey));
      }
      hasCurrency = hasCurrency;
      wcurrency = wcurrency;
    } catch (Exception e) {
      System.out.println("An exception occurred:");
      
      if ((e instanceof MError)) {
        throw ((MError)e);
      }
      throw new MError(e);
    }
    return wc;
  }
  
  public MPnuts findMPnut() throws MError
  {
    return wMTable.findMPnut();
  }
  
  private void doPnut(String wfase, String waction, MRow wnew, MRow wold, MCursor mcursor) {
    MPnuts mpnut = findMPnut();
    MRow wrow = (MRow)mpnut.get("row");
    MRow wrowold = (MRow)mpnut.get("rowold");
    
    mpnut.setRow(wnew);
    mpnut.set("rowold", wold);
    mpnut.set("mcursor", mcursor);
    mpnut.evalRowPnut(this, waction, wfase);
    mpnut.unset("mcursor");
    mpnut.set("rowold", null);
    
    mpnut.setRow(wrow);
    mpnut.set("rowold", wrowold);
  }
  
  public void insertRow() {
    MCursor wupdate = MConnection.getMCursor();
    insertRow(wupdate);
    wupdate.close();
  }
  
  public void insertRow(MCursor wupdate) {
    insertRow(wupdate, true);
  }
  
  public void insertRow(MCursor wupdate, boolean createZK) {
    String wcols = "";
    String wvalues = "";
    String[] wcolumns = getColNames();
    Vector wpars = new Vector();
    

    for (int i = 0; i < wcolumns.length; i++) {
      String wkey = wcolumns[i];
      

      if ((getRule(wkey) != null) && 
        (getRule(wkey).startsWith("%"))) {
        int wpos = prepVal(wkey).indexOf("%");
        if (wpos != -1)
        {


          setField(wkey, MUtil.seqNumber(wkey, prepVal(wkey).substring(0, wpos), this, wupdate));
        }
      }
      

      if ((getKey(wkey).startsWith("A")) || (getKey(wkey).startsWith("K")))
      {

        wpars.addElement(wkey);
      }
    }
    

    doPnut("BR", "INSERT_ROW", this, null, wupdate);
    
    String wmode = "N";
    
    String wamb = wupdate.getAmbient().substring(0, 1);
    if ((wamb.equals("I")) || (wamb.equals("O")) || (wamb.equals("P"))) {
      wmode = "O";
    }
    
    try
    {
      String wzk = prepVal("zk");
      if (createZK) {
        setField("zk", wupdate.getKey(getEnt(), "BF", wzk));
      }
      

      String wvaluesW = "";
      String wval = "";
      if (wmode.equals("N")) {
        for (int i = 0; i < wpars.size(); i++) {
          String wkey = (String)wpars.elementAt(i);
          int j = i + 1;
          wval = prepValW(wkey);
          if ((!wval.equals("'")) && ((getType(wkey) == "TEXTTYPE") || (getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL"))) {
            wvaluesW = wvaluesW + prepValW(wkey) + ",";
          } else {
            wmode = "O";
          }
        }
      }
      

      if (wmode.equals("O"))
      {
        for (int i = 0; i < wcolumns.length; i++) {
          String wkey = wcolumns[i];
          if ((getKey(wkey).startsWith("A")) || (getKey(wkey).startsWith("K"))) {
            wcols = wcols + "," + wkey;
            wvalues = wvalues + ",?";
          }
        }
        
        wcols = " " + wcols.substring(1);
        wvalues = " " + wvalues.substring(1);
        String wcsel = "insert into " + getTable() + " (" + wcols + ") values (" + wvalues + ") ";
        
        wupdate.prepareStatement(wcsel);
        
        for (int i = 0; i < wpars.size(); i++) {
          String wkey = (String)wpars.elementAt(i);
          int j = i + 1;
          if (getType(wkey) == "TEXTTYPE") {
            wupdate.setPreparedPar(j, prepVal(wkey));
          }
          if (getType(wkey) == "NUMTYPE") {
            wupdate.setPreparedPar(j, new BigDecimal(prepVal(wkey)));
          }
          if (getType(wkey) == "SERIAL") {
            wupdate.setPreparedPar(j, Integer.valueOf(prepVal(wkey)).intValue());
          }
          if (getType(wkey) == "TEXT") {
            wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
          }
          if (getType(wkey) == "BINARY") {
            wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
          }
        }
        wupdate.execPreparedUpdate();
        wupdate.closePreparedStatement();
      }
      int qcode;
      if (wmode.equals("N")) {
        wvaluesW = wvaluesW.substring(0, wvaluesW.length() - 1);
        String wsel = "insert into " + getTable() + " values (" + wvaluesW + ") ";
        qcode = MUtil.executeSQLDirect(wsel, wupdate);
      }
      

      wzk = prepVal("zk");
      if (createZK) {
        setField("zk", wupdate.getKey(getEnt(), "AF", wzk));
      }
      
      for (int i = 0; i < wcolumns.length; i++) {
        String wkey = wcolumns[i];
        if (getKey(wkey).startsWith("EX")) {
          setExtension(wkey, wupdate);
        }
      }
      doPnut("AR", "INSERT_ROW", this, null, wupdate);
    }
    finally {
      if (wmode.equals("O")) {
        wupdate.closePreparedStatement();
        wupdate.closeStatement();
      }
    }
    
    wcst = "ps";
  }
  
  public void updateRow(MRow wrow)
    throws MError
  {
    MCursor wupdate = MConnection.getMCursor();
    updateRow(wupdate, wrow);
    wupdate.close();
  }
  
  public void updateRow(MCursor wupdate, MRow wrow) throws MError {
    updateRow(wupdate, wrow, true);
  }
  
  public void updateRow(MCursor wupdate, MRow wrow, boolean checkChanges) {
    updateRow(wupdate, wrow, checkChanges, false);
  }
  
  public void updateRow(MCursor wupdate, MRow wrow, boolean checkChanges, boolean updZk) throws MError {
    Hashtable wh = wrow.getRecord();
    String wset = "";
    String wand = "";
    String[] wcolumns = getColNames();
    Vector wpars = new Vector();
    Vector wKeypars = new Vector();
    Vector wparsvalues = new Vector();
    
    System.out.println("!*** MRow doPnut: " + wrow.getEnt());
    doPnut("BR", "UPDATE_ROW", wrow, this, wupdate);
    

    String wmode = "N";
    
    String wamb = wupdate.getAmbient().substring(0, 1);
    if ((wamb.equals("I")) || (wamb.equals("O")) || (wamb.equals("P"))) {
      wmode = "O";
    }
    

    String wsetW = "";
    String wandW = "";
    String wval = "";
    
    if (wmode.equals("N")) {
      for (int i = 0; i < wcolumns.length; i++) {
        String wkey = wcolumns[i];
        if (((getKey(wkey).startsWith("A")) || (getKey(wkey).startsWith("K"))) && (!wkey.equals("zk"))) {
          wval = prepValW(wkey, wh);
          if ((!wval.equals("'")) && ((getType(wkey) == "TEXTTYPE") || (getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL"))) {
            wsetW = wsetW + "," + wkey + " = " + wval;
          } else {
            wmode = "O";
          }
        }
      }
    }
    
    if (wmode.equals("N")) {
      wsetW = " " + wsetW.substring(1);
      










      wandW = wandW + " and zk = " + prepValW("zk", wh);
      
      wandW = " " + wandW.substring(5);
      System.out.println(wandW);
    }
    

    String wcsel = "";
    if (wmode.equals("O"))
    {
      for (int i = 0; i < wcolumns.length; i++) {
        String wkey = wcolumns[i];
        if ((getKey(wkey).startsWith("A")) && (!wkey.equals("zk"))) {
          wset = wset + "," + wkey + " = ? ";
          wpars.addElement(wkey);
        }
        if ((getKey(wkey).startsWith("K")) && (!wkey.equals("zk")) && (updZk)) {
          wset = wset + "," + wkey + " = ? ";
          wpars.addElement(wkey);
        }
      }
      
      wset = " " + wset.substring(1);
      
      if (!updZk) {
        for (int i = 0; i < wcolumns.length; i++) {
          String wkey = wcolumns[i];
          if (getKey(wkey).startsWith("K")) {
            wand = wand + " and " + wkey + " = ? ";
            wpars.addElement(wkey);
          }
        }
      } else {
        wand = " and zk = " + (String)getField("zk");
      }
      wand = " " + wand.substring(5);
    }
    try
    {
      if ((wupdate.inBeginWork()) || (wupdate.inBeginBatch())) {
        String wzk = (String)getField("zk");
        wupdate.lockRows("select * from " + getTable() + " where zk=" + wzk);
      }
      
      if (wmode.equals("O"))
      {

        wcsel = "update " + getTable();
        if (wamb.equals("S")) {
          wcsel = wcsel + " WITH (ROWLOCK) set " + wset;
        } else {
          wcsel = wcsel + " set " + wset;
        }
        wcsel = wcsel + " where " + wand;
        

        if (checkChanges) {
          wcsel = wcsel + " and zdst = ? and zhst = ?";
        }
      }
      
      if (wmode.equals("N")) {
        String wsel = "update " + getTable();
        wsel = wsel + " WITH (ROWLOCK) set " + wsetW;
        wsel = wsel + " where " + wandW;
        

        if (checkChanges) {
          wsel = wsel + " and zdst = " + prepValW("zdst") + " and zhst = " + prepValW("zhst") + "";
        }
        System.out.println("!*** MRow Update Direct Changes: " + wsel);
        int nrows = MUtil.executeSQLDirect(wsel, wupdate);
        
        if (nrows == 0) {
          throw new MError("MIN-000102E");
        }
        if (nrows > 1) {
          throw new MError("MIN-000284E");
        }
      }
      
      if (wmode.equals("O")) {
        System.out.println("!*** MRow Update Changes: " + wcsel);
        wupdate.prepareStatement(wcsel);
        int j = 0;
        if (MConsole.getTraceSql()) System.out.println();
        for (int i = 0; i < wpars.size(); i++) {
          String wkey = (String)wpars.elementAt(i);
          j = i + 1;
          if (getType(wkey) == "TEXTTYPE") {
            if ((getKey(wkey).startsWith("K")) && (!updZk)) {
              if (MConsole.getTraceSql()) System.out.print(" and " + wkey + " = '" + prepFillVal(wkey) + "'");
              wupdate.setPreparedPar(j, prepFillVal(wkey));
            } else {
              if (MConsole.getTraceSql()) System.out.print(wkey + " = '" + prepVal(wkey, wh) + "',");
              wupdate.setPreparedPar(j, prepVal(wkey, wh));
            }
          }
          if (getType(wkey) == "NUMTYPE") {
            if ((getKey(wkey).startsWith("K")) && (!updZk)) {
              if (MConsole.getTraceSql()) System.out.print(" and " + wkey + " = " + prepVal(wkey) + " ");
              wupdate.setPreparedPar(j, new BigDecimal(prepVal(wkey)));
            } else {
              if (MConsole.getTraceSql()) System.out.print(wkey + " = " + prepVal(wkey, wh) + ",");
              wupdate.setPreparedPar(j, new BigDecimal(prepVal(wkey, wh)));
            }
          }
          if (getType(wkey) == "SERIAL") {
            if ((getKey(wkey).startsWith("K")) && (!updZk)) {
              if (MConsole.getTraceSql()) System.out.print(" and " + wkey + " = '" + prepFillVal(wkey) + "'");
              wupdate.setPreparedPar(j, Integer.valueOf(prepVal(wkey)).intValue());
            } else {
              if (MConsole.getTraceSql()) System.out.print(wkey + " = '" + prepVal(wkey, wh) + "',");
              wupdate.setPreparedPar(j, Integer.valueOf(prepVal(wkey, wh)).intValue());
              wparsvalues.addElement(prepVal(wkey, wh));
            }
          }
          if (getType(wkey) == "TEXT")
          {
            if ((getKey(wkey).startsWith("K")) && (!updZk)) {
              wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
            } else {
              wupdate.setPreparedPar(j, prepVal(wkey, wh).getBytes());
            }
          }
          if (getType(wkey) == "BINARY") {
            if ((getKey(wkey).startsWith("K")) && (!updZk)) {
              wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
            } else {
              wupdate.setPreparedPar(j, prepVal(wkey, wh).getBytes());
            }
          }
        }
        if (MConsole.getTraceSql()) System.out.println();
        if (checkChanges) {
          System.out.println("!*** MRow zdst: " + prepFillVal("zdst"));
          System.out.println("!*** MRow zhst: " + prepFillVal("zhst"));
          wupdate.setPreparedPar(j + 1, prepFillVal("zdst"));
          wupdate.setPreparedPar(j + 2, prepFillVal("zhst"));
        }
        System.out.println("!*** MRow exec before: ");
        int nrows = wupdate.execPreparedUpdate();
        
        System.out.println("!*** MRow exec after: " + nrows);
        wupdate.closePreparedStatement();
        if (nrows == 0) {
          throw new MError("MIN-000102E");
        }
        if (nrows > 1) {
          throw new MError("MIN-000284E");
        }
      }
      
      for (int i = 0; i < wcolumns.length; i++) {
        String wkey = wcolumns[i];
        
        if (getKey(wkey).startsWith("EX")) {
          setExtension(wkey, wupdate);
        }
      }
      
      doPnut("AR", "UPDATE_ROW", wrow, this, wupdate);
    }
    finally {
      if (wmode.equals("O")) {
        wupdate.closePreparedStatement();
        wupdate.closeStatement();
      }
    }
  }
  






  public void updateRow(MCursor wupdate, MRow wrow, String wtable, String wset)
    throws MError
  {
    Hashtable wh = wrow.getRecord();
    
    String wand = "";
    String[] wcolumns = getColNames();
    Vector wpars = new Vector();
    Vector wKeypars = new Vector();
    Vector wparsvalues = new Vector();
    


    for (int i = 0; i < wcolumns.length; i++) {
      String wkey = wcolumns[i];
      
      if (getKey(wkey).startsWith("K")) {
        wand = wand + " and " + wkey + " = ? ";
        wpars.addElement(wkey);
      }
    }
    wand = " " + wand.substring(5);
    
    try
    {
      String wcsel = "update " + wtable + " set " + wset + " where " + wand;
      

      System.out.println("!*** MRow Update Nochanges: " + wcsel);
      wupdate.prepareStatement(wcsel);
      int j = 0;
      for (int i = 0; i < wpars.size(); i++) {
        String wkey = (String)wpars.elementAt(i);
        
        j = i + 1;
        if (getType(wkey) == "TEXTTYPE") {
          if (getKey(wkey).startsWith("K"))
          {
            wupdate.setPreparedPar(j, prepFillVal(wkey));
          }
          else {
            wupdate.setPreparedPar(j, prepVal(wkey, wh));
          }
        }
        if (getType(wkey) == "NUMTYPE") {
          if (getKey(wkey).startsWith("K")) {
            wupdate.setPreparedPar(j, new BigDecimal(prepVal(wkey)));
          } else {
            wupdate.setPreparedPar(j, new BigDecimal(prepVal(wkey, wh)));
          }
        }
        if (getType(wkey) == "SERIAL") {
          if (getKey(wkey).startsWith("K")) {
            wupdate.setPreparedPar(j, Integer.valueOf(prepVal(wkey)).intValue());
          } else {
            wupdate.setPreparedPar(j, Integer.valueOf(prepVal(wkey, wh)).intValue());
            wparsvalues.addElement(prepVal(wkey, wh));
          }
        }
        if (getType(wkey) == "TEXT")
        {
          if (getKey(wkey).startsWith("K")) {
            wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
          } else {
            wupdate.setPreparedPar(j, prepVal(wkey, wh).getBytes());
          }
        }
        if (getType(wkey) == "BINARY") {
          if (getKey(wkey).startsWith("K")) {
            wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
          } else {
            wupdate.setPreparedPar(j, prepVal(wkey, wh).getBytes());
          }
        }
      }
      
      int nrows = wupdate.execPreparedUpdate();
      wupdate.closePreparedStatement();



    }
    finally
    {


      wupdate.closePreparedStatement();
      wupdate.closeStatement();
    }
  }
  
  public void deleteRow() throws MError
  {
    MCursor wupdate = MConnection.getMCursor();
    deleteRow(wupdate);
    wupdate.close();
  }
  
  public void deleteRow(MCursor wupdate) throws MError { deleteRow(wupdate, true); }
  
  public void deleteRow(MCursor wupdate, boolean checkChanges)
    throws MError
  {
    String wand = "";
    String[] wcolumns = getColNames();
    Vector wpars = new Vector();
    
    doPnut("BR", "DELETE_ROW", this, null, wupdate);
    

    String wmode = "N";
    
    String wamb = wupdate.getAmbient().substring(0, 1);
    if ((wamb.equals("I")) || (wamb.equals("O")) || (wamb.equals("P"))) {
      wmode = "O";
    }
    
    String wsetW = " ";
    String wandW = " ";
    String wval = " ";
    if (wmode.equals("N")) {
      Hashtable wh = getRecord();
      wsetW = " " + wsetW.substring(1);
      for (int i = 0; i < wcolumns.length; i++) {
        String wkey = wcolumns[i];
        if (getKey(wkey).startsWith("K")) {
          wval = prepValW(wkey, wh);
          if ((!wval.equals("'")) && ((getType(wkey) == "TEXTTYPE") || (getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL"))) {
            wandW = wandW + " and " + wkey + " = " + wval;
          } else {
            wmode = "O";
          }
        }
      }
      
      wandW = " " + wandW.substring(5);
    }
    
    if (wmode.equals("O")) {
      for (int i = 0; i < wcolumns.length; i++) {
        String wkey = wcolumns[i];
        if (getKey(wkey).startsWith("K")) {
          wand = wand + " and " + wkey + " = ? ";
          wpars.addElement(wkey);
        }
      }
      wand = " " + wand.substring(5);
    }
    String wcsel = " ";
    try {
      if ((wupdate.inBeginWork()) || (wupdate.inBeginBatch())) {
        String wzk = (String)getField("zk");
        wupdate.lockRows("select * from " + getTable() + " where zk=" + wzk);
      }
      if (wmode.equals("O")) {
        wcsel = "delete from " + getTable() + " where " + wand;
        
        if (checkChanges) {
          wcsel = wcsel + " and zdst = ? and zhst = ?";
        }
      }
      if (wmode.equals("N")) {
        wcsel = "delete from " + getTable() + " where " + wandW;
        
        if (checkChanges) {
          wcsel = wcsel + " and zdst = " + prepValW("zdst") + " and zhst = " + prepValW("zhst") + "";
        }
        
        int nrows = MUtil.executeSQLDirect(wcsel, wupdate);
        
        if (nrows == 0) {
          throw new MError("MIN-000102E");
        }
        if (nrows > 1) {
          throw new MError("MIN-000284E");
        }
      }
      if (wmode.equals("O"))
      {
        wupdate.prepareStatement(wcsel);
        int j = 0;
        for (int i = 0; i < wpars.size(); i++) {
          String wkey = (String)wpars.elementAt(i);
          j = i + 1;
          if (getType(wkey) == "TEXTTYPE")
          {
            wupdate.setPreparedPar(j, prepFillVal(wkey));
          }
          if (getType(wkey) == "NUMTYPE")
          {
            wupdate.setPreparedPar(j, new BigDecimal(prepVal(wkey)));
          }
          if (getType(wkey) == "SERIAL")
          {
            wupdate.setPreparedPar(j, Integer.valueOf(prepVal(wkey)).intValue());
          }
          if (getType(wkey) == "TEXT") {
            wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
          }
          if (getType(wkey) == "BINARY") {
            wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
          }
        }
        if (checkChanges) {
          wupdate.setPreparedPar(j + 1, prepFillVal("zdst"));
          wupdate.setPreparedPar(j + 2, prepFillVal("zhst"));
        }
        int nrows = wupdate.execPreparedUpdate();
        wupdate.closePreparedStatement();
        if (nrows == 0) {
          throw new MError("MIN-000102E");
        }
        if (nrows > 1) {
          throw new MError("MIN-000284E");
        }
      }
      doPnut("AR", "DELETE_ROW", this, null, wupdate);
    }
    finally {
      wupdate.closePreparedStatement();
      wupdate.closeStatement();
    }
    
    wcst = "ds";
    refresh();
  }
  
  public void refreshRow() throws MError {
    MCursor mcursor = MConnection.getMCursor();
    try {
      refreshRow(mcursor);
    } finally {
      mcursor.close();
    }
  }
  



  public void refreshRow(MCursor mcursor)
    throws MError
  {
    String wzk = (String)getField("zk");
    refreshRow("select * from " + getTable() + " where zk=" + wzk, mcursor);
  }
  
  public void refreshRow(String wcsel, MCursor mcursor) throws MError
  {
    doPnut("BR", "REFRESH_ROW", this, null, mcursor);
    try {
      mcursor.execQuery(wcsel);
      if (mcursor.getWdcode() == 100) {
        throw new MError("MIN-999999E");
      }
      MUtil.MoveRecord(mcursor.getRecord(), getRecord());
      
      refresh();
    } finally {
      mcursor.closeStatement();
    }
    doPnut("AR", "REFRESH_ROW", this, null, mcursor);
    changeCurrency(wcurrency);
  }
  
  public void selectRow() throws MError {
    MCursor wupdate = MConnection.getMCursor();
    selectRow(wupdate);
    wupdate.close();
  }
  
  public void selectRow(MCursor wupdate) throws MError
  {
    String wand = " ";
    String[] wcolumns = getColNames();
    Vector wpars = new Vector();
    
    doPnut("BR", "INQUIRY_ROW", this, null, wupdate);
    
    for (int i = 0; i < wcolumns.length; i++) {
      String wkey = wcolumns[i];
      if (getKey(wkey).startsWith("K")) {
        if (wand.trim().length() != 0) {
          wand = wand + " and ";
        }
        wand = wand + wkey + " = ? ";
        wpars.addElement(wkey);
      }
    }
    
    if (wand.trim().length() != 0) {
      String wcsel = "select * from " + getTable() + " where " + wand;
      
      try
      {
        wupdate.prepareStatement(wcsel);
        for (int i = 0; i < wpars.size(); i++) {
          String wkey = (String)wpars.elementAt(i);
          int j = i + 1;
          if (getType(wkey) == "TEXTTYPE") {
            wupdate.setPreparedPar(j, prepVal(wkey));
          }
          if (getType(wkey) == "NUMTYPE") {
            wupdate.setPreparedPar(j, Double.valueOf(prepVal(wkey)).doubleValue());
          }
          if (getType(wkey) == "SERIAL") {
            wupdate.setPreparedPar(j, Double.valueOf(prepVal(wkey)).intValue());
          }
          if (getType(wkey) == "TEXT") {
            wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
          }
          if (getType(wkey) == "BINARY") {
            wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
          }
        }
        
        if (!wupdate.execPreparedQuery()) {
          wupdate.prepareStatement(wcsel);
          for (int i = 0; i < wpars.size(); i++) {
            String wkey = (String)wpars.elementAt(i);
            int j = i + 1;
            if (getType(wkey) == "TEXTTYPE") {
              wupdate.setPreparedPar(j, prepVal(wkey));
            }
            if (getType(wkey) == "NUMTYPE") {
              wupdate.setPreparedPar(j, Double.valueOf(prepVal(wkey)).doubleValue());
            }
            if (getType(wkey) == "SERIAL") {
              wupdate.setPreparedPar(j, Double.valueOf(prepVal(wkey)).intValue());
            }
            if (getType(wkey) == "TEXT") {
              wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
            }
            if (getType(wkey) == "BINARY") {
              wupdate.setPreparedPar(j, prepVal(wkey).getBytes());
            }
          }
          wupdate.execPreparedQuery();
        }
        if (wupdate.getWdcode() == 0) {
          MUtil.MoveRecord(wupdate.getRecord(), getRecord());
        }
        wupdate.closePreparedStatement();
        for (int i = 0; i < wcolumns.length; i++) {
          String wkey = wcolumns[i];
          if (getKey(wkey).startsWith("EX")) {
            getExtension(wkey, wupdate);
          }
        }
        
        doPnut("AR", "INQUIRY_ROW", this, null, wupdate);
      } finally {
        wupdate.closePreparedStatement();
        wupdate.closeStatement();
      }
    }
  }
  






  public boolean hasExtensions()
  {
    return wMTable.hasExtensions();
  }
  
  public void getExtensions() throws MError {
    if (hasExtensions()) {
      MCursor wupdate = MConnection.getMCursor();
      getExtensions(wupdate);
      wupdate.close();
    }
  }
  
  public void getExtensions(MCursor wupdate) throws MError {
    if (hasExtensions()) {
      String[] wcolumns = getColNames();
      for (int i = 0; i < wcolumns.length; i++) {
        String wkey = wcolumns[i];
        if (getKey(wkey).startsWith("EX")) {
          getExtension(wkey, wupdate);
        }
      }
    }
  }
  
  public String getAssociatedText() throws MError
  {
    return getAssociatedText("ROWTXT");
  }
  
  public String getAssociatedText(String wobjname) throws MError {
    String wtext = "";
    byte[] wobjbytes = getAssociatedObject(wobjname);
    if (wobjbytes != null) {
      wtext = new String(wobjbytes);
    }
    return wtext;
  }
  
  public String getAssociatedText(String wobjname, MCursor egbo_curs) throws MError {
    String wtext = "";
    byte[] wobjbytes = getAssociatedObject(wobjname, egbo_curs);
    if (wobjbytes != null) {
      wtext = new String(wobjbytes);
    }
    return wtext;
  }
  
  public String getAssociatedText(MCursor egbo_curs) throws MError {
    String wtext = "";
    byte[] wobjbytes = getAssociatedObject("ROWTXT", egbo_curs);
    if (wobjbytes != null) {
      wtext = new String(wobjbytes);
    }
    return wtext;
  }
  
  public void setAssociatedText(String wtext) throws MError {
    hasTextBlob = setAssociatedObject("ROWTXT", wtext.getBytes());
    checkedTextBlob = true;
  }
  
  public void setAssociatedText(String wtext, String wegboi) throws MError {
    hasTextBlob = setAssociatedObject(wegboi, wtext.getBytes());
    checkedTextBlob = true;
  }
  
  public void setAssociatedText(String wtext, MCursor egbo_curs) throws MError
  {
    hasTextBlob = setAssociatedObject("ROWTXT", wtext.getBytes(), egbo_curs);
    checkedTextBlob = true;
  }
  
  public byte[] getAssociatedObject(String wobjname) throws MError
  {
    byte[] wobj = null;
    MCursor egbo_curs = MConnection.getMCursor();
    wobj = getAssociatedObject(wobjname, egbo_curs);
    egbo_curs.close();
    return wobj;
  }
  


  public byte[] getAssociatedObject(String wobjname, MCursor egbo_curs) throws MError { return getAssociatedObject(wobjname, egbo_curs, getEnt()); }
  
  public byte[] getAssociatedObject(String wobjname, MCursor egbo_curs, String ent) throws MError {
    if ((!isValidRow()) || (isDeletedRow())) { return null;
    }
    String wzk = (String)getField("zk");
    byte[] wobj = null;
    
    String wtable = "egbot0";
    
    if (getEnt().substring(0, 4).toUpperCase().equals("MFIC")) {
      wtable = "mobjt0";
    }
    
    String wcsel = " select a.zobj from " + wtable + " a " + " where a.zent = '" + ent.substring(0, 4).toUpperCase() + "'" + " and a.zkent = " + wzk + " and a.egboi = '" + wobjname + "'";
    


    try
    {
      egbo_curs.execQuery(wcsel);
      if (egbo_curs.getWdcode() == 0) {
        wobj = egbo_curs.getBytes(1);
      }
    } finally {
      egbo_curs.closeStatement();
    }
    return wobj;
  }
  
  public boolean hasObjA(MCursor egbo_curs) throws MError { if ((!isValidRow()) || (isDeletedRow())) { return false;
    }
    boolean hasObjA = false;
    String wzk = (String)getField("zk");
    
    String wcsel = " select zk from bobat0 a  where a.zent = '" + getEnt().substring(0, 4).toUpperCase() + "'" + " and a.zkent = " + wzk;
    

    try
    {
      egbo_curs.execQuery(wcsel);
      if (egbo_curs.getWdcode() == 0) {
        hasObjA = true;
      }
    } finally {
      egbo_curs.closeStatement();
    }
    return hasObjA;
  }
  
  public boolean setAssociatedObject(String wobjname, byte[] wobj) throws MError {
    boolean wboo = false;
    MCursor text_curs = MConnection.getMCursor();
    wboo = setAssociatedObject(wobjname, wobj, text_curs);
    text_curs.close();
    return wboo;
  }
  

  public boolean setAssociatedObject(String wobjname, byte[] wobj, MCursor text_curs) throws MError { return setAssociatedObject(wobjname, wobj, text_curs, getEnt()); }
  
  public boolean setAssociatedObject(String wobjname, byte[] wobj, MCursor text_curs, String ent) throws MError {
    if ((!isValidRow()) || (isDeletedRow())) {
      throw new MError("MIN-999999E");
    }
    String wtable = "egbot0";
    
    if (getEnt().substring(0, 4).toUpperCase().equals("MFIC")) {
      wtable = "mobjt0";
    }
    

    boolean wboo = false;
    String wzk = (String)getField("zk");
    
    String wcsel = " delete from " + wtable + "  " + " where zent = '" + ent.substring(0, 4).toUpperCase() + "'" + " and zkent = " + wzk + " and egboi = '" + wobjname + "'";
    



    try
    {
      text_curs.execStatement(wcsel);
      text_curs.closeStatement();
      
      if ((wobj != null) && (wobj.length != 0)) {
        wcsel = " insert into " + wtable + " (zcri, znod, zhrh, zdrd, zcst, egboi, zobj, zent, zkent) " + " values (?,?,?,?,?,?,?,?,?) ";
        

        text_curs.prepareStatement(wcsel);
        
        text_curs.setPreparedPar(1, MLogin.getUser());
        text_curs.setPreparedPar(2, MLogin.getUserNodo());
        text_curs.setPreparedPar(3, MUtil.getTime());
        text_curs.setPreparedPar(4, MUtil.getToday());
        text_curs.setPreparedPar(5, "ps");
        text_curs.setPreparedPar(6, wobjname);
        text_curs.setPreparedPar(7, wobj);
        text_curs.setPreparedPar(8, ent.substring(0, 4).toUpperCase());
        text_curs.setPreparedPar(9, new BigDecimal(wzk));
        
        int nrows = text_curs.execPreparedUpdate();
        text_curs.closePreparedStatement();
        if (nrows == 0) {
          throw new MError("MIN-999999E");
        }
        wboo = true;
      }
      if (wobjname.equals("ROWTXT")) {
        hasTextBlob = wboo;
        checkedTextBlob = true;
      }
    } finally {
      text_curs.closePreparedStatement();
      text_curs.closeStatement();
    }
    return wboo;
  }
  
  public int getBinaryObjectCount(String ibin)
  {
    MCursor curs = MConnection.getMCursor();
    int count;
    try { count = getBinaryObjectCount(ibin, curs);
    } finally {
      curs.close();
    }
    return count;
  }
  
  public int getBinaryObjectCount(String ibin, MCursor curs)
  {
    String wcsel = " select count(*) as count from bbint0 a  where a.zent = '" + getEnt().substring(0, 4).toUpperCase() + "'" + " and a.zkent = " + (String)getField("zk") + " and a.ibin = '" + ibin + "'";
    


    return Integer.valueOf(MUtil.getSQLValue(wcsel, curs)).intValue();
  }
  
  public Vector getBinaryObject(String ibin) throws MError {
    Vector vobj = null;
    MCursor curs = MConnection.getMCursor();
    try {
      vobj = getBinaryObject(ibin, curs);
    } finally {
      curs.close();
    }
    return vobj;
  }
  
  public Vector getBinaryObject(String ibin, MCursor curs) throws MError
  {
    if ((!isValidRow()) || (isDeletedRow())) { return null;
    }
    String wcsel = " select a.nbin, a.tpath, a.ibinns, a.bbin from bbint0 a  where a.zent = '" + getEnt().substring(0, 4).toUpperCase() + "'" + " and a.zkent = " + (String)getField("zk") + " and a.ibin = '" + ibin + "'" + " order by ibinns";
    




    Vector v = new Vector();
    try {
      curs.execQuery(wcsel);
      while (curs.getWdcode() == 0) {
        MHashtable h = new MHashtable(true);
        h.put("nbin", curs.get("nbin"));
        h.put("tpath", curs.get("tpath"));
        h.put("bbin", curs.getBytes("bbin"));
        v.addElement(h);
        curs.next();
      }
    } finally {
      curs.closeStatement();
    }
    return v;
  }
  
  public void setBinaryObject(String ibin, String cbint, Vector list)
  {
    MCursor curs = MConnection.getMCursor();
    try {
      setBinaryObject(ibin, cbint, list, curs);
    } finally {
      curs.close();
    }
  }
  
  public void setBinaryObject(String ibin, String cbint, Vector list, MCursor curs)
  {
    if ((!isValidRow()) || (isDeletedRow())) {
      throw new MError("MIN-999999E");
    }
    
    String wzk = (String)getField("zk");
    
    String wcsel = " delete from bbint0 where zent = '" + getEnt().substring(0, 4).toUpperCase() + "'" + " and zkent = " + wzk + " and ibin = '" + ibin + "'";
    



    try
    {
      curs.execStatement(wcsel);
      curs.closeStatement();
      
      if ((list != null) && (list.size() != 0)) {
        wcsel = " insert into bbint0 (zutl, zgrp, zhst, zdst, zcst, zent, zkent, ibin, ibinns, cbint, nbin, tpath, bbin)  values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        


        curs.prepareStatement(wcsel);
        
        curs.setPreparedPar(1, MLogin.getUser());
        curs.setPreparedPar(2, MLogin.getUserNodo());
        curs.setPreparedPar(3, MUtil.getTime());
        curs.setPreparedPar(4, MUtil.getToday());
        curs.setPreparedPar(5, "ps");
        curs.setPreparedPar(6, getEnt().substring(0, 4).toUpperCase());
        curs.setPreparedPar(7, new BigDecimal(wzk));
        curs.setPreparedPar(8, ibin);
        curs.setPreparedPar(10, cbint);
        
        for (int i = 0; i < list.size(); i++) {
          MHashtable h = (MHashtable)list.elementAt(i);
          curs.setPreparedPar(9, i);
          curs.setPreparedPar(11, h.getString("nbin"));
          curs.setPreparedPar(12, h.getString("tpath"));
          curs.setPreparedPar(13, (byte[])h.get("bbin"));
          int nrows = curs.execPreparedUpdate();
          if (nrows == 0) {
            throw new MError("MIN-999999E");
          }
        }
        curs.closePreparedStatement();
      }
    } finally {
      curs.closePreparedStatement();
      curs.closeStatement();
    }
  }
  
  public String getKeyString()
  {
    String wkeystring = "";
    Vector wordered = new Vector();
    Vector wunordered = new Vector();
    for (Enumeration e = getKeys().keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (getKey(wkey).startsWith("K")) {
        wunordered.addElement(wkey);
      }
    }
    for (int j = wunordered.size(); j != wordered.size();) {
      int wnlow = 0;
      for (int i = 0; i < wunordered.size(); i++) {
        if (getKey((String)wunordered.elementAt(i)).charAt(1) < getKey((String)wunordered.elementAt(wnlow)).charAt(1))
        {



          wnlow = i;
        }
      }
      wordered.addElement(wunordered.elementAt(wnlow));
      wunordered.removeElementAt(wnlow);
    }
    for (int i = 0; i < wordered.size(); i++)
    {
      wkeystring = wkeystring + fillVal((String)wordered.elementAt(i));
    }
    return wkeystring;
  }
  
  public void refresh()
  {
    setChanged();
    notifyObservers();
  }
  


  private String fillVal(String wkey)
  {
    String wval = (String)rowAtrib.get(wkey);
    for (int j = getNumCar(wkey); wval.length() < j;) {
      wval = wval + " ";
    }
    return wval;
  }
  
  private String prepVal(String wkey)
  {
    String wval = " ";
    if ((rowAtrib.get(wkey) == null) || (((String)rowAtrib.get(wkey)).trim().length() == 0))
    {
      if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
        wval = "0";
      }
    } else {
      wval = (String)rowAtrib.get(wkey);
    }
    return wval;
  }
  
  private String prepValW(String wkey) {
    String wval = "' '";
    if ((rowAtrib.get(wkey) == null) || (((String)rowAtrib.get(wkey)).trim().length() == 0))
    {
      if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
        wval = "0";
      }
    }
    else if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
      wval = (String)rowAtrib.get(wkey);
    } else {
      wval = (String)rowAtrib.get(wkey);
      if (wval.indexOf("'") < 0) {
        wval = "'" + wval + "'";
      } else {
        wval = "'";
      }
    }
    
    return wval;
  }
  
  private String prepFillVal(String wkey) {
    String wval = " ";
    if ((rowAtrib.get(wkey) == null) || (((String)rowAtrib.get(wkey)).trim().length() == 0))
    {
      if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
        wval = "0";
      }
    } else {
      wval = (String)rowAtrib.get(wkey);
    }
    for (int j = getNumCar(wkey); wval.length() < j;) {
      wval = wval + " ";
    }
    return wval;
  }
  
  private String prepFillValW(String wkey) {
    String wval = " ";
    if ((rowAtrib.get(wkey) == null) || (((String)rowAtrib.get(wkey)).trim().length() == 0))
    {
      if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
        wval = "0";
      }
    }
    else if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
      wval = (String)rowAtrib.get(wkey);
    }
    else if (wval.indexOf("'") < 0) {
      wval = "'" + wval + "'";
    } else {
      wval = "'";
    }
    

    for (int j = getNumCar(wkey); wval.length() < j;) {
      wval = wval + " ";
    }
    return wval;
  }
  
  private String prepVal(String wkey, Hashtable wh)
  {
    String wval = " ";
    if ((wh.get(wkey) == null) || (((String)wh.get(wkey)).trim().length() == 0))
    {
      if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
        wval = "0";
      }
    } else {
      wval = (String)wh.get(wkey);
    }
    return wval;
  }
  
  private String prepValW(String wkey, Hashtable wh) {
    String wval = "' '";
    if ((wh.get(wkey) == null) || (((String)wh.get(wkey)).trim().length() == 0))
    {
      if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
        wval = "0";
      }
    }
    else if ((getType(wkey) == "NUMTYPE") || (getType(wkey) == "SERIAL")) {
      wval = (String)wh.get(wkey);
    } else {
      wval = (String)wh.get(wkey);
      if (wval.indexOf("'") < 0) {
        wval = "'" + wval + "'";
      } else {
        wval = "'";
      }
    }
    
    return wval;
  }
  
  public void setExtension(String wkey, MCursor wupdate) throws MError {
    String wzk = (String)getField("zk");
    
    String wcsel = " select count(*) from " + getEnt().substring(0, 4) + "TX " + " where zcomp = '" + wkey + "'";
    
    if (getType(wkey) != "NUMTYPE") {
      wcsel = wcsel + " and zchar = '" + prepVal(wkey) + "' ";
    } else {
      wcsel = wcsel + " and zdec = " + Double.valueOf(prepVal(wkey)).intValue();
    }
    wcsel = wcsel + " and zkent = " + wzk;
    if (MNumberUtil.toInt(MUtil.getSQLValue(wcsel)) > 0) {
      return;
    }
    System.out.println("!*** MRow ExtensionUpdate: " + wkey + "|" + prepVal(wkey));
    wcsel = " delete from " + getEnt().substring(0, 4) + "TX " + " where zcomp = '" + wkey + "'" + " and zkent = " + wzk;
    



    try
    {
      wupdate.execStatement(wcsel);
      wupdate.closeStatement();
      
      if (!prepVal(wkey).trim().equals("")) {
        System.out.println("!*** MRow ExtensionUpdate2: " + wkey + "|" + prepVal(wkey));
        
        wcsel = " insert into " + getEnt().substring(0, 4) + "TX " + " (zkent, zcomp, zchar, zdec) " + " values (?,?,?,?)";
        











        wupdate.prepareStatement(wcsel);
        
        wupdate.setPreparedPar(1, new BigDecimal(wzk));
        wupdate.setPreparedPar(2, wkey);
        if (getType(wkey) == "NUMTYPE") {
          wupdate.setPreparedPar(3, " ");
          
          wupdate.setPreparedPar(4, Double.valueOf(prepVal(wkey)).intValue());
        } else {
          wupdate.setPreparedPar(3, prepVal(wkey));
          wupdate.setPreparedPar(4, 0);
        }
        
        wupdate.execPreparedUpdate();
      }
      
    }
    finally
    {
      wupdate.closePreparedStatement();
      wupdate.closeStatement();
    }
  }
  
  public void getExtension(String wkey, MCursor wupdate) throws MError {
    String wzk = (String)getField("zk");
    
    if (wzk.trim() != "") { String wcsel;
      String wcsel; if (getType(wkey) == "NUMTYPE") {
        wcsel = " select zdec as " + wkey + " from " + getEnt().substring(0, 4) + "TX ";
      } else {
        wcsel = " select zchar as " + wkey + " from " + getEnt().substring(0, 4) + "TX ";
      }
      try {
        wcsel = wcsel + " where zcomp = '" + wkey + "'" + " and zkent = " + wzk;
        
        wupdate.execQuery(wcsel);
        if (wupdate.getWdcode() == 0) {
          MUtil.MoveRecord(wupdate.getRecord(), getRecord());
        }
      } finally {
        wupdate.closePreparedStatement();
        wupdate.closeStatement();
      }
    }
  }
  
  public Hashtable setZCommand(String wcommand, String wfase, String wargs) throws MError {
    Hashtable wht = null;
    MCursor wupdate = MConnection.getMCursor();
    try {
      wht = setZCommand(wcommand, wfase, wargs, wupdate);
    } finally {
      wupdate.close();
    }
    return wht;
  }
  
  public Hashtable setZCommand(String wcommand, String wfase, String wargs, MCursor wupdate) throws MError { return wupdate.setZCommand(wcommand, wfase, getEnt(), wargs, prepVal("zk")); }
  

























  public String getString(String wkey)
  {
    return get(wkey).toString();
  }
  
  public Object get(String wkey) {
    Object wobj;
    Object wobj;
    if (wkey.equals("text")) {
      wobj = getAssociatedText();
    } else {
      if (!rowAtrib.containsKey(wkey.toLowerCase())) {
        throw new MError("MIN-910002E", new String[] { getEnt() + "." + wkey });
      }
      wobj = getFormatedText(wkey.toLowerCase());
      if (getType(wkey) == "NUMTYPE") {
        wobj = new Double(MUtil.getNumber((String)wobj));
      }
    }
    if (MPnuts.getTrace()) System.out.println("!*** MRow 1: " + getEnt() + "." + wkey + ": " + wobj);
    return wobj;
  }
  
  public String getFormatedValue(String wkey)
  {
    if (wkey.equals("text")) {
      return getAssociatedText();
    }
    if (!rowAtrib.containsKey(wkey.toLowerCase())) {
      throw new MError("MIN-910001E", new String[] { "MRow " + getEnt() + "." + wkey });
    }
    return getFormatedText(wkey.toLowerCase());
  }
  
  public void set(String wkey, Object wval) {
    if (MPnuts.getTrace()) System.out.println("!*** MRow 2: " + getEnt() + "." + wkey + "= " + wval);
    if (wkey.equals("text")) {
      try {
        setAssociatedText(wval.toString());
        return;
      } catch (MError err) {
        throw new RuntimeException(err.getMessage());
      }
    }
    if (!rowAtrib.containsKey(wkey.toLowerCase())) {
      String wemsg = "Variável '" + wkey + "' não encontrada.";
      throw new MError("MIN-910001E", new String[] { "MRow " + getEnt() + "." + wkey });
    }
    
    if ((wval instanceof Number)) { BigDecimal wbdc;
      BigDecimal wbdc;
      if ((wval instanceof BigDecimal)) {
        wbdc = (BigDecimal)wval;
      }
      else {
        wbdc = new BigDecimal(((Number)wval).doubleValue());
      }
      if (wbdc.scale() > 20) wbdc = wbdc.setScale(20, 4);
      wval = wbdc.toString();
    }
    
    if ((wval instanceof String)) {
      setField(wkey.toLowerCase(), (String)wval);
    } else {
      String wemsg = "MRow " + getEnt() + "." + wkey + "': valor de tipo " + wval.getClass() + " não permitido.";
      throw new MError("MIN-910001E", new String[] { wemsg });
    }
  }
  
  public boolean containsKey(String wkey) {
    boolean b = rowAtrib.containsKey(wkey.toLowerCase());
    if (MPnuts.getTrace()) System.out.println("!*** MRow 3: " + wkey + "? " + b);
    return b;
  }
  

  public boolean hasCurrencies() { return hasCurrency; }
  
  public String getCurrencyX() {
    if (wcurrency != null) {
      return wcurrency;
    }
    return "";
  }
  
  public String getCurrency() {
    return wcurrency;
  }
  
  public int getNumberOfCurrencies() {
    return wno_currency;
  }
  
  public boolean hasText(MCursor egbo_curs) throws MError {
    if (!checkedTextBlob) {
      hasTextBlob = hasAssociatedObject("ROWTXT", egbo_curs);
      checkedTextBlob = true;
    }
    return hasTextBlob;
  }
  
  public boolean hasAssociatedObject(String wobjname, MCursor egbo_curs) throws MError {
    return hasAssociatedObject(wobjname, egbo_curs, getEnt());
  }
  
  public boolean hasAssociatedObject(String wobjname, MCursor egbo_curs, String ent) throws MError {
    boolean hasObject = false;
    String wzk = (String)getField("zk");
    String wtable = "egbot0";
    
    if (getEnt().substring(0, 4).toUpperCase().equals("MFIC")) {
      wtable = "mobjt0";
    }
    
    String wcsel = " select '*' from " + wtable + " a " + " where a.zent = '" + ent.substring(0, 4).toUpperCase() + "'" + " and a.zkent = " + wzk + " and a.egboi = '" + wobjname + "'";
    


    try
    {
      egbo_curs.execQuery(wcsel);
      if (egbo_curs.getWdcode() == 0) {
        hasObject = true;
      }
    } finally {
      egbo_curs.closeStatement();
    }
    return hasObject;
  }
  
  public boolean isDirectory() {
    return false;
  }
  
  public void copyFieldsFrom(Hashtable h) {
    MUtil.MoveRecord(h, getRecord());
  }
  
  public void copyFieldsFrom(MRow r) { MUtil.MoveRecord(r.getRecord(), getRecord()); }
  

  public String toString()
  {
    String[] wcolumns = getColNames();
    
    String out = getClass() + "[" + getEnt();
    for (int i = 0; i < wcolumns.length; i++) {
      String wkey = wcolumns[i];
      if (getKey(wkey).startsWith("K")) {
        out = out + "," + wkey + "=" + getField(wkey);
      }
    }
    return out + "]";
  }
  




  public String toLongString()
  {
    String title = "Columns";
    int maxLen = title.length();
    
    String[] names = getColNames();
    int index = 0; for (int size = names.length; index < size; index++) {
      int len = names[index].length();
      if (len > maxLen) {
        maxLen = len;
      }
    }
    
    StringBuffer str = new StringBuffer(toString());
    str.append("\n\t").append(MString.fillRight(title, maxLen)).append("  T Len  Row");
    int index = 0; for (int size = names.length; index < size; index++) {
      String name = names[index];
      str.append("\n\t").append(MString.fillRight(name, maxLen)).append("  ");
      Object val = get(name);
      if (val == null) {
        str.append("?          = null");
      } else if ((val instanceof Number)) {
        str.append("N          = ").append(val);
      } else if ((val instanceof String)) {
        int len = val.toString().length();
        str.append("C").append(MString.fillLeft(String.valueOf(getLengthx), 4)).append(":");
        str.append(MString.fillLeft(String.valueOf(len), 4)).append(" = '").append(val).append("'");
      } else {
        str.append("?          = ").append(val);
      }
    }
    return str.toString();
  }
}
