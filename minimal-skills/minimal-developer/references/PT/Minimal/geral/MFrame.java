package PT.Minimal.geral;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JRootPane;








public class MFrame
  extends JFrame
{
  public boolean inAnApplet = false;
  


  public static Hashtable LFrame;
  

  public static Hashtable LFrameInv;
  

  private boolean isBlocked = false;
  


  private String Ent;
  



  public MFrame()
  {
    this("Minimal");
    
    setBackground(new Color(190, 213, 239));
  }
  








  public MFrame(String wtitle)
  {
    super(wtitle);
    
    if (wtitle.equals("Menu Minimal")) {
      setBackground(new Color(224, 233, 254));
    } else {
      setBackground(new Color(190, 213, 239));
    }
    

    setIconImage(MImages.getPreparedImage("minicon.gif"));
    getContentPane().setLayout(new BorderLayout());
  }
  





















  public void setBusy(boolean busy) {}
  




















  public void setForm(MForm wform)
  {
    getContentPane().add(wform, "Center");
    
    addWindowListener(wform.getWindowListener());
    addFocusListener(wform.getFocusListener());
    














    Component wok = MUtil.getComponent(wform.getBotPanel(), "wok");
    
    if ((wok != null) && ((wok instanceof MButton)) && 
      (wform.getOKButton() == null)) {
      getRootPane().setDefaultButton((MButton)wok);
      wform.setOKButton((MButton)wok);
    }
    
    setEnt(wform.getEnt());
    wform.setFormParent(this);
  }
  





















  public void center(int width, int height, boolean bShow)
  {
    int xloc = 0;
    int yloc = 0;
    

    Toolkit tk;
    
    if ((tk = getToolkit()) != null) {
      Dimension screenSize = tk.getScreenSize();
      
      if (width > width) {
        width = width;
      }
      xloc = (width - width) / 2;
      
      if (height > height) {
        height = height;
      }
      yloc = (height - height) / 2;
    }
    
    if (bShow) {
      setVisible(true);
    }
    
    setBounds(xloc, yloc, width, height);
  }
  
  public void centerRigth(int width, int height, boolean bShow) {
    int xloc = 0;
    int yloc = 0;
    

    Toolkit tk;
    
    if ((tk = getToolkit()) != null) {
      Dimension screenSize = tk.getScreenSize();
      
      if (width > width) {
        width = width;
      }
      xloc = (width - width) / 2;
      xloc += 110;
      
      if (height > height) {
        height = height;
      }
      yloc = (height - height) / 2;
      yloc += 55;
    }
    
    if (bShow) {
      setVisible(true);
    }
    
    setBounds(xloc, yloc, width, height);
  }
  









  public static void setModalParent(MFrame wparent, MFrame wchild)
  {
    if (LFrame == null) {
      LFrame = new Hashtable();
      LFrameInv = new Hashtable();
    }
    
    LFrame.put(wparent, wchild);
    LFrameInv.put(wchild, wparent);
    wparent.setBlocked(true);
  }
  











  public static MFrame getModalParent(MFrame wchild)
  {
    MFrame wframe = null;
    
    if ((LFrame != null) && (wchild != null)) {
      wframe = (MFrame)LFrameInv.get(wchild);
    }
    



    return wframe;
  }
  










  public static MFrame getModalChild(MFrame wparent)
  {
    MFrame wframe = null;
    
    if (LFrame != null) {
      wframe = (MFrame)LFrame.get(wparent);
    }
    



    return wframe;
  }
  










  public static MFrame getFinalModalChild(MFrame wparent)
  {
    MFrame wframe = wparent;
    
    if (LFrame != null) {
      while (LFrame.get(wframe) != null) { wframe = (MFrame)LFrame.get(wframe);
      }
    }
    


    return wframe;
  }
  








  public static void removeModalChild(MFrame wchild)
  {
    MFrame wframe = null;
    
    if ((LFrame != null) && 
      (LFrameInv.get(wchild) != null)) {
      wframe = (MFrame)LFrameInv.get(wchild);
      LFrameInv.remove(wchild);
      LFrame.remove(wframe);
      wframe.setBlocked(false);
      wframe.setVisible(true);
    }
  }
  













  public void setBlocked(boolean wboo)
  {
    isBlocked = wboo;
    setEnabled(!isBlocked);
  }
  








  public boolean getBlocked()
  {
    return isBlocked;
  }
  








  public void setEnt(String ws)
  {
    Ent = ws;
  }
  








  public String getEnt()
  {
    return Ent;
  }
}
