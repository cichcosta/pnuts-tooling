package PT.Minimal.actions;

import PT.Minimal.geral.MButton;
import PT.Minimal.geral.MCommand;
import PT.Minimal.geral.MCursor;
import PT.Minimal.geral.MError;
import PT.Minimal.geral.MForm;
import PT.Minimal.geral.MFrame;
import PT.Minimal.geral.MHashtable;
import PT.Minimal.geral.MImage;
import PT.Minimal.geral.MRow;
import PT.Minimal.geral.MUtil;
import PT.Minimal.util.MBmpFile;
import PT.Minimal.util.MFileFilter;
import PT.Minimal.util.MImageSelection;
import SK.gnome.twain.Twain;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JFileChooser;

public class MShowImage implements MExternalCommand
{
  protected static String strLastDirectory = "";
  
  protected MCommand minCommand;
  
  protected MForm mform;
  protected Vector wrows;
  protected PT.Minimal.pnuts. mpnut;
  protected Twain twainDrv = new Twain();
  


  protected MImage mimg;
  


  protected Vector vctImagens;
  


  protected int imagePosition;
  



  public MShowImage() {}
  



  public void setMCommand(MCommand wcmd)
  {
    minCommand = wcmd;
  }
  








  public void setMForm(MForm wform)
  {
    mform = wform;
  }
  








  public void setRows(Vector wv)
  {
    wrows = wv;
  }
  








  public void execute()
  {
    mpnut = PT.Minimal.pnuts..getMPnut(mform.getEnt());
    if (mform.getName().endsWith("MIMG")) {
      getMImage();
    }
    
    boolean isValid = true;
    if ((minCommand.getCallerArgs() != null) && (minCommand.getCallerArgs().trim().length() != 0) && 
      (minCommand.getCallerArgs().startsWith("CANCEL"))) {
      cancelOperation();
      isValid = false;
    }
    

    if ((isValid) && (!minCommand.getName().equals("IMAGE_ROW"))) {
      isValid = false;
    }
    
    if (isValid) {
      String args = minCommand.getCallerArgs();
      if ((args == null) || (args.trim().length() == 0)) {
        args = minCommand.getArgs();
      }
      
      String[] argList = MUtil.getArgs(args);
      String cmd = (argList != null) && (argList.length > 0) ? argList[0] : null;
      try
      {
        if ("SHOW_IMAGE".equals(cmd)) {
          int fase = mform.getFase();
          if (fase == 0) {
            if ((wrows == null) && (mform.getType().equals("SINGLEROW"))) {
              wrows = new Vector(1);
              wrows.addElement(mform.getRow());
            }
            if ((wrows == null) || (wrows.size() == 0)) {
              throw new MError("MIN-000103W");
            }
            show_image();
          } else if (fase == 1) {
            do_image_row();
          } else {
            throw new MError("Fase invalida");
          }
        } else if ("SCAN_IMAGE".equals(cmd)) {
          scanImage(false);
        } else if ("SCAN_WINDOW".equals(cmd)) {
          scanImage(true);
        } else if ("READ_CLIP".equals(cmd)) {
          readClipboard();
        } else if ("READ_FILE".equals(cmd)) {
          readFile();
        } else if ("WRITE_FILE".equals(cmd)) {
          writeFile();
        } else if ("WRITE_CLIP".equals(cmd)) {
          writeClipboard();
        } else if ("ERASE_IMAGE".equals(cmd)) {
          eraseImage();
        } else if ("ERASE_ALL".equals(cmd)) {
          eraseAllImages();
        } else if ("PREV_IMAGE".equals(cmd)) {
          previousImage();
        } else if ("NEXT_IMAGE".equals(cmd)) {
          nextImage();
        } else {
          System.out.println("MSHowImage: Unknown command argument " + cmd);
        }
      } catch (MError err) {
        err.show(mform.getMFrame());
      }
    }
  }
  







