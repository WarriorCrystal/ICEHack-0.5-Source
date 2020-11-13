package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class IntConst extends ASTree {
  protected long value;
  
  protected int type;
  
  public IntConst(long paramLong, int paramInt) {
    this.value = paramLong;
    this.type = paramInt;
  }
  
  public long get() {
    return this.value;
  }
  
  public void set(long paramLong) {
    this.value = paramLong;
  }
  
  public int getType() {
    return this.type;
  }
  
  public String toString() {
    return Long.toString(this.value);
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atIntConst(this);
  }
  
  public ASTree compute(int paramInt, ASTree paramASTree) {
    if (paramASTree instanceof IntConst)
      return compute0(paramInt, (IntConst)paramASTree); 
    if (paramASTree instanceof DoubleConst)
      return compute0(paramInt, (DoubleConst)paramASTree); 
    return null;
  }
  
  private IntConst compute0(int paramInt, IntConst paramIntConst) {
    int k;
    long l3;
    int i = this.type;
    int j = paramIntConst.type;
    if (i == 403 || j == 403) {
      k = 403;
    } else if (i == 401 && j == 401) {
      k = 401;
    } else {
      k = 402;
    } 
    long l1 = this.value;
    long l2 = paramIntConst.value;
    switch (paramInt) {
      case 43:
        l3 = l1 + l2;
        return new IntConst(l3, k);
      case 45:
        l3 = l1 - l2;
        return new IntConst(l3, k);
      case 42:
        l3 = l1 * l2;
        return new IntConst(l3, k);
      case 47:
        l3 = l1 / l2;
        return new IntConst(l3, k);
      case 37:
        l3 = l1 % l2;
        return new IntConst(l3, k);
      case 124:
        l3 = l1 | l2;
        return new IntConst(l3, k);
      case 94:
        l3 = l1 ^ l2;
        return new IntConst(l3, k);
      case 38:
        l3 = l1 & l2;
        return new IntConst(l3, k);
      case 364:
        l3 = this.value << (int)l2;
        k = i;
        return new IntConst(l3, k);
      case 366:
        l3 = this.value >> (int)l2;
        k = i;
        return new IntConst(l3, k);
      case 370:
        l3 = this.value >>> (int)l2;
        k = i;
        return new IntConst(l3, k);
    } 
    return null;
  }
  
  private DoubleConst compute0(int paramInt, DoubleConst paramDoubleConst) {
    double d3, d1 = this.value;
    double d2 = paramDoubleConst.value;
    switch (paramInt) {
      case 43:
        d3 = d1 + d2;
        return new DoubleConst(d3, paramDoubleConst.type);
      case 45:
        d3 = d1 - d2;
        return new DoubleConst(d3, paramDoubleConst.type);
      case 42:
        d3 = d1 * d2;
        return new DoubleConst(d3, paramDoubleConst.type);
      case 47:
        d3 = d1 / d2;
        return new DoubleConst(d3, paramDoubleConst.type);
      case 37:
        d3 = d1 % d2;
        return new DoubleConst(d3, paramDoubleConst.type);
    } 
    return null;
  }
}
