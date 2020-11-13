package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class BootstrapMethodsAttribute extends AttributeInfo {
  public static final String tag = "BootstrapMethods";
  
  public static class BootstrapMethod {
    public int methodRef;
    
    public int[] arguments;
    
    public BootstrapMethod(int param1Int, int[] param1ArrayOfint) {
      this.methodRef = param1Int;
      this.arguments = param1ArrayOfint;
    }
  }
  
  BootstrapMethodsAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public BootstrapMethodsAttribute(ConstPool14 paramConstPool14, BootstrapMethod[] paramArrayOfBootstrapMethod) {
    super(paramConstPool14, "BootstrapMethods");
    int i = 2;
    for (byte b1 = 0; b1 < paramArrayOfBootstrapMethod.length; b1++)
      i += 4 + (paramArrayOfBootstrapMethod[b1]).arguments.length * 2; 
    byte[] arrayOfByte = new byte[i];
    ByteArray.write16bit(paramArrayOfBootstrapMethod.length, arrayOfByte, 0);
    byte b2 = 2;
    for (byte b3 = 0; b3 < paramArrayOfBootstrapMethod.length; b3++) {
      ByteArray.write16bit((paramArrayOfBootstrapMethod[b3]).methodRef, arrayOfByte, b2);
      ByteArray.write16bit((paramArrayOfBootstrapMethod[b3]).arguments.length, arrayOfByte, b2 + 2);
      int[] arrayOfInt = (paramArrayOfBootstrapMethod[b3]).arguments;
      b2 += 4;
      for (byte b = 0; b < arrayOfInt.length; b++) {
        ByteArray.write16bit(arrayOfInt[b], arrayOfByte, b2);
        b2 += 2;
      } 
    } 
    set(arrayOfByte);
  }
  
  public BootstrapMethod[] getMethods() {
    byte[] arrayOfByte = get();
    int i = ByteArray.readU16bit(arrayOfByte, 0);
    BootstrapMethod[] arrayOfBootstrapMethod = new BootstrapMethod[i];
    byte b1 = 2;
    for (byte b2 = 0; b2 < i; b2++) {
      int j = ByteArray.readU16bit(arrayOfByte, b1);
      int k = ByteArray.readU16bit(arrayOfByte, b1 + 2);
      int[] arrayOfInt = new int[k];
      b1 += 4;
      for (byte b = 0; b < k; b++) {
        arrayOfInt[b] = ByteArray.readU16bit(arrayOfByte, b1);
        b1 += 2;
      } 
      arrayOfBootstrapMethod[b2] = new BootstrapMethod(j, arrayOfInt);
    } 
    return arrayOfBootstrapMethod;
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    BootstrapMethod[] arrayOfBootstrapMethod = getMethods();
    ConstPool14 constPool14 = getConstPool();
    for (byte b = 0; b < arrayOfBootstrapMethod.length; b++) {
      BootstrapMethod bootstrapMethod = arrayOfBootstrapMethod[b];
      bootstrapMethod.methodRef = constPool14.copy(bootstrapMethod.methodRef, paramConstPool14, paramMap);
      for (byte b1 = 0; b1 < bootstrapMethod.arguments.length; b1++)
        bootstrapMethod.arguments[b1] = constPool14.copy(bootstrapMethod.arguments[b1], paramConstPool14, paramMap); 
    } 
    return new BootstrapMethodsAttribute(paramConstPool14, arrayOfBootstrapMethod);
  }
}
