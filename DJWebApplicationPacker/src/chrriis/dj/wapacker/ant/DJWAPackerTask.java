/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.wapacker.ant;

import java.io.File;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import chrriis.dj.wapacker.WAPacker;
import chrriis.dj.wapacker.WAPackerConfiguration;

/**
 * @author Christopher Deckers
 */
public class DJWAPackerTask extends Task {

  protected String jnlpURLString;
  protected File toFile;
  protected File readmeFile;
  protected File licenseFile;
  protected File applicationArchiveFile;
  
  public void setJnlpurl(String jnlpURLString) {
    this.jnlpURLString = jnlpURLString;
  }
  
  public void setTofile(File toFile) {
    this.toFile = toFile;
  }
  
  public void setReadmefile(File readmeFile) {
    this.readmeFile = readmeFile;
  }
  
  public void setLicensefile(File licenseFile) {
    this.licenseFile = licenseFile;
  }
  
  public void setApplicationarchivefile(File applicationArchiveFile) {
    this.applicationArchiveFile = applicationArchiveFile;
  }

  public void execute() throws BuildException {
    if(jnlpURLString == null) {
      throw new BuildException("The \"jnlpurl\" attribute is mandatory!");
    }
    if(toFile == null) {
      throw new BuildException("The \"tofile\" attribute is mandatory!");
    }
    if(readmeFile != null && !readmeFile.exists()) {
      throw new BuildException("The readme file that is specified does not exist!");
    }
    if(licenseFile != null && !licenseFile.exists()) {
      throw new BuildException("The license file that is specified does not exist!");
    }
    URL jnlpURL;
    try {
      jnlpURL = new URL(jnlpURLString);
    } catch(Exception ex) {
      try {
        File jnlpDescriptorFile = new File(jnlpURLString);
        if(jnlpDescriptorFile.exists()) {
          jnlpURL = jnlpDescriptorFile.toURI().toURL();
        } else {
          jnlpURL = null;
        }
      } catch(Exception exc) {
        jnlpURL = null;
      }
    }
    if(jnlpURL == null) {
      throw new BuildException("The JNLP descriptor could not be accessed!");
    }
    WAPackerConfiguration waPackerConfiguration = new WAPackerConfiguration(jnlpURL, toFile, readmeFile, licenseFile, applicationArchiveFile);
    try {
      new WAPacker().pack(waPackerConfiguration);
    } catch(Throwable t) {
      t.printStackTrace();
      String message = t.getMessage();
      if(t instanceof NoClassDefFoundError) {
        message = "Could not find class " + message;
      }
      message = "Generation failed" + (message == null? "!": ": " + message); 
      throw new BuildException(message);
    }
  }
  
}
