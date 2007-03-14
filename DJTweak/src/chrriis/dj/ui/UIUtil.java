/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.ui;

import java.io.File;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Christopher Deckers
 */
public class UIUtil {

  public static final String JAR_ICONS_PATH = "JarIcons/";

  protected final static JFileChooser JAR_FILE_CHOOSER = new JFileChooser();
  static {
    JAR_FILE_CHOOSER.setFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar");
      }
      @Override
      public String getDescription() {
        return "JAR files (*.jar)";
      }
    });
  }
  
  public static JFileChooser getJarFileChooser() {
    return JAR_FILE_CHOOSER;
  }
  
  protected static final JFileChooser FILE_CHOOSER = new JFileChooser();

  public static JFileChooser getImagesFileChooser() {
    FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
    FILE_CHOOSER.setMultiSelectionEnabled(true);
    FILE_CHOOSER.setFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        String lcName = f.getName().toLowerCase(Locale.ENGLISH);
        return f.isDirectory() || lcName.endsWith(".gif") || lcName.endsWith(".png");
      }
      @Override
      public String getDescription() {
        return "GIF and PNG files (*.gif, *.png)";
      }
    });
    return FILE_CHOOSER;
  }
  

}
