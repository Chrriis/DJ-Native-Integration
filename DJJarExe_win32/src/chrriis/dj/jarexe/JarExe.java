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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author Christopher Deckers
 */
public class JarExe {

  public JarExe(String[] args) {
    String tmpDirPath = System.getProperty("java.io.tmpdir") + "/.djjarexe";
    File tmpDir = new File(tmpDirPath);
    // Check argument validity
    if(args.length < 1) {
      tmpDir.delete();
      throw new IllegalArgumentException("There must be at least one argument which is the path to the JAR file!");
    }
    String jarFilePath = args[0];
    File jarFile = new File(jarFilePath);
    String jarFileParentPath = jarFile.getParentFile().getAbsolutePath().replace('/', '_').replace('\\', '_').replace(':', '_');
    String pid = System.getProperty("dj.jarexe.pid");
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
    // Run the new exe file
    String[] newArgs = new String[args.length + 2];
    newArgs[0] = exeFile.getAbsolutePath();
    newArgs[1] = "-jar";
    System.arraycopy(args, 0, newArgs, 2, args.length);
    args = newArgs;
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(args);
      processBuilder.environment().put("JAVA_HOME", javaHome);
      processBuilder.directory(jarFile.getParentFile());
      processBuilder.start();
    } catch(Exception e) {
      e.printStackTrace();
    }
    // Cleanup
    cleanUp(tmpDir);
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
