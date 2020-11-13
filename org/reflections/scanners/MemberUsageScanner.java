package org.reflections.scanners;

import com.google.common.base.Joiner;
import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.MethodInfo;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;
import org.reflections.ReflectionsException;
import org.reflections.util.ClasspathHelper;

public class MemberUsageScanner extends AbstractScanner {
  private ClassPool classPool;
  
  public void scan(Object paramObject) {
    try {
      CtClass ctClass = getClassPool().get(getMetadataAdapter().getClassName(paramObject));
      for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors())
        scanMember((CtBehavior)ctConstructor); 
      for (CtMethod ctMethod : ctClass.getDeclaredMethods())
        scanMember((CtBehavior)ctMethod); 
      ctClass.detach();
    } catch (Exception exception) {
      throw new ReflectionsException("Could not scan method usage for " + getMetadataAdapter().getClassName(paramObject), exception);
    } 
  }
  
  void scanMember(CtBehavior paramCtBehavior) throws CannotCompileException {
    final String key = paramCtBehavior.getDeclaringClass().getName() + "." + paramCtBehavior.getMethodInfo().getName() + "(" + parameterNames(paramCtBehavior.getMethodInfo()) + ")";
    paramCtBehavior.instrument(new ExprEditor() {
          public void edit(NewExpr param1NewExpr) throws CannotCompileException {
            try {
              MemberUsageScanner.this.put(param1NewExpr.getConstructor().getDeclaringClass().getName() + ".<init>(" + MemberUsageScanner.this
                  .parameterNames(param1NewExpr.getConstructor().getMethodInfo()) + ")", param1NewExpr.getLineNumber(), key);
            } catch (NotFoundException notFoundException) {
              throw new ReflectionsException("Could not find new instance usage in " + key, notFoundException);
            } 
          }
          
          public void edit(MethodCall param1MethodCall) throws CannotCompileException {
            try {
              MemberUsageScanner.this.put(param1MethodCall.getMethod().getDeclaringClass().getName() + "." + param1MethodCall.getMethodName() + "(" + MemberUsageScanner.this
                  .parameterNames(param1MethodCall.getMethod().getMethodInfo()) + ")", param1MethodCall.getLineNumber(), key);
            } catch (NotFoundException notFoundException) {
              throw new ReflectionsException("Could not find member " + param1MethodCall.getClassName() + " in " + key, notFoundException);
            } 
          }
          
          public void edit(ConstructorCall param1ConstructorCall) throws CannotCompileException {
            try {
              MemberUsageScanner.this.put(param1ConstructorCall.getConstructor().getDeclaringClass().getName() + ".<init>(" + MemberUsageScanner.this
                  .parameterNames(param1ConstructorCall.getConstructor().getMethodInfo()) + ")", param1ConstructorCall.getLineNumber(), key);
            } catch (NotFoundException notFoundException) {
              throw new ReflectionsException("Could not find member " + param1ConstructorCall.getClassName() + " in " + key, notFoundException);
            } 
          }
          
          public void edit(FieldAccess param1FieldAccess) throws CannotCompileException {
            try {
              MemberUsageScanner.this.put(param1FieldAccess.getField().getDeclaringClass().getName() + "." + param1FieldAccess.getFieldName(), param1FieldAccess.getLineNumber(), key);
            } catch (NotFoundException notFoundException) {
              throw new ReflectionsException("Could not find member " + param1FieldAccess.getFieldName() + " in " + key, notFoundException);
            } 
          }
        });
  }
  
  private void put(String paramString1, int paramInt, String paramString2) {
    if (acceptResult(paramString1))
      getStore().put(paramString1, paramString2 + " #" + paramInt); 
  }
  
  String parameterNames(MethodInfo paramMethodInfo) {
    return Joiner.on(", ").join(getMetadataAdapter().getParameterNames(paramMethodInfo));
  }
  
  private ClassPool getClassPool() {
    if (this.classPool == null)
      synchronized (this) {
        this.classPool = new ClassPool();
        ClassLoader[] arrayOfClassLoader = getConfiguration().getClassLoaders();
        if (arrayOfClassLoader == null)
          arrayOfClassLoader = ClasspathHelper.classLoaders(new ClassLoader[0]); 
        for (ClassLoader classLoader : arrayOfClassLoader)
          this.classPool.appendClassPath((ClassPath)new LoaderClassPath(classLoader)); 
      }  
    return this.classPool;
  }
}
