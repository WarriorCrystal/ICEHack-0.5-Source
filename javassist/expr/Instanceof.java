package javassist.expr;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.compiler.JvstCodeGen;
import javassist.compiler.JvstTypeChecker;
import javassist.compiler.ProceedHandler;
import javassist.compiler.ast.ASTList;

public class Instanceof extends Expr {
  protected Instanceof(int paramInt, CodeIterator paramCodeIterator, CtClass paramCtClass, MethodInfo paramMethodInfo) {
    super(paramInt, paramCodeIterator, paramCtClass, paramMethodInfo);
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
  
  public CtClass getType() throws NotFoundException {
    ConstPool14 constPool14 = getConstPool();
    int i = this.currentPos;
    int j = this.iterator.u16bitAt(i + 1);
    String str = constPool14.getClassInfo(j);
    return this.thisClass.getClassPool().getCtClass(str);
  }
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  public void replace(String paramString) throws CannotCompileException {
    this.thisClass.getClassFile();
    ConstPool14 constPool14 = getConstPool();
    int i = this.currentPos;
    int j = this.iterator.u16bitAt(i + 1);
    Javac javac = new Javac(this.thisClass);
    ClassPool classPool = this.thisClass.getClassPool();
    CodeAttribute codeAttribute = this.iterator.get();
    try {
      CtClass[] arrayOfCtClass = { classPool.get("java.lang.Object") };
      CtClass ctClass = CtClass.booleanType;
      int k = codeAttribute.getMaxLocals();
      javac.recordParams("java.lang.Object", arrayOfCtClass, true, k, 
          withinStatic());
      int m = javac.recordReturnType(ctClass, true);
      javac.recordProceed(new ProceedForInstanceof(j));
      javac.recordType(getType());
      checkResultValue(ctClass, paramString);
      Bytecode1 bytecode1 = javac.getBytecode();
      storeStack(arrayOfCtClass, true, k, bytecode1);
      javac.recordLocalVariables(codeAttribute, i);
      bytecode1.addConstZero(ctClass);
      bytecode1.addStore(m, ctClass);
      javac.compileStmnt(paramString);
      bytecode1.addLoad(m, ctClass);
      replace0(i, bytecode1, 3);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException("broken method");
    } 
  }
  
  static class ProceedForInstanceof implements ProceedHandler {
    int index;
    
    ProceedForInstanceof(int param1Int) {
      this.index = param1Int;
    }
    
    public void doit(JvstCodeGen param1JvstCodeGen, Bytecode1 param1Bytecode1, ASTList param1ASTList) throws CompileError {
      if (param1JvstCodeGen.getMethodArgsLength(param1ASTList) != 1)
        throw new CompileError("$proceed() cannot take more than one parameter for instanceof"); 
      param1JvstCodeGen.atMethodArgs(param1ASTList, new int[1], new int[1], new String[1]);
      param1Bytecode1.addOpcode(193);
      param1Bytecode1.addIndex(this.index);
      param1JvstCodeGen.setType(CtClass.booleanType);
    }
    
    public void setReturnType(JvstTypeChecker param1JvstTypeChecker, ASTList param1ASTList) throws CompileError {
      param1JvstTypeChecker.atMethodArgs(param1ASTList, new int[1], new int[1], new String[1]);
      param1JvstTypeChecker.setType(CtClass.booleanType);
    }
  }
}
