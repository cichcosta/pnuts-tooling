package PT.Minimal.actions;

import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;
import java.awt.Window;
import java.util.Vector;

public class MJumpMRFC implements MExternalCommand
{
  MCommand minCommand;
  MForm minForm;
  Vector wrows;
  private String went;
  private String wentname;
  private String[] wargs;
  
  public MJumpMRFC() {}
  
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
    wargs = PT.Minimal.geral.MUtil.getArgs(minCommand.getCallerArgs(), ' ');
    System.out.println(wargs.length);
    
    System.out.println(minCommand.getCallerArgs());
    if ((PT.Minimal.geral.MLogin.hasLoginProperty("JMETADATAMODE")) && ((PT.Minimal.geral.MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILE")) || (PT.Minimal.geral.MLogin.getLoginProperty("JMETADATAMODE").equals("METAFILEALL"))))
    {
      return; }
    if (wargs.length < 1) {
      throw new MError("Não existe opcão indicada ...");
    }
    if (minForm.getEnt().startsWith("MRFC")) {
      return;
    }
    went = "MRFC0";
    wentname = "Atributos Runtime";
    if (minCommand.getName().equals("JUMPMRFC")) {
      do_JumpMRFC();
    }
  }
  
  public void do_JumpMRFC() throws MError {
    try {
      PT.Minimal.geral.MExp[] wvexp = PT.Minimal.geral.MMData.getExp("JUMPMRFC", "*", "S1");
      boolean bfound = false;
      if (wvexp != null) {
        for (int i = 0; i < wvexp.length; i++) {
          PT.Minimal.geral.MExp wexp = wvexp[i];
          if (PT.Minimal.pnuts..getMPnut(minForm.getEnt()).evalCondition(wexp.getCondition())) {
            MRow wr = minForm.getRow().Copy();
            
            wr.getRecord().put("i_ob", ((Window)minForm.getFormParent()).getFocusOwner().getParent().getName().toUpperCase());
            wr.getRecord().put("i_comp", ((Window)minForm.getFormParent()).getFocusOwner().getName());
            PT.Minimal.geral.MUtil.MoveRecord(PT.Minimal.geral.MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
            PT.Minimal.geral.MinSaltoME wms = new PT.Minimal.geral.MinSaltoME(went, wentname, minForm, wr, wexp, "INQUIRY_ROW");
            
            bfound = true;
            break;
          }
        }
      }
      if (!bfound) {
        throw new MError("Não existe expressão S1 válida...");
      }
    } catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
    
    wrows = null;
  }
}
