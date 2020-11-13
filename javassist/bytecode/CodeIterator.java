package javassist.bytecode;

import java.util.ArrayList;

public class CodeIterator implements Opcode {
  protected CodeAttribute codeAttr;
  
  protected byte[] bytecode;
  
  protected int endPos;
  
  protected int currentPos;
  
  protected int mark;
  
  protected CodeIterator(CodeAttribute paramCodeAttribute) {
    this.codeAttr = paramCodeAttribute;
    this.bytecode = paramCodeAttribute.getCode();
    begin();
  }
  
  public void begin() {
    this.currentPos = this.mark = 0;
    this.endPos = getCodeLength();
  }
  
  public void move(int paramInt) {
    this.currentPos = paramInt;
  }
  
  public void setMark(int paramInt) {
    this.mark = paramInt;
  }
  
  public int getMark() {
    return this.mark;
  }
  
  public CodeAttribute get() {
    return this.codeAttr;
  }
  
  public int getCodeLength() {
    return this.bytecode.length;
  }
  
  public int byteAt(int paramInt) {
    return this.bytecode[paramInt] & 0xFF;
  }
  
  public int signedByteAt(int paramInt) {
    return this.bytecode[paramInt];
  }
  
  public void writeByte(int paramInt1, int paramInt2) {
    this.bytecode[paramInt2] = (byte)paramInt1;
  }
  
  public int u16bitAt(int paramInt) {
    return ByteArray.readU16bit(this.bytecode, paramInt);
  }
  
  public int s16bitAt(int paramInt) {
    return ByteArray.readS16bit(this.bytecode, paramInt);
  }
  
  public void write16bit(int paramInt1, int paramInt2) {
    ByteArray.write16bit(paramInt1, this.bytecode, paramInt2);
  }
  
  public int s32bitAt(int paramInt) {
    return ByteArray.read32bit(this.bytecode, paramInt);
  }
  
  public void write32bit(int paramInt1, int paramInt2) {
    ByteArray.write32bit(paramInt1, this.bytecode, paramInt2);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramArrayOfbyte.length;
    for (byte b = 0; b < i; b++)
      this.bytecode[paramInt++] = paramArrayOfbyte[b]; 
  }
  
  public boolean hasNext() {
    return (this.currentPos < this.endPos);
  }
  
  public int next() throws BadBytecode {
    int i = this.currentPos;
    this.currentPos = nextOpcode(this.bytecode, i);
    return i;
  }
  
  public int lookAhead() {
    return this.currentPos;
  }
  
  public int skipConstructor() throws BadBytecode {
    return skipSuperConstructor0(-1);
  }
  
  public int skipSuperConstructor() throws BadBytecode {
    return skipSuperConstructor0(0);
  }
  
  public int skipThisConstructor() throws BadBytecode {
    return skipSuperConstructor0(1);
  }
  
  private int skipSuperConstructor0(int paramInt) throws BadBytecode {
    begin();
    ConstPool14 constPool14 = this.codeAttr.getConstPool();
    String str = this.codeAttr.getDeclaringClass();
    byte b = 0;
    while (hasNext()) {
      int i = next();
      int j = byteAt(i);
      if (j == 187) {
        b++;
        continue;
      } 
      if (j == 183) {
        int k = ByteArray.readU16bit(this.bytecode, i + 1);
        if (constPool14.getMethodrefName(k).equals("<init>") && 
          --b < 0) {
          if (paramInt < 0)
            return i; 
          String str1 = constPool14.getMethodrefClassName(k);
          if (str1.equals(str) == ((paramInt > 0)))
            return i; 
          break;
        } 
      } 
    } 
    begin();
    return -1;
  }
  
  public int insert(byte[] paramArrayOfbyte) throws BadBytecode {
    return insert0(this.currentPos, paramArrayOfbyte, false);
  }
  
