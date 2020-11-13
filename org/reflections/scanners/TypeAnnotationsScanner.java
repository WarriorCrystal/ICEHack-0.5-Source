package org.reflections.scanners;

import java.lang.annotation.Inherited;

public class TypeAnnotationsScanner extends AbstractScanner {
  public void scan(Object paramObject) {
    String str = getMetadataAdapter().getClassName(paramObject);
    for (String str1 : getMetadataAdapter().getClassAnnotationNames(paramObject)) {
      if (acceptResult(str1) || str1
        .equals(Inherited.class.getName()))
        getStore().put(str1, str); 
    } 
  }
}
