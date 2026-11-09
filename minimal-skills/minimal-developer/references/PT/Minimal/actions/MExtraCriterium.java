package PT.Minimal.actions;

import PT.Minimal.geral.MBrowseEdit;
import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MMData;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MTextFieldEditingPolicy;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.io.PrintStream;
import java.util.Vector;






public class MExtraCriterium
  implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  private boolean isValid = true;
  








  public MExtraCriterium() {}
  








  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  








  public void setMForm(MForm wform)
  {
    minForm = wform;
  }
  








  public void setRows(Vector wv)
  {
    wrows = wv;
  }
  







  public void execute()
    throws MError
  {
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0) && 
      (minCommand.getCallerArgs().startsWith("CANCEL"))) {
      System.out.println(minCommand.getCallerArgs());
      cancelOperation(minForm);
      isValid = false;
    }
    
    if (isValid) {
      if (minForm.getFase() == 1) {
        show_criterium_row();
      }
      if (minForm.getFase() == 2) {
        do_criterium_row();
      }
    }
  }
  







  public void show_criterium_row()
    throws MError
  {
    Component wcomp = minForm.getFormWindow().getFocusOwner();
    
    MForm wfmc = MForm.getMForm(minForm.getEnt() + "MEC");
    try
    {
      if (wfmc == null) {
        wfmc = new MForm(minForm.getEnt() + "MEC");
        wfmc.setEnt(minForm.getEnt());
        wfmc.setEntName(minForm.getEntName());
        


        wfmc.setCenterPanel(MForm.getPanel("ZMCR0ME"));
        wfmc.setBotPanel(wfmc.constrBotPanel(2));
        wfmc.showStatusBar(true);
        MButton wok = (MButton)MUtil.getComponent(wfmc.getBotPanel(), "wok");
        
        wfmc.setOKButton(wok);
        

        wfmc.setENDButton(wok);
        wfmc.prepareForm();
        
        MFrame wframe = new MFrame();
        

        wframe.setForm(wfmc);
        wframe.pack();
        wframe.setSize(wfmc.getPreferredSize());
        wframe.center(Math.max(getPreferredSizewidth, 525), Math.max(getPreferredSizeheight, 350), false);
        





        mpnut.put(wfmc.getName(), wfmc);
        
        mpnut.put(wfmc.getName(), wfmc);
        
        wfmc.setRow(minForm.getRow());
        wfmc.getMFrame().setTitle(minForm.getEntName() + " - " + minCommand.getMenuText());
        prepareExtraCrit(wfmc);
      }
      

      MFrame.setModalParent(minForm.getMFrame(), wfmc.getMFrame());
      
      wfmc.setAction(minCommand.getName());
      wfmc.setActionObject(minForm);
      wfmc.setFase(2);
      

      mpnut.setForm(wfmc);
      mpnut.evalFormPnut(wfmc, "BF");
    }
    catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
    wfmc.showForm(false, false);
    
    MBrowseEdit wbrc = (MBrowseEdit)wfmc.getFormComponent("ZMCR02CBR");
    
    int wrownumber = 1;
    
    if (wcomp != null) {
      String watrib = wcomp.getName();
      
      if (watrib.endsWith("_z")) {
        watrib = watrib.substring(0, watrib.indexOf("_z"));
      }
      System.out.println(watrib);
      Vector wv = wbrc.getMRows();
      
      for (int i = 0; i < wv.size(); i++) {
        MRow wrow = (MRow)wv.elementAt(i);
        String wratrib = (String)wrow.getField("iatrib");
        
        System.out.println(wratrib);
        if (watrib.equals(wratrib)) {
          wrownumber = i + 1;
          break;
        }
      }
    }
    

    wbrc.clearSelection();
    wbrc.scrollToMRow(wrownumber);
    MTextFieldEditingPolicy wep = (MTextFieldEditingPolicy)wbrc.getColumnEditingPolicy(4);
    
    wep.setHandleClose(false);
    wbrc.openEditor(4, wrownumber);
    MUtil.requestFocus(wbrc);
  }
  








  public void do_criterium_row()
    throws MError
  {
    try
    {
      minForm.hideForm();
    } catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
  }
  









  public void cancelOperation(MForm wfmc)
    throws MError
  {
    wfmc.hideForm();
  }
  









  public void prepareExtraCrit(MForm wfmc)
    throws MError
  {
    Vector wrows = new Vector();
    MRow wyrow = MMData.getMRow("ZMCR");
    MRow worig = wfmc.getRow();
    
    String[] wcols = worig.getColNames();
    String[] wdescs = worig.getColDescs();
    
    MRow wrow = wyrow.Copy();
    
    wrow.set("iatrib", "SQL");
    wrow.set("natrib", "SQL Livre");
    wrow.set("coper", "*");
    wrow.set("tsql", "");
    wrows.addElement(wrow);
    
    for (int i = 0; i < wcols.length; i++) {
      if ((worig.getKey(wcols[i]).startsWith("A")) || (worig.getKey(wcols[i]).startsWith("K"))) {
        wrow = wyrow.Copy();
        wrow.set("iatrib", wcols[i]);
        wrow.set("natrib", wdescs[i]);
        wrow.set("coper", "=");
        wrow.set("tsql", "");
        wrows.addElement(wrow);
      }
    }
    

    wrow = wyrow.Copy();
    wrow.set("iatrib", "ORDER");
    wrow.set("natrib", "Ordenação");
    wrow.set("coper", "*");
    wrow.set("tsql", "");
    wrows.addElement(wrow);
    
    MBrowseEdit wbrc = (MBrowseEdit)wfmc.getFormComponent("ZMCR02CBR");
    
    wbrc.setRows(wrows);
    
    wbrc.setEditable(true);
    wbrc.allowsAdd(false);
  }
}
