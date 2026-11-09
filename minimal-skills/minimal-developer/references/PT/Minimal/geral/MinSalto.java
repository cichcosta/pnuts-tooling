package PT.Minimal.geral;

import PT.Minimal.actions.MCriterium;
import PT.Minimal.actions.MRegister;
import PT.Minimal.pnuts.MPnuts;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintStream;
import java.util.Date;
import java.util.Vector;
import javax.swing.JMenuBar;



public class MinSalto
{
  private static String sLastEnt = "";
  
  public MinSalto(String went, String wentname, MForm wform, MRow wrow, MExp wsexp) throws MError { this(went, wentname, wform, wrow, wsexp, true); }
  
















  public MinSalto(String went, String wentname, MForm wform, MRow wrow, MExp wsexp, boolean checkTriggers)
    throws MError
  {
    MRegister.checkJump(wform.getEnt(), went);
    if (went.equals("MEXT9")) {
      sLastEnt = wform.getEnt();
    }
    
    MPnuts mpnut = null;
    


    try
    {
      MForm wfbr = MForm.getMForm(went + "BR");
      if ((wfbr == null) || (wfbr.getName().startsWith("SOUT9")) || (wfbr.getName().startsWith("SQRY9")) || (wfbr.getName().startsWith("SCGP9")) || (wfbr.getName().startsWith("BOBA0")) || (wfbr.getName().startsWith("MEXT9"))) {
        if ((checkTriggers) && (MUtil.getMinProperty("CHECKTRIGGERS") != null) && (MUtil.getMinProperty("CHECKTRIGGERS").equals("true")))
        {
          System.out.println(went + " - " + wentname + " CheckTriggers MinSalto ");
          MCursor wcurs = MConnection.getMCursor();
          wcurs.checkTriggers(went);
          wcurs.close();
        }
        wfbr = new MForm(went + "BR");
        
        MPrepareEnt wpe = new MPrepareEnt(went, wentname);
        MRow wr = wpe.getMRow();
        MResData wrd = wpe.getResData();
        
        wfbr.setRow(wr);
        wfbr.setType("MULTIROW");
        

        MForm.addCmds(went, wrd.getCmds());
        
        wfbr.setEnt(wrd.getEnt());
        wfbr.setEntName(wrd.getEntName());
        
        wfbr.addPanelList(wrd.getAllPanels());
        
        MToolBar minToolBarGeral = (MToolBar)MForm.getPanel("ToolBar Minimal");
        if (wr.hasCurrencies()) {
          MPanel wtbpanel = new MPanel();
          wtbpanel.setLayout(new BorderLayout());
          minToolBarGeral.addActionListener(wfbr.getActionListener());
          minToolBarGeral.prepareToolBar();
          wtbpanel.add(minToolBarGeral, "West");
          if ((!wfbr.getName().startsWith("H")) || (!MLogin.hasDBProperty("MINMOEDA1")) || ((MLogin.getDBProperty("MINMOEDA1").equals("EUR")) && (MLogin.getDBProperty("MINMOEDA2").equals("PTE"))) || ((MLogin.getDBProperty("MINMOEDA1").equals("CVE")) && (MLogin.getDBProperty("MINMOEDA2").equals("EUR"))))
          {

            MToolBar wctb = wrd.getCurrencyToolBar(wr.getNumberOfCurrencies());
            wctb.addActionListener(wfbr.getActionListener());
            wctb.prepareToolBar();
            wtbpanel.add(wctb, "East");
          }
          wfbr.addToolBarPanel(wtbpanel);
          wfbr.setCurrencyButtonGroup(wrd.getCurrencyButtonGroup());
        } else {
          wfbr.addToolBar(minToolBarGeral);
        }
        
        wfbr.setTopPanel(MForm.getPanel(went + "BRTP"));
        wfbr.setCenterPanel(MForm.getPanel(went, "BR"));
        wfbr.setBotPanel(MForm.getPanel(went + "BRBT"));
        wfbr.showStatusBar(true);
        JMenuBar wmenubar = MMenu.getMinMenuBar(went + "BR");
        if (wmenubar == null) {
          wfbr.addMenuBar(wrd.getJMenuBar());
        } else {
          wfbr.addMenuBar(wmenubar);
        }
        wfbr.setAction("BROWSE_ROW");
        wfbr.prepareForm();
        
        MFrame wframe = new MFrame();
        

        wframe.setForm(wfbr);
        wframe.pack();
        if ((!went.equals("SOUT9")) && (!went.equals("SOUC9")) && (!went.equals("SQRY9")) && (!went.equals("SCGP9"))) {
          wframe.setSize(wfbr.getPreferredSize());
          
          System.out.println("!*** MinSalto Fim da preparacao da Frame - " + new Date());
          
          wframe.center(Math.max(getPreferredSizewidth, 760), Math.max(getPreferredSizeheight, 500), false);
        }
        else
        {
          if (went.equals("SOUT9")) {
            Dimension d = new Dimension(525, 420);
            wframe.setSize(d);
            wframe.centerRigth(525, 420, false);
          }
          if (went.equals("SOUC9")) {
            Dimension d = new Dimension(300, 420);
            wframe.setSize(d);
            wframe.centerRigth(300, 420, false);
          }
          if (went.equals("SQRY9")) {
            Dimension d = new Dimension(400, 420);
            wframe.setSize(d);
            wframe.centerRigth(400, 420, false);
          }
          if (went.equals("SCGP9")) {
            Dimension d = new Dimension(300, 420);
            wframe.setSize(d);
            wframe.centerRigth(300, 420, false);
          }
        }
      }
      









      wfbr.setEntName(wentname);
      wfbr.getMFrame().setTitle(wentname);
      
      MFrame.setModalParent(wform.getMFrame(), wfbr.getMFrame());
      
      MUtil.MoveRecord(wrow.getRecord(), wfbr.getRow().getRecord());
      


      wfbr.getBrowse().removeAll();
      


      mpnut = MPnuts.getMPnut(went);
      mpnut.setForm(wfbr);
      mpnut.setRow(wfbr.getRow());
      

      mpnut.put("Prow", wrow);
      mpnut.set("Prow", wrow);
      mpnut.put("Pform", wform);
      mpnut.set("Pform", wform);
      
      MExp wexp = wsexp;
      MQuery mq = new MQuery();
      if (wexp != null) {
        mq.addSelect(wexp.getSelect());
        mq.addFrom(wexp.getFrom());
        mq.addWhere(wexp.getWhere());
        mq.addGroup(wexp.getGroup());
        mq.addOrder(wexp.getOrder());
        MExp[] wvexp = MForm.getExp(wfbr.getEnt(), "*", "E5");
        if (wvexp != null) {
          boolean bfound = false;
          for (int i = 0; i < wvexp.length; i++) {
            MExp wexp1 = wvexp[i];
            if (mpnut.evalCondition(wexp1.getCondition())) {
              mq.addSelect(wexp1.getSelect());
              mq.addFrom(wexp1.getFrom());
              mq.addWhere(wexp1.getWhere());
              mq.addGroup(wexp1.getGroup());
              
              bfound = true;
              break;
            }
          }
          if (!bfound) {
            throw new MError("Não existe expressão E5 válida...");
          }
        }
      } else {
        mq.addSelect("a.*");
        mq.addFrom(wfbr.getRow().getTable() + " a");
      }
      
      MExp[] wvexp = MForm.getExp(wfbr.getEnt(), "*", "SS");
      if (wvexp != null) {
        boolean bfound = false;
        for (int i = 0; i < wvexp.length; i++) {
          MExp wexp1 = wvexp[i];
          if (mpnut.evalCondition(wexp1.getCondition())) {
            mq.addSelect(wexp1.getSelect());
            mq.addFrom(wexp1.getFrom());
            mq.addWhere(wexp1.getWhere());
            if ((wexp1.getOrder() != null) && (wexp1.getOrder().trim().length() != 0)) {
              mq.setOrder(wexp1.getOrder());
            }
            bfound = true;
            break;
          }
        }
      }
      
      wfbr.getRow().changeCurrency(wrow.getCurrency());
      wfbr.setCurrencySelectedButton(wrow.getCurrency());
      
      wfbr.getBrowse().setEnt(wfbr.getEnt());
      wfbr.getBrowse().setMQuery(mq);
      

      mpnut.put("wquery", mq);
      mpnut.set("wquery", mq);
      mpnut.evalFormPnut(wfbr, wfbr.getName(), "BJ");
      String wcsel = mq.getQuery();
      wcsel = mpnut.evalValues(wcsel);
      System.out.println("!*** MinSalto: " + wcsel);
      Vector wvrows = MUtil.getRows(wfbr.getRow().Copy(), wcsel, 99999);
      mpnut.set("vrows", wvrows);
      mpnut.evalFormPnut(wfbr, "AQ");
      
      if ((wexp != null) && 
        (wexp.getTextArgs() != null) && (wexp.getTextArgs().trim().length() != 0) && (wvrows.size() != 0)) {
        MCriterium.doBreakVector(wexp.getTextArgs(), wvrows);
      }
      
      wfbr.getBrowse().setRows(wvrows);
      


      MTextField wnr = (MTextField)MUtil.getComponent(wfbr.getTopPanel(), "wnreg");
      if (wfbr.getBrowse() != null) {
        wnr.setText(String.valueOf(wfbr.getBrowse().getNumberMRows()));
        wnr.setText(wnr.getFormatedText());
      }
      
      ((MFrame)wfbr.getFormParent()).setVisible(true);
    }
    catch (MError err) {
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
        mpnut.remove("vrows");
        mpnut.unset("vrows");
      }
    }
  }
  
  public static String getLastEnt() {
    return sLastEnt;
  }
}
