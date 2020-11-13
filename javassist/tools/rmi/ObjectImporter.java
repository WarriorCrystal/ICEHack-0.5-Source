package javassist.tools.rmi;

import java.applet.Applet;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.net.URL;

public class ObjectImporter implements Serializable {
  private final byte[] endofline = new byte[] { 13, 10 };
  
  private String servername;
  
  private String orgServername;
  
  private int port;
  
  private int orgPort;
  
  protected byte[] lookupCommand = "POST /lookup HTTP/1.0".getBytes();
  
  protected byte[] rmiCommand = "POST /rmi HTTP/1.0".getBytes();
  
  public ObjectImporter(Applet paramApplet) {
    URL uRL = paramApplet.getCodeBase();
    this.orgServername = this.servername = uRL.getHost();
    this.orgPort = this.port = uRL.getPort();
  }
  
  public ObjectImporter(String paramString, int paramInt) {
    this.orgServername = this.servername = paramString;
    this.orgPort = this.port = paramInt;
  }
  
  public Object getObject(String paramString) {
    try {
      return lookupObject(paramString);
    } catch (ObjectNotFoundException objectNotFoundException) {
      return null;
    } 
  }
  
  public void setHttpProxy(String paramString, int paramInt) {
    String str1 = "POST http://" + this.orgServername + ":" + this.orgPort;
    String str2 = str1 + "/lookup HTTP/1.0";
    this.lookupCommand = str2.getBytes();
    str2 = str1 + "/rmi HTTP/1.0";
    this.rmiCommand = str2.getBytes();
    this.servername = paramString;
    this.port = paramInt;
  }
  
  public Object lookupObject(String paramString) throws ObjectNotFoundException {
    try {
      Socket socket = new Socket(this.servername, this.port);
      OutputStream outputStream = socket.getOutputStream();
      outputStream.write(this.lookupCommand);
      outputStream.write(this.endofline);
      outputStream.write(this.endofline);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeUTF(paramString);
      objectOutputStream.flush();
      BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
      skipHeader(bufferedInputStream);
      ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
      int i = objectInputStream.readInt();
      String str = objectInputStream.readUTF();
      objectInputStream.close();
      objectOutputStream.close();
      socket.close();
      if (i >= 0)
        return createProxy(i, str); 
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new ObjectNotFoundException(paramString, exception);
    } 
    throw new ObjectNotFoundException(paramString);
  }
  
  private static final Class[] proxyConstructorParamTypes = new Class[] { ObjectImporter.class, int.class };
  
  private Object createProxy(int paramInt, String paramString) throws Exception {
    Class<?> clazz = Class.forName(paramString);
    Constructor<?> constructor = clazz.getConstructor(proxyConstructorParamTypes);
    return constructor.newInstance(new Object[] { this, new Integer(paramInt) });
  }
  
  public Object call(int paramInt1, int paramInt2, Object[] paramArrayOfObject) throws RemoteException {
    boolean bool;
    Object object;
    String str;
    try {
      Socket socket = new Socket(this.servername, this.port);
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
      bufferedOutputStream.write(this.rmiCommand);
      bufferedOutputStream.write(this.endofline);
      bufferedOutputStream.write(this.endofline);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
      objectOutputStream.writeInt(paramInt1);
      objectOutputStream.writeInt(paramInt2);
      writeParameters(objectOutputStream, paramArrayOfObject);
      objectOutputStream.flush();
      BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
      skipHeader(bufferedInputStream);
      ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
      bool = objectInputStream.readBoolean();
      object = null;
      str = null;
      if (bool) {
        object = objectInputStream.readObject();
      } else {
        str = objectInputStream.readUTF();
      } 
      objectInputStream.close();
      objectOutputStream.close();
      socket.close();
      if (object instanceof RemoteRef) {
        RemoteRef remoteRef = (RemoteRef)object;
        object = createProxy(remoteRef.oid, remoteRef.classname);
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RemoteException(classNotFoundException);
    } catch (IOException iOException) {
      throw new RemoteException(iOException);
    } catch (Exception exception) {
      throw new RemoteException(exception);
    } 
    if (bool)
      return object; 
    throw new RemoteException(str);
  }
  
  private void skipHeader(InputStream paramInputStream) throws IOException {
    byte b;
    do {
      b = 0;
      int i;
      while ((i = paramInputStream.read()) >= 0 && i != 13)
        b++; 
      paramInputStream.read();
    } while (b > 0);
  }
  
  private void writeParameters(ObjectOutputStream paramObjectOutputStream, Object[] paramArrayOfObject) throws IOException {
    int i = paramArrayOfObject.length;
    paramObjectOutputStream.writeInt(i);
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfObject[b] instanceof Proxy) {
        Proxy proxy = (Proxy)paramArrayOfObject[b];
        paramObjectOutputStream.writeObject(new RemoteRef(proxy._getObjectId()));
      } else {
        paramObjectOutputStream.writeObject(paramArrayOfObject[b]);
      } 
    } 
  }
}
