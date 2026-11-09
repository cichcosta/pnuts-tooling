package PT.Minimal.geral;

import java.util.Vector;




public class MRowParent
  extends MRow
{
  private Vector children;
  
  public MRowParent(MRow wr)
  {
    super(wr);
    super.setRecord(wr.getRecord());
  }
  
  public void setChildren(Vector newChildren) {
    children = newChildren;
    setChanged();
    notifyObservers("children");
  }
  
  public MRow[] getChildren() {
    MRow[] wchilds = new MRow[children.size()];
    for (int i = 0; i < children.size(); i++) {
      wchilds[i] = ((MRow)children.elementAt(i));
    }
    return wchilds;
  }
  
  public boolean isDirectory() {
    return true;
  }
}
