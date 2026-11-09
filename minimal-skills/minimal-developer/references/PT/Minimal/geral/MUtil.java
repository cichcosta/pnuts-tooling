package PT.Minimal.geral;

import PT.Minimal.pnuts.MPnuts;
import java.awt.Component;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.TextComponent;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JPasswordField;
import javax.swing.text.JTextComponent;

public class MUtil
{
  public static int FIELD_HEIGHT = 20;
  public static int LABEL_HEIGHT = 20;
  public static int BUTTON_HEIGHT = 20;
  public static int BUTTON_WIDTH = 90;
  public static int CHECKBOX_HEIGHT = 20;
  public static int CHOICEBOX_HEIGHT = 20;
  public static int DEFAULTROW_HEIGHT = 18;
  
  public static final String EOL = "\n";
  
  public static final String FORCE = "Force";
  public static final String NOFORCE = "NoForce";
  private static final int cacheSize = 150;
  private static Hashtable hstCached = new Hashtable(150);
  
  public static final DecimalFormatSymbols DFS = getMDFS();
  public static Properties myProp = new Properties();
  
  public String wstring;
  

  private MUtil() {}
  

  private static DecimalFormatSymbols getMDFS()
  {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols(java.util.Locale.getDefault());
    dfs.setGroupingSeparator(' ');
    dfs.setDecimalSeparator('.');
    return dfs;
  }
  
  public static double round(double num) {
    return PT.Minimal.util.MNumberUtil.round(num);
  }
  
  public static double round(double num, int dec) { return PT.Minimal.util.MNumberUtil.round(num, dec); }
  
  public static BigDecimal round(BigDecimal num) {
    return PT.Minimal.util.MNumberUtil.round(num);
  }
  
  public static BigDecimal round(BigDecimal num, int dec) { return PT.Minimal.util.MNumberUtil.round(num, dec); }
  
  public static String getToday(String wformat)
  {
    String wtoday = PT.Minimal.util.MDate.getToday(wformat);
    if (wtoday == null) return "";
    return wtoday;
  }
  
  public static String getToday() {
    return PT.Minimal.util.MDate.getToday();
  }
  
  public static String getTime() {
    return PT.Minimal.util.MDate.getTime();
  }
  
  public static String getTime(String wformat) {
    return PT.Minimal.util.MDate.getTime(wformat);
  }
  
  public static String getNumber(String wnum)
  {
    char wds = '.';
    java.text.NumberFormat numform = java.text.NumberFormat.getInstance(java.util.Locale.getDefault());
    if ((numform instanceof DecimalFormat)) {
      wds = DFS.getDecimalSeparator();
    }
    
    boolean isDecimazed = false;
    boolean isSinalized = false;
    StringBuffer wval = new StringBuffer();
    wnum = wnum.trim();
    for (int i = wnum.length() - 1; i >= 0; i--) {
      char c = wnum.charAt(i);
      if ((c >= '0') && (c <= '9')) {
        wval.append(c);
      } else if ((c == wds) && (!isDecimazed)) {
        isDecimazed = true;
        wval.append('.');
      } else if ((c == '-') && (!isSinalized)) {
        isSinalized = true;
        wval.append('-');
        break;
      }
    }
    if (wval.length() == 0) {
      return "0";
    }
    return wval.reverse().toString();
  }
  
  public static String getFormattedNumber(String wvalue, String wformat) { return getFormattedNumber(wvalue, wformat, 0); }
  
  public static String getFormattedNumber(String wvalue, String wformat, int length)
  {
    return getFormattedNumber(new Double(getNumber(wvalue)).doubleValue(), wformat, length);
  }
  
  public static String getFormattedNumber(BigDecimal wvalue, String wformat) {
    return getFormattedNumber(wvalue, wformat, 0);
  }
  
  public static String getFormattedNumber(BigDecimal wvalue, String wformat, int length) { return getFormattedNumber(wvalue.doubleValue(), wformat, length); }
  
  public static String getFormattedNumber(double wvalue, String wformat) {
    return getFormattedNumber(wvalue, wformat, 0);
  }
  
  public static String getFormattedNumber(double wvalue, String wformat, int length) {
    if (wformat.charAt(0) == 'N') {
      wformat = wformat.substring(1);
    }
    boolean bformat = true;
    if (wformat.charAt(0) == '*')
    {
      if (wvalue == 0.0D)
      {
        bformat = false;
      } else {
        wformat = wformat.substring(1);
      }
    }
    String str = "";
    if (bformat) {
      DecimalFormat wdf = new DecimalFormat(wformat, DFS);
      try {
        str = wdf.format(wvalue);
      } catch (Exception ex) {
        str = "*********";
      }
    }
    if (length != 0) {
      str = PT.Minimal.util.MString.fillLeft(str, length);
    }
    return str;
  }
  
  public static void MoveRecord(Hashtable whashtable, Container wcontainer)
  {
    Component[] wcomponents = wcontainer.getComponents();
    for (int j = 0; j < wcontainer.getComponentCount(); j++)
    {
      if (((wcomponents[j] instanceof JTextComponent)) && 
        (whashtable.containsKey(wcomponents[j].getName()))) {
        if ((wcomponents[j] instanceof MTextField)) {
          ((JTextComponent)wcomponents[j]).setText(String.valueOf(whashtable.get(wcomponents[j].getName())));
          ((MTextField)wcomponents[j]).setText(((MTextField)wcomponents[j]).getFormatedText());
        }
        else if ((wcomponents[j] instanceof MTextArea))
        {
          ((JTextComponent)wcomponents[j]).setText(String.valueOf(whashtable.get(wcomponents[j].getName())));
          ((MTextArea)wcomponents[j]).setText(((MTextArea)wcomponents[j]).getFormatedText());
        } else {
          ((JTextComponent)wcomponents[j]).setText(String.valueOf(whashtable.get(wcomponents[j].getName())));
        }
      }
      

      if ((wcomponents[j] instanceof TextComponent)) {
        ((TextComponent)wcomponents[j]).setText(String.valueOf(whashtable.get(wcomponents[j].getName())));
      }
    }
  }
  
  public static void MoveRecord(Hashtable whashtable1, Hashtable whashtable2) {
    for (Enumeration e = whashtable1.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      
      if (whashtable2.containsKey(wkey)) {
        whashtable2.put(wkey, whashtable1.get(wkey));
      }
    }
  }
  
  public static void MoveRecordElements(Hashtable whashtable1, Hashtable whashtable2) {
    for (Enumeration e = whashtable1.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      whashtable2.put(wkey, whashtable1.get(wkey));
    }
  }
  
  public static Hashtable getRecord(Container wcontainer) {
    Hashtable whashtable = new Hashtable();
    Component[] wcomponents = wcontainer.getComponents();
    for (int j = 0; j < wcontainer.getComponentCount(); j++)
    {
      if ((wcomponents[j] instanceof JTextComponent)) {
        if ((wcomponents[j] instanceof MTextField))
        {
          whashtable.put(wcomponents[j].getName(), ((MTextField)wcomponents[j]).getUnFormatedText());
        }
        else if ((wcomponents[j] instanceof MTextArea))
        {
          whashtable.put(wcomponents[j].getName(), ((MTextArea)wcomponents[j]).getText());
        } else {
          whashtable.put(wcomponents[j].getName(), ((JTextComponent)wcomponents[j]).getText());
        }
      }
      
      if ((wcomponents[j] instanceof MChoice)) {
        whashtable.put(wcomponents[j].getName(), ((MChoice)wcomponents[j]).getSelectedItem());
      }
      if ((wcomponents[j] instanceof TextComponent)) {
        whashtable.put(wcomponents[j].getName(), ((TextComponent)wcomponents[j]).getText());
      }
    }
    return whashtable;
  }
  
  public static void clearRecord(Hashtable whashtable) {
    for (Enumeration e = whashtable.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      whashtable.put(wkey, "");
    }
  }
  
  public static void clearContainer(Container wcontainer) {
    Component[] wcomponents = wcontainer.getComponents();
    for (int j = 0; j < wcontainer.getComponentCount(); j++)
    {
      if ((wcomponents[j] instanceof TextComponent)) {
        ((TextComponent)wcomponents[j]).setText("");
      }
      if ((wcomponents[j] instanceof JTextComponent)) {
        ((JTextComponent)wcomponents[j]).setText("");
      }
    }
  }
  
  public static String getFilterSql(Hashtable wh)
  {
    String wcsel = " ";
    for (Enumeration e = wh.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (((String)wh.get(wkey)).length() != 0) {
        if (wcsel.trim().length() != 0) {
          wcsel = wcsel + " and ";
        }
        wcsel = wcsel + "a." + wkey + " like '" + (String)wh.get(wkey) + "%'";
      }
    }
    return wcsel;
  }
  
