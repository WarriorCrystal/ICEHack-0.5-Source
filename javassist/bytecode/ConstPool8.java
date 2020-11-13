package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;

class ConstPool8 extends ConstPool17 {
  static final int tag = 10;
  
  public ConstPool8(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt1, paramInt2, paramInt3);
  }
  
  public ConstPool8(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramDataInputStream, paramInt);
  }
  
  public int getTag() {
    return 10;
  }
  
  public String getTagName() {
    return "Method";
  }
  
  protected int copy2(ConstPool14 paramConstPool14, int paramInt1, int paramInt2) {
    return paramConstPool14.addMethodrefInfo(paramInt1, paramInt2);
  }
}
