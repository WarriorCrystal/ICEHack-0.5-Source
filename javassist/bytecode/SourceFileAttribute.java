package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class SourceFileAttribute extends AttributeInfo {
  public static final String tag = "SourceFile";
  
  SourceFileAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public SourceFileAttribute(ConstPool14 paramConstPool14, String paramString) {
    super(paramConstPool14, "SourceFile");
    int i = paramConstPool14.addUtf8Info(paramString);
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = (byte)(i >>> 8);
    arrayOfByte[1] = (byte)i;
    set(arrayOfByte);
  }
  
  public String getFileName() {
    return getConstPool().getUtf8Info(ByteArray.readU16bit(get(), 0));
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    return new SourceFileAttribute(paramConstPool14, getFileName());
  }
}
