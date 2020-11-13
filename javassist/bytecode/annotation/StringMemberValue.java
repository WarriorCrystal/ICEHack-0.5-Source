package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class StringMemberValue extends MemberValue {
  int valueIndex;
  
  public StringMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('s', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public StringMemberValue(String paramString, ConstPool14 paramConstPool14) {
    super('s', paramConstPool14);
    setValue(paramString);
  }
  
  public StringMemberValue(ConstPool14 paramConstPool14) {
    super('s', paramConstPool14);
    setValue("");
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return getValue();
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return String.class;
  }
  
  public String getValue() {
    return this.cp.getUtf8Info(this.valueIndex);
  }
  
  public void setValue(String paramString) {
    this.valueIndex = this.cp.addUtf8Info(paramString);
  }
  
  public String toString() {
    return "\"" + getValue() + "\"";
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitStringMemberValue(this);
  }
}
