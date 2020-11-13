package javassist.compiler;

import java.util.HashMap;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SyntheticAttribute;

public class AccessorMaker {
  private CtClass clazz;
  
  private int uniqueNumber;
  
  private HashMap accessors;
  
  static final String lastParamType = "javassist.runtime.Inner";
  
  public AccessorMaker(CtClass paramCtClass) {
    this.clazz = paramCtClass;
    this.uniqueNumber = 1;
    this.accessors = new HashMap<Object, Object>();
  }
  
  public String getConstructor(CtClass paramCtClass, String paramString, MethodInfo paramMethodInfo) throws CompileError {
    String str1 = "<init>:" + paramString;
    String str2 = (String)this.accessors.get(str1);
    if (str2 != null)
      return str2; 
    str2 = Descriptor.appendParameter("javassist.runtime.Inner", paramString);
    ClassFile classFile = this.clazz.getClassFile();
    try {
      ConstPool14 constPool14 = classFile.getConstPool();
      ClassPool classPool = this.clazz.getClassPool();
      MethodInfo methodInfo = new MethodInfo(constPool14, "<init>", str2);
      methodInfo.setAccessFlags(0);
      methodInfo.addAttribute((AttributeInfo)new SyntheticAttribute(constPool14));
      ExceptionsAttribute exceptionsAttribute = paramMethodInfo.getExceptionsAttribute();
      if (exceptionsAttribute != null)
        methodInfo.addAttribute(exceptionsAttribute.copy(constPool14, null)); 
      CtClass[] arrayOfCtClass = Descriptor.getParameterTypes(paramString, classPool);
      Bytecode1 bytecode1 = new Bytecode1(constPool14);
      bytecode1.addAload(0);
      int i = 1;
      for (byte b = 0; b < arrayOfCtClass.length; b++)
        i += bytecode1.addLoad(i, arrayOfCtClass[b]); 
      bytecode1.setMaxLocals(i + 1);
      bytecode1.addInvokespecial(this.clazz, "<init>", paramString);
      bytecode1.addReturn(null);
      methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
      classFile.addMethod(methodInfo);
    } catch (CannotCompileException cannotCompileException) {
      throw new CompileError(cannotCompileException);
    } catch (NotFoundException notFoundException) {
      throw new CompileError(notFoundException);
    } 
    this.accessors.put(str1, str2);
    return str2;
  }
  
  public String getMethodAccessor(String paramString1, String paramString2, String paramString3, MethodInfo paramMethodInfo) throws CompileError {
    String str1 = paramString1 + ":" + paramString2;
    String str2 = (String)this.accessors.get(str1);
    if (str2 != null)
      return str2; 
    ClassFile classFile = this.clazz.getClassFile();
    str2 = findAccessorName(classFile);
    try {
      ConstPool14 constPool14 = classFile.getConstPool();
      ClassPool classPool = this.clazz.getClassPool();
      MethodInfo methodInfo = new MethodInfo(constPool14, str2, paramString3);
      methodInfo.setAccessFlags(8);
      methodInfo.addAttribute((AttributeInfo)new SyntheticAttribute(constPool14));
      ExceptionsAttribute exceptionsAttribute = paramMethodInfo.getExceptionsAttribute();
      if (exceptionsAttribute != null)
        methodInfo.addAttribute(exceptionsAttribute.copy(constPool14, null)); 
      CtClass[] arrayOfCtClass = Descriptor.getParameterTypes(paramString3, classPool);
      int i = 0;
      Bytecode1 bytecode1 = new Bytecode1(constPool14);
      for (byte b = 0; b < arrayOfCtClass.length; b++)
        i += bytecode1.addLoad(i, arrayOfCtClass[b]); 
      bytecode1.setMaxLocals(i);
      if (paramString2 == paramString3) {
        bytecode1.addInvokestatic(this.clazz, paramString1, paramString2);
      } else {
        bytecode1.addInvokevirtual(this.clazz, paramString1, paramString2);
      } 
      bytecode1.addReturn(Descriptor.getReturnType(paramString2, classPool));
      methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
      classFile.addMethod(methodInfo);
    } catch (CannotCompileException cannotCompileException) {
      throw new CompileError(cannotCompileException);
    } catch (NotFoundException notFoundException) {
      throw new CompileError(notFoundException);
    } 
    this.accessors.put(str1, str2);
    return str2;
  }
  
