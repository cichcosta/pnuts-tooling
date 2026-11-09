package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.util.Vector;

public class MValidateForm implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  
  public MValidateForm() {}
  
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
    do_validate_form();
  }
  
  public void do_validate_form() throws MError {
    MUtil.validate(minForm, "Force");
    minForm.setFormValues(MUtil.expand(minForm, "Force", minForm.getFormValues()));
     mpnut = .getMPnut(minForm.getEnt());
    mpnut.put(minForm.getName(), minForm);
    mpnut.evalFormPnut(minForm, "AF");
    
    MUtil.MoveRecord(minForm.getFormValues(), minForm.getRow().getRecord());
    minForm.hideForm();
  }
}
