package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.MemberResolver;

public class CallExpr extends Expr {
  private MemberResolver.Method method;
  
  private CallExpr(ASTree paramASTree, ASTList paramASTList) {
    super(67, paramASTree, paramASTList);
    this.method = null;
  }
  
  public void setMethod(MemberResolver.Method paramMethod) {
    this.method = paramMethod;
  }
  
  public MemberResolver.Method getMethod() {
    return this.method;
  }
  
  public static CallExpr makeCall(ASTree paramASTree1, ASTree paramASTree2) {
    return new CallExpr(paramASTree1, new ASTList(paramASTree2));
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atCallExpr(this);
  }
}
