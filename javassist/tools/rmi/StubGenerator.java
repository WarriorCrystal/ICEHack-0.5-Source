package javassist.tools.rmi;

import java.lang.reflect.Method;
import java.util.Hashtable;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.Translator;

public class StubGenerator implements Translator {
  private static final String fieldImporter = "importer";
  
  private static final String fieldObjectId = "objectId";
  
  private static final String accessorObjectId = "_getObjectId";
  
  private static final String sampleClass = "javassist.tools.rmi.Sample";
  
  private ClassPool classPool;
  
  private Hashtable proxyClasses = new Hashtable<Object, Object>();
  
  private CtMethod forwardMethod;
  
  private CtMethod forwardStaticMethod;
  
  private CtClass[] proxyConstructorParamTypes;
  
  private CtClass[] interfacesForProxy;
  
  private CtClass[] exceptionForProxy;
  
  public void start(ClassPool paramClassPool) throws NotFoundException {
    this.classPool = paramClassPool;
    CtClass ctClass = paramClassPool.get("javassist.tools.rmi.Sample");
    this.forwardMethod = ctClass.getDeclaredMethod("forward");
    this.forwardStaticMethod = ctClass.getDeclaredMethod("forwardStatic");
    this
      .proxyConstructorParamTypes = paramClassPool.get(new String[] { "javassist.tools.rmi.ObjectImporter", "int" });
    this
      .interfacesForProxy = paramClassPool.get(new String[] { "java.io.Serializable", "javassist.tools.rmi.Proxy" });
    this
      .exceptionForProxy = new CtClass[] { paramClassPool.get("javassist.tools.rmi.RemoteException") };
  }
  
  public void onLoad(ClassPool paramClassPool, String paramString) {}
  
  public boolean isProxyClass(String paramString) {
    return (this.proxyClasses.get(paramString) != null);
  }
  
  public synchronized boolean makeProxyClass(Class paramClass) throws CannotCompileException, NotFoundException {
    String str = paramClass.getName();
    if (this.proxyClasses.get(str) != null)
      return false; 
    CtClass ctClass = produceProxyClass(this.classPool.get(str), paramClass);
    this.proxyClasses.put(str, ctClass);
    modifySuperclass(ctClass);
    return true;
  }
  
  private CtClass produceProxyClass(CtClass paramCtClass, Class paramClass) throws CannotCompileException, NotFoundException {
    int i = paramCtClass.getModifiers();
    if (Modifier.isAbstract(i) || Modifier.isNative(i) || 
      !Modifier.isPublic(i))
      throw new CannotCompileException(paramCtClass.getName() + " must be public, non-native, and non-abstract."); 
    CtClass ctClass = this.classPool.makeClass(paramCtClass.getName(), paramCtClass
        .getSuperclass());
    ctClass.setInterfaces(this.interfacesForProxy);
    CtField ctField = new CtField(this.classPool.get("javassist.tools.rmi.ObjectImporter"), "importer", ctClass);
    ctField.setModifiers(2);
    ctClass.addField(ctField, CtField.Initializer.byParameter(0));
    ctField = new CtField(CtClass.intType, "objectId", ctClass);
    ctField.setModifiers(2);
    ctClass.addField(ctField, CtField.Initializer.byParameter(1));
    ctClass.addMethod(CtNewMethod.getter("_getObjectId", ctField));
    ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
    CtConstructor ctConstructor = CtNewConstructor.skeleton(this.proxyConstructorParamTypes, null, ctClass);
    ctClass.addConstructor(ctConstructor);
    try {
      addMethods(ctClass, paramClass.getMethods());
      return ctClass;
    } catch (SecurityException securityException) {
      throw new CannotCompileException(securityException);
    } 
  }
  
  private CtClass toCtClass(Class<?> paramClass) throws NotFoundException {
    String str;
    if (!paramClass.isArray()) {
      str = paramClass.getName();
    } else {
      StringBuffer stringBuffer = new StringBuffer();
      while (true) {
        stringBuffer.append("[]");
        paramClass = paramClass.getComponentType();
        if (!paramClass.isArray()) {
          stringBuffer.insert(0, paramClass.getName());
          str = stringBuffer.toString();
          return this.classPool.get(str);
        } 
      } 
    } 
    return this.classPool.get(str);
  }
  
  private CtClass[] toCtClass(Class[] paramArrayOfClass) throws NotFoundException {
    int i = paramArrayOfClass.length;
    CtClass[] arrayOfCtClass = new CtClass[i];
    for (byte b = 0; b < i; b++)
      arrayOfCtClass[b] = toCtClass(paramArrayOfClass[b]); 
    return arrayOfCtClass;
  }
  
  private void addMethods(CtClass paramCtClass, Method[] paramArrayOfMethod) throws CannotCompileException, NotFoundException {
    for (byte b = 0; b < paramArrayOfMethod.length; b++) {
      Method method = paramArrayOfMethod[b];
      int i = method.getModifiers();
      if (method.getDeclaringClass() != Object.class && 
        !Modifier.isFinal(i))
        if (Modifier.isPublic(i)) {
          CtMethod ctMethod2;
          if (Modifier.isStatic(i)) {
            ctMethod2 = this.forwardStaticMethod;
          } else {
            ctMethod2 = this.forwardMethod;
          } 
          CtMethod ctMethod1 = CtNewMethod.wrapped(toCtClass(method.getReturnType()), method
              .getName(), 
              toCtClass(method.getParameterTypes()), this.exceptionForProxy, ctMethod2, 
              
              CtMethod.ConstParameter.integer(b), paramCtClass);
          ctMethod1.setModifiers(i);
          paramCtClass.addMethod(ctMethod1);
        } else if (!Modifier.isProtected(i) && 
          !Modifier.isPrivate(i)) {
          throw new CannotCompileException("the methods must be public, protected, or private.");
        }  
    } 
  }
  
  private void modifySuperclass(CtClass paramCtClass) throws CannotCompileException, NotFoundException {
    for (;; paramCtClass = ctClass) {
      CtClass ctClass = paramCtClass.getSuperclass();
      if (ctClass == null)
        break; 
      try {
        ctClass.getDeclaredConstructor(null);
        break;
      } catch (NotFoundException notFoundException) {
        ctClass.addConstructor(
            CtNewConstructor.defaultConstructor(ctClass));
      } 
    } 
  }
}
