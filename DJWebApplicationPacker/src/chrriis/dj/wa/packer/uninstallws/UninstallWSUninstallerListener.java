package chrriis.dj.wa.packer.uninstallws;

import java.io.File;
import java.util.List;
import java.util.jar.JarFile;

import chrriis.dj.wa.wslink.WSLinkGenerator;

import com.izforge.izpack.event.SimpleUninstallerListener;
import com.izforge.izpack.util.AbstractUIProgressHandler;

/**
 * @author Christopher Deckers
 */
public class UninstallWSUninstallerListener extends SimpleUninstallerListener {

  @SuppressWarnings("unchecked")
  @Override
  public void beforeDeletion(List files, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    if(System.getProperty("java.version").compareTo("1.6") < 0) {
      return;
    }
    for(int i=files.size()-1; i>=0; i--) {
      File file = (File)files.get(i);
      try {
        String jnlpURLString = new JarFile(file).getManifest().getMainAttributes().getValue(WSLinkGenerator.WS_URL_ATTRIBUTE).trim();
        if(jnlpURLString.length() > 0) {
          ProcessBuilder processBuilder = new ProcessBuilder("javaws", "-uninstall", jnlpURLString);
          processBuilder.start();
        }
      } catch(Exception e) {
      }
    }
    super.beforeDeletion(files, abstractUIProgressHandler);
  }
  
}
