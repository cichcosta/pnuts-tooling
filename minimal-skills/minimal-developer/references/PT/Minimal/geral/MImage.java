package PT.Minimal.geral;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;






public class MImage
  extends JScrollPane
  implements MField, Serializable
{
  public String minNome;
  public String minAnchor;
  public int minRelPosX = 0;
  public int minRelPosY = 0;
  public int minPosX = 0;
  public int minPosY = 0;
  public boolean minBreakLn = false;
  public int minWidth = 1000;
  public int minHeight = 1000;
  
  public String minCmd;
  public String minCmdArgs;
  public String minTipoCmd;
  private String atribType = "4";
  private String dataType = "BINARY";
  private String displayType = "N";
  private boolean mustFill = false;
  private String minMask = " ";
  private String minInternalMask = "";
  private String minMsg = " ";
  
  private MForm minForm;
  private Image image;
  private int iCurrentImage = -1;
  private Vector vImages;
  private boolean apagouImagem = false;
  private int width;
  private int height;
  private JViewport port; private static boolean bPreview = false;
  private static boolean bChanged = false;
  


  private JPanel panel = new JPanel()
  {



    public void paint(Graphics g)
    {


      g.clearRect(0, 0, getParent().getWidth(), getParent().getHeight());
      
      if (null != image) {
        g.drawImage(image, 0, 0, image.getWidth(this), image.getHeight(this), this);
      } else {
        String sNimagem = new String("Não existe imagem associada ao registo.");
        int iFontLength = g.getFont().getSize() / 2;
        
        g.drawString(sNimagem, (getWidth() - sNimagem.length() * iFontLength) / 2, getHeight() / 2);
      }
    }
  };
  











  public MImage(String wnome)
  {
    minNome = wnome;
    setAtrrib();
  }
  









  public MImage(String wnome, String wcmd)
  {
    this(wnome, "", wcmd);
  }
  










  public MImage(String wnome, String wlabel, String wcmd)
  {
    this(wnome, wlabel, wcmd, false);
  }
  











  public MImage(String wnome, String wlabel, String wcmd, boolean wbreakln)
  {
    System.out.println("!***MImage: " + wnome + " | " + wlabel + " | " + wcmd + " | " + wbreakln);
    
    image = null;
    minNome = wnome;
    setCmd(wcmd);
    minBreakLn = wbreakln;
    







    setAtrrib();
    image = null;
    setScrollBars();
  }
  
  public MImage(String wnome, String wlabel, String wcmd, boolean wbreakln, boolean scroll) { minNome = wnome;
    setCmd(wcmd);
    minBreakLn = wbreakln;
    

    setAtrrib();
    image = null;
    if (scroll) {
      setScrollBars();
    }
  }
  







  public Vector getImageVector()
  {
    return vImages;
  }
  








  public void setImageVector(Vector obj)
  {
    vImages = obj;
  }
  





  public int getImagePosition()
  {
    return iCurrentImage;
  }
  






  public void setImagePosition(int i)
  {
    iCurrentImage = i;
  }
  






  public void setScrollBars()
  {
    System.out.println("!***MImage setScrollBars()");
    port = getViewport();
    port.add(panel);
    setHorizontalScrollBarPolicy(32);
    setVerticalScrollBarPolicy(22);
  }
  






  public void unsetScrollBars()
  {
    port.remove(panel);
  }
  










  public Dimension getPreferredSize()
  {
    if ((minWidth == 1000) && (minHeight == 1000)) {
      return new Dimension(getParent().getWidth() - 15, getParent().getHeight() - 55);
    }
    return new Dimension(minWidth, minHeight);
  }
  









  public void setImage(Image img)
  {
    System.out.println("!***MImage setImage:" + img);
    
    if (image != null) {
      image.flush();
    }
    image = img;
    if (image != null) {
      width = img.getWidth(null);
      height = img.getHeight(null);
      panel.setSize(width, height);
      panel.setPreferredSize(new Dimension(width, height));
    } else {
      panel.setSize(new Dimension(getParent().getWidth() - 40, getParent().getHeight() - 80));
      panel.setPreferredSize(new Dimension(getParent().getWidth() - 40, getParent().getHeight() - 80));
    }
  }
  








  public Image getImage()
  {
    return image;
  }
  








  public void setApagouImagem(boolean b)
  {
    apagouImagem = b;
  }
  








  public boolean getApagouImagem()
  {
    return apagouImagem;
  }
  






  public void setAtrrib()
  {
    setName(minNome);
  }
  








  public void setCmd(String wcmd)
  {
    minCmd = wcmd;
  }
  








  public Dimension getMinimumSize()
  {
    return super.getPreferredSize();
  }
  








  public void setWidth(int width)
  {
    minWidth = width;
  }
  








  public void setMustFill(boolean wboo)
  {
    mustFill = wboo;
  }
  








  public boolean getMustFill()
  {
    return mustFill;
  }
  








  public void setMsg(String wmsg)
  {
    minMsg = wmsg;
  }
  








  public String getMsg()
  {
    return minMsg;
  }
  








  public JPanel getImagePanel()
  {
    return panel;
  }
  








  public void setDisplayType(String ws)
  {
    displayType = ws;
  }
  








  public String getDisplayType()
  {
    return displayType;
  }
  








  public void setMask(String wmask)
  {
    minMask = wmask;
  }
  








  public String getMask()
  {
    return minMask;
  }
  








  public String getFormatedText()
  {
    return toString();
  }
  








  public String getUnFormatedText()
  {
    return toString();
  }
  








  public void setInternalMask(String wmask)
  {
    minInternalMask = wmask;
  }
  








  public String getInternalMask()
  {
    return minInternalMask;
  }
  








  public void setAtribType(String wat)
  {
    atribType = wat;
  }
  








  public String getAtribType()
  {
    return atribType;
  }
  








  public void setDataType(String ws)
  {
    dataType = ws;
  }
  








  public String getDataType()
  {
    return dataType;
  }
  








  public void setCommand(String wcmd)
  {
    minCmd = wcmd;
  }
  








  public String getCommand()
  {
    return minCmd;
  }
  








  public void setCommandArgs(String wcmdargs)
  {
    minCmdArgs = wcmdargs;
  }
  








  public String getCommandArgs()
  {
    return minCmdArgs;
  }
  








  public String getMinAnchor()
  {
    return minAnchor;
  }
  








  public int getMinRelPosX()
  {
    return minRelPosX;
  }
  








  public int getMinRelPosY()
  {
    return minRelPosY;
  }
  








  public int getMinPosX()
  {
    return minPosX;
  }
  








  public int getMinPosY()
  {
    return minPosY;
  }
  








  public boolean getMinBreakLn()
  {
    return minBreakLn;
  }
  








  public boolean isFocusTraversable()
  {
    return false;
  }
  





  public void setChanged(boolean value)
  {
    bChanged = value;
  }
  








  public boolean isChanged()
  {
    return bChanged;
  }
  
  public void setMode(boolean wmode) {}
  
  public void setAtribFormat(String wmode) {}
}
