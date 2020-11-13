package javassist;

import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;

class CtNewWrappedConstructor extends CtNewWrappedMethod {
  private static final int PASS_NONE = 0;
  
  private static final int PASS_PARAMS = 2;
  
  public static CtConstructor wrapped(CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, int paramInt, CtMethod paramCtMethod, CtMethod.ConstParameter paramConstParameter, CtClass paramCtClass) throws CannotCompileException {
    try {
      CtConstructor ctConstructor = new CtConstructor(paramArrayOfCtClass1, paramCtClass);
      ctConstructor.setExceptionTypes(paramArrayOfCtClass2);
      Bytecode1 bytecode1 = makeBody(paramCtClass, paramCtClass.getClassFile2(), paramInt, paramCtMethod, paramArrayOfCtClass1, paramConstParameter);
      ctConstructor.getMethodInfo2().setCodeAttribute(bytecode1.toCodeAttribute());
      return ctConstructor;
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
  }
  
  protected static Bytecode1 makeBody(CtClass paramCtClass, ClassFile paramClassFile, int paramInt, CtMethod paramCtMethod, CtClass[] paramArrayOfCtClass, CtMethod.ConstParameter paramConstParameter) throws CannotCompileException {
    int i, j = paramClassFile.getSuperclassId();
    Bytecode1 bytecode1 = new Bytecode1(paramClassFile.getConstPool(), 0, 0);
    bytecode1.setMaxLocals(false, paramArrayOfCtClass, 0);
    bytecode1.addAload(0);
    if (paramInt == 0) {
      i = 1;
      bytecode1.addInvokespecial(j, "<init>", "()V");
    } else if (paramInt == 2) {
      i = bytecode1.addLoadParameters(paramArrayOfCtClass, 1) + 1;
      bytecode1.addInvokespecial(j, "<init>", 
          Descriptor.ofConstructor(paramArrayOfCtClass));
    } else {
      int k;
      String str;
      i = compileParameterList(bytecode1, paramArrayOfCtClass, 1);
      if (paramConstParameter == null) {
        k = 2;
        str = CtMethod.ConstParameter.defaultConstDescriptor();
      } else {
        k = paramConstParameter.compile(bytecode1) + 2;
        str = paramConstParameter.constDescriptor();
      } 
      if (i < k)
        i = k; 
      bytecode1.addInvokespecial(j, "<init>", str);
    } 
    if (paramCtMethod == null) {
      bytecode1.add(177);
    } else {
      int k = makeBody0(paramCtClass, paramClassFile, paramCtMethod, false, paramArrayOfCtClass, CtClass.voidType, paramConstParameter, bytecode1);
      if (i < k)
        i = k; 
    } 
    bytecode1.setMaxStack(i);
    return bytecode1;
  }
}
