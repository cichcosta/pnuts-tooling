package PT.Minimal.geral;

import PT.Minimal.pnuts.;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
























class MFocusListener
  extends FocusAdapter
{
  MFrame minFrame;
  MDialog minDialog;
  MForm minForm;
  FocusEvent wFocusEvent;
  String minCommand;
  MTextField wtf;
  MBTextField wbtf;
  MTextArea wta;
  
  public MFocusListener(MFrame wframe)
  {
    minFrame = wframe;
  }
  









  public MFocusListener(MForm wform)
  {
    minForm = wform;
  }
  









  public MFocusListener(MDialog wdialog)
  {
    minDialog = wdialog;
  }
  










  public void focusGained(FocusEvent e)
  {
    if (e.isTemporary())
    {

      return;
    }
    


    wFocusEvent = e;
    boolean isSelected = false;
    












    if ((e.getComponent() instanceof MTextField)) {
      try
      {
        wtf = ((MTextField)e.getComponent());
        


        if (wtf.getSelectionEnd() > 0) {
          isSelected = true;
        }
        minForm.getStatusBar().setPaneText(0, ((MTextField)e.getSource()).getMsg());
        
        if ((wtf.getMask().startsWith("N")) && (wtf.getText().trim().length() != 0)) {
          wtf.setText(MUtil.getNumber(wtf.getText()));
          if (isSelected) {
            wtf.selectAll();
          }
        }
        wtf.selectAll();
        
        if (.isMPnut(minForm.getEnt())) {
           mpnut = .getMPnut(minForm.getEnt());
          
          mpnut.evalFieldPnut(minForm, wtf.getName(), "BF");
        }
      }
      catch (MError ex)
      {
        MUtil.showLater(ex, minForm);
      }
      catch (RuntimeException ex) {
        MUtil.showLater(new MError(ex), minForm);
      }
    }
    

    if ((e.getComponent() instanceof MTextArea)) {
      try
      {
        wta = ((MTextArea)e.getComponent());
        if (wta.getSelectionEnd() > 0) {
          isSelected = true;
        }
        minForm.getStatusBar().setPaneText(0, ((MTextArea)e.getSource()).getMsg());
        
        if ((wta.getMask().startsWith("N")) && (wta.getText().trim().length() != 0)) {
          wta.setText(MUtil.getNumber(wta.getText()));
          if (isSelected) {
            wta.selectAll();
          }
        }
        

        if (.isMPnut(minForm.getEnt())) {
           mpnut = .getMPnut(minForm.getEnt());
          
          mpnut.evalFieldPnut(minForm, wta.getName(), "BF");
        }
      }
      catch (MError ex)
      {
        MUtil.showLater(ex, minForm);
      }
      catch (RuntimeException ex) {
        MUtil.showLater(new MError(ex), minForm);
      }
    }
  }
  










  public void focusLost(FocusEvent e)
  {
    if (e.isTemporary())
    {

      return;
    }
    


    wFocusEvent = e;
    if ((e.getComponent() instanceof MTextField))
    {
      wtf = ((MTextField)e.getComponent());
      try
      {
        wtf.setSelectionStart(0);
        wtf.setSelectionEnd(0);
        



        minForm.setFormValue(wtf.getName(), wtf.getText(), wtf.getAtribType());
        
        if (.isMPnut(minForm.getEnt())) {
           mpnut = .getMPnut(minForm.getEnt());
          
          mpnut.evalFieldPnut(minForm, wtf.getName(), "AF");
        }
      } catch (MError ex) {
        MUtil.showLater(ex, minForm);

      }
      catch (RuntimeException ex)
      {
        MUtil.showLater(new MError(ex), minForm);
      }
    }
    
    if ((e.getComponent() instanceof MTextArea))
    {
      wta = ((MTextArea)e.getComponent());
      try
      {
        wta.setSelectionStart(0);
        wta.setSelectionEnd(0);
        



        minForm.setFormValue(wta.getName(), wta.getText(), wta.getAtribType());
        
        if (.isMPnut(minForm.getEnt())) {
           mpnut = .getMPnut(minForm.getEnt());
          
          mpnut.evalFieldPnut(minForm, wtf.getName(), "AF");
        }
      } catch (MError ex) {
        MUtil.showLater(ex, minForm);

      }
      catch (RuntimeException ex)
      {
        MUtil.showLater(new MError(ex), minForm);
      }
    }
  }
  












  public void close()
  {
    minForm = null;
    minFrame = null;
    minDialog = null;
    minCommand = null;
    wFocusEvent = null;
    wtf = null;
  }
}
