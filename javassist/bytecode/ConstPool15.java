package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool15 extends ConstPool13 {
  static final int tag = 4;
  
  float value;
  
  public ConstPool15(float paramFloat, int paramInt) {
    super(paramInt);
    this.value = paramFloat;
  }
  
  public ConstPool15(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.value = paramDataInputStream.readFloat();
  }
  
  public int hashCode() {
    return Float.floatToIntBits(this.value);
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof ConstPool15 && ((ConstPool15)paramObject).value == this.value);
  }
  
  public int getTag() {
    return 4;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addFloatInfo(this.value);
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(4);
    paramDataOutputStream.writeFloat(this.value);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("Float ");
    paramPrintWriter.println(this.value);
  }
}
