package PT.Minimal.geral;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.PrintStream;
import java.util.Hashtable;








public class MImages
  extends Component
{
  private static final String imgGeral = "MinTool1.gif";
  private static final String imgBusy = "call.jpg";
  public static final String imgIcon = "minicon.gif";
  private static Hashtable LImg = null;
  private static final String[] IGerais = { "ALLBLANK", "BLANK", "Undo", "Cut", "Copy", "Paste", "Imprimir", "Prompt", "Criterios", "Help", "Open", "New", "Save", "Texto Associado", "EUR", "PTE", "CMD", "Alterar", "Inserir", "Eliminar", "Consultar", "Alterar2", "Inserir2", "Limpar", "Inserir3", "Redo", "Stop", "Refrescar", "Excel-Old", "Criterios Adicionais", "Word", "Importar File", "Exportar File", "Excel", "Imagem", "Outputs / Outros Comandos" };
  















  public MImages() {}
  















  private void readyImages()
  {
    try
    {
      Image imageGeral = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/MinTool1.gif"));
      Image imageBusy = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/call.jpg"));
      Image imageIcon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/minicon.gif"));
      

      MediaTracker tracker = new MediaTracker(new Canvas());
      tracker.addImage(imageGeral, 0);
      tracker.addImage(imageBusy, 0);
      tracker.addImage(imageIcon, 0);
      try {
        tracker.waitForAll();
      }
      catch (InterruptedException e) {}
      tracker.removeImage(imageGeral);
      tracker.removeImage(imageBusy);
      tracker.removeImage(imageIcon);
      

      LImg.put("MinTool1.gif", imageGeral);
      LImg.put("call.jpg", imageBusy);
      LImg.put("minicon.gif", imageIcon);
      
      Image img = (Image)LImg.get("MinTool1.gif");
      



      for (int j = 0; j < IGerais.length; j++) {
        Image img_off = createImage(new FilteredImageSource(img.getSource(), new CropImageFilter(j * 23, 0, 20, 16)));
        


        LImg.put(IGerais[j].toLowerCase() + "_off", img_off);
        
        Image img_on = createImage(new FilteredImageSource(img.getSource(), new CropImageFilter(j * 23, 26, 20, 16)));
        
        LImg.put(IGerais[j].toLowerCase() + "_on", img_on);
        
        Image img_mi = createImage(new FilteredImageSource(img.getSource(), new CropImageFilter(j * 23 + 2, 26, 20, 16)));
        
        LImg.put(IGerais[j].toLowerCase() + "_mi", img_mi);
      }
    } catch (Exception ex) {
      System.out.println("Imagens nao encontradas");
    }
  }
  









  public Image getImg(String ws)
  {
    if (LImg == null) {
      LImg = new Hashtable();
      readyImages();
    }
    if (!LImg.containsKey(ws.toLowerCase())) {
      return null;
    }
    return (Image)LImg.get(ws.toLowerCase());
  }
  








  public static Image getPreparedImage(String ws)
  {
    if (LImg == null) {
      return null;
    }
    if (!LImg.containsKey(ws.toLowerCase())) {
      return null;
    }
    return (Image)LImg.get(ws.toLowerCase());
  }
}
