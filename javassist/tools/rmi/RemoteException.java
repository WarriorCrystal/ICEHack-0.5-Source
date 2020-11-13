package javassist.tools.rmi;

public class RemoteException extends RuntimeException {
  public RemoteException(String paramString) {
    super(paramString);
  }
  
  public RemoteException(Exception paramException) {
    super("by " + paramException.toString());
  }
}