  public MethodInfo getFieldGetter(FieldInfo paramFieldInfo, boolean paramBoolean) throws CompileError {
    String str1 = paramFieldInfo.getName();
    String str2 = str1 + ":getter";
    Object object = this.accessors.get(str2);
    if (object != null)
      return (MethodInfo)object; 
    ClassFile classFile = this.clazz.getClassFile();
    String str3 = findAccessorName(classFile);
    try {
      String str5;
      ConstPool14 constPool14 = classFile.getConstPool();
      ClassPool classPool = this.clazz.getClassPool();
      String str4 = paramFieldInfo.getDescriptor();
      if (paramBoolean) {
        str5 = "()" + str4;
      } else {
        str5 = "(" + Descriptor.of(this.clazz) + ")" + str4;
      } 
      MethodInfo methodInfo = new MethodInfo(constPool14, str3, str5);
      methodInfo.setAccessFlags(8);
      methodInfo.addAttribute((AttributeInfo)new SyntheticAttribute(constPool14));
      Bytecode1 bytecode1 = new Bytecode1(constPool14);
      if (paramBoolean) {
        bytecode1.addGetstatic(Bytecode1.THIS, str1, str4);
      } else {
        bytecode1.addAload(0);
        bytecode1.addGetfield(Bytecode1.THIS, str1, str4);
        bytecode1.setMaxLocals(1);
      } 
      bytecode1.addReturn(Descriptor.toCtClass(str4, classPool));
      methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
      classFile.addMethod(methodInfo);
      this.accessors.put(str2, methodInfo);
      return methodInfo;
    } catch (CannotCompileException cannotCompileException) {
      throw new CompileError(cannotCompileException);
    } catch (NotFoundException notFoundException) {
      throw new CompileError(notFoundException);
    } 
  }
  
  public MethodInfo getFieldSetter(FieldInfo paramFieldInfo, boolean paramBoolean) throws CompileError {
    String str1 = paramFieldInfo.getName();
    String str2 = str1 + ":setter";
    Object object = this.accessors.get(str2);
    if (object != null)
      return (MethodInfo)object; 
    ClassFile classFile = this.clazz.getClassFile();
    String str3 = findAccessorName(classFile);
    try {
      String str5;
      int i;
      ConstPool14 constPool14 = classFile.getConstPool();
      ClassPool classPool = this.clazz.getClassPool();
      String str4 = paramFieldInfo.getDescriptor();
      if (paramBoolean) {
        str5 = "(" + str4 + ")V";
      } else {
        str5 = "(" + Descriptor.of(this.clazz) + str4 + ")V";
      } 
      MethodInfo methodInfo = new MethodInfo(constPool14, str3, str5);
      methodInfo.setAccessFlags(8);
      methodInfo.addAttribute((AttributeInfo)new SyntheticAttribute(constPool14));
      Bytecode1 bytecode1 = new Bytecode1(constPool14);
      if (paramBoolean) {
        i = bytecode1.addLoad(0, Descriptor.toCtClass(str4, classPool));
        bytecode1.addPutstatic(Bytecode1.THIS, str1, str4);
      } else {
        bytecode1.addAload(0);
        i = bytecode1.addLoad(1, Descriptor.toCtClass(str4, classPool)) + 1;
        bytecode1.addPutfield(Bytecode1.THIS, str1, str4);
      } 
      bytecode1.addReturn(null);
      bytecode1.setMaxLocals(i);
      methodInfo.setCodeAttribute(bytecode1.toCodeAttribute());
      classFile.addMethod(methodInfo);
      this.accessors.put(str2, methodInfo);
      return methodInfo;
    } catch (CannotCompileException cannotCompileException) {
      throw new CompileError(cannotCompileException);
    } catch (NotFoundException notFoundException) {
      throw new CompileError(notFoundException);
    } 
  }
  
  private String findAccessorName(ClassFile paramClassFile) {
    while (true) {
      String str = "access$" + this.uniqueNumber++;
      if (paramClassFile.getMethod(str) == null)
        return str; 
    } 
  }
}
