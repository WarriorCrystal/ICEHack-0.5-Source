package org.yaml.snakeyaml.events;

public class ImplicitTuple {
  private final boolean plain;
  
  private final boolean nonPlain;
  
  public ImplicitTuple(boolean paramBoolean1, boolean paramBoolean2) {
    this.plain = paramBoolean1;
    this.nonPlain = paramBoolean2;
  }
  
  public boolean canOmitTagInPlainScalar() {
    return this.plain;
  }
  
  public boolean canOmitTagInNonPlainScalar() {
    return this.nonPlain;
  }
  
  public boolean bothFalse() {
    return (!this.plain && !this.nonPlain);
  }
  
  public String toString() {
    return "implicit=[" + this.plain + ", " + this.nonPlain + "]";
  }
}
