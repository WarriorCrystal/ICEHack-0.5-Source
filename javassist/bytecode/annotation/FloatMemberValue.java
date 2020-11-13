package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class FloatMemberValue extends MemberValue {
  int valueIndex;
  
  public FloatMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('F', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public FloatMemberValue(float paramFloat, ConstPool14 paramConstPool14) {
    super('F', paramConstPool14);
    setValue(paramFloat);
  }
  
  public FloatMemberValue(ConstPool14 paramConstPool14) {
    super('F', paramConstPool14);
    setValue(0.0F);
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return new Float(getValue());
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return float.class;
  }
  
  public float getValue() {
    return this.cp.getFloatInfo(this.valueIndex);
  }
  
  public void setValue(float paramFloat) {
    this.valueIndex = this.cp.addFloatInfo(paramFloat);
  }
  
  public String toString() {
    return Float.toString(getValue());
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitFloatMemberValue(this);
  }
}
