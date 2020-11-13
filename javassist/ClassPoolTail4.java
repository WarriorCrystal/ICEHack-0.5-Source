package javassist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

final class ClassPoolTail4 implements ClassPath {
  String directory;
  
  ClassPoolTail4(String paramString) {
    this.directory = paramString;
  }
  
  public InputStream openClassfile(String paramString) {
    try {
      char c = File.separatorChar;
      String str = this.directory + c + paramString.replace('.', c) + ".class";
      return new FileInputStream(str.toString());
    } catch (FileNotFoundException fileNotFoundException) {
    
    } catch (SecurityException securityException) {}
    return null;
  }
  
  public URL find(String paramString) {
    char c = File.separatorChar;
    String str = this.directory + c + paramString.replace('.', c) + ".class";
    File file = new File(str);
    if (file.exists())
      try {
        return file.getCanonicalFile().toURI().toURL();
      } catch (MalformedURLException malformedURLException) {
      
      } catch (IOException iOException) {} 
    return null;
  }
  
  public void close() {}
  
  public String toString() {
    return this.directory;
  }
}
