package javassist.bytecode.analysis;

import java.io.PrintStream;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.InstructionPrinter;
import javassist.bytecode.MethodInfo;

public final class FramePrinter {
  private final PrintStream stream;
  
  public FramePrinter(PrintStream paramPrintStream) {
    this.stream = paramPrintStream;
  }
  
  public static void print(CtClass paramCtClass, PrintStream paramPrintStream) {
    (new FramePrinter(paramPrintStream)).print(paramCtClass);
  }
  
  public void print(CtClass paramCtClass) {
    CtMethod[] arrayOfCtMethod = paramCtClass.getDeclaredMethods();
    for (byte b = 0; b < arrayOfCtMethod.length; b++)
      print(arrayOfCtMethod[b]); 
  }
  
  private String getMethodString(CtMethod paramCtMethod) {
    try {
      return Modifier.toString(paramCtMethod.getModifiers()) + " " + paramCtMethod
        .getReturnType().getName() + " " + paramCtMethod.getName() + 
        Descriptor.toString(paramCtMethod.getSignature()) + ";";
    } catch (NotFoundException notFoundException) {
      throw new RuntimeException(notFoundException);
    } 
  }
  
  public void print(CtMethod paramCtMethod) {
    Frame[] arrayOfFrame;
    this.stream.println("\n" + getMethodString(paramCtMethod));
    MethodInfo methodInfo = paramCtMethod.getMethodInfo2();
    ConstPool14 constPool14 = methodInfo.getConstPool();
    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
    if (codeAttribute == null)
      return; 
    try {
      arrayOfFrame = (new Analyzer()).analyze(paramCtMethod.getDeclaringClass(), methodInfo);
    } catch (BadBytecode badBytecode) {
      throw new RuntimeException(badBytecode);
    } 
    int i = String.valueOf(codeAttribute.getCodeLength()).length();
    CodeIterator codeIterator = codeAttribute.iterator();
    while (codeIterator.hasNext()) {
      int j;
      try {
        j = codeIterator.next();
      } catch (BadBytecode badBytecode) {
        throw new RuntimeException(badBytecode);
      } 
      this.stream.println(j + ": " + InstructionPrinter.instructionString(codeIterator, j, constPool14));
      addSpacing(i + 3);
      Frame frame = arrayOfFrame[j];
      if (frame == null) {
        this.stream.println("--DEAD CODE--");
        continue;
      } 
      printStack(frame);
      addSpacing(i + 3);
      printLocals(frame);
    } 
  }
  
  private void printStack(Frame paramFrame) {
    this.stream.print("stack [");
    int i = paramFrame.getTopIndex();
    for (byte b = 0; b <= i; b++) {
      if (b > 0)
        this.stream.print(", "); 
      Type type = paramFrame.getStack(b);
      this.stream.print(type);
    } 
    this.stream.println("]");
  }
  
  private void printLocals(Frame paramFrame) {
    this.stream.print("locals [");
    int i = paramFrame.localsLength();
    for (byte b = 0; b < i; b++) {
      if (b > 0)
        this.stream.print(", "); 
      Type type = paramFrame.getLocal(b);
      this.stream.print((type == null) ? "empty" : type.toString());
    } 
    this.stream.println("]");
  }
  
  private void addSpacing(int paramInt) {
    while (paramInt-- > 0)
      this.stream.print(' '); 
  }
}