  public void insert(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {
    insert0(paramInt, paramArrayOfbyte, false);
  }
  
  public int insertAt(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {
    return insert0(paramInt, paramArrayOfbyte, false);
  }
  
  public int insertEx(byte[] paramArrayOfbyte) throws BadBytecode {
    return insert0(this.currentPos, paramArrayOfbyte, true);
  }
  
  public void insertEx(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {
    insert0(paramInt, paramArrayOfbyte, true);
  }
  
  public int insertExAt(int paramInt, byte[] paramArrayOfbyte) throws BadBytecode {
    return insert0(paramInt, paramArrayOfbyte, true);
  }
  
  private int insert0(int paramInt, byte[] paramArrayOfbyte, boolean paramBoolean) throws BadBytecode {
    int i = paramArrayOfbyte.length;
    if (i <= 0)
      return paramInt; 
    paramInt = (insertGapAt(paramInt, i, paramBoolean)).position;
    int j = paramInt;
    for (byte b = 0; b < i; b++)
      this.bytecode[j++] = paramArrayOfbyte[b]; 
    return paramInt;
  }
  
  public int insertGap(int paramInt) throws BadBytecode {
    return (insertGapAt(this.currentPos, paramInt, false)).position;
  }
  
  public int insertGap(int paramInt1, int paramInt2) throws BadBytecode {
    return (insertGapAt(paramInt1, paramInt2, false)).length;
  }
  
  public int insertExGap(int paramInt) throws BadBytecode {
    return (insertGapAt(this.currentPos, paramInt, true)).position;
  }
  
  public int insertExGap(int paramInt1, int paramInt2) throws BadBytecode {
    return (insertGapAt(paramInt1, paramInt2, true)).length;
  }
  
  public static class Gap {
    public int position;
    
    public int length;
  }
  
  public Gap insertGapAt(int paramInt1, int paramInt2, boolean paramBoolean) throws BadBytecode {
    byte[] arrayOfByte;
    int i;
    Gap gap = new Gap();
    if (paramInt2 <= 0) {
      gap.position = paramInt1;
      gap.length = 0;
      return gap;
    } 
    if (this.bytecode.length + paramInt2 > 32767) {
      arrayOfByte = insertGapCore0w(this.bytecode, paramInt1, paramInt2, paramBoolean, 
          get().getExceptionTable(), this.codeAttr, gap);
      paramInt1 = gap.position;
      i = paramInt2;
    } else {
      int j = this.currentPos;
      arrayOfByte = insertGapCore0(this.bytecode, paramInt1, paramInt2, paramBoolean, 
          get().getExceptionTable(), this.codeAttr);
      i = arrayOfByte.length - this.bytecode.length;
      gap.position = paramInt1;
      gap.length = i;
      if (j >= paramInt1)
        this.currentPos = j + i; 
      if (this.mark > paramInt1 || (this.mark == paramInt1 && paramBoolean))
        this.mark += i; 
    } 
    this.codeAttr.setCode(arrayOfByte);
    this.bytecode = arrayOfByte;
    this.endPos = getCodeLength();
    updateCursors(paramInt1, i);
    return gap;
  }
  
  protected void updateCursors(int paramInt1, int paramInt2) {}
  
  public void insert(ExceptionTable paramExceptionTable, int paramInt) {
    this.codeAttr.getExceptionTable().add(0, paramExceptionTable, paramInt);
  }
  
  public int append(byte[] paramArrayOfbyte) {
    int i = getCodeLength();
    int j = paramArrayOfbyte.length;
    if (j <= 0)
      return i; 
    appendGap(j);
    byte[] arrayOfByte = this.bytecode;
    for (byte b = 0; b < j; b++)
      arrayOfByte[b + i] = paramArrayOfbyte[b]; 
    return i;
  }
  
  public void appendGap(int paramInt) {
    byte[] arrayOfByte1 = this.bytecode;
    int i = arrayOfByte1.length;
    byte[] arrayOfByte2 = new byte[i + paramInt];
    int j;
    for (j = 0; j < i; j++)
      arrayOfByte2[j] = arrayOfByte1[j]; 
    for (j = i; j < i + paramInt; j++)
      arrayOfByte2[j] = 0; 
    this.codeAttr.setCode(arrayOfByte2);
    this.bytecode = arrayOfByte2;
    this.endPos = getCodeLength();
  }
  
  public void append(ExceptionTable paramExceptionTable, int paramInt) {
    ExceptionTable exceptionTable = this.codeAttr.getExceptionTable();
    exceptionTable.add(exceptionTable.size(), paramExceptionTable, paramInt);
  }
  
  private static final int[] opcodeLength = new int[] { 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 
      3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
      1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 
      3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 
      0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 
      3, 3, 3, 3, 3, 5, 5, 3, 2, 3, 
      1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 
      5, 5 };
  
  static int nextOpcode(byte[] paramArrayOfbyte, int paramInt) throws BadBytecode {
    int i;
    try {
      i = paramArrayOfbyte[paramInt] & 0xFF;
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new BadBytecode("invalid opcode address");
    } 
    try {
      int j = opcodeLength[i];
      if (j > 0)
        return paramInt + j; 
      if (i == 196) {
        if (paramArrayOfbyte[paramInt + 1] == -124)
          return paramInt + 6; 
        return paramInt + 4;
      } 
      int k = (paramInt & 0xFFFFFFFC) + 8;
      if (i == 171) {
        int m = ByteArray.read32bit(paramArrayOfbyte, k);
        return k + m * 8 + 4;
      } 
      if (i == 170) {
        int m = ByteArray.read32bit(paramArrayOfbyte, k);
        int n = ByteArray.read32bit(paramArrayOfbyte, k + 4);
        return k + (n - m + 1) * 4 + 8;
      } 
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {}
    throw new BadBytecode(i);
  }
  
  static class AlignmentException extends Exception {}
  
  static byte[] insertGapCore0(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean, ExceptionTable paramExceptionTable, CodeAttribute paramCodeAttribute) throws BadBytecode {
    if (paramInt2 <= 0)
      return paramArrayOfbyte; 
    try {
      return insertGapCore1(paramArrayOfbyte, paramInt1, paramInt2, paramBoolean, paramExceptionTable, paramCodeAttribute);
    } catch (AlignmentException alignmentException) {
      try {
        return insertGapCore1(paramArrayOfbyte, paramInt1, paramInt2 + 3 & 0xFFFFFFFC, paramBoolean, paramExceptionTable, paramCodeAttribute);
      } catch (AlignmentException alignmentException1) {
        throw new RuntimeException("fatal error?");
      } 
    } 
  }
  
  private static byte[] insertGapCore1(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean, ExceptionTable paramExceptionTable, CodeAttribute paramCodeAttribute) throws BadBytecode, AlignmentException {
    int i = paramArrayOfbyte.length;
    byte[] arrayOfByte = new byte[i + paramInt2];
    insertGap2(paramArrayOfbyte, paramInt1, paramInt2, i, arrayOfByte, paramBoolean);
    paramExceptionTable.shiftPc(paramInt1, paramInt2, paramBoolean);
    LineNumberAttribute lineNumberAttribute = (LineNumberAttribute)paramCodeAttribute.getAttribute("LineNumberTable");
    if (lineNumberAttribute != null)
      lineNumberAttribute.shiftPc(paramInt1, paramInt2, paramBoolean); 
    LocalVariableAttribute localVariableAttribute1 = (LocalVariableAttribute)paramCodeAttribute.getAttribute("LocalVariableTable");
    if (localVariableAttribute1 != null)
      localVariableAttribute1.shiftPc(paramInt1, paramInt2, paramBoolean); 
    LocalVariableAttribute localVariableAttribute2 = (LocalVariableAttribute)paramCodeAttribute.getAttribute("LocalVariableTypeTable");
    if (localVariableAttribute2 != null)
      localVariableAttribute2.shiftPc(paramInt1, paramInt2, paramBoolean); 
    StackMapTable stackMapTable = (StackMapTable)paramCodeAttribute.getAttribute("StackMapTable");
    if (stackMapTable != null)
      stackMapTable.shiftPc(paramInt1, paramInt2, paramBoolean); 
    StackMap stackMap = (StackMap)paramCodeAttribute.getAttribute("StackMap");
    if (stackMap != null)
      stackMap.shiftPc(paramInt1, paramInt2, paramBoolean); 
    return arrayOfByte;
  }
  
  private static void insertGap2(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfbyte2, boolean paramBoolean) throws BadBytecode, AlignmentException {
    int i = 0;
    int j = 0;
    for (; i < paramInt3; i = k) {
      if (i == paramInt1) {
        int n = j + paramInt2;
        while (j < n)
          paramArrayOfbyte2[j++] = 0; 
      } 
      int k = nextOpcode(paramArrayOfbyte1, i);
      int m = paramArrayOfbyte1[i] & 0xFF;
      if ((153 <= m && m <= 168) || m == 198 || m == 199) {
        int n = paramArrayOfbyte1[i + 1] << 8 | paramArrayOfbyte1[i + 2] & 0xFF;
        n = newOffset(i, n, paramInt1, paramInt2, paramBoolean);
        paramArrayOfbyte2[j] = paramArrayOfbyte1[i];
        ByteArray.write16bit(n, paramArrayOfbyte2, j + 1);
        j += 3;
      } else if (m == 200 || m == 201) {
        int n = ByteArray.read32bit(paramArrayOfbyte1, i + 1);
        n = newOffset(i, n, paramInt1, paramInt2, paramBoolean);
        paramArrayOfbyte2[j++] = paramArrayOfbyte1[i];
        ByteArray.write32bit(n, paramArrayOfbyte2, j);
        j += 4;
      } else if (m == 170) {
        if (i != j && (paramInt2 & 0x3) != 0)
          throw new AlignmentException(); 
        int n = (i & 0xFFFFFFFC) + 4;
        j = copyGapBytes(paramArrayOfbyte2, j, paramArrayOfbyte1, i, n);
        int i1 = newOffset(i, ByteArray.read32bit(paramArrayOfbyte1, n), paramInt1, paramInt2, paramBoolean);
        ByteArray.write32bit(i1, paramArrayOfbyte2, j);
        int i2 = ByteArray.read32bit(paramArrayOfbyte1, n + 4);
        ByteArray.write32bit(i2, paramArrayOfbyte2, j + 4);
        int i3 = ByteArray.read32bit(paramArrayOfbyte1, n + 8);
        ByteArray.write32bit(i3, paramArrayOfbyte2, j + 8);
        j += 12;
        int i4 = n + 12;
        n = i4 + (i3 - i2 + 1) * 4;
        while (i4 < n) {
          int i5 = newOffset(i, ByteArray.read32bit(paramArrayOfbyte1, i4), paramInt1, paramInt2, paramBoolean);
          ByteArray.write32bit(i5, paramArrayOfbyte2, j);
          j += 4;
          i4 += 4;
        } 
      } else if (m == 171) {
        if (i != j && (paramInt2 & 0x3) != 0)
          throw new AlignmentException(); 
        int n = (i & 0xFFFFFFFC) + 4;
        j = copyGapBytes(paramArrayOfbyte2, j, paramArrayOfbyte1, i, n);
        int i1 = newOffset(i, ByteArray.read32bit(paramArrayOfbyte1, n), paramInt1, paramInt2, paramBoolean);
        ByteArray.write32bit(i1, paramArrayOfbyte2, j);
        int i2 = ByteArray.read32bit(paramArrayOfbyte1, n + 4);
        ByteArray.write32bit(i2, paramArrayOfbyte2, j + 4);
        j += 8;
        int i3 = n + 8;
        n = i3 + i2 * 8;
        while (i3 < n) {
          ByteArray.copy32bit(paramArrayOfbyte1, i3, paramArrayOfbyte2, j);
          int i4 = newOffset(i, 
              ByteArray.read32bit(paramArrayOfbyte1, i3 + 4), paramInt1, paramInt2, paramBoolean);
          ByteArray.write32bit(i4, paramArrayOfbyte2, j + 4);
          j += 8;
          i3 += 8;
        } 
      } else {
        while (i < k)
          paramArrayOfbyte2[j++] = paramArrayOfbyte1[i++]; 
      } 
    } 
  }
  
  private static int copyGapBytes(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, int paramInt3) {
    switch (paramInt3 - paramInt2) {
      case 4:
        paramArrayOfbyte1[paramInt1++] = paramArrayOfbyte2[paramInt2++];
      case 3:
        paramArrayOfbyte1[paramInt1++] = paramArrayOfbyte2[paramInt2++];
      case 2:
        paramArrayOfbyte1[paramInt1++] = paramArrayOfbyte2[paramInt2++];
      case 1:
        paramArrayOfbyte1[paramInt1++] = paramArrayOfbyte2[paramInt2++];
        break;
    } 
    return paramInt1;
  }
  
  private static int newOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    int i = paramInt1 + paramInt2;
    if (paramInt1 < paramInt3) {
      if (paramInt3 < i || (paramBoolean && paramInt3 == i))
        paramInt2 += paramInt4; 
    } else if (paramInt1 == paramInt3) {
      if (i < paramInt3)
        paramInt2 -= paramInt4; 
    } else if (i < paramInt3 || (!paramBoolean && paramInt3 == i)) {
      paramInt2 -= paramInt4;
    } 
    return paramInt2;
  }
  
  static class Pointers {
    int cursor;
    
    int mark0;
    
    int mark;
    
    ExceptionTable etable;
    
    LineNumberAttribute line;
    
    LocalVariableAttribute vars;
    
    LocalVariableAttribute types;
    
    StackMapTable stack;
    
    StackMap stack2;
    
    Pointers(int param1Int1, int param1Int2, int param1Int3, ExceptionTable param1ExceptionTable, CodeAttribute param1CodeAttribute) {
      this.cursor = param1Int1;
      this.mark = param1Int2;
      this.mark0 = param1Int3;
      this.etable = param1ExceptionTable;
      this.line = (LineNumberAttribute)param1CodeAttribute.getAttribute("LineNumberTable");
      this.vars = (LocalVariableAttribute)param1CodeAttribute.getAttribute("LocalVariableTable");
      this.types = (LocalVariableAttribute)param1CodeAttribute.getAttribute("LocalVariableTypeTable");
      this.stack = (StackMapTable)param1CodeAttribute.getAttribute("StackMapTable");
      this.stack2 = (StackMap)param1CodeAttribute.getAttribute("StackMap");
    }
    
    void shiftPc(int param1Int1, int param1Int2, boolean param1Boolean) throws BadBytecode {
      if (param1Int1 < this.cursor || (param1Int1 == this.cursor && param1Boolean))
        this.cursor += param1Int2; 
      if (param1Int1 < this.mark || (param1Int1 == this.mark && param1Boolean))
        this.mark += param1Int2; 
      if (param1Int1 < this.mark0 || (param1Int1 == this.mark0 && param1Boolean))
        this.mark0 += param1Int2; 
      this.etable.shiftPc(param1Int1, param1Int2, param1Boolean);
      if (this.line != null)
        this.line.shiftPc(param1Int1, param1Int2, param1Boolean); 
      if (this.vars != null)
        this.vars.shiftPc(param1Int1, param1Int2, param1Boolean); 
      if (this.types != null)
        this.types.shiftPc(param1Int1, param1Int2, param1Boolean); 
      if (this.stack != null)
        this.stack.shiftPc(param1Int1, param1Int2, param1Boolean); 
      if (this.stack2 != null)
        this.stack2.shiftPc(param1Int1, param1Int2, param1Boolean); 
    }
    
    void shiftForSwitch(int param1Int1, int param1Int2) throws BadBytecode {
      if (this.stack != null)
        this.stack.shiftForSwitch(param1Int1, param1Int2); 
      if (this.stack2 != null)
        this.stack2.shiftForSwitch(param1Int1, param1Int2); 
    }
  }
  
  static byte[] changeLdcToLdcW(byte[] paramArrayOfbyte, ExceptionTable paramExceptionTable, CodeAttribute paramCodeAttribute, CodeAttribute.LdcEntry paramLdcEntry) throws BadBytecode {
    Pointers pointers = new Pointers(0, 0, 0, paramExceptionTable, paramCodeAttribute);
    ArrayList arrayList = makeJumpList(paramArrayOfbyte, paramArrayOfbyte.length, pointers);
    while (paramLdcEntry != null) {
      addLdcW(paramLdcEntry, arrayList);
      paramLdcEntry = paramLdcEntry.next;
    } 
    return insertGap2w(paramArrayOfbyte, 0, 0, false, arrayList, pointers);
  }
  
  private static void addLdcW(CodeAttribute.LdcEntry paramLdcEntry, ArrayList<LdcW> paramArrayList) {
    int i = paramLdcEntry.where;
    LdcW ldcW = new LdcW(i, paramLdcEntry.index);
    int j = paramArrayList.size();
    for (byte b = 0; b < j; b++) {
      if (i < ((Branch)paramArrayList.get(b)).orgPos) {
        paramArrayList.add(b, ldcW);
        return;
      } 
    } 
    paramArrayList.add(ldcW);
  }
  
  private byte[] insertGapCore0w(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean, ExceptionTable paramExceptionTable, CodeAttribute paramCodeAttribute, Gap paramGap) throws BadBytecode {
    if (paramInt2 <= 0)
      return paramArrayOfbyte; 
    Pointers pointers = new Pointers(this.currentPos, this.mark, paramInt1, paramExceptionTable, paramCodeAttribute);
    ArrayList arrayList = makeJumpList(paramArrayOfbyte, paramArrayOfbyte.length, pointers);
    byte[] arrayOfByte = insertGap2w(paramArrayOfbyte, paramInt1, paramInt2, paramBoolean, arrayList, pointers);
    this.currentPos = pointers.cursor;
    this.mark = pointers.mark;
    int i = pointers.mark0;
    if (i == this.currentPos && !paramBoolean)
      this.currentPos += paramInt2; 
    if (paramBoolean)
      i -= paramInt2; 
    paramGap.position = i;
    paramGap.length = paramInt2;
    return arrayOfByte;
  }
  
  private static byte[] insertGap2w(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean, ArrayList<Branch> paramArrayList, Pointers paramPointers) throws BadBytecode {
    int i = paramArrayList.size();
    if (paramInt2 > 0) {
      paramPointers.shiftPc(paramInt1, paramInt2, paramBoolean);
      for (byte b = 0; b < i; b++)
        ((Branch)paramArrayList.get(b)).shift(paramInt1, paramInt2, paramBoolean); 
    } 
    boolean bool = true;
    do {
      while (bool) {
        bool = false;
        for (byte b1 = 0; b1 < i; b1++) {
          Branch branch = paramArrayList.get(b1);
          if (branch.expanded()) {
            bool = true;
            int j = branch.pos;
            int k = branch.deltaSize();
            paramPointers.shiftPc(j, k, false);
            for (byte b2 = 0; b2 < i; b2++)
              ((Branch)paramArrayList.get(b2)).shift(j, k, false); 
          } 
        } 
      } 
      for (byte b = 0; b < i; b++) {
        Branch branch = paramArrayList.get(b);
        int j = branch.gapChanged();
        if (j > 0) {
          bool = true;
          int k = branch.pos;
          paramPointers.shiftPc(k, j, false);
          for (byte b1 = 0; b1 < i; b1++)
            ((Branch)paramArrayList.get(b1)).shift(k, j, false); 
        } 
      } 
    } while (bool);
    return makeExapndedCode(paramArrayOfbyte, paramArrayList, paramInt1, paramInt2);
  }
  
  private static ArrayList makeJumpList(byte[] paramArrayOfbyte, int paramInt, Pointers paramPointers) throws BadBytecode {
    ArrayList<If16> arrayList = new ArrayList();
    int i;
    for (i = 0; i < paramInt; i = j) {
      int j = nextOpcode(paramArrayOfbyte, i);
      int k = paramArrayOfbyte[i] & 0xFF;
      if ((153 <= k && k <= 168) || k == 198 || k == 199) {
        If16 if16;
        int m = paramArrayOfbyte[i + 1] << 8 | paramArrayOfbyte[i + 2] & 0xFF;
        if (k == 167 || k == 168) {
          Jump16 jump16 = new Jump16(i, m);
        } else {
          if16 = new If16(i, m);
        } 
        arrayList.add(if16);
      } else if (k == 200 || k == 201) {
        int m = ByteArray.read32bit(paramArrayOfbyte, i + 1);
        arrayList.add(new Jump32(i, m));
      } else if (k == 170) {
        int m = (i & 0xFFFFFFFC) + 4;
        int n = ByteArray.read32bit(paramArrayOfbyte, m);
        int i1 = ByteArray.read32bit(paramArrayOfbyte, m + 4);
        int i2 = ByteArray.read32bit(paramArrayOfbyte, m + 8);
        int i3 = m + 12;
        int i4 = i2 - i1 + 1;
        int[] arrayOfInt = new int[i4];
        for (byte b = 0; b < i4; b++) {
          arrayOfInt[b] = ByteArray.read32bit(paramArrayOfbyte, i3);
          i3 += 4;
        } 
        arrayList.add(new Table(i, n, i1, i2, arrayOfInt, paramPointers));
      } else if (k == 171) {
        int m = (i & 0xFFFFFFFC) + 4;
        int n = ByteArray.read32bit(paramArrayOfbyte, m);
        int i1 = ByteArray.read32bit(paramArrayOfbyte, m + 4);
        int i2 = m + 8;
        int[] arrayOfInt1 = new int[i1];
        int[] arrayOfInt2 = new int[i1];
        for (byte b = 0; b < i1; b++) {
          arrayOfInt1[b] = ByteArray.read32bit(paramArrayOfbyte, i2);
          arrayOfInt2[b] = ByteArray.read32bit(paramArrayOfbyte, i2 + 4);
          i2 += 8;
        } 
        arrayList.add(new Lookup(i, n, arrayOfInt1, arrayOfInt2, paramPointers));
      } 
    } 
    return arrayList;
  }
  
  private static byte[] makeExapndedCode(byte[] paramArrayOfbyte, ArrayList<Branch> paramArrayList, int paramInt1, int paramInt2) throws BadBytecode {
    Branch branch;
    int i1, i = paramArrayList.size();
    int j = paramArrayOfbyte.length + paramInt2;
    for (byte b1 = 0; b1 < i; b1++) {
      Branch branch1 = paramArrayList.get(b1);
      j += branch1.deltaSize();
    } 
    byte[] arrayOfByte = new byte[j];
    int k = 0, m = 0;
    byte b2 = 0;
    int n = paramArrayOfbyte.length;
    if (0 < i) {
      branch = paramArrayList.get(0);
      i1 = branch.orgPos;
    } else {
      branch = null;
      i1 = n;
    } 
    while (k < n) {
      if (k == paramInt1) {
        int i3 = m + paramInt2;
        while (m < i3)
          arrayOfByte[m++] = 0; 
      } 
      if (k != i1) {
        arrayOfByte[m++] = paramArrayOfbyte[k++];
        continue;
      } 
      int i2 = branch.write(k, paramArrayOfbyte, m, arrayOfByte);
      k += i2;
      m += i2 + branch.deltaSize();
      if (++b2 < i) {
        branch = paramArrayList.get(b2);
        i1 = branch.orgPos;
        continue;
      } 
      branch = null;
      i1 = n;
    } 
    return arrayOfByte;
  }
  
  static abstract class Branch {
    int pos;
    
    int orgPos;
    
    Branch(int param1Int) {
      this.pos = this.orgPos = param1Int;
    }
    
    void shift(int param1Int1, int param1Int2, boolean param1Boolean) {
      if (param1Int1 < this.pos || (param1Int1 == this.pos && param1Boolean))
        this.pos += param1Int2; 
    }
    
    static int shiftOffset(int param1Int1, int param1Int2, int param1Int3, int param1Int4, boolean param1Boolean) {
      int i = param1Int1 + param1Int2;
      if (param1Int1 < param1Int3) {
        if (param1Int3 < i || (param1Boolean && param1Int3 == i))
          param1Int2 += param1Int4; 
      } else if (param1Int1 == param1Int3) {
        if (i < param1Int3 && param1Boolean) {
          param1Int2 -= param1Int4;
        } else if (param1Int3 < i && !param1Boolean) {
          param1Int2 += param1Int4;
        } 
      } else if (i < param1Int3 || (!param1Boolean && param1Int3 == i)) {
        param1Int2 -= param1Int4;
      } 
      return param1Int2;
    }
    
    boolean expanded() {
      return false;
    }
    
    int gapChanged() {
      return 0;
    }
    
    int deltaSize() {
      return 0;
    }
    
    abstract int write(int param1Int1, byte[] param1ArrayOfbyte1, int param1Int2, byte[] param1ArrayOfbyte2) throws BadBytecode;
  }
  
  static class LdcW extends Branch {
    int index;
    
    boolean state;
    
    LdcW(int param1Int1, int param1Int2) {
      super(param1Int1);
      this.index = param1Int2;
      this.state = true;
    }
    
    boolean expanded() {
      if (this.state) {
        this.state = false;
        return true;
      } 
      return false;
    }
    
    int deltaSize() {
      return 1;
    }
    
    int write(int param1Int1, byte[] param1ArrayOfbyte1, int param1Int2, byte[] param1ArrayOfbyte2) {
      param1ArrayOfbyte2[param1Int2] = 19;
      ByteArray.write16bit(this.index, param1ArrayOfbyte2, param1Int2 + 1);
      return 2;
    }
  }
  
  static abstract class Branch16 extends Branch {
    int offset;
    
    int state;
    
    static final int BIT16 = 0;
    
    static final int EXPAND = 1;
    
    static final int BIT32 = 2;
    
    Branch16(int param1Int1, int param1Int2) {
      super(param1Int1);
      this.offset = param1Int2;
      this.state = 0;
    }
    
    void shift(int param1Int1, int param1Int2, boolean param1Boolean) {
      this.offset = shiftOffset(this.pos, this.offset, param1Int1, param1Int2, param1Boolean);
      super.shift(param1Int1, param1Int2, param1Boolean);
      if (this.state == 0 && (
        this.offset < -32768 || 32767 < this.offset))
        this.state = 1; 
    }
    
    boolean expanded() {
      if (this.state == 1) {
        this.state = 2;
        return true;
      } 
      return false;
    }
    
    abstract int deltaSize();
    
    abstract void write32(int param1Int1, byte[] param1ArrayOfbyte1, int param1Int2, byte[] param1ArrayOfbyte2);
    
    int write(int param1Int1, byte[] param1ArrayOfbyte1, int param1Int2, byte[] param1ArrayOfbyte2) {
      if (this.state == 2) {
        write32(param1Int1, param1ArrayOfbyte1, param1Int2, param1ArrayOfbyte2);
      } else {
        param1ArrayOfbyte2[param1Int2] = param1ArrayOfbyte1[param1Int1];
        ByteArray.write16bit(this.offset, param1ArrayOfbyte2, param1Int2 + 1);
      } 
      return 3;
    }
  }
  
  static class Jump16 extends Branch16 {
    Jump16(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
    }
    
    int deltaSize() {
      return (this.state == 2) ? 2 : 0;
    }
    
    void write32(int param1Int1, byte[] param1ArrayOfbyte1, int param1Int2, byte[] param1ArrayOfbyte2) {
      param1ArrayOfbyte2[param1Int2] = (byte)(((param1ArrayOfbyte1[param1Int1] & 0xFF) == 167) ? 200 : 201);
      ByteArray.write32bit(this.offset, param1ArrayOfbyte2, param1Int2 + 1);
    }
  }
  
  static class If16 extends Branch16 {
    If16(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
    }
    
    int deltaSize() {
      return (this.state == 2) ? 5 : 0;
    }
    
    void write32(int param1Int1, byte[] param1ArrayOfbyte1, int param1Int2, byte[] param1ArrayOfbyte2) {
      param1ArrayOfbyte2[param1Int2] = (byte)opcode(param1ArrayOfbyte1[param1Int1] & 0xFF);
      param1ArrayOfbyte2[param1Int2 + 1] = 0;
      param1ArrayOfbyte2[param1Int2 + 2] = 8;
      param1ArrayOfbyte2[param1Int2 + 3] = -56;
      ByteArray.write32bit(this.offset - 3, param1ArrayOfbyte2, param1Int2 + 4);
    }
    
    int opcode(int param1Int) {
      if (param1Int == 198)
        return 199; 
      if (param1Int == 199)
        return 198; 
      if ((param1Int - 153 & 0x1) == 0)
        return param1Int + 1; 
      return param1Int - 1;
    }
  }
  
  static class Jump32 extends Branch {
    int offset;
    
    Jump32(int param1Int1, int param1Int2) {
      super(param1Int1);
      this.offset = param1Int2;
    }
    
    void shift(int param1Int1, int param1Int2, boolean param1Boolean) {
      this.offset = shiftOffset(this.pos, this.offset, param1Int1, param1Int2, param1Boolean);
      super.shift(param1Int1, param1Int2, param1Boolean);
    }
    
    int write(int param1Int1, byte[] param1ArrayOfbyte1, int param1Int2, byte[] param1ArrayOfbyte2) {
      param1ArrayOfbyte2[param1Int2] = param1ArrayOfbyte1[param1Int1];
      ByteArray.write32bit(this.offset, param1ArrayOfbyte2, param1Int2 + 1);
      return 5;
    }
  }
  
  static abstract class Switcher extends Branch {
    int gap;
    
    int defaultByte;
    
    int[] offsets;
    
    CodeIterator.Pointers pointers;
    
    Switcher(int param1Int1, int param1Int2, int[] param1ArrayOfint, CodeIterator.Pointers param1Pointers) {
      super(param1Int1);
      this.gap = 3 - (param1Int1 & 0x3);
      this.defaultByte = param1Int2;
      this.offsets = param1ArrayOfint;
      this.pointers = param1Pointers;
    }
    
    void shift(int param1Int1, int param1Int2, boolean param1Boolean) {
      int i = this.pos;
      this.defaultByte = shiftOffset(i, this.defaultByte, param1Int1, param1Int2, param1Boolean);
      int j = this.offsets.length;
      for (byte b = 0; b < j; b++)
        this.offsets[b] = shiftOffset(i, this.offsets[b], param1Int1, param1Int2, param1Boolean); 
      super.shift(param1Int1, param1Int2, param1Boolean);
    }
    
    int gapChanged() {
      int i = 3 - (this.pos & 0x3);
      if (i > this.gap) {
        int j = i - this.gap;
        this.gap = i;
        return j;
      } 
      return 0;
    }
    
    int deltaSize() {
      return this.gap - 3 - (this.orgPos & 0x3);
    }
    
    int write(int param1Int1, byte[] param1ArrayOfbyte1, int param1Int2, byte[] param1ArrayOfbyte2) throws BadBytecode {
      int i = 3 - (this.pos & 0x3);
      int j = this.gap - i;
      int k = 5 + 3 - (this.orgPos & 0x3) + tableSize();
      if (j > 0)
        adjustOffsets(k, j); 
      param1ArrayOfbyte2[param1Int2++] = param1ArrayOfbyte1[param1Int1];
      while (i-- > 0)
        param1ArrayOfbyte2[param1Int2++] = 0; 
      ByteArray.write32bit(this.defaultByte, param1ArrayOfbyte2, param1Int2);
      int m = write2(param1Int2 + 4, param1ArrayOfbyte2);
      param1Int2 += m + 4;
      while (j-- > 0)
        param1ArrayOfbyte2[param1Int2++] = 0; 
      return 5 + 3 - (this.orgPos & 0x3) + m;
    }
    
    abstract int write2(int param1Int, byte[] param1ArrayOfbyte);
    
    abstract int tableSize();
    
    void adjustOffsets(int param1Int1, int param1Int2) throws BadBytecode {
      this.pointers.shiftForSwitch(this.pos + param1Int1, param1Int2);
      if (this.defaultByte == param1Int1)
        this.defaultByte -= param1Int2; 
      for (byte b = 0; b < this.offsets.length; b++) {
        if (this.offsets[b] == param1Int1)
          this.offsets[b] = this.offsets[b] - param1Int2; 
      } 
    }
  }
  
  static class Table extends Switcher {
    int low;
    
    int high;
    
    Table(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int[] param1ArrayOfint, CodeIterator.Pointers param1Pointers) {
      super(param1Int1, param1Int2, param1ArrayOfint, param1Pointers);
      this.low = param1Int3;
      this.high = param1Int4;
    }
    
    int write2(int param1Int, byte[] param1ArrayOfbyte) {
      ByteArray.write32bit(this.low, param1ArrayOfbyte, param1Int);
      ByteArray.write32bit(this.high, param1ArrayOfbyte, param1Int + 4);
      int i = this.offsets.length;
      param1Int += 8;
      for (byte b = 0; b < i; b++) {
        ByteArray.write32bit(this.offsets[b], param1ArrayOfbyte, param1Int);
        param1Int += 4;
      } 
      return 8 + 4 * i;
    }
    
    int tableSize() {
      return 8 + 4 * this.offsets.length;
    }
  }
  
  static class Lookup extends Switcher {
    int[] matches;
    
    Lookup(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2, CodeIterator.Pointers param1Pointers) {
      super(param1Int1, param1Int2, param1ArrayOfint2, param1Pointers);
      this.matches = param1ArrayOfint1;
    }
    
    int write2(int param1Int, byte[] param1ArrayOfbyte) {
      int i = this.matches.length;
      ByteArray.write32bit(i, param1ArrayOfbyte, param1Int);
      param1Int += 4;
      for (byte b = 0; b < i; b++) {
        ByteArray.write32bit(this.matches[b], param1ArrayOfbyte, param1Int);
        ByteArray.write32bit(this.offsets[b], param1ArrayOfbyte, param1Int + 4);
        param1Int += 8;
      } 
      return 4 + 8 * i;
    }
    
    int tableSize() {
      return 4 + 8 * this.matches.length;
    }
  }
}
