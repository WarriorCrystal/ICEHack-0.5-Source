package org.reflections.scanners;

import org.reflections.vfs.Vfs;

public class ResourcesScanner extends AbstractScanner {
  public boolean acceptsInput(String paramString) {
    return !paramString.endsWith(".class");
  }
  
  public Object scan(Vfs.File paramFile, Object paramObject) {
    getStore().put(paramFile.getName(), paramFile.getRelativePath());
    return paramObject;
  }
  
  public void scan(Object paramObject) {
    throw new UnsupportedOperationException();
  }
}
