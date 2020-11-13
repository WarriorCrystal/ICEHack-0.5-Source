package javassist.expr;

import java.util.Iterator;
import java.util.LinkedList;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public abstract class Expr implements Opcode {
  int currentPos;
  
  CodeIterator iterator;
  
  CtClass thisClass;
  
  MethodInfo thisMethod;
  
  boolean edited;
  
  int maxLocals;
  
  int maxStack;
  
  static final String javaLangObject = "java.lang.Object";
  
  protected Expr(int paramInt, CodeIterator paramCodeIterator, CtClass paramCtClass, MethodInfo paramMethodInfo) {
    this.currentPos = paramInt;
    this.iterator = paramCodeIterator;
    this.thisClass = paramCtClass;
    this.thisMethod = paramMethodInfo;
  }
  
  public CtClass getEnclosingClass() {
    return this.thisClass;
  }
  
  protected final ConstPool14 getConstPool() {
    return this.thisMethod.getConstPool();
  }
  
  protected final boolean edited() {
    return this.edited;
  }
  
  protected final int locals() {
    return this.maxLocals;
  }
  
  protected final int stack() {
    return this.maxStack;
  }
  
  protected final boolean withinStatic() {
    return ((this.thisMethod.getAccessFlags() & 0x8) != 0);
  }
  
  public CtBehavior where() {
    MethodInfo methodInfo = this.thisMethod;
    CtBehavior[] arrayOfCtBehavior = this.thisClass.getDeclaredBehaviors();
    for (int i = arrayOfCtBehavior.length - 1; i >= 0; i--) {
      if (arrayOfCtBehavior[i].getMethodInfo2() == methodInfo)
        return arrayOfCtBehavior[i]; 
    } 
    CtConstructor ctConstructor = this.thisClass.getClassInitializer();
    if (ctConstructor != null && ctConstructor.getMethodInfo2() == methodInfo)
      return (CtBehavior)ctConstructor; 
    for (int j = arrayOfCtBehavior.length - 1; j >= 0; j--) {
      if (this.thisMethod.getName().equals(arrayOfCtBehavior[j].getMethodInfo2().getName()) && this.thisMethod
        .getDescriptor()
        .equals(arrayOfCtBehavior[j].getMethodInfo2().getDescriptor()))
        return arrayOfCtBehavior[j]; 
    } 
    throw new RuntimeException("fatal: not found");
  }
  
  public CtClass[] mayThrow() {
    ClassPool classPool = this.thisClass.getClassPool();
    ConstPool14 constPool14 = this.thisMethod.getConstPool();
    LinkedList linkedList = new LinkedList();
    try {
      CodeAttribute codeAttribute = this.thisMethod.getCodeAttribute();
      ExceptionTable exceptionTable = codeAttribute.getExceptionTable();
      int i = this.currentPos;
      int j = exceptionTable.size();
      for (byte b = 0; b < j; b++) {
        if (exceptionTable.startPc(b) <= i && i < exceptionTable.endPc(b)) {
          int k = exceptionTable.catchType(b);
          if (k > 0)
            try {
              addClass(linkedList, classPool.get(constPool14.getClassInfo(k)));
            } catch (NotFoundException notFoundException) {} 
        } 
      } 
    } catch (NullPointerException nullPointerException) {}
    ExceptionsAttribute exceptionsAttribute = this.thisMethod.getExceptionsAttribute();
    if (exceptionsAttribute != null) {
      String[] arrayOfString = exceptionsAttribute.getExceptions();
      if (arrayOfString != null) {
        int i = arrayOfString.length;
        for (byte b = 0; b < i; b++) {
          try {
            addClass(linkedList, classPool.get(arrayOfString[b]));
          } catch (NotFoundException notFoundException) {}
        } 
      } 
    } 
    return (CtClass[])linkedList.toArray((Object[])new CtClass[linkedList.size()]);
  }
  
  private static void addClass(LinkedList<CtClass> paramLinkedList, CtClass paramCtClass) {
    Iterator<CtClass> iterator = paramLinkedList.iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == paramCtClass)
        return; 
    } 
    paramLinkedList.add(paramCtClass);
  }
  
  public int indexOfBytecode() {
    return this.currentPos;
  }
  
  public int getLineNumber() {
    return this.thisMethod.getLineNumber(this.currentPos);
  }
  
  public String getFileName() {
    ClassFile classFile = this.thisClass.getClassFile2();
    if (classFile == null)
      return null; 
    return classFile.getSourceFile();
  }
  
  static final boolean checkResultValue(CtClass paramCtClass, String paramString) throws CannotCompileException {
    boolean bool = (paramString.indexOf("$_") >= 0) ? true : false;
    if (!bool && paramCtClass != CtClass.voidType)
      throw new CannotCompileException("the resulting value is not stored in $_"); 
    return bool;
  }
  
  static final void storeStack(CtClass[] paramArrayOfCtClass, boolean paramBoolean, int paramInt, Bytecode1 paramBytecode1) {
    storeStack0(0, paramArrayOfCtClass.length, paramArrayOfCtClass, paramInt + 1, paramBytecode1);
    if (paramBoolean)
      paramBytecode1.addOpcode(1); 
    paramBytecode1.addAstore(paramInt);
  }
  
  private static void storeStack0(int paramInt1, int paramInt2, CtClass[] paramArrayOfCtClass, int paramInt3, Bytecode1 paramBytecode1) {
    byte b;
    if (paramInt1 >= paramInt2)
      return; 
    CtClass ctClass = paramArrayOfCtClass[paramInt1];
    if (ctClass instanceof CtPrimitiveType) {
      b = ((CtPrimitiveType)ctClass).getDataSize();
    } else {
      b = 1;
    } 
    storeStack0(paramInt1 + 1, paramInt2, paramArrayOfCtClass, paramInt3 + b, paramBytecode1);
    paramBytecode1.addStore(paramInt3, ctClass);
  }
  
  public abstract void replace(String paramString) throws CannotCompileException;
  
  public void replace(String paramString, ExprEditor paramExprEditor) throws CannotCompileException {
    replace(paramString);
    if (paramExprEditor != null)
      runEditor(paramExprEditor, this.iterator); 
  }
  
  protected void replace0(int paramInt1, Bytecode1 paramBytecode1, int paramInt2) throws BadBytecode {
    byte[] arrayOfByte = paramBytecode1.get();
    this.edited = true;
    int i = arrayOfByte.length - paramInt2;
    for (byte b = 0; b < paramInt2; b++)
      this.iterator.writeByte(0, paramInt1 + b); 
    if (i > 0)
      paramInt1 = (this.iterator.insertGapAt(paramInt1, i, false)).position; 
    this.iterator.write(arrayOfByte, paramInt1);
    this.iterator.insert(paramBytecode1.getExceptionTable(), paramInt1);
    this.maxLocals = paramBytecode1.getMaxLocals();
    this.maxStack = paramBytecode1.getMaxStack();
  }
  
  protected void runEditor(ExprEditor paramExprEditor, CodeIterator paramCodeIterator) throws CannotCompileException {
    CodeAttribute codeAttribute = paramCodeIterator.get();
    int i = codeAttribute.getMaxLocals();
    int j = codeAttribute.getMaxStack();
    int k = locals();
    codeAttribute.setMaxStack(stack());
    codeAttribute.setMaxLocals(k);
    ExprEditor.LoopContext loopContext = new ExprEditor.LoopContext(k);
    int m = paramCodeIterator.getCodeLength();
    int n = paramCodeIterator.lookAhead();
    paramCodeIterator.move(this.currentPos);
    if (paramExprEditor.doit(this.thisClass, this.thisMethod, loopContext, paramCodeIterator, n))
      this.edited = true; 
    paramCodeIterator.move(n + paramCodeIterator.getCodeLength() - m);
    codeAttribute.setMaxLocals(i);
    codeAttribute.setMaxStack(j);
    this.maxLocals = loopContext.maxLocals;
    this.maxStack += loopContext.maxStack;
  }
}
