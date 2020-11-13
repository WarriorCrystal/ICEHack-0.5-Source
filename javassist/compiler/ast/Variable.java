package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class Variable extends Symbol {
  protected Declarator declarator;
  
  public Variable(String paramString, Declarator paramDeclarator) {
    super(paramString);
    this.declarator = paramDeclarator;
  }
  
  public Declarator getDeclarator() {
    return this.declarator;
  }
  
  public String toString() {
    return this.identifier + ":" + this.declarator.getType();
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atVariable(this);
  }
}
