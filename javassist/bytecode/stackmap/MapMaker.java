package javassist.bytecode.stackmap;

import java.util.ArrayList;
import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ByteArray;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.StackMap;
import javassist.bytecode.StackMapTable;

public class MapMaker extends Tracer {
  public static StackMapTable make(ClassPool paramClassPool, MethodInfo paramMethodInfo) throws BadBytecode {
    TypedBlock[] arrayOfTypedBlock;
    CodeAttribute codeAttribute = paramMethodInfo.getCodeAttribute();
    if (codeAttribute == null)
      return null; 
    try {
      arrayOfTypedBlock = TypedBlock.makeBlocks(paramMethodInfo, codeAttribute, true);
    } catch (JsrBytecode jsrBytecode) {
      return null;
    } 
    if (arrayOfTypedBlock == null)
      return null; 
    MapMaker mapMaker = new MapMaker(paramClassPool, paramMethodInfo, codeAttribute);
    try {
      mapMaker.make(arrayOfTypedBlock, codeAttribute.getCode());
    } catch (BadBytecode badBytecode) {
      throw new BadBytecode(paramMethodInfo, badBytecode);
    } 
    return mapMaker.toStackMap(arrayOfTypedBlock);
  }
  
  public static StackMap make2(ClassPool paramClassPool, MethodInfo paramMethodInfo) throws BadBytecode {
    TypedBlock[] arrayOfTypedBlock;
    CodeAttribute codeAttribute = paramMethodInfo.getCodeAttribute();
    if (codeAttribute == null)
      return null; 
    try {
      arrayOfTypedBlock = TypedBlock.makeBlocks(paramMethodInfo, codeAttribute, true);
    } catch (JsrBytecode jsrBytecode) {
      return null;
    } 
    if (arrayOfTypedBlock == null)
      return null; 
    MapMaker mapMaker = new MapMaker(paramClassPool, paramMethodInfo, codeAttribute);
    try {
      mapMaker.make(arrayOfTypedBlock, codeAttribute.getCode());
    } catch (BadBytecode badBytecode) {
      throw new BadBytecode(paramMethodInfo, badBytecode);
    } 
    return mapMaker.toStackMap2(paramMethodInfo.getConstPool(), arrayOfTypedBlock);
  }
  
  public MapMaker(ClassPool paramClassPool, MethodInfo paramMethodInfo, CodeAttribute paramCodeAttribute) {
    super(paramClassPool, paramMethodInfo.getConstPool(), paramCodeAttribute
        .getMaxStack(), paramCodeAttribute.getMaxLocals(), 
        TypedBlock.getRetType(paramMethodInfo.getDescriptor()));
  }
  
  protected MapMaker(MapMaker paramMapMaker) {
    super(paramMapMaker);
  }
  
  void make(TypedBlock[] paramArrayOfTypedBlock, byte[] paramArrayOfbyte) throws BadBytecode {
    make(paramArrayOfbyte, paramArrayOfTypedBlock[0]);
    findDeadCatchers(paramArrayOfbyte, paramArrayOfTypedBlock);
    try {
      fixTypes(paramArrayOfbyte, paramArrayOfTypedBlock);
    } catch (NotFoundException notFoundException) {
      throw new BadBytecode("failed to resolve types", notFoundException);
    } 
  }
  
  private void make(byte[] paramArrayOfbyte, TypedBlock paramTypedBlock) throws BadBytecode {
    copyTypeData(paramTypedBlock.stackTop, paramTypedBlock.stackTypes, this.stackTypes);
    this.stackTop = paramTypedBlock.stackTop;
    copyTypeData(paramTypedBlock.localsTypes.length, paramTypedBlock.localsTypes, this.localsTypes);
    traceException(paramArrayOfbyte, paramTypedBlock.toCatch);
    int i = paramTypedBlock.position;
    int j = i + paramTypedBlock.length;
    while (i < j) {
      i += doOpcode(i, paramArrayOfbyte);
      traceException(paramArrayOfbyte, paramTypedBlock.toCatch);
    } 
    if (paramTypedBlock.exit != null)
      for (byte b = 0; b < paramTypedBlock.exit.length; b++) {
        TypedBlock typedBlock = (TypedBlock)paramTypedBlock.exit[b];
        if (typedBlock.alreadySet()) {
          mergeMap(typedBlock, true);
        } else {
          recordStackMap(typedBlock);
          MapMaker mapMaker = new MapMaker(this);
          mapMaker.make(paramArrayOfbyte, typedBlock);
        } 
      }  
  }
  
