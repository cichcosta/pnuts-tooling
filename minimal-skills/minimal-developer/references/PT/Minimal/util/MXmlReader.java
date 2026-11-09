package PT.Minimal.util;

import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MXmlReader
{
  DocumentBuilderFactory xdbf;
  javax.xml.parsers.DocumentBuilder xdb;
  Document xdoc;
  NodeList xnl;
  Node xn;
  
  public MXmlReader(String sFile)
  {
    try
    {
      xdbf = DocumentBuilderFactory.newInstance();
      xdb = xdbf.newDocumentBuilder();
      xdoc = xdb.parse(sFile);
    } catch (Exception e) {
      System.out.println("!*** MXmlReader Ficheiro: " + sFile + " não encontrado !!!");
      e.printStackTrace();
    }
  }
  
  public String getTagValue(String sTag) {
    xnl = xdoc.getElementsByTagName(sTag);
    if (xnl.getLength() > 0) {
      xn = xnl.item(0).getFirstChild();
      if (xn != null) {
        return xn.getNodeValue().trim();
      }
      return "";
    }
    
    return "";
  }
  
  public Hashtable getTagValues(String sTag)
  {
    xnl = xdoc.getElementsByTagName(sTag);
    Hashtable wh = new Hashtable();
    for (int i = 0; i < xnl.getLength(); i++) {
      xn = xnl.item(i).getFirstChild();
      if (xn != null) {
        wh.put(String.valueOf(i), xnl.item(i).getFirstChild().getNodeValue());
      } else {
        wh.put(String.valueOf(i), "");
      }
    }
    return wh;
  }
}
