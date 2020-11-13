package javassist.scopedpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;

public class ScopedClassPoolRepositoryImpl implements ScopedClassPoolRepository {
  private static final ScopedClassPoolRepositoryImpl instance = new ScopedClassPoolRepositoryImpl();
  
  private boolean prune = true;
  
  boolean pruneWhenCached;
  
  protected Map registeredCLs = Collections.synchronizedMap(new WeakHashMap<Object, Object>());
  
  protected ClassPool classpool;
  
  protected ScopedClassPoolFactory factory = new ScopedClassPoolFactoryImpl();
  
  public static ScopedClassPoolRepository getInstance() {
    return instance;
  }
  
  private ScopedClassPoolRepositoryImpl() {
    this.classpool = ClassPool.getDefault();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    this.classpool.insertClassPath((ClassPath)new LoaderClassPath(classLoader));
  }
  
  public boolean isPrune() {
    return this.prune;
  }
  
  public void setPrune(boolean paramBoolean) {
    this.prune = paramBoolean;
  }
  
  public ScopedClassPool createScopedClassPool(ClassLoader paramClassLoader, ClassPool paramClassPool) {
    return this.factory.create(paramClassLoader, paramClassPool, this);
  }
  
  public ClassPool findClassPool(ClassLoader paramClassLoader) {
    if (paramClassLoader == null)
      return registerClassLoader(ClassLoader.getSystemClassLoader()); 
    return registerClassLoader(paramClassLoader);
  }
  
  public ClassPool registerClassLoader(ClassLoader paramClassLoader) {
    synchronized (this.registeredCLs) {
      if (this.registeredCLs.containsKey(paramClassLoader))
        return (ClassPool)this.registeredCLs.get(paramClassLoader); 
      ScopedClassPool scopedClassPool = createScopedClassPool(paramClassLoader, this.classpool);
      this.registeredCLs.put(paramClassLoader, scopedClassPool);
      return scopedClassPool;
    } 
  }
  
  public Map getRegisteredCLs() {
    clearUnregisteredClassLoaders();
    return this.registeredCLs;
  }
  
  public void clearUnregisteredClassLoaders() {
    ArrayList<ClassLoader> arrayList = null;
    synchronized (this.registeredCLs) {
      Iterator<ScopedClassPool> iterator = this.registeredCLs.values().iterator();
      while (iterator.hasNext()) {
        ScopedClassPool scopedClassPool = iterator.next();
        if (scopedClassPool.isUnloadedClassLoader()) {
          iterator.remove();
          ClassLoader classLoader = scopedClassPool.getClassLoader();
          if (classLoader != null) {
            if (arrayList == null)
              arrayList = new ArrayList(); 
            arrayList.add(classLoader);
          } 
        } 
      } 
      if (arrayList != null)
        for (byte b = 0; b < arrayList.size(); b++)
          unregisterClassLoader(arrayList.get(b));  
    } 
  }
  
  public void unregisterClassLoader(ClassLoader paramClassLoader) {
    synchronized (this.registeredCLs) {
      ScopedClassPool scopedClassPool = (ScopedClassPool)this.registeredCLs.remove(paramClassLoader);
      if (scopedClassPool != null)
        scopedClassPool.close(); 
    } 
  }
  
  public void insertDelegate(ScopedClassPoolRepository paramScopedClassPoolRepository) {}
  
  public void setClassPoolFactory(ScopedClassPoolFactory paramScopedClassPoolFactory) {
    this.factory = paramScopedClassPoolFactory;
  }
  
  public ScopedClassPoolFactory getClassPoolFactory() {
    return this.factory;
  }
}
