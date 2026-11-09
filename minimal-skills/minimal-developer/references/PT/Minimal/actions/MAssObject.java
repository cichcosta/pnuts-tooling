package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MConnection;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MFile;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MAssObject
  implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean isValid = true;
  
  private String wobj;
  private String wobjname;
  private String[] wargs;
  private SimpleDateFormat wdatef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  

  public MAssObject() {}
  
  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm mform) {
    minForm = mform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError {
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    if ((wargs == null) || (wargs.length < 2)) {
      throw new MError("MIN-900000E");
    }
    System.out.println("!*** MAssObject args.length: " + wargs.length);
    System.out.println("!*** MAssObject args: " + minCommand.getArgs());
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    
    if (wrows.size() == 0) {
      throw new MError("MIN-000103W");
    }
    
    wobj = wargs[0].toUpperCase();
    wobjname = wargs[1];
    
    if (minCommand.getName().equals("DATABASE-DISK")) {
      saveToDisk();
    }
    if (minCommand.getName().equals("DISK-DATABASE")) {
      getFromDisk();
    }
    
    MError werr = new MError("MIN-000000M");
    werr.show(minForm.getMFrame());
  }
  
  private void saveToDisk()
    throws MError
  {
    MCursor wupdate = MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow wrow = (MRow)wrows.elementAt(j);
        if ((wrow.isValidRow()) && (!wrow.isDeletedRow()))
        {




          if (!wrow.hasAssociatedObject(wobj, wupdate)) {
            throw new MError("MIN-MFIC01E");
          }
          MFile wfile = MFile.getMFileFromSystem(minForm.getMFrame(), 1);
          wfile.setBytes(wrow.getAssociatedObject(wobj, wupdate));
          wfile.saveFile();
          if (wrow.getEnt().startsWith("MFIC")) {
            String wdmodif = (String)wrow.get("dmodif");
            System.out.println("!*** MAssObject dmodif: " + wdmodif);
            if (wdmodif.trim().length() != 0) {
              try {
                Date wdate = wdatef.parse(wdmodif);
                wfile.setLastModified(wdate.getTime());
                System.out.println("!*** MAssObject wfile.lastModified(): " + wfile.lastModified());
              }
              catch (Exception ex) {}
            }
          }
        }
      }
    }
    finally
    {
      wupdate.close();
    }
  }
  
  private void getFromDisk()
    throws MError
  {
    MCursor wupdate = MConnection.getMCursor();
    try {
      for (int j = 0; j < wrows.size(); j++) {
        MRow wrow = (MRow)wrows.elementAt(j);
        if ((wrow.isValidRow()) && (!wrow.isDeletedRow()))
        {

          if (((String)wrow.getField("zcst")).toLowerCase().startsWith("r")) {
            throw new MError("MIN-002030E");
          }
          if (wrow.hasAssociatedObject(wobj, wupdate)) {
            MError werr = new MError("MIN-MFIC02Q");
            int wanswer = werr.show();
            System.out.println("!*** MAssObject answer" + wanswer);
            if (wanswer == 1) {
              throw new MError("MIN-000001W");
            }
          }
          MFile wfile = MFile.getMFileFromSystem(minForm.getMFrame(), 0);
          String wpath = " ";String wfolder = "";String wiob = "";String wtob = "*";String wcacess = " ";
          String wtoba = " ";String wsel = " ";
          wpath = wfile.getAbsolutePath();
          if (wpath.lastIndexOf("\\") != -1) {
            wfolder = wpath.substring(0, wpath.lastIndexOf("\\")).replace('\\', '/');
            wiob = wpath.substring(wpath.lastIndexOf("\\") + 1);
          }
          if (wpath.lastIndexOf("/") != -1) {
            wfolder = wpath.substring(0, wpath.lastIndexOf("/"));
            wiob = wpath.substring(wpath.lastIndexOf("/") + 1);
          }
          if (wiob.lastIndexOf(".") != -1) {
            wtob = wiob.substring(wiob.lastIndexOf(".") + 1);
          }
          Date wdate = new Date(wfile.lastModified());
          if (wcacess == " ") {
            wcacess = "*";
            wtoba = wfolder;
          }
          
          wsel = " insert into bobat0 values (0,";
          wsel = wsel + " '" + wrow.getField("zutl") + "','" + wrow.getField("zgrp") + "',";
          wsel = wsel + " '" + MUtil.getTime() + "','" + MUtil.getToday() + "',";
          wsel = wsel + " 'pa',";
          wsel = wsel + " -1,' ',";
          wsel = wsel + " '" + wrow.getEnt().substring(0, 4) + "',";
          wsel = wsel + " " + wrow.getField("zk") + ",'" + wtob + "',";
          wsel = wsel + " '" + wiob + "',";
          wsel = wsel + " '" + wdatef.format(wdate).substring(0, 10) + "','" + wcacess + "',";
          wsel = wsel + " '" + wtoba + "',";
          wsel = wsel + " ' ','PD',";
          wsel = wsel + " '" + MUtil.getToday() + "')";
          
          MCursor wcursor = MConnection.getMCursor();
          wcursor.execStatement(wsel);





        }
        





      }
      





    }
    finally
    {





      wupdate.close();
    }
  }
}
