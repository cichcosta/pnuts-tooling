package PT.Minimal.geral;

public abstract interface MTextComponent
  extends MField
{
  public abstract int getInternalWidth();
  
  public abstract String getSelectedText();
  
  public abstract String getText();
  
  public abstract int getCaretPosition();
  
  public abstract void setText(String paramString);
  
  public abstract void transferFocus();
  
  public abstract void setChanged(boolean paramBoolean);
  
  public abstract void setPromptCode(String paramString);
  
  public abstract String getPromptCode();
  
  public abstract void setPromptTable(String paramString);
  
  public abstract String getPromptTable();
  
  public abstract void setExpansionField(String paramString);
  
  public abstract String getExpansionField();
  
  public abstract void setPromptControl(String paramString);
  
  public abstract String getPromptControl();
  
  public abstract void setRule(String paramString);
  
  public abstract String getRule();
  
  public abstract void setPromptArgs(String paramString);
  
  public abstract String getPromptArgs();
}
