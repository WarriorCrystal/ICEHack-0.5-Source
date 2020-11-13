package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;

class ConstPool extends ConstPool17 {
  static final int tag = 9;
  
  public ConstPool(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt1, paramInt2, paramInt3);
  }
  
  public ConstPool(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramDataInputStream, paramInt);
  }
  
  public int getTag() {
    return 9;
  }
  
  public String getTagName() {
    return "Field";
  }
  
  protected int copy2(ConstPool14 paramConstPool14, int paramInt1, int paramInt2) {
    return paramConstPool14.addFieldrefInfo(paramInt1, paramInt2);
  }
}
