/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Christopher Deckers
 */
public class UIUtil {

  protected static final JFileChooser FILE_CHOOSER = new JFileChooser();
  static {
    FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
  }
  
  public static JFileChooser getJarFileChooser() {
    FILE_CHOOSER.setMultiSelectionEnabled(false);
    FILE_CHOOSER.removeChoosableFileFilter(imageFileFilter);
    FILE_CHOOSER.setFileFilter(jarFileFilter);
    return FILE_CHOOSER;
  }
  

  protected static FileFilter jarFileFilter = new FileFilter() {
    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar");
    }
    @Override
    public String getDescription() {
      return "JAR files (*.jar)";
    }
  };

  protected static FileFilter imageFileFilter = new FileFilter() {
    @Override
    public boolean accept(File f) {
      String lcName = f.getName().toLowerCase(Locale.ENGLISH);
      return f.isDirectory() || lcName.endsWith(".gif") || lcName.endsWith(".png");
    }
    @Override
    public String getDescription() {
      return "GIF and PNG files (*.gif, *.png)";
    }
  };

  public static JFileChooser getImagesFileChooser() {
    FILE_CHOOSER.setMultiSelectionEnabled(true);
    FILE_CHOOSER.removeChoosableFileFilter(jarFileFilter);
    FILE_CHOOSER.setFileFilter(imageFileFilter);
    return FILE_CHOOSER;
  }
  
  public static void setWorkingDirectory(File workingDirectory) {
    FILE_CHOOSER.setCurrentDirectory(workingDirectory);
  }

  public static final DataFlavor URI_LIST_FLAVOR;
  static {
    DataFlavor uriListFlavor;
    try {
      uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
    } catch(Exception e) {
      uriListFlavor = null;
    }
    URI_LIST_FLAVOR = uriListFlavor;
  }

  @SuppressWarnings("unchecked")
  public static List<File> getFileList(Transferable t, boolean isJavaFileListFlavorSupported, boolean isURIListFlavorSupported) {
    try {
      List<File> fileList;
      if(isJavaFileListFlavorSupported) {
        fileList = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
      } else if(isURIListFlavorSupported) {
        fileList = new ArrayList<File>();
        // RFC 2483: text/uri-list format.
        String uriList = (String) t.getTransferData(URI_LIST_FLAVOR);
        String[] filepathList = uriList.split("\r\n");
        for(int i = 0; i < filepathList.length; i++) {
          String filepath = filepathList[i].trim();
          if(filepath.length() > 0) {
            filepath = filepath.replace("%25%25", "%25");
            fileList.add(new File(URI.create(filepath)));
          }
        }
      } else {
        return Collections.EMPTY_LIST;
      }
      return fileList;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return Collections.EMPTY_LIST;
  }

}
