package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javassist.CtClass;

public final class ConstPool14 {
  LongVector items;
  
  int numOfItems;
  
  int thisClassInfo;
  
  HashMap itemsCache;
  
  public static final int CONST_Class = 7;
  
  public static final int CONST_Fieldref = 9;
  
  public static final int CONST_Methodref = 10;
  
  public static final int CONST_InterfaceMethodref = 11;
  
  public static final int CONST_String = 8;
  
  public static final int CONST_Integer = 3;
  
  public static final int CONST_Float = 4;
  
  public static final int CONST_Long = 5;
  
  public static final int CONST_Double = 6;
  
  public static final int CONST_NameAndType = 12;
  
  public static final int CONST_Utf8 = 1;
  
  public static final int CONST_MethodHandle = 15;
  
  public static final int CONST_MethodType = 16;
  
  public static final int CONST_InvokeDynamic = 18;
  
  public static final CtClass THIS = null;
  
  public static final int REF_getField = 1;
  
  public static final int REF_getStatic = 2;
  
  public static final int REF_putField = 3;
  
  public static final int REF_putStatic = 4;
  
  public static final int REF_invokeVirtual = 5;
  
  public static final int REF_invokeStatic = 6;
  
  public static final int REF_invokeSpecial = 7;
  
  public static final int REF_newInvokeSpecial = 8;
  
  public static final int REF_invokeInterface = 9;
  
  public ConstPool14(String paramString) {
    this.items = new LongVector();
    this.itemsCache = null;
    this.numOfItems = 0;
    addItem0(null);
    this.thisClassInfo = addClassInfo(paramString);
  }
  
  public ConstPool14(DataInputStream paramDataInputStream) throws IOException {
    this.itemsCache = null;
    this.thisClassInfo = 0;
    read(paramDataInputStream);
  }
  
  void prune() {
    this.itemsCache = null;
  }
  
  public int getSize() {
    return this.numOfItems;
  }
  
  public String getClassName() {
    return getClassInfo(this.thisClassInfo);
  }
  
  public int getThisClassInfo() {
    return this.thisClassInfo;
  }
  
  void setThisClassInfo(int paramInt) {
    this.thisClassInfo = paramInt;
  }
  
  ConstPool13 getItem(int paramInt) {
    return this.items.elementAt(paramInt);
  }
  
  public int getTag(int paramInt) {
    return getItem(paramInt).getTag();
  }
  
  public String getClassInfo(int paramInt) {
    ConstPool4 constPool4 = (ConstPool4)getItem(paramInt);
    if (constPool4 == null)
      return null; 
    return Descriptor.toJavaName(getUtf8Info(constPool4.name));
  }
  
  public String getClassInfoByDescriptor(int paramInt) {
    ConstPool4 constPool4 = (ConstPool4)getItem(paramInt);
    if (constPool4 == null)
      return null; 
    String str = getUtf8Info(constPool4.name);
    if (str.charAt(0) == '[')
      return str; 
    return Descriptor.of(str);
  }
  
  public int getNameAndTypeName(int paramInt) {
    ConstPool2 constPool2 = (ConstPool2)getItem(paramInt);
    return constPool2.memberName;
  }
  
  public int getNameAndTypeDescriptor(int paramInt) {
    ConstPool2 constPool2 = (ConstPool2)getItem(paramInt);
    return constPool2.typeDescriptor;
  }
  
  public int getMemberClass(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    return constPool17.classIndex;
  }
  
  public int getMemberNameAndType(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    return constPool17.nameAndTypeIndex;
  }
  
  public int getFieldrefClass(int paramInt) {
    ConstPool constPool = (ConstPool)getItem(paramInt);
    return constPool.classIndex;
  }
  
  public String getFieldrefClassName(int paramInt) {
    ConstPool constPool = (ConstPool)getItem(paramInt);
    if (constPool == null)
      return null; 
    return getClassInfo(constPool.classIndex);
  }
  
  public int getFieldrefNameAndType(int paramInt) {
    ConstPool constPool = (ConstPool)getItem(paramInt);
    return constPool.nameAndTypeIndex;
  }
  
