package org.reflections.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.adapters.JavaReflectionAdapter;
import org.reflections.adapters.JavassistAdapter;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.serializers.Serializer;
import org.reflections.serializers.XmlSerializer;

public class ConfigurationBuilder implements Configuration {
  @Nonnull
  private Set<Scanner> scanners;
  
  @Nonnull
  private Set<URL> urls;
  
  protected MetadataAdapter metadataAdapter;
  
  @Nullable
  private Predicate<String> inputsFilter;
  
  private Serializer serializer;
  
  @Nullable
  private ExecutorService executorService;
  
  @Nullable
  private ClassLoader[] classLoaders;
  
  private boolean expandSuperTypes = true;
  
  public ConfigurationBuilder() {
    this.scanners = Sets.newHashSet((Object[])new Scanner[] { (Scanner)new TypeAnnotationsScanner(), (Scanner)new SubTypesScanner() });
    this.urls = Sets.newHashSet();
  }
  
  public static ConfigurationBuilder build(@Nullable Object... paramVarArgs) {
    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    ArrayList<Object> arrayList = Lists.newArrayList();
    if (paramVarArgs != null)
      for (Object object : paramVarArgs) {
        if (object != null)
          if (object.getClass().isArray()) {
            for (Object object1 : (Object[])object) {
              if (object1 != null)
                arrayList.add(object1); 
            } 
          } else if (object instanceof Iterable) {
            for (Object object1 : object) {
              if (object1 != null)
                arrayList.add(object1); 
            } 
          } else {
            arrayList.add(object);
          }  
      }  
    ArrayList<ClassLoader> arrayList1 = Lists.newArrayList();
    for (ClassLoader classLoader : arrayList) {
      if (classLoader instanceof ClassLoader)
        arrayList1.add(classLoader); 
    } 
    ClassLoader[] arrayOfClassLoader = arrayList1.isEmpty() ? null : arrayList1.<ClassLoader>toArray(new ClassLoader[arrayList1.size()]);
    FilterBuilder filterBuilder = new FilterBuilder();
    ArrayList<Scanner> arrayList2 = Lists.newArrayList();
    for (String str : arrayList) {
      if (str instanceof String) {
        configurationBuilder.addUrls(ClasspathHelper.forPackage(str, arrayOfClassLoader));
        filterBuilder.includePackage(new String[] { str });
        continue;
      } 
      if (str instanceof Class) {
        if (Scanner.class.isAssignableFrom((Class)str))
          try {
            configurationBuilder.addScanners(new Scanner[] { ((Class<Scanner>)str).newInstance() });
          } catch (Exception exception) {} 
        configurationBuilder.addUrls(new URL[] { ClasspathHelper.forClass((Class)str, arrayOfClassLoader) });
        filterBuilder.includePackage((Class)str);
        continue;
      } 
      if (str instanceof Scanner) {
        arrayList2.add((Scanner)str);
        continue;
      } 
      if (str instanceof URL) {
        configurationBuilder.addUrls(new URL[] { (URL)str });
        continue;
      } 
      if (str instanceof ClassLoader)
        continue; 
      if (str instanceof Predicate) {
        filterBuilder.add((Predicate<String>)str);
        continue;
      } 
      if (str instanceof ExecutorService) {
        configurationBuilder.setExecutorService((ExecutorService)str);
        continue;
      } 
      if (Reflections.log != null)
        throw new ReflectionsException("could not use param " + str); 
    } 
    if (configurationBuilder.getUrls().isEmpty())
      if (arrayOfClassLoader != null) {
        configurationBuilder.addUrls(ClasspathHelper.forClassLoader(arrayOfClassLoader));
      } else {
        configurationBuilder.addUrls(ClasspathHelper.forClassLoader());
      }  
    configurationBuilder.filterInputsBy(filterBuilder);
    if (!arrayList2.isEmpty())
      configurationBuilder.setScanners(arrayList2.<Scanner>toArray(new Scanner[arrayList2.size()])); 
    if (!arrayList1.isEmpty())
      configurationBuilder.addClassLoaders(arrayList1); 
    return configurationBuilder;
  }
  
  public ConfigurationBuilder forPackages(String... paramVarArgs) {
    for (String str : paramVarArgs)
      addUrls(ClasspathHelper.forPackage(str, new ClassLoader[0])); 
    return this;
  }
  
  @Nonnull
  public Set<Scanner> getScanners() {
    return this.scanners;
  }
  
