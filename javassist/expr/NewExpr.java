package javassist.expr;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
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
import javassist.compiler.JvstCodeGen;
import javassist.compiler.JvstTypeChecker;
import javassist.compiler.ProceedHandler;
import javassist.compiler.ast.ASTList;

public class NewExpr extends Expr {
  String newTypeName;
  
  int newPos;
  
  protected NewExpr(int paramInt1, CodeIterator paramCodeIterator, CtClass paramCtClass, MethodInfo paramMethodInfo, String paramString, int paramInt2) {
    super(paramInt1, paramCodeIterator, paramCtClass, paramMethodInfo);
    this.newTypeName = paramString;
    this.newPos = paramInt2;
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
  
  private CtClass getCtClass() throws NotFoundException {
    return this.thisClass.getClassPool().get(this.newTypeName);
  }
  
  public String getClassName() {
    return this.newTypeName;
  }
  
  public String getSignature() {
    ConstPool14 constPool14 = getConstPool();
    int i = this.iterator.u16bitAt(this.currentPos + 1);
    return constPool14.getMethodrefType(i);
  }
  
  public CtConstructor getConstructor() throws NotFoundException {
    ConstPool14 constPool14 = getConstPool();
    int i = this.iterator.u16bitAt(this.currentPos + 1);
    String str = constPool14.getMethodrefType(i);
    return getCtClass().getConstructor(str);
  }
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  private int canReplace() throws CannotCompileException {
    int i = this.iterator.byteAt(this.newPos + 3);
    if (i == 89)
      return (this.iterator.byteAt(this.newPos + 4) == 94 && this.iterator
        .byteAt(this.newPos + 5) == 88) ? 6 : 4; 
    if (i == 90 && this.iterator
      .byteAt(this.newPos + 4) == 95)
      return 5; 
    return 3;
  }
  
  public void replace(String paramString) throws CannotCompileException {
    this.thisClass.getClassFile();
    byte b = 3;
    int i = this.newPos;
    int j = this.iterator.u16bitAt(i + 1);
    int k = canReplace();
    int m = i + k;
    for (int n = i; n < m; n++)
      this.iterator.writeByte(0, n); 
    ConstPool14 constPool14 = getConstPool();
    i = this.currentPos;
    int i1 = this.iterator.u16bitAt(i + 1);
    String str = constPool14.getMethodrefType(i1);
    Javac javac = new Javac(this.thisClass);
    ClassPool classPool = this.thisClass.getClassPool();
    CodeAttribute codeAttribute = this.iterator.get();
    try {
      CtClass[] arrayOfCtClass = Descriptor.getParameterTypes(str, classPool);
      CtClass ctClass = classPool.get(this.newTypeName);
      int i2 = codeAttribute.getMaxLocals();
      javac.recordParams(this.newTypeName, arrayOfCtClass, true, i2, 
          withinStatic());
      int i3 = javac.recordReturnType(ctClass, true);
      javac.recordProceed(new ProceedForNew(ctClass, j, i1));
      checkResultValue(ctClass, paramString);
      Bytecode1 bytecode1 = javac.getBytecode();
      storeStack(arrayOfCtClass, true, i2, bytecode1);
      javac.recordLocalVariables(codeAttribute, i);
      bytecode1.addConstZero(ctClass);
      bytecode1.addStore(i3, ctClass);
      javac.compileStmnt(paramString);
      if (k > 3)
        bytecode1.addAload(i3); 
      replace0(i, bytecode1, 3);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException("broken method");
    } 
  }
  
  static class ProceedForNew implements ProceedHandler {
    CtClass newType;
    
    int newIndex;
    
    int methodIndex;
    
    ProceedForNew(CtClass param1CtClass, int param1Int1, int param1Int2) {
      this.newType = param1CtClass;
      this.newIndex = param1Int1;
      this.methodIndex = param1Int2;
    }
    
    public void doit(JvstCodeGen param1JvstCodeGen, Bytecode1 param1Bytecode1, ASTList param1ASTList) throws CompileError {
      param1Bytecode1.addOpcode(187);
      param1Bytecode1.addIndex(this.newIndex);
      param1Bytecode1.addOpcode(89);
      param1JvstCodeGen.atMethodCallCore(this.newType, "<init>", param1ASTList, false, true, -1, null);
      param1JvstCodeGen.setType(this.newType);
    }
    
    public void setReturnType(JvstTypeChecker param1JvstTypeChecker, ASTList param1ASTList) throws CompileError {
      param1JvstTypeChecker.atMethodCallCore(this.newType, "<init>", param1ASTList);
      param1JvstTypeChecker.setType(this.newType);
    }
  }
}