  private void traceException(byte[] paramArrayOfbyte, BasicBlock.Catch paramCatch) throws BadBytecode {
    while (paramCatch != null) {
      TypedBlock typedBlock = (TypedBlock)paramCatch.body;
      if (typedBlock.alreadySet()) {
        mergeMap(typedBlock, false);
        if (typedBlock.stackTop < 1)
          throw new BadBytecode("bad catch clause: " + paramCatch.typeIndex); 
        typedBlock.stackTypes[0] = merge(toExceptionType(paramCatch.typeIndex), typedBlock.stackTypes[0]);
      } else {
        recordStackMap(typedBlock, paramCatch.typeIndex);
        MapMaker mapMaker = new MapMaker(this);
        mapMaker.make(paramArrayOfbyte, typedBlock);
      } 
      paramCatch = paramCatch.next;
    } 
  }
  
  private void mergeMap(TypedBlock paramTypedBlock, boolean paramBoolean) throws BadBytecode {
    int i = this.localsTypes.length;
    byte b;
    for (b = 0; b < i; b++)
      paramTypedBlock.localsTypes[b] = merge(validateTypeData(this.localsTypes, i, b), paramTypedBlock.localsTypes[b]); 
    if (paramBoolean) {
      i = this.stackTop;
      for (b = 0; b < i; b++)
        paramTypedBlock.stackTypes[b] = merge(this.stackTypes[b], paramTypedBlock.stackTypes[b]); 
    } 
  }
  
  private TypeData merge(TypeData paramTypeData1, TypeData paramTypeData2) throws BadBytecode {
    if (paramTypeData1 == paramTypeData2)
      return paramTypeData2; 
    if (paramTypeData2 instanceof TypeData.ClassName || paramTypeData2 instanceof TypeData.BasicType)
      return paramTypeData2; 
    if (paramTypeData2 instanceof TypeData.AbsTypeVar) {
      ((TypeData.AbsTypeVar)paramTypeData2).merge(paramTypeData1);
      return paramTypeData2;
    } 
    throw new RuntimeException("fatal: this should never happen");
  }
  
  private void recordStackMap(TypedBlock paramTypedBlock) throws BadBytecode {
    TypeData[] arrayOfTypeData = TypeData.make(this.stackTypes.length);
    int i = this.stackTop;
    recordTypeData(i, this.stackTypes, arrayOfTypeData);
    recordStackMap0(paramTypedBlock, i, arrayOfTypeData);
  }
  
  private void recordStackMap(TypedBlock paramTypedBlock, int paramInt) throws BadBytecode {
    TypeData[] arrayOfTypeData = TypeData.make(this.stackTypes.length);
    arrayOfTypeData[0] = toExceptionType(paramInt).join();
    recordStackMap0(paramTypedBlock, 1, arrayOfTypeData);
  }
  
  private TypeData.ClassName toExceptionType(int paramInt) {
    String str;
    if (paramInt == 0) {
      str = "java.lang.Throwable";
    } else {
      str = this.cpool.getClassInfo(paramInt);
    } 
    return new TypeData.ClassName(str);
  }
  
  private void recordStackMap0(TypedBlock paramTypedBlock, int paramInt, TypeData[] paramArrayOfTypeData) throws BadBytecode {
    int i = this.localsTypes.length;
    TypeData[] arrayOfTypeData = TypeData.make(i);
    int j = recordTypeData(i, this.localsTypes, arrayOfTypeData);
    paramTypedBlock.setStackMap(paramInt, paramArrayOfTypeData, j, arrayOfTypeData);
  }
  
