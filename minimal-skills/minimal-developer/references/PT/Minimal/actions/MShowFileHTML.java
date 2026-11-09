package PT.Minimal.actions;

import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;import PT.Minimal.pnuts.;
import java.awt.Dimension;
import java.io.File;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.text.html.HTMLEditorKit;

public class MShowFileHTML implements MExternalCommand {
    MCommand minCommand;
    MForm minForm;
    Vector wrows;mpnut;
    private boolean isValid = true;

    private String wobj;
    private String wobjname;
    private String[] wargs;
    private Dimension wdim = new Dimension(525, 350);

    public MShowFileHTML() {
    }

    public void setMCommand(MCommand wcmd) {
        minCommand = wcmd;
    }

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
      wargs = MUtil.getArgs(minCommand.getCallerArgs(), ',');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wargs = MUtil.getArgs(minCommand.getArgs(), ',');
    }
    System.out.println(wargs.length);
    System.out.println(minCommand.getCallerArgs());
    if ((wargs == null) || (wargs.length < 2)) {
      throw new MError("Não existe opcão indicada ...");
    }
    if (minForm.getType() == "SINGLEROW") {
      Vector wv = new Vector();
      MRow wr = minForm.getRow().Copy();
      MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
      wv.addElement(wr);
      setRows(wv);
    }
    wobj = wargs[0].toUpperCase();
    wobjname = wargs[1];
    
    if (((wargs == null) || (wargs.length > 2)) && 
      (wargs[2].startsWith("CANCEL"))) {
      System.out.println(wargs[2]);
      cancelOperation(minForm);
      isValid = false;
    }
    
    if (isValid) {
      if (minForm.getFase() != 401) {
        if (wrows.size() == 0) {
          throw new MError("MIN-000103W");
        }
        showAssText();
      }
      if (minForm.getFase() == 401) {
        saveAssText();
      }
    }
  }

    private void showAssText()
            throws MError {
        MForm wfmc = MForm.getMForm("FILEHTML");
        try {
            if (wfmc == null) {
                JEditorPane minHTML = new JEditorPane();
                minHTML.setName("minHTML");
                minHTML.setBackground(java.awt.Color.white);
                minHTML.setFont(new java.awt.Font("Courier", 0, 12));
                minHTML.setEditable(false);

                HTMLEditorKit minKit = new HTMLEditorKit();
                minHTML.setEditorKit(minKit);

                JScrollPane wscroll = new JScrollPane(minHTML);
                JViewport wvport = wscroll.getViewport();
                wvport.add(minHTML);

                wvport.setScrollMode(2);

                minHTML.setName(wobj);

                wfmc = new MForm("FILEHTML");
                wfmc.setEnt(minForm.getEnt());

                wfmc.setCenterPanel(wscroll);
                wfmc.setBotPanel(wfmc.constrBotPanel(3));
                MButton wok = (MButton) MUtil.getComponent(wfmc.getBotPanel(), "wok");
                wfmc.setOKButton(wok);
                MButton wend = (MButton) MUtil.getComponent(wfmc.getBotPanel(), "wend");
                wfmc.setENDButton(wend);
                wend.setCommand(minCommand.getName());
                wend.setCommandArgs(wobj + "," + wobjname + "," + "CANCEL");
                lastContainer = minHTML;

                wfmc.prepareForm();

                MFrame wframe = new MFrame();
                wframe.setTitle(minForm.getEntName() + " - " + wobjname);

                wframe.setForm(wfmc);
                wframe.pack();
                wframe.center(wdim.width, wdim.height, false);

                mpnut.put(wfmc.getName(), wfmc);
            }

            mpnut.setForm(wfmc);
            mpnut.setRow(null);
            MFrame.setModalParent(minForm.getMFrame(), wfmc.getMFrame());

            ((MFrame) wfmc.getFormParent()).setTitle(minForm.getEntName() + " - " + wobjname);
            wfmc.setAtribFormat(wfmc.getCenterPanel(), minCommand.getName().trim());
            wfmc.setAction(minCommand.getName());
            wfmc.setFase(401);
            wfmc.setActionObject(wrows);
            wfmc.setActionObjectIndex(0);
            if (minForm.getBrowse() != null) {
                wfmc.setBrowse(minForm.getBrowse());
            }
            prepareNext(wfmc);
        } catch (MError ex) {
            ex.show(minForm.getMFrame());
        }
    }

    public void prepareNext(MForm wfmc)
            throws MError {
        Vector wrows = (Vector) wfmc.getActionObject();
        int j = wfmc.getActionObjectIndex();
        if (j >= wrows.size()) {
            wfmc.hideForm();
        }
        if (j < wrows.size()) {
            JEditorPane minHTML = (JEditorPane) lastContainer;

            wfmc.setRow((MRow) wrows.elementAt(j));
            mpnut.setRow(wfmc.getRow());

            String wtext = "";
            minHTML.setText(wtext);
            String wfilename = mpnut.evalFormatedValues(wargs[0]);
            File wfile = new File(wfilename);
            if (wfile.exists()) {
                try {
                    System.out.println(getClass().getResource("/" + wfilename));
                    minHTML.setPage(getClass().getResource("/" + wfilename));
                } catch (java.io.IOException ex) {
                    throw new MError(ex);
                }
            }

            wfmc.showForm();
        }
    }

    private void saveAssText()
            throws MError {
        JEditorPane minHTML = (JEditorPane) minForm.lastContainer;

        minForm.setActionObjectIndex(minForm.getActionObjectIndex() + 1);
        prepareNext(minForm);
    }

    public void cancelOperation(MForm wfmc) throws MError {
        wfmc.setActionObjectIndex(wfmc.getActionObjectIndex() + 1);
        prepareNext(wfmc);
    }
}
