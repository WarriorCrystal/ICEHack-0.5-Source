package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class Pair extends ASTree {
  protected ASTree left;
  
  protected ASTree right;
  
  public Pair(ASTree paramASTree1, ASTree paramASTree2) {
    this.left = paramASTree1;
    this.right = paramASTree2;
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atPair(this);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(<Pair> ");
    stringBuffer.append((this.left == null) ? "<null>" : this.left.toString());
    stringBuffer.append(" . ");
    stringBuffer.append((this.right == null) ? "<null>" : this.right.toString());
    stringBuffer.append(')');
    return stringBuffer.toString();
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
    this.right = paramASTree;
  }
}
