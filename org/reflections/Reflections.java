package org.reflections;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.serializers.Serializer;
import org.reflections.serializers.XmlSerializer;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.reflections.util.Utils;
import org.reflections.vfs.Vfs;
import org.slf4j.Logger;

public class Reflections {
  @Nullable
  public static Logger log = Utils.findLogger(Reflections.class);
  
  protected final transient Configuration configuration;
  
  protected Store store;
  
  public Reflections(Configuration paramConfiguration) {
    this.configuration = paramConfiguration;
    this.store = new Store(paramConfiguration);
    if (paramConfiguration.getScanners() != null && !paramConfiguration.getScanners().isEmpty()) {
      for (Scanner scanner : paramConfiguration.getScanners()) {
        scanner.setConfiguration(paramConfiguration);
        scanner.setStore(this.store.getOrCreate(scanner.getClass().getSimpleName()));
      } 
      scan();
      if (paramConfiguration.shouldExpandSuperTypes())
        expandSuperTypes(); 
    } 
  }
  
  public Reflections(String paramString, @Nullable Scanner... paramVarArgs) {
    this(new Object[] { paramString, paramVarArgs });
  }
  
  public Reflections(Object... paramVarArgs) {
    this((Configuration)ConfigurationBuilder.build(paramVarArgs));
  }
  
  protected Reflections() {
    this.configuration = (Configuration)new ConfigurationBuilder();
    this.store = new Store(this.configuration);
  }
  
  protected void scan() {
    if (this.configuration.getUrls() == null || this.configuration.getUrls().isEmpty()) {
      if (log != null)
        log.warn("given scan urls are empty. set urls in the configuration"); 
      return;
    } 
    if (log != null && log.isDebugEnabled())
      log.debug("going to scan these urls:\n" + Joiner.on("\n").join(this.configuration.getUrls())); 
    long l = System.currentTimeMillis();
    byte b = 0;
    ExecutorService executorService = this.configuration.getExecutorService();
    ArrayList<Future<?>> arrayList = Lists.newArrayList();
    for (URL uRL : this.configuration.getUrls()) {
      try {
        if (executorService != null) {
          arrayList.add(executorService.submit(new Runnable() {
                  public void run() {
                    if (Reflections.log != null && Reflections.log.isDebugEnabled())
                      Reflections.log.debug("[" + Thread.currentThread().toString() + "] scanning " + url); 
                    Reflections.this.scan(url);
                  }
                }));
        } else {
          scan(uRL);
        } 
        b++;
      } catch (ReflectionsException reflectionsException) {
        if (log != null && log.isWarnEnabled())
          log.warn("could not create Vfs.Dir from url. ignoring the exception and continuing", reflectionsException); 
      } 
    } 
    if (executorService != null)
      for (Future<?> future : arrayList) {
        try {
          future.get();
        } catch (Exception exception) {
          throw new RuntimeException(exception);
        } 
      }  
    l = System.currentTimeMillis() - l;
    if (executorService != null)
      executorService.shutdown(); 
    if (log != null) {
      int i = 0;
      int j = 0;
      for (String str : this.store.keySet()) {
        i += this.store.get(str).keySet().size();
        j += this.store.get(str).size();
      } 
      log.info(String.format("Reflections took %d ms to scan %d urls, producing %d keys and %d values %s", new Object[] { Long.valueOf(l), Integer.valueOf(b), Integer.valueOf(i), Integer.valueOf(j), (executorService != null && executorService instanceof ThreadPoolExecutor) ? 
              
              String.format("[using %d cores]", new Object[] { Integer.valueOf(((ThreadPoolExecutor)executorService).getMaximumPoolSize()) }) : "" }));
    } 
  }
  
  protected void scan(URL paramURL) {
    Vfs.Dir dir = Vfs.fromURL(paramURL);
    try {
      for (Vfs.File file : dir.getFiles()) {
        Predicate<String> predicate = this.configuration.getInputsFilter();
        String str1 = file.getRelativePath();
        String str2 = str1.replace('/', '.');
        if (predicate == null || predicate.apply(str1) || predicate.apply(str2)) {
          Object object = null;
          for (Scanner scanner : this.configuration.getScanners()) {
            try {
              if (scanner.acceptsInput(str1) || scanner.acceptResult(str2))
                object = scanner.scan(file, object); 
            } catch (Exception exception) {
              if (log != null && log.isDebugEnabled())
                log.debug("could not scan file " + file.getRelativePath() + " in url " + paramURL.toExternalForm() + " with scanner " + scanner.getClass().getSimpleName(), exception); 
            } 
          } 
        } 
      } 
    } finally {
      dir.close();
    } 
  }
  
  public static Reflections collect() {
    return collect("META-INF/reflections/", (Predicate<String>)(new FilterBuilder()).include(".*-reflections.xml"), new Serializer[0]);
  }
  
