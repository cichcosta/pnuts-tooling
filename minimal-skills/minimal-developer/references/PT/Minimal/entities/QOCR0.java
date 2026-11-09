package PT.Minimal.entities;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MDialog;
import PT.Minimal.geral.MExp;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MPanel;
import PT.Minimal.geral.MQuery;
import PT.Minimal.pnuts.;
import java.awt.TextArea;
import java.util.Vector;

public class QOCR0 implements PT.Minimal.actions.MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  
  public QOCR0() {}
  
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
  
  public void execute() throws PT.Minimal.geral.MError {
    System.out.println("Entrou execute: " + minCommand.getName());
    if (minCommand.getName().toUpperCase().equals("C-TEXT")) {
      if (wrows.size() == 0) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      String wformaction = (String)minForm.get("formaction");
      int wfase = minForm.getFase();
      if ((wformaction.equals("INSERT_ROW")) && (wfase == 1)) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      showTexp();
    }
    if (minCommand.getName().toUpperCase().equals("C-EST_CL")) {
      if (wrows.size() == 0) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      showEst_Cl();
    }
    if (minCommand.getName().equals("SAVE_C-T")) {
      saveText();
    }
  }
  
  private void showTexp() {
    if (wrows.size() > 0) {
      for (int j = 0; j < wrows.size(); j++) {
        MPanel minTextos = new MPanel("Textos");
        minTextos.setLayout(new PT.Minimal.geral.MLayoutManager());
        
        PT.Minimal.geral.MLabel wlabel1 = new PT.Minimal.geral.MLabel("Causa:", true);
        TextArea ttxt1 = new TextArea();
        ttxt1.setName("ttxt1");
        
        PT.Minimal.geral.MLabel wlabel2 = new PT.Minimal.geral.MLabel("Resolução:", true);
        TextArea ttxt2 = new TextArea();
        ttxt2.setName("ttxt2");
        
        PT.Minimal.geral.MLabel wlabel3 = new PT.Minimal.geral.MLabel("Considerações:", true);
        TextArea ttxt3 = new TextArea();
        ttxt3.setName("ttxt3");
        
        minTextos.add(wlabel1);
        minTextos.add(ttxt1);
        minTextos.add(wlabel2);
        minTextos.add(ttxt2);
        minTextos.add(wlabel3);
        minTextos.add(ttxt3);
        
        PT.Minimal.geral.MUtil.MoveRecord(((PT.Minimal.geral.MRow)wrows.elementAt(j)).getRecord(), minTextos);
        
        MDialog wd = new MDialog(minForm.getMFrame(), "Textos Associados ", true, minTextos, 3, "SAVE_C-T");
        
        wd.getForm().setEnt(minForm.getEnt());
        wd.getForm().setAction("SAVE_C-T");
        wd.getForm().setRow((PT.Minimal.geral.MRow)wrows.elementAt(j));
        wd.pack();
        wd.setSize(wd.getPreferredSize());
        wd.setCentered();
        wd.setVisible(true);
      }
    }
  }
  



  private void saveText()
  {
    PT.Minimal.geral.MUtil.MoveRecord(PT.Minimal.geral.MUtil.getRecord(minForm.getCenterPanel()), minForm.getRow().getRecord());
    minForm.hideForm();
  }
  
  private void showEst_Cl() throws PT.Minimal.geral.MError {
    MExp wexp = null;
    MExp[] wvexp = MForm.getExp(minForm.getEnt(), "QOCRYBR", "AU");
    if (wvexp != null) {
      wexp = wvexp[0];
    }
    if (wexp != null) {
      MForm wfmc = null;
      String wobjass = wexp.getAssociateObject().trim();
      String wftitle = wobjass;
      String[] wexp_args = wexp.getArgs(wexp.getTextArgs());
      if (wexp_args.length > 0) {
        wftitle = wexp_args[0];
      }
      if (wobjass.length() != 0) {
        wfmc = MForm.getMForm(wobjass);
        if (wfmc == null) {
          System.out.println(wobjass);
          wfmc = new MForm(wobjass);
          wfmc.setEnt(minForm.getEnt());
          wfmc.setCenterPanel(MForm.getPanel(wobjass));
          MDialog wd = new MDialog(minForm.getMFrame(), wftitle, true, wfmc, 3);
          wd.pack();
          wd.setSize(wfmc.getPreferredSize());
          wd.setCentered();
          if (minForm.getBrowse() != null) {
            wfmc.setBrowse(minForm.getBrowse());
          }
          wfmc.setRow(minForm.getRow());
        }
        
         mpnut = .getMPnut(wfmc.getEnt());
        mpnut.put(wobjass, wfmc);
        mpnut.evalFormPnut(wfmc, "BF");
        mpnut.remove(wobjass);
        ((MDialog)wfmc.getParent()).setVisible(true);
      }
      if ((wobjass.length() == 0) || ((wobjass.length() != 0) && (getParentbuttonChoosed == 2)))
      {





        MPanel minPanEst = (MPanel)MForm.getPanel("QOCRYBR");
        MDialog wd = new MDialog(minForm.getMFrame(), "Estatisticas por Clientes", true, minPanEst, 3, "Print Y:/InetPub/wwwroot/Java/template/DEFAULT.rtf");
        wd.getForm().setEnt(minForm.getEnt());
        wd.getForm().setAction("Print Y:/InetPub/wwwroot/Java/template/QOCRYQE1.rtf");
        

        wd.getForm().getBrowse().removeAll();
        MQuery wqbrowse = minForm.getBrowse().getMQuery();
        MQuery wq = new MQuery();
        wq.addSelect(wexp.getSelect());
        wq.addFrom(wexp.getFrom());
        wq.addWhere(wqbrowse.getWhere());
        wq.addWhere(wexp.getWhere());
        wq.addGroup(wexp.getGroup());
        wq.addOrder(wexp.getOrder());
        


        PT.Minimal.geral.MCursor wcurs = PT.Minimal.geral.MConnection.getMCursor();
        String wcsel = wq.getQuery();
        wcsel = .getMPnut(minForm.getEnt()).evalValues(wcsel);
        System.out.println(wcsel);
        Vector wv = PT.Minimal.geral.MUtil.getRows(wcsel, wcurs);
        

        wcurs.close();
        wd.getForm().getBrowse().setRows(wv);
        wd.pack();
        wd.setSize(wd.getPreferredSize());
        wd.setCentered();
        wd.setVisible(true);
      }
    }
  }
}
