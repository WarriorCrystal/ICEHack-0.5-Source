package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class MethodParametersAttribute extends AttributeInfo {
  public static final String tag = "MethodParameters";
  
  MethodParametersAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public MethodParametersAttribute(ConstPool14 paramConstPool14, String[] paramArrayOfString, int[] paramArrayOfint) {
    super(paramConstPool14, "MethodParameters");
    byte[] arrayOfByte = new byte[paramArrayOfString.length * 4 + 1];
    arrayOfByte[0] = (byte)paramArrayOfString.length;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      ByteArray.write16bit(paramConstPool14.addUtf8Info(paramArrayOfString[b]), arrayOfByte, b * 4 + 1);
      ByteArray.write16bit(paramArrayOfint[b], arrayOfByte, b * 4 + 3);
    } 
    set(arrayOfByte);
  }
  
  public int size() {
    return this.info[0] & 0xFF;
  }
  
  public int name(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 4 + 1);
  }
  
  public int accessFlags(int paramInt) {
    return ByteArray.readU16bit(this.info, paramInt * 4 + 3);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    int i = size();
    ConstPool14 constPool14 = getConstPool();
    String[] arrayOfString = new String[i];
    int[] arrayOfInt = new int[i];
    for (byte b = 0; b < i; b++) {
      arrayOfString[b] = constPool14.getUtf8Info(name(b));
      arrayOfInt[b] = accessFlags(b);
    } 
    return new MethodParametersAttribute(paramConstPool14, arrayOfString, arrayOfInt);
  }
}
