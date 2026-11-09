package PT.Minimal.actions;

import PT.Minimal.geral.MBrowseEdit;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MResData;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import java.awt.Component;
import java.util.Vector;


























public class MClearForm
  implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private String wobj;
  private String wobjname;
  private String[] wargs;
  
  public MClearForm() {}
  
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
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0)) {
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0)) {
      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    minForm.clearFormValues();
    
    MForm wfmc = MForm.getMForm(minForm.getEnt() + "MEC");
    
    if (wfmc != null) {
      Component wcomp = wfmc.getFormComponent("ZMCR02CBR");
      
      if (wcomp != null) {
        MBrowseEdit wbrc = (MBrowseEdit)wcomp;
        Vector wv = wbrc.getMRows();
        
        for (int i = 0; i < wv.size(); i++) {
          MRow wrow = (MRow)wv.elementAt(i);
          
          wrow.setField("tsql", "");
        }
        wbrc.damageAll();
      }
    }
    MResData.setCritExt(minForm.getEnt(), "");
  }
}
