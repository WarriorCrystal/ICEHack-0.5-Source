package javassist;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

public class LoaderClassPath implements ClassPath {
  private WeakReference clref;
  
  public LoaderClassPath(ClassLoader paramClassLoader) {
    this.clref = new WeakReference<ClassLoader>(paramClassLoader);
  }
  
  public String toString() {
    Object object = null;
    if (this.clref != null)
      object = this.clref.get(); 
    return (object == null) ? "<null>" : object.toString();
  }
  
  public InputStream openClassfile(String paramString) {
    String str = paramString.replace('.', '/') + ".class";
    ClassLoader classLoader = this.clref.get();
    if (classLoader == null)
      return null; 
    return classLoader.getResourceAsStream(str);
  }
  
  public URL find(String paramString) {
    String str = paramString.replace('.', '/') + ".class";
    ClassLoader classLoader = this.clref.get();
    if (classLoader == null)
      return null; 
    return classLoader.getResource(str);
  }
  
  public void close() {
    this.clref = null;
  }
}
