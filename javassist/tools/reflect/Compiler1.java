package javassist.tools.reflect;

import java.io.PrintStream;
import javassist.ClassPool;
import javassist.CtClass;

public class Compiler1 {
  public static void main(String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString.length == 0) {
      help(System.err);
      return;
    } 
    Compiler[] arrayOfCompiler = new Compiler[paramArrayOfString.length];
    int i = parse(paramArrayOfString, arrayOfCompiler);
    if (i < 1) {
      System.err.println("bad parameter.");
      return;
    } 
    processClasses(arrayOfCompiler, i);
  }
  
  private static void processClasses(Compiler[] paramArrayOfCompiler, int paramInt) throws Exception {
    Reflection reflection = new Reflection();
    ClassPool classPool = ClassPool.getDefault();
    reflection.start(classPool);
    byte b;
    for (b = 0; b < paramInt; b++) {
      CtClass ctClass = classPool.get((paramArrayOfCompiler[b]).classname);
      if ((paramArrayOfCompiler[b]).metaobject != null || (paramArrayOfCompiler[b]).classobject != null) {
        String str1, str2;
        if ((paramArrayOfCompiler[b]).metaobject == null) {
          str1 = "javassist.tools.reflect.Metaobject";
        } else {
          str1 = (paramArrayOfCompiler[b]).metaobject;
        } 
        if ((paramArrayOfCompiler[b]).classobject == null) {
          str2 = "javassist.tools.reflect.ClassMetaobject";
        } else {
          str2 = (paramArrayOfCompiler[b]).classobject;
        } 
        if (!reflection.makeReflective(ctClass, classPool.get(str1), classPool
            .get(str2)))
          System.err.println("Warning: " + ctClass.getName() + " is reflective.  It was not changed."); 
        System.err.println(ctClass.getName() + ": " + str1 + ", " + str2);
      } else {
        System.err.println(ctClass.getName() + ": not reflective");
      } 
    } 
    for (b = 0; b < paramInt; b++) {
      reflection.onLoad(classPool, (paramArrayOfCompiler[b]).classname);
      classPool.get((paramArrayOfCompiler[b]).classname).writeFile();
    } 
  }
  
  private static int parse(String[] paramArrayOfString, Compiler[] paramArrayOfCompiler) {
    byte b = -1;
    for (byte b1 = 0; b1 < paramArrayOfString.length; b1++) {
      String str = paramArrayOfString[b1];
      if (str.equals("-m")) {
        if (b < 0 || b1 + 1 > paramArrayOfString.length)
          return -1; 
        (paramArrayOfCompiler[b]).metaobject = paramArrayOfString[++b1];
      } else if (str.equals("-c")) {
        if (b < 0 || b1 + 1 > paramArrayOfString.length)
          return -1; 
        (paramArrayOfCompiler[b]).classobject = paramArrayOfString[++b1];
      } else {
        if (str.charAt(0) == '-')
          return -1; 
        Compiler compiler = new Compiler();
        compiler.classname = str;
        compiler.metaobject = null;
        compiler.classobject = null;
        paramArrayOfCompiler[++b] = compiler;
      } 
    } 
    return b + 1;
  }
  
  private static void help(PrintStream paramPrintStream) {
    paramPrintStream.println("Usage: java javassist.tools.reflect.Compiler");
    paramPrintStream.println("            (<class> [-m <metaobject>] [-c <class metaobject>])+");
  }
}
