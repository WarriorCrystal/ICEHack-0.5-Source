package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationsWriter;

public class ParameterAnnotationsAttribute extends AttributeInfo {
  public static final String visibleTag = "RuntimeVisibleParameterAnnotations";
  
  public static final String invisibleTag = "RuntimeInvisibleParameterAnnotations";
  
  public ParameterAnnotationsAttribute(ConstPool14 paramConstPool14, String paramString, byte[] paramArrayOfbyte) {
    super(paramConstPool14, paramString, paramArrayOfbyte);
  }
  
  public ParameterAnnotationsAttribute(ConstPool14 paramConstPool14, String paramString) {
    this(paramConstPool14, paramString, new byte[] { 0 });
  }
  
  ParameterAnnotationsAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public int numParameters() {
    return this.info[0] & 0xFF;
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    AnnotationsAttribute.Copier copier = new AnnotationsAttribute.Copier(this.info, this.constPool, paramConstPool14, paramMap);
    try {
      copier.parameters();
      return new ParameterAnnotationsAttribute(paramConstPool14, getName(), copier
          .close());
    } catch (Exception exception) {
      throw new RuntimeException(exception.toString());
    } 
  }
  
  public Annotation[][] getAnnotations() {
    try {
      return (new AnnotationsAttribute.Parser(this.info, this.constPool)).parseParameters();
    } catch (Exception exception) {
      throw new RuntimeException(exception.toString());
    } 
  }
  
  public void setAnnotations(Annotation[][] paramArrayOfAnnotation) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    AnnotationsWriter annotationsWriter = new AnnotationsWriter(byteArrayOutputStream, this.constPool);
    try {
      int i = paramArrayOfAnnotation.length;
      annotationsWriter.numParameters(i);
      for (byte b = 0; b < i; b++) {
        Annotation[] arrayOfAnnotation = paramArrayOfAnnotation[b];
        annotationsWriter.numAnnotations(arrayOfAnnotation.length);
        for (byte b1 = 0; b1 < arrayOfAnnotation.length; b1++)
          arrayOfAnnotation[b1].write(annotationsWriter); 
      } 
      annotationsWriter.close();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    set(byteArrayOutputStream.toByteArray());
  }
  
  void renameClass(String paramString1, String paramString2) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put(paramString1, paramString2);
    renameClass(hashMap);
  }
  
  void renameClass(Map paramMap) {
    AnnotationsAttribute.Renamer renamer = new AnnotationsAttribute.Renamer(this.info, getConstPool(), paramMap);
    try {
      renamer.parameters();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  void getRefClasses(Map paramMap) {
    renameClass(paramMap);
  }
  
  public String toString() {
    Annotation[][] arrayOfAnnotation = getAnnotations();
    StringBuilder stringBuilder = new StringBuilder();
    byte b = 0;
    while (b < arrayOfAnnotation.length) {
      Annotation[] arrayOfAnnotation1 = arrayOfAnnotation[b++];
      byte b1 = 0;
      while (b1 < arrayOfAnnotation1.length) {
        stringBuilder.append(arrayOfAnnotation1[b1++].toString());
        if (b1 != arrayOfAnnotation1.length)
          stringBuilder.append(" "); 
      } 
      if (b != arrayOfAnnotation.length)
        stringBuilder.append(", "); 
    } 
    return stringBuilder.toString();
  }
}
