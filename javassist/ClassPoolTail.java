package javassist;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;

final class ClassPoolTail implements ClassPath {
  ClassPoolTail3[] jars;
  
  ClassPoolTail(String paramString) throws NotFoundException {
    File[] arrayOfFile = (new File(paramString)).listFiles(new FilenameFilter() {
          public boolean accept(File param1File, String param1String) {
            param1String = param1String.toLowerCase();
            return (param1String.endsWith(".jar") || param1String.endsWith(".zip"));
          }
        });
    if (arrayOfFile != null) {
      this.jars = new ClassPoolTail3[arrayOfFile.length];
      for (byte b = 0; b < arrayOfFile.length; b++)
        this.jars[b] = new ClassPoolTail3(arrayOfFile[b].getPath()); 
    } 
  }
  
  public InputStream openClassfile(String paramString) throws NotFoundException {
    if (this.jars != null)
      for (byte b = 0; b < this.jars.length; b++) {
        InputStream inputStream = this.jars[b].openClassfile(paramString);
        if (inputStream != null)
          return inputStream; 
      }  
    return null;
  }
  
  public URL find(String paramString) {
    if (this.jars != null)
      for (byte b = 0; b < this.jars.length; b++) {
        URL uRL = this.jars[b].find(paramString);
        if (uRL != null)
          return uRL; 
      }  
    return null;
  }
  
  public void close() {
    if (this.jars != null)
      for (byte b = 0; b < this.jars.length; b++)
        this.jars[b].close();  
  }
}
