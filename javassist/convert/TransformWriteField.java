package javassist.convert;

import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;

public final class TransformWriteField extends TransformReadField {
  public TransformWriteField(Transformer paramTransformer, CtField paramCtField, String paramString1, String paramString2) {
    super(paramTransformer, paramCtField, paramString1, paramString2);
  }
  
  public int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool14 paramConstPool14) throws BadBytecode {
    int i = paramCodeIterator.byteAt(paramInt);
    if (i == 181 || i == 179) {
      int j = paramCodeIterator.u16bitAt(paramInt + 1);
      String str = isField(paramCtClass.getClassPool(), paramConstPool14, this.fieldClass, this.fieldname, this.isPrivate, j);
      if (str != null) {
        if (i == 179) {
          CodeAttribute codeAttribute = paramCodeIterator.get();
          paramCodeIterator.move(paramInt);
          char c = str.charAt(0);
          if (c == 'J' || c == 'D') {
            paramInt = paramCodeIterator.insertGap(3);
            paramCodeIterator.writeByte(1, paramInt);
            paramCodeIterator.writeByte(91, paramInt + 1);
            paramCodeIterator.writeByte(87, paramInt + 2);
            codeAttribute.setMaxStack(codeAttribute.getMaxStack() + 2);
          } else {
            paramInt = paramCodeIterator.insertGap(2);
            paramCodeIterator.writeByte(1, paramInt);
            paramCodeIterator.writeByte(95, paramInt + 1);
            codeAttribute.setMaxStack(codeAttribute.getMaxStack() + 1);
          } 
          paramInt = paramCodeIterator.next();
        } 
        int k = paramConstPool14.addClassInfo(this.methodClassname);
        String str1 = "(Ljava/lang/Object;" + str + ")V";
        int m = paramConstPool14.addMethodrefInfo(k, this.methodName, str1);
        paramCodeIterator.writeByte(184, paramInt);
        paramCodeIterator.write16bit(m, paramInt + 1);
      } 
    } 
    return paramInt;
  }
}
