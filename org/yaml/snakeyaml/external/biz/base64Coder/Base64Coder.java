package org.yaml.snakeyaml.external.biz.base64Coder;

public class Base64Coder {
  private static final String systemLineSeparator = System.getProperty("line.separator");
  
  private static char[] map1 = new char[64];
  
  static {
    byte b = 0;
    char c;
    for (c = 'A'; c <= 'Z'; c = (char)(c + 1))
      map1[b++] = c; 
    for (c = 'a'; c <= 'z'; c = (char)(c + 1))
      map1[b++] = c; 
    for (c = '0'; c <= '9'; c = (char)(c + 1))
      map1[b++] = c; 
    map1[b++] = '+';
    map1[b++] = '/';
  }
  
  private static byte[] map2 = new byte[128];
  
  static {
    for (b = 0; b < map2.length; b++)
      map2[b] = -1; 
    for (b = 0; b < 64; b++)
      map2[map1[b]] = (byte)b; 
  }
  
  public static String encodeString(String paramString) {
    return new String(encode(paramString.getBytes()));
  }
  
  public static String encodeLines(byte[] paramArrayOfbyte) {
    return encodeLines(paramArrayOfbyte, 0, paramArrayOfbyte.length, 76, systemLineSeparator);
  }
  
  public static String encodeLines(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, String paramString) {
    int i = paramInt3 * 3 / 4;
    if (i <= 0)
      throw new IllegalArgumentException(); 
    int j = (paramInt2 + i - 1) / i;
    int k = (paramInt2 + 2) / 3 * 4 + j * paramString.length();
    StringBuilder stringBuilder = new StringBuilder(k);
    int m = 0;
    while (m < paramInt2) {
      int n = Math.min(paramInt2 - m, i);
      stringBuilder.append(encode(paramArrayOfbyte, paramInt1 + m, n));
      stringBuilder.append(paramString);
      m += n;
    } 
    return stringBuilder.toString();
  }
  
  public static char[] encode(byte[] paramArrayOfbyte) {
    return encode(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public static char[] encode(byte[] paramArrayOfbyte, int paramInt) {
    return encode(paramArrayOfbyte, 0, paramInt);
  }
  
  public static char[] encode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = (paramInt2 * 4 + 2) / 3;
    int j = (paramInt2 + 2) / 3 * 4;
    char[] arrayOfChar = new char[j];
    int k = paramInt1;
    int m = paramInt1 + paramInt2;
    byte b = 0;
    while (k < m) {
      int n = paramArrayOfbyte[k++] & 0xFF;
      byte b1 = (k < m) ? (paramArrayOfbyte[k++] & 0xFF) : 0;
      byte b2 = (k < m) ? (paramArrayOfbyte[k++] & 0xFF) : 0;
      int i1 = n >>> 2;
      int i2 = (n & 0x3) << 4 | b1 >>> 4;
      int i3 = (b1 & 0xF) << 2 | b2 >>> 6;
      int i4 = b2 & 0x3F;
      arrayOfChar[b++] = map1[i1];
      arrayOfChar[b++] = map1[i2];
      arrayOfChar[b] = (b < i) ? map1[i3] : '=';
      b++;
      arrayOfChar[b] = (b < i) ? map1[i4] : '=';
      b++;
    } 
    return arrayOfChar;
  }
  
  public static String decodeString(String paramString) {
    return new String(decode(paramString));
  }
  
  public static byte[] decodeLines(String paramString) {
    char[] arrayOfChar = new char[paramString.length()];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramString.length(); b2++) {
      char c = paramString.charAt(b2);
      if (c != ' ' && c != '\r' && c != '\n' && c != '\t')
        arrayOfChar[b1++] = c; 
    } 
    return decode(arrayOfChar, 0, b1);
  }
  
  public static byte[] decode(String paramString) {
    return decode(paramString.toCharArray());
  }
  
  public static byte[] decode(char[] paramArrayOfchar) {
    return decode(paramArrayOfchar, 0, paramArrayOfchar.length);
  }
  
  public static byte[] decode(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    if (paramInt2 % 4 != 0)
      throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4."); 
    while (paramInt2 > 0 && paramArrayOfchar[paramInt1 + paramInt2 - 1] == '=')
      paramInt2--; 
    int i = paramInt2 * 3 / 4;
    byte[] arrayOfByte = new byte[i];
    int j = paramInt1;
    int k = paramInt1 + paramInt2;
    byte b = 0;
    while (j < k) {
      char c1 = paramArrayOfchar[j++];
      char c2 = paramArrayOfchar[j++];
      byte b1 = (j < k) ? paramArrayOfchar[j++] : 65;
      byte b2 = (j < k) ? paramArrayOfchar[j++] : 65;
      if (c1 > '' || c2 > '' || b1 > 127 || b2 > 127)
        throw new IllegalArgumentException("Illegal character in Base64 encoded data."); 
      byte b3 = map2[c1];
      byte b4 = map2[c2];
      byte b5 = map2[b1];
      byte b6 = map2[b2];
      if (b3 < 0 || b4 < 0 || b5 < 0 || b6 < 0)
        throw new IllegalArgumentException("Illegal character in Base64 encoded data."); 
      int m = b3 << 2 | b4 >>> 4;
      int n = (b4 & 0xF) << 4 | b5 >>> 2;
      int i1 = (b5 & 0x3) << 6 | b6;
      arrayOfByte[b++] = (byte)m;
      if (b < i)
        arrayOfByte[b++] = (byte)n; 
      if (b < i)
        arrayOfByte[b++] = (byte)i1; 
    } 
    return arrayOfByte;
  }
}
