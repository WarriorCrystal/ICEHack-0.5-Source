package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class UnicodeReader extends Reader {
  private static final Charset UTF8 = Charset.forName("UTF-8");
  
  private static final Charset UTF16BE = Charset.forName("UTF-16BE");
  
  private static final Charset UTF16LE = Charset.forName("UTF-16LE");
  
  PushbackInputStream internalIn;
  
  InputStreamReader internalIn2 = null;
  
  private static final int BOM_SIZE = 3;
  
  public UnicodeReader(InputStream paramInputStream) {
    this.internalIn = new PushbackInputStream(paramInputStream, 3);
  }
  
  public String getEncoding() {
    return this.internalIn2.getEncoding();
  }
  
  protected void init() throws IOException {
    Charset charset;
    int j;
    if (this.internalIn2 != null)
      return; 
    byte[] arrayOfByte = new byte[3];
    int i = this.internalIn.read(arrayOfByte, 0, arrayOfByte.length);
    if (arrayOfByte[0] == -17 && arrayOfByte[1] == -69 && arrayOfByte[2] == -65) {
      charset = UTF8;
      j = i - 3;
    } else if (arrayOfByte[0] == -2 && arrayOfByte[1] == -1) {
      charset = UTF16BE;
      j = i - 2;
    } else if (arrayOfByte[0] == -1 && arrayOfByte[1] == -2) {
      charset = UTF16LE;
      j = i - 2;
    } else {
      charset = UTF8;
      j = i;
    } 
    if (j > 0)
      this.internalIn.unread(arrayOfByte, i - j, j); 
    CharsetDecoder charsetDecoder = charset.newDecoder().onUnmappableCharacter(CodingErrorAction.REPORT);
    this.internalIn2 = new InputStreamReader(this.internalIn, charsetDecoder);
  }
  
  public void close() throws IOException {
    init();
    this.internalIn2.close();
  }
  
  public int read(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException {
    init();
    return this.internalIn2.read(paramArrayOfchar, paramInt1, paramInt2);
  }
}
