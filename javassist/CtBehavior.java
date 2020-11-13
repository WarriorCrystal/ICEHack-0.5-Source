package javassist;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.LocalVariableTypeAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.StackMap;
import javassist.bytecode.StackMapTable;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.expr.ExprEditor;

public abstract class CtBehavior extends CtMember {
  protected MethodInfo methodInfo;
  
  protected CtBehavior(CtClass paramCtClass, MethodInfo paramMethodInfo) {
    super(paramCtClass);
    this.methodInfo = paramMethodInfo;
  }
  
  void copy(CtBehavior paramCtBehavior, boolean paramBoolean, ClassMap paramClassMap) throws CannotCompileException {
    CtClass ctClass1 = this.declaringClass;
    MethodInfo methodInfo = paramCtBehavior.methodInfo;
    CtClass ctClass2 = paramCtBehavior.getDeclaringClass();
    ConstPool14 constPool14 = ctClass1.getClassFile2().getConstPool();
    paramClassMap = new ClassMap(paramClassMap);
    paramClassMap.put(ctClass2.getName(), ctClass1.getName());
    try {
      boolean bool = false;
      CtClass ctClass3 = ctClass2.getSuperclass();
      CtClass ctClass4 = ctClass1.getSuperclass();
      String str = null;
      if (ctClass3 != null && ctClass4 != null) {
        String str1 = ctClass3.getName();
        str = ctClass4.getName();
        if (!str1.equals(str))
          if (str1.equals("java.lang.Object")) {
            bool = true;
          } else {
            paramClassMap.putIfNone(str1, str);
          }  
      } 
      this.methodInfo = new MethodInfo(constPool14, methodInfo.getName(), methodInfo, paramClassMap);
      if (paramBoolean && bool)
        this.methodInfo.setSuperclass(str); 
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  protected void extendToString(StringBuffer paramStringBuffer) {
    paramStringBuffer.append(' ');
    paramStringBuffer.append(getName());
    paramStringBuffer.append(' ');
    paramStringBuffer.append(this.methodInfo.getDescriptor());
  }
  
  public abstract String getLongName();
  
  public MethodInfo getMethodInfo() {
    this.declaringClass.checkModify();
    return this.methodInfo;
  }
  
  public MethodInfo getMethodInfo2() {
    return this.methodInfo;
  }
  
  public int getModifiers() {
    return AccessFlag.toModifier(this.methodInfo.getAccessFlags());
  }
  
  public void setModifiers(int paramInt) {
    this.declaringClass.checkModify();
    this.methodInfo.setAccessFlags(AccessFlag.of(paramInt));
  }
  
  public boolean hasAnnotation(String paramString) {
    MethodInfo methodInfo = getMethodInfo2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)methodInfo.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)methodInfo.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType1.hasAnnotationType(paramString, 
        getDeclaringClass().getClassPool(), annotationsAttribute1, annotationsAttribute2);
  }
  
