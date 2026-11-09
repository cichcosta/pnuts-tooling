package PT.Minimal.geral;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.io.Serializable;
import javax.swing.JPanel;








public class MPanel
  extends JPanel
  implements Serializable
{
  public String minNome;
  public String minAnchor;
  public int minRelPosX = 0;
  public int minRelPosY = 0;
  public int minPosX = 0;
  public int minPosY = 0;
  public boolean minBreakLn = false;
  public int minWidth = 100;
  public int minPanelStyle = 0;
  

  public static final int NORMAL = 0;
  
  public static final int IN = 1;
  
  public static final int OUT = 2;
  
  public static final int STRONG = 3;
  

  public MPanel()
  {
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(215, 228, 244));
    } else {
      setBackground(new Color(236, 233, 216));
    }
  }
  








  public MPanel(String wnome)
  {
    minNome = wnome;
    setName(minNome);
    

    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(215, 228, 244));
    } else {
      setBackground(new Color(236, 233, 216));
    }
  }
  












  public MPanel(String wnome, int wstyle)
  {
    minNome = wnome;
    minPanelStyle = wstyle;
    setName(minNome);
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(215, 228, 244));
    } else {
      setBackground(new Color(236, 233, 216));
    }
  }
  










  public void setStyle(int wstyle)
  {
    minPanelStyle = wstyle;
    
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new Color(215, 228, 244));
    } else {
      setBackground(new Color(236, 233, 216));
    }
  }
  











  public Dimension getPreferredSize()
  {
    doLayout();
    validate();
    return getLayout().preferredLayoutSize(this);
  }
  








  public void addButtons(int wbuttons)
  {
    MButton wok = null;
    MButton wend = null;
    
    if (wbuttons - 2 >= 0) {
      wbuttons -= 2;
      wok = new MButton("OK");
      wok.setName("wok");
      wok.setCmd("OKDIALOG");
    }
    if (wbuttons - 1 >= 0) {
      wbuttons--;
      wend = new MButton("wend", MUtil.lang("\"Cancelar\""), "CLOSEWINDOW");
      wend.setCmd("CLOSEWINDOW");
    }
    LayoutManager lm = getLayout();
    
    if (((lm instanceof BorderLayout)) || ((lm instanceof GridLayout))) {
      if (wend != null) {
        add("East", wend);
      }
      if (wok != null) {
        add("East", wok);
      }
    } else {
      if (wok != null) {
        add(wok);
      }
      if (wend != null) {
        add(wend);
      }
    }
  }
}
