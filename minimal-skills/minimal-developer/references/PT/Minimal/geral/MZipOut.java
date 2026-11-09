package PT.Minimal.geral;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;








public class MZipOut
{
  ByteArrayOutputStream wba;
  ZipOutputStream fos;
  
  public MZipOut()
  {
    wba = new ByteArrayOutputStream();
    fos = new ZipOutputStream(wba);
  }
  

  public void addObject(String wname, Object wobj)
    throws MError
  {
    try
    {
      ZipEntry wze = new ZipEntry(wname);
      wze.setMethod(8);
      fos.putNextEntry(wze);
      

      ObjectOutputStream os = new ObjectOutputStream(fos);
      os.writeObject(wobj);
      os.flush();
    }
    catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    }
  }
  
  public void add(String wname, byte[] b)
    throws MError
  {
    try
    {
      ZipEntry wze = new ZipEntry(wname);
      wze.setMethod(8);
      fos.putNextEntry(wze);
      fos.write(b);
      
      fos.closeEntry();
    } catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    }
  }
  
  public byte[] getZipBytes() throws MError {
    close();
    return wba.toByteArray();
  }
  
  public void close() throws MError {
    try {
      fos.close();
    } catch (IOException e) {
      MError werr = new MError(e);
      throw werr;
    }
  }
}
