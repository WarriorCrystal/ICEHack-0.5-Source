package javassist;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Collection;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.compiler.AccessorMaker;
import javassist.expr.ExprEditor;

public abstract class CtClass {
  protected String qualifiedName;
  
  public static String debugDump = null;
  
  public static final String version = "3.21.0-GA";
  
  static final String javaLangObject = "java.lang.Object";
  
  public static CtClass booleanType;
  
  public static CtClass charType;
  
  public static CtClass byteType;
  
  public static CtClass shortType;
  
  public static CtClass intType;
  
  public static CtClass longType;
  
  public static CtClass floatType;
  
  public static CtClass doubleType;
  
  public static CtClass voidType;
  
  public static void main(String[] paramArrayOfString) {
    System.out.println("Javassist version 3.21.0-GA");
    System.out.println("Copyright (C) 1999-2016 Shigeru Chiba. All Rights Reserved.");
  }
  
  static CtClass[] primitiveTypes = new CtClass[9];
  
  static {
    booleanType = new CtPrimitiveType("boolean", 'Z', "java.lang.Boolean", "booleanValue", "()Z", 172, 4, 1);
    primitiveTypes[0] = booleanType;
    charType = new CtPrimitiveType("char", 'C', "java.lang.Character", "charValue", "()C", 172, 5, 1);
    primitiveTypes[1] = charType;
    byteType = new CtPrimitiveType("byte", 'B', "java.lang.Byte", "byteValue", "()B", 172, 8, 1);
    primitiveTypes[2] = byteType;
    shortType = new CtPrimitiveType("short", 'S', "java.lang.Short", "shortValue", "()S", 172, 9, 1);
    primitiveTypes[3] = shortType;
    intType = new CtPrimitiveType("int", 'I', "java.lang.Integer", "intValue", "()I", 172, 10, 1);
    primitiveTypes[4] = intType;
    longType = new CtPrimitiveType("long", 'J', "java.lang.Long", "longValue", "()J", 173, 11, 2);
    primitiveTypes[5] = longType;
    floatType = new CtPrimitiveType("float", 'F', "java.lang.Float", "floatValue", "()F", 174, 6, 1);
    primitiveTypes[6] = floatType;
    doubleType = new CtPrimitiveType("double", 'D', "java.lang.Double", "doubleValue", "()D", 175, 7, 2);
    primitiveTypes[7] = doubleType;
    voidType = new CtPrimitiveType("void", 'V', "java.lang.Void", null, null, 177, 0, 0);
    primitiveTypes[8] = voidType;
  }
  
