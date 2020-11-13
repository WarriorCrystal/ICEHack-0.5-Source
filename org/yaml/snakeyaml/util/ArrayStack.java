package org.yaml.snakeyaml.util;

import java.util.ArrayList;

public class ArrayStack<T> {
  private ArrayList<T> stack;
  
  public ArrayStack(int paramInt) {
    this.stack = new ArrayList<T>(paramInt);
  }
  
  public void push(T paramT) {
    this.stack.add(paramT);
  }
  
  public T pop() {
    return this.stack.remove(this.stack.size() - 1);
  }
  
  public boolean isEmpty() {
    return this.stack.isEmpty();
  }
  
  public void clear() {
    this.stack.clear();
  }
}
