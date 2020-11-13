package org.reflections.adapters;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import org.reflections.ReflectionsException;
import org.reflections.util.Utils;
import org.reflections.vfs.Vfs;

public class JavassistAdapter implements MetadataAdapter<ClassFile, FieldInfo, MethodInfo> {
  public static boolean includeInvisibleTag = true;
  
  public List<FieldInfo> getFields(ClassFile paramClassFile) {
    return paramClassFile.getFields();
  }
  
  public List<MethodInfo> getMethods(ClassFile paramClassFile) {
    return paramClassFile.getMethods();
  }
  
  public String getMethodName(MethodInfo paramMethodInfo) {
    return paramMethodInfo.getName();
  }
  
  public List<String> getParameterNames(MethodInfo paramMethodInfo) {
    String str = paramMethodInfo.getDescriptor();
    str = str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"));
    return splitDescriptorToTypeNames(str);
  }
  
  public List<String> getClassAnnotationNames(ClassFile paramClassFile) {
    return getAnnotationNames(new AnnotationsAttribute[] { (AnnotationsAttribute)paramClassFile.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)paramClassFile
          .getAttribute("RuntimeInvisibleAnnotations") : null });
  }
  
  public List<String> getFieldAnnotationNames(FieldInfo paramFieldInfo) {
    return getAnnotationNames(new AnnotationsAttribute[] { (AnnotationsAttribute)paramFieldInfo.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)paramFieldInfo
          .getAttribute("RuntimeInvisibleAnnotations") : null });
  }
  
  public List<String> getMethodAnnotationNames(MethodInfo paramMethodInfo) {
    return getAnnotationNames(new AnnotationsAttribute[] { (AnnotationsAttribute)paramMethodInfo.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)paramMethodInfo
          .getAttribute("RuntimeInvisibleAnnotations") : null });
  }
  
  public List<String> getParameterAnnotationNames(MethodInfo paramMethodInfo, int paramInt) {
    ArrayList<String> arrayList = Lists.newArrayList();
    ArrayList arrayList1 = Lists.newArrayList((Object[])new ParameterAnnotationsAttribute[] { (ParameterAnnotationsAttribute)paramMethodInfo.getAttribute("RuntimeVisibleParameterAnnotations"), (ParameterAnnotationsAttribute)paramMethodInfo
          .getAttribute("RuntimeInvisibleParameterAnnotations") });
    if (arrayList1 != null)
      for (ParameterAnnotationsAttribute parameterAnnotationsAttribute : arrayList1) {
        if (parameterAnnotationsAttribute != null) {
          Annotation[][] arrayOfAnnotation = parameterAnnotationsAttribute.getAnnotations();
          if (paramInt < arrayOfAnnotation.length) {
            Annotation[] arrayOfAnnotation1 = arrayOfAnnotation[paramInt];
            arrayList.addAll(getAnnotationNames(arrayOfAnnotation1));
          } 
        } 
      }  
    return arrayList;
  }
  
  public String getReturnTypeName(MethodInfo paramMethodInfo) {
    String str = paramMethodInfo.getDescriptor();
    str = str.substring(str.lastIndexOf(")") + 1);
    return splitDescriptorToTypeNames(str).get(0);
  }
  
  public String getFieldName(FieldInfo paramFieldInfo) {
    return paramFieldInfo.getName();
  }
  
  public ClassFile getOfCreateClassObject(Vfs.File paramFile) {
    InputStream inputStream = null;
    try {
      inputStream = paramFile.openInputStream();
      DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
      return new ClassFile(dataInputStream);
    } catch (IOException iOException) {
      throw new ReflectionsException("could not create class file from " + paramFile.getName(), iOException);
    } finally {
      Utils.close(inputStream);
    } 
  }
  
  public String getMethodModifier(MethodInfo paramMethodInfo) {
    int i = paramMethodInfo.getAccessFlags();
    return AccessFlag.isPrivate(i) ? "private" : (
      AccessFlag.isProtected(i) ? "protected" : (
      isPublic(Integer.valueOf(i)) ? "public" : ""));
  }
  
  public String getMethodKey(ClassFile paramClassFile, MethodInfo paramMethodInfo) {
    return getMethodName(paramMethodInfo) + "(" + Joiner.on(", ").join(getParameterNames(paramMethodInfo)) + ")";
  }
  
  public String getMethodFullKey(ClassFile paramClassFile, MethodInfo paramMethodInfo) {
    return getClassName(paramClassFile) + "." + getMethodKey(paramClassFile, paramMethodInfo);
  }
  
  public boolean isPublic(Object paramObject) {
    Integer integer = Integer.valueOf((paramObject instanceof ClassFile) ? ((ClassFile)paramObject).getAccessFlags() : ((paramObject instanceof FieldInfo) ? ((FieldInfo)paramObject)
        .getAccessFlags() : ((paramObject instanceof MethodInfo) ? 
        Integer.valueOf(((MethodInfo)paramObject).getAccessFlags()) : null).intValue()));
    return (integer != null && AccessFlag.isPublic(integer.intValue()));
  }
  
  public String getClassName(ClassFile paramClassFile) {
    return paramClassFile.getName();
  }
  
  public String getSuperclassName(ClassFile paramClassFile) {
    return paramClassFile.getSuperclass();
  }
  
  public List<String> getInterfacesNames(ClassFile paramClassFile) {
    return Arrays.asList(paramClassFile.getInterfaces());
  }
  
  public boolean acceptsInput(String paramString) {
    return paramString.endsWith(".class");
  }
  
  private List<String> getAnnotationNames(AnnotationsAttribute... paramVarArgs) {
    ArrayList<String> arrayList = Lists.newArrayList();
    if (paramVarArgs != null)
      for (AnnotationsAttribute annotationsAttribute : paramVarArgs) {
        if (annotationsAttribute != null)
          for (Annotation annotation : annotationsAttribute.getAnnotations())
            arrayList.add(annotation.getTypeName());  
      }  
    return arrayList;
  }
  
  private List<String> getAnnotationNames(Annotation[] paramArrayOfAnnotation) {
    ArrayList<String> arrayList = Lists.newArrayList();
    for (Annotation annotation : paramArrayOfAnnotation)
      arrayList.add(annotation.getTypeName()); 
    return arrayList;
  }
  
  private List<String> splitDescriptorToTypeNames(String paramString) {
    ArrayList<String> arrayList = Lists.newArrayList();
    if (paramString != null && paramString.length() != 0) {
      ArrayList<Integer> arrayList1 = Lists.newArrayList();
      Descriptor.Iterator iterator = new Descriptor.Iterator(paramString);
      while (iterator.hasNext())
        arrayList1.add(Integer.valueOf(iterator.next())); 
      arrayList1.add(Integer.valueOf(paramString.length()));
      for (byte b = 0; b < arrayList1.size() - 1; b++) {
        String str = Descriptor.toString(paramString.substring(((Integer)arrayList1.get(b)).intValue(), ((Integer)arrayList1.get(b + 1)).intValue()));
        arrayList.add(str);
      } 
    } 
    return arrayList;
  }
}
