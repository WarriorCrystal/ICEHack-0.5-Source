package javassist.tools.web;

public class BadHttpRequest extends Exception {
  private Exception e;
  
  public BadHttpRequest() {
    this.e = null;
  }
  
  public BadHttpRequest(Exception paramException) {
    this.e = paramException;
  }
  
  public String toString() {
    if (this.e == null)
      return super.toString(); 
    return this.e.toString();
  }
}
