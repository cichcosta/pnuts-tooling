package PT.Minimal.geral;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;







public class MZipIn
{
  byte[] b;
  
  public MZipIn(byte[] wb)
  {
    b = wb;
  }
  
  public boolean contains(String wname) throws MError {
    boolean wboo = false;
    



    if (b == null) {
      return false;
    }
    
    ByteArrayInputStream wba = new ByteArrayInputStream(b);
    ZipInputStream fis = new ZipInputStream(wba);
    try {
      ZipEntry wze = getZipEntry(wname, fis);
      if (wze != null) {
        wboo = true;
      }
      
      fis.close();
    } catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    }
    return wboo;
  }
  
  public Object getObject(String wname) throws MError
  {
    Object wobj = null;
    




    ByteArrayInputStream wba = new ByteArrayInputStream(b);
    ZipInputStream fis = new ZipInputStream(wba);
    try {
      ZipEntry wze = getZipEntry(wname, fis);
      if (wze != null) {
        System.out.println(wze.getName());
        
        ObjectInputStream is = new ObjectInputStream(fis);
        wobj = is.readObject();
      }
      fis.close();
    } catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    } catch (ClassNotFoundException e) {
      MError werr = new MError(e);
      throw werr;
    }
    
    return wobj;
  }
  
  public Object getObject() throws MError
  {
    Object wobj = null;
    




    ByteArrayInputStream wba = new ByteArrayInputStream(b);
    ZipInputStream fis = new ZipInputStream(wba);
    try {
      ZipEntry wze = fis.getNextEntry();
      if (wze != null) {
        System.out.println(wze.getName());
        
        ObjectInputStream is = new ObjectInputStream(fis);
        wobj = is.readObject();
      }
      fis.close();
    } catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    } catch (ClassNotFoundException e) {
      MError werr = new MError(e);
      throw werr;
    }
    
    return wobj;
  }
  
  public byte[] get(String wname) throws MError
  {
    byte[] wb = null;
    



    ByteArrayInputStream wba = new ByteArrayInputStream(b);
    ZipInputStream fis = new ZipInputStream(wba);
    try {
      ZipEntry wze = getZipEntry(wname, fis);
      if (wze != null) {
        System.out.println(wze.getName());
        wb = new byte[(int)wze.getCompressedSize()];
        fis.read(wb, 0, wb.length);
      }
      fis.close();
    } catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    }
    return wb;
  }
  
  public byte[] get() throws MError {
    byte[] wb = null;
    




    ByteArrayInputStream wba = new ByteArrayInputStream(b);
    ZipInputStream fis = new ZipInputStream(wba);
    try {
      ZipEntry wze = fis.getNextEntry();
      if (wze != null) {
        System.out.println(wze.getName());
        wb = new byte[(int)wze.getCompressedSize()];
        fis.read(wb, 0, wb.length);
      }
      fis.close();
    } catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    }
    return wb;
  }
  
  public ZipEntry getZipEntry(String wname, ZipInputStream fis) throws MError {
    ZipEntry wze = null;
    try {
      for (wze = fis.getNextEntry(); (wze != null) && (!wze.getName().equals(wname)); wze = fis.getNextEntry()) {}
    } catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    }
    return wze;
  }
}
