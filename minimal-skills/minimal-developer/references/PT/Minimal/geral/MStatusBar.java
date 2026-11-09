package PT.Minimal.geral;

import java.awt.Font;
import java.util.Properties;
import java.util.Vector;

public class MStatusBar extends MPanel implements java.io.Serializable
{
  private Vector LPanes = new Vector();
  private String wklc = "klc";
  Properties p = MUtil.myProp;
  
  public MStatusBar(String wnome) {
    minNome = wnome;
    setName(minNome);
    LPanes.addElement(new MLabel("Pane1", 340));
    LPanes.addElement(new MLabel("Pane2", 15));
    LPanes.addElement(new MLabel("Pane3", 50));
    LPanes.addElement(new MLabel("Pane4", 60));
    LPanes.addElement(new MLabel("Pane5", 230));
    
    MPanel leftPanel = new MPanel("leftPanel");
    leftPanel.setLayout(new java.awt.FlowLayout(0));
    
    MPanel rightPanel = new MPanel("rightPanel");
    rightPanel.setLayout(new java.awt.FlowLayout(1));
    
    ((MLabel)LPanes.elementAt(0)).setHorizontalAlignment(2);
    ((MLabel)LPanes.elementAt(0)).setText(" ");
    leftPanel.add((MLabel)LPanes.elementAt(0));
    
    ((MLabel)LPanes.elementAt(1)).setHorizontalAlignment(0);
    ((MLabel)LPanes.elementAt(1)).setText("0");
    ((MLabel)LPanes.elementAt(1)).setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    rightPanel.add((MLabel)LPanes.elementAt(1));
    
    ((MLabel)LPanes.elementAt(2)).setHorizontalAlignment(0);
    ((MLabel)LPanes.elementAt(2)).setText((String)p.get("user.name"));
    ((MLabel)LPanes.elementAt(2)).setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    rightPanel.add((MLabel)LPanes.elementAt(2));
    
    ((MLabel)LPanes.elementAt(3)).setHorizontalAlignment(0);
    ((MLabel)LPanes.elementAt(3)).setText(wnome);
    ((MLabel)LPanes.elementAt(3)).setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    rightPanel.add((MLabel)LPanes.elementAt(3));
    
    ((MLabel)LPanes.elementAt(4)).setHorizontalAlignment(0);
    

    String wclc = "          ";
    if ((!wnome.startsWith("Menu")) && (MLogin.getDefaultAmbient().equals("SQL Server"))) {
      String wsel = " select nvl(max(iunq),'<|>MNDM') from sunqt0 ";
      wsel = wsel + " where iunq like '<|>%' ";
      
      if ((wclc.substring(3, 7).equals("MNDM")) || (wclc.substring(3, 7).equals("MNIT"))) {
        if (wclc.substring(3, 7).equals("MNDM")) {
          ((MLabel)LPanes.elementAt(4)).setText("Software MINIMAL de Demonstração");
        }
        if (wclc.substring(3, 7).equals("MNIT")) {
          ((MLabel)LPanes.elementAt(4)).setText("Versão Interna da MINIMAL p/ Testes");
        }
      } else {
        int i = wclc.indexOf("|");
        if (i > 0) i = wclc.indexOf("|", i + 1);
        if (i > 0) {
          wclc = wclc.substring(i + 1).trim() + "  *  ";
        } else {
          wclc = "";
        }
        

        if (MResData.getQueries(wklc).length() > 18) {
          wclc = MResData.getQueries(wklc).substring(18).trim() + " : ";
        }
        
        if ((MLogin.hasLoginProperty("user.xdsn")) && (p.get("user.xdsn").equals(null))) {
          ((MLabel)LPanes.elementAt(4)).setText(wclc + (String)p.get("user.dsn"));
        } else {
          ((MLabel)LPanes.elementAt(4)).setText(wclc + (String)p.get("user.xdsn"));
        }
      }
    }
    else
    {
      if (MResData.getQueries(wklc).length() > 18) {
        wclc = MResData.getQueries(wklc).substring(18).trim() + " : ";
      }
      ((MLabel)LPanes.elementAt(4)).setText(wclc + (String)p.get("user.xdsn"));
    }
    
    ((MLabel)LPanes.elementAt(4)).setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    rightPanel.add((MLabel)LPanes.elementAt(4));
    
    Font wfont = ((MLabel)LPanes.elementAt(4)).getFont();
    Font wdesfont = new Font(wfont.getName(), 0, 11);
    
    for (int j = 0; j < LPanes.size(); j++) {
      ((MLabel)LPanes.elementAt(j)).setFont(wdesfont);
    }
    
    setLayout(new java.awt.BorderLayout());
    add(leftPanel, "Center");
    add(rightPanel, "East");
    
    setBackground(new java.awt.Color(195, 213, 253));
  }
  

  public void setPaneText(int npane, String ws)
  {
    ((MLabel)LPanes.elementAt(npane)).setText(ws);
  }
  
  public java.awt.Dimension getPreferredSize() {
    doLayout();
    validate();
    return getLayout().preferredLayoutSize(this);
  }
}
