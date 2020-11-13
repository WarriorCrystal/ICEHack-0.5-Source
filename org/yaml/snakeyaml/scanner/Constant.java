package org.yaml.snakeyaml.scanner;

import java.util.Arrays;

public final class Constant {
  private static final String ALPHA_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
  
  private static final String LINEBR_S = "\n  ";
  
  private static final String FULL_LINEBR_S = "\r\n  ";
  
  private static final String NULL_OR_LINEBR_S = "\000\r\n  ";
  
  private static final String NULL_BL_LINEBR_S = " \000\r\n  ";
  
  private static final String NULL_BL_T_LINEBR_S = "\t \000\r\n  ";
  
  private static final String NULL_BL_T_S = "\000 \t";
  
  private static final String URI_CHARS_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$,_.!~*'()[]%";
  
  public static final Constant LINEBR = new Constant("\n  ");
  
  public static final Constant FULL_LINEBR = new Constant("\r\n  ");
  
  public static final Constant NULL_OR_LINEBR = new Constant("\000\r\n  ");
  
  public static final Constant NULL_BL_LINEBR = new Constant(" \000\r\n  ");
  
  public static final Constant NULL_BL_T_LINEBR = new Constant("\t \000\r\n  ");
  
  public static final Constant NULL_BL_T = new Constant("\000 \t");
  
  public static final Constant URI_CHARS = new Constant("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$,_.!~*'()[]%");
  
  public static final Constant ALPHA = new Constant("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_");
  
  private String content;
  
  boolean[] contains = new boolean[128];
  
  boolean noASCII = false;
  
  private Constant(String paramString) {
    Arrays.fill(this.contains, false);
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramString.length(); b++) {
      int i = paramString.codePointAt(b);
      if (i < 128) {
        this.contains[i] = true;
      } else {
        stringBuilder.appendCodePoint(i);
      } 
    } 
    if (stringBuilder.length() > 0) {
      this.noASCII = true;
      this.content = stringBuilder.toString();
    } 
  }
  
  public boolean has(int paramInt) {
    return (paramInt < 128) ? this.contains[paramInt] : ((this.noASCII && this.content.indexOf(paramInt, 0) != -1));
  }
  
  public boolean hasNo(int paramInt) {
    return !has(paramInt);
  }
  
  public boolean has(int paramInt, String paramString) {
    return (has(paramInt) || paramString.indexOf(paramInt, 0) != -1);
  }
  
  public boolean hasNo(int paramInt, String paramString) {
    return !has(paramInt, paramString);
  }
}
