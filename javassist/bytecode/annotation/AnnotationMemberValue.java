package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool14;

public class AnnotationMemberValue extends MemberValue {
  Annotation value;
  
  public AnnotationMemberValue(ConstPool14 paramConstPool14) {
    this(null, paramConstPool14);
  }
  
  public AnnotationMemberValue(Annotation paramAnnotation, ConstPool14 paramConstPool14) {
    super('@', paramConstPool14);
    this.value = paramAnnotation;
  }
  
  Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) throws ClassNotFoundException {
    return AnnotationImpl.make(paramClassLoader, getType(paramClassLoader), paramClassPool, this.value);
  }
  
  Class getType(ClassLoader paramClassLoader) throws ClassNotFoundException {
    if (this.value == null)
      throw new ClassNotFoundException("no type specified"); 
    return loadClass(paramClassLoader, this.value.getTypeName());
  }
  
  public Annotation getValue() {
    return this.value;
  }
  
  public void setValue(Annotation paramAnnotation) {
    this.value = paramAnnotation;
  }
  
  public String toString() {
    return this.value.toString();
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    paramAnnotationsWriter.annotationValue();
    this.value.write(paramAnnotationsWriter);
  }
  
  public void accept(MemberValueVisitor paramMemberValueVisitor) {
    paramMemberValueVisitor.visitAnnotationMemberValue(this);
  }
}
