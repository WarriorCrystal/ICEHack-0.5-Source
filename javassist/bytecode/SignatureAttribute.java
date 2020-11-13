package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javassist.CtClass;

public class SignatureAttribute extends AttributeInfo {
  public static final String tag = "Signature";
  
  SignatureAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public SignatureAttribute(ConstPool14 paramConstPool14, String paramString) {
    super(paramConstPool14, "Signature");
    int i = paramConstPool14.addUtf8Info(paramString);
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = (byte)(i >>> 8);
    arrayOfByte[1] = (byte)i;
    set(arrayOfByte);
  }
  
  public String getSignature() {
    return getConstPool().getUtf8Info(ByteArray.readU16bit(get(), 0));
  }
  
  public void setSignature(String paramString) {
    int i = getConstPool().addUtf8Info(paramString);
    ByteArray.write16bit(i, this.info, 0);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    return new SignatureAttribute(paramConstPool14, getSignature());
  }
  
  void renameClass(String paramString1, String paramString2) {
    String str = renameClass(getSignature(), paramString1, paramString2);
    setSignature(str);
  }
  
  void renameClass(Map paramMap) {
    String str = renameClass(getSignature(), paramMap);
    setSignature(str);
  }
  
  static String renameClass(String paramString1, String paramString2, String paramString3) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put(paramString2, paramString3);
    return renameClass(paramString1, hashMap);
  }
  
  static String renameClass(String paramString, Map paramMap) {
    if (paramMap == null)
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    int j = 0;
    while (true) {
      char c;
      int m = paramString.indexOf('L', j);
      if (m < 0)
        break; 
      StringBuilder stringBuilder1 = new StringBuilder();
      int n = m;
      try {
        while ((c = paramString.charAt(++n)) != ';') {
          stringBuilder1.append(c);
          if (c == '<') {
            while ((c = paramString.charAt(++n)) != '>')
              stringBuilder1.append(c); 
            stringBuilder1.append(c);
          } 
        } 
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        break;
      } 
      j = n + 1;
      String str1 = stringBuilder1.toString();
      String str2 = (String)paramMap.get(str1);
      if (str2 != null) {
        stringBuilder.append(paramString.substring(i, m));
        stringBuilder.append('L');
        stringBuilder.append(str2);
        stringBuilder.append(c);
        i = j;
      } 
    } 
    if (i == 0)
      return paramString; 
    int k = paramString.length();
    if (i < k)
      stringBuilder.append(paramString.substring(i, k)); 
    return stringBuilder.toString();
  }
  
  private static boolean isNamePart(int paramInt) {
    return (paramInt != 59 && paramInt != 60);
  }
  
  private static class Cursor {
    int position = 0;
    
    int indexOf(String param1String, int param1Int) throws BadBytecode {
      int i = param1String.indexOf(param1Int, this.position);
      if (i < 0)
        throw SignatureAttribute.error(param1String); 
      this.position = i + 1;
      return i;
    }
    
    private Cursor() {}
  }
  
  public static class ClassSignature {
    SignatureAttribute.TypeParameter[] params;
    
    SignatureAttribute.ClassType superClass;
    
    SignatureAttribute.ClassType[] interfaces;
    
    public ClassSignature(SignatureAttribute.TypeParameter[] param1ArrayOfTypeParameter, SignatureAttribute.ClassType param1ClassType, SignatureAttribute.ClassType[] param1ArrayOfClassType) {
      this.params = (param1ArrayOfTypeParameter == null) ? new SignatureAttribute.TypeParameter[0] : param1ArrayOfTypeParameter;
      this.superClass = (param1ClassType == null) ? SignatureAttribute.ClassType.OBJECT : param1ClassType;
      this.interfaces = (param1ArrayOfClassType == null) ? new SignatureAttribute.ClassType[0] : param1ArrayOfClassType;
    }
    
    public ClassSignature(SignatureAttribute.TypeParameter[] param1ArrayOfTypeParameter) {
      this(param1ArrayOfTypeParameter, null, null);
    }
    
    public SignatureAttribute.TypeParameter[] getParameters() {
      return this.params;
    }
    
    public SignatureAttribute.ClassType getSuperClass() {
      return this.superClass;
    }
    
    public SignatureAttribute.ClassType[] getInterfaces() {
      return this.interfaces;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      SignatureAttribute.TypeParameter.toString(stringBuffer, this.params);
      stringBuffer.append(" extends ").append(this.superClass);
      if (this.interfaces.length > 0) {
        stringBuffer.append(" implements ");
        SignatureAttribute.Type.toString(stringBuffer, (SignatureAttribute.Type[])this.interfaces);
      } 
      return stringBuffer.toString();
    }
    
    public String encode() {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.params.length > 0) {
        stringBuffer.append('<');
        for (byte b1 = 0; b1 < this.params.length; b1++)
          this.params[b1].encode(stringBuffer); 
        stringBuffer.append('>');
      } 
      this.superClass.encode(stringBuffer);
      for (byte b = 0; b < this.interfaces.length; b++)
        this.interfaces[b].encode(stringBuffer); 
      return stringBuffer.toString();
    }
  }
  
  public static class MethodSignature {
    SignatureAttribute.TypeParameter[] typeParams;
    
    SignatureAttribute.Type[] params;
    
    SignatureAttribute.Type retType;
    
    SignatureAttribute.ObjectType[] exceptions;
    
    public MethodSignature(SignatureAttribute.TypeParameter[] param1ArrayOfTypeParameter, SignatureAttribute.Type[] param1ArrayOfType, SignatureAttribute.Type param1Type, SignatureAttribute.ObjectType[] param1ArrayOfObjectType) {
      this.typeParams = (param1ArrayOfTypeParameter == null) ? new SignatureAttribute.TypeParameter[0] : param1ArrayOfTypeParameter;
      this.params = (param1ArrayOfType == null) ? new SignatureAttribute.Type[0] : param1ArrayOfType;
      this.retType = (param1Type == null) ? new SignatureAttribute.BaseType("void") : param1Type;
      this.exceptions = (param1ArrayOfObjectType == null) ? new SignatureAttribute.ObjectType[0] : param1ArrayOfObjectType;
    }
    
    public SignatureAttribute.TypeParameter[] getTypeParameters() {
      return this.typeParams;
    }
    
    public SignatureAttribute.Type[] getParameterTypes() {
      return this.params;
    }
    
    public SignatureAttribute.Type getReturnType() {
      return this.retType;
    }
    
    public SignatureAttribute.ObjectType[] getExceptionTypes() {
      return this.exceptions;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      SignatureAttribute.TypeParameter.toString(stringBuffer, this.typeParams);
      stringBuffer.append(" (");
      SignatureAttribute.Type.toString(stringBuffer, this.params);
      stringBuffer.append(") ");
      stringBuffer.append(this.retType);
      if (this.exceptions.length > 0) {
        stringBuffer.append(" throws ");
        SignatureAttribute.Type.toString(stringBuffer, (SignatureAttribute.Type[])this.exceptions);
      } 
      return stringBuffer.toString();
    }
    
    public String encode() {
      StringBuffer stringBuffer = new StringBuffer();
      if (this.typeParams.length > 0) {
        stringBuffer.append('<');
        for (byte b1 = 0; b1 < this.typeParams.length; b1++)
          this.typeParams[b1].encode(stringBuffer); 
        stringBuffer.append('>');
      } 
      stringBuffer.append('(');
      byte b;
      for (b = 0; b < this.params.length; b++)
        this.params[b].encode(stringBuffer); 
      stringBuffer.append(')');
      this.retType.encode(stringBuffer);
      if (this.exceptions.length > 0)
        for (b = 0; b < this.exceptions.length; b++) {
          stringBuffer.append('^');
          this.exceptions[b].encode(stringBuffer);
        }  
      return stringBuffer.toString();
    }
  }
  
  public static class TypeParameter {
    String name;
    
    SignatureAttribute.ObjectType superClass;
    
    SignatureAttribute.ObjectType[] superInterfaces;
    
    TypeParameter(String param1String, int param1Int1, int param1Int2, SignatureAttribute.ObjectType param1ObjectType, SignatureAttribute.ObjectType[] param1ArrayOfObjectType) {
      this.name = param1String.substring(param1Int1, param1Int2);
      this.superClass = param1ObjectType;
      this.superInterfaces = param1ArrayOfObjectType;
    }
    
    public TypeParameter(String param1String, SignatureAttribute.ObjectType param1ObjectType, SignatureAttribute.ObjectType[] param1ArrayOfObjectType) {
      this.name = param1String;
      this.superClass = param1ObjectType;
      if (param1ArrayOfObjectType == null) {
        this.superInterfaces = new SignatureAttribute.ObjectType[0];
      } else {
        this.superInterfaces = param1ArrayOfObjectType;
      } 
    }
    
    public TypeParameter(String param1String) {
      this(param1String, null, null);
    }
    
    public String getName() {
      return this.name;
    }
    
    public SignatureAttribute.ObjectType getClassBound() {
      return this.superClass;
    }
    
    public SignatureAttribute.ObjectType[] getInterfaceBound() {
      return this.superInterfaces;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer(getName());
      if (this.superClass != null)
        stringBuffer.append(" extends ").append(this.superClass.toString()); 
      int i = this.superInterfaces.length;
      if (i > 0)
        for (byte b = 0; b < i; b++) {
          if (b > 0 || this.superClass != null) {
            stringBuffer.append(" & ");
          } else {
            stringBuffer.append(" extends ");
          } 
          stringBuffer.append(this.superInterfaces[b].toString());
        }  
      return stringBuffer.toString();
    }
    
    static void toString(StringBuffer param1StringBuffer, TypeParameter[] param1ArrayOfTypeParameter) {
      param1StringBuffer.append('<');
      for (byte b = 0; b < param1ArrayOfTypeParameter.length; b++) {
        if (b > 0)
          param1StringBuffer.append(", "); 
        param1StringBuffer.append(param1ArrayOfTypeParameter[b]);
      } 
      param1StringBuffer.append('>');
    }
    
    void encode(StringBuffer param1StringBuffer) {
      param1StringBuffer.append(this.name);
      if (this.superClass == null) {
        param1StringBuffer.append(":Ljava/lang/Object;");
      } else {
        param1StringBuffer.append(':');
        this.superClass.encode(param1StringBuffer);
      } 
      for (byte b = 0; b < this.superInterfaces.length; b++) {
        param1StringBuffer.append(':');
        this.superInterfaces[b].encode(param1StringBuffer);
      } 
    }
  }
  
  public static class TypeArgument {
    SignatureAttribute.ObjectType arg;
    
    char wildcard;
    
    TypeArgument(SignatureAttribute.ObjectType param1ObjectType, char param1Char) {
      this.arg = param1ObjectType;
      this.wildcard = param1Char;
    }
    
    public TypeArgument(SignatureAttribute.ObjectType param1ObjectType) {
      this(param1ObjectType, ' ');
    }
    
    public TypeArgument() {
      this(null, '*');
    }
    
    public static TypeArgument subclassOf(SignatureAttribute.ObjectType param1ObjectType) {
      return new TypeArgument(param1ObjectType, '+');
    }
    
    public static TypeArgument superOf(SignatureAttribute.ObjectType param1ObjectType) {
      return new TypeArgument(param1ObjectType, '-');
    }
    
    public char getKind() {
      return this.wildcard;
    }
    
    public boolean isWildcard() {
      return (this.wildcard != ' ');
    }
    
    public SignatureAttribute.ObjectType getType() {
      return this.arg;
    }
    
    public String toString() {
      if (this.wildcard == '*')
        return "?"; 
      String str = this.arg.toString();
      if (this.wildcard == ' ')
        return str; 
      if (this.wildcard == '+')
        return "? extends " + str; 
      return "? super " + str;
    }
    
    static void encode(StringBuffer param1StringBuffer, TypeArgument[] param1ArrayOfTypeArgument) {
      param1StringBuffer.append('<');
      for (byte b = 0; b < param1ArrayOfTypeArgument.length; b++) {
        TypeArgument typeArgument = param1ArrayOfTypeArgument[b];
        if (typeArgument.isWildcard())
          param1StringBuffer.append(typeArgument.wildcard); 
        if (typeArgument.getType() != null)
          typeArgument.getType().encode(param1StringBuffer); 
      } 
      param1StringBuffer.append('>');
    }
  }
  
  public static abstract class Type {
    abstract void encode(StringBuffer param1StringBuffer);
    
    static void toString(StringBuffer param1StringBuffer, Type[] param1ArrayOfType) {
      for (byte b = 0; b < param1ArrayOfType.length; b++) {
        if (b > 0)
          param1StringBuffer.append(", "); 
        param1StringBuffer.append(param1ArrayOfType[b]);
      } 
    }
    
    public String jvmTypeName() {
      return toString();
    }
  }
  
  public static class BaseType extends Type {
    char descriptor;
    
    BaseType(char param1Char) {
      this.descriptor = param1Char;
    }
    
    public BaseType(String param1String) {
      this(Descriptor.of(param1String).charAt(0));
    }
    
    public char getDescriptor() {
      return this.descriptor;
    }
    
    public CtClass getCtlass() {
      return Descriptor.toPrimitiveClass(this.descriptor);
    }
    
    public String toString() {
      return Descriptor.toClassName(Character.toString(this.descriptor));
    }
    
    void encode(StringBuffer param1StringBuffer) {
      param1StringBuffer.append(this.descriptor);
    }
  }
  
  public static abstract class ObjectType extends Type {
    public String encode() {
      StringBuffer stringBuffer = new StringBuffer();
      encode(stringBuffer);
      return stringBuffer.toString();
    }
  }
  
  public static class ClassType extends ObjectType {
    String name;
    
    SignatureAttribute.TypeArgument[] arguments;
    
    static ClassType make(String param1String, int param1Int1, int param1Int2, SignatureAttribute.TypeArgument[] param1ArrayOfTypeArgument, ClassType param1ClassType) {
      if (param1ClassType == null)
        return new ClassType(param1String, param1Int1, param1Int2, param1ArrayOfTypeArgument); 
      return new SignatureAttribute.NestedClassType(param1String, param1Int1, param1Int2, param1ArrayOfTypeArgument, param1ClassType);
    }
    
    ClassType(String param1String, int param1Int1, int param1Int2, SignatureAttribute.TypeArgument[] param1ArrayOfTypeArgument) {
      this.name = param1String.substring(param1Int1, param1Int2).replace('/', '.');
      this.arguments = param1ArrayOfTypeArgument;
    }
    
    public static ClassType OBJECT = new ClassType("java.lang.Object", null);
    
    public ClassType(String param1String, SignatureAttribute.TypeArgument[] param1ArrayOfTypeArgument) {
      this.name = param1String;
      this.arguments = param1ArrayOfTypeArgument;
    }
    
    public ClassType(String param1String) {
      this(param1String, null);
    }
    
    public String getName() {
      return this.name;
    }
    
    public SignatureAttribute.TypeArgument[] getTypeArguments() {
      return this.arguments;
    }
    
    public ClassType getDeclaringClass() {
      return null;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      ClassType classType = getDeclaringClass();
      if (classType != null)
        stringBuffer.append(classType.toString()).append('.'); 
      return toString2(stringBuffer);
    }
    
    private String toString2(StringBuffer param1StringBuffer) {
      param1StringBuffer.append(this.name);
      if (this.arguments != null) {
        param1StringBuffer.append('<');
        int i = this.arguments.length;
        for (byte b = 0; b < i; b++) {
          if (b > 0)
            param1StringBuffer.append(", "); 
          param1StringBuffer.append(this.arguments[b].toString());
        } 
        param1StringBuffer.append('>');
      } 
      return param1StringBuffer.toString();
    }
    
    public String jvmTypeName() {
      StringBuffer stringBuffer = new StringBuffer();
      ClassType classType = getDeclaringClass();
      if (classType != null)
        stringBuffer.append(classType.jvmTypeName()).append('$'); 
      return toString2(stringBuffer);
    }
    
    void encode(StringBuffer param1StringBuffer) {
      param1StringBuffer.append('L');
      encode2(param1StringBuffer);
      param1StringBuffer.append(';');
    }
    
    void encode2(StringBuffer param1StringBuffer) {
      ClassType classType = getDeclaringClass();
      if (classType != null) {
        classType.encode2(param1StringBuffer);
        param1StringBuffer.append('$');
      } 
      param1StringBuffer.append(this.name.replace('.', '/'));
      if (this.arguments != null)
        SignatureAttribute.TypeArgument.encode(param1StringBuffer, this.arguments); 
    }
  }
  
  public static class NestedClassType extends ClassType {
    SignatureAttribute.ClassType parent;
    
    NestedClassType(String param1String, int param1Int1, int param1Int2, SignatureAttribute.TypeArgument[] param1ArrayOfTypeArgument, SignatureAttribute.ClassType param1ClassType) {
      super(param1String, param1Int1, param1Int2, param1ArrayOfTypeArgument);
      this.parent = param1ClassType;
    }
    
    public NestedClassType(SignatureAttribute.ClassType param1ClassType, String param1String, SignatureAttribute.TypeArgument[] param1ArrayOfTypeArgument) {
      super(param1String, param1ArrayOfTypeArgument);
      this.parent = param1ClassType;
    }
    
    public SignatureAttribute.ClassType getDeclaringClass() {
      return this.parent;
    }
  }
  
  public static class ArrayType extends ObjectType {
    int dim;
    
    SignatureAttribute.Type componentType;
    
    public ArrayType(int param1Int, SignatureAttribute.Type param1Type) {
      this.dim = param1Int;
      this.componentType = param1Type;
    }
    
    public int getDimension() {
      return this.dim;
    }
    
    public SignatureAttribute.Type getComponentType() {
      return this.componentType;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer(this.componentType.toString());
      for (byte b = 0; b < this.dim; b++)
        stringBuffer.append("[]"); 
      return stringBuffer.toString();
    }
    
    void encode(StringBuffer param1StringBuffer) {
      for (byte b = 0; b < this.dim; b++)
        param1StringBuffer.append('['); 
      this.componentType.encode(param1StringBuffer);
    }
  }
  
  public static class TypeVariable extends ObjectType {
    String name;
    
    TypeVariable(String param1String, int param1Int1, int param1Int2) {
      this.name = param1String.substring(param1Int1, param1Int2);
    }
    
    public TypeVariable(String param1String) {
      this.name = param1String;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String toString() {
      return this.name;
    }
    
    void encode(StringBuffer param1StringBuffer) {
      param1StringBuffer.append('T').append(this.name).append(';');
    }
  }
  
  public static ClassSignature toClassSignature(String paramString) throws BadBytecode {
    try {
      return parseSig(paramString);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw error(paramString);
    } 
  }
  
  public static MethodSignature toMethodSignature(String paramString) throws BadBytecode {
    try {
      return parseMethodSig(paramString);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw error(paramString);
    } 
  }
  
  public static ObjectType toFieldSignature(String paramString) throws BadBytecode {
    try {
      return parseObjectType(paramString, new Cursor(), false);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw error(paramString);
    } 
  }
  
  public static Type toTypeSignature(String paramString) throws BadBytecode {
    try {
      return parseType(paramString, new Cursor());
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw error(paramString);
    } 
  }
  
  private static ClassSignature parseSig(String paramString) throws BadBytecode, IndexOutOfBoundsException {
    Cursor cursor = new Cursor();
    TypeParameter[] arrayOfTypeParameter = parseTypeParams(paramString, cursor);
    ClassType classType = parseClassType(paramString, cursor);
    int i = paramString.length();
    ArrayList<ClassType> arrayList = new ArrayList();
    while (cursor.position < i && paramString.charAt(cursor.position) == 'L')
      arrayList.add(parseClassType(paramString, cursor)); 
    ClassType[] arrayOfClassType = arrayList.<ClassType>toArray(new ClassType[arrayList.size()]);
    return new ClassSignature(arrayOfTypeParameter, classType, arrayOfClassType);
  }
  
  private static MethodSignature parseMethodSig(String paramString) throws BadBytecode {
    Cursor cursor = new Cursor();
    TypeParameter[] arrayOfTypeParameter = parseTypeParams(paramString, cursor);
    if (paramString.charAt(cursor.position++) != '(')
      throw error(paramString); 
    ArrayList<Type> arrayList = new ArrayList();
    while (paramString.charAt(cursor.position) != ')') {
      Type type1 = parseType(paramString, cursor);
      arrayList.add(type1);
    } 
    cursor.position++;
    Type type = parseType(paramString, cursor);
    int i = paramString.length();
    ArrayList<ObjectType> arrayList1 = new ArrayList();
    while (cursor.position < i && paramString.charAt(cursor.position) == '^') {
      cursor.position++;
      ObjectType objectType = parseObjectType(paramString, cursor, false);
      if (objectType instanceof ArrayType)
        throw error(paramString); 
      arrayList1.add(objectType);
    } 
    Type[] arrayOfType = arrayList.<Type>toArray(new Type[arrayList.size()]);
    ObjectType[] arrayOfObjectType = arrayList1.<ObjectType>toArray(new ObjectType[arrayList1.size()]);
    return new MethodSignature(arrayOfTypeParameter, arrayOfType, type, arrayOfObjectType);
  }
  
  private static TypeParameter[] parseTypeParams(String paramString, Cursor paramCursor) throws BadBytecode {
    ArrayList<TypeParameter> arrayList = new ArrayList();
    if (paramString.charAt(paramCursor.position) == '<') {
      paramCursor.position++;
      while (paramString.charAt(paramCursor.position) != '>') {
        int i = paramCursor.position;
        int j = paramCursor.indexOf(paramString, 58);
        ObjectType objectType = parseObjectType(paramString, paramCursor, true);
        ArrayList<ObjectType> arrayList1 = new ArrayList();
        while (paramString.charAt(paramCursor.position) == ':') {
          paramCursor.position++;
          ObjectType objectType1 = parseObjectType(paramString, paramCursor, false);
          arrayList1.add(objectType1);
        } 
        TypeParameter typeParameter = new TypeParameter(paramString, i, j, objectType, arrayList1.<ObjectType>toArray(new ObjectType[arrayList1.size()]));
        arrayList.add(typeParameter);
      } 
      paramCursor.position++;
    } 
    return arrayList.<TypeParameter>toArray(new TypeParameter[arrayList.size()]);
  }
  
  private static ObjectType parseObjectType(String paramString, Cursor paramCursor, boolean paramBoolean) throws BadBytecode {
    int i, j = paramCursor.position;
    switch (paramString.charAt(j)) {
      case 'L':
        return parseClassType2(paramString, paramCursor, (ClassType)null);
      case 'T':
        i = paramCursor.indexOf(paramString, 59);
        return new TypeVariable(paramString, j + 1, i);
      case '[':
        return parseArray(paramString, paramCursor);
    } 
    if (paramBoolean)
      return null; 
    throw error(paramString);
  }
  
  private static ClassType parseClassType(String paramString, Cursor paramCursor) throws BadBytecode {
    if (paramString.charAt(paramCursor.position) == 'L')
      return parseClassType2(paramString, paramCursor, (ClassType)null); 
    throw error(paramString);
  }
  
  private static ClassType parseClassType2(String paramString, Cursor paramCursor, ClassType paramClassType) throws BadBytecode {
    char c;
    TypeArgument[] arrayOfTypeArgument;
    int i = ++paramCursor.position;
    do {
      c = paramString.charAt(paramCursor.position++);
    } while (c != '$' && c != '<' && c != ';');
    int j = paramCursor.position - 1;
    if (c == '<') {
      arrayOfTypeArgument = parseTypeArgs(paramString, paramCursor);
      c = paramString.charAt(paramCursor.position++);
    } else {
      arrayOfTypeArgument = null;
    } 
    ClassType classType = ClassType.make(paramString, i, j, arrayOfTypeArgument, paramClassType);
    if (c == '$' || c == '.') {
      paramCursor.position--;
      return parseClassType2(paramString, paramCursor, classType);
    } 
    return classType;
  }
  
  private static TypeArgument[] parseTypeArgs(String paramString, Cursor paramCursor) throws BadBytecode {
    ArrayList<TypeArgument> arrayList = new ArrayList();
    char c;
    while ((c = paramString.charAt(paramCursor.position++)) != '>') {
      TypeArgument typeArgument;
      if (c == '*') {
        typeArgument = new TypeArgument(null, '*');
      } else {
        if (c != '+' && c != '-') {
          c = ' ';
          paramCursor.position--;
        } 
        typeArgument = new TypeArgument(parseObjectType(paramString, paramCursor, false), c);
      } 
      arrayList.add(typeArgument);
    } 
    return arrayList.<TypeArgument>toArray(new TypeArgument[arrayList.size()]);
  }
  
  private static ObjectType parseArray(String paramString, Cursor paramCursor) throws BadBytecode {
    byte b = 1;
    while (paramString.charAt(++paramCursor.position) == '[')
      b++; 
    return new ArrayType(b, parseType(paramString, paramCursor));
  }
  
  private static Type parseType(String paramString, Cursor paramCursor) throws BadBytecode {
    BaseType baseType;
    ObjectType objectType = parseObjectType(paramString, paramCursor, true);
    if (objectType == null)
      baseType = new BaseType(paramString.charAt(paramCursor.position++)); 
    return baseType;
  }
  
  private static BadBytecode error(String paramString) {
    return new BadBytecode("bad signature: " + paramString);
  }
}
