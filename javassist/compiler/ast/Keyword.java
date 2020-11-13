package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class Keyword extends ASTree {
  protected int tokenId;
  
  public Keyword(int paramInt) {
    this.tokenId = paramInt;
  }
  
  public int get() {
    return this.tokenId;
  }
  
  public String toString() {
    return "id:" + this.tokenId;
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atKeyword(this);
  }
}
