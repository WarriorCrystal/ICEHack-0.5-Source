package javassist;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.Hashtable;
import java.util.Vector;

public class Loader extends ClassLoader {
  private Hashtable notDefinedHere;
  
  private Vector notDefinedPackages;
  
  private ClassPool source;
  
  private Translator translator;
  
  private ProtectionDomain domain;
  
  public boolean doDelegation = true;
  
  public Loader() {
    this((ClassPool)null);
  }
  
  public Loader(ClassPool paramClassPool) {
    init(paramClassPool);
  }
  
  public Loader(ClassLoader paramClassLoader, ClassPool paramClassPool) {
    super(paramClassLoader);
    init(paramClassPool);
  }
  
  private void init(ClassPool paramClassPool) {
    this.notDefinedHere = new Hashtable<Object, Object>();
    this.notDefinedPackages = new Vector();
    this.source = paramClassPool;
    this.translator = null;
    this.domain = null;
    delegateLoadingOf("javassist.Loader");
  }
  
  public void delegateLoadingOf(String paramString) {
    if (paramString.endsWith(".")) {
      this.notDefinedPackages.addElement(paramString);
    } else {
      this.notDefinedHere.put(paramString, this);
    } 
  }
  
  public void setDomain(ProtectionDomain paramProtectionDomain) {
    this.domain = paramProtectionDomain;
  }
  
  public void setClassPool(ClassPool paramClassPool) {
    this.source = paramClassPool;
  }
  
  public void addTranslator(ClassPool paramClassPool, Translator paramTranslator) throws NotFoundException, CannotCompileException {
    this.source = paramClassPool;
    this.translator = paramTranslator;
    paramTranslator.start(paramClassPool);
  }
  
  public static void main(String[] paramArrayOfString) throws Throwable {
    Loader loader = new Loader();
    loader.run(paramArrayOfString);
  }
  
  public void run(String[] paramArrayOfString) throws Throwable {
    int i = paramArrayOfString.length - 1;
    if (i >= 0) {
      String[] arrayOfString = new String[i];
      for (byte b = 0; b < i; b++)
        arrayOfString[b] = paramArrayOfString[b + 1]; 
      run(paramArrayOfString[0], arrayOfString);
    } 
  }
  
  public void run(String paramString, String[] paramArrayOfString) throws Throwable {
    Class<?> clazz = loadClass(paramString);
    try {
      clazz.getDeclaredMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { paramArrayOfString });
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } 
  }
  
  protected Class loadClass(String paramString, boolean paramBoolean) throws ClassFormatError, ClassNotFoundException {
    paramString = paramString.intern();
    synchronized (paramString) {
      Class<?> clazz = findLoadedClass(paramString);
      if (clazz == null)
        clazz = loadClassByDelegation(paramString); 
      if (clazz == null)
        clazz = findClass(paramString); 
      if (clazz == null)
        clazz = delegateToParent(paramString); 
      if (paramBoolean)
        resolveClass(clazz); 
      return clazz;
    } 
  }
  
  protected Class findClass(String paramString) throws ClassNotFoundException {
    byte[] arrayOfByte;
    try {
      if (this.source != null) {
        if (this.translator != null)
          this.translator.onLoad(this.source, paramString); 
        try {
          arrayOfByte = this.source.get(paramString).toBytecode();
        } catch (NotFoundException notFoundException) {
          return null;
        } 
      } else {
        String str = "/" + paramString.replace('.', '/') + ".class";
        InputStream inputStream = getClass().getResourceAsStream(str);
        if (inputStream == null)
          return null; 
        arrayOfByte = ClassPoolTail2.readStream(inputStream);
      } 
    } catch (Exception exception) {
      throw new ClassNotFoundException("caught an exception while obtaining a class file for " + paramString, exception);
    } 
    int i = paramString.lastIndexOf('.');
    if (i != -1) {
      String str = paramString.substring(0, i);
      if (getPackage(str) == null)
        try {
          definePackage(str, null, null, null, null, null, null, null);
        } catch (IllegalArgumentException illegalArgumentException) {} 
    } 
    if (this.domain == null)
      return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length); 
    return defineClass(paramString, arrayOfByte, 0, arrayOfByte.length, this.domain);
  }
  
  protected Class loadClassByDelegation(String paramString) throws ClassNotFoundException {
    Class clazz = null;
    if (this.doDelegation && (
      paramString.startsWith("java.") || paramString
      .startsWith("javax.") || paramString
      .startsWith("sun.") || paramString
      .startsWith("com.sun.") || paramString
      .startsWith("org.w3c.") || paramString
      .startsWith("org.xml.") || 
      notDelegated(paramString)))
      clazz = delegateToParent(paramString); 
    return clazz;
  }
  
  private boolean notDelegated(String paramString) {
    if (this.notDefinedHere.get(paramString) != null)
      return true; 
    int i = this.notDefinedPackages.size();
    for (byte b = 0; b < i; b++) {
      if (paramString.startsWith(this.notDefinedPackages.elementAt(b)))
        return true; 
    } 
    return false;
  }
  
  protected Class delegateToParent(String paramString) throws ClassNotFoundException {
    ClassLoader classLoader = getParent();
    if (classLoader != null)
      return classLoader.loadClass(paramString); 
    return findSystemClass(paramString);
  }
}
