package PT.Minimal.actions;

import PT.Minimal.geral.MBrowseEdit;
import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MDialog;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MField;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MLogin;
import PT.Minimal.geral.MUtil;
import java.awt.Component;
import java.awt.Window;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.text.JTextComponent;

public class MHandleCommand {

    public MHandleCommand(String minCmd) {
        if (minCmd.startsWith("SLEEP")) {
            System.out.println("!*** MHandleCommand SLEEP ");
            try {
                Thread.sleep(25000L);
            } catch (InterruptedException e) {
            }
        }
    }

    public MHandleCommand(MFrame minFrame, MDialog minDialog, MForm minForm, Window minWindow, Component minComponent, String minCmd) {
        this(minFrame, minDialog, minForm, minWindow, minComponent, minCmd, null);
    }

    public MHandleCommand(MFrame minFrame, MDialog minDialog, MForm minForm, Window minWindow, Component minComponent, String minCmd, Vector row_vector) {
        String wtmp = minCmd;
        minForm.setBusy(true);
        try {
            if (minCmd.equals("OKDIALOG")) {
                if ((minForm.getFormParent() instanceof MDialog)) {
                    getFormParentbuttonChoosed = 2;
                    minCmd = minForm.getAction();
                }

                if ((minForm.getFormParent() instanceof MFrame)) {
                    minCmd = minForm.getAction();
                }
            }
            if (minCmd.equals("CLOSEWINDOW")) {

                if ((minForm.getFormParent() instanceof MDialog)) {
                    getFormParentbuttonChoosed = 1;
                    minForm.hideForm();
                    minCmd = " ";
                }
            }

            String[] wobj_args = MUtil.getArgs(minCmd.toUpperCase(), ',');
            System.out.println("!*** MHandleCommand minCmd.toUpperCase() " + minCmd.toUpperCase());
            MCommand wcmd = null;
            if ((wobj_args != null) && (wobj_args.length > 0)) {
                System.out.println("!*** MHandleCommand minForm.getEnt() " + minForm.getEnt() + " - " + wobj_args[0]);
                wcmd = MForm.getCmd(minForm.getEnt(), wobj_args[0]);
                System.out.println("!*** MHandleCommand minForm.getEnt() " + minForm.getEnt() + " - " + wcmd);
            }
            if (wcmd != null) {
                wcmd.setCallerArgs("");
                wcmd.setObjArgs(wobj_args);
                System.out.println("!*** MHandleCommad wcmd.getType(): " + wcmd.getType());
                if (wcmd.getType().equals("MJ")) {
                    System.out.println("!*** MHandleCommad wcmd: " + wcmd.getCmd() + " / " + wcmd.getArgs());
                    if (!PT.Minimal.geral.MCheckAut.checkAut(minForm.getEnt(), minCmd.toUpperCase(), wcmd.getCommandAutLevel())) {
                        throw new MError("MIN-009998E", new String[]{MLogin.getUser(), wcmd.getMenuText()});
                    }
                    try {
                        Component wc = minComponent;
                        System.out.println("!*** MHandleCommad minComponent: " + wc);
                        if ((wc != null) && ((wc instanceof MField))) {
                            if ((((MField) wc).getCommandArgs() != null) && (((MField) wc).getCommandArgs().trim().length() != 0)) {
                                wcmd.setCallerArgs(((MField) wc).getCommandArgs());
                            } else if (wcmd.getArgs().trim().length() == 0) {
                                wcmd.setCallerArgs(((MField) wc).getName() + ", " + ((MField) wc).getFormatedText());
                            }
                        }

                        System.out.println("!*** MHandleCommand Class.forName: " + wcmd.getCmd());
                        Class wclass = Class.forName(wcmd.getCmd());
                        Object wobj = wclass.newInstance();
                        if ((wobj instanceof MExternalCommand)) {
                            ((MExternalCommand) wobj).setMCommand(wcmd);
                            ((MExternalCommand) wobj).setMForm(minForm);

                            if ((minCmd.equals("TEXT_ROW")) && (minForm.getBrowse() != null)) {
                                minForm.setType("MULTIROW");
                            }

                            int wvsize = 0;
                            if ((minForm.getBrowse() != null) || (row_vector != null)) {
                                if (row_vector == null) {
                                    row_vector = minForm.getBrowse().getSelectedRowObjectVector();
                                    wvsize = row_vector.size();
                                }
                                ((MExternalCommand) wobj).setRows(row_vector);
                            }
                            System.out.println("!*** MHandleCommand Vai chamar execute: " + wcmd.getCmd() + " " + minCmd + " " + wvsize);
                            ((MExternalCommand) wobj).execute();
                            minCmd = " ";
                        }
                    } catch (MError ex) {
                        ex.show(minForm.getMFrame());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        new MError(ex).show(minForm.getMFrame());
                    }
                    return;
                }
                if (wcmd.getType().equals("PX")) {
                    minCmd = wcmd.getCmd();
                }
            }
            if ((wtmp.equals("OKDIALOG")) && (wcmd == null)) {
                minCmd = wtmp;
            }

            if (minCmd.startsWith("HELP")) {
                PT.Minimal.geral.MHelp.showHelp("HELP " + minForm.getEnt());
            }

            if (minCmd.startsWith("RERUN_FX")) {
                String went = minForm.getEnt();
                System.out.println("!*** MHandleCommand Fichas a recarregar: " + went);
                PT.Minimal.pnuts
                ..reloadAll();
            }
            java.util.Enumeration e;
            if (minCmd.startsWith("RERUN_SQL")) {
                String went = minForm.getEnt();
                System.out.println("!*** MHandleCommand SQL a recarregar: " + went);
                MUtil.clearCachedValues();
                PT.Minimal.geral.MMData.reloadExps(went);
                for (e = PT.Minimal.geral.MCache.getExpObjs(); e.hasMoreElements();) {
                    String wobj = (String) e.nextElement();
                    if (wobj.indexOf("*") != -1) {
                        PT.Minimal.geral.MMData.reloadExps(wobj);
                    }
                }
            }

            if ((minCmd.startsWith("RUN")) || (minCmd.startsWith("XRUN"))) {
                String went = minCmd.substring(4, 9);
                String wparms = minCmd.substring(10);
                MForm wfmc = MForm.getMForm(went + "BR");
                PT.Minimal.geral.MinMain wmmain;
                if (wfmc == null) {
                    if ((minCmd.startsWith("XRUN")) || ((MLogin.hasLoginProperty("RUNOBJECTS")) && ((MLogin.getLoginProperty("RUNOBJECTS").equals("ASSYNC")) || ((MLogin.getLoginProperty("RUNOBJECTS").equals("ASSYNCBXPF")) && (went.startsWith("BXPF")))))) {

                        System.out.println("!*** MHandleCommand SQL run assync: " + went + " " + wparms);
                        String wrun = " java -Xincgc -cp \"MinimalJ-2-001.jar;MinimalJ-RH-2-001.jar;ifxjdbc-211p.jar;orajdbc-815.jar;pnuts-10b2.jar;jhall-10.jar;jkitgrid-110.jar;MinimalJ-Help;jactivation.jar;jmail.jar;twain.jar;jacob.jar;.\" ";
                        wrun = wrun + " -DPT.Minimal.propfile=" + MLogin.getLoginProperty("propfile");
                        wrun = wrun + " -DPT.Minimal.trace=" + MLogin.getLoginProperty("trace");
                        wrun = wrun + " -DPT.Minimal.user.name=" + MLogin.getLoginProperty("user.name");
                        wrun = wrun + " -DPT.Minimal.user.passwd=" + MLogin.getLoginProperty("user.passwd");
                        wrun = wrun + " -DPT.Minimal.user.dsn=" + MLogin.getLoginProperty("user.dsn");
                        wrun = wrun + " -DPT.Minimal.trace=true -DPT.Minimal.pnutsColor=false PT.Minimal.geral.MMenu -" + went + " " + wparms;

                        try {
                            Runtime.getRuntime().exec(wrun);

                            Runtime.getRuntime().gc();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();

                        } finally {
                        }
                    } else {
                        System.out.println("!*** MHandleCommand SQL run normal: " + went + " " + wparms);
                        wmmain = new PT.Minimal.geral.MinMain(went, wparms);
                    }
                } else {
                    System.out.println("!*** MHandleCommand SQL run cached: " + went + " " + wparms);
                    wfmc.getMFrame().setTitle(wparms);
                    wfmc.showForm();
                }
            }

            if (minCmd.startsWith("MAPRUN")) {
                String went = minCmd.substring(7, 12);
                String wparms = minCmd.substring(13);
                MForm wfmc = MForm.getMForm(went + "BR");
                System.out.println("!*** MHandleCommand MAPRUN " + went + " " + wparms);
                PT.Minimal.geral.MinMain wmmain;
                if (wfmc == null) {
                    wmmain = new PT.Minimal.geral.MinMain(went, wparms, false);
                } else {
                    wfmc.getMFrame().setTitle(wparms);
                    wfmc.showForm();
                }
            }

            MHandleCommand wcomm;
            if (minCmd.startsWith("BRCMD")) {
                String wwcmd = minCmd.substring(5).toUpperCase();
                if ((wwcmd != null) && (wwcmd.trim().length() != 0)) {
                    if ((PT.Minimal.util.MString.isNumber(wwcmd)) && (minForm.getBrowse() != null)) {
                        minForm.getBrowse().scrollToMRow(Integer.valueOf(wwcmd).intValue());
                    }
                    if (!PT.Minimal.util.MString.isNumber(wwcmd)) {
                        minCmd = wwcmd;
                        wcomm = new MHandleCommand(minFrame, minDialog, minForm, minWindow, minComponent, minCmd);
                    }
                }
            }
            System.out.println("!*** MHandleCommad minCmd: " + minCmd);
            if ("Paste".equals(minCmd)) {
                Component wactioncomp = minForm.getFormWindow().getFocusOwner();
                System.out.println("!*** MHandleCommad Paste wactioncomp: " + wactioncomp);

                if ((wactioncomp instanceof MButton)) {
                    throw new MError("FOCUS POSICIONADO NO BOTÃO '" + ((MButton) wactioncomp).getText() + "'\nClick onde pretende o focus!  ");
                }

                if ((wactioncomp instanceof JTextComponent)) {
                    ((JTextComponent) wactioncomp).paste();
                }
                if ((wactioncomp instanceof MBrowseEdit)) {
                    ((MBrowseEdit) wactioncomp).paste();
                }
            }
            if ("Copy".equals(minCmd)) {
                Component wactioncomp = minForm.getFormWindow().getFocusOwner();
                System.out.println("!*** MHandleCommad Copy wactioncomp: " + wactioncomp);

                if ((wactioncomp instanceof MButton)) {
                    throw new MError("FOCUS POSICIONADO NO BOTÃO '" + ((MButton) wactioncomp).getText() + "'\nClick onde pretende o focus!  ");
                }

                if ((wactioncomp instanceof JTextComponent)) {
                    ((JTextComponent) wactioncomp).copy();
                }
                if ((wactioncomp instanceof MBrowseEdit)) {
                    ((MBrowseEdit) wactioncomp).copy();
                }
            }
            if ("Cut".equals(minCmd)) {
                Component wactioncomp = minForm.getFormWindow().getFocusOwner();
                System.out.println("!*** MHandleCommad Cut wactioncomp: " + wactioncomp);

                if ((wactioncomp instanceof MButton)) {
                    throw new MError("FOCUS POSICIONADO NO BOTÃO '" + ((MButton) wactioncomp).getText() + "'\nClick onde pretende o focus!  ");
                }

                if ((wactioncomp instanceof JTextComponent)) {
                    ((JTextComponent) wactioncomp).cut();
                }
                if ((wactioncomp instanceof MBrowseEdit)) {
                    ((MBrowseEdit) wactioncomp).cut();
                }
            }

            if ("CLOSEWINDOW".equals(minCmd)) {
                MRegister.removeJump(minForm.getEnt());
                if ((minForm.getEnt() != null) && (minForm.getEnt().startsWith("EEEE"))) {
                    minCmd = "ENDALL";
                }

                minForm.hideForm();

                if ((minForm.getBrowse() != null) && (minForm.getAction() != null) && ((minForm.getAction().equals("BROWSE_ROW")) || (minForm.getAction().equals("PROMPT_ROW")))) {
                    minForm.getBrowse().removeAll();
                }
            }

            if (minCmd.equals("ENDALL")) {

                try {

                    PT.Minimal.geral.MConnection.closeAll();
                } catch (MError ex) {
                    ex.show(minFrame);
                }
                minForm.hideForm();
                System.exit(0);
            }
            if (minCmd.equals("CLOSEALL")) {
                MForm.closeAllForms();
                PT.Minimal.pnuts
                ..clearAll();
                PT.Minimal.geral.MCache.removeAll();
                MLogin.loadClientMeta();
                PT.Minimal.geral.MCheckAut.clearAut();
                MUtil.clearCachedValues();

                MRegister.clearJump();
            }

            if (minCmd.equals("RECONNECT")) {
                PT.Minimal.geral.MConnection wcon = PT.Minimal.geral.MConnection.getMConnection("DEFAULT");
                wcon.reconnect();
            }
            if (minCmd.equals("Execute")) {
                System.out.println("Nivel : " + MLogin.getUserAutLevel());
                if (!MLogin.getUserAutLevel().trim().equals("9")) {
                    throw new MError("MIN-009998E", new String[]{MLogin.getUser(), "Executar"});
                }
                MUtil.showExec(minForm.getMFrame());
            }
            if (minCmd.equals("CHANGE_PASSWD")) {
                MLogin.changePasswd(minForm.getMFrame());
            }

            if (minCmd.startsWith("Explain")) {
                JCheckBoxMenuItem wop = (JCheckBoxMenuItem) minComponent;
                if (wop.getState()) {
                    MCursor wmc = PT.Minimal.geral.MConnection.getMCursor();
                    try {
                        wmc.execStatement("set explain on");
                    } catch (MError ex) {
                        ex.show();
                    } finally {
                        wmc.closeStatement();
                        wmc.close();
                        wmc = null;
                    }
                } else {
                    MCursor wmc = PT.Minimal.geral.MConnection.getMCursor();
                    try {
                        wmc.execStatement("set explain off");
                    } catch (MError ex) {
                        ex.show();
                    } finally {
                        wmc.closeStatement();
                        wmc.close();
                        wmc = null;
                    }
                }
            }

            if (minCmd.startsWith("pnutsTrace")) {
                JCheckBoxMenuItem wop = (JCheckBoxMenuItem) minComponent;
                PT.Minimal.pnuts
                ..setTrace(wop.getState());
            }

            if (minCmd.startsWith("sqlTrace")) {
                JCheckBoxMenuItem wop = (JCheckBoxMenuItem) minComponent;
                PT.Minimal.geral.console.MConsole.setTraceSql(wop.getState());
            }

            if (minCmd.startsWith("printPreview")) {
                JCheckBoxMenuItem wop = (JCheckBoxMenuItem) minComponent;
                MLogin.setPrintPreview(wop.getState());
            }

            MHandleCommand c;
            if (("QNOT0_INSERT".equals(minCmd)) || ("QNOT0_JUMP".equals(minCmd)) || ("QNOT1_JUMP".equals(minCmd))) {
                String wjump = "JUMPBROWSE";
                if ("QNOT0_INSERT".equals(minCmd)) {
                    wjump = "JUMPENT";
                }

                String wjqnot = wjump + minCmd.substring(0, 5);
                PT.Minimal.geral.MPrepareEnt e = new PT.Minimal.geral.MPrepareEnt("BCAL0", "DDDDD");

                minForm.setRow(e.getMRow());
                c = new MHandleCommand(minFrame, null, minForm, minWindow, null, wjqnot);
            }

            if ("SHOW_CONSOLE".equals(minCmd)) {
                PT.Minimal.geral.console.MConsole.showConsole();
            }

            if ("SHOW_MEM".equals(minCmd)) {
                MForm wdata = MForm.getDialogForm(minForm, "ZEMC0MEM", "ME", "Memória", 2);

                int wmemtot = (int) Runtime.getRuntime().totalMemory();
                int wmemfree = (int) Runtime.getRuntime().freeMemory();
                int wmembusy = wmemtot - wmemfree;

                wdata.setFormValue("zmemtot", new Integer(wmemtot).toString());
                wdata.setFormValue("zmemfree", new Integer(wmemfree).toString());
                wdata.setFormValue("zmembusy", new Integer(wmembusy).toString());

                wdata.setAtribFormat(wdata.getCenterPanel(), "DELETE_ROW");
                wdata.setSize(getPreferredSizewidth, getPreferredSizeheight + 30);
                wdata.getFormParent().setLocation(100, 100);
                wdata.setEnt("NONE");
                wdata.showForm();
            }

            if ("OKDIALOG".equals(minCmd)) {
                try {
                    System.out.println("!*** MHandleCommad minForm.getEnt(): " + minForm.getEnt() + " - " + minForm.getAction());
                    MCommand wformcmd = MForm.getCmd(minForm.getEnt(), minForm.getAction().toUpperCase());
                    if ((wformcmd != null)
                            && (wformcmd.getType().equals("MJ"))) {
                        try {
                            Class wclass = Class.forName(wformcmd.getCmd());
                            Object wobj = wclass.newInstance();
                            if ((wobj instanceof MExternalCommand)) {
                                ((MExternalCommand) wobj).setMCommand(wformcmd);
                                ((MExternalCommand) wobj).setMForm(minForm);
                                if ((minForm.getBrowse() != null) || (row_vector != null)) {
                                    if (row_vector == null) {
                                        row_vector = minForm.getBrowse().getSelectedRowObjectVector();
                                    }
                                    ((MExternalCommand) wobj).setRows(row_vector);
                                }
                                ((MExternalCommand) wobj).execute();
                            }
                        } catch (MError ex) {
                            ex.show(minFrame);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (minForm.getAction().startsWith("Print")) {
                        minCmd = minForm.getAction();
                    }

                    if ("UPText".equals(minForm.getAction())) {
                        Component wc = MUtil.getComponent(minForm.getCenterPanel(), "minText");
                        if (wc != null) {
                            minForm.getRow().setAssociatedText(((java.awt.TextArea) wc).getText());
                        }
                    }
                    MHandleCommand wcomm;
                    if ("Executar".equals(minForm.getAction())) {
                        java.util.Hashtable wh = MUtil.getRecord(minForm.getCenterPanel());
                        java.util.Properties p = MUtil.myProp;
                        p.put("user.went", (String) wh.get("went"));
                        minCmd = "MAPRUN " + (String) wh.get("went") + " Entidade";
                        minForm.hideForm();
                        wcomm = new MHandleCommand(minForm.getMFrame(), null, minForm, minWindow, minComponent, minCmd);
                    }
                    if ("login".equals(minForm.getAction())) {
                        MLogin.doLogin(minForm);
                    }

                    if ("mailerror".equals(minForm.getAction())) {
                        MError.doMailSend(minForm);
                    }
                    if (("browse".equals(minCmd)) || ("browse".equals(minForm.getAction()))) {
                        MError.doBrowse(minForm);
                    }
                    if ("Prompt".equals(minForm.getAction())) {
                        Vector wv = minForm.getBrowse().getSelectedRowObjectVector();
                        if (wv.size() > 0) {
                            MUtil.MoveRecord(((PT.Minimal.geral.MRow) wv.elementAt(0)).getRecord(), lastContainer);
                        }
                    }

                    minForm.hideForm();
                } catch (MError ex) {
                    ex.show(minFrame);
                }

            }

        } finally {
            minForm.setBusy(false);
        }
    }
}
