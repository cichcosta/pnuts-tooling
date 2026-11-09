package PT.Minimal.util;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.PixelGrabber;
import java.io.PrintStream;










public class MImageSelection
  implements Transferable, ClipboardOwner
{
  public static DataFlavor imageFlavor;
  public static DataFlavor bitmapFlavor;
  private DataFlavor[] supportedFlavors = { imageFlavor, bitmapFlavor };
  





  private Image picture;
  






  public MImageSelection(Image parPicture)
  {
    picture = parPicture;
    try {
      imageFlavor = new DataFlavor(Class.forName("java.awt.Image"), "AWT Image");
      bitmapFlavor = new DataFlavor("image/gif", "GIF Image");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
  











  public synchronized DataFlavor[] getTransferDataFlavors()
  {
    return supportedFlavors;
  }
  












  public boolean isDataFlavorSupported(DataFlavor parFlavor)
  {
    return parFlavor.equals(imageFlavor);
  }
  












  public synchronized Object getTransferData(DataFlavor parFlavor)
    throws UnsupportedFlavorException
  {
    if (parFlavor.equals(imageFlavor))
      return picture;
    if (parFlavor.equals(bitmapFlavor))
    {
      return getBitmapOfImage();
    }
    throw new UnsupportedFlavorException(imageFlavor);
  }
  











  private int[] getBitmapOfImage()
  {
    int[] bitmap = new int['✐'];
    
    PixelGrabber grabber = new PixelGrabber(picture, 0, 0, 100, 100, bitmap, 0, 100);
    try {
      grabber.grabPixels();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return bitmap;
  }
  











  public void lostOwnership(Clipboard parClipboard, Transferable parTransferable)
  {
    System.out.println("Lost ownership");
  }
}
