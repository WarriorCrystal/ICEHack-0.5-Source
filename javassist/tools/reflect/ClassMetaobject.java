package javassist.tools.reflect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ClassMetaobject implements Serializable {
  static final String methodPrefix = "_m_";
  
  static final int methodPrefixLen = 3;
  
  private Class javaClass;
  
  private Constructor[] constructors;
  
  private Method[] methods;
  
  public static boolean useContextClassLoader = false;
  
  public ClassMetaobject(String[] paramArrayOfString) {
    try {
      this.javaClass = getClassObject(paramArrayOfString[0]);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException("not found: " + paramArrayOfString[0] + ", useContextClassLoader: " + 
          
          Boolean.toString(useContextClassLoader), classNotFoundException);
    } 
    this.constructors = (Constructor[])this.javaClass.getConstructors();
    this.methods = null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeUTF(this.javaClass.getName());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.javaClass = getClassObject(paramObjectInputStream.readUTF());
    this.constructors = (Constructor[])this.javaClass.getConstructors();
    this.methods = null;
  }
  
  private Class getClassObject(String paramString) throws ClassNotFoundException {
    if (useContextClassLoader)
      return Thread.currentThread().getContextClassLoader().loadClass(paramString); 
    return Class.forName(paramString);
  }
  
  public final Class getJavaClass() {
    return this.javaClass;
  }
  
  public final String getName() {
    return this.javaClass.getName();
  }
  
  public final boolean isInstance(Object paramObject) {
    return this.javaClass.isInstance(paramObject);
  }
  
  public final Object newInstance(Object[] paramArrayOfObject) throws CannotCreateException {
    int i = this.constructors.length;
    for (byte b = 0; b < i; b++) {
      try {
        return this.constructors[b].newInstance(paramArrayOfObject);
      } catch (IllegalArgumentException illegalArgumentException) {
      
      } catch (InstantiationException instantiationException) {
        throw new CannotCreateException(instantiationException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new CannotCreateException(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        throw new CannotCreateException(invocationTargetException);
      } 
    } 
    throw new CannotCreateException("no constructor matches");
  }
  
  public Object trapFieldRead(String paramString) {
    Class clazz = getJavaClass();
    try {
      return clazz.getField(paramString).get(null);
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new RuntimeException(noSuchFieldException.toString());
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException.toString());
    } 
  }
  
  public void trapFieldWrite(String paramString, Object paramObject) {
    Class clazz = getJavaClass();
    try {
      clazz.getField(paramString).set(null, paramObject);
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new RuntimeException(noSuchFieldException.toString());
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException.toString());
    } 
  }
  
  public static Object invoke(Object paramObject, int paramInt, Object[] paramArrayOfObject) throws Throwable {
    Method[] arrayOfMethod = paramObject.getClass().getMethods();
    int i = arrayOfMethod.length;
    String str = "_m_" + paramInt;
    for (byte b = 0; b < i; b++) {
      if (arrayOfMethod[b].getName().startsWith(str))
        try {
          return arrayOfMethod[b].invoke(paramObject, paramArrayOfObject);
        } catch (InvocationTargetException invocationTargetException) {
          throw invocationTargetException.getTargetException();
        } catch (IllegalAccessException illegalAccessException) {
          throw new CannotInvokeException(illegalAccessException);
        }  
    } 
    throw new CannotInvokeException("cannot find a method");
  }
  
  public Object trapMethodcall(int paramInt, Object[] paramArrayOfObject) throws Throwable {
    try {
      Method[] arrayOfMethod = getReflectiveMethods();
      return arrayOfMethod[paramInt].invoke(null, paramArrayOfObject);
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } catch (IllegalAccessException illegalAccessException) {
      throw new CannotInvokeException(illegalAccessException);
    } 
  }
  
  public final Method[] getReflectiveMethods() {
    if (this.methods != null)
      return this.methods; 
    Class clazz = getJavaClass();
    Method[] arrayOfMethod = clazz.getDeclaredMethods();
    int i = arrayOfMethod.length;
    int[] arrayOfInt = new int[i];
    int j = 0;
    byte b;
    for (b = 0; b < i; b++) {
      Method method = arrayOfMethod[b];
      String str = method.getName();
      if (str.startsWith("_m_")) {
        int k = 0;
        byte b1 = 3;
        while (true) {
          char c = str.charAt(b1);
          if ('0' <= c && c <= '9') {
            k = k * 10 + c - 48;
            b1++;
          } 
          break;
        } 
        arrayOfInt[b] = ++k;
        if (k > j)
          j = k; 
      } 
    } 
    this.methods = new Method[j];
    for (b = 0; b < i; b++) {
      if (arrayOfInt[b] > 0)
        this.methods[arrayOfInt[b] - 1] = arrayOfMethod[b]; 
    } 
    return this.methods;
  }
  
  public final Method getMethod(int paramInt) {
    return getReflectiveMethods()[paramInt];
  }
  
  public final String getMethodName(int paramInt) {
    char c;
    String str = getReflectiveMethods()[paramInt].getName();
    byte b = 3;
    do {
      c = str.charAt(b++);
    } while (c >= '0' && '9' >= c);
    return str.substring(b);
  }
  
  public final Class[] getParameterTypes(int paramInt) {
    return getReflectiveMethods()[paramInt].getParameterTypes();
  }
  
  public final Class getReturnType(int paramInt) {
    return getReflectiveMethods()[paramInt].getReturnType();
  }
  
  public final int getMethodIndex(String paramString, Class[] paramArrayOfClass) throws NoSuchMethodException {
    Method[] arrayOfMethod = getReflectiveMethods();
    for (byte b = 0; b < arrayOfMethod.length; b++) {
      if (arrayOfMethod[b] != null)
        if (getMethodName(b).equals(paramString) && 
          Arrays.equals((Object[])paramArrayOfClass, (Object[])arrayOfMethod[b].getParameterTypes()))
          return b;  
    } 
    throw new NoSuchMethodException("Method " + paramString + " not found");
  }
}
