package javassist.compiler;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.Keyword;
import javassist.compiler.ast.Symbol;

public class MemberResolver implements TokenId {
  private ClassPool classPool;
  
  private static final int YES = 0;
  
  private static final int NO = -1;
  
  private static final String INVALID = "<invalid>";
  
  public MemberResolver(ClassPool paramClassPool) {
    this.invalidNames = null;
    this.classPool = paramClassPool;
  }
  
  public ClassPool getClassPool() {
    return this.classPool;
  }
  
  private static void fatal() throws CompileError {
    throw new CompileError("fatal");
  }
  
  public static class Method {
    public CtClass declaring;
    
    public MethodInfo info;
    
    public int notmatch;
    
    public Method(CtClass param1CtClass, MethodInfo param1MethodInfo, int param1Int) {
      this.declaring = param1CtClass;
      this.info = param1MethodInfo;
      this.notmatch = param1Int;
    }
    
    public boolean isStatic() {
      int i = this.info.getAccessFlags();
      return ((i & 0x8) != 0);
    }
  }
  
  public Method lookupMethod(CtClass paramCtClass1, CtClass paramCtClass2, MethodInfo paramMethodInfo, String paramString, int[] paramArrayOfint1, int[] paramArrayOfint2, String[] paramArrayOfString) throws CompileError {
    Method method1 = null;
    if (paramMethodInfo != null && paramCtClass1 == paramCtClass2 && paramMethodInfo.getName().equals(paramString)) {
      int i = compareSignature(paramMethodInfo.getDescriptor(), paramArrayOfint1, paramArrayOfint2, paramArrayOfString);
      if (i != -1) {
        Method method = new Method(paramCtClass1, paramMethodInfo, i);
        if (i == 0)
          return method; 
        method1 = method;
      } 
    } 
    Method method2 = lookupMethod(paramCtClass1, paramString, paramArrayOfint1, paramArrayOfint2, paramArrayOfString, (method1 != null));
    if (method2 != null)
      return method2; 
    return method1;
  }
  
  private Method lookupMethod(CtClass paramCtClass, String paramString, int[] paramArrayOfint1, int[] paramArrayOfint2, String[] paramArrayOfString, boolean paramBoolean) throws CompileError {
    Method method = null;
    ClassFile classFile = paramCtClass.getClassFile2();
    if (classFile != null) {
      List<MethodInfo> list = classFile.getMethods();
      int j = list.size();
      for (byte b = 0; b < j; b++) {
        MethodInfo methodInfo = list.get(b);
        if (methodInfo.getName().equals(paramString) && (methodInfo.getAccessFlags() & 0x40) == 0) {
          int k = compareSignature(methodInfo.getDescriptor(), paramArrayOfint1, paramArrayOfint2, paramArrayOfString);
          if (k != -1) {
            Method method1 = new Method(paramCtClass, methodInfo, k);
            if (k == 0)
              return method1; 
            if (method == null || method.notmatch > k)
              method = method1; 
          } 
        } 
      } 
    } 
    if (paramBoolean) {
      method = null;
    } else if (method != null) {
      return method;
    } 
    int i = paramCtClass.getModifiers();
    boolean bool = Modifier.isInterface(i);
    try {
      if (!bool) {
        CtClass ctClass = paramCtClass.getSuperclass();
        if (ctClass != null) {
          Method method1 = lookupMethod(ctClass, paramString, paramArrayOfint1, paramArrayOfint2, paramArrayOfString, paramBoolean);
          if (method1 != null)
            return method1; 
        } 
      } 
    } catch (NotFoundException notFoundException) {}
    try {
      CtClass[] arrayOfCtClass = paramCtClass.getInterfaces();
      int j = arrayOfCtClass.length;
      for (byte b = 0; b < j; b++) {
        Method method1 = lookupMethod(arrayOfCtClass[b], paramString, paramArrayOfint1, paramArrayOfint2, paramArrayOfString, paramBoolean);
        if (method1 != null)
          return method1; 
      } 
      if (bool) {
        CtClass ctClass = paramCtClass.getSuperclass();
        if (ctClass != null) {
          Method method1 = lookupMethod(ctClass, paramString, paramArrayOfint1, paramArrayOfint2, paramArrayOfString, paramBoolean);
          if (method1 != null)
            return method1; 
        } 
      } 
    } catch (NotFoundException notFoundException) {}
    return method;
  }
  
