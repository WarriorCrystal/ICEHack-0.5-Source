package javassist.convert;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;

public class TransformReadField extends Transformer {
  protected String fieldname;
  
  protected CtClass fieldClass;
  
  protected boolean isPrivate;
  
  protected String methodClassname;
  
  protected String methodName;
  
  public TransformReadField(Transformer paramTransformer, CtField paramCtField, String paramString1, String paramString2) {
    super(paramTransformer);
    this.fieldClass = paramCtField.getDeclaringClass();
    this.fieldname = paramCtField.getName();
    this.methodClassname = paramString1;
    this.methodName = paramString2;
    this.isPrivate = Modifier.isPrivate(paramCtField.getModifiers());
  }
  
  static String isField(ClassPool paramClassPool, ConstPool14 paramConstPool14, CtClass paramCtClass, String paramString, boolean paramBoolean, int paramInt) {
    if (!paramConstPool14.getFieldrefName(paramInt).equals(paramString))
      return null; 
    try {
      CtClass ctClass = paramClassPool.get(paramConstPool14.getFieldrefClassName(paramInt));
      if (ctClass == paramCtClass || (!paramBoolean && isFieldInSuper(ctClass, paramCtClass, paramString)))
        return paramConstPool14.getFieldrefType(paramInt); 
    } catch (NotFoundException notFoundException) {}
    return null;
  }
  
  static boolean isFieldInSuper(CtClass paramCtClass1, CtClass paramCtClass2, String paramString) {
    if (!paramCtClass1.subclassOf(paramCtClass2))
      return false; 
    try {
      CtField ctField = paramCtClass1.getField(paramString);
      return (ctField.getDeclaringClass() == paramCtClass2);
    } catch (NotFoundException notFoundException) {
      return false;
    } 
  }
  
  public int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool14 paramConstPool14) throws BadBytecode {
    int i = paramCodeIterator.byteAt(paramInt);
    if (i == 180 || i == 178) {
      int j = paramCodeIterator.u16bitAt(paramInt + 1);
      String str = isField(paramCtClass.getClassPool(), paramConstPool14, this.fieldClass, this.fieldname, this.isPrivate, j);
      if (str != null) {
        if (i == 178) {
          paramCodeIterator.move(paramInt);
          paramInt = paramCodeIterator.insertGap(1);
          paramCodeIterator.writeByte(1, paramInt);
          paramInt = paramCodeIterator.next();
        } 
        String str1 = "(Ljava/lang/Object;)" + str;
        int k = paramConstPool14.addClassInfo(this.methodClassname);
        int m = paramConstPool14.addMethodrefInfo(k, this.methodName, str1);
        paramCodeIterator.writeByte(184, paramInt);
        paramCodeIterator.write16bit(m, paramInt + 1);
        return paramInt;
      } 
    } 
    return paramInt;
  }
}
