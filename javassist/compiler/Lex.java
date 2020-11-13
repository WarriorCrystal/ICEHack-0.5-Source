package javassist.compiler;

public class Lex implements TokenId {
  private int lastChar;
  
  private StringBuffer textBuffer;
  
  private Lex1 currentToken;
  
  private Lex1 lookAheadTokens;
  
  private String input;
  
  private int position;
  
  private int maxlen;
  
  private int lineNumber;
  
  public Lex(String paramString) {
    this.lastChar = -1;
    this.textBuffer = new StringBuffer();
    this.currentToken = new Lex1();
    this.lookAheadTokens = null;
    this.input = paramString;
    this.position = 0;
    this.maxlen = paramString.length();
    this.lineNumber = 0;
  }
  
  public int get() {
    if (this.lookAheadTokens == null)
      return get(this.currentToken); 
    Lex1 lex1 = this.lookAheadTokens;
    this.lookAheadTokens = this.lookAheadTokens.next;
    return lex1.tokenId;
  }
  
  public int lookAhead() {
    return lookAhead(0);
  }
  
  public int lookAhead(int paramInt) {
    Lex1 lex1 = this.lookAheadTokens;
    if (lex1 == null) {
      this.lookAheadTokens = lex1 = this.currentToken;
      lex1.next = null;
      get(lex1);
    } 
    for (; paramInt-- > 0; lex1 = lex1.next) {
      if (lex1.next == null) {
        Lex1 lex11 = new Lex1();
        get(lex11);
      } 
    } 
    this.currentToken = lex1;
    return lex1.tokenId;
  }
  
  public String getString() {
    return this.currentToken.textValue;
  }
  
  public long getLong() {
    return this.currentToken.longValue;
  }
  
  public double getDouble() {
    return this.currentToken.doubleValue;
  }
  
  private int get(Lex1 paramLex1) {
    while (true) {
      int i = readLine(paramLex1);
      if (i != 10) {
        paramLex1.tokenId = i;
        return i;
      } 
    } 
  }
  
  private int readLine(Lex1 paramLex1) {
    int i = getNextNonWhiteChar();
    if (i < 0)
      return i; 
    if (i == 10) {
      this.lineNumber++;
      return 10;
    } 
    if (i == 39)
      return readCharConst(paramLex1); 
    if (i == 34)
      return readStringL(paramLex1); 
    if (48 <= i && i <= 57)
      return readNumber(i, paramLex1); 
    if (i == 46) {
      i = getc();
      if (48 <= i && i <= 57) {
        StringBuffer stringBuffer = this.textBuffer;
        stringBuffer.setLength(0);
        stringBuffer.append('.');
        return readDouble(stringBuffer, i, paramLex1);
      } 
      ungetc(i);
      return readSeparator(46);
    } 
    if (Character.isJavaIdentifierStart((char)i))
      return readIdentifier(i, paramLex1); 
    return readSeparator(i);
  }
  
  private int getNextNonWhiteChar() {
    while (true) {
      int i = getc();
      if (i == 47) {
        i = getc();
        if (i == 47) {
          do {
            i = getc();
          } while (i != 10 && i != 13 && i != -1);
        } else if (i == 42) {
          while (true) {
            i = getc();
            if (i == -1)
              break; 
            if (i == 42) {
              if ((i = getc()) == 47) {
                i = 32;
                break;
              } 
              ungetc(i);
            } 
          } 
        } else {
          ungetc(i);
          i = 47;
        } 
      } 
      if (!isBlank(i))
        return i; 
    } 
  }
  
  private int readCharConst(Lex1 paramLex1) {
    int j = 0;
    int i;
    while ((i = getc()) != 39) {
      if (i == 92) {
        j = readEscapeChar();
        continue;
      } 
      if (i < 32) {
        if (i == 10)
          this.lineNumber++; 
        return 500;
      } 
      j = i;
    } 
    paramLex1.longValue = j;
    return 401;
  }
  
  private int readEscapeChar() {
    int i = getc();
    if (i == 110) {
      i = 10;
    } else if (i == 116) {
      i = 9;
    } else if (i == 114) {
      i = 13;
    } else if (i == 102) {
      i = 12;
    } else if (i == 10) {
      this.lineNumber++;
    } 
    return i;
  }
  
