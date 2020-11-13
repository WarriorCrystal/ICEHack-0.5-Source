package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool3 extends ConstPool13 {
  static final int tag = 8;
  
  int string;
  
  public ConstPool3(int paramInt1, int paramInt2) {
    super(paramInt2);
    this.string = paramInt1;
  }
  
  public ConstPool3(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.string = paramDataInputStream.readUnsignedShort();
  }
  
  public int hashCode() {
    return this.string;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof ConstPool3 && ((ConstPool3)paramObject).string == this.string);
  }
  
  public int getTag() {
    return 8;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addStringInfo(paramConstPool141.getUtf8Info(this.string));
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(8);
    paramDataOutputStream.writeShort(this.string);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("String #");
    paramPrintWriter.println(this.string);
  }
}
