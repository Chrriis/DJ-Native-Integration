/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.shellextension;

import java.io.File;
import java.util.List;

import com.izforge.izpack.event.SimpleUninstallerListener;
import com.izforge.izpack.util.AbstractUIProgressHandler;
import com.izforge.izpack.util.os.RegistryDefaultHandler;
import com.izforge.izpack.util.os.RegistryHandler;

/**
 * @author Christopher Deckers
 */
public class DJShellExtensionUninstallerListener extends SimpleUninstallerListener {

  public void beforeDeletion(List files, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    RegistryHandler rh = RegistryDefaultHandler.getInstance();
    rh.setRoot(RegistryHandler.HKEY_CLASSES_ROOT);
    String keyPath = "jarfile\\DefaultIcon";
    if(rh.keyExist(keyPath)) {
      rh.deleteValue(keyPath, "");
    }
    rh.deleteKeyIfEmpty(keyPath);
    keyPath = "jarfile\\ShellEx\\IconHandler";
    if(rh.keyExist(keyPath)) {
      rh.deleteValue(keyPath, "");
    }
    rh.deleteKeyIfEmpty("jarfile\\ShellEx\\IconHandler");
    rh.deleteKeyIfEmpty("jarfile\\ShellEx\\PropertySheetHandlers\\{5EC050B4-7064-44B9-8D71-EC1A33DFB604}");
    rh.deleteKeyIfEmpty("jarfile\\ShellEx\\PropertySheetHandlers");
    rh.deleteKeyIfEmpty("jarfile\\ShellEx");
  }

  @Override
  public void beforeDelete(File file, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    if(!file.getName().equals("DJShellExtension.dll")) {
      return;
    }
    Process process = new ProcessBuilder("regsvr32.exe", "/u", "/s", file.getAbsolutePath()).start();
    process.waitFor();
    try {
      Thread.sleep(500);
    } catch(Exception e) {
    }
    if(!file.delete()) {
      File tempFile = File.createTempFile("~DJ", "DL_");
      tempFile.delete();
      file.renameTo(tempFile);
    }
  }
  
  public static void main(String[] args) {
    try {
      DJShellExtensionUninstallerListener shellExtensionUninstallerListener = new DJShellExtensionUninstallerListener();
      shellExtensionUninstallerListener.beforeDeletion(null, null);
      shellExtensionUninstallerListener.beforeDelete(new File("C:\\Program Files\\DJ Project\\DJ ShellExtension\\DJShellExtension.dll"), null);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