  public String getFieldrefName(int paramInt) {
    ConstPool constPool = (ConstPool)getItem(paramInt);
    if (constPool == null)
      return null; 
    ConstPool2 constPool2 = (ConstPool2)getItem(constPool.nameAndTypeIndex);
    if (constPool2 == null)
      return null; 
    return getUtf8Info(constPool2.memberName);
  }
  
  public String getFieldrefType(int paramInt) {
    ConstPool constPool = (ConstPool)getItem(paramInt);
    if (constPool == null)
      return null; 
    ConstPool2 constPool2 = (ConstPool2)getItem(constPool.nameAndTypeIndex);
    if (constPool2 == null)
      return null; 
    return getUtf8Info(constPool2.typeDescriptor);
  }
  
  public int getMethodrefClass(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    return constPool17.classIndex;
  }
  
  public String getMethodrefClassName(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    if (constPool17 == null)
      return null; 
    return getClassInfo(constPool17.classIndex);
  }
  
  public int getMethodrefNameAndType(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    return constPool17.nameAndTypeIndex;
  }
  
  public String getMethodrefName(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    if (constPool17 == null)
      return null; 
    ConstPool2 constPool2 = (ConstPool2)getItem(constPool17.nameAndTypeIndex);
    if (constPool2 == null)
      return null; 
    return getUtf8Info(constPool2.memberName);
  }
  
  public String getMethodrefType(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    if (constPool17 == null)
      return null; 
    ConstPool2 constPool2 = (ConstPool2)getItem(constPool17.nameAndTypeIndex);
    if (constPool2 == null)
      return null; 
    return getUtf8Info(constPool2.typeDescriptor);
  }
  
  public int getInterfaceMethodrefClass(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    return constPool17.classIndex;
  }
  
  public String getInterfaceMethodrefClassName(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    return getClassInfo(constPool17.classIndex);
  }
  
  public int getInterfaceMethodrefNameAndType(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    return constPool17.nameAndTypeIndex;
  }
  
  public String getInterfaceMethodrefName(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    if (constPool17 == null)
      return null; 
    ConstPool2 constPool2 = (ConstPool2)getItem(constPool17.nameAndTypeIndex);
    if (constPool2 == null)
      return null; 
    return getUtf8Info(constPool2.memberName);
  }
  
  public String getInterfaceMethodrefType(int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    if (constPool17 == null)
      return null; 
    ConstPool2 constPool2 = (ConstPool2)getItem(constPool17.nameAndTypeIndex);
    if (constPool2 == null)
      return null; 
    return getUtf8Info(constPool2.typeDescriptor);
  }
  
  public Object getLdcValue(int paramInt) {
    ConstPool13 constPool13 = getItem(paramInt);
    String str = null;
    if (constPool13 instanceof ConstPool3) {
      str = getStringInfo(paramInt);
    } else if (constPool13 instanceof ConstPool15) {
      Float float_ = new Float(getFloatInfo(paramInt));
    } else if (constPool13 instanceof ConstPool7) {
      Integer integer = new Integer(getIntegerInfo(paramInt));
    } else if (constPool13 instanceof ConstPool16) {
      Long long_ = new Long(getLongInfo(paramInt));
    } else if (constPool13 instanceof ConstPool5) {
      Double double_ = new Double(getDoubleInfo(paramInt));
    } else {
      str = null;
    } 
    return str;
  }
  
  public int getIntegerInfo(int paramInt) {
    ConstPool7 constPool7 = (ConstPool7)getItem(paramInt);
    return constPool7.value;
  }
  
  public float getFloatInfo(int paramInt) {
    ConstPool15 constPool15 = (ConstPool15)getItem(paramInt);
    return constPool15.value;
  }
  
  public long getLongInfo(int paramInt) {
    ConstPool16 constPool16 = (ConstPool16)getItem(paramInt);
    return constPool16.value;
  }
  
  public double getDoubleInfo(int paramInt) {
    ConstPool5 constPool5 = (ConstPool5)getItem(paramInt);
    return constPool5.value;
  }
  
