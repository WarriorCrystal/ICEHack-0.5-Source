package org.reflections.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Utils {
  public static String repeat(String paramString, int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramInt; b++)
      stringBuilder.append(paramString); 
    return stringBuilder.toString();
  }
  
  public static boolean isEmpty(String paramString) {
    return (paramString == null || paramString.length() == 0);
  }
  
  public static boolean isEmpty(Object[] paramArrayOfObject) {
    return (paramArrayOfObject == null || paramArrayOfObject.length == 0);
  }
  
  public static File prepareFile(String paramString) {
    File file1 = new File(paramString);
    File file2 = file1.getAbsoluteFile().getParentFile();
    if (!file2.exists())
      file2.mkdirs(); 
    return file1;
  }
  
  public static Member getMemberFromDescriptor(String paramString, ClassLoader... paramVarArgs) throws ReflectionsException {
    int i = paramString.lastIndexOf('(');
    String str1 = (i != -1) ? paramString.substring(0, i) : paramString;
    String str2 = (i != -1) ? paramString.substring(i + 1, paramString.lastIndexOf(')')) : "";
    int j = Math.max(str1.lastIndexOf('.'), str1.lastIndexOf("$"));
    String str3 = str1.substring(str1.lastIndexOf(' ') + 1, j);
    String str4 = str1.substring(j + 1);
    Class[] arrayOfClass = null;
    if (!isEmpty(str2)) {
      String[] arrayOfString = str2.split(",");
      ArrayList<Class<?>> arrayList = new ArrayList(arrayOfString.length);
      for (String str : arrayOfString)
        arrayList.add(ReflectionUtils.forName(str.trim(), paramVarArgs)); 
      arrayOfClass = (Class[])arrayList.<Class<?>[]>toArray((Class<?>[][])new Class[arrayList.size()]);
    } 
    Class clazz = ReflectionUtils.forName(str3, paramVarArgs);
    while (clazz != null) {
      try {
        if (!paramString.contains("("))
          return clazz.isInterface() ? clazz.getField(str4) : clazz.getDeclaredField(str4); 
        if (isConstructor(paramString))
          return clazz.isInterface() ? clazz.getConstructor(arrayOfClass) : clazz.getDeclaredConstructor(arrayOfClass); 
        return clazz.isInterface() ? clazz.getMethod(str4, arrayOfClass) : clazz.getDeclaredMethod(str4, arrayOfClass);
      } catch (Exception exception) {
        clazz = clazz.getSuperclass();
      } 
    } 
    throw new ReflectionsException("Can't resolve member named " + str4 + " for class " + str3);
  }
  
  public static Set<Method> getMethodsFromDescriptors(Iterable<String> paramIterable, ClassLoader... paramVarArgs) {
    HashSet<Method> hashSet = Sets.newHashSet();
    for (String str : paramIterable) {
      if (!isConstructor(str)) {
        Method method = (Method)getMemberFromDescriptor(str, paramVarArgs);
        if (method != null)
          hashSet.add(method); 
      } 
    } 
    return hashSet;
  }
  
  public static Set<Constructor> getConstructorsFromDescriptors(Iterable<String> paramIterable, ClassLoader... paramVarArgs) {
    HashSet<Constructor> hashSet = Sets.newHashSet();
    for (String str : paramIterable) {
      if (isConstructor(str)) {
        Constructor constructor = (Constructor)getMemberFromDescriptor(str, paramVarArgs);
        if (constructor != null)
          hashSet.add(constructor); 
      } 
    } 
    return hashSet;
  }
  
  public static Set<Member> getMembersFromDescriptors(Iterable<String> paramIterable, ClassLoader... paramVarArgs) {
    HashSet<Member> hashSet = Sets.newHashSet();
    for (String str : paramIterable) {
      try {
        hashSet.add(getMemberFromDescriptor(str, paramVarArgs));
      } catch (ReflectionsException reflectionsException) {
        throw new ReflectionsException("Can't resolve member named " + str, reflectionsException);
      } 
    } 
    return hashSet;
  }
  
  public static Field getFieldFromString(String paramString, ClassLoader... paramVarArgs) {
    String str1 = paramString.substring(0, paramString.lastIndexOf('.'));
    String str2 = paramString.substring(paramString.lastIndexOf('.') + 1);
    try {
      return ReflectionUtils.forName(str1, paramVarArgs).getDeclaredField(str2);
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new ReflectionsException("Can't resolve field named " + str2, noSuchFieldException);
    } 
  }
  
  public static void close(InputStream paramInputStream) {
    try {
      if (paramInputStream != null)
        paramInputStream.close(); 
    } catch (IOException iOException) {
      if (Reflections.log != null)
        Reflections.log.warn("Could not close InputStream", iOException); 
    } 
  }
  
  @Nullable
  public static Logger findLogger(Class<?> paramClass) {
    try {
      Class.forName("org.slf4j.impl.StaticLoggerBinder");
      return LoggerFactory.getLogger(paramClass);
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  public static boolean isConstructor(String paramString) {
    return paramString.contains("init>");
  }
  
  public static String name(Class<?> paramClass) {
    if (!paramClass.isArray())
      return paramClass.getName(); 
    byte b = 0;
    while (paramClass.isArray()) {
      b++;
      paramClass = paramClass.getComponentType();
    } 
    return paramClass.getName() + repeat("[]", b);
  }
  
  public static List<String> names(Iterable<Class<?>> paramIterable) {
    ArrayList<String> arrayList = new ArrayList();
    for (Class<?> clazz : paramIterable)
      arrayList.add(name(clazz)); 
    return arrayList;
  }
  
  public static List<String> names(Class<?>... paramVarArgs) {
    return names(Arrays.asList(paramVarArgs));
  }
  
  public static String name(Constructor paramConstructor) {
    return paramConstructor.getName() + ".<init>(" + Joiner.on(", ").join(names(paramConstructor.getParameterTypes())) + ")";
  }
  
  public static String name(Method paramMethod) {
    return paramMethod.getDeclaringClass().getName() + "." + paramMethod.getName() + "(" + Joiner.on(", ").join(names(paramMethod.getParameterTypes())) + ")";
  }
  
  public static String name(Field paramField) {
    return paramField.getDeclaringClass().getName() + "." + paramField.getName();
  }
}
