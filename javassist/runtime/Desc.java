package javassist.runtime;

public class Desc {
  public static boolean useContextClassLoader = false;
  
  private static Class getClassObject(String paramString) throws ClassNotFoundException {
    if (useContextClassLoader)
      return Class.forName(paramString, true, Thread.currentThread().getContextClassLoader()); 
    return Class.forName(paramString);
  }
  
  public static Class getClazz(String paramString) {
    try {
      return getClassObject(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException("$class: internal error, could not find class '" + paramString + "' (Desc.useContextClassLoader: " + 
          
          Boolean.toString(useContextClassLoader) + ")", classNotFoundException);
    } 
  }
  
  public static Class[] getParams(String paramString) {
    if (paramString.charAt(0) != '(')
      throw new RuntimeException("$sig: internal error"); 
    return getType(paramString, paramString.length(), 1, 0);
  }
  
  public static Class getType(String paramString) {
    Class[] arrayOfClass = getType(paramString, paramString.length(), 0, 0);
    if (arrayOfClass == null || arrayOfClass.length != 1)
      throw new RuntimeException("$type: internal error"); 
    return arrayOfClass[0];
  }
  
  private static Class[] getType(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    Class<boolean> clazz8;
    Class<char> clazz7;
    Class<byte> clazz6;
    Class<short> clazz5;
    Class<int> clazz4;
    Class<long> clazz3;
    Class<float> clazz2;
    Class<double> clazz1;
    Class<void> clazz;
    Class[] arrayOfClass;
    if (paramInt2 >= paramInt1)
      return new Class[paramInt3]; 
    char c = paramString.charAt(paramInt2);
    switch (c) {
      case 'Z':
        clazz8 = boolean.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz8;
        return arrayOfClass;
      case 'C':
        clazz7 = char.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz7;
        return arrayOfClass;
      case 'B':
        clazz6 = byte.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz6;
        return arrayOfClass;
      case 'S':
        clazz5 = short.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz5;
        return arrayOfClass;
      case 'I':
        clazz4 = int.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz4;
        return arrayOfClass;
      case 'J':
        clazz3 = long.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz3;
        return arrayOfClass;
      case 'F':
        clazz2 = float.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz2;
        return arrayOfClass;
      case 'D':
        clazz1 = double.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz1;
        return arrayOfClass;
      case 'V':
        clazz = void.class;
        arrayOfClass = getType(paramString, paramInt1, paramInt2 + 1, paramInt3 + 1);
        arrayOfClass[paramInt3] = clazz;
        return arrayOfClass;
      case 'L':
      case '[':
        return getClassType(paramString, paramInt1, paramInt2, paramInt3);
    } 
    return new Class[paramInt3];
  }
  
  private static Class[] getClassType(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    String str;
    int i = paramInt2;
    while (paramString.charAt(i) == '[')
      i++; 
    if (paramString.charAt(i) == 'L') {
      i = paramString.indexOf(';', i);
      if (i < 0)
        throw new IndexOutOfBoundsException("bad descriptor"); 
    } 
    if (paramString.charAt(paramInt2) == 'L') {
      str = paramString.substring(paramInt2 + 1, i);
    } else {
      str = paramString.substring(paramInt2, i + 1);
    } 
    Class[] arrayOfClass = getType(paramString, paramInt1, i + 1, paramInt3 + 1);
    try {
      arrayOfClass[paramInt3] = getClassObject(str.replace('/', '.'));
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException(classNotFoundException.getMessage());
    } 
    return arrayOfClass;
  }
}