  public static String stringRep(String wline, String wold, String wnew)
  {
    if ((wnew != null) && (wnew.length() != 0)) {
      for (int i = wline.indexOf(wold); i != -1; i = wline.indexOf(wold, i + wnew.length())) {
        wline = wline.substring(0, i) + wnew + wline.substring(i + wold.length(), wline.length());
      }
      
    }
    else {
      for (int i = wline.indexOf(wold); i != -1; i = wline.indexOf(wold, i)) {
        wline = wline.substring(0, i) + wline.substring(i + wold.length(), wline.length());
      }
    }
    

    return wline;
  }
  
  public static Vector startGetMRows(String went, String wcsel, int wcount, MCursor wcurs) {
    return startGetMRows(MMData.getMRow(went), wcsel, wcount, wcurs);
  }
  
  public static Vector continueGetMRows(String went, int wcount, MCursor wcurs) { return continueGetMRows(MMData.getMRow(went), wcount, wcurs); }
  
  public static Vector startGetMRows(MRow wrow, String wcsel, int wcount, MCursor wcurs)
  {
    wcurs.execQuery(wcsel);
    return continueGetMRows(wrow, wcount, wcurs);
  }
  
  public static Vector continueGetMRows(MRow wr, int wcount, MCursor wcurs) { Vector wrows = new Vector();
    Vector wbrec = wcurs.getXRecords(wcount);
    for (int i = 0; i < wbrec.size(); i++) {
      MRow wyrow = wr.Copy();
      clearRecord(wyrow.getRecord());
      MoveRecord((Hashtable)wbrec.elementAt(i), wyrow.getRecord());
      wyrow.changeCurrency(wr.getCurrency());
      wrows.addElement(wyrow);
    }
    wbrec.removeAllElements();
    
    wbrec.removeAllElements();
    return wrows;
  }
  
  public static Vector getRows(MRow wr, String wcsel) throws MError {
    return getRows(wr, wcsel, 200);
  }
  
  public static Vector getRows(MRow wr, String wcsel, MCursor wcurs) throws MError { return getRows(wr, wcsel, -1, wcurs); }
  

  public static Vector getRows(MRow wr, String wcsel, int wcount) throws MError { return getRows(wr, wcsel, wcount, "DEFAULT"); }
  
  public static Vector getRows(MRow wr, String wcsel, int wcount, boolean withWarning) throws MError {
    MCursor wcurs = MConnection.getMCursor("DEFAULT");
    Vector wv = getRows(wr, wcsel, wcount, wcurs, withWarning);
    wcurs.closeStatement();
    wcurs.close();
    return wv;
  }
  
  public static Vector getRows(MRow wr, String wcsel, int wcount, String wconnect) throws MError { MCursor wcurs = MConnection.getMCursor(wconnect);
    Vector wv = getRows(wr, wcsel, wcount, wcurs);
    wcurs.closeStatement();
    wcurs.close();
    return wv;
  }
  

  public static Vector getRows(MRow wr, String wcsel, int wcount, MCursor wcurs) throws MError { return getRows(wr, wcsel, wcount, wcurs, false); }
  
  public static Vector getRows(MRow wr, String wcsel, int wcount, MCursor wcurs, boolean withWarning) throws MError {
    Vector wrows = new Vector();
    try {
      wcurs.execQuery(wcsel);
      

      Vector wbrec = wcurs.getXRecords(wcount);
      wcurs.closeStatement();
      wcurs.close();
      for (int i = 0; i < wbrec.size(); i++) {
        MRow wyrow = wr.Copy();
        clearRecord(wyrow.getRecord());
        MoveRecord((Hashtable)wbrec.elementAt(i), wyrow.getRecord());
        wyrow.getExtensions();
        

        wyrow.changeCurrency(wr.getCurrency());
        wrows.addElement(wyrow);
      }
      wbrec.removeAllElements();
      
      wbrec = null;
      if ((withWarning) && (wcurs.getWdcode() != 100)) {
        MError wwarn = new MError("MIN-000011W", new String[] { String.valueOf(wcount) });
        wwarn.show();
      }
    }
    finally {
      wcurs.closeStatement();
      wcurs.close();
    }
    return wrows;
  }
  
  public static Vector getRows(String wcsel) throws MError {
    return getRows(wcsel, 200, false);
  }
  
  public static Vector getRows(String wcsel, boolean withWarning) throws MError { return getRows(wcsel, 200, withWarning); }
  
  public static Vector getRows(String wcsel, int wcount, boolean withWarning) throws MError {
    Vector wrows = null;
    MCursor wcurs = MConnection.getMCursor();
    try {
      wrows = getRows(wcsel, wcurs, wcount, withWarning);
    } finally {
      wcurs.closeStatement();
      wcurs.close();
    }
    return wrows;
  }
  
  public static Vector getRows(String wcsel, MCursor wcurs) throws MError { return getRows(wcsel, wcurs, 200); }
  
  public static Vector getRows(String wcsel, MCursor wcurs, int wcount) throws MError
  {
    return getRows(wcsel, wcurs, wcount, false);
  }
  
  public static Vector getRows(String wcsel, MCursor wcurs, int wcount, boolean withWarning) throws MError {
    Vector wrows = new Vector();
    try {
      wcurs.execQuery(wcsel);
      Vector wbrec = wcurs.getXRecords(wcount);
      if (wbrec.size() > 0) {
        MTable wmt = new MTable((Hashtable)wbrec.elementAt(0));
        wmt.setColNames(wcurs.getColNames());
        for (int i = 0; i < wbrec.size(); i++) {
          MRow wyrow = new MRow(wmt, (Hashtable)wbrec.elementAt(i));
          
          MoveRecord((Hashtable)wbrec.elementAt(i), wyrow.getRecord());
          wrows.addElement(wyrow);
        }
      }
      wbrec.removeAllElements();
      
      wbrec = null;
      if ((withWarning) && (wcurs.getWdcode() != 100)) {
        MError wwarn = new MError("MIN-000011W", new String[] { String.valueOf(wcount) });
        wwarn.show();
      }
    } finally {
      wcurs.closeStatement();
      wcurs.close();
    }
    return wrows;
  }
  
  public static Vector getCachedValues(String wcsel) throws MError {
    return getCachedValues(wcsel, 200);
  }
  
  public static Vector getCachedValues(String wcsel, MCursor wcurs) throws MError {
    return getCachedValues(wcsel, 200, wcurs);
  }
  
  public static Vector getCachedValues(String wcsel, int wcount) throws MError {
    Vector wrows = null;
    MCursor wcurs = MConnection.getMCursor();
    try {
      wrows = getCachedValues(wcsel, wcount, wcurs);
    } finally {
      wcurs.closeStatement();
      wcurs.close();
    }
    return wrows;
  }
  
  public static void clearCachedValues()
  {
    hstCached.clear();
  }
  
  public static Vector getCachedValues(String wcsel, int wcount, MCursor wcurs)
    throws MError
  {
    if ((hstCached.containsKey(wcsel)) && ((!MLogin.hasLoginProperty("XCACHE")) || (!MLogin.getLoginProperty("XCACHE").startsWith("N"))))
    {
      return (Vector)hstCached.get(wcsel);
    }
    
    wcurs.execQuery(wcsel);
    Vector wbrec = wcurs.getXRecords(wcount);
    wcurs.closeStatement();
    wcurs.close();
    if ((wbrec != null) && (wbrec.size() > 0)) {
      hstCached.put(wcsel, wbrec);
    }
    
    return wbrec;
  }
  
  public static Component getComponent(Container wct, String ws) {
    Component wc = null;
    if (wct != null) {
      Component[] wcomponents = wct.getComponents();
      for (int j = 0; j < wcomponents.length; j++) {
        if ((wcomponents[j].getName() != null) && (wcomponents[j].getName().equals(ws))) {
          wc = wcomponents[j];
          break;
        }
      }
    }
    return wc;
  }
  

































































  public static void showDoubleLogin(MFrame wf)
    throws MError
  {
    Properties p = myProp;
    Vector wv = new Vector();
    

    wv.addElement(new MLabel("Utilizador", true));
    MTextField wtf = new MTextField("user", 8, false);
    wtf.setText((String)p.get("user.name"));
    wv.addElement(wtf);
    wv.addElement(new MLabel("Password", true));
    


    JPasswordField wtt = new JPasswordField(9);
    wtt.setName("passwd");
    wtt.setEchoChar('*');
    wtt.setFont(MTextField.DEFAULTFONT);
    wtt.setBackground(MTextField.DEFAULTCOLOR);
    wtt.setForeground(MTextField.DEFFONTCOLOR);
    wtt.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    wv.addElement(wtt);
    



    wv.addElement(new MLabel("Origem", true));
    

    MChoice wch1 = new MChoice("dsn1");
    wch1.setMaximumRowCount(3);
    for (Enumeration e = p.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (wkey.startsWith("wdsn")) {
        wch1.addItem((String)p.get(wkey));
      }
    }
    wch1.setEditable(true);
    wv.addElement(wch1);
    
    wv.addElement(new MLabel("Destino", true));
    MChoice wch2 = new MChoice("dsn2");
    wch2.setMaximumRowCount(3);
    for (Enumeration e = p.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (wkey.startsWith("wdsn")) {
        wch2.addItem((String)p.get(wkey));
      }
    }
    wch2.setEditable(true);
    wv.addElement(wch2);
    
    MDialog wd = new MDialog(wf, "Duplo Login", true, wv, 3, "doublelogin");
    wd.getForm().setEnt("Duplo Login");
    wd.getForm().setEntName("Duplo Login");
    wd.pack();
    wd.setSize(getPreferredSizewidth, getPreferredSizeheight + 20);
    wd.setLocation(30, 30);
    

    wd.setVisible(true);
  }
  
