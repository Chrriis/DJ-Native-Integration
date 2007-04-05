/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.jarexe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Christopher Deckers
 */
public class JarExe {

  protected static final String PID_PROPERTY = "dj.jarexe.pid";
  protected static final String EXE_PROPERTY = "dj.jarexe.exe";
  protected static final String VM_ARGUMENTS_HEADER = "VM-Arguments";

  public JarExe(String[] arguments) {
    String tmpDirPath = System.getProperty("java.io.tmpdir") + "/.djjarexe";
    File tmpDir = new File(tmpDirPath);
    // Check argument validity
    if(arguments.length < 1) {
      tmpDir.delete();
      throw new IllegalArgumentException("There must be at least one argument which is the path to the JAR file!");
    }
    String jarFilePath = arguments[0];
    File jarFile = new File(jarFilePath);
    String jarFileParentPath = jarFile.getParentFile().getAbsolutePath().replace('/', '_').replace('\\', '_').replace(':', '_');
    String pid = System.getProperty(PID_PROPERTY);
    if(pid != null) {
      jarFileParentPath += pid;
    }
    File exeFile = new File(tmpDir, jarFileParentPath + "/" + jarFile.getName());
    String javaHome = System.getProperty("java.home");
    if(!exeFile.exists() || exeFile.delete()) {
      exeFile.getParentFile().mkdirs();
      try {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(javaHome + "\\bin\\javaw.exe"));
        // Start of new implementation that removes the process description, which makes the shell default to the name of the process
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[8192];
        for(int i; (i=in.read(bytes))>-1; ) {
          baos.write(bytes, 0, i);
        }
        bytes = baos.toByteArray();
        byte[] pattern = new byte[] {'F', 0, 'i', 0, 'l', 0, 'e', 0, 'D', 0, 'e', 0, 's', 0, 'c', 0, 'r', 0, 'i', 0, 'p', 0, 't', 0, 'i', 0, 'o', 0, 'n', 0};
        for(int i=0; i<bytes.length; i++) {
          for(int j=0; j<pattern.length; j++) {
            if(bytes[i + j] != pattern[j]) {
              break;
            }
            if(j == pattern.length - 1) {
              int start = i + j + 1;
              for(; start < bytes.length && bytes[start] == 0; start++);
              int end = start;
              for(; end < bytes.length && bytes[end] != 0; end+=2);
              String name = jarFile.getName();
              int charCount = name.length();
              if(name.endsWith(".jar")) {
                charCount -= ".jar".length();
                name = name.substring(0, charCount);
              }
              if(end - start > charCount * 2) {
                for(int k=0; k<charCount; k++) {
                  int value = name.charAt(k);
                  bytes[start + k * 2] = (byte)(value & 0xFF);
                  bytes[start + k * 2 + 1] = (byte)(value >> 8);
                }
                bytes[start + charCount * 2] = 0;
                bytes[start + charCount * 2 + 1] = 0;
              } else {
                bytes[start] = 0;
                bytes[start + 1] = 0;
              }
            }
          }
        }
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(exeFile));
        out.write(bytes);
        // End of new implementation
//        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(exeFile));
//        byte[] bytes = new byte[8192];
//        for(int i; (i=in.read(bytes))>-1; ) {
//          out.write(bytes, 0, i);
//        }
        try {
          in.close();
        } catch(Exception e) {
          e.printStackTrace();
        }
        try {
          out.close();
        } catch(Exception e) {
          e.printStackTrace();
        }
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    if(!exeFile.exists()) {
      exeFile = new File(javaHome + "\\bin\\javaw.exe");
    }
    String[] vmArguments = getVMArguments(jarFile);
    // Run the new exe file
    String[] newArguments = new String[2 + vmArguments.length + arguments.length];
    newArguments[0] = exeFile.getAbsolutePath();
    System.arraycopy(vmArguments, 0, newArguments, 1, vmArguments.length);
    newArguments[1 + vmArguments.length] = "-jar";
    System.arraycopy(arguments, 0, newArguments, 2 + vmArguments.length, arguments.length);
    arguments = newArguments;
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(arguments);
      processBuilder.environment().put("JAVA_HOME", javaHome);
      processBuilder.directory(jarFile.getParentFile());
      processBuilder.start();
    } catch(Exception e) {
      e.printStackTrace();
    }
    // Cleanup
    cleanUp(tmpDir);
  }
  
  protected String[] getVMArguments(File jarFile) {
    try {
      StringBuilder sb = new StringBuilder();
      Manifest manifest = new JarFile(jarFile).getManifest();
      Attributes attributes = manifest.getMainAttributes();
      for(Object key: attributes.keySet()) {
        Attributes.Name name = (Attributes.Name)key;
        String s = name.toString();
        if(s.equals(VM_ARGUMENTS_HEADER)) {
          String arguments = attributes.getValue(s).trim();
          if(arguments.length() > 0) {
            if(!arguments.startsWith("<")) {
              sb.append(arguments);
            } else {
              try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new ByteArrayInputStream(arguments.getBytes("UTF-8")));
                NodeList childNodes = document.getElementsByTagName("vmarguments").item(0).getChildNodes();
                for(int i=0; i<childNodes.getLength(); i++) {
                  Node node = childNodes.item(i);
                  if("pattern".equals(node.getNodeName())) {
                    NamedNodeMap argumentsAttributes = node.getAttributes();
                    Node item = argumentsAttributes.getNamedItem("vendor");
                    String vendor = item == null? "": item.getNodeValue();
                    item = argumentsAttributes.getNamedItem("version");
                    String version = item == null? "": item.getNodeValue();
                    item = argumentsAttributes.getNamedItem("arguments");
                    if(item != null) {
                      String jVendor = System.getProperty("java.vendor");
                      String jVersion = System.getProperty("java.version");
                      if(jVendor.matches(vendor.length() == 0? ".*": vendor) && jVersion.matches(version.length() == 0? ".*": version)) {
                        sb.append(' ').append(item.getNodeValue().trim());
                      }
                    }
                  }
                }
              } catch(Exception e) {
                e.printStackTrace();
              }
            }
          }
          break;
        }
      }
      int charCount = sb.length();
      List<String> argList = new ArrayList<String>(charCount);
      char lastChar = 0;
      boolean isEscaping = false;
      StringBuilder argSB = new StringBuilder();
      for(int i=0; i<charCount; i++) {
        char c = sb.charAt(i);
        switch(c) {
          case '"':
            if(lastChar == c) {
              argSB.append(c);
              lastChar = 0;
            } else {
              lastChar = c;
            }
            isEscaping = !isEscaping;
            break;
          case ' ':
            if(isEscaping) {
              argSB.append(c);
            } else if(argSB.length() > 0) {
              argList.add(argSB.toString());
              argSB = new StringBuilder(charCount - i + 1);
            }
            lastChar = c;
            break;
          default:
            argSB.append(c);
            lastChar = c;
            break;
        }
      }
      if(argSB.length() > 0) {
        argList.add(argSB.toString());
      }
      return argList.toArray(new String[0]);
    } catch(Exception e) {
      e.printStackTrace();
    }
    return new String[0];
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
  
  public static void main(String[] args) {
    new JarExe(args);
  }
  
}
