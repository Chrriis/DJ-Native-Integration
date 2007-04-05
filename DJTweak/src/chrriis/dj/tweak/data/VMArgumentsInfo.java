package chrriis.dj.tweak.data;

/**
 * @author Christopher Deckers
 */
public class VMArgumentsInfo {

  protected String vendor;
  protected String version;
  protected String arguments;
  
  public VMArgumentsInfo(String vendor, String version, String arguments) {
    this.vendor = vendor;
    this.version = version;
    this.arguments = arguments;
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
  
  public String getArguments() {
    return arguments;
  }
  
  public void setArguments(String arguments) {
    this.arguments = arguments;
  }
  
}
