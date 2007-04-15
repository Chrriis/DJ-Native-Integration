/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
  protected VMArgsInfo[] vmArgsInfos;
  protected String[] imagePaths;
  protected Manifest manifest;
  
  public static final String JAR_ICONS_PATH = "META-INF/JarIcons/";
  protected static final String META_INF_PATH = "META-INF/";
  protected static final String JAR_ICON_HEADER_PREFIX = "Jar-Icon-";
  protected static final String VM_ARGS_HEADER = "VM-Args";
  
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
      fileURL = sourceFile.toURI().toURL();
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
        List<VMArgsInfo> vmArgsInfoList = new ArrayList<VMArgsInfo>();
        List<IconInfo> iconInfoList = new ArrayList<IconInfo>();
        for(Object key: attributes.keySet()) {
          Attributes.Name name = (Attributes.Name)key;
          String s = name.toString();
          if(s.equals(VM_ARGS_HEADER)) {
            String value = attributes.getValue(s).trim();
            if(value.length() > 0) {
              if(!value.startsWith("<")) {
                vmArgsInfoList.add(new VMArgsInfo("", "", value));
              } else {
                try {
                  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                  DocumentBuilder builder = factory.newDocumentBuilder();
                  Document document = builder.parse(new ByteArrayInputStream(value.getBytes("UTF-8")));
                  NodeList childNodes = document.getElementsByTagName("vmargs").item(0).getChildNodes();
                  for(int i=0; i<childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if("pattern".equals(node.getNodeName())) {
                      NamedNodeMap argsAttributes = node.getAttributes();
                      Node item = argsAttributes.getNamedItem("vendor");
                      String vendor = item == null? "": item.getNodeValue();
                      item = argsAttributes.getNamedItem("version");
                      String version = item == null? "": item.getNodeValue();
                      item = argsAttributes.getNamedItem("args");
                      if(item != null) {
                        String args = item.getNodeValue();
                        if(args.length() > 0) {
                          vmArgsInfoList.add(new VMArgsInfo(vendor, version, args));
                        }
                      }
                    }
                  }
                } catch(Exception e) {
                  e.printStackTrace();
                }
            }
            }
          } else if(s.startsWith(JAR_ICON_HEADER_PREFIX)) {
            String[] sizes = s.substring(JAR_ICON_HEADER_PREFIX.length()).split("x");
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
        vmArgsInfos = vmArgsInfoList.toArray(new VMArgsInfo[0]);
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
  
  public VMArgsInfo[] getVMArgsInfos() {
    return vmArgsInfos;
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
  
  public boolean saveInfos(AttributeInfo[] attributeInfos, IconInfo[] iconInfos, VMArgsInfo[] vmArgsInfos, File outFile) {
    boolean isSuccess = false;
    if(manifest == null) {
      manifest = new Manifest();
      manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    }
    try {
      Attributes attributes = manifest.getMainAttributes();
      attributes.clear();
      for(AttributeInfo attributeInfo: attributeInfos) {
        attributes.putValue(attributeInfo.getKey(), attributeInfo.getValue());
      }
      List<String> oldExternalIconPathList = new ArrayList<String>();
      List<IconInfo> newExternalIconInfoList = new ArrayList<IconInfo>();
      for(IconInfo iconInfo: iconInfos) {
        String key = JAR_ICON_HEADER_PREFIX + iconInfo.getWidth() + "x" + iconInfo.getHeight();
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
      if(vmArgsInfos.length > 0) {
        if(vmArgsInfos.length == 1 && vmArgsInfos[0].getVendor().length() == 0 && vmArgsInfos[0].getVersion().length() == 0) {
          String args = vmArgsInfos[0].getArgs();
          if(args.length() > 0) {
            attributes.putValue(VM_ARGS_HEADER, args);
          }
        } else {
          StringBuilder sb = new StringBuilder();
          sb.append("<vmargs>");
          for(VMArgsInfo vmArgsInfo: vmArgsInfos) {
            String args = vmArgsInfo.getArgs();
            if(args.length() > 0) {
              String vendor = vmArgsInfo.getVendor();
              String version = vmArgsInfo.getVersion();
              sb.append("<pattern ");
              if(vendor.length() > 0) {
                sb.append("vendor=\"").append(DataUtil.escapeXML(vendor)).append("\" ");
              }
              if(version.length() > 0) {
                sb.append("version=\"").append(DataUtil.escapeXML(version)).append("\" ");
              }
              sb.append("args=\"").append(DataUtil.escapeXML(args)).append("\"/>");
            }
          }
          sb.append("</vmargs>");
          attributes.putValue(VM_ARGS_HEADER, sb.toString());
        }
      }
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
        if(!name.equals(JarFile.MANIFEST_NAME) && (!name.startsWith(JAR_ICONS_PATH) || oldExternalIconPathList.contains(name))) {
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
