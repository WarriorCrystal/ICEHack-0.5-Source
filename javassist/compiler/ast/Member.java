package javassist.compiler.ast;

import javassist.CtField;
import javassist.compiler.CompileError;

public class Member extends Symbol {
  private CtField field;
  
  public Member(String paramString) {
    super(paramString);
    this.field = null;
  }
  
  public void setField(CtField paramCtField) {
    this.field = paramCtField;
  }
  
  public CtField getField() {
    return this.field;
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atMember(this);
  }
}
