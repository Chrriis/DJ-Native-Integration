/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.wapacker.uninstallws;

import java.io.File;
import java.util.List;
import java.util.jar.JarFile;

import chrriis.dj.wapacker.wslink.WSLinkGenerator;

import com.izforge.izpack.event.SimpleUninstallerListener;
import com.izforge.izpack.util.AbstractUIProgressHandler;

/**
 * @author Christopher Deckers
 */
public class UninstallWSUninstallerListener extends SimpleUninstallerListener {

  @SuppressWarnings("unchecked")
  @Override
  public void beforeDeletion(List files, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    if(System.getProperty("java.version").compareTo("1.6") >= 0) {
      for(int i=files.size()-1; i>=0; i--) {
        File file = (File)files.get(i);
        JarFile jarFile = null;
        try {
          jarFile = new JarFile(file);
          String jnlpURLString = jarFile.getManifest().getMainAttributes().getValue(WSLinkGenerator.WS_URL_ATTRIBUTE).trim();
          if(jnlpURLString.length() > 0) {
            ProcessBuilder processBuilder = new ProcessBuilder("javaws", "-uninstall", jnlpURLString);
            Process process = processBuilder.start();
            process.waitFor();
          }
        } catch(Exception e) {
        }
        if(jarFile != null) {
          jarFile.close();
        }
      }
    }
    File importDir = new File(new File(System.getProperty("user.dir")).getParent(), ".djwaimport");
    if(importDir.exists()) {
      cleanUp(importDir);
    }
    super.beforeDeletion(files, abstractUIProgressHandler);
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

}
