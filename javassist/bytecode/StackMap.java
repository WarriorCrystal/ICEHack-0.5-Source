package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javassist.CannotCompileException;

public class StackMap extends AttributeInfo {
  public static final String tag = "StackMap";
  
  public static final int TOP = 0;
  
  public static final int INTEGER = 1;
  
  public static final int FLOAT = 2;
  
  public static final int DOUBLE = 3;
  
  public static final int LONG = 4;
  
  public static final int NULL = 5;
  
  public static final int THIS = 6;
  
  public static final int OBJECT = 7;
  
  public static final int UNINIT = 8;
  
  StackMap(ConstPool14 paramConstPool14, byte[] paramArrayOfbyte) {
    super(paramConstPool14, "StackMap", paramArrayOfbyte);
  }
  
  StackMap(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public int numOfEntries() {
    return ByteArray.readU16bit(this.info, 0);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    Copier copier = new Copier(this, paramConstPool14, paramMap);
    copier.visit();
    return copier.getStackMap();
  }
  
  public static class Walker {
    byte[] info;
    
    public Walker(StackMap param1StackMap) {
      this.info = param1StackMap.get();
    }
    
    public void visit() {
      int i = ByteArray.readU16bit(this.info, 0);
      int j = 2;
      for (byte b = 0; b < i; b++) {
        int k = ByteArray.readU16bit(this.info, j);
        int m = ByteArray.readU16bit(this.info, j + 2);
        j = locals(j + 4, k, m);
        int n = ByteArray.readU16bit(this.info, j);
        j = stack(j + 2, k, n);
      } 
    }
    
    public int locals(int param1Int1, int param1Int2, int param1Int3) {
      return typeInfoArray(param1Int1, param1Int2, param1Int3, true);
    }
    
    public int stack(int param1Int1, int param1Int2, int param1Int3) {
      return typeInfoArray(param1Int1, param1Int2, param1Int3, false);
    }
    
    public int typeInfoArray(int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      for (byte b = 0; b < param1Int3; b++)
        param1Int1 = typeInfoArray2(b, param1Int1); 
      return param1Int1;
    }
    
    int typeInfoArray2(int param1Int1, int param1Int2) {
      byte b = this.info[param1Int2];
      if (b == 7) {
        int i = ByteArray.readU16bit(this.info, param1Int2 + 1);
        objectVariable(param1Int2, i);
        param1Int2 += 3;
      } else if (b == 8) {
        int i = ByteArray.readU16bit(this.info, param1Int2 + 1);
        uninitialized(param1Int2, i);
        param1Int2 += 3;
      } else {
        typeInfo(param1Int2, b);
        param1Int2++;
      } 
      return param1Int2;
    }
    
    public void typeInfo(int param1Int, byte param1Byte) {}
    
    public void objectVariable(int param1Int1, int param1Int2) {}
    
    public void uninitialized(int param1Int1, int param1Int2) {}
  }
  
  static class Copier extends Walker {
    byte[] dest;
    
    ConstPool14 srcCp;
    
    ConstPool14 destCp;
    
    Map classnames;
    
    Copier(StackMap param1StackMap, ConstPool14 param1ConstPool14, Map param1Map) {
      super(param1StackMap);
      this.srcCp = param1StackMap.getConstPool();
      this.dest = new byte[this.info.length];
      this.destCp = param1ConstPool14;
      this.classnames = param1Map;
    }
    
    public void visit() {
      int i = ByteArray.readU16bit(this.info, 0);
      ByteArray.write16bit(i, this.dest, 0);
      super.visit();
    }
    
    public int locals(int param1Int1, int param1Int2, int param1Int3) {
      ByteArray.write16bit(param1Int2, this.dest, param1Int1 - 4);
      return super.locals(param1Int1, param1Int2, param1Int3);
    }
    
    public int typeInfoArray(int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      ByteArray.write16bit(param1Int3, this.dest, param1Int1 - 2);
      return super.typeInfoArray(param1Int1, param1Int2, param1Int3, param1Boolean);
    }
    
    public void typeInfo(int param1Int, byte param1Byte) {
      this.dest[param1Int] = param1Byte;
    }
    
    public void objectVariable(int param1Int1, int param1Int2) {
      this.dest[param1Int1] = 7;
      int i = this.srcCp.copy(param1Int2, this.destCp, this.classnames);
      ByteArray.write16bit(i, this.dest, param1Int1 + 1);
    }
    
    public void uninitialized(int param1Int1, int param1Int2) {
      this.dest[param1Int1] = 8;
      ByteArray.write16bit(param1Int2, this.dest, param1Int1 + 1);
    }
    
    public StackMap getStackMap() {
      return new StackMap(this.destCp, this.dest);
    }
  }
  
  public void insertLocal(int paramInt1, int paramInt2, int paramInt3) throws BadBytecode {
    byte[] arrayOfByte = (new InsertLocal(this, paramInt1, paramInt2, paramInt3)).doit();
    set(arrayOfByte);
  }
  
  static class SimpleCopy extends Walker {
    StackMap.Writer writer;
    
    SimpleCopy(StackMap param1StackMap) {
      super(param1StackMap);
      this.writer = new StackMap.Writer();
    }
    
    byte[] doit() {
      visit();
      return this.writer.toByteArray();
    }
    
    public void visit() {
      int i = ByteArray.readU16bit(this.info, 0);
      this.writer.write16bit(i);
      super.visit();
    }
    
    public int locals(int param1Int1, int param1Int2, int param1Int3) {
      this.writer.write16bit(param1Int2);
      return super.locals(param1Int1, param1Int2, param1Int3);
    }
    
    public int typeInfoArray(int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      this.writer.write16bit(param1Int3);
      return super.typeInfoArray(param1Int1, param1Int2, param1Int3, param1Boolean);
    }
    
    public void typeInfo(int param1Int, byte param1Byte) {
      this.writer.writeVerifyTypeInfo(param1Byte, 0);
    }
    
    public void objectVariable(int param1Int1, int param1Int2) {
      this.writer.writeVerifyTypeInfo(7, param1Int2);
    }
    
    public void uninitialized(int param1Int1, int param1Int2) {
      this.writer.writeVerifyTypeInfo(8, param1Int2);
    }
  }
  
  static class InsertLocal extends SimpleCopy {
    private int varIndex;
    
    private int varTag;
    
    private int varData;
    
    InsertLocal(StackMap param1StackMap, int param1Int1, int param1Int2, int param1Int3) {
      super(param1StackMap);
      this.varIndex = param1Int1;
      this.varTag = param1Int2;
      this.varData = param1Int3;
    }
    
    public int typeInfoArray(int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      if (!param1Boolean || param1Int3 < this.varIndex)
        return super.typeInfoArray(param1Int1, param1Int2, param1Int3, param1Boolean); 
      this.writer.write16bit(param1Int3 + 1);
      for (byte b = 0; b < param1Int3; b++) {
        if (b == this.varIndex)
          writeVarTypeInfo(); 
        param1Int1 = typeInfoArray2(b, param1Int1);
      } 
      if (param1Int3 == this.varIndex)
        writeVarTypeInfo(); 
      return param1Int1;
    }
    
    private void writeVarTypeInfo() {
      if (this.varTag == 7) {
        this.writer.writeVerifyTypeInfo(7, this.varData);
      } else if (this.varTag == 8) {
        this.writer.writeVerifyTypeInfo(8, this.varData);
      } else {
        this.writer.writeVerifyTypeInfo(this.varTag, 0);
      } 
    }
  }
  
  void shiftPc(int paramInt1, int paramInt2, boolean paramBoolean) throws BadBytecode {
    (new Shifter(this, paramInt1, paramInt2, paramBoolean)).visit();
  }
  
  static class Shifter extends Walker {
    private int where;
    
    private int gap;
    
    private boolean exclusive;
    
    public Shifter(StackMap param1StackMap, int param1Int1, int param1Int2, boolean param1Boolean) {
      super(param1StackMap);
      this.where = param1Int1;
      this.gap = param1Int2;
      this.exclusive = param1Boolean;
    }
    
    public int locals(int param1Int1, int param1Int2, int param1Int3) {
      if (this.exclusive ? (this.where <= param1Int2) : (this.where < param1Int2))
        ByteArray.write16bit(param1Int2 + this.gap, this.info, param1Int1 - 4); 
      return super.locals(param1Int1, param1Int2, param1Int3);
    }
    
    public void uninitialized(int param1Int1, int param1Int2) {
      if (this.where <= param1Int2)
        ByteArray.write16bit(param1Int2 + this.gap, this.info, param1Int1 + 1); 
    }
  }
  
  void shiftForSwitch(int paramInt1, int paramInt2) throws BadBytecode {
    (new SwitchShifter(this, paramInt1, paramInt2)).visit();
  }
  
  static class SwitchShifter extends Walker {
    private int where;
    
    private int gap;
    
    public SwitchShifter(StackMap param1StackMap, int param1Int1, int param1Int2) {
      super(param1StackMap);
      this.where = param1Int1;
      this.gap = param1Int2;
    }
    
    public int locals(int param1Int1, int param1Int2, int param1Int3) {
      if (this.where == param1Int1 + param1Int2) {
        ByteArray.write16bit(param1Int2 - this.gap, this.info, param1Int1 - 4);
      } else if (this.where == param1Int1) {
        ByteArray.write16bit(param1Int2 + this.gap, this.info, param1Int1 - 4);
      } 
      return super.locals(param1Int1, param1Int2, param1Int3);
    }
  }
  
  public void removeNew(int paramInt) throws CannotCompileException {
    byte[] arrayOfByte = (new NewRemover(this, paramInt)).doit();
    set(arrayOfByte);
  }
  
  static class NewRemover extends SimpleCopy {
    int posOfNew;
    
    NewRemover(StackMap param1StackMap, int param1Int) {
      super(param1StackMap);
      this.posOfNew = param1Int;
    }
    
    public int stack(int param1Int1, int param1Int2, int param1Int3) {
      return stackTypeInfoArray(param1Int1, param1Int2, param1Int3);
    }
    
    private int stackTypeInfoArray(int param1Int1, int param1Int2, int param1Int3) {
      int i = param1Int1;
      byte b1 = 0;
      byte b2;
      for (b2 = 0; b2 < param1Int3; b2++) {
        byte b = this.info[i];
        if (b == 7) {
          i += 3;
        } else if (b == 8) {
          int j = ByteArray.readU16bit(this.info, i + 1);
          if (j == this.posOfNew)
            b1++; 
          i += 3;
        } else {
          i++;
        } 
      } 
      this.writer.write16bit(param1Int3 - b1);
      for (b2 = 0; b2 < param1Int3; b2++) {
        byte b = this.info[param1Int1];
        if (b == 7) {
          int j = ByteArray.readU16bit(this.info, param1Int1 + 1);
          objectVariable(param1Int1, j);
          param1Int1 += 3;
        } else if (b == 8) {
          int j = ByteArray.readU16bit(this.info, param1Int1 + 1);
          if (j != this.posOfNew)
            uninitialized(param1Int1, j); 
          param1Int1 += 3;
        } else {
          typeInfo(param1Int1, b);
          param1Int1++;
        } 
      } 
      return param1Int1;
    }
  }
  
  public void print(PrintWriter paramPrintWriter) {
    (new Printer(this, paramPrintWriter)).print();
  }
  
  static class Printer extends Walker {
    private PrintWriter writer;
    
    public Printer(StackMap param1StackMap, PrintWriter param1PrintWriter) {
      super(param1StackMap);
      this.writer = param1PrintWriter;
    }
    
    public void print() {
      int i = ByteArray.readU16bit(this.info, 0);
      this.writer.println(i + " entries");
      visit();
    }
    
    public int locals(int param1Int1, int param1Int2, int param1Int3) {
      this.writer.println("  * offset " + param1Int2);
      return super.locals(param1Int1, param1Int2, param1Int3);
    }
  }
  
  public static class Writer {
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    
    public byte[] toByteArray() {
      return this.output.toByteArray();
    }
    
    public StackMap toStackMap(ConstPool14 param1ConstPool14) {
      return new StackMap(param1ConstPool14, this.output.toByteArray());
    }
    
    public void writeVerifyTypeInfo(int param1Int1, int param1Int2) {
      this.output.write(param1Int1);
      if (param1Int1 == 7 || param1Int1 == 8)
        write16bit(param1Int2); 
    }
    
    public void write16bit(int param1Int) {
      this.output.write(param1Int >>> 8 & 0xFF);
      this.output.write(param1Int & 0xFF);
    }
  }
}
