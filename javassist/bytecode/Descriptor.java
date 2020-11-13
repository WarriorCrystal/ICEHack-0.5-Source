package javassist.bytecode;

import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;

public class Descriptor {
  public static String toJvmName(String paramString) {
    return paramString.replace('.', '/');
  }
  
  public static String toJavaName(String paramString) {
    return paramString.replace('/', '.');
  }
  
  public static String toJvmName(CtClass paramCtClass) {
    if (paramCtClass.isArray())
      return of(paramCtClass); 
    return toJvmName(paramCtClass.getName());
  }
  
  public static String toClassName(String paramString) {
    String str;
    byte b = 0;
    int i = 0;
    char c = paramString.charAt(0);
    while (c == '[') {
      b++;
      c = paramString.charAt(++i);
    } 
    if (c == 'L') {
      int j = paramString.indexOf(';', i++);
      str = paramString.substring(i, j).replace('/', '.');
      i = j;
    } else if (c == 'V') {
      str = "void";
    } else if (c == 'I') {
      str = "int";
    } else if (c == 'B') {
      str = "byte";
    } else if (c == 'J') {
      str = "long";
    } else if (c == 'D') {
      str = "double";
    } else if (c == 'F') {
      str = "float";
    } else if (c == 'C') {
      str = "char";
    } else if (c == 'S') {
      str = "short";
    } else if (c == 'Z') {
      str = "boolean";
    } else {
      throw new RuntimeException("bad descriptor: " + paramString);
    } 
    if (i + 1 != paramString.length())
      throw new RuntimeException("multiple descriptors?: " + paramString); 
    if (b == 0)
      return str; 
    StringBuffer stringBuffer = new StringBuffer(str);
    do {
      stringBuffer.append("[]");
    } while (--b > 0);
    return stringBuffer.toString();
  }
  
  public static String of(String paramString) {
    if (paramString.equals("void"))
      return "V"; 
    if (paramString.equals("int"))
      return "I"; 
    if (paramString.equals("byte"))
      return "B"; 
    if (paramString.equals("long"))
      return "J"; 
    if (paramString.equals("double"))
      return "D"; 
    if (paramString.equals("float"))
      return "F"; 
    if (paramString.equals("char"))
      return "C"; 
    if (paramString.equals("short"))
      return "S"; 
    if (paramString.equals("boolean"))
      return "Z"; 
    return "L" + toJvmName(paramString) + ";";
  }
  
  public static String rename(String paramString1, String paramString2, String paramString3) {
    if (paramString1.indexOf(paramString2) < 0)
      return paramString1; 
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    int j = 0;
    while (true) {
      int m = paramString1.indexOf('L', j);
      if (m < 0)
        break; 
      if (paramString1.startsWith(paramString2, m + 1) && paramString1
        .charAt(m + paramString2.length() + 1) == ';') {
        stringBuffer.append(paramString1.substring(i, m));
        stringBuffer.append('L');
        stringBuffer.append(paramString3);
        stringBuffer.append(';');
        i = j = m + paramString2.length() + 2;
        continue;
      } 
      j = paramString1.indexOf(';', m) + 1;
      if (j < 1)
        break; 
    } 
    if (i == 0)
      return paramString1; 
    int k = paramString1.length();
    if (i < k)
      stringBuffer.append(paramString1.substring(i, k)); 
    return stringBuffer.toString();
  }
  
  public static String rename(String paramString, Map paramMap) {
    if (paramMap == null)
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    int j = 0;
    while (true) {
      int m = paramString.indexOf('L', j);
      if (m < 0)
        break; 
      int n = paramString.indexOf(';', m);
      if (n < 0)
        break; 
      j = n + 1;
      String str1 = paramString.substring(m + 1, n);
      String str2 = (String)paramMap.get(str1);
      if (str2 != null) {
        stringBuffer.append(paramString.substring(i, m));
        stringBuffer.append('L');
        stringBuffer.append(str2);
        stringBuffer.append(';');
        i = j;
      } 
    } 
    if (i == 0)
      return paramString; 
    int k = paramString.length();
    if (i < k)
      stringBuffer.append(paramString.substring(i, k)); 
    return stringBuffer.toString();
  }
  
