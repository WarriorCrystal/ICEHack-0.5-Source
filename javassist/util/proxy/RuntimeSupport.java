package javassist.util.proxy;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.lang.reflect.Method;

public class RuntimeSupport {
  public static MethodHandler default_interceptor = new DefaultMethodHandler();
  
  static class DefaultMethodHandler implements MethodHandler, Serializable {
    public Object invoke(Object param1Object, Method param1Method1, Method param1Method2, Object[] param1ArrayOfObject) throws Exception {
      return param1Method2.invoke(param1Object, param1ArrayOfObject);
    }
  }
  
  public static void find2Methods(Class paramClass, String paramString1, String paramString2, int paramInt, String paramString3, Method[] paramArrayOfMethod) {
    paramArrayOfMethod[paramInt + 1] = (paramString2 == null) ? null : 
      findMethod(paramClass, paramString2, paramString3);
    paramArrayOfMethod[paramInt] = findSuperClassMethod(paramClass, paramString1, paramString3);
  }
  
  public static void find2Methods(Object paramObject, String paramString1, String paramString2, int paramInt, String paramString3, Method[] paramArrayOfMethod) {
    paramArrayOfMethod[paramInt + 1] = (paramString2 == null) ? null : 
      findMethod(paramObject, paramString2, paramString3);
    paramArrayOfMethod[paramInt] = findSuperMethod(paramObject, paramString1, paramString3);
  }
  
  public static Method findMethod(Object paramObject, String paramString1, String paramString2) {
    Method method = findMethod2(paramObject.getClass(), paramString1, paramString2);
    if (method == null)
      error(paramObject.getClass(), paramString1, paramString2); 
    return method;
  }
  
  public static Method findMethod(Class paramClass, String paramString1, String paramString2) {
    Method method = findMethod2(paramClass, paramString1, paramString2);
    if (method == null)
      error(paramClass, paramString1, paramString2); 
    return method;
  }
  
  public static Method findSuperMethod(Object paramObject, String paramString1, String paramString2) {
    Class<?> clazz = paramObject.getClass();
    return findSuperClassMethod(clazz, paramString1, paramString2);
  }
  
  public static Method findSuperClassMethod(Class paramClass, String paramString1, String paramString2) {
    Method method = findSuperMethod2(paramClass.getSuperclass(), paramString1, paramString2);
    if (method == null)
      method = searchInterfaces(paramClass, paramString1, paramString2); 
    if (method == null)
      error(paramClass, paramString1, paramString2); 
    return method;
  }
  
  private static void error(Class paramClass, String paramString1, String paramString2) {
    throw new RuntimeException("not found " + paramString1 + ":" + paramString2 + " in " + paramClass
        .getName());
  }
  
  private static Method findSuperMethod2(Class paramClass, String paramString1, String paramString2) {
    Method method = findMethod2(paramClass, paramString1, paramString2);
    if (method != null)
      return method; 
    Class clazz = paramClass.getSuperclass();
    if (clazz != null) {
      method = findSuperMethod2(clazz, paramString1, paramString2);
      if (method != null)
        return method; 
    } 
    return searchInterfaces(paramClass, paramString1, paramString2);
  }
  
  private static Method searchInterfaces(Class paramClass, String paramString1, String paramString2) {
    Method method = null;
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      method = findSuperMethod2(arrayOfClass[b], paramString1, paramString2);
      if (method != null)
        return method; 
    } 
    return method;
  }
  
  private static Method findMethod2(Class paramClass, String paramString1, String paramString2) {
    Method[] arrayOfMethod = SecurityActions.getDeclaredMethods(paramClass);
    int i = arrayOfMethod.length;
    for (byte b = 0; b < i; b++) {
      if (arrayOfMethod[b].getName().equals(paramString1) && 
        makeDescriptor(arrayOfMethod[b]).equals(paramString2))
        return arrayOfMethod[b]; 
    } 
    return null;
  }
  
  public static String makeDescriptor(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    return makeDescriptor(arrayOfClass, paramMethod.getReturnType());
  }
  
  public static String makeDescriptor(Class[] paramArrayOfClass, Class paramClass) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    for (byte b = 0; b < paramArrayOfClass.length; b++)
      makeDesc(stringBuffer, paramArrayOfClass[b]); 
    stringBuffer.append(')');
    if (paramClass != null)
      makeDesc(stringBuffer, paramClass); 
    return stringBuffer.toString();
  }
  
  public static String makeDescriptor(String paramString, Class paramClass) {
    StringBuffer stringBuffer = new StringBuffer(paramString);
    makeDesc(stringBuffer, paramClass);
    return stringBuffer.toString();
  }
  
  private static void makeDesc(StringBuffer paramStringBuffer, Class<void> paramClass) {
    if (paramClass.isArray()) {
      paramStringBuffer.append('[');
      makeDesc(paramStringBuffer, paramClass.getComponentType());
    } else if (paramClass.isPrimitive()) {
      if (paramClass == void.class) {
        paramStringBuffer.append('V');
      } else if (paramClass == int.class) {
        paramStringBuffer.append('I');
      } else if (paramClass == byte.class) {
        paramStringBuffer.append('B');
      } else if (paramClass == long.class) {
        paramStringBuffer.append('J');
      } else if (paramClass == double.class) {
        paramStringBuffer.append('D');
      } else if (paramClass == float.class) {
        paramStringBuffer.append('F');
      } else if (paramClass == char.class) {
        paramStringBuffer.append('C');
      } else if (paramClass == short.class) {
        paramStringBuffer.append('S');
      } else if (paramClass == boolean.class) {
        paramStringBuffer.append('Z');
      } else {
        throw new RuntimeException("bad type: " + paramClass.getName());
      } 
    } else {
      paramStringBuffer.append('L').append(paramClass.getName().replace('.', '/'))
        .append(';');
    } 
  }
  
  public static SerializedProxy makeSerializedProxy(Object paramObject) throws InvalidClassException {
    Class<?> clazz = paramObject.getClass();
    MethodHandler methodHandler = null;
    if (paramObject instanceof ProxyObject) {
      methodHandler = ((ProxyObject)paramObject).getHandler();
    } else if (paramObject instanceof Proxy) {
      methodHandler = ProxyFactory.getHandler((Proxy)paramObject);
    } 
    return new SerializedProxy(clazz, ProxyFactory.getFilterSignature(clazz), methodHandler);
  }
}
