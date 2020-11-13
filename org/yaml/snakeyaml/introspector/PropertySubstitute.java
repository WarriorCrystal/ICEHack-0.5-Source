package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.error.YAMLException;

public class PropertySubstitute extends Property {
  private static final Logger log = Logger.getLogger(PropertySubstitute.class.getPackage()
      .getName());
  
  protected Class<?> targetType;
  
  private final String readMethod;
  
  private final String writeMethod;
  
  private transient Method read;
  
  private transient Method write;
  
  private Field field;
  
  protected Class<?>[] parameters;
  
  private Property delegate;
  
  private boolean filler;
  
  public PropertySubstitute(String paramString1, Class<?> paramClass, String paramString2, String paramString3, Class<?>... paramVarArgs) {
    super(paramString1, paramClass);
    this.readMethod = paramString2;
    this.writeMethod = paramString3;
    setActualTypeArguments(paramVarArgs);
    this.filler = false;
  }
  
  public PropertySubstitute(String paramString, Class<?> paramClass, Class<?>... paramVarArgs) {
    this(paramString, paramClass, null, null, paramVarArgs);
  }
  
  public Class<?>[] getActualTypeArguments() {
    if (this.parameters == null && this.delegate != null)
      return this.delegate.getActualTypeArguments(); 
    return this.parameters;
  }
  
  public void setActualTypeArguments(Class<?>... paramVarArgs) {
    if (paramVarArgs != null && paramVarArgs.length > 0) {
      this.parameters = paramVarArgs;
    } else {
      this.parameters = null;
    } 
  }
  
  public void set(Object paramObject1, Object paramObject2) throws Exception {
    if (this.write != null) {
      if (!this.filler) {
        this.write.invoke(paramObject1, new Object[] { paramObject2 });
      } else if (paramObject2 != null) {
        if (paramObject2 instanceof Collection) {
          Collection collection = (Collection)paramObject2;
          for (Object object : collection) {
            this.write.invoke(paramObject1, new Object[] { object });
          } 
        } else if (paramObject2 instanceof Map) {
          Map map = (Map)paramObject2;
          for (Map.Entry entry : map.entrySet()) {
            this.write.invoke(paramObject1, new Object[] { entry.getKey(), entry.getValue() });
          } 
        } else if (paramObject2.getClass().isArray()) {
          int i = Array.getLength(paramObject2);
          for (byte b = 0; b < i; b++) {
            this.write.invoke(paramObject1, new Object[] { Array.get(paramObject2, b) });
          } 
        } 
      } 
    } else if (this.field != null) {
      this.field.set(paramObject1, paramObject2);
    } else if (this.delegate != null) {
      this.delegate.set(paramObject1, paramObject2);
    } else {
      log.warning("No setter/delegate for '" + getName() + "' on object " + paramObject1);
    } 
  }
  
  public Object get(Object paramObject) {
    try {
      if (this.read != null)
        return this.read.invoke(paramObject, new Object[0]); 
      if (this.field != null)
        return this.field.get(paramObject); 
    } catch (Exception exception) {
      throw new YAMLException("Unable to find getter for property '" + getName() + "' on object " + paramObject + ":" + exception);
    } 
    if (this.delegate != null)
      return this.delegate.get(paramObject); 
    throw new YAMLException("No getter or delegate for property '" + getName() + "' on object " + paramObject);
  }
  
  public List<Annotation> getAnnotations() {
    Annotation[] arrayOfAnnotation = null;
    if (this.read != null) {
      arrayOfAnnotation = this.read.getAnnotations();
    } else if (this.field != null) {
      arrayOfAnnotation = this.field.getAnnotations();
    } 
    return (arrayOfAnnotation != null) ? Arrays.<Annotation>asList(arrayOfAnnotation) : this.delegate.getAnnotations();
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass) {
    A a;
    if (this.read != null) {
      a = this.read.getAnnotation((Class)paramClass);
    } else if (this.field != null) {
      a = this.field.getAnnotation((Class)paramClass);
    } else {
      a = this.delegate.getAnnotation((Class)paramClass);
    } 
    return a;
  }
  
  public void setTargetType(Class<?> paramClass) {
    if (this.targetType != paramClass) {
      this.targetType = paramClass;
      String str = getName();
      for (Class<?> clazz = paramClass; clazz != null; clazz = clazz.getSuperclass()) {
        for (Field field : clazz.getDeclaredFields()) {
          if (field.getName().equals(str)) {
            int i = field.getModifiers();
            if (!Modifier.isStatic(i) && !Modifier.isTransient(i)) {
              field.setAccessible(true);
              this.field = field;
            } 
            break;
          } 
        } 
      } 
      if (this.field == null && log.isLoggable(Level.FINE))
        log.fine(String.format("Failed to find field for %s.%s", new Object[] { paramClass.getName(), 
                getName() })); 
      if (this.readMethod != null)
        this.read = discoverMethod(paramClass, this.readMethod, new Class[0]); 
      if (this.writeMethod != null) {
        this.filler = false;
        this.write = discoverMethod(paramClass, this.writeMethod, new Class[] { getType() });
        if (this.write == null && this.parameters != null) {
          this.filler = true;
          this.write = discoverMethod(paramClass, this.writeMethod, this.parameters);
        } 
      } 
    } 
  }
  
  private Method discoverMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) {
    for (Class<?> clazz = paramClass; clazz != null; clazz = clazz.getSuperclass()) {
      for (Method method : clazz.getDeclaredMethods()) {
        if (paramString.equals(method.getName())) {
          Class[] arrayOfClass = method.getParameterTypes();
          if (arrayOfClass.length == paramVarArgs.length) {
            boolean bool = true;
            for (byte b = 0; b < arrayOfClass.length; b++) {
              if (!arrayOfClass[b].isAssignableFrom(paramVarArgs[b]))
                bool = false; 
            } 
            if (bool) {
              method.setAccessible(true);
              return method;
            } 
          } 
        } 
      } 
    } 
    if (log.isLoggable(Level.FINE))
      log.fine(String.format("Failed to find [%s(%d args)] for %s.%s", new Object[] { paramString, Integer.valueOf(paramVarArgs.length), this.targetType
              .getName(), getName() })); 
    return null;
  }
  
  public String getName() {
    String str = super.getName();
    if (str != null)
      return str; 
    return (this.delegate != null) ? this.delegate.getName() : null;
  }
  
  public Class<?> getType() {
    Class<?> clazz = super.getType();
    if (clazz != null)
      return clazz; 
    return (this.delegate != null) ? this.delegate.getType() : null;
  }
  
  public boolean isReadable() {
    return (this.read != null || this.field != null || (this.delegate != null && this.delegate.isReadable()));
  }
  
  public boolean isWritable() {
    return (this.write != null || this.field != null || (this.delegate != null && this.delegate.isWritable()));
  }
  
  public void setDelegate(Property paramProperty) {
    this.delegate = paramProperty;
    if (this.writeMethod != null && this.write == null && !this.filler) {
      this.filler = true;
      this.write = discoverMethod(this.targetType, this.writeMethod, getActualTypeArguments());
    } 
  }
}
