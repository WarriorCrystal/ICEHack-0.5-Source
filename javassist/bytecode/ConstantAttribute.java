package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class ConstantAttribute extends AttributeInfo {
  public static final String tag = "ConstantValue";
  
  ConstantAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public ConstantAttribute(ConstPool14 paramConstPool14, int paramInt) {
    super(paramConstPool14, "ConstantValue");
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = (byte)(paramInt >>> 8);
    arrayOfByte[1] = (byte)paramInt;
    set(arrayOfByte);
  }
  
  public int getConstantValue() {
    return ByteArray.readU16bit(get(), 0);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    int i = getConstPool().copy(getConstantValue(), paramConstPool14, paramMap);
    return new ConstantAttribute(paramConstPool14, i);
  }
}
