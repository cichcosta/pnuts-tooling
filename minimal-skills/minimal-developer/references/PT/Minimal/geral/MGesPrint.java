package PT.Minimal.geral;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;

import PT.Minimal.pnuts.MPnuts;

public class MGesPrint
{
  private static int fileNumber = 1;
  public static String EOL = "\n";
  public static String PGF = "\000X0C";
  private String fileExtension = ".rtf";
  
  public MGesPrint(MReport wreport) throws MError {
    MPnuts mpnut = wreport.getMPnut();
    String wfilename = wreport.getReportFileName();
    System.out.println("!*** MGesPrint wfilename:" + wfilename);
    if (wfilename.lastIndexOf(".") != -1) {
      fileExtension = wfilename.substring(wfilename.lastIndexOf(".")).toLowerCase().trim();
    }
    try
    {
      String wos = (String)System.getProperties().get("os.name");
      System.out.println("!*** MGesPrint os.name:" + wos);
      byte[] b; if ((wfilename.charAt(1) == ':') || (wos.equals("Linux"))) {
        System.out.println("!*** MGesPrint AF1");
        File wfile = new File(wreport.getReportFileName());
        BufferedInputStream wstream = new BufferedInputStream(new java.io.FileInputStream(wfile));
        

        System.out.println("!*** MGesPrint AF2");
        byte[] b = new byte[(int)wfile.length()];
        wstream.read(b, 0, b.length);
        wstream.close();
      } else {
        getClass().getClassLoader();java.io.InputStream stream = ClassLoader.getSystemResourceAsStream(wfilename);
        if (stream == null) {
          throw new java.io.FileNotFoundException("Template '" + wfilename + "' não encontrado !");
        }
        b = MUtil.getBytesFromStream(stream);
      }
      String wallfile = clearRTF(new String(b));
      
      int wpos1 = wallfile.indexOf("STARTCURSOR");
      int wpos2 = wallfile.lastIndexOf("ENDCURSOR");
      int wpospar1 = -1;
      int wpospar2 = -1;
      int wpospar3 = -1;
      if ((wpos1 != -1) && (wpos2 != -1) && (fileExtension.equals(".rtf"))) {
        wpospar1 = wallfile.indexOf("\\par", wpos1);
        wpospar2 = wallfile.lastIndexOf("\\par", wpos2);
        wpospar3 = wallfile.indexOf("\\par", wpos2);
      }
      if ((wpospar1 == -1) && (wpospar2 == -1) && (wpos1 != -1) && (wpos2 != -1) && (!fileExtension.equals(".rtf"))) {
        wpospar1 = wallfile.indexOf(EOL, wpos1);
        wpospar2 = wallfile.lastIndexOf(EOL, wpos2);
        wpospar3 = wallfile.indexOf(EOL, wpos2);
        if ((wpospar1 != -1) && (wpospar2 != -1)) {
          wpospar1 += 1;
          wpospar2 += 1;
          wpospar3 += 1;
        }
      }
      if (wpospar3 < 1) {
        wpospar3 = wallfile.length();
      }
      
      wreport.prepareReport();
      
      String wfile_head = wallfile;
      String wfile_curs = "";
      String wfile_foot = "";
      
      StringBuffer wout_curs = new StringBuffer(1000000);
      String wstartline = "";
      String wendline = "";
      
      System.out.println("!*** MGesPrint wpos1:" + wpos1 + " wpos2:" + wpos2 + " wpospar1:" + wpospar1 + " wpospar2:" + wpospar2 + " wpospar3:" + wpospar3);
      
      if ((wpos1 != -1) && (wpos2 != -1) && (wpospar1 != -1) && (wpospar2 != -1)) {
        wfile_head = wallfile.substring(0, wpos1);
        wfile_curs = wallfile.substring(wpospar1, wpospar2);
        wstartline = wallfile.substring(wpos1, wpospar1);
        wendline = wallfile.substring(wpos2, wpospar3);
        if ((wpospar3 > 0) && (wpospar3 != wallfile.length())) {
          wfile_foot = wallfile.substring(wpospar3);
        }
        
        wreport.prepareCursor("DEFAULT", wfile_curs);
        

        mpnut.set("wout_curs", wout_curs);
        for (int i = 0; wreport.hasMoreThen("DEFAULT", i); i++) {
          mpnut.set("wrowcount", new Integer(i));
          mpnut.evalFormatedValues(wstartline);
          wout_curs.append(wreport.getCursorRow("DEFAULT", i));
          mpnut.evalFormatedValues(wendline);
        }
        System.out.println("!*** MGesPrint Registos tratados:" + i);
        mpnut.unset("wrowcount");
        mpnut.unset("wout_curs");
      }
      
      wfile_head = mpnut.evalFormatedValues(wfile_head);
      wfile_foot = mpnut.evalFormatedValues(wfile_foot);
      wreport.closeReport();
      
      String fileOutName = MUtil.myProp.getProperty("user.name") + fileNumber + fileExtension;
      fileNumber += 1;
      wfilename = MUtil.myProp.getProperty("JTEMP") + File.separator + fileOutName;
      File wfile = new File(wfilename);
      System.out.println("!*** MGesPrint Output File: " + wfile.getAbsolutePath());
      DataOutputStream fos = new DataOutputStream(new java.io.FileOutputStream(wfile));
      
      if (wfile_head.trim().length() != 0) {
        fos.write(wfile_head.getBytes());
      }
      


      System.out.println("Bytes : " + wout_curs.toString().length());
      if (wout_curs.length() != 0)
      {

        fos.writeBytes(wout_curs.toString());
      }
      if (wfile_foot.trim().length() != 0) {
        fos.write(wfile_foot.getBytes());
      }
      fos.close();
      String wcmd = "";
      System.out.println("!*** MGesPrint isPrintPreviewOn:" + MLogin.isPrintPreviewOn());
      System.out.println("!*** MGesPrint fileExtension:" + fileExtension);
      if (MLogin.getLoginProperties().getProperty("JSTART" + fileExtension.substring(1, 4).toUpperCase()) != null) {
        wcmd = MUtil.myProp.getProperty("JSTART" + fileExtension.substring(1, 4).toUpperCase());
      }
      else if ((MLogin.isPrintPreviewOn()) && (fileExtension.substring(1, 4).toUpperCase().startsWith("RTF"))) {
        wcmd = MUtil.myProp.getProperty("JSTART") + " wscript.exe minword.vbs " + wfile.getAbsolutePath();
      } else {
        wcmd = MUtil.myProp.getProperty("JSTART") + " " + wfile.getAbsolutePath();
        System.out.println("!*** MGesPrint WCMD1: " + MUtil.myProp.getProperty("JSTART"));
        System.out.println("!*** MGesPrint WCMD2: " + wfile.getAbsolutePath());
      }
      
      mpnut.put("FILENAME", wfile.getAbsolutePath());
      mpnut.put("JSTART", MUtil.myProp.getProperty("JSTART"));
      System.out.println("!*** MGesPrint WCMD: " + wcmd);
      wcmd = mpnut.evalFormatedValues(wcmd);
      System.out.println("!*** MGesPrint WCMD: " + wcmd);
      Runtime.getRuntime().exec(wcmd);
    } catch (java.io.FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError(e.getMessage());
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
      throw new MError(e.getMessage());
    }
  }
  
  public String clearRTF(String wtext) {
    int j = 0;
    for (int i = wtext.indexOf("["); i != -1; i = wtext.indexOf("[", j)) {
      int wend = wtext.indexOf("]", i);
      if (wend != -1) {
        wtext = wtext.substring(0, i) + clearRTFPnut(wtext.substring(i, wend + 1)) + wtext.substring(wend + 1);
      }
      j = i + 1;
    }
    return wtext;
  }
  
  public String clearPnut(String wpnut) {
    String wclearpnut = "";
    boolean inLixo = false;
    String wscar = "";
    for (int i = 0; i < wpnut.length(); i++) {
      wscar = wpnut.substring(i, i + 1);
      if ((wscar.equals("}")) || (wscar.equals("{"))) {
        inLixo = true;
      }
      if (inLixo) {
        if (wscar.equals(" ")) {
          inLixo = false;
        }
      } else {
        wclearpnut = wclearpnut + wscar;
      }
    }
    wclearpnut = MUtil.stringRep(wclearpnut, "\n", "");
    wclearpnut = MUtil.stringRep(wclearpnut, "\r", "");
    return wclearpnut;
  }
}
