/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import chrriis.dj.tweak.data.DataUtil;
import chrriis.dj.tweak.data.IconInfo;
import chrriis.dj.tweak.data.JarFileInfo;

/**
 * @author Christopher Deckers
 */
public class IconInfoJList extends JList {

  protected static final BufferedImage MISSING_IMAGE;
  static {
    BufferedImage image = null;
    try {
      image = ImageIO.read(new ByteArrayInputStream(DataUtil.getImageData(IconInfoJList.class.getResource("resource/MissingImage16x16.png"))));
    } catch(Exception e) {
      e.printStackTrace();
    }
    MISSING_IMAGE = image;
  }
  
  protected static final int ICON_WIDTH = 32;
  protected static final int ICON_HEIGHT = 32;

  protected Map<String, Icon> pathToIconCachedMap = new HashMap<String, Icon>();
  
  protected Icon getCachedIcon(JarFileInfo jarFileInfo, String imagePath, URL resourceURL) {
    Icon icon = pathToIconCachedMap.get(imagePath);
    if(icon != null) {
      return icon;
    }
    BufferedImage image;
    try {
      if(resourceURL != null) {
        image = ImageIO.read(resourceURL);
      } else {
        image = ImageIO.read(new ByteArrayInputStream(DataUtil.getImageData(jarFileInfo.getImageURL(imagePath))));
      }
    } catch(Exception e) {
      image = MISSING_IMAGE;
    }
    if(image == null) {
      // Missing image is not supposed to be null...
      return null;
    }
    int iconWidth = image.getWidth();
    int iconHeight = image.getHeight();
    if(iconWidth <= ICON_WIDTH && iconHeight <= ICON_HEIGHT) {
      icon = new ImageIcon(image);
    } else {
      image = getScaledInstance(image, ICON_WIDTH, ICON_HEIGHT);
      icon = new ImageIcon(image);
    }
    pathToIconCachedMap.put(imagePath, icon);
    return icon;
  }
  
  protected static BufferedImage getScaledInstance(BufferedImage image, int targetWidth, int targetHeight) {
    int type = image.getTransparency() == Transparency.OPAQUE? BufferedImage.TYPE_INT_RGB: BufferedImage.TYPE_INT_ARGB;
    BufferedImage resultImage = image;
    //Use multi-step technique
    int width = image.getWidth();
    int height = image.getHeight();
    do {
      if(width > targetWidth) {
        width /= 2;
        if(width < targetWidth) {
          width = targetWidth;
        }
      }
      if(height > targetHeight) {
        height /= 2;
        if(height < targetHeight) {
          height = targetHeight;
        }
      }
      BufferedImage tmpImage = new BufferedImage(width, height, type);
      Graphics2D g2 = tmpImage.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2.drawImage(resultImage, 0, 0, width, height, null);
      g2.dispose();
      resultImage = tmpImage;
    } while(width != targetWidth || height != targetHeight);
    return resultImage;
  }

  protected static IconInfo[] sort(IconInfo[] iconInfos) {
    iconInfos = iconInfos.clone();
    Arrays.sort(iconInfos, new Comparator<IconInfo>() {
      public int compare(IconInfo o1, IconInfo o2) {
        return o1.getPath().toLowerCase(Locale.ENGLISH).compareTo(o2.getPath().toLowerCase(Locale.ENGLISH));
      }
    });
    return iconInfos;
  }
  
  public void setModel(IconInfo[] iconInfos) {
    final IconInfo[] newIconInfos = iconInfos == null? new IconInfo[0]: sort(iconInfos);
    setModel(new AbstractListModel() {
      public int getSize() {
        return newIconInfos.length;
      }
      public Object getElementAt(int i) {
        return newIconInfos[i];
      }
    });
  }
  
  public IconInfoJList() {
    this(null, null);
  }
  
  protected JarFileInfo jarFileInfo;
  
  public void setJarFileInfo(JarFileInfo jarFileInfo) {
    this.jarFileInfo = jarFileInfo;
  }
  
  public IconInfoJList(JarFileInfo jarFileInfo, IconInfo[] iconInfos) {
    setJarFileInfo(jarFileInfo);
    setModel(iconInfos);
    setCellRenderer(new ListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 2));
        IconInfo iconInfo = (IconInfo)value;
        String path = iconInfo.getPath();
        JarFileInfo jarFileInfo = IconInfoJList.this.jarFileInfo;
        JLabel iconLabel = new JLabel(jarFileInfo == null? null: getCachedIcon(jarFileInfo, path, iconInfo.getResourceURL()));
        iconLabel.setBorder(null);
        iconLabel.setPreferredSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
        contentPane.add(iconLabel, BorderLayout.WEST);
        Font font = UIManager.getFont("List.font");
        Color foreground;
        if(isSelected) {
          contentPane.setBackground(UIManager.getColor("List.selectionBackground"));
          foreground = UIManager.getColor("List.selectionForeground");
        } else {
          contentPane.setBackground(UIManager.getColor("List.background"));
          foreground = UIManager.getColor("List.foreground");
        }
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel infoPanel = new JPanel(gridBag);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        infoPanel.setOpaque(false);
        cons.gridx = 0;
        cons.gridy = 0;
        cons.fill = GridBagConstraints.HORIZONTAL;
        JLabel pathLabel = new JLabel("Path: ");
        pathLabel.setForeground(foreground);
        pathLabel.setFont(font);
        gridBag.setConstraints(pathLabel, cons);
        infoPanel.add(pathLabel);
        cons.gridy++;
        JLabel sizeLabel = new JLabel("Size: ");
        sizeLabel.setForeground(foreground);
        sizeLabel.setFont(font);
        gridBag.setConstraints(sizeLabel, cons);
        infoPanel.add(sizeLabel);
        cons.weightx = 1;
        cons.gridx++;
        cons.gridy = 0;
        JLabel pathValueLabel = new JLabel(path);
        pathValueLabel.setForeground(foreground);
        pathValueLabel.setFont(font);
        gridBag.setConstraints(pathValueLabel, cons);
        infoPanel.add(pathValueLabel);
        cons.gridy++;
        JLabel sizeValueLabel = new JLabel(iconInfo.getWidth() + "x" + iconInfo.getHeight());
        sizeValueLabel.setForeground(foreground);
        sizeValueLabel.setFont(font);
        gridBag.setConstraints(sizeValueLabel, cons);
        infoPanel.add(sizeValueLabel);
        contentPane.add(infoPanel, BorderLayout.CENTER);
        return contentPane;
      }
    });
  }

}
