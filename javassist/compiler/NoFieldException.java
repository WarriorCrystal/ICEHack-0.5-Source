package javassist.compiler;

import javassist.compiler.ast.ASTree;

public class NoFieldException extends CompileError {
  private String fieldName;
  
  private ASTree expr;
  
  public NoFieldException(String paramString, ASTree paramASTree) {
    super("no such field: " + paramString);
    this.fieldName = paramString;
    this.expr = paramASTree;
  }
  
  public String getField() {
    return this.fieldName;
  }
  
  public ASTree getExpr() {
    return this.expr;
  }
}
