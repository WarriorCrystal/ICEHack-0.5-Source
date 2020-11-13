package javassist.bytecode.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class Type {
  private final CtClass clazz;
  
  private final boolean special;
  
  private static final Map prims = new IdentityHashMap<Object, Object>();
  
  public static final Type DOUBLE = new Type(CtClass.doubleType);
  
  public static final Type BOOLEAN = new Type(CtClass.booleanType);
  
  public static final Type LONG = new Type(CtClass.longType);
  
  public static final Type CHAR = new Type(CtClass.charType);
  
  public static final Type BYTE = new Type(CtClass.byteType);
  
  public static final Type SHORT = new Type(CtClass.shortType);
  
  public static final Type INTEGER = new Type(CtClass.intType);
  
  public static final Type FLOAT = new Type(CtClass.floatType);
  
  public static final Type VOID = new Type(CtClass.voidType);
  
  public static final Type UNINIT = new Type(null);
  
  public static final Type RETURN_ADDRESS = new Type(null, true);
  
  public static final Type TOP = new Type(null, true);
  
  public static final Type BOGUS = new Type(null, true);
  
  public static final Type OBJECT = lookupType("java.lang.Object");
  
  public static final Type SERIALIZABLE = lookupType("java.io.Serializable");
  
  public static final Type CLONEABLE = lookupType("java.lang.Cloneable");
  
  public static final Type THROWABLE = lookupType("java.lang.Throwable");
  
  static {
    prims.put(CtClass.doubleType, DOUBLE);
    prims.put(CtClass.longType, LONG);
    prims.put(CtClass.charType, CHAR);
    prims.put(CtClass.shortType, SHORT);
    prims.put(CtClass.intType, INTEGER);
    prims.put(CtClass.floatType, FLOAT);
    prims.put(CtClass.byteType, BYTE);
    prims.put(CtClass.booleanType, BOOLEAN);
    prims.put(CtClass.voidType, VOID);
  }
  
  public static Type get(CtClass paramCtClass) {
    Type type = (Type)prims.get(paramCtClass);
    return (type != null) ? type : new Type(paramCtClass);
  }
  
  private static Type lookupType(String paramString) {
    try {
      return new Type(ClassPool.getDefault().get(paramString));
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException);
    } 
  }
  
  Type(CtClass paramCtClass) {
    this(paramCtClass, false);
  }
  
  private Type(CtClass paramCtClass, boolean paramBoolean) {
    this.clazz = paramCtClass;
    this.special = paramBoolean;
  }
  
  boolean popChanged() {
    return false;
  }
  
  public int getSize() {
    return (this.clazz == CtClass.doubleType || this.clazz == CtClass.longType || this == TOP) ? 2 : 1;
  }
  
  public CtClass getCtClass() {
    return this.clazz;
  }
  
  public boolean isReference() {
    return (!this.special && (this.clazz == null || !this.clazz.isPrimitive()));
  }
  
  public boolean isSpecial() {
    return this.special;
  }
  
  public boolean isArray() {
    return (this.clazz != null && this.clazz.isArray());
  }
  
  public int getDimensions() {
    if (!isArray())
      return 0; 
    String str = this.clazz.getName();
    int i = str.length() - 1;
    byte b = 0;
    while (str.charAt(i) == ']') {
      i -= 2;
      b++;
    } 
    return b;
  }
  
  public Type getComponent() {
    CtClass ctClass;
    if (this.clazz == null || !this.clazz.isArray())
      return null; 
    try {
      ctClass = this.clazz.getComponentType();
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException);
    } 
    Type type = (Type)prims.get(ctClass);
    return (type != null) ? type : new Type(ctClass);
  }
  
  public boolean isAssignableFrom(Type paramType) {
    if (this == paramType)
      return true; 
    if ((paramType == UNINIT && isReference()) || (this == UNINIT && paramType.isReference()))
      return true; 
    if (paramType instanceof MultiType)
      return ((MultiType)paramType).isAssignableTo(this); 
    if (paramType instanceof MultiArrayType)
      return ((MultiArrayType)paramType).isAssignableTo(this); 
    if (this.clazz == null || this.clazz.isPrimitive())
      return false; 
    try {
      return paramType.clazz.subtypeOf(this.clazz);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  public Type merge(Type paramType) {
    if (paramType == this)
      return this; 
    if (paramType == null)
      return this; 
    if (paramType == UNINIT)
      return this; 
    if (this == UNINIT)
      return paramType; 
    if (!paramType.isReference() || !isReference())
      return BOGUS; 
    if (paramType instanceof MultiType)
      return paramType.merge(this); 
    if (paramType.isArray() && isArray())
      return mergeArray(paramType); 
    try {
      return mergeClasses(paramType);
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException);
    } 
  }
  
  Type getRootComponent(Type paramType) {
    while (paramType.isArray())
      paramType = paramType.getComponent(); 
    return paramType;
  }
  
  private Type createArray(Type paramType, int paramInt) {
    Type type;
    if (paramType instanceof MultiType)
      return new MultiArrayType((MultiType)paramType, paramInt); 
    String str = arrayName(paramType.clazz.getName(), paramInt);
    try {
      type = get(getClassPool(paramType).get(str));
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException);
    } 
    return type;
  }
  
  String arrayName(String paramString, int paramInt) {
    int i = paramString.length();
    int j = i + paramInt * 2;
    char[] arrayOfChar = new char[j];
    paramString.getChars(0, i, arrayOfChar, 0);
    while (i < j) {
      arrayOfChar[i++] = '[';
      arrayOfChar[i++] = ']';
    } 
    paramString = new String(arrayOfChar);
    return paramString;
  }
  
  private ClassPool getClassPool(Type paramType) {
    ClassPool classPool = paramType.clazz.getClassPool();
    return (classPool != null) ? classPool : ClassPool.getDefault();
  }
  
  private Type mergeArray(Type paramType) {
    Type type3;
    int k;
    Type type1 = getRootComponent(paramType);
    Type type2 = getRootComponent(this);
    int i = paramType.getDimensions();
    int j = getDimensions();
    if (i == j) {
      type3 = type2.merge(type1);
      if (type3 == BOGUS)
        return OBJECT; 
      return createArray(type3, j);
    } 
    if (i < j) {
      type3 = type1;
      k = i;
    } else {
      type3 = type2;
      k = j;
    } 
    if (eq(CLONEABLE.clazz, type3.clazz) || eq(SERIALIZABLE.clazz, type3.clazz))
      return createArray(type3, k); 
    return createArray(OBJECT, k);
  }
  
  private static CtClass findCommonSuperClass(CtClass paramCtClass1, CtClass paramCtClass2) throws NotFoundException {
    CtClass ctClass1 = paramCtClass1;
    CtClass ctClass2 = paramCtClass2;
    CtClass ctClass3 = ctClass2;
    CtClass ctClass4 = ctClass1;
    while (true) {
      if (eq(ctClass1, ctClass2) && ctClass1.getSuperclass() != null)
        return ctClass1; 
      CtClass ctClass5 = ctClass1.getSuperclass();
      CtClass ctClass6 = ctClass2.getSuperclass();
      if (ctClass6 == null) {
        ctClass2 = ctClass3;
        break;
      } 
      if (ctClass5 == null) {
        ctClass1 = ctClass4;
        ctClass4 = ctClass3;
        ctClass3 = ctClass1;
        ctClass1 = ctClass2;
        ctClass2 = ctClass3;
        break;
      } 
      ctClass1 = ctClass5;
      ctClass2 = ctClass6;
    } 
    while (true) {
      ctClass1 = ctClass1.getSuperclass();
      if (ctClass1 == null)
        break; 
      ctClass4 = ctClass4.getSuperclass();
    } 
    ctClass1 = ctClass4;
    while (!eq(ctClass1, ctClass2)) {
      ctClass1 = ctClass1.getSuperclass();
      ctClass2 = ctClass2.getSuperclass();
    } 
    return ctClass1;
  }
  
  private Type mergeClasses(Type paramType) throws NotFoundException {
    CtClass ctClass = findCommonSuperClass(this.clazz, paramType.clazz);
    if (ctClass.getSuperclass() == null) {
      Map map1 = findCommonInterfaces(paramType);
      if (map1.size() == 1)
        return new Type(map1.values().iterator().next()); 
      if (map1.size() > 1)
        return new MultiType(map1); 
      return new Type(ctClass);
    } 
    Map map = findExclusiveDeclaredInterfaces(paramType, ctClass);
    if (map.size() > 0)
      return new MultiType(map, new Type(ctClass)); 
    return new Type(ctClass);
  }
  
  private Map findCommonInterfaces(Type paramType) {
    Map map1 = getAllInterfaces(paramType.clazz, null);
    Map map2 = getAllInterfaces(this.clazz, null);
    return findCommonInterfaces(map1, map2);
  }
  
  private Map findExclusiveDeclaredInterfaces(Type paramType, CtClass paramCtClass) {
    Map map1 = getDeclaredInterfaces(paramType.clazz, null);
    Map map2 = getDeclaredInterfaces(this.clazz, null);
    Map map3 = getAllInterfaces(paramCtClass, null);
    Iterator<Object> iterator = map3.keySet().iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      map1.remove(object);
      map2.remove(object);
    } 
    return findCommonInterfaces(map1, map2);
  }
  
  Map findCommonInterfaces(Map paramMap1, Map paramMap2) {
    Iterator<?> iterator = paramMap2.keySet().iterator();
    while (iterator.hasNext()) {
      if (!paramMap1.containsKey(iterator.next()))
        iterator.remove(); 
    } 
    iterator = (new ArrayList(paramMap2.values())).iterator();
    while (iterator.hasNext()) {
      CtClass arrayOfCtClass[], ctClass = (CtClass)iterator.next();
      try {
        arrayOfCtClass = ctClass.getInterfaces();
      } catch (NotFoundException notFoundException) {
        throw new RuntimeException(notFoundException);
      } 
      for (byte b = 0; b < arrayOfCtClass.length; b++)
        paramMap2.remove(arrayOfCtClass[b].getName()); 
    } 
    return paramMap2;
  }
  
  Map getAllInterfaces(CtClass paramCtClass, Map<Object, Object> paramMap) {
    if (paramMap == null)
      paramMap = new HashMap<Object, Object>(); 
    if (paramCtClass.isInterface())
      paramMap.put(paramCtClass.getName(), paramCtClass); 
    do {
      try {
        CtClass[] arrayOfCtClass = paramCtClass.getInterfaces();
        for (byte b = 0; b < arrayOfCtClass.length; b++) {
          CtClass ctClass = arrayOfCtClass[b];
          paramMap.put(ctClass.getName(), ctClass);
          getAllInterfaces(ctClass, paramMap);
        } 
        paramCtClass = paramCtClass.getSuperclass();
      } catch (NotFoundException notFoundException) {
        throw new RuntimeException(notFoundException);
      } 
    } while (paramCtClass != null);
    return paramMap;
  }
  
  Map getDeclaredInterfaces(CtClass paramCtClass, Map<Object, Object> paramMap) {
    CtClass[] arrayOfCtClass;
    if (paramMap == null)
      paramMap = new HashMap<Object, Object>(); 
    if (paramCtClass.isInterface())
      paramMap.put(paramCtClass.getName(), paramCtClass); 
    try {
      arrayOfCtClass = paramCtClass.getInterfaces();
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException);
    } 
    for (byte b = 0; b < arrayOfCtClass.length; b++) {
      CtClass ctClass = arrayOfCtClass[b];
      paramMap.put(ctClass.getName(), ctClass);
      getDeclaredInterfaces(ctClass, paramMap);
    } 
    return paramMap;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Type))
      return false; 
    return (paramObject.getClass() == getClass() && eq(this.clazz, ((Type)paramObject).clazz));
  }
  
  static boolean eq(CtClass paramCtClass1, CtClass paramCtClass2) {
    return (paramCtClass1 == paramCtClass2 || (paramCtClass1 != null && paramCtClass2 != null && paramCtClass1.getName().equals(paramCtClass2.getName())));
  }
  
  public String toString() {
    if (this == BOGUS)
      return "BOGUS"; 
    if (this == UNINIT)
      return "UNINIT"; 
    if (this == RETURN_ADDRESS)
      return "RETURN ADDRESS"; 
    if (this == TOP)
      return "TOP"; 
    return (this.clazz == null) ? "null" : this.clazz.getName();
  }
}
