/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.wapacker;

import java.io.File;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author Christopher Deckers
 */
public class Util {

  protected static final FileFilter JNLP_FILE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".jnlp");
    }
    @Override
    public String getDescription() {
      return "JNLP descriptor files (*.jnlp)";
    }
  };

  protected static final FileFilter JAR_FILE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar");
    }
    @Override
    public String getDescription() {
      return "JAR files (*.jar)";
    }
  };

  protected static final FileFilter ZIP_JAR_FILE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File f) {
      String lcName = f.getName().toLowerCase(Locale.ENGLISH);
      return f.isDirectory() || lcName.endsWith(".zip") || lcName.endsWith(".jar");
    }
    @Override
    public String getDescription() {
      return "ZIP and JAR files (*.zip, *.jar)";
    }
  };
  
  protected static final FileFilter TXT_HTML_FILE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File f) {
      String lcName = f.getName().toLowerCase(Locale.ENGLISH);
      return f.isDirectory() || lcName.endsWith(".txt") || lcName.endsWith(".html") || lcName.endsWith(".htm");
    }
    @Override
    public String getDescription() {
      return "Text and HTML files (*.txt, *.html, *.htm)";
    }
  };
  
  protected static JFileChooser FILE_CHOOSER = new JFileChooser();
  static {
    FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
  }
  
  public static JFileChooser getJnlpFileChooser() {
    FILE_CHOOSER.setMultiSelectionEnabled(true);
    FILE_CHOOSER.removeChoosableFileFilter(TXT_HTML_FILE_FILTER);
    FILE_CHOOSER.removeChoosableFileFilter(JAR_FILE_FILTER);
    FILE_CHOOSER.removeChoosableFileFilter(ZIP_JAR_FILE_FILTER);
    FILE_CHOOSER.setFileFilter(JNLP_FILE_FILTER);
    return FILE_CHOOSER;
  }
  
  public static JFileChooser getJarFileChooser() {
    FILE_CHOOSER.setMultiSelectionEnabled(true);
    FILE_CHOOSER.removeChoosableFileFilter(TXT_HTML_FILE_FILTER);
    FILE_CHOOSER.removeChoosableFileFilter(JNLP_FILE_FILTER);
    FILE_CHOOSER.removeChoosableFileFilter(ZIP_JAR_FILE_FILTER);
    FILE_CHOOSER.setFileFilter(JAR_FILE_FILTER);
    return FILE_CHOOSER;
  }
  
  public static JFileChooser getZipJarFileChooser() {
    FILE_CHOOSER.setMultiSelectionEnabled(true);
    FILE_CHOOSER.removeChoosableFileFilter(TXT_HTML_FILE_FILTER);
    FILE_CHOOSER.removeChoosableFileFilter(JNLP_FILE_FILTER);
    FILE_CHOOSER.removeChoosableFileFilter(JAR_FILE_FILTER);
    FILE_CHOOSER.setFileFilter(ZIP_JAR_FILE_FILTER);
    return FILE_CHOOSER;
  }
  
  public static JFileChooser getTextHtmlFileChooser() {
    FILE_CHOOSER.setMultiSelectionEnabled(true);
    FILE_CHOOSER.removeChoosableFileFilter(JNLP_FILE_FILTER);
    FILE_CHOOSER.removeChoosableFileFilter(JAR_FILE_FILTER);
    FILE_CHOOSER.removeChoosableFileFilter(ZIP_JAR_FILE_FILTER);
    FILE_CHOOSER.setFileFilter(TXT_HTML_FILE_FILTER);
    return FILE_CHOOSER;
  }
  
  public static File getApplicationDirectory() {
    String path = Util.class.getResource('/' + Util.class.getName().replace('.', '/') + ".class").toExternalForm();
    if(path.startsWith("jar:file:/")) {
      path = path.substring("jar:file:/".length());
      path = path.substring(0, path.indexOf("!"));
      File dir = new File(path).getParentFile();
      if(dir.exists() && dir.isDirectory()) {
        System.err.println(dir.getAbsolutePath());
        return dir;
      }
    }
    return new File(".");
  }
  
}