  public Object getAnnotation(Class paramClass) throws ClassNotFoundException {
    MethodInfo methodInfo = getMethodInfo2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)methodInfo.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)methodInfo.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType1.getAnnotationType(paramClass, 
        getDeclaringClass().getClassPool(), annotationsAttribute1, annotationsAttribute2);
  }
  
  public Object[] getAnnotations() throws ClassNotFoundException {
    return getAnnotations(false);
  }
  
  public Object[] getAvailableAnnotations() {
    try {
      return getAnnotations(true);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException("Unexpected exception", classNotFoundException);
    } 
  }
  
  private Object[] getAnnotations(boolean paramBoolean) throws ClassNotFoundException {
    MethodInfo methodInfo = getMethodInfo2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)methodInfo.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)methodInfo.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType1.toAnnotationType(paramBoolean, 
        getDeclaringClass().getClassPool(), annotationsAttribute1, annotationsAttribute2);
  }
  
  public Object[][] getParameterAnnotations() throws ClassNotFoundException {
    return getParameterAnnotations(false);
  }
  
  public Object[][] getAvailableParameterAnnotations() {
    try {
      return getParameterAnnotations(true);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException("Unexpected exception", classNotFoundException);
    } 
  }
  
  Object[][] getParameterAnnotations(boolean paramBoolean) throws ClassNotFoundException {
    MethodInfo methodInfo = getMethodInfo2();
    ParameterAnnotationsAttribute parameterAnnotationsAttribute1 = (ParameterAnnotationsAttribute)methodInfo.getAttribute("RuntimeInvisibleParameterAnnotations");
    ParameterAnnotationsAttribute parameterAnnotationsAttribute2 = (ParameterAnnotationsAttribute)methodInfo.getAttribute("RuntimeVisibleParameterAnnotations");
    return CtClassType1.toAnnotationType(paramBoolean, 
        getDeclaringClass().getClassPool(), parameterAnnotationsAttribute1, parameterAnnotationsAttribute2, methodInfo);
  }
  
  public CtClass[] getParameterTypes() throws NotFoundException {
    return Descriptor.getParameterTypes(this.methodInfo.getDescriptor(), this.declaringClass
        .getClassPool());
  }
  
  CtClass getReturnType0() throws NotFoundException {
    return Descriptor.getReturnType(this.methodInfo.getDescriptor(), this.declaringClass
        .getClassPool());
  }
  
  public String getSignature() {
    return this.methodInfo.getDescriptor();
  }
  
  public String getGenericSignature() {
    SignatureAttribute signatureAttribute = (SignatureAttribute)this.methodInfo.getAttribute("Signature");
    return (signatureAttribute == null) ? null : signatureAttribute.getSignature();
  }
  
  public void setGenericSignature(String paramString) {
    this.declaringClass.checkModify();
    this.methodInfo.addAttribute((AttributeInfo)new SignatureAttribute(this.methodInfo.getConstPool(), paramString));
  }
  
  public CtClass[] getExceptionTypes() throws NotFoundException {
    String[] arrayOfString;
    ExceptionsAttribute exceptionsAttribute = this.methodInfo.getExceptionsAttribute();
    if (exceptionsAttribute == null) {
      arrayOfString = null;
    } else {
      arrayOfString = exceptionsAttribute.getExceptions();
    } 
    return this.declaringClass.getClassPool().get(arrayOfString);
  }
  
  public void setExceptionTypes(CtClass[] paramArrayOfCtClass) throws NotFoundException {
    this.declaringClass.checkModify();
    if (paramArrayOfCtClass == null || paramArrayOfCtClass.length == 0) {
      this.methodInfo.removeExceptionsAttribute();
      return;
    } 
    String[] arrayOfString = new String[paramArrayOfCtClass.length];
    for (byte b = 0; b < paramArrayOfCtClass.length; b++)
      arrayOfString[b] = paramArrayOfCtClass[b].getName(); 
    ExceptionsAttribute exceptionsAttribute = this.methodInfo.getExceptionsAttribute();
    if (exceptionsAttribute == null) {
      exceptionsAttribute = new ExceptionsAttribute(this.methodInfo.getConstPool());
      this.methodInfo.setExceptionsAttribute(exceptionsAttribute);
    } 
    exceptionsAttribute.setExceptions(arrayOfString);
  }
  
  public abstract boolean isEmpty();
  
  public void setBody(String paramString) throws CannotCompileException {
    setBody(paramString, (String)null, (String)null);
  }
  
  public void setBody(String paramString1, String paramString2, String paramString3) throws CannotCompileException {
    CtClass ctClass = this.declaringClass;
    ctClass.checkModify();
    try {
      Javac javac = new Javac(ctClass);
      if (paramString3 != null)
        javac.recordProceed(paramString2, paramString3); 
      Bytecode1 bytecode1 = javac.compileBody(this, paramString1);
      this.methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
      this.methodInfo.setAccessFlags(this.methodInfo.getAccessFlags() & 0xFFFFFBFF);
      this.methodInfo.rebuildStackMapIf6(ctClass.getClassPool(), ctClass.getClassFile2());
      this.declaringClass.rebuildClassFile();
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  static void setBody0(CtClass paramCtClass1, MethodInfo paramMethodInfo1, CtClass paramCtClass2, MethodInfo paramMethodInfo2, ClassMap paramClassMap) throws CannotCompileException {
    paramCtClass2.checkModify();
    paramClassMap = new ClassMap(paramClassMap);
    paramClassMap.put(paramCtClass1.getName(), paramCtClass2.getName());
    try {
      CodeAttribute codeAttribute = paramMethodInfo1.getCodeAttribute();
      if (codeAttribute != null) {
        ConstPool14 constPool14 = paramMethodInfo2.getConstPool();
        CodeAttribute codeAttribute1 = (CodeAttribute)codeAttribute.copy(constPool14, paramClassMap);
        paramMethodInfo2.setCodeAttribute(codeAttribute1);
      } 
    } catch (javassist.bytecode.CodeAttribute.RuntimeCopyException runtimeCopyException) {
      throw new CannotCompileException(runtimeCopyException);
    } 
    paramMethodInfo2.setAccessFlags(paramMethodInfo2.getAccessFlags() & 0xFFFFFBFF);
    paramCtClass2.rebuildClassFile();
  }
  
  public byte[] getAttribute(String paramString) {
    AttributeInfo attributeInfo = this.methodInfo.getAttribute(paramString);
    if (attributeInfo == null)
      return null; 
    return attributeInfo.get();
  }
  
  public void setAttribute(String paramString, byte[] paramArrayOfbyte) {
    this.declaringClass.checkModify();
    this.methodInfo.addAttribute(new AttributeInfo(this.methodInfo.getConstPool(), paramString, paramArrayOfbyte));
  }
  
  public void useCflow(String paramString) throws CannotCompileException {
    String str;
    CtClass ctClass = this.declaringClass;
    ctClass.checkModify();
    ClassPool classPool = ctClass.getClassPool();
    byte b = 0;
    while (true) {
      str = "_cflow$" + b++;
      try {
        ctClass.getDeclaredField(str);
      } catch (NotFoundException notFoundException) {
        classPool.recordCflow(paramString, this.declaringClass.getName(), str);
        break;
      } 
    } 
    try {
      CtClass ctClass1 = classPool.get("javassist.runtime.Cflow");
      CtField ctField = new CtField(ctClass1, str, ctClass);
      ctField.setModifiers(9);
      ctClass.addField(ctField, CtField.Initializer.byNew(ctClass1));
      insertBefore(str + ".enter();", false);
      String str1 = str + ".exit();";
      insertAfter(str1, true);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
  }
  
  public void addLocalVariable(String paramString, CtClass paramCtClass) throws CannotCompileException {
    this.declaringClass.checkModify();
    ConstPool14 constPool14 = this.methodInfo.getConstPool();
    CodeAttribute codeAttribute = this.methodInfo.getCodeAttribute();
    if (codeAttribute == null)
      throw new CannotCompileException("no method body"); 
    LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute)codeAttribute.getAttribute("LocalVariableTable");
    if (localVariableAttribute == null) {
      localVariableAttribute = new LocalVariableAttribute(constPool14);
      codeAttribute.getAttributes().add(localVariableAttribute);
    } 
    int i = codeAttribute.getMaxLocals();
    String str = Descriptor.of(paramCtClass);
    localVariableAttribute.addEntry(0, codeAttribute.getCodeLength(), constPool14
        .addUtf8Info(paramString), constPool14.addUtf8Info(str), i);
    codeAttribute.setMaxLocals(i + Descriptor.dataSize(str));
  }
  
  public void insertParameter(CtClass paramCtClass) throws CannotCompileException {
    this.declaringClass.checkModify();
    String str1 = this.methodInfo.getDescriptor();
    String str2 = Descriptor.insertParameter(paramCtClass, str1);
    try {
      addParameter2(Modifier.isStatic(getModifiers()) ? 0 : 1, paramCtClass, str1);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
    this.methodInfo.setDescriptor(str2);
  }
  
  public void addParameter(CtClass paramCtClass) throws CannotCompileException {
    this.declaringClass.checkModify();
    String str1 = this.methodInfo.getDescriptor();
    String str2 = Descriptor.appendParameter(paramCtClass, str1);
    byte b = Modifier.isStatic(getModifiers()) ? 0 : 1;
    try {
      addParameter2(b + Descriptor.paramSize(str1), paramCtClass, str1);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
    this.methodInfo.setDescriptor(str2);
  }
  
  private void addParameter2(int paramInt, CtClass paramCtClass, String paramString) throws BadBytecode {
    CodeAttribute codeAttribute = this.methodInfo.getCodeAttribute();
    if (codeAttribute != null) {
      int i = 1;
      char c = 'L';
      int j = 0;
      if (paramCtClass.isPrimitive()) {
        CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramCtClass;
        i = ctPrimitiveType.getDataSize();
        c = ctPrimitiveType.getDescriptor();
      } else {
        j = this.methodInfo.getConstPool().addClassInfo(paramCtClass);
      } 
      codeAttribute.insertLocalVar(paramInt, i);
      LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute)codeAttribute.getAttribute("LocalVariableTable");
      if (localVariableAttribute != null)
        localVariableAttribute.shiftIndex(paramInt, i); 
      LocalVariableTypeAttribute localVariableTypeAttribute = (LocalVariableTypeAttribute)codeAttribute.getAttribute("LocalVariableTypeTable");
      if (localVariableTypeAttribute != null)
        localVariableTypeAttribute.shiftIndex(paramInt, i); 
      StackMapTable stackMapTable = (StackMapTable)codeAttribute.getAttribute("StackMapTable");
      if (stackMapTable != null)
        stackMapTable.insertLocal(paramInt, StackMapTable.typeTagOf(c), j); 
      StackMap stackMap = (StackMap)codeAttribute.getAttribute("StackMap");
      if (stackMap != null)
        stackMap.insertLocal(paramInt, StackMapTable.typeTagOf(c), j); 
    } 
  }
  
  public void instrument(CodeConverter paramCodeConverter) throws CannotCompileException {
    this.declaringClass.checkModify();
    ConstPool14 constPool14 = this.methodInfo.getConstPool();
    paramCodeConverter.doit(getDeclaringClass(), this.methodInfo, constPool14);
  }
  
  public void instrument(ExprEditor paramExprEditor) throws CannotCompileException {
    if (this.declaringClass.isFrozen())
      this.declaringClass.checkModify(); 
    if (paramExprEditor.doit(this.declaringClass, this.methodInfo))
      this.declaringClass.checkModify(); 
  }
  
  public void insertBefore(String paramString) throws CannotCompileException {
    insertBefore(paramString, true);
  }
  
  private void insertBefore(String paramString, boolean paramBoolean) throws CannotCompileException {
    CtClass ctClass = this.declaringClass;
    ctClass.checkModify();
    CodeAttribute codeAttribute = this.methodInfo.getCodeAttribute();
    if (codeAttribute == null)
      throw new CannotCompileException("no method body"); 
    CodeIterator codeIterator = codeAttribute.iterator();
    Javac javac = new Javac(ctClass);
    try {
      int i = javac.recordParams(getParameterTypes(), 
          Modifier.isStatic(getModifiers()));
      javac.recordParamNames(codeAttribute, i);
      javac.recordLocalVariables(codeAttribute, 0);
      javac.recordType(getReturnType0());
      javac.compileStmnt(paramString);
      Bytecode1 bytecode1 = javac.getBytecode();
      int j = bytecode1.getMaxStack();
      int k = bytecode1.getMaxLocals();
      if (j > codeAttribute.getMaxStack())
        codeAttribute.setMaxStack(j); 
      if (k > codeAttribute.getMaxLocals())
        codeAttribute.setMaxLocals(k); 
      int m = codeIterator.insertEx(bytecode1.get());
      codeIterator.insert(bytecode1.getExceptionTable(), m);
      if (paramBoolean)
        this.methodInfo.rebuildStackMapIf6(ctClass.getClassPool(), ctClass.getClassFile2()); 
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  public void insertAfter(String paramString) throws CannotCompileException {
    insertAfter(paramString, false);
  }
  
  public void insertAfter(String paramString, boolean paramBoolean) throws CannotCompileException {
    CtClass ctClass = this.declaringClass;
    ctClass.checkModify();
    ConstPool14 constPool14 = this.methodInfo.getConstPool();
    CodeAttribute codeAttribute = this.methodInfo.getCodeAttribute();
    if (codeAttribute == null)
      throw new CannotCompileException("no method body"); 
    CodeIterator codeIterator = codeAttribute.iterator();
    int i = codeAttribute.getMaxLocals();
    Bytecode1 bytecode1 = new Bytecode1(constPool14, 0, i + 1);
    bytecode1.setStackDepth(codeAttribute.getMaxStack() + 1);
    Javac javac = new Javac(bytecode1, ctClass);
    try {
      int j = javac.recordParams(getParameterTypes(), 
          Modifier.isStatic(getModifiers()));
      javac.recordParamNames(codeAttribute, j);
      CtClass ctClass1 = getReturnType0();
      int k = javac.recordReturnType(ctClass1, true);
      javac.recordLocalVariables(codeAttribute, 0);
      int m = insertAfterHandler(paramBoolean, bytecode1, ctClass1, k, javac, paramString);
      int n = codeIterator.getCodeLength();
      if (paramBoolean)
        codeAttribute.getExceptionTable().add(getStartPosOfBody(codeAttribute), n, n, 0); 
      int i1 = 0;
      int i2 = 0;
      boolean bool = true;
      while (codeIterator.hasNext()) {
        int i3 = codeIterator.next();
        if (i3 >= n)
          break; 
        int i4 = codeIterator.byteAt(i3);
        if (i4 == 176 || i4 == 172 || i4 == 174 || i4 == 173 || i4 == 175 || i4 == 177) {
          if (bool) {
            i1 = insertAfterAdvice(bytecode1, javac, paramString, constPool14, ctClass1, k);
            n = codeIterator.append(bytecode1.get());
            codeIterator.append(bytecode1.getExceptionTable(), n);
            i2 = codeIterator.getCodeLength() - i1;
            m = i2 - n;
            bool = false;
          } 
          insertGoto(codeIterator, i2, i3);
          i2 = codeIterator.getCodeLength() - i1;
          n = i2 - m;
        } 
      } 
      if (bool) {
        n = codeIterator.append(bytecode1.get());
        codeIterator.append(bytecode1.getExceptionTable(), n);
      } 
      codeAttribute.setMaxStack(bytecode1.getMaxStack());
      codeAttribute.setMaxLocals(bytecode1.getMaxLocals());
      this.methodInfo.rebuildStackMapIf6(ctClass.getClassPool(), ctClass.getClassFile2());
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  private int insertAfterAdvice(Bytecode1 paramBytecode1, Javac paramJavac, String paramString, ConstPool14 paramConstPool14, CtClass paramCtClass, int paramInt) throws CompileError {
    int i = paramBytecode1.currentPc();
    if (paramCtClass == CtClass.voidType) {
      paramBytecode1.addOpcode(1);
      paramBytecode1.addAstore(paramInt);
      paramJavac.compileStmnt(paramString);
      paramBytecode1.addOpcode(177);
      if (paramBytecode1.getMaxLocals() < 1)
        paramBytecode1.setMaxLocals(1); 
    } else {
      paramBytecode1.addStore(paramInt, paramCtClass);
      paramJavac.compileStmnt(paramString);
      paramBytecode1.addLoad(paramInt, paramCtClass);
      if (paramCtClass.isPrimitive()) {
        paramBytecode1.addOpcode(((CtPrimitiveType)paramCtClass).getReturnOp());
      } else {
        paramBytecode1.addOpcode(176);
      } 
    } 
    return paramBytecode1.currentPc() - i;
  }
  
  private void insertGoto(CodeIterator paramCodeIterator, int paramInt1, int paramInt2) throws BadBytecode {
    paramCodeIterator.setMark(paramInt1);
    paramCodeIterator.writeByte(0, paramInt2);
    boolean bool = (paramInt1 + 2 - paramInt2 > 32767) ? true : false;
    byte b = bool ? 4 : 2;
    CodeIterator.Gap gap = paramCodeIterator.insertGapAt(paramInt2, b, false);
    paramInt2 = gap.position + gap.length - b;
    int i = paramCodeIterator.getMark() - paramInt2;
    if (bool) {
      paramCodeIterator.writeByte(200, paramInt2);
      paramCodeIterator.write32bit(i, paramInt2 + 1);
    } else if (i <= 32767) {
      paramCodeIterator.writeByte(167, paramInt2);
      paramCodeIterator.write16bit(i, paramInt2 + 1);
    } else {
      if (gap.length < 4) {
        CodeIterator.Gap gap1 = paramCodeIterator.insertGapAt(gap.position, 2, false);
        paramInt2 = gap1.position + gap1.length + gap.length - 4;
      } 
      paramCodeIterator.writeByte(200, paramInt2);
      paramCodeIterator.write32bit(paramCodeIterator.getMark() - paramInt2, paramInt2 + 1);
    } 
  }
  
  private int insertAfterHandler(boolean paramBoolean, Bytecode1 paramBytecode1, CtClass paramCtClass, int paramInt, Javac paramJavac, String paramString) throws CompileError {
    if (!paramBoolean)
      return 0; 
    int i = paramBytecode1.getMaxLocals();
    paramBytecode1.incMaxLocals(1);
    int j = paramBytecode1.currentPc();
    paramBytecode1.addAstore(i);
    if (paramCtClass.isPrimitive()) {
      char c = ((CtPrimitiveType)paramCtClass).getDescriptor();
      if (c == 'D') {
        paramBytecode1.addDconst(0.0D);
        paramBytecode1.addDstore(paramInt);
      } else if (c == 'F') {
        paramBytecode1.addFconst(0.0F);
        paramBytecode1.addFstore(paramInt);
      } else if (c == 'J') {
        paramBytecode1.addLconst(0L);
        paramBytecode1.addLstore(paramInt);
      } else if (c == 'V') {
        paramBytecode1.addOpcode(1);
        paramBytecode1.addAstore(paramInt);
      } else {
        paramBytecode1.addIconst(0);
        paramBytecode1.addIstore(paramInt);
      } 
    } else {
      paramBytecode1.addOpcode(1);
      paramBytecode1.addAstore(paramInt);
    } 
    paramJavac.compileStmnt(paramString);
    paramBytecode1.addAload(i);
    paramBytecode1.addOpcode(191);
    return paramBytecode1.currentPc() - j;
  }
  
  public void addCatch(String paramString, CtClass paramCtClass) throws CannotCompileException {
    addCatch(paramString, paramCtClass, "$e");
  }
  
  public void addCatch(String paramString1, CtClass paramCtClass, String paramString2) throws CannotCompileException {
    CtClass ctClass = this.declaringClass;
    ctClass.checkModify();
    ConstPool14 constPool14 = this.methodInfo.getConstPool();
    CodeAttribute codeAttribute = this.methodInfo.getCodeAttribute();
    CodeIterator codeIterator = codeAttribute.iterator();
    Bytecode1 bytecode1 = new Bytecode1(constPool14, codeAttribute.getMaxStack(), codeAttribute.getMaxLocals());
    bytecode1.setStackDepth(1);
    Javac javac = new Javac(bytecode1, ctClass);
    try {
      javac.recordParams(getParameterTypes(), 
          Modifier.isStatic(getModifiers()));
      int i = javac.recordVariable(paramCtClass, paramString2);
      bytecode1.addAstore(i);
      javac.compileStmnt(paramString1);
      int j = bytecode1.getMaxStack();
      int k = bytecode1.getMaxLocals();
      if (j > codeAttribute.getMaxStack())
        codeAttribute.setMaxStack(j); 
      if (k > codeAttribute.getMaxLocals())
        codeAttribute.setMaxLocals(k); 
      int m = codeIterator.getCodeLength();
      int n = codeIterator.append(bytecode1.get());
      codeAttribute.getExceptionTable().add(getStartPosOfBody(codeAttribute), m, m, constPool14
          .addClassInfo(paramCtClass));
      codeIterator.append(bytecode1.getExceptionTable(), n);
      this.methodInfo.rebuildStackMapIf6(ctClass.getClassPool(), ctClass.getClassFile2());
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  int getStartPosOfBody(CodeAttribute paramCodeAttribute) throws CannotCompileException {
    return 0;
  }
  
  public int insertAt(int paramInt, String paramString) throws CannotCompileException {
    return insertAt(paramInt, true, paramString);
  }
  
  public int insertAt(int paramInt, boolean paramBoolean, String paramString) throws CannotCompileException {
    CodeAttribute codeAttribute = this.methodInfo.getCodeAttribute();
    if (codeAttribute == null)
      throw new CannotCompileException("no method body"); 
    LineNumberAttribute lineNumberAttribute = (LineNumberAttribute)codeAttribute.getAttribute("LineNumberTable");
    if (lineNumberAttribute == null)
      throw new CannotCompileException("no line number info"); 
    LineNumberAttribute.Pc pc = lineNumberAttribute.toNearPc(paramInt);
    paramInt = pc.line;
    int i = pc.index;
    if (!paramBoolean)
      return paramInt; 
    CtClass ctClass = this.declaringClass;
    ctClass.checkModify();
    CodeIterator codeIterator = codeAttribute.iterator();
    Javac javac = new Javac(ctClass);
    try {
      javac.recordLocalVariables(codeAttribute, i);
      javac.recordParams(getParameterTypes(), 
          Modifier.isStatic(getModifiers()));
      javac.setMaxLocals(codeAttribute.getMaxLocals());
      javac.compileStmnt(paramString);
      Bytecode1 bytecode1 = javac.getBytecode();
      int j = bytecode1.getMaxLocals();
      int k = bytecode1.getMaxStack();
      codeAttribute.setMaxLocals(j);
      if (k > codeAttribute.getMaxStack())
        codeAttribute.setMaxStack(k); 
      i = codeIterator.insertAt(i, bytecode1.get());
      codeIterator.insert(bytecode1.getExceptionTable(), i);
      this.methodInfo.rebuildStackMapIf6(ctClass.getClassPool(), ctClass.getClassFile2());
      return paramInt;
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
}
