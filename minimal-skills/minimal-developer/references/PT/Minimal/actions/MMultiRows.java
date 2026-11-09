package PT.Minimal.actions;

import PT.Minimal.geral.MBrowse;
import PT.Minimal.geral.MBrowseEdit;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MExp;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MQuery;
import PT.Minimal.geral.MResData;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.Vector;

public class MMultiRows implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private String wargs;
  private boolean isValid = true;
  
  public MMultiRows() {}
  
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
  
  public void execute() throws MError {
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = minCommand.getCallerArgs();
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = minCommand.getArgs();
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wargs = minCommand.getCallerArgs();
    }
    System.out.println("!*** MMultiRows CallerArgs: " + minCommand.getCallerArgs());
    if ((wargs.startsWith("CANCEL")) || (wargs.startsWith("wok, OK")))
    {
      MRegister.removeJump(minForm.getEnt());
    }
    if (wargs.startsWith("CANCEL"))
    {
      cancelOperation(minForm);
      isValid = false;
    }
    if (wargs.startsWith("SET_EDIT")) {
      if (!PT.Minimal.geral.MCheckAut.checkAut(minForm.getEnt(), "SET_EDIT", minCommand.getCommandAutLevel())) {
        throw new MError("MIN-009998E", new String[] { PT.Minimal.geral.MLogin.getUser(), "Editar" });
      }
      setEditableMode(minForm, true);
      isValid = false;
    }
    if (wargs.startsWith("SHOW_MULTIROWS")) {
      show_multirows();
      isValid = false;
    }
    if (isValid) {
      do_multiRow(minForm);
    }
  }
  
  public void show_multirows() throws MError {
    String[] wvargs = MUtil.getArgs(wargs, ',');
    if ((wvargs == null) || (wvargs.length < 4)) {
      throw new MError("Argumentos invalidos");
    }
    String went = wvargs[1];
    String wentname = wvargs[2];
    String waction = wvargs[3];
    
    MForm wfmc = MForm.getMForm(went + "ME");
    try {
      if (wfmc == null) {
        if ((MUtil.getMinProperty("CHECKTRIGGERS") != null) && (MUtil.getMinProperty("CHECKTRIGGERS").equals("true")))
        {
          System.out.println("!*** MMultiRows went: " + went + " - " + wentname + " CheckTriggers MinSalto ");
          MCursor wcurs = PT.Minimal.geral.MConnection.getMCursor();
          wcurs.checkTriggers(went);
          wcurs.close();
        }
        wfmc = new MForm(went + "ME");
        
        PT.Minimal.geral.MPrepareEnt wpe = new PT.Minimal.geral.MPrepareEnt(went, wentname);
        MRow wr = wpe.getMRow();
        MResData wrd = wpe.getResData();
        wfmc.setRow(wr);
        
        MForm.addCmds(went, wrd.getCmds());
        
        wfmc.setEnt(wrd.getEnt());
        wfmc.setEntName(wrd.getEntName());
        
        wfmc.addPanelList(wrd.getAllPanels());
        
        wfmc.addToolBar((PT.Minimal.geral.MToolBar)MForm.getPanel("ToolBar Entidade"));
        wfmc.setCenterPanel(MForm.getPanel(wfmc.getEnt() + "ME"));
        PT.Minimal.geral.MPanel wbp = null;
        try {
          wbp = PT.Minimal.geral.MMData.getPanel(wfmc.getEnt() + "MEBT", "ME");
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        if (wbp == null) {
          wfmc.setBotPanel(MForm.getPanel(wfmc.getEnt() + "MEBT"));
        } else {
          wfmc.setBotPanel(wbp);
        }
        wfmc.showStatusBar(true);
        wfmc.prepareForm();
        
        MFrame wframe = new MFrame();
        wframe.setForm(wfmc);
        wframe.pack();
        wframe.setSize(wfmc.getPreferredSize());
        wframe.center(getPreferredSizewidth, getPreferredSizeheight, false);
        if (waction.startsWith("MULTIROWS")) {
          wframe.center(Math.max(getPreferredSizewidth, 790), Math.max(getPreferredSizeheight, 560), false);
        }
      }
      




      wfmc.setEntName(wentname);
      wfmc.getMFrame().setTitle(wentname);
      mpnut = .getMPnut(went);
      
      MFrame.setModalParent(minForm.getMFrame(), wfmc.getMFrame());
      
      wfmc.clearFormValues();
      

      System.out.println("!*** MMultiRows waction: " + waction);
      wfmc.setAction(waction);
      wfmc.setAtribFormat(wfmc.getCenterPanel(), wfmc.getAction());
      wfmc.setFase(1);
      
      mpnut.setForm(wfmc);
      mpnut.setRow(wfmc.getRow());
      
      mpnut.evalFormPnut(wfmc, wfmc.getName(), "BF");
      
      if (waction.startsWith("MULTIROWS")) {
        prepareMultiRows(wfmc);
      }
      
      ((MFrame)wfmc.getFormParent()).setVisible(true);
    }
    catch (MError err) {
      err.show(minForm.getMFrame());
    }
  }
  
  public void do_multiRow(MForm wfmc)
    throws MError
  {
     mpnut = .getMPnut(wfmc.getEnt());
    int wnrow = 0;
    int wncol = 0;
    MBrowse wbrc = (MBrowse)wfmc.getActionObject();
    
    wbrc.closeEditor();
    com.objectshare.grid.data.JKTableModel myModel = (com.objectshare.grid.data.JKTableModel)wbrc.getModel();
    
    MRow wr = wfmc.getRow().Copy();
    minForm.setRow(wr);
    
    mpnut.setForm(wfmc);
    mpnut.setRow(wfmc.getRow());
    

    MUtil.validate(wfmc, "Force");
    wfmc.setFormValues(MUtil.expand(wfmc, "Force", wfmc.getFormValues()));
    mpnut.set(wbrc.getName().toUpperCase(), wbrc);
    
    mpnut.evalFormPnut(wfmc, "AF");
    MUtil.MoveRecord(wfmc.getFormValues(), wfmc.getRow().getRecord());
    try
    {
      Vector wv = wbrc.getMRows();
      Vector wvR = new Vector();
      Vector wvI = new Vector();
      String wzks = "0";String wzk = "";
      
      for (wnrow = 0; wnrow < wv.size(); wnrow++) {
        MRow wrow = (MRow)wv.elementAt(wnrow);
        
        MUtil.validate(wbrc, "Force", wrow.getRecord());
        MUtil.expand(wbrc, "Force", wrow.getRecord());
      }
      

      for (wnrow = 0; wnrow < wv.size(); wnrow++) {
        MRow wrow = (MRow)wv.elementAt(wnrow);
        MRow yrow = wrow.Copy();
        wzk = wrow.get("zk").toString();
        if (!MUtil.getSQLValue("select count(*) from " + wrow.getEnt().substring(0, 4) + "t0 where zk = " + wzk).equals("0")) {
          wvI.add("N");
          wvR.add(yrow);
          wzks = wzks + "," + wrow.get("zk").toString();
        } else {
          wvI.add("S");
        }
        
        java.util.Hashtable wh = wbrc.getBrowseRecordFromRow(wrow);
        MUtil.MoveRecord(wfmc.getRow().getRecord(), wrow.getRecord());
        MUtil.MoveRecord(wh, wrow.getRecord());
      }
      
      System.out.println("!*** MMultiRows getName: " + wbrc.getName());
      mpnut.set(wbrc.getName().toUpperCase(), wbrc);
      mpnut.evalBrowsePnut(wbrc, "MR");
      
      mpnut.setForm(wfmc);
      mpnut.setRow(wfmc.getRow());
      
      MQuery mq = new MQuery();
      mq.setType("DELETE");
      
      MExp wexp = getMultiRowsExp(wfmc, "*", "MD");
      mq.addFrom(wexp.getFrom());
      mq.addWhere(wexp.getWhere());
      mq.addWhere("zk not in (" + wzks + ")");
      
      String wcsel = mpnut.evalValues(mq.getQuery());
      System.out.println("!*** MMultiRows wcsel: " + wcsel + " ! " + wexp.getWhere());
      
      MCursor wupdate = PT.Minimal.geral.MConnection.getMCursor();
      try {
        wupdate.beginBatch();
        mpnut.set("cursor", wupdate);
        mpnut.evalFormPnut(wfmc, "BAMR");
        wupdate.execStatement(wcsel);
        wupdate.closeStatement();
        
        for (int i = 0; i < wv.size(); i++) {
          MRow wrow = (MRow)wv.elementAt(i);
          if (((String)wvI.elementAt(i)).equals("N")) {
            wrow = (MRow)wvR.elementAt(i);
            wrow.setField("zcst", "MM");
            if ((wrow.containsKey("cst")) && (!wrow.get("cst").equals("PD"))) {
              wrow.setField("zcst", "rM");
            }
            mpnut.setRow(wrow);
            mpnut.evalFormPnut(wfmc, "BMR");
            
            System.out.println("!*** MMultiRows update wrow " + wrow.toString());
            
            wrow.updateRow(wupdate, wrow, true, true);
            wrow.setZCommand("UPDATE_ROW", "AF", " ", wupdate);
          }
          else {
            wrow.setField("zcst", "MM");
            if ((wrow.containsKey("cst")) && (!wrow.get("cst").equals("PD"))) {
              wrow.setField("zcst", "rM");
            }
            mpnut.setRow(wrow);
            mpnut.evalFormPnut(wfmc, "BMR");
            
            System.out.println("!*** MMultiRows insert wrow " + wrow.toString());
            wrow.insertRow(wupdate);
            wrow.setZCommand("INSERT_ROW", "AF", " ", wupdate);
          }
          wrow.refreshRow(wupdate);
          mpnut.setRow(wrow);
          mpnut.evalFormPnut(wfmc, "AMR");
        }
        




        wr = wfmc.getRow();
        mpnut.setRow(wfmc.getRow());
        mpnut.evalFormPnut(wfmc, "AAMR");
        wupdate.commitBatch();
      } finally {
        if (wupdate.inBeginBatch()) {
          wupdate.rollbackBatch();
        }
        wupdate.close();
        wfmc.setRow(wr);
      }
      prepareNext(wfmc);
    } catch (MError ex) {
      String watrib = ex.getAtrib();
      ex.setAtrib("XX");
      ex.show(wfmc.getMFrame());
      if ((ex.getMinErrorCode() != null) && ((ex.getMinErrorCode().equals("MIN-001010E")) || (ex.getMinErrorCode().equals("MIN-001020E"))))
      {


        wncol = myModel.getColumnNumber(watrib);
        System.out.println("!*** MMultiRows openEditor:" + wnrow + " , " + wncol);
        PT.Minimal.geral.MTextFieldEditingPolicy wep = (PT.Minimal.geral.MTextFieldEditingPolicy)wbrc.getColumnEditingPolicy(wncol);
        wep.setHandleClose(false);
        wfmc.showComponentTab(wbrc.getName());
        wbrc.openEditor(wncol, wnrow + 1);
        
        MUtil.requestFocus(wbrc);
      }
    }
  }
  
  public void cancelOperation(MForm wfmc) throws MError {
    wfmc.hideForm();
  }
  
  public void prepareNext(MForm wfmc) throws MError {
    if (wfmc.getAction().equals("MULTIROWS_FULL")) {
      MError wquestion = new MError("MIN-000000Q");
      int wanswer = wquestion.show(wfmc.getMFrame());
      System.out.println("!*** MMultiRows Resposta: " + wanswer);
      if (wanswer == 1) {
        wfmc.hideForm();
      }
      if (wanswer == 0) {
        MBrowse wbrc = (MBrowse)wfmc.getActionObject();
        mpnut.evalFormPnut(wfmc, wfmc.getName(), "BF");
        prepareMultiRows(wfmc);
      }
    } else {
      wfmc.hideForm();
    }
  }
  
  public static void setEditableMode(MForm wfmc, boolean wboo) {
    java.awt.Component wok = MUtil.getComponent(wfmc.getBotPanel(), "wok");
    java.awt.Component wedit = MUtil.getComponent(wfmc.getBotPanel(), "wedit");
    
    if (!wboo) {
      wfmc.setAtribFormat(wfmc.getCenterPanel(), "INQUIRY_ROW");
    }
    if (wboo) {
      if ((wfmc.getAction().equals("MULTIROWS_FULL")) || (wfmc.getAction().equals("MULTIROWS_FULLU"))) {
        wfmc.setAtribFormat(wfmc.getCenterPanel(), "INSERT_ROW");
      } else if (wfmc.getAction().equals("MULTIROWS_EDIT")) {
        wfmc.setAtribFormat(wfmc.getCenterPanel(), "UPDATE_ROW");
      } else if (wfmc.getAction().equals("MULTIROWS_BROWS")) {
        wfmc.setAtribFormat(wfmc.getCenterPanel(), "INQUIRY_ROW");
      } else {
        wfmc.setAtribFormat(wfmc.getCenterPanel(), "UPDATE_ROW");
      }
    }
    

    MBrowse wbrc = (MBrowse)wfmc.getActionObject();
    wbrc.setEditable(wboo);
    
    if ((wok != null) && (wedit != null)) {
      ((PT.Minimal.geral.MButton)wok).setEnabled(wboo);
      ((PT.Minimal.geral.MButton)wedit).setEnabled(!wboo);
    }
    
    if (wboo) {
      wfmc.showComponentTab(wbrc.getName());
      wbrc.clearSelection();
      wbrc.openEditor(1, 1);
    }
  }
  
  public static void prepareMultiRows(MForm wfmc)
  {
     mpnut = .getMPnut(wfmc.getEnt());
    
    MQuery mq = new MQuery();
    MExp wexp = getMultiRowsExp(wfmc, "*", "MR");
    
    mq.addSelect(wexp.getSelect());
    mq.addFrom(wexp.getFrom());
    mq.addWhere(wexp.getWhere());
    mq.addGroup(wexp.getGroup());
    mq.addOrder(wexp.getOrder());
    java.awt.Component wc = wfmc.getFormComponent(wexp.getAssociateObject());
    if (wc == null) {
      throw new MError("Não existe componente indicado na expressão MR!");
    }
    if ((wc instanceof MBrowse)) {
      MBrowse wbrc = (MBrowse)wc;
      wbrc.setEnt(wfmc.getEnt());
    }
    else {
      System.out.println("!*** MMultiRows Componente: " + wc);
      throw new MError("Componente indicado <" + wexp.getAssociateObject() + "> na expressão MR não é adequado!");
    }
    MBrowse wbrc;
    wfmc.setActionObject(wbrc);
    mpnut.setForm(wfmc);
    mpnut.setRow(wfmc.getRow());
    
    wbrc.setMQuery(mq);
    String wcsel = mpnut.evalValues(mq.getQuery());
    System.out.println("!*** MMultiRows wcsel2: " + wcsel);
    MRow wwwrow = wfmc.getRow().Copy();
    Vector wvrows = MUtil.getRows(wwwrow, wcsel, 999);
    
    for (int i = 0; i < wvrows.size(); i++) {
      if ((((MRow)wvrows.elementAt(i)).containsKey("zt")) && 
        (!((MRow)wvrows.elementAt(i)).get("text").toString().trim().equals(""))) {
        ((MRow)wvrows.elementAt(i)).set("zt", "*");
      }
    }
    
    wbrc.setRows(wvrows);
    


    if ((wfmc.getAction().equals("MULTIROWS_FULL")) || (wfmc.getAction().equals("MULTIROWS_FULLU"))) {
      if (wvrows.size() == 0) {
        ((MBrowseEdit)wbrc).addNewRowForEdit();
      }
      setEditableMode(wfmc, true);
    } else if (wfmc.getAction().equals("MULTIROWS_EDIT")) {
      if (wvrows.size() == 0) {
        ((MBrowseEdit)wbrc).addNewRowForEdit();
      }
      setEditableMode(wfmc, true);
    } else if (wfmc.getAction().equals("MULTIROWS_BROWS")) {
      if (wvrows.size() == 0) {
        ((MBrowseEdit)wbrc).addNewRowForEdit();
      }
      setEditableMode(wfmc, false);
    } else {
      setEditableMode(wfmc, false);
    }
  }
  
  private static MExp getMultiRowsExp(MForm wfmc, String wcomp, String wtexp) {
     mpnut = .getMPnut(wfmc.getEnt());
    MExp[] wvexp = MForm.getExp(wfmc.getEnt(), wcomp, wtexp);
    MExp wexp = null;
    if (wvexp != null) {
      boolean bfound = false;
      for (int i = 0; i < wvexp.length; i++) {
        wexp = wvexp[i];
        if (mpnut.evalCondition(wexp.getCondition())) {
          bfound = true;
          break;
        }
      }
      if (!bfound) {
        throw new MError("Não existe expressão " + wtexp + " válida!");
      }
    }
    if (wexp == null) {
      throw new MError("Não existe expressão " + wtexp + " válida!");
    }
    return wexp;
  }
}
