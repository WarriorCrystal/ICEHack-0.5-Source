package javassist.tools;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.analysis.FramePrinter;

public class framedump {
  public static void main(String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString.length != 1) {
      System.err.println("Usage: java javassist.tools.framedump <fully-qualified class name>");
      return;
    } 
    ClassPool classPool = ClassPool.getDefault();
    CtClass ctClass = classPool.get(paramArrayOfString[0]);
    System.out.println("Frame Dump of " + ctClass.getName() + ":");
    FramePrinter.print(ctClass, System.out);
  }
}