  private int readStringL(Lex1 paramLex1) {
    StringBuffer stringBuffer = this.textBuffer;
    stringBuffer.setLength(0);
    while (true) {
      int i;
      while ((i = getc()) != 34) {
        if (i == 92) {
          i = readEscapeChar();
        } else if (i == 10 || i < 0) {
          this.lineNumber++;
          return 500;
        } 
        stringBuffer.append((char)i);
      } 
      while (true) {
        i = getc();
        if (i == 10) {
          this.lineNumber++;
          continue;
        } 
        if (!isBlank(i))
          break; 
      } 
      if (i != 34) {
        ungetc(i);
        paramLex1.textValue = stringBuffer.toString();
        return 406;
      } 
    } 
  }
  
  private int readNumber(int paramInt, Lex1 paramLex1) {
    long l = 0L;
    int i = getc();
    if (paramInt == 48) {
      if (i == 88 || i == 120) {
        while (true) {
          paramInt = getc();
          if (48 <= paramInt && paramInt <= 57) {
            l = l * 16L + (paramInt - 48);
            continue;
          } 
          if (65 <= paramInt && paramInt <= 70) {
            l = l * 16L + (paramInt - 65 + 10);
            continue;
          } 
          if (97 <= paramInt && paramInt <= 102) {
            l = l * 16L + (paramInt - 97 + 10);
            continue;
          } 
          break;
        } 
        paramLex1.longValue = l;
        if (paramInt == 76 || paramInt == 108)
          return 403; 
        ungetc(paramInt);
        return 402;
      } 
      if (48 <= i && i <= 55) {
        l = (i - 48);
        while (true) {
          paramInt = getc();
          if (48 <= paramInt && paramInt <= 55) {
            l = l * 8L + (paramInt - 48);
            continue;
          } 
          break;
        } 
        paramLex1.longValue = l;
        if (paramInt == 76 || paramInt == 108)
          return 403; 
        ungetc(paramInt);
        return 402;
      } 
    } 
    l = (paramInt - 48);
    while (48 <= i && i <= 57) {
      l = l * 10L + i - 48L;
      i = getc();
    } 
    paramLex1.longValue = l;
    if (i == 70 || i == 102) {
      paramLex1.doubleValue = l;
      return 404;
    } 
    if (i == 69 || i == 101 || i == 68 || i == 100 || i == 46) {
      StringBuffer stringBuffer = this.textBuffer;
      stringBuffer.setLength(0);
      stringBuffer.append(l);
      return readDouble(stringBuffer, i, paramLex1);
    } 
    if (i == 76 || i == 108)
      return 403; 
    ungetc(i);
    return 402;
  }
  
  private int readDouble(StringBuffer paramStringBuffer, int paramInt, Lex1 paramLex1) {
    if (paramInt != 69 && paramInt != 101 && paramInt != 68 && paramInt != 100) {
      paramStringBuffer.append((char)paramInt);
      while (true) {
        paramInt = getc();
        if (48 <= paramInt && paramInt <= 57) {
          paramStringBuffer.append((char)paramInt);
          continue;
        } 
        break;
      } 
    } 
    if (paramInt == 69 || paramInt == 101) {
      paramStringBuffer.append((char)paramInt);
      paramInt = getc();
      if (paramInt == 43 || paramInt == 45) {
        paramStringBuffer.append((char)paramInt);
        paramInt = getc();
      } 
      while (48 <= paramInt && paramInt <= 57) {
        paramStringBuffer.append((char)paramInt);
        paramInt = getc();
      } 
    } 
    try {
      paramLex1.doubleValue = Double.parseDouble(paramStringBuffer.toString());
    } catch (NumberFormatException numberFormatException) {
      return 500;
    } 
    if (paramInt == 70 || paramInt == 102)
      return 404; 
    if (paramInt != 68 && paramInt != 100)
      ungetc(paramInt); 
    return 405;
  }
  
  private static final int[] equalOps = new int[] { 
      350, 0, 0, 0, 351, 352, 0, 0, 0, 353, 
      354, 0, 355, 0, 356, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 357, 358, 359, 
      0 };
  
