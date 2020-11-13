package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class MethodDecl extends ASTList {
  public static final String initName = "<init>";
  
  public MethodDecl(ASTree paramASTree, ASTList paramASTList) {
    super(paramASTree, paramASTList);
  }
  
  public boolean isConstructor() {
    Symbol symbol = getReturn().getVariable();
    return (symbol != null && "<init>".equals(symbol.get()));
  }
  
  public ASTList getModifiers() {
    return (ASTList)getLeft();
  }
  
  public Declarator getReturn() {
    return (Declarator)tail().head();
  }
  
  public ASTList getParams() {
    return (ASTList)sublist(2).head();
  }
  
  public ASTList getThrows() {
    return (ASTList)sublist(3).head();
  }
  
  public Stmnt getBody() {
    return (Stmnt)sublist(4).head();
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atMethodDecl(this);
  }
}
