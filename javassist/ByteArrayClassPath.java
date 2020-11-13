package javassist;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ByteArrayClassPath implements ClassPath {
  protected String classname;
  
  protected byte[] classfile;
  
  public ByteArrayClassPath(String paramString, byte[] paramArrayOfbyte) {
    this.classname = paramString;
    this.classfile = paramArrayOfbyte;
  }
  
  public void close() {}
  
  public String toString() {
    return "byte[]:" + this.classname;
  }
  
  public InputStream openClassfile(String paramString) {
    if (this.classname.equals(paramString))
      return new ByteArrayInputStream(this.classfile); 
    return null;
  }
  
  public URL find(String paramString) {
    if (this.classname.equals(paramString)) {
      String str = paramString.replace('.', '/') + ".class";
      try {
        return new URL("file:/ByteArrayClassPath/" + str);
      } catch (MalformedURLException malformedURLException) {}
    } 
    return null;
  }
}
