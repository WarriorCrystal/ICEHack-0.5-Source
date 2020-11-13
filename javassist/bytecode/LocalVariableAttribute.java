package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class LocalVariableAttribute extends AttributeInfo {
  public static final String tag = "LocalVariableTable";
  
  public static final String typeTag = "LocalVariableTypeTable";
  
  public LocalVariableAttribute(ConstPool14 paramConstPool14) {
    super(paramConstPool14, "LocalVariableTable", new byte[2]);
    ByteArray.write16bit(0, this.info, 0);
  }
  
  public LocalVariableAttribute(ConstPool14 paramConstPool14, String paramString) {
    super(paramConstPool14, paramString, new byte[2]);
    ByteArray.write16bit(0, this.info, 0);
  }
  
  LocalVariableAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  LocalVariableAttribute(ConstPool14 paramConstPool14, String paramString, byte[] paramArrayOfbyte) {
    super(paramConstPool14, paramString, paramArrayOfbyte);
  }
  
  public void addEntry(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    int i = this.info.length;
    byte[] arrayOfByte = new byte[i + 10];
    ByteArray.write16bit(tableLength() + 1, arrayOfByte, 0);
    for (byte b = 2; b < i; b++)
      arrayOfByte[b] = this.info[b]; 
    ByteArray.write16bit(paramInt1, arrayOfByte, i);
    ByteArray.write16bit(paramInt2, arrayOfByte, i + 2);
    ByteArray.write16bit(paramInt3, arrayOfByte, i + 4);
    ByteArray.write16bit(paramInt4, arrayOfByte, i + 6);
    ByteArray.write16bit(paramInt5, arrayOfByte, i + 8);
    this.info = arrayOfByte;
  }
  
  void renameClass(String paramString1, String paramString2) {
    ConstPool14 constPool14 = getConstPool();
    int i = tableLength();
    for (byte b = 0; b < i; b++) {
      int j = b * 10 + 2;
      int k = ByteArray.readU16bit(this.info, j + 6);
      if (k != 0) {
        String str = constPool14.getUtf8Info(k);
        str = renameEntry(str, paramString1, paramString2);
        ByteArray.write16bit(constPool14.addUtf8Info(str), this.info, j + 6);
      } 
    } 
  }
  
  String renameEntry(String paramString1, String paramString2, String paramString3) {
    return Descriptor.rename(paramString1, paramString2, paramString3);
  }
  
  void renameClass(Map paramMap) {
    ConstPool14 constPool14 = getConstPool();
    int i = tableLength();
    for (byte b = 0; b < i; b++) {
      int j = b * 10 + 2;
      int k = ByteArray.readU16bit(this.info, j + 6);
      if (k != 0) {
        String str = constPool14.getUtf8Info(k);
        str = renameEntry(str, paramMap);
        ByteArray.write16bit(constPool14.addUtf8Info(str), this.info, j + 6);
      } 
    } 
  }
  
  String renameEntry(String paramString, Map paramMap) {
    return Descriptor.rename(paramString, paramMap);
  }
  
  public void shiftIndex(int paramInt1, int paramInt2) {
    int i = this.info.length;
    for (byte b = 2; b < i; b += 10) {
      int j = ByteArray.readU16bit(this.info, b + 8);
      if (j >= paramInt1)
        ByteArray.write16bit(j + paramInt2, this.info, b + 8); 
    } 
  }
  
  public int tableLength() {
    return ByteArray.readU16bit(this.info, 0);
  }
  
  public int startPc(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 10 + 2);
  }
  
  public int codeLength(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 10 + 4);
  }
  
  void shiftPc(int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = tableLength();
    for (byte b = 0; b < i; b++) {
      int j = b * 10 + 2;
      int k = ByteArray.readU16bit(this.info, j);
      int m = ByteArray.readU16bit(this.info, j + 2);
      if (k > paramInt1 || (paramBoolean && k == paramInt1 && k != 0)) {
        ByteArray.write16bit(k + paramInt2, this.info, j);
      } else if (k + m > paramInt1 || (paramBoolean && k + m == paramInt1)) {
        ByteArray.write16bit(m + paramInt2, this.info, j + 2);
      } 
    } 
  }
  
  public int nameIndex(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 10 + 6);
  }
  
  public String variableName(int paramInt) {
    return getConstPool().getUtf8Info(nameIndex(paramInt));
  }
  
  public int descriptorIndex(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 10 + 8);
  }
  
  public int signatureIndex(int paramInt) {
    return descriptorIndex(paramInt);
  }
  
  public String descriptor(int paramInt) {
    return getConstPool().getUtf8Info(descriptorIndex(paramInt));
  }
  
  public String signature(int paramInt) {
    return descriptor(paramInt);
  }
  
  public int index(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 10 + 10);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    byte[] arrayOfByte1 = get();
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
    ConstPool14 constPool14 = getConstPool();
    LocalVariableAttribute localVariableAttribute = makeThisAttr(paramConstPool14, arrayOfByte2);
    int i = ByteArray.readU16bit(arrayOfByte1, 0);
    ByteArray.write16bit(i, arrayOfByte2, 0);
    byte b1 = 2;
    for (byte b2 = 0; b2 < i; b2++) {
      int j = ByteArray.readU16bit(arrayOfByte1, b1);
      int k = ByteArray.readU16bit(arrayOfByte1, b1 + 2);
      int m = ByteArray.readU16bit(arrayOfByte1, b1 + 4);
      int n = ByteArray.readU16bit(arrayOfByte1, b1 + 6);
      int i1 = ByteArray.readU16bit(arrayOfByte1, b1 + 8);
      ByteArray.write16bit(j, arrayOfByte2, b1);
      ByteArray.write16bit(k, arrayOfByte2, b1 + 2);
      if (m != 0)
        m = constPool14.copy(m, paramConstPool14, null); 
      ByteArray.write16bit(m, arrayOfByte2, b1 + 4);
      if (n != 0) {
        String str = constPool14.getUtf8Info(n);
        str = Descriptor.rename(str, paramMap);
        n = paramConstPool14.addUtf8Info(str);
      } 
      ByteArray.write16bit(n, arrayOfByte2, b1 + 6);
      ByteArray.write16bit(i1, arrayOfByte2, b1 + 8);
      b1 += 10;
    } 
    return localVariableAttribute;
  }
  
  LocalVariableAttribute makeThisAttr(ConstPool14 paramConstPool14, byte[] paramArrayOfbyte) {
    return new LocalVariableAttribute(paramConstPool14, "LocalVariableTable", paramArrayOfbyte);
  }
}
