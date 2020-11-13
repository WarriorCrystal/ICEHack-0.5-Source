package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class ExceptionsAttribute extends AttributeInfo {
  public static final String tag = "Exceptions";
  
  ExceptionsAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  private ExceptionsAttribute(ConstPool14 paramConstPool14, ExceptionsAttribute paramExceptionsAttribute, Map paramMap) {
    super(paramConstPool14, "Exceptions");
    copyFrom(paramExceptionsAttribute, paramMap);
  }
  
  public ExceptionsAttribute(ConstPool14 paramConstPool14) {
    super(paramConstPool14, "Exceptions");
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[1] = 0;
    arrayOfByte[0] = 0;
    this.info = arrayOfByte;
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    return new ExceptionsAttribute(paramConstPool14, this, paramMap);
  }
  
  private void copyFrom(ExceptionsAttribute paramExceptionsAttribute, Map paramMap) {
    ConstPool14 constPool141 = paramExceptionsAttribute.constPool;
    ConstPool14 constPool142 = this.constPool;
    byte[] arrayOfByte1 = paramExceptionsAttribute.info;
    int i = arrayOfByte1.length;
    byte[] arrayOfByte2 = new byte[i];
    arrayOfByte2[0] = arrayOfByte1[0];
    arrayOfByte2[1] = arrayOfByte1[1];
    for (byte b = 2; b < i; b += 2) {
      int j = ByteArray.readU16bit(arrayOfByte1, b);
      ByteArray.write16bit(constPool141.copy(j, constPool142, paramMap), arrayOfByte2, b);
    } 
    this.info = arrayOfByte2;
  }
  
  public int[] getExceptionIndexes() {
    byte[] arrayOfByte = this.info;
    int i = arrayOfByte.length;
    if (i <= 2)
      return null; 
    int[] arrayOfInt = new int[i / 2 - 1];
    byte b1 = 0;
    for (byte b2 = 2; b2 < i; b2 += 2)
      arrayOfInt[b1++] = (arrayOfByte[b2] & 0xFF) << 8 | arrayOfByte[b2 + 1] & 0xFF; 
    return arrayOfInt;
  }
  
  public String[] getExceptions() {
    byte[] arrayOfByte = this.info;
    int i = arrayOfByte.length;
    if (i <= 2)
      return null; 
    String[] arrayOfString = new String[i / 2 - 1];
    byte b1 = 0;
    for (byte b2 = 2; b2 < i; b2 += 2) {
      int j = (arrayOfByte[b2] & 0xFF) << 8 | arrayOfByte[b2 + 1] & 0xFF;
      arrayOfString[b1++] = this.constPool.getClassInfo(j);
    } 
    return arrayOfString;
  }
  
  public void setExceptionIndexes(int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    byte[] arrayOfByte = new byte[i * 2 + 2];
    ByteArray.write16bit(i, arrayOfByte, 0);
    for (byte b = 0; b < i; b++)
      ByteArray.write16bit(paramArrayOfint[b], arrayOfByte, b * 2 + 2); 
    this.info = arrayOfByte;
  }
  
  public void setExceptions(String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    byte[] arrayOfByte = new byte[i * 2 + 2];
    ByteArray.write16bit(i, arrayOfByte, 0);
    for (byte b = 0; b < i; b++)
      ByteArray.write16bit(this.constPool.addClassInfo(paramArrayOfString[b]), arrayOfByte, b * 2 + 2); 
    this.info = arrayOfByte;
  }
  
  public int tableLength() {
    return this.info.length / 2 - 1;
  }
  
  public int getException(int paramInt) {
    int i = paramInt * 2 + 2;
    return (this.info[i] & 0xFF) << 8 | this.info[i + 1] & 0xFF;
  }
}
