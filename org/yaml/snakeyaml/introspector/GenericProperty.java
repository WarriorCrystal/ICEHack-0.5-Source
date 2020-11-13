package org.yaml.snakeyaml.introspector;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class GenericProperty extends Property {
  private Type genType;
  
  private boolean actualClassesChecked;
  
  private Class<?>[] actualClasses;
  
  public GenericProperty(String paramString, Class<?> paramClass, Type paramType) {
    super(paramString, paramClass);
    this.genType = paramType;
    this.actualClassesChecked = (paramType == null);
  }
  
  public Class<?>[] getActualTypeArguments() {
    if (!this.actualClassesChecked) {
      if (this.genType instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType)this.genType;
        Type[] arrayOfType = parameterizedType.getActualTypeArguments();
        if (arrayOfType.length > 0) {
          this.actualClasses = new Class[arrayOfType.length];
          for (byte b = 0; b < arrayOfType.length; b++) {
            if (arrayOfType[b] instanceof Class) {
              this.actualClasses[b] = (Class)arrayOfType[b];
            } else if (arrayOfType[b] instanceof ParameterizedType) {
              this.actualClasses[b] = (Class)((ParameterizedType)arrayOfType[b])
                .getRawType();
            } else if (arrayOfType[b] instanceof GenericArrayType) {
              Type type = ((GenericArrayType)arrayOfType[b]).getGenericComponentType();
              if (type instanceof Class) {
                this.actualClasses[b] = Array.newInstance((Class)type, 0)
                  .getClass();
              } else {
                this.actualClasses = null;
                break;
              } 
            } else {
              this.actualClasses = null;
              break;
            } 
          } 
        } 
      } else if (this.genType instanceof GenericArrayType) {
        Type type = ((GenericArrayType)this.genType).getGenericComponentType();
        if (type instanceof Class)
          this.actualClasses = new Class[] { (Class)type }; 
      } else if (this.genType instanceof Class) {
        Class clazz = (Class)this.genType;
        if (clazz.isArray()) {
          this.actualClasses = new Class[1];
          this.actualClasses[0] = getType().getComponentType();
        } 
      } 
      this.actualClassesChecked = true;
    } 
    return this.actualClasses;
  }
}
