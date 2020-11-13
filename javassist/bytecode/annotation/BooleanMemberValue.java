package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class BooleanMemberValue extends MemberValue {
  int valueIndex;
  
  public BooleanMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('Z', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public BooleanMemberValue(boolean paramBoolean, ConstPool14 paramConstPool14) {
    super('Z', paramConstPool14);
    setValue(paramBoolean);
  }
  
  public BooleanMemberValue(ConstPool14 paramConstPool14) {
    super('Z', paramConstPool14);
    setValue(false);
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return new Boolean(getValue());
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return boolean.class;
  }
  
  public boolean getValue() {
    return (this.cp.getIntegerInfo(this.valueIndex) != 0);
  }
  
  public void setValue(boolean paramBoolean) {
    this.valueIndex = this.cp.addIntegerInfo(paramBoolean ? 1 : 0);
  }
  
  public String toString() {
    return getValue() ? "true" : "false";
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitBooleanMemberValue(this);
  }
}
