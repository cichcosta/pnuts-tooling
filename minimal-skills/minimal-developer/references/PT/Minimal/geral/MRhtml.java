package PT.Minimal.geral;

import java.io.BufferedReader;
import java.io.File;

public class MRhtml
{
  public String EOL = "\r\n";
  public String EXISTE = "Existe!";
  public String wcsel;
  public String wlinha;
  public java.io.DataOutputStream fos;
  public BufferedReader fis;
  private File inputFile;
  public String wtexto;
  
  public MRhtml()
  {
    File inputFile = new File("MEsnm.src");
    whead = getText(inputFile, "<HTML>", "<!--RIGTH_COLUMN_START-->", true);
    wfoot = getText(inputFile, "<!--RIGTH_COLUMN_END-->", "</HTML>", true); }
  
  public String whead;
  
  public void replaceHtml(File wfile) { java.util.Date wdate = new java.util.Date();
    try {
      File outputFile = wfile;
      File outputFile2 = new File(stringRep(wfile.getName() + "b", "&", "-"));
      System.out.println(outputFile.getName());
      wtexto = (whead + getText(outputFile, "<!--RIGTH_COLUMN_START-->", "<!--RIGTH_COLUMN_END-->", false) + wfoot);
      wtitle = getText(outputFile, "<title>", "</title>", false);
      if (wtitle == "") {
        wtitle = getText(outputFile, "<TITLE>", "</TITLE>", false);
      }
      wtitulo = getText(outputFile, "<!--TITULO2_START-->", "<!--TITULO2_END-->", false);
      woptions = getText(outputFile, "<!--OPTIONS_COLUMN_START-->", "<!--OPTIONS_COLUMN_END-->", false);
      if (woptions.indexOf("<") == -1) {
        wtexto = stringRep(wtexto, "WIDTH=225", "WIDTH=100");
      }
      if (wtitulo == "") {
        wtitulo = wtitle;
      }
      wtexto = stringRep(wtexto, "WTITULO1", wtitle);
      wtexto = stringRep(wtexto, "WTITULO2", wtitulo);
      wtexto = stringRep(wtexto, "EEEES", wfile.getName().substring(0, 5));
      wtexto = stringRep(wtexto, "WOPTIONS", woptions);
      wtexto = stringRep(wtexto, "WDATE", wdate.toString());
      fos = new java.io.DataOutputStream(new java.io.FileOutputStream(outputFile2));
      fos.writeBytes(wtexto);
      fos.close();
    } catch (java.io.FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
    } }
  
  public String wfoot;
  
  public String stringRep(String wline, String wold, String wnew) { while (wline.indexOf(wold) != -1) {
      wline = wline.substring(0, wline.indexOf(wold)) + wnew + wline.substring(wline.indexOf(wold) + wold.length(), wline.length());
    }
    

    return wline; }
  
  public String wtitle;
  
  public String getText(File wfile, String winicio, String wfim, boolean winclude) { String wline = "";String wfiletxt = "";
    try {
      if (wfile.canRead()) {
        BufferedReader wstream = new BufferedReader(new java.io.FileReader(wfile));
        String wlixo; while ((wlixo = wstream.readLine()) != null) {
          wfiletxt = wfiletxt + wlixo + EOL;
        }
        if ((wfiletxt.indexOf(winicio) != -1) && (wfiletxt.indexOf(wfim) != -1)) {
          if (winclude) {
            wline = wfiletxt.substring(wfiletxt.indexOf(winicio), wfiletxt.indexOf(wfim) + wfim.length()) + EOL;
          } else {
            wline = wfiletxt.substring(wfiletxt.indexOf(winicio) + winicio.length(), wfiletxt.indexOf(wfim)) + EOL;
          }
        }
        wstream.close();
      }
    } catch (java.io.FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
    }
    return wline; }
  
  public String wtitulo;
  
  public static void main(String[] args) { MRhtml ttt = new MRhtml(); }
  
  public String woptions;
  private static final String wlmenu = "<BR><BR><A HREF=\"WESNMTDS.htm\"><IMG SRC=\"/minimal/docs/base/gif/arrow.gif\" ALT=\"*\" WIDTH=16 HEIGHT=15 ALIGN=MIDDLE BORDER=0><B>WESNMNDS</B></A>";
  private static final String wlopcao = "<BR><BR><A HREF=\"hop/WESNMTDS5@00.htm\"><IMG SRC=\"/minimal/docs/base/gif/minhelp.gif\" ALT=\"*\" WIDTH=16 HEIGHT=15 ALIGN=MIDDLE BORDER=0></A> <A HREF=\"era/WESNMTDS.era\"> <B>WESNMNDS</B></A>";
}
