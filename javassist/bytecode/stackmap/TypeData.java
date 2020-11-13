package javassist.bytecode.stackmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;

public abstract class TypeData {
  public static TypeData[] make(int paramInt) {
    TypeData[] arrayOfTypeData = new TypeData[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfTypeData[b] = TypeTag.TOP; 
    return arrayOfTypeData;
  }
  
  private static void setType(TypeData paramTypeData, String paramString, ClassPool paramClassPool) throws BadBytecode {
    paramTypeData.setType(paramString, paramClassPool);
  }
  
  public abstract int getTypeTag();
  
  public abstract int getTypeData(ConstPool14 paramConstPool14);
  
  public TypeData join() {
    return new TypeVar(this);
  }
  
  public abstract BasicType isBasicType();
  
  public abstract boolean is2WordType();
  
  public boolean isNullType() {
    return false;
  }
  
  public boolean isUninit() {
    return false;
  }
  
  public abstract boolean eq(TypeData paramTypeData);
  
  public abstract String getName();
  
  public abstract void setType(String paramString, ClassPool paramClassPool) throws BadBytecode;
  
  public abstract TypeData getArrayType(int paramInt) throws NotFoundException;
  
  public int dfs(ArrayList paramArrayList, int paramInt, ClassPool paramClassPool) throws NotFoundException {
    return paramInt;
  }
  
  protected TypeVar toTypeVar(int paramInt) {
    return null;
  }
  
  public void constructorCalled(int paramInt) {}
  
  public String toString() {
    return super.toString() + "(" + toString2(new HashSet()) + ")";
  }
  
  abstract String toString2(HashSet paramHashSet);
  
  protected static class BasicType extends TypeData {
    private String name;
    
    private int typeTag;
    
    private char decodedName;
    
    public BasicType(String param1String, int param1Int, char param1Char) {
      this.name = param1String;
      this.typeTag = param1Int;
      this.decodedName = param1Char;
    }
    
    public int getTypeTag() {
      return this.typeTag;
    }
    
    public int getTypeData(ConstPool14 param1ConstPool14) {
      return 0;
    }
    
    public TypeData join() {
      if (this == TypeTag.TOP)
        return this; 
      return super.join();
    }
    
    public BasicType isBasicType() {
      return this;
    }
    
    public boolean is2WordType() {
      return (this.typeTag == 4 || this.typeTag == 3);
    }
    
    public boolean eq(TypeData param1TypeData) {
      return (this == param1TypeData);
    }
    
    public String getName() {
      return this.name;
    }
    
    public char getDecodedName() {
      return this.decodedName;
    }
    
    public void setType(String param1String, ClassPool param1ClassPool) throws BadBytecode {
      throw new BadBytecode("conflict: " + this.name + " and " + param1String);
    }
    
    public TypeData getArrayType(int param1Int) throws NotFoundException {
      if (this == TypeTag.TOP)
        return this; 
      if (param1Int < 0)
        throw new NotFoundException("no element type: " + this.name); 
      if (param1Int == 0)
        return this; 
      char[] arrayOfChar = new char[param1Int + 1];
      for (byte b = 0; b < param1Int; b++)
        arrayOfChar[b] = '['; 
      arrayOfChar[param1Int] = this.decodedName;
      return new TypeData.ClassName(new String(arrayOfChar));
    }
    
    String toString2(HashSet param1HashSet) {
      return this.name;
    }
  }
  
  public static abstract class AbsTypeVar extends TypeData {
    public abstract void merge(TypeData param1TypeData);
    
    public int getTypeTag() {
      return 7;
    }
    
    public int getTypeData(ConstPool14 param1ConstPool14) {
      return param1ConstPool14.addClassInfo(getName());
    }
    
    public boolean eq(TypeData param1TypeData) {
      return getName().equals(param1TypeData.getName());
    }
  }
  
  public static class TypeVar extends AbsTypeVar {
    protected ArrayList lowers;
    
    protected ArrayList usedBy;
    
    protected ArrayList uppers;
    
    protected String fixedType;
    
    private boolean is2WordType;
    
    private int visited;
    
    private int smallest;
    
    private boolean inList;
    
    private int dimension;
    
    public TypeVar(TypeData param1TypeData) {
      this.visited = 0;
      this.smallest = 0;
      this.inList = false;
      this.dimension = 0;
      this.uppers = null;
      this.lowers = new ArrayList(2);
      this.usedBy = new ArrayList(2);
      merge(param1TypeData);
      this.fixedType = null;
      this.is2WordType = param1TypeData.is2WordType();
    }
    
    public String getName() {
      if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).getName(); 
      return this.fixedType;
    }
    
    public TypeData.BasicType isBasicType() {
      if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).isBasicType(); 
      return null;
    }
    
