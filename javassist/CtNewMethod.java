package javassist;

import javassist.bytecode.Bytecode1;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

public class CtNewMethod {
  public static CtMethod make(String paramString, CtClass paramCtClass) throws CannotCompileException {
    return make(paramString, paramCtClass, null, null);
  }
  
  public static CtMethod make(String paramString1, CtClass paramCtClass, String paramString2, String paramString3) throws CannotCompileException {
    Javac javac = new Javac(paramCtClass);
    try {
      if (paramString3 != null)
        javac.recordProceed(paramString2, paramString3); 
      CtMember ctMember = javac.compile(paramString1);
      if (ctMember instanceof CtMethod)
        return (CtMethod)ctMember; 
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } 
    throw new CannotCompileException("not a method");
  }
  
  public static CtMethod make(CtClass paramCtClass1, String paramString1, CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, String paramString2, CtClass paramCtClass2) throws CannotCompileException {
    return make(1, paramCtClass1, paramString1, paramArrayOfCtClass1, paramArrayOfCtClass2, paramString2, paramCtClass2);
  }
  
  public static CtMethod make(int paramInt, CtClass paramCtClass1, String paramString1, CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, String paramString2, CtClass paramCtClass2) throws CannotCompileException {
    try {
      CtMethod ctMethod = new CtMethod(paramCtClass1, paramString1, paramArrayOfCtClass1, paramCtClass2);
      ctMethod.setModifiers(paramInt);
      ctMethod.setExceptionTypes(paramArrayOfCtClass2);
      ctMethod.setBody(paramString2);
      return ctMethod;
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
  }
  
  public static CtMethod copy(CtMethod paramCtMethod, CtClass paramCtClass, ClassMap paramClassMap) throws CannotCompileException {
    return new CtMethod(paramCtMethod, paramCtClass, paramClassMap);
  }
  
  public static CtMethod copy(CtMethod paramCtMethod, String paramString, CtClass paramCtClass, ClassMap paramClassMap) throws CannotCompileException {
    CtMethod ctMethod = new CtMethod(paramCtMethod, paramCtClass, paramClassMap);
    ctMethod.setName(paramString);
    return ctMethod;
  }
  
  public static CtMethod abstractMethod(CtClass paramCtClass1, String paramString, CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, CtClass paramCtClass2) throws NotFoundException {
    CtMethod ctMethod = new CtMethod(paramCtClass1, paramString, paramArrayOfCtClass1, paramCtClass2);
    ctMethod.setExceptionTypes(paramArrayOfCtClass2);
    return ctMethod;
  }
  
  public static CtMethod getter(String paramString, CtField paramCtField) throws CannotCompileException {
    FieldInfo fieldInfo = paramCtField.getFieldInfo2();
    String str1 = fieldInfo.getDescriptor();
    String str2 = "()" + str1;
    ConstPool14 constPool14 = fieldInfo.getConstPool();
    MethodInfo methodInfo = new MethodInfo(constPool14, paramString, str2);
    methodInfo.setAccessFlags(1);
    Bytecode1 bytecode1 = new Bytecode1(constPool14, 2, 1);
    try {
      String str = fieldInfo.getName();
      if ((fieldInfo.getAccessFlags() & 0x8) == 0) {
        bytecode1.addAload(0);
        bytecode1.addGetfield(Bytecode1.THIS, str, str1);
      } else {
        bytecode1.addGetstatic(Bytecode1.THIS, str, str1);
      } 
      bytecode1.addReturn(paramCtField.getType());
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
    methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
    CtClass ctClass = paramCtField.getDeclaringClass();
    return new CtMethod(methodInfo, ctClass);
  }
  
  public static CtMethod setter(String paramString, CtField paramCtField) throws CannotCompileException {
    FieldInfo fieldInfo = paramCtField.getFieldInfo2();
    String str1 = fieldInfo.getDescriptor();
    String str2 = "(" + str1 + ")V";
    ConstPool14 constPool14 = fieldInfo.getConstPool();
    MethodInfo methodInfo = new MethodInfo(constPool14, paramString, str2);
    methodInfo.setAccessFlags(1);
    Bytecode1 bytecode1 = new Bytecode1(constPool14, 3, 3);
    try {
      String str = fieldInfo.getName();
      if ((fieldInfo.getAccessFlags() & 0x8) == 0) {
        bytecode1.addAload(0);
        bytecode1.addLoad(1, paramCtField.getType());
        bytecode1.addPutfield(Bytecode1.THIS, str, str1);
      } else {
        bytecode1.addLoad(1, paramCtField.getType());
        bytecode1.addPutstatic(Bytecode1.THIS, str, str1);
      } 
      bytecode1.addReturn(null);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
    methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
    CtClass ctClass = paramCtField.getDeclaringClass();
    return new CtMethod(methodInfo, ctClass);
  }
  
  public static CtMethod delegator(CtMethod paramCtMethod, CtClass paramCtClass) throws CannotCompileException {
    try {
      return delegator0(paramCtMethod, paramCtClass);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
  }
  
  private static CtMethod delegator0(CtMethod paramCtMethod, CtClass paramCtClass) throws CannotCompileException, NotFoundException {
    int i;
    MethodInfo methodInfo1 = paramCtMethod.getMethodInfo2();
    String str1 = methodInfo1.getName();
    String str2 = methodInfo1.getDescriptor();
    ConstPool14 constPool14 = paramCtClass.getClassFile2().getConstPool();
    MethodInfo methodInfo2 = new MethodInfo(constPool14, str1, str2);
    methodInfo2.setAccessFlags(methodInfo1.getAccessFlags());
    ExceptionsAttribute exceptionsAttribute = methodInfo1.getExceptionsAttribute();
    if (exceptionsAttribute != null)
      methodInfo2.setExceptionsAttribute((ExceptionsAttribute)exceptionsAttribute
          .copy(constPool14, null)); 
    Bytecode1 bytecode1 = new Bytecode1(constPool14, 0, 0);
    boolean bool = Modifier.isStatic(paramCtMethod.getModifiers());
    CtClass ctClass = paramCtMethod.getDeclaringClass();
    CtClass[] arrayOfCtClass = paramCtMethod.getParameterTypes();
    if (bool) {
      i = bytecode1.addLoadParameters(arrayOfCtClass, 0);
      bytecode1.addInvokestatic(ctClass, str1, str2);
    } else {
      bytecode1.addLoad(0, ctClass);
      i = bytecode1.addLoadParameters(arrayOfCtClass, 1);
      bytecode1.addInvokespecial(ctClass, str1, str2);
    } 
    bytecode1.addReturn(paramCtMethod.getReturnType());
    bytecode1.setMaxLocals(++i);
    bytecode1.setMaxStack((i < 2) ? 2 : i);
    methodInfo2.setCodeAttribute(bytecode1.toCodeAttribute());
    return new CtMethod(methodInfo2, paramCtClass);
  }
  
  public static CtMethod wrapped(CtClass paramCtClass1, String paramString, CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, CtMethod paramCtMethod, CtMethod.ConstParameter paramConstParameter, CtClass paramCtClass2) throws CannotCompileException {
    return CtNewWrappedMethod.wrapped(paramCtClass1, paramString, paramArrayOfCtClass1, paramArrayOfCtClass2, paramCtMethod, paramConstParameter, paramCtClass2);
  }
}
