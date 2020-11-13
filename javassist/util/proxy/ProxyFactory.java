package javassist.util.proxy;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javassist.CannotCompileException;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.StackMapTable;

public class ProxyFactory {
  private Class superClass;
  
  private Class[] interfaces;
  
  private MethodFilter methodFilter;
  
  private MethodHandler handler;
  
  private List signatureMethods;
  
  private boolean hasGetHandler;
  
  private byte[] signature;
  
  private String classname;
  
  private String basename;
  
  private String superName;
  
  private Class thisClass;
  
  private boolean factoryUseCache;
  
  private boolean factoryWriteReplace;
  
  public String writeDirectory;
  
  private static final Class OBJECT_TYPE = Object.class;
  
  private static final String HOLDER = "_methods_";
  
  private static final String HOLDER_TYPE = "[Ljava/lang/reflect/Method;";
  
  private static final String FILTER_SIGNATURE_FIELD = "_filter_signature";
  
  private static final String FILTER_SIGNATURE_TYPE = "[B";
  
  private static final String HANDLER = "handler";
  
  private static final String NULL_INTERCEPTOR_HOLDER = "javassist.util.proxy.RuntimeSupport";
  
  private static final String DEFAULT_INTERCEPTOR = "default_interceptor";
  
  private static final String HANDLER_TYPE = 'L' + MethodHandler.class
    .getName().replace('.', '/') + ';';
  
  private static final String HANDLER_SETTER = "setHandler";
  
  private static final String HANDLER_SETTER_TYPE = "(" + HANDLER_TYPE + ")V";
  
  private static final String HANDLER_GETTER = "getHandler";
  
  private static final String HANDLER_GETTER_TYPE = "()" + HANDLER_TYPE;
  
  private static final String SERIAL_VERSION_UID_FIELD = "serialVersionUID";
  
  private static final String SERIAL_VERSION_UID_TYPE = "J";
  
  private static final long SERIAL_VERSION_UID_VALUE = -1L;
  
  public static volatile boolean useCache = true;
  
  public static volatile boolean useWriteReplace = true;
  
  public boolean isUseCache() {
    return this.factoryUseCache;
  }
  
  public void setUseCache(boolean paramBoolean) {
    if (this.handler != null && paramBoolean)
      throw new RuntimeException("caching cannot be enabled if the factory default interceptor has been set"); 
    this.factoryUseCache = paramBoolean;
  }
  
  public boolean isUseWriteReplace() {
    return this.factoryWriteReplace;
  }
  
  public void setUseWriteReplace(boolean paramBoolean) {
    this.factoryWriteReplace = paramBoolean;
  }
  
  private static WeakHashMap proxyCache = new WeakHashMap<Object, Object>();
  
  public static boolean isProxyClass(Class<?> paramClass) {
    return Proxy.class.isAssignableFrom(paramClass);
  }
  
  static class ProxyDetails {
    byte[] signature;
    
    WeakReference proxyClass;
    
    boolean isUseWriteReplace;
    
    ProxyDetails(byte[] param1ArrayOfbyte, Class<?> param1Class, boolean param1Boolean) {
      this.signature = param1ArrayOfbyte;
      this.proxyClass = new WeakReference<Class<?>>(param1Class);
      this.isUseWriteReplace = param1Boolean;
    }
  }
  
  public ProxyFactory() {
    this.superClass = null;
    this.interfaces = null;
    this.methodFilter = null;
    this.handler = null;
    this.signature = null;
    this.signatureMethods = null;
    this.hasGetHandler = false;
    this.thisClass = null;
    this.writeDirectory = null;
    this.factoryUseCache = useCache;
    this.factoryWriteReplace = useWriteReplace;
  }
  
  public void setSuperclass(Class paramClass) {
    this.superClass = paramClass;
    this.signature = null;
  }
  
  public Class getSuperclass() {
    return this.superClass;
  }
  
  public void setInterfaces(Class[] paramArrayOfClass) {
    this.interfaces = paramArrayOfClass;
    this.signature = null;
  }
  
  public Class[] getInterfaces() {
    return this.interfaces;
  }
  
  public void setFilter(MethodFilter paramMethodFilter) {
    this.methodFilter = paramMethodFilter;
    this.signature = null;
  }
  
  public Class createClass() {
    if (this.signature == null)
      computeSignature(this.methodFilter); 
    return createClass1();
  }
  
  public Class createClass(MethodFilter paramMethodFilter) {
    computeSignature(paramMethodFilter);
    return createClass1();
  }
  
  Class createClass(byte[] paramArrayOfbyte) {
    installSignature(paramArrayOfbyte);
    return createClass1();
  }
  
