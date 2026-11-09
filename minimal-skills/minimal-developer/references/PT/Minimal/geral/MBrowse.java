package PT.Minimal.geral;

import PT.Minimal.pnuts.;
import com.objectshare.grid.area.JKTableArea;
import com.objectshare.grid.data.JKRowAdapter;
import com.objectshare.grid.data.JKTableModel;
import com.objectshare.grid.edit.JKTableEdit;
import com.objectshare.grid.list.JKSelectionEvent;
import com.objectshare.grid.tool.JKCompositeTool;
import com.objectshare.grid.tool.JKExtendedRowSelectTool;
import com.objectshare.grid.tool.JKResizeColTool;
import com.objectshare.grid.tool.JKSortTool;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;

public class MBrowse extends JKTableEdit implements com.objectshare.grid.list.JKSelectionListener, java.io.Serializable
{
  public String minNome;
  public String minAnchor;
  public int minRelPosX = 0;
  public int minRelPosY = 0;
  public int minPosX = 0;
  public int minPosY = 0;
  public boolean minBreakLn = false;
  public int minWidth = 50;
  public String minCmd;
  public String minTipoCmd;
  protected Vector wrows;
  protected JKTableModel myModel;
  private String Ent;
  private MExp wexp;
  private String wtype;
  private MRow wtotal;
  private boolean hasTotal = false;
  private MQuery wquery;
  private MTextField wregsel;
  private MTextField wnreg;
  private boolean isBrowseEditable = false;
  
  private Hashtable hBColCar;
  
  public MBrowse() {}
  
  public void setEditable(boolean wboo)
  {
    if (wboo) {
      setCellAreaTool(new com.objectshare.grid.tool.JKHierarchicalTool(new MJKTabWrapExtendTool(new JKExtendedRowSelectTool())));

    }
    else
    {
      setCellAreaTool(new JKExtendedRowSelectTool());
    }
    
    isBrowseEditable = wboo;
  }
  
  public boolean isEditable() {
    return isBrowseEditable;
  }
  
  public void constrBrowse(String[] wzemc) {
    MBrowseColumn[] wcoldef = new MBrowseColumn[wzemc.length];
    for (int i = 0; i < wzemc.length; i++) {
      wcoldef[i] = new MBrowseColumn(wzemc[i], " ", i);
    }
    constrBrowse(wcoldef);
  }
  
  public void constrBrowse(String[][] wwzemc) {
    MBrowseColumn[] wcoldef = new MBrowseColumn[wwzemc.length];
    for (int i = 0; i < wwzemc.length; i++) {
      wcoldef[i] = new MBrowseColumn(wwzemc[i][0], wwzemc[i][1], i);
    }
    constrBrowse(wcoldef);
  }
  
  public void constrBrowse(Vector wv) {
    MBrowseColumn[] wcoldef = new MBrowseColumn[wv.size()];
    for (int i = 0; i < wv.size(); i++) {
      wcoldef[i] = ((MBrowseColumn)wv.elementAt(i));
    }
    constrBrowse(wcoldef);
  }
  
  public void constrBrowse(MBrowseColumn[] wcoldef)
  {
    setDisplaySystem(new com.objectshare.base.draw.OSIAsyncDisplaySystem());
    
    myModel = new JKTableModel();
    myModel.addColumns(wcoldef);
    JKRowAdapter.addRowAdapterAssociation("PT.Minimal.geral.MRow", "com.objectshare.grid.data.JKRowAdapter");
    

    JKRowAdapter.addRowAdapterAssociation("PT.Minimal.geral.MRowParent", "PT.Minimal.geral.MRowParentJKRA");
    







    setHeaderFont(new Font(getHeaderFont().getName(), 0, getHeaderFont().getSize()));
    


    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setHeaderBackColor(new Color(163, 189, 252));
    } else {
      setHeaderBackColor(new Color(236, 233, 216));
    }
    
    setHeaderForeColor(Color.black);
    
