package PT.Minimal.geral;

import java.awt.Dimension;
import java.io.PrintStream;
import java.util.Date;
import java.util.Properties;

public class MinMain
{
  private String went;
  private String wentname;
  private static Properties myProp = new Properties();
  private static MLoginWindow wsplash;
  
  public MinMain() { this("FEXT5", "Extractos Bancarios"); }
  

  public MinMain(String ent, String entname) { this(ent, entname, true); }
  
  public MinMain(String ent, String entname, boolean checkTriggers) {
    went = ent;
    wentname = entname;
    System.out.println("!*** MinMain " + went + " - " + wentname);
    
    run(checkTriggers);
  }
  
  public void run(boolean checkTriggers)
  {
    MFrame wframe = new MFrame();
    try {
      System.out.println("!*** MinMain ASSYNC ?????: " + went + "" + wentname);
      
      if (went.startsWith("-")) {
        System.out.println("!*** MinMain ASSYNC: " + went + "" + wentname);
        went = went.substring(1, 6);
        MLogin.showLogin(wframe);
      }
      
      if ((checkTriggers) && (MUtil.getMinProperty("CHECKTRIGGERS") != null) && (MUtil.getMinProperty("CHECKTRIGGERS").equals("true")))
      {
        System.out.println("!*** MinMain " + went + " - " + wentname + " CheckTriggers MinMain ");
        MCursor wcurs = MConnection.getMCursor();
        try {
          wcurs.checkTriggers(went);
        } finally {
          wcurs.close();
        }
      }
      
      if (!MCheckAut.checkAut(went, "*", "1")) {
        throw new MError("MIN-009998E", new String[] { MLogin.getUser(), went + " " + wentname });
      }
      
      System.out.println("!*** MinMain Inicio da preparacao da MResData - " + new Date() + " " + went + " " + wentname);
      

      MPrepareEnt wpe = new MPrepareEnt(went, wentname);
      MRow wr = wpe.getMRow();
      MResData wrd = wpe.getResData();
      

      System.out.println("!*** MinMain Inicio da preparacao da Frame - " + new Date() + " " + went);
      






      MForm wfbr = new MForm(went + "BR");
      
      MForm.addCmds(went, wrd.getCmds());
      







      wfbr.setEnt(wrd.getEnt());
      wfbr.setEntName(wrd.getEntName());
      
      wfbr.addPanelList(wrd.getAllPanels());
      
      wfbr.setTopPanel(MForm.getPanel(went + "BRTP"));
      wfbr.setCenterPanel(MForm.getPanel(went, "BR"));
      
      System.out.println("!*** MinMain Inicio da preparacao BRBT - " + new Date());
      wfbr.setBotPanel(MForm.getPanel(went + "BRBT"));
      
      MToolBar minToolBarGeral = (MToolBar)MForm.getPanel("ToolBar Minimal");
      if (wr.hasCurrencies()) {
        MPanel wtbpanel = new MPanel();
        wtbpanel.setLayout(new java.awt.BorderLayout());
        minToolBarGeral.addActionListener(wfbr.getActionListener());
        minToolBarGeral.prepareToolBar();
        wtbpanel.add(minToolBarGeral, "West");
        
        if ((!wfbr.getName().startsWith("H")) || (!MLogin.hasDBProperty("MINMOEDA1")) || ((MLogin.getDBProperty("MINMOEDA1").equals("EUR")) && (MLogin.getDBProperty("MINMOEDA2").equals("PTE"))) || ((MLogin.getDBProperty("MINMOEDA1").equals("CVE")) && (MLogin.getDBProperty("MINMOEDA2").equals("EUR")))) {
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
      



      wfbr.setRow(wr);
      wfbr.setType("MULTIROW");
      wfbr.setAction("BROWSE_ROW");
      getBotPanelgetInsetsright = 10;
      wfbr.showStatusBar(true);
      
      javax.swing.JMenuBar wmenubar = MMenu.getMinMenuBar(went + "BR");
      if (wmenubar == null) {
        wfbr.addMenuBar(wrd.getJMenuBar());
      } else {
        wfbr.addMenuBar(wmenubar);
      }
      wfbr.prepareForm();
      wframe.setTitle(wrd.getEntName());
      wframe.setForm(wfbr);
      
      wframe.setSize(wfbr.getPreferredSize());
      System.out.println("!*** MinMain Fim da preparacao da Frame - " + new Date());
      
      wframe.center(Math.max(getPreferredSizewidth, 775), Math.max(getPreferredSizeheight, 510), false);
      

      wcomm = new PT.Minimal.actions.MHandleCommand(wframe, null, wfbr, wframe, null, "CRITERIUM_ROW");
    } catch (MError err) {
      PT.Minimal.actions.MHandleCommand wcomm;
      err.show(wframe);
    }
  }
  
  public static void main(String[] args)
  {
    MinMain wmain;
    MinMain wmain;
    if (args.length >= 2) {
      System.out.println("!*** MinMain Inicio:" + new Date() + " Args: " + args[0] + " " + args[1]);
      wmain = new MinMain(args[0], args[1]);
    } else {
      System.out.println("!*** MinMain Inicio - " + new Date());
      wmain = new MinMain("QOCR0", "Objectos");
    }
    
    System.out.println("!*** MinMain Fim: " + new Date());
  }
}
