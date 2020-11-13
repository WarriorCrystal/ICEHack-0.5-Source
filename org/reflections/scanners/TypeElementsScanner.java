package org.reflections.scanners;

import com.google.common.base.Joiner;

public class TypeElementsScanner extends AbstractScanner {
  private boolean includeFields = true;
  
  private boolean includeMethods = true;
  
  private boolean includeAnnotations = true;
  
  private boolean publicOnly = true;
  
  public void scan(Object paramObject) {
    String str = getMetadataAdapter().getClassName(paramObject);
    if (!acceptResult(str))
      return; 
    getStore().put(str, "");
    if (this.includeFields)
      for (Object object : getMetadataAdapter().getFields(paramObject)) {
        String str1 = getMetadataAdapter().getFieldName(object);
        getStore().put(str, str1);
      }  
    if (this.includeMethods)
      for (Object object : getMetadataAdapter().getMethods(paramObject)) {
        if (!this.publicOnly || getMetadataAdapter().isPublic(object)) {
          String str1 = getMetadataAdapter().getMethodName(object) + "(" + Joiner.on(", ").join(getMetadataAdapter().getParameterNames(object)) + ")";
          getStore().put(str, str1);
        } 
      }  
    if (this.includeAnnotations)
      for (Object object : getMetadataAdapter().getClassAnnotationNames(paramObject))
        getStore().put(str, "@" + object);  
  }
  
  public TypeElementsScanner includeFields() {
    return includeFields(true);
  }
  
  public TypeElementsScanner includeFields(boolean paramBoolean) {
    this.includeFields = paramBoolean;
    return this;
  }
  
  public TypeElementsScanner includeMethods() {
    return includeMethods(true);
  }
  
  public TypeElementsScanner includeMethods(boolean paramBoolean) {
    this.includeMethods = paramBoolean;
    return this;
  }
  
  public TypeElementsScanner includeAnnotations() {
    return includeAnnotations(true);
  }
  
  public TypeElementsScanner includeAnnotations(boolean paramBoolean) {
    this.includeAnnotations = paramBoolean;
    return this;
  }
  
  public TypeElementsScanner publicOnly(boolean paramBoolean) {
    this.publicOnly = paramBoolean;
    return this;
  }
  
  public TypeElementsScanner publicOnly() {
    return publicOnly(true);
  }
}
