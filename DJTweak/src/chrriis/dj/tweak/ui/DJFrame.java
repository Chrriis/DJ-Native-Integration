/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * @author Christopher Deckers
 */
public class DJFrame extends JFrame {

  public DJFrame(File jarFile) {
    super("DJ Tweak");
    if(System.getProperty("java.version").compareTo("1.6") >= 0) {
      setIconImages(Arrays.asList(new Image[] {
          new ImageIcon(getClass().getResource("resource/TweakIcon16x16.png")).getImage(),
          new ImageIcon(getClass().getResource("resource/TweakIcon24x24.png")).getImage(),
          new ImageIcon(getClass().getResource("resource/TweakIcon32x32.png")).getImage(),
          new ImageIcon(getClass().getResource("resource/TweakIcon48x48.png")).getImage(),
          new ImageIcon(getClass().getResource("resource/TweakIcon256x256.png")).getImage(),
      }));
    } else {
      setIconImage(new ImageIcon(getClass().getResource("resource/TweakIcon32x32Plain.png")).getImage());
    }
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    final DJPane djPane = new DJPane();
    setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
      @Override
      public void dragEnter(DropTargetDragEvent dtde) {
        processDrag(dtde);
      }
      @Override
      public void dragOver(DropTargetDragEvent dtde) {
        processDrag(dtde);
      }
      @Override
      public void dropActionChanged(DropTargetDragEvent dtde) {
        processDrag(dtde);
      }
      protected void processDrag(DropTargetDragEvent dtde) {
        int sourceActions = dtde.getSourceActions();
        if((sourceActions & DnDConstants.ACTION_COPY) == 0) {
          dtde.rejectDrag();
          return;
        }
        if(isFileListValid(UIUtil.getDnDFileList(dtde))) {
          dtde.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
          dtde.rejectDrag();
        }
      }
      public void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        List<File> fileList = UIUtil.getDnDFileList(dtde);
        if(isFileListValid(fileList)) {
          djPane.loadJarFile(fileList.get(0));
        }
      }
      protected boolean isFileListValid(List<File> fileList) {
        if(fileList.size() != 1) {
          return false;
        }
        return fileList.get(0).getName().toLowerCase(Locale.ENGLISH).endsWith(".jar");
      }
    }));
    getContentPane().add(djPane, BorderLayout.CENTER);
    setSize(600, 400);
    setLocationByPlatform(true);
    if(jarFile != null) {
      djPane.loadJarFile(jarFile);
    }
  }
  
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch(Exception e) {
      e.printStackTrace();
    }
    File jarFile;
    if(args.length > 0) {
      jarFile = new File(args[0]);
    } else {
      jarFile = null;
    }
    new DJFrame(jarFile).setVisible(true);
  }

}
