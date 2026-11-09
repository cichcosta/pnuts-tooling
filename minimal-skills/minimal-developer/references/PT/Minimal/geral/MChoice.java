package PT.Minimal.geral;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class MChoice extends JComboBox
{
  public String minNome;
  public String minAnchor;
  public String minMsg = " ";
  public int minRelPosX = 0;
  public int minRelPosY = 0;
  public int minPosX = 0;
  public int minPosY = 0;
  public boolean minBreakLn = false;
  public int minWidth = 250;
  private static java.awt.event.FocusListener focusListener;
  
  public MChoice(String wnome) { minNome = wnome;
    setAtrib();
  }
  
  public void setAtrib()
  {
    setName(minNome);
    setFont(new java.awt.Font("Dialog", 0, 12));
  }
  
  public java.awt.Dimension getMinimumSize()
  {
    java.awt.Dimension d = super.getPreferredSize();
    if (width > minWidth) {
      minWidth = width;
    }
    width = minWidth;
    height = MUtil.CHOICEBOX_HEIGHT;
    return d;
  }
  
  public java.awt.Dimension getPreferredSize() {
    return getMinimumSize();
  }
  
  public void setWidth(int width) { minWidth = width; }
  
  public int getWidth()
  {
    return minWidth;
  }
  
  class MemComboAgent extends java.awt.event.KeyAdapter
  {
    protected JComboBox m_comboBox;
    protected JTextField m_editor;
    
    public MemComboAgent(JComboBox comboBox) {
      m_comboBox = comboBox;
      m_editor = ((JTextField)comboBox.getEditor().getEditorComponent());
      m_editor.addKeyListener(this);
    }
    
    public void keyReleased(java.awt.event.KeyEvent e) {
      char ch = e.getKeyChar();
      



      if (e.getKeyCode() == 10) {
        java.awt.Cursor waitCursor = new java.awt.Cursor(3);
        
        setCursor(waitCursor);
        MLoginWindow.bok.doClick();
      }
      if (e.getKeyCode() == 27) {
        MLoginWindow.bcancel.doClick();
      }
      





      if ((ch == 65535) || (Character.isISOControl(ch))) {
        return;
      }
      
      String str = m_editor.getText();
      int len = str.length();
      if (len == 0) {
        return;
      }
      
      String strLower = str.toLowerCase();
      int pos = m_editor.getCaretPosition();
      int k = 0; for (int n = m_comboBox.getItemCount(); k < n; k++) {
        String item = m_comboBox.getItemAt(k).toString();
        if (item.toLowerCase().startsWith(strLower)) {
          if (!m_comboBox.isPopupVisible()) {
            m_comboBox.showPopup();
          }
          m_comboBox.setSelectedIndex(k);
          

          m_editor.setText(item);
          m_editor.setCaretPosition(item.length());
          m_editor.moveCaretPosition(pos);
          
          break;
        }
      }
    }
  }
  
  public void addDefaultNavigatorAgent() {
    new MemComboAgent(this);
  }
  

  public void addDefaultFocusListener()
  {
    if (focusListener == null) {
      focusListener = new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent e) {
          ((JTextField)e.getComponent()).selectAll();
        }
      };
    }
    getEditor().getEditorComponent().addFocusListener(focusListener);
  }
}