    public boolean is2WordType() {
      if (this.fixedType == null)
        return this.is2WordType; 
      return false;
    }
    
    public boolean isNullType() {
      if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).isNullType(); 
      return false;
    }
    
    public boolean isUninit() {
      if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).isUninit(); 
      return false;
    }
    
    public void merge(TypeData param1TypeData) {
      this.lowers.add(param1TypeData);
      if (param1TypeData instanceof TypeVar)
        ((TypeVar)param1TypeData).usedBy.add(this); 
    }
    
    public int getTypeTag() {
      if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).getTypeTag(); 
      return super.getTypeTag();
    }
    
    public int getTypeData(ConstPool14 param1ConstPool14) {
      if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).getTypeData(param1ConstPool14); 
      return super.getTypeData(param1ConstPool14);
    }
    
    public void setType(String param1String, ClassPool param1ClassPool) throws BadBytecode {
      if (this.uppers == null)
        this.uppers = new ArrayList(); 
      this.uppers.add(param1String);
    }
    
    protected TypeVar toTypeVar(int param1Int) {
      this.dimension = param1Int;
      return this;
    }
    
    public TypeData getArrayType(int param1Int) throws NotFoundException {
      if (param1Int == 0)
        return this; 
      TypeData.BasicType basicType = isBasicType();
      if (basicType == null) {
        if (isNullType())
          return new TypeData.NullType(); 
        return (new TypeData.ClassName(getName())).getArrayType(param1Int);
      } 
      return basicType.getArrayType(param1Int);
    }
    
    public int dfs(ArrayList<TypeVar> param1ArrayList, int param1Int, ClassPool param1ClassPool) throws NotFoundException {
      if (this.visited > 0)
        return param1Int; 
      this.visited = this.smallest = ++param1Int;
      param1ArrayList.add(this);
      this.inList = true;
      int i = this.lowers.size();
      for (byte b = 0; b < i; b++) {
        TypeVar typeVar = ((TypeData)this.lowers.get(b)).toTypeVar(this.dimension);
        if (typeVar != null)
          if (typeVar.visited == 0) {
            param1Int = typeVar.dfs(param1ArrayList, param1Int, param1ClassPool);
            if (typeVar.smallest < this.smallest)
              this.smallest = typeVar.smallest; 
          } else if (typeVar.inList && 
            typeVar.visited < this.smallest) {
            this.smallest = typeVar.visited;
          }  
      } 
      if (this.visited == this.smallest) {
        ArrayList<TypeVar> arrayList = new ArrayList();
        while (true) {
          TypeVar typeVar = param1ArrayList.remove(param1ArrayList.size() - 1);
          typeVar.inList = false;
          arrayList.add(typeVar);
          if (typeVar == this) {
            fixTypes(arrayList, param1ClassPool);
            break;
          } 
        } 
      } 
      return param1Int;
    }
    
    private void fixTypes(ArrayList<TypeVar> param1ArrayList, ClassPool param1ClassPool) throws NotFoundException {
      HashSet<String> hashSet = new HashSet();
      boolean bool = false;
      TypeData typeData = null;
      int i = param1ArrayList.size();
      for (byte b = 0; b < i; b++) {
        TypeVar typeVar = param1ArrayList.get(b);
        ArrayList<TypeData> arrayList = typeVar.lowers;
        int j = arrayList.size();
        for (byte b1 = 0; b1 < j; b1++) {
          TypeData typeData1 = arrayList.get(b1);
          TypeData typeData2 = typeData1.getArrayType(typeVar.dimension);
          TypeData.BasicType basicType = typeData2.isBasicType();
          if (typeData == null) {
            if (basicType == null) {
              bool = false;
              typeData = typeData2;
              if (typeData2.isUninit())
                break; 
            } else {
              bool = true;
              typeData = basicType;
            } 
          } else if ((basicType == null && bool) || (basicType != null && typeData != basicType)) {
            bool = true;
            typeData = TypeTag.TOP;
            break;
          } 
          if (basicType == null && !typeData2.isNullType())
            hashSet.add(typeData2.getName()); 
        } 
      } 
      if (bool) {
        this.is2WordType = typeData.is2WordType();
        fixTypes1(param1ArrayList, typeData);
      } else {
        String str = fixTypes2(param1ArrayList, hashSet, param1ClassPool);
        fixTypes1(param1ArrayList, new TypeData.ClassName(str));
      } 
    }
    
    private void fixTypes1(ArrayList<TypeVar> param1ArrayList, TypeData param1TypeData) throws NotFoundException {
      int i = param1ArrayList.size();
      for (byte b = 0; b < i; b++) {
        TypeVar typeVar = param1ArrayList.get(b);
        TypeData typeData = param1TypeData.getArrayType(-typeVar.dimension);
        if (typeData.isBasicType() == null) {
          typeVar.fixedType = typeData.getName();
        } else {
          typeVar.lowers.clear();
          typeVar.lowers.add(typeData);
          typeVar.is2WordType = typeData.is2WordType();
        } 
      } 
    }
    
    private String fixTypes2(ArrayList param1ArrayList, HashSet param1HashSet, ClassPool param1ClassPool) throws NotFoundException {
      Iterator<String> iterator = param1HashSet.iterator();
      if (param1HashSet.size() == 0)
        return null; 
      if (param1HashSet.size() == 1)
        return iterator.next(); 
      CtClass ctClass = param1ClassPool.get(iterator.next());
      while (iterator.hasNext())
        ctClass = commonSuperClassEx(ctClass, param1ClassPool.get(iterator.next())); 
      if (ctClass.getSuperclass() == null || isObjectArray(ctClass))
        ctClass = fixByUppers(param1ArrayList, param1ClassPool, new HashSet(), ctClass); 
      if (ctClass.isArray())
        return Descriptor.toJvmName(ctClass); 
      return ctClass.getName();
    }
    
    private static boolean isObjectArray(CtClass param1CtClass) throws NotFoundException {
      return (param1CtClass.isArray() && param1CtClass.getComponentType().getSuperclass() == null);
    }
    
    private CtClass fixByUppers(ArrayList<TypeVar> param1ArrayList, ClassPool param1ClassPool, HashSet<TypeVar> param1HashSet, CtClass param1CtClass) throws NotFoundException {
      if (param1ArrayList == null)
        return param1CtClass; 
      int i = param1ArrayList.size();
      for (byte b = 0; b < i; b++) {
        TypeVar typeVar = param1ArrayList.get(b);
        if (!param1HashSet.add(typeVar))
          return param1CtClass; 
        if (typeVar.uppers != null) {
          int j = typeVar.uppers.size();
          for (byte b1 = 0; b1 < j; b1++) {
            CtClass ctClass = param1ClassPool.get(typeVar.uppers.get(b1));
            if (ctClass.subtypeOf(param1CtClass))
              param1CtClass = ctClass; 
          } 
        } 
        param1CtClass = fixByUppers(typeVar.usedBy, param1ClassPool, param1HashSet, param1CtClass);
      } 
      return param1CtClass;
    }
    
    String toString2(HashSet<TypeVar> param1HashSet) {
      param1HashSet.add(this);
      if (this.lowers.size() > 0) {
        TypeData typeData = this.lowers.get(0);
        if (typeData != null && !param1HashSet.contains(typeData))
          return typeData.toString2(param1HashSet); 
      } 
      return "?";
    }
  }
  
  public static CtClass commonSuperClassEx(CtClass paramCtClass1, CtClass paramCtClass2) throws NotFoundException {
    if (paramCtClass1 == paramCtClass2)
      return paramCtClass1; 
    if (paramCtClass1.isArray() && paramCtClass2.isArray()) {
      CtClass ctClass1 = paramCtClass1.getComponentType();
      CtClass ctClass2 = paramCtClass2.getComponentType();
      CtClass ctClass3 = commonSuperClassEx(ctClass1, ctClass2);
      if (ctClass3 == ctClass1)
        return paramCtClass1; 
      if (ctClass3 == ctClass2)
        return paramCtClass2; 
      return paramCtClass1.getClassPool().get((ctClass3 == null) ? "java.lang.Object" : (ctClass3
          .getName() + "[]"));
    } 
    if (paramCtClass1.isPrimitive() || paramCtClass2.isPrimitive())
      return null; 
    if (paramCtClass1.isArray() || paramCtClass2.isArray())
      return paramCtClass1.getClassPool().get("java.lang.Object"); 
    return commonSuperClass(paramCtClass1, paramCtClass2);
  }
  
  public static CtClass commonSuperClass(CtClass paramCtClass1, CtClass paramCtClass2) throws NotFoundException {
    CtClass ctClass1 = paramCtClass1;
    CtClass ctClass2 = paramCtClass2;
    CtClass ctClass3 = ctClass2;
    CtClass ctClass4 = ctClass1;
    while (true) {
      if (eq(ctClass1, ctClass2) && ctClass1.getSuperclass() != null)
        return ctClass1; 
      CtClass ctClass5 = ctClass1.getSuperclass();
      CtClass ctClass6 = ctClass2.getSuperclass();
      if (ctClass6 == null) {
        ctClass2 = ctClass3;
        break;
      } 
      if (ctClass5 == null) {
        ctClass1 = ctClass4;
        ctClass4 = ctClass3;
        ctClass3 = ctClass1;
        ctClass1 = ctClass2;
        ctClass2 = ctClass3;
        break;
      } 
      ctClass1 = ctClass5;
      ctClass2 = ctClass6;
    } 
    while (true) {
      ctClass1 = ctClass1.getSuperclass();
      if (ctClass1 == null)
        break; 
      ctClass4 = ctClass4.getSuperclass();
    } 
    ctClass1 = ctClass4;
    while (!eq(ctClass1, ctClass2)) {
      ctClass1 = ctClass1.getSuperclass();
      ctClass2 = ctClass2.getSuperclass();
    } 
    return ctClass1;
  }
  
  static boolean eq(CtClass paramCtClass1, CtClass paramCtClass2) {
    return (paramCtClass1 == paramCtClass2 || (paramCtClass1 != null && paramCtClass2 != null && paramCtClass1.getName().equals(paramCtClass2.getName())));
  }
  
  public static void aastore(TypeData paramTypeData1, TypeData paramTypeData2, ClassPool paramClassPool) throws BadBytecode {
    if (paramTypeData1 instanceof AbsTypeVar && 
      !paramTypeData2.isNullType())
      ((AbsTypeVar)paramTypeData1).merge(ArrayType.make(paramTypeData2)); 
    if (paramTypeData2 instanceof AbsTypeVar)
      if (paramTypeData1 instanceof AbsTypeVar) {
        ArrayElement.make(paramTypeData1);
      } else if (paramTypeData1 instanceof ClassName) {
        if (!paramTypeData1.isNullType()) {
          String str = ArrayElement.typeName(paramTypeData1.getName());
          paramTypeData2.setType(str, paramClassPool);
        } 
      } else {
        throw new BadBytecode("bad AASTORE: " + paramTypeData1);
      }  
  }
  
  public static class ArrayType extends AbsTypeVar {
    private TypeData.AbsTypeVar element;
    
    private ArrayType(TypeData.AbsTypeVar param1AbsTypeVar) {
      this.element = param1AbsTypeVar;
    }
    
    static TypeData make(TypeData param1TypeData) throws BadBytecode {
      if (param1TypeData instanceof TypeData.ArrayElement)
        return ((TypeData.ArrayElement)param1TypeData).arrayType(); 
      if (param1TypeData instanceof TypeData.AbsTypeVar)
        return new ArrayType((TypeData.AbsTypeVar)param1TypeData); 
      if (param1TypeData instanceof TypeData.ClassName && 
        !param1TypeData.isNullType())
        return new TypeData.ClassName(typeName(param1TypeData.getName())); 
      throw new BadBytecode("bad AASTORE: " + param1TypeData);
    }
    
    public void merge(TypeData param1TypeData) {
      try {
        if (!param1TypeData.isNullType())
          this.element.merge(TypeData.ArrayElement.make(param1TypeData)); 
      } catch (BadBytecode badBytecode) {
        throw new RuntimeException("fatal: " + badBytecode);
      } 
    }
    
    public String getName() {
      return typeName(this.element.getName());
    }
    
    public TypeData.AbsTypeVar elementType() {
      return this.element;
    }
    
    public TypeData.BasicType isBasicType() {
      return null;
    }
    
    public boolean is2WordType() {
      return false;
    }
    
    public static String typeName(String param1String) {
      if (param1String.charAt(0) == '[')
        return "[" + param1String; 
      return "[L" + param1String.replace('.', '/') + ";";
    }
    
    public void setType(String param1String, ClassPool param1ClassPool) throws BadBytecode {
      this.element.setType(TypeData.ArrayElement.typeName(param1String), param1ClassPool);
    }
    
    protected TypeData.TypeVar toTypeVar(int param1Int) {
      return this.element.toTypeVar(param1Int + 1);
    }
    
    public TypeData getArrayType(int param1Int) throws NotFoundException {
      return this.element.getArrayType(param1Int + 1);
    }
    
    public int dfs(ArrayList param1ArrayList, int param1Int, ClassPool param1ClassPool) throws NotFoundException {
      return this.element.dfs(param1ArrayList, param1Int, param1ClassPool);
    }
    
    String toString2(HashSet param1HashSet) {
      return "[" + this.element.toString2(param1HashSet);
    }
  }
  
  public static class ArrayElement extends AbsTypeVar {
    private TypeData.AbsTypeVar array;
    
    private ArrayElement(TypeData.AbsTypeVar param1AbsTypeVar) {
      this.array = param1AbsTypeVar;
    }
    
    public static TypeData make(TypeData param1TypeData) throws BadBytecode {
      if (param1TypeData instanceof TypeData.ArrayType)
        return ((TypeData.ArrayType)param1TypeData).elementType(); 
      if (param1TypeData instanceof TypeData.AbsTypeVar)
        return new ArrayElement((TypeData.AbsTypeVar)param1TypeData); 
      if (param1TypeData instanceof TypeData.ClassName && 
        !param1TypeData.isNullType())
        return new TypeData.ClassName(typeName(param1TypeData.getName())); 
      throw new BadBytecode("bad AASTORE: " + param1TypeData);
    }
    
    public void merge(TypeData param1TypeData) {
      try {
        if (!param1TypeData.isNullType())
          this.array.merge(TypeData.ArrayType.make(param1TypeData)); 
      } catch (BadBytecode badBytecode) {
        throw new RuntimeException("fatal: " + badBytecode);
      } 
    }
    
    public String getName() {
      return typeName(this.array.getName());
    }
    
    public TypeData.AbsTypeVar arrayType() {
      return this.array;
    }
    
    public TypeData.BasicType isBasicType() {
      return null;
    }
    
    public boolean is2WordType() {
      return false;
    }
    
    private static String typeName(String param1String) {
      if (param1String.length() > 1 && param1String.charAt(0) == '[') {
        char c = param1String.charAt(1);
        if (c == 'L')
          return param1String.substring(2, param1String.length() - 1).replace('/', '.'); 
        if (c == '[')
          return param1String.substring(1); 
      } 
      return "java.lang.Object";
    }
    
    public void setType(String param1String, ClassPool param1ClassPool) throws BadBytecode {
      this.array.setType(TypeData.ArrayType.typeName(param1String), param1ClassPool);
    }
    
    protected TypeData.TypeVar toTypeVar(int param1Int) {
      return this.array.toTypeVar(param1Int - 1);
    }
    
    public TypeData getArrayType(int param1Int) throws NotFoundException {
      return this.array.getArrayType(param1Int - 1);
    }
    
    public int dfs(ArrayList param1ArrayList, int param1Int, ClassPool param1ClassPool) throws NotFoundException {
      return this.array.dfs(param1ArrayList, param1Int, param1ClassPool);
    }
    
    String toString2(HashSet param1HashSet) {
      return "*" + this.array.toString2(param1HashSet);
    }
  }
  
  public static class UninitTypeVar extends AbsTypeVar {
    protected TypeData type;
    
    public UninitTypeVar(TypeData.UninitData param1UninitData) {
      this.type = param1UninitData;
    }
    
    public int getTypeTag() {
      return this.type.getTypeTag();
    }
    
    public int getTypeData(ConstPool14 param1ConstPool14) {
      return this.type.getTypeData(param1ConstPool14);
    }
    
    public TypeData.BasicType isBasicType() {
      return this.type.isBasicType();
    }
    
    public boolean is2WordType() {
      return this.type.is2WordType();
    }
    
    public boolean isUninit() {
      return this.type.isUninit();
    }
    
    public boolean eq(TypeData param1TypeData) {
      return this.type.eq(param1TypeData);
    }
    
    public String getName() {
      return this.type.getName();
    }
    
    protected TypeData.TypeVar toTypeVar(int param1Int) {
      return null;
    }
    
    public TypeData join() {
      return this.type.join();
    }
    
    public void setType(String param1String, ClassPool param1ClassPool) throws BadBytecode {
      this.type.setType(param1String, param1ClassPool);
    }
    
    public void merge(TypeData param1TypeData) {
      if (!param1TypeData.eq(this.type))
        this.type = TypeTag.TOP; 
    }
    
    public void constructorCalled(int param1Int) {
      this.type.constructorCalled(param1Int);
    }
    
    public int offset() {
      if (this.type instanceof TypeData.UninitData)
        return ((TypeData.UninitData)this.type).offset; 
      throw new RuntimeException("not available");
    }
    
    public TypeData getArrayType(int param1Int) throws NotFoundException {
      return this.type.getArrayType(param1Int);
    }
    
    String toString2(HashSet param1HashSet) {
      return "";
    }
  }
  
  public static class ClassName extends TypeData {
    private String name;
    
    public ClassName(String param1String) {
      this.name = param1String;
    }
    
    public String getName() {
      return this.name;
    }
    
    public TypeData.BasicType isBasicType() {
      return null;
    }
    
    public boolean is2WordType() {
      return false;
    }
    
    public int getTypeTag() {
      return 7;
    }
    
    public int getTypeData(ConstPool14 param1ConstPool14) {
      return param1ConstPool14.addClassInfo(getName());
    }
    
    public boolean eq(TypeData param1TypeData) {
      return this.name.equals(param1TypeData.getName());
    }
    
    public void setType(String param1String, ClassPool param1ClassPool) throws BadBytecode {}
    
    public TypeData getArrayType(int param1Int) throws NotFoundException {
      if (param1Int == 0)
        return this; 
      if (param1Int > 0) {
        char[] arrayOfChar = new char[param1Int];
        for (byte b = 0; b < param1Int; b++)
          arrayOfChar[b] = '['; 
        String str = getName();
        if (str.charAt(0) != '[')
          str = "L" + str.replace('.', '/') + ";"; 
        return new ClassName(new String(arrayOfChar) + str);
      } 
      char c;
      for (c = Character.MIN_VALUE; c < -param1Int; c++) {
        if (this.name.charAt(c) != '[')
          throw new NotFoundException("no " + param1Int + " dimensional array type: " + getName()); 
      } 
      c = this.name.charAt(-param1Int);
      if (c == '[')
        return new ClassName(this.name.substring(-param1Int)); 
      if (c == 'L')
        return new ClassName(this.name.substring(-param1Int + 1, this.name.length() - 1).replace('/', '.')); 
      if (c == TypeTag.DOUBLE.decodedName)
        return TypeTag.DOUBLE; 
      if (c == TypeTag.FLOAT.decodedName)
        return TypeTag.FLOAT; 
      if (c == TypeTag.LONG.decodedName)
        return TypeTag.LONG; 
      return TypeTag.INTEGER;
    }
    
    String toString2(HashSet param1HashSet) {
      return this.name;
    }
  }
  
  public static class NullType extends ClassName {
    public NullType() {
      super("null-type");
    }
    
    public int getTypeTag() {
      return 5;
    }
    
    public boolean isNullType() {
      return true;
    }
    
    public int getTypeData(ConstPool14 param1ConstPool14) {
      return 0;
    }
    
    public TypeData getArrayType(int param1Int) {
      return this;
    }
  }
  
  public static class UninitData extends ClassName {
    int offset;
    
    boolean initialized;
    
    UninitData(int param1Int, String param1String) {
      super(param1String);
      this.offset = param1Int;
      this.initialized = false;
    }
    
    public UninitData copy() {
      return new UninitData(this.offset, getName());
    }
    
    public int getTypeTag() {
      return 8;
    }
    
    public int getTypeData(ConstPool14 param1ConstPool14) {
      return this.offset;
    }
    
    public TypeData join() {
      if (this.initialized)
        return new TypeData.TypeVar(new TypeData.ClassName(getName())); 
      return new TypeData.UninitTypeVar(copy());
    }
    
    public boolean isUninit() {
      return true;
    }
    
    public boolean eq(TypeData param1TypeData) {
      if (param1TypeData instanceof UninitData) {
        UninitData uninitData = (UninitData)param1TypeData;
        return (this.offset == uninitData.offset && getName().equals(uninitData.getName()));
      } 
      return false;
    }
    
    public int offset() {
      return this.offset;
    }
    
    public void constructorCalled(int param1Int) {
      if (param1Int == this.offset)
        this.initialized = true; 
    }
    
    String toString2(HashSet param1HashSet) {
      return getName() + "," + this.offset;
    }
  }
  
  public static class UninitThis extends UninitData {
    UninitThis(String param1String) {
      super(-1, param1String);
    }
    
    public TypeData.UninitData copy() {
      return new UninitThis(getName());
    }
    
    public int getTypeTag() {
      return 6;
    }
    
    public int getTypeData(ConstPool14 param1ConstPool14) {
      return 0;
    }
    
    String toString2(HashSet param1HashSet) {
      return "uninit:this";
    }
  }
}
