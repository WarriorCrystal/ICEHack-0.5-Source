package org.reflections.adapters;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.reflections.ReflectionUtils;
import org.reflections.util.Utils;
import org.reflections.vfs.Vfs;

public class JavaReflectionAdapter implements MetadataAdapter<Class, Field, Member> {
  public List<Field> getFields(Class paramClass) {
    return Lists.newArrayList((Object[])paramClass.getDeclaredFields());
  }
  
  public List<Member> getMethods(Class paramClass) {
    ArrayList<Member> arrayList = Lists.newArrayList();
    arrayList.addAll(Arrays.asList(paramClass.getDeclaredMethods()));
    arrayList.addAll(Arrays.asList(paramClass.getDeclaredConstructors()));
    return arrayList;
  }
  
  public String getMethodName(Member paramMember) {
    return (paramMember instanceof Method) ? paramMember.getName() : ((paramMember instanceof Constructor) ? "<init>" : null);
  }
  
  public List<String> getParameterNames(Member paramMember) {
    ArrayList<String> arrayList = Lists.newArrayList();
    Class<?>[] arrayOfClass = (paramMember instanceof Method) ? ((Method)paramMember).getParameterTypes() : ((paramMember instanceof Constructor) ? ((Constructor)paramMember).getParameterTypes() : null);
    if (arrayOfClass != null)
      for (Class<?> clazz : arrayOfClass) {
        String str = getName(clazz);
        arrayList.add(str);
      }  
    return arrayList;
  }
  
  public List<String> getClassAnnotationNames(Class paramClass) {
    return getAnnotationNames(paramClass.getDeclaredAnnotations());
  }
  
  public List<String> getFieldAnnotationNames(Field paramField) {
    return getAnnotationNames(paramField.getDeclaredAnnotations());
  }
  
  public List<String> getMethodAnnotationNames(Member paramMember) {
    Annotation[] arrayOfAnnotation = (paramMember instanceof Method) ? ((Method)paramMember).getDeclaredAnnotations() : ((paramMember instanceof Constructor) ? ((Constructor)paramMember).getDeclaredAnnotations() : null);
    return getAnnotationNames(arrayOfAnnotation);
  }
  
  public List<String> getParameterAnnotationNames(Member paramMember, int paramInt) {
    Annotation[][] arrayOfAnnotation = (paramMember instanceof Method) ? ((Method)paramMember).getParameterAnnotations() : ((paramMember instanceof Constructor) ? ((Constructor)paramMember).getParameterAnnotations() : (Annotation[][])null);
    return getAnnotationNames((arrayOfAnnotation != null) ? arrayOfAnnotation[paramInt] : null);
  }
  
  public String getReturnTypeName(Member paramMember) {
    return ((Method)paramMember).getReturnType().getName();
  }
  
  public String getFieldName(Field paramField) {
    return paramField.getName();
  }
  
  public Class getOfCreateClassObject(Vfs.File paramFile) throws Exception {
    return getOfCreateClassObject(paramFile, null);
  }
  
  public Class getOfCreateClassObject(Vfs.File paramFile, @Nullable ClassLoader... paramVarArgs) throws Exception {
    String str = paramFile.getRelativePath().replace("/", ".").replace(".class", "");
    return ReflectionUtils.forName(str, paramVarArgs);
  }
  
  public String getMethodModifier(Member paramMember) {
    return Modifier.toString(paramMember.getModifiers());
  }
  
  public String getMethodKey(Class paramClass, Member paramMember) {
    return getMethodName(paramMember) + "(" + Joiner.on(", ").join(getParameterNames(paramMember)) + ")";
  }
  
  public String getMethodFullKey(Class paramClass, Member paramMember) {
    return getClassName(paramClass) + "." + getMethodKey(paramClass, paramMember);
  }
  
  public boolean isPublic(Object paramObject) {
    Integer integer = Integer.valueOf((paramObject instanceof Class) ? ((Class)paramObject).getModifiers() : ((paramObject instanceof Member) ? 
        Integer.valueOf(((Member)paramObject).getModifiers()) : null).intValue());
    return (integer != null && Modifier.isPublic(integer.intValue()));
  }
  
  public String getClassName(Class paramClass) {
    return paramClass.getName();
  }
  
  public String getSuperclassName(Class paramClass) {
    Class clazz = paramClass.getSuperclass();
    return (clazz != null) ? clazz.getName() : "";
  }
  
  public List<String> getInterfacesNames(Class paramClass) {
    Class[] arrayOfClass = paramClass.getInterfaces();
    ArrayList<String> arrayList = new ArrayList((arrayOfClass != null) ? arrayOfClass.length : 0);
    if (arrayOfClass != null)
      for (Class clazz : arrayOfClass)
        arrayList.add(clazz.getName());  
    return arrayList;
  }
  
  public boolean acceptsInput(String paramString) {
    return paramString.endsWith(".class");
  }
  
  private List<String> getAnnotationNames(Annotation[] paramArrayOfAnnotation) {
    ArrayList<String> arrayList = new ArrayList(paramArrayOfAnnotation.length);
    for (Annotation annotation : paramArrayOfAnnotation)
      arrayList.add(annotation.annotationType().getName()); 
    return arrayList;
  }
  
  public static String getName(Class<?> paramClass) {
    if (paramClass.isArray())
      try {
        Class<?> clazz = paramClass;
        byte b;
        for (b = 0; clazz.isArray(); ) {
          b++;
          clazz = clazz.getComponentType();
        } 
        return clazz.getName() + Utils.repeat("[]", b);
      } catch (Throwable throwable) {} 
    return paramClass.getName();
  }
}
