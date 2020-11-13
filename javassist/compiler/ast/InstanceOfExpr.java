package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class InstanceOfExpr extends CastExpr {
  public InstanceOfExpr(ASTList paramASTList, int paramInt, ASTree paramASTree) {
    super(paramASTList, paramInt, paramASTree);
  }
  
  public InstanceOfExpr(int paramInt1, int paramInt2, ASTree paramASTree) {
    super(paramInt1, paramInt2, paramASTree);
  }
  
  public String getTag() {
    return "instanceof:" + this.castType + ":" + this.arrayDim;
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atInstanceOfExpr(this);
  }
}
