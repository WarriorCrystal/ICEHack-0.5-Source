package javassist.tools.reflect;

import java.lang.reflect.InvocationTargetException;

public class CannotInvokeException extends RuntimeException {
  private Throwable err = null;
  
  public Throwable getReason() {
    return this.err;
  }
  
  public CannotInvokeException(String paramString) {
    super(paramString);
  }
  
  public CannotInvokeException(InvocationTargetException paramInvocationTargetException) {
    super("by " + paramInvocationTargetException.getTargetException().toString());
    this.err = paramInvocationTargetException.getTargetException();
  }
  
  public CannotInvokeException(IllegalAccessException paramIllegalAccessException) {
    super("by " + paramIllegalAccessException.toString());
    this.err = paramIllegalAccessException;
  }
  
  public CannotInvokeException(ClassNotFoundException paramClassNotFoundException) {
    super("by " + paramClassNotFoundException.toString());
    this.err = paramClassNotFoundException;
  }
}
