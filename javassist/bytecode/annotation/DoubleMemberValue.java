package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class DoubleMemberValue extends MemberValue {
  int valueIndex;
  
  public DoubleMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('D', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public DoubleMemberValue(double paramDouble, ConstPool14 paramConstPool14) {
    super('D', paramConstPool14);
    setValue(paramDouble);
  }
  
  public DoubleMemberValue(ConstPool14 paramConstPool14) {
    super('D', paramConstPool14);
    setValue(0.0D);
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return new Double(getValue());
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return double.class;
  }
  
  public double getValue() {
    return this.cp.getDoubleInfo(this.valueIndex);
  }
  
  public void setValue(double paramDouble) {
    this.valueIndex = this.cp.addDoubleInfo(paramDouble);
  }
  
  public String toString() {
    return Double.toString(getValue());
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitDoubleMemberValue(this);
  }
}
