package org.reflections;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.Utils;

public abstract class ReflectionUtils {
  public static boolean includeObject = false;
  
  private static List<String> primitiveNames;
  
  private static List<Class> primitiveTypes;
  
  private static List<String> primitiveDescriptors;
  
  public static Set<Class<?>> getAllSuperTypes(Class<?> paramClass, Predicate<? super Class<?>>... paramVarArgs) {
    LinkedHashSet<Class<?>> linkedHashSet = Sets.newLinkedHashSet();
    if (paramClass != null && (includeObject || !paramClass.equals(Object.class))) {
      linkedHashSet.add(paramClass);
      for (Class<?> clazz : getSuperTypes(paramClass))
        linkedHashSet.addAll(getAllSuperTypes(clazz, (Predicate<? super Class<?>>[])new Predicate[0])); 
    } 
    return filter(linkedHashSet, paramVarArgs);
  }
  
  public static Set<Class<?>> getSuperTypes(Class<?> paramClass) {
    LinkedHashSet<Class<?>> linkedHashSet = new LinkedHashSet();
    Class<?> clazz = paramClass.getSuperclass();
    Class[] arrayOfClass = paramClass.getInterfaces();
    if (clazz != null && (includeObject || !clazz.equals(Object.class)))
      linkedHashSet.add(clazz); 
    if (arrayOfClass != null && arrayOfClass.length > 0)
      linkedHashSet.addAll(Arrays.asList(arrayOfClass)); 
    return linkedHashSet;
  }
  
  public static Set<Method> getAllMethods(Class<?> paramClass, Predicate<? super Method>... paramVarArgs) {
    HashSet<Method> hashSet = Sets.newHashSet();
    for (Class<?> clazz : getAllSuperTypes(paramClass, (Predicate<? super Class<?>>[])new Predicate[0]))
      hashSet.addAll(getMethods(clazz, paramVarArgs)); 
    return hashSet;
  }
  
  public static Set<Method> getMethods(Class<?> paramClass, Predicate<? super Method>... paramVarArgs) {
    return filter(paramClass.isInterface() ? paramClass.getMethods() : paramClass.getDeclaredMethods(), paramVarArgs);
  }
  
  public static Set<Constructor> getAllConstructors(Class<?> paramClass, Predicate<? super Constructor>... paramVarArgs) {
    HashSet<Constructor> hashSet = Sets.newHashSet();
    for (Class<?> clazz : getAllSuperTypes(paramClass, (Predicate<? super Class<?>>[])new Predicate[0]))
      hashSet.addAll(getConstructors(clazz, paramVarArgs)); 
    return hashSet;
  }
  
  public static Set<Constructor> getConstructors(Class<?> paramClass, Predicate<? super Constructor>... paramVarArgs) {
    return filter((Constructor[])paramClass.getDeclaredConstructors(), paramVarArgs);
  }
  
  public static Set<Field> getAllFields(Class<?> paramClass, Predicate<? super Field>... paramVarArgs) {
    HashSet<Field> hashSet = Sets.newHashSet();
    for (Class<?> clazz : getAllSuperTypes(paramClass, (Predicate<? super Class<?>>[])new Predicate[0]))
      hashSet.addAll(getFields(clazz, paramVarArgs)); 
    return hashSet;
  }
  
  public static Set<Field> getFields(Class<?> paramClass, Predicate<? super Field>... paramVarArgs) {
    return filter(paramClass.getDeclaredFields(), paramVarArgs);
  }
  
  public static <T extends java.lang.reflect.AnnotatedElement> Set<Annotation> getAllAnnotations(T paramT, Predicate<Annotation>... paramVarArgs) {
    HashSet<Annotation> hashSet = Sets.newHashSet();
    if (paramT instanceof Class) {
      for (Class<?> clazz : getAllSuperTypes((Class)paramT, (Predicate<? super Class<?>>[])new Predicate[0]))
        hashSet.addAll(getAnnotations(clazz, paramVarArgs)); 
    } else {
      hashSet.addAll(getAnnotations(paramT, paramVarArgs));
    } 
    return hashSet;
  }
  
  public static <T extends java.lang.reflect.AnnotatedElement> Set<Annotation> getAnnotations(T paramT, Predicate<Annotation>... paramVarArgs) {
    return filter(paramT.getDeclaredAnnotations(), (Predicate<? super Annotation>[])paramVarArgs);
  }
  
