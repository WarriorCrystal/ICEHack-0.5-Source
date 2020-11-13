package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.util.List;

public abstract class Property implements Comparable<Property> {
  private final String name;
  
  private final Class<?> type;
  
  public Property(String paramString, Class<?> paramClass) {
    this.name = paramString;
    this.type = paramClass;
  }
  
  public Class<?> getType() {
    return this.type;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String toString() {
    return getName() + " of " + getType();
  }
  
  public int compareTo(Property paramProperty) {
    return getName().compareTo(paramProperty.getName());
  }
  
  public boolean isWritable() {
    return true;
  }
  
  public boolean isReadable() {
    return true;
  }
  
  public int hashCode() {
    return getName().hashCode() + getType().hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Property) {
      Property property = (Property)paramObject;
      return (getName().equals(property.getName()) && getType().equals(property.getType()));
    } 
    return false;
  }
  
  public abstract Class<?>[] getActualTypeArguments();
  
  public abstract void set(Object paramObject1, Object paramObject2) throws Exception;
  
  public abstract Object get(Object paramObject);
  
  public abstract List<Annotation> getAnnotations();
  
  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
}
