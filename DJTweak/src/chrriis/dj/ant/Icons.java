/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.ant;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.selectors.SelectorUtils;

import chrriis.dj.data.DataUtil;
import chrriis.dj.data.IconInfo;
import chrriis.dj.data.JarFileInfo;
import chrriis.dj.ui.UIUtil;

/**
 * @author Christopher Deckers
 */
public class Icons {

  protected String images;
  
  public void setImages(String images) {
    this.images = images;
  }
  
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
          System.out.println("Adding internal icon: " + entryName);
          if(!entryName.startsWith("/")) {
            entryName = "/" + entryName;
          }
          Dimension size = DataUtil.getImageSize(jarfileInfo.getImageURL(entryName));
          iconInfoList.add(new IconInfo(size.width, size.height, entryName, null));
        }
      }
    }
//    if(images != null) {
//      String[] imagePaths = images.split("[:;]");
//      for(String imagePath: imagePaths) {
//        if(!imagePath.startsWith("/")) {
//          imagePath = "/" + imagePath;
//        }
//        try {
//          System.out.println("Adding internal icon: " + imagePath);
//          Dimension size = DataUtil.getImageSize(jarfileInfo.getImageURL(imagePath));
//          iconInfoList.add(new IconInfo(size.width, size.height, imagePath, null));
//        } catch(Exception e) {
////          e.printStackTrace();
//          System.err.println("Could not get the image information for \"" + imagePath + "\"");
//        }
//      }
//    }
    for(FileSet fileSet: fileSetList) {
      DirectoryScanner ds = fileSet.getDirectoryScanner(project);
      File dir = ds.getBasedir();
      String[] fileNames = ds.getIncludedFiles();
      for(String fileName: fileNames) {
        File file = new File(dir, fileName);
        System.out.println("Adding external icon: " + file.getPath());
        try {
          URL fileURL = file.toURL();
          Dimension size = DataUtil.getImageSize(fileURL);
          iconInfoList.add(new IconInfo(size.width, size.height, UIUtil.JAR_ICONS_PATH + file.getName(), fileURL));
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
