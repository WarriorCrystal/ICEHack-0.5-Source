package javassist.expr;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

public class MethodCall extends Expr {
  protected MethodCall(int paramInt, CodeIterator paramCodeIterator, CtClass paramCtClass, MethodInfo paramMethodInfo) {
    super(paramInt, paramCodeIterator, paramCtClass, paramMethodInfo);
  }
  
  private int getNameAndType(ConstPool14 paramConstPool14) {
    int i = this.currentPos;
    int j = this.iterator.byteAt(i);
    int k = this.iterator.u16bitAt(i + 1);
    if (j == 185)
      return paramConstPool14.getInterfaceMethodrefNameAndType(k); 
    return paramConstPool14.getMethodrefNameAndType(k);
  }
  
  public CtBehavior where() {
    return super.where();
  }
  
  public int getLineNumber() {
    return super.getLineNumber();
  }
  
  public String getFileName() {
    return super.getFileName();
  }
  
  protected CtClass getCtClass() throws NotFoundException {
    return this.thisClass.getClassPool().get(getClassName());
  }
  
  public String getClassName() {
    String str;
    ConstPool14 constPool14 = getConstPool();
    int i = this.currentPos;
    int j = this.iterator.byteAt(i);
    int k = this.iterator.u16bitAt(i + 1);
    if (j == 185) {
      str = constPool14.getInterfaceMethodrefClassName(k);
    } else {
      str = constPool14.getMethodrefClassName(k);
    } 
    if (str.charAt(0) == '[')
      str = Descriptor.toClassName(str); 
    return str;
  }
  
  public String getMethodName() {
    ConstPool14 constPool14 = getConstPool();
    int i = getNameAndType(constPool14);
    return constPool14.getUtf8Info(constPool14.getNameAndTypeName(i));
  }
  
  public CtMethod getMethod() throws NotFoundException {
    return getCtClass().getMethod(getMethodName(), getSignature());
  }
  
  public String getSignature() {
    ConstPool14 constPool14 = getConstPool();
    int i = getNameAndType(constPool14);
    return constPool14.getUtf8Info(constPool14.getNameAndTypeDescriptor(i));
  }
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  public boolean isSuper() {
    return (this.iterator.byteAt(this.currentPos) == 183 && 
      !where().getDeclaringClass().getName().equals(getClassName()));
  }
  
  public void replace(String paramString) throws CannotCompileException {
    String str1, str2, str3;
    byte b;
    this.thisClass.getClassFile();
    ConstPool14 constPool14 = getConstPool();
    int i = this.currentPos;
    int j = this.iterator.u16bitAt(i + 1);
    int k = this.iterator.byteAt(i);
    if (k == 185) {
      b = 5;
      str1 = constPool14.getInterfaceMethodrefClassName(j);
      str2 = constPool14.getInterfaceMethodrefName(j);
      str3 = constPool14.getInterfaceMethodrefType(j);
    } else if (k == 184 || k == 183 || k == 182) {
      b = 3;
      str1 = constPool14.getMethodrefClassName(j);
      str2 = constPool14.getMethodrefName(j);
      str3 = constPool14.getMethodrefType(j);
    } else {
      throw new CannotCompileException("not method invocation");
    } 
    Javac javac = new Javac(this.thisClass);
    ClassPool classPool = this.thisClass.getClassPool();
    CodeAttribute codeAttribute = this.iterator.get();
    try {
      CtClass[] arrayOfCtClass = Descriptor.getParameterTypes(str3, classPool);
      CtClass ctClass = Descriptor.getReturnType(str3, classPool);
      int m = codeAttribute.getMaxLocals();
      javac.recordParams(str1, arrayOfCtClass, true, m, 
          withinStatic());
      int n = javac.recordReturnType(ctClass, true);
      if (k == 184) {
        javac.recordStaticProceed(str1, str2);
      } else if (k == 183) {
        javac.recordSpecialProceed("$0", str1, str2, str3, j);
      } else {
        javac.recordProceed("$0", str2);
      } 
      checkResultValue(ctClass, paramString);
      Bytecode1 bytecode1 = javac.getBytecode();
      storeStack(arrayOfCtClass, (k == 184), m, bytecode1);
      javac.recordLocalVariables(codeAttribute, i);
      if (ctClass != CtClass.voidType) {
        bytecode1.addConstZero(ctClass);
        bytecode1.addStore(n, ctClass);
      } 
      javac.compileStmnt(paramString);
      if (ctClass != CtClass.voidType)
        bytecode1.addLoad(n, ctClass); 
      replace0(i, bytecode1, b);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException("broken method");
    } 
  }
}
