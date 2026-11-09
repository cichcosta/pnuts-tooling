package PT.Minimal.geral;

import PT.Minimal.pnuts.;
import PT.Minimal.util.MString;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JFileChooser;

public class MError extends RuntimeException
{
  protected boolean thrownFromPnut = false;
  protected boolean isHandledFlag = false;
  protected int wdcode = 0;
  protected int wmcode = 0;
  protected int wpcode = 0;
  
  protected String minErrorCode;
  protected MHashtable wparms;
  protected String wcsel = " ";
  protected String wmsg = " ";
  protected String wauxmsg = " ";
  protected String wsqlmsg = " ";
  protected String watrib = " ";
  protected MForm wform;
  protected Exception wexception = null;
  protected static Vector vAttachments = new Vector();
  
  private Hashtable htTran;
  private String wamb = " ";
  private static String TMPTRACE = "trace.txt";
  MTextField wtfDe;
  MTextField wtfPara;
  MTextField wtfAssunto;
  MTextField wtfAttach; MTextField wtfServer; MTextArea wtaTexto; MButton mbBrowse = new MButton("browse", "Procurar Ficheiro", null, false);
  
  public MError(String msg)
  {
    this(msg, (MHashtable)null);
  }
  
  public MError(String msg, Object[] parms) { this(msg, new MHashtable(parms)); }
  
  public MError(String msg, Vector parms) {
    this(msg, new MHashtable(parms));
  }
  
  public MError(String msg, MHashtable parms) {
    super(msg);
    wmsg = msg;
    if (wmsg.startsWith("MIN")) {
      getMinErrorCode(wmsg);
    }
    setParms(parms);
  }
  
  public MError(Exception we) {
    super(we.getMessage());
    System.out.println("!*** MError Exception: " + we);
    wmsg = we.getMessage();
    setNextException(we);
  }
  
  public MError(SQLException we) {
    super(we.getMessage());
    System.out.println("!*** MError SQLException: " + we);
    wdcode = we.getErrorCode();
    setNextException(we);
    wmsg = (this.wsqlmsg = we.getMessage() + we.getSQLState());
    if ((!MLogin.isLoginOk()) && 
      (getMinErrorCode() == null)) {
      setMessage("Erro ao conectar à base de dados!");
      setAuxMessage("Erro: " + wdcode + " ==> " + we.getMessage());
      setMinErrorCode("MIN-LOG001E");
    }
  }
  
