package org.yaml.snakeyaml.external.com.google.gdata.util.common.base;

public class PercentEscaper extends UnicodeEscaper {
  public static final String SAFECHARS_URLENCODER = "-_.*";
  
  public static final String SAFEPATHCHARS_URLENCODER = "-_.!~*'()@:$&,;=";
  
  public static final String SAFEQUERYSTRINGCHARS_URLENCODER = "-_.!~*'()@:$,;/?:";
  
  private static final char[] URI_ESCAPED_SPACE = new char[] { '+' };
  
  private static final char[] UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();
  
  private final boolean plusForSpace;
  
  private final boolean[] safeOctets;
  
  public PercentEscaper(String paramString, boolean paramBoolean) {
    if (paramString.matches(".*[0-9A-Za-z].*"))
      throw new IllegalArgumentException("Alphanumeric characters are always 'safe' and should not be explicitly specified"); 
    if (paramBoolean && paramString.contains(" "))
      throw new IllegalArgumentException("plusForSpace cannot be specified when space is a 'safe' character"); 
    if (paramString.contains("%"))
      throw new IllegalArgumentException("The '%' character cannot be specified as 'safe'"); 
    this.plusForSpace = paramBoolean;
    this.safeOctets = createSafeOctets(paramString);
  }
  
  private static boolean[] createSafeOctets(String paramString) {
    int i = 122;
    char[] arrayOfChar = paramString.toCharArray();
    for (char c : arrayOfChar)
      i = Math.max(c, i); 
    boolean[] arrayOfBoolean = new boolean[i + 1];
    byte b;
    for (b = 48; b <= 57; b++)
      arrayOfBoolean[b] = true; 
    for (b = 65; b <= 90; b++)
      arrayOfBoolean[b] = true; 
    for (b = 97; b <= 122; b++)
      arrayOfBoolean[b] = true; 
    for (char c : arrayOfChar)
      arrayOfBoolean[c] = true; 
    return arrayOfBoolean;
  }
  
  protected int nextEscapeIndex(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    for (; paramInt1 < paramInt2; paramInt1++) {
      char c = paramCharSequence.charAt(paramInt1);
      if (c >= this.safeOctets.length || !this.safeOctets[c])
        break; 
    } 
    return paramInt1;
  }
  
  public String escape(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c >= this.safeOctets.length || !this.safeOctets[c])
        return escapeSlow(paramString, b); 
    } 
    return paramString;
  }
  
  protected char[] escape(int paramInt) {
    if (paramInt < this.safeOctets.length && this.safeOctets[paramInt])
      return null; 
    if (paramInt == 32 && this.plusForSpace)
      return URI_ESCAPED_SPACE; 
    if (paramInt <= 127) {
      char[] arrayOfChar = new char[3];
      arrayOfChar[0] = '%';
      arrayOfChar[2] = UPPER_HEX_DIGITS[paramInt & 0xF];
      arrayOfChar[1] = UPPER_HEX_DIGITS[paramInt >>> 4];
      return arrayOfChar;
    } 
    if (paramInt <= 2047) {
      char[] arrayOfChar = new char[6];
      arrayOfChar[0] = '%';
      arrayOfChar[3] = '%';
      arrayOfChar[5] = UPPER_HEX_DIGITS[paramInt & 0xF];
      paramInt >>>= 4;
      arrayOfChar[4] = UPPER_HEX_DIGITS[0x8 | paramInt & 0x3];
      paramInt >>>= 2;
      arrayOfChar[2] = UPPER_HEX_DIGITS[paramInt & 0xF];
      paramInt >>>= 4;
      arrayOfChar[1] = UPPER_HEX_DIGITS[0xC | paramInt];
      return arrayOfChar;
    } 
    if (paramInt <= 65535) {
      char[] arrayOfChar = new char[9];
      arrayOfChar[0] = '%';
      arrayOfChar[1] = 'E';
      arrayOfChar[3] = '%';
      arrayOfChar[6] = '%';
      arrayOfChar[8] = UPPER_HEX_DIGITS[paramInt & 0xF];
      paramInt >>>= 4;
      arrayOfChar[7] = UPPER_HEX_DIGITS[0x8 | paramInt & 0x3];
      paramInt >>>= 2;
      arrayOfChar[5] = UPPER_HEX_DIGITS[paramInt & 0xF];
      paramInt >>>= 4;
      arrayOfChar[4] = UPPER_HEX_DIGITS[0x8 | paramInt & 0x3];
      paramInt >>>= 2;
      arrayOfChar[2] = UPPER_HEX_DIGITS[paramInt];
      return arrayOfChar;
    } 
    if (paramInt <= 1114111) {
      char[] arrayOfChar = new char[12];
      arrayOfChar[0] = '%';
      arrayOfChar[1] = 'F';
      arrayOfChar[3] = '%';
      arrayOfChar[6] = '%';
      arrayOfChar[9] = '%';
      arrayOfChar[11] = UPPER_HEX_DIGITS[paramInt & 0xF];
      paramInt >>>= 4;
      arrayOfChar[10] = UPPER_HEX_DIGITS[0x8 | paramInt & 0x3];
      paramInt >>>= 2;
      arrayOfChar[8] = UPPER_HEX_DIGITS[paramInt & 0xF];
      paramInt >>>= 4;
      arrayOfChar[7] = UPPER_HEX_DIGITS[0x8 | paramInt & 0x3];
      paramInt >>>= 2;
      arrayOfChar[5] = UPPER_HEX_DIGITS[paramInt & 0xF];
      paramInt >>>= 4;
      arrayOfChar[4] = UPPER_HEX_DIGITS[0x8 | paramInt & 0x3];
      paramInt >>>= 2;
      arrayOfChar[2] = UPPER_HEX_DIGITS[paramInt & 0x7];
      return arrayOfChar;
    } 
    throw new IllegalArgumentException("Invalid unicode character value " + paramInt);
  }
}
