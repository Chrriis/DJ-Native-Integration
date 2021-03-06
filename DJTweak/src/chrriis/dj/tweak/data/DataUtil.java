/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.tweak.data;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.ImageIcon;

/**
 * @author Christopher Deckers
 */
public class DataUtil {

  public static byte[] getImageData(URL imageURL) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      URLConnection openConnection = imageURL.openConnection();
      openConnection.setDefaultUseCaches(false);
      InputStream in = openConnection.getInputStream();
      byte[] bytes = new byte[1024];
      for(int i; (i=in.read(bytes))>= 0; ) {
        out.write(bytes, 0, i);
      }
      out.close();
      in.close();
      return out.toByteArray();
    } catch(Exception e) {
      throw new IllegalStateException("Could not get the image data!", e);
    }
  }
  
  public static Dimension getImageSize(URL imageURL) {
    ImageIcon icon = new ImageIcon(getImageData(imageURL));
    return new Dimension(icon.getIconWidth(), icon.getIconHeight());
  }
  
  public static String escapeXML(String s) {
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
  
}