  public static <T extends java.lang.reflect.AnnotatedElement> Set<T> getAll(Set<T> paramSet, Predicate<? super T>... paramVarArgs) {
    return Utils.isEmpty((Object[])paramVarArgs) ? paramSet : Sets.newHashSet(Iterables.filter(paramSet, Predicates.and((Predicate[])paramVarArgs)));
  }
  
  public static <T extends Member> Predicate<T> withName(final String name) {
    return new Predicate<T>() {
        public boolean apply(@Nullable T param1T) {
          return (param1T != null && param1T.getName().equals(name));
        }
      };
  }
  
  public static <T extends Member> Predicate<T> withPrefix(final String prefix) {
    return new Predicate<T>() {
        public boolean apply(@Nullable T param1T) {
          return (param1T != null && param1T.getName().startsWith(prefix));
        }
      };
  }
  
  public static <T extends java.lang.reflect.AnnotatedElement> Predicate<T> withPattern(final String regex) {
    return new Predicate<T>() {
        public boolean apply(@Nullable T param1T) {
          return Pattern.matches(regex, param1T.toString());
        }
      };
  }
  
  public static <T extends java.lang.reflect.AnnotatedElement> Predicate<T> withAnnotation(final Class<? extends Annotation> annotation) {
    return new Predicate<T>() {
        public boolean apply(@Nullable T param1T) {
          return (param1T != null && param1T.isAnnotationPresent(annotation));
        }
      };
  }
  
  public static <T extends java.lang.reflect.AnnotatedElement> Predicate<T> withAnnotations(Class<? extends Annotation>... annotations) {
    return new Predicate<T>() {
        public boolean apply(@Nullable T param1T) {
          return (param1T != null && Arrays.equals((Object[])annotations, (Object[])ReflectionUtils.annotationTypes(param1T.getAnnotations())));
        }
      };
  }
  
  public static <T extends java.lang.reflect.AnnotatedElement> Predicate<T> withAnnotation(final Annotation annotation) {
    return new Predicate<T>() {
        public boolean apply(@Nullable T param1T) {
          return (param1T != null && param1T.isAnnotationPresent(annotation.annotationType()) && ReflectionUtils
            .areAnnotationMembersMatching(param1T.getAnnotation((Class)annotation.annotationType()), annotation));
        }
      };
  }
  
  public static <T extends java.lang.reflect.AnnotatedElement> Predicate<T> withAnnotations(Annotation... annotations) {
    return new Predicate<T>() {
        public boolean apply(@Nullable T param1T) {
          if (param1T != null) {
            Annotation[] arrayOfAnnotation = param1T.getAnnotations();
            if (arrayOfAnnotation.length == annotations.length)
              for (byte b = 0; b < arrayOfAnnotation.length; b++) {
                if (!ReflectionUtils.areAnnotationMembersMatching(arrayOfAnnotation[b], annotations[b]))
                  return false; 
              }  
          } 
          return true;
        }
      };
  }
  
  public static Predicate<Member> withParameters(Class<?>... types) {
    return new Predicate<Member>() {
        public boolean apply(@Nullable Member param1Member) {
          return Arrays.equals((Object[])ReflectionUtils.parameterTypes(param1Member), (Object[])types);
        }
      };
  }
  
  public static Predicate<Member> withParametersAssignableTo(Class... types) {
    return new Predicate<Member>() {
        public boolean apply(@Nullable Member param1Member) {
          if (param1Member != null) {
            Class[] arrayOfClass = ReflectionUtils.parameterTypes(param1Member);
            if (arrayOfClass.length == types.length) {
              for (byte b = 0; b < arrayOfClass.length; b++) {
                if (!arrayOfClass[b].isAssignableFrom(types[b]) || (arrayOfClass[b] == Object.class && types[b] != Object.class))
                  return false; 
              } 
              return true;
            } 
          } 
          return false;
        }
      };
  }
  
  public static Predicate<Member> withParametersCount(final int count) {
    return new Predicate<Member>() {
        public boolean apply(@Nullable Member param1Member) {
          return (param1Member != null && (ReflectionUtils.parameterTypes(param1Member)).length == count);
        }
      };
  }
  
