/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.installer.uninstall;

import java.awt.Frame;
import java.io.File;

import javax.swing.JOptionPane;

import com.coi.tools.os.win.RegDataContainer;
import com.izforge.izpack.event.SimpleInstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.util.AbstractUIProgressHandler;
import com.izforge.izpack.util.VariableSubstitutor;
import com.izforge.izpack.util.os.RegistryDefaultHandler;
import com.izforge.izpack.util.os.RegistryHandler;

/**
 * @author Christopher Deckers
 */
public class UninstallInstallerListener extends SimpleInstallerListener {

  @Override
  public void beforePacks(AutomatedInstallData automatedInstallData, Integer packCount, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    super.beforePacks(automatedInstallData, packCount, abstractUIProgressHandler);
    try {
      uninstallDJShellExtension();
    } catch(Exception e) {
      e.printStackTrace();
    }
    VariableSubstitutor substitutor = new VariableSubstitutor(automatedInstallData.getVariables());
    String appName = substitutor.substitute("$APP_NAME", null);
    try {
      uninstallPreviousVersion(appName);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  protected void uninstallDJShellExtension() throws Exception {
    boolean isFound = uninstallDJShellExtension("{20359C1D-DDBD-4703-A3A9-4622E172112A}", true);
    uninstallDJShellExtension("{80780CF8-106E-417C-8105-D2F1225924EC}", !isFound);
  }
  
  protected boolean uninstallDJShellExtension(String clsID, boolean showMessage) throws Exception {
    // This is the first release of the shell extension, which was shipped using an MSI.
    RegistryHandler rh = RegistryDefaultHandler.getInstance();
    rh.setRoot(RegistryHandler.HKEY_LOCAL_MACHINE);
    String keyPath = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + clsID;
    if(!rh.keyExist(keyPath)) {
      return false;
    }
    if(showMessage) {
      Frame[] frames = Frame.getFrames();
      if(frames.length > 0) {
        JOptionPane.showMessageDialog(frames[0], "The old version \"DJ ShellExtension\" was found on your computer and needs to be removed.\nThis may take a few minutes during which the installer may seem to hang, so please be patient.", "\"DJ ShellExtension\" detected", JOptionPane.INFORMATION_MESSAGE);
      }
    }
    ProcessBuilder processBuilder = new ProcessBuilder("MsiExec.exe", "/norestart", "/q", "/x" + clsID);
    Process process = processBuilder.start();
    process.waitFor();
    return true;
  }
  
  protected void uninstallPreviousVersion(String appName) throws Exception {
    RegistryHandler rh = RegistryDefaultHandler.getInstance();
    rh.setRoot(RegistryHandler.HKEY_LOCAL_MACHINE);
    String keyPath = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + appName;
    if(!rh.keyExist(keyPath)) {
      return;
    }
    RegDataContainer value = rh.getValue(keyPath, "UninstallString", null);
    if(value != null) {
      String uninstallString = value.getStringData();
      if(value != null) {
        String uninstallerFilePath = uninstallString.substring(uninstallString.indexOf(" -jar ") + " -jar ".length()).trim();
        if(uninstallerFilePath.startsWith("\"")) {
          uninstallerFilePath = uninstallerFilePath.substring(1);
        }
        if(uninstallerFilePath.endsWith("\"")) {
          uninstallerFilePath = uninstallerFilePath.substring(0, uninstallerFilePath.length() - 1);
        }
        int appPath = uninstallerFilePath.indexOf("\\Uninstaller\\uninstaller.jar");
        File appDir = new File(uninstallerFilePath.substring(0, appPath));
        if(new File(uninstallerFilePath).exists()) {
          ProcessBuilder processBuilder = new ProcessBuilder(System.getProperty("java.home") + "\\bin\\javaw.exe", "-jar", uninstallerFilePath, "-c", "-f");
          Process process = processBuilder.start();
          process.waitFor();
          for(int i=0; i<100; i++) {
            if(!appDir.exists()) {
              break;
            }
            // Old first version
            File shellExtensionFile = new File(appDir, "DJShellExtension");
            shellExtensionFile.delete();
            // Current version
            new File(appDir, "Uninstaller").delete();
            new File(appDir, "DJ ShellExtension").delete();
            new File(appDir, "DJ Tweak").delete();
            new File(appDir, "DJ JarExe").delete();
            appDir.delete();
            if(!appDir.exists()) {
              break;
            }
            File[] files = appDir.listFiles();
            if(files.length == 0 || files.length == 1 && files[0].equals(shellExtensionFile)) {
              break;
            }
            try {
              Thread.sleep(100);
            } catch(Exception e) {
            }
          }
        }
      }
    }
  }
  
}
