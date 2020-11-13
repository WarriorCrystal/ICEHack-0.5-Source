package javassist.bytecode.annotation;

import java.io.IOException;
import java.io.OutputStream;
import javassist.bytecode.ByteArray;
import javassist.bytecode.ConstPool14;

public class AnnotationsWriter {
  protected OutputStream output;
  
  private ConstPool14 pool;
  
  public AnnotationsWriter(OutputStream paramOutputStream, ConstPool14 paramConstPool14) {
    this.output = paramOutputStream;
    this.pool = paramConstPool14;
  }
  
  public ConstPool14 getConstPool() {
    return this.pool;
  }
  
  public void close() throws IOException {
    this.output.close();
  }
  
  public void numParameters(int paramInt) throws IOException {
    this.output.write(paramInt);
  }
  
  public void numAnnotations(int paramInt) throws IOException {
    write16bit(paramInt);
  }
  
  public void annotation(String paramString, int paramInt) throws IOException {
    annotation(this.pool.addUtf8Info(paramString), paramInt);
  }
  
  public void annotation(int paramInt1, int paramInt2) throws IOException {
    write16bit(paramInt1);
    write16bit(paramInt2);
  }
  
  public void memberValuePair(String paramString) throws IOException {
    memberValuePair(this.pool.addUtf8Info(paramString));
  }
  
  public void memberValuePair(int paramInt) throws IOException {
    write16bit(paramInt);
  }
  
  public void constValueIndex(boolean paramBoolean) throws IOException {
    constValueIndex(90, this.pool.addIntegerInfo(paramBoolean ? 1 : 0));
  }
  
  public void constValueIndex(byte paramByte) throws IOException {
    constValueIndex(66, this.pool.addIntegerInfo(paramByte));
  }
  
  public void constValueIndex(char paramChar) throws IOException {
    constValueIndex(67, this.pool.addIntegerInfo(paramChar));
  }
  
  public void constValueIndex(short paramShort) throws IOException {
    constValueIndex(83, this.pool.addIntegerInfo(paramShort));
  }
  
  public void constValueIndex(int paramInt) throws IOException {
    constValueIndex(73, this.pool.addIntegerInfo(paramInt));
  }
  
  public void constValueIndex(long paramLong) throws IOException {
    constValueIndex(74, this.pool.addLongInfo(paramLong));
  }
  
  public void constValueIndex(float paramFloat) throws IOException {
    constValueIndex(70, this.pool.addFloatInfo(paramFloat));
  }
  
  public void constValueIndex(double paramDouble) throws IOException {
    constValueIndex(68, this.pool.addDoubleInfo(paramDouble));
  }
  
  public void constValueIndex(String paramString) throws IOException {
    constValueIndex(115, this.pool.addUtf8Info(paramString));
  }
  
  public void constValueIndex(int paramInt1, int paramInt2) throws IOException {
    this.output.write(paramInt1);
    write16bit(paramInt2);
  }
  
  public void enumConstValue(String paramString1, String paramString2) throws IOException {
    enumConstValue(this.pool.addUtf8Info(paramString1), this.pool
        .addUtf8Info(paramString2));
  }
  
  public void enumConstValue(int paramInt1, int paramInt2) throws IOException {
    this.output.write(101);
    write16bit(paramInt1);
    write16bit(paramInt2);
  }
  
  public void classInfoIndex(String paramString) throws IOException {
    classInfoIndex(this.pool.addUtf8Info(paramString));
  }
  
  public void classInfoIndex(int paramInt) throws IOException {
    this.output.write(99);
    write16bit(paramInt);
  }
  
  public void annotationValue() throws IOException {
    this.output.write(64);
  }
  
  public void arrayValue(int paramInt) throws IOException {
    this.output.write(91);
    write16bit(paramInt);
  }
  
  protected void write16bit(int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[2];
    ByteArray.write16bit(paramInt, arrayOfByte, 0);
    this.output.write(arrayOfByte);
  }
}
