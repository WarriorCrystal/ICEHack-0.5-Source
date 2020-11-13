package javassist.bytecode;

import java.io.PrintStream;
import javassist.CtMethod;

public class InstructionPrinter implements Opcode {
  private static final String[] opcodes = Mnemonic.OPCODE;
  
  private final PrintStream stream;
  
  public InstructionPrinter(PrintStream paramPrintStream) {
    this.stream = paramPrintStream;
  }
  
  public static void print(CtMethod paramCtMethod, PrintStream paramPrintStream) {
    (new InstructionPrinter(paramPrintStream)).print(paramCtMethod);
  }
  
  public void print(CtMethod paramCtMethod) {
    MethodInfo methodInfo = paramCtMethod.getMethodInfo2();
    ConstPool14 constPool14 = methodInfo.getConstPool();
    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
    if (codeAttribute == null)
      return; 
    CodeIterator codeIterator = codeAttribute.iterator();
    while (codeIterator.hasNext()) {
      int i;
      try {
        i = codeIterator.next();
      } catch (BadBytecode badBytecode) {
        throw new RuntimeException(badBytecode);
      } 
      this.stream.println(i + ": " + instructionString(codeIterator, i, constPool14));
    } 
  }
  
  public static String instructionString(CodeIterator paramCodeIterator, int paramInt, ConstPool14 paramConstPool14) {
    int i = paramCodeIterator.byteAt(paramInt);
    if (i > opcodes.length || i < 0)
      throw new IllegalArgumentException("Invalid opcode, opcode: " + i + " pos: " + paramInt); 
    String str = opcodes[i];
    switch (i) {
      case 16:
        return str + " " + paramCodeIterator.byteAt(paramInt + 1);
      case 17:
        return str + " " + paramCodeIterator.s16bitAt(paramInt + 1);
      case 18:
        return str + " " + ldc(paramConstPool14, paramCodeIterator.byteAt(paramInt + 1));
      case 19:
      case 20:
        return str + " " + ldc(paramConstPool14, paramCodeIterator.u16bitAt(paramInt + 1));
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
        return str + " " + paramCodeIterator.byteAt(paramInt + 1);
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 198:
      case 199:
        return str + " " + (paramCodeIterator.s16bitAt(paramInt + 1) + paramInt);
      case 132:
        return str + " " + paramCodeIterator.byteAt(paramInt + 1) + ", " + paramCodeIterator.signedByteAt(paramInt + 2);
      case 167:
      case 168:
        return str + " " + (paramCodeIterator.s16bitAt(paramInt + 1) + paramInt);
      case 169:
        return str + " " + paramCodeIterator.byteAt(paramInt + 1);
      case 170:
        return tableSwitch(paramCodeIterator, paramInt);
      case 171:
        return lookupSwitch(paramCodeIterator, paramInt);
      case 178:
      case 179:
      case 180:
      case 181:
        return str + " " + fieldInfo(paramConstPool14, paramCodeIterator.u16bitAt(paramInt + 1));
      case 182:
      case 183:
      case 184:
        return str + " " + methodInfo(paramConstPool14, paramCodeIterator.u16bitAt(paramInt + 1));
      case 185:
        return str + " " + interfaceMethodInfo(paramConstPool14, paramCodeIterator.u16bitAt(paramInt + 1));
      case 186:
        return str + " " + paramCodeIterator.u16bitAt(paramInt + 1);
      case 187:
        return str + " " + classInfo(paramConstPool14, paramCodeIterator.u16bitAt(paramInt + 1));
      case 188:
        return str + " " + arrayInfo(paramCodeIterator.byteAt(paramInt + 1));
      case 189:
      case 192:
        return str + " " + classInfo(paramConstPool14, paramCodeIterator.u16bitAt(paramInt + 1));
      case 196:
        return wide(paramCodeIterator, paramInt);
      case 197:
        return str + " " + classInfo(paramConstPool14, paramCodeIterator.u16bitAt(paramInt + 1));
      case 200:
      case 201:
        return str + " " + (paramCodeIterator.s32bitAt(paramInt + 1) + paramInt);
    } 
    return str;
  }
  
  private static String wide(CodeIterator paramCodeIterator, int paramInt) {
    int i = paramCodeIterator.byteAt(paramInt + 1);
    int j = paramCodeIterator.u16bitAt(paramInt + 2);
    switch (i) {
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 132:
      case 169:
        return opcodes[i] + " " + j;
    } 
    throw new RuntimeException("Invalid WIDE operand");
  }
  
