package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

abstract class ConstPool17 extends ConstPool13 {
  int classIndex;
  
  int nameAndTypeIndex;
  
  public ConstPool17(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt3);
    this.classIndex = paramInt1;
    this.nameAndTypeIndex = paramInt2;
  }
  
  public ConstPool17(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.classIndex = paramDataInputStream.readUnsignedShort();
    this.nameAndTypeIndex = paramDataInputStream.readUnsignedShort();
  }
  
  public int hashCode() {
    return this.classIndex << 16 ^ this.nameAndTypeIndex;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ConstPool17) {
      ConstPool17 constPool17 = (ConstPool17)paramObject;
      return (constPool17.classIndex == this.classIndex && constPool17.nameAndTypeIndex == this.nameAndTypeIndex && constPool17
        .getClass() == getClass());
    } 
    return false;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    int i = paramConstPool141.getItem(this.classIndex).copy(paramConstPool141, paramConstPool142, paramMap);
    int j = paramConstPool141.getItem(this.nameAndTypeIndex).copy(paramConstPool141, paramConstPool142, paramMap);
    return copy2(paramConstPool142, i, j);
  }
  
  protected abstract int copy2(ConstPool14 paramConstPool14, int paramInt1, int paramInt2);
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(getTag());
    paramDataOutputStream.writeShort(this.classIndex);
    paramDataOutputStream.writeShort(this.nameAndTypeIndex);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print(getTagName() + " #");
    paramPrintWriter.print(this.classIndex);
    paramPrintWriter.print(", name&type #");
    paramPrintWriter.println(this.nameAndTypeIndex);
  }
  
  public abstract String getTagName();
}