    setCellAreaFont(MField.DEFAULTFONT);
    
    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setCellAreaBackColor(new Color(240, 236, 219));
    } else {
      setCellAreaBackColor(new Color(255, 255, 255));
    }
    
    setCellAreaForeColor(Color.black);
    

    if ((!MLogin.hasLoginProperty("COLORSBASE")) || (!MLogin.getLoginProperty("COLORSBASE").equals("GREY")))
    {
      setCellAreaSelectionDrawingPolicy(new com.objectshare.grid.draw.JKColorDrawingPolicy(getDrawingPolicy(), null, new Color(245, 195, 37)));
    }
    else
    {
      setCellAreaSelectionDrawingPolicy(new com.objectshare.grid.draw.JKColorDrawingPolicy(getDrawingPolicy(), null, new Color(200, 200, 255)));
    }
    








    JKCompositeTool compTool = new JKCompositeTool();
    com.objectshare.grid.tool.JKReorderColTool reorderTool = new com.objectshare.grid.tool.JKReorderColTool();
    JKResizeColTool resizeTool = new JKResizeColTool();
    JKSortTool sortTool = new JKSortTool();
    compTool.setTool(reorderTool);
    compTool.setTool(2, resizeTool);
    compTool.setTool(1, sortTool);
    setColumnHeaderTool(compTool);
    



    wrows = null;
    

    setRowHeaderTool(new JKExtendedRowSelectTool());
    setCellAreaTool(new JKExtendedRowSelectTool());
    addJKSelectionListener(this);
    setKeyboardTool(new JKExtendedRowSelectTool());
    
    setModel(myModel);
    setRowHeaders(false);
    
    for (int i = 0; i < wcoldef.length; i++) {
      if (wcoldef[i].isTotalized()) {
        hasTotal = true;
        setNumberOfColumnFooters(1);
        myModel.set(1, 0, "Total");
        JKTableArea wfooter = JKTableArea.newForRow(1);
        setRowBackColor(1, Color.lightGray);
        setRowForeColor(1, Color.black);
        break;
      }
    }
  }
  
  public void setRows(Vector wwrows)
    throws MError
  {
    removeAll();
    wrows = wwrows;
    MExp[] wvexp = null;
    if (getEnt() != null) {
      wvexp = MForm.getExp(getEnt(), "*", "HR");
    }
    if (wvexp == null)
    {


      setRowHeaders(false);
      
      if (getEnt() != null) {
         mpnut = .getMPnut(getEnt());
        mpnut.set("vrows", wrows);
        if ((getEnt().substring(0, 4).equals(getName().substring(0, 4))) && (!getEnt().substring(4, 5).equals(getName().substring(4, 5))))
        {
          setName(getEnt() + "CBR");
        }
        
        mpnut.evalBrowsePnut(this, "BB");
      }
      
      myModel.addRows(wrows);
      

      if (wrows.size() > 0) {
        setSelectedRowIndex(1);
        if ((wrows.size() > 1) && 
          (!getMRowAt(1).isValidRow())) {
          setSelectedRowIndex(2);
        }
      }
      
      if ((wrows.size() > 0) && (hasTotal))
      {


        wtotal = new MRow((MRow)wrows.elementAt(0));
        wtotal.setCst("st");
        calcTotal();
        

        myModel.setRow(myModel.getNumberOfRows() + 1, wtotal);
        

        myModel.set(myModel.getNumberOfRows() + 1, 0, "Total");
        

        JKTableArea wfooter = JKTableArea.newForRow(myModel.getNumberOfRows() + 1);
        


        setRowBackColor(getNumberOfRows() + 1, new Color(224, 233, 254));
        setRowForeColor(getNumberOfRows() + 1, Color.black);
        setRowEditingPolicy(getNumberOfRows() + 1, com.objectshare.grid.edit.JKEditingPolicy.READ_ONLY);
      }
      








      myModel.set(myModel.getNumberOfRows() + 1, 0, "Total");
      if (wnreg != null) {
        wnreg.setText(wnreg.getFormatedText(String.valueOf(getNumberMRows())));
      }
      damageRowHeaders();
      setRowHeaders(true);
    }
    


    if (wvexp != null) {
      boolean bfound = false;
      for (int k = 0; k < wvexp.length; k++) {
        MExp wexp = wvexp[k];
        if (.getMPnut(getEnt()).evalCondition(wexp.getCondition()))
        {
          String[] wargs = wexp.getArgs(wexp.getCondition());
          Vector wtop = new Vector();
          for (int i = 0; i < wrows.size(); i++) {
            if (isParent(wrows, i, wargs[1], wargs[2])) {
              wrows.setElementAt(new MRowParent((MRow)wrows.elementAt(i)), i);
            }
          }
          for (int i = 0; i < wrows.size(); i++) {
            if (!hasParent(wrows, i, wargs[1], wargs[2])) {
              wtop.addElement(wrows.elementAt(i));
            }
          }
          for (int i = 0; i < wrows.size(); i++) {
            if ((wrows.elementAt(i) instanceof MRowParent)) {
              ((MRowParent)wrows.elementAt(i)).setChildren(getKids(wrows, i, wargs[1], wargs[2]));
            }
          }
          myModel.addRows(wtop);
          bfound = true;
          break;
        }
      }
      if (!bfound) {
        throw new MError("Não foi encontrada expressão HR válida...");
      }
    }
  }
  
  public void addRow(MRow wr) throws MError
  {
     mpnut = .getMPnut(getEnt());
    MRow mprow = (MRow)mpnut.retrieve("row");
    try {
      mpnut.set("vrows", wrows);
      mpnut.setRow(wr);
      mpnut.evalBrowsePnut(this, "NR");
      if ((wrows != null) && (wrows.size() != 0)) {
        wrows.addElement(wr);
        myModel.addRow(wr);
        if (wnreg != null) {
          wnreg.setText(wnreg.getFormatedText(String.valueOf(getNumberMRows())));
        }
      }
      if ((wrows == null) || (wrows.size() == 0)) {
        Vector wv = new Vector();
        wv.addElement(wr);
        setRows(wv);
      }
    }
    finally {
      mpnut.unset("vrows");
      mpnut.setRow(mprow);
    }
  }
  

  public void removeAll()
  {
    if (wrows != null)
    {


      if (myModel.getNumberOfRows() != 0)
      {
        myModel.removeRows(1, myModel.getNumberOfRows());
      }
      
      wrows.removeAllElements();
      wrows = null;
      System.gc();
    }
    clearTotal();
  }
  





  public void refresh(MRow mr)
  {
    if ((wrows.size() > 0) && (hasTotal)) {
      calcTotal();
    }
  }
  
  public void refreshRowWithZK(BigDecimal wzk)
  {
    refreshRowWithZK(wzk.toString());
  }
  
  public void refreshRowWithZK(String wzk) {
    for (int i = 0; i < wrows.size(); i++) {
      MRow wrow = (MRow)wrows.elementAt(i);
      if (wrow.getField("zk").equals(wzk))
      {
        wrow.refreshRow();
        wrow.refresh();
      }
    }
  }
  
  public void refreshRow(MRow mr) {
    String wzk = (String)mr.getField("zk");
    for (int i = 0; i < wrows.size(); i++) {
      MRow wrow = (MRow)wrows.elementAt(i);
      if (wrow.getField("zk").equals(wzk))
      {
        wrow.copyFieldsFrom(mr);
        wrow.refresh();
      }
    }
  }
  





  public void jkSelecting(JKSelectionEvent e) {}
  




  public void jkSelected(JKSelectionEvent e)
  {
    if (wregsel != null) {
      wregsel.setText(wregsel.getFormatedText(String.valueOf(getSelectedRowIndexVector().size())));
    }
    try {
      int[] indice = e.getNewSelectedRowIndices().toArray();
      int j = indice.length - 1;
      int i = indice[j];
      



      try
      {
        if (e.getNewSelectedRowIndex() >= e.getOldSelectedRowIndex()) {
          for (; j > 0; j--) {
            i = Math.max(i, indice[(j - 1)]);
          }
        }
        i = e.getNewSelectedRowIndex();
      }
      catch (com.objectshare.grid.list.JKNoSelectionException ex) {
        i = e.getNewSelectedRowIndex();
      }
      

      if (!isCellVisible(i, 0)) {
        keepCellVisible(i, 0);
      }
      
    }
    catch (ArrayIndexOutOfBoundsException ex) {}catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public void scrollToMRow(int wnrow) {
    if (wrows != null) {
      if (wnrow < 1) {
        wnrow = 1;
      }
      if (wnrow > myModel.getNumberOfRows()) {
        wnrow = myModel.getNumberOfRows();
      }
      scrollToRow(wnrow);
    }
  }
  


  public void selectMRow(int wnrow)
  {
    setSelection(JKTableArea.newForRow(wnrow));
  }
  



  public void selectToMRow(int wnrow, int direction)
  {
    int start = -1;
    int[] list = getSelectedRowIndexArray();
    if ((list == null) || (list.length == 0)) {
      start = 0;
    } else {
      switch (direction) {
      case -1:  start = list[0]; break;
      case 0:  start = wnrow; break;
      case 1:  start = list[(list.length - 1)];
      }
    }
    setSelection(JKTableArea.newForRows(start, wnrow));
  }
  









  public void processKeyEvent(KeyEvent e)
  {
    if ((e.getKeyCode() == 36) || (e.getKeyCode() == 35) || (e.getKeyCode() == 33) || (e.getKeyCode() == 34))
    {



      if (e.getID() == 401) {
        e.consume();
        
        int wnrow;
        switch (e.getKeyCode()) {
        case 36:  wnrow = 1; break;
        case 35:  wnrow = myModel.getNumberOfRows(); break;
        case 33:  wnrow = getFirstVisibleRow() - getNumberOfVisibleRows(); break;
        case 34:  wnrow = getLastVisibleRow() + 1; break;
        default:  wnrow = 64537;
        }
        if ((!e.isShiftDown()) && 
          (wnrow != 64537)) {
          selectMRow(wnrow);
        }
        


        scrollToMRow(wnrow);
      }
    }
    super.processKeyEvent(e);
  }
  
















































  public void setType(String ws)
  {
    wtype = ws;
  }
  
  public String getType() { return wtype; }
  
  public void setEnt(String ws)
  {
    Ent = ws;
  }
  
  public String getEnt() { return Ent; }
  
  private boolean isParent(Vector wv, int i, String wparent, String wkey)
  {
    boolean wpapa = false;
    for (int j = i + 1; j < wv.size(); j++) {
      if (((MRow)wv.elementAt(j)).getField(wparent).equals(((MRow)wv.elementAt(i)).getField(wkey))) {
        wpapa = true;
        break;
      }
    }
    return wpapa;
  }
  
  private boolean hasParent(Vector wv, int i, String wparent, String wkey) { boolean wpapa = false;
    if (((MRow)wv.elementAt(i)).getField(wparent).equals(((MRow)wv.elementAt(i)).getField(wkey))) {
      return wpapa;
    }
    for (int j = i - 1; j >= 0; j--) {
      if (((MRow)wv.elementAt(i)).getField(wparent).equals(((MRow)wv.elementAt(j)).getField(wkey))) {
        wpapa = true;
        break;
      }
    }
    return wpapa;
  }
  
  private Vector getKids(Vector wv, int i, String wparent, String wkey) {
    Vector wkids = new Vector();
    for (int j = i + 1; j < wv.size(); j++) {
      if (((MRow)wv.elementAt(j)).getField(wparent).equals(((MRow)wv.elementAt(i)).getField(wkey))) {
        for (int z = j; z < wv.size(); z++) {
          if (((MRow)wv.elementAt(z)).getField(wparent).equals(((MRow)wv.elementAt(i)).getField(wkey))) {
            wkids.addElement(wv.elementAt(z));
          } else {
            return wkids;
          }
        }
        break;
      }
    }
    return wkids;
  }
  
  public int getNumberMRows() {
    int wcount = 0;
    if (wrows != null) {
      wcount = wrows.size();
    }
    return wcount;
  }
  
  public Object getColumn(String wcol) {
    return myModel.getColumnNamed(wcol);
  }
  
  public Object[] getColumns() {
    return myModel.getColumnArray();
  }
  
  public void insertColumns(int i, Object[] wcols) {
    myModel.insertColumnsOnce(i, wcols);
  }
  
  public void insertColumnBefore(String wcol, Object wcolx) {
    myModel.insertColumnBefore(myModel.getColumnNamed(wcol), wcolx);
  }
  
  public void insertColumnAfter(String wcol, Object wcolx) { myModel.insertColumnAfter(myModel.getColumnNamed(wcol), wcolx); }
  
  public void insertColumnsBefore(String wcol, Object[] wcols)
  {
    myModel.insertColumnsBefore(myModel.getColumnNamed(wcol), wcols);
  }
  
  public void insertColumnsAfter(String wcol, Object[] wcols) { myModel.insertColumnsAfter(myModel.getColumnNamed(wcol), wcols); }
  
  public void calcTotal()
  {
    if ((hasTotal) && (wtotal != null))
    {

      Object[] myCols = myModel.getColumnArray();
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          wtotal.setField(wcol.getName(), "");
          if (wcol.isTotalized())
          {
            BigDecimal total = new BigDecimal(0);
            for (int row = 1; row <= myModel.getNumberOfRows(); row++) {
              MRow wmrow = (MRow)myModel.getRow(row);
              if ((!wmrow.getCst().equals("st")) && (!wmrow.getCst().equals("nt")))
              {
                total = total.add(new BigDecimal(convert(myModel.get(row, i))));
              }
            }
            
            wtotal.setField(wcol.getName(), total.toString());
          }
        }
      }
       mpnut = .getMPnut(getEnt());
      mpnut.set("vrows", getMRows());
      mpnut.set("wtotal", wtotal);
      
      if (mpnut.existsPnut(getEnt(), "FM", getName(), "TO"))
      {
        mpnut.evalBrowsePnut(this, "TO");
        wtotal.refresh();
      }
      
      damageRows(myModel.getNumberOfRows() + 1, 1);
    }
  }
  
  public void calcTotal(Vector wv, MRow wrowtotal)
  {
    if ((hasTotal) && (wrowtotal != null))
    {

      Object[] myCols = myModel.getColumnArray();
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          wrowtotal.setField(wcol.getName(), "");
          if (wcol.isTotalized())
          {
            BigDecimal total = new BigDecimal(0);
            for (int row = 0; row < wv.size(); row++) {
              MRow wmrow = (MRow)wv.elementAt(row);
              if ((!wmrow.getCst().equals("st")) && (!wmrow.getCst().equals("nt")))
              {
                total = total.add(new BigDecimal(convert(wmrow.get(wcol.getName()))));
              }
            }
            
            wrowtotal.setField(wcol.getName(), total.toString());
          }
        }
      }
       mpnut = .getMPnut(getEnt());
      mpnut.set("vrows", wv);
      mpnut.set("wtotal", wrowtotal);
      
      mpnut.evalBrowsePnut(this, "TO");
    }
  }
  
  public void calcTotal(MBrowseColumn wcol)
  {
    if (hasTotal)
    {
      if (wcol.isTotalized()) {
        wtotal.setField(wcol.getName(), "");
        
        BigDecimal total = new BigDecimal(0);
        for (int row = 1; row <= myModel.getNumberOfRows(); row++) {
          MRow wmrow = (MRow)myModel.getRow(row);
          if (wmrow.getCst() != "st")
          {
            total = total.add(new BigDecimal(convert(wmrow.get(wcol.getName()))));
          }
        }
        
        wtotal.setField(wcol.getName(), total.toString());
        wtotal.refresh();
      }
    }
  }
  
  public void calcTotal(int wncol)
  {
    calcTotal((MBrowseColumn)myModel.getColumn(wncol));
  }
  
  public void clearTotal() {
    if ((hasTotal) && (wtotal != null))
    {
      Object[] myCols = myModel.getColumnArray();
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          wtotal.setField(wcol.getName(), "");
          if (wcol.isTotalized()) {
            wtotal.setField(wcol.getName(), "0");
          }
        }
      }
    }
  }
  
  public MRow getTotal()
  {
    return wtotal;
  }
  
  public void setColumnTitle(String wcolname, String wtitle) {
    if (myModel != null) {
      Object[] myCols = myModel.getColumnArray();
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          if (wcol.getName().equals(wcolname)) {
            wcol.setTitle(wtitle);
            break;
          }
        }
      }
    }
  }
  
  public void removeColumn(String wcolname)
  {
    removeColumns(wcolname, 1);
  }
  
  public void removeColumns(String wcolname, int wcount) {
    if (myModel != null) {
      Object[] myCols = myModel.getColumnArray();
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          if (wcol.getName().equals(wcolname)) {
            myModel.removeColumns(i, wcount);
            break;
          }
        }
      }
    }
  }
  
  public void setColumnPrintTitles(String wcolname, String wtitle1, String wtitle2) {
    if (myModel != null) {
      Object[] myCols = myModel.getColumnArray();
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          if (wcol.getName().equals(wcolname)) {
            wcol.setPrintTitle1(wtitle1);
            wcol.setPrintTitle2(wtitle2);
            break;
          }
        }
      }
    }
  }
  
  public void setColumnPrintTitle1(String wcolname, String wtitle) {
    if (myModel != null) {
      Object[] myCols = myModel.getColumnArray();
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          if (wcol.getName().equals(wcolname)) {
            wcol.setPrintTitle1(wtitle);
            break;
          }
        }
      }
    }
  }
  
  public void setColumnPrintTitle2(String wcolname, String wtitle) {
    if (myModel != null) {
      Object[] myCols = myModel.getColumnArray();
      for (int i = 0; i < myCols.length; i++) {
        if ((myCols[i] instanceof MBrowseColumn)) {
          MBrowseColumn wcol = (MBrowseColumn)myCols[i];
          if (wcol.getName().equals(wcolname)) {
            wcol.setPrintTitle2(wtitle);
            break;
          }
        }
      }
    }
  }
  
  public double convert(Object anObject) {
    if (anObject == null)
      return 0.0D;
    if ((anObject instanceof Number)) {
      double answer = ((Number)anObject).doubleValue();
      
      return answer;
    }
    try { String wn = MUtil.getNumber(anObject.toString());
      return new Double(wn).doubleValue();
    }
    catch (NumberFormatException ex) {}
    
    return 0.0D;
  }
  
  public void setMQuery(MQuery ws) {
    wquery = ws;
  }
  
  public MQuery getMQuery() { return wquery; }
  
  public Vector getMRows() {
    return wrows;
  }
  
  public MRow getMRowAt(int wn) {
    if (myModel.getNumberOfRows() < wn) {
      return null;
    }
    return (MRow)myModel.getRow(wn);
  }
  
  public void setRegSelField(MTextField wtf) {
    wregsel = wtf;
    wregsel.setText("0");
    wregsel.setText(wregsel.getFormatedText());
  }
  
  public void setNumRegField(MTextField wtf) {
    wnreg = wtf;
    wregsel.setText("0");
    wregsel.setText(wregsel.getFormatedText());
  }
  
  public boolean isTotalized() {
    return hasTotal;
  }
  
  public void resized()
  {
    closeEditor();
    super.resized();
  }
  
  public Hashtable getBrowseRecordFromRow(MRow wrow)
  {
    Hashtable wh = new Hashtable();
    Object[] myCols = myModel.getColumnArray();
    for (int i = 0; i < myCols.length; i++) {
      if ((myCols[i] instanceof MBrowseColumn)) {
        MBrowseColumn wcol = (MBrowseColumn)myCols[i];
        wh.put(wcol.getName(), wrow.getField(wcol.getName()));
      }
    }
    return wh;
  }
  
  public void setBColCar(Hashtable hBColCar) {
    this.hBColCar = hBColCar;
  }
  
  public int getBColCar(String ws) { return Integer.valueOf((String)hBColCar.get(ws)).intValue(); }
}
