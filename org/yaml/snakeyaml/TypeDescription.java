package org.yaml.snakeyaml;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertySubstitute;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

public class TypeDescription {
  private final Class<? extends Object> type;
  
  private Class<?> impl;
  
  private Tag tag;
  
  private transient Set<Property> dumpProperties;
  
  private transient PropertyUtils propertyUtils;
  
  private transient boolean delegatesChecked;
  
  private Map<String, PropertySubstitute> properties = Collections.emptyMap();
  
  protected Set<String> excludes = Collections.emptySet();
  
  protected String[] includes = null;
  
  protected BeanAccess beanAccess;
  
  public TypeDescription(Class<? extends Object> paramClass, Tag paramTag) {
    this(paramClass, paramTag, null);
  }
  
  public TypeDescription(Class<? extends Object> paramClass, Tag paramTag, Class<?> paramClass1) {
    this.type = paramClass;
    this.tag = paramTag;
    this.impl = paramClass1;
    this.beanAccess = null;
  }
  
  public TypeDescription(Class<? extends Object> paramClass, String paramString) {
    this(paramClass, new Tag(paramString), null);
  }
  
  public TypeDescription(Class<? extends Object> paramClass) {
    this(paramClass, (Tag)null, null);
  }
  
  public TypeDescription(Class<? extends Object> paramClass, Class<?> paramClass1) {
    this(paramClass, null, paramClass1);
  }
  
  public Tag getTag() {
    return this.tag;
  }
  
  public void setTag(Tag paramTag) {
    this.tag = paramTag;
  }
  
  public void setTag(String paramString) {
    setTag(new Tag(paramString));
  }
  
  public Class<? extends Object> getType() {
    return this.type;
  }
  
  @Deprecated
  public void putListPropertyType(String paramString, Class<? extends Object> paramClass) {
    addPropertyParameters(paramString, new Class[] { paramClass });
  }
  
  @Deprecated
  public Class<? extends Object> getListPropertyType(String paramString) {
    if (this.properties.containsKey(paramString)) {
      Class[] arrayOfClass = ((PropertySubstitute)this.properties.get(paramString)).getActualTypeArguments();
      if (arrayOfClass != null && arrayOfClass.length > 0)
        return arrayOfClass[0]; 
    } 
    return null;
  }
  
  @Deprecated
  public void putMapPropertyType(String paramString, Class<? extends Object> paramClass1, Class<? extends Object> paramClass2) {
    addPropertyParameters(paramString, new Class[] { paramClass1, paramClass2 });
  }
  
  @Deprecated
  public Class<? extends Object> getMapKeyType(String paramString) {
    if (this.properties.containsKey(paramString)) {
      Class[] arrayOfClass = ((PropertySubstitute)this.properties.get(paramString)).getActualTypeArguments();
      if (arrayOfClass != null && arrayOfClass.length > 0)
        return arrayOfClass[0]; 
    } 
    return null;
  }
  
  @Deprecated
  public Class<? extends Object> getMapValueType(String paramString) {
    if (this.properties.containsKey(paramString)) {
      Class[] arrayOfClass = ((PropertySubstitute)this.properties.get(paramString)).getActualTypeArguments();
      if (arrayOfClass != null && arrayOfClass.length > 1)
        return arrayOfClass[1]; 
    } 
    return null;
  }
  
  public void addPropertyParameters(String paramString, Class<?>... paramVarArgs) {
    if (!this.properties.containsKey(paramString)) {
      substituteProperty(paramString, null, null, null, paramVarArgs);
    } else {
      PropertySubstitute propertySubstitute = this.properties.get(paramString);
      propertySubstitute.setActualTypeArguments(paramVarArgs);
    } 
  }
  
  public String toString() {
    return "TypeDescription for " + getType() + " (tag='" + getTag() + "')";
  }
  
  private void checkDelegates() {
    Collection<PropertySubstitute> collection = this.properties.values();
    for (PropertySubstitute propertySubstitute : collection) {
      try {
        propertySubstitute.setDelegate(discoverProperty(propertySubstitute.getName()));
      } catch (YAMLException yAMLException) {}
    } 
    this.delegatesChecked = true;
  }
  
