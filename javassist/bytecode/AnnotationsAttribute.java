package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class AnnotationsAttribute extends AttributeInfo {
  public static final String visibleTag = "RuntimeVisibleAnnotations";
  
  public static final String invisibleTag = "RuntimeInvisibleAnnotations";
  
  public AnnotationsAttribute(ConstPool14 paramConstPool14, String paramString, byte[] paramArrayOfbyte) {
    super(paramConstPool14, paramString, paramArrayOfbyte);
  }
  
  public AnnotationsAttribute(ConstPool14 paramConstPool14, String paramString) {
    this(paramConstPool14, paramString, new byte[] { 0, 0 });
  }
  
  AnnotationsAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public int numAnnotations() {
    return ByteArray.readU16bit(this.info, 0);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    Copier copier = new Copier(this.info, this.constPool, paramConstPool14, paramMap);
    try {
      copier.annotationArray();
      return new AnnotationsAttribute(paramConstPool14, getName(), copier.close());
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  public Annotation getAnnotation(String paramString) {
    Annotation[] arrayOfAnnotation = getAnnotations();
    for (byte b = 0; b < arrayOfAnnotation.length; b++) {
      if (arrayOfAnnotation[b].getTypeName().equals(paramString))
        return arrayOfAnnotation[b]; 
    } 
    return null;
  }
  
  public void addAnnotation(Annotation paramAnnotation) {
    String str = paramAnnotation.getTypeName();
    Annotation[] arrayOfAnnotation1 = getAnnotations();
    for (byte b = 0; b < arrayOfAnnotation1.length; b++) {
      if (arrayOfAnnotation1[b].getTypeName().equals(str)) {
        arrayOfAnnotation1[b] = paramAnnotation;
        setAnnotations(arrayOfAnnotation1);
        return;
      } 
    } 
    Annotation[] arrayOfAnnotation2 = new Annotation[arrayOfAnnotation1.length + 1];
    System.arraycopy(arrayOfAnnotation1, 0, arrayOfAnnotation2, 0, arrayOfAnnotation1.length);
    arrayOfAnnotation2[arrayOfAnnotation1.length] = paramAnnotation;
    setAnnotations(arrayOfAnnotation2);
  }
  
  public boolean removeAnnotation(String paramString) {
    Annotation[] arrayOfAnnotation = getAnnotations();
    for (byte b = 0; b < arrayOfAnnotation.length; b++) {
      if (arrayOfAnnotation[b].getTypeName().equals(paramString)) {
        Annotation[] arrayOfAnnotation1 = new Annotation[arrayOfAnnotation.length - 1];
        System.arraycopy(arrayOfAnnotation, 0, arrayOfAnnotation1, 0, b);
        if (b < arrayOfAnnotation.length - 1)
          System.arraycopy(arrayOfAnnotation, b + 1, arrayOfAnnotation1, b, arrayOfAnnotation.length - b - 1); 
        setAnnotations(arrayOfAnnotation1);
        return true;
      } 
    } 
    return false;
  }
  
  public Annotation[] getAnnotations() {
    try {
      return (new Parser(this.info, this.constPool)).parseAnnotations();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  public void setAnnotations(Annotation[] paramArrayOfAnnotation) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    AnnotationsWriter annotationsWriter = new AnnotationsWriter(byteArrayOutputStream, this.constPool);
    try {
      int i = paramArrayOfAnnotation.length;
      annotationsWriter.numAnnotations(i);
      for (byte b = 0; b < i; b++)
        paramArrayOfAnnotation[b].write(annotationsWriter); 
      annotationsWriter.close();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    set(byteArrayOutputStream.toByteArray());
  }
  
  public void setAnnotation(Annotation paramAnnotation) {
    setAnnotations(new Annotation[] { paramAnnotation });
  }
  
  void renameClass(String paramString1, String paramString2) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put(paramString1, paramString2);
    renameClass(hashMap);
  }
  
  void renameClass(Map paramMap) {
    Renamer renamer = new Renamer(this.info, getConstPool(), paramMap);
    try {
      renamer.annotationArray();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  void getRefClasses(Map paramMap) {
    renameClass(paramMap);
  }
  
  public String toString() {
    Annotation[] arrayOfAnnotation = getAnnotations();
    StringBuilder stringBuilder = new StringBuilder();
    byte b = 0;
    while (b < arrayOfAnnotation.length) {
      stringBuilder.append(arrayOfAnnotation[b++].toString());
      if (b != arrayOfAnnotation.length)
        stringBuilder.append(", "); 
    } 
    return stringBuilder.toString();
  }
  
  static class Walker {
    byte[] info;
    
    Walker(byte[] param1ArrayOfbyte) {
      this.info = param1ArrayOfbyte;
    }
    
    final void parameters() throws Exception {
      int i = this.info[0] & 0xFF;
      parameters(i, 1);
    }
    
    void parameters(int param1Int1, int param1Int2) throws Exception {
      for (byte b = 0; b < param1Int1; b++)
        param1Int2 = annotationArray(param1Int2); 
    }
    
    final void annotationArray() throws Exception {
      annotationArray(0);
    }
    
    final int annotationArray(int param1Int) throws Exception {
      int i = ByteArray.readU16bit(this.info, param1Int);
      return annotationArray(param1Int + 2, i);
    }
    
    int annotationArray(int param1Int1, int param1Int2) throws Exception {
      for (byte b = 0; b < param1Int2; b++)
        param1Int1 = annotation(param1Int1); 
      return param1Int1;
    }
    
    final int annotation(int param1Int) throws Exception {
      int i = ByteArray.readU16bit(this.info, param1Int);
      int j = ByteArray.readU16bit(this.info, param1Int + 2);
      return annotation(param1Int + 4, i, j);
    }
    
    int annotation(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      for (byte b = 0; b < param1Int3; b++)
        param1Int1 = memberValuePair(param1Int1); 
      return param1Int1;
    }
    
    final int memberValuePair(int param1Int) throws Exception {
      int i = ByteArray.readU16bit(this.info, param1Int);
      return memberValuePair(param1Int + 2, i);
    }
    
    int memberValuePair(int param1Int1, int param1Int2) throws Exception {
      return memberValue(param1Int1);
    }
    
    final int memberValue(int param1Int) throws Exception {
      int i = this.info[param1Int] & 0xFF;
      if (i == 101) {
        int k = ByteArray.readU16bit(this.info, param1Int + 1);
        int m = ByteArray.readU16bit(this.info, param1Int + 3);
        enumMemberValue(param1Int, k, m);
        return param1Int + 5;
      } 
      if (i == 99) {
        int k = ByteArray.readU16bit(this.info, param1Int + 1);
        classMemberValue(param1Int, k);
        return param1Int + 3;
      } 
      if (i == 64)
        return annotationMemberValue(param1Int + 1); 
      if (i == 91) {
        int k = ByteArray.readU16bit(this.info, param1Int + 1);
        return arrayMemberValue(param1Int + 3, k);
      } 
      int j = ByteArray.readU16bit(this.info, param1Int + 1);
      constValueMember(i, j);
      return param1Int + 3;
    }
    
    void constValueMember(int param1Int1, int param1Int2) throws Exception {}
    
    void enumMemberValue(int param1Int1, int param1Int2, int param1Int3) throws Exception {}
    
    void classMemberValue(int param1Int1, int param1Int2) throws Exception {}
    
    int annotationMemberValue(int param1Int) throws Exception {
      return annotation(param1Int);
    }
    
    int arrayMemberValue(int param1Int1, int param1Int2) throws Exception {
      for (byte b = 0; b < param1Int2; b++)
        param1Int1 = memberValue(param1Int1); 
      return param1Int1;
    }
  }
  
  static class Renamer extends Walker {
    ConstPool14 cpool;
    
    Map classnames;
    
    Renamer(byte[] param1ArrayOfbyte, ConstPool14 param1ConstPool14, Map param1Map) {
      super(param1ArrayOfbyte);
      this.cpool = param1ConstPool14;
      this.classnames = param1Map;
    }
    
    int annotation(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      renameType(param1Int1 - 4, param1Int2);
      return super.annotation(param1Int1, param1Int2, param1Int3);
    }
    
    void enumMemberValue(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      renameType(param1Int1 + 1, param1Int2);
      super.enumMemberValue(param1Int1, param1Int2, param1Int3);
    }
    
    void classMemberValue(int param1Int1, int param1Int2) throws Exception {
      renameType(param1Int1 + 1, param1Int2);
      super.classMemberValue(param1Int1, param1Int2);
    }
    
    private void renameType(int param1Int1, int param1Int2) {
      String str1 = this.cpool.getUtf8Info(param1Int2);
      String str2 = Descriptor.rename(str1, this.classnames);
      if (!str1.equals(str2)) {
        int i = this.cpool.addUtf8Info(str2);
        ByteArray.write16bit(i, this.info, param1Int1);
      } 
    }
  }
  
  static class Copier extends Walker {
    ByteArrayOutputStream output;
    
    AnnotationsWriter writer;
    
    ConstPool14 srcPool;
    
    ConstPool14 destPool;
    
    Map classnames;
    
    Copier(byte[] param1ArrayOfbyte, ConstPool14 param1ConstPool141, ConstPool14 param1ConstPool142, Map param1Map) {
      this(param1ArrayOfbyte, param1ConstPool141, param1ConstPool142, param1Map, true);
    }
    
    Copier(byte[] param1ArrayOfbyte, ConstPool14 param1ConstPool141, ConstPool14 param1ConstPool142, Map param1Map, boolean param1Boolean) {
      super(param1ArrayOfbyte);
      this.output = new ByteArrayOutputStream();
      if (param1Boolean)
        this.writer = new AnnotationsWriter(this.output, param1ConstPool142); 
      this.srcPool = param1ConstPool141;
      this.destPool = param1ConstPool142;
      this.classnames = param1Map;
    }
    
    byte[] close() throws IOException {
      this.writer.close();
      return this.output.toByteArray();
    }
    
    void parameters(int param1Int1, int param1Int2) throws Exception {
      this.writer.numParameters(param1Int1);
      super.parameters(param1Int1, param1Int2);
    }
    
    int annotationArray(int param1Int1, int param1Int2) throws Exception {
      this.writer.numAnnotations(param1Int2);
      return super.annotationArray(param1Int1, param1Int2);
    }
    
    int annotation(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      this.writer.annotation(copyType(param1Int2), param1Int3);
      return super.annotation(param1Int1, param1Int2, param1Int3);
    }
    
    int memberValuePair(int param1Int1, int param1Int2) throws Exception {
      this.writer.memberValuePair(copy(param1Int2));
      return super.memberValuePair(param1Int1, param1Int2);
    }
    
    void constValueMember(int param1Int1, int param1Int2) throws Exception {
      this.writer.constValueIndex(param1Int1, copy(param1Int2));
      super.constValueMember(param1Int1, param1Int2);
    }
    
    void enumMemberValue(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      this.writer.enumConstValue(copyType(param1Int2), copy(param1Int3));
      super.enumMemberValue(param1Int1, param1Int2, param1Int3);
    }
    
    void classMemberValue(int param1Int1, int param1Int2) throws Exception {
      this.writer.classInfoIndex(copyType(param1Int2));
      super.classMemberValue(param1Int1, param1Int2);
    }
    
    int annotationMemberValue(int param1Int) throws Exception {
      this.writer.annotationValue();
      return super.annotationMemberValue(param1Int);
    }
    
    int arrayMemberValue(int param1Int1, int param1Int2) throws Exception {
      this.writer.arrayValue(param1Int2);
      return super.arrayMemberValue(param1Int1, param1Int2);
    }
    
    int copy(int param1Int) {
      return this.srcPool.copy(param1Int, this.destPool, this.classnames);
    }
    
    int copyType(int param1Int) {
      String str1 = this.srcPool.getUtf8Info(param1Int);
      String str2 = Descriptor.rename(str1, this.classnames);
      return this.destPool.addUtf8Info(str2);
    }
  }
  
  static class Parser extends Walker {
    ConstPool14 pool;
    
    Annotation[][] allParams;
    
    Annotation[] allAnno;
    
    Annotation currentAnno;
    
    MemberValue currentMember;
    
    Parser(byte[] param1ArrayOfbyte, ConstPool14 param1ConstPool14) {
      super(param1ArrayOfbyte);
      this.pool = param1ConstPool14;
    }
    
    Annotation[][] parseParameters() throws Exception {
      parameters();
      return this.allParams;
    }
    
    Annotation[] parseAnnotations() throws Exception {
      annotationArray();
      return this.allAnno;
    }
    
    MemberValue parseMemberValue() throws Exception {
      memberValue(0);
      return this.currentMember;
    }
    
    void parameters(int param1Int1, int param1Int2) throws Exception {
      Annotation[][] arrayOfAnnotation = new Annotation[param1Int1][];
      for (byte b = 0; b < param1Int1; b++) {
        param1Int2 = annotationArray(param1Int2);
        arrayOfAnnotation[b] = this.allAnno;
      } 
      this.allParams = arrayOfAnnotation;
    }
    
    int annotationArray(int param1Int1, int param1Int2) throws Exception {
      Annotation[] arrayOfAnnotation = new Annotation[param1Int2];
      for (byte b = 0; b < param1Int2; b++) {
        param1Int1 = annotation(param1Int1);
        arrayOfAnnotation[b] = this.currentAnno;
      } 
      this.allAnno = arrayOfAnnotation;
      return param1Int1;
    }
    
    int annotation(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      this.currentAnno = new Annotation(param1Int2, this.pool);
      return super.annotation(param1Int1, param1Int2, param1Int3);
    }
    
    int memberValuePair(int param1Int1, int param1Int2) throws Exception {
      param1Int1 = super.memberValuePair(param1Int1, param1Int2);
      this.currentAnno.addMemberValue(param1Int2, this.currentMember);
      return param1Int1;
    }
    
    void constValueMember(int param1Int1, int param1Int2) throws Exception {
      ByteMemberValue byteMemberValue;
      CharMemberValue charMemberValue;
      DoubleMemberValue doubleMemberValue;
      FloatMemberValue floatMemberValue;
      IntegerMemberValue integerMemberValue;
      LongMemberValue longMemberValue;
      ShortMemberValue shortMemberValue;
      BooleanMemberValue booleanMemberValue;
      StringMemberValue stringMemberValue;
      ConstPool14 constPool14 = this.pool;
      switch (param1Int1) {
        case 66:
          byteMemberValue = new ByteMemberValue(param1Int2, constPool14);
          break;
        case 67:
          charMemberValue = new CharMemberValue(param1Int2, constPool14);
          break;
        case 68:
          doubleMemberValue = new DoubleMemberValue(param1Int2, constPool14);
          break;
        case 70:
          floatMemberValue = new FloatMemberValue(param1Int2, constPool14);
          break;
        case 73:
          integerMemberValue = new IntegerMemberValue(param1Int2, constPool14);
          break;
        case 74:
          longMemberValue = new LongMemberValue(param1Int2, constPool14);
          break;
        case 83:
          shortMemberValue = new ShortMemberValue(param1Int2, constPool14);
          break;
        case 90:
          booleanMemberValue = new BooleanMemberValue(param1Int2, constPool14);
          break;
        case 115:
          stringMemberValue = new StringMemberValue(param1Int2, constPool14);
          break;
        default:
          throw new RuntimeException("unknown tag:" + param1Int1);
      } 
      this.currentMember = (MemberValue)stringMemberValue;
      super.constValueMember(param1Int1, param1Int2);
    }
    
    void enumMemberValue(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      this.currentMember = (MemberValue)new EnumMemberValue(param1Int2, param1Int3, this.pool);
      super.enumMemberValue(param1Int1, param1Int2, param1Int3);
    }
    
    void classMemberValue(int param1Int1, int param1Int2) throws Exception {
      this.currentMember = (MemberValue)new ClassMemberValue(param1Int2, this.pool);
      super.classMemberValue(param1Int1, param1Int2);
    }
    
    int annotationMemberValue(int param1Int) throws Exception {
      Annotation annotation = this.currentAnno;
      param1Int = super.annotationMemberValue(param1Int);
      this.currentMember = (MemberValue)new AnnotationMemberValue(this.currentAnno, this.pool);
      this.currentAnno = annotation;
      return param1Int;
    }
    
    int arrayMemberValue(int param1Int1, int param1Int2) throws Exception {
      ArrayMemberValue arrayMemberValue = new ArrayMemberValue(this.pool);
      MemberValue[] arrayOfMemberValue = new MemberValue[param1Int2];
      for (byte b = 0; b < param1Int2; b++) {
        param1Int1 = memberValue(param1Int1);
        arrayOfMemberValue[b] = this.currentMember;
      } 
      arrayMemberValue.setValue(arrayOfMemberValue);
      this.currentMember = (MemberValue)arrayMemberValue;
      return param1Int1;
    }
  }
}
