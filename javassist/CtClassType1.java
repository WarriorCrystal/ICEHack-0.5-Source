package javassist;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.ConstantAttribute;
import javassist.bytecode.Descriptor;
import javassist.bytecode.EnclosingMethodAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationImpl;
import javassist.compiler.AccessorMaker;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.expr.ExprEditor;

class CtClassType1 extends CtClass {
  ClassPool classPool;
  
  boolean wasChanged;
  
  private boolean wasFrozen;
  
  boolean wasPruned;
  
  boolean gcConstPool;
  
  ClassFile classfile;
  
  byte[] rawClassfile;
  
  private WeakReference memberCache;
  
  private AccessorMaker accessors;
  
  private CtClassType fieldInitializers;
  
  private Hashtable hiddenMethods;
  
  private int uniqueNumberSeed;
  
  private boolean doPruning = ClassPool.doPruning;
  
  private int getCount;
  
  private static final int GET_THRESHOLD = 2;
  
  CtClassType1(String paramString, ClassPool paramClassPool) {
    super(paramString);
    this.classPool = paramClassPool;
    this.wasChanged = this.wasFrozen = this.wasPruned = this.gcConstPool = false;
    this.classfile = null;
    this.rawClassfile = null;
    this.memberCache = null;
    this.accessors = null;
    this.fieldInitializers = null;
    this.hiddenMethods = null;
    this.uniqueNumberSeed = 0;
    this.getCount = 0;
  }
  
  CtClassType1(InputStream paramInputStream, ClassPool paramClassPool) throws IOException {
    this((String)null, paramClassPool);
    this.classfile = new ClassFile(new DataInputStream(paramInputStream));
    this.qualifiedName = this.classfile.getName();
  }
  
  CtClassType1(ClassFile paramClassFile, ClassPool paramClassPool) {
    this((String)null, paramClassPool);
    this.classfile = paramClassFile;
    this.qualifiedName = this.classfile.getName();
  }
  
  protected void extendToString(StringBuffer paramStringBuffer) {
    if (this.wasChanged)
      paramStringBuffer.append("changed "); 
    if (this.wasFrozen)
      paramStringBuffer.append("frozen "); 
    if (this.wasPruned)
      paramStringBuffer.append("pruned "); 
    paramStringBuffer.append(Modifier.toString(getModifiers()));
    paramStringBuffer.append(" class ");
    paramStringBuffer.append(getName());
    try {
      CtClass ctClass = getSuperclass();
      if (ctClass != null) {
        String str = ctClass.getName();
        if (!str.equals("java.lang.Object"))
          paramStringBuffer.append(" extends " + ctClass.getName()); 
      } 
    } catch (NotFoundException notFoundException) {
      paramStringBuffer.append(" extends ??");
    } 
    try {
      CtClass[] arrayOfCtClass = getInterfaces();
      if (arrayOfCtClass.length > 0)
        paramStringBuffer.append(" implements "); 
      for (byte b = 0; b < arrayOfCtClass.length; b++) {
        paramStringBuffer.append(arrayOfCtClass[b].getName());
        paramStringBuffer.append(", ");
      } 
    } catch (NotFoundException notFoundException) {
      paramStringBuffer.append(" extends ??");
    } 
    CtMember.Cache cache = getMembers();
    exToString(paramStringBuffer, " fields=", cache
        .fieldHead(), cache.lastField());
    exToString(paramStringBuffer, " constructors=", cache
        .consHead(), cache.lastCons());
    exToString(paramStringBuffer, " methods=", cache
        .methodHead(), cache.lastMethod());
  }
  
  private void exToString(StringBuffer paramStringBuffer, String paramString, CtMember paramCtMember1, CtMember paramCtMember2) {
    paramStringBuffer.append(paramString);
    while (paramCtMember1 != paramCtMember2) {
      paramCtMember1 = paramCtMember1.next();
      paramStringBuffer.append(paramCtMember1);
      paramStringBuffer.append(", ");
    } 
  }
  
  public AccessorMaker getAccessorMaker() {
    if (this.accessors == null)
      this.accessors = new AccessorMaker(this); 
    return this.accessors;
  }
  
  public ClassFile getClassFile2() {
    return getClassFile3(true);
  }
  
