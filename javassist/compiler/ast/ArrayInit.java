package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class ArrayInit extends ASTList {
  public ArrayInit(ASTree paramASTree) {
    super(paramASTree);
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atArrayInit(this);
  }
  
  public String getTag() {
    return "array";
  }
}
