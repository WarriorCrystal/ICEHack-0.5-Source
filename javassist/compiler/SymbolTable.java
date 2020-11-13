package javassist.compiler;

import java.util.HashMap;
import javassist.compiler.ast.Declarator;

public final class SymbolTable extends HashMap {
  private SymbolTable parent;
  
  public SymbolTable() {
    this((SymbolTable)null);
  }
  
  public SymbolTable(SymbolTable paramSymbolTable) {
    this.parent = paramSymbolTable;
  }
  
  public SymbolTable getParent() {
    return this.parent;
  }
  
  public Declarator lookup(String paramString) {
    Declarator declarator = (Declarator)get(paramString);
    if (declarator == null && this.parent != null)
      return this.parent.lookup(paramString); 
    return declarator;
  }
  
  public void append(String paramString, Declarator paramDeclarator) {
    put((K)paramString, (V)paramDeclarator);
  }
}
