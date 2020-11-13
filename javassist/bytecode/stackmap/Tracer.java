package javassist.bytecode.stackmap;

import javassist.ClassPool;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ByteArray;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public abstract class Tracer implements TypeTag {
  protected ClassPool classPool;
  
  protected ConstPool14 cpool;
  
  protected String returnType;
  
  protected int stackTop;
  
  protected TypeData[] stackTypes;
  
  protected TypeData[] localsTypes;
  
  public Tracer(ClassPool paramClassPool, ConstPool14 paramConstPool14, int paramInt1, int paramInt2, String paramString) {
    this.classPool = paramClassPool;
    this.cpool = paramConstPool14;
    this.returnType = paramString;
    this.stackTop = 0;
    this.stackTypes = TypeData.make(paramInt1);
    this.localsTypes = TypeData.make(paramInt2);
  }
  
  public Tracer(Tracer paramTracer) {
    this.classPool = paramTracer.classPool;
    this.cpool = paramTracer.cpool;
    this.returnType = paramTracer.returnType;
    this.stackTop = paramTracer.stackTop;
    this.stackTypes = TypeData.make(paramTracer.stackTypes.length);
    this.localsTypes = TypeData.make(paramTracer.localsTypes.length);
  }
  
  protected int doOpcode(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {
    try {
      int i = paramArrayOfbyte[paramInt] & 0xFF;
      if (i < 96) {
        if (i < 54)
          return doOpcode0_53(paramInt, paramArrayOfbyte, i); 
        return doOpcode54_95(paramInt, paramArrayOfbyte, i);
      } 
      if (i < 148)
        return doOpcode96_147(paramInt, paramArrayOfbyte, i); 
      return doOpcode148_201(paramInt, paramArrayOfbyte, i);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new BadBytecode("inconsistent stack height " + arrayIndexOutOfBoundsException.getMessage(), arrayIndexOutOfBoundsException);
    } 
  }
  
  protected void visitBranch(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) throws BadBytecode {}
  
  protected void visitGoto(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) throws BadBytecode {}
  
  protected void visitReturn(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {}
  
  protected void visitThrow(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {}
  
  protected void visitTableSwitch(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3, int paramInt4) throws BadBytecode {}
  
  protected void visitLookupSwitch(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3, int paramInt4) throws BadBytecode {}
  
  protected void visitJSR(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {}
  
  protected void visitRET(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {}
  
  private int doOpcode0_53(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) throws BadBytecode {
    int i, j;
    TypeData typeData, arrayOfTypeData[] = this.stackTypes;
    switch (paramInt2) {
      case 0:
        return 1;
      case 1:
        arrayOfTypeData[this.stackTop++] = new TypeData.NullType();
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
        arrayOfTypeData[this.stackTop++] = INTEGER;
      case 9:
      case 10:
        arrayOfTypeData[this.stackTop++] = LONG;
        arrayOfTypeData[this.stackTop++] = TOP;
      case 11:
      case 12:
      case 13:
        arrayOfTypeData[this.stackTop++] = FLOAT;
      case 14:
      case 15:
        arrayOfTypeData[this.stackTop++] = DOUBLE;
        arrayOfTypeData[this.stackTop++] = TOP;
      case 16:
      case 17:
        arrayOfTypeData[this.stackTop++] = INTEGER;
        return (paramInt2 == 17) ? 3 : 2;
      case 18:
        doLDC(paramArrayOfbyte[paramInt1 + 1] & 0xFF);
        return 2;
      case 19:
      case 20:
        doLDC(ByteArray.readU16bit(paramArrayOfbyte, paramInt1 + 1));
        return 3;
      case 21:
        return doXLOAD(INTEGER, paramArrayOfbyte, paramInt1);
      case 22:
        return doXLOAD(LONG, paramArrayOfbyte, paramInt1);
      case 23:
        return doXLOAD(FLOAT, paramArrayOfbyte, paramInt1);
      case 24:
        return doXLOAD(DOUBLE, paramArrayOfbyte, paramInt1);
      case 25:
        return doALOAD(paramArrayOfbyte[paramInt1 + 1] & 0xFF);
      case 26:
      case 27:
      case 28:
      case 29:
        arrayOfTypeData[this.stackTop++] = INTEGER;
      case 30:
      case 31:
      case 32:
      case 33:
        arrayOfTypeData[this.stackTop++] = LONG;
        arrayOfTypeData[this.stackTop++] = TOP;
      case 34:
      case 35:
      case 36:
      case 37:
        arrayOfTypeData[this.stackTop++] = FLOAT;
      case 38:
      case 39:
      case 40:
      case 41:
        arrayOfTypeData[this.stackTop++] = DOUBLE;
        arrayOfTypeData[this.stackTop++] = TOP;
      case 42:
      case 43:
      case 44:
      case 45:
        i = paramInt2 - 42;
        arrayOfTypeData[this.stackTop++] = this.localsTypes[i];
      case 46:
        arrayOfTypeData[--this.stackTop - 1] = INTEGER;
      case 47:
        arrayOfTypeData[this.stackTop - 2] = LONG;
        arrayOfTypeData[this.stackTop - 1] = TOP;
      case 48:
        arrayOfTypeData[--this.stackTop - 1] = FLOAT;
      case 49:
        arrayOfTypeData[this.stackTop - 2] = DOUBLE;
        arrayOfTypeData[this.stackTop - 1] = TOP;
      case 50:
        j = --this.stackTop - 1;
        typeData = arrayOfTypeData[j];
        arrayOfTypeData[j] = TypeData.ArrayElement.make(typeData);
      case 51:
      case 52:
      case 53:
        arrayOfTypeData[--this.stackTop - 1] = INTEGER;
    } 
    throw new RuntimeException("fatal");
  }
  
  private void doLDC(int paramInt) {
    TypeData[] arrayOfTypeData = this.stackTypes;
    int i = this.cpool.getTag(paramInt);
    if (i == 8) {
      arrayOfTypeData[this.stackTop++] = new TypeData.ClassName("java.lang.String");
    } else if (i == 3) {
      arrayOfTypeData[this.stackTop++] = INTEGER;
    } else if (i == 4) {
      arrayOfTypeData[this.stackTop++] = FLOAT;
    } else if (i == 5) {
      arrayOfTypeData[this.stackTop++] = LONG;
      arrayOfTypeData[this.stackTop++] = TOP;
    } else if (i == 6) {
      arrayOfTypeData[this.stackTop++] = DOUBLE;
      arrayOfTypeData[this.stackTop++] = TOP;
    } else if (i == 7) {
      arrayOfTypeData[this.stackTop++] = new TypeData.ClassName("java.lang.Class");
    } else {
      throw new RuntimeException("bad LDC: " + i);
    } 
  }
  
  private int doXLOAD(TypeData paramTypeData, byte[] paramArrayOfbyte, int paramInt) {
    int i = paramArrayOfbyte[paramInt + 1] & 0xFF;
    return doXLOAD(i, paramTypeData);
  }
  
  private int doXLOAD(int paramInt, TypeData paramTypeData) {
    this.stackTypes[this.stackTop++] = paramTypeData;
    if (paramTypeData.is2WordType())
      this.stackTypes[this.stackTop++] = TOP; 
    return 2;
  }
  
  private int doALOAD(int paramInt) {
    this.stackTypes[this.stackTop++] = this.localsTypes[paramInt];
    return 2;
  }
  
  private int doOpcode54_95(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) throws BadBytecode {
    int i;
    int j;
    TypeData typeData;
    switch (paramInt2) {
      case 54:
        return doXSTORE(paramInt1, paramArrayOfbyte, INTEGER);
      case 55:
        return doXSTORE(paramInt1, paramArrayOfbyte, LONG);
      case 56:
        return doXSTORE(paramInt1, paramArrayOfbyte, FLOAT);
      case 57:
        return doXSTORE(paramInt1, paramArrayOfbyte, DOUBLE);
      case 58:
        return doASTORE(paramArrayOfbyte[paramInt1 + 1] & 0xFF);
      case 59:
      case 60:
      case 61:
      case 62:
        i = paramInt2 - 59;
        this.localsTypes[i] = INTEGER;
        this.stackTop--;
        return 1;
      case 63:
      case 64:
      case 65:
      case 66:
        i = paramInt2 - 63;
        this.localsTypes[i] = LONG;
        this.localsTypes[i + 1] = TOP;
        this.stackTop -= 2;
        return 1;
      case 67:
      case 68:
      case 69:
      case 70:
        i = paramInt2 - 67;
        this.localsTypes[i] = FLOAT;
        this.stackTop--;
        return 1;
      case 71:
      case 72:
      case 73:
      case 74:
        i = paramInt2 - 71;
        this.localsTypes[i] = DOUBLE;
        this.localsTypes[i + 1] = TOP;
        this.stackTop -= 2;
        return 1;
      case 75:
      case 76:
      case 77:
      case 78:
        i = paramInt2 - 75;
        doASTORE(i);
        return 1;
      case 79:
      case 80:
      case 81:
      case 82:
        this.stackTop -= (paramInt2 == 80 || paramInt2 == 82) ? 4 : 3;
        return 1;
      case 83:
        TypeData.ArrayElement.aastore(this.stackTypes[this.stackTop - 3], this.stackTypes[this.stackTop - 1], this.classPool);
        this.stackTop -= 3;
        return 1;
      case 84:
      case 85:
      case 86:
        this.stackTop -= 3;
        return 1;
      case 87:
        this.stackTop--;
        return 1;
      case 88:
        this.stackTop -= 2;
        return 1;
      case 89:
        i = this.stackTop;
        this.stackTypes[i] = this.stackTypes[i - 1];
        this.stackTop = i + 1;
        return 1;
      case 90:
      case 91:
        i = paramInt2 - 90 + 2;
        doDUP_XX(1, i);
        j = this.stackTop;
        this.stackTypes[j - i] = this.stackTypes[j];
        this.stackTop = j + 1;
        return 1;
      case 92:
        doDUP_XX(2, 2);
        this.stackTop += 2;
        return 1;
      case 93:
      case 94:
        i = paramInt2 - 93 + 3;
        doDUP_XX(2, i);
        j = this.stackTop;
        this.stackTypes[j - i] = this.stackTypes[j];
        this.stackTypes[j - i + 1] = this.stackTypes[j + 1];
        this.stackTop = j + 2;
        return 1;
      case 95:
        i = this.stackTop - 1;
        typeData = this.stackTypes[i];
        this.stackTypes[i] = this.stackTypes[i - 1];
        this.stackTypes[i - 1] = typeData;
        return 1;
    } 
    throw new RuntimeException("fatal");
  }
  
  private int doXSTORE(int paramInt, byte[] paramArrayOfbyte, TypeData paramTypeData) {
    int i = paramArrayOfbyte[paramInt + 1] & 0xFF;
    return doXSTORE(i, paramTypeData);
  }
  
  private int doXSTORE(int paramInt, TypeData paramTypeData) {
    this.stackTop--;
    this.localsTypes[paramInt] = paramTypeData;
    if (paramTypeData.is2WordType()) {
      this.stackTop--;
      this.localsTypes[paramInt + 1] = TOP;
    } 
    return 2;
  }
  
  private int doASTORE(int paramInt) {
    this.stackTop--;
    this.localsTypes[paramInt] = this.stackTypes[this.stackTop];
    return 2;
  }
  
  private void doDUP_XX(int paramInt1, int paramInt2) {
    TypeData[] arrayOfTypeData = this.stackTypes;
    int i = this.stackTop - 1;
    int j = i - paramInt2;
    while (i > j) {
      arrayOfTypeData[i + paramInt1] = arrayOfTypeData[i];
      i--;
    } 
  }
  
  private int doOpcode96_147(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    if (paramInt2 <= 131) {
      this.stackTop += Opcode.STACK_GROW[paramInt2];
      return 1;
    } 
    switch (paramInt2) {
      case 132:
        return 3;
      case 133:
        this.stackTypes[this.stackTop - 1] = LONG;
        this.stackTypes[this.stackTop] = TOP;
        this.stackTop++;
      case 134:
        this.stackTypes[this.stackTop - 1] = FLOAT;
      case 135:
        this.stackTypes[this.stackTop - 1] = DOUBLE;
        this.stackTypes[this.stackTop] = TOP;
        this.stackTop++;
      case 136:
        this.stackTypes[--this.stackTop - 1] = INTEGER;
      case 137:
        this.stackTypes[--this.stackTop - 1] = FLOAT;
      case 138:
        this.stackTypes[this.stackTop - 2] = DOUBLE;
      case 139:
        this.stackTypes[this.stackTop - 1] = INTEGER;
      case 140:
        this.stackTypes[this.stackTop - 1] = LONG;
        this.stackTypes[this.stackTop] = TOP;
        this.stackTop++;
      case 141:
        this.stackTypes[this.stackTop - 1] = DOUBLE;
        this.stackTypes[this.stackTop] = TOP;
        this.stackTop++;
      case 142:
        this.stackTypes[--this.stackTop - 1] = INTEGER;
      case 143:
        this.stackTypes[this.stackTop - 2] = LONG;
      case 144:
        this.stackTypes[--this.stackTop - 1] = FLOAT;
      case 145:
      case 146:
      case 147:
        return 1;
    } 
    throw new RuntimeException("fatal");
  }
  
  private int doOpcode148_201(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) throws BadBytecode {
    int i;
    int j;
    String str;
    int k;
    int m;
    switch (paramInt2) {
      case 148:
        this.stackTypes[this.stackTop - 4] = INTEGER;
        this.stackTop -= 3;
        break;
      case 149:
      case 150:
        this.stackTypes[--this.stackTop - 1] = INTEGER;
        break;
      case 151:
      case 152:
        this.stackTypes[this.stackTop - 4] = INTEGER;
        this.stackTop -= 3;
        break;
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
        this.stackTop--;
        visitBranch(paramInt1, paramArrayOfbyte, ByteArray.readS16bit(paramArrayOfbyte, paramInt1 + 1));
        return 3;
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
        this.stackTop -= 2;
        visitBranch(paramInt1, paramArrayOfbyte, ByteArray.readS16bit(paramArrayOfbyte, paramInt1 + 1));
        return 3;
      case 167:
        visitGoto(paramInt1, paramArrayOfbyte, ByteArray.readS16bit(paramArrayOfbyte, paramInt1 + 1));
        return 3;
      case 168:
        visitJSR(paramInt1, paramArrayOfbyte);
        return 3;
      case 169:
        visitRET(paramInt1, paramArrayOfbyte);
        return 2;
      case 170:
        this.stackTop--;
        i = (paramInt1 & 0xFFFFFFFC) + 8;
        j = ByteArray.read32bit(paramArrayOfbyte, i);
        k = ByteArray.read32bit(paramArrayOfbyte, i + 4);
        m = k - j + 1;
        visitTableSwitch(paramInt1, paramArrayOfbyte, m, i + 8, ByteArray.read32bit(paramArrayOfbyte, i - 4));
        return m * 4 + 16 - (paramInt1 & 0x3);
      case 171:
        this.stackTop--;
        i = (paramInt1 & 0xFFFFFFFC) + 8;
        j = ByteArray.read32bit(paramArrayOfbyte, i);
        visitLookupSwitch(paramInt1, paramArrayOfbyte, j, i + 4, ByteArray.read32bit(paramArrayOfbyte, i - 4));
        return j * 8 + 12 - (paramInt1 & 0x3);
      case 172:
        this.stackTop--;
        visitReturn(paramInt1, paramArrayOfbyte);
        break;
      case 173:
        this.stackTop -= 2;
        visitReturn(paramInt1, paramArrayOfbyte);
        break;
      case 174:
        this.stackTop--;
        visitReturn(paramInt1, paramArrayOfbyte);
        break;
      case 175:
        this.stackTop -= 2;
        visitReturn(paramInt1, paramArrayOfbyte);
        break;
      case 176:
        this.stackTypes[--this.stackTop].setType(this.returnType, this.classPool);
        visitReturn(paramInt1, paramArrayOfbyte);
        break;
      case 177:
        visitReturn(paramInt1, paramArrayOfbyte);
        break;
      case 178:
        return doGetField(paramInt1, paramArrayOfbyte, false);
      case 179:
        return doPutField(paramInt1, paramArrayOfbyte, false);
      case 180:
        return doGetField(paramInt1, paramArrayOfbyte, true);
      case 181:
        return doPutField(paramInt1, paramArrayOfbyte, true);
      case 182:
      case 183:
        return doInvokeMethod(paramInt1, paramArrayOfbyte, true);
      case 184:
        return doInvokeMethod(paramInt1, paramArrayOfbyte, false);
      case 185:
        return doInvokeIntfMethod(paramInt1, paramArrayOfbyte);
      case 186:
        return doInvokeDynamic(paramInt1, paramArrayOfbyte);
      case 187:
        i = ByteArray.readU16bit(paramArrayOfbyte, paramInt1 + 1);
        this.stackTypes[this.stackTop++] = new TypeData.UninitData(paramInt1, this.cpool
            .getClassInfo(i));
        return 3;
      case 188:
        return doNEWARRAY(paramInt1, paramArrayOfbyte);
      case 189:
        i = ByteArray.readU16bit(paramArrayOfbyte, paramInt1 + 1);
        str = this.cpool.getClassInfo(i).replace('.', '/');
        if (str.charAt(0) == '[') {
          str = "[" + str;
        } else {
          str = "[L" + str + ";";
        } 
        this.stackTypes[this.stackTop - 1] = new TypeData.ClassName(str);
        return 3;
      case 190:
        this.stackTypes[this.stackTop - 1].setType("[Ljava.lang.Object;", this.classPool);
        this.stackTypes[this.stackTop - 1] = INTEGER;
        break;
      case 191:
        this.stackTypes[--this.stackTop].setType("java.lang.Throwable", this.classPool);
        visitThrow(paramInt1, paramArrayOfbyte);
        break;
      case 192:
        i = ByteArray.readU16bit(paramArrayOfbyte, paramInt1 + 1);
        str = this.cpool.getClassInfo(i);
        if (str.charAt(0) == '[')
          str = str.replace('.', '/'); 
        this.stackTypes[this.stackTop - 1] = new TypeData.ClassName(str);
        return 3;
      case 193:
        this.stackTypes[this.stackTop - 1] = INTEGER;
        return 3;
      case 194:
      case 195:
        this.stackTop--;
        break;
      case 196:
        return doWIDE(paramInt1, paramArrayOfbyte);
      case 197:
        return doMultiANewArray(paramInt1, paramArrayOfbyte);
      case 198:
      case 199:
        this.stackTop--;
        visitBranch(paramInt1, paramArrayOfbyte, ByteArray.readS16bit(paramArrayOfbyte, paramInt1 + 1));
        return 3;
      case 200:
        visitGoto(paramInt1, paramArrayOfbyte, ByteArray.read32bit(paramArrayOfbyte, paramInt1 + 1));
        return 5;
      case 201:
        visitJSR(paramInt1, paramArrayOfbyte);
        return 5;
    } 
    return 1;
  }
  
  private int doWIDE(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {
    int j, i = paramArrayOfbyte[paramInt + 1] & 0xFF;
    switch (i) {
      case 21:
        doWIDE_XLOAD(paramInt, paramArrayOfbyte, INTEGER);
        return 4;
      case 22:
        doWIDE_XLOAD(paramInt, paramArrayOfbyte, LONG);
        return 4;
      case 23:
        doWIDE_XLOAD(paramInt, paramArrayOfbyte, FLOAT);
        return 4;
      case 24:
        doWIDE_XLOAD(paramInt, paramArrayOfbyte, DOUBLE);
        return 4;
      case 25:
        j = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 2);
        doALOAD(j);
        return 4;
      case 54:
        doWIDE_STORE(paramInt, paramArrayOfbyte, INTEGER);
        return 4;
      case 55:
        doWIDE_STORE(paramInt, paramArrayOfbyte, LONG);
        return 4;
      case 56:
        doWIDE_STORE(paramInt, paramArrayOfbyte, FLOAT);
        return 4;
      case 57:
        doWIDE_STORE(paramInt, paramArrayOfbyte, DOUBLE);
        return 4;
      case 58:
        j = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 2);
        doASTORE(j);
        return 4;
      case 132:
        return 6;
      case 169:
        visitRET(paramInt, paramArrayOfbyte);
        return 4;
    } 
    throw new RuntimeException("bad WIDE instruction: " + i);
  }
  
  private void doWIDE_XLOAD(int paramInt, byte[] paramArrayOfbyte, TypeData paramTypeData) {
    int i = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 2);
    doXLOAD(i, paramTypeData);
  }
  
  private void doWIDE_STORE(int paramInt, byte[] paramArrayOfbyte, TypeData paramTypeData) {
    int i = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 2);
    doXSTORE(i, paramTypeData);
  }
  
  private int doPutField(int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean) throws BadBytecode {
    int i = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 1);
    String str = this.cpool.getFieldrefType(i);
    this.stackTop -= Descriptor.dataSize(str);
    char c = str.charAt(0);
    if (c == 'L') {
      this.stackTypes[this.stackTop].setType(getFieldClassName(str, 0), this.classPool);
    } else if (c == '[') {
      this.stackTypes[this.stackTop].setType(str, this.classPool);
    } 
    setFieldTarget(paramBoolean, i);
    return 3;
  }
  
  private int doGetField(int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean) throws BadBytecode {
    int i = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 1);
    setFieldTarget(paramBoolean, i);
    String str = this.cpool.getFieldrefType(i);
    pushMemberType(str);
    return 3;
  }
  
  private void setFieldTarget(boolean paramBoolean, int paramInt) throws BadBytecode {
    if (paramBoolean) {
      String str = this.cpool.getFieldrefClassName(paramInt);
      this.stackTypes[--this.stackTop].setType(str, this.classPool);
    } 
  }
  
  private int doNEWARRAY(int paramInt, byte[] paramArrayOfbyte) {
    String str;
    int i = this.stackTop - 1;
    switch (paramArrayOfbyte[paramInt + 1] & 0xFF) {
      case 4:
        str = "[Z";
        this.stackTypes[i] = new TypeData.ClassName(str);
        return 2;
      case 5:
        str = "[C";
        this.stackTypes[i] = new TypeData.ClassName(str);
        return 2;
      case 6:
        str = "[F";
        this.stackTypes[i] = new TypeData.ClassName(str);
        return 2;
      case 7:
        str = "[D";
        this.stackTypes[i] = new TypeData.ClassName(str);
        return 2;
      case 8:
        str = "[B";
        this.stackTypes[i] = new TypeData.ClassName(str);
        return 2;
      case 9:
        str = "[S";
        this.stackTypes[i] = new TypeData.ClassName(str);
        return 2;
      case 10:
        str = "[I";
        this.stackTypes[i] = new TypeData.ClassName(str);
        return 2;
      case 11:
        str = "[J";
        this.stackTypes[i] = new TypeData.ClassName(str);
        return 2;
    } 
    throw new RuntimeException("bad newarray");
  }
  
  private int doMultiANewArray(int paramInt, byte[] paramArrayOfbyte) {
    int i = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 1);
    int j = paramArrayOfbyte[paramInt + 3] & 0xFF;
    this.stackTop -= j - 1;
    String str = this.cpool.getClassInfo(i).replace('.', '/');
    this.stackTypes[this.stackTop - 1] = new TypeData.ClassName(str);
    return 4;
  }
  
  private int doInvokeMethod(int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean) throws BadBytecode {
    int i = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 1);
    String str = this.cpool.getMethodrefType(i);
    checkParamTypes(str, 1);
    if (paramBoolean) {
      String str1 = this.cpool.getMethodrefClassName(i);
      TypeData typeData = this.stackTypes[--this.stackTop];
      if (typeData instanceof TypeData.UninitTypeVar && typeData.isUninit()) {
        constructorCalled(typeData, ((TypeData.UninitTypeVar)typeData).offset());
      } else if (typeData instanceof TypeData.UninitData) {
        constructorCalled(typeData, ((TypeData.UninitData)typeData).offset());
      } 
      typeData.setType(str1, this.classPool);
    } 
    pushMemberType(str);
    return 3;
  }
  
  private void constructorCalled(TypeData paramTypeData, int paramInt) {
    paramTypeData.constructorCalled(paramInt);
    byte b;
    for (b = 0; b < this.stackTop; b++)
      this.stackTypes[b].constructorCalled(paramInt); 
    for (b = 0; b < this.localsTypes.length; b++)
      this.localsTypes[b].constructorCalled(paramInt); 
  }
  
  private int doInvokeIntfMethod(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {
    int i = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 1);
    String str1 = this.cpool.getInterfaceMethodrefType(i);
    checkParamTypes(str1, 1);
    String str2 = this.cpool.getInterfaceMethodrefClassName(i);
    this.stackTypes[--this.stackTop].setType(str2, this.classPool);
    pushMemberType(str1);
    return 5;
  }
  
  private int doInvokeDynamic(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {
    int i = ByteArray.readU16bit(paramArrayOfbyte, paramInt + 1);
    String str = this.cpool.getInvokeDynamicType(i);
    checkParamTypes(str, 1);
    pushMemberType(str);
    return 5;
  }
  
  private void pushMemberType(String paramString) {
    int i = 0;
    if (paramString.charAt(0) == '(') {
      i = paramString.indexOf(')') + 1;
      if (i < 1)
        throw new IndexOutOfBoundsException("bad descriptor: " + paramString); 
    } 
    TypeData[] arrayOfTypeData = this.stackTypes;
    int j = this.stackTop;
    switch (paramString.charAt(i)) {
      case '[':
        arrayOfTypeData[j] = new TypeData.ClassName(paramString.substring(i));
        break;
      case 'L':
        arrayOfTypeData[j] = new TypeData.ClassName(getFieldClassName(paramString, i));
        break;
      case 'J':
        arrayOfTypeData[j] = LONG;
        arrayOfTypeData[j + 1] = TOP;
        this.stackTop += 2;
        return;
      case 'F':
        arrayOfTypeData[j] = FLOAT;
        break;
      case 'D':
        arrayOfTypeData[j] = DOUBLE;
        arrayOfTypeData[j + 1] = TOP;
        this.stackTop += 2;
        return;
      case 'V':
        return;
      default:
        arrayOfTypeData[j] = INTEGER;
        break;
    } 
    this.stackTop++;
  }
  
  private static String getFieldClassName(String paramString, int paramInt) {
    return paramString.substring(paramInt + 1, paramString.length() - 1).replace('/', '.');
  }
  
  private void checkParamTypes(String paramString, int paramInt) throws BadBytecode {
    char c = paramString.charAt(paramInt);
    if (c == ')')
      return; 
    int i = paramInt;
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
    checkParamTypes(paramString, i);
    if (!bool && (c == 'J' || c == 'D')) {
      this.stackTop -= 2;
    } else {
      this.stackTop--;
    } 
    if (bool) {
      this.stackTypes[this.stackTop].setType(paramString.substring(paramInt, i), this.classPool);
    } else if (c == 'L') {
      this.stackTypes[this.stackTop].setType(paramString.substring(paramInt + 1, i - 1).replace('/', '.'), this.classPool);
    } 
  }
}