  public static Predicate<Member> withAnyParameterAnnotation(final Class<? extends Annotation> annotationClass) {
    return new Predicate<Member>() {
        public boolean apply(@Nullable Member param1Member) {
          return (param1Member != null && Iterables.any(ReflectionUtils.annotationTypes(ReflectionUtils.parameterAnnotations(param1Member)), new Predicate<Class<? extends Annotation>>() {
                public boolean apply(@Nullable Class<? extends Annotation> param2Class) {
                  return param2Class.equals(annotationClass);
                }
              }));
        }
      };
  }
  
  public static Predicate<Member> withAnyParameterAnnotation(final Annotation annotation) {
    return new Predicate<Member>() {
        public boolean apply(@Nullable Member param1Member) {
          return (param1Member != null && Iterables.any(ReflectionUtils.parameterAnnotations(param1Member), new Predicate<Annotation>() {
                public boolean apply(@Nullable Annotation param2Annotation) {
                  return ReflectionUtils.areAnnotationMembersMatching(annotation, param2Annotation);
                }
              }));
        }
      };
  }
  
  public static <T> Predicate<Field> withType(final Class<T> type) {
    return new Predicate<Field>() {
        public boolean apply(@Nullable Field param1Field) {
          return (param1Field != null && param1Field.getType().equals(type));
        }
      };
  }
  
  public static <T> Predicate<Field> withTypeAssignableTo(final Class<T> type) {
    return new Predicate<Field>() {
        public boolean apply(@Nullable Field param1Field) {
          return (param1Field != null && type.isAssignableFrom(param1Field.getType()));
        }
      };
  }
  
  public static <T> Predicate<Method> withReturnType(final Class<T> type) {
    return new Predicate<Method>() {
        public boolean apply(@Nullable Method param1Method) {
          return (param1Method != null && param1Method.getReturnType().equals(type));
        }
      };
  }
  
  public static <T> Predicate<Method> withReturnTypeAssignableTo(final Class<T> type) {
    return new Predicate<Method>() {
        public boolean apply(@Nullable Method param1Method) {
          return (param1Method != null && type.isAssignableFrom(param1Method.getReturnType()));
        }
      };
  }
  
  public static <T extends Member> Predicate<T> withModifier(final int mod) {
    return new Predicate<T>() {
        public boolean apply(@Nullable T param1T) {
          return (param1T != null && (param1T.getModifiers() & mod) != 0);
        }
      };
  }
  
  public static Predicate<Class<?>> withClassModifier(final int mod) {
    return new Predicate<Class<?>>() {
        public boolean apply(@Nullable Class<?> param1Class) {
          return (param1Class != null && (param1Class.getModifiers() & mod) != 0);
        }
      };
  }
  
  public static Class<?> forName(String paramString, ClassLoader... paramVarArgs) {
    String str;
    if (getPrimitiveNames().contains(paramString))
      return getPrimitiveTypes().get(getPrimitiveNames().indexOf(paramString)); 
    if (paramString.contains("[")) {
      int i = paramString.indexOf("[");
      str = paramString.substring(0, i);
      String str1 = paramString.substring(i).replace("]", "");
      if (getPrimitiveNames().contains(str)) {
        str = getPrimitiveDescriptors().get(getPrimitiveNames().indexOf(str));
      } else {
        str = "L" + str + ";";
      } 
      str = str1 + str;
    } else {
      str = paramString;
    } 
    ArrayList<ReflectionsException> arrayList = Lists.newArrayList();
    for (ClassLoader classLoader : ClasspathHelper.classLoaders(paramVarArgs)) {
      if (str.contains("["))
        try {
          return Class.forName(str, false, classLoader);
        } catch (Throwable throwable) {
          arrayList.add(new ReflectionsException("could not get type for name " + paramString, throwable));
        }  
      try {
        return classLoader.loadClass(str);
      } catch (Throwable throwable) {
        arrayList.add(new ReflectionsException("could not get type for name " + paramString, throwable));
      } 
    } 
    if (Reflections.log != null)
      for (ReflectionsException reflectionsException : arrayList)
        Reflections.log.warn("could not get type for name " + paramString + " from any class loader", reflectionsException);  
    return null;
  }
  
  public static <T> List<Class<? extends T>> forNames(Iterable<String> paramIterable, ClassLoader... paramVarArgs) {
    ArrayList<Class<?>> arrayList = new ArrayList();
    for (String str : paramIterable) {
      Class<?> clazz = forName(str, paramVarArgs);
      if (clazz != null)
        arrayList.add(clazz); 
    } 
    return (List)arrayList;
  }
  
