package javassist.expr;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
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

public class NewArray extends Expr {
  int opcode;
  
  protected NewArray(int paramInt1, CodeIterator paramCodeIterator, CtClass paramCtClass, MethodInfo paramMethodInfo, int paramInt2) {
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
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  public CtClass getComponentType() throws NotFoundException {
    if (this.opcode == 188) {
      int i = this.iterator.byteAt(this.currentPos + 1);
      return getPrimitiveType(i);
    } 
    if (this.opcode == 189 || this.opcode == 197) {
      int i = this.iterator.u16bitAt(this.currentPos + 1);
      String str = getConstPool().getClassInfo(i);
      int j = Descriptor.arrayDimension(str);
      str = Descriptor.toArrayComponent(str, j);
      return Descriptor.toCtClass(str, this.thisClass.getClassPool());
    } 
    throw new RuntimeException("bad opcode: " + this.opcode);
  }
  
  CtClass getPrimitiveType(int paramInt) {
    switch (paramInt) {
      case 4:
        return CtClass.booleanType;
      case 5:
        return CtClass.charType;
      case 6:
        return CtClass.floatType;
      case 7:
        return CtClass.doubleType;
      case 8:
        return CtClass.byteType;
      case 9:
        return CtClass.shortType;
      case 10:
        return CtClass.intType;
      case 11:
        return CtClass.longType;
    } 
    throw new RuntimeException("bad atype: " + paramInt);
  }
  
  public int getDimension() {
    if (this.opcode == 188)
      return 1; 
    if (this.opcode == 189 || this.opcode == 197) {
      int i = this.iterator.u16bitAt(this.currentPos + 1);
      String str = getConstPool().getClassInfo(i);
      return Descriptor.arrayDimension(str) + ((this.opcode == 189) ? 1 : 0);
    } 
    throw new RuntimeException("bad opcode: " + this.opcode);
  }
  
  public int getCreatedDimensions() {
    if (this.opcode == 197)
      return this.iterator.byteAt(this.currentPos + 3); 
    return 1;
  }
  
  public void replace(String paramString) throws CannotCompileException {
    try {
      replace2(paramString);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException("broken method");
    } 
  }
  
  private void replace2(String paramString) throws CompileError, NotFoundException, BadBytecode, CannotCompileException {
    byte b;
    String str;
    this.thisClass.getClassFile();
    ConstPool14 constPool14 = getConstPool();
    int i = this.currentPos;
    int j = 0;
    int k = 1;
    if (this.opcode == 188) {
      j = this.iterator.byteAt(this.currentPos + 1);
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)getPrimitiveType(j);
      str = "[" + ctPrimitiveType.getDescriptor();
      b = 2;
    } else if (this.opcode == 189) {
      j = this.iterator.u16bitAt(i + 1);
      str = constPool14.getClassInfo(j);
      if (str.startsWith("[")) {
        str = "[" + str;
      } else {
        str = "[L" + str + ";";
      } 
      b = 3;
    } else if (this.opcode == 197) {
      j = this.iterator.u16bitAt(this.currentPos + 1);
      str = constPool14.getClassInfo(j);
      k = this.iterator.byteAt(this.currentPos + 3);
      b = 4;
    } else {
      throw new RuntimeException("bad opcode: " + this.opcode);
    } 
    CtClass ctClass = Descriptor.toCtClass(str, this.thisClass.getClassPool());
    Javac javac = new Javac(this.thisClass);
    CodeAttribute codeAttribute = this.iterator.get();
    CtClass[] arrayOfCtClass = new CtClass[k];
    int m;
    for (m = 0; m < k; m++)
      arrayOfCtClass[m] = CtClass.intType; 
    m = codeAttribute.getMaxLocals();
    javac.recordParams("java.lang.Object", arrayOfCtClass, true, m, 
        withinStatic());
    checkResultValue(ctClass, paramString);
    int n = javac.recordReturnType(ctClass, true);
    javac.recordProceed(new ProceedForArray(ctClass, this.opcode, j, k));
    Bytecode1 bytecode1 = javac.getBytecode();
    storeStack(arrayOfCtClass, true, m, bytecode1);
    javac.recordLocalVariables(codeAttribute, i);
    bytecode1.addOpcode(1);
    bytecode1.addAstore(n);
    javac.compileStmnt(paramString);
    bytecode1.addAload(n);
    replace0(i, bytecode1, b);
  }
  
  static class ProceedForArray implements ProceedHandler {
    CtClass arrayType;
    
    int opcode;
    
    int index;
    
    int dimension;
    
    ProceedForArray(CtClass param1CtClass, int param1Int1, int param1Int2, int param1Int3) {
      this.arrayType = param1CtClass;
      this.opcode = param1Int1;
      this.index = param1Int2;
      this.dimension = param1Int3;
    }
    
    public void doit(JvstCodeGen param1JvstCodeGen, Bytecode1 param1Bytecode1, ASTList param1ASTList) throws CompileError {
      int i = param1JvstCodeGen.getMethodArgsLength(param1ASTList);
      if (i != this.dimension)
        throw new CompileError("$proceed() with a wrong number of parameters"); 
      param1JvstCodeGen.atMethodArgs(param1ASTList, new int[i], new int[i], new String[i]);
      param1Bytecode1.addOpcode(this.opcode);
      if (this.opcode == 189) {
        param1Bytecode1.addIndex(this.index);
      } else if (this.opcode == 188) {
        param1Bytecode1.add(this.index);
      } else {
        param1Bytecode1.addIndex(this.index);
        param1Bytecode1.add(this.dimension);
        param1Bytecode1.growStack(1 - this.dimension);
      } 
      param1JvstCodeGen.setType(this.arrayType);
    }
    
    public void setReturnType(JvstTypeChecker param1JvstTypeChecker, ASTList param1ASTList) throws CompileError {
      param1JvstTypeChecker.setType(this.arrayType);
    }
  }
}
