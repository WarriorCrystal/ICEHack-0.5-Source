package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class IntegerMemberValue extends MemberValue {
  int valueIndex;
  
  public IntegerMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('I', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public IntegerMemberValue(ConstPool14 paramConstPool14, int paramInt) {
    super('I', paramConstPool14);
    setValue(paramInt);
  }
  
  public IntegerMemberValue(ConstPool14 paramConstPool14) {
    super('I', paramConstPool14);
    setValue(0);
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return new Integer(getValue());
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return int.class;
  }
  
  public int getValue() {
    return this.cp.getIntegerInfo(this.valueIndex);
  }
  
  public void setValue(int paramInt) {
    this.valueIndex = this.cp.addIntegerInfo(paramInt);
  }
  
  public String toString() {
    return Integer.toString(getValue());
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitIntegerMemberValue(this);
  }
}
