package chrriis.dj.wa.wslink;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author Christopher Deckers
 */
public class WSLink {

  protected static final String WS_URL_ATTRIBUTE = "WS-URL";

  public WSLink(String[] args) {
    String url = null;
    try {
      String referenceName = getClass().getName();
      referenceName = referenceName.replace('.', '/') + ".class";
      String name = referenceName.substring(referenceName.lastIndexOf('/') + 1);
      String externalForm = getClass().getResource(name).toExternalForm();
      int index = externalForm.indexOf(referenceName);
      externalForm = externalForm.substring(0, index) + JarFile.MANIFEST_NAME + externalForm.substring(index + referenceName.length());
      InputStream in = new URL(externalForm).openStream();
      Attributes mainAttributes = new Manifest(in).getMainAttributes();
      url = mainAttributes.getValue(WS_URL_ATTRIBUTE);
      in.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
    if(url == null) {
      System.err.println("JNLP location could not be read from manifest definition!");
      return;
    }
    String[] newArgs = new String[args.length + 2];
    newArgs[0] = "javaws";
    System.arraycopy(args, 0, newArgs, 1, args.length);
    newArgs[newArgs.length - 1] = url;
    System.out.print("Opening:");
    for(String arg: newArgs) {
      System.out.print(" " + arg);
    }
    System.out.println();
    ProcessBuilder processBuilder = new ProcessBuilder(newArgs);
    try {
      processBuilder.start();
    } catch(Exception e) {
      e.printStackTrace();
    }
    File importDir = new File(".djwaimport");
    if(importDir.exists()) {
      cleanUp(importDir);
    }
  }
  
  protected void cleanUp(File tmpDir) {
    if(!tmpDir.delete()) {
      if(tmpDir.isDirectory()) {
        for(File file: tmpDir.listFiles()) {
          cleanUp(file);
        }
        tmpDir.delete();
      }
    }
  }

  public static void main(String[] args) {
    new WSLink(args);
  }
  
}
