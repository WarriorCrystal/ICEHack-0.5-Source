package javassist.bytecode.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Subroutine {
  private List callers = new ArrayList();
  
  private Set access = new HashSet();
  
  private int start;
  
  public Subroutine(int paramInt1, int paramInt2) {
    this.start = paramInt1;
    this.callers.add(new Integer(paramInt2));
  }
  
  public void addCaller(int paramInt) {
    this.callers.add(new Integer(paramInt));
  }
  
  public int start() {
    return this.start;
  }
  
  public void access(int paramInt) {
    this.access.add(new Integer(paramInt));
  }
  
  public boolean isAccessed(int paramInt) {
    return this.access.contains(new Integer(paramInt));
  }
  
  public Collection accessed() {
    return this.access;
  }
  
  public Collection callers() {
    return this.callers;
  }
  
  public String toString() {
    return "start = " + this.start + " callers = " + this.callers.toString();
  }
}
