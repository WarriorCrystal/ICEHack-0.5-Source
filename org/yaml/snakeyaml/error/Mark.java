package org.yaml.snakeyaml.error;

import java.io.Serializable;
import org.yaml.snakeyaml.scanner.Constant;

public final class Mark implements Serializable {
  private String name;
  
  private int index;
  
  private int line;
  
  private int column;
  
  private String buffer;
  
  private int pointer;
  
  public Mark(String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2, int paramInt4) {
    this.name = paramString1;
    this.index = paramInt1;
    this.line = paramInt2;
    this.column = paramInt3;
    this.buffer = paramString2;
    this.pointer = paramInt4;
  }
  
  private boolean isLineBreak(int paramInt) {
    return Constant.NULL_OR_LINEBR.has(paramInt);
  }
  
  public String get_snippet(int paramInt1, int paramInt2) {
    if (this.buffer == null)
      return null; 
    float f = (paramInt2 / 2 - 1);
    int i = this.pointer;
    String str1 = "";
    while (i > 0 && !isLineBreak(this.buffer.codePointAt(i - 1))) {
      i--;
      if ((this.pointer - i) > f) {
        str1 = " ... ";
        i += 5;
        break;
      } 
    } 
    String str2 = "";
    int j = this.pointer;
    while (j < this.buffer.length() && !isLineBreak(this.buffer.codePointAt(j))) {
      j++;
      if ((j - this.pointer) > f) {
        str2 = " ... ";
        j -= 5;
        break;
      } 
    } 
    String str3 = this.buffer.substring(i, j);
    StringBuilder stringBuilder = new StringBuilder();
    byte b;
    for (b = 0; b < paramInt1; b++)
      stringBuilder.append(" "); 
    stringBuilder.append(str1);
    stringBuilder.append(str3);
    stringBuilder.append(str2);
    stringBuilder.append("\n");
    for (b = 0; b < paramInt1 + this.pointer - i + str1.length(); b++)
      stringBuilder.append(" "); 
    stringBuilder.append("^");
    return stringBuilder.toString();
  }
  
  public String get_snippet() {
    return get_snippet(4, 75);
  }
  
  public String toString() {
    String str = get_snippet();
    StringBuilder stringBuilder = new StringBuilder(" in ");
    stringBuilder.append(this.name);
    stringBuilder.append(", line ");
    stringBuilder.append(this.line + 1);
    stringBuilder.append(", column ");
    stringBuilder.append(this.column + 1);
    if (str != null) {
      stringBuilder.append(":\n");
      stringBuilder.append(str);
    } 
    return stringBuilder.toString();
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getLine() {
    return this.line;
  }
  
  public int getColumn() {
    return this.column;
  }
  
  public int getIndex() {
    return this.index;
  }
}
