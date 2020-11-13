package javassist.bytecode.stackmap;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.MethodInfo;

public class TypedBlock extends BasicBlock {
  public int stackTop;
  
  public int numLocals;
  
  public TypeData[] localsTypes;
  
  public TypeData[] stackTypes;
  
  public static TypedBlock[] makeBlocks(MethodInfo paramMethodInfo, CodeAttribute paramCodeAttribute, boolean paramBoolean) throws BadBytecode {
    TypedBlock[] arrayOfTypedBlock = (TypedBlock[])(new Maker()).make(paramMethodInfo);
    if (paramBoolean && arrayOfTypedBlock.length < 2 && (
      arrayOfTypedBlock.length == 0 || (arrayOfTypedBlock[0]).incoming == 0))
      return null; 
    ConstPool14 constPool14 = paramMethodInfo.getConstPool();
    boolean bool = ((paramMethodInfo.getAccessFlags() & 0x8) != 0) ? true : false;
    arrayOfTypedBlock[0].initFirstBlock(paramCodeAttribute.getMaxStack(), paramCodeAttribute.getMaxLocals(), constPool14
        .getClassName(), paramMethodInfo.getDescriptor(), bool, paramMethodInfo
        .isConstructor());
    return arrayOfTypedBlock;
  }
  
  protected TypedBlock(int paramInt) {
    super(paramInt);
    this.localsTypes = null;
  }
  
  protected void toString2(StringBuffer paramStringBuffer) {
    super.toString2(paramStringBuffer);
    paramStringBuffer.append(",\n stack={");
    printTypes(paramStringBuffer, this.stackTop, this.stackTypes);
    paramStringBuffer.append("}, locals={");
    printTypes(paramStringBuffer, this.numLocals, this.localsTypes);
    paramStringBuffer.append('}');
  }
  
  private void printTypes(StringBuffer paramStringBuffer, int paramInt, TypeData[] paramArrayOfTypeData) {
    if (paramArrayOfTypeData == null)
      return; 
    for (byte b = 0; b < paramInt; b++) {
      if (b > 0)
        paramStringBuffer.append(", "); 
      TypeData typeData = paramArrayOfTypeData[b];
      paramStringBuffer.append((typeData == null) ? "<>" : typeData.toString());
    } 
  }
  
  public boolean alreadySet() {
    return (this.localsTypes != null);
  }
  
  public void setStackMap(int paramInt1, TypeData[] paramArrayOfTypeData1, int paramInt2, TypeData[] paramArrayOfTypeData2) throws BadBytecode {
    this.stackTop = paramInt1;
    this.stackTypes = paramArrayOfTypeData1;
    this.numLocals = paramInt2;
    this.localsTypes = paramArrayOfTypeData2;
  }
  
  public void resetNumLocals() {
    if (this.localsTypes != null) {
      int i = this.localsTypes.length;
      while (i > 0 && this.localsTypes[i - 1].isBasicType() == TypeTag.TOP && (
        i <= 1 || 
        !this.localsTypes[i - 2].is2WordType()))
        i--; 
      this.numLocals = i;
    } 
  }
  
  public static class Maker extends BasicBlock.Maker {
    protected BasicBlock makeBlock(int param1Int) {
      return new TypedBlock(param1Int);
    }
    
    protected BasicBlock[] makeArray(int param1Int) {
      return (BasicBlock[])new TypedBlock[param1Int];
    }
  }
  
  void initFirstBlock(int paramInt1, int paramInt2, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) throws BadBytecode {
    if (paramString2.charAt(0) != '(')
      throw new BadBytecode("no method descriptor: " + paramString2); 
    this.stackTop = 0;
    this.stackTypes = TypeData.make(paramInt1);
    TypeData[] arrayOfTypeData = TypeData.make(paramInt2);
    if (paramBoolean2) {
      arrayOfTypeData[0] = new TypeData.UninitThis(paramString1);
    } else if (!paramBoolean1) {
      arrayOfTypeData[0] = new TypeData.ClassName(paramString1);
    } 
    byte b = paramBoolean1 ? -1 : 0;
    int i = 1;
    try {
      while ((i = descToTag(paramString2, i, ++b, arrayOfTypeData)) > 0) {
        if (arrayOfTypeData[b].is2WordType())
          arrayOfTypeData[++b] = TypeTag.TOP; 
      } 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new BadBytecode("bad method descriptor: " + paramString2);
    } 
    this.numLocals = b;
    this.localsTypes = arrayOfTypeData;
  }
  
  private static int descToTag(String paramString, int paramInt1, int paramInt2, TypeData[] paramArrayOfTypeData) throws BadBytecode {
    int i = paramInt1;
    byte b = 0;
    char c = paramString.charAt(paramInt1);
    if (c == ')')
      return 0; 
    while (c == '[') {
      b++;
      c = paramString.charAt(++paramInt1);
    } 
    if (c == 'L') {
      int j = paramString.indexOf(';', ++paramInt1);
      if (b > 0) {
        paramArrayOfTypeData[paramInt2] = new TypeData.ClassName(paramString.substring(i, ++j));
      } else {
        paramArrayOfTypeData[paramInt2] = new TypeData.ClassName(paramString.substring(i + 1, ++j - 1)
            .replace('/', '.'));
      } 
      return j;
    } 
    if (b > 0) {
      paramArrayOfTypeData[paramInt2] = new TypeData.ClassName(paramString.substring(i, ++paramInt1));
      return paramInt1;
    } 
    TypeData typeData = toPrimitiveTag(c);
    if (typeData == null)
      throw new BadBytecode("bad method descriptor: " + paramString); 
    paramArrayOfTypeData[paramInt2] = typeData;
    return paramInt1 + 1;
  }
  
  private static TypeData toPrimitiveTag(char paramChar) {
    switch (paramChar) {
      case 'B':
      case 'C':
      case 'I':
      case 'S':
      case 'Z':
        return TypeTag.INTEGER;
      case 'J':
        return TypeTag.LONG;
      case 'F':
        return TypeTag.FLOAT;
      case 'D':
        return TypeTag.DOUBLE;
    } 
    return null;
  }
  
  public static String getRetType(String paramString) {
    int i = paramString.indexOf(')');
    if (i < 0)
      return "java.lang.Object"; 
    char c = paramString.charAt(i + 1);
    if (c == '[')
      return paramString.substring(i + 1); 
    if (c == 'L')
      return paramString.substring(i + 2, paramString.length() - 1).replace('/', '.'); 
    return "java.lang.Object";
  }
}
