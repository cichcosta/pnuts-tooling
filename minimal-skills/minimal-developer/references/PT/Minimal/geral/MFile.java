package PT.Minimal.geral;

import java.io.File;
import java.util.Vector;

public class MFile extends File
{
  private String EOL = "\r\n";
  private String wtexto = "";
  private String wlixo;
  private String wname;
  
  public MFile(String wfilename) {
    super(wfilename);
  }
  
  public void readFile() {
    try {
      if (canRead()) {
        java.io.BufferedInputStream wstream = new java.io.BufferedInputStream(new java.io.FileInputStream(this));
        

        b = new byte[(int)length()];
        wstream.read(b, 0, b.length);
        wstream.close();
      }
    } catch (java.io.FileNotFoundException e) {
      System.err.println("!*** Mfile FileStreamsTest: " + e);
    } catch (java.io.IOException e) {
      System.err.println("!*** Mfile FileStreamsTest: " + e);
    }
  }
  
  public byte[] getBytes() {
    if ((b == null) || (b.length == 0)) {
      readFile();
    }
    return b;
  }
  
  public void setBytes(byte[] wb) {
    b = wb;
  }
  
  public String getText() {
    return wtexto;
  }
  
  public Vector getLines() {
    return wlinhas;
  }
  
  public void setText(String wt) {
    wtexto = wt;
  }
  
  public boolean stringRep(String wold, String wnew) {
    boolean wrb = false;
    while (wtexto.indexOf(wold) != -1) {
      wtexto = (wtexto.substring(0, wtexto.indexOf(wold)) + wnew + wtexto.substring(wtexto.indexOf(wold) + wold.length(), wtexto.length()));
      

      wrb = true;
    }
    return wrb;
  }
  
  public boolean HREFRep(String wold, String wnew) {
    boolean wrb = false;
    int j = 0;
    for (int i = wtexto.indexOf(wold); i != -1; i = wtexto.indexOf(wold, j)) {
      if (isHREF(i)) {
        wtexto = (wtexto.substring(0, i) + wnew + wtexto.substring(i + wold.length(), wtexto.length()));
        

        wrb = true;
      }
      j = i + 1;
    }
    return wrb;
  }
  
  private boolean isHREF(int wpos) {
    boolean wrb = false;
    if (wtexto.lastIndexOf("<A HREF", wpos) > wtexto.lastIndexOf(">", wpos)) {
      wrb = true;
    }
    return wrb;
  }
  
  public void saveFile() {
    saveFile(getAbsolutePath());
  }
  
  public void saveFile(String wname) {
    try {
      java.io.DataOutputStream fos = new java.io.DataOutputStream(new java.io.FileOutputStream(new File(wname)));
      fos.write(b);
      fos.close();
    } catch (java.io.FileNotFoundException e) {
      System.err.println("!*** Mfile FileStreamsTest: " + e);
    } catch (java.io.IOException e) {
      System.err.println("!*** Mfile FileStreamsTest: " + e);
    }
  }
  
  public String getSufix() {
    String wiob = getFinalName();
    if (wiob.lastIndexOf(".") != -1) {
      wiob = wiob.substring(wiob.lastIndexOf(".") + 1);
    } else {
      wiob = " ";
    }
    System.out.println("!*** Mfile " + wiob);
    return wiob;
  }
  
  public String getPrefix() {
    String wiob = getFinalName();
    if (wiob.lastIndexOf(".") != -1) {
      wiob = wiob.substring(0, wiob.lastIndexOf("."));
    }
    System.out.println("!*** Mfile " + wiob);
    return wiob;
  }
  
  public String getUnSufixedName()
  {
    return getPrefix();
  }
  
  public String getFinalName() {
    String wiob = getName();
    if (wiob.lastIndexOf(separator) != -1) {
      wiob = wiob.substring(wiob.lastIndexOf(separator) + 1);
    }
    if (wiob.lastIndexOf("/") != -1) {
      wiob = wiob.substring(wiob.lastIndexOf("/") + 1);
    }
    System.out.println("!*** Mfile " + wiob);
    return wiob;
  }
  
  public static MFile getMFileFromSystem(javax.swing.JFrame parent, int wtype)
    throws MError
  {
    MFile wfile = null;
    String wpath = " ";String wfolder = "";String wiob = "";
    javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
    chooser.setFileSelectionMode(0);
    if (MLogin.hasLoginProperty("chooser.dir")) {
      chooser.setCurrentDirectory(new File(MLogin.getLoginProperty("chooser.dir")));
    } else {
      chooser.setCurrentDirectory(new File(MLogin.getLoginProperty("user.dir")));
    }
    

    int returnVal;
    
    int returnVal;
    
    if (wtype == 1) {
      returnVal = chooser.showSaveDialog(parent);
    } else {
      returnVal = chooser.showOpenDialog(parent);
    }
    if (returnVal == 1) {
      throw new MError("MIN-000001W");
    }
    if (returnVal == 0) {
      System.out.println("!*** Mfile You chose to open this file: " + chooser.getSelectedFile().getAbsolutePath());
      wfile = new MFile(chooser.getSelectedFile().getAbsolutePath());
      

      wpath = wfile.getAbsolutePath();
      if (wpath.lastIndexOf("\\") != -1) {
        wfolder = wpath.substring(0, wpath.lastIndexOf("\\")).replace('\\', '/');
        wiob = wpath.substring(wpath.lastIndexOf("\\") + 1);
      }
      if (wpath.lastIndexOf("/") != -1) {
        wfolder = wpath.substring(0, wpath.lastIndexOf("/"));
        wiob = wpath.substring(wpath.lastIndexOf("/") + 1);
      }
      MLogin.setLoginProperty("chooser.dir", wfolder);
    }
    
    return wfile;
  }
  
  private Vector wlinhas;
  private byte[] b;
  public Vector getAllFiles(boolean includeDirectorys) {
    Vector wv = new Vector();
    String[] wfilelist = list();
    for (int i = 0; (wfilelist != null) && (i < wfilelist.length); i++) {
      MFile wfile = new MFile(getPath() + separator + wfilelist[i]);
      System.out.println("!*** Mfile " + wfile.getPath());
      if (wfile.isDirectory()) {
        if (includeDirectorys) {
          wv.addElement(wfile);
        }
        Vector wvnew = wfile.getAllFiles(includeDirectorys);
        for (int j = 0; j < wvnew.size(); j++) {
          wv.addElement(wvnew.elementAt(j));
        }
      }
      else {
        wv.addElement(wfile);
      }
    }
    return wv;
  }
  
  public static boolean deleteDirectory(File path) { if (path.exists()) {
      File[] files = path.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          deleteDirectory(files[i]);
        } else {
          System.out.println("Elimina " + files[i].toString());
          files[i].delete();
        }
      }
    }
    return path.delete();
  }
}