  private static Class[] parameterTypes(Member paramMember) {
    return (paramMember != null) ? (
      (paramMember.getClass() == Method.class) ? ((Method)paramMember).getParameterTypes() : (
      (paramMember.getClass() == Constructor.class) ? ((Constructor)paramMember).getParameterTypes() : null)) : null;
  }
  
  private static Set<Annotation> parameterAnnotations(Member paramMember) {
    HashSet<? super Annotation> hashSet = Sets.newHashSet();
    Annotation[][] arrayOfAnnotation = (paramMember instanceof Method) ? ((Method)paramMember).getParameterAnnotations() : ((paramMember instanceof Constructor) ? ((Constructor)paramMember).getParameterAnnotations() : (Annotation[][])null);
    for (Annotation[] arrayOfAnnotation1 : arrayOfAnnotation)
      Collections.addAll(hashSet, arrayOfAnnotation1); 
    return (Set)hashSet;
  }
  
  private static Set<Class<? extends Annotation>> annotationTypes(Iterable<Annotation> paramIterable) {
    HashSet<Class<? extends Annotation>> hashSet = Sets.newHashSet();
    for (Annotation annotation : paramIterable)
      hashSet.add(annotation.annotationType()); 
    return hashSet;
  }
  
  private static Class<? extends Annotation>[] annotationTypes(Annotation[] paramArrayOfAnnotation) {
    Class[] arrayOfClass = new Class[paramArrayOfAnnotation.length];
    for (byte b = 0; b < paramArrayOfAnnotation.length; ) {
      arrayOfClass[b] = paramArrayOfAnnotation[b].annotationType();
      b++;
    } 
    return (Class<? extends Annotation>[])arrayOfClass;
  }
  
  private static void initPrimitives() {
    if (primitiveNames == null) {
      primitiveNames = Lists.newArrayList((Object[])new String[] { "boolean", "char", "byte", "short", "int", "long", "float", "double", "void" });
      primitiveTypes = Lists.newArrayList((Object[])new Class[] { boolean.class, char.class, byte.class, short.class, int.class, long.class, float.class, double.class, void.class });
      primitiveDescriptors = Lists.newArrayList((Object[])new String[] { "Z", "C", "B", "S", "I", "J", "F", "D", "V" });
    } 
  }
  
  private static List<String> getPrimitiveNames() {
    initPrimitives();
    return primitiveNames;
  }
  
  private static List<Class> getPrimitiveTypes() {
    initPrimitives();
    return primitiveTypes;
  }
  
  private static List<String> getPrimitiveDescriptors() {
    initPrimitives();
    return primitiveDescriptors;
  }
  
  static <T> Set<T> filter(T[] paramArrayOfT, Predicate<? super T>... paramVarArgs) {
    return Utils.isEmpty((Object[])paramVarArgs) ? Sets.newHashSet((Object[])paramArrayOfT) : 
      Sets.newHashSet(Iterables.filter(Arrays.asList(paramArrayOfT), Predicates.and((Predicate[])paramVarArgs)));
  }
  
  static <T> Set<T> filter(Iterable<T> paramIterable, Predicate<? super T>... paramVarArgs) {
    return Utils.isEmpty((Object[])paramVarArgs) ? Sets.newHashSet(paramIterable) : 
      Sets.newHashSet(Iterables.filter(paramIterable, Predicates.and((Predicate[])paramVarArgs)));
  }
  
  private static boolean areAnnotationMembersMatching(Annotation paramAnnotation1, Annotation paramAnnotation2) {
    if (paramAnnotation2 != null && paramAnnotation1.annotationType() == paramAnnotation2.annotationType()) {
      for (Method method : paramAnnotation1.annotationType().getDeclaredMethods()) {
        try {
          if (!method.invoke(paramAnnotation1, new Object[0]).equals(method.invoke(paramAnnotation2, new Object[0])))
            return false; 
        } catch (Exception exception) {
          throw new ReflectionsException(String.format("could not invoke method %s on annotation %s", new Object[] { method.getName(), paramAnnotation1.annotationType() }), exception);
        } 
      } 
      return true;
    } 
    return false;
  }
}