  public String getStringInfo(int paramInt) {
    ConstPool3 constPool3 = (ConstPool3)getItem(paramInt);
    return getUtf8Info(constPool3.string);
  }
  
  public String getUtf8Info(int paramInt) {
    ConstPool12 constPool12 = (ConstPool12)getItem(paramInt);
    return constPool12.string;
  }
  
  public int getMethodHandleKind(int paramInt) {
    ConstPool11 constPool11 = (ConstPool11)getItem(paramInt);
    return constPool11.refKind;
  }
  
  public int getMethodHandleIndex(int paramInt) {
    ConstPool11 constPool11 = (ConstPool11)getItem(paramInt);
    return constPool11.refIndex;
  }
  
  public int getMethodTypeInfo(int paramInt) {
    ConstPool1 constPool1 = (ConstPool1)getItem(paramInt);
    return constPool1.descriptor;
  }
  
  public int getInvokeDynamicBootstrap(int paramInt) {
    ConstPool6 constPool6 = (ConstPool6)getItem(paramInt);
    return constPool6.bootstrap;
  }
  
  public int getInvokeDynamicNameAndType(int paramInt) {
    ConstPool6 constPool6 = (ConstPool6)getItem(paramInt);
    return constPool6.nameAndType;
  }
  
  public String getInvokeDynamicType(int paramInt) {
    ConstPool6 constPool6 = (ConstPool6)getItem(paramInt);
    if (constPool6 == null)
      return null; 
    ConstPool2 constPool2 = (ConstPool2)getItem(constPool6.nameAndType);
    if (constPool2 == null)
      return null; 
    return getUtf8Info(constPool2.typeDescriptor);
  }
  
  public int isConstructor(String paramString, int paramInt) {
    return isMember(paramString, "<init>", paramInt);
  }
  
  public int isMember(String paramString1, String paramString2, int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    if (getClassInfo(constPool17.classIndex).equals(paramString1)) {
      ConstPool2 constPool2 = (ConstPool2)getItem(constPool17.nameAndTypeIndex);
      if (getUtf8Info(constPool2.memberName).equals(paramString2))
        return constPool2.typeDescriptor; 
    } 
    return 0;
  }
  
  public String eqMember(String paramString1, String paramString2, int paramInt) {
    ConstPool17 constPool17 = (ConstPool17)getItem(paramInt);
    ConstPool2 constPool2 = (ConstPool2)getItem(constPool17.nameAndTypeIndex);
    if (getUtf8Info(constPool2.memberName).equals(paramString1) && 
      getUtf8Info(constPool2.typeDescriptor).equals(paramString2))
      return getClassInfo(constPool17.classIndex); 
    return null;
  }
  
  private int addItem0(ConstPool13 paramConstPool13) {
    this.items.addElement(paramConstPool13);
    return this.numOfItems++;
  }
  
  private int addItem(ConstPool13 paramConstPool13) {
    if (this.itemsCache == null)
      this.itemsCache = makeItemsCache(this.items); 
    ConstPool13 constPool13 = (ConstPool13)this.itemsCache.get(paramConstPool13);
    if (constPool13 != null)
      return constPool13.index; 
    this.items.addElement(paramConstPool13);
    this.itemsCache.put(paramConstPool13, paramConstPool13);
    return this.numOfItems++;
  }
  
  public int copy(int paramInt, ConstPool14 paramConstPool14, Map paramMap) {
    if (paramInt == 0)
      return 0; 
    ConstPool13 constPool13 = getItem(paramInt);
    return constPool13.copy(this, paramConstPool14, paramMap);
  }
  
  int addConstInfoPadding() {
    return addItem0(new ConstPool10(this.numOfItems));
  }
  
  public int addClassInfo(CtClass paramCtClass) {
    if (paramCtClass == THIS)
      return this.thisClassInfo; 
    if (!paramCtClass.isArray())
      return addClassInfo(paramCtClass.getName()); 
    return addClassInfo(Descriptor.toJvmName(paramCtClass));
  }
  
  public int addClassInfo(String paramString) {
    int i = addUtf8Info(Descriptor.toJvmName(paramString));
    return addItem(new ConstPool4(i, this.numOfItems));
  }
  
