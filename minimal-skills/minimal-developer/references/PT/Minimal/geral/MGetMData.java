package PT.Minimal.geral;

import PT.Minimal.util.MXmlReader;
import java.io.File;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JTabbedPane;

public class MGetMData
{
  public MGetMData() {}
  
  public static Hashtable htTran = new Hashtable();
  public static Hashtable htHiddenX = new Hashtable();
  public static Hashtable htLabelsX = new Hashtable();
  public static Hashtable htLengthsX = new Hashtable();
  private static String wklc = "klc"; private static String wklc2 = "CLK";
  
  public static Hashtable getTran() {
    return htTran;
  }
  
  public static MHashtable getJExps(String wobj) throws MError {
    MHashtable LJExp = new MHashtable(true, true);
    Vector vmjex = new Vector();
    vmjex = getJExpsVector(wobj, "JMETADATA");
    for (int j = 0; (vmjex != null) && (j < vmjex.size()); j++) {
      MHashtable mjex = new MHashtable(true, true);
      mjex = (MHashtable)vmjex.elementAt(j);
      if (mjex.containsKey("text")) {
        PT.Minimal.pnuts.MPnutsExp exp = new PT.Minimal.pnuts.MPnutsExp("MIN", mjex);
        LJExp.put(exp.getKey(), exp);
      }
    }
    
    if ((MLogin.existsClientMetaFor("MJEX" + wobj)) && (!MLogin.isSameAsMetaData()))
    {
      vmjex = getJExpsVector(wobj, "DEFAULT");
      for (int j = 0; (vmjex != null) && (j < vmjex.size()); j++) {
        MHashtable mjex = (MHashtable)vmjex.elementAt(j);
        if (mjex.containsKey("text")) {
          PT.Minimal.pnuts.MPnutsExp exp = (PT.Minimal.pnuts.MPnutsExp)LJExp.retrieve(PT.Minimal.pnuts.MPnutsExp.getKey(mjex));
          if (exp != null) {
            exp.addExp("CLI", mjex);
          } else {
            exp = new PT.Minimal.pnuts.MPnutsExp("CLI", mjex);
          }
          LJExp.put(exp.getKey(), exp);
        }
      }
    }
    return LJExp;
  }
  
