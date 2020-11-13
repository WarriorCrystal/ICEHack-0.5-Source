package org.reflections.serializers;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.Utils;

public class JavaCodeSerializer implements Serializer {
  private static final String pathSeparator = "_";
  
  private static final String doubleSeparator = "__";
  
  private static final String dotSeparator = ".";
  
  private static final String arrayDescriptor = "$$";
  
  private static final String tokenSeparator = "_";
  
  public Reflections read(InputStream paramInputStream) {
    throw new UnsupportedOperationException("read is not implemented on JavaCodeSerializer");
  }
  
  public File save(Reflections paramReflections, String paramString) {
    String str2, str3;
    if (paramString.endsWith("/"))
      paramString = paramString.substring(0, paramString.length() - 1); 
    String str1 = paramString.replace('.', '/').concat(".java");
    File file = Utils.prepareFile(str1);
    int i = paramString.lastIndexOf('.');
    if (i == -1) {
      str2 = "";
      str3 = paramString.substring(paramString.lastIndexOf('/') + 1);
    } else {
      str2 = paramString.substring(paramString.lastIndexOf('/') + 1, i);
      str3 = paramString.substring(i + 1);
    } 
    try {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("//generated using Reflections JavaCodeSerializer")
        .append(" [").append(new Date()).append("]")
        .append("\n");
      if (str2.length() != 0) {
        stringBuilder.append("package ").append(str2).append(";\n");
        stringBuilder.append("\n");
      } 
      stringBuilder.append("public interface ").append(str3).append(" {\n\n");
      stringBuilder.append(toString(paramReflections));
      stringBuilder.append("}\n");
      Files.write(stringBuilder.toString(), new File(str1), Charset.defaultCharset());
    } catch (IOException iOException) {
      throw new RuntimeException();
    } 
    return file;
  }
  
  public String toString(Reflections paramReflections) {
    if (paramReflections.getStore().get(TypeElementsScanner.class.getSimpleName()).isEmpty() && 
      Reflections.log != null)
      Reflections.log.warn("JavaCodeSerializer needs TypeElementsScanner configured"); 
    StringBuilder stringBuilder = new StringBuilder();
    ArrayList<String> arrayList = Lists.newArrayList();
    byte b = 1;
    ArrayList<Comparable> arrayList1 = Lists.newArrayList(paramReflections.getStore().get(TypeElementsScanner.class.getSimpleName()).keySet());
    Collections.sort(arrayList1);
    for (String str1 : arrayList1) {
      ArrayList<String> arrayList2 = Lists.newArrayList((Object[])str1.split("\\."));
      byte b1 = 0;
      while (b1 < Math.min(arrayList2.size(), arrayList.size()) && ((String)arrayList2.get(b1)).equals(arrayList.get(b1)))
        b1++; 
      int j;
      for (j = arrayList.size(); j > b1; j--)
        stringBuilder.append(Utils.repeat("\t", --b)).append("}\n"); 
      for (j = b1; j < arrayList2.size() - 1; j++)
        stringBuilder.append(Utils.repeat("\t", b++)).append("public interface ").append(getNonDuplicateName(arrayList2.get(j), arrayList2, j)).append(" {\n"); 
      String str2 = arrayList2.get(arrayList2.size() - 1);
      ArrayList<String> arrayList3 = Lists.newArrayList();
      ArrayList<String> arrayList4 = Lists.newArrayList();
      SetMultimap setMultimap = Multimaps.newSetMultimap(new HashMap<>(), new Supplier<Set<String>>() {
            public Set<String> get() {
              return Sets.newHashSet();
            }
          });
      for (String str : paramReflections.getStore().get(TypeElementsScanner.class.getSimpleName(), new String[] { str1 })) {
        if (str.startsWith("@")) {
          arrayList3.add(str.substring(1));
          continue;
        } 
        if (str.contains("(")) {
          if (!str.startsWith("<")) {
            int k = str.indexOf('(');
            String str3 = str.substring(0, k);
            String str4 = str.substring(k + 1, str.indexOf(")"));
            String str5 = "";
            if (str4.length() != 0)
              str5 = "_" + str4.replace(".", "_").replace(", ", "__").replace("[]", "$$"); 
            String str6 = str3 + str5;
            setMultimap.put(str3, str6);
          } 
          continue;
        } 
        if (!Utils.isEmpty(str))
          arrayList4.add(str); 
      } 
      stringBuilder.append(Utils.repeat("\t", b++)).append("public interface ").append(getNonDuplicateName(str2, arrayList2, arrayList2.size() - 1)).append(" {\n");
      if (!arrayList4.isEmpty()) {
        stringBuilder.append(Utils.repeat("\t", b++)).append("public interface fields {\n");
        for (String str : arrayList4)
          stringBuilder.append(Utils.repeat("\t", b)).append("public interface ").append(getNonDuplicateName(str, arrayList2)).append(" {}\n"); 
        stringBuilder.append(Utils.repeat("\t", --b)).append("}\n");
      } 
      if (!setMultimap.isEmpty()) {
        stringBuilder.append(Utils.repeat("\t", b++)).append("public interface methods {\n");
        for (Map.Entry entry : setMultimap.entries()) {
          String str3 = (String)entry.getKey();
          String str4 = (String)entry.getValue();
          String str5 = (setMultimap.get(str3).size() == 1) ? str3 : str4;
          str5 = getNonDuplicateName(str5, arrayList4);
          stringBuilder.append(Utils.repeat("\t", b)).append("public interface ").append(getNonDuplicateName(str5, arrayList2)).append(" {}\n");
        } 
        stringBuilder.append(Utils.repeat("\t", --b)).append("}\n");
      } 
      if (!arrayList3.isEmpty()) {
        stringBuilder.append(Utils.repeat("\t", b++)).append("public interface annotations {\n");
        for (String str3 : arrayList3) {
          String str4 = str3;
          str4 = getNonDuplicateName(str4, arrayList2);
          stringBuilder.append(Utils.repeat("\t", b)).append("public interface ").append(str4).append(" {}\n");
        } 
        stringBuilder.append(Utils.repeat("\t", --b)).append("}\n");
      } 
      arrayList = arrayList2;
    } 
    for (int i = arrayList.size(); i >= 1; i--)
      stringBuilder.append(Utils.repeat("\t", i)).append("}\n"); 
    return stringBuilder.toString();
  }
  
