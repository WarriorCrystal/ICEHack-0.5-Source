package javassist.tools.web;

import java.io.IOException;
import java.net.Socket;

class Webserver1 extends Thread {
  Webserver web;
  
  Socket sock;
  
  public Webserver1(Webserver paramWebserver, Socket paramSocket) {
    this.web = paramWebserver;
    this.sock = paramSocket;
  }
  
  public void run() {
    try {
      this.web.process(this.sock);
    } catch (IOException iOException) {}
  }
}
