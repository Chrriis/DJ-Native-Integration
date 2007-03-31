/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.shellextension;

import java.awt.Frame;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.izforge.izpack.event.SimpleUninstallerListener;
import com.izforge.izpack.util.AbstractUIProgressHandler;
import com.izforge.izpack.util.os.RegistryDefaultHandler;
import com.izforge.izpack.util.os.RegistryHandler;

/**
 * @author Christopher Deckers
 */
public class DJShellExtensionUninstallerListener extends SimpleUninstallerListener {

  @Override
  public void afterDeletion(List files, AbstractUIProgressHandler handler) throws Exception {
    super.afterDeletion(files, handler);
    if(!isShellExtensionInstalled) {
      return;
    }
    // We postpone to allow the progress to reach 100% before blocking with the modal dialog.
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Frame[] frames = Frame.getFrames();
        if(frames.length > 0) {
          JOptionPane.showMessageDialog(frames[0], "You will need to restart the computer after the uninstallation\nfor some changes to take effect.", "Restart needed", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });
  }
  
  protected boolean isShellExtensionInstalled;
  
  public void beforeDeletion(List files, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    super.beforeDeletion(files, abstractUIProgressHandler);
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
    // beforeDelete is not called by the installer, so let's call it explicitely (renamed to avoid problems in case it actually gets called...)
    for(Object file: files) {
      beforeDelete_((File)file, abstractUIProgressHandler);
    }
  }

  /*@Override*/
  public void beforeDelete_(File file, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
//    super.beforeDelete(file, abstractUIProgressHandler);
    if(!file.getName().equals("DJShellExtension.dll")) {
      return;
    }
    isShellExtensionInstalled = true;
    Process process = new ProcessBuilder("regsvr32.exe", "/u", "/s", file.getAbsolutePath()).start();
    process.waitFor();
    if(!file.delete()) {
      File tempFile = File.createTempFile("~DJ", ".DL_");
      tempFile.delete();
      file.renameTo(tempFile);
    }
    if(process.exitValue() != 0) {
      Frame[] frames = Frame.getFrames();
      if(frames.length > 0) {
        JOptionPane.showMessageDialog(frames[0], "The DJ ShellExtension failed to unregister.\nA possible reason is because of too restrictive access rights.", "Registration failed", JOptionPane.ERROR_MESSAGE);
      }
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
