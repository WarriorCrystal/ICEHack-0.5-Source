package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class ShortMemberValue extends MemberValue {
  int valueIndex;
  
  public ShortMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('S', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public ShortMemberValue(short paramShort, ConstPool14 paramConstPool14) {
    super('S', paramConstPool14);
    setValue(paramShort);
  }
  
  public ShortMemberValue(ConstPool14 paramConstPool14) {
    super('S', paramConstPool14);
    setValue((short)0);
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return new Short(getValue());
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return short.class;
  }
  
  public short getValue() {
    return (short)this.cp.getIntegerInfo(this.valueIndex);
  }
  
  public void setValue(short paramShort) {
    this.valueIndex = this.cp.addIntegerInfo(paramShort);
  }
  
  public String toString() {
    return Short.toString(getValue());
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitShortMemberValue(this);
  }
}
