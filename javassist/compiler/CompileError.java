package javassist.compiler;

import javassist.CannotCompileException;
import javassist.NotFoundException;

public class CompileError extends Exception {
  private Lex lex;
  
  private String reason;
  
  public CompileError(String paramString, Lex paramLex) {
    this.reason = paramString;
    this.lex = paramLex;
  }
  
  public CompileError(String paramString) {
    this.reason = paramString;
    this.lex = null;
  }
  
  public CompileError(CannotCompileException paramCannotCompileException) {
    this(paramCannotCompileException.getReason());
  }
  
  public CompileError(NotFoundException paramNotFoundException) {
    this("cannot find " + paramNotFoundException.getMessage());
  }
  
  public Lex getLex() {
    return this.lex;
  }
  
  public String getMessage() {
    return this.reason;
  }
  
  public String toString() {
    return "compile error: " + this.reason;
  }
}
