package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

class ConstPool1 extends ConstPool13 {
  static final int tag = 16;
  
  int descriptor;
  
  public ConstPool1(int paramInt1, int paramInt2) {
    super(paramInt2);
    this.descriptor = paramInt1;
  }
  
  public ConstPool1(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.descriptor = paramDataInputStream.readUnsignedShort();
  }
  
  public int hashCode() {
    return this.descriptor;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ConstPool1)
      return (((ConstPool1)paramObject).descriptor == this.descriptor); 
    return false;
  }
  
  public int getTag() {
    return 16;
  }
  
  public void renameClass(ConstPool14 paramConstPool14, String paramString1, String paramString2, HashMap<ConstPool1, ConstPool1> paramHashMap) {
    String str1 = paramConstPool14.getUtf8Info(this.descriptor);
    String str2 = Descriptor.rename(str1, paramString1, paramString2);
    if (str1 != str2)
      if (paramHashMap == null) {
        this.descriptor = paramConstPool14.addUtf8Info(str2);
      } else {
        paramHashMap.remove(this);
        this.descriptor = paramConstPool14.addUtf8Info(str2);
        paramHashMap.put(this, this);
      }  
  }
  
  public void renameClass(ConstPool14 paramConstPool14, Map paramMap, HashMap<ConstPool1, ConstPool1> paramHashMap) {
    String str1 = paramConstPool14.getUtf8Info(this.descriptor);
    String str2 = Descriptor.rename(str1, paramMap);
    if (str1 != str2)
      if (paramHashMap == null) {
        this.descriptor = paramConstPool14.addUtf8Info(str2);
      } else {
        paramHashMap.remove(this);
        this.descriptor = paramConstPool14.addUtf8Info(str2);
        paramHashMap.put(this, this);
      }  
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    String str = paramConstPool141.getUtf8Info(this.descriptor);
    str = Descriptor.rename(str, paramMap);
    return paramConstPool142.addMethodTypeInfo(paramConstPool142.addUtf8Info(str));
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(16);
    paramDataOutputStream.writeShort(this.descriptor);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("MethodType #");
    paramPrintWriter.println(this.descriptor);
  }
}
