package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javassist.CannotCompileException;

public final class ClassFile {
  int major;
  
  int minor;
  
  ConstPool14 constPool;
  
  int thisClass;
  
  int accessFlags;
  
  int superClass;
  
  int[] interfaces;
  
  ArrayList fields;
  
  ArrayList methods;
  
  ArrayList attributes;
  
  String thisclassname;
  
  String[] cachedInterfaces;
  
  String cachedSuperclass;
  
  public static final int JAVA_1 = 45;
  
  public static final int JAVA_2 = 46;
  
  public static final int JAVA_3 = 47;
  
  public static final int JAVA_4 = 48;
  
  public static final int JAVA_5 = 49;
  
  public static final int JAVA_6 = 50;
  
  public static final int JAVA_7 = 51;
  
  public static final int JAVA_8 = 52;
  
  public static final int MAJOR_VERSION;
  
  static {
    byte b = 47;
    try {
      Class.forName("java.lang.StringBuilder");
      b = 49;
      Class.forName("java.util.zip.DeflaterInputStream");
      b = 50;
      Class.forName("java.lang.invoke.CallSite");
      b = 51;
      Class.forName("java.util.function.Function");
      b = 52;
    } catch (Throwable throwable) {}
    MAJOR_VERSION = b;
  }
  
  public ClassFile(DataInputStream paramDataInputStream) throws IOException {
    read(paramDataInputStream);
  }
  
  public ClassFile(boolean paramBoolean, String paramString1, String paramString2) {
    this.major = MAJOR_VERSION;
    this.minor = 0;
    this.constPool = new ConstPool14(paramString1);
    this.thisClass = this.constPool.getThisClassInfo();
    if (paramBoolean) {
      this.accessFlags = 1536;
    } else {
      this.accessFlags = 32;
    } 
    initSuperclass(paramString2);
    this.interfaces = null;
    this.fields = new ArrayList();
    this.methods = new ArrayList();
    this.thisclassname = paramString1;
    this.attributes = new ArrayList();
    this.attributes.add(new SourceFileAttribute(this.constPool, 
          getSourcefileName(this.thisclassname)));
  }
  
  private void initSuperclass(String paramString) {
    if (paramString != null) {
      this.superClass = this.constPool.addClassInfo(paramString);
      this.cachedSuperclass = paramString;
    } else {
      this.superClass = this.constPool.addClassInfo("java.lang.Object");
      this.cachedSuperclass = "java.lang.Object";
    } 
  }
  
  private static String getSourcefileName(String paramString) {
    int i = paramString.lastIndexOf('.');
    if (i >= 0)
      paramString = paramString.substring(i + 1); 
    return paramString + ".java";
  }
  
  public void compact() {
    ConstPool14 constPool14 = compact0();
    ArrayList<MethodInfo> arrayList = this.methods;
    int i = arrayList.size();
    byte b;
    for (b = 0; b < i; b++) {
      MethodInfo methodInfo = arrayList.get(b);
      methodInfo.compact(constPool14);
    } 
    arrayList = this.fields;
    i = arrayList.size();
    for (b = 0; b < i; b++) {
      FieldInfo fieldInfo = (FieldInfo)arrayList.get(b);
      fieldInfo.compact(constPool14);
    } 
    this.attributes = AttributeInfo.copyAll(this.attributes, constPool14);
    this.constPool = constPool14;
  }
  
  private ConstPool14 compact0() {
    ConstPool14 constPool14 = new ConstPool14(this.thisclassname);
    this.thisClass = constPool14.getThisClassInfo();
    String str = getSuperclass();
    if (str != null)
      this.superClass = constPool14.addClassInfo(getSuperclass()); 
    if (this.interfaces != null) {
      int i = this.interfaces.length;
      for (byte b = 0; b < i; b++)
        this.interfaces[b] = constPool14
          .addClassInfo(this.constPool.getClassInfo(this.interfaces[b])); 
    } 
    return constPool14;
  }
  
