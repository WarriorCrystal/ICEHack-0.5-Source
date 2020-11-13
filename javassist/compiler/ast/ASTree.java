package javassist.compiler.ast;

import java.io.Serializable;
import javassist.compiler.CompileError;

public abstract class ASTree implements Serializable {
  public ASTree getLeft() {
    return null;
  }
  
  public ASTree getRight() {
    return null;
  }
  
  public void setLeft(ASTree paramASTree) {}
  
  public void setRight(ASTree paramASTree) {}
  
  public abstract void accept(Visitor paramVisitor) throws CompileError;
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('<');
    stringBuffer.append(getTag());
    stringBuffer.append('>');
    return stringBuffer.toString();
  }
  
  protected String getTag() {
    String str = getClass().getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
}
