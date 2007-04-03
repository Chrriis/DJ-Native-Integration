/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * @author Christopher Deckers
 */
public class JarFileInfo {

  protected File sourceFile;
  protected URL fileURL;
  protected boolean isSigned;
  protected String mainClassName;
  protected AttributeInfo[] attributeInfos;
  protected IconInfo[] iconInfos;
  protected String[] imagePaths;
  protected Manifest manifest;
  
  public static final String JAR_ICONS_PATH = "META-INF/JarIcons/";
  protected static final String META_INF_PATH = "META-INF/";
  protected static final String JAR_ICON_PREFIX = "Jar-Icon-";
  
  protected JarFileInfo() {
  }
  
  protected boolean load(File sourceFile) {
    if(!sourceFile.exists()) {
      throw new IllegalArgumentException("The JAR file \"" + sourceFile.getPath() + "\" does not exist!");
    }
    this.sourceFile = sourceFile;
    try {
      JarFile jarFile = new JarFile(sourceFile);
      List<String> imagePathList = new ArrayList<String>();
      fileURL = sourceFile.toURL();
      Enumeration<? extends ZipEntry> entries = jarFile.entries();
      while(entries.hasMoreElements()) {
        String name = entries.nextElement().getName();
        String ucName = name.toUpperCase(Locale.ENGLISH);
        if(!isSigned && ucName.startsWith(META_INF_PATH) && ucName.endsWith(".SF")) {
          isSigned = true;
        } else if(ucName.endsWith(".GIF") || ucName.endsWith(".PNG")) {
          imagePathList.add(name);
        }
      }
      imagePaths = imagePathList.toArray(new String[0]);
      manifest = jarFile.getManifest();
      if(manifest != null) {
        Attributes attributes = manifest.getMainAttributes();
        mainClassName = attributes.getValue(Attributes.Name.MAIN_CLASS);
        List<AttributeInfo> attributeInfoList = new ArrayList<AttributeInfo>();
        List<IconInfo> iconInfoList = new ArrayList<IconInfo>();
        for(Object key: attributes.keySet()) {
          Attributes.Name name = (Attributes.Name)key;
          String s = name.toString();
          if(s.startsWith(JAR_ICON_PREFIX)) {
            String[] sizes = s.substring(JAR_ICON_PREFIX.length()).split("x");
            if(sizes.length == 2) {
              try {
                String imagePath = attributes.getValue(s);
                iconInfoList.add(new IconInfo(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]), imagePath, null));
              } catch(Exception e) {
                e.printStackTrace();
              }
            }
          } else {
            attributeInfoList.add(new AttributeInfo(s, attributes.getValue(s)));
          }
        }
        attributeInfos = attributeInfoList.toArray(new AttributeInfo[0]);
        iconInfos = iconInfoList.toArray(new IconInfo[0]);
      }
      jarFile.close();
      return true;
    } catch(IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static JarFileInfo getJarfileInfo(File jarFile) {
    JarFileInfo jarFileInfo = new JarFileInfo();
    if(jarFileInfo.load(jarFile)) {
      return jarFileInfo;
    }
    return null;
  }

  public String getMainClassName() {
    return mainClassName;
  }
  
  public boolean isSigned() {
    return isSigned;
  }
  
  public AttributeInfo[] getAttributeInfos() {
    return attributeInfos;
  }
  
  public IconInfo[] getIconInfos() {
    return iconInfos;
  }
  
  public String[] getImagePaths() {
    return imagePaths;
  }

  public URL getImageURL(String imagePath) {
    if(fileURL == null) {
      throw new IllegalStateException("The JAR file has no URL location!");
    }
    if(!imagePath.startsWith("/")) {
      imagePath = '/' + imagePath;
    }
    try {
      return new URL("jar:" + fileURL + "!" + imagePath);
    } catch(Exception e) {
      throw new IllegalStateException("The icon has no URL location!", e);
    }
  }
  
  public boolean saveInfos(AttributeInfo[] attributeInfos, IconInfo[] iconInfos, File outFile) {
    boolean isSuccess = false;
    if(manifest == null) {
      manifest = new Manifest();
    }
    Attributes attributes = manifest.getMainAttributes();
    attributes.clear();
    for(AttributeInfo attributeInfo: attributeInfos) {
      attributes.putValue(attributeInfo.getKey(), attributeInfo.getValue());
    }
    List<String> oldExternalIconPathList = new ArrayList<String>();
    List<IconInfo> newExternalIconInfoList = new ArrayList<IconInfo>();
    for(int i=0; i<iconInfos.length; i++) {
      IconInfo iconInfo = iconInfos[i];
      String key = JAR_ICON_PREFIX + iconInfo.getWidth() + "x" + iconInfo.getHeight();
      if(!attributes.containsKey(key)) {
        String path = iconInfo.getPath();
        attributes.putValue(key, path);
        if(iconInfo.getResourceURL() != null) {
          newExternalIconInfoList.add(iconInfo);
        } else if(path.startsWith(JAR_ICONS_PATH)) {
          oldExternalIconPathList.add(path);
        }
      }
    }
    try {
      if(outFile != null && sourceFile.getAbsolutePath().equals(outFile.getAbsolutePath())) {
        outFile = null;
      }
      File outputFile = outFile != null? outFile: File.createTempFile(sourceFile.getName(), null);
      JarOutputStream out = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)), manifest);
      out.setLevel(9);
      JarFile jarFile = new JarFile(sourceFile);
      Enumeration<? extends ZipEntry> entries = jarFile.entries();
      while(entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        String name = entry.getName();
        entry.setCompressedSize(-1);
        if(!name.toLowerCase(Locale.ENGLISH).equals("meta-inf/manifest.mf") && (!name.startsWith(JAR_ICONS_PATH) || oldExternalIconPathList.contains(name))) {
          out.putNextEntry(entry);
          InputStream in = jarFile.getInputStream(entry);
          byte[] bytes = new byte[1024];
          for(int i; (i=in.read(bytes))>= 0; ) {
            out.write(bytes, 0, i);
          }
          in.close();
          out.closeEntry();
        }
      }
      for(IconInfo iconInfo: newExternalIconInfoList) {
        BufferedInputStream in = new BufferedInputStream(iconInfo.getResourceURL().openStream());
        out.putNextEntry(new ZipEntry(iconInfo.getPath()));
        iconInfo.setResourceURL(null);
        byte[] bytes = new byte[1024];
        for(int i; (i=in.read(bytes))>= 0; ) {
          out.write(bytes, 0, i);
        }
        in.close();
        out.closeEntry();
      }
      out.close();
      jarFile.close();
      if(outFile == null) {
        sourceFile.delete();
        if(!outputFile.renameTo(sourceFile)) {
          outputFile.delete();
        } else {
          isSuccess = true;
        }
      } else {
        isSuccess = true;
      }
      load(sourceFile);
    } catch(Exception e) {
//      e.printStackTrace();
    }
    return isSuccess;
  }
  
  public File getSourceFile() {
    return sourceFile;
  }
  
}