  public void show_image()
  {
    MForm mformParent = mform;
    
    String formName = mformParent.getEnt() + "MIMG";
    mform = MForm.getMForm(formName);
    try {
      if (mform == null) {
        mform = new MForm(formName);
        mform.setSize(new Dimension(500, 300));
        mform.setEnt(mformParent.getEnt());
        mform.setEntName(mformParent.getEntName());
        
        mform.setCenterPanel(MForm.getPanelCopy("ZIMG0ME", mformParent.getEnt() + "PIMG"));
        
        mform.setBotPanel(mform.constrBotPanel(3));
        
        mform.showStatusBar(true);
        MButton wok = (MButton)MUtil.getComponent(mform.getBotPanel(), "wok");
        mform.setOKButton(wok);
        MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
        mform.setENDButton(wend);
        
        javax.swing.JMenuBar wmenubar = PT.Minimal.geral.MMenu.getMinMenuBar("ZIMG0ME");
        if (wmenubar != null) {
          mform.addMenuBar(wmenubar);
        }
        mform.prepareForm();
        
        MFrame wframe = new MFrame();
        
        wframe.setTitle(mform.getEntName());
        
        wframe.setForm(mform);
        wframe.pack();
        wframe.setSize(mform.getPreferredSize());
        wframe.center(mform.getPreferredSize().width, mform.getPreferredSize().height, false);
        mpnut.put(mform.getName(), mform);
      }
      


      MFrame.setModalParent(mformParent.getMFrame(), mform.getMFrame());
      
      String title = mformParent.getEntName() + " - Imagens";
      ((MFrame)mform.getFormParent()).setTitle(title);
      mform.setAtribFormat(mform.getCenterPanel(), minCommand.getName().trim());
      mform.setAction(minCommand.getName());
      mform.setFase(1);
      mform.setActionObject(wrows);
      mform.setActionObjectIndex(0);
      
      MButton wend = (MButton)MUtil.getComponent(mform.getBotPanel(), "wend");
      wend.setCommand(minCommand.getName());
      wend.setCommandArgs("CANCEL");
      wend.setText(MUtil.lang("\"Seguinte\""));
      wend.setEnabled(true);
      
      MButton wok = (MButton)MUtil.getComponent(mform.getBotPanel(), "wok");
      wok.setEnabled(true);
      
      if (mformParent.getBrowse() != null) {
        mform.setBrowse(mformParent.getBrowse());
      }
      
      getMImage();
      prepareNext();
    } catch (MError ex) {
      mform.hideForm();
      ex.show(mformParent.getMFrame());
    }
  }
  






  public void do_image_row()
  {
    updateCurrentImage();
    
    if (!mimg.isChanged()) {
      System.out.println("OK: no changes");
    } else {
      System.out.println("OK: updating images");
      MCursor wupdate = PT.Minimal.geral.MConnection.getMCursor();
      try {
        wupdate.beginBatch();
        
        MRow wrow = mform.getRow();
        wrow.setBinaryObject("IMAGE", "JPEG", vctImagens);
        
        wupdate.commitBatch();
      } finally {
        if (wupdate.inBeginBatch()) {
          wupdate.rollbackBatch();
        }
        wupdate.close();
      }
    }
    vctImagens.removeAllElements();
    
    mform.setActionObjectIndex(mform.getActionObjectIndex() + 1);
    prepareNext();
  }
  




  public void prepareNext()
  {
    mimg.setImage(null);
    setChanged(false);
    
    Vector wrows = (Vector)mform.getActionObject();
    int index = mform.getActionObjectIndex();
    
    MRow wrow = null;
    if (wrows != null) {
      while (index < wrows.size()) {
        wrow = (MRow)wrows.elementAt(index);
        if (wrow.isValidRow()) {
          break;
        }
        mform.setActionObjectIndex(++index);
        index = mform.getActionObjectIndex();
      }
    }
    
    if ((wrows == null) || (index >= wrows.size())) {
      mform.hideForm();
      return;
    }
    
    if (index >= wrows.size() - 1) {
      mform.getENDButton().setText(MUtil.lang("\"Terminar\""));
    }
    
    mform.setRow(wrow);
    String squal = mform.getFormValue("qqual");
    if (squal.trim().length() == 0) {
      squal = "50";
    }
    mform.clearFormValues();
    

    vctImagens = wrow.getBinaryObject("IMAGE");
    mimg.setImageVector(vctImagens);
    
    int size = vctImagens.size();
    mform.setUnFormattedFormValue("qmaximg", "" + size);
    mform.setUnFormattedFormValue("qnextimage", "" + (size + 1));
    mform.setUnFormattedFormValue("qqual", squal);
    calcTotalKb();
    

    boolean readOnly = ((String)wrow.getField("zcst")).toLowerCase().startsWith("r");
    boolean empty = vctImagens.isEmpty();
    
    String format = readOnly ? "INQUIRY_ROW" : "INSERT_ROW";
    mform.setAtribFormat(mform.getCenterPanel(), format);
    
    boolean enabled = !readOnly;
    ((MButton)mform.getFormComponent("bscan")).setEnabled(enabled);
    ((MButton)mform.getFormComponent("bscanwindow")).setEnabled(enabled);
    ((MButton)mform.getFormComponent("breadclip")).setEnabled(enabled);
    ((MButton)mform.getFormComponent("breadfile")).setEnabled(enabled);
    ((MButton)MUtil.getComponent(mform.getBotPanel(), "wok")).setEnabled(enabled);
    
    enabled = (!empty) && (!readOnly);
    ((MButton)mform.getFormComponent("beraseimage")).setEnabled(enabled);
    ((MButton)mform.getFormComponent("beraseall")).setEnabled(enabled);
    
    enabled = !empty;
    ((MButton)mform.getFormComponent("bwriteclip")).setEnabled(enabled);
    ((MButton)mform.getFormComponent("bwritefile")).setEnabled(enabled);
    
    setCurrentImage(0);
    
    mform.showComponentTab("gimagem");
    mform.showForm();
  }
  



