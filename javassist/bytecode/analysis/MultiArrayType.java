package javassist.bytecode.analysis;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class MultiArrayType extends Type {
  private MultiType component;
  
  private int dims;
  
  public MultiArrayType(MultiType paramMultiType, int paramInt) {
    super(null);
    this.component = paramMultiType;
    this.dims = paramInt;
  }
  
  public CtClass getCtClass() {
    CtClass ctClass = this.component.getCtClass();
    if (ctClass == null)
      return null; 
    ClassPool classPool = ctClass.getClassPool();
    if (classPool == null)
      classPool = ClassPool.getDefault(); 
    String str = arrayName(ctClass.getName(), this.dims);
    try {
      return classPool.get(str);
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException);
    } 
  }
  
  boolean popChanged() {
    return this.component.popChanged();
  }
  
  public int getDimensions() {
    return this.dims;
  }
  
  public Type getComponent() {
    return (this.dims == 1) ? this.component : new MultiArrayType(this.component, this.dims - 1);
  }
  
  public int getSize() {
    return 1;
  }
  
  public boolean isArray() {
    return true;
  }
  
  public boolean isAssignableFrom(Type paramType) {
    throw new UnsupportedOperationException("Not implemented");
  }
  
  public boolean isReference() {
    return true;
  }
  
  public boolean isAssignableTo(Type paramType) {
    if (eq(paramType.getCtClass(), Type.OBJECT.getCtClass()))
      return true; 
    if (eq(paramType.getCtClass(), Type.CLONEABLE.getCtClass()))
      return true; 
    if (eq(paramType.getCtClass(), Type.SERIALIZABLE.getCtClass()))
      return true; 
    if (!paramType.isArray())
      return false; 
    Type type = getRootComponent(paramType);
    int i = paramType.getDimensions();
    if (i > this.dims)
      return false; 
    if (i < this.dims) {
      if (eq(type.getCtClass(), Type.OBJECT.getCtClass()))
        return true; 
      if (eq(type.getCtClass(), Type.CLONEABLE.getCtClass()))
        return true; 
      if (eq(type.getCtClass(), Type.SERIALIZABLE.getCtClass()))
        return true; 
      return false;
    } 
    return this.component.isAssignableTo(type);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof MultiArrayType))
      return false; 
    MultiArrayType multiArrayType = (MultiArrayType)paramObject;
    return (this.component.equals(multiArrayType.component) && this.dims == multiArrayType.dims);
  }
  
  public String toString() {
    return arrayName(this.component.toString(), this.dims);
  }
}