  private Property discoverProperty(String paramString) {
    if (this.propertyUtils != null) {
      if (this.beanAccess == null)
        return this.propertyUtils.getProperty(this.type, paramString); 
      return this.propertyUtils.getProperty(this.type, paramString, this.beanAccess);
    } 
    return null;
  }
  
  public Property getProperty(String paramString) {
    if (!this.delegatesChecked)
      checkDelegates(); 
    return this.properties.containsKey(paramString) ? (Property)this.properties.get(paramString) : discoverProperty(paramString);
  }
  
  public void substituteProperty(String paramString1, Class<?> paramClass, String paramString2, String paramString3, Class<?>... paramVarArgs) {
    substituteProperty(new PropertySubstitute(paramString1, paramClass, paramString2, paramString3, paramVarArgs));
  }
  
  public void substituteProperty(PropertySubstitute paramPropertySubstitute) {
    if (Collections.EMPTY_MAP == this.properties)
      this.properties = new LinkedHashMap<String, PropertySubstitute>(); 
    paramPropertySubstitute.setTargetType(this.type);
    this.properties.put(paramPropertySubstitute.getName(), paramPropertySubstitute);
  }
  
  public void setPropertyUtils(PropertyUtils paramPropertyUtils) {
    this.propertyUtils = paramPropertyUtils;
  }
  
  public void setIncludes(String... paramVarArgs) {
    this.includes = (paramVarArgs != null && paramVarArgs.length > 0) ? paramVarArgs : null;
  }
  
  public void setExcludes(String... paramVarArgs) {
    if (paramVarArgs != null && paramVarArgs.length > 0) {
      this.excludes = new HashSet<String>();
      for (String str : paramVarArgs)
        this.excludes.add(str); 
    } else {
      this.excludes = Collections.emptySet();
    } 
  }
  
  public Set<Property> getProperties() {
    if (this.dumpProperties != null)
      return this.dumpProperties; 
    if (this.propertyUtils != null) {
      if (this.includes != null) {
        this.dumpProperties = new LinkedHashSet<Property>();
        for (String str : this.includes) {
          if (!this.excludes.contains(str))
            this.dumpProperties.add(getProperty(str)); 
        } 
        return this.dumpProperties;
      } 
      Set<Property> set = (this.beanAccess == null) ? this.propertyUtils.getProperties(this.type) : this.propertyUtils.getProperties(this.type, this.beanAccess);
      if (this.properties.isEmpty()) {
        if (this.excludes.isEmpty())
          return this.dumpProperties = set; 
        this.dumpProperties = new LinkedHashSet<Property>();
        for (Property property : set) {
          if (!this.excludes.contains(property.getName()))
            this.dumpProperties.add(property); 
        } 
        return this.dumpProperties;
      } 
      if (!this.delegatesChecked)
        checkDelegates(); 
      this.dumpProperties = new LinkedHashSet<Property>();
      for (Property property : this.properties.values()) {
        if (!this.excludes.contains(property.getName()) && property.isReadable())
          this.dumpProperties.add(property); 
      } 
      for (Property property : set) {
        if (!this.excludes.contains(property.getName()))
          this.dumpProperties.add(property); 
      } 
      return this.dumpProperties;
    } 
    return null;
  }
  
  public boolean setupPropertyType(String paramString, Node paramNode) {
    return false;
  }
  
  public boolean setProperty(Object paramObject1, String paramString, Object paramObject2) throws Exception {
    return false;
  }
  
  public Object newInstance(Node paramNode) {
    if (this.impl != null)
      try {
        Constructor<?> constructor = this.impl.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        return constructor.newInstance(new Object[0]);
      } catch (Exception exception) {
        exception.printStackTrace();
        this.impl = null;
      }  
    return null;
  }
  
  public Object newInstance(String paramString, Node paramNode) {
    return null;
  }
  
  public Object finalizeConstruction(Object paramObject) {
    return paramObject;
  }
}
