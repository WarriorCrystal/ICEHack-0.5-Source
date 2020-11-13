package javassist.bytecode.analysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javassist.CtClass;

public class MultiType extends Type {
  private Map interfaces;
  
  private Type resolved;
  
  private Type potentialClass;
  
  private MultiType mergeSource;
  
  private boolean changed = false;
  
  public MultiType(Map paramMap) {
    this(paramMap, (Type)null);
  }
  
  public MultiType(Map paramMap, Type paramType) {
    super(null);
    this.interfaces = paramMap;
    this.potentialClass = paramType;
  }
  
  public CtClass getCtClass() {
    if (this.resolved != null)
      return this.resolved.getCtClass(); 
    return Type.OBJECT.getCtClass();
  }
  
  public Type getComponent() {
    return null;
  }
  
  public int getSize() {
    return 1;
  }
  
  public boolean isArray() {
    return false;
  }
  
  boolean popChanged() {
    boolean bool = this.changed;
    this.changed = false;
    return bool;
  }
  
  public boolean isAssignableFrom(Type paramType) {
    throw new UnsupportedOperationException("Not implemented");
  }
  
  public boolean isAssignableTo(Type paramType) {
    if (this.resolved != null)
      return paramType.isAssignableFrom(this.resolved); 
    if (Type.OBJECT.equals(paramType))
      return true; 
    if (this.potentialClass != null && !paramType.isAssignableFrom(this.potentialClass))
      this.potentialClass = null; 
    Map map = mergeMultiAndSingle(this, paramType);
    if (map.size() == 1 && this.potentialClass == null) {
      this.resolved = Type.get(map.values().iterator().next());
      propogateResolved();
      return true;
    } 
    if (map.size() >= 1) {
      this.interfaces = map;
      propogateState();
      return true;
    } 
    if (this.potentialClass != null) {
      this.resolved = this.potentialClass;
      propogateResolved();
      return true;
    } 
    return false;
  }
  
  private void propogateState() {
    MultiType multiType = this.mergeSource;
    while (multiType != null) {
      multiType.interfaces = this.interfaces;
      multiType.potentialClass = this.potentialClass;
      multiType = multiType.mergeSource;
    } 
  }
  
  private void propogateResolved() {
    MultiType multiType = this.mergeSource;
    while (multiType != null) {
      multiType.resolved = this.resolved;
      multiType = multiType.mergeSource;
    } 
  }
  
  public boolean isReference() {
    return true;
  }
  
  private Map getAllMultiInterfaces(MultiType paramMultiType) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    Iterator<CtClass> iterator = paramMultiType.interfaces.values().iterator();
    while (iterator.hasNext()) {
      CtClass ctClass = iterator.next();
      hashMap.put(ctClass.getName(), ctClass);
      getAllInterfaces(ctClass, hashMap);
    } 
    return hashMap;
  }
  
  private Map mergeMultiInterfaces(MultiType paramMultiType1, MultiType paramMultiType2) {
    Map map1 = getAllMultiInterfaces(paramMultiType1);
    Map map2 = getAllMultiInterfaces(paramMultiType2);
    return findCommonInterfaces(map1, map2);
  }
  
  private Map mergeMultiAndSingle(MultiType paramMultiType, Type paramType) {
    Map map1 = getAllMultiInterfaces(paramMultiType);
    Map map2 = getAllInterfaces(paramType.getCtClass(), null);
    return findCommonInterfaces(map1, map2);
  }
  
  private boolean inMergeSource(MultiType paramMultiType) {
    while (paramMultiType != null) {
      if (paramMultiType == this)
        return true; 
      paramMultiType = paramMultiType.mergeSource;
    } 
    return false;
  }
  
  public Type merge(Type paramType) {
    Map map;
    if (this == paramType)
      return this; 
    if (paramType == UNINIT)
      return this; 
    if (paramType == BOGUS)
      return BOGUS; 
    if (paramType == null)
      return this; 
    if (this.resolved != null)
      return this.resolved.merge(paramType); 
    if (this.potentialClass != null) {
      Type type = this.potentialClass.merge(paramType);
      if (!type.equals(this.potentialClass) || type.popChanged()) {
        this.potentialClass = Type.OBJECT.equals(type) ? null : type;
        this.changed = true;
      } 
    } 
    if (paramType instanceof MultiType) {
      MultiType multiType = (MultiType)paramType;
      if (multiType.resolved != null) {
        map = mergeMultiAndSingle(this, multiType.resolved);
      } else {
        map = mergeMultiInterfaces(multiType, this);
        if (!inMergeSource(multiType))
          this.mergeSource = multiType; 
      } 
    } else {
      map = mergeMultiAndSingle(this, paramType);
    } 
    if (map.size() > 1 || (map.size() == 1 && this.potentialClass != null)) {
      if (map.size() != this.interfaces.size()) {
        this.changed = true;
      } else if (!this.changed) {
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
          if (!this.interfaces.containsKey(iterator.next()))
            this.changed = true; 
        } 
      } 
      this.interfaces = map;
      propogateState();
      return this;
    } 
    if (map.size() == 1) {
      this.resolved = Type.get(map.values().iterator().next());
    } else if (this.potentialClass != null) {
      this.resolved = this.potentialClass;
    } else {
      this.resolved = OBJECT;
    } 
    propogateResolved();
    return this.resolved;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof MultiType))
      return false; 
    MultiType multiType = (MultiType)paramObject;
    if (this.resolved != null)
      return this.resolved.equals(multiType.resolved); 
    if (multiType.resolved != null)
      return false; 
    return this.interfaces.keySet().equals(multiType.interfaces.keySet());
  }
  
  public String toString() {
    if (this.resolved != null)
      return this.resolved.toString(); 
    StringBuffer stringBuffer = new StringBuffer("{");
    Iterator iterator = this.interfaces.keySet().iterator();
    while (iterator.hasNext()) {
      stringBuffer.append(iterator.next());
      stringBuffer.append(", ");
    } 
    stringBuffer.setLength(stringBuffer.length() - 2);
    if (this.potentialClass != null)
      stringBuffer.append(", *").append(this.potentialClass.toString()); 
    stringBuffer.append("}");
    return stringBuffer.toString();
  }
}
