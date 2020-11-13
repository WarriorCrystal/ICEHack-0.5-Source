package org.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public class JarInputFile implements Vfs.File {
  private final ZipEntry entry;
  
  private final JarInputDir jarInputDir;
  
  private final long fromIndex;
  
  private final long endIndex;
  
  public JarInputFile(ZipEntry paramZipEntry, JarInputDir paramJarInputDir, long paramLong1, long paramLong2) {
    this.entry = paramZipEntry;
    this.jarInputDir = paramJarInputDir;
    this.fromIndex = paramLong1;
    this.endIndex = paramLong2;
  }
  
  public String getName() {
    String str = this.entry.getName();
    return str.substring(str.lastIndexOf("/") + 1);
  }
  
  public String getRelativePath() {
    return this.entry.getName();
  }
  
  public InputStream openInputStream() throws IOException {
    return new InputStream() {
        public int read() throws IOException {
          if (JarInputFile.this.jarInputDir.cursor >= JarInputFile.this.fromIndex && JarInputFile.this.jarInputDir.cursor <= JarInputFile.this.endIndex) {
            int i = JarInputFile.this.jarInputDir.jarInputStream.read();
            JarInputFile.this.jarInputDir.cursor++;
            return i;
          } 
          return -1;
        }
      };
  }
}
