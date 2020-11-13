package javassist.compiler;

import javassist.bytecode.Bytecode1;
import javassist.compiler.ast.ASTList;

public interface ProceedHandler {
  void doit(JvstCodeGen paramJvstCodeGen, Bytecode1 paramBytecode1, ASTList paramASTList) throws CompileError;
  
  void setReturnType(JvstTypeChecker paramJvstTypeChecker, ASTList paramASTList) throws CompileError;
}
