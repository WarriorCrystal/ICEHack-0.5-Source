package javassist.tools.rmi;

public class ObjectNotFoundException extends Exception {
  public ObjectNotFoundException(String paramString) {
    super(paramString + " is not exported");
  }
  
  public ObjectNotFoundException(String paramString, Exception paramException) {
    super(paramString + " because of " + paramException.toString());
  }
}
