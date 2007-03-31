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
        byte[] pattern = new byte[] {'J', 0, 'a', 0, 'v', 0, 'a', 0, '(', 0, 'T', 0, 'M', 0, ')', 0};
        for(int i=0; i<bytes.length; i++) {
          for(int j=0; j<pattern.length; j++) {
            if(bytes[i + j] != pattern[j]) {
              break;
            }
            if(j == pattern.length - 1) {
              bytes[i] = 0;
              bytes[i + 1] = 0;
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
