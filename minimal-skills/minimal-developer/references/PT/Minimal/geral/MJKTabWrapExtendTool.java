package PT.Minimal.geral;

import com.objectshare.grid.tool.JKTabWrapExtendTool;
import com.objectshare.grid.tool.JKTableTool;






public class MJKTabWrapExtendTool
  extends JKTabWrapExtendTool
{
  public MJKTabWrapExtendTool(JKTableTool newCascadedTool)
  {
    super(newCascadedTool);
  }
  
  public void addNewRow() {
    ((MBrowseEdit)getOwner()).addNewRowForEdit();
  }
}
