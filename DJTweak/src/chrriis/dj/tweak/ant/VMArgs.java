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
import chrriis.dj.tweak.data.VMArgsInfo;

/**
 * @author Christopher Deckers
 */
public class VMArgs {

  public static class Pattern {

    protected String version = "";
    
    public void setVersion(String version) {
      this.version = version;
    }
    
    protected String vendor = "";
    
    public void setVendor(String vendor) {
      this.vendor = vendor;
    }
    
    protected String args;
    
    public void setArgs(String args) {
      this.args = args;
    }
    
  }
  
  protected List<Pattern> patternList = new ArrayList<Pattern>();

  public void addPattern(Pattern pattern) {
      patternList.add(pattern);
  }

  public VMArgsInfo[] getVMArgsInfo(Project project, JarFileInfo jarfileInfo) {
    VMArgsInfo[] vmArgsInfos = new VMArgsInfo[patternList.size()];
    for(int i=0; i<vmArgsInfos.length; i++) {
      Pattern pattern = patternList.get(i);
      System.out.println("Adding VM arguments: vendor=\"" + pattern.vendor + "\", version=\"" + pattern.version + "\", args=\"" + pattern.args + '"');
      vmArgsInfos[i] = new VMArgsInfo(pattern.vendor, pattern.version, pattern.args);
    }
    return vmArgsInfos;
  }
  
}
