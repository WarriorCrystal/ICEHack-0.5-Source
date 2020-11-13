package javassist.util.proxy;

import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

class SerializedProxy implements Serializable {
  private String superClass;
  
  private String[] interfaces;
  
  private byte[] filterSignature;
  
  private MethodHandler handler;
  
  SerializedProxy(Class paramClass, byte[] paramArrayOfbyte, MethodHandler paramMethodHandler) {
    this.filterSignature = paramArrayOfbyte;
    this.handler = paramMethodHandler;
    this.superClass = paramClass.getSuperclass().getName();
    Class[] arrayOfClass = paramClass.getInterfaces();
    int i = arrayOfClass.length;
    this.interfaces = new String[i - 1];
    String str1 = ProxyObject.class.getName();
    String str2 = Proxy.class.getName();
    for (byte b = 0; b < i; b++) {
      String str = arrayOfClass[b].getName();
      if (!str.equals(str1) && !str.equals(str2))
        this.interfaces[b] = str; 
    } 
  }
  
  protected Class loadClass(final String className) throws ClassNotFoundException {
    try {
      return AccessController.<Class<?>>doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
            public Object run() throws Exception {
              ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
              return Class.forName(className, true, classLoader);
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new RuntimeException("cannot load the class: " + className, privilegedActionException.getException());
    } 
  }
  
  Object readResolve() throws ObjectStreamException {
    try {
      int i = this.interfaces.length;
      Class[] arrayOfClass = new Class[i];
      for (byte b = 0; b < i; b++)
        arrayOfClass[b] = loadClass(this.interfaces[b]); 
      ProxyFactory proxyFactory = new ProxyFactory();
      proxyFactory.setSuperclass(loadClass(this.superClass));
      proxyFactory.setInterfaces(arrayOfClass);
      Proxy proxy = proxyFactory.createClass(this.filterSignature).newInstance();
      proxy.setHandler(this.handler);
      return proxy;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new InvalidClassException(classNotFoundException.getMessage());
    } catch (InstantiationException instantiationException) {
      throw new InvalidObjectException(instantiationException.getMessage());
    } catch (IllegalAccessException illegalAccessException) {
      throw new InvalidClassException(illegalAccessException.getMessage());
    } 
  }
}
