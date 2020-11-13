package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class LongMemberValue extends MemberValue {
  int valueIndex;
  
  public LongMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('J', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public LongMemberValue(long paramLong, ConstPool14 paramConstPool14) {
    super('J', paramConstPool14);
    setValue(paramLong);
  }
  
  public LongMemberValue(ConstPool14 paramConstPool14) {
    super('J', paramConstPool14);
    setValue(0L);
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return new Long(getValue());
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return long.class;
  }
  
  public long getValue() {
    return this.cp.getLongInfo(this.valueIndex);
  }
  
  public void setValue(long paramLong) {
    this.valueIndex = this.cp.addLongInfo(paramLong);
  }
  
  public String toString() {
    return Long.toString(getValue());
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitLongMemberValue(this);
  }
}
