package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.scanner.Constant;

public class StreamReader {
  private String name;
  
  private final Reader stream;
  
  private int pointer = 0;
  
  private boolean eof = true;
  
  private String buffer;
  
  private int index = 0;
  
  private int line = 0;
  
  private int column = 0;
  
  private char[] data;
  
  private static final int BUFFER_SIZE = 1025;
  
  public StreamReader(String paramString) {
    this.name = "'string'";
    this.buffer = "";
    checkPrintable(paramString);
    this.buffer = paramString + "\000";
    this.stream = null;
    this.eof = true;
    this.data = null;
  }
  
  public StreamReader(Reader paramReader) {
    this.name = "'reader'";
    this.buffer = "";
    this.stream = paramReader;
    this.eof = false;
    this.data = new char[1025];
    update();
  }
  
  void checkPrintable(String paramString) {
    int i = paramString.length();
    for (int j = 0; j < i; ) {
      int k = paramString.codePointAt(j);
      if (!isPrintable(k))
        throw new ReaderException(this.name, j, k, "special characters are not allowed"); 
      j += Character.charCount(k);
    } 
  }
  
  public static boolean isPrintable(String paramString) {
    int i = paramString.length();
    for (int j = 0; j < i; ) {
      int k = paramString.codePointAt(j);
      if (!isPrintable(k))
        return false; 
      j += Character.charCount(k);
    } 
    return true;
  }
  
  public static boolean isPrintable(int paramInt) {
    return ((paramInt >= 32 && paramInt <= 126) || paramInt == 9 || paramInt == 10 || paramInt == 13 || paramInt == 133 || (paramInt >= 160 && paramInt <= 55295) || (paramInt >= 57344 && paramInt <= 65533) || (paramInt >= 65536 && paramInt <= 1114111));
  }
  
  public Mark getMark() {
    return new Mark(this.name, this.index, this.line, this.column, this.buffer, this.pointer);
  }
  
  public void forward() {
    forward(1);
  }
  
  public void forward(int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      if (this.pointer == this.buffer.length())
        update(); 
      if (this.pointer == this.buffer.length())
        break; 
      int i = this.buffer.codePointAt(this.pointer);
      this.pointer += Character.charCount(i);
      this.index += Character.charCount(i);
      if (Constant.LINEBR.has(i) || (i == 13 && this.buffer.charAt(this.pointer) != '\n')) {
        this.line++;
        this.column = 0;
      } else if (i != 65279) {
        this.column++;
      } 
    } 
    if (this.pointer == this.buffer.length())
      update(); 
  }
  
  public int peek() {
    if (this.pointer == this.buffer.length())
      update(); 
    if (this.pointer == this.buffer.length())
      return -1; 
    return this.buffer.codePointAt(this.pointer);
  }
  
  public int peek(int paramInt) {
    int j, i = 0;
    byte b = 0;
    do {
      if (this.pointer + i == this.buffer.length())
        update(); 
      if (this.pointer + i == this.buffer.length())
        return -1; 
      j = this.buffer.codePointAt(this.pointer + i);
      i += Character.charCount(j);
      ++b;
    } while (b <= paramInt);
    return j;
  }
  
  public String prefix(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    byte b = 0;
    while (b < paramInt) {
      if (this.pointer + i == this.buffer.length())
        update(); 
      if (this.pointer + i == this.buffer.length())
        break; 
      int j = this.buffer.codePointAt(this.pointer + i);
      stringBuilder.appendCodePoint(j);
      i += Character.charCount(j);
      b++;
    } 
    return stringBuilder.toString();
  }
  
  public String prefixForward(int paramInt) {
    String str = prefix(paramInt);
    this.pointer += str.length();
    this.index += str.length();
    this.column += paramInt;
    return str;
  }
  
  private void update() {
    if (!this.eof) {
      this.buffer = this.buffer.substring(this.pointer);
      this.pointer = 0;
      try {
        boolean bool = false;
        int i = this.stream.read(this.data, 0, 1024);
        if (i > 0) {
          if (Character.isHighSurrogate(this.data[i - 1])) {
            int j = this.stream.read(this.data, i, 1);
            if (j != -1) {
              i += j;
            } else {
              bool = true;
            } 
          } 
          StringBuilder stringBuilder = (new StringBuilder(this.buffer.length() + i)).append(this.buffer).append(this.data, 0, i);
          if (bool) {
            this.eof = true;
            stringBuilder.append(false);
          } 
          this.buffer = stringBuilder.toString();
          checkPrintable(this.buffer);
        } else {
          this.eof = true;
          this.buffer += "\000";
        } 
      } catch (IOException iOException) {
        throw new YAMLException(iOException);
      } 
    } 
  }
  
  public int getColumn() {
    return this.column;
  }
  
  public Charset getEncoding() {
    return Charset.forName(((UnicodeReader)this.stream).getEncoding());
  }
  
  public int getIndex() {
    return this.index;
  }
  
  public int getLine() {
    return this.line;
  }
}
