package javassist.bytecode;

import javassist.CtClass;
import javassist.CtPrimitiveType;

public class Bytecode1 extends Bytecode implements Cloneable, Opcode {
  public static final CtClass THIS = ConstPool14.THIS;
  
  ConstPool14 constPool;
  
  int maxStack;
  
  int maxLocals;
  
  ExceptionTable tryblocks;
  
  private int stackDepth;
  
  public Bytecode1(ConstPool14 paramConstPool14, int paramInt1, int paramInt2) {
    this.constPool = paramConstPool14;
    this.maxStack = paramInt1;
    this.maxLocals = paramInt2;
    this.tryblocks = new ExceptionTable(paramConstPool14);
    this.stackDepth = 0;
  }
  
  public Bytecode1(ConstPool14 paramConstPool14) {
    this(paramConstPool14, 0, 0);
  }
  
  public Object clone() {
    try {
      Bytecode1 bytecode1 = (Bytecode1)super.clone();
      bytecode1.tryblocks = (ExceptionTable)this.tryblocks.clone();
      return bytecode1;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new RuntimeException(cloneNotSupportedException);
    } 
  }
  
  public ConstPool14 getConstPool() {
    return this.constPool;
  }
  
  public ExceptionTable getExceptionTable() {
    return this.tryblocks;
  }
  
  public CodeAttribute toCodeAttribute() {
    return new CodeAttribute(this.constPool, this.maxStack, this.maxLocals, 
        get(), this.tryblocks);
  }
  
  public int length() {
    return getSize();
  }
  
  public byte[] get() {
    return copy();
  }
  
  public int getMaxStack() {
    return this.maxStack;
  }
  
  public void setMaxStack(int paramInt) {
    this.maxStack = paramInt;
  }
  
  public int getMaxLocals() {
    return this.maxLocals;
  }
  
  public void setMaxLocals(int paramInt) {
    this.maxLocals = paramInt;
  }
  
  public void setMaxLocals(boolean paramBoolean, CtClass[] paramArrayOfCtClass, int paramInt) {
    if (!paramBoolean)
      paramInt++; 
    if (paramArrayOfCtClass != null) {
      CtClass ctClass1 = CtClass.doubleType;
      CtClass ctClass2 = CtClass.longType;
      int i = paramArrayOfCtClass.length;
      for (byte b = 0; b < i; b++) {
        CtClass ctClass = paramArrayOfCtClass[b];
        if (ctClass == ctClass1 || ctClass == ctClass2) {
          paramInt += 2;
        } else {
          paramInt++;
        } 
      } 
    } 
    this.maxLocals = paramInt;
  }
  
  public void incMaxLocals(int paramInt) {
    this.maxLocals += paramInt;
  }
  
  public void addExceptionHandler(int paramInt1, int paramInt2, int paramInt3, CtClass paramCtClass) {
    addExceptionHandler(paramInt1, paramInt2, paramInt3, this.constPool
        .addClassInfo(paramCtClass));
  }
  
  public void addExceptionHandler(int paramInt1, int paramInt2, int paramInt3, String paramString) {
    addExceptionHandler(paramInt1, paramInt2, paramInt3, this.constPool
        .addClassInfo(paramString));
  }
  
