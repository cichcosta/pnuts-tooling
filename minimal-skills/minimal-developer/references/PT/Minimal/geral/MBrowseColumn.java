package PT.Minimal.geral;

import com.objectshare.grid.draw.JKColumnDef;
import com.objectshare.grid.draw.JKDrawingContext;
import java.math.BigDecimal;
import java.text.DecimalFormat;


public class MBrowseColumn
  extends JKColumnDef
{
  private String minMask = " "; private String defMask = " ";
  private String dataType = "TEXTTYPE";
  private boolean hasTotal = false;
  

  private int minWidth = 0;
  private boolean mustFill = false;
  private int minInternalWidth = 10;
  
  private String wxrg = " ";
  private String wxajp = " ";
  private String wictx = " ";
  private String wniob = " ";
  private String wiexs = " ";
  private String wexsargs = " ";
  
  private String atribType = "4";
  private String displayType = "N";
  private String wcolumn;
  
  public MBrowseColumn(String wcol, String aTitle, int wid) { super(wcol, aTitle, wid);
    wcolumn = wcol;
    setDrawingContext(new JKDrawingContext());
  }
  
  public void setEditable(boolean wboo) {
    if (wboo)
    {
      drawingContext().setEditingPolicy(new MTextFieldEditingPolicy(this));
    } else {
      drawingContext().setEditingPolicy(null);
    }
  }
  
  public Object getField(int row, int column, Object target)
  {
    String ws = (String)((MRow)target).getField(wcolumn);
    int k = -1;
    if (ws == null) {
      throw new MError("MIN-910004E", new String[] { wcolumn });
    }
    
    if ((minMask.startsWith("N")) && (ws.trim().length() != 0))
    {
      String wcur = ((MRow)target).getCurrencyX();
      
      if (defMask.trim().equals("")) {
        defMask = minMask;
      }
      

      if (MLogin.hasLoginProperty("MDECIMALSZERO")) {
        if (wcur.equals("m")) {
          k = minMask.indexOf(".");
          if (k > -1) {
            String wdec = minMask.substring(k);
            if ((wdec.trim().length() < 5) && ((wdec.trim().length() <= 3) || (!wdec.substring(1, 4).equals(".000")))) {
              minMask = (minMask.substring(0, k) + ".##");
            }
          }
        }
      } else {
        minMask = defMask;
      }
      

      if (MLogin.hasLoginProperty("VDECIMALSZERO")) {
        if (wcur.equals("v")) {
          k = minMask.indexOf(".");
          if (k > -1) {
            String wdec = minMask.substring(k);
            if ((wdec.trim().length() < 5) && ((wdec.trim().length() <= 3) || (!wdec.substring(1, 4).equals(".000")))) {
              minMask = (minMask.substring(0, k) + ".##");
            }
          }
        }
      } else {
        minMask = defMask;
      }
      
      if (MLogin.hasLoginProperty("YDECIMALSZERO")) {
        if (wcur.equals("y")) {
          k = minMask.indexOf(".");
          if (k > -1) {
            String wdec = minMask.substring(k);
            if ((wdec.trim().length() < 5) && ((wdec.trim().length() <= 3) || (!wdec.substring(1, 4).equals(".000")))) {
              minMask = (minMask.substring(0, k) + ".##");
            }
          }
        }
      } else {
        minMask = defMask;
      }
      


      String wformat = minMask.substring(1);
      
      DecimalFormat wdf = new DecimalFormat(wformat);
      wdf.setDecimalFormatSymbols(MUtil.DFS);
      try {
        BigDecimal wbd = new BigDecimal(ws);
        ws = wdf.format(wbd.doubleValue());
        while (ws.length() < getWidth()) ws = ' ' + ws;
      } catch (Exception ex) {
        ws = "*********";
      }
    }
    return ws;
  }
  
  public void setField(int row, int column, Object target, Object value) { ((MRow)target).setField(wcolumn, value); }
  
  private String printTitle1;
  private String printTitle2;
  public void setMask(String wmask)
  {
    minMask = wmask;
    if (minMask.startsWith("N")) {
      drawingContext().setHorizontalJustification(3);
    }
  }
  
  public String getMask() {
    return minMask;
  }
  
  public void setInternalWidth(int width) {
    minInternalWidth = width;
  }
  
  public int getInternalWidth() {
    return minInternalWidth;
  }
  
  public void setDataType(String ws) { dataType = ws; }
  
  public String getDataType()
  {
    return dataType;
  }
  
  public void setPrintTitle1(String ws) {
    printTitle1 = ws;
  }
  
  public String getPrintTitle1() {
    return printTitle1;
  }
  
  public void setPrintTitle2(String ws) {
    printTitle2 = ws;
  }
  
  public String getPrintTitle2() {
    return printTitle2;
  }
  
  public boolean isTotalized() {
    return hasTotal;
  }
  
  public void setTotalized(boolean wboo) {
    hasTotal = wboo;
  }
  
  public void setWidth(int width) {
    minWidth = width;
  }
  
  public int getWidth() {
    return minWidth;
  }
  
  public String getColName() { return wcolumn; }
  
  public void setMustFill(boolean wboo) {
    mustFill = wboo;
  }
  
  public boolean getMustFill() {
    return mustFill;
  }
  
  public void setPromptCode(String ws) { wiexs = ws; }
  
  public String getPromptCode()
  {
    return wiexs;
  }
  
  public void setPromptTable(String ws) {
    wictx = ws;
  }
  
  public String getPromptTable() {
    return wictx;
  }
  
  public void setExpansionField(String ws) {
    wniob = ws;
  }
  
  public String getExpansionField() {
    return wniob;
  }
  
  public void setPromptControl(String wpc) {
    wxajp = wpc;
  }
  
  public String getPromptControl() {
    return wxajp;
  }
  
  public void setRule(String wrg) {
    wxrg = wrg;
  }
  
  public String getRule() {
    return wxrg;
  }
  
  public void setPromptArgs(String wargs) {
    wexsargs = wargs;
  }
  
  public String getPromptArgs() {
    return wexsargs;
  }
  
  public void setAtribType(String wat) {
    atribType = wat;
  }
  
  public String getAtribType() {
    return atribType;
  }
  
  public void setDisplayType(String ws) { displayType = ws; }
  
  public String getDisplayType()
  {
    return displayType;
  }
}