  protected CtClass(String paramString) {
    this.qualifiedName = paramString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(getClass().getName());
    stringBuffer.append("@");
    stringBuffer.append(Integer.toHexString(hashCode()));
    stringBuffer.append("[");
    extendToString(stringBuffer);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  protected void extendToString(StringBuffer paramStringBuffer) {
    paramStringBuffer.append(getName());
  }
  
  public ClassPool getClassPool() {
    return null;
  }
  
  public ClassFile getClassFile() {
    checkModify();
    return getClassFile2();
  }
  
  public ClassFile getClassFile2() {
    return null;
  }
  
  public AccessorMaker getAccessorMaker() {
    return null;
  }
  
  public URL getURL() throws NotFoundException {
    throw new NotFoundException(getName());
  }
  
  public boolean isModified() {
    return false;
  }
  
  public boolean isFrozen() {
    return true;
  }
  
  public void freeze() {}
  
  void checkModify() throws RuntimeException {
    if (isFrozen())
      throw new RuntimeException(getName() + " class is frozen"); 
  }
  
  public void defrost() {
    throw new RuntimeException("cannot defrost " + getName());
  }
  
  public boolean isPrimitive() {
    return false;
  }
  
  public boolean isArray() {
    return false;
  }
  
  public CtClass getComponentType() throws NotFoundException {
    return null;
  }
  
  public boolean subtypeOf(CtClass paramCtClass) throws NotFoundException {
    return (this == paramCtClass || getName().equals(paramCtClass.getName()));
  }
  
  public String getName() {
    return this.qualifiedName;
  }
  
  public final String getSimpleName() {
    String str = this.qualifiedName;
    int i = str.lastIndexOf('.');
    if (i < 0)
      return str; 
    return str.substring(i + 1);
  }
  
  public final String getPackageName() {
    String str = this.qualifiedName;
    int i = str.lastIndexOf('.');
    if (i < 0)
      return null; 
    return str.substring(0, i);
  }
  
  public void setName(String paramString) {
    checkModify();
    if (paramString != null)
      this.qualifiedName = paramString; 
  }
  
  public String getGenericSignature() {
    return null;
  }
  
  public void setGenericSignature(String paramString) {
    checkModify();
  }
  
  public void replaceClassName(String paramString1, String paramString2) {
    checkModify();
  }
  
  public void replaceClassName(ClassMap paramClassMap) {
    checkModify();
  }
  
  public synchronized Collection getRefClasses() {
    ClassFile classFile = getClassFile2();
    if (classFile != null) {
      ClassMap classMap = new ClassMap() {
          public void put(String param1String1, String param1String2) {
            put0(param1String1, param1String2);
          }
          
          public Object get(Object param1Object) {
            String str = toJavaName((String)param1Object);
            put0(str, str);
            return null;
          }
          
          public void fix(String param1String) {}
        };
      classFile.getRefClasses(classMap);
      return classMap.values();
    } 
    return null;
  }
  
  public boolean isInterface() {
    return false;
  }
  
  public boolean isAnnotation() {
    return false;
  }
  
  public boolean isEnum() {
    return false;
  }
  
  public int getModifiers() {
    return 0;
  }
  
  public boolean hasAnnotation(Class paramClass) {
    return hasAnnotation(paramClass.getName());
  }
  
  public boolean hasAnnotation(String paramString) {
    return false;
  }
  
  public Object getAnnotation(Class paramClass) throws ClassNotFoundException {
    return null;
  }
  
  public Object[] getAnnotations() throws ClassNotFoundException {
    return new Object[0];
  }
  
  public Object[] getAvailableAnnotations() {
    return new Object[0];
  }
  
  public CtClass[] getDeclaredClasses() throws NotFoundException {
    return getNestedClasses();
  }
  
  public CtClass[] getNestedClasses() throws NotFoundException {
    return new CtClass[0];
  }
  
  public void setModifiers(int paramInt) {
    checkModify();
  }
  
  public boolean subclassOf(CtClass paramCtClass) {
    return false;
  }
  
  public CtClass getSuperclass() throws NotFoundException {
    return null;
  }
  
  public void setSuperclass(CtClass paramCtClass) throws CannotCompileException {
    checkModify();
  }
  
  public CtClass[] getInterfaces() throws NotFoundException {
    return new CtClass[0];
  }
  
  public void setInterfaces(CtClass[] paramArrayOfCtClass) {
    checkModify();
  }
  
  public void addInterface(CtClass paramCtClass) {
    checkModify();
  }
  
  public CtClass getDeclaringClass() throws NotFoundException {
    return null;
  }
  
  public final CtMethod getEnclosingMethod() throws NotFoundException {
    CtBehavior ctBehavior = getEnclosingBehavior();
    if (ctBehavior == null)
      return null; 
    if (ctBehavior instanceof CtMethod)
      return (CtMethod)ctBehavior; 
    throw new NotFoundException(ctBehavior.getLongName() + " is enclosing " + getName());
  }
  
  public CtBehavior getEnclosingBehavior() throws NotFoundException {
    return null;
  }
  
  public CtClass makeNestedClass(String paramString, boolean paramBoolean) {
    throw new RuntimeException(getName() + " is not a class");
  }
  
  public CtField[] getFields() {
    return new CtField[0];
  }
  
  public CtField getField(String paramString) throws NotFoundException {
    return getField(paramString, null);
  }
  
  public CtField getField(String paramString1, String paramString2) throws NotFoundException {
    throw new NotFoundException(paramString1);
  }
  
  CtField getField2(String paramString1, String paramString2) {
    return null;
  }
  
  public CtField[] getDeclaredFields() {
    return new CtField[0];
  }
  
  public CtField getDeclaredField(String paramString) throws NotFoundException {
    throw new NotFoundException(paramString);
  }
  
  public CtField getDeclaredField(String paramString1, String paramString2) throws NotFoundException {
    throw new NotFoundException(paramString1);
  }
  
  public CtBehavior[] getDeclaredBehaviors() {
    return new CtBehavior[0];
  }
  
  public CtConstructor[] getConstructors() {
    return new CtConstructor[0];
  }
  
  public CtConstructor getConstructor(String paramString) throws NotFoundException {
    throw new NotFoundException("no such constructor");
  }
  
  public CtConstructor[] getDeclaredConstructors() {
    return new CtConstructor[0];
  }
  
  public CtConstructor getDeclaredConstructor(CtClass[] paramArrayOfCtClass) throws NotFoundException {
    String str = Descriptor.ofConstructor(paramArrayOfCtClass);
    return getConstructor(str);
  }
  
  public CtConstructor getClassInitializer() {
    return null;
  }
  
  public CtMethod[] getMethods() {
    return new CtMethod[0];
  }
  
  public CtMethod getMethod(String paramString1, String paramString2) throws NotFoundException {
    throw new NotFoundException(paramString1);
  }
  
  public CtMethod[] getDeclaredMethods() {
    return new CtMethod[0];
  }
  
  public CtMethod getDeclaredMethod(String paramString, CtClass[] paramArrayOfCtClass) throws NotFoundException {
    throw new NotFoundException(paramString);
  }
  
  public CtMethod[] getDeclaredMethods(String paramString) throws NotFoundException {
    throw new NotFoundException(paramString);
  }
  
  public CtMethod getDeclaredMethod(String paramString) throws NotFoundException {
    throw new NotFoundException(paramString);
  }
  
  public CtConstructor makeClassInitializer() throws CannotCompileException {
    throw new CannotCompileException("not a class");
  }
  
  public void addConstructor(CtConstructor paramCtConstructor) throws CannotCompileException {
    checkModify();
  }
  
  public void removeConstructor(CtConstructor paramCtConstructor) throws NotFoundException {
    checkModify();
  }
  
  public void addMethod(CtMethod paramCtMethod) throws CannotCompileException {
    checkModify();
  }
  
  public void removeMethod(CtMethod paramCtMethod) throws NotFoundException {
    checkModify();
  }
  
  public void addField(CtField paramCtField) throws CannotCompileException {
    addField(paramCtField, (CtField.Initializer)null);
  }
  
  public void addField(CtField paramCtField, String paramString) throws CannotCompileException {
    checkModify();
  }
  
  public void addField(CtField paramCtField, CtField.Initializer paramInitializer) throws CannotCompileException {
    checkModify();
  }
  
  public void removeField(CtField paramCtField) throws NotFoundException {
    checkModify();
  }
  
  public byte[] getAttribute(String paramString) {
    return null;
  }
  
  public void setAttribute(String paramString, byte[] paramArrayOfbyte) {
    checkModify();
  }
  
  public void instrument(CodeConverter paramCodeConverter) throws CannotCompileException {
    checkModify();
  }
  
  public void instrument(ExprEditor paramExprEditor) throws CannotCompileException {
    checkModify();
  }
  
  public Class toClass() throws CannotCompileException {
    return getClassPool().toClass(this);
  }
  
  public Class toClass(ClassLoader paramClassLoader, ProtectionDomain paramProtectionDomain) throws CannotCompileException {
    ClassPool classPool = getClassPool();
    if (paramClassLoader == null)
      paramClassLoader = classPool.getClassLoader(); 
    return classPool.toClass(this, paramClassLoader, paramProtectionDomain);
  }
  
  public final Class toClass(ClassLoader paramClassLoader) throws CannotCompileException {
    return getClassPool().toClass(this, paramClassLoader);
  }
  
  public void detach() {
    ClassPool classPool = getClassPool();
    CtClass ctClass = classPool.removeCached(getName());
    if (ctClass != this)
      classPool.cacheCtClass(getName(), ctClass, false); 
  }
  
  public boolean stopPruning(boolean paramBoolean) {
    return true;
  }
  
  public void prune() {}
  
  void incGetCounter() {}
  
  public void rebuildClassFile() {}
  
  public byte[] toBytecode() throws IOException, CannotCompileException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      toBytecode(dataOutputStream);
    } finally {
      dataOutputStream.close();
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public void writeFile() throws NotFoundException, IOException, CannotCompileException {
    writeFile(".");
  }
  
  public void writeFile(String paramString) throws CannotCompileException, IOException {
    DataOutputStream dataOutputStream = makeFileOutput(paramString);
    try {
      toBytecode(dataOutputStream);
    } finally {
      dataOutputStream.close();
    } 
  }
  
  protected DataOutputStream makeFileOutput(String paramString) {
    String str1 = getName();
    String str2 = paramString + File.separatorChar + str1.replace('.', File.separatorChar) + ".class";
    int i = str2.lastIndexOf(File.separatorChar);
    if (i > 0) {
      String str = str2.substring(0, i);
      if (!str.equals("."))
        (new File(str)).mkdirs(); 
    } 
    return new DataOutputStream(new BufferedOutputStream(new DelayedFileOutputStream(str2)));
  }
  
  public void debugWriteFile() {
    debugWriteFile(".");
  }
  
  public void debugWriteFile(String paramString) {
    try {
      boolean bool = stopPruning(true);
      writeFile(paramString);
      defrost();
      stopPruning(bool);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  static class DelayedFileOutputStream extends OutputStream {
    private FileOutputStream file;
    
    private String filename;
    
    DelayedFileOutputStream(String param1String) {
      this.file = null;
      this.filename = param1String;
    }
    
    private void init() throws IOException {
      if (this.file == null)
        this.file = new FileOutputStream(this.filename); 
    }
    
    public void write(int param1Int) throws IOException {
      init();
      this.file.write(param1Int);
    }
    
    public void write(byte[] param1ArrayOfbyte) throws IOException {
      init();
      this.file.write(param1ArrayOfbyte);
    }
    
    public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      init();
      this.file.write(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    public void flush() throws IOException {
      init();
      this.file.flush();
    }
    
    public void close() throws IOException {
      init();
      this.file.close();
    }
  }
  
  public void toBytecode(DataOutputStream paramDataOutputStream) throws CannotCompileException, IOException {
    throw new CannotCompileException("not a class");
  }
  
  public String makeUniqueName(String paramString) {
    throw new RuntimeException("not available in " + getName());
  }
  
  void compress() {}
}
