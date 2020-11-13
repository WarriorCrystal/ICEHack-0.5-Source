package javassist;

public final class CtPrimitiveType extends CtClass {
  private char descriptor;
  
  private String wrapperName;
  
  private String getMethodName;
  
  private String mDescriptor;
  
  private int returnOp;
  
  private int arrayType;
  
  private int dataSize;
  
  CtPrimitiveType(String paramString1, char paramChar, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3) {
    super(paramString1);
    this.descriptor = paramChar;
    this.wrapperName = paramString2;
    this.getMethodName = paramString3;
    this.mDescriptor = paramString4;
    this.returnOp = paramInt1;
    this.arrayType = paramInt2;
    this.dataSize = paramInt3;
  }
  
  public boolean isPrimitive() {
    return true;
  }
  
  public int getModifiers() {
    return 17;
  }
  
  public char getDescriptor() {
    return this.descriptor;
  }
  
  public String getWrapperName() {
    return this.wrapperName;
  }
  
  public String getGetMethodName() {
    return this.getMethodName;
  }
  
  public String getGetMethodDescriptor() {
    return this.mDescriptor;
  }
  
  public int getReturnOp() {
    return this.returnOp;
  }
  
  public int getArrayType() {
    return this.arrayType;
  }
  
  public int getDataSize() {
    return this.dataSize;
  }
}
