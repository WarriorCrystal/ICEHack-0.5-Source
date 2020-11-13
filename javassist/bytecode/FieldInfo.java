package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class FieldInfo {
  ConstPool14 constPool;
  
  int accessFlags;
  
  int name;
  
  String cachedName;
  
  String cachedType;
  
  int descriptor;
  
  ArrayList attribute;
  
  private FieldInfo(ConstPool14 paramConstPool14) {
    this.constPool = paramConstPool14;
    this.accessFlags = 0;
    this.attribute = null;
  }
  
  public FieldInfo(ConstPool14 paramConstPool14, String paramString1, String paramString2) {
    this(paramConstPool14);
    this.name = paramConstPool14.addUtf8Info(paramString1);
    this.cachedName = paramString1;
    this.descriptor = paramConstPool14.addUtf8Info(paramString2);
  }
  
  FieldInfo(ConstPool14 paramConstPool14, DataInputStream paramDataInputStream) throws IOException {
    this(paramConstPool14);
    read(paramDataInputStream);
  }
  
  public String toString() {
    return getName() + " " + getDescriptor();
  }
  
  void compact(ConstPool14 paramConstPool14) {
    this.name = paramConstPool14.addUtf8Info(getName());
    this.descriptor = paramConstPool14.addUtf8Info(getDescriptor());
    this.attribute = AttributeInfo.copyAll(this.attribute, paramConstPool14);
    this.constPool = paramConstPool14;
  }
  
  void prune(ConstPool14 paramConstPool14) {
    ArrayList<AttributeInfo> arrayList = new ArrayList();
    AttributeInfo attributeInfo1 = getAttribute("RuntimeInvisibleAnnotations");
    if (attributeInfo1 != null) {
      attributeInfo1 = attributeInfo1.copy(paramConstPool14, null);
      arrayList.add(attributeInfo1);
    } 
    AttributeInfo attributeInfo2 = getAttribute("RuntimeVisibleAnnotations");
    if (attributeInfo2 != null) {
      attributeInfo2 = attributeInfo2.copy(paramConstPool14, null);
      arrayList.add(attributeInfo2);
    } 
    AttributeInfo attributeInfo3 = getAttribute("Signature");
    if (attributeInfo3 != null) {
      attributeInfo3 = attributeInfo3.copy(paramConstPool14, null);
      arrayList.add(attributeInfo3);
    } 
    int i = getConstantValue();
    if (i != 0) {
      i = this.constPool.copy(i, paramConstPool14, null);
      arrayList.add(new ConstantAttribute(paramConstPool14, i));
    } 
    this.attribute = arrayList;
    this.name = paramConstPool14.addUtf8Info(getName());
    this.descriptor = paramConstPool14.addUtf8Info(getDescriptor());
    this.constPool = paramConstPool14;
  }
  
  public ConstPool14 getConstPool() {
    return this.constPool;
  }
  
  public String getName() {
    if (this.cachedName == null)
      this.cachedName = this.constPool.getUtf8Info(this.name); 
    return this.cachedName;
  }
  
  public void setName(String paramString) {
    this.name = this.constPool.addUtf8Info(paramString);
    this.cachedName = paramString;
  }
  
  public int getAccessFlags() {
    return this.accessFlags;
  }
  
  public void setAccessFlags(int paramInt) {
    this.accessFlags = paramInt;
  }
  
  public String getDescriptor() {
    return this.constPool.getUtf8Info(this.descriptor);
  }
  
  public void setDescriptor(String paramString) {
    if (!paramString.equals(getDescriptor()))
      this.descriptor = this.constPool.addUtf8Info(paramString); 
  }
  
  public int getConstantValue() {
    if ((this.accessFlags & 0x8) == 0)
      return 0; 
    ConstantAttribute constantAttribute = (ConstantAttribute)getAttribute("ConstantValue");
    if (constantAttribute == null)
      return 0; 
    return constantAttribute.getConstantValue();
  }
  
  public List getAttributes() {
    if (this.attribute == null)
      this.attribute = new ArrayList(); 
    return this.attribute;
  }
  
  public AttributeInfo getAttribute(String paramString) {
    return AttributeInfo.lookup(this.attribute, paramString);
  }
  
  public AttributeInfo removeAttribute(String paramString) {
    return AttributeInfo.remove(this.attribute, paramString);
  }
  
  public void addAttribute(AttributeInfo paramAttributeInfo) {
    if (this.attribute == null)
      this.attribute = new ArrayList(); 
    AttributeInfo.remove(this.attribute, paramAttributeInfo.getName());
    this.attribute.add(paramAttributeInfo);
  }
  
  private void read(DataInputStream paramDataInputStream) throws IOException {
    this.accessFlags = paramDataInputStream.readUnsignedShort();
    this.name = paramDataInputStream.readUnsignedShort();
    this.descriptor = paramDataInputStream.readUnsignedShort();
    int i = paramDataInputStream.readUnsignedShort();
    this.attribute = new ArrayList();
    for (byte b = 0; b < i; b++)
      this.attribute.add(AttributeInfo.read(this.constPool, paramDataInputStream)); 
  }
  
  void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.accessFlags);
    paramDataOutputStream.writeShort(this.name);
    paramDataOutputStream.writeShort(this.descriptor);
    if (this.attribute == null) {
      paramDataOutputStream.writeShort(0);
    } else {
      paramDataOutputStream.writeShort(this.attribute.size());
      AttributeInfo.writeAll(this.attribute, paramDataOutputStream);
    } 
  }
}