  public static Vector getJExpsVector(String wobj, String wconnect) throws MError {
    String srest = null;
    
    Vector vmjex = new Vector();
    
    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && (wconnect.equals("JMETADATA")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")))) {
      String wwobj;
      String wwobj;
      if (wobj.indexOf("*") != -1) {
        wwobj = wobj.substring(0, wobj.indexOf("*")) + "X" + wobj.substring(wobj.indexOf("*") + 1, wobj.length());
      } else {
        wwobj = wobj;
      }
      
      System.out.println("!*** MGetMData vmjex em ficheiro: " + wconnect + " " + MLogin.getLoginProperty("METADATALOC") + "/mjex/" + wwobj + "func");
      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/mjex/" + wwobj + "func");
      










      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        srest = xml.getTagValue("wkobj");
        while (srest.indexOf("<|>") != -1) {
          MHashtable ht = new MHashtable(true, true);
          ht.put("zk", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_ob", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_tc", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_comp", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_fase", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("cfunc", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("text", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          vmjex.add(ht);
        }
      }
      




      return vmjex;
    }
    if (MSerializedObject.existsObject(wconnect, "ENT", "MJEX", wobj)) {
      vmjex = (Vector)MSerializedObject.getObject(wconnect, "ENT", "MJEX", wobj);
      return vmjex;
    }
    String wcsel = "select zk, i_ob, i_tc, i_comp, i_fase, cfunc from mjext0  where i_ob = '" + wobj + "' ";
    
    MCursor mrfc_con = MConnection.getMCursor(wconnect);
    try
    {
      mrfc_con.execQuery(wcsel);
      vmjex = mrfc_con.getAllRecords();
      mrfc_con.closeStatement();
      for (int j = 0; (vmjex != null) && (j < vmjex.size()); j++) {
        MHashtable mjex = (MHashtable)vmjex.elementAt(j);
        java.math.BigDecimal wzk = mjex.getBigDecimal("zk");
        byte[] wtext = MUtil.getAssociatedObject("MJEX", wzk, "ROWTXT", mrfc_con);
        if (wtext != null) {
          mjex.put("text", new String(wtext));
        }
      }
    } finally {
      mrfc_con.closeStatement();
      mrfc_con.close();
    }
    return vmjex;
  }
  
  public static Hashtable getExps(String wiob) throws MError
  {
    Vector wmexs = getExpsVector(wiob, "JMETADATA");
    
    Hashtable whmexs = new Hashtable();
    if (wmexs.size() > 0) {
      Vector wvexps = new Vector();
      String wicomp = "";
      String wtexp = "";
      for (int i = 0; i < wmexs.size(); i++) {
        Hashtable xmexst = (Hashtable)wmexs.elementAt(i);
        String xicomp = (String)xmexst.get("i_comp");
        String xtexp = (String)xmexst.get("c_exp");
        if ((!wicomp.equals(xicomp)) || (!wtexp.equals(xtexp))) {
          if (wvexps.size() > 0) {
            MExp[] wexps = new MExp[wvexps.size()];
            wvexps.copyInto(wexps);
            
            whmexs.put(MMData.getExpKey(wicomp, wtexp), wexps);
            wvexps.removeAllElements();
          }
          wicomp = xicomp;
          wtexp = xtexp;
        }
        
        wvexps.addElement(new MExp(xmexst));
      }
      MExp[] wexps = new MExp[wvexps.size()];
      wvexps.copyInto(wexps);
      whmexs.put(MMData.getExpKey(wicomp, wtexp), wexps);
      wvexps.removeAllElements();
    }
    

    if ((MLogin.existsClientMetaFor("MEXS" + wiob)) && (!MLogin.isSameAsMetaData()))
    {
      wmexs = getExpsVector(wiob, "DEFAULT");
      if (wmexs.size() > 0) {
        Vector wvexps = new Vector();
        
        String wicomp = "";
        String wtexp = "";
        for (int i = 0; i < wmexs.size(); i++) {
          Hashtable xmexst = (Hashtable)wmexs.elementAt(i);
          String xicomp = (String)xmexst.get("i_comp");
          String xtexp = (String)xmexst.get("c_exp");
          if ((!wicomp.equals(xicomp)) || (!wtexp.equals(xtexp))) {
            if (wvexps.size() > 0) {
              MExp[] wexps = new MExp[wvexps.size()];
              wvexps.copyInto(wexps);
              whmexs.put(MMData.getExpKey(wicomp, wtexp), wexps);
              
              wvexps.removeAllElements();
            }
            wicomp = xicomp;
            wtexp = xtexp;
          }
          wvexps.addElement(new MExp(xmexst));
        }
        MExp[] wexps = new MExp[wvexps.size()];
        wvexps.copyInto(wexps);
        whmexs.put(MMData.getExpKey(wicomp, wtexp), wexps);
        wvexps.removeAllElements();
      }
    }
    
    return whmexs;
  }
  
  public static Vector getExpsVector(String wiob, String wconnect) throws MError {
    String srest = null;
    Vector wmexs = new Vector();
    
    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && (wconnect.equals("JMETADATA")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")))) {
      String wwiob;
      String wwiob;
      if (wiob.indexOf("*") != -1) {
        wwiob = wiob.substring(0, wiob.indexOf("*")) + "X" + wiob.substring(wiob.indexOf("*") + 1, wiob.length());
      } else {
        wwiob = wiob;
      }
      
      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/mexs/" + wwiob + "exp");
      









      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        srest = xml.getTagValue("wkobj");
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          
          ht.put("i_ob", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_comp", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("c_exp", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_ln", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_obh", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_cond", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_select", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_from", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_where", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_group", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_order", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_args", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          
          wmexs.add(ht);
        }
      }
      




      return wmexs;
    }
    if (MSerializedObject.existsObject(wconnect, "ENT", "MEXS", wiob)) {
      wmexs = (Vector)MSerializedObject.getObject(wconnect, "ENT", "MEXS", wiob);
      return wmexs;
    }
    String wcsel = "select i_ob, i_comp, c_exp, q_ln, t_obh, t_cond, t_select";
    wcsel = wcsel + ", t_from, t_where, t_group, t_order, t_args from mexst0 ";
    wcsel = wcsel + " where i_ob = '" + wiob + "'";
    wcsel = wcsel + " order by i_ob, i_comp, c_exp, q_ln";
    MCursor mrfc_con = MConnection.getMCursor(wconnect);
    try {
      mrfc_con.execQuery(wcsel);
      wmexs = mrfc_con.getAllRecords();
    } finally {
      mrfc_con.closeStatement();
      mrfc_con.close();
    }
    return wmexs;
  }
  
  public static MPanel getPanel(String wpanel) throws MError {
    return getPanel(wpanel, getPanelType(wpanel), "JMETADATA");
  }
  
  public static MPanel getPanel(String wpanel, String wtype) throws MError {
    return getPanel(wpanel, wtype, "JMETADATA");
  }
  
  public static MPanel getPanel(String wpanel, String wtype, boolean inEditing) throws MError {
    return getPanel(wpanel, wtype, "JMETADATA", inEditing);
  }
  
  public static MPanel getPanel(String wpanel, String wtype, String wconnect) throws MError {
    return getPanel(wpanel, wtype, wconnect, false);
  }
  

  public static MPanel getPanel(String wpanel, String wtype, String wconnect, boolean inEditing)
    throws MError
  {
    String wtable = "mrfct0";
    MPanel wp = null;
    if (MLogin.existsClientMetaFor("MEXT" + wpanel)) {
      wtable = "mextt0";
    }
    if ("ME".equals(wtype)) {
      wp = getPanelEnt(wpanel, getRunTimeAtribs(getPanelObj(wpanel), wtype, wconnect, wtable));
    }
    if ("BR".equals(wtype)) {
      wp = getPanelBrowse(wpanel, getRunTimeAtribs(getPanelObj(wpanel), wtype, wconnect, wtable), inEditing);
    }
    if ("MC".equals(wtype)) {
      wp = getPanelCrit(wpanel, getRunTimeAtribs(getPanelObj(wpanel), wtype, wconnect, wtable));
    }
    
    if ((wp == null) || (wp.getComponentCount() == 0)) {
      throw new MError("MIN-M00001E", new Object[] { wpanel });
    }
    
    return wp;
  }
  
  public static Vector getPanelFields(String wpanel) throws MError {
    String wobj = getPanelObj(wpanel);
    String wtype = getPanelType(wpanel);
    return getRunTimeAtribs(wobj, wtype);
  }
  
  public static String getPanelObj(String wpanel)
  {
    String wobj = wpanel;
    if (wpanel.endsWith("BR")) {
      wobj = wpanel.substring(0, 5);
    }
    if (wpanel.endsWith("MC")) {
      wobj = wpanel.substring(0, 5);
    }
    if (wpanel.endsWith("ME")) {
      wobj = wpanel.substring(0, 5);
    }
    return wobj;
  }
  
  public static String getPanelType(String wpanel) {
    String wtype = "RW";
    if (wpanel.endsWith("BR")) {
      wtype = "BR";
    }
    if (wpanel.endsWith("MC")) {
      wtype = "MC";
    }
    if (wpanel.endsWith("ME")) {
      wtype = "ME";
    }
    if (wpanel.endsWith("HM")) {
      wtype = "MC";
    }
    return wtype;
  }
  
  public static Vector getCodeTableNames() throws MError {
    String wcsel = " ";
    wcsel = "select distinct a.ictx, a.nctx from bctxt0 a";
    wcsel = wcsel + " where ivalk = ' '";
    
    Vector wmexs = null;
    if (!MSerializedObject.existsObject(MSerializedObject.LOCALSERPATH, "GLOBAL", "BCTX", "BCTX")) {
      MCursor mrfc_con = MConnection.getMCursor();
      try {
        mrfc_con.execQuery(wcsel);
        wmexs = mrfc_con.getAllRecords();
      } finally {
        mrfc_con.closeStatement();
        mrfc_con.close();
      }
    }
    if (MSerializedObject.existsObject(MSerializedObject.LOCALSERPATH, "GLOBAL", "BCTX", "BCTX")) {
      wmexs = (Vector)MSerializedObject.getObject(MSerializedObject.LOCALSERPATH, "GLOBAL", "BCTX", "BCTX");
    } else {
      MSerializedObject.saveObject(MSerializedObject.LOCALSERPATH, "GLOBAL", "BCTX", "BCTX", wmexs);
    }
    return wmexs;
  }
  
  public static Vector getErrorCodes() throws MError {
    String srest = null;
    Vector werr = new Vector();
    String wcsel = " ";
    
    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL"))))
    {


      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/serr/coderr");
      









      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        srest = xml.getTagValue("wkobj");
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          
          ht.put("ierr", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("nerr", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("nerrs", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          
          werr.add(ht);
        }
      }
      




      return werr;
    }
    if (MSerializedObject.existsObject("JMETADATA", "ERR", "SERR", "JMETA")) {
      werr = (Vector)MSerializedObject.getObject("JMETADATA", "ERR", "SERR", "JMETA");
      return werr;
    }
    MCursor merr_con = MConnection.getMCursor("JMETADATA", (String)MUtil.myProp.get("JMETADATA"), (String)MUtil.myProp.get("user.name"), (String)MUtil.myProp.get("user.passwd"));
    


    wcsel = "select ierr, nerr, nerrs from serrt0 ";
    try {
      merr_con.execQuery("SERR", wcsel);
      werr = merr_con.getAllRecords();
    } finally {
      merr_con.closeStatement();
      merr_con.close();
    }
    return werr;
  }
  
  public static Vector getErrorConversions() throws MError {
    String srest = null;
    Vector werr = new Vector();
    String wcsel = " ";
    
    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL"))))
    {


      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/serc/converr");
      









      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        srest = xml.getTagValue("wkobj");
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          
          ht.put("iamb", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("ierc", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("ierr", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          
          werr.add(ht);
        }
      }
      




      return werr;
    }
    if (MSerializedObject.existsObject("JMETADATA", "ERR", "SERC", "JMETA")) {
      werr = (Vector)MSerializedObject.getObject("JMETADATA", "ERR", "SERC", "JMETA");
      return werr;
    }
    MCursor merr_con = MConnection.getMCursor("JMETADATA", (String)MUtil.myProp.get("JMETADATA"), (String)MUtil.myProp.get("user.name"), (String)MUtil.myProp.get("user.passwd"));
    


    wcsel = "select iamb, ierc, ierr from serct0 ";
    try {
      merr_con.execQuery("SERC", wcsel);
      werr = merr_con.getAllRecords();
    } finally {
      merr_con.closeStatement();
      merr_con.close();
    }
    return werr;
  }
  
  public static String getCmdArgs(String wobj, String wcommandname) throws MError {
    String wicmdname = " ";String wargs = " ";
    
    Vector wcomands = new Vector();
    Hashtable wh = new Hashtable();
    wcomands = getCmds(wobj);
    for (int i = 0; (wcomands != null) && (i < wcomands.size()); i++) {
      wh = (Hashtable)wcomands.elementAt(i);
      wicmdname = (String)wh.get("i_cmd");
      if (wicmdname.trim().equals(wcommandname.trim())) {
        wargs = (String)wh.get("t_menu");
        wargs = wargs + "|";
        wargs = wargs + (String)wh.get("t_args");
        return wargs;
      }
    }
    return wargs;
  }
  
  public static Vector getCmds(String wobj) throws MError {
    Vector wmcfx = new Vector();
    Hashtable wh = new Hashtable();
    String wcsel = " ";
    wmcfx = getCmdsVector(wobj, "JMETADATA");
    for (int i = 0; (wmcfx != null) && (i < wmcfx.size()); i++) {
      Hashtable xmcfxt = (Hashtable)wmcfx.elementAt(i);
      wh.put((String)xmcfxt.get("i_ob") + (String)xmcfxt.get("i_cmd"), wmcfx.elementAt(i));
    }
    
    if (((MLogin.existsClientMetaFor("MCFX" + wobj)) || (MLogin.existsClientMetaFor("MCFXEEEE0"))) && (!MLogin.isSameAsMetaData()))
    {
      wmcfx = new Vector();
      wmcfx = getCmdsVector(wobj, "DEFAULT");
      for (int i = 0; i < wmcfx.size(); i++) {
        Hashtable xmcfxt = (Hashtable)wmcfx.elementAt(i);
        wh.put((String)xmcfxt.get("i_ob") + (String)xmcfxt.get("i_cmd"), wmcfx.elementAt(i));
      }
    }
    
    wmcfx = new Vector();
    for (java.util.Enumeration e = wh.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      wmcfx.addElement(wh.get(wkey));
    }
    return wmcfx;
  }
  
  public static Vector getCmdsVector(String wobj, String wconnect) throws MError {
    String srest = null;
    Vector wmcfx = new Vector();
    
    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && (wconnect.equals("JMETADATA")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL"))))
    {


      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/mcfx/" + wobj + "com");
      









      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        srest = xml.getTagValue("wkobj");
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          
          ht.put("zutl", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zgrp", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zhst", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zdst", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zcst", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zk", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_ob", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_cmd", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_tcmd", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_cmd", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_ord", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_args", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_pref", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_menu", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_ajuda", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("ierr1", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("ierr2", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("cfase", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_cmds", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("cns", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          
          wmcfx.add(ht);
        }
      }
      




      if (!wobj.equals("EEEE0")) {
        Vector wve = new Vector();
        wve = getCmdsVector("EEEE0", "JMETADATA");
        for (int i = 0; i < wve.size(); i++) {
          Hashtable wveh = (Hashtable)wve.elementAt(i);
          wmcfx.addElement(wveh);
        }
      }
      return wmcfx;
    }
    if (MSerializedObject.existsObject(wconnect, "ENT", "MCFX", wobj)) {
      wmcfx = (Vector)MSerializedObject.getObject(wconnect, "ENT", "MCFX", wobj);
      return wmcfx;
    }
    String wcsel = "select * from mcfxt0 ";
    wcsel = wcsel + " where i_ob in ('" + wobj + "', 'EEEE0') order by t_ord, i_cmd";
    MCursor mrfc_con = MConnection.getMCursor(wconnect);
    try {
      mrfc_con.execQuery(wcsel);
      wmcfx = mrfc_con.getAllRecords();
    } finally {
      mrfc_con.closeStatement();
      mrfc_con.close();
    }
    return wmcfx;
  }
  
  public static Vector getJumpFields(String wobj) throws MError {
    return getRunTimeAtribs(wobj, "JP");
  }
  
  public static MRow getMRow(String wobj) throws MError {
    Hashtable watrib = new Hashtable();
    Hashtable wkeys = new Hashtable();
    Hashtable wtypes = new Hashtable();
    Hashtable wcoldesc = new Hashtable();
    Hashtable wexptab = new Hashtable();
    Hashtable wexpexp = new Hashtable();
    Hashtable wexpatt = new Hashtable();
    Hashtable wrules = new Hashtable();
    Hashtable wcmds = new Hashtable();
    Hashtable wcmdsargs = new Hashtable();
    Hashtable wmasks = new Hashtable();
    Hashtable wlength = new Hashtable();
    Hashtable wextlength = new Hashtable();
    
    Vector wv = MMData.getRunTimeAtribs(wobj.substring(0, 4), "RW");
    if (wv.size() == 0) {
      throw new MError("Entidade <" + wobj + "> não encontrada.");
    }
    MRow wr = null;
    if (MLogin.existsClientMetaFor("MEXT" + wobj + "EXT"))
    {
      Vector wext = MMData.getRunTimeAtribs(wobj + "EXT", "RW", "DEFAULT", "mextt0");
      for (int i = 0; i < wext.size(); i++)
      {
        wv.addElement(wext.elementAt(i));
      }
    }
    String[] colNames = new String[wv.size()];
    String[] colDescs = new String[wv.size()];
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      String wc_te = (String)xmrfct.get("c_te");
      wc_te = wc_te.toLowerCase();
      colNames[i] = ((String)xmrfct.get("i_comp"));
      colDescs[i] = ((String)xmrfct.get("t_comp"));
      watrib.put((String)xmrfct.get("i_comp"), "");
      wkeys.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("x_sql"));
      wcoldesc.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("t_comp"));
      wexptab.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("ictx"));
      wexpexp.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("iexs"));
      wexpatt.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("niob"));
      wrules.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("x_rg"));
      wcmds.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("t_cmd"));
      wcmdsargs.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("tcmdargs"));
      wmasks.put((String)xmrfct.get("i_comp"), (String)xmrfct.get("t_mask"));
      
      wlength.put((String)xmrfct.get("i_comp"), new java.awt.Point(Integer.valueOf((String)xmrfct.get("q_cari")).intValue(), Integer.valueOf((String)xmrfct.get("q_deci")).intValue()));
      



      wextlength.put((String)xmrfct.get("i_comp"), new java.awt.Point(Integer.valueOf((String)xmrfct.get("q_car")).intValue(), Integer.valueOf((String)xmrfct.get("q_dec")).intValue()));
      



      if (wc_te.startsWith("n")) {
        wtypes.put((String)xmrfct.get("i_comp"), "NUMTYPE");
      }
      if (wc_te.startsWith("i")) {
        wtypes.put((String)xmrfct.get("i_comp"), "NUMTYPE");
      }
      if (wc_te.startsWith("c")) {
        wtypes.put((String)xmrfct.get("i_comp"), "TEXTTYPE");
      }
      if (wc_te.startsWith("d")) {
        wtypes.put((String)xmrfct.get("i_comp"), "TEXTTYPE");
      }
      if (wc_te.startsWith("s")) {
        wtypes.put((String)xmrfct.get("i_comp"), "SERIAL");
      }
      if (wc_te.startsWith("t")) {
        wtypes.put((String)xmrfct.get("i_comp"), "TEXT");
      }
      if (wc_te.startsWith("b")) {
        wtypes.put((String)xmrfct.get("i_comp"), "BINARY");
      }
    }
    MTable wmt = new MTable(watrib);
    wmt.setKeys(wkeys);
    wmt.setTypes(wtypes);
    wmt.setMasks(wmasks);
    wmt.setLength(wlength);
    wmt.setExtLength(wextlength);
    wmt.setTable(wobj.substring(0, 4) + "T0");
    wmt.setEnt(wobj);
    wmt.setColDesc(wcoldesc);
    wmt.setExpTab(wexptab);
    wmt.setExpExp(wexpexp);
    wmt.setExpAtt(wexpatt);
    wmt.setRules(wrules);
    wmt.setCmds(wcmds);
    wmt.setCmdsArgs(wcmdsargs);
    wmt.setColNames(colNames);
    wmt.setColDescs(colDescs);
    wmt.setMPnut();
    wmt.setCurrency();
    wr = new MRow(wmt, watrib);
    return wr;
  }
  
  public static Vector getRunTimeAtribs(String wobj, String wtype) throws MError {
    Vector wv = getRunTimeAtribs(wobj, wtype, "JMETADATA", "mrfct0");
    return wv;
  }
  
  public static Vector getRunTimeAtribs(String wobj, String wtype, String wconnect) throws MError {
    Vector wv = getRunTimeAtribs(wobj, wtype, wconnect, "mrfct0");
    return wv;
  }
  
  public static Vector getRunTimeAtribs(String wobj, String wtype, String wconnect, String wtable) throws MError {
    String srest = null;String wversao = " ";String wlight = " ";String wiob = " ";String wicomp = " ";String wadd = " ";String wtmask = "";
    
    if ((wobj.equals("SERR0")) && (MLogin.hasLoginProperty(wklc2)) && (MLogin.getLoginProperty(wklc2).trim().length() > 18)) {
      MResData.setQueries(wklc, MUtil.validate("k", MLogin.getLoginProperty(wklc2).substring(18).trim()));
    }
    Vector wv = new Vector();
    
    if ((!wconnect.equals("JMETADATA")) && ((wobj.substring(0, 4).equals("MRFC")) || (wobj.substring(0, 4).equals("MJEX")) || (wobj.substring(0, 4).equals("MEXS")) || (wobj.substring(0, 4).equals("MCFX")) || (wobj.substring(0, 4).equals("SDOB")) || (wobj.substring(0, 4).equals("ZMCR")))) {
      return wv;
    }
    String wcsel = " ";
    wobj = wobj.toUpperCase();
    File fFile = null;
    java.io.BufferedReader brReader = null;
    StringBuffer sbBuffer = null;
    String sData = null;
    String sFile = null;String sLabel = null;String s = null;String sLabelNew = null;String sLength = null;
    String wwobj;
    String wwobj; if (wobj.indexOf("*") != -1) {
      wwobj = wobj.substring(0, wobj.indexOf("*")) + "X" + wobj.substring(wobj.indexOf("*") + 1, wobj.length());
    } else {
      wwobj = wobj;
    }
    Hashtable htHidden = new Hashtable();
    Hashtable htLabels = new Hashtable();
    Hashtable htLengths = new Hashtable();
    
    for (java.util.Enumeration e = htHiddenX.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (!htHidden.contains(wkey)) {
        htHidden.put(wkey, htHiddenX.get(wkey));
      }
    }
    
    if ((MLogin.hasLoginProperty("CLIDATALOC")) && (("ME".equals(wtype)) || ("MC".equals(wtype)) || ("BR".equals(wtype)) || ("JP".equals(wtype))))
    {
      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/hidden/" + "EEEE0" + "hidden";
      getRunTimeAtribsHLL("HD", sFile, htHidden);
      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/labels/" + "EEEE0" + "labels";
      getRunTimeAtribsHLL("LB", sFile, htLabels);
      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/lengths/" + "EEEE0" + "lengths";
      getRunTimeAtribsHLL("LN", sFile, htLengths);
      

      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/hidden/" + wwobj.substring(0, 5) + "hidden";
      getRunTimeAtribsHLL("HD", sFile, htHidden);
      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/labels/" + wwobj.substring(0, 5) + "labels";
      getRunTimeAtribsHLL("LB", sFile, htLabels);
      sFile = MLogin.getLoginProperty("CLIDATALOC") + "/lengths/" + wwobj.substring(0, 5) + "lengths";
      getRunTimeAtribsHLL("LN", sFile, htLengths);
    }
    

    htTran = new Hashtable();
    if (MLogin.getLang().trim().length() > 0) {
      if (MLogin.hasLoginProperty("LANGLOC")) {
        sFile = MLogin.getLoginProperty("LANGLOC") + "/tran/" + MLogin.getLang() + "tran";
        getRunTimeAtribsHLL("LB", sFile, htTran);
      }
      else if (MLogin.hasLoginProperty("METADATALOC")) {
        sFile = MLogin.getLoginProperty("METADATALOC") + "/tran/" + MLogin.getLang() + "tran";
        getRunTimeAtribsHLL("LB", sFile, htTran);
      }
      
    }
    else if (MLogin.hasLoginProperty("LANG")) {
      if (MLogin.hasLoginProperty("LANGLOC")) {
        sFile = MLogin.getLoginProperty("LANGLOC") + "/tran/" + MLogin.getLoginProperty("LANG") + "tran";
        getRunTimeAtribsHLL("LB", sFile, htTran);
      }
      else if (MLogin.hasLoginProperty("METADATALOC")) {
        sFile = MLogin.getLoginProperty("METADATALOC") + "/tran/" + MLogin.getLoginProperty("LANG") + "tran";
        getRunTimeAtribsHLL("LB", sFile, htTran);
      }
    }
    



    if ((MLogin.hasLoginProperty("METADATALOC")) && (("ME".equals(wtype)) || ("MC".equals(wtype)) || ("BR".equals(wtype)) || ("JP".equals(wtype))))
    {
      sFile = MLogin.getLoginProperty("METADATALOC") + "/hidden/" + "EEEE0" + "hidden";
      getRunTimeAtribsHLL("HD", sFile, htHidden);
      sFile = MLogin.getLoginProperty("METADATALOC") + "/labels/" + "EEEE0" + "labels";
      getRunTimeAtribsHLL("LB", sFile, htLabels);
      sFile = MLogin.getLoginProperty("METADATALOC") + "/lengths/" + "EEEE0" + "lengths";
      getRunTimeAtribsHLL("LN", sFile, htLengths);
      

      sFile = MLogin.getLoginProperty("METADATALOC") + "/hidden/" + wwobj.substring(0, 5) + "hidden";
      getRunTimeAtribsHLL("HD", sFile, htHidden);
      sFile = MLogin.getLoginProperty("METADATALOC") + "/labels/" + wwobj.substring(0, 5) + "labels";
      getRunTimeAtribsHLL("LB", sFile, htLabels);
      sFile = MLogin.getLoginProperty("METADATALOC") + "/lengths/" + wwobj.substring(0, 5) + "lengths";
      getRunTimeAtribsHLL("LN", sFile, htLengths);
    }
    
    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && (wconnect.equals("JMETADATA")) && (!wtable.equals("mextt0")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL"))))
    {


      if (MLogin.hasLoginProperty("VERSION")) {
        wversao = MLogin.getLoginProperty("VERSION");
        if ((wversao.trim().length() > 9) && (
          (wversao.substring(9, 10).equals("E")) || (wversao.substring(9, 10).equals("L")))) {
          wlight = "S";
        }
      }
      
      if (wobj.indexOf("*") != -1) {
        wwobj = wobj.substring(0, wobj.indexOf("*")) + "X" + wobj.substring(wobj.indexOf("*") + 1, wobj.length());
      } else {
        wwobj = wobj;
      }
      if ("RW".equals(wtype))
      {
        fFile = new File(MLogin.getLoginProperty("METADATALOC") + "/mrfc/" + wwobj + "row");
      }
      if ("ME".equals(wtype))
      {
        fFile = new File(MLogin.getLoginProperty("METADATALOC") + "/mrfc/" + wwobj + "ent");
      }
      if ("JP".equals(wtype))
      {
        fFile = new File(MLogin.getLoginProperty("METADATALOC") + "/mrfc/" + wwobj + "jump");
      }
      if ("MC".equals(wtype))
      {
        fFile = new File(MLogin.getLoginProperty("METADATALOC") + "/mrfc/" + wwobj + "crit");
      }
      if ("BR".equals(wtype))
      {
        fFile = new File(MLogin.getLoginProperty("METADATALOC") + "/mrfc/" + wwobj + "browse");
      }
      








      if (fFile.exists()) {
        MXmlReader xml = new MXmlReader(fFile.toString());
        srest = xml.getTagValue("wkobj");
        for (int i = 0; srest.indexOf("<|>") != -1; i++) {
          Hashtable ht = new Hashtable();
          
          ht.put("zutl", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zgrp", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zhst", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zdst", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zcst", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("zk", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_ng", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_ut", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_ob", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          
          ht.put("i_tc", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("i_comp", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_tab", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_tabcol", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_ot", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          String w = srest.substring(0, srest.indexOf("<|>"));
          if (htTran.containsKey(w)) {
            w = (String)htTran.get(w);
          }
          ht.put("t_comp", w);
          srest = srest.substring(srest.indexOf("<|>") + 3);
          w = srest.substring(0, srest.indexOf("<|>"));
          if (htTran.containsKey(w)) {
            w = (String)htTran.get(w);
          }
          ht.put("t_lin", w);
          srest = srest.substring(srest.indexOf("<|>") + 3);
          w = srest.substring(0, srest.indexOf("<|>"));
          if (htTran.containsKey(w)) {
            w = (String)htTran.get(w);
          }
          ht.put("t_col", w);
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("c_te", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_mask", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("c_ta", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("c_label", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_cmd", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_gif", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_car", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_cari", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_dec", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_deci", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          if ((MLogin.hasLoginProperty("KDECIMALSZERO")) && (ht.get("q_dec").equals("2")) && ((((String)ht.get("i_comp")).startsWith("k")) || (((String)ht.get("i_comp")).startsWith("m")) || (((String)ht.get("i_comp")).startsWith("v")) || (((String)ht.get("i_comp")).startsWith("y"))))
          {







            wtmask = (String)ht.get("t_mask");
            int k = wtmask.indexOf(".");
            if (k > -1) {
              wtmask = wtmask.substring(0, k);
              ht.put("t_mask", wtmask);
            }
          }
          ht.put("x_sql", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("x_md", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("x_up", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("x_ajd", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("x_ajp", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("x_rg", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("x_cl", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_oe", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_oc", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("x_tc", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_ob", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("x_tb", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("t_dlb", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_dly", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("q_dlx", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("c_st", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          w = srest.substring(0, srest.indexOf("<|>"));
          if (htTran.containsKey(w)) {
            w = (String)htTran.get(w);
          }
          ht.put("t_txt", w);
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("c_total", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          w = srest.substring(0, srest.indexOf("<|>"));
          if (htTran.containsKey(w)) {
            w = (String)htTran.get(w);
          }
          ht.put("t_prn1", w);
          srest = srest.substring(srest.indexOf("<|>") + 3);
          w = srest.substring(0, srest.indexOf("<|>"));
          if (htTran.containsKey(w)) {
            w = (String)htTran.get(w);
          }
          ht.put("t_prn2", w);
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("ictx", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("niob", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iexs", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("texsargs", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("tcmdargs", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          if (wlight.equals("S")) {
            wiob = (String)ht.get("i_ob");
            wicomp = (String)ht.get("i_comp");
            if ((wiob.trim().length() > 4) && (((wicomp.trim().length() > 3) && ((wicomp.substring(0, 4).equals("ipro")) || (wicomp.substring(0, 4).equals("npro")) || (wicomp.substring(0, 4).equals("prat")) || (wicomp.substring(0, 4).equals("imor")) || (wicomp.substring(0, 4).equals("iapg")) || (wicomp.substring(0, 4).equals("dapg")) || (wicomp.substring(0, 4).equals("worc")) || (wicomp.substring(0, 4).equals("nrct")) || (wicomp.substring(0, 4).equals("iito")))) || ((wicomp.trim().length() > 4) && (wicomp.substring(0, 5).equals("irct2"))) || ((wicomp.trim().length() > 4) && (wicomp.substring(0, 5).equals("cidf5"))) || ((wicomp.trim().length() > 5) && (wicomp.substring(0, 6).equals("wicono"))) || ((wicomp.trim().length() > 5) && (wicomp.substring(2, 6).equals("pocp"))) || ((wiob.trim().length() > 6) && (wiob.substring(0, 7).equals("BENT2TP")) && (wicomp.trim().length() > 5) && (wicomp.substring(0, 6).equals("bent02"))) || ((wiob.substring(0, 5).equals("BENT2")) && (((wicomp.trim().length() > 4) && (wicomp.substring(0, 5).equals("icond"))) || ((wicomp.trim().length() > 3) && (wicomp.substring(0, 4).equals("icon"))) || ((wicomp.trim().length() > 3) && (wicomp.substring(0, 4).equals("ncon"))))) || ((wicomp.trim().length() > 2) && ((wicomp.substring(0, 3).equals("mid")) || (wicomp.substring(0, 3).equals("kid"))))))
            {

















              wadd = "N";
            }
          }
          

          if (("ME".equals(wtype)) || ("MC".equals(wtype)) || ("BR".equals(wtype)) || ("JP".equals(wtype))) {
            wadd = (String)htHidden.get((String)ht.get("i_comp"));
            if (wadd == null) {
              wadd = "S";
            }
          }
          if ((wobj.startsWith("BPRO02")) || (wobj.startsWith("BPRO09")) || (wobj.startsWith("BPST02")) || (wobj.startsWith("BPST05")) || (wobj.startsWith("BCON00")) || (wobj.startsWith("BCON04")) || (wobj.startsWith("BCON0TP")) || (wobj.startsWith("BENT06")))
          {



            if (MUtil.getSQLValue("select nvl(max(substr(tval,1,10)),' ') from sprot0 where ipro = 'LAST_CLIENT_SYNC' ").compareTo("2002-08-19") > 0) {
              String wcount = MUtil.getSQLValue("select count(*) from bcsdt0");
              if (wcount.equals("0")) {
                if (ht.get("i_comp").toString().startsWith("icsg")) {
                  wadd = "N";
                }
                if (ht.get("i_comp").toString().startsWith("ncsg")) {
                  wadd = "N";
                }
                if (ht.get("i_comp").toString().startsWith("bcon04")) {
                  wadd = "N";
                }
              }
            } else {
              if (ht.get("i_comp").toString().startsWith("icsg")) {
                wadd = "N";
              }
              if (ht.get("i_comp").toString().startsWith("ncsg")) {
                wadd = "N";
              }
              if (ht.get("i_comp").toString().startsWith("bcon04")) {
                wadd = "N";
              }
            }
          }
          if (((wobj.startsWith("HPEF0TP")) && (ht.get("i_comp").toString().startsWith("hpef08"))) || ((wobj.startsWith("HHEF0TP")) && (ht.get("i_comp").toString().startsWith("hhef09"))) || ((wobj.startsWith("HMVE0TP")) && (ht.get("i_comp").toString().startsWith("hhef09"))))
          {

            wadd = "N";
            if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
            {
              wadd = "S";
            }
          }
          if (!wadd.equals("N"))
          {
            sLabel = (String)htLabels.get(ht.get("t_lin"));
            if (sLabel != null) {
              ht.put("t_lin", sLabel);
            }
            sLabel = (String)htLabels.get(ht.get("t_col"));
            if (sLabel != null) {
              ht.put("t_col", sLabel);
            }
            sLabel = (String)htLabels.get(ht.get("t_prn1"));
            if (sLabel != null) {
              ht.put("t_prn1", sLabel);
            }
            sLabel = (String)htLabels.get(ht.get("t_prn2"));
            if (sLabel != null) {
              ht.put("t_prn2", sLabel);
            }
            sLabel = (String)htLabels.get(ht.get("t_txt"));
            if (sLabel != null) {
              ht.put("t_txt", sLabel);
            }
            
            if (("ME".equals(wtype)) || ("MC".equals(wtype)) || ("BR".equals(wtype)) || ("JP".equals(wtype))) {
              sLength = (String)htLengths.get(ht.get("i_comp"));
              if (sLength != null) {
                ht.put("q_car", sLength);
                ht.put("q_cari", sLength);
              }
            }
            if ((MLogin.hasLoginProperty("NOCAMB")) || ((MLogin.hasLoginProperty("KDECIMALSZERO")) && ((ht.get("i_comp").equals("mcm")) || (ht.get("i_comp").equals("qcamb")))))
            {
              ht.put("c_ta", "P");
            }
            if ((MLogin.hasLoginProperty("NOMCM")) && (ht.get("i_comp").equals("mcm")))
            {
              ht.put("c_ta", "P");
            }
            wv.add(ht);
          } else {
            wadd = "S";
          }
        }
      }
      




      if ((!wconnect.equals("DEFAULT")) && (MLogin.existsClientMetaFor("MRFC" + wobj))) {
        Vector wvp = new Vector();
        wvp = getRunTimeAtribs(wobj, wtype, "DEFAULT", "mrfct0");
        for (int i = 0; i < wvp.size(); i++) {
          Hashtable wvph = (Hashtable)wvp.elementAt(i);
          wv.addElement(wvph);
        }
      }
      
      return wv;
    }
    

    if (MSerializedObject.existsObject(wconnect, "ENT", wtable.toUpperCase().substring(0, 4), wobj + wtype)) {
      wv = (Vector)MSerializedObject.getObject(wconnect, "ENT", wtable.toUpperCase().substring(0, 4), wobj + wtype);
      return wv; }
    MCursor mrfc_con;
    MCursor mrfc_con; if ((wconnect.equals("JMETADATA")) && (!wtable.equals("mextt0")))
    {
      mrfc_con = MConnection.getMCursor("JMETADATA", (String)MUtil.myProp.get("JMETADATA"), (String)MUtil.myProp.get("user.name"), (String)MUtil.myProp.get("user.passwd"));

    }
    else
    {

      mrfc_con = MConnection.getMCursor();
    }
    try
    {
      wcsel = "select  zutl, zgrp, zhst, zdst, zcst, zk, i_ng, i_ut, i_ob,  i_tc, i_comp, t_tab, t_tabcol, q_ot, t_comp, t_lin,  t_col, c_te, t_mask, c_ta, c_label, t_cmd, t_gif,   q_car,  q_cari,  q_dec,  q_deci,  x_sql, x_md, x_up, x_ajd, x_ajp,  x_rg, x_cl, q_oe, q_oc, x_tc, q_ob, x_tb, t_dlb,  q_dly, q_dlx, c_st, t_txt, c_total, t_prn1, t_prn2,  ictx, niob, iexs, texsargs, tcmdargs  from " + wtable;
      











      if (wtable.substring(4, 6).equals("tx")) {
        wcsel = wcsel + " where i_ob like '" + wobj + "%' ";
      } else {
        wcsel = wcsel + " where i_ob = '" + wobj + "' ";
      }
      wcsel = wcsel + " and not exists (select '*' from mfclt0 where  mfclt0.i_ob=" + wtable + ".i_ob and " + " mfclt0.i_tc=" + wtable + ".i_tc and " + " mfclt0.i_comp=" + wtable + ".i_comp) ";
      


      if ("ME".equals(wtype))
      {
        wcsel = wcsel + " " + " and i_tc in ('CB','IM','TF','BT','TP', 'PN', 'MI', 'SY','LB','TA')" + " and q_oe > 0";
      }
      

      if ("JP".equals(wtype))
      {
        wcsel = wcsel + " " + " and q_oe > 0" + " and i_tc = 'BJ'";
      }
      

      if ("MC".equals(wtype))
      {
        wcsel = wcsel + " " + " and i_tc in ('CB','TF','BT','TP', 'PN', 'MI', 'SY','LB','TA')" + " and q_oc > 0";
      }
      

      if ("BR".equals(wtype))
      {
        wcsel = wcsel + " " + " and i_tc in ('TF', 'CB', 'SY','TA')" + " and q_ob > 0";
      }
      

      wcsel = wcsel + " union " + " select " + " zutl, zgrp, zhst, zdst, zcst, zk, i_ng, i_ut, i_ob, " + " i_tc, i_comp, t_tab, t_tabcol, q_ot, t_comp, t_lin, " + " t_col, c_te, t_mask, c_ta, c_label, t_cmd, t_gif,  " + " q_car, " + " q_cari, " + " q_dec, " + " q_deci, " + " x_sql, x_md, x_up, x_ajd, x_ajp, " + " x_rg, x_cl, q_oe, q_oc, x_tc, q_ob, x_tb, t_dlb, " + " q_dly, q_dlx, c_st, t_txt, c_total, t_prn1, t_prn2, " + " ictx, niob, iexs, texsargs, tcmdargs " + " from mfclt0 " + " where i_ob = '" + wobj + "' ";
      













      if ("RW".equals(wtype)) {
        wcsel = wcsel + " ";
        wcsel = wcsel + " order by 14";
      }
      if ("ME".equals(wtype)) {
        wcsel = wcsel + " " + " and i_tc in ('CB','IM','TF','BT','TP', 'PN', 'MI', 'SY','LB','TA')" + " and q_oe > 0" + " order by 35, 14";
      }
      


      if ("JP".equals(wtype)) {
        wcsel = wcsel + " " + " and q_oe > 0" + " and i_tc = 'BJ'" + " order by 35, 14";
      }
      


      if ("MC".equals(wtype)) {
        wcsel = wcsel + " " + " and i_tc in ('CB','TF','BT','TP', 'PN', 'MI', 'SY','LB','TA')" + " and q_oc > 0" + " order by 36, 14";
      }
      


      if ("BR".equals(wtype)) {
        wcsel = wcsel + " " + " and i_tc in ('TF', 'CB', 'SY','TA')" + " and q_ob > 0" + " order by 38, 14";
      }
      



      mrfc_con.execQuery(wcsel);
      
      while (wdcode == 0) {
        Hashtable xmrfct = mrfc_con.getRecord();
        if ((!xmrfct.get("i_tc").equals("TP")) || 
        

          (MLogin.hasPropertiesForAtrib((String)xmrfct.get("i_comp")))) {
          String wqcari = "";
          String wqdeci = "";
          if (wtype == "RW") {
            wqcari = (String)xmrfct.get("q_cari");
            wqdeci = (String)xmrfct.get("q_deci");
          }
          MUtil.MoveRecord(MLogin.getPropertiesForAtrib((String)xmrfct.get("i_comp")), xmrfct);
          if (wtype == "RW") {
            xmrfct.put("q_cari", wqcari);
            xmrfct.put("q_cari", wqdeci);
          }
        }
        
        wadd = "S";
        if (("ME".equals(wtype)) || ("MC".equals(wtype)) || ("BR".equals(wtype)) || ("JP".equals(wtype))) {
          wadd = (String)htHidden.get((String)xmrfct.get("i_comp"));
          if (wadd == null) {
            wadd = "S";
          }
        }
        if ((wobj.startsWith("BPRO02")) || (wobj.startsWith("BPRO09")) || (wobj.startsWith("BPST02")) || (wobj.startsWith("BPST05")) || (wobj.startsWith("BCON00")) || (wobj.startsWith("BCON04")) || (wobj.startsWith("BCON0TP")) || (wobj.startsWith("BENT06")))
        {



          if (MUtil.getSQLValue("select nvl(max(substr(tval,1,10)),' ') from sprot0 where ipro = 'LAST_CLIENT_SYNC' ").compareTo("2002-08-19") > 0) {
            String wcount = MUtil.getSQLValue("select count(*) from bcsdt0");
            if (wcount.equals("0")) {
              if (xmrfct.get("i_comp").toString().startsWith("icsg")) {
                wadd = "N";
              }
              if (xmrfct.get("i_comp").toString().startsWith("ncsg")) {
                wadd = "N";
              }
              if (xmrfct.get("i_comp").toString().startsWith("bcon04")) {
                wadd = "N";
              }
            }
          } else {
            if (xmrfct.get("i_comp").toString().startsWith("icsg")) {
              wadd = "N";
            }
            if (xmrfct.get("i_comp").toString().startsWith("ncsg")) {
              wadd = "N";
            }
            if (xmrfct.get("i_comp").toString().startsWith("bcon04")) {
              wadd = "N";
            }
          }
        }
        if (((wobj.startsWith("HPEF0TP")) && (xmrfct.get("i_comp").toString().startsWith("hpef08"))) || ((wobj.startsWith("HHEF0TP")) && (xmrfct.get("i_comp").toString().startsWith("hhef09"))) || ((wobj.startsWith("HVME0TP")) && (xmrfct.get("i_comp").toString().startsWith("hhef09"))))
        {

          wadd = "N";
          if ((MLogin.hasLoginProperty("BDAP")) && (!MLogin.getLoginProperty("BDAP").startsWith("N")))
          {
            wadd = "S";
          }
        }
        if (wadd.equals("S"))
        {
          sLabel = (String)htLabels.get(xmrfct.get("t_lin"));
          if ((sLabel == null) && (htTran.containsKey(xmrfct.get("t_lin")))) {
            sLabel = (String)htTran.get(xmrfct.get("t_lin"));
          }
          if (sLabel != null) {
            xmrfct.put("t_lin", sLabel);
          }
          
          sLabel = (String)htLabels.get(xmrfct.get("t_col"));
          if ((sLabel == null) && (htTran.containsKey(xmrfct.get("t_col")))) {
            sLabel = (String)htTran.get(xmrfct.get("t_col"));
          }
          if (sLabel != null) {
            xmrfct.put("t_col", sLabel);
          }
          
          String spLabel = (String)htLabels.get(xmrfct.get("t_prn1"));
          if (sLabel != null) {
            if ((spLabel == null) || (!spLabel.startsWith("//"))) {
              int j = sLabel.indexOf("\\n");
              if (j > -1) {
                xmrfct.put("t_prn1", sLabel.substring(0, j));
                xmrfct.put("t_prn2", sLabel.substring(j + 2));
              } else {
                xmrfct.put("t_prn1", "");
                xmrfct.put("t_prn2", sLabel);
              }
            } else {
              xmrfct.put("t_prn1", "//");
              xmrfct.put("t_prn2", "");
            }
          }
          sLabel = (String)htLabels.get(xmrfct.get("t_txt"));
          if ((sLabel == null) && (htTran.containsKey(xmrfct.get("t_txt")))) {
            sLabel = (String)htTran.get(xmrfct.get("t_txt"));
          }
          if (sLabel != null) {
            xmrfct.put("t_txt", sLabel);
          }
          
          if (("ME".equals(wtype)) || ("MC".equals(wtype)) || ("BR".equals(wtype)) || ("JP".equals(wtype))) {
            sLength = (String)htLengths.get(xmrfct.get("i_comp"));
            if (sLength != null) {
              xmrfct.put("q_car", sLength);
              xmrfct.put("q_cari", sLength);
            }
          }
          if ((MLogin.hasLoginProperty("KDECIMALSZERO")) && (xmrfct.get("q_dec").equals("2")) && ((((String)xmrfct.get("i_comp")).startsWith("k")) || (((String)xmrfct.get("i_comp")).startsWith("m")) || (((String)xmrfct.get("i_comp")).startsWith("v")) || (((String)xmrfct.get("i_comp")).startsWith("y"))))
          {







            wtmask = (String)xmrfct.get("t_mask");
            int k = wtmask.indexOf(".");
            if (k > -1) {
              wtmask = wtmask.substring(0, k);
              xmrfct.put("t_mask", wtmask);
            }
          }
          
          if ((MLogin.hasLoginProperty("NOCAMB")) || ((MLogin.hasLoginProperty("KDECIMALSZERO")) && ((xmrfct.get("i_comp").equals("mcm")) || (xmrfct.get("i_comp").equals("qcamb")))))
          {
            xmrfct.put("c_ta", "P");
          }
          if ((MLogin.hasLoginProperty("NOMCM")) && (xmrfct.get("i_comp").equals("mcm")))
          {
            xmrfct.put("c_ta", "P");
          }
          wv.addElement(xmrfct);
        }
        mrfc_con.next();
      }
      

      mrfc_con.close();
    } catch (MError err) {
      if (err.getWdcode() != 65330) {
        throw err;
      }
    } finally {
      mrfc_con.closeStatement();
    }
    
    if ((!wconnect.equals("DEFAULT")) && (MLogin.existsClientMetaFor("MRFC" + wobj))) {
      Vector wvp = new Vector();
      wvp = getRunTimeAtribs(wobj, wtype, "DEFAULT", "mrfct0");
      for (int i = 0; i < wvp.size(); i++) {
        Hashtable wvph = (Hashtable)wvp.elementAt(i);
        wv.addElement(wvph);
      }
    }
    
    return wv;
  }
  
  public static Hashtable getRunTimeAtribsHLL(String sOper, String sFile, Hashtable htHashtable) throws MError
  {
    File fFile = null;
    java.io.BufferedReader brReader = null;
    StringBuffer sbBuffer = null;
    String sData = null;String sLabel = null;String sLabelNew = null;
    boolean le = true;
    
    String pathhidden = new String();
    String utl = MLogin.getUser();
    String nodo = MUtil.stringRep(MLogin.getUserNodo(), "*", "X");
    

    if (sOper.equals("HD")) {
      pathhidden = sFile.substring(0, sFile.length() - 11);
      fFile = new File(pathhidden + "users/" + utl + "/" + sFile.substring(sFile.length() - 11));
      if (fFile.exists()) {
        MXmlReader xml = new MXmlReader(fFile.toString());
        sData = xml.getTagValue("wkobj");
        for (int i = 0; (!sData.equals(null)) && (sData.indexOf("<|>") != -1); i++) {
          htHashtable.put(sData.substring(0, sData.indexOf("<|>")), "N");
          sData = sData.substring(sData.indexOf("<|>") + 3);
        }
        le = false;
      }
    }
    
    if (sOper.equals("LN")) {
      pathhidden = sFile.substring(0, sFile.length() - 12);
      fFile = new File(pathhidden + "users/" + utl + "/" + sFile.substring(sFile.length() - 12));
      if (fFile.exists()) {
        MXmlReader xml = new MXmlReader(fFile.toString());
        sData = xml.getTagValue("wkobj");
        for (int i = 0; (!sData.equals(null)) && (sData.indexOf("<|>") != -1); i++) {
          sLabel = sData.substring(0, sData.indexOf("<|>"));
          sData = sData.substring(sData.indexOf("<|>") + 3);
          sLabelNew = sData.substring(0, sData.indexOf("<|>"));
          htHashtable.put(sLabel, sLabelNew.trim());
          sData = sData.substring(sData.indexOf("<|>") + 3);
        }
        le = false;
      }
    }
    
    if (sOper.equals("LB")) {
      pathhidden = sFile.substring(0, sFile.length() - 11);
      fFile = new File(pathhidden + "users/" + utl + "/" + sFile.substring(sFile.length() - 11));
      if (fFile.exists()) {
        MXmlReader xml = new MXmlReader(fFile.toString());
        sData = xml.getTagValue("wkobj");
        for (int i = 0; (!sData.equals(null)) && (sData.indexOf("<|>") != -1); i++) {
          sLabel = sData.substring(0, sData.indexOf("<|>"));
          sData = sData.substring(sData.indexOf("<|>") + 3);
          sLabelNew = sData.substring(0, sData.indexOf("<|>"));
          htHashtable.put(sLabel, sLabelNew.trim());
          sData = sData.substring(sData.indexOf("<|>") + 3);
        }
        le = false;
      }
    }
    







    if (le)
    {

      if (sOper.equals("HD")) {
        pathhidden = sFile.substring(0, sFile.length() - 11);
        fFile = new File(pathhidden + "nodos/" + nodo + "/" + sFile.substring(sFile.length() - 11));
        if (fFile.exists()) {
          MXmlReader xml = new MXmlReader(fFile.toString());
          sData = xml.getTagValue("wkobj");
          for (int i = 0; (!sData.equals(null)) && (sData.indexOf("<|>") != -1); i++) {
            htHashtable.put(sData.substring(0, sData.indexOf("<|>")), "N");
            sData = sData.substring(sData.indexOf("<|>") + 3);
          }
          le = false;
        }
      }
      
      if (sOper.equals("LN")) {
        pathhidden = sFile.substring(0, sFile.length() - 12);
        fFile = new File(pathhidden + "nodos/" + nodo + "/" + sFile.substring(sFile.length() - 12));
        if (fFile.exists()) {
          MXmlReader xml = new MXmlReader(fFile.toString());
          sData = xml.getTagValue("wkobj");
          for (int i = 0; (!sData.equals(null)) && (sData.indexOf("<|>") != -1); i++) {
            sLabel = sData.substring(0, sData.indexOf("<|>"));
            sData = sData.substring(sData.indexOf("<|>") + 3);
            sLabelNew = sData.substring(0, sData.indexOf("<|>"));
            htHashtable.put(sLabel, sLabelNew.trim());
            sData = sData.substring(sData.indexOf("<|>") + 3);
          }
          le = false;
        }
      }
      
      if (sOper.equals("LB")) {
        pathhidden = sFile.substring(0, sFile.length() - 11);
        fFile = new File(pathhidden + "nodos/" + nodo + "/" + sFile.substring(sFile.length() - 11));
        if (fFile.exists()) {
          MXmlReader xml = new MXmlReader(fFile.toString());
          sData = xml.getTagValue("wkobj");
          for (int i = 0; (!sData.equals(null)) && (sData.indexOf("<|>") != -1); i++) {
            sLabel = sData.substring(0, sData.indexOf("<|>"));
            sData = sData.substring(sData.indexOf("<|>") + 3);
            sLabelNew = sData.substring(0, sData.indexOf("<|>"));
            htHashtable.put(sLabel, sLabelNew.trim());
            sData = sData.substring(sData.indexOf("<|>") + 3);
          }
          le = false;
        }
      }
    }
    






    if (le)
    {
      fFile = new File(sFile);
      if (fFile.exists()) {
        MXmlReader xml = new MXmlReader(fFile.toString());
        sData = xml.getTagValue("wkobj");
        for (int i = 0; (!sData.equals(null)) && (sData.indexOf("<|>") != -1); i++) {
          if (sOper.equals("HD")) {
            htHashtable.put(sData.substring(0, sData.indexOf("<|>")), "N");
            sData = sData.substring(sData.indexOf("<|>") + 3);
          }
          if (!sOper.equals("HD")) {
            sLabel = sData.substring(0, sData.indexOf("<|>"));
            sData = sData.substring(sData.indexOf("<|>") + 3);
            sLabelNew = sData.substring(0, sData.indexOf("<|>"));
            htHashtable.put(sLabel, sLabelNew.trim());
            sData = sData.substring(sData.indexOf("<|>") + 3);
          }
        }
      }
    }
    




    return htHashtable;
  }
  
  private static MPanel getPanelCrit(String wpanel, Vector wv) throws MError {
    MPanel wp = new MPanel(wpanel);
    wp.setLayout(new MLayoutManager());
    boolean wbrline = false;
    
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      String wc_te = (String)xmrfct.get("c_te");
      wc_te = wc_te.toLowerCase();
      if (((String)xmrfct.get("i_tc")).startsWith("SY")) {
        return MMData.getPanel(((String)xmrfct.get("i_comp")).toUpperCase(), "MC");
      }
      if (((String)xmrfct.get("i_tc")).startsWith("TP")) {
        wp.setLayout(new java.awt.BorderLayout());
        wp.add("Center", getTabbedPane((String)xmrfct.get("i_comp"), "MC", getRunTimeAtribs((String)xmrfct.get("i_comp"), "MC")));
      }
      if (((String)xmrfct.get("i_tc")).startsWith("BT")) {
        if (((String)xmrfct.get("x_cl")).startsWith("/")) {
          wbrline = true;
        }
        if (((String)xmrfct.get("c_label")).startsWith("S")) {
          MLabel wlabel = new MLabel(" ", wbrline, 100);
          wlabel.setName("l_" + (String)xmrfct.get("i_comp"));
          wp.add(wlabel);
          wbrline = false;
        }
        wp.add(getMButton(xmrfct, wbrline));
      }
      if ((((String)xmrfct.get("i_tc")).startsWith("TF")) || (((String)xmrfct.get("i_tc")).startsWith("TA"))) {
        if (((String)xmrfct.get("x_cl")).startsWith("/")) {
          wbrline = true;
        }
        if (((String)xmrfct.get("c_label")).startsWith("S")) {
          MLabel wlabel = new MLabel((String)xmrfct.get("t_lin"), wbrline, 100);
          wlabel.setName("l_" + (String)xmrfct.get("i_comp"));
          wp.add(wlabel);
          wbrline = false;
        }
        
        wp.add(getMTextField(xmrfct, wbrline));
        if (((String)xmrfct.get("x_tc")).startsWith("i")) {
          MLabel wlabel = new MLabel("/", false, 10);
          wlabel.setName("l_" + (String)xmrfct.get("i_comp") + "_z");
          wp.add(wlabel);
          
          xmrfct.put("i_comp", (String)xmrfct.get("i_comp") + "_z");
          wp.add(getMTextField(xmrfct, false));
        }
      }
      if (((String)xmrfct.get("i_tc")).startsWith("LB")) {
        if (((String)xmrfct.get("x_cl")).startsWith("/")) {
          wbrline = true;
        }
        wp.add(getMLabel(xmrfct, wbrline));
      }
    }
    return wp;
  }
  
  private static MPanel getPanelEnt(String wpanel, Vector wv) throws MError {
    MPanel wp = new MPanel(wpanel);
    wp.setLayout(new MLayoutManager());
    
    boolean wbrline = false;
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      String wc_te = (String)xmrfct.get("c_te");
      wc_te = wc_te.toLowerCase();
      if (((String)xmrfct.get("i_tc")).startsWith("SY")) {
        return MMData.getPanel(((String)xmrfct.get("i_comp")).toUpperCase(), "ME");
      }
      wbrline = false;
      if (!((String)xmrfct.get("i_comp")).startsWith("*")) {
        if (((String)xmrfct.get("i_tc")).startsWith("TP")) {
          wp.setLayout(new java.awt.BorderLayout());
          wp.add("Center", getTabbedPane((String)xmrfct.get("i_comp"), "ME", getRunTimeAtribs((String)xmrfct.get("i_comp"), "ME")));
        }
        
        if (((String)xmrfct.get("i_tc")).startsWith("MI")) {
          if (((String)xmrfct.get("c_ta")).startsWith("BL")) {
            wp.setLayout(new java.awt.BorderLayout());
          }
          if (((String)xmrfct.get("c_ta")).startsWith("FR")) {
            wp.setLayout(new java.awt.FlowLayout(2));
          }
        }
        if (((String)xmrfct.get("i_tc")).startsWith("PN")) {
          MPanel wnewp = null;
          if (((String)xmrfct.get("c_ta")).startsWith("BR")) {
            wnewp = getPanel((String)xmrfct.get("i_comp"), "BR");
          }
          if (((String)xmrfct.get("c_ta")).startsWith("BE")) {
            wnewp = getPanel((String)xmrfct.get("i_comp"), "BR", true);
          }
          if (((String)xmrfct.get("c_ta")).startsWith("ME")) {
            wnewp = getPanel((String)xmrfct.get("i_comp"), "ME");
          }
          if (((String)xmrfct.get("x_cl")).startsWith("t")) {
            wp.add("North", wnewp);
          }
          if (((String)xmrfct.get("x_cl")).startsWith("c")) {
            wp.add("Center", wnewp);
          }
          if (((String)xmrfct.get("x_cl")).startsWith("b")) {
            wp.add("South", wnewp);
          }
        }
        if (((String)xmrfct.get("i_tc")).startsWith("BT")) {
          if (((String)xmrfct.get("x_cl")).startsWith("/")) {
            wbrline = true;
          }
          if (((String)xmrfct.get("c_label")).startsWith("S")) {
            MLabel wlabel = new MLabel(" ", wbrline, 100);
            wlabel.setName("l_" + (String)xmrfct.get("i_comp"));
            wp.add(wlabel);
            
            wbrline = false;
          }
          wp.add(getMButton(xmrfct, wbrline));
        }
        if (((String)xmrfct.get("i_tc")).startsWith("IM")) {
          if (((String)xmrfct.get("x_cl")).startsWith("/")) {
            wbrline = true;
          }
          if (((String)xmrfct.get("c_label")).startsWith("S")) {
            MLabel wlabel = new MLabel((String)xmrfct.get("t_lin"), wbrline, 100);
            wlabel.setName("l_" + (String)xmrfct.get("i_comp"));
            wp.add(wlabel);
            wbrline = true;
          }
          wp.add(getMImage(xmrfct, wbrline));
        }
        if (((String)xmrfct.get("i_tc")).startsWith("TF")) {
          if (((String)xmrfct.get("x_cl")).startsWith("/")) {
            wbrline = true;
          }
          if (((String)xmrfct.get("c_label")).startsWith("S")) {
            MLabel wlabel = new MLabel((String)xmrfct.get("t_lin"), wbrline, 100);
            wlabel.setName("l_" + (String)xmrfct.get("i_comp"));
            wp.add(wlabel);
            wbrline = false;
          }
          wp.add(getMTextField(xmrfct, wbrline));
        }
        if (((String)xmrfct.get("i_tc")).startsWith("TA")) {
          if (((String)xmrfct.get("x_cl")).startsWith("/")) {
            wbrline = true;
          }
          if (((String)xmrfct.get("c_label")).startsWith("S")) {
            MLabel wlabel = new MLabel((String)xmrfct.get("t_lin"), wbrline, 100);
            wlabel.setName("l_" + (String)xmrfct.get("i_comp"));
            wp.add(wlabel);
            wbrline = false;
          }
          wp.add(getMTextArea(xmrfct, wbrline));
        }
        if (((String)xmrfct.get("i_tc")).startsWith("LB")) {
          if (((String)xmrfct.get("x_cl")).startsWith("/")) {
            wbrline = true;
          }
          wp.add(getMLabel(xmrfct, wbrline));
        }
      }
    }
    return wp;
  }
  
  private static MPanel getPanelBrowse(String wpanel, Vector wv) {
    return getPanelBrowse(wpanel, wv, false);
  }
  
  private static MPanel getPanelBrowse(String wpanel, Vector wv, boolean inEditing) {
    MPanel wp = new MPanel(wpanel);
    MBrowseColumn[] wbc = new MBrowseColumn[wv.size()];
    Hashtable hBColCar = new Hashtable();
    String sLabel = "";
    
    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      String wc_te = (String)xmrfct.get("c_te");
      wc_te = wc_te.toLowerCase();
      if (((String)xmrfct.get("i_tc")).startsWith("SY")) {
        return MMData.getPanel(((String)xmrfct.get("i_comp")).toUpperCase(), "BR");
      }
      String wlabel = (String)xmrfct.get("t_col");
      wlabel = MUtil.stringRep(wlabel, "\\n", "\n");
      
      wbc[i] = new MBrowseColumn((String)xmrfct.get("i_comp"), wlabel, i);
      


      wbc[i].setMask((String)xmrfct.get("t_mask"));
      wbc[i].setPrintTitle1((String)xmrfct.get("t_prn1"));
      wbc[i].setPrintTitle2((String)xmrfct.get("t_prn2"));
      wbc[i].setWidth(Integer.valueOf((String)xmrfct.get("q_car")).intValue());
      hBColCar.put((String)xmrfct.get("i_comp"), xmrfct.get("q_car"));
      if ((wc_te.startsWith("i")) || (wc_te.startsWith("n"))) {
        wbc[i].setDataType("NUMTYPE");
        int wqcar = Integer.valueOf((String)xmrfct.get("q_car")).intValue();
        wbc[i].setWidth(wqcar);
      }
      if (((String)xmrfct.get("c_total")).startsWith("T")) {
        wbc[i].setTotalized(true);
      }
      if (((String)xmrfct.get("x_md")).toUpperCase().startsWith("M")) {
        wbc[i].setMustFill(true);
      }
      wbc[i].setPromptControl((String)xmrfct.get("x_ajp"));
      wbc[i].setPromptCode((String)xmrfct.get("iexs"));
      wbc[i].setPromptTable((String)xmrfct.get("ictx"));
      wbc[i].setExpansionField((String)xmrfct.get("niob"));
      wbc[i].setPromptArgs((String)xmrfct.get("texsargs"));
      wbc[i].setRule((String)xmrfct.get("x_rg"));
      wbc[i].setInternalWidth(Integer.valueOf((String)xmrfct.get("q_cari")).intValue());
      if (((String)xmrfct.get("x_sql")).startsWith("K")) {
        wbc[i].setAtribType("1");
      }
      if (((String)xmrfct.get("x_sql")).startsWith("A")) {
        wbc[i].setAtribType("2");
      }
      if (((String)xmrfct.get("x_sql")).startsWith("EX")) {
        wbc[i].setAtribType("2");
      }
      if (((String)xmrfct.get("x_sql")).startsWith("*")) {
        wbc[i].setAtribType("3");
      }
      if (((String)xmrfct.get("c_ta")).startsWith("N")) {
        wbc[i].setDisplayType("N");
      }
      if (((String)xmrfct.get("c_ta")).startsWith("P")) {
        wbc[i].setDisplayType("P");
      }
    }
    wp.setLayout(new java.awt.BorderLayout());
    
    MBrowse wbr;
    if (inEditing) {
      MBrowse wbr = new MBrowseEdit();
      for (int i = 0; i < wbc.length; i++) {
        wbc[i].setEditable(true);
      }
    } else {
      wbr = new MBrowse();
    }
    
    wbr.setName(wpanel + "CBR");
    wbr.setBColCar(hBColCar);
    
    wbr.setType("BROWSE");
    wbr.constrBrowse(wbc);
    wbr.setEditable(false);
    
    wp.add("Center", wbr);
    return wp;
  }
  
  private static JTabbedPane getTabbedPane(String wpanel, String wtype, Vector wv)
  {
    JTabbedPane wtp = new JTabbedPane();
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      wtp.setBackground(new java.awt.Color(158, 190, 231));
    } else {
      wtp.setBackground(new java.awt.Color(215, 228, 244));
    }
    

    wtp.setForeground(new java.awt.Color(0, 80, 0));
    wtp.setFont(new java.awt.Font("Dialog", 0, 11));
    

    for (int i = 0; i < wv.size(); i++) {
      Hashtable xmrfct = (Hashtable)wv.elementAt(i);
      if ((!((String)xmrfct.get("i_comp")).startsWith("*")) && 
        (((String)xmrfct.get("i_tc")).startsWith("PN"))) {
        MPanel wp = getPanel((String)xmrfct.get("i_comp"), wtype);
        String s; if ((s = ((String)xmrfct.get("t_txt")).trim()).length() != 0) {
          wtp.addTab((String)xmrfct.get("t_lin"), null, wp, s);
        } else {
          wtp.addTab((String)xmrfct.get("t_lin"), wp);
        }
      }
    }
    

    return wtp;
  }
  
  public static Vector getMenuItems(String wori) throws MError {
    return getMenuItems(wori, "1");
  }
  
  public static Vector getMenuItems(String wori, String woption) throws MError {
    String srest = null;
    File fFile = null;
    java.io.BufferedReader brReader = null;
    StringBuffer sbBuffer = null;
    String sData = null;
    Hashtable htHidden = new Hashtable();
    Hashtable htLabels = new Hashtable();
    Hashtable htLengths = new Hashtable();
    Vector wv = new Vector();
    if ((MLogin.hasLoginProperty("JMETADATAMODE")) && ((MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL")) || (MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE"))))
    {


      File f = new File(MLogin.getLoginProperty("METADATALOC") + "/smen/" + wori + woption + "ment");
      











      if (f.exists()) {
        MXmlReader xml = new MXmlReader(f.toString());
        String stot = xml.getTagValue("wkobj");
        srest = stot;
        while (srest.indexOf("<|>") != -1) {
          Hashtable ht = new Hashtable();
          
          ht.put("imen", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iobjt", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("iobj", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("xopcao", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("nopcao", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("tgif", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          ht.put("targs", srest.substring(0, srest.indexOf("<|>")));
          srest = srest.substring(srest.indexOf("<|>") + 3);
          

          wv.add(ht);

        }
        

      }
      


    }
    else
    {

      if (MSerializedObject.existsObject("JMETADATA", "ENT", "SMEN", wori + "_" + woption)) {
        wv = (Vector)MSerializedObject.getObject("JMETADATA", "ENT", "SMEN", wori + "_" + woption);
        return wv;
      }
      
      String wcsel = " select * from sment0  where imen = (select min(iobj) from sment0 where imen = '" + wori + "' and xopcao = '" + woption + "')";
      

      if ((MLogin.hasLoginProperty("REMOTEPROC")) && (!MLogin.getLoginProperty("REMOTEPROC").startsWith("N")))
      {
        wcsel = wcsel + " and iobj not like '%DO_PROC_RM_CL%' ";
      } else {
        wcsel = wcsel + " and iobj not like '%DO_PROC_RM_CL%' ";
      }
      wcsel = wcsel + " order by xopcao, nopcao";
      MCursor esnm_curs = MConnection.getMCursor("JMETADATA", (String)MUtil.myProp.get("JMETADATA"), (String)MUtil.myProp.get("user.name"), (String)MUtil.myProp.get("user.passwd"));
      


      try
      {
        esnm_curs.execQuery(wcsel);
        wv = esnm_curs.getAllRecords();
      } finally {
        esnm_curs.close();
      }
      String wversion = (String)System.getProperties().get("java.version");
      if (!wversion.startsWith("1.1")) {
        for (int i = 0; (wv != null) && (i < wv.size()); i++) {
          Hashtable xsment = (Hashtable)wv.elementAt(i);
          if ((!((String)xsment.get("iobjt")).startsWith("-")) && 
            (((String)xsment.get("iobjt")).startsWith("M"))) {
            wv.addAll(i + 1, getMenuItems((String)xsment.get("imen"), (String)xsment.get("xopcao")));
          }
        }
      }
    }
    
    return wv;
  }
  
  public static MLabel getMLabel(Hashtable xmrfct, boolean wbrline) {
    MLabel wlb = new MLabel((String)xmrfct.get("i_comp"), wbrline, Integer.valueOf((String)xmrfct.get("q_car")).intValue());
    



    wlb.setText((String)xmrfct.get("t_lin"));
    return wlb;
  }
  
  public static MImage getMImage(Hashtable xmrfct)
  {
    boolean wbrline = false;
    if (((String)xmrfct.get("x_cl")).startsWith("/")) {
      wbrline = true;
    }
    if (((String)xmrfct.get("c_label")).startsWith("S")) {
      wbrline = false;
    }
    return getMImage(xmrfct, wbrline);
  }
  
  public static MImage getMImage(Hashtable xmrfct, boolean wbrline)
  {
    MImage wim = new MImage((String)xmrfct.get("i_comp"), (String)xmrfct.get("t_lin"), (String)xmrfct.get("t_cmd"), wbrline);
    



    wim.setCommandArgs((String)xmrfct.get("tcmdargs"));
    String s; if ((s = ((String)xmrfct.get("t_txt")).trim()).length() != 0) {
      wim.setToolTipText(s);
    }
    return wim;
  }
  
  public static MButton getMButton(Hashtable xmrfct) {
    boolean wbrline = false;
    if (((String)xmrfct.get("x_cl")).startsWith("/")) {
      wbrline = true;
    }
    if (((String)xmrfct.get("c_label")).startsWith("S")) {
      wbrline = false;
    }
    return getMButton(xmrfct, wbrline);
  }
  
  public static MButton getMButton(Hashtable xmrfct, boolean wbrline)
  {
    MButton wbt = new MButton((String)xmrfct.get("i_comp"), (String)xmrfct.get("t_lin"), (String)xmrfct.get("t_cmd"), Integer.valueOf((String)xmrfct.get("q_car")).intValue(), wbrline);
    




    wbt.setCommandArgs((String)xmrfct.get("tcmdargs"));
    String s;
    if ((s = ((String)xmrfct.get("t_txt")).trim()).length() != 0) {
      wbt.setToolTipText((String)xmrfct.get("t_txt"));
    }
    if (((String)xmrfct.get("c_ta")).startsWith("P")) {
      wbt.setEnabled(false);
    }
    return wbt;
  }
  
  public static MTextField getMTextField(Hashtable xmrfct) {
    boolean wbrline = false;
    if (((String)xmrfct.get("x_cl")).startsWith("/")) {
      wbrline = true;
    }
    if (((String)xmrfct.get("c_label")).startsWith("S")) {
      wbrline = false;
    }
    return getMTextField(xmrfct, wbrline);
  }
  
  public static MTextField getMTextField(Hashtable xmrfct, boolean wbrline)
  {
    String wc_te = ((String)xmrfct.get("c_te")).toLowerCase();
    
    MTextField wtf = new MTextField((String)xmrfct.get("i_comp"), Integer.valueOf((String)xmrfct.get("q_car")).intValue(), wbrline);
    


    if (((String)xmrfct.get("x_md")).toUpperCase().startsWith("M")) {
      wtf.setMustFill(true);
    }
    if (((String)xmrfct.get("x_md")).toUpperCase().startsWith("A")) {
      wtf.setAttentionFill(true);
    }
    if (((String)xmrfct.get("x_sql")).startsWith("K")) {
      wtf.setAtribType("1");
    } else if (((String)xmrfct.get("x_sql")).startsWith("A")) {
      wtf.setAtribType("2");
    } else if (((String)xmrfct.get("x_sql")).startsWith("EX")) {
      wtf.setAtribType("2");
    } else if (((String)xmrfct.get("x_sql")).startsWith("*")) {
      wtf.setAtribType("3");
    }
    if (((String)xmrfct.get("c_ta")).startsWith("N")) {
      wtf.setDisplayType("N");
    } else if (((String)xmrfct.get("c_ta")).startsWith("P")) {
      wtf.setDisplayType("P");
    }
    wtf.setMsg((String)xmrfct.get("t_txt"));
    String s; if ((s = ((String)xmrfct.get("t_txt")).trim()).length() != 0) {
      wtf.setToolTipText((String)xmrfct.get("t_txt"));
    }
    wtf.setMask((String)xmrfct.get("t_mask"));
    if ((wtf.getMask().trim().length() == 0) && (
      (wc_te.startsWith("n")) || (wc_te.startsWith("d")))) {
      wtf.setMask(MUtil.getMask(wc_te, Integer.valueOf((String)xmrfct.get("q_car")).intValue(), Integer.valueOf((String)xmrfct.get("q_dec")).intValue()));
    }
    




    wtf.setInternalWidth(Integer.valueOf((String)xmrfct.get("q_cari")).intValue());
    wtf.setDecimals(Integer.valueOf((String)xmrfct.get("q_dec")).intValue());
    
    wtf.setPromptControl((String)xmrfct.get("x_ajp"));
    wtf.setPromptCode((String)xmrfct.get("iexs"));
    wtf.setPromptTable((String)xmrfct.get("ictx"));
    wtf.setExpansionField((String)xmrfct.get("niob"));
    wtf.setPromptArgs((String)xmrfct.get("texsargs"));
    wtf.setRule((String)xmrfct.get("x_rg"));
    wtf.setCommand((String)xmrfct.get("t_cmd"));
    wtf.setCommandArgs((String)xmrfct.get("tcmdargs"));
    return wtf;
  }
  
  public static MTextArea getMTextArea(Hashtable xmrfct) {
    boolean wbrline = false;
    if (((String)xmrfct.get("x_cl")).startsWith("/")) {
      wbrline = true;
    }
    if (((String)xmrfct.get("c_label")).startsWith("S")) {
      wbrline = false;
    }
    return getMTextArea(xmrfct, wbrline);
  }
  
  public static MTextArea getMTextArea(Hashtable xmrfct, boolean wbrline) {
    MTextArea wta = new MTextArea((String)xmrfct.get("i_comp"), (String)xmrfct.get("t_lin"), (String)xmrfct.get("t_cmd"), Integer.valueOf((String)xmrfct.get("q_dly")).intValue(), Integer.valueOf((String)xmrfct.get("q_car")).intValue(), wbrline);
    





    if (((String)xmrfct.get("x_md")).toUpperCase().startsWith("M")) {
      wta.setMustFill(true);
    }
    if (((String)xmrfct.get("x_md")).toUpperCase().startsWith("A")) {
      wta.setAttentionFill(true);
    }
    wta.setCommandArgs((String)xmrfct.get("tcmdargs"));
    wta.setDisplayType("N");
    return wta;
  }
  
  public static void putHidden(String ws) {
    if (!htHiddenX.containsKey(ws)) {
      htHiddenX.put(ws, " ");
    }
  }
  
  public static void removeHidden(String ws) {
    if (htHiddenX.containsKey(ws)) {
      htHiddenX.remove(ws);
    }
  }
  
  public static void clearHidden() {
    htHiddenX.clear();
  }
  
  public static Hashtable getHidden() {
    return htHiddenX;
  }
}
