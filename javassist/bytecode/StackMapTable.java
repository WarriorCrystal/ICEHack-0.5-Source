package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import javassist.CannotCompileException;

public class StackMapTable extends AttributeInfo {
  public static final String tag = "StackMapTable";
  
  public static final int TOP = 0;
  
  public static final int INTEGER = 1;
  
  public static final int FLOAT = 2;
  
  public static final int DOUBLE = 3;
  
  public static final int LONG = 4;
  
  public static final int NULL = 5;
  
  public static final int THIS = 6;
  
  public static final int OBJECT = 7;
  
  public static final int UNINIT = 8;
  
  StackMapTable(ConstPool14 paramConstPool14, byte[] paramArrayOfbyte) {
    super(paramConstPool14, "StackMapTable", paramArrayOfbyte);
  }
  
  StackMapTable(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) throws RuntimeCopyException {
    try {
      return new StackMapTable(paramConstPool14, (new Copier(this.constPool, this.info, paramConstPool14, paramMap))
          .doit());
    } catch (BadBytecode badBytecode) {
      throw new RuntimeCopyException("bad bytecode. fatal?");
    } 
  }
  
  public static class RuntimeCopyException extends RuntimeException {
    public RuntimeCopyException(String param1String) {
      super(param1String);
    }
  }
  
  void write(DataOutputStream paramDataOutputStream) throws IOException {
    super.write(paramDataOutputStream);
  }
  
  public static class Walker {
    byte[] info;
    
    int numOfEntries;
    
    public Walker(StackMapTable param1StackMapTable) {
      this(param1StackMapTable.get());
    }
    
    public Walker(byte[] param1ArrayOfbyte) {
      this.info = param1ArrayOfbyte;
      this.numOfEntries = ByteArray.readU16bit(param1ArrayOfbyte, 0);
    }
    
    public final int size() {
      return this.numOfEntries;
    }
    
    public void parse() throws BadBytecode {
      int i = this.numOfEntries;
      int j = 2;
      for (byte b = 0; b < i; b++)
        j = stackMapFrames(j, b); 
    }
    
    int stackMapFrames(int param1Int1, int param1Int2) throws BadBytecode {
      int i = this.info[param1Int1] & 0xFF;
      if (i < 64) {
        sameFrame(param1Int1, i);
        param1Int1++;
      } else if (i < 128) {
        param1Int1 = sameLocals(param1Int1, i);
      } else {
        if (i < 247)
          throw new BadBytecode("bad frame_type in StackMapTable"); 
        if (i == 247) {
          param1Int1 = sameLocals(param1Int1, i);
        } else if (i < 251) {
          int j = ByteArray.readU16bit(this.info, param1Int1 + 1);
          chopFrame(param1Int1, j, 251 - i);
          param1Int1 += 3;
        } else if (i == 251) {
          int j = ByteArray.readU16bit(this.info, param1Int1 + 1);
          sameFrame(param1Int1, j);
          param1Int1 += 3;
        } else if (i < 255) {
          param1Int1 = appendFrame(param1Int1, i);
        } else {
          param1Int1 = fullFrame(param1Int1);
        } 
      } 
      return param1Int1;
    }
    
    public void sameFrame(int param1Int1, int param1Int2) throws BadBytecode {}
    
    private int sameLocals(int param1Int1, int param1Int2) throws BadBytecode {
      int j, i = param1Int1;
      if (param1Int2 < 128) {
        j = param1Int2 - 64;
      } else {
        j = ByteArray.readU16bit(this.info, param1Int1 + 1);
        param1Int1 += 2;
      } 
      int k = this.info[param1Int1 + 1] & 0xFF;
      int m = 0;
      if (k == 7 || k == 8) {
        m = ByteArray.readU16bit(this.info, param1Int1 + 2);
        objectOrUninitialized(k, m, param1Int1 + 2);
        param1Int1 += 2;
      } 
      sameLocals(i, j, k, m);
      return param1Int1 + 2;
    }
    
    public void sameLocals(int param1Int1, int param1Int2, int param1Int3, int param1Int4) throws BadBytecode {}
    
    public void chopFrame(int param1Int1, int param1Int2, int param1Int3) throws BadBytecode {}
    
