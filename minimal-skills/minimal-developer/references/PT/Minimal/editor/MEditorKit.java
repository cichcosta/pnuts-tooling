package PT.Minimal.editor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.PrintStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.JTextComponent.KeyBinding;
import javax.swing.text.TextAction;
import javax.swing.text.ViewFactory;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


public class MEditorKit
  extends DefaultEditorKit
{
  MEditor editor;
  
  public MEditorKit(MEditor editor)
  {
    this.editor = editor;
  }
  








  public MEditorContext getStylePreferences()
  {
    if (preferences == null) {
      preferences = new MEditorContext();
    }
    return preferences;
  }
  
  public void setStylePreferences(MEditorContext prefs) {
    preferences = prefs;
  }
  





  public String getContentType()
  {
    return "text/java";
  }
  




  public Object clone()
  {
    MEditorKit kit = new MEditorKit(editor);
    preferences = preferences;
    return kit;
  }
  








  public Document createDefaultDocument()
  {
    Document doc = new MEditorDocument(editor);
    doc.addUndoableEditListener(new UndoHandler());
    return doc;
  }
  



  public void resetUndo()
  {
    undo.discardAllEdits();
  }
  

  protected UndoManager undo = new UndoManager();
  MEditorContext preferences;
  public static final String selectionPageUpAction = "selection-page-up";
  public static final String selectionPageDownAction = "selection-page-down";
  public static final String undoAction = "undo";
  public static final String redoAction = "redo";
  
  class UndoHandler implements UndoableEditListener { UndoHandler() {}
    
    public void undoableEditHappened(UndoableEditEvent e) { undo.addEdit(e.getEdit());
      myUndoAction.update();
      myRedoAction.update();
    }
  }
  









  public final ViewFactory getViewFactory()
  {
    return getStylePreferences();
  }
  









  public Action[] getActions()
  {
    System.out.println("!*** MEditorKit getActions");
    return TextAction.augmentList(super.getActions(), defaultActions);
  }
  



































  private UndoAction myUndoAction = new UndoAction();
  private RedoAction myRedoAction = new RedoAction();
  
  private Action[] defaultActions = { new SelectionPageUpAction(), new SelectionPageDownAction(), myUndoAction, myRedoAction };
  



  class UndoAction
    extends AbstractAction
  {
    public UndoAction()
    {
      super();
      setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent e) {
      try {
        undo.undo();
      } catch (CannotUndoException ex) {
        System.out.println("!*** MEditorKit Unable to undo: " + ex);
        ex.printStackTrace();
      }
      update();
      myRedoAction.update();
    }
    
    protected void update() {
      if (undo.canUndo()) {
        setEnabled(true);
        putValue("Name", undo.getUndoPresentationName());
      } else {
        setEnabled(false);
        putValue("Name", "undo");
      }
    }
  }
  
  class RedoAction extends AbstractAction {
    public RedoAction() {
      super();
      setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent e) {
      try {
        undo.redo();
      } catch (CannotRedoException ex) {
        System.out.println("!*** MEditorKit Unable to redo: " + ex);
        ex.printStackTrace();
      }
      update();
      myUndoAction.update();
    }
    
    protected void update() {
      if (undo.canRedo()) {
        setEnabled(true);
        putValue("Name", undo.getRedoPresentationName());
      } else {
        setEnabled(false);
        putValue("Name", "redo");
      }
    }
  }
  




  static class SelectionPageUpAction
    extends TextAction
  {
    SelectionPageUpAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent e)
    {
      JTextComponent target = getTextComponent(e);
      if (target != null)
      {

        Rectangle visible = new Rectangle();
        
        target.computeVisibleRect(visible);
        int scrollOffset = y;
        y -= height;
        if (y < 0)
          y = 0;
        scrollOffset -= y;
        target.scrollRectToVisible(visible);
        
        int selectedIndex = target.getCaretPosition();
        try {
          if (selectedIndex != -1) {
            Rectangle r = target.modelToView(selectedIndex);
            y -= scrollOffset;
            selectedIndex = target.viewToModel(new Point(x, y));
            if (selectedIndex < 0) {
              selectedIndex = 0;
            }
            target.moveCaretPosition(selectedIndex);
          }
        } catch (BadLocationException bl) {
          target.getToolkit().beep();
        }
      }
    }
  }
  




  static class SelectionPageDownAction
    extends TextAction
  {
    SelectionPageDownAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent e)
    {
      JTextComponent target = getTextComponent(e);
      if (target != null)
      {

        Rectangle visible = new Rectangle();
        
        target.computeVisibleRect(visible);
        int scrollOffset = y;
        y += height;
        if (y + height > target.getHeight())
          y = (target.getHeight() - height);
        scrollOffset = y - scrollOffset;
        target.scrollRectToVisible(visible);
        
        int selectedIndex = target.getCaretPosition();
        try {
          if (selectedIndex != -1) {
            Rectangle r = target.modelToView(selectedIndex);
            y += scrollOffset;
            selectedIndex = target.viewToModel(new Point(x, y));
            Document doc = target.getDocument();
            if (selectedIndex > doc.getLength() - 1)
              selectedIndex = doc.getLength() - 1;
            target.moveCaretPosition(selectedIndex);
          }
        } catch (BadLocationException bl) {
          target.getToolkit().beep();
        }
      }
    }
  }
  
  public static JTextComponent.KeyBinding[] defaultBindings = { new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(35, 2), "caret-end"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(36, 2), "caret-begin"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(35, 3), "selection-end"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(36, 3), "selection-begin"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(65, 2), "select-all") };
  










  JTextComponent component;
  










  public void setComponent(JTextComponent c)
  {
    System.out.println("!*** MEditorKit setComponent");
    component = c;
    JTextComponent.loadKeymap(component.getKeymap(), defaultBindings, component.getActions());
    component.registerKeyboardAction(new SelectionPageUpAction(), "selection-page-up", KeyStroke.getKeyStroke(33, 1), 0);
    



    component.registerKeyboardAction(new SelectionPageDownAction(), "selection-page-down", KeyStroke.getKeyStroke(34, 1), 0);
    



    component.registerKeyboardAction(myRedoAction, "redo", KeyStroke.getKeyStroke(89, 2), 0);
    



    component.registerKeyboardAction(myUndoAction, "undo", KeyStroke.getKeyStroke(90, 2), 0);
  }
}
