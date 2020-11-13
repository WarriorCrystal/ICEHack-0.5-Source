package javassist.runtime;

public class Cflow extends ThreadLocal {
  private static class Depth {
    private int depth = 0;
    
    int get() {
      return this.depth;
    }
    
    void inc() {
      this.depth++;
    }
    
    void dec() {
      this.depth--;
    }
  }
  
  protected synchronized Object initialValue() {
    return new Depth();
  }
  
  public void enter() {
    ((Depth)get()).inc();
  }
  
  public void exit() {
    ((Depth)get()).dec();
  }
  
  public int value() {
    return ((Depth)get()).get();
  }
}
