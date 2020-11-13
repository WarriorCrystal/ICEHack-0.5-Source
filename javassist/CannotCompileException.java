package javassist;

import javassist.compiler.CompileError;

public class CannotCompileException extends Exception {
  private Throwable myCause;
  
  private String message;
  
  public Throwable getCause() {
    return (this.myCause == this) ? null : this.myCause;
  }
  
  public synchronized Throwable initCause(Throwable paramThrowable) {
    this.myCause = paramThrowable;
    return this;
  }
  
  public String getReason() {
    if (this.message != null)
      return this.message; 
    return toString();
  }
  
  public CannotCompileException(String paramString) {
    super(paramString);
    this.message = paramString;
    initCause(null);
  }
  
  public CannotCompileException(Throwable paramThrowable) {
    super("by " + paramThrowable.toString());
    this.message = null;
    initCause(paramThrowable);
  }
  
  public CannotCompileException(String paramString, Throwable paramThrowable) {
    this(paramString);
    initCause(paramThrowable);
  }
  
  public CannotCompileException(NotFoundException paramNotFoundException) {
    this("cannot find " + paramNotFoundException.getMessage(), paramNotFoundException);
  }
  
  public CannotCompileException(CompileError paramCompileError) {
    this("[source error] " + paramCompileError.getMessage(), (Throwable)paramCompileError);
  }
  
  public CannotCompileException(ClassNotFoundException paramClassNotFoundException, String paramString) {
    this("cannot find " + paramString, paramClassNotFoundException);
  }
  
  public CannotCompileException(ClassFormatError paramClassFormatError, String paramString) {
    this("invalid class format: " + paramString, paramClassFormatError);
  }
}
