package javassist.compiler.ast;

import javassist.compiler.CompileError;
import javassist.compiler.TokenId;

public class Declarator extends ASTList implements TokenId {
  protected int varType;
  
  protected int arrayDim;
  
  protected int localVar;
  
  protected String qualifiedClass;
  
  public Declarator(int paramInt1, int paramInt2) {
    super(null);
    this.varType = paramInt1;
    this.arrayDim = paramInt2;
    this.localVar = -1;
    this.qualifiedClass = null;
  }
  
  public Declarator(ASTList paramASTList, int paramInt) {
    super(null);
    this.varType = 307;
    this.arrayDim = paramInt;
    this.localVar = -1;
    this.qualifiedClass = astToClassName(paramASTList, '/');
  }
  
  public Declarator(int paramInt1, String paramString, int paramInt2, int paramInt3, Symbol paramSymbol) {
    super(null);
    this.varType = paramInt1;
    this.arrayDim = paramInt2;
    this.localVar = paramInt3;
    this.qualifiedClass = paramString;
    setLeft(paramSymbol);
    append(this, null);
  }
  
  public Declarator make(Symbol paramSymbol, int paramInt, ASTree paramASTree) {
    Declarator declarator = new Declarator(this.varType, this.arrayDim + paramInt);
    declarator.qualifiedClass = this.qualifiedClass;
    declarator.setLeft(paramSymbol);
    append(declarator, paramASTree);
    return declarator;
  }
  
  public int getType() {
    return this.varType;
  }
  
  public int getArrayDim() {
    return this.arrayDim;
  }
  
  public void addArrayDim(int paramInt) {
    this.arrayDim += paramInt;
  }
  
  public String getClassName() {
    return this.qualifiedClass;
  }
  
  public void setClassName(String paramString) {
    this.qualifiedClass = paramString;
  }
  
  public Symbol getVariable() {
    return (Symbol)getLeft();
  }
  
  public void setVariable(Symbol paramSymbol) {
    setLeft(paramSymbol);
  }
  
  public ASTree getInitializer() {
    ASTList aSTList = tail();
    if (aSTList != null)
      return aSTList.head(); 
    return null;
  }
  
  public void setLocalVar(int paramInt) {
    this.localVar = paramInt;
  }
  
  public int getLocalVar() {
    return this.localVar;
  }
  
  public String getTag() {
    return "decl";
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atDeclarator(this);
  }
  
  public static String astToClassName(ASTList paramASTList, char paramChar) {
    if (paramASTList == null)
      return null; 
    StringBuffer stringBuffer = new StringBuffer();
    astToClassName(stringBuffer, paramASTList, paramChar);
    return stringBuffer.toString();
  }
  
  private static void astToClassName(StringBuffer paramStringBuffer, ASTList paramASTList, char paramChar) {
    while (true) {
      ASTree aSTree = paramASTList.head();
      if (aSTree instanceof Symbol) {
        paramStringBuffer.append(((Symbol)aSTree).get());
      } else if (aSTree instanceof ASTList) {
        astToClassName(paramStringBuffer, (ASTList)aSTree, paramChar);
      } 
      paramASTList = paramASTList.tail();
      if (paramASTList == null)
        break; 
      paramStringBuffer.append(paramChar);
    } 
  }
}