  public void cancelOperation()
  {
    System.out.println("Cancel - imagens alteradas: " + mimg.isChanged());
    if (mimg.isChanged()) {
      MError msg = new MError("MIN-MIMG03Q");
      if (msg.show() != 0) {
        return;
      }
    }
    
    if (wrows == null) {
      mform.hideForm();
      return;
    }
    
    mform.setActionObjectIndex(mform.getActionObjectIndex() + 1);
    prepareNext();
  }
  








  public BufferedImage toBufferedImage(Image image)
  {
    image = new javax.swing.ImageIcon(image).getImage();
    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
    
    Graphics g = bufferedImage.createGraphics();
    
    g.setColor(java.awt.Color.white);
    g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
    g.drawImage(image, 0, 0, null);
    g.dispose();
    
    return bufferedImage;
  }
  






  void scanImage(boolean showWindow)
  {
    updateCurrentImage();
    
    System.out.println("TWAIN: " + twainDrv);
    twainDrv.setVisible(showWindow);
    
    Image image = Toolkit.getDefaultToolkit().createImage(twainDrv);
    MediaTracker tracker = new MediaTracker(mimg);
    tracker.addImage(image, 0);
    try {
      tracker.waitForAll();
    }
    catch (InterruptedException e) {}
    tracker.removeImage(image);
    
    SK.gnome.twain.TwainException twainException = Twain.getLastException();
    
    if (twainException == null) {
      adicionarNovaImagem(image, "Imagem lida por Scanner", "Imagem lida por Scanner");
    } else {
      throw new MError("MIN-MIMG10E");
    }
  }
  




  void readFile()
  {
    updateCurrentImage();
    
    JFileChooser chooser = new JFileChooser(strLastDirectory);
    chooser.setDialogTitle("Ficheiro a ler");
    
    MFileFilter filterBMP = new MFileFilter(new String[] { "bmp" }, "Imagem BMP");
    MFileFilter filterJPG = new MFileFilter(new String[] { "jpg", "jpeg" }, "Imagem JPEG");
    MFileFilter filterGIF = new MFileFilter(new String[] { "gif" }, "Imagem GIF");
    MFileFilter filterALL = new MFileFilter(new String[] { "bmp", "gif", "jpg", "jpeg" }, "Imagem BMP, GIF e JPEG");
    chooser.addChoosableFileFilter(filterBMP);
    chooser.addChoosableFileFilter(filterGIF);
    chooser.addChoosableFileFilter(filterJPG);
    chooser.addChoosableFileFilter(filterALL);
    
    int returnVal = chooser.showOpenDialog(mform);
    if (returnVal != 0) {
      return;
    }
    
    strLastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
    readFile(chooser.getSelectedFile());
  }
  






