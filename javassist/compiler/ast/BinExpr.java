package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class BinExpr extends Expr {
  private BinExpr(int paramInt, ASTree paramASTree, ASTList paramASTList) {
    super(paramInt, paramASTree, paramASTList);
  }
  
  public static BinExpr makeBin(int paramInt, ASTree paramASTree1, ASTree paramASTree2) {
    return new BinExpr(paramInt, paramASTree1, new ASTList(paramASTree2));
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atBinExpr(this);
  }
}
