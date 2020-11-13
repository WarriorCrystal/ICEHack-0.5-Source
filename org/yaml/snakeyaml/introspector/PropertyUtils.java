package org.yaml.snakeyaml.introspector;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.PlatformFeatureDetector;

public class PropertyUtils {
  private static final Logger log = Logger.getLogger(PropertyUtils.class.getPackage().getName());
  
  private final Map<Class<?>, Map<String, Property>> propertiesCache = new HashMap<Class<?>, Map<String, Property>>();
  
  private final Map<Class<?>, Set<Property>> readableProperties = new HashMap<Class<?>, Set<Property>>();
  
  private BeanAccess beanAccess = BeanAccess.DEFAULT;
  
  private boolean allowReadOnlyProperties = false;
  
  private boolean skipMissingProperties = false;
  
  private PlatformFeatureDetector platformFeatureDetector;
  
  private boolean transientMethodChecked;
  
  private Method isTransientMethod;
  
  public PropertyUtils() {
    this(new PlatformFeatureDetector());
  }
  
  PropertyUtils(PlatformFeatureDetector paramPlatformFeatureDetector) {
    this.platformFeatureDetector = paramPlatformFeatureDetector;
    if (paramPlatformFeatureDetector.isRunningOnAndroid())
      this.beanAccess = BeanAccess.FIELD; 
  }
  
  protected Map<String, Property> getPropertiesMap(Class<?> paramClass, BeanAccess paramBeanAccess) {
    Class<?> clazz;
    if (this.propertiesCache.containsKey(paramClass))
      return this.propertiesCache.get(paramClass); 
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<Object, Object>();
    boolean bool = false;
    switch (paramBeanAccess) {
      case FIELD:
        for (clazz = paramClass; clazz != null; clazz = clazz.getSuperclass()) {
          for (Field field : clazz.getDeclaredFields()) {
            int i = field.getModifiers();
            if (!Modifier.isStatic(i) && !Modifier.isTransient(i) && 
              !linkedHashMap.containsKey(field.getName()))
              linkedHashMap.put(field.getName(), new FieldProperty(field)); 
          } 
        } 
        break;
      default:
        try {
          for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(paramClass)
            .getPropertyDescriptors()) {
            Method method = propertyDescriptor.getReadMethod();
            if ((method == null || !method.getName().equals("getClass")) && 
              !isTransient(propertyDescriptor))
              linkedHashMap.put(propertyDescriptor.getName(), new MethodProperty(propertyDescriptor)); 
          } 
        } catch (IntrospectionException introspectionException) {
          throw new YAMLException(introspectionException);
        } 
        for (clazz = paramClass; clazz != null; clazz = clazz.getSuperclass()) {
          for (Field field : clazz.getDeclaredFields()) {
            int i = field.getModifiers();
            if (!Modifier.isStatic(i) && !Modifier.isTransient(i))
              if (Modifier.isPublic(i)) {
                linkedHashMap.put(field.getName(), new FieldProperty(field));
              } else {
                bool = true;
              }  
          } 
        } 
        break;
    } 
    if (linkedHashMap.isEmpty() && bool)
      throw new YAMLException("No JavaBean properties found in " + paramClass.getName()); 
    this.propertiesCache.put(paramClass, linkedHashMap);
    return (Map)linkedHashMap;
  }
  
  private boolean isTransient(FeatureDescriptor paramFeatureDescriptor) {
    if (!this.transientMethodChecked) {
      this.transientMethodChecked = true;
      try {
        this.isTransientMethod = FeatureDescriptor.class.getDeclaredMethod("isTransient", new Class[0]);
        this.isTransientMethod.setAccessible(true);
      } catch (NoSuchMethodException noSuchMethodException) {
        log.fine("NoSuchMethod: FeatureDescriptor.isTransient(). Don't check it anymore.");
      } catch (SecurityException securityException) {
        securityException.printStackTrace();
        this.isTransientMethod = null;
      } 
    } 
    if (this.isTransientMethod != null) {
      try {
        return Boolean.TRUE.equals(this.isTransientMethod.invoke(paramFeatureDescriptor, new Object[0]));
      } catch (IllegalAccessException illegalAccessException) {
        illegalAccessException.printStackTrace();
      } catch (IllegalArgumentException illegalArgumentException) {
        illegalArgumentException.printStackTrace();
      } catch (InvocationTargetException invocationTargetException) {
        invocationTargetException.printStackTrace();
      } 
      this.isTransientMethod = null;
    } 
    return false;
  }
  
  public Set<Property> getProperties(Class<? extends Object> paramClass) {
    return getProperties(paramClass, this.beanAccess);
  }
  
  public Set<Property> getProperties(Class<? extends Object> paramClass, BeanAccess paramBeanAccess) {
    if (this.readableProperties.containsKey(paramClass))
      return this.readableProperties.get(paramClass); 
    Set<Property> set = createPropertySet(paramClass, paramBeanAccess);
    this.readableProperties.put(paramClass, set);
    return set;
  }
  
  protected Set<Property> createPropertySet(Class<? extends Object> paramClass, BeanAccess paramBeanAccess) {
    TreeSet<Property> treeSet = new TreeSet();
    Collection<Property> collection = getPropertiesMap(paramClass, paramBeanAccess).values();
    for (Property property : collection) {
      if (property.isReadable() && (this.allowReadOnlyProperties || property.isWritable()))
        treeSet.add(property); 
    } 
    return treeSet;
  }
  
  public Property getProperty(Class<? extends Object> paramClass, String paramString) {
    return getProperty(paramClass, paramString, this.beanAccess);
  }
  
  public Property getProperty(Class<? extends Object> paramClass, String paramString, BeanAccess paramBeanAccess) {
    Map<String, Property> map = getPropertiesMap(paramClass, paramBeanAccess);
    Property property = map.get(paramString);
    if (property == null && this.skipMissingProperties)
      property = new MissingProperty(paramString); 
    if (property == null)
      throw new YAMLException("Unable to find property '" + paramString + "' on class: " + paramClass
          .getName()); 
    return property;
  }
  
  public void setBeanAccess(BeanAccess paramBeanAccess) {
    if (this.platformFeatureDetector.isRunningOnAndroid() && paramBeanAccess != BeanAccess.FIELD)
      throw new IllegalArgumentException("JVM is Android - only BeanAccess.FIELD is available"); 
    if (this.beanAccess != paramBeanAccess) {
      this.beanAccess = paramBeanAccess;
      this.propertiesCache.clear();
      this.readableProperties.clear();
    } 
  }
  
  public void setAllowReadOnlyProperties(boolean paramBoolean) {
    if (this.allowReadOnlyProperties != paramBoolean) {
      this.allowReadOnlyProperties = paramBoolean;
      this.readableProperties.clear();
    } 
  }
  
  public boolean isAllowReadOnlyProperties() {
    return this.allowReadOnlyProperties;
  }
  
  public void setSkipMissingProperties(boolean paramBoolean) {
    if (this.skipMissingProperties != paramBoolean) {
      this.skipMissingProperties = paramBoolean;
      this.readableProperties.clear();
    } 
  }
  
  public boolean isSkipMissingProperties() {
    return this.skipMissingProperties;
  }
}