  void readFile(File file)
  {
    String filename = file.getAbsolutePath();
    System.out.println("Ficheiro a ler: " + file);
    if ((!file.exists()) || (!file.isFile()) || (!file.canRead())) {
      throw new MError("MIN-MIMG05E", new String[] { filename });
    }
    int point = filename.lastIndexOf('.') + 1;
    if ((point <= 2) || (point >= filename.length())) {
      throw new MError("MIN-MIMG09E", new String[] { filename });
    }
    String extension = filename.substring(point).toLowerCase();
    
    Image image = null;
    if (PT.Minimal.util.MString.in(extension, new String[] { "jpg", "jpeg", "gif" }) != -1) {
      image = Toolkit.getDefaultToolkit().createImage(filename);
    } else if (extension.equals("bmp")) {
      MBmpFile mbmpFile = new MBmpFile();
      image = mbmpFile.loadbitmap(filename);
    } else {
      throw new MError("MIN-MIMG04E", new String[] { extension, filename });
    }
    


    MediaTracker tracker = new MediaTracker(mimg);
    tracker.addImage(image, 0);
    try {
      tracker.waitForAll();
    }
    catch (InterruptedException e) {}
    tracker.removeImage(image);
    
    adicionarNovaImagem(image, file.getName(), filename);
  }
  




  void writeFile()
  {
    if (vctImagens.isEmpty()) {
      throw new MError("MIN-MIMG07E");
    }
    
    JFileChooser chooser = new JFileChooser(strLastDirectory);
    chooser.setDialogTitle("Ficheiro a escrever");
    
    MFileFilter filterJPG = new MFileFilter(new String[] { "jpg", "jpeg" }, "Imagem JPEG");
    MFileFilter filterBMP = new MFileFilter(new String[] { "bmp" }, "Imagem BMP");
    chooser.addChoosableFileFilter(filterBMP);
    chooser.addChoosableFileFilter(filterJPG);
    
    int returnVal = chooser.showSaveDialog(mform);
    if (returnVal != 0) {
      return;
    }
    
    strLastDirectory = chooser.getCurrentDirectory().getAbsolutePath();
    
    writeFile(chooser.getSelectedFile());
  }
  






  void writeFile(File file)
  {
    String filename = file.getAbsolutePath();
    
    int point = filename.lastIndexOf('.') + 1;
    String extension; String extension; if ((point > 1) && (point < filename.length())) {
      extension = filename.substring(point).toLowerCase();
    } else {
      extension = "jpeg";
      filename = filename + ".jpeg";
      file = new File(filename);
    }
    
    if (file.exists()) {
      MError msg = new MError("MIN-MIMG06Q", new String[] { filename });
      if (msg.show() != 0) {
        return;
      }
    }
    
    mform.setUnFormattedFormValue("twritepath", filename);
    
    MHashtable hstImage = (MHashtable)vctImagens.elementAt(imagePosition);
    byte[] bbin = (byte[])hstImage.get("bbin");
    try
    {
      if ((extension.equals("jpg")) || (extension.equals("jpeg"))) {
        FileOutputStream dataOut = new FileOutputStream(file);
        try {
          dataOut.write(bbin);
        } finally {
          try {
            dataOut.close();
          } catch (IOException e) {}
        }
      }
      if (extension.equals("bmp")) {
        Image image = Toolkit.getDefaultToolkit().createImage(bbin);
        
        MediaTracker tracker = new MediaTracker(mimg);
        tracker.addImage(image, 0);
        try {
          tracker.waitForAll();
        }
        catch (InterruptedException e) {}
        tracker.removeImage(image);
        
        MBmpFile mbmpFile = new MBmpFile();
        mbmpFile.saveBitmap(filename, image, image.getWidth(null), image.getHeight(null));
      } else {
        throw new MError("MIN-MIMG04E", new String[] { extension, filename });

      }
      

    }
    catch (IOException e)
    {

      e.printStackTrace();
    }
  }
  





  void readClipboard()
  {
    updateCurrentImage();
    
    Image image = null;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    Transferable t = clipboard.getContents(this);
    try
    {
      if ((t != null) && (t.isDataFlavorSupported(MImageSelection.imageFlavor))) {
        image = (Image)t.getTransferData(MImageSelection.imageFlavor);
        adicionarNovaImagem(image, "Imagem lida do clipboard", "Imagem lida do clipboard");
      }
    } catch (java.awt.datatransfer.UnsupportedFlavorException e) {
      throw new MError("MIN-MIMG08W");
    } catch (IOException e) {
      throw new MError("MIN-MIMG08E");
    }
  }
  




