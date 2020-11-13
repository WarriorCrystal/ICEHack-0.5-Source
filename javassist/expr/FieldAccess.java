package javassist.expr;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtPrimitiveType;
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

public class FieldAccess extends Expr {
  int opcode;
  
  protected FieldAccess(int paramInt1, CodeIterator paramCodeIterator, CtClass paramCtClass, MethodInfo paramMethodInfo, int paramInt2) {
    super(paramInt1, paramCodeIterator, paramCtClass, paramMethodInfo);
    this.opcode = paramInt2;
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
  
  public boolean isStatic() {
    return isStatic(this.opcode);
  }
  
  static boolean isStatic(int paramInt) {
    return (paramInt == 178 || paramInt == 179);
  }
  
  public boolean isReader() {
    return (this.opcode == 180 || this.opcode == 178);
  }
  
  public boolean isWriter() {
    return (this.opcode == 181 || this.opcode == 179);
  }
  
  private CtClass getCtClass() throws NotFoundException {
    return this.thisClass.getClassPool().get(getClassName());
  }
  
  public String getClassName() {
    int i = this.iterator.u16bitAt(this.currentPos + 1);
    return getConstPool().getFieldrefClassName(i);
  }
  
  public String getFieldName() {
    int i = this.iterator.u16bitAt(this.currentPos + 1);
    return getConstPool().getFieldrefName(i);
  }
  
  public CtField getField() throws NotFoundException {
    CtClass ctClass = getCtClass();
    int i = this.iterator.u16bitAt(this.currentPos + 1);
    ConstPool14 constPool14 = getConstPool();
    return ctClass.getField(constPool14.getFieldrefName(i), constPool14.getFieldrefType(i));
  }
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  public String getSignature() {
    int i = this.iterator.u16bitAt(this.currentPos + 1);
    return getConstPool().getFieldrefType(i);
  }
  
  public void replace(String paramString) throws CannotCompileException {
    this.thisClass.getClassFile();
    ConstPool14 constPool14 = getConstPool();
    int i = this.currentPos;
    int j = this.iterator.u16bitAt(i + 1);
    Javac javac = new Javac(this.thisClass);
    CodeAttribute codeAttribute = this.iterator.get();
    try {
      CtClass arrayOfCtClass[], ctClass1, ctClass2 = Descriptor.toCtClass(constPool14.getFieldrefType(j), this.thisClass
          .getClassPool());
      boolean bool1 = isReader();
      if (bool1) {
        arrayOfCtClass = new CtClass[0];
        ctClass1 = ctClass2;
      } else {
        arrayOfCtClass = new CtClass[1];
        arrayOfCtClass[0] = ctClass2;
        ctClass1 = CtClass.voidType;
      } 
      int k = codeAttribute.getMaxLocals();
      javac.recordParams(constPool14.getFieldrefClassName(j), arrayOfCtClass, true, k, 
          withinStatic());
      boolean bool2 = checkResultValue(ctClass1, paramString);
      if (bool1)
        bool2 = true; 
      int m = javac.recordReturnType(ctClass1, bool2);
      if (bool1) {
        javac.recordProceed(new ProceedForRead(ctClass1, this.opcode, j, k));
      } else {
        javac.recordType(ctClass2);
        javac.recordProceed(new ProceedForWrite(arrayOfCtClass[0], this.opcode, j, k));
      } 
      Bytecode1 bytecode1 = javac.getBytecode();
      storeStack(arrayOfCtClass, isStatic(), k, bytecode1);
      javac.recordLocalVariables(codeAttribute, i);
      if (bool2)
        if (ctClass1 == CtClass.voidType) {
          bytecode1.addOpcode(1);
          bytecode1.addAstore(m);
        } else {
          bytecode1.addConstZero(ctClass1);
          bytecode1.addStore(m, ctClass1);
        }  
      javac.compileStmnt(paramString);
      if (bool1)
        bytecode1.addLoad(m, ctClass1); 
      replace0(i, bytecode1, 3);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException("broken method");
    } 
  }
  
  static class ProceedForRead implements ProceedHandler {
    CtClass fieldType;
    
    int opcode;
    
    int targetVar;
    
    int index;
    
    ProceedForRead(CtClass param1CtClass, int param1Int1, int param1Int2, int param1Int3) {
      this.fieldType = param1CtClass;
      this.targetVar = param1Int3;
      this.opcode = param1Int1;
      this.index = param1Int2;
    }
    
    public void doit(JvstCodeGen param1JvstCodeGen, Bytecode1 param1Bytecode1, ASTList param1ASTList) throws CompileError {
      int i;
      if (param1ASTList != null && !param1JvstCodeGen.isParamListName(param1ASTList))
        throw new CompileError("$proceed() cannot take a parameter for field reading"); 
      if (FieldAccess.isStatic(this.opcode)) {
        i = 0;
      } else {
        i = -1;
        param1Bytecode1.addAload(this.targetVar);
      } 
      if (this.fieldType instanceof CtPrimitiveType) {
        i += ((CtPrimitiveType)this.fieldType).getDataSize();
      } else {
        i++;
      } 
      param1Bytecode1.add(this.opcode);
      param1Bytecode1.addIndex(this.index);
      param1Bytecode1.growStack(i);
      param1JvstCodeGen.setType(this.fieldType);
    }
    
    public void setReturnType(JvstTypeChecker param1JvstTypeChecker, ASTList param1ASTList) throws CompileError {
      param1JvstTypeChecker.setType(this.fieldType);
    }
  }
  
  static class ProceedForWrite implements ProceedHandler {
    CtClass fieldType;
    
    int opcode;
    
    int targetVar;
    
    int index;
    
    ProceedForWrite(CtClass param1CtClass, int param1Int1, int param1Int2, int param1Int3) {
      this.fieldType = param1CtClass;
      this.targetVar = param1Int3;
      this.opcode = param1Int1;
      this.index = param1Int2;
    }
    
    public void doit(JvstCodeGen param1JvstCodeGen, Bytecode1 param1Bytecode1, ASTList param1ASTList) throws CompileError {
      int i;
      if (param1JvstCodeGen.getMethodArgsLength(param1ASTList) != 1)
        throw new CompileError("$proceed() cannot take more than one parameter for field writing"); 
      if (FieldAccess.isStatic(this.opcode)) {
        i = 0;
      } else {
        i = -1;
        param1Bytecode1.addAload(this.targetVar);
      } 
      param1JvstCodeGen.atMethodArgs(param1ASTList, new int[1], new int[1], new String[1]);
      param1JvstCodeGen.doNumCast(this.fieldType);
      if (this.fieldType instanceof CtPrimitiveType) {
        i -= ((CtPrimitiveType)this.fieldType).getDataSize();
      } else {
        i--;
      } 
      param1Bytecode1.add(this.opcode);
      param1Bytecode1.addIndex(this.index);
      param1Bytecode1.growStack(i);
      param1JvstCodeGen.setType(CtClass.voidType);
      param1JvstCodeGen.addNullIfVoid();
    }
    
    public void setReturnType(JvstTypeChecker param1JvstTypeChecker, ASTList param1ASTList) throws CompileError {
      param1JvstTypeChecker.atMethodArgs(param1ASTList, new int[1], new int[1], new String[1]);
      param1JvstTypeChecker.setType(CtClass.voidType);
      param1JvstTypeChecker.addNullIfVoid();
    }
  }
}
