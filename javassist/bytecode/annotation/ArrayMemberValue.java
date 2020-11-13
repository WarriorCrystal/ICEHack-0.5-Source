package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class ArrayMemberValue extends MemberValue {
  MemberValue type;
  
  MemberValue[] values;
  
  public ArrayMemberValue(ConstPool14 paramConstPool14) {
    super('[', paramConstPool14);
    this.type = null;
    this.values = null;
  }
  
  public ArrayMemberValue(MemberValue paramMemberValue, ConstPool14 paramConstPool14) {
    super('[', paramConstPool14);
    this.type = paramMemberValue;
    this.values = null;
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) throws ClassNotFoundException {
    Class<?> clazz;
    if (this.values == null)
      throw new ClassNotFoundException("no array elements found: " + paramMethod
          .getName()); 
    int i = this.values.length;
    if (this.type == null) {
      clazz = paramMethod.getReturnType().getComponentType();
      if (clazz == null || i > 0)
        throw new ClassNotFoundException("broken array type: " + paramMethod
            .getName()); 
    } else {
      clazz = this.type.getType(paramClassLoader);
    } 
    Object object = Array.newInstance(clazz, i);
    for (byte b = 0; b < i; b++)
      Array.set(object, b, this.values[b].getValue(paramClassLoader, paramClassPool, paramMethod)); 
    return object;
  }
  
  Class getType(ClassLoader paramClassLoader) throws ClassNotFoundException {
    if (this.type == null)
      throw new ClassNotFoundException("no array type specified"); 
    Object object = Array.newInstance(this.type.getType(paramClassLoader), 0);
    return object.getClass();
  }
  
  public MemberValue getType() {
    return this.type;
  }
  
  public MemberValue[] getValue() {
    return this.values;
  }
  
  public void setValue(MemberValue[] paramArrayOfMemberValue) {
    this.values = paramArrayOfMemberValue;
    if (paramArrayOfMemberValue != null && paramArrayOfMemberValue.length > 0)
      this.type = paramArrayOfMemberValue[0]; 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("{");
    if (this.values != null)
      for (byte b = 0; b < this.values.length; b++) {
        stringBuffer.append(this.values[b].toString());
        if (b + 1 < this.values.length)
          stringBuffer.append(", "); 
      }  
    stringBuffer.append("}");
    return stringBuffer.toString();
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    byte b1 = (this.values == null) ? 0 : this.values.length;
    paramAnnotationsWriter.arrayValue(b1);
    for (byte b2 = 0; b2 < b1; b2++)
      this.values[b2].write(paramAnnotationsWriter); 
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitArrayMemberValue(this);
  }
}
