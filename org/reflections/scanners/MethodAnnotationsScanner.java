package org.reflections.scanners;

public class MethodAnnotationsScanner extends AbstractScanner {
  public void scan(Object paramObject) {
    for (Object object : getMetadataAdapter().getMethods(paramObject)) {
      for (String str : getMetadataAdapter().getMethodAnnotationNames(object)) {
        if (acceptResult(str))
          getStore().put(str, getMetadataAdapter().getMethodFullKey(paramObject, object)); 
      } 
    } 
  }
}
