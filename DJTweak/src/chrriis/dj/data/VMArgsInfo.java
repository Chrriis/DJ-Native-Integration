package chrriis.dj.data;

/**
 * @author Christopher Deckers
 */
public class VMArgsInfo {

  protected String vendor;
  protected String version;
  protected String args;
  
  public VMArgsInfo(String vendor, String version, String args) {
    this.vendor = vendor;
    this.version = version;
    this.args = args;
  }
  
  public String getVendor() {
    return vendor;
  }
  
  public void setVendor(String vendor) {
    this.vendor = vendor;
  }
  
  public String getVersion() {
    return version;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public String getArgs() {
    return args;
  }
  
  public void setArgs(String args) {
    this.args = args;
  }
  
}
