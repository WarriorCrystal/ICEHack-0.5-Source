package javassist.convert;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.StackMap;
import javassist.bytecode.StackMapTable;

public final class TransformNew extends Transformer {
  private int nested;
  
  private String classname;
  
  private String trapClass;
  
  private String trapMethod;
  
  public TransformNew(Transformer paramTransformer, String paramString1, String paramString2, String paramString3) {
    super(paramTransformer);
    this.classname = paramString1;
    this.trapClass = paramString2;
    this.trapMethod = paramString3;
  }
  
  public void initialize(ConstPool14 paramConstPool14, CodeAttribute paramCodeAttribute) {
    this.nested = 0;
  }
  
  public int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool14 paramConstPool14) throws CannotCompileException {
    int i = paramCodeIterator.byteAt(paramInt);
    if (i == 187) {
      int j = paramCodeIterator.u16bitAt(paramInt + 1);
      if (paramConstPool14.getClassInfo(j).equals(this.classname)) {
        if (paramCodeIterator.byteAt(paramInt + 3) != 89)
          throw new CannotCompileException("NEW followed by no DUP was found"); 
        paramCodeIterator.writeByte(0, paramInt);
        paramCodeIterator.writeByte(0, paramInt + 1);
        paramCodeIterator.writeByte(0, paramInt + 2);
        paramCodeIterator.writeByte(0, paramInt + 3);
        this.nested++;
        StackMapTable stackMapTable = (StackMapTable)paramCodeIterator.get().getAttribute("StackMapTable");
        if (stackMapTable != null)
          stackMapTable.removeNew(paramInt); 
        StackMap stackMap = (StackMap)paramCodeIterator.get().getAttribute("StackMap");
        if (stackMap != null)
          stackMap.removeNew(paramInt); 
      } 
    } else if (i == 183) {
      int j = paramCodeIterator.u16bitAt(paramInt + 1);
      int k = paramConstPool14.isConstructor(this.classname, j);
      if (k != 0 && this.nested > 0) {
        int m = computeMethodref(k, paramConstPool14);
        paramCodeIterator.writeByte(184, paramInt);
        paramCodeIterator.write16bit(m, paramInt + 1);
        this.nested--;
      } 
    } 
    return paramInt;
  }
  
  private int computeMethodref(int paramInt, ConstPool14 paramConstPool14) {
    int i = paramConstPool14.addClassInfo(this.trapClass);
    int j = paramConstPool14.addUtf8Info(this.trapMethod);
    paramInt = paramConstPool14.addUtf8Info(
        Descriptor.changeReturnType(this.classname, paramConstPool14
          .getUtf8Info(paramInt)));
    return paramConstPool14.addMethodrefInfo(i, paramConstPool14
        .addNameAndTypeInfo(j, paramInt));
  }
}