  public ConfigurationBuilder setScanners(@Nonnull Scanner... paramVarArgs) {
    this.scanners.clear();
    return addScanners(paramVarArgs);
  }
  
  public ConfigurationBuilder addScanners(Scanner... paramVarArgs) {
    this.scanners.addAll(Sets.newHashSet((Object[])paramVarArgs));
    return this;
  }
  
  @Nonnull
  public Set<URL> getUrls() {
    return this.urls;
  }
  
  public ConfigurationBuilder setUrls(@Nonnull Collection<URL> paramCollection) {
    this.urls = Sets.newHashSet(paramCollection);
    return this;
  }
  
  public ConfigurationBuilder setUrls(URL... paramVarArgs) {
    this.urls = Sets.newHashSet((Object[])paramVarArgs);
    return this;
  }
  
  public ConfigurationBuilder addUrls(Collection<URL> paramCollection) {
    this.urls.addAll(paramCollection);
    return this;
  }
  
  public ConfigurationBuilder addUrls(URL... paramVarArgs) {
    this.urls.addAll(Sets.newHashSet((Object[])paramVarArgs));
    return this;
  }
  
  public MetadataAdapter getMetadataAdapter() {
    if (this.metadataAdapter != null)
      return this.metadataAdapter; 
    try {
      return this.metadataAdapter = (MetadataAdapter)new JavassistAdapter();
    } catch (Throwable throwable) {
      if (Reflections.log != null)
        Reflections.log.warn("could not create JavassistAdapter, using JavaReflectionAdapter", throwable); 
      return this.metadataAdapter = (MetadataAdapter)new JavaReflectionAdapter();
    } 
  }
  
  public ConfigurationBuilder setMetadataAdapter(MetadataAdapter paramMetadataAdapter) {
    this.metadataAdapter = paramMetadataAdapter;
    return this;
  }
  
  @Nullable
  public Predicate<String> getInputsFilter() {
    return this.inputsFilter;
  }
  
  public void setInputsFilter(@Nullable Predicate<String> paramPredicate) {
    this.inputsFilter = paramPredicate;
  }
  
  public ConfigurationBuilder filterInputsBy(Predicate<String> paramPredicate) {
    this.inputsFilter = paramPredicate;
    return this;
  }
  
  @Nullable
  public ExecutorService getExecutorService() {
    return this.executorService;
  }
  
  public ConfigurationBuilder setExecutorService(@Nullable ExecutorService paramExecutorService) {
    this.executorService = paramExecutorService;
    return this;
  }
  
  public ConfigurationBuilder useParallelExecutor() {
    return useParallelExecutor(Runtime.getRuntime().availableProcessors());
  }
  
  public ConfigurationBuilder useParallelExecutor(int paramInt) {
    ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("org.reflections-scanner-%d").build();
    setExecutorService(Executors.newFixedThreadPool(paramInt, threadFactory));
    return this;
  }
  
  public Serializer getSerializer() {
    return (this.serializer != null) ? this.serializer : (this.serializer = (Serializer)new XmlSerializer());
  }
  
  public ConfigurationBuilder setSerializer(Serializer paramSerializer) {
    this.serializer = paramSerializer;
    return this;
  }
  
  @Nullable
  public ClassLoader[] getClassLoaders() {
    return this.classLoaders;
  }
  
  public boolean shouldExpandSuperTypes() {
    return this.expandSuperTypes;
  }
  
  public ConfigurationBuilder setExpandSuperTypes(boolean paramBoolean) {
    this.expandSuperTypes = paramBoolean;
    return this;
  }
  
  public void setClassLoaders(@Nullable ClassLoader[] paramArrayOfClassLoader) {
    this.classLoaders = paramArrayOfClassLoader;
  }
  
  public ConfigurationBuilder addClassLoader(ClassLoader paramClassLoader) {
    return addClassLoaders(new ClassLoader[] { paramClassLoader });
  }
  
  public ConfigurationBuilder addClassLoaders(ClassLoader... paramVarArgs) {
    this.classLoaders = (this.classLoaders == null) ? paramVarArgs : (ClassLoader[])ObjectArrays.concat((Object[])this.classLoaders, (Object[])paramVarArgs, ClassLoader.class);
    return this;
  }
  
  public ConfigurationBuilder addClassLoaders(Collection<ClassLoader> paramCollection) {
    return addClassLoaders(paramCollection.<ClassLoader>toArray(new ClassLoader[paramCollection.size()]));
  }
}