  protected static int recordTypeData(int paramInt, TypeData[] paramArrayOfTypeData1, TypeData[] paramArrayOfTypeData2) {
    int i = -1;
    for (byte b = 0; b < paramInt; b++) {
      TypeData typeData = validateTypeData(paramArrayOfTypeData1, paramInt, b);
      paramArrayOfTypeData2[b] = typeData.join();
      if (typeData != TOP)
        i = b + 1; 
    } 
    return i + 1;
  }
  
  protected static void copyTypeData(int paramInt, TypeData[] paramArrayOfTypeData1, TypeData[] paramArrayOfTypeData2) {
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfTypeData2[b] = paramArrayOfTypeData1[b]; 
  }
  
  private static TypeData validateTypeData(TypeData[] paramArrayOfTypeData, int paramInt1, int paramInt2) {
    TypeData typeData = paramArrayOfTypeData[paramInt2];
    if (typeData.is2WordType() && paramInt2 + 1 < paramInt1 && 
      paramArrayOfTypeData[paramInt2 + 1] != TOP)
      return TOP; 
    return typeData;
  }
  
  private void findDeadCatchers(byte[] paramArrayOfbyte, TypedBlock[] paramArrayOfTypedBlock) throws BadBytecode {
    int i = paramArrayOfTypedBlock.length;
    for (byte b = 0; b < i; b++) {
      TypedBlock typedBlock = paramArrayOfTypedBlock[b];
      if (!typedBlock.alreadySet()) {
        fixDeadcode(paramArrayOfbyte, typedBlock);
        BasicBlock.Catch catch_ = typedBlock.toCatch;
        if (catch_ != null) {
          TypedBlock typedBlock1 = (TypedBlock)catch_.body;
          if (!typedBlock1.alreadySet()) {
            recordStackMap(typedBlock1, catch_.typeIndex);
            fixDeadcode(paramArrayOfbyte, typedBlock1);
            typedBlock1.incoming = 1;
          } 
        } 
      } 
    } 
  }
  
  private void fixDeadcode(byte[] paramArrayOfbyte, TypedBlock paramTypedBlock) throws BadBytecode {
    int i = paramTypedBlock.position;
    int j = paramTypedBlock.length - 3;
    if (j < 0) {
      if (j == -1)
        paramArrayOfbyte[i] = 0; 
      paramArrayOfbyte[i + paramTypedBlock.length - 1] = -65;
      paramTypedBlock.incoming = 1;
      recordStackMap(paramTypedBlock, 0);
      return;
    } 
    paramTypedBlock.incoming = 0;
    for (byte b = 0; b < j; b++)
      paramArrayOfbyte[i + b] = 0; 
    paramArrayOfbyte[i + j] = -89;
    ByteArray.write16bit(-j, paramArrayOfbyte, i + j + 1);
  }
  
  private void fixTypes(byte[] paramArrayOfbyte, TypedBlock[] paramArrayOfTypedBlock) throws NotFoundException, BadBytecode {
    ArrayList arrayList = new ArrayList();
    int i = paramArrayOfTypedBlock.length;
    int j = 0;
    for (byte b = 0; b < i; b++) {
      TypedBlock typedBlock = paramArrayOfTypedBlock[b];
      if (typedBlock.alreadySet()) {
        int k = typedBlock.localsTypes.length;
        byte b1;
        for (b1 = 0; b1 < k; b1++)
          j = typedBlock.localsTypes[b1].dfs(arrayList, j, this.classPool); 
        k = typedBlock.stackTop;
        for (b1 = 0; b1 < k; b1++)
          j = typedBlock.stackTypes[b1].dfs(arrayList, j, this.classPool); 
      } 
    } 
  }
  