  void writeClipboard()
  {
    if (!vctImagens.isEmpty()) {
      byte[] bbin = (byte[])((MHashtable)vctImagens.elementAt(imagePosition)).get("bbin");
      
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      MImageSelection contents = new MImageSelection(Toolkit.getDefaultToolkit().createImage(bbin));
      clipboard.setContents(contents, null);
    } else {
      throw new MError("MIN-MIMG07E");
    }
  }
  




  public void eraseImage()
  {
    MError msg = new MError("MIN-MIMG02Q");
    if (msg.show() != 0) {
      return;
    }
    
    vctImagens.remove(imagePosition);
    setChanged(true);
    
    int position = imagePosition;
    if (position >= vctImagens.size()) {
      position--;
    }
    
    if (vctImagens.isEmpty()) {
      ((MButton)mform.getFormComponent("beraseimage")).setEnabled(false);
      ((MButton)mform.getFormComponent("beraseall")).setEnabled(false);
      ((MButton)mform.getFormComponent("bwritefile")).setEnabled(false);
      ((MButton)mform.getFormComponent("bwriteclip")).setEnabled(false);
    }
    
    mform.setUnFormattedFormValue("qmaximg", "" + vctImagens.size());
    
    int nextImage = PT.Minimal.util.MNumberUtil.toInt(mform.getFormValue("qnextimage"));
    if (nextImage > position + 1) {
      mform.setUnFormattedFormValue("qnextimage", "" + (nextImage - 1));
    }
    
    calcTotalKb();
    
    setCurrentImage(position);
  }
  




  public void eraseAllImages()
  {
    MError msg = new MError("MIN-MIMG01Q");
    if (msg.show() != 0) {
      return;
    }
    
    vctImagens.removeAllElements();
    mimg.setImagePosition(-1);
    mimg.setImage(null);
    setChanged(true);
    
    ((MButton)mform.getFormComponent("bnext")).setEnabled(false);
    ((MButton)mform.getFormComponent("bprev")).setEnabled(false);
    ((MButton)mform.getFormComponent("beraseimage")).setEnabled(false);
    ((MButton)mform.getFormComponent("beraseall")).setEnabled(false);
    ((MButton)mform.getFormComponent("bwritefile")).setEnabled(false);
    ((MButton)mform.getFormComponent("bwriteclip")).setEnabled(false);
    mform.setUnFormattedFormValue("qcurimg", "0");
    mform.setUnFormattedFormValue("qmaximg", "0");
    mform.setUnFormattedFormValue("qkb", "0");
    mform.setUnFormattedFormValue("qkbtotal", "0");
    mform.setUnFormattedFormValue("tdesc", "");
    mform.setUnFormattedFormValue("qnextimage", "1");
  }
  



  public void updateCurrentImage()
  {
    if (imagePosition < 0) {
      return;
    }
    
    MHashtable hst = (MHashtable)vctImagens.get(imagePosition);
    String sDesc = mform.getFormValue("tdesc");
    if (!hst.getString("nbin").equals(sDesc)) {
      hst.put("nbin", sDesc);
      setChanged(true);
    }
  }
  




  public void previousImage()
  {
    updateCurrentImage();
    
    int position = imagePosition - 1;
    if (position >= 0) {
      setCurrentImage(position);
    }
  }
  




  public void nextImage()
  {
    updateCurrentImage();
    
    int position = imagePosition + 1;
    if (position < vctImagens.size()) {
      setCurrentImage(position);
    }
  }
  





  public void setCurrentImage(int position)
  {
    int size = vctImagens.size();
    
    if (position < 0) {
      position = 0;
    }
    if (position >= size) {
      position = size - 1;
    }
    mimg.setImagePosition(position);
    
    MButton mbtNext = (MButton)mform.getFormComponent("bnext");
    mbtNext.setEnabled((position + 1 < size) && (size != 0));
    MButton mbtPrevious = (MButton)mform.getFormComponent("bprev");
    mbtPrevious.setEnabled((position > 0) && (size != 0));
    
    if (size != 0) {
      System.out.println("Imagens: " + size);
      
      MHashtable hstImagem = (MHashtable)vctImagens.elementAt(position);
      byte[] bbin = (byte[])hstImagem.get("bbin");
      Image img = Toolkit.getDefaultToolkit().createImage(bbin);
      MediaTracker tracker = new MediaTracker(mimg);
      tracker.addImage(img, 0);
      try {
        tracker.waitForAll();
      }
      catch (InterruptedException e) {}
      tracker.removeImage(img);
      mimg.setImage(img);
      
      mform.setUnFormattedFormValue("qkb", "" + bbin.length / 1024);
      mform.setUnFormattedFormValue("qcurimg", "" + (position + 1));
      mform.setUnFormattedFormValue("tdesc", hstImagem.getString("nbin"));
      mform.setUnFormattedFormValue("treadpath", hstImagem.getString("tpath"));
    }
    else {
      mform.setUnFormattedFormValue("qkb", "0");
      mform.setUnFormattedFormValue("qcurimg", "0");
      mform.setUnFormattedFormValue("tdesc", "");
      mform.setUnFormattedFormValue("treadpath", "");
      mimg.setImage(null);
    }
    
    mimg.repaint();
    mform.getCenterPanel().repaint();
  }
  









