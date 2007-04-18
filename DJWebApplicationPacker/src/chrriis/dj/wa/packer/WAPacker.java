package chrriis.dj.wa.packer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import chrriis.dj.wa.packer.installws.InstallWSInstallerListener;
import chrriis.dj.wa.packer.uninstallws.UninstallWSUninstallerListener;
import chrriis.dj.wa.wslink.WSLinkGenerator;

import com.izforge.izpack.compiler.CompilerConfig;

/**
 * @author Christopher Deckers
 */
public class WAPacker {

  protected static final String IZPACK_HOME = "lib/izpack";
  protected static final String CUSTOM_ACTIONS_PATH = IZPACK_HOME + "/bin/customActions";

  public void pack(WAPackerConfiguration waPackerConfiguration) {
    waPackerConfiguration.getOutputJarFile().delete();
    try {
      waPackerConfiguration.getOutputJarFile().createNewFile();
    } catch(Exception e) {
      throw new IllegalStateException("Could not create the output JAR file!");
    }
    waPackerConfiguration.getOutputJarFile().delete();
    String tmpDirPath = System.getProperty("java.io.tmpdir") + "/.djwapacker";
    File tmpDir = new File(tmpDirPath);
    tmpDir.mkdirs();
    File customLangPackXMLFile = null;
    File installerXMLFile = null;
    File shortcutSpecXMLFile = null;
    File wsLinkJarFile = null;
    File installWSInstallerListenerJarFile = null;
    File uninstallWSUninstallerListenerJarFile = null;
    RuntimeException ex = null;
    try {
      customLangPackXMLFile = File.createTempFile("~DJC", ".xml", tmpDir);
      shortcutSpecXMLFile = File.createTempFile("~DJS", ".xml", tmpDir);
      installerXMLFile = File.createTempFile("~DJI", ".xml", tmpDir);
      WSLinkGenerator wsLinkGenerator = new WSLinkGenerator();
      wsLinkJarFile = wsLinkGenerator.generateWSLink(waPackerConfiguration.getJnlpURL(), tmpDir);
      if(wsLinkJarFile == null) {
        throw new IllegalStateException("Failed to create the WSLink Jar file!");
      }
      if(waPackerConfiguration.getApplicationArchiveFile() != null) {
        installWSInstallerListenerJarFile = extractInstallWSInstallerListenerJarFile();
        if(installWSInstallerListenerJarFile == null) {
          throw new IllegalStateException("The custom action to install could not be created!");
        }
      }
      uninstallWSUninstallerListenerJarFile = extractUninstallWSUninstallerListenerJarFile();
      if(uninstallWSUninstallerListenerJarFile == null) {
        throw new IllegalStateException("The custom action to uninstall could not be created!");
      }
      if(!generateCustomLangPackXMLFile(customLangPackXMLFile)) {
        throw new IllegalStateException("Could not generate the Custom Lang Pack XML file content!");
      }
      if(!generateShortcutSpecXMLFile(waPackerConfiguration, wsLinkGenerator, shortcutSpecXMLFile, wsLinkJarFile)) {
        throw new IllegalStateException("Could not generate the Shortcut Spec XML file content!");
      }
      if(!generateInstallerXMLFileContent(waPackerConfiguration, wsLinkGenerator, installerXMLFile, wsLinkJarFile, customLangPackXMLFile, shortcutSpecXMLFile)) {
        throw new IllegalStateException("Could not generate the installer XML file content!");
      }
      CompilerConfig c = new CompilerConfig(installerXMLFile.getAbsolutePath(), ".", CompilerConfig.STANDARD, waPackerConfiguration.getOutputJarFile().getAbsolutePath());
      CompilerConfig.setIzpackHome(IZPACK_HOME);
      c.executeCompiler();
    } catch(RuntimeException e) {
      String message = e.getMessage();
      ex = new RuntimeException("Failed to generate the installer" + (message == null? "!": ": " + message), e);
    } catch(Exception e) {
      String message = e.getMessage();
      ex = new RuntimeException("Failed to generate the installer" + (message == null? "!": ": " + message), e);
    }
    if(customLangPackXMLFile != null) {
      customLangPackXMLFile.delete();
    }
    if(installerXMLFile != null) {
      installerXMLFile.delete();
    }
    if(shortcutSpecXMLFile != null) {
      shortcutSpecXMLFile.delete();
    }
    if(wsLinkJarFile != null) {
      wsLinkJarFile.delete();
    }
    if(installWSInstallerListenerJarFile != null) {
      installWSInstallerListenerJarFile.delete();
    }
    if(uninstallWSUninstallerListenerJarFile != null) {
      uninstallWSUninstallerListenerJarFile.delete();
    }
    cleanUp(tmpDir);
    if(ex != null) {
      throw ex;
    }
  }
  
