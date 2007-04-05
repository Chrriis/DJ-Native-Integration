/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.ant;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;

import chrriis.dj.tweak.data.JarFileInfo;
import chrriis.dj.tweak.data.VMArgumentsInfo;

/**
 * @author Christopher Deckers
 */
public class VMArguments {

  public static class Pattern {

    protected String version = "";
    
    public void setVersion(String version) {
      this.version = version;
    }
    
    protected String vendor = "";
    
    public void setVendor(String vendor) {
      this.vendor = vendor;
    }
    
    protected String arguments;
    
    public void setArguments(String arguments) {
      this.arguments = arguments;
    }
    
  }
  
  protected List<Pattern> patternList = new ArrayList<Pattern>();

  public void addPattern(Pattern pattern) {
      patternList.add(pattern);
  }

  public VMArgumentsInfo[] getVMArgumentsInfo(Project project, JarFileInfo jarfileInfo) {
    VMArgumentsInfo[] vmArgumentsInfos = new VMArgumentsInfo[patternList.size()];
    for(int i=0; i<vmArgumentsInfos.length; i++) {
      Pattern pattern = patternList.get(i);
      System.out.println("Adding VM arguments: vendor=\"" + pattern.vendor + "\", version=\"" + pattern.version + "\", arguments=\"" + pattern.arguments + '"');
      vmArgumentsInfos[i] = new VMArgumentsInfo(pattern.vendor, pattern.version, pattern.arguments);
    }
    return vmArgumentsInfos;
  }
  
}
