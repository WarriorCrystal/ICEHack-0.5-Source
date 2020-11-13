package org.yaml.snakeyaml.external.com.google.gdata.util.common.base;

import java.io.IOException;

public abstract class UnicodeEscaper implements Escaper {
  private static final int DEST_PAD = 32;
  
  protected abstract char[] escape(int paramInt);
  
  protected int nextEscapeIndex(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    int i = paramInt1;
    while (i < paramInt2) {
      int j = codePointAt(paramCharSequence, i, paramInt2);
      if (j < 0 || escape(j) != null)
        break; 
      i += Character.isSupplementaryCodePoint(j) ? 2 : 1;
    } 
    return i;
  }
  
  public String escape(String paramString) {
    int i = paramString.length();
    int j = nextEscapeIndex(paramString, 0, i);
    return (j == i) ? paramString : escapeSlow(paramString, j);
  }
  
  protected final String escapeSlow(String paramString, int paramInt) {
    int i = paramString.length();
    char[] arrayOfChar = DEST_TL.get();
    int j = 0;
    int k = 0;
    while (paramInt < i) {
      int n = codePointAt(paramString, paramInt, i);
      if (n < 0)
        throw new IllegalArgumentException("Trailing high surrogate at end of input"); 
      char[] arrayOfChar1 = escape(n);
      if (arrayOfChar1 != null) {
        int i1 = paramInt - k;
        int i2 = j + i1 + arrayOfChar1.length;
        if (arrayOfChar.length < i2) {
          int i3 = i2 + i - paramInt + 32;
          arrayOfChar = growBuffer(arrayOfChar, j, i3);
        } 
        if (i1 > 0) {
          paramString.getChars(k, paramInt, arrayOfChar, j);
          j += i1;
        } 
        if (arrayOfChar1.length > 0) {
          System.arraycopy(arrayOfChar1, 0, arrayOfChar, j, arrayOfChar1.length);
          j += arrayOfChar1.length;
        } 
      } 
      k = paramInt + (Character.isSupplementaryCodePoint(n) ? 2 : 1);
      paramInt = nextEscapeIndex(paramString, k, i);
    } 
    int m = i - k;
    if (m > 0) {
      int n = j + m;
      if (arrayOfChar.length < n)
        arrayOfChar = growBuffer(arrayOfChar, j, n); 
      paramString.getChars(k, i, arrayOfChar, j);
      j = n;
    } 
    return new String(arrayOfChar, 0, j);
  }
  
  public Appendable escape(final Appendable out) {
    assert out != null;
    return new Appendable() {
        int pendingHighSurrogate = -1;
        
        char[] decodedChars = new char[2];
        
        public Appendable append(CharSequence param1CharSequence) throws IOException {
          return append(param1CharSequence, 0, param1CharSequence.length());
        }
        
        public Appendable append(CharSequence param1CharSequence, int param1Int1, int param1Int2) throws IOException {
          int i = param1Int1;
          if (i < param1Int2) {
            int j = i;
            if (this.pendingHighSurrogate != -1) {
              char c = param1CharSequence.charAt(i++);
              if (!Character.isLowSurrogate(c))
                throw new IllegalArgumentException("Expected low surrogate character but got " + c); 
              char[] arrayOfChar = UnicodeEscaper.this.escape(Character.toCodePoint((char)this.pendingHighSurrogate, c));
              if (arrayOfChar != null) {
                outputChars(arrayOfChar, arrayOfChar.length);
                j++;
              } else {
                out.append((char)this.pendingHighSurrogate);
              } 
              this.pendingHighSurrogate = -1;
            } 
            while (true) {
              i = UnicodeEscaper.this.nextEscapeIndex(param1CharSequence, i, param1Int2);
              if (i > j)
                out.append(param1CharSequence, j, i); 
              if (i == param1Int2)
                break; 
              int k = UnicodeEscaper.codePointAt(param1CharSequence, i, param1Int2);
              if (k < 0) {
                this.pendingHighSurrogate = -k;
                break;
              } 
              char[] arrayOfChar = UnicodeEscaper.this.escape(k);
              if (arrayOfChar != null) {
                outputChars(arrayOfChar, arrayOfChar.length);
              } else {
                int m = Character.toChars(k, this.decodedChars, 0);
                outputChars(this.decodedChars, m);
              } 
              i += Character.isSupplementaryCodePoint(k) ? 2 : 1;
              j = i;
            } 
          } 
          return this;
        }
        
        public Appendable append(char param1Char) throws IOException {
          if (this.pendingHighSurrogate != -1) {
            if (!Character.isLowSurrogate(param1Char))
              throw new IllegalArgumentException("Expected low surrogate character but got '" + param1Char + "' with value " + param1Char); 
            char[] arrayOfChar = UnicodeEscaper.this.escape(Character.toCodePoint((char)this.pendingHighSurrogate, param1Char));
            if (arrayOfChar != null) {
              outputChars(arrayOfChar, arrayOfChar.length);
            } else {
              out.append((char)this.pendingHighSurrogate);
              out.append(param1Char);
            } 
            this.pendingHighSurrogate = -1;
          } else if (Character.isHighSurrogate(param1Char)) {
            this.pendingHighSurrogate = param1Char;
          } else {
            if (Character.isLowSurrogate(param1Char))
              throw new IllegalArgumentException("Unexpected low surrogate character '" + param1Char + "' with value " + param1Char); 
            char[] arrayOfChar = UnicodeEscaper.this.escape(param1Char);
            if (arrayOfChar != null) {
              outputChars(arrayOfChar, arrayOfChar.length);
            } else {
              out.append(param1Char);
            } 
          } 
          return this;
        }
        
        private void outputChars(char[] param1ArrayOfchar, int param1Int) throws IOException {
          for (byte b = 0; b < param1Int; b++)
            out.append(param1ArrayOfchar[b]); 
        }
      };
  }
  
  protected static final int codePointAt(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    if (paramInt1 < paramInt2) {
      char c = paramCharSequence.charAt(paramInt1++);
      if (c < '?' || c > '?')
        return c; 
      if (c <= '?') {
        if (paramInt1 == paramInt2)
          return -c; 
        char c1 = paramCharSequence.charAt(paramInt1);
        if (Character.isLowSurrogate(c1))
          return Character.toCodePoint(c, c1); 
        throw new IllegalArgumentException("Expected low surrogate but got char '" + c1 + "' with value " + c1 + " at index " + paramInt1);
      } 
      throw new IllegalArgumentException("Unexpected low surrogate character '" + c + "' with value " + c + " at index " + (paramInt1 - 1));
    } 
    throw new IndexOutOfBoundsException("Index exceeds specified range");
  }
  
  private static final char[] growBuffer(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    char[] arrayOfChar = new char[paramInt2];
    if (paramInt1 > 0)
      System.arraycopy(paramArrayOfchar, 0, arrayOfChar, 0, paramInt1); 
    return arrayOfChar;
  }
  
  private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>() {
      protected char[] initialValue() {
        return new char[1024];
      }
    };
}
