/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.data;

import java.net.URL;

/**
 * @author Christopher Deckers
 */
public class IconInfo {

  protected int width;
  protected int height;
  protected String path;
  protected URL resourceURL;
  
  public IconInfo(int width, int height, String path, URL resourceURL) {
    this.width = width;
    this.height = height;
    this.path = path;
    this.resourceURL = resourceURL;
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public String getPath() {
    return path;
  }

  public URL getResourceURL() {
    return resourceURL;
  }

  protected void setResourceURL(URL resourceURL) {
    this.resourceURL = resourceURL;
  }

}
