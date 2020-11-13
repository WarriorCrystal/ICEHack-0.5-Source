package org.reflections.vfs;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class SystemDir implements Vfs.Dir {
  private final File file;
  
  public SystemDir(File paramFile) {
    if (paramFile != null && (!paramFile.isDirectory() || !paramFile.canRead()))
      throw new RuntimeException("cannot use dir " + paramFile); 
    this.file = paramFile;
  }
  
  public String getPath() {
    if (this.file == null)
      return "/NO-SUCH-DIRECTORY/"; 
    return this.file.getPath().replace("\\", "/");
  }
  
  public Iterable<Vfs.File> getFiles() {
    if (this.file == null || !this.file.exists())
      return Collections.emptyList(); 
    return new Iterable<Vfs.File>() {
        public Iterator<Vfs.File> iterator() {
          return (Iterator<Vfs.File>)new AbstractIterator<Vfs.File>() {
              final Stack<File> stack;
              
              protected Vfs.File computeNext() {
                while (!this.stack.isEmpty()) {
                  File file = this.stack.pop();
                  if (file.isDirectory()) {
                    this.stack.addAll(SystemDir.listFiles(file));
                    continue;
                  } 
                  return new SystemFile(SystemDir.this, file);
                } 
                return (Vfs.File)endOfData();
              }
            };
        }
      };
  }
  
  private static List<File> listFiles(File paramFile) {
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile != null)
      return Lists.newArrayList((Object[])arrayOfFile); 
    return Lists.newArrayList();
  }
  
  public void close() {}
  
  public String toString() {
    return getPath();
  }
}