    private int appendFrame(int param1Int1, int param1Int2) throws BadBytecode {
      int i = param1Int2 - 251;
      int j = ByteArray.readU16bit(this.info, param1Int1 + 1);
      int[] arrayOfInt1 = new int[i];
      int[] arrayOfInt2 = new int[i];
      int k = param1Int1 + 3;
      for (byte b = 0; b < i; b++) {
        int m = this.info[k] & 0xFF;
        arrayOfInt1[b] = m;
        if (m == 7 || m == 8) {
          arrayOfInt2[b] = ByteArray.readU16bit(this.info, k + 1);
          objectOrUninitialized(m, arrayOfInt2[b], k + 1);
          k += 3;
        } else {
          arrayOfInt2[b] = 0;
          k++;
        } 
      } 
      appendFrame(param1Int1, j, arrayOfInt1, arrayOfInt2);
      return k;
    }
    
    public void appendFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2) throws BadBytecode {}
    
    private int fullFrame(int param1Int) throws BadBytecode {
      int i = ByteArray.readU16bit(this.info, param1Int + 1);
      int j = ByteArray.readU16bit(this.info, param1Int + 3);
      int[] arrayOfInt1 = new int[j];
      int[] arrayOfInt2 = new int[j];
      int k = verifyTypeInfo(param1Int + 5, j, arrayOfInt1, arrayOfInt2);
      int m = ByteArray.readU16bit(this.info, k);
      int[] arrayOfInt3 = new int[m];
      int[] arrayOfInt4 = new int[m];
      k = verifyTypeInfo(k + 2, m, arrayOfInt3, arrayOfInt4);
      fullFrame(param1Int, i, arrayOfInt1, arrayOfInt2, arrayOfInt3, arrayOfInt4);
      return k;
    }
    
