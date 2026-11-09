package PT.Minimal.util;

import java.awt.event.ActionEvent;
import java.io.PrintStream;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;






















































public class AltGrPatch
{
  public AltGrPatch() {}
  
  static class DefaultKeyTypedAction
    extends TextAction
  {
    public DefaultKeyTypedAction()
    {
      super();
    }
    




    public void actionPerformed(ActionEvent e)
    {
      JTextComponent target = getTextComponent(e);
      if ((target != null) && (e != null)) {
        String content = e.getActionCommand();
        int mod = e.getModifiers();
        if ((content != null) && (content.length() > 0) && (((mod & 0x8) == 0) || ((mod & 0x2) != 0)))
        {


          char c = content.charAt(0);
          if ((c >= ' ') && (c != '')) {
            target.replaceSelection(content);
          }
        }
      }
    }
  }
  

  static
  {
    String vmname = System.getProperty("java.vm.name");
    String vminfo = System.getProperty("java.vm.info");
    String version = System.getProperty("java.version");
    String vmversion = System.getProperty("java.vm.version");
    System.out.println("!*** AltGrPatch Version: " + version);
    System.out.println("!*** AltGrPatch VM: " + vmname + " " + vminfo + "  VM version: " + vmversion);
    if (version.compareTo("1.2") <= 0) {
      System.out.println("!*** AltGrPatch");
      Keymap map = JTextComponent.getKeymap("default");
      if (map != null) {
        map.setDefaultAction(new DefaultKeyTypedAction());
        System.err.println("!*** AltGrPatch applied");
      } else {
        System.err.println("!*** AltGrPatch Default keymap doesn't exist, patch not applied");
      }
    }
  }
}