  public static Reflections collect(String paramString, Predicate<String> paramPredicate, @Nullable Serializer... paramVarArgs) {
    Serializer serializer = (Serializer)((paramVarArgs != null && paramVarArgs.length == 1) ? paramVarArgs[0] : new XmlSerializer());
    Collection collection = ClasspathHelper.forPackage(paramString, new ClassLoader[0]);
    if (collection.isEmpty())
      return null; 
    long l = System.currentTimeMillis();
    Reflections reflections = new Reflections();
    Iterable iterable = Vfs.findFiles(collection, paramString, paramPredicate);
    for (Vfs.File file : iterable) {
      InputStream inputStream = null;
      try {
        inputStream = file.openInputStream();
        reflections.merge(serializer.read(inputStream));
      } catch (IOException iOException) {
        throw new ReflectionsException("could not merge " + file, iOException);
      } finally {
        Utils.close(inputStream);
      } 
    } 
    if (log != null) {
      Store store = reflections.getStore();
      int i = 0;
      int j = 0;
      for (String str : store.keySet()) {
        i += store.get(str).keySet().size();
        j += store.get(str).size();
      } 
      log.info(String.format("Reflections took %d ms to collect %d url%s, producing %d keys and %d values [%s]", new Object[] { Long.valueOf(System.currentTimeMillis() - l), Integer.valueOf(collection.size()), (collection.size() > 1) ? "s" : "", Integer.valueOf(i), Integer.valueOf(j), Joiner.on(", ").join(collection) }));
    } 
    return reflections;
  }
  
  public Reflections collect(InputStream paramInputStream) {
    try {
      merge(this.configuration.getSerializer().read(paramInputStream));
      if (log != null)
        log.info("Reflections collected metadata from input stream using serializer " + this.configuration.getSerializer().getClass().getName()); 
    } catch (Exception exception) {
      throw new ReflectionsException("could not merge input stream", exception);
    } 
    return this;
  }
  
