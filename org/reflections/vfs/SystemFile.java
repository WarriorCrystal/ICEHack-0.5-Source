package org.reflections.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SystemFile implements Vfs.File {
  private final SystemDir root;
  
  private final File file;
  
  public SystemFile(SystemDir paramSystemDir, File paramFile) {
    this.root = paramSystemDir;
    this.file = paramFile;
  }
  
  public String getName() {
    return this.file.getName();
  }
  
  public String getRelativePath() {
    String str = this.file.getPath().replace("\\", "/");
    if (str.startsWith(this.root.getPath()))
      return str.substring(this.root.getPath().length() + 1); 
    return null;
  }
  
  public InputStream openInputStream() {
    try {
      return new FileInputStream(this.file);
    } catch (FileNotFoundException fileNotFoundException) {
      throw new RuntimeException(fileNotFoundException);
    } 
  }
  
  public String toString() {
    return this.file.toString();
  }
}
