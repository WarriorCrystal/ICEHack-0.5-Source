package org.yaml.snakeyaml.introspector;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.ArrayUtils;

public class MethodProperty extends GenericProperty {
  private final PropertyDescriptor property;
  
  private final boolean readable;
  
  private final boolean writable;
  
  private static Type discoverGenericType(PropertyDescriptor paramPropertyDescriptor) {
    Method method1 = paramPropertyDescriptor.getReadMethod();
    if (method1 != null)
      return method1.getGenericReturnType(); 
    Method method2 = paramPropertyDescriptor.getWriteMethod();
    if (method2 != null) {
      Type[] arrayOfType = method2.getGenericParameterTypes();
      if (arrayOfType.length > 0)
        return arrayOfType[0]; 
    } 
    return null;
  }
  
  public MethodProperty(PropertyDescriptor paramPropertyDescriptor) {
    super(paramPropertyDescriptor.getName(), paramPropertyDescriptor.getPropertyType(), 
        discoverGenericType(paramPropertyDescriptor));
    this.property = paramPropertyDescriptor;
    this.readable = (paramPropertyDescriptor.getReadMethod() != null);
    this.writable = (paramPropertyDescriptor.getWriteMethod() != null);
  }
  
  public void set(Object paramObject1, Object paramObject2) throws Exception {
    if (!this.writable)
      throw new YAMLException("No writable property '" + getName() + "' on class: " + paramObject1
          .getClass().getName()); 
    this.property.getWriteMethod().invoke(paramObject1, new Object[] { paramObject2 });
  }
  
  public Object get(Object paramObject) {
    try {
      this.property.getReadMethod().setAccessible(true);
      return this.property.getReadMethod().invoke(paramObject, new Object[0]);
    } catch (Exception exception) {
      throw new YAMLException("Unable to find getter for property '" + this.property.getName() + "' on object " + paramObject + ":" + exception);
    } 
  }
  
  public List<Annotation> getAnnotations() {
    List<Annotation> list;
    if (isReadable() && isWritable()) {
      list = ArrayUtils.toUnmodifiableCompositeList((Object[])this.property.getReadMethod().getAnnotations(), (Object[])this.property.getWriteMethod().getAnnotations());
    } else if (isReadable()) {
      list = ArrayUtils.toUnmodifiableList((Object[])this.property.getReadMethod().getAnnotations());
    } else {
      list = ArrayUtils.toUnmodifiableList((Object[])this.property.getWriteMethod().getAnnotations());
    } 
    return list;
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass) {
    A a = null;
    if (isReadable())
      a = this.property.getReadMethod().getAnnotation((Class)paramClass); 
    if (a == null && isWritable())
      a = this.property.getWriteMethod().getAnnotation((Class)paramClass); 
    return a;
  }
  
  public boolean isWritable() {
    return this.writable;
  }
  
  public boolean isReadable() {
    return this.readable;
  }
}
