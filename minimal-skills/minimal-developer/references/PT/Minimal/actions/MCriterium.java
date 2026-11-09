package PT.Minimal.actions;

import PT.Minimal.geral.MBrowse;
import PT.Minimal.geral.MBrowseEdit;
import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MExp;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MQuery;
import PT.Minimal.geral.MResData;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MTextField;
import PT.Minimal.geral.MUtil;
import PT.Minimal.pnuts.;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MCriterium implements MExternalCommand
{
  String sOrigem = ""; String wklc = "klc"; String wklc2 = "CLK";
  
  private boolean isValid = true;
  MCommand minCommand;
  
  public MCriterium() { sOrigem = ""; }
  
  MForm minForm;
  
  public MCriterium(String s) { sOrigem = s; }
  
  Vector wrows;
   mpnut;
  public void setMCommand(MCommand wcmd) { minCommand = wcmd; }
  

  public void setMForm(MForm wform) {
    minForm = wform;
  }
  
  public void setRows(Vector wv) {
    wrows = wv;
  }
  
  public void execute() throws MError {
    if (mpnut == null) {
      mpnut = .getMPnut(minForm.getEnt());
    }
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      if (minCommand.getCallerArgs().startsWith("CANCEL"))
      {
        cancelOperation(minForm);
        isValid = false;
      }
    }
    if (isValid) {
      if ((minCommand.getName().equals("CRITERIUM_ROW")) && (minForm.getFase() == 0)) {
        show_criterium_row();
      }
      if ((minCommand.getName().equals("CRITERIUM_ROW")) && (minForm.getFase() == 1)) {
        do_criterium_row();
      }
      if (minCommand.getName().equals("PRINT_CRIT")) {
        print();
      }
    }
  }
  
  public void show_criterium_row() throws MError
  {
    MForm wfmc = MForm.getMForm(minForm.getEnt() + "MC");
    try {
      if (wfmc == null)
      {
        wfmc = new MForm(minForm.getEnt() + "MC");
        wfmc.setEnt(minForm.getEnt());
        wfmc.setEntName(minForm.getEntName());
        wfmc.addToolBar((PT.Minimal.geral.MToolBar)MForm.getPanel("ToolBar Criterios"));
        wfmc.setTopPanel(MForm.getPanel(minForm.getEnt() + "MCTP"));
        wfmc.setCenterPanel(MForm.getPanel(minForm.getEnt(), "MC"));
        wfmc.setBotPanel(MForm.getPanel(minForm.getEnt() + "MCBT"));
        wfmc.showStatusBar(true);
        MButton wok = (MButton)MUtil.getComponent(wfmc.getBotPanel(), "wok");
        wfmc.setOKButton(wok);
        MButton wend = (MButton)MUtil.getComponent(wfmc.getBotPanel(), "wend");
        wfmc.setENDButton(wend);
        javax.swing.JMenuBar wmenubar = PT.Minimal.geral.MMenu.getMinMenuBar(wfmc.getEnt() + "MC");
        if (wmenubar != null) {
          wfmc.addMenuBar(wmenubar);
        }
        wfmc.prepareForm();
        if (wfmc.getFormComponent("iorg") != null) {
          wfmc.setFormValue("iorg", PT.Minimal.geral.MLogin.getUserOrg());
        }
        MFrame wframe = new MFrame();
        wframe.setTitle(minForm.getEntName() + " - " + minCommand.getMenuText());
        
        wframe.setForm(wfmc);
        wframe.pack();
        wframe.setSize(wfmc.getPreferredSize());
        wframe.center(getPreferredSizewidth, getPreferredSizeheight, false);
        
        System.out.println("!*** MCriterium criterium_row: 6" + minNome);
        wend.setCommand(minCommand.getName());
        System.out.println("!*** MCriterium criterium_row: 7");
        wend.setCommandArgs("CANCEL");
        System.out.println("!*** MCriterium criterium_row: 7.1");
        mpnut.put(wfmc.getName(), wfmc);
      }
      

      System.out.println("!*** MCriterium criterium_row: 7.2");
      if (minForm.getBrowse() != null) {
        wfmc.setBrowse(minForm.getBrowse());
        minForm.getBrowse().setEnt(minForm.getEnt());
      }
      System.out.println("!*** MCriterium criterium_row: 8");
      wfmc.setCritRecord(MUtil.getRecord(wfmc.getCenterPanel()));
      wfmc.setRow(minForm.getRow());
      
      System.out.println("!*** MCriterium criterium_row: 9");
      wfmc.getMFrame().setTitle(minForm.getEntName() + " - " + minCommand.getMenuText());
      wfmc.setAction(minCommand.getName());
      
      wfmc.setFase(1);
      System.out.println("!*** MCriterium criterium_row: 10");
      MFrame.setModalParent(minForm.getMFrame(), wfmc.getMFrame());
      mpnut.setForm(wfmc);
      minForm.setCurrencySelectedButton(wfmc.getRow().getCurrency());
      System.out.println("!*** MCriterium Currency 1: " + wfmc.getRow().getCurrency());
      mpnut.evalFormPnut(wfmc, "BF");
      System.out.println("!*** MCriterium Currency 2: " + wfmc.getRow().getCurrency());
      System.out.println("!*** MCriterium AF 4: ");
    } catch (MError ex) {
      ex.show(minForm.getMFrame());
    }
    wfmc.showForm();
  }
  
  public void do_criterium_row() throws MError {
    try {
      if (minForm.getBrowse() != null) {
        minForm.getBrowse().removeAll();
        
        Vector wvrows = prepareRowVector();
        minForm.getBrowse().setRows(wvrows);
        
        if (minForm.getBrowse().getNumberMRows() == 0) {
          throw new MError("MIN-000101W");
        }
        minForm.hideForm();
      }
    } catch (MError ex) { ex = 
      


        ex;ex.show(minForm.getMFrame());
    } finally {}
  }
  
  public void print() throws MError {
    try { if (minForm.getBrowse() != null) {
        minForm.getBrowse().removeAll();
        Vector wvrows = prepareRowVector();
        if (wvrows.size() == 0) {
          throw new MError("MIN-000101W");
        }
        if (minForm.getBrowse().isTotalized()) {
          MRow wtotal = new MRow((MRow)wvrows.elementAt(0));
          wtotal.setCst("st");
          minForm.getBrowse().calcTotal(wvrows, wtotal);
          wvrows.addElement(wtotal);
        }
        MCommand wdocrit = new MCommand(minForm.getEnt(), "PRINT_CRIT");
        wdocrit.setArgs(minCommand.getArgs());
        MPrintBrowse wprint = new MPrintBrowse();
        wprint.setMForm(minForm);
        wprint.setRows(wvrows);
        wprint.setMCommand(wdocrit);
        wprint.execute();
      }
    } catch (MError ex) {
      ex = 
      


        ex;ex.show(minForm.getMFrame());
    } finally {}
  }
  
  public Vector prepareRowVector() throws MError { Vector wvrows = null;
    try {
      mpnut.setForm(minForm);
      mpnut.setRow(null);
      mpnut.evalFormPnut(minForm, "AF");
      


      MQuery mq = new MQuery();
      MExp[] wvexp = MForm.getExp(minForm.getEnt(), "*", "BR");
      MExp wexp = null;
      if (wvexp != null) {
        boolean bfound = false;
        for (int i = 0; i < wvexp.length; i++) {
          wexp = wvexp[i];
          if (mpnut.evalCondition(wexp.getCondition())) {
            mq.addSelect(wexp.getSelect());
            mq.addFrom(wexp.getFrom());
            mq.addWhere(wexp.getWhere());
            mq.addGroup(wexp.getGroup());
            mq.addOrder(wexp.getOrder());
            bfound = true;
            break;
          }
        }
        if (!bfound) {
          throw new MError("Não existe expressão BR válida!");
        }
      } else {
        mq.addSelect("a.*");
        mq.addFrom(minForm.getRow().getTable() + " a");
      }
      wvexp = MForm.getExp(minForm.getEnt(), "*", "E5");
      if (wvexp != null) {
        boolean bfound = false;
        for (int i = 0; i < wvexp.length; i++) {
          MExp wexp1 = wvexp[i];
          if (mpnut.evalCondition(wexp1.getCondition())) {
            mq.addSelect(wexp1.getSelect());
            mq.addFrom(wexp1.getFrom());
            mq.addWhere(wexp1.getWhere());
            mq.addGroup(wexp1.getGroup());
            mq.addOrder(wexp1.getOrder());
            bfound = true;
            break;
          }
        }
        if (!bfound) {
          throw new MError("Não existe expressão E5 válida...");
        }
      }
      wvexp = MForm.getExp(minForm.getEnt(), "*", "SS");
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
      



      mpnut.evalFormPnut(minForm, "TC");
      Hashtable critRecord = minForm.getFormValues();
      MRow wcrow = minForm.getRow();
      
      for (Enumeration e = critRecord.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((!wkey.endsWith("_z")) && (wcrow.getRecord().containsKey(wkey))) {
          if (wcrow.getKey(wkey).startsWith("EX")) {
            mq.addWhereZkExtension(wcrow.getTable(), wkey, wcrow.getType(wkey), (String)critRecord.get(wkey));
          }
          if ((wcrow.getKey(wkey).startsWith("K")) || (wcrow.getKey(wkey).startsWith("A")))
          {
            if (critRecord.containsKey(wkey + "_z")) {
              mq.addWhere("a." + wkey, wcrow.getType(wkey), (String)critRecord.get(wkey), (String)critRecord.get(wkey + "_z"));
            } else {
              mq.addWhere("a." + wkey, wcrow.getType(wkey), (String)critRecord.get(wkey));
            }
          }
        }
      }
      
      Hashtable wqjoins = new Hashtable();
      Hashtable formExpansions = minForm.getFormExpansions();
      String woutertab = "";
      String wouteratrib = "";
      for (Enumeration e = formExpansions.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        if ((!wkey.endsWith("_z")) && 
        

          (((String)formExpansions.get(wkey)).trim().length() != 0))
        {

          MTextField wtf = (MTextField)minForm.getFormExpansion(wkey);
          if (wtf.getCommandArgs().trim().length() != 0)
          {

            if (wtf.getCommandArgs().indexOf(".") != -1) {
              woutertab = wtf.getCommandArgs().substring(0, wtf.getCommandArgs().indexOf("."));
              wouteratrib = wtf.getCommandArgs().substring(wtf.getCommandArgs().indexOf(".") + 1);
            } else {
              woutertab = wtf.getCommandArgs();
              wouteratrib = wkey;
            }
            








            woutertab = woutertab.toUpperCase();
            wouteratrib = wouteratrib.toUpperCase();
            
            if (!wqjoins.containsKey(woutertab)) {
              wvexp = MForm.getExp(minForm.getEnt(), woutertab, "JO");
              if (wvexp != null) {
                boolean bfound = false;
                MQuery mq1 = new MQuery();
                for (int i = 0; i < wvexp.length; i++) {
                  MExp wexp1 = wvexp[i];
                  if (mpnut.evalCondition(wexp1.getCondition())) {
                    mq1.addSelect(wexp1.getSelect());
                    mq1.addFrom(wexp1.getFrom());
                    mq1.addWhere(wexp1.getWhere());
                    bfound = true;
                    break;
                  }
                }
                if (!bfound) {
                  throw new MError("Não existe expressão JO válida...");
                }
                wqjoins.put(woutertab, mq1);
              }
            }
            if (wqjoins.containsKey(woutertab)) {
              MQuery mq1 = (MQuery)wqjoins.get(woutertab);
              if (formExpansions.containsKey(wkey + "_z")) {
                mq1.addWhere(woutertab + "." + wouteratrib, wtf.getDataType(), (String)formExpansions.get(wkey), (String)formExpansions.get(wkey + "_z"));
              } else
                mq1.addWhere(woutertab + "." + wouteratrib, wtf.getDataType(), (String)formExpansions.get(wkey));
            }
          }
        } }
      for (Enumeration e = wqjoins.keys(); e.hasMoreElements();) {
        String wkey = (String)e.nextElement();
        MQuery mq1 = (MQuery)wqjoins.get(wkey);
        mq.addWhere(mq1.getSelect() + " where " + mq1.getWhere() + ")");
      }
      




      MForm wfmc = MForm.getMForm(minForm.getEnt() + "MEC");
      String orderadic = "";
      
      if (wfmc != null) {
        java.awt.Component wcomp = wfmc.getFormComponent("ZMCR02CBR");
        if (wcomp != null) {
          MBrowseEdit wbrc = (MBrowseEdit)wcomp;
          Vector wv = wbrc.getMRows();
          for (int i = 0; i < wv.size(); i++) {
            MRow wrow = (MRow)wv.elementAt(i);
            String watrib = (String)wrow.getField("iatrib");
            String wcoper = (String)wrow.getField("coper");
            String wtsql = (String)wrow.getField("tsql");
            if (wtsql.trim().length() != 0) {
              if (watrib.equals("SQL")) {
                if (wtsql.trim().toLowerCase().startsWith("and ")) {
                  wtsql = wtsql.substring(3);
                }
                if (wtsql.trim().toLowerCase().startsWith("where ")) {
                  wtsql = wtsql.substring(5);
                }
                mq.addWhere(wtsql);
              }
              if ((!watrib.equals("SQL")) && (!watrib.equals("ORDER"))) {
                if ((wcoper.indexOf("=") != -1) || (wcoper.indexOf("<") != -1) || (wcoper.indexOf(">") != -1) || (wcoper.indexOf("LIKE") != -1))
                {


                  if ((wcrow.getType(watrib) == "TEXTTYPE") && 
                    (!wtsql.trim().startsWith("\"")) && (!wtsql.trim().startsWith("'")))
                  {
                    wtsql = "'" + wtsql + "'";
                  }
                  
                  mq.addWhere("a." + watrib + " " + wcoper + " " + wtsql);
                }
                if (wcoper.indexOf("IN") != -1) {
                  String[] winargs = MUtil.getArgs(wtsql, ",");
                  wtsql = "";
                  for (int j = 0; j < winargs.length; j++) {
                    if ((wcrow.getType(watrib) == "TEXTTYPE") && 
                      (!winargs[j].trim().startsWith("\"")) && (!winargs[j].trim().startsWith("'")))
                    {
                      winargs[j] = ("'" + winargs[j] + "'");
                    }
                    
                    wtsql = wtsql + "," + winargs[j];
                  }
                  mq.addWhere("a." + watrib + " " + wcoper + " (" + wtsql.substring(1) + ")");
                }
                if (wcoper.indexOf("BETWEEN") != -1) {
                  String[] winargs = MUtil.getArgs(wtsql, ",");
                  wtsql = "";
                  for (int j = 0; j < winargs.length; j++) {
                    if ((wcrow.getType(watrib) == "TEXTTYPE") && 
                      (!winargs[j].trim().startsWith("\"")) && (!winargs[j].trim().startsWith("'")))
                    {
                      winargs[j] = ("'" + winargs[j] + "'");
                    }
                  }
                  
                  if (winargs.length >= 2) {
                    mq.addWhere("a." + watrib + " " + wcoper + " " + winargs[0] + " and " + winargs[1]);
                  }
                }
              }
              
              if (watrib.equals("ORDER")) {
                orderadic = wtsql;
              }
            }
          }
        }
      }
      
      mq.addWhere(MResData.getCritExt(minForm.getEnt()));
      
      minForm.getBrowse().setMQuery(mq);
      MTextField wmax = (MTextField)MUtil.getComponent(minForm.getTopPanel(), "wmaxsel");
      int wcount = Integer.valueOf(wmax.getUnFormatedText()).intValue();
      mpnut.put("wquery", mq);
      mpnut.set("wquery", mq);
      mpnut.evalFormPnut(minForm, "BQ");
      
      if (orderadic.trim().length() > 0) {
        mq.setOrder(orderadic);
        mpnut.remove("wquery");
        mpnut.unset("wquery");
        mpnut.put("wquery", mq);
        mpnut.set("wquery", mq);
      }
      String wcsel = mpnut.evalValues(minForm.getBrowse().getMQuery().getQuery());
      
      MResData.setQueries(minForm.getEnt() + "MC", wcsel);
      
      if ((sOrigem.equals("BR")) && (MResData.getQueries(minForm.getEnt() + "BR").trim() != "")) {
        wcsel = MResData.getQueries(minForm.getEnt() + "BR");
        if (wcsel.trim() == "") {
          wcsel = MResData.getQueries(minForm.getEnt() + "MC");
          sOrigem = "MC";
        }
      }
      else {
        MResData.setQueries(minForm.getEnt() + "BR", "");
      }
      
      if (!MResData.getQueryTerc().trim().equals("")) {
        int i = wcsel.indexOf(" where ");
        if (i > -1) {
          wcsel = wcsel.substring(0, i + 7) + MResData.getQueryTerc() + " and " + wcsel.substring(i + 7);
        } else {
          i = wcsel.indexOf(" from ");
          i = wcsel.indexOf(" t0", i);
          wcsel = wcsel.substring(0, i + 3) + " where " + MResData.getQueryTerc() + " " + wcsel.substring(i + 3);
        }
      }
      
      System.out.println("!*** MCriterium wcsel: " + sOrigem + ": " + wcsel);
      
      if (sOrigem.equals("BR")) {
        wvrows = MUtil.getRows(minForm.getRow().Copy(), wcsel, 99999, true);
      } else {
        wvrows = MUtil.getRows(minForm.getRow().Copy(), wcsel, wcount, true);
      }
      

      if (wvrows.size() > 0) {
        mpnut.set("vrows", wvrows);
        mpnut.evalFormPnut(minForm, "AQ");
        if ((wexp != null) && 
          (wexp.getTextArgs() != null) && (wexp.getTextArgs().trim().length() != 0) && (wvrows.size() != 0)) {
          doBreakVector(wexp.getTextArgs(), wvrows);
        }
      }
      

      if ((!MResData.getQueries(wklc).trim().equals("")) && (!PT.Minimal.geral.MLogin.getLoginProperty(wklc2).equals(MResData.getQueries(wklc)))) {
        String w = "Licensa Inválida!";
        throw new MError(w);
      }
    }
    finally {
      if (mpnut != null) {
        mpnut.remove("wquery");
        mpnut.unset("wquery");
      }
    }
    return wvrows;
  }
  
  public void cancelOperation(MForm wfmc) throws MError {
    wfmc.hideForm();
  }
  
  public static void doBreakVector(String wtxt, Vector wvrows) {
    String wbreak = "";
    String wrowbreak = "";
    
    String[] wbreakargs = null;
    String[] wtotalargs = null;
    String[] woutargs = null;
    String[] wheader = null;
    double[] wtotals = null;
    
    int i1 = -1;
    int i2 = -1;
    int i3 = -1;
    int i4 = -1;
    
    i1 = wtxt.indexOf(")");
    if (i1 == -1) {
      return;
    }
    wbreakargs = MUtil.getArgs(wtxt.substring(1, i1), ',');
    i2 = wtxt.indexOf(")", i1 + 1);
    
    if (i2 != -1) {
      wtotalargs = MUtil.getArgs(wtxt.substring(i1 + 2, i2), ',');
      if ((wtotalargs != null) && (wtotalargs.length == 1) && (wtotalargs[0].equals("' '"))) {
        wtotalargs = null;
      }
      i3 = wtxt.indexOf(")", i2 + 1);
    }
    if (i3 != -1) {
      woutargs = MUtil.getArgs(wtxt.substring(i2 + 2, i3), ',');
      if ((woutargs != null) && (woutargs.length == 1) && (woutargs[0].equals("' '"))) {
        woutargs = null;
      }
      i4 = wtxt.indexOf(")", i3 + 1);
    }
    if (i4 != -1) {
      wheader = MUtil.getArgs(wtxt.substring(i3 + 2, i4), ',');
    }
    
    for (int i = 0; i < wbreakargs.length; i++) {
      wbreak = wbreak + ((MRow)wvrows.elementAt(0)).getField(wbreakargs[i]);
    }
    if (wtotalargs != null) {
      wtotals = new double[wtotalargs.length];
      for (int i = 0; i < wtotals.length; i++) {
        wtotals[i] = 0.0D;
      }
    }
    
    int j = 0;
    if ((j < wvrows.size()) && 
      (wheader != null)) {
      MRow wbrow = new MRow((MRow)wvrows.elementAt(0));
      wbrow.setCst("st");
      for (int z = 0; z + 1 < wheader.length; z += 2) {
        if (wheader[(z + 1)].startsWith("'")) {
          wbrow.setField(wheader[z], wheader[(z + 1)].substring(1, wheader[(z + 1)].length() - 1));
        } else {
          wbrow.setField(wheader[z], ((MRow)wvrows.elementAt(j)).getField(wheader[(z + 1)]));
        }
      }
      wvrows.insertElementAt(wbrow, j);
      j++;
    }
    for (; 
        
        j < wvrows.size(); j++) {
      wrowbreak = "";
      for (int i = 0; i < wbreakargs.length; i++) {
        wrowbreak = wrowbreak + ((MRow)wvrows.elementAt(j)).getField(wbreakargs[i]);
      }
      

      if (!wrowbreak.equals(wbreak)) {
        wbreak = wrowbreak;
        
        if ((wtotalargs != null) || (woutargs != null)) {
          MRow wbrow = new MRow((MRow)wvrows.elementAt(0));
          wbrow.setCst("st");
          if (wtotalargs != null) {
            for (int i = 0; i < wtotals.length; i++) {
              wbrow.setField(wtotalargs[i], new BigDecimal(wtotals[i]).toString());
              wtotals[i] = 0.0D;
            }
          }
          if (woutargs != null) {
            for (int i = 0; i + 1 < woutargs.length; i += 2) {
              if (woutargs[(i + 1)].startsWith("'")) {
                wbrow.setField(woutargs[i], woutargs[(i + 1)].substring(1, woutargs[(i + 1)].length() - 1));
              } else {
                wbrow.setField(woutargs[i], ((MRow)wvrows.elementAt(j - 1)).getField(woutargs[(i + 1)]));
              }
            }
          }
          wvrows.insertElementAt(wbrow, j);
          j++;
        }
        MRow wbrow = new MRow((MRow)wvrows.elementAt(0));
        wbrow.setCst("st");
        wvrows.insertElementAt(wbrow, j);
        j++;
        
        if (wheader != null) {
          wbrow = new MRow((MRow)wvrows.elementAt(0));
          wbrow.setCst("st");
          for (int z = 0; z + 1 < wheader.length; z += 2) {
            if (wheader[(z + 1)].startsWith("'")) {
              wbrow.setField(wheader[z], wheader[(z + 1)].substring(1, wheader[(z + 1)].length() - 1));
            } else {
              wbrow.setField(wheader[z], ((MRow)wvrows.elementAt(j)).getField(wheader[(z + 1)]));
            }
          }
          wvrows.insertElementAt(wbrow, j);
          j += 1;
        }
      }
      

      if (wtotalargs != null) {
        for (int i = 0; i < wtotalargs.length; i++) {
          wtotals[i] += MUtil.convert(((MRow)wvrows.elementAt(j)).getField(wtotalargs[i]));
        }
      }
    }
    


    if ((wtotalargs != null) || (woutargs != null)) {
      MRow wbrow = new MRow((MRow)wvrows.elementAt(0));
      wbrow.setCst("st");
      if (wtotalargs != null) {
        for (int i = 0; i < wtotals.length; i++) {
          wbrow.setField(wtotalargs[i], new BigDecimal(wtotals[i]).toString());
          wtotals[i] = 0.0D;
        }
      }
      if (woutargs != null) {
        for (int i = 0; i + 1 < woutargs.length; i += 2) {
          if (woutargs[(i + 1)].startsWith("'")) {
            wbrow.setField(woutargs[i], woutargs[(i + 1)].substring(1, woutargs[(i + 1)].length() - 1));
          } else {
            wbrow.setField(woutargs[i], ((MRow)wvrows.elementAt(j - 1)).getField(woutargs[(i + 1)]));
          }
        }
      }
      wvrows.insertElementAt(wbrow, j);
    }
  }
}
