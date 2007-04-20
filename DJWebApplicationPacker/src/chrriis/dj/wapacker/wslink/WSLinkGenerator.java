/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.wapacker.wslink;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author Christopher Deckers
 */
public class WSLinkGenerator {

  protected static final String JAR_ICONS_PATH = "META-INF/JarIcons/";
  protected static final String JAR_ICON_HEADER_PREFIX = "Jar-Icon-";
  public static final String WS_URL_ATTRIBUTE = "WS-URL";

  public File generateWSLink(URL jnlpURL, File dir) throws Exception {
    InputStream in = null;
    try {
      if(jnlpURL == null) {
        throw new IllegalStateException("Could not access the JNLP file!");
      }
      in = new BufferedInputStream(jnlpURL.openStream());
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new ErrorHandler() {
        public void error(SAXParseException exception) throws SAXException {
        }
        public void fatalError(SAXParseException exception) throws SAXException {
        }
        public void warning(SAXParseException exception) throws SAXException {
        }
      });
      Document document = builder.parse(in);
      Node jnlpNode = document.getElementsByTagName("jnlp").item(0);
      NamedNodeMap attributes = jnlpNode.getAttributes();
      String href = attributes.getNamedItem("href").getNodeValue();
      String codeBase = attributes.getNamedItem("codebase").getNodeValue();
      if(!codeBase.endsWith("/")) {
        codeBase += "/";
      }
      URL url;
      try {
        url = new URL(href);
      } catch(Exception e) {
        url = new URL(codeBase + href);
      }
      jnlpPath = url.toExternalForm().substring(codeBase.length());
      List<String> imageHrefList = new ArrayList<String>();
      List<String> shortcutImageHrefList = new ArrayList<String>();
      NodeList childNodes = jnlpNode.getChildNodes();
      for(int i=0; i<childNodes.getLength(); i++) {
        Node node = childNodes.item(i);
        String nodeName = node.getNodeName();
        if("information".equals(nodeName)) {
          NodeList informationChildNodes = node.getChildNodes();
          for(int j=0; j<informationChildNodes.getLength(); j++) {
            Node informationChildNode = informationChildNodes.item(j);
            String infoNodeName = informationChildNode.getNodeName();
            if("title".equals(infoNodeName)) {
              applicationName = purifyXMLContent(informationChildNode.getTextContent());
            } else if("vendor".equals(infoNodeName)) {
              vendor = purifyXMLContent(informationChildNode.getTextContent());
            } else if("homepage".equals(infoNodeName)) {
              try {
                websiteURL = new URL(informationChildNode.getAttributes().getNamedItem("href").getNodeValue());
              } catch(Exception e) {
                e.printStackTrace();
              }
            } else if("description".equals(infoNodeName)) {
              // TODO: if multiple descriptions, should use the "kind" argument to decide which one to take...
              description = purifyXMLContent(informationChildNode.getTextContent());
            } else if("icon".equals(infoNodeName)) {
              NamedNodeMap iconAttributes = informationChildNode.getAttributes();
              Node kindItem = iconAttributes.getNamedItem("kind");
              String iconKind = kindItem == null? "default": kindItem.getNodeValue();
              String iconHref = iconAttributes.getNamedItem("href").getNodeValue();
              if(!"splash".equals(iconKind)) {
                if("shortcut".equals(iconKind)) {
                  shortcutImageHrefList.add(iconHref);
                } else {
                  imageHrefList.add(iconHref);
                }
              }
            }
          }
        } else if("application-desc".equals(nodeName)) {
          isApplication = true;
        }
      }
      if(applicationName != null) {
        List<BufferedImage> imageList = new ArrayList<BufferedImage>();
        @SuppressWarnings("unchecked")
        List<String>[] imageHrefLists = new List[] {shortcutImageHrefList, imageHrefList};
        for(List<String> hrefList: imageHrefLists) {
          for(String imageHref: hrefList) {
            try {
              URL imageURL;
              try {
                imageURL = new URL(imageHref);
              } catch(Exception e) {
                imageURL = new URL(codeBase + imageHref);
              }
              BufferedImage image = ImageIO.read(imageURL);
              boolean isAdding = true;
              for(BufferedImage img: imageList) {
                if(img.getWidth() == image.getWidth() && img.getHeight() == image.getHeight()) {
                  isAdding = false;
                  break;
                }
              }
              if(isAdding) {
                imageList.add(image);
              }
            } catch(Exception e) {
              e.printStackTrace();
            }
          }
          if(!imageList.isEmpty()) {
            break;
          }
        }
        return writeJar(dir, url.toExternalForm(), imageList.toArray(new BufferedImage[0]));
      }
    } finally {
      if(in != null) {
        try {
          in.close();
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }
  
  protected File writeJar(File dir, String urlSpec, BufferedImage[] images) {
    applicationNameFS = applicationName.replaceAll("[\\/:*?\"<>|]", "-");
    Manifest manifest = new Manifest();
    Attributes mainAttributes = manifest.getMainAttributes();
    mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
    mainAttributes.put(Attributes.Name.MAIN_CLASS, WSLink.class.getName());
    mainAttributes.putValue(WS_URL_ATTRIBUTE, urlSpec);
    if(images.length == 0) {
      try {
        images = new BufferedImage[] {
            ImageIO.read(getClass().getResourceAsStream("resource/WebApplicationIcon16x16.png")),
            ImageIO.read(getClass().getResourceAsStream("resource/WebApplicationIcon24x24.png")),
            ImageIO.read(getClass().getResourceAsStream("resource/WebApplicationIcon32x32.png")),
            ImageIO.read(getClass().getResourceAsStream("resource/WebApplicationIcon48x48.png")),
            ImageIO.read(getClass().getResourceAsStream("resource/WebApplicationIcon256x256.png")),
        };
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    for(int i=0; i<images.length; i++) {
      BufferedImage image = images[i];
      mainAttributes.putValue(JAR_ICON_HEADER_PREFIX + image.getWidth() + "x" + image.getHeight(), JAR_ICONS_PATH + "icon" + i + ".png");
    }
    dir.mkdirs();
    File jarFile = null;
    try {
      jarFile = new File(dir, applicationNameFS + ".jar");
      JarOutputStream out = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jarFile)), manifest);
      out.setLevel(9);
      String linkClassPath = WSLink.class.getName().replace('.', '/') + ".class";
      out.putNextEntry(new ZipEntry(linkClassPath));
      InputStream in = getClass().getResourceAsStream('/' + linkClassPath);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] bytes = new byte[1024];
      for(int i; (i=in.read(bytes)) >= 0;) {
        baos.write(bytes, 0, i);
      }
      in.close();
      out.write(baos.toByteArray());
      out.closeEntry();
      for(int i=0; i<images.length; i++) {
        out.putNextEntry(new ZipEntry(JAR_ICONS_PATH + "icon" + i + ".png"));
        ImageIO.write(images[i], "png", out);
        out.closeEntry();
      }
      out.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
    return jarFile;
  }

  protected String purifyXMLContent(String content) {
    StringBuilder sb = new StringBuilder();
    char lastChar = 0;
    for(int i=0; i<content.length(); i++) {
      char c = content.charAt(i);
      boolean isValid = true;
      if(Character.isWhitespace(c)) {
        if(lastChar != ' ') {
          c = ' ';
        } else {
          isValid = false;
        }
      } else {
        lastChar = c;
      }
      if(isValid) {
        sb.append(c);
      }
    }
    content = sb.toString().trim();
    if(content.length() == 0) {
      return null;
    }
    return content;
  }
  
  protected boolean isApplication;
  
  public boolean isApplication() {
    return isApplication;
  }
  
  protected String description;
  
  public String getDescription() {
    return description;
  }
  
  protected URL websiteURL;
  
  public URL getWebsiteURL() {
    return websiteURL;
  }
  
  protected String applicationName;
  
  public String getApplicationName() {
    return applicationName;
  }
  
  protected String applicationNameFS;
  
  public String getApplicationNameFS() {
    return applicationNameFS;
  }
  
  protected String vendor;
  
  public String getVendor() {
    return vendor;
  }
  
  protected String jnlpPath;
  
  /**
   * @return a relative path to the codebase
   */
  public String getJnlpPath() {
    return jnlpPath;
  }
  
}
