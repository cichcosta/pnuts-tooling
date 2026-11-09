package PT.Minimal.geral;

import javax.swing.JButton;

public class MToolBar extends javax.swing.JToolBar
{
  private java.util.Vector LAction = new java.util.Vector();
  
  public MToolBar() {
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setBackground(new java.awt.Color(190, 213, 239));
    } else {
      setBackground(new java.awt.Color(236, 233, 216));
    }
  }
  










  public void addPushButton(String sLabel, String actionCommand, boolean bEnabled, java.awt.Image normalImage, java.awt.Image pressedImage, java.awt.Image disabledImage)
  {
    JButton wjb;
    









    if (normalImage != null) {
      JButton wjb = new JButton(new javax.swing.ImageIcon(normalImage));
      wjb.setToolTipText(sLabel);
      wjb.setRolloverEnabled(true);



    }
    else if (sLabel.startsWith("#")) {
      JButton wjb = new JButton(sLabel.substring(1, sLabel.indexOf(" ")));
      wjb.setToolTipText(sLabel.substring(sLabel.indexOf(" ")).trim());
      wjb.setRolloverEnabled(true);
    } else {
      wjb = new JButton(sLabel);
    }
    
    wjb.setFont(new java.awt.Font("Dialog", 0, 11));
    wjb.setName(sLabel);
    wjb.setRequestFocusEnabled(false);
    if (sLabel.startsWith("#")) {
      wjb.setMargin(new java.awt.Insets(1, 1, 1, 1));
    } else {
      wjb.setMargin(new java.awt.Insets(1, 1, 1, 1));
    }
    wjb.setActionCommand(actionCommand);
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      wjb.setBackground(new java.awt.Color(158, 190, 231));
    } else {
      wjb.setBackground(new java.awt.Color(236, 233, 216));
    }
    
    add(wjb);
    LAction.addElement(actionCommand);
  }
  


  public javax.swing.JRadioButton addRadioButton(String sLabel, String actionCommand, boolean bEnabled, java.awt.Image normalImage, java.awt.Image pressedImage, java.awt.Image disabledImage)
  {
    javax.swing.JRadioButton wjb;
    

    if (normalImage != null) {
      javax.swing.JRadioButton wjb = new javax.swing.JRadioButton(new javax.swing.ImageIcon(normalImage));
      wjb.setToolTipText(sLabel);


    }
    else
    {

      wjb = new javax.swing.JRadioButton(sLabel);
    }
    wjb.setName(sLabel);
    wjb.setRequestFocusEnabled(false);
    wjb.setMargin(new java.awt.Insets(1, 1, 1, 1));
    wjb.setActionCommand(actionCommand);
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      wjb.setBackground(new java.awt.Color(158, 190, 231));
    } else {
      wjb.setBackground(new java.awt.Color(236, 233, 216));
    }
    
    add(wjb);
    LAction.addElement(actionCommand);
    return wjb;
  }
  


  public javax.swing.JToggleButton addToggleButton(String sLabel, String actionCommand, boolean bEnabled, java.awt.Image normalImage, java.awt.Image pressedImage, java.awt.Image disabledImage)
  {
    javax.swing.JToggleButton wjb;
    

    if (normalImage != null) {
      javax.swing.JToggleButton wjb = new javax.swing.JToggleButton(new javax.swing.ImageIcon(normalImage));
      wjb.setToolTipText(sLabel);

    }
    else
    {

      wjb = new javax.swing.JToggleButton(sLabel);
    }
    wjb.setName(sLabel);
    wjb.setRequestFocusEnabled(false);
    wjb.setMargin(new java.awt.Insets(1, 1, 1, 1));
    wjb.setActionCommand(actionCommand);
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      wjb.setBackground(new java.awt.Color(158, 190, 231));
    } else {
      wjb.setBackground(new java.awt.Color(236, 233, 216));
    }
    add(wjb);
    LAction.addElement(actionCommand);
    return wjb;
  }
  
  public void addButton(javax.swing.AbstractButton wbutton) {
    wbutton.setRequestFocusEnabled(false);
    wbutton.setMargin(new java.awt.Insets(1, 1, 1, 1));
    add(wbutton);
  }
  
  public void prepareToolBar()
  {
    setFloatable(false);
    add(javax.swing.Box.createHorizontalGlue());
  }
  
  public void addActionListener(MActionListener minAction)
  {
    for (int i = 0; i < getComponentCount(); i++) {
      if ((getComponent(i) instanceof javax.swing.AbstractButton)) {
        ((javax.swing.AbstractButton)getComponent(i)).addActionListener(minAction);
      }
    }
  }
  
  public void addItemListener(MItemListener minItemListener) {
    for (int i = 0; i < getComponentCount(); i++) {
      if ((getComponent(i) instanceof JButton)) {
        ((JButton)getComponent(i)).addItemListener(minItemListener);
      }
    }
  }
  

  public String getActionCommand(int i) { return (String)LAction.elementAt(i); }
  
  public java.awt.Dimension getPreferredSize() {
    doLayout();
    validate();
    return getLayout().preferredLayoutSize(this);
  }
}
