/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.wapacker;

import java.io.File;
import java.net.URL;

/**
 * @author Christopher Deckers
 */
public class WAPackerConfiguration {

  public WAPackerConfiguration() {
  }
  
  public WAPackerConfiguration(URL jnlpURL, File outputJarFile, File readmeFile, File licenseFile, File applicationArchiveFile) {
    this.jnlpURL = jnlpURL;
    this.outputJarFile = outputJarFile;
    this.readmeFile = readmeFile;
    this.licenseFile = licenseFile;
    this.applicationArchiveFile = applicationArchiveFile;
  }
  
  protected URL jnlpURL;
  
  public void setJnlpURL(URL jnlpURL) {
    this.jnlpURL = jnlpURL;
  }
  
  public URL getJnlpURL() {
    return jnlpURL;
  }
  
  protected File outputJarFile;
  
  public void setOutputJarFile(File outputJarFile) {
    this.outputJarFile = outputJarFile;
  }
  
  public File getOutputJarFile() {
    return outputJarFile;
  }
  
  protected File readmeFile;
  
  public void setReadmeFile(File readmeFile) {
    this.readmeFile = readmeFile;
  }
  
  public File getReadmeFile() {
    return readmeFile;
  }
  
  protected File licenseFile;
  
  public void setLicenseFile(File licenseFile) {
    this.licenseFile = licenseFile;
  }
  
  public File getLicenseFile() {
    return licenseFile;
  }
  
  protected File applicationArchiveFile;
  
  public void setApplicationArchiveFile(File applicationArchiveFile) {
    this.applicationArchiveFile = applicationArchiveFile;
  }
  
  public File getApplicationArchiveFile() {
    return applicationArchiveFile;
  }
  
}
