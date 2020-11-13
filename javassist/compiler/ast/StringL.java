package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class StringL extends ASTree {
  protected String text;
  
  public StringL(String paramString) {
    this.text = paramString;
  }
  
  public String get() {
    return this.text;
  }
  
  public String toString() {
    return "\"" + this.text + "\"";
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atStringL(this);
  }
}