  public MError(SQLException we, String wamb)
  {
    super(we.getMessage());
    System.out.println("!*** MError SQLException / wamb: " + we + " 7 " + wamb);
    setNextException(we);
    setAmbient(wamb);
    wdcode = we.getErrorCode();
    wsqlmsg = (we.getMessage() + we.getSQLState());
    

    System.out.println("!*** MError: " + wdcode + " " + wsqlmsg);
    if ((wamb.equals("PostgreSQL")) && (wsqlmsg.trim().length() > 4)) {
      wdcode = 0;
      String s = wsqlmsg.substring(wsqlmsg.trim().length() - 5);
      
      if (s.equals("42P01")) {
        wdcode = 65330;
      }
      if (s.equals("23505")) {
        wdcode = 59733;
      }
      if ((wdcode >= 0) && (s.indexOf("X") <= -1) && (s.indexOf("P") <= -1) && (s.indexOf("S") <= -1)) {
        wdcode = Integer.valueOf(s).intValue();
      }
      System.out.println("!*** MError2: " + wdcode + " " + s);
    }
    if ((!wamb.equals("PostgreSQL")) && (wsqlmsg.trim().length() > 4) && (wsqlmsg.substring(wsqlmsg.trim().length() - 5, wsqlmsg.trim().length()).equals("37000"))) {
      wsqlmsg = wsqlmsg.substring(0, wsqlmsg.trim().length() - 5);
    }
    int z = wsqlmsg.indexOf(".");
    if (z != -1) {
      int w = wsqlmsg.lastIndexOf("-", z);
      if ((w != -1) && (MString.isNumber(wsqlmsg.substring(w, z)))) {
        wdcode = Integer.valueOf(wsqlmsg.substring(w, z).trim()).intValue();
      }
    }
    if ((!MLogin.isLoginOk()) && 
      (getMinErrorCode() == null)) {
      setMessage("Erro ao conectar à base de dados!");
      setAuxMessage("Erro: " + wdcode + " ==> " + we.getMessage());
      setMinErrorCode("MIN-LOG001E");
    }
    
    if (MLogin.isLoginOk()) {
      int i = wsqlmsg.indexOf("MIN-");
      if (i == -1) {
        wmsg = wsqlmsg;
        getMinErrorCode(String.valueOf(wdcode), wamb);
        if ((wamb.equals("Informix")) && (wdcode == 64790)) {
          if (findErrorInMessage(wsqlmsg) != 0) {
            getMinErrorCode(String.valueOf(findErrorInMessage(wsqlmsg)), wamb);
          } else {
            setMessage("Erro na execução de código na base de dados!");
            setAuxMessage(MUtil.stringRep(we.getMessage(), "\\n", "\n"));
            setMinErrorCode("MIN-000746E");
          }
        }
        if ((wamb.equals("Oracle")) && (wdcode == 44790)) {
          setMessage("Erro na execução de código na base de dados!");
          setAuxMessage(MUtil.stringRep(we.getMessage(), "\\n", "\n"));
          setMinErrorCode("MIN-000746E");
        }
        if ((wamb.equals("SQL Server")) && ((wdcode == 50000) || (wdcode == 37000))) {
          if (findErrorInMessage(wsqlmsg) != 0) {
            getMinErrorCode(String.valueOf(findErrorInMessage(wsqlmsg)), wamb);
          } else {
            setMessage("Erro na execução de código na base de dados!");
            setAuxMessage(MUtil.stringRep(we.getMessage(), "\\n", "\n"));
            setMinErrorCode("MIN-000746E");
          }
        }
        if (getMinErrorCode() == null) {
          setMessage("Erro na base de dados!");
          setAuxMessage("Erro: " + wdcode + " ==> " + we.getMessage());
          setMinErrorCode("MIN-000747E");
        }
      } else {
        int j = wsqlmsg.indexOf("\\n", i);
        if (j == -1) {
          j = wsqlmsg.indexOf('\n', i);
        }
        if (j == -1) {
          wmsg = wsqlmsg.substring(i);
        } else {
          wmsg = wsqlmsg.substring(i, j);
        }
        Object[] parms = null;
        j = wmsg.indexOf(':');
        if (j > 0) {
          parms = new Object[] { wmsg.substring(j + 1) };
          wmsg = wmsg.substring(0, j);
        }
        getMinErrorCode(wmsg);
        if (parms != null) setParms(new MHashtable(parms));
      }
    }
  }
  
  public void setFromPnut(boolean wboo) {
    thrownFromPnut = wboo;
  }
  
  public boolean isFromPnut() { return thrownFromPnut; }
  
  public boolean isHandled() {
    return isHandledFlag;
  }
  
  public void setHandled(boolean wboo) { isHandledFlag = wboo; }
  


  public void setMessage(String ws)
  {
    htTran = MGetMData.getTran();
    if ((htTran != null) && (htTran.containsKey(ws)) && 
      (!((String)htTran.get(ws)).trim().equals("*"))) {
      ws = (String)htTran.get(ws);
    }
    


    wmsg = ws;
  }
  
  public String getMessage() { return wmsg; }
  
  public void setAuxMessage(String ws)
  {
    if ((htTran != null) && (htTran.containsKey(ws)) && 
      (!((String)htTran.get(ws)).trim().equals("*"))) {
      ws = (String)htTran.get(ws);
    }
    

    wauxmsg = ws;
  }
  
