package PT.Minimal.geral;

import java.awt.Color;
import java.awt.Font;





public abstract interface MField
{
  public static final String KEY = "1";
  public static final String ATRIB = "2";
  public static final String EXPANSION = "3";
  public static final String FORMONLY = "4";
  public static final String NORMAL = "N";
  public static final String PROTECTED = "P";
  public static final Font DEFAULTFONT = new Font("DialogInput", 0, 12);
  public static final Font DEFAULTBROWSEFONT = new Font("Tahoma", 0, 12);
  public static final Font DEFAULTTABFONT = new Font("DialogInput", 1, 12);
  public static final Font EXPFONT = new Font("DialogInput", 0, 12);
  







  public static final Color DEFAULTCOLOR = Color.white;
  public static final Color KEYCOLOR = new Color(255, 255, 200);
  



  public static final Color EXPCOLOR = new Color(240, 235, 208);
  

  public static final Color DEFFONTCOLOR = Color.blue;
  public static final Color EXPFONTCOLOR = Color.darkGray;
  
  public static final Color PROTFONTCOLOR = Color.darkGray;
  public static final Color ATTENTIONFILL = new Color(223, 255, 217);
  public static final Color MUSTFILL = new Color(166, 255, 147);
  
  public abstract String getMinAnchor();
  
  public abstract int getMinRelPosX();
  
  public abstract int getMinRelPosY();
  
  public abstract int getMinPosX();
  
  public abstract int getMinPosY();
  
  public abstract boolean getMinBreakLn();
  
  public abstract void setName(String paramString);
  
  public abstract String getName();
  
  public abstract void setMustFill(boolean paramBoolean);
  
  public abstract boolean getMustFill();
  
  public abstract void setMsg(String paramString);
  
  public abstract String getMsg();
  
  public abstract void setDisplayType(String paramString);
  
  public abstract String getDisplayType();
  
  public abstract void setMask(String paramString);
  
  public abstract String getMask();
  
  public abstract String getFormatedText();
  
  public abstract String getUnFormatedText();
  
  public abstract void setInternalMask(String paramString);
  
  public abstract String getInternalMask();
  
  public abstract void setAtribType(String paramString);
  
  public abstract String getAtribType();
  
  public abstract void setDataType(String paramString);
  
  public abstract String getDataType();
  
  public abstract void setCommand(String paramString);
  
  public abstract String getCommand();
  
  public abstract void setCommandArgs(String paramString);
  
  public abstract String getCommandArgs();
  
  public abstract void setAtribFormat(String paramString);
  
  public abstract boolean isFocusTraversable();
  
  public abstract void setMode(boolean paramBoolean);
}
