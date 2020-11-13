package org.reflections.vfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public class ZipFile implements Vfs.File {
  private final ZipDir root;
  
  private final ZipEntry entry;
  
  public ZipFile(ZipDir paramZipDir, ZipEntry paramZipEntry) {
    this.root = paramZipDir;
    this.entry = paramZipEntry;
  }
  
  public String getName() {
    String str = this.entry.getName();
    return str.substring(str.lastIndexOf("/") + 1);
  }
  
  public String getRelativePath() {
    return this.entry.getName();
  }
  
  public InputStream openInputStream() throws IOException {
    return this.root.jarFile.getInputStream(this.entry);
  }
  
  public String toString() {
    return this.root.getPath() + "!" + File.separatorChar + this.entry.toString();
  }
}
