package javassist.tools.reflect;

public class CannotCreateException extends Exception {
  public CannotCreateException(String paramString) {
    super(paramString);
  }
  
  public CannotCreateException(Exception paramException) {
    super("by " + paramException.toString());
  }
}