  public static void showTripleLogin(MFrame wf) throws MError {
    Properties p = myProp;
    Vector wv = new Vector();
    



    wv.addElement(new MLabel("Metadata Minimal", true, 300, 0, 0, 0));
    
    wv.addElement(new MLabel("Utilizador", true));
    MTextField wtf = new MTextField("user1", 8, false);
    wv.addElement(wtf);
    wv.addElement(new MLabel("Password", true));
    JPasswordField wtt = new JPasswordField(9);
    wtt.setName("passwd1");
    wtt.setEchoChar('*');
    wtt.setFont(MTextField.DEFAULTFONT);
    wtt.setBackground(MTextField.DEFAULTCOLOR);
    wtt.setForeground(MTextField.DEFFONTCOLOR);
    wtt.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    wv.addElement(wtt);
    wv.addElement(new MLabel("Metadata Minimal", true));
    MChoice wch1 = new MChoice("dsn1");
    wch1.setMaximumRowCount(3);
    for (Enumeration e = p.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (wkey.startsWith("wdsn")) {
        wch1.addItem((String)p.get(wkey));
      }
    }
    wch1.setEditable(true);
    wv.addElement(wch1);
    wv.addElement(new MLabel(" ", true));
    

    wv.addElement(new MLabel("Metadata Cliente", true, 300, 0, 0, 0));
    
    wv.addElement(new MLabel("Utilizador", true));
    wtf = new MTextField("user2", 8, false);
    wv.addElement(wtf);
    wv.addElement(new MLabel("Password", true));
    


    wtt = new JPasswordField(9);
    wtt.setName("passwd2");
    wtt.setEchoChar('*');
    wtt.setFont(MTextField.DEFAULTFONT);
    wtt.setBackground(MTextField.DEFAULTCOLOR);
    wtt.setForeground(MTextField.DEFFONTCOLOR);
    wtt.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    wv.addElement(wtt);
    wv.addElement(new MLabel("Metadata Cliente", true));
    

    wch1 = new MChoice("dsn2");
    wch1.setMaximumRowCount(3);
    for (Enumeration e = p.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (wkey.startsWith("wdsn")) {
        wch1.addItem((String)p.get(wkey));
      }
    }
    wch1.setEditable(true);
    wv.addElement(wch1);
    wv.addElement(new MLabel(" ", true));
    
    wv.addElement(new MLabel("Base Dados Cliente", true, 300, 0, 0, 0));
    
    wv.addElement(new MLabel("Utilizador", true));
    wtf = new MTextField("user3", 8, false);
    wv.addElement(wtf);
    wv.addElement(new MLabel("Password", true));
    


    wtt = new JPasswordField(9);
    wtt.setName("passwd3");
    wtt.setEchoChar('*');
    wtt.setFont(MTextField.DEFAULTFONT);
    wtt.setBackground(MTextField.DEFAULTCOLOR);
    wtt.setForeground(MTextField.DEFFONTCOLOR);
    wtt.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    wv.addElement(wtt);
    
    wv.addElement(new MLabel("Base Dados Cliente", true));
    

    wch1 = new MChoice("dsn3");
    wch1.setMaximumRowCount(3);
    for (Enumeration e = p.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (wkey.startsWith("wdsn")) {
        wch1.addItem((String)p.get(wkey));
      }
    }
    wch1.setEditable(true);
    wv.addElement(wch1);
    wv.addElement(new MLabel(" ", true));
    
    wv.addElement(new MLabel(" ", true));
    wv.addElement(new MCheckBox("wsyncdata", "Sincronizar Menus e Códigos", false));
    
    MDialog wd = new MDialog(wf, "Sincronização", true, wv, 3, "doublelogin");
    wd.getForm().setEnt("Sincronização");
    wd.getForm().setEntName("Sincronização");
    wd.pack();
    wd.setSize(getPreferredSizewidth, getPreferredSizeheight + 20);
    
    wd.setCentered();
    
    wd.setVisible(true);
  }
  
  public static void showExec(MFrame wf) throws MError {
    Properties p = myProp;
    showExec(wf, (String)p.get("user.went"));
  }
  
  public static void showExec(MFrame wf, String went) throws MError {
    Properties p = myProp;
    Vector wv = new Vector();
    
    wv.addElement(new MLabel("Entidade", true));
    MTextField wtf = new MTextField("went", 5, false);
    wtf.setText(went);
    wtf.setMask("U");
    wv.addElement(wtf);
    MDialog wd = new MDialog(wf, "Exec", true, wv, 3, "Executar");
    wd.getForm().setEnt("EXECE");
    wd.pack();
    wd.setSize(wd.getPreferredSize());
    wd.setLocation(10, 10);
    
    wtf.requestFocus();
    wd.setVisible(true);
  }
  
  public static String seqNumber(String wkey, String ws, MRow wrow) throws MError {
    String wnum = "";
    MCursor wcurs = MConnection.getMCursor();
    try {
      wnum = seqNumber(wkey, ws, wrow, wcurs);
    } finally {
      wcurs.closeStatement();
      wcurs.close();
    }
    return wnum;
  }
  
  public static String seqNumber(String wkey, String ws, MRow wrow, MCursor wcurs) throws MError { return seqNumber(wkey, ws, wrow, wcurs, wrow.getNumCar(wkey)); }
  
  public static String seqNumber(String wkey, String ws, MRow wrow, MCursor wcurs, int wkeylen) throws MError
  {
    boolean bfound = false;
    
    MExp[] wvexp = MForm.getExp(wrow.getEnt(), wkey, "M1");
    if (wvexp != null) {
      MHashtable fzemc = new MHashtable();
      
      MPnuts mpnut = MPnuts.findMPnut("SeqNumber");
      mpnut.setRow(wrow);
      mpnut.put("FZEMC", fzemc);
      try {
        for (int i = 0; i < wvexp.length; i++) {
          MExp wexp = wvexp[i];
          if (mpnut.evalCondition(wexp.getCondition())) {
            bfound = true;
            
            String wkeyval = (String)wrow.get(wkey);
            

            int wfieldlen = wrow.getNumCar(wkey);
            
            String[] wargs = wexp.getArgs(wexp.getTextArgs());
            int wargslen = wargs.length;
            int wseqlen = wargslen > 0 ? Integer.valueOf(getNumber(wargs[0])).intValue() : wkeylen;
            String wseqtp = wargslen > 1 ? wargs[1] : "T";
            int wseqmax = wargslen > 2 ? Integer.valueOf(getNumber(wargs[2])).intValue() : wkeylen;
            if (wseqmax > wkeylen) { wseqmax = wkeylen;
            }
            
            String wprefix = wkeyval.substring(0, wkeyval.indexOf('%')).trim();
            int wpreflen = wprefix.length();
            if ("A".equals(wseqtp)) {
              for (int j = wpreflen - 1; (j >= 0) && (Character.isDigit(wprefix.charAt(j))); wseqlen--) { j--;
              }
            }
            int wlimite = wpreflen + wseqlen;
            if (wlimite > wseqmax) {
              wlimite = wseqmax;
              wseqlen = wlimite - wpreflen;
            }
            
            if ((wpreflen >= wseqmax) || (wseqlen <= 0)) {
              ws = wprefix;
              break;
            }
            

            String wbet1 = PT.Minimal.util.MString.fillRight(wprefix, wlimite, '0');
            String wbet2 = PT.Minimal.util.MString.fillRight(wprefix, wlimite, '9');
            String wlike = PT.Minimal.util.MString.fillRight(wprefix, wlimite, '_') + " %";
            if (wlike.length() > wfieldlen) {
              wlike = wlike.substring(0, wkeylen);
            }
            
            String wkeyx = "a." + wkey;
            String wseqsql = wkeyx + " like '" + wlike + "' and " + wkeyx + " between '" + wbet1 + "' and '" + wbet2 + "'" + " and length(rtrim(" + wkeyx + ")) = " + wlimite;
            




            fzemc.put("seqsql", wseqsql);
            
            MQuery mq = new MQuery();
            mq.addSelect(wexp.getSelect());
            mq.addFrom(wexp.getFrom());
            mq.addWhere(mpnut.evalFzemcValues(wexp.getWhere()));
            mq.addGroup(wexp.getGroup());
            mq.addOrder(wexp.getOrder());
            
            String wcsel = mq.getQuery();
            wcsel = mpnut.evalValues(wcsel);
            
            System.out.println("!*** MUtil mpnut.evalValues: " + wkey + ": " + wkeyval + ", prefix: " + wprefix + ", seqlen: " + wseqlen + ", seqtp: " + wseqtp + ", keylen: " + wkeylen + "\n" + wcsel);
            

            wcurs.execQuery(wcsel);
            Vector wv = wcurs.getXRecords(1);
            if (wv.size() == 0) break;
            Hashtable wh = (Hashtable)wv.firstElement();
            ws = (String)wh.get("seq");
            if ((ws == null) || (ws.trim().length() == 0)) {
              ws = wprefix + "0";
            }
            ws = incrementNum(ws, wlimite, wseqlen, 1, wpreflen);
            break;
          }
        }
      }
      finally
      {
        mpnut.remove("FZEMC");
        mpnut.unset("FZEMC");
        fzemc.clear();
      }
    }
    
    if (!bfound) {
      System.out.println("!*** MUtil MIN-MEXS01E " + wkey);
      MError wme = new MError("MIN-MEXS01E", new String[] { wkey });
      wme.setWdcode(64734);
      wme.setWmcode(-14);
      wme.setAtrib(wkey);
      throw wme;
    }
    return ws;
  }
  
