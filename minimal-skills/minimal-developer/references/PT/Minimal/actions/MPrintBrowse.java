package PT.Minimal.actions;

import PT.Minimal.geral.MBrowseColumn;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MGesPrint;
import PT.Minimal.geral.MHashtable;
import PT.Minimal.geral.MLogin;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;import PT.Minimal.pnuts.;
import java.awt.FileDialog;
import java.io.File;
import java.io.PrintStream;
import java.util.Vector;

public class MPrintBrowse implements MExternalCommand, PT.Minimal.geral.MReport {
	MCommand minCommand;
	MForm minForm;
	Vector wrows;
	com.objectshare.grid.data.JKTableModel myModel;
	private String[] wargs;private mpnut;
	private String listType = "SEL";
	private String wfilename;

	public MPrintBrowse() {
	}

	public void setMCommand(MCommand wcmd) {
		minCommand = wcmd;
	}

	public void setMForm(MForm wform) {
		minForm = wform;

		if (minForm.getType() == "SINGLEROW") {
			Vector wv = new Vector();
			MRow wr = minForm.getRow().Copy();
			MUtil.MoveRecord(MUtil.getRecord(minForm.getCenterPanel()), wr.getRecord());
			wv.addElement(wr);
			setRows(wv);
		}
	}

	public void setRows(Vector wv) {
		wrows = wv;
	}