  public StackMapTable toStackMap(TypedBlock[] paramArrayOfTypedBlock) {
    StackMapTable.Writer writer = new StackMapTable.Writer(32);
    int i = paramArrayOfTypedBlock.length;
    TypedBlock typedBlock = paramArrayOfTypedBlock[0];
    int j = typedBlock.length;
    if (typedBlock.incoming > 0) {
      writer.sameFrame(0);
      j--;
    } 
    for (byte b = 1; b < i; b++) {
      TypedBlock typedBlock1 = paramArrayOfTypedBlock[b];
      if (isTarget(typedBlock1, paramArrayOfTypedBlock[b - 1])) {
        typedBlock1.resetNumLocals();
        int k = stackMapDiff(typedBlock.numLocals, typedBlock.localsTypes, typedBlock1.numLocals, typedBlock1.localsTypes);
        toStackMapBody(writer, typedBlock1, k, j, typedBlock);
        j = typedBlock1.length - 1;
        typedBlock = typedBlock1;
      } else if (typedBlock1.incoming == 0) {
        writer.sameFrame(j);
        j = typedBlock1.length - 1;
        typedBlock = typedBlock1;
      } else {
        j += typedBlock1.length;
      } 
    } 
    return writer.toStackMapTable(this.cpool);
  }
  
  private boolean isTarget(TypedBlock paramTypedBlock1, TypedBlock paramTypedBlock2) {
    int i = paramTypedBlock1.incoming;
    if (i > 1)
      return true; 
    if (i < 1)
      return false; 
    return paramTypedBlock2.stop;
  }
  
  private void toStackMapBody(StackMapTable.Writer paramWriter, TypedBlock paramTypedBlock1, int paramInt1, int paramInt2, TypedBlock paramTypedBlock2) {
    int i = paramTypedBlock1.stackTop;
    if (i == 0) {
      if (paramInt1 == 0) {
        paramWriter.sameFrame(paramInt2);
        return;
      } 
      if (0 > paramInt1 && paramInt1 >= -3) {
        paramWriter.chopFrame(paramInt2, -paramInt1);
        return;
      } 
      if (0 < paramInt1 && paramInt1 <= 3) {
        int[] arrayOfInt5 = new int[paramInt1];
        int[] arrayOfInt6 = fillStackMap(paramTypedBlock1.numLocals - paramTypedBlock2.numLocals, paramTypedBlock2.numLocals, arrayOfInt5, paramTypedBlock1.localsTypes);
        paramWriter.appendFrame(paramInt2, arrayOfInt6, arrayOfInt5);
        return;
      } 
    } else {
      if (i == 1 && paramInt1 == 0) {
        TypeData typeData = paramTypedBlock1.stackTypes[0];
        paramWriter.sameLocals(paramInt2, typeData.getTypeTag(), typeData.getTypeData(this.cpool));
        return;
      } 
      if (i == 2 && paramInt1 == 0) {
        TypeData typeData = paramTypedBlock1.stackTypes[0];
        if (typeData.is2WordType()) {
          paramWriter.sameLocals(paramInt2, typeData.getTypeTag(), typeData.getTypeData(this.cpool));
          return;
        } 
      } 
    } 
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = fillStackMap(i, 0, arrayOfInt1, paramTypedBlock1.stackTypes);
    int[] arrayOfInt3 = new int[paramTypedBlock1.numLocals];
    int[] arrayOfInt4 = fillStackMap(paramTypedBlock1.numLocals, 0, arrayOfInt3, paramTypedBlock1.localsTypes);
    paramWriter.fullFrame(paramInt2, arrayOfInt4, arrayOfInt3, arrayOfInt2, arrayOfInt1);
  }
  
