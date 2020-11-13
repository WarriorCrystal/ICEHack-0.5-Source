package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.error.Mark;

final class SimpleKey {
  private int tokenNumber;
  
  private boolean required;
  
  private int index;
  
  private int line;
  
  private int column;
  
  private Mark mark;
  
  public SimpleKey(int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, Mark paramMark) {
    this.tokenNumber = paramInt1;
    this.required = paramBoolean;
    this.index = paramInt2;
    this.line = paramInt3;
    this.column = paramInt4;
    this.mark = paramMark;
  }
  
  public int getTokenNumber() {
    return this.tokenNumber;
  }
  
  public int getColumn() {
    return this.column;
  }
  
  public Mark getMark() {
    return this.mark;
  }
  
  public int getIndex() {
    return this.index;
  }
  
  public int getLine() {
    return this.line;
  }
  
  public boolean isRequired() {
    return this.required;
  }
  
  public String toString() {
    return "SimpleKey - tokenNumber=" + this.tokenNumber + " required=" + this.required + " index=" + this.index + " line=" + this.line + " column=" + this.column;
  }
}