	public void execute() throws MError{
    if (minForm.getBrowse() != null) {
      myModel = ((com.objectshare.grid.data.JKTableModel)minForm.getBrowse().getModel());
    }
    
    if (wrows.size() == 0) {
      throw new MError("MIN-000103W");
    }
    String[] wwargs = null;
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0))
    {
      wwargs = MUtil.getArgs(minCommand.getCallerArgs(), ' ');
    }
    if ((wargs == null) && (minCommand.getArgs() != null) && (minCommand.getArgs().trim().length() != 0))
    {

      wwargs = MUtil.getArgs(minCommand.getArgs(), ' ');
    }
    


    wargs = new String[4];
    for (int i = 0; (i < wwargs.length) && (i < 4); i++) {
      wargs[i] = wwargs[i];
    }
    
    if (wargs[0] == null) {
      wargs[0] = "FILEDIALOG";
    }
    if (wargs[1] == null) {
      wargs[1] = "SEL";
    }
    if (wargs[1].toUpperCase().equals("PRINTALL")) {
      listType = "MULTI";
    }
    if (wargs[1].toUpperCase().equals("ALL")) {
      if (minForm.getBrowse() != null) {
        wrows = myModel.getRowVector();
      }
      listType = "MULTI";
    }
    if (wargs[1].toUpperCase().equals("SINGLESEL")) {
      listType = "SINGLE";
    }
    if (wargs[1].toUpperCase().equals("SEL")) {
      listType = "MULTISEL";
    }
    if (wargs[2] == null) {
      wargs[2] = "MINTEMPLATES";
    }
    if (wargs[0].equals("FILEDIALOG")) {
      FileDialog wfd = new FileDialog(new java.awt.Frame(), "Template a usar");
      if (wargs[2].equals("MINTEMPLATES")) {
        wargs[2] = MLogin.getMinTemplateDir();
      }
      if (wargs[2].equals("CLITEMPLATES")) {
        wargs[2] = MLogin.getCliTemplateDir();
      }
      System.out.println("!*** MPrintBrowse wargs[2] - " + wargs[2]);
      File wentdir = new File(wargs[2] + "/" + minForm.getEnt().substring(0, 4));
      System.out.println("!*** MPrintBrowse wentdir.getAbsolutePath()" + wentdir.getAbsolutePath());
      if ((wentdir.exists()) && (wentdir.isDirectory())) {
        File wentsufdir = new File(wargs[2] + "/" + minForm.getEnt().substring(0, 4) + "/" + minForm.getEnt());
        System.out.println("!*** MPrintBrowse wentsufdir.getAbsolutePath()" + wentsufdir.getAbsolutePath());
        if ((wentsufdir.exists()) && (wentsufdir.isDirectory())) {
          wargs[2] = wentsufdir.getAbsolutePath();
        } else {
          wargs[2] = wentdir.getAbsolutePath();
        }
      }
      wfd.setDirectory(wargs[2]);
      wfd.setVisible(true);
      
      if (wfd.getFile() == null) {
        throw new MError("MIN-000001W");
      }
      wargs[0] = (wfd.getDirectory() + "/" + wfd.getFile());
    }
    

    wfilename = wargs[0];
    mpnut = .getMPnut(minForm.getEnt());
    mpnut.setForm(minForm);
    if (wrows.size() > 0) {
      mpnut.setRow((MRow)wrows.elementAt(0));
    }
    mpnut.set("vrows", wrows);
    
    MGesPrint wgp;
    if (listType.startsWith("MULTI")) {
      wfilename = checkFile(wargs[0], (MRow)wrows.elementAt(0));
      
      wgp = new MGesPrint(this);
    } else {
      Vector wmulti = wrows;
      for (int j = 0; j < wmulti.size(); j++) {
        MRow wr = (MRow)wmulti.elementAt(j);
        wfilename = checkFile(wargs[0], wr);
        Vector wv = new Vector();
        wv.addElement(wr);
        setRows(wv);
        System.out.println("!*** MPrintBrowse Impressão: " + j + " - " + wfilename);
        MGesPrint wgp = new MGesPrint(this);
        if ((minCommand.getZFase().startsWith("AF")) || (minCommand.getZFase().startsWith("BT"))) {
          wr.setZCommand(minCommand.getName(), "AF", " ");
          wr.refreshRow();
        }
      }
    }
  }

	private String checkFile(String wfilename, MRow wrow) {
		mpnut.setRow(wrow);

		String wiorg = "";
		if (wrow.containsKey("iorg")) {
			wiorg = ((String) wrow.get("iorg")).trim();
			if (wiorg.equals("*")) {
				wiorg = "X";
			}
			mpnut.put("IORG", wiorg);
		} else {
			mpnut.put("IORG", "");
		}

		if (wfilename.indexOf("DEFAULT.rtf") != -1) {
			File wnfile = new File(MLogin.getCliTemplateDir() + "/" + minForm.getEnt().substring(0, 4) + "/"
					+ minForm.getEnt() + "/" + "DEFAULT.rtf");

			System.out.println("!*** MPrintBrowse: " + wnfile.getAbsolutePath() + " - " + wnfile.exists());
			if (wnfile.exists()) {
				return wnfile.getAbsolutePath();
			}

			wnfile = new File(
					MLogin.getLogoTemplateDir() + "/" + minForm.getEnt().substring(0, 4) + "/" + minForm.getEnt()
							+ "/" + "DEFAULT.rtf");

			System.out.println("!*** MPrintBrowse " + wnfile.getAbsolutePath() + " - " + wnfile.exists());
			if (wnfile.exists()) {
				return wnfile.getAbsolutePath();
			}

			wnfile = new File(
					MLogin.getMinTemplateDir() + "/" + minForm.getEnt().substring(0, 4) + "/" + minForm.getEnt()
							+ "/" + "DEFAULT.rtf");

			System.out.println("!*** MPrintBrowse " + wnfile.getAbsolutePath() + " - " + wnfile.exists());
			if (wnfile.exists()) {
				return wnfile.getAbsolutePath();
			}
		}

		mpnut.put("TEMPLATES", MLogin.getCliTemplateDir());

		String ws = mpnut.evalFormatedValues(wfilename);
		File wfile = new File(ws);
		System.out.println("!*** MPrintBrowse " + wfile.getAbsolutePath() + " - " + wfile.exists());
		if (wfile.exists()) {
			return wfile.getAbsolutePath();
		}
		mpnut.put("IORG", "");
		ws = mpnut.evalFormatedValues(wfilename);
		wfile = new File(ws);
		System.out.println("!*** MPrintBrowse " + wfile.getAbsolutePath() + " - " + wfile.exists());
		if (wfile.exists()) {
			return wfile.getAbsolutePath();
		}

		mpnut.put("TEMPLATES", MLogin.getLogoTemplateDir());
		mpnut.put("IORG", wiorg);

		ws = mpnut.evalFormatedValues(wfilename);
		wfile = new File(ws);
		System.out.println("!*** MPrintBrowse " + wfile.getAbsolutePath() + " - " + wfile.exists());
		if (wfile.exists()) {
			return wfile.getAbsolutePath();
		}
		mpnut.put("IORG", "");
		ws = mpnut.evalFormatedValues(wfilename);
		wfile = new File(ws);
		System.out.println("!*** MPrintBrowse " + wfile.getAbsolutePath() + " - " + wfile.exists());
		if (wfile.exists()) {
			return wfile.getAbsolutePath();
		}

		mpnut.put("TEMPLATES", MLogin.getMinTemplateDir());
		ws = mpnut.evalFormatedValues(wfilename);
		wfile = new File(ws);
		System.out.println("!*** MPrintBrowse " + wfile.getAbsolutePath() + " - " + wfile.exists());
		if (wfile.exists()) {
			return wfile.getAbsolutePath();
		}
		throw new MError("MIN-910006E", new String[] { wfilename });
	}

	public void prepareReport() throws MError {
		String wheader1 = " ";
		String wheader2 = " ";
		String wdef_cursor = "";
		String wmask = "";
		if (wargs[0].toUpperCase().indexOf("DEF") != -1) {
			Object[] myCols = myModel.getColumnArray();
			int wpos1 = 0;

			for (int i = 0; i < myCols.length; i++) {
				if ((myCols[i] instanceof MBrowseColumn)) {
					MBrowseColumn wcol = (MBrowseColumn) myCols[i];
					String wtitle1 = wcol.getPrintTitle1();
					String wtitle2 = wcol.getPrintTitle2();
					System.out
							.println("!*** MPrintBrowse " + wcol.getColName() + ":" + wcol.getDataType() + ":"
									+ wcol.getWidth());
					int wpos2 = wpos1 + wcol.getWidth();
					if (wcol.getDataType() == "NUMTYPE") {
						if (wtitle1.length() != 0) {
							while (wtitle1.length() < wcol.getWidth())
								wtitle1 = ' ' + wtitle1;
						}
						if (wtitle2.length() != 0) {
							while (wtitle2.length() < wcol.getWidth())
								wtitle2 = ' ' + wtitle2;
						}
					}
					if (wtitle1.length() != 0) {
						wheader1 = wheader1.substring(0, wpos1) + wtitle1;
					}
					if (wtitle2.length() != 0) {
						wheader2 = wheader2.substring(0, wpos1) + wtitle2;
					}
					while (wheader1.length() < wpos2 + 1)
						wheader1 = wheader1 + ' ';
					while (wheader2.length() < wpos2 + 1)
						wheader2 = wheader2 + ' ';
					if ((wcol.getDataType() == "NUMTYPE") && (wcol.getMask().trim().length() != 0)) {
						wmask = wcol.getMask();
						if (wmask.charAt(1) == ' ') {
							wmask = wmask.substring(0, 1) + "*" + wmask.substring(2);
						}
						wdef_cursor = wdef_cursor + "[row." + wcol.getName() + ":" + wmask + ":" + wcol.getWidth()
								+ "] ";
					} else {
						wdef_cursor = wdef_cursor + "[row." + wcol.getName() + ":" + wcol.getWidth() + "] ";
					}
					wpos1 = wpos2 + 1;
				}
			}
			System.out.println("!*** MPrintBrowse wheader1 " + wheader1);
			System.out.println("!*** MPrintBrowse wheader2 " + wheader2);
			System.out.println("!*** MPrintBrowse wdef_cursor" + wdef_cursor);
		}
		MHashtable wh = new MHashtable(true);
		wh.put("HEADER1", wheader1);
		wh.put("HEADER2", wheader2);
		wh.put("DEFAULT", wdef_cursor);
		wh.put("TOTAL", " ");
		wh.put("ENTNAME", minForm.getEntName());
		wh.put("ENT", minForm.getEnt());
		wh.put("USER", MLogin.getUser());
		wh.put("MCOMMAND", this);

		mpnut.put("REPORT", wh);
		mpnut.set("REPORT", wh);
		mpnut.set("wrows", wrows);
		mpnut.evalPnut(minForm.getEnt(), "MC", minCommand.getName(), "BF");
	}

	public String getReportFileName()
			throws MError {
		if (wargs.length > 0) {
			return wfilename;
		}
		throw new MError("Não existe ficheiro template para a impressão");
	}

	public void closeReport() throws MError {
	}

	public void prepareCursor(String cursor_name, String cursor_mask) throws MError {
		MHashtable wmh = (MHashtable) mpnut.get("REPORT");
		if (cursor_mask.indexOf("DEFAULT") != -1) {
			wmh.put(cursor_name, mpnut.evalFormatedValues(cursor_mask));
			if ((minForm.getBrowse().isTotalized()) && (listType.equals("MULTI"))) {
				wmh.put("TOTAL", getCursorRow("DEFAULT", wrows.size() - 1));
			}
		} else {
			wmh.put(cursor_name, cursor_mask);
		}
	}

	public boolean hasMoreThen(String cursor_name, int i) throws MError {
		if (wrows.size() > i) {
			if ((minForm.getBrowse().isTotalized()) && (listType.equals("MULTI")) && (wrows.size() == i + 1)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public String getCursorRow(String cursor_name, int i) throws MError {
		MHashtable wmh = (MHashtable) mpnut.get("REPORT");
		MRow wrow = (MRow) wrows.elementAt(i);
		System.out.println("!*** MPrintBrowse " + i);

		mpnut.setRow(wrow);
		mpnut.evalPnut(minForm.getEnt(), "MC", minCommand.getName(), "BR");
		String ws = mpnut.evalFormatedValues((String) wmh.get(cursor_name));
		return ws;
	}

	public String getCursor(String cursor_name) throws MError {
		String ws = "";
		MHashtable wmh = (MHashtable) mpnut.get("REPORT");
		String wsql = (String) wmh.get(cursor_name + "SQL");
		String went = (String) wmh.get(cursor_name + "ENT");

		System.out.println("!*** MPrintBrowse " + wsql);

		MRow wcursrow = PT.Minimal.geral.MMData.getMRow(went);
		wsql = mpnut.evalValues(wsql);
		Vector wvrows = MUtil.getRows(wcursrow, wsql, 999999);
		for (int i = 0; i < wvrows.size(); i++) {
			MRow wrow = (MRow) wvrows.elementAt(i);
			mpnut.put(cursor_name, wrow);
			mpnut.set(cursor_name, wrow);
			mpnut.evalPnut(minForm.getEnt(), "MC", minCommand.getName(), "BR_" + cursor_name.toUpperCase());
			ws = ws + mpnut.evalFormatedValues((String) wmh.get(cursor_name));
		}
		return ws;
	}

	public String[] getArgs() {
		return wargs;
	}

	public  getMPnut() { return mpnut; }
}
