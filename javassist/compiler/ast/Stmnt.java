package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.TokenId;

public class Stmnt extends ASTList implements TokenId {
  protected int operatorId;
  
  public Stmnt(int paramInt, ASTree paramASTree, ASTList paramASTList) {
    super(paramASTree, paramASTList);
    this.operatorId = paramInt;
  }
  
  public Stmnt(int paramInt, ASTree paramASTree) {
    super(paramASTree);
    this.operatorId = paramInt;
  }
  
  public Stmnt(int paramInt) {
    this(paramInt, null);
  }
  
  public static Stmnt make(int paramInt, ASTree paramASTree1, ASTree paramASTree2) {
    return new Stmnt(paramInt, paramASTree1, new ASTList(paramASTree2));
  }
  
  public static Stmnt make(int paramInt, ASTree paramASTree1, ASTree paramASTree2, ASTree paramASTree3) {
    return new Stmnt(paramInt, paramASTree1, new ASTList(paramASTree2, new ASTList(paramASTree3)));
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atStmnt(this);
  }
  
  public int getOperator() {
    return this.operatorId;
  }
  
  protected String getTag() {
    if (this.operatorId < 128)
      return "stmnt:" + (char)this.operatorId; 
    return "stmnt:" + this.operatorId;
  }
}
