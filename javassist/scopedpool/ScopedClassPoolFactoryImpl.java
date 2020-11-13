package javassist.scopedpool;

import javassist.ClassPool;

public class ScopedClassPoolFactoryImpl implements ScopedClassPoolFactory {
  public ScopedClassPool create(ClassLoader paramClassLoader, ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository) {
    return new ScopedClassPool(paramClassLoader, paramClassPool, paramScopedClassPoolRepository, false);
  }
  
  public ScopedClassPool create(ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository) {
    return new ScopedClassPool(null, paramClassPool, paramScopedClassPoolRepository, true);
  }
}