  public int addNameAndTypeInfo(String paramString1, String paramString2) {
    return addNameAndTypeInfo(addUtf8Info(paramString1), addUtf8Info(paramString2));
  }
  
  public int addNameAndTypeInfo(int paramInt1, int paramInt2) {
    return addItem(new ConstPool2(paramInt1, paramInt2, this.numOfItems));
  }
  
  public int addFieldrefInfo(int paramInt, String paramString1, String paramString2) {
    int i = addNameAndTypeInfo(paramString1, paramString2);
    return addFieldrefInfo(paramInt, i);
  }
  
  public int addFieldrefInfo(int paramInt1, int paramInt2) {
    return addItem(new ConstPool(paramInt1, paramInt2, this.numOfItems));
  }
  
  public int addMethodrefInfo(int paramInt, String paramString1, String paramString2) {
    int i = addNameAndTypeInfo(paramString1, paramString2);
    return addMethodrefInfo(paramInt, i);
  }
  
  public int addMethodrefInfo(int paramInt1, int paramInt2) {
    return addItem(new ConstPool8(paramInt1, paramInt2, this.numOfItems));
  }
  
  public int addInterfaceMethodrefInfo(int paramInt, String paramString1, String paramString2) {
    int i = addNameAndTypeInfo(paramString1, paramString2);
    return addInterfaceMethodrefInfo(paramInt, i);
  }
  
  public int addInterfaceMethodrefInfo(int paramInt1, int paramInt2) {
    return addItem(new ConstPool9(paramInt1, paramInt2, this.numOfItems));
  }
  
  public int addStringInfo(String paramString) {
    int i = addUtf8Info(paramString);
    return addItem(new ConstPool3(i, this.numOfItems));
  }
  
  public int addIntegerInfo(int paramInt) {
    return addItem(new ConstPool7(paramInt, this.numOfItems));
  }
  
  public int addFloatInfo(float paramFloat) {
    return addItem(new ConstPool15(paramFloat, this.numOfItems));
  }
  
  public int addLongInfo(long paramLong) {
    int i = addItem(new ConstPool16(paramLong, this.numOfItems));
    if (i == this.numOfItems - 1)
      addConstInfoPadding(); 
    return i;
  }
  
  public int addDoubleInfo(double paramDouble) {
    int i = addItem(new ConstPool5(paramDouble, this.numOfItems));
    if (i == this.numOfItems - 1)
      addConstInfoPadding(); 
    return i;
  }
  
  public int addUtf8Info(String paramString) {
    return addItem(new ConstPool12(paramString, this.numOfItems));
  }
  
  public int addMethodHandleInfo(int paramInt1, int paramInt2) {
    return addItem(new ConstPool11(paramInt1, paramInt2, this.numOfItems));
  }
  
  public int addMethodTypeInfo(int paramInt) {
    return addItem(new ConstPool1(paramInt, this.numOfItems));
  }
  
  public int addInvokeDynamicInfo(int paramInt1, int paramInt2) {
    return addItem(new ConstPool6(paramInt1, paramInt2, this.numOfItems));
  }
  
  public Set getClassNames() {
    HashSet<String> hashSet = new HashSet();
    LongVector longVector = this.items;
    int i = this.numOfItems;
    for (byte b = 1; b < i; b++) {
      String str = longVector.elementAt(b).getClassName(this);
      if (str != null)
        hashSet.add(str); 
    } 
    return hashSet;
  }
  
  public void renameClass(String paramString1, String paramString2) {
    LongVector longVector = this.items;
    int i = this.numOfItems;
    for (byte b = 1; b < i; b++) {
      ConstPool13 constPool13 = longVector.elementAt(b);
      constPool13.renameClass(this, paramString1, paramString2, this.itemsCache);
    } 
  }
  
  public void renameClass(Map paramMap) {
    LongVector longVector = this.items;
    int i = this.numOfItems;
    for (byte b = 1; b < i; b++) {
      ConstPool13 constPool13 = longVector.elementAt(b);
      constPool13.renameClass(this, paramMap, this.itemsCache);
    } 
  }
  
