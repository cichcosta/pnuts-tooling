package PT.Minimal.actions;

import PT.Minimal.geral.MDialog;
import PT.Minimal.geral.MExp;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MHashtable;
import PT.Minimal.geral.MTextComponent;
import PT.Minimal.pnuts.;
import java.util.Vector;

public class MPrompt implements MExternalCommand
{
  PT.Minimal.geral.MCommand minCommand;
  MForm minForm;
  Vector wrows;
   mpnut;
  
  public MPrompt() {}
  
  public void setMCommand(PT.Minimal.geral.MCommand wcmd)
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
    System.out.println("!*** MPromp MPrompt: " + minForm.getFase());
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if (minForm.getFase() != 51) {
      show_prompt_row();
    }
    if (minForm.getFase() == 51) {
      do_prompt_row();
    }
  }
  
  public void show_prompt_row() throws PT.Minimal.geral.MError {
    System.out.println("!*** MPrompt show_prompt_row");
    
    MHashtable fzemc = null;
    try
    {
      MTextComponent wtf = null;
      mpnut.setForm(minForm);
      mpnut.setRow(minForm.getRow());
      java.awt.Component wcomp = minForm.getFormWindow().getFocusOwner();
      if (wcomp == null) {
        return;
      }
      


      MExp[] wvexp = MForm.getExp(minForm.getEnt(), wcomp.getName(), "P1");
      
      if ((wvexp == null) && ((wcomp instanceof MTextComponent))) {
        wtf = (MTextComponent)wcomp;
        if ((wtf instanceof PT.Minimal.geral.MBTextField)) {
          PT.Minimal.geral.MTextFieldEditingPolicy wep = ((PT.Minimal.geral.MBTextField)wtf).getMTextFieldEditingPolicy();
          wep.setHandleClose(false);
          wep.setCanClose(false);
        }
        if ((wtf instanceof PT.Minimal.geral.MTextField)) {
          PT.Minimal.geral.MTextField mtf = (PT.Minimal.geral.MTextField)wcomp;
          if (!mtf.isEditable()) {
            return;
          }
        }
        System.out.println("!*** MPrompt wtf.getPromptCode()" + wtf.getPromptCode());
        
        if (wtf.getPromptCode().trim().length() != 0) {
          System.out.println("!*** MPrompt wtf.getPromptTable()" + wtf.getPromptTable());
          wvexp = PT.Minimal.geral.MMData.getExpCopy(wtf.getPromptCode(), wtf.getPromptTable(), "P1");
          
          if (wvexp != null) {
            fzemc = new MHashtable();
            mpnut.put("FZEMC", fzemc);
            mpnut.set("FZEMC", fzemc);
            
            fzemc.put("iexs", wtf.getPromptCode());
            fzemc.put("ictx", wtf.getPromptTable());
            fzemc.put("iob", wtf.getName());
            fzemc.put("nargs", wtf.getPromptArgs());
            boolean wzk = false;
            if (wtf.getExpansionField().trim().length() != 0)
            {
              String[] wargs = PT.Minimal.geral.MUtil.getArgs(wtf.getExpansionField().toLowerCase(), ',');
              for (int i = 0; i < wargs.length; i++) {
                if (wargs[i].startsWith("zk")) {
                  fzemc.put("nzk", wargs[i]);
                  wzk = true;
                }
                else if (i == 0) {
                  fzemc.put("niob", wargs[i]);
                } else {
                  fzemc.put("niob" + i, wargs[i]);
                  fzemc.put("valiob" + i, "[m." + wargs[i] + "]");
                }
              }
            }
            else {
              fzemc.put("niob", "Xnctx");
            }
            if (!wzk) {
              fzemc.put("nzk", "zkXnctx");
            }
            fzemc.put("nctx", PT.Minimal.geral.MMData.getCodeTableName(wtf.getPromptTable()));
            
            for (int k = 0; k < wvexp.length; k++) {
              MExp wexp = wvexp[k];
              
              wexp.setTextArgs(mpnut.evalFzemcValues(wexp.getTextArgs()));
              wexp.setSelect(mpnut.evalFzemcValues(wexp.getSelect()));
              wexp.setWhere(mpnut.evalFzemcValues(wexp.getWhere()));
              
              wexp.setObj(minForm.getEnt());
              wexp.setAtrib(wtf.getName());
              wexp.setTexp("P1");
            }
          }
        }
      }
      





      if (wvexp != null) {
        boolean bfound = false;
        for (int k = 0; k < wvexp.length; k++) {
          MExp wexp = wvexp[k];
          if (mpnut.evalCondition(wexp.getCondition())) {
            bfound = true;
            PT.Minimal.geral.MQuery mq = new PT.Minimal.geral.MQuery();
            MForm wfmc = null;
            String wobjass = wexp.getAssociateObject().trim();
            String wftitle = wobjass;
            String[] wexp_args = wexp.getArgs(mpnut.evalValues(wexp.getTextArgs()));
            if (wexp_args.length > 0) {
              wftitle = wexp_args[0];
            }
            
            if ((wobjass != null) && (wobjass.length() != 0))
            {
              wfmc = null;
              if (wfmc == null) {
                wfmc = new MForm(wobjass);
                wfmc.setEnt(minForm.getEnt());
                wfmc.setCenterPanel(MForm.getPanel(wobjass, "MC"));
                MDialog wd = new MDialog(minForm.getMFrame(), "Ajuda", true, wfmc, 3);
                wd.pack();
                wd.setSize(wfmc.getPreferredSize());
                wd.setCentered();
                wfmc.setRow(minForm.getRow());
              }
              
              wfmc.setFormValues(minForm.getFormValues());
              
              System.out.println("!*** MPrompt eval BF: " + wobjass);
              mpnut.put(wobjass, wfmc);
              mpnut.evalFormPnut(minForm, wobjass, "BF");
              PT.Minimal.geral.MResData.setPromptForm(wobjass);
              
              mpnut.remove(wobjass);
              wfmc.showForm();
              
              java.awt.Component[] wcomponents = wfmc.getCenterPanel().getComponents();
              for (int j = 0; j < wcomponents.length; j++) {
                if (((wcomponents[j] instanceof MTextComponent)) && 
                  (!wcomponents[j].getName().endsWith("_z"))) {
                  java.awt.Component wc = PT.Minimal.geral.MUtil.getComponent(minForm.getCenterPanel(), wcomponents[j].getName() + "_z");
                  String wcname = wcomponents[j].getName();
                  char wlc = wcname.charAt(wcname.length() - 1);
                  if (Character.isDigit(wlc)) {
                    wcname = wcname.substring(0, wcname.length() - 1);
                  }
                  System.out.println("!*** MPromp Comps: a." + wcname + " / " + ((MTextComponent)wcomponents[j]).getDataType() + " / " + ((MTextComponent)wcomponents[j]).getUnFormatedText() + " ");
                  if (wc == null) {
                    if ((!wcname.startsWith("m")) && (!wcname.startsWith("q")) && (!wcname.startsWith("p"))) {
                      mq.addWhere("a." + wcname, ((MTextComponent)wcomponents[j]).getDataType(), ((MTextComponent)wcomponents[j]).getUnFormatedText());
                    } else {
                      mq.addWhere("a." + wcname, "NUMTYPE", ((MTextComponent)wcomponents[j]).getUnFormatedText());
                    }
                  }
                  else if ((!wcname.startsWith("m")) && (!wcname.startsWith("q")) && (!wcname.startsWith("p"))) {
                    mq.addWhere("a." + wcname, ((MTextComponent)wcomponents[j]).getDataType(), ((MTextComponent)wcomponents[j]).getUnFormatedText(), ((MTextComponent)wc).getUnFormatedText());
                  } else {
                    mq.addWhere("a." + wcname, "NUMTYPE", ((MTextComponent)wcomponents[j]).getUnFormatedText(), ((MTextComponent)wc).getUnFormatedText());
                  }
                }
              }
            }
            

            if ((wobjass.length() != 0) && ((wobjass.length() == 0) || (getFormParentbuttonChoosed != 2))) {
              break;
            }
            


            mq.addSelect(wexp.getSelect());
            mq.addFrom(wexp.getFrom());
            mq.addWhere(wexp.getWhere());
            mq.addGroup(wexp.getGroup());
            mq.addOrder(wexp.getOrder());
            String wcsel = mq.getQuery();
            wcsel = mpnut.evalValues(wcsel);
            System.out.println("!*** MPrompt wcsel: " + wcsel);
            Vector wv = PT.Minimal.geral.MUtil.getRows(wcsel, 999, true);
            if (wv.size() == 0) {
              throw new PT.Minimal.geral.MError("MIN-000111W");
            }
            if (wv.size() > 0) {
              PT.Minimal.geral.MPanel wp = new PT.Minimal.geral.MPanel(minForm.getEnt() + "P1");
              wp.setLayout(new java.awt.BorderLayout());
              PT.Minimal.geral.MBrowse wbr = new PT.Minimal.geral.MBrowse();
              wbr.setReadOnly();
              wbr.setType("PROMPT");
              System.out.println("!*** MPrompt Nr Colunas: " + ((PT.Minimal.geral.MRow)wv.elementAt(0)).getColNames().length);
              String[] wwzemc = ((PT.Minimal.geral.MRow)wv.elementAt(0)).getColNames();
              Vector wvcoldef = new Vector();
              for (int i = 0; i < wwzemc.length; i++) {
                if (wexp_args.length > i + 1) {
                  if (!wexp_args[(i + 1)].trim().startsWith("HIDE")) {
                    System.out.println("!*** MPrompt cols: " + ((PT.Minimal.geral.MRow)wv.elementAt(0)).getColName(i));
                    wvcoldef.addElement(new PT.Minimal.geral.MBrowseColumn(wwzemc[i], PT.Minimal.geral.MUtil.stringRep(wexp_args[(i + 1)], "\\n", "\n"), i));
                  }
                } else {
                  wvcoldef.addElement(new PT.Minimal.geral.MBrowseColumn(wwzemc[i], String.valueOf(i + 1), i));
                }
              }
              

              PT.Minimal.geral.MBrowseColumn[] wcoldef = new PT.Minimal.geral.MBrowseColumn[wvcoldef.size()];
              wvcoldef.copyInto(wcoldef);
              
              wbr.constrBrowse(wcoldef);
              wp.add("Center", wbr);
              wbr.setName(minForm.getEnt() + "P1");
              wbr.setEnt(minForm.getEnt());
              System.out.println("!*** MPrompt getEnt: " + minForm.getEnt());
              
              wbr.setRows(wv);
              
              MDialog wd = new MDialog(minForm.getMFrame(), PT.Minimal.geral.MUtil.lang("\"Ajuda - \"") + wftitle, true, wp, 3, "Prompt");
              getFormlastContainer = wcomp.getParent();
              wd.getForm().setActionObject(wcomp);
              wd.getForm().setAction(minCommand.getName());
              wd.getForm().setEnt(minForm.getEnt());
              wd.getForm().setFase(51);
              wd.pack();
              if (wwzemc.length < 4) {
                wd.setSize(new java.awt.Dimension(425, 330));
                wd.setCentered(new java.awt.Dimension(425, 330));
              } else {
                wd.setSize(new java.awt.Dimension(700, 320));
                wd.setCentered(new java.awt.Dimension(700, 320));
              }
              








              wd.getForm().showForm();
            }
            
            break;
          }
        }
        
        if (!bfound) {
          throw new PT.Minimal.geral.MError("Não foi encontrada expressão P1 válida...");
        }
      }
    } finally {
      if (fzemc != null) {
        fzemc.clear();
        mpnut.remove("FZEMC");
        mpnut.set("FZEMC", null);
      }
    }
    wrows = null;
  }
  
  public void do_prompt_row() throws PT.Minimal.geral.MError
  {
    System.out.println("!*** MPrompt do_prompt_row: " + minForm.lastContainer + " " + minForm.getEnt());
    Vector wv = minForm.getBrowse().getSelectedRowObjectVector();
    if (wv.size() > 0)
    {
      java.awt.Component wc = (java.awt.Component)minForm.getActionObject();
      if ((wc instanceof PT.Minimal.geral.MBTextField)) {
        MTextComponent wtf = (MTextComponent)wc;
        String ws = (String)((PT.Minimal.geral.MRow)wv.elementAt(0)).get(wtf.getName());
        wtf.setText(ws);
        if ((wtf instanceof PT.Minimal.geral.MBTextField)) {
          PT.Minimal.geral.MTextFieldEditingPolicy wep = ((PT.Minimal.geral.MBTextField)wtf).getMTextFieldEditingPolicy();
          wep.setCanClose(true);
        }
      }
      if ((wc instanceof PT.Minimal.geral.MTextField)) {
        MTextComponent wtf = (MTextComponent)wc;
        PT.Minimal.geral.MUtil.MoveRecord(((PT.Minimal.geral.MRow)wv.elementAt(0)).getRecord(), minForm.lastContainer);
        ((MTextComponent)minForm.getActionObject()).setChanged(false);
      }
      if ((wc instanceof PT.Minimal.geral.MButton))
      {
        PT.Minimal.geral.MUtil.MoveRecord(((PT.Minimal.geral.MRow)wv.elementAt(0)).getRecord(), minForm.lastContainer);
      }
    }
    

    String wobjass = PT.Minimal.geral.MResData.getPromptForm();
    if ((wobjass != null) && (wobjass.length() != 0)) {
      mpnut.setRow((PT.Minimal.geral.MRow)wv.elementAt(0));
      mpnut.evalFormPnut(minForm, wobjass, "AF");
      mpnut.remove(wobjass);
    }
    
    minForm.hideForm();
    minForm.getBrowse().removeAll();
  }
}
