package javassist;

import java.util.Hashtable;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SyntheticAttribute;
import javassist.compiler.JvstCodeGen;

class CtNewWrappedMethod {
  private static final String addedWrappedMethod = "_added_m$";
  
  public static CtMethod wrapped(CtClass paramCtClass1, String paramString, CtClass[] paramArrayOfCtClass1, CtClass[] paramArrayOfCtClass2, CtMethod paramCtMethod, CtMethod.ConstParameter paramConstParameter, CtClass paramCtClass2) throws CannotCompileException {
    CtMethod ctMethod = new CtMethod(paramCtClass1, paramString, paramArrayOfCtClass1, paramCtClass2);
    ctMethod.setModifiers(paramCtMethod.getModifiers());
    try {
      ctMethod.setExceptionTypes(paramArrayOfCtClass2);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
    Bytecode1 bytecode1 = makeBody(paramCtClass2, paramCtClass2.getClassFile2(), paramCtMethod, paramArrayOfCtClass1, paramCtClass1, paramConstParameter);
    MethodInfo methodInfo = ctMethod.getMethodInfo2();
    methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
    return ctMethod;
  }
  
  static Bytecode1 makeBody(CtClass paramCtClass1, ClassFile paramClassFile, CtMethod paramCtMethod, CtClass[] paramArrayOfCtClass, CtClass paramCtClass2, CtMethod.ConstParameter paramConstParameter) throws CannotCompileException {
    boolean bool = Modifier.isStatic(paramCtMethod.getModifiers());
    Bytecode1 bytecode1 = new Bytecode1(paramClassFile.getConstPool(), 0, 0);
    int i = makeBody0(paramCtClass1, paramClassFile, paramCtMethod, bool, paramArrayOfCtClass, paramCtClass2, paramConstParameter, bytecode1);
    bytecode1.setMaxStack(i);
    bytecode1.setMaxLocals(bool, paramArrayOfCtClass, 0);
    return bytecode1;
  }
  
  protected static int makeBody0(CtClass paramCtClass1, ClassFile paramClassFile, CtMethod paramCtMethod, boolean paramBoolean, CtClass[] paramArrayOfCtClass, CtClass paramCtClass2, CtMethod.ConstParameter paramConstParameter, Bytecode1 paramBytecode1) throws CannotCompileException {
    int j;
    String str1, str2;
    if (!(paramCtClass1 instanceof CtClassType1))
      throw new CannotCompileException("bad declaring class" + paramCtClass1
          .getName()); 
    if (!paramBoolean)
      paramBytecode1.addAload(0); 
    int i = compileParameterList(paramBytecode1, paramArrayOfCtClass, paramBoolean ? 0 : 1);
    if (paramConstParameter == null) {
      j = 0;
      str1 = CtMethod.ConstParameter.defaultDescriptor();
    } else {
      j = paramConstParameter.compile(paramBytecode1);
      str1 = paramConstParameter.descriptor();
    } 
    checkSignature(paramCtMethod, str1);
    try {
      str2 = addBodyMethod((CtClassType1)paramCtClass1, paramClassFile, paramCtMethod);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
    if (paramBoolean) {
      paramBytecode1.addInvokestatic(Bytecode1.THIS, str2, str1);
    } else {
      paramBytecode1.addInvokespecial(Bytecode1.THIS, str2, str1);
    } 
    compileReturn(paramBytecode1, paramCtClass2);
    if (i < j + 2)
      i = j + 2; 
    return i;
  }
  
  private static void checkSignature(CtMethod paramCtMethod, String paramString) throws CannotCompileException {
    if (!paramString.equals(paramCtMethod.getMethodInfo2().getDescriptor()))
      throw new CannotCompileException("wrapped method with a bad signature: " + paramCtMethod
          
          .getDeclaringClass().getName() + '.' + paramCtMethod
          .getName()); 
  }
  
  private static String addBodyMethod(CtClassType1 paramCtClassType1, ClassFile paramClassFile, CtMethod paramCtMethod) throws BadBytecode, CannotCompileException {
    Hashtable<CtMethod, String> hashtable = paramCtClassType1.getHiddenMethods();
    String str = (String)hashtable.get(paramCtMethod);
    if (str == null)
      while (true) {
        str = "_added_m$" + paramCtClassType1.getUniqueNumber();
        if (paramClassFile.getMethod(str) == null) {
          ClassMap classMap = new ClassMap();
          classMap.put(paramCtMethod.getDeclaringClass().getName(), paramCtClassType1.getName());
          MethodInfo methodInfo = new MethodInfo(paramClassFile.getConstPool(), str, paramCtMethod.getMethodInfo2(), classMap);
          int i = methodInfo.getAccessFlags();
          methodInfo.setAccessFlags(AccessFlag.setPrivate(i));
          methodInfo.addAttribute((AttributeInfo)new SyntheticAttribute(paramClassFile.getConstPool()));
          paramClassFile.addMethod(methodInfo);
          hashtable.put(paramCtMethod, str);
          CtMember.Cache cache = paramCtClassType1.hasMemberCache();
          if (cache != null)
            cache.addMethod(new CtMethod(methodInfo, paramCtClassType1)); 
          break;
        } 
      }  
    return str;
  }
  
  static int compileParameterList(Bytecode1 paramBytecode1, CtClass[] paramArrayOfCtClass, int paramInt) {
    return JvstCodeGen.compileParameterList(paramBytecode1, paramArrayOfCtClass, paramInt);
  }
  
  private static void compileReturn(Bytecode1 paramBytecode1, CtClass paramCtClass) {
    if (paramCtClass.isPrimitive()) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramCtClass;
      if (ctPrimitiveType != CtClass.voidType) {
        String str = ctPrimitiveType.getWrapperName();
        paramBytecode1.addCheckcast(str);
        paramBytecode1.addInvokevirtual(str, ctPrimitiveType.getGetMethodName(), ctPrimitiveType
            .getGetMethodDescriptor());
      } 
      paramBytecode1.addOpcode(ctPrimitiveType.getReturnOp());
    } else {
      paramBytecode1.addCheckcast(paramCtClass);
      paramBytecode1.addOpcode(176);
    } 
  }
}