  private String getNonDuplicateName(String paramString, List<String> paramList, int paramInt) {
    String str = normalize(paramString);
    for (byte b = 0; b < paramInt; b++) {
      if (str.equals(paramList.get(b)))
        return getNonDuplicateName(str + "_", paramList, paramInt); 
    } 
    return str;
  }
  
  private String normalize(String paramString) {
    return paramString.replace(".", "_");
  }
  
  private String getNonDuplicateName(String paramString, List<String> paramList) {
    return getNonDuplicateName(paramString, paramList, paramList.size());
  }
  
  public static Class<?> resolveClassOf(Class<?> paramClass) throws ClassNotFoundException {
    Class<?> clazz = paramClass;
    LinkedList<String> linkedList = Lists.newLinkedList();
    while (clazz != null) {
      linkedList.addFirst(clazz.getSimpleName());
      clazz = clazz.getDeclaringClass();
    } 
    String str = Joiner.on(".").join(linkedList.subList(1, linkedList.size())).replace(".$", "$");
    return Class.forName(str);
  }
  
  public static Class<?> resolveClass(Class paramClass) {
    try {
      return resolveClassOf(paramClass);
    } catch (Exception exception) {
      throw new ReflectionsException("could not resolve to class " + paramClass.getName(), exception);
    } 
  }
  
  public static Field resolveField(Class paramClass) {
    try {
      String str = paramClass.getSimpleName();
      Class<?> clazz = paramClass.getDeclaringClass().getDeclaringClass();
      return resolveClassOf(clazz).getDeclaredField(str);
    } catch (Exception exception) {
      throw new ReflectionsException("could not resolve to field " + paramClass.getName(), exception);
    } 
  }
  
  public static Annotation resolveAnnotation(Class paramClass) {
    try {
      String str = paramClass.getSimpleName().replace("_", ".");
      Class<?> clazz1 = paramClass.getDeclaringClass().getDeclaringClass();
      Class<?> clazz2 = resolveClassOf(clazz1);
      Class<Object> clazz = ReflectionUtils.forName(str, new ClassLoader[0]);
      return (Annotation)clazz2.getAnnotation(clazz);
    } catch (Exception exception) {
      throw new ReflectionsException("could not resolve to annotation " + paramClass.getName(), exception);
    } 
  }
  
  public static Method resolveMethod(Class paramClass) {
    String str = paramClass.getSimpleName();
    try {
      String str1;
      Class[] arrayOfClass;
      if (str.contains("_")) {
        str1 = str.substring(0, str.indexOf("_"));
        String[] arrayOfString = str.substring(str.indexOf("_") + 1).split("__");
        arrayOfClass = new Class[arrayOfString.length];
        for (byte b = 0; b < arrayOfString.length; b++) {
          String str2 = arrayOfString[b].replace("$$", "[]").replace("_", ".");
          arrayOfClass[b] = ReflectionUtils.forName(str2, new ClassLoader[0]);
        } 
      } else {
        str1 = str;
        arrayOfClass = null;
      } 
      Class<?> clazz = paramClass.getDeclaringClass().getDeclaringClass();
      return resolveClassOf(clazz).getDeclaredMethod(str1, arrayOfClass);
    } catch (Exception exception) {
      throw new ReflectionsException("could not resolve to method " + paramClass.getName(), exception);
    } 
  }
}
