package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class InnerClassesAttribute extends AttributeInfo {
  public static final String tag = "InnerClasses";
  
  InnerClassesAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  private InnerClassesAttribute(ConstPool14 paramConstPool14, byte[] paramArrayOfbyte) {
    super(paramConstPool14, "InnerClasses", paramArrayOfbyte);
  }
  
  public InnerClassesAttribute(ConstPool14 paramConstPool14) {
    super(paramConstPool14, "InnerClasses", new byte[2]);
    ByteArray.write16bit(0, get(), 0);
  }
  
  public int tableLength() {
    return ByteArray.readU16bit(get(), 0);
  }
  
  public int innerClassIndex(int paramInt) {
    return ByteArray.readU16bit(get(), paramInt * 8 + 2);
  }
  
  public String innerClass(int paramInt) {
    int i = innerClassIndex(paramInt);
    if (i == 0)
      return null; 
    return this.constPool.getClassInfo(i);
  }
  
  public void setInnerClassIndex(int paramInt1, int paramInt2) {
    ByteArray.write16bit(paramInt2, get(), paramInt1 * 8 + 2);
  }
  
  public int outerClassIndex(int paramInt) {
    return ByteArray.readU16bit(get(), paramInt * 8 + 4);
  }
  
  public String outerClass(int paramInt) {
    int i = outerClassIndex(paramInt);
    if (i == 0)
      return null; 
    return this.constPool.getClassInfo(i);
  }
  
  public void setOuterClassIndex(int paramInt1, int paramInt2) {
    ByteArray.write16bit(paramInt2, get(), paramInt1 * 8 + 4);
  }
  
  public int innerNameIndex(int paramInt) {
    return ByteArray.readU16bit(get(), paramInt * 8 + 6);
  }
  
  public String innerName(int paramInt) {
    int i = innerNameIndex(paramInt);
    if (i == 0)
      return null; 
    return this.constPool.getUtf8Info(i);
  }
  
  public void setInnerNameIndex(int paramInt1, int paramInt2) {
    ByteArray.write16bit(paramInt2, get(), paramInt1 * 8 + 6);
  }
  
  public int accessFlags(int paramInt) {
    return ByteArray.readU16bit(get(), paramInt * 8 + 8);
  }
  
  public void setAccessFlags(int paramInt1, int paramInt2) {
    ByteArray.write16bit(paramInt2, get(), paramInt1 * 8 + 8);
  }
  
  public void append(String paramString1, String paramString2, String paramString3, int paramInt) {
    int i = this.constPool.addClassInfo(paramString1);
    int j = this.constPool.addClassInfo(paramString2);
    int k = this.constPool.addUtf8Info(paramString3);
    append(i, j, k, paramInt);
  }
  
  public void append(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    byte[] arrayOfByte1 = get();
    int i = arrayOfByte1.length;
    byte[] arrayOfByte2 = new byte[i + 8];
    int j;
    for (j = 2; j < i; j++)
      arrayOfByte2[j] = arrayOfByte1[j]; 
    j = ByteArray.readU16bit(arrayOfByte1, 0);
    ByteArray.write16bit(j + 1, arrayOfByte2, 0);
    ByteArray.write16bit(paramInt1, arrayOfByte2, i);
    ByteArray.write16bit(paramInt2, arrayOfByte2, i + 2);
    ByteArray.write16bit(paramInt3, arrayOfByte2, i + 4);
    ByteArray.write16bit(paramInt4, arrayOfByte2, i + 6);
    set(arrayOfByte2);
  }
  
  public int remove(int paramInt) {
    byte[] arrayOfByte1 = get();
    int i = arrayOfByte1.length;
    if (i < 10)
      return 0; 
    int j = ByteArray.readU16bit(arrayOfByte1, 0);
    int k = 2 + paramInt * 8;
    if (j <= paramInt)
      return j; 
    byte[] arrayOfByte2 = new byte[i - 8];
    ByteArray.write16bit(j - 1, arrayOfByte2, 0);
    byte b1 = 2, b2 = 2;
    while (b1 < i) {
      if (b1 == k) {
        b1 += 8;
        continue;
      } 
      arrayOfByte2[b2++] = arrayOfByte1[b1++];
    } 
    set(arrayOfByte2);
    return j - 1;
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    byte[] arrayOfByte1 = get();
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
    ConstPool14 constPool14 = getConstPool();
    InnerClassesAttribute innerClassesAttribute = new InnerClassesAttribute(paramConstPool14, arrayOfByte2);
    int i = ByteArray.readU16bit(arrayOfByte1, 0);
    ByteArray.write16bit(i, arrayOfByte2, 0);
    byte b1 = 2;
    for (byte b2 = 0; b2 < i; b2++) {
      int j = ByteArray.readU16bit(arrayOfByte1, b1);
      int k = ByteArray.readU16bit(arrayOfByte1, b1 + 2);
      int m = ByteArray.readU16bit(arrayOfByte1, b1 + 4);
      int n = ByteArray.readU16bit(arrayOfByte1, b1 + 6);
      if (j != 0)
        j = constPool14.copy(j, paramConstPool14, paramMap); 
      ByteArray.write16bit(j, arrayOfByte2, b1);
      if (k != 0)
        k = constPool14.copy(k, paramConstPool14, paramMap); 
      ByteArray.write16bit(k, arrayOfByte2, b1 + 2);
      if (m != 0)
        m = constPool14.copy(m, paramConstPool14, paramMap); 
      ByteArray.write16bit(m, arrayOfByte2, b1 + 4);
      ByteArray.write16bit(n, arrayOfByte2, b1 + 6);
      b1 += 8;
    } 
    return innerClassesAttribute;
  }
}
