package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool16 extends ConstPool13 {
  static final int tag = 5;
  
  long value;
  
  public ConstPool16(long paramLong, int paramInt) {
    super(paramInt);
    this.value = paramLong;
  }
  
  public ConstPool16(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.value = paramDataInputStream.readLong();
  }
  
  public int hashCode() {
    return (int)(this.value ^ this.value >>> 32L);
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof ConstPool16 && ((ConstPool16)paramObject).value == this.value);
  }
  
  public int getTag() {
    return 5;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addLongInfo(this.value);
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(5);
    paramDataOutputStream.writeLong(this.value);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("Long ");
    paramPrintWriter.println(this.value);
  }
}
