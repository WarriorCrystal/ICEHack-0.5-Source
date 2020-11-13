package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class MissingProperty extends Property {
  public MissingProperty(String paramString) {
    super(paramString, Object.class);
  }
  
  public Class<?>[] getActualTypeArguments() {
    return new Class[0];
  }
  
  public void set(Object paramObject1, Object paramObject2) throws Exception {}
  
  public Object get(Object paramObject) {
    return paramObject;
  }
  
  public List<Annotation> getAnnotations() {
    return Collections.emptyList();
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass) {
    return null;
  }
}
