/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.ant;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.selectors.SelectorUtils;

import chrriis.dj.tweak.data.DataUtil;
import chrriis.dj.tweak.data.IconInfo;
import chrriis.dj.tweak.data.JarFileInfo;

/**
 * @author Christopher Deckers
 */
public class Icons {

  protected List<PatternSet> patternSetList = new ArrayList<PatternSet>();

  public void addInternalset(PatternSet patternSet) {
      patternSetList.add(patternSet);
  }

  protected List<FileSet> fileSetList = new ArrayList<FileSet>();
  
  public void addExternalset(FileSet fileSet) {
    if(!fileSetList.contains(fileSet)) {
      fileSetList.add(fileSet);
    }
  }

  public IconInfo[] getIconInfo(Project project, JarFileInfo jarfileInfo) throws IOException {
    List<IconInfo> iconInfoList = new ArrayList<IconInfo>();
    if(patternSetList != null && !patternSetList.isEmpty()) {
      for(Enumeration<JarEntry> en=new JarFile(jarfileInfo.getSourceFile()).entries(); en.hasMoreElements(); ) {
        String entryName = en.nextElement().getName();
        if(isIncluded(project, entryName)) {
          System.out.println("Adding internal image: " + entryName);
          if(entryName.startsWith("/")) {
            entryName = entryName.substring(1);
          }
          Dimension size = DataUtil.getImageSize(jarfileInfo.getImageURL(entryName));
          for(IconInfo iconInfo: iconInfoList) {
            if(iconInfo.getWidth() == size.width && iconInfo.getHeight() == size.height) {
              throw new BuildException("The image \"" + entryName + "\" has the same size as \"" + iconInfo.getPath() + "\"");
            }
          }
          if(size.width != size.height) {
            System.out.println("Warning: the image \"" + entryName + "\" has a width that does not equal its height, which may be ignored by the icon extension.");
          }
          iconInfoList.add(new IconInfo(size.width, size.height, entryName, null));
        }
      }
    }
    for(FileSet fileSet: fileSetList) {
      DirectoryScanner ds = fileSet.getDirectoryScanner(project);
      File dir = ds.getBasedir();
      String[] fileNames = ds.getIncludedFiles();
      for(String fileName: fileNames) {
        File file = new File(dir, fileName);
        String filePath = file.getPath();
        System.out.println("Adding external image: " + filePath);
        try {
          URL fileURL = file.toURL();
          Dimension size = DataUtil.getImageSize(fileURL);
          for(IconInfo iconInfo: iconInfoList) {
            if(iconInfo.getWidth() == size.width && iconInfo.getHeight() == size.height) {
              throw new BuildException("The image \"" + filePath + "\" has the same size as \"" + iconInfo.getPath() + "\"");
            }
          }
          if(size.width != size.height) {
            System.out.println("Warning: the image \"" + filePath + "\" has a width that does not equal its height, which may be ignored by the icon extension.");
          }
          iconInfoList.add(new IconInfo(size.width, size.height, JarFileInfo.JAR_ICONS_PATH + file.getName(), fileURL));
        } catch(Exception e) {
//          e.printStackTrace();
          System.err.println("Could not get the image information for \"" + fileName + "\"");
        }
      }
    }
    return iconInfoList.toArray(new IconInfo[0]);
  }
  
  protected boolean isIncluded(Project project, String entryName) {
    if(patternSetList == null || patternSetList.isEmpty()) {
      return false;
    }
    boolean isIncluded = false;
    for(PatternSet patternSet: patternSetList) {
      String[] includePatterns = patternSet.getIncludePatterns(project);
      if(includePatterns == null || includePatterns.length == 0) {
        includePatterns = new String[] {"**"};
      }
      for(String includePattern: includePatterns) {
        if(includePattern.endsWith("/")) {
          includePattern += "**";
        }
        isIncluded = SelectorUtils.matchPath(includePattern, entryName);
        if(isIncluded) {
          break;
        }
      }
      if(!isIncluded) {
        return false;
      }
      String[] excludePatterns = patternSet.getExcludePatterns(project);
      if(excludePatterns != null) {
        for(String excludePattern: excludePatterns) {
          if (excludePattern.endsWith("/")) {
            excludePattern += "**";
          }
          isIncluded = !SelectorUtils.matchPath(excludePattern, entryName);
          if(!isIncluded) {
            return false;
          }
        }
      }
    }
    return isIncluded;
  }

  
}