  public byte[] JpegEncode(Image image, float qqual)
  {
    ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
    BufferedImage bufImg = toBufferedImage(image);
    try
    {
      com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(dataOut);
      com.sun.image.codec.jpeg.JPEGEncodeParam myParams = com.sun.image.codec.jpeg.JPEGCodec.getDefaultJPEGEncodeParam(bufImg);
      
      System.out.println("Parametros: " + myParams);
      System.out.println("Qualidade: " + qqual);
      myParams.setQuality(qqual, true);
      encoder.setJPEGEncodeParam(myParams);
      encoder.encode(bufImg);
      dataOut.flush();
    }
    catch (IOException e) {}
    
    return dataOut.toByteArray();
  }
  









  public void adicionarNovaImagem(Image image, String tdesc, String tpath)
  {
    String squal = mform.getFormValue("qqual");
    float qqual;
    try {
      qqual = Float.valueOf(squal).floatValue() / 100.0F;
    } catch (NumberFormatException e) {
      throw new MError("MIN-MIMG11E", new String[] { squal });
    }
    if ((qqual < 0.01D) || (qqual > 1.0F)) {
      throw new MError("MIN-MIMG11E", new String[] { squal });
    }
    
    int size = vctImagens.size();
    int position = PT.Minimal.util.MNumberUtil.toInt(mform.getFormValue("qnextimage")) - 1;
    
    if (position < 0) {
      position = 0;
    } else if (position > size) {
      position = size;
    }
    
    setChanged(true);
    MHashtable hstImagem = new MHashtable();
    if (position == size) {
      vctImagens.addElement(hstImagem);
    } else {
      vctImagens.insertElementAt(hstImagem, position);
    }
    size++;
    
    hstImagem.put("bbin", JpegEncode(image, qqual));
    hstImagem.put("tpath", tpath);
    hstImagem.put("nbin", tdesc);
    
    if (vctImagens.size() == 1) {
      ((MButton)mform.getFormComponent("beraseimage")).setEnabled(true);
      ((MButton)mform.getFormComponent("beraseall")).setEnabled(true);
      ((MButton)mform.getFormComponent("bwritefile")).setEnabled(true);
      ((MButton)mform.getFormComponent("bwriteclip")).setEnabled(true);
    }
    mform.setUnFormattedFormValue("qmaximg", "" + size);
    mform.setUnFormattedFormValue("qnextimage", "" + (position + 2));
    calcTotalKb();
    
    setCurrentImage(position);
    mform.showComponentTab("gimagem");
  }
  

  protected void setChanged(boolean status)
  {
    if (status != mimg.isChanged()) {
      mimg.setChanged(status);
      String title = mform.getEntName() + " - Imagens";
      if (status) {
        title = title + " *";
      }
      ((MFrame)mform.getFormParent()).setTitle(title);
    }
  }
  
  protected void getMImage() {
    if (mimg == null) {
      mimg = ((MImage)mform.getFormComponent("gimagem"));
      vctImagens = mimg.getImageVector();
      imagePosition = mimg.getImagePosition();
    }
  }
  
  protected void calcTotalKb()
  {
    long totalSize = 0L;
    int index = 0; for (int size = vctImagens.size(); index < size; index++) {
      MHashtable hstImagem = (MHashtable)vctImagens.elementAt(index);
      totalSize += ((byte[])hstImagem.get("bbin")).length / 1024;
    }
    
    mform.setUnFormattedFormValue("qkbtotal", "" + totalSize);
  }
}