  private void read(DataInputStream paramDataInputStream) throws IOException {
    int i = paramDataInputStream.readUnsignedShort();
    this.items = new LongVector(i);
    this.numOfItems = 0;
    addItem0(null);
    while (--i > 0) {
      int j = readOne(paramDataInputStream);
      if (j == 5 || j == 6) {
        addConstInfoPadding();
        i--;
      } 
    } 
  }
  
  private static HashMap makeItemsCache(LongVector paramLongVector) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    byte b = 1;
    while (true) {
      ConstPool13 constPool13 = paramLongVector.elementAt(b++);
      if (constPool13 == null)
        break; 
      hashMap.put(constPool13, constPool13);
    } 
    return hashMap;
  }
  
  private int readOne(DataInputStream paramDataInputStream) throws IOException {
    ConstPool12 constPool12;
    ConstPool7 constPool7;
    ConstPool15 constPool15;
    ConstPool16 constPool16;
    ConstPool5 constPool5;
    ConstPool4 constPool4;
    ConstPool3 constPool3;
    ConstPool constPool;
    ConstPool8 constPool8;
    ConstPool9 constPool9;
    ConstPool2 constPool2;
    ConstPool11 constPool11;
    ConstPool1 constPool1;
    ConstPool6 constPool6;
    int i = paramDataInputStream.readUnsignedByte();
    switch (i) {
      case 1:
        constPool12 = new ConstPool12(paramDataInputStream, this.numOfItems);
        addItem0(constPool12);
        return i;
      case 3:
        constPool7 = new ConstPool7(paramDataInputStream, this.numOfItems);
        addItem0(constPool7);
        return i;
      case 4:
        constPool15 = new ConstPool15(paramDataInputStream, this.numOfItems);
        addItem0(constPool15);
        return i;
      case 5:
        constPool16 = new ConstPool16(paramDataInputStream, this.numOfItems);
        addItem0(constPool16);
        return i;
      case 6:
        constPool5 = new ConstPool5(paramDataInputStream, this.numOfItems);
        addItem0(constPool5);
        return i;
      case 7:
        constPool4 = new ConstPool4(paramDataInputStream, this.numOfItems);
        addItem0(constPool4);
        return i;
      case 8:
        constPool3 = new ConstPool3(paramDataInputStream, this.numOfItems);
        addItem0(constPool3);
        return i;
      case 9:
        constPool = new ConstPool(paramDataInputStream, this.numOfItems);
        addItem0(constPool);
        return i;
      case 10:
        constPool8 = new ConstPool8(paramDataInputStream, this.numOfItems);
        addItem0(constPool8);
        return i;
      case 11:
        constPool9 = new ConstPool9(paramDataInputStream, this.numOfItems);
        addItem0(constPool9);
        return i;
      case 12:
        constPool2 = new ConstPool2(paramDataInputStream, this.numOfItems);
        addItem0(constPool2);
        return i;
      case 15:
        constPool11 = new ConstPool11(paramDataInputStream, this.numOfItems);
        addItem0(constPool11);
        return i;
      case 16:
        constPool1 = new ConstPool1(paramDataInputStream, this.numOfItems);
        addItem0(constPool1);
        return i;
      case 18:
        constPool6 = new ConstPool6(paramDataInputStream, this.numOfItems);
        addItem0(constPool6);
        return i;
    } 
    throw new IOException("invalid constant type: " + i + " at " + this.numOfItems);
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.numOfItems);
    LongVector longVector = this.items;
    int i = this.numOfItems;
    for (byte b = 1; b < i; b++)
      longVector.elementAt(b).write(paramDataOutputStream); 
  }
  
  public void print() {
    print(new PrintWriter(System.out, true));
  }
  
  public void print(PrintWriter paramPrintWriter) {
    int i = this.numOfItems;
    for (byte b = 1; b < i; b++) {
      paramPrintWriter.print(b);
      paramPrintWriter.print(" ");
      this.items.elementAt(b).print(paramPrintWriter);
    } 
  }
}
