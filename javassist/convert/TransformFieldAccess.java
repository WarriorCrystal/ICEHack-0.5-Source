package javassist.convert;

import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;

public final class TransformFieldAccess extends Transformer {
  private String newClassname;
  
  private String newFieldname;
  
  private String fieldname;
  
  private CtClass fieldClass;
  
  private boolean isPrivate;
  
  private int newIndex;
  
  private ConstPool14 constPool;
  
  public TransformFieldAccess(Transformer paramTransformer, CtField paramCtField, String paramString1, String paramString2) {
    super(paramTransformer);
    this.fieldClass = paramCtField.getDeclaringClass();
    this.fieldname = paramCtField.getName();
    this.isPrivate = Modifier.isPrivate(paramCtField.getModifiers());
    this.newClassname = paramString1;
    this.newFieldname = paramString2;
    this.constPool = null;
  }
  
  public void initialize(ConstPool14 paramConstPool14, CodeAttribute paramCodeAttribute) {
    if (this.constPool != paramConstPool14)
      this.newIndex = 0; 
  }
  
  public int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool14 paramConstPool14) {
    int i = paramCodeIterator.byteAt(paramInt);
    if (i == 180 || i == 178 || i == 181 || i == 179) {
      int j = paramCodeIterator.u16bitAt(paramInt + 1);
      String str = TransformReadField.isField(paramCtClass.getClassPool(), paramConstPool14, this.fieldClass, this.fieldname, this.isPrivate, j);
      if (str != null) {
        if (this.newIndex == 0) {
          int k = paramConstPool14.addNameAndTypeInfo(this.newFieldname, str);
          this.newIndex = paramConstPool14.addFieldrefInfo(paramConstPool14
              .addClassInfo(this.newClassname), k);
          this.constPool = paramConstPool14;
        } 
        paramCodeIterator.write16bit(this.newIndex, paramInt + 1);
      } 
    } 
    return paramInt;
  }
}