  private int[] fillStackMap(int paramInt1, int paramInt2, int[] paramArrayOfint, TypeData[] paramArrayOfTypeData) {
    int i = diffSize(paramArrayOfTypeData, paramInt2, paramInt2 + paramInt1);
    ConstPool14 constPool14 = this.cpool;
    int[] arrayOfInt = new int[i];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt1; b2++) {
      TypeData typeData = paramArrayOfTypeData[paramInt2 + b2];
      arrayOfInt[b1] = typeData.getTypeTag();
      paramArrayOfint[b1] = typeData.getTypeData(constPool14);
      if (typeData.is2WordType())
        b2++; 
      b1++;
    } 
    return arrayOfInt;
  }
  
  private static int stackMapDiff(int paramInt1, TypeData[] paramArrayOfTypeData1, int paramInt2, TypeData[] paramArrayOfTypeData2) {
    int j, i = paramInt2 - paramInt1;
    if (i > 0) {
      j = paramInt1;
    } else {
      j = paramInt2;
    } 
    if (stackMapEq(paramArrayOfTypeData1, paramArrayOfTypeData2, j)) {
      if (i > 0)
        return diffSize(paramArrayOfTypeData2, j, paramInt2); 
      return -diffSize(paramArrayOfTypeData1, j, paramInt1);
    } 
    return -100;
  }
  
  private static boolean stackMapEq(TypeData[] paramArrayOfTypeData1, TypeData[] paramArrayOfTypeData2, int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      if (!paramArrayOfTypeData1[b].eq(paramArrayOfTypeData2[b]))
        return false; 
    } 
    return true;
  }
  
  private static int diffSize(TypeData[] paramArrayOfTypeData, int paramInt1, int paramInt2) {
    byte b = 0;
    while (paramInt1 < paramInt2) {
      TypeData typeData = paramArrayOfTypeData[paramInt1++];
      b++;
      if (typeData.is2WordType())
        paramInt1++; 
    } 
    return b;
  }
  
  public StackMap toStackMap2(ConstPool14 paramConstPool14, TypedBlock[] paramArrayOfTypedBlock) {
    StackMap.Writer writer = new StackMap.Writer();
    int i = paramArrayOfTypedBlock.length;
    boolean[] arrayOfBoolean = new boolean[i];
    TypedBlock typedBlock = paramArrayOfTypedBlock[0];
    arrayOfBoolean[0] = (typedBlock.incoming > 0);
    byte b1 = arrayOfBoolean[0] ? 1 : 0;
    byte b2;
    for (b2 = 1; b2 < i; b2++) {
      TypedBlock typedBlock1 = paramArrayOfTypedBlock[b2];
      arrayOfBoolean[b2] = isTarget(typedBlock1, paramArrayOfTypedBlock[b2 - 1]);
      if (isTarget(typedBlock1, paramArrayOfTypedBlock[b2 - 1])) {
        typedBlock1.resetNumLocals();
        typedBlock = typedBlock1;
        b1++;
      } 
    } 
    if (b1 == 0)
      return null; 
    writer.write16bit(b1);
    for (b2 = 0; b2 < i; b2++) {
      if (arrayOfBoolean[b2])
        writeStackFrame(writer, paramConstPool14, (paramArrayOfTypedBlock[b2]).position, paramArrayOfTypedBlock[b2]); 
    } 
    return writer.toStackMap(paramConstPool14);
  }
  
  private void writeStackFrame(StackMap.Writer paramWriter, ConstPool14 paramConstPool14, int paramInt, TypedBlock paramTypedBlock) {
    paramWriter.write16bit(paramInt);
    writeVerifyTypeInfo(paramWriter, paramConstPool14, paramTypedBlock.localsTypes, paramTypedBlock.numLocals);
    writeVerifyTypeInfo(paramWriter, paramConstPool14, paramTypedBlock.stackTypes, paramTypedBlock.stackTop);
  }
  
  private void writeVerifyTypeInfo(StackMap.Writer paramWriter, ConstPool14 paramConstPool14, TypeData[] paramArrayOfTypeData, int paramInt) {
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < paramInt; b2++) {
      TypeData typeData = paramArrayOfTypeData[b2];
      if (typeData != null && typeData.is2WordType()) {
        b1++;
        b2++;
      } 
    } 
    paramWriter.write16bit(paramInt - b1);
    for (b2 = 0; b2 < paramInt; b2++) {
      TypeData typeData = paramArrayOfTypeData[b2];
      paramWriter.writeVerifyTypeInfo(typeData.getTypeTag(), typeData.getTypeData(paramConstPool14));
      if (typeData.is2WordType())
        b2++; 
    } 
  }
}
