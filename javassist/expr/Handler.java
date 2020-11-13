package javassist.expr;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

public class Handler extends Expr {
  private static String EXCEPTION_NAME = "$1";
  
  private ExceptionTable etable;
  
  private int index;
  
  protected Handler(ExceptionTable paramExceptionTable, int paramInt, CodeIterator paramCodeIterator, CtClass paramCtClass, MethodInfo paramMethodInfo) {
    super(paramExceptionTable.handlerPc(paramInt), paramCodeIterator, paramCtClass, paramMethodInfo);
    this.etable = paramExceptionTable;
    this.index = paramInt;
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
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  public CtClass getType() throws NotFoundException {
    int i = this.etable.catchType(this.index);
    if (i == 0)
      return null; 
    ConstPool14 constPool14 = getConstPool();
    String str = constPool14.getClassInfo(i);
    return this.thisClass.getClassPool().getCtClass(str);
  }
  
  public boolean isFinally() {
    return (this.etable.catchType(this.index) == 0);
  }
  
  public void replace(String paramString) throws CannotCompileException {
    throw new RuntimeException("not implemented yet");
  }
  
  public void insertBefore(String paramString) throws CannotCompileException {
    this.edited = true;
    ConstPool14 constPool14 = getConstPool();
    CodeAttribute codeAttribute = this.iterator.get();
    Javac javac = new Javac(this.thisClass);
    Bytecode1 bytecode1 = javac.getBytecode();
    bytecode1.setStackDepth(1);
    bytecode1.setMaxLocals(codeAttribute.getMaxLocals());
    try {
      CtClass ctClass = getType();
      int i = javac.recordVariable(ctClass, EXCEPTION_NAME);
      javac.recordReturnType(ctClass, false);
      bytecode1.addAstore(i);
      javac.compileStmnt(paramString);
      bytecode1.addAload(i);
      int j = this.etable.handlerPc(this.index);
      bytecode1.addOpcode(167);
      bytecode1.addIndex(j - this.iterator.getCodeLength() - bytecode1
          .currentPc() + 1);
      this.maxStack = bytecode1.getMaxStack();
      this.maxLocals = bytecode1.getMaxLocals();
      int k = this.iterator.append(bytecode1.get());
      this.iterator.append(bytecode1.getExceptionTable(), k);
      this.etable.setHandlerPc(this.index, k);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } 
  }
}
