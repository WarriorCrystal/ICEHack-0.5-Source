package javassist.tools.reflect;

import java.util.Iterator;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;

public class Reflection implements Translator {
  static final String classobjectField = "_classobject";
  
  static final String classobjectAccessor = "_getClass";
  
  static final String metaobjectField = "_metaobject";
  
  static final String metaobjectGetter = "_getMetaobject";
  
  static final String metaobjectSetter = "_setMetaobject";
  
  static final String readPrefix = "_r_";
  
  static final String writePrefix = "_w_";
  
  static final String metaobjectClassName = "javassist.tools.reflect.Metaobject";
  
  static final String classMetaobjectClassName = "javassist.tools.reflect.ClassMetaobject";
  
  protected CtMethod trapMethod;
  
  protected CtMethod trapStaticMethod;
  
  protected CtMethod trapRead;
  
  protected CtMethod trapWrite;
  
  protected CtClass[] readParam;
  
  protected ClassPool classPool;
  
  protected CodeConverter converter;
  
  private boolean isExcluded(String paramString) {
    return (paramString.startsWith("_m_") || paramString
      .equals("_getClass") || paramString
      .equals("_setMetaobject") || paramString
      .equals("_getMetaobject") || paramString
      .startsWith("_r_") || paramString
      .startsWith("_w_"));
  }
  
  public Reflection() {
    this.classPool = null;
    this.converter = new CodeConverter();
  }
  
  public void start(ClassPool paramClassPool) throws NotFoundException {
    this.classPool = paramClassPool;
    String str = "javassist.tools.reflect.Sample is not found or broken.";
    try {
      CtClass ctClass = this.classPool.get("javassist.tools.reflect.Sample");
      rebuildClassFile(ctClass.getClassFile());
      this.trapMethod = ctClass.getDeclaredMethod("trap");
      this.trapStaticMethod = ctClass.getDeclaredMethod("trapStatic");
      this.trapRead = ctClass.getDeclaredMethod("trapRead");
      this.trapWrite = ctClass.getDeclaredMethod("trapWrite");
      this
        .readParam = new CtClass[] { this.classPool.get("java.lang.Object") };
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException("javassist.tools.reflect.Sample is not found or broken.");
    } catch (BadBytecode badBytecode) {
      throw new RuntimeException("javassist.tools.reflect.Sample is not found or broken.");
    } 
  }
  
  public void onLoad(ClassPool paramClassPool, String paramString) throws CannotCompileException, NotFoundException {
    CtClass ctClass = paramClassPool.get(paramString);
    ctClass.instrument(this.converter);
  }
  
  public boolean makeReflective(String paramString1, String paramString2, String paramString3) throws CannotCompileException, NotFoundException {
    return makeReflective(this.classPool.get(paramString1), this.classPool
        .get(paramString2), this.classPool
        .get(paramString3));
  }
  
  public boolean makeReflective(Class paramClass1, Class paramClass2, Class paramClass3) throws CannotCompileException, NotFoundException {
    return makeReflective(paramClass1.getName(), paramClass2.getName(), paramClass3
        .getName());
  }
  
  public boolean makeReflective(CtClass paramCtClass1, CtClass paramCtClass2, CtClass paramCtClass3) throws CannotCompileException, CannotReflectException, NotFoundException {
    if (paramCtClass1.isInterface())
      throw new CannotReflectException("Cannot reflect an interface: " + paramCtClass1
          .getName()); 
    if (paramCtClass1.subclassOf(this.classPool.get("javassist.tools.reflect.ClassMetaobject")))
      throw new CannotReflectException("Cannot reflect a subclass of ClassMetaobject: " + paramCtClass1
          
          .getName()); 
    if (paramCtClass1.subclassOf(this.classPool.get("javassist.tools.reflect.Metaobject")))
      throw new CannotReflectException("Cannot reflect a subclass of Metaobject: " + paramCtClass1
          
          .getName()); 
    registerReflectiveClass(paramCtClass1);
    return modifyClassfile(paramCtClass1, paramCtClass2, paramCtClass3);
  }
  
  private void registerReflectiveClass(CtClass paramCtClass) {
    CtField[] arrayOfCtField = paramCtClass.getDeclaredFields();
    for (byte b = 0; b < arrayOfCtField.length; b++) {
      CtField ctField = arrayOfCtField[b];
      int i = ctField.getModifiers();
      if ((i & 0x1) != 0 && (i & 0x10) == 0) {
        String str = ctField.getName();
        this.converter.replaceFieldRead(ctField, paramCtClass, "_r_" + str);
        this.converter.replaceFieldWrite(ctField, paramCtClass, "_w_" + str);
      } 
    } 
  }
  
  private boolean modifyClassfile(CtClass paramCtClass1, CtClass paramCtClass2, CtClass paramCtClass3) throws CannotCompileException, NotFoundException {
    if (paramCtClass1.getAttribute("Reflective") != null)
      return false; 
    paramCtClass1.setAttribute("Reflective", new byte[0]);
    CtClass ctClass = this.classPool.get("javassist.tools.reflect.Metalevel");
    boolean bool = !paramCtClass1.subtypeOf(ctClass) ? true : false;
    if (bool)
      paramCtClass1.addInterface(ctClass); 
    processMethods(paramCtClass1, bool);
    processFields(paramCtClass1);
    if (bool) {
      CtField ctField1 = new CtField(this.classPool.get("javassist.tools.reflect.Metaobject"), "_metaobject", paramCtClass1);
      ctField1.setModifiers(4);
      paramCtClass1.addField(ctField1, CtField.Initializer.byNewWithParams(paramCtClass2));
      paramCtClass1.addMethod(CtNewMethod.getter("_getMetaobject", ctField1));
      paramCtClass1.addMethod(CtNewMethod.setter("_setMetaobject", ctField1));
    } 
    CtField ctField = new CtField(this.classPool.get("javassist.tools.reflect.ClassMetaobject"), "_classobject", paramCtClass1);
    ctField.setModifiers(10);
    paramCtClass1.addField(ctField, CtField.Initializer.byNew(paramCtClass3, new String[] { paramCtClass1
            .getName() }));
    paramCtClass1.addMethod(CtNewMethod.getter("_getClass", ctField));
    return true;
  }
  
