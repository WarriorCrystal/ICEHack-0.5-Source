package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

class ConstPool2 extends ConstPool13 {
  static final int tag = 12;
  
  int memberName;
  
  int typeDescriptor;
  
  public ConstPool2(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt3);
    this.memberName = paramInt1;
    this.typeDescriptor = paramInt2;
  }
  
  public ConstPool2(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramInt);
    this.memberName = paramDataInputStream.readUnsignedShort();
    this.typeDescriptor = paramDataInputStream.readUnsignedShort();
  }
  
  public int hashCode() {
    return this.memberName << 16 ^ this.typeDescriptor;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ConstPool2) {
      ConstPool2 constPool2 = (ConstPool2)paramObject;
      return (constPool2.memberName == this.memberName && constPool2.typeDescriptor == this.typeDescriptor);
    } 
    return false;
  }
  
  public int getTag() {
    return 12;
  }
  
  public void renameClass(ConstPool14 paramConstPool14, String paramString1, String paramString2, HashMap<ConstPool2, ConstPool2> paramHashMap) {
    String str1 = paramConstPool14.getUtf8Info(this.typeDescriptor);
    String str2 = Descriptor.rename(str1, paramString1, paramString2);
    if (str1 != str2)
      if (paramHashMap == null) {
        this.typeDescriptor = paramConstPool14.addUtf8Info(str2);
      } else {
        paramHashMap.remove(this);
        this.typeDescriptor = paramConstPool14.addUtf8Info(str2);
        paramHashMap.put(this, this);
      }  
  }
  
  public void renameClass(ConstPool14 paramConstPool14, Map paramMap, HashMap<ConstPool2, ConstPool2> paramHashMap) {
    String str1 = paramConstPool14.getUtf8Info(this.typeDescriptor);
    String str2 = Descriptor.rename(str1, paramMap);
    if (str1 != str2)
      if (paramHashMap == null) {
        this.typeDescriptor = paramConstPool14.addUtf8Info(str2);
      } else {
        paramHashMap.remove(this);
        this.typeDescriptor = paramConstPool14.addUtf8Info(str2);
        paramHashMap.put(this, this);
      }  
  }
  
  public int copy(ConstPool14 paramConstPool141, ConstPool14 paramConstPool142, Map paramMap) {
    String str1 = paramConstPool141.getUtf8Info(this.memberName);
    String str2 = paramConstPool141.getUtf8Info(this.typeDescriptor);
    str2 = Descriptor.rename(str2, paramMap);
    return paramConstPool142.addNameAndTypeInfo(paramConstPool142.addUtf8Info(str1), paramConstPool142
        .addUtf8Info(str2));
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeByte(12);
    paramDataOutputStream.writeShort(this.memberName);
    paramDataOutputStream.writeShort(this.typeDescriptor);
  }
  
  public void print(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("NameAndType #");
    paramPrintWriter.print(this.memberName);
    paramPrintWriter.print(", type #");
    paramPrintWriter.println(this.typeDescriptor);
  }
}
