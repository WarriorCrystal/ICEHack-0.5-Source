package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class CharMemberValue extends MemberValue {
  int valueIndex;
  
  public CharMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('C', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public CharMemberValue(char paramChar, ConstPool14 paramConstPool14) {
    super('C', paramConstPool14);
    setValue(paramChar);
  }
  
  public CharMemberValue(ConstPool14 paramConstPool14) {
    super('C', paramConstPool14);
    setValue(false);
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) {
    return new Character(getValue());
  }
  
  Class getType(ClassLoader paramClassLoader) {
    return char.class;
  }
  
  public char getValue() {
    return (char)this.cp.getIntegerInfo(this.valueIndex);
  }
  
  public void setValue(char paramChar) {
    this.valueIndex = this.cp.addIntegerInfo(paramChar);
  }
  
  public String toString() {
    return Character.toString(getValue());
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.constValueIndex(getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitCharMemberValue(this);
  }
}
