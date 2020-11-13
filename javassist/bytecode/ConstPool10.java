package javassist.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class ConstPool10 extends ConstPool13 {
  public ConstPool10(int paramInt) {
    super(paramInt);
  }
  
  public int getTag() {
    return 0;
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    return paramConstPool142.addConstInfoPadding();
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {}
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.println("padding");
  }
}