  public Reflections collect(File paramFile) {
    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(paramFile);
      return collect(fileInputStream);
    } catch (FileNotFoundException fileNotFoundException) {
      throw new ReflectionsException("could not obtain input stream from file " + paramFile, fileNotFoundException);
    } finally {
      Utils.close(fileInputStream);
    } 
  }
  
  public Reflections merge(Reflections paramReflections) {
    if (paramReflections.store != null)
      for (String str : paramReflections.store.keySet()) {
        Multimap<String, String> multimap = paramReflections.store.get(str);
        for (String str1 : multimap.keySet()) {
          for (String str2 : multimap.get(str1))
            this.store.getOrCreate(str).put(str1, str2); 
        } 
      }  
    return this;
  }
  
  public void expandSuperTypes() {
    if (this.store.keySet().contains(index((Class)SubTypesScanner.class))) {
      Multimap<String, String> multimap = this.store.get(index((Class)SubTypesScanner.class));
      Sets.SetView setView = Sets.difference(multimap.keySet(), Sets.newHashSet(multimap.values()));
      HashMultimap hashMultimap = HashMultimap.create();
      for (UnmodifiableIterator<String> unmodifiableIterator = setView.iterator(); unmodifiableIterator.hasNext(); ) {
        String str = unmodifiableIterator.next();
        Class<?> clazz = ReflectionUtils.forName(str, new ClassLoader[0]);
        if (clazz != null)
          expandSupertypes((Multimap<String, String>)hashMultimap, str, clazz); 
      } 
      multimap.putAll((Multimap)hashMultimap);
    } 
  }
  
  private void expandSupertypes(Multimap<String, String> paramMultimap, String paramString, Class<?> paramClass) {
    for (Class<?> clazz : ReflectionUtils.getSuperTypes(paramClass)) {
      if (paramMultimap.put(clazz.getName(), paramString)) {
        if (log != null)
          log.debug("expanded subtype {} -> {}", clazz.getName(), paramString); 
        expandSupertypes(paramMultimap, clazz.getName(), clazz);
      } 
    } 
  }
  
  public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> paramClass) {
    return Sets.newHashSet(ReflectionUtils.forNames(this.store
          .getAll(index((Class)SubTypesScanner.class), Arrays.asList(new String[] { paramClass.getName() })), loaders()));
  }
  
  public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> paramClass) {
    return getTypesAnnotatedWith(paramClass, false);
  }
  
  public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> paramClass, boolean paramBoolean) {
    Iterable<String> iterable1 = this.store.get(index((Class)TypeAnnotationsScanner.class), new String[] { paramClass.getName() });
    Iterable<String> iterable2 = getAllAnnotated(iterable1, paramClass.isAnnotationPresent((Class)Inherited.class), paramBoolean);
    return Sets.newHashSet(Iterables.concat(ReflectionUtils.forNames(iterable1, loaders()), ReflectionUtils.forNames(iterable2, loaders())));
  }
  
  public Set<Class<?>> getTypesAnnotatedWith(Annotation paramAnnotation) {
    return getTypesAnnotatedWith(paramAnnotation, false);
  }
  
  public Set<Class<?>> getTypesAnnotatedWith(Annotation paramAnnotation, boolean paramBoolean) {
    Iterable<String> iterable1 = this.store.get(index((Class)TypeAnnotationsScanner.class), new String[] { paramAnnotation.annotationType().getName() });
    Set<?> set = ReflectionUtils.filter(ReflectionUtils.forNames(iterable1, loaders()), (Predicate<?>[])new Predicate[] { ReflectionUtils.withAnnotation(paramAnnotation) });
    Iterable<String> iterable2 = getAllAnnotated(Utils.names(set), paramAnnotation.annotationType().isAnnotationPresent((Class)Inherited.class), paramBoolean);
    return Sets.newHashSet(Iterables.concat(set, ReflectionUtils.forNames(ReflectionUtils.filter(iterable2, (Predicate<? super String>[])new Predicate[] { Predicates.not(Predicates.in(Sets.newHashSet(iterable1))) }), loaders())));
  }
  
  protected Iterable<String> getAllAnnotated(Iterable<String> paramIterable, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean2) {
      if (paramBoolean1) {
        Iterable<String> iterable1 = this.store.get(index((Class)SubTypesScanner.class), ReflectionUtils.filter(paramIterable, (Predicate<? super String>[])new Predicate[] { new Predicate<String>() {
                  public boolean apply(@Nullable String param1String) {
                    Class<?> clazz = ReflectionUtils.forName(param1String, Reflections.this.loaders());
                    return (clazz != null && !clazz.isInterface());
                  }
                } }));
        return Iterables.concat(iterable1, this.store.getAll(index((Class)SubTypesScanner.class), iterable1));
      } 
      return paramIterable;
    } 
    Iterable<String> iterable = Iterables.concat(paramIterable, this.store.getAll(index((Class)TypeAnnotationsScanner.class), paramIterable));
    return Iterables.concat(iterable, this.store.getAll(index((Class)SubTypesScanner.class), iterable));
  }
  
  public Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> paramClass) {
    Iterable<String> iterable = this.store.get(index((Class)MethodAnnotationsScanner.class), new String[] { paramClass.getName() });
    return Utils.getMethodsFromDescriptors(iterable, loaders());
  }
  
  public Set<Method> getMethodsAnnotatedWith(Annotation paramAnnotation) {
    return ReflectionUtils.filter(getMethodsAnnotatedWith(paramAnnotation.annotationType()), (Predicate<? super Method>[])new Predicate[] { ReflectionUtils.withAnnotation(paramAnnotation) });
  }
  
  public Set<Method> getMethodsMatchParams(Class<?>... paramVarArgs) {
    return Utils.getMethodsFromDescriptors(this.store.get(index((Class)MethodParameterScanner.class), new String[] { Utils.names(paramVarArgs).toString() }), loaders());
  }
  
  public Set<Method> getMethodsReturn(Class paramClass) {
    return Utils.getMethodsFromDescriptors(this.store.get(index((Class)MethodParameterScanner.class), Utils.names(new Class[] { paramClass })), loaders());
  }
  
  public Set<Method> getMethodsWithAnyParamAnnotated(Class<? extends Annotation> paramClass) {
    return Utils.getMethodsFromDescriptors(this.store.get(index((Class)MethodParameterScanner.class), new String[] { paramClass.getName() }), loaders());
  }
  
  public Set<Method> getMethodsWithAnyParamAnnotated(Annotation paramAnnotation) {
    return ReflectionUtils.filter(getMethodsWithAnyParamAnnotated(paramAnnotation.annotationType()), (Predicate<? super Method>[])new Predicate[] { ReflectionUtils.withAnyParameterAnnotation(paramAnnotation) });
  }
  
  public Set<Constructor> getConstructorsAnnotatedWith(Class<? extends Annotation> paramClass) {
    Iterable<String> iterable = this.store.get(index((Class)MethodAnnotationsScanner.class), new String[] { paramClass.getName() });
    return Utils.getConstructorsFromDescriptors(iterable, loaders());
  }
  
  public Set<Constructor> getConstructorsAnnotatedWith(Annotation paramAnnotation) {
    return ReflectionUtils.filter(getConstructorsAnnotatedWith(paramAnnotation.annotationType()), (Predicate<? super Constructor>[])new Predicate[] { ReflectionUtils.withAnnotation(paramAnnotation) });
  }
  
  public Set<Constructor> getConstructorsMatchParams(Class<?>... paramVarArgs) {
    return Utils.getConstructorsFromDescriptors(this.store.get(index((Class)MethodParameterScanner.class), new String[] { Utils.names(paramVarArgs).toString() }), loaders());
  }
  
  public Set<Constructor> getConstructorsWithAnyParamAnnotated(Class<? extends Annotation> paramClass) {
    return Utils.getConstructorsFromDescriptors(this.store.get(index((Class)MethodParameterScanner.class), new String[] { paramClass.getName() }), loaders());
  }
  
  public Set<Constructor> getConstructorsWithAnyParamAnnotated(Annotation paramAnnotation) {
    return ReflectionUtils.filter(getConstructorsWithAnyParamAnnotated(paramAnnotation.annotationType()), (Predicate<? super Constructor>[])new Predicate[] { ReflectionUtils.withAnyParameterAnnotation(paramAnnotation) });
  }
  
  public Set<Field> getFieldsAnnotatedWith(Class<? extends Annotation> paramClass) {
    HashSet<Field> hashSet = Sets.newHashSet();
    for (String str : this.store.get(index((Class)FieldAnnotationsScanner.class), new String[] { paramClass.getName() }))
      hashSet.add(Utils.getFieldFromString(str, loaders())); 
    return hashSet;
  }
  
  public Set<Field> getFieldsAnnotatedWith(Annotation paramAnnotation) {
    return ReflectionUtils.filter(getFieldsAnnotatedWith(paramAnnotation.annotationType()), (Predicate<? super Field>[])new Predicate[] { ReflectionUtils.withAnnotation(paramAnnotation) });
  }
  
  public Set<String> getResources(Predicate<String> paramPredicate) {
    Iterable<String> iterable = Iterables.filter(this.store.get(index((Class)ResourcesScanner.class)).keySet(), paramPredicate);
    return Sets.newHashSet(this.store.get(index((Class)ResourcesScanner.class), iterable));
  }
  
  public Set<String> getResources(final Pattern pattern) {
    return getResources(new Predicate<String>() {
          public boolean apply(String param1String) {
            return pattern.matcher(param1String).matches();
          }
        });
  }
  
  public List<String> getMethodParamNames(Method paramMethod) {
    Iterable<String> iterable = this.store.get(index((Class)MethodParameterNamesScanner.class), new String[] { Utils.name(paramMethod) });
    return !Iterables.isEmpty(iterable) ? Arrays.<String>asList(((String)Iterables.getOnlyElement(iterable)).split(", ")) : Arrays.<String>asList(new String[0]);
  }
  
  public List<String> getConstructorParamNames(Constructor paramConstructor) {
    Iterable<String> iterable = this.store.get(index((Class)MethodParameterNamesScanner.class), new String[] { Utils.name(paramConstructor) });
    return !Iterables.isEmpty(iterable) ? Arrays.<String>asList(((String)Iterables.getOnlyElement(iterable)).split(", ")) : Arrays.<String>asList(new String[0]);
  }
  
  public Set<Member> getFieldUsage(Field paramField) {
    return Utils.getMembersFromDescriptors(this.store.get(index((Class)MemberUsageScanner.class), new String[] { Utils.name(paramField) }), new ClassLoader[0]);
  }
  
  public Set<Member> getMethodUsage(Method paramMethod) {
    return Utils.getMembersFromDescriptors(this.store.get(index((Class)MemberUsageScanner.class), new String[] { Utils.name(paramMethod) }), new ClassLoader[0]);
  }
  
  public Set<Member> getConstructorUsage(Constructor paramConstructor) {
    return Utils.getMembersFromDescriptors(this.store.get(index((Class)MemberUsageScanner.class), new String[] { Utils.name(paramConstructor) }), new ClassLoader[0]);
  }
  
  public Set<String> getAllTypes() {
    HashSet<String> hashSet = Sets.newHashSet(this.store.getAll(index((Class)SubTypesScanner.class), Object.class.getName()));
    if (hashSet.isEmpty())
      throw new ReflectionsException("Couldn't find subtypes of Object. Make sure SubTypesScanner initialized to include Object class - new SubTypesScanner(false)"); 
    return hashSet;
  }
  
  public Store getStore() {
    return this.store;
  }
  
  public Configuration getConfiguration() {
    return this.configuration;
  }
  
  public File save(String paramString) {
    return save(paramString, this.configuration.getSerializer());
  }
  
  public File save(String paramString, Serializer paramSerializer) {
    File file = paramSerializer.save(this, paramString);
    if (log != null)
      log.info("Reflections successfully saved in " + file.getAbsolutePath() + " using " + paramSerializer.getClass().getSimpleName()); 
    return file;
  }
  
  private static String index(Class<? extends Scanner> paramClass) {
    return paramClass.getSimpleName();
  }
  
  private ClassLoader[] loaders() {
    return this.configuration.getClassLoaders();
  }
}
