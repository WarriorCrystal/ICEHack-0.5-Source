package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class DoubleConst extends ASTree {
  protected double value;
  
  protected int type;
  
  public DoubleConst(double paramDouble, int paramInt) {
    this.value = paramDouble;
    this.type = paramInt;
  }
  
  public double get() {
    return this.value;
  }
  
  public void set(double paramDouble) {
    this.value = paramDouble;
  }
  
  public int getType() {
    return this.type;
  }
  
  public String toString() {
    return Double.toString(this.value);
  }
  
  public void accept(Visitor paramVisitor) throws CompileError {
    paramVisitor.atDoubleConst(this);
  }
  
  public ASTree compute(int paramInt, ASTree paramASTree) {
    if (paramASTree instanceof IntConst)
      return compute0(paramInt, (IntConst)paramASTree); 
    if (paramASTree instanceof DoubleConst)
      return compute0(paramInt, (DoubleConst)paramASTree); 
    return null;
  }
  
  private DoubleConst compute0(int paramInt, DoubleConst paramDoubleConst) {
    char c;
    if (this.type == 405 || paramDoubleConst.type == 405) {
      c = 'ƕ';
    } else {
      c = 'Ɣ';
    } 
    return compute(paramInt, this.value, paramDoubleConst.value, c);
  }
  
  private DoubleConst compute0(int paramInt, IntConst paramIntConst) {
    return compute(paramInt, this.value, paramIntConst.value, this.type);
  }
  
  private static DoubleConst compute(int paramInt1, double paramDouble1, double paramDouble2, int paramInt2) {
    double d;
    switch (paramInt1) {
      case 43:
        d = paramDouble1 + paramDouble2;
        return new DoubleConst(d, paramInt2);
      case 45:
        d = paramDouble1 - paramDouble2;
        return new DoubleConst(d, paramInt2);
      case 42:
        d = paramDouble1 * paramDouble2;
        return new DoubleConst(d, paramInt2);
      case 47:
        d = paramDouble1 / paramDouble2;
        return new DoubleConst(d, paramInt2);
      case 37:
        d = paramDouble1 % paramDouble2;
        return new DoubleConst(d, paramInt2);
    } 
    return null;
  }
}
