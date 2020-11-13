package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class EnclosingMethodAttribute extends AttributeInfo {
  public static final String tag = "EnclosingMethod";
  
  EnclosingMethodAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public EnclosingMethodAttribute(ConstPool14 paramConstPool14, String paramString1, String paramString2, String paramString3) {
    super(paramConstPool14, "EnclosingMethod");
    int i = paramConstPool14.addClassInfo(paramString1);
    int j = paramConstPool14.addNameAndTypeInfo(paramString2, paramString3);
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(i >>> 8);
    arrayOfByte[1] = (byte)i;
    arrayOfByte[2] = (byte)(j >>> 8);
    arrayOfByte[3] = (byte)j;
    set(arrayOfByte);
  }
  
  public EnclosingMethodAttribute(ConstPool14 paramConstPool14, String paramString) {
    super(paramConstPool14, "EnclosingMethod");
    int i = paramConstPool14.addClassInfo(paramString);
    byte b = 0;
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(i >>> 8);
    arrayOfByte[1] = (byte)i;
    arrayOfByte[2] = (byte)(b >>> 8);
    arrayOfByte[3] = (byte)b;
    set(arrayOfByte);
  }
  
  public int classIndex() {
    return ByteArray.readU16bit(get(), 0);
  }
  
  public int methodIndex() {
    return ByteArray.readU16bit(get(), 2);
  }
  
  public String className() {
    return getConstPool().getClassInfo(classIndex());
  }
  
  public String methodName() {
    ConstPool14 constPool14 = getConstPool();
    int i = methodIndex();
    if (i == 0)
      return "<clinit>"; 
    int j = constPool14.getNameAndTypeName(i);
    return constPool14.getUtf8Info(j);
  }
  
  public String methodDescriptor() {
    ConstPool14 constPool14 = getConstPool();
    int i = methodIndex();
    int j = constPool14.getNameAndTypeDescriptor(i);
    return constPool14.getUtf8Info(j);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    if (methodIndex() == 0)
      return new EnclosingMethodAttribute(paramConstPool14, className()); 
    return new EnclosingMethodAttribute(paramConstPool14, className(), 
        methodName(), methodDescriptor());
  }
}
