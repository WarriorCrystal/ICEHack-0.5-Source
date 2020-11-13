package javassist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

final class ClassPoolTail2 {
  protected ClassPoolTail1 pathList = null;
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[class path: ");
    ClassPoolTail1 classPoolTail1 = this.pathList;
    while (classPoolTail1 != null) {
      stringBuffer.append(classPoolTail1.path.toString());
      stringBuffer.append(File.pathSeparatorChar);
      classPoolTail1 = classPoolTail1.next;
    } 
    stringBuffer.append(']');
    return stringBuffer.toString();
  }
  
  public synchronized ClassPath insertClassPath(ClassPath paramClassPath) {
    this.pathList = new ClassPoolTail1(paramClassPath, this.pathList);
    return paramClassPath;
  }
  
  public synchronized ClassPath appendClassPath(ClassPath paramClassPath) {
    ClassPoolTail1 classPoolTail11 = new ClassPoolTail1(paramClassPath, null);
    ClassPoolTail1 classPoolTail12 = this.pathList;
    if (classPoolTail12 == null) {
      this.pathList = classPoolTail11;
    } else {
      while (classPoolTail12.next != null)
        classPoolTail12 = classPoolTail12.next; 
      classPoolTail12.next = classPoolTail11;
    } 
    return paramClassPath;
  }
  
  public synchronized void removeClassPath(ClassPath paramClassPath) {
    ClassPoolTail1 classPoolTail1 = this.pathList;
    if (classPoolTail1 != null)
      if (classPoolTail1.path == paramClassPath) {
        this.pathList = classPoolTail1.next;
      } else {
        while (classPoolTail1.next != null) {
          if (classPoolTail1.next.path == paramClassPath) {
            classPoolTail1.next = classPoolTail1.next.next;
            continue;
          } 
          classPoolTail1 = classPoolTail1.next;
        } 
      }  
    paramClassPath.close();
  }
  
  public ClassPath appendSystemPath() {
    return appendClassPath(new ClassClassPath());
  }
  
  public ClassPath insertClassPath(String paramString) throws NotFoundException {
    return insertClassPath(makePathObject(paramString));
  }
  
  public ClassPath appendClassPath(String paramString) throws NotFoundException {
    return appendClassPath(makePathObject(paramString));
  }
  
  private static ClassPath makePathObject(String paramString) throws NotFoundException {
    String str = paramString.toLowerCase();
    if (str.endsWith(".jar") || str.endsWith(".zip"))
      return new ClassPoolTail3(paramString); 
    int i = paramString.length();
    if (i > 2 && paramString.charAt(i - 1) == '*' && (paramString
      .charAt(i - 2) == '/' || paramString
      .charAt(i - 2) == File.separatorChar)) {
      String str1 = paramString.substring(0, i - 2);
      return new ClassPoolTail(str1);
    } 
    return new ClassPoolTail4(paramString);
  }
  
  void writeClassfile(String paramString, OutputStream paramOutputStream) throws NotFoundException, IOException, CannotCompileException {
    InputStream inputStream = openClassfile(paramString);
    if (inputStream == null)
      throw new NotFoundException(paramString); 
    try {
      copyStream(inputStream, paramOutputStream);
    } finally {
      inputStream.close();
    } 
  }
  
  InputStream openClassfile(String paramString) throws NotFoundException {
    ClassPoolTail1 classPoolTail1 = this.pathList;
    InputStream inputStream = null;
    NotFoundException notFoundException = null;
    while (classPoolTail1 != null) {
      try {
        inputStream = classPoolTail1.path.openClassfile(paramString);
      } catch (NotFoundException notFoundException1) {
        if (notFoundException == null)
          notFoundException = notFoundException1; 
      } 
      if (inputStream == null) {
        classPoolTail1 = classPoolTail1.next;
        continue;
      } 
      return inputStream;
    } 
    if (notFoundException != null)
      throw notFoundException; 
    return null;
  }
  
  public URL find(String paramString) {
    ClassPoolTail1 classPoolTail1 = this.pathList;
    URL uRL = null;
    while (classPoolTail1 != null) {
      uRL = classPoolTail1.path.find(paramString);
      if (uRL == null) {
        classPoolTail1 = classPoolTail1.next;
        continue;
      } 
      return uRL;
    } 
    return null;
  }
  
  public static byte[] readStream(InputStream paramInputStream) throws IOException {
    byte[][] arrayOfByte = new byte[8][];
    int i = 4096;
    for (byte b = 0; b < 8; b++) {
      arrayOfByte[b] = new byte[i];
      int j = 0;
      int k = 0;
      while (true) {
        k = paramInputStream.read(arrayOfByte[b], j, i - j);
        if (k >= 0) {
          j += k;
        } else {
          byte[] arrayOfByte1 = new byte[i - 4096 + j];
          int m = 0;
          for (byte b1 = 0; b1 < b; b1++) {
            System.arraycopy(arrayOfByte[b1], 0, arrayOfByte1, m, m + 4096);
            m = m + m + 4096;
          } 
          System.arraycopy(arrayOfByte[b], 0, arrayOfByte1, m, j);
          return arrayOfByte1;
        } 
        if (j >= i) {
          i *= 2;
          break;
        } 
      } 
    } 
    throw new IOException("too much data");
  }
  
  public static void copyStream(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    int i = 4096;
    byte[] arrayOfByte = null;
    for (byte b = 0; b < 64; b++) {
      if (b < 8) {
        i *= 2;
        arrayOfByte = new byte[i];
      } 
      int j = 0;
      int k = 0;
      while (true) {
        k = paramInputStream.read(arrayOfByte, j, i - j);
        if (k >= 0) {
          j += k;
        } else {
          paramOutputStream.write(arrayOfByte, 0, j);
          return;
        } 
        if (j >= i) {
          paramOutputStream.write(arrayOfByte);
          break;
        } 
      } 
    } 
    throw new IOException("too much data");
  }
}
