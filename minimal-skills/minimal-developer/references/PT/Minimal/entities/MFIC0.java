package PT.Minimal.entities;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MFile;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MGetMData;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MZipOut;
import java.util.Vector;

public class MFIC0 implements PT.Minimal.actions.MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  MForm minForm;
  Vector wrows;
  String EOL = "\n";
  private java.text.SimpleDateFormat wdatef = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  public MFIC0() {}
  
  public void setMCommand(PT.Minimal.geral.MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError
  {
    if (minCommand.getName().equals("GERFIC")) {
      if (wrows.size() == 0) {
        throw new MError("MIN-000103W");
      }
      ger_fic();
    }
    if (minCommand.getName().equals("CREATEFIC")) {
      create_fic();
    }
    if (minCommand.getName().equals("CREATERTF")) {
      create_RTF_fic();
    }
    MError werr = new MError("MIN-000000M");
    werr.show(minForm.getMFrame());
  }
  


  public void ger_fic()
    throws MError
  {
    for (int j = 0; j < wrows.size(); j++) {
      MRow xmfict = (MRow)wrows.elementAt(j);
      if (((String)xmfict.getField("zcst")).toLowerCase().startsWith("r")) {
        throw new MError("MIN-002030E");
      }
      String wifict = (String)xmfict.getField("ifict");
      boolean wboo = PT.Minimal.geral.MSerializedObject.getMode();
      PT.Minimal.geral.MSerializedObject.setMode(false);
      
      if (wifict.equals("ENT")) {
        ger_ent_fic(xmfict);
      }
      if (wifict.equals("ROW")) {
        ger_row_fic(xmfict);
      }
      if (wifict.equals("MEN")) {
        ger_men_fic(xmfict);
      }
      if (wifict.equals("ERR")) {
        ger_err_fic(xmfict);
      }
      if (wifict.equals("RTF")) {
        ger_rtf_fic(xmfict);
      }
      PT.Minimal.geral.MSerializedObject.setMode(wboo);
    }
  }
  
  public void ger_rtf_fic(MRow xmfict) throws MError {
    String went = (String)xmfict.getField("ific");
    String wdmodif = (String)xmfict.getField("dmodif");
    
    MFile wfile = new MFile(went);
    java.util.Date wdate = new java.util.Date(wfile.lastModified());
    String wfiledate = wdatef.format(wdate);
    if (wfiledate.compareTo(wdmodif) > 0) {
      save_file(xmfict, wfile);
    }
  }
  

  public void ger_err_fic(MRow xmfict)
    throws MError
  {
    String went = (String)xmfict.getField("ific");
    String wconnect = (String)xmfict.getField("cmeta");
    

    MZipOut wzipout = new MZipOut();
    


    wzipout.addObject("SERRJMETA", MGetMData.getErrorCodes());
    


    wzipout.addObject("SERCJMETA", MGetMData.getErrorConversions());
    

    save_fic(xmfict, wzipout.getZipBytes());
  }
  
  public void ger_men_fic(MRow xmfict) throws MError
  {
    String went = (String)xmfict.getField("ific");
    
    String wconnect = (String)xmfict.getField("cmeta");
    

    MZipOut wzipout = new MZipOut();
    


    wzipout.addObject(went, PT.Minimal.geral.MMenu.getMainMenuBar(went));
    
    save_fic(xmfict, wzipout.getZipBytes());
  }
  
  public void ger_row_fic(MRow xmfict) throws MError {
    String went = (String)xmfict.getField("ific");
    String wconnect = (String)xmfict.getField("cmeta");
    String wdmodif = (String)xmfict.getField("dmodif");
    


    String wcsel = " select nvl(max('EXISTS'),'NOT EXISTS')  from dual  where exists (  select '+' from mrfct0  where i_ob = '" + went + "' " + " and zdst||' '||zhst > '" + wdmodif + "') " + " or exists ( " + " select '+' from mjext0 " + " where i_ob = '" + went + "' " + " and zdst||' '||zhst > '" + wdmodif + "') ";
    









    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor(wconnect);
    try {
      if (PT.Minimal.geral.MUtil.getSQLValue(wcsel, mrfc_con).equals("NOT EXISTS")) {
        return;
      }
    }
    finally {
      mrfc_con.close();
    }
    
    MZipOut wzipout = new MZipOut();
    


    wzipout.addObject("MRFC" + went + "RW", MGetMData.getRunTimeAtribs(went, "RW", wconnect));
    


    wzipout.addObject("MJEX" + went, MGetMData.getJExpsVector(went, wconnect));
    

    save_fic(xmfict, wzipout.getZipBytes());
  }
  
  public void ger_ent_fic(MRow xmfict)
    throws MError
  {
    String[] wentobjs = null;
    
    String went = (String)xmfict.getField("ific");
    String wconnect = (String)xmfict.getField("cmeta");
    String wdmodif = (String)xmfict.getField("dmodif");
    MZipOut wzipout = null;
    

    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor(wconnect);
    try
    {
      String wcsel = " select nvl(max('EXISTS'),'NOT EXISTS')  from dual  where exists (  select '+' from mrfct0  where i_ob like '" + went + "%' " + " and zdst||' '||zhst > '" + wdmodif + "') " + " or exists ( " + " select '+' from mjext0 " + " where i_ob like '" + went + "%' " + " and zdst||' '||zhst > '" + wdmodif + "') " + " or exists ( " + " select '+' from mexst0 " + " where i_ob like '" + went + "%' " + " and zdst||' '||zhst > '" + wdmodif + "') " + " or exists ( " + " select '+' from mcfxt0 " + " where i_ob like '" + went + "%' " + " and zdst||' '||zhst > '" + wdmodif + "') " + " or exists ( " + " select '+' from sment0 " + " where imen like '" + went + "%' " + " and zdst||' '||zhst > '" + wdmodif + "') ";
      





















      if (PT.Minimal.geral.MUtil.getSQLValue(wcsel, mrfc_con).equals("NOT EXISTS")) {
        return;
      }
      
      wzipout = new MZipOut();
      


      wzipout.addObject("MJEX" + went, MGetMData.getJExpsVector(went, wconnect));
      wzipout.addObject("MCFX" + went, MGetMData.getCmdsVector(went, wconnect));
      
      wzipout.addObject("SMEN" + went + "BR", PT.Minimal.geral.MMenu.getMinMenuBar(went + "BR"));
      wzipout.addObject("SMEN" + went + "ME", PT.Minimal.geral.MMenu.getMinMenuBar(went + "ME"));
      wzipout.addObject("SMEN" + went + "MC", PT.Minimal.geral.MMenu.getMinMenuBar(went + "MC"));
      
      wzipout.addObject("SMEN" + went + "BR_1", MGetMData.getMenuItems(went + "BR"));
      

      wentobjs = null;
      
      wcsel = " select distinct i_ob as iob  from mrfct0  where i_ob like '" + went + "%' ";
      


      System.out.println(wcsel);
      wentobjs = mrfc_con.getColumnAsArray(wcsel, 1);
      
      for (int i = 0; (wentobjs != null) && (i < wentobjs.length); i++) {
        wzipout.addObject("MRFC" + wentobjs[i] + "ME", MGetMData.getRunTimeAtribs(wentobjs[i], "ME", wconnect));
        wzipout.addObject("MRFC" + wentobjs[i] + "MC", MGetMData.getRunTimeAtribs(wentobjs[i], "MC", wconnect));
        wzipout.addObject("MRFC" + wentobjs[i] + "BR", MGetMData.getRunTimeAtribs(wentobjs[i], "BR", wconnect));
        wzipout.addObject("MRFC" + wentobjs[i] + "JP", MGetMData.getRunTimeAtribs(wentobjs[i], "JP", wconnect));
      }
      
      wentobjs = null;
      
      wcsel = " select distinct i_ob as iob  from mexst0  where i_ob like '" + went + "%' ";
      


      System.out.println(wcsel);
      wentobjs = mrfc_con.getColumnAsArray(wcsel, 1);
      
      for (int i = 0; (wentobjs != null) && (i < wentobjs.length); i++) {
        wzipout.addObject("MEXS" + wentobjs[i], MGetMData.getExpsVector(wentobjs[i], wconnect));
      }
    }
    finally {
      mrfc_con.close();
    }
    save_fic(xmfict, wzipout.getZipBytes());
  }
  
  public void create_fic() throws MError {
    String[] wentobjs = null;
    
    MRow wmfic = PT.Minimal.geral.MMData.getMRow("MFIC");
    

    MCursor mrfc_con = PT.Minimal.geral.MConnection.getMCursor("JMETADATA");
    MCursor mfic_con = PT.Minimal.geral.MConnection.getMCursor("DEFAULT");
    
    try
    {
      wentobjs = null;
      
      String wcsel = "select distinct substr(a.i_ob,1,4) as iob from mrfct0 a where length(trim(a.i_ob)) = 4 and not exists (select b.ific from mfict0 b where b.ific = substr(a.i_ob,1,4) and b.ifict = 'ROW' and b.cmeta = 'JMETADATA') union select distinct substr(a.i_ob,1,4) as iob from mjext0 a where length(trim(a.i_ob)) <= 4 and not exists (select b.ific from mfict0 b where b.ific = substr(a.i_ob,1,4) and b.ifict = 'ROW' and b.cmeta = 'JMETADATA') ";
      















      System.out.println(wcsel);
      wentobjs = mrfc_con.getColumnAsArray(wcsel, 1);
      

      for (int i = 0; (wentobjs != null) && (i < wentobjs.length); i++) {
        MRow xmfict = wmfic.Copy();
        xmfict.setField("ific", wentobjs[i]);
        xmfict.setField("ifict", "ROW");
        xmfict.setField("cmeta", "JMETADATA");
        xmfict.setField("ipsr", "MJV-PT.EMP.02.01");
        xmfict.setField("nfic", "Serialização do row");
        xmfict.setField("cref", "OBJECT");
        xmfict.setField("cformat", "ZIP");
        xmfict.setField("cst", "PD");
        xmfict.setField("dst", PT.Minimal.geral.MUtil.getToday());
        xmfict.setField("cstm", "MN");
        
        xmfict.insertRow(mfic_con);
        
        if (minForm.getBrowse() != null) {
          minForm.getBrowse().addRow(xmfict);
          minForm.getBrowse().refresh(xmfict);
        }
      }
      


      wentobjs = null;
      
      wcsel = "select distinct substr(a.i_ob,1,5) as iob from mrfct0 a where ((length(trim(a.i_ob)) = 5 or a.i_ob like '_____H%') or length(trim(a.i_ob)) >= 5  and not exists (select '*' from mrfct0 c where length(trim(c.i_ob)) = 5 and c.i_ob=a.i_ob)) and not exists (select b.ific from mfict0 b where b.ific = substr(a.i_ob,1,5) and b.ifict = 'ENT' and b.cmeta = 'JMETADATA') union select distinct substr(a.i_ob,1,5) as iob from mjext0 a where length(trim(a.i_ob)) >= 5 and not exists (select b.ific from mfict0 b where b.ific = substr(a.i_ob,1,5) and b.ifict = 'ENT' and b.cmeta = 'JMETADATA') union select distinct substr(a.i_ob,1,5) as iob from mexst0 a where length(trim(a.i_ob)) >= 5 and not exists (select b.ific from mfict0 b where b.ific = substr(a.i_ob,1,5) and b.ifict = 'ENT' and b.cmeta = 'JMETADATA') ";
      



























      System.out.println(wcsel);
      wentobjs = mrfc_con.getColumnAsArray(wcsel, 1);
      

      for (int i = 0; (wentobjs != null) && (i < wentobjs.length); i++) {
        MRow xmfict = wmfic.Copy();
        xmfict.setField("ific", wentobjs[i]);
        xmfict.setField("ifict", "ENT");
        xmfict.setField("cmeta", "JMETADATA");
        xmfict.setField("ipsr", "MJV-PT.EMP.02.01");
        xmfict.setField("nfic", "Serialização do sufixo");
        xmfict.setField("cref", "OBJECT");
        xmfict.setField("cformat", "ZIP");
        xmfict.setField("cst", "PD");
        xmfict.setField("dst", PT.Minimal.geral.MUtil.getToday());
        xmfict.setField("cstm", "MN");
        
        xmfict.insertRow(mfic_con);
        
        if (minForm.getBrowse() != null) {
          minForm.getBrowse().addRow(xmfict);
          minForm.getBrowse().refresh(xmfict);
        }
      }
    }
    finally
    {
      mrfc_con.close();
      mfic_con.close();
    }
  }
  


  public void create_RTF_fic()
    throws MError
  {
    MFile wroot = new MFile("Template");
    Vector wfilelist = wroot.getAllFiles(false);
    
    MRow wmfic = PT.Minimal.geral.MMData.getMRow("MFIC");
    

    MCursor mfic_con = PT.Minimal.geral.MConnection.getMCursor("DEFAULT");
    try {
      for (int i = 0; (wfilelist != null) && (i < wfilelist.size()); i++) {
        MFile wfile = (MFile)wfilelist.elementAt(i);
        MRow xmfict = wmfic.Copy();
        xmfict.setField("ific", wfile.getPath());
        xmfict.setField("ifict", "RTF");
        xmfict.setField("cmeta", "JMETADATA");
        xmfict.setField("ipsr", "MJV-PT.EMP.02.01");
        xmfict.setField("nfic", "Serialização de RTFs");
        xmfict.setField("cref", "OBJECT");
        xmfict.setField("cformat", "ZIP");
        xmfict.setField("tpath", wfile.getPath());
        xmfict.setField("cst", "PD");
        xmfict.setField("dst", PT.Minimal.geral.MUtil.getToday());
        xmfict.setField("cstm", "MN");
        
        try
        {
          xmfict.insertRow(mfic_con);
          if (minForm.getBrowse() != null) {
            minForm.getBrowse().addRow(xmfict);
            minForm.getBrowse().refresh(xmfict);
          }
        } catch (MError err) {
          if ((err.getMinErrorCode() == null) || (!err.getMinErrorCode().equals("MIN-000803E")))
          {

            throw err;
          }
          
        }
      }
    }
    finally
    {
      mfic_con.close();
    }
  }
  


  public void save_fic(MRow xmfict, byte[] b)
    throws MError
  {
    xmfict.setAssociatedObject("FICH_ROW", b);
    xmfict.set("qsize", String.valueOf(b.length));
    java.util.Date wdate = new java.util.Date();
    xmfict.set("dmodif", wdatef.format(wdate));
    
    java.util.zip.Adler32 checksum = new java.util.zip.Adler32();
    checksum.update(b);
    xmfict.set("qcheck", Long.toString(checksum.getValue()));
    
    xmfict.updateRow(xmfict);
    xmfict.refreshRow();
  }
  
  public void save_file(MRow xmfict, MFile wfile) throws MError
  {
    byte[] b = wfile.getBytes();
    MZipOut wzipout = new MZipOut();
    

    wzipout.add(wfile.getName(), b);
    
    b = wzipout.getZipBytes();
    xmfict.setAssociatedObject("FICH_ROW", b);
    xmfict.set("qsize", String.valueOf(b.length));
    java.util.Date wdate = new java.util.Date(wfile.lastModified());
    xmfict.set("dmodif", wdatef.format(wdate));
    
    java.util.zip.Adler32 checksum = new java.util.zip.Adler32();
    checksum.update(b);
    xmfict.set("qcheck", Long.toString(checksum.getValue()));
    
    xmfict.updateRow(xmfict);
    xmfict.refreshRow();
  }
}
