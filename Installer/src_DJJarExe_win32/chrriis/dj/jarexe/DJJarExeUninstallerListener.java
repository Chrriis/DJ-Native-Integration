/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.jarexe;

import java.util.List;

import com.coi.tools.os.win.RegDataContainer;
import com.izforge.izpack.event.SimpleUninstallerListener;
import com.izforge.izpack.util.AbstractUIProgressHandler;
import com.izforge.izpack.util.os.RegistryDefaultHandler;
import com.izforge.izpack.util.os.RegistryHandler;

/**
 * @author Christopher Deckers
 */
public class DJJarExeUninstallerListener extends SimpleUninstallerListener {

  public void beforeDeletion(List files, AbstractUIProgressHandler abstractUIProgressHandler) throws Exception {
    super.beforeDeletion(files, abstractUIProgressHandler);
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
          rh.setValue(keyPath, key, stringData.substring(0, index1 + " -jar".length()) + stringData.substring(index2));
        }
      }
    }
  }

  public static void main(String[] args) {
    try {
      new DJJarExeUninstallerListener().beforeDeletion(null, null);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
