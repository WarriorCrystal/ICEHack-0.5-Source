package net.jodah.typetools;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import sun.misc.Unsafe;

public final class TypeResolver {
  private static final Map<Class<?>, Reference<Map<TypeVariable<?>, Type>>> TYPE_VARIABLE_CACHE = Collections.synchronizedMap(new WeakHashMap<Class<?>, Reference<Map<TypeVariable<?>, Type>>>());
  
  private static volatile boolean CACHE_ENABLED = true;
  
  private static boolean RESOLVES_LAMBDAS;
  
  private static Method GET_CONSTANT_POOL;
  
  private static Method GET_CONSTANT_POOL_SIZE;
  
  private static Method GET_CONSTANT_POOL_METHOD_AT;
  
  private static final Map<String, Method> OBJECT_METHODS = new HashMap<String, Method>();
  
  private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS;
  
  private static final Double JAVA_VERSION = Double.valueOf(Double.parseDouble(System.getProperty("java.specification.version", "0")));
  
  static {
    try {
      Unsafe unsafe = AccessController.<Unsafe>doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
            public Unsafe run() throws Exception {
              Field field = Unsafe.class.getDeclaredField("theUnsafe");
              field.setAccessible(true);
              return (Unsafe)field.get((Object)null);
            }
          });
      GET_CONSTANT_POOL = Class.class.getDeclaredMethod("getConstantPool", new Class[0]);
      String str = (JAVA_VERSION.doubleValue() < 9.0D) ? "sun.reflect.ConstantPool" : "jdk.internal.reflect.ConstantPool";
      Class<?> clazz = Class.forName(str);
      GET_CONSTANT_POOL_SIZE = clazz.getDeclaredMethod("getSize", new Class[0]);
      GET_CONSTANT_POOL_METHOD_AT = clazz.getDeclaredMethod("getMethodAt", new Class[] { int.class });
      Field field = AccessibleObject.class.getDeclaredField("override");
      long l = unsafe.objectFieldOffset(field);
      unsafe.putBoolean(GET_CONSTANT_POOL, l, true);
      unsafe.putBoolean(GET_CONSTANT_POOL_SIZE, l, true);
      unsafe.putBoolean(GET_CONSTANT_POOL_METHOD_AT, l, true);
      Object object = GET_CONSTANT_POOL.invoke(Object.class, new Object[0]);
      GET_CONSTANT_POOL_SIZE.invoke(object, new Object[0]);
      for (Method method : Object.class.getDeclaredMethods())
        OBJECT_METHODS.put(method.getName(), method); 
      RESOLVES_LAMBDAS = true;
    } catch (Exception exception) {}
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put(boolean.class, Boolean.class);
    hashMap.put(byte.class, Byte.class);
    hashMap.put(char.class, Character.class);
    hashMap.put(double.class, Double.class);
    hashMap.put(float.class, Float.class);
    hashMap.put(int.class, Integer.class);
    hashMap.put(long.class, Long.class);
    hashMap.put(short.class, Short.class);
    hashMap.put(void.class, Void.class);
    PRIMITIVE_WRAPPERS = Collections.unmodifiableMap(hashMap);
  }
  
  public static final class Unknown {}
  
  public static void enableCache() {
    CACHE_ENABLED = true;
  }
  
  public static void disableCache() {
    TYPE_VARIABLE_CACHE.clear();
    CACHE_ENABLED = false;
  }
  
  public static <T, S extends T> Class<?> resolveRawArgument(Class<T> paramClass, Class<S> paramClass1) {
    return resolveRawArgument(resolveGenericType(paramClass, paramClass1), paramClass1);
  }
  
  public static Class<?> resolveRawArgument(Type paramType, Class<?> paramClass) {
    Class[] arrayOfClass = resolveRawArguments(paramType, paramClass);
    if (arrayOfClass == null)
      return Unknown.class; 
    if (arrayOfClass.length != 1)
      throw new IllegalArgumentException("Expected 1 argument for generic type " + paramType + " but found " + arrayOfClass.length); 
    return arrayOfClass[0];
  }
  
  public static <T, S extends T> Class<?>[] resolveRawArguments(Class<T> paramClass, Class<S> paramClass1) {
    return resolveRawArguments(resolveGenericType(paramClass, paramClass1), paramClass1);
  }
  
  public static Class<?>[] resolveRawArguments(Type paramType, Class<?> paramClass) {
    Class[] arrayOfClass = null;
    Class<?> clazz = null;
    if (RESOLVES_LAMBDAS && paramClass.isSynthetic()) {
      Class<?> clazz1 = (paramType instanceof ParameterizedType && ((ParameterizedType)paramType).getRawType() instanceof Class) ? (Class)((ParameterizedType)paramType).getRawType() : ((paramType instanceof Class) ? (Class)paramType : null);
      if (clazz1 != null && clazz1.isInterface())
        clazz = clazz1; 
    } 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      Type[] arrayOfType = parameterizedType.getActualTypeArguments();
      arrayOfClass = new Class[arrayOfType.length];
      for (byte b = 0; b < arrayOfType.length; b++)
        arrayOfClass[b] = resolveRawClass(arrayOfType[b], paramClass, clazz); 
    } else if (paramType instanceof TypeVariable) {
      arrayOfClass = new Class[1];
      arrayOfClass[0] = resolveRawClass(paramType, paramClass, clazz);
    } else if (paramType instanceof Class) {
      TypeVariable[] arrayOfTypeVariable = ((Class)paramType).getTypeParameters();
      arrayOfClass = new Class[arrayOfTypeVariable.length];
      for (byte b = 0; b < arrayOfTypeVariable.length; b++)
        arrayOfClass[b] = resolveRawClass(arrayOfTypeVariable[b], paramClass, clazz); 
    } 
    return arrayOfClass;
  }
  
  public static Type resolveGenericType(Class<?> paramClass, Type paramType) {
    Class clazz;
    if (paramType instanceof ParameterizedType) {
      clazz = (Class)((ParameterizedType)paramType).getRawType();
    } else {
      clazz = (Class)paramType;
    } 
    if (paramClass.equals(clazz))
      return paramType; 
    if (paramClass.isInterface())
      for (Type type4 : clazz.getGenericInterfaces()) {
        Type type3;
        if (type4 != null && !type4.equals(Object.class) && (
          type3 = resolveGenericType(paramClass, type4)) != null)
          return type3; 
      }  
    Type type2 = clazz.getGenericSuperclass();
    Type type1;
    if (type2 != null && !type2.equals(Object.class) && (
      type1 = resolveGenericType(paramClass, type2)) != null)
      return type1; 
    return null;
  }
  
  public static Class<?> resolveRawClass(Type paramType, Class<?> paramClass) {
    return resolveRawClass(paramType, paramClass, null);
  }
  
  private static Class<?> resolveRawClass(Type paramType, Class<?> paramClass1, Class<?> paramClass2) {
    if (paramType instanceof Class)
      return (Class)paramType; 
    if (paramType instanceof ParameterizedType)
      return resolveRawClass(((ParameterizedType)paramType).getRawType(), paramClass1, paramClass2); 
    if (paramType instanceof GenericArrayType) {
      GenericArrayType genericArrayType = (GenericArrayType)paramType;
      Class<?> clazz = resolveRawClass(genericArrayType.getGenericComponentType(), paramClass1, paramClass2);
      return Array.newInstance(clazz, 0).getClass();
    } 
    if (paramType instanceof TypeVariable) {
      TypeVariable<?> typeVariable = (TypeVariable)paramType;
      paramType = getTypeVariableMap(paramClass1, paramClass2).get(typeVariable);
      paramType = (paramType == null) ? resolveBound(typeVariable) : resolveRawClass(paramType, paramClass1, paramClass2);
    } 
    return (paramType instanceof Class) ? (Class)paramType : Unknown.class;
  }
  
  private static Map<TypeVariable<?>, Type> getTypeVariableMap(Class<?> paramClass1, Class<?> paramClass2) {
    Reference<Map> reference = (Reference)TYPE_VARIABLE_CACHE.get(paramClass1);
    Map<Object, Object> map = (reference != null) ? reference.get() : null;
    if (map == null) {
      map = new HashMap<Object, Object>();
      if (paramClass2 != null)
        populateLambdaArgs(paramClass2, paramClass1, (Map)map); 
      populateSuperTypeArgs(paramClass1.getGenericInterfaces(), (Map)map, (paramClass2 != null));
      Type type = paramClass1.getGenericSuperclass();
      Class<?> clazz = paramClass1.getSuperclass();
      while (clazz != null && !Object.class.equals(clazz)) {
        if (type instanceof ParameterizedType)
          populateTypeArgs((ParameterizedType)type, (Map)map, false); 
        populateSuperTypeArgs(clazz.getGenericInterfaces(), (Map)map, false);
        type = clazz.getGenericSuperclass();
        clazz = clazz.getSuperclass();
      } 
      clazz = paramClass1;
      while (clazz.isMemberClass()) {
        type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType)
          populateTypeArgs((ParameterizedType)type, (Map)map, (paramClass2 != null)); 
        clazz = clazz.getEnclosingClass();
      } 
      if (CACHE_ENABLED)
        TYPE_VARIABLE_CACHE.put(paramClass1, new WeakReference(map)); 
    } 
    return (Map)map;
  }
  
  private static void populateSuperTypeArgs(Type[] paramArrayOfType, Map<TypeVariable<?>, Type> paramMap, boolean paramBoolean) {
    for (Type type : paramArrayOfType) {
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType)type;
        if (!paramBoolean)
          populateTypeArgs(parameterizedType, paramMap, paramBoolean); 
        Type type1 = parameterizedType.getRawType();
        if (type1 instanceof Class)
          populateSuperTypeArgs(((Class)type1).getGenericInterfaces(), paramMap, paramBoolean); 
        if (paramBoolean)
          populateTypeArgs(parameterizedType, paramMap, paramBoolean); 
      } else if (type instanceof Class) {
        populateSuperTypeArgs(((Class)type).getGenericInterfaces(), paramMap, paramBoolean);
      } 
    } 
  }
  
  private static void populateTypeArgs(ParameterizedType paramParameterizedType, Map<TypeVariable<?>, Type> paramMap, boolean paramBoolean) {
    if (paramParameterizedType.getRawType() instanceof Class) {
      TypeVariable[] arrayOfTypeVariable = ((Class)paramParameterizedType.getRawType()).getTypeParameters();
      Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
      if (paramParameterizedType.getOwnerType() != null) {
        Type type = paramParameterizedType.getOwnerType();
        if (type instanceof ParameterizedType)
          populateTypeArgs((ParameterizedType)type, paramMap, paramBoolean); 
      } 
      for (byte b = 0; b < arrayOfType.length; b++) {
        TypeVariable<?> typeVariable = arrayOfTypeVariable[b];
        Type type = arrayOfType[b];
        if (type instanceof Class) {
          paramMap.put(typeVariable, type);
          continue;
        } 
        if (type instanceof GenericArrayType) {
          paramMap.put(typeVariable, type);
          continue;
        } 
        if (type instanceof ParameterizedType) {
          paramMap.put(typeVariable, type);
          continue;
        } 
        if (type instanceof TypeVariable) {
          TypeVariable<?> typeVariable1 = (TypeVariable)type;
          if (paramBoolean) {
            Type type2 = paramMap.get(typeVariable);
            if (type2 != null) {
              paramMap.put(typeVariable1, type2);
              continue;
            } 
          } 
          Type type1 = paramMap.get(typeVariable1);
          if (type1 == null)
            type1 = resolveBound(typeVariable1); 
          paramMap.put(typeVariable, type1);
        } 
        continue;
      } 
    } 
  }
  
  public static Type resolveBound(TypeVariable<?> paramTypeVariable) {
    Type[] arrayOfType = paramTypeVariable.getBounds();
    if (arrayOfType.length == 0)
      return Unknown.class; 
    Type type = arrayOfType[0];
    if (type instanceof TypeVariable)
      type = resolveBound((TypeVariable)type); 
    return (type == Object.class) ? Unknown.class : type;
  }
  
  private static void populateLambdaArgs(Class<?> paramClass1, Class<?> paramClass2, Map<TypeVariable<?>, Type> paramMap) {
    if (RESOLVES_LAMBDAS)
      for (Method method : paramClass1.getMethods()) {
        if (!isDefaultMethod(method) && !Modifier.isStatic(method.getModifiers()) && !method.isBridge()) {
          Method method1 = OBJECT_METHODS.get(method.getName());
          if (method1 == null || !Arrays.equals((Object[])method.getTypeParameters(), (Object[])method1.getTypeParameters())) {
            Type type = method.getGenericReturnType();
            Type[] arrayOfType = method.getGenericParameterTypes();
            Member member = getMemberRef(paramClass2);
            if (member == null)
              return; 
            if (type instanceof TypeVariable) {
              Class<?> clazz = (member instanceof Method) ? ((Method)member).getReturnType() : ((Constructor)member).getDeclaringClass();
              clazz = wrapPrimitives(clazz);
              if (!clazz.equals(Void.class))
                paramMap.put((TypeVariable)type, clazz); 
            } 
            Class<?>[] arrayOfClass = (member instanceof Method) ? ((Method)member).getParameterTypes() : ((Constructor)member).getParameterTypes();
            byte b1 = 0;
            if (arrayOfType.length > 0 && arrayOfType[0] instanceof TypeVariable && arrayOfType.length == arrayOfClass.length + 1) {
              Class<?> clazz = member.getDeclaringClass();
              paramMap.put((TypeVariable)arrayOfType[0], clazz);
              b1 = 1;
            } 
            int i = 0;
            if (arrayOfType.length < arrayOfClass.length)
              i = arrayOfClass.length - arrayOfType.length; 
            for (byte b2 = 0; b2 + i < arrayOfClass.length; b2++) {
              if (arrayOfType[b2] instanceof TypeVariable)
                paramMap.put((TypeVariable)arrayOfType[b2 + b1], wrapPrimitives(arrayOfClass[b2 + i])); 
            } 
            return;
          } 
        } 
      }  
  }
  
  private static boolean isDefaultMethod(Method paramMethod) {
    return (JAVA_VERSION.doubleValue() >= 1.8D && paramMethod.isDefault());
  }
  
  private static Member getMemberRef(Class<?> paramClass) {
    Object object;
    try {
      object = GET_CONSTANT_POOL.invoke(paramClass, new Object[0]);
    } catch (Exception exception) {
      return null;
    } 
    Member member = null;
    for (int i = getConstantPoolSize(object) - 1; i >= 0; i--) {
      Member member1 = getConstantPoolMethodAt(object, i);
      if (member1 != null && (!(member1 instanceof Constructor) || 
        
        !member1.getDeclaringClass().getName().equals("java.lang.invoke.SerializedLambda")) && 
        !member1.getDeclaringClass().isAssignableFrom(paramClass)) {
        member = member1;
        if (!(member1 instanceof Method) || !isAutoBoxingMethod((Method)member1))
          break; 
      } 
    } 
    return member;
  }
  
  private static boolean isAutoBoxingMethod(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    return (paramMethod.getName().equals("valueOf") && arrayOfClass.length == 1 && arrayOfClass[0].isPrimitive() && 
      wrapPrimitives(arrayOfClass[0]).equals(paramMethod.getDeclaringClass()));
  }
  
  private static Class<?> wrapPrimitives(Class<?> paramClass) {
    return paramClass.isPrimitive() ? PRIMITIVE_WRAPPERS.get(paramClass) : paramClass;
  }
  
  private static int getConstantPoolSize(Object paramObject) {
    try {
      return ((Integer)GET_CONSTANT_POOL_SIZE.invoke(paramObject, new Object[0])).intValue();
    } catch (Exception exception) {
      return 0;
    } 
  }
  
  private static Member getConstantPoolMethodAt(Object paramObject, int paramInt) {
    try {
      return (Member)GET_CONSTANT_POOL_METHOD_AT.invoke(paramObject, new Object[] { Integer.valueOf(paramInt) });
    } catch (Exception exception) {
      return null;
    } 
  }
}