  public static String incrementNum(String ws, int wcar, int wnmask, int wi, int wprefcar) throws MError {
    System.out.println("!*** MUtil number: " + ws + ", seqmax: " + wcar + ", seqlen: " + wnmask + ", ini: " + wi + ", seqlen: " + wprefcar);
    String wsnum = ws.trim();
    if (ws.length() > wcar) {
      wsnum = wsnum.substring(0, wcar);
    }
    int j = wsnum.length() - 1;
    System.out.println("!*** MUtil j- " + j);
    for (int i = j; (i >= wprefcar) && (Character.isDigit(ws.charAt(i))); i--) {
      j = i;
    }
    System.out.println("!*** MUtil j- " + j);
    if (j + wnmask > wcar) {
      wnmask = wcar - j;
    }
    if (j < wsnum.length()) {
      String wnum = wsnum.substring(j);
      String wmask = "";
      for (int i = 0; i < wnmask; i++) {
        wmask = wmask + "0";
      }
      DecimalFormat wdf = new DecimalFormat(wmask);
      wdf.setDecimalFormatSymbols(DFS);
      try {
        BigDecimal wbd = new BigDecimal(wnum);
        int i = wbd.intValue() + wi;
        wbd = new BigDecimal(i);
        wnum = wdf.format(wbd.intValue());
      } catch (Exception ex) {
        MError wme = new MError("Sequencialização invalida!");
        wme.setWmcode(-14);
        throw wme;
      }
      wsnum = wsnum.substring(0, j) + wnum;
    }
    System.out.println("!*** MUtil wsnum" + wsnum);
    return wsnum;
  }
  
  public static void expandExp(MExp[] wvexp, String went, String watrib, String wtable, String wprompt, String wexpansion, String wargs) throws MError
  {
    MHashtable fzemc = new MHashtable();
    MPnuts mpnut = MPnuts.getMPnut(went);
    mpnut.put("FZEMC", fzemc);
    mpnut.set("FZEMC", fzemc);
    
    fzemc.put("ictx", wtable);
    fzemc.put("iexs", wprompt);
    fzemc.put("iob", watrib);
    fzemc.put("valiob", "[m." + watrib + "]");
    fzemc.put("nargs", wargs);
    
    boolean wzk = false;
    if (wexpansion.trim().length() != 0) {
      String[] wniobs = getArgs(wexpansion.toLowerCase(), ',');
      for (int i = 0; i < wniobs.length; i++) {
        if (wniobs[i].startsWith("zk")) {
          fzemc.put("nzk", wniobs[i]);
          wzk = true;
        }
        else if (i == 0) {
          fzemc.put("niob", wniobs[i]);
        } else {
          fzemc.put("niob" + i, wniobs[i]);
          fzemc.put("valiob" + i, "[m." + wniobs[i] + "]");
        }
      }
    }
    else {
      fzemc.put("niob", "Xnctx");
    }
    if (!wzk) {
      fzemc.put("nzk", "zkXnctx");
    }
    


    for (int k = 0; k < wvexp.length; k++) {
      MExp wexp = wvexp[k];
      System.out.println("!*** MUtil wexp.getSelect(): " + wexp.getSelect());
      System.out.println("!*** MUtil wexp.getWhere(): " + wexp.getWhere());
      System.out.println("!*** MUtil mpnut.evalFzemcValues(wexp.getSelect()): " + mpnut.evalFzemcValues(wexp.getSelect()));
      System.out.println("!*** MUtil mpnut.evalFzemcValues(wexp.getWhere())" + mpnut.evalFzemcValues(wexp.getWhere()));
      
      wexp.setTextArgs(mpnut.evalFzemcValues(wexp.getTextArgs()));
      wexp.setSelect(mpnut.evalFzemcValues(wexp.getSelect()));
      wexp.setWhere(mpnut.evalFzemcValues(wexp.getWhere()));
      
      wexp.setObj(went);
      wexp.setAtrib(watrib);
      wexp.setTexp("E1");
    }
    
    mpnut.remove("FZEMC");
    mpnut.set("FZEMC", null);
    fzemc.clear();
  }
  
  public static void expandForm(MForm wform, boolean withErrors) {
    expandForm(wform, withErrors, wform.getFormValues());
  }
  
  public static void expandForm(MForm wform, boolean withErrors, Hashtable wh) {
    if (withErrors) {
      wh = expand(wform, "Force", wh);
    } else {
      wh = expand(wform, "NoForce", wh);
    }
    wform.setFormValues(wh);
  }
  
  public static Hashtable expand(MForm wform, String wtype, Hashtable wh) throws MError
  {
    MPnuts mpnut = MPnuts.getMPnut(wform.getEnt());
    MCursor wcurs = MConnection.getMCursor();
    
    MForm mold = (MForm)mpnut.get("m");
    mpnut.setForm(wform);
    try {
      for (e = wh.keys(); e.hasMoreElements();) {
        MExp[] wvexp = null;
        String wkey = (String)e.nextElement();
        MTextField wtf = null;
        

        Component wcomp = wform.getFormComponent(wkey);
        if ((wcomp != null) && ((wcomp instanceof MTextField)))
        {

          wtf = (MTextField)wcomp;
          
          if (wtf.getAtribType() != "3")
          {



            if (((String)wh.get(wkey)).trim().length() != 0)
            {
              wvexp = MForm.getExp(wform.getEnt(), wkey, "E1");
              if ((wvexp == null) && (((String)wh.get(wkey)).trim().length() != 0) && 
                ((wcomp instanceof MTextField)) && (wtf.getPromptCode().trim().length() != 0))
              {

                wvexp = MMData.getExpCopy(wtf.getPromptCode(), wtf.getPromptTable(), "E1");
                
                if (wvexp != null) {
                  expandExp(wvexp, wform.getEnt(), wkey, wtf.getPromptTable(), wtf.getPromptCode(), wtf.getExpansionField(), wtf.getPromptArgs());
                  MForm.addPreparedExp(wform.getEnt(), wkey, "E1", wvexp);
                }
              }
            }
            



            if ((wvexp != null) && (((String)wh.get(wkey)).trim().length() != 0)) {
              boolean bfound = false;
              boolean berr = true;
              for (int i = 0; i < wvexp.length; i++) {
                MExp wexp = wvexp[i];
                try {
                  boolean bcond = false;
                  try {
                    System.out.println("!*** MUtil Expand " + wform.getEnt() + " " + wkey + " : " + wexp.getCondition());
                    bcond = mpnut.evalCondition(wexp.getCondition());
                  } catch (MError err) {
                    if ((wtype.equals("Force")) || ((err.getMinErrorCode() != null) && (!err.getMinErrorCode().equals("MIN-001020E"))))
                    {

                      throw err;
                    }
                  }
                  if (bcond) {
                    bfound = true;
                    MQuery mq = new MQuery();
                    mq.addSelect(wexp.getSelect());
                    mq.addFrom(wexp.getFrom());
                    mq.addWhere(wexp.getWhere());
                    
                    mq.addGroup(wexp.getGroup());
                    mq.addOrder(wexp.getOrder());
                    
                    String[] wargs = wexp.getArgs(wtf.getExpansionField());
                    String wcsel = mq.getQuery();
                    wcsel = mpnut.evalValues(wcsel);
                    System.out.println("!*** MUtil Exp " + wkey + " - " + wexp.toString() + ": " + wcsel);
                    wcsel = wcsel.replace('"', '\'');
                    Vector wv = getCachedValues(wcsel, wcurs);
                    if (wv.size() != 0)
                    {
                      MoveRecord((Hashtable)wv.elementAt(0), wh);
                      berr = false;
                      break;
                    }
                    for (int j = 0; j < wargs.length; j++) {
                      Component wc = wform.getFormComponent(wargs[j].trim());
                      if (wc != null) {
                        ((MTextField)wc).setText("?");
                      }
                      if (wh.containsKey(wargs[j])) {
                        wh.put(wargs[j], "?");
                      }
                    }
                  }
                }
                catch (MError err) {
                  if ((err.getMinErrorCode() != null) && ((err.getMinErrorCode().equals("MIN-910001E")) || (err.getMinErrorCode().equals("MIN-910002E"))))
                  {

                    err.setAuxMessage("Atributo:" + wkey + "\n" + wexp.toString());
                  }
                  throw err;
                }
              }
              if (wtype.equals("Force")) {
                if (!bfound) {
                  throw new MError("Não foi encontrada expressão E1 válida...");
                }
                
                if ((berr) && (wtf.getDisplayType() != "P")) {
                  MError wme = new MError("MIN-001020E");
                  wme.setWmcode(-11);
                  wme.setAtrib(wkey);
                  
                  wme.setForm(wform);
                  
                  throw wme;
                }
              }
            }
          }
        }
      }
    } finally {
      Enumeration e;
      mpnut.remove("FZEMC");
      mpnut.set("FZEMC", null);
      mpnut.setForm(mold);
      wcurs.closeStatement();
      wcurs.close();
    }
    return wh;
  }
  
