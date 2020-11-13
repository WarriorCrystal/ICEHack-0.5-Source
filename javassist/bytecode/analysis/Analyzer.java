package javassist.bytecode.analysis;

import java.util.Iterator;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class Analyzer implements Opcode {
  private final SubroutineScanner scanner = new SubroutineScanner();
  
  private CtClass clazz;
  
  private ExceptionInfo[] exceptions;
  
  private Frame[] frames;
  
  private Subroutine[] subroutines;
  
  private static class ExceptionInfo {
    private int end;
    
    private int handler;
    
    private int start;
    
    private Type type;
    
    private ExceptionInfo(int param1Int1, int param1Int2, int param1Int3, Type param1Type) {
      this.start = param1Int1;
      this.end = param1Int2;
      this.handler = param1Int3;
      this.type = param1Type;
    }
  }
  
  public Frame[] analyze(CtClass paramCtClass, MethodInfo paramMethodInfo) throws BadBytecode {
    this.clazz = paramCtClass;
    CodeAttribute codeAttribute = paramMethodInfo.getCodeAttribute();
    if (codeAttribute == null)
      return null; 
    int i = codeAttribute.getMaxLocals();
    int j = codeAttribute.getMaxStack();
    int k = codeAttribute.getCodeLength();
    CodeIterator codeIterator = codeAttribute.iterator();
    IntQueue intQueue = new IntQueue();
    this.exceptions = buildExceptionInfo(paramMethodInfo);
    this.subroutines = this.scanner.scan(paramMethodInfo);
    Executor executor = new Executor(paramCtClass.getClassPool(), paramMethodInfo.getConstPool());
    this.frames = new Frame[k];
    this.frames[codeIterator.lookAhead()] = firstFrame(paramMethodInfo, i, j);
    intQueue.add(codeIterator.next());
    while (!intQueue.isEmpty())
      analyzeNextEntry(paramMethodInfo, codeIterator, intQueue, executor); 
    return this.frames;
  }
  
  public Frame[] analyze(CtMethod paramCtMethod) throws BadBytecode {
    return analyze(paramCtMethod.getDeclaringClass(), paramCtMethod.getMethodInfo2());
  }
  
  private void analyzeNextEntry(MethodInfo paramMethodInfo, CodeIterator paramCodeIterator, IntQueue paramIntQueue, Executor paramExecutor) throws BadBytecode {
    int i = paramIntQueue.take();
    paramCodeIterator.move(i);
    paramCodeIterator.next();
    Frame frame = this.frames[i].copy();
    Subroutine subroutine = this.subroutines[i];
    try {
      paramExecutor.execute(paramMethodInfo, i, paramCodeIterator, frame, subroutine);
    } catch (RuntimeException runtimeException) {
      throw new BadBytecode(runtimeException.getMessage() + "[pos = " + i + "]", runtimeException);
    } 
    int j = paramCodeIterator.byteAt(i);
    if (j == 170) {
      mergeTableSwitch(paramIntQueue, i, paramCodeIterator, frame);
    } else if (j == 171) {
      mergeLookupSwitch(paramIntQueue, i, paramCodeIterator, frame);
    } else if (j == 169) {
      mergeRet(paramIntQueue, paramCodeIterator, i, frame, subroutine);
    } else if (Util.isJumpInstruction(j)) {
      int k = Util.getJumpTarget(i, paramCodeIterator);
      if (Util.isJsr(j)) {
        mergeJsr(paramIntQueue, this.frames[i], this.subroutines[k], i, lookAhead(paramCodeIterator, i));
      } else if (!Util.isGoto(j)) {
        merge(paramIntQueue, frame, lookAhead(paramCodeIterator, i));
      } 
      merge(paramIntQueue, frame, k);
    } else if (j != 191 && !Util.isReturn(j)) {
      merge(paramIntQueue, frame, lookAhead(paramCodeIterator, i));
    } 
    mergeExceptionHandlers(paramIntQueue, paramMethodInfo, i, frame);
  }
  
  private ExceptionInfo[] buildExceptionInfo(MethodInfo paramMethodInfo) {
    ConstPool14 constPool14 = paramMethodInfo.getConstPool();
    ClassPool classPool = this.clazz.getClassPool();
    ExceptionTable exceptionTable = paramMethodInfo.getCodeAttribute().getExceptionTable();
    ExceptionInfo[] arrayOfExceptionInfo = new ExceptionInfo[exceptionTable.size()];
    for (byte b = 0; b < exceptionTable.size(); b++) {
      Type type;
      int i = exceptionTable.catchType(b);
      try {
        type = (i == 0) ? Type.THROWABLE : Type.get(classPool.get(constPool14.getClassInfo(i)));
      } catch (NotFoundException notFoundException) {
        throw new IllegalStateException(notFoundException.getMessage());
      } 
      arrayOfExceptionInfo[b] = new ExceptionInfo(exceptionTable.startPc(b), exceptionTable.endPc(b), exceptionTable.handlerPc(b), type);
    } 
    return arrayOfExceptionInfo;
  }
  
  private Frame firstFrame(MethodInfo paramMethodInfo, int paramInt1, int paramInt2) {
    CtClass[] arrayOfCtClass;
    byte b1 = 0;
    Frame frame = new Frame(paramInt1, paramInt2);
    if ((paramMethodInfo.getAccessFlags() & 0x8) == 0)
      frame.setLocal(b1++, Type.get(this.clazz)); 
    try {
      arrayOfCtClass = Descriptor.getParameterTypes(paramMethodInfo.getDescriptor(), this.clazz.getClassPool());
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException);
    } 
    for (byte b2 = 0; b2 < arrayOfCtClass.length; b2++) {
      Type type = zeroExtend(Type.get(arrayOfCtClass[b2]));
      frame.setLocal(b1++, type);
      if (type.getSize() == 2)
        frame.setLocal(b1++, Type.TOP); 
    } 
    return frame;
  }
  
  private int getNext(CodeIterator paramCodeIterator, int paramInt1, int paramInt2) throws BadBytecode {
    paramCodeIterator.move(paramInt1);
    paramCodeIterator.next();
    int i = paramCodeIterator.lookAhead();
    paramCodeIterator.move(paramInt2);
    paramCodeIterator.next();
    return i;
  }
  
  private int lookAhead(CodeIterator paramCodeIterator, int paramInt) throws BadBytecode {
    if (!paramCodeIterator.hasNext())
      throw new BadBytecode("Execution falls off end! [pos = " + paramInt + "]"); 
    return paramCodeIterator.lookAhead();
  }
  
  private void merge(IntQueue paramIntQueue, Frame paramFrame, int paramInt) {
    boolean bool;
    Frame frame = this.frames[paramInt];
    if (frame == null) {
      this.frames[paramInt] = paramFrame.copy();
      bool = true;
    } else {
      bool = frame.merge(paramFrame);
    } 
    if (bool)
      paramIntQueue.add(paramInt); 
  }
  
  private void mergeExceptionHandlers(IntQueue paramIntQueue, MethodInfo paramMethodInfo, int paramInt, Frame paramFrame) {
    for (byte b = 0; b < this.exceptions.length; b++) {
      ExceptionInfo exceptionInfo = this.exceptions[b];
      if (paramInt >= exceptionInfo.start && paramInt < exceptionInfo.end) {
        Frame frame = paramFrame.copy();
        frame.clearStack();
        frame.push(exceptionInfo.type);
        merge(paramIntQueue, frame, exceptionInfo.handler);
      } 
    } 
  }
  
  private void mergeJsr(IntQueue paramIntQueue, Frame paramFrame, Subroutine paramSubroutine, int paramInt1, int paramInt2) throws BadBytecode {
    if (paramSubroutine == null)
      throw new BadBytecode("No subroutine at jsr target! [pos = " + paramInt1 + "]"); 
    Frame frame = this.frames[paramInt2];
    boolean bool = false;
    if (frame == null) {
      frame = this.frames[paramInt2] = paramFrame.copy();
      bool = true;
    } else {
      for (byte b = 0; b < paramFrame.localsLength(); b++) {
        if (!paramSubroutine.isAccessed(b)) {
          Type type1 = frame.getLocal(b);
          Type type2 = paramFrame.getLocal(b);
          if (type1 == null) {
            frame.setLocal(b, type2);
            bool = true;
          } else {
            type2 = type1.merge(type2);
            frame.setLocal(b, type2);
            if (!type2.equals(type1) || type2.popChanged())
              bool = true; 
          } 
        } 
      } 
    } 
    if (!frame.isJsrMerged()) {
      frame.setJsrMerged(true);
      bool = true;
    } 
    if (bool && frame.isRetMerged())
      paramIntQueue.add(paramInt2); 
  }
  
  private void mergeLookupSwitch(IntQueue paramIntQueue, int paramInt, CodeIterator paramCodeIterator, Frame paramFrame) throws BadBytecode {
    int i = (paramInt & 0xFFFFFFFC) + 4;
    merge(paramIntQueue, paramFrame, paramInt + paramCodeIterator.s32bitAt(i));
    i += 4;
    int j = paramCodeIterator.s32bitAt(i);
    i += 4;
    int k = j * 8 + i;
    for (i += 4; i < k; i += 8) {
      int m = paramCodeIterator.s32bitAt(i) + paramInt;
      merge(paramIntQueue, paramFrame, m);
    } 
  }
  
  private void mergeRet(IntQueue paramIntQueue, CodeIterator paramCodeIterator, int paramInt, Frame paramFrame, Subroutine paramSubroutine) throws BadBytecode {
    if (paramSubroutine == null)
      throw new BadBytecode("Ret on no subroutine! [pos = " + paramInt + "]"); 
    Iterator<Integer> iterator = paramSubroutine.callers().iterator();
    while (iterator.hasNext()) {
      int i = ((Integer)iterator.next()).intValue();
      int j = getNext(paramCodeIterator, i, paramInt);
      boolean bool = false;
      Frame frame = this.frames[j];
      if (frame == null) {
        frame = this.frames[j] = paramFrame.copyStack();
        bool = true;
      } else {
        bool = frame.mergeStack(paramFrame);
      } 
      for (Iterator<Integer> iterator1 = paramSubroutine.accessed().iterator(); iterator1.hasNext(); ) {
        int k = ((Integer)iterator1.next()).intValue();
        Type type1 = frame.getLocal(k);
        Type type2 = paramFrame.getLocal(k);
        if (type1 != type2) {
          frame.setLocal(k, type2);
          bool = true;
        } 
      } 
      if (!frame.isRetMerged()) {
        frame.setRetMerged(true);
        bool = true;
      } 
      if (bool && frame.isJsrMerged())
        paramIntQueue.add(j); 
    } 
  }
  
  private void mergeTableSwitch(IntQueue paramIntQueue, int paramInt, CodeIterator paramCodeIterator, Frame paramFrame) throws BadBytecode {
    int i = (paramInt & 0xFFFFFFFC) + 4;
    merge(paramIntQueue, paramFrame, paramInt + paramCodeIterator.s32bitAt(i));
    i += 4;
    int j = paramCodeIterator.s32bitAt(i);
    i += 4;
    int k = paramCodeIterator.s32bitAt(i);
    i += 4;
    int m = (k - j + 1) * 4 + i;
    for (; i < m; i += 4) {
      int n = paramCodeIterator.s32bitAt(i) + paramInt;
      merge(paramIntQueue, paramFrame, n);
    } 
  }
  
  private Type zeroExtend(Type paramType) {
    if (paramType == Type.SHORT || paramType == Type.BYTE || paramType == Type.CHAR || paramType == Type.BOOLEAN)
      return Type.INTEGER; 
    return paramType;
  }
}
