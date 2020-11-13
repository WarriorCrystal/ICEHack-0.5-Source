package javassist.bytecode.analysis;

import java.util.NoSuchElementException;

class IntQueue {
  private Entry head;
  
  private Entry tail;
  
  private static class Entry {
    private Entry next;
    
    private int value;
    
    private Entry(int param1Int) {
      this.value = param1Int;
    }
  }
  
  void add(int paramInt) {
    Entry entry = new Entry(paramInt);
    if (this.tail != null)
      this.tail.next = entry; 
    this.tail = entry;
    if (this.head == null)
      this.head = entry; 
  }
  
  boolean isEmpty() {
    return (this.head == null);
  }
  
  int take() {
    if (this.head == null)
      throw new NoSuchElementException(); 
    int i = this.head.value;
    this.head = this.head.next;
    if (this.head == null)
      this.tail = null; 
    return i;
  }
}
