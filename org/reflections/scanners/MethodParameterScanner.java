package org.reflections.scanners;

import java.util.List;
import org.reflections.adapters.MetadataAdapter;

public class MethodParameterScanner extends AbstractScanner {
  public void scan(Object paramObject) {
    MetadataAdapter metadataAdapter = getMetadataAdapter();
    for (Object object : metadataAdapter.getMethods(paramObject)) {
      String str1 = metadataAdapter.getParameterNames(object).toString();
      if (acceptResult(str1))
        getStore().put(str1, metadataAdapter.getMethodFullKey(paramObject, object)); 
      String str2 = metadataAdapter.getReturnTypeName(object);
      if (acceptResult(str2))
        getStore().put(str2, metadataAdapter.getMethodFullKey(paramObject, object)); 
      List list = metadataAdapter.getParameterNames(object);
      for (byte b = 0; b < list.size(); b++) {
        for (String str : metadataAdapter.getParameterAnnotationNames(object, b)) {
          if (acceptResult(str))
            getStore().put(str, metadataAdapter.getMethodFullKey(paramObject, object)); 
        } 
      } 
    } 
  }
}