  private int compareSignature(String paramString, int[] paramArrayOfint1, int[] paramArrayOfint2, String[] paramArrayOfString) throws CompileError {
    byte b = 0;
    int i = 1;
    int j = paramArrayOfint1.length;
    if (j != Descriptor.numOfParameters(paramString))
      return -1; 
    int k = paramString.length();
    for (int m = 0; i < k; m++) {
      char c = paramString.charAt(i++);
      if (c == ')')
        return (m == j) ? b : -1; 
      if (m >= j)
        return -1; 
      byte b1 = 0;
      while (c == '[') {
        b1++;
        c = paramString.charAt(i++);
      } 
      if (paramArrayOfint1[m] == 412) {
        if (b1 == 0 && c != 'L')
          return -1; 
        if (c == 'L')
          i = paramString.indexOf(';', i) + 1; 
      } else if (paramArrayOfint2[m] != b1) {
        if (b1 != 0 || c != 'L' || !paramString.startsWith("java/lang/Object;", i))
          return -1; 
        i = paramString.indexOf(';', i) + 1;
        b++;
        if (i <= 0)
          return -1; 
      } else if (c == 'L') {
        int n = paramString.indexOf(';', i);
        if (n < 0 || paramArrayOfint1[m] != 307)
          return -1; 
        String str = paramString.substring(i, n);
        if (!str.equals(paramArrayOfString[m])) {
          CtClass ctClass = lookupClassByJvmName(paramArrayOfString[m]);
          try {
            if (ctClass.subtypeOf(lookupClassByJvmName(str))) {
              b++;
            } else {
              return -1;
            } 
          } catch (NotFoundException notFoundException) {
            b++;
          } 
        } 
        i = n + 1;
      } else {
        int n = descToType(c);
        int i1 = paramArrayOfint1[m];
        if (n != i1)
          if (n == 324 && (i1 == 334 || i1 == 303 || i1 == 306)) {
            b++;
          } else {
            return -1;
          }  
      } 
    } 
    return -1;
  }
  
  public CtField lookupFieldByJvmName2(String paramString, Symbol paramSymbol, ASTree paramASTree) throws NoFieldException {
    String str = paramSymbol.get();
    CtClass ctClass = null;
    try {
      ctClass = lookupClass(jvmToJavaName(paramString), true);
    } catch (CompileError compileError) {
      throw new NoFieldException(paramString + "/" + str, paramASTree);
    } 
    try {
      return ctClass.getField(str);
    } catch (NotFoundException notFoundException) {
      paramString = javaToJvmName(ctClass.getName());
      throw new NoFieldException(paramString + "$" + str, paramASTree);
    } 
  }
  
  public CtField lookupFieldByJvmName(String paramString, Symbol paramSymbol) throws CompileError {
    return lookupField(jvmToJavaName(paramString), paramSymbol);
  }
  
  public CtField lookupField(String paramString, Symbol paramSymbol) throws CompileError {
    CtClass ctClass = lookupClass(paramString, false);
    try {
      return ctClass.getField(paramSymbol.get());
    } catch (NotFoundException notFoundException) {
      throw new CompileError("no such field: " + paramSymbol.get());
    } 
  }
  
  public CtClass lookupClassByName(ASTList paramASTList) throws CompileError {
    return lookupClass(Declarator.astToClassName(paramASTList, '.'), false);
  }
  
  public CtClass lookupClassByJvmName(String paramString) throws CompileError {
    return lookupClass(jvmToJavaName(paramString), false);
  }
  
  public CtClass lookupClass(Declarator paramDeclarator) throws CompileError {
    return lookupClass(paramDeclarator.getType(), paramDeclarator.getArrayDim(), paramDeclarator.getClassName());
  }
  
  public CtClass lookupClass(int paramInt1, int paramInt2, String paramString) throws CompileError {
    String str = "";
    if (paramInt1 == 307) {
      CtClass ctClass = lookupClassByJvmName(paramString);
      if (paramInt2 > 0) {
        str = ctClass.getName();
      } else {
        return ctClass;
      } 
    } else {
      str = getTypeName(paramInt1);
    } 
    while (paramInt2-- > 0)
      str = str + "[]"; 
    return lookupClass(str, false);
  }
  
  static String getTypeName(int paramInt) throws CompileError {
    String str = "";
    switch (paramInt) {
      case 301:
        str = "boolean";
        return str;
      case 306:
        str = "char";
        return str;
      case 303:
        str = "byte";
        return str;
      case 334:
        str = "short";
        return str;
      case 324:
        str = "int";
        return str;
      case 326:
        str = "long";
        return str;
      case 317:
        str = "float";
        return str;
      case 312:
        str = "double";
        return str;
      case 344:
        str = "void";
        return str;
    } 
    fatal();
    return str;
  }
  
  public CtClass lookupClass(String paramString, boolean paramBoolean) throws CompileError {
    Hashtable<String, String> hashtable = getInvalidNames();
    Object object = hashtable.get(paramString);
    if (object == "<invalid>")
      throw new CompileError("no such class: " + paramString); 
    if (object != null)
      try {
        return this.classPool.get((String)object);
      } catch (NotFoundException notFoundException) {} 
    CtClass ctClass = null;
    try {
      ctClass = lookupClass0(paramString, paramBoolean);
    } catch (NotFoundException notFoundException) {
      ctClass = searchImports(paramString);
    } 
    hashtable.put(paramString, ctClass.getName());
    return ctClass;
  }
  
