package javassist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class ClassPoolTail3 implements ClassPath {
  JarFile jarfile;
  
  String jarfileURL;
  
  ClassPoolTail3(String paramString) throws NotFoundException {
    try {
      this.jarfile = new JarFile(paramString);
      this
        .jarfileURL = (new File(paramString)).getCanonicalFile().toURI().toURL().toString();
      return;
    } catch (IOException iOException) {
      throw new NotFoundException(paramString);
    } 
  }
  
  public InputStream openClassfile(String paramString) throws NotFoundException {
    try {
      String str = paramString.replace('.', '/') + ".class";
      JarEntry jarEntry = this.jarfile.getJarEntry(str);
      if (jarEntry != null)
        return this.jarfile.getInputStream(jarEntry); 
      return null;
    } catch (IOException iOException) {
      throw new NotFoundException("broken jar file?: " + this.jarfile
          .getName());
    } 
  }
  
  public URL find(String paramString) {
    String str = paramString.replace('.', '/') + ".class";
    JarEntry jarEntry = this.jarfile.getJarEntry(str);
    if (jarEntry != null)
      try {
        return new URL("jar:" + this.jarfileURL + "!/" + str);
      } catch (MalformedURLException malformedURLException) {} 
    return null;
  }
  
  public void close() {
    try {
      this.jarfile.close();
      this.jarfile = null;
    } catch (IOException iOException) {}
  }
  
  public String toString() {
    return (this.jarfile == null) ? "<null>" : this.jarfile.toString();
  }
}