  private static String arrayInfo(int paramInt) {
    switch (paramInt) {
      case 4:
        return "boolean";
      case 5:
        return "char";
      case 8:
        return "byte";
      case 9:
        return "short";
      case 10:
        return "int";
      case 11:
        return "long";
      case 6:
        return "float";
      case 7:
        return "double";
    } 
    throw new RuntimeException("Invalid array type");
  }
  
  private static String classInfo(ConstPool14 paramConstPool14, int paramInt) {
    return "#" + paramInt + " = Class " + paramConstPool14.getClassInfo(paramInt);
  }
  
  private static String interfaceMethodInfo(ConstPool14 paramConstPool14, int paramInt) {
    return "#" + paramInt + " = Method " + paramConstPool14
      .getInterfaceMethodrefClassName(paramInt) + "." + paramConstPool14
      .getInterfaceMethodrefName(paramInt) + "(" + paramConstPool14
      .getInterfaceMethodrefType(paramInt) + ")";
  }
  
  private static String methodInfo(ConstPool14 paramConstPool14, int paramInt) {
    return "#" + paramInt + " = Method " + paramConstPool14
      .getMethodrefClassName(paramInt) + "." + paramConstPool14
      .getMethodrefName(paramInt) + "(" + paramConstPool14
      .getMethodrefType(paramInt) + ")";
  }
  
  private static String fieldInfo(ConstPool14 paramConstPool14, int paramInt) {
    return "#" + paramInt + " = Field " + paramConstPool14
      .getFieldrefClassName(paramInt) + "." + paramConstPool14
      .getFieldrefName(paramInt) + "(" + paramConstPool14
      .getFieldrefType(paramInt) + ")";
  }
  
  private static String lookupSwitch(CodeIterator paramCodeIterator, int paramInt) {
    StringBuffer stringBuffer = new StringBuffer("lookupswitch {\n");
    int i = (paramInt & 0xFFFFFFFC) + 4;
    stringBuffer.append("\t\tdefault: ").append(paramInt + paramCodeIterator.s32bitAt(i)).append("\n");
    i += 4;
    int j = paramCodeIterator.s32bitAt(i);
    i += 4;
    int k = j * 8 + i;
    for (; i < k; i += 8) {
      int m = paramCodeIterator.s32bitAt(i);
      int n = paramCodeIterator.s32bitAt(i + 4) + paramInt;
      stringBuffer.append("\t\t").append(m).append(": ").append(n).append("\n");
    } 
    stringBuffer.setCharAt(stringBuffer.length() - 1, '}');
    return stringBuffer.toString();
  }
  
  private static String tableSwitch(CodeIterator paramCodeIterator, int paramInt) {
    StringBuffer stringBuffer = new StringBuffer("tableswitch {\n");
    int i = (paramInt & 0xFFFFFFFC) + 4;
    stringBuffer.append("\t\tdefault: ").append(paramInt + paramCodeIterator.s32bitAt(i)).append("\n");
    i += 4;
    int j = paramCodeIterator.s32bitAt(i);
    i += 4;
    int k = paramCodeIterator.s32bitAt(i);
    i += 4;
    int m = (k - j + 1) * 4 + i;
    for (int n = j; i < m; i += 4, n++) {
      int i1 = paramCodeIterator.s32bitAt(i) + paramInt;
      stringBuffer.append("\t\t").append(n).append(": ").append(i1).append("\n");
    } 
    stringBuffer.setCharAt(stringBuffer.length() - 1, '}');
    return stringBuffer.toString();
  }
  
  private static String ldc(ConstPool14 paramConstPool14, int paramInt) {
    int i = paramConstPool14.getTag(paramInt);
    switch (i) {
      case 8:
        return "#" + paramInt + " = \"" + paramConstPool14.getStringInfo(paramInt) + "\"";
      case 3:
        return "#" + paramInt + " = int " + paramConstPool14.getIntegerInfo(paramInt);
      case 4:
        return "#" + paramInt + " = float " + paramConstPool14.getFloatInfo(paramInt);
      case 5:
        return "#" + paramInt + " = long " + paramConstPool14.getLongInfo(paramInt);
      case 6:
        return "#" + paramInt + " = int " + paramConstPool14.getDoubleInfo(paramInt);
      case 7:
        return classInfo(paramConstPool14, paramInt);
    } 
    throw new RuntimeException("bad LDC: " + i);
  }
}