  private static WeakHashMap invalidNamesMap = new WeakHashMap<Object, Object>();
  
  private Hashtable invalidNames;
  
  public static int getInvalidMapSize() {
    return invalidNamesMap.size();
  }
  
  private Hashtable getInvalidNames() {
    Hashtable<Object, Object> hashtable = this.invalidNames;
    if (hashtable == null) {
      synchronized (MemberResolver.class) {
        WeakReference<Hashtable> weakReference = (WeakReference)invalidNamesMap.get(this.classPool);
        if (weakReference != null)
          hashtable = weakReference.get(); 
        if (hashtable == null) {
          hashtable = new Hashtable<Object, Object>();
          invalidNamesMap.put(this.classPool, new WeakReference<Hashtable<Object, Object>>(hashtable));
        } 
      } 
      this.invalidNames = hashtable;
    } 
    return hashtable;
  }
  
  private CtClass searchImports(String paramString) throws CompileError {
    if (paramString.indexOf('.') < 0) {
      Iterator<String> iterator = this.classPool.getImportedPackages();
      while (iterator.hasNext()) {
        String str1 = iterator.next();
        String str2 = str1 + '.' + paramString;
        try {
          return this.classPool.get(str2);
        } catch (NotFoundException notFoundException) {
          try {
            if (str1.endsWith("." + paramString))
              return this.classPool.get(str1); 
          } catch (NotFoundException notFoundException1) {}
        } 
      } 
    } 
    getInvalidNames().put(paramString, "<invalid>");
    throw new CompileError("no such class: " + paramString);
  }
  
  private CtClass lookupClass0(String paramString, boolean paramBoolean) throws NotFoundException {
    CtClass ctClass = null;
    while (true) {
      try {
        ctClass = this.classPool.get(paramString);
      } catch (NotFoundException notFoundException) {
        int i = paramString.lastIndexOf('.');
        if (paramBoolean || i < 0)
          throw notFoundException; 
        StringBuffer stringBuffer = new StringBuffer(paramString);
        stringBuffer.setCharAt(i, '$');
        paramString = stringBuffer.toString();
      } 
      if (ctClass != null)
        return ctClass; 
    } 
  }
  
  public String resolveClassName(ASTList paramASTList) throws CompileError {
    if (paramASTList == null)
      return null; 
    return javaToJvmName(lookupClassByName(paramASTList).getName());
  }
  
  public String resolveJvmClassName(String paramString) throws CompileError {
    if (paramString == null)
      return null; 
    return javaToJvmName(lookupClassByJvmName(paramString).getName());
  }
  
  public static CtClass getSuperclass(CtClass paramCtClass) throws CompileError {
    try {
      CtClass ctClass = paramCtClass.getSuperclass();
      if (ctClass != null)
        return ctClass; 
    } catch (NotFoundException notFoundException) {}
    throw new CompileError("cannot find the super class of " + paramCtClass
        .getName());
  }
  
  public static CtClass getSuperInterface(CtClass paramCtClass, String paramString) throws CompileError {
    try {
      CtClass[] arrayOfCtClass = paramCtClass.getInterfaces();
      for (byte b = 0; b < arrayOfCtClass.length; b++) {
        if (arrayOfCtClass[b].getName().equals(paramString))
          return arrayOfCtClass[b]; 
      } 
    } catch (NotFoundException notFoundException) {}
    throw new CompileError("cannot find the super inetrface " + paramString + " of " + paramCtClass
        .getName());
  }
  
  public static String javaToJvmName(String paramString) {
    return paramString.replace('.', '/');
  }
  
  public static String jvmToJavaName(String paramString) {
    return paramString.replace('/', '.');
  }
  
  public static int descToType(char paramChar) throws CompileError {
    switch (paramChar) {
      case 'Z':
        return 301;
      case 'C':
        return 306;
      case 'B':
        return 303;
      case 'S':
        return 334;
      case 'I':
        return 324;
      case 'J':
        return 326;
      case 'F':
        return 317;
      case 'D':
        return 312;
      case 'V':
        return 344;
      case 'L':
      case '[':
        return 307;
    } 
    fatal();
    return 344;
  }
  
  public static int getModifiers(ASTList paramASTList) {
    int i = 0;
    while (paramASTList != null) {
      Keyword keyword = (Keyword)paramASTList.head();
      paramASTList = paramASTList.tail();
      switch (keyword.get()) {
        case 335:
          i |= 0x8;
        case 315:
          i |= 0x10;
        case 338:
          i |= 0x20;
        case 300:
          i |= 0x400;
        case 332:
          i |= 0x1;
        case 331:
          i |= 0x4;
        case 330:
          i |= 0x2;
        case 345:
          i |= 0x40;
        case 342:
          i |= 0x80;
        case 347:
          i |= 0x800;
      } 
    } 
    return i;
  }
}
