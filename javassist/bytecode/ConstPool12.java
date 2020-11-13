package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool12 extends ConstPool13 {
  static final int tag = 1;
  
  String string;
  
  public ConstPool12(String paramString, int paramInt) {
    super(paramInt);
    this.string = paramString;
  }
  
  public ConstPool12(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.string = paramDataInputStream.readUTF();
  }
  
  public int hashCode() {
    return this.string.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof ConstPool12 && ((ConstPool12)paramObject).string.equals(this.string));
  }
  
  public int getTag() {
    return 1;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addUtf8Info(this.string);
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(1);
    paramDataOutputStream.writeUTF(this.string);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("UTF8 \"");
    paramPrintWriter.print(this.string);
    paramPrintWriter.println("\"");
  }
}
