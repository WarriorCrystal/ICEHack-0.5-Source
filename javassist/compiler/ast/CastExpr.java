package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.TokenId;

public class CastExpr extends ASTList implements TokenId {
  protected int castType;
  
  protected int arrayDim;
  
  public CastExpr(ASTList paramASTList, int paramInt, ASTree paramASTree) {
    super(paramASTList, new ASTList(paramASTree));
    this.castType = 307;
    this.arrayDim = paramInt;
  }
  
  public CastExpr(int paramInt1, int paramInt2, ASTree paramASTree) {
    super(null, new ASTList(paramASTree));
    this.castType = paramInt1;
    this.arrayDim = paramInt2;
  }
  
  public int getType() {
    return this.castType;
  }
  
  public int getArrayDim() {
    return this.arrayDim;
  }
  
  public ASTList getClassName() {
    return (ASTList)getLeft();
  }
  
  public ASTree getOprand() {
    return getRight().getLeft();
  }
  
  public void setOprand(ASTree paramASTree) {
    getRight().setLeft(paramASTree);
  }
  
  public String getTag() {
    return "cast:" + this.castType + ":" + this.arrayDim;
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atCastExpr(this);
  }
}