  public static Hashtable expand(MBrowse wbrc, String wtype, Hashtable wh) throws MError
  {
    MPnuts mpnut = MPnuts.getMPnut(wbrc.getEnt());
    Object mold = mpnut.get("m");
    MHashtable wmh = new MHashtable(wh);
    mpnut.put("m", wmh);
    mpnut.set("m", wmh);
    
    MCursor wcurs = MConnection.getMCursor();
    try {
      Object[] myCols = wbrc.getColumns();
      for (int z = 0; z < myCols.length; z++) {
        if ((myCols[z] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[z];
          String wkey = wcol.getName();
          
          MExp[] wvexp = null;
          
          if (((String)wh.get(wkey)).trim().length() != 0) {
            wvexp = MForm.getExp(wbrc.getEnt(), wkey, "E1");
            if ((wvexp == null) && (((String)wh.get(wkey)).trim().length() != 0) && 
              (wcol.getPromptCode().trim().length() != 0)) {
              wvexp = MMData.getExpCopy(wcol.getPromptCode(), wcol.getPromptTable(), "E1");
              
              if (wvexp != null) {
                expandExp(wvexp, wbrc.getEnt(), wkey, wcol.getPromptTable(), wcol.getPromptCode(), wcol.getExpansionField(), wcol.getPromptArgs());
                MForm.addPreparedExp(wbrc.getEnt(), wkey, "E1", wvexp);
              }
            }
          }
          


          if ((wvexp != null) && (((String)wh.get(wkey)).trim().length() != 0)) {
            boolean bfound = false;
            boolean berr = true;
            for (int i = 0; i < wvexp.length; i++) {
              MExp wexp = wvexp[i];
              try {
                boolean bcond = false;
                try {
                  bcond = mpnut.evalCondition(wexp.getCondition());
                } catch (MError err) {
                  if ((wtype.equals("Force")) || ((err.getMinErrorCode() != null) && (!err.getMinErrorCode().equals("MIN-001020E"))))
                  {

                    throw err;
                  }
                }
                if (bcond) {
                  bfound = true;
                  MQuery mq = new MQuery();
                  mq.addSelect(wexp.getSelect());
                  mq.addFrom(wexp.getFrom());
                  mq.addWhere(wexp.getWhere());
                  
                  mq.addGroup(wexp.getGroup());
                  mq.addOrder(wexp.getOrder());
                  String[] wargs = wexp.getArgs(wexp.getTextArgs());
                  String wcsel = mq.getQuery();
                  wcsel = mpnut.evalValues(wcsel);
                  System.out.println("!*** MUtil Exp " + wkey + " - " + wexp.toString() + ": " + wcsel);
                  wcsel = wcsel.replace('"', '\'');
                  Vector wv = getCachedValues(wcsel, wcurs);
                  if (wv.size() != 0)
                  {
                    MoveRecord((Hashtable)wv.elementAt(0), wh);
                    berr = false;
                    break;
                  }
                }
              } catch (MError err) {
                if (err.getMinErrorCode().equals("MIN-910001E")) {
                  err.setAuxMessage("Atributo:" + wkey + "\n" + wexp.toString());
                }
                throw err;
              }
            }
            if (wtype.equals("Force")) {
              if (!bfound) {
                throw new MError("Não foi encontrada expressão E1 válida...");
              }
              if (berr) {
                MError wme = new MError("MIN-001020E");
                wme.setWmcode(-11);
                wme.setAtrib(wkey);
                

                throw wme;
              }
            }
          }
        }
      }
    }
    finally {
      mpnut.remove("FZEMC");
      mpnut.set("FZEMC", null);
      mpnut.set("m", mold);
      wcurs.closeStatement();
      wcurs.close();
    }
    return wh;
  }
  
  public static void expand(MRow wrow) throws MError {
    expand(wrow, null);
  }
  
  public static void expand(MRow wrow, String wcol) throws MError
  {
    MPnuts mpnut = wrow.findMPnut();
    MRow rold = mpnut.containsKey("row") ? (MRow)mpnut.get("row") : null;
    Object mold = mpnut.get("m");
    mpnut.setRow(wrow);
    mpnut.put("m", wrow);
    mpnut.set("m", wrow);
    MCursor wcurs = MConnection.getMCursor();
    try { String[] wcols;
      String[] wcols;
      if (wcol == null) {
        wcols = wrow.getColNames();
      } else {
        wcols = new String[1];
        wcols[0] = wcol;
      }
      for (int i = 0; i < wcols.length; i++) {
        String wkey = wcols[i];
        if (((String)wrow.getField(wkey)).trim().length() != 0) {
          MExp[] wvexp = MForm.getExp(wrow.getEnt(), wkey, "E1");
          if (wvexp != null) {
            for (int k = 0; k < wvexp.length; k++) {
              MExp wexp = wvexp[k];
              boolean bcond = false;
              try {
                bcond = mpnut.evalCondition(wexp.getCondition());
              } catch (MError err) {
                if ((err.getMinErrorCode() == null) || (!err.getMinErrorCode().equals("MIN-001020E"))) {
                  throw err;
                }
              }
              if (bcond) {
                MQuery mq = new MQuery();
                mq.addSelect(wexp.getSelect());
                mq.addFrom(wexp.getFrom());
                mq.addWhere(wexp.getWhere());
                mq.addGroup(wexp.getGroup());
                mq.addOrder(wexp.getOrder());
                String wcsel = mq.getQuery();
                wcsel = mpnut.evalValues(wcsel);
                
                Vector wv = getCachedValues(wcsel, wcurs);
                if (wv.size() == 0) break;
                MoveRecord((Hashtable)wv.elementAt(0), wrow.getRecord()); break;
              }
            }
          }
        }
      }
    }
    finally
    {
      mpnut.setRow(rold);
      mpnut.set("m", mold);
      wcurs.closeStatement();
      wcurs.close();
    }
  }
  
  public static String validate(String ws, String wklcs) throws MError {
    String w = "";String w0 = wklcs.trim();
    int w1 = w0.length();
    w0 = w0 + w0 + "Z" + w0 + "S" + w0 + "N" + w0 + "G" + w0 + "K" + w0;
    w0 = w0 + w0 + w0 + w0 + w0;
    w0 = w0 + w0.substring(2);
    int w2 = wklcs.trim().hashCode();
    w2 += w1 * w1;
    String w3 = new Integer(w2).toString();
    w3 = w3 + w3 + w3 + w3 + w3;
    
    int k = w3.length() - 3;
    int j = k - 1;
    String wn0 = w0.substring(j, k);
    if (((wn0.toUpperCase().compareTo("0") <= 0) || (wn0.toUpperCase().compareTo("9") > 0)) && ((wn0.toUpperCase().compareTo("A") < 0) || (wn0.toUpperCase().compareTo("Z") > 0))) wn0 = "2";
    String wn1 = w3.substring(j, k);
    if (((wn1.toUpperCase().compareTo("0") <= 0) || (wn1.toUpperCase().compareTo("9") > 0)) && ((wn1.toUpperCase().compareTo("A") < 0) || (wn1.toUpperCase().compareTo("Z") > 0))) wn1 = "3";
    String wn2 = w3.substring(j - 7, k - 7);
    if (((wn2.toUpperCase().compareTo("0") <= 0) || (wn2.toUpperCase().compareTo("9") > 0)) && ((wn2.toUpperCase().compareTo("A") < 0) || (wn2.toUpperCase().compareTo("Z") > 0))) wn2 = "4";
    w = w + wn0 + "A" + wn1 + "F" + wn2 + "-";
    
    k = j - 3;
    j = k - 1;
    wn0 = w0.substring(j, k);
    if (((wn0.toUpperCase().compareTo("0") <= 0) || (wn0.toUpperCase().compareTo("9") > 0)) && ((wn0.toUpperCase().compareTo("A") < 0) || (wn0.toUpperCase().compareTo("Z") > 0))) wn0 = "4";
    wn1 = w3.substring(j, k);
    if (((wn1.toUpperCase().compareTo("0") <= 0) || (wn1.toUpperCase().compareTo("9") > 0)) && ((wn1.toUpperCase().compareTo("A") < 0) || (wn1.toUpperCase().compareTo("Z") > 0))) wn1 = "5";
    wn2 = w3.substring(j - 7, k - 7);
    if (((wn2.toUpperCase().compareTo("0") <= 0) || (wn2.toUpperCase().compareTo("9") > 0)) && ((wn2.toUpperCase().compareTo("A") < 0) || (wn2.toUpperCase().compareTo("Z") > 0))) wn2 = "6";
    w = w + wn0 + "I" + wn1 + "C" + wn2 + "-";
    
    k = j - 3;
    j = k - 1;
    wn0 = w0.substring(j, k);
    if (((wn0.toUpperCase().compareTo("0") <= 0) || (wn0.toUpperCase().compareTo("9") > 0)) && ((wn0.toUpperCase().compareTo("A") < 0) || (wn0.toUpperCase().compareTo("Z") > 0))) wn0 = "6";
    wn1 = w3.substring(j, k);
    if (((wn1.toUpperCase().compareTo("0") <= 0) || (wn1.toUpperCase().compareTo("9") > 0)) && ((wn1.toUpperCase().compareTo("A") < 0) || (wn1.toUpperCase().compareTo("Z") > 0))) wn1 = "7";
    wn2 = w3.substring(j - 7, k - 7);
    if (((wn2.toUpperCase().compareTo("0") <= 0) || (wn2.toUpperCase().compareTo("9") > 0)) && ((wn2.toUpperCase().compareTo("A") < 0) || (wn2.toUpperCase().compareTo("Z") > 0))) wn2 = "8";
    w = w + wn0 + "Q" + wn1 + "H" + wn2 + "-" + wklcs;
    






    return w;
  }
  
  public static void validate(MForm wform, String wtype) throws MError
  {
    Hashtable whashtable = wform.getFormComponentsRecord();
    for (Enumeration e = whashtable.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      Component[] wcomponents = (Component[])whashtable.get(wkey);
      for (int j = 0; j < wcomponents.length; j++) {
        if ((wcomponents[j] instanceof MTextField)) {
          MTextField wtf = (MTextField)wcomponents[j];
          if ((wtype.equals("Force")) && (wtf.getText().trim().length() == 0) && (wtf.getMustFill()))
          {

            MError wme = new MError("MIN-001010E");
            wme.setWmcode(-13);
            wme.setAtrib(wtf.getName());
            wme.setForm(wform);
            System.out.println("!*** MUtil v1 Atributo: " + wtf.getName());
            throw wme;
          }
          if ((wtype.equals("Force")) && ((wtf.getMask().startsWith("D")) || (wtf.getMask().startsWith("N"))))
          {
            try
            {
              wtf.getFormatedText(wtf.getText(), true);
            } catch (MError wme) {
              wme.setWmcode(-13);
              wme.setAtrib(wtf.getName());
              wme.setForm(wform);
              throw wme;
            }
          }
        }
        if ((wcomponents[j] instanceof MTextArea)) {
          MTextArea wta = (MTextArea)wcomponents[j];
          if ((wtype.equals("Force")) && (wta.getText().trim().length() == 0) && (wta.getMustFill()))
          {

            MError wme = new MError("MIN-001010E");
            wme.setWmcode(-13);
            wme.setAtrib(wta.getName());
            wme.setForm(wform);
            System.out.println("!*** MUtil v1 Atributo: " + wta.getName());
            throw wme;
          }
        }
      }
    }
  }
  
  public static void FieldSettings(MForm wform, String wfieldname, boolean wEditOn, int width) throws MError { FieldSettings(wform, wfieldname, wEditOn, width, null); }
  
  public static void FieldSettings(MForm wform, String wfieldname, boolean wEditOn, int width, String wtext) throws MError
  {
    Hashtable whashtable = wform.getFormComponentsRecord();
    for (Enumeration e = whashtable.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      Component[] wcomponents = (Component[])whashtable.get(wkey);
      for (int j = 0; j < wcomponents.length; j++)
      {


        if ((wcomponents[j] instanceof MTextField)) {
          MTextField wtf = (MTextField)wcomponents[j];
          if (wtf.getName().equals(wfieldname))
          {
            if (wEditOn)
            {

              wtf.setDisplayType("N");


            }
            else
            {

              wtf.setDisplayType("P");
            }
            




            if (width > -1) {
              if (width == 0) {
                wtf.setVisible(false);
              }
              else
              {
                String t = wtf.getText();
                String m = wtf.getMask();
                wtf.setVisible(true);
                wtf.setNumCar(width);
                wtf.setInternalWidth(width);
                wtf.setAtrrib();
                wtf.setInternalWidth(width);
                wtf.setEditable(true);
                wtf.setMode(true);
                wtf.setText(t);
                wtf.setMask(m);




              }
              





            }
            else
            {




              String t = wtf.getText();
              String m = wtf.getMask();
              wtf.setVisible(true);
              

              wtf.setAtrrib();
              wtf.setText(t);
              wtf.setMask(m);
            }
            
            if (wtext != null) {
              wtf.setText(wtext);
            }
          }
        }
        
        if ((wcomponents[j] instanceof MTextArea)) {
          MTextArea wta = (MTextArea)wcomponents[j];
          if (wta.getName().equals(wfieldname)) {
            if (wEditOn) {
              wta.setDisplayType("N");
            } else {
              wta.setDisplayType("P");
            }
            if (wtext != null) {
              wta.setText(wtext);
            }
          }
        }
      }
    }
  }
  
  public static void validate(MBrowse wbrc, String wtype, Hashtable whashtable) throws MError
  {
    Object[] myCols = wbrc.getColumns();
    for (int i = 0; i < myCols.length; i++) {
      if ((myCols[i] instanceof MBrowseColumn)) {
        MBrowseColumn wcol = (MBrowseColumn)myCols[i];
        String wkey = wcol.getName();
        if ((wtype.equals("Force")) && (((String)whashtable.get(wkey)).trim().length() == 0) && (wcol.getMustFill()))
        {

          MError wme = new MError("MIN-001010E");
          wme.setWmcode(-13);
          wme.setAtrib(wcol.getName());
          
          System.out.println("!*** MUtil v2 Atributo: " + wcol.getName());
          throw wme;
        }
      }
    }
  }
  





















  public static String[] getArgs(String wt)
  {
    return getArgs(wt, ',');
  }
  
  public static String[] getArgs(String wt, char wsep) {
    Vector wv = new Vector();
    
    if ((wt == null) || (wt.trim().length() == 0)) {
      return null;
    }
    

    int j = 0;
    int i; while ((i = wt.indexOf(wsep, j)) != -1) {
      if (i > j) {
        wv.addElement(wt.substring(j, i).trim());
      } else {
        wv.addElement("");
      }
      j = i + 1;
    }
    if (j < wt.length()) {
      wv.addElement(wt.substring(j).trim());
    } else {
      wv.addElement("");
    }
    
    String[] args = new String[wv.size()];
    wv.copyInto(args);
    return args;
  }
  
  public static String[] getArgs(String wt, String wsep) {
    Vector wv = new Vector();
    
    if ((wt == null) || (wt.trim().length() == 0)) {
      return null;
    }
    
    int j = 0;
    int wseplen = wsep.length();
    for (int i = wt.indexOf(wsep); i != -1; i = wt.indexOf(wsep, j)) {
      if (i > j) {
        wv.addElement(wt.substring(j, i).trim());
      } else {
        wv.addElement("");
      }
      j = i + wseplen;
    }
    if (j < wt.length()) {
      wv.addElement(wt.substring(j).trim());
    } else {
      wv.addElement("");
    }
    
    String[] args = new String[wv.size()];
    wv.copyInto(args);
    return args;
  }
  
  public static String getArg(String wt, char wsep) {
    return getArg(wt, wsep, 1);
  }
  
  public static String getArg(String s, char wsep, int n) {
    if ((s == null) || (s.trim().length() == 0)) {
      return null;
    }
    
    int j = 0;
    int k = 0;
    for (int i = s.indexOf(wsep); i != -1; i = s.indexOf(wsep, j)) {
      k++; if (k == n) {
        return s.substring(j, i).trim();
      }
      j = i + 1;
    }
    k++; if (k == n) {
      return s.substring(j).trim();
    }
    
    return "";
  }
  
  public static String getMask(String wtype, int wcar, int wdec) {
    String ws = " ";
    if (wtype.startsWith("n")) {
      ws = "N,##0";
      if (wdec > 0) {
        ws = ws + ".";
        for (int j = 0; j < wdec; j++) {
          ws = ws + "0";
        }
        ws = ws + "#";
      }
    }
    if (wtype.startsWith("d")) {
      ws = "Dyyyy-MM-dd";
      if (wcar == 4) {
        ws = "Dyyyy";
      }
      if (wcar == 7) {
        ws = "Dyyyy-MM";
      }
    }
    return ws;
  }
  
  public static byte[] getAssociatedObject(String went, BigDecimal wzk, String wobjname) throws MError {
    MCursor egbo_curs = MConnection.getMCursor();
    byte[] wobj = getAssociatedObject(went, wzk, wobjname, egbo_curs);
    egbo_curs.closeStatement();
    egbo_curs.close();
    return wobj;
  }
  
  public static byte[] getAssociatedObject(String went, BigDecimal wzk, String wobjname, MCursor egbo_curs) throws MError
  {
    byte[] wobj = null;
    String wtable = "egbot0";
    
    if (went.substring(0, 4).toUpperCase().equals("MFIC")) {
      wtable = "mobjt0";
    }
    
    String wcsel = " select zobj from " + wtable + " a " + " where a.zent = '" + went.substring(0, 4).toUpperCase() + "'" + " and a.zkent = " + wzk + " and a.egboi = '" + wobjname + "'";
    


    try
    {
      egbo_curs.execQuery(wcsel);
      if (egbo_curs.getWdcode() == 0) {
        wobj = egbo_curs.getBytes(1);
      }
    } finally {
      egbo_curs.closeStatement();
      egbo_curs.close();
    }
    return wobj;
  }
  
  public static String getSQLValue(String wcsel) throws MError {
    MCursor curs = MConnection.getMCursor();
    try {
      return getSQLValue(wcsel, curs);
    } finally {
      curs.closeStatement();
      curs.close();
    }
  }
  
  public static String getSQLValue(String wcsel, MCursor mcurs) throws MError {
    try {
      mcurs.execQuery(wcsel);
      Hashtable wrecord = mcurs.getRecord();
      if (mcurs.getWdcode() == 0) {
        String[] wcols = mcurs.getColNames();
        return (String)wrecord.get(wcols[0]);
      }
    } finally {
      mcurs.closeStatement();
      mcurs.close();
    }
    return "";
  }
  
  public static int executeSQL(String wcsel) throws MError {
    MCursor curs = MConnection.getMCursor();
    try {
      return executeSQL(wcsel, curs);
    } finally {
      curs.closeStatement();
      curs.close();
    }
  }
  
  public static int executeSQL(String wcsel, MCursor mcurs) {
    return mcurs.execStatement(wcsel);
  }
  
  public static int executeSQLDirect(String wcsel) throws MError {
    MCursor curs = MConnection.getMCursor();
    try {
      return executeSQLDirect(wcsel, curs);
    } finally {
      curs.closeStatement();
      curs.close();
    }
  }
  
  public static int executeSQLDirect(String wcsel, MCursor mcurs) {
    return mcurs.execStatement(wcsel, false);
  }
  
  public static void createStatement(MCursor mcurs)
  {
    mcurs.createStatement();
  }
  
  public static void addBatch(String wcsel, MCursor mcurs) {
    mcurs.addBatch(wcsel);
  }
  







  public static void executeBatchStatement(MCursor mcurs)
  {
    mcurs.executeBatchStatement();
  }
  
  public static double convert(Object anObject)
  {
    if (anObject == null)
      return 0.0D;
    if ((anObject instanceof Number)) {
      double answer = ((Number)anObject).doubleValue();
      
      return answer;
    }
    try { String wn = getNumber(anObject.toString());
      return new Double(wn).doubleValue();
    }
    catch (NumberFormatException ex) {}
    
    return 0.0D;
  }
  
  public static String getMinProperty(String wp) { return (String)myProp.get(wp); }
  
  public static Properties getMinProperties() {
    return myProp;
  }
  
  public static byte[] getBytesFromStream(java.io.InputStream wbin) throws MError {
    try {
      java.io.ByteArrayOutputStream wbout = new java.io.ByteArrayOutputStream();
      int wbyte;
      while ((wbyte = wbin.read()) != -1) {
        wbout.write(wbyte);
      }
      return wbout.toByteArray();
    } catch (java.io.IOException e) {
      throw new MError(e);
    }
  }
  
  public static MForm getMForm(Component wc) {
    while ((!(wc instanceof java.awt.Window)) && (!(wc instanceof MForm))) {
      wc = wc.getParent();
    }
    if ((wc instanceof MForm)) {
      return (MForm)wc;
    }
    return null;
  }
  
  public static MRow getMFormRowOrNull(Component wc) {
    MForm mform = getMForm(wc);
    if (mform != null) {
      return mform.getRow();
    }
    return null;
  }
  
  public static MForm getComponentMForm(Component wc) {
    while ((!(wc instanceof java.awt.Window)) && (!(wc instanceof MForm))) {
      wc = wc.getParent();
    }
    if ((wc instanceof MForm)) {
      return (MForm)wc;
    }
    return null;
  }
  
  public static void refreshForm(MForm wfmc) {
    wfmc.getRow().refreshRow();
    wfmc.setFormValues(expand(wfmc, "NoForce", wfmc.getRow().getRecord()));
  }
  
  public static String showTextRTF(MForm minForm, String wtitle) throws MError {
    return showTextRTF(minForm, wtitle, "");
  }
  
  public static String showTextRTF(MForm minForm, String wtitle, Object wtext) throws MError {
    return stringRep(showText(minForm, wtitle, wtext), "\n", "\\line ");
  }
  
  public static String showText(MForm minForm, String wtitle) throws MError {
    return showText(minForm, wtitle, "");
  }
  
  public static String showText(MForm minForm, String wtitle, Object wtext) throws MError {
    String wfname = "MUTIL.SHOW_TEXT";
    PT.Minimal.editor.MEditor meditor = (PT.Minimal.editor.MEditor)MForm.getMForm(wfname);
    
    if (meditor == null)
    {
      String type = "text/plain";
      meditor = new PT.Minimal.editor.MEditor(wfname, minForm.getEnt(), minForm.getEntName(), type);
    
      meditor.setAction("SHOW_TEXT");
    }
    
    meditor.setText(wtext.toString());
    meditor.setFase(1);
    
    MDialog wd = new MDialog(minForm.getMFrame(), wtitle, true, meditor);
    

    wd.pack();
    
    java.awt.Dimension wdim = new java.awt.Dimension(500, 350);
    wd.setSize(wdim);
    wd.setCentered(wdim);
    wd.getForm().showForm();
    wd.dispose();
    return meditor.getText();
  }
  

  public static String getTextRTF(String wtext) throws MError { return stringRep(wtext, "\n", "\\line "); }
  
  public static String getTextXML(String wtext) throws MError {
    String w = stringRep(wtext, "\n", "&lt;br&gt;");
    w = stringRep(w, "<br>", "&lt;br&gt;");
    return stringRep(w, "\"", "\\\"");
  }
  
  public static String getStringInput(MForm wform, String wtitle, String wquest, String wvalue) {
    return getStringInput(wform, wtitle, wquest, wvalue, 40);
  }
  
  public static String getStringInput(MForm wform, String wtitle, String wquest, String wvalue, int i) {
    int wmsgtype = 3;
    
    Object[] woptions = new Object[2];
    woptions[0] = new String("     OK     ");
    woptions[1] = new String(lang("\"Cancelar\""));
    
    Object[] wmsgs = new Object[2];
    wmsgs[0] = wquest;
    MTextField winput = new MTextField("winput", i);
    winput.setText(wvalue);
    wmsgs[1] = winput;
    int wanswer = javax.swing.JOptionPane.showOptionDialog(wform.getMFrame(), wmsgs, wtitle, -1, wmsgtype, null, woptions, woptions[0]);
    

    if (wanswer == 1) {
      throw new MError("MIN-000001W");
    }
    return winput.getText();
  }
  
  public static BigDecimal getNumberInput(MForm wform, String wtitle, String wquest, Number wvalue) {
    int wmsgtype = 3;
    
    Object[] woptions = new Object[2];
    woptions[0] = new String("     OK     ");
    woptions[1] = new String(lang("\"Cancelar\""));
    
    Object[] wmsgs = new Object[2];
    wmsgs[0] = wquest;
    MTextField winput = new MTextField("winput", 20);
    winput.setText(wvalue.toString());
    wmsgs[1] = winput;
    int wanswer = javax.swing.JOptionPane.showOptionDialog(wform.getMFrame(), wmsgs, wtitle, -1, wmsgtype, null, woptions, woptions[0]);
    

    if (wanswer == 1) {
      throw new MError("MIN-000001W");
    }
    return new BigDecimal(getNumber(winput.getText()));
  }
  
  public static StringBuffer getSimpleText(StringBuffer wtb) {
    StringBuffer wout = new StringBuffer();
    wout.append(getSimpleText(wtb.toString()));
    return wout;
  }
  
  public static String getSimpleText(String wt)
  {
    wt = stringRep(wt, "ç", "c");
    
    wt = stringRep(wt, "ã", "a");
    wt = stringRep(wt, "â", "a");
    wt = stringRep(wt, "á", "a");
    wt = stringRep(wt, "à", "a");
    
    wt = stringRep(wt, "ê", "e");
    wt = stringRep(wt, "é", "e");
    wt = stringRep(wt, "è", "e");
    
    wt = stringRep(wt, "í", "i");
    
    wt = stringRep(wt, "õ", "o");
    wt = stringRep(wt, "ô", "o");
    wt = stringRep(wt, "ó", "o");
    wt = stringRep(wt, "ò", "o");
    
    wt = stringRep(wt, "û", "u");
    wt = stringRep(wt, "ú", "u");
    wt = stringRep(wt, "ù", "u");
    
    wt = stringRep(wt, "Ç", "C");
    
    wt = stringRep(wt, "Ã", "A");
    wt = stringRep(wt, "Â", "A");
    wt = stringRep(wt, "Á", "A");
    wt = stringRep(wt, "À", "A");
    
    wt = stringRep(wt, "Ê", "E");
    wt = stringRep(wt, "É", "E");
    wt = stringRep(wt, "È", "E");
    
    wt = stringRep(wt, "Í", "I");
    
    wt = stringRep(wt, "Õ", "O");
    wt = stringRep(wt, "Ô", "O");
    wt = stringRep(wt, "Ó", "O");
    wt = stringRep(wt, "Ò", "O");
    
    wt = stringRep(wt, "Û", "U");
    wt = stringRep(wt, "Ú", "U");
    wt = stringRep(wt, "Ù", "U");
    
    return wt;
  }
  







  public static void grabFocus(javax.swing.JComponent comp) {}
  






  public static void requestFocus(Component comp)
  {
    System.out.println("!*** MUtil Executing requestFocus: " + comp.getName());
    comp.requestFocus();
  }
  












  public static void transferFocus(Component comp)
  {
    System.out.println("!*** MUtil Executing transferFocus: " + comp.getName());
    comp.transferFocus();
  }
  














  public static void showLater(MError err, MForm form) {}
  














  public static String getLogoPath()
  {
    return getLogoPath(MLogin.getUserOrg());
  }
  
  public static String getLogoPath(String wiorg)
  {
    String wpath = "";
    
    String wlogo = "LOGO_" + wiorg.trim();
    if (MLogin.hasDBProperty(wlogo)) {
      return MLogin.getDBProperty(wlogo);
    }
    
    wlogo = "LOGO_DEFAULT";
    if (MLogin.hasDBProperty(wlogo)) {
      return MLogin.getDBProperty(wlogo);
    }
    
    return wpath;
  }
  
  public static String getFooterPath() {
    return getLogoPath(MLogin.getUserOrg());
  }
  
  public static String getFooterPath(String wiorg)
  {
    String wpath = "";
    
    String wlogo = "FOOTER_" + wiorg.trim();
    if (MLogin.hasDBProperty(wlogo)) {
      return MLogin.getDBProperty(wlogo);
    }
    
    wlogo = "FOOTER_DEFAULT";
    if (MLogin.hasDBProperty(wlogo)) {
      return MLogin.getDBProperty(wlogo);
    }
    
    return wpath;
  }
  
  public static String getFilePath(String wtitle) {
    return getFilePath(wtitle, "");
  }
  
  public static String getFilePath(String wtitle, String wdir) {
    return getFilePath(wtitle, wdir, "");
  }
  
  public static String getFilePath(String wtitle, String wdir, String wfilename) {
    String wchoosedfile = "";
    FileDialog wfd = new FileDialog(new java.awt.Frame(), wtitle, 1);
    if (wdir.trim().length() != 0) {
      java.io.File wentdir = new java.io.File(wdir);
      if ((wentdir.exists()) && (wentdir.isDirectory())) {
        wfd.setDirectory(wentdir.getAbsolutePath());
      }
    }
    wfd.setFile(wfilename);
    wfd.setVisible(true);
    
    if (wfd.getFile() == null) {
      throw new MError("MIN-000001W");
    }
    wchoosedfile = wfd.getDirectory() + "/" + wfd.getFile();
    
    return wchoosedfile;
  }
  
  public static String getIndexedName(String wnome) {
    System.out.println("!*** MUtil wnome: " + wnome);
    String wcsel = "select minixn('Q','" + wnome + "') as wresult from dual ";
    String wresult = "";
    Vector wv = getCachedValues(wcsel);
    if (wv.size() != 0) {
      Hashtable wh = (Hashtable)wv.elementAt(0);
      wresult = (String)wh.get("wresult");
    }
    System.out.println("!*** MUtil wresult: " + wresult);
    return wresult;
  }
  
  public static String getIndexedNameFromField(String formName, String wkey)
  {
    String ws = "";
    MForm wform = MForm.getMForm(formName);
    if ((wform != null) && (wform.getFormValue(wkey) != null)) {
      ws = getIndexedName(wform.getFormValue(wkey));
    }
    if (ws == "") {
      ws = "%";
    }
    return ws;
  }
  
  public static String[] getConnectionParameters() throws MError {
    MForm wlogin = MForm.getMForm("Menu Login");
    wlogin.setEnt("SDOB0");
    wlogin.setAction("NONE");
    wlogin.showForm();
    if (getFormParentbuttonChoosed == 1) {
      throw new MError("MIN-000001W");
    }
    Hashtable wh = getRecord(wlogin.getCenterPanel());
    String wdsn = (String)wh.get("dsn");
    Properties p = myProp;
    Enumeration e; if (p.contains(wdsn)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          wdsn = (String)p.get("x" + wkey.substring(1));
        }
      }
    }
    