  public String getAuxMessage() { return wauxmsg; }
  
  public void setMinErrorCode(String ws) {
    minErrorCode = ws;
  }
  
  public String getMinErrorCode() { return minErrorCode; }
  
  public void setAtrib(String ws) {
    watrib = ws;
  }
  
  public String getAtrib() { return watrib; }
  
  public void setForm(MForm ws) {
    wform = ws;
  }
  
  public MForm getForm() { return wform; }
  
  public void setAmbient(String ws) {
    wamb = ws;
  }
  
  public String getAmbient() { return wamb; }
  
  public void setSQLStatement(String ws) {
    wcsel = ws;
  }
  
  public String getSQLStatement() { return wcsel; }
  
  public void setNextException(Exception we) {
    wexception = we;
  }
  
  public Exception getNextException() { return wexception; }
  
  public int getWdcode() {
    return wdcode;
  }
  
  public void setWdcode(int i) { wdcode = i; }
  
  public int getWmcode() {
    return wmcode;
  }
  
  public void setWmcode(int i) { wmcode = i; }
  
  public int getWpcode() {
    return wpcode;
  }
  
  public void setWpcode(int i) { wpcode = i; }
  


  public MHashtable getParms() { return wparms; }
  
  public void setParms(MHashtable parms) {
    wparms = parms;
    if (parms != null) {
      try {
         pnut = .findMPnut("MError");
        pnut.put("msg", wparms);
        wmsg = pnut.evalValues(wmsg);
        wauxmsg = pnut.evalValues(wauxmsg);
        if ((htTran != null) && (htTran.containsKey(wmsg)) && 
          (!((String)htTran.get(wmsg)).trim().equals("*"))) {
          wmsg = ((String)htTran.get(wmsg));
        }
        
        if ((htTran != null) && (htTran.containsKey(wauxmsg)) && 
          (!((String)htTran.get(wauxmsg)).trim().equals("*"))) {
          wauxmsg = ((String)htTran.get(wauxmsg));
        }
      }
      catch (MError err) {
        err.printStackTrace();
      }
    }
  }
  





  public void sendMail(MFrame wparent)
  {
    System.out.println("!*** MError ENVIA MAIL");
    
    MFrame wf = new MFrame();
    
    Vector wv = new Vector();
    
    wv.addElement(new MLabel("De", true));
    wtfDe = new MTextField("TFDe", 40, false);
    wtfDe.setInternalWidth(60);
    wtfDe.setText(MUtil.myProp.getProperty("JUSERMAIL"));
    wv.addElement(wtfDe);
    
    wv.addElement(new MLabel("Para", true));
    wtfPara = new MTextField("TFPara", 40, false);
    wtfPara.setInternalWidth(60);
    wtfPara.setText(MUtil.myProp.getProperty("JMAILHELPDESK"));
    wv.addElement(wtfPara);
    
    wv.addElement(new MLabel("Assunto", true));
    wtfAssunto = new MTextField("TFAssunto", 40, false);
    wtfAssunto.setInternalWidth(60);
    wv.addElement(wtfAssunto);
    
    wv.addElement(new MLabel("Attachments", true));
    wtfAttach = new MTextField("TFAttachments", 40, false);
    wtfAttach.setInternalWidth(60);
    wv.addElement(wtfAttach);
    
    mbBrowse.setCommand("browse");
    wv.addElement(mbBrowse);
    
    wv.addElement(new MLabel("Servidor Mail", true));
    wtfServer = new MTextField("TFServidor Mail", 40, false);
    wtfServer.setInternalWidth(60);
    wtfServer.setText(MUtil.myProp.getProperty("JMAILSERVER"));
    wv.addElement(wtfServer);
    
    wtaTexto = new MTextArea("TATexto", "", null, 40, 34, true);
    
    wv.addElement(wtaTexto);
    
    MDialog wd = new MDialog(wf, "Reportar Erro", true, wv, 3, "mailerror");
    wd.getForm().setEnt("Reportar Erro");
    wd.pack();
    wd.setSize(getPreferredSizewidth, getPreferredSizeheight + 20);
    wd.setLocation(30, 30);
    
    MUtil.requestFocus(wtfDe);
    wd.setVisible(true);
  }
  
