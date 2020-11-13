package org.reflections.vfs;

import com.google.common.collect.AbstractIterator;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.reflections.Reflections;

public class ZipDir implements Vfs.Dir {
  final ZipFile jarFile;
  
  public ZipDir(JarFile paramJarFile) {
    this.jarFile = paramJarFile;
  }
  
  public String getPath() {
    return this.jarFile.getName();
  }
  
  public Iterable<Vfs.File> getFiles() {
    return new Iterable<Vfs.File>() {
        public Iterator<Vfs.File> iterator() {
          return (Iterator<Vfs.File>)new AbstractIterator<Vfs.File>() {
              final Enumeration<? extends ZipEntry> entries = ZipDir.this.jarFile.entries();
              
              protected Vfs.File computeNext() {
                while (this.entries.hasMoreElements()) {
                  ZipEntry zipEntry = this.entries.nextElement();
                  if (!zipEntry.isDirectory())
                    return new ZipFile(ZipDir.this, zipEntry); 
                } 
                return (Vfs.File)endOfData();
              }
            };
        }
      };
  }
  
  public void close() {
    try {
      this.jarFile.close();
    } catch (IOException iOException) {
      if (Reflections.log != null)
        Reflections.log.warn("Could not close JarFile", iOException); 
    } 
  }
  
  public String toString() {
    return this.jarFile.getName();
  }
}