    String[] wparameters = { wdsn, (String)wh.get("user"), (String)wh.get("passwd") };
    return wparameters;
  }
  
  public static void apllySCFG() throws MError
  {
    PT.Minimal.entities.SDOB0.apllySCFG(false);
  }
  
  public static String getPasswordInput(MForm wform, String wtitle, String wquest, String wvalue) {
    int wmsgtype = 3;
    
    Object[] woptions = new Object[2];
    woptions[0] = new String("     OK     ");
    woptions[1] = new String(lang("\"Cancelar\""));
    
    Object[] wmsgs = new Object[2];
    wmsgs[0] = wquest;
    
    JPasswordField winput = new JPasswordField(9);
    winput.setEchoChar('*');
    winput.setText(wvalue);
    wmsgs[1] = winput;
    int wanswer = javax.swing.JOptionPane.showOptionDialog(wform.getMFrame(), wmsgs, wtitle, -1, wmsgtype, null, woptions, woptions[0]);
    

    if (wanswer == 1) {
      throw new MError("MIN-000001W");
    }
    char[] c = winput.getPassword();
    String pwd = " ".trim();
    for (int i = 0; i < c.length; i++) {
      pwd = pwd + c[i];
    }
    return pwd;
  }
  
  public static String lang(String orig)
  {
    if (orig.indexOf("\n") >= 0) orig = stringRep(orig, "\n", "\\n");
    Hashtable ht = MGetMData.getTran();
    boolean inString = false;
    
    String fim = " ".trim();
    String palavra = " ".trim();
    for (int i = 0; i < orig.length(); i++) {
      Character ch = new Character(orig.charAt(i));
      if (ch.charValue() == '"') {
        if (!inString) {
          inString = true;
          palavra = " ".trim();
        } else {
          if (ht.containsKey(palavra.trim()))
          {
            fim = fim + stringRep(palavra, palavra.trim(), ht.get(palavra.trim()).toString());
          }
          else {
            fim = fim + palavra;
          }
          inString = false;
        }
      }
      else if (inString) {
        palavra = palavra + ch.toString();
      } else {
        fim = fim + ch.toString();
      }
    }
    


    fim = stringRep(fim, "\\n", "\n");
    return fim;
  }
  
  public static boolean classExists(String classe) {
    try { Class.forName(classe);
      return true;
    } catch (ClassNotFoundException e) {}
    return false;
  }
}
