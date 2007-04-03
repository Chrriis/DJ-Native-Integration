/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import chrriis.dj.data.IconInfo;
import chrriis.dj.data.JarFileInfo;

/**
 * @author Christopher Deckers
 */
public class DJTask extends Task {

  protected File fromFile;
  protected File toFile;
  
  public void setFile(File fromFile) {
    this.fromFile = fromFile;
  }
  
  public void setTofile(File toFile) {
    this.toFile = toFile;
  }

  protected Icons icons;
  
  public void addIcons(Icons icons) {
    this.icons = icons;
  }
  
  public void execute() throws BuildException {
    if(fromFile == null) {
      throw new BuildException("The \"file\" attribute is mandatory!");
    }
    if(!fromFile.exists()) {
      throw new BuildException("The source file must exist!");
    }
    if(toFile == null) {
      throw new BuildException("The \"tofile\" attribute is mandatory!");
    }
    if(toFile.exists()) {
      throw new BuildException("The target file must not exist!");
    }
    System.out.println("Loading jar: " + fromFile.getPath());
    JarFileInfo jarfileInfo = JarFileInfo.getJarfileInfo(fromFile);
    IconInfo[] iconInfos = jarfileInfo.getIconInfos();
    if(icons != null) {
      try {
        iconInfos = icons.getIconInfo(getProject(), jarfileInfo);
      } catch(Exception e) {
        throw new BuildException(e);
      }
    }
    String toFilePath = toFile.getPath();
    System.out.println("Saving jar: " + toFilePath);
    if(!jarfileInfo.saveInfos(jarfileInfo.getAttributeInfos(), iconInfos, toFile)) {
      throw new BuildException("The information could not be written to the file \"" + toFilePath + "\". The path may be invalid, or you may not have the necessary permissions.");
    }
  }
  
}
