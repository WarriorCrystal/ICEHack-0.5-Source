package org.reflections.scanners;

import org.reflections.vfs.Vfs;

@Deprecated
public class TypesScanner extends AbstractScanner {
  public Object scan(Vfs.File paramFile, Object paramObject) {
    paramObject = super.scan(paramFile, paramObject);
    String str = getMetadataAdapter().getClassName(paramObject);
    getStore().put(str, str);
    return paramObject;
  }
  
  public void scan(Object paramObject) {
    throw new UnsupportedOperationException("should not get here");
  }
}
