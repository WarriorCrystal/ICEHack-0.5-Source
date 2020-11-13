package javassist.convert;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public abstract class Transformer implements Opcode {
  private Transformer next;
  
  public Transformer(Transformer paramTransformer) {
    this.next = paramTransformer;
  }
  
  public Transformer getNext() {
    return this.next;
  }
  
  public void initialize(ConstPool14 paramConstPool14, CodeAttribute paramCodeAttribute) {}
  
  public void initialize(ConstPool14 paramConstPool14, CtClass paramCtClass, MethodInfo paramMethodInfo) throws CannotCompileException {
    initialize(paramConstPool14, paramMethodInfo.getCodeAttribute());
  }
  
  public void clean() {}
  
  public abstract int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool14 paramConstPool14) throws CannotCompileException, BadBytecode;
  
  public int extraLocals() {
    return 0;
  }
  
  public int extraStack() {
    return 0;
  }
}
