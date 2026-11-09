package PT.Minimal.geral;

import PT.Minimal.actions.MMultiRows;
import PT.Minimal.actions.MRegister;
import PT.Minimal.pnuts.MPnuts;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.Vector;

public class MinSaltoME
{
  public MinSaltoME(String went, String wentname, MForm wform, MRow wrow, MExp wsexp, String waction) throws MError
  {
    this(went, wentname, wform, wrow, wsexp, waction, true);
  }
  
  public MinSaltoME(String went, String wentname, MForm wform, MRow wrow, MExp wsexp, String waction, boolean checkTriggers) throws MError {
    Vector wrows = new Vector();
    wrows.addElement(wrow);
    doJump(went, wentname, wform, wrows, wsexp, waction, true);
  }
  
  public MinSaltoME(String went, String wentname, MForm wform, Vector wrows, MExp wsexp, String waction, boolean checkTriggers) throws MError {
    doJump(went, wentname, wform, wrows, wsexp, waction, true);
  }
  















  public void doJump(String went, String wentname, MForm wform, Vector wrows, MExp wsexp, String waction, boolean checkTriggers)
    throws MError
  {
    MRegister.checkJump(wform.getEnt(), went);
    

    MRow wrow = (MRow)wrows.elementAt(0);
    
    MPnuts mpnut = null;
    


    try
    {
      MForm wfbr = MForm.getMForm(went + "ME");
      if (wfbr == null) {
        String[] wargs = MUtil.getArgs(wsexp.getTextArgs(), ' ');
        String wconnect; String wconnect; if ((wargs != null) && (wargs.length > 0)) {
          wconnect = wargs[0];
        } else {
          wconnect = "DEFAULT";
        }
        if ((checkTriggers) && (MUtil.getMinProperty("CHECKTRIGGERS") != null) && (MUtil.getMinProperty("CHECKTRIGGERS").equals("true")) && (wconnect.equals("DEFAULT")))
        {

          System.out.println(went + " - " + wentname + " CheckTriggers MinSalto ");
          MCursor wcurs = MConnection.getMCursor();
          wcurs.checkTriggers(went);
          wcurs.close();
        }
        wfbr = new MForm(went + "ME");
        
        MPrepareEnt wpe = new MPrepareEnt(went, wentname);
        MResData wrd = wpe.getResData();
        wfbr.setEnt(wrd.getEnt());
        wfbr.setEntName(wrd.getEntName());
        
        MRow wr = wpe.getMRow();
        wfbr.setRow(wr);
        

        MForm.addCmds(went, wrd.getCmds());
        
        wfbr.addPanelList(wrd.getAllPanels());
        
        wfbr.addToolBar((MToolBar)MForm.getPanel("ToolBar Entidade"));
        
        wfbr.setCenterPanel(MForm.getPanel(wfbr.getEnt() + "ME"));
        MPanel wbp = null;
        try {
          wbp = MMData.getPanel(wfbr.getEnt() + "MEBT", "ME");
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        if (wbp == null) {
          wfbr.setBotPanel(MForm.getPanel(wfbr.getEnt() + "MEBT"));
        } else {
          wfbr.setBotPanel(wbp);
        }
        wfbr.showStatusBar(true);
        MButton wok = (MButton)MUtil.getComponent(wfbr.getBotPanel(), "wok");
        wfbr.setOKButton(wok);
        MButton wend = (MButton)MUtil.getComponent(wfbr.getBotPanel(), "wend");
        wfbr.setENDButton(wend);
        wfbr.prepareForm();
        
        MFrame wframe = new MFrame();
        

        wframe.setForm(wfbr);
        wframe.pack();
        wframe.setSize(wfbr.getPreferredSize());
        wframe.center(getPreferredSizewidth, getPreferredSizeheight, false);
        if (waction.startsWith("MULTIROWS")) {
          if (went.startsWith("SOUC9")) {
            wframe.center(Math.max(getPreferredSizewidth, 500), Math.max(getPreferredSizeheight, 500), false);
          }
          else
          {
            wframe.center(Math.max(getPreferredSizewidth, 760), Math.max(getPreferredSizeheight, 500), false);
          }
        }
      }
      







      wfbr.setEntName(wentname);
      wfbr.getMFrame().setTitle(wentname);
      mpnut = MPnuts.getMPnut(went);
      
      MFrame.setModalParent(wform.getMFrame(), wfbr.getMFrame());
      
      wfbr.clearFormValues();
      MUtil.clearRecord(wfbr.getRow().getRecord());
      
      MUtil.MoveRecord(wrow.getRecord(), wfbr.getRow().getRecord());
      
      wfbr.setFormValues(wfbr.getRow().getRecord());
      


      Vector wv = new Vector();
      

      mpnut.setForm(wform);
      mpnut.setRow(wrow);
      

      mpnut.put("Prow", wrow);
      mpnut.set("Prow", wrow);
      mpnut.put("Pform", wform);
      mpnut.set("Pform", wform);
      if (!waction.startsWith("INSERT"))
      {
        MExp wexp = wsexp;
        MQuery mq = new MQuery();
        if (wexp != null) {
          mq.addSelect(wexp.getSelect());
          mq.addFrom(wexp.getFrom());
          mq.addWhere(wexp.getWhere());
          mq.addGroup(wexp.getGroup());
          mq.addOrder(wexp.getOrder());
        } else {
          mq.addSelect("a.*");
          mq.addFrom(wfbr.getRow().getTable() + " a");
        }
        

        mpnut.put("wquery", mq);
        mpnut.set("wquery", mq);
        mpnut.evalFormPnut(wfbr, wfbr.getName(), "BJ");
        String wcsel = mq.getQuery();
        
        wcsel = mpnut.evalValues(wcsel);
        System.out.println("!*** MinSaltoME: " + wcsel);
        
        String[] wargs = MUtil.getArgs(wexp.getTextArgs(), ' ');
        String wconnect; String wconnect; if ((wargs != null) && (wargs.length > 0)) {
          wconnect = wargs[0];
        } else {
          wconnect = "DEFAULT";
        }
        
        wv = MUtil.getRows(wfbr.getRow().Copy(), wcsel, 9999, wconnect);
      }
      
      if ((wv.size() == 0) && (!waction.startsWith("MULTIROWS")))
      {
        if (waction.startsWith("INQUIRY")) {
          throw new MError("MIN-000100E");
        }
        wfbr.setAction("INSERT_ROW");
        wfbr.getRow().setField("zk", "0");
      } else {
        if ((wv.size() > 0) && (!waction.startsWith("INSERT"))) {
          wfbr.setRow((MRow)wv.elementAt(0));
        }
        


        if ((wfbr.getRow().containsKey("zcst")) && (((String)wfbr.getRow().getField("zcst")).toLowerCase().charAt(0) == 'r') && ((waction.startsWith("UPDATE")) || (waction.startsWith("DELETE"))))
        {



          wfbr.setAction("INQUIRY_ROW");
        } else {
          wfbr.setAction(waction);
        }
      }
      if (!waction.startsWith("MULTIROWS")) {
        wfbr.setActionObjectIndex(0);
        wfbr.setAtribFormat(wfbr.getCenterPanel(), wfbr.getAction());
        wfbr.setFase(1);
        wfbr.getOKButton().setEnabled(!wfbr.getAction().startsWith("INQUIRY"));
        wfbr.getENDButton().setEnabled(true);
        wfbr.getENDButton().setCommand(wfbr.getAction());
        wfbr.getENDButton().setCommandArgs("CANCEL");
        wfbr.getENDButton().setText(MUtil.lang("\"Terminar\""));
      }
      
      mpnut.setForm(wfbr);
      mpnut.setRow(wfbr.getRow());
      

      if ((wrows.size() > 0) && (waction.startsWith("INSERT"))) {
        wv = new Vector();
        for (int i = 0; i < wrows.size(); i++) {
          MRow zrow = (MRow)wrows.elementAt(i);
          MRow yrow = wfbr.getRow().Copy();
          MUtil.clearRecord(yrow.getRecord());
          MUtil.MoveRecord(zrow.getRecord(), yrow.getRecord());
          wv.addElement(yrow);
        }
        wfbr.setActionObject(wv);
        wfbr.setActionObjectIndex(1);
        wfbr.getENDButton().setCommand(wfbr.getAction());
        wfbr.getENDButton().setCommandArgs("CANCEL");
        if (wrows.size() == 1) {
          wfbr.getENDButton().setText(MUtil.lang("\"Terminar\""));
        } else {
          wfbr.getENDButton().setText(MUtil.lang("\"Seguinte\""));
        }
      }
      
      wfbr.setFormValues(wfbr.getRow().getRecord());
      wfbr.setFormValues(MUtil.expand(wfbr, "NoForce", wfbr.getFormValues()));
      
      mpnut.evalFormPnut(wfbr, wfbr.getName(), "BF");
      
      if (waction.startsWith("MULTIROWS")) {
        wfbr.setAction(waction);
        MMultiRows.prepareMultiRows(wfbr);
      }
      
      wfbr.showForm();
    } catch (MError err) {
      err.show(wform.getMFrame());
    } finally {
      if (mpnut != null) {
        mpnut.remove("FZEMC");
        mpnut.remove("Prow");
        mpnut.unset("Prow");
        mpnut.remove("Pform");
        mpnut.unset("Pform");
        mpnut.remove("wquery");
        mpnut.unset("wquery");
      }
    }
  }
}
