package javassist.tools.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.Translator;

public class Webserver {
  private ServerSocket socket;
  
  private ClassPool classPool;
  
  protected Translator translator;
  
  private static final byte[] endofline = new byte[] { 13, 10 };
  
  private static final int typeHtml = 1;
  
  private static final int typeClass = 2;
  
  private static final int typeGif = 3;
  
  private static final int typeJpeg = 4;
  
  private static final int typeText = 5;
  
  public String debugDir = null;
  
  public String htmlfileBase = null;
  
  public static void main(String[] paramArrayOfString) throws IOException {
    if (paramArrayOfString.length == 1) {
      Webserver webserver = new Webserver(paramArrayOfString[0]);
      webserver.run();
    } else {
      System.err.println("Usage: java javassist.tools.web.Webserver <port number>");
    } 
  }
  
  public Webserver(String paramString) throws IOException {
    this(Integer.parseInt(paramString));
  }
  
  public Webserver(int paramInt) throws IOException {
    this.socket = new ServerSocket(paramInt);
    this.classPool = null;
    this.translator = null;
  }
  
  public void setClassPool(ClassPool paramClassPool) {
    this.classPool = paramClassPool;
  }
  
  public void addTranslator(ClassPool paramClassPool, Translator paramTranslator) throws NotFoundException, CannotCompileException {
    this.classPool = paramClassPool;
    this.translator = paramTranslator;
    paramTranslator.start(this.classPool);
  }
  
  public void end() throws IOException {
    this.socket.close();
  }
  
  public void logging(String paramString) {
    System.out.println(paramString);
  }
  
  public void logging(String paramString1, String paramString2) {
    System.out.print(paramString1);
    System.out.print(" ");
    System.out.println(paramString2);
  }
  
  public void logging(String paramString1, String paramString2, String paramString3) {
    System.out.print(paramString1);
    System.out.print(" ");
    System.out.print(paramString2);
    System.out.print(" ");
    System.out.println(paramString3);
  }
  
  public void logging2(String paramString) {
    System.out.print("    ");
    System.out.println(paramString);
  }
  
  public void run() {
    System.err.println("ready to service...");
    while (true) {
      try {
        while (true) {
          Webserver1 webserver1 = new Webserver1(this, this.socket.accept());
          webserver1.start();
        } 
        break;
      } catch (IOException iOException) {
        logging(iOException.toString());
      } 
    } 
  }
  
  final void process(Socket paramSocket) throws IOException {
    BufferedInputStream bufferedInputStream = new BufferedInputStream(paramSocket.getInputStream());
    String str = readLine(bufferedInputStream);
    logging(paramSocket.getInetAddress().getHostName(), (new Date())
        .toString(), str);
    while (skipLine(bufferedInputStream) > 0);
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(paramSocket.getOutputStream());
    try {
      doReply(bufferedInputStream, bufferedOutputStream, str);
    } catch (BadHttpRequest badHttpRequest) {
      replyError(bufferedOutputStream, badHttpRequest);
    } 
    bufferedOutputStream.flush();
    bufferedInputStream.close();
    bufferedOutputStream.close();
    paramSocket.close();
  }
  
