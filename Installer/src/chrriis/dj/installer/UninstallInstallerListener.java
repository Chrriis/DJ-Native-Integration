/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.installer;

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
public class UninstallInstallerListener extends SimpleInstallerListener {

  @Override
  public void beforePacks(AutomatedInstallData automatedInstallData, Integer packCount, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    super.beforePacks(automatedInstallData, packCount, abstractUIProgressHandler);
    RegistryHandler rh = RegistryDefaultHandler.getInstance();
    rh.setRoot(RegistryHandler.HKEY_LOCAL_MACHINE);
    VariableSubstitutor substitutor = new VariableSubstitutor(automatedInstallData.getVariables());
    String appName = substitutor.substitute("$APP_NAME", null);
    String keyPath = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + appName;
    String key = "UninstallString";
    RegDataContainer value = rh.getValue(keyPath, key, null);
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
