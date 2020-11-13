package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class LocalVariableTypeAttribute extends LocalVariableAttribute {
  public static final String tag = "LocalVariableTypeTable";
  
  public LocalVariableTypeAttribute(ConstPool14 paramConstPool14) {
    super(paramConstPool14, "LocalVariableTypeTable", new byte[2]);
    ByteArray.write16bit(0, this.info, 0);
  }
  
  LocalVariableTypeAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  private LocalVariableTypeAttribute(ConstPool14 paramConstPool14, byte[] paramArrayOfbyte) {
    super(paramConstPool14, "LocalVariableTypeTable", paramArrayOfbyte);
  }
  
  String renameEntry(String paramString1, String paramString2, String paramString3) {
    return SignatureAttribute.renameClass(paramString1, paramString2, paramString3);
  }
  
  String renameEntry(String paramString, Map paramMap) {
    return SignatureAttribute.renameClass(paramString, paramMap);
  }
  
  LocalVariableAttribute makeThisAttr(ConstPool14 paramConstPool14, byte[] paramArrayOfbyte) {
    return new LocalVariableTypeAttribute(paramConstPool14, paramArrayOfbyte);
  }
}
