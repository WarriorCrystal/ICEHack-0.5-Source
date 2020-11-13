package javassist.bytecode.analysis;

public class Frame {
  private Type[] locals;
  
  private Type[] stack;
  
  private int top;
  
  private boolean jsrMerged;
  
  private boolean retMerged;
  
  public Frame(int paramInt1, int paramInt2) {
    this.locals = new Type[paramInt1];
    this.stack = new Type[paramInt2];
  }
  
  public Type getLocal(int paramInt) {
    return this.locals[paramInt];
  }
  
  public void setLocal(int paramInt, Type paramType) {
    this.locals[paramInt] = paramType;
  }
  
  public Type getStack(int paramInt) {
    return this.stack[paramInt];
  }
  
  public void setStack(int paramInt, Type paramType) {
    this.stack[paramInt] = paramType;
  }
  
  public void clearStack() {
    this.top = 0;
  }
  
  public int getTopIndex() {
    return this.top - 1;
  }
  
  public int localsLength() {
    return this.locals.length;
  }
  
  public Type peek() {
    if (this.top < 1)
      throw new IndexOutOfBoundsException("Stack is empty"); 
    return this.stack[this.top - 1];
  }
  
  public Type pop() {
    if (this.top < 1)
      throw new IndexOutOfBoundsException("Stack is empty"); 
    return this.stack[--this.top];
  }
  
  public void push(Type paramType) {
    this.stack[this.top++] = paramType;
  }
  
  public Frame copy() {
    Frame frame = new Frame(this.locals.length, this.stack.length);
    System.arraycopy(this.locals, 0, frame.locals, 0, this.locals.length);
    System.arraycopy(this.stack, 0, frame.stack, 0, this.stack.length);
    frame.top = this.top;
    return frame;
  }
  
  public Frame copyStack() {
    Frame frame = new Frame(this.locals.length, this.stack.length);
    System.arraycopy(this.stack, 0, frame.stack, 0, this.stack.length);
    frame.top = this.top;
    return frame;
  }
  
  public boolean mergeStack(Frame paramFrame) {
    boolean bool = false;
    if (this.top != paramFrame.top)
      throw new RuntimeException("Operand stacks could not be merged, they are different sizes!"); 
    for (byte b = 0; b < this.top; b++) {
      if (this.stack[b] != null) {
        Type type1 = this.stack[b];
        Type type2 = type1.merge(paramFrame.stack[b]);
        if (type2 == Type.BOGUS)
          throw new RuntimeException("Operand stacks could not be merged due to differing primitive types: pos = " + b); 
        this.stack[b] = type2;
        if (!type2.equals(type1) || type2.popChanged())
          bool = true; 
      } 
    } 
    return bool;
  }
  
  public boolean merge(Frame paramFrame) {
    boolean bool = false;
    for (byte b = 0; b < this.locals.length; b++) {
      if (this.locals[b] != null) {
        Type type1 = this.locals[b];
        Type type2 = type1.merge(paramFrame.locals[b]);
        this.locals[b] = type2;
        if (!type2.equals(type1) || type2.popChanged())
          bool = true; 
      } else if (paramFrame.locals[b] != null) {
        this.locals[b] = paramFrame.locals[b];
        bool = true;
      } 
    } 
    bool |= mergeStack(paramFrame);
    return bool;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("locals = [");
    byte b;
    for (b = 0; b < this.locals.length; b++) {
      stringBuffer.append((this.locals[b] == null) ? "empty" : this.locals[b].toString());
      if (b < this.locals.length - 1)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append("] stack = [");
    for (b = 0; b < this.top; b++) {
      stringBuffer.append(this.stack[b]);
      if (b < this.top - 1)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  boolean isJsrMerged() {
    return this.jsrMerged;
  }
  
  void setJsrMerged(boolean paramBoolean) {
    this.jsrMerged = paramBoolean;
  }
  
  boolean isRetMerged() {
    return this.retMerged;
  }
  
  void setRetMerged(boolean paramBoolean) {
    this.retMerged = paramBoolean;
  }
}
