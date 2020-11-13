package javassist.util.proxy;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

public class ProxyObjectOutputStream extends ObjectOutputStream {
  public ProxyObjectOutputStream(OutputStream paramOutputStream) throws IOException {
    super(paramOutputStream);
  }
  
  protected void writeClassDescriptor(ObjectStreamClass paramObjectStreamClass) throws IOException {
    Class<?> clazz = paramObjectStreamClass.forClass();
    if (ProxyFactory.isProxyClass(clazz)) {
      writeBoolean(true);
      Class<?> clazz1 = clazz.getSuperclass();
      Class[] arrayOfClass = clazz.getInterfaces();
      byte[] arrayOfByte = ProxyFactory.getFilterSignature(clazz);
      String str = clazz1.getName();
      writeObject(str);
      writeInt(arrayOfClass.length - 1);
      for (byte b = 0; b < arrayOfClass.length; b++) {
        Class<ProxyObject> clazz2 = arrayOfClass[b];
        if (clazz2 != ProxyObject.class && clazz2 != Proxy.class) {
          str = arrayOfClass[b].getName();
          writeObject(str);
        } 
      } 
      writeInt(arrayOfByte.length);
      write(arrayOfByte);
    } else {
      writeBoolean(false);
      super.writeClassDescriptor(paramObjectStreamClass);
    } 
  }
}
