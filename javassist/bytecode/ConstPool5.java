package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool5 extends ConstPool13 {
  static final int tag = 6;
  
  double value;
  
  public ConstPool5(double paramDouble, int paramInt) {
    super(paramInt);
    this.value = paramDouble;
  }
  
  public ConstPool5(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.value = paramDataInputStream.readDouble();
  }
  
  public int hashCode() {
    long l = Double.doubleToLongBits(this.value);
    return (int)(l ^ l >>> 32L);
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof ConstPool5 && ((ConstPool5)paramObject).value == this.value);
  }
  
  public int getTag() {
    return 6;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addDoubleInfo(this.value);
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(6);
    paramDataOutputStream.writeDouble(this.value);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("Double ");
    paramPrintWriter.println(this.value);
  }
}
