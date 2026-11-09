package PT.Minimal.geral;

import PT.Minimal.actions.MPrompt;
import PT.Minimal.pnuts.MPnuts;
import com.objectshare.grid.edit.JKEditAction;
import com.objectshare.grid.edit.JKEditEvent;
import com.objectshare.grid.edit.JKEditingPolicy;
import com.objectshare.grid.edit.JKTableEdit;
import com.objectshare.grid.list.JKFocusEvent;
import com.objectshare.grid.list.JKKeyEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.io.PrintStream;

public class MTextFieldEditingPolicy extends JKEditingPolicy
{
  MBTextField wtf;
  MBrowseColumn wbc;
  MBrowseEdit wbrc;
  private static MBKeyListener wkl = new MBKeyListener();
  
  private boolean handleClose = true;
  private boolean canClose = true;
  
  public MTextFieldEditingPolicy()
  {
    wtf = new MBTextField();
    showEditorOnMouseDown = true;
  }
  
  public MTextFieldEditingPolicy(MBrowseColumn wwbc) {
    wbc = wwbc;
    wtf = new MBTextField(this);
    wtf.addKeyListener(wkl);
    showEditorOnMouseDown = true;
  }
  
  public MBrowseColumn getMBrowseColumn() {
    return wbc;
  }
  
  public void moveForward() {
    wbrc.moveForward();
  }
  









  public Object getEditor(JKEditEvent evt)
  {
    System.out.println("!*** MTextFieldEditingPolicy Made call to getEditor");
    getEditActioneditor = wtf;
    evt.getEditAction().cacheEditor(wtf);
    evt.getEditAction().showEditor();
    wbrc = ((MBrowseEdit)evt.getTableEdit());
    return wtf;
  }
  
  public void initEditor(JKEditEvent evt)
  {
    System.out.println("!*** MTextFieldEditingPolicy initEditor");
    super.initEditor(evt);
    Component editor = (Component)evt.getEditor();
  }
  


  public void setEditorValue(JKEditEvent evt) {}
  

  public void openEditor(JKEditEvent evt)
  {
    System.out.println("!*** MTextFieldEditingPolicy openEditor");
    
    if ((evt.getEditor() instanceof Component)) {
      evt.addMouseListener();
      
      evt.addKeyListener();
      evt.addFocusListener();
    }
    if (evt.getOldValue() == null) {
      System.out.println("!*** MTextFieldEditingPolicy Value was null");
    }
    else {
      wtf.setText((String)evt.getOldValue());
      if ((wtf.getMask().startsWith("N")) && (wtf.getText().trim().length() != 0)) {
        wtf.setText(MUtil.getNumber(wtf.getText()));
      }
      wtf.setBackground(new Color(255, 255, 192));
      wtf.selectAll();
    }
  }
  


  public boolean validateEditorValue(JKEditEvent evt)
  {
    return true;
  }
  
  public Object getEditorValue(JKEditEvent evt)
  {
    System.out.println("!*** MTextFieldEditingPolicy getEditorValue");
    return wtf.getText();
  }
  
  public void closeEditor(JKEditEvent evt)
  {
    System.out.println("!*** MTextFieldEditingPolicy closeEditor");
    super.closeEditor(evt);
    MForm minForm = MUtil.getComponentMForm(wbrc);
    System.out.println("!*** MTextFieldEditingPolicy getRowNumber: " + evt.getRowNumber());
    
    if ((MPnuts.isMPnut(minForm.getEnt())) && (evt.getRowNumber() > 0)) {
      MPnuts mpnut = MPnuts.getMPnut(minForm.getEnt());
      
      mpnut.set("bedit", wbrc);
      mpnut.setRow(wbrc.getMRowAt(evt.getRowNumber()));
      mpnut.evalFieldPnut(minForm, wtf.getName(), "AF");
    }
    evt.getTableEdit().damageRows(evt.getRowNumber(), 1);
    wbrc.calcTotal(evt.getColumnNumber());
  }
  
  public void focusLost(JKEditEvent editEvent, JKFocusEvent focusEvent)
  {
    System.out.println("!*** MTextFieldEditingPolicy focusLost");
    super.focusLost(editEvent, focusEvent);
    if ((handleClose) && (canClose)) {
      editEvent.getTableEdit().closeEditor();
    } else {
      editEvent.getTableEdit().validateEditor();
      
      setHandleClose(true);
    }
  }
  
  public void setHandleClose(boolean wboo)
  {
    handleClose = wboo;
  }
  
  public void setCanClose(boolean wboo) {
    canClose = wboo;
  }
  
  public void keyPressed(JKEditEvent editEvent, JKKeyEvent keyEvent) {
    int wkeycode = keyEvent.getKeyCode();
    




    if (keyEvent.getModifiers() == 0) {
      if (wkeycode == 9)
      {
        keyEvent.consume();
        wbrc.moveForward();
      }
      if (wkeycode == 38) {
        keyEvent.consume();
        wbrc.moveUp();
      }
      if (wkeycode == 40)
      {
        keyEvent.consume();
        wbrc.moveDown();
      }
      if (wkeycode == 115) {
        keyEvent.consume();
        MCommand wdopromp = new MCommand(wbrc.getEnt(), "PROMPT_ROW");
        MPrompt wprompt = new MPrompt();
        wprompt.setMForm(MUtil.getComponentMForm(wbrc));
        wprompt.setMCommand(wdopromp);
        try {
          wprompt.execute();
        } catch (MError ex) {
          ex.show(MUtil.getComponentMForm(wbrc).getMFrame());
        } finally {
          setCanClose(true);
        }
      }
    }
    

    if (wkeycode == 9) {
      System.out.println("!*** MTextFieldEditingPolicy editEvent SHIFT VK_TAB: 16 9 " + wkeycode);
      keyEvent.consume();
      wbrc.moveBackward();
    }
    

    super.keyPressed(editEvent, keyEvent);
  }
  
  public Boolean handleEvent(Event sysEvent, JKEditAction editAction) {
    switch (id) {
    case 401: 
      if (key == 10) {
        editAction.validate();
        return Boolean.TRUE;
      }
      break;
    case 403:  switch (key) {
      case 1000: 
      case 1001: 
      case 1002: 
      case 1003: 
      case 1004: 
      case 1005: 
      case 1006: 
      case 1007: 
        if ((sysEvent.controlDown()) || (sysEvent.metaDown())) {
          return null;
        }
        return Boolean.FALSE; }
      return Boolean.TRUE; }
    return super.handleEvent(sysEvent, editAction);
  }
}
