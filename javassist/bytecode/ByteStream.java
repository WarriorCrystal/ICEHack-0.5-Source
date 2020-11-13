package javassist.bytecode;

import java.io.IOException;
import java.io.OutputStream;

final class ByteStream extends OutputStream {
  private byte[] buf;
  
  private int count;
  
  public ByteStream() {
    this(32);
  }
  
  public ByteStream(int paramInt) {
    this.buf = new byte[paramInt];
    this.count = 0;
  }
  
  public int getPos() {
    return this.count;
  }
  
  public int size() {
    return this.count;
  }
  
  public void writeBlank(int paramInt) {
    enlarge(paramInt);
    this.count += paramInt;
  }
  
  public void write(byte[] paramArrayOfbyte) {
    write(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    enlarge(paramInt2);
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.count, paramInt2);
    this.count += paramInt2;
  }
  
  public void write(int paramInt) {
    enlarge(1);
    int i = this.count;
    this.buf[i] = (byte)paramInt;
    this.count = i + 1;
  }
  
  public void writeShort(int paramInt) {
    enlarge(2);
    int i = this.count;
    this.buf[i] = (byte)(paramInt >>> 8);
    this.buf[i + 1] = (byte)paramInt;
    this.count = i + 2;
  }
  
  public void writeInt(int paramInt) {
    enlarge(4);
    int i = this.count;
    this.buf[i] = (byte)(paramInt >>> 24);
    this.buf[i + 1] = (byte)(paramInt >>> 16);
    this.buf[i + 2] = (byte)(paramInt >>> 8);
    this.buf[i + 3] = (byte)paramInt;
    this.count = i + 4;
  }
  
  public void writeLong(long paramLong) {
    enlarge(8);
    int i = this.count;
    this.buf[i] = (byte)(int)(paramLong >>> 56L);
    this.buf[i + 1] = (byte)(int)(paramLong >>> 48L);
    this.buf[i + 2] = (byte)(int)(paramLong >>> 40L);
    this.buf[i + 3] = (byte)(int)(paramLong >>> 32L);
    this.buf[i + 4] = (byte)(int)(paramLong >>> 24L);
    this.buf[i + 5] = (byte)(int)(paramLong >>> 16L);
    this.buf[i + 6] = (byte)(int)(paramLong >>> 8L);
    this.buf[i + 7] = (byte)(int)paramLong;
    this.count = i + 8;
  }
  
  public void writeFloat(float paramFloat) {
    writeInt(Float.floatToIntBits(paramFloat));
  }
  
  public void writeDouble(double paramDouble) {
    writeLong(Double.doubleToLongBits(paramDouble));
  }
  
  public void writeUTF(String paramString) {
    int i = paramString.length();
    int j = this.count;
    enlarge(i + 2);
    byte[] arrayOfByte = this.buf;
    arrayOfByte[j++] = (byte)(i >>> 8);
    arrayOfByte[j++] = (byte)i;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if ('\001' <= c && c <= '') {
        arrayOfByte[j++] = (byte)c;
      } else {
        writeUTF2(paramString, i, b);
        return;
      } 
    } 
    this.count = j;
  }
  
  private void writeUTF2(String paramString, int paramInt1, int paramInt2) {
    int i = paramInt1;
    int j;
    for (j = paramInt2; j < paramInt1; j++) {
      char c = paramString.charAt(j);
      if (c > '߿') {
        i += 2;
      } else if (c == '\000' || c > '') {
        i++;
      } 
    } 
    if (i > 65535)
      throw new RuntimeException("encoded string too long: " + paramInt1 + i + " bytes"); 
    enlarge(i + 2);
    j = this.count;
    byte[] arrayOfByte = this.buf;
    arrayOfByte[j] = (byte)(i >>> 8);
    arrayOfByte[j + 1] = (byte)i;
    j += 2 + paramInt2;
    for (int k = paramInt2; k < paramInt1; k++) {
      char c = paramString.charAt(k);
      if ('\001' <= c && c <= '') {
        arrayOfByte[j++] = (byte)c;
      } else if (c > '߿') {
        arrayOfByte[j] = (byte)(0xE0 | c >> 12 & 0xF);
        arrayOfByte[j + 1] = (byte)(0x80 | c >> 6 & 0x3F);
        arrayOfByte[j + 2] = (byte)(0x80 | c & 0x3F);
        j += 3;
      } else {
        arrayOfByte[j] = (byte)(0xC0 | c >> 6 & 0x1F);
        arrayOfByte[j + 1] = (byte)(0x80 | c & 0x3F);
        j += 2;
      } 
    } 
    this.count = j;
  }
  
  public void write(int paramInt1, int paramInt2) {
    this.buf[paramInt1] = (byte)paramInt2;
  }
  
  public void writeShort(int paramInt1, int paramInt2) {
    this.buf[paramInt1] = (byte)(paramInt2 >>> 8);
    this.buf[paramInt1 + 1] = (byte)paramInt2;
  }
  
  public void writeInt(int paramInt1, int paramInt2) {
    this.buf[paramInt1] = (byte)(paramInt2 >>> 24);
    this.buf[paramInt1 + 1] = (byte)(paramInt2 >>> 16);
    this.buf[paramInt1 + 2] = (byte)(paramInt2 >>> 8);
    this.buf[paramInt1 + 3] = (byte)paramInt2;
  }
  
  public byte[] toByteArray() {
    byte[] arrayOfByte = new byte[this.count];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
    return arrayOfByte;
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write(this.buf, 0, this.count);
  }
  
  public void enlarge(int paramInt) {
    int i = this.count + paramInt;
    if (i > this.buf.length) {
      int j = this.buf.length << 1;
      byte[] arrayOfByte = new byte[(j > i) ? j : i];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
      this.buf = arrayOfByte;
    } 
  }
}