  private int readSeparator(int paramInt) {
    int i;
    if (33 <= paramInt && paramInt <= 63) {
      int j = equalOps[paramInt - 33];
      if (j == 0)
        return paramInt; 
      i = getc();
      if (paramInt == i) {
        int k;
        switch (paramInt) {
          case 61:
            return 358;
          case 43:
            return 362;
          case 45:
            return 363;
          case 38:
            return 369;
          case 60:
            k = getc();
            if (k == 61)
              return 365; 
            ungetc(k);
            return 364;
          case 62:
            k = getc();
            if (k == 61)
              return 367; 
            if (k == 62) {
              k = getc();
              if (k == 61)
                return 371; 
              ungetc(k);
              return 370;
            } 
            ungetc(k);
            return 366;
        } 
      } else if (i == 61) {
        return j;
      } 
    } else if (paramInt == 94) {
      i = getc();
      if (i == 61)
        return 360; 
    } else if (paramInt == 124) {
      i = getc();
      if (i == 61)
        return 361; 
      if (i == 124)
        return 368; 
    } else {
      return paramInt;
    } 
    ungetc(i);
    return paramInt;
  }
  
  private int readIdentifier(int paramInt, Lex1 paramLex1) {
    StringBuffer stringBuffer = this.textBuffer;
    stringBuffer.setLength(0);
    do {
      stringBuffer.append((char)paramInt);
      paramInt = getc();
    } while (Character.isJavaIdentifierPart((char)paramInt));
    ungetc(paramInt);
    String str = stringBuffer.toString();
    int i = ktable.lookup(str);
    if (i >= 0)
      return i; 
    paramLex1.textValue = str;
    return 400;
  }
  
  private static final KeywordTable ktable = new KeywordTable();
  
  static {
    ktable.append("abstract", 300);
    ktable.append("boolean", 301);
    ktable.append("break", 302);
    ktable.append("byte", 303);
    ktable.append("case", 304);
    ktable.append("catch", 305);
    ktable.append("char", 306);
    ktable.append("class", 307);
    ktable.append("const", 308);
    ktable.append("continue", 309);
    ktable.append("default", 310);
    ktable.append("do", 311);
    ktable.append("double", 312);
    ktable.append("else", 313);
    ktable.append("extends", 314);
    ktable.append("false", 411);
    ktable.append("final", 315);
    ktable.append("finally", 316);
    ktable.append("float", 317);
    ktable.append("for", 318);
    ktable.append("goto", 319);
    ktable.append("if", 320);
    ktable.append("implements", 321);
    ktable.append("import", 322);
    ktable.append("instanceof", 323);
    ktable.append("int", 324);
    ktable.append("interface", 325);
    ktable.append("long", 326);
    ktable.append("native", 327);
    ktable.append("new", 328);
    ktable.append("null", 412);
    ktable.append("package", 329);
    ktable.append("private", 330);
    ktable.append("protected", 331);
    ktable.append("public", 332);
    ktable.append("return", 333);
    ktable.append("short", 334);
    ktable.append("static", 335);
    ktable.append("strictfp", 347);
    ktable.append("super", 336);
    ktable.append("switch", 337);
    ktable.append("synchronized", 338);
    ktable.append("this", 339);
    ktable.append("throw", 340);
    ktable.append("throws", 341);
    ktable.append("transient", 342);
    ktable.append("true", 410);
    ktable.append("try", 343);
    ktable.append("void", 344);
    ktable.append("volatile", 345);
    ktable.append("while", 346);
  }
  
  private static boolean isBlank(int paramInt) {
    return (paramInt == 32 || paramInt == 9 || paramInt == 12 || paramInt == 13 || paramInt == 10);
  }
  
  private static boolean isDigit(int paramInt) {
    return (48 <= paramInt && paramInt <= 57);
  }
  
  private void ungetc(int paramInt) {
    this.lastChar = paramInt;
  }
  
  public String getTextAround() {
    int i = this.position - 10;
    if (i < 0)
      i = 0; 
    int j = this.position + 10;
    if (j > this.maxlen)
      j = this.maxlen; 
    return this.input.substring(i, j);
  }
  
  private int getc() {
    if (this.lastChar < 0) {
      if (this.position < this.maxlen)
        return this.input.charAt(this.position++); 
      return -1;
    } 
    int i = this.lastChar;
    this.lastChar = -1;
    return i;
  }
}
