package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class AttributeInfo {
  protected ConstPool14 constPool;
  
  int name;
  
  byte[] info;
  
  protected AttributeInfo(ConstPool14 paramConstPool14, int paramInt, byte[] paramArrayOfbyte) {
    this.constPool = paramConstPool14;
    this.name = paramInt;
    this.info = paramArrayOfbyte;
  }
  
  protected AttributeInfo(ConstPool14 paramConstPool14, String paramString) {
    this(paramConstPool14, paramString, (byte[])null);
  }
  
  public AttributeInfo(ConstPool14 paramConstPool14, String paramString, byte[] paramArrayOfbyte) {
    this(paramConstPool14, paramConstPool14.addUtf8Info(paramString), paramArrayOfbyte);
  }
  
  protected AttributeInfo(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    this.constPool = paramConstPool14;
    this.name = paramInt;
    int i = paramDataInputStream.readInt();
    this.info = new byte[i];
    if (i > 0)
      paramDataInputStream.readFully(this.info); 
  }
  
  static AttributeInfo read(ConstPool14 paramConstPool14, DataInputStream paramDataInputStream) throws IOException {
    int i = paramDataInputStream.readUnsignedShort();
    String str = paramConstPool14.getUtf8Info(i);
    char c = str.charAt(0);
    if (c < 'M') {
      if (c < 'E') {
        if (str.equals("AnnotationDefault"))
          return new AnnotationDefaultAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("BootstrapMethods"))
          return new BootstrapMethodsAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("Code"))
          return new CodeAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("ConstantValue"))
          return new ConstantAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("Deprecated"))
          return new DeprecatedAttribute(paramConstPool14, i, paramDataInputStream); 
      } else {
        if (str.equals("EnclosingMethod"))
          return new EnclosingMethodAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("Exceptions"))
          return new ExceptionsAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("InnerClasses"))
          return new InnerClassesAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("LineNumberTable"))
          return new LineNumberAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("LocalVariableTable"))
          return new LocalVariableAttribute(paramConstPool14, i, paramDataInputStream); 
        if (str.equals("LocalVariableTypeTable"))
          return new LocalVariableTypeAttribute(paramConstPool14, i, paramDataInputStream); 
      } 
    } else if (c < 'S') {
      if (str.equals("MethodParameters"))
        return new MethodParametersAttribute(paramConstPool14, i, paramDataInputStream); 
      if (str.equals("RuntimeVisibleAnnotations") || str
        .equals("RuntimeInvisibleAnnotations"))
        return new AnnotationsAttribute(paramConstPool14, i, paramDataInputStream); 
      if (str.equals("RuntimeVisibleParameterAnnotations") || str
        .equals("RuntimeInvisibleParameterAnnotations"))
        return new ParameterAnnotationsAttribute(paramConstPool14, i, paramDataInputStream); 
      if (str.equals("RuntimeVisibleTypeAnnotations") || str
        .equals("RuntimeInvisibleTypeAnnotations"))
        return new TypeAnnotationsAttribute(paramConstPool14, i, paramDataInputStream); 
    } else {
      if (str.equals("Signature"))
        return new SignatureAttribute(paramConstPool14, i, paramDataInputStream); 
      if (str.equals("SourceFile"))
        return new SourceFileAttribute(paramConstPool14, i, paramDataInputStream); 
      if (str.equals("Synthetic"))
        return new SyntheticAttribute(paramConstPool14, i, paramDataInputStream); 
      if (str.equals("StackMap"))
        return new StackMap(paramConstPool14, i, paramDataInputStream); 
      if (str.equals("StackMapTable"))
        return new StackMapTable(paramConstPool14, i, paramDataInputStream); 
    } 
    return new AttributeInfo(paramConstPool14, i, paramDataInputStream);
  }
  
  public String getName() {
    return this.constPool.getUtf8Info(this.name);
  }
  
  public ConstPool14 getConstPool() {
    return this.constPool;
  }
  
  public int length() {
    return this.info.length + 6;
  }
  
  public byte[] get() {
    return this.info;
  }
  
  public void set(byte[] paramArrayOfbyte) {
    this.info = paramArrayOfbyte;
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    int i = this.info.length;
    byte[] arrayOfByte1 = this.info;
    byte[] arrayOfByte2 = new byte[i];
    for (byte b = 0; b < i; b++)
      arrayOfByte2[b] = arrayOfByte1[b]; 
    return new AttributeInfo(paramConstPool14, getName(), arrayOfByte2);
  }
  
  void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeShort(this.name);
    paramDataOutputStream.writeInt(this.info.length);
    if (this.info.length > 0)
      paramDataOutputStream.write(this.info); 
  }
  
  static int getLength(ArrayList<AttributeInfo> paramArrayList) {
    int i = 0;
    int j = paramArrayList.size();
    for (byte b = 0; b < j; b++) {
      AttributeInfo attributeInfo = paramArrayList.get(b);
      i += attributeInfo.length();
    } 
    return i;
  }
  
  static AttributeInfo lookup(ArrayList paramArrayList, String paramString) {
    if (paramArrayList == null)
      return null; 
    ListIterator<AttributeInfo> listIterator = paramArrayList.listIterator();
    while (listIterator.hasNext()) {
      AttributeInfo attributeInfo = listIterator.next();
      if (attributeInfo.getName().equals(paramString))
        return attributeInfo; 
    } 
    return null;
  }
  
  static synchronized AttributeInfo remove(ArrayList paramArrayList, String paramString) {
    if (paramArrayList == null)
      return null; 
    AttributeInfo attributeInfo = null;
    ListIterator<AttributeInfo> listIterator = paramArrayList.listIterator();
    while (listIterator.hasNext()) {
      AttributeInfo attributeInfo1 = listIterator.next();
      if (attributeInfo1.getName().equals(paramString)) {
        listIterator.remove();
        attributeInfo = attributeInfo1;
      } 
    } 
    return attributeInfo;
  }
  
  static void writeAll(ArrayList<AttributeInfo> paramArrayList, DataOutputStream paramDataOutputStream) throws IOException {
    if (paramArrayList == null)
      return; 
    int i = paramArrayList.size();
    for (byte b = 0; b < i; b++) {
      AttributeInfo attributeInfo = paramArrayList.get(b);
      attributeInfo.write(paramDataOutputStream);
    } 
  }
  
  static ArrayList copyAll(ArrayList<AttributeInfo> paramArrayList, ConstPool14 paramConstPool14) {
    if (paramArrayList == null)
      return null; 
    ArrayList<AttributeInfo> arrayList = new ArrayList();
    int i = paramArrayList.size();
    for (byte b = 0; b < i; b++) {
      AttributeInfo attributeInfo = paramArrayList.get(b);
      arrayList.add(attributeInfo.copy(paramConstPool14, null));
    } 
    return arrayList;
  }
  
  void renameClass(String paramString1, String paramString2) {}
  
  void renameClass(Map paramMap) {}
  
  static void renameClass(List paramList, String paramString1, String paramString2) {
    Iterator<AttributeInfo> iterator = paramList.iterator();
    while (iterator.hasNext()) {
      AttributeInfo attributeInfo = iterator.next();
      attributeInfo.renameClass(paramString1, paramString2);
    } 
  }
  
  static void renameClass(List paramList, Map paramMap) {
    Iterator<AttributeInfo> iterator = paramList.iterator();
    while (iterator.hasNext()) {
      AttributeInfo attributeInfo = iterator.next();
      attributeInfo.renameClass(paramMap);
    } 
  }
  
  void getRefClasses(Map paramMap) {}
  
  static void getRefClasses(List paramList, Map paramMap) {
    Iterator<AttributeInfo> iterator = paramList.iterator();
    while (iterator.hasNext()) {
      AttributeInfo attributeInfo = iterator.next();
      attributeInfo.getRefClasses(paramMap);
    } 
  }
}
