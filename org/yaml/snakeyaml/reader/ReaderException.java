package org.yaml.snakeyaml.reader;

import org.yaml.snakeyaml.error.YAMLException;

public class ReaderException extends YAMLException {
  private static final long serialVersionUID = 8710781187529689083L;
  
  private final String name;
  
  private final int codePoint;
  
  private final int position;
  
  public ReaderException(String paramString1, int paramInt1, int paramInt2, String paramString2) {
    super(paramString2);
    this.name = paramString1;
    this.codePoint = paramInt2;
    this.position = paramInt1;
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getCodePoint() {
    return this.codePoint;
  }
  
  public int getPosition() {
    return this.position;
  }
  
  public String toString() {
    String str = new String(Character.toChars(this.codePoint));
    return "unacceptable code point '" + str + "' (0x" + 
      Integer.toHexString(this.codePoint).toUpperCase() + ") " + getMessage() + "\nin \"" + this.name + "\", position " + this.position;
  }
}
