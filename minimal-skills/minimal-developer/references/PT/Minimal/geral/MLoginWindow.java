package PT.Minimal.geral;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;

class MLoginWindow extends JFrame
{
  private static Properties myProp = new Properties();
  private static String wuser = "";
  private static String wuserNodo = "";
  private static String wuserMenu = "J000A";
  private static String wuserOrg = "";
  private int i = -1;
  private String sEmp;
  static JButton b1;
  static JButton bok;
  static JButton bcancel;
  JLabel lcliente;
  JLabel wtfl;
  JLabel wttl;
  JLabel wchl;
  MTextField wtf;
  JPasswordField wtt;
  MChoice wch;
  Image im;
  JBackgroundPanel jbp;
  Properties p; private String splashScreen = "splashScreen.gif";
  private String closeButton = "closeButton.gif";
  







  MLoginWindow(String slicenciado, Vector componentes)
  {
    setVisible(false);
    
    setResizable(false);
    
    setTitle("Minimal L&W");
    setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/minicon.gif")));
    







    MediaTracker tracker = new MediaTracker(this);
    tracker.addImage(im, 0);
    try {
      im = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/" + splashScreen));
      tracker.waitForAll();
    }
    catch (InterruptedException e) {}
    

    addWindowListener(new WindowAdapter() {
      public void windowOpened(WindowEvent evt) {
        wtf.requestFocus();
      }
      
      public void windowClosing(WindowEvent evt) { System.exit(0);
      }
      


    });
    ActionListener alButtons = new ActionListener()
    {





      public void actionPerformed(ActionEvent e)
      {




        if (e.getActionCommand().equals("closewindow")) {
          System.out.println("CLOSE WINDOW");
          MLogin.cancelSplashLogin();
        }
        if (e.getActionCommand().equals("login")) {
          doLogin();
































        }
        
































      }
      

































    };
    Container cp = getContentPane();
    

    jbp = new JBackgroundPanel(im);
    Insets insets = jbp.getInsets();
    jbp.setLayout(null);
    




    p = MLogin.getLoginProperties();
    Vector wv = new Vector();
    
    String sLang = "PT";
    String sLic = "  Licença";String sUtiliz = "Utilizador";String sData = "Base de Dados";String sCancel = "Cancelar";
    sEmp = "";
    if ((MLogin.hasLoginProperty("TERLOGIN")) && (MLogin.getLoginProperty("TERLOGIN").startsWith("S"))) {
      sEmp = "  Número";
    }
    if ((MLogin.hasLoginProperty("LANGLOC")) && (MLogin.hasLoginProperty("LANG"))) {
      sLang = MLogin.getLoginProperty("LANG");
      if (sLang.equals("US")) {
        sLic = "    License";sUtiliz = "        User";sData = "         Data Base";sCancel = "Cancel";
      }
      if ((MLogin.hasLoginProperty("TERLOGIN")) && (MLogin.getLoginProperty("TERLOGIN").startsWith("S"))) {
        sEmp = "  Number";
      }
    }
    
    lcliente = new JLabel(sLic + ": " + slicenciado);
    
    lcliente.setFont(new Font("Dialog", 0, 12));
    jbp.add(lcliente);
    



    if (!sEmp.trim().equals("")) {
      sUtiliz = sEmp;
    }
    
    wtfl = new JLabel(sUtiliz);
    wtfl.setFont(new Font("Dialog", 0, 12));
    wtf = new MTextField("user", 100, false);
    wtf.setText((String)p.get("user.name"));
    wtf.setInternalWidth(8);
    wtf.setBorder(BorderFactory.createLoweredBevelBorder());
    wttl = new JLabel("Password");
    wttl.setFont(new Font("Dialog", 0, 12));
    wtt = new JPasswordField(16);
    wtt.setName("passwd");
    wtt.setColumns(16);
    wtt.setEchoChar('*');
    wtt.setFont(MTextField.DEFAULTFONT);
    wtt.setBackground(MTextField.DEFAULTCOLOR);
    wtt.setForeground(MTextField.DEFFONTCOLOR);
    wtt.setBorder(BorderFactory.createLoweredBevelBorder());
    


    wtf.addKeyListener(new KeyListener() {
      public void keyTyped(KeyEvent e) {}
      
      public void keyPressed(KeyEvent e) {}
      
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
          Cursor waitCursor = new Cursor(3);
          setCursor(waitCursor);
          MLoginWindow.bok.doClick();
        }
        if (e.getKeyCode() == 27) {
          MLoginWindow.bcancel.doClick();
        }
        
      }
    });
    wtt.addKeyListener(new KeyListener() {
      public void keyTyped(KeyEvent e) {}
      
      public void keyPressed(KeyEvent e) {}
      
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
          Cursor waitCursor = new Cursor(3);
          setCursor(waitCursor);
          MLoginWindow.bok.doClick();
        }
        if (e.getKeyCode() == 27) {
          MLoginWindow.bcancel.doClick();

        }
        

      }
      

    });
    wchl = new JLabel(sData);
    wchl.setFont(new Font("Dialog", 0, 12));
    wch = new MChoice("dsn");
    wch.setMaximumRowCount(3);
    wch.getEditor().getEditorComponent().setSize(350, 40);
    Vector wvdsn = new Vector();
    for (Enumeration e = p.keys(); e.hasMoreElements();) {
      String wkey = (String)e.nextElement();
      if (wkey.startsWith("wdsn")) {
        wvdsn.addElement((String)p.get(wkey));
      }
    }
    
    String wversion = (String)System.getProperties().get("java.version");
    if (!wversion.startsWith("1.1")) {
      java.util.Collections.sort(wvdsn);
    }
    
    for (int i = 0; i < wvdsn.size(); i++) {
      wch.addItem((String)wvdsn.elementAt(i));
    }
    wch.setEditable(true);
    wch.addDefaultNavigatorAgent();
    wch.addDefaultFocusListener();
    


    bok = new JButton("OK");
    
    changeCursor(bok);
    

    bok.setBackground(new Color(158, 190, 231));
    
    Border empty = BorderFactory.createEmptyBorder();
    bok.setBorder(empty);
    bok.setFont(new Font("Dialog", 0, 12));
    jbp.add(bok);
    bok.setActionCommand("login");
    bok.addActionListener(alButtons);
    


    bcancel = new JButton(sCancel);
    
    changeCursor(bcancel);
    
    bcancel.setBackground(new Color(158, 190, 231));
    empty = BorderFactory.createEmptyBorder();
    bcancel.setBorder(empty);
    bcancel.setFont(new Font("Dialog", 0, 12));
    bcancel.setActionCommand("closewindow");
    bcancel.addActionListener(alButtons);
    jbp.add(bcancel);
    
    jbp.add(wtfl);
    jbp.add(wtf);
    jbp.add(wttl);
    jbp.add(wtt);
    jbp.add(wchl);
    jbp.add(wch);
    
    setComponentsLocation(insets);
    
    addComponents(cp, jbp);
    
    setSize(480, 329);
    setCentered();
    setVisible(true);
    
    toFront();
  }
  

  public void changeCursor(JButton b)
  {
    b.addMouseListener(new java.awt.event.MouseListener()
    {
      public void mouseClicked(MouseEvent evt) {
        Cursor normalCursor = new Cursor(0);
        setCursor(normalCursor);
      }
      
      public void mousePressed(MouseEvent evt)
      {
        Cursor normalCursor = new Cursor(3);
        setCursor(normalCursor);
      }
      

      public void mouseReleased(MouseEvent evt)
      {
        Cursor normalCursor = new Cursor(0);
        setCursor(normalCursor);
      }
      
      public void mouseEntered(MouseEvent evt)
      {
        Cursor normalCursor = new Cursor(12);
        setCursor(normalCursor);
      }
      
      public void mouseExited(MouseEvent evt)
      {
        Cursor normalCursor = new Cursor(0);
        setCursor(normalCursor);
      }
    });
  }
  






  public void setCentered()
  {
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle frameDim = getBounds();
    setLocation((width - width) / 2, (height - height) / 2);
  }
  










  public void setComponentsLocation(Insets insets)
  {
    lcliente.setBounds(left + 118, top + 130, 300, 20);
    wtfl.setBounds(left + 120, top + 163, 150, 20);
    wtf.setBounds(left + 178, top + 163, 150, 20);
    wttl.setBounds(left + 114, top + 190, 100, 20);
    wtt.setBounds(left + 178, top + 190, 100, 20);
    wchl.setBounds(left + 84, top + 217, 350, 20);
    wch.setBounds(left + 178, top + 217, 350, 20);
    
    bok.setBounds(left + 273, top + 265, 80, 20);
    bcancel.setBounds(left + 363, top + 265, 85, 20);
  }
  









  public void addComponents(Container cp, JBackgroundPanel panel)
  {
    cp.add(panel);
  }
  






  public void doLogin()
  {
    wtf.setText(wtf.getText().toLowerCase());
    myProp = new Properties();
    String sUser = wtf.getUnFormatedText();
    if (!sEmp.trim().equals("")) {
      sUser = "mndsfa00";
      String sIentT = "EM";
      String sIent = "FA";
      MResData.setQueryTerc("a.ientt='" + sIentT + "' and a.ient='" + sIent + "'");
    }
    myProp.put("user", sUser);
    myProp.put("passwd", new String(wtt.getPassword()));
    myProp.put("dsn", wch.getSelectedItem());
    MLogin.doSplashLogin(myProp);
  }
}