  private String readLine(InputStream paramInputStream) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    int i;
    while ((i = paramInputStream.read()) >= 0 && i != 13)
      stringBuffer.append((char)i); 
    paramInputStream.read();
    return stringBuffer.toString();
  }
  
  private int skipLine(InputStream paramInputStream) throws IOException {
    byte b = 0;
    int i;
    while ((i = paramInputStream.read()) >= 0 && i != 13)
      b++; 
    paramInputStream.read();
    return b;
  }
  
  public void doReply(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws IOException, BadHttpRequest {
    byte b;
    String str1, str2;
    if (paramString.startsWith("GET /")) {
      str1 = str2 = paramString.substring(5, paramString.indexOf(' ', 5));
    } else {
      throw new BadHttpRequest();
    } 
    if (str1.endsWith(".class")) {
      b = 2;
    } else if (str1.endsWith(".html") || str1.endsWith(".htm")) {
      b = 1;
    } else if (str1.endsWith(".gif")) {
      b = 3;
    } else if (str1.endsWith(".jpg")) {
      b = 4;
    } else {
      b = 5;
    } 
    int i = str1.length();
    if (b == 2 && 
      letUsersSendClassfile(paramOutputStream, str1, i))
      return; 
    checkFilename(str1, i);
    if (this.htmlfileBase != null)
      str1 = this.htmlfileBase + str1; 
    if (File.separatorChar != '/')
      str1 = str1.replace('/', File.separatorChar); 
    File file = new File(str1);
    if (file.canRead()) {
      sendHeader(paramOutputStream, file.length(), b);
      FileInputStream fileInputStream = new FileInputStream(file);
      byte[] arrayOfByte = new byte[4096];
      while (true) {
        i = fileInputStream.read(arrayOfByte);
        if (i <= 0)
          break; 
        paramOutputStream.write(arrayOfByte, 0, i);
      } 
      fileInputStream.close();
      return;
    } 
    if (b == 2) {
      InputStream inputStream = getClass().getResourceAsStream("/" + str2);
      if (inputStream != null) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte1 = new byte[4096];
        while (true) {
          i = inputStream.read(arrayOfByte1);
          if (i <= 0)
            break; 
          byteArrayOutputStream.write(arrayOfByte1, 0, i);
        } 
        byte[] arrayOfByte2 = byteArrayOutputStream.toByteArray();
        sendHeader(paramOutputStream, arrayOfByte2.length, 2);
        paramOutputStream.write(arrayOfByte2);
        inputStream.close();
        return;
      } 
    } 
    throw new BadHttpRequest();
  }
  
  private void checkFilename(String paramString, int paramInt) throws BadHttpRequest {
    for (byte b = 0; b < paramInt; b++) {
      char c = paramString.charAt(b);
      if (!Character.isJavaIdentifierPart(c) && c != '.' && c != '/')
        throw new BadHttpRequest(); 
    } 
    if (paramString.indexOf("..") >= 0)
      throw new BadHttpRequest(); 
  }
  
  private boolean letUsersSendClassfile(OutputStream paramOutputStream, String paramString, int paramInt) throws IOException, BadHttpRequest {
    byte[] arrayOfByte;
    if (this.classPool == null)
      return false; 
    String str = paramString.substring(0, paramInt - 6).replace('/', '.');
    try {
      if (this.translator != null)
        this.translator.onLoad(this.classPool, str); 
      CtClass ctClass = this.classPool.get(str);
      arrayOfByte = ctClass.toBytecode();
      if (this.debugDir != null)
        ctClass.writeFile(this.debugDir); 
    } catch (Exception exception) {
      throw new BadHttpRequest(exception);
    } 
    sendHeader(paramOutputStream, arrayOfByte.length, 2);
    paramOutputStream.write(arrayOfByte);
    return true;
  }
  
  private void sendHeader(OutputStream paramOutputStream, long paramLong, int paramInt) throws IOException {
    paramOutputStream.write("HTTP/1.0 200 OK".getBytes());
    paramOutputStream.write(endofline);
    paramOutputStream.write("Content-Length: ".getBytes());
    paramOutputStream.write(Long.toString(paramLong).getBytes());
    paramOutputStream.write(endofline);
    if (paramInt == 2) {
      paramOutputStream.write("Content-Type: application/octet-stream".getBytes());
    } else if (paramInt == 1) {
      paramOutputStream.write("Content-Type: text/html".getBytes());
    } else if (paramInt == 3) {
      paramOutputStream.write("Content-Type: image/gif".getBytes());
    } else if (paramInt == 4) {
      paramOutputStream.write("Content-Type: image/jpg".getBytes());
    } else if (paramInt == 5) {
      paramOutputStream.write("Content-Type: text/plain".getBytes());
    } 
    paramOutputStream.write(endofline);
    paramOutputStream.write(endofline);
  }
  
  private void replyError(OutputStream paramOutputStream, BadHttpRequest paramBadHttpRequest) throws IOException {
    logging2("bad request: " + paramBadHttpRequest.toString());
    paramOutputStream.write("HTTP/1.0 400 Bad Request".getBytes());
    paramOutputStream.write(endofline);
    paramOutputStream.write(endofline);
    paramOutputStream.write("<H1>Bad Request</H1>".getBytes());
  }
}
