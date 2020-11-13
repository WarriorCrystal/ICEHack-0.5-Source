package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ExceptionTable implements Cloneable {
  private ConstPool14 constPool;
  
  private ArrayList entries;
  
  public ExceptionTable(ConstPool14 paramConstPool14) {
    this.constPool = paramConstPool14;
    this.entries = new ArrayList();
  }
  
  ExceptionTable(ConstPool14 paramConstPool14, DataInputStream paramDataInputStream) throws IOException {
    this.constPool = paramConstPool14;
    int i = paramDataInputStream.readUnsignedShort();
    ArrayList<ExceptionTable1> arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      int j = paramDataInputStream.readUnsignedShort();
      int k = paramDataInputStream.readUnsignedShort();
      int m = paramDataInputStream.readUnsignedShort();
      int n = paramDataInputStream.readUnsignedShort();
      arrayList.add(new ExceptionTable1(j, k, m, n));
    } 
    this.entries = arrayList;
  }
  
  public Object clone() throws CloneNotSupportedException {
    ExceptionTable exceptionTable = (ExceptionTable)super.clone();
    exceptionTable.entries = new ArrayList(this.entries);
    return exceptionTable;
  }
  
  public int size() {
    return this.entries.size();
  }
  
  public int startPc(int paramInt) {
    ExceptionTable1 exceptionTable1 = this.entries.get(paramInt);
    return exceptionTable1.startPc;
  }
  
  public void setStartPc(int paramInt1, int paramInt2) {
    ExceptionTable1 exceptionTable1 = this.entries.get(paramInt1);
    exceptionTable1.startPc = paramInt2;
  }
  
  public int endPc(int paramInt) {
    ExceptionTable1 exceptionTable1 = this.entries.get(paramInt);
    return exceptionTable1.endPc;
  }
  
  public void setEndPc(int paramInt1, int paramInt2) {
    ExceptionTable1 exceptionTable1 = this.entries.get(paramInt1);
    exceptionTable1.endPc = paramInt2;
  }
  
  public int handlerPc(int paramInt) {
    ExceptionTable1 exceptionTable1 = this.entries.get(paramInt);
    return exceptionTable1.handlerPc;
  }
  
  public void setHandlerPc(int paramInt1, int paramInt2) {
    ExceptionTable1 exceptionTable1 = this.entries.get(paramInt1);
    exceptionTable1.handlerPc = paramInt2;
  }
  
  public int catchType(int paramInt) {
    ExceptionTable1 exceptionTable1 = this.entries.get(paramInt);
    return exceptionTable1.catchType;
  }
  
  public void setCatchType(int paramInt1, int paramInt2) {
    ExceptionTable1 exceptionTable1 = this.entries.get(paramInt1);
    exceptionTable1.catchType = paramInt2;
  }
  
  public void add(int paramInt1, ExceptionTable paramExceptionTable, int paramInt2) {
    int i = paramExceptionTable.size();
    while (--i >= 0) {
      ExceptionTable1 exceptionTable1 = paramExceptionTable.entries.get(i);
      add(paramInt1, exceptionTable1.startPc + paramInt2, exceptionTable1.endPc + paramInt2, exceptionTable1.handlerPc + paramInt2, exceptionTable1.catchType);
    } 
  }
  
  public void add(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if (paramInt2 < paramInt3)
      this.entries.add(paramInt1, new ExceptionTable1(paramInt2, paramInt3, paramInt4, paramInt5)); 
  }
  
  public void add(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt1 < paramInt2)
      this.entries.add(new ExceptionTable1(paramInt1, paramInt2, paramInt3, paramInt4)); 
  }
  
  public void remove(int paramInt) {
    this.entries.remove(paramInt);
  }
  
  public ExceptionTable copy(ConstPool14 paramConstPool14, Map paramMap) {
    ExceptionTable exceptionTable = new ExceptionTable(paramConstPool14);
    ConstPool14 constPool14 = this.constPool;
    int i = size();
    for (byte b = 0; b < i; b++) {
      ExceptionTable1 exceptionTable1 = this.entries.get(b);
      int j = constPool14.copy(exceptionTable1.catchType, paramConstPool14, paramMap);
      exceptionTable.add(exceptionTable1.startPc, exceptionTable1.endPc, exceptionTable1.handlerPc, j);
    } 
    return exceptionTable;
  }
  
  void shiftPc(int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = size();
    for (byte b = 0; b < i; b++) {
      ExceptionTable1 exceptionTable1 = this.entries.get(b);
      exceptionTable1.startPc = shiftPc(exceptionTable1.startPc, paramInt1, paramInt2, paramBoolean);
      exceptionTable1.endPc = shiftPc(exceptionTable1.endPc, paramInt1, paramInt2, paramBoolean);
      exceptionTable1.handlerPc = shiftPc(exceptionTable1.handlerPc, paramInt1, paramInt2, paramBoolean);
    } 
  }
  
  private static int shiftPc(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    if (paramInt1 > paramInt2 || (paramBoolean && paramInt1 == paramInt2))
      paramInt1 += paramInt3; 
    return paramInt1;
  }
  
  void write(DataOutputStream paramDataOutputStream) throws IOException {
    int i = size();
    paramDataOutputStream.writeShort(i);
    for (byte b = 0; b < i; b++) {
      ExceptionTable1 exceptionTable1 = this.entries.get(b);
      paramDataOutputStream.writeShort(exceptionTable1.startPc);
      paramDataOutputStream.writeShort(exceptionTable1.endPc);
      paramDataOutputStream.writeShort(exceptionTable1.handlerPc);
      paramDataOutputStream.writeShort(exceptionTable1.catchType);
    } 
  }
}
