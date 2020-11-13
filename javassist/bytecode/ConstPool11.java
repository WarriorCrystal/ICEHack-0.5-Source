package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool11 extends ConstPool13 {
  static final int tag = 15;
  
  int refKind;
  
  int refIndex;
  
  public ConstPool11(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt3);
    this.refKind = paramInt1;
    this.refIndex = paramInt2;
  }
  
  public ConstPool11(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.refKind = paramDataInputStream.readUnsignedByte();
    this.refIndex = paramDataInputStream.readUnsignedShort();
  }
  
  public int hashCode() {
    return this.refKind << 16 ^ this.refIndex;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ConstPool11) {
      ConstPool11 constPool11 = (ConstPool11)paramObject;
      return (constPool11.refKind == this.refKind && constPool11.refIndex == this.refIndex);
    } 
    return false;
  }
  
  public int getTag() {
    return 15;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addMethodHandleInfo(this.refKind, paramConstPool141
        .getItem(this.refIndex).copy(paramConstPool141, paramConstPool142, paramMap));
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(15);
    paramDataOutputStream.writeByte(this.refKind);
    paramDataOutputStream.writeShort(this.refIndex);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("MethodHandle #");
    paramPrintWriter.print(this.refKind);
    paramPrintWriter.print(", index #");
    paramPrintWriter.println(this.refIndex);
  }
}
