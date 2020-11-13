package javassist;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;

public final class CtMethod extends CtBehavior {
  protected String cachedStringRep;
  
  CtMethod(MethodInfo paramMethodInfo, CtClass paramCtClass) {
    super(paramCtClass, paramMethodInfo);
    this.cachedStringRep = null;
  }
  
  public CtMethod(CtClass paramCtClass1, String paramString, CtClass[] paramArrayOfCtClass, CtClass paramCtClass2) {
    this((MethodInfo)null, paramCtClass2);
    ConstPool14 constPool14 = paramCtClass2.getClassFile2().getConstPool();
    String str = Descriptor.ofMethod(paramCtClass1, paramArrayOfCtClass);
    this.methodInfo = new MethodInfo(constPool14, paramString, str);
    setModifiers(1025);
  }
  
  public CtMethod(CtMethod paramCtMethod, CtClass paramCtClass, ClassMap paramClassMap) throws CannotCompileException {
    this((MethodInfo)null, paramCtClass);
    copy(paramCtMethod, false, paramClassMap);
  }
  
  public static CtMethod make(String paramString, CtClass paramCtClass) throws CannotCompileException {
    return CtNewMethod.make(paramString, paramCtClass);
  }
  
  public static CtMethod make(MethodInfo paramMethodInfo, CtClass paramCtClass) throws CannotCompileException {
    if (paramCtClass.getClassFile2().getConstPool() != paramMethodInfo.getConstPool())
      throw new CannotCompileException("bad declaring class"); 
    return new CtMethod(paramMethodInfo, paramCtClass);
  }
  
  public int hashCode() {
    return getStringRep().hashCode();
  }
  
  void nameReplaced() {
    this.cachedStringRep = null;
  }
  
  final String getStringRep() {
    if (this.cachedStringRep == null)
      this
        .cachedStringRep = this.methodInfo.getName() + Descriptor.getParamDescriptor(this.methodInfo.getDescriptor()); 
    return this.cachedStringRep;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject != null && paramObject instanceof CtMethod && ((CtMethod)paramObject)
      .getStringRep().equals(getStringRep()));
  }
  
  public String getLongName() {
    return getDeclaringClass().getName() + "." + 
      getName() + Descriptor.toString(getSignature());
  }
  
  public String getName() {
    return this.methodInfo.getName();
  }
  
  public void setName(String paramString) {
    this.declaringClass.checkModify();
    this.methodInfo.setName(paramString);
  }
  
  public CtClass getReturnType() throws NotFoundException {
    return getReturnType0();
  }
  
  public boolean isEmpty() {
    CodeAttribute codeAttribute = getMethodInfo2().getCodeAttribute();
    if (codeAttribute == null)
      return ((getModifiers() & 0x400) != 0); 
    CodeIterator codeIterator = codeAttribute.iterator();
    try {
      return (codeIterator.hasNext() && codeIterator.byteAt(codeIterator.next()) == 177 && 
        !codeIterator.hasNext());
    } catch (BadBytecode badBytecode) {
      return false;
    } 
  }
  
  public void setBody(CtMethod paramCtMethod, ClassMap paramClassMap) throws CannotCompileException {
    setBody0(paramCtMethod.declaringClass, paramCtMethod.methodInfo, this.declaringClass, this.methodInfo, paramClassMap);
  }
  
  public void setWrappedBody(CtMethod paramCtMethod, ConstParameter paramConstParameter) throws CannotCompileException {
    CtClass arrayOfCtClass[], ctClass2;
    this.declaringClass.checkModify();
    CtClass ctClass1 = getDeclaringClass();
    try {
      arrayOfCtClass = getParameterTypes();
      ctClass2 = getReturnType();
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
    Bytecode1 bytecode1 = CtNewWrappedMethod.makeBody(ctClass1, ctClass1
        .getClassFile2(), paramCtMethod, arrayOfCtClass, ctClass2, paramConstParameter);
    CodeAttribute codeAttribute = bytecode1.toCodeAttribute();
    this.methodInfo.setCodeAttribute(codeAttribute);
    this.methodInfo.setAccessFlags(this.methodInfo.getAccessFlags() & 0xFFFFFBFF);
  }
  
  public static class ConstParameter {
    public static ConstParameter integer(int param1Int) {
      return new CtMethod.IntConstParameter(param1Int);
    }
    
    public static ConstParameter integer(long param1Long) {
      return new CtMethod.LongConstParameter(param1Long);
    }
    
    public static ConstParameter string(String param1String) {
      return new CtMethod.StringConstParameter(param1String);
    }
    
    int compile(Bytecode1 param1Bytecode1) throws CannotCompileException {
      return 0;
    }
    
    String descriptor() {
      return defaultDescriptor();
    }
    
    static String defaultDescriptor() {
      return "([Ljava/lang/Object;)Ljava/lang/Object;";
    }
    
    String constDescriptor() {
      return defaultConstDescriptor();
    }
    
    static String defaultConstDescriptor() {
      return "([Ljava/lang/Object;)V";
    }
  }
  
  static class IntConstParameter extends ConstParameter {
    int param;
    
    IntConstParameter(int param1Int) {
      this.param = param1Int;
    }
    
    int compile(Bytecode1 param1Bytecode1) throws CannotCompileException {
      param1Bytecode1.addIconst(this.param);
      return 1;
    }
    
    String descriptor() {
      return "([Ljava/lang/Object;I)Ljava/lang/Object;";
    }
    
    String constDescriptor() {
      return "([Ljava/lang/Object;I)V";
    }
  }
  
  static class LongConstParameter extends ConstParameter {
    long param;
    
    LongConstParameter(long param1Long) {
      this.param = param1Long;
    }
    
    int compile(Bytecode1 param1Bytecode1) throws CannotCompileException {
      param1Bytecode1.addLconst(this.param);
      return 2;
    }
    
    String descriptor() {
      return "([Ljava/lang/Object;J)Ljava/lang/Object;";
    }
    
    String constDescriptor() {
      return "([Ljava/lang/Object;J)V";
    }
  }
  
  static class StringConstParameter extends ConstParameter {
    String param;
    
    StringConstParameter(String param1String) {
      this.param = param1String;
    }
    
    int compile(Bytecode1 param1Bytecode1) throws CannotCompileException {
      param1Bytecode1.addLdc(this.param);
      return 1;
    }
    
    String descriptor() {
      return "([Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;";
    }
    
    String constDescriptor() {
      return "([Ljava/lang/Object;Ljava/lang/String;)V";
    }
  }
}
