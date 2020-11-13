package javassist.bytecode.annotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationDefaultAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;

public class AnnotationImpl implements InvocationHandler {
  private static final String JDK_ANNOTATION_CLASS_NAME = "java.lang.annotation.Annotation";
  
  private static Method JDK_ANNOTATION_TYPE_METHOD = null;
  
  private Annotation annotation;
  
  private ClassPool pool;
  
  private ClassLoader classLoader;
  
  private transient Class annotationType;
  
  private transient int cachedHashCode = Integer.MIN_VALUE;
  
  static {
    try {
      Class<?> clazz = Class.forName("java.lang.annotation.Annotation");
      JDK_ANNOTATION_TYPE_METHOD = clazz.getMethod("annotationType", (Class[])null);
    } catch (Exception exception) {}
  }
  
  public static Object make(ClassLoader paramClassLoader, Class paramClass, ClassPool paramClassPool, Annotation paramAnnotation) {
    AnnotationImpl annotationImpl = new AnnotationImpl(paramAnnotation, paramClassPool, paramClassLoader);
    return Proxy.newProxyInstance(paramClassLoader, new Class[] { paramClass }, annotationImpl);
  }
  
  private AnnotationImpl(Annotation paramAnnotation, ClassPool paramClassPool, ClassLoader paramClassLoader) {
    this.annotation = paramAnnotation;
    this.pool = paramClassPool;
    this.classLoader = paramClassLoader;
  }
  
  public String getTypeName() {
    return this.annotation.getTypeName();
  }
  
  private Class getAnnotationType() {
    if (this.annotationType == null) {
      String str = this.annotation.getTypeName();
      try {
        this.annotationType = this.classLoader.loadClass(str);
      } catch (ClassNotFoundException classNotFoundException) {
        NoClassDefFoundError noClassDefFoundError = new NoClassDefFoundError("Error loading annotation class: " + str);
        noClassDefFoundError.setStackTrace(classNotFoundException.getStackTrace());
        throw noClassDefFoundError;
      } 
    } 
    return this.annotationType;
  }
  
  public Annotation getAnnotation() {
    return this.annotation;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    String str = paramMethod.getName();
    if (Object.class == paramMethod.getDeclaringClass()) {
      if ("equals".equals(str)) {
        Object object = paramArrayOfObject[0];
        return new Boolean(checkEquals(object));
      } 
      if ("toString".equals(str))
        return this.annotation.toString(); 
      if ("hashCode".equals(str))
        return new Integer(hashCode()); 
    } else if ("annotationType".equals(str) && (paramMethod
      .getParameterTypes()).length == 0) {
      return getAnnotationType();
    } 
    MemberValue memberValue = this.annotation.getMemberValue(str);
    if (memberValue == null)
      return getDefault(str, paramMethod); 
    return memberValue.getValue(this.classLoader, this.pool, paramMethod);
  }
  
  private Object getDefault(String paramString, Method paramMethod) throws ClassNotFoundException, RuntimeException {
    String str = this.annotation.getTypeName();
    if (this.pool != null)
      try {
        CtClass ctClass = this.pool.get(str);
        ClassFile classFile = ctClass.getClassFile2();
        MethodInfo methodInfo = classFile.getMethod(paramString);
        if (methodInfo != null) {
          AnnotationDefaultAttribute annotationDefaultAttribute = (AnnotationDefaultAttribute)methodInfo.getAttribute("AnnotationDefault");
          if (annotationDefaultAttribute != null) {
            MemberValue memberValue = annotationDefaultAttribute.getDefaultValue();
            return memberValue.getValue(this.classLoader, this.pool, paramMethod);
          } 
        } 
      } catch (NotFoundException notFoundException) {
        throw new RuntimeException("cannot find a class file: " + str);
      }  
    throw new RuntimeException("no default value: " + str + "." + paramString + "()");
  }
  
  public int hashCode() {
    if (this.cachedHashCode == Integer.MIN_VALUE) {
      int i = 0;
      getAnnotationType();
      Method[] arrayOfMethod = this.annotationType.getDeclaredMethods();
      for (byte b = 0; b < arrayOfMethod.length; b++) {
        String str = arrayOfMethod[b].getName();
        int j = 0;
        MemberValue memberValue = this.annotation.getMemberValue(str);
        Object object = null;
        try {
          if (memberValue != null)
            object = memberValue.getValue(this.classLoader, this.pool, arrayOfMethod[b]); 
          if (object == null)
            object = getDefault(str, arrayOfMethod[b]); 
        } catch (RuntimeException runtimeException) {
          throw runtimeException;
        } catch (Exception exception) {
          throw new RuntimeException("Error retrieving value " + str + " for annotation " + this.annotation.getTypeName(), exception);
        } 
        if (object != null)
          if (object.getClass().isArray()) {
            j = arrayHashCode(object);
          } else {
            j = object.hashCode();
          }  
        i += 127 * str.hashCode() ^ j;
      } 
      this.cachedHashCode = i;
    } 
    return this.cachedHashCode;
  }
  
  private boolean checkEquals(Object paramObject) throws Exception {
    if (paramObject == null)
      return false; 
    if (paramObject instanceof Proxy) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramObject);
      if (invocationHandler instanceof AnnotationImpl) {
        AnnotationImpl annotationImpl = (AnnotationImpl)invocationHandler;
        return this.annotation.equals(annotationImpl.annotation);
      } 
    } 
    Class clazz = (Class)JDK_ANNOTATION_TYPE_METHOD.invoke(paramObject, (Object[])null);
    if (!getAnnotationType().equals(clazz))
      return false; 
    Method[] arrayOfMethod = this.annotationType.getDeclaredMethods();
    for (byte b = 0; b < arrayOfMethod.length; b++) {
      String str = arrayOfMethod[b].getName();
      MemberValue memberValue = this.annotation.getMemberValue(str);
      Object object1 = null;
      Object object2 = null;
      try {
        if (memberValue != null)
          object1 = memberValue.getValue(this.classLoader, this.pool, arrayOfMethod[b]); 
        if (object1 == null)
          object1 = getDefault(str, arrayOfMethod[b]); 
        object2 = arrayOfMethod[b].invoke(paramObject, (Object[])null);
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new RuntimeException("Error retrieving value " + str + " for annotation " + this.annotation.getTypeName(), exception);
      } 
      if (object1 == null && object2 != null)
        return false; 
      if (object1 != null && !object1.equals(object2))
        return false; 
    } 
    return true;
  }
  
  private static int arrayHashCode(Object paramObject) {
    if (paramObject == null)
      return 0; 
    int i = 1;
    Object[] arrayOfObject = (Object[])paramObject;
    for (byte b = 0; b < arrayOfObject.length; b++) {
      int j = 0;
      if (arrayOfObject[b] != null)
        j = arrayOfObject[b].hashCode(); 
      i = 31 * i + j;
    } 
    return i;
  }
}
