/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.installer.jarexe;

import java.io.File;

import com.coi.tools.os.win.RegDataContainer;
import com.izforge.izpack.Pack;
import com.izforge.izpack.event.SimpleInstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.util.AbstractUIProgressHandler;
import com.izforge.izpack.util.VariableSubstitutor;
import com.izforge.izpack.util.os.RegistryDefaultHandler;
import com.izforge.izpack.util.os.RegistryHandler;

/**
 * @author Christopher Deckers
 */
public class DJJarExeInstallerListener extends SimpleInstallerListener {

  @Override
  public void afterPack(Pack pack, Integer packNumber, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    super.afterPack(pack, packNumber, abstractUIProgressHandler);
    if(!"DJ JarExe".equals(pack.name)) {
      return;
    }
    RegistryHandler rh = RegistryDefaultHandler.getInstance();
    rh.setRoot(RegistryHandler.HKEY_CLASSES_ROOT);
    String keyPath = "jarfile\\shell\\open\\command";
    String key = "";
    RegDataContainer value = rh.getValue(keyPath, key, null);
    if(value != null) {
      String stringData = value.getStringData();
      if(stringData != null) {
        int index1 = stringData.indexOf(" -jar ");
        int index2 = stringData.indexOf(" \"%1\"", Math.max(index1 - 1, 0));
        if(index1 != -1 && index2 != -1) {
          VariableSubstitutor substitutor = new VariableSubstitutor(getInstalldata().getVariables());
          String installPath = substitutor.substitute("$INSTALL_PATH", null);
          rh.setValue(keyPath, key, stringData.substring(0, index1 + " -jar ".length()) + "\"" + new File(installPath).getAbsolutePath() + "\\DJ JarExe\\DJJarExe.jar\"" + stringData.substring(index2));
          // Open in Shell command
          String oisKeyPath = "jarfile\\shell\\openinshell";
          String oiscKeyPath = "jarfile\\shell\\openinshell\\command";
          if(!rh.keyExist(oiscKeyPath)) {
            rh.createKey(oiscKeyPath);
          }
          rh.setValue(oiscKeyPath, key, stringData.substring(0, index1) + " -Ddj.jarexe.console=true -jar \"" + new File(installPath).getAbsolutePath() + "\\DJ JarExe\\DJJarExe.jar\"" + stringData.substring(index2));
          rh.setValue(oisKeyPath, "", "Open in Shell");
        }
      }
    }
  }

  public static void main(String[] args) {
    try {
      AutomatedInstallData automatedInstallData = new AutomatedInstallData();
      automatedInstallData.setVariable("INSTALL_PATH", "C:\\Program Files\\DJ Project");
      DJJarExeInstallerListener jarExeInstallerListener = new DJJarExeInstallerListener();
      jarExeInstallerListener.beforePacks(automatedInstallData, 0, null);
      jarExeInstallerListener.afterPack(new Pack("DJ JarExe", null, null, null, null, false, false, false, null), 0, null);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
