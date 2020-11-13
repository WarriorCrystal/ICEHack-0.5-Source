package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.MemberValue;

public class AnnotationDefaultAttribute extends AttributeInfo {
  public static final String tag = "AnnotationDefault";
  
  public AnnotationDefaultAttribute(ConstPool14 paramConstPool14, byte[] paramArrayOfbyte) {
    super(paramConstPool14, "AnnotationDefault", paramArrayOfbyte);
  }
  
  public AnnotationDefaultAttribute(ConstPool14 paramConstPool14) {
    this(paramConstPool14, new byte[] { 0, 0 });
  }
  
  AnnotationDefaultAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    AnnotationsAttribute.Copier copier = new AnnotationsAttribute.Copier(this.info, this.constPool, paramConstPool14, paramMap);
    try {
      copier.memberValue(0);
      return new AnnotationDefaultAttribute(paramConstPool14, copier.close());
    } catch (Exception exception) {
      throw new RuntimeException(exception.toString());
    } 
  }
  
  public MemberValue getDefaultValue() {
    try {
      return (new AnnotationsAttribute.Parser(this.info, this.constPool)).parseMemberValue();
    } catch (Exception exception) {
      throw new RuntimeException(exception.toString());
    } 
  }
  
  public void setDefaultValue(MemberValue paramMemberValue) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    AnnotationsWriter annotationsWriter = new AnnotationsWriter(byteArrayOutputStream, this.constPool);
    try {
      paramMemberValue.write(annotationsWriter);
      annotationsWriter.close();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    set(byteArrayOutputStream.toByteArray());
  }
  
  public String toString() {
    return getDefaultValue().toString();
  }
}
