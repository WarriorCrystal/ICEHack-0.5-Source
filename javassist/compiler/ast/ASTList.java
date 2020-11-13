package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class ASTList extends ASTree {
  private ASTree left;
  
  private ASTList right;
  
  public ASTList(ASTree paramASTree, ASTList paramASTList) {
    this.left = paramASTree;
    this.right = paramASTList;
  }
  
  public ASTList(ASTree paramASTree) {
    this.left = paramASTree;
    this.right = null;
  }
  
  public static ASTList make(ASTree paramASTree1, ASTree paramASTree2, ASTree paramASTree3) {
    return new ASTList(paramASTree1, new ASTList(paramASTree2, new ASTList(paramASTree3)));
  }
  
  public ASTree getLeft() {
    return this.left;
  }
  
  public ASTree getRight() {
    return this.right;
  }
  
  public void setLeft(ASTree paramASTree) {
    this.left = paramASTree;
  }
  
  public void setRight(ASTree paramASTree) {
    this.right = (ASTList)paramASTree;
  }
  
  public ASTree head() {
    return this.left;
  }
  
  public void setHead(ASTree paramASTree) {
    this.left = paramASTree;
  }
  
  public ASTList tail() {
    return this.right;
  }
  
  public void setTail(ASTList paramASTList) {
    this.right = paramASTList;
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atASTList(this);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(<");
    stringBuffer.append(getTag());
    stringBuffer.append('>');
    ASTList aSTList = this;
    while (aSTList != null) {
      stringBuffer.append(' ');
      ASTree aSTree = aSTList.left;
      stringBuffer.append((aSTree == null) ? "<null>" : aSTree.toString());
      aSTList = aSTList.right;
    } 
    stringBuffer.append(')');
    return stringBuffer.toString();
  }
  
  public int length() {
    return length(this);
  }
  
  public static int length(ASTList paramASTList) {
    if (paramASTList == null)
      return 0; 
    byte b = 0;
    while (paramASTList != null) {
      paramASTList = paramASTList.right;
      b++;
    } 
    return b;
  }
  
  public ASTList sublist(int paramInt) {
    ASTList aSTList = this;
    while (paramInt-- > 0)
      aSTList = aSTList.right; 
    return aSTList;
  }
  
  public boolean subst(ASTree paramASTree1, ASTree paramASTree2) {
    for (ASTList aSTList = this; aSTList != null; aSTList = aSTList.right) {
      if (aSTList.left == paramASTree2) {
        aSTList.left = paramASTree1;
        return true;
      } 
    } 
    return false;
  }
  
  public static ASTList append(ASTList paramASTList, ASTree paramASTree) {
    return concat(paramASTList, new ASTList(paramASTree));
  }
  
  public static ASTList concat(ASTList paramASTList1, ASTList paramASTList2) {
    if (paramASTList1 == null)
      return paramASTList2; 
    ASTList aSTList = paramASTList1;
    while (aSTList.right != null)
      aSTList = aSTList.right; 
    aSTList.right = paramASTList2;
    return paramASTList1;
  }
}