  public static void doMailSend(MForm mform) {
    System.out.println("!*** MError SEND MAIL");
    
    MMail msg = new MMail();
    System.out.println("!*** MError MFORM: " + mform);
    
    msg.setMailServer(((MTextField)mform.getFormComponent("TFServidor Mail")).getFormatedText());
    msg.setFrom(((MTextField)mform.getFormComponent("TFDe")).getFormatedText());
    msg.setTo(((MTextField)mform.getFormComponent("TFPara")).getFormatedText());
    msg.setSubject(((MTextField)mform.getFormComponent("TFAssunto")).getFormatedText());
    msg.setText(((MTextArea)mform.getFormComponent("TATexto")).getTextArea().getText());
    
    byte[] traceBytes = PT.Minimal.geral.console.MConsole.getConsoleText().trim().getBytes();
    if (traceBytes.length > 0) {
      String filename = MUtil.myProp.getProperty("JTEMP") + File.separator + TMPTRACE;
      File fTrace = new File(filename);
      FileOutputStream fosTrace = null;
      try {
        fosTrace = new FileOutputStream(fTrace);
        fosTrace.write(PT.Minimal.geral.console.MConsole.getConsoleText().getBytes());
        vAttachments.add(filename);
        msg.setAttachments(vAttachments);
        msg.sendMail();
        ((MTextArea)mform.getFormComponent("TATexto")).getTextArea().setText("");
      } catch (IOException e) {}finally {
        vAttachments.removeAllElements();
        try {
          fosTrace.close();
          fTrace.delete();
        }
        catch (IOException e) {}
      }
    }
  }
  
  public static void doBrowse(MForm mform) {
    String filename = new String();
    String path = new String();
    JFileChooser chooser = new JFileChooser();
    String sAttach = ((MTextField)mform.getFormComponent("TFAttachments")).getFormatedText();
    int returnVal = chooser.showOpenDialog(new Panel());
    if (returnVal == 0) {
      path = chooser.getCurrentDirectory() + System.getProperty("file.separator");
      filename = chooser.getSelectedFile().getName();
      if (sAttach == null) {
        ((MTextField)mform.getFormComponent("TFAttachments")).setText(filename + ";");
      } else {
        ((MTextField)mform.getFormComponent("TFAttachments")).setText(sAttach + filename + ";");
      }
      vAttachments.add(path + filename);
      System.out.println("!*** MError VECTOR = " + vAttachments);
    }
  }
  
  public int show()
  {
    return show(new MFrame());
  }
  
  public int show(Frame wparent) {
    int wanswer = -1;
    java.awt.Component wc = null;
    if ((wform != null) && (wmcode < 0)) {
      wc = wform.getFormComponent(getAtrib());
      if (wc != null) {
        wform.showComponentTab(getAtrib());
      }
    }
    if (getMinErrorCode() == null) {
      setAuxMessage(getMessage());
      setMessage("ATENÇÃO!");
      setMinErrorCode("MIN-UNK001E");
    }
    


















    wanswer = showMinErrCode(wparent);
    System.out.println("!*** MError RESPOSTA: " + wanswer);
    

    if (wform != null)
    {
      MUtil.requestFocus(wform.getOKButton());
    }
    

    if (wc != null) {
      if ((wc instanceof MTextField)) {
        ((MTextField)wc).setBackground(Color.red);
        ((MTextField)wc).selectAll();
      }
      MUtil.requestFocus(wc);
    }
    
    if ((wanswer == 1) && (getMinErrorType() != 3)) {
      sendMail((MFrame)wparent);
    }
    

    return wanswer;
  }
  
