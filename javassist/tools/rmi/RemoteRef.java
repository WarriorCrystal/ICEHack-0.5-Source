package javassist.tools.rmi;

import java.io.Serializable;

public class RemoteRef implements Serializable {
  public int oid;
  
  public String classname;
  
  public RemoteRef(int paramInt) {
    this.oid = paramInt;
    this.classname = null;
  }
  
  public RemoteRef(int paramInt, String paramString) {
    this.oid = paramInt;
    this.classname = paramString;
  }
}
