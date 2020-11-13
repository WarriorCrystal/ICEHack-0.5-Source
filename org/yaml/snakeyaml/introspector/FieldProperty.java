package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.ArrayUtils;

public class FieldProperty extends GenericProperty {
  private final Field field;
  
  public FieldProperty(Field paramField) {
    super(paramField.getName(), paramField.getType(), paramField.getGenericType());
    this.field = paramField;
    paramField.setAccessible(true);
  }
  
  public void set(Object paramObject1, Object paramObject2) throws Exception {
    this.field.set(paramObject1, paramObject2);
  }
  
  public Object get(Object paramObject) {
    try {
      return this.field.get(paramObject);
    } catch (Exception exception) {
      throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + paramObject + " : " + exception);
    } 
  }
  
  public List<Annotation> getAnnotations() {
    return ArrayUtils.toUnmodifiableList((Object[])this.field.getAnnotations());
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass) {
    return this.field.getAnnotation(paramClass);
  }
}