  private void processMethods(CtClass paramCtClass, boolean paramBoolean) throws CannotCompileException, NotFoundException {
    CtMethod[] arrayOfCtMethod = paramCtClass.getMethods();
    for (byte b = 0; b < arrayOfCtMethod.length; b++) {
      CtMethod ctMethod = arrayOfCtMethod[b];
      int i = ctMethod.getModifiers();
      if (Modifier.isPublic(i) && !Modifier.isAbstract(i))
        processMethods0(i, paramCtClass, ctMethod, b, paramBoolean); 
    } 
  }
  
  private void processMethods0(int paramInt1, CtClass paramCtClass, CtMethod paramCtMethod, int paramInt2, boolean paramBoolean) throws CannotCompileException, NotFoundException {
    CtMethod ctMethod1, ctMethod2;
    String str = paramCtMethod.getName();
    if (isExcluded(str))
      return; 
    if (paramCtMethod.getDeclaringClass() == paramCtClass) {
      if (Modifier.isNative(paramInt1))
        return; 
      ctMethod2 = paramCtMethod;
      if (Modifier.isFinal(paramInt1)) {
        paramInt1 &= 0xFFFFFFEF;
        ctMethod2.setModifiers(paramInt1);
      } 
    } else {
      if (Modifier.isFinal(paramInt1))
        return; 
      paramInt1 &= 0xFFFFFEFF;
      ctMethod2 = CtNewMethod.delegator(findOriginal(paramCtMethod, paramBoolean), paramCtClass);
      ctMethod2.setModifiers(paramInt1);
      paramCtClass.addMethod(ctMethod2);
    } 
    ctMethod2.setName("_m_" + paramInt2 + "_" + str);
    if (Modifier.isStatic(paramInt1)) {
      ctMethod1 = this.trapStaticMethod;
    } else {
      ctMethod1 = this.trapMethod;
    } 
    CtMethod ctMethod3 = CtNewMethod.wrapped(paramCtMethod.getReturnType(), str, paramCtMethod
        .getParameterTypes(), paramCtMethod.getExceptionTypes(), ctMethod1, 
        CtMethod.ConstParameter.integer(paramInt2), paramCtClass);
    ctMethod3.setModifiers(paramInt1);
    paramCtClass.addMethod(ctMethod3);
  }
  
  private CtMethod findOriginal(CtMethod paramCtMethod, boolean paramBoolean) throws NotFoundException {
    if (paramBoolean)
      return paramCtMethod; 
    String str = paramCtMethod.getName();
    CtMethod[] arrayOfCtMethod = paramCtMethod.getDeclaringClass().getDeclaredMethods();
    for (byte b = 0; b < arrayOfCtMethod.length; b++) {
      String str1 = arrayOfCtMethod[b].getName();
      if (str1.endsWith(str) && str1
        .startsWith("_m_") && arrayOfCtMethod[b]
        .getSignature().equals(paramCtMethod.getSignature()))
        return arrayOfCtMethod[b]; 
    } 
    return paramCtMethod;
  }
  
  private void processFields(CtClass paramCtClass) throws CannotCompileException, NotFoundException {
    CtField[] arrayOfCtField = paramCtClass.getDeclaredFields();
    for (byte b = 0; b < arrayOfCtField.length; b++) {
      CtField ctField = arrayOfCtField[b];
      int i = ctField.getModifiers();
      if ((i & 0x1) != 0 && (i & 0x10) == 0) {
        i |= 0x8;
        String str = ctField.getName();
        CtClass ctClass = ctField.getType();
        CtMethod ctMethod = CtNewMethod.wrapped(ctClass, "_r_" + str, this.readParam, null, this.trapRead, 
            
            CtMethod.ConstParameter.string(str), paramCtClass);
        ctMethod.setModifiers(i);
        paramCtClass.addMethod(ctMethod);
        CtClass[] arrayOfCtClass = new CtClass[2];
        arrayOfCtClass[0] = this.classPool.get("java.lang.Object");
        arrayOfCtClass[1] = ctClass;
        ctMethod = CtNewMethod.wrapped(CtClass.voidType, "_w_" + str, arrayOfCtClass, null, this.trapWrite, 
            
            CtMethod.ConstParameter.string(str), paramCtClass);
        ctMethod.setModifiers(i);
        paramCtClass.addMethod(ctMethod);
      } 
    } 
  }
  
  public void rebuildClassFile(ClassFile paramClassFile) throws BadBytecode {
    if (ClassFile.MAJOR_VERSION < 50)
      return; 
    Iterator<MethodInfo> iterator = paramClassFile.getMethods().iterator();
    while (iterator.hasNext()) {
      MethodInfo methodInfo = iterator.next();
      methodInfo.rebuildStackMap(this.classPool);
    } 
  }
}
