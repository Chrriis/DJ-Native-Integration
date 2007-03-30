/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.shellextension;

import java.io.File;

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
public class DJShellExtensionInstallerListener extends SimpleInstallerListener {

  protected String installPath;
  protected String winDirPath;
  
  @Override
  public void beforePacks(AutomatedInstallData automatedInstallData, Integer packCount, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    VariableSubstitutor substitutor = new VariableSubstitutor(automatedInstallData.getVariables());
    installPath = substitutor.substitute("$INSTALL_PATH", null);
    winDirPath = substitutor.substitute("${ENV[windir]}", null);
  }

  @Override
  public void afterPack(Pack pack, Integer integer, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    if(!"DJ ShellExtension".equals(pack.name)) {
      return;
    }
    RegistryHandler rh = RegistryDefaultHandler.getInstance();
    rh.setRoot(RegistryHandler.HKEY_CLASSES_ROOT);
    rh.setValue("jarfile\\DefaultIcon", "", "%1");
    rh.setValue("jarfile\\ShellEx\\IconHandler", "", "{560F06E8-E0EF-4DD6-9733-5C2507132C57}");
    String keyPath = "jarfile\\ShellEx\\PropertySheetHandlers\\{5EC050B4-7064-44B9-8D71-EC1A33DFB604}";
    if(!rh.keyExist(keyPath)) {
      rh.createKey(keyPath);
    }
    Process process = new ProcessBuilder("regsvr32.exe", "/s", new File(installPath).getAbsolutePath() + "\\DJ ShellExtension\\DJShellExtension.dll").start();
    process.waitFor();
  }

  public static void main(String[] args) {
    try {
      AutomatedInstallData automatedInstallData = new AutomatedInstallData();
      automatedInstallData.setVariable("INSTALL_PATH", "C:\\Program Files\\DJ Project");
      DJShellExtensionInstallerListener shellExtensionInstallerListener = new DJShellExtensionInstallerListener();
      shellExtensionInstallerListener.beforePacks(automatedInstallData, 0, null);
      shellExtensionInstallerListener.afterPack(new Pack("DJ ShellExtension", null, null, null, null, false, false, false, null), 0, null);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
