package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class DeprecatedAttribute extends AttributeInfo {
  public static final String tag = "Deprecated";
  
  DeprecatedAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public DeprecatedAttribute(ConstPool14 paramConstPool14) {
    super(paramConstPool14, "Deprecated", new byte[0]);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    return new DeprecatedAttribute(paramConstPool14);
  }
}
