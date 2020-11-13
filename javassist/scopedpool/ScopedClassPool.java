package javassist.scopedpool;

import java.lang.ref.WeakReference;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.Map;
import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class ScopedClassPool extends ClassPool {
  protected ScopedClassPoolRepository repository;
  
  protected WeakReference classLoader;
  
  protected LoaderClassPath classPath;
  
  protected SoftValueHashMap softcache = new SoftValueHashMap();
  
  boolean isBootstrapCl = true;
  
  static {
    ClassPool.doPruning = false;
    ClassPool.releaseUnmodifiedClassFile = false;
  }
  
  protected ScopedClassPool(ClassLoader paramClassLoader, ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository) {
    this(paramClassLoader, paramClassPool, paramScopedClassPoolRepository, false);
  }
  
  protected ScopedClassPool(ClassLoader paramClassLoader, ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository, boolean paramBoolean) {
    super(paramClassPool);
    this.repository = paramScopedClassPoolRepository;
    this.classLoader = new WeakReference<ClassLoader>(paramClassLoader);
    if (paramClassLoader != null) {
      this.classPath = new LoaderClassPath(paramClassLoader);
      insertClassPath((ClassPath)this.classPath);
    } 
    this.childFirstLookup = true;
    if (!paramBoolean && paramClassLoader == null)
      this.isBootstrapCl = true; 
  }
  
  public ClassLoader getClassLoader() {
    ClassLoader classLoader = getClassLoader0();
    if (classLoader == null && !this.isBootstrapCl)
      throw new IllegalStateException("ClassLoader has been garbage collected"); 
    return classLoader;
  }
  
  protected ClassLoader getClassLoader0() {
    return this.classLoader.get();
  }
  
  public void close() {
    removeClassPath((ClassPath)this.classPath);
    this.classPath.close();
    this.classes.clear();
    this.softcache.clear();
  }
  
  public synchronized void flushClass(String paramString) {
    this.classes.remove(paramString);
    this.softcache.remove(paramString);
  }
  
  public synchronized void soften(CtClass paramCtClass) {
    if (this.repository.isPrune())
      paramCtClass.prune(); 
    this.classes.remove(paramCtClass.getName());
    this.softcache.put(paramCtClass.getName(), paramCtClass);
  }
  
  public boolean isUnloadedClassLoader() {
    return false;
  }
  
  protected CtClass getCached(String paramString) {
    CtClass ctClass = getCachedLocally(paramString);
    if (ctClass == null) {
      boolean bool = false;
      ClassLoader classLoader = getClassLoader0();
      if (classLoader != null) {
        int i = paramString.lastIndexOf('$');
        String str = null;
        if (i < 0) {
          str = paramString.replaceAll("[\\.]", "/") + ".class";
        } else {
          str = paramString.substring(0, i).replaceAll("[\\.]", "/") + paramString.substring(i) + ".class";
        } 
        bool = (classLoader.getResource(str) != null) ? true : false;
      } 
      if (!bool) {
        Map map = this.repository.getRegisteredCLs();
        synchronized (map) {
          Iterator<ScopedClassPool> iterator = map.values().iterator();
          while (iterator.hasNext()) {
            ScopedClassPool scopedClassPool = iterator.next();
            if (scopedClassPool.isUnloadedClassLoader()) {
              this.repository.unregisterClassLoader(scopedClassPool
                  .getClassLoader());
              continue;
            } 
            ctClass = scopedClassPool.getCachedLocally(paramString);
            if (ctClass != null)
              return ctClass; 
          } 
        } 
      } 
    } 
    return ctClass;
  }
  
  protected void cacheCtClass(String paramString, CtClass paramCtClass, boolean paramBoolean) {
    if (paramBoolean) {
      super.cacheCtClass(paramString, paramCtClass, paramBoolean);
    } else {
      if (this.repository.isPrune())
        paramCtClass.prune(); 
      this.softcache.put(paramString, paramCtClass);
    } 
  }
  
  public void lockInCache(CtClass paramCtClass) {
    super.cacheCtClass(paramCtClass.getName(), paramCtClass, false);
  }
  
  protected CtClass getCachedLocally(String paramString) {
    CtClass ctClass = (CtClass)this.classes.get(paramString);
    if (ctClass != null)
      return ctClass; 
    synchronized (this.softcache) {
      return (CtClass)this.softcache.get(paramString);
    } 
  }
  
  public synchronized CtClass getLocally(String paramString) throws NotFoundException {
    this.softcache.remove(paramString);
    CtClass ctClass = (CtClass)this.classes.get(paramString);
    if (ctClass == null) {
      ctClass = createCtClass(paramString, true);
      if (ctClass == null)
        throw new NotFoundException(paramString); 
      super.cacheCtClass(paramString, ctClass, false);
    } 
    return ctClass;
  }
  
  public Class toClass(CtClass paramCtClass, ClassLoader paramClassLoader, ProtectionDomain paramProtectionDomain) throws CannotCompileException {
    lockInCache(paramCtClass);
    return super.toClass(paramCtClass, getClassLoader0(), paramProtectionDomain);
  }
}
