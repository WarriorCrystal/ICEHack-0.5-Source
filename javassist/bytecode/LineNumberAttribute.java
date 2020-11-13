package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class LineNumberAttribute extends AttributeInfo {
  public static final String tag = "LineNumberTable";
  
  LineNumberAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  private LineNumberAttribute(ConstPool14 paramConstPool14, byte[] paramArrayOfbyte) {
    super(paramConstPool14, "LineNumberTable", paramArrayOfbyte);
  }
  
  public int tableLength() {
    return ByteArray.readU16bit(this.info, 0);
  }
  
  public int startPc(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 4 + 2);
  }
  
  public int lineNumber(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 4 + 4);
  }
  
  public int toLineNumber(int paramInt) {
    int i = tableLength();
    byte b = 0;
    for (; b < i; b++) {
      if (paramInt < startPc(b)) {
        if (b == 0)
          return lineNumber(0); 
        break;
      } 
    } 
    return lineNumber(b - 1);
  }
  
  public int toStartPc(int paramInt) {
    int i = tableLength();
    for (byte b = 0; b < i; b++) {
      if (paramInt == lineNumber(b))
        return startPc(b); 
    } 
    return -1;
  }
  
  public static class Pc {
    public int index;
    
    public int line;
  }
  
  public Pc toNearPc(int paramInt) {
    int i = tableLength();
    int j = 0;
    int k = 0;
    if (i > 0) {
      k = lineNumber(0) - paramInt;
      j = startPc(0);
    } 
    for (byte b = 1; b < i; b++) {
      int m = lineNumber(b) - paramInt;
      if ((m < 0 && m > k) || (m >= 0 && (m < k || k < 0))) {
        k = m;
        j = startPc(b);
      } 
    } 
    Pc pc = new Pc();
    pc.index = j;
    pc.line = paramInt + k;
    return pc;
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    byte[] arrayOfByte1 = this.info;
    int i = arrayOfByte1.length;
    byte[] arrayOfByte2 = new byte[i];
    for (byte b = 0; b < i; b++)
      arrayOfByte2[b] = arrayOfByte1[b]; 
    return new LineNumberAttribute(paramConstPool14, arrayOfByte2);
  }
  
  void shiftPc(int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = tableLength();
    for (byte b = 0; b < i; b++) {
      int j = b * 4 + 2;
      int k = ByteArray.readU16bit(this.info, j);
      if (k > paramInt1 || (paramBoolean && k == paramInt1))
        ByteArray.write16bit(k + paramInt2, this.info, j); 
    } 
  }
}
