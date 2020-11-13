package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class CondExpr extends ASTList {
  public CondExpr(ASTree paramASTree1, ASTree paramASTree2, ASTree paramASTree3) {
    super(paramASTree1, new ASTList(paramASTree2, new ASTList(paramASTree3)));
  }
  
  public ASTree condExpr() {
    return head();
  }
  
  public void setCond(ASTree paramASTree) {
    setHead(paramASTree);
  }
  
  public ASTree thenExpr() {
    return tail().head();
  }
  
  public void setThen(ASTree paramASTree) {
    tail().setHead(paramASTree);
  }
  
  public ASTree elseExpr() {
    return tail().tail().head();
  }
  
  public void setElse(ASTree paramASTree) {
    tail().tail().setHead(paramASTree);
  }
  
  public String getTag() {
    return "?:";
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atCondExpr(this);
  }
}
