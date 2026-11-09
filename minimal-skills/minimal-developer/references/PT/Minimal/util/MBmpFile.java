package PT.Minimal.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;















public class MBmpFile
  extends Component
{
  private static final int BITMAPFILEHEADER_SIZE = 14;
  private static final int BITMAPINFOHEADER_SIZE = 40;
  private byte[] bitmapFileHeader = new byte[14];
  private byte[] bfType = { 66, 77 };
  

  private int bfSize = 0;
  private int bfReserved1 = 0;
  private int bfReserved2 = 0;
  private int bfOffBits = 54;
  

  private byte[] bitmapInfoHeader = new byte[40];
  private int biSize = 40;
  private int biWidth = 0;
  private int biHeight = 0;
  private int biPlanes = 1;
  private int biBitCount = 24;
  private int biCompression = 0;
  private int biSizeImage = 196608;
  private int biXPelsPerMeter = 0;
  private int biYPelsPerMeter = 0;
  private int biClrUsed = 0;
  private int biClrImportant = 0;
  





  private int[] bitmap;
  




  private FileOutputStream fo;
  





  public MBmpFile() {}
  





  public void saveBitmap(String parFilename, Image parImage, int parWidth, int parHeight)
  {
    try
    {
      fo = new FileOutputStream(parFilename);
      save(parImage, parWidth, parHeight);
      fo.close();
    } catch (Exception saveEx) {
      saveEx.printStackTrace();
    }
  }
  












  private void save(Image parImage, int parWidth, int parHeight)
  {
    try
    {
      convertImage(parImage, parWidth, parHeight);
      writeBitmapFileHeader();
      writeBitmapInfoHeader();
      writeBitmap();
    } catch (Exception saveEx) {
      saveEx.printStackTrace();
    }
  }
  












  private boolean convertImage(Image parImage, int parWidth, int parHeight)
  {
    bitmap = new int[parWidth * parHeight];
    
    PixelGrabber pg = new PixelGrabber(parImage, 0, 0, parWidth, parHeight, bitmap, 0, parWidth);
    try
    {
      pg.grabPixels();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return false;
    }
    
    int pad = (4 - parWidth * 3 % 4) * parHeight;
    biSizeImage = (parWidth * parHeight * 3 + pad);
    bfSize = (biSizeImage + 14 + 40);
    biWidth = parWidth;
    biHeight = parHeight;
    
    return true;
  }
  

















  private void writeBitmap()
  {
    byte[] rgb = new byte[3];
    

    int size = biWidth * biHeight - 1;
    int pad = 4 - biWidth * 3 % 4;
    if (pad == 4) {
      pad = 0;
    }
    int rowCount = 1;
    int padCount = 0;
    int rowIndex = size - biWidth;
    int lastRowIndex = rowIndex;
    try
    {
      for (int j = 0; j < size; j++) {
        int value = bitmap[rowIndex];
        rgb[0] = ((byte)(value & 0xFF));
        rgb[1] = ((byte)(value >> 8 & 0xFF));
        rgb[2] = ((byte)(value >> 16 & 0xFF));
        fo.write(rgb);
        if (rowCount == biWidth) {
          padCount += pad;
          for (int i = 1; i <= pad; i++) {
            fo.write(0);
          }
          rowCount = 1;
          rowIndex = lastRowIndex - biWidth;
          lastRowIndex = rowIndex;
        } else {
          rowCount++;
        }
        rowIndex++;
      }
      

      bfSize += padCount - pad;
      biSizeImage += padCount - pad;
    } catch (Exception wb) {
      wb.printStackTrace();
    }
  }
  




  private void writeBitmapFileHeader()
  {
    try
    {
      fo.write(bfType);
      fo.write(intToDWord(bfSize));
      fo.write(intToWord(bfReserved1));
      fo.write(intToWord(bfReserved2));
      fo.write(intToDWord(bfOffBits));
    }
    catch (Exception wbfh) {
      wbfh.printStackTrace();
    }
  }
  





  private void writeBitmapInfoHeader()
  {
    try
    {
      fo.write(intToDWord(biSize));
      fo.write(intToDWord(biWidth));
      fo.write(intToDWord(biHeight));
      fo.write(intToWord(biPlanes));
      fo.write(intToWord(biBitCount));
      fo.write(intToDWord(biCompression));
      fo.write(intToDWord(biSizeImage));
      fo.write(intToDWord(biXPelsPerMeter));
      fo.write(intToDWord(biYPelsPerMeter));
      fo.write(intToDWord(biClrUsed));
      fo.write(intToDWord(biClrImportant));
    } catch (Exception wbih) {
      wbih.printStackTrace();
    }
  }
  










  private byte[] intToWord(int parValue)
  {
    byte[] retValue = new byte[2];
    
    retValue[0] = ((byte)(parValue & 0xFF));
    retValue[1] = ((byte)(parValue >> 8 & 0xFF));
    
    return retValue;
  }
  










  private byte[] intToDWord(int parValue)
  {
    byte[] retValue = new byte[4];
    
    retValue[0] = ((byte)(parValue & 0xFF));
    retValue[1] = ((byte)(parValue >> 8 & 0xFF));
    retValue[2] = ((byte)(parValue >> 16 & 0xFF));
    retValue[3] = ((byte)(parValue >> 24 & 0xFF));
    
    return retValue;
  }
  






















  public Image loadbitmap(String filename)
  {
    System.out.println("loading:" + filename);
    try {
      FileInputStream fs = new FileInputStream(filename);
      int bflen = 14;
      byte[] bf = new byte[bflen];
      fs.read(bf, 0, bflen);
      int bilen = 40;
      byte[] bi = new byte[bilen];
      fs.read(bi, 0, bilen);
      

      int nsize = (bf[5] & 0xFF) << 24 | (bf[4] & 0xFF) << 16 | (bf[3] & 0xFF) << 8 | bf[2] & 0xFF;
      
      System.out.println("File type is :" + (char)bf[0] + (char)bf[1]);
      System.out.println("Size of file is :" + nsize);
      
      int nbisize = (bi[3] & 0xFF) << 24 | (bi[2] & 0xFF) << 16 | (bi[1] & 0xFF) << 8 | bi[0] & 0xFF;
      
      System.out.println("Size of bitmapinfoheader is :" + nbisize);
      
      int nwidth = (bi[7] & 0xFF) << 24 | (bi[6] & 0xFF) << 16 | (bi[5] & 0xFF) << 8 | bi[4] & 0xFF;
      
      System.out.println("Width is :" + nwidth);
      
      int nheight = (bi[11] & 0xFF) << 24 | (bi[10] & 0xFF) << 16 | (bi[9] & 0xFF) << 8 | bi[8] & 0xFF;
      
      System.out.println("Height is :" + nheight);
      
      int nplanes = (bi[13] & 0xFF) << 8 | bi[12] & 0xFF;
      System.out.println("Planes is :" + nplanes);
      
      int nbitcount = (bi[15] & 0xFF) << 8 | bi[14] & 0xFF;
      System.out.println("BitCount is :" + nbitcount);
      

      int ncompression = bi[19] << 24 | bi[18] << 16 | bi[17] << 8 | bi[16];
      
      System.out.println("Compression is :" + ncompression);
      
      int nsizeimage = (bi[23] & 0xFF) << 24 | (bi[22] & 0xFF) << 16 | (bi[21] & 0xFF) << 8 | bi[20] & 0xFF;
      
      System.out.println("SizeImage is :" + nsizeimage);
      
      int nxpm = (bi[27] & 0xFF) << 24 | (bi[26] & 0xFF) << 16 | (bi[25] & 0xFF) << 8 | bi[24] & 0xFF;
      
      System.out.println("X-Pixels per meter is :" + nxpm);
      
      int nypm = (bi[31] & 0xFF) << 24 | (bi[30] & 0xFF) << 16 | (bi[29] & 0xFF) << 8 | bi[28] & 0xFF;
      
      System.out.println("Y-Pixels per meter is :" + nypm);
      
      int nclrused = (bi[35] & 0xFF) << 24 | (bi[34] & 0xFF) << 16 | (bi[33] & 0xFF) << 8 | bi[32] & 0xFF;
      
      System.out.println("Colors used are :" + nclrused);
      
      int nclrimp = (bi[39] & 0xFF) << 24 | (bi[38] & 0xFF) << 16 | (bi[37] & 0xFF) << 8 | bi[36] & 0xFF;
      
      System.out.println("Colors important are :" + nclrimp);
      Image image;
      Image image; if (nbitcount == 24)
      {

        int npad = nsizeimage / nheight - nwidth * 3;
        int[] ndata = new int[nheight * nwidth];
        byte[] brgb = new byte[(nwidth + npad) * 3 * nheight];
        fs.read(brgb, 0, (nwidth + npad) * 3 * nheight);
        int nindex = 0;
        for (int j = 0; j < nheight; j++) {
          for (int i = 0; i < nwidth; i++) {
            ndata[(nwidth * (nheight - j - 1) + i)] = (0xFF000000 | (brgb[(nindex + 2)] & 0xFF) << 16 | (brgb[(nindex + 1)] & 0xFF) << 8 | brgb[nindex] & 0xFF);
            


            nindex += 3;
          }
          nindex += npad;
        }
        
        image = createImage(new MemoryImageSource(nwidth, nheight, ndata, 0, nwidth)); } else { Image image;
        if (nbitcount == 8)
        {


          int nNumColors = 0;
          if (nclrused > 0) {
            nNumColors = nclrused;
          } else {
            nNumColors = 1 << nbitcount;
          }
          System.out.println("The number of Colors is" + nNumColors);
          


          if (nsizeimage == 0) {
            nsizeimage = (nwidth * nbitcount + 31 & 0xFFFFFFE0) >> 3;
            nsizeimage *= nheight;
            System.out.println("nsizeimage (backup) is" + nsizeimage);
          }
          

          int[] npalette = new int[nNumColors];
          byte[] bpalette = new byte[nNumColors * 4];
          fs.read(bpalette, 0, nNumColors * 4);
          int nindex8 = 0;
          for (int n = 0; n < nNumColors; n++) {
            npalette[n] = (0xFF000000 | (bpalette[(nindex8 + 2)] & 0xFF) << 16 | (bpalette[(nindex8 + 1)] & 0xFF) << 8 | bpalette[nindex8] & 0xFF);
            
            nindex8 += 4;
          }
          



          int npad8 = nsizeimage / nheight - nwidth;
          System.out.println("nPad is:" + npad8);
          
          int[] ndata8 = new int[nwidth * nheight];
          byte[] bdata = new byte[(nwidth + npad8) * nheight];
          fs.read(bdata, 0, (nwidth + npad8) * nheight);
          nindex8 = 0;
          for (int j8 = 0; j8 < nheight; j8++) {
            for (int i8 = 0; i8 < nwidth; i8++) {
              ndata8[(nwidth * (nheight - j8 - 1) + i8)] = npalette[(bdata[nindex8] & 0xFF)];
              nindex8++;
            }
            nindex8 += npad8;
          }
          
          image = createImage(new MemoryImageSource(nwidth, nheight, ndata8, 0, nwidth));
        } else {
          System.out.println("Not a 24-bit or 8-bit Windows Bitmap, aborting...");
          image = (Image)null;
        }
      }
      fs.close();
      return image;
    } catch (Exception e) {
      System.out.println("Caught exception in loadbitmap!");
    }
    return (Image)null;
  }
}
