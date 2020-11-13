package me.fluffycq.icehack.util;

public class Pair<T, S> {
  public Pair(T paramT, S paramS) {
    this.key = paramT;
    this.value = paramS;
  }
  
  public T getKey() {
    return this.key;
  }
  
  public S getValue() {
    return this.value;
  }
  
  public void setKey(T paramT) {
    this.key = paramT;
  }
  
  public void setValue(S paramS) {
    this.value = paramS;
  }
}
