package PT.Minimal.geral;

import java.io.PrintStream;

public class MManual {
  public String EOL = "\r\n";
  public String EXISTE = "Existe!";
  public String wcsel;
  public String wlinha;
  public java.io.DataOutputStream fos;
  public java.io.BufferedReader fis;
  public String wesnmior;
  public String wesnmnop;
  
  public MManual(String wman) { try { MLogin.showLogin(new MFrame());
      
      String wcsel = "select i_man, i_ord, i_dep";
      wcsel = wcsel + " from mmant0 ";
      wcsel = wcsel + " where i_man like '" + wman.toUpperCase() + "%' ";
      wcsel = wcsel + " order by i_ord ";
      
      MCursor mman_curs = MConnection.getMCursor();
      mman_curs.execQuery(wcsel);
      java.util.Vector wv = mman_curs.getAllRecords();
      mman_curs.close();
      



      java.io.File outputFile2 = new java.io.File(wman + ".html");
      fos = new java.io.DataOutputStream(new java.io.FileOutputStream(outputFile2));
      fos.writeBytes("<HTML><HEAD></head><BODY bgcolor=\"#FFFFFF\" link=\"#000080\"  alink=\"#FF0000\">" + EOL);
      for (int i = 0; i < wv.size(); i += 1) {
        String wdepend = (String)((java.util.Hashtable)wv.elementAt(i)).get("i_dep");
        System.out.println(wdepend);
        java.io.File wfile = new java.io.File(wdepend);
        if (wfile.exists()) {
          wtitulo = getText(wfile, "<!--TITULO2_START-->", "<!--TITULO2_END-->", false);
          wtexto = getText(wfile, "<!--RIGTH_COLUMN_START-->", "<!--RIGTH_COLUMN_END-->", false);
          System.out.println(wfile + ": Chars " + wtexto.length());
          fos.writeBytes("<P> " + wtitulo + EOL + wtexto);
        }
        else {
          System.out.println("Não encontrou: " + wfile);
        }
      }
      fos.writeBytes("</body></HTML>" + EOL);
      fos.close();
    } catch (java.io.FileNotFoundException e) {
      System.err.println("FileStreamsTest: " + e);
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
    } catch (MError err) {
      err.show(new MFrame());
    }
    System.exit(0);
  }
  
  public String wesnmcf;
  
  public String getText(java.io.File wfile, String winicio, String wfim, boolean winclude) { String wline = "";String wfiletxt = "";
    try {
      if (wfile.canRead()) {
        java.io.BufferedReader wstream = new java.io.BufferedReader(new java.io.FileReader(wfile));
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
      System.out.println("Não encontrou: " + wfile);
      
      System.err.println("FileStreamsTest: " + e);
    } catch (java.io.IOException e) {
      System.err.println("FileStreamsTest: " + e);
    }
    

    return wline; }
  
  public String wesnmnds;
  
  public static void main(String[] args) { MManual ttt = new MManual(args[0]); }
  
  public String wesnmtds;
  public String wtexto;
  public String whead;
  public String wfoot;
  public String wtitulo;
}
