package javassist;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

public final class CtConstructor extends CtBehavior {
  protected CtConstructor(MethodInfo paramMethodInfo, CtClass paramCtClass) {
    super(paramCtClass, paramMethodInfo);
  }
  
  public CtConstructor(CtClass[] paramArrayOfCtClass, CtClass paramCtClass) {
    this((MethodInfo)null, paramCtClass);
    ConstPool14 constPool14 = paramCtClass.getClassFile2().getConstPool();
    String str = Descriptor.ofConstructor(paramArrayOfCtClass);
    this.methodInfo = new MethodInfo(constPool14, "<init>", str);
    setModifiers(1);
  }
  
  public CtConstructor(CtConstructor paramCtConstructor, CtClass paramCtClass, ClassMap paramClassMap) throws CannotCompileException {
    this((MethodInfo)null, paramCtClass);
    copy(paramCtConstructor, true, paramClassMap);
  }
  
  public boolean isConstructor() {
    return this.methodInfo.isConstructor();
  }
  
  public boolean isClassInitializer() {
    return this.methodInfo.isStaticInitializer();
  }
  
  public String getLongName() {
    return getDeclaringClass().getName() + (
      isConstructor() ? Descriptor.toString(getSignature()) : ".<clinit>()");
  }
  
  public String getName() {
    if (this.methodInfo.isStaticInitializer())
      return "<clinit>"; 
    return this.declaringClass.getSimpleName();
  }
  
  public boolean isEmpty() {
    CodeAttribute codeAttribute = getMethodInfo2().getCodeAttribute();
    if (codeAttribute == null)
      return false; 
    ConstPool14 constPool14 = codeAttribute.getConstPool();
    CodeIterator codeIterator = codeAttribute.iterator();
    try {
      int i = codeIterator.byteAt(codeIterator.next());
      if (i != 177) {
        int j;
        if (i == 42 && codeIterator
          
          .byteAt(j = codeIterator.next()) == 183) {
          int k;
          if ((k = constPool14.isConstructor(getSuperclassName(), codeIterator
              .u16bitAt(j + 1))) != 0 && "()V"
            .equals(constPool14.getUtf8Info(k)) && codeIterator
            .byteAt(codeIterator.next()) == 177 && 
            !codeIterator.hasNext());
        } 
        return false;
      } 
    } catch (BadBytecode badBytecode) {
      return false;
    } 
  }
  
  private String getSuperclassName() {
    ClassFile classFile = this.declaringClass.getClassFile2();
    return classFile.getSuperclass();
  }
  
  public boolean callsSuper() throws CannotCompileException {
    CodeAttribute codeAttribute = this.methodInfo.getCodeAttribute();
    if (codeAttribute != null) {
      CodeIterator codeIterator = codeAttribute.iterator();
      try {
        int i = codeIterator.skipSuperConstructor();
        return (i >= 0);
      } catch (BadBytecode badBytecode) {
        throw new CannotCompileException(badBytecode);
      } 
    } 
    return false;
  }
  
  public void setBody(String paramString) throws CannotCompileException {
    if (paramString == null)
      if (isClassInitializer()) {
        paramString = ";";
      } else {
        paramString = "super();";
      }  
    super.setBody(paramString);
  }
  
  public void setBody(CtConstructor paramCtConstructor, ClassMap paramClassMap) throws CannotCompileException {
    setBody0(paramCtConstructor.declaringClass, paramCtConstructor.methodInfo, this.declaringClass, this.methodInfo, paramClassMap);
  }
  
  public void insertBeforeBody(String paramString) throws CannotCompileException {
    CtClass ctClass = this.declaringClass;
    ctClass.checkModify();
    if (isClassInitializer())
      throw new CannotCompileException("class initializer"); 
    CodeAttribute codeAttribute = this.methodInfo.getCodeAttribute();
    CodeIterator codeIterator = codeAttribute.iterator();
    Bytecode1 bytecode1 = new Bytecode1(this.methodInfo.getConstPool(), codeAttribute.getMaxStack(), codeAttribute.getMaxLocals());
    bytecode1.setStackDepth(codeAttribute.getMaxStack());
    Javac javac = new Javac(bytecode1, ctClass);
    try {
      javac.recordParams(getParameterTypes(), false);
      javac.compileStmnt(paramString);
      codeAttribute.setMaxStack(bytecode1.getMaxStack());
      codeAttribute.setMaxLocals(bytecode1.getMaxLocals());
      codeIterator.skipConstructor();
      int i = codeIterator.insertEx(bytecode1.get());
      codeIterator.insert(bytecode1.getExceptionTable(), i);
      this.methodInfo.rebuildStackMapIf6(ctClass.getClassPool(), ctClass.getClassFile2());
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  int getStartPosOfBody(CodeAttribute paramCodeAttribute) throws CannotCompileException {
    CodeIterator codeIterator = paramCodeAttribute.iterator();
    try {
      codeIterator.skipConstructor();
      return codeIterator.next();
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  public CtMethod toMethod(String paramString, CtClass paramCtClass) throws CannotCompileException {
    return toMethod(paramString, paramCtClass, (ClassMap)null);
  }
  
  public CtMethod toMethod(String paramString, CtClass paramCtClass, ClassMap paramClassMap) throws CannotCompileException {
    CtMethod ctMethod = new CtMethod(null, paramCtClass);
    ctMethod.copy(this, false, paramClassMap);
    if (isConstructor()) {
      MethodInfo methodInfo = ctMethod.getMethodInfo2();
      CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
      if (codeAttribute != null) {
        removeConsCall(codeAttribute);
        try {
          this.methodInfo.rebuildStackMapIf6(paramCtClass.getClassPool(), paramCtClass
              .getClassFile2());
        } catch (BadBytecode badBytecode) {
          throw new CannotCompileException(badBytecode);
        } 
      } 
    } 
    ctMethod.setName(paramString);
    return ctMethod;
  }
  
  private static void removeConsCall(CodeAttribute paramCodeAttribute) throws CannotCompileException {
    CodeIterator codeIterator = paramCodeAttribute.iterator();
    try {
      int i = codeIterator.skipConstructor();
      if (i >= 0) {
        int j = codeIterator.u16bitAt(i + 1);
        String str = paramCodeAttribute.getConstPool().getMethodrefType(j);
        int k = Descriptor.numOfParameters(str) + 1;
        if (k > 3)
          i = (codeIterator.insertGapAt(i, k - 3, false)).position; 
        codeIterator.writeByte(87, i++);
        codeIterator.writeByte(0, i);
        codeIterator.writeByte(0, i + 1);
        Descriptor.Iterator iterator = new Descriptor.Iterator(str);
        while (true) {
          iterator.next();
          if (iterator.isParameter()) {
            codeIterator.writeByte(iterator.is2byte() ? 88 : 87, i++);
            continue;
          } 
          break;
        } 
      } 
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
}
