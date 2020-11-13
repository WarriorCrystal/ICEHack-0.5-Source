package javassist;

import java.io.InputStream;
import java.net.URL;

public class ClassClassPath implements ClassPath {
  private Class thisClass;
  
  public ClassClassPath(Class paramClass) {
    this.thisClass = paramClass;
  }
  
  ClassClassPath() {
    this(Object.class);
  }
  
  public InputStream openClassfile(String paramString) {
    String str = "/" + paramString.replace('.', '/') + ".class";
    return this.thisClass.getResourceAsStream(str);
  }
  
  public URL find(String paramString) {
    String str = "/" + paramString.replace('.', '/') + ".class";
    return this.thisClass.getResource(str);
  }
  
  public void close() {}
  
  public String toString() {
    return this.thisClass.getName() + ".class";
  }
}
