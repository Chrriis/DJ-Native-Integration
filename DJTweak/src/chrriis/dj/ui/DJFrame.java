/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

/**
 * @author Christopher Deckers
 */
public class DJFrame extends JFrame {

  public DJFrame() {
    super("DJ Tweak");
    if(System.getProperty("java.version").compareTo("1.6") >= 0) {
      setIconImages(Arrays.asList(new Image[] {
          new ImageIcon(getClass().getResource("resource/DJTweak16x16.png")).getImage(),
          new ImageIcon(getClass().getResource("resource/DJTweak24x24.png")).getImage(),
          new ImageIcon(getClass().getResource("resource/DJTweak32x32.png")).getImage(),
          new ImageIcon(getClass().getResource("resource/DJTweak48x48.png")).getImage(),
      }));
    } else {
      setIconImage(new ImageIcon(getClass().getResource("resource/DJTweak32x32Plain.png")).getImage());
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
        for(File file: fileList) {
          String lcName = file.getName().toLowerCase(Locale.ENGLISH);
          if(!lcName.endsWith(".jar")) {
            return false;
          }
        }
        return fileList.size() == 1;
      }
    }));
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    JMenuItem fileOpenMenuItem = new JMenuItem("Open...");
    fileOpenMenuItem.setMnemonic('O');
    fileOpenMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        djPane.openJarChooser();
      }
    });
    fileMenu.add(fileOpenMenuItem);
    fileMenu.addSeparator();
    JMenuItem fileExitMenuItem = new JMenuItem("Exit");
    fileExitMenuItem.setMnemonic('x');
    fileExitMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    fileMenu.add(fileExitMenuItem);
    menuBar.add(fileMenu);
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic('H');
    JMenuItem helpAboutMenuItem = new JMenuItem("About");
    helpAboutMenuItem.setMnemonic('A');
    helpAboutMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        AboutDialog aboutDialog = new AboutDialog(DJFrame.this);
        aboutDialog.setLocationRelativeTo(DJFrame.this);
        aboutDialog.setVisible(true);
      }
    });
    helpMenu.add(helpAboutMenuItem);
    menuBar.add(helpMenu);
    setJMenuBar(menuBar);
    getContentPane().add(djPane, BorderLayout.CENTER);
    setSize(600, 400);
    setLocationByPlatform(true);
  }
  
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch(Exception e) {
      e.printStackTrace();
    }
    new DJFrame().setVisible(true);
  }

}
