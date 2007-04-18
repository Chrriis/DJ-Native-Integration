/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.wapacker.installws;

import java.io.File;

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
public class InstallWSInstallerListener extends SimpleInstallerListener {

  @Override
  public void afterPacks(AutomatedInstallData automatedInstallData, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    super.afterPacks(automatedInstallData, abstractUIProgressHandler);
    if(System.getProperty("java.version").compareTo("1.5") < 0) {
      return;
    }
    VariableSubstitutor substitutor = new VariableSubstitutor(automatedInstallData.getVariables());
    String jnlpLocalPath = substitutor.substitute("$JNLP_LOCAL_PATH", null);
    String installPath = substitutor.substitute("$INSTALL_PATH", null);
    File importDir = new File(installPath + "/.djwaimport");
    if(importDir.exists()) {
      ProcessBuilder processBuilder = new ProcessBuilder("javaws", "-Xnosplash", "-import", "-silent", new File(importDir, jnlpLocalPath).getAbsolutePath());
//      processBuilder.directory(importDir);
      Process process = processBuilder.start();
      process.waitFor();
//      cleanUp(importDir);
    }
    if(System.getProperty("os.name").startsWith("Windows")) {
      String appNameFS = substitutor.substitute("$APP_NAME_FS", null);
      modifyRegistryKey(appNameFS, installPath);
    }
  }
  
  protected void modifyRegistryKey(final String appNameFS, String installPath) {
    final String installPath_ = installPath.replace('/', '\\');
    for(int i=0; i<20; i++) {
      try {
        Thread.sleep(500);
      } catch(Exception e) {
      }
      if(modifyRegistryKey(RegistryHandler.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall", appNameFS, installPath_)) {
        break;
      }
      if(modifyRegistryKey(RegistryHandler.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall", appNameFS, installPath_)) {
        break;
      }
    }
  }
  
  protected boolean modifyRegistryKey(int root, String keyPath, String appNameFS, String installPath) {
    RegistryHandler rh = RegistryDefaultHandler.getInstance();
    try {
      rh.setRoot(root);
      if(rh.keyExist(keyPath)) {
        for(String subKeyPath: rh.getSubkeys(keyPath)) {
          subKeyPath = keyPath + '\\' + subKeyPath;
          RegDataContainer value = rh.getValue(subKeyPath, "DisplayName", null);
          if(value != null && appNameFS.equals(value.getStringData())) {
            value = rh.getValue(subKeyPath, "UninstallString", null);
            if(value != null) {
              String uninstallCommand = value.getStringData();
              if(uninstallCommand != null) {
                int index = uninstallCommand.indexOf("javaws.exe");
                if(index >= 0) {
                  int endIndex = index + "javaws.exe".length();
                  uninstallCommand = uninstallCommand.substring(0, index) + "javaw.exe" + uninstallCommand.substring(endIndex, uninstallCommand.indexOf(' ', endIndex)) + " -jar \"" + installPath + "\\Uninstaller\\uninstaller.jar\"";
                  rh.setValue(subKeyPath, "UninstallString", uninstallCommand);
                  return true;
                }
              }
            }
          }
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
//  protected void cleanUp(File tmpDir) {
//    if(!tmpDir.delete()) {
//      if(tmpDir.isDirectory()) {
//        for(File file: tmpDir.listFiles()) {
//          cleanUp(file);
//        }
//        tmpDir.delete();
//      }
//    }
//  }

}
