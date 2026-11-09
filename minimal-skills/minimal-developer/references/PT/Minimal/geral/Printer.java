package PT.Minimal.geral;

import java.util.Vector;

public class Printer extends java.awt.Canvas
{
  java.awt.Frame f;
  java.awt.PrintJob pjob;
  java.awt.Graphics pg;
  Vector objects;
  java.awt.Point pt;
  java.awt.Font fnt;
  java.awt.Font tabFont;
  
  public Printer(java.awt.Frame frm) {
    f = frm;
    f.add(this);
    setVisible(false);
    pjob = null;
    
    pt = new java.awt.Point(0, 0);
    objects = new Vector();
    tabFont = new java.awt.Font("MonoSpaced", 0, 12);
    fnt = new java.awt.Font("SansSerif", 0, 12);
  }
  
  public void setFont(java.awt.Font f)
  {
    objects.addElement(new printFont(f));
  }
  
  public void print(String s) {
    objects.addElement(new printString(s));
  }
  
  public void println(String s) {
    print(s);
    objects.addElement(new newLine());
  }
  
  public void newPage() {
    if (pjob == null)
      pjob = getToolkit().getPrintJob(f, "Printer", null);
    pg = pjob.getGraphics();
    print(pg);
    pg.dispose();
    pt = new java.awt.Point(0, 0);
    objects = new Vector();
  }
  
  public void finalize() {
    if (objects.size() > 0)
      newPage();
    endJob();
  }
  
  public void endJob() {
    pjob.end();
  }
  
  public void tab(int tabstop) {
    objects.addElement(new printTab(tabFont, tabstop));
  }
  
  public java.awt.Dimension pageSize() {
    if (pjob == null) {
      return new java.awt.Dimension(620, 790);
    }
    pjob = getToolkit().getPrintJob(f, "Printer", null);
    return pjob.getPageDimension();
  }
  
  public void paint(java.awt.Graphics g)
  {
    pt = new java.awt.Point(0, 0);
    print(g);
  }
  
  public void print(java.awt.Graphics g)
  {
    f.setFont(fnt);
    for (int i = 0; i < objects.size(); i++) {
      printerObject p = (printerObject)objects.elementAt(i);
      
      p.draw(g, pt);
    }
  }
}
