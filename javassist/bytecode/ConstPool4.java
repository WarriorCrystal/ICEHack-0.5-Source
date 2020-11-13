package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

class ConstPool4 extends ConstPool13 {
  static final int tag = 7;
  
  int name;
  
  public ConstPool4(int paramInt1, int paramInt2) {
    super(paramInt2);
    this.name = paramInt1;
  }
  
  public ConstPool4(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.name = paramDataInputStream.readUnsignedShort();
  }
  
  public int hashCode() {
    return this.name;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof ConstPool4 && ((ConstPool4)paramObject).name == this.name);
  }
  
  public int getTag() {
    return 7;
  }
  
  public String getClassName(ConstPool14 paramConstPool14) {
    return paramConstPool14.getUtf8Info(this.name);
  }
  
  public void renameClass(ConstPool14 paramConstPool14, String paramString1, String paramString2, HashMap<ConstPool4, ConstPool4> paramHashMap) {
    String str1 = paramConstPool14.getUtf8Info(this.name);
    String str2 = null;
    if (str1.equals(paramString1)) {
      str2 = paramString2;
    } else if (str1.charAt(0) == '[') {
      String str = Descriptor.rename(str1, paramString1, paramString2);
      if (str1 != str)
        str2 = str; 
    } 
    if (str2 != null)
      if (paramHashMap == null) {
        this.name = paramConstPool14.addUtf8Info(str2);
      } else {
        paramHashMap.remove(this);
        this.name = paramConstPool14.addUtf8Info(str2);
        paramHashMap.put(this, this);
      }  
  }
  
  public void renameClass(ConstPool14 paramConstPool14, Map paramMap, HashMap<ConstPool4, ConstPool4> paramHashMap) {
    String str1 = paramConstPool14.getUtf8Info(this.name);
    String str2 = null;
    if (str1.charAt(0) == '[') {
      String str = Descriptor.rename(str1, paramMap);
      if (str1 != str)
        str2 = str; 
    } else {
      String str = (String)paramMap.get(str1);
      if (str != null && !str.equals(str1))
        str2 = str; 
    } 
    if (str2 != null)
      if (paramHashMap == null) {
        this.name = paramConstPool14.addUtf8Info(str2);
      } else {
        paramHashMap.remove(this);
        this.name = paramConstPool14.addUtf8Info(str2);
        paramHashMap.put(this, this);
      }  
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    String str = paramConstPool141.getUtf8Info(this.name);
    if (paramMap != null) {
      String str1 = (String)paramMap.get(str);
      if (str1 != null)
        str = str1; 
    } 
    return paramConstPool142.addClassInfo(str);
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(7);
    paramDataOutputStream.writeShort(this.name);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("Class #");
    paramPrintWriter.println(this.name);
  }
}
