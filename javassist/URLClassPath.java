package javassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class URLClassPath implements ClassPath {
  protected String hostname;
  
  protected int port;
  
  protected String directory;
  
  protected String packageName;
  
  public URLClassPath(String paramString1, int paramInt, String paramString2, String paramString3) {
    this.hostname = paramString1;
    this.port = paramInt;
    this.directory = paramString2;
    this.packageName = paramString3;
  }
  
  public String toString() {
    return this.hostname + ":" + this.port + this.directory;
  }
  
  public InputStream openClassfile(String paramString) {
    try {
      URLConnection uRLConnection = openClassfile0(paramString);
      if (uRLConnection != null)
        return uRLConnection.getInputStream(); 
    } catch (IOException iOException) {}
    return null;
  }
  
  private URLConnection openClassfile0(String paramString) throws IOException {
    if (this.packageName == null || paramString.startsWith(this.packageName)) {
      String str = this.directory + paramString.replace('.', '/') + ".class";
      return fetchClass0(this.hostname, this.port, str);
    } 
    return null;
  }
  
  public URL find(String paramString) {
    try {
      URLConnection uRLConnection = openClassfile0(paramString);
      InputStream inputStream = uRLConnection.getInputStream();
      if (inputStream != null) {
        inputStream.close();
        return uRLConnection.getURL();
      } 
    } catch (IOException iOException) {}
    return null;
  }
  
  public void close() {}
  
  public static byte[] fetchClass(String paramString1, int paramInt, String paramString2, String paramString3) throws IOException {
    byte[] arrayOfByte;
    URLConnection uRLConnection = fetchClass0(paramString1, paramInt, paramString2 + paramString3
        .replace('.', '/') + ".class");
    int i = uRLConnection.getContentLength();
    InputStream inputStream = uRLConnection.getInputStream();
    try {
      if (i <= 0) {
        arrayOfByte = ClassPoolTail2.readStream(inputStream);
      } else {
        arrayOfByte = new byte[i];
        int j = 0;
        do {
          int k = inputStream.read(arrayOfByte, j, i - j);
          if (k < 0)
            throw new IOException("the stream was closed: " + paramString3); 
          j += k;
        } while (j < i);
      } 
    } finally {
      inputStream.close();
    } 
    return arrayOfByte;
  }
  
  private static URLConnection fetchClass0(String paramString1, int paramInt, String paramString2) throws IOException {
    URL uRL;
    try {
      uRL = new URL("http", paramString1, paramInt, paramString2);
    } catch (MalformedURLException malformedURLException) {
      throw new IOException("invalid URL?");
    } 
    URLConnection uRLConnection = uRL.openConnection();
    uRLConnection.connect();
    return uRLConnection;
  }
}
