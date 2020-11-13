package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.TokenId;

public class NewExpr extends ASTList implements TokenId {
  protected boolean newArray;
  
  protected int arrayType;
  
  public NewExpr(ASTList paramASTList1, ASTList paramASTList2) {
    super(paramASTList1, new ASTList(paramASTList2));
    this.newArray = false;
    this.arrayType = 307;
  }
  
  public NewExpr(int paramInt, ASTList paramASTList, ArrayInit paramArrayInit) {
    super(null, new ASTList(paramASTList));
    this.newArray = true;
    this.arrayType = paramInt;
    if (paramArrayInit != null)
      append(this, paramArrayInit); 
  }
  
  public static NewExpr makeObjectArray(ASTList paramASTList1, ASTList paramASTList2, ArrayInit paramArrayInit) {
    NewExpr newExpr = new NewExpr(paramASTList1, paramASTList2);
    newExpr.newArray = true;
    if (paramArrayInit != null)
      append(newExpr, paramArrayInit); 
    return newExpr;
  }
  
  public boolean isArray() {
    return this.newArray;
  }
  
  public int getArrayType() {
    return this.arrayType;
  }
  
  public ASTList getClassName() {
    return (ASTList)getLeft();
  }
  
  public ASTList getArguments() {
    return (ASTList)getRight().getLeft();
  }
  
  public ASTList getArraySize() {
    return getArguments();
  }
  
  public ArrayInit getInitializer() {
    ASTree aSTree = getRight().getRight();
    if (aSTree == null)
      return null; 
    return (ArrayInit)aSTree.getLeft();
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atNewExpr(this);
  }
  
  protected String getTag() {
    return this.newArray ? "new[]" : "new";
  }
}
