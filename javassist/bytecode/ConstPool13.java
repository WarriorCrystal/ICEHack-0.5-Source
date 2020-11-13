package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

abstract class ConstPool13 {
  int index;
  
  public ConstPool13(int paramInt) {
    this.index = paramInt;
  }
  
  public abstract int getTag();
  
  public String getClassName(ConstPool14 paramConstPool14) {
    return null;
  }
  
  public void renameClass(ConstPool14 paramConstPool14, String paramString1, String paramString2, HashMap paramHashMap) {}
  
  public void renameClass(ConstPool14 paramConstPool14, Map paramMap, HashMap paramHashMap) {}
  
  public abstract int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap);
  
  public abstract void write(DataOutputStream paramDataOutputStream) throws IOException;
  
  public abstract void print(PrintWriter paramPrintWriter);
  
  public String toString() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
    print(printWriter);
    return byteArrayOutputStream.toString();
  }
}
