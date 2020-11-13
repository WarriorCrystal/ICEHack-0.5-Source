package org.reflections.scanners;

import java.util.List;

public class FieldAnnotationsScanner extends AbstractScanner {
  public void scan(Object paramObject) {
    String str = getMetadataAdapter().getClassName(paramObject);
    List list = getMetadataAdapter().getFields(paramObject);
    for (Object object : list) {
      List list1 = getMetadataAdapter().getFieldAnnotationNames(object);
      for (String str1 : list1) {
        if (acceptResult(str1)) {
          String str2 = getMetadataAdapter().getFieldName(object);
          getStore().put(str1, String.format("%s.%s", new Object[] { str, str2 }));
        } 
      } 
    } 
  }
}
