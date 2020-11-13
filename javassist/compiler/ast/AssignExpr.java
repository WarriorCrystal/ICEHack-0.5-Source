package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class AssignExpr extends Expr {
  private AssignExpr(int paramInt, ASTree paramASTree, ASTList paramASTList) {
    super(paramInt, paramASTree, paramASTList);
  }
  
  public static AssignExpr makeAssign(int paramInt, ASTree paramASTree1, ASTree paramASTree2) {
    return new AssignExpr(paramInt, paramASTree1, new ASTList(paramASTree2));
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atAssignExpr(this);
  }
}
