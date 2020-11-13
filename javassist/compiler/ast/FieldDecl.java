package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class FieldDecl extends ASTList {
  public FieldDecl(ASTree paramASTree, ASTList paramASTList) {
    super(paramASTree, paramASTList);
  }
  
  public ASTList getModifiers() {
    return (ASTList)getLeft();
  }
  
  public Declarator getDeclarator() {
    return (Declarator)tail().head();
  }
  
  public ASTree getInit() {
    return sublist(2).head();
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atFieldDecl(this);
  }
}