  public static String of(CtClass paramCtClass) {
    StringBuffer stringBuffer = new StringBuffer();
    toDescriptor(stringBuffer, paramCtClass);
    return stringBuffer.toString();
  }
  
  private static void toDescriptor(StringBuffer paramStringBuffer, CtClass paramCtClass) {
    if (paramCtClass.isArray()) {
      paramStringBuffer.append('[');
      try {
        toDescriptor(paramStringBuffer, paramCtClass.getComponentType());
      } catch (NotFoundException notFoundException) {
        paramStringBuffer.append('L');
        String str = paramCtClass.getName();
        paramStringBuffer.append(toJvmName(str.substring(0, str.length() - 2)));
        paramStringBuffer.append(';');
      } 
    } else if (paramCtClass.isPrimitive()) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramCtClass;
      paramStringBuffer.append(ctPrimitiveType.getDescriptor());
    } else {
      paramStringBuffer.append('L');
      paramStringBuffer.append(paramCtClass.getName().replace('.', '/'));
      paramStringBuffer.append(';');
    } 
  }
  
  public static String ofConstructor(CtClass[] paramArrayOfCtClass) {
    return ofMethod(CtClass.voidType, paramArrayOfCtClass);
  }
  
  public static String ofMethod(CtClass paramCtClass, CtClass[] paramArrayOfCtClass) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    if (paramArrayOfCtClass != null) {
      int i = paramArrayOfCtClass.length;
      for (byte b = 0; b < i; b++)
        toDescriptor(stringBuffer, paramArrayOfCtClass[b]); 
    } 
    stringBuffer.append(')');
    if (paramCtClass != null)
      toDescriptor(stringBuffer, paramCtClass); 
    return stringBuffer.toString();
  }
  
  public static String ofParameters(CtClass[] paramArrayOfCtClass) {
    return ofMethod(null, paramArrayOfCtClass);
  }
  
  public static String appendParameter(String paramString1, String paramString2) {
    int i = paramString2.indexOf(')');
    if (i < 0)
      return paramString2; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(paramString2.substring(0, i));
    stringBuffer.append('L');
    stringBuffer.append(paramString1.replace('.', '/'));
    stringBuffer.append(';');
    stringBuffer.append(paramString2.substring(i));
    return stringBuffer.toString();
  }
  
  public static String insertParameter(String paramString1, String paramString2) {
    if (paramString2.charAt(0) != '(')
      return paramString2; 
    return "(L" + paramString1.replace('.', '/') + ';' + paramString2
      .substring(1);
  }
  
  public static String appendParameter(CtClass paramCtClass, String paramString) {
    int i = paramString.indexOf(')');
    if (i < 0)
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(paramString.substring(0, i));
    toDescriptor(stringBuffer, paramCtClass);
    stringBuffer.append(paramString.substring(i));
    return stringBuffer.toString();
  }
  
  public static String insertParameter(CtClass paramCtClass, String paramString) {
    if (paramString.charAt(0) != '(')
      return paramString; 
    return "(" + of(paramCtClass) + paramString.substring(1);
  }
  
  public static String changeReturnType(String paramString1, String paramString2) {
    int i = paramString2.indexOf(')');
    if (i < 0)
      return paramString2; 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(paramString2.substring(0, i + 1));
    stringBuffer.append('L');
    stringBuffer.append(paramString1.replace('.', '/'));
    stringBuffer.append(';');
    return stringBuffer.toString();
  }
  
  public static CtClass[] getParameterTypes(String paramString, ClassPool paramClassPool) throws NotFoundException {
    if (paramString.charAt(0) != '(')
      return null; 
    int i = numOfParameters(paramString);
    CtClass[] arrayOfCtClass = new CtClass[i];
    byte b = 0;
    int j = 1;
    while (true) {
      j = toCtClass(paramClassPool, paramString, j, arrayOfCtClass, b++);
      if (j <= 0)
        return arrayOfCtClass; 
    } 
  }
  
  public static boolean eqParamTypes(String paramString1, String paramString2) {
    if (paramString1.charAt(0) != '(')
      return false; 
    for (byte b = 0;; b++) {
      char c = paramString1.charAt(b);
      if (c != paramString2.charAt(b))
        return false; 
      if (c == ')')
        return true; 
    } 
  }
  
  public static String getParamDescriptor(String paramString) {
    return paramString.substring(0, paramString.indexOf(')') + 1);
  }
  
  public static CtClass getReturnType(String paramString, ClassPool paramClassPool) throws NotFoundException {
    int i = paramString.indexOf(')');
    if (i < 0)
      return null; 
    CtClass[] arrayOfCtClass = new CtClass[1];
    toCtClass(paramClassPool, paramString, i + 1, arrayOfCtClass, 0);
    return arrayOfCtClass[0];
  }
  
  public static int numOfParameters(String paramString) {
    byte b = 0;
    int i = 1;
    while (true) {
      char c = paramString.charAt(i);
      if (c == ')')
        break; 
      while (c == '[')
        c = paramString.charAt(++i); 
      if (c == 'L') {
        i = paramString.indexOf(';', i) + 1;
        if (i <= 0)
          throw new IndexOutOfBoundsException("bad descriptor"); 
      } else {
        i++;
      } 
      b++;
    } 
    return b;
  }
  
  public static CtClass toCtClass(String paramString, ClassPool paramClassPool) throws NotFoundException {
    CtClass[] arrayOfCtClass = new CtClass[1];
    int i = toCtClass(paramClassPool, paramString, 0, arrayOfCtClass, 0);
    if (i >= 0)
      return arrayOfCtClass[0]; 
    return paramClassPool.get(paramString.replace('/', '.'));
  }
  
  private static int toCtClass(ClassPool paramClassPool, String paramString, int paramInt1, CtClass[] paramArrayOfCtClass, int paramInt2) throws NotFoundException {
    int i;
    String str;
    byte b = 0;
    char c = paramString.charAt(paramInt1);
    while (c == '[') {
      b++;
      c = paramString.charAt(++paramInt1);
    } 
    if (c == 'L') {
      i = paramString.indexOf(';', ++paramInt1);
      str = paramString.substring(paramInt1, i++).replace('/', '.');
    } else {
      CtClass ctClass = toPrimitiveClass(c);
      if (ctClass == null)
        return -1; 
      i = paramInt1 + 1;
      if (b == 0) {
        paramArrayOfCtClass[paramInt2] = ctClass;
        return i;
      } 
      str = ctClass.getName();
    } 
    if (b > 0) {
      StringBuffer stringBuffer = new StringBuffer(str);
      while (b-- > 0)
        stringBuffer.append("[]"); 
      str = stringBuffer.toString();
    } 
    paramArrayOfCtClass[paramInt2] = paramClassPool.get(str);
    return i;
  }
  
  static CtClass toPrimitiveClass(char paramChar) {
    CtClass ctClass = null;
    switch (paramChar) {
      case 'Z':
        ctClass = CtClass.booleanType;
        break;
      case 'C':
        ctClass = CtClass.charType;
        break;
      case 'B':
        ctClass = CtClass.byteType;
        break;
      case 'S':
        ctClass = CtClass.shortType;
        break;
      case 'I':
        ctClass = CtClass.intType;
        break;
      case 'J':
        ctClass = CtClass.longType;
        break;
      case 'F':
        ctClass = CtClass.floatType;
        break;
      case 'D':
        ctClass = CtClass.doubleType;
        break;
      case 'V':
        ctClass = CtClass.voidType;
        break;
    } 
    return ctClass;
  }
  
  public static int arrayDimension(String paramString) {
    byte b = 0;
    while (paramString.charAt(b) == '[')
      b++; 
    return b;
  }
  
  public static String toArrayComponent(String paramString, int paramInt) {
    return paramString.substring(paramInt);
  }
  
  public static int dataSize(String paramString) {
    return dataSize(paramString, true);
  }
  
  public static int paramSize(String paramString) {
    return -dataSize(paramString, false);
  }
  
  private static int dataSize(String paramString, boolean paramBoolean) {
    byte b = 0;
    char c = paramString.charAt(0);
    if (c == '(') {
      int i = 1;
      while (true) {
        c = paramString.charAt(i);
        if (c == ')') {
          c = paramString.charAt(i + 1);
          break;
        } 
        boolean bool = false;
        while (c == '[') {
          bool = true;
          c = paramString.charAt(++i);
        } 
        if (c == 'L') {
          i = paramString.indexOf(';', i) + 1;
          if (i <= 0)
            throw new IndexOutOfBoundsException("bad descriptor"); 
        } else {
          i++;
        } 
        if (!bool && (c == 'J' || c == 'D')) {
          b -= true;
          continue;
        } 
        b--;
      } 
    } 
    if (paramBoolean)
      if (c == 'J' || c == 'D') {
        b += 2;
      } else if (c != 'V') {
        b++;
      }  
    return b;
  }
  
  public static String toString(String paramString) {
    return PrettyPrinter.toString(paramString);
  }
  
  static class PrettyPrinter {
    static String toString(String param1String) {
      StringBuffer stringBuffer = new StringBuffer();
      if (param1String.charAt(0) == '(') {
        int i = 1;
        stringBuffer.append('(');
        while (param1String.charAt(i) != ')') {
          if (i > 1)
            stringBuffer.append(','); 
          i = readType(stringBuffer, i, param1String);
        } 
        stringBuffer.append(')');
      } else {
        readType(stringBuffer, 0, param1String);
      } 
      return stringBuffer.toString();
    }
    
    static int readType(StringBuffer param1StringBuffer, int param1Int, String param1String) {
      char c = param1String.charAt(param1Int);
      byte b = 0;
      while (c == '[') {
        b++;
        c = param1String.charAt(++param1Int);
      } 
      if (c == 'L') {
        while (true) {
          c = param1String.charAt(++param1Int);
          if (c == ';')
            break; 
          if (c == '/')
            c = '.'; 
          param1StringBuffer.append(c);
        } 
      } else {
        CtClass ctClass = Descriptor.toPrimitiveClass(c);
        param1StringBuffer.append(ctClass.getName());
      } 
      while (b-- > 0)
        param1StringBuffer.append("[]"); 
      return param1Int + 1;
    }
  }
  
  public static class Iterator {
    private String desc;
    
    private int index;
    
    private int curPos;
    
    private boolean param;
    
    public Iterator(String param1String) {
      this.desc = param1String;
      this.index = this.curPos = 0;
      this.param = false;
    }
    
    public boolean hasNext() {
      return (this.index < this.desc.length());
    }
    
    public boolean isParameter() {
      return this.param;
    }
    
    public char currentChar() {
      return this.desc.charAt(this.curPos);
    }
    
    public boolean is2byte() {
      char c = currentChar();
      return (c == 'D' || c == 'J');
    }
    
    public int next() {
      int i = this.index;
      char c = this.desc.charAt(i);
      if (c == '(') {
        this.index++;
        c = this.desc.charAt(++i);
        this.param = true;
      } 
      if (c == ')') {
        this.index++;
        c = this.desc.charAt(++i);
        this.param = false;
      } 
      while (c == '[')
        c = this.desc.charAt(++i); 
      if (c == 'L') {
        i = this.desc.indexOf(';', i) + 1;
        if (i <= 0)
          throw new IndexOutOfBoundsException("bad descriptor"); 
      } else {
        i++;
      } 
      this.curPos = this.index;
      this.index = i;
      return this.curPos;
    }
  }
}
