package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.TokenId;

public class Expr extends ASTList implements TokenId {
  protected int operatorId;
  
  Expr(int paramInt, ASTree paramASTree, ASTList paramASTList) {
    super(paramASTree, paramASTList);
    this.operatorId = paramInt;
  }
  
  Expr(int paramInt, ASTree paramASTree) {
    super(paramASTree);
    this.operatorId = paramInt;
  }
  
  public static Expr make(int paramInt, ASTree paramASTree1, ASTree paramASTree2) {
    return new Expr(paramInt, paramASTree1, new ASTList(paramASTree2));
  }
  
  public static Expr make(int paramInt, ASTree paramASTree) {
    return new Expr(paramInt, paramASTree);
  }
  
  public int getOperator() {
    return this.operatorId;
  }
  
  public void setOperator(int paramInt) {
    this.operatorId = paramInt;
  }
  
  public ASTree oprand1() {
    return getLeft();
  }
  
  public void setOprand1(ASTree paramASTree) {
    setLeft(paramASTree);
  }
  
  public ASTree oprand2() {
    return getRight().getLeft();
  }
  
  public void setOprand2(ASTree paramASTree) {
    getRight().setLeft(paramASTree);
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atExpr(this);
  }
  
  public String getName() {
    int i = this.operatorId;
    if (i < 128)
      return String.valueOf((char)i); 
    if (350 <= i && i <= 371)
      return opNames[i - 350]; 
    if (i == 323)
      return "instanceof"; 
    return String.valueOf(i);
  }
  
  protected String getTag() {
    return "op:" + getName();
  }
}
