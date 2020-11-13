package javassist.convert;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;

public final class TransformNewClass extends Transformer {
  private int nested;
  
  private String classname;
  
  private String newClassName;
  
  private int newClassIndex;
  
  private int newMethodNTIndex;
  
  private int newMethodIndex;
  
  public TransformNewClass(Transformer paramTransformer, String paramString1, String paramString2) {
    super(paramTransformer);
    this.classname = paramString1;
    this.newClassName = paramString2;
  }
  
  public void initialize(ConstPool14 paramConstPool14, CodeAttribute paramCodeAttribute) {
    this.nested = 0;
    this.newClassIndex = this.newMethodNTIndex = this.newMethodIndex = 0;
  }
  
  public int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool14 paramConstPool14) throws CannotCompileException {
    int i = paramCodeIterator.byteAt(paramInt);
    if (i == 187) {
      int j = paramCodeIterator.u16bitAt(paramInt + 1);
      if (paramConstPool14.getClassInfo(j).equals(this.classname)) {
        if (paramCodeIterator.byteAt(paramInt + 3) != 89)
          throw new CannotCompileException("NEW followed by no DUP was found"); 
        if (this.newClassIndex == 0)
          this.newClassIndex = paramConstPool14.addClassInfo(this.newClassName); 
        paramCodeIterator.write16bit(this.newClassIndex, paramInt + 1);
        this.nested++;
      } 
    } else if (i == 183) {
      int j = paramCodeIterator.u16bitAt(paramInt + 1);
      int k = paramConstPool14.isConstructor(this.classname, j);
      if (k != 0 && this.nested > 0) {
        int m = paramConstPool14.getMethodrefNameAndType(j);
        if (this.newMethodNTIndex != m) {
          this.newMethodNTIndex = m;
          this.newMethodIndex = paramConstPool14.addMethodrefInfo(this.newClassIndex, m);
        } 
        paramCodeIterator.write16bit(this.newMethodIndex, paramInt + 1);
        this.nested--;
      } 
    } 
    return paramInt;
  }
}
