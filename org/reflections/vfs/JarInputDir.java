package org.reflections.vfs;

import com.google.common.collect.AbstractIterator;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.reflections.ReflectionsException;
import org.reflections.util.Utils;

public class JarInputDir implements Vfs.Dir {
  private final URL url;
  
  JarInputStream jarInputStream;
  
  long cursor = 0L;
  
  long nextCursor = 0L;
  
  public JarInputDir(URL paramURL) {
    this.url = paramURL;
  }
  
  public String getPath() {
    return this.url.getPath();
  }
  
  public Iterable<Vfs.File> getFiles() {
    return new Iterable<Vfs.File>() {
        public Iterator<Vfs.File> iterator() {
          return (Iterator<Vfs.File>)new AbstractIterator<Vfs.File>() {
              protected Vfs.File computeNext() {
                try {
                  while (true) {
                    JarEntry jarEntry = JarInputDir.this.jarInputStream.getNextJarEntry();
                    if (jarEntry == null)
                      return (Vfs.File)endOfData(); 
                    long l = jarEntry.getSize();
                    if (l < 0L)
                      l = 4294967295L + l; 
                    JarInputDir.this.nextCursor += l;
                    if (!jarEntry.isDirectory())
                      return new JarInputFile(jarEntry, JarInputDir.this, JarInputDir.this.cursor, JarInputDir.this.nextCursor); 
                  } 
                } catch (IOException iOException) {
                  throw new ReflectionsException("could not get next zip entry", iOException);
                } 
              }
            };
        }
      };
  }
  
  public void close() {
    Utils.close(this.jarInputStream);
  }
}
