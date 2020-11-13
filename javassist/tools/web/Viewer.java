package javassist.tools.web;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;

public class Viewer extends ClassLoader {
  private String server;
  
  private int port;
  
  public static void main(String[] paramArrayOfString) throws Throwable {
    if (paramArrayOfString.length >= 3) {
      Viewer viewer = new Viewer(paramArrayOfString[0], Integer.parseInt(paramArrayOfString[1]));
      String[] arrayOfString = new String[paramArrayOfString.length - 3];
      System.arraycopy(paramArrayOfString, 3, arrayOfString, 0, paramArrayOfString.length - 3);
      viewer.run(paramArrayOfString[2], arrayOfString);
    } else {
      System.err.println("Usage: java javassist.tools.web.Viewer <host> <port> class [args ...]");
    } 
  }
  
  public Viewer(String paramString, int paramInt) {
    this.server = paramString;
    this.port = paramInt;
  }
  
  public String getServer() {
    return this.server;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public void run(String paramString, String[] paramArrayOfString) throws Throwable {
    Class<?> clazz = loadClass(paramString);
    try {
      clazz.getDeclaredMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { paramArrayOfString });
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } 
  }
  
  protected synchronized Class loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
    Class<?> clazz = findLoadedClass(paramString);
    if (clazz == null)
      clazz = findClass(paramString); 
    if (clazz == null)
      throw new ClassNotFoundException(paramString); 
    if (paramBoolean)
      resolveClass(clazz); 
    return clazz;
  }
  
  protected Class findClass(String paramString) throws ClassNotFoundException {
    Class<?> clazz = null;
    if (paramString.startsWith("java.") || paramString.startsWith("javax.") || paramString
      .equals("javassist.tools.web.Viewer"))
      clazz = findSystemClass(paramString); 
    if (clazz == null)
      try {
        byte[] arrayOfByte = fetchClass(paramString);
        if (arrayOfByte != null)
          clazz = defineClass(paramString, arrayOfByte, 0, arrayOfByte.length); 
      } catch (Exception exception) {} 
    return clazz;
  }
  
  protected byte[] fetchClass(String paramString) throws Exception {
    byte[] arrayOfByte;
    URL uRL = new URL("http", this.server, this.port, "/" + paramString.replace('.', '/') + ".class");
    URLConnection uRLConnection = uRL.openConnection();
    uRLConnection.connect();
    int i = uRLConnection.getContentLength();
    InputStream inputStream = uRLConnection.getInputStream();
    if (i <= 0) {
      arrayOfByte = readStream(inputStream);
    } else {
      arrayOfByte = new byte[i];
      int j = 0;
      do {
        int k = inputStream.read(arrayOfByte, j, i - j);
        if (k < 0) {
          inputStream.close();
          throw new IOException("the stream was closed: " + paramString);
        } 
        j += k;
      } while (j < i);
    } 
    inputStream.close();
    return arrayOfByte;
  }
  
  private byte[] readStream(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte1 = new byte[4096];
    int i = 0;
    int j = 0;
    do {
      i += j;
      if (arrayOfByte1.length - i <= 0) {
        byte[] arrayOfByte = new byte[arrayOfByte1.length * 2];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte, 0, i);
        arrayOfByte1 = arrayOfByte;
      } 
      j = paramInputStream.read(arrayOfByte1, i, arrayOfByte1.length - i);
    } while (j >= 0);
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
    return arrayOfByte2;
  }
}