  public void prune() {
    ConstPool14 constPool14 = compact0();
    ArrayList<AttributeInfo> arrayList = new ArrayList();
    AttributeInfo attributeInfo1 = getAttribute("RuntimeInvisibleAnnotations");
    if (attributeInfo1 != null) {
      attributeInfo1 = attributeInfo1.copy(constPool14, null);
      arrayList.add(attributeInfo1);
    } 
    AttributeInfo attributeInfo2 = getAttribute("RuntimeVisibleAnnotations");
    if (attributeInfo2 != null) {
      attributeInfo2 = attributeInfo2.copy(constPool14, null);
      arrayList.add(attributeInfo2);
    } 
    AttributeInfo attributeInfo3 = getAttribute("Signature");
    if (attributeInfo3 != null) {
      attributeInfo3 = attributeInfo3.copy(constPool14, null);
      arrayList.add(attributeInfo3);
    } 
    ArrayList<MethodInfo> arrayList1 = this.methods;
    int i = arrayList1.size();
    byte b;
    for (b = 0; b < i; b++) {
      MethodInfo methodInfo = arrayList1.get(b);
      methodInfo.prune(constPool14);
    } 
    arrayList1 = this.fields;
    i = arrayList1.size();
    for (b = 0; b < i; b++) {
      FieldInfo fieldInfo = (FieldInfo)arrayList1.get(b);
      fieldInfo.prune(constPool14);
    } 
    this.attributes = arrayList;
    this.constPool = constPool14;
  }
  
  public ConstPool14 getConstPool() {
    return this.constPool;
  }
  
  public boolean isInterface() {
    return ((this.accessFlags & 0x200) != 0);
  }
  
  public boolean isFinal() {
    return ((this.accessFlags & 0x10) != 0);
  }
  
  public boolean isAbstract() {
    return ((this.accessFlags & 0x400) != 0);
  }
  
  public int getAccessFlags() {
    return this.accessFlags;
  }
  
  public void setAccessFlags(int paramInt) {
    if ((paramInt & 0x200) == 0)
      paramInt |= 0x20; 
    this.accessFlags = paramInt;
  }
  
  public int getInnerAccessFlags() {
    InnerClassesAttribute innerClassesAttribute = (InnerClassesAttribute)getAttribute("InnerClasses");
    if (innerClassesAttribute == null)
      return -1; 
    String str = getName();
    int i = innerClassesAttribute.tableLength();
    for (byte b = 0; b < i; b++) {
      if (str.equals(innerClassesAttribute.innerClass(b)))
        return innerClassesAttribute.accessFlags(b); 
    } 
    return -1;
  }
  
  public String getName() {
    return this.thisclassname;
  }
  
  public void setName(String paramString) {
    renameClass(this.thisclassname, paramString);
  }
  
  public String getSuperclass() {
    if (this.cachedSuperclass == null)
      this.cachedSuperclass = this.constPool.getClassInfo(this.superClass); 
    return this.cachedSuperclass;
  }
  
  public int getSuperclassId() {
    return this.superClass;
  }
  
