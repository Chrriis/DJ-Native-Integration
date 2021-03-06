/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.ui.screen;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chrriis.dj.tweak.data.DataUtil;
import chrriis.dj.tweak.data.IconInfo;
import chrriis.dj.tweak.data.JarFileInfo;
import chrriis.dj.tweak.ui.DJPane;
import chrriis.dj.tweak.ui.IconInfoJList;
import chrriis.dj.tweak.ui.UIUtil;

/**
 * @author Christopher Deckers
 */
public class IconsPanel extends JPanel {

  protected IconInfoJList iconInfoJList;
  protected JButton addFromJarButton;
  protected JButton addFromFileButton;
  protected JButton removeButton;
  
  public IconsPanel(final DJPane djPane) {
    super(new BorderLayout(0, 0));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    iconInfoJList = new IconInfoJList();
    iconInfoJList.setDropTarget(new DropTarget(iconInfoJList, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
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
          if(fileList.size() == 1) {
            File file = fileList.get(0);
            if(file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar")) {
              djPane.loadJarFile(file);
              return;
            }
          }
          importExternalFiles(fileList.toArray(new File[0]));
        }
      }
      protected boolean isFileListValid(List<File> fileList) {
        for(File file: fileList) {
          String lcName = file.getName().toLowerCase(Locale.ENGLISH);
          if(!lcName.endsWith(".gif") && !lcName.endsWith(".png")) {
            return lcName.endsWith(".jar") && fileList.size() == 1;
          }
        }
        if(!iconInfoJList.isEnabled()) {
          return false;
        }
        return !fileList.isEmpty();
      }
    }));
    add(new JScrollPane(iconInfoJList), BorderLayout.CENTER);
    JPanel iconActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
    iconActionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
    addFromJarButton = new JButton("Add from JAR File...");
    iconActionsPanel.add(addFromJarButton);
    addFromFileButton = new JButton("Add from File Sytem...");
    iconActionsPanel.add(addFromFileButton);
    removeButton = new JButton("Remove");
    iconActionsPanel.add(removeButton);
    add(iconActionsPanel, BorderLayout.SOUTH);
    iconInfoJList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if(jarFileInfo == null || jarFileInfo.isSigned()) {
          return;
        }
        removeButton.setEnabled(iconInfoJList.getSelectedIndices().length != 0);
      }
    });
    removeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        List<IconInfo> iconInfoList = new ArrayList<IconInfo>();
        ListModel model = iconInfoJList.getModel();
        for(int i=model.getSize()-1; i>=0; i--) {
          if(!iconInfoJList.isSelectedIndex(i)) {
            iconInfoList.add((IconInfo)model.getElementAt(i));
          }
        }
        iconInfoJList.setModel(iconInfoList.toArray(new IconInfo[0]));
        IconsPanel.this.firePropertyChange("jarModified", false, true);
      }
    });
    addFromFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser imageFileChooser = UIUtil.getImagesFileChooser();
        if(imageFileChooser.showOpenDialog(IconsPanel.this) == JFileChooser.APPROVE_OPTION) {
          importExternalFiles(imageFileChooser.getSelectedFiles());
        }
      }
    });
    addFromJarButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Window window = SwingUtilities.getWindowAncestor(IconsPanel.this);
        final JDialog dialog;
        if(window instanceof Frame) {
          dialog = new JDialog((Frame)window, "Image Selection", true);
        } else {
          dialog = new JDialog((Dialog)window, "Image Selection", true);
        }
        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        Set<String> imagePathSet = new HashSet<String>();
        final List<IconInfo> iconInfoList = new ArrayList<IconInfo>();
        for(int i=iconInfoJList.getModel().getSize()-1; i>=0; i--) {
          IconInfo IconInfo = (IconInfo)iconInfoJList.getModel().getElementAt(i);
          iconInfoList.add(IconInfo);
          imagePathSet.add((IconInfo).getPath());
        }
        List<IconInfo> availableIconInfoList = new ArrayList<IconInfo>();
        for(String imagePath: jarFileInfo.getImagePaths()) {
          if(!imagePathSet.contains(imagePath)) {
            Dimension size = DataUtil.getImageSize(jarFileInfo.getImageURL(imagePath));
            availableIconInfoList.add(new IconInfo(size.width, size.height, imagePath, null));
          }
        }
        final IconInfoJList availableIconInfoJList = new IconInfoJList(jarFileInfo, availableIconInfoList.toArray(new IconInfo[0]));
        JScrollPane scrollPane = new JScrollPane(availableIconInfoJList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        contentPane.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        final JButton addButton = new JButton("Add");
        addButton.setEnabled(false);
        final Runnable actionRunnable = new Runnable() {
          public void run() {
            Object[] selectedValues = availableIconInfoJList.getSelectedValues();
            List<IconInfo> addedIconInfoList = new ArrayList<IconInfo>();
            boolean isModified = false;
            for(int i=0; i<selectedValues.length; i++) {
              IconInfo selectedIconInfo = (IconInfo)selectedValues[i];
              boolean isValid = true;
              int iconWidth = selectedIconInfo.getWidth();
              int iconHeight = selectedIconInfo.getHeight();
              if(iconWidth != iconHeight) {
                String path = selectedIconInfo.getPath();
                isValid = JOptionPane.showOptionDialog(IconsPanel.this, "The icon \"" + path.substring(path.lastIndexOf('/') + 1) + "\" has a width that does not equal its height, which may be ignored by the icon extension.\nDo you want to add it anyway?", "Non-square icon", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION;
              }
              if(isValid) {
                for(Iterator<IconInfo> it=iconInfoList.iterator(); it.hasNext(); ) {
                  IconInfo iconInfo = it.next();
                  if(iconInfo.getWidth() == iconWidth && iconInfo.getHeight() == iconHeight) {
                    String path = selectedIconInfo.getPath();
                    if(JOptionPane.showOptionDialog(IconsPanel.this, "The icon \"" + path.substring(path.lastIndexOf('/') + 1) + "\" has the same size as another icon.\nDo you want to replace the other icon?", "Duplicate sizes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                      it.remove();
                    } else {
                      isValid = false;
                    }
                    break;
                  }
                }
              }
              if(isValid) {
                addedIconInfoList.add(selectedIconInfo);
                isModified = true;
              }
            }
            if(isModified) {
              final List<Object> newIconInfoList = new ArrayList<Object>();
              newIconInfoList.addAll(iconInfoList);
              newIconInfoList.addAll(addedIconInfoList);
              iconInfoJList.setModel(newIconInfoList.toArray(new IconInfo[0]));
              IconsPanel.this.firePropertyChange("jarModified", false, true);
              dialog.dispose();
            }
          }
        };
        addButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            actionRunnable.run();
          }
        });
        availableIconInfoJList.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
              actionRunnable.run();
            }
          }
        });
        buttonPane.add(addButton);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
        availableIconInfoJList.addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            addButton.setEnabled(availableIconInfoJList.getSelectedIndices().length > 0);
          }
        });
        dialog.getContentPane().add(contentPane);
        dialog.pack();
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
      }
    });
    loadContent(null);
  }

  protected void importExternalFiles(File[] files) {
    List<IconInfo> iconInfoList = new ArrayList<IconInfo>();
    for(int i=iconInfoJList.getModel().getSize()-1; i>=0; i--) {
      IconInfo IconInfo = (IconInfo)iconInfoJList.getModel().getElementAt(i);
      iconInfoList.add(IconInfo);
    }
    boolean isModified = false;
    for(File file: files) {
      try {
        URL fileURL = file.toURI().toURL();
        ImageIcon imageIcon = new ImageIcon(fileURL);
        int iconWidth = imageIcon.getIconWidth();
        int iconHeight = imageIcon.getIconHeight();
        boolean isValid = true;
        if(iconWidth != iconHeight) {
          isValid = JOptionPane.showOptionDialog(IconsPanel.this, "The icon \"" + file.getName() + "\" has a width that does not equal its height, which may be ignored by the icon extension.\nDo you want to add it anyway?", "Non-square icon", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION;
        }
        if(isValid) {
          for(Iterator<IconInfo> it=iconInfoList.iterator(); it.hasNext(); ) {
            IconInfo iconInfo = it.next();
            if(iconInfo.getWidth() == iconWidth && iconInfo.getHeight() == iconHeight) {
              if(JOptionPane.showOptionDialog(IconsPanel.this, "The icon \"" + file.getName() + "\" has the same size as another icon.\nDo you want to replace the other icon?", "Duplicate sizes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                it.remove();
              } else {
                isValid = false;
              }
              break;
            }
          }
        }
        if(isValid) {
          iconInfoList.add(new IconInfo(iconWidth, iconHeight, JarFileInfo.JAR_ICONS_PATH + file.getName(), fileURL));
          isModified = true;
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    if(isModified) {
      iconInfoJList.setModel(iconInfoList.toArray(new IconInfo[0]));
      IconsPanel.this.firePropertyChange("jarModified", false, true);
    }
  }
  
  protected JarFileInfo jarFileInfo;
  
  public void loadContent(JarFileInfo jarFileInfo) {
    this.jarFileInfo = jarFileInfo;
    iconInfoJList.setJarFileInfo(jarFileInfo);
    iconInfoJList.setModel(jarFileInfo == null? null: jarFileInfo.getIconInfos());
    boolean isEnabled = jarFileInfo != null && !jarFileInfo.isSigned();
    iconInfoJList.setEnabled(jarFileInfo != null);
    addFromJarButton.setEnabled(isEnabled);
    addFromFileButton.setEnabled(isEnabled);
    removeButton.setEnabled(false);
  }

  public IconInfo[] getIconInfos() {
    List<IconInfo> iconInfoList = new ArrayList<IconInfo>();
    for(int i=iconInfoJList.getModel().getSize()-1; i>=0; i--) {
      IconInfo IconInfo = (IconInfo)iconInfoJList.getModel().getElementAt(i);
      iconInfoList.add(IconInfo);
    }
    return iconInfoList.toArray(new IconInfo[0]);
  }
  
}