  public int getMinErrorType() {
    int wmsgtype = 2;
    
    if (getMinErrorCode().endsWith("E")) {
      wmsgtype = 0;
    }
    if (getMinErrorCode().endsWith("M")) {
      wmsgtype = 1;
    }
    if (getMinErrorCode().endsWith("Q")) {
      wmsgtype = 3;
    }
    return wmsgtype;
  }
  
  private int showMinErrCode(Frame wparent) {
    int wmsgtype = getMinErrorType();
    if (wmsgtype == 0) {
      printStackTrace();
      System.out.println(wcsel);
    }
    
    Object[] woptions;
    Object[] woptions;
    if (wmsgtype == 3) {
      woptions = new Object[] { "     OK     ", MUtil.lang("\"Cancelar\"") };

    }
    else
    {

      woptions = new Object[] { "     OK     " };
    }
    String wpgmsg = getAuxMessage();
    if (wpgmsg.indexOf("MIN-") > -1) {}
    

    String[] wauxmsg = MUtil.getArgs(getMessage() + "\n" + wpgmsg, "\n");
    System.out.println("!*** MError wauxmsg: " + wauxmsg);
    if (getAuxMessage() != null) { System.out.println("!*** MError getAuxMessage(): " + wpgmsg + " " + wpgmsg.indexOf("\n"));
    }
    Object[] wmsgs = new Object[wauxmsg.length + 1];
    wmsgs[0] = getMinErrorCode();
    

    for (int i = 0; i < wauxmsg.length; i++) {
      wmsgs[(i + 1)] = wauxmsg[i];
    }
    









    Frame f = new Frame();
    f.setBackground(new Color(163, 189, 252));
    Panel p = new Panel();
    p.setBackground(new Color(163, 189, 252));
    return javax.swing.JOptionPane.showOptionDialog(p, wmsgs, "Minimal - Alerta", -1, wmsgtype, null, woptions, woptions[0]);
  }
  



  private void getMinErrorCode(String wcode)
    throws MError
  {
    try
    {
      setMinErrorCode(wcode);
      Hashtable wh = MMData.getErrorCode(wcode);
      System.out.println("!*** MError getMinErrorCode MN: " + wcode);
      if (wh != null) {
        setMessage(MUtil.stringRep((String)wh.get("nerr"), "\\n", "\n"));
        setAuxMessage(MUtil.stringRep((String)wh.get("nerrs"), "\\n", "\n"));
      }
    } catch (MError ex) {
      ex.printStackTrace();
    }
  }
  
  private void getMinErrorCode(String wcode, String wamb) throws MError {
    String werrconv = MMData.getErrorConversion(wcode, wamb);
    System.out.println("!*** MError getMinErrorCode CV: " + wcode + " " + wamb);
    if (werrconv != null) {
      getMinErrorCode(werrconv);
    }
  }
  
  public void printStackTrace()
  {
    if (wexception != null) {
      wexception.printStackTrace();
    }
    super.printStackTrace();
  }
  
  public void printStackTrace(PrintStream s) { if (wexception != null) {
      wexception.printStackTrace(s);
    }
    super.printStackTrace(s);
  }
  
  public void printStackTrace(java.io.PrintWriter s) { if (wexception != null) {
      wexception.printStackTrace(s);
    }
    super.printStackTrace(s);
  }
  
  public int findErrorInMessage(String wmsg) {
    int werror = 0;
    int z = wmsg.indexOf("-");
    if (z != -1) {
      int w = wmsg.indexOf(" ", z);
      if ((w != -1) && (MString.isNumber(wmsg.substring(z, w)))) {
        werror = Integer.valueOf(wmsg.substring(z, w).trim()).intValue();
      }
    }
    return werror;
  }
}
