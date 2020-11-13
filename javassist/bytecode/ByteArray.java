package javassist.bytecode;

public class ByteArray {
  public static int readU16bit(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt] & 0xFF) << 8 | paramArrayOfbyte[paramInt + 1] & 0xFF;
  }
  
  public static int readS16bit(byte[] paramArrayOfbyte, int paramInt) {
    return paramArrayOfbyte[paramInt] << 8 | paramArrayOfbyte[paramInt + 1] & 0xFF;
  }
  
  public static void write16bit(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2 + 1] = (byte)paramInt1;
  }
  
  public static int read32bit(byte[] paramArrayOfbyte, int paramInt) {
    return paramArrayOfbyte[paramInt] << 24 | (paramArrayOfbyte[paramInt + 1] & 0xFF) << 16 | (paramArrayOfbyte[paramInt + 2] & 0xFF) << 8 | paramArrayOfbyte[paramInt + 3] & 0xFF;
  }
  
  public static void write32bit(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    paramArrayOfbyte[paramInt2] = (byte)(paramInt1 >>> 24);
    paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >>> 16);
    paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >>> 8);
    paramArrayOfbyte[paramInt2 + 3] = (byte)paramInt1;
  }
  
  static void copy32bit(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2) {
    paramArrayOfbyte2[paramInt2] = paramArrayOfbyte1[paramInt1];
    paramArrayOfbyte2[paramInt2 + 1] = paramArrayOfbyte1[paramInt1 + 1];
    paramArrayOfbyte2[paramInt2 + 2] = paramArrayOfbyte1[paramInt1 + 2];
    paramArrayOfbyte2[paramInt2 + 3] = paramArrayOfbyte1[paramInt1 + 3];
  }
}
