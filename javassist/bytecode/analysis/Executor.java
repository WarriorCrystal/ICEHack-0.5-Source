package javassist.bytecode.analysis;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class Executor implements Opcode {
  private final ConstPool14 constPool;
  
  private final ClassPool classPool;
  
  private final Type STRING_TYPE;
  
  private final Type CLASS_TYPE;
  
  private final Type THROWABLE_TYPE;
  
  private int lastPos;
  
  public Executor(ClassPool paramClassPool, ConstPool14 paramConstPool14) {
    this.constPool = paramConstPool14;
    this.classPool = paramClassPool;
    try {
      this.STRING_TYPE = getType("java.lang.String");
      this.CLASS_TYPE = getType("java.lang.Class");
      this.THROWABLE_TYPE = getType("java.lang.Throwable");
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  public void execute(MethodInfo paramMethodInfo, int paramInt, CodeIterator paramCodeIterator, Frame paramFrame, Subroutine paramSubroutine) throws BadBytecode {
    Type type3;
    int k;
    Type type2;
    int j;
    Type type1;
    int m;
    Type type4;
    int n;
    Type type5, type6;
    this.lastPos = paramInt;
    int i = paramCodeIterator.byteAt(paramInt);
    switch (i) {
      case 1:
        paramFrame.push(Type.UNINIT);
        break;
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
        paramFrame.push(Type.INTEGER);
        break;
      case 9:
      case 10:
        paramFrame.push(Type.LONG);
        paramFrame.push(Type.TOP);
        break;
      case 11:
      case 12:
      case 13:
        paramFrame.push(Type.FLOAT);
        break;
      case 14:
      case 15:
        paramFrame.push(Type.DOUBLE);
        paramFrame.push(Type.TOP);
        break;
      case 16:
      case 17:
        paramFrame.push(Type.INTEGER);
        break;
      case 18:
        evalLDC(paramCodeIterator.byteAt(paramInt + 1), paramFrame);
        break;
      case 19:
      case 20:
        evalLDC(paramCodeIterator.u16bitAt(paramInt + 1), paramFrame);
        break;
      case 21:
        evalLoad(Type.INTEGER, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 22:
        evalLoad(Type.LONG, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 23:
        evalLoad(Type.FLOAT, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 24:
        evalLoad(Type.DOUBLE, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 25:
        evalLoad(Type.OBJECT, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 26:
      case 27:
      case 28:
      case 29:
        evalLoad(Type.INTEGER, i - 26, paramFrame, paramSubroutine);
        break;
      case 30:
      case 31:
      case 32:
      case 33:
        evalLoad(Type.LONG, i - 30, paramFrame, paramSubroutine);
        break;
      case 34:
      case 35:
      case 36:
      case 37:
        evalLoad(Type.FLOAT, i - 34, paramFrame, paramSubroutine);
        break;
      case 38:
      case 39:
      case 40:
      case 41:
        evalLoad(Type.DOUBLE, i - 38, paramFrame, paramSubroutine);
        break;
      case 42:
      case 43:
      case 44:
      case 45:
        evalLoad(Type.OBJECT, i - 42, paramFrame, paramSubroutine);
        break;
      case 46:
        evalArrayLoad(Type.INTEGER, paramFrame);
        break;
      case 47:
        evalArrayLoad(Type.LONG, paramFrame);
        break;
      case 48:
        evalArrayLoad(Type.FLOAT, paramFrame);
        break;
      case 49:
        evalArrayLoad(Type.DOUBLE, paramFrame);
        break;
      case 50:
        evalArrayLoad(Type.OBJECT, paramFrame);
        break;
      case 51:
      case 52:
      case 53:
        evalArrayLoad(Type.INTEGER, paramFrame);
        break;
      case 54:
        evalStore(Type.INTEGER, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 55:
        evalStore(Type.LONG, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 56:
        evalStore(Type.FLOAT, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 57:
        evalStore(Type.DOUBLE, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 58:
        evalStore(Type.OBJECT, paramCodeIterator.byteAt(paramInt + 1), paramFrame, paramSubroutine);
        break;
      case 59:
      case 60:
      case 61:
      case 62:
        evalStore(Type.INTEGER, i - 59, paramFrame, paramSubroutine);
        break;
      case 63:
      case 64:
      case 65:
      case 66:
        evalStore(Type.LONG, i - 63, paramFrame, paramSubroutine);
        break;
      case 67:
      case 68:
      case 69:
      case 70:
        evalStore(Type.FLOAT, i - 67, paramFrame, paramSubroutine);
        break;
      case 71:
      case 72:
      case 73:
      case 74:
        evalStore(Type.DOUBLE, i - 71, paramFrame, paramSubroutine);
        break;
      case 75:
      case 76:
      case 77:
      case 78:
        evalStore(Type.OBJECT, i - 75, paramFrame, paramSubroutine);
        break;
      case 79:
        evalArrayStore(Type.INTEGER, paramFrame);
        break;
      case 80:
        evalArrayStore(Type.LONG, paramFrame);
        break;
      case 81:
        evalArrayStore(Type.FLOAT, paramFrame);
        break;
      case 82:
        evalArrayStore(Type.DOUBLE, paramFrame);
        break;
      case 83:
        evalArrayStore(Type.OBJECT, paramFrame);
        break;
      case 84:
      case 85:
      case 86:
        evalArrayStore(Type.INTEGER, paramFrame);
        break;
      case 87:
        if (paramFrame.pop() == Type.TOP)
          throw new BadBytecode("POP can not be used with a category 2 value, pos = " + paramInt); 
        break;
      case 88:
        paramFrame.pop();
        paramFrame.pop();
        break;
      case 89:
        type3 = paramFrame.peek();
        if (type3 == Type.TOP)
          throw new BadBytecode("DUP can not be used with a category 2 value, pos = " + paramInt); 
        paramFrame.push(paramFrame.peek());
        break;
      case 90:
      case 91:
        type3 = paramFrame.peek();
        if (type3 == Type.TOP)
          throw new BadBytecode("DUP can not be used with a category 2 value, pos = " + paramInt); 
        m = paramFrame.getTopIndex();
        n = m - i - 90 - 1;
        paramFrame.push(type3);
        while (m > n) {
          paramFrame.setStack(m, paramFrame.getStack(m - 1));
          m--;
        } 
        paramFrame.setStack(n, type3);
        break;
      case 92:
        paramFrame.push(paramFrame.getStack(paramFrame.getTopIndex() - 1));
        paramFrame.push(paramFrame.getStack(paramFrame.getTopIndex() - 1));
        break;
      case 93:
      case 94:
        k = paramFrame.getTopIndex();
        m = k - i - 93 - 1;
        type5 = paramFrame.getStack(paramFrame.getTopIndex() - 1);
        type6 = paramFrame.peek();
        paramFrame.push(type5);
        paramFrame.push(type6);
        while (k > m) {
          paramFrame.setStack(k, paramFrame.getStack(k - 2));
          k--;
        } 
        paramFrame.setStack(m, type6);
        paramFrame.setStack(m - 1, type5);
        break;
      case 95:
        type2 = paramFrame.pop();
        type4 = paramFrame.pop();
        if (type2.getSize() == 2 || type4.getSize() == 2)
          throw new BadBytecode("Swap can not be used with category 2 values, pos = " + paramInt); 
        paramFrame.push(type2);
        paramFrame.push(type4);
        break;
      case 96:
        evalBinaryMath(Type.INTEGER, paramFrame);
        break;
      case 97:
        evalBinaryMath(Type.LONG, paramFrame);
        break;
      case 98:
        evalBinaryMath(Type.FLOAT, paramFrame);
        break;
      case 99:
        evalBinaryMath(Type.DOUBLE, paramFrame);
        break;
      case 100:
        evalBinaryMath(Type.INTEGER, paramFrame);
        break;
      case 101:
        evalBinaryMath(Type.LONG, paramFrame);
        break;
      case 102:
        evalBinaryMath(Type.FLOAT, paramFrame);
        break;
      case 103:
        evalBinaryMath(Type.DOUBLE, paramFrame);
        break;
      case 104:
        evalBinaryMath(Type.INTEGER, paramFrame);
        break;
      case 105:
        evalBinaryMath(Type.LONG, paramFrame);
        break;
      case 106:
        evalBinaryMath(Type.FLOAT, paramFrame);
        break;
      case 107:
        evalBinaryMath(Type.DOUBLE, paramFrame);
        break;
      case 108:
        evalBinaryMath(Type.INTEGER, paramFrame);
        break;
      case 109:
        evalBinaryMath(Type.LONG, paramFrame);
        break;
      case 110:
        evalBinaryMath(Type.FLOAT, paramFrame);
        break;
      case 111:
        evalBinaryMath(Type.DOUBLE, paramFrame);
        break;
      case 112:
        evalBinaryMath(Type.INTEGER, paramFrame);
        break;
      case 113:
        evalBinaryMath(Type.LONG, paramFrame);
        break;
      case 114:
        evalBinaryMath(Type.FLOAT, paramFrame);
        break;
      case 115:
        evalBinaryMath(Type.DOUBLE, paramFrame);
        break;
      case 116:
        verifyAssignable(Type.INTEGER, simplePeek(paramFrame));
        break;
      case 117:
        verifyAssignable(Type.LONG, simplePeek(paramFrame));
        break;
      case 118:
        verifyAssignable(Type.FLOAT, simplePeek(paramFrame));
        break;
      case 119:
        verifyAssignable(Type.DOUBLE, simplePeek(paramFrame));
        break;
      case 120:
        evalShift(Type.INTEGER, paramFrame);
        break;
      case 121:
        evalShift(Type.LONG, paramFrame);
        break;
      case 122:
        evalShift(Type.INTEGER, paramFrame);
        break;
      case 123:
        evalShift(Type.LONG, paramFrame);
        break;
      case 124:
        evalShift(Type.INTEGER, paramFrame);
        break;
      case 125:
        evalShift(Type.LONG, paramFrame);
        break;
      case 126:
        evalBinaryMath(Type.INTEGER, paramFrame);
        break;
      case 127:
        evalBinaryMath(Type.LONG, paramFrame);
        break;
      case 128:
        evalBinaryMath(Type.INTEGER, paramFrame);
        break;
      case 129:
        evalBinaryMath(Type.LONG, paramFrame);
        break;
      case 130:
        evalBinaryMath(Type.INTEGER, paramFrame);
        break;
      case 131:
        evalBinaryMath(Type.LONG, paramFrame);
        break;
      case 132:
        j = paramCodeIterator.byteAt(paramInt + 1);
        verifyAssignable(Type.INTEGER, paramFrame.getLocal(j));
        access(j, Type.INTEGER, paramSubroutine);
        break;
      case 133:
        verifyAssignable(Type.INTEGER, simplePop(paramFrame));
        simplePush(Type.LONG, paramFrame);
        break;
      case 134:
        verifyAssignable(Type.INTEGER, simplePop(paramFrame));
        simplePush(Type.FLOAT, paramFrame);
        break;
      case 135:
        verifyAssignable(Type.INTEGER, simplePop(paramFrame));
        simplePush(Type.DOUBLE, paramFrame);
        break;
      case 136:
        verifyAssignable(Type.LONG, simplePop(paramFrame));
        simplePush(Type.INTEGER, paramFrame);
        break;
      case 137:
        verifyAssignable(Type.LONG, simplePop(paramFrame));
        simplePush(Type.FLOAT, paramFrame);
        break;
      case 138:
        verifyAssignable(Type.LONG, simplePop(paramFrame));
        simplePush(Type.DOUBLE, paramFrame);
        break;
      case 139:
        verifyAssignable(Type.FLOAT, simplePop(paramFrame));
        simplePush(Type.INTEGER, paramFrame);
        break;
      case 140:
        verifyAssignable(Type.FLOAT, simplePop(paramFrame));
        simplePush(Type.LONG, paramFrame);
        break;
      case 141:
        verifyAssignable(Type.FLOAT, simplePop(paramFrame));
        simplePush(Type.DOUBLE, paramFrame);
        break;
      case 142:
        verifyAssignable(Type.DOUBLE, simplePop(paramFrame));
        simplePush(Type.INTEGER, paramFrame);
        break;
      case 143:
        verifyAssignable(Type.DOUBLE, simplePop(paramFrame));
        simplePush(Type.LONG, paramFrame);
        break;
      case 144:
        verifyAssignable(Type.DOUBLE, simplePop(paramFrame));
        simplePush(Type.FLOAT, paramFrame);
        break;
      case 145:
      case 146:
      case 147:
        verifyAssignable(Type.INTEGER, paramFrame.peek());
        break;
      case 148:
        verifyAssignable(Type.LONG, simplePop(paramFrame));
        verifyAssignable(Type.LONG, simplePop(paramFrame));
        paramFrame.push(Type.INTEGER);
        break;
      case 149:
      case 150:
        verifyAssignable(Type.FLOAT, simplePop(paramFrame));
        verifyAssignable(Type.FLOAT, simplePop(paramFrame));
        paramFrame.push(Type.INTEGER);
        break;
      case 151:
      case 152:
        verifyAssignable(Type.DOUBLE, simplePop(paramFrame));
        verifyAssignable(Type.DOUBLE, simplePop(paramFrame));
        paramFrame.push(Type.INTEGER);
        break;
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
        verifyAssignable(Type.INTEGER, simplePop(paramFrame));
        break;
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
        verifyAssignable(Type.INTEGER, simplePop(paramFrame));
        verifyAssignable(Type.INTEGER, simplePop(paramFrame));
        break;
      case 165:
      case 166:
        verifyAssignable(Type.OBJECT, simplePop(paramFrame));
        verifyAssignable(Type.OBJECT, simplePop(paramFrame));
        break;
      case 168:
        paramFrame.push(Type.RETURN_ADDRESS);
        break;
      case 169:
        verifyAssignable(Type.RETURN_ADDRESS, paramFrame.getLocal(paramCodeIterator.byteAt(paramInt + 1)));
        break;
      case 170:
      case 171:
      case 172:
        verifyAssignable(Type.INTEGER, simplePop(paramFrame));
        break;
      case 173:
        verifyAssignable(Type.LONG, simplePop(paramFrame));
        break;
      case 174:
        verifyAssignable(Type.FLOAT, simplePop(paramFrame));
        break;
      case 175:
        verifyAssignable(Type.DOUBLE, simplePop(paramFrame));
        break;
      case 176:
        try {
          CtClass ctClass = Descriptor.getReturnType(paramMethodInfo.getDescriptor(), this.classPool);
          verifyAssignable(Type.get(ctClass), simplePop(paramFrame));
        } catch (NotFoundException notFoundException) {
          throw new RuntimeException(notFoundException);
        } 
        break;
      case 178:
        evalGetField(i, paramCodeIterator.u16bitAt(paramInt + 1), paramFrame);
        break;
      case 179:
        evalPutField(i, paramCodeIterator.u16bitAt(paramInt + 1), paramFrame);
        break;
      case 180:
        evalGetField(i, paramCodeIterator.u16bitAt(paramInt + 1), paramFrame);
        break;
      case 181:
        evalPutField(i, paramCodeIterator.u16bitAt(paramInt + 1), paramFrame);
        break;
      case 182:
      case 183:
      case 184:
        evalInvokeMethod(i, paramCodeIterator.u16bitAt(paramInt + 1), paramFrame);
        break;
      case 185:
        evalInvokeIntfMethod(i, paramCodeIterator.u16bitAt(paramInt + 1), paramFrame);
        break;
      case 186:
        evalInvokeDynamic(i, paramCodeIterator.u16bitAt(paramInt + 1), paramFrame);
        break;
      case 187:
        paramFrame.push(resolveClassInfo(this.constPool.getClassInfo(paramCodeIterator.u16bitAt(paramInt + 1))));
        break;
      case 188:
        evalNewArray(paramInt, paramCodeIterator, paramFrame);
        break;
      case 189:
        evalNewObjectArray(paramInt, paramCodeIterator, paramFrame);
        break;
      case 190:
        type1 = simplePop(paramFrame);
        if (!type1.isArray() && type1 != Type.UNINIT)
          throw new BadBytecode("Array length passed a non-array [pos = " + paramInt + "]: " + type1); 
        paramFrame.push(Type.INTEGER);
        break;
      case 191:
        verifyAssignable(this.THROWABLE_TYPE, simplePop(paramFrame));
        break;
      case 192:
        verifyAssignable(Type.OBJECT, simplePop(paramFrame));
        paramFrame.push(typeFromDesc(this.constPool.getClassInfoByDescriptor(paramCodeIterator.u16bitAt(paramInt + 1))));
        break;
      case 193:
        verifyAssignable(Type.OBJECT, simplePop(paramFrame));
        paramFrame.push(Type.INTEGER);
        break;
      case 194:
      case 195:
        verifyAssignable(Type.OBJECT, simplePop(paramFrame));
        break;
      case 196:
        evalWide(paramInt, paramCodeIterator, paramFrame, paramSubroutine);
        break;
      case 197:
        evalNewObjectArray(paramInt, paramCodeIterator, paramFrame);
        break;
      case 198:
      case 199:
        verifyAssignable(Type.OBJECT, simplePop(paramFrame));
        break;
      case 201:
        paramFrame.push(Type.RETURN_ADDRESS);
        break;
    } 
  }
  
  private Type zeroExtend(Type paramType) {
    if (paramType == Type.SHORT || paramType == Type.BYTE || paramType == Type.CHAR || paramType == Type.BOOLEAN)
      return Type.INTEGER; 
    return paramType;
  }
  
  private void evalArrayLoad(Type paramType, Frame paramFrame) throws BadBytecode {
    Type type1 = paramFrame.pop();
    Type type2 = paramFrame.pop();
    if (type2 == Type.UNINIT) {
      verifyAssignable(Type.INTEGER, type1);
      if (paramType == Type.OBJECT) {
        simplePush(Type.UNINIT, paramFrame);
      } else {
        simplePush(paramType, paramFrame);
      } 
      return;
    } 
    Type type3 = type2.getComponent();
    if (type3 == null)
      throw new BadBytecode("Not an array! [pos = " + this.lastPos + "]: " + type3); 
    type3 = zeroExtend(type3);
    verifyAssignable(paramType, type3);
    verifyAssignable(Type.INTEGER, type1);
    simplePush(type3, paramFrame);
  }
  
  private void evalArrayStore(Type paramType, Frame paramFrame) throws BadBytecode {
    Type type1 = simplePop(paramFrame);
    Type type2 = paramFrame.pop();
    Type type3 = paramFrame.pop();
    if (type3 == Type.UNINIT) {
      verifyAssignable(Type.INTEGER, type2);
      return;
    } 
    Type type4 = type3.getComponent();
    if (type4 == null)
      throw new BadBytecode("Not an array! [pos = " + this.lastPos + "]: " + type4); 
    type4 = zeroExtend(type4);
    verifyAssignable(paramType, type4);
    verifyAssignable(Type.INTEGER, type2);
    if (paramType == Type.OBJECT) {
      verifyAssignable(paramType, type1);
    } else {
      verifyAssignable(type4, type1);
    } 
  }
  
  private void evalBinaryMath(Type paramType, Frame paramFrame) throws BadBytecode {
    Type type1 = simplePop(paramFrame);
    Type type2 = simplePop(paramFrame);
    verifyAssignable(paramType, type1);
    verifyAssignable(paramType, type2);
    simplePush(type2, paramFrame);
  }
  
  private void evalGetField(int paramInt1, int paramInt2, Frame paramFrame) throws BadBytecode {
    String str = this.constPool.getFieldrefType(paramInt2);
    Type type = zeroExtend(typeFromDesc(str));
    if (paramInt1 == 180) {
      Type type1 = resolveClassInfo(this.constPool.getFieldrefClassName(paramInt2));
      verifyAssignable(type1, simplePop(paramFrame));
    } 
    simplePush(type, paramFrame);
  }
  
  private void evalInvokeIntfMethod(int paramInt1, int paramInt2, Frame paramFrame) throws BadBytecode {
    String str1 = this.constPool.getInterfaceMethodrefType(paramInt2);
    Type[] arrayOfType = paramTypesFromDesc(str1);
    int i = arrayOfType.length;
    while (i > 0)
      verifyAssignable(zeroExtend(arrayOfType[--i]), simplePop(paramFrame)); 
    String str2 = this.constPool.getInterfaceMethodrefClassName(paramInt2);
    Type type1 = resolveClassInfo(str2);
    verifyAssignable(type1, simplePop(paramFrame));
    Type type2 = returnTypeFromDesc(str1);
    if (type2 != Type.VOID)
      simplePush(zeroExtend(type2), paramFrame); 
  }
  
  private void evalInvokeMethod(int paramInt1, int paramInt2, Frame paramFrame) throws BadBytecode {
    String str = this.constPool.getMethodrefType(paramInt2);
    Type[] arrayOfType = paramTypesFromDesc(str);
    int i = arrayOfType.length;
    while (i > 0)
      verifyAssignable(zeroExtend(arrayOfType[--i]), simplePop(paramFrame)); 
    if (paramInt1 != 184) {
      Type type1 = resolveClassInfo(this.constPool.getMethodrefClassName(paramInt2));
      verifyAssignable(type1, simplePop(paramFrame));
    } 
    Type type = returnTypeFromDesc(str);
    if (type != Type.VOID)
      simplePush(zeroExtend(type), paramFrame); 
  }
  
  private void evalInvokeDynamic(int paramInt1, int paramInt2, Frame paramFrame) throws BadBytecode {
    String str = this.constPool.getInvokeDynamicType(paramInt2);
    Type[] arrayOfType = paramTypesFromDesc(str);
    int i = arrayOfType.length;
    while (i > 0)
      verifyAssignable(zeroExtend(arrayOfType[--i]), simplePop(paramFrame)); 
    Type type = returnTypeFromDesc(str);
    if (type != Type.VOID)
      simplePush(zeroExtend(type), paramFrame); 
  }
  
  private void evalLDC(int paramInt, Frame paramFrame) throws BadBytecode {
    Type type;
    int i = this.constPool.getTag(paramInt);
    switch (i) {
      case 8:
        type = this.STRING_TYPE;
        break;
      case 3:
        type = Type.INTEGER;
        break;
      case 4:
        type = Type.FLOAT;
        break;
      case 5:
        type = Type.LONG;
        break;
      case 6:
        type = Type.DOUBLE;
        break;
      case 7:
        type = this.CLASS_TYPE;
        break;
      default:
        throw new BadBytecode("bad LDC [pos = " + this.lastPos + "]: " + i);
    } 
    simplePush(type, paramFrame);
  }
  
  private void evalLoad(Type paramType, int paramInt, Frame paramFrame, Subroutine paramSubroutine) throws BadBytecode {
    Type type = paramFrame.getLocal(paramInt);
    verifyAssignable(paramType, type);
    simplePush(type, paramFrame);
    access(paramInt, type, paramSubroutine);
  }
  
  private void evalNewArray(int paramInt, CodeIterator paramCodeIterator, Frame paramFrame) throws BadBytecode {
    verifyAssignable(Type.INTEGER, simplePop(paramFrame));
    Type type = null;
    int i = paramCodeIterator.byteAt(paramInt + 1);
    switch (i) {
      case 4:
        type = getType("boolean[]");
        break;
      case 5:
        type = getType("char[]");
        break;
      case 8:
        type = getType("byte[]");
        break;
      case 9:
        type = getType("short[]");
        break;
      case 10:
        type = getType("int[]");
        break;
      case 11:
        type = getType("long[]");
        break;
      case 6:
        type = getType("float[]");
        break;
      case 7:
        type = getType("double[]");
        break;
      default:
        throw new BadBytecode("Invalid array type [pos = " + paramInt + "]: " + i);
    } 
    paramFrame.push(type);
  }
  
  private void evalNewObjectArray(int paramInt, CodeIterator paramCodeIterator, Frame paramFrame) throws BadBytecode {
    byte b;
    Type type = resolveClassInfo(this.constPool.getClassInfo(paramCodeIterator.u16bitAt(paramInt + 1)));
    String str = type.getCtClass().getName();
    int i = paramCodeIterator.byteAt(paramInt);
    if (i == 197) {
      b = paramCodeIterator.byteAt(paramInt + 3);
    } else {
      str = str + "[]";
      b = 1;
    } 
    while (b-- > 0)
      verifyAssignable(Type.INTEGER, simplePop(paramFrame)); 
    simplePush(getType(str), paramFrame);
  }
  
  private void evalPutField(int paramInt1, int paramInt2, Frame paramFrame) throws BadBytecode {
    String str = this.constPool.getFieldrefType(paramInt2);
    Type type = zeroExtend(typeFromDesc(str));
    verifyAssignable(type, simplePop(paramFrame));
    if (paramInt1 == 181) {
      Type type1 = resolveClassInfo(this.constPool.getFieldrefClassName(paramInt2));
      verifyAssignable(type1, simplePop(paramFrame));
    } 
  }
  
  private void evalShift(Type paramType, Frame paramFrame) throws BadBytecode {
    Type type1 = simplePop(paramFrame);
    Type type2 = simplePop(paramFrame);
    verifyAssignable(Type.INTEGER, type1);
    verifyAssignable(paramType, type2);
    simplePush(type2, paramFrame);
  }
  
  private void evalStore(Type paramType, int paramInt, Frame paramFrame, Subroutine paramSubroutine) throws BadBytecode {
    Type type = simplePop(paramFrame);
    if (paramType != Type.OBJECT || type != Type.RETURN_ADDRESS)
      verifyAssignable(paramType, type); 
    simpleSetLocal(paramInt, type, paramFrame);
    access(paramInt, type, paramSubroutine);
  }
  
  private void evalWide(int paramInt, CodeIterator paramCodeIterator, Frame paramFrame, Subroutine paramSubroutine) throws BadBytecode {
    int i = paramCodeIterator.byteAt(paramInt + 1);
    int j = paramCodeIterator.u16bitAt(paramInt + 2);
    switch (i) {
      case 21:
        evalLoad(Type.INTEGER, j, paramFrame, paramSubroutine);
        return;
      case 22:
        evalLoad(Type.LONG, j, paramFrame, paramSubroutine);
        return;
      case 23:
        evalLoad(Type.FLOAT, j, paramFrame, paramSubroutine);
        return;
      case 24:
        evalLoad(Type.DOUBLE, j, paramFrame, paramSubroutine);
        return;
      case 25:
        evalLoad(Type.OBJECT, j, paramFrame, paramSubroutine);
        return;
      case 54:
        evalStore(Type.INTEGER, j, paramFrame, paramSubroutine);
        return;
      case 55:
        evalStore(Type.LONG, j, paramFrame, paramSubroutine);
        return;
      case 56:
        evalStore(Type.FLOAT, j, paramFrame, paramSubroutine);
        return;
      case 57:
        evalStore(Type.DOUBLE, j, paramFrame, paramSubroutine);
        return;
      case 58:
        evalStore(Type.OBJECT, j, paramFrame, paramSubroutine);
        return;
      case 132:
        verifyAssignable(Type.INTEGER, paramFrame.getLocal(j));
        return;
      case 169:
        verifyAssignable(Type.RETURN_ADDRESS, paramFrame.getLocal(j));
        return;
    } 
    throw new BadBytecode("Invalid WIDE operand [pos = " + paramInt + "]: " + i);
  }
  
  private Type getType(String paramString) throws BadBytecode {
    try {
      return Type.get(this.classPool.get(paramString));
    } catch (NotFoundException notFoundException) {
      throw new BadBytecode("Could not find class [pos = " + this.lastPos + "]: " + paramString);
    } 
  }
  
  private Type[] paramTypesFromDesc(String paramString) throws BadBytecode {
    CtClass[] arrayOfCtClass = null;
    try {
      arrayOfCtClass = Descriptor.getParameterTypes(paramString, this.classPool);
    } catch (NotFoundException notFoundException) {
      throw new BadBytecode("Could not find class in descriptor [pos = " + this.lastPos + "]: " + notFoundException.getMessage());
    } 
    if (arrayOfCtClass == null)
      throw new BadBytecode("Could not obtain parameters for descriptor [pos = " + this.lastPos + "]: " + paramString); 
    Type[] arrayOfType = new Type[arrayOfCtClass.length];
    for (byte b = 0; b < arrayOfType.length; b++)
      arrayOfType[b] = Type.get(arrayOfCtClass[b]); 
    return arrayOfType;
  }
  
  private Type returnTypeFromDesc(String paramString) throws BadBytecode {
    CtClass ctClass = null;
    try {
      ctClass = Descriptor.getReturnType(paramString, this.classPool);
    } catch (NotFoundException notFoundException) {
      throw new BadBytecode("Could not find class in descriptor [pos = " + this.lastPos + "]: " + notFoundException.getMessage());
    } 
    if (ctClass == null)
      throw new BadBytecode("Could not obtain return type for descriptor [pos = " + this.lastPos + "]: " + paramString); 
    return Type.get(ctClass);
  }
  
  private Type simplePeek(Frame paramFrame) {
    Type type = paramFrame.peek();
    return (type == Type.TOP) ? paramFrame.getStack(paramFrame.getTopIndex() - 1) : type;
  }
  
  private Type simplePop(Frame paramFrame) {
    Type type = paramFrame.pop();
    return (type == Type.TOP) ? paramFrame.pop() : type;
  }
  
  private void simplePush(Type paramType, Frame paramFrame) {
    paramFrame.push(paramType);
    if (paramType.getSize() == 2)
      paramFrame.push(Type.TOP); 
  }
  
  private void access(int paramInt, Type paramType, Subroutine paramSubroutine) {
    if (paramSubroutine == null)
      return; 
    paramSubroutine.access(paramInt);
    if (paramType.getSize() == 2)
      paramSubroutine.access(paramInt + 1); 
  }
  
  private void simpleSetLocal(int paramInt, Type paramType, Frame paramFrame) {
    paramFrame.setLocal(paramInt, paramType);
    if (paramType.getSize() == 2)
      paramFrame.setLocal(paramInt + 1, Type.TOP); 
  }
  
  private Type resolveClassInfo(String paramString) throws BadBytecode {
    CtClass ctClass = null;
    try {
      if (paramString.charAt(0) == '[') {
        ctClass = Descriptor.toCtClass(paramString, this.classPool);
      } else {
        ctClass = this.classPool.get(paramString);
      } 
    } catch (NotFoundException notFoundException) {
      throw new BadBytecode("Could not find class in descriptor [pos = " + this.lastPos + "]: " + notFoundException.getMessage());
    } 
    if (ctClass == null)
      throw new BadBytecode("Could not obtain type for descriptor [pos = " + this.lastPos + "]: " + paramString); 
    return Type.get(ctClass);
  }
  
  private Type typeFromDesc(String paramString) throws BadBytecode {
    CtClass ctClass = null;
    try {
      ctClass = Descriptor.toCtClass(paramString, this.classPool);
    } catch (NotFoundException notFoundException) {
      throw new BadBytecode("Could not find class in descriptor [pos = " + this.lastPos + "]: " + notFoundException.getMessage());
    } 
    if (ctClass == null)
      throw new BadBytecode("Could not obtain type for descriptor [pos = " + this.lastPos + "]: " + paramString); 
    return Type.get(ctClass);
  }
  
  private void verifyAssignable(Type paramType1, Type paramType2) throws BadBytecode {
    if (!paramType1.isAssignableFrom(paramType2))
      throw new BadBytecode("Expected type: " + paramType1 + " Got: " + paramType2 + " [pos = " + this.lastPos + "]"); 
  }
}
