package javassist;

import java.util.ListIterator;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.SignatureAttribute;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.compiler.SymbolTable;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.DoubleConst;
import javassist.compiler.ast.IntConst;
import javassist.compiler.ast.StringL;

public class CtField extends CtMember {
  static final String javaLangString = "java.lang.String";
  
  protected FieldInfo fieldInfo;
  
  public CtField(CtClass paramCtClass1, String paramString, CtClass paramCtClass2) throws CannotCompileException {
    this(Descriptor.of(paramCtClass1), paramString, paramCtClass2);
  }
  
  public CtField(CtField paramCtField, CtClass paramCtClass) throws CannotCompileException {
    this(paramCtField.fieldInfo.getDescriptor(), paramCtField.fieldInfo.getName(), paramCtClass);
    ListIterator<AttributeInfo> listIterator = paramCtField.fieldInfo.getAttributes().listIterator();
    FieldInfo fieldInfo = this.fieldInfo;
    fieldInfo.setAccessFlags(paramCtField.fieldInfo.getAccessFlags());
    ConstPool14 constPool14 = fieldInfo.getConstPool();
    while (listIterator.hasNext()) {
      AttributeInfo attributeInfo = listIterator.next();
      fieldInfo.addAttribute(attributeInfo.copy(constPool14, null));
    } 
  }
  
  private CtField(String paramString1, String paramString2, CtClass paramCtClass) throws CannotCompileException {
    super(paramCtClass);
    ClassFile classFile = paramCtClass.getClassFile2();
    if (classFile == null)
      throw new CannotCompileException("bad declaring class: " + paramCtClass
          .getName()); 
    this.fieldInfo = new FieldInfo(classFile.getConstPool(), paramString2, paramString1);
  }
  
  CtField(FieldInfo paramFieldInfo, CtClass paramCtClass) {
    super(paramCtClass);
    this.fieldInfo = paramFieldInfo;
  }
  
  public String toString() {
    return getDeclaringClass().getName() + "." + getName() + ":" + this.fieldInfo
      .getDescriptor();
  }
  
  protected void extendToString(StringBuffer paramStringBuffer) {
    paramStringBuffer.append(' ');
    paramStringBuffer.append(getName());
    paramStringBuffer.append(' ');
    paramStringBuffer.append(this.fieldInfo.getDescriptor());
  }
  
  protected ASTree getInitAST() {
    return null;
  }
  
  Initializer getInit() {
    ASTree aSTree = getInitAST();
    if (aSTree == null)
      return null; 
    return Initializer.byExpr(aSTree);
  }
  
  public static CtField make(String paramString, CtClass paramCtClass) throws CannotCompileException {
    Javac javac = new Javac(paramCtClass);
    try {
      CtMember ctMember = javac.compile(paramString);
      if (ctMember instanceof CtField)
        return (CtField)ctMember; 
    } catch (CompileError compileError) {
      throw new CannotCompileException(compileError);
    } 
    throw new CannotCompileException("not a field");
  }
  
  public FieldInfo getFieldInfo() {
    this.declaringClass.checkModify();
    return this.fieldInfo;
  }
  
  public FieldInfo getFieldInfo2() {
    return this.fieldInfo;
  }
  
  public CtClass getDeclaringClass() {
    return super.getDeclaringClass();
  }
  
  public String getName() {
    return this.fieldInfo.getName();
  }
  
  public void setName(String paramString) {
    this.declaringClass.checkModify();
    this.fieldInfo.setName(paramString);
  }
  
  public int getModifiers() {
    return AccessFlag.toModifier(this.fieldInfo.getAccessFlags());
  }
  
  public void setModifiers(int paramInt) {
    this.declaringClass.checkModify();
    this.fieldInfo.setAccessFlags(AccessFlag.of(paramInt));
  }
  
