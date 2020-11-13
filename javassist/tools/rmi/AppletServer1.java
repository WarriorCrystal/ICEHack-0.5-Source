package javassist.tools.rmi;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.tools.web.BadHttpRequest;
import javassist.tools.web.Webserver;

public class AppletServer1 extends Webserver {
  private StubGenerator stubGen;
  
  private Hashtable exportedNames;
  
  private Vector exportedObjects;
  
  private static final byte[] okHeader = "HTTP/1.0 200 OK\r\n\r\n"
    .getBytes();
  
  public AppletServer1(String paramString) throws IOException, NotFoundException, CannotCompileException {
    this(Integer.parseInt(paramString));
  }
  
  public AppletServer1(int paramInt) throws IOException, NotFoundException, CannotCompileException {
    this(ClassPool.getDefault(), new StubGenerator(), paramInt);
  }
  
  public AppletServer1(int paramInt, ClassPool paramClassPool) throws IOException, NotFoundException, CannotCompileException {
    this(new ClassPool(paramClassPool), new StubGenerator(), paramInt);
  }
  
  private AppletServer1(ClassPool paramClassPool, StubGenerator paramStubGenerator, int paramInt) throws IOException, NotFoundException, CannotCompileException {
    super(paramInt);
    this.exportedNames = new Hashtable<Object, Object>();
    this.exportedObjects = new Vector();
    this.stubGen = paramStubGenerator;
    addTranslator(paramClassPool, paramStubGenerator);
  }
  
  public void run() {
    super.run();
  }
  
  public synchronized int exportObject(String paramString, Object paramObject) throws CannotCompileException {
    Class<?> clazz = paramObject.getClass();
    AppletServer appletServer = new AppletServer();
    appletServer.object = paramObject;
    appletServer.methods = clazz.getMethods();
    this.exportedObjects.addElement(appletServer);
    appletServer.identifier = this.exportedObjects.size() - 1;
    if (paramString != null)
      this.exportedNames.put(paramString, appletServer); 
    try {
      this.stubGen.makeProxyClass(clazz);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
    return appletServer.identifier;
  }
  
  public void doReply(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws IOException, BadHttpRequest {
    if (paramString.startsWith("POST /rmi ")) {
      processRMI(paramInputStream, paramOutputStream);
    } else if (paramString.startsWith("POST /lookup ")) {
      lookupName(paramString, paramInputStream, paramOutputStream);
    } else {
      super.doReply(paramInputStream, paramOutputStream, paramString);
    } 
  }
  
  private void processRMI(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    ObjectInputStream objectInputStream = new ObjectInputStream(paramInputStream);
    int i = objectInputStream.readInt();
    int j = objectInputStream.readInt();
    Exception exception = null;
    Object object = null;
    try {
      AppletServer appletServer = this.exportedObjects.elementAt(i);
      Object[] arrayOfObject = readParameters(objectInputStream);
      object = convertRvalue(appletServer.methods[j].invoke(appletServer.object, arrayOfObject));
    } catch (Exception exception1) {
      exception = exception1;
      logging2(exception1.toString());
    } 
    paramOutputStream.write(okHeader);
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(paramOutputStream);
    if (exception != null) {
      objectOutputStream.writeBoolean(false);
      objectOutputStream.writeUTF(exception.toString());
    } else {
      try {
        objectOutputStream.writeBoolean(true);
        objectOutputStream.writeObject(object);
      } catch (NotSerializableException notSerializableException) {
        logging2(notSerializableException.toString());
      } catch (InvalidClassException invalidClassException) {
        logging2(invalidClassException.toString());
      } 
    } 
    objectOutputStream.flush();
    objectOutputStream.close();
    objectInputStream.close();
  }
  
  private Object[] readParameters(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    int i = paramObjectInputStream.readInt();
    Object[] arrayOfObject = new Object[i];
    for (byte b = 0; b < i; b++) {
      Object object = paramObjectInputStream.readObject();
      if (object instanceof RemoteRef) {
        RemoteRef remoteRef = (RemoteRef)object;
        AppletServer appletServer = this.exportedObjects.elementAt(remoteRef.oid);
        object = appletServer.object;
      } 
      arrayOfObject[b] = object;
    } 
    return arrayOfObject;
  }
  
  private Object convertRvalue(Object paramObject) throws CannotCompileException {
    if (paramObject == null)
      return null; 
    String str = paramObject.getClass().getName();
    if (this.stubGen.isProxyClass(str))
      return new RemoteRef(exportObject((String)null, paramObject), str); 
    return paramObject;
  }
  
  private void lookupName(String paramString, InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    ObjectInputStream objectInputStream = new ObjectInputStream(paramInputStream);
    String str = DataInputStream.readUTF(objectInputStream);
    AppletServer appletServer = (AppletServer)this.exportedNames.get(str);
    paramOutputStream.write(okHeader);
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(paramOutputStream);
    if (appletServer == null) {
      logging2(str + "not found.");
      objectOutputStream.writeInt(-1);
      objectOutputStream.writeUTF("error");
    } else {
      logging2(str);
      objectOutputStream.writeInt(appletServer.identifier);
      objectOutputStream.writeUTF(appletServer.object.getClass().getName());
    } 
    objectOutputStream.flush();
    objectOutputStream.close();
    objectInputStream.close();
  }
}
