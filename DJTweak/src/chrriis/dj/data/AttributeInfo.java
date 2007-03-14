/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.data;

/**
 * @author Christopher Deckers
 */
public class AttributeInfo {

  protected String key;
  protected String value;
  
  public AttributeInfo(String key, String value) {
    this.key = key;
    this.value = value;
  }
  
  public String getKey() {
    return key;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setKey(String key) {
    this.key = key;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
}
