package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.SignatureAttribute;

public class ClassMemberValue extends MemberValue {
  int valueIndex;
  
  public ClassMemberValue(int paramInt, ConstPool14 paramConstPool14) {
    super('c', paramConstPool14);
    this.valueIndex = paramInt;
  }
  
  public ClassMemberValue(String paramString, ConstPool14 paramConstPool14) {
    super('c', paramConstPool14);
    setValue(paramString);
  }
  
  public ClassMemberValue(ConstPool14 paramConstPool14) {
    super('c', paramConstPool14);
    setValue("java.lang.Class");
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) throws ClassNotFoundException {
    String str = getValue();
    if (str.equals("void"))
      return void.class; 
    if (str.equals("int"))
      return int.class; 
    if (str.equals("byte"))
      return byte.class; 
    if (str.equals("long"))
      return long.class; 
    if (str.equals("double"))
      return double.class; 
    if (str.equals("float"))
      return float.class; 
    if (str.equals("char"))
      return char.class; 
    if (str.equals("short"))
      return short.class; 
    if (str.equals("boolean"))
      return boolean.class; 
    return loadClass(paramClassLoader, str);
  }
  
  Class getType(ClassLoader paramClassLoader) throws ClassNotFoundException {
    return loadClass(paramClassLoader, "java.lang.Class");
  }
  
  public String getValue() {
    String str = this.cp.getUtf8Info(this.valueIndex);
    try {
      return SignatureAttribute.toTypeSignature(str).jvmTypeName();
    } catch (BadBytecode badBytecode) {
      throw new RuntimeException(badBytecode);
    } 
  }
  
  public void setValue(String paramString) {
    String str = Descriptor.of(paramString);
    this.valueIndex = this.cp.addUtf8Info(str);
  }
  
  public String toString() {
    return getValue().replace('$', '.') + ".class";
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.classInfoIndex(this.cp.getUtf8Info(this.valueIndex));
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitClassMemberValue(this);
  }
}
