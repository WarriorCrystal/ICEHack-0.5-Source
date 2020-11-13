package javassist.bytecode.annotation;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;

public class Annotation {
  ConstPool14 pool;
  
  int typeIndex;
  
  LinkedHashMap members;
  
  static class Pair {
    int name;
    
    MemberValue value;
  }
  
  public Annotation(int paramInt, ConstPool14 paramConstPool14) {
    this.pool = paramConstPool14;
    this.typeIndex = paramInt;
    this.members = null;
  }
  
  public Annotation(String paramString, ConstPool14 paramConstPool14) {
    this(paramConstPool14.addUtf8Info(Descriptor.of(paramString)), paramConstPool14);
  }
  
  public Annotation(ConstPool14 paramConstPool14, CtClass paramCtClass) throws NotFoundException {
    this(paramConstPool14.addUtf8Info(Descriptor.of(paramCtClass.getName())), paramConstPool14);
    if (!paramCtClass.isInterface())
      throw new RuntimeException("Only interfaces are allowed for Annotation creation."); 
    CtMethod[] arrayOfCtMethod = paramCtClass.getDeclaredMethods();
    if (arrayOfCtMethod.length > 0)
      this.members = new LinkedHashMap<Object, Object>(); 
    for (byte b = 0; b < arrayOfCtMethod.length; b++) {
      CtClass ctClass = arrayOfCtMethod[b].getReturnType();
      addMemberValue(arrayOfCtMethod[b].getName(), 
          createMemberValue(paramConstPool14, ctClass));
    } 
  }
  
  public static MemberValue createMemberValue(ConstPool14 paramConstPool14, CtClass paramCtClass) throws NotFoundException {
    if (paramCtClass == CtClass.booleanType)
      return new BooleanMemberValue(paramConstPool14); 
    if (paramCtClass == CtClass.byteType)
      return new ByteMemberValue(paramConstPool14); 
    if (paramCtClass == CtClass.charType)
      return new CharMemberValue(paramConstPool14); 
    if (paramCtClass == CtClass.shortType)
      return new ShortMemberValue(paramConstPool14); 
    if (paramCtClass == CtClass.intType)
      return new IntegerMemberValue(paramConstPool14); 
    if (paramCtClass == CtClass.longType)
      return new LongMemberValue(paramConstPool14); 
    if (paramCtClass == CtClass.floatType)
      return new FloatMemberValue(paramConstPool14); 
    if (paramCtClass == CtClass.doubleType)
      return new DoubleMemberValue(paramConstPool14); 
    if (paramCtClass.getName().equals("java.lang.Class"))
      return new ClassMemberValue(paramConstPool14); 
    if (paramCtClass.getName().equals("java.lang.String"))
      return new StringMemberValue(paramConstPool14); 
    if (paramCtClass.isArray()) {
      CtClass ctClass = paramCtClass.getComponentType();
      MemberValue memberValue = createMemberValue(paramConstPool14, ctClass);
      return new ArrayMemberValue(memberValue, paramConstPool14);
    } 
    if (paramCtClass.isInterface()) {
      Annotation annotation = new Annotation(paramConstPool14, paramCtClass);
      return new AnnotationMemberValue(annotation, paramConstPool14);
    } 
    EnumMemberValue enumMemberValue = new EnumMemberValue(paramConstPool14);
    enumMemberValue.setType(paramCtClass.getName());
    return enumMemberValue;
  }
  
  public void addMemberValue(int paramInt, MemberValue paramMemberValue) {
    Pair pair = new Pair();
    pair.name = paramInt;
    pair.value = paramMemberValue;
    addMemberValue(pair);
  }
  
  public void addMemberValue(String paramString, MemberValue paramMemberValue) {
    Pair pair = new Pair();
    pair.name = this.pool.addUtf8Info(paramString);
    pair.value = paramMemberValue;
    if (this.members == null)
      this.members = new LinkedHashMap<Object, Object>(); 
    this.members.put(paramString, pair);
  }
  
  private void addMemberValue(Pair paramPair) {
    String str = this.pool.getUtf8Info(paramPair.name);
    if (this.members == null)
      this.members = new LinkedHashMap<Object, Object>(); 
    this.members.put(str, paramPair);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("@");
    stringBuffer.append(getTypeName());
    if (this.members != null) {
      stringBuffer.append("(");
      Iterator<String> iterator = this.members.keySet().iterator();
      while (iterator.hasNext()) {
        String str = iterator.next();
        stringBuffer.append(str).append("=").append(getMemberValue(str));
        if (iterator.hasNext())
          stringBuffer.append(", "); 
      } 
      stringBuffer.append(")");
    } 
    return stringBuffer.toString();
  }
  
  public String getTypeName() {
    return Descriptor.toClassName(this.pool.getUtf8Info(this.typeIndex));
  }
  
  public Set getMemberNames() {
    if (this.members == null)
      return null; 
    return this.members.keySet();
  }
  
  public MemberValue getMemberValue(String paramString) {
    if (this.members == null)
      return null; 
    Pair pair = (Pair)this.members.get(paramString);
    if (pair == null)
      return null; 
    return pair.value;
  }
  
  public Object toAnnotationType(ClassLoader paramClassLoader, ClassPool paramClassPool) throws ClassNotFoundException, NoSuchClassError {
    return AnnotationImpl.make(paramClassLoader, 
        MemberValue.loadClass(paramClassLoader, getTypeName()), paramClassPool, this);
  }
  
  public void write(AnnotationsWriter paramAnnotationsWriter) throws IOException {
    String str = this.pool.getUtf8Info(this.typeIndex);
    if (this.members == null) {
      paramAnnotationsWriter.annotation(str, 0);
      return;
    } 
    paramAnnotationsWriter.annotation(str, this.members.size());
    Iterator<Pair> iterator = this.members.values().iterator();
    while (iterator.hasNext()) {
      Pair pair = iterator.next();
      paramAnnotationsWriter.memberValuePair(pair.name);
      pair.value.write(paramAnnotationsWriter);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject == null || !(paramObject instanceof Annotation))
      return false; 
    Annotation annotation = (Annotation)paramObject;
    if (!getTypeName().equals(annotation.getTypeName()))
      return false; 
    LinkedHashMap linkedHashMap = annotation.members;
    if (this.members == linkedHashMap)
      return true; 
    if (this.members == null)
      return (linkedHashMap == null); 
    if (linkedHashMap == null)
      return false; 
    return this.members.equals(linkedHashMap);
  }
}
