package javassist;

public class NotFoundException extends Exception {
  public NotFoundException(String paramString) {
    super(paramString);
  }
  
  public NotFoundException(String paramString, Exception paramException) {
    super(paramString + " because of " + paramException.toString());
  }
}
