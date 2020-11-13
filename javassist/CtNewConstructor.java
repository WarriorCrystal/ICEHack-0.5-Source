package javassist;

import javassist.bytecode.Bytecode1;
import javassist.bytecode.ConstPool14;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

public class CtNewConstructor {
  public static final int PASS_NONE = 0;
  
  public static final int PASS_ARRAY = 1;
  
  public static final int PASS_PARAMS = 2;
  
  public static CtConstructor make(String paramString, CtClass paramCtClass) throws CannotCompileException {
    Javac javac = new Javac(paramCtClass);
    try {
      CtMember ctMember = javac.compile(paramString);
      if (ctMember instanceof CtConstructor)
        return (CtConstructor)ctMember; 
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } 
    throw new CannotCompileException("not a constructor");
  }
  
  public static CtConstructor make(CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, String paramString, CtClass paramCtClass) throws CannotCompileException {
    try {
      CtConstructor ctConstructor = new CtConstructor(paramArrayOfCtClass1, paramCtClass);
      ctConstructor.setExceptionTypes(paramArrayOfCtClass2);
      ctConstructor.setBody(paramString);
      return ctConstructor;
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
  }
  
  public static CtConstructor copy(CtConstructor paramCtConstructor, CtClass paramCtClass, ClassMap paramClassMap) throws CannotCompileException {
    return new CtConstructor(paramCtConstructor, paramCtClass, paramClassMap);
  }
  
  public static CtConstructor defaultConstructor(CtClass paramCtClass) throws CannotCompileException {
    CtConstructor ctConstructor = new CtConstructor((CtClass[])null, paramCtClass);
    ConstPool14 constPool14 = paramCtClass.getClassFile2().getConstPool();
    Bytecode1 bytecode1 = new Bytecode1(constPool14, 1, 1);
    bytecode1.addAload(0);
    try {
      bytecode1.addInvokespecial(paramCtClass.getSuperclass(), "<init>", "()V");
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
    bytecode1.add(177);
    ctConstructor.getMethodInfo2().setCodeAttribute(bytecode1.toCodeAttribute());
    return ctConstructor;
  }
  
  public static CtConstructor skeleton(CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, CtClass paramCtClass) throws CannotCompileException {
    return make(paramArrayOfCtClass1, paramArrayOfCtClass2, 0, null, null, paramCtClass);
  }
  
  public static CtConstructor make(CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, CtClass paramCtClass) throws CannotCompileException {
    return make(paramArrayOfCtClass1, paramArrayOfCtClass2, 2, null, null, paramCtClass);
  }
  
  public static CtConstructor make(CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, int paramInt, CtMethod paramCtMethod, CtMethod.ConstParameter paramConstParameter, CtClass paramCtClass) throws CannotCompileException {
    return CtNewWrappedConstructor.wrapped(paramArrayOfCtClass1, paramArrayOfCtClass2, paramInt, paramCtMethod, paramConstParameter, paramCtClass);
  }
}
