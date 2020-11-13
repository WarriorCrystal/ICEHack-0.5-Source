package javassist.util.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ProxyObjectInputStream extends ObjectInputStream {
  private ClassLoader loader;
  
  public ProxyObjectInputStream(InputStream paramInputStream) throws IOException {
    super(paramInputStream);
    this.loader = Thread.currentThread().getContextClassLoader();
    if (this.loader == null)
      this.loader = ClassLoader.getSystemClassLoader(); 
  }
  
  public void setClassLoader(ClassLoader paramClassLoader) {
    if (paramClassLoader != null) {
      this.loader = paramClassLoader;
    } else {
      paramClassLoader = ClassLoader.getSystemClassLoader();
    } 
  }
  
  protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
    boolean bool = readBoolean();
    if (bool) {
      String str = (String)readObject();
      Class<?> clazz1 = this.loader.loadClass(str);
      int i = readInt();
      Class[] arrayOfClass = new Class[i];
      for (byte b = 0; b < i; b++) {
        str = (String)readObject();
        arrayOfClass[b] = this.loader.loadClass(str);
      } 
      i = readInt();
      byte[] arrayOfByte = new byte[i];
      read(arrayOfByte);
      ProxyFactory proxyFactory = new ProxyFactory();
      proxyFactory.setUseCache(true);
      proxyFactory.setUseWriteReplace(false);
      proxyFactory.setSuperclass(clazz1);
      proxyFactory.setInterfaces(arrayOfClass);
      Class<?> clazz2 = proxyFactory.createClass(arrayOfByte);
      return ObjectStreamClass.lookup(clazz2);
    } 
    return super.readClassDescriptor();
  }
}
