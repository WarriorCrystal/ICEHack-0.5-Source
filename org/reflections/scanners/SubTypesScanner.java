package org.reflections.scanners;

import com.google.common.base.Predicate;
import org.reflections.util.FilterBuilder;

public class SubTypesScanner extends AbstractScanner {
  public SubTypesScanner() {
    this(true);
  }
  
  public SubTypesScanner(boolean paramBoolean) {
    if (paramBoolean)
      filterResultsBy((Predicate<String>)(new FilterBuilder()).exclude(Object.class.getName())); 
  }
  
  public void scan(Object paramObject) {
    String str1 = getMetadataAdapter().getClassName(paramObject);
    String str2 = getMetadataAdapter().getSuperclassName(paramObject);
    if (acceptResult(str2))
      getStore().put(str2, str1); 
    for (String str : getMetadataAdapter().getInterfacesNames(paramObject)) {
      if (acceptResult(str))
        getStore().put(str, str1); 
    } 
  }
}
