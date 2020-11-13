package javassist.convert;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;

public class TransformBefore extends TransformCall {
  protected CtClass[] parameterTypes;
  
  protected int locals;
  
  protected int maxLocals;
  
  protected byte[] saveCode;
  
  protected byte[] loadCode;
  
  public TransformBefore(Transformer paramTransformer, CtMethod paramCtMethod1, CtMethod paramCtMethod2) throws NotFoundException {
    super(paramTransformer, paramCtMethod1, paramCtMethod2);
    this.methodDescriptor = paramCtMethod1.getMethodInfo2().getDescriptor();
    this.parameterTypes = paramCtMethod1.getParameterTypes();
    this.locals = 0;
    this.maxLocals = 0;
    this.saveCode = this.loadCode = null;
  }
  
  public void initialize(ConstPool14 paramConstPool14, CodeAttribute paramCodeAttribute) {
    super.initialize(paramConstPool14, paramCodeAttribute);
    this.locals = 0;
    this.maxLocals = paramCodeAttribute.getMaxLocals();
    this.saveCode = this.loadCode = null;
  }
  
  protected int match(int paramInt1, int paramInt2, CodeIterator paramCodeIterator, int paramInt3, ConstPool14 paramConstPool14) throws BadBytecode {
    if (this.newIndex == 0) {
      String str = Descriptor.ofParameters(this.parameterTypes) + 'V';
      str = Descriptor.insertParameter(this.classname, str);
      int i = paramConstPool14.addNameAndTypeInfo(this.newMethodname, str);
      int j = paramConstPool14.addClassInfo(this.newClassname);
      this.newIndex = paramConstPool14.addMethodrefInfo(j, i);
      this.constPool = paramConstPool14;
    } 
    if (this.saveCode == null)
      makeCode(this.parameterTypes, paramConstPool14); 
    return match2(paramInt2, paramCodeIterator);
  }
  
  protected int match2(int paramInt, CodeIterator paramCodeIterator) throws BadBytecode {
    paramCodeIterator.move(paramInt);
    paramCodeIterator.insert(this.saveCode);
    paramCodeIterator.insert(this.loadCode);
    int i = paramCodeIterator.insertGap(3);
    paramCodeIterator.writeByte(184, i);
    paramCodeIterator.write16bit(this.newIndex, i + 1);
    paramCodeIterator.insert(this.loadCode);
    return paramCodeIterator.next();
  }
  
  public int extraLocals() {
    return this.locals;
  }
  
  protected void makeCode(CtClass[] paramArrayOfCtClass, ConstPool14 paramConstPool14) {
    Bytecode1 bytecode11 = new Bytecode1(paramConstPool14, 0, 0);
    Bytecode1 bytecode12 = new Bytecode1(paramConstPool14, 0, 0);
    int i = this.maxLocals;
    boolean bool = (paramArrayOfCtClass == null) ? false : paramArrayOfCtClass.length;
    bytecode12.addAload(i);
    makeCode2(bytecode11, bytecode12, 0, bool, paramArrayOfCtClass, i + 1);
    bytecode11.addAstore(i);
    this.saveCode = bytecode11.get();
    this.loadCode = bytecode12.get();
  }
  
  private void makeCode2(Bytecode1 paramBytecode11, Bytecode1 paramBytecode12, int paramInt1, int paramInt2, CtClass[] paramArrayOfCtClass, int paramInt3) {
    if (paramInt1 < paramInt2) {
      int i = paramBytecode12.addLoad(paramInt3, paramArrayOfCtClass[paramInt1]);
      makeCode2(paramBytecode11, paramBytecode12, paramInt1 + 1, paramInt2, paramArrayOfCtClass, paramInt3 + i);
      paramBytecode11.addStore(paramInt3, paramArrayOfCtClass[paramInt1]);
    } else {
      this.locals = paramInt3 - this.maxLocals;
    } 
  }
}
