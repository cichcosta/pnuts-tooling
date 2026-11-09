package PT.Minimal.geral;

import PT.Minimal.pnuts.MPnuts;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

public class MLogin
{
  private static Properties myProp = new Properties();
  private static Hashtable localMetaData;
  private static Hashtable xsuact;
  private static String wuser = "";
  private static String wuserNodo = "";
  private static String wlang = "";
  
  private static String wuserTipo = "";
  private static String wuserMenu = "J000A";
  private static String wuserOrg = "";
  private static String wuserAutLevel = "";
  
  private static MDBProperties wdbprop;
  private static MDBProperties wmetaprop;
  private static MAtribProperties watribprop;
  private static boolean logged = false;
  private static boolean METAlogged = false;
  private static MMenu wmenu_inicial;
  private static MLoginWindow wsplash;
  private static boolean printPreview = false;
  
  private static SimpleDateFormat wdatef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  


  public MLogin() {}
  

  public static void showLogin()
    throws MError
  {
    showLogin(new MFrame());
  }
  
  public static void loadLoginProperties() throws MError {
    Properties p = myProp;
    Properties mprop; Enumeration e; if (p.get("user.dsn") == null) {
      mprop = new Properties();
      try {
        String wprop = System.getProperties().getProperty("PT.Minimal.propfile", "/Minimal.pro");
        p.put("propfile", wprop);
        System.out.println("!*** MLogin Properties file: " + wprop);
        
        if (wprop.indexOf("/") != -1) {
          wprop = MUtil.stringRep(wprop, "/", "");
        }
        BufferedReader wimp = new BufferedReader(new java.io.FileReader(wprop));
        String wline = "";
        while ((wline = wimp.readLine()) != null) {
          if (wline.indexOf("=") != -1) {
            if (wline.indexOf("jdbc") == -1) {
              wline = MUtil.stringRep(wline, "\\\\", "\\");
              wline = MUtil.stringRep(wline, "\\", "\\\\");
            }
            mprop.put(wline.substring(0, wline.indexOf("=")).trim(), wline.substring(wline.indexOf("=") + 1).trim());
          }
        }
        wimp.close();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
      wuserNodo = "0000";
      p.put("user.group", wuserNodo);
      if (System.getProperties().getProperty("PT.Minimal.user.name") != null) {
        p.put("user.name", System.getProperties().getProperty("PT.Minimal.user.name"));
      }
      if (System.getProperties().getProperty("PT.Minimal.user.passwd") != null) {
        p.put("user.passwd", System.getProperties().getProperty("PT.Minimal.user.passwd"));
      }
      if (System.getProperties().getProperty("PT.Minimal.user.dsn") != null) {
        p.put("user.dsn", System.getProperties().getProperty("PT.Minimal.user.dsn"));
      }
      if (System.getProperties().getProperty("PT.Minimal.user.xdsn") != null) {
        p.put("user.xdsn", System.getProperties().getProperty("PT.Minimal.user.dsn"));
      }
      for (e = mprop.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        p.put(wkey, (String)mprop.get(wkey));
      }
    }
    MUtil.myProp = myProp;
  }
  
  public static void showLogin(MFrame wf, MMenu wm)
    throws MError
  {
    String wversion = (String)System.getProperties().get("java.version");
    if (wversion.startsWith("1.1")) {
      MFrame mf = new MFrame();
      mf.setBackground(new Color(224, 233, 254));
      showLogin(mf);
      wmenu_inicial.doInitialMenu();
      return;
    }
    Properties p = myProp;
    loadLoginProperties();
    
    System.out.println("!*** MLogin showLogin: " + p.get("user.dsn") + " " + p.get("user.name"));
    
    if ((p.get("user.dsn") != null) && (p.get("user.name") != null) && (p.get("user.passwd") != null))
    {

      doLogin();
      wmenu_inicial.doInitialMenu();
      return;
    }
    

    prepareLoginDialog(wm);
    wsplash = new MLoginWindow(p.getProperty("CLK", "..."), new Vector());
  }
  
  public static void cancelSplashLogin() {
    System.exit(0);
  }
  
  public static void doSplashLogin(Properties wsplash_prop) throws MError {
    try {
      doLogin(wsplash_prop);
      
      wmenu_inicial.doInitialMenu();
      wsplash.setVisible(false);
    }
    catch (MError err)
    {
      err.show(wmenu_inicial);
      throw err;
    }
  }
  
  public static void showLogin(MFrame wf) throws MError {
    Properties p = myProp;
    loadLoginProperties();
    System.out.println("!*** MLogin showLogin: " + p.get("user.dsn") + " " + p.get("user.name") + " " + p.get("user.passwd"));
    

    if ((p.get("user.dsn") != null) && (p.get("user.name") != null) && (p.get("user.passwd") != null))
    {

      doLogin();
      
      return;
    }
    

    if (p.get("user.dsn") == null) {
      Properties mprop = new Properties();
      try {
        String wprop = System.getProperties().getProperty("PT.Minimal.propfile", "/Minimal.pro");
        mprop.load(mprop.getClass().getResourceAsStream(wprop));
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      wuserNodo = "0000";
      p.put("user.group", wuserNodo);
      Vector wv = new Vector();
      

      wv.addElement(new MLabel("Utilizador", true));
      MTextField wtf = new MTextField("user", 8, false);
      wtf.setText((String)p.get("user.name"));
      wtf.setInternalWidth(8);
      

      wv.addElement(wtf);
      wv.addElement(new MLabel("Password", true));
      JPasswordField wtt = new JPasswordField(9);
      wtt.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(215, 228, 244)));
      wtt.setName("passwd");
      wtt.setEchoChar('*');
      wtt.setFont(MTextField.DEFAULTFONT);
      wtt.setBackground(MTextField.DEFAULTCOLOR);
      wtt.setForeground(MTextField.DEFFONTCOLOR);
      


      wv.addElement(wtt);
      
      wv.addElement(new MLabel("DSN", true));
      MChoice wch = new MChoice("dsn");
      wch.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(215, 228, 244)));
      wch.addDefaultNavigatorAgent();
      wch.addDefaultFocusListener();
      wch.setMaximumRowCount(3);
      Vector wvdsn = new Vector();
      for (Enumeration e = mprop.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if (wkey.startsWith("wdsn")) {
          wvdsn.addElement((String)mprop.get(wkey));
        }
        p.put(wkey, (String)mprop.get(wkey));
      }
      
