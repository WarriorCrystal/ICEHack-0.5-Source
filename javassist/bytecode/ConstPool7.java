package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool7 extends ConstPool13 {
  static final int tag = 3;
  
  int value;
  
  public ConstPool7(int paramInt1, int paramInt2) {
    super(paramInt2);
    this.value = paramInt1;
  }
  
  public ConstPool7(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.value = paramDataInputStream.readInt();
  }
  
  public int hashCode() {
    return this.value;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof ConstPool7 && ((ConstPool7)paramObject).value == this.value);
  }
  
  public int getTag() {
    return 3;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addIntegerInfo(this.value);
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(3);
    paramDataOutputStream.writeInt(this.value);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("Integer ");
    paramPrintWriter.println(this.value);
  }
}
