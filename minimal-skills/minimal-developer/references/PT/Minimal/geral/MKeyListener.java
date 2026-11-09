package PT.Minimal.geral;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.DefaultFocusManager;


class MKeyListener
  extends KeyAdapter
{
  MForm minForm;
  MFrame minFrame;
  MDialog minDialog;
  String minCommand;
  KeyEvent wKeyEvent;
  MTextComponent wtf;
  DefaultFocusManager focusMG = new DefaultFocusManager();
  
  private char[] wacc = { ' ', ' ', ' ' };
  
  public MKeyListener(MFrame wframe) {
    minFrame = wframe;
  }
  
  public MKeyListener(MForm wform) {
    minForm = wform;
  }
  
  public MKeyListener(MDialog wdialog) {
    minDialog = wdialog;
  }
  
  public void keyTyped(KeyEvent e)
  {
    wKeyEvent = e;
    int wkey = e.getKeyChar();
    
    prepareKey();
    if ((e.getSource() instanceof MTextComponent)) {
      wtf = ((MTextComponent)e.getSource());
    }
  }
  
















































  public void keyPressed(KeyEvent e)
  {
    try
    {
      wKeyEvent = e;
      int wkeycode = e.getKeyCode();
      
      if (e.getKeyCode() >= 32) {
        for (int i = wacc.length - 1; i > 0; i--) {
          wacc[i] = wacc[(i - 1)];
        }
        wacc[0] = e.getKeyChar();
      }
      if (e.getModifiers() == 0)
        if (wkeycode == 40)
        {
          if (!(e.getSource() instanceof MBrowse)) {
            focusMG.focusNextComponent(e.getComponent());
          }
        }
        else if (wkeycode == 38)
        {
          if (!(e.getSource() instanceof MBrowse)) {
            focusMG.focusPreviousComponent(e.getComponent());
          }
        } else {
          MHandleEvent MHE;
          if (wkeycode == 114) {
            if (minForm.getENDButton() != null)
            {

              minForm.getENDButton().doClick();
            } else {
              minCommand = "CLOSEWINDOW";
              MHE = new MHandleEvent(minCommand, this);
            } } else { MHandleEvent MHE;
            if (wkeycode == 27) {
              minCommand = "CLOSEWINDOW";
              MHE = new MHandleEvent(minCommand, this); } else { MHandleEvent MHE;
              if (wkeycode == 115) {
                minCommand = "PROMPT_ROW";
                MHE = new MHandleEvent(minCommand, this);
              } else if (wkeycode == 116) {
                minCommand = "JUMPMRFC";
                MHE = new MHandleEvent(minCommand, this);
              }
            }
          } } } catch (MError err) { MHandleEvent MHE;
      err.show();
    }
  }
  
  private void prepareKey() {
    int wkey = wKeyEvent.getKeyChar();
    int wsetkey = 0;
    



    char c = wacc[1];
    if ((c == '~') || (c == '^') || (c == '´') || (c == '`') || (c == '¨'))
    {
      if (wkey == 32) {
        wsetkey = c;
      } else {
        switch (c) {
        case '~':  switch (wkey) {
          case 65:  wsetkey = 195; break;
          case 97:  wsetkey = 227; break;
          case 79:  wsetkey = 213; break;
          case 111:  wsetkey = 245; }
          break;
        case '^':  switch (wkey) {
          case 65:  wsetkey = 194; break;
          case 97:  wsetkey = 226; break;
          case 69:  wsetkey = 202; break;
          case 101:  wsetkey = 234; break;
          case 73:  wsetkey = 206; break;
          case 105:  wsetkey = 238; break;
          case 79:  wsetkey = 212; break;
          case 111:  wsetkey = 244; break;
          case 85:  wsetkey = 219; break;
          case 117:  wsetkey = 251; }
          break;
        case '´':  switch (wkey) {
          case 65:  wsetkey = 193; break;
          case 97:  wsetkey = 225; break;
          case 69:  wsetkey = 201; break;
          case 101:  wsetkey = 233; break;
          case 73:  wsetkey = 205; break;
          case 105:  wsetkey = 237; break;
          case 79:  wsetkey = 211; break;
          case 111:  wsetkey = 243; break;
          case 85:  wsetkey = 218; break;
          case 117:  wsetkey = 250; }
          break;
        case '`':  switch (wkey) {
          case 65:  wsetkey = 192; break;
          case 97:  wsetkey = 224; break;
          case 69:  wsetkey = 200; break;
          case 101:  wsetkey = 232; break;
          case 73:  wsetkey = 204; break;
          case 105:  wsetkey = 236; break;
          case 79:  wsetkey = 210; break;
          case 111:  wsetkey = 242; break;
          case 85:  wsetkey = 217; break;
          case 117:  wsetkey = 249; }
          break;
        case '¨':  switch (wkey) {
          case 65:  wsetkey = 196; break;
          case 97:  wsetkey = 228; break;
          case 69:  wsetkey = 203; break;
          case 101:  wsetkey = 235; break;
          case 73:  wsetkey = 207; break;
          case 105:  wsetkey = 239; break;
          case 79:  wsetkey = 214; break;
          case 111:  wsetkey = 246; break;
          case 85:  wsetkey = 220; break;
          case 117:  wsetkey = 252;
          }
          break;
        }
      }
    }
    if (wsetkey != 0) {
      wKeyEvent.setKeyChar((char)wsetkey);
    }
    for (int i = 0; i < wacc.length; i++) {
      wacc[i] = ' ';
    }
  }
  
  public void close() {
    minForm = null;
    minFrame = null;
    minDialog = null;
    minCommand = null;
    wKeyEvent = null;
    wtf = null;
  }
}