      String wversion = (String)System.getProperties().get("java.version");
      if (!wversion.startsWith("1.1")) {
        java.util.Collections.sort(wvdsn);
      }
      
      for (int i = 0; i < wvdsn.size(); i++) {
        wch.addItem((String)wvdsn.elementAt(i));
      }
      wch.setEditable(true);
      wv.addElement(wch);
      MDialog wd = new MDialog(wf, "Menu Login", true, wv, 3, "login");
      wd.getForm().setEnt("Menu Login");
      wd.pack();
      wd.setSize(getPreferredSizewidth, getPreferredSizeheight + 20);
      wd.setLocation(30, 30);
      MUtil.myProp = myProp;
      wd.setVisible(true);
      
      MUtil.requestFocus(wtf);
    }
  }
  
  public static void doLogin() throws MError
  {
    MConnection.closeAll();
    logged = false;
    METAlogged = false;
    Properties p = myProp;
    if (!p.containsKey("JMETAUSER")) {
      p.put("user.metaname", p.get("user.name"));
    } else {
      p.put("user.metaname", p.get("JMETAUSER"));
    }
    if (!p.containsKey("JMETAPASSWD")) {
      p.put("user.metapasswd", p.get("user.passwd"));
    } else {
      p.put("user.metapasswd", p.get("JMETAPASSWD"));
    }
    if ((!p.containsKey("JMETADATA")) && (p.containsKey("DEFAULT"))) {
      p.put("JMETADATA", p.get("DEFAULT"));
    }
    


    String wos = (String)System.getProperties().get("os.name");
    String wpath = (String)System.getProperties().get("user.dir");
    p.put("user.dir", wpath);
    String wpcmd; if ((wos.equals("Windows 95")) || (wos.equals("Windows 98")) || (wos.equals("Windows Me"))) {
      String wtemp = "c:" + File.separator + "windows" + File.separator + "temp";
      wpcmd = "start";
    } else { String wpcmd;
      if (wos.equals("Linux")) {
        String wtemp = "/tmp";
        wpcmd = " ";
      } else {
        wtemp = "c:" + File.separator + "temp";
        wpcmd = "cmd /c start";
      }
    }
    
    String wtemp = System.getProperties().getProperty("PT.Minimal.temp", wtemp);
    p.put("JTEMP", wtemp);
    String wpcmd = System.getProperties().getProperty("PT.Minimal.print", wpcmd);
    p.put("JSTART", wpcmd);
    
    wuser = (String)myProp.get("user.name");
    MUtil.myProp = myProp;
    doMetaDataConnection();
    METAlogged = true;
    doDefaultConnection();
    logged = true;
    
    setGlobalVariables();
  }
  
  public static void doLogin(MForm minForm) throws MError {
    Hashtable wh = MUtil.getRecord(minForm.getCenterPanel());
    doLogin(wh);
  }
  
  public static void doLogin(Hashtable wh) throws MError
  {
    Properties p = myProp;
    p.put("user.name", ((String)wh.get("user")).trim());
    p.put("user.passwd", ((String)wh.get("passwd")).trim());
    p.put("user.dsn", ((String)wh.get("dsn")).trim());
    p.put("user.xdsn", ((String)wh.get("dsn")).trim());
    String wdsn = (String)wh.get("dsn");
    Enumeration e; if (p.contains(wdsn)) {
      for (e = p.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((p.getProperty(wkey).equals(wdsn)) && 
          (p.get("x" + wkey.substring(1)) != null)) {
          p.put("user.dsn", p.get("x" + wkey.substring(1)));
        }
      }
    }
    
    p.put("DEFAULT", p.get("user.dsn"));
    doLogin();
  }
  
  private static void doDefaultConnection()
    throws MError
  {
    MCursor suac_con = MConnection.getMCursor("DEFAULT", (String)myProp.get("user.dsn"), (String)myProp.get("user.name"), (String)myProp.get("user.passwd"));
    








    String wcsel = "select * from suact0 ";
    wcsel = wcsel + " where iutl = lower(USER)";
    wcsel = wcsel + "   and today() between dini and dfim";
    

    System.out.println("!*** MLogin query: " + wcsel);
    try {
      wuserOrg = MUtil.getSQLValue("select max(tparms) from bctxt0 where ictx = '@*ORGDEFAULT'");
      wuserOrg = wuserOrg.trim();
      suac_con.execQuery(wcsel);
      
      if (suac_con.getWdcode() == 0) {
        xsuact = suac_con.getRecord();
        if ((xsuact.get("imen") != null) && (((String)xsuact.get("imen")).trim().length() != 0))
        {
          wuserMenu = (String)xsuact.get("imen");
        }
        
        if ((xsuact.get("iorg") != null) && (((String)xsuact.get("iorg")).trim().length() != 0))
        {
          wuserOrg = (String)xsuact.get("iorg");
        }
        
        if ((xsuact.get("igrp") != null) && (((String)xsuact.get("igrp")).trim().length() != 0))
        {
          wuserNodo = (String)xsuact.get("igrp");
        }
        if ((xsuact.get("cns") != null) && (((String)xsuact.get("cns")).trim().length() != 0))
        {
          wuserAutLevel = (String)xsuact.get("cns");
        }
        
        if ((xsuact.get("cut") != null) && (((String)xsuact.get("cut")).trim().length() != 0))
        {
          wuserTipo = ((String)xsuact.get("cut")).trim();
        }
        if ((xsuact.get("ilan") != null) && (((String)xsuact.get("ilan")).trim().length() != 0))
        {
          wlang = ((String)xsuact.get("ilan")).trim();
        }
      }
      else {
        if (!wuser.equals("minimal")) {
          throw new MError("MIN-009997E", new String[] { (String)myProp.get("user.name") });
        }
        wuserAutLevel = "9";
      }
      
      suac_con.closeStatement();
      

      loadClientMeta(suac_con);
    }
    finally {
      suac_con.close();
    }
    

    wdbprop = new MDBProperties(MConnection.getMConnection("DEFAULT"));
    watribprop = new MAtribProperties(MConnection.getMConnection("DEFAULT"));
    
    getLastModifiedForClassJar(watribprop.getClass());
  }
  
  private static void doMetaDataConnection() throws MError
  {
    if (((!hasLoginProperty("JMETADATAMODE")) || (!getLoginProperty("JMETADATAMODE").equals("OFFLINE"))) && ((!hasLoginProperty("JMETADATAMODE")) || (!getLoginProperty("JMETADATAMODE").equals("METAFILE"))) && ((!hasLoginProperty("JMETADATAMODE")) || (!getLoginProperty("JMETADATAMODE").equals("METAFILEALL"))))
    {


      MCursor suac_con = MConnection.getMCursor("JMETADATA", (String)myProp.get("JMETADATA"), (String)myProp.get("user.metaname"), (String)myProp.get("user.metapasswd"));
      





      try
      {
        wmetaprop = new MDBProperties(suac_con.getMConnection());
      } finally {
        suac_con.closeStatement();
        suac_con.close();
      }
    }
    if ((hasLoginProperty("JMETADATAMODE")) && ((getLoginProperty("JMETADATAMODE").equals("OFFLINE")) || (getLoginProperty("JMETADATAMODE").equals("INLINE"))))
    {

      MSerializedObject.setMode(true);
    }
  }
  
  public static void checkMinimalKey() throws MError {
    String winkey = "";
    if (myProp.get("JMETADATAKEY") != null) {
      winkey = (String)myProp.get("JMETADATAKEY");
    }
    
    if (!winkey.equals(getMinKey())) {
      MForm wminlogin = MForm.getDialogForm(MForm.getMForm("Menu Login"), "ZLOG0", "ME", "Introduza chave MINIMAL", 2);
      if (!wminlogin.getFormValue("zpasswd").equals(getMinKey())) {
        wminlogin.showForm();
      }
      winkey = wminlogin.getFormValue("zpasswd");
      if (!winkey.equals(getMinKey())) {
        MConnection.closeAll();
        throw new MError("MIN-009996E");
      }
    }
  }
  
  public static boolean isSameAsMetaData()
  {
    return myProp.get("JMETADATA").equals(myProp.get("DEFAULT"));
  }
  
  public static String getDefaultAmbient()
  {
    return MConnection.getMConnection("DEFAULT").getAmbient();
  }
  
  public static String getMetaAmbient() {
    return MConnection.getMConnection("JMETADATA").getAmbient();
  }
  
  private static String getMinKey() {
    Calendar rightNow = Calendar.getInstance();
    
    int wano = rightNow.get(1);
    int wmes = rightNow.get(2) + 1;
    int wdia = rightNow.get(5);
    
    int[] wprimos = { 1, 3, 5, 7, 11, 13, 17, 19, 23, 27, 29, 31 };
    
    int wnkey = wano * wmes * wdia * wprimos[(wmes - 1)];
    String wkey = String.valueOf(wnkey);
    
    return wkey;
  }
  
  public static String getUser() {
    return wuser;
  }
  
  public static String getUserNodo() {
    return wuserNodo;
  }
  
  public static String getUserAutLevel() {
    return wuserAutLevel;
  }
  
  public static String getUserTipo()
  {
    return wuserTipo;
  }
  

  public static String getUserMenu()
  {
    return wuserMenu;
  }
  
  public static String getUserOrg()
  {
    return wuserOrg;
  }
  
  public static String getLang() {
    return wlang;
  }
  
  public static String getMinTemplateDir() {
    if (myProp.containsKey("MINTEMPLATES")) {
      return (String)myProp.get("MINTEMPLATES");
    }
    return "Template";
  }
  
  public static String getChosenDSN()
  {
    if (myProp.containsKey("user.xdsn")) {
      return (String)myProp.get("user.xdsn");
    }
    return "";
  }
  
  public static String getCliTemplateDir()
  {
    if (myProp.containsKey("CLITEMPLATES")) {
      return (String)myProp.get("CLITEMPLATES");
    }
    return "Template";
  }
  
  public static String getLogoTemplateDir()
  {
    if (myProp.containsKey("LOGOTEMPLATES")) {
      return (String)myProp.get("LOGOTEMPLATES");
    }
    return "Template";
  }
  
  public static Properties getLoginProperties()
  {
    return myProp;
  }
  
  public static boolean hasLoginProperty(String wkey) {
    if (myProp == null) {
      return false;
    }
    if (myProp.containsKey(wkey)) {
      return true;
    }
    return false;
  }
  
  public static String getLoginProperty(String wkey)
  {
    if (hasLoginProperty(wkey)) {
      return (String)myProp.get(wkey);
    }
    return null;
  }
  
  public static void setLoginProperty(String wkey, String wval)
  {
    myProp.put(wkey, wval);
  }
  
  public static boolean isPrintPreviewOn() {
    return printPreview;
  }
  
  public static void setPrintPreview(boolean wboo) {
    printPreview = wboo;
  }
  
  public static boolean isLoginOk()
  {
    return logged;
  }
  
  public static boolean isMetaLoginOk() {
    return METAlogged;
  }
  
  public static boolean hasDBProperty(String wkey) {
    if (wdbprop == null) {
      return false;
    }
    if (wdbprop.containsKey(wkey)) {
      return true;
    }
    return false;
  }
  
  public static String getDBProperty(String wkey)
  {
    if (hasDBProperty(wkey)) {
      return (String)wdbprop.get(wkey);
    }
    return null;
  }
  
  public static boolean hasPropertiesForAtrib(String wkey)
  {
    if (watribprop == null) {
      return false;
    }
    if (watribprop.containsKey(wkey)) {
      return true;
    }
    return false;
  }
  
  public static Hashtable getPropertiesForAtrib(String wkey)
  {
    if (hasPropertiesForAtrib(wkey)) {
      return (Hashtable)watribprop.get(wkey);
    }
    return null;
  }
  
  public static void loadClientMeta()
  {
    MCursor suac_con = MConnection.getMCursor("DEFAULT");
    try {
      loadClientMeta(suac_con);
    } finally {
      suac_con.close();
    }
  }
  
  public static void clearClientMeta() {
    if (localMetaData == null) {
      localMetaData = new Hashtable();
    } else {
      localMetaData.clear();
    }
  }
  
  public static void loadClientMeta(MCursor suac_con)
  {
    if (localMetaData == null) {
      localMetaData = new Hashtable();
    } else {
      localMetaData.clear();
    }
    
    if (!isSameAsMetaData())
    {
      String wcsel = "select distinct 'MRFC'||i_ob as iob  from mrfct0  union  select distinct 'MEXT'||i_ob as iob  from mextt0  union  select distinct 'MJEX'||i_ob as iob  from mjext0  union  select distinct 'MEXS'||i_ob as iob  from mexst0  union  select distinct 'SMEN'||imen as iob  from sment0  where substr(imen,6,2) in ('BR', 'ME', 'MC')  union  select distinct 'MCFX'||i_ob as iob  from mcfxt0 ";
      


















      Vector wmetainf = null;
      if (!MSerializedObject.existsObject(MSerializedObject.LOCALSERPATH, "GLOBAL", "UNION", "MetaInf")) {
        try
        {
          suac_con.execQuery(wcsel);
          if (suac_con.getWdcode() == 0) {
            wmetainf = suac_con.getAllRecords();
          }
        } finally {
          suac_con.closeStatement();
        }
      }
      
      if (MSerializedObject.existsObject(MSerializedObject.LOCALSERPATH, "GLOBAL", "UNION", "MetaInf")) {
        wmetainf = (Vector)MSerializedObject.getObject(MSerializedObject.LOCALSERPATH, "GLOBAL", "UNION", "MetaInf");
      } else {
        MSerializedObject.saveObject(MSerializedObject.LOCALSERPATH, "GLOBAL", "UNION", "MetaInf", wmetainf);
      }
      for (int i = 0; (wmetainf != null) && (i < wmetainf.size()); i++) {
        Hashtable wh = (Hashtable)wmetainf.elementAt(i);
        localMetaData.put(wh.get("iob"), " ");
      }
    }
  }
  

  public static boolean existsClientMetaFor(String wiob)
  {
    if (localMetaData == null) {
      localMetaData = new Hashtable();
    }
    return localMetaData.containsKey(wiob);
  }
  
  public static void changePasswd(MFrame wf) throws MError {
    System.out.println("Ambiente " + getDefaultAmbient());
    if ((!getDefaultAmbient().equals("Oracle")) && (!getDefaultAmbient().equals("SQL Server")) && (!getDefaultAmbient().equals("PostgreSQL"))) {
      throw new MError("MIN-CPASS1E");
    }
    
    Properties p = myProp;
    Vector wv = new Vector();
    



    String wuser = (String)p.get("user.name");
    String wpasswd = (String)p.get("user.passwd");
    
    wv.addElement(new MLabel("Utilizador", true));
    MTextField wtf = new MTextField("user", 8, false);
    wtf.setText(wuser);
    wtf.setInternalWidth(8);
    wtf.setEditable(false);
    wtf.setEnabled(false);
    wv.addElement(wtf);
    
    wv.addElement(new MLabel("Password Actual", true));
    
    JPasswordField wpass = new JPasswordField(9);
    wpass.setName("passwd");
    wpass.setEchoChar('*');
    wpass.setFont(MTextField.DEFAULTFONT);
    wpass.setBackground(MTextField.DEFAULTCOLOR);
    wpass.setForeground(MTextField.DEFFONTCOLOR);
    wpass.setBorder(BorderFactory.createLoweredBevelBorder());
    wv.addElement(wpass);
    
    wv.addElement(new MLabel("Password Nova", true));
    
    JPasswordField wtt = new JPasswordField(9);
    wtt.setName("passwd_new1");
    wtt.setEchoChar('*');
    wtt.setFont(MTextField.DEFAULTFONT);
    wtt.setBackground(MTextField.DEFAULTCOLOR);
    wtt.setForeground(MTextField.DEFFONTCOLOR);
    wtt.setBorder(BorderFactory.createLoweredBevelBorder());
    wv.addElement(wtt);
    
    wv.addElement(new MLabel("Repita Password", true));
    
    wtt = new JPasswordField(9);
    wtt.setName("passwd_new2");
    wtt.setEchoChar('*');
    wtt.setFont(MTextField.DEFAULTFONT);
    wtt.setBackground(MTextField.DEFAULTCOLOR);
    wtt.setForeground(MTextField.DEFFONTCOLOR);
    wtt.setBorder(BorderFactory.createLoweredBevelBorder());
    wv.addElement(wtt);
    
    MDialog wd = new MDialog(wf, "Mudança Password", true, wv, 3, "cpasswd");
    wd.getForm().setEnt("CPASS");
    wd.pack();
    wd.setSize(getPreferredSizewidth, getPreferredSizeheight + 20);
    wd.setLocation(50, 50);
    
    MUtil.requestFocus(wpass);
    wd.setVisible(true);
    
    if (wd.getForm().getLastCmd().equals("CLOSEWINDOW")) {
      return;
    }
    
    MForm minForm = wd.getForm();
    
    Hashtable wh = MUtil.getRecord(minForm.getCenterPanel());
    
    String woldpass = (String)wh.get("passwd");
    String wpass_new1 = (String)wh.get("passwd_new1");
    String wpass_new2 = (String)wh.get("passwd_new2");
    
    if ((wpass_new1.trim().length() == 0) || (wpass_new2.trim().length() == 0)) {
      throw new MError("MIN-CPASS2E");
    }
    
    if (!wpass_new1.equals(wpass_new2)) {
      throw new MError("MIN-CPASS3E");
    }
    
    if (!woldpass.equals(wpasswd)) {
      throw new MError("MIN-CPASS4E");
    }
    
    String wcsel = "";
    if (getDefaultAmbient().equals("Oracle")) {
      wcsel = " alter user " + wuser + " identified by \"" + wpass_new1 + "\"";
    }
    if (getDefaultAmbient().equals("SQL Server")) {
      wcsel = " exec sp_password '" + woldpass + "' , '" + wpass_new1 + "'";
    }
    if (getDefaultAmbient().equals("PostgreSQL")) {
      wcsel = " alter user " + wuser + " password '" + wpass_new1 + "'";
    }
    MCursor wupdate = MConnection.getMCursor("DEFAULT");
    wupdate.execStatement(wcsel, false);
    wupdate.closeStatement();
    

    p.put("user.passwd", wpass_new1);
    
    throw new MError("MIN-CPASS0M");
  }
  
  public static String getLastModifiedForClassJar(Class wclass)
  {
    return getLastModifiedForResourceJar(wclass.getName().replace('.', '/') + ".class");
  }
  
  public static String getLastModifiedForResourceJar(String wresource) {
    String wlmodif = null;
    String wversion = (String)System.getProperties().get("java.version");
    if (!wversion.startsWith("1.1"))
    {
      myProp.getClass().getClassLoader();URL wresurl = ClassLoader.getSystemResource(wresource);
      
      if ((wresurl != null) && (wresurl.toString().startsWith("jar:"))) {
        try {
          JarURLConnection wurlconnect = (JarURLConnection)wresurl.openConnection();
          wurlconnect.connect();
          
          java.util.jar.JarFile wjar = wurlconnect.getJarFile();
          MFile wfile = new MFile(wjar.getName());
          






          if (wfile.canRead()) {
            java.util.Date wdate = new java.util.Date(wfile.lastModified());
            
            wlmodif = wdatef.format(wdate);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    
    return wlmodif;
  }
  
  public static void setInitialMenu(MMenu wmenu)
  {
    wmenu_inicial = wmenu;
  }
  
  public static MMenu getInitialMenu() {
    return wmenu_inicial;
  }
  
  public static void prepareLoginDialog(MFrame wf) throws MError
  {
    Properties p = myProp;
    Vector wv = new Vector();
    

    wv.addElement(new MLabel("Utilizador", true));
    MTextField wtf = new MTextField("user", 8, false);
    wtf.setText((String)p.get("user.name"));
    wtf.setInternalWidth(8);
    wv.addElement(wtf);
    wv.addElement(new MLabel("Password", true));
    JPasswordField wtt = new JPasswordField(9);
    wtt.setName("passwd");
    wtt.setEchoChar('*');
    wtt.setFont(MTextField.DEFAULTFONT);
    wtt.setBackground(MTextField.DEFAULTCOLOR);
    wtt.setForeground(MTextField.DEFFONTCOLOR);
    wtt.setBorder(BorderFactory.createLoweredBevelBorder());
    wv.addElement(wtt);
    

    wv.addElement(new MLabel("DSN", true));
    MChoice wch = new MChoice("dsn");
    wch.addDefaultNavigatorAgent();
    wch.addDefaultFocusListener();
    wch.setMaximumRowCount(3);
    Vector wvdsn = new Vector();
    for (Enumeration e = p.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (wkey.startsWith("wdsn")) {
        wvdsn.addElement((String)p.get(wkey));
      }
    }
    
    String wversion = (String)System.getProperties().get("java.version");
    
    if (!wversion.startsWith("1.1")) {
      java.util.Collections.sort(wvdsn);
    }
    
    for (int i = 0; i < wvdsn.size(); i++) {
      wch.addItem((String)wvdsn.elementAt(i));
    }
    wch.setEditable(true);
    
    wv.addElement(wch);
    MDialog wd = new MDialog(wf, "Menu Login", true, wv, 3, "login");
    wd.getForm().setEnt("Menu Login");
    wd.pack();
    wd.setSize(getPreferredSizewidth, getPreferredSizeheight + 20);
    wd.setLocation(30, 30);
  }
  

  private static void setGlobalVariables()
    throws MError
  {
    if (hasLoginProperty("SCHEMA")) {
      String wschema = getLoginProperty("SCHEMA").toUpperCase();
      if ((hasLoginProperty("SCHEMAFORCE")) && (getLoginProperty("SCHEMAFORCE").equals("S")) && (!getChosenDSN().startsWith(wschema))) {
        throw new MError("Utilizador Inválido.");
      }
    }
    
    String wdbtype = (String)wdbprop.get("DATABASE_TYPE");
    
    if (wdbtype == null) {
      wdbtype = "METADATA";
    }
    
    MPnuts mpnut = new MPnuts("LOGIN");
    if (mpnut.existsPnut("LOGIN", "MC", "VARIABLES", "FN")) {
      mpnut.evalPnut("LOGIN", "MC", "VARIABLES", "FN");
    } else {
      throw new MError("MIN-LOG081E");
    }
    

    if ((!isSameAsMetaData()) && (!wdbtype.equals("METADATA")))
    {

      MCursor default_con = MConnection.getMCursor("DEFAULT");
      if ((default_con.getAmbient().startsWith("Informix")) && ((!hasLoginProperty("JMETADATAMODE")) || (!getLoginProperty("JMETADATAMODE").equals("OFFLINE"))) && ((!hasLoginProperty("JMETADATAMODE")) || (!getLoginProperty("JMETADATAMODE").equals("METAFILE"))) && ((!hasLoginProperty("JMETADATAMODE")) || (!getLoginProperty("JMETADATAMODE").equals("METAFILEALL"))))
      {



        MCursor meta_con = MConnection.getMCursor("JMETADATA");
        try
        {
          String wcsel = "execute procedure scfgpgetgm(' ',' ',' ',' ',' ',' ',' ') ";
          
          default_con.execQuery(wcsel);
          if (default_con.getWdcode() == 0) {
            PT.Minimal.sql.MSqlProc.doStaticProc(meta_con.getMConnection(), "scfgpsetgm", default_con.getRecordAsVector());

          }
          


        }
        finally
        {

          default_con.close();
          meta_con.close();
        }
      }
    }
  }
}