  public boolean hasAnnotation(String paramString) {
    FieldInfo fieldInfo = getFieldInfo2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)fieldInfo.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)fieldInfo.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType1.hasAnnotationType(paramString, getDeclaringClass().getClassPool(), annotationsAttribute1, annotationsAttribute2);
  }
  
  public Object getAnnotation(Class paramClass) throws ClassNotFoundException {
    FieldInfo fieldInfo = getFieldInfo2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)fieldInfo.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)fieldInfo.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType1.getAnnotationType(paramClass, getDeclaringClass().getClassPool(), annotationsAttribute1, annotationsAttribute2);
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
    FieldInfo fieldInfo = getFieldInfo2();
    AnnotationsAttribute annotationsAttribute1 = (AnnotationsAttribute)fieldInfo.getAttribute("RuntimeInvisibleAnnotations");
    AnnotationsAttribute annotationsAttribute2 = (AnnotationsAttribute)fieldInfo.getAttribute("RuntimeVisibleAnnotations");
    return CtClassType1.toAnnotationType(paramBoolean, getDeclaringClass().getClassPool(), annotationsAttribute1, annotationsAttribute2);
  }
  
  public String getSignature() {
    return this.fieldInfo.getDescriptor();
  }
  
  public String getGenericSignature() {
    SignatureAttribute signatureAttribute = (SignatureAttribute)this.fieldInfo.getAttribute("Signature");
    return (signatureAttribute == null) ? null : signatureAttribute.getSignature();
  }
  
  public void setGenericSignature(String paramString) {
    this.declaringClass.checkModify();
    this.fieldInfo.addAttribute((AttributeInfo)new SignatureAttribute(this.fieldInfo.getConstPool(), paramString));
  }
  
  public CtClass getType() throws NotFoundException {
    return Descriptor.toCtClass(this.fieldInfo.getDescriptor(), this.declaringClass
        .getClassPool());
  }
  
  public void setType(CtClass paramCtClass) {
    this.declaringClass.checkModify();
    this.fieldInfo.setDescriptor(Descriptor.of(paramCtClass));
  }
  
  public Object getConstantValue() {
    int j, i = this.fieldInfo.getConstantValue();
    if (i == 0)
      return null; 
    ConstPool14 constPool14 = this.fieldInfo.getConstPool();
    switch (constPool14.getTag(i)) {
      case 5:
        return new Long(constPool14.getLongInfo(i));
      case 4:
        return new Float(constPool14.getFloatInfo(i));
      case 6:
        return new Double(constPool14.getDoubleInfo(i));
      case 3:
        j = constPool14.getIntegerInfo(i);
        if ("Z".equals(this.fieldInfo.getDescriptor()))
          return new Boolean((j != 0)); 
        return new Integer(j);
      case 8:
        return constPool14.getStringInfo(i);
    } 
    throw new RuntimeException("bad tag: " + constPool14.getTag(i) + " at " + i);
  }
  
  public byte[] getAttribute(String paramString) {
    AttributeInfo attributeInfo = this.fieldInfo.getAttribute(paramString);
    if (attributeInfo == null)
      return null; 
    return attributeInfo.get();
  }
  
  public void setAttribute(String paramString, byte[] paramArrayOfbyte) {
    this.declaringClass.checkModify();
    this.fieldInfo.addAttribute(new AttributeInfo(this.fieldInfo.getConstPool(), paramString, paramArrayOfbyte));
  }
  
  public static abstract class Initializer {
    public static Initializer constant(int param1Int) {
      return new CtField.IntInitializer(param1Int);
    }
    
    public static Initializer constant(boolean param1Boolean) {
      return new CtField.IntInitializer(param1Boolean ? 1 : 0);
    }
    
    public static Initializer constant(long param1Long) {
      return new CtField.LongInitializer(param1Long);
    }
    
    public static Initializer constant(float param1Float) {
      return new CtField.FloatInitializer(param1Float);
    }
    
    public static Initializer constant(double param1Double) {
      return new CtField.DoubleInitializer(param1Double);
    }
    
    public static Initializer constant(String param1String) {
      return new CtField.StringInitializer(param1String);
    }
    
    public static Initializer byParameter(int param1Int) {
      CtField.ParamInitializer paramInitializer = new CtField.ParamInitializer();
      paramInitializer.nthParam = param1Int;
      return paramInitializer;
    }
    
    public static Initializer byNew(CtClass param1CtClass) {
      CtField.NewInitializer newInitializer = new CtField.NewInitializer();
      newInitializer.objectType = param1CtClass;
      newInitializer.stringParams = null;
      newInitializer.withConstructorParams = false;
      return newInitializer;
    }
    
    public static Initializer byNew(CtClass param1CtClass, String[] param1ArrayOfString) {
      CtField.NewInitializer newInitializer = new CtField.NewInitializer();
      newInitializer.objectType = param1CtClass;
      newInitializer.stringParams = param1ArrayOfString;
      newInitializer.withConstructorParams = false;
      return newInitializer;
    }
    
    public static Initializer byNewWithParams(CtClass param1CtClass) {
      CtField.NewInitializer newInitializer = new CtField.NewInitializer();
      newInitializer.objectType = param1CtClass;
      newInitializer.stringParams = null;
      newInitializer.withConstructorParams = true;
      return newInitializer;
    }
    
    public static Initializer byNewWithParams(CtClass param1CtClass, String[] param1ArrayOfString) {
      CtField.NewInitializer newInitializer = new CtField.NewInitializer();
      newInitializer.objectType = param1CtClass;
      newInitializer.stringParams = param1ArrayOfString;
      newInitializer.withConstructorParams = true;
      return newInitializer;
    }
    
    public static Initializer byCall(CtClass param1CtClass, String param1String) {
      CtField.MethodInitializer methodInitializer = new CtField.MethodInitializer();
      methodInitializer.objectType = param1CtClass;
      methodInitializer.methodName = param1String;
      methodInitializer.stringParams = null;
      methodInitializer.withConstructorParams = false;
      return methodInitializer;
    }
    
    public static Initializer byCall(CtClass param1CtClass, String param1String, String[] param1ArrayOfString) {
      CtField.MethodInitializer methodInitializer = new CtField.MethodInitializer();
      methodInitializer.objectType = param1CtClass;
      methodInitializer.methodName = param1String;
      methodInitializer.stringParams = param1ArrayOfString;
      methodInitializer.withConstructorParams = false;
      return methodInitializer;
    }
    
    public static Initializer byCallWithParams(CtClass param1CtClass, String param1String) {
      CtField.MethodInitializer methodInitializer = new CtField.MethodInitializer();
      methodInitializer.objectType = param1CtClass;
      methodInitializer.methodName = param1String;
      methodInitializer.stringParams = null;
      methodInitializer.withConstructorParams = true;
      return methodInitializer;
    }
    
    public static Initializer byCallWithParams(CtClass param1CtClass, String param1String, String[] param1ArrayOfString) {
      CtField.MethodInitializer methodInitializer = new CtField.MethodInitializer();
      methodInitializer.objectType = param1CtClass;
      methodInitializer.methodName = param1String;
      methodInitializer.stringParams = param1ArrayOfString;
      methodInitializer.withConstructorParams = true;
      return methodInitializer;
    }
    
    public static Initializer byNewArray(CtClass param1CtClass, int param1Int) throws NotFoundException {
      return new CtField.ArrayInitializer(param1CtClass.getComponentType(), param1Int);
    }
    
    public static Initializer byNewArray(CtClass param1CtClass, int[] param1ArrayOfint) {
      return new CtField.MultiArrayInitializer(param1CtClass, param1ArrayOfint);
    }
    
    public static Initializer byExpr(String param1String) {
      return new CtField.CodeInitializer(param1String);
    }
    
    static Initializer byExpr(ASTree param1ASTree) {
      return new CtField.PtreeInitializer(param1ASTree);
    }
    
    void check(String param1String) throws CannotCompileException {}
    
    abstract int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException;
    
    abstract int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException;
    
    int getConstantValue(ConstPool14 param1ConstPool14, CtClass param1CtClass) {
      return 0;
    }
  }
  
  static abstract class CodeInitializer0 extends Initializer {
    abstract void compileExpr(Javac param1Javac) throws CompileError;
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      try {
        param1Bytecode1.addAload(0);
        compileExpr(param1Javac);
        param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
        return param1Bytecode1.getMaxStack();
      } catch (CompileError compileError) {
        throw new CannotCompileException(compileError);
      } 
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      try {
        compileExpr(param1Javac);
        param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
        return param1Bytecode1.getMaxStack();
      } catch (CompileError compileError) {
        throw new CannotCompileException(compileError);
      } 
    }
    
    int getConstantValue2(ConstPool14 param1ConstPool14, CtClass param1CtClass, ASTree param1ASTree) {
      if (param1CtClass.isPrimitive()) {
        if (param1ASTree instanceof IntConst) {
          long l = ((IntConst)param1ASTree).get();
          if (param1CtClass == CtClass.doubleType)
            return param1ConstPool14.addDoubleInfo(l); 
          if (param1CtClass == CtClass.floatType)
            return param1ConstPool14.addFloatInfo((float)l); 
          if (param1CtClass == CtClass.longType)
            return param1ConstPool14.addLongInfo(l); 
          if (param1CtClass != CtClass.voidType)
            return param1ConstPool14.addIntegerInfo((int)l); 
        } else if (param1ASTree instanceof DoubleConst) {
          double d = ((DoubleConst)param1ASTree).get();
          if (param1CtClass == CtClass.floatType)
            return param1ConstPool14.addFloatInfo((float)d); 
          if (param1CtClass == CtClass.doubleType)
            return param1ConstPool14.addDoubleInfo(d); 
        } 
      } else if (param1ASTree instanceof StringL && param1CtClass
        .getName().equals("java.lang.String")) {
        return param1ConstPool14.addStringInfo(((StringL)param1ASTree).get());
      } 
      return 0;
    }
  }
  
  static class CodeInitializer extends CodeInitializer0 {
    private String expression;
    
    CodeInitializer(String param1String) {
      this.expression = param1String;
    }
    
    void compileExpr(Javac param1Javac) throws CompileError {
      param1Javac.compileExpr(this.expression);
    }
    
    int getConstantValue(ConstPool14 param1ConstPool14, CtClass param1CtClass) {
      try {
        ASTree aSTree = Javac.parseExpr(this.expression, new SymbolTable());
        return getConstantValue2(param1ConstPool14, param1CtClass, aSTree);
      } catch (CompileError compileError) {
        return 0;
      } 
    }
  }
  
  static class PtreeInitializer extends CodeInitializer0 {
    private ASTree expression;
    
    PtreeInitializer(ASTree param1ASTree) {
      this.expression = param1ASTree;
    }
    
    void compileExpr(Javac param1Javac) throws CompileError {
      param1Javac.compileExpr(this.expression);
    }
    
    int getConstantValue(ConstPool14 param1ConstPool14, CtClass param1CtClass) {
      return getConstantValue2(param1ConstPool14, param1CtClass, this.expression);
    }
  }
  
  static class ParamInitializer extends Initializer {
    int nthParam;
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      if (param1ArrayOfCtClass != null && this.nthParam < param1ArrayOfCtClass.length) {
        param1Bytecode1.addAload(0);
        int i = nthParamToLocal(this.nthParam, param1ArrayOfCtClass, false);
        int j = param1Bytecode1.addLoad(i, param1CtClass) + 1;
        param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
        return j;
      } 
      return 0;
    }
    
    static int nthParamToLocal(int param1Int, CtClass[] param1ArrayOfCtClass, boolean param1Boolean) {
      byte b1;
      CtClass ctClass1 = CtClass.longType;
      CtClass ctClass2 = CtClass.doubleType;
      if (param1Boolean) {
        b1 = 0;
      } else {
        b1 = 1;
      } 
      for (byte b2 = 0; b2 < param1Int; b2++) {
        CtClass ctClass = param1ArrayOfCtClass[b2];
        if (ctClass == ctClass1 || ctClass == ctClass2) {
          b1 += true;
        } else {
          b1++;
        } 
      } 
      return b1;
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      return 0;
    }
  }
  
  static class NewInitializer extends Initializer {
    CtClass objectType;
    
    String[] stringParams;
    
    boolean withConstructorParams;
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      int i;
      param1Bytecode1.addAload(0);
      param1Bytecode1.addNew(this.objectType);
      param1Bytecode1.add(89);
      param1Bytecode1.addAload(0);
      if (this.stringParams == null) {
        i = 4;
      } else {
        i = compileStringParameter(param1Bytecode1) + 4;
      } 
      if (this.withConstructorParams)
        i += CtNewWrappedMethod.compileParameterList(param1Bytecode1, param1ArrayOfCtClass, 1); 
      param1Bytecode1.addInvokespecial(this.objectType, "<init>", getDescriptor());
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return i;
    }
    
    private String getDescriptor() {
      String str = "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)V";
      if (this.stringParams == null) {
        if (this.withConstructorParams)
          return "(Ljava/lang/Object;[Ljava/lang/Object;)V"; 
        return "(Ljava/lang/Object;)V";
      } 
      if (this.withConstructorParams)
        return "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)V"; 
      return "(Ljava/lang/Object;[Ljava/lang/String;)V";
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      String str;
      param1Bytecode1.addNew(this.objectType);
      param1Bytecode1.add(89);
      int i = 2;
      if (this.stringParams == null) {
        str = "()V";
      } else {
        str = "([Ljava/lang/String;)V";
        i += compileStringParameter(param1Bytecode1);
      } 
      param1Bytecode1.addInvokespecial(this.objectType, "<init>", str);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return i;
    }
    
    protected final int compileStringParameter(Bytecode1 param1Bytecode1) throws CannotCompileException {
      int i = this.stringParams.length;
      param1Bytecode1.addIconst(i);
      param1Bytecode1.addAnewarray("java.lang.String");
      for (byte b = 0; b < i; b++) {
        param1Bytecode1.add(89);
        param1Bytecode1.addIconst(b);
        param1Bytecode1.addLdc(this.stringParams[b]);
        param1Bytecode1.add(83);
      } 
      return 4;
    }
  }
  
  static class MethodInitializer extends NewInitializer {
    String methodName;
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      int i;
      param1Bytecode1.addAload(0);
      param1Bytecode1.addAload(0);
      if (this.stringParams == null) {
        i = 2;
      } else {
        i = compileStringParameter(param1Bytecode1) + 2;
      } 
      if (this.withConstructorParams)
        i += CtNewWrappedMethod.compileParameterList(param1Bytecode1, param1ArrayOfCtClass, 1); 
      String str1 = Descriptor.of(param1CtClass);
      String str2 = getDescriptor() + str1;
      param1Bytecode1.addInvokestatic(this.objectType, this.methodName, str2);
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, str1);
      return i;
    }
    
    private String getDescriptor() {
      String str = "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)";
      if (this.stringParams == null) {
        if (this.withConstructorParams)
          return "(Ljava/lang/Object;[Ljava/lang/Object;)"; 
        return "(Ljava/lang/Object;)";
      } 
      if (this.withConstructorParams)
        return "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)"; 
      return "(Ljava/lang/Object;[Ljava/lang/String;)";
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      String str1;
      int i = 1;
      if (this.stringParams == null) {
        str1 = "()";
      } else {
        str1 = "([Ljava/lang/String;)";
        i += compileStringParameter(param1Bytecode1);
      } 
      String str2 = Descriptor.of(param1CtClass);
      param1Bytecode1.addInvokestatic(this.objectType, this.methodName, str1 + str2);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, str2);
      return i;
    }
  }
  
  static class IntInitializer extends Initializer {
    int value;
    
    IntInitializer(int param1Int) {
      this.value = param1Int;
    }
    
    void check(String param1String) throws CannotCompileException {
      char c = param1String.charAt(0);
      if (c != 'I' && c != 'S' && c != 'B' && c != 'C' && c != 'Z')
        throw new CannotCompileException("type mismatch"); 
    }
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addAload(0);
      param1Bytecode1.addIconst(this.value);
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 2;
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addIconst(this.value);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 1;
    }
    
    int getConstantValue(ConstPool14 param1ConstPool14, CtClass param1CtClass) {
      return param1ConstPool14.addIntegerInfo(this.value);
    }
  }
  
  static class LongInitializer extends Initializer {
    long value;
    
    LongInitializer(long param1Long) {
      this.value = param1Long;
    }
    
    void check(String param1String) throws CannotCompileException {
      if (!param1String.equals("J"))
        throw new CannotCompileException("type mismatch"); 
    }
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addAload(0);
      param1Bytecode1.addLdc2w(this.value);
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 3;
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addLdc2w(this.value);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 2;
    }
    
    int getConstantValue(ConstPool14 param1ConstPool14, CtClass param1CtClass) {
      if (param1CtClass == CtClass.longType)
        return param1ConstPool14.addLongInfo(this.value); 
      return 0;
    }
  }
  
  static class FloatInitializer extends Initializer {
    float value;
    
    FloatInitializer(float param1Float) {
      this.value = param1Float;
    }
    
    void check(String param1String) throws CannotCompileException {
      if (!param1String.equals("F"))
        throw new CannotCompileException("type mismatch"); 
    }
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addAload(0);
      param1Bytecode1.addFconst(this.value);
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 3;
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addFconst(this.value);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 2;
    }
    
    int getConstantValue(ConstPool14 param1ConstPool14, CtClass param1CtClass) {
      if (param1CtClass == CtClass.floatType)
        return param1ConstPool14.addFloatInfo(this.value); 
      return 0;
    }
  }
  
  static class DoubleInitializer extends Initializer {
    double value;
    
    DoubleInitializer(double param1Double) {
      this.value = param1Double;
    }
    
    void check(String param1String) throws CannotCompileException {
      if (!param1String.equals("D"))
        throw new CannotCompileException("type mismatch"); 
    }
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addAload(0);
      param1Bytecode1.addLdc2w(this.value);
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 3;
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addLdc2w(this.value);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 2;
    }
    
    int getConstantValue(ConstPool14 param1ConstPool14, CtClass param1CtClass) {
      if (param1CtClass == CtClass.doubleType)
        return param1ConstPool14.addDoubleInfo(this.value); 
      return 0;
    }
  }
  
  static class StringInitializer extends Initializer {
    String value;
    
    StringInitializer(String param1String) {
      this.value = param1String;
    }
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addAload(0);
      param1Bytecode1.addLdc(this.value);
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 2;
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addLdc(this.value);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 1;
    }
    
    int getConstantValue(ConstPool14 param1ConstPool14, CtClass param1CtClass) {
      if (param1CtClass.getName().equals("java.lang.String"))
        return param1ConstPool14.addStringInfo(this.value); 
      return 0;
    }
  }
  
  static class ArrayInitializer extends Initializer {
    CtClass type;
    
    int size;
    
    ArrayInitializer(CtClass param1CtClass, int param1Int) {
      this.type = param1CtClass;
      this.size = param1Int;
    }
    
    private void addNewarray(Bytecode1 param1Bytecode1) {
      if (this.type.isPrimitive()) {
        param1Bytecode1.addNewarray(((CtPrimitiveType)this.type).getArrayType(), this.size);
      } else {
        param1Bytecode1.addAnewarray(this.type, this.size);
      } 
    }
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addAload(0);
      addNewarray(param1Bytecode1);
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 2;
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      addNewarray(param1Bytecode1);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return 1;
    }
  }
  
  static class MultiArrayInitializer extends Initializer {
    CtClass type;
    
    int[] dim;
    
    MultiArrayInitializer(CtClass param1CtClass, int[] param1ArrayOfint) {
      this.type = param1CtClass;
      this.dim = param1ArrayOfint;
    }
    
    void check(String param1String) throws CannotCompileException {
      if (param1String.charAt(0) != '[')
        throw new CannotCompileException("type mismatch"); 
    }
    
    int compile(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, CtClass[] param1ArrayOfCtClass, Javac param1Javac) throws CannotCompileException {
      param1Bytecode1.addAload(0);
      int i = param1Bytecode1.addMultiNewarray(param1CtClass, this.dim);
      param1Bytecode1.addPutfield(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return i + 1;
    }
    
    int compileIfStatic(CtClass param1CtClass, String param1String, Bytecode1 param1Bytecode1, Javac param1Javac) throws CannotCompileException {
      int i = param1Bytecode1.addMultiNewarray(param1CtClass, this.dim);
      param1Bytecode1.addPutstatic(Bytecode1.THIS, param1String, Descriptor.of(param1CtClass));
      return i;
    }
  }
}