    public void fullFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2, int[] param1ArrayOfint3, int[] param1ArrayOfint4) throws BadBytecode {}
    
    private int verifyTypeInfo(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2) {
      for (byte b = 0; b < param1Int2; b++) {
        int i = this.info[param1Int1++] & 0xFF;
        param1ArrayOfint1[b] = i;
        if (i == 7 || i == 8) {
          param1ArrayOfint2[b] = ByteArray.readU16bit(this.info, param1Int1);
          objectOrUninitialized(i, param1ArrayOfint2[b], param1Int1);
          param1Int1 += 2;
        } 
      } 
      return param1Int1;
    }
    
    public void objectOrUninitialized(int param1Int1, int param1Int2, int param1Int3) {}
  }
  
  static class SimpleCopy extends Walker {
    private StackMapTable.Writer writer;
    
    public SimpleCopy(byte[] param1ArrayOfbyte) {
      super(param1ArrayOfbyte);
      this.writer = new StackMapTable.Writer(param1ArrayOfbyte.length);
    }
    
    public byte[] doit() throws BadBytecode {
      parse();
      return this.writer.toByteArray();
    }
    
    public void sameFrame(int param1Int1, int param1Int2) {
      this.writer.sameFrame(param1Int2);
    }
    
    public void sameLocals(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.writer.sameLocals(param1Int2, param1Int3, copyData(param1Int3, param1Int4));
    }
    
    public void chopFrame(int param1Int1, int param1Int2, int param1Int3) {
      this.writer.chopFrame(param1Int2, param1Int3);
    }
    
    public void appendFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2) {
      this.writer.appendFrame(param1Int2, param1ArrayOfint1, copyData(param1ArrayOfint1, param1ArrayOfint2));
    }
    
    public void fullFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2, int[] param1ArrayOfint3, int[] param1ArrayOfint4) {
      this.writer.fullFrame(param1Int2, param1ArrayOfint1, copyData(param1ArrayOfint1, param1ArrayOfint2), param1ArrayOfint3, 
          copyData(param1ArrayOfint3, param1ArrayOfint4));
    }
    
    protected int copyData(int param1Int1, int param1Int2) {
      return param1Int2;
    }
    
    protected int[] copyData(int[] param1ArrayOfint1, int[] param1ArrayOfint2) {
      return param1ArrayOfint2;
    }
  }
  
  static class Copier extends SimpleCopy {
    private ConstPool14 srcPool;
    
    private ConstPool14 destPool;
    
    private Map classnames;
    
    public Copier(ConstPool14 param1ConstPool141, byte[] param1ArrayOfbyte, ConstPool14 param1ConstPool142, Map param1Map) {
      super(param1ArrayOfbyte);
      this.srcPool = param1ConstPool141;
      this.destPool = param1ConstPool142;
      this.classnames = param1Map;
    }
    
    protected int copyData(int param1Int1, int param1Int2) {
      if (param1Int1 == 7)
        return this.srcPool.copy(param1Int2, this.destPool, this.classnames); 
      return param1Int2;
    }
    
    protected int[] copyData(int[] param1ArrayOfint1, int[] param1ArrayOfint2) {
      int[] arrayOfInt = new int[param1ArrayOfint2.length];
      for (byte b = 0; b < param1ArrayOfint2.length; b++) {
        if (param1ArrayOfint1[b] == 7) {
          arrayOfInt[b] = this.srcPool.copy(param1ArrayOfint2[b], this.destPool, this.classnames);
        } else {
          arrayOfInt[b] = param1ArrayOfint2[b];
        } 
      } 
      return arrayOfInt;
    }
  }
  
  public void insertLocal(int paramInt1, int paramInt2, int paramInt3) throws BadBytecode {
    byte[] arrayOfByte = (new InsertLocal(get(), paramInt1, paramInt2, paramInt3)).doit();
    set(arrayOfByte);
  }
  
  public static int typeTagOf(char paramChar) {
    switch (paramChar) {
      case 'D':
        return 3;
      case 'F':
        return 2;
      case 'J':
        return 4;
      case 'L':
      case '[':
        return 7;
    } 
    return 1;
  }
  
  static class InsertLocal extends SimpleCopy {
    private int varIndex;
    
    private int varTag;
    
    private int varData;
    
    public InsertLocal(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2, int param1Int3) {
      super(param1ArrayOfbyte);
      this.varIndex = param1Int1;
      this.varTag = param1Int2;
      this.varData = param1Int3;
    }
    
    public void fullFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2, int[] param1ArrayOfint3, int[] param1ArrayOfint4) {
      int i = param1ArrayOfint1.length;
      if (i < this.varIndex) {
        super.fullFrame(param1Int1, param1Int2, param1ArrayOfint1, param1ArrayOfint2, param1ArrayOfint3, param1ArrayOfint4);
        return;
      } 
      byte b1 = (this.varTag == 4 || this.varTag == 3) ? 2 : 1;
      int[] arrayOfInt1 = new int[i + b1];
      int[] arrayOfInt2 = new int[i + b1];
      int j = this.varIndex;
      int k = 0;
      for (byte b2 = 0; b2 < i; b2++) {
        if (k == j)
          k += b1; 
        arrayOfInt1[k] = param1ArrayOfint1[b2];
        arrayOfInt2[k++] = param1ArrayOfint2[b2];
      } 
      arrayOfInt1[j] = this.varTag;
      arrayOfInt2[j] = this.varData;
      if (b1 > 1) {
        arrayOfInt1[j + 1] = 0;
        arrayOfInt2[j + 1] = 0;
      } 
      super.fullFrame(param1Int1, param1Int2, arrayOfInt1, arrayOfInt2, param1ArrayOfint3, param1ArrayOfint4);
    }
  }
  
  public static class Writer {
    ByteArrayOutputStream output;
    
    int numOfEntries;
    
    public Writer(int param1Int) {
      this.output = new ByteArrayOutputStream(param1Int);
      this.numOfEntries = 0;
      this.output.write(0);
      this.output.write(0);
    }
    
    public byte[] toByteArray() {
      byte[] arrayOfByte = this.output.toByteArray();
      ByteArray.write16bit(this.numOfEntries, arrayOfByte, 0);
      return arrayOfByte;
    }
    
    public StackMapTable toStackMapTable(ConstPool14 param1ConstPool14) {
      return new StackMapTable(param1ConstPool14, toByteArray());
    }
    
    public void sameFrame(int param1Int) {
      this.numOfEntries++;
      if (param1Int < 64) {
        this.output.write(param1Int);
      } else {
        this.output.write(251);
        write16(param1Int);
      } 
    }
    
    public void sameLocals(int param1Int1, int param1Int2, int param1Int3) {
      this.numOfEntries++;
      if (param1Int1 < 64) {
        this.output.write(param1Int1 + 64);
      } else {
        this.output.write(247);
        write16(param1Int1);
      } 
      writeTypeInfo(param1Int2, param1Int3);
    }
    
    public void chopFrame(int param1Int1, int param1Int2) {
      this.numOfEntries++;
      this.output.write(251 - param1Int2);
      write16(param1Int1);
    }
    
    public void appendFrame(int param1Int, int[] param1ArrayOfint1, int[] param1ArrayOfint2) {
      this.numOfEntries++;
      int i = param1ArrayOfint1.length;
      this.output.write(i + 251);
      write16(param1Int);
      for (byte b = 0; b < i; b++)
        writeTypeInfo(param1ArrayOfint1[b], param1ArrayOfint2[b]); 
    }
    
    public void fullFrame(int param1Int, int[] param1ArrayOfint1, int[] param1ArrayOfint2, int[] param1ArrayOfint3, int[] param1ArrayOfint4) {
      this.numOfEntries++;
      this.output.write(255);
      write16(param1Int);
      int i = param1ArrayOfint1.length;
      write16(i);
      byte b;
      for (b = 0; b < i; b++)
        writeTypeInfo(param1ArrayOfint1[b], param1ArrayOfint2[b]); 
      i = param1ArrayOfint3.length;
      write16(i);
      for (b = 0; b < i; b++)
        writeTypeInfo(param1ArrayOfint3[b], param1ArrayOfint4[b]); 
    }
    
    private void writeTypeInfo(int param1Int1, int param1Int2) {
      this.output.write(param1Int1);
      if (param1Int1 == 7 || param1Int1 == 8)
        write16(param1Int2); 
    }
    
    private void write16(int param1Int) {
      this.output.write(param1Int >>> 8 & 0xFF);
      this.output.write(param1Int & 0xFF);
    }
  }
  
  public void println(PrintWriter paramPrintWriter) {
    Printer.print(this, paramPrintWriter);
  }
  
  public void println(PrintStream paramPrintStream) {
    Printer.print(this, new PrintWriter(paramPrintStream, true));
  }
  
  static class Printer extends Walker {
    private PrintWriter writer;
    
    private int offset;
    
    public static void print(StackMapTable param1StackMapTable, PrintWriter param1PrintWriter) {
      try {
        (new Printer(param1StackMapTable.get(), param1PrintWriter)).parse();
      } catch (BadBytecode badBytecode) {
        param1PrintWriter.println(badBytecode.getMessage());
      } 
    }
    
    Printer(byte[] param1ArrayOfbyte, PrintWriter param1PrintWriter) {
      super(param1ArrayOfbyte);
      this.writer = param1PrintWriter;
      this.offset = -1;
    }
    
    public void sameFrame(int param1Int1, int param1Int2) {
      this.offset += param1Int2 + 1;
      this.writer.println(this.offset + " same frame: " + param1Int2);
    }
    
    public void sameLocals(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.offset += param1Int2 + 1;
      this.writer.println(this.offset + " same locals: " + param1Int2);
      printTypeInfo(param1Int3, param1Int4);
    }
    
    public void chopFrame(int param1Int1, int param1Int2, int param1Int3) {
      this.offset += param1Int2 + 1;
      this.writer.println(this.offset + " chop frame: " + param1Int2 + ",    " + param1Int3 + " last locals");
    }
    
    public void appendFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2) {
      this.offset += param1Int2 + 1;
      this.writer.println(this.offset + " append frame: " + param1Int2);
      for (byte b = 0; b < param1ArrayOfint1.length; b++)
        printTypeInfo(param1ArrayOfint1[b], param1ArrayOfint2[b]); 
    }
    
    public void fullFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2, int[] param1ArrayOfint3, int[] param1ArrayOfint4) {
      this.offset += param1Int2 + 1;
      this.writer.println(this.offset + " full frame: " + param1Int2);
      this.writer.println("[locals]");
      byte b;
      for (b = 0; b < param1ArrayOfint1.length; b++)
        printTypeInfo(param1ArrayOfint1[b], param1ArrayOfint2[b]); 
      this.writer.println("[stack]");
      for (b = 0; b < param1ArrayOfint3.length; b++)
        printTypeInfo(param1ArrayOfint3[b], param1ArrayOfint4[b]); 
    }
    
    private void printTypeInfo(int param1Int1, int param1Int2) {
      String str = null;
      switch (param1Int1) {
        case 0:
          str = "top";
          break;
        case 1:
          str = "integer";
          break;
        case 2:
          str = "float";
          break;
        case 3:
          str = "double";
          break;
        case 4:
          str = "long";
          break;
        case 5:
          str = "null";
          break;
        case 6:
          str = "this";
          break;
        case 7:
          str = "object (cpool_index " + param1Int2 + ")";
          break;
        case 8:
          str = "uninitialized (offset " + param1Int2 + ")";
          break;
      } 
      this.writer.print("    ");
      this.writer.println(str);
    }
  }
  
  void shiftPc(int paramInt1, int paramInt2, boolean paramBoolean) throws BadBytecode {
    (new OffsetShifter(this, paramInt1, paramInt2)).parse();
    (new Shifter(this, paramInt1, paramInt2, paramBoolean)).doit();
  }
  
  static class OffsetShifter extends Walker {
    int where;
    
    int gap;
    
    public OffsetShifter(StackMapTable param1StackMapTable, int param1Int1, int param1Int2) {
      super(param1StackMapTable);
      this.where = param1Int1;
      this.gap = param1Int2;
    }
    
    public void objectOrUninitialized(int param1Int1, int param1Int2, int param1Int3) {
      if (param1Int1 == 8 && 
        this.where <= param1Int2)
        ByteArray.write16bit(param1Int2 + this.gap, this.info, param1Int3); 
    }
  }
  
  static class Shifter extends Walker {
    private StackMapTable stackMap;
    
    int where;
    
    int gap;
    
    int position;
    
    byte[] updatedInfo;
    
    boolean exclusive;
    
    public Shifter(StackMapTable param1StackMapTable, int param1Int1, int param1Int2, boolean param1Boolean) {
      super(param1StackMapTable);
      this.stackMap = param1StackMapTable;
      this.where = param1Int1;
      this.gap = param1Int2;
      this.position = 0;
      this.updatedInfo = null;
      this.exclusive = param1Boolean;
    }
    
    public void doit() throws BadBytecode {
      parse();
      if (this.updatedInfo != null)
        this.stackMap.set(this.updatedInfo); 
    }
    
    public void sameFrame(int param1Int1, int param1Int2) {
      update(param1Int1, param1Int2, 0, 251);
    }
    
    public void sameLocals(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      update(param1Int1, param1Int2, 64, 247);
    }
    
    void update(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      boolean bool;
      int i = this.position;
      this.position = i + param1Int2 + ((i == 0) ? 0 : 1);
      if (this.exclusive) {
        bool = (i < this.where && this.where <= this.position) ? true : false;
      } else {
        bool = (i <= this.where && this.where < this.position) ? true : false;
      } 
      if (bool) {
        int j = param1Int2 + this.gap;
        this.position += this.gap;
        if (j < 64) {
          this.info[param1Int1] = (byte)(j + param1Int3);
        } else if (param1Int2 < 64) {
          byte[] arrayOfByte = insertGap(this.info, param1Int1, 2);
          arrayOfByte[param1Int1] = (byte)param1Int4;
          ByteArray.write16bit(j, arrayOfByte, param1Int1 + 1);
          this.updatedInfo = arrayOfByte;
        } else {
          ByteArray.write16bit(j, this.info, param1Int1 + 1);
        } 
      } 
    }
    
    static byte[] insertGap(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      int i = param1ArrayOfbyte.length;
      byte[] arrayOfByte = new byte[i + param1Int2];
      for (byte b = 0; b < i; b++)
        arrayOfByte[b + ((b < param1Int1) ? 0 : param1Int2)] = param1ArrayOfbyte[b]; 
      return arrayOfByte;
    }
    
    public void chopFrame(int param1Int1, int param1Int2, int param1Int3) {
      update(param1Int1, param1Int2);
    }
    
    public void appendFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2) {
      update(param1Int1, param1Int2);
    }
    
    public void fullFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2, int[] param1ArrayOfint3, int[] param1ArrayOfint4) {
      update(param1Int1, param1Int2);
    }
    
    void update(int param1Int1, int param1Int2) {
      boolean bool;
      int i = this.position;
      this.position = i + param1Int2 + ((i == 0) ? 0 : 1);
      if (this.exclusive) {
        bool = (i < this.where && this.where <= this.position) ? true : false;
      } else {
        bool = (i <= this.where && this.where < this.position) ? true : false;
      } 
      if (bool) {
        int j = param1Int2 + this.gap;
        ByteArray.write16bit(j, this.info, param1Int1 + 1);
        this.position += this.gap;
      } 
    }
  }
  
  void shiftForSwitch(int paramInt1, int paramInt2) throws BadBytecode {
    (new SwitchShifter(this, paramInt1, paramInt2)).doit();
  }
  
  static class SwitchShifter extends Shifter {
    SwitchShifter(StackMapTable param1StackMapTable, int param1Int1, int param1Int2) {
      super(param1StackMapTable, param1Int1, param1Int2, false);
    }
    
    void update(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      int i = this.position;
      this.position = i + param1Int2 + ((i == 0) ? 0 : 1);
      int j = param1Int2;
      if (this.where == this.position) {
        j = param1Int2 - this.gap;
      } else if (this.where == i) {
        j = param1Int2 + this.gap;
      } else {
        return;
      } 
      if (param1Int2 < 64) {
        if (j < 64) {
          this.info[param1Int1] = (byte)(j + param1Int3);
        } else {
          byte[] arrayOfByte = insertGap(this.info, param1Int1, 2);
          arrayOfByte[param1Int1] = (byte)param1Int4;
          ByteArray.write16bit(j, arrayOfByte, param1Int1 + 1);
          this.updatedInfo = arrayOfByte;
        } 
      } else if (j < 64) {
        byte[] arrayOfByte = deleteGap(this.info, param1Int1, 2);
        arrayOfByte[param1Int1] = (byte)(j + param1Int3);
        this.updatedInfo = arrayOfByte;
      } else {
        ByteArray.write16bit(j, this.info, param1Int1 + 1);
      } 
    }
    
    static byte[] deleteGap(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      param1Int1 += param1Int2;
      int i = param1ArrayOfbyte.length;
      byte[] arrayOfByte = new byte[i - param1Int2];
      for (byte b = 0; b < i; b++)
        arrayOfByte[b - ((b < param1Int1) ? 0 : param1Int2)] = param1ArrayOfbyte[b]; 
      return arrayOfByte;
    }
    
    void update(int param1Int1, int param1Int2) {
      int i = this.position;
      this.position = i + param1Int2 + ((i == 0) ? 0 : 1);
      int j = param1Int2;
      if (this.where == this.position) {
        j = param1Int2 - this.gap;
      } else if (this.where == i) {
        j = param1Int2 + this.gap;
      } else {
        return;
      } 
      ByteArray.write16bit(j, this.info, param1Int1 + 1);
    }
  }
  
  public void removeNew(int paramInt) throws CannotCompileException {
    try {
      byte[] arrayOfByte = (new NewRemover(get(), paramInt)).doit();
      set(arrayOfByte);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException("bad stack map table", badBytecode);
    } 
  }
  
  static class NewRemover extends SimpleCopy {
    int posOfNew;
    
    public NewRemover(byte[] param1ArrayOfbyte, int param1Int) {
      super(param1ArrayOfbyte);
      this.posOfNew = param1Int;
    }
    
    public void sameLocals(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1Int3 == 8 && param1Int4 == this.posOfNew) {
        sameFrame(param1Int1, param1Int2);
      } else {
        super.sameLocals(param1Int1, param1Int2, param1Int3, param1Int4);
      } 
    }
    
    public void fullFrame(int param1Int1, int param1Int2, int[] param1ArrayOfint1, int[] param1ArrayOfint2, int[] param1ArrayOfint3, int[] param1ArrayOfint4) {
      int i = param1ArrayOfint3.length - 1;
      for (byte b = 0; b < i; b++) {
        if (param1ArrayOfint3[b] == 8 && param1ArrayOfint4[b] == this.posOfNew && param1ArrayOfint3[b + 1] == 8 && param1ArrayOfint4[b + 1] == this.posOfNew) {
          i++;
          int[] arrayOfInt1 = new int[i - 2];
          int[] arrayOfInt2 = new int[i - 2];
          byte b1 = 0;
          for (byte b2 = 0; b2 < i; b2++) {
            if (b2 == b) {
              b2++;
            } else {
              arrayOfInt1[b1] = param1ArrayOfint3[b2];
              arrayOfInt2[b1++] = param1ArrayOfint4[b2];
            } 
          } 
          param1ArrayOfint3 = arrayOfInt1;
          param1ArrayOfint4 = arrayOfInt2;
          break;
        } 
      } 
      super.fullFrame(param1Int1, param1Int2, param1ArrayOfint1, param1ArrayOfint2, param1ArrayOfint3, param1ArrayOfint4);
    }
  }
}
