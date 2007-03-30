/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.jarexe;

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

  protected String installPath;
  
  @Override
  public void beforePacks(AutomatedInstallData automatedInstallData, Integer packCount, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    VariableSubstitutor substitutor = new VariableSubstitutor(automatedInstallData.getVariables());
    installPath = substitutor.substitute("$INSTALL_PATH", null);
  }

  @Override
  public void afterPack(Pack pack, Integer integer, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
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
          rh.setValue(keyPath, key, stringData.substring(0, index1 + " -jar ".length()) + "\"" + new File(installPath).getAbsolutePath() + "\\DJ JarExe\\DJJarExe.jar\"" + stringData.substring(index2));
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
