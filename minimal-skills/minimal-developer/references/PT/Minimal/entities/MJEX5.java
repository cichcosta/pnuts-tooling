package PT.Minimal.entities;

import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MDialog;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MRow;

public class MJEX5 implements PT.Minimal.actions.MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  MForm minForm;
  java.util.Vector wrows;
  
  public MJEX5() {}
  
  public void setMCommand(PT.Minimal.geral.MCommand wcmd)
  {
    minCommand = wcmd;
  }
  
  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(java.util.Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws PT.Minimal.geral.MError {
    System.out.println("Entrou execute: " + minCommand.getName());
    if ((minCommand.getName().equals("SAVE_EXP")) && (minForm.getFase() != 71)) {
      if (wrows.size() == 0) {
        throw new PT.Minimal.geral.MError("MIN-000103W");
      }
      
      showJExps();
    }
    if ((minCommand.getName().equals("SAVE_EXP")) && (minForm.getFase() == 71)) {
      saveJExps();
    }
  }
  
  private void showJExps() throws PT.Minimal.geral.MError {
    if (wrows.size() > 0) {
      try {
        for (int j = 0; j < wrows.size(); j++) {
          java.awt.TextArea minExps = new java.awt.TextArea(getAssociatedText((MRow)wrows.elementAt(j)));
          minExps.setName("minExps");
          
          MDialog wd = new MDialog(minForm.getMFrame(), "Expressoes - " + ((MRow)wrows.elementAt(j)).getEnt().substring(0, 4) + ((MRow)wrows.elementAt(j)).getKeyString(), true, 3, minExps);
          

          wd.getForm().setEnt(minForm.getEnt());
          wd.getForm().setAction(minCommand.getName());
          wd.getForm().setFase(71);
          wd.getForm().setRow((MRow)wrows.elementAt(j));
          wd.pack();
          wd.setSize(wd.getPreferredSize());
          wd.setCentered();
          wd.setVisible(true);
        }
      } catch (PT.Minimal.geral.MError err) {
        err.show(new java.awt.Frame());
      }
    }
  }
  
  private void saveJExps() throws PT.Minimal.geral.MError {
    java.awt.Component wc = PT.Minimal.geral.MUtil.getComponent(minForm.getCenterPanel(), "minExps");
    if (wc != null) {
      setAssociatedText(minForm.getRow(), ((java.awt.TextArea)wc).getText());
    }
    ((MDialog)minForm.getParent()).setVisible(false);
  }
  
  private String getAssociatedText(MRow wrow) throws PT.Minimal.geral.MError {
    int wzk = Integer.valueOf((String)wrow.getField("zk")).intValue();
    String ws = " ";
    
    String wcsel = " select zobj from egbot0 a  where a.zent = '" + wrow.getEnt().substring(0, 4) + "'" + " and a.zkent = " + wzk;
    





    MCursor egbo_curs = PT.Minimal.geral.MConnection.getMCursor();
    egbo_curs.execQuery(wcsel);
    if (egbo_curs.getWdcode() == 0) {
      byte[] wobj = egbo_curs.getBytes("zobj");
      ws = new String(wobj);
    }
    egbo_curs.close();
    return ws;
  }
  
  private void setAssociatedText(MRow wrow, String wtext) throws PT.Minimal.geral.MError {
    int wzk = Integer.valueOf((String)wrow.getField("zk")).intValue();
    String wkey = ((String)wrow.getField("i_ob")).trim() + ((String)wrow.getField("i_tc")).trim() + ((String)wrow.getField("i_comp")).trim() + ((String)wrow.getField("i_fase")).trim();
    



    String wcsel = " delete from egbot0   where zent = '" + wrow.getEnt().substring(0, 4) + "'" + " and zk_zent = " + wzk;
    




    MCursor text_curs = PT.Minimal.geral.MConnection.getMCursor();
    text_curs.execStatement(wcsel);
    text_curs.closeStatement();
    
    wcsel = " insert into egbot0 (egboi, egboct, egbon, zobj, zent, zkent)  values ('" + wrow.getEnt().substring(0, 4) + wrow.getKeyString() + "' ,'TXT',?,?,?,?) ";
    




    text_curs.prepareStatement(wcsel);
    















    text_curs.setPreparedPar(1, wkey);
    text_curs.setPreparedPar(2, wtext.getBytes());
    text_curs.setPreparedPar(3, wrow.getEnt().substring(0, 4));
    text_curs.setPreparedPar(4, wzk);
    
    long nrows = text_curs.execPreparedUpdate();
    text_curs.closePreparedStatement();
    if (nrows == 0L) {
      throw new PT.Minimal.geral.MError("MIN-000100E");
    }
  }
}