  public ClassFile getClassFile3(boolean paramBoolean) {
    ClassFile classFile = this.classfile;
    if (classFile != null)
      return classFile; 
    if (paramBoolean)
      this.classPool.compress(); 
    if (this.rawClassfile != null)
      try {
        ClassFile classFile1 = new ClassFile(new DataInputStream(new ByteArrayInputStream(this.rawClassfile)));
        this.rawClassfile = null;
        this.getCount = 2;
        return setClassFile(classFile1);
      } catch (IOException iOException) {
        throw new RuntimeException(iOException.toString(), iOException);
      }  
    InputStream inputStream = null;
    try {
      inputStream = this.classPool.openClassfile(getName());
      if (inputStream == null)
        throw new NotFoundException(getName()); 
      inputStream = new BufferedInputStream(inputStream);
      ClassFile classFile1 = new ClassFile(new DataInputStream(inputStream));
      if (!classFile1.getName().equals(this.qualifiedName))
        throw new RuntimeException("cannot find " + this.qualifiedName + ": " + classFile1
            .getName() + " found in " + this.qualifiedName
            .replace('.', '/') + ".class"); 
      return setClassFile(classFile1);
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException.toString(), notFoundException);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.toString(), iOException);
    } finally {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (IOException iOException) {} 
    } 
  }
  
  final void incGetCounter() {
    this.getCount++;
  }
  
  void compress() {
    if (this.getCount < 2)
      if (!isModified() && ClassPool.releaseUnmodifiedClassFile) {
        removeClassFile();
      } else if (isFrozen() && !this.wasPruned) {
        saveClassFile();
      }  
    this.getCount = 0;
  }
  
  private synchronized void saveClassFile() {
    if (this.classfile == null || hasMemberCache() != null)
      return; 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      this.classfile.write(dataOutputStream);
      byteArrayOutputStream.close();
      this.rawClassfile = byteArrayOutputStream.toByteArray();
      this.classfile = null;
    } catch (IOException iOException) {}
  }
  
  private synchronized void removeClassFile() {
    if (this.classfile != null && !isModified() && hasMemberCache() == null)
      this.classfile = null; 
  }
  
  private synchronized ClassFile setClassFile(ClassFile paramClassFile) {
    if (this.classfile == null)
      this.classfile = paramClassFile; 
    return this.classfile;
  }
  
  public ClassPool getClassPool() {
    return this.classPool;
  }
  
  void setClassPool(ClassPool paramClassPool) {
    this.classPool = paramClassPool;
  }
  
  public URL getURL() throws NotFoundException {
    URL uRL = this.classPool.find(getName());
    if (uRL == null)
      throw new NotFoundException(getName()); 
    return uRL;
  }
  
  public boolean isModified() {
    return this.wasChanged;
  }
  
  public boolean isFrozen() {
    return this.wasFrozen;
  }
  
  public void freeze() {
    this.wasFrozen = true;
  }
  
  void checkModify() throws RuntimeException {
    if (isFrozen()) {
      String str = getName() + " class is frozen";
      if (this.wasPruned)
        str = str + " and pruned"; 
      throw new RuntimeException(str);
    } 
    this.wasChanged = true;
  }
  
  public void defrost() {
    checkPruned("defrost");
    this.wasFrozen = false;
  }
  
  public boolean subtypeOf(CtClass paramCtClass) throws NotFoundException {
    String str1 = paramCtClass.getName();
    if (this == paramCtClass || getName().equals(str1))
      return true; 
    ClassFile classFile = getClassFile2();
    String str2 = classFile.getSuperclass();
    if (str2 != null && str2.equals(str1))
      return true; 
    String[] arrayOfString = classFile.getInterfaces();
    int i = arrayOfString.length;
    byte b;
    for (b = 0; b < i; b++) {
      if (arrayOfString[b].equals(str1))
        return true; 
    } 
    if (str2 != null && this.classPool.get(str2).subtypeOf(paramCtClass))
      return true; 
    for (b = 0; b < i; b++) {
      if (this.classPool.get(arrayOfString[b]).subtypeOf(paramCtClass))
        return true; 
    } 
    return false;
  }
  
  public void setName(String paramString) throws RuntimeException {
    String str = getName();
    if (paramString.equals(str))
      return; 
    this.classPool.checkNotFrozen(paramString);
    ClassFile classFile = getClassFile2();
    super.setName(paramString);
    classFile.setName(paramString);
    nameReplaced();
    this.classPool.classNameChanged(str, this);
  }
  
  public String getGenericSignature() {
    SignatureAttribute signatureAttribute = (SignatureAttribute)getClassFile2().getAttribute("Signature");
    return (signatureAttribute == null) ? null : signatureAttribute.getSignature();
  }
  
  public void setGenericSignature(String paramString) {
    ClassFile classFile = getClassFile();
    SignatureAttribute signatureAttribute = new SignatureAttribute(classFile.getConstPool(), paramString);
    classFile.addAttribute((AttributeInfo)signatureAttribute);
  }
  
  public void replaceClassName(ClassMap paramClassMap) throws RuntimeException {
    String str1 = getName();
    String str2 = (String)paramClassMap.get(Descriptor.toJvmName(str1));
    if (str2 != null) {
      str2 = Descriptor.toJavaName(str2);
      this.classPool.checkNotFrozen(str2);
    } 
    super.replaceClassName(paramClassMap);
    ClassFile classFile = getClassFile2();
    classFile.renameClass(paramClassMap);
    nameReplaced();
    if (str2 != null) {
      super.setName(str2);
      this.classPool.classNameChanged(str1, this);
    } 
  }
  
  public void replaceClassName(String paramString1, String paramString2) throws RuntimeException {
    String str = getName();
    if (str.equals(paramString1)) {
      setName(paramString2);
    } else {
      super.replaceClassName(paramString1, paramString2);
      getClassFile2().renameClass(paramString1, paramString2);
      nameReplaced();
    } 
  }
  
  public boolean isInterface() {
    return Modifier.isInterface(getModifiers());
  }
  
  public boolean isAnnotation() {
    return Modifier.isAnnotation(getModifiers());
  }
  
  public boolean isEnum() {
    return Modifier.isEnum(getModifiers());
  }
  
  public int getModifiers() {
    ClassFile classFile = getClassFile2();
    int i = classFile.getAccessFlags();
    i = AccessFlag.clear(i, 32);
    int j = classFile.getInnerAccessFlags();
    if (j != -1 && (j & 0x8) != 0)
      i |= 0x8; 
    return AccessFlag.toModifier(i);
  }
  
  public CtClass[] getNestedClasses() throws NotFoundException {
    ClassFile classFile = getClassFile2();
    InnerClassesAttribute innerClassesAttribute = (InnerClassesAttribute)classFile.getAttribute("InnerClasses");
    if (innerClassesAttribute == null)
      return new CtClass[0]; 
    String str = classFile.getName() + "$";
    int i = innerClassesAttribute.tableLength();
    ArrayList<CtClass> arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      String str1 = innerClassesAttribute.innerClass(b);
      if (str1 != null && 
        str1.startsWith(str))
        if (str1.lastIndexOf('$') < str.length())
          arrayList.add(this.classPool.get(str1));  
    } 
    return arrayList.<CtClass>toArray(new CtClass[arrayList.size()]);
  }
  
  public void setModifiers(int paramInt) {
    ClassFile classFile = getClassFile2();
    if (Modifier.isStatic(paramInt)) {
      int i = classFile.getInnerAccessFlags();
      if (i != -1 && (i & 0x8) != 0) {
        paramInt &= 0xFFFFFFF7;
      } else {
        throw new RuntimeException("cannot change " + getName() + " into a static class");
      } 
    } 
    checkModify();
    classFile.setAccessFlags(AccessFlag.of(paramInt));
  }
  
  public boolean hasAnnotation(String paramString) {
    ClassFile classFile = getClassFile2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)classFile.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)classFile.getAttribute("RuntimeVisibleAnnotations");
    return hasAnnotationType(paramString, getClassPool(), annotationsAttribute1, annotationsAttribute2);
  }
  
  static boolean hasAnnotationType(Class paramClass, ClassPool paramClassPool, AnnotationsAttribute paramAnnotationsAttribute1, AnnotationsAttribute paramAnnotationsAttribute2) {
    return hasAnnotationType(paramClass.getName(), paramClassPool, paramAnnotationsAttribute1, paramAnnotationsAttribute2);
  }
  
  static boolean hasAnnotationType(String paramString, ClassPool paramClassPool, AnnotationsAttribute paramAnnotationsAttribute1, AnnotationsAttribute paramAnnotationsAttribute2) {
    Annotation[] arrayOfAnnotation1;
    Annotation[] arrayOfAnnotation2;
    if (paramAnnotationsAttribute1 == null) {
      arrayOfAnnotation1 = null;
    } else {
      arrayOfAnnotation1 = paramAnnotationsAttribute1.getAnnotations();
    } 
    if (paramAnnotationsAttribute2 == null) {
      arrayOfAnnotation2 = null;
    } else {
      arrayOfAnnotation2 = paramAnnotationsAttribute2.getAnnotations();
    } 
    if (arrayOfAnnotation1 != null)
      for (byte b = 0; b < arrayOfAnnotation1.length; b++) {
        if (arrayOfAnnotation1[b].getTypeName().equals(paramString))
          return true; 
      }  
    if (arrayOfAnnotation2 != null)
      for (byte b = 0; b < arrayOfAnnotation2.length; b++) {
        if (arrayOfAnnotation2[b].getTypeName().equals(paramString))
          return true; 
      }  
    return false;
  }
  
  public Object getAnnotation(Class paramClass) throws ClassNotFoundException {
    ClassFile classFile = getClassFile2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)classFile.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)classFile.getAttribute("RuntimeVisibleAnnotations");
    return getAnnotationType(paramClass, getClassPool(), annotationsAttribute1, annotationsAttribute2);
  }
  
  static Object getAnnotationType(Class paramClass, ClassPool paramClassPool, AnnotationsAttribute paramAnnotationsAttribute1, AnnotationsAttribute paramAnnotationsAttribute2) throws ClassNotFoundException {
    Annotation[] arrayOfAnnotation1, arrayOfAnnotation2;
    if (paramAnnotationsAttribute1 == null) {
      arrayOfAnnotation1 = null;
    } else {
      arrayOfAnnotation1 = paramAnnotationsAttribute1.getAnnotations();
    } 
    if (paramAnnotationsAttribute2 == null) {
      arrayOfAnnotation2 = null;
    } else {
      arrayOfAnnotation2 = paramAnnotationsAttribute2.getAnnotations();
    } 
    String str = paramClass.getName();
    if (arrayOfAnnotation1 != null)
      for (byte b = 0; b < arrayOfAnnotation1.length; b++) {
        if (arrayOfAnnotation1[b].getTypeName().equals(str))
          return toAnnoType(arrayOfAnnotation1[b], paramClassPool); 
      }  
    if (arrayOfAnnotation2 != null)
      for (byte b = 0; b < arrayOfAnnotation2.length; b++) {
        if (arrayOfAnnotation2[b].getTypeName().equals(str))
          return toAnnoType(arrayOfAnnotation2[b], paramClassPool); 
      }  
    return null;
  }
  
  public Object[] getAnnotations() throws ClassNotFoundException {
    return getAnnotations(false);
  }
  
  public Object[] getAvailableAnnotations() {
    try {
      return getAnnotations(true);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException("Unexpected exception ", classNotFoundException);
    } 
  }
  
  private Object[] getAnnotations(boolean paramBoolean) throws ClassNotFoundException {
    ClassFile classFile = getClassFile2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)classFile.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)classFile.getAttribute("RuntimeVisibleAnnotations");
    return toAnnotationType(paramBoolean, getClassPool(), annotationsAttribute1, annotationsAttribute2);
  }
  
  static Object[] toAnnotationType(boolean paramBoolean, ClassPool paramClassPool, AnnotationsAttribute paramAnnotationsAttribute1, AnnotationsAttribute paramAnnotationsAttribute2) throws ClassNotFoundException {
    Annotation[] arrayOfAnnotation1, arrayOfAnnotation2;
    int i, j;
    if (paramAnnotationsAttribute1 == null) {
      arrayOfAnnotation1 = null;
      i = 0;
    } else {
      arrayOfAnnotation1 = paramAnnotationsAttribute1.getAnnotations();
      i = arrayOfAnnotation1.length;
    } 
    if (paramAnnotationsAttribute2 == null) {
      arrayOfAnnotation2 = null;
      j = 0;
    } else {
      arrayOfAnnotation2 = paramAnnotationsAttribute2.getAnnotations();
      j = arrayOfAnnotation2.length;
    } 
    if (!paramBoolean) {
      Object[] arrayOfObject = new Object[i + j];
      byte b1;
      for (b1 = 0; b1 < i; b1++)
        arrayOfObject[b1] = toAnnoType(arrayOfAnnotation1[b1], paramClassPool); 
      for (b1 = 0; b1 < j; b1++)
        arrayOfObject[b1 + i] = toAnnoType(arrayOfAnnotation2[b1], paramClassPool); 
      return arrayOfObject;
    } 
    ArrayList<Object> arrayList = new ArrayList();
    byte b;
    for (b = 0; b < i; b++) {
      try {
        arrayList.add(toAnnoType(arrayOfAnnotation1[b], paramClassPool));
      } catch (ClassNotFoundException classNotFoundException) {}
    } 
    for (b = 0; b < j; b++) {
      try {
        arrayList.add(toAnnoType(arrayOfAnnotation2[b], paramClassPool));
      } catch (ClassNotFoundException classNotFoundException) {}
    } 
    return arrayList.toArray();
  }
  
  static Object[][] toAnnotationType(boolean paramBoolean, ClassPool paramClassPool, ParameterAnnotationsAttribute paramParameterAnnotationsAttribute1, ParameterAnnotationsAttribute paramParameterAnnotationsAttribute2, MethodInfo paramMethodInfo) throws ClassNotFoundException {
    int i = 0;
    if (paramParameterAnnotationsAttribute1 != null) {
      i = paramParameterAnnotationsAttribute1.numParameters();
    } else if (paramParameterAnnotationsAttribute2 != null) {
      i = paramParameterAnnotationsAttribute2.numParameters();
    } else {
      i = Descriptor.numOfParameters(paramMethodInfo.getDescriptor());
    } 
    Object[][] arrayOfObject = new Object[i][];
    for (byte b = 0; b < i; b++) {
      Annotation[] arrayOfAnnotation1;
      Annotation[] arrayOfAnnotation2;
      int j;
      int k;
      if (paramParameterAnnotationsAttribute1 == null) {
        arrayOfAnnotation1 = null;
        j = 0;
      } else {
        arrayOfAnnotation1 = paramParameterAnnotationsAttribute1.getAnnotations()[b];
        j = arrayOfAnnotation1.length;
      } 
      if (paramParameterAnnotationsAttribute2 == null) {
        arrayOfAnnotation2 = null;
        k = 0;
      } else {
        arrayOfAnnotation2 = paramParameterAnnotationsAttribute2.getAnnotations()[b];
        k = arrayOfAnnotation2.length;
      } 
      if (!paramBoolean) {
        arrayOfObject[b] = new Object[j + k];
        byte b1;
        for (b1 = 0; b1 < j; b1++)
          arrayOfObject[b][b1] = toAnnoType(arrayOfAnnotation1[b1], paramClassPool); 
        for (b1 = 0; b1 < k; b1++)
          arrayOfObject[b][b1 + j] = toAnnoType(arrayOfAnnotation2[b1], paramClassPool); 
      } else {
        ArrayList<Object> arrayList = new ArrayList();
        byte b1;
        for (b1 = 0; b1 < j; b1++) {
          try {
            arrayList.add(toAnnoType(arrayOfAnnotation1[b1], paramClassPool));
          } catch (ClassNotFoundException classNotFoundException) {}
        } 
        for (b1 = 0; b1 < k; b1++) {
          try {
            arrayList.add(toAnnoType(arrayOfAnnotation2[b1], paramClassPool));
          } catch (ClassNotFoundException classNotFoundException) {}
        } 
        arrayOfObject[b] = arrayList.toArray();
      } 
    } 
    return arrayOfObject;
  }
  
  private static Object toAnnoType(Annotation paramAnnotation, ClassPool paramClassPool) throws ClassNotFoundException {
    try {
      ClassLoader classLoader = paramClassPool.getClassLoader();
      return paramAnnotation.toAnnotationType(classLoader, paramClassPool);
    } catch (ClassNotFoundException classNotFoundException) {
      ClassLoader classLoader = paramClassPool.getClass().getClassLoader();
      try {
        return paramAnnotation.toAnnotationType(classLoader, paramClassPool);
      } catch (ClassNotFoundException classNotFoundException1) {
        try {
          Class clazz = paramClassPool.get(paramAnnotation.getTypeName()).toClass();
          return AnnotationImpl.make(clazz
              .getClassLoader(), clazz, paramClassPool, paramAnnotation);
        } catch (Throwable throwable) {
          throw new ClassNotFoundException(paramAnnotation.getTypeName());
        } 
      } 
    } 
  }
  
  public boolean subclassOf(CtClass paramCtClass) {
    if (paramCtClass == null)
      return false; 
    String str = paramCtClass.getName();
    CtClassType1 ctClassType1 = this;
    try {
      while (ctClassType1 != null) {
        if (ctClassType1.getName().equals(str))
          return true; 
        CtClass ctClass = ctClassType1.getSuperclass();
      } 
    } catch (Exception exception) {}
    return false;
  }
  
  public CtClass getSuperclass() throws NotFoundException {
    String str = getClassFile2().getSuperclass();
    if (str == null)
      return null; 
    return this.classPool.get(str);
  }
  
  public void setSuperclass(CtClass paramCtClass) throws CannotCompileException {
    checkModify();
    if (isInterface()) {
      addInterface(paramCtClass);
    } else {
      getClassFile2().setSuperclass(paramCtClass.getName());
    } 
  }
  
  public CtClass[] getInterfaces() throws NotFoundException {
    String[] arrayOfString = getClassFile2().getInterfaces();
    int i = arrayOfString.length;
    CtClass[] arrayOfCtClass = new CtClass[i];
    for (byte b = 0; b < i; b++)
      arrayOfCtClass[b] = this.classPool.get(arrayOfString[b]); 
    return arrayOfCtClass;
  }
  
  public void setInterfaces(CtClass[] paramArrayOfCtClass) {
    String[] arrayOfString;
    checkModify();
    if (paramArrayOfCtClass == null) {
      arrayOfString = new String[0];
    } else {
      int i = paramArrayOfCtClass.length;
      arrayOfString = new String[i];
      for (byte b = 0; b < i; b++)
        arrayOfString[b] = paramArrayOfCtClass[b].getName(); 
    } 
    getClassFile2().setInterfaces(arrayOfString);
  }
  
  public void addInterface(CtClass paramCtClass) {
    checkModify();
    if (paramCtClass != null)
      getClassFile2().addInterface(paramCtClass.getName()); 
  }
  
  public CtClass getDeclaringClass() throws NotFoundException {
    ClassFile classFile = getClassFile2();
    InnerClassesAttribute innerClassesAttribute = (InnerClassesAttribute)classFile.getAttribute("InnerClasses");
    if (innerClassesAttribute == null)
      return null; 
    String str = getName();
    int i = innerClassesAttribute.tableLength();
    for (byte b = 0; b < i; b++) {
      if (str.equals(innerClassesAttribute.innerClass(b))) {
        String str1 = innerClassesAttribute.outerClass(b);
        if (str1 != null)
          return this.classPool.get(str1); 
        EnclosingMethodAttribute enclosingMethodAttribute = (EnclosingMethodAttribute)classFile.getAttribute("EnclosingMethod");
        if (enclosingMethodAttribute != null)
          return this.classPool.get(enclosingMethodAttribute.className()); 
      } 
    } 
    return null;
  }
  
  public CtBehavior getEnclosingBehavior() throws NotFoundException {
    ClassFile classFile = getClassFile2();
    EnclosingMethodAttribute enclosingMethodAttribute = (EnclosingMethodAttribute)classFile.getAttribute("EnclosingMethod");
    if (enclosingMethodAttribute == null)
      return null; 
    CtClass ctClass = this.classPool.get(enclosingMethodAttribute.className());
    String str = enclosingMethodAttribute.methodName();
    if ("<init>".equals(str))
      return ctClass.getConstructor(enclosingMethodAttribute.methodDescriptor()); 
    if ("<clinit>".equals(str))
      return ctClass.getClassInitializer(); 
    return ctClass.getMethod(str, enclosingMethodAttribute.methodDescriptor());
  }
  
  public CtClass makeNestedClass(String paramString, boolean paramBoolean) {
    if (!paramBoolean)
      throw new RuntimeException("sorry, only nested static class is supported"); 
    checkModify();
    CtClass ctClass = this.classPool.makeNestedClass(getName() + "$" + paramString);
    ClassFile classFile1 = getClassFile2();
    ClassFile classFile2 = ctClass.getClassFile2();
    InnerClassesAttribute innerClassesAttribute = (InnerClassesAttribute)classFile1.getAttribute("InnerClasses");
    if (innerClassesAttribute == null) {
      innerClassesAttribute = new InnerClassesAttribute(classFile1.getConstPool());
      classFile1.addAttribute((AttributeInfo)innerClassesAttribute);
    } 
    innerClassesAttribute.append(ctClass.getName(), getName(), paramString, classFile2
        .getAccessFlags() & 0xFFFFFFDF | 0x8);
    classFile2.addAttribute(innerClassesAttribute.copy(classFile2.getConstPool(), null));
    return ctClass;
  }
  
  private void nameReplaced() {
    CtMember.Cache cache = hasMemberCache();
    if (cache != null) {
      CtMember ctMember1 = cache.methodHead();
      CtMember ctMember2 = cache.lastMethod();
      while (ctMember1 != ctMember2) {
        ctMember1 = ctMember1.next();
        ctMember1.nameReplaced();
      } 
    } 
  }
  
  protected CtMember.Cache hasMemberCache() {
    WeakReference<CtMember.Cache> weakReference = this.memberCache;
    if (weakReference != null)
      return weakReference.get(); 
    return null;
  }
  
  protected synchronized CtMember.Cache getMembers() {
    CtMember.Cache cache = null;
    if (this.memberCache == null || (
      cache = this.memberCache.get()) == null) {
      cache = new CtMember.Cache(this);
      makeFieldCache(cache);
      makeBehaviorCache(cache);
      this.memberCache = new WeakReference<CtMember.Cache>(cache);
    } 
    return cache;
  }
  
  private void makeFieldCache(CtMember.Cache paramCache) {
    List<FieldInfo> list = getClassFile3(false).getFields();
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      FieldInfo fieldInfo = list.get(b);
      CtField ctField = new CtField(fieldInfo, this);
      paramCache.addField(ctField);
    } 
  }
  
  private void makeBehaviorCache(CtMember.Cache paramCache) {
    List<MethodInfo> list = getClassFile3(false).getMethods();
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      MethodInfo methodInfo = list.get(b);
      if (methodInfo.isMethod()) {
        CtMethod ctMethod = new CtMethod(methodInfo, this);
        paramCache.addMethod(ctMethod);
      } else {
        CtConstructor ctConstructor = new CtConstructor(methodInfo, this);
        paramCache.addConstructor(ctConstructor);
      } 
    } 
  }
  
  public CtField[] getFields() {
    ArrayList arrayList = new ArrayList();
    getFields(arrayList, this);
    return (CtField[])arrayList.toArray((Object[])new CtField[arrayList.size()]);
  }
  
  private static void getFields(ArrayList<CtMember> paramArrayList, CtClass paramCtClass) {
    if (paramCtClass == null)
      return; 
    try {
      getFields(paramArrayList, paramCtClass.getSuperclass());
    } catch (NotFoundException notFoundException) {}
    try {
      CtClass[] arrayOfCtClass = paramCtClass.getInterfaces();
      int i = arrayOfCtClass.length;
      for (byte b = 0; b < i; b++)
        getFields(paramArrayList, arrayOfCtClass[b]); 
    } catch (NotFoundException notFoundException) {}
    CtMember.Cache cache = ((CtClassType1)paramCtClass).getMembers();
    CtMember ctMember1 = cache.fieldHead();
    CtMember ctMember2 = cache.lastField();
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      if (!Modifier.isPrivate(ctMember1.getModifiers()))
        paramArrayList.add(ctMember1); 
    } 
  }
  
  public CtField getField(String paramString1, String paramString2) throws NotFoundException {
    CtField ctField = getField2(paramString1, paramString2);
    return checkGetField(ctField, paramString1, paramString2);
  }
  
  private CtField checkGetField(CtField paramCtField, String paramString1, String paramString2) throws NotFoundException {
    if (paramCtField == null) {
      String str = "field: " + paramString1;
      if (paramString2 != null)
        str = str + " type " + paramString2; 
      throw new NotFoundException(str + " in " + getName());
    } 
    return paramCtField;
  }
  
  CtField getField2(String paramString1, String paramString2) {
    CtField ctField = getDeclaredField2(paramString1, paramString2);
    if (ctField != null)
      return ctField; 
    try {
      CtClass[] arrayOfCtClass = getInterfaces();
      int i = arrayOfCtClass.length;
      for (byte b = 0; b < i; b++) {
        CtField ctField1 = arrayOfCtClass[b].getField2(paramString1, paramString2);
        if (ctField1 != null)
          return ctField1; 
      } 
      CtClass ctClass = getSuperclass();
      if (ctClass != null)
        return ctClass.getField2(paramString1, paramString2); 
    } catch (NotFoundException notFoundException) {}
    return null;
  }
  
  public CtField[] getDeclaredFields() {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.fieldHead();
    CtMember ctMember2 = cache.lastField();
    int i = CtMember.Cache.count(ctMember1, ctMember2);
    CtField[] arrayOfCtField = new CtField[i];
    byte b = 0;
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      arrayOfCtField[b++] = (CtField)ctMember1;
    } 
    return arrayOfCtField;
  }
  
  public CtField getDeclaredField(String paramString) throws NotFoundException {
    return getDeclaredField(paramString, (String)null);
  }
  
  public CtField getDeclaredField(String paramString1, String paramString2) throws NotFoundException {
    CtField ctField = getDeclaredField2(paramString1, paramString2);
    return checkGetField(ctField, paramString1, paramString2);
  }
  
  private CtField getDeclaredField2(String paramString1, String paramString2) {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.fieldHead();
    CtMember ctMember2 = cache.lastField();
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      if (ctMember1.getName().equals(paramString1) && (paramString2 == null || paramString2
        .equals(ctMember1.getSignature())))
        return (CtField)ctMember1; 
    } 
    return null;
  }
  
  public CtBehavior[] getDeclaredBehaviors() {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.consHead();
    CtMember ctMember2 = cache.lastCons();
    int i = CtMember.Cache.count(ctMember1, ctMember2);
    CtMember ctMember3 = cache.methodHead();
    CtMember ctMember4 = cache.lastMethod();
    int j = CtMember.Cache.count(ctMember3, ctMember4);
    CtBehavior[] arrayOfCtBehavior = new CtBehavior[i + j];
    byte b = 0;
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      arrayOfCtBehavior[b++] = (CtBehavior)ctMember1;
    } 
    while (ctMember3 != ctMember4) {
      ctMember3 = ctMember3.next();
      arrayOfCtBehavior[b++] = (CtBehavior)ctMember3;
    } 
    return arrayOfCtBehavior;
  }
  
  public CtConstructor[] getConstructors() {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.consHead();
    CtMember ctMember2 = cache.lastCons();
    byte b1 = 0;
    CtMember ctMember3 = ctMember1;
    while (ctMember3 != ctMember2) {
      ctMember3 = ctMember3.next();
      if (isPubCons((CtConstructor)ctMember3))
        b1++; 
    } 
    CtConstructor[] arrayOfCtConstructor = new CtConstructor[b1];
    byte b2 = 0;
    ctMember3 = ctMember1;
    while (ctMember3 != ctMember2) {
      ctMember3 = ctMember3.next();
      CtConstructor ctConstructor = (CtConstructor)ctMember3;
      if (isPubCons(ctConstructor))
        arrayOfCtConstructor[b2++] = ctConstructor; 
    } 
    return arrayOfCtConstructor;
  }
  
  private static boolean isPubCons(CtConstructor paramCtConstructor) {
    return (!Modifier.isPrivate(paramCtConstructor.getModifiers()) && paramCtConstructor
      .isConstructor());
  }
  
  public CtConstructor getConstructor(String paramString) throws NotFoundException {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.consHead();
    CtMember ctMember2 = cache.lastCons();
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      CtConstructor ctConstructor = (CtConstructor)ctMember1;
      if (ctConstructor.getMethodInfo2().getDescriptor().equals(paramString) && ctConstructor
        .isConstructor())
        return ctConstructor; 
    } 
    return super.getConstructor(paramString);
  }
  
  public CtConstructor[] getDeclaredConstructors() {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.consHead();
    CtMember ctMember2 = cache.lastCons();
    byte b1 = 0;
    CtMember ctMember3 = ctMember1;
    while (ctMember3 != ctMember2) {
      ctMember3 = ctMember3.next();
      CtConstructor ctConstructor = (CtConstructor)ctMember3;
      if (ctConstructor.isConstructor())
        b1++; 
    } 
    CtConstructor[] arrayOfCtConstructor = new CtConstructor[b1];
    byte b2 = 0;
    ctMember3 = ctMember1;
    while (ctMember3 != ctMember2) {
      ctMember3 = ctMember3.next();
      CtConstructor ctConstructor = (CtConstructor)ctMember3;
      if (ctConstructor.isConstructor())
        arrayOfCtConstructor[b2++] = ctConstructor; 
    } 
    return arrayOfCtConstructor;
  }
  
  public CtConstructor getClassInitializer() {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.consHead();
    CtMember ctMember2 = cache.lastCons();
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      CtConstructor ctConstructor = (CtConstructor)ctMember1;
      if (ctConstructor.isClassInitializer())
        return ctConstructor; 
    } 
    return null;
  }
  
  public CtMethod[] getMethods() {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    getMethods0(hashMap, this);
    return (CtMethod[])hashMap.values().toArray((Object[])new CtMethod[hashMap.size()]);
  }
  
  private static void getMethods0(HashMap<String, CtMember> paramHashMap, CtClass paramCtClass) {
    try {
      CtClass[] arrayOfCtClass = paramCtClass.getInterfaces();
      int i = arrayOfCtClass.length;
      for (byte b = 0; b < i; b++)
        getMethods0(paramHashMap, arrayOfCtClass[b]); 
    } catch (NotFoundException notFoundException) {}
    try {
      CtClass ctClass = paramCtClass.getSuperclass();
      if (ctClass != null)
        getMethods0(paramHashMap, ctClass); 
    } catch (NotFoundException notFoundException) {}
    if (paramCtClass instanceof CtClassType1) {
      CtMember.Cache cache = ((CtClassType1)paramCtClass).getMembers();
      CtMember ctMember1 = cache.methodHead();
      CtMember ctMember2 = cache.lastMethod();
      while (ctMember1 != ctMember2) {
        ctMember1 = ctMember1.next();
        if (!Modifier.isPrivate(ctMember1.getModifiers()))
          paramHashMap.put(((CtMethod)ctMember1).getStringRep(), ctMember1); 
      } 
    } 
  }
  
  public CtMethod getMethod(String paramString1, String paramString2) throws NotFoundException {
    CtMethod ctMethod = getMethod0(this, paramString1, paramString2);
    if (ctMethod != null)
      return ctMethod; 
    throw new NotFoundException(paramString1 + "(..) is not found in " + 
        getName());
  }
  
  private static CtMethod getMethod0(CtClass paramCtClass, String paramString1, String paramString2) {
    if (paramCtClass instanceof CtClassType1) {
      CtMember.Cache cache = ((CtClassType1)paramCtClass).getMembers();
      CtMember ctMember1 = cache.methodHead();
      CtMember ctMember2 = cache.lastMethod();
      while (ctMember1 != ctMember2) {
        ctMember1 = ctMember1.next();
        if (ctMember1.getName().equals(paramString1) && ((CtMethod)ctMember1)
          .getMethodInfo2().getDescriptor().equals(paramString2))
          return (CtMethod)ctMember1; 
      } 
    } 
    try {
      CtClass ctClass = paramCtClass.getSuperclass();
      if (ctClass != null) {
        CtMethod ctMethod = getMethod0(ctClass, paramString1, paramString2);
        if (ctMethod != null)
          return ctMethod; 
      } 
    } catch (NotFoundException notFoundException) {}
    try {
      CtClass[] arrayOfCtClass = paramCtClass.getInterfaces();
      int i = arrayOfCtClass.length;
      for (byte b = 0; b < i; b++) {
        CtMethod ctMethod = getMethod0(arrayOfCtClass[b], paramString1, paramString2);
        if (ctMethod != null)
          return ctMethod; 
      } 
    } catch (NotFoundException notFoundException) {}
    return null;
  }
  
  public CtMethod[] getDeclaredMethods() {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.methodHead();
    CtMember ctMember2 = cache.lastMethod();
    int i = CtMember.Cache.count(ctMember1, ctMember2);
    CtMethod[] arrayOfCtMethod = new CtMethod[i];
    byte b = 0;
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      arrayOfCtMethod[b++] = (CtMethod)ctMember1;
    } 
    return arrayOfCtMethod;
  }
  
  public CtMethod[] getDeclaredMethods(String paramString) throws NotFoundException {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.methodHead();
    CtMember ctMember2 = cache.lastMethod();
    ArrayList<CtMethod> arrayList = new ArrayList();
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      if (ctMember1.getName().equals(paramString))
        arrayList.add((CtMethod)ctMember1); 
    } 
    return arrayList.<CtMethod>toArray(new CtMethod[arrayList.size()]);
  }
  
  public CtMethod getDeclaredMethod(String paramString) throws NotFoundException {
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.methodHead();
    CtMember ctMember2 = cache.lastMethod();
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      if (ctMember1.getName().equals(paramString))
        return (CtMethod)ctMember1; 
    } 
    throw new NotFoundException(paramString + "(..) is not found in " + 
        getName());
  }
  
  public CtMethod getDeclaredMethod(String paramString, CtClass[] paramArrayOfCtClass) throws NotFoundException {
    String str = Descriptor.ofParameters(paramArrayOfCtClass);
    CtMember.Cache cache = getMembers();
    CtMember ctMember1 = cache.methodHead();
    CtMember ctMember2 = cache.lastMethod();
    while (ctMember1 != ctMember2) {
      ctMember1 = ctMember1.next();
      if (ctMember1.getName().equals(paramString) && ((CtMethod)ctMember1)
        .getMethodInfo2().getDescriptor().startsWith(str))
        return (CtMethod)ctMember1; 
    } 
    throw new NotFoundException(paramString + "(..) is not found in " + 
        getName());
  }
  
  public void addField(CtField paramCtField, String paramString) throws CannotCompileException {
    addField(paramCtField, CtField.Initializer.byExpr(paramString));
  }
  
  public void addField(CtField paramCtField, CtField.Initializer paramInitializer) throws CannotCompileException {
    checkModify();
    if (paramCtField.getDeclaringClass() != this)
      throw new CannotCompileException("cannot add"); 
    if (paramInitializer == null)
      paramInitializer = paramCtField.getInit(); 
    if (paramInitializer != null) {
      paramInitializer.check(paramCtField.getSignature());
      int i = paramCtField.getModifiers();
      if (Modifier.isStatic(i) && Modifier.isFinal(i))
        try {
          ConstPool14 constPool14 = getClassFile2().getConstPool();
          int j = paramInitializer.getConstantValue(constPool14, paramCtField.getType());
          if (j != 0) {
            paramCtField.getFieldInfo2().addAttribute((AttributeInfo)new ConstantAttribute(constPool14, j));
            paramInitializer = null;
          } 
        } catch (NotFoundException notFoundException) {} 
    } 
    getMembers().addField(paramCtField);
    getClassFile2().addField(paramCtField.getFieldInfo2());
    if (paramInitializer != null) {
      CtClassType ctClassType1 = new CtClassType(paramCtField, paramInitializer);
      CtClassType ctClassType2 = this.fieldInitializers;
      if (ctClassType2 == null) {
        this.fieldInitializers = ctClassType1;
      } else {
        while (ctClassType2.next != null)
          ctClassType2 = ctClassType2.next; 
        ctClassType2.next = ctClassType1;
      } 
    } 
  }
  
  public void removeField(CtField paramCtField) throws NotFoundException {
    checkModify();
    FieldInfo fieldInfo = paramCtField.getFieldInfo2();
    ClassFile classFile = getClassFile2();
    if (classFile.getFields().remove(fieldInfo)) {
      getMembers().remove(paramCtField);
      this.gcConstPool = true;
    } else {
      throw new NotFoundException(paramCtField.toString());
    } 
  }
  
  public CtConstructor makeClassInitializer() throws CannotCompileException {
    CtConstructor ctConstructor = getClassInitializer();
    if (ctConstructor != null)
      return ctConstructor; 
    checkModify();
    ClassFile classFile = getClassFile2();
    Bytecode1 bytecode1 = new Bytecode1(classFile.getConstPool(), 0, 0);
    modifyClassConstructor(classFile, bytecode1, 0, 0);
    return getClassInitializer();
  }
  
  public void addConstructor(CtConstructor paramCtConstructor) throws CannotCompileException {
    checkModify();
    if (paramCtConstructor.getDeclaringClass() != this)
      throw new CannotCompileException("cannot add"); 
    getMembers().addConstructor(paramCtConstructor);
    getClassFile2().addMethod(paramCtConstructor.getMethodInfo2());
  }
  
  public void removeConstructor(CtConstructor paramCtConstructor) throws NotFoundException {
    checkModify();
    MethodInfo methodInfo = paramCtConstructor.getMethodInfo2();
    ClassFile classFile = getClassFile2();
    if (classFile.getMethods().remove(methodInfo)) {
      getMembers().remove(paramCtConstructor);
      this.gcConstPool = true;
    } else {
      throw new NotFoundException(paramCtConstructor.toString());
    } 
  }
  
  public void addMethod(CtMethod paramCtMethod) throws CannotCompileException {
    checkModify();
    if (paramCtMethod.getDeclaringClass() != this)
      throw new CannotCompileException("bad declaring class"); 
    int i = paramCtMethod.getModifiers();
    if ((getModifiers() & 0x200) != 0) {
      if (Modifier.isProtected(i) || Modifier.isPrivate(i))
        throw new CannotCompileException("an interface method must be public: " + paramCtMethod
            .toString()); 
      paramCtMethod.setModifiers(i | 0x1);
    } 
    getMembers().addMethod(paramCtMethod);
    getClassFile2().addMethod(paramCtMethod.getMethodInfo2());
    if ((i & 0x400) != 0)
      setModifiers(getModifiers() | 0x400); 
  }
  
  public void removeMethod(CtMethod paramCtMethod) throws NotFoundException {
    checkModify();
    MethodInfo methodInfo = paramCtMethod.getMethodInfo2();
    ClassFile classFile = getClassFile2();
    if (classFile.getMethods().remove(methodInfo)) {
      getMembers().remove(paramCtMethod);
      this.gcConstPool = true;
    } else {
      throw new NotFoundException(paramCtMethod.toString());
    } 
  }
  
  public byte[] getAttribute(String paramString) {
    AttributeInfo attributeInfo = getClassFile2().getAttribute(paramString);
    if (attributeInfo == null)
      return null; 
    return attributeInfo.get();
  }
  
  public void setAttribute(String paramString, byte[] paramArrayOfbyte) {
    checkModify();
    ClassFile classFile = getClassFile2();
    classFile.addAttribute(new AttributeInfo(classFile.getConstPool(), paramString, paramArrayOfbyte));
  }
  
  public void instrument(CodeConverter paramCodeConverter) throws CannotCompileException {
    checkModify();
    ClassFile classFile = getClassFile2();
    ConstPool14 constPool14 = classFile.getConstPool();
    List<MethodInfo> list = classFile.getMethods();
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      MethodInfo methodInfo = list.get(b);
      paramCodeConverter.doit(this, methodInfo, constPool14);
    } 
  }
  
  public void instrument(ExprEditor paramExprEditor) throws CannotCompileException {
    checkModify();
    ClassFile classFile = getClassFile2();
    List<MethodInfo> list = classFile.getMethods();
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      MethodInfo methodInfo = list.get(b);
      paramExprEditor.doit(this, methodInfo);
    } 
  }
  
  public void prune() {
    if (this.wasPruned)
      return; 
    this.wasPruned = this.wasFrozen = true;
    getClassFile2().prune();
  }
  
  public void rebuildClassFile() {
    this.gcConstPool = true;
  }
  
  public void toBytecode(DataOutputStream paramDataOutputStream) throws CannotCompileException, IOException {
    try {
      if (isModified()) {
        checkPruned("toBytecode");
        ClassFile classFile = getClassFile2();
        if (this.gcConstPool) {
          classFile.compact();
          this.gcConstPool = false;
        } 
        modifyClassConstructor(classFile);
        modifyConstructors(classFile);
        if (debugDump != null)
          dumpClassFile(classFile); 
        classFile.write(paramDataOutputStream);
        paramDataOutputStream.flush();
        this.fieldInitializers = null;
        if (this.doPruning) {
          classFile.prune();
          this.wasPruned = true;
        } 
      } else {
        this.classPool.writeClassfile(getName(), paramDataOutputStream);
      } 
      this.getCount = 0;
      this.wasFrozen = true;
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (IOException iOException) {
      throw new CannotCompileException(iOException);
    } 
  }
  
  private void dumpClassFile(ClassFile paramClassFile) throws IOException {
    DataOutputStream dataOutputStream = makeFileOutput(debugDump);
    try {
      paramClassFile.write(dataOutputStream);
    } finally {
      dataOutputStream.close();
    } 
  }
  
  private void checkPruned(String paramString) {
    if (this.wasPruned)
      throw new RuntimeException(paramString + "(): " + getName() + " was pruned."); 
  }
  
  public boolean stopPruning(boolean paramBoolean) {
    boolean bool = !this.doPruning ? true : false;
    this.doPruning = !paramBoolean;
    return bool;
  }
  
  private void modifyClassConstructor(ClassFile paramClassFile) throws CannotCompileException, NotFoundException {
    if (this.fieldInitializers == null)
      return; 
    Bytecode1 bytecode1 = new Bytecode1(paramClassFile.getConstPool(), 0, 0);
    Javac javac = new Javac(bytecode1, this);
    int i = 0;
    boolean bool = false;
    for (CtClassType ctClassType = this.fieldInitializers; ctClassType != null; ctClassType = ctClassType.next) {
      CtField ctField = ctClassType.field;
      if (Modifier.isStatic(ctField.getModifiers())) {
        bool = true;
        int j = ctClassType.init.compileIfStatic(ctField.getType(), ctField.getName(), bytecode1, javac);
        if (i < j)
          i = j; 
      } 
    } 
    if (bool)
      modifyClassConstructor(paramClassFile, bytecode1, i, 0); 
  }
  
  private void modifyClassConstructor(ClassFile paramClassFile, Bytecode1 paramBytecode1, int paramInt1, int paramInt2) throws CannotCompileException {
    MethodInfo methodInfo = paramClassFile.getStaticInitializer();
    if (methodInfo == null) {
      paramBytecode1.add(177);
      paramBytecode1.setMaxStack(paramInt1);
      paramBytecode1.setMaxLocals(paramInt2);
      methodInfo = new MethodInfo(paramClassFile.getConstPool(), "<clinit>", "()V");
      methodInfo.setAccessFlags(8);
      methodInfo.setCodeAttribute(paramBytecode1.toCodeAttribute());
      paramClassFile.addMethod(methodInfo);
      CtMember.Cache cache = hasMemberCache();
      if (cache != null)
        cache.addConstructor(new CtConstructor(methodInfo, this)); 
    } else {
      CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
      if (codeAttribute == null)
        throw new CannotCompileException("empty <clinit>"); 
      try {
        CodeIterator codeIterator = codeAttribute.iterator();
        int i = codeIterator.insertEx(paramBytecode1.get());
        codeIterator.insert(paramBytecode1.getExceptionTable(), i);
        int j = codeAttribute.getMaxStack();
        if (j < paramInt1)
          codeAttribute.setMaxStack(paramInt1); 
        int k = codeAttribute.getMaxLocals();
        if (k < paramInt2)
          codeAttribute.setMaxLocals(paramInt2); 
      } catch (BadBytecode badBytecode) {
        throw new CannotCompileException(badBytecode);
      } 
    } 
    try {
      methodInfo.rebuildStackMapIf6(this.classPool, paramClassFile);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  private void modifyConstructors(ClassFile paramClassFile) throws CannotCompileException, NotFoundException {
    if (this.fieldInitializers == null)
      return; 
    ConstPool14 constPool14 = paramClassFile.getConstPool();
    List<MethodInfo> list = paramClassFile.getMethods();
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      MethodInfo methodInfo = list.get(b);
      if (methodInfo.isConstructor()) {
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        if (codeAttribute != null)
          try {
            Bytecode1 bytecode1 = new Bytecode1(constPool14, 0, codeAttribute.getMaxLocals());
            CtClass[] arrayOfCtClass = Descriptor.getParameterTypes(methodInfo
                .getDescriptor(), this.classPool);
            int j = makeFieldInitializer(bytecode1, arrayOfCtClass);
            insertAuxInitializer(codeAttribute, bytecode1, j);
            methodInfo.rebuildStackMapIf6(this.classPool, paramClassFile);
          } catch (BadBytecode badBytecode) {
            throw new CannotCompileException(badBytecode);
          }  
      } 
    } 
  }
  
  private static void insertAuxInitializer(CodeAttribute paramCodeAttribute, Bytecode1 paramBytecode1, int paramInt) throws BadBytecode {
    CodeIterator codeIterator = paramCodeAttribute.iterator();
    int i = codeIterator.skipSuperConstructor();
    if (i < 0) {
      i = codeIterator.skipThisConstructor();
      if (i >= 0)
        return; 
    } 
    int j = codeIterator.insertEx(paramBytecode1.get());
    codeIterator.insert(paramBytecode1.getExceptionTable(), j);
    int k = paramCodeAttribute.getMaxStack();
    if (k < paramInt)
      paramCodeAttribute.setMaxStack(paramInt); 
  }
  
  private int makeFieldInitializer(Bytecode1 paramBytecode1, CtClass[] paramArrayOfCtClass) throws CannotCompileException, NotFoundException {
    int i = 0;
    Javac javac = new Javac(paramBytecode1, this);
    try {
      javac.recordParams(paramArrayOfCtClass, false);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } 
    for (CtClassType ctClassType = this.fieldInitializers; ctClassType != null; ctClassType = ctClassType.next) {
      CtField ctField = ctClassType.field;
      if (!Modifier.isStatic(ctField.getModifiers())) {
        int j = ctClassType.init.compile(ctField.getType(), ctField.getName(), paramBytecode1, paramArrayOfCtClass, javac);
        if (i < j)
          i = j; 
      } 
    } 
    return i;
  }
  
  Hashtable getHiddenMethods() {
    if (this.hiddenMethods == null)
      this.hiddenMethods = new Hashtable<Object, Object>(); 
    return this.hiddenMethods;
  }
  
  int getUniqueNumber() {
    return this.uniqueNumberSeed++;
  }
  
  public String makeUniqueName(String paramString) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    makeMemberList(hashMap);
    Set set = hashMap.keySet();
    String[] arrayOfString = new String[set.size()];
    set.toArray((Object[])arrayOfString);
    if (notFindInArray(paramString, arrayOfString))
      return paramString; 
    byte b = 100;
    while (true) {
      if (b > 'ϧ')
        throw new RuntimeException("too many unique name"); 
      String str = paramString + b++;
      if (notFindInArray(str, arrayOfString))
        return str; 
    } 
  }
  
  private static boolean notFindInArray(String paramString, String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfString[b].startsWith(paramString))
        return false; 
    } 
    return true;
  }
  
  private void makeMemberList(HashMap<String, CtClassType1> paramHashMap) {
    int i = getModifiers();
    if (Modifier.isAbstract(i) || Modifier.isInterface(i))
      try {
        CtClass[] arrayOfCtClass = getInterfaces();
        int k = arrayOfCtClass.length;
        for (byte b1 = 0; b1 < k; b1++) {
          CtClass ctClass = arrayOfCtClass[b1];
          if (ctClass != null && ctClass instanceof CtClassType1)
            ((CtClassType1)ctClass).makeMemberList(paramHashMap); 
        } 
      } catch (NotFoundException notFoundException) {} 
    try {
      CtClass ctClass = getSuperclass();
      if (ctClass != null && ctClass instanceof CtClassType1)
        ((CtClassType1)ctClass).makeMemberList(paramHashMap); 
    } catch (NotFoundException notFoundException) {}
    List<MethodInfo> list = getClassFile2().getMethods();
    int j = list.size();
    byte b;
    for (b = 0; b < j; b++) {
      MethodInfo methodInfo = list.get(b);
      paramHashMap.put(methodInfo.getName(), this);
    } 
    list = getClassFile2().getFields();
    j = list.size();
    for (b = 0; b < j; b++) {
      FieldInfo fieldInfo = (FieldInfo)list.get(b);
      paramHashMap.put(fieldInfo.getName(), this);
    } 
  }
}
