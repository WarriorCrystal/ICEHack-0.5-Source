package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class Symbol extends ASTree {
  protected String identifier;
  
  public Symbol(String paramString) {
    this.identifier = paramString;
  }
  
  public String get() {
    return this.identifier;
  }
  
  public String toString() {
    return this.identifier;
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atSymbol(this);
  }
}
