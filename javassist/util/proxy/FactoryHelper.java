package javassist.util.proxy;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.bytecode.ClassFile;

public class FactoryHelper {
  private static Method defineClass1;
  
  private static Method defineClass2;
  
  static {
    try {
      Class<?> clazz = Class.forName("java.lang.ClassLoader");
      defineClass1 = SecurityActions.getDeclaredMethod(clazz, "defineClass", new Class[] { String.class, byte[].class, int.class, int.class });
      defineClass2 = SecurityActions.getDeclaredMethod(clazz, "defineClass", new Class[] { String.class, byte[].class, int.class, int.class, ProtectionDomain.class });
    } catch (Exception exception) {
      throw new RuntimeException("cannot initialize");
    } 
  }
  
  public static final int typeIndex(Class paramClass) {
    Class[] arrayOfClass = primitiveTypes;
    int i = arrayOfClass.length;
    for (byte b = 0; b < i; b++) {
      if (arrayOfClass[b] == paramClass)
        return b; 
    } 
    throw new RuntimeException("bad type:" + paramClass.getName());
  }
  
  public static final Class[] primitiveTypes = new Class[] { boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class, void.class };
  
  public static final String[] wrapperTypes = new String[] { "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Void" };
  
  public static final String[] wrapperDesc = new String[] { "(Z)V", "(B)V", "(C)V", "(S)V", "(I)V", "(J)V", "(F)V", "(D)V" };
  
  public static final String[] unwarpMethods = new String[] { "booleanValue", "byteValue", "charValue", "shortValue", "intValue", "longValue", "floatValue", "doubleValue" };
  
  public static final String[] unwrapDesc = new String[] { "()Z", "()B", "()C", "()S", "()I", "()J", "()F", "()D" };
  
  public static final int[] dataSize = new int[] { 1, 1, 1, 1, 1, 2, 1, 2 };
  
  public static Class toClass(ClassFile paramClassFile, ClassLoader paramClassLoader) throws CannotCompileException {
    return toClass(paramClassFile, paramClassLoader, null);
  }
  
  public static Class toClass(ClassFile paramClassFile, ClassLoader paramClassLoader, ProtectionDomain paramProtectionDomain) throws CannotCompileException {
    try {
      Method method;
      Object[] arrayOfObject;
      byte[] arrayOfByte = toBytecode(paramClassFile);
      if (paramProtectionDomain == null) {
        method = defineClass1;
        arrayOfObject = new Object[] { paramClassFile.getName(), arrayOfByte, new Integer(0), new Integer(arrayOfByte.length) };
      } else {
        method = defineClass2;
        arrayOfObject = new Object[] { paramClassFile.getName(), arrayOfByte, new Integer(0), new Integer(arrayOfByte.length), paramProtectionDomain };
      } 
      return toClass2(method, paramClassLoader, arrayOfObject);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (InvocationTargetException invocationTargetException) {
      throw new CannotCompileException(invocationTargetException.getTargetException());
    } catch (Exception exception) {
      throw new CannotCompileException(exception);
    } 
  }
  
  private static synchronized Class toClass2(Method paramMethod, ClassLoader paramClassLoader, Object[] paramArrayOfObject) throws Exception {
    SecurityActions.setAccessible(paramMethod, true);
    Class clazz = (Class)paramMethod.invoke(paramClassLoader, paramArrayOfObject);
    SecurityActions.setAccessible(paramMethod, false);
    return clazz;
  }
  
  private static byte[] toBytecode(ClassFile paramClassFile) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      paramClassFile.write(dataOutputStream);
    } finally {
      dataOutputStream.close();
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public static void writeFile(ClassFile paramClassFile, String paramString) throws CannotCompileException {
    try {
      writeFile0(paramClassFile, paramString);
    } catch (IOException iOException) {
      throw new CannotCompileException(iOException);
    } 
  }
  
  private static void writeFile0(ClassFile paramClassFile, String paramString) throws CannotCompileException, IOException {
    String str1 = paramClassFile.getName();
    String str2 = paramString + File.separatorChar + str1.replace('.', File.separatorChar) + ".class";
    int i = str2.lastIndexOf(File.separatorChar);
    if (i > 0) {
      String str = str2.substring(0, i);
      if (!str.equals("."))
        (new File(str)).mkdirs(); 
    } 
    DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(str2)));
    try {
      paramClassFile.write(dataOutputStream);
    } catch (IOException iOException) {
      throw iOException;
    } finally {
      dataOutputStream.close();
    } 
  }
}
