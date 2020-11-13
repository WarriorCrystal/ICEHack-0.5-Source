package javassist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;

public class ClassPool {
  private static Method defineClass1;
  
  private static Method defineClass2;
  
  private static Method definePackage;
  
  static {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws Exception {
              Class<?> clazz = Class.forName("java.lang.ClassLoader");
              ClassPool.defineClass1 = clazz.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });
              ClassPool.defineClass2 = clazz.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class, ProtectionDomain.class });
              ClassPool.definePackage = clazz.getDeclaredMethod("definePackage", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class });
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new RuntimeException("cannot initialize ClassPool", privilegedActionException.getException());
    } 
  }
  
  public boolean childFirstLookup = false;
  
  public static boolean doPruning = false;
  
  private int compressCount;
  
  private static final int COMPRESS_THRESHOLD = 100;
  
  public static boolean releaseUnmodifiedClassFile = true;
  
  protected ClassPoolTail2 source;
  
  protected ClassPool parent;
  
  protected Hashtable classes;
  
  private Hashtable cflow = null;
  
  private static final int INIT_HASH_SIZE = 191;
  
  private ArrayList importedPackages;
  
  public ClassPool() {
    this((ClassPool)null);
  }
  
  public ClassPool(boolean paramBoolean) {
    this((ClassPool)null);
    if (paramBoolean)
      appendSystemPath(); 
  }
  
  public ClassPool(ClassPool paramClassPool) {
    this.classes = new Hashtable<Object, Object>(191);
    this.source = new ClassPoolTail2();
    this.parent = paramClassPool;
    if (paramClassPool == null) {
      CtClass[] arrayOfCtClass = CtClass.primitiveTypes;
      for (byte b = 0; b < arrayOfCtClass.length; b++)
        this.classes.put(arrayOfCtClass[b].getName(), arrayOfCtClass[b]); 
    } 
    this.cflow = null;
    this.compressCount = 0;
    clearImportedPackages();
  }
  
  public static synchronized ClassPool getDefault() {
    if (defaultPool == null) {
      defaultPool = new ClassPool(null);
      defaultPool.appendSystemPath();
    } 
    return defaultPool;
  }
  
  private static ClassPool defaultPool = null;
  
  protected CtClass getCached(String paramString) {
    return (CtClass)this.classes.get(paramString);
  }
  
  protected void cacheCtClass(String paramString, CtClass paramCtClass, boolean paramBoolean) {
    this.classes.put(paramString, paramCtClass);
  }
  
  protected CtClass removeCached(String paramString) {
    return (CtClass)this.classes.remove(paramString);
  }
  
  public String toString() {
    return this.source.toString();
  }
  
  void compress() {
    if (this.compressCount++ > 100) {
      this.compressCount = 0;
      Enumeration<CtClass> enumeration = this.classes.elements();
      while (enumeration.hasMoreElements())
        ((CtClass)enumeration.nextElement()).compress(); 
    } 
  }
  
  public void importPackage(String paramString) {
    this.importedPackages.add(paramString);
  }
  
  public void clearImportedPackages() {
    this.importedPackages = new ArrayList();
    this.importedPackages.add("java.lang");
  }
  
  public Iterator getImportedPackages() {
    return this.importedPackages.iterator();
  }
  
  public void recordInvalidClassName(String paramString) {}
  
  void recordCflow(String paramString1, String paramString2, String paramString3) {
    if (this.cflow == null)
      this.cflow = new Hashtable<Object, Object>(); 
    this.cflow.put(paramString1, new Object[] { paramString2, paramString3 });
  }
  
  public Object[] lookupCflow(String paramString) {
    if (this.cflow == null)
      this.cflow = new Hashtable<Object, Object>(); 
    return (Object[])this.cflow.get(paramString);
  }
  
  public CtClass getAndRename(String paramString1, String paramString2) throws NotFoundException {
    CtClass ctClass = get0(paramString1, false);
    if (ctClass == null)
      throw new NotFoundException(paramString1); 
    if (ctClass instanceof CtClassType1)
      ((CtClassType1)ctClass).setClassPool(this); 
    ctClass.setName(paramString2);
    return ctClass;
  }
  
  synchronized void classNameChanged(String paramString, CtClass paramCtClass) {
    CtClass ctClass = getCached(paramString);
    if (ctClass == paramCtClass)
      removeCached(paramString); 
    String str = paramCtClass.getName();
    checkNotFrozen(str);
    cacheCtClass(str, paramCtClass, false);
  }
  
  public CtClass get(String paramString) throws NotFoundException {
    CtClass ctClass;
    if (paramString == null) {
      ctClass = null;
    } else {
      ctClass = get0(paramString, true);
    } 
    if (ctClass == null)
      throw new NotFoundException(paramString); 
    ctClass.incGetCounter();
    return ctClass;
  }
  
  public CtClass getOrNull(String paramString) {
    CtClass ctClass = null;
    if (paramString == null) {
      ctClass = null;
    } else {
      try {
        ctClass = get0(paramString, true);
      } catch (NotFoundException notFoundException) {}
    } 
    if (ctClass != null)
      ctClass.incGetCounter(); 
    return ctClass;
  }
  
  public CtClass getCtClass(String paramString) throws NotFoundException {
    if (paramString.charAt(0) == '[')
      return Descriptor.toCtClass(paramString, this); 
    return get(paramString);
  }
  
  protected synchronized CtClass get0(String paramString, boolean paramBoolean) throws NotFoundException {
    CtClass ctClass = null;
    if (paramBoolean) {
      ctClass = getCached(paramString);
      if (ctClass != null)
        return ctClass; 
    } 
    if (!this.childFirstLookup && this.parent != null) {
      ctClass = this.parent.get0(paramString, paramBoolean);
      if (ctClass != null)
        return ctClass; 
    } 
    ctClass = createCtClass(paramString, paramBoolean);
    if (ctClass != null) {
      if (paramBoolean)
        cacheCtClass(ctClass.getName(), ctClass, false); 
      return ctClass;
    } 
    if (this.childFirstLookup && this.parent != null)
      ctClass = this.parent.get0(paramString, paramBoolean); 
    return ctClass;
  }
  
  protected CtClass createCtClass(String paramString, boolean paramBoolean) {
    if (paramString.charAt(0) == '[')
      paramString = Descriptor.toClassName(paramString); 
    if (paramString.endsWith("[]")) {
      String str = paramString.substring(0, paramString.indexOf('['));
      if ((!paramBoolean || getCached(str) == null) && find(str) == null)
        return null; 
      return new CtArray(paramString, this);
    } 
    if (find(paramString) == null)
      return null; 
    return new CtClassType1(paramString, this);
  }
  
  public URL find(String paramString) {
    return this.source.find(paramString);
  }
  
  void checkNotFrozen(String paramString) throws RuntimeException {
    CtClass ctClass = getCached(paramString);
    if (ctClass == null) {
      if (!this.childFirstLookup && this.parent != null) {
        try {
          ctClass = this.parent.get0(paramString, true);
        } catch (NotFoundException notFoundException) {}
        if (ctClass != null)
          throw new RuntimeException(paramString + " is in a parent ClassPool.  Use the parent."); 
      } 
    } else if (ctClass.isFrozen()) {
      throw new RuntimeException(paramString + ": frozen class (cannot edit)");
    } 
  }
  
  CtClass checkNotExists(String paramString) {
    CtClass ctClass = getCached(paramString);
    if (ctClass == null && 
      !this.childFirstLookup && this.parent != null)
      try {
        ctClass = this.parent.get0(paramString, true);
      } catch (NotFoundException notFoundException) {} 
    return ctClass;
  }
  
  InputStream openClassfile(String paramString) throws NotFoundException {
    return this.source.openClassfile(paramString);
  }
  
  void writeClassfile(String paramString, OutputStream paramOutputStream) throws NotFoundException, IOException, CannotCompileException {
    this.source.writeClassfile(paramString, paramOutputStream);
  }
  
  public CtClass[] get(String[] paramArrayOfString) throws NotFoundException {
    if (paramArrayOfString == null)
      return new CtClass[0]; 
    int i = paramArrayOfString.length;
    CtClass[] arrayOfCtClass = new CtClass[i];
    for (byte b = 0; b < i; b++)
      arrayOfCtClass[b] = get(paramArrayOfString[b]); 
    return arrayOfCtClass;
  }
  
  public CtMethod getMethod(String paramString1, String paramString2) throws NotFoundException {
    CtClass ctClass = get(paramString1);
    return ctClass.getDeclaredMethod(paramString2);
  }
  
  public CtClass makeClass(InputStream paramInputStream) throws IOException, RuntimeException {
    return makeClass(paramInputStream, true);
  }
  
  public CtClass makeClass(InputStream paramInputStream, boolean paramBoolean) throws IOException, RuntimeException {
    compress();
    paramInputStream = new BufferedInputStream(paramInputStream);
    CtClassType1 ctClassType1 = new CtClassType1(paramInputStream, this);
    ctClassType1.checkModify();
    String str = ctClassType1.getName();
    if (paramBoolean)
      checkNotFrozen(str); 
    cacheCtClass(str, ctClassType1, true);
    return ctClassType1;
  }
  
  public CtClass makeClass(ClassFile paramClassFile) throws RuntimeException {
    return makeClass(paramClassFile, true);
  }
  
  public CtClass makeClass(ClassFile paramClassFile, boolean paramBoolean) throws RuntimeException {
    compress();
    CtClassType1 ctClassType1 = new CtClassType1(paramClassFile, this);
    ctClassType1.checkModify();
    String str = ctClassType1.getName();
    if (paramBoolean)
      checkNotFrozen(str); 
    cacheCtClass(str, ctClassType1, true);
    return ctClassType1;
  }
  
  public CtClass makeClassIfNew(InputStream paramInputStream) throws IOException, RuntimeException {
    compress();
    paramInputStream = new BufferedInputStream(paramInputStream);
    CtClassType1 ctClassType1 = new CtClassType1(paramInputStream, this);
    ctClassType1.checkModify();
    String str = ctClassType1.getName();
    CtClass ctClass = checkNotExists(str);
    if (ctClass != null)
      return ctClass; 
    cacheCtClass(str, ctClassType1, true);
    return ctClassType1;
  }
  
  public CtClass makeClass(String paramString) throws RuntimeException {
    return makeClass(paramString, (CtClass)null);
  }
  
  public synchronized CtClass makeClass(String paramString, CtClass paramCtClass) throws RuntimeException {
    checkNotFrozen(paramString);
    CtNewClass ctNewClass = new CtNewClass(paramString, this, false, paramCtClass);
    cacheCtClass(paramString, ctNewClass, true);
    return ctNewClass;
  }
  
  synchronized CtClass makeNestedClass(String paramString) {
    checkNotFrozen(paramString);
    CtNewNestedClass ctNewNestedClass = new CtNewNestedClass(paramString, this, false, null);
    cacheCtClass(paramString, ctNewNestedClass, true);
    return ctNewNestedClass;
  }
  
  public CtClass makeInterface(String paramString) throws RuntimeException {
    return makeInterface(paramString, null);
  }
  
  public synchronized CtClass makeInterface(String paramString, CtClass paramCtClass) throws RuntimeException {
    checkNotFrozen(paramString);
    CtNewClass ctNewClass = new CtNewClass(paramString, this, true, paramCtClass);
    cacheCtClass(paramString, ctNewClass, true);
    return ctNewClass;
  }
  
  public CtClass makeAnnotation(String paramString) throws RuntimeException {
    try {
      CtClass ctClass = makeInterface(paramString, get("java.lang.annotation.Annotation"));
      ctClass.setModifiers(ctClass.getModifiers() | 0x2000);
      return ctClass;
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException.getMessage(), notFoundException);
    } 
  }
  
  public ClassPath appendSystemPath() {
    return this.source.appendSystemPath();
  }
  
  public ClassPath insertClassPath(ClassPath paramClassPath) {
    return this.source.insertClassPath(paramClassPath);
  }
  
  public ClassPath appendClassPath(ClassPath paramClassPath) {
    return this.source.appendClassPath(paramClassPath);
  }
  
  public ClassPath insertClassPath(String paramString) throws NotFoundException {
    return this.source.insertClassPath(paramString);
  }
  
  public ClassPath appendClassPath(String paramString) throws NotFoundException {
    return this.source.appendClassPath(paramString);
  }
  
  public void removeClassPath(ClassPath paramClassPath) {
    this.source.removeClassPath(paramClassPath);
  }
  
  public void appendPathList(String paramString) throws NotFoundException {
    char c = File.pathSeparatorChar;
    int i = 0;
    while (true) {
      int j = paramString.indexOf(c, i);
      if (j < 0) {
        appendClassPath(paramString.substring(i));
        break;
      } 
      appendClassPath(paramString.substring(i, j));
      i = j + 1;
    } 
  }
  
  public Class toClass(CtClass paramCtClass) throws CannotCompileException {
    return toClass(paramCtClass, getClassLoader());
  }
  
  public ClassLoader getClassLoader() {
    return getContextClassLoader();
  }
  
  static ClassLoader getContextClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
  
  public Class toClass(CtClass paramCtClass, ClassLoader paramClassLoader) throws CannotCompileException {
    return toClass(paramCtClass, paramClassLoader, null);
  }
  
  public Class toClass(CtClass paramCtClass, ClassLoader paramClassLoader, ProtectionDomain paramProtectionDomain) throws CannotCompileException {
    try {
      Method method;
      Object[] arrayOfObject;
      byte[] arrayOfByte = paramCtClass.toBytecode();
      if (paramProtectionDomain == null) {
        method = defineClass1;
        arrayOfObject = new Object[] { paramCtClass.getName(), arrayOfByte, new Integer(0), new Integer(arrayOfByte.length) };
      } else {
        method = defineClass2;
        arrayOfObject = new Object[] { paramCtClass.getName(), arrayOfByte, new Integer(0), new Integer(arrayOfByte.length), paramProtectionDomain };
      } 
      return (Class)toClass2(method, paramClassLoader, arrayOfObject);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (InvocationTargetException invocationTargetException) {
      throw new CannotCompileException(invocationTargetException.getTargetException());
    } catch (Exception exception) {
      throw new CannotCompileException(exception);
    } 
  }
  
  private static synchronized Object toClass2(Method paramMethod, ClassLoader paramClassLoader, Object[] paramArrayOfObject) throws Exception {
    paramMethod.setAccessible(true);
    try {
      return paramMethod.invoke(paramClassLoader, paramArrayOfObject);
    } finally {
      paramMethod.setAccessible(false);
    } 
  }
  
  public void makePackage(ClassLoader paramClassLoader, String paramString) throws CannotCompileException {
    Throwable throwable;
    Object[] arrayOfObject = { paramString, null, null, null, null, null, null, null };
    try {
      toClass2(definePackage, paramClassLoader, arrayOfObject);
      return;
    } catch (InvocationTargetException invocationTargetException) {
      throwable = invocationTargetException.getTargetException();
      if (throwable == null) {
        throwable = invocationTargetException;
      } else if (throwable instanceof IllegalArgumentException) {
        return;
      } 
    } catch (Exception exception) {
      throwable = exception;
    } 
    throw new CannotCompileException(throwable);
  }
}
