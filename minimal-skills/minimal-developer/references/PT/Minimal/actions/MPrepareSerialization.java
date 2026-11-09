package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MGetMData;
import PT.Minimal.geral.MHashtable;
import PT.Minimal.geral.MImages;
import PT.Minimal.geral.MLogin;
import java.io.PrintStream;
import java.util.Vector;

public class MPrepareSerialization implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  PT.Minimal.pnuts. mpnut;
  private String[] wargs;
  private boolean isValid = true;
  
  private String wobj;
  private String wobjname;
  
  public MPrepareSerialization() {}
  
  public void setMCommand(MCommand wcmd)
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
  {}
  
  private void executeSerialization() throws MError
  {
    MLogin.showLogin();
    MLogin.setLoginProperty("JMETADATAMODE", "ONLINE");
    MLogin.setLoginProperty("SAVESERMETADATA", "true");
    MLogin.clearClientMeta();
    MImages wimgs = new MImages();
    wimgs.getImg(" ");
    

    MCursor wcurs = MConnection.getMCursor();
    try
    {
      Vector wv = null;
      


      String wcsel = " select distinct i_ob as iob from mrfct0  where length(i_ob) = 4";
      
      try
      {
        wcurs.execQuery(wcsel);
        wv = wcurs.getAllRecords();
      } finally {
        wcurs.closeStatement(); }
      String wiob;
      for (int j = 0; (wv != null) && (j < wv.size()); j++) {
        MHashtable wobj = (MHashtable)wv.elementAt(j);
        wiob = (String)wobj.get("iob");
      }
      


      wcsel = " select distinct i_ob as iob from mrfct0  where length(i_ob) = 5";
      
      try
      {
        wcurs.execQuery(wcsel);
        wv = wcurs.getAllRecords();
      } finally {
        wcurs.closeStatement();
      }
      for (int j = 0; (wv != null) && (j < wv.size()); j++) {
        MHashtable wobj = (MHashtable)wv.elementAt(j);
        String wiob = (String)wobj.get("iob");
        
        MGetMData.getExps(wiob);
        MGetMData.getJExps(wiob);
        MGetMData.getCmds(wiob);
        PT.Minimal.geral.MMenu.getMinMenuBar(wiob + "BR");
        MGetMData.getMenuItems(wiob + "BR");
        PT.Minimal.geral.MMenu.getMinMenuBar(wiob + "ME");
        
        MGetMData.getRunTimeAtribs(wiob.substring(0, 5), "JP");
        try
        {
          MGetMData.getPanel(wiob.substring(0, 5), "BR");
        } catch (MError err) {
          System.out.println(err.getWdcode());
          if (!err.getMinErrorCode().equals("MIN-M00001E")) {
            throw err;
          }
        }
        try {
          MGetMData.getPanel(wiob.substring(0, 5), "ME");
        } catch (MError err) {
          System.out.println(err.getWdcode());
          if (!err.getMinErrorCode().equals("MIN-M00001E")) {
            throw err;
          }
        }
        try {
          MGetMData.getPanel(wiob.substring(0, 5), "MC");
        } catch (MError err) {
          System.out.println(err.getWdcode());
          if (!err.getMinErrorCode().equals("MIN-M00001E")) {
            throw err;
          }
        }
      }
    }
    finally {
      MConnection.closeAll();
    }
  }
  


  public static void main(String[] args)
  {
    System.out.println("Inicio - " + new java.util.Date());
    try {
      MPrepareSerialization wmain = new MPrepareSerialization();
      wargs = args;
      wmain.executeSerialization();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(0);
    }
    System.out.println("Fim - " + new java.util.Date());
    System.exit(0);
  }
}
