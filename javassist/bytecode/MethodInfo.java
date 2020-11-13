package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javassist.ClassPool;
import javassist.bytecode.stackmap.MapMaker;

public class MethodInfo {
  ConstPool14 constPool;
  
  int accessFlags;
  
  int name;
  
  String cachedName;
  
  int descriptor;
  
  ArrayList attribute;
  
  public static boolean doPreverify = false;
  
  public static final String nameInit = "<init>";
  
  public static final String nameClinit = "<clinit>";
  
  private MethodInfo(ConstPool14 paramConstPool14) {
    this.constPool = paramConstPool14;
    this.attribute = null;
  }
  
  public MethodInfo(ConstPool14 paramConstPool14, String paramString1, String paramString2) {
    this(paramConstPool14);
    this.accessFlags = 0;
    this.name = paramConstPool14.addUtf8Info(paramString1);
    this.cachedName = paramString1;
    this.descriptor = this.constPool.addUtf8Info(paramString2);
  }
  
  MethodInfo(ConstPool14 paramConstPool14, DataInputStream paramDataInputStream) throws IOException {
    this(paramConstPool14);
    read(paramDataInputStream);
  }
  
  public MethodInfo(ConstPool14 paramConstPool14, String paramString, MethodInfo paramMethodInfo, Map paramMap) throws BadBytecode {
    this(paramConstPool14);
    read(paramMethodInfo, paramString, paramMap);
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
    AttributeInfo attributeInfo3 = getAttribute("RuntimeInvisibleParameterAnnotations");
    if (attributeInfo3 != null) {
      attributeInfo3 = attributeInfo3.copy(paramConstPool14, null);
      arrayList.add(attributeInfo3);
    } 
    AttributeInfo attributeInfo4 = getAttribute("RuntimeVisibleParameterAnnotations");
    if (attributeInfo4 != null) {
      attributeInfo4 = attributeInfo4.copy(paramConstPool14, null);
      arrayList.add(attributeInfo4);
    } 
    AnnotationDefaultAttribute annotationDefaultAttribute = (AnnotationDefaultAttribute)getAttribute("AnnotationDefault");
    if (annotationDefaultAttribute != null)
      arrayList.add(annotationDefaultAttribute); 
    ExceptionsAttribute exceptionsAttribute = getExceptionsAttribute();
    if (exceptionsAttribute != null)
      arrayList.add(exceptionsAttribute); 
    AttributeInfo attributeInfo5 = getAttribute("Signature");
    if (attributeInfo5 != null) {
      attributeInfo5 = attributeInfo5.copy(paramConstPool14, null);
      arrayList.add(attributeInfo5);
    } 
    this.attribute = arrayList;
    this.name = paramConstPool14.addUtf8Info(getName());
    this.descriptor = paramConstPool14.addUtf8Info(getDescriptor());
    this.constPool = paramConstPool14;
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
  
  public boolean isMethod() {
    String str = getName();
    return (!str.equals("<init>") && !str.equals("<clinit>"));
  }
  
  public ConstPool14 getConstPool() {
    return this.constPool;
  }
  
  public boolean isConstructor() {
    return getName().equals("<init>");
  }
  
  public boolean isStaticInitializer() {
    return getName().equals("<clinit>");
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
  
  public ExceptionsAttribute getExceptionsAttribute() {
    AttributeInfo attributeInfo = AttributeInfo.lookup(this.attribute, "Exceptions");
    return (ExceptionsAttribute)attributeInfo;
  }
  
  public CodeAttribute getCodeAttribute() {
    AttributeInfo attributeInfo = AttributeInfo.lookup(this.attribute, "Code");
    return (CodeAttribute)attributeInfo;
  }
  
  public void removeExceptionsAttribute() {
    AttributeInfo.remove(this.attribute, "Exceptions");
  }
  
  public void setExceptionsAttribute(ExceptionsAttribute paramExceptionsAttribute) {
    removeExceptionsAttribute();
    if (this.attribute == null)
      this.attribute = new ArrayList(); 
    this.attribute.add(paramExceptionsAttribute);
  }
  
  public void removeCodeAttribute() {
    AttributeInfo.remove(this.attribute, "Code");
  }
  
  public void setCodeAttribute(CodeAttribute paramCodeAttribute) {
    removeCodeAttribute();
    if (this.attribute == null)
      this.attribute = new ArrayList(); 
    this.attribute.add(paramCodeAttribute);
  }
  
  public void rebuildStackMapIf6(ClassPool paramClassPool, ClassFile paramClassFile) throws BadBytecode {
    if (paramClassFile.getMajorVersion() >= 50)
      rebuildStackMap(paramClassPool); 
    if (doPreverify)
      rebuildStackMapForME(paramClassPool); 
  }
  
  public void rebuildStackMap(ClassPool paramClassPool) throws BadBytecode {
    CodeAttribute codeAttribute = getCodeAttribute();
    if (codeAttribute != null) {
      StackMapTable stackMapTable = MapMaker.make(paramClassPool, this);
      codeAttribute.setAttribute(stackMapTable);
    } 
  }
  
  public void rebuildStackMapForME(ClassPool paramClassPool) throws BadBytecode {
    CodeAttribute codeAttribute = getCodeAttribute();
    if (codeAttribute != null) {
      StackMap stackMap = MapMaker.make2(paramClassPool, this);
      codeAttribute.setAttribute(stackMap);
    } 
  }
  
  public int getLineNumber(int paramInt) {
    CodeAttribute codeAttribute = getCodeAttribute();
    if (codeAttribute == null)
      return -1; 
    LineNumberAttribute lineNumberAttribute = (LineNumberAttribute)codeAttribute.getAttribute("LineNumberTable");
    if (lineNumberAttribute == null)
      return -1; 
    return lineNumberAttribute.toLineNumber(paramInt);
  }
  
  public void setSuperclass(String paramString) throws BadBytecode {
    if (!isConstructor())
      return; 
    CodeAttribute codeAttribute = getCodeAttribute();
    byte[] arrayOfByte = codeAttribute.getCode();
    CodeIterator codeIterator = codeAttribute.iterator();
    int i = codeIterator.skipSuperConstructor();
    if (i >= 0) {
      ConstPool14 constPool14 = this.constPool;
      int j = ByteArray.readU16bit(arrayOfByte, i + 1);
      int k = constPool14.getMethodrefNameAndType(j);
      int m = constPool14.addClassInfo(paramString);
      int n = constPool14.addMethodrefInfo(m, k);
      ByteArray.write16bit(n, arrayOfByte, i + 1);
    } 
  }
  
  private void read(MethodInfo paramMethodInfo, String paramString, Map paramMap) throws BadBytecode {
    ConstPool14 constPool141 = this.constPool;
    this.accessFlags = paramMethodInfo.accessFlags;
    this.name = constPool141.addUtf8Info(paramString);
    this.cachedName = paramString;
    ConstPool14 constPool142 = paramMethodInfo.constPool;
    String str1 = constPool142.getUtf8Info(paramMethodInfo.descriptor);
    String str2 = Descriptor.rename(str1, paramMap);
    this.descriptor = constPool141.addUtf8Info(str2);
    this.attribute = new ArrayList();
    ExceptionsAttribute exceptionsAttribute = paramMethodInfo.getExceptionsAttribute();
    if (exceptionsAttribute != null)
      this.attribute.add(exceptionsAttribute.copy(constPool141, paramMap)); 
    CodeAttribute codeAttribute = paramMethodInfo.getCodeAttribute();
    if (codeAttribute != null)
      this.attribute.add(codeAttribute.copy(constPool141, paramMap)); 
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