  private Class createClass1() {
    Class clazz = this.thisClass;
    if (clazz == null) {
      ClassLoader classLoader = getClassLoader();
      synchronized (proxyCache) {
        if (this.factoryUseCache) {
          createClass2(classLoader);
        } else {
          createClass3(classLoader);
        } 
        clazz = this.thisClass;
        this.thisClass = null;
      } 
    } 
    return clazz;
  }
  
  private static char[] hexDigits = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
  
  public String getKey(Class paramClass, Class[] paramArrayOfClass, byte[] paramArrayOfbyte, boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramClass != null)
      stringBuffer.append(paramClass.getName()); 
    stringBuffer.append(":");
    byte b;
    for (b = 0; b < paramArrayOfClass.length; b++) {
      stringBuffer.append(paramArrayOfClass[b].getName());
      stringBuffer.append(":");
    } 
    for (b = 0; b < paramArrayOfbyte.length; b++) {
      byte b1 = paramArrayOfbyte[b];
      int i = b1 & 0xF;
      int j = b1 >> 4 & 0xF;
      stringBuffer.append(hexDigits[i]);
      stringBuffer.append(hexDigits[j]);
    } 
    if (paramBoolean)
      stringBuffer.append(":w"); 
    return stringBuffer.toString();
  }
  
  private void createClass2(ClassLoader paramClassLoader) {
    String str = getKey(this.superClass, this.interfaces, this.signature, this.factoryWriteReplace);
    HashMap<Object, Object> hashMap = (HashMap)proxyCache.get(paramClassLoader);
    if (hashMap == null) {
      hashMap = new HashMap<Object, Object>();
      proxyCache.put(paramClassLoader, hashMap);
    } 
    ProxyDetails proxyDetails = (ProxyDetails)hashMap.get(str);
    if (proxyDetails != null) {
      WeakReference<Class<?>> weakReference = proxyDetails.proxyClass;
      this.thisClass = weakReference.get();
      if (this.thisClass != null)
        return; 
    } 
    createClass3(paramClassLoader);
    proxyDetails = new ProxyDetails(this.signature, this.thisClass, this.factoryWriteReplace);
    hashMap.put(str, proxyDetails);
  }
  
  private void createClass3(ClassLoader paramClassLoader) {
    allocateClassName();
    try {
      ClassFile classFile = make();
      if (this.writeDirectory != null)
        FactoryHelper.writeFile(classFile, this.writeDirectory); 
      this.thisClass = FactoryHelper.toClass(classFile, paramClassLoader, getDomain());
      setField("_filter_signature", this.signature);
      if (!this.factoryUseCache)
        setField("default_interceptor", this.handler); 
    } catch (CannotCompileException cannotCompileException) {
      throw new RuntimeException(cannotCompileException.getMessage(), cannotCompileException);
    } 
  }
  
  private void setField(String paramString, Object paramObject) {
    if (this.thisClass != null && paramObject != null)
      try {
        Field field = this.thisClass.getField(paramString);
        SecurityActions.setAccessible(field, true);
        field.set((Object)null, paramObject);
        SecurityActions.setAccessible(field, false);
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }  
  }
  
  static byte[] getFilterSignature(Class paramClass) {
    return (byte[])getField(paramClass, "_filter_signature");
  }
  
  private static Object getField(Class paramClass, String paramString) {
    try {
      Field field = paramClass.getField(paramString);
      field.setAccessible(true);
      Object object = field.get((Object)null);
      field.setAccessible(false);
      return object;
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  public static MethodHandler getHandler(Proxy paramProxy) {
    try {
      Field field = paramProxy.getClass().getDeclaredField("handler");
      field.setAccessible(true);
      Object object = field.get(paramProxy);
      field.setAccessible(false);
      return (MethodHandler)object;
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  public static ClassLoaderProvider classLoaderProvider = new ClassLoaderProvider() {
      public ClassLoader get(ProxyFactory param1ProxyFactory) {
        return param1ProxyFactory.getClassLoader0();
      }
    };
  
  protected ClassLoader getClassLoader() {
    return classLoaderProvider.get(this);
  }
  
  protected ClassLoader getClassLoader0() {
    ClassLoader classLoader = null;
    if (this.superClass != null && !this.superClass.getName().equals("java.lang.Object")) {
      classLoader = this.superClass.getClassLoader();
    } else if (this.interfaces != null && this.interfaces.length > 0) {
      classLoader = this.interfaces[0].getClassLoader();
    } 
    if (classLoader == null) {
      classLoader = getClass().getClassLoader();
      if (classLoader == null) {
        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
          classLoader = ClassLoader.getSystemClassLoader(); 
      } 
    } 
    return classLoader;
  }
  
  protected ProtectionDomain getDomain() {
    Class<?> clazz;
    if (this.superClass != null && !this.superClass.getName().equals("java.lang.Object")) {
      clazz = this.superClass;
    } else if (this.interfaces != null && this.interfaces.length > 0) {
      clazz = this.interfaces[0];
    } else {
      clazz = getClass();
    } 
    return clazz.getProtectionDomain();
  }
  
  public Object create(Class[] paramArrayOfClass, Object[] paramArrayOfObject, MethodHandler paramMethodHandler) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Object object = create(paramArrayOfClass, paramArrayOfObject);
    ((Proxy)object).setHandler(paramMethodHandler);
    return object;
  }
  
  public Object create(Class[] paramArrayOfClass, Object[] paramArrayOfObject) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Class clazz = createClass();
    Constructor constructor = clazz.getConstructor(paramArrayOfClass);
    return constructor.newInstance(paramArrayOfObject);
  }
  
  public void setHandler(MethodHandler paramMethodHandler) {
    if (this.factoryUseCache && paramMethodHandler != null) {
      this.factoryUseCache = false;
      this.thisClass = null;
    } 
    this.handler = paramMethodHandler;
    setField("default_interceptor", this.handler);
  }
  
  public static UniqueName nameGenerator = new UniqueName() {
      private final String sep = "_$$_jvst" + Integer.toHexString(hashCode() & 0xFFF) + "_";
      
      private int counter = 0;
      
      public String get(String param1String) {
        return param1String + this.sep + Integer.toHexString(this.counter++);
      }
    };
  
  private static String makeProxyName(String paramString) {
    synchronized (nameGenerator) {
      return nameGenerator.get(paramString);
    } 
  }
  
  private ClassFile make() throws CannotCompileException {
    ClassFile classFile = new ClassFile(false, this.classname, this.superName);
    classFile.setAccessFlags(1);
    setInterfaces(classFile, this.interfaces, this.hasGetHandler ? Proxy.class : ProxyObject.class);
    ConstPool14 constPool14 = classFile.getConstPool();
    if (!this.factoryUseCache) {
      FieldInfo fieldInfo = new FieldInfo(constPool14, "default_interceptor", HANDLER_TYPE);
      fieldInfo.setAccessFlags(9);
      classFile.addField(fieldInfo);
    } 
    FieldInfo fieldInfo1 = new FieldInfo(constPool14, "handler", HANDLER_TYPE);
    fieldInfo1.setAccessFlags(2);
    classFile.addField(fieldInfo1);
    FieldInfo fieldInfo2 = new FieldInfo(constPool14, "_filter_signature", "[B");
    fieldInfo2.setAccessFlags(9);
    classFile.addField(fieldInfo2);
    FieldInfo fieldInfo3 = new FieldInfo(constPool14, "serialVersionUID", "J");
    fieldInfo3.setAccessFlags(25);
    classFile.addField(fieldInfo3);
    makeConstructors(this.classname, classFile, constPool14, this.classname);
    ArrayList arrayList = new ArrayList();
    int i = overrideMethods(classFile, constPool14, this.classname, arrayList);
    addClassInitializer(classFile, constPool14, this.classname, i, arrayList);
    addSetter(this.classname, classFile, constPool14);
    if (!this.hasGetHandler)
      addGetter(this.classname, classFile, constPool14); 
    if (this.factoryWriteReplace)
      try {
        classFile.addMethod(makeWriteReplace(constPool14));
      } catch (DuplicateMemberException duplicateMemberException) {} 
    this.thisClass = null;
    return classFile;
  }
  
  private void checkClassAndSuperName() {
    if (this.interfaces == null)
      this.interfaces = new Class[0]; 
    if (this.superClass == null) {
      this.superClass = OBJECT_TYPE;
      this.superName = this.superClass.getName();
      this
        .basename = (this.interfaces.length == 0) ? this.superName : this.interfaces[0].getName();
    } else {
      this.superName = this.superClass.getName();
      this.basename = this.superName;
    } 
    if (Modifier.isFinal(this.superClass.getModifiers()))
      throw new RuntimeException(this.superName + " is final"); 
    if (this.basename.startsWith("java."))
      this.basename = "org.javassist.tmp." + this.basename; 
  }
  
  private void allocateClassName() {
    this.classname = makeProxyName(this.basename);
  }
  
  private static Comparator sorter = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        Map.Entry entry1 = (Map.Entry)param1Object1;
        Map.Entry entry2 = (Map.Entry)param1Object2;
        String str1 = (String)entry1.getKey();
        String str2 = (String)entry2.getKey();
        return str1.compareTo(str2);
      }
    };
  
  private static final String HANDLER_GETTER_KEY = "getHandler:()";
  
  private void makeSortedMethodList() {
    checkClassAndSuperName();
    this.hasGetHandler = false;
    HashMap hashMap = getMethods(this.superClass, this.interfaces);
    this.signatureMethods = new ArrayList(hashMap.entrySet());
    Collections.sort(this.signatureMethods, sorter);
  }
  
  private void computeSignature(MethodFilter paramMethodFilter) {
    makeSortedMethodList();
    int i = this.signatureMethods.size();
    int j = i + 7 >> 3;
    this.signature = new byte[j];
    for (byte b = 0; b < i; b++) {
      Map.Entry entry = this.signatureMethods.get(b);
      Method method = (Method)entry.getValue();
      int k = method.getModifiers();
      if (!Modifier.isFinal(k) && !Modifier.isStatic(k) && 
        isVisible(k, this.basename, method) && (paramMethodFilter == null || paramMethodFilter.isHandled(method)))
        setBit(this.signature, b); 
    } 
  }
  
  private void installSignature(byte[] paramArrayOfbyte) {
    makeSortedMethodList();
    int i = this.signatureMethods.size();
    int j = i + 7 >> 3;
    if (paramArrayOfbyte.length != j)
      throw new RuntimeException("invalid filter signature length for deserialized proxy class"); 
    this.signature = paramArrayOfbyte;
  }
  
  private boolean testBit(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramInt >> 3;
    if (i > paramArrayOfbyte.length)
      return false; 
    int j = paramInt & 0x7;
    int k = 1 << j;
    byte b = paramArrayOfbyte[i];
    return ((b & k) != 0);
  }
  
  private void setBit(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramInt >> 3;
    if (i < paramArrayOfbyte.length) {
      int j = paramInt & 0x7;
      int k = 1 << j;
      byte b = paramArrayOfbyte[i];
      paramArrayOfbyte[i] = (byte)(b | k);
    } 
  }
  
  private static void setInterfaces(ClassFile paramClassFile, Class[] paramArrayOfClass, Class paramClass) {
    String arrayOfString[], str = paramClass.getName();
    if (paramArrayOfClass == null || paramArrayOfClass.length == 0) {
      arrayOfString = new String[] { str };
    } else {
      arrayOfString = new String[paramArrayOfClass.length + 1];
      for (byte b = 0; b < paramArrayOfClass.length; b++)
        arrayOfString[b] = paramArrayOfClass[b].getName(); 
      arrayOfString[paramArrayOfClass.length] = str;
    } 
    paramClassFile.setInterfaces(arrayOfString);
  }
  
  private static void addClassInitializer(ClassFile paramClassFile, ConstPool14 paramConstPool14, String paramString, int paramInt, ArrayList paramArrayList) throws CannotCompileException {
    FieldInfo fieldInfo = new FieldInfo(paramConstPool14, "_methods_", "[Ljava/lang/reflect/Method;");
    fieldInfo.setAccessFlags(10);
    paramClassFile.addField(fieldInfo);
    MethodInfo methodInfo = new MethodInfo(paramConstPool14, "<clinit>", "()V");
    methodInfo.setAccessFlags(8);
    setThrows(methodInfo, paramConstPool14, new Class[] { ClassNotFoundException.class });
    Bytecode1 bytecode1 = new Bytecode1(paramConstPool14, 0, 2);
    bytecode1.addIconst(paramInt * 2);
    bytecode1.addAnewarray("java.lang.reflect.Method");
    boolean bool1 = false;
    bytecode1.addAstore(0);
    bytecode1.addLdc(paramString);
    bytecode1.addInvokestatic("java.lang.Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
    boolean bool2 = true;
    bytecode1.addAstore(1);
    Iterator<Find2MethodsArgs> iterator = paramArrayList.iterator();
    while (iterator.hasNext()) {
      Find2MethodsArgs find2MethodsArgs = iterator.next();
      callFind2Methods(bytecode1, find2MethodsArgs.methodName, find2MethodsArgs.delegatorName, find2MethodsArgs.origIndex, find2MethodsArgs.descriptor, 1, 0);
    } 
    bytecode1.addAload(0);
    bytecode1.addPutstatic(paramString, "_methods_", "[Ljava/lang/reflect/Method;");
    bytecode1.addLconst(-1L);
    bytecode1.addPutstatic(paramString, "serialVersionUID", "J");
    bytecode1.addOpcode(177);
    methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
    paramClassFile.addMethod(methodInfo);
  }
  
  private static void callFind2Methods(Bytecode1 paramBytecode1, String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2, int paramInt3) {
    String str1 = RuntimeSupport.class.getName();
    String str2 = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;[Ljava/lang/reflect/Method;)V";
    paramBytecode1.addAload(paramInt2);
    paramBytecode1.addLdc(paramString1);
    if (paramString2 == null) {
      paramBytecode1.addOpcode(1);
    } else {
      paramBytecode1.addLdc(paramString2);
    } 
    paramBytecode1.addIconst(paramInt1);
    paramBytecode1.addLdc(paramString3);
    paramBytecode1.addAload(paramInt3);
    paramBytecode1.addInvokestatic(str1, "find2Methods", str2);
  }
  
  private static void addSetter(String paramString, ClassFile paramClassFile, ConstPool14 paramConstPool14) throws CannotCompileException {
    MethodInfo methodInfo = new MethodInfo(paramConstPool14, "setHandler", HANDLER_SETTER_TYPE);
    methodInfo.setAccessFlags(1);
    Bytecode1 bytecode1 = new Bytecode1(paramConstPool14, 2, 2);
    bytecode1.addAload(0);
    bytecode1.addAload(1);
    bytecode1.addPutfield(paramString, "handler", HANDLER_TYPE);
    bytecode1.addOpcode(177);
    methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
    paramClassFile.addMethod(methodInfo);
  }
  
  private static void addGetter(String paramString, ClassFile paramClassFile, ConstPool14 paramConstPool14) throws CannotCompileException {
    MethodInfo methodInfo = new MethodInfo(paramConstPool14, "getHandler", HANDLER_GETTER_TYPE);
    methodInfo.setAccessFlags(1);
    Bytecode1 bytecode1 = new Bytecode1(paramConstPool14, 1, 1);
    bytecode1.addAload(0);
    bytecode1.addGetfield(paramString, "handler", HANDLER_TYPE);
    bytecode1.addOpcode(176);
    methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
    paramClassFile.addMethod(methodInfo);
  }
  
  private int overrideMethods(ClassFile paramClassFile, ConstPool14 paramConstPool14, String paramString, ArrayList paramArrayList) throws CannotCompileException {
    String str = makeUniqueName("_d", this.signatureMethods);
    Iterator<Map.Entry> iterator = this.signatureMethods.iterator();
    byte b = 0;
    while (iterator.hasNext()) {
      Map.Entry entry = iterator.next();
      String str1 = (String)entry.getKey();
      Method method = (Method)entry.getValue();
      if ((ClassFile.MAJOR_VERSION < 49 || !isBridge(method)) && 
        testBit(this.signature, b))
        override(paramString, method, str, b, 
            keyToDesc(str1, method), paramClassFile, paramConstPool14, paramArrayList); 
      b++;
    } 
    return b;
  }
  
  private static boolean isBridge(Method paramMethod) {
    return paramMethod.isBridge();
  }
  
  private void override(String paramString1, Method paramMethod, String paramString2, int paramInt, String paramString3, ClassFile paramClassFile, ConstPool14 paramConstPool14, ArrayList paramArrayList) throws CannotCompileException {
    Class<?> clazz = paramMethod.getDeclaringClass();
    String str = paramString2 + paramInt + paramMethod.getName();
    if (Modifier.isAbstract(paramMethod.getModifiers())) {
      str = null;
    } else {
      MethodInfo methodInfo1 = makeDelegator(paramMethod, paramString3, paramConstPool14, clazz, str);
      methodInfo1.setAccessFlags(methodInfo1.getAccessFlags() & 0xFFFFFFBF);
      paramClassFile.addMethod(methodInfo1);
    } 
    MethodInfo methodInfo = makeForwarder(paramString1, paramMethod, paramString3, paramConstPool14, clazz, str, paramInt, paramArrayList);
    paramClassFile.addMethod(methodInfo);
  }
  
  private void makeConstructors(String paramString1, ClassFile paramClassFile, ConstPool14 paramConstPool14, String paramString2) throws CannotCompileException {
    Constructor[] arrayOfConstructor = SecurityActions.getDeclaredConstructors(this.superClass);
    boolean bool = !this.factoryUseCache ? true : false;
    for (byte b = 0; b < arrayOfConstructor.length; b++) {
      Constructor constructor = arrayOfConstructor[b];
      int i = constructor.getModifiers();
      if (!Modifier.isFinal(i) && !Modifier.isPrivate(i) && 
        isVisible(i, this.basename, constructor)) {
        MethodInfo methodInfo = makeConstructor(paramString1, constructor, paramConstPool14, this.superClass, bool);
        paramClassFile.addMethod(methodInfo);
      } 
    } 
  }
  
  private static String makeUniqueName(String paramString, List paramList) {
    if (makeUniqueName0(paramString, paramList.iterator()))
      return paramString; 
    for (byte b = 100; b < 'ϧ'; b++) {
      String str = paramString + b;
      if (makeUniqueName0(str, paramList.iterator()))
        return str; 
    } 
    throw new RuntimeException("cannot make a unique method name");
  }
  
  private static boolean makeUniqueName0(String paramString, Iterator<Map.Entry> paramIterator) {
    while (paramIterator.hasNext()) {
      Map.Entry entry = paramIterator.next();
      String str = (String)entry.getKey();
      if (str.startsWith(paramString))
        return false; 
    } 
    return true;
  }
  
  private static boolean isVisible(int paramInt, String paramString, Member paramMember) {
    if ((paramInt & 0x2) != 0)
      return false; 
    if ((paramInt & 0x5) != 0)
      return true; 
    String str1 = getPackageName(paramString);
    String str2 = getPackageName(paramMember.getDeclaringClass().getName());
    if (str1 == null)
      return (str2 == null); 
    return str1.equals(str2);
  }
  
  private static String getPackageName(String paramString) {
    int i = paramString.lastIndexOf('.');
    if (i < 0)
      return null; 
    return paramString.substring(0, i);
  }
  
  private HashMap getMethods(Class paramClass, Class[] paramArrayOfClass) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    HashSet hashSet = new HashSet();
    for (byte b = 0; b < paramArrayOfClass.length; b++)
      getMethods(hashMap, paramArrayOfClass[b], hashSet); 
    getMethods(hashMap, paramClass, hashSet);
    return hashMap;
  }
  
  private void getMethods(HashMap<String, Method> paramHashMap, Class<?> paramClass, Set<Class<?>> paramSet) {
    if (!paramSet.add(paramClass))
      return; 
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b1 = 0; b1 < arrayOfClass.length; b1++)
      getMethods(paramHashMap, arrayOfClass[b1], paramSet); 
    Class<?> clazz = paramClass.getSuperclass();
    if (clazz != null)
      getMethods(paramHashMap, clazz, paramSet); 
    Method[] arrayOfMethod = SecurityActions.getDeclaredMethods(paramClass);
    for (byte b2 = 0; b2 < arrayOfMethod.length; b2++) {
      if (!Modifier.isPrivate(arrayOfMethod[b2].getModifiers())) {
        Method method1 = arrayOfMethod[b2];
        String str = method1.getName() + ':' + RuntimeSupport.makeDescriptor(method1);
        if (str.startsWith("getHandler:()"))
          this.hasGetHandler = true; 
        Method method2 = paramHashMap.put(str, method1);
        if (null != method2 && isBridge(method1) && 
          !Modifier.isPublic(method2.getDeclaringClass().getModifiers()) && 
          !Modifier.isAbstract(method2.getModifiers()) && !isOverloaded(b2, arrayOfMethod))
          paramHashMap.put(str, method2); 
        if (null != method2 && Modifier.isPublic(method2.getModifiers()) && 
          !Modifier.isPublic(method1.getModifiers()))
          paramHashMap.put(str, method2); 
      } 
    } 
  }
  
  private static boolean isOverloaded(int paramInt, Method[] paramArrayOfMethod) {
    String str = paramArrayOfMethod[paramInt].getName();
    for (byte b = 0; b < paramArrayOfMethod.length; b++) {
      if (b != paramInt && 
        str.equals(paramArrayOfMethod[b].getName()))
        return true; 
    } 
    return false;
  }
  
  private static String keyToDesc(String paramString, Method paramMethod) {
    return paramString.substring(paramString.indexOf(':') + 1);
  }
  
  private static MethodInfo makeConstructor(String paramString, Constructor paramConstructor, ConstPool14 paramConstPool14, Class paramClass, boolean paramBoolean) {
    String str = RuntimeSupport.makeDescriptor(paramConstructor.getParameterTypes(), void.class);
    MethodInfo methodInfo = new MethodInfo(paramConstPool14, "<init>", str);
    methodInfo.setAccessFlags(1);
    setThrows(methodInfo, paramConstPool14, paramConstructor.getExceptionTypes());
    Bytecode1 bytecode1 = new Bytecode1(paramConstPool14, 0, 0);
    if (paramBoolean) {
      bytecode1.addAload(0);
      bytecode1.addGetstatic(paramString, "default_interceptor", HANDLER_TYPE);
      bytecode1.addPutfield(paramString, "handler", HANDLER_TYPE);
      bytecode1.addGetstatic(paramString, "default_interceptor", HANDLER_TYPE);
      bytecode1.addOpcode(199);
      bytecode1.addIndex(10);
    } 
    bytecode1.addAload(0);
    bytecode1.addGetstatic("javassist.util.proxy.RuntimeSupport", "default_interceptor", HANDLER_TYPE);
    bytecode1.addPutfield(paramString, "handler", HANDLER_TYPE);
    int i = bytecode1.currentPc();
    bytecode1.addAload(0);
    int j = addLoadParameters(bytecode1, paramConstructor.getParameterTypes(), 1);
    bytecode1.addInvokespecial(paramClass.getName(), "<init>", str);
    bytecode1.addOpcode(177);
    bytecode1.setMaxLocals(j + 1);
    CodeAttribute codeAttribute = bytecode1.toCodeAttribute();
    methodInfo.setCodeAttribute(codeAttribute);
    StackMapTable.Writer writer = new StackMapTable.Writer(32);
    writer.sameFrame(i);
    codeAttribute.setAttribute(writer.toStackMapTable(paramConstPool14));
    return methodInfo;
  }
  
  private MethodInfo makeDelegator(Method paramMethod, String paramString1, ConstPool14 paramConstPool14, Class paramClass, String paramString2) {
    MethodInfo methodInfo = new MethodInfo(paramConstPool14, paramString2, paramString1);
    methodInfo.setAccessFlags(0x11 | paramMethod
        .getModifiers() & 0xFFFFFAD9);
    setThrows(methodInfo, paramConstPool14, paramMethod);
    Bytecode1 bytecode1 = new Bytecode1(paramConstPool14, 0, 0);
    bytecode1.addAload(0);
    int i = addLoadParameters(bytecode1, paramMethod.getParameterTypes(), 1);
    Class clazz = invokespecialTarget(paramClass);
    bytecode1.addInvokespecial(clazz.isInterface(), paramConstPool14.addClassInfo(clazz.getName()), paramMethod
        .getName(), paramString1);
    addReturn(bytecode1, paramMethod.getReturnType());
    bytecode1.setMaxLocals(++i);
    methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
    return methodInfo;
  }
  
  private Class invokespecialTarget(Class paramClass) {
    if (paramClass.isInterface())
      for (Class<?> clazz : this.interfaces) {
        if (paramClass.isAssignableFrom(clazz))
          return clazz; 
      }  
    return this.superClass;
  }
  
  private static MethodInfo makeForwarder(String paramString1, Method paramMethod, String paramString2, ConstPool14 paramConstPool14, Class paramClass, String paramString3, int paramInt, ArrayList<Find2MethodsArgs> paramArrayList) {
    MethodInfo methodInfo = new MethodInfo(paramConstPool14, paramMethod.getName(), paramString2);
    methodInfo.setAccessFlags(0x10 | paramMethod
        .getModifiers() & 0xFFFFFADF);
    setThrows(methodInfo, paramConstPool14, paramMethod);
    int i = Descriptor.paramSize(paramString2);
    Bytecode1 bytecode1 = new Bytecode1(paramConstPool14, 0, i + 2);
    int j = paramInt * 2;
    int k = paramInt * 2 + 1;
    int m = i + 1;
    bytecode1.addGetstatic(paramString1, "_methods_", "[Ljava/lang/reflect/Method;");
    bytecode1.addAstore(m);
    paramArrayList.add(new Find2MethodsArgs(paramMethod.getName(), paramString3, paramString2, j));
    bytecode1.addAload(0);
    bytecode1.addGetfield(paramString1, "handler", HANDLER_TYPE);
    bytecode1.addAload(0);
    bytecode1.addAload(m);
    bytecode1.addIconst(j);
    bytecode1.addOpcode(50);
    bytecode1.addAload(m);
    bytecode1.addIconst(k);
    bytecode1.addOpcode(50);
    makeParameterList(bytecode1, paramMethod.getParameterTypes());
    bytecode1.addInvokeinterface(MethodHandler.class.getName(), "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", 5);
    Class<?> clazz = paramMethod.getReturnType();
    addUnwrapper(bytecode1, clazz);
    addReturn(bytecode1, clazz);
    CodeAttribute codeAttribute = bytecode1.toCodeAttribute();
    methodInfo.setCodeAttribute(codeAttribute);
    return methodInfo;
  }
  
  static class Find2MethodsArgs {
    String methodName;
    
    String delegatorName;
    
    String descriptor;
    
    int origIndex;
    
    Find2MethodsArgs(String param1String1, String param1String2, String param1String3, int param1Int) {
      this.methodName = param1String1;
      this.delegatorName = param1String2;
      this.descriptor = param1String3;
      this.origIndex = param1Int;
    }
  }
  
  private static void setThrows(MethodInfo paramMethodInfo, ConstPool14 paramConstPool14, Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    setThrows(paramMethodInfo, paramConstPool14, arrayOfClass);
  }
  
  private static void setThrows(MethodInfo paramMethodInfo, ConstPool14 paramConstPool14, Class[] paramArrayOfClass) {
    if (paramArrayOfClass.length == 0)
      return; 
    String[] arrayOfString = new String[paramArrayOfClass.length];
    for (byte b = 0; b < paramArrayOfClass.length; b++)
      arrayOfString[b] = paramArrayOfClass[b].getName(); 
    ExceptionsAttribute exceptionsAttribute = new ExceptionsAttribute(paramConstPool14);
    exceptionsAttribute.setExceptions(arrayOfString);
    paramMethodInfo.setExceptionsAttribute(exceptionsAttribute);
  }
  
  private static int addLoadParameters(Bytecode1 paramBytecode1, Class[] paramArrayOfClass, int paramInt) {
    int i = 0;
    int j = paramArrayOfClass.length;
    for (byte b = 0; b < j; b++)
      i += addLoad(paramBytecode1, i + paramInt, paramArrayOfClass[b]); 
    return i;
  }
  
  private static int addLoad(Bytecode1 paramBytecode1, int paramInt, Class<long> paramClass) {
    if (paramClass.isPrimitive()) {
      if (paramClass == long.class) {
        paramBytecode1.addLload(paramInt);
        return 2;
      } 
      if (paramClass == float.class) {
        paramBytecode1.addFload(paramInt);
      } else {
        if (paramClass == double.class) {
          paramBytecode1.addDload(paramInt);
          return 2;
        } 
        paramBytecode1.addIload(paramInt);
      } 
    } else {
      paramBytecode1.addAload(paramInt);
    } 
    return 1;
  }
  
  private static int addReturn(Bytecode1 paramBytecode1, Class<long> paramClass) {
    if (paramClass.isPrimitive()) {
      if (paramClass == long.class) {
        paramBytecode1.addOpcode(173);
        return 2;
      } 
      if (paramClass == float.class) {
        paramBytecode1.addOpcode(174);
      } else {
        if (paramClass == double.class) {
          paramBytecode1.addOpcode(175);
          return 2;
        } 
        if (paramClass == void.class) {
          paramBytecode1.addOpcode(177);
          return 0;
        } 
        paramBytecode1.addOpcode(172);
      } 
    } else {
      paramBytecode1.addOpcode(176);
    } 
    return 1;
  }
  
  private static void makeParameterList(Bytecode1 paramBytecode1, Class[] paramArrayOfClass) {
    int i = 1;
    int j = paramArrayOfClass.length;
    paramBytecode1.addIconst(j);
    paramBytecode1.addAnewarray("java/lang/Object");
    for (byte b = 0; b < j; b++) {
      paramBytecode1.addOpcode(89);
      paramBytecode1.addIconst(b);
      Class clazz = paramArrayOfClass[b];
      if (clazz.isPrimitive()) {
        i = makeWrapper(paramBytecode1, clazz, i);
      } else {
        paramBytecode1.addAload(i);
        i++;
      } 
      paramBytecode1.addOpcode(83);
    } 
  }
  
  private static int makeWrapper(Bytecode1 paramBytecode1, Class paramClass, int paramInt) {
    int i = FactoryHelper.typeIndex(paramClass);
    String str = FactoryHelper.wrapperTypes[i];
    paramBytecode1.addNew(str);
    paramBytecode1.addOpcode(89);
    addLoad(paramBytecode1, paramInt, paramClass);
    paramBytecode1.addInvokespecial(str, "<init>", FactoryHelper.wrapperDesc[i]);
    return paramInt + FactoryHelper.dataSize[i];
  }
  
  private static void addUnwrapper(Bytecode1 paramBytecode1, Class<void> paramClass) {
    if (paramClass.isPrimitive()) {
      if (paramClass == void.class) {
        paramBytecode1.addOpcode(87);
      } else {
        int i = FactoryHelper.typeIndex(paramClass);
        String str = FactoryHelper.wrapperTypes[i];
        paramBytecode1.addCheckcast(str);
        paramBytecode1.addInvokevirtual(str, FactoryHelper.unwarpMethods[i], FactoryHelper.unwrapDesc[i]);
      } 
    } else {
      paramBytecode1.addCheckcast(paramClass.getName());
    } 
  }
  
  private static MethodInfo makeWriteReplace(ConstPool14 paramConstPool14) {
    MethodInfo methodInfo = new MethodInfo(paramConstPool14, "writeReplace", "()Ljava/lang/Object;");
    String[] arrayOfString = new String[1];
    arrayOfString[0] = "java.io.ObjectStreamException";
    ExceptionsAttribute exceptionsAttribute = new ExceptionsAttribute(paramConstPool14);
    exceptionsAttribute.setExceptions(arrayOfString);
    methodInfo.setExceptionsAttribute(exceptionsAttribute);
    Bytecode1 bytecode1 = new Bytecode1(paramConstPool14, 0, 1);
    bytecode1.addAload(0);
    bytecode1.addInvokestatic("javassist.util.proxy.RuntimeSupport", "makeSerializedProxy", "(Ljava/lang/Object;)Ljavassist/util/proxy/SerializedProxy;");
    bytecode1.addOpcode(176);
    methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
    return methodInfo;
  }
  
  public static interface UniqueName {
    String get(String param1String);
  }
  
  public static interface ClassLoaderProvider {
    ClassLoader get(ProxyFactory param1ProxyFactory);
  }
}
