package javassist.tools.rmi;

public class Sample {
  private ObjectImporter importer;
  
  private int objectId;
  
  public Object forward(Object[] paramArrayOfObject, int paramInt) {
    return this.importer.call(this.objectId, paramInt, paramArrayOfObject);
  }
  
  public static Object forwardStatic(Object[] paramArrayOfObject, int paramInt) throws RemoteException {
    throw new RemoteException("cannot call a static method.");
  }
}