  public void addExceptionHandler(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.tryblocks.add(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public int currentPc() {
    return getSize();
  }
  
  public int read(int paramInt) {
    return super.read(paramInt);
  }
  
  public int read16bit(int paramInt) {
    int i = read(paramInt);
    int j = read(paramInt + 1);
    return (i << 8) + (j & 0xFF);
  }
  
  public int read32bit(int paramInt) {
    int i = read16bit(paramInt);
    int j = read16bit(paramInt + 2);
    return (i << 16) + (j & 0xFFFF);
  }
  
  public void write(int paramInt1, int paramInt2) {
    super.write(paramInt1, paramInt2);
  }
  
  public void write16bit(int paramInt1, int paramInt2) {
    write(paramInt1, paramInt2 >> 8);
    write(paramInt1 + 1, paramInt2);
  }
  
  public void write32bit(int paramInt1, int paramInt2) {
    write16bit(paramInt1, paramInt2 >> 16);
    write16bit(paramInt1 + 2, paramInt2);
  }
  
  public void add(int paramInt) {
    super.add(paramInt);
  }
  
  public void add32bit(int paramInt) {
    add(paramInt >> 24, paramInt >> 16, paramInt >> 8, paramInt);
  }
  
  public void addGap(int paramInt) {
    super.addGap(paramInt);
  }
  
  public void addOpcode(int paramInt) {
    add(paramInt);
    growStack(STACK_GROW[paramInt]);
  }
  
  public void growStack(int paramInt) {
    setStackDepth(this.stackDepth + paramInt);
  }
  
  public int getStackDepth() {
    return this.stackDepth;
  }
  
  public void setStackDepth(int paramInt) {
    this.stackDepth = paramInt;
    if (this.stackDepth > this.maxStack)
      this.maxStack = this.stackDepth; 
  }
  
  public void addIndex(int paramInt) {
    add(paramInt >> 8, paramInt);
  }
  
  public void addAload(int paramInt) {
    if (paramInt < 4) {
      addOpcode(42 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(25);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(25);
      addIndex(paramInt);
    } 
  }
  
  public void addAstore(int paramInt) {
    if (paramInt < 4) {
      addOpcode(75 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(58);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(58);
      addIndex(paramInt);
    } 
  }
  
  public void addIconst(int paramInt) {
    if (paramInt < 6 && -2 < paramInt) {
      addOpcode(3 + paramInt);
    } else if (paramInt <= 127 && -128 <= paramInt) {
      addOpcode(16);
      add(paramInt);
    } else if (paramInt <= 32767 && -32768 <= paramInt) {
      addOpcode(17);
      add(paramInt >> 8);
      add(paramInt);
    } else {
      addLdc(this.constPool.addIntegerInfo(paramInt));
    } 
  }
  
  public void addConstZero(CtClass paramCtClass) {
    if (paramCtClass.isPrimitive()) {
      if (paramCtClass == CtClass.longType) {
        addOpcode(9);
      } else if (paramCtClass == CtClass.floatType) {
        addOpcode(11);
      } else if (paramCtClass == CtClass.doubleType) {
        addOpcode(14);
      } else {
        if (paramCtClass == CtClass.voidType)
          throw new RuntimeException("void type?"); 
        addOpcode(3);
      } 
    } else {
      addOpcode(1);
    } 
  }
  
  public void addIload(int paramInt) {
    if (paramInt < 4) {
      addOpcode(26 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(21);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(21);
      addIndex(paramInt);
    } 
  }
  
  public void addIstore(int paramInt) {
    if (paramInt < 4) {
      addOpcode(59 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(54);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(54);
      addIndex(paramInt);
    } 
  }
  
  public void addLconst(long paramLong) {
    if (paramLong == 0L || paramLong == 1L) {
      addOpcode(9 + (int)paramLong);
    } else {
      addLdc2w(paramLong);
    } 
  }
  
  public void addLload(int paramInt) {
    if (paramInt < 4) {
      addOpcode(30 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(22);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(22);
      addIndex(paramInt);
    } 
  }
  
  public void addLstore(int paramInt) {
    if (paramInt < 4) {
      addOpcode(63 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(55);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(55);
      addIndex(paramInt);
    } 
  }
  
  public void addDconst(double paramDouble) {
    if (paramDouble == 0.0D || paramDouble == 1.0D) {
      addOpcode(14 + (int)paramDouble);
    } else {
      addLdc2w(paramDouble);
    } 
  }
  
  public void addDload(int paramInt) {
    if (paramInt < 4) {
      addOpcode(38 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(24);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(24);
      addIndex(paramInt);
    } 
  }
  
  public void addDstore(int paramInt) {
    if (paramInt < 4) {
      addOpcode(71 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(57);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(57);
      addIndex(paramInt);
    } 
  }
  
  public void addFconst(float paramFloat) {
    if (paramFloat == 0.0F || paramFloat == 1.0F || paramFloat == 2.0F) {
      addOpcode(11 + (int)paramFloat);
    } else {
      addLdc(this.constPool.addFloatInfo(paramFloat));
    } 
  }
  
  public void addFload(int paramInt) {
    if (paramInt < 4) {
      addOpcode(34 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(23);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(23);
      addIndex(paramInt);
    } 
  }
  
  public void addFstore(int paramInt) {
    if (paramInt < 4) {
      addOpcode(67 + paramInt);
    } else if (paramInt < 256) {
      addOpcode(56);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(56);
      addIndex(paramInt);
    } 
  }
  
  public int addLoad(int paramInt, CtClass paramCtClass) {
    if (paramCtClass.isPrimitive()) {
      if (paramCtClass == CtClass.booleanType || paramCtClass == CtClass.charType || paramCtClass == CtClass.byteType || paramCtClass == CtClass.shortType || paramCtClass == CtClass.intType) {
        addIload(paramInt);
      } else {
        if (paramCtClass == CtClass.longType) {
          addLload(paramInt);
          return 2;
        } 
        if (paramCtClass == CtClass.floatType) {
          addFload(paramInt);
        } else {
          if (paramCtClass == CtClass.doubleType) {
            addDload(paramInt);
            return 2;
          } 
          throw new RuntimeException("void type?");
        } 
      } 
    } else {
      addAload(paramInt);
    } 
    return 1;
  }
  
  public int addStore(int paramInt, CtClass paramCtClass) {
    if (paramCtClass.isPrimitive()) {
      if (paramCtClass == CtClass.booleanType || paramCtClass == CtClass.charType || paramCtClass == CtClass.byteType || paramCtClass == CtClass.shortType || paramCtClass == CtClass.intType) {
        addIstore(paramInt);
      } else {
        if (paramCtClass == CtClass.longType) {
          addLstore(paramInt);
          return 2;
        } 
        if (paramCtClass == CtClass.floatType) {
          addFstore(paramInt);
        } else {
          if (paramCtClass == CtClass.doubleType) {
            addDstore(paramInt);
            return 2;
          } 
          throw new RuntimeException("void type?");
        } 
      } 
    } else {
      addAstore(paramInt);
    } 
    return 1;
  }
  
  public int addLoadParameters(CtClass[] paramArrayOfCtClass, int paramInt) {
    int i = 0;
    if (paramArrayOfCtClass != null) {
      int j = paramArrayOfCtClass.length;
      for (byte b = 0; b < j; b++)
        i += addLoad(i + paramInt, paramArrayOfCtClass[b]); 
    } 
    return i;
  }
  
  public void addCheckcast(CtClass paramCtClass) {
    addOpcode(192);
    addIndex(this.constPool.addClassInfo(paramCtClass));
  }
  
  public void addCheckcast(String paramString) {
    addOpcode(192);
    addIndex(this.constPool.addClassInfo(paramString));
  }
  
  public void addInstanceof(String paramString) {
    addOpcode(193);
    addIndex(this.constPool.addClassInfo(paramString));
  }
  
  public void addGetfield(CtClass paramCtClass, String paramString1, String paramString2) {
    add(180);
    int i = this.constPool.addClassInfo(paramCtClass);
    addIndex(this.constPool.addFieldrefInfo(i, paramString1, paramString2));
    growStack(Descriptor.dataSize(paramString2) - 1);
  }
  
  public void addGetfield(String paramString1, String paramString2, String paramString3) {
    add(180);
    int i = this.constPool.addClassInfo(paramString1);
    addIndex(this.constPool.addFieldrefInfo(i, paramString2, paramString3));
    growStack(Descriptor.dataSize(paramString3) - 1);
  }
  
  public void addGetstatic(CtClass paramCtClass, String paramString1, String paramString2) {
    add(178);
    int i = this.constPool.addClassInfo(paramCtClass);
    addIndex(this.constPool.addFieldrefInfo(i, paramString1, paramString2));
    growStack(Descriptor.dataSize(paramString2));
  }
  
  public void addGetstatic(String paramString1, String paramString2, String paramString3) {
    add(178);
    int i = this.constPool.addClassInfo(paramString1);
    addIndex(this.constPool.addFieldrefInfo(i, paramString2, paramString3));
    growStack(Descriptor.dataSize(paramString3));
  }
  
  public void addInvokespecial(CtClass paramCtClass1, String paramString, CtClass paramCtClass2, CtClass[] paramArrayOfCtClass) {
    String str = Descriptor.ofMethod(paramCtClass2, paramArrayOfCtClass);
    addInvokespecial(paramCtClass1, paramString, str);
  }
  
  public void addInvokespecial(CtClass paramCtClass, String paramString1, String paramString2) {
    boolean bool = (paramCtClass == null) ? false : paramCtClass.isInterface();
    addInvokespecial(bool, this.constPool
        .addClassInfo(paramCtClass), paramString1, paramString2);
  }
  
  public void addInvokespecial(String paramString1, String paramString2, String paramString3) {
    addInvokespecial(false, this.constPool.addClassInfo(paramString1), paramString2, paramString3);
  }
  
  public void addInvokespecial(int paramInt, String paramString1, String paramString2) {
    addInvokespecial(false, paramInt, paramString1, paramString2);
  }
  
  public void addInvokespecial(boolean paramBoolean, int paramInt, String paramString1, String paramString2) {
    int i;
    if (paramBoolean) {
      i = this.constPool.addInterfaceMethodrefInfo(paramInt, paramString1, paramString2);
    } else {
      i = this.constPool.addMethodrefInfo(paramInt, paramString1, paramString2);
    } 
    addInvokespecial(i, paramString2);
  }
  
  public void addInvokespecial(int paramInt, String paramString) {
    add(183);
    addIndex(paramInt);
    growStack(Descriptor.dataSize(paramString) - 1);
  }
  
  public void addInvokestatic(CtClass paramCtClass1, String paramString, CtClass paramCtClass2, CtClass[] paramArrayOfCtClass) {
    String str = Descriptor.ofMethod(paramCtClass2, paramArrayOfCtClass);
    addInvokestatic(paramCtClass1, paramString, str);
  }
  
  public void addInvokestatic(CtClass paramCtClass, String paramString1, String paramString2) {
    boolean bool;
    if (paramCtClass == THIS) {
      bool = false;
    } else {
      bool = paramCtClass.isInterface();
    } 
    addInvokestatic(this.constPool.addClassInfo(paramCtClass), paramString1, paramString2, bool);
  }
  
  public void addInvokestatic(String paramString1, String paramString2, String paramString3) {
    addInvokestatic(this.constPool.addClassInfo(paramString1), paramString2, paramString3);
  }
  
  public void addInvokestatic(int paramInt, String paramString1, String paramString2) {
    addInvokestatic(paramInt, paramString1, paramString2, false);
  }
  
  private void addInvokestatic(int paramInt, String paramString1, String paramString2, boolean paramBoolean) {
    int i;
    add(184);
    if (paramBoolean) {
      i = this.constPool.addInterfaceMethodrefInfo(paramInt, paramString1, paramString2);
    } else {
      i = this.constPool.addMethodrefInfo(paramInt, paramString1, paramString2);
    } 
    addIndex(i);
    growStack(Descriptor.dataSize(paramString2));
  }
  
  public void addInvokevirtual(CtClass paramCtClass1, String paramString, CtClass paramCtClass2, CtClass[] paramArrayOfCtClass) {
    String str = Descriptor.ofMethod(paramCtClass2, paramArrayOfCtClass);
    addInvokevirtual(paramCtClass1, paramString, str);
  }
  
  public void addInvokevirtual(CtClass paramCtClass, String paramString1, String paramString2) {
    addInvokevirtual(this.constPool.addClassInfo(paramCtClass), paramString1, paramString2);
  }
  
  public void addInvokevirtual(String paramString1, String paramString2, String paramString3) {
    addInvokevirtual(this.constPool.addClassInfo(paramString1), paramString2, paramString3);
  }
  
  public void addInvokevirtual(int paramInt, String paramString1, String paramString2) {
    add(182);
    addIndex(this.constPool.addMethodrefInfo(paramInt, paramString1, paramString2));
    growStack(Descriptor.dataSize(paramString2) - 1);
  }
  
  public void addInvokeinterface(CtClass paramCtClass1, String paramString, CtClass paramCtClass2, CtClass[] paramArrayOfCtClass, int paramInt) {
    String str = Descriptor.ofMethod(paramCtClass2, paramArrayOfCtClass);
    addInvokeinterface(paramCtClass1, paramString, str, paramInt);
  }
  
  public void addInvokeinterface(CtClass paramCtClass, String paramString1, String paramString2, int paramInt) {
    addInvokeinterface(this.constPool.addClassInfo(paramCtClass), paramString1, paramString2, paramInt);
  }
  
  public void addInvokeinterface(String paramString1, String paramString2, String paramString3, int paramInt) {
    addInvokeinterface(this.constPool.addClassInfo(paramString1), paramString2, paramString3, paramInt);
  }
  
  public void addInvokeinterface(int paramInt1, String paramString1, String paramString2, int paramInt2) {
    add(185);
    addIndex(this.constPool.addInterfaceMethodrefInfo(paramInt1, paramString1, paramString2));
    add(paramInt2);
    add(0);
    growStack(Descriptor.dataSize(paramString2) - 1);
  }
  
  public void addInvokedynamic(int paramInt, String paramString1, String paramString2) {
    int i = this.constPool.addNameAndTypeInfo(paramString1, paramString2);
    int j = this.constPool.addInvokeDynamicInfo(paramInt, i);
    add(186);
    addIndex(j);
    add(0, 0);
    growStack(Descriptor.dataSize(paramString2));
  }
  
  public void addLdc(String paramString) {
    addLdc(this.constPool.addStringInfo(paramString));
  }
  
  public void addLdc(int paramInt) {
    if (paramInt > 255) {
      addOpcode(19);
      addIndex(paramInt);
    } else {
      addOpcode(18);
      add(paramInt);
    } 
  }
  
  public void addLdc2w(long paramLong) {
    addOpcode(20);
    addIndex(this.constPool.addLongInfo(paramLong));
  }
  
  public void addLdc2w(double paramDouble) {
    addOpcode(20);
    addIndex(this.constPool.addDoubleInfo(paramDouble));
  }
  
  public void addNew(CtClass paramCtClass) {
    addOpcode(187);
    addIndex(this.constPool.addClassInfo(paramCtClass));
  }
  
  public void addNew(String paramString) {
    addOpcode(187);
    addIndex(this.constPool.addClassInfo(paramString));
  }
  
  public void addAnewarray(String paramString) {
    addOpcode(189);
    addIndex(this.constPool.addClassInfo(paramString));
  }
  
  public void addAnewarray(CtClass paramCtClass, int paramInt) {
    addIconst(paramInt);
    addOpcode(189);
    addIndex(this.constPool.addClassInfo(paramCtClass));
  }
  
  public void addNewarray(int paramInt1, int paramInt2) {
    addIconst(paramInt2);
    addOpcode(188);
    add(paramInt1);
  }
  
  public int addMultiNewarray(CtClass paramCtClass, int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    for (byte b = 0; b < i; b++)
      addIconst(paramArrayOfint[b]); 
    growStack(i);
    return addMultiNewarray(paramCtClass, i);
  }
  
  public int addMultiNewarray(CtClass paramCtClass, int paramInt) {
    add(197);
    addIndex(this.constPool.addClassInfo(paramCtClass));
    add(paramInt);
    growStack(1 - paramInt);
    return paramInt;
  }
  
  public int addMultiNewarray(String paramString, int paramInt) {
    add(197);
    addIndex(this.constPool.addClassInfo(paramString));
    add(paramInt);
    growStack(1 - paramInt);
    return paramInt;
  }
  
  public void addPutfield(CtClass paramCtClass, String paramString1, String paramString2) {
    addPutfield0(paramCtClass, (String)null, paramString1, paramString2);
  }
  
  public void addPutfield(String paramString1, String paramString2, String paramString3) {
    addPutfield0((CtClass)null, paramString1, paramString2, paramString3);
  }
  
  private void addPutfield0(CtClass paramCtClass, String paramString1, String paramString2, String paramString3) {
    add(181);
    int i = (paramString1 == null) ? this.constPool.addClassInfo(paramCtClass) : this.constPool.addClassInfo(paramString1);
    addIndex(this.constPool.addFieldrefInfo(i, paramString2, paramString3));
    growStack(-1 - Descriptor.dataSize(paramString3));
  }
  
  public void addPutstatic(CtClass paramCtClass, String paramString1, String paramString2) {
    addPutstatic0(paramCtClass, (String)null, paramString1, paramString2);
  }
  
  public void addPutstatic(String paramString1, String paramString2, String paramString3) {
    addPutstatic0((CtClass)null, paramString1, paramString2, paramString3);
  }
  
  private void addPutstatic0(CtClass paramCtClass, String paramString1, String paramString2, String paramString3) {
    add(179);
    int i = (paramString1 == null) ? this.constPool.addClassInfo(paramCtClass) : this.constPool.addClassInfo(paramString1);
    addIndex(this.constPool.addFieldrefInfo(i, paramString2, paramString3));
    growStack(-Descriptor.dataSize(paramString3));
  }
  
  public void addReturn(CtClass paramCtClass) {
    if (paramCtClass == null) {
      addOpcode(177);
    } else if (paramCtClass.isPrimitive()) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramCtClass;
      addOpcode(ctPrimitiveType.getReturnOp());
    } else {
      addOpcode(176);
    } 
  }
  
  public void addRet(int paramInt) {
    if (paramInt < 256) {
      addOpcode(169);
      add(paramInt);
    } else {
      addOpcode(196);
      addOpcode(169);
      addIndex(paramInt);
    } 
  }
  
  public void addPrintln(String paramString) {
    addGetstatic("java.lang.System", "err", "Ljava/io/PrintStream;");
    addLdc(paramString);
    addInvokevirtual("java.io.PrintStream", "println", "(Ljava/lang/String;)V");
  }
}
