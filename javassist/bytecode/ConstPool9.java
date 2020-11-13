package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;

class ConstPool9 extends ConstPool17 {
  static final int tag = 11;
  
  public ConstPool9(int paramInt1, int paramInt2, int paramInt3) {
    super(paramInt1, paramInt2, paramInt3);
  }
  
  public ConstPool9(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    super(paramDataInputStream, paramInt);
  }
  
  public int getTag() {
    return 11;
  }
  
  public String getTagName() {
    return "Interface";
  }
  
  protected int copy2(ConstPool14 paramConstPool14, int paramInt1, int paramInt2) {
    return paramConstPool14.addInterfaceMethodrefInfo(paramInt1, paramInt2);
  }
}
