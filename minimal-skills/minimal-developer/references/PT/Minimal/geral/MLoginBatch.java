package PT.Minimal.geral;

import PT.Minimal.geral.console.MConsole;
import PT.Minimal.pnuts.MPnuts;
import java.util.Properties;
























public class MLoginBatch
{
  public static final Object NONE = "NONE";
  public static final Object AUTO = "AUTO";
  public static final Object SPLASH = "SPLASH";
  

  private static final String[] aliases = { "ix_meta", "jdbc:informix-sqli://simh2160.minimal.pt:1526/minj9801:INFORMIXSERVER=simh2160" };
  




  public MLoginBatch() {}
  




  private static String changeDatabaseAlias(String db)
  {
    for (int i = 0; i < aliases.length; i += 2) {
      if (aliases[i].equals(db)) {
        return aliases[(i + 1)];
      }
    }
    return db;
  }
  

















































  public static void doLogin(Properties prop, Object loginMode)
  {
    MLogin.loadLoginProperties();
    Properties loginProp = MLogin.getLoginProperties();
    

    String client = prop.getProperty("JCLIENT");
    String user = prop.getProperty("JUSER");
    String password = prop.getProperty("JPASSWORD");
    
    String metadata = prop.getProperty("JMETADATA");
    String metakey = prop.getProperty("JMETAKEY");
    String metauser = prop.getProperty("JMETAUSER");
    String metapass = prop.getProperty("JMETAPASSWD");
    
    String command = prop.getProperty("JCOMMAND");
    String start = prop.getProperty("JSTART");
    String startcsv = prop.getProperty("JSTARTCSV");
    String starttxt = prop.getProperty("JSTARTTXT");
    String startasc = prop.getProperty("JSTARTASC");
    
    String mailServer = prop.getProperty("JMAILSERVER");
    String helpdesk = prop.getProperty("JHELPDESK");
    String licenciado = prop.getProperty("LICENCIADO");
    
    boolean console = Boolean.valueOf(prop.getProperty("JCONSOLE", "false")).booleanValue();
    boolean showConsole = Boolean.valueOf(prop.getProperty("JSHOWCONSOLE", "false")).booleanValue();
    boolean tracePnuts = Boolean.valueOf(prop.getProperty("JTRACEPNUTS", "false")).booleanValue();
    boolean pnutsColor = Boolean.valueOf(prop.getProperty("JPNUTSPNUTS", "false")).booleanValue();
    

    String os = System.getProperty("os.name");
    boolean nt = os.equals("Windows_NT");
    boolean linux = os.equals("Linux");
    boolean win98 = (!os.equals("Windows_NT")) && (!os.equals("Linux"));
    


    if ((metauser == null) || (metauser.equals(""))) {
      metauser = loginProp.getProperty("JMETAUSER");
      if ((metauser == null) || (metauser.equals(""))) {
        metauser = user;
      }
    }
    
    if ((metapass == null) || (metapass.equals(""))) {
      metapass = loginProp.getProperty("JMETAPASSWD");
      if ((metapass == null) || (metapass.equals(""))) {
        metapass = password;
      }
    }
    

    if ((command == null) || (command.equals(""))) {
      command = loginProp.getProperty("JCOMMAND");
      if ((command == null) || (command.equals(""))) {
        if (nt) {
          command = "cmd.exe /c";
        }
        else if (win98) {
          command = "command.com /c";
        } else {
          command = " ";
        }
      }
    }
    


    if ((start == null) || (start.equals(""))) {
      start = loginProp.getProperty("JSTART");
      if ((start == null) || (start.equals(""))) {
        if (!linux) {
          start = command + " start";
        } else {
          start = " ";
        }
      }
    }
    

    if ((startcsv == null) || (startcsv.equals(""))) {
      startcsv = loginProp.getProperty("JSTARTCSV");
      if ((startcsv == null) || (startcsv.equals(""))) {
        if (!linux) {
          startcsv = start + " excel [FILENAME]";
        } else {
          startcsv = "/var/minimal/soffice [FILENAME]";
        }
      }
    }
    

    if ((starttxt == null) || (starttxt.equals(""))) {
      starttxt = loginProp.getProperty("JSTARTTXT");
      if ((starttxt == null) || (starttxt.equals(""))) {
        if (!linux) {
          starttxt = command + " type \"[FILENAME]\" > lpt1";
        } else {
          starttxt = " ";
        }
      }
    }
    

    if ((startasc == null) || (startasc.equals(""))) {
      startasc = loginProp.getProperty("JSTARTASC");
      if ((startasc == null) || (startasc.equals(""))) {
        if (!linux) {
          startasc = command + " copy \"[FILENAME]\" \"[pnuts:getFilePath(\"Ficheiro Output\", \" \", \"output.asc\")]\"";
        } else {
          startasc = command + " cp \"[FILENAME]\" \"[pnuts:getFilePath(\"Ficheiro Output\", \" \", \"output.asc\")]\"";
        }
      }
    }
    

    if ((mailServer == null) || (mailServer.equals(""))) {
      mailServer = loginProp.getProperty("JMAILSERVER");
      if ((mailServer == null) || (mailServer.equals(""))) {
        mailServer = "mail.minimal.pt";
      }
    }
    

    if ((helpdesk == null) || (helpdesk.equals(""))) {
      helpdesk = loginProp.getProperty("helpdesk");
      if ((helpdesk == null) || (helpdesk.equals(""))) {
        helpdesk = "helpdesk@minimal.pt";
      }
    }
    

    if ((licenciado == null) || (licenciado.equals(""))) {
      licenciado = loginProp.getProperty("LICENCIADO");
      if ((licenciado == null) || (licenciado.equals(""))) {
        licenciado = "Aplicação MinimalJ";
      }
    }
    

    if (console) {
      MConsole.start();
    }
    


    if (tracePnuts) {
      MPnuts.setTrace(true);
    }
    
    client = changeDatabaseAlias(client);
    metadata = changeDatabaseAlias(metadata);
    
    loginProp.put("user.name", user);
    loginProp.put("user.passwd", password);
    loginProp.put("user.group", "0000");
    loginProp.put("user.dsn", client);
    
    if ((metadata != null) && (!metadata.equals(""))) loginProp.put("JMETADATA", metadata);
    if ((metakey != null) && (!metakey.equals(""))) loginProp.put("JMETAKEY", metakey);
    if ((metauser != null) && (!metauser.equals(""))) loginProp.put("JMETAUSER", metauser);
    if ((metapass != null) && (!metapass.equals(""))) { loginProp.put("JMETAPASSWD", metapass);
    }
    loginProp.put("JCOMMAND", command);
    loginProp.put("JSTART", start);
    loginProp.put("JSTARTCSV", startcsv);
    loginProp.put("JSTARTTXT", starttxt);
    loginProp.put("JSTARTASC", startasc);
    
    loginProp.put("LICENCIADO", licenciado);
    
    if (loginMode == AUTO) {
      MLogin.doLogin();
    }
  }
}