  protected File extractInstallWSInstallerListenerJarFile() {
    File jarFile = new File(CUSTOM_ACTIONS_PATH + "/InstallWSInstallerListener.jar");
    jarFile.getParentFile().mkdirs();
    try {
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(jarFile)));
      String uninstallerClassPath = InstallWSInstallerListener.class.getName().replace('.', '/') + ".class";
      out.putNextEntry(new ZipEntry(uninstallerClassPath));
      InputStream in = getClass().getResourceAsStream('/' + uninstallerClassPath);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] bytes = new byte[1024];
      for(int i; (i=in.read(bytes)) >= 0;) {
        baos.write(bytes, 0, i);
      }
      in.close();
      out.write(baos.toByteArray());
      out.closeEntry();
      out.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
    if(!jarFile.exists()) {
      return null;
    }
    return jarFile;
  }

  protected File extractUninstallWSUninstallerListenerJarFile() {
    File jarFile = new File(CUSTOM_ACTIONS_PATH + "/UninstallWSUninstallerListener.jar");
    jarFile.getParentFile().mkdirs();
    try {
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(jarFile)));
      String uninstallerClassPath = UninstallWSUninstallerListener.class.getName().replace('.', '/') + ".class";
      out.putNextEntry(new ZipEntry(uninstallerClassPath));
      InputStream in = getClass().getResourceAsStream('/' + uninstallerClassPath);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] bytes = new byte[1024];
      for(int i; (i=in.read(bytes)) >= 0;) {
        baos.write(bytes, 0, i);
      }
      in.close();
      out.write(baos.toByteArray());
      out.closeEntry();
      out.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
    if(!jarFile.exists()) {
      return null;
    }
    return jarFile;
  }
  
  protected boolean generateCustomLangPackXMLFile(File file) {
    try {
      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
      InputStream in = getClass().getResourceAsStream("resource/CustomLangpack_eng.xml");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] bytes = new byte[1024];
      for(int i; (i=in.read(bytes)) >= 0;) {
        baos.write(bytes, 0, i);
      }
      in.close();
      out.write(baos.toByteArray());
      out.close();
      return true;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  protected static boolean generateShortcutSpecXMLFile(WAPackerConfiguration waPackerConfiguration, WSLinkGenerator wsLinkGenerator, File xmlFile, File wsLinkJarFile) {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sb.append("<shortcuts>");
    sb.append("<skipIfNotSupported/>");
    sb.append("<programGroup defaultName=\"$APP_NAME\" location=\"applications\"/>");
    sb.append("<shortcut");
    sb.append(" name=\"$APP_NAME\"");
    sb.append(" programGroup=\"yes\"");
    sb.append(" desktop=\"yes\"");
    sb.append(" applications=\"no\"");
    sb.append(" startMenu=\"no\"");
    sb.append(" startup=\"no\"");
    sb.append(" target=\"$INSTALL_PATH\\").append(escapeXML(wsLinkJarFile.getName())).append("\"");
    sb.append(" commandLine=\"\"");
    String description = wsLinkGenerator.getDescription();
    if(description == null) {
      description = "$APP_NAME";
    }
    sb.append(" description=\"").append(escapeXML(description)).append("\">");
    sb.append("</shortcut>");
    File readmeFile = waPackerConfiguration.getReadmeFile();
    if(readmeFile != null) {
      sb.append("<shortcut");
      sb.append(" name=\"Read Me\"");
      sb.append(" programGroup=\"yes\"");
      sb.append(" desktop=\"no\"");
      sb.append(" applications=\"no\"");
      sb.append(" startMenu=\"no\"");
      sb.append(" startup=\"no\"");
      sb.append(" target=\"$INSTALL_PATH\\").append(escapeXML(readmeFile.getName())).append("\"");
      sb.append(" commandLine=\"\"");
      sb.append(" description=\"Information about $APP_NAME\">");
      sb.append("</shortcut>");
    }
    URL websiteURL = wsLinkGenerator.getWebsiteURL();
    if(websiteURL != null) {
      sb.append("<shortcut");
      sb.append(" name=\"Website\"");
      sb.append(" programGroup=\"yes\"");
      sb.append(" desktop=\"no\"");
      sb.append(" applications=\"no\"");
      sb.append(" startMenu=\"no\"");
      sb.append(" startup=\"no\"");
      sb.append(" target=\"").append(escapeXML(websiteURL.toExternalForm())).append("\"");
      sb.append(" commandLine=\"\"");
      sb.append(" description=\"The home of $APP_NAME\">");
      sb.append("</shortcut>");
    }
    sb.append("<shortcut");
    sb.append(" name=\"Uninstall $APP_NAME\"");
    sb.append(" programGroup=\"yes\"");
    sb.append(" desktop=\"no\"");
    sb.append(" applications=\"no\"");
    sb.append(" startMenu=\"no\"");
    sb.append(" startup=\"no\"");
    sb.append(" target=\"$INSTALL_PATH\\Uninstaller\\uninstaller.jar\"");
    sb.append(" commandLine=\"\"");
    sb.append(" iconFile=\"%SystemRoot%\\system32\\SHELL32.dll\"");
    sb.append(" iconIndex=\"31\"");
    sb.append(" description=\"The uninstaller of $APP_NAME\">");
    sb.append("</shortcut>");
    sb.append("</shortcuts>");
    try {
      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(xmlFile));
      out.write(sb.toString().getBytes("UTF-8"));
      out.close();
      return true;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  protected static boolean generateInstallerXMLFileContent(WAPackerConfiguration waPackerConfiguration, WSLinkGenerator wsLinkGenerator, File xmlFile, File wsLinkJarFile, File customLangPackXMLFile, File shortcutSpecXMLFile) {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sb.append("<installation version=\"1.0\">");
    sb.append("<info>");
    String appName = wsLinkGenerator.getApplicationName();
    sb.append("<appname>").append(escapeXML(appName)).append("</appname>");
    sb.append("<appversion>Web Application</appversion>");
    sb.append("<authors>");
    sb.append("<author name=\"").append(escapeXML(wsLinkGenerator.getVendor())).append("\" email=\"\"/>");
    sb.append("</authors>");
    URL websiteURL = wsLinkGenerator.getWebsiteURL();
    if(websiteURL != null) {
      sb.append("<url>").append(escapeXML(websiteURL.toExternalForm())).append("</url>");
    }
    sb.append("</info>");
    sb.append("<guiprefs width=\"550\" height=\"400\" resizable=\"yes\">");
    sb.append("<modifier key=\"useLabelIcons\" value=\"no\"/>");
    sb.append("<modifier key=\"useHeadingPanel\" value=\"yes\"/>");
    sb.append("<modifier key=\"headingLineCount\" value=\"1\"/>");
    sb.append("<modifier key=\"headingFontSize\" value=\"1.5\"/>");
    sb.append("<modifier key=\"headingBackgroundColor\" value=\"0x00ffffff\"/>");
    sb.append("<modifier key=\"headingPanelCounter\" value=\"text\"/>");
    sb.append("<modifier key=\"headingPanelCounterPos\" value=\"inHeading\"/>");
    sb.append("<modifier key=\"paragraphYGap\" value=\"15\"/>");
    sb.append("<modifier key=\"labelGap\" value=\"2\"/>");
    sb.append("<modifier key=\"allYGap\" value=\"3\"/>");
    sb.append("</guiprefs>");
    sb.append("<locale>");
    sb.append("<langpack iso3=\"eng\"/>");
    sb.append("</locale>");
    sb.append("<variables>");
    sb.append("<variable name=\"APP_NAME_FS\" value=\"").append(escapeXML(wsLinkGenerator.getApplicationNameFS())).append("\"/>");
    sb.append("<variable name=\"JNLP_LOCAL_PATH\" value=\"").append(escapeXML(wsLinkGenerator.getJnlpPath())).append("\"/>");
    sb.append("</variables>");
    sb.append("<resources>");
//    sb.append("<res id=\"Heading.image\" src=\"resources/DJ64x56.png\"/>");
    File readmeFile = waPackerConfiguration.getReadmeFile();
    if(readmeFile != null && readmeFile.exists()) {
      if(isHTML(readmeFile)) {
        sb.append("<res id=\"HTMLInfoPanel.info\" src=\"").append(escapeXML(readmeFile.getAbsolutePath())).append("\"/>");
      } else {
        sb.append("<res id=\"InfoPanel.info\" src=\"").append(escapeXML(readmeFile.getAbsolutePath())).append("\"/>");
      }
    }
    File licenseFile = waPackerConfiguration.getLicenseFile();
    if(licenseFile != null && licenseFile.exists()) {
      if(isHTML(licenseFile)) {
        sb.append("<res id=\"HTMLLicencePanel.licence\" src=\"").append(escapeXML(licenseFile.getAbsolutePath())).append("\"/>");
      } else {
        sb.append("<res id=\"LicencePanel.licence\" src=\"").append(escapeXML(licenseFile.getAbsolutePath())).append("\"/>");
      }
    }
    sb.append("<res id=\"shortcutSpec.xml\" src=\"").append(escapeXML(shortcutSpecXMLFile.getAbsolutePath())).append("\"/>");
    sb.append("<res id=\"Unix_shortcutSpec.xml\" src=\"").append(escapeXML(shortcutSpecXMLFile.getAbsolutePath())).append("\"/>");
    sb.append("<res id=\"CustomLangpack.xml_eng\" src=\"").append(escapeXML(customLangPackXMLFile.getAbsolutePath())).append("\"/>");
//    sb.append("<res id=\"customicons.xml\" src=\"resources/CustomIcons.xml\"/>");
//    sb.append("<res id=\"DJFrameIcon.png\" src=\"resources/DJInstall32x32.png\"/>");
    sb.append("</resources>");
    sb.append("<panels>");
//    sb.append("<panel classname=\"HelloPanel\"/>");
    if(readmeFile != null && readmeFile.exists()) {
      if(isHTML(readmeFile)) {
        sb.append("<panel classname=\"HTMLInfoPanel\"/>");
      } else {
        sb.append("<panel classname=\"InfoPanel\"/>");
      }
    }
    if(licenseFile != null && licenseFile.exists()) {
      if(isHTML(licenseFile)) {
        sb.append("<panel classname=\"HTMLLicencePanel\"/>");
      } else {
        sb.append("<panel classname=\"LicencePanel\"/>");
      }
    }
    sb.append("<panel classname=\"PacksPanel\"/>");
    sb.append("<panel classname=\"TargetPanel\"/>");
    sb.append("<panel classname=\"InstallPanel\"/>");
    sb.append("<panel classname=\"ShortcutPanel\"/>");
    sb.append("<panel classname=\"SimpleFinishPanel\"/>");
    sb.append("</panels>");
    sb.append("<jar src=\"").append(escapeXML(IZPACK_HOME)).append("/bin/customActions/RegistryUninstallerListener.jar\" stage=\"install\"/>");
    sb.append("<jar src=\"").append(escapeXML(IZPACK_HOME)).append("/lib/izevent.jar\" stage=\"uninstall\"/>");
    sb.append("<listeners>");
    sb.append("<listener uninstaller=\"UninstallWSUninstallerListener\">");
//    sb.append("<os family=\"windows\"/>");
    sb.append("</listener>");
    File applicationArchiveFile = waPackerConfiguration.getApplicationArchiveFile();
    if(applicationArchiveFile != null) {
      sb.append("<listener installer=\"InstallWSInstallerListener\">");
//      sb.append("<os family=\"windows\"/>");
      sb.append("</listener>");
    }
    sb.append("</listeners>");
    sb.append("<packs>");
    sb.append("<pack name=\"").append(escapeXML(appName)).append(" Files\" required=\"yes\">");
    sb.append("<description>The core files of this Web Application.</description>");
    sb.append("<file src=\"").append(escapeXML(wsLinkJarFile.getAbsolutePath())).append("\" targetdir=\"$INSTALL_PATH\"/>");
    if(readmeFile != null && readmeFile.exists()) {
      sb.append("<file src=\"").append(escapeXML(readmeFile.getAbsolutePath())).append("\" targetdir=\"$INSTALL_PATH\"/>");
    }
    if(licenseFile != null && licenseFile.exists()) {
      sb.append("<file src=\"").append(escapeXML(licenseFile.getAbsolutePath())).append("\" targetdir=\"$INSTALL_PATH\"/>");
    }
    if(applicationArchiveFile != null) {
      sb.append("<file src=\"").append(escapeXML(applicationArchiveFile.getAbsolutePath())).append("\" targetdir=\"$INSTALL_PATH/.djwaimport\" unpack=\"true\"/>");
    }
    sb.append("</pack>");
    sb.append("</packs>");
    sb.append("<native type=\"izpack\" name=\"ShellLink.dll\">");
    sb.append("<os family=\"windows\"/>");
    sb.append("</native>");
    sb.append("<native type=\"3rdparty\" name=\"COIOSHelper.dll\" stage=\"install\">");
    sb.append("<os family=\"windows\"/>");
    sb.append("</native>");
    sb.append("</installation>");
    try {
      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(xmlFile));
      out.write(sb.toString().getBytes("UTF-8"));
      out.close();
      return true;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  protected void cleanUp(File tmpDir) {
    if(!tmpDir.delete()) {
      if(tmpDir.isDirectory()) {
        for(File file: tmpDir.listFiles()) {
          cleanUp(file);
        }
        tmpDir.delete();
      }
    }
  }
  
  protected static boolean isHTML(File file) {
    String nameLC = file.getName().toLowerCase(Locale.ENGLISH);
    return nameLC.endsWith(".html") || nameLC.endsWith(".htm");
  }
  
  protected static String escapeXML(String s) {
    if(s == null || s.length() == 0) {
      return s;
    }
    StringBuffer sb = new StringBuffer((int)(s.length() * 1.1));
    for(int i=0; i<s.length(); i++) {
      char c = s.charAt(i);
      switch(c) {
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '\'':
          sb.append("&apos;");
          break;
        case '\"':
          sb.append("&quot;");
          break;
        default:
          sb.append(c);
        break;
      }
    }
    return sb.toString();
  }
  
  public static void main(String[] args) {
    try {
      new WAPacker().pack(new WAPackerConfiguration(new URL("http://javadesktop.org/swinglabs/demos/nimbus/nimbus.jnlp"), new File("C:\\eclipse\\workspace_DJ\\Installer\\resources\\install-readme.html"), new File("C:\\eclipse\\workspace_DJ\\Installer\\resources\\license.txt"), new File("nimbus-installer.jar"), null));
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}
