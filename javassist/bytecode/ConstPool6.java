package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool6 extends ConstPool13 {
  static final int tag = 18;
  
  int bootstrap;
  
  int nameAndType;
  
  public ConstPool6(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt3);
    this.bootstrap = paramInt1;
    this.nameAndType = paramInt2;
  }
  
  public ConstPool6(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.bootstrap = paramDataInputStream.readUnsignedShort();
    this.nameAndType = paramDataInputStream.readUnsignedShort();
  }
  
  public int hashCode() {
    return this.bootstrap << 16 ^ this.nameAndType;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ConstPool6) {
      ConstPool6 constPool6 = (ConstPool6)paramObject;
      return (constPool6.bootstrap == this.bootstrap && constPool6.nameAndType == this.nameAndType);
    } 
    return false;
  }
  
  public int getTag() {
    return 18;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addInvokeDynamicInfo(this.bootstrap, paramConstPool141
        .getItem(this.nameAndType).copy(paramConstPool141, paramConstPool142, paramMap));
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(18);
    paramDataOutputStream.writeShort(this.bootstrap);
    paramDataOutputStream.writeShort(this.nameAndType);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("InvokeDynamic #");
    paramPrintWriter.print(this.bootstrap);
    paramPrintWriter.print(", name&type #");
    paramPrintWriter.println(this.nameAndType);
  }
}
