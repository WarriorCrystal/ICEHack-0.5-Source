package javassist.bytecode;

public class BadBytecode extends Exception {
  public BadBytecode(int paramInt) {
    super("bytecode " + paramInt);
  }
  
  public BadBytecode(String paramString) {
    super(paramString);
  }
  
  public BadBytecode(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public BadBytecode(MethodInfo paramMethodInfo, Throwable paramThrowable) {
    super(paramMethodInfo.toString() + " in " + paramMethodInfo
        .getConstPool().getClassName() + ": " + paramThrowable
        .getMessage(), paramThrowable);
  }
}
