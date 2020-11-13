package org.yaml.snakeyaml.constructor;

public class CustomClassLoaderConstructor extends Constructor {
  private ClassLoader loader = CustomClassLoaderConstructor.class.getClassLoader();
  
  public CustomClassLoaderConstructor(ClassLoader paramClassLoader) {
    this(Object.class, paramClassLoader);
  }
  
  public CustomClassLoaderConstructor(Class<? extends Object> paramClass, ClassLoader paramClassLoader) {
    super(paramClass);
    if (paramClassLoader == null)
      throw new NullPointerException("Loader must be provided."); 
    this.loader = paramClassLoader;
  }
  
  protected Class<?> getClassForName(String paramString) throws ClassNotFoundException {
    return Class.forName(paramString, true, this.loader);
  }
}
