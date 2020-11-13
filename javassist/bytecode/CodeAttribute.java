package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeAttribute extends AttributeInfo implements Opcode {
  public static final String tag = "Code";
  
  private int maxStack;
  
  private int maxLocals;
  
  private ExceptionTable exceptions;
  
  private ArrayList attributes;
  
  public CodeAttribute(ConstPool14 paramConstPool14, int paramInt1, int paramInt2, byte[] paramArrayOfbyte, ExceptionTable paramExceptionTable) {
    super(paramConstPool14, "Code");
    this.maxStack = paramInt1;
    this.maxLocals = paramInt2;
    this.info = paramArrayOfbyte;
    this.exceptions = paramExceptionTable;
    this.attributes = new ArrayList();
  }
  
  private CodeAttribute(ConstPool14 paramConstPool14, CodeAttribute paramCodeAttribute, Map paramMap) throws BadBytecode {
    super(paramConstPool14, "Code");
    this.maxStack = paramCodeAttribute.getMaxStack();
    this.maxLocals = paramCodeAttribute.getMaxLocals();
    this.exceptions = paramCodeAttribute.getExceptionTable().copy(paramConstPool14, paramMap);
    this.attributes = new ArrayList();
    List<AttributeInfo> list = paramCodeAttribute.getAttributes();
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      AttributeInfo attributeInfo = list.get(b);
      this.attributes.add(attributeInfo.copy(paramConstPool14, paramMap));
    } 
    this.info = paramCodeAttribute.copyCode(paramConstPool14, paramMap, this.exceptions, this);
  }
  
  CodeAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, (byte[])null);
    int i = paramDataInputStream.readInt();
    this.maxStack = paramDataInputStream.readUnsignedShort();
    this.maxLocals = paramDataInputStream.readUnsignedShort();
    int j = paramDataInputStream.readInt();
    this.info = new byte[j];
    paramDataInputStream.readFully(this.info);
    this.exceptions = new ExceptionTable(paramConstPool14, paramDataInputStream);
    this.attributes = new ArrayList();
    int k = paramDataInputStream.readUnsignedShort();
    for (byte b = 0; b < k; b++)
      this.attributes.add(AttributeInfo.read(paramConstPool14, paramDataInputStream)); 
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) throws RuntimeCopyException {
    try {
      return new CodeAttribute(paramConstPool14, this, paramMap);
    } catch (BadBytecode badBytecode) {
      throw new RuntimeCopyException("bad bytecode. fatal?");
    } 
  }
  
  public static class RuntimeCopyException extends RuntimeException {
    public RuntimeCopyException(String param1String) {
      super(param1String);
    }
  }
  
  public int length() {
    return 18 + this.info.length + this.exceptions.size() * 8 + AttributeInfo.getLength(this.attributes);
  }
  
  void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.name);
    paramDataOutputStream.writeInt(length() - 6);
    paramDataOutputStream.writeShort(this.maxStack);
    paramDataOutputStream.writeShort(this.maxLocals);
    paramDataOutputStream.writeInt(this.info.length);
    paramDataOutputStream.write(this.info);
    this.exceptions.write(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.attributes.size());
    AttributeInfo.writeAll(this.attributes, paramDataOutputStream);
  }
  
  public byte[] get() {
    throw new UnsupportedOperationException("CodeAttribute.get()");
  }
  
  public void set(byte[] paramArrayOfbyte) {
    throw new UnsupportedOperationException("CodeAttribute.set()");
  }
  
  void renameClass(String paramString1, String paramString2) {
    AttributeInfo.renameClass(this.attributes, paramString1, paramString2);
  }
  
  void renameClass(Map paramMap) {
    AttributeInfo.renameClass(this.attributes, paramMap);
  }
  
  void getRefClasses(Map paramMap) {
    AttributeInfo.getRefClasses(this.attributes, paramMap);
  }
  
  public String getDeclaringClass() {
    ConstPool14 constPool14 = getConstPool();
    return constPool14.getClassName();
  }
  
  public int getMaxStack() {
    return this.maxStack;
  }
  
  public void setMaxStack(int paramInt) {
    this.maxStack = paramInt;
  }
  
  public int computeMaxStack() throws BadBytecode {
    this.maxStack = (new CodeAnalyzer(this)).computeMaxStack();
    return this.maxStack;
  }
  
  public int getMaxLocals() {
    return this.maxLocals;
  }
  
  public void setMaxLocals(int paramInt) {
    this.maxLocals = paramInt;
  }
  
  public int getCodeLength() {
    return this.info.length;
  }
  
  public byte[] getCode() {
    return this.info;
  }
  
  void setCode(byte[] paramArrayOfbyte) {
    super.set(paramArrayOfbyte);
  }
  
  public CodeIterator iterator() {
    return new CodeIterator(this);
  }
  
  public ExceptionTable getExceptionTable() {
    return this.exceptions;
  }
  
  public List getAttributes() {
    return this.attributes;
  }
  
  public AttributeInfo getAttribute(String paramString) {
    return AttributeInfo.lookup(this.attributes, paramString);
  }
  
  public void setAttribute(StackMapTable paramStackMapTable) {
    AttributeInfo.remove(this.attributes, "StackMapTable");
    if (paramStackMapTable != null)
      this.attributes.add(paramStackMapTable); 
  }
  
  public void setAttribute(StackMap paramStackMap) {
    AttributeInfo.remove(this.attributes, "StackMap");
    if (paramStackMap != null)
      this.attributes.add(paramStackMap); 
  }
  
  private byte[] copyCode(ConstPool14 paramConstPool14, Map paramMap, ExceptionTable paramExceptionTable, CodeAttribute paramCodeAttribute) throws BadBytecode {
    int i = getCodeLength();
    byte[] arrayOfByte = new byte[i];
    paramCodeAttribute.info = arrayOfByte;
    LdcEntry ldcEntry = copyCode(this.info, 0, i, getConstPool(), arrayOfByte, paramConstPool14, paramMap);
    return LdcEntry.doit(arrayOfByte, ldcEntry, paramExceptionTable, paramCodeAttribute);
  }
  
  private static LdcEntry copyCode(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, ConstPool14 paramConstPool141, byte[] paramArrayOfbyte2, ConstPool14 paramConstPool142, Map paramMap) throws BadBytecode {
    LdcEntry ldcEntry = null;
    int i;
    for (i = paramInt1; i < paramInt2; i = j) {
      int k;
      LdcEntry ldcEntry1;
      int j = CodeIterator.nextOpcode(paramArrayOfbyte1, i);
      byte b = paramArrayOfbyte1[i];
      paramArrayOfbyte2[i] = b;
      switch (b & 0xFF) {
        case 19:
        case 20:
        case 178:
        case 179:
        case 180:
        case 181:
        case 182:
        case 183:
        case 184:
        case 187:
        case 189:
        case 192:
        case 193:
          copyConstPoolInfo(i + 1, paramArrayOfbyte1, paramConstPool141, paramArrayOfbyte2, paramConstPool142, paramMap);
          break;
        case 18:
          k = paramArrayOfbyte1[i + 1] & 0xFF;
          k = paramConstPool141.copy(k, paramConstPool142, paramMap);
          if (k < 256) {
            paramArrayOfbyte2[i + 1] = (byte)k;
            break;
          } 
          paramArrayOfbyte2[i] = 0;
          paramArrayOfbyte2[i + 1] = 0;
          ldcEntry1 = new LdcEntry();
          ldcEntry1.where = i;
          ldcEntry1.index = k;
          ldcEntry1.next = ldcEntry;
          ldcEntry = ldcEntry1;
          break;
        case 185:
          copyConstPoolInfo(i + 1, paramArrayOfbyte1, paramConstPool141, paramArrayOfbyte2, paramConstPool142, paramMap);
          paramArrayOfbyte2[i + 3] = paramArrayOfbyte1[i + 3];
          paramArrayOfbyte2[i + 4] = paramArrayOfbyte1[i + 4];
          break;
        case 186:
          copyConstPoolInfo(i + 1, paramArrayOfbyte1, paramConstPool141, paramArrayOfbyte2, paramConstPool142, paramMap);
          paramArrayOfbyte2[i + 3] = 0;
          paramArrayOfbyte2[i + 4] = 0;
          break;
        case 197:
          copyConstPoolInfo(i + 1, paramArrayOfbyte1, paramConstPool141, paramArrayOfbyte2, paramConstPool142, paramMap);
          paramArrayOfbyte2[i + 3] = paramArrayOfbyte1[i + 3];
          break;
        default:
          while (++i < j)
            paramArrayOfbyte2[i] = paramArrayOfbyte1[i]; 
          break;
      } 
    } 
    return ldcEntry;
  }
  
  private static void copyConstPoolInfo(int paramInt, byte[] paramArrayOfbyte1, ConstPool14 paramConstPool141, byte[] paramArrayOfbyte2, ConstPool14 paramConstPool142, Map paramMap) {
    int i = (paramArrayOfbyte1[paramInt] & 0xFF) << 8 | paramArrayOfbyte1[paramInt + 1] & 0xFF;
    i = paramConstPool141.copy(i, paramConstPool142, paramMap);
    paramArrayOfbyte2[paramInt] = (byte)(i >> 8);
    paramArrayOfbyte2[paramInt + 1] = (byte)i;
  }
  
  static class LdcEntry {
    LdcEntry next;
    
    int where;
    
    int index;
    
    static byte[] doit(byte[] param1ArrayOfbyte, LdcEntry param1LdcEntry, ExceptionTable param1ExceptionTable, CodeAttribute param1CodeAttribute) throws BadBytecode {
      if (param1LdcEntry != null)
        param1ArrayOfbyte = CodeIterator.changeLdcToLdcW(param1ArrayOfbyte, param1ExceptionTable, param1CodeAttribute, param1LdcEntry); 
      return param1ArrayOfbyte;
    }
  }
  
  public void insertLocalVar(int paramInt1, int paramInt2) throws BadBytecode {
    CodeIterator codeIterator = iterator();
    while (codeIterator.hasNext())
      shiftIndex(codeIterator, paramInt1, paramInt2); 
    setMaxLocals(getMaxLocals() + paramInt2);
  }
  
  private static void shiftIndex(CodeIterator paramCodeIterator, int paramInt1, int paramInt2) throws BadBytecode {
    int i = paramCodeIterator.next();
    int j = paramCodeIterator.byteAt(i);
    if (j < 21)
      return; 
    if (j < 79) {
      if (j < 26) {
        shiftIndex8(paramCodeIterator, i, j, paramInt1, paramInt2);
      } else if (j < 46) {
        shiftIndex0(paramCodeIterator, i, j, paramInt1, paramInt2, 26, 21);
      } else {
        if (j < 54)
          return; 
        if (j < 59) {
          shiftIndex8(paramCodeIterator, i, j, paramInt1, paramInt2);
        } else {
          shiftIndex0(paramCodeIterator, i, j, paramInt1, paramInt2, 59, 54);
        } 
      } 
    } else if (j == 132) {
      int k = paramCodeIterator.byteAt(i + 1);
      if (k < paramInt1)
        return; 
      k += paramInt2;
      if (k < 256) {
        paramCodeIterator.writeByte(k, i + 1);
      } else {
        byte b = (byte)paramCodeIterator.byteAt(i + 2);
        int m = paramCodeIterator.insertExGap(3);
        paramCodeIterator.writeByte(196, m - 3);
        paramCodeIterator.writeByte(132, m - 2);
        paramCodeIterator.write16bit(k, m - 1);
        paramCodeIterator.write16bit(b, m + 1);
      } 
    } else if (j == 169) {
      shiftIndex8(paramCodeIterator, i, j, paramInt1, paramInt2);
    } else if (j == 196) {
      int k = paramCodeIterator.u16bitAt(i + 2);
      if (k < paramInt1)
        return; 
      k += paramInt2;
      paramCodeIterator.write16bit(k, i + 2);
    } 
  }
  
  private static void shiftIndex8(CodeIterator paramCodeIterator, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws BadBytecode {
    int i = paramCodeIterator.byteAt(paramInt1 + 1);
    if (i < paramInt3)
      return; 
    i += paramInt4;
    if (i < 256) {
      paramCodeIterator.writeByte(i, paramInt1 + 1);
    } else {
      int j = paramCodeIterator.insertExGap(2);
      paramCodeIterator.writeByte(196, j - 2);
      paramCodeIterator.writeByte(paramInt2, j - 1);
      paramCodeIterator.write16bit(i, j);
    } 
  }
  
  private static void shiftIndex0(CodeIterator paramCodeIterator, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) throws BadBytecode {
    int i = (paramInt2 - paramInt5) % 4;
    if (i < paramInt3)
      return; 
    i += paramInt4;
    if (i < 4) {
      paramCodeIterator.writeByte(paramInt2 + paramInt4, paramInt1);
    } else {
      paramInt2 = (paramInt2 - paramInt5) / 4 + paramInt6;
      if (i < 256) {
        int j = paramCodeIterator.insertExGap(1);
        paramCodeIterator.writeByte(paramInt2, j - 1);
        paramCodeIterator.writeByte(i, j);
      } else {
        int j = paramCodeIterator.insertExGap(3);
        paramCodeIterator.writeByte(196, j - 1);
        paramCodeIterator.writeByte(paramInt2, j);
        paramCodeIterator.write16bit(i, j + 1);
      } 
    } 
  }
}
