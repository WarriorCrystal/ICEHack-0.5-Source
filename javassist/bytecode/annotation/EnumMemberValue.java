package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;

public class EnumMemberValue extends MemberValue {
  int typeIndex;
  
  int valueIndex;
  
  public EnumMemberValue(int paramInt1, int paramInt2, ConstPool14 paramConstPool14) {
    super('e', paramConstPool14);
    this.typeIndex = paramInt1;
    this.valueIndex = paramInt2;
  }
  
  public EnumMemberValue(ConstPool14 paramConstPool14) {
    super('e', paramConstPool14);
    this.typeIndex = this.valueIndex = 0;
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) throws ClassNotFoundException {
    try {
      return getType(paramClassLoader).getField(getValue()).get(null);
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new ClassNotFoundException(getType() + "." + getValue());
    } catch (IllegalAccessException illegalAccessException) {
      throw new ClassNotFoundException(getType() + "." + getValue());
    } 
  }
  
  Class getType(ClassLoader paramClassLoader) throws ClassNotFoundException {
    return loadClass(paramClassLoader, getType());
  }
  
  public String getType() {
    return Descriptor.toClassName(this.cp.getUtf8Info(this.typeIndex));
  }
  
  public void setType(String paramString) {
    this.typeIndex = this.cp.addUtf8Info(Descriptor.of(paramString));
  }
  
  public String getValue() {
    return this.cp.getUtf8Info(this.valueIndex);
  }
  
  public void setValue(String paramString) {
    this.valueIndex = this.cp.addUtf8Info(paramString);
  }
  
  public String toString() {
    return getType() + "." + getValue();
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.enumConstValue(this.cp.getUtf8Info(this.typeIndex), getValue());
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitEnumMemberValue(this);
  }
}
