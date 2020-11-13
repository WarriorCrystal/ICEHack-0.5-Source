package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class ByteMemberValue extends MemberValue {
  int valueIndex;
  
  public ByteMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('B', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public ByteMemberValue(byte paramByte, ConstPool14 paramConstPool14) {
    super('B', paramConstPool14);
    setValue(paramByte);
  }
  
  public ByteMemberValue(ConstPool14 paramConstPool14) {
    super('B', paramConstPool14);
    setValue((byte)0);
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return new Byte(getValue());
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return byte.class;
  }
  
  public byte getValue() {
    return (byte)this.cp.getIntegerInfo(this.valueIndex);
  }
  
  public void setValue(byte paramByte) {
    this.valueIndex = this.cp.addIntegerInfo(paramByte);
  }
  
  public String toString() {
    return Byte.toString(getValue());
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitByteMemberValue(this);
  }
}