  public void setSuperclass(String paramString) throws CannotCompileException {
    if (paramString == null)
      paramString = "java.lang.Object"; 
    try {
      this.superClass = this.constPool.addClassInfo(paramString);
      ArrayList<MethodInfo> arrayList = this.methods;
      int i = arrayList.size();
      for (byte b = 0; b < i; b++) {
        MethodInfo methodInfo = arrayList.get(b);
        methodInfo.setSuperclass(paramString);
      } 
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
    this.cachedSuperclass = paramString;
  }
  
  public final void renameClass(String paramString1, String paramString2) {
    if (paramString1.equals(paramString2))
      return; 
    if (paramString1.equals(this.thisclassname))
      this.thisclassname = paramString2; 
    paramString1 = Descriptor.toJvmName(paramString1);
    paramString2 = Descriptor.toJvmName(paramString2);
    this.constPool.renameClass(paramString1, paramString2);
    AttributeInfo.renameClass(this.attributes, paramString1, paramString2);
    ArrayList<MethodInfo> arrayList = this.methods;
    int i = arrayList.size();
    byte b;
    for (b = 0; b < i; b++) {
      MethodInfo methodInfo = arrayList.get(b);
      String str = methodInfo.getDescriptor();
      methodInfo.setDescriptor(Descriptor.rename(str, paramString1, paramString2));
      AttributeInfo.renameClass(methodInfo.getAttributes(), paramString1, paramString2);
    } 
    arrayList = this.fields;
    i = arrayList.size();
    for (b = 0; b < i; b++) {
      FieldInfo fieldInfo = (FieldInfo)arrayList.get(b);
      String str = fieldInfo.getDescriptor();
      fieldInfo.setDescriptor(Descriptor.rename(str, paramString1, paramString2));
      AttributeInfo.renameClass(fieldInfo.getAttributes(), paramString1, paramString2);
    } 
  }
  
  public final void renameClass(Map paramMap) {
    String str = (String)paramMap.get(
        Descriptor.toJvmName(this.thisclassname));
    if (str != null)
      this.thisclassname = Descriptor.toJavaName(str); 
    this.constPool.renameClass(paramMap);
    AttributeInfo.renameClass(this.attributes, paramMap);
    ArrayList<MethodInfo> arrayList = this.methods;
    int i = arrayList.size();
    byte b;
    for (b = 0; b < i; b++) {
      MethodInfo methodInfo = arrayList.get(b);
      String str1 = methodInfo.getDescriptor();
      methodInfo.setDescriptor(Descriptor.rename(str1, paramMap));
      AttributeInfo.renameClass(methodInfo.getAttributes(), paramMap);
    } 
    arrayList = this.fields;
    i = arrayList.size();
    for (b = 0; b < i; b++) {
      FieldInfo fieldInfo = (FieldInfo)arrayList.get(b);
      String str1 = fieldInfo.getDescriptor();
      fieldInfo.setDescriptor(Descriptor.rename(str1, paramMap));
      AttributeInfo.renameClass(fieldInfo.getAttributes(), paramMap);
    } 
  }
  
  public final void getRefClasses(Map paramMap) {
    this.constPool.renameClass(paramMap);
    AttributeInfo.getRefClasses(this.attributes, paramMap);
    ArrayList<MethodInfo> arrayList = this.methods;
    int i = arrayList.size();
    byte b;
    for (b = 0; b < i; b++) {
      MethodInfo methodInfo = arrayList.get(b);
      String str = methodInfo.getDescriptor();
      Descriptor.rename(str, paramMap);
      AttributeInfo.getRefClasses(methodInfo.getAttributes(), paramMap);
    } 
    arrayList = this.fields;
    i = arrayList.size();
    for (b = 0; b < i; b++) {
      FieldInfo fieldInfo = (FieldInfo)arrayList.get(b);
      String str = fieldInfo.getDescriptor();
      Descriptor.rename(str, paramMap);
      AttributeInfo.getRefClasses(fieldInfo.getAttributes(), paramMap);
    } 
  }
  
  public String[] getInterfaces() {
    if (this.cachedInterfaces != null)
      return this.cachedInterfaces; 
    String[] arrayOfString = null;
    if (this.interfaces == null) {
      arrayOfString = new String[0];
    } else {
      int i = this.interfaces.length;
      String[] arrayOfString1 = new String[i];
      for (byte b = 0; b < i; b++)
        arrayOfString1[b] = this.constPool.getClassInfo(this.interfaces[b]); 
      arrayOfString = arrayOfString1;
    } 
    this.cachedInterfaces = arrayOfString;
    return arrayOfString;
  }
  
  public void setInterfaces(String[] paramArrayOfString) {
    this.cachedInterfaces = null;
    if (paramArrayOfString != null) {
      int i = paramArrayOfString.length;
      this.interfaces = new int[i];
      for (byte b = 0; b < i; b++)
        this.interfaces[b] = this.constPool.addClassInfo(paramArrayOfString[b]); 
    } 
  }
  
  public void addInterface(String paramString) {
    this.cachedInterfaces = null;
    int i = this.constPool.addClassInfo(paramString);
    if (this.interfaces == null) {
      this.interfaces = new int[1];
      this.interfaces[0] = i;
    } else {
      int j = this.interfaces.length;
      int[] arrayOfInt = new int[j + 1];
      System.arraycopy(this.interfaces, 0, arrayOfInt, 0, j);
      arrayOfInt[j] = i;
      this.interfaces = arrayOfInt;
    } 
  }
  
  public List getFields() {
    return this.fields;
  }
  
  public void addField(FieldInfo paramFieldInfo) throws DuplicateMemberException {
    testExistingField(paramFieldInfo.getName(), paramFieldInfo.getDescriptor());
    this.fields.add(paramFieldInfo);
  }
  
  public final void addField2(FieldInfo paramFieldInfo) {
    this.fields.add(paramFieldInfo);
  }
  
  private void testExistingField(String paramString1, String paramString2) throws DuplicateMemberException {
    ListIterator<FieldInfo> listIterator = this.fields.listIterator(0);
    while (listIterator.hasNext()) {
      FieldInfo fieldInfo = listIterator.next();
      if (fieldInfo.getName().equals(paramString1))
        throw new DuplicateMemberException("duplicate field: " + paramString1); 
    } 
  }
  
  public List getMethods() {
    return this.methods;
  }
  
  public MethodInfo getMethod(String paramString) {
    ArrayList<MethodInfo> arrayList = this.methods;
    int i = arrayList.size();
    for (byte b = 0; b < i; b++) {
      MethodInfo methodInfo = arrayList.get(b);
      if (methodInfo.getName().equals(paramString))
        return methodInfo; 
    } 
    return null;
  }
  
  public MethodInfo getStaticInitializer() {
    return getMethod("<clinit>");
  }
  
  public void addMethod(MethodInfo paramMethodInfo) throws DuplicateMemberException {
    testExistingMethod(paramMethodInfo);
    this.methods.add(paramMethodInfo);
  }
  
  public final void addMethod2(MethodInfo paramMethodInfo) {
    this.methods.add(paramMethodInfo);
  }
  
  private void testExistingMethod(MethodInfo paramMethodInfo) throws DuplicateMemberException {
    String str1 = paramMethodInfo.getName();
    String str2 = paramMethodInfo.getDescriptor();
    ListIterator<MethodInfo> listIterator = this.methods.listIterator(0);
    while (listIterator.hasNext()) {
      if (isDuplicated(paramMethodInfo, str1, str2, listIterator.next(), listIterator))
        throw new DuplicateMemberException("duplicate method: " + str1 + " in " + 
            getName()); 
    } 
  }
  
  private static boolean isDuplicated(MethodInfo paramMethodInfo1, String paramString1, String paramString2, MethodInfo paramMethodInfo2, ListIterator paramListIterator) {
    if (!paramMethodInfo2.getName().equals(paramString1))
      return false; 
    String str = paramMethodInfo2.getDescriptor();
    if (!Descriptor.eqParamTypes(str, paramString2))
      return false; 
    if (str.equals(paramString2)) {
      if (notBridgeMethod(paramMethodInfo2))
        return true; 
      paramListIterator.remove();
      return false;
    } 
    return false;
  }
  
  private static boolean notBridgeMethod(MethodInfo paramMethodInfo) {
    return ((paramMethodInfo.getAccessFlags() & 0x40) == 0);
  }
  
  public List getAttributes() {
    return this.attributes;
  }
  
  public AttributeInfo getAttribute(String paramString) {
    ArrayList<AttributeInfo> arrayList = this.attributes;
    int i = arrayList.size();
    for (byte b = 0; b < i; b++) {
      AttributeInfo attributeInfo = arrayList.get(b);
      if (attributeInfo.getName().equals(paramString))
        return attributeInfo; 
    } 
    return null;
  }
  
  public AttributeInfo removeAttribute(String paramString) {
    return AttributeInfo.remove(this.attributes, paramString);
  }
  
  public void addAttribute(AttributeInfo paramAttributeInfo) {
    AttributeInfo.remove(this.attributes, paramAttributeInfo.getName());
    this.attributes.add(paramAttributeInfo);
  }
  
  public String getSourceFile() {
    SourceFileAttribute sourceFileAttribute = (SourceFileAttribute)getAttribute("SourceFile");
    if (sourceFileAttribute == null)
      return null; 
    return sourceFileAttribute.getFileName();
  }
  
  private void read(DataInputStream paramDataInputStream) throws IOException {
    int j = paramDataInputStream.readInt();
    if (j != -889275714)
      throw new IOException("bad magic number: " + Integer.toHexString(j)); 
    this.minor = paramDataInputStream.readUnsignedShort();
    this.major = paramDataInputStream.readUnsignedShort();
    this.constPool = new ConstPool14(paramDataInputStream);
    this.accessFlags = paramDataInputStream.readUnsignedShort();
    this.thisClass = paramDataInputStream.readUnsignedShort();
    this.constPool.setThisClassInfo(this.thisClass);
    this.superClass = paramDataInputStream.readUnsignedShort();
    int i = paramDataInputStream.readUnsignedShort();
    if (i == 0) {
      this.interfaces = null;
    } else {
      this.interfaces = new int[i];
      for (byte b1 = 0; b1 < i; b1++)
        this.interfaces[b1] = paramDataInputStream.readUnsignedShort(); 
    } 
    ConstPool14 constPool14 = this.constPool;
    i = paramDataInputStream.readUnsignedShort();
    this.fields = new ArrayList();
    byte b;
    for (b = 0; b < i; b++)
      addField2(new FieldInfo(constPool14, paramDataInputStream)); 
    i = paramDataInputStream.readUnsignedShort();
    this.methods = new ArrayList();
    for (b = 0; b < i; b++)
      addMethod2(new MethodInfo(constPool14, paramDataInputStream)); 
    this.attributes = new ArrayList();
    i = paramDataInputStream.readUnsignedShort();
    for (b = 0; b < i; b++)
      addAttribute(AttributeInfo.read(constPool14, paramDataInputStream)); 
    this.thisclassname = this.constPool.getClassInfo(this.thisClass);
  }
  
  public void write(DataOutputStream paramDataOutputStream) throws IOException {
    paramDataOutputStream.writeInt(-889275714);
    paramDataOutputStream.writeShort(this.minor);
    paramDataOutputStream.writeShort(this.major);
    this.constPool.write(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.accessFlags);
    paramDataOutputStream.writeShort(this.thisClass);
    paramDataOutputStream.writeShort(this.superClass);
    if (this.interfaces == null) {
      i = 0;
    } else {
      i = this.interfaces.length;
    } 
    paramDataOutputStream.writeShort(i);
    byte b;
    for (b = 0; b < i; b++)
      paramDataOutputStream.writeShort(this.interfaces[b]); 
    ArrayList<FieldInfo> arrayList = this.fields;
    int i = arrayList.size();
    paramDataOutputStream.writeShort(i);
    for (b = 0; b < i; b++) {
      FieldInfo fieldInfo = arrayList.get(b);
      fieldInfo.write(paramDataOutputStream);
    } 
    arrayList = this.methods;
    i = arrayList.size();
    paramDataOutputStream.writeShort(i);
    for (b = 0; b < i; b++) {
      MethodInfo methodInfo = (MethodInfo)arrayList.get(b);
      methodInfo.write(paramDataOutputStream);
    } 
    paramDataOutputStream.writeShort(this.attributes.size());
    AttributeInfo.writeAll(this.attributes, paramDataOutputStream);
  }
  
  public int getMajorVersion() {
    return this.major;
  }
  
  public void setMajorVersion(int paramInt) {
    this.major = paramInt;
  }
  
  public int getMinorVersion() {
    return this.minor;
  }
  
  public void setMinorVersion(int paramInt) {
    this.minor = paramInt;
  }
  
  public void setVersionToJava5() {
    this.major = 49;
    this.minor = 0;
  }
}
